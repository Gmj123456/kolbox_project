<template>
  <div class="email-statistics-container">
    <a-row style="margin-bottom: 10px" :gutter="12">
      <a-col :span="6">
        <div class="emai-statistics-data-card">
          <div class="email-statistics-data-card-title">
            <div class="email-statistics-data-card-title-left">
              <span>数据总览</span>
              <div>
                <a-input-group compact>
                  <a-select
                  style="border-right: 0px;"
                    @change="emailAllFormDateTypeChange"
                    v-model:value="emailAllForm.dateType"
                  >
                    <a-select-option :value="0">月份</a-select-option>
                    <a-select-option :value="1">年份</a-select-option>
                  </a-select>

                  <a-month-picker
                  
                    v-if="emailAllForm.dateType == 0"
                    @change="emailAllFormDateChange"
                    v-model:value="emailAllForm.dateValue"
                  />
                  <a-date-picker
                    v-if="emailAllForm.dateType == 1"
                    v-model:value="emailAllForm.dateValue"
                    placeholder="请选择年份"
                    mode="year"
                    format="YYYY"
                    :open="emailAllFormYearShowOne"
                    @open-change="emailAllFormOpenChangeOne"
                    @panel-change="panelChangeOneAll"
                  />
                </a-input-group>
              </div>
            </div>
          </div>
          <div class="emmail-all-data">
            <div class="emmail-all-data-item">
              <div style="display: flex; align-items: center; gap: 10px; flex: 2">
                <div>
                  <a>
                    <span
                      style="font-size: 22px"
                      class="icon iconfont icon-24gl-envelopeSent5"
                    ></span>
                  </a>
                </div>
                <span>成功：</span>
                <a>{{ emailAllData.actualSentCount }}</a>
              </div>
              <div style="display: flex; align-items: center; gap: 10px; flex: 3">
                <span>成功率：</span>
                <a-progress
                  style="width: 64%"
                  :percent="emailAllData.sendSuccessRate"
                  status="active"
                />
              </div>
            </div>

            <div class="emmail-all-data-item">
              <div style="display: flex; align-items: center; gap: 10px; flex: 2">
                <div>
                  <a>
                    <span
                      style="font-size: 22px"
                      class="icon iconfont icon-a-011_xinjian"
                    ></span>
                  </a>
                </div>
                <span>打开：</span>
                <a>{{ emailAllData.openCount }}</a>
              </div>
              <div style="display: flex; align-items: center; gap: 10px; flex: 3">
                <span>打开率：</span>
                <a-progress
                  style="width: 64%"
                  :percent="emailAllData.openRate"
                  status="active"
                />
              </div>
            </div>
            <div class="emmail-all-data-item">
              <div style="display: flex; align-items: center; gap: 10px; flex: 2">
                <div>
                  <a>
                    <span
                      style="font-size: 22px"
                      class="icon iconfont icon-dahuifu"
                    ></span>
                  </a>
                </div>
                <span>回复：</span>
                <a>{{ emailAllData.replyCount }}</a>
              </div>
              <div style="display: flex; align-items: center; gap: 10px; flex: 3">
                <span>回复率：</span>
                <a-progress
                  style="width: 64%"
                  :percent="emailAllData.replyRate"
                  status="active"
                />
              </div>
            </div>
            <div class="emmail-all-data-item">
              <div style="display: flex; align-items: center; gap: 10px; flex: 2">
                <div>
                  <a>
                    <span
                      style="font-size: 22px"
                      class="icon iconfont icon-24gl-envelopeCross"
                    ></span>
                  </a>
                </div>
                <span>失败：</span>
                <a>{{ emailAllData.bounceCount }}</a>
              </div>
              <div style="display: flex; align-items: center; gap: 10px; flex: 3">
                <span>失败率：</span>
                <a-progress
                  style="width: 64%"
                  :percent="emailAllData.bounceRate"
                  status="active"
                />
              </div>
            </div>
          </div>
        </div>
      </a-col>
      <a-col :span="18">
        <div class="emai-statistics-data-card">
          <div class="email-statistics-data-card-title">
            <div class="email-statistics-data-card-title-left">
              <span>顾问明细</span>
              <div>
                <a-input-group compact>
                  <a-select
                    style="border-right: 0px;"
                    @change="emailCounselorFormDateTypeChange"
                    v-model:value="emailCounselorForm.dateType"
                  >
                    <a-select-option :value="0">月份</a-select-option>
                    <a-select-option :value="1">年份</a-select-option>
                  </a-select>

                  <a-month-picker
                    v-if="emailCounselorForm.dateType == 0"
                    @change="emailCounselorFormDateChange"
                    v-model:value="emailCounselorForm.dateValue"
                  />

                  <a-date-picker
                    v-if="emailCounselorForm.dateType == 1"
                    v-model:value="emailCounselorForm.dateValue"
                    placeholder="请选择年份"
                    mode="year"
                    format="YYYY"
                    :open="yearShowOne"
                    @open-change="openChangeOne"
                    @panel-change="panelChangeOne"
                  />
                </a-input-group>
              </div>
              <div style="display: flex; gap: 12px" v-if="emailCounselorForm.type == 1">
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #dce4fa"></div>
                  <div>成功</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #246bfd"></div>
                  <div>打开</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #00c695"></div>
                  <div>回复</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #5c0202"></div>
                  <div>失败</div>
                </div>
              </div>
            </div>
            <div class="">
              <a-radio-group v-model:value="emailCounselorForm.type" button-style="solid">
                <a-radio-button :value="0"> 列表 </a-radio-button>
                <a-radio-button :value="1"> 图表 </a-radio-button>
              </a-radio-group>
            </div>
          </div>
          <div   v-if="emailCounselorForm.type === 0" style="height: 260px;">
          
            <vxe-grid
              :loading="emailCounselorFormLoading"
            
              style="margin-top: 10px"
              :row-class-name="rowClassName"
              :cell-class-name="cellClassName"
              size="mini"
              height="auto"
              class="reverse-table"
              v-bind="gridOptions"
            ></vxe-grid>
          </div>
          <div     v-if="emailCounselorForm.type === 1" style="margin-top: 10px">
            <div ref="counselorChart" style="width: 100%; height: 260px"></div>
          </div>
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="12">
      <a-col :span="24">
        <div class="emai-statistics-data-card">
          <div class="email-statistics-data-card-title">
            <div class="email-statistics-data-card-title-left">
              <span>日期数据</span>
              <div>
                <a-input-group compact>
                  <a-select style="width: 100px;border-right: 0px;" v-model:value="emailDateForm.selectType">
                    <a-select-option :value="0">默认区间</a-select-option>
                    <a-select-option :value="1">自定义区间</a-select-option>
                  </a-select>
                  <a-radio-group
                    v-if="emailDateForm.selectType === 0"
                    @change="emailDateFormDateChange"
                    v-model:value="emailDateForm.dateType"
                    button-style="solid"
                  >
                    <a-radio-button
                      v-for="item in dateOptions"
                      :key="item.value"
                      :value="item.value"
                    >
                      {{ item.label }}
                    </a-radio-button>
                  </a-radio-group>
                  <a-range-picker
                    v-else
                    @change="emailDateFormDateRangeChange"
                   
                    v-model:value="emailDateForm.dateRange"
                  />
                </a-input-group>
              </div>

              <div style="display: flex; gap: 12px">
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #dce4fa"></div>
                  <div>成功</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #246bfd"></div>
                  <div>打开</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #00c695"></div>
                  <div>回复</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #5c0202"></div>
                  <div>失败</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #fab005"></div>
                  <div>回复率</div>
                </div>
                <div style="display: flex; align-items: center; gap: 6px">
                  <div style="width: 10px; height: 10px; background-color: #C8EB39"></div>
                  <div>打开率</div>
                </div>
              </div>
            </div>
          </div>
          <div
            class="date-chart-container"
            style="margin-top: 10px"
            :style="{
              height:
                windowHeight - 90 - 20 - 32 - 28 - 10 - 10 - 28 - 10 - 32 - 260 + 'px',
            }"
          >
            <div ref="dateChart" style="width: 100%; height: 100%"></div>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script setup>
import { ref, shallowRef, watch, onMounted, onBeforeUnmount, nextTick } from "vue";
import dayjs from "dayjs";
import { defHttp } from "/@/utils/http/axios";
import * as echarts from "echarts";
import { message } from "ant-design-vue";


// 响应式数据
const emailAllForm = ref({
  dateType: 0,
  dateValue: null,
});

const emailCounselorFormLoading = ref(false);
const emailAllFormYearShowOne = ref(false);
const yearShowOne = ref(false);
const emailAllData = ref({});
const allCounselorEmailData = ref([]);
const emailDateData = ref([]);

const emailCounselorForm = ref({
  dateType: 0,
  dateValue: null,
  type: 0,
});

const dateOptions = [
  {
    label: "近七天",
    value: 0,
    startDate: dayjs().subtract(6, "days").format("YYYY-MM-DD 00:00:00"),
    endDate: dayjs().format("YYYY-MM-DD 23:59:59"),
  },
  {
    label: "近14天",
    value: 1,
    startDate: dayjs().subtract(13, "days").format("YYYY-MM-DD 00:00:00"),
    endDate: dayjs().format("YYYY-MM-DD 23:59:59"),
  },
  {
    label: "近30天",
    value: 2,
    startDate: dayjs().subtract(29, "days").format("YYYY-MM-DD 00:00:00"),
    endDate: dayjs().format("YYYY-MM-DD 23:59:59"),
  },
];

const emailDateForm = ref({
  dateType: 0,
  dateValue: null,
  dateRange: [],
  selectType: 0,
});

const windowHeight = ref(undefined);

const gridOptions = ref({
  border: false,
  showHeader: false,
  data: [],
  columns: [],
});

const chartInstance = shallowRef(null); // 使用 shallowRef 存储图表实例
const dateChartInstance = shallowRef(null); // 使用 shallowRef 存储图表实例

// refs
const counselorChart = ref(null);
const dateChart = ref(null);

// 方法
const emailAllFormDateTypeChange = () => {
  emailAllForm.value.dateValue = undefined;
};

const emailCounselorFormDateTypeChange = () => {
  emailCounselorForm.value.dateValue = undefined;
};

const openChangeOne = (status) => {
  if (status) {
    yearShowOne.value = true;
  }
};

const emailAllFormOpenChangeOne = (status) => {
  if (status) {
    emailAllFormYearShowOne.value = true;
  }
};

const panelChangeOneAll = (value) => {
  emailAllForm.value.dateValue = value;
  emailAllFormYearShowOne.value = false;
  initAllEmailData();
};

const panelChangeOne = (value) => {
  emailCounselorForm.value.dateValue = value;
  yearShowOne.value = false;
  initAllCounselorEmailData();
};

const cellClassName = ({ columnIndex }) => {
  if (columnIndex === 0) {
    return "reverse-table-header";
  }
};

const rowClassName = ({ rowIndex }) => {
  if (rowIndex === 0) {
    return "reverse-table-header";
  }
};

const emailCounselorFormDateChange = (value) => {
  if (value) {
    initAllCounselorEmailData();
  }
};

const emailAllFormDateChange = (value) => {
  if (value) {
    initAllEmailData();
  }
};

const emailDateFormDateChange = () => {
  initEmailDateData();
};

const emailDateFormDateRangeChange = () => {
  initEmailDateData();
};

const initEmailDateData = () => {
  const params = {};
  if (emailDateForm.value.selectType === 0) {
    params.startDate = dateOptions[emailDateForm.value.dateType].startDate;
    params.endDate = dateOptions[emailDateForm.value.dateType].endDate;
  } else {
    params.startDate = emailDateForm.value.dateRange[0].format("YYYY-MM-DD 00:00:00");
    params.endDate = emailDateForm.value.dateRange[1].format("YYYY-MM-DD 23:59:59");
  }
  defHttp.post({ url: "/email/emailPushDetail/businessDailyTrend", data: params }).then((res) => {
    console.log(res);
    if (res) {
      emailDateData.value = res;
      nextTick(() => {
        initDateChart();
      });
    } else {
      message.error(res.message);
    }
  });
};

const initAllEmailData = () => {
  const params = {};
  if (emailAllForm.value.dateType === 0) {
    params.startDate = dayjs(emailAllForm.value.dateValue)
      .startOf("month")
      .format("YYYY-MM-DD 00:00:00");
    params.endDate = dayjs(emailAllForm.value.dateValue)
      .endOf("month")
      .format("YYYY-MM-DD 23:59:59");
  } else {
    params.startDate = dayjs(emailAllForm.value.dateValue)
      .startOf("year")
      .format("YYYY-MM-DD 00:00:00");
    params.endDate = dayjs(emailAllForm.value.dateValue)
      .endOf("year")
      .format("YYYY-MM-DD 23:59:59");
  }
  defHttp.post({ url: "/email/emailPushDetail/businessSummary", data: params }).then((res) => {
    console.log(res);
    if (res) {
      emailAllData.value = res;
    } else {
      message.error(res.message);
    }
  });
};

const reverse = (columns, counselorData) => {
  // 1️⃣ columns → col1, col2, col3...
  const tableColumns = [
    { field: "col1", width: 60 ,fixed:'left',align:'center'},
    ...columns.map((_, index) => ({
      field: `col${index + 2}`,
      minWidth: 100,
    })),
  ];

  const rows = [
    { key: "counselorName", label: "顾问" },
    { key: "totalSentCount", label: "发送" },
    { key: "actualSentCount", label: "成功" },
    { key: "openCount", label: "打开" },
    { key: "replyCount", label: "回复" },
    { key: "bounceCount", label: "退信" },
  ];

  // 3️⃣ 行转列
  const tableData = rows.map((row) => {
    const obj = { col1: row.label };

    columns.forEach((col, i) => {
      const target = counselorData.find((d) => d.counselorName === col.field);
      let val = target ? target[row.key] : null;

      // 顾问行直接显示"顾问名称"
      if (row.key === "counselorName") {
        val = col.field;
      }

      // 百分号处理(仅百分比行)
      if (row.suffix && val !== null && val !== undefined) {
        val = val + row.suffix;
      }

      obj[`col${i + 2}`] = val;
    });

    return obj;
  });

  return {
    columns: tableColumns,
    data: tableData,
  };
};

const initAllCounselorEmailData = () => {
  const params = {};
  if (emailCounselorForm.value.dateType === 0) {
    params.startDate = dayjs(emailCounselorForm.value.dateValue)
      .startOf("month")
      .format("YYYY-MM-DD 00:00:00");
    params.endDate = dayjs(emailCounselorForm.value.dateValue)
      .endOf("month")
      .format("YYYY-MM-DD 23:59:59");
  } else {
    params.startDate = dayjs(emailCounselorForm.value.dateValue)
      .startOf("year")
      .format("YYYY-MM-DD 00:00:00");
    params.endDate = dayjs(emailCounselorForm.value.dateValue)
      .endOf("year")
      .format("YYYY-MM-DD 23:59:59");
  }
  emailCounselorFormLoading.value = true;
  console.log(params);
  defHttp.post({ url: "/email/emailPushDetail/businessCounselorTop", data: params })
    .then((res) => {
      console.log(res);
      if (res) {
        allCounselorEmailData.value = res;
        const tableColumns = res.map((item) => {
          return {
            field: item.counselorName,
            title: item.counselorName,
          };
        });
        const counselorData = res.map((item) => {
          return {
            counselorName: item.counselorName,
            totalSentCount: item.totalSentCount,
            actualSentCount: `${item.actualSentCount} (${item.sendSuccessRate}%)`,
            openCount: `${item.openCount} (${item.openRate}%)`,
            replyCount: `${item.replyCount} (${item.replyRate}%)`,
            bounceCount: `${item.bounceCount} (${item.bounceRate}%)`,
          };
        });
        const { columns, data } = reverse(tableColumns, counselorData);
        gridOptions.value.columns = columns;
        gridOptions.value.data = data;

        // 如果当前是图表模式,更新图表
        if (emailCounselorForm.value.type === 1) {
          nextTick(() => {
            initChart();
          });
        }
      } else {
        message.error(res.message);
      }
    })
    .finally(() => {
      emailCounselorFormLoading.value = false;
    });
};

const initChart = () => {
  if (!counselorChart.value) return;

  // 销毁旧实例
  if (chartInstance.value) {
    chartInstance.value.dispose();
  }

  // 创建新实例
  chartInstance.value = echarts.init(counselorChart.value);

  // 按 totalSentCount 降序排序
  const sortedData = [...allCounselorEmailData.value].sort(
    (a, b) => b.totalSentCount - a.totalSentCount
  );

  // 准备数据
  const counselorNames = sortedData.map((item) => item.counselorName);
  const replyData = sortedData.map((item) => item.replyCount);
  const openData = sortedData.map((item) => item.openCount);
  const bounceData = sortedData.map((item) => item.bounceCount);
  const sentData = sortedData.map((item) => item.actualSentCount);

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
        crossStyle: {
          color: "#999",
        },
      },
      formatter: function (params) {
        const dataIndex = params[0].dataIndex;
        const data = sortedData[dataIndex];
        return `
          <div style="padding: 5px;">
            <div style="display: flex;  margin-bottom: 4px;align-items: center;">
              <div style="width: 10px; height: 10px; background-color: #DCE4FA; margin-right: 4px;"></div>
              <span>成功数：</span>
              <span style="font-weight: bold;">${data.actualSentCount} (${data.sendSuccessRate}%)</span>
            </div>
            <div style="display: flex; margin-bottom: 4px;align-items: center;">
              <div style="width: 10px; height: 10px; background-color: #00C695; margin-right: 4px;"></div>
              <span>回复数：</span>
              <span style="font-weight: bold;">${data.replyCount} (${data.replyRate}%)</span>
            </div>
            <div style="display: flex; margin-bottom: 4px;align-items: center;">
              <div style="width: 10px; height: 10px; background-color: #246BFD; margin-right: 4px;"></div>
              <span>打开数：</span>
              <span style="font-weight: bold;">${data.openCount} (${data.openRate}%)</span>
            </div>
            <div style="display: flex; margin-bottom: 4px;align-items: center;">
              <div style="width: 10px; height: 10px; background-color: #5C0202; margin-right: 4px;"></div>
              <span>失败数：</span>
              <span style="font-weight: bold;">${data.bounceCount} (${data.bounceRate}%)</span>
            </div>
         
          </div>
        `;
      },
    },
    grid: {
      left: "0%",
      right: "0%",
      bottom: 8,
      top: "10%",
      containLabel: true,
    },
    dataZoom: {
      type: "slider",
      show: counselorNames.length > 15 ? true : false,
      startValue: 0,
      endValue: counselorNames.length > 15 ? 15 : counselorNames.length,
      height: 6,
      bottom: 0,
      showDetail: false,
      showDataShadow: false,
      fillerColor: "#dbdee5",
      borderColor: "transparent",
      zoomLock: true,
      brushSelect: false,
      handleStyle: {
        opacity: 0,
      },
    },
    xAxis: {
      type: "category",
      data: counselorNames,
      axisLabel: {
        interval: 0,
        rotate: 0,
      },
      axisPointer: {
        type: "shadow",
      },
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: "回复",
        type: "bar",
        stack: "total",
        barWidth: 24,
        label: {
          show: false,
        },
        data: replyData,
        itemStyle: {
          color: "#00C695",
        },
      },
      {
        name: "打开",
        type: "bar",
        stack: "total",
        barWidth: 24,
        label: {
          show: false,
        },
        data: openData,
        itemStyle: {
          color: "#246BFD",
        },
      },
      {
        name: "失败",
        type: "bar",
        stack: "total",
        barWidth: 24,
        label: {
          show: false,
        },
        data: bounceData,
        itemStyle: {
          color: "#5C0202",
        },
      },
      {
        name: "成功",
        type: "bar",
        stack: "total",
        barWidth: 24,
        label: {
          show: true,
          position: "top",
          formatter: function (params) {
            const dataIndex = params.dataIndex;
            return sortedData[dataIndex].totalSentCount;
          },
        },
        data: sentData,
        itemStyle: {
          color: "#DCE4FA",
        },
      },
    ],
  };

  chartInstance.value.setOption(option);
};

function handleResize() {
  chartInstance.value?.resize();
  dateChartInstance.value?.resize();
}

const initDateChart = () => {
  if (!dateChart.value) return;

  // 销毁旧实例
  if (dateChartInstance.value) {
    dateChartInstance.value.dispose();
  }

  // 创建新实例
  dateChartInstance.value = echarts.init(dateChart.value);

  // 准备数据
  const dates = emailDateData.value.map((item) => item.date.substring(5)); // 只显示月-日
  const sentData = emailDateData.value.map((item) => item.actualSentCount);
  const replyData = emailDateData.value.map((item) => item.replyCount);
  const openData = emailDateData.value.map((item) => item.openCount);
  const bounceData = emailDateData.value.map((item) => item.bounceCount);
  const replyRateData = emailDateData.value.map((item) => item.replyRate);
  const openRateData = emailDateData.value.map((item) => item.openRate);

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
        crossStyle: {
          color: "#999",
        },
      },
      formatter: function (params) {
        const dataIndex = params[0].dataIndex;
        const data = emailDateData.value[dataIndex];
        let result = `<div style="padding: 5px;">`;

        result += `<div style="display: flex; margin-bottom: 4px;align-items: center;">`;
        result += `<div style="width: 10px; height: 10px; background-color: #DCE4FA; margin-right: 4px;"></div>`;
        result += `<span>成功数：</span><span style="font-weight: bold;">${data.actualSentCount} (${data.sendSuccessRate}%)</span>`;
        result += `</div>`;
        result += `<div style="display: flex; margin-bottom: 4px;align-items: center;">`;
        result += `<div style="width: 10px; height: 10px; background-color: #246BFD; margin-right: 4px;"></div>`;
        result += `<span>打开数：</span><span style="font-weight: bold;">${data.openCount} (${data.openRate}%)</span>`;
        result += `</div>`;
        result += `<div style="display: flex; margin-bottom: 4px;align-items: center;">`;
        result += `<div style="width: 10px; height: 10px; background-color: #00C695; margin-right: 4px;"></div>`;
        result += `<span>回复数：</span><span style="font-weight: bold;">${data.replyCount} (${data.replyRate}%)</span>`;
        result += `</div>`;
        result += `<div style="display: flex; margin-bottom: 4px;align-items: center;">`;
        result += `<div style="width: 10px; height: 10px; background-color: #5C0202; margin-right: 4px;"></div>`;
        result += `<span>失败数：</span><span style="font-weight: bold;">${data.bounceCount} (${data.bounceRate}%)</span>`;
        result += `</div>`;
        result += `</div>`;
        result += `</div>`;
        return result;
      },
    },
    grid: {
      left: "0%",
      right: "0%",
      bottom: 8,
      top: "10%",
      containLabel: true,
    },
    dataZoom: {
      type: "slider",
      show: dates.length > 30 ? true : false,
      startValue: 0,
      endValue: dates.length > 30 ? 30 : dates.length,
      height: 6,
      bottom: 0,
      showDetail: false,
      showDataShadow: false,
      fillerColor: "#dbdee5",
      borderColor: "transparent",
      zoomLock: true,
      brushSelect: false,
      handleStyle: {
        opacity: 0,
      },
    },
    xAxis: {
      type: "category",
      data: dates,
      axisPointer: {
        type: "shadow",
      },
    },
    yAxis: [
      {
        type: "value",
        name: "",
        axisLabel: {
          formatter: "{value}",
        },
      },
      {
        type: "value",
        name: "",
        axisLabel: {
          formatter: "{value}%",
        },
      },
    ],
    series: [
      {
        name: "失败数",
        type: "bar",
        stack: "total",
        barWidth: 24,
        data: bounceData,
        itemStyle: {
          color: "#5C0202",
        },
      },
      {
        name: "回复数",
        type: "bar",
        stack: "total",
        barWidth: 24,
        data: replyData,
        itemStyle: {
          color: "#00C695",
        },
      },
      {
        name: "打开数",
        type: "bar",
        stack: "total",
        barWidth: 24,
        data: openData,
        itemStyle: {
          color: "#246BFD",
        },
      },
      {
        name: "成功数",
        type: "bar",
        stack: "total",
        barWidth: 24,
        data: sentData,
        itemStyle: {
          color: "#DCE4FA",
        },
        label: {
          show: true,
          position: "top",
          formatter: function (params) {
            const dataIndex = params.dataIndex;
            return emailDateData.value[dataIndex].totalSentCount;
          },
        },
      },
      {
        name: "回复率",
        type: "line",
        yAxisIndex: 1,
        smooth: true,
        data: replyRateData,
        itemStyle: {
          color: "#FFA500",
        },
        lineStyle: {
          width: 2,
        },
      },
      {
        name: "打开率",
        type: "line",
        yAxisIndex: 1,
        smooth: true,
        data: openRateData,
        itemStyle: {
          color: "#C8EB39",
        },
        lineStyle: {
          width: 2,
        },
      },
    ],
  };

  dateChartInstance.value.setOption(option);
};

// 监听
watch(
  () => emailCounselorForm.value.type,
  (newVal) => {
    if (newVal === 1) {
      nextTick(() => {
        initChart();
      });
    }
  }
);

emailAllForm.value.dateValue = dayjs();
emailCounselorForm.value.dateValue = dayjs();

onMounted(() => {
  windowHeight.value = window.innerHeight;
  console.log(windowHeight.value);
  // 初始化数据
  initAllEmailData();
  initAllCounselorEmailData();
  initEmailDateData();
  window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  if (chartInstance.value) {
    chartInstance.value.dispose();
    chartInstance.value = null;
  }
  if (dateChartInstance.value) {
    dateChartInstance.value.dispose();
    dateChartInstance.value = null;
  }
});
</script>
<style>
.reverse-table-header {
  background-color: #f4f7ff;
}
</style>
<style lang="less" scoped>
:deep(.ant-radio-button-wrapper:not(:first-child)::before) {
  width: 0.1px !important;
  background-color: transparent;
}
:deep(.ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before) {
  background-color: #3155ed !important;
}
:deep( .vxe-table--render-default.size--mini .vxe-body--column:not(.col--ellipsis)) {
  padding: 3px 0px !important;
}
.email-statistics-container {
  padding: 10px;
  .emai-statistics-data-card {
    padding: 14px;
    background-color: #fff;
    border-radius: 10px;
    .email-statistics-data-card-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .email-statistics-data-card-title-left {
      display: flex;
      gap: 6px;
      align-items: center;
      font-size: 12px;
    }
  }
}
.emmail-all-data {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  row-gap: 8px;
  height: 260px;
  justify-content: space-between;

  .emmail-all-data-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #f4f7ff;
    border-radius: 4px;
    padding: 0 20px;
    flex: 1 1 0; // 使高度均分
    a {
      font-size: 16px;
    }
  }
}
</style>
