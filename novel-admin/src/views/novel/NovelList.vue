<template>
  <div class="novel-list">
    <div class="header">
      <h2>小说管理</h2>
      <el-button type="primary" @click="handleCreate">新增小说</el-button>
    </div>
    
    <el-table :data="tableData" v-loading="loading" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="阅读量" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="handleChapter(row)">章节</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="success" @click="handleSubmit(row)">提交审核</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadData"
      style="margin-top: 20px; justify-content: flex-end; display: flex;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { novelApi } from '../../api'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getStatusType = (status) => {
  const map = {
    DRAFT: '',
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    DRAFT: '草稿',
    PENDING: '待审核',
    APPROVED: '已审核',
    REJECTED: '已拒绝'
  }
  return map[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await novelApi.getList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  router.push('/novel/edit')
}

const handleEdit = (row) => {
  router.push(`/novel/edit/${row.id}`)
}

const handleChapter = (row) => {
  router.push(`/novel/${row.id}/chapters`)
}

const handleSubmit = async (row) => {
  try {
    await novelApi.submitAudit(row.id)
    ElMessage.success('提交成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
  try {
    await novelApi.delete(row.id)
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
.novel-list .header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
