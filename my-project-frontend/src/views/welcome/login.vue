<template>
        <div style="text-align: center;margin: 0 20px;">
            <div style="margin-top: 150px;">
              <div style="font-size: 26px;font-weight: bold;">登录</div>
              <div style="font-size: 14px;color:grey">请输入用户名与密码进行登录操作</div>
          </div>
          <div style="margin-top: 30px;">
            <el-form :model="form" :rules="rule" ref="formRef">
              <el-form-item prop="username">
                <el-input v-model="form.username" type="text" placeholder="用户名/邮箱" style="width: 25vw;">
                  <template #prefix><el-icon  :size="20" ><Avatar /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="form.password"  type="password" placeholder="密码" style="width: 25vw;margin-top: 10px;">
                  <template #prefix><el-icon  :size="20" ><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
            </el-form>
                <el-row style="margin-top: 5px;">
                  <el-col :span="12" style="text-align: left;">
                    <el-form-item prop="remember">
                      <el-checkbox v-model="form.remember" label="记住我" size="large" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12" style="text-align: right;">
                    <el-link @click="router.push('/reset')">忘记密码?</el-link>
                  </el-col>
                </el-row>
          </div>
          <div style="margin-top: 40px;">
            <ElButton @click="userLogin" type="success" style="width: 200px;" plain>立即登录</ElButton>
          </div>
          <ElDivider>
            <span style="color:grey;font-size: 13px;">没有账号</span>
          </ElDivider>
          <div style="margin-top: 40px;">
            <ElButton @click="router.push('/register')" type="warning" style="width: 200px;" plain>注册账号</ElButton>
          </div>
        </div>
</template>
<script setup>
import { login } from '@/net';
import { Avatar,Lock } from '@element-plus/icons-vue';
import { ElButton, ElDivider, ElMessage } from 'element-plus';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter()
const form = reactive({
  username:'',
  password:'',
  remember:false
})
const formRef = ref()
const rule = {
  username:[
    {required:'true',message:"请输入用户名或邮箱"}
  ],
  password:[
    {required:'true',message:"请输入密码"}
  ]
}
const userLogin = () =>{
  formRef.value.validate((valid)=>{
    if(valid)
    {
      login(form.username,form.password,form.remember,()=>{router.push('/index')})
    }
  })
}
</script>
<style  scoped>


</style>
