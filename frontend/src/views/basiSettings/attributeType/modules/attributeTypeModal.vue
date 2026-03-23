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
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类型编码" prop="typeCode">
          <a-input v-model:value="model.typeCode" placeholder="请输入类型编码" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类型名称" prop="typeName">
          <a-input v-model:value="model.typeName" placeholder="请输入类型名称" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注" prop="remark">
          <a-textarea
            v-model:value="model.remark"
            placeholder="请输入备注"
            :auto-size="{ minRows: 3, maxRows: 5 }"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { nextTick, reactive, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';

const emit = defineEmits(['ok']);
const { createMessage } = useMessage();

const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);

const model = reactive({
  id: undefined,
  typeCode: '',
  typeName: '',
  remark: '',
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
  typeCode: [
    { required: true, message: '请输入类型编码', trigger: 'change' },
    { max: 20, message: '类型编码不能超过20个字符', trigger: 'change' },
  ],
  typeName: [
    { required: true, message: '请输入类型名称', trigger: 'change' },
    { max: 10, message: '类型名称不能超过10个字符', trigger: 'change' },
  ],
  remark: [{ max: 500, message: '备注不能超过500个字符', trigger: 'change' }],
};

const resetForm = () => {
  model.id = undefined;
  model.typeCode = '';
  model.typeName = '';
  model.remark = '';
};

const open = (record = {}) => {
  resetForm();
  Object.assign(model, record);
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
  const isEdit = !!model.id;
  const url = isEdit ? '/kol/kolAttributeType/edit' : '/kol/kolAttributeType/add';
  const method = isEdit ? 'put' : 'post';
  try {
    await defHttp.request({
      url,
      method,
      data: {
        ...model,
        type: 2,
      },
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
