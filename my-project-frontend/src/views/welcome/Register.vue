<template>
    <div style="text-align: center;margin: 0 20px;">
          <div style="margin-top: 100px;">
              <div style="font-size: 26px;font-weight: bold;">注册新用户</div>
              <div style="font-size: 14px;color:grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
          </div>
          <div style="margin-top: 50px;">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                <el-form-item prop="username">
                    <el-input type="text" v-model="form.username" placeholder="用户名" maxlength="10">
                        <template #prefix><el-icon  :size="20" ><Avatar /></el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item prop=password>
                    <el-input type="password" v-model="form.password" placeholder="密码"  maxlength="20">
                        <template #prefix><el-icon  :size="20" ><Lock /></el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password_repeat">
                    <el-input type="password" v-model="form.password_repeat" placeholder="重复密码"   maxlength="20">
                        <template #prefix><el-icon  :size="20" ><Lock /></el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="email">
                    <el-input type="email" v-model="form.email" placeholder="请填写电子邮件地址" >
                        <template #prefix><el-icon  :size="20" ><Message /></el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="code">
                    <el-row :gutter="10" style="width: 100%;" >
                        <el-col :span="17"  maxlength="6">
                            <el-input type="email" v-model="form.code" placeholder="请填写验证码">
                                <template #prefix><el-icon  :size="20" ><EditPen /></el-icon></template>
                            </el-input>
                        </el-col>
                        <el-col :span="6">
                            <el-button @click="askCode()" type="success" plain :disabled="!IsEmail || coldTime != 0 ">
                              {{ coldTime > 0 ? `请稍等${coldTime}秒` : "获取验证码"}}
                            </el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
            </el-form>
          </div>
          <div style="margin-top: 80px;">
            <el-button @click="register" style="width: 250px;" type="success" plain >立即注册</el-button>
          </div>
          <div style="margin-top: 20px;">
            <span style="font-size: 14px;line-height: 16px; color:grey">已有账号?</span>
            <el-link type="primary" style="translate: 0 -2px;" @click="router.push('/')">立即登录</el-link>
          </div>
    </div>
</template>
<script setup>
import { get, post } from '@/net';
import { Avatar,Lock,Message,EditPen } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const formRef = ref()
const form = reactive({
    username:'',
    password:'',
    password_repeat:'',
    email:'',
    code:''
})

const coldTime = ref(0)

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if(!/^[a-zA-z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符'))
  }
  else{
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入密码不一致"))
  } else {
    callback()
  }
}
const rules = {
    username:[
    { validator:validatePass, trigger: ['blur','change'] },
    { min: 1, max: 10, message: '用户名长度必须在1到10个字符之间', trigger: ['blur','change']},
    ],
    password:[
        { required:true , message:"请输入密码", trigger: 'blur'},
        { min: 6, max: 20, message: '密码长度必须在6到20个字符之间', trigger: ['blur','change'] },
    ],
    password_repeat:[
    { validator:validatePassword, trigger: ['blur','change'] }
    ],
    email:[
        { required:true , message:"请输入电子邮件地址", trigger: 'blur'},
        {
          type: 'email',
          message: '请输入合法电子邮件地址',
          trigger: ['blur', 'change'],
        },
    ]
}

const IsEmail = ref(false);
const onValidate = (prop,isValid)=>{
    if(prop == 'email')
    {
        IsEmail.value = isValid;
    }
}

function askCode()
{
  if(/^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))
  {
      coldTime.value = 60;
      get(`/api/auth/ask-code?email=${form.email}&type=register`,()=>{
      ElMessage.success("验证码已发送,注意查收") 
    },(message)=>{
      ElMessage.warning(message)
      coldTime.value=0
  })
      setInterval(()=>coldTime.value--,1000);
  }
  else
  {
    ElMessage.warning("请输入正确电子邮件地址")
  }

}

function register(){
  formRef.value.validate((valid)=>{
    if(valid){
      post('/api/auth/register',{...form},()=>{
        ElMessage.success("注册成功")
        router.push('/')
      }
    )
    }
    else
    {
      ElMessage.warning('请完整填写表单内容')
    }
  })
}

</script>
<style  scoped>




</style>
