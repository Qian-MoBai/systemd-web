<script setup lang="ts">
import { useRouter } from 'vue-router'
/** 路由 */
const router = useRouter()
/** 标签页 */
const tabs = ['Systemd']
/** 当前标签页 */
const activeName = ref('')
/** 切换标签页 */
const tabChangeHandler = () => {
  router.push({ name: activeName.value })
}
onMounted(() => {
  activeName.value = tabs[0]!
  tabChangeHandler()
})
</script>

<template>
  <el-container>
    <el-header>
      <h1>Systemd Web</h1>
    </el-header>
    <el-main>
      <el-tabs v-model="activeName" @tab-change="tabChangeHandler">
        <el-tab-pane v-for="(item, index) in tabs" :key="index" :label="item" :name="item" />
      </el-tabs>
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped lang="scss">
// 样式重写
:deep(.el-tabs__nav) {
  width: 100%;
  justify-content: space-evenly;
  .el-tabs__item {
    width: 100%;
    font-size: 2rem;
  }
}
</style>
