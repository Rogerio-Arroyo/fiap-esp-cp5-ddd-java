# Checkpoint 5 — Avaliação Teórica (Seção 5 e 8 do Cartão-Resposta)

**Aluno:** Rogério Cruz Arroyo | **RM:** RM563517 | **Turma:** 2ESPH

> Este documento responde às três perguntas da Seção 5 ("Identifique quais projetos no estado inicial violam as boas práticas do DDD e da Arquitetura RESTful") do Checkpoint 5, para uso no Cartão-Resposta da Seção 8. O conteúdo abaixo pode ser copiado diretamente para o PDF de entrega no Microsoft Teams.

---

## 1. Quais componentes violam o isolamento do domínio e misturam código de infraestrutura relacional com regras de negócio?

O componente que viola o isolamento do domínio é a **`fiap-bank-plataforma-credito`**, no seu estado inicial (antes da refatoração).

No estado original, todo o microsserviço vivia em um **único módulo Maven**, com pacotes "técnicos" (`model`, `dto`, `service`, `repository`, `controller`) em vez de camadas DDD. O problema mais grave estava na classe `br.com.fiap.bank.plataforma.model.SolicitacaoCredito`: ela era, ao mesmo tempo, a **entidade de domínio** (onde deveriam residir as regras de negócio de concessão de crédito) e a **entidade de persistência relacional**, anotada diretamente com `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column` e `@Enumerated`. Isso significa que o modelo de negócio estava rigidamente acoplado ao Hibernate/JPA: qualquer mudança de schema relacional (ex.: renomear uma coluna) impactava diretamente a classe de domínio, e o domínio não podia ser testado, reaproveitado ou evoluído independentemente da infraestrutura de persistência — uma violação direta do princípio de **isolamento do domínio** do DDD.

Essa mistura se propagava para a camada de orquestração: a classe `PlataformaCreditoService` (que continha o "motor de regras" de concessão de crédito — scoring, limite de comprometimento de renda, etc.) dependia diretamente de `SolicitacaoCreditoRepository extends JpaRepository<SolicitacaoCredito, Long>`, ou seja, a lógica de negócio estava fisicamente no mesmo módulo, no mesmo classpath e sem qualquer fronteira de compilação em relação ao Spring Data JPA. Não havia nenhuma interface de repositório desacoplada da infraestrutura: a "porta" de persistência e o "adaptador" JPA eram a mesma coisa.

O microsserviço **`fiap-bank-core-bancario`** não apresentava essa violação porque, no estado inicial do repositório, ele ainda não existia (apenas um diretório vazio com um arquivo `.keep`) — foi construído do zero já seguindo DDD, conforme a Fase 1 da missão.

A aplicação **`fiap-bank-mesa-operacoes-credito`** também não apresenta essa violação: ela não possui um modelo de domínio próprio (é um front-end Thymeleaf que apenas consome a Plataforma de Crédito via Feign e renderiza telas), portanto não há regra de negócio nem persistência relacional nela para misturar.

---

## 2. Quais componentes possuem rotas de API (endpoints) que violam a semântica do protocolo HTTP e o conceito de Recursos RESTful?

O componente que viola a arquitetura RESTful é, novamente, a **`fiap-bank-plataforma-credito`**, através da classe `CreditoOperacoesNonRestfulController` (cujo próprio nome já denuncia o problema).

Todos os 7 endpoints expostos sob `/api/credito` usavam o verbo HTTP `POST`, inclusive para operações de **leitura** e **consulta**, com o nome da ação (verbo de negócio) codificado na própria URI, por exemplo:

- `POST /api/credito/solicitarCredito`
- `POST /api/credito/consultarStatusCredito`
- `POST /api/credito/buscarSolicitacaoPorId`
- `POST /api/credito/listarTodasSolicitacoes`
- `POST /api/credito/aprovarSolicitacao`
- `POST /api/credito/reprovarSolicitacao`
- `POST /api/credito/consultarClienteCore`

Isso viola pelo menos três princípios centrais do REST:

1. **Recursos (substantivos), não ações (verbos), nas URIs.** `/solicitarCredito`, `/aprovarSolicitacao` e `/reprovarSolicitacao` modelam a API como um conjunto de chamadas de procedimento remoto (estilo RPC "fingindo" ser HTTP), e não como manipulações de um recurso canônico (`/solicitacoes-credito`). O verbo da operação deveria estar no **método HTTP**, não na URI.
2. **Semântica correta dos métodos HTTP.** Operações de leitura como `consultarStatusCredito`, `buscarSolicitacaoPorId` e `listarTodasSolicitacoes` deveriam usar `GET` (uma operação seguramente idempotente e sem efeitos colaterais, cacheável), mas usavam `POST`. Isso impede caching HTTP padrão, quebra a idempotência esperada e é semanticamente incorreto.
3. **Uso incorreto dos status codes de sucesso.** Toda resposta retornava `200 OK` via `ResponseEntity.ok(...)` — inclusive a criação de uma nova Solicitação de Crédito (`solicitarCredito`), que, por criar um novo recurso, deveria retornar `201 Created` com um cabeçalho `Location` apontando para o recurso recém-criado.

