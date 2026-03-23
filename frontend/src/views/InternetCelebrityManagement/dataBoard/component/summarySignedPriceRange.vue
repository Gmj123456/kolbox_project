<template>
  <div class="summarySignedPriceRange">
    <div class="title">
      <div class="title_lf">签约价格区间汇总</div>
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
              <img style="width: 15px; height: 15px" src="@/assets/images/tk.png" alt="" />
              <span>TikTok</span>
            </div>
          </a-select-option>
          <a-select-option :value="1">
            <div style="display: flex;align-items: center;">
              <img style="width: 15px; height: 15px" src="@/assets/images/yt.png" alt="" />
              <span>YouTube</span>
            </div>
          </a-select-option>
          <a-select-option :value="0">
            <div style="display: flex;align-items: center;">
              <img style="width: 15px; height: 15px" src="@/assets/images/ins.png" alt="" />
              <span>Instagram</span>
            </div>
          </a-select-option>
        </a-select>
        <a style="margin-left: 10px" @click="refreshData">
          <SyncOutlined style="font-size: 16px" />
        </a>
      </div>
    </div>
    <div class="data-summary">
      <a-row>
        <a-col :span="6">
          <div class="summary-item">
            <div class="summary-item_center">
              <span style="background-color: red" class="summary-item—label"></span>
              <span style="margin-left: 5px">合计: {{ totalCount }}</span>
            </div>
          </div>
        </a-col>
        <a-col v-for="(item, index) in summaryData" :key="index" :span="6">
          <div class="summary-item">
            <div class="summary-item_center">
              <span :style="{ backgroundColor: item.color }" class="summary-item—label"></span>
              <span style="margin-left: 5px">{{ item.name }}: {{ item.count }}</span>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
    <div class="summarySignedPriceRange_bar_chart">
      <div class="chart-wrapper" ref="barChartContainer"></div>
    </div>
  </div>
</template>
<script setup>
import { SyncOutlined } from '@ant-design/icons-vue';
import * as echarts from 'echarts';
import { onBeforeUnmount, onMounted, ref, shallowRef } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
const { createMessage: $message } = useMessage();
defineProps({
  celebritySegmentList: {
    type: Array,
    default: () => [],
  },
});

const barChartContainer = ref();
const barChart = shallowRef(); // 使用 shallowRef 存储图表实例
const queryParams = ref({
  platformType: 2,
});
const chartData = ref({});
const summaryData = ref([]);
const totalCount = ref(0);

function calculateSummaryData() {
  if (!chartData.value.series || !chartData.value.series.length) return;
  summaryData.value = [];
  totalCount.value = 0;
  chartData.value.series.forEach((series) => {
    const seriesTotal = series.starValueList.reduce((sum, val) => sum + val, 0);
    summaryData.value.push({
      name: series.starName,
      count: seriesTotal,
      color: series.color,
    });
    totalCount.value += seriesTotal;
  });
}

async function initChartData() {
  try {
    const res = await defHttp.get({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/getCostStats',
      params: {
        platformType: queryParams.value.platformType,
      },
    },{isTransformResponse:false});
    if (res.success) {
      // $message.success('刷新成功');
      chartData.value = Object.assign(transformBackendData(res.result));
      updateChart();
      calculateSummaryData();
      return true;
    }
    return false;
  } catch (error) {
    console.error('获取图表数据失败:', error);
    return false;
  }
}

function transformBackendData(backendData) {
  if (!backendData || !backendData.length) return {};
  const ranges = [...new Set(backendData.map((item) => item.costRange))];
  const allStars = [];
  backendData.forEach((item) => {
    item.celebrityStarModelList.forEach((star) => {
      if (!allStars.find((s) => s.starName === star.starName)) {
        allStars.push({
          starName: star.starName,
          color: star.starColor,
        });
      }
    });
  });
  const series = allStars.map((star) => {
    const starValueList = [];
    ranges.forEach((range) => {
      const platformData = backendData.find((item) => item.costRange === range);
      if (platformData) {
        const starData = platformData.celebrityStarModelList.find((s) => s.starName === star.starName);
        starValueList.push(starData ? starData.starValue : 0);
      } else {
        starValueList.push(0);
      }
    });
    return {
      starName: star.starName,
      color: star.color,
      starValueList,
    };
  });
  return {
    ranges,
    series,
  };
}

function initChart() {
  if (!barChartContainer.value) return;
  barChart.value = echarts.init(barChartContainer.value);
}

