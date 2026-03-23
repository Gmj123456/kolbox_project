<template>
  <a-modal
    v-model:visible="visible"
    :title="title"
    :width="600"
    :footer="null"
    :confirmLoading="confirmLoading"
    cancelText="关闭"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <a-table
        ref="tableRef"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="list"
        :loading="confirmLoading"
        :scroll="{ y: 500, x: '100%' }"
      />
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const title = ref('标签列表');
const visible = ref(false);
const confirmLoading = ref(false);
const list = ref<any[]>([]);
const tableRef = ref();

const columns = [
  {
    title: '序号',
    dataIndex: 'rowIndex',
    key: 'rowIndex',
    width: 50,
    customRender: (_: any, __: any, index: number) => index + 1,
  },
  {
    title: '社媒标签',
    align: 'left',
    dataIndex: 'socialMediaTag',
  },
  {
    title: '标签类目',
    align: 'left',
    dataIndex: 'tagCategoryName',
  },
  {
    title: 'tagName',
    align: 'left',
    dataIndex: 'tagName',
  },
];

function detail(data: any[]) {
  list.value = Array.isArray(data) ? data : [];
  visible.value = true;
}

function close() {
  visible.value = false;
  list.value = [];
}

function handleCancel() {
  close();
}

defineExpose({
  detail,
  close,
});
</script>

<style lang="less" scoped></style>