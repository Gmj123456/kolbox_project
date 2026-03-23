<template>
  <a-card :bordered="false" >
    <a-tabs class="kol-tabs" v-model:activeKey="activeTabKey" @change="handleTabChange">
      <a-tab-pane
        v-for="item in categoryTypeList"
        :key="item.id"
        :tab="item.typeName"
      />
    </a-tabs>

    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.attributeName" placeholder="名称" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
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
      @expand="handleExpand"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="['attributeEnName', 'remark'].includes(column.dataIndex)">
          <EllipsisTooltip :text="text" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip placement="top" title="添加子属性">
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
          <a-popconfirm placement="topRight"  title="确定删除吗?"  @confirm="() => handleDelete(record.id)">
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

    <AttributeModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="attributeList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import AttributeModal from './modules/attributeModal.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const categoryTypeList = ref([]);
const activeTabKey = ref();
const expandedRowKeys = ref([]);

const setEmptyChildrenToNull = (nodes) => {
  if (!Array.isArray(nodes)) return nodes;
  return nodes.map(node => {
    if (Array.isArray(node.children)) {
      node.children = setEmptyChildrenToNull(node.children);
      if (node.children.length === 0) {
        node.children = null;
        // delete node.children;
      }
    }
    return node;
  });
};

const loadDataApi = async (params = {}) => {

  const res = await defHttp.get({ url: '/kol/attribute/rootList', params });
  if (res && Array.isArray(res.records)) {
    res.records = setEmptyChildrenToNull(res.records);
  }
  return res || [];
};

const {
  searchQuery,
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
  updateSort
} = useTable(loadDataApi, 60);

queryParam.value = {
  attributeName: undefined,
  attributeTypeId: undefined,
};

const columns = [
  {
    title: '#',
    dataIndex: 'index',
    key: 'index',
    minWidth: 70,
    maxWidth: 120,
    // align: 'center',
  },
  {
    title: '属性名称',
    dataIndex: 'attributeName',
    key: 'attributeName',
    align: 'left',
  },
  {
    title: '英文名称',
    dataIndex: 'attributeEnName',
    key: 'attributeEnName',
    align: 'left',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    align: 'left',
  },
  {
    title: '创建人',
    dataIndex: 'createBy',
    key: 'createBy',
    align: 'left',
  },
  {
    title: '排序',
    dataIndex: 'sortCode',
    key: 'sortCode',
    width: 100,
    align: 'left',
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    align: 'left',
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'action',
    align: 'center',
    width: 160,
  },
];



updateSort({
  column: 'sortCode',
  order: 'asc'
})

const searchReset = async () => {
  queryParam.value.attributeName = undefined;

   fetchTable();
};


const modalFormOk = async () => {
   fetchTable();
};

const getCurrentType = () =>
  categoryTypeList.value.find((item) => item.id === queryParam.value.attributeTypeId);

const handleAdd = () => {
  const current = getCurrentType();
  if (!current) return;
  modalRef.value?.add(queryParam.value.attributeTypeId, current.typeCode);
};

const handleEdit = (record) => {
  const current = getCurrentType();
  if (!current) return;
  modalRef.value?.open(record, current.typeCode);
};

const addSubordinates = (record) => {
  const current = getCurrentType();
  if (!current) return;
  modalRef.value?.addSubordinates(record, queryParam.value.attributeTypeId, current.typeCode);
};

const handleDelete = async (id) => {
  try {
    const relationRes = await defHttp.get({
      url: '/kol/kolTagCategoryRelation/queryByCategoryId',
      params: { categoryId: id },
    });
    const relations = relationRes || [];
    if (Array.isArray(relations) && relations.length > 0) {
      createMessage.error('该属性下有标签，请先处理后再删除');
      return;
    }
    const res =  await defHttp.delete({ url: '/kol/attribute/delete', data: { id } },{ joinParamsToUrl: true, isTransformResponse: false });
    if (res.success) {
      createMessage.success(res.message);
      fetchTable();
    } else {
      createMessage.error(res?.message || '删除失败');
    }
   
  } catch (error) {
    console.error(error);
  }
};


const handleExpand = async (expanded, record) => {
  if (expanded) {
    if (!expandedRowKeys.value.includes(record.id)) {
      expandedRowKeys.value.push(record.id);
    }
  
  } else {
    expandedRowKeys.value = expandedRowKeys.value.filter((key) => key !== record.id);
  }
};

const handleTabChange = async (key) => {
  activeTabKey.value = key;
  queryParam.value.attributeTypeId = key;
  queryParam.value.attributeName = undefined;
  expandedRowKeys.value = [];
  fetchTable(1)
};

const initCategoryType = async () => {
  try {
    const res = await defHttp.get({ url: '/kol/kolAttributeType/listAll' });
    const list = res || [];
    categoryTypeList.value = list;
    if (list.length > 0) {
      activeTabKey.value = list[0].id;
      queryParam.value.attributeTypeId = list[0].id;
      fetchTable(1)
    }
  } catch (error) {
    console.error(error);
  }
};

onMounted(async () => {
  await initCategoryType();
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
