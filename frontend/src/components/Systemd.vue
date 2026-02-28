<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import {
  getServiceUnits,
  operateServiceUnit,
  getServiceTemplate,
  uploadService,
} from '@/api/systemd'
import type { ServiceUnitInfo, ServiceUnitOperation, ServiceFile } from '@/types/systemd'

// 状态管理
const level = ref<'system' | 'user'>('system')
const loading = ref(false)
const serviceUnits = ref<ServiceUnitInfo[]>([])
const operationLoading = ref(false)
const uploadDialogVisible = ref(false)
const uploadLoading = ref(false)
// 搜索相关状态
const searchKeyword = ref('')
const searchField = ref('all')
// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

// 表单数据
const uploadForm = ref<ServiceFile>({
  level: 'system',
  unitName: '',
  content: '',
})

// 可用操作列表
const operations = [
  { value: 'start', label: '启动' },
  { value: 'stop', label: '停止' },
  { value: 'restart', label: '重启' },
  { value: 'enable', label: '启用' },
  { value: 'disable', label: '禁用' },
  { value: 'reload', label: '重载' },
]

// 搜索字段选项
const searchFields = [
  { value: 'all', label: '全部' },
  { value: 'unitFile', label: '服务名称' },
  { value: 'state', label: '加载状态' },
  { value: 'preset', label: '运行状态' },
]

// 计算属性：过滤后的服务列表
const filteredServiceUnits = computed(() => {
  if (!searchKeyword.value.trim()) {
    return serviceUnits.value
  }

  const keyword = searchKeyword.value.toLowerCase().trim()

  return serviceUnits.value.filter((service) => {
    switch (searchField.value) {
      case 'unitFile':
        return service.unitFile.toLowerCase().includes(keyword)
      case 'state':
        return service.state.toLowerCase().includes(keyword)
      case 'preset':
        return service.preset.toLowerCase().includes(keyword)
      case 'all':
      default:
        return (
          service.unitFile.toLowerCase().includes(keyword) ||
          service.state.toLowerCase().includes(keyword) ||
          service.preset.toLowerCase().includes(keyword)
        )
    }
  })
})

// 计算属性：当前页显示的数据
const paginatedServiceUnits = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return filteredServiceUnits.value.slice(startIndex, endIndex)
})

// 计算总条目数（用于分页组件）
const computedTotal = computed(() => {
  return filteredServiceUnits.value.length
})

