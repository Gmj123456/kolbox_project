<template>
  <a-modal
    :title="title"
    :width="1000"
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
      <a-form ref="formRef" labelAlign="left" :model="model" :rules="validatorRules">
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item>
              <JDictSelectPlatformType
                v-model:value="model.platformType"
                dictCode="platform_type"
                :triggerChange="true"
                @change="onPlatformTypeChange"
              ></JDictSelectPlatformType>
            </a-form-item>
          </a-col>
          <a-col :span="12" style="margin-bottom: 6px">
            <a-form-item name="productId">
              <a-input-group compact>
                <a-select
                  show-search
        
                  option-filter-prop="label"
                  allowClear
                  placeholder="所属品牌"
                  v-model:value="model.brandId"
                  style="width: 150px;border-right: 0px;"
                  @change="onBrandChange"
                >
                  <a-select-option
                    v-for="item in brandList"
                    :key="item.id"
                    :value="item.id"
                    :label="item.brandName"
                  >
                    {{ item.brandName }}
                  </a-select-option>
                </a-select>
                <a-select
                  show-search
                  option-filter-prop="label"
                  allowClear
                  placeholder="所属产品"
                  style="width: calc(100% - 150px);"
                  v-model:value="model.productId"
                  @change="onProductChange"
                >
                  <a-select-option
                    v-for="item in brandProductList"
                    :key="item.id"
                    :value="item.id"
                    :label="item.productName"
                  >
                    {{ item.productName }}
                  </a-select-option>
                </a-select>
              </a-input-group>
            </a-form-item>
          </a-col>

          <a-col :span="24">
            <a-form-item name="templateContentOriginal">
              <div
                class="templateContentBox"
                style="padding: 10px; border: 1px solid #d9d9d9; border-radius: 4px;border-bottom: ;"
              >
                <div style="display: flex; align-items: center;border-bottom: 1px solid rgb(217, 217, 217);">
                  <a-input
                    style="
                      font-size: 14px !important;
                      font-family: sans-serif !important;
                      color: black;
                    "
                    placeholder="请输入邮件主题"
                    v-model:value="model.templateTitle"
                    :maxlength="500"
                  ></a-input>
                  <a-divider type="vertical" />
                  <a-dropdown>
                    <a style="width: 68px" @click="(e) => e.preventDefault()">
                      添加变量
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
                </div>
               
                <kolEditor
                  v-if="visible"
                  ref="JEditorRef"
                  v-model:value="model.templateContentOriginal"
                 
                  @change-blur="handleEditorBlur"
                />
              </div>
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
const JEditorRef = ref(null);
const title = ref("操作");
const visible = ref(false);
const confirmLoading = ref(false);

const model = ref({
  platformType: undefined,
  brandId: undefined,
  productId: undefined,
  templateTitle: undefined,
  templateContentOriginal: undefined,
});

const brandList = ref([]);
const productList = ref([]);
const brandProductList = ref([]);
const templateVarList = ref([]);

const validatorRules = {
  platformType: [
    {
      required: true,
      message: "请选择平台",
    },
  ],
  productId: [
    {
      required: true,
      validator: (_rule, value) => {
        if (!value) {
          if (!model.value.brandId) {
            return Promise.reject("请选择品牌");
          } else {
            return Promise.reject("请选择产品");
          }
        }
        return Promise.resolve();
      },
    },
  ],
};

const url = {
  add: "/email/emailTemplate/add",
  edit: "/email/emailTemplate/edit",
};

async function initBrandList() {
  const res = await defHttp.get({ url: "/kolBrand/listAll" });
  if (res) {
    brandList.value = res || [];
  }
}

async function initProductList() {
  const res = await defHttp.get({ url: "/kol/kolProduct/listAll" });
  if (res) {
    productList.value = res || [];
  }
}

function onBrandChange(value) {
  model.value.productId = undefined;
  brandProductList.value = [];
  brandProductList.value = productList.value.filter((item) => item.brandId == value);
  formRef.value?.clearValidate("productId");
  if (brandProductList.value.length == 1) {
    model.value.productId = brandProductList.value[0].id;
  }
}

function onProductChange(_value) {
  formRef.value?.clearValidate("productId");
}

function onPlatformTypeChange(value) {
  model.value.platformType = value;
}

async function initTemplateVarList() {
  const res = await getDictItems("template_var");
  if (res) {
    templateVarList.value = res;
  }
}

function addVariable(text) {
  JEditorRef.value?.insertText(text);
}

function add() {
  edit({});
}

async function edit(record) {
  Object.assign(model.value, record);
  model.value.templateContentOriginal = model.value.templateContentOriginal || undefined;
  
  initBrandList();
  initTemplateVarList();
  await initProductList();
  brandProductList.value = productList.value.filter(
    (item) => item.brandId == model.value.brandId
  );
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
    
    if (model.value.platformType < 0) {
      createMessage.warning("请选择平台");
      return;
    }
    if (!model.value.templateTitle) {
      createMessage.warning("邮件主题不能为空");
      return;
    }
    if (!model.value.templateContentOriginal) {
      createMessage.warning("模版内容不能为空");
      return;
    }
    
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
      templateContent: JEditorRef.value?.getPlainText() || "",
      brandName: brandList.value.find((item) => item.id == model.value.brandId)?.brandName,
      productName: brandProductList.value.find(
        (item) => item.id == model.value.productId
      )?.productName,
      platformType: model.value.platformType,
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
