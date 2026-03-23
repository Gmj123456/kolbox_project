<template>
  <a-modal
    centered
    title="更换商务"
    :open="visible"
    :width="1200"
    @cancel="handleCancel"
    @ok="handleOk"
    :confirmLoading="confirmLoading"
  >
    <a-form>
      <a-row >
        <a-col :span="4">
          <a-form-item>
            <a-select
             
              v-model:value="queryParam.counselorUserId"
              placeholder="商务人员"
              @change="changeUser($event,'0')"
              allowClear
            >
              <a-select-option v-for="item in platformStaff" :key="item.id" :value="item.id">
                <a-tooltip :title="item.realname">
                  {{ item.username }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="4" style="margin-left: 8px;">
          <a-form-item>
            <a-select v-model:value="queryParam.getSellerCounselorId" placeholder="商家顾问" allowClear>
              <a-select-option v-for="item in storeUserCounselorList" :key="item.id" :value="item.id">
                <a-tooltip :title="item.username">
                  {{ item.username }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="2" >
          <a-button type="primary" style="margin-left: 8px;" @click="searchQuery" :icon="h(SearchOutlined)">查询</a-button>
        </a-col>
        <a-col :span="4" style="margin-left: 110px;">
          <a-form-item>
            <a-select
              
              v-model:value="queryParam.setCounselorUserId"
              placeholder="商务人员"
              @change="changeUser($event,'1')"
              allowClear
            >
              <a-select-option v-for="item in platformStaff" :key="item.id" :value="item.id">
                <a-tooltip :title="item.realname">
                  {{ item.username }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="4" style="margin-left: 8px;">
          <a-form-item>
            <a-select
              
              v-model:value="queryParam.sellerCounselorId"
              placeholder="商家顾问"
              allowClear
            >
              <a-select-option v-for="item in setStoreUserCounselorList" :key="item.id" :value="item.id">
                <a-tooltip :title="item.username">
                  {{ item.username }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-spin :spinning="loading">
      <a-transfer
        :loading="loading"
        :data-source="mockData"
        v-model:target-keys="targetKeys"
        :list-style="{
          width: '555px',
          height: '500px',
        }"
        :show-select-all="false"
        :render="(item) => item.title"
     
      >
        <template
          #children="{
            direction,
            filteredItems,
            selectedKeys,
            onItemSelectAll,
            onItemSelect,
          }"
        >
          <a-table
            class="table-page-background-one"
            :row-selection="
              getRowSelection({ selectedKeys,  onItemSelectAll, onItemSelect })
            "
            :pagination="false"
            :columns="direction === 'left' ? leftColumns : rightColumns"
            :data-source="filteredItems"
            :scroll="{y:400}"
            size="middle"
          />
        </template>
      </a-transfer>
    </a-spin>
  </a-modal>
</template>
<script setup>
import { ref, h } from 'vue';
import { SearchOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();
const emit = defineEmits(['close', 'ok']);

const visible = ref(false);
const confirmLoading = ref(false);
const loading = ref(false);
const mockData = ref([]);
const queryParam = ref({
  counselorUserId: undefined,
  setCounselorUserId: undefined,
  extensionCode: undefined,
  sellerCounselorId: undefined,
  getSellerCounselorId: undefined,
});

const platformStaff = ref([]);
const targetKeys = ref([]);
const storeUserCounselorList = ref([]);
const setStoreUserCounselorList = ref([]);

const leftColumns = [
  {
    title: '#',
    dataIndex: 'index',
    width: '100px',
    customRender: ({ index }) => parseInt(index) + 1,
  },
  {
    dataIndex: 'extensionCode',
    title: '推广单号',
  },
  {
    dataIndex: 'promTitle',
    title: '推广标题',
  },
];

const rightColumns = [
  {
    title: '#',
    dataIndex: 'index',
    width: '100px',
    customRender: ({ index }) => parseInt(index) + 1,
  },
  {
    dataIndex: 'extensionCode',
    title: '推广单号',
  },
  {
    dataIndex: 'promTitle',
    title: '推广标题',
  },
];

const getRowSelection = ({
  disabled,
  selectedKeys,
  onItemSelectAll,
  onItemSelect,
}) => {
return {
    getCheckboxProps: (item) => ({
      disabled: disabled || item.disabled,
    }),
    onSelectAll(selected, selectedRows) {
      const treeSelectedKeys = selectedRows.filter(item => !item.disabled).map(({ key }) => key);
      onItemSelectAll(treeSelectedKeys, selected);
    },
    onSelect({ key },selected) {
      onItemSelect(key, selected);
    },
    selectedRowKeys: selectedKeys,
  }
}

function changeUser(e, type) {
  if (e) {
    storeUserCounselor(e, type);
  }
  if (type == '0') {
    queryParam.value.getSellerCounselorId = undefined;
  } else {
    queryParam.value.sellerCounselorId = undefined;
  }
}

async function storeUserCounselor(sysUserId, type) {
  try {
    const res = await defHttp.get({ url: '/storeUserCounselor/getList', params: { sysUserId } });
    if (res) {
      if (type == '0') {
        storeUserCounselorList.value = res;
      } else {
        setStoreUserCounselorList.value = res;
      }
    } else {
      storeUserCounselorList.value = [];
    }
  } catch (error) {
    console.error('storeUserCounselor error:', error);
    storeUserCounselorList.value = [];
  }
}

function show() {
  mockData.value = [];
  targetKeys.value = [];
  queryParam.value = {
    counselorUserId: undefined,
    setCounselorUserId: undefined,
    extensionCode: undefined,
    sellerCounselorId: undefined,
    getSellerCounselorId: undefined,
  };
  visible.value = true;
  getBusinessPeople();
}

async function getBusinessPeople() {
  try {
    const res = await defHttp.get({ url: '/sys/user/getBusinessPeople' });
    if (res) {
      platformStaff.value = res;
    }
  } catch (error) {
    console.error('getBusinessPeople error:', error);
  }
}

function close() {
  emit('close');
  visible.value = false;
}

async function handleOk() {
  if (!queryParam.value.setCounselorUserId) {
    createMessage.warning('请选择需要更换的商务人员！');
    return;
  }
  if (targetKeys.value.length < 1) {
    createMessage.warning('请至少选择一个订单！');
    return;
  }
  confirmLoading.value = true;
  try {
    const res = await defHttp.get({
      url: '/storeSellerPromotion/updateCounselorUser',
      params: {
        sellerPromotionIds: targetKeys.value.join(','),
        counselorUserId: queryParam.value.setCounselorUserId,
        sellerCounselorId: queryParam.value.sellerCounselorId,
      },
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
}

function handleCancel() {
  close();
}

async function loadData() {
  if (!queryParam.value.counselorUserId) {
    createMessage.warning('请选择商务人员！');
    return;
  }
  loading.value = true;
  try {
    const res = await defHttp.get({
      url: '/storeSellerPromotion/promotionList',
      params: {
        counselorUserId: queryParam.value.counselorUserId,
        sellerCounselorId: queryParam.value.getSellerCounselorId,
      },
    },{isTransformResponse:false});
    if (res.success) {
      mockData.value = res.result.map((item, index) => ({
        ...item,
        index,
        title: `${item.extensionCode || ''} ${item.promTitle || ''}`,
        key: item.id,
      }));
      console.log(mockData.value)
    } else {
      mockData.value = [];
    }
  } catch (error) {
    console.error('loadData error:', error);
    createMessage.error('加载数据失败');
  } finally {
    loading.value = false;
  }
}

function searchQuery() {
  loadData();
}

function handleChange(nextTargetKeys) {
  targetKeys.value = nextTargetKeys;
}

function handleSelectChange(sourceSelectedKeys, targetSelectedKeys) {
  selectedKeys.value = [...sourceSelectedKeys, ...targetSelectedKeys];
}


defineExpose({
  show,
});
</script>
<style scoped>
	   .ant-table-tbody tr td{
    color: #0b1019 !important;
  }
</style>