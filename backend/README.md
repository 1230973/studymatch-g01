# StudyMatch — Backend (Sprints 1 e 2)

Backend do projeto **StudyMatch — Os Mestres do Java**, desenvolvido em **Java (JDK 17 ou superior)**, **Spring Boot 3.5.16**, **Maven**, **Spring Data JPA** e **PostgreSQL**.

No **Sprint 1**, foi criada a base full-stack da aplicação: API REST para cursos e estudantes, persistência e comunicação com o frontend React (Issue #7). No **Sprint 2**, foram integradas a gestão do histórico académico (Issue #13), o cálculo do perfil de competências (Issue #15), a estratégia *Cold Start* (Issue #17) e a edição e visualização dos dados no frontend. As funcionalidades do Sprint 2 foram testadas através da API e da interface React.

> A **formação automática de grupos** não faz parte do âmbito do Sprint 2 e não está implementada nesta versão.

## 1. Requisitos

- Java JDK 17 ou superior (`java -version`).
- Apache Maven disponível no `PATH` (`mvn -version`).
- Docker Desktop com Docker Compose, ou uma instância PostgreSQL local configurada.
- Para utilizar a aplicação completa: Node.js e npm (ver [`../README.md`](../README.md)).

## 2. Iniciar a base de dados PostgreSQL

Abrir um terminal na pasta `studymatch-g01/backend`:

```powershell
docker compose up -d
docker compose ps
```

Aguardar que o contentor PostgreSQL do StudyMatch apresente o estado **healthy**. Na configuração local, o PostgreSQL é disponibilizado em `localhost:5432`.

| Definição de desenvolvimento | Valor |
| --- | --- |
| Base de dados | `studymatch` |
| Utilizador | `studymatch` |
| Palavra-passe | `studymatch_dev` |
| Porta PostgreSQL | `5432` |

Estes valores são **exclusivos do ambiente local de demonstração**; não devem ser utilizados como credenciais de produção.

Se a porta `5432` estiver ocupada por outra instância de PostgreSQL, verificar primeiro qual o contentor em execução. Se necessário, ajustar o mapeamento de portas no ficheiro Compose e atualizar a variável `DB_URL`.

Para parar a base de dados sem eliminar os registos:

```powershell
docker compose down
```

**Não usar `docker compose down -v`** quando pretender conservar os dados, uma vez que a opção `-v` elimina os volumes geridos pelo Compose.

## 3. Executar o backend

Dentro da pasta `backend/`, iniciar o Spring Boot com os dados de demonstração:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Em alternativa, sem o perfil de demonstração:

```powershell
mvn spring-boot:run
```

A API fica disponível em **http://localhost:8080**. Para verificar o estado da aplicação e da ligação à base de dados, abrir:

`http://localhost:8080/api/health`

Manter o terminal aberto enquanto a API estiver em utilização. Se a porta `8080` estiver ocupada, verificar se já existe outra instância do backend antes de iniciar uma segunda.

### Dados de demonstração

No perfil `demo`, a configuração `DemoDataConfig.java` prepara os dados necessários aos cenários de teste, sem duplicar os registos que já existam:

- Curso `EI` — Engenharia Informática.
- Estudante `2026001` — Estudante Exemplo.
- Unidades curriculares `PROG` (Programação), `BD` (Bases de Dados) e `ES` (Engenharia de Software).
- Competências `PROGRAMMING`, `DATABASES` e `SOFTWARE_ENGINEERING`, associadas às respetivas unidades curriculares.

As classificações e respostas ao questionário não são valores fixos: dependem dos registos guardados em cada base de dados local.

A configuração de desenvolvimento utiliza atualização do esquema através do Hibernate (`ddl-auto: update`). **Isto não substitui migrações de base de dados para produção.**

## 4. Endpoints REST

### Sprint 1 — Cursos e estudantes

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/health` | Estado da API e da ligação à base de dados |
| `GET` | `/api/degree-courses` | Listar cursos |
| `POST` | `/api/degree-courses` | Criar curso |
| `GET` | `/api/students` | Listar estudantes |
| `GET` | `/api/students/{studentNumber}` | Consultar um estudante |
| `POST` | `/api/students` | Registar um estudante |

O pedido de registo de estudante utiliza `degreeCourseCode` para identificar o curso existente.

### Sprint 2 — Gestão de estudantes e histórico académico

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `PUT` | `/api/students/{studentNumber}` | Atualizar os dados de um estudante |
| `GET` | `/api/course-units` | Listar unidades curriculares |
| `GET` | `/api/students/{studentNumber}/academic-records` | Consultar classificações do estudante |
| `POST` | `/api/students/{studentNumber}/academic-records` | Registar uma nova classificação |

### Sprint 2 — Competências e perfil académico

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/competencies` | Listar as competências disponíveis |
| `GET` | `/api/students/{studentNumber}/profile` | Consultar o perfil calculado do estudante |

### Sprint 2 — Questionário Cold Start

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/cold-start/questions` | Consultar as perguntas e a escala de resposta |
| `GET` | `/api/students/{studentNumber}/cold-start` | Consultar as respostas do estudante |
| `POST` | `/api/students/{studentNumber}/cold-start` | Guardar ou atualizar uma resposta |

**Nota:** a pesquisa e os filtros por nome, número, curso e ano estão implementados na interface React. A listagem de estudantes é disponibilizada pela API; não é necessário supor que existem endpoints dedicados para cada filtro.

## 5. Modelo de dados e organização

As principais entidades persistentes são:

| Entidade | Responsabilidade |
| --- | --- |
| `DegreeCourse` | Curso superior e respetivos dados |
| `Student` | Identificação, contacto, ano e curso do estudante |
| `CourseUnit` | Unidade curricular e competência principal associada |
| `AcademicRecord` | Classificação, unidade curricular, estudante, ano letivo e semestre |
| `Competency` | Código e nome de uma competência |
| `ColdStartAnswer` | Resposta de autoavaliação de um estudante a uma competência |

O perfil é **calculado** a partir dos dados persistidos, em vez de depender de um valor estático guardado para cada competência.

Estrutura principal do backend:

```text
backend/
├── docs/          # Documentação técnica do perfil e do Cold Start
├── src/main/java/pt/studymatch/backend/
│   ├── config/    # Configurações e dados de demonstração
│   ├── controller/# Endpoints REST
│   ├── dto/       # Pedidos e respostas JSON
│   ├── exception/ # Tratamento de exceções
│   ├── model/     # Entidades JPA
│   ├── repository/# Acesso à base de dados
│   └── service/   # Regras de negócio
└── src/test/java/pt/studymatch/backend/ # Testes automatizados
```

## 6. Histórico académico — Issue #13

Cada registo académico associa um estudante a uma unidade curricular e contém os campos `grade`, `academicYear` e `semester`.

Ao criar um registo, o `AcademicRecordService` aplica as seguintes regras:

- Todos os campos do pedido são obrigatórios.
- A classificação tem de estar entre **0 e 20**, inclusive.
- O semestre tem de ser **1 ou 2**.
- O ano letivo tem de respeitar o formato `AAAA/AAAA` (por exemplo, `2025/2026`). A validação verifica o formato, não se os anos são consecutivos.
- O estudante e a unidade curricular têm de existir.
- O código da unidade curricular é normalizado para maiúsculas antes da pesquisa.

Uma classificação inválida devolve **HTTP 400 Bad Request**. Um estudante ou unidade curricular inexistente origina **HTTP 404 Not Found**.

### Exemplo: registar uma classificação

Com o backend em execução, abrir outro terminal PowerShell:

```powershell
$body = @{
    courseUnitCode = "PROG"
    grade = 16
    academicYear = "2025/2026"
    semester = 1
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/students/2026001/academic-records" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

Uma criação bem-sucedida devolve **HTTP 201 Created**. **Este POST insere um registo novo**: repeti-lo poderá adicionar mais uma classificação e alterar a média calculada.

Para consultar o histórico:

`http://localhost:8080/api/students/2026001/academic-records`

## 7. Competências e cálculo do perfil — Issue #15

Nesta versão foram definidas três competências iniciais:

| Unidade curricular | Competência |
| --- | --- |
| `PROG` | `PROGRAMMING` |
| `BD` | `DATABASES` |
| `ES` | `SOFTWARE_ENGINEERING` |

Cada unidade curricular contribui para uma competência principal. Para cada competência com classificações académicas, a pontuação é calculada por:

**Pontuação académica = média aritmética das classificações associadas × 5**

A escala final vai de **0 a 100**. Por exemplo, uma nota de 16 valores corresponde a 80 pontos. Duas notas de 12 e 18 correspondem a uma média de 15 e a 75 pontos. Todas as classificações existentes para essa competência têm o mesmo peso na versão atual.

Quando não existe nenhuma classificação, a pontuação académica não é inventada. O perfil é recalculado a cada consulta, pelo que novas notas afetam a resposta seguinte.

O endpoint do perfil é:

`GET /api/students/{studentNumber}/profile`

A resposta contém `studentNumber` e uma lista `competencies`, cujos elementos incluem:

- `code` e `name` — identificação da competência.
- `score` — pontuação de 0 a 100, ou `null` quando desconhecida.
- `evidenceCount` — quantidade de registos **académicos** utilizados.
- `source` — origem da pontuação (`ACADEMIC`, `COLD_START` ou `UNKNOWN`).

## 8. Cold Start — Issue #17

O Cold Start permite construir um perfil inicial para estudantes sem notas académicas, através de **autoavaliação numa escala de 1 a 5**. As perguntas são obtidas em:

`GET /api/cold-start/questions`

A pontuação de autoavaliação é calculada por:

**Pontuação Cold Start = (resposta − 1) × 25**

| Resposta | Pontuação |
| --- | --- |
| 1 | 0 |
| 2 | 25 |
| 3 | 50 |
| 4 | 75 |
| 5 | 100 |

Cada estudante pode ter **uma resposta por competência**. Ao enviar uma nova resposta à mesma competência, o registo existente é atualizado em vez de duplicado.

### Regra de prioridade das evidências

O sistema escolhe a origem de cada competência pela seguinte ordem:

1. **`ACADEMIC`** — se existem notas para a competência, utiliza a média académica × 5.
2. **`COLD_START`** — se não existem notas, mas existe autoavaliação, utiliza `(rating − 1) × 25`.
3. **`UNKNOWN`** — se não existem notas nem autoavaliação, devolve `score: null`.

Uma competência baseada apenas em autoavaliação apresenta `evidenceCount: 0`, porque esse campo conta **registos académicos**, não respostas ao questionário.

Exemplo: um estudante responde 4/5 em Programação e recebe 75 pontos (`COLD_START`). Mais tarde, obtém 14 valores a Programação e o perfil passa a 70 pontos (`ACADEMIC`). A resposta Cold Start continua guardada, mas a evidência académica prevalece.

### Exemplo: guardar uma resposta Cold Start

```powershell
$body = @{
    competencyCode = "DATABASES"
    rating = 3
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/students/2026001/cold-start" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

O valor `3` corresponde a `initialScore: 50`. Para consultar as respostas guardadas:

`GET /api/students/2026001/cold-start`

Respostas fora do intervalo **1 a 5** são rejeitadas com **HTTP 400**.

## 9. Testes automatizados e build

Na pasta `backend/`, executar:

```powershell
mvn clean test
mvn clean package
```

Os testes utilizam **JUnit 5** e **Mockito**, além dos testes de integração existentes. Entre as classes de teste estão:

- `StudyMatchApiIntegrationTest.java` — testes da API.
- `AcademicRecordServiceTest.java` — criação de registos, classificações inválidas e limites 0 e 20.
- `StudentProfileServiceTest.java` — cálculo com uma nota, média de várias notas, dados ausentes, estudante inexistente e prioridade académica.
- `ColdStartServiceTest.java` — criação e atualização de respostas e rejeição de valores fora do intervalo.

O perfil de testes utiliza uma base de dados **H2 em memória**, pelo que os testes automatizados não exigem o PostgreSQL a correr. Para verificar a persistência real é necessário testar também a API com PostgreSQL.

`mvn clean package` executa os testes e cria o ficheiro JAR na pasta `backend/target/`.

### Testar também o frontend

Na pasta `frontend/`, executar:

```powershell
npm.cmd install
npm.cmd run lint
npm.cmd run build
```

O build Vite produz os ficheiros finais em `frontend/dist/`.

## 10. Demonstração integrada do Sprint 2

Com PostgreSQL, Spring Boot e React em execução (`http://localhost:5173/`), demonstrar:

1. **Estudante com histórico:** consultar as unidades curriculares e registar uma classificação. Verificar o cálculo das competências.
2. **Cold Start:** num estudante sem notas, responder às perguntas e verificar as pontuações iniciais.
3. **Transição:** adicionar uma nota numa competência anteriormente avaliada por Cold Start e confirmar que a origem passa a `ACADEMIC`.
4. **Funcionalidade extra:** pesquisar ou filtrar estudantes por curso, ano, nome ou número; testar também a edição de dados.
5. **Qualidade:** executar `mvn clean package` e demonstrar os testes automáticos sem falhas.

O fluxo integrado é: **React → API Spring Boot → Services → Repositories JPA → PostgreSQL**. Os resultados devem continuar disponíveis após reiniciar a aplicação, desde que o volume da base de dados seja preservado.

## 11. Variáveis de ambiente

| Variável | Valor por omissão local | Utilização |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/studymatch` | Endereço JDBC PostgreSQL |
| `DB_USER` | `studymatch` | Utilizador da base de dados |
| `DB_PASSWORD` | `studymatch_dev` | Palavra-passe apenas de desenvolvimento |
| `SERVER_PORT` | `8080` | Porta HTTP da API |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | Origens autorizadas para o frontend |

Não partilhar credenciais reais de produção nem dados pessoais em commits ou Issues.

## 12. Documentação adicional e workflow

- [README principal — execução da aplicação completa](../README.md)
- [Perfil académico — regras de cálculo](docs/student-profile.md)
- [Estratégia Cold Start](docs/cold-start.md)
- [Workflow de desenvolvimento](../WORKFLOW.md)
- [Arquitetura do projeto](../docs/architecture.md)
- [Modelo de domínio](../docs/DOMAIN_MODEL.md)

O trabalho segue o fluxo **Issue → Branch → Implementação → Pull Request → Review → Merge**, com revisão por outro membro da equipa antes da integração na `main`.

Referências às principais tarefas: [Issue #13](https://github.com/1230973/studymatch-g01/issues/13) (histórico académico), [Issue #15](https://github.com/1230973/studymatch-g01/issues/15) (competências e perfil), [Issue #17](https://github.com/1230973/studymatch-g01/issues/17) (Cold Start) e [Issue #21](https://github.com/1230973/studymatch-g01/issues/21) (documentação final).
