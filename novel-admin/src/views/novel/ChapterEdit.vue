<template>
  <div class="chapter-edit">
    <div class="header">
      <h2>{{ isEdit ? '编辑章节' : '新增章节' }}</h2>
      <div class="actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </div>
    </div>
    
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="章节序号" prop="chapterNum">
        <el-input-number v-model="form.chapterNum" :min="1" :precision="0" />
      </el-form-item>
      
      <el-form-item label="章节标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入章节标题" />
      </el-form-item>
      
      <el-form-item label="章节内容" prop="content">
        <div class="editor-wrapper">
          <QuillEditor
            v-model:content="form.content"
            contentType="html"
            theme="snow"
            :options="editorOptions"
            style="height: 500px"
          />
        </div>
        <div class="word-count">字数：{{ wordCount }}</div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { chapterApi } from '../../api'

const route = useRoute()
const router = useRouter()

const novelId = ref(parseInt(route.params.novelId))
const chapterId = ref(route.params.id ? parseInt(route.params.id) : null)
const isEdit = computed(() => !!chapterId.value)

const formRef = ref(null)
const saving = ref(false)

const form = ref({
  chapterNum: 1,
  title: '',
  content: ''
})

const rules = {
  chapterNum: [
    { required: true, message: '请输入章节序号', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入章节标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入章节内容', trigger: 'blur' }
  ]
}

const editorOptions = {
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote', 'code-block'],
      [{ 'header': 1 }, { 'header': 2 }],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'script': 'sub'}, { 'script': 'super' }],
      [{ 'indent': '-1'}, { 'indent': '+1' }],
      [{ 'direction': 'rtl' }],
      [{ 'size': ['small', false, 'large', 'huge'] }],
      [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
      [{ 'color': [] }, { 'background': [] }],
      [{ 'font': [] }],
      [{ 'align': [] }],
      ['clean'],
      ['link', 'image']
    ]
  },
  placeholder: '请输入章节内容...'
}

const wordCount = computed(() => {
  if (!form.value.content) return 0
  // 去除HTML标签，计算纯文本字数
  const text = form.value.content.replace(/<[^>]*>/g, '').trim()
  return text.length
})

const loadChapter = async () => {
  if (!chapterId.value) return
  
  try {
    const res = await chapterApi.getDetail(chapterId.value)
    form.value = {
      chapterNum: res.data.chapterNum,
      title: res.data.title,
      content: res.data.content || ''
    }
  } catch (error) {
    ElMessage.error(error.message || '加载章节失败')
  }
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
    
    // 提取纯文本内容用于字数统计
    const textContent = form.value.content.replace(/<[^>]*>/g, '')
    if (textContent.trim().length === 0) {
      ElMessage.warning('章节内容不能为空')
      return
    }
    
    saving.value = true
    
    const data = {
      novelId: novelId.value,
      chapterNum: form.value.chapterNum,
      title: form.value.title,
      content: form.value.content
    }
    
    if (isEdit.value) {
      await chapterApi.update(chapterId.value, data)
      ElMessage.success('更新成功')
    } else {
      await chapterApi.create(novelId.value, data)
      ElMessage.success('创建成功')
    }
    
    router.back()
  } catch (error) {
    if (error.message !== '表单验证失败') {
      ElMessage.error(error.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  loadChapter()
})
</script>

<style scoped>
.chapter-edit {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.actions {
  display: flex;
  gap: 10px;
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.word-count {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
  text-align: right;
}

:deep(.ql-editor) {
  min-height: 400px;
}
</style>
