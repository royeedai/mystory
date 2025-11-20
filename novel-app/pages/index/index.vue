<template>
  <view class="container">
    <view class="search-box">
      <view class="search-wrapper">
        <text class="search-icon">🔍</text>
        <input 
          class="search-input" 
          placeholder="搜索小说" 
          v-model="keyword" 
          @confirm="handleSearch"
          @input="handleSearchInput"
        />
        <text v-if="keyword" class="clear-icon" @tap="clearSearch">✕</text>
      </view>
    </view>
    
    <view v-if="!loading && novelList.length === 0 && !keyword" class="empty-state">
      <text class="empty-icon">📚</text>
      <text class="empty-text">暂无小说</text>
    </view>
    
    <view v-else-if="!loading && novelList.length === 0 && keyword" class="empty-state">
      <text class="empty-icon">🔍</text>
      <text class="empty-text">未找到相关小说</text>
    </view>
    
    <view v-else class="novel-list">
      <view 
        class="novel-item" 
        v-for="item in novelList" 
        :key="item.id" 
        @click="goToDetail(item.id)"
      >
        <view class="cover-wrapper">
          <image 
            class="novel-cover" 
            :src="item.cover || '/static/default-cover.png'" 
            mode="aspectFill"
            :lazy-load="true"
          />
          <view v-if="item.status === 'APPROVED'" class="status-badge">已审核</view>
        </view>
        <view class="novel-info">
          <text class="novel-title">{{ item.title }}</text>
          <text class="novel-author">作者：{{ item.authorName || '未知' }}</text>
          <text class="novel-desc">{{ item.description || '暂无简介' }}</text>
          <view class="novel-meta">
            <text class="meta-item">{{ item.category || '未分类' }}</text>
            <text class="meta-item">阅读量：{{ formatNumber(item.viewCount || 0) }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <view v-if="loading && novelList.length === 0" class="loading-wrapper">
      <view class="loading-item" v-for="i in 3" :key="i">
        <view class="skeleton-cover"></view>
        <view class="skeleton-content">
          <view class="skeleton-line" style="width: 60%;"></view>
          <view class="skeleton-line" style="width: 40%;"></view>
          <view class="skeleton-line" style="width: 80%;"></view>
        </view>
      </view>
    </view>
    
    <view v-if="loading && novelList.length > 0" class="loading">加载中...</view>
    <view v-if="noMore && novelList.length > 0" class="no-more">没有更多了</view>
  </view>
</template>

<script>
import { checkLogin } from '../../utils/auth'
import request from '../../api/request'

export default {
  data() {
    return {
      novelList: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      noMore: false,
      keyword: ''
    }
  },
  onLoad() {
    this.loadNovels()
  },
  onPullDownRefresh() {
    this.pageNum = 1
    this.novelList = []
    this.loadNovels().finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  onReachBottom() {
    if (!this.noMore && !this.loading) {
      this.pageNum++
      this.loadNovels()
    }
  },
  methods: {
    async loadNovels() {
      if (this.loading) return
      this.loading = true
      
      try {
        const res = await request.get('/novel/list', {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.keyword
        })
        
        if (res.data.records.length === 0) {
          this.noMore = true
        } else {
          this.novelList = this.novelList.concat(res.data.records)
        }
      } catch (error) {
        console.error('加载失败', error)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageNum = 1
      this.novelList = []
      this.noMore = false
      this.loadNovels()
    },
    handleSearchInput() {
      // 可以添加防抖搜索
    },
    clearSearch() {
      this.keyword = ''
      this.handleSearch()
    },
    formatNumber(num) {
      if (!num) return '0'
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toString()
    },
    goToDetail(id) {
      uni.navigateTo({
        url: `/pages/novel-detail/novel-detail?id=${id}`
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

.search-box {
  margin-bottom: 24rpx;
}

.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: white;
  border-radius: 12rpx;
  padding: 0 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.search-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
  color: #999;
}

.search-input {
  flex: 1;
  height: 80rpx;
  font-size: 28rpx;
  color: #333;
}

.clear-icon {
  font-size: 28rpx;
  color: #999;
  padding: 8rpx;
  margin-left: 8rpx;
}

.novel-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.novel-item {
  display: flex;
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.novel-item:active {
  transform: scale(0.98);
  box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0.1);
}

.cover-wrapper {
  position: relative;
  margin-right: 24rpx;
}

.novel-cover {
  width: 160rpx;
  height: 220rpx;
  border-radius: 12rpx;
  background: #f0f0f0;
}

.status-badge {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  background: rgba(64, 158, 255, 0.9);
  color: white;
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.novel-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.novel-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.novel-author {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.novel-desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12rpx;
  flex: 1;
}

.novel-meta {
  display: flex;
  gap: 24rpx;
  margin-top: 12rpx;
}

.meta-item {
  font-size: 22rpx;
  color: #999;
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
  gap: 24rpx;
}

.loading-item {
  display: flex;
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
}

.skeleton-cover {
  width: 160rpx;
  height: 220rpx;
  border-radius: 12rpx;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s ease-in-out infinite;
  margin-right: 24rpx;
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.skeleton-line {
  height: 24rpx;
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

.loading, .no-more {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 26rpx;
}
</style>
