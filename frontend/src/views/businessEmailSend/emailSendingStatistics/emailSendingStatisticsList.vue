<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
      <a-form class="searchForm" @keyup.enter="handleSearchQuery">
        <a-row :gutter="12">
          <a-col :xl="4" :lg="14" :md="16" :sm="24" class="emailSendingStatisticsRangeradio">
            <a-form-item>
              <a-input-group compact style="display: flex; align-items: center;" >
                <a-radio-group v-model:value="dateRangeRadio" @change="onDateRangeRadioChange" >
                  <a-radio-button value="近7天">近7天</a-radio-button>
                  <a-radio-button value="近15天">近15天</a-radio-button>
                  <a-radio-button value="近30天">近30天</a-radio-button>
                  <a-radio-button value="自定义">自定义</a-radio-button>
                </a-radio-group>
                
              </a-input-group>
            </a-form-item>
          </a-col>
           <a-col :xl="4" :lg="14" :md="16" :sm="24" v-if="dateRangeRadio === '自定义'">
            <a-range-picker
                  
                  v-model:value="queryParam.dateRange"
                 
                  :placeholder="['开始发送日期', '结束发送日期']"
                />
           </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" :icon="h(SearchOutlined)" @click="handleSearchQuery">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" style="margin-left: 8px" @click="handleSearchReset">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>


    <!-- table区域 -->
    <s-table
      ref="tableRef"
      :rangeSelection="false"
      :scroll="{ y: sTableHeight, x: '100%' }"
      size="small"
      summaryFixed="bottom"
      :columns="columns"
      :data-source="dataSource"
      :ignoreCellKey="true"
      :loading="loading"
      :rowKey="record => record.id"
      :pagination="false"
      

    >
      <template #headerCell="{ column }">
        <template v-if="column.dataIndex === 'sendSuccessRate'">
          <div style="display: flex;align-items: center;">
            <span style="margin-right: 4px">成功率</span>
            <a-tooltip title="成功数/发送总数">
              <QuestionCircleOutlined />
            </a-tooltip>
          </div>
        </template>
        <template v-if="column.dataIndex === 'openRate'">
          <div style="display: flex;align-items: center;">
            <span style="margin-right: 4px">打开率</span>
            <a-tooltip title="打开数/成功数">
              <QuestionCircleOutlined />
            </a-tooltip>
          </div>
        </template>
        <template v-if="column.dataIndex === 'replyRate'">
          <div style="display: flex;align-items: center;">
            <span style="margin-right: 4px">回复率</span>
            <a-tooltip title="回复数/成功数">
              <QuestionCircleOutlined />
            </a-tooltip>
          </div>
        </template>
        <template v-if="column.dataIndex === 'bounceRate'">
          <div style="display: flex;align-items: center;">
            <span style="margin-right: 4px">失败率</span>
            <a-tooltip title="失败数/发送总数">
              <QuestionCircleOutlined />
            </a-tooltip>
          </div>
        </template>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'sendSuccessRate'">
          {{ record.sendSuccessRate }}%
        </template>
        <template v-if="column.dataIndex === 'openRate'">
          {{ record.openRate }}%
        </template>
        <template v-if="column.dataIndex === 'replyRate'">
          {{ record.replyRate }}%
        </template>
        <template v-if="column.dataIndex === 'bounceRate'">
          {{ record.bounceRate }}%
        </template>
      </template>

      <template #summary>
          <s-table-summary-row>
            <s-table-summary-cell :index="0">合计</s-table-summary-cell>
            <s-table-summary-cell :index="1"></s-table-summary-cell>
            <s-table-summary-cell :index="2">{{ emailAllData.totalSentCount }}</s-table-summary-cell>
            <s-table-summary-cell :index="3">{{ emailAllData.actualSentCount }}</s-table-summary-cell>
            <s-table-summary-cell :index="4">{{ emailAllData.sendSuccessRate }}%</s-table-summary-cell>
            <s-table-summary-cell :index="5">{{ emailAllData.openCount }}</s-table-summary-cell>
            <s-table-summary-cell :index="6">{{ emailAllData.openRate }}%</s-table-summary-cell>
            <s-table-summary-cell :index="7">{{ emailAllData.replyCount }}</s-table-summary-cell>
            <s-table-summary-cell :index="8">{{ emailAllData.replyRate }}%</s-table-summary-cell>
            <s-table-summary-cell :index="9">{{ emailAllData.bounceCount }}</s-table-summary-cell>
            <s-table-summary-cell :index="10">{{ emailAllData.bounceRate }}%</s-table-summary-cell>
          </s-table-summary-row>
      
      </template>
    </s-table>
  </a-card>
</template>

<script setup name="emailSendingStatisticsList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, QuestionCircleOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import { useUserStore } from '/@/store/modules/user';
import dayjs from 'dayjs';

const { createMessage } = useMessage();
const userStore = useUserStore();
const userInfo = userStore.getUserInfo;

const tableRef = ref(null);

// 日期范围单选：近7天 | 近15天 | 近30天 | 自定义
const dateRangeRadio = ref('近7天');

function applyPresetDateRange() {
  const today = dayjs();
  switch (dateRangeRadio.value) {
    case '近7天':
      queryParam.value.dateRange = [today.subtract(6, 'days'), today];
      break;
    case '近15天':
      queryParam.value.dateRange = [today.subtract(14, 'days'), today];
      break;
    case '近30天':
      queryParam.value.dateRange = [today.subtract(29, 'days'), today];
      break;
    case '自定义':
      // 保持用户已选日期，不覆盖
      break;
    default:
      queryParam.value.dateRange = [today.subtract(6, 'days'), today];
  }
}

