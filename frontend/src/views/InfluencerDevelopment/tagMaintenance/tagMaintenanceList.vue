<template>
  <a-card :bordered="false">
    <a-form  class="searchForm" @keyup.enter="searchQuery">
      <a-row :gutter="12">
        <a-col :xl="3" :lg="6" :md="8" :sm="24">
          <a-form-item>
            <JDictSelectPlatformType @change="platformTypeChange" v-model:value="queryParam.platformType" dictCode="platform_type" />
          </a-form-item>
        </a-col>
        <!-- <a-col :xl="3" :lg="6" :md="8" :sm="24">
          <a-form-item>
            <a-select
              v-model:value="queryParam.platformType"
              show-search
              allowClear
              placeholder="平台类型"
              option-filter-prop="label"
            >
              <a-select-option v-for="item in platformTypeList" :key="item.value" :value="item.value" :label="item.text">
                {{ item.text }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col> -->
        <a-col :xl="2" :lg="6" :md="8" :sm="24">
          <a-form-item>
            <a-select
              v-model:value="queryParam.tagType"
              show-search
              allowClear
              placeholder="标签类型"
              option-filter-prop="label"
            >
              <a-select-option v-for="item in tagTypeList" :key="item.value" :value="item.value" :label="item.text">
                {{ item.text }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :xl="2" :lg="8" :md="12" :sm="24">
          <a-form-item>
            <a-input v-model:value="queryParam.tagName" allowClear placeholder="标签名称" />
          </a-form-item>
        </a-col>
        <a-col :xl="5" :lg="10" :md="12" :sm="24">
          <a-form-item>
            <a-input-group compact>
              <a-select
                v-model:value="queryParam.brandId"
                style="width: 120px;border-right: 0px;"
                show-search
                allowClear
                placeholder="品牌"
                option-filter-prop="label"
                :dropdownMatchSelectWidth="false"
                :dropdown-style="{ width: '350px' }"
                @change="onBrandChange"
              >
                <a-select-option v-for="item in brandList" :key="item.id" :value="item.id" :label="item.brandName">
                  {{ item.brandName }}
                </a-select-option>
              </a-select>
              <a-select
                v-model:value="queryParam.productId"
                style="width: calc(100% - 120px)"
                show-search
                allowClear
                placeholder="产品"
          
                option-filter-prop="label"
              >
                <a-select-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.text">
                  {{ item.text }}
                </a-select-option>
              </a-select>
            </a-input-group>
          </a-form-item>
        </a-col>
        <a-col :xl="12" :lg="12" :md="24" :sm="24">
          <a-form-item>
          
            <div class="table-toolbar">
              <ElPopover
                ref="mainFilterPopoverRef"
                popper-class="more-filter-popover"
                placement="bottom-start"
                :width="650"
                trigger="click"
                @show="onPopoverShow"
                @hide="onPopoverHide"
              >
                <template #default>
                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-content_menu">
                        <div
                          class="more-filter-content_menu_item"
                          :class="{
                            selected:
                              filterType === item.value || filterType === item.typeId,
                          }"
                          v-for="item in filterOptions"
                          :key="item.typeId"
                          @click="selectFilter(item)"
                        >
                          <span>{{ item.typeName }}</span>
                          <span>
                            <span
                              v-if="getAttributeCount(item.typeId) > 0"
                              class="selected-count"
                            >
                              {{ getAttributeCount(item.typeId) }}
                            </span>
                            <RightOutlined style="color: #606266" />
                          </span>
                        </div>
                      </div>
                      <div class="more-filter-form">
                        <ElCascaderPanel
                          v-model="attributeIdsMap[filterType]"
                          :options="options"
                          :props="{ multiple: true, value: 'id', label: 'attributeName' }"
                        ></ElCascaderPanel>
                      </div>
                    </div>
                    <div class="more-filter-footer">
                      <a-button
                        type="primary"
                        :icon="h(SafetyCertificateOutlined)"
                        style="margin-right: 8px"
                        @click="applyAllFilters(false)"
                        >应用</a-button
                      >
                      <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                        >查询</a-button
                      >
                    </div>
                  </div>
                </template>
                <template #reference>
                  <a-button
                    :class="
                      mainFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
                    "
                  >
                    更多筛选
                    <DownOutlined class="rotating-arrow-filter-icon" />
                  </a-button>
                </template>
              </ElPopover>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery" style="margin-left: 8px">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" @click="handleSearchReset" style="margin-left: 8px">重置</a-button>
              <a-button type="primary" :icon="h(PlusOutlined)" @click="handleAdd" style="margin-left: 8px">新增</a-button>
              <a-button :icon="h(AppstoreAddOutlined)" type="primary" :disabled="!selectedRowKeys.length"  @click="bindProduct" style="margin-left: 8px">绑定产品</a-button>
              <a-button  type="primary"  :icon="h(CopyOutlined)"  :disabled="!selectedRowKeys.length" @click="copyTags" style="margin-left: 8px">批量复制</a-button>
             
              <a-button  style="margin-left: 8px" v-if="hasPermission('tag:import')" @click="tagTemplate">标签模版地址</a-button>
              <a-button
                type="primary"
                :icon="h(SyncOutlined)"
              
                style="margin-left: 8px"
                @click="syncData"
                >同步标签信息</a-button
              >
            </div>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="12"      v-if="queryParam.subCategoryIds">
        <!-- 显示已选择的筛选条件 -->
        <a-col :span="24">
          <a-form-item v-if="queryParam.subCategoryIds">
            <template v-for="item in filterOptions" :key="item.typeId">
              <ElPopover
                :ref="(el) => setAttributeFilterPopoverRef(el, item.typeId)"
                popper-class="more-filter-popover"
                placement="bottom-start"
                :width="400"
                trigger="click"
                @show="onAttributePopoverShow(item.typeId)"
                @hide="onAttributePopoverHide(item.typeId)"
              >
                <template #default>
                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <ElCascaderPanel
                        v-model="attributeIdsMap[item.typeId]"
                        :options="item.data"
                        :props="{ multiple: true, value: 'id', label: 'attributeName' }"
                      ></ElCascaderPanel>
                    </div>
                    <div class="more-filter-footer">
                      <a-button
                        :icon="h(SafetyCertificateOutlined)"
                        type="primary"
                        style="margin-right: 8px"
                        @click="applyAllFilters(false)"
                        >应用</a-button
                      >
                      <a-button
                       :icon="h(SearchOutlined)"
                        type="primary"
                        @click="applyAllFilters(true)"
                        >查询</a-button
                      >
                    </div>
                  </div>
                </template>
                <template #reference>
                  <div
                    v-show="isShowAttribute(item.data)"
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span>{{ item.typeName }}:</span>
                    <span>{{
                      parseAttributeIds(queryParam.subCategoryIds, item.data)
                    }}</span>
                    <span
                      style="margin-left: 6px"
                      :class="
                        attributeFilterPopoverVisible[item.typeId]
                          ? 'main-filter-open'
                          : 'main-filter-closed'
                      "
                    >
                      <DownOutlined class="rotating-arrow-filter-icon" />
                    </span>
                    <span>
                      <a-icon
                        @click.stop.prevent="clearAttributeFilter($event, item.typeId)"
                        @mousedown.stop.prevent
                        type="close-circle"
                        theme="filled"
                      />
                    </span>
                  </div>
                </template>
              </ElPopover>
            </template>
            <div
              v-show="queryParam.subCategoryIds"
              class="clear-all-button"
              @click="clearAllFilters"
            >
              <span>全部清除</span>
            </div>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

    <a-table
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :rangeSelection="false"
      :row-selection="atableRowSelection"
      :scroll="{ y: sTableHeight }"
    
      
      @change="pageChange"
    >
      <template #bodyCell="{ record, column,index}">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'tagName'">
          <div class="tag-name-cell">
            <a  @click="handleCopy(record.tagName)" style="margin-right: 4px">
              <JEllipsis :value="(record.tagName || '').toLowerCase()" :length="14" />
            </a>
            <a v-if="record.tagName" class="copy-icon"  @click="handleCopy(record.tagName)">
              <CopyOutlined />
            </a>
          </div>
          <div class="tag-meta">
           <img
                style="width: 16px; height: 16px; margin-right: 4px"
                :src="platformTypeImg(record.platformType)"
                alt=""
              />
            <span>{{ getTagTypeText(record.tagType) }}</span>
          </div>
        </template>
        <template v-else-if="column.key === 'brandName'">
          {{ record.brandName || '--' }}
        </template>
        <template v-else-if="column.key === 'productData'">
          <template v-if="record.productData">
          
        
          <div class="product-brief">
            <div
              v-for="(item, index) in parseProductData(record.productData).slice(0, 2)"
              :key="index"
            >
              {{ item }}

              <template v-if="record.productData">
              <a-popover >
                <template #content>
                  <div class="product-popover">
                    <!-- <div v-for="(item, index) in parseProductDataArr(record.productData)" :key="index">
                      {{ item.brandName }} - {{ item.productName }}
                    </div> -->
                    <a-table :columns="productColumns" size="small" :data-source="parseProductDataArr(record.productData)" :pagination="false">
                     
                      
                    </a-table>
                  </div>
                </template>
                
                <span
                  style="display: inline-block;"
                  v-if="
                    index === 1 &&
                    parseProductData(record.productData).length > 2
                  "
                >
                  +{{ parseProductData(record.productData).length - 2 }}
                </span>
              </a-popover>
          </template>
            </div>
          </div>
          </template>
          
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'platformType'">
          {{ getPlatformTypeText(record.platformType) }}
        </template>
        <template v-else-if="column.key === 'tagType'">
          {{ getTagTypeText(record.tagType) }}
        </template>
        <template v-else-if="column.key === 'tagStatus'">
          <a-tag v-if="record.tagStatus === 0" color="cyan">未开始</a-tag>
          <a-tag v-else-if="record.tagStatus === 10" color="pink">执行中</a-tag>
          <a-tag v-else-if="record.tagStatus === 90" color="red">中断完成</a-tag>
          <a-tag v-else-if="record.tagStatus === 99" color="blue">执行成功</a-tag>
          <a-tag v-else-if="record.tagStatus === -403" color="red">执行异常</a-tag>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'tagViews'">
          {{ getFollower(record.tagViews) }}
        </template>
        <template v-else-if="column.key === 'tagPostsCount'">
          {{ getFollower(record.tagPostsCount) }}
        </template>
        <template v-else-if="['totalKolsJson', 'allocatedKolsJson', 'unallocatedKolsJson', 'withEmailKolsJson'].includes(column.key)">
          <template v-if="record[column.dataIndex]">
            <a-popover>
              <template #content>
                <a-table
                  :columns="kolCountColumns"
                  :data-source="parseCount(record[column.dataIndex])"
                  size="small"
                  :pagination="false"
                
                />
              </template>
              <a>{{ getCountTotal(record[column.dataIndex]) }}</a>
            </a-popover>
          </template>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'lastExecutionTime'">
          <div>{{ record.importTime ? parseTime(record.importTime) : '--' }}</div>
          <div>{{ record.lastExecutionTime ? parseTime(record.lastExecutionTime) : '--' }}</div>
        </template>
        <template v-else-if="categoryTypeList.some(cat => cat.id === column.dataIndex)">
             <template v-if="record.dataList && record.dataList.length">
              <div>
                <a-popover placement="top">
                  <template #content>
                    <div style="max-width: 200px; ">
                      <a-table
                      size="small"
                      :columns="attrColumns"
                      :data-source="
                        record.dataList[0].list
                      "
                 
                      :pagination="false"
                      :bordered="false"
                      :scroll="{
                          x:150,
                          y:200
                        }"
                    >
                   
                    </a-table>
                    </div>
                  </template>
                  <span
                    style="
                      max-width: calc(100% - 26px);
                      overflow: hidden;
                      text-overflow: ellipsis;
                      white-space: nowrap;
                      display: inline-block;
                    "
                  >
                  <a-tag  v-for="(categoryItem, index) in record.dataList[0].list" style="margin-bottom: 0px;background-color: #fff" :key="index">
                    {{ categoryItem.attributeName }}
                  </a-tag>
                   
                  </span>
                </a-popover>
              </div>
            </template>
            <span v-else>--</span>
          </template>
        <template v-else-if="column.key === 'videoUrl'">
          <a
            v-if="record.videoUrl"
            :href="record.videoUrl"
            target="_blank"
            rel="noopener noreferrer"
          >
            <JEllipsis :value="record.videoUrl" :length="18" />
          </a>
          <span v-else>--</span>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-tooltip title="编辑">
            <a  style="font-size: 15px;" @click="handleEdit(record)">
              <FormOutlined />
              
            </a>
          </a-tooltip>
              <a-divider type="vertical" />
          <a-tooltip title="重置状态">
            <a  style="font-size: 15px;" @click="handleResetStatus(record)">
              <RedoOutlined />
            
            </a>
          </a-tooltip>
        </template>
        <template v-else>
          {{ formatValue(record[column.dataIndex]) }}
        </template>
      </template>
    </a-table>
    <div style="display: flex; align-items: center; justify-content: space-between; position: absolute; bottom: 32px;">
      <div>
        已选择 <a>{{ selectedRowKeys.length }}</a> 个标签
      </div>
     
    </div>


    <TiktokTagsModal ref="tiktokTagsModalRef" @ok="modalFormOk" />
    <AssociationTagsModal ref="associationTagsModalRef" @ok="modalFormOk" />
    <bindProductModal ref="bindProductModalRef" @ok="bindProductModalOk" />
  </a-card>
