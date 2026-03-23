<template>
  <a-card :bordered="false">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.uniqueId" placeholder="网红账号" />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.videoUrl" placeholder="视频链接" />
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input-group compact>
                <a-input
                  style="width: 72px; background-color: #fff; color: #0b1019"
                  default-value="日期区间"
                  disabled
                />
                <a-range-picker
                  style="width: calc(100% - 72px)"
                  v-model:value="dateList"
                  @change="dateChang"
                />
              </a-input-group>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-checkbox v-model:checked="autoRefreshChecked" @change="onChange">自动更新</a-checkbox>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" @click="handleSearchReset" style="margin-left: 8px">
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
                :icon="h(DownloadOutlined)"
                style="margin-left: 8px"
                @click="handleExportXls('Tiktok视频获取')"
              >
                导出
              </a-button>
              <a-button
                v-if="selectedRowKeys.length > 0"
                type="primary"
                style="margin-left: 8px"
                @click="batchDel"
              >
                <span class="icon iconfont icon-shanchu" style="font-size: 12px; margin-right: 4px"></span>
                批量删除
              </a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>


    <s-table
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :row-selection="rowSelection"
      :loading="loading"
      :pagination="pagination"
      :rangeSelection="false"
      :scroll="{ y: sTableHeight, x: '100%' }"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'uniqueId'">
          <JEllipsis v-if="text" :value="text" :length="10" />
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'videoUrl'">
          <a v-if="text" @click="openLink(text)">
            <JEllipsis :value="text" :length="25" />
          </a>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'videoDesc'">
          <JEllipsis v-if="text" :value="text" :length="40" />
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'videoCollectCount'">
          <template v-if="text">{{ getFollower(text) }}</template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'videoCommentCount'">
          <template v-if="text">{{ getFollower(text) }}</template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'videoDiggCount'">
          <template v-if="text">{{ getFollower(text) }}</template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'videoPlayCount'">
          <template v-if="text">{{ getFollower(text) }}</template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'videoShareCount'">
          <template v-if="text">{{ getFollower(text) }}</template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-tag v-if="text == 0" color="#f50">未更新</a-tag>
          <a-tag v-if="text == 1" color="red">更新异常</a-tag>
          <a-tag v-if="text == 2" color="#87d068">已更新</a-tag>
        </template>
        <template v-else-if="column.key === 'createTime'">
          <template v-if="text">
            <span>{{ timeFormat(text) }}</span>
          </template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'updateTime'">
          <template v-if="text">
            <span>{{ timeFormat(text) }}</span>
          </template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip title="重置">
            <a @click="relaod(record)">
              <ReloadOutlined style="font-size: 15px" />
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-popconfirm placement="left" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>
              <span class="icon iconfont icon-shanchu"></span>
            </a>
          </a-popconfirm>
        </template>
        <template v-else>
          {{ text || '--' }}
        </template>
      </template>
    </s-table>


    <ReloadModal ref="reloadModalRef" @ok="modalFormOk" />
    <TiktokVideoModal ref="modalFormRef" @ok="modalFormOk" />
  </a-card>
</template>

<script setup name="videoUpdateList">
import { h, ref, onMounted, onBeforeUnmount, onActivated } from 'vue';
import { onBeforeRouteLeave } from 'vue-router';
import { Modal } from 'ant-design-vue';
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DownloadOutlined,
} from '@ant-design/icons-vue';
import dayjs from 'dayjs';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { useTable } from '/@/components/useSTable/useSTable';
import JEllipsis from '/@/components/Form/src/jeecg/components/JEllipsis.vue';
import TiktokVideoModal from './modules/TiktokVideoModal.vue';
import ReloadModal from './modules/ReloadModal.vue';

const { createMessage } = useMessage();

const Api = {
  List: '/tiktokVideo/list',
  Delete: '/tiktokVideo/delete',
  DeleteBatch: '/tiktokVideo/deleteBatch',
  Export: '/tiktokVideo/exportXls',
};

