<template>
  <a-modal
    v-model:visible="visibleModal"
    :maskClosable="false"
    :title="title"
    :width="440"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    centered
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }" ref="formRef" :rules="rules">
        <a-form-item label="网红顾问" prop="celebrityCounselor">
          <a-select
            v-model:value="model.celebrityCounselor"
            option-filter-prop="label"
            mode="multiple"
            allowClear
            placeholder="请选择网红顾问"
          >
            <a-select-option
              v-for="item in celebrityConsultantsList"
              :key="item.id"
              :value="item.id"
              :label="item.username"
            >
              {{ item.username }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>
   
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';

const emit = defineEmits(['ok']);

const { createMessage } = useMessage();

const title = ref('操作');
const visibleModal = ref(false);
const confirmLoading = ref(false);
const celebrityConsultantsList = ref<any[]>([]);
const formRef = ref();
const currentType = ref<number | string>();

const model = reactive<Record<string, any>>({
  celebrityCounselor: [],
  tagNameList: [],
  countryCode: undefined,
});

const rules = {
  celebrityCounselor: [
    { required: true, message: '请选择网红顾问', trigger: 'change' },
  ],
};

async function initlCelebrityConsultant() {
  try {
    const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
    if (res) {
      celebrityConsultantsList.value = res;
    }
  } catch (error) {
    console.error('获取网红顾问列表失败:', error);
  }
}

function resetModel() {
  model.celebrityCounselor = [];
  model.tagNameList = [];
  model.countryCode = undefined;
}

function close() {
  resetModel();
  formRef.value?.resetFields();
  visibleModal.value = false;
}

async function handleOk() {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
  } catch {
    return;
  }

  confirmLoading.value = true;
  
  let url = '';
  if (currentType.value === 1 || currentType.value === '1') {
    url = '/youtube/ytCelebrityTags/allotTagsNew';
  } else if (currentType.value === 2 || currentType.value === '2') {
    url = '/tiktok/tkCelebrityTags/allotTagsNew';
  } else {
    url = '/instagram/igCelebrityTags/allotTagsNew';
  }
  
  const params = {
    tagNameList: model.tagNameList,
    platformType: currentType.value,
    countryCode: model.countryCode,
    celebrityCounselorList: model.celebrityCounselor,
    allotType: 1,
  };

  try {
    const res = await defHttp.post({
      url,
      data: params,
    },{isTransformResponse: false});
    
    if (res.success) {
      // createMessage.success(res.message || '操作成功');
      createMessage.success(res.message || '操作成功');
      close();
      emit('ok');
    } else {
      createMessage.error(res?.message || '操作失败');
    }
  } catch (error) {
    console.error('分配失败:', error);
    createMessage.error('网络波动，请重新分配');
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

function add(type: number | string, list: any[], countryCode: string) {
  currentType.value = type;
  visibleModal.value = true;
  title.value = '分配标签';
  model.tagNameList = list;
  model.countryCode = countryCode;
  initlCelebrityConsultant();
}

defineExpose({
  add,
});
</script>

<style lang="less" scoped></style>
