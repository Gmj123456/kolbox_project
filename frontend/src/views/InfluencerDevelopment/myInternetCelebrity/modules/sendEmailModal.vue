<template>
  <a-modal
    :title="title"
    :width="610"
    v-model:visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    class="send-email-modal"
    cancelText="关闭"
    :maskClosable="false"
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
              name="brandId"
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
        <!-- <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="{
                xs: { span: 24 },
                sm: { span: 8 },
              }"
              label="模版平台"
            >
              <JDictSelectPlatformType
                v-model:value="model.platformType"
                dictCode="platform_type"
                :triggerChange="true"
                @change="onPlatformTypeChange"
              >
              </JDictSelectPlatformType>
            </a-form-item>
          </a-col>
        </a-row> -->
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
                <!-- <div style="color: #0b1019; font-size: 12px; margin-bottom: 10px">
                  有以下邮件模版：
                </div> -->
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
                        <span
                          class="icon iconfont icon-yingpingmoban"
                          style="font-size: 14px; color: #3155ed"
                        ></span>

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
              label="发送时间"
              name="emailPushDate"
            >
              <a-date-picker
                dropdownClassName="send-email-time-picker"
                placeholder="请选择发送时间"
                style="width: 100%; font-size: 12px"
                format="YYYY-MM-DD HH:mm"
                :disabled-date="disabledDate"
                :disabled-time="disabledTime"
                :show-time="{ format: 'YYYY-MM-DD HH:mm', showNow: false }"
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
                placeholder="请输入最小发送间隔"
                v-model:value="model.sendIntervalMin"
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
                v-model:value="model.sendIntervalMax"
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
              <a-input placeholder="请输入备注" v-model:value="model.remark" />
            </a-form-item>
          </a-col>
        </a-row> -->
      </a-form>
    </a-spin>
    <template #footer>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <div>
          已选择<a>{{ emailPushDetails.length }}</a
          >个
        </div>
        <div>
          <a-button key="back" :loading="confirmLoading" @click="handleCancel">
            关闭
          </a-button>
          <a-button
            key="submit"
            type="primary"
            :loading="confirmLoading"
            @click="handleOk"
          >
            确定
          </a-button>
        </div>
      </div>
    </template>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import dayjs from 'dayjs';
import { Empty } from 'ant-design-vue';
const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const title = ref('发送邮件');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);

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

const model = ref({
  contactEmailId: undefined,
  ccEmails: [],
  signatureId: undefined,
  brandId: undefined,
  productId: undefined,
  platformType: undefined,
  emailPushDate: undefined,
  sendIntervalMin: undefined,
  sendIntervalMax: undefined,
  remark: undefined,
  templateId: undefined,
  id: undefined,
});

const validatorRules = {
  remark: [{ max: 100, message: '备注不能超过100字', trigger: 'blur' }],
  contactEmailId: [
    { required: true, message: '请选择发送邮箱', trigger: 'change' },
  ],
  templateId: [{ required: true, message: '请选择邮件模版', trigger: 'change' }],
  emailPushDate: [{ required: true, message: '请选择发送时间', trigger: 'change' }],
  brandId: [
    {
      required: true,
      validator: (_rule, _value) => {
        const { brandId, productId } = model.value;
        // 品牌未选
        if (!brandId) {
          return Promise.reject('请选择品牌');
        }

        // 品牌选了但产品没选
        if (!productId) {
          return Promise.reject('请选择产品');
        }

        // 都正确
        return Promise.resolve();
      },
      trigger: 'change',
    },
  ],
  sendIntervalMin: [
    { required: true, message: '请输入最小发送间隔', trigger: 'change' },
    {
      validator: (rule, value) => {
        if (
          model.value.sendIntervalMax !== undefined &&
          value !== undefined &&
          value > model.value.sendIntervalMax
        ) {
          return Promise.reject('最小间隔 ≤ 最大间隔');
        } else {
          return Promise.resolve();
        }
      },
      trigger: 'change',
    },
  ],
  sendIntervalMax: [
    { required: true, message: '请输入最大发送间隔', trigger: 'change' },
    {
      validator: (rule, value) => {
        if (
          model.value.sendIntervalMin !== undefined &&
          value !== undefined &&
          value < model.value.sendIntervalMin
        ) {
          return Promise.reject('最大间隔 ≥ 最小间隔');
        } else {
          return Promise.resolve();
        }
      },
      trigger: 'change',
    },
  ],
};