const columns = [
  { title: '#', key: 'index', width: 50, align: 'center' },
  { title: '网红账号', dataIndex: 'uniqueId', key: 'uniqueId', align: 'center' },
  { title: '视频链接', dataIndex: 'videoUrl', key: 'videoUrl', width: 200, align: 'left' },
  { title: '视频创建时间', dataIndex: 'videoCreateTime', key: 'videoCreateTime', width: 150, align: 'center' },
  { title: '视频标题', dataIndex: 'videoDesc', key: 'videoDesc', width: 300, align: 'left' },
  { title: '收藏数', dataIndex: 'videoCollectCount', key: 'videoCollectCount', align: 'center' },
  { title: '评论数', dataIndex: 'videoCommentCount', key: 'videoCommentCount', align: 'center' },
  { title: '点赞数', dataIndex: 'videoDiggCount', key: 'videoDiggCount', align: 'center' },
  { title: '播放数', dataIndex: 'videoPlayCount', key: 'videoPlayCount', align: 'center' },
  { title: '分享数', dataIndex: 'videoShareCount', key: 'videoShareCount', align: 'center' },
  { title: '状态', dataIndex: 'status', key: 'status', align: 'center' },
  { title: '添加时间', dataIndex: 'createTime', key: 'createTime', align: 'center' },
  { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', align: 'center', sorter: true },
  { title: '备注', dataIndex: 'remark', key: 'remark', align: 'center' },
  { title: '操作', dataIndex: 'action', key: 'action', align: 'center' },
];

async function loadDataApi(params) {
  const res = await defHttp.get({ url: Api.List, params });
  console.log(res);
  
  const result = res || {};
  result.records.forEach((item) => {
    if (!item.videoId) {
      item.status = 0;
    } else if ((!item.videoPlayCount && item.videoId) || item.videoId == -1) {
      item.status = 1;
    } else {
      item.status = 2;
    }
  });
  return result;
}

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
  rowSelection,
  selectedRowKeys,
  searchQuery,
  searchReset,
} = useTable(loadDataApi);

queryParam.value = {
  uniqueId: '',
  videoUrl: '',
  startDateTime: undefined,
  endDateTime: undefined,
};

const dateList = ref([]);
const autoRefreshChecked = ref(false);
const timer = ref(null);
const countdownTimer = ref(null);
const refreshInterval = ref(20000);
const countdownSeconds = ref(20);
const reloadModalRef = ref(null);
const modalFormRef = ref(null);

function setDefaultDate() {
  const today = dayjs().startOf('day');
  const tomorrow = dayjs(today).add(1, 'day').startOf('day');
  dateList.value = [today, tomorrow];
  queryParam.value.startDateTime = today.format('YYYY-MM-DD');
  queryParam.value.endDateTime = tomorrow.format('YYYY-MM-DD');
}

function dateChang(dates, dateStrings) {
  queryParam.value.startDateTime = dateStrings[0]
    ? dayjs(dateStrings[0]).format('YYYY-MM-DD')
    : undefined;
  queryParam.value.endDateTime = dateStrings[1]
    ? dayjs(dateStrings[1]).format('YYYY-MM-DD')
    : undefined;
}

function timeFormat(text) {
  if (!text) return '--';
  return text.split(' ')[0];
}

function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    const kValue = (num / 1000).toFixed(2);
    return parseFloat(kValue) % 1 === 0 ? `${parseInt(kValue)}K` : `${parseFloat(kValue)}K`;
  } else if (num >= 1000000) {
    const mValue = (num / 1000000).toFixed(2);
    return parseFloat(mValue) % 1 === 0 ? `${parseInt(mValue)}M` : `${parseFloat(mValue)}M`;
  } else if (num > 0 && num < 1000) {
    return num;
  } else {
    return '--';
  }
}

