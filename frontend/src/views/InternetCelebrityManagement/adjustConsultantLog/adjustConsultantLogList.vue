<template>
  <a-card :bordered="false">
      <a-form  class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                show-search
                option-filter-prop="label"
                allowClear
                v-model:value="queryParam.fromCounselorId"
                placeholder="交接人"
              >
                <a-select-option
                  v-for="item in celebrityCounselorList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.username"
                  >{{ item.username }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                show-search
                option-filter-prop="label"
                allowClear
                v-model:value="queryParam.toCounselorId"
                placeholder="接收人"
              >
                <a-select-option
                  v-for="item in celebrityCounselorList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.username"
                  >{{ item.username }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                show-search
                option-filter-prop="label"
                allowClear
                v-model:value="queryParam.operatorId"
                placeholder="操作人"
              >
                <a-select-option
                  v-for="item in celebrityCounselorList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.username"
                  >{{ item.username }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
               
                allowClear
                v-model:value="queryParam.changeStatus"
                placeholder="变更状态"
              >
                <a-select-option :value="1" >变更成功</a-select-option>
                <a-select-option :value="-1" >变更失败</a-select-option>
                <a-select-option :value="-2">撤销交接</a-select-option>
                
              </a-select>
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
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'fromContactEmail'">
          {{ record.fromContactEmail || '--' }}
        </template>
        <template v-else-if="column.key === 'toContactEmail'">
          {{ record.toContactEmail || '--' }}
        </template>
        <template v-else-if="column.key === 'isCooperated'">
          <a-tag color="green" v-if="record.isCooperated == 1">是</a-tag>
          <a-tag color="orange" v-else>否</a-tag>
        </template>
        <template v-else-if="column.key === 'changeStatus'">
          <a-tag color="green" v-if="record.changeStatus == 1" style="margin-right: 0px"
            >变更成功</a-tag
          >
          <a-tag
            color="red"
            v-else-if="record.changeStatus == -1"
            style="margin-right: 0px"
            >变更失败</a-tag
          >
          <a-tag v-else style="margin-right: 0px">撤销交接</a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip placement="top">
            <template #title>
              <span>交接明细</span>
            </template>
            <a @click="viewAdjustmentCounselorDetail(record)">
              <i style="font-size: 15px" class="iconfont action-cursor">&#xe66b;</i>
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-tooltip placement="top">
            <template #title>
              <span>撤销交接</span>
            </template>
            <a @click="cancelAdjustmentCounselor(record.id)">
              <i style="font-size: 17px" class="iconfont action-cursor">&#xe610;</i>
            </a>
          </a-tooltip>
        </template>
        <template v-else>
          {{ text }}
        </template>
      </template>
    </s-table>
    <AdjustmentCounselorDetail ref="AdjustmentCounselorDetailRef" @ok="modalFormOk"></AdjustmentCounselorDetail>
  </a-card>
</template>

<script setup name="adjustConsultantLogList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import AdjustmentCounselorDetail from './modules/AdjustmentCounselorDetail.vue';
import { Modal } from 'ant-design-vue';
const { createMessage } = useMessage();
const modalRef = ref(null);
const tableRef = ref(null);
const celebrityCounselorList = ref([]);
const url = {
   list: "/counselorChangeLog/list",
   delete: "/counselorChangeDetail/cancelAdjustmentCounselor",
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


const AdjustmentCounselorDetailRef = ref(null);
const columns = [
  {
    title: '#',
    key: 'index',
    width: 60,
    align: 'center',
  },
  {
      title: "交接人",
      width: 100,
      // align: "center",
      dataIndex: "fromCounselorName",
      key: "fromCounselorName",
    },
    {
      title: "交接人邮箱",
      // align: "center",
      key: "fromContactEmail",
      scopedSlots: {
        customRender: "fromContactEmail",
      },
    },
    {
      title: "接收人",
      width: 100,
      dataIndex: "toCounselorName",
      // align: "center",
      key: "toCounselorName",
    },
    {
      title: "接收人邮箱",
      dataIndex: "toContactEmail",
      // align: "center",
      key: "toContactEmail",
     
    },

    {
      title: "是否合作",
      align: "center",
      dataIndex: "isCooperated",
      key: "isCooperated",
      width: 150,
    
    },
    {
      title: "操作人",
      width: 100,
      dataIndex: "createBy",
      align: "center",
      key: "createBy",
    },

    {
      title: "变更状态",
      dataIndex: "changeStatus",
      key: "changeStatus",
      align: "center",
      width: 150,
     
    },
    {
      title: "操作时间",
      dataIndex: "createTime",
      width: 200,
      // align: "center",
      key: "createTime",
    },

    {
      title: "操作",
      dataIndex: "action",
      align: "center",
      key: "action",
      width: 120,
      // align: "center",
     
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

const handleAdd = () => {
  modalRef.value?.open();
};


const modalFormOk = () => {
  fetchTable();
};

function viewAdjustmentCounselorDetail(record) {
  AdjustmentCounselorDetailRef.value.edit(record);
  AdjustmentCounselorDetailRef.value.title = "交接明细";
}
function cancelAdjustmentCounselor(id) {

  Modal.confirm({
    title: '提示',
    
    content: '确定撤销交接吗?',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      console.log("id", id);
      defHttp.get({ url: this.url.delete, params: { logId: id } }).then((res) => {
        if (res) {
        fetchTable();
        } else {
          createMessage.error(res.message);
          }
        })
      },
      onCancel: () => {
       
      },
  });
}
async function initCelebrityCounselor() {
  const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
  if (res) {
    celebrityCounselorList.value = res;
  }
}
onMounted(() => {
});
initCelebrityCounselor()
fetchTable();
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
