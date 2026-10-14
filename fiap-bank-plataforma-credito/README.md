# FIAP Bank - Plataforma de Crédito 💳

Microsserviço de **Esteira e Motor de Decisão de Crédito** do FIAP Bank. Orquestra a jornada completa de uma proposta de crédito: consulta o SCR (Banco Central), o Bureau de Crédito, o CRM corporativo e o Core Bancário (via **Spring Cloud OpenFeign**), aciona o Motor de Crédito (regras de negócio puras de domínio) e dispara notificações ao cliente.

> **Refatoração DDD (Checkpoint 5):** este microsserviço foi reengenharizado a partir de um estado legado que violava DDD (regras de negócio misturadas com persistência/HTTP em um único módulo) e REST (todas as rotas eram `POST` com verbos de ação na URI, ex.: `/api/credito/solicitarCredito`). Veja a seção [Diagnóstico e Estratégia de Refatoração](#-diagnóstico-e-estratégia-de-refatoração) para o detalhamento exigido pela Avaliação Teórica do Checkpoint.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Cloud OpenFeign 2025.1.3** (comunicação declarativa com o Core Bancário)
- **Spring Data JPA**
- **Spring Boot Validation** (Jakarta Bean Validation)
- **Spring Boot Actuator**
- **Lombok**
- **H2 Database** (em memória)
- **Maven** (projeto multi-módulo)

---

## 🏗️ Arquitetura DDD Multi-Módulo

Assim como o Core Bancário, o projeto é um **pom agregador** com 4 submódulos físicos, respeitando a hierarquia `presentation → application → infrastructure → domain`, validada em tempo de build pelo `maven-enforcer-plugin`:

| Módulo | Responsabilidade |
| :--- | :--- |
| **`domain`** | Entidade pura `SolicitacaoCredito`, Value Objects (`Cpf`, `StatusSolicitacao`, `ContaClienteInfo`, `HistoricoCrmInfo`), o **Motor de Crédito** (`MotorDeCreditoDomainService`, serviço de domínio 100% puro que decide `APROVADO`/`REPROVADO`/`EM_ANALISE`), as Portas de integração externa (`ScrPort`, `BureauCreditoPort`, `CrmPort`, `CoreBancarioPort`, `NotificacaoPort`) e a interface de Repositório. Zero dependência de Spring/JPA. |
| **`infrastructure`** | Entidade JPA, Spring Data Repository, Repository Adapter, os Adapters simulados de SCR/Bureau/CRM/Notificação, e o `CoreBancarioFeignClient` + Adapter que consome o Core Bancário real via Spring Cloud OpenFeign. |
| **`application`** | DTOs validados (`SolicitarCreditoRequest`, `DecisaoOperadorRequest`) e os Serviços de Caso de Uso (`SolicitacaoCreditoService`, `ConsultaCoreBancarioService`), que orquestram as Portas do domínio. |
| **`presentation`** | Classe `@SpringBootApplication`, os **novos Controllers RESTful**, o **Controller Adaptador legado** (retrocompatibilidade) e o `GlobalExceptionHandler`. |

---

## 📋 Pré-requisitos

- **Java JDK 21** instalado e configurado no `PATH`.
- **Apache Maven 3.9+** instalado.
- *(Opcional, mas recomendado)* Serviço **Core Bancário** rodando na porta `8080` — se indisponível, a consulta de conta degrada graciosamente e retorna `statusConta: "INDISPONIVEL"`.

---

## 🚀 Como Executar a Aplicação

```bash
cd fiap-bank-plataforma-credito
mvn clean package -DskipTests
java -jar presentation/target/fiap-bank-plataforma-credito-presentation-1.0.0-SNAPSHOT.jar
```

Ou via plugin Maven, a partir do módulo `presentation`:

```bash
mvn -pl presentation spring-boot:run
```

A aplicação estará acessível em: **`http://localhost:8081`**

---

## ⚙️ Variáveis de Ambiente e Configuração

| Propriedade | Variável de Ambiente | Valor Padrão | Descrição |
| :--- | :--- | :--- | :--- |
| `server.port` | `SERVER_PORT` | `8081` | Porta HTTP da aplicação |
| `core.bancario.url` | `CORE_BANCARIO_URL` | `http://localhost:8080` | URL base da API do Core Bancário consumida via Feign |

---

## 🗄️ Console do Banco de Dados H2

- **URL:** `http://localhost:8081/h2-console`
- **JDBC URL:** `jdbc:h2:mem:plataformacreditodb`
- **User Name:** `sa`
- **Password:** *(deixe em branco)*

---

## 📌 Dados Iniciais (Carga Automática)

Os mesmos 3 CPFs válidos semeados no Core Bancário são usados para submeter 3 solicitações de crédito de demonstração via `PlataformaCreditoDataInitializer`:

- **Ana Silva** (`111.222.333-96`) — parcela estimada < 30% da renda → **aprovação automática**.
- **Carlos Eduardo Oliveira** (`987.654.321-00`) — parcela estimada < 30% da renda → **aprovação automática**.
- **Mariana Souza** (`444.555.666-19`) — CPF terminado em `9` → demonstra a **reprovação automática por restrição no SCR**.

---

## 🌐 Novo Recurso RESTful: `/api/v1/solicitacoes-credito`

API estritamente RESTful, com substantivo no plural, verbos HTTP semânticos e a decisão do operador modelada como atualização parcial (`PATCH`) do sub-recurso `status` — em vez de verbos de ação na URI.

| Verbo | URI | Sucesso | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/v1/solicitacoes-credito` | `201` | Submete uma nova proposta de crédito (aciona o Motor de Crédito) |
| `GET` | `/api/v1/solicitacoes-credito` | `200` | Lista todas as solicitações |
| `GET` | `/api/v1/solicitacoes-credito/{id}` | `200` | Busca uma solicitação por ID |
| `GET` | `/api/v1/solicitacoes-credito/cliente/{cpf}` | `200` | Lista as solicitações de um cliente |
| `PATCH` | `/api/v1/solicitacoes-credito/{id}/status` | `200` | Registra a decisão do operador (`APROVADO`/`REPROVADO`) |
| `GET` | `/api/v1/clientes/{cpf}/conta` | `200` | Consulta (proxy) a conta do cliente no Core Bancário |

**Exemplo — submeter proposta:**
```http
POST /api/v1/solicitacoes-credito
Content-Type: application/json

{
  "cpfCliente": "11122233396",
  "nomeCliente": "Ana Silva",
  "tipoCredito": "EMPRESTIMO_PESSOAL",
  "valorSolicitado": 15000.00,
  "quantidadeParcelas": 24,
  "rendaMensal": 7500.00
}
```

**Exemplo — decisão do operador:**
```http
PATCH /api/v1/solicitacoes-credito/3/status
Content-Type: application/json

{
  "status": "APROVADO",
  "observacao": "Documentação validada manualmente."
}
```

---

## ♻️ Controller Adaptador (Retrocompatibilidade)

Para **não quebrar** os front-ends legados em produção (`fiap-bank-mesa-operacoes-credito` via Feign, e `fiap-bank-internet-banking-mobile` via Angular `HttpClient`), o `CreditoLegacyController` preserva **exatamente** as 7 rotas originais, como uma casca fina (*thin adapter*) que delega para os mesmos Casos de Uso da camada `application` usados pelo novo recurso RESTful — nenhuma regra de negócio é duplicada.

Base URL: `http://localhost:8081/api/credito` *(todas `POST`, formato de payload/resposta idêntico ao original)*

| Rota Legada | Caso de Uso delegado |
| :--- | :--- |
| `POST /api/credito/solicitarCredito` | `SolicitacaoCreditoService.solicitar(...)` |
| `POST /api/credito/consultarStatusCredito` | `SolicitacaoCreditoService.buscarPorCpfCliente(...)` |
| `POST /api/credito/buscarSolicitacaoPorId` | `SolicitacaoCreditoService.buscarPorId(...)` |
| `POST /api/credito/listarTodasSolicitacoes` | `SolicitacaoCreditoService.listarTodas()` |
| `POST /api/credito/aprovarSolicitacao` | `SolicitacaoCreditoService.registrarDecisaoOperador(..., APROVADO)` |
| `POST /api/credito/reprovarSolicitacao` | `SolicitacaoCreditoService.registrarDecisaoOperador(..., REPROVADO)` |
| `POST /api/credito/consultarClienteCore` | `ConsultaCoreBancarioService.consultarConta(...)` |

---

## 🔎 Diagnóstico e Estratégia de Refatoração

Ver as respostas completas da **Avaliação Teórica** (Seção 5 do Checkpoint) em [`/docs/avaliacao-teorica.md`](../docs/avaliacao-teorica.md), na raiz do monorepo.
