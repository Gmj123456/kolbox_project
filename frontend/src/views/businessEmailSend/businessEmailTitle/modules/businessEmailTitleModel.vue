<template>
  <a-modal
    :title="title"
    :width="450"
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
      <a-form ref="formRef" labelAlign="left" :model="model" layout="vertical" :rules="validatorRules">
        <a-row :gutter="12">
        
          <!-- <a-col :span="24">
            <a-form-item name="brandCategoryId" label="品牌类目">
              <a-select v-model:value="model.brandCategoryId" placeholder="请选择品牌类目" option-filter-prop="label" showSearch>
                <a-select-option v-for="item in brandCategoryList" :key="item.id" :value="item.id" :label="item.categoryName">
                  {{ item.categoryName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-item name="templateTitle" label="邮件主题">
              <a-input v-model:value="model.templateTitle" placeholder="请输入邮件主题" >
              
                <template #suffix>
                  <a-dropdown>
                      <a style="width: 68px" @click="(e) => e.preventDefault()">
                        添加变量
                        <DownOutlined />
                      </a>
                      <template #overlay>
                        <a-menu>
                          <a-menu-item
                            v-for="item in templateVarList"
                            :key="item.value"
                            @click="addVariable(item.value)"
                          >
                            <a>{{ item.title }}</a>
                          </a-menu-item>
                        </a-menu>
                      </template>
                    </a-dropdown>
              </template>
              </a-input>
               
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
import { DownOutlined } from '@ant-design/icons-vue';


const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close', 'changeBlur']);

const formRef = ref(null);
const JEditorRef = ref(null);
const title = ref("操作");
const visible = ref(false);
const confirmLoading = ref(false);

const model = ref({

 
});

const brandCategoryList = ref([]);

const templateVarList = ref([]);

const validatorRules = {
  
  // brandCategoryId: [
  //   {
  //     required: true,
  //     message: "请选择品牌类目",
  //   },
  // ],
  templateTitle: [
    {
      required: true,
      message: "请输入邮件主题",
    },
  ],
};

const url = {
  add: "/email/emailTemplateBusinessTitle/add",
  edit: "/email/emailTemplateBusinessTitle/edit",
};

async function initBrandCategoryList() {
  const res = await defHttp.get({ url: "/email/emailTemplateCategory/listAll",params: { templateType:0}  });
  if (res) {
    brandCategoryList.value = res || [];
  }
}

async function initTemplateVarList() {
  const res = await getDictItems("business_template_var");
  if (res) {
    templateVarList.value = res;
  }
}

function addVariable(text) {
  if (!model.value.templateTitle) {
    model.value.templateTitle = text;
  } else {
    model.value.templateTitle += text;
  }
}

function add() {
  edit({});
}

async function edit(record) {
  Object.assign(model.value, record);

  title.value = record.id ? "编辑" : "新增";
  // initBrandCategoryList();
  initTemplateVarList();

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
    
    const params = {
      ...model.value,
    
    };
    
    const res = await defHttp.request({ url: httpurl, method, params });
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

function handleEditorBlur() {
  emit("changeBlur");
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
