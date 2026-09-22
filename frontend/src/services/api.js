import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' }
})

// Submit code for review
export const submitReview = (code, language) =>
  api.post('/api/review', { code, language }).then(r => r.data)

// Get all past reviews
export const getAllReviews = () =>
  api.get('/api/reviews').then(r => r.data)

// Get single review
export const getReviewById = (id) =>
  api.get(`/api/reviews/${id}`).then(r => r.data)

// Delete a review
export const deleteReview = (id) =>
  api.delete(`/api/reviews/${id}`)

export default api
