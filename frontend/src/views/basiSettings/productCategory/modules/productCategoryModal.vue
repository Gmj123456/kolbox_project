<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="width"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef" :rules="rules">
        <a-form-item label="上级类目" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-tree-select
            v-model:value="model.parentId"
            show-search
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            placeholder="请选择上级类目"
            allow-clear
            :treeData="productCategoryList"
            :replaceFields="{ label: 'categoryName', key: 'id', value: 'id' }"
             tree-node-filter-prop="categoryName"
          
          />
        </a-form-item>
        <a-form-item
          label="类目名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          prop="categoryName"
        >
          <a-input
            v-model:value="model.categoryName"
            placeholder="请输入类目名称"
            @blur="model.categoryName = model.categoryName?.trim()"
          />
        </a-form-item>
        <a-form-item
          label="英文名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          prop="categoryEnName"
        >
          <a-input
            v-model:value="model.categoryEnName"
            placeholder="请输入英文名称"
            @blur="model.categoryEnName = model.categoryEnName?.trim()"
          />
        </a-form-item>
        <a-form-item label="排序" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number
            :min="0"
            :precision="0"
            style="width: 100%"
            v-model:value="model.sortCode"
            placeholder="请输入排序"
          />
        </a-form-item>
        <a-form-item
          label="备注"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          prop="remark"
        >
          <a-textarea
            v-model:value="model.remark"
            :auto-size="{ minRows: 3, maxRows: 5 }"
            placeholder="请输入备注"
            @blur="model.remark = model.remark?.trim()"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';

const emit = defineEmits(['ok', 'close']);
const title = ref('操作');
const width = ref(400);
const visible = ref(false);
const confirmLoading = ref(false);
const isEdit = ref(false);
const formRef = ref(null);

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 19 },
};

const model = ref({
  id: undefined,
  parentId: undefined,
  categoryName: '',
  categoryEnName: '',
  sortCode: undefined,
  remark: '',
});

const rules = {
  categoryName: [
    { required: true, message: '请输入类目名称!', trigger: 'change' },
    { max: 20, message: '类目名称不能超过20个字符!', trigger: 'change' },
    { pattern: /^[^|]*$/, message: '类目名称不能包含"|"字符!', trigger: 'change' },
  ],
  categoryEnName: [
    { required: false, message: '请输入英文名称!', trigger: 'change' },
    { max: 50, message: '英文名称不能超过50个字符!', trigger: 'change' },
  ],
  remark: [{ max: 500, message: '备注不能超过500个字符', trigger: 'change' }],
};

const url = {
  add: '/kol/category/add',
  edit: '/kol/category/edit',
};

const productCategoryList = ref([]);
const { createMessage } = useMessage();


const queryCategory = async () => {
  try {
    const res = await defHttp.get({ url: '/kol/category/loadTreeDataAll' });
    if (res) {
      productCategoryList.value = res || [];
    }
  } catch (error) {
    console.error('queryCategory error:', error);
  }
};

const resetModel = () => {
  model.value = {
   
  }
};

const close = () => {
  resetModel();
  formRef.value?.resetFields();
  visible.value = false;
  isEdit.value = false;
  emit('close');
};

const handleOk = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  confirmLoading.value = true;
  try {
    const formData = { ...model.value };
    if (formData.categoryEnName) {
      formData.categoryEnName = formData.categoryEnName.trim();
    }
    if (!formData.parentId) {
      formData.parentId = '0';
    }
    const httpurl = model.value.id ? url.edit : url.add;
    const method = model.value.id ? 'put' : 'post';
    const res = await defHttp.request({
      url: httpurl,
      method,
      data: formData,
    },{isTransformResponse:false});
    if (res?.success) {
      createMessage.success(res.message || '操作成功');
      emit('ok');
      close();
    } else {
      createMessage.error(res?.message || '操作失败');
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

const addSubordinates = (record) => {
  isEdit.value = true;
  open({ parentId: record.id });
};

const add = () => {
  open({});
};

const open = (record = {}) => {
  visible.value = true;
  Object.assign(model.value, {
    id: record.id,
    parentId: record.parentId == 0 ? undefined : record.parentId,
    categoryName: record.categoryName || '',
    categoryEnName: record.categoryEnName || '',
    sortCode: record.sortCode,
    remark: record.remark || '',
  });
  queryCategory();
};

defineExpose({
  add,
  addSubordinates,
  open,
});
</script>

<style lang="less" scoped></style>
