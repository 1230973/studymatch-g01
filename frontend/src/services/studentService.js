const BASE_URL = 'http://localhost:8080/api'
const STUDENTS_URL = `${BASE_URL}/students`

// Função auxiliar para pedidos com sessão (cookies HttpOnly)
async function apiFetch(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    credentials: 'include',
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...(options.headers || {}),
    },
  })
  return handleResponse(response)
}

// Função que trata as respostas da API
async function handleResponse(response) {
  if (!response.ok) {
    let message = 'Ocorreu um erro ao comunicar com o servidor.'

    try {
      const error = await response.json()
      message = error.error || error.message || message
    } catch {
      // Mantém a mensagem predefinida
    }

    const err = new Error(message)
    err.status = response.status
    throw err
  }

  return response.json()
}

// Autenticação e Sessão
export async function login(username, password) {
  return apiFetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
}

export async function logout() {
  return apiFetch(`${BASE_URL}/auth/logout`, {
    method: 'POST',
  })
}

export async function getCurrentUser() {
  return apiFetch(`${BASE_URL}/auth/me`)
}

// Consultar todos os estudantes (Apenas Professor)
export async function getStudents() {
  return apiFetch(STUDENTS_URL)
}

// Consultar um estudante específico (Professor ou o próprio Aluno)
export async function getStudent(studentNumber) {
  return apiFetch(`${STUDENTS_URL}/${encodeURIComponent(studentNumber)}`)
}

// Registar um novo estudante (Apenas Professor)
export async function createStudent(student) {
  return apiFetch(STUDENTS_URL, {
    method: 'POST',
    body: JSON.stringify(student),
  })
}

// Atualizar dados de um estudante existente (Apenas Professor)
export async function updateStudent(studentNumber, studentData) {
  return apiFetch(`${STUDENTS_URL}/${encodeURIComponent(studentNumber)}`, {
    method: 'PUT',
    body: JSON.stringify(studentData),
  })
}

// Listar unidades curriculares disponíveis
export async function getCourseUnits() {
  return apiFetch(`${BASE_URL}/course-units`)
}

// Consultar histórico académico de um estudante
export async function getAcademicRecords(studentNumber) {
  return apiFetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/academic-records`
  )
}

// Registar uma nova classificação no histórico académico (Apenas Professor)
export async function createAcademicRecord(studentNumber, record) {
  return apiFetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/academic-records`,
    {
      method: 'POST',
      body: JSON.stringify(record),
    }
  )
}

// Consultar perfil de competências calculado do estudante
export async function getStudentProfile(studentNumber) {
  return apiFetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/profile`
  )
}

// Consultar perguntas do questionário Cold Start
export async function getColdStartQuestions() {
  return apiFetch(`${BASE_URL}/cold-start/questions`)
}

// Consultar respostas Cold Start já guardadas para um estudante
export async function getColdStartAnswers(studentNumber) {
  return apiFetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/cold-start`
  )
}

// Guardar ou atualizar uma resposta Cold Start de um estudante
export async function saveColdStartAnswer(studentNumber, answer) {
  return apiFetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/cold-start`,
    {
      method: 'POST',
      body: JSON.stringify(answer),
    }
  )
}