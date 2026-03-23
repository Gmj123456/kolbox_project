<template>
  <a-modal
    :maskClosable="false"
    title="新增产品"
    :width="400"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="formState" :rules="rules" ref="formRef" :labelCol="labelCol" :wrapperCol="wrapperCol">
        <a-form-item label="产品图片">
          <a-avatar shape="square" :size="64"  :icon="h(PictureOutlined)"  :src="formState.goodsPicUrl" />
        </a-form-item>
        <a-form-item name="brandId" label="品牌">
          <a-select
            show-search
            option-filter-prop="label"
            @change="brandChange"
            allowClear
            style="width: 100%"
            v-model:value="formState.brandId"
            placeholder="请选择品牌"
          >
            <a-select-option v-for="item in brandList" :key="item.id" :value="item.id" :label="item.brandName">
              {{ item.brandName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item name="kolProductId" label="产品">
          <a-select
            allowClear
            show-search
            option-filter-prop="label"
            @change="productChange"
            v-model:value="formState.kolProductId"
            style="width: 100%"
            placeholder="请选择产品"
          >
            <a-select-option v-for="item in productList" :key="item.id" :value="item.id"  :label="item.text">
              {{ item.text }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="产品链接">
          <a-input
            disabled
            style="width: 100%"
            v-model:value="formState.goodsUrl"
            placeholder="产品链接"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive,h } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { PictureOutlined } from '@ant-design/icons-vue';
const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();
const promId = ref<string>('');

// 状态
const visible = ref(false);
const confirmLoading = ref(false);
const productList = ref<any[]>([]);
const brandList = ref<any[]>([]);

// 布局配置
const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 19 },
};

// 表单数据
const formState = reactive({
  brandId: undefined,
  kolProductId: undefined,
  goodsPicUrl: '',
  goodsUrl: '',
  brandName: '',
  productModel: '',
});

// 表单验证规则
const rules = {
  brandId: [{ required: true, message: '请选择品牌', trigger: 'change' }],
  kolProductId: [{ required: true, message: '请选择产品', trigger: 'change' }],
};

// 品牌变更
function brandChange(value: number | undefined) {
  productList.value = [];
  formState.kolProductId = undefined;
  if (value) {
    initProduct();
  } else {
    formState.kolProductId = undefined;
  }
}

// 初始化品牌列表
async function initBrandList() {
  const res = await defHttp.get({
    url: '/kolBrand/listAll',
  });
  if (res) {
    brandList.value = res;
  }
}

// 产品变更
function productChange(value: number | undefined) {
  if (value) {
    setProductInfo();
  } else {
    formState.goodsUrl = '';
    formState.goodsPicUrl = '';
    formState.brandName = '';
    formState.productModel = '';
  }
}

// 初始化产品列表
async function initProduct() {
  const res = await defHttp.get({
    url: '/kol/kolProduct/listAll',
    params: {
      brandId: formState.brandId,
    },
  });
  if (res) {
    productList.value = res.map((item: any) => ({
      ...item,
      text: item.productName,
    }));
    if (formState.kolProductId) {
      setProductInfo();
    }
  }
}

// 设置产品信息
function setProductInfo() {
  const product = productList.value.find((item) => item.id === formState.kolProductId);
  if (product) {
    formState.goodsPicUrl = product.productImage || '';
    formState.brandName = product.brandName || '';
    formState.productModel =
      product.productAttributes && JSON.parse(product.productAttributes).productModel
        ? JSON.parse(product.productAttributes).productModel
        : undefined;
    formState.goodsUrl = product.productUrl || '';
  }
}

// 打开/关闭
function close() {
  visible.value = false;
  formRef.value?.resetFields();
  Object.assign(formState, {
    brandId: undefined,
    kolProductId: undefined,
    goodsPicUrl: '',
    goodsUrl: '',
    brandName: '',
    productModel: '',
  });
}

function add(id: string) {
  promId.value = id;
  edit({});
}

function edit(record: any) {
  Object.assign(formState, record);
  visible.value = true;
  initBrandList();
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

    const product = productList.value.find((item) => item.id === formState.kolProductId);
    const formData = {
     
      promId: promId.value,
      kolProductId: formState.kolProductId,
      goodsPicUrl: formState.goodsPicUrl,
      goodsUrl: formState.goodsUrl,
      goodsTitle: product?.productName || '',
    };

    const res = await defHttp.post({
      url: '/storepromotiongoods/storePromotionGoods/add',
      data: formData,
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

function handleCancel() {
  close();
}

// 暴露方法
defineExpose({

  add,
  edit,
  close,
});

// 定义事件
const emit = defineEmits<{

  (e: 'ok'): void;
  (e: 'close'): void;
}>();
</script>

<style lang="less" scoped>
.clearfix:after {
  content: '.';
  display: block;
  height: 0;
  font-size: 0;
  clear: both;
  visibility: hidden;
}
</style>
