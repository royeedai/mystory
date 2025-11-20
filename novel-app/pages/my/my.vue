<template>
  <view class="container">
    <view class="user-info">
      <view class="avatar-wrapper">
        <image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'" />
        <view v-if="!userInfo.nickname" class="login-tip">点击登录</view>
      </view>
      <text class="nickname">{{ userInfo.nickname || '未登录' }}</text>
      <text v-if="userInfo.nickname" class="user-id">ID: {{ userInfo.id || '' }}</text>
    </view>
    
    <view class="trial-info">
      <view class="trial-icon">⏱</view>
      <view class="trial-content">
        <text class="trial-label">剩余试看时间</text>
        <text class="trial-time">{{ formatTime(userInfo.trialViewTime || 0) }}</text>
      </view>
    </view>
    
    <view class="menu-list">
      <view class="menu-item" @click="goToReadingRecords">
        <view class="menu-left">
          <text class="menu-icon">📚</text>
          <text class="menu-text">阅读记录</text>
        </view>
        <text class="arrow">›</text>
      </view>
      <view class="menu-item" @click="goToSettings">
        <view class="menu-left">
          <text class="menu-icon">⚙️</text>
          <text class="menu-text">设置</text>
        </view>
        <text class="arrow">›</text>
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
    },
    goToSettings() {
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
  min-height: 100vh;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  padding-bottom: 40rpx;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 40rpx 40rpx;
  margin-bottom: 30rpx;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 24rpx;
}

.avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 70rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.login-tip {
  position: absolute;
  bottom: -8rpx;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.9);
  padding: 4rpx 16rpx;
  border-radius: 12rpx;
  font-size: 20rpx;
  color: #667eea;
  white-space: nowrap;
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
  color: white;
  margin-bottom: 8rpx;
}

.user-id {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.trial-info {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background: white;
  border-radius: 20rpx;
  margin: 0 30rpx 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.trial-icon {
  font-size: 48rpx;
  margin-right: 24rpx;
}

.trial-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.trial-label {
  font-size: 26rpx;
  color: #666;
}

.trial-time {
  font-size: 36rpx;
  font-weight: bold;
  color: #667eea;
}

.menu-list {
  background: white;
  border-radius: 20rpx;
  margin: 0 30rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
  transition: background-color 0.2s;
}

.menu-item:active {
  background-color: #f5f5f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.menu-icon {
  font-size: 36rpx;
}

.menu-text {
  font-size: 30rpx;
  color: #333;
}

.arrow {
  font-size: 32rpx;
  color: #ccc;
  font-weight: bold;
}
</style>
