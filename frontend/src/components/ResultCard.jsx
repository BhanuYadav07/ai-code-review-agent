import s from '../styles/ResultCard.module.css'

function scoreColor(n) {
  if (n >= 75) return s.green
  if (n >= 50) return s.amber
  return s.red
}

function complexityColor(c = '') {
  if (c.includes('1')) return s.green
  if (c.includes('log')) return s.blue
  if (c.includes('n²') || c.includes('n^2')) return s.red
  return s.amber
}

function severityColor(sev) {
  if (sev === 'high') return s.sevHigh
  if (sev === 'medium') return s.sevMed
  return s.sevLow
}

export default function ResultCard({ result }) {
  const { scores, complexity, bugs, suggestions, optimizedSnippet, unitTests } = result

  return (
    <div className={s.results}>

      {/* Scores */}
      <div className={s.card}>
        <div className={s.cardHead}><i className="ti ti-chart-bar" /> Review Summary</div>
        <div className={s.cardBody}>
          <div className={s.scoreGrid}>
            {[
              { label: 'Quality', val: scores?.quality },
              { label: 'Maintainability', val: scores?.maintainability },
              { label: 'Performance', val: scores?.performance },
            ].map(({ label, val }) => (
              <div key={label} className={s.scoreBox}>
                <div className={s.scoreLabel}>{label}</div>
                <div className={`${s.scoreVal} ${scoreColor(val)}`}>{val ?? '—'}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Complexity */}
      <div className={s.card}>
        <div className={s.cardHead}><i className="ti ti-chart-line" /> Complexity Analysis</div>
        <div className={s.cardBody}>
          <div className={s.badges}>
            <span className={`${s.cbadge} ${complexityColor(complexity?.time)}`}>
              Time: {complexity?.time}
            </span>
            <span className={`${s.cbadge} ${s.blue}`}>
              Space: {complexity?.space}
            </span>
          </div>
          <p className={s.explain}>{complexity?.explanation}</p>
        </div>
      </div>

      {/* Bugs */}
      <div className={s.card}>
        <div className={s.cardHead}>
          <i className="ti ti-bug" /> Issues Found
          <span className={s.count}>{bugs?.length ?? 0} issue{bugs?.length !== 1 ? 's' : ''}</span>
        </div>
        <div className={s.cardBody}>
          {!bugs?.length
            ? <span className={s.noIssues}><i className="ti ti-circle-check" /> No issues found</span>
            : bugs.map((b, i) => (
              <div key={i} className={s.bugRow}>
                <div className={`${s.severityBar} ${severityColor(b.severity)}`} />
                <div className={s.bugContent}>
                  <div className={s.bugLine}>{b.line}</div>
                  <div className={s.bugDesc}>{b.description}</div>
                </div>
                <span className={`${s.sevLabel} ${severityColor(b.severity)}`}>{b.severity}</span>
              </div>
            ))}
        </div>
      </div>

      {/* Suggestions */}
      <div className={s.card}>
        <div className={s.cardHead}><i className="ti ti-bulb" /> Optimization Suggestions</div>
        <div className={s.cardBody}>
          {suggestions?.map((sug, i) => (
            <div key={i} className={s.sugRow}>
              <div className={s.sugNum}>{i + 1}</div>
              <div>{sug}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Optimized snippet */}
      {optimizedSnippet && (
        <div className={s.card}>
          <div className={s.cardHead}><i className="ti ti-sparkles" /> Optimized Version</div>
          <div className={s.cardBody}>
            <pre className={s.code}>{optimizedSnippet}</pre>
          </div>
        </div>
      )}

      {/* Unit tests */}
      {unitTests && (
        <div className={s.card}>
          <div className={s.cardHead}><i className="ti ti-test-pipe" /> Generated Unit Tests</div>
          <div className={s.cardBody}>
            <pre className={s.code}>{unitTests}</pre>
          </div>
        </div>
      )}
    </div>
  )
}
