# StudyMatch

**Os Mestres do Java — Adaptive Student Group Formation — Software Quality Project**

O **StudyMatch** é uma aplicação full-stack de apoio à futura formação de grupos académicos, utilizando informação sobre os estudantes, o seu percurso académico e as competências desenvolvidas ao longo do curso.

No **Sprint 1**, a equipa estabeleceu a arquitetura e uma primeira integração funcional entre React, Spring Boot e PostgreSQL. No **Sprint 2 — Student Profile & Cold Start**, foram implementadas a gestão de dados dos estudantes, a representação do histórico académico, o cálculo do perfil de competências e uma estratégia inicial para estudantes sem classificações académicas (*Cold Start*). A interface permite consultar e alterar estes dados, bem como pesquisar e filtrar estudantes.

> **Âmbito:** a formação automática de grupos **não faz parte do Sprint 2**. O perfil de competências e o histórico académico servem de base a funcionalidades futuras de formação de grupos.

## 1. Tecnologias

| Componente | Tecnologia |
| --- | --- |
| Frontend | React + Vite |
| Backend | Java + Spring Boot |
| API | REST / HTTP / JSON |
| Persistência | PostgreSQL + Spring Data JPA |
| Base de dados local | Docker Compose |
| Build e testes do backend | Maven, JUnit 5 e Mockito |
| Build e análise estática do frontend | npm, Vite e ESLint |

## 2. Requisitos

- Git.
- Java JDK 17 ou superior, compatível com o projeto.
- Apache Maven disponível no `PATH`.
- Docker Desktop com o motor Docker em execução.
- Node.js e npm compatíveis com a versão do Vite utilizada no projeto.

## 3. Obter o projeto

```powershell
git clone https://github.com/1230973/studymatch-g01.git
cd studymatch-g01
```

Se o repositório já estiver clonado, confirmar primeiro que não existem alterações locais por guardar e atualizar a branch pretendida de acordo com o [workflow da equipa](WORKFLOW.md). Para executar a versão integrada do Sprint 2, utilizar a branch `main` atualizada.

## 4. Executar a aplicação completa

Os comandos seguintes partem da **raiz do repositório** (`studymatch-g01`). Utilizar terminais separados para a base de dados, o backend e o frontend.

### 4.1. Iniciar o PostgreSQL

Num terminal:

```powershell
cd backend
docker compose up -d
docker compose ps
```

Aguardar até o contentor `studymatch-postgres` apresentar o estado `healthy`. O serviço utiliza a imagem `postgres:17-alpine` e disponibiliza o PostgreSQL na porta **5432**. O volume Docker preserva os dados entre reinícios.

Também é possível iniciar o contentor através do Docker Desktop. Se a porta 5432 estiver ocupada por outro projeto, verificar qual o contentor responsável antes de o alterar.

### 4.2. Iniciar o backend

Noutro terminal, a partir da raiz do repositório:

```powershell
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

O backend fica disponível em **http://localhost:8080**. O perfil `demo` cria os dados de exemplo que ainda não existam: curso de Engenharia Informática (`EI`), estudante `2026001`, unidades curriculares e competências iniciais.

Verificar a API:

- Estado da aplicação: http://localhost:8080/api/health
- Estudantes: http://localhost:8080/api/students
- Unidades curriculares: http://localhost:8080/api/course-units
- Competências: http://localhost:8080/api/competencies

A resposta de `/api/health` deve indicar o estado da aplicação e da base de dados. Manter o terminal do Spring Boot aberto enquanto a aplicação estiver a ser utilizada. Se a porta 8080 estiver ocupada, confirmar se já existe outra instância do backend em execução.

### 4.3. Iniciar o frontend

Num terceiro terminal, a partir da raiz do repositório:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Abrir o endereço apresentado pelo Vite, normalmente **http://localhost:5173/**.

No Windows PowerShell, `npm.cmd` evita o bloqueio de execução de `npm.ps1` que pode ocorrer devido à política de execução de scripts. O frontend comunica com a API através de HTTP/JSON, usando os serviços definidos em `frontend/src/services/`.

### 4.4. Parar os serviços

- Parar o Spring Boot e o Vite com `Ctrl + C` nos respetivos terminais.
- Para parar a base de dados, a partir da pasta `backend`:

```powershell
docker compose down
```

O comando `docker compose down` **não elimina o volume de dados** do PostgreSQL. Não utilizar `-v` se pretender conservar os registos.

## 5. Sprint 1 — Fundação full-stack

O Sprint 1 teve como objetivo criar uma base técnica funcional e estabelecer o processo de desenvolvimento em equipa.

### 5.1. Arquitetura inicial

```text
Frontend React + Vite
        |
        | HTTP / JSON
        v
