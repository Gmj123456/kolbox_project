<template>
  <div class="summaryExpertCountries">
    <div class="title">
      <div class="title_lf">
        <div style="margin-right: 10px">顾问签约数量汇总</div>
        <div>
          <a-input-group compact>
            <a-select style="border-right: 0px;"  v-model:value="queryParams.dateType" @change="dateTypeChange">
              <a-select-option :value="0"> 月份 </a-select-option>
              <a-select-option :value="1"> 年份 </a-select-option>
            </a-select>
            <a-date-picker
              v-if="queryParams.dateType === 1"
              v-model:value="queryParams.year"
              placeholder="请选择年份"
              mode="year"
              format="YYYY"
              :open="yearShowOne"
              @openChange="openChangeOne"
              @panelChange="panelChangeOne"
            />
            <a-month-picker
              v-else
              v-model:value="queryParams.month"
              :disabledDate="disabledFutureMonths"
              format="YYYY-MM"
              placeholder="请选择月份"
              @change="monthChange"
            />
          </a-input-group>
        </div>
      </div>
      <div class="title_rg">
        <a-select
        :bordered="false"
          :dropdownMatchSelectWidth="false"
          size="small"
          v-model:value="queryParams.platformType"
          @change="platformTypeChange"
        >
          <a-select-option :value="2">
           <div style="display: flex;align-items: center;">
            <img
              style="width: 15px; height: 15px"
              src="@/assets/images/tk.png"
              alt=""
            />
            <span>TikTok</span>
           </div>
          </a-select-option>
          <a-select-option :value="1">
            <div style="display: flex;align-items: center;">
              <img
                style="width: 15px; height: 15px"
                src="@/assets/images/yt.png"
                alt=""
              />
              <span>YouTube</span>
           </div>
          </a-select-option>
          <a-select-option :value="0">
            <div style="display: flex;align-items: center;">
              <img
                style="width: 15px; height: 15px"
                src="@/assets/images/ins.png"
                alt=""
              />
              <span>Instagram</span>
           </div>
          </a-select-option>
        </a-select>
        <a-radio-group
          size="small"
          style="margin-right: 10px"
          v-model:value="queryParams.datatype"
          @change="datatypeChange"
        >
          <a-radio-button :value="0"> 图表 </a-radio-button>
          <a-radio-button :value="1"> 列表 </a-radio-button>
        </a-radio-group>
        <a @click="reload">
          <SyncOutlined style="font-size: 16px" />
        </a>
      </div>
    </div>
    <div class="summaryExpertCountries_chart">
      <summaryExpertCountriesBarChart
        v-if="queryParams.datatype === 0"
        :key="`bar-${queryParams.datatype}-${chartKey}`"
        ref="summaryExpertCountriesBarChartRef"
        :chartData="barChartData"
        :typeSummary="typeSummary"
      />
      <summaryExpertCountriesPieChart
        v-else
        :key="`pie-${queryParams.datatype}-${chartKey}`"
        ref="summaryExpertCountriesPieChartRef"
        :columns="columns"
        :loading="loading"
        :chartData="typeSummary"
        :totalNumber="totalNumber"
        :dataSourse="dataSourse"
      />
    </div>
  </div>
