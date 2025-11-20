<template>
  <view class="container">
    <view v-if="loading && chapterList.length === 0" class="loading-wrapper">
      <view class="loading-item" v-for="i in 5" :key="i">
        <view class="skeleton-line" style="width: 60%;"></view>
        <view class="skeleton-line" style="width: 30%;"></view>
      </view>
    </view>
    
    <view v-else-if="chapterList.length === 0" class="empty-state">
      <text class="empty-icon">📖</text>
      <text class="empty-text">暂无章节</text>
    </view>
    
    <view v-else class="chapter-list">
      <view 
        class="chapter-item" 
        v-for="(item, index) in chapterList" 
        :key="item.id" 
        @click="goToReader(item.id)"
      >
        <view class="chapter-content">
          <text class="chapter-number">{{ index + 1 }}</text>
          <text class="chapter-title">{{ item.title }}</text>
        </view>
        <view class="chapter-meta">
          <text class="chapter-info">{{ formatWordCount(item.wordCount) }}</text>
          <text class="arrow">›</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import request from '../../api/request'

export default {
  data() {
    return {
      novelId: '',
      chapterList: [],
      loading: false
    }
  },
  onLoad(options) {
    this.novelId = options.novelId
    this.loadChapters()
  },
  methods: {
    async loadChapters() {
      this.loading = true
      try {
        const res = await request.get(`/chapter/novel/${this.novelId}`)
        this.chapterList = res.data || []
      } catch (error) {
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
        this.chapterList = []
      } finally {
        this.loading = false
      }
    },
    formatWordCount(count) {
      if (!count) return '0字'
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + '万字'
      }
      return count + '字'
    },
    goToReader(chapterId) {
      uni.navigateTo({
        url: `/pages/reader/reader?chapterId=${chapterId}`
      })
    }
  }
}
</script>

<style scoped>
.container {
  padding: 20rpx;
  min-height: 100vh;
  background: #f5f5f5;
}

.chapter-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.chapter-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 24rpx;
  background: white;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.chapter-item:active {
  transform: scale(0.98);
  box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.1);
}

.chapter-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  overflow: hidden;
}

.chapter-number {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8rpx;
  font-size: 22rpx;
  font-weight: bold;
  flex-shrink: 0;
}

.chapter-title {
  flex: 1;
  font-size: 30rpx;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chapter-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.chapter-info {
  font-size: 24rpx;
  color: #999;
}

.arrow {
  font-size: 32rpx;
  color: #ccc;
  font-weight: bold;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.loading-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.loading-item {
  background: white;
  border-radius: 16rpx;
  padding: 32rpx 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16rpx;
}

.skeleton-line {
  height: 28rpx;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s ease-in-out infinite;
  border-radius: 4rpx;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>
