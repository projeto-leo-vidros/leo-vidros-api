package com.project.extension.strategy.pdf;

import com.project.extension.rabbitmq.queue.PdfResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.environment", havingValue = "production")
public class S3PdfStorageStrategy implements PdfStorageStrategy {

    private static final Pattern NUMERO_VALIDO = Pattern.compile("^[A-Za-z0-9_\\-]+$");

    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${app.pdf.storage-path:resources/pdfs}")
    private String storagePath;

    @Override
    public void armazenar(PdfResponse response) {
        String numero = response.getNumeroOrcamento();
        String nomeArquivo = response.getNomeArquivo();

        if (numero == null || numero.isBlank()) {
            log.warn("Resposta de PDF sem numeroOrcamento");
            return;
        }
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            log.warn("Resposta de PDF sem nomeArquivo para orçamento {}", numero);
            return;
        }
        if (!NUMERO_VALIDO.matcher(numero).matches()) {
            log.warn("Número de orçamento inválido, PDF não será salvo: {}", numero);
            return;
        }

        byte[] bytes = baixarDoS3(numero, nomeArquivo);
        if (bytes == null) return;

        cache.put(numero, bytes);
        try {
            salvarEmDisco(numero, bytes);
        } catch (IOException e) {
            log.error("Falha ao salvar PDF em disco para orçamento {}", numero, e);
        }
    }

    @Override
    public byte[] obterPorNumeroOrcamento(String numero) {
        if (!NUMERO_VALIDO.matcher(numero).matches()) {
            log.warn("Número de orçamento inválido para busca: {}", numero);
            return null;
        }

        byte[] cached = cache.get(numero);
        if (cached != null) return cached;

        try {
            byte[] bytes = lerDoDisco(numero);
            if (bytes != null) {
                cache.put(numero, bytes);
                return bytes;
            }
        } catch (IOException e) {
            log.error("Falha ao ler PDF do disco para orçamento {}", numero, e);
        }

        // Disco frio (instância reiniciada ou volume limpo): tenta buscar diretamente no S3.
        String nomeArquivo = "orcamento_" + numero + ".pdf";
        byte[] bytes = baixarDoS3(numero, nomeArquivo);
        if (bytes != null) {
            cache.put(numero, bytes);
            try {
                salvarEmDisco(numero, bytes);
            } catch (IOException e) {
                log.warn("PDF recuperado do S3 mas falhou ao salvar em disco para orçamento {}", numero, e);
            }
        }
        return bytes;
    }

    private byte[] baixarDoS3(String numero, String nomeArquivo) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(nomeArquivo)
                    .build();
            ResponseBytes<GetObjectResponse> objeto = s3Client.getObjectAsBytes(request);
            log.info("PDF do orçamento {} baixado do S3 (chave: {})", numero, nomeArquivo);
            return objeto.asByteArray();
        } catch (Exception e) {
            log.error("Falha ao baixar PDF do S3 para orçamento {} (chave: {})", numero, nomeArquivo, e);
            return null;
        }
    }

    private void salvarEmDisco(String numero, byte[] bytes) throws IOException {
        Path dir = Paths.get(storagePath);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        Path arquivo = dir.resolve("orcamento_" + numero + ".pdf");
        try (FileOutputStream fos = new FileOutputStream(arquivo.toFile())) {
            fos.write(bytes);
        }
        log.debug("PDF do orçamento {} salvo em {}", numero, arquivo.toAbsolutePath());
    }

    private byte[] lerDoDisco(String numero) throws IOException {
        Path arquivo = Paths.get(storagePath).resolve("orcamento_" + numero + ".pdf");
        if (!Files.exists(arquivo)) return null;
        return Files.readAllBytes(arquivo);
    }
}
