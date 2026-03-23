<template>
  <a-card :bordered="false">
    <!-- 搜索区域 -->
    <div >
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.name" placeholder="姓名" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item >
              <a-input v-model:value="queryParam.nameEn" placeholder="英文名" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item >
              <a-input v-model:value="queryParam.nameWechat" placeholder="微信名" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item >
              <a-input v-model:value="queryParam.phone" placeholder="手机号" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.email" placeholder="邮箱" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" style="margin: 0 8px" @click="searchReset">重置</a-button>
              <a-button type="primary" :icon="h(PlusOutlined)" @click="handleAdd">新增</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 表格区域 -->
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
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip>
            <template #title>编辑</template>
            <a @click="handleEdit(record)"><FormOutlined  style="font-size: 15px" /></a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>
              <span class="icon iconfont icon-shanchu"></span>
            </a>
          </a-popconfirm>
        </template>
      </template>
    </s-table>

    <!-- 弹窗 -->
    <StoreEmployeeSecurityModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="employeeAnti-CounterfeitingList">
import { h, onMounted, ref } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined,FormOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import StoreEmployeeSecurityModal from './modules/StoreEmployeeSecurityModal.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);

const url = {
  list: '/employeeSecurity/list',
  delete: '/employeeSecurity/delete',
};

const fetchTableApi = async (params) => {
  const res = await defHttp.get({ url: url.list, params });
  return res;
};

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
} = useTable(fetchTableApi);

queryParam.value = {
  name: undefined,
  nameEn: undefined,
  nameWechat: undefined,
  phone: undefined,
  email: undefined,
};

const columns = [
  {
    title: '序号',
    key: 'index',
    width: 60,
    align: 'center',
    customRender: ({ index }) => index + 1,
  },
  {
    title: '姓名',
    dataIndex: 'name',
    align: 'center',
  },
  {
    title: '英文名',
    dataIndex: 'nameEn',
    align: 'center',
  },
  {
    title: '微信名',
    dataIndex: 'nameWechat',
    align: 'center',
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    align: 'center',
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    align: 'center',
  },
  {
    title: '微信号',
    dataIndex: 'wechatNum',
    align: 'center',
  },
  {
    title: '职务',
    dataIndex: 'job',
    align: 'center',
  },
  {
    title: '操作',
    key: 'action',
    align: 'center',
    width: 140,
    fixed: 'right',
  },
];

const handleAdd = () => {
  modalRef.value?.add();
};

const handleEdit = (record) => {
  modalRef.value?.edit(record);
};

const handleDelete = async (id) => {
  try {
  const res = await defHttp.delete({ url: url.delete, data: { id } }, { joinParamsToUrl: true,isTransformResponse:false });
    if (res.success) {
      createMessage.success(res.message);
      fetchTable();
    } else {
      createMessage.error(res.message);
    }
  } catch (error) {
    console.error('handleDelete error:', error);
  }
};

const modalFormOk = () => {
  fetchTable();
};

onMounted(() => {
  fetchTable();
});
</script>

<style scoped lang="less">
.table-page-search-wrapper {
  margin-bottom: 16px;
}
</style>