const contactEmailList = ref([]);
const sedEmailList = ref([]);
const emailPushDetails = ref([]);
const emailTemplateList = ref([]);
const signatureList = ref([]);
const brandList = ref([]);
const productList = ref([]);
const brandProductList = ref([]);

const url = {
  add: '/email/emailPushMain/batchAdd',
  edit: '/email/signature/edit',
};

// const onPlatformTypeChange = (value) => {
//   model.value.platformType = value;
//   initEmailTemplate();
// };

const disabledDate = (current) => {
  const now = dayjs();
  return current && current < now.startOf('day');
};

const disabledTime = (date) => {
  if (!date) return {};

  const now = dayjs();
  const currentHour = now.hour();
  const currentMinute = now.minute();

  // 是否今天
  if (date.isSame(now, 'day')) {
    const disabledHours = [];
    for (let h = 0; h < currentHour; h++) {
      disabledHours.push(h); // 禁用当前小时之前
    }

    return {
      disabledHours: () => disabledHours,

      disabledMinutes: (selectedHour) => {
        // 当前小时
        if (selectedHour === currentHour) {
          const disabledMinutes = [];
          for (let m = 0; m <= currentMinute; m++) {
            disabledMinutes.push(m);
          }

          // ⛔ 关键点：禁用当前分钟（禁止选择此刻）
          // 上面的 <= currentMinute 已经覆盖了当前分钟

          return disabledMinutes;
        }

        return [];
      },
    };
  }

  return {};
};

const onProductChange = (value) => {
  if (value) {
    initEmailTemplate();
  } else {
    emailTemplateList.value = [];
    model.value.productId = undefined;
  }
  formRef.value?.clearValidate('brandId');
};

const onBrandChange = (value) => {
  model.value.productId = undefined;
  brandProductList.value = [];
  emailTemplateList.value = [];
  brandProductList.value = productList.value.filter((item) => item.brandId == value);
  if (brandProductList.value.length == 1) {
    model.value.productId = brandProductList.value[0].id;
    initEmailTemplate();
  }
  formRef.value?.clearValidate('brandId');
};

const initBrandList = async () => {
  try {
    const res = await defHttp.get({ url: '/kolBrand/listAll' });
    if (res) {
      brandList.value = res;
    }
  } catch (error) {
    console.error('initBrandList error:', error);
  }
};

const initProductList = async () => {
  try {
    const res = await defHttp.get({ url: '/kol/kolProduct/listAll' });
    if (res) {
      productList.value = res;
    }
  } catch (error) {
    console.error('initProductList error:', error);
  }
};



const initSignature = async () => {
  try {
    const res = await defHttp.get({
      url: '/email/signature/getSignatureList',
      params: {
        id: model.value.contactEmailId,
      },
    });
    if (res) {
      signatureList.value = res;
    }
  } catch (error) {
    console.error('initSignature error:', error);
  }
};

const handleContactEmailChange = (value) => {
  model.value.ccEmails = [];
  signatureList.value = [];
  model.value.signatureId = undefined;
  if (value) {
    signatureList.value = sedEmailList.value.find((item) => item.id === value)?.signatureList;
    // initSignature();
  }
};

const initEmail = async () => {
  try {
    const res = await defHttp.get({ url: '/storeUserContactEmail/queryListByCounselor',params:{type:'0'}});
    if (res) {
      sedEmailList.value = res.filter((item) => item.isAuthorized === 1);
      contactEmailList.value = res;
    }
  } catch (error) {
    console.error('initEmail error:', error);
  }
};

