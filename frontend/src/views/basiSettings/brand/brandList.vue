<template>
  <a-card :bordered="false">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.brandName" placeholder="品牌名称" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" style="margin-left: 8px" @click="searchReset">
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
            </span>
          </a-col>
        </a-row>
      </a-form>
 

    <s-table
      :rangeSelection="false"
      ref="tableRef"
      :scroll="{ y: sTableHeight, x: '100%' }"
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.dataIndex === 'logoUrl'">
          <img
            v-if="text"
            :src="text"
            style="width: 50px; height: 50px; border-radius: 100%"
          />
        </template>
        <template v-else-if="['description', 'remark'].includes(column.dataIndex)">
          <EllipsisTooltip :text="text" />
        </template>
       
        <template v-else-if="column.key === 'action'">
          <a-tooltip placement="top" title="编辑">
            <a @click="handleEdit(record)">
              <a-icon type="form" style="font-size: 15px" />
            </a>
          </a-tooltip>
             <a-divider type="vertical" />
          <a-popconfirm
            title="确定删除吗?"
            @confirm="() => handleDelete(record.id)"
            style="margin-left: 7px"
          >
            <a-tooltip placement="top" title="删除">
              <a>
                <span class="icon iconfont icon-shanchu"></span>
              </a>
            </a-tooltip>
          </a-popconfirm>
        </template>
        <template v-else>
          {{ text }}
        </template>
      </template>
    </s-table>

    <BrandModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="brandList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import BrandModal from './modules/brandModal.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);

const url = {
  list: '/kolBrand/list',
  delete: '/kolBrand/delete',
};

const fetchTableApi = async (params) => {
  const res = await defHttp.get({ url: url.list, params });
  return res || [];
};

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
  brandName: undefined,
};

const columns = [
  {
    title: '#',
    key: 'index',
    width: 60,
    align: 'center',
  },
  {
    title: '品牌名称',
    dataIndex: 'brandName',
  },
  {
    title: '品牌描述',
    dataIndex: 'description',
  },
  {
    title: '备注',
    dataIndex: 'remark',
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
  },
];

const searchQuery = () => {
  fetchTable(1);
};

const searchReset = () => {
  queryParam.value = {
    brandName: undefined,
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
  try {
    const res = await defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true });
    if (res) {

      fetchTable();
    } else {
    
    }
  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};

const modalFormOk = () => {
  fetchTable();
};



onMounted(() => {
  fetchTable();
});
</script>

<style scoped>

</style>