function updateChart() {
  if (!barChart.value) return;
  const seriesData =
  chartData.value.series && chartData.value.series.length > 0
    ? chartData.value.series.map((item, index, array) => ({
        name: item.starName,
        type: 'bar',
        stack: 'total',
        label: {
          show: index === array.length - 1,
          position: 'right',
          formatter(params) {
            const total = array.reduce((sum, series) => sum + series.starValueList[params.dataIndex], 0);
            return total;
          },
        },
        emphasis: {
          focus: 'series',
        },
        itemStyle: {
          color: item.color,
        },
        data: item.starValueList,
      }))
    : [];
  const chartOption = {
    tooltip: {
      trigger: 'axis',
      show: true,
      axisPointer: {
        type: 'shadow',
      },
      appendToBody: true, // 将 tooltip 渲染到 body，避免被容器 overflow 隐藏
      confine: false, // 不限制在图表区域内，确保 tooltip 可以显示
      enterable: false, // 鼠标是否可进入提示框浮层中
      formatter: function(params) {
        if (!params || !Array.isArray(params) || params.length === 0) {
          return '';
        }
        
        // 如果是空数据
        if (chartData.value.ranges && chartData.value.ranges[0] === '暂无数据') {
          return '<div style="font-weight:bold;margin-bottom:5px;">暂无数据</div>';
        }

        const rangeName = params[0].name || '';
        let result = `<div style="font-weight:bold;margin-bottom:5px;">${rangeName}</div>`;
        let sum = 0;

        params.forEach((item) => {
          if (item.seriesName && item.seriesName !== '暂无数据') {
            const value = item.value || 0;
            result += `<div style="width:150px;display:flex;justify-content:space-between;align-items:center;margin-bottom:3px;">
                <span style="margin-right:5px;display:inline-block;width:10px;height:10px;background-color:${item.color || '#999'};"></span>
                <span style="flex:1;">${item.seriesName}</span>
                <span style="font-weight:bold;margin-left:10px;">${value}</span>
              </div>`;
            sum += value;
          }
        });
        
        if (sum > 0) {
          result += `<div style="display:flex;justify-content:space-between;margin-top:5px;border-top:1px solid #eee;padding-top:5px;">
            <span>总计:</span>
            <span style="font-weight:bold;">${sum}</span>
          </div>`;
        }

        return result;
      },
    },
    grid: {
      left: 80,
      right: 80,
      top: 0,
      bottom: 0,
    },
    xAxis: {
      type: 'value',
      splitLine: {
        show: false,
      },
    },
    yAxis: {
      type: 'category',
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      splitLine: {
        show: false,
      },
      data: chartData.value.ranges || ['暂无数据'],
    },
    series: seriesData.length
      ? seriesData
      : [
          {
            type: 'bar',
            data: [0],
            itemStyle: {
              color: '#e1e1e1',
            },
            name: '暂无数据',
          },
        ],
  };
  barChart.value.setOption(chartOption);
}

function platformTypeChange() {
  initChartData();
}

async function refreshData() {
  try {
    await initChartData();
  } catch (error) {
    console.error('刷新数据失败:', error);
  }
}

function resizeChart() {
  barChart.value?.resize();
}

onMounted(() => {
  initChart();
  initChartData();
  window.addEventListener('resize', resizeChart);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart);
  if (barChart.value) {
    barChart.value.dispose();
    barChart.value = null;
  }
});
</script>
<style scoped lang="less">
   :deep(.ant-select-selection-item){
    display: flex !important;
    align-items: center !important;
  }
.summarySignedPriceRange {
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
  }

  .title_rg {
    display: flex;
    align-items: center;
  }

  .data-summary {
    background-color: #f1f5ff;
    padding: 10px 15px;
    border-radius: 4px;
    overflow: hidden;
    height: 65px;
    display: flex;
    align-items: center;
  }

  .summary-item {
    display: flex;
    align-items: center;

    // justify-content: center;
    .summary-item_center {
      flex: 3;
    }

    div {
      flex: 1;
    }

    .summary-item—label {
      width: 8px;
      height: 8px;
      display: inline-block;
    }
  }
}

.summarySignedPriceRange_bar_chart {
  flex: 1;
  display: flex;
  overflow: visible; // 确保 tooltip 可以显示
  position: relative;
  
  .chart-wrapper {
    flex: 1;
    width: 100%;
    height: 100%;
    position: relative;
    overflow: visible;
  }
}
</style>
<style>
.summarySignedPriceRange .title_rg .ant-select-selection {
  border: none !important;
}

.summarySignedPriceRange .title_rg .ant-select-focused .ant-select-selection,
.ant-select-selection:focus,
.ant-select-selection:active {
  box-shadow: none !important;
}
</style>
