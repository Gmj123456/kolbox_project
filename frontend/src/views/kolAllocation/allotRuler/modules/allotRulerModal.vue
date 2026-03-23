<template>
  <a-modal
    :title="title"
    :width="400"
    v-model:visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-item label="平台" name="platformType">
          <a-select v-model:value="model.platformType" :disabled="disabled">
            <a-select-option :value="0">IG</a-select-option>
            <a-select-option :value="1">YT</a-select-option>
            <a-select-option :value="2">TK</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="规则档位" name="ruleLevel">
          <a-input v-model:value="model.ruleLevel" />
        </a-form-item>
        <a-form-item label="最小值" name="minNum">
          <a-input-number
            :max="9999999999"
            style="width: 100%"
            v-model:value="model.minNum"
          />
        </a-form-item>
        <a-form-item label="最大值" name="maxNum">
          <a-input-number
            :max="9999999999"
            style="width: 100%"
            v-model:value="model.maxNum"
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

const { createMessage } = useMessage();

const title = ref('操作');
const visible = ref(false);
const disabled = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 16 },
};

const model = reactive({
  id: undefined,
  platformType: undefined,
  ruleLevel: '',
  minNum: undefined,
  maxNum: undefined,
});

const validateMax = async (_rule, value) => {
  if (value < model.minNum) {
    throw new Error('最大值不能低于最小值');
  }
};

const validateMin = async (_rule, value) => {
  if (value > model.maxNum) {
    throw new Error('最小值不能高于最大值');
  }
};

const rules = {
  platformType: [{ required: true, message: '请选择平台', trigger: 'change' }],
  ruleLevel: [{ required: true, message: '请输入规则档位', trigger: 'change' }],
  minNum: [
    { required: true, message: '请输入最小值', trigger: 'change' },
    { validator: validateMin, trigger: 'change' },
  ],
  maxNum: [
    { required: true, message: '请输入最大值', trigger: 'change' },
    { validator: validateMax, trigger: 'change' },
  ],
};

const url = {
  add: '/tiktokcelebrityrule/add',
  edit: '/tiktokcelebrityrule/edit',
};

function resetModel() {
  model.id = undefined;
  model.platformType = undefined;
  model.ruleLevel = '';
  model.minNum = undefined;
  model.maxNum = undefined;
}

function close() {
  visible.value = false;
  resetModel();
  formRef.value?.resetFields();
  emit('close');
}

function add() {
  edit({});
}

function edit(record) {
  if (Object.keys(record).length > 0) {
    disabled.value = true;
  } else {
    disabled.value = false;
  }
  
  resetModel();
  if (record && record.id) {
    Object.assign(model, record);
  }
  visible.value = true;
}

async function handleOk() {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
  } catch {
    return;
  }

  confirmLoading.value = true;
  
  const httpurl = model.id ? url.edit : url.add;
  const method = model.id ? 'put' : 'post';
  
  try {
    const res = await defHttp.request({
      url: httpurl,
      method,
      data: model,
    });
    
    if (res) {
     
      emit('ok');
      close();
    } 
  } catch (error) {
    console.error('操作失败:', error);
 
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

<style lang="less" scoped></style>
