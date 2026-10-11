# StudyMatch — Backend (Sprint 1, Issue #5)

Backend Java 17 / Spring Boot 3.5.16 / Maven / PostgreSQL. É um **esqueleto executável**
com API REST para cursos e estudantes, adequado à demonstração técnica do Sprint 1.
Não implementa ainda o matching, o percurso académico completo nem UC02-UC05.

## 1. Requisitos

- JDK 17 ou superior (`java -version`)
- Maven 3.6.3 ou superior (`mvn -version`)
- Docker Desktop com `docker compose` (recomendado) **ou** PostgreSQL instalado localmente

## 2. Iniciar a base de dados

Abrir o terminal na pasta `studymatch-g01/backend` e executar:

```powershell
docker compose up -d
docker compose ps
```

O Docker cria a BD `studymatch` no `localhost:5432`, com utilizador `studymatch`
e password `studymatch_dev` **apenas para desenvolvimento local**.
Se a porta 5432 já estiver ocupada, altera o mapeamento em `compose.yaml`
(ex.: `5433:5432`) e define `DB_URL=jdbc:postgresql://localhost:5433/studymatch`.

Para parar sem apagar os dados:

```powershell
docker compose down
```

**Não** executar `docker compose down -v` se quiseres manter os dados de teste.

Alternativa sem Docker: cria a base de dados `studymatch` no PostgreSQL instalado
localmente e configura `DB_URL`, `DB_USER` e `DB_PASSWORD`.

## 3. Executar o backend

No terminal dentro de `backend/`:

```powershell
mvn spring-boot:run
```

API no endereço <http://localhost:8080>. Para iniciar com um curso e aluno de exemplo:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

A configuração `demo` só insere os registos se ainda não existirem.
O schema é gerado automaticamente pelo Hibernate para **desenvolvimento** (`ddl-auto: update`).

## 4. Testar API (PowerShell)

Verificar servidor **e ligação real à base de dados**:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

Criar curso (se não tiver iniciado o perfil `demo`):

```powershell
$curso = @{ code='EI'; name='Engenharia Informatica'; totalCredits=180 } | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8080/api/degree-courses -Method Post -ContentType 'application/json' -Body $curso
```

Criar estudante:

```powershell
$aluno = @{
  studentNumber='2026002'
  name='Maria Exemplo'
  institutionalEmail='maria@example.org'
  enrolledYear=1
  degreeCourseCode='EI'
} | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8080/api/students -Method Post -ContentType 'application/json' -Body $aluno
```

Consultar estudantes persistidos:

```powershell
Invoke-RestMethod http://localhost:8080/api/students
Invoke-RestMethod http://localhost:8080/api/students/2026002
```

O navegador também abre os endpoints GET, nomeadamente `http://localhost:8080/api/students`.

## 5. Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/health` | Saúde da API e verificação de acesso à BD |
| GET | `/api/degree-courses` | Listar cursos |
| POST | `/api/degree-courses` | Criar curso (code, name, totalCredits) |
| GET | `/api/students` | Listar estudantes |
| GET | `/api/students/{studentNumber}` | Consultar estudante |
| POST | `/api/students` | Criar estudante com courseCode existente |

Atenção: no POST do estudante, o campo JSON chama-se **`degreeCourseCode`**.
Validações devolvem HTTP `400`, duplicados `409` e entidades não encontradas `404`.
O frontend React comunica via HTTP/JSON com estes endpoints. CORS está configurado
para `http://localhost:5173` e `http://127.0.0.1:5173`.

## 6. Testes e build

Os testes são executados com uma base H2 **apenas no ambiente de testes**, sem
depender de Docker/PostgreSQL local:

```powershell
mvn test
mvn clean package
```

O `mvn clean package` cria um JAR em `target/` que pode ser executado com `java -jar`.
Para demonstrar persistência real de Sprint 1, **executa a API normal com PostgreSQL**,
não o perfil de testes H2.

## 7. Variáveis de ambiente

| Variável | Predefinição local | Uso |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/studymatch` | JDBC PostgreSQL |
| `DB_USER` | `studymatch` | Utilizador BD |
| `DB_PASSWORD` | `studymatch_dev` | Palavra-passe local |
| `SERVER_PORT` | `8080` | Porta HTTP |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | Frontends permitidos |

Nunca colocar passwords de produção no GitHub. A password no Compose é apenas um exemplo local.

## 8. Organização

- `controller/`: pedidos e respostas HTTP
- `service/`: validações e regras de negócio
- `repository/`: comunicação JPA com PostgreSQL
- `model/`: entidades Student e DegreeCourse alinhadas com o domínio
- `dto/`: contratos JSON da API
- `exception/`: respostas de erro consistentes
- `config/`: CORS e dados facultativos de demonstração

## 9. Continuação (Issue #7)

- Integrar o React com `GET /api/students` e, se necessário, `POST /api/students`.
- Criar no frontend um `src/services/studentService.js` para centralizar chamadas HTTP.
- Demonstrar no navegador a leitura de um aluno que está **no PostgreSQL**.
- Atualizar README principal quando backend e frontend estiverem integrados.

A configuração apresentada é apropriada apenas ao desenvolvimento do Sprint 1:
ainda não inclui autenticação, migrações Flyway nem toda a lógica de matching.