</template>
<script setup>
import { SyncOutlined } from '@ant-design/icons-vue';
import dayjs from 'dayjs';
import { computed, nextTick, onMounted, reactive, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import summaryExpertCountriesBarChart from './modules/summaryExpertCountriesBarChart.vue';
import summaryExpertCountriesPieChart from './modules/summaryExpertCountriesPieChart.vue';

const { createMessage } = useMessage();

const yearShowOne = ref(false);
const queryParams = reactive({
  datatype: 0,
  platformType: 2,
  dateType: 0,
  year: undefined,
  month: dayjs(),
});
const totalNumber = ref(0);
const barChartData = ref([]);
const typeSummary = ref([]);
const columns = ref([]);
const dataSourse = ref([]);
const loading = ref(false);
const chartKey = ref(0);
const summaryExpertCountriesBarChartRef = ref();
const summaryExpertCountriesPieChartRef = ref();

const chartWidth = computed(() => {
  const el = summaryExpertCountriesBarChartRef.value?.$el;
  return el ? el.clientWidth : 0;
});

function disabledFutureMonths(current) {
  return current > dayjs().endOf('month');
}

async function reload() {
  try {
    await initChart();
    await nextTick();
    if (queryParams.datatype === 0) {
      summaryExpertCountriesBarChartRef.value?.updateChart?.();
    } else {
      summaryExpertCountriesPieChartRef.value?.updateChart?.();
    }
    createMessage.success('刷新成功');
  } catch (error) {
    console.error('刷新数据失败:', error);
    createMessage.error('刷新失败');
  }
}

function datatypeChange(e) {
  if (e?.target?.value === 1) {
    void chartWidth.value;
  }
  initChart();
}

function parseStartType(val) {
  const result = columns.value.find((item) => item.column === val);
  return result ? result.name : '';
}

async function initChart() {
  loading.value = true;
  try {
    const params = {
      platformType: queryParams.platformType,
    };
    if (queryParams.dateType === 0) {
      const monthDate = dayjs(queryParams.month);
      params.contractStartTime = monthDate ? monthDate.startOf('month').format('YYYY-MM-DD') : undefined;
      params.contractEndTime = monthDate ? monthDate.endOf('month').format('YYYY-MM-DD') : undefined;
    } else {
      const yearDate = dayjs(queryParams.year);
      params.contractStartTime = yearDate ? yearDate.startOf('year').format('YYYY-MM-DD') : undefined;
      params.contractEndTime = yearDate ? yearDate.endOf('year').format('YYYY-MM-DD') : undefined;
    }
    const res = await defHttp.get({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/getCounselorStats',
      params,
    });
    if (res) {
      dataSourse.value = (res.data || []).sort((a, b) => b.totalSignedCount - a.totalSignedCount);
      columns.value = res.columns || [];
      transformCounselorData(res.dataStats);
    } else {
      createMessage.error('获取数据失败');
    }
  } catch (error) {
    console.error('获取数据时发生错误:', error);
  } finally {
    loading.value = false;
  }
}

function transformCounselorData(counselorData) {
  if (!counselorData || !counselorData.length) {
    barChartData.value = [];
    computedSummary();
    return;
  }
  const counselorMap = {};
  counselorData.sort((a, b) => b.totalSignedCount - a.totalSignedCount);
  counselorData.forEach((item) => {
    const counselorName = item.counselorName;
    if (!counselorMap[counselorName]) {
      counselorMap[counselorName] = {
        name: counselorName,
        data: [],
      };
    }
    item.celebrityStarModelList.forEach((starModel) => {
      counselorMap[counselorName].data.push({
        type: parseStartType(starModel.starName),
        num: starModel.starValue,
        color: starModel.starColor,
      });
    });
  });
  barChartData.value = Object.values(counselorMap);
  computedSummary();
}

function computedSummary() {
  if (!barChartData.value || !barChartData.value.length) return;
  const allTypes = [...new Set(barChartData.value.flatMap((item) => item.data.map((d) => d.type)))];
  const colorMap = {};
  barChartData.value.forEach((person) => {
    person.data.forEach((item) => {
      if (item.color && !colorMap[item.type]) {
        colorMap[item.type] = item.color;
      }
    });
  });
  const typeCounts = {};
  allTypes.forEach((type) => {
    typeCounts[type] = 0;
  });
  barChartData.value.forEach((person) => {
    person.data.forEach((item) => {
      const type = item.type;
      if (typeCounts[type] !== undefined) {
        typeCounts[type] += item.num;
      }
    });
  });
  typeSummary.value = allTypes.map((type) => ({
    type,
    name: type,
    color: colorMap[type] || '#999999',
    count: typeCounts[type],
  }));
  totalNumber.value = Object.values(typeCounts).reduce((sum, count) => sum + count, 0);
  if (queryParams.datatype === 0) {
    typeSummary.value.push({
      type: '合计',
      name: '合计',
      color: 'red',
      count: totalNumber.value,
    });
  }
}

function platformTypeChange() {
  initChart();
}

function dateTypeChange(e) {
  if (e === 1) {
    queryParams.month = undefined;
  } else {
    queryParams.year = undefined;
  }
}

function refreshData() {
  initChart();
}

function openChangeOne(status) {
  if (status) {
    yearShowOne.value = true;
  }
}

function monthChange(value) {
  queryParams.month = value;
  initChart();
}

function panelChangeOne(value) {
  queryParams.year = value;
  yearShowOne.value = false;
  initChart();
}

onMounted(() => {
  initChart();
});
</script>
<style scoped lang="less">
  .title_rg  :deep(.ant-select-selection-item){
    display: flex !important;
    align-items: center !important;
  }
.summaryExpertCountries {
  padding: 12px 20px;
  background-color: white;
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;

  .title {
    font-size: 14px;
    font-weight: 600;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 35px;

    .title_lf {
      display: flex;
      align-items: center;
    }
  }

  .title_rg {
    display: flex;
    align-items: center;
  }

  .summaryExpertCountries_chart {
    flex: 1;
    width: 100%;
  }
}
</style>
<style>
.summaryExpertCountries .title_rg .ant-select-selection {
  border: none !important;
}

.summaryExpertCountries .title_rg .ant-select-focused .ant-select-selection,
.ant-select-selection:focus,
.ant-select-selection:active {
  box-shadow: none !important;
}

.summaryExpertCountries .ant-radio-group {
  font-size: 12px !important;
}
</style>
