<template>
  <div class="novel-list">
    <div class="header">
      <div class="header-left">
        <h2>小说管理</h2>
        <p class="subtitle">管理所有小说内容</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleCreate">新增小说</el-button>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="请输入标题或作者" 
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已审核" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card>
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        style="width: 100%"
        stripe
        :empty-text="emptyText"
      >
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读量" width="100" align="right">
          <template #default="{ row }">
            <span>{{ formatNumber(row.viewCount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" :icon="Document" @click="handleChapter(row)">章节</el-button>
            <el-button 
              v-if="row.status === 'DRAFT'" 
              link 
              type="success" 
              :icon="Promotion"
              @click="handleSubmit(row)"
            >
              提交审核
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadData"
        @size-change="handleSizeChange"
        style="margin-top: 20px; justify-content: flex-end; display: flex;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Document, Promotion, Delete } from '@element-plus/icons-vue'
import { novelApi } from '../../api'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({
  keyword: '',
  status: ''
})

const emptyText = computed(() => {
  return loading.value ? '加载中...' : '暂无数据'
})

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
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.value.keyword || undefined,
      status: searchForm.value.status || undefined
    }
    const res = await novelApi.getList(params)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    status: ''
  }
  pageNum.value = 1
  loadData()
}

const handleSizeChange = () => {
  pageNum.value = 1
  loadData()
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
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
.novel-list {
  min-height: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.search-card {
  margin-bottom: 20px;
}

.search-card :deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #fafafa;
  font-weight: 600;
}
</style>
