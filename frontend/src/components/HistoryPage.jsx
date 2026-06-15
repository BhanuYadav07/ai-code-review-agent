import { useState, useEffect } from 'react'
import { getAllReviews, deleteReview } from '../services/api'
import s from '../styles/HistoryPage.module.css'

export default function HistoryPage() {
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => { fetchReviews() }, [])

  async function fetchReviews() {
    try {
      const data = await getAllReviews()
      setReviews(data)
    } catch (e) {
      setError('Failed to load reviews. Is the backend running?')
    } finally {
      setLoading(false)
    }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this review?')) return
    await deleteReview(id)
    setReviews(reviews.filter(r => r.id !== id))
  }

  function scoreColor(n) {
    if (n >= 75) return s.green
    if (n >= 50) return s.amber
    return s.red
  }

  if (loading) return (
    <div className={s.center}><i className="ti ti-loader-2 ti-spin" /> Loading history...</div>
  )

  if (error) return (
    <div className={s.errorCard}><i className="ti ti-alert-circle" /> {error}</div>
  )

  return (
    <div>
      <div className={s.header}>
        <h1><i className="ti ti-history" /> Review History</h1>
        <p>{reviews.length} review{reviews.length !== 1 ? 's' : ''} saved</p>
      </div>

      {reviews.length === 0 ? (
        <div className={s.empty}>
          <i className="ti ti-code-off" />
          <p>No reviews yet. Submit some code to get started!</p>
        </div>
      ) : (
        <div className={s.list}>
          {reviews.map(r => (
            <div key={r.id} className={s.row}>
              <div className={s.rowLeft}>
                <span className={s.lang}>{r.language}</span>
                <code className={s.snippet}>{r.code?.slice(0, 80)}{r.code?.length > 80 ? '...' : ''}</code>
              </div>
              <div className={s.rowMid}>
                <span className={`${s.score} ${scoreColor(r.scores?.quality)}`}>
                  Q: {r.scores?.quality}
                </span>
                <span className={s.complexity}>{r.complexity?.time}</span>
                <span className={s.bugCount}>
                  <i className="ti ti-bug" /> {r.bugs?.length ?? 0}
                </span>
              </div>
              <div className={s.rowRight}>
                <span className={s.date}>
                  {r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ''}
                </span>
                <button className={s.deleteBtn} onClick={() => handleDelete(r.id)}>
                  <i className="ti ti-trash" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
