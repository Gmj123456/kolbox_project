<template>
  <a-modal :title="title" :width="500" :open="visible" :confirmLoading="confirmLoading" @ok="handleOk"
    @cancel="handleCancel" cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="rules" :labelCol="labelCol" :wrapperCol="wrapperCol">

        <a-form-item label="姓名" name="name">
          <a-input v-model:value="model.name" />
        </a-form-item>
        <a-form-item label="英文名" name="nameEn">
          <a-input v-model:value="model.nameEn" />
        </a-form-item>
        <a-form-item label="微信名" name="nameWechat">
          <a-input v-model:value="model.nameWechat" />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="model.phone" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="model.email" />
        </a-form-item>
        <a-form-item label="微信号" name="wechatNum">
          <a-input v-model:value="model.wechatNum" />
        </a-form-item>
        <a-form-item label="职务" name="job">
          <a-input v-model:value="model.job" />
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();
const emit = defineEmits(['close', 'ok']);

const formRef = ref(null);
const visible = ref(false);
const confirmLoading = ref(false);
const title = ref('操作');
const model = ref({
  id: undefined,
  name: undefined,
  nameEn: undefined,
  nameWechat: undefined,
  phone: undefined,
  email: undefined,
  wechatNum: undefined,
  job: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 16 },
};

const rules = {
  name: [
    { required: true, message: '请输入姓名!', trigger: 'blur' }
  ],
  nameEn: [
    { required: true, message: '请输入英文名!', trigger: 'blur' }
  ],
  nameWechat: [
    { required: true, message: '请输入微信名!', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号!', trigger: 'blur' },
    { pattern: /^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/, message: '请输入正确手机号!', trigger: 'blur' }
  ],
  email: [
    { required: true, type: 'email', message: '请输入正确的邮箱!', trigger: 'blur' }
  ],
  wechatNum: [
    { required: true, message: '请输入微信号!', trigger: 'blur' }
  ],
  job: [
    { required: true, message: '请输入职务!', trigger: 'blur' }
  ],
};

const url = {
  add: '/employeeSecurity/add',
  edit: '/employeeSecurity/edit',
};

function add() {
  edit({});
}

function edit(record) {
  model.value = Object.assign({}, record);
  visible.value = true;
  
}

function close() {
  emit('close');
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    
    const httpurl = model.value.id ? url.edit : url.add;
    const method = model.value.id ? 'put' : 'post';
    
    const formData = {
      id: model.value.id,
      name: model.value.name,
      nameEn: model.value.nameEn,
      nameWechat: model.value.nameWechat,
      phone: model.value.phone,
      email: model.value.email,
      wechatNum: model.value.wechatNum,
      job: model.value.job,
    };

    const res = await defHttp[method]({ url: httpurl, data: formData }, { isTransformResponse: false });
    
    if (res.success) {
      createMessage.success(res.message);
      emit('ok');
      close();
    } else {
      createMessage.warning(res.message);
    }
  } catch (error) {
    if (error.errorFields) {
      // 表单验证错误
      return;
    }
    console.error('handleOk error:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

defineExpose({
  add,
  edit,
});
</script>

<style lang="less" scoped>

</style>