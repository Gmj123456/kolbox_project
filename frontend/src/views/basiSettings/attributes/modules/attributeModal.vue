<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="width"
    :confirmLoading="confirmLoading"
    :destroyOnClose="true"
    cancelText="关闭"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="rules">
        <a-form-item label="上级属性" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-tree-select
            v-model:value="model.parentId"
            show-search
            allow-clear
            :tree-data="productCategoryList"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :replace-fields="treeReplaceFields"
           tree-node-filter-prop="attributeName"
            placeholder="请选择上级属性"
          />
        </a-form-item>
        <a-form-item
          label="属性名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          name="attributeName"
        >
          <a-input v-model:value="model.attributeName" placeholder="请输入属性名称" />
        </a-form-item>
        <a-form-item
          label="英文名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          name="attributeEnName"
        >
          <a-input v-model:value="model.attributeEnName" placeholder="请输入英文名称" />
        </a-form-item>
        <a-form-item label="排序" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number
            v-model:value="model.sortCode"
            :min="0"
            :precision="0"
            style="width: 100%"
            placeholder="请输入排序"
          />
        </a-form-item>
        <a-form-item
          label="备注"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          name="remark"
        >
          <a-textarea
            v-model:value="model.remark"
            :auto-size="{ minRows: 3, maxRows: 5 }"
            placeholder="请输入备注"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const emit = defineEmits(['ok']);
const { createMessage } = useMessage();

const title = ref('操作');
const width = 400;
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);
const productCategoryList = ref([]);
const currentTypeCode = ref('');

const model = ref({
  id: undefined,
  attributeTypeId: undefined,
  parentId: undefined,
  attributeName: '',
  attributeEnName: '',
  sortCode: undefined,
  remark: '',
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 19 },
};

const rules = {
  attributeName: [
    { required: true, message: '请输入名称!', trigger: 'change' },
    { max: 20, message: '名称不能超过20个字符!', trigger: 'change' },
    { pattern: /^[^|]*$/, message: '名称不能包含"|"字符!', trigger: 'change' },
  ],
  attributeEnName: [
    { max: 50, message: '英文名称不能超过50个字符!', trigger: 'change' },
  ],
  remark: [{ max: 500, message: '备注不能超过500个字符', trigger: 'change' }],
};

const treeReplaceFields = {
  children: 'children',
  label: 'attributeName',
  key: 'id',
  value: 'id',
};

const resetForm = () => {
  model.value = {

  }
}



const queryCategory = async () => {
  if (!currentTypeCode.value) {
    productCategoryList.value = [];
    return;
  }
  const res = await defHttp.get({
    url: '/kol/attribute/loadTreeDataAll',
    params: { typeCode: currentTypeCode.value },
  });
  const list = res || [];
  productCategoryList.value = Array.isArray(list) && list.length > 0 ? list[0].data || [] : [];
};

const open = (record = {}, typeCode) => {
  resetForm();
  Object.assign(model.value, record);
  model.value.parentId = model.value.parentId && model.value.parentId !== '0' ? model.value.parentId : undefined;
  currentTypeCode.value = typeCode;
  title.value = record.id ? '编辑' : '新增';
  visible.value = true;
  nextTick(() => {
    formRef.value?.clearValidate();
  });
  queryCategory();
};

const add = (attributeTypeId, typeCode) => {
  open({ attributeTypeId }, typeCode);
};

const addSubordinates = (record, attributeTypeId, typeCode) => {
  open(
    {
      attributeTypeId,
      parentId: record.id,
    },
    typeCode,
  );
};

const handleOk = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  confirmLoading.value = true;
  const isEdit = !!model.value.id;
  const payload = {
    ...model.value,
    attributeEnName: model.value.attributeEnName?.trim(),
    parentId: model.value.parentId || '0',
  };

  try {
    await defHttp.request({
      url: isEdit ? '/kol/attribute/edit' : '/kol/attribute/add',
      method: isEdit ? 'put' : 'post',
      data: payload,
    });
 
    visible.value = false;
    emit('ok');
  } finally {
    confirmLoading.value = false;
  }
};

const handleCancel = () => {
  model.value = Object.assign({});
  visible.value = false;
};

defineExpose({
  open,
  add,
  addSubordinates,
});
</script>

<style scoped></style>
