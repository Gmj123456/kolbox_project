<template>
  <div class="summaryExpertCountries_chart_box">
    <div class="chart-container" ref="chartContainer"></div>
    <div class="chart_info">
      <div class="chart_info_box" v-for="item in typeSummary" :key="item.type">
        <span
          :style="{ backgroundColor: item.color }"
          style="margin-right: 5px; display: inline-block; width: 10px; height: 10px"
        ></span>
        <span style="flex: 1">{{ item.name }}</span>
        <span style="font-weight: bold">{{ item.count }}</span>
      </div>
    </div>
  </div>
</template>
<script setup>
import * as echarts from 'echarts';
import { ref, shallowRef, watch, onMounted, onBeforeUnmount, nextTick } from 'vue';

const props = defineProps({
  chartData: {
    type: Array,
    default: () => [],
  },
  typeSummary: {
    type: Array,
    default: () => [],
  },
});

const chartContainer = ref();
const chart = shallowRef(); // 使用 shallowRef 存储图表实例

// 处理数据，将原始数据转换为图表所需格式
function processData() {
  if (!props.chartData || props.chartData.length === 0) {
    return {
      categories: [],
      series: [],
    };
  }

  // 获取所有顾问名称作为x轴类别
  const categories = props.chartData.map((item) => item.name);

  // 获取所有类型
  const allTypes = [
    ...new Set(props.chartData.flatMap((item) => item.data.map((d) => d.type))),
  ];

  // 收集颜色信息
  const colorMap = {};
  props.chartData.forEach((person) => {
    person.data.forEach((item) => {
      if (item.color) {
        colorMap[item.type] = item.color;
      }
    });
  });

  // 为每种类型创建一个系列
  const series = [];
  const seriesData = {};

  // 初始化每个系列的数据数组
  allTypes.forEach((type) => {
    seriesData[type] = new Array(categories.length).fill(0);
  });

  // 填充数据
  props.chartData.forEach((person, personIndex) => {
    person.data.forEach((item) => {
      const type = item.type;
      if (seriesData[type] !== undefined) {
        seriesData[type][personIndex] = item.num || 0;
      }
    });
  });

  // 计算每个顾问的总数（仅在标签格式化器中使用）
  const totalData = categories.map((_, index) => {
    return Object.values(seriesData).reduce(
      (sum, data) => sum + (data[index] || 0),
      0
    );
  });

  // 构建系列数据
  Object.entries(seriesData).forEach(([type, data], index) => {
    series.push({
      name: type, // 直接使用type作为名称
      type: "bar",
      stack: "total",
      barWidth: "30px",
      itemStyle: {
        color: colorMap[type] || "#999999",
      },
      label: {
        show: index === Object.entries(seriesData).length - 1, // 只在最后一个系列显示标签
        position: "top",
        formatter: (params) => {
          // 计算当前柱子的总和
          const total = totalData[params.dataIndex];
          return total > 0 ? total : "";
        },
        fontSize: 12,
        color: "black",
      },
      data: data,
      // ECharts 5.6.0 新增配置项
    
    });
  });

  return {
    categories,
    series,
    colorList: allTypes.map((type) => colorMap[type] || "#999999"),
  };
}

function initChart() {
  if (chartContainer.value) {
    // 如果图表已存在，先销毁
    if (chart.value) {
      chart.value.dispose();
    }

    // ECharts 5.6.0 推荐使用 echarts.getInstanceByDom 检查实例
    // 先检查是否已有实例
    const existingInstance = echarts.getInstanceByDom(chartContainer.value);
    if (existingInstance) {
      chart.value = existingInstance;
    } else {
      chart.value = echarts.init(chartContainer.value, null, {
        // ECharts 5.6.0 支持更多渲染选项
        renderer: 'canvas', // 或 'svg'
        useDirtyRect: false,
      });
    }

    // 使用 nextTick 确保图表容器完全准备好后再更新
    nextTick(() => {
      if (props.chartData && props.chartData.length > 0) {
        updateChart();
      }
    });
  }
}

function updateChart() {
  if (!chart.value) return;

  const processedData = processData();
  const { categories, series, colorList } = processedData;

  // 计算图表容器的动态宽度
  // this.calculateChartWidth();

  // 配置网格
  const grid = {
    left: 40,
    right: 10,
    top: 40,
    bottom: 40,
  };

  // 图表配置
  const option = {
    color: colorList,
    tooltip: {
      trigger: "axis",
      show: true,
      axisPointer: {
        type: "shadow" // 使用阴影指示器
      },
      appendToBody: true, // 将 tooltip 渲染到 body，避免被容器 overflow 隐藏
      confine: false, // 不限制在图表区域内，确保 tooltip 可以显示
      enterable: false, // 鼠标是否可进入提示框浮层中
      formatter: function(params) {
        // 确保 params 存在且是数组
        if (!params || !Array.isArray(params) || params.length === 0) {
          return '';
        }

        // 如果是空数据，显示简单提示
        if (categories[0] === "暂无数据") {
          return '<div style="font-weight:bold;margin-bottom:5px;">暂无数据</div>';
        }

        let result = `<div style="font-weight:bold;margin-bottom:5px;">${params[0].name || ''}</div>`;
        let sum = 0;

        params.forEach((item) => {
          if (item.seriesName && item.seriesName !== "总数") {
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

    grid,
    yAxis: {
      type: "value",
      splitLine: {
        lineStyle: {
          type: "solid",
        },
      },
    },
    xAxis: {
      type: "category", // 设置x轴类型为类目轴
      data: categories, // x轴数据源
      axisLabel: {
        interval: 0, // 显示所有标签，不自动隐藏
        textStyle: {
          fontSize: 12, // 设置标签文字大小
        },
        formatter: function (value) {
          return value; // 直接返回原始值作为标签显示
        },
        overflow: "break", // 标签文字过长时自动换行
        width: null, // 取消固定宽度，让标签自适应
        // rotate: 45, // 标签旋转45度，防止重叠
        // margin: 20, // 增加标签与轴线的间距
      },
      axisTick: {
        show: false, // 隐藏刻度线
        alignWithLabel: true, // 刻度线与标签对齐
      },
      axisLine: {
        show: false, // 隐藏轴线
      },
      boundaryGap: true, // 让第一个和最后一个标签与图表边界保持一定距离
    },
    // 修复 dataZoom 配置，应该是一个数组，并添加 inside 类型支持鼠标滚轮缩放
    dataZoom: [
      {
        type: "inside", // 内置型缩放组件，支持鼠标滚轮和触摸板手势
        start: 0,
        end: Math.min(100, (16 / Math.max(1, categories.length)) * 100),
        zoomOnMouseWheel: true,
        moveOnMouseMove: true,
        moveOnMouseWheel: true,
      },
      {
        type: "slider", // 滑动条类型
        show: categories.length > 10, // 是否显示滑动条
        start: 0,
        end: Math.min(100, (16 / Math.max(1, categories.length)) * 100),
        height: 6, // 滑动条组件高度
        bottom: 10, // 距离图表区域下边的距离
        showDetail: false, // 拖拽时是否显示详情
        showDataShadow: false, // 是否在组件中显示数据阴影
        fillerColor: "#dbdee5", // 平移条的填充颜色
        borderColor: "transparent", // 边框颜色
        brushSelect: false, // 不可缩放
        handleStyle: {
          // 手柄样式
          opacity: 0,
        },
      }
    ],
    series,
  };

  // ECharts 5.6.0 推荐使用 notMerge: false, replaceMerge: [] 来优化更新性能
  // 移除 tooltip 避免配置被覆盖
  chart.value.setOption(option, {
    notMerge: false,
    replaceMerge: ['series', 'dataZoom'],
    transition: {
      duration: 300,
      easing: 'cubicInOut'
    }
  });

  // 如果是空数据，添加文本提示
  if (categories[0] === "暂无数据") {
    option.graphic = [
      {
        type: "text",
        left: "center",
        top: "middle",
        style: {
          text: "暂无数据",
          textAlign: "center",
          fill: "#999",
          fontSize: 14,
        },
      },
    ];
    chart.value.setOption(option, {
      notMerge: false,
      replaceMerge: ['graphic']
    });
  }
}

function resizeChart() {
  chart.value?.resize();
  }

watch(() => props.chartData, (newVal) => {
  console.log("chartData changed:", newVal);
  if (!newVal || newVal.length === 0) {
    return;
  }
  nextTick(() => {
    updateChart();
  });
}, { deep: true, immediate: true });

watch(() => props.typeSummary, (newVal) => {
  console.log("typeSummary changed:", newVal);
  if (newVal && newVal.length > 0) {
    nextTick(() => {
      updateChart();
    });
  }
}, { deep: true });

onMounted(() => {
  initChart();
  window.addEventListener("resize", resizeChart);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeChart);
  if (chart.value) {
    chart.value.dispose();
    chart.value = null;
  }
});
</script>
<style lang="less" scoped>
.summaryExpertCountries_chart_box {
  height: 100%;
  display: flex;

  .chart-container {
    overflow-x: hidden;
    overflow-y: visible; // 确保 tooltip 可以显示
    height: 100%;
    // min-width: 100%;
    flex: 1;
    position: relative;
  }

  .chart_info {
    margin-left: 10px;
    width: 150px;
    height: 100%;
    padding: 10px;
    background-color: #f1f5ff;
    border-radius: 10px;
    display: flex;
    justify-content: space-around;
    flex-direction: column;

    .chart_info_box {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
