<template>
  <!-- 商品列表 -->
  <a-modal
    :maskClosable="false"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    :title="title"
    @ok="handleOk"
    :width="700"
    class="StorePromotionGoodsManagementModalNew"
  >
        <a-form :model="formState" :rules="rules" ref="formRef" class="clearfix">
          <a-row>
            <a-col :xs="24" :sm="12">
              <a-form-item name="promTitle" :labelCol="labelCol" :wrapperCol="wrapperCol" label="标题">
                <a-input
                  v-model:value="formState.promTitle"
                  placeholder="请输入推广标题"
                ></a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12">
              <a-form-item name="productBrand" :labelCol="labelCol" :wrapperCol="wrapperCol" label="品牌">
                <a-input disabled style="width: 100%" v-model:value="formState.productBrand">
                </a-input>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :xs="24" :sm="10" style="transform: translateX(2px)">
              <a-form-item
                name="promAmount"
                :labelCol="{
                  xs: { span: 24 },
                  sm: { span: 7 },
                }"
                :wrapperCol="{
                  xs: { span: 24 },
                  sm: { span: 16 },
                }"
                label="费用"
              >
                <a-input
                  type="number"
                  @change="promAmountChange"
                  v-model:value="formState.promAmount"
                  :disabled="true"
                >
                  <template #addonBefore>{{ symbol }}</template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="7">
              <a-form-item name="exchangeRate" :labelCol="labelCol8" :wrapperCol="wrapperCol8" label="汇率">
                <a-input
                  type="number"
                  @change="exchangeRateChange"
                  :disabled="true"
                  v-model:value="formState.exchangeRate"
                />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="7" style="transform: translateX(-3px)">
              <a-form-item name="taxRate" :labelCol="labelCol8" :wrapperCol="wrapperCol8" label="税率">
                <a-input
                  type="number"
                  :disabled="true"
                  v-model:value="formState.taxRate"
                  suffix="%"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-col :xs="24" :sm="24">
            <a-form-item name="remark" :labelCol="labelColAll" :wrapperCol="wrapperColAll" label="备注">
              <a-textarea
                style="width: 100%"
                :auto-size="{ minRows: 3, maxRows: 3 }"
                v-model:value="formState.remark"
              />
            </a-form-item>
          </a-col>
        </a-form>
  
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import {
  floatAdd,
  floatDivide,
  floatMultiply,
  floatSubtract,
} from '/@/utils/floatUtils';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const symbol = ref('');
const rateFlag = ref(false);
const promAmountReadOnly = ref(false);

// 布局配置
const labelCol = {
  xs: { span: 24 },
  sm: { span: 6 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 16 },
};
const labelCol8 = {
  xs: { span: 24 },
  sm: { span: 7 },
};
const wrapperCol8 = {
  xs: { span: 24 },
  sm: { span: 14 },
};
const labelColAll = {
  xs: { span: 24 },
  sm: { span: 3 },
};
const wrapperColAll = {
  xs: { span: 24 },
  sm: { span: 20 },
};

// 表单数据
const formState = reactive({
  promTitle: '',
  productBrand: '',
  promAmount: undefined as number | undefined,
  exchangeRate: undefined as number | undefined,
  taxRate: undefined as number | undefined,
  remark: '',
  sid: '',
});

// 表单验证规则
const rules = {
  promTitle: [{ required: true, message: '请输入推广标题' }],
};

// 数据
const model = reactive<any>({});
const paidAmount = ref(0);
const payableAmount = ref(0);
const paymentAmount = ref(0);
const payment = ref(0);
const payAmount = ref(0);
const totalAmount = ref(0);
const totalRmbAmount = ref(0);
const exchangeRate = ref(0);
const paypalFeesAmount = ref(0);
const paypalRate = ref(0);
const paypalBaseFees = ref(0);
const intentionAmount = ref(0);
const refundAmount = ref(0);
const paymentAmountTemp = ref(0);
const paymentAmountReal = ref(0);
const celebrityUserName = ref('');
const brandList = ref<any[]>([]);
const celebrityCounselorList = ref<any[]>([]);
const sellerCounselorData = ref<any[]>([]);

// 初始化品牌列表
async function initBrandList() {
  const res = await defHttp.get({
    url: '/kolBrand/listAll',
  });
  if (res) {
    brandList.value = res;
  }
}

