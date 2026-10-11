# studymatch
Os Mestres do Java - Adaptive Student Group Formation - Software Quality Project 

## Frontend — React + Vite

O frontend do StudyMatch foi desenvolvido com React e Vite.

### Requisitos

- Node.js (versão compatível com o Vite utilizado)
- npm
- Git

### Instalação

Clonar o repositório:

```powershell
git clone https://github.com/1230973/studymatch-g01.git
```

Entrar na pasta do frontend:

```powershell
cd studymatch-g01/frontend
```

Instalar as dependências:

```powershell
npm.cmd install
```

### Executar a aplicação

Iniciar o servidor de desenvolvimento:

```powershell
npm.cmd run dev
```

Abrir no navegador o endereço apresentado pelo Vite, normalmente:

http://localhost:5173/

### Testar o frontend

Compilar a aplicação:

```powershell
npm.cmd run build
```

Executar a análise estática com ESLint:

```powershell
npm.cmd run lint
```

### Estado atual

O frontend apresenta a interface inicial do StudyMatch, incluindo as áreas de estudantes, competências, Cold Start, disponibilidade e formação de grupos.

Nesta fase, estas áreas são apenas informativas.

A ligação à API Spring Boot e à base de dados PostgreSQL será desenvolvida na Issue #7 (Vertical Slice).



## Backend — Spring Boot + PostgreSQL

O backend do StudyMatch foi desenvolvido em Java, utilizando Spring Boot, Maven e PostgreSQL.

### Requisitos

- Java JDK 17 ou superior
- Apache Maven
- Docker Desktop

### Iniciar a base de dados

Na pasta principal do projeto, executar:

```powershell
cd backend
docker compose up -d
```

Verificar o estado do PostgreSQL:

```powershell
docker compose ps
```

### Executar o backend

Na pasta `backend`, executar:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

O backend ficará disponível em:

http://localhost:8080

### Testar a API

Verificar a ligação à base de dados:

http://localhost:8080/api/health

Consultar estudantes:

http://localhost:8080/api/students

Consultar cursos:

http://localhost:8080/api/degree-courses

### Executar testes automatizados

Na pasta `backend`, executar:

```powershell
mvn clean test
```

### Parar a base de dados

```powershell
docker compose down
```

Este comando não elimina os dados armazenados no volume Docker.

### Estado atual

O backend permite consultar e registar estudantes e cursos através de uma API REST ligada ao PostgreSQL.

A integração com o frontend React será realizada na Issue #7 (Vertical Slice).

Para mais informações, consultar [Documentação do Backend](backend/README.md).



## Integração Full-Stack — Sprint 1 (Issue #7)

O StudyMatch utiliza uma arquitetura full-stack composta por:

- Frontend: React + Vite
- Backend: Java com Spring Boot
- Base de dados: PostgreSQL
- Gestão de dependências: npm e Maven
- Contentores: Docker Compose

### 1. Iniciar o PostgreSQL

Na pasta principal do projeto:

```powershell
cd backend
docker compose up -d
```

Verificar se a base de dados está operacional:

```powershell
docker compose ps
```

O contentor deverá apresentar o estado healthy.

### 2. Iniciar o backend

Na pasta backend, executar:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

O backend ficará disponível em:

http://localhost:8080

Endpoints para verificação:

- GET /api/health
- GET /api/students
- POST /api/students
- GET /api/degree-courses

### 3. Iniciar o frontend

Abrir um novo terminal na pasta principal do projeto.

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Abrir no navegador:

http://localhost:5173/

### 4. Testar a integração

Na interface StudyMatch:

1. Consultar a lista de estudantes.
2. Preencher o formulário de registo.
3. Registar um novo estudante.
4. Confirmar que aparece na lista.
5. Consultar /api/students para verificar os dados.
6. Reiniciar o backend e confirmar que o estudante continua registado.

Os dados são armazenados no PostgreSQL e consultados através da API REST.

### 5. Executar os testes

Frontend:

```powershell
cd frontend
npm.cmd run lint
npm.cmd run build
```

Backend:

```powershell
cd backend
mvn clean test
```

Executar os comandos a partir da raiz do projeto em terminais separados.

### 6. Arquitetura da integração

React + Vite
    |
    | HTTP / JSON
    v
Spring Boot REST API
    |
    | Spring Data JPA
    v
PostgreSQL

A lógica de comunicação HTTP do frontend encontra-se no ficheiro:

frontend/src/services/studentService.js

Este ficheiro centraliza os pedidos à API, mantendo a separação de responsabilidades.

### 7. Funcionalidades implementadas

- Consulta de estudantes na base de dados.
- Registo de novos estudantes.
- Atualização da lista de estudantes.
- Validação básica do formulário.
- Tratamento de erros da API.
- Persistência de dados no PostgreSQL.

A formação automática de grupos e as restantes funcionalidades avançadas ficam previstas para Sprints posteriores.
