<template>
  <a-modal
    v-model:visible="visibleAssignment"
    :maskClosable="false"
    :title="title"
    :width="1200"
    :centered="true"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    class="undistributedTagNumbersModel"
    @ok="handleOk"
  >
    <a-spin :spinning="confirmLoading">
      <a-form style="" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :span="4">
            <a-form-item>
              <JDictSelectPlatformType
               
                v-model:value="queryParam.platformType"
                dictCode="platform_type"
                :triggerChange="true"
                @change="onPlatformTypeChange"
              ></JDictSelectPlatformType>
            </a-form-item>
          </a-col>
          <a-col :span="2">
            <a-form-item>
              <a-select
                option-filter-prop="label"
                show-search
                v-model:value="queryParam.countryCode"
                placeholder="国家"
              >
                <a-select-option
                  v-for="(item, key) in countryList"
                  :key="key"
                  :value="item.shortCode"
                  :label="item.country"
                >
                  {{ item.country }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item>
              <div style="display: flex">
                <div style="flex: 1; position: relative">
                  <a-select
                    :autoClearSearchValue="false"
                    @blur="tagBlur"
                    @search="fetchUser"
                    @change="handleChange"
                    option-filter-prop="label"
                    :not-found-content="fetching ? undefined : null"
                    :maxTagCount="4"
                    mode="multiple"
                    v-model:value="queryParam.tagNameList"
                    allowClear
                    placeholder="标签"
                  >
                    <template #notFoundContent>
                      <a-spin v-if="fetching" size="small" />
                    </template>
                    <a-select-option
                      v-for="tag in tags"
                      :key="tag.index"
                      :value="tag.tagName"
                      :label="tag.tagName"
                    >
                      {{ tag.tagName }}
                    </a-select-option>
                  </a-select>
                </div>
                <div>
                  <a-popover
                    v-model:visible="tagVisible"
                    :overlayStyle="overlayStyle"
                    trigger="click"
                    placement="bottomLeft"
                  >
                    <template #content>
                      <a-textarea
                        
                        v-model:value="tagValue"
                        :auto-size="{ minRows: 6, maxRows: 6 }"
                        placeholder="精确输入，一行一项"
                        rows=""
                        cols=""
                      ></a-textarea>
                      <!-- <p style="color:red;margin-top:10px" v-if="noTag">不存在的标签名：<span style="margin-right: 5px;" v-for="item in nonExistentTag" :key="item">{{item}}</span></p> 
                            -->
                      <div
                        style="
                          color: red;
                          margin-top: 10px;
                          height: 100px;
                          overflow-y: auto;
                        "
                        v-if="noTag"
                      >
                        不存在的标签名：
                        <div
                          style="margin-right: 5px"
                          v-for="item in nonExistentTag"
                          :key="item"
                        >
                          {{ item }}
                        </div>
                      </div>
                      <div style="margin-top: 10px;display: flex;justify-content: space-between">
                        <a-button @click="closeTagEdit" 
                          >关闭</a-button
                        >
                        <div>
                          <a-button @click="clear" style="margin-right: 8px">清空</a-button>
                          <a-button @click="tagSerch(tagValue)">确定</a-button>
                        </div>
                      </div>
                    </template>
                    <AlignLeftOutlined
                      :style="{ color: tagVisible ? 'blue' : '' }"
                      style="position: absolute; right: 20px; top: 10px; cursor: pointer"
                      @click="tagVisible = !tagVisible"
                    />
                  </a-popover>
                </div>
              </div>
            </a-form-item>
          </a-col>
          <a-col  :span="10">
            <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
            <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px"
              >重置</a-button
            >
            <a-button
              type="primary"
              style="margin-left: 8px"
              :icon="h(DownloadOutlined)"
              @click="handleExportXls('未分配标签')"
              >导出</a-button
            >
            <a-button type="primary" style="margin-left: 8px" @click="batchDistribute"
              >批量分配</a-button
            >

            <a-button type="primary" :icon="h(SyncOutlined)" style="margin-left: 8px" @click="refresh"
              >手动刷新</a-button
            >
          </a-col>
        </a-row>
      </a-form>
      <a-table
        :row-selection="rowSelection"
        size="small"
        rowKey="tagName"
        :columns="columns"
        :pagination="ipagination"
        :loading="loading"
        :dataSource="dataSourceResult"
        :scroll="{ y: 600 }"
        @change="handleTableChange"
      >
        <template #tagName="{ text }">
          <a-tag @click="handleCopy(text)" color="#3155ED">
            {{ text }}
          </a-tag>
        </template>
      </a-table>
    </a-spin>
    <SelectCelebrityConsultantModal
      ref="selectCelebrityConsultantModalRef"
      @ok="SelectCelebrityConsultantModalOk"
    ></SelectCelebrityConsultantModal>
  </a-modal>
</template>

<script setup>
import { computed, nextTick, reactive, ref, h } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { postFileblob } from '/@/api/common/api';
import { debounce } from 'lodash';
import { defHttp } from '@/utils/http/axios';
import {
  SearchOutlined,
  ReloadOutlined,
  DownloadOutlined,
  SyncOutlined,
  AlignLeftOutlined,
} from '@ant-design/icons-vue';
import SelectCelebrityConsultantModal from './SelectCelebrityConsultantModal.vue';
import JDictSelectPlatformType from "@/components/jeecg/JDictSelectPlatformType.vue";
import { copyTextToClipboard } from '@/hooks/web/useCopyToClipboard';
const emit = defineEmits(['ok']);
const title = ref('未分配标签');
const visibleAssignment = ref(false);
const confirmLoading = ref(false);
const loading = ref(false);
const fetching = ref(false);
const type = ref('');
const dataSourceResult = ref([]);
const selectedRowKeys = ref([]);
const selectionRows = ref([]);
const tagVisible = ref(false);
const noTag = ref(false);
const tagValue = ref('');
const tags = ref([]);
const overlayStyle = { width: '270px' };
const nonExistentTag = ref([]);
const countryList = ref([]);
const url = {
  list: '/kol/tags/getUnAllottedTagCount',
  exportXlsUrl: '/kol/kolTagCelebrity/exportTagsExcel',
};
const params = ref({});
const queryParam = reactive({
  platformType: '2',
  countryCode: 'US',
  tagNameList: [],
});
const ipagination = reactive({
  current: 1,
  pageSize: 30,
  total: 0,
  showQuickJumper: true,
  showSizeChanger: true,
});
const isorter = reactive({
  column: '',
  order: '',
});
const filters = reactive({});
const superQueryParams = ref('');
const sortedInfo = ref({});
const selectCelebrityConsultantModalRef = ref(null);
const { createMessage } = useMessage();
const lastFetchId = ref(0);

const columns = [
  {
    title: '#',
    dataIndex: 'index',
    width: 60,
    align: 'center',
    customRender: ({ index }) => parseInt(String(index)) + 1,
  },
  {
    title: '标签',
    dataIndex: 'tagName',
    align: 'center',
    slots: {
      customRender: 'tagName',
    },
  },
  {
    title: '未分配数量',
    dataIndex: 'tagNum',
    align: 'center',
    slots: {
      customRender: 'tagNum',
    },
  },
];
function handleCopy(text) {
  const success = copyTextToClipboard(text);
  if (success) {
    createMessage.success('复制成功！');
  } else {
  createMessage.error('复制失败！');
  }
}
function SelectCelebrityConsultantModalOk() {
  selectedRowKeys.value = [];
  selectionRows.value = [];
  loadData();
}

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: onSelectChange,
}));

