<template>
  <view class="reader-container">
    <view class="reader-header" v-if="showHeader">
      <text class="chapter-title">{{ chapterTitle }}</text>
      <text class="page-info">{{ currentPage }}/{{ totalPages }}</text>
    </view>
    
    <view class="reader-content" @tap="toggleHeader">
      <text class="content-text">{{ currentPageContent }}</text>
    </view>
    
    <view class="reader-footer" v-if="showHeader">
      <text class="trial-time">剩余试看：{{ formatTime(trialTime) }}</text>
      <button class="ad-btn" @click="watchAd">观看广告获取时间</button>
    </view>
    
    <view class="trial-limit" v-if="showTrialLimit">
      <text class="limit-text">试看时间不足</text>
      <button class="ad-btn" @click="watchAd">观看广告获取时间</button>
    </view>
  </view>
</template>

<script>
import { checkLogin } from '../../utils/auth'
import request from '../../api/request'

export default {
  data() {
    return {
      chapterId: '',
      chapterTitle: '',
      currentPage: 1,
      totalPages: 1,
      currentPageContent: '',
      trialTime: 0,
      showHeader: true,
      showTrialLimit: false,
      rewardVideoAd: null
    }
  },
  onLoad(options) {
    this.chapterId = options.chapterId
    this.initAd()
    this.checkLoginAndLoad()
  },
  methods: {
    async checkLoginAndLoad() {
      try {
        await checkLogin()
        await this.checkTrial()
        this.loadChapter()
      } catch (error) {
        uni.showToast({
          title: '登录失败',
          icon: 'none'
        })
      }
    },
    async checkTrial() {
      try {
        const res = await request.get('/reading/check-trial', {
          chapterId: this.chapterId
        })
        if (!res.data.canRead) {
          this.showTrialLimit = true
          this.trialTime = res.data.remainingTime || 0
        } else {
          this.trialTime = res.data.remainingTime || 0
        }
      } catch (error) {
        console.error('检查试看权限失败', error)
      }
    },
    async loadChapter() {
      try {
        const res = await request.get(`/chapter/${this.chapterId}/content`, {
          page: this.currentPage
        })
        this.chapterTitle = res.data.title
        this.currentPageContent = res.data.content
        this.currentPage = res.data.currentPage
        this.totalPages = res.data.totalPages
      } catch (error) {
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    },
    toggleHeader() {
      this.showHeader = !this.showHeader
    },
    initAd() {
      // #ifdef MP-WEIXIN
      if (wx.createRewardedVideoAd) {
        this.rewardVideoAd = wx.createRewardedVideoAd({
          adUnitId: 'your-ad-unit-id' // 需要替换为实际的广告位ID
        })
        
        this.rewardVideoAd.onClose((res) => {
          if (res && res.isEnded) {
            this.requestAdReward()
          }
        })
      }
      // #endif
    },
    watchAd() {
      // #ifdef MP-WEIXIN
      if (this.rewardVideoAd) {
        this.rewardVideoAd.show().catch(() => {
          this.rewardVideoAd.load()
            .then(() => this.rewardVideoAd.show())
        })
      } else {
        uni.showToast({
          title: '广告加载失败',
          icon: 'none'
        })
      }
      // #endif
      
      // #ifndef MP-WEIXIN
      // H5或其他平台，模拟广告奖励
      this.requestAdReward()
      // #endif
    },
    async requestAdReward() {
      try {
        const res = await request.post('/user/ad-reward', {
          adType: 'REWARD'
        })
        uni.showToast({
          title: `获得${res.data.rewardTime}秒试看时间`,
          icon: 'success'
        })
        this.showTrialLimit = false
        await this.checkTrial()
        this.loadChapter()
      } catch (error) {
        uni.showToast({
          title: '获取奖励失败',
          icon: 'none'
        })
      }
    },
    formatTime(seconds) {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}分${secs}秒`
    }
  }
}
</script>

<style scoped>
.reader-container {
  min-height: 100vh;
  background: #f5f0e8;
  display: flex;
  flex-direction: column;
}

.reader-header {
  display: flex;
  justify-content: space-between;
  padding: 20rpx;
  background: rgba(0, 0, 0, 0.5);
  color: white;
}

.chapter-title {
  font-size: 28rpx;
}

.page-info {
  font-size: 24rpx;
}

.reader-content {
  flex: 1;
  padding: 40rpx;
}

.content-text {
  font-size: 32rpx;
  line-height: 1.8;
  color: #333;
}

.reader-footer {
  padding: 20rpx;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.trial-time {
  font-size: 24rpx;
}

.ad-btn {
  background: #3cc51f;
  color: white;
  border-radius: 10rpx;
  padding: 10rpx 30rpx;
}

.trial-limit {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 40rpx;
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30rpx;
}

.limit-text {
  font-size: 32rpx;
  color: #333;
}
</style>
