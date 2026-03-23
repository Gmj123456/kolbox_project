<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                show-search
                option-filter-prop="label"
                v-model:value="queryParam.sysUserId"
                allowClear
                placeholder="用户名称"
              >
                <a-select-option
                  v-for="(user, key) in consultantList"
                  :key="key"
                  :value="user.id"
                  :label="user.username"
                >
                  {{ user.username }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                show-search
                option-filter-prop="label"
                v-model:value="queryParam.spreadSheetType"
                allowClear
                placeholder="表格类型"

              >
                <a-select-option
                  v-for="(type, key) in feiSshuTypeList"
                  :key="key"
                  :value="type.value"
                  :label="type.title"
                >
                  {{ type.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <span>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery" style="margin-left: 8px">
                查询
              </a-button>
              <a-button :icon="h(ReloadOutlined)" style="margin-left: 8px" @click="searchReset">重置</a-button>
              <a-button
                type="primary"
                :icon="h(PlusOutlined)"
                @click="handleAdd"
                style="margin-left: 8px"
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
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.dataIndex === 'spreadSheetType'">
          {{ parseDict(text) }}
        </template>
        <template v-else-if="column.dataIndex === 'spreadSheetUrl'">
          <a :href="text" target="_blank">
            <EllipsisTooltip :text="text" />
          </a>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip title="编辑">
            <a :disabled="record.createBy == 'system'" @click="handleEdit(record)">
              <a-icon style="font-size: 15px" type="form" />
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-popconfirm
            :disabled="record.createBy == 'system'"
            placement="topLeft"
            title="确定删除吗?"
            @confirm="() => handleDelete(record.id)"
          >
            <a-tooltip title="删除">
              <a :disabled="record.createBy == 'system'">
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


    <ConsultantFeiShuPageModal ref="modalRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="consultantFeiShuPageList">
import { onMounted, onActivated, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import { getDictItems } from '@/api/common/api';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import ConsultantFeiShuPageModal from './modules/consultantFeiShuPageModal.vue';

const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);

const feiSshuTypeList = ref([]);
const consultantList = ref([]);

const url = {
  list: '/kolsysuserfeishusheet/list',
  delete: '/kolsysuserfeishusheet/delete',
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

queryParam.value = {
  sysUserId: undefined,
  spreadSheetType: undefined,
};

const columns = [
  {
    title: '#',
    key: 'index',
    width: 50,
    align: 'center',
  },
  {
    title: '用户名称',
    dataIndex: 'username',
    width: 150,
    align: 'left',
  },
  {
    title: '表格类型',
    dataIndex: 'spreadSheetType',
    width: 100,
    align: 'left',
  },
  {
    title: '表格URL',
    dataIndex: 'spreadSheetUrl',
    align: 'left',
  },
  {
    title: '表格ID',
    dataIndex: 'spreadSheetId',
    width: 230,
    align: 'left',
  },
  {
    title: 'sheetID',
    dataIndex: 'sheetId',
    width: 100,
    align: 'left',
  },
  {
    title: '开始列',
    dataIndex: 'startColumn',
    width: 100,
    align: 'left',
  },
  {
    title: '结束列',
    dataIndex: 'endColumn',
    width: 100,
    align: 'left',
  },
  {
    title: '标题行',
    dataIndex: 'headerRow',
    width: 100,
    align: 'left',
  },
  {
    title: '是否同步列',
    dataIndex: 'isSynchronizeColumn',
    width: 100,
    align: 'left',
  },
  {
    title: '错误原因列',
    dataIndex: 'errorReasonColumn',
    width: 100,
    align: 'left',
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    align: 'center',
    fixed: 'right',
  },
];

const parseDict = (value) => {
  if (value && feiSshuTypeList.value.find((item) => item.value == value)) {
    return feiSshuTypeList.value.find((item) => item.value == value).title || '';
  }
  return '';
};



const initConsultantList = async () => {
  const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
  if (res) {
    consultantList.value = res || [];
  }
};

const initFeiSshuTypeList = async () => {
  const res = await getDictItems('feishu_type');
  if (res) {
    feiSshuTypeList.value = res || [];
  }
};

const searchQuery = () => {
  fetchTable(1);
};

const searchReset = () => {
  queryParam.value = {
    sysUserId: undefined,
    spreadSheetType: undefined,
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
    const res = await defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true });
    if (res) {
      // createMessage.success('删除成功');
      fetchTable();
    } else {
      // createMessage.warning(res?.message || '删除失败');
    }
  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};

const modalFormOk = () => {
  fetchTable();
};


initFeiSshuTypeList();
initConsultantList();
fetchTable();
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
</style>
