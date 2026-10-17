const BASE_URL = 'http://localhost:8080/api'
const STUDENTS_URL = `${BASE_URL}/students`

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

    throw new Error(message)
  }

  return response.json()
}

// Consultar todos os estudantes
export async function getStudents() {
  const response = await fetch(STUDENTS_URL)
  return handleResponse(response)
}

// Registar um novo estudante
export async function createStudent(student) {
  const response = await fetch(STUDENTS_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(student),
  })
  return handleResponse(response)
}

// Atualizar dados de um estudante existente
export async function updateStudent(studentNumber, studentData) {
  const response = await fetch(`${STUDENTS_URL}/${encodeURIComponent(studentNumber)}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(studentData),
  })
  return handleResponse(response)
}

// Listar unidades curriculares disponíveis
export async function getCourseUnits() {
  const response = await fetch(`${BASE_URL}/course-units`)
  return handleResponse(response)
}

// Consultar histórico académico de um estudante
export async function getAcademicRecords(studentNumber) {
  const response = await fetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/academic-records`
  )
  return handleResponse(response)
}

// Registar uma nova classificação no histórico académico
export async function createAcademicRecord(studentNumber, record) {
  const response = await fetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/academic-records`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(record),
    }
  )
  return handleResponse(response)
}

// Consultar perfil de competências calculado do estudante
export async function getStudentProfile(studentNumber) {
  const response = await fetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/profile`
  )
  return handleResponse(response)
}

// Consultar perguntas do questionário Cold Start
export async function getColdStartQuestions() {
  const response = await fetch(`${BASE_URL}/cold-start/questions`)
  return handleResponse(response)
}

// Consultar respostas Cold Start já guardadas para um estudante
export async function getColdStartAnswers(studentNumber) {
  const response = await fetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/cold-start`
  )
  return handleResponse(response)
}

// Guardar ou atualizar uma resposta Cold Start de um estudante
export async function saveColdStartAnswer(studentNumber, answer) {
  const response = await fetch(
    `${STUDENTS_URL}/${encodeURIComponent(studentNumber)}/cold-start`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(answer),
    }
  )
  return handleResponse(response)
}