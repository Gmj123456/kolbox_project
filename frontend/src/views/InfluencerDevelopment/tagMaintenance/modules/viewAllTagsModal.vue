<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="500"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :footer="null"
    okText="确定"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <s-table
        :rangeSelection="false"	
        :data-source="tags"
        :columns="columns"
        :pagination="false"
        rowKey="id"
        size="small"
        :scroll="{ y: 350 }"
      />
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';

const title = ref('选中标签');
const visible = ref(false);
const tags = ref([]);
const confirmLoading = ref(false);

const columns = [
  {
    title: '#',
    key: 'id',
    dataIndex: 'id',
    width: 50,
  
  
  },
  {
    title: '标签',
    dataIndex: 'tag',
    key: 'tag',
  },
];

function show(data) {
  tags.value = data.split('、').map((item, i) => (
    {
    id: i + 1,
    tag: item,
  }));
  visible.value = true;
}

function handleOk() {
  close();
}

function close() {
  visible.value = false;
  tags.value = [];
}

function handleCancel() {
  close();
}

defineExpose({
  show,
  close,
});
</script>

<style lang="less" scoped>
:deep(.ant-select-selection--multiple) {
  padding-bottom: 0px !important;
}
</style>
