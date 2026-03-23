<template>
  <a-modal 
    title="修改邮箱密码" 
    :width="440" 
    v-model:open="visible" 
    :confirmLoading="confirmLoading" 
    switchFullscreen
    @ok="handleOk" 
    @cancel="handleCancel" 
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱"
              name="contactEmail"
            >
             {{ model.contactEmail }}
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱名称"
              name="emailDisplayName"
            >
              {{ model.emailDisplayName }}
            </a-form-item>
          </a-col>
          <a-col :span="24" >
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱密码"
              name="emailPassword"
            >
              <a-input-password  v-model:value="model.emailPassword" placeholder="请输入邮箱密码" />
            </a-form-item>
          </a-col>
          <a-col :span="24"  >
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="确认密码"
              name="emailPasswordConfirm"
            >
              <a-input-password  v-model:value="model.emailPasswordConfirm" placeholder="请输入确认密码" />
            </a-form-item>
          </a-col>
      
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);
const title = ref("操作");
const visible = ref(false);
const confirmLoading = ref(false);

const model = ref({
  contactEmail: undefined,
  emailDisplayName: undefined,
  emailPassword: undefined,
  emailStatus: undefined,
  emailPasswordConfirm: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 4},
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 20 },
};

const validatorRules = {
  contactEmail: [
    {
      required: true,
      message: '请输入邮箱!',
      trigger: 'change'
    },
    { 
      pattern: /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/, 
      message: '请输入正确格式的邮箱' 
    }
  ],
  emailDisplayName: [
    {
      required: true,
      message: '请输入邮箱名称!',
      trigger: 'change'
    }
  ],
  emailPassword: [
    {
      required: true,
      message: '请输入邮箱密码!',
      trigger: 'change'
    }
  ],
  emailPasswordConfirm: [
    {
      required: true,
      message: '请输入确认密码!',
      trigger: 'change'
    }
  ],
  emailStatus: [
    {
      required: true,
      message: '请选择邮箱状态!',
      trigger: 'change'
    }
  ],
};

const url = {
  add: "/storeUserContactEmail/add",
  edit: "/storeUserContactEmail/edit",
};

function add() {
  edit({});
}

function edit(record) {
  Object.assign(model.value, record);
  model.value.emailPassword = undefined;
  model.value.emailPasswordConfirm = undefined;
  title.value = record.id ? "编辑" : "新增";
  visible.value = true;
}

function close() {
  emit('close');
  model.value = {};
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    if (model.value.emailPassword !== model.value.emailPasswordConfirm) {
      createMessage.error('两次输入的密码不一致!');
      return;
    }
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (!model.value.id) {
      httpurl = url.add;
      method = 'post';
      delete model.value.emailPasswordConfirm;
    } else {
      httpurl = url.edit;
      method = 'put';
      delete model.value.emailPasswordConfirm;

    }
    model.value.type = 1
    const res = await defHttp.request({ url: httpurl, method, params: model.value });
    emit('ok');
    close();
    
  } catch (error) {
    console.error("提交失败:", error);
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
