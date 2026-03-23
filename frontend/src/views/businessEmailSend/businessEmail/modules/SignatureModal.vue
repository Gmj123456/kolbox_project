<template>
  <a-modal
    :title="title"
    :width="1000"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    centered
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-item name="signatureContentOriginal">
              <a-col :span="24">
                <a-form-item>
                  <div
                    class="templateContentBox"
                    style="padding: 10px; border: 1px solid #d9d9d9; border-radius: 4px"
                  >
                    <div style="border-bottom: 1px solid rgb(217, 217, 217);">
                      <a-input
                      style="
                        font-size: 14px !important;
                        font-family: sans-serif !important;
                        color: black;
                      "
                      placeholder="请输入签名"
                      v-model:value="model.signatureTitle"
                    ></a-input>
                    </div>
                    <kolEditor
                  
                      ref="JEditorRef"
                      v-model:value="model.signatureContentOriginal"
                      :toolbar="'undo redo | fontselect | bold italic underline forecolor | textAlign numlist bullist outdent indent | blockquote | removeformat | delete | link image '"
                    />
                  </div>
                </a-form-item>
              </a-col>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import kolEditor from '/@/components/Form/src/jeecg/components/kolEditor.vue';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);
const JEditorRef = ref(null);
const title = ref("操作");
const visible = ref(false);
const confirmLoading = ref(false);

const model = ref({
  signatureTitle: undefined,
  signatureContentOriginal: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 4 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 20 },
};

const validatorRules = {};

const contactEmailRecord = ref({});

const url = {
  add: "/email/signature/add",
  edit: "/email/signature/edit",
};

function add(contactEmailRecordParam) {
  edit(contactEmailRecordParam, {});
}

function edit(contactEmailRecordParam, record) {
  contactEmailRecord.value = contactEmailRecordParam;
  Object.assign(model.value, record);
  title.value = record.id ? "编辑" : "新增";
  visible.value = true;
}

function close() {
  emit("close");
  model.value = {};
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    
    if (!model.value.signatureTitle) {
      createMessage.warning("邮箱签名标题不能为空");
      return;
    }
    if (!model.value.signatureContentOriginal) {
      createMessage.warning("邮箱签名内容不能为空");
      return;
    }
    
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
      contactEmailId: contactEmailRecord.value.id,
      contactEmail: contactEmailRecord.value.contactEmail,
      ...model.value,
      signatureContent: JEditorRef.value?.getPlainText() || "",
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
  title,
});
</script>
<style>
    .templateContentBox {
   border-bottom: 1px solid rgb(217, 217, 217)
  }
.templateContentBox .ant-input {
  border: none !important;
}
.templateContentBox .ant-input:focus {
  box-shadow: none !important;
}
.tox .tox-toolbar__group:not(:last-of-type)::after {
  content: "" !important;

  width: 1px !important;
  height: 20px !important;
  background-color: #b8bebe;
}
</style>
<style lang="less" scoped>

</style>
