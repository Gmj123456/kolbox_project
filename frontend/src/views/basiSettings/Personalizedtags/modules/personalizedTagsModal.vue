<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="440"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef" :rules="rules">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="个性化标签"
              prop="tagName"
            >
              <a-input
                v-model:value="model.tagName"
                placeholder="请输入个性化标签"
                @blur="model.tagName = model.tagName?.trim()"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="排序"
              prop="sortCode"
            >
              <a-input-number
                :min="0"
                :precision="0"
                style="width: 100%"
                v-model:value="model.sortCode"
                placeholder="请输入排序"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="备注"
              prop="remark"
            >
              <a-textarea
                v-model:value="model.remark"
                :auto-size="{ minRows: 3, maxRows: 5 }"
                placeholder="请输入备注"
                @blur="model.remark = model.remark?.trim()"
              />
            </a-form-item>
          </a-col>
        </a-row>
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
  span: 5,
};
const wrapperCol = {
  span: 19,
};

const model = reactive({
  id: undefined,
  tagName: '',
  sortCode: undefined,
  remark: '',
});

const rules = {
  tagName: [
    { required: true, message: '请输入个性化标签名称', trigger: 'change' },
    { max: 70, message: '个性化标签名称最多输入70个字符', trigger: 'change' },
  ],
  remark: [{ max: 100, message: '备注不能超过100个字符', trigger: 'change' }],
};

const url = {
  add: '/personalTags/add',
  edit: '/personalTags/edit',
};

const { createMessage } = useMessage();

const resetModel = () => {
  model.id = undefined;
  model.tagName = '';
  model.sortCode = undefined;
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
    close();
    emit('ok');
  
  } catch (error) {
    console.error('handleOk error:', error);
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
  Object.assign(model, {
    id: record.id,
    tagName: record.tagName || '',
    sortCode: record.sortCode,
    remark: record.remark || '',
  });
};

defineExpose({
  open,
});
</script>

<style lang="less" scoped></style>
