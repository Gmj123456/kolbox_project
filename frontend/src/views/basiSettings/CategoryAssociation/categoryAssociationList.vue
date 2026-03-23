<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-tree-select
                v-model:value="queryParam.categoryId"
                show-search
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                placeholder="产品类目"
                 :treeData="productCategoryList"
                :replaceFields="{ label: 'categoryName', key: 'id', value: 'id' }"
         
                 tree-node-filter-prop="categoryName"
              />
            </a-form-item>
          </a-col> 
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                v-model:value="queryParam.subAttributeName"
                placeholder="社媒属性"
              />
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
                匹配
              </a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <s-table
        :rangeSelection="false"
       
        :defaultExpandedRowKeys="defaultExpandedRowKeys"
        :scroll="{ y: sTableHeight, x: '100%' }"
        ref="tableRef"
        size="small"
        :rowKey="(record) => record.id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="pagination"
        :loading="loading"
        @change="pageChange"
      >
      <template #bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'categoryName'">
          {{ text }}
        </template>   
         <template v-else-if="column.key === 'action'">
          <a-tooltip title="编辑">
            <a @click="handleEdit(record)">
              <a-icon type="form" style="font-size: 15px" />
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-popconfirm
            placement="leftTop"
           
            title="确定要删除关联关系吗?"
            @confirm="() => handleDelete(record.id)"
          >
            <a>
              <span class="icon iconfont icon-shanchu"></span>
            </a>
          </a-popconfirm>
        </template>
        <template
          v-else-if="column.dataIndex && dynamicColumnMap[String(column.dataIndex)]"
        >
          <CategoryCell
            :type-id="column.dataIndex"
            :type-name="dynamicColumnMap[String(column.dataIndex)]"
            :record="record"
            :parse-category-text="parseCategoryText"
            :parse-category="parseCategory"
          />
        </template>
      </template>
    </s-table>

  


    <CategoryAssociationModal ref="modalRef" @update="update" @ok="modalFormOk" />
    <MatchCategoryAssociationModal
      ref="matchModalRef"
      @ok="modalFormOk"
    />
  </a-card>
</template>

<script setup name="categoryAssociationList">
import { onMounted, ref, h,nextTick } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import CategoryAssociationModal from './modules/categoryAssociationModal.vue';
import MatchCategoryAssociationModal from './modules/matchCategoryAssociationModal.vue';
import CategoryCell from './modules/CategoryCell.vue';


const { createMessage } = useMessage();
const modalRef = ref(null);
const matchModalRef = ref(null);
const tableRef = ref(null);
// 预留的表格引用（如有需要可使用）
// const tableRef2 = ref(null);
const categoryTypeList = ref([]);
const productCategoryList = ref([]);
const dynamicColumnMap = ref({});
const defaultExpandedRowKeys = ref([]);
const url = {
  list: '/kol/kolCategoryRelation/list',
  delete: '/kol/kolCategoryRelation/delete',
};

const fetchTableApi = async (params) => {
  const res = await defHttp.get({ url: url.list, params });
  return res
};

// 处理树形 children 为空数组为 null
function processTreeData(nodes) {
  if (!Array.isArray(nodes)) return;
  nodes.forEach((item) => {
    if (item.children && item.children.length === 0) {
      item.children = null;
    } else if (item.children && item.children.length > 0) {
      defaultExpandedRowKeys.value.push(item.id);
      processTreeData(item.children);
    }
  });
  
}

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
  searchQuery,
  searchReset,
  calcTableHeight
} = useTable(fetchTableApi, 0, {
  afterFetch: async (params, res) => {
    CategoryAssociationAfterFetch(res);
    processTreeData(dataSource.value);
  },
});

function CategoryAssociationAfterFetch(res) {
  console.log(res);
  nextTick(()=>{
    calcTableHeight()
  })
}
queryParam.value = {
  categoryId: undefined,
  subAttributeName: undefined,
};

const columns = ref([
  {
    title: '产品类目',
    width: 200,
    dataIndex: 'categoryName',
    key: 'categoryName',
    autoHeight:true,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    align: 'center',
    fixed: 'right',
  },
])




// 解析类目路径（如有需要可使用）目前未使用，保留函数方便后续扩展
// const parseCategoryNamePath = (categoryNamePath) => {
//   if (categoryNamePath) {
//     return categoryNamePath.replaceAll('|', '-');
//   }
//   return '';
// };

const parseCategoryText = (id, row) => {
  const filterList = row.dataList?.filter((item) => item.typeId == id) || [];
  if (filterList.length > 0) {
    return filterList
      .map((item) => item.list?.map((category) => category.attributeName).join(', ') || '')
      .join(', ');
  }
  return '';
};

const groupByLevel = (data) => {
  const allItems = data.flatMap((item) => item.list || []);
  const grouped = allItems.reduce((acc, item) => {
    const { level, attributeId } = item;
    let group = acc.find((g) => g.level === level);
    if (!group) {
      group = { level, list: [] };
      acc.push(group);
    }
    const seen = new Set(group.list.map((i) => i.attributeId));
    if (!seen.has(attributeId)) {
      group.list.push(item);
    }
    return acc;
  }, []);
  return grouped;
};

const parseCategory = (id, row) => {
  const filterList = row.dataList?.filter((item) => item.typeId == id) || [];
  if (filterList.length > 0) {
    return groupByLevel(filterList);
  }
  return [];
};

const initProductCategory = async () => {
  try {
    const res = await defHttp.get({ url: '/kol/category/loadTreeDataAll' });
    if (res) {
      productCategoryList.value = res || [];
    }
  } catch (error) {
    console.error('initProductCategory error:', error);
  }
};

const initCatoryTypeList = async () => {
  try {
    const res = await defHttp.get({ url: '/kol/kolAttributeType/listAll' });
    if (res) {
      categoryTypeList.value = res.filter((item) => item.type == 2) || [];
      await initProductCategory();
      
      const map = {};
      const dynamicColumns = categoryTypeList.value.map((item) => {
        map[item.id] = item.typeName;
        return {
          autoHeight:true,
          title: item.typeName,
          width: 350,
          dataIndex: item.id,
        };
      });
      
      dynamicColumnMap.value = map;
      
      const actionColumnIndex = columns.value.findIndex((col) => col.key === 'action');
      if (actionColumnIndex !== -1) {
        columns.value.splice(actionColumnIndex, 0, ...dynamicColumns);
      } else {
        columns.value.push(...dynamicColumns);
      }
      columns.value = [...columns.value]
      // 初始化加载表格数据
      fetchTable(1);
    }
  } catch (error) {
    console.error('initCatoryTypeList error:', error);
  }
};

const update = (id) => {
  getEditRowData(id);
};

const getEditRowData = async (id) => {
  try {
    const res = await defHttp.get({
      url: '/kol/kolCategoryRelation/list',
      params: { categoryId: id },
    });
    const records = res?.records || [];
    if (records.length > 0) {
      const updateTree = (nodes) => {
        return nodes.map((node) => {
          if (node.id == records[0].id) {
            return { ...node, ...records[0] };
          }
          if (node.children && node.children.length > 0) {
            return {
              ...node,
              children: updateTree(node.children),
            };
          }
          return node;
        });
      };
      dataSource.value = updateTree(dataSource.value);
    }
  } catch (error) {
    console.error('getEditRowData error:', error);
  }
};

const handleAdd = () => {
  matchModalRef.value?.add();
};

const handleEdit = (record) => {
  // modalRef.value?.title = "编辑匹配"
  modalRef.value?.edit(record)


};

const handleDelete = async (id) => {
  if (!url.delete) {
    createMessage.error('请设置url.delete属性!');
    return;
  }
  try {
    await defHttp.delete({
      url: url.delete,
      data: { categoryId: id },
    },{ joinParamsToUrl: true });
    // 删除后刷新当前页
    fetchTable();
  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};

const modalFormOk = () => {
  fetchTable();
};


onMounted(() => {
  initCatoryTypeList();
});
</script>

<style scoped lang="less">
.table-page-search-submitButtons {
  display: inline-flex;
  align-items: center;
}

.table-pagination {
  margin-top: 16px;
  text-align: right;
}

.category-list {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
}

.level-high {
  width: 22px;
  border-radius: 4px;
  height: 16px;
  background: rgba(255, 232, 232, 1);
  color: rgba(255, 0, 0, 1);
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.level-small {
  margin-right: 10px;
  width: 22px;
  border-radius: 4px;
  height: 16px;
  background: rgba(255, 232, 209, 1);
  color: rgba(255, 128, 0, 1);
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.ant-table-tbody tr td) {
  padding: 10px 2px !important;
}
</style>
