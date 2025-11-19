<template>
  <div class="chapter-list">
    <div class="header">
      <h2>章节管理</h2>
      <el-button type="primary" @click="handleCreate">新增章节</el-button>
    </div>
    
    <el-table :data="tableData" v-loading="loading" style="width: 100%">
      <el-table-column prop="chapterNum" label="序号" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="wordCount" label="字数" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { chapterApi } from '../../api'

const route = useRoute()
const novelId = route.params.id

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  // TODO: 实现章节列表加载
  tableData.value = []
}

const handleCreate = () => {
  // TODO: 实现新增章节
  ElMessage.info('功能开发中')
}

const handleEdit = (row) => {
  // TODO: 实现编辑章节
  ElMessage.info('功能开发中')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
  try {
    await chapterApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.chapter-list .header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
