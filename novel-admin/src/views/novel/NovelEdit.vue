<template>
  <div class="novel-edit">
    <h2>{{ isEdit ? '编辑小说' : '新增小说' }}</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 800px;">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入小说标题" />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input v-model="form.category" placeholder="请输入分类" />
      </el-form-item>
      <el-form-item label="封面" prop="cover">
        <el-input v-model="form.cover" placeholder="请输入封面URL" />
      </el-form-item>
      <el-form-item label="简介" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请输入简介" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
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

onMounted(() => {
  if (route.params.id) {
    isEdit.value = true
    // TODO: 加载小说详情
  }
})
</script>

<style scoped>
.novel-edit {
  background: white;
  padding: 20px;
  border-radius: 4px;
}
</style>
