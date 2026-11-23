import { useEffect, useState } from 'react'
import {
  getGroupingContexts,
  createGroupingContext,
  getContextParticipants,
  updateContextParticipants,
  getContextGenerations,
  getGenerationDetails,
  generateGroups,
} from '../services/groupService'

function computeGroupDistribution(n) {
  if (n < 4) return null
  for (let b = Math.floor(n / 5); b >= 0; b--) {
    const rem = n - 5 * b
    if (rem % 4 === 0) {
      return { groupsOf5: b, groupsOf4: rem / 4 }
    }
  }
  return null
}

function GroupFormationPanel({ students }) {
  const [contexts, setContexts] = useState([])
  const [selectedContextId, setSelectedContextId] = useState('')
  const [contextForm, setContextForm] = useState({ name: '', description: '' })

  const [selectedNumbers, setSelectedNumbers] = useState([])
  const [generations, setGenerations] = useState([])
  const [activeGeneration, setActiveGeneration] = useState(null)
  const [strategy, setStrategy] = useState('BALANCED_COMPETENCIES')

  const [loading, setLoading] = useState(false)
  const [savingContext, setSavingContext] = useState(false)
  const [savingParticipants, setSavingParticipants] = useState(false)
  const [generating, setGenerating] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    loadContexts()
  }, [])

  async function loadContexts() {
    setLoading(true)
    setError('')
    try {
      const data = await getGroupingContexts()
      setContexts(data)
      if (data.length > 0 && !selectedContextId) {
        await handleSelectContext(data[0].id)
      }
    } catch (err) {
      if (err.status !== 404) {
        setError(err.message)
      }
    } finally {
      setLoading(false)
    }
  }

  async function handleSelectContext(contextId) {
    setSelectedContextId(String(contextId))
    setError('')
    setSuccess('')
    setActiveGeneration(null)

    if (!contextId) {
      setSelectedNumbers([])
      setGenerations([])
      return
    }

    try {
      const [participantsData, generationsData] = await Promise.all([
        getContextParticipants(contextId),
        getContextGenerations(contextId),
      ])
      setSelectedNumbers(participantsData.map((p) => p.studentNumber))
      setGenerations(generationsData)

      if (generationsData.length > 0) {
        const latest = await getGenerationDetails(contextId, generationsData[0].id)
        setActiveGeneration(latest)
      }
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleCreateContext(event) {
    event.preventDefault()
    setSavingContext(true)
    setError('')
    setSuccess('')
    try {
      const created = await createGroupingContext({
        name: contextForm.name.trim(),
        description: contextForm.description.trim(),
      })
      setContextForm({ name: '', description: '' })
      setSuccess(`Contexto "${created.name}" criado com sucesso!`)
      const updatedList = await getGroupingContexts()
      setContexts(updatedList)
      await handleSelectContext(created.id)
    } catch (err) {
      setError(err.message)
    } finally {
      setSavingContext(false)
    }
  }

  function toggleStudent(studentNumber) {
    setSelectedNumbers((prev) =>
      prev.includes(studentNumber)
        ? prev.filter((n) => n !== studentNumber)
        : [...prev, studentNumber]
    )
  }

  function selectAllStudents() {
    setSelectedNumbers(students.map((s) => s.studentNumber))
  }

  function clearSelection() {
    setSelectedNumbers([])
  }

  async function handleSaveParticipants() {
    if (!selectedContextId) return
    setSavingParticipants(true)
    setError('')
    setSuccess('')
    try {
      const saved = await updateContextParticipants(selectedContextId, selectedNumbers)
      setSelectedNumbers(saved.map((p) => p.studentNumber))
      setSuccess(`${saved.length} participantes elegíveis guardados no contexto.`)
    } catch (err) {
      setError(err.message)
    } finally {
      setSavingParticipants(false)
    }
  }

  async function handleGenerateGroups() {
    if (!selectedContextId) return
    setGenerating(true)
    setError('')
    setSuccess('')
    try {
      await updateContextParticipants(selectedContextId, selectedNumbers)
      const generated = await generateGroups(selectedContextId, strategy)
      setActiveGeneration(generated)
      const gens = await getContextGenerations(selectedContextId)
      setGenerations(gens)
      setSuccess('Grupos gerados e persistidos com sucesso!')
    } catch (err) {
      setError(err.message)
    } finally {
      setGenerating(false)
    }
  }

  async function handleViewGeneration(generationId) {
    if (!selectedContextId) return
    setError('')
    try {
      const details = await getGenerationDetails(selectedContextId, generationId)
      setActiveGeneration(details)
    } catch (err) {
      setError(err.message)
    }
  }

  function handleExportCsv() {
    if (!activeGeneration || !activeGeneration.groups) return
    const rows = [['Grupo', 'Equilibrio', 'Numero_Estudante', 'Nome', 'Explicacao']]
    activeGeneration.groups.forEach((group) => {
      group.members.forEach((member) => {
        rows.push([
          `Grupo ${group.groupNumber}`,
          group.balanceScore ?? '',
          member.studentNumber,
          member.name,
          `"${(group.explanation || '').replaceAll('"', '""')}"`,
        ])
      })
    })
    const csvContent = rows.map((r) => r.join(',')).join('\n')
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `studymatch-grupos-gen-${activeGeneration.id}.csv`
    link.click()
    URL.revokeObjectURL(url)
  }

  const distribution = computeGroupDistribution(selectedNumbers.length)

  return (
    <section className="groups-section">
      <div className="section-header">
        <h2>Formação Inteligente de Grupos</h2>
        <button
          type="button"
          className="refresh-button"
          onClick={loadContexts}
          disabled={loading}
        >
          Atualizar Contextos
        </button>
      </div>

      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}
      {success && (
        <p className="success-message" role="status">
          {success}
        </p>
      )}

      <div className="details-grid">
        {/* 1. Criar / Selecionar Contexto */}
        <div className="details-panel">
          <h3>1. Contexto de Formação</h3>
          <p className="panel-hint">
            Crie uma atividade académica ou selecione um contexto existente.
          </p>

          <div className="compact-form">
            <label htmlFor="contextSelect">Contexto ativo</label>
            <select
              id="contextSelect"
              value={selectedContextId}
              onChange={(e) => handleSelectContext(e.target.value)}
            >
              <option value="">-- Selecione um contexto --</option>
              {contexts.map((ctx) => (
                <option key={ctx.id} value={ctx.id}>
                  #{ctx.id} — {ctx.name}
                </option>
              ))}
            </select>
          </div>

          <form onSubmit={handleCreateContext} className="compact-form">
            <h4>Novo Contexto</h4>
            <label htmlFor="ctxName">Nome da atividade</label>
            <input
              id="ctxName"
              type="text"
              value={contextForm.name}
              onChange={(e) =>
                setContextForm((prev) => ({ ...prev, name: e.target.value }))
              }
              placeholder="Ex: Projeto Engenharia de Software"
              maxLength={150}
              required
            />

            <label htmlFor="ctxDesc">Descrição (opcional)</label>
            <input
              id="ctxDesc"
              type="text"
              value={contextForm.description}
              onChange={(e) =>
                setContextForm((prev) => ({ ...prev, description: e.target.value }))
              }
              placeholder="Ex: Grupos de 4/5 alunos equilibrados"
              maxLength={1000}
            />

            <button type="submit" disabled={savingContext}>
              {savingContext ? 'A criar...' : 'Criar Contexto'}
            </button>
          </form>
        </div>

        {/* 2. Selecionar Elegíveis + Simulador de Distribuição */}
        <div className="details-panel">
          <h3>2. Estudantes Elegíveis ({selectedNumbers.length})</h3>
          <p className="panel-hint">
            Selecione os participantes. Os grupos terão 4 ou 5 elementos sempre que possível.
          </p>

          <div className="participants-actions">
            <button
              type="button"
              className="secondary-btn"
              onClick={selectAllStudents}
            >
              Selecionar Todos
            </button>
            <button
              type="button"
              className="secondary-btn"
              onClick={clearSelection}
            >
              Limpar Seleção
            </button>
          </div>

          <div className="participants-checklist">
            {students.map((student) => (
              <label key={student.studentNumber} className="participant-checkbox">
                <input
                  type="checkbox"
                  checked={selectedNumbers.includes(student.studentNumber)}
                  onChange={() => toggleStudent(student.studentNumber)}
                />
                <span>
                  <strong>{student.studentNumber}</strong> — {student.name} ({student.degreeCourseCode})
                </span>
              </label>
            ))}
          </div>

          <div className={`distribution-preview ${distribution ? 'valid' : 'invalid'}`}>
            {selectedNumbers.length === 0 && (
              <span>Selecione estudantes para prever a distribuição.</span>
            )}
            {selectedNumbers.length > 0 && distribution && (
              <span>
                Distribuição prevista: <strong>{distribution.groupsOf5}</strong> grupo(s) de 5 +{' '}
                <strong>{distribution.groupsOf4}</strong> grupo(s) de 4.
              </span>
            )}
            {selectedNumbers.length > 0 && !distribution && (
              <span>
                Atenção: com {selectedNumbers.length} estudante(s) não é possível formar apenas grupos de 4 e 5.
              </span>
            )}
          </div>

          {selectedContextId && (
            <div className="generation-controls">
              <label htmlFor="strategySelect">Estratégia de Matching</label>
              <select
                id="strategySelect"
                value={strategy}
                onChange={(e) => setStrategy(e.target.value)}
              >
                <option value="BALANCED_COMPETENCIES">
                  Equilíbrio de Competências
                </option>
                <option value="COMPLEMENTARY_SKILLS">
                  Complementaridade de Competências
                </option>
              </select>

              <div className="generation-buttons">
                <button
                  type="button"
                  className="secondary-btn"
                  onClick={handleSaveParticipants}
                  disabled={savingParticipants}
                >
                  {savingParticipants ? 'A guardar...' : 'Guardar Elegíveis'}
                </button>
                <button
                  type="button"
                  className="primary-btn"
                  onClick={handleGenerateGroups}
                  disabled={generating || selectedNumbers.length === 0}
                >
                  {generating ? 'A gerar grupos...' : 'Gerar Grupos'}
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* 3. Histórico de Gerações e Resultados */}
      {selectedContextId && (
        <div className="generations-results">
          <div className="section-header">
            <h3>3. Grupos Gerados e Histórico</h3>
            {activeGeneration && (
              <button
                type="button"
                className="secondary-btn"
                onClick={handleExportCsv}
              >
                Exportar CSV
              </button>
            )}
          </div>

          {generations.length > 0 && (
            <div className="generations-history-bar">
              <span>Gerações anteriores:</span>
              {generations.map((gen) => (
                <button
                  key={gen.id}
                  type="button"
                  className={`history-pill ${activeGeneration?.id === gen.id ? 'active' : ''}`}
                  onClick={() => handleViewGeneration(gen.id)}
                >
                  #{gen.id} ({gen.groupCount} grupos)
                </button>
              ))}
            </div>
          )}

          {!activeGeneration ? (
            <p className="panel-hint">
              Ainda não existem grupos gerados para este contexto.
            </p>
          ) : (
            <div className="generated-groups-grid">
              {activeGeneration.groups.map((group) => (
                <article key={group.id} className="group-card">
                  <div className="group-card-header">
                    <h4>Grupo {group.groupNumber}</h4>
                    <span className="balance-badge">
                      Equilíbrio: {group.balanceScore ?? 'N/A'}
                    </span>
                  </div>
                  <p className="group-explanation">
                    {group.explanation || 'Distribuição equilibrada por competências.'}
                  </p>
                  <ul className="group-members-list">
                    {group.members.map((m) => (
                      <li key={m.studentNumber}>
                        <strong>{m.studentNumber}</strong> — {m.name}
                      </li>
                    ))}
                  </ul>
                </article>
              ))}
            </div>
          )}
        </div>
      )}
    </section>
  )
}

export default GroupFormationPanel