A aplicação **`fiap-bank-mesa-operacoes-credito`** possui dois endpoints próprios em `POST` (`/aprovar` e `/reprovar`), mas eles **não são uma API REST**: são *form actions* de um controller Thymeleaf server-side renderizado, acionados por um `<form method="post">` no navegador — um padrão web legítimo e fora do escopo da crítica REST, já que não expõe um contrato de API para integração entre sistemas. Ainda assim, o Feign Client dessa aplicação (`PlataformaCreditoFeignClient`) consumia diretamente as rotas não-RESTful da Plataforma de Crédito, propagando o anti-padrão pelo ecossistema.

---

## 3. Explique a estratégia técnica adotada para refatorar o componente de forma a implementar o padrão RESTful e o DDD sem quebrar as aplicações cliente legadas.

A estratégia combinou **quatro técnicas complementares**:

### a) Isolamento físico via Maven Multi-Módulo + `maven-enforcer-plugin`
Tanto `fiap-bank-core-bancario` quanto `fiap-bank-plataforma-credito` foram reorganizados (ou construídos, no caso do Core) em 4 submódulos Maven físicos — `domain`, `infrastructure`, `application`, `presentation` — com a hierarquia de dependências `presentation → application → infrastructure → domain` estritamente unidirecional. A entidade `SolicitacaoCredito` foi desmembrada em duas classes: uma **Entidade de domínio pura** (`domain.model.SolicitacaoCredito`, sem nenhuma anotação de persistência, com um **Motor de Crédito** extraído para um serviço de domínio puro, `MotorDeCreditoDomainService`) e uma **Entidade JPA** (`infrastructure.persistence.entity.SolicitacaoCreditoJpaEntity`), conectadas por um *Repository Adapter* (`SolicitacaoCreditoRepositoryAdapter`) que implementa a interface `SolicitacaoCreditoRepository` definida no domínio (padrão **Ports & Adapters**). A conformidade dessa hierarquia é **auditada em tempo de build**: cada submódulo declara uma regra `bannedDependencies` do `maven-enforcer-plugin` que falha o build caso, por exemplo, o módulo `domain` declare uma dependência de Spring/JPA, ou o módulo `presentation` declare uma dependência direta de `domain`/`infrastructure`.

### b) Reengenharia para recursos RESTful genuínos
As 7 rotas de ação foram redesenhadas como um recurso canônico `/api/v1/solicitacoes-credito` (substantivo, plural), com os verbos HTTP assumindo o papel que a URI assumia antes:
- `POST /api/v1/solicitacoes-credito` (cria, `201 Created` + `Location`)
- `GET /api/v1/solicitacoes-credito` e `GET /api/v1/solicitacoes-credito/{id}` (consultas, `200 OK`)
- `GET /api/v1/solicitacoes-credito/cliente/{cpf}` (consulta por relacionamento)
- `PATCH /api/v1/solicitacoes-credito/{id}/status` (a decisão do operador — aprovação/reprovação — modelada como atualização parcial de um **sub-recurso** `status`, em vez de dois verbos de ação `aprovarSolicitacao`/`reprovarSolicitacao` na URI)

O mesmo princípio foi aplicado do zero ao Core Bancário (`/api/v1/clientes` e `/api/v1/contas`, com `GET`/`POST`/`PUT`/`DELETE` semanticamente corretos, conforme a Matriz de Endpoints da Seção 10.1 do Checkpoint).

### c) Padrão de Adaptação (Strangler Fig / Anti-Corruption Layer) para retrocompatibilidade
Em vez de alterar o contrato consumido pela Mesa de Operações e pelo Mobile, foi criado o `CreditoLegacyController`, um *Controller Adaptador* explicitamente marcado como `@Deprecated`, que preserva **byte a byte** as 7 rotas e os formatos de request/response originais em `/api/credito/*`, mas **delega toda a lógica** para os mesmos Casos de Uso da camada `application` (`SolicitacaoCreditoService`, `ConsultaCoreBancarioService`) usados pelo novo recurso REST — ou seja, não existe nenhuma regra de negócio duplicada entre o adaptador legado e a API nova; ambos são apenas duas "portas de entrada" (presentation) diferentes para o mesmo núcleo de domínio. Isso permite que `fiap-bank-mesa-operacoes-credito` (consumidor via Feign) e `fiap-bank-internet-banking-mobile` (consumidor via Angular `HttpClient`) continuem funcionando **sem nenhuma alteração de contrato**, enquanto toda a arquitetura interna foi reescrita.

### d) Validação, tratamento de erros e integração resiliente
Todos os DTOs de entrada (novos e legados) passaram a usar **Jakarta Bean Validation** (`@NotBlank`, `@Pattern`, `@Positive`, etc.), com um `GlobalExceptionHandler` (`@RestControllerAdvice`) centralizando a tradução de exceções de domínio em códigos HTTP corretos (`404` para "não encontrado", `409` para conflitos de regra de negócio, `400` para validação). A comunicação com o Core Bancário passou a usar um `CoreBancarioPort` (interface de domínio) implementado por um adaptador Feign real (`CoreBancarioAdapter`), que consulta o novo endpoint RESTful `GET /api/v1/contas/cliente/{cpf}` do Core Bancário e degrada graciosamente (retornando um estado `"INDISPONIVEL"` honesto, em vez de dados fictícios) caso o Core esteja fora do ar — preservando a alta disponibilidade exigida pelo cenário de negócio sem comprometer a transparência dos dados exibidos ao usuário final.

Com essas quatro técnicas combinadas, foi possível migrar a Plataforma de Crédito para uma arquitetura DDD estrita e uma API RESTful completa, **sem um único minuto de indisponibilidade de contrato** para os dois front-ends legados em produção.
