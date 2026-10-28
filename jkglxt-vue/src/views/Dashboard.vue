<template>
  <div>
    <h3>欢迎使用健康管理系统</h3>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  const user = window.sessionStorage.getItem('user')
  if (!user) {
    router.push('/login')
  }
})
</script>

<style scoped>
h3 {
  color: #333;
}
</style>