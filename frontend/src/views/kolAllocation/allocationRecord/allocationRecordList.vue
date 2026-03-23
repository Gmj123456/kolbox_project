<template>
  <a-card :bordered="false">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <JDictSelectPlatformType  v-model:value="queryParam.platformType" dictCode="platform_type" />
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model:value="queryParam.allotStatus" placeholder="分配状态" allowClear>
                <a-select-option v-for="item in allotStatusList" :key="item.value" :value="item.value">
                  {{ item.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model:value="queryParam.allotType" placeholder="分配类型" allowClear>
                <a-select-option v-for="item in allotTypeList" :key="item.value" :value="item.value">
                  {{ item.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <a-select
                v-model:value="queryParam.allotCounselorNames"
                show-search
                allowClear
                placeholder="分配顾问"
                option-filter-prop="label"
              >
                <a-select-option v-for="user in userList" :key="user.username" :value="user.username" :label="user.username">
                  {{ user.username }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.allotContent" allowClear placeholder="分配内容" />
            </a-form-item>
          </a-col>
        
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <a-select
                v-model:value="queryParam.createBy"
                show-search
                allowClear
                placeholder="分配人"
                option-filter-prop="label"
              >
                <a-select-option v-for="user in userList" :key="user.username" :value="user.username" :label="user.username">
                  {{ user.username }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="12" :md="24" :sm="24">
            <a-form-item>
              <a-space>
                <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
                <a-button :icon="h(ReloadOutlined)" @click="handleSearchReset">重置</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
   

    <s-table
    size="small"  
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      :rangeSelection="false"
      :scroll="{ y: sTableHeight, x: '100%' }"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'platformType'">
          <img style="width: 20px;height: 20px;" v-if="platformTypeImg(record.platformType)" :src="platformTypeImg(record.platformType)" class="platform-icon" />
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'allotStatus'">
          <a-tag :color="parseAllotStatusColor(record.allotStatus)">
            {{ parseAllotStatus(record.allotStatus) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'allotType'">
          {{ parseAllotType(record.allotType) }}
        </template>
        <template v-else-if="['allotCounselorNames', 'allotContent'].includes(column.key)">
          <EllipsisTooltip :text="text" />
        </template>
        <template v-else-if="column.key === 'allotEndTime'">
          <span v-if="record.allotStartTime && record.allotEndTime">
            {{ calculateDuration(record.allotStartTime, record.allotEndTime) }}
          </span>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'allotResult'">
          <EllipsisTooltip :text="parseAllotResult(record)" />
        </template>
        <template v-else>
          {{ text || '--' }}
        </template>
      </template>
    </s-table>

   
  </a-card>
</template>

<script setup name="allocationRecordList">
import { h, ref } from 'vue';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import { useTable } from '/@/components/useSTable/useSTable';
import { defHttp } from '/@/utils/http/axios';
import { getDictItems } from '@/api/common/api';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import JDictSelectPlatformType from "@/components/jeecg/JDictSelectPlatformType.vue";
import dayjs from 'dayjs';

const Api = {
  List: '/kol/kolAllotLog/list',
};

const columns = [
  { title: '#', key: 'index', width: 60 ,align: 'center'},
  { title: '平台', dataIndex: 'platformType', key: 'platformType', width: 80 },

  { title: '分配类型', dataIndex: 'allotType', key: 'allotType', width: 120 },
  { title: '分配内容', dataIndex: 'allotContent', key: 'allotContent' },
  { title: '分配顾问', dataIndex: 'allotCounselorNames', key: 'allotCounselorNames', width: 180 },
  { title: '分配开始时间', dataIndex: 'allotStartTime', key: 'allotStartTime', width: 160 },
  { title: '分配时长', dataIndex: 'allotEndTime', key: 'allotEndTime', width: 140 },
  { title: '分配人', dataIndex: 'createBy', key: 'createBy', width: 120 },
  { title: '分配状态', dataIndex: 'allotStatus', key: 'allotStatus', width: 120 },
  { title: '分配结果', dataIndex: 'allotResult', key: 'allotResult' },
];

async function loadDataApi(params) {
  const res = await defHttp.get({ url: Api.List, params });
  return res || [];
 
}

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
  searchQuery,
  searchReset,
} = useTable(loadDataApi);

queryParam.value = {
  allotStatus: undefined,
  allotType: undefined,
  allotContent: '',
  allotCounselorNames: undefined,
  createBy: undefined,
};

const userList = ref([]);
const allotStatusList = ref([]);
const allotTypeList = ref([]);

const platformIcons = {
  0: new URL('../../../assets/images/ins.png', import.meta.url).href,
  1: new URL('../../../assets/images/yt.png', import.meta.url).href,
  2: new URL('../../../assets/images/tk.png', import.meta.url).href,
};

function platformTypeImg(type) {
  return platformIcons[type] || '';
}

function calculateDuration(startTime, endTime) {
  if (!startTime || !endTime) return '--';
  const start = dayjs(startTime, 'YYYY-MM-DD HH:mm:ss');
  const end = dayjs(endTime, 'YYYY-MM-DD HH:mm:ss');
  const seconds = end.diff(start, 'seconds');
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  if (minutes <= 0 && remainingSeconds <= 0) return '0秒';
  return `${minutes > 0 ? `${minutes}分` : ''}${remainingSeconds > 0 ? `${remainingSeconds}秒` : ''}`;
}

function parseAllotType(value) {
  const item = allotTypeList.value.find((dict) => dict.value == value);
  return item ? item.title : '--';
}

function parseAllotStatus(value) {
  const item = allotStatusList.value.find((dict) => dict.value == value);
  return item ? item.title : '--';
}

function parseAllotStatusColor(value) {
  const item = allotStatusList.value.find((dict) => dict.value == value);
  return item ? item.description : 'blue';
}

function parseAllotResult(record) {
  if (!record?.allotResult) {
    return '--';
  }
  try {
    const result = JSON.parse(record.allotResult);
    if (record.allotStatus == 9 && Array.isArray(result)) {
      return result.map((item) => `${item.name}:${item.isAllotNum}`).join(',');
    }
    return result?.message || '--';
  } catch (error) {
    return record.allotResult;
  }
}


function handleSearchReset() {
  searchReset();
  // Object.assign(queryParam.value, {
  //   allotStatus: undefined,
  //   allotType: undefined,
  //   allotContent: '',
  //   allotCounselorNames: undefined,
  //   createBy: undefined,
  // });
  // fetchTable(1);
}

async function initDict() {
  try {
    const [statusRes, typeRes] = await Promise.all([
      getDictItems('allot_status'),
      getDictItems('allot_type'),
    ]);
    if (statusRes) allotStatusList.value = statusRes || [];
    if (typeRes) allotTypeList.value = typeRes || [];
  } catch (error) {
    console.error(error);
  }
}

async function initUserList() {
  try {
    const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
    userList.value = Array.isArray(res) ? res : [];
  } catch (error) {
    console.error(error);
  }
}

initDict();
initUserList();
fetchTable(1);
</script>

<style scoped lang="less">
/deep/ .ant-radio-button-wrapper:not(:first-child)::before {
  width: 0.1px !important;
  background-color: transparent;
}
/deep/ .ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before {
  background-color: #3155ed !important;
}
</style>