function onDateRangeRadioChange() {
  if (dateRangeRadio.value === '自定义') {
    queryParam.value.dateRange = undefined;
  } else {
    applyPresetDateRange();
  }
}

const emailAllData = ref({
  bounceRate: 0,
  replyRate: 0,
  openRate: 0,
  sendSuccessRate: 0,
  totalSentCount: 0,
  actualSentCount: 0,
  openCount: 0,
  replyCount: 0,
  bounceCount: 0,
});

// API 端点定义
const url = {
  list: '/email/emailPushDetail/businessDailyTrend',
  summary: '/email/emailPushDetail/businessSummary',
};

// 表格数据获取函数
const fetchTableApi = async () => {
  const requestParams = {
    counselorId: userInfo.id,
    startDate: queryParam.value.dateRange?.[0]
      ? dayjs(queryParam.value.dateRange[0]).format('YYYY-MM-DD HH:mm:ss')
      : undefined,
    endDate: queryParam.value.dateRange?.[1]
      ? dayjs(queryParam.value.dateRange[1]).format('YYYY-MM-DD HH:mm:ss')
      : undefined,
  };

  const res = await defHttp.post({ url: url.list, data: requestParams });
  
  return res;
};

// 使用 useTable 组合式函数
const {
  loading,
  dataSource,
  queryParam,
  sTableHeight,
  pagination,
  fetchTable,
} = useTable(fetchTableApi,32 ,{
  afterFetch: emailSendingStatisticsAfterFetch,
});

async function emailSendingStatisticsAfterFetch(res, data) {
  console.log(dataSource);
  dataSource.value = data.map((item, index) => {
    return {
      ...item,
      id: item.date,
    }
  })
  fetchSummary()
}

// 初始化查询参数
queryParam.value = {
  dateRange: [dayjs().subtract(6, 'days'), dayjs()],
};
// 初始为近7天，与 dateRange 一致
dateRangeRadio.value = '近7天';

// 列配置
const columns = [
  {
    title: '#',
    dataIndex: 'index',
    width: 60,
    align: 'center',
    customRender: ({ index }) => index + 1,
  },
  {
    title: '日期',
    dataIndex: 'date',
    align: 'left',
  },
  {
    title: '发送总数',
    dataIndex: 'totalSentCount',
    align: 'left',
  },
  {
    title: '成功数',
    dataIndex: 'actualSentCount',
    align: 'left',
  },
  {
    title: '成功率',
    dataIndex: 'sendSuccessRate',
    align: 'left',
  },
  {
    title: '打开数',
    dataIndex: 'openCount',
    align: 'left',
  },
  {
    title: '打开率',
    dataIndex: 'openRate',
    align: 'left',
  },
  {
    title: '回复数',
    dataIndex: 'replyCount',
    align: 'left',
  },
  {
    title: '回复率',
    dataIndex: 'replyRate',
    align: 'left',
  },
  {
    title: '失败数',
    dataIndex: 'bounceCount',
    align: 'left',
  },
  {
    title: '失败率',
    dataIndex: 'bounceRate',
    align: 'left',
  },
];

// 获取汇总数据
const fetchSummary = async () => {
  try {
    const params = {
      counselorId: userInfo.id,
      startDate: queryParam.value.dateRange?.[0]
        ? dayjs(queryParam.value.dateRange[0]).format('YYYY-MM-DD 00:00:00')
        : undefined,
      endDate: queryParam.value.dateRange?.[1]
        ? dayjs(queryParam.value.dateRange[1]).format('YYYY-MM-DD 23:59:59')
        : undefined,
    };
    const res = await defHttp.post({ url: url.summary, data: params },{isTransformResponse:false});
    if (res.success) {
      emailAllData.value = res.result;
    } else {
      createMessage.error(res.message);
    }
  } catch (error) {
    console.error('fetchSummary error:', error);
  }
};

// 搜索
const handleSearchQuery = () => {
  fetchTable(1);

};

// 重置
const handleSearchReset = () => {
  dateRangeRadio.value = '近7天';
  applyPresetDateRange();
  fetchTable(1);
};

fetchTable();
onMounted(() => {
  document.querySelector('.surely-table-center-container').style.height = sTableHeight.value + 'px';
  console.log( document.querySelector('.surely-table-center-container'))
});
</script>
<style>
.emailSendingStatisticsRangeradio .ant-radio-group {
  display: flex !important;
  width: 100% !important;
}
.emailSendingStatisticsRangeradio .ant-radio-button-wrapper {
  padding: 0 10px !important;
}
.emailSendingStatisticsRangeradio .ant-radio-button-wrapper {
  flex: 1 !important;
  text-align: center;
}
</style>

<style scoped lang="less">
  :deep(.surely-table-horizontal-scroll + div + div)  {
    visibility: hidden !important;
  }
  :deep(.surely-table .surely-table-horizontal-scroll + div){
    visibility: inherit !important;
  }
  :deep(.surely-table-row.surely-table-row-hover){
    background-color: #fff !important;
  }
.table-page-search-wrapper {
  margin-bottom: 16px;
}
</style>
