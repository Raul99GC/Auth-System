import axios from 'axios'

const api = axios.create({
  baseURL: `${import.meta.env.VITE_BACK_URL}/api/v1`,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json'
  },
  withCredentials: true
})

export default api;