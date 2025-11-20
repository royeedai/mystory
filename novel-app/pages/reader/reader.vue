<template>
  <view class="reader-container" :class="{ 'dark-mode': isDarkMode }">
    <!-- 顶部导航栏 -->
    <view class="reader-header" v-if="showHeader">
      <view class="header-left" @tap="goBack">
        <text class="icon">‹</text>
      </view>
      <view class="header-center">
        <text class="chapter-title">{{ chapterTitle }}</text>
      </view>
      <view class="header-right" @tap="showSettings = true">
        <text class="icon">⚙</text>
      </view>
    </view>
    
    <!-- 阅读内容区域 -->
    <view class="reader-content" @tap="toggleHeader">
      <view class="content-wrapper" :style="contentStyle">
        <text class="content-text" :style="textStyle">{{ currentPageContent }}</text>
      </view>
      
      <!-- 翻页提示 -->
      <view class="page-hint" v-if="showPageHint">
        <text>{{ currentPage }}/{{ totalPages }}</text>
      </view>
    </view>
    
    <!-- 底部工具栏 -->
    <view class="reader-footer" v-if="showHeader">
      <view class="footer-info">
        <text class="trial-time">剩余试看：{{ formatTime(trialTime) }}</text>
        <text class="page-info">{{ currentPage }}/{{ totalPages }}</text>
      </view>
      <view class="footer-actions">
        <button class="action-btn" @click="prevPage" :disabled="currentPage <= 1">上一页</button>
        <button class="action-btn" @click="nextPage" :disabled="currentPage >= totalPages">下一页</button>
        <button class="action-btn ad-btn" @click="watchAd">观看广告</button>
      </view>
    </view>
    
    <!-- 试看限制提示 -->
    <view class="trial-limit" v-if="showTrialLimit">
      <view class="limit-content">
        <text class="limit-text">试看时间不足</text>
        <text class="limit-desc">观看广告可获取更多试看时间</text>
        <button class="ad-btn" @click="watchAd">观看广告获取时间</button>
      </view>
    </view>
    
    <!-- 设置面板 -->
    <view class="settings-panel" v-if="showSettings" @tap="showSettings = false">
      <view class="settings-content" @tap.stop>
        <view class="settings-header">
          <text class="settings-title">阅读设置</text>
          <text class="close-btn" @tap="showSettings = false">✕</text>
        </view>
        
        <view class="settings-item">
          <text class="settings-label">字体大小</text>
          <view class="font-size-controls">
            <button class="size-btn" @click="decreaseFontSize">A-</button>
            <text class="size-display">{{ fontSize }}px</text>
            <button class="size-btn" @click="increaseFontSize">A+</button>
          </view>
        </view>
        
        <view class="settings-item">
          <text class="settings-label">行间距</text>
          <view class="line-height-controls">
            <button class="size-btn" @click="decreaseLineHeight">-</button>
            <text class="size-display">{{ lineHeight }}</text>
            <button class="size-btn" @click="increaseLineHeight">+</button>
          </view>
        </view>
        
        <view class="settings-item">
          <text class="settings-label">夜间模式</text>
          <switch :checked="isDarkMode" @change="toggleDarkMode" />
        </view>
        
        <view class="settings-item">
          <text class="settings-label">背景色</text>
          <view class="bg-colors">
            <view 
              class="bg-color-item" 
              v-for="(color, index) in bgColors" 
              :key="index"
              :class="{ active: currentBgColor === index }"
              :style="{ backgroundColor: color }"
              @tap="changeBgColor(index)"
            ></view>
          </view>
        </view>
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
      chapterId: '',
      novelId: '',
      chapterTitle: '',
      currentPage: 1,
      totalPages: 1,
      currentPageContent: '',
      trialTime: 0,
      showHeader: true,
      showTrialLimit: false,
      showSettings: false,
      showPageHint: false,
      rewardVideoAd: null,
      fontSize: 32,
      lineHeight: 1.8,
      isDarkMode: false,
      currentBgColor: 0,
      bgColors: ['#f5f0e8', '#e8f5e8', '#f0f0f0', '#1a1a1a']
    }
  },
  computed: {
    textStyle() {
      return {
        fontSize: this.fontSize + 'rpx',
        lineHeight: this.lineHeight,
        color: this.isDarkMode ? '#e0e0e0' : '#333'
      }
    },
    contentStyle() {
      return {
        backgroundColor: this.bgColors[this.currentBgColor],
        color: this.isDarkMode ? '#e0e0e0' : '#333'
      }
    }
  },
  onLoad(options) {
    this.chapterId = options.chapterId
    this.novelId = options.novelId || ''
    this.loadSettings()
    this.initAd()
    this.checkLoginAndLoad()
  },
  onUnload() {
    this.saveReadingProgress()
    this.saveSettings()
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
          params: { chapterId: this.chapterId }
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
          params: { page: this.currentPage }
        })
        this.chapterTitle = res.data.title
        this.currentPageContent = res.data.content
        this.currentPage = res.data.currentPage
        this.totalPages = res.data.totalPages
        this.showTrialLimit = false
      } catch (error) {
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.loadChapter()
        this.saveReadingProgress()
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.loadChapter()
        this.saveReadingProgress()
      }
    },
    toggleHeader() {
      this.showHeader = !this.showHeader
      if (!this.showHeader) {
        this.showSettings = false
      }
    },
    goBack() {
      this.saveReadingProgress()
      uni.navigateBack()
    },
    increaseFontSize() {
      if (this.fontSize < 48) {
        this.fontSize += 2
      }
    },
    decreaseFontSize() {
      if (this.fontSize > 24) {
        this.fontSize -= 2
      }
    },
    increaseLineHeight() {
      if (this.lineHeight < 2.5) {
        this.lineHeight += 0.1
      }
    },
    decreaseLineHeight() {
      if (this.lineHeight > 1.2) {
        this.lineHeight -= 0.1
      }
    },
    toggleDarkMode(e) {
      this.isDarkMode = e.detail.value
      if (this.isDarkMode) {
        this.currentBgColor = 3
      }
    },
    changeBgColor(index) {
      this.currentBgColor = index
      if (index === 3) {
        this.isDarkMode = true
      } else {
        this.isDarkMode = false
      }
    },
    async saveReadingProgress() {
      if (!this.novelId || !this.chapterId) return
      try {
        await request.post('/reading/record', {
          novelId: this.novelId,
          chapterId: this.chapterId,
          pageIndex: this.currentPage
        })
      } catch (error) {
        console.error('保存阅读进度失败', error)
      }
    },
    loadSettings() {
      try {
        const settings = uni.getStorageSync('readerSettings')
        if (settings) {
          this.fontSize = settings.fontSize || 32
          this.lineHeight = settings.lineHeight || 1.8
          this.isDarkMode = settings.isDarkMode || false
          this.currentBgColor = settings.currentBgColor || 0
        }
      } catch (error) {
        console.error('加载设置失败', error)
      }
    },
    saveSettings() {
      try {
        uni.setStorageSync('readerSettings', {
          fontSize: this.fontSize,
          lineHeight: this.lineHeight,
          isDarkMode: this.isDarkMode,
          currentBgColor: this.currentBgColor
        })
      } catch (error) {
        console.error('保存设置失败', error)
      }
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
  display: flex;
  flex-direction: column;
  position: relative;
}

.reader-container.dark-mode {
  background: #1a1a1a;
}

.reader-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.header-left, .header-right {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-center {
  flex: 1;
  text-align: center;
}

.chapter-title {
  font-size: 28rpx;
  font-weight: bold;
}

.icon {
  font-size: 48rpx;
  font-weight: bold;
}

.reader-content {
  flex: 1;
  padding: 40rpx;
  padding-top: 100rpx;
  padding-bottom: 200rpx;
  position: relative;
}

.content-wrapper {
  min-height: 100%;
}

.content-text {
  display: block;
  word-wrap: break-word;
  word-break: break-all;
  text-align: justify;
}

.page-hint {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 20rpx 40rpx;
  border-radius: 10rpx;
  font-size: 28rpx;
  z-index: 50;
}

.reader-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 20rpx;
  z-index: 100;
}

.footer-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
  font-size: 24rpx;
}

.footer-actions {
  display: flex;
  gap: 20rpx;
}

.action-btn {
  flex: 1;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
}

.action-btn:disabled {
  opacity: 0.5;
}

.ad-btn {
  background: #3cc51f !important;
  border-color: #3cc51f !important;
}

.trial-limit {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.limit-content {
  background: white;
  padding: 60rpx 40rpx;
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30rpx;
  margin: 0 40rpx;
}

.limit-text {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.limit-desc {
  font-size: 28rpx;
  color: #666;
}

.settings-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 300;
  display: flex;
  align-items: flex-end;
}

.settings-content {
  width: 100%;
  background: white;
  border-radius: 30rpx 30rpx 0 0;
  padding: 40rpx;
  max-height: 70vh;
  overflow-y: auto;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.settings-title {
  font-size: 36rpx;
  font-weight: bold;
}

.close-btn {
  font-size: 48rpx;
  color: #999;
}

.settings-item {
  margin-bottom: 40rpx;
}

.settings-label {
  display: block;
  font-size: 28rpx;
  margin-bottom: 20rpx;
  color: #333;
}

.font-size-controls, .line-height-controls {
  display: flex;
  align-items: center;
  gap: 30rpx;
}

.size-btn {
  width: 80rpx;
  height: 60rpx;
  background: #f0f0f0;
  border: none;
  border-radius: 10rpx;
  font-size: 28rpx;
}

.size-display {
  font-size: 28rpx;
  min-width: 100rpx;
  text-align: center;
}

.bg-colors {
  display: flex;
  gap: 20rpx;
}

.bg-color-item {
  width: 80rpx;
  height: 80rpx;
  border-radius: 10rpx;
  border: 3rpx solid transparent;
}

.bg-color-item.active {
  border-color: #3cc51f;
}

.dark-mode .settings-content {
  background: #2a2a2a;
  color: #e0e0e0;
}

.dark-mode .settings-label {
  color: #e0e0e0;
}

.dark-mode .limit-content {
  background: #2a2a2a;
  color: #e0e0e0;
}

.dark-mode .limit-text {
  color: #e0e0e0;
}
</style>
