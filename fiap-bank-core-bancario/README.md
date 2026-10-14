# FIAP Bank - Core Bancário 🏦

Microsserviço de **Core Bancário** responsável pela gestão central de clientes e contas correntes/poupança do FIAP Bank. O projeto segue os princípios de **Domain-Driven Design (DDD)** com segregação estrita em arquitetura multi-módulo Maven.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.1+**
- **Spring Data JPA**
- **H2 Database** (em memória)
- **Maven** (projeto multi-módulo)

---

## 📋 Pré-requisitos

- **Java JDK 21** instalado e configurado nas variáveis de ambiente.
- **Apache Maven 3.9+** instalado.

---

## 🚀 Como Executar a Aplicação

### 1. Compilar os módulos Maven

Na raiz do diretório `fiap-bank-core-bancario`:

```bash
mvn clean install
```

### 2. Iniciar a aplicação

Você pode iniciar o serviço diretamente da raiz do módulo:

```bash
mvn -pl fiap-bank-core-bancario-presentation spring-boot:run
```

Ou navegando até o módulo de apresentação:

```bash
cd fiap-bank-core-bancario-presentation
mvn spring-boot:run
```

### 3. Executar via arquivo JAR

Após o empacotamento com `mvn clean package`:

```bash
java -jar fiap-bank-core-bancario-presentation/target/fiap-bank-core-bancario-presentation-1.0.0-SNAPSHOT.jar
```

A aplicação estará acessível em: **`http://localhost:8080`**

---

## 🗄️ Console do Banco de Dados H2

O banco de dados H2 em memória pode ser acessado pelo navegador:

- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:corebancariodb`
- **User Name:** `sa`
- **Password:** *(deixe em branco)*

---

## 📌 Dados Iniciais (Carga Automática)

Ao subir o serviço, os seguintes dados são carregados automaticamente via `CoreBancarioDataInitializer` (os mesmos CPFs usados na carga da Plataforma de Crédito, para uma demonstração coerente ponta a ponta):

- **Cliente 1:** Ana Silva | CPF: `111.222.333-96` | Agência: `0001` | Saldo inicial: `R$ 12.500,00`
- **Cliente 2:** Carlos Eduardo Oliveira | CPF: `987.654.321-00` | Agência: `0001` | Saldo inicial: `R$ 48.000,00`
- **Cliente 3:** Mariana Souza | CPF: `444.555.666-19` | Agência: `0001` | Saldo inicial: `R$ 3.200,00`

> Os CPFs acima são **matematicamente válidos** (dígito verificador módulo 11 correto), pois o Value Object `Cpf` do domínio valida essa regra de negócio real — ver `Cpf.isValido(...)`. O número de conta é gerado automaticamente pela aplicação a cada execução.

---

## 🏗️ Arquitetura DDD Multi-Módulo

O projeto é um **pom agregador** (`fiap-bank-core-bancario`) com 4 submódulos Maven físicos, respeitando estritamente a hierarquia de dependências `presentation → application → infrastructure → domain` (validada em tempo de build pelo `maven-enforcer-plugin`, regra `bannedDependencies` em cada submódulo):

| Módulo | Responsabilidade |
| :--- | :--- |
| **`domain`** | Entidades puras (`Cliente`, `ContaBancaria`), Value Objects (`Cpf`, `Email`, `Saldo`, `TipoConta`, `StatusConta`), interfaces de Repositório e exceções de domínio. **Zero** dependência de Spring/JPA — falha o build se alguma for declarada (`enforce-domain-purity`). |
| **`infrastructure`** | Entidades JPA (`@Entity`), Spring Data Repositories, e os *Repository Adapters* que implementam as interfaces do domínio (padrão Ports & Adapters), traduzindo entre o modelo de domínio puro e o modelo relacional. |
| **`application`** | DTOs (Java Records) com Bean Validation (`jakarta.validation`) e os Serviços de Caso de Uso (`ClienteService`, `ContaBancariaService`), que orquestram o domínio e os repositórios. |
| **`presentation`** | Classe `@SpringBootApplication`, Controllers RESTful e o `GlobalExceptionHandler` (`@RestControllerAdvice`) com mapeamento semântico de exceções de domínio para status HTTP (404, 400, 409). |

A classe principal (`CoreBancarioApplication`) reside fisicamente no módulo `presentation`, mas é declarada no pacote raiz `br.com.fiap.bank.core` (e não em `...presentation`) para que o component-scan padrão do Spring Boot alcance as demais camadas sem a necessidade de configuração adicional.

---

## 🌐 Endpoints RESTful Principais

### Clientes (`/api/v1/clientes`)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/v1/clientes` | Lista todos os clientes cadastrados |
| `GET` | `/api/v1/clientes/{id}` | Busca cliente por UUID |
| `GET` | `/api/v1/clientes/cpf/{cpf}` | Busca cliente por CPF |
| `POST` | `/api/v1/clientes` | Cadastra um novo cliente |
| `PUT` | `/api/v1/clientes/{id}` | Atualiza dados do cliente por UUID |
| `DELETE` | `/api/v1/clientes/{id}` | Remove um cliente por UUID |

### Contas Bancárias (`/api/v1/contas`)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/v1/contas` | Lista todas as contas bancárias |
| `GET` | `/api/v1/contas/{numeroConta}` | Busca detalhes de uma conta por número |
| `GET` | `/api/v1/contas/cliente/{cpf}` | Busca conta bancária associada a um CPF |
| `POST` | `/api/v1/contas` | Abre uma nova conta bancária para um cliente |
| `PUT` | `/api/v1/contas/{numeroConta}/saldo` | Atualiza o saldo da conta (depósito ou saque) |
| `DELETE` | `/api/v1/contas/{numeroConta}` | Encerra uma conta bancária |

> **Nota de design:** a especificação do Checkpoint apresenta uma pequena divergência entre o texto da Seção 9 (que usa `{numeroConta}` como chave de negócio nos exemplos de URI) e a Matriz de Endpoints da Seção 10.1 (que rotula o mesmo segmento genericamente como `{id}`). Optou-se por seguir o texto da Seção 9, que é explícito quanto à intenção de negócio (consultar/movimentar/encerrar a conta pelo seu número, não pelo UUID técnico), tratando o `{id}` da tabela como um rótulo genérico de "segmento identificador". O UUID técnico da conta continua disponível via `GET /api/v1/contas` (campo `id` no corpo da resposta) para integrações que o exijam.
