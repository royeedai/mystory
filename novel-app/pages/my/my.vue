<template>
  <view class="container">
    <view class="user-info">
      <image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'" />
      <text class="nickname">{{ userInfo.nickname || '未登录' }}</text>
    </view>
    
    <view class="trial-info">
      <text class="trial-label">剩余试看时间</text>
      <text class="trial-time">{{ formatTime(userInfo.trialViewTime || 0) }}</text>
    </view>
    
    <view class="menu-list">
      <view class="menu-item" @click="goToReadingRecords">
        <text>阅读记录</text>
        <text class="arrow">></text>
      </view>
    </view>
  </view>
</template>

<script>
import { checkLogin } from '../../utils/auth'
import request from '../../api/request'

export default {
  data() {
    return {
      userInfo: {}
    }
  },
  onLoad() {
    this.loadUserInfo()
  },
  onShow() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        await checkLogin()
        const res = await request.get('/user/info')
        this.userInfo = res.data
      } catch (error) {
        console.error('加载用户信息失败', error)
      }
    },
    formatTime(seconds) {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}分${secs}秒`
    },
    goToReadingRecords() {
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped>
.container {
  padding: 40rpx;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
  background: white;
  border-radius: 20rpx;
  margin-bottom: 30rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-bottom: 20rpx;
}

.nickname {
  font-size: 32rpx;
  font-weight: bold;
}

.trial-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  background: white;
  border-radius: 20rpx;
  margin-bottom: 30rpx;
}

.trial-label {
  font-size: 28rpx;
  color: #666;
}

.trial-time {
  font-size: 32rpx;
  font-weight: bold;
  color: #3cc51f;
}

.menu-list {
  background: white;
  border-radius: 20rpx;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.arrow {
  color: #999;
}
</style>
