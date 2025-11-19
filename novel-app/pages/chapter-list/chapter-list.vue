<template>
  <view class="container">
    <view class="chapter-item" v-for="item in chapterList" :key="item.id" @click="goToReader(item.id)">
      <text class="chapter-title">{{ item.title }}</text>
      <text class="chapter-info">{{ item.wordCount }}字</text>
    </view>
  </view>
</template>

<script>
import request from '../../api/request'

export default {
  data() {
    return {
      novelId: '',
      chapterList: []
    }
  },
  onLoad(options) {
    this.novelId = options.novelId
    this.loadChapters()
  },
  methods: {
    async loadChapters() {
      try {
        const res = await request.get(`/chapter/novel/${this.novelId}`)
        this.chapterList = res.data
      } catch (error) {
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
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
}

.chapter-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  background: white;
  border-radius: 10rpx;
  margin-bottom: 20rpx;
}

.chapter-title {
  font-size: 30rpx;
  color: #333;
}

.chapter-info {
  font-size: 24rpx;
  color: #999;
}
</style>
