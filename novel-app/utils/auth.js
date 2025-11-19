import request from '../api/request'

export function wxLogin() {
  return new Promise((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: (res) => {
        // 调用后端接口进行登录
        request.post('/auth/wx-login', { code: res.code })
          .then(result => {
            // 保存token
            uni.setStorageSync('token', result.data.token)
            resolve(result.data)
          })
          .catch(err => {
            reject(err)
          })
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

export function checkLogin() {
  const token = uni.getStorageSync('token')
  if (!token) {
    return wxLogin()
  }
  return Promise.resolve()
}
