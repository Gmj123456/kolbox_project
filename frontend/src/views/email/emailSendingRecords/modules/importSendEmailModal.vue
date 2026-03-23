<template>
  <a-modal
    title="导入"
    :width="620"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    centered
    :maskClosable="false"
    @ok="handleOk"
    @cancel="handleCancel"
    class="import-send-email-modal"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="发送邮箱"
              name="contactEmailId"
            >
              <a-select
                show-search
             
                option-filter-prop="label"
                allowClear
                @change="handleContactEmailChange"
                v-model:value="model.contactEmailId"
                placeholder="请选择发送邮箱"
              >
                <a-select-option
                  v-for="item in sedEmailList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.contactEmail"
                >
                  {{ item.contactEmail }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="抄送邮箱"
            >
              <a-select
                show-search
                option-filter-prop="label"  
                allowClear
                v-model:value="model.ccEmails"
                mode="multiple"
                placeholder="请选择抄送邮箱"
              >
                <a-select-option
                  v-for="item in contactEmailList"
                  :key="item.contactEmail"
                  :value="item.contactEmail"
                  :label="item.contactEmail"
                >
                  {{ item.contactEmail }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱签名"
            >
              <a-select
                show-search
                option-filter-prop="label"
                allowClear
                placeholder="请选择邮箱签名"
                v-model:value="model.signatureId"
              >
                <a-select-option
                  v-for="item in signatureList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.signatureTitle"
                >
                  {{ item.signatureTitle }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="标记开发产品"
              name="productId"
            >
              <a-input-group compact>
                <a-select
                  show-search
                  option-filter-prop="label"
                  allowClear
                  placeholder="请选择品牌"
                  v-model:value="model.brandId"
                  style="width: 200px;border-right: 0px;"
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
                  placeholder="请选择产品"
                  style="width: calc(100% - 200px)"
                  @change="onProductChange"
                  v-model:value="model.productId"
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
        </a-row>

        <a-row :gutter="12" style="margin: 6px 0">
          <a-col :span="24" style="padding: 0px">
            <a-form-item
              label="邮件模版预览"
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
            >
              <div
                style="background-color: #f6f8fa; border-radius: 4px; padding: 10px 20px"
              >
                <div style="height: 302px; overflow-y: auto">
                  <template v-if="emailTemplateList.length > 0">
                    <div
                      v-for="item in emailTemplateList"
                      :key="item.id"
                      style="margin-bottom: 10px"
                    >
                      <div
                        style="
                          color: #0b1019;
                          font-size: 12px;
                          margin-bottom: 2px;
                          display: flex;
                          align-items: center;
                          gap: 4px;
                          line-height: normal;
                        "
                      >
                        <img
                          style="width: 20px; height: 20px"
                          src="@/assets/images/yt.png"
                          alt=""
                          v-if="item.platformType == 1"
                        />
                        <img
                          style="width: 20px; height: 20px"
                          src="@/assets/images/tk.png"
                          alt=""
                          v-if="item.platformType == 2"
                        />
                        <img
                          style="width: 20px; height: 20px"
                          src="@/assets/images/ins.png"
                          alt=""
                          v-if="item.platformType == 0"
                        />
                        <span>{{ item.templateTitle }}</span>
                      </div>
                      <div
                        style="
                          color: #969696;
                          overflow: hidden;
                          font-size: 12px;
                          text-overflow: ellipsis;
                          display: -webkit-box;
                          -webkit-line-clamp: 6;
                          line-clamp: 6;
                          -webkit-box-orient: vertical;
                          white-space: normal;
                          line-height: normal;
                        "
                      >
                        {{ item.templateContent }}
                      </div>
                    </div>
                  </template>
                  <template v-else>
                    <div
                      style="
                        width: 100%;
                        height: 100%;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                      "
                    >
                      <a-empty
                      :image="simpleImage"
                        :image-style="{
                          height: '80px',
                        }"
                      />
                    </div>
                  </template>
                </div>
              </div>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="上传文件"
              name="fileList"
            >
              <a-upload
                style="width: 100%; display: flex;gap:4px"
                name="file"
                :showUploadList="true"
                :multiple="false"
                :fileList="model.fileList"
                accept=".xls,.xlsx"
                :beforeUpload="beforeUpload"
                @change="handleImportExcelFn"
              >
                <a-button 
                  ><UploadOutlined />选择文件</a-button
                >
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="发送时间"
              name="emailPushDate"
            >
              <a-date-picker
                dropdownClassName="send-email-time-picker"
                placeholder="请选择发送时间"
                style="width: 100%; font-size: 12px"
                format="YYYY-MM-DD HH:mm"
                :show-time="{ format: 'YYYY-MM-DD HH:mm' }"
                :disabled-date="disabledDate"
                :disabled-time="disabledTime"
                v-model:value="model.emailPushDate"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="12">
          <a-col :span="12" class="send-email-interval-col"  style="transform: translateX(2px)">
            <a-form-item
              :labelCol="labelCol2"
              :wrapperCol="wrapperCol2"
              label="最小发送间隔"
              name="sendIntervalMin"
            >
              <a-input-number
                :min="0"
                :max="60"
                :precision="0"
                class="input-num"
                placeholder="请输入最小发送间隔"
                v-model:value.trim="model.sendIntervalMin"
                style="width: 100%"
              />
              <span
                style="
                  position: absolute;
                  top: 50%;
                  z-index: 2;
                  display: -ms-flexbox;
                  display: flex;
                  -ms-flex-align: center;
                  align-items: center;
                  color: rgba(0, 0, 0, 0.65);
                  line-height: 0;
                  transform: translateY(-50%);
                  right: 12px;
                  font-size: 12px;
                "
                class="ant-input-suffix"
                >分钟</span
              >
            </a-form-item>
          </a-col>
          <a-col :span="12" style="transform: translateX(24px)">
            <a-form-item
              :labelCol="labelCol2"
              :wrapperCol="wrapperCol2"
              label="最大发送间隔"
              name="sendIntervalMax"
            >
              <a-input-number
                :min="0"
                :max="60"
                :precision="0"
                class="input-num"
                placeholder="请输入最大发送间隔"
                v-model:value.trim="model.sendIntervalMax"
                style="width: 100%"
              />
              <span
                style="
                  position: absolute;
                  top: 50%;
                  z-index: 2;
                  display: -ms-flexbox;
                  display: flex;
                  -ms-flex-align: center;
                  align-items: center;
                  color: rgba(0, 0, 0, 0.65);
                  line-height: 0;
                  transform: translateY(-50%);
                  right: 12px;
                  font-size: 12px;
                "
                class="ant-input-suffix"
                >分钟</span
              >
            </a-form-item>
          </a-col>
        </a-row>
        <!-- <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="备注"
              name="remark"
            >
              <a-input placeholder="请输入备注" v-model:value.trim="model.remark" />
            </a-form-item>
          </a-col>
        </a-row> -->
      </a-form>
      <importErrorListModal ref="importErrorListModalRef" />
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { UploadOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import importErrorListModal from "./importErrorlistModal.vue";
import dayjs from 'dayjs';
import { Empty } from 'ant-design-vue';
const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);
const importErrorListModalRef = ref(null);
const visible = ref(false);
const confirmLoading = ref(false);

// 将 reactive 写法改为 ref 写法
const model = ref({
  contactEmailId: undefined,
  ccEmails: [],
  signatureId: undefined,
  brandId: undefined,
  productId: undefined,
  fileList: [],
  emailPushDate: undefined,
  sendIntervalMin: undefined,
  sendIntervalMax: undefined,
  remark: undefined,
  platformType: undefined,
});

const labelCol = {
  span: 4,
};

const wrapperCol = {
  span: 20,
};

const labelCol2 = {
  span: 8,
};

const wrapperCol2 = {
  span: 14,
};

const validatorRules = {
  remark: [{ max: 100, message: "备注不能超过100字", trigger: "blur" }],
  contactEmailId: [
    { required: true, message: "请选择发送邮箱", trigger: "change" },
  ],
  templateId: [{ required: true, message: "请选择邮件模版", trigger: "change" }],
  emailPushDate: [{ required: true, message: "请选择发送时间", trigger: "change" }],
  fileList: [{ required: true, message: "", trigger: "change" }],
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
  sendIntervalMin: [
    { required: true, message: "请输入最小发送间隔", trigger: "change" },
    {
      validator: (_rule, value) => {
        if (
          model.value.sendIntervalMax !== undefined &&
          value !== undefined &&
          value > model.value.sendIntervalMax
        ) {
          return Promise.reject("最小间隔 ≤ 最大间隔");
        }
        return Promise.resolve();
      },
      trigger: "change",
    },
  ],
  sendIntervalMax: [
    { required: true, message: "请输入最大发送间隔", trigger: "change" },
    {
      validator: (_rule, value) => {
        if (
          model.value.sendIntervalMin !== undefined &&
          value !== undefined &&
          value < model.value.sendIntervalMin
        ) {
          return Promise.reject("最大间隔 ≥ 最小间隔");
        }
        return Promise.resolve();
      },
      trigger: "change",
    },
  ],
};

const contactEmailList = ref([]);
const sedEmailList = ref([]);
const emailTemplateList = ref([]);
const signatureList = ref([]);
const brandList = ref([]);
const productList = ref([]);
const brandProductList = ref([]);

const url = {
  add: "/email/emailPushMain/batchImport",
  edit: "/email/emailPushMain/edit",
};

function disabledDate(current) {
  const now = dayjs();
  return current && current < now.startOf("day");
}

function disabledTime(date) {
  if (!date) return {};

  const now = dayjs();
  const currentHour = now.hour();
  const currentMinute = now.minute();

  if (date.isSame(now, "day")) {
    const disabledHours = [];
    for (let h = 0; h < currentHour; h++) {
      disabledHours.push(h);
    }

    return {
      disabledHours: () => disabledHours,
      disabledMinutes: (selectedHour) => {
        if (selectedHour === currentHour) {
          const disabledMinutes = [];
          for (let m = 0; m <= currentMinute; m++) {
            disabledMinutes.push(m);
          }
          return disabledMinutes;
        }
        return [];
      },
    };
  }

  return {};
}


function onBrandChange(value) {
  model.value.productId = undefined;
  brandProductList.value = [];
  brandProductList.value = productList.value.filter((item) => item.brandId == value);
  if (brandProductList.value.length == 1) {
    model.value.productId = brandProductList.value[0].id;
    initEmailTemplate();
  }
  formRef.value?.clearValidate("productId");
}

function onProductChange(value) {
  console.log(value);
  if (value) {
    initEmailTemplate();
  } else {
    emailTemplateList.value = [];
    model.value.productId = undefined;
  }
  formRef.value?.clearValidate("productId");
}

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

function beforeUpload(file) {
  console.log("选择的文件:", file);
  const isExcel =
    file.type === "application/vnd.ms-excel" ||
    file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  if (!isExcel) {
    createMessage.error("只能上传 Excel 文件！");
    return false;
  }
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    createMessage.error("文件大小不能超过 10MB！");
    return false;
  }
  return false;
}

function handleImportExcelFn(info) {
  console.log("文件变化:", info);
  model.value.fileList = [];
  if (info.fileList.length > 0) {
    const latestFile = info.fileList[info.fileList.length - 1];
    model.value.fileList = [latestFile];
  } else {
    model.value.fileList = [];
  }
}

async function initSignature() {
  const res = await defHttp.get({ 
    url: "/email/signature/getSignatureList", 
    params: {
      id: model.value.contactEmailId,
    }
  });
  if (res) {
    signatureList.value = res || [];
  }
}

function handleContactEmailChange(value) {
  model.value.ccEmail = [];
  model.value.signatureId = null;
  signatureList.value = [];
  if (value) {
    // initSignature();
    signatureList.value = sedEmailList.value.find((item) => item.id === value)?.signatureList;
  }
}

async function initEmail() {
  const res = await defHttp.get({ url: "/storeUserContactEmail/queryListByCounselor",params:{type:'0'}});
  if (res) {
    sedEmailList.value = res.filter((item) => item.isAuthorized === 1);
    contactEmailList.value = res;
  }
}

async function initEmailTemplate() {
  const res = await defHttp.get({ 
    url: "/email/emailTemplate/listByProduct", 
    params: {
      productId: model.value.productId,
      platformType: model.value.platformType,
    }
  });
  if (res) {
    emailTemplateList.value = res;
  }
}

async function show() {
  visible.value = true;
  initEmail();
  await initBrandList();
  await initProductList();
  await initEmailTemplate();
}

function close() {
  emit("close");
  // 清空值
  Object.keys(model.value).forEach(key => {
    if (key === 'ccEmails' || key === 'fileList') {
      model.value[key] = [];
    } else {
      model.value[key] = undefined;
    }
  });
  emailTemplateList.value = [];
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  if (!model.value.fileList || model.value.fileList.length === 0) {
    createMessage.warning("请上传文件");
    return;
  }
  
  const reader = new FileReader();
  reader.readAsText(model.value.fileList[0].originFileObj);
  reader.onerror = (_err) => {
    createMessage.error("请重新上传文件并确保文件内容未被修改");
  };
  
  reader.onload = async () => {
    try {
      await formRef.value.validate();
      
      if (emailTemplateList.value.length == 0) {
        createMessage.warning("该产品没有邮件模版，请先添加邮件模版");
        return;
      }
      
      const selectedEmail = sedEmailList.value.find(
        (item) => item.id === model.value.contactEmailId
      );
      if (!selectedEmail) {
        createMessage.warning("请选择有效的发送邮箱");
        return;
      }
      
      if (
        model.value.ccEmails &&
        Array.isArray(model.value.ccEmails) &&
        model.value.ccEmails.includes(selectedEmail.contactEmail)
      ) {
        createMessage.warning("抄送邮箱不能与发送邮箱相同");
        return;
      }

      confirmLoading.value = true;
      
      const formData = new FormData();
      formData.append("file", model.value.fileList[0].originFileObj);
      formData.append("sendType", 1);
      formData.append("sendEmail", selectedEmail.contactEmail);
      formData.append("contactEmailId", model.value.contactEmailId);
      formData.append("brandId", model.value.brandId);
      formData.append("productId", model.value.productId);
      formData.append("sendIntervalMin", model.value.sendIntervalMin);
      formData.append("sendIntervalMax", model.value.sendIntervalMax);
      formData.append(
        "productName",
        brandProductList.value.find((item) => item.id == model.value.productId)?.productName || ""
      );
      formData.append(
        "brandName",
        brandList.value.find((item) => item.id == model.value.brandId)?.brandName || ""
      );

      if (model.value.signatureId) {
        formData.append("signatureId", model.value.signatureId);
      }
      if (model.value.ccEmails && model.value.ccEmails.length > 0) {
        formData.append("ccEmails", model.value.ccEmails.join(","));
      }
      if (model.value.emailPushDate) {
        formData.append(
          "emailPushDate",
          dayjs(model.value.emailPushDate).format("YYYY-MM-DD HH:mm:00")
        );
      }
      if (model.value.remark) {
        formData.append("remark", model.value.remark);
      }
      let headers = {
          'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime(),
        };
      const res = await defHttp.post({ url: url.add, params: formData,headers }, { isTransformResponse: false });
      console.log(res)
      if (res.success) {
        createMessage.success(res.message);
        emit("ok");
        close();
      } else {
        createMessage.warning(res.message);
        if (Array.isArray(res.result) && res.result.length > 0) {
          importErrorListModalRef.value?.show(res.result);
        }
      }
    } catch (error) {
      console.error("提交失败:", error);
      // createMessage.error("提交失败，请重试");
    } finally {
      confirmLoading.value = false;
    }
  };
}

function handleCancel() {
  close();
}

defineExpose({
  show,
});
</script>
<style>
.import-send-email-modal .ant-modal-body {
  max-height: 800px !important;
  overflow-y: auto !important;
  overflow-x: hidden !important;
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
.send-email-time-picker {
  .ant-calendar-today-btn {
    display: none !important;
  }
}
.send-email-interval-col .ant-form-item-label {
  width: 100px !important;
}
</style>
<style lang="less" scoped>
:deep(.tox-tinymce) {
  border: none !important;
}
:deep(.tox-editor-header) {
  padding: 0px 10px 10px 10px !important;
  border: none !important;
  display: flex !important;
}
:deep(.tox-toolbar) {
  border-radius: 40px !important;
  background-color: #f2f6fc !important;
  padding: 0 10px !important;
  border: none !important;
}
:deep(.tox .tox-toolbar__group:not(:last-of-type)) {
  border-right: none !important;
}
:deep(.ant-calendar-picker-input) {
  font-size: 12px !important;
}
:deep(.ant-calendar-input) {
  font-size: 12px !important;
}
:deep(.ant-upload-list-item-name) {
  max-width: 200px !important;
}
:deep(.ant-upload-list-text > div) {
  width: 100% !important;
  position: relative !important;
  top: 50% !important;
  transform: translateY(-50%) !important;
}
:deep(.ant-upload-list-item) {
  margin-top: 0px !important;
}
:deep(.input-num .ant-input-number-handler-wrap) {
  display: none !important;
}
</style>
