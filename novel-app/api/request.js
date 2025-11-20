// 使用相对路径，自动使用当前域名
// 如果部署在同一域名下，会自动请求当前域名的/api接口
const BASE_URL = '/api'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    
    // 处理GET请求的params参数
    let url = BASE_URL + options.url
    let data = options.data || {}
    
    if (options.method === 'GET' && options.params) {
      // 将params拼接到URL上
      const params = options.params
      const queryString = Object.keys(params)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
      if (queryString) {
        url += (url.includes('?') ? '&' : '?') + queryString
      }
      data = {}
    }
    
    uni.request({
      url: url,
      method: options.method || 'GET',
      data: data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else {
            uni.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(res.data)
          }
        } else if (res.statusCode === 401) {
          uni.showToast({
            title: '未登录或登录已过期',
            icon: 'none'
          })
          // 跳转到登录页
          uni.removeStorageSync('token')
          reject(res)
        } else {
          uni.showToast({
            title: '请求失败',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export default {
  get: (url, options = {}) => {
    if (options.params) {
      return request({ url, method: 'GET', params: options.params })
    }
    return request({ url, method: 'GET', data: options })
  },
  post: (url, data) => request({ url, method: 'POST', data }),
  put: (url, data) => request({ url, method: 'PUT', data }),
  delete: (url, data) => request({ url, method: 'DELETE', data })
}
