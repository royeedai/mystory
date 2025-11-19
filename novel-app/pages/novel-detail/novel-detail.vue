<template>
  <view class="container">
    <image class="cover" :src="novel.cover || '/static/default-cover.png'" mode="aspectFill" />
    <view class="info">
      <text class="title">{{ novel.title }}</text>
      <text class="author">作者：{{ novel.authorName }}</text>
      <text class="desc">{{ novel.description }}</text>
      <text class="category">分类：{{ novel.category }}</text>
    </view>
    
    <view class="actions">
      <button class="btn" @click="goToChapterList">开始阅读</button>
    </view>
  </view>
</template>

<script>
import request from '../../api/request'

export default {
  data() {
    return {
      novelId: '',
      novel: {}
    }
  },
  onLoad(options) {
    this.novelId = options.id
    this.loadNovel()
  },
  methods: {
    async loadNovel() {
      try {
        const res = await request.get(`/novel/${this.novelId}`)
        this.novel = res.data
      } catch (error) {
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
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
  display: flex;
  flex-direction: column;
}

.cover {
  width: 100%;
  height: 500rpx;
}

.info {
  padding: 30rpx;
  background: white;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 20rpx;
}

.author {
  font-size: 28rpx;
  color: #666;
  display: block;
  margin-bottom: 20rpx;
}

.desc {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
  line-height: 1.6;
}

.category {
  font-size: 24rpx;
  color: #999;
  display: block;
}

.actions {
  padding: 30rpx;
}

.btn {
  width: 100%;
  background: #3cc51f;
  color: white;
  border-radius: 10rpx;
  height: 80rpx;
  line-height: 80rpx;
}
</style>
