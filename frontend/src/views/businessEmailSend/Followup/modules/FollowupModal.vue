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
    :maskClosable="false"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef"   :model="model" :rules="validatorRules" layout="vertical">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item name="categoryName" label="跟进名称">
              <a-input 
                v-model:value="model.categoryName" 
                placeholder="请输入跟进名称" 
                :maxlength="50"
                show-count
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item name="remark" label="备注">
              <a-input 
                v-model:value="model.remark" 
                placeholder="请输入备注" 
                :maxlength="200"
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
  categoryName: undefined,
  remark: undefined,
});

const validatorRules = {
  categoryName: [
    { required: true, message: "请输入跟进名称" },
  ],
};

const url = {
  add: "/email/emailTemplateCategory/add",
  edit: "/email/emailTemplateCategory/edit",
};

function add() {
  edit({});
}

async function edit(record) {
  Object.assign(model.value, record);
  title.value = record.id ? "编辑" : "新增";
  visible.value = true;
}

function close() {
  emit("close");
  model.value = {};
  formRef.value.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    
    let httpurl = "";
    let method = "";
    if (!model.value.id) {
      httpurl = url.add;
      method = "post";
    } else {
      httpurl = url.edit;
      method = "put";
    }

    const params = {
      ...model.value,
      templateType:1,
    };

    const res = await defHttp.request({ url: httpurl, method, params });
    emit("ok");
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
<style>
/* Minimal style or keep as-is if none is needed */
</style>
