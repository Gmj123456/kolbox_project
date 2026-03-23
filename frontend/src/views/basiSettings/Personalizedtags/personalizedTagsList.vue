<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                v-model:value="queryParam.tagName"
                placeholder="个性化标签名称"
                @blur="queryParam.tagName = queryParam.tagName?.trim()"
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
                新增
              </a-button>
              <a-button
                type="primary"
                :icon="h(SyncOutlined)"
                style="margin-left: 8px"
                @click="syncData"
                v-if="hasPermission('user:import')"
              >
                同步个性化标签
              </a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <s-table
      ref="tableRef"
      :scroll="{ y: sTableHeight, x: '100%' }"
      size="small"
      rowKey="id"
        :rangeSelection="false"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'remark'">
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
            placement="topLeft"
            @confirm="() => handleDelete(record.id)"
            style="margin-left: 7px"
          >
            <template #title>
              <div>确定删除吗?</div>
              <div>将会同步删除私有网红身上的个性化标签</div>
            </template>
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



    <PersonalizedTagsModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="personalizedTagsList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, SyncOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import PersonalizedTagsModal from './modules/personalizedTagsModal.vue';
import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);

const url = {
  list: '/personalTags/list',
  delete: '/personalTags/deleteBatch',
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
  isorter,
  pageChange,
} = useTable(fetchTableApi);

// 设置默认排序
isorter.value = {
  column: 'sortCode',
  order: 'asc',
};

queryParam.value = {
  tagName: undefined,
};

const columns = [
  {
    title: '#',
    dataIndex: 'sortCode',
    key: 'sortCode',
    width: 80,
    align: 'center',
  },
  {
    title: '个性化标签',
    dataIndex: 'tagName',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '创建人',
    dataIndex: 'createBy',
  },
  {
    title: '备注',
    dataIndex: 'remark',
  },
  {
    title: '操作',
    key: 'action',
    align: 'center',
    width: 120,
  },
];

const searchQuery = () => {
  fetchTable(1);
};

const searchReset = () => {
  queryParam.value = {
    tagName: undefined,
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
    const res = await defHttp.delete({ url: url.delete, data: { ids: id } },{ joinParamsToUrl: true,isTransformResponse:false });
    if (res.success) {
      createMessage.success('删除成功');
      fetchTable();
    } else {
      createMessage.error(res?.message || '删除失败');
    }

  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};

const syncData = async () => {
  try {
    const res = await defHttp.get({ url: '/personalTags/synchronizeData' });
    fetchTable();

  } catch (error) {
    console.error('syncData error:', error);
    createMessage.error('同步失败');
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
