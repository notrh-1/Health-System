import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:9000',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const user = window.sessionStorage.getItem('user')
    if (user) {
      const userData = JSON.parse(user)
      config.headers['userId'] = userData.id
      config.headers['userRole'] = userData.role
      config.headers['username'] = userData.username
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    console.log('Response:', response)
    let data = response.data
    if (typeof data === 'string') {
      try {
        data = JSON.parse(data)
      } catch (e) {
        console.error('JSON parse error:', e)
      }
    }
    return data
  },
  error => {
    console.log('Error:', error)
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