API REST Spring Boot
        |
        | Services / Repositories / Spring Data JPA
        v
PostgreSQL
```

### 5.2. Funcionalidades implementadas

- Registo de novos estudantes.
- Consulta da lista de estudantes no frontend e na API.
- Atualização da listagem no frontend após operações.
- Validação básica dos dados do formulário e apresentação de erros da API.
- Consulta e registo de cursos através da API.
- Persistência da informação no PostgreSQL, incluindo após reiniciar o backend.
- Estrutura inicial preparada para a evolução do projeto nos sprints seguintes.

### 5.3. Verificação da integração

1. Abrir `http://localhost:5173/`.
2. Consultar a lista de estudantes.
3. Criar um estudante com número e email ainda não utilizados.
4. Confirmar que aparece na interface e em `http://localhost:8080/api/students`.
5. Reiniciar o backend e confirmar que o estudante continua disponível.

## 6. Sprint 2 — Student Profile & Cold Start

O Sprint 2 acrescenta funcionalidades de negócio à fundação do Sprint 1. As funcionalidades abaixo foram integradas na aplicação full-stack e verificadas com testes automáticos e testes manuais da API e da interface.

### 6.1. Gestão dos estudantes

A aplicação permite:

- Criar e consultar estudantes.
- Editar informações relevantes de um estudante, incluindo nome, email, ano de inscrição e curso.
- Consultar os dados de um estudante numa área de gestão na interface React.
- Pesquisar estudantes pelo nome, número ou email.
- Filtrar a lista por curso e por ano de inscrição, incluindo a combinação de filtros.

A pesquisa e os filtros constituem a **funcionalidade adicional definida pela equipa** para o Sprint 2.

### 6.2. Histórico académico — Issue #13

O histórico académico regista as classificações dos estudantes ao longo do curso. Foram introduzidas as entidades:

**`CourseUnit`** — unidade curricular, com identificador interno, código único, nome e associação a uma competência.

**`AcademicRecord`** — registo académico, com identificador interno, estudante, unidade curricular, classificação (`grade`), ano letivo (`academicYear`) e semestre (`semester`).

As entidades `Student` e `DegreeCourse` são reutilizadas. O histórico é persistido com Spring Data JPA e PostgreSQL.

A interface React permite consultar o histórico de cada estudante e registar uma nova classificação.

**Regras principais:**

- Os campos obrigatórios devem ser fornecidos.
- Classificação entre **0 e 20**, inclusive.
- Semestre igual a **1 ou 2**.
- Ano letivo com formato `AAAA/AAAA`, por exemplo `2025/2026`. A validação atual verifica o formato, não a consecutividade dos anos.
- O estudante e a unidade curricular devem existir.
- Códigos das unidades curriculares são normalizados para maiúsculas na criação do registo.
- Pedidos inválidos, incluindo notas fora do intervalo permitido, são rejeitados com **HTTP 400**.

**Dados de demonstração:**

| Código | Unidade curricular |
| --- | --- |
| `PROG` | Programação |
| `BD` | Bases de Dados |
| `ES` | Engenharia de Software |

> Um pedido `POST` de registo académico cria uma nova classificação. Repetir o pedido pode originar mais de um registo; os registos são considerados na média do perfil.

### 6.3. Competências e perfil académico — Issue #15

O sistema representa três competências iniciais:

| Código | Competência | Unidade curricular de demonstração |
| --- | --- | --- |
| `PROGRAMMING` | Programação | `PROG` |
| `DATABASES` | Bases de Dados | `BD` |
| `SOFTWARE_ENGINEERING` | Engenharia de Software | `ES` |

Cada unidade curricular contribui para uma competência principal. O perfil é calculado a partir das classificações associadas a cada competência:

```text
Pontuação académica = média das classificações × 5
```

O resultado é apresentado numa escala de **0 a 100**. Por exemplo, uma classificação de `14/20` em Programação corresponde a **70/100** na competência `PROGRAMMING`.

Quando existem várias classificações para a mesma competência, utiliza-se a sua média aritmética, atribuindo o mesmo peso a cada registo.

O perfil é recalculado sempre que é consultado: adicionar uma classificação altera a pontuação apresentada sem ser necessário guardar um perfil calculado separadamente.

### 6.4. Estratégia Cold Start — Issue #17

