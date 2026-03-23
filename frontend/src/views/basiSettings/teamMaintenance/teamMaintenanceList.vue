<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.groupName" placeholder="小组名称" />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
            
                show-search
                option-filter-prop="label"
                v-model:value="queryParam.leaderUserId"
                allowClear
                placeholder="组长"
              >
                <a-select-option
                  v-for="(user, key) in celebrityConsultantList"
                  :key="key"
                  :value="user.id"
                  :label="user.username"
                >
                  {{ user.username }}
                </a-select-option>
              </a-select>
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
        <template v-else-if="column.key === 'memberList'">
          <a-tooltip v-if="record.memberList?.length" :title="getMemberNames(record.memberList)">
            {{ getMemberNames(record.memberList) }}
          </a-tooltip>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'success' : 'default'">
            {{ record.status === 1 ? '开启' : '禁用' }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip placement="top" title="编辑">
            <a @click="handleEdit(record)">
              <span class="icon iconfont icon-bianji"></span>
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-tooltip placement="top" title="添加组员">
            <a @click="handleAddMembers(record)">
              <span class="icon iconfont icon-tianjia"></span>
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-popconfirm placement="topLeft" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>
              <span class="icon iconfont icon-shanchu"></span>
            </a>
          </a-popconfirm>
        </template>
      </template>
    </s-table>

    <TeamMaintenanceModal ref="modalRef" @ok="modalFormOk" />
    <AddMembersModal ref="addMembersModalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="teamMaintenanceList">
import { onMounted, onUnmounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import { queryAllCelebrityCounselorApi } from '/@/api/sys/systemCommon';
import TeamMaintenanceModal from './modules/teamMaintenanceModal.vue';
import AddMembersModal from './modules/addMembersModal.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const addMembersModalRef = ref(null);
const celebrityConsultantList = ref([]);
const fetchTableApi = (params) =>
  defHttp.get({ url: '/counselor/celebrityCounselorGroup/list', params });

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
  groupName: undefined,
  leaderUserId: undefined,
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
    title: '小组名称',
    dataIndex: 'groupName',
    key: 'groupName',
  },
  {
    title: '组长',
    dataIndex: 'leaderUserName',
    key: 'leaderUserName',
  },
  {
    title: '组员',
    dataIndex: 'memberList',
    key: 'memberList',
    ellipsis: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'action',
    width: 200,
    align: 'center',
  },
];


async function queryAllCelebrityCounselor() {
    const res = await queryAllCelebrityCounselorApi({});
    celebrityConsultantList.value = Array.isArray(res)
      ? res
      : [];
  }
const getMemberNames = (memberList) => {
  if (!Array.isArray(memberList) || !memberList.length) return '--';
  return memberList.map((m) => m.memberUserName).filter(Boolean).join('、');
};

const searchQuery = () => fetchTable(1);

const resetQuery = () => {
  queryParam.value = {
    groupName: undefined,
    leaderUserId: undefined,
  };
  fetchTable(1);
};

const handleAdd = () => {
  modalRef.value?.open();
};

const handleEdit = (record) => {
  modalRef.value?.open(record);
};

const handleAddMembers = (record) => {
  addMembersModalRef.value?.open(record);
};

const handleDelete = async (id) => {
  await defHttp.delete({
    url: '/counselor/celebrityCounselorGroup/delete',
    data: { id },
  }, { joinParamsToUrl: true });

  fetchTable();
};

const modalFormOk = () => {
  fetchTable();
};

onMounted(() => {
 

  fetchTable();
  queryAllCelebrityCounselor()
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
