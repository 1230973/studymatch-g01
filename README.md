# StudyMatch

**Os Mestres do Java — Adaptive Student Group Formation — Software Quality Project**

O **StudyMatch** é uma aplicação de apoio à futura formação de grupos académicos, tendo em conta informação dos estudantes e, nas fases seguintes do projeto, o seu percurso académico, competências e preferências.

O projeto foi iniciado no **Sprint 1**, com uma base full-stack funcional. No **Sprint 2**, está em desenvolvimento o subsistema de perfil académico e *Cold Start*. A **Issue #13** acrescenta a estrutura de histórico académico ao backend; as restantes funcionalidades do Sprint 2 não são apresentadas aqui como concluídas.

## Tecnologias

| Componente | Tecnologia |
| --- | --- |
| Frontend | React + Vite |
| Backend | Java + Spring Boot |
| Persistência | PostgreSQL + Spring Data JPA |
| Base de dados local | Docker Compose |
| Build e testes do backend | Maven, JUnit 5 e Mockito |
| Build e análise do frontend | npm, Vite e ESLint |

## Requisitos

- Git.
- Java JDK 17 ou superior.
- Apache Maven disponível no `PATH`.
- Docker Desktop com o motor em execução.
- Node.js e npm compatíveis com a versão do Vite do projeto.

## Obter o projeto

```powershell
git clone https://github.com/1230973/studymatch-g01.git
cd studymatch-g01
```

Se o projeto já estiver clonado, entrar na sua pasta e atualizar a branch pretendida através do fluxo GitHub da equipa. Não executar `git pull` numa branch com alterações locais por guardar sem as verificar primeiro.

## Executar a aplicação completa

Os comandos seguintes partem da **raiz do repositório** (`studymatch-g01`). Utilizar terminais separados para o backend e o frontend.

### 1. Iniciar o PostgreSQL

No primeiro terminal:

```powershell
cd backend
docker compose up -d
docker compose ps
```

Aguardar até o contentor `studymatch-postgres` apresentar o estado `healthy`. O PostgreSQL é disponibilizado na porta **5432**.

### 2. Iniciar o backend

Num segundo terminal, a partir da raiz do repositório:

```powershell
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

O backend fica disponível em **http://localhost:8080**. O perfil `demo` cria, quando ainda não existem, um curso de Engenharia Informática, um estudante de exemplo e as unidades curriculares de demonstração indicadas na secção do Sprint 2.

Para verificar a ligação entre a aplicação e a base de dados:

http://localhost:8080/api/health

A resposta deverá indicar `status: UP` e `database: UP`.

**Manter este terminal aberto** enquanto a API estiver a ser utilizada. Se a porta 8080 estiver ocupada, verificar se já existe outra instância do Spring Boot em execução antes de iniciar uma nova.

### 3. Iniciar o frontend

Num terceiro terminal, a partir da raiz do repositório:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Abrir o endereço indicado pelo Vite, normalmente:

http://localhost:5173/

O frontend comunica com a API do Spring Boot através de HTTP/JSON. Os pedidos relativos a estudantes estão centralizados no ficheiro `frontend/src/services/studentService.js`.

### 4. Parar a aplicação

- Parar o Spring Boot e o Vite nos respetivos terminais com `Ctrl + C`.
- Para parar o PostgreSQL, na pasta `backend` executar:

```powershell
docker compose down
```

O comando `docker compose down` **não elimina o volume de dados** do PostgreSQL. Não utilizar a opção `-v` se for necessário conservar os registos.

## Sprint 1 — Base full-stack (Issue #7)

No Sprint 1 foi implementado e testado o fluxo:

```text
Frontend React + Vite
        |
        | HTTP / JSON
        v
API REST Spring Boot
        |
        | Spring Data JPA
        v
