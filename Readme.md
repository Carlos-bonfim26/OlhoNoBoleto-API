# OlhoNoBoleto API

![spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![junit](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![insomia](https://img.shields.io/badge/Insomnia-5849be?style=for-the-badge&logo=Insomnia&logoColor=white)
![swaguer](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)
![aws](https://img.shields.io/badge/Amazon_Web_Services-FF9900?style=for-the-badge&logo=amazonwebservices&logoColor=white)

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#🏗️-arquitetura)
- [Tecnologias](#🛠️-tecnologias)
- [Instalação](#🚀-instalação)
- [Endpoints](#🔗-endpoints)
- [Exemplos de Uso](#💡-exemplos-de-uso)
- [Testes](#🧪-testes)
- [Deploy](#☁️-deploy)

## 📖 Visão Geral

A **OlhoNoBoleto API** é uma solução RESTful desenvolvida em Java com Spring Boot para validação segura de boletos bancários e códigos QR do Pix. A API analisa dados de pagamentos, consulta fontes externas e verifica histórico de denúncias para identificar possíveis fraudes.

### ⚡ Funcionalidades Principais

- ✅ Validação de boletos bancários via linha digitável
- 🔍 Análise de QR Codes do Pix
- 📊 Consulta à BrasilAPI para verificação de CNPJs
- 🚨 Sistema de denúncias e beneficiários suspeitos
- 🔒 Autenticação segura com cookies

## 🏗️ Arquitetura

### Padrão MVC

```
Cliente (Front-end/Insomnia)
        ↓
    Controller Layer (REST Endpoints)
        ↓
    Service Layer (Lógica de Negócio)
        ↓
 Repository Layer (JPA/Hibernate)
        ↓
   Banco de Dados (MySQL)
```

### Componentes Principais

- **Controllers**: Manipulam requisições HTTP e respostas
- **Services**: Contêm a lógica de negócio e validações
- **Repositories**: Gerenciam acesso aos dados
- **Models**: Representam tabelas do banco de dados
- **DTOs**: Transferência de dados entre camadas

## 🛠️ Tecnologias

| Tecnologia        | Versão | Finalidade                    |
| ----------------- | ------ | ----------------------------- |
| Java              | 21     | Linguagem principal           |
| Spring Boot       | 3.5.6  | Framework backend             |
| Maven             | -      | Gerenciamento de dependências |
| MySQL             | -      | Banco de dados                |
| JPA/Hibernate     | -      | ORM e persistência            |
| Spring Security   | -      | Segurança e autenticação      |
| JUnit 5           | -      | Testes unitários              |
| Mockito           | -      | Mocking em testes             |
| SpringDoc OpenAPI | -      | Documentação Swagger          |

## 🚀 Instalação

### Pré-requisitos

- Java 21
- Maven 3.6+
- MySQL 8.0+
- Git

### Configuração do Projeto

```bash
# Clone o repositório
git clone https://github.com/Carlos-bonfim26/OlhoNoBoleto-API.git

# Navegue até o diretório
cd OlhoNoBoleto-API

# Configure o banco de dados
# Edite o arquivo application.properties com suas credenciais MySQL

# Execute a aplicação
mvn spring-boot:run
```

### Configuração do Banco de Dados

```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/olhonoboleto
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

## 🔗 Endpoints

### 👤 Autenticação

#### Registrar Usuário

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@email.com",
  "password": "senha123"
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@email.com",
  "password": "senha123"
}
```

### 📄 Boletos

#### Validar Boleto

```http
POST /api/boletos/validar
Content-Type: application/json
Cookie: sessionId=abc123

{
  "linhaDigitavel": "34191.09008 00001.190428 91020.150008 8 85950000015000"
}
```

**Resposta:**

```json
{
  "valido": true,
  "beneficiario": "Empresa XYZ LTDA",
  "valor": 150.0,
  "vencimento": "2025-01-15",
  "status": "CONFIAVEL",
  "mensagem": "Boleto válido e beneficiário confiável"
}
```

#### Listar Boletos

```http
GET /api/boletos
Cookie: sessionId=abc123
```

### 🏢 Beneficiários

#### Buscar Beneficiário por Documento

```http
GET /api/beneficiarios/documento/{cnpj}
Cookie: sessionId=abc123
```

#### Cadastrar Beneficiário

```http
POST /api/beneficiarios
Content-Type: application/json
Cookie: sessionId=abc123

{
  "nome": "Empresa ABC",
  "documento": "12.345.678/0001-90",
  "banco": "341"
}
```

### 🚨 Reports (Denúncias)

#### Criar Denúncia

```http
POST /api/reports
Content-Type: application/json
Cookie: sessionId=abc123

{
  "beneficiarioId": 1,
  "descricao": "Boleto com valores divergentes",
  "tipoReport": "FRAUDE"
}
```

#### Dashboard de Reports

```http
GET /api/reports/dashboard
Cookie: sessionId=abc123
```

## 💡 Exemplos de Uso

### Validação de Boleto com Suspeita

**Requisição:**

```http
POST /api/boletos/validar
Content-Type: application/json

{
  "linhaDigitavel": "34191.09008 00001.190428 91020.150008 8 85950000015000"
}
```

**Resposta (Suspeita):**

```json
{
  "valido": false,
  "beneficiario": "Empresa Suspeita LTDA",
  "valor": 150.0,
  "vencimento": "2025-01-15",
  "status": "SUSPEITO",
  "motivo": "Beneficiário possui 5 denúncias registradas",
  "recomendacao": "NÃO EFETUE O PAGAMENTO"
}
```

### Consulta à BrasilAPI

A API integra automaticamente com a BrasilAPI para validar CNPJs:

```java
// Exemplo de integração automática
public BeneficiarioResponse validarBeneficiario(String cnpj) {
    BrasilApiResponse brasilApiData = brasilApiService.consultarCnpj(cnpj);
    // Compara dados do boleto com dados oficiais
}
```

## 🧪 Testes

### Executando Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório de cobertura
mvn jacoco:report
```

### Estrutura de Testes

```
src/test/java/
├── controller/
│   ├── BoletoControllerTest.java
│   ├── AuthControllerTest.java
│   └── BeneficiarioControllerTest.java
├── service/
│   ├── BoletoServiceTest.java
│   └── BrasilApiServiceTest.java
└── integration/
    └── BoletoValidationIT.java
```

### Exemplo de Teste Unitário

```java
@Test
void deveValidarBoletoComLinhaDigitavelCorreta() {
    // Given
    String linhaDigitavel = "34191.09008 00001.190428 91020.150008 8 85950000015000";

    // When
    BoletoResponse response = boletoService.validarBoleto(linhaDigitavel);

    // Then
    assertTrue(response.isValido());
    assertEquals("CONFIAVEL", response.getStatus());
}
```

## ☁️ Deploy

### Ambiente de Produção

- **Backend**: Hospedado na Koyeb
- **Banco de Dados**: AWS RDS (MySQL)
- **Monitoramento**: Logs e métricas integradas

### URLs de Produção

- **API**: `https://olhonoboleto-api.koyeb.app`
- **Swagger UI**: `https://olhonoboleto-api.koyeb.app/swagger-ui.html`
- **Health Check**: `https://olhonoboleto-api.koyeb.app/actuator/health`

### Variáveis de Ambiente

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://aws-rds-url:3306/olhonoboleto
SPRING_DATASOURCE_USERNAME=usuario_prod
SPRING_DATASOURCE_PASSWORD=senha_segura
BRASIL_API_URL=https://brasilapi.com.br/api
```

## 📊 Monitoramento

A API inclui endpoints de monitoramento:

```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/nova-funcionalidade`
3. Commit suas mudanças: `git commit -m 'Adiciona nova funcionalidade'`
4. Push para a branch: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

## 📝 Licença

Este projeto é desenvolvido para fins acadêmicos na Universidade Anhembi Morumbi.

---

## 👨‍💻Desenvolvedores

 <div style="display:flex; gap:1rem;">
     <div style="display:flex; flex-direction:column;">
     <img src="https://github.com/Carlos-bonfim26.png?size=200" width=180>
     <a href="https://www.linkedin.com/in/carlosbonfim26/" target="_blank" style="font-size: 1.5rem">Carlos Bonfim</a>
     </div>
       <div style="display:flex; flex-direction:column;">
          <img src="https://github.com/JoaquimGuilhermeNunesLeal.png?size=115" width=180>
           <a href="https://www.linkedin.com/in/guilherme-nunes-a7415b2ba/" target="_blank"style="font-size: 1.5rem">Joaquim Guilherme</a>
           </div>
 </div>

<br/>

**Repositório**: [https://github.com/Carlos-bonfim26/OlhoNoBoleto-API](https://github.com/Carlos-bonfim26/OlhoNoBoleto-API)
