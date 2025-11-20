<template>
  <view class="container">
    <view class="header-section">
      <image class="cover" :src="novel.cover || '/static/default-cover.png'" mode="aspectFill" />
      <view class="header-info">
        <text class="title">{{ novel.title || '加载中...' }}</text>
        <text class="author">作者：{{ novel.authorName || '未知' }}</text>
        <view class="meta-row">
          <text class="meta-item">分类：{{ novel.category || '未分类' }}</text>
          <text class="meta-item">阅读量：{{ formatNumber(novel.viewCount || 0) }}</text>
        </view>
        <view v-if="novel.status" class="status-badge" :class="novel.status === 'APPROVED' ? 'approved' : 'pending'">
          {{ novel.status === 'APPROVED' ? '已审核' : '待审核' }}
        </view>
      </view>
    </view>
    
    <view class="info-section">
      <view class="section-title">简介</view>
      <text class="desc">{{ novel.description || '暂无简介' }}</text>
    </view>
    
    <view class="actions">
      <button class="btn primary" @click="goToChapterList">
        <text class="btn-icon">📖</text>
        <text>开始阅读</text>
      </button>
    </view>
    
    <view v-if="loading" class="loading-overlay">
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script>
import request from '../../api/request'

export default {
  data() {
    return {
      novelId: '',
      novel: {},
      loading: false
    }
  },
  onLoad(options) {
    this.novelId = options.id
    this.loadNovel()
  },
  methods: {
    async loadNovel() {
      this.loading = true
      try {
        const res = await request.get(`/novel/${this.novelId}`)
        this.novel = res.data || {}
      } catch (error) {
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    formatNumber(num) {
      if (!num) return '0'
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toString()
    },
    goToChapterList() {
      uni.navigateTo({
        url: `/pages/chapter-list/chapter-list?novelId=${this.novelId}`
      })
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.header-section {
  background: white;
  padding: 40rpx;
  display: flex;
  gap: 24rpx;
  margin-bottom: 20rpx;
}

.cover {
  width: 200rpx;
  height: 280rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.header-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.author {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.meta-row {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.meta-item {
  font-size: 24rpx;
  color: #999;
}

.status-badge {
  position: absolute;
  top: 0;
  right: 0;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}

.status-badge.approved {
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.status-badge.pending {
  background: rgba(255, 193, 7, 0.1);
  color: #FFC107;
}

.info-section {
  background: white;
  padding: 40rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
  display: block;
}

.actions {
  padding: 0 40rpx;
}

.btn {
  width: 100%;
  height: 88rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
  border: none;
}

.btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.btn-icon {
  font-size: 32rpx;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}
</style>
