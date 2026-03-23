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
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="视频链接" name="videoUrl">
          <a-input v-model:value="model.videoUrl" disabled />
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

const title = ref('重置');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);

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
  videoUrl: '',
});

const validatorRules = {
  videoUrl: [{ required: true, message: '请输入视频链接', trigger: 'blur' }],
};

const Api = {
  Reset: '/tiktokVideo/resetVideo',
};

function edit(record) {
  Object.assign(model, {
    id: record.id,
    videoUrl: record.videoUrl || '',
  });
  visible.value = true;
  title.value = '重置';
  nextTick(() => {
    if (formRef.value) {
      formRef.value.resetFields();
    }
  });
}

function close() {
  visible.value = false;
  emit('close');
  emit('ok');
}

async function handleOk() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    confirmLoading.value = true;

    const params = {
      videoUrl: model.videoUrl,
      id: model.id,
    };

    const res = await defHttp.put({ url: Api.Reset, params });

    if (res) {
      // createMessage.success(res.message);
      close();
      emit('ok');
    } else {
      // createMessage.error(res.message);
    }
  } catch (error) {
    console.error(error);
    // createMessage.error('重置失败');
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

defineExpose({
  edit,
  visible,
  title,
});
</script>

<style lang="less" scoped></style>
