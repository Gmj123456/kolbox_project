<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                v-model:value="queryParam.productName"
                placeholder="产品名称"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
            
              <a-select
                showSearch
                option-filter-prop="label"

                v-model:value="queryParam.brandId"
                placeholder="品牌"
                allowClear
              >
                <a-select-option v-for="item in bandList" :key="item.id" :value="item.id" :label="item.brandName">
                  {{ item.brandName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                v-model:value="queryParam.productUrl"
                placeholder="产品链接"
                allowClear
              />
            </a-form-item>
          </a-col>
<!-- 
          <a-col
            :xl="3"
            :lg="7"
            :md="8"
            :sm="24"
            v-for="item in categoryList"
            :key="item.id"
          >
            <a-form-item>
              <a-tree-select
                show-search
                v-model:value="item.categoryIds"
                treeNodeFilterProp="title"
                :tree-data="item.data"
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                :placeholder="'请选择' + item.name"
                allow-clear
              />
            </a-form-item>
          </a-col> -->
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-tree-select
                v-model:value="queryParam.categoryId"
                show-search
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                placeholder="产品类目"
                allow-clear
                :treeData="productCategoryList"
                :replaceFields="{ label: 'categoryName', key: 'id', value: 'id' }"
               
                 tree-node-filter-prop="categoryName"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
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
            </a-form-item>
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
       :row-height="70"
      :columns="columns"
      :data-source="dataSource"
       v-model:pagination="pagination"
      :loading="loading"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.dataIndex === 'productImage'">
          <a-avatar shape="square" :size="48" :icon="h(PictureOutlined)" :src="text" />
        </template>
        <template v-else-if="column.dataIndex === 'productName'">
          <EllipsisTooltip :text="text" />  
        </template>
        <template v-else-if="column.dataIndex === 'productUrl'">
          <a @click="goProductLink(record)" style="display: flex; align-items: center; margin-right: 20px">
            <EllipsisTooltip :text="text" />
          </a>
        </template>
        <template v-else-if="column.dataIndex === 'categoryName'">
          <EllipsisTooltip :text="text" />
        </template>
        <template v-else-if="isDynamicColumn(column.dataIndex)">
          <!-- {{ column.dataIndex }} -->
          <CategoryCell 
            :type-id="column.dataIndex" 
            :record="record" 
            :type-name="getTypeName(column.dataIndex)"
            :parse-category-text="parseCategoryText"
            :parse-category="parseCategory"
          />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip placement="bottom" title="编辑">
            <a @click="handleEdit(record)">
              <a-icon type="form" style="font-size: 15px" />
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
        <template v-else>
          {{ text }}
        </template>
      </template>
    </s-table>
    <ProductionInformationModal ref="modalRef" @ok="modalFormOk" @update="updateBrand"/>
    <!-- <ImportErrorModal ref="importErrModalRef" @ok="modalFormOk" /> -->
  </a-card>
</template>

<script setup name="productionInformationList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, PictureOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import ProductionInformationModal from './modules/productionInformationModal.vue';
import CategoryCell from '../CategoryAssociation/modules/CategoryCell.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);

const bandList = ref([]);
const categoryTypeList = ref([]);
const categoryList = ref([]);
const productCategoryList = ref([]);

const url = {
  list: '/kol/kolProduct/list',
  delete: '/kol/kolProduct/delete',
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
  pageChange,
  fetchTable,
} = useTable(fetchTableApi);

queryParam.value = {
  productName: undefined,
  brandId: undefined,
  productUrl: undefined,
  categoryId: undefined,
};

const columns = ref( [
  {
    title: '序号',
    key: 'index',
   
    width: 50,
    align: 'center',
  },
  {
    title: '产品图片',
   
   
    dataIndex: 'productImage',
    width: 70,
  },
  {
    title: '产品名称',
   
   
    dataIndex: 'productName',

  },
  {
    title: '品牌',
   
   

    dataIndex: 'brandName',
  },
  {
    title: '产品链接',
   
   
 
    dataIndex: 'productUrl',
  },
  {
    title: '产品类目',
   
   
  
    dataIndex: 'categoryName',
  },
  {
    title: '操作',
    key: 'action',
    align: 'center',
    fixed: 'right',
    width: 100,
  }
])

// const columns = ref([...baseColumns, actionColumn]);

const isDynamicColumn = (dataIndex) => {


  return categoryTypeList.value.some((item) => item.id == dataIndex);
};

const getTypeName = (typeId) => {

  const type = categoryTypeList.value.find((item) => item.id === typeId);
  return type?.typeName || '';
};


const updateBrand = () => {
  initBrandList();
};
// 添加解析函数
const parseCategoryText = (typeId, record) => {
  // 根据组件的实现，这里应该从record中提取相应的分类文本
  const dataList = record.dataList || [];
  const categoryData = dataList.find(item => item.typeId === typeId);
  if (categoryData && categoryData.list) {
    return categoryData.list.map(item => item.attributeName).join(',');
  }
  return '';
};

const parseCategory = (typeId, record) => {
  // 根据组件的实现，这里应该从record中提取分类数据
  const dataList = record.dataList || [];
  const categoryData = dataList.find(item => item.typeId === typeId);
  if (categoryData && categoryData.list) {
    // 按照level分组
    const groups = {};
    categoryData.list.forEach(item => {
      if (!groups[item.level]) {
        groups[item.level] = {
          level: item.level,
          list: []
        };
      }
      groups[item.level].list.push(item);
    });
    return Object.values(groups);
  }
  return [];
};


const processTreeData = (treeNodes) => {
  const processNode = (node) => {
    const newNode = {
      title: node.categoryName,
      key: node.id,
      value: node.id,
      disabled: node.children && node.children.length > 0,
    };

    if (node.children && node.children.length > 0) {
      newNode.children = node.children.map((child) => processNode(child));
    }

    return newNode;
  };

  return treeNodes.map((node) => processNode(node));
};

const filterProdctCategory = (id) => {
  const findParentChain = (tree, targetId) => {
    for (let node of tree) {
      if (node.id === targetId) {
        return [node];
      }
      if (node.children && node.children.length > 0) {
        const result = findParentChain(node.children, targetId);
        if (result) {
          return [node, ...result];
        }
      }
    }
    return null;
  };

  const nodeChain = findParentChain(productCategoryList.value, id) || [];
  return nodeChain.map((node) => node.id);
};

const goProductLink = (record) => {
  if (record.productUrl) {
    window.open(record.productUrl, '_blank');
  } else {
    console.warn('产品链接为空');
  }
};

const initBrandList = async () => {
  const res = await defHttp.get({ url: '/kolBrand/listAll' });
  if (res) {
    bandList.value = res || [];
  }
};

const initProductCategory = async () => {
  const res = await defHttp.get({ url: '/kol/category/loadTreeDataAll' });
  if (res) {
    productCategoryList.value = res || [];
  }
};

const getAllTagCategor = async () => {
  const promises = categoryTypeList.value.map((item) =>
    defHttp.get({ url: '/kol/category/loadTreeDataAll', params: { typeCode: item.typeCode } })
  );

  try {
    const results = await Promise.all(promises);
    return results
      .map((res, index) => {
        if (res) {
          return {
            name: categoryTypeList.value[index].typeName,
            id: categoryTypeList.value[index].id,
            categoryIds: [],
            data: processTreeData(res || []),
          };
        }
        return null;
      })
      .filter((item) => item !== null);
  } catch (error) {
    console.error('获取标签类别数据失败:', error);
    createMessage.error('获取标签类别数据失败');
    return [];
  }
};

const initCatoryTypeList = async () => {
  const res = await defHttp.get({ url: '/kol/kolAttributeType/listAll' });
  if (res) {
    categoryTypeList.value = res.filter((item) => item.type == 2);
    await initProductCategory();
    
    const dynamicColumns = categoryTypeList.value.map((item) => ({
      title: item.typeName,
      width: 350,
      key: String(item.id),
      dataIndex: String(item.id),
    }));
    
    const actionColumnIndex = columns.value.findIndex((col) => col.key === 'action');
    // if (actionColumnIndex !== -1) {
    //   columns.value.splice(actionColumnIndex, 0, ...dynamicColumns);
    // } else {
    //   columns.value.push(...dynamicColumns);
    // }
    columns.value = [...columns.value, ...dynamicColumns];

    console.log(columns.value)
    // 初始化 categoryList
    categoryList.value = await getAllTagCategor();
    
    // 确保在添加列后获取数据
    fetchTable(1);
  }
};


const searchQuery = () => {
  // const params = getQueryParams();
  fetchTable(1);
};

const searchReset = () => {
  queryParam.value = {
    productName: undefined,
    brandId: undefined,
    productUrl: undefined,
    categoryId: undefined,
  };
  categoryList.value.forEach((item) => {
    item.categoryIds = [];
  });
  fetchTable(1);
};

const handleAdd = () => {
  modalRef.value?.add();
};

const handleEdit = (record) => {
  const productCategory = filterProdctCategory(record.categoryId);
  const newRecord = {
    ...record,
    categoryIds: productCategory,
  };
  modalRef.value?.open(newRecord);
};

const handleDelete = async (id) => {
  try {
    await defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true });
    fetchTable();
    
  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};

const modalFormOk = () => {
  fetchTable();
};


initBrandList();

onMounted(() => {
  initCatoryTypeList();
  // initBrandList();
  // initProductCategory();
});
</script>

<style scoped lang="less">

</style>
