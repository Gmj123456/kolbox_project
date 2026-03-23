<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.typeCode" placeholder="类型编码" />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.typeName" placeholder="类型名称" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
              <a-button
                :icon="h(ReloadOutlined)"
                style="margin-left: 8px"
                @click="resetQuery"
              >
                重置
              </a-button>
              <!-- <a-button
                type="primary"
                :icon="h(PlusOutlined)"
                style="margin-left: 8px"
                @click="handleAdd"
              >
                新增
              </a-button> -->
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <s-table
      :rangeSelection="false"
      :scroll="{ y: sTableHeight, x: '100%' }"
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'action'">
          <!-- <a-tooltip placement="top" title="编辑">
       
            <a @click="handleEdit(record)" :disabled="isActionDisabled(record.typeCode)">
              <a-icon type="form" style="font-size: 15px" />
            </a>
          </a-tooltip> -->
          <!-- <a-divider type="vertical" />
          <a-popconfirm
            placement="topLeft"
            title="确定删除吗?"
            :disabled="isActionDisabled(record.typeCode)"
            @confirm="() => handleDelete(record.id)"
          >
            <a :disabled="isActionDisabled(record.typeCode)">
              <span class="icon iconfont icon-shanchu"></span>
            </a>
          </a-popconfirm> -->
        </template>
      </template>
    </s-table>


    <AttributeTypeModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="attributeTypeList">
import { onMounted, onUnmounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import { getDictItems } from '@/api/common/api';
import AttributeTypeModal from './modules/attributeTypeModal.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const attributeTypeList = ref([]);

const fetchTableApi = (params) =>
  defHttp.get({ url: '/kol/kolAttributeType/list', params });

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
} = useTable(fetchTableApi);

queryParam.value = {
  typeCode: undefined,
  typeName: undefined,
};

const columns = [
  {
    title: '#',
    dataIndex: 'index',
    key: 'index',
    width: 60,
    align: 'center',
  },
  {
    title: '类型编码',
    dataIndex: 'typeCode',
    key: 'typeCode',
  },
  {
    title: '类型名称',
    dataIndex: 'typeName',
    key: 'typeName',
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
  },
  // {
  //   title: '操作',
  //   dataIndex: 'action',
  //   key: 'action',
  //   width: 120,
  //   align: 'center',
  // },
];


const initTypeDict = async () => {
  const attributeTypeRes = await getDictItems('attr_type');
  attributeTypeList.value = attributeTypeRes || [];
};

const searchQuery = () => fetchTable(1);

const resetQuery = () => {
  queryParam.value = {
    typeCode: undefined,
    typeName: undefined,
  };
  fetchTable(1);
};

const handleAdd = () => {
  modalRef.value?.open();
};

const handleEdit = (record) => {
  modalRef.value?.open(record);
};

const handleDelete = async (id) => {
  await defHttp.delete({
    url: '/kol/kolAttributeType/delete',
    data: { id },
  },{ joinParamsToUrl: true });
  createMessage.success('删除成功');
  fetchTable();
};

const isActionDisabled = (typeCode) =>
  attributeTypeList.value.some((item) => item.value == typeCode);

const modalFormOk = () => {
  fetchTable();
};

onMounted(() => {
 
  initTypeDict();
  fetchTable();
 
});

onUnmounted(() => {
  
});
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