// 入口
async function getPromotionGoods(record: any) {
  initBrandList();
  rateFlag.value = false;
  symbol.value = '$';
  celebrityUserName.value = record.celebrityUserName;
  payableAmount.value = record.payableAmount || 0;
  formRef.value?.resetFields();
  Object.assign(model, record);

  paypalFeesAmount.value = Number(record.paypalFeesAmount) || 0;
  await getPaypalFee();
  totalAmount.value = Number(floatAdd(paypalFeesAmount.value, record.totalAmount)) || 0;
  totalRmbAmount.value = Number(floatMultiply(totalAmount.value, record.exchangeRate)) || 0;
  exchangeRate.value = Number(model.exchangeRate) || 0;
  if (exchangeRate.value != 0 && exchangeRate.value != null) {
    rateFlag.value = true;
  }
  await getSellerCounselor();
  await initCelebrityCounselor();
  paymentAmount.value = Number(model.paymentAmount) || 0;
  // 意向金
  intentionAmount.value = Number(model.intentionAmount) || 0;
  // 退款金额
  refundAmount.value = Number(model.refundAmount) || 0;
  // 实际支付金额=支付金额+意向金-退款金额
  paymentAmountTemp.value = Number(floatAdd(paymentAmount.value, intentionAmount.value)) || 0;
  paymentAmountReal.value = Number(floatSubtract(paymentAmountTemp.value, refundAmount.value)) || 0;
  paidAmount.value = Number(floatSubtract(totalRmbAmount.value, paymentAmountReal.value)) || 0;
  if (paidAmount.value < 0) {
    paidAmount.value = 0;
  }
  if (exchangeRate.value === 0) {
    payment.value = 0;
    payAmount.value = 0;
  } else {
    payment.value = Number(floatDivide(paymentAmountReal.value, exchangeRate.value)) || 0;
    payAmount.value = Number(floatDivide(paidAmount.value, exchangeRate.value)) || 0;
  }
  visible.value = true;
  if (
    record.exchangeRate == null ||
    record.exchangeRate == 0 ||
    record.exchangeRate == ''
  ) {
    showDefaultRate();
  }
  nextTick(() => {
    Object.assign(formState, {
      promAmount: Number(model.promAmount) || undefined,
      exchangeRate: Number(model.exchangeRate) || undefined,
      taxRate: Number(model.taxRate) || undefined,
      sid: model.sid,
      remark: model.remark,
      productBrand: model.productBrand,
      promTitle: model.promTitle,
    });
  });
  promAmountReadOnly.value = record.promStatus == 99 || record.promStatus == 90;
}

// 初始化网红顾问列表
async function initCelebrityCounselor() {
  const res = await defHttp.get({
    url: '/sys/user/queryAllCelebrityCounselor',
  });
  if (res) {
    celebrityCounselorList.value = res;
  }
}

// 显示默认汇率
async function showDefaultRate() {
  const res = await defHttp.get({
    url: '/sys/dict/queryItemByDictCode',
    params: {
      dictCode: 'promotion_default_rate',
    },
  });
  if (res) {
    const filter = res.filter((item: any) => item.itemText == 'default_rate');
    if (filter.length > 0) {
      exchangeRate.value = Number(parseFloat(filter[0].itemValue));
      formState.exchangeRate = exchangeRate.value;
    }
  }
}

// 获取 PayPal 费用
async function getPaypalFee() {
  const res = await defHttp.get({
    url: '/storecountry/storeCountry/getCountryByCode',
    params: {
      code: 'US',
    },
  });
  if (res) {
    paypalRate.value = res.paypalRate;
    paypalBaseFees.value = res.paypalBaseFees;
  }
}

// 获取商家顾问
async function getSellerCounselor() {
  const res = await defHttp.get({
    url: '/sys/user/getSellerCounselor',
  });
  if (res) {
    sellerCounselorData.value = res;
  }
}

// 推广费用变更
function promAmountChange(e: any) {
  const promAmount = Number(e.target.value);
  if (!promAmount || isNaN(promAmount)) {
    totalRmbAmount.value = 0;
    paidAmount.value = 0;
    payAmount.value = 0;
    totalAmount.value = 0;
    formState.promAmount = undefined;
    paypalFeesAmount.value = 0;
  } else {
    formState.promAmount = promAmount;
    model.promAmount = promAmount;
    if (!exchangeRate.value || exchangeRate.value === 0) {
      totalRmbAmount.value = 0;
      paidAmount.value = 0;
      payAmount.value = 0;
    } else {
      totalAmount.value = promAmount;
      totalRmbAmount.value = Number(floatMultiply(exchangeRate.value, totalAmount.value)) || 0;
      paidAmount.value = Number(floatSubtract(totalRmbAmount.value, paymentAmountReal.value)) || 0;
      payAmount.value = Number(floatDivide(paidAmount.value, exchangeRate.value)) || 0;
      payment.value = Number(floatDivide(paymentAmountReal.value, exchangeRate.value)) || 0;
      if (paidAmount.value < 0) {
        paidAmount.value = 0;
      }
      if (payAmount.value < 0) {
        payAmount.value = 0;
      }
    }
  }
}

