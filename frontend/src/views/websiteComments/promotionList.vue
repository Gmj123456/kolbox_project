<template>
  <a-card :bordered="false">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.name" placeholder="名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.companyName" placeholder="公司名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.phone" placeholder="联系电话"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.email" placeholder="邮箱"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" style="margin-left: 8px" @click="searchReset">
                重置
              </a-button>
            
            </a-form-item>
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
        
        <EllipsisTooltip :text="text"></EllipsisTooltip>
      </template>
    </s-table>

    <BrandModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="promotionList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);

const url = {
  list: "/promotion/request/list",

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

const columns =  [
    {
      title: "#",
      dataIndex: "",
      key: "rowIndex",
      width: 60,
      align: "center",
      customRender: function ({index}) {
        return parseInt(index) + 1;
      },
    },
    {
      title: "名称",
      dataIndex: "name",

    },
    {
      title: "公司名称",
      dataIndex: "companyName",

    },
    {
      title: "联系电话",
      dataIndex: "phone",

    },
    {
      title: "邮箱",
      dataIndex: "email",

    },
    {
      title: "职位",
      dataIndex: "position",

    },
    {
      title: "所属行业",
      dataIndex: "industry",
     
    },
    {
      title: "推广地区",
      dataIndex: "promotionArea",

    },
    {
      title: "推广预算",
      dataIndex: "promotionBudget",
 
    },
    {
      title: "需求说明",
      dataIndex: "demandMessage",
   
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
fetchTable();

</script>

<style scoped>

</style>
