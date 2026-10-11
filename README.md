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

