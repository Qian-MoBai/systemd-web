<script setup lang="ts">
import { getServiceUnits } from '@/api/systemd'
import type { ServiceUnitInfo } from '@/types/systemd'

/** 运行级别 */
const level = ref('system')
/** 服务列表 */
const serviceUnits = ref<ServiceUnitInfo[]>([])
const changeLevelHandler = async () => {
  const resp = await getServiceUnits(level.value)
  serviceUnits.value = resp.data
}
onMounted(async () => {
  changeLevelHandler()
})
</script>
<template>
  <el-container>
    <el-header>
      <el-form :inline="true" label-suffix=": ">
        <el-form-item label="运行级别">
          <el-select
            v-model="level"
            style="width: calc((100 / 16) * 1rem)"
            @change="changeLevelHandler"
          >
            <el-option label="系统级" value="system" />
            <el-option label="用户级" value="user" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-header>
    <el-main>
      <el-table :data="serviceUnits" border stripe>
        <el-table-column type="index" width="50" />
        <el-table-column prop="unitName" label="服务名称" />
        <el-table-column prop="description" label="服务描述" />
        <el-table-column prop="loadState" label="服务加载状态" />
        <el-table-column prop="activeState" label="服务运行状态" />
        <el-table-column prop="subState" label="服务子状态" />
      </el-table>
    </el-main>
  </el-container>
</template>

<style scoped></style>
