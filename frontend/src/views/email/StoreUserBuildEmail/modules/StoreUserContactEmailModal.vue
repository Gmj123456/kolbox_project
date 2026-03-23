<template>
  <a-modal 
    :title="title" 
    :width="400" 
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
              label="建联邮箱"
              name="contactEmail"
            >
              <a-input v-model:value="model.contactEmail" placeholder="请输入建联邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="建联邮箱名称"
              name="emailDisplayName"
            >
              <a-input v-model:value="model.emailDisplayName" placeholder="请输入建联邮箱名称" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱状态"
              name="emailStatus"
            >
              <a-select v-model:value="model.emailStatus" placeholder="请选择邮箱状态">
                <a-select-option :value="1">
                  可用
                </a-select-option>
                <a-select-option :value="0">
                  不可用
                </a-select-option>
              </a-select>
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
  emailStatus: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 7 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 16 },
};

const validatorRules = {
  contactEmail: [
    {
      required: true,
      message: '请输入建联邮箱!',
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
      message: '请输入建联邮箱名称!',
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
  edit({emailStatus:1});
}

function edit(record) {
  Object.assign(model.value, record);
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
    
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (!model.value.id) {
      httpurl = url.add;
      method = 'post';
    } else {
      httpurl = url.edit;
      method = 'put';
    }
    model.value.type = 0
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
