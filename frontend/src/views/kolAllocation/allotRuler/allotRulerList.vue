<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
      <a-form class="searchForm"  @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="7" :md="8" :sm="24" style="transform: translateY(-1px)">
            <a-form-item>
              <JDictSelectPlatformType
                v-model:value="queryParam.platformType"
                dictCode="platform_type"
                :triggerChange="true"
              ></JDictSelectPlatformType>
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.ruleLevel" placeholder="规则档位"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span
              style="float: left; overflow: hidden"
              class="table-page-search-submitButtons"
            >
              <a-button type="primary" @click="searchQuery" :icon="h(SearchOutlined)">查询</a-button>
              <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px"
                >重置</a-button
              >
              <a-button
                @click="handleAdd"
                type="primary"
                :icon="h(PlusOutlined)"
                style="margin-left: 8px"
                >新增</a-button
              >
            </span>
          </a-col>
        </a-row>
      </a-form>
   
    <s-table
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :scroll="{ y: sTableHeight, x: '100%' }"
      :rangeSelection="false"
      @change="pageChange"
    
    >
      <template #bodyCell="{ column, record, text }">
        <template v-if="column.key === 'platformType'">
          <img style="width: 22px; height: 22px" v-if="record.platformType === 0" src="@/assets/images/ins.png" alt="" />
          <img style="width: 22px; height: 22px" v-if="record.platformType === 1" src="@/assets/images/yt.png"  alt="" />
          <img style="width: 22px; height: 22px" v-if="record.platformType === 2" src="@/assets/images/tk.png"  alt="" />
        </template>
        <template v-else-if="column.key === 'minNum'">
          {{ getFollower(text) }}
        </template>
        <template v-else-if="column.key === 'maxNum'">
          {{ getFollower(text) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip placement="bottom" title="编辑">
            <a @click="handleEdit(record)">
              <FormOutlined style="font-size: 15px" />
            </a>
          </a-tooltip>

          <a-divider type="vertical" />
          <a-popconfirm
            placement="leftTop"
            title="确定删除吗?"
            @confirm="() => handleDelete(record.id)"
          >
            <a>
              <span class="icon iconfont icon-shanchu"></span>
            </a>
          </a-popconfirm>
        </template>
      </template>
    </s-table>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <allotRulerModal ref="modalFormRef" @ok="modalFormOk"></allotRulerModal>
  </a-card>
</template>

<script setup>
import { ref, h } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import { defHttp } from '/@/utils/http/axios';
import { SearchOutlined, ReloadOutlined, PlusOutlined, FormOutlined } from '@ant-design/icons-vue';
import JDictSelectPlatformType from '@/components/jeecg/JDictSelectPlatformType.vue';
import AllotRulerModal from './modules/allotRulerModal.vue';

const { createMessage } = useMessage();

const modalFormRef = ref(null);


async function loadDataApi(params) {
  const res = await defHttp.get({
    url: '/tiktokcelebrityrule/list',
    params,
  });
  return res;
}



// 使用 useTable hook
const {
  loading,
  dataSource,
  pagination,
  queryParam,
  fetchTable,
  pageChange,
  searchQuery,
  searchReset,
  sTableHeight,
} = useTable(loadDataApi);

// 表格列定义
const columns = [
  {
    title: '#',
    dataIndex: '',
    key: 'index',
    width: 60,
    align: 'center',
    customRender: function ({index}) {
      return parseInt(index) + 1;
    },
  },
  {
    title: '规则档位',
    align: 'center',
    dataIndex: 'ruleLevel',
    key: 'ruleLevel',
  },
  {
    title: '平台',
    dataIndex: 'platformType',
    key: 'platformType',
  },
  {
    title: '最小值',
    align: 'center',
    dataIndex: 'minNum',
    key: 'minNum',
  },
  {
    title: '最大值',
    align: 'center',
    dataIndex: 'maxNum',
    key: 'maxNum',
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'action',
    align: 'center',
  },
];

function platformTypeImg(text) {
  switch (text) {
    case 0:
      return require('../../assets/images/ins.png');
    case 1:
      return require('../../assets/images/yt.png');
    case 2:
      return require('../../assets/images/tk.png');
    default:
      return '';
  }
}

function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    const kValue = (num / 1000).toFixed(2);
    return parseFloat(kValue) % 1 === 0
      ? `${parseInt(kValue)}K`
      : `${parseFloat(kValue)}K`;
  } else if (num >= 1000000) {
    const mValue = (num / 1000000).toFixed(2);
    return parseFloat(mValue) % 1 === 0
      ? `${parseInt(mValue)}M`
      : `${parseFloat(mValue)}M`;
  } else if (num > 0 && num < 1000) {
    return num;
  } else {
    return '--';
  }
}

function handleAdd() {
  modalFormRef.value?.add();
}

function handleEdit(record) {
  modalFormRef.value?.edit(record);
}

async function handleDelete(id) {
  try {
    await defHttp.delete({ url: '/tiktokcelebrityrule/delete', params: { id } });
    fetchTable();
  } catch (error) {
    console.error('删除失败:', error);
  
  }
}

function modalFormOk() {
  fetchTable();
}
fetchTable()
</script>
<style scoped>
</style>
