import { useState } from 'react'
import { submitReview } from '../services/api'
import ResultCard from './ResultCard'
import s from '../styles/ReviewPage.module.css'

const SAMPLES = {
  nested: `public int findDuplicate(int[] arr) {\n    for (int i = 0; i < arr.length; i++) {\n        for (int j = 0; j < arr.length; j++) {\n            if (i != j && arr[i] == arr[j]) return arr[i];\n        }\n    }\n    return -1;\n}`,
  nullcheck: `public String getUserName(User user) {\n    return user.getProfile().getName().toUpperCase();\n}`,
  string: `public String buildCsv(List<String> items) {\n    String result = "";\n    for (String item : items) {\n        result += item + ",";\n    }\n    return result;\n}`,
  sort: `public void bubbleSort(int[] arr) {\n    int n = arr.length;\n    for (int i = 0; i < n; i++)\n        for (int j = 0; j < n - i - 1; j++)\n            if (arr[j] > arr[j+1]) {\n                int t = arr[j]; arr[j] = arr[j+1]; arr[j+1] = t;\n            }\n}`
}

const TOOLS = [
  { id: 'analyzer', icon: 'ti-bug', label: 'Code Analyzer' },
  { id: 'complexity', icon: 'ti-chart-line', label: 'Complexity Checker' },
  { id: 'testgen', icon: 'ti-test-pipe', label: 'Test Generator' },
]

export default function ReviewPage() {
  const [code, setCode] = useState('')
  const [language, setLanguage] = useState('java')
  const [loading, setLoading] = useState(false)
  const [activeTool, setActiveTool] = useState(null)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  async function handleReview() {
    if (!code.trim()) return
    setLoading(true); setError(null); setResult(null)

    const tools = ['analyzer', 'complexity', 'testgen']
    for (let i = 0; i < tools.length; i++) {
      setActiveTool(tools[i])
      await new Promise(r => setTimeout(r, 500))
    }

    try {
      const data = await submitReview(code, language)
      setResult(data)
    } catch (e) {
      setError(e.response?.data?.message || e.message || 'Review failed')
    } finally {
      setLoading(false); setActiveTool(null)
    }
  }

  return (
    <div>
      {/* Header */}
      <div className={s.header}>
        <h1><i className="ti ti-robot" /> AI Code Review Agent</h1>
        <p>Paste your code → get bugs, complexity analysis, and unit tests</p>
      </div>

      {/* Agent tool badges */}
      <div className={s.tools}>
        {TOOLS.map(t => (
          <span key={t.id} className={`${s.badge} ${s[t.id]} ${activeTool === t.id ? s.pulse : ''}`}>
            <i className={`ti ${t.icon}`} /> {t.label}
          </span>
        ))}
      </div>

      {/* Samples */}
      <div className={s.samplesRow}>
        <span className={s.samplesLabel}>Samples:</span>
        {Object.keys(SAMPLES).map(k => (
          <button key={k} className={s.sampleBtn} onClick={() => setCode(SAMPLES[k])}>
            {k === 'nested' ? 'Nested loops' : k === 'nullcheck' ? 'Null pointer' : k === 'string' ? 'String concat' : 'Bubble sort'}
          </button>
        ))}
      </div>

      {/* Editor */}
      <div className={s.editorWrap}>
        <select className={s.langSelect} value={language} onChange={e => setLanguage(e.target.value)}>
          <option value="java">Java</option>
          <option value="python">Python</option>
          <option value="javascript">JavaScript</option>
        </select>
        <textarea
          className={s.editor}
          value={code}
          onChange={e => setCode(e.target.value)}
          placeholder="Paste your code here..."
          spellCheck={false}
        />
      </div>

      {/* Actions */}
      <div className={s.actions}>
        <button className={s.reviewBtn} onClick={handleReview} disabled={loading || !code.trim()}>
          {loading
            ? <><i className="ti ti-loader-2 ti-spin" /> Running agent...</>
            : <><i className="ti ti-player-play" /> Run Code Review</>}
        </button>
        <button className={s.clearBtn} onClick={() => { setCode(''); setResult(null); setError(null) }}>
          Clear
        </button>
      </div>

      {/* Loading steps */}
      {loading && (
        <div className={s.loadingCard}>
          {[
            { id: 'analyzer', label: '🔍 Running Code Analyzer — scanning for bugs and anti-patterns...' },
            { id: 'complexity', label: '📊 Running Complexity Checker — computing Big-O...' },
            { id: 'testgen', label: '🧪 Running Test Generator — writing JUnit tests...' },
          ].map(step => (
            <div key={step.id} className={`${s.step} ${activeTool === step.id ? s.stepActive : ''}`}>
              {activeTool === step.id
                ? <i className="ti ti-loader-2 ti-spin" />
                : <i className="ti ti-circle" />}
              {step.label}
            </div>
          ))}
        </div>
      )}

      {/* Error */}
      {error && (
        <div className={s.errorCard}>
          <i className="ti ti-alert-circle" /> {error}
        </div>
      )}

      {/* Results */}
      {result && <ResultCard result={result} />}
    </div>
  )
}
