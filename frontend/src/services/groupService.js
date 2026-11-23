const BASE_URL = 'http://localhost:8080/api'
const CONTEXTS_URL = `${BASE_URL}/grouping-contexts`

async function apiFetch(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    credentials: 'include',
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...(options.headers || {}),
    },
  })

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

export async function getGroupingContexts() {
  return apiFetch(CONTEXTS_URL)
}

export async function createGroupingContext(payload) {
  return apiFetch(CONTEXTS_URL, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function getContextParticipants(contextId) {
  return apiFetch(`${CONTEXTS_URL}/${contextId}/participants`)
}

export async function updateContextParticipants(contextId, studentNumbers) {
  return apiFetch(`${CONTEXTS_URL}/${contextId}/participants`, {
    method: 'PUT',
    body: JSON.stringify({ studentNumbers }),
  })
}

export async function getContextGenerations(contextId) {
  return apiFetch(`${CONTEXTS_URL}/${contextId}/generations`)
}

export async function getGenerationDetails(contextId, generationId) {
  return apiFetch(`${CONTEXTS_URL}/${contextId}/generations/${generationId}`)
}

export async function generateGroups(contextId, strategy = 'BALANCED_COMPETENCIES') {
  return apiFetch(`${CONTEXTS_URL}/${contextId}/generations`, {
    method: 'POST',
    body: JSON.stringify({ strategy }),
  })
}

export async function getMyGroups() {
  return apiFetch(`${BASE_URL}/me/groups`)
}
