<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="450"
    v-model:visible="visibleModal"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-alert
        style="margin-bottom: 6px"
        v-if="participateInternet"
        :message="'分配时间约为' + getRecommendedTimeRange(participateInternet).rangeText"
        type="info"
        show-icon
      />
      <a-form
        :model="queryParam"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 19 }"
      >
        <a-row :gutter="12" v-if="queryParam.productId">
          <a-col :span="24">
            <a-form-item label="产品名称">
              <a-input disabled v-model:value="queryParam.productName"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <template v-if="queryParam.attributeIds">
          <a-row :gutter="12" v-for="item in attributeList" :key="item.typeId">
            <a-col
              :span="24"
              v-if="parseAttributeIds(queryParam.attributeIds, item.data)"
            >
              <a-form-item :label="item.typeName">
                <a-tag style="display: inline-flex">{{
                  parseAttributeIds(queryParam.attributeIds, item.data)
                }}</a-tag>
              </a-form-item>
            </a-col>
          </a-row>
        </template>
        <a-row
          :gutter="12"
          v-if="queryParam.tagNameList && queryParam.tagNameList.length > 0"
        >
          <a-col :span="24" style="margin-bottom: 6px">
            <vxe-table
              :data="queryParam.tagNameDataList"
              size="small"
              height="300"
              show-footer
              :footer-method="footerMethod"
            >
              <vxe-table-column type="seq" width="60px" align="center"></vxe-table-column>
              <vxe-table-column field="tagName" title="标签"></vxe-table-column>
              <vxe-table-column field="tagTypeName" title="类型" width="100px">
              </vxe-table-column>
            </vxe-table>
          </a-col>
        </a-row>
      </a-form>
      <a-form
        :model="model"
        ref="formRef"
        :rules="rules"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 19 }"
      >
        <a-form-item prop="counselorId" label="网红顾问">
          <a-select
         
            option-filter-prop="label"
            mode="multiple"
            v-model:value="model.counselorId"
            allowClear
            placeholder="请选择网红顾问(必填)"
              :options="celebrityConsultantsList.map(item => ({ value: item.id, label: item.username }))"
          >
          
            <template #dropdownRender="{ menuNode }">
              <v-nodes :vnodes="menuNode" />
              <a-divider style="margin: 4px 0" />
              <a-button style="padding:6px 12px;" v-if="(model.counselorId || []).length !== celebrityConsultantsList.length" type="link" @click="handleAllCounselorIdChange">选择全部</a-button>
              <a-button style="padding:6px 12px;" v-else  type="link" @click="closeAllCounselorIdChange">取消全部</a-button>
            </template>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>
    <template #footer>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <div style="display: flex; align-items: center">
          <div style="color: #0b1019 !important; font-size: 12px">待分配网红:</div>
          <a-button
            style="color: #3155ed !important; font-weight: 600"
            :loading="participateInternetLoading"
            type="link"
            >{{ participateInternet }}</a-button
          >
        </div>
        <div>
          <a-button key="back" :loading="confirmLoading" @click="handleCancel"
            >关闭</a-button
          >
          <a-button
            :disabled="participateInternet === 0"
            key="submit"
            type="primary"
            :loading="confirmLoading"
            @click="handleOk"
            >分配</a-button
          >
        </div>
      </div>
    </template>
  </a-modal>
</template>

<script setup>
import { ref, defineComponent } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';

const emit = defineEmits(['ok', 'close']);

const { createMessage } = useMessage();

const title = ref('分配');
const visibleModal = ref(false);
const confirmLoading = ref(false);
const participateInternet = ref(0);
const participateInternetLoading = ref(false);
const queryParam = ref({});
const celebrityConsultantsList = ref([]);
const attributeList = ref([]);
const formRef = ref(null);

const model = ref({
  counselorId: undefined,
});
const VNodes = defineComponent({
  props: {
    vnodes: {
      type: Object,
      required: true,
    },
  },
  render() {
    return this.vnodes;
  },
});
const rules = {
  counselorId: [{ required: true, message: '请选择网红顾问', trigger: 'change' }],
};

const url = ref('');

function footerMethod({ columns }) {
  return [
    columns.map((column) => {
      if (!column.field) {
        return '合计';
      }
      if (column.field === 'tagName') {
        return `${queryParam.value.tagNameDataList?.length || 0}`;
      }
      return '';
    }),
  ];
}

function parseAttributeIds(attributeIds, data) {
  if (!attributeIds || !data) return '';
  const found = data.find((item) => item.id === attributeIds);
  return found ? found.attributeName : '';
}


function getRecommendedTimeRange(totalPeople) {
  // 基础档：5000人及以下，推荐3-5分钟
  const baseMin = 3;
  const baseMax = 5;

  // 每5000人为一档
  const tier = Math.ceil(totalPeople / 5000); // 向上取整得到档位

  // 策略：每增加一档，时间范围线性增加（例如每档+1分钟）
  const incrementPerTier = 1; // 每档增加1分钟（可调）

  const minTime = baseMin + (tier - 1) * incrementPerTier;
  const maxTime = baseMax + (tier - 1) * incrementPerTier;

  return {
    min: minTime,
    max: maxTime,
    tier: tier,
    totalPeople: totalPeople,
    // 格式化为 "3-5分钟" 字符串
    rangeText: `${minTime}-${maxTime}分钟`,
  };
}

