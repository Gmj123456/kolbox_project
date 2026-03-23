<template>
  <a-card :bordered="false">
      <a-form class="searchForm"  @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                v-model:value="queryParam.title"
                placeholder="请输入文章标题"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                v-model:value="queryParam.url"
                placeholder="请输入文章链接"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item >
              <a-input
                v-model:value="queryParam.remark"
                placeholder="请输入备注"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">
                查询
              </a-button>
              <a-button
            
                :icon="h(ReloadOutlined)"
                style="margin-left: 8px"
                @click="searchReset"
              >
                重置
              </a-button>
              <a-button
                type="primary"
                :icon="h(PlusOutlined)"
                style="margin-left: 8px"
                @click="handleAdd"
              >
                新增
              </a-button>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item>
            
              <a-alert type="info">
                <template #message>
                    <span>已选中{{ selectedRowKeys.length }}条记录</span>
                    <a style="margin-left: 8px;" v-if="selectedRowKeys.length" type="link" @click="selectedRowKeys = []" >清空</a>
                    <a
                      v-if="selectedRowKeys.length > 0"
                      style="margin-left: 24px; color: red"
                      @click="batchDel"
                    >
                      删除
                    </a>
                
                
                </template>
              </a-alert>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <s-table
        ref="tableRef"
        :rangeSelection="false"
        size="small"
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="pagination"
        :loading="loading"
        :scroll="{ y: sTableHeight, x: '100%' }"
        :rowSelection="rowSelection"
        @change="pageChange"
      >
        <template #bodyCell="{ column, record, text, index }">
          <template v-if="column.key === 'index'">
            {{ (pagination.current - 1) * pagination.pageSize + index + 1 }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-tooltip title="编辑">
              <a @click="handleEdit(record)">
                <a-icon type="form" style="font-size: 15px" />
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-popconfirm
              placement="topLeft"
              title="确定删除吗?"
              @confirm="() => handleDelete(record.id)"
              style="margin-left: 7px"
            >
              <a>
                <span class="icon iconfont icon-shanchu"></span>
              </a>
            </a-popconfirm>
          </template>
          <template v-else>
            {{ text }}
          </template>
        </template>
      </s-table>
    <InformationManagementModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="informationManagementList">
import { computed, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import InformationManagementModal from './modules/informationManagementModal.vue';

const { createMessage, createConfirm } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);

const url = {
  list: '/promotionalArticle/list',
  delete: '/promotionalArticle/delete',
  deleteBatch: '/promotionalArticle/deleteBatch',
};

const fetchTableApi = async (params) => {
  const res = await defHttp.get({ url: url.list, params });
  return res
};

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  selectedRowKeys,
  rowSelection: baseRowSelection,
  searchQuery: baseSearchQuery,
  searchReset: baseSearchReset,
  pageChange,
} = useTable(fetchTableApi);

queryParam.value = {
  title: undefined,
  url: undefined,
  remark: undefined,
};

const columns = [
  {
    title: '#',
    key: 'index',
    width: 60,
    align: 'center',
  },
  {
    title: '文章标题',
    align: 'center',
    dataIndex: 'title',
    width: 400,
  },
  {
    title: '文章链接',
    align: 'center',
    dataIndex: 'url',
    width: 900,
    ellipsis: true,
  },
  {
    title: '备注',
    align: 'center',
    dataIndex: 'remark',
  },
  {
    title: '操作',
    key: 'action',
    align: 'center',
    width: 120,
  },
];

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: baseRowSelection.onChange,
}));

const searchQuery = () => {
  baseSearchQuery();
};

const searchReset = () => {
  baseSearchReset();
};

const handleAdd = () => {
  modalRef.value?.open();
};

const handleEdit = (record) => {
  modalRef.value?.open(record);
};

const handleDelete = async (id) => {
  try {
    const res = await defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true });
    fetchTable();
    
  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};

const onClearSelected = () => {
  selectedRowKeys.value = [];
};

const batchDel = () => {
  if (!selectedRowKeys.value.length) {
    createMessage.warning('请选择要删除的记录');
    return;
  }
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: `确定删除选中的 ${selectedRowKeys.value.length} 条记录吗？`,
    onOk: async () => {
      try {
        const res = await defHttp.delete({
          url: url.deleteBatch,
          data: { ids: selectedRowKeys.value.join(',') },
        },{ joinParamsToUrl: true });
        fetchTable();
      
      } catch (error) {
        console.error('batchDel error:', error);
        createMessage.error('删除失败');
      }
    },
  });
};

const modalFormOk = () => {
  fetchTable();
};

fetchTable();

</script>

<style scoped>

.table-page-search-submitButtons {
  display: inline-flex;
  align-items: center;
}

.table-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>
