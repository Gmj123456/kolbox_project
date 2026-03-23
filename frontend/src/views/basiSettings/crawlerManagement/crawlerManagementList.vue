<template>
  <a-card :bordered="false">
    <div >
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <JDictSelectTag
              :showChooseOption="false"
                v-model:value="queryParam.platformType"
                placeholder="平台"
                dictCode="platform_type"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.crawlerName" placeholder="名称" />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.deviceCode" placeholder="设备编码" />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model:value="queryParam.taskStatus" placeholder="任务状态">
                <a-select-option :value="0">已停止</a-select-option>
                <a-select-option :value="1">运行中</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <span>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery" style="margin-left: 8px">
                查询
              </a-button>
              <a-button :icon="h(ReloadOutlined)" style="margin-left: 8px" @click="searchReset">重置</a-button>
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
        <template v-else-if="column.dataIndex === 'platformType'">
          <span v-if="text == 0">IG</span>
          <span v-else-if="text == 1">YT</span>
          <span v-else-if="text == 2">TK</span>
        </template>
        <template v-else-if="['crawlerName', 'ipAddress', 'deviceCode', 'remoteId'].includes(column.dataIndex)">
          <a @click="copyFn(text)">{{ text }}</a>
        </template>
        <template v-else-if="column.dataIndex === 'taskStatus'">
          <span>
            <span class="status" :style="{ backgroundColor: text == 1 ? '#10D269' : 'red' }"></span>
            <span>{{ text == 1 ? '运行中' : '已停止' }}</span>
          </span>
        </template>
        <template v-else-if="column.dataIndex === 'lastExecutionTime'">
          {{ parseTime(text) }}
        </template>
        <template v-else-if="column.dataIndex === 'isReminder'">
          <a-switch
            size="small"
            checked-children="是"
            un-checked-children="否"
            :checked="record.isReminder"
            @change="(checked) => handleChangeSwitch(checked, record)"
          />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip :title="record.isReminder == 1 ? '关闭' : '开启'">
            <a>
              <a-icon
                style="font-size: 17px"
                :type="record.isReminder == 1 ? 'close-circle' : 'play-circle'"
                @click="handleTaskStatus(record)"
              />
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a-tooltip title="删除">
              <a>
                <a-icon style="font-size: 17px" type="delete" />
              </a>
            </a-tooltip>
          </a-popconfirm>
        </template>
        <template v-else>
          {{ text }}
        </template>
      </template>
    </s-table>

    
  </a-card>
</template>

<script setup name="crawlerManagementList">
import { onMounted, ref, h } from 'vue';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import dayjs from 'dayjs';
import JDictSelectTag from '@/components/Form/src/jeecg/components/JDictSelectTag.vue';

const { createMessage, createConfirm } = useMessage();
const tableRef = ref(null);

const url = {
  list: '/scrapemonitor/list',
  delete: '/scrapemonitor/delete',
};

const fetchTableApi = async (params) => {
  const res = await defHttp.get({ url: url.list, params });
  // const result = res?.result || {};
  console.log(res)
  const result = res
  
  // 处理 isReminder 字段，将数字转换为布尔值
  result.records.forEach((element) => {
    element.isReminder = element.isReminder == 1;
  });

  return result;
};

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
  copyFn,
} = useTable(fetchTableApi);

queryParam.value = {
  platformType: undefined,
  crawlerName: undefined,
  deviceCode: undefined,
  taskStatus: undefined,
};

const columns = [
  {
    title: '#',
    key: 'index',
    width: 50,
    align: 'center',
  },
  {
    title: '平台',
    width: 120,
    dataIndex: 'platformType',
  },
  {
    title: '名称',
    dataIndex: 'crawlerName',
  },
  {
    title: 'IP地址',
    width: 130,
    dataIndex: 'ipAddress',
  },
  {
    title: '设备编码',
    dataIndex: 'deviceCode',
  },
  {
    title: '远程码',
    width: 120,
    dataIndex: 'remoteId',
  },
  {
    title: '最后执行时间',
    dataIndex: 'lastExecutionTime',
  },
  {
    title: '任务内容',
    dataIndex: 'taskContent',
  },
  {
    title: '任务状态',
    width: 120,
    dataIndex: 'taskStatus',
  },
  {
    title: '启用提醒',
    width: 120,
    dataIndex: 'isReminder',
  },
  {
    title: '剩余提醒次数',
    width: 120,
    dataIndex: 'remainingTimes',
  },
  {
    title: '操作',
    width: 120,
    key: 'action',
  },
];

const parseTime = (date) => {
  if (date) {
    return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
  } else {
    return '-';
  }
};

const handleTaskStatus = async (record) => {
  try {
    const res = await defHttp.post({ url: '/scrapemonitor/reminder', data: { id: record.id } });
    fetchTable();

  } catch (error) {
    console.error('handleTaskStatus error:', error);
    createMessage.error('操作失败');
  }
};

const handleChangeSwitch = (checked, record) => {
  createConfirm({
    iconType: 'warning',
    title: checked ? '启用' : '禁用',
    content: checked ? '确定要开启提醒吗?' : '确定要禁用提醒吗?',
    onOk: async () => {
      try {
        await defHttp.post({ url: '/scrapemonitor/reminder', data: { id: record.id } });
        fetchTable();
       
      } catch (error) {
        console.error('handleChangeSwitch error:', error);
        record.isReminder = !checked;
       
      }
    },
    onCancel: () => {
      record.isReminder = !checked;
    },
  });
};

const searchQuery = () => {
  fetchTable(1);
};

const searchReset = () => {
  queryParam.value = {
    platformType: undefined,
    crawlerName: undefined,
    deviceCode: undefined,
    taskStatus: undefined,
  };
  fetchTable(1);
};

const handleDelete = async (id) => {
  try {
    const res = await defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true });
    fetchTable();
   
  } catch (error) {
    console.error('handleDelete error:', error);
    createMessage.error('删除失败');
  }
};



fetchTable();
</script>

<style scoped>

</style>