async function initAttributeList() {
  try {
    const res = await defHttp.get({
      url: '/kol/attribute/getKolAttribute',
      params: {
        isParent: 1,
      },
    });
    if (res) {
      attributeList.value = res;
    }
  } catch (error) {
    console.error('获取属性列表失败:', error);
  }
}

async function initBrandList() {
  try {
    const res = await defHttp.get({ url: '/kolBrand/listAll' });
    if (res.success) {
      // brandList 未在模板中使用，但保留初始化逻辑
    }
  } catch (error) {
    console.error('获取品牌列表失败:', error);
  }
}

async function initProductList() {
  try {
    const res = await defHttp.get({
      url: '/kol/kolProduct/listAll',
      params: {
        brandId: queryParam.value.brandId,
      },
    });
    if (res.success) {
      // productList 未在模板中使用，但保留初始化逻辑
    }
  } catch (error) {
    console.error('获取产品列表失败:', error);
  }
}

function togglePlatformType(value) {
  if (value == 1) {
    url.value = '/youtube/ytCelebrityTags/allotTagsNew';
  } else if (value == 2) {
    url.value = '/tiktok/tkCelebrityTags/allotTagsNew';
  } else {
    url.value = '/instagram/igCelebrityTags/allotTagsNew';
  }
}

async function initlCelebrityConsultant() {
  try {
    const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
    if (res) {
      celebrityConsultantsList.value = res
      // [
      //   {
      //     id: "100",
      //     username: "全部",
      //     realname: null,
      //     shortName: null
      //   },
      //   ...res
      // ]
    }
  } catch (error) {
    console.error('获取网红顾问列表失败:', error);
  }
}

function handleAllCounselorIdChange() {
  model.value.counselorId = celebrityConsultantsList.value.map(item => item.id);
   formRef.value.validate();
}

function closeAllCounselorIdChange() {
  model.value.counselorId = undefined;
}

function handleCounselorIdChange(value) {
  console.log(value)
  // if (value == 100) {
  //   model.counselorId = celebrityConsultantsList.value.map(item => item.id);
  // } else {
  //   model.counselorId = [...(model.counselorId || [])];
  // }
}
function close() {
  visibleModal.value = false;
  participateInternet.value = 0;
  model.value.counselorId = undefined;
  formRef.value?.resetFields();
  emit('close');
  queryParam.value = {};
}

async function handleOk() {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
  } catch {
    return;
  }

  if (participateInternet.value === 0) {
    createMessage.warning('暂无需要分配的网红');
    return;
  }

  confirmLoading.value = true;
  const param = {
    ...queryParam.value,
  };
  
  if (param.productId) {
    param.allotType = 2;
  } else if (param.attributeIds) {
    param.allotType = 3;
  } else {
    param.allotType = 1;
  }
  // if (model.counselorId == 100) {
  //   param.celebrityCounselorList =  celebrityConsultantsList.value.map(item => item.id);
  // } else {
    // }
      param.celebrityCounselorList = [...(model.value.counselorId || [])];


  try {
    const res = await defHttp.post({
      url: url.value,
      data: param,
    }, { isTransformResponse: false });
    
    if (res.success) {
      createMessage.success(res.message);
      close();
      emit('ok');
    } else {
      createMessage.error(res.message);
    }
  } catch (error) {
    console.error('分配失败:', error);
    createMessage.error('网络波动，请重新分配');
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

function openModalTable(participateInternetValue, queryParamValue) {
  if (
    queryParamValue.productId &&
    queryParamValue.tagNameList &&
    queryParamValue.tagNameList.length > 0
  ) {
    title.value = '按产品、标签分配';
  } else if (queryParamValue.productId && queryParamValue.attributeIds) {
    title.value = '按产品、达人类型分配';
  } else if (queryParamValue.productId) {
    title.value = '按产品分配';
  } else if (queryParamValue.attributeIds) {
    title.value = '按达人类型分配';
  } else {
    title.value = '按标签分配';
  }
  
  initlCelebrityConsultant();
  initAttributeList();
  // initBrandList();
  // initProductList();
  participateInternet.value = participateInternetValue || 0;
  queryParam.value = Object.assign({}, queryParamValue);
  
  if (queryParam.value.tagNameList && queryParam.value.tagNameList.length > 0) {
    queryParam.value.tagNameDataList = queryParamValue.tagNameList.map((item, index) => {
      return {
        id: index,
        tagName: item,
        tagTypeName: queryParam.value.tagTypeName,
      };
    });
  }
  
  togglePlatformType(queryParam.value.platformType);
  console.log(queryParam.value)
  visibleModal.value = true;
}

function updateCount(res) {
  console.log(res)
  if (visibleModal.value) {
    participateInternet.value = res.notAllotCount;
  }
}

defineExpose({
  openModalTable,
  updateCount,
});
</script>

<style lang="less" scoped></style>
