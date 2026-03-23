<template>
  <a-card :bordered="false">
      <a-form  class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <JDictSelectPlatformType
                v-model:value="queryParam.platformType"
                dictCode="platform_type"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :xl="8" :lg="10" :md="12" :sm="24">
            <a-form-item>
              <a-space>
                <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">
                  查询
                </a-button>
                <a-button :icon="h(ReloadOutlined)" @click="handleSearchReset">重置</a-button>
                <a-button
                  type="primary"
                  :icon="h(DownloadOutlined)"
                  :disabled="!selectedRowKeys.length"
                  @click="handleExportXls('分配日志')"
                >
                  导出
                </a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>


    <a-table
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :row-selection="rowSelection"
      :loading="loading"
      :pagination="pagination"
   
      :scroll="{ y: sTableHeight, x: '100%' }"
      :rangeSelection="false"
      @change="pageChange"
      
    >
      <template #bodyCell="{ column, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'platformType'">
          <img v-if="platformTypeImg(text)" :src="platformTypeImg(text)" class="platform-icon" />
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'allotContent'">
          <div style="display: flex;
    flex-wrap: wrap;
    row-gap: 6px;">
            <a-tag
            v-for="(item, itemIndex) in tagContent(text)"
            :key="itemIndex"
            @click="copyFn(item.split(':')[0])"
          
            class="allot-tag"
          >
            {{ item }}
          </a-tag>
          </div>
        </template>
        <template v-else>
          {{ text || '--' }}
        </template>
      </template>
    </a-table>

   
  </a-card>
</template>

<script setup name="allocationTagSummaryList">
import { h,nextTick } from 'vue';
import { SearchOutlined, ReloadOutlined, DownloadOutlined } from '@ant-design/icons-vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { useTable } from '/@/components/useSTable/useSTable';
import JDictSelectPlatformType from '@/components/jeecg/JDictSelectPlatformType.vue';

const { createMessage } = useMessage();

const Api = {
  List: '/kol/kolAllotLogDetail/list',
  Export: '/kol/kolAllotLogDetail/exportXls',
};

const columns = [
  { title: '#', key: 'index', width: 60 ,align: 'center'},
  { title: '平台', dataIndex: 'platformType', key: 'platformType', width: 120 },
  { title: '网红顾问', dataIndex: 'counselorName', key: 'counselorName', width: 200 },
  { title: '分配日期', dataIndex: 'allotTime', key: 'allotTime', width: 200 },
  { title: '分配内容', dataIndex: 'allotContent', key: 'allotContent',autoHeight:true },
];

async function allocationTagSummaryAfterFetchFn(params,res) {
  nextTick(() => {
    calcTableHeight('aTable');
  });
}

async function loadDataApi(params) {
  const res = await defHttp.get({ url: Api.List, params });
  return res || [];
}

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  rowSelection,
  selectedRowKeys,
  searchQuery,
  searchReset,
  pageChange,
  copyFn,
  calcTableHeight
} = useTable(loadDataApi,0,{tableType:"aTable",afterFetch:allocationTagSummaryAfterFetchFn});

queryParam.value = {
  platformType: undefined,
};

const platformIcons = {
  0: new URL('../../../assets/images/ins.png', import.meta.url).href,
  1: new URL('../../../assets/images/yt.png', import.meta.url).href,
  2: new URL('../../../assets/images/tk.png', import.meta.url).href,
};

function platformTypeImg(value) {
  return platformIcons[value] || '';
}

function tagContent(text) {
  if (!text) return [];
  try {
    const obj = JSON.parse(text);
    return Object.entries(obj).map(([key, value]) => `${key}:${value}`);
  } catch (error) {
    return [];
  }
}

function handleSearchReset() {
  searchReset();
  queryParam.value = { platformType: undefined };
  fetchTable(1);
}

async function handleExportXls(fileName = '分配日志') {
  if (!selectedRowKeys.value.length) {
    createMessage.warning('请选择需要导出的记录');
    return;
  }
  try {
    const params = {
      ...queryParam.value,
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      allotIds: selectedRowKeys.value.join(','),
    };
    const blob = await defHttp.get(
      { url: Api.Export, params, responseType: 'blob' },
      { isTransformResponse: false },
    );
    if (!blob) {
      createMessage.warning('文件下载失败');
      return;
    }
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement('a');
    link.style.display = 'none';
    link.href = url;
    link.download = `${fileName}.xls`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error(error);
    createMessage.error('导出失败');
  }
}

fetchTable(1);
</script>

<style scoped lang="less">
/deep/ .ant-radio-button-wrapper:not(:first-child)::before {
  width: 0.1px !important;
  background-color: transparent;
}
/deep/ .ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before {
  background-color: #3155ed !important;
}
.platform-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}


</style>
 
