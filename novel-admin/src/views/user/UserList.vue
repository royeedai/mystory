<template>
  <div class="user-list">
    <h2>小程序用户管理</h2>
    <el-table :data="tableData" v-loading="loading" style="width: 100%">
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="trialViewTime" label="试看时间（秒）" width="150" />
      <el-table-column prop="isTrialEnabled" label="试看权限" width="120">
        <template #default="{ row }">
          <el-tag :type="row.isTrialEnabled === 1 ? 'success' : 'danger'">
            {{ row.isTrialEnabled === 1 ? '允许' : '禁止' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleToggleTrial(row)">
            {{ row.isTrialEnabled === 1 ? '禁止试看' : '允许试看' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../../api'

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  // TODO: 实现用户列表加载
  tableData.value = []
}

const handleToggleTrial = async (row) => {
  try {
    await userApi.setTrialSettings(row.id, {
      isTrialEnabled: row.isTrialEnabled === 1 ? 0 : 1
    })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
</style>
