<template>
  <div class="novel-edit">
    <div class="edit-header">
      <h2>{{ isEdit ? '编辑小说' : '新增小说' }}</h2>
      <p class="subtitle">{{ isEdit ? '修改小说信息' : '创建新的小说' }}</p>
    </div>
    <el-card>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 800px;">
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="form.title" 
            placeholder="请输入小说标题" 
            size="large"
            :prefix-icon="Document"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input 
            v-model="form.category" 
            placeholder="请输入分类，如：玄幻、都市、言情等" 
            size="large"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="封面" prop="cover">
          <el-input 
            v-model="form.cover" 
            placeholder="请输入封面图片URL" 
            size="large"
            :prefix-icon="Picture"
          />
          <div v-if="form.cover" class="cover-preview">
            <img :src="form.cover" alt="封面预览" @error="handleImageError" />
          </div>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="6" 
            placeholder="请输入小说简介，建议200字以内"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" size="large">
            {{ loading ? '保存中...' : '保存' }}
          </el-button>
          <el-button @click="handleCancel" size="large">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, Picture } from '@element-plus/icons-vue'
import { novelApi } from '../../api'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const isEdit = ref(false)
const form = reactive({
  title: '',
  category: '',
  cover: '',
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

const handleSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  
  try {
    if (isEdit.value) {
      await novelApi.update(route.params.id, form)
      ElMessage.success('更新成功')
    } else {
      await novelApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/novel')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}

const handleImageError = () => {
  ElMessage.warning('封面图片加载失败，请检查URL是否正确')
}

onMounted(() => {
  if (route.params.id) {
    isEdit.value = true
    loadNovelDetail()
  }
})

const loadNovelDetail = async () => {
  try {
    const res = await novelApi.getDetail(route.params.id)
    Object.assign(form, res.data)
  } catch (error) {
    ElMessage.error('加载小说详情失败')
  }
}
</script>

<style scoped>
.novel-edit {
  min-height: 100%;
}

.edit-header {
  margin-bottom: 24px;
}

.edit-header h2 {
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

.cover-preview {
  margin-top: 12px;
  width: 200px;
  height: 280px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}
</style>
