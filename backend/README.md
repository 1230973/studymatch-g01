# StudyMatch — Backend (Sprint 1 + Sprint 2, Issue #13)

Backend do projeto **StudyMatch — Os Mestres do Java**, desenvolvido em **Java (JDK 17 ou superior)**, **Spring Boot 3.5.16**, **Maven** e **PostgreSQL**.

No **Sprint 1**, foi criada a base executável da API REST para cursos e estudantes e integrada com o frontend React (Issue #7).

No **Sprint 2**, a **Issue #13 — Implementar modelo e API do histórico académico** acrescenta unidades curriculares, registo e consulta de classificações, validações e testes automatizados. **O Sprint 2 ainda não está concluído**: a edição de estudantes, o cálculo do perfil de competências e o Cold Start pertencem a outras tarefas ou fases de integração.

## 1. Requisitos

- JDK 17 ou superior (`java -version`).
- Apache Maven (`mvn -version`).
- Docker Desktop com Docker Compose (recomendado), **ou** PostgreSQL instalado localmente.
- Para utilizar o frontend, Node.js e npm (ver `../README.md`).

## 2. Iniciar a base de dados

Abrir um terminal **na pasta `studymatch-g01/backend`** e executar:

```powershell
docker compose up -d
docker compose ps
```

Aguardar até o contentor `studymatch-postgres` apresentar o estado **`Up (healthy)`**.

A configuração de desenvolvimento cria a base de dados `studymatch` em `localhost:5432`, com o utilizador `studymatch` e a palavra-passe `studymatch_dev` (**apenas para desenvolvimento local**).

Se a porta `5432` estiver ocupada, pode ajustar-se o mapeamento no ficheiro `compose.yaml` (por exemplo, `5433:5432`) e configurar `DB_URL=jdbc:postgresql://localhost:5433/studymatch`.

Para parar os contentores **sem apagar os dados**:

```powershell
docker compose down
```

**Não executar `docker compose down -v` se quiseres preservar os dados de teste**, pois essa opção remove também os volumes geridos pelo Compose.

Sem Docker, é necessário criar a base de dados no PostgreSQL local e configurar `DB_URL`, `DB_USER` e `DB_PASSWORD`.

## 3. Executar o backend

No terminal, dentro da pasta `backend/`:

```powershell
mvn spring-boot:run
```

A API fica disponível em **http://localhost:8080**.

Para iniciar com os **dados de demonstração**, usar:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

O perfil `demo` cria os registos apenas quando ainda não existem:

- Curso `EI` — Engenharia Informática.
- Estudante `2026001` — Estudante Exemplo.
- Unidade curricular `PROG` — Programacao.
- Unidade curricular `BD` — Bases de Dados.
- Unidade curricular `ES` — Engenharia de Software.

Estes dados são inseridos pelo ficheiro `config/DemoDataConfig.java`. A configuração de desenvolvimento utiliza Hibernate com `ddl-auto: update` para atualizar o esquema da base de dados; **não se trata de um mecanismo de migrações de produção**.

Manter o terminal do Spring Boot aberto durante os testes. Se a porta `8080` estiver ocupada, confirmar primeiro se já existe outra instância do backend a executar, em vez de iniciar uma segunda.

## 4. Endpoints REST

### Sprint 1 — Cursos e estudantes

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/health` | Estado da API e ligação à base de dados |
| `GET` | `/api/degree-courses` | Listar cursos |
| `POST` | `/api/degree-courses` | Criar curso (`code`, `name`, `totalCredits`) |
| `GET` | `/api/students` | Listar estudantes |
| `GET` | `/api/students/{studentNumber}` | Consultar um estudante |
| `POST` | `/api/students` | Registar estudante num curso existente |

No registo de estudantes, o campo JSON que identifica o curso é **`degreeCourseCode`**.

### Sprint 2 — Histórico académico (Issue #13)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/course-units` | Listar unidades curriculares |
| `GET` | `/api/students/{studentNumber}/academic-records` | Consultar o histórico de um estudante |
| `POST` | `/api/students/{studentNumber}/academic-records` | Registar uma classificação de um estudante |

O `GET /api/course-units` devolve as unidades curriculares persistidas. Nesta fase, o repositório disponibiliza as três cadeiras de exemplo através do perfil `demo`; **não foi implementado um endpoint de criação de unidades curriculares**.

### Exemplo — Registar uma classificação

Com a base de dados operacional e o backend iniciado no perfil `demo`, executar num **novo terminal PowerShell**:

```powershell
$body = @{
    courseUnitCode = "PROG"
    grade = 16
    academicYear = "2025/2026"
    semester = 1
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/students/2026001/academic-records" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

Uma criação válida devolve **HTTP `201 Created`**, com uma resposta semelhante a:

```json
{
  "id": 1,
  "courseUnitCode": "PROG",
  "courseUnitName": "Programacao",
  "grade": 16,
  "academicYear": "2025/2026",
  "semester": 1
}
```

O identificador pode ser diferente. Este pedido é um **teste manual**, não código a acrescentar aos ficheiros Java.

### Exemplo — Consultar o histórico

No navegador, abrir:

http://localhost:8080/api/students/2026001/academic-records

Ou executar no PowerShell:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/students/2026001/academic-records"
```

O endpoint devolve uma lista JSON com os registos académicos do estudante. **Não é necessário criar novamente a nota para a consultar.**

### Outros pedidos de verificação

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/health"
Invoke-RestMethod -Uri "http://localhost:8080/api/students"
Invoke-RestMethod -Uri "http://localhost:8080/api/degree-courses"
Invoke-RestMethod -Uri "http://localhost:8080/api/course-units"
```

## 5. Validações da Issue #13

O `AcademicRecordService` aplica as seguintes regras ao registar uma classificação:

- Todos os campos do pedido são obrigatórios.
- A nota deve estar entre **0 e 20**, inclusive.
- O semestre deve ser **1 ou 2**.
- O ano letivo deve respeitar o formato `AAAA/AAAA` (por exemplo, `2025/2026`). Esta validação verifica **o formato**, não se o segundo ano é consecutivo ao primeiro.
- O estudante indicado no URL tem de existir.
- A unidade curricular indicada em `courseUnitCode` tem de existir; o código é convertido para maiúsculas antes da pesquisa.

Os pedidos inválidos são rejeitados com **HTTP `400 Bad Request`**; estudantes ou unidades curriculares inexistentes originam **HTTP `404 Not Found`**. As validações de duplicação já existentes para estudantes e cursos mantêm o comportamento anterior (`409 Conflict`, quando aplicável).

**Teste manual realizado:** foi registada uma nota de `16` na unidade curricular `PROG`; uma tentativa de registar a nota `25` recebeu `400 Bad Request` e não acrescentou um novo registo. Após reiniciar o backend, a nota válida continuou acessível, demonstrando a persistência no PostgreSQL.

## 6. Testes automatizados e build

Executar estes comandos **na pasta `backend/`**:

```powershell
mvn clean test
mvn clean package
```

O projeto contém:

- `StudyMatchApiIntegrationTest.java` — testes existentes da API do Sprint 1.
- `AcademicRecordServiceTest.java` — testes unitários JUnit 5 e Mockito para o registo académico: criação válida, nota superior a 20, nota negativa e condições-limite (0 e 20).

Os testes automatizados utilizam **H2 em memória** no perfil de testes e **não dependem do Docker**. Os testes com PostgreSQL realizados manualmente validam a integração com a base de dados real.

O comando `mvn clean package` também produz um artefacto **JAR** na pasta `target/`.

**Nota:** um build com sucesso não substitui os testes das futuras regras de perfil e Cold Start. Essas regras exigem testes próprios no Sprint 2.

## 7. Variáveis de ambiente

| Variável | Predefinição local | Utilização |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/studymatch` | Endereço JDBC PostgreSQL |
| `DB_USER` | `studymatch` | Utilizador da base de dados |
| `DB_PASSWORD` | `studymatch_dev` | Palavra-passe de desenvolvimento |
| `SERVER_PORT` | `8080` | Porta HTTP do Spring Boot |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | Origens permitidas para o frontend |

**Não colocar credenciais de produção no GitHub.** A palavra-passe documentada corresponde apenas ao ambiente de demonstração local.

## 8. Organização do código

O backend mantém a separação de responsabilidades do Sprint 1:

```text
src/main/java/pt/studymatch/backend/
├── config/       # CORS e dados de demonstração
├── controller/   # Endpoints HTTP
├── dto/          # Pedidos e respostas JSON
├── exception/    # Tratamento de erros
├── model/        # Entidades JPA
├── repository/   # Acesso à base de dados
└── service/      # Validações e regras de negócio
```

Na Issue #13 foram acrescentados:

- **Model:** `CourseUnit.java` e `AcademicRecord.java`.
- **Repository:** `CourseUnitRepository.java` e `AcademicRecordRepository.java`.
- **DTO:** `CourseUnitResponse.java`, `CreateAcademicRecordRequest.java` e `AcademicRecordResponse.java`.
- **Service:** `CourseUnitService.java` e `AcademicRecordService.java`.
- **Controller:** `CourseUnitController.java` e `AcademicRecordController.java`.
- **Config:** atualização de `DemoDataConfig.java` com unidades curriculares de exemplo.
- **Testes:** `AcademicRecordServiceTest.java`.

### Decisões de modelação do histórico académico

- `CourseUnit` identifica cada unidade curricular por um ID e um código único.
- `AcademicRecord` associa um estudante a uma unidade curricular e guarda a classificação (`BigDecimal`), o ano letivo e o semestre.
- Um estudante pode ter vários registos académicos; uma unidade curricular pode surgir no histórico de vários estudantes.
- A API utiliza **DTOs** para não expor diretamente entidades JPA nas respostas.
- O histórico fica **persistido no PostgreSQL**; no futuro, será utilizado para gerar perfis de competências.

## 9. Integração com o frontend

A integração **React → Spring Boot → PostgreSQL** já foi realizada no **Sprint 1 (Issue #7)** para a listagem e o registo de estudantes.

O frontend utiliza `frontend/src/services/studentService.js` para comunicar com a API. Os endpoints do histórico académico adicionados na Issue #13 já estão disponíveis no backend, mas **a interface React para introduzir notas e apresentar perfis ainda não está concluída nesta Issue**.

Consultar o [README principal](../README.md) para os passos de execução do frontend e do projeto completo.

## 10. Funcionalidades previstas para as próximas tarefas do Sprint 2

A Issue #13 **não implementa** ainda:

- Edição de estudantes pela API.
- Alteração de uma classificação já registada.
- Gestão de competências e cálculo do perfil académico.
- Estratégia e questionário de Cold Start.
- Visualização do histórico e do perfil no frontend React.
- Pesquisa/filtragem como funcionalidade adicional.
- Formação automática de grupos (fora do âmbito do Sprint 2).

Estas funcionalidades serão desenvolvidas e integradas separadamente, com as respetivas Issues, Pull Requests e testes.

---

**Estado desta documentação:** inclui o Sprint 1 e o trabalho verificado da **Issue #13 do Sprint 2**, sem apresentar o Sprint 2 completo como terminado.
