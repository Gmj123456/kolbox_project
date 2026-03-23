<template>
  <a-modal
    :title="title"
    :width="500"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
        <a-row>
          <a-col :span="24">
            <a-form-item label="code" name="code">
              <a-textarea
                v-model:value="model.code"
                placeholder="请输入code"
             
                show-count
              />
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
  code: undefined,
});

const validatorRules = {
  code: [
    {
      required: true,
      message: '请输入code!',
      trigger: 'change'
    }
  ],
};

const url = {
  add: "/email/emailStationery/add",
  edit: "/email/emailStationery/edit",
};

function add() {
  edit({});
}

function edit(record) {
  Object.assign(model.value, record);
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
