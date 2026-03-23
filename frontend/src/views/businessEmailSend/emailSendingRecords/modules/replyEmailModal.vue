<template>
  <a-modal
    :title="title"
    :width="800"
    centered
    :maskClosable="false"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
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
            <a-avatar style="background: #1296db"  :size="24" >
              <template #icon><UserOutlined /></template>
            </a-avatar>

            <span>{{ model.username }}</span>
            <span><{{ receiveEmail.email }}></span>
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
                "
                placeholder="请输入邮件标题"
                v-model:value.trim="receiveEmail.emailTitle"
              ></a-input>
              <JEditor
                
                :disabled="true"
                :height="600"
                v-model:value="receiveEmail.emailContent"
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
import { ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import JEditor from '/@/components/Form/src/jeecg/components/JEditor.vue';
import { UserOutlined } from '@ant-design/icons-vue';
const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const title = ref("邮件回复信息");
const visible = ref(false);
const model = ref({
  username: undefined,
});
const receiveEmail = ref({
  email: undefined,
  emailTitle: undefined,
  emailContent: undefined,
  ccEmails: undefined,
});
const sendEmail = ref({
  emailContent: undefined,
  ccEmails: [],
  replyType: undefined,
});
const ccEmailList = ref([]);
const confirmLoading = ref(false);

function edit(record) {
  Object.assign(model.value, record);
  visible.value = true;
  getReplyDetail(record.id);
}

async function getReplyDetail(id) {
  try {
    const res = await defHttp.get({ 
      url: "/email/emailReply/queryByEmailDetailId", 
      params: { id }
    },{isTransformResponse:false});
    if (res.success) {
      const receiveItem = res.result.filter((item) => item.replyType == 0)[0];
      Object.assign(receiveEmail.value, receiveItem);
      ccEmailList.value = receiveEmail.value.ccEmails
        ? receiveEmail.value.ccEmails.split(",")
        : [];
      const sendItem = res.result.filter((item) => item.replyType == 1)[0] || {};
      Object.assign(sendEmail.value, sendItem);
      sendEmail.value.templateId = undefined;
    }
  } catch (error) {
    console.error(error);
  }
}

function close() {
  emit("close");
  Object.keys(model.value).forEach(key => {
    model.value[key] = undefined;
  });
  Object.keys(receiveEmail.value).forEach(key => {
    receiveEmail.value[key] = undefined;
  });
  Object.keys(sendEmail.value).forEach(key => {
    if (key === 'ccEmails') {
      sendEmail.value[key] = [];
    } else {
      sendEmail.value[key] = undefined;
    }
  });
  visible.value = false;
}

function handleCancel() {
  close();
}

defineExpose({
  edit,
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
.emailContentBox .tox .tox-edit-area {
  border-top: none !important;
}
</style>
<style lang="less" scoped>
:deep(.tox-tinymce) {
  border: none !important;
}
:deep(.tox-editor-header) {
  padding: 0px 10px 10px 10px !important;
  border: none !important;
  display: flex !important;
}
:deep(.tox-toolbar) {
  border-radius: 40px !important;
  background-color: #f2f6fc !important;
  padding: 0 10px !important;
  border: none !important;
}
:deep(.tox .tox-toolbar__group:not(:last-of-type)) {
  border-right: none !important;
}
</style>
