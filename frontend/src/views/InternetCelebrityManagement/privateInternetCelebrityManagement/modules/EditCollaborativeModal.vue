<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="600"
    v-model:open="visible"
    centered
    :confirmLoading="confirmLoading"
    @ok="handleOk()"
    class="EditCollaborativeModal"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef">
        <a-row :gutter="12">
          <a-col
            :span="24"
            v-for="(item, index) in model.collaborativeProducts"
            :key="index"
          >
            <a-form-item
              :label="`历史合作${index + 1}`"
              :labelCol="labelColAll"
              :wrapperCol="wrapperColAll"
            >
              <a-input-group  compact>
                <a-select
                  style="width: 130px;border-right: 0px;"
                  show-search
                  allowClear
                  option-filter-prop="label"
                  v-model:value="item.brandId"
                  :placeholder="`品牌${index + 1}`"
                  @change="(v) => brandChange(v, index)"
                >
                  <a-select-option v-for="brand in brandList" :key="brand.id" :value="brand.id" :label="brand.brandName">{{
                    brand.brandName
                  }}</a-select-option>
                </a-select>
                <a-select
                  style="width: calc(100% - 130px - 20px - 68px - 130px);border-right: 0px;"
                  show-search
                  option-filter-prop="label"
                  v-model:value="item.productId"
                  placeholder="合作产品"
                >
                  <a-select-option v-for="product in item.productList" :key="product.id" :value="product.id" :label="product.text">{{
                    product.text
                  }}</a-select-option>
                </a-select>
                <a-select
                  style="width: 130px;border-right: 0px;"
                  show-search
                  allowClear
                  option-filter-prop="label"
                  v-model:value="item.celebrityCounselorId"
                  placeholder="网红顾问"
                >
                  <a-select-option v-for="celebrityConsultant in celebrityConsultantList" :key="celebrityConsultant.id" :value="celebrityConsultant.id" :label="celebrityConsultant.username">{{
                    celebrityConsultant.username
                  }}</a-select-option>
                </a-select>
                <span style="margin-left: 10px; display: inline-block; margin-top: 7px;    border-right: 0px;">
                  <a>
                    <MinusCircleOutlined
                      style="font-size: 18px; margin: 0px 8px; color: red"
                      @click="removeProduct(index)"
                    />
                  </a>
                  <a v-if="index == model.collaborativeProducts.length - 1">
                    <PlusCircleOutlined
                      style="font-size: 18px; margin: 0px 8px"
                      @click="addProduct(index)"
                    />
                  </a>
                </span>
              </a-input-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>
<script setup>
import { ref, nextTick } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { getBrandListApi, getProductListApi } from '/@/api/sys/systemCommon';
import { MinusCircleOutlined, PlusCircleOutlined } from '@ant-design/icons-vue';
import {queryAllCelebrityCounselorApi} from '/@/api/sys/systemCommon'
const { createMessage: $message } = useMessage();

// 响应式数据
const title = ref('编辑历史合作');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);
const productList = ref([]);
const brandList = ref([]);
const celebrityConsultantList = ref([]);
const model = ref({
  productSelectIds: [],
  productCooperateIds: [],
  collaborativeProducts: [],
});

const labelColAll = {
  xs: { span: 24 },
  sm: { span: 4 },
};

const wrapperColAll = {
  xs: { span: 24 },
  sm: { span: 20 },
};

// 定义 emits
const emit = defineEmits(['close', 'ok']);

// 方法
function addProduct(_index) {
  model.value.collaborativeProducts.push({
    brandId: undefined,
    productId: undefined,
    celebrityCounselorId: undefined,
    productList: [],
  });
  nextTick(() => {
    const modalBody = document.querySelector('.ant-modal-body');
    if (modalBody) {
      modalBody.scrollTop = modalBody.scrollHeight;
    }
  });
}

function removeProduct(index) {
  model.value.collaborativeProducts.splice(index, 1);
}

function brandChange(value, index) {
  model.value.collaborativeProducts[index].productId = undefined;
  model.value.collaborativeProducts[index].productList = [];
  if (value) {
    getBrandProductList(index);
  }
}
function celebrityCounselorChange(value, index) {
  model.value.collaborativeProducts[index].celebrityCounselorId = value;
}
function getBrandProductList(index) {
  const brandProductList = productList.value.filter(
    (item) => item.brandId === model.value.collaborativeProducts[index].brandId
  );
  model.value.collaborativeProducts[index].productList = brandProductList;
  if (brandProductList.length == 1) {
    model.value.collaborativeProducts[index].productId = brandProductList[0].id;
  }
}

async function initBrandList() {
  const res = await getBrandListApi({});
  brandList.value = res || [];
}

async function initProductList() {
  const res = await getProductListApi({});
  const list = res || [];
  productList.value = list.map((item) => ({
    ...item,
    text: item.productName,
  }));

  if (model.value.id) {
    const collaborativeProducts = [];
    if (model.value.productCooperateIds) {
      JSON.parse(model.value.productCooperateIds).forEach((item) => {
        collaborativeProducts.push({
          brandId: item.brandId,
          productId: item.productId,
          celebrityCounselorId: item.celebrityCounselorId,
          productList: productList.value.filter(
            (product) => product.brandId === item.brandId
          ),
        });
      });
    } else {
      collaborativeProducts.push({
        brandId: undefined,
        productId: undefined,
        celebrityCounselorId: undefined,
        productList: [],
      });
      collaborativeProducts.push({
        brandId: undefined,
        productId: undefined,
        celebrityCounselorId: undefined,
        productList: [],
      });
    }
    model.value.collaborativeProducts =
      collaborativeProducts.length > 0
        ? collaborativeProducts
        : [{ brandId: undefined, productId: undefined, celebrityCounselorId: undefined, productList: [] }];
  }
}
async function queryAllCelebrityCounselor() {
    const res = await queryAllCelebrityCounselorApi({});
    celebrityConsultantList.value = Array.isArray(res)
      ? res
      : [];
  }
function edit(record) {
  visible.value = true;
  model.value = { ...record };
  console.log(model.value);
  celebrityConsultantList.value  = record.privateCounselorList.map(item => ({
    id: item.celebrityCounselorId,
    username: item.celebrityCounselorName,
  }));
  console.log(celebrityConsultantList.value);
  initBrandList();
  initProductList();
  // queryAllCelebrityCounselor()
}

async function handleOk() {
  try {
    await formRef.value.validate();
    for (let i = 0; i < model.value.collaborativeProducts.length; i++) {
      const item = model.value.collaborativeProducts[i];
      const hasBrand = !!item.brandId;
      const hasProduct = !!item.productId;
      const hasCounselor = !!item.celebrityCounselorId;
      if (!hasBrand && !hasProduct && !hasCounselor) continue;
      if (!hasBrand) {
        $message.warning(`请选择历史合作${i + 1}的品牌`);
        return;
      }
      if (!hasProduct) {
        $message.warning(`请选择历史合作${i + 1}的合作产品`);
        return;
      }
      if (!hasCounselor) {
        $message.warning(`请选择历史合作${i + 1}的网红顾问`);
        return;
      }
    }
    confirmLoading.value = true;
    const params = {
      id: model.value.id,

      privateProducts: model.value.collaborativeProducts
        .filter(item => item.brandId || item.productId || item.celebrityCounselorId)
        .map(item => ({
          brandId: item.brandId,
          productId: item.productId,
          celebrityCounselorId: item.celebrityCounselorId,
          celebrityCounselorName: celebrityConsultantList.value.find(c => c.id === item.celebrityCounselorId)?.username,
        }))
      
    };
    const res = await defHttp.put({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/edit',
      data: params,
    });
    emit('ok');
    handleCancel();
   
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  visible.value = false;
  model.value = {
    productSelectIds: [],
    productCooperateIds: [],
    collaborativeProducts: [],
  };
}

// 暴露方法给父组件
defineExpose({
  edit,
});
</script>
<style lang="less" >
.EditCollaborativeModal .ant-modal-body {
  height: 289px !important;
  overflow-y: scroll !important;
}
</style>