function openLink(url) {
  window.open(url);
}

function handleAdd() {
  if (modalFormRef.value) {
    modalFormRef.value.add();
  }
}

function relaod(record) {
  if (reloadModalRef.value) {
    reloadModalRef.value.edit(record);
    reloadModalRef.value.title = '重置';
  }
}

function handleDelete(id) {
  defHttp.delete({ url: Api.Delete, data: { id } },{ joinParamsToUrl: true }).then(() => {
    // createMessage.success('删除成功');
    fetchTable(1);
  });
}

function batchDel() {
  if (!selectedRowKeys.value.length) {
    createMessage.warning('请选择需要删除的记录');
    return;
  }
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      defHttp.delete({ url: Api.DeleteBatch, data: { ids: selectedRowKeys.value } },{ joinParamsToUrl: true }).then(() => {
        // createMessage.success('删除成功');
        selectedRowKeys.value = [];
        fetchTable(1);
      });
    },
  });
}

async function handleExportXls(fileName = 'Tiktok视频获取') {
  try {
    const params = {
      ...queryParam.value,
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
    };
    const blob = await defHttp.get(
      { url: Api.Export, params, responseType: 'blob' },
      { isTransformResponse: false },
    );
    if (!blob) {
      createMessage.warning('文件下载失败');
      return;
    }
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement('a');
    link.style.display = 'none';
    link.href = url;
    link.download = `${fileName}.xls`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error(error);
    createMessage.error('导出失败');
  }
}

function modalFormOk() {
  fetchTable(1);
}

function handleSearchReset() {

  setDefaultDate();
  fetchTable(1);
}

function updateCountdown() {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
  countdownTimer.value = setInterval(() => {
    if (countdownSeconds.value > 0) {
      countdownSeconds.value -= 1;
    } else {
      countdownSeconds.value = Math.floor(refreshInterval.value / 1000);
    }
  }, 1000);
}

function onChange(e) {
  const checked = e.target.checked;
  if (checked) {
    localStorage.setItem('autoRefresh', 'true');
    startAutoRefresh();
  } else {
    clearAutoRefresh();
    localStorage.setItem('autoRefresh', 'false');
  }
}

function startAutoRefresh() {
  clearAutoRefresh();
  countdownSeconds.value = Math.floor(refreshInterval.value / 1000);
  updateCountdown();
  timer.value = setInterval(() => {
    const isModalVisible = modalFormRef.value && modalFormRef.value.visible;
    const isReloadModalVisible = reloadModalRef.value && reloadModalRef.value.visible;
    if (!loading.value && !isModalVisible && !isReloadModalVisible) {
      fetchTable();
      countdownSeconds.value = Math.floor(refreshInterval.value / 1000);
    } else {
      resetRefreshInterval(20000);
    }
  }, refreshInterval.value);
}

function resetRefreshInterval(interval) {
  if (interval && typeof interval === 'number') {
    refreshInterval.value = interval;
  }
  clearAutoRefresh();
  startAutoRefresh();
}

function clearAutoRefresh() {
  if (timer.value) {
    clearInterval(timer.value);
    timer.value = null;
  }
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
    countdownTimer.value = null;
  }
}

onBeforeRouteLeave((to, from, next) => {
  clearAutoRefresh();
  next();
});

onMounted(() => {
  const savedAutoRefresh = localStorage.getItem('autoRefresh') === 'true';
  autoRefreshChecked.value = savedAutoRefresh;
  if (savedAutoRefresh) {
    startAutoRefresh();
  }
  setDefaultDate();
  fetchTable(1);
});

onBeforeUnmount(() => {
  clearAutoRefresh();
});

onActivated(() => {
  const savedAutoRefresh = localStorage.getItem('autoRefresh') === 'true';
  if (savedAutoRefresh && !timer.value) {
    startAutoRefresh();
  }
});
</script>

<style scoped>

</style>
