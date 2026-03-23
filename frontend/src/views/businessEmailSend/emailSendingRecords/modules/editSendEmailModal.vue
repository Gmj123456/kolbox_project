<template>
  <a-modal
    :title="title"
    :width="610"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
    cancelText="关闭"
    class="edit-send-email-modal"
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
              name="signatureId"
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
        <!-- <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="品牌类目"
              name="brandCategoryId"
            >
            <a-select
              show-search
              option-filter-prop="label"
              allowClear
              placeholder="请选择品牌类目"
              v-model:value="model.brandCategoryId"
          
            >
              <a-select-option
                v-for="item in brandCategoryList"
                :key="item.id"
                :value="item.id"
                :label="item.categoryName"
              >
                {{ item.categoryName }}
              </a-select-option>
            </a-select>
            </a-form-item>
          </a-col>
        </a-row> -->
        <!-- <a-row :gutter="12">
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
        </a-row> -->
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
          <a-col :span="12" class="send-email-interval-col">
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
                @blur="handleSendIntervalBlur('sendIntervalMin')"
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
                @blur="handleSendIntervalBlur('sendIntervalMax')"
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
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import dayjs from 'dayjs';
import { Empty } from 'ant-design-vue';
const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);
const title = ref("编辑");
const visible = ref(false);
const model = reactive({
  id: undefined,
  contactEmailId: undefined,
  ccEmails: [],
  signatureId: undefined,
  brandId: undefined,
  productId: undefined,
  emailPushDate: undefined,
  sendIntervalMin: undefined,
  sendIntervalMax: undefined,
  remark: undefined,
  sendType: undefined,
  platformType: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 4 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 20 },
};

const labelCol2 = {
  xs: { span: 24 },
  sm: { span: 8 },
};

const wrapperCol2 = {
  xs: { span: 24 },
  sm: { span: 14 },
};

const validatorRules = {
  signatureId: [{ required: true, message: "请选择邮箱签名", trigger: "change" }],
  remark: [{ max: 100, message: "备注不能超过100字", trigger: "blur" }],
  sendIntervalMin: [
    { required: true, message: "请输入最小发送间隔", trigger: "change" },
    {
      validator: (_rule, value) => {
        if (
          model.sendIntervalMax !== undefined &&
          value !== undefined &&
          value > model.sendIntervalMax
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
          model.sendIntervalMin !== undefined &&
          value !== undefined &&
          value < model.sendIntervalMin
        ) {
          return Promise.reject("最大间隔 ≥ 最小间隔");
        }
        return Promise.resolve();
      },
      trigger: "change",
    },
  ],
  contactEmailId: [
    { required: true, message: "请选择发送邮箱", trigger: "change" },
  ],
  emailPushDate: [{ required: true, message: "请选择发送时间", trigger: "change" }],
  // brandCategoryId: [
  //   { required: true, message: "请选择品牌类目", trigger: "change" },
  // ],
};

const contactEmailList = ref([]);
const sedEmailList = ref([]);

const signatureList = ref([]);
const brandCategoryList = ref([]);
const confirmLoading = ref(false);

const url = {
  add: "/email/emailPushMain/batchAdd",
  edit: "/email/emailPushMain/edit",
};

function handleSendIntervalBlur(field) {
  formRef.value?.validateFields([field]);
  if (field === 'sendIntervalMin') {
    formRef.value?.validateFields(['sendIntervalMax']);
  } else {
    formRef.value?.validateFields(['sendIntervalMin']);
  }
}

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


async function initSignature() {
  const res = await defHttp.get({ 
    url: "/email/signature/getSignatureList", 
    params: {
      id: model.contactEmailId,
    }
  });
  if (res) {
    signatureList.value = res;
  }
}



async function initBrandCategoryList() {
  const res = await defHttp.get({ url: "/email/emailTemplateCategory/listAll" });
  if (res) {
    brandCategoryList.value = res || [];
  }
}



async function handleContactEmailChange(value) {
  model.ccEmail = [];
  model.signatureId = null;
  signatureList.value = [];
  if (value) {
    // await initSignature();
    signatureList.value = sedEmailList.value.find((item) => item.id === value)?.signatureList;
  }
}

async function initEmail() {
  const res = await defHttp.get({ url: "/storeUserContactEmail/queryListByCounselor", params: {
    type: '1'
  } });
  if (res) {
    sedEmailList.value = res.filter((item) => item.isAuthorized === 1);
    if (model.contactEmailId) {
      signatureList.value = sedEmailList.value.find((item) => item.id === model.contactEmailId)?.signatureList || [];
    }
    contactEmailList.value = res;
  }
}


async function show(record) {
  visible.value = true;
  Object.assign(model, record);
  model.templateContentOriginal = record.emailContent;
  model.signatureId = record.signatureId || undefined;
  
  if (record.emailPushDate) {
    model.emailPushDate = dayjs(record.emailPushDate);
  }
  
  if (record.ccEmails && typeof record.ccEmails === "string") {
    model.ccEmails = record.ccEmails.split(",").filter((email) => email.trim());
  } else {
    model.ccEmails = undefined;
  }
  // await initBrandCategoryList();
  await initEmail();
  // await initSignature();

}

function close() {
  emit("close");
  Object.keys(model).forEach(key => {
    if (key === 'ccEmails') {
      model[key] = [];
    } else {
      model[key] = undefined;
    }
  });
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    

    
    const selectedEmail = sedEmailList.value.find(
      (item) => item.id === model.contactEmailId
    );
    if (!selectedEmail) {
      createMessage.warning("请选择有效的发送邮箱");
      return;
    }
    
    if (
      model.ccEmails &&
      Array.isArray(model.ccEmails) &&
      model.ccEmails.includes(selectedEmail.contactEmail)
    ) {
      createMessage.warning("抄送邮箱不能与发送邮箱相同");
      return;
    }
    
    if (!model.emailPushDate) {
      createMessage.warning("请选择发送时间");
      return;
    }

    confirmLoading.value = true;
    
    let httpurl = "";
    let method = "";
    if (!model.id) {
      httpurl = url.add;
      method = "post";
    } else {
      httpurl = url.edit;
      method = "put";
    }

    const params = {
      id: model.id,
      templateId: model.templateId,
      sendEmail: selectedEmail.contactEmail,

      contactEmailId: model.contactEmailId,
      emailPushDate: dayjs(model.emailPushDate).format("YYYY-MM-DD HH:mm:ss"),
      sendIntervalMin: model.sendIntervalMin,
      sendIntervalMax: model.sendIntervalMax,
   
      remark: model.remark,
      // brandCategoryId: model.brandCategoryId,
      signatureId: model.signatureId || "",
    };
    

    if (model.ccEmails && model.ccEmails.length > 0) {
      params.ccEmails = model.ccEmails.join(",");
    }
    
    const res = await defHttp.request({ url: httpurl, method, params },{isTransformResponse:false});
    if (res.success) {
      createMessage.success(res.message);
      emit("ok");
      close();
    } else {
      createMessage.warning(res.message);
    }
  } catch (error) {
    console.error("提交失败:", error);
    createMessage.error("提交失败，请重试");
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

defineExpose({
  show,
});
</script>
<style>
.edit-send-email-modal .ant-modal-body {
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
  width: 98px !important;
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
:deep(.input-num .ant-input-number-handler-wrap) {
  display: none !important;
}
</style>