// 汇率变更
function exchangeRateChange(e: any) {
  const exchangeRateValue = Number(e.target.value);
  if (!exchangeRateValue || isNaN(exchangeRateValue)) {
    exchangeRate.value = 0;
    formState.exchangeRate = undefined;
    model.exchangeRate = 0;
    totalRmbAmount.value = 0;
    paidAmount.value = 0;
    payAmount.value = 0;
  } else {
    exchangeRate.value = exchangeRateValue;
    formState.exchangeRate = exchangeRateValue;
    model.exchangeRate = exchangeRateValue;
    totalRmbAmount.value = Number(floatMultiply(totalAmount.value, exchangeRateValue)) || 0;
    paidAmount.value = Number(floatSubtract(totalRmbAmount.value, paymentAmountReal.value)) || 0;
    payAmount.value = Number(floatDivide(paidAmount.value, exchangeRateValue)) || 0;
    payment.value = Number(floatDivide(paymentAmountReal.value, exchangeRate.value)) || 0;
    if (paidAmount.value < 0) {
      paidAmount.value = 0;
    }
    if (payAmount.value < 0) {
      payAmount.value = 0;
    }
  }
}

// 提交
async function handleOk() {
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    const data: any = {
      id: model.id,
      totalRmbAmount: totalRmbAmount.value,
      totalAmount: totalAmount.value,
      taxRate: formState.taxRate,
      promAmount: formState.promAmount,
      exchangeRate: exchangeRate.value,
      sellerId: model.sellerId,
      sid: formState.sid,
      remark: formState.remark,
      productBrand: formState.productBrand,
      promTitle: formState.promTitle,
    };

    if (
      parseFloat(data.totalAmount) > 0 &&
      parseFloat(data.totalAmount) < parseFloat(payment.value.toString())
    ) {
      confirmLoading.value = false;
      return $message.warning('当前实付金额超过订单总金额！');
    }

    const res = await defHttp.put({
      url: '/storeSellerPromotion/editStoreSellerPromotion',
      data,
    },{isTransformResponse:false});

    if (res.success) {
      $message.success(res.message);
      emit('ok');
      close();
    } else {
      $message.error(res.message);
    }
  } catch (error) {
    console.error(error);
  } finally {
    confirmLoading.value = false;
  }
}

// 关闭
function close() {
  emit('ok');
  visible.value = false;
  rateFlag.value = false;
  celebrityUserName.value = '';
}


// 暴露方法
defineExpose({
  getPromotionGoods,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
}>();
</script>

<style lang="less" scoped>
.ant-input-number {
  width: 100%;
}

.clearfix {
  zoom: 1;
}

.clearfix:after {
  content: '.';
  display: block;
  height: 0;
  font-size: 0;
  clear: both;
  visibility: hidden;
}

:deep(input::-webkit-outer-spin-button),
:deep(input::-webkit-inner-spin-button) {
  -webkit-appearance: none;
}

:deep(input[type='number']) {
  -moz-appearance: textfield;
  appearance: textfield;
}

.copy {
  color: #595959;
}

.paypal-box {
  display: flex;
  transform: translate(-3px, -20px);
  height: 32px;
  cursor: pointer;

  .paypal-label {
    margin-left: 23px;
    line-height: 32px;
  }

  .paypal-btn {
    display: flex;
    text-align: center;
    margin-left: 10px;
    margin-top: 5px;
  }
}

.btn-hover {
  color: #ffffff;
  background-color: #2e75c2;
}
</style>
<style lang="less">
.StorePromotionGoodsManagementModalNew {
  margin-top: -80px;
}

.StorePromotionGoodsManagementModalNew .ant-modal-body .ant-form-item {
  margin-bottom: 10px !important;
}

.StorePromotionGoodsManagementModalNew .ant-modal-body .ant-form-item-with-help {
  margin-bottom: 5px !important;
}
</style>
