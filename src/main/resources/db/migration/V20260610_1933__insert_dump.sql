-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: leo_vidros
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `agendamento`
--

LOCK TABLES `agendamento` WRITE;
/*!40000 ALTER TABLE `agendamento` DISABLE KEYS */;
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (1,1,1,2,3,'ORCAMENTO','2026-06-10','19:06:00','19:10:00','Sem observações','2026-06-10 19:05:45','2026-06-10 19:10:40');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (2,1,2,6,3,'ORCAMENTO','2026-06-10','19:10:00','19:15:00','Sem observações','2026-06-10 19:06:12','2026-06-10 19:14:49');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (3,1,7,7,3,'ORCAMENTO','2026-06-10','19:16:00','19:20:00','Sem observações','2026-06-10 19:06:39','2026-06-10 19:16:17');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (4,1,6,8,1,'ORCAMENTO','2026-06-12','09:10:00','13:00:00','','2026-06-10 19:07:05','2026-06-10 19:07:05');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (5,1,5,4,1,'ORCAMENTO','2026-06-17','09:00:00','10:00:00','','2026-06-10 19:07:50','2026-06-10 19:07:50');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (6,1,3,3,1,'ORCAMENTO','2026-06-18','19:10:00','19:15:00','','2026-06-10 19:09:11','2026-06-10 19:09:11');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (7,1,4,1,3,'ORCAMENTO','2026-06-10','19:16:00','19:20:00','Sem observações','2026-06-10 19:09:35','2026-06-10 19:15:29');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (8,1,6,8,3,'SERVICO','2026-06-10','20:18:00','20:20:00',NULL,'2026-06-10 19:18:57','2026-06-10 19:19:29');
INSERT INTO `agendamento` (`id`, `ativo`, `servico_id`, `endereco_id`, `status_id`, `tipo`, `data_agendamento`, `inicio_agendamento`, `fim_agendamento`, `observacao`, `created_at`, `updated_at`) VALUES (9,1,4,1,1,'SERVICO','2026-06-10','20:18:00','20:21:00',NULL,'2026-06-10 19:23:06','2026-06-10 19:23:06');
/*!40000 ALTER TABLE `agendamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `agendamento_funcionario`
--

LOCK TABLES `agendamento_funcionario` WRITE;
/*!40000 ALTER TABLE `agendamento_funcionario` DISABLE KEYS */;
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (1,1);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (2,1);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (3,1);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (4,1);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (5,1);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (6,1);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (8,2);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (9,3);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (7,4);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (8,4);
INSERT INTO `agendamento_funcionario` (`agendamento_id`, `funcionario_id`) VALUES (9,5);
/*!40000 ALTER TABLE `agendamento_funcionario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `agendamento_produto`
--

LOCK TABLES `agendamento_produto` WRITE;
/*!40000 ALTER TABLE `agendamento_produto` DISABLE KEYS */;
/*!40000 ALTER TABLE `agendamento_produto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `atributo_produto`
--

LOCK TABLES `atributo_produto` WRITE;
/*!40000 ALTER TABLE `atributo_produto` DISABLE KEYS */;
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (1,1,'Espessura','4mm','2026-06-10 20:27:54','2026-06-10 20:27:54');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (2,1,'Tipo','Float incolor','2026-06-10 20:27:54','2026-06-10 20:27:54');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (3,2,'Espessura','6mm','2026-06-10 20:30:39','2026-06-10 20:30:39');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (4,2,'Tipo','Float incolor','2026-06-10 20:30:39','2026-06-10 20:30:39');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (5,3,'Espessura','8mm','2026-06-10 20:32:34','2026-06-10 20:32:34');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (6,3,'Norma','ABNT NBR 7199','2026-06-10 20:32:34','2026-06-10 20:32:34');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (7,4,'Espessura','10mm','2026-06-10 20:34:49','2026-06-10 20:34:49');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (8,4,'Aplicação','Sacadas e fachadas','2026-06-10 20:34:49','2026-06-10 20:34:49');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (9,5,'Textura','Canelado','2026-06-10 20:37:12','2026-06-10 20:37:12');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (10,5,'Translucidez','Translúcido','2026-06-10 20:37:12','2026-06-10 20:37:12');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (11,6,'Acabamento','Prata','2026-06-10 20:38:39','2026-06-10 20:38:39');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (12,6,'Espessura','4mm','2026-06-10 20:38:39','2026-06-10 20:38:39');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (13,7,'Composição','3+3mm com PVB','2026-06-10 20:40:21','2026-06-10 20:40:21');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (14,7,'Uso','Coberturas e guarda-corpos','2026-06-10 20:40:21','2026-06-10 20:40:21');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (15,8,'Material','Alumínio anodizado','2026-06-10 20:41:49','2026-06-10 20:41:49');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (16,8,'Abertura','10mm','2026-06-10 20:41:49','2026-06-10 20:41:49');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (17,9,'Acabamento','Escovado','2026-06-10 20:43:50','2026-06-10 20:43:50');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (18,9,'Uso','BOx de banheiro','2026-06-10 20:43:50','2026-06-10 20:43:50');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (19,10,'Acabamento','Escovado','2026-06-10 20:45:01','2026-06-10 20:45:01');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (20,10,'Uso','Box de banheiro','2026-06-10 20:45:01','2026-06-10 20:45:01');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (21,11,'Material','PVC','2026-06-10 20:46:05','2026-06-10 20:46:05');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (22,11,'Largura','8mm','2026-06-10 20:46:05','2026-06-10 20:46:05');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (23,12,'Material','Aço inox 304','2026-06-10 20:47:26','2026-06-10 20:47:26');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (24,12,'Capacidade','Vidro até 12mm','2026-06-10 20:47:26','2026-06-10 20:47:26');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (25,13,'Material','Inox escovado','2026-06-10 20:48:28','2026-06-10 20:48:28');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (26,13,'Comprimento','30cm','2026-06-10 20:48:28','2026-06-10 20:48:28');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (27,14,'Material','Inox','2026-06-10 20:49:27','2026-06-10 20:49:27');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (28,14,'Tipo','Embutir','2026-06-10 20:49:27','2026-06-10 20:49:27');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (29,15,'Tipo','Botão','2026-06-10 20:50:24','2026-06-10 20:50:24');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (30,15,'Diâmetro','50mm','2026-06-10 20:50:24','2026-06-10 20:50:24');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (31,16,'Material','Nylon','2026-06-10 20:51:28','2026-06-10 20:51:28');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (32,16,'Uso','Box deslizante','2026-06-10 20:51:28','2026-06-10 20:51:28');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (33,17,'Tipo','Neutro','2026-06-10 20:52:45','2026-06-10 20:52:45');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (34,17,'Cor','Incolor','2026-06-10 20:52:45','2026-06-10 20:52:45');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (35,18,'Tipo','Estrutural','2026-06-10 20:53:40','2026-06-10 20:53:40');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (36,18,'Cor','Preto','2026-06-10 20:53:40','2026-06-10 20:53:40');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (37,19,'Largura','12mm','2026-06-10 20:54:58','2026-06-10 20:54:58');
INSERT INTO `atributo_produto` (`id`, `produto_id`, `tipo`, `valor`, `created_at`, `updated_at`) VALUES (38,19,'Comprimento','5m','2026-06-10 20:54:58','2026-06-10 20:54:58');
/*!40000 ALTER TABLE `atributo_produto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` (`id`, `nome`, `created_at`, `updated_at`) VALUES (1,'INFO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `categoria` (`id`, `nome`, `created_at`, `updated_at`) VALUES (2,'ERROR','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `categoria` (`id`, `nome`, `created_at`, `updated_at`) VALUES (3,'DEBUG','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `categoria` (`id`, `nome`, `created_at`, `updated_at`) VALUES (4,'WARNING','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `categoria` (`id`, `nome`, `created_at`, `updated_at`) VALUES (5,'SUCCESS','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `categoria` (`id`, `nome`, `created_at`, `updated_at`) VALUES (6,'FATAL','2026-06-10 20:22:37','2026-06-10 20:22:37');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (1,'Ricardo Aparecido Ferreira','07431285671','ricardo.ferreira@gmail.com','11987452231','Ativo','2026-06-10 18:26:31','2026-06-10 19:00:22');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (2,'Juliana Costa Barbosa','23198406722','ju.barbosa@hotmail.com','11976239010','Ativo','2026-06-10 18:27:24','2026-06-10 18:55:51');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (3,'Marcos Vinicius Almeida','38920174504','mv.almeida@yahoo.com.br','11993128854','Ativo','2026-06-10 18:28:46','2026-06-10 18:59:01');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (4,'Construtora Alves & Prado Ltda','63480913849','engenharia@alvesprado.com.br','1132914400','Ativo','2026-06-10 18:30:22','2026-06-10 19:01:11');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (5,'Patricia Nunes Rodrigues','51284709388','patricia.nunes@gmail.com','11945217760','Ativo','2026-06-10 18:30:58','2026-06-10 21:51:56');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (6,'Diego Henrique Lopes','64302918713','diegolopes@uol.com.br','11980013322','Ativo','2026-06-10 18:31:38','2026-06-10 18:56:47');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (7,' Simone Tavares de Oliveira','18745630208','si.tavares@gmail.com','11967345599','Ativo','2026-06-10 18:32:21','2026-06-10 19:02:31');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (8,'Amadeu Pires Santos','92061374840','amadeu.santos@gmail.com','11974891123','Ativo','2026-06-10 18:32:57','2026-06-10 19:01:53');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (9,'Phelipe',NULL,NULL,NULL,'Ativo','2026-06-10 18:40:24','2026-06-10 21:51:30');
INSERT INTO `cliente` (`id`, `nome`, `cpf`, `email`, `telefone`, `status`, `created_at`, `updated_at`) VALUES (11,'Leticia',NULL,NULL,NULL,'Avulso','2026-06-10 18:54:02','2026-06-10 18:54:02');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `cliente_endereco`
--

LOCK TABLES `cliente_endereco` WRITE;
/*!40000 ALTER TABLE `cliente_endereco` DISABLE KEYS */;
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (1,1);
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (2,2);
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (3,3);
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (4,4);
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (5,5);
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (6,6);
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (7,7);
INSERT INTO `cliente_endereco` (`cliente_id`, `endereco_id`) VALUES (8,8);
/*!40000 ALTER TABLE `cliente_endereco` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `endereco`
--

LOCK TABLES `endereco` WRITE;
/*!40000 ALTER TABLE `endereco` DISABLE KEYS */;
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (1,'Avenida Indianópolis',3412,'Apto 81','Indianópolis','São Paulo','SP','04062003','Brasil','2026-06-10 18:26:31','2026-06-10 18:26:31');
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (2,'Rua Cambuci do Vale',346,'casa','Cidade Dutra','São Paulo','SP','04805110','Brasil','2026-06-10 18:27:24','2026-06-10 18:27:24');
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (3,'Rua Júlio Ramos da Silva',67,'','Chácara Portal das Estâncias','Bragança Paulista','SP','12915015','Brasil','2026-06-10 18:28:46','2026-06-10 18:28:46');
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (4,'Rua Haddock Lobo',889,'Sala 305','Cerqueira César','São Paulo','SP','01414001','Brasil','2026-06-10 18:30:22','2026-06-10 18:30:22');
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (5,'Rua Barbosa Lopes',54,'','Granja Julieta','São Paulo','SP','04720000','Brasil','2026-06-10 18:30:58','2026-06-10 18:30:58');
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (6,'Avenida Presidente Kennedy',400,'Bloco B Apto 12','Rochdale','Osasco','SP','06220040','Brasil','2026-06-10 18:31:38','2026-06-10 18:31:38');
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (7,'Rua Fidêncio Ramos',758,'Apto 31','Vila Olímpia','São Paulo','SP','04551010','Brasil','2026-06-10 18:32:21','2026-06-10 18:32:21');
INSERT INTO `endereco` (`id`, `rua`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep`, `pais`, `created_at`, `updated_at`) VALUES (8,'Avenida Afonso de Sampaio e Sousa',112,'Casa','Jardim Nossa Senhora do Carmo','São Paulo','SP','08270000','Brasil','2026-06-10 18:32:57','2026-06-10 18:32:57');
/*!40000 ALTER TABLE `endereco` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `estoque`
--

LOCK TABLES `estoque` WRITE;
/*!40000 ALTER TABLE `estoque` DISABLE KEYS */;
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (1,1,52.00,52.00,0.00,'Galpão Vidros','2026-06-10 20:27:54','2026-06-10 21:08:42');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (2,2,42.00,42.00,0.00,'Galpão Vidros','2026-06-10 20:30:39','2026-06-10 21:08:03');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (3,3,0.00,0.00,0.00,'Galpão Vidros','2026-06-10 20:32:34','2026-06-10 21:08:07');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (4,4,94.00,94.00,0.00,'Galpão Vidros','2026-06-10 20:34:49','2026-06-10 21:07:48');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (5,5,97.00,95.00,2.00,'Galpão Vidros','2026-06-10 20:37:12','2026-06-10 22:23:05');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (6,6,7.00,7.00,0.00,'Galpão Vidros','2026-06-10 20:38:39','2026-06-10 21:08:11');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (7,7,11.00,11.00,0.00,'Galpão Vidros','2026-06-10 20:40:21','2026-06-10 22:19:29');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (8,8,123.00,123.00,0.00,'Estante A','2026-06-10 20:41:49','2026-06-10 21:54:02');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (9,9,91.00,91.00,0.00,'Estante A','2026-06-10 20:43:50','2026-06-10 21:11:42');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (10,10,33.00,33.00,0.00,'Estante A','2026-06-10 20:45:01','2026-06-10 21:03:32');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (11,11,44.00,44.00,0.00,'Estante A','2026-06-10 20:46:05','2026-06-10 21:04:19');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (12,12,81.00,81.00,0.00,'Estante B','2026-06-10 20:47:26','2026-06-10 21:40:23');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (13,13,0.00,0.00,0.00,'Estante B','2026-06-10 20:48:28','2026-06-10 21:10:08');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (14,14,25.00,25.00,0.00,'Estante B','2026-06-10 20:49:27','2026-06-10 21:09:25');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (15,15,164.00,164.00,0.00,'Estante B','2026-06-10 20:50:24','2026-06-10 21:53:33');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (16,16,50.00,50.00,0.00,'Estante B','2026-06-10 20:51:28','2026-06-10 21:10:32');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (17,17,120.00,110.00,10.00,'Estante C','2026-06-10 20:52:45','2026-06-10 22:23:05');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (18,18,2.00,2.00,0.00,'Estante C','2026-06-10 20:53:40','2026-06-10 21:10:42');
INSERT INTO `estoque` (`id`, `produto_id`, `quantidade_total`, `quantidade_disponivel`, `reservado`, `localizacao`, `created_at`, `updated_at`) VALUES (19,19,30.00,30.00,0.00,'Estante C','2026-06-10 20:54:58','2026-06-10 21:53:02');
/*!40000 ALTER TABLE `estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `etapa`
--

LOCK TABLES `etapa` WRITE;
/*!40000 ALTER TABLE `etapa` DISABLE KEYS */;
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (1,'PEDIDO','AGUARDANDO AGENDA DE ORÇAMENTO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (2,'PEDIDO','ORÇAMENTO AGENDADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (3,'PEDIDO','ANÁLISE DO ORÇAMENTO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (4,'PEDIDO','ORÇAMENTO APROVADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (5,'PEDIDO','AGUARDANDO AGENDA DE SERVIÇO/INSTALAÇÃO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (6,'PEDIDO','SERVIÇO AGENDADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (7,'PEDIDO','AGENDAMENTO EM EXECUÇÃO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `etapa` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (8,'PEDIDO','CONCLUÍDO','2026-06-10 20:22:37','2026-06-10 20:22:37');
/*!40000 ALTER TABLE `etapa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (1,'20260601.0000','baseline schema','SQL','V20260601_0000__baseline_schema.sql',-872508969,'leo_vidros_user','2026-06-10 20:22:37',1028,1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (2,'20260601.0100','baseline seed','SQL','V20260601_0100__baseline_seed.sql',-1382804439,'leo_vidros_user','2026-06-10 20:22:37',7,1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (3,'20260601.0200','create servico produto','SQL','V20260601_0200__create_servico_produto.sql',1051393500,'leo_vidros_user','2026-06-10 20:22:37',40,1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (4,'20260609.2032','dump','SQL','V20260609_2032__dump.sql',796451739,'leo_vidros_user','2026-06-10 20:22:37',4,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `funcionario`
--

LOCK TABLES `funcionario` WRITE;
/*!40000 ALTER TABLE `funcionario` DISABLE KEYS */;
INSERT INTO `funcionario` (`id`, `nome`, `telefone`, `funcao`, `contrato`, `escala`, `ativo`, `created_at`, `updated_at`) VALUES (1,'Leandro Souza Martins','(11) 99187-4432','Técnico de orçamentos','Registrado','5x2',1,'2026-06-10 18:35:53','2026-06-10 18:35:53');
INSERT INTO `funcionario` (`id`, `nome`, `telefone`, `funcao`, `contrato`, `escala`, `ativo`, `created_at`, `updated_at`) VALUES (2,'Roberto Carlos Figueiredo','(11) 98834-2201','Vidraceiro','Registrado','5x2',1,'2026-06-10 18:36:16','2026-06-10 18:36:16');
INSERT INTO `funcionario` (`id`, `nome`, `telefone`, `funcao`, `contrato`, `escala`, `ativo`, `created_at`, `updated_at`) VALUES (3,'Tiago Nascimento Lima','(11) 97023-5580','Especialista em Esquadrias','Registrado','5x2',1,'2026-06-10 18:37:04','2026-06-10 18:37:04');
INSERT INTO `funcionario` (`id`, `nome`, `telefone`, `funcao`, `contrato`, `escala`, `ativo`, `created_at`, `updated_at`) VALUES (4,'Angelo Cunha da SIlva','(11) 96541-8873','Vidraceiro','Registrado','5x2',1,'2026-06-10 18:37:54','2026-06-10 18:37:54');
INSERT INTO `funcionario` (`id`, `nome`, `telefone`, `funcao`, `contrato`, `escala`, `ativo`, `created_at`, `updated_at`) VALUES (5,'Edson Pereira dos Santos','(11) 99302-1145','Auxiliar','Temporário','Sem Escala Definida',1,'2026-06-10 18:38:23','2026-06-10 18:38:23');
INSERT INTO `funcionario` (`id`, `nome`, `telefone`, `funcao`, `contrato`, `escala`, `ativo`, `created_at`, `updated_at`) VALUES (6,'João Pedro Andrade','(11) 96796-2887','Auxiliar','Temporário','Sem Escala Definida',0,'2026-06-10 18:38:59','2026-06-10 18:38:59');
/*!40000 ALTER TABLE `funcionario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `historico_estoque`
--

LOCK TABLES `historico_estoque` WRITE;
/*!40000 ALTER TABLE `historico_estoque` DISABLE KEYS */;
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (1,1,1,'ENTRADA',28.00,28.00,'Entrada de 28,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:27:54');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (2,2,1,'ENTRADA',30.00,30.00,'Entrada de 30,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:30:39');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (3,3,1,'ENTRADA',15.00,15.00,'Entrada de 15,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:32:34');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (4,4,1,'ENTRADA',26.00,26.00,'Entrada de 26,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:34:49');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (5,5,1,'ENTRADA',20.00,20.00,'Entrada de 20,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:37:12');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (6,6,1,'ENTRADA',40.00,40.00,'Entrada de 40,000000 unidades de \'Vidro Espelhado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:38:39');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (7,7,1,'ENTRADA',19.00,19.00,'Entrada de 19,000000 unidades de \'Vidro Laminado 6mm (3+3)\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:40:21');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (8,8,1,'ENTRADA',130.00,130.00,'Entrada de 130,000000 unidades de \'Perfil de alumínio U 10mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 20:41:49');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (9,9,1,'ENTRADA',61.00,61.00,'Entrada de 61,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 20:43:50');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (10,10,1,'ENTRADA',53.00,53.00,'Entrada de 53,000000 unidades de \'Trilho Superior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 20:45:01');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (11,11,1,'ENTRADA',89.00,89.00,'Entrada de 89,000000 unidades de \'Perfil Canaleta PVC 8mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 20:46:05');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (12,12,1,'ENTRADA',100.00,100.00,'Entrada de 100,000000 unidades de \'Dobradiça para Vidro Temperado\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 20:47:26');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (13,13,1,'ENTRADA',30.00,30.00,'Entrada de 30,000000 unidades de \'Puxador Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 20:48:28');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (14,14,1,'ENTRADA',15.00,15.00,'Entrada de 15,000000 unidades de \'Fechadura Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 20:49:27');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (15,15,1,'ENTRADA',156.00,156.00,'Entrada de 156,000000 unidades de \'Ponteira Botão Inox\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 20:50:24');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (16,16,1,'ENTRADA',70.00,70.00,'Entrada de 70,000000 unidades de \'Rolamento para Box Deslizante\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 20:51:28');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (17,17,1,'ENTRADA',100.00,100.00,'Entrada de 100,000000 unidades de \'Silicone Neutro Incolor 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 20:52:45');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (18,18,1,'ENTRADA',32.00,32.00,'Entrada de 32,000000 unidades de \'Silicone Estrutural Preto 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 20:53:40');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (19,19,1,'ENTRADA',40.00,40.00,'Entrada de 40,000000 unidades de \'Fita Dupla Face Espuma 12mm\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 20:54:58');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (20,5,1,'SAIDA',10.00,10.00,'Saída de 10,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:56:16');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (21,4,1,'SAIDA',10.00,16.00,'Saída de 10,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:56:18');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (22,2,1,'SAIDA',10.00,20.00,'Saída de 10,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:56:21');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (23,1,1,'SAIDA',10.00,18.00,'Saída de 10,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:56:45');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (24,2,1,'SAIDA',10.00,10.00,'Saída de 10,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:56:47');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (25,3,1,'SAIDA',10.00,5.00,'Saída de 10,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:56:49');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (26,3,1,'ENTRADA',20.00,25.00,'Entrada de 20,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:01');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (27,2,1,'ENTRADA',20.00,30.00,'Entrada de 20,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:04');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (28,1,1,'ENTRADA',20.00,38.00,'Entrada de 20,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:08');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (29,2,1,'ENTRADA',5.00,35.00,'Entrada de 5,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:19');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (30,1,1,'ENTRADA',5.00,43.00,'Entrada de 5,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:21');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (31,3,1,'ENTRADA',5.00,30.00,'Entrada de 5,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:24');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (32,3,1,'SAIDA',10.00,20.00,'Saída de 10,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:39');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (33,4,1,'SAIDA',10.00,6.00,'Saída de 10,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:45');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (34,2,1,'SAIDA',10.00,25.00,'Saída de 10,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:57:47');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (35,4,1,'SAIDA',6.00,0.00,'Saída de 6,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:58:13');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (36,3,1,'SAIDA',10.00,10.00,'Saída de 10,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:58:15');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (37,2,1,'SAIDA',10.00,15.00,'Saída de 10,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:58:17');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (38,1,1,'SAIDA',1.00,42.00,'Saída de 1,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:58:19');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (39,2,1,'ENTRADA',17.00,32.00,'Entrada de 17,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:58:30');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (40,1,1,'SAIDA',30.00,12.00,'Saída de 30,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:58:42');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (41,4,1,'ENTRADA',4.00,4.00,'Entrada de 4,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:59:21');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (42,5,1,'SAIDA',10.00,0.00,'Saída de 10,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:59:34');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (43,6,1,'SAIDA',10.00,30.00,'Saída de 10,000000 unidades de \'Vidro Espelhado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:59:36');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (44,6,1,'ENTRADA',17.00,47.00,'Entrada de 17,000000 unidades de \'Vidro Espelhado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:59:42');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (45,5,1,'ENTRADA',30.00,30.00,'Entrada de 30,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 20:59:59');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (46,5,1,'ENTRADA',20.00,50.00,'Entrada de 20,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:00:06');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (47,5,1,'ENTRADA',5.00,55.00,'Entrada de 5,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:00:12');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (48,5,1,'ENTRADA',30.00,85.00,'Entrada de 30,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:00:21');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (49,6,1,'SAIDA',20.00,27.00,'Saída de 20,000000 unidades de \'Vidro Espelhado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:00:43');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (50,7,1,'SAIDA',10.00,9.00,'Saída de 10,000000 unidades de \'Vidro Laminado 6mm (3+3)\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:07');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (51,8,1,'SAIDA',10.00,120.00,'Saída de 10,000000 unidades de \'Perfil de alumínio U 10mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:10');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (52,9,1,'SAIDA',10.00,51.00,'Saída de 10,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:13');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (53,10,1,'SAIDA',10.00,43.00,'Saída de 10,000000 unidades de \'Trilho Superior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:15');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (54,12,1,'SAIDA',10.00,90.00,'Saída de 10,000000 unidades de \'Dobradiça para Vidro Temperado\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:17');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (55,11,1,'SAIDA',10.00,79.00,'Saída de 10,000000 unidades de \'Perfil Canaleta PVC 8mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:22');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (56,7,1,'ENTRADA',8.00,17.00,'Entrada de 8,000000 unidades de \'Vidro Laminado 6mm (3+3)\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:33');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (57,8,1,'ENTRADA',6.00,126.00,'Entrada de 6,000000 unidades de \'Perfil de alumínio U 10mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:35');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (58,9,1,'ENTRADA',10.00,61.00,'Entrada de 10,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:38');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (59,10,1,'ENTRADA',10.00,53.00,'Entrada de 10,000000 unidades de \'Trilho Superior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:40');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (60,11,1,'ENTRADA',5.00,84.00,'Entrada de 5,000000 unidades de \'Perfil Canaleta PVC 8mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:42');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (61,12,1,'ENTRADA',10.00,100.00,'Entrada de 10,000000 unidades de \'Dobradiça para Vidro Temperado\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:01:46');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (62,7,1,'ENTRADA',6.00,23.00,'Entrada de 6,000000 unidades de \'Vidro Laminado 6mm (3+3)\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:02:10');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (63,8,1,'ENTRADA',10.00,136.00,'Entrada de 10,000000 unidades de \'Perfil de alumínio U 10mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:02:15');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (64,9,1,'SAIDA',10.00,51.00,'Saída de 10,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:02:19');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (65,10,1,'SAIDA',10.00,43.00,'Saída de 10,000000 unidades de \'Trilho Superior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:02:22');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (66,11,1,'SAIDA',10.00,74.00,'Saída de 10,000000 unidades de \'Perfil Canaleta PVC 8mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:02:25');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (67,12,1,'SAIDA',10.00,90.00,'Saída de 10,000000 unidades de \'Dobradiça para Vidro Temperado\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:02:27');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (68,1,1,'SAIDA',10.00,2.00,'Saída de 10,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:07');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (69,2,1,'SAIDA',10.00,22.00,'Saída de 10,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:10');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (70,3,1,'SAIDA',10.00,0.00,'Saída de 10,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:12');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (71,7,1,'SAIDA',10.00,13.00,'Saída de 10,000000 unidades de \'Vidro Laminado 6mm (3+3)\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:25');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (72,8,1,'SAIDA',10.00,126.00,'Saída de 10,000000 unidades de \'Perfil de alumínio U 10mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:27');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (73,9,1,'SAIDA',10.00,41.00,'Saída de 10,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:29');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (74,10,1,'SAIDA',10.00,33.00,'Saída de 10,000000 unidades de \'Trilho Superior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:32');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (75,11,1,'SAIDA',10.00,64.00,'Saída de 10,000000 unidades de \'Perfil Canaleta PVC 8mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:35');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (76,12,1,'SAIDA',5.00,85.00,'Saída de 5,000000 unidades de \'Dobradiça para Vidro Temperado\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:39');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (77,9,1,'SAIDA',30.00,11.00,'Saída de 30,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:03:59');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (78,11,1,'SAIDA',20.00,44.00,'Saída de 20,000000 unidades de \'Perfil Canaleta PVC 8mm\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:04:19');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (79,19,1,'ENTRADA',10.00,50.00,'Entrada de 10,000000 unidades de \'Fita Dupla Face Espuma 12mm\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:04:49');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (80,19,1,'SAIDA',20.00,30.00,'Saída de 20,000000 unidades de \'Fita Dupla Face Espuma 12mm\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:02');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (81,19,1,'ENTRADA',50.00,80.00,'Entrada de 50,000000 unidades de \'Fita Dupla Face Espuma 12mm\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:10');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (82,19,1,'SAIDA',40.00,40.00,'Saída de 40,000000 unidades de \'Fita Dupla Face Espuma 12mm\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:19');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (83,13,1,'ENTRADA',10.00,40.00,'Entrada de 10,000000 unidades de \'Puxador Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:32');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (84,14,1,'ENTRADA',10.00,25.00,'Entrada de 10,000000 unidades de \'Fechadura Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:35');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (85,15,1,'ENTRADA',10.00,166.00,'Entrada de 10,000000 unidades de \'Ponteira Botão Inox\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:37');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (86,16,1,'ENTRADA',10.00,80.00,'Entrada de 10,000000 unidades de \'Rolamento para Box Deslizante\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:39');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (87,17,1,'ENTRADA',10.00,110.00,'Entrada de 10,000000 unidades de \'Silicone Neutro Incolor 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:42');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (88,18,1,'ENTRADA',10.00,42.00,'Entrada de 10,000000 unidades de \'Silicone Estrutural Preto 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:45');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (89,13,1,'SAIDA',20.00,20.00,'Saída de 20,000000 unidades de \'Puxador Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:05:55');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (90,14,1,'SAIDA',10.00,15.00,'Saída de 10,000000 unidades de \'Fechadura Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:00');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (91,15,1,'SAIDA',10.00,156.00,'Saída de 10,000000 unidades de \'Ponteira Botão Inox\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:04');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (92,16,1,'SAIDA',20.00,60.00,'Saída de 20,000000 unidades de \'Rolamento para Box Deslizante\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:08');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (93,17,1,'SAIDA',30.00,80.00,'Saída de 30,000000 unidades de \'Silicone Neutro Incolor 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:13');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (94,18,1,'SAIDA',30.00,12.00,'Saída de 30,000000 unidades de \'Silicone Estrutural Preto 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:16');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (95,1,1,'ENTRADA',10.00,12.00,'Entrada de 10,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:33');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (96,2,1,'ENTRADA',10.00,32.00,'Entrada de 10,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:35');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (97,3,1,'ENTRADA',10.00,10.00,'Entrada de 10,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:37');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (98,4,1,'ENTRADA',10.00,14.00,'Entrada de 10,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:39');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (99,5,1,'ENTRADA',10.00,95.00,'Entrada de 10,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:41');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (100,6,1,'ENTRADA',10.00,37.00,'Entrada de 10,000000 unidades de \'Vidro Espelhado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:44');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (101,1,1,'ENTRADA',40.00,52.00,'Entrada de 40,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:56');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (102,2,1,'ENTRADA',20.00,52.00,'Entrada de 20,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:06:59');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (103,3,1,'ENTRADA',10.00,20.00,'Entrada de 10,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:02');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (104,4,1,'ENTRADA',50.00,64.00,'Entrada de 50,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:12');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (105,6,1,'ENTRADA',10.00,47.00,'Entrada de 10,000000 unidades de \'Vidro Espelhado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:17');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (106,5,1,'ENTRADA',1.00,96.00,'Entrada de 1,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:20');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (107,2,1,'ENTRADA',30.00,82.00,'Entrada de 30,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:41');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (108,3,1,'ENTRADA',20.00,40.00,'Entrada de 20,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:43');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (109,4,1,'ENTRADA',30.00,94.00,'Entrada de 30,000000 unidades de \'Vidro Temperado 10mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:48');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (110,5,1,'ENTRADA',1.00,97.00,'Entrada de 1,000000 unidades de \'Vidro Canelado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:07:50');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (111,2,1,'SAIDA',40.00,42.00,'Saída de 40,000000 unidades de \'Vidro Liso Incolor 6mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:08:03');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (112,3,1,'SAIDA',40.00,0.00,'Saída de 40,000000 unidades de \'Vidro Temperado 8mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:08:07');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (113,6,1,'SAIDA',40.00,7.00,'Saída de 40,000000 unidades de \'Vidro Espelhado 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:08:11');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (114,1,1,'ENTRADA',20.00,72.00,'Entrada de 20,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:08:20');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (115,1,1,'ENTRADA',50.00,122.00,'Entrada de 50,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:08:33');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (116,1,1,'SAIDA',70.00,52.00,'Saída de 70,000000 unidades de \'Vidro Liso Incolor 4mm\' em \'Galpão Vidros\'',NULL,'MANUAL',NULL,'2026-06-10 21:08:42');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (117,13,1,'ENTRADA',20.00,40.00,'Entrada de 20,000000 unidades de \'Puxador Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:09:23');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (118,14,1,'ENTRADA',10.00,25.00,'Entrada de 10,000000 unidades de \'Fechadura Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:09:25');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (119,15,1,'ENTRADA',40.00,196.00,'Entrada de 40,000000 unidades de \'Ponteira Botão Inox\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:09:29');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (120,16,1,'ENTRADA',10.00,70.00,'Entrada de 10,000000 unidades de \'Rolamento para Box Deslizante\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:09:32');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (121,17,1,'ENTRADA',50.00,130.00,'Entrada de 50,000000 unidades de \'Silicone Neutro Incolor 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:09:43');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (122,18,1,'ENTRADA',40.00,52.00,'Entrada de 40,000000 unidades de \'Silicone Estrutural Preto 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:09:48');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (123,13,1,'SAIDA',40.00,0.00,'Saída de 40,000000 unidades de \'Puxador Inox para Vidro\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:10:08');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (124,15,1,'SAIDA',30.00,166.00,'Saída de 30,000000 unidades de \'Ponteira Botão Inox\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:10:27');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (125,16,1,'SAIDA',20.00,50.00,'Saída de 20,000000 unidades de \'Rolamento para Box Deslizante\' em \'Estante B\'',NULL,'MANUAL',NULL,'2026-06-10 21:10:32');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (126,18,1,'SAIDA',50.00,2.00,'Saída de 50,000000 unidades de \'Silicone Estrutural Preto 280ml\' em \'Estante C\'',NULL,'MANUAL',NULL,'2026-06-10 21:10:42');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (127,9,1,'ENTRADA',30.00,41.00,'Entrada de 30,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:11:08');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (128,9,1,'ENTRADA',10.00,51.00,'Entrada de 10,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:11:19');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (129,9,1,'ENTRADA',30.00,81.00,'Entrada de 30,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:11:29');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (130,9,1,'ENTRADA',10.00,91.00,'Entrada de 10,000000 unidades de \'Trilho Inferior Box Aluminío\' em \'Estante A\'',NULL,'MANUAL',NULL,'2026-06-10 21:11:42');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (131,12,1,'SAIDA',4.00,81.00,'Saída de 4,000000 unidades de \'Dobradiça para Vidro Temperado\' em \'Estante B\'',1,'PEDIDO',NULL,'2026-06-10 21:40:23');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (132,19,1,'SAIDA',10.00,30.00,'Saída de 10,000000 unidades de \'Fita Dupla Face Espuma 12mm\' em \'Estante C\'',2,'PEDIDO',NULL,'2026-06-10 21:53:02');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (133,15,1,'SAIDA',2.00,164.00,'Saída de 2,000000 unidades de \'Ponteira Botão Inox\' em \'Estante B\'',3,'PEDIDO',NULL,'2026-06-10 21:53:33');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (134,17,1,'SAIDA',10.00,120.00,'Saída de 10,000000 unidades de \'Silicone Neutro Incolor 280ml\' em \'Estante C\'',3,'PEDIDO',NULL,'2026-06-10 21:53:33');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (135,8,1,'SAIDA',3.00,123.00,'Saída de 3,000000 unidades de \'Perfil de alumínio U 10mm\' em \'Estante A\'',4,'PEDIDO',NULL,'2026-06-10 21:54:02');
INSERT INTO `historico_estoque` (`id`, `estoque_id`, `usuario_id`, `tipo_movimentacao`, `quantidade`, `quantidade_atual`, `observacao`, `pedido_id`, `origem`, `motivo_perda`, `data_movimentacao`) VALUES (136,7,1,'SAIDA',2.00,11.00,'Baixa de estoque por conclusao de agendamento - Saída de 2,000000 unidades de \'Vidro Laminado 6mm (3+3)\' em \'Galpão Vidros\'',NULL,'AGENDAMENTO',NULL,'2026-06-10 22:19:29');
/*!40000 ALTER TABLE `historico_estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `item_pedido`
--

LOCK TABLES `item_pedido` WRITE;
/*!40000 ALTER TABLE `item_pedido` DISABLE KEYS */;
INSERT INTO `item_pedido` (`id`, `pedido_id`, `estoque_id`, `quantidade_solicitada`, `preco_unitario_negociado`, `observacao`, `created_at`, `updated_at`) VALUES (1,1,12,4.00000,45.00000,'','2026-06-10 21:40:23','2026-06-10 21:40:23');
INSERT INTO `item_pedido` (`id`, `pedido_id`, `estoque_id`, `quantidade_solicitada`, `preco_unitario_negociado`, `observacao`, `created_at`, `updated_at`) VALUES (2,2,19,10.00000,18.00000,'','2026-06-10 21:53:02','2026-06-10 21:53:02');
INSERT INTO `item_pedido` (`id`, `pedido_id`, `estoque_id`, `quantidade_solicitada`, `preco_unitario_negociado`, `observacao`, `created_at`, `updated_at`) VALUES (3,3,15,2.00000,22.00000,'','2026-06-10 21:53:33','2026-06-10 21:53:33');
INSERT INTO `item_pedido` (`id`, `pedido_id`, `estoque_id`, `quantidade_solicitada`, `preco_unitario_negociado`, `observacao`, `created_at`, `updated_at`) VALUES (4,3,17,10.00000,22.00000,'','2026-06-10 21:53:33','2026-06-10 21:53:33');
INSERT INTO `item_pedido` (`id`, `pedido_id`, `estoque_id`, `quantidade_solicitada`, `preco_unitario_negociado`, `observacao`, `created_at`, `updated_at`) VALUES (5,4,8,3.00000,18.50000,'','2026-06-10 21:54:02','2026-06-10 21:54:02');
/*!40000 ALTER TABLE `item_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (1,'2026-06-10 17:22:56',5,'Login bem-sucedido. Usuário ID 1 autenticado com e-mail: admin@leovidros.com.br.','2026-06-10 20:22:55','2026-06-10 20:22:55');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (2,'2026-06-10 17:27:54',5,'Produto ID 1 atualizado com sucesso. Nome: Vidro Liso Incolor 4mm, Preço: 85,00.','2026-06-10 20:27:54','2026-06-10 20:27:54');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (3,'2026-06-10 17:30:40',5,'Produto ID 2 atualizado com sucesso. Nome: Vidro Liso Incolor 6mm, Preço: 130,00.','2026-06-10 20:30:39','2026-06-10 20:30:39');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (4,'2026-06-10 17:32:35',5,'Produto ID 3 atualizado com sucesso. Nome: Vidro Temperado 8mm, Preço: 280,00.','2026-06-10 20:32:34','2026-06-10 20:32:34');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (5,'2026-06-10 17:34:49',5,'Produto ID 4 atualizado com sucesso. Nome: Vidro Temperado 10mm, Preço: 360,00.','2026-06-10 20:34:49','2026-06-10 20:34:49');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (6,'2026-06-10 17:37:13',5,'Produto ID 5 atualizado com sucesso. Nome: Vidro Canelado 4mm, Preço: 95,00.','2026-06-10 20:37:12','2026-06-10 20:37:12');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (7,'2026-06-10 17:38:39',5,'Produto ID 6 atualizado com sucesso. Nome: Vidro Espelhado 4mm, Preço: 120,00.','2026-06-10 20:38:39','2026-06-10 20:38:39');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (8,'2026-06-10 17:40:21',5,'Produto ID 7 atualizado com sucesso. Nome: Vidro Laminado 6mm (3+3), Preço: 210,00.','2026-06-10 20:40:21','2026-06-10 20:40:21');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (9,'2026-06-10 17:41:49',5,'Produto ID 8 atualizado com sucesso. Nome: Perfil de alumínio U 10mm, Preço: 18,50.','2026-06-10 20:41:49','2026-06-10 20:41:49');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (10,'2026-06-10 17:43:50',5,'Produto ID 9 atualizado com sucesso. Nome: Trilho Inferior Box Aluminío, Preço: 35,00.','2026-06-10 20:43:50','2026-06-10 20:43:50');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (11,'2026-06-10 17:45:01',5,'Produto ID 10 atualizado com sucesso. Nome: Trilho Superior Box Aluminío, Preço: 32,00.','2026-06-10 20:45:01','2026-06-10 20:45:01');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (12,'2026-06-10 17:46:06',5,'Produto ID 11 atualizado com sucesso. Nome: Perfil Canaleta PVC 8mm, Preço: 8,00.','2026-06-10 20:46:05','2026-06-10 20:46:05');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (13,'2026-06-10 17:47:27',5,'Produto ID 12 atualizado com sucesso. Nome: Dobradiça para Vidro Temperado, Preço: 45,00.','2026-06-10 20:47:26','2026-06-10 20:47:26');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (14,'2026-06-10 17:48:28',5,'Produto ID 13 atualizado com sucesso. Nome: Puxador Inox para Vidro, Preço: 65,00.','2026-06-10 20:48:28','2026-06-10 20:48:28');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (15,'2026-06-10 17:49:27',5,'Produto ID 14 atualizado com sucesso. Nome: Fechadura Inox para Vidro, Preço: 120,00.','2026-06-10 20:49:27','2026-06-10 20:49:27');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (16,'2026-06-10 17:50:24',5,'Produto ID 15 atualizado com sucesso. Nome: Ponteira Botão Inox, Preço: 22,00.','2026-06-10 20:50:24','2026-06-10 20:50:24');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (17,'2026-06-10 17:51:29',5,'Produto ID 16 atualizado com sucesso. Nome: Rolamento para Box Deslizante, Preço: 15,00.','2026-06-10 20:51:28','2026-06-10 20:51:28');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (18,'2026-06-10 17:52:45',5,'Produto ID 17 atualizado com sucesso. Nome: Silicone Neutro Incolor 280ml, Preço: 22,00.','2026-06-10 20:52:45','2026-06-10 20:52:45');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (19,'2026-06-10 17:53:40',5,'Produto ID 18 atualizado com sucesso. Nome: Silicone Estrutural Preto 280ml, Preço: 38,00.','2026-06-10 20:53:40','2026-06-10 20:53:40');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (20,'2026-06-10 17:54:58',5,'Produto ID 19 atualizado com sucesso. Nome: Fita Dupla Face Espuma 12mm, Preço: 18,00.','2026-06-10 20:54:58','2026-06-10 20:54:58');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (21,'2026-06-10 17:58:13',5,'Produto ID 4 atualizado com sucesso. Nome: Vidro Temperado 10mm, Preço: 360,00.','2026-06-10 20:58:13','2026-06-10 20:58:13');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (22,'2026-06-10 17:59:22',5,'Produto ID 4 atualizado com sucesso. Nome: Vidro Temperado 10mm, Preço: 360,00.','2026-06-10 20:59:21','2026-06-10 20:59:21');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (23,'2026-06-10 17:59:35',5,'Produto ID 5 atualizado com sucesso. Nome: Vidro Canelado 4mm, Preço: 95,00.','2026-06-10 20:59:34','2026-06-10 20:59:34');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (24,'2026-06-10 18:00:00',5,'Produto ID 5 atualizado com sucesso. Nome: Vidro Canelado 4mm, Preço: 95,00.','2026-06-10 20:59:59','2026-06-10 20:59:59');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (25,'2026-06-10 18:03:13',5,'Produto ID 3 atualizado com sucesso. Nome: Vidro Temperado 8mm, Preço: 280,00.','2026-06-10 21:03:12','2026-06-10 21:03:12');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (26,'2026-06-10 18:06:38',5,'Produto ID 3 atualizado com sucesso. Nome: Vidro Temperado 8mm, Preço: 280,00.','2026-06-10 21:06:37','2026-06-10 21:06:37');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (27,'2026-06-10 18:08:08',5,'Produto ID 3 atualizado com sucesso. Nome: Vidro Temperado 8mm, Preço: 280,00.','2026-06-10 21:08:07','2026-06-10 21:08:07');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (28,'2026-06-10 18:10:08',5,'Produto ID 13 atualizado com sucesso. Nome: Puxador Inox para Vidro, Preço: 65,00.','2026-06-10 21:10:08','2026-06-10 21:10:08');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (29,'2026-06-10 18:12:27',1,'Listagem de pedidos: 0 registros.','2026-06-10 21:12:27','2026-06-10 21:12:27');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (30,'2026-06-10 18:23:11',1,'Listagem de pedidos: 0 registros.','2026-06-10 21:23:10','2026-06-10 21:23:10');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (31,'2026-06-10 18:26:31',5,'Novo Cliente ID 1 cadastrado com sucesso. Nome: Ricardo Aparecido Ferreira.','2026-06-10 21:26:31','2026-06-10 21:26:31');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (32,'2026-06-10 18:27:24',5,'Novo Cliente ID 2 cadastrado com sucesso. Nome: Juliana Costa Barbosa.','2026-06-10 21:27:23','2026-06-10 21:27:23');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (33,'2026-06-10 18:28:46',5,'Novo Cliente ID 3 cadastrado com sucesso. Nome: Marcos Vinicius Almeida.','2026-06-10 21:28:46','2026-06-10 21:28:46');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (34,'2026-06-10 18:30:22',5,'Novo Cliente ID 4 cadastrado com sucesso. Nome: Construtora Alves & Prado Ltda.','2026-06-10 21:30:21','2026-06-10 21:30:21');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (35,'2026-06-10 18:30:58',5,'Novo Cliente ID 5 cadastrado com sucesso. Nome: Patricia Nunes Rodrigues.','2026-06-10 21:30:58','2026-06-10 21:30:58');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (36,'2026-06-10 18:31:38',5,'Novo Cliente ID 6 cadastrado com sucesso. Nome: Diego Henrique Lopes.','2026-06-10 21:31:38','2026-06-10 21:31:38');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (37,'2026-06-10 18:32:21',5,'Novo Cliente ID 7 cadastrado com sucesso. Nome:  Simone Tavares de Oliveira.','2026-06-10 21:32:20','2026-06-10 21:32:20');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (38,'2026-06-10 18:32:57',5,'Novo Cliente ID 8 cadastrado com sucesso. Nome: Amadeu Pires Santos.','2026-06-10 21:32:57','2026-06-10 21:32:57');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (39,'2026-06-10 18:40:24',5,'Novo Cliente ID 9 cadastrado com sucesso. Nome: Phelipe.','2026-06-10 21:40:23','2026-06-10 21:40:23');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (40,'2026-06-10 18:40:24',5,'Novo Pedido ID 1 criado com sucesso. Tipo: produto, Total: 180,00.','2026-06-10 21:40:23','2026-06-10 21:40:23');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (41,'2026-06-10 18:40:50',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:40:49','2026-06-10 21:40:49');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (42,'2026-06-10 18:40:54',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:40:53','2026-06-10 21:40:53');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (43,'2026-06-10 18:41:07',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:41:06','2026-06-10 21:41:06');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (44,'2026-06-10 18:41:18',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:41:17','2026-06-10 21:41:17');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (45,'2026-06-10 18:42:24',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:42:24','2026-06-10 21:42:24');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (46,'2026-06-10 18:42:39',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:42:38','2026-06-10 21:42:38');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (47,'2026-06-10 18:43:21',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:43:20','2026-06-10 21:43:20');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (48,'2026-06-10 18:50:42',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:50:42','2026-06-10 21:50:42');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (49,'2026-06-10 18:52:18',1,'Listagem de pedidos: 1 registros.','2026-06-10 21:52:18','2026-06-10 21:52:18');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (50,'2026-06-10 18:53:02',5,'Novo Pedido ID 2 criado com sucesso. Tipo: produto, Total: 180,00.','2026-06-10 21:53:02','2026-06-10 21:53:02');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (51,'2026-06-10 18:53:33',5,'Novo Pedido ID 3 criado com sucesso. Tipo: produto, Total: 264,00.','2026-06-10 21:53:33','2026-06-10 21:53:33');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (52,'2026-06-10 18:54:02',5,'Novo Cliente ID 11 cadastrado com sucesso. Nome: Leticia.','2026-06-10 21:54:02','2026-06-10 21:54:02');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (53,'2026-06-10 18:54:02',5,'Novo Pedido ID 4 criado com sucesso. Tipo: produto, Total: 55,50.','2026-06-10 21:54:02','2026-06-10 21:54:02');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (54,'2026-06-10 18:55:51',5,'Novo Pedido ID 5 criado com sucesso. Tipo: serviço, Total: 3000,00.','2026-06-10 21:55:51','2026-06-10 21:55:51');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (55,'2026-06-10 18:56:47',5,'Novo Pedido ID 6 criado com sucesso. Tipo: serviço, Total: 1900,00.','2026-06-10 21:56:46','2026-06-10 21:56:46');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (56,'2026-06-10 18:59:01',5,'Novo Pedido ID 7 criado com sucesso. Tipo: serviço, Total: 180,00.','2026-06-10 21:59:00','2026-06-10 21:59:00');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (57,'2026-06-10 19:00:22',5,'Novo Pedido ID 8 criado com sucesso. Tipo: serviço, Total: 890,00.','2026-06-10 22:00:22','2026-06-10 22:00:22');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (58,'2026-06-10 19:01:11',5,'Novo Pedido ID 9 criado com sucesso. Tipo: serviço, Total: 1100,00.','2026-06-10 22:01:10','2026-06-10 22:01:10');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (59,'2026-06-10 19:01:53',5,'Novo Pedido ID 10 criado com sucesso. Tipo: serviço, Total: 120,00.','2026-06-10 22:01:52','2026-06-10 22:01:52');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (60,'2026-06-10 19:02:31',5,'Novo Pedido ID 11 criado com sucesso. Tipo: serviço, Total: 520,00.','2026-06-10 22:02:30','2026-06-10 22:02:30');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (61,'2026-06-10 19:03:59',1,'Pedido ID 5 atualizado com sucesso. Total: 3000,00.','2026-06-10 22:03:59','2026-06-10 22:03:59');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (62,'2026-06-10 19:03:59',1,'Lista de produtos do Serviço ID 1 substituída (3 itens ativos).','2026-06-10 22:03:59','2026-06-10 22:03:59');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (63,'2026-06-10 19:04:10',1,'Pedido ID 5 atualizado com sucesso. Total: 3000,00.','2026-06-10 22:04:10','2026-06-10 22:04:10');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (64,'2026-06-10 19:04:10',1,'Lista de produtos do Serviço ID 1 substituída (3 itens ativos).','2026-06-10 22:04:10','2026-06-10 22:04:10');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (65,'2026-06-10 19:04:24',1,'Pedido ID 5 atualizado com sucesso. Total: 3000,00.','2026-06-10 22:04:24','2026-06-10 22:04:24');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (66,'2026-06-10 19:04:24',1,'Lista de produtos do Serviço ID 1 substituída (3 itens ativos).','2026-06-10 22:04:24','2026-06-10 22:04:24');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (67,'2026-06-10 19:05:45',5,'Novo Agendamento ID 1 criado com sucesso. Tipo: ORCAMENTO, Data: 2026-06-10.','2026-06-10 22:05:45','2026-06-10 22:05:45');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (68,'2026-06-10 19:06:12',5,'Novo Agendamento ID 2 criado com sucesso. Tipo: ORCAMENTO, Data: 2026-06-10.','2026-06-10 22:06:12','2026-06-10 22:06:12');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (69,'2026-06-10 19:06:39',5,'Novo Agendamento ID 3 criado com sucesso. Tipo: ORCAMENTO, Data: 2026-06-10.','2026-06-10 22:06:38','2026-06-10 22:06:38');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (70,'2026-06-10 19:07:05',5,'Novo Agendamento ID 4 criado com sucesso. Tipo: ORCAMENTO, Data: 2026-06-12.','2026-06-10 22:07:05','2026-06-10 22:07:05');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (71,'2026-06-10 19:07:50',5,'Novo Agendamento ID 5 criado com sucesso. Tipo: ORCAMENTO, Data: 2026-06-17.','2026-06-10 22:07:50','2026-06-10 22:07:50');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (72,'2026-06-10 19:09:11',5,'Novo Agendamento ID 6 criado com sucesso. Tipo: ORCAMENTO, Data: 2026-06-18.','2026-06-10 22:09:11','2026-06-10 22:09:11');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (73,'2026-06-10 19:09:35',5,'Novo Agendamento ID 7 criado com sucesso. Tipo: ORCAMENTO, Data: 2026-06-10.','2026-06-10 22:09:34','2026-06-10 22:09:34');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (74,'2026-06-10 19:10:43',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:10:42','2026-06-10 22:10:42');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (75,'2026-06-10 19:10:59',5,'Orçamento ID 1 criado. Número: ORC-2026-P5, Pedido: 5, Itens: 3, Total: 400.','2026-06-10 22:10:58','2026-06-10 22:10:58');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (76,'2026-06-10 19:14:50',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:14:49','2026-06-10 22:14:49');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (77,'2026-06-10 19:15:11',5,'Orçamento ID 2 criado. Número: ORC-2026-P6, Pedido: 6, Itens: 1, Total: 150.','2026-06-10 22:15:11','2026-06-10 22:15:11');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (78,'2026-06-10 19:15:29',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:15:29','2026-06-10 22:15:29');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (79,'2026-06-10 19:16:04',5,'Orçamento ID 3 criado. Número: ORC-2026-P8, Pedido: 8, Itens: 2, Total: 410.','2026-06-10 22:16:03','2026-06-10 22:16:03');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (80,'2026-06-10 19:16:17',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:16:17','2026-06-10 22:16:17');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (81,'2026-06-10 19:16:44',5,'Orçamento ID 4 criado. Número: ORC-2026-P11, Pedido: 11, Itens: 2, Total: 392.','2026-06-10 22:16:43','2026-06-10 22:16:43');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (82,'2026-06-10 19:16:46',5,'Orçamento ID 4 atualizado com sucesso.','2026-06-10 22:16:45','2026-06-10 22:16:45');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (83,'2026-06-10 19:17:14',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:17:13','2026-06-10 22:17:13');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (84,'2026-06-10 19:17:30',5,'Orçamento ID 5 criado. Número: ORC-2026-P10, Pedido: 10, Itens: 1, Total: 420.','2026-06-10 22:17:29','2026-06-10 22:17:29');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (85,'2026-06-10 19:17:31',5,'Orçamento ID 5 atualizado com sucesso.','2026-06-10 22:17:31','2026-06-10 22:17:31');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (86,'2026-06-10 19:18:27',1,'Pedido ID 10 atualizado com sucesso. Total: 120,00.','2026-06-10 22:18:26','2026-06-10 22:18:26');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (87,'2026-06-10 19:18:27',1,'Lista de produtos do Serviço ID 6 substituída (1 itens ativos).','2026-06-10 22:18:26','2026-06-10 22:18:26');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (88,'2026-06-10 19:18:57',5,'Novo Agendamento ID 8 criado com sucesso. Tipo: SERVICO, Data: 2026-06-10.','2026-06-10 22:18:57','2026-06-10 22:18:57');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (89,'2026-06-10 19:19:29',5,'Agendamento de serviço ID 8 concluído com informe de utilização.','2026-06-10 22:19:29','2026-06-10 22:19:29');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (90,'2026-06-10 19:20:49',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:20:48','2026-06-10 22:20:48');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (91,'2026-06-10 19:20:59',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:20:59','2026-06-10 22:20:59');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (92,'2026-06-10 19:21:22',5,'Orçamento ID 6 criado. Número: ORC-2026-P9, Pedido: 9, Itens: 2, Total: 1460.','2026-06-10 22:21:22','2026-06-10 22:21:22');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (93,'2026-06-10 19:22:10',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:22:09','2026-06-10 22:22:09');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (94,'2026-06-10 19:22:15',5,'Orçamento ID 3 atualizado com sucesso.','2026-06-10 22:22:14','2026-06-10 22:22:14');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (95,'2026-06-10 19:23:06',5,'Novo Agendamento ID 9 criado com sucesso. Tipo: SERVICO, Data: 2026-06-10.','2026-06-10 22:23:05','2026-06-10 22:23:05');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (96,'2026-06-10 19:23:35',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:23:34','2026-06-10 22:23:34');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (97,'2026-06-10 19:24:45',5,'Nova Solicitacao ID 1 criada. Nome: Ana Julia, E-mail: anajulia@gmail.com. Status: PENDENTE.','2026-06-10 22:24:44','2026-06-10 22:24:44');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (98,'2026-06-10 19:25:16',5,'Nova Solicitacao ID 2 criada. Nome: Maria Eduarda, E-mail: eduarda123@gmail.com. Status: PENDENTE.','2026-06-10 22:25:16','2026-06-10 22:25:16');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (99,'2026-06-10 19:25:51',5,'Nova Solicitacao ID 3 criada. Nome: Pedro Henrique, E-mail: henripedro@gmail.com.br. Status: PENDENTE.','2026-06-10 22:25:50','2026-06-10 22:25:50');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (100,'2026-06-10 19:26:42',5,'Login bem-sucedido. Usuário ID 1 autenticado com e-mail: admin@leovidros.com.br.','2026-06-10 22:26:42','2026-06-10 22:26:42');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (101,'2026-06-10 19:26:51',1,'Solicitacao ID 2 aceita. Status alterado para ACEITO.','2026-06-10 22:26:50','2026-06-10 22:26:50');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (102,'2026-06-10 19:26:51',5,'Usuário ID 2 criado com sucesso. E-mail: eduarda123@gmail.com.','2026-06-10 22:26:51','2026-06-10 22:26:51');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (103,'2026-06-10 19:26:51',5,'Novo Usuário ID 2 criado a partir da Solicitacao ID 2. E-mail: eduarda123@gmail.com.','2026-06-10 22:26:51','2026-06-10 22:26:51');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (104,'2026-06-10 19:26:53',4,'Solicitacao ID 3 recusada. Status alterado para RECUSADO.','2026-06-10 22:26:53','2026-06-10 22:26:53');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (105,'2026-06-10 19:26:57',1,'Email de ACEITE com credenciais enviado para: eduarda123@gmail.com.','2026-06-10 22:26:56','2026-06-10 22:26:56');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (106,'2026-06-10 19:26:58',1,'Email de RECUSA enviado para: henripedro@gmail.com.br.','2026-06-10 22:26:57','2026-06-10 22:26:57');
INSERT INTO `log` (`id`, `data_hora`, `id_categoria`, `mensagem`, `created_at`, `updated_at`) VALUES (107,'2026-06-10 19:27:12',1,'Listagem de pedidos: 11 registros.','2026-06-10 22:27:12','2026-06-10 22:27:12');
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `metrica_estoque`
--

LOCK TABLES `metrica_estoque` WRITE;
/*!40000 ALTER TABLE `metrica_estoque` DISABLE KEYS */;
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (1,20,100,'2026-06-10 20:27:54','2026-06-10 20:27:54');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (2,15,80,'2026-06-10 20:30:39','2026-06-10 20:30:39');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (3,10,50,'2026-06-10 20:32:34','2026-06-10 20:32:34');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (4,5,30,'2026-06-10 20:34:49','2026-06-10 20:34:49');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (5,10,40,'2026-06-10 20:37:12','2026-06-10 20:37:12');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (6,10,50,'2026-06-10 20:38:39','2026-06-10 20:38:39');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (7,5,30,'2026-06-10 20:40:21','2026-06-10 20:40:21');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (8,50,200,'2026-06-10 20:41:49','2026-06-10 20:41:49');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (9,20,80,'2026-06-10 20:43:50','2026-06-10 20:43:50');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (10,20,80,'2026-06-10 20:45:01','2026-06-10 20:45:01');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (11,50,200,'2026-06-10 20:46:05','2026-06-10 20:46:05');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (12,20,100,'2026-06-10 20:47:26','2026-06-10 20:47:26');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (13,10,50,'2026-06-10 20:48:28','2026-06-10 20:48:28');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (14,5,20,'2026-06-10 20:49:27','2026-06-10 20:49:27');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (15,30,120,'2026-06-10 20:50:24','2026-06-10 20:50:24');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (16,30,100,'2026-06-10 20:51:28','2026-06-10 20:51:28');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (17,20,100,'2026-06-10 20:52:45','2026-06-10 20:52:45');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (18,10,50,'2026-06-10 20:53:40','2026-06-10 20:53:40');
INSERT INTO `metrica_estoque` (`id`, `nivel_minimo`, `nivel_maximo`, `created_at`, `updated_at`) VALUES (19,15,60,'2026-06-10 20:54:58','2026-06-10 20:54:58');
/*!40000 ALTER TABLE `metrica_estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `orcamento`
--

LOCK TABLES `orcamento` WRITE;
/*!40000 ALTER TABLE `orcamento` DISABLE KEYS */;
INSERT INTO `orcamento` (`id`, `pedido_id`, `cliente_id`, `status_id`, `numero_orcamento`, `data_orcamento`, `observacoes`, `prazo_instalacao`, `garantia`, `forma_pagamento`, `valor_subtotal`, `valor_desconto`, `valor_total`, `pdf_path`, `status_fila`, `ativo`, `created_at`, `updated_at`) VALUES (1,5,2,13,'ORC-2026-P5','2026-06-10','Os itens estão com medidas, cores e espessura conforme a medida orçada.','15 dias úteis','8 meses','50% de entrada e 50% após a finalização do serviço',400.00,0.00,400.00,NULL,'PENDENTE',1,'2026-06-10 22:10:58','2026-06-10 22:10:58');
INSERT INTO `orcamento` (`id`, `pedido_id`, `cliente_id`, `status_id`, `numero_orcamento`, `data_orcamento`, `observacoes`, `prazo_instalacao`, `garantia`, `forma_pagamento`, `valor_subtotal`, `valor_desconto`, `valor_total`, `pdf_path`, `status_fila`, `ativo`, `created_at`, `updated_at`) VALUES (2,6,6,14,'ORC-2026-P6','2026-06-10','Os itens estão com medidas, cores e espessura conforme a medida orçada.','15 dias úteis','12 meses','50% de entrada e 50% após a finalização do serviço',150.00,0.00,150.00,NULL,'PENDENTE',1,'2026-06-10 22:15:11','2026-06-10 22:15:11');
INSERT INTO `orcamento` (`id`, `pedido_id`, `cliente_id`, `status_id`, `numero_orcamento`, `data_orcamento`, `observacoes`, `prazo_instalacao`, `garantia`, `forma_pagamento`, `valor_subtotal`, `valor_desconto`, `valor_total`, `pdf_path`, `status_fila`, `ativo`, `created_at`, `updated_at`) VALUES (3,8,1,14,'ORC-2026-P8','2026-06-10','Os itens estão com medidas, cores e espessura conforme a medida orçada.','15 dias úteis','3 meses','50% de entrada e 50% após a finalização do serviço',410.00,0.00,410.00,NULL,'PENDENTE',1,'2026-06-10 22:16:03','2026-06-10 22:16:03');
INSERT INTO `orcamento` (`id`, `pedido_id`, `cliente_id`, `status_id`, `numero_orcamento`, `data_orcamento`, `observacoes`, `prazo_instalacao`, `garantia`, `forma_pagamento`, `valor_subtotal`, `valor_desconto`, `valor_total`, `pdf_path`, `status_fila`, `ativo`, `created_at`, `updated_at`) VALUES (4,11,7,13,'ORC-2026-P11','2026-06-10','Os itens estão com medidas, cores e espessura conforme a medida orçada.','15 dias úteis','5 meses','50% de entrada e 50% após a finalização do serviço',392.00,0.00,392.00,NULL,'PENDENTE',1,'2026-06-10 22:16:43','2026-06-10 22:16:43');
INSERT INTO `orcamento` (`id`, `pedido_id`, `cliente_id`, `status_id`, `numero_orcamento`, `data_orcamento`, `observacoes`, `prazo_instalacao`, `garantia`, `forma_pagamento`, `valor_subtotal`, `valor_desconto`, `valor_total`, `pdf_path`, `status_fila`, `ativo`, `created_at`, `updated_at`) VALUES (5,10,8,14,'ORC-2026-P10','2026-06-10','Os itens estão com medidas, cores e espessura conforme a medida orçada.','15 dias úteis','2 meses','50% de entrada e 50% após a finalização do serviço',420.00,0.00,420.00,NULL,'PENDENTE',1,'2026-06-10 22:17:29','2026-06-10 22:17:29');
INSERT INTO `orcamento` (`id`, `pedido_id`, `cliente_id`, `status_id`, `numero_orcamento`, `data_orcamento`, `observacoes`, `prazo_instalacao`, `garantia`, `forma_pagamento`, `valor_subtotal`, `valor_desconto`, `valor_total`, `pdf_path`, `status_fila`, `ativo`, `created_at`, `updated_at`) VALUES (6,9,4,13,'ORC-2026-P9','2026-06-10','Os itens estão com medidas, cores e espessura conforme a medida orçada.','15 dias úteis','6 meses','50% de entrada e 50% após a finalização do serviço',1460.00,0.00,1460.00,NULL,'PENDENTE',1,'2026-06-10 22:21:22','2026-06-10 22:21:22');
/*!40000 ALTER TABLE `orcamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `orcamento_item`
--

LOCK TABLES `orcamento_item` WRITE;
/*!40000 ALTER TABLE `orcamento_item` DISABLE KEYS */;
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (1,1,2,'Vidro Liso Incolor 6mm',2.00000,130.00000,0.00,NULL,1,'2026-06-10 22:10:58','2026-06-10 22:10:58');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (2,1,17,'Silicone Neutro Incolor 280ml',5.00000,22.00000,0.00,NULL,2,'2026-06-10 22:10:58','2026-06-10 22:10:58');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (3,1,16,'Rolamento para Box Deslizante',2.00000,15.00000,0.00,NULL,3,'2026-06-10 22:10:58','2026-06-10 22:10:58');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (4,2,16,'Rolamento para Box Deslizante',10.00000,15.00000,0.00,NULL,1,'2026-06-10 22:15:11','2026-06-10 22:15:11');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (9,4,6,'Vidro Espelhado 4mm',2.00000,120.00000,0.00,NULL,1,'2026-06-10 22:16:45','2026-06-10 22:16:45');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (10,4,18,'Silicone Estrutural Preto 280ml',4.00000,38.00000,0.00,NULL,2,'2026-06-10 22:16:45','2026-06-10 22:16:45');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (12,5,7,'Vidro Laminado 6mm (3+3)',2.00000,210.00000,0.00,NULL,1,'2026-06-10 22:17:31','2026-06-10 22:17:31');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (13,6,4,'Vidro Temperado 10mm',3.00000,360.00000,0.00,NULL,1,'2026-06-10 22:21:22','2026-06-10 22:21:22');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (14,6,18,'Silicone Estrutural Preto 280ml',10.00000,38.00000,0.00,NULL,2,'2026-06-10 22:21:22','2026-06-10 22:21:22');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (15,3,5,'Vidro Canelado 4mm',2.00000,95.00000,0.00,NULL,1,'2026-06-10 22:22:14','2026-06-10 22:22:14');
INSERT INTO `orcamento_item` (`id`, `orcamento_id`, `produto_id`, `descricao`, `quantidade`, `preco_unitario`, `desconto`, `observacao`, `ordem`, `created_at`, `updated_at`) VALUES (16,3,17,'Silicone Neutro Incolor 280ml',10.00000,22.00000,0.00,NULL,2,'2026-06-10 22:22:14','2026-06-10 22:22:14');
/*!40000 ALTER TABLE `orcamento_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (1,9,5,180.00,1,'','Dinheiro','produto','2026-06-10 18:40:24','2026-06-10 18:40:24');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (2,3,5,180.00,1,'','Cartão de crédito - 11x','produto','2026-06-10 18:53:02','2026-06-10 18:53:02');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (3,8,5,264.00,1,'','Cartão de crédito - 4x','produto','2026-06-10 18:53:33','2026-06-10 18:53:33');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (4,11,5,55.50,1,'','Dinheiro','produto','2026-06-10 18:54:02','2026-06-10 18:54:02');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (5,2,5,3000.00,1,'','Cartão de débito','serviço','2026-06-10 18:55:51','2026-06-10 19:04:24');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (6,6,5,1900.00,1,NULL,NULL,'serviço','2026-06-10 18:56:47','2026-06-10 18:56:47');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (7,3,5,180.00,1,NULL,NULL,'serviço','2026-06-10 18:59:01','2026-06-10 18:59:01');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (8,1,5,890.00,1,NULL,NULL,'serviço','2026-06-10 19:00:22','2026-06-10 19:00:22');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (9,4,5,1100.00,1,NULL,NULL,'serviço','2026-06-10 19:01:11','2026-06-10 19:01:11');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (10,8,6,120.00,0,'','Cartão de crédito - 8x','serviço','2026-06-10 19:01:53','2026-06-10 19:19:29');
INSERT INTO `pedido` (`id`, `cliente_id`, `status_id`, `valor_total`, `ativo`, `observacao`, `forma_pagamento`, `tipo_pedido`, `created_at`, `updated_at`) VALUES (11,7,5,520.00,1,NULL,NULL,'serviço','2026-06-10 19:02:31','2026-06-10 19:02:31');
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `produto`
--

LOCK TABLES `produto` WRITE;
/*!40000 ALTER TABLE `produto` DISABLE KEYS */;
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (1,'Vidro Liso Incolor 4mm','Vidro float incolor 4mm, uso geral em janelas e divisórias residenciais.','m²',85.00000,1,1,'2026-06-10 17:27:54','2026-06-10 17:27:54');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (2,'Vidro Liso Incolor 6mm','Vidro float incolor 6mm, indicado para portas e janelas maiores.','m²',130.00000,2,1,'2026-06-10 17:30:40','2026-06-10 17:30:40');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (3,'Vidro Temperado 8mm','Vidro temperado 8mm, alta resistência. Obrigatório em box, portas e sacadas.','m²',280.00000,3,0,'2026-06-10 17:32:35','2026-06-10 18:08:08');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (4,'Vidro Temperado 10mm','Vidro temperado 10mm para fechamentos de sacada e fachadas.','m²',360.00000,4,1,'2026-06-10 17:34:49','2026-06-10 17:59:22');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (5,'Vidro Canelado 4mm','Vidro canelado translúcido 4mm, uso em banheiros e áreas de privacidade.','m²',95.00000,5,1,'2026-06-10 17:37:13','2026-06-10 18:00:00');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (6,'Vidro Espelhado 4mm','Espelho de vidro 4mm, acabamento prata. Uso em ambientes internos.','m²',120.00000,6,1,'2026-06-10 17:38:39','2026-06-10 17:38:39');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (7,'Vidro Laminado 6mm (3+3)','Vidro laminado com PVB entre duas lâminas de 3mm. Segurança e isolamento acústico.','m²',210.00000,7,1,'2026-06-10 17:40:21','2026-06-10 17:40:21');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (8,'Perfil de alumínio U 10mm','Perfil em U de alumínio anodizado para fixação de vidros 10mm.','m²',18.50000,8,1,'2026-06-10 17:41:49','2026-06-10 17:41:49');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (9,'Trilho Inferior Box Aluminío','Trilho inferior para box de banheiro em alumínio com acabamento escovado.','m²',35.00000,9,1,'2026-06-10 17:43:50','2026-06-10 17:43:50');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (10,'Trilho Superior Box Aluminío','Trilho superior para box de banheiro em alumínio com guia de rolamento.','m²',32.00000,10,1,'2026-06-10 17:45:01','2026-06-10 17:45:01');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (11,'Perfil Canaleta PVC 8mm','Canaleta PVC branca para fixação e proteção de bordas de vidro.\n\n','Kg',8.00000,11,1,'2026-06-10 17:46:06','2026-06-10 17:46:06');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (12,'Dobradiça para Vidro Temperado','Dobradiça de aço inox para porta de vidro temperado, regulagem em 3 eixos.','Unidade',45.00000,12,1,'2026-06-10 17:47:27','2026-06-10 17:47:27');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (13,'Puxador Inox para Vidro','Puxador tubular de inox escovado 30cm, fixação por ponteiras parafusadas.','Unidade',65.00000,13,0,'2026-06-10 17:48:28','2026-06-10 18:10:08');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (14,'Fechadura Inox para Vidro','Fechadura de embutir para porta de vidro, com trava e chave.','Unidade',120.00000,14,1,'2026-06-10 17:49:27','2026-06-10 17:49:27');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (15,'Ponteira Botão Inox','Ponteira tipo botão inox para fixação de vidros em spider e fachadas.','Unidade',22.00000,15,1,'2026-06-10 17:50:24','2026-06-10 17:50:24');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (16,'Rolamento para Box Deslizante','Rolamento nylon para porta de box deslizante, encaixe em trilho de alumínio.','Unidade',15.00000,16,1,'2026-06-10 17:51:29','2026-06-10 17:51:29');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (17,'Silicone Neutro Incolor 280ml','Silicone neutro incolor para vedação de vidros. Não mancha alumínio nem inox.','Unidade',22.00000,17,1,'2026-06-10 17:52:45','2026-06-10 17:52:45');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (18,'Silicone Estrutural Preto 280ml','Silicone estrutural preto para colagem de vidros em fachadas e sacadas.','Unidade',38.00000,18,1,'2026-06-10 17:53:40','2026-06-10 17:53:40');
INSERT INTO `produto` (`id`, `nome`, `descricao`, `unidade_medida`, `preco`, `metrica_estoque_id`, `ativo`, `created_at`, `updated_at`) VALUES (19,'Fita Dupla Face Espuma 12mm','Fita dupla face com espuma de 12mm para fixação de espelhos e vidros leves.','Unidade',18.00000,19,1,'2026-06-10 17:54:58','2026-06-10 17:54:58');
/*!40000 ALTER TABLE `produto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `refresh_token`
--

LOCK TABLES `refresh_token` WRITE;
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
INSERT INTO `refresh_token` (`id`, `token`, `usuario_id`, `expiracao`) VALUES (2,'6d0a5fe3-f0eb-4ac2-804c-8d02db77e717',1,'2026-06-17 22:26:42');
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `servico`
--

LOCK TABLES `servico` WRITE;
/*!40000 ALTER TABLE `servico` DISABLE KEYS */;
INSERT INTO `servico` (`id`, `nome`, `codigo`, `descricao`, `preco_base`, `ativo`, `pedido_id`, `etapa_id`, `created_at`, `updated_at`) VALUES (1,'Box Banheiro Inox 8mm','#001','Apartamento no centro, sem elevador',3000.00,1,5,2,'2026-06-10 21:55:51','2026-06-10 22:04:10');
INSERT INTO `servico` (`id`, `nome`, `codigo`, `descricao`, `preco_base`, `ativo`, `pedido_id`, `etapa_id`, `created_at`, `updated_at`) VALUES (2,'Instalação guarda-corpo médio','#002','Varanda exterma',1900.00,1,6,2,'2026-06-10 21:56:46','2026-06-10 22:06:12');
INSERT INTO `servico` (`id`, `nome`, `codigo`, `descricao`, `preco_base`, `ativo`, `pedido_id`, `etapa_id`, `created_at`, `updated_at`) VALUES (3,'Instalação de espelho simples','#003','Espelho redondo sem moldura',180.00,1,7,2,'2026-06-10 21:59:00','2026-06-10 22:09:11');
INSERT INTO `servico` (`id`, `nome`, `codigo`, `descricao`, `preco_base`, `ativo`, `pedido_id`, `etapa_id`, `created_at`, `updated_at`) VALUES (4,'Instalação de porta de vidro temperado','#004','Varanda externa, casa no centro',890.00,1,8,6,'2026-06-10 22:00:22','2026-06-10 22:23:05');
INSERT INTO `servico` (`id`, `nome`, `codigo`, `descricao`, `preco_base`, `ativo`, `pedido_id`, `etapa_id`, `created_at`, `updated_at`) VALUES (5,'Divisória de escritório em vidro','#005','Prédio centro, acesso com elevador pequeno',1100.00,1,9,2,'2026-06-10 22:01:10','2026-06-10 22:07:50');
INSERT INTO `servico` (`id`, `nome`, `codigo`, `descricao`, `preco_base`, `ativo`, `pedido_id`, `etapa_id`, `created_at`, `updated_at`) VALUES (6,'Troca de silicone e vedação','#006','Box grande instalado pela Léo Vidros',120.00,1,10,8,'2026-06-10 22:01:52','2026-06-10 22:19:29');
INSERT INTO `servico` (`id`, `nome`, `codigo`, `descricao`, `preco_base`, `ativo`, `pedido_id`, `etapa_id`, `created_at`, `updated_at`) VALUES (7,'Instalação de espelho de parede inteira','#007','Quarto pequeno',520.00,1,11,3,'2026-06-10 22:02:30','2026-06-10 22:16:45');
/*!40000 ALTER TABLE `servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `servico_produto`
--

LOCK TABLES `servico_produto` WRITE;
/*!40000 ALTER TABLE `servico_produto` DISABLE KEYS */;
INSERT INTO `servico_produto` (`id`, `servico_id`, `produto_id`, `quantidade_planejada`, `quantidade_utilizada`, `preco_unitario`, `observacao`, `ordem`, `ativo`, `created_at`, `updated_at`) VALUES (1,1,2,2.00000,NULL,130.00000,'',0,1,'2026-06-10 22:03:59','2026-06-10 22:03:59');
INSERT INTO `servico_produto` (`id`, `servico_id`, `produto_id`, `quantidade_planejada`, `quantidade_utilizada`, `preco_unitario`, `observacao`, `ordem`, `ativo`, `created_at`, `updated_at`) VALUES (2,1,17,5.00000,NULL,22.00000,'',1,1,'2026-06-10 22:03:59','2026-06-10 22:03:59');
INSERT INTO `servico_produto` (`id`, `servico_id`, `produto_id`, `quantidade_planejada`, `quantidade_utilizada`, `preco_unitario`, `observacao`, `ordem`, `ativo`, `created_at`, `updated_at`) VALUES (3,1,16,2.00000,NULL,15.00000,'',2,1,'2026-06-10 22:03:59','2026-06-10 22:03:59');
INSERT INTO `servico_produto` (`id`, `servico_id`, `produto_id`, `quantidade_planejada`, `quantidade_utilizada`, `preco_unitario`, `observacao`, `ordem`, `ativo`, `created_at`, `updated_at`) VALUES (4,2,16,10.00000,NULL,15.00000,NULL,0,1,'2026-06-10 22:15:11','2026-06-10 22:15:11');
INSERT INTO `servico_produto` (`id`, `servico_id`, `produto_id`, `quantidade_planejada`, `quantidade_utilizada`, `preco_unitario`, `observacao`, `ordem`, `ativo`, `created_at`, `updated_at`) VALUES (5,6,7,2.00000,2.00000,210.00000,'',0,1,'2026-06-10 22:17:29','2026-06-10 22:17:29');
INSERT INTO `servico_produto` (`id`, `servico_id`, `produto_id`, `quantidade_planejada`, `quantidade_utilizada`, `preco_unitario`, `observacao`, `ordem`, `ativo`, `created_at`, `updated_at`) VALUES (6,4,5,2.00000,NULL,95.00000,NULL,0,1,'2026-06-10 22:22:14','2026-06-10 22:22:14');
INSERT INTO `servico_produto` (`id`, `servico_id`, `produto_id`, `quantidade_planejada`, `quantidade_utilizada`, `preco_unitario`, `observacao`, `ordem`, `ativo`, `created_at`, `updated_at`) VALUES (7,4,17,10.00000,NULL,22.00000,NULL,1,1,'2026-06-10 22:22:14','2026-06-10 22:22:14');
/*!40000 ALTER TABLE `servico_produto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `solicitacao`
--

LOCK TABLES `solicitacao` WRITE;
/*!40000 ALTER TABLE `solicitacao` DISABLE KEYS */;
INSERT INTO `solicitacao` (`id`, `nome`, `cpf`, `email`, `telefone`, `status_id`, `created_at`, `updated_at`) VALUES (1,'Ana Julia','61268157856','anajulia@gmail.com','11674838383',8,'2026-06-10 22:24:44','2026-06-10 22:24:44');
INSERT INTO `solicitacao` (`id`, `nome`, `cpf`, `email`, `telefone`, `status_id`, `created_at`, `updated_at`) VALUES (2,'Maria Eduarda','28186773860','eduarda123@gmail.com','11966534343',9,'2026-06-10 22:25:16','2026-06-10 22:26:50');
INSERT INTO `solicitacao` (`id`, `nome`, `cpf`, `email`, `telefone`, `status_id`, `created_at`, `updated_at`) VALUES (3,'Pedro Henrique','24872649850','henripedro@gmail.com.br','11943456778',10,'2026-06-10 22:25:50','2026-06-10 22:26:53');
/*!40000 ALTER TABLE `solicitacao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `status`
--

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (1,'AGENDAMENTO','PENDENTE','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (2,'AGENDAMENTO','EM ANDAMENTO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (3,'AGENDAMENTO','CONCLUÍDO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (4,'AGENDAMENTO','CANCELADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (5,'PEDIDO','ATIVO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (6,'PEDIDO','INATIVO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (7,'PEDIDO','CANCELADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (8,'SOLICITACAO','PENDENTE','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (9,'SOLICITACAO','ACEITO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (10,'SOLICITACAO','RECUSADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (11,'ORCAMENTO','RASCUNHO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (12,'ORCAMENTO','ENVIADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (13,'ORCAMENTO','EM ANALISE','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (14,'ORCAMENTO','APROVADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (15,'ORCAMENTO','RECUSADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `status` (`id`, `tipo`, `nome`, `created_at`, `updated_at`) VALUES (16,'ORCAMENTO','EXPIRADO','2026-06-10 20:22:37','2026-06-10 20:22:37');
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`id`, `nome`, `cpf`, `email`, `senha`, `telefone`, `first_login`, `endereco_id`, `created_at`, `updated_at`) VALUES (1,'Administrador','00000000000','admin@leovidros.com.br','$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG','11999990000',0,1,'2026-06-10 20:22:37','2026-06-10 20:22:37');
INSERT INTO `usuario` (`id`, `nome`, `cpf`, `email`, `senha`, `telefone`, `first_login`, `endereco_id`, `created_at`, `updated_at`) VALUES (2,'Maria Eduarda','28186773860','eduarda123@gmail.com','$2a$10$V1ubF6Jex1/VZDIXmDdHCeGYIvowDMtmSPmqNQEgC6pmipAUhSzJq','11966534343',1,NULL,'2026-06-10 19:26:51','2026-06-10 19:26:51');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-10 22:30:37