<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="400"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-form :model="model">
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="网红顾问">
        <a-select
          v-model:value="model.counselorId"
          allowClear
          placeholder="请选择网红顾问"
        >
          <a-select-option
            v-for="item in celebrityConsultants"
            :key="item.id"
            :value="item.id"
          >
            {{ item.username }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const emit = defineEmits(['ok', 'close']);

const { createMessage } = useMessage();

const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const celebrityConsultants = ref([]);

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
  tagsId: undefined,
  counselorId: undefined,
});

const initlCelebrityConsultant = async () => {
  try {
    const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
    if (res) {
      celebrityConsultants.value = res;
    }
  } catch (error) {
    console.error('initlCelebrityConsultant error:', error);
    createMessage.error('加载顾问列表失败');
  }
};

const close = () => {
  visible.value = false;
  emit('close');
};

const handleOk = async () => {
  if (!model.counselorId) {
    createMessage.warning('请选择网红顾问');
    return;
  }

  const selectedConsultant = celebrityConsultants.value.find(
    (item) => item.id == model.counselorId
  );

  if (!selectedConsultant) {
    createMessage.error('未找到选中的顾问信息');
    return;
  }

  confirmLoading.value = true;
  try {
    const params = {
      id: model.id,
      platformType: model.platformType,
      tagsId: model.tagsId,
      counselorId: model.counselorId,
      counselorName: selectedConsultant.username,
    };

    const res = await defHttp.get({
      url: '/kol/kolTagCelebrity/updateCounselor',
      params,
    },{isTransformResponse: false});

    if (res.success) {
      createMessage.success(res.message || '操作成功');
      close();
      emit('ok');
    } else {
      createMessage.error(res.message || '操作失败');
    }
  } catch (error) {
    console.error('handleOk error:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
  }
};

const handleCancel = () => {
  close();
};

const open = (record = {}) => {
  Object.assign(model, {
    id: record.id,
    platformType: record.platformType,
    tagsId: record.tagsId,
    counselorId: record.counselorId,
  });
  visible.value = true;
  initlCelebrityConsultant();
};

defineExpose({
  open,
});
</script>

<style lang="less" scoped></style>
