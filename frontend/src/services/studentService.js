
const API_URL = 'http://localhost:8080/api/students'

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
  const response = await fetch(API_URL)

  return handleResponse(response)
}

// Registar um novo estudante
export async function createStudent(student) {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(student),
  })

  return handleResponse(response)
}