Para estudantes com pouca ou nenhuma informação académica, foi implementado um **questionário de autoavaliação** com uma pergunta por competência. Cada resposta utiliza uma escala de **1 a 5**.

```text
Pontuação Cold Start = (resposta - 1) × 25
```

| Resposta | Pontuação inicial |
| --- | --- |
| 1 | 0/100 |
| 2 | 25/100 |
| 3 | 50/100 |
| 4 | 75/100 |
| 5 | 100/100 |

As respostas são persistidas na entidade `ColdStartAnswer`. Existe uma única resposta por estudante e competência: responder novamente atualiza o registo anterior.

**A prioridade das fontes é aplicada por competência:**

1. **`ACADEMIC`:** se existirem classificações académicas, estas determinam a pontuação.
2. **`COLD_START`:** se não existirem classificações nessa competência, mas existir autoavaliação, é utilizada a pontuação inicial.
3. **`UNKNOWN`:** se não existirem classificações nem autoavaliação, a pontuação é `null`.

No resultado do perfil, `source` identifica a origem da pontuação. O campo `evidenceCount` indica **o número de classificações académicas utilizadas**, pelo que é `0` quando a fonte é `COLD_START` ou `UNKNOWN`.

**Exemplo de transição:** um estudante responde `4/5` em Programação, obtendo `75/100` (`COLD_START`). Posteriormente, regista uma nota de `14/20` nessa unidade curricular. O perfil passa automaticamente a apresentar `70/100` (`ACADEMIC`), com uma evidência académica. A resposta de autoavaliação continua guardada, mas deixa de determinar a pontuação dessa competência.

Mais detalhes:

- [Perfil académico e regras de cálculo](backend/docs/student-profile.md)
- [Estratégia Cold Start](backend/docs/cold-start.md)

### 6.5. Funcionalidades disponíveis no frontend

Na interface React é possível:

- Listar e registar estudantes.
- Selecionar um estudante para consultar o seu painel de gestão.
- Editar e guardar os dados do estudante.
- Visualizar o histórico académico e registar classificações.
- Consultar as pontuações das competências, o número de evidências e a origem dos resultados.
- Responder às perguntas Cold Start e visualizar a atualização do perfil.
- Pesquisar e filtrar estudantes por curso e ano.

A apresentação do perfil distingue visualmente competências baseadas em evidência académica, autoavaliação e informação desconhecida.

## 7. Endpoints REST

### 7.1. Estudantes e cursos

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/health` | Estado da aplicação e da base de dados |
| `GET` | `/api/students` | Listar estudantes |
| `POST` | `/api/students` | Criar estudante |
| `GET` | `/api/students/{studentNumber}` | Consultar estudante |
| `PUT` | `/api/students/{studentNumber}` | Atualizar dados do estudante |
| `GET` | `/api/degree-courses` | Listar cursos |
| `POST` | `/api/degree-courses` | Criar curso |

### 7.2. Histórico académico

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/course-units` | Listar unidades curriculares |
| `GET` | `/api/students/{studentNumber}/academic-records` | Consultar histórico académico |
| `POST` | `/api/students/{studentNumber}/academic-records` | Registar classificação (HTTP 201 se válida) |

### 7.3. Competências, perfil e Cold Start

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/competencies` | Listar competências |
| `GET` | `/api/students/{studentNumber}/profile` | Consultar perfil de competências calculado |
| `GET` | `/api/cold-start/questions` | Consultar perguntas e intervalo de respostas |
| `GET` | `/api/students/{studentNumber}/cold-start` | Consultar respostas Cold Start |
| `POST` | `/api/students/{studentNumber}/cold-start` | Guardar ou atualizar resposta Cold Start |

### 7.4. Exemplo — Registar uma classificação

Com o backend a funcionar, executar **noutro terminal PowerShell**:

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

O exemplo cria um registo académico, desde que o estudante e a unidade curricular existam. Para consultar o histórico:

http://localhost:8080/api/students/2026001/academic-records

### 7.5. Exemplo — Guardar uma resposta Cold Start

```powershell
$body = @{
    competencyCode = "PROGRAMMING"
    rating = 4
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/students/2026001/cold-start" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

A resposta `4/5` produz uma pontuação inicial de `75/100`. Se existirem notas académicas para Programação, estas prevalecem no perfil.

Consultar o perfil em:

http://localhost:8080/api/students/2026001/profile

## 8. Compilar e executar os testes

### 8.1. Backend

A partir da raiz do repositório:

```powershell
cd backend
mvn clean test
mvn clean package
```

`mvn clean test` executa os testes automatizados. `mvn clean package` compila o projeto, volta a executar os testes e produz o artefacto `.jar` em `backend/target/`.

A suite utiliza **JUnit 5 e Mockito** e inclui testes de:

- Cenário de registo académico válido e classificações inválidas.
- Limites de classificação académica (`0` e `20`).
- Cálculo de competências a partir de uma nota e da média de vários registos.
- Competências sem histórico académico e estudante inexistente.
- Respostas Cold Start válidas e inválidas (`0` e `6`).
- Atualização de respostas sem duplicados.
- Prioridade das classificações académicas sobre o Cold Start.

Os testes unitários simulam Repositories onde apropriado. A persistência e a integração entre frontend, API e PostgreSQL foram também verificadas manualmente.

### 8.2. Frontend

A partir da raiz do repositório:

```powershell
cd frontend
npm.cmd install
npm.cmd run lint
npm.cmd run build
```

`npm.cmd run lint` executa a análise estática configurada no frontend. `npm.cmd run build` produz a versão de distribuição em `frontend/dist/`.

## 9. Organização do projeto

```text
studymatch-g01/
├── backend/
│   ├── docs/
│   │   ├── student-profile.md
│   │   └── cold-start.md
│   ├── src/
│   │   ├── main/java/pt/studymatch/backend/
│   │   │   ├── config/       # Configuração e dados de demonstração
│   │   │   ├── controller/   # Endpoints REST
│   │   │   ├── dto/          # Dados de entrada e saída da API
│   │   │   ├── exception/    # Exceções e tratamento de erros
│   │   │   ├── model/        # Entidades persistentes
│   │   │   ├── repository/   # Acesso à base de dados com JPA
│   │   │   └── service/      # Regras de negócio
│   │   └── test/java/pt/studymatch/backend/
│   ├── compose.yaml
│   ├── pom.xml
│   └── README.md
├── frontend/
│   ├── src/
│   │   ├── services/
│   │   └── App.jsx
│   └── package.json
├── docs/                     # Análise, domínio e arquitetura
├── WORKFLOW.md
└── README.md
```

## 10. Guião de demonstração do Sprint 2

Durante a Sprint Review, a equipa pode utilizar o seguinte guião:

1. **Estudante com histórico académico:** abrir um estudante, registar uma classificação (por exemplo `14/20` em Programação) e confirmar que o perfil apresenta `70/100`, com origem `ACADEMIC`.
2. **Estudante sem histórico:** abrir um estudante sem notas, responder `4/5` em Programação e confirmar a pontuação `75/100`, com origem `COLD_START`.
3. **Transição entre fontes:** adicionar uma nota à competência que tinha apenas autoavaliação e confirmar que a fonte passa a `ACADEMIC`.
4. **Funcionalidade adicional:** demonstrar a pesquisa por nome/número/email e os filtros combinados por curso e ano.
5. **Testes e build:** executar `mvn clean package` e demonstrar a execução dos testes; executar também a verificação do frontend.

> Os valores anteriores são exemplos. Os resultados reais dependem dos registos existentes no PostgreSQL. Para uma demonstração controlada, utilizar estudantes de teste e evitar adicionar classificações repetidas sem intenção.

## 11. Processo de desenvolvimento

O trabalho é organizado no GitHub segundo o fluxo:

```text
Issue → Branch → Implementação → Pull Request → Review → Merge
```

Cada Pull Request deve ser revisto por outro elemento da equipa antes de ser integrado na branch `main`.

- [Repositório do StudyMatch](https://github.com/1230973/studymatch-g01)
- [Issue #13 — Histórico académico](https://github.com/1230973/studymatch-g01/issues/13)
- [Issue #15 — Competências e perfil académico](https://github.com/1230973/studymatch-g01/issues/15)
- [Issue #17 — Estratégia Cold Start](https://github.com/1230973/studymatch-g01/issues/17)
- [Issue #21 — Documentação final do Sprint 2](https://github.com/1230973/studymatch-g01/issues/21)

## 12. Documentação adicional

- [Documentação técnica do backend](backend/README.md)
- [Regras de cálculo do perfil académico](backend/docs/student-profile.md)
- [Estratégia Cold Start](backend/docs/cold-start.md)
- [Workflow da equipa](WORKFLOW.md)
- [Casos de uso](docs/USE_CASES.md)
- [Modelo de domínio](docs/DOMAIN_MODEL.md)
- [Arquitetura](docs/architecture.md)
