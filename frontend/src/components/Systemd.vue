<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getServiceUnits, 
  operateServiceUnit, 
  getServiceTemplate, 
  uploadService 
} from '@/api/systemd'
import type { ServiceUnitInfo, ServiceUnitOperation, ServiceFile } from '@/types/systemd'

// 状态管理
const level = ref<'system' | 'user'>('system')
const loading = ref(false)
const serviceUnits = ref<ServiceUnitInfo[]>([])
const operationLoading = ref(false)
const uploadDialogVisible = ref(false)
const uploadLoading = ref(false)

// 表单数据
const uploadForm = ref<ServiceFile>({
  level: 'system',
  unitName: '',
  content: ''
})

// 可用操作列表
const operations = [
  { value: 'start', label: '启动' },
  { value: 'stop', label: '停止' },
  { value: 'restart', label: '重启' },
  { value: 'enable', label: '启用' },
  { value: 'disable', label: '禁用' },
  { value: 'reload', label: '重载' }
]

// 获取服务列表
const fetchServiceUnits = async () => {
  try {
    loading.value = true
    const resp = await getServiceUnits(level.value)
    serviceUnits.value = resp.data
  } catch (error) {
    ElMessage.error('获取服务列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 切换运行级别
const handleLevelChange = () => {
  fetchServiceUnits()
}

// 操作服务单元
const handleOperateService = async (unitFile: string, operation: string) => {
  try {
    await ElMessageBox.confirm(
      `确定要${operations.find(op => op.value === operation)?.label}服务 ${unitFile} 吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    operationLoading.value = true
    const operationData: ServiceUnitOperation = {
      level: level.value,
      operation: operation,
      unitName: unitFile
    }
    
    const resp = await operateServiceUnit(operationData)
    if (resp.data) {
      ElMessage.success('操作成功')
      fetchServiceUnits() // 刷新列表
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
      console.error(error)
    }
  } finally {
    operationLoading.value = false
  }
}

// 获取服务模板
const handleGetTemplate = async () => {
  try {
    const resp = await getServiceTemplate()
    uploadForm.value.content = resp.data
    ElMessage.success('模板获取成功')
  } catch (error) {
    ElMessage.error('获取模板失败')
    console.error(error)
  }
}

// 上传服务文件
const handleUploadService = async () => {
  if (!uploadForm.value.unitName || !uploadForm.value.content) {
    ElMessage.warning('请输入服务名称和内容')
    return
  }

  // 验证服务名称格式
  const serviceNameRegex = /^(?:[a-zA-Z0-9_.@-]|\\x[0-9a-fA-F]{2})+\.service$/
  if (!serviceNameRegex.test(uploadForm.value.unitName)) {
    ElMessage.error('服务名称格式不正确')
    return
  }

  try {
    uploadLoading.value = true
    const resp = await uploadService(uploadForm.value)
    if (resp.data) {
      ElMessage.success('服务上传成功')
      uploadDialogVisible.value = false
      uploadForm.value = { level: 'system', unitName: '', content: '' }
      fetchServiceUnits() // 刷新列表
    }
  } catch (error) {
    ElMessage.error('服务上传失败')
    console.error(error)
  } finally {
    uploadLoading.value = false
  }
}

// 打开上传对话框
const openUploadDialog = () => {
  uploadForm.value.level = level.value
  uploadDialogVisible.value = true
}

// 初始化
onMounted(() => {
  fetchServiceUnits()
})
</script>

<template>
  <div class="systemd-container">
    <!-- 头部控制栏 -->
    <div class="header-section">
      <div class="control-panel">
        <div class="level-selector">
          <span class="label">运行级别:</span>
          <el-select 
            v-model="level" 
            @change="handleLevelChange"
            class="level-select"
          >
            <el-option label="系统级" value="system" />
            <el-option label="用户级" value="user" />
          </el-select>
        </div>
        
        <div class="action-buttons">
          <el-button 
            type="success" 
            @click="openUploadDialog"
            class="action-btn"
          >
            上传服务
          </el-button>
          <el-button 
            @click="fetchServiceUnits"
            :loading="loading"
            class="refresh-btn"
          >
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 服务列表 -->
    <div class="services-section">
      <el-card class="services-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">服务单元列表</span>
            <span class="record-count">共 {{ serviceUnits.length }} 项</span>
          </div>
        </template>
        
        <el-table 
          :data="serviceUnits" 
          border 
          stripe
          :loading="loading"
          class="services-table"
        >
          <el-table-column type="index" width="60" label="#" align="center" />
          <el-table-column prop="unitFile" label="服务名称" min-width="200" />
          <el-table-column prop="state" label="加载状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="row.state === 'loaded' ? 'success' : 'info'"
                size="small"
              >
                {{ row.state }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="preset" label="运行状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="row.preset === 'enabled' ? 'success' : 'danger'"
                size="small"
              >
                {{ row.preset }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="320" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons-cell">
                <el-button 
                  size="small" 
                  type="primary"
                  @click="handleOperateService(row.unitFile, 'start')"
                  :loading="operationLoading"
                  class="table-action-btn"
                >
                  启动
                </el-button>
                <el-button 
                  size="small" 
                  type="danger"
                  @click="handleOperateService(row.unitFile, 'stop')"
                  :loading="operationLoading"
                  class="table-action-btn"
                >
                  停止
                </el-button>
                <el-dropdown trigger="click" @command="(command: string) => handleOperateService(row.unitFile, command)">
                  <el-button size="small" class="more-actions-btn">
                    更多操作
                    <el-icon class="el-icon--right">
                      <arrow-down />
                    </el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="restart">重启</el-dropdown-item>
                      <el-dropdown-item command="enable">启用</el-dropdown-item>
                      <el-dropdown-item command="disable">禁用</el-dropdown-item>
                      <el-dropdown-item command="reload">重载</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 上传服务对话框 -->
    <el-dialog 
      v-model="uploadDialogVisible" 
      title="上传服务"
      width="700px"
      class="upload-dialog"
    >
      <div class="upload-header">
        <el-button @click="handleGetTemplate" type="primary" size="small">
          获取模板
        </el-button>
        <p class="template-tip">请先获取模板，然后修改相应内容</p>
      </div>
      
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="服务名称">
          <el-input 
            v-model="uploadForm.unitName" 
            placeholder="请输入服务文件名（必须以.service结尾）"
          />
        </el-form-item>
        
        <el-form-item label="服务内容">
          <el-input
            v-model="uploadForm.content"
            type="textarea"
            :rows="15"
            placeholder="请输入服务文件内容"
            class="service-content"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button 
            type="success" 
            @click="handleUploadService"
            :loading="uploadLoading"
          >
            上传服务
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.systemd-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 头部控制区域 */
.header-section {
  margin-bottom: 20px;
}

.control-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.level-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}

.label {
  font-weight: 500;
  color: #606266;
}

.level-select {
  width: 120px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  border-radius: 6px;
}

.refresh-btn {
  border-radius: 6px;
}

/* 服务列表区域 */
.services-section {
  margin-bottom: 20px;
}

.services-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.record-count {
  font-size: 14px;
  color: #909399;
}

.services-table {
  border-radius: 4px;
}

.services-table :deep(.el-table__header th) {
  background-color: #f8f9fa;
  font-weight: 500;
}

/* 表格操作按钮 */
.action-buttons-cell {
  display: flex;
  gap: 14px;
  justify-content: center;
  min-width: 280px;
  width: 100%;
}

.table-action-btn {
  padding: 6px 16px;
  font-size: 13px;
  min-width: 65px;
}

.more-actions-btn {
  padding: 6px 16px;
  font-size: 13px;
  min-width: 85px;
}

/* 对话框样式 */
.upload-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.upload-header {
  margin-bottom: 20px;
  text-align: center;
}

.template-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 0;
}

.service-content {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .systemd-container {
    padding: 15px;
  }
  
  .control-panel {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .card-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .action-buttons-cell {
    flex-wrap: wrap;
  }
}
</style>