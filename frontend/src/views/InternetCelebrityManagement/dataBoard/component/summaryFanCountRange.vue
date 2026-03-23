<template>
  <div class="summaryFanCountRange">
    <div class="title">
      <div class="title_lf">粉丝数区间汇总</div>
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
    <div class="summaryFanCountRange_bar_chart">
      <div style="flex: 1" ref="barChartContainer"></div>
    </div>
  </div>
</template>
<script setup>
import { SyncOutlined } from '@ant-design/icons-vue';
import * as echarts from 'echarts';
import { onBeforeUnmount, onMounted, ref, shallowRef } from 'vue';
import { defHttp } from '/@/utils/http/axios';

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
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/getFollowersStats',
      params: {
        platformType: queryParams.value.platformType,
      },
    });
    if (res) {
      chartData.value = Object.assign(transformBackendData(res));
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
  const priceRanges = [...new Set(backendData.map((item) => item.followerRange))];
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
    priceRanges.forEach((range) => {
      const platformData = backendData.find((item) => item.followerRange === range);
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
    priceRanges,
    series,
  };
}

// 初始化图表DOM
function initChart() {
  if (!barChartContainer.value) return;
  barChart.value = echarts.init(barChartContainer.value);
  
  // 初始化空图表配置
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
      backgroundColor: 'white',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [10, 15],
      textStyle: {
        color: '#333',
        fontSize: 14,
      },
      extraCssText: 'border-radius: 4px; box-shadow: 0 0 10px rgba(0,0,0,0.1);',
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    yAxis: {
      type: 'value',
      splitLine: {
        show: true,
        lineStyle: {
          type: 'solid',
          color: '#E9E9E9',
        },
      },
    },
    xAxis: {
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
      data: ['暂无数据'],
    },
    series: [
      {
        type: 'bar',
        data: [0],
        itemStyle: {
          color: '#e1e1e1',
        },
      },
    ],
  };
  barChart.value.setOption(option);
}

// 更新图表数据
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
            position: 'top',
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
      
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
      backgroundColor: 'white',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [10, 15],
      textStyle: {
        color: '#333',
        fontSize: 14,
      },
      extraCssText: 'border-radius: 4px; box-shadow: 0 0 10px rgba(0,0,0,0.1);',
      formatter: (params) => {
        if (!params?.length) return '';
        
        let result = `<div style="font-weight:bold;margin-bottom:6px;">${params[0].name}</div>`;
        let sum = 0;

        params.forEach((item) => {
          if (item.seriesName) {
            const value = typeof item.value === 'number' ? item.value : 0;
            sum += value;
            result += `
              <div style="display:flex;align-items:center;margin:3px 0;width:160px;">
                <span style="display:inline-block;width:10px;height:10px;background-color:${item.color || '#999'};margin-right:6px;"></span>
                <span style="flex:1;">${item.seriesName}</span>
                <span style="font-weight:bold;margin-left:8px;">${value}</span>
              </div>
            `;
          }
        });

        if (sum > 0) {
          result += `
            <div style="margin-top:6px;padding-top:6px;border-top:1px solid #eee;display:flex;justify-content:space-between;">
              <span>总计:</span>
              <span style="font-weight:bold;">${sum}</span>
            </div>
          `;
        }
        return `<div style="padding:8px;">${result}</div>`;
      },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    yAxis: {
      type: 'value',
      splitLine: {
        show: true,
        lineStyle: {
          type: 'solid',
          color: '#E9E9E9',
        },
      },
    },
    xAxis: {
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
      data: chartData.value.priceRanges || ['暂无数据'],
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
          },
        ],
  };
  
  barChart.value.setOption(option);
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
.summaryFanCountRange {
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
    justify-content: center;

    div {
      flex: 1;
    }

    .summary-item_center {
      flex: 3;
    }

    .summary-item—label {
      width: 8px;
      height: 8px;
      display: inline-block;
    }

    .title_rg {
      display: flex;
      align-items: center;
    }
  }
}

.summaryFanCountRange_bar_chart {
  flex: 1;
  min-height: 260px; /* 必须给一个固定高度 */
}

.summaryFanCountRange_bar_chart > div {
  width: 100%;
  height: 100%;
}
</style>
<style scoped>
.summaryFanCountRange .title_rg .ant-select-selection {
  border: none !important;
}

.summaryFanCountRange .title_rg .ant-select-focused .ant-select-selection,
.ant-select-selection:focus,
.ant-select-selection:active {
  box-shadow: none !important;
}
</style>