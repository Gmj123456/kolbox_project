<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="400"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="modelState" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item name="brandId" label="品牌名称">
              <a-select
                disabled
                show-search
                allowClear
                option-filter-prop="label"
                v-model:value="modelState.brandId"
                placeholder="品牌"
                @change="onBrandChange"
              >
                <a-select-option v-for="item in brandList" :key="item.id" :label="item.brandName">{{
                  item.brandName
                }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item name="productId" label="产品名称">
              <a-select
                disabled
                show-search
                option-filter-prop="label"
                v-model:value="modelState.productId"
                placeholder="产品"
                allowClear
              >
                <a-select-option
                  v-for="item in allProductList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.productName"
                >
                  {{ item.productName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item name="shieldReason" label="屏蔽原因">
              <a-textarea
                v-model:value="modelState.shieldReason"
                placeholder="屏蔽原因"
                :auto-size="{ minRows: 3, maxRows: 5 }"
                :maxLength="100"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <template #footer>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <div style="display: flex; align-items: center">
          <div style="color: #0b1019 !important; font-size: 12px">已选择:</div>
          <a-button style="color: #3155ed !important; font-weight: 600" type="link">{{
            blackList.length
          }}</a-button>
        </div>
        <div>
          <a-button key="back" :loading="confirmLoading" @click="handleCancel"
            >关闭</a-button
          >
          <a-button
            key="submit"
            type="primary"
            :loading="confirmLoading"
            @click="handleOk"
            >确定</a-button
          >
        </div>
      </div>
    </template>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const title = ref('屏蔽');
const visible = ref(false);
const confirmLoading = ref(false);
const blackList = ref<any[]>([]);
const brandList = ref<any[]>([]);
const allProductList = ref<any[]>([]);
const productList = ref<any[]>([]);

// 布局配置
const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 17 },
};

// 表单数据
const modelState = reactive<any>({
  brandId: undefined,
  productId: undefined,
  shieldReason: undefined,
});

// 表单验证规则
const rules = {};

// 品牌变化
function onBrandChange(value: any) {
  productList.value = allProductList.value.filter((item) => item.brandId == modelState.brandId);
  modelState.productId = undefined;
}

// 初始化产品列表
async function initProduct() {
  try {
    const res = await defHttp.get({ url: '/kol/kolProduct/listAll' });
    if (res) {
      allProductList.value = res;
    }
  } catch (error) {
    console.error(error);
  }
}

// 初始化品牌列表
async function initBrandList() {
  try {
    const res = await defHttp.get({ url: '/kolBrand/listAll' });
    if (res) {
      brandList.value = res;
    }
  } catch (error) {
    console.error(error);
  }
}

// 显示
function show(blackListData: any[], { brandId, productId }: { brandId: any; productId: any }) {
  visible.value = true;
  modelState.brandId = brandId;
  modelState.productId = productId;
  blackList.value = [...blackListData];
  initBrandList();
  initProduct();
}

// 关闭
function close() {
  emit('close');
  Object.assign(modelState, {
    brandId: undefined,
    productId: undefined,
    shieldReason: undefined,
  });
  blackList.value = [];
  visible.value = false;
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
    const params = blackList.value.map((item) => {
      return {
        ...item,
        shieldReason: modelState.shieldReason,
      };
    });

    const res = await defHttp.post({
      url: '/kol/kolshields/add',
      data: params,
    },{isTransformResponse:false});

    if (res.success) {
      $message.success(res.message);
      emit('ok');
      close();
    } else {
      $message.warning(res.message);
    }
  } catch (error) {
    console.error(error);
  } finally {
    confirmLoading.value = false;
  }
}

// 取消
function handleCancel() {
  close();
}

// 暴露方法
defineExpose({
  show,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
  (e: 'close'): void;
}>();
</script>

<style lang="less" scoped>
.personalTagsBox {
  border: 1px solid #d9d9d9;
  width: 100%;
  min-height: 34px;
  padding: 2px 10px;
  border-radius: 3px;
  box-sizing: border-box;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  position: relative;
  max-height: 150px;
  overflow-y: auto;
}

.personalTagsSelect {
  min-height: 33px;
  margin-top: 10px;
  width: 100%;
  background: #fff;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  height: 200px;
  overflow-y: auto;
  overflow-x: hidden;

  div {
    height: 23px;
    line-height: 23px;
    border-radius: 8px;
    font-size: 12px;
    padding: 0 10px;
    margin: 5px;
    cursor: pointer;
    background: #e7e7f0;
    border: none;
    user-select: none;
  }

  .currentTag {
    background: #fff;
    border: 1px solid #3155ed;
    color: #3155ed;
  }
}
</style>
