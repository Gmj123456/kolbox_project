<template>
  <a-modal
    v-model:visible="visible"
    :title="title"
    :width="400"
    :footer="null"
    :confirmLoading="confirmLoading"
    cancelText="关闭"
    @cancel="handleCancel"
  >
    <div style="height: 250px; overflow-y: auto">
      <div v-for="(item, index) in dataSourceList" :key="index">
        {{ item }}
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const title = ref('导入失败信息');
const visible = ref(false);
const confirmLoading = ref(false);
const dataSourceList = ref<string[]>([]);

function show(list: string[]) {
  dataSourceList.value = Array.isArray(list) ? list : [];
  visible.value = true;
}

function close() {
  visible.value = false;
  dataSourceList.value = [];
}

function handleCancel() {
  close();
}

defineExpose({
  show,
  close,
});
</script>

<style lang="less" scoped></style>