PostgreSQL
```

### Funcionalidades implementadas no Sprint 1

- Registo de novos estudantes.
- Consulta da lista de estudantes na interface e na API.
- Atualização da lista de estudantes no frontend.
- Validação básica dos dados do formulário e apresentação de erros da API.
- Consulta de cursos através da API.
- Persistência dos dados no PostgreSQL, incluindo após reinício do backend.
- Interface inicial com áreas informativas para competências, *Cold Start*, disponibilidade e formação de grupos.

As áreas informativas **não significam** que o algoritmo de formação de grupos ou as funcionalidades avançadas já tenham sido implementados.

### Principais endpoints do Sprint 1

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/health` | Estado da aplicação e da base de dados |
| `GET` | `/api/students` | Listar estudantes |
| `POST` | `/api/students` | Criar estudante |
| `GET` | `/api/students/{studentNumber}` | Consultar estudante |
| `GET` | `/api/degree-courses` | Listar cursos |
| `POST` | `/api/degree-courses` | Criar curso |

### Verificar a integração do Sprint 1

1. Abrir `http://localhost:5173/`.
2. Consultar a lista de estudantes.
3. Registar um estudante com número e email ainda não utilizados.
4. Confirmar que aparece na lista.
5. Consultar `http://localhost:8080/api/students` e confirmar o registo.
6. Reiniciar apenas o backend e confirmar que o estudante continua disponível.

## Sprint 2 — Histórico académico (Issue #13)

**Estado:** funcionalidade de backend implementada e testada na branch de trabalho `feat/13-academic-history`. A integração desta funcionalidade no frontend e os restantes requisitos do Sprint 2 pertencem a tarefas posteriores ou paralelas.

A Issue #13 acrescenta a representação das unidades curriculares e das classificações dos estudantes, permitindo que o futuro mecanismo de perfil de competências utilize evidência académica persistida.

### Novas entidades

**`CourseUnit`** — unidade curricular:

| Atributo | Descrição |
| --- | --- |
| `id` | Identificador interno |
| `code` | Código único da unidade curricular |
| `name` | Nome da unidade curricular |

**`AcademicRecord`** — registo académico:

| Atributo | Descrição |
| --- | --- |
| `id` | Identificador interno |
| `student` | Estudante associado |
| `courseUnit` | Unidade curricular associada |
| `grade` | Classificação obtida |
| `academicYear` | Ano letivo, no formato `AAAA/AAAA` |
| `semester` | Semestre da classificação |

As entidades existentes `Student` e `DegreeCourse` são reutilizadas. As relações são persistidas através de Spring Data JPA no PostgreSQL.

