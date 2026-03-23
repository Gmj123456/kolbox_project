<template>
  <a-modal
    :title="title"
    :width="400"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="rules" :labelCol="labelCol" :wrapperCol="wrapperCol">
        <a-form-item label="平台" name="platformType">
          <a-select v-model:value="model.platformType" placeholder="请选择平台">
            <a-select-option :value="0">IG</a-select-option>
            <a-select-option :value="1">YT</a-select-option>
            <a-select-option :value="2">TK</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="主标签" name="storeTags">
          <a-select v-model:value="model.storeTags" placeholder="请选择主标签">
            <a-select-option v-for="item in storeTagsList" :key="item.id" :value="item.likeTagName">
              {{ item.likeTagName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="子标签" name="tagNames" style="position: relative">
          <a-textarea
            v-model:value="model.tagNames"
            placeholder="请输入关联的子标签"
            :auto-size="{ minRows: 5, maxRows: 15 }"
          />
          <div style="float: right; position: absolute; right: 0; top: 20px">
            共<span style="color: red">{{ tagNamesSize }}</span>个
          </div>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { getStoreTagsListApi, associatedTagsApi } from '../tagMaintenanceApi';

const { createMessage } = useMessage();

const title = ref('关联标签');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref();
const storeTagsList = ref([]);

const model = ref({
  platformType: '',
  tagNames: '',
  storeTags: '',
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 4 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 18 },
};

const rules = {
  platformType: [{ required: true, message: '请选择平台', trigger: 'change' }],
  storeTags: [{ required: true, message: '请选择主标签', trigger: 'change' }],
  tagNames: [{ required: true, message: '请输入子标签', trigger: 'change' }],
};

const tagNamesSize = computed(() => {
  if (model.value.tagNames) {
    const filterTagNames = model.value.tagNames.split('\n').filter((item) => item.trim() !== '');
    return filterTagNames.length;
  }
  return 0;
});

async function getStoreTagsList() {
  try {
    const res = await getStoreTagsListApi();
    storeTagsList.value = res?.result?.records || res || [];
  } catch (error) {
    console.error('获取主标签列表失败:', error);
  }
}

function add() {
  visible.value = true;
  getStoreTagsList();
}

function close() {
  visible.value = false;
  model.value = {
    platformType: '',
    tagNames: '',
    storeTags: '',
  };
  formRef.value?.resetFields();
}

async function handleOk() {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    const params = {
      ...model.value,
      tagNames: model.value.tagNames.replace(/\n/g, ','),
    };
    const res = await associatedTagsApi(params);
    if (res && res.success) {
      createMessage.success(res.message);
      emit('ok');
      close();
    } else {
      createMessage.warning(res.message || '操作失败');
    }
  } catch (error) {
    if (error.errorFields) {
      return;
    }
    console.error('操作失败:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

const emit = defineEmits(['ok', 'close']);

defineExpose({
  add,
  close,
});
</script>

<style lang="less" scoped></style>
