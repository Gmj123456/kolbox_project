<template>
  <a-modal
    :title="title"
    :width="800"
    centered
    :maskClosable="false"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-row :gutter="12">
        <a-col :span="24">
          <div
            style="
              display: flex;
              align-items: center;
              gap: 6px;
              height: 32px;
              margin-bottom: 10px;
            "
          >
            <a-avatar style="background: #1296db" :size="24">
              <template #icon><UserOutlined /></template>
            </a-avatar>
            <span>发送至-{{ model.username }}</span>
            <span><{{ model.email }}></span>
          </div>
          <div>
            <div
              class="templateContentBox"
              style="padding: 10px; border: 1px solid #d9d9d9; border-radius: 4px"
            >
              <a-input
                disabled
                style="
                  font-size: 14px !important;
                  font-family: sans-serif !important;
                  background-color: white;
                  color: black;
                  padding: 5px 6px;
                "
                v-model:value.trim="model.emailTitle"
              ></a-input>
              <JEditor
                :disabled="true"
                :height="600"
                v-model:value="model.content"
                :toolbar="' '"
              />
            </div>
          </div>
        </a-col>
      </a-row>
    </a-spin>
    <template #footer>
      <a-button key="back" @click="handleCancel"> 关闭 </a-button>
    </template>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
  import JEditor from '/@/components/Form/src/jeecg/components/JEditor.vue';
  import { UserOutlined } from '@ant-design/icons-vue';
const emit = defineEmits(['close']);

const title = ref("预览");
const visible = ref(false);
const model = reactive({
  username: undefined,
  email: undefined,
  emailTitle: undefined,
  content: undefined,
  emailContent: undefined,
  signatureContent: undefined,
});
const confirmLoading = ref(false);

function view(record) {
  Object.assign(model, record);
  model.content =
    model.emailContent +
    (model.signatureContent ? "<div>--<div>" + model.signatureContent : "");
  visible.value = true;
}

function close() {
  emit("close");
  Object.keys(model).forEach(key => {
    model[key] = undefined;
  });
  visible.value = false;
}

function handleCancel() {
  close();
}

defineExpose({
  view,
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
/* .emailContentBox .tox-tinymce {
  height: 533px !important;
} */
.emailContentBox .tox .tox-edit-area {
  border-top: none !important;
}
</style>
<style lang="less" scoped>
/deep/ .tox-tinymce {
  border: none !important;
}
/deep/ .tox-editor-header {
  padding: 0px 10px 10px 10px !important;
  border: none !important;
  display: flex !important;
}
/deep/ .tox-toolbar {
  border-radius: 40px !important;
  background-color: #f2f6fc !important;
  padding: 0 10px !important;
  border: none !important;
}
/deep/ .tox .tox-toolbar__group:not(:last-of-type) {
  border-right: none !important;
}
</style>