### Endpoints implementados na Issue #13

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/course-units` | Listar unidades curriculares |
| `GET` | `/api/students/{studentNumber}/academic-records` | Consultar histórico académico de um estudante |
| `POST` | `/api/students/{studentNumber}/academic-records` | Registar uma classificação |

**Nota:** estes endpoints pertencem ao backend. A interface React para adicionar ou consultar classificações ainda não faz parte da Issue #13.

### Dados de demonstração

Com o perfil `demo`, o ficheiro `DemoDataConfig.java` insere as seguintes unidades curriculares **caso ainda não existam**:

| Código | Unidade curricular |
| --- | --- |
| `PROG` | Programacao |
| `BD` | Bases de Dados |
| `ES` | Engenharia de Software |

O mesmo perfil mantém a criação inicial do estudante de exemplo com o número `2026001`. A verificação prévia evita repetir esses dados a cada arranque.

### Exemplo — Consultar unidades curriculares

Abrir no navegador:

http://localhost:8080/api/course-units

A resposta é uma lista JSON com `id`, `code` e `name` de cada unidade curricular.

### Exemplo — Registar uma classificação

Com o backend a funcionar, abrir **outro terminal PowerShell** e executar:

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

O endpoint devolve **HTTP 201 Created**, acompanhado do registo criado, caso os dados sejam válidos e o estudante e a unidade curricular existam. Este comando **cria um novo registo**; executá-lo repetidamente pode criar mais do que uma classificação de demonstração.

### Exemplo — Consultar o histórico

Abrir:

http://localhost:8080/api/students/2026001/academic-records

Exemplo ilustrativo de resposta:

```json
[
  {
    "id": 1,
    "courseUnitCode": "PROG",
    "courseUnitName": "Programacao",
    "grade": 16,
    "academicYear": "2025/2026",
    "semester": 1
  }
]
```

O `id` e a quantidade de registos dependem dos dados efetivamente guardados.

### Validações implementadas

- Todos os campos do pedido de criação são obrigatórios.
- A nota tem de estar entre **0 e 20**, inclusive.
- O semestre tem de ser **1 ou 2**.
- O ano letivo deve respeitar o formato `AAAA/AAAA` (por exemplo, `2025/2026`).
- O estudante e a unidade curricular têm de existir.
- Os códigos das unidades curriculares são normalizados para maiúsculas na criação do histórico.
- Pedidos inválidos são rejeitados; uma nota fora do intervalo permitido devolve **HTTP 400**.

A validação implementada para o ano letivo verifica o **formato**, não se o segundo ano é necessariamente consecutivo do primeiro.

### Testes realizados na Issue #13

Foram executados testes manuais da API e testes automatizados com Maven:

- Registo de uma classificação válida de 16 valores.
- Rejeição de uma classificação inválida de 25 valores, sem a persistir.
- Consulta do histórico académico por estudante.
- Confirmação de que a nota válida continua disponível após reinício do backend.
- Testes unitários do `AcademicRecordService` com **JUnit 5 e Mockito**, incluindo nota válida, notas fora do intervalo e valores-limite (`0` e `20`).

Os testes unitários simulam os Repositories; **não substituem** a verificação manual de persistência com PostgreSQL.

## Compilar e executar os testes

### Backend

A partir da raiz do repositório:

```powershell
cd backend
mvn clean test
mvn clean package
```

`mvn clean test` compila o código e executa os testes automatizados. `mvn clean package` executa o processo de build e produz o artefacto do backend em `backend/target/`.

### Frontend

A partir da raiz do repositório:

```powershell
cd frontend
npm.cmd install
npm.cmd run lint
npm.cmd run build
```

O comando de build produz os ficheiros do frontend na pasta `frontend/dist/`.

## Organização do backend

```text
backend/src/main/java/pt/studymatch/backend/
├── config/       # Configuração e dados de demonstração
├── controller/   # Endpoints REST
├── dto/          # Dados de entrada e de saída da API
├── exception/    # Exceções e tratamento de erros
├── model/        # Entidades persistentes
├── repository/   # Acesso à base de dados com JPA
└── service/      # Regras de negócio
```

Os testes Java encontram-se em `backend/src/test/java/pt/studymatch/backend/`.

## Trabalho ainda previsto no Sprint 2

A Issue #13 **não corresponde à conclusão de todo o Sprint 2**. Continuam previstos, noutras tarefas da equipa:

- Atualização dos dados dos estudantes.
- Cálculo do perfil de competências com base no histórico académico.
- Implementação e testes da estratégia *Cold Start*.
- Interface React para histórico, perfil de competências e *Cold Start*.
- Uma funcionalidade adicional definida pela equipa.
- Documentação e demonstração da integração completa do Sprint 2.

A **formação automática de grupos** não é exigida no Sprint 2 e fica reservada para fases posteriores do projeto.

## Processo de desenvolvimento

O trabalho é organizado no GitHub pelo fluxo:

```text
Issue → Branch → Implementação → Pull Request → Review → Merge
```

Cada Pull Request deve ser revisto por outro elemento da equipa antes de ser integrado na branch `main`.

- Repositório: https://github.com/1230973/studymatch-g01
- Issue #13 — Histórico académico: https://github.com/1230973/studymatch-g01/issues/13

## Documentação adicional

- [Documentação do backend](backend/README.md)
- [Workflow da equipa](WORKFLOW.md)
- [Casos de uso](docs/USE_CASES.md)
- [Modelo de domínio](docs/DOMAIN_MODEL.md)
- [Arquitetura](docs/architecture.md)
