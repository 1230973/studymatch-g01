
import { useEffect, useState } from 'react'
import { getStudents, createStudent } from './services/studentService'
import './App.css'

const initialForm = {
  studentNumber: '',
  name: '',
  institutionalEmail: '',
  enrolledYear: 1,
  degreeCourseCode: 'EI',
}

function App() {
  const [students, setStudents] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [form, setForm] = useState(initialForm)

  // Consultar estudantes através da API
  async function loadStudents() {
    setLoading(true)
    setError('')

    try {
      const data = await getStudents()
      setStudents(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  // Consultar estudantes quando a página abre
  useEffect(() => {
    let active = true

    getStudents()
      .then((data) => {
        if (active) {
          setStudents(data)
        }
      })
      .catch((err) => {
        if (active) {
          setError(err.message)
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false)
        }
      })

    return () => {
      active = false
    }
  }, [])

  // Atualizar campos do formulário
  function handleChange(event) {
    const { name, value } = event.target

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }))
  }

  // Registar estudante na base de dados
  async function handleSubmit(event) {
    event.preventDefault()

    setSaving(true)
    setError('')
    setSuccess('')

    try {
      await createStudent({
        ...form,
        enrolledYear: Number(form.enrolledYear),
      })

      setSuccess('Estudante registado com sucesso!')
      setForm(initialForm)

      await loadStudents()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

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
            Uma plataforma que apoia a formação de grupos
            académicos através do percurso escolar,
            competências e preferências dos estudantes.
          </p>
        </section>

        <section className="features">
          <h2>Funcionalidades do projeto</h2>

          <div className="cards">
            <article className="card">
              <h3>Estudantes</h3>
              <p>Consulta e gestão dos estudantes.</p>
            </article>

            <article className="card">
              <h3>Competências</h3>
              <p>Representação das competências académicas.</p>
            </article>

            <article className="card">
              <h3>Cold Start</h3>
              <p>Criação de perfis através de dilemas situacionais.</p>
            </article>

            <article className="card">
              <h3>Disponibilidade</h3>
              <p>Gestão dos horários e ritmos de trabalho.</p>
            </article>

            <article className="card">
              <h3>Formação de Grupos</h3>
              <p>Futura formação automática de grupos.</p>
            </article>
          </div>
        </section>

        <section className="students-section">
          <h2>Lista de Estudantes</h2>

          <button
            type="button"
            className="refresh-button"
            onClick={loadStudents}
            disabled={loading}
          >
            Atualizar lista
          </button>

          {loading && <p>A carregar estudantes...</p>}

          {error && (
            <p className="error-message" role="alert">{error}</p>
          )}

          {!loading && students.length === 0 && !error && (
            <p>Não existem estudantes registados.</p>
          )}

          {!loading && students.length > 0 && (
            <div className="students-list">
              {students.map((student) => (
                <article
                  className="student-card"
                  key={student.studentNumber}
                >
                  <h3>{student.name}</h3>
                  <p><strong>Número:</strong> {student.studentNumber}</p>
                  <p><strong>Email:</strong> {student.institutionalEmail}</p>
                  <p><strong>Ano:</strong> {student.enrolledYear}</p>
                  <p>
                    <strong>Curso:</strong> {student.degreeCourseName}
                  </p>
                </article>
              ))}
            </div>
          )}
        </section>

        <section className="register-section">
          <h2>Registar Estudante</h2>

          <form onSubmit={handleSubmit} className="student-form">

            <label htmlFor="studentNumber">
              Número de estudante
            </label>
            <input
              id="studentNumber"
              type="text"
              name="studentNumber"
              value={form.studentNumber}
              onChange={handleChange}
              required
              maxLength={30}
              placeholder="Ex: 2026002"
            />

            <label htmlFor="name">Nome completo</label>
            <input
              id="name"
              type="text"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
              maxLength={150}
              placeholder="Ex: João Silva"
            />

            <label htmlFor="institutionalEmail">
              Email institucional
            </label>
            <input
              id="institutionalEmail"
              type="email"
              name="institutionalEmail"
              value={form.institutionalEmail}
              onChange={handleChange}
              required
              maxLength={200}
              placeholder="Ex: joao@example.org"
            />

            <label htmlFor="enrolledYear">
              Ano de inscrição
            </label>
            <input
              id="enrolledYear"
              type="number"
              name="enrolledYear"
              min="1"
              max="10"
              value={form.enrolledYear}
              onChange={handleChange}
              required
            />

            <label htmlFor="degreeCourseCode">
              Código do curso
            </label>
            <input
              id="degreeCourseCode"
              type="text"
              name="degreeCourseCode"
              value={form.degreeCourseCode}
              onChange={handleChange}
              required
              placeholder="EI"
            />

            <button type="submit" disabled={saving}>
              {saving ? 'A guardar...' : 'Registar estudante'}
            </button>
          </form>

          {success && (
            <p className="success-message" role="status">{success}</p>
          )}
        </section>

        <section className="status">
          <h2>Integração Técnica — Sprint 1</h2>
          <p>Frontend: React + Vite</p>
          <p>Backend: Spring Boot</p>
          <p>Base de dados: PostgreSQL</p>
          <p>
            A consulta e o registo de estudantes utilizam
            a API REST do StudyMatch.
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
