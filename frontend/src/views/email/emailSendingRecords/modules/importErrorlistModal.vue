<template>
  <a-modal
    :title="title"
    :width="600"
    :footer="null"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <vxe-table :data="dataSourceList" size="small" height="400">
        <vxe-table-column type="seq" width="50"></vxe-table-column>
        <vxe-table-column field="username" title="网红账号"></vxe-table-column>
        <vxe-table-column field="email" title="网红邮箱"></vxe-table-column>
        <vxe-table-column field="platform" title="平台"></vxe-table-column>
        <vxe-table-column field="errors" title="失败信息">
          <template #default="{ row }">
            <div v-for="(error, index) in row.errors" :key="index">{{ error }}</div>
          </template>
        </vxe-table-column>
      </vxe-table>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';

const title = ref("导入失败信息");
const visible = ref(false);
const confirmLoading = ref(false);
const dataSourceList = ref([]);

function handleOk() {
  visible.value = false;
  dataSourceList.value = [];
}

function show(data) {
  visible.value = true;
  dataSourceList.value = data;
}

function handleCancel() {
  visible.value = false;
  dataSourceList.value = [];
}

defineExpose({
  show,
});
</script>

<style lang="less" scoped></style>