// 获取服务列表
const fetchServiceUnits = async () => {
  try {
    loading.value = true
    const resp = await getServiceUnits(level.value)
    serviceUnits.value = resp.data
    // 重置分页
    currentPage.value = 1
    totalItems.value = serviceUnits.value.length
    // 清空搜索关键词
    searchKeyword.value = ''
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
      `确定要${operations.find((op) => op.value === operation)?.label}服务 ${unitFile} 吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )

    operationLoading.value = true
    const operationData: ServiceUnitOperation = {
      level: level.value,
      operation: operation,
      unitName: unitFile,
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

// 处理搜索
const handleSearch = () => {
  // 搜索后重置到第一页
  currentPage.value = 1
  console.log('搜索关键词:', searchKeyword.value)
  console.log('搜索字段:', searchField.value)
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = ''
  // 清空搜索后重置到第一页
  currentPage.value = 1
}

// 处理分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1 // 改变每页大小时回到第一页
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
        <div class="left-controls">
          <div class="level-selector">
            <span class="label">运行级别:</span>
            <el-select v-model="level" @change="handleLevelChange" class="level-select">
              <el-option label="系统级" value="system" />
              <el-option label="用户级" value="user" />
            </el-select>
          </div>

          <div class="search-container">
            <span class="label">搜索:</span>
            <el-select v-model="searchField" class="search-field-select" placeholder="选择搜索字段">
              <el-option
                v-for="field in searchFields"
                :key="field.value"
                :label="field.label"
                :value="field.value"
              />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="请输入搜索关键词"
              class="search-input"
              clearable
              @clear="clearSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button
              type="primary"
              @click="handleSearch"
              :disabled="!searchKeyword.trim()"
              class="search-btn"
            >
              搜索
            </el-button>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="success" @click="openUploadDialog" class="action-btn">
            上传服务
          </el-button>
          <el-button @click="fetchServiceUnits" :loading="loading" class="refresh-btn">
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
            <div class="header-info">
              <span class="record-count">共 {{ serviceUnits.length }} 项</span>
              <span v-if="searchKeyword.trim()" class="search-result">
                搜索结果: {{ filteredServiceUnits.length }} 项
              </span>
            </div>
          </div>
        </template>

        <el-table
          :data="paginatedServiceUnits"
          border
          stripe
          :loading="loading"
          class="services-table"
        >
          <!-- 修改序号列：使用计算属性生成正确的分页序号 -->
          <el-table-column width="60" label="#" align="center">
            <template #default="{ $index }">
              {{ $index + 1 + (currentPage - 1) * pageSize }}
            </template>
          </el-table-column>
          <el-table-column prop="unitFile" label="服务名称" min-width="200" />
          <el-table-column prop="state" label="加载状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="row.state === 'loaded' ? 'success' : 'info'" size="small">
                {{ row.state }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="preset" label="运行状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="row.preset === 'enabled' ? 'success' : 'danger'" size="small">
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
                <el-dropdown
                  trigger="click"
                  @command="(command: string) => handleOperateService(row.unitFile, command)"
                >
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

        <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="computedTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            background
            class="custom-pagination"
          />
        </div>
      </el-card>
    </div>

    <!-- 上传服务对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传服务" width="700px" class="upload-dialog">
      <div class="upload-header">
        <el-button @click="handleGetTemplate" type="primary" size="small"> 获取模板 </el-button>
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
          <el-button type="success" @click="handleUploadService" :loading="uploadLoading">
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

.left-controls {
  display: flex;
  align-items: center;
  gap: 30px;
}

.level-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.label {
  font-weight: 500;
  color: #606266;
  white-space: nowrap;
}

.level-select {
  width: 120px;
}

.search-field-select {
  width: 120px;
}

.search-input {
  width: 250px;
}

.search-btn {
  border-radius: 6px;
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

.header-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.record-count {
  font-size: 14px;
  color: #909399;
}

.search-result {
  font-size: 13px;
  color: #409eff;
  font-weight: 500;
}

.services-table {
  border-radius: 4px;
  margin-bottom: 20px;
}

.services-table :deep(.el-table__header th) {
  background-color: #f8f9fa;
  font-weight: 500;
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  background-color: #fafafa;
  border-top: 1px solid #ebeef5;
  border-radius: 0 0 8px 8px;
}

.custom-pagination :deep(.el-pagination__total) {
  font-weight: 500;
}

.custom-pagination :deep(.el-pagination__sizes) {
  margin-left: 15px;
}

.custom-pagination :deep(.el-pagination__jump) {
  margin-left: 15px;
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
@media (max-width: 1200px) {
  .left-controls {
    flex-wrap: wrap;
    gap: 20px;
  }

  .search-container {
    flex-wrap: wrap;
  }

  .search-input {
    width: 200px;
  }
}

@media (max-width: 992px) {
  .control-panel {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }

  .left-controls {
    justify-content: center;
    flex-wrap: wrap;
  }

  .action-buttons {
    justify-content: center;
  }

  .header-info {
    align-items: center;
  }

  .pagination-container {
    padding: 15px 0;
  }
}

@media (max-width: 768px) {
  .systemd-container {
    padding: 15px;
  }

  .control-panel {
    padding: 15px;
  }

  .left-controls {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .search-container {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .search-input {
    width: 100%;
  }

  .card-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .header-info {
    align-items: flex-start;
  }

  .action-buttons-cell {
    flex-wrap: wrap;
    gap: 8px;
  }

  .table-action-btn,
  .more-actions-btn {
    flex: 1;
    min-width: auto;
  }

  .pagination-container {
    padding: 10px 0;
  }

  .custom-pagination :deep(.el-pagination__sizes) {
    margin-left: 8px;
  }

  .custom-pagination :deep(.el-pagination__jump) {
    margin-left: 8px;
  }
}

/* 搜索高亮效果（可选） */
.search-highlight {
  background-color: #ffff00;
  padding: 1px 2px;
  border-radius: 2px;
}
</style>
