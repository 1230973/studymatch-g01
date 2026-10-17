import { useEffect, useState } from 'react'
import {
  getStudents,
  createStudent,
  updateStudent,
  getCourseUnits,
  getAcademicRecords,
  createAcademicRecord,
  getStudentProfile,
  getColdStartQuestions,
  getColdStartAnswers,
  saveColdStartAnswer,
} from './services/studentService'
import './App.css'

const initialForm = {
  studentNumber: '',
  name: '',
  institutionalEmail: '',
  enrolledYear: 1,
  degreeCourseCode: 'EI',
}

const initialGradeForm = {
  courseUnitCode: 'PROG',
  grade: '',
  academicYear: '2025/2026',
  semester: 1,
}

function App() {
  const [students, setStudents] = useState([])
  const [courseUnits, setCourseUnits] = useState([])
  const [questions, setQuestions] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [form, setForm] = useState(initialForm)

  // Funcionalidade Extra: Pesquisa e Filtragem de estudantes
  const [searchTerm, setSearchTerm] = useState('')
  const [courseFilter, setCourseFilter] = useState('ALL')
  const [yearFilter, setYearFilter] = useState('ALL')

  // Estudante selecionado (Histórico, Perfil, Cold Start e Edição)
  const [selectedStudent, setSelectedStudent] = useState(null)
  const [detailsLoading, setDetailsLoading] = useState(false)
  const [detailsError, setDetailsError] = useState('')
  const [detailsSuccess, setDetailsSuccess] = useState('')

  const [editForm, setEditForm] = useState({
    name: '',
    institutionalEmail: '',
    enrolledYear: 1,
    degreeCourseCode: 'EI',
  })
  const [savingEdit, setSavingEdit] = useState(false)

  const [academicRecords, setAcademicRecords] = useState([])
  const [gradeForm, setGradeForm] = useState(initialGradeForm)
  const [savingGrade, setSavingGrade] = useState(false)

  const [profile, setProfile] = useState(null)
  const [coldStartRatings, setColdStartRatings] = useState({})
  const [savingColdStart, setSavingColdStart] = useState(false)

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

  async function selectStudent(student) {
    setSelectedStudent(student)
    setEditForm({
      name: student.name,
      institutionalEmail: student.institutionalEmail,
      enrolledYear: student.enrolledYear,
      degreeCourseCode: student.degreeCourseCode,
    })
    setDetailsLoading(true)
    setDetailsError('')
    setDetailsSuccess('')

    try {
      const [recordsData, profileData, answersData] = await Promise.all([
        getAcademicRecords(student.studentNumber),
        getStudentProfile(student.studentNumber),
        getColdStartAnswers(student.studentNumber),
      ])

      setAcademicRecords(recordsData)
      setProfile(profileData)

      const ratingsMap = {}
      questions.forEach((q) => {
        ratingsMap[q.competencyCode] = 3
      })
      answersData.forEach((ans) => {
        ratingsMap[ans.competencyCode] = ans.rating
      })
      setColdStartRatings(ratingsMap)
    } catch (err) {
      setDetailsError(err.message)
    } finally {
      setDetailsLoading(false)
    }
  }

  useEffect(() => {
    let active = true

    Promise.all([
      getStudents(),
      getCourseUnits().catch(() => []),
      getColdStartQuestions().catch(() => []),
    ])
      .then(([studentsData, unitsData, questionsData]) => {
        if (active) {
          setStudents(studentsData)
          setCourseUnits(unitsData)
          setQuestions(questionsData)
          if (unitsData.length > 0) {
            setGradeForm((prev) => ({
              ...prev,
              courseUnitCode: unitsData[0].code,
            }))
          }
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

  function handleChange(event) {
    const { name, value } = event.target
    setForm((previous) => ({
      ...previous,
      [name]: value,
    }))
  }

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

  function handleEditChange(event) {
    const { name, value } = event.target
    setEditForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  async function handleUpdateStudent(event) {
    event.preventDefault()
    if (!selectedStudent) return

    setSavingEdit(true)
    setDetailsError('')
    setDetailsSuccess('')

    try {
      const updated = await updateStudent(selectedStudent.studentNumber, {
        ...editForm,
        enrolledYear: Number(editForm.enrolledYear),
      })
      setSelectedStudent(updated)
      setDetailsSuccess('Dados do estudante atualizados com sucesso!')
      await loadStudents()
    } catch (err) {
      setDetailsError(err.message)
    } finally {
      setSavingEdit(false)
    }
  }

  function handleGradeChange(event) {
    const { name, value } = event.target
    setGradeForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  async function handleAddGrade(event) {
    event.preventDefault()
    if (!selectedStudent) return

    setSavingGrade(true)
    setDetailsError('')
    setDetailsSuccess('')

    try {
      await createAcademicRecord(selectedStudent.studentNumber, {
        courseUnitCode: gradeForm.courseUnitCode,
        grade: Number(gradeForm.grade),
        academicYear: gradeForm.academicYear,
        semester: Number(gradeForm.semester),
      })

      const [recordsData, profileData] = await Promise.all([
        getAcademicRecords(selectedStudent.studentNumber),
        getStudentProfile(selectedStudent.studentNumber),
      ])

      setAcademicRecords(recordsData)
      setProfile(profileData)
      setGradeForm((prev) => ({ ...prev, grade: '' }))
      setDetailsSuccess('Classificação registada e perfil atualizado!')
    } catch (err) {
      setDetailsError(err.message)
    } finally {
      setSavingGrade(false)
    }
  }

  function handleRatingChange(competencyCode, value) {
    setColdStartRatings((prev) => ({
      ...prev,
      [competencyCode]: Number(value),
    }))
  }

  async function handleSaveColdStart(competencyCode) {
    if (!selectedStudent) return

    setSavingColdStart(true)
    setDetailsError('')
    setDetailsSuccess('')

    try {
      const rating = coldStartRatings[competencyCode] || 3
      await saveColdStartAnswer(selectedStudent.studentNumber, {
        competencyCode,
        rating: Number(rating),
      })

      const profileData = await getStudentProfile(selectedStudent.studentNumber)
      setProfile(profileData)
      setDetailsSuccess(`Autoavaliação (${competencyCode}) guardada com sucesso!`)
    } catch (err) {
      setDetailsError(err.message)
    } finally {
      setSavingColdStart(false)
    }
  }

  // Cursos e anos únicos para alimentar os filtros
  const availableCourses = Array.from(
    new Set(students.map((s) => s.degreeCourseCode))
  )
  const availableYears = Array.from(
    new Set(students.map((s) => s.enrolledYear))
  ).sort((a, b) => a - b)

  // Filtragem combinada de estudantes
  const filteredStudents = students.filter((student) => {
    const matchesSearch =
      student.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      student.studentNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      student.institutionalEmail.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesCourse =
      courseFilter === 'ALL' || student.degreeCourseCode === courseFilter

    const matchesYear =
      yearFilter === 'ALL' || student.enrolledYear === Number(yearFilter)

    return matchesSearch && matchesCourse && matchesYear
  })

  function formatSourceBadge(source) {
    if (source === 'ACADEMIC') return 'Evidência Académica'
    if (source === 'COLD_START') return 'Cold Start (Autoavaliação)'
    return 'Sem Dados (Desconhecido)'
  }

  return (
    <div className="app">
      <header className="header">
        <h1>StudyMatch</h1>
        <p>Formação inteligente de grupos académicos — Sprint 2</p>
      </header>

      <main className="container">
        <section className="welcome">
          <h2>Subsistema de Perfil do Estudante &amp; Cold Start</h2>
          <p>
            Selecione qualquer estudante da lista para consultar ou editar os
            seus dados, adicionar classificações ao histórico académico,
            visualizar o perfil de competências calculado em tempo real ou
            preencher o questionário inicial de Cold Start.
          </p>
        </section>

        <section className="students-section">
          <div className="section-header">
            <h2>Lista de Estudantes</h2>
            <button
              type="button"
              className="refresh-button"
              onClick={loadStudents}
              disabled={loading}
            >
              Atualizar lista
            </button>
          </div>

          {/* Funcionalidade Extra: Barra de Pesquisa e Filtros */}
          <div className="filters-bar">
            <div className="filter-group">
              <label htmlFor="searchFilter">Pesquisar (nome, número ou email)</label>
              <input
                id="searchFilter"
                type="text"
                placeholder="Ex: 2026001 ou João..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            <div className="filter-group">
              <label htmlFor="courseFilter">Filtrar por Curso</label>
              <select
                id="courseFilter"
                value={courseFilter}
                onChange={(e) => setCourseFilter(e.target.value)}
              >
                <option value="ALL">Todos os cursos</option>
                {availableCourses.map((code) => (
                  <option key={code} value={code}>
                    {code}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-group">
              <label htmlFor="yearFilter">Filtrar por Ano</label>
              <select
                id="yearFilter"
                value={yearFilter}
                onChange={(e) => setYearFilter(e.target.value)}
              >
                <option value="ALL">Todos os anos</option>
                {availableYears.map((year) => (
                  <option key={year} value={year}>
                    {year}.º Ano
                  </option>
                ))}
              </select>
            </div>
          </div>

          {loading && <p>A carregar estudantes...</p>}

          {error && (
            <p className="error-message" role="alert">
              {error}
            </p>
          )}

          {!loading && filteredStudents.length === 0 && !error && (
            <p>Não foram encontrados estudantes com os filtros selecionados.</p>
          )}

          {!loading && filteredStudents.length > 0 && (
            <div className="students-list">
              {filteredStudents.map((student) => {
                const isSelected =
                  selectedStudent?.studentNumber === student.studentNumber
                return (
                  <article
                    className={`student-card ${isSelected ? 'selected-card' : ''}`}
                    key={student.studentNumber}
                  >
                    <h3>{student.name}</h3>
                    <p>
                      <strong>Número:</strong> {student.studentNumber}
                    </p>
                    <p>
                      <strong>Email:</strong> {student.institutionalEmail}
                    </p>
                    <p>
                      <strong>Ano:</strong> {student.enrolledYear}.º Ano
                    </p>
                    <p>
                      <strong>Curso:</strong> {student.degreeCourseName} (
                      {student.degreeCourseCode})
                    </p>
                    <button
                      type="button"
                      className="select-student-btn"
                      onClick={() => selectStudent(student)}
                    >
                      {isSelected
                        ? 'Estudante Selecionado'
                        : 'Ver Perfil / Histórico / Cold Start'}
                    </button>
                  </article>
                )
              })}
            </div>
          )}
        </section>

        {/* Painel Completo do Estudante Selecionado */}
        {selectedStudent && (
          <section className="student-details-section">
            <div className="details-header">
              <h2>
                Gestão do Estudante: {selectedStudent.name} (
                {selectedStudent.studentNumber})
              </h2>
              <button
                type="button"
                className="close-button"
                onClick={() => setSelectedStudent(null)}
              >
                Fechar Painel
              </button>
            </div>

            {detailsLoading && <p>A carregar dados do estudante...</p>}

            {detailsError && (
              <p className="error-message" role="alert">
                {detailsError}
              </p>
            )}

            {detailsSuccess && (
              <p className="success-message" role="status">
                {detailsSuccess}
              </p>
            )}

            <div className="details-grid">
              {/* 1. Perfil de Competências */}
              <div className="details-panel">
                <h3>1. Perfil de Competências Calculado</h3>
                <p className="panel-hint">
                  As classificações académicas (Média × 5) têm prioridade sobre
                  a autoavaliação Cold Start ((Resposta - 1) × 25).
                </p>

                {profile && profile.competencies ? (
                  <div className="competencies-list">
                    {profile.competencies.map((comp) => (
                      <div key={comp.code} className="competency-item">
                        <div className="competency-top">
                          <strong>
                            {comp.name} ({comp.code})
                          </strong>
                          <span className={`badge badge-${comp.source.toLowerCase()}`}>
                            {formatSourceBadge(comp.source)}
                          </span>
                        </div>

                        <div className="progress-bar-bg">
                          <div
                            className="progress-bar-fill"
                            style={{
                              width: `${comp.score !== null ? comp.score : 0}%`,
                            }}
                          />
                        </div>

                        <div className="competency-meta">
                          <span>
                            <strong>Pontuação:</strong>{' '}
                            {comp.score !== null
                              ? `${comp.score} / 100`
                              : 'Sem pontuação (null)'}
                          </span>
                          <span>
                            <strong>Evidências académicas:</strong>{' '}
                            {comp.evidenceCount}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p>Perfil não disponível.</p>
                )}
              </div>

              {/* 2. Questionário Cold Start */}
              <div className="details-panel">
                <h3>2. Questionário Cold Start (Autoavaliação)</h3>
                <p className="panel-hint">
                  Para estudantes sem histórico numa competência, a autoavaliação
                  (1 a 5) gera uma pontuação provisória.
                </p>

                {questions.length === 0 ? (
                  <p>Não existem perguntas configuradas.</p>
                ) : (
                  <div className="cold-start-list">
                    {questions.map((q) => (
                      <div key={q.competencyCode} className="cold-start-item">
                        <label htmlFor={`cs-${q.competencyCode}`}>
                          <strong>{q.competencyCode}:</strong> {q.question}
                        </label>
                        <div className="cold-start-controls">
                          <select
                            id={`cs-${q.competencyCode}`}
                            value={coldStartRatings[q.competencyCode] || 3}
                            onChange={(e) =>
                              handleRatingChange(
                                q.competencyCode,
                                e.target.value
                              )
                            }
                          >
                            <option value={1}>1 — Nenhuma experiência (0 pts)</option>
                            <option value={2}>2 — Noções básicas (25 pts)</option>
                            <option value={3}>3 — Experiência intermédia (50 pts)</option>
                            <option value={4}>4 — Boa experiência (75 pts)</option>
                            <option value={5}>5 — Experiência avançada (100 pts)</option>
                          </select>
                          <button
                            type="button"
                            disabled={savingColdStart}
                            onClick={() => handleSaveColdStart(q.competencyCode)}
                          >
                            Guardar
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* 3. Histórico Académico */}
              <div className="details-panel">
                <h3>3. Histórico Académico</h3>

                {academicRecords.length === 0 ? (
                  <p>
                    Este estudante ainda não possui classificações registadas
                    (em estado Cold Start).
                  </p>
                ) : (
                  <table className="records-table">
                    <thead>
                      <tr>
                        <th>UC</th>
                        <th>Nota</th>
                        <th>Ano Letivo</th>
                        <th>Sem.</th>
                      </tr>
                    </thead>
                    <tbody>
                      {academicRecords.map((rec) => (
                        <tr key={rec.id}>
                          <td>
                            {rec.courseUnitName} ({rec.courseUnitCode})
                          </td>
                          <td>
                            <strong>{rec.grade}</strong>
                          </td>
                          <td>{rec.academicYear}</td>
                          <td>{rec.semester}.º</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}

                <form onSubmit={handleAddGrade} className="compact-form">
                  <h4>Registar Nova Classificação</h4>

                  <label htmlFor="courseUnitCode">Unidade Curricular</label>
                  <select
                    id="courseUnitCode"
                    name="courseUnitCode"
                    value={gradeForm.courseUnitCode}
                    onChange={handleGradeChange}
                    required
                  >
                    {courseUnits.length > 0 ? (
                      courseUnits.map((cu) => (
                        <option key={cu.code} value={cu.code}>
                          {cu.code} — {cu.name}
                        </option>
                      ))
                    ) : (
                      <>
                        <option value="PROG">PROG — Programacao</option>
                        <option value="BD">BD — Bases de Dados</option>
                        <option value="ES">ES — Engenharia de Software</option>
                      </>
                    )}
                  </select>

                  <label htmlFor="grade">Classificação (0 a 20)</label>
                  <input
                    id="grade"
                    type="number"
                    step="0.01"
                    min="0"
                    max="20"
                    name="grade"
                    value={gradeForm.grade}
                    onChange={handleGradeChange}
                    placeholder="Ex: 16"
                    required
                  />

                  <label htmlFor="academicYear">Ano Letivo (AAAA/AAAA)</label>
                  <input
                    id="academicYear"
                    type="text"
                    name="academicYear"
                    value={gradeForm.academicYear}
                    onChange={handleGradeChange}
                    placeholder="Ex: 2025/2026"
                    required
                  />

                  <label htmlFor="semester">Semestre</label>
                  <select
                    id="semester"
                    name="semester"
                    value={gradeForm.semester}
                    onChange={handleGradeChange}
                    required
                  >
                    <option value={1}>1.º Semestre</option>
                    <option value={2}>2.º Semestre</option>
                  </select>

                  <button type="submit" disabled={savingGrade}>
                    {savingGrade ? 'A adicionar...' : 'Adicionar Classificação'}
                  </button>
                </form>
              </div>

              {/* 4. Editar Dados do Estudante */}
              <div className="details-panel">
                <h3>4. Editar Dados do Estudante</h3>
                <form onSubmit={handleUpdateStudent} className="compact-form">
                  <label htmlFor="editName">Nome completo</label>
                  <input
                    id="editName"
                    type="text"
                    name="name"
                    value={editForm.name}
                    onChange={handleEditChange}
                    required
                    maxLength={150}
                  />

                  <label htmlFor="editEmail">Email institucional</label>
                  <input
                    id="editEmail"
                    type="email"
                    name="institutionalEmail"
                    value={editForm.institutionalEmail}
                    onChange={handleEditChange}
                    required
                    maxLength={200}
                  />

                  <label htmlFor="editYear">Ano de inscrição</label>
                  <input
                    id="editYear"
                    type="number"
                    name="enrolledYear"
                    min="1"
                    max="10"
                    value={editForm.enrolledYear}
                    onChange={handleEditChange}
                    required
                  />

                  <label htmlFor="editCourse">Código do curso</label>
                  <input
                    id="editCourse"
                    type="text"
                    name="degreeCourseCode"
                    value={editForm.degreeCourseCode}
                    onChange={handleEditChange}
                    required
                  />

                  <button type="submit" disabled={savingEdit}>
                    {savingEdit ? 'A atualizar...' : 'Guardar Alterações'}
                  </button>
                </form>
              </div>
            </div>
          </section>
        )}

        <section className="register-section">
          <h2>Registar Novo Estudante</h2>

          <form onSubmit={handleSubmit} className="student-form">
            <label htmlFor="studentNumber">Número de estudante</label>
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

            <label htmlFor="institutionalEmail">Email institucional</label>
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

            <label htmlFor="enrolledYear">Ano de inscrição</label>
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

            <label htmlFor="degreeCourseCode">Código do curso</label>
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
            <p className="success-message" role="status">
              {success}
            </p>
          )}
        </section>

        <section className="status">
          <h2>Integração Full-Stack — Sprint 2</h2>
          <p>Frontend: React + Vite (Pesquisa/Filtro, Histórico, Perfil e Cold Start)</p>
          <p>Backend: Spring Boot API REST (Java 17)</p>
          <p>Persistência: PostgreSQL + Spring Data JPA</p>
        </section>
      </main>

      <footer className="footer">
        <p>StudyMatch — Projeto de Qualidade de Software</p>
      </footer>
    </div>
  )
}

export default App