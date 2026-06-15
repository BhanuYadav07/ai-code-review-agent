import { Routes, Route, NavLink } from 'react-router-dom'
import ReviewPage from './components/ReviewPage'
import HistoryPage from './components/HistoryPage'
import styles from './styles/App.module.css'

export default function App() {
  return (
    <div className={styles.app}>
      <nav className={styles.nav}>
        <div className={styles.navBrand}>
          <i className="ti ti-robot" />
          <span>AI Code Review Agent</span>
        </div>
        <div className={styles.navLinks}>
          <NavLink to="/" end className={({ isActive }) => isActive ? styles.active : ''}>
            <i className="ti ti-code" /> Review
          </NavLink>
          <NavLink to="/history" className={({ isActive }) => isActive ? styles.active : ''}>
            <i className="ti ti-history" /> History
          </NavLink>
        </div>
      </nav>

      <main className={styles.main}>
        <Routes>
          <Route path="/" element={<ReviewPage />} />
          <Route path="/history" element={<HistoryPage />} />
        </Routes>
      </main>
    </div>
  )
}
