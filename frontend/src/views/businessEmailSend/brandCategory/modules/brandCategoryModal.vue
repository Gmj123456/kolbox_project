<template>
  <a-modal
    :title="title"
    :width="440"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item label="类目名称" name="categoryName">
              <a-input v-model:value="model.categoryName" placeholder="请输入类目名称" />
            </a-form-item>
            <a-form-item label="备注" name="remark">
              <a-input v-model:value="model.remark" placeholder="请输入备注" />
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
import { getDictItems } from '/@/api/common/api';
import kolEditor from '/@/components/Form/src/jeecg/components/kolEditor.vue';
// import { Tinymce } from '/@/components/Tinymce';
import JDictSelectPlatformType from '@/components/jeecg/JDictSelectPlatformType.vue';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close', 'changeBlur']);

const formRef = ref(null);

const title = ref("操作");
const visible = ref(false);
const confirmLoading = ref(false);

const model = ref({
  
});



const validatorRules = {
  categoryName: [
    { required: true, message: '类目名称不能为空' },
  ],
  remark: [
    { required: false, min: 1, max: 70, message: '备注不能超过70字' },
  ],
};

const url = {
  add: "/email/emailTemplateCategory/add",
  edit: "/email/emailTemplateCategory/edit",
};


function add() {
  edit({});
}

async function edit(record) {
  Object.assign(model.value, record);
  title.value = record.id ? "编辑" : "新增";
  visible.value = true;
}

function close() {
  emit("close");

  model.value = {};
  formRef.value.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    
  
    
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
    

    
    const res = await defHttp.request({ url: httpurl, method, params: { ...model.value, templateType:0 } });
    emit("ok");
    close();
    // if (res.success) {
    //   createMessage.success(res.message);
    // } else {
    //   createMessage.warning(res.message);
    // }
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
:deep(.ant-radio-button-wrapper:not(:first-child)::before) {
  width: 0.1px !important;
  background-color: transparent;
}
:deep(.ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before) {
  background-color: #3155ed !important;
}

</style>
