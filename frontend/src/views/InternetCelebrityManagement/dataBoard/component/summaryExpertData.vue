<template>
  <div class="summaryExpertData">
    <div class="title">
      <div>网红数据概览</div>
      <div>
        <a @click="reload">
          <SyncOutlined style="font-size: 16px" />
        </a>
      </div>
    </div>
    <div class="summaryExpertData_chart">
      <div ref="pieChartContainer"></div>
      <div ref="dashboardChartContainer"></div>
    </div>
  </div>
</template>
<script setup>
import { onBeforeUnmount, onMounted, nextTick, ref, shallowRef } from 'vue';
import { SyncOutlined } from '@ant-design/icons-vue';
import * as echarts from 'echarts';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const pieChartContainer = ref();
const dashboardChartContainer = ref();
const pieChart = shallowRef();
const dashboardChart = shallowRef();
const signedCount = ref(0);
const totalCount = ref(0);
const pieData = ref([]);
const pieLoading = ref(false);
const dashboardLoading = ref(false);
const pietotalNumber = ref(0);
const { createMessage } = useMessage();

// 刷新
async function reload() {
  try {
    await Promise.all([initPieChartData(), initDashboardChartData()]);
    createMessage.success('刷新成功');
  } catch (error) {
    console.error('刷新失败:', error);
    createMessage.error('刷新失败');
  }
}

// 仪表盘数据接口
async function initDashboardChartData() {
  dashboardLoading.value = true;
  try {
    const res = await defHttp.get({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/signed',
     
    });
    console.log(res);
    if (res) {
      signedCount.value = res.signedCount;
      totalCount.value = res.totalCount;
      updateDashboardChart();
      return true;
    }
    return false;
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
    return false;
  } finally {
    dashboardLoading.value = false;
  }
}

// 饼图数据接口
async function initPieChartData() {
  pieLoading.value = true;
  try {
    const res = await defHttp.get({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/distribution',
    
    });
    if (res) {
      pieData.value = res || [];
      pietotalNumber.value = pieData.value.reduce((sum, item) => sum + item.count, 0);
      updatePieChart();
      return true;
    }
    return false;
  } catch (error) {
    console.error('获取饼图数据失败:', error);
    return false;
  } finally {
    pieLoading.value = false;
  }
}

// 初始化饼图DOM
function initPieChart() {
  if (!pieChartContainer.value) return;
  pieChart.value = echarts.init(pieChartContainer.value);
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'white',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [10, 15],
      position(point, params, dom, rect, size) {
        return [point[0] + 10, point[1] - size.contentSize[1] / 2];
      },
      textStyle: {
        color: '#333',
        fontSize: 14,
      },
      extraCssText: 'width: 150px; border-radius: 4px; box-shadow: 0 0 10px rgba(0,0,0,0.1);',
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          position: 'inside',
          formatter: '{d}%',
          fontSize: 12,
          color: '#333',
        },
        emphasis: {
          scale: false,
          labelLine: {
            show: false,
          },
        },
        labelLine: {
          show: false,
        },
        data: [],
      },
    ],
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '45%',
        style: {
          text: '网红总人数',
          textAlign: 'center',
          fill: '#666',
          fontSize: 14,
        },
      },
      {
        type: 'text',
        left: 'center',
        top: '55%',
        style: {
          text: '0',
          textAlign: 'center',
          fill: '#333',
          fontSize: 20,
          fontWeight: 'bold',
        },
      },
    ],
  };
  pieChart.value.setOption(option);
}

// 更新饼图数据
function updatePieChart() {
  if (!pieChart.value) return;
  const colorMap = {
    TikTok: '#5AD8A6',
    Instagram: '#5B8FF9',
    YouTube: '#FFC300',
  };
  const chartData = pieData.value.map((item) => ({
    value: item.count,
    name: item.platform,
    itemStyle: {
      color: colorMap[item.platform] || '#999',
    },
  }));

  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'white',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [10, 15],
      textStyle: {
        color: '#333',
        fontSize: 14,
      },
      extraCssText: 'width: 150px; border-radius: 4px; box-shadow: 0 0 10px rgba(0,0,0,0.1);',
      formatter(params) {

        const color = params.color;
        return `<div style="display:flex; align-items:center; margin-bottom:10px;justify-content: center;">
                  <span style="display:inline-block; width:10px; height:10px; border-radius:50%; background-color:${color}; margin-right:8px;"></span>
                  <span style="font-weight:bold; font-size:16px;">${params.name}</span>
                </div>
                <div style="display:flex; justify-content:space-between; margin:5px 0;">
                  <span style="color:#666;">人数</span>
                  <span style="font-weight:bold; color:#333;">${params.value.toLocaleString()}</span>
                </div>
                <div style="display:flex; justify-content:space-between; margin:5px 0;">
                  <span style="color:#666;">占比</span>
                  <span style="font-weight:bold; color:#333;">${params.percent.toFixed(2)}%</span>
                </div>`;
      },
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          position: 'inside',
          formatter: '{d}%',
          fontSize: 12,
          color: '#333',
        },
        emphasis: {
          scale: false,
          labelLine: {
            show: false,
          },
        },
        labelLine: {
          show: false,
        },
        data: chartData,
      },
    ],
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '45%',
        style: {
          text: '网红总人数',
          textAlign: 'center',
          fill: '#666',
          fontSize: 14,
        },
      },
      {
        type: 'text',
        left: 'center',
        top: '55%',
        style: {
          text: pietotalNumber.value.toString(),
          textAlign: 'center',
          fill: '#333',
          fontSize: 20,
          fontWeight: 'bold',
        },
      },
    ],
  };
  pieChart.value.setOption(option);
}

// 初始化仪表盘DOM
function initDashboardChart() {
  if (!dashboardChartContainer.value) return;
  dashboardChart.value = echarts.init(dashboardChartContainer.value);
  const option = {
    animationDuration: 2000,
    animationEasing: 'cubicOut',
    grid: {
      top: 'center',
      left: 'center',
    },
    series: [
      {
        type: 'gauge',
        startAngle: 180,
        endAngle: 0,
        min: 0,
        max: 100,
        radius: '80%',
        center: ['50%', '60%'],
        splitNumber: 10,
        pointer: {
          show: false,
        },
        progress: {
          show: false,
        },
        axisLine: {
          lineStyle: {
            width: 15,
            color: [
              [
                0,
                {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 1,
                  y2: 0,
                  colorStops: [
                    {
                      offset: 0,
                      color: '#91c7ff',
                    },
                    {
                      offset: 1,
                      color: '#5b8ff9',
                    },
                  ],
                },
              ],
              [1, '#F0F2F5'],
            ],
          },
        },
        axisTick: {
          show: false,
        },
        splitLine: {
          show: false,
        },
        axisLabel: {
          show: false,
        },
        title: {
          show: false,
        },
        detail: {
          show: true,
          offsetCenter: [0, 0],
          valueAnimation: true,
          formatter: () => '{value|0.00%}\n{name|网红签约占比}\n{count|签约数量: 0}',
          rich: {
            value: {
              fontSize: 28,
              fontWeight: 'bold',
              color: '#333',
              padding: [0, 0, 5, 0],
              lineHeight: 36,
            },
            name: {
              fontSize: 14,
              color: '#333',
              padding: [0, 0, 5, 0],
              lineHeight: 20,
            },
            count: {
              fontSize: 14,
              color: '#333',
              lineHeight: 20,
            },
          },
        },
        data: [
          {
            value: 0,
            name: '签约占比',
          },
        ],
        animation: true,
        animationDuration: 1000,
        animationEasing: 'cubicInOut',
      },
    ],
  };
  dashboardChart.value.setOption(option);
}

// 更新仪表盘数据
function updateDashboardChart() {
  if (!dashboardChart.value) return;
  const ratio = totalCount.value ? ((signedCount.value / totalCount.value) * 100).toFixed(2) : 0;
  const option = {
    animationDuration: 2000,
    animationEasing: 'cubicOut',
    grid: {
      top: 'center',
      left: 'center',
    },
    series: [
      {
        type: 'gauge',
        startAngle: 180,
        endAngle: 0,
        min: 0,
        max: 100,
        radius: '80%',
        center: ['50%', '60%'],
        splitNumber: 10,
        pointer: {
          show: false,
        },
        progress: {
          show: false,
        },
        axisLine: {
          lineStyle: {
            width: 15,
            color: [
              [
                ratio / 100,
                {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 1,
                  y2: 0,
                  colorStops: [
                    {
                      offset: 0,
                      color: '#91c7ff',
                    },
                    {
                      offset: 1,
                      color: '#5b8ff9',
                    },
                  ],
                },
              ],
              [1, '#F0F2F5'],
            ],
          },
        },
        axisTick: {
          show: false,
        },
        splitLine: {
          show: false,
        },
        axisLabel: {
          show: false,
        },
        title: {
          show: false,
        },
        detail: {
          show: true,
          offsetCenter: [0, 0],
          valueAnimation: true,
          formatter: (value) => `{value|${value.toFixed(2)}%}\n{name|网红签约占比}\n{count|签约数量: ${signedCount.value}}`,
          rich: {
            value: {
              fontSize: 28,
              fontWeight: 'bold',
              color: '#333',
              padding: [0, 0, 5, 0],
              lineHeight: 36,
            },
            name: {
              fontSize: 14,
              color: '#333',
              padding: [0, 0, 5, 0],
              lineHeight: 20,
            },
            count: {
              fontSize: 14,
              color: '#333',
              lineHeight: 20,
            },
          },
        },
        data: [
          {
            value: Number(ratio),
            name: '签约占比',
          },
        ],
        animation: true,
        animationDuration: 1000,
        animationEasing: 'cubicInOut',
      },
    ],
  };
  dashboardChart.value.setOption(option);
}

function resizeChart() {
  pieChart.value?.resize();
  dashboardChart.value?.resize();
}

onMounted(() => {

  initPieChart();
  initDashboardChart();
  window.addEventListener('resize', resizeChart);
});
initPieChartData();
  initDashboardChartData();
onBeforeUnmount(() => {
  if (pieChart.value) {
    pieChart.value.dispose();
    pieChart.value = null;
  }
  if (dashboardChart.value) {
    dashboardChart.value.dispose();
    dashboardChart.value = null;
  }
  window.removeEventListener('resize', resizeChart);
});
</script>
<style lang="less" scoped>
.summaryExpertData {
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

  .summaryExpertData_chart {
    display: flex;
    justify-content: space-between;
    flex: 1;

    div {
      flex: 1;
      height: 100%;
    }
  }
}
</style>