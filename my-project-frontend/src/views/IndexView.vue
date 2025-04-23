<template>
    <div>欢迎进入学习平台</div>
    <div>
        <ElButton @click="userLogout" type="danger" plain>退出登录</ElButton>
    </div>
</template>
<script setup >

import { logout } from '@/net';
import { ElButton, ElMessage } from 'element-plus';
import {  useRouter } from 'vue-router'
const router = useRouter()
function userLogout()
{
    logout(()=>{
        router.push('/')
    })
}
</script>
<style scoped>




</style>
