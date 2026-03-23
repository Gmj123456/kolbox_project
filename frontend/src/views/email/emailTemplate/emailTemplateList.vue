<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <a-form  class="searchForm" @keyup.enter="searchQuery">
      <a-row :gutter="12">
        <a-col :xl="4" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-input
              placeholder="邮件主题"
              v-model:value.trim="queryParam.templateTitle"
            ></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="4" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-input-group compact>
              <a-select
                show-search
                option-filter-prop="label"
                allowClear
                placeholder="品牌"
                v-model:value="queryParam.brandId"
                style="width: 120px;border-right: 0px;"
                @change="onBrandChange"
              >
                <a-select-option
                  v-for="item in brandList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.brandName"
                >
                  {{ item.brandName }}
                </a-select-option>
              </a-select>
              <a-select
                show-search
                option-filter-prop="label"
                allowClear
                placeholder="产品"
                style="width: calc(100% - 120px)"
                v-model:value="queryParam.productId"
              >
                <a-select-option
                  v-for="item in brandProductList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.productName"
                >
                  {{ item.productName }}
                </a-select-option>
              </a-select>
            </a-input-group>
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
          <template v-if="column.dataIndex === 'platformType'">
            <div>
              <img
                style="width: 22px; height: 22px"
                src="@/assets/images/yt.png"
                alt=""
                v-if="record.platformType == 1"
              />
              <img
                style="width: 22px; height: 22px"
                src="@/assets/images/tk.png"
                alt=""
                v-if="record.platformType == 2"
              />
              <img
                style="width: 22px; height: 22px"
                src="@/assets/images/ins.png"
                alt=""
                v-if="record.platformType == 0"
              />
            </div>
          </template>
          <template v-else-if="column.dataIndex === 'productName'">
            {{ record.brandName }}-{{ record.productName }}
          </template>
          <template v-else-if="column.dataIndex === 'templateTitle'">
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
    <emailTempalteModal ref="modalFormRef" @ok="modalFormOk"></emailTempalteModal>
  </a-card>
</template>

<script setup name="emailTemplateList">
import { ref, reactive, onMounted, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, FormOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import emailTempalteModal from "./modules/emailTemplateModal.vue";

const { createMessage } = useMessage();

// Refs
const tableRef = ref(null);
const modalFormRef = ref(null);

// Data
const brandList = ref([]);
const brandProductList = ref([]);
const productList = ref([]);

const url = {
  list: "/email/emailTemplate/list",
  delete: "/email/emailTemplate/delete",
  deleteBatch: "/email/emailTemplate/deleteBatch",
  exportXlsUrl: "/email/emailTemplate/exportXls",
  importExcelUrl: "/email/emailTemplate/importExcel",
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

// 初始化查询参数


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
    title: "邮件主题",
    width: 250,
    dataIndex: "templateTitle",
  },
  {
    title: "平台",
    width: 80,
    dataIndex: "platformType",
  },
  {
    title: "所属产品",
    width: 300,
    dataIndex: "productName",
  },
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



async function initBrandList() {
  const res = await defHttp.get({ url: "/kolBrand/listAll" });
  if (res) {
    brandList.value = res || [];
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
  Object.keys(queryParam.value).forEach(key => {
    queryParam.value[key] = undefined;
  });
  brandProductList.value = [];
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


initBrandList();
initProductList();
fetchTable();
</script>
<style scoped>

</style>
