<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="600"
    :confirmLoading="confirmLoading"
    cancelText="关闭"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <a-table
        :row-selection="rowSelection"
        size="small"
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="false"
        :scroll="{ y: 400 }"
      />
    </a-spin>
  </a-modal>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { queryAllCelebrityCounselorApi } from '/@/api/sys/systemCommon';

const emit = defineEmits(['ok']);
const { createMessage } = useMessage();

const title = ref('添加组员');
const visible = ref(false);
const confirmLoading = ref(false);
const groupId = ref('');
const groupName = ref('');
const dataSource = ref([]);
const selectedRowKeys = ref([]);
const selectedRows = ref([]);

const columns = [
  {
    title: '#',
    dataIndex: 'index',
    key: 'index',
    width: 60,
    align: 'center',
    customRender: ({ index }) => index + 1,
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '简称',
    dataIndex: 'shortName',
    key: 'shortName',
  },
];

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys, rows) => {
    selectedRowKeys.value = keys;
    selectedRows.value = rows;
  },
}));

const open = async (record) => {
  groupId.value = record.id;
  groupName.value = record.groupName || '';
  title.value = `添加组员 - ${groupName.value}`;
  selectedRowKeys.value = [];
  selectedRows.value = [];
  const [consultantRes, membersRes] = await Promise.all([
    queryAllCelebrityCounselorApi({}),
    defHttp.get({
      url: '/counselor/celebrityCounselorGroup/getMembers',
      params: { groupId: record.id },
    }),
  ]);
  dataSource.value = Array.isArray(consultantRes) ? consultantRes : [];
  const members = Array.isArray(membersRes) ? membersRes : membersRes?.records ?? membersRes?.result ?? [];
  const memberUserIds = members.map((m) => m.memberUserId ?? m.userId ?? m.id).filter(Boolean);
  selectedRowKeys.value = memberUserIds;
  selectedRows.value = dataSource.value.filter((item) => memberUserIds.includes(item.id));
  visible.value = true;
};

const close = () => {
  visible.value = false;
};

const handleOk = async () => {
  if (selectedRows.value.length === 0) {
    createMessage.warning('请选择要添加的组员');
    return;
  }
  confirmLoading.value = true;
  try {
    const members = selectedRows.value.map((row) => ({
      memberUserId: row.id,
      memberUserName: row.username,
    }));
    await defHttp.post({
      url: '/counselor/celebrityCounselorGroup/addMembers',
      params: { groupId: groupId.value },
      data: members,
    });
  
    emit('ok');
    close();
  } finally {
    confirmLoading.value = false;
  }
};

const handleCancel = () => {
  close();
};

defineExpose({
  open,
});
</script>

<style scoped></style>
