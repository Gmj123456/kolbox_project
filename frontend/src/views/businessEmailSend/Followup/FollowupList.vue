<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <a-form  class="searchForm" @keyup.enter="searchQuery">
      <a-row :gutter="12">
       
        <a-col :xl="4" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-input v-model:value.trim="queryParam.followupName" placeholder="跟进名称"></a-input>
          </a-form-item>
        </a-col>
   
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item>
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
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <!-- table区域-begin -->
    <div>
      <s-table
       :animate-rows="false"
        :rangeSelection="false"
        ref="tableRef"
        size="small"
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        v-model:pagination="pagination"
        :loading="loading"
        :scroll="{ y: sTableHeight, x: '100%' }"
   
        @change="pageChange"
      >
        <template #bodyCell="{ column, record, text }">

          
          <template v-if="column.dataIndex === 'action'">
            <a-tooltip title="编辑">
              <a @click="handleEdit(record)">
                <FormOutlined style="font-size: 15px" />
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-popconfirm
              placement="topLeft"
              title="确定删除吗?"
              @confirm="handleDelete(record.id)"
            >
              <a-tooltip title="删除">
                <a>
                  <span class="icon iconfont icon-shanchu"></span>
                </a>
              </a-tooltip>
            </a-popconfirm>
          </template>
        </template>
      </s-table>
    </div>
    <FollowupModal ref="modalFormRef" @ok="modalFormOk"></FollowupModal>
  </a-card>
</template>

<script setup >
import { ref, reactive, onMounted, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, FormOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import FollowupModal from "./modules/FollowupModal.vue";

const { createMessage } = useMessage();

// Refs
const tableRef = ref(null);
const modalFormRef = ref(null);
const expandedRowKeys = ref([]);
// Data
const brandCategoryList = ref([]);

const url = {
  list: "/email/emailTemplateCategory/list",
  delete: "/email/emailTemplateCategory/delete",

  
};

// Use Table Hook
const fetchTableApi = async (params) => {
  const res = await defHttp.get({ url: url.list, params });
  return res || { records: [], total: 0 };
};

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  pageChange,
  fetchTable,
} = useTable(fetchTableApi);

// 表头
const columns = [
  {
    title: "#",
    dataIndex: "index",
    key: "rowIndex",
    width: 60,
    align: "center",
    customRender: ({ index }) => parseInt(index) + 1,
  },
  {
    title: "跟进名称",


    dataIndex: "categoryName",
  },
  {
    title: "备注",


    dataIndex: "remark",
  },
  {
    title: "创建时间",
    width: 250,

    dataIndex: "createTime",
  },
  
  {
    title: "操作",
    dataIndex: "action",
    width: 120,
    align: "center",
  },
];



function searchQuery() {
  fetchTable();
}

function searchReset() {
 
  queryParam.value.templateType = 1;
  fetchTable();
}

function handleAdd() {
  modalFormRef.value?.add();
}

function handleEdit(record) {
  modalFormRef.value?.edit(record);
}

async function handleDelete(id) {
  try {
    const res = await defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true });
    fetchTable();
    
  } catch (error) {
    console.error("删除失败:", error);
  }
}

function modalFormOk() {
  fetchTable();
}

queryParam.value.templateType = 1;

fetchTable();
</script>
<style scoped>

</style>
