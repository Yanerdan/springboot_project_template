<script setup>
import { useDark, useToggle } from '@vueuse/core';

useDark({
  selector:'html',
  attribute:'class',
  valueDark:'dark',
  valueLight:'light'
})
useDark({
  onChanged(dark){useToggle(dark)}
})
</script>

<template>
  <RouterView />
</template>

<style scoped>

</style>
