<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="400"
    :open="visible"
    :confirmLoading="confirmLoading"
    cancelText="关闭"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="modelState">
        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item label="产品" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-group compact>
                <a-select
                  style="width: 130px"
                  placeholder="请选择品牌"
                  showSearch
                  v-model:value="modelState.brandId"
                  @change="onBrandChange"
                  allowClear
                  option-filter-prop="label"
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
                  style="width: calc(100% - 130px)"
                  :showSearch="true"
                  option-filter-prop="label"
                  v-model:value="modelState.productId"
                  placeholder="请选择产品"
                  allowClear
                >
                  <a-select-option
                    v-for="item in productList"
                    :key="item.id"
                    :value="item.id"
                    :label="item.text"
                  >
                    {{ item.text }}
                  </a-select-option>
                </a-select>
              </a-input-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <div v-if="dataSource.length > 0">
        <div style="font-size: 16px; font-weight: 600; margin: 10px 0">失败条数</div>
        <s-table
          size="small"
          :columns="columns"
          :pagination="false"
          :data-source="dataSource"
          bordered
        >
          <template #bodyCell="{ text, record, index, column }">
            <template v-if="column.key === 'rowIndex'">
              {{ index + 1 }}
            </template>
            <template v-if="column.key === 'uniqueId'">
              <span v-if="record.platformType == 1">
                {{ record.username }}
              </span>
              <span v-if="record.platformType == 2">
                {{ record.uniqueId }}
              </span>
            </template>
          </template>
        </s-table>
      </div>
    </a-spin>
    <template #footer>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <div>
          已选择<a>{{ markerDevelopingList.length }}</a>个
        </div>
        <div>
          <a-button key="back" @click="handleCancel"> 关闭 </a-button>
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

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const title = ref('标记开发');
const visible = ref(false);
const confirmLoading = ref(false);
const productList = ref<any[]>([]);
const markerDevelopingList = ref<any[]>([]);
const brandList = ref<any[]>([]);
const platformType = ref('');
const dataSource = ref<any[]>([]);

// 布局配置
const labelCol = {
  xs: { span: 24 },
  sm: { span: 3 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 21 },
};

// 表单数据
const modelState = reactive<any>({
  brandId: undefined,
  productId: undefined,
});

// 表格列
const columns = [
  {
    title: '#',
    key: 'rowIndex',
    width: 60,
    align: 'center',
  },
  {
    title: '账号',
    key: 'uniqueId',
    width: 100,
  },
];

// 表单验证规则
const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
};


// 品牌变化
function onBrandChange(value: any) {
  modelState.productId = undefined;
  if (value) {
    initProduct();
  } else {
    productList.value = [];
  }
}

// 初始化品牌列表
async function initBrand() {
  try {
    const res = await defHttp.get({ url: '/kolBrand/listAll' });
    if (res) {
      brandList.value = res;
    }
  } catch (error) {
    console.error(error);
  }
}

// 初始化产品列表
async function initProduct() {
  try {
    const res = await defHttp.get({
      url: '/kol/kolProduct/listAll',
      params: {
        brandId: modelState.brandId,
      },
    });
    if (res) {
      productList.value = res.map((item: any) => {
        return {
          ...item,
          text: item.productName,
        };
      });
      if (productList.value.length == 1) {
        modelState.productId = productList.value[0].id;
      }
    }
  } catch (error) {
    console.error(error);
  }
}

// 显示
function show(platformTypeValue: string, markerDevelopingListData: any[]) {
  markerDevelopingList.value = [...markerDevelopingListData];
  platformType.value = platformTypeValue;
  visible.value = true;
  initBrand();
}

// 关闭
function close() {
  emit('close');
  Object.assign(modelState, {
    brandId: undefined,
    productId: undefined,
  });
  dataSource.value = [];
  productList.value = [];
  visible.value = false;
  formRef.value?.clearValidate();
}

// 提交
async function handleOk() {
  if (!modelState.brandId) {
    $message.warning('请选择品牌');
    return;
  }
  if (!modelState.productId) {
    $message.warning('请选择产品');
    return;
  }

  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    const params = {
      productId: modelState.productId,
      platformType: platformType.value,
      kolList: markerDevelopingList.value,
    };

    const res = await defHttp.post({
      url: '/kol/kolContact/createContact',
      data: params,
    },{isTransformResponse: false});

    if (res.success) {
      $message.success(res.message);
      emit('ok');
      close();
    } else {
      $message.error(res.message);
      if (Array.isArray(res.result)) {
        dataSource.value = res.result;
      }
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
