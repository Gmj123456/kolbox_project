<template>
  <a-modal
    v-model:visible="visible"
    :title="title"
    :width="450"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="rules">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="文章标题"
          name="title"
        >
          <a-input
            v-model:value="model.title"
            placeholder="请输入文章标题"
            @blur="model.title = model.title?.trim()"
          />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="文章链接"
          name="url"
        >
          <a-input
            v-model:value="model.url"
            placeholder="请输入文章链接"
            @blur="model.url = model.url?.trim()"
          />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注"
          name="remark"
        >
          <a-input
            v-model:value="model.remark"
            placeholder="请输入备注"
            @blur="model.remark = model.remark?.trim()"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';

const emit = defineEmits(['ok', 'close']);

const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);

const labelCol = {
  xs: { span: 24 },
  sm: { span: 4 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 20 },
};

const model = reactive({
  id: undefined,
  title: '',
  url: '',
  remark: '',
});

const rules = {
  title: [{ required: true, message: '请输入文章标题!', trigger: 'change' }],
  url: [{ required: true, message: '请输入文章链接!', trigger: 'change' }],
  remark: [{ max: 255, message: '备注不能超过255个字符', trigger: 'change' }],
};

const url = {
  add: '/promotionalArticle/add',
  edit: '/promotionalArticle/edit',
};

const { createMessage } = useMessage();

const resetModel = () => {
  model.id = undefined;
  model.title = '';
  model.url = '';
  model.remark = '';
};

const close = () => {
  visible.value = false;
  resetModel();
  formRef.value?.resetFields();
  emit('close');
};

const handleOk = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  confirmLoading.value = true;
  try {
    const httpurl = model.id ? url.edit : url.add;
    const method = model.id ? 'put' : 'post';
    const res = await defHttp.request({
      url: httpurl,
      method,
      data: model,
    },{isTransformResponse:false});
    if (res?.success) {
      createMessage.success(res.message || '操作成功');
      emit('ok');
      close();
    } else {
      createMessage.warning(res?.message || '操作失败');
    }
  } catch (error) {
    console.error('informationManagementModal handleOk error:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
  }
};

const handleCancel = () => {
  close();
};

const open = (record = {}) => {
  visible.value = true;
  model.id = record.id;
  model.title = record.title || '';
  model.url = record.url || '';
  model.remark = record.remark || '';
};

defineExpose({
  open,
});
</script>

<style lang="less" scoped>
</style>