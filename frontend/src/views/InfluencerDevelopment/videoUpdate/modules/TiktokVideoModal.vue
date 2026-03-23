<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="600"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules" layout="vertical">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="视频链接"
          name="videoUrlListStr"
        >
          <a-textarea
            v-model:value="model.videoUrlListStr"
            style="width: 100%"
            :auto-size="{ minRows: 10 }"
            placeholder="请输入视频链接"
          />
        </a-form-item>
        <a-form-item
          v-if="videoUrlErrorList.length > 0"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="不存在链接"
        >
          <ul style="color: red">
            <li v-for="(item, index) in videoUrlErrorList" :key="index">{{ item }}</li>
          </ul>
        </a-form-item>
        <a-form-item
          v-if="noUrlList.length > 0"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="无效链接"
        >
          <ul style="color: red">
            <li v-for="(item, index) in noUrlList" :key="index">{{ item }}</li>
          </ul>
        </a-form-item>
        <a-form-item
          v-if="duplicateUrlList.length > 0"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="重复链接"
        >
          <ul style="color: orange">
            <li v-for="(item, index) in duplicateUrlList" :key="index">{{ item }}</li>
          </ul>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, reactive, nextTick, defineExpose } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';

const emit = defineEmits(['ok', 'close']);

const { createMessage } = useMessage();

const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);
const videoUrlErrorList = ref([]);
const duplicateUrlList = ref([]);
const noUrlList = ref([]);

const labelCol = {
  xs: { span: 24 },
  sm: { span: 6 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 24 },
};

const model = reactive({
  id: undefined,
  videoUrlListStr: '',
});

const validatorRules = {
  videoUrlListStr: [{ required: true, message: '请输入视频链接', trigger: 'change' }],
};

const Api = {
  Add: '/tiktokVideo/add',
  Edit: '/tiktokVideo/edit',
};

function isValidUrl(url) {
  const regex = /^(http|https):\/\/[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}/;
  if (regex.test(url)) {
    if (url.includes('"') || url.includes("'")) {
      return false;
    } else {
      return true;
    }
  } else {
    return false;
  }
}

function add() {
  edit({});
}

function edit(record) {
  Object.assign(model, {
    id: record.id,
    videoUrlListStr: '',
  });
  visible.value = true;
  videoUrlErrorList.value = [];
  noUrlList.value = [];
  duplicateUrlList.value = [];
  nextTick(() => {
    if (formRef.value) {
      formRef.value.resetFields();
    }
  });
}

function close() {
  visible.value = false;
  videoUrlErrorList.value = [];
  noUrlList.value = [];
  duplicateUrlList.value = [];
  emit('close');
  emit('ok');
}

async function handleOk() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    noUrlList.value = [];
    duplicateUrlList.value = [];
    confirmLoading.value = true;

    const videoUrlList = model.videoUrlListStr.trim().split('\n').filter((url) => url.trim());

    const uniqueUrls = {};

    videoUrlList.forEach((item) => {
      if (!isValidUrl(item)) {
        noUrlList.value.push(item);
      } else {
        if (uniqueUrls[item]) {
          duplicateUrlList.value.push(item);
        } else {
          uniqueUrls[item] = true;
        }
      }
    });

    if (noUrlList.value.length > 0 || duplicateUrlList.value.length > 0) {
      confirmLoading.value = false;
      return;
    }

    const uniqueVideoUrlList = Object.keys(uniqueUrls);
    const params = { videoUrlList: uniqueVideoUrlList };

    const httpurl = model.id ? Api.Edit : Api.Add;
    const method = model.id ? 'put' : 'post';

    const res = await defHttp.request({
      url: httpurl,
      method,
      data: params,
    });

    if (res) {
      if (res && res.videoUrlErrorList && res.videoUrlErrorList.length > 0) {
        videoUrlErrorList.value = res.videoUrlErrorList;
        // createMessage.success(res.message);
        emit('ok');
      } else {
        close();
        emit('ok');
      }
    } else {
      if (res && res.videoUrlErrorList && res.videoUrlErrorList.length > 0) {
        videoUrlErrorList.value = res.videoUrlErrorList;
        createMessage.error(res.message);
      } else {
        createMessage.error(res.message);
      }
    }
  } catch (error) {
    console.error(error);
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
  visible,
  title,
});
</script>

<style lang="less" scoped></style>