const initCountryList = async () => {
  const res = await defHttp.get({ url: '/tiktokcountry/getCountryList' });
  countryList.value = res || [];
};

const tagBlur = () => {
  tags.value = [];
};

const closeTagEdit = () => {
  tagVisible.value = false;
  clear();
  noTag.value = false;
};

const clear = () => {
  tagValue.value = '';
  noTag.value = false;
};

const onPlatformTypeChange = (value) => {
  queryParam.platformType = value != null ? String(value) : undefined;
};

const refresh = () => {
  defHttp.get({ url: '/kol/tagAllot/refreshUnallocatedQty', params: {
    platformType: queryParam.platformType,
  } }).then((res) => {
    if (res) {
      // createMessage.success('刷新成功');
    } else {
      // createMessage.error('刷新失败');
    }
  });
};

const searchReset = () => {
  queryParam.tagNameList = [];
  queryParam.platformType = '2';
  queryParam.countryCode = 'US';
  closeTagEdit();
  loadData(1);
};

const handleChange = () => {
  fetching.value = false;
};

const doFetchUser = async (value) => {
  if (!value) {
    return;
  }
  lastFetchId.value += 1;
  const fetchId = lastFetchId.value;
  tags.value = [];
  fetching.value = true;
  const res = await defHttp.post({ url: '/kol/tags/queryTagsListByName', data: {
    tagName: value,
    platformType: queryParam.platformType,
  } });
  if (fetchId !== lastFetchId.value) {
    return;
  }
  tags.value = res || [];
  fetching.value = false;
};

const fetchUser = debounce(doFetchUser, 800);

const tagSerch = (value) => {
  if (!value) return;
  let allTag = value.trim().split('\n');
  allTag = allTag.map((item) => item.trim());
  defHttp.post({ url: '/kol/tags/queryTagsListByName', data: {
    tagNames: allTag.join('|'),
    platformType:queryParam.platformType,
  } }).then((res) => {
    if (res) {
      const filterTags = res || [];
      const lowerTags = filterTags.map((tag) => tag.tagName.toLowerCase());
      nonExistentTag.value = allTag.filter(
        (item) => !lowerTags.includes(item.toLowerCase())
      );
      if (nonExistentTag.value.length > 0) {
        noTag.value = true;
      } else {
        queryParam.tagNameList = allTag;
        tagVisible.value = false;
      }
    }
  });
};

const handleExportXls = (fileName) => {
  if (selectedRowKeys.value.length === 0) {
    createMessage.warning('请选择导出的标签');
    return;
  }
  const param = {
    ...queryParam,
    platformType: queryParam.platformType,
    minFollowers: params.value.minFollowers || '',
    maxFollowers: params.value.maxFollowers || '',
    minVideoStandardCount: params.value.minVideoStandardCount || '',
    maxVideoStandardCount: params.value.maxVideoStandardCount || '',
    tagNameList: selectedRowKeys.value,
  };

  postFileblob(
    url.exportXlsUrl,
    param
  )
    .then((data) => {
      console.log(data);
      if (typeof window.navigator.msSaveBlob !== "undefined") {
        window.navigator.msSaveBlob(
          new Blob([data], { type: "application/vnd.ms-excel" }),
          fileName + ".xlsx"
        );
      } else {
        let urlfile = window.URL.createObjectURL(
          new Blob([data], { type: "application/vnd.ms-excel" })
        );
        let link = document.createElement("a");
        link.style.display = "none";
        link.href = urlfile;
        link.setAttribute("download", fileName + ".xls");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link); //下载完成移除元素
        window.URL.revokeObjectURL(urlfile); //释放掉blob对象
      }
    })
}

const batchDistribute = () => {
  if (selectedRowKeys.value.length === 0) {
    createMessage.warning('请选择标签');
  } else {
    selectCelebrityConsultantModalRef.value && selectCelebrityConsultantModalRef.value.add(
      queryParam.platformType,
      selectedRowKeys.value,
      queryParam.countryCode
    );
  }
};

function onSelectChange(keys, rows) {
  selectedRowKeys.value = keys;
  selectionRows.value = rows;
}

const getQueryField = () => {
  let str = 'id,';
  columns.forEach((value) => {
    str += `,${value.dataIndex}`;
  });
  return str;
};

const getQueryParams = () => {
  const sqp = {};
  if (superQueryParams.value) {
    sqp.superQueryParams = encodeURI(superQueryParams.value);
  }
  const param = Object.assign({}, sqp, queryParam, isorter, filters);
  param.field = getQueryField();
  param.pageNo = ipagination.current;
  param.pageSize = ipagination.pageSize;
  return param;
};

const handleTableChange = (pagination, _filters, sorter) => {
  if (Object.keys(sorter).length > 0) {
    isorter.order = sorter.order === 'ascend' ? 'asc' : 'desc';
  }
  ipagination.current = pagination.current;
  ipagination.pageSize = pagination.pageSize;
  sortedInfo.value = sorter;
  loadData();
};

const add = async (platformType, countryCode, newParams) => {
  params.value = newParams;
  type.value = Number(platformType);
  visibleAssignment.value = true;
  queryParam.platformType = String(platformType);
  queryParam.countryCode = countryCode || 'US';
  queryParam.tagNameList = [];
  await initCountryList();
  await loadData();
  nextTick(() => {
    const tableBody = document.querySelector(
      '.undistributedTagNumbersModel .ant-table-body'
    );
    if (tableBody) {
      tableBody.style.minHeight = '400px';
    }
  });
};

const loadData = async (arg) => {
  if (!url.list) {
    createMessage.error('请设置url.list属性!');
    return;
  }
  if (arg === 1) {
    ipagination.current = 1;
  }
  const queryParams = getQueryParams();
  delete queryParams.column;
  loading.value = true;
  try {
    const res = await defHttp.post(
      { url: url.list + '?pageNo=' + queryParams.pageNo + '&pageSize=' + queryParams.pageSize, data: queryParams },
      { isTransformResponse: false }
    );
    console.log(res)
    if (res.success) {
      dataSourceResult.value = res.result.records;
      ipagination.total = res.result.total;
      dataSourceResult.value.forEach(function (item) {
        item.rowKey = item.tagName + ',' + item.tagNum;
      });
      selectedRowKeys.value = [];
      selectionRows.value = [];
    }
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  closeTagEdit();
  ipagination.current = 1;
  ipagination.pageSize = 30;
  dataSourceResult.value = [];
  selectedRowKeys.value = [];
  selectionRows.value = [];
  queryParam.tagNameList = [];
  queryParam.platformType = type.value;
  visibleAssignment.value = false;
  emit('ok');
};

const handleOk = () => {
  handleCancel();
};

const openAssignment = (list) => {
  dataSourceResult.value = list;
};

const searchQuery = () => {
  loadData(1);
};

defineExpose({
  add,
  openAssignment,
});
</script>
<style></style>
<style lang="less" scoped></style>
