<template>
      <div style="width: 100vw;height: 100vh;overflow: hidden; display: flex" >
      <div style="flex: 1;background-color: antiquewhite">
        <el-image style="width: 100%;height: 100%;object-fit: cover;"
            src="https://img2.baidu.com/it/u=2316341716,305155432&fm=253&fmt=auto&app=120&f=JPEG?w=1422&h=800"/>
      </div>
      <div class="welcome-title">
        <div style="    font-size: 30px;font-weight: bold;">欢迎来到学习平台</div>
        <div style="margin-top: 10px;">今朝有酒今朝醉 tomorrow is another day</div>
        <div style="margin-top: 10px;"> 压力大，欠款大，花费大，年龄大，传说中的前途远大。</div>
      </div>
      <div style="width: 400px;background-color: var(--el-bg-color);text-align: center;z-index: 1;">
        <transition name="el-fade-in-linear">
          <RouterView/>
        </transition>
       
      </div>
  </div>
</template>
<script setup>

</script>
<style scoped>
.welcome-title{
    position: absolute;
    bottom: 30px;
    left: 30px;
    text-shadow: 0 0 10px white;

}
</style>