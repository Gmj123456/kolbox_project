<template>
  <div>
    <a-modal
      :title="title"
      :maskClosable="false"
      :width="400"
      :open="visible"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
    >
      <a-spin :spinning="confirmLoading">
        <s-table
          size="small"
          :columns="columns"
          :pagination="false"
          :data-source="dataSource"
          bordered
        >
          <template #bodyCell="{ text, record, index, column }">
            <template v-if="column.key === 'rowIndex'">
              {{ index + 1 }}
            </template>
            <template v-if="column.key === 'uniqueId'">
              <span v-if="platformType == 1">
                {{ record.username }}
              </span>
              <span v-if="platformType == 2">
                {{ record.uniqueId }}
              </span>
            </template>
          </template>
        </s-table>
      </a-spin>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// 状态
const title = ref('失败条数');
const visible = ref(false);
const confirmLoading = ref(false);
const platformType = ref('');
const dataSource = ref<any[]>([]);

// 表格列
const columns = [
  {
    title: '#',
    key: 'rowIndex',
    width: 60,
    align: 'center',
  },
  {
    title: '账号',
    key: 'uniqueId',
    width: 100,
  },
];

// 关闭
function close() {
  emit('ok');
  visible.value = false;
}

// 取消
function handleCancel() {
  close();
}

// 确定
function handleOk() {
  visible.value = false;
  emit('ok');
}

// 显示
function show(platformTypeValue: string, data: any[]) {
  platformType.value = platformTypeValue;
  dataSource.value = data;
  visible.value = true;
}

// 暴露方法
defineExpose({
  show,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
}>();
</script>
