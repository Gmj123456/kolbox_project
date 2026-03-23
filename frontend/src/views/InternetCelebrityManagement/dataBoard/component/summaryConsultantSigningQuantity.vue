<template>
  <div class="summaryConsultantSigningQuantity">
    <div class="title">
      <div class="title_lf">网红类目汇总</div>
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
        <a @click="loadData" style="margin-left: 10px">
          <SyncOutlined style="font-size: 16px" />
        </a>
      </div>
    </div>
    
    <div class="summaryConsultantSigningQuantity_table">
      <vxe-table
        ref="vxeTableRef"
        :data="dataSourse"
        :height="vxeHeight"
        :footer-method="footerMethod"
        show-footer
      
        :loading="loading"
        size="mini"
        stripe
        show-overflow
      >
        <vxe-table-column field="id" width="60px" title="序号" show-overflow >
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
        <vxe-table-column field="categoryName" width="120px" title="类目" show-overflow />
        <vxe-table-column field="totalCelebrityCount" title="合计">
          <template #default="{ row }">
            <a-popover overlayClassName="popover_box" placement="bottom">
              <template #content>
                <div>
                  <div
                    v-for="(item, i) in totalParse(row.counselorStats)"
                    :key="i"
                    style="display: flex; justify-content: space-between"
                  >
                    <span>{{ item.counselorName }}</span>
                    <span style="margin-left: 20px">{{ item.count }}</span>
                  </div>
                </div>
              </template>
              <a v-if="row.totalCelebrityCount"> {{ row.totalCelebrityCount }}</a>
              <a v-else> {{ row.totalCelebrityCount }}</a>
            </a-popover>
          </template>
        </vxe-table-column>
        <vxe-table-column
          v-for="(item, i) in columns"
          :key="i"
          :field="item.column"
          :title="item.name"
        >
          <template #default="{ row, column }">
            <a-popover overlayClassName="popover_box" placement="bottom">
              <template #content>
                <div>
                  <div
                    v-for="(item, i) in parseLevel(row.counselorStats, column.property)"
                    :key="i"
                    style="display: flex; justify-content: space-between"
                  >
                    <span>{{ item.counselorName }}</span>
                    <span style="margin-left: 20px">{{ item.count }}</span>
                  </div>
                </div>
              </template>
              <a v-if="row[column.property]"> {{ row[column.property] }}</a>
              <a v-else> {{ row[column.property] }}</a>
            </a-popover>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>
<script setup>
import { SyncOutlined } from '@ant-design/icons-vue';
import XEUtils from 'xe-utils';
import { onMounted, ref, nextTick } from 'vue';
import { defHttp } from '/@/utils/http/axios';

const queryParams = ref({
  platformType: 2,
});

const vxeHeight = ref(0);
const dataSourse = ref([]);
const columns = ref([]);
const loading = ref(false);
const vxeTableRef = ref(null);

// 使用普通函数而非箭头函数，确保 this 指向正确
function totalParse(arr) {
  if (!arr || !arr.length) return [];
  const counselorMap = new Map();
  arr.forEach(({ counselorName, count }) => {
    counselorMap.set(counselorName, (counselorMap.get(counselorName) || 0) + count);
  });
  return Array.from(counselorMap).map(([counselorName, count]) => ({
    counselorName,
    count,
  }));
}

function parseLevel(data, column) {
  // 确保数据存在且为数组
  if (!data || !Array.isArray(data)) return [];
  return data.filter((item) => item.starLevel === column);
}

// 使用普通函数定义 footerMethod
function footerMethod({ columns: cols, data }) {
  const sums = [];
  cols.forEach((column, columnIndex) => {
    if (columnIndex === 0) {
      sums.push(data.length + 1);
    } else if (columnIndex === 1) {
      sums.push('合计');
    } else {
      let sumCell = null;
      if (column.property !== 'categoryName') {
        sumCell = XEUtils.sum(data, column.property);
      }
      sums.push(sumCell);
    }
  });
  return [sums];
}

function platformTypeChange() {
  loadData();
}

async function loadData() {
  try {
    loading.value = true;
    const res = await defHttp.get({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/getCategoryStats',
      params: {
        platformType: queryParams.value.platformType,
      },
    });
    if (res && res.data) {
      // 确保在 DOM 更新后再设置数据
      await nextTick();
      dataSourse.value = res.data.map((item, index) => ({
        ...item,
        id: index + 1,
      })) || [];
      columns.value = res.columns || [];
    }
  } catch (error) {
    console.error('加载数据失败:', error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  // 确保 DOM 已经渲染完成
  nextTick(() => {
    const wrap = document.querySelector('.summaryConsultantSigningQuantity_table');
    vxeHeight.value = wrap ? wrap.offsetHeight : 0;
  });
});

// 页面加载时获取数据
loadData();
</script>
<style lang="less" scoped>
  :deep(.ant-select-selection-item){
    display: flex !important;
    align-items: center !important;
  }
.LevelContent {
  height: 150px;
  overflow-y: auto;
}

.summaryConsultantSigningQuantity {
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

  .summaryConsultantSigningQuantity_table {
    flex: 1;
  }
}
</style>
<style scoped>
.summaryConsultantSigningQuantity .title_rg .ant-select-selection {
  border: none !important;
}

.summaryConsultantSigningQuantity .title_rg .ant-select-focused .ant-select-selection,
.ant-select-selection:focus,
.ant-select-selection:active {
  box-shadow: none !important;
}

.popover_box .ant-popover-inner-content {
  overflow-y: auto !important;
  max-height: 150px !important;
}
</style>