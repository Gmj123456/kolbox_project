<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div id="dataBoardBox">
    <a-row class="dataBoardBox_top" :gutter="12">
      <a-col class="dataBoardBox_top_col" :span="8">
        <!-- 网红数据概览 -->
        <SummaryExpertData />
      </a-col>
      <a-col class="dataBoardBox_top_col" :span="16">
        <!-- 顾问签约数量汇总 -->
        <SummaryExpertCountries />
      </a-col>
    </a-row>
    <a-row class="dataBoardBox_bottom" :gutter="12">
      <a-col class="dataBoardBox_top_col" :span="8">
        <!-- 签约价格范围 -->
        <SummarySignedPriceRange :celebritySegmentList="celebritySegmentList" />
      </a-col>
      <a-col class="dataBoardBox_top_col" :span="8">
        <!-- 粉丝数量范围 -->
        <SummaryFanCountRange :celebritySegmentList="celebritySegmentList" />
      </a-col>
      <a-col class="dataBoardBox_top_col" :span="8">
        <!-- 网红类目汇总 -->
        <SummaryConsultantSigningQuantity />
      </a-col>
    </a-row>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue';
import { getDictItems } from '@/api/common/api';
import SummaryExpertData from './component/summaryExpertData.vue';
import SummaryExpertCountries from './component/summaryExpertCountries.vue';
import SummaryConsultantSigningQuantity from './component/summaryConsultantSigningQuantity.vue';
import SummarySignedPriceRange from './component/summarySignedPriceRange.vue';
import SummaryFanCountRange from './component/summaryFanCountRange.vue';

const celebritySegmentList = ref([]);

async function initDict() {
  const res = await getDictItems('celebrity_segment');
  if (res) {
    celebritySegmentList.value = res || [];
  }
}

onMounted(() => {
});
initDict();
</script>
<style lang="less" scoped>
#dataBoardBox {
  padding: 12px;
  height: calc(100vh - 94px);
  min-height: calc(100vh - 94px);
  display: flex;
  flex-direction: column;

  .dataBoardBox_top {
    flex: 2;
    margin-bottom: 12px;

    .dataBoardBox_top_col {
      height: 100%;
    }
  }

  .dataBoardBox_bottom {
    flex: 4;

    .dataBoardBox_top_col {
      height: 100%;
    }
  }
}
</style>