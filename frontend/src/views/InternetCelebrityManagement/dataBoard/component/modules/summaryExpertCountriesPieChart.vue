<template>
  <div class="summaryExpertCountriesPieChart_box">
    <div class="summaryExpertCountriesPieChart_box_left">
      <div class="pieChart" ref="pieChartContainer"></div>
      <div class="legend-list">
        <div class="legend-row-wrapper">
          <div
            v-for="(item, index) in processedLegendData"
            :key="index"
            class="legend-item"
            :style="{ width: 'calc(50% - 10px)' }"
          >
            <div class="legend-header">
              <span class="legend-color" :style="{ backgroundColor: item.color }"></span>
              <span class="legend-name">{{ item.name }}</span>
            </div>
            <div class="legend-content">
              <div class="legend-data-row">
                <span class="legend-label">人数：</span>
                <span class="legend-value">{{ item.count }}</span>
              </div>
              <div class="legend-data-row">
                <span class="legend-label">占比：</span>
                <span class="legend-value">{{ item.percentage }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="summaryExpertCountriesPieChart_box_right">
      <vxe-table
        ref="vxeTbale"
        :footer-method="footerMethod"
        show-footer
        :data="dataSourse"
        :height="vxeHeight"
        :loading="loading"
        size="mini"
        stripe
        show-overflow
      >
        <vxe-table-column field="id" title="#" width="50" align="center">
          <template #default="{ rowIndex }">
            <span v-if="rowIndex == 0">
              <img src="@/assets/images/one.png" alt="" />
            </span>
            <span v-else-if="rowIndex == 1">
              <img src="@/assets/images/two.png" alt="" />
            </span>
            <span v-else-if="rowIndex == 2">
              <img src="@/assets/images/three.png" alt="" />
            </span>
            <span v-else>
              {{ rowIndex + 1 }}
            </span>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="counselorName"
          width="120"
          title="网红顾问"
        ></vxe-table-column>
        <vxe-table-column field="totalSignedCount" title="合计"></vxe-table-column>
        <vxe-table-column
          v-for="item in columns"
          :key="item.column"
          :field="item.column"
          :title="item.name"
        ></vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script setup>
import * as echarts from "echarts";
import XEUtils from "xe-utils";
import { ref, shallowRef, onMounted, onBeforeUnmount, watch } from 'vue';

// 定义props
const props = defineProps({
  totalNumber: {
    type: Number,
    default: 0,
  },
  chartData: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  columns: {
    type: Array,
    default: () => [],
  },
  dataSourse: {
    type: Array,
    default: () => [],
  },
});

// 定义响应式数据
const pieChartContainer = ref(null);
const pieChart = shallowRef(null); // 使用 shallowRef 存储图表实例
const processedLegendData = ref([]);
const vxeHeight = ref(0);
const vxeTbale = ref(null);

// 计算总数
const getTotalCount = () => {
  if (props.chartData && props.chartData.length > 0) {
    const totalItem = props.chartData.find((item) => item.type === "total");
    if (totalItem) {
      return totalItem.count;
    }

    // 如果没有总计项，则计算所有项的总和
    return props.chartData
      .filter((item) => item.type !== "total")
      .reduce((sum, item) => sum + item.count, 0);
  }

  return props.totalNumber;
};

// 处理图表数据
const processChartData = () => {
  if (props.chartData && props.chartData.length > 0) {
    return props.chartData
      .filter((item) => item.type !== "total") // 过滤掉总计项
      .map((item) => ({
        value: item.count,
        name: item.name,
        itemStyle: {
          color: item.color,
        },
      }));
  }

  // 添加默认返回值，避免undefined错误
  return [];
};

// 处理图例数据
const processLegendData = () => {
  if (props.chartData && props.chartData.length > 0) {
    const total = getTotalCount();

    processedLegendData.value = props.chartData
      .filter((item) => item.type !== "total")
      .map((item) => ({
        name: item.name,
        count: item.count,
        color: item.color,
        percentage: total > 0 ? ((item.count / total) * 100).toFixed(0) : 0,
      }));

    // 添加"其他"项
    const otherItem = props.chartData.find((item) => item.name === "其他");
    if (otherItem) {
      // 将"其他"项移到最后
      const otherIndex = processedLegendData.value.findIndex(
        (item) => item.name === "其他"
      );
      if (otherIndex !== -1) {
        const other = processedLegendData.value.splice(otherIndex, 1)[0];
        processedLegendData.value.push(other);
      }
    }
  }
};

// 表格footer方法
const footerMethod = ({ columns, data }) => {
  const sums = [];
  columns.forEach((column, columnIndex) => {
    if (columnIndex === 0) {
      sums.push(data.length + 1);
    } else if (columnIndex === 1) {
      sums.push("合计");
    } else {
      let sumCell = null;
      if (column.property !== "counselorName") {
        sumCell = XEUtils.sum(data, column.property);
      }
      sums.push(sumCell);
    }
  });
  return [sums];
};

// 重置图表大小
function resizeChart() {
  pieChart.value?.resize();
  }

// 初始化饼图
const initPieChart = () => {
  // 在挂载后初始化图表
  if (pieChartContainer.value) {
    pieChart.value = echarts.init(pieChartContainer.value);

    // 处理数据
    const processedData = processChartData();
    // 处理图例数据
    processLegendData();

    // 饼图配置选项
    const option = {
      // 提示框配置
      tooltip: {
        trigger: "item", // 触发类型：数据项触发
        backgroundColor: "white", // 背景色
        borderColor: "#eee", // 边框颜色
        borderWidth: 1, // 边框宽度
        padding: [10, 15], // 内边距
        textStyle: {
          color: "#333", // 文本颜色
          fontSize: 14, // 文本大小
        },
        extraCssText:
          "width: 150px; border-radius: 4px; box-shadow: 0 0 10px rgba(0,0,0,0.1);", // 额外CSS样式
        formatter: function (params) {
          // 根据不同平台获取对应的颜色
          const color = params.color;
          return `<div style="display:flex; align-items:center; margin-bottom:10px;justify-content: center;">
                    <span style="display:inline-block; width:10px; height:10px; border-radius:50%; background-color:${color}; margin-right:8px;"></span>
                    <span style="font-weight:bold; font-size:16px;">${
                      params.name
                    }</span>
                  </div>
                  <div style="display:flex; justify-content:space-between; margin:5px 0;">
                    <span style="color:#666;">人数</span>
                    <span style="font-weight:bold; color:#333;">${params.value.toLocaleString()}</span>
                  </div>
                  <div style="display:flex; justify-content:space-between; margin:5px 0;">
                    <span style="color:#666;">占比</span>
                    <span style="font-weight:bold; color:#333;">${params.percent.toFixed(
                      2
                    )}%</span>
                  </div>`;
        },
      },
      // 不使用内置图例，我们自定义右侧图例
      legend: {
        show: false,
      },
      // 系列列表配置
      series: [
        {
          type: "pie", // 图表类型：饼图
          radius: ["40%", "70%"], // 饼图的半径，内外半径
          avoidLabelOverlap: false, // 是否启用防止标签重叠
          // 标签配置
          label: {
            show: true, // 显示标签
            position: "inside", // 标签位置：外部
            formatter: "{d}%", // 显示百分比
            fontSize: 12,
            color: "#333",
          },
          emphasis: {
            scale: false, // 取消高亮时的放大效果
            labelLine: {
              show: false,
            },
          },
          // 标签引导线配置
          labelLine: {
            show: false, // 不显示引导线
          },
          // 数据配置
          data: processedData,
        },
      ],
    };

    // 获取总数
    const total = getTotalCount();

    option.graphic = [
      {
        type: "text",
        left: "center",
        top: "45%",
        style: {
          text: "总签约数",
          textAlign: "center",
          fill: "#666",
          fontSize: 14,
        },
      },
      {
        type: "text",
        left: "center",
        top: "55%",
        style: {
          text: total.toString(),
          textAlign: "center",
          fill: "#333",
          fontSize: 20,
          fontWeight: "bold",
        },
      },
    ];
    
    // 设置配置项
    pieChart.value.setOption(option);
  }
};

// 监听chartData变化
watch(
  () => props.chartData,
  () => {
    // 当数据发生变化时重新绘制图表
    initPieChart();
  },
  { deep: true }
);

// 组件挂载
onMounted(() => {
  // 获取容器高度
  if (document.querySelector(".summaryExpertCountriesPieChart_box_right")) {
    vxeHeight.value = document.querySelector(
      ".summaryExpertCountriesPieChart_box_right"
    ).offsetHeight;
  }

  // 初始化图表
  initPieChart();
  
  // 添加窗口大小调整监听器
  window.addEventListener("resize", resizeChart);
});

// 组件卸载前清理
onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeChart);
  if (pieChart.value) {
    pieChart.value.dispose();
    pieChart.value = null;
  }
});
</script>

<style scoped lang="less">
.summaryExpertCountriesPieChart_box {
  display: flex;
  justify-content: space-between;
  height: 100%;

  .summaryExpertCountriesPieChart_box_left {
    display: flex;
    justify-content: space-between;
    flex: 1;

    .pieChart {
      flex: 1;
    }

    .legend-list {
      flex: 1;
      padding: 0 10px;
      display: flex;
      align-items: center;
    }

    .legend-row-wrapper {
      display: flex;
      flex-wrap: wrap;
      justify-content: flex-start;
    }

    .legend-item {
      display: flex;
      flex-direction: column;
      margin-bottom: 5px;

      &:last-child {
        margin-bottom: 0; // 取消最后一个legend-item的margin
      }
    }

    .legend-header {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .legend-color {
      display: inline-block;
      width: 10px;
      height: 10px;
      border-radius: 50%;
    }

    .legend-name {
      font-weight: bold;
      // font-size: 16px;
    }

    .legend-content {
    }

    .legend-data-row {
      display: flex;
      font-size: 12px;
    }

    .legend-label {
      color: #666;
    }

    .legend-value {
      font-weight: bold;
      color: #333;
    }
  }

  .summaryExpertCountriesPieChart_box_right {
    flex: 1;
  }
}
</style>