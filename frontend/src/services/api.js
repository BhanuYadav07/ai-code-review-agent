import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' }
})

// Submit code for review
export const submitReview = (code, language) =>
  api.post('/review', { code, language }).then(r => r.data)

// Get all past reviews
export const getAllReviews = () =>
  api.get('/reviews').then(r => r.data)

// Get single review
export const getReviewById = (id) =>
  api.get(`/reviews/${id}`).then(r => r.data)

// Delete a review
export const deleteReview = (id) =>
  api.delete(`/reviews/${id}`)

export default api