const initEmailTemplate = async () => {
  if (!model.value.productId) {
    return;
  }
  try {
    const res = await defHttp.get({
      url: '/email/emailTemplate/listByProduct',
      params: {
        productId: model.value.productId,
        platformType: model.value.platformType,
      },
    });
    if (res) {
      emailTemplateList.value = res;
    }
  } catch (error) {
    console.error('initEmailTemplate error:', error);
  }
};

const show = async (emailPushDetailsParam, { brandId, productId, platformType }) => {
  visible.value = true;
  signatureList.value = [];
  emailPushDetails.value = emailPushDetailsParam;
  await initEmail();
  await initBrandList();
  await initProductList();
  if (platformType) {
    model.value.platformType = platformType;
  }
  if (brandId) {
    model.value.brandId = brandId;
    brandProductList.value = productList.value.filter(
      (item) => item.brandId == brandId
    );
  }
  if (productId) {
    model.value.productId = productId;
    await initEmailTemplate();
  }
};

const close = () => {
  emit('close');
  emailTemplateList.value = [];
  Object.assign(model.value, {
    contactEmailId: undefined,
    ccEmails: [],
    signatureId: undefined,
    brandId: undefined,
    productId: undefined,
    platformType: undefined,
    emailPushDate: undefined,
    sendIntervalMin: undefined,
    sendIntervalMax: undefined,
    remark: undefined,
    templateId: undefined,
    id: undefined,
  });
  formRef.value?.resetFields();
  visible.value = false;
};

const handleOk = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  confirmLoading.value = true;
  try {
    let httpurl = '';
    let method = '';
    if (!model.value.id) {
      httpurl = url.add;
      method = 'post';
    } else {
      httpurl = url.edit;
      method = 'put';
    }

    if (emailTemplateList.value.length == 0) {
      createMessage.warning('该产品没有邮件模版，请先添加邮件模版');
      confirmLoading.value = false;
      return;
    }

    if (
      model.value.ccEmails &&
      model.value.ccEmails.length > 0 &&
      model.value.ccEmails.includes(
        sedEmailList.value.find((item) => item.id === model.value.contactEmailId)
          ?.contactEmail
      )
    ) {
      createMessage.warning('抄送邮箱不能与发送邮箱相同');
      confirmLoading.value = false;
      return;
    }

    const sendEmailItem = sedEmailList.value.find(
      (item) => item.id === model.value.contactEmailId
    );
    const productItem = brandProductList.value.find(
      (item) => item.id == model.value.productId
    );
    const brandItem = brandList.value.find((item) => item.id == model.value.brandId);

    const params = {
      sendType: 0,
      platformType: model.value.platformType,
      sendEmail: sendEmailItem?.contactEmail,
      templateId: model.value.templateId,
      signatureId: model.value.signatureId,
      contactEmailId: model.value.contactEmailId,
      ccEmails:
        model.value.ccEmails && model.value.ccEmails.length > 0
          ? model.value.ccEmails.join(',')
          : '',
      emailPushDate: dayjs(model.value.emailPushDate).format('YYYY-MM-DD HH:mm:00'),
      remark: model.value.remark,
      emailPushDetails: emailPushDetails.value,
      productId: model.value.productId,
      brandId: model.value.brandId,
      sendIntervalMin: model.value.sendIntervalMin,
      sendIntervalMax: model.value.sendIntervalMax,
      productName: productItem?.productName,
      brandName: brandItem?.brandName,
    };

    const res = await defHttp.request({
      url: httpurl,
      method,
      data: params,
    },{isTransformResponse:false});

    if (res.success) {
      createMessage.success(res.message);
      emit('ok');
      close();
    } else {
      createMessage.warning(res.message);
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

defineExpose({
  show,
  title
});
</script>

<style>
.send-email-modal .ant-modal-body {
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
  content: '' !important;

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
.required-title {
  display: inline-block;
  margin-right: 4px;
  color: #f5222d;
  font-size: 14px;
  font-family: SimSun, sans-serif;
  line-height: 1;
}
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
