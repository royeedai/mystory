<template>
  <div class="audit-list">
    <h2>审核管理</h2>
    <el-table :data="tableData" v-loading="loading" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="authorName" label="作者" width="120" />
      <el-table-column prop="createTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="success" @click="handleApprove(row)">通过</el-button>
          <el-button link type="danger" @click="handleReject(row)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { novelApi } from '../../api'

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await novelApi.getList({
      pageNum: 1,
      pageSize: 100,
      status: 'PENDING'
    })
    tableData.value = res.data.records
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row) => {
  await ElMessageBox.confirm('确定要通过审核吗？', '提示', { type: 'warning' })
  try {
    await novelApi.audit(row.id, { status: 'APPROVED', remark: '' })
    ElMessage.success('审核通过')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleReject = async (row) => {
  await ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    novelApi.audit(row.id, { status: 'REJECTED', remark: value })
      .then(() => {
        ElMessage.success('已拒绝')
        loadData()
      })
      .catch(error => {
        ElMessage.error(error.message || '操作失败')
      })
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
</style>
