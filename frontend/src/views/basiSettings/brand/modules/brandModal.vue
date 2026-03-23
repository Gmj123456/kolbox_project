<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="450"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef" :rules="rules">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="品牌名称"
          prop="brandName"
        >
          <a-input
            v-model:value="model.brandName"
            placeholder="请输入品牌名称"
            @blur="model.brandName = model.brandName?.trim()"
          />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="品牌描述"
          prop="description"
        >
          <a-textarea
            v-model:value="model.description"
            :auto-size="{ minRows: 3, maxRows: 5 }"
            placeholder="请输入品牌描述"
            @blur="model.description = model.description?.trim()"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注" prop="remark">
          <a-textarea
            v-model:value="model.remark"
            :auto-size="{ minRows: 3, maxRows: 5 }"
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
  brandName: '',
  description: '',
  remark: '',
});

const rules = {
  brandName: [
    { required: true, message: '请输入品牌名称', trigger: 'change' },
    { max: 70, message: '品牌名称最多输入70个字符', trigger: 'change' },
  ],
  description: [{ max: 500, message: '品牌描述不能超过500个字符', trigger: 'change' }],
  remark: [{ max: 100, message: '备注不能超过100个字符', trigger: 'change' }],
};

const url = {
  add: '/kolBrand/add',
  edit: '/kolBrand/edit',
};

const { createMessage } = useMessage();

const resetModel = () => {
  model.id = undefined;
  model.brandName = '';
  model.description = '';
  model.remark = '';
};

const close = () => {
  resetModel();
  formRef.value?.resetFields();
  visible.value = false;
  emit('close');
};

const handleOk = async () => {
  try {
    await formRef.value?.validate();
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
    });
    if (res) {
      
      close();
      emit('ok');
    } 
  } catch (error) {
    console.error('handleOk error:', error);
    // createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
  }
};

const handleCancel = () => {
  close();
};

const open = (record = {}) => {
  visible.value = true;
  Object.assign(model, {
    id: record.id,
    brandName: record.brandName || '',
    description: record.description || '',
    remark: record.remark || '',
  });
};

defineExpose({
  open,

});
</script>

<style lang="less" scoped></style>
