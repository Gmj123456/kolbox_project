<template>
  <a-modal
    :maskClosable="false"
    title="产品编辑"
    :width="400"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-item label="产品图片">
          <a-avatar shape="square" :size="64" icon="picture" :src="model.goodsPicUrl" />
        </a-form-item>
        <a-form-item label="产品名称" name="kolProductId">
          <a-select
            show-search
            option-filter-prop="label"
            @change="productChange"
            v-model:value="model.kolProductId"
            style="width: 100%"
            placeholder="请选择产品"
          >
            <a-select-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.text">
              {{ item.text }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="品牌">
          <a-input disabled style="width: 100%" v-model:value="model.brandName" />
        </a-form-item>
        <!-- <a-form-item label="产品型号">
          <a-input disabled style="width: 100%" v-model:value="model.productModel" />
        </a-form-item> -->

        <a-form-item label="产品链接">
          <a-input disabled style="width: 100%" v-model:value="model.goodsUrl" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();

// 表单引用
const formRef = ref(null);

// 状态
const visible = ref(false);
const confirmLoading = ref(false);
const productList = ref([]);

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
const model = ref({
  id: undefined,
  promId: undefined,
  kolProductId: undefined,
  goodsPicUrl: undefined,
  brandName: undefined,
  productModel: undefined,
  goodsUrl: undefined,
  goodsRemark: undefined,
});

// API 端点
const url = {
  add: '/storeSellerPromotion/add',
  edit: '/storepromotiongoods/storePromotionGoods/edit',
};

// 表单验证规则
const rules = {
  kolProductId: [{ required: true, message: '请选择产品', trigger: 'change' }],
};



// 产品选择变化
function productChange(value) {
  console.log(value);
  if (value) {
    setProductInfo();
  } else {
    model.value.goodsUrl = '';
    model.value.goodsPicUrl = '';
    model.value.brandName = '';
    model.value.productModel = '';
  }
}

// 初始化产品列表
async function initProduct() {
  try {
    const res = await defHttp.get({ url: '/kol/kolProduct/listAll' });
    if (res) {
      productList.value = res.map((item) => {
        return {
          ...item,
          text: item.productName,
        };
      });
      if (model.value.kolProductId) {
        setProductInfo();
      }
    }
  } catch (error) {
    console.error('initProduct error:', error);
  }
}

// 设置产品信息
function setProductInfo() {
  const product = productList.value.find((item) => item.id === model.value.kolProductId);
  if (product) {
    model.value.goodsPicUrl = product.productImage;
    model.value.brandName = product.brandName;
    model.value.productModel =
      product.productAttributes && JSON.parse(product.productAttributes).productModel
        ? JSON.parse(product.productAttributes).productModel
        : undefined;
    model.value.goodsUrl = product.productUrl;
  }
}

// 新增
function add() {
  edit({});
}

// 编辑
function edit(record) {
  Object.assign(model.value, record);
  visible.value = true;
  console.log(record);
  initProduct();
}

// 关闭
function close() {
  emit('close');
  visible.value = false;
}

// 取消
function handleCancel() {
  close();
}

// 确认
async function handleOk() {
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (!model.value.id) {
      httpurl = url.add;
      method = 'post';
    } else {
      httpurl = url.edit;
      method = 'put';
    }

    const formData = {
      id: model.value.id,
      promId: model.value.promId,
      kolProductId: model.value.kolProductId,
      goodsTitle: productList.value.find((item) => item.id === model.value.kolProductId)?.productName,
      goodsPicUrl: model.value.goodsPicUrl,
      goodsRemark: model.value.goodsRemark,
      goodsUrl: model.value.goodsUrl,
    };

    console.log(formData);

    let res;
    if (method === 'post') {
      res = await defHttp.post({ url: httpurl, data: formData },{isTransformResponse:false});
    } else {
      res = await defHttp.put({ url: httpurl, data: formData },{isTransformResponse:false});
    }

    if (res.success) {
      createMessage.success(res.message);
      emit('ok');
    } else {
      createMessage.warning(res.message);
    }
  } catch (error) {
    console.error('handleOk error:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
    close();
  }
}

// 定义事件
const emit = defineEmits(['ok', 'close']);

// 暴露方法给父组件
defineExpose({
  add,
  edit,
  close,
});
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