</template>

<script setup name="tagMaintenanceList">
import { ref, computed, onMounted, h, nextTick } from 'vue';
import dayjs from 'dayjs';
import { useMessage } from '/@/hooks/web/useMessage';
import { getDictItems } from '/@/api/common/api';
import { useTable } from '/@/components/useSTable/useSTable';
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DownOutlined,
  CopyOutlined,
  FormOutlined,
  RedoOutlined,
  SyncOutlined,
  AppstoreAddOutlined,
  RightOutlined,
  SafetyCertificateOutlined,
} from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { ElCascaderPanel, ElPopover } from 'element-plus';
import JDictSelectPlatformType from "@/components/jeecg/JDictSelectPlatformType.vue";
import TiktokTagsModal from './modules/TiktokTagsModal.vue';
import AssociationTagsModal from './modules/AssociationTagsModal.vue';
import JEllipsis from '/@/components/Form/src/jeecg/components/JEllipsis.vue';
import bindProductModal from './modules/bindProductModal.vue';
import { copyTextToClipboard } from '@/hooks/web/useCopyToClipboard';
import {
  fetchTagListApi,
  getBrandListApi,
  getProductListApi,
  queryTagsByIdsApi,
} from './tagMaintenanceApi.ts';
import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
const { createMessage } = useMessage();



const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  rowSelection,
  selectedRowKeys,
  updatePagination,
  searchQuery,
  searchReset,
  pageChange,
  calcTableHeight,
  atableRowSelection
} = useTable(fetchTagListApi,-10,{tableType:'aTble',afterFetch:tagMaintenanceAfterFetchFn});
const attrColumns = ref([
  {
    title: "#",
    dataIndex: "",
    width: 40,
    key: "index",
    align: "center",
    customRender: function ({index}) {
      return parseInt(index) + 1;
    },
  },
  {

    title: "达人类型",
    dataIndex: "attributeName",
    key: "attributeName",
  },
])
queryParam.value = {
  platformType: '2',
  tagType: undefined,
  tagName: undefined,
  brandId: undefined,
  productId: undefined,
  subCategoryIds: undefined,
};

const platformTypeList = ref([]);
const tagTypeList = ref([]);
const brandList = ref([]);
const productList = ref([]);
const tiktokTagsModalRef = ref();
const associationTagsModalRef = ref();
const bindProductModalRef = ref();
const copying = ref(false);
const productCategoryList = ref([]);
const categoryTypeList = ref([]);
const socialMediaCategoryList = ref([]);
const dynamicColumnsAdded = ref(false); // 用于跟踪是否已添加动态列
const tagGroupList = ref([]);
const tagTypeAllList = ref([]);

// 更多筛选相关数据
const filterOptions = ref([]);
const filterType = ref('');
const options = ref([]);
const attributeIdsMap = ref({});
const mainFilterPopoverVisible = ref(false);
const attributeFilterPopoverVisible = ref({});
const isApplyClose = ref(false);
const backupData = ref({
  attributeIdsMap: {},
  filterData: {
    attributeSelections: {},
  },
});
const mainFilterPopoverRef = ref(null);
const attributeFilterPopoverRefs = ref({});
// 修改 columns 为响应式引用而不是 computed 属性
const tableColumns = ref([
  { title: '#', dataIndex: 'index',key:'index',width: 40, },
  { title: '标签名称', dataIndex: 'tagName', key: 'tagName', width: 150, autoHeight:true},
  { title: '历史品牌', dataIndex: 'brandName', key: 'brandName', width: 130,autoHeight:true },
  { title: '品牌/产品', dataIndex: 'productData', key: 'productData', width: 160 },
  // { title: '平台类型', dataIndex: 'platformType', key: 'platformType', width: 120 },
  { title: '标签状态', dataIndex: 'tagStatus', key: 'tagStatus', width: 80 },
  { title: '标签用户数', dataIndex: 'tagViews', key: 'tagViews', width: 100 },
  { title: '标签帖子数', dataIndex: 'tagPostsCount', key: 'tagPostsCount', width: 100 },
  { title: '网红数', dataIndex: 'totalKolsJson', key: 'totalKolsJson', width: 100 },
  { title: '已分配网红数', dataIndex: 'allocatedKolsJson', key: 'allocatedKolsJson', width: 120 },
  { title: '未分配网红数', dataIndex: 'unallocatedKolsJson', key: 'unallocatedKolsJson', width: 120 },
  { title: '有邮箱网红数', dataIndex: 'withEmailKolsJson', key: 'withEmailKolsJson', width: 120 },
  { title: '上传/最后执行时间', dataIndex: 'lastExecutionTime', key: 'lastExecutionTime', width: 150 },
  { title: '视频链接', dataIndex: 'videoUrl', key: 'videoUrl', width: 150 },
  { title: '操作', key: 'action', width: 80,  align: 'center',fixed:'right' },
]);

// 动态列添加到表格列中
const columns = computed(() => {
  if (!dynamicColumnsAdded.value || categoryTypeList.value.length === 0) {
    return tableColumns.value;
  }
  
  // 找到视频GMV列的索引
  const videoGmvIndex = tableColumns.value.findIndex(col => col.key === 'videoUrl');
  if (videoGmvIndex === -1) return tableColumns.value;
  
  // 创建新的列数组
  const newColumns = [...tableColumns.value];
  console.log(categoryTypeList.value)
  // 生成动态列
  const dynamicColumns = categoryTypeList.value.map(item => ({
    title: item.typeName,
    width: 250,
    dataIndex:item.id,
    key: item.id,
    typeId: item.id,
    typeCode: item.typeCode,
  }));
  
  // 在视频GMV列之后插入动态列
  newColumns.splice(videoGmvIndex + 1, 0, ...dynamicColumns);
  
  return newColumns;
});
const productColumns = [
  { title: '#', dataIndex: 'index', width: 50,key: 'index',align: 'center',customRender: function ({index}) {
    return parseInt(index) + 1;
  } },
  { title: '品牌名称', dataIndex: 'brandName', width: 120,key: 'brandName' },
  { title: '产品名称', dataIndex: 'productName', width: 120,key: 'productName' },
];
const kolCountColumns = [
  { title: '#', dataIndex: '_index', width: 50 },
  { title: '国家', dataIndex: 'countryName', width: 120 },
  { title: '数量', dataIndex: 'quantity', width: 80 },
];
updatePagination({
  defaultPageSize: 50,           // 默认每页条数
  pageSize: 50,                  // 当前每页条数
  pageSizeOptions: ['50', '100', '200'],  // 每页条数选项
  current: 1,                    // 当前页码
  total: 0,                      // 总条数
  showQuickJumper: true,         // 是否显示快速跳转
  showSizeChanger: true,         // 是否显示每页条数选择器
  showTotal: (total, range) => { // 自定义显示总数
    return `${range[0]}-${range[1]} 共${total}条`
  }

})
function tagMaintenanceAfterFetchFn(params,res) {

  nextTick(() => {
    calcTableHeight('aTble');
  });
}
async function syncData() {
 const res = await defHttp.get({url:"/kol/feishu/importTagsData"},{
    isTransformResponse: false,
  });
  if (res.success) {
    createMessage.success(res.message);
    fetchTable();
  } else {
    createMessage.error(res.message);
  }
}
function bindProductModalOk(){
  selectedRowKeys.value = [];
   fetchTable();
}
const platformTypeChange = (e) => {
  console.log(e);
  queryParam.value.platformType = e != null ? e.toString() : undefined;
  const matchedGroup = tagGroupList.value.find((group) => group.title == e);
  console.log(matchedGroup);

  if (matchedGroup && matchedGroup.value) {
    tagTypeList.value = matchedGroup.value
      .split(",")
      .map((j) => {
        return tagTypeAllList.value.find((item) => item.value == j.trim());
      })
      .filter((item) => item !== undefined); // 排除找不到的项
  } else {
    tagTypeList.value = []; // 默认为空数组
  }
  if (tagTypeList.value.length == 1) {
    queryParam.value.tagType = tagTypeList.value[0].value;
  } else {
    queryParam.value.tagType = undefined;
  }
  console.log(tagTypeList.value)
};
function attrData(text) {
  return text.split(",").map((item) => ({
    category: item,
  }));
}
function parseCategoryText(id, row) {
  if (row.dataList) {
    row.dataList[0].list
    // const filterList = row.dataList.filter((item) => item.typeId == id);
    // console.log(filterList)
    // if (filterList.length > 0) {
    //   return filterList
    //     .map((item) => item.list.map((category) => category.attributeName).join(", "))
    //     .join(", ");
    // }
    return "";
  } else {
    return "";
  }
}
function platformTypeImg(text) {
  switch (text) {
    case 0:
      return new URL("@/assets/images/ins.png", import.meta.url).href;
    case 1:
      return new URL("@/assets/images/yt.png", import.meta.url).href;
    case 2:
      return new URL("@/assets/images/tk.png", import.meta.url).href;
    default:
      return '';
  }
}
function handleCopy(text) {
  const success = copyTextToClipboard(text);
    if (success) {
     createMessage.success('复制成功！');
    } else {
     createMessage.error('复制失败！');
    }
    return success;
}

function formatDict(res) {
  if (Array.isArray(res)) {
    return res;
  }
  if (Array.isArray(res?.result)) {
    return res.result;
  }
  return [];
}

async function initDictData() {
  try {
    const [platformRes, tagRes,groupRes] = await Promise.all([
      getDictItems('platform_type'),
      getDictItems('tag_type'),
      getDictItems("tag_group")
    ]);
    platformTypeList.value = formatDict(platformRes);
    tagTypeAllList.value = formatDict(tagRes);
    tagGroupList.value = formatDict(groupRes);
    platformTypeChange(queryParam.value.platformType);
  } catch (error) {
    console.error('获取字典失败', error);
  }
}

async function initBrandList() {
  try {
    const res = await getBrandListApi();
    brandList.value = Array.isArray(res) ? res : res?.result || [];
  } catch (error) {
    console.error('获取品牌失败', error);
  }
}

async function initProductList(brandId) {
  if (!brandId) {
    productList.value = [];
    return;
  }
  try {
    const res = await getProductListApi({ brandId });
    const list = Array.isArray(res) ? res : res?.result || [];
    productList.value = list.map((item) => ({
      ...item,
      text: item.productName,
    }));
  } catch (error) {
    console.error('获取产品失败', error);
  }
}

function onBrandChange(value) {
  queryParam.value.brandId = value;
  queryParam.value.productId = undefined;
  initProductList(value);
}

function getPlatformTypeText(value) {
  const item = platformTypeList.value.find((dict) => dict.value == value);
  return item ? item.text : '--';
}

function getTagTypeText(value) {
  const item = tagTypeAllList.value.find((dict) => dict.value == value);
  return item ? item.text : '--';
}

function parseProductDataArr(text) {
  if (!text) return [];
  try {
    return JSON.parse(text);
  } catch (error) {
    return [];
  }
}

function parseProductData(text) {
  return parseProductDataArr(text).map((item) => `${item.brandName}-${item.productName}`);
}

function parseTime(time) {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '--';
}

function parseCount(text) {
  if (!text) return [];
  try {
    const parsed = JSON.parse(text);
    const list = parsed?.countryCounts || [];
    return list.map((item, index) => ({
      ...item,
      _index: index + 1,
    }));
  } catch (error) {
    return [];
  }
}

function getCountTotal(text) {
  if (!text) return '--';
  try {
    const parsed = JSON.parse(text);
    return parsed?.total ?? '--';
  } catch (error) {
    return '--';
  }
}

function getFollower(num) {
  if (typeof num !== 'number') return '--';
  if (num >= 1_000_000) {
    return `${(num / 1_000_000).toFixed(1).replace(/\\.0$/, '')}M`;
  }
  if (num >= 1_000) {
    return `${(num / 1_000).toFixed(1).replace(/\\.0$/, '')}K`;
  }
  return num > 0 ? String(num) : '--';
}

function formatValue(value) {
  if (value === null || value === undefined || value === '') {
    return '--';
  }
  return value;
}

function handleAdd() {
  tiktokTagsModalRef.value?.add?.();
}

function handleEdit(record) {
      const row = Object.assign({}, record);
  row.tagTypeName = getTagTypeText(row.tagType);
  tiktokTagsModalRef.value?.edit?.(row);
}

function modalFormOk() {
  fetchTable(1);
}

function handleResetStatus(record) {
  defHttp.put({
    url: '/kol/tags/editStatus',
    params: { id: record.id },
  }).then((res) => {
    console.log(res);
    fetchTable();
   
  });
}

async function copyTags() {
  if (!selectedRowKeys.value.length) {
    createMessage.warning('请选择需要复制的标签');
    return;
  }
  copying.value = true;
  try {
    const res = await queryTagsByIdsApi(selectedRowKeys.value);
    const message = res.result
    if (!message) {
      createMessage.warning('暂无可复制的标签');
      return;
    }
    const text = message.replace(/\|/g, '\n');
    await copyToClipboard(text);
    createMessage.success('标签已复制到剪贴板');
  } catch (error) {
    console.error(error);
    createMessage.error('复制失败');
  } finally {
    copying.value = false;
  }
}

function copyToClipboard(text) {
  if (navigator?.clipboard) {
    return navigator.clipboard.writeText(text);
  }
  const textarea = document.createElement('textarea');
  textarea.readOnly = true;
  textarea.style.position = 'absolute';
  textarea.style.left = '-9999px';
  textarea.value = text;
  document.body.appendChild(textarea);
  textarea.select();
  document.execCommand('copy');
  document.body.removeChild(textarea);
  return Promise.resolve();
}

function bindProduct() {
  if (!selectedRowKeys.value.length) {
    createMessage.warning('请选择要绑定的标签');
    return;
  }
  bindProductModalRef.value?.edit(selectedRowKeys.value);
}

async function tagTemplate() {
  try {
    const res = await defHttp.get({
      url: '/kolsysuserfeishusheet/queryByUser',
      params: { spreadSheetType: 'tag' },
    });
    if (res?.success && res.result?.spreadSheetUrl) {
      window.open(res.result.spreadSheetUrl, '_blank');
    } else {
      createMessage.error(res?.message || '未拥有权限，请联系管理员');
    }
  } catch (error) {
    console.error(error);
    createMessage.error('获取标签模版失败');
  }
}

function handleSearchReset() {
  queryParam.value = {
    
  };
  productList.value = [];
  selectedRowKeys.value = [];
  queryParam.value.platformType = '2';
  platformTypeChange(queryParam.value.platformType);
  
  // 重置更多筛选中的数据
  attributeIdsMap.value = {};
  filterType.value = '';
  if (queryParam.value.subCategoryIds) {
    delete queryParam.value.subCategoryIds;
  }
  
  fetchTable(1);
}

async function initProductCategory() {
  try {
    const res = await defHttp.get({ url: "/kol/category/loadTreeDataAll" });
    if (res) {
      productCategoryList.value = res;
    }
  } catch (error) {
    console.error("获取产品分类失败:", error);
  }
}

async function initCategoryTypeList() {
  try {
    // 并行获取两个接口数据
    const [res, categoryRes] = await Promise.all([
      defHttp.get({ url: "/kol/kolAttributeType/listAll" }),
      defHttp.get({ url: "/kol/attribute/getKolAttribute" })
    ]);

    if (res && categoryRes) {
      // 过滤出类型为2的数据作为categoryTypeList
      categoryTypeList.value = res.filter((item) => item.type == 2);
      
      // 处理社交媒体分类列表
      socialMediaCategoryList.value = res.map((typeItem) => {
        // 查找是否存在匹配的数据
        const existingItem = categoryRes.find(
          (item) => item.typeId === typeItem.id
        );
        
        if (existingItem) {
          // 如果存在，使用现有数据并添加必要属性
          return {
            ...existingItem,
            selectedValues: [],
            typeName: typeItem.typeName,
          };
        } else {
          return {
            data: [],
            selectedValues: [],
            typeName: typeItem.typeName,
            typeId: typeItem.id,
          };
        }
      });
      
      // 初始化产品分类
      await initProductCategory();
      
      // 初始化属性列表（需要在 socialMediaCategoryList 初始化后调用）
      await initAttributeList();
      
      // 添加动态列
      addDynamicColumns();
      
      // 重新加载表格数据
      fetchTable(1);
    }
  } catch (error) {
    console.error("获取数据失败:", error);
  }
}

function addDynamicColumns() {
  // 设置标志位表示已添加动态列
  dynamicColumnsAdded.value = true;
  // 由于columns是computed属性，会自动重新计算
}

// 更多筛选相关方法
function setAttributeFilterPopoverRef(el, typeId) {
  if (el) {
    attributeFilterPopoverRefs.value[typeId] = el;
  }
}

function onPopoverShow() {
  isApplyClose.value = false;
  mainFilterPopoverVisible.value = true;

  // 第一次打开时默认选中左侧菜单第一个选项
  if (!filterType.value && filterOptions.value.length > 0) {
    const firstOption = filterOptions.value[0];
    filterType.value = firstOption.typeId;
    options.value = parseData(firstOption.data);
  }

  // 备份当前所有数据
  backupData.value = {
    attributeIdsMap: JSON.parse(JSON.stringify(attributeIdsMap.value)),
    filterData: JSON.parse(JSON.stringify({ attributeSelections: {} })),
  };
}

function onPopoverHide() {
  mainFilterPopoverVisible.value = false;

  // 重置 filterType 和 options，确保下次打开时能重新应用默认选择逻辑
  filterType.value = '';
  options.value = [];

  // 如果不是通过应用按钮关闭，则恢复备份数据
  if (!isApplyClose.value) {
    attributeIdsMap.value = JSON.parse(
      JSON.stringify(backupData.value.attributeIdsMap)
    );
  }
  // 重置标记
  isApplyClose.value = false;
}

function selectFilter(item) {
  // 保存当前筛选条件的数据
  saveCurrentFilterData();

  // 切换到新的筛选条件
  filterType.value = item.typeId;
  options.value = parseData(item.data);
}

function saveCurrentFilterData() {
  if (filterType.value && attributeIdsMap.value[filterType.value]) {
    attributeIdsMap.value[filterType.value] = [...attributeIdsMap.value[filterType.value]];
  }
}

function applyAllFilters(isSearch) {
  // 标记为通过应用按钮关闭
  isApplyClose.value = true;

  // 先保存当前筛选条件的数据
  saveCurrentFilterData();

  // 收集所有筛选类型的attributeIds
  let allAttributeIds = [];
  Object.keys(attributeIdsMap.value).forEach((typeId) => {
    if (attributeIdsMap.value[typeId] && Array.isArray(attributeIdsMap.value[typeId])) {
      const flatIds = Array.isArray(attributeIdsMap.value[typeId])
        ? attributeIdsMap.value[typeId].flat()
        : [];
      allAttributeIds = allAttributeIds.concat(flatIds);
    }
  });

  // 去重并设置到查询参数
  const uniqueAttributeIds = [...new Set(allAttributeIds)];
  if (uniqueAttributeIds.length > 0) {
    queryParam.value.subCategoryIds = uniqueAttributeIds.join(',');
  } else {
    delete queryParam.value.subCategoryIds;
  }

  if (isSearch) {
    selectedRowKeys.value = [];
    fetchTable(1);
  }
  // calcTableHeight('aTble');
  // 关闭所有弹窗
  closeAllPopovers();
 
}

function closeAllPopovers() {
  // 关闭主筛选弹窗
  if (mainFilterPopoverRef.value) {
    mainFilterPopoverRef.value.hide?.();
  }

  // 关闭属性筛选弹窗
  Object.keys(attributeFilterPopoverRefs.value).forEach((typeId) => {
    const popoverRef = attributeFilterPopoverRefs.value[typeId];
    if (popoverRef) {
      if (Array.isArray(popoverRef) && popoverRef[0]) {
        popoverRef[0].hide?.();
      } else if (typeof popoverRef.hide === 'function') {
        popoverRef.hide();
      }
    }
  });
}

function getAttributeCount(typeId) {
  if (attributeIdsMap.value[typeId]) {
    return attributeIdsMap.value[typeId].length;
  }
  return 0;
}

function isShowAttribute(data) {
  if (queryParam.value.subCategoryIds && data) {
    const arr = queryParam.value.subCategoryIds.split(',');
    if (data.find((item) => arr.includes(item.id))) {
      return true;
    }
  }
  return false;
}

function parseAttributeIds(ids, data) {
  if (ids && data) {
    const arr = ids.split(',');
    const res = data.filter((item) => arr.includes(item.id));
    return res.map((item) => item.attributeName).join(',');
  }
  return '';
}

function parseData(data) {
  if (!data) return [];
  return data.map((item) => ({
    id: item.id,
    attributeName: item.attributeName,
  }));
}

function clearAllFilters() {
  closeAllPopovers();
  nextTick(() => {
    attributeIdsMap.value = {};
    if (queryParam.value.subCategoryIds) {
      delete queryParam.value.subCategoryIds;
    }
    fetchTable(1);
  });
}

function clearAttributeFilter(event, attributeType) {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
    event.stopImmediatePropagation();
  }
  closeAllPopovers();
  
  // 清除特定属性类型的数据
  attributeIdsMap.value[attributeType] = [];

  // 重新计算attributeIds
  let allAttributeIds = [];
  Object.keys(attributeIdsMap.value).forEach((typeId) => {
    if (attributeIdsMap.value[typeId] && Array.isArray(attributeIdsMap.value[typeId])) {
      const flatIds = Array.isArray(attributeIdsMap.value[typeId])
        ? attributeIdsMap.value[typeId].flat()
        : [];
      allAttributeIds = allAttributeIds.concat(flatIds);
    }
  });

  const uniqueAttributeIds = [...new Set(allAttributeIds)];
  if (uniqueAttributeIds.length > 0) {
    queryParam.value.subCategoryIds = uniqueAttributeIds.join(',');
  } else {
    delete queryParam.value.subCategoryIds;
  }

  fetchTable(1);
}

function onAttributePopoverShow(typeId) {
  attributeFilterPopoverVisible.value[typeId] = true;
}

function onAttributePopoverHide(typeId) {
  attributeFilterPopoverVisible.value[typeId] = false;
}

async function initAttributeList() {
  try {
    const res = await defHttp.get({ url: '/kol/attribute/loadTreeDataAll' });
    if (res) {
      filterOptions.value = res.map((item) => ({
        ...item,
        data: item.data.map((dataItem) => ({
          ...dataItem,
          children: null,
        })),
      }));

      // 根据 socialMediaCategoryList 中的 typeName 对 filterOptions 进行排序
      filterOptions.value.sort((a, b) => {
        const aIndex = socialMediaCategoryList.value.findIndex(
          (item) => item.typeId === a.typeId
        );
        const bIndex = socialMediaCategoryList.value.findIndex(
          (item) => item.typeId === b.typeId
        );

        if (aIndex !== -1 && bIndex !== -1) {
          return aIndex - bIndex;
        }
        if (aIndex !== -1 && bIndex === -1) {
          return -1;
        }
        if (aIndex === -1 && bIndex !== -1) {
          return 1;
        }
        return 0;
      });
    }
  } catch (error) {
    console.error('获取属性列表失败:', error);
  }
}

onMounted(() => {
  initDictData();
  initBrandList();
  initCategoryTypeList();
  // initAttributeList 会在 initCategoryTypeList 中调用
  // fetchTable(1);
});
// fetchTable()
</script>

<style scoped lang="less">
/* 更多筛选相关样式 */
:deep(.el-cascader-menu) {
  width: 100% !important;
}
:deep(.el-cascader-panel) {
  height: 100% !important;
  width: 100% !important;
  border: none !important;
  height: 291px !important;
}
:deep(.el-cascader-menu__wrap) {
  height: 100% !important;
}
.more-filter-popover {
  padding: 0 !important;
}
.more-filter-container {
  display: flex;
  flex-direction: column;
  .more-filter-content {
    flex: 1;
    display: flex;
    overflow-y: auto;
    .more-filter-form {
      width: 100%;
      overflow-x: auto;
    }
    .more-filter-content_menu {
      width: 200px;
      height: 100%;
      border-right: 1px solid #d9d9d9;
      .more-filter-content_menu_item {
        width: 100%;
        display: flex;
        align-items: center;
        color: #121415;
        cursor: pointer;
        justify-content: space-between;
        padding: 6px 12px;
        transition: all;

        &:hover,
        &.selected {
          background-color: #f2f8ff;
        }
      }
    }
  }
  .more-filter-footer {
    align-items: center;
    border-top: 1px solid #d9d9d9;
    display: flex;
    justify-content: flex-end;
    padding: 8px 16px;
    padding-bottom: 0px;
    padding-top: 12px;
  }
}

/* 选中个数样式 */
.selected-count {
  display: inline-block;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  background-color: @primary-color;
  color: white;
  border-radius: 8px;
  font-size: 12px;
  margin-right: 8px;
  padding: 0 4px;
}

.more-filter-button span:first-child {
  color: #969696;
}
.more-filter-button span:last-child {
  margin-left: 8px;
}
.more-filter-button {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 15px;
  display: inline-block;
  align-items: center;
  cursor: pointer;
  height: 32px;
  line-height: 32px;
  font-size: 12px;
  background-color: #fff;
  text-align: center;
}

:deep(.more-filter-button span) {
  display: inline-block !important;
  max-width: 250px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}
:deep(.more-filter-button span:nth-child(2)) {
  font-weight: 600;
}
:deep(.ant-tag) {
  margin-bottom: 4px;
}

.rotating-arrow-filter-icon {
  color: rgba(0, 0, 0, 0.25);
  transition: transform 0.3s ease;
  transform-origin: center center;
}
.main-filter-open .rotating-arrow-filter-icon {
  transform: rotate(180deg);
}
.main-filter-closed .rotating-arrow-filter-icon {
  transform: rotate(0deg);
}

.clear-all-button {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 15px;
  display: inline-block;
  align-items: center;
  cursor: pointer;
  height: 32px;
  line-height: 32px;
  font-size: 12px;
  background-color: #fff;
  text-align: center;
  span {
    display: inline-block !important;
    max-width: 350px !important;
    overflow: hidden !important;
    text-overflow: ellipsis !important;
    white-space: nowrap !important;
    line-height: 32px;
    font-weight: 600;
  }
}
</style>
 
