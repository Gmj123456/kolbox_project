<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.categoryName" placeholder="名称" />
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
    </div>

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
      :expandedRowKeys="expandedRowKeys"
      @expand="handleExpand"
    >
      <template #bodyCell="{ column, record, text }">
        <template v-if="['categoryEnName', 'remark'].includes(column.dataIndex)">
          <EllipsisTooltip :text="text" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip placement="top" title="添加子类目">
            <a @click="addSubordinates(record)">
              <a-icon type="plus" style="font-size: 15px" />
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-tooltip placement="top" title="编辑">
            <a @click="handleEdit(record)">
              <a-icon type="form" style="font-size: 15px" />
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
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


    <ProductCategoryModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="productCategoryList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import ProductCategoryModal from './modules/productCategoryModal.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);
const expandedRowKeys = ref([]);

const url = {
  list: '/kol/category/rootList',
  delete: '/kol/category/delete',
  childList: '/kol/category/childList',
};

const setEmptyChildrenToNull = (nodes) => {
  if (!Array.isArray(nodes)) return nodes;
  return nodes.map(node => {
    if (Array.isArray(node.children)) {
      node.children = setEmptyChildrenToNull(node.children);
      if (node.children.length === 0) {
        node.children = null;
      }
    }
    return node;
  });
};

const fetchTableApi = async (params) => {
  const res = await defHttp.get({ url: url.list, params });
  if (res && Array.isArray(res.records)) {
    res.records = setEmptyChildrenToNull(res.records);
  }
  return res || [];
};

const {
  filterObj,
  isorter,
  unref,
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
} = useTable(fetchTableApi)

queryParam.value = {
  categoryName: undefined,
  categoryEnName: undefined,
};
 
const columns = [
  {
    title: '类目名称',
    align: 'left',
    dataIndex: 'categoryName',
  },
  {
    title: '英文名称',
    align: 'left',
    dataIndex: 'categoryEnName',
  },
  {
    title: '创建时间',
    align: 'left',
    dataIndex: 'createTime',
  },
  {
    title: '创建人',
    align: 'left',
    dataIndex: 'createBy',
  },
  {
    title: '排序',
    align: 'left',
    width: 100,
    dataIndex: 'sortCode',
  },
  {
    title: '备注',
    align: 'left',
    dataIndex: 'remark',
  },
  {
    title: '操作',
    key: 'action',
    align: 'center',
  },
];






const handleExpand = async (expanded, record) => {
  if (expanded) {
    expandedRowKeys.value.push(record.id);
   
  } else {
    const keyIndex = expandedRowKeys.value.indexOf(record.id);
    if (keyIndex >= 0) {
      expandedRowKeys.value.splice(keyIndex, 1);
    }
  }
};

const searchQuery = () => {

  fetchTable()
};

const searchReset = () => {
  queryParam.value = {
    categoryName: undefined,
    categoryEnName: undefined,
  };
  fetchTable()
};

const handleAdd = () => {
  modalRef.value?.open();
};

const handleEdit = (record) => {
  modalRef.value?.open(record);
};

const handleDelete = async (id) => {
  try {
    const res = await defHttp.get({
      url: '/kol/kolTagCategoryRelation/queryByCategoryId',
      params: { categoryId: id },
    });
    if (res) {
      if (res && res.length > 0) {
        createMessage.error('该类目下有标签，请先把标签禁用或者移动到其他类目下');
      } else {
       await defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true});
        fetchTable();
      
      }
    }
  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};

const addSubordinates = async (record) => {
  try {
    const res = await defHttp.get({
      url: '/kol/kolTagCategoryRelation/queryByCategoryId',
      params: { categoryId: record.id },
    });
    if (res) {
      if (res && res.length > 0) {
        createMessage.error('该类目下有标签，请先把标签禁用或者移动到其他类目下');
      } else {
        modalRef.value?.addSubordinates(record);
      }
    }
  } catch (error) {
    console.error('addSubordinates error:', error);
  }
};

const modalFormOk = () => {
  fetchTable();
};
fetchTable()
onMounted(() => {

});
</script>

<style scoped>

</style>
