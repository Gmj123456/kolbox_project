<template>
  <a-card :bordered="false">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
          <a-form-item>
            <JDictSelectPlatformType  v-model:value="queryParam.platformType" dictCode="platform_type" />
          </a-form-item>
        </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.uniqueId" placeholder="网红账号" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" @click="handleSearchReset" style="margin-left: 8px">
                重置
              </a-button>
              <a-button
                v-if="selectedRowKeys.length > 0"
                type="primary"
                :icon="h(CloseSquareOutlined)"
                style="margin-left: 8px"
                @click="batchDel"
              >
                取消屏蔽
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
        <template v-else-if="column.key === 'avatar'">
          <a-tooltip placement="top">
            <template #title>
              <span>
                <img
                  v-if="text"
                 
                  :src="authorUrl(record)"
                  style="max-width: 200px;height: 200px;"
                />
                <img
                  v-else
                  :src="avatarDefault"
                  style="max-width: 30px; max-height: 30px; margin: 0 auto"
                />
              </span>
            </template>
            <span>
              <img
                v-if="text"
                :src="authorUrl(record)"
               
                style="max-height: 30px; max-width: 30px; cursor: pointer"
                :preview="record.id"
              />
              <img
                v-else
                :src="avatarDefault"
                style="max-width: 30px; max-height: 30px; margin: 0 auto"
              />
            </span>
          </a-tooltip>
        </template>
        <template v-else-if="column.key === 'productName'">
          {{ record.brandName }}-{{ record.productName }}
        </template>
        <template v-else-if="column.key === 'shieldReason'">
          <EllipsisTooltip v-if="text" :text="text" />
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'platformType'">
          <img
            v-if="text == 0"
            style="width: 20px; height: 20px"
            :src="platformIcons[0]"
            alt=""
          />
          <img
            v-if="text == 1"
            style="width: 20px; height: 20px"
            :src="platformIcons[1]"
            alt=""
          />
          <img
            v-if="text == 2"
            style="width: 20px; height: 20px"
            :src="platformIcons[2]"
            alt=""
          />
        </template>
        <template v-else-if="column.key === 'action'">
         
            <a-tooltip placement="top" title="取消屏蔽">
              <a @click="handleDelete(record.id)">
                <CloseSquareOutlined style="font-size: 16px" />
              </a>
            </a-tooltip>
        </template>
        <template v-else>
          {{ text || '--' }}
        </template>
      </template>
    </s-table>
  </a-card>
</template>

<script setup name="KolBlacklistList">
import { h, ref, onMounted } from 'vue';
import { Modal } from 'ant-design-vue';
import { SearchOutlined, ReloadOutlined, CloseSquareOutlined } from '@ant-design/icons-vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { useTable } from '/@/components/useSTable/useSTable';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import JDictSelectPlatformType from "@/components/jeecg/JDictSelectPlatformType.vue";
const { createMessage } = useMessage();

const Api = {
  List: '/kol/kolshields/list',
  Delete: '/kol/kolshields/delete',
  DeleteBatch: '/kol/kolshields/deleteBatch',
};

const columns = [
  { title: '#', key: 'index', width: 60, align: 'center' },
  { title: '头像', dataIndex: 'avatar', key: 'avatar', align: 'center' },
  { title: '网红账号', dataIndex: 'uniqueId', key: 'uniqueId' },
  { title: '平台', dataIndex: 'platformType', key: 'platformType', align: 'center' },
  { title: '产品', dataIndex: 'productName', key: 'productName' },
  { title: '屏蔽时间', dataIndex: 'shieldTime', key: 'shieldTime' },
  { title: '屏蔽原因', dataIndex: 'shieldReason', key: 'shieldReason' },
  { title: '操作', dataIndex: 'action', key: 'action', align: 'center' },
];

async function loadDataApi(params) {
  const res = await defHttp.get({ url: Api.List, params });
  return res || [];
}

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  rowSelection,
  selectedRowKeys,
  searchQuery,
  searchReset,
  pageChange
} = useTable(loadDataApi);

queryParam.value = {
  uniqueId: '',
};

const modalFormRef = ref(null);
const celebrityConsultants = ref([]);

const avatarDefault = new URL('@/assets/avatar.png', import.meta.url).href; /* @vite-ignore */

const platformIcons = {
  0: new URL('@/assets/images/ins.png', import.meta.url).href,
  1: new URL('@/assets/images/yt.png', import.meta.url).href,
  2: new URL('@/assets/images/tk.png', import.meta.url).href,
};

function handleImageError(e) {
  e.target.src = avatarDefault;
}

function authorUrl(record) {
  if (record.avatar && record.avatar.includes('https')) {
    return record.avatar;
  } else if (record.avatar) {
    return 'https://gemstone-image.kolbox.com/avatar/' + record.avatar;
  }
  return avatarDefault;
}

function handleDelete(id) {
  Modal.confirm({
    title: '取消屏蔽',
    content: '确定取消屏蔽吗?',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        loading.value = true;
        const res = await defHttp.delete({ url: Api.Delete, data: { id } }, { joinParamsToUrl: true, isTransformResponse: false });
        if (res.success) {
          createMessage.success(res.message);
          fetchTable();
        }else{
          createMessage.warning(res.message);
        }
          
        
       
      } catch (error) {
        console.error(error);
        // createMessage.error('操作失败');
      } finally {
        loading.value = false;
      }
    },
  });
}

function batchDel() {
  if (!selectedRowKeys.value.length) {
    createMessage.warning('请选择一条记录！');
    return;
  }
  Modal.confirm({
    title: '取消屏蔽',
    content: '是否将选中的网红移出屏蔽列表？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        loading.value = true;
        const ids = selectedRowKeys.value.join(',');
        const res = await defHttp.delete({ url: Api.DeleteBatch, data: { ids } }, { joinParamsToUrl: true, isTransformResponse: false });
        selectedRowKeys.value = [];
        if (res.success) {
          createMessage.success(res.message);
          fetchTable();
        }else{
          createMessage.warning(res.message);
        }
      } catch (error) {
        console.error(error);
        createMessage.error('操作失败');
      } finally {
        loading.value = false;
      }
    },
  });
}

function modalFormOk() {
  fetchTable(1);
}

function handleSearchReset() {
  searchReset();
  queryParam.value = { uniqueId: '' };
  fetchTable(1);
}

async function initlCelebrityConsultant() {
  try {
    const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
    if (res) {
      celebrityConsultants.value = res.result || [];
    }
  } catch (error) {
    console.error(error);
  }
}

onMounted(() => {
  initlCelebrityConsultant();
  fetchTable(1);
});
</script>

<style scoped lang="less">
/deep/ .ant-radio-button-wrapper:not(:first-child)::before {
  width: 0.1px !important;
  background-color: transparent;
}
/deep/ .ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before {
  background-color: #3155ed !important;
}
</style>
