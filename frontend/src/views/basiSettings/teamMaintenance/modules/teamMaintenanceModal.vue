<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="400"
    :confirmLoading="confirmLoading"
    cancelText="关闭"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="小组名称" prop="groupName">
          <a-input v-model:value="model.groupName" placeholder="请输入小组名称" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="组长" prop="leaderUserId">
          <a-select
            v-model:value="model.leaderUserId"
            show-search
            option-filter-prop="label"
            allowClear
            placeholder="请选择组长"
          >
            <a-select-option
              v-for="(user, key) in celebrityConsultantList"
              :key="key"
              :value="user.id"
              :label="user.username"
            >
              {{ user.username }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="描述" prop="description">
          <a-textarea
            v-model:value="model.description"
            placeholder="请输入描述"
            :auto-size="{ minRows: 3, maxRows: 5 }"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态" prop="status">
          <a-select v-model:value="model.status" placeholder="请选择状态">
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <!-- <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="排序" prop="sort">
          <a-input-number v-model:value="model.sort" placeholder="请输入排序" :min="0" style="width: 100%" />
        </a-form-item> -->
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { queryAllCelebrityCounselorApi } from '/@/api/sys/systemCommon';

const emit = defineEmits(['ok']);
const { createMessage } = useMessage();

const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);
const celebrityConsultantList = ref([]);

const model = ref({
  id: undefined,
  groupName: '',
  leaderUserId: undefined,
  leaderUserName: '',
  description: '',
  status: 1,
  // sort: 0,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 19 },
};

const validatorRules = {
  groupName: [
    { required: true, message: '请输入小组名称', trigger: 'change' },
    { max: 50, message: '小组名称不能超过50个字符', trigger: 'change' },
  ],
  leaderUserId: [{ required: true, message: '请选择组长', trigger: 'change' }],
  description: [{ max: 500, message: '描述不能超过500个字符', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
};

const resetForm = () => {
  model.value = {
    id: undefined,
    groupName: '',
    leaderUserId: undefined,
    leaderUserName: '',
    description: '',
    status: 1,
    // sort: 0,
  };
};

const loadCelebrityConsultant = async () => {
  const res = await queryAllCelebrityCounselorApi({});
  celebrityConsultantList.value = Array.isArray(res) ? res : [];
};

const open = async (record = {}) => {
  resetForm();
  await loadCelebrityConsultant();
  Object.assign(model.value, record);
  if (record.leaderUserName && !record.leaderUserId) {
    const user = celebrityConsultantList.value.find((u) => u.username === record.leaderUserName);
    if (user) model.value.leaderUserId = user.id;
  }
  title.value = record.id ? '编辑' : '新增';
  visible.value = true;
  nextTick(() => {
    formRef.value?.clearValidate();
  });
};

const close = () => {
  visible.value = false;
};

const handleOk = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  confirmLoading.value = true;
  const isEdit = !!model.value.id;
  const url = isEdit ? '/counselor/celebrityCounselorGroup/edit' : '/counselor/celebrityCounselorGroup/add';
  const method = isEdit ? 'put' : 'post';
  const leaderUser = celebrityConsultantList.value.find((u) => u.id === model.value.leaderUserId);
  try {
    await defHttp.request({
      url,
      method,
      data: {
        id: model.value.id,
        groupName: model.value.groupName,
        leaderUserId: model.value.leaderUserId,
        leaderUserName: leaderUser?.username ?? model.value.leaderUserName,
        description: model.value.description,
        status: model.value.status,
        // sort: model.value.sort ?? 0,
      },
    });
    // createMessage.success(isEdit ? '编辑成功' : '新增成功');
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
