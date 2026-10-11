
import './App.css'

function App() {
  return (
    <div className="app">
      <header className="header">
        <h1>StudyMatch</h1>
        <p>Formação inteligente de grupos académicos</p>
      </header>

      <main className="container">
        <section className="welcome">
          <h2>Bem-vindo ao StudyMatch</h2>
          <p>
            Uma plataforma que ajuda a formar grupos de estudantes
            com base no percurso académico, competências e
            preferências de colaboração.
          </p>
        </section>

        <section className="features">
          <h2>Funcionalidades do projeto</h2>

          <div className="cards">
            <article className="card">
              <h3>Estudantes</h3>
              <p>
                Consulta e gestão da informação académica
                dos estudantes.
              </p>
            </article>

            <article className="card">
              <h3>Competências</h3>
              <p>
                Representação das competências adquiridas
                ao longo do percurso académico.
              </p>
            </article>

            <article className="card">
              <h3>Cold Start</h3>
              <p>
                Criação de perfis provisórios através
                de dilemas situacionais.
              </p>
            </article>

            <article className="card">
              <h3>Disponibilidade</h3>
              <p>
                Registo da disponibilidade semanal e
                das preferências de ritmo de trabalho.
              </p>
            </article>

            <article className="card">
              <h3>Formação de Grupos</h3>
              <p>
                Formação futura de grupos considerando
                critérios académicos e operacionais.
              </p>
            </article>
          </div>
        </section>

        <section className="status">
          <h2>Estado da aplicação</h2>
          <p>Frontend React inicializado com sucesso.</p>
          <p>
            A integração com a API Spring Boot será
            realizada numa etapa posterior.
          </p>
        </section>
      </main>

      <footer className="footer">
        <p>StudyMatch — Projeto de Qualidade de Software</p>
      </footer>
    </div>
  )
}

export default App
