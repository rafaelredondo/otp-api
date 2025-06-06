# OTP Service

Serviço de geração e validação de OTP (One-Time Password) implementado em Java com Spring Boot.

## 🚀 Tecnologias

- Java 21
- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database (em memória)
- Maven
- Hibernate Validator
- Lombok

## 📋 Pré-requisitos

- JDK 21 ou superior
- Maven 3.9.x ou superior
- Git

## 🔧 Configuração

1. Clone o repositório:
```bash
git clone [URL_DO_REPOSITÓRIO]
cd java-project
```

2. Configure a chave de criptografia:
   - Crie um arquivo `application.properties` em `src/main/resources/` (se não existir)
   - Adicione a propriedade:
   ```properties
   encryption.key.secret=[CHAVE_BASE64_32_BYTES]
   ```
   - Para gerar uma chave válida, você pode usar o método `DefaultEncryptionService.generateValidKey()`

3. Compile o projeto:
```bash
mvn clean install
```

## 🚀 Executando a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 📝 Endpoints da API

### Gerar OTP
```http
POST /api/otp/generate?email={email}
```
- Gera um novo OTP para o email especificado
- O OTP é enviado por email (simulado em ambiente de desenvolvimento)
- Retorna uma mensagem de sucesso

### Validar OTP
```http
POST /api/otp/validate?email={email}&otp={otp}
```
- Valida o OTP fornecido para o email especificado
- Retorna sucesso se o OTP for válido
- Lança exceção se o OTP for inválido ou expirado

### Revogar OTP
```http
POST /api/otp/revoke?email={email}&reason={reason}
```
- Revoga um OTP ativo para o email especificado
- Útil para invalidar OTPs quando necessário

## 📝 Documentação da API (Swagger/OpenAPI)

A documentação completa da API está disponível através do Swagger UI:

```http
http://localhost:8080/swagger-ui.html
```

A especificação OpenAPI em formato JSON pode ser acessada em:

```http
http://localhost:8080/api-docs
```

Através do Swagger UI, você pode:
- Explorar todos os endpoints disponíveis
- Ver detalhes de cada parâmetro e possíveis códigos de resposta
- Testar os endpoints diretamente na interface
- Baixar a documentação em formato JSON ou YAML para uso em ferramentas de cliente

### Exemplo de uso com Swagger

1. Acesse `http://localhost:8080/swagger-ui.html` no navegador
2. Expanda o endpoint desejado (ex: `/api/otp/generate`)
3. Clique em "Try it out"
4. Preencha os parâmetros necessários 
5. Clique em "Execute" para testar o endpoint

## 🔒 Segurança

- OTPs são criptografados usando AES-256 em modo CBC
- Chave de criptografia de 32 bytes (256 bits)
- IV fixo para garantir consistência na validação
- Validação de formato de email
- Limite de tentativas de validação
- Expiração automática de OTPs

## 🧪 Testes

Para executar os testes:
```bash
mvn test
```

## 📊 Monitoramento

- Logs detalhados em nível DEBUG (configurável)
- Console H2 disponível em `/h2-console` (apenas em ambiente de desenvolvimento)
  - URL: `jdbc:h2:mem:otpdb`
  - Usuário: `SA`
  - Senha: (vazia)

## ⚠️ Limitações

- Em ambiente de desenvolvimento, o envio de email é simulado
- Banco de dados H2 em memória (dados são perdidos ao reiniciar)
- Perfil "test" ativo por padrão

## 🔄 Fluxo de Trabalho

1. Cliente solicita geração de OTP
2. Sistema gera OTP, criptografa e armazena
3. OTP é enviado por email (simulado em dev)
4. Cliente envia OTP para validação
5. Sistema valida OTP e retorna resultado

## 📝 Notas de Implementação

- Serviço de criptografia centralizado em `DefaultEncryptionService`
- Validação de OTP com regras configuráveis
- Histórico de OTPs mantido para auditoria
- Tratamento de exceções global
- Validação de entrada com Hibernate Validator

## Features

### Functional Requirements

#### OTP Management
- Generation of secure one-time passwords
- Validation of OTPs with expiration checks
- Case-insensitive email handling
- OTP history tracking
- Email notifications for OTP delivery

#### Security
- Encrypted OTP storage
- Spring Security integration
- Rate limiting for OTP requests
- Protection against brute force attacks
- Headers security configuration

#### Fault Tolerance
- Message queuing with RabbitMQ
- Automatic retry mechanism for failed operations
- Dead Letter Queue (DLQ) for failed attempts
- Email notification queuing
- Error recovery procedures

### Non-Functional Requirements

#### Security
- Data encryption at rest
- Secure communication channels
- Protection against common attack vectors
- Rate limiting implementation
- Access control and authentication

#### Reliability
- Fault tolerance through message queuing
- Automatic retry mechanisms
- Dead letter queuing for failed operations
- System state recovery
- Transaction management

#### Testability
- Comprehensive unit tests
- Integration tests
- Mocked external dependencies
- Isolated test environments
- Test coverage for retry scenarios

#### Maintainability
- Clean code architecture
- Dependency injection
- Interface-based design
- Clear separation of concerns
- Documented code and APIs

## Technical Stack
- Java 17+
- Spring Boot
- Spring Security
- RabbitMQ
- JUnit 5
- Mockito
- Maven

## Project Structure
```
src/
├── main/
│   └── java/         # Application source code
│       └── com/
│           └── example/    # Main package
└── test/
    └── java/         # Test source code
        └── com/
            └── example/    # Test package
```

## Getting Started
1. Make sure you have Java JDK 17 or later installed
2. Make sure you have Maven installed
3. Build the project: `mvn clean install`
4. Run the tests: `mvn test`

## Controle de Versão e .gitignore

Para manter o repositório limpo e evitar a inclusão de arquivos binários e gerados no controle de versão, este projeto utiliza um arquivo `.gitignore` configurado para Java/Maven. 

### Arquivos ignorados:
- **Binários compilados**: `.class`, `.jar`, `.war`
- **Diretórios gerados**: `/target/`, `/build/`
- **Arquivos de log**: `/logs/`, `*.log`
- **Arquivos específicos de IDEs**: `.idea/`, `.vscode/`, `.project`, etc.
- **Arquivos de sistema**: `.DS_Store`, `Thumbs.db`

### Boas práticas para o controle de versão:
1. **Nunca comitar** arquivos binários compilados
2. **Nunca comitar** arquivos de log ou dumps
3. **Nunca comitar** credenciais ou informações sensíveis
4. **Utilizar variáveis de ambiente** ou arquivos de configuração específicos para cada ambiente

### Se você já comitou arquivos binários:
Para remover arquivos binários já adicionados ao repositório:
```bash
git rm -r --cached target/
git rm -r --cached logs/
git commit -m "Remove arquivos binários e gerados do controle de versão"
```

## Dependencies
- JUnit 5 for testing 