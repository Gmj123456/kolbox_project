<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
      <a-form  class="searchForm" @keyup.enter.native="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <JDictSelectPlatformType
                v-model:value="queryParam.platformType"
                dictCode="platform_type"
                :triggerChange="true"
                @change="onPlatformTypeChange"
              />
            </a-form-item>
          </a-col>

          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.celebrityName" placeholder="账号ID" />
            </a-form-item>
          </a-col>

          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                v-model:value="queryParam.countryName"
                show-search
                option-filter-prop="label"
                allow-clear
                placeholder="国家"
              >
                <a-select-option
                  v-for="(item, key) in countrys"
                  :key="key"
                  :value="item.country"
                  :label="item.country"
                >
                  {{ item.country }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.brandName" placeholder="品牌" />
            </a-form-item>
          </a-col>

          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.productName" placeholder="提报产品" />
            </a-form-item>
          </a-col>

          <a-col :xl="9" :lg="24" :md="24" :sm="24">
            <a-form-item>
              <a-select
             
                v-model:value="queryParam.kolTypes"
                style="width: 100%"
                :autoClearSearchValue="false"
                :not-found-content="fetching ? undefined : null"
                @search="fetchUser"
                :getPopupContainer="getPopupContainer"
                @change="handleChange"
                option-filter-prop="label"
                :maxTagCount="5"
                mode="multiple"
                allow-clear
                placeholder="商家达人类型"
                @popup-scroll="kolTypePopupScroll"
              >
                <template #maxTagPlaceholder>
                  <a-tooltip>
                    <template #title>
                      <div style="max-height: 200px; overflow-y: auto">
                        <div v-for="(item, i) in queryParam.kolTypes.slice(4)" :key="i">
                          <div>{{ item }}</div>
                        </div>
                      </div>
                    </template>
                    <span v-if="queryParam.kolTypes && queryParam.kolTypes.length > 4">
                      +{{ queryParam.kolTypes.length - 4 }}
                    </span>
                  </a-tooltip>
                </template>
                <a-spin v-if="fetching" size="small" />
                <a-select-option
                  v-for="item in kolTypes"
                  :key="item.kolType"
                  :value="item.kolType"
                  :label="item.kolType"
                >
                  {{ item.kolType }}
                </a-select-option>

                <!-- 虚拟滚动加载更多指示器 -->
                <a-select-option
                  v-if="kolTypeLoading && hasMoreTags"
                  key="loading"
                  disabled
                >
                  <a-spin size="small" />
                  <span style="margin-left: 8px">加载中...</span>
                </a-select-option>
              </a-select>
              <a-popover
                v-model:open="kolTypeVisible"
                :destroyTooltipOnHide="true"
                :getPopupContainer="(trigger) => trigger.parentNode"
                trigger="click"
                placement="bottomRight"
              >
                <template #content>
                  <div style="width: 270px;">
                  
                    <a-textarea
                      v-model:value="kolTypeValue"
                      
                      :auto-size="{ minRows: 6, maxRows: 6 }"
                      placeholder="精确输入，一行一项"
                    />

                    <div
                      v-if="noKolType"
                      style="color: red; margin-top: 5px; height: 100px; overflow-y: auto"
                    >
                      不存在的商家达人类型：
                      <div
                        v-for="item in nonExistentKolType"
                        :key="item"
                        style="margin-right: 5px"
                      >
                        {{ item }}
                      </div>
                    </div>
                    <div style="margin-top: 10px;display: flex;justify-content: space-between">
                          <a-button @click="closeKolTypeEdit" style="margin-right: 50px">
                            关闭
                          </a-button>
                      <div>
                        <a-button @click="clearKolType" style="margin-right: 8px">
                          清空
                        </a-button>
                        <a-button @click="kolTypeSerch(kolTypeValue)">确定</a-button>
                      </div>
                    </div>
                  </div>
                </template>
                <div
                  style="
                    position: absolute;
                    right: 24px;
                    top: 3.5px;
                    width: 24px;
                    height: 24px;
                    background-color: #f0f2f5;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    cursor: pointer;
                  "
                >
                  <AlignLeftOutlined
                    class="tagIcon"
                    :style="{ color: kolTypeVisible ? 'blue' : '' }"
                    @click="kolTypeVisible = !kolTypeVisible"
                  />
                </div>
              </a-popover>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
            <a-button :icon="h(ReloadOutlined)" style="margin-left: 8px" @click="searchReset">重置</a-button>
            <a-button v-if="hasPermission('historicalReporting:async')" :icon="h(SyncOutlined)" style="margin-left: 8px" @click="syncData">同步数据</a-button>
            <a-button :icon="h(CopyOutlined)" type="primary" style="margin-left: 8px" @click="copyData">复制</a-button>
            </a-form-item>
          
          </a-col>
        </a-row>
      </a-form>
   

    <!-- table区域-begin -->
    <s-table
      ref="tableRef"
        :rangeSelection="false"
      :scroll="{ y: sTableHeight, x: '100%' }"
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :expandedRowKeys="expandedRowKeys"
      :selectedRowKeys="selectedRowKeys"
      :row-selection="rowSelection"
      @change="pageChange"
      @expand="onExpand"
       :animate-rows="false"
    >
      <template #expandedRowRender="{ record }">
        <div style="margin-left: 40px;">
          
          <s-table
          :rangeSelection="false"
            :columns="innerColumns"
            :data-source="record.innerData"
            :loading="innerLoading"
            :pagination="false"
            size="small"
            rowKey="id"
            class="historicalReportingInnerTable"
          >
            <template #bodyCell="{ column, record: innerRow, index }">
              <template v-if="column.key === 'index'">
                  {{ index + 1 }}
                </template>
              <template v-else-if="column.dataIndex === 'productName'">
                <span v-if="innerRow.productName">
                  {{ innerRow.brandName }}-{{ innerRow.productName }}
                </span>
                <span v-else>--</span>
              </template>
              <template v-else-if="column.dataIndex === 'submitDate'">
                <span>{{ innerRow.submitDate || "--" }}</span>
              </template>
              <template v-else-if="column.dataIndex === 'deliveryContent'">
                <span>{{ innerRow.deliveryContent || "--" }}</span>
              </template>
              <template v-else-if="column.dataIndex === 'kolType'">
                <a-tag>{{ innerRow.kolType }}</a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'celebrityRemark'">
                <EllipsisTooltip :text="innerRow.celebrityRemark || '--'"/>

              </template>
              <template v-else-if="column.dataIndex === 'isSelected'">
                <a-tag v-if="innerRow.isSelected == 1" color="green">是</a-tag>
                <a-tag v-else-if="innerRow.isSelected == 2" color="orange">也许</a-tag>
                <a-tag v-else>否</a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'partyAFeedback'">
                <EllipsisTooltip :text="innerRow.partyAFeedback || '--'"/>

              </template>
              <template v-else-if="column.dataIndex === 'cooperationResult'">
                <span>{{ innerRow.cooperationResult || "--" }}</span>
              </template>
            </template>
          </s-table>
        </div>
      </template>

      <template #bodyCell="{ column, record, index }">
          <template v-if="column.key === 'index'">
            {{ index + 1 }}
          </template>
        <template v-else-if="column.dataIndex === 'celebrityName'">
          {{ record.celebrityName }}
        </template>
        <template v-else-if="column.dataIndex === 'platformType'">
          <img
            v-if="record.platformType == 1"
            style="width: 22px; height: 22px"
            src="@/assets/images/yt.png"
            alt="YouTube"
          />
          <img
            v-if="record.platformType == 2"
            style="width: 22px; height: 22px"
            src="@/assets/images/tk.png"
            alt="TikTok"
          />
          <img
            v-if="record.platformType == 0"
            style="width: 22px; height: 22px"
            src="@/assets/images/ins.png"
            alt="Instagram"
          />
        </template>
        <template v-else-if="column.dataIndex === 'gender'">
          <span>
            <img
              src="@/assets/images/man.png"
              v-show="record.gender == '男'"
              style="width: 22px; height: 22px"
              alt=""
            />
            <img
              src="@/assets/images/woman.png"
              v-show="record.gender == '女'"
              style="width: 22px; height: 22px"
              alt=""
            />
          </span>
        </template>
        <template v-else-if="column.dataIndex === 'countryName'">
          {{ record.countryName }}
        </template>
        <template v-else-if="column.dataIndex === 'followersNum'">
          <span>{{ getFollower(record.followersNum) || "--" }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'playAvgNum'">
          <span>{{ getFollower(record.playAvgNum) || "--" }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'kolTypes'">
          <div
            v-if="record.kolTypes"
            style="
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              display: block;
              max-width: 100%;
            "
          >
            <a-tag
              v-for="(item, idx) in record.kolTypes.split(',')"
              :key="item"
              :style="{ marginLeft: idx > 0 ? '4px' : '0' }"
            >
              {{ item }}
            </a-tag>
          </div>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.dataIndex === 'productName'">
          <span v-if="record.productName">{{ record.brandName }}-{{ record.productName }}</span>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.dataIndex === 'lastSubmitDate'">
          <span>{{ record.lastSubmitDate || "--" }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'webUrl'">
          <a :href="record.webUrl" target="_blank">{{ record.webUrl || "--" }}</a>
        </template>
      </template>
    </s-table>
  </a-card>
</template>

<script setup name="historicalReportingList">
import { ref, onMounted, nextTick, onUnmounted,h } from 'vue';
import { 
  SearchOutlined, 
  ReloadOutlined, 
  SyncOutlined,
  AlignLeftOutlined,
  CopyOutlined,
} from '@ant-design/icons-vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { useTable } from '/@/components/useSTable/useSTable';
import JDictSelectPlatformType from '@/components/jeecg/JDictSelectPlatformType.vue';
import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
const { createMessage } = useMessage();
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
// Refs
const tableRef = ref(null);

// 查询参数
const {
  loading,
  dataSource,
  pagination,
  queryParam,
  fetchTable,
  rowSelection,
  selectedRowKeys,
  searchQuery: searchQueryHook,
  searchReset: searchResetHook,
  sTableHeight,
  pageChange
} = useTable(loadDataApi, 0, {
  beforeFetch: (params) => {
    // 处理 kolTypes 参数
    if (Array.isArray(queryParam.value.kolTypes) && queryParam.value.kolTypes.length > 0) {
      params.kolTypes = queryParam.value.kolTypes.join(",");
    }
    return params;
  },
  afterFetch: historicalReportingListAfterFetch,
});
// 选中的key和表格数据请确保在setup中声明，如 selectedRowKeys/dataSource
const copyData = async () => {
  if (selectedRowKeys.value.length === 0) {
    createMessage.warning("请选择要复制的达人");
    return;
  }
  const headers = ["账号ID", "主页链接"];
  const headerRow = headers.join("\t");
  const dataRows = dataSource.value
    .filter((item) => selectedRowKeys.value.includes(item.id))
    .map((item) => `${item.celebrityName}\t${item.webUrl}`);
  const tsv = [headerRow, ...dataRows].join("\n");

  if (navigator.clipboard && typeof navigator.clipboard.writeText === "function") {
    try {
      await navigator.clipboard.writeText(tsv);
      createMessage.success("复制成功！");
    } catch (err) {
      console.error("Clipboard write failed:", err);
      createMessage.error("复制失败，请手动复制。");
    }
  } else {
    fallbackCopyText(tsv);
  }
};

const fallbackCopyText = (text) => {
  const textarea = document.createElement("textarea");
  textarea.value = text;
  textarea.style.position = "fixed";
  textarea.style.opacity = "0";
  textarea.style.left = "-9999px";
  document.body.appendChild(textarea);
  textarea.select();
  try {
    const successful = document.execCommand("copy");
    if (successful) {
      createMessage.success("复制成功");
    } else {
      createMessage.error("复制失败，请手动复制。");
    }
  } catch (err) {
    createMessage.error("复制失败，请手动复制。");
  }
  document.body.removeChild(textarea);
};
async function historicalReportingListAfterFetch(res, dataSource) {
  console.log(expandedRowKeys.value)
  selectedRowKeys.value = []
  if (expandedRowKeys.value.length > 0) {
    await getInnerData(expandedRowKeys.value[0]);
  }
}

// 表格相关数据
const innerLoading = ref(false);
const expandedRowKeys = ref([]);

// 国家数据
const countrys = ref([]);

// 商家达人类型相关数据
const fetching = ref(false);
const kolTypeVisible = ref(false);
const kolTypes = ref([]);
const noKolType = ref(false);
const nonExistentKolType = ref([]);
const kolTypeValue = ref("");

// 虚拟滚动相关变量
const tagPageSize = 50; // 每页加载的标签数量
const tagCurrentPage = ref(1); // 当前标签页
const allKolTypes = ref([]); // 存储所有标签数据
const hasMoreTags = ref(true); // 是否还有更多标签
const kolTypeLoading = ref(false); // 标签加载状态
const lastScrollTop = ref(0); // 记录上次滚动位置
const lastFetchId = ref(0);

// 主表列定义
const columns = [
  {
    title: '#',
    key: 'index',
    width: 60,
    align: 'center',
  },
  {
    title: '账号ID',
    dataIndex: 'celebrityName',
    width: 150,
  },
  {
    title: '平台',
    dataIndex: 'platformType',
    width: 60,
    align: 'center',
  },
  {
    title: '性别',
    dataIndex: 'gender',
    width: 60,
    align: 'center',
  },
  {
    title: '国家',
    dataIndex: 'countryName',
    width: 100,
  },
  {
    title: '粉丝',
    dataIndex: 'followersNum',
    width: 130,
  },
  {
    title: '均播',
    dataIndex: 'playAvgNum',
    width: 130,
  },
  {
    title: '商家达人类型',
    dataIndex: 'kolTypes',
    ellipsis: true,
  },
  {
    title: '最近提报产品',
    dataIndex: 'productName',
    width: 200,
  },
  {
    title: '最近提报时间',
    dataIndex: 'lastSubmitDate',
    width: 150,
  },
  {
    title: '主页链接',
    dataIndex: 'webUrl',
    width: 350,
    ellipsis: true,
  },
];

// 内表列定义
const innerColumns = [
  {
    title: '#',
    key: 'index',
    width: 60,
    align: 'center',
  },
  {
    title: '提报产品',
    dataIndex: 'productName',
  },
  {
    title: '提报时间',
    dataIndex: 'submitDate',
    width: 150,
  },
  {
    title: '交付内容',
    dataIndex: 'deliveryContent',
    width: 150,
  },
  {
    title: '商家达人类型',
    dataIndex: 'kolType',
  },
  {
    title: '达人备注',
    dataIndex: 'celebrityRemark',
  },
  {
    title: '是否选中',
    dataIndex: 'isSelected',
    width: 100,
    align: 'center',
  },
  {
    title: '甲方反馈',
    dataIndex: 'partyAFeedback',
  },
  {
    title: 'BD',
    dataIndex: 'celebrityCounselorName',
    width: 100,
  },
  {
    title: '合作结果',
    dataIndex: 'cooperationResult',
    width: 150,
    align: 'center',
  },
];

// API 函数
async function loadDataApi(params) {
  const res = await defHttp.get({
    url: '/history/kolHistoryCelebrity/list',
    params
  });
  return res;
}

// 获取内表数据
async function getInnerData(id) {
  innerLoading.value = true;
  try {
    const res = await defHttp.get({
      url: "/history/kolHistoryCelebrityDetail/queryByConditions",
      params: {
        celebrityId: id,
      }
    });
    
    if (res) {
      const targetItem = dataSource.value.find((item) => item.id === id);
      if (targetItem) {
        res.forEach((item) => {
          item.loading = false;
          item.isOpen = false;
        });
        targetItem.innerData = res;
      }
    }
  } finally {
    innerLoading.value = false;
  }
}

// 获取国家列表
async function initCountry() {
  const res = await defHttp.get({ url: "/tiktokcountry/getCountryList" });
  countrys.value = res;
}
initCountry();
fetchTable();

// 清理事件监听
onUnmounted(() => {
});

// 展开行处理
function onExpand(expanded, record) {
  if (expanded) {
    expandedRowKeys.value = [record.id];
    getInnerData(record.id);
  } else {
    expandedRowKeys.value = [];
  }
}

// 搜索查询
function searchQuery() {
  searchQueryHook();
}

// 重置搜索
function searchReset() {
  searchResetHook();
  // 重置额外的状态
  kolTypes.value = [];
  allKolTypes.value = [];
  kolTypeValue.value = "";
  noKolType.value = false;
  nonExistentKolType.value = [];
}

// 同步数据
async function syncData() {
  try {
    const res = await defHttp.post({ url: "/history/kolHistoryCelebrityDetail/importFeishuExcel" });
    fetchTable(1);
   
  } catch (error) {
    console.error('同步数据失败:', error);
    createMessage.error('同步数据失败');
  }
}

// 获取粉丝数格式化
function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    const kValue = (num / 1000).toFixed(0);
    return parseFloat(kValue) % 1 === 0
      ? `${parseInt(kValue)}K`
      : `${parseFloat(kValue)}K`;
  } else if (num >= 1000000) {
    const mValue = (num / 1000000).toFixed(0);
    return parseFloat(mValue) % 1 === 0
      ? `${parseInt(mValue)}M`
      : `${parseFloat(mValue)}M`;
  } else if (num > 0 && num < 1000) {
    return num;
  } else {
    return "--";
  }
}

// 关闭达人类型编辑
function closeKolTypeEdit() {
  kolTypeVisible.value = false;
  clearKolType();
  noKolType.value = false;
}

// 清空达人类型
function clearKolType() {
  kolTypeValue.value = "";
  noKolType.value = false;
}

// 搜索达人类型
async function kolTypeSerch(value) {
  let allKolType = value.trim().split("\n");
  const arr = [];
  for (let index = 0; index < value.trim().split("\n").length; index++) {
    const el = allKolType[index];
    arr.push(el.trim());
  }
  allKolType = [...arr];
  let filterKolTypes = [];
  
  try {
    const res = await defHttp.get({
      url: "/history/kolHistoryKolType/listAll",
      params: {
        kolTypes: allKolType.join("|"),
      }
    });
    
    filterKolTypes = res.map((item) => item.kolType);
    nonExistentKolType.value = allKolType.filter((item) => {
      return !filterKolTypes.includes(item);
    });
    
    if (nonExistentKolType.value.length > 0) {
      noKolType.value = true;
    } else {
      queryParam.value.kolTypes = allKolType;
      kolTypeVisible.value = false;
      noKolType.value = false;
    }
  } catch (error) {
    console.error('搜索达人类型失败:', error);
    createMessage.error('搜索达人类型失败');
  }
}

// 获取弹出容器
function getPopupContainer(triggerNode) {
  return triggerNode.parentNode;
}

// 处理选择变化
function handleChange(value) {
  queryParam.value.kolTypes = value;
  
  // 保持下拉框打开状态
  if (value.length === 0) {
    kolTypes.value = [];
  }
}

// 重置标签分页状态
function resetTagPagination() {
  tagCurrentPage.value = 1;
  kolTypes.value = [];
  hasMoreTags.value = true;
  kolTypeLoading.value = false;
  lastScrollTop.value = 0;
}

// 初始化标签分页
function initTagPagination() {
  if (!allKolTypes.value || !Array.isArray(allKolTypes.value)) {
    console.warn("标签数据格式不正确，重置数据");
    return false;
  }

  if (allKolTypes.value.length === 0) {
    kolTypes.value = [];
    hasMoreTags.value = false;
    return;
  }

  // 加载第一页数据
  const firstPageTags = allKolTypes.value.slice(0, tagPageSize);
  kolTypes.value = firstPageTags;

  // 检查是否还有更多数据
  hasMoreTags.value = allKolTypes.value.length > tagPageSize;
  tagCurrentPage.value = 1;
}

// 加载更多标签数据
async function loadMoreTags() {
  if (kolTypeLoading.value || !hasMoreTags.value) return;

  // 验证数据完整性
  if (!allKolTypes.value || !Array.isArray(allKolTypes.value)) {
    return;
  }

  kolTypeLoading.value = true;

  try {
    // 等待 DOM 更新完成
    await nextTick();

    // 计算下一页的起始位置
    const startIndex = tagCurrentPage.value * tagPageSize;
    const endIndex = startIndex + tagPageSize;

    // 从所有标签中截取当前页的数据
    const newTags = allKolTypes.value.slice(startIndex, endIndex);

    if (newTags.length > 0) {
      // 将新数据添加到当前显示的标签中
      kolTypes.value = [...kolTypes.value, ...newTags];
      tagCurrentPage.value++;

      // 检查是否还有更多数据
      hasMoreTags.value = endIndex < allKolTypes.value.length;
    } else {
      hasMoreTags.value = false;
    }
  } catch (error) {
    console.error("加载更多标签失败:", error);
    // 发生错误时，标记为没有更多数据，避免无限重试
    hasMoreTags.value = false;
  } finally {
    kolTypeLoading.value = false;
  }
}

// 标签虚拟滚动相关方法
function kolTypePopupScroll(e) {
  const { target } = e;
  const { scrollTop, scrollHeight, clientHeight } = target;

  // 记录滚动方向
  const isScrollingDown = scrollTop > (lastScrollTop.value || 0);
  lastScrollTop.value = scrollTop;

  // 只在向下滚动且接近底部时加载更多
  if (isScrollingDown && !kolTypeLoading.value && hasMoreTags.value) {
    // 计算滚动位置
    const scrollBottom = scrollTop + clientHeight;
    const isNearBottom = scrollHeight - scrollBottom < 100; // 距离底部100px时触发加载

    if (isNearBottom) {
      loadMoreTags();
    }
  }
}

// 获取用户数据
async function fetchUser(value) {
  if (value) {
    lastFetchId.value += 1;
    const fetchId = lastFetchId.value;

    // 重置虚拟滚动状态
    resetTagPagination();

    fetching.value = true;

    try {
      const res = await defHttp.get({
        url: "/history/kolHistoryKolType/listAll",
        params: {
          kolType: value,
        }
      });
      
      if (fetchId !== lastFetchId.value) {
        // for fetch callback order
        return;
      }
      
      if (loading.value) {
        fetching.value = false;
        return;
      }

      // 存储所有标签数据
      allKolTypes.value = res || [];

      // 初始化虚拟滚动
      initTagPagination();

      fetching.value = false;
    } catch (error) {
      console.error('获取用户数据失败:', error);
      fetching.value = false;
    }
  } else {
    kolTypes.value = [];
  }
}

// 平台类型改变
function onPlatformTypeChange(value) {
  queryParam.value.platformType = value != null ? value.toString() : undefined;
}

</script>
<style>

  .historicalReportingInnerTable .surely-table-cell-content{
    padding: 8px !important;
  }
</style>
<style scoped lang="less">
:deep(.ant-select-selection--multiple .ant-select-selection__rendered > ul > li) {
  max-width: 110px !important;
}

:deep(.ant-tag) {
  margin-right: 0px !important;
}

:deep(.ant-calendar-input) {
  font-size: 12px !important;
}

</style>
