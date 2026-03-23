<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <a-form  class="searchForm" @keyup.enter="searchQuery">
      <a-row :gutter="12">
        <a-col :xl="4" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-select
              placeholder="品牌类目"
              v-model:value.trim="queryParam.brandCategoryId"
            >
              <a-select-option v-for="item in brandCategoryList" :key="item.id" :value="item.id" :label="item.categoryName">
                {{ item.categoryName }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <!-- <a-col :xl="4" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-input
              placeholder="邮件主题"
              v-model:value.trim="queryParam.templateTitle"
            ></a-input>
          </a-form-item>
        </a-col> -->
   
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

          <template v-if="column.dataIndex === 'templateTitle'">
            <span style="display: inline-block; width: 230px">{{ text || "--" }}</span>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
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
    <businessEmailTemplateModal ref="modalFormRef" @ok="modalFormOk"></businessEmailTemplateModal>
  </a-card>
</template>

<script setup name="emailTemplate">
import { ref, reactive, onMounted, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, FormOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import businessEmailTemplateModal from "./modules/businessEmailTemplateModal.vue";

const { createMessage } = useMessage();

// Refs
const tableRef = ref(null);
const modalFormRef = ref(null);
const expandedRowKeys = ref([]);
// Data
const brandCategoryList = ref([]);

const url = {
  list: "/email/emailTemplateBusiness/list",
  delete: "/email/emailTemplateBusiness/delete",
  deleteBatch: "/email/emailTemplateBusiness/deleteBatch",
  exportXlsUrl: "/email/emailTemplateBusiness/exportXls",
  importExcelUrl: "/email/emailTemplateBusiness/importExcel",
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
    title: "品牌类目",
    width: 250,
    autoHeight: true,
    dataIndex: "brandCategory",
  },
  // {
  //   title: "邮件主题",
  //   width: 250,
  //   autoHeight: true,
  //   dataIndex: "templateTitle",
  // },
  {
    title: "模板内容",
    dataIndex: "templateContent",
    autoHeight: true,
  },
  {
    title: "操作",
    dataIndex: "action",
    width: 120,
    align: "center",
  },
];


  
async function initBrandCategoryList() {
  const res = await defHttp.get({ url: "/email/emailTemplateCategory/listAll", params: { templateType:0} });
  if (res) {
    brandCategoryList.value = res || [];
  }
}

async function initProductList() {
  const res = await defHttp.get({ url: "/kol/kolProduct/listAll" });
  if (res) {
    productList.value = res || [];
  }
}

function onBrandChange(value) {
  brandProductList.value = [];
  brandProductList.value = productList.value.filter((item) => item.brandId == value);

  if (brandProductList.value.length == 1) {
    queryParam.value.productId = brandProductList.value[0].id;
  } else {
    queryParam.value.productId = undefined;
  }
}

function searchQuery() {
  fetchTable();
}

function searchReset() {
  queryParam.value =  {}
  queryParam.value.templateType = 0
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


queryParam.value.templateType = 0;
initBrandCategoryList();
fetchTable();
</script>
<style scoped>

</style>
