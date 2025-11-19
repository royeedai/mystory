<template>
  <view class="container">
    <view class="search-box">
      <input class="search-input" placeholder="搜索小说" v-model="keyword" @confirm="handleSearch" />
    </view>
    
    <view class="novel-list">
      <view class="novel-item" v-for="item in novelList" :key="item.id" @click="goToDetail(item.id)">
        <image class="novel-cover" :src="item.cover || '/static/default-cover.png'" mode="aspectFill" />
        <view class="novel-info">
          <text class="novel-title">{{ item.title }}</text>
          <text class="novel-author">{{ item.authorName }}</text>
          <text class="novel-desc">{{ item.description }}</text>
        </view>
      </view>
    </view>
    
    <view v-if="loading" class="loading">加载中...</view>
    <view v-if="noMore" class="no-more">没有更多了</view>
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
}

.search-box {
  margin-bottom: 20rpx;
}

.search-input {
  width: 100%;
  height: 70rpx;
  background: white;
  border-radius: 10rpx;
  padding: 0 20rpx;
}

.novel-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.novel-item {
  display: flex;
  background: white;
  border-radius: 10rpx;
  padding: 20rpx;
}

.novel-cover {
  width: 160rpx;
  height: 220rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.novel-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.novel-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.novel-author {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.novel-desc {
  font-size: 26rpx;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.loading, .no-more {
  text-align: center;
  padding: 20rpx;
  color: #999;
}
</style>
