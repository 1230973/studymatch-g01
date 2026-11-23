import { useEffect, useState } from 'react'
import { getMyGroups } from '../services/groupService'

function MyGroupsPanel() {
  const [groups, setGroups] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    getMyGroups()
      .then((data) => {
        if (active) {
          setGroups(Array.isArray(data) ? data : [])
        }
      })
      .catch((err) => {
        if (active && err.status !== 404) {
          setError(err.message)
        }
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [])

  return (
    <section className="groups-section">
      <h2>Os Meus Grupos Académicos</h2>
      <p className="panel-hint">
        Consulte os grupos em que foi integrado pelos seus docentes.
      </p>

      {loading && <p>A carregar os seus grupos...</p>}
      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}
      {!loading && !error && groups.length === 0 && (
        <p>Ainda não foi atribuído a nenhum grupo académico.</p>
      )}

      {!loading && groups.length > 0 && (
        <div className="generated-groups-grid">
          {groups.map((group) => (
            <article key={group.id} className="group-card">
              <div className="group-card-header">
                <h4>
                  {group.contextName ? `${group.contextName} — ` : ''}Grupo{' '}
                  {group.groupNumber}
                </h4>
                <span className="balance-badge">
                  Equilíbrio: {group.balanceScore ?? 'N/A'}
                </span>
              </div>
              {group.explanation && (
                <p className="group-explanation">{group.explanation}</p>
              )}
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
    </section>
  )
}

export default MyGroupsPanel
