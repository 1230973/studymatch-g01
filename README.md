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
