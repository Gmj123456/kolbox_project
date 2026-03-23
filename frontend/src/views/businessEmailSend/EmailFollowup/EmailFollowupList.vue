<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
            
              <a-select
                allowClear
                showSearch
                option-filter-prop="label"
                @change="handleContactEmailChange"
                v-model:value="queryParam.contactEmailId"
                placeholder="发送邮箱"
              >
                <a-select-option
                  v-for="item in sedEmailList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.contactEmail"
                  >{{ item.contactEmail }}</a-select-option
                >
              </a-select>
             </a-form-item>
          </a-col>

          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                allowClear
                showSearch
                option-filter-prop="label"
                v-model:value="queryParam.signatureId"
                placeholder="邮箱签名"
              >
                <a-select-option
                  v-for="item in signatureList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.signatureTitle"
                  >{{ item.signatureTitle }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select  allowClear showSearch v-model:value="queryParam.followUpCategoryId" placeholder="跟进类型"   option-filter-prop="label">
                <a-select-option v-for="item in brandCategoryList" :key="item.id" :value="item.id" :label="item.categoryName">
                  {{ item.categoryName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        
         
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-range-picker
                style="width: 100%"
                v-model:value="queryParam.emailPushDateRange"
                format="YYYY-MM-DD HH:mm"
                :show-time="{ format: 'YYYY-MM-DD HH:mm' }"
                :placeholder="['发送开始时间', '发送结束时间']"
              />
            </a-form-item>
          </a-col>

          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select allowClear v-model:value="queryParam.emailStatus" placeholder="状态" option-filter-prop="label">
                <a-select-option
                  v-for="item in followupEmailStatusList"
                  :key="item.value"
                  :value="item.value"
                  :label="item.text"
                >
                  {{ item.text }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-button type="primary" @click="searchQuery" :icon="h(SearchOutlined)">查询</a-button>
              <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px">重置</a-button>
              <a-button
                
                type="primary"
                style="margin-left: 8px"
                @click="handleBatchFollowUp"
              >
                一键发送邮件
              </a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    <div :style="{ height: sTableHeight + 'px' }">
      <vxe-table
        border="inner"
        :loading="loading"
        id="id"
        ref="vxeTableRef"
        height="auto"
        :data="dataSource"
        :row-config="{ keyField: 'id' }"
        :checkbox-config="{ reserve: true }"
        :expand-config="{ accordion: true, iconOpen: ' ', iconClose: ' ' }"
        :tooltip-config="{ contentMethod: showTooltipMethod }"
        size="small"
        :show-overflow="true"
        @checkbox-change="handleCheckboxChange"
        @checkbox-all="handleCheckboxChange"
      >
      <template #empty>
          <a-empty :image="simpleImage" />
      </template>
  
        <vxe-table-column type="checkbox" width="50" align="center"></vxe-table-column>
        <vxe-table-column type="seq" width="50" align="center" title="#"></vxe-table-column>
        <vxe-table-column
          field="sendEmail"
          title="发送邮箱"
          width="160"
        ></vxe-table-column>
        <vxe-table-column field="signatureTitle" width="160" title="邮箱签名">
          <template #default="{ row }">
            <span>{{ row.signatureTitle || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="ccEmails" width="160" title="抄送邮箱">
          <template #default="{ row }">
            <span>{{ row.ccEmails || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="brandCategory" width="120" title="品牌">
          <template #default="{ row }">
            <span>{{ row.brandCategory || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="followUpCategoryName" width="120" title="跟进类型">
          <template #default="{ row }">
            <span>{{ row.followUpCategoryName || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="email" width="160" title="品牌邮箱">
          <template #default="{ row }">
            <span>{{ row.email || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="emailTitle" miinWidth="160" title="邮件标题">
          <template #default="{ row }">
            <span>{{ row.emailTitle || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="emailContent" miinWidth="160"  title="邮件内容">
          <template #default="{ row }">
            <span>{{ stripHtmlTags(row.emailContent) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="planSendTime" width="160" title="发送时间">
          <template #default="{ row }">
            <span>{{ parseDate(row.planSendTime, "YYYY-MM-DD HH:mm") }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="followUpCount" width="80" title="跟进次数">
          <template #default="{ row }">
            <span v-if="row.followUpCount !== true">
              {{ row.followUpCount }} 次
            </span>
            <span v-else>
              {{ row.followUpCount }}
            </span>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="isOpened"
          width="80"
          title="是否打开"
          align="center"
        >
          <template #default="{ row }">
            <a-tag v-if="row.isOpened == 1" color="green">是</a-tag>
            <a-tag v-else color="orange">否</a-tag>
          </template>
        </vxe-table-column>
       
        <vxe-table-column field="openCount" width="80" title="打开次数">
          <template #default="{ row }">
            <span>{{ row.openCount || 0 }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="emailStatus" width="100" title="状态">
          <template #default="{ row }">
            <a-tag v-if="getEmailStatusLabel(row.emailStatus)" :color="getEmailStatusColor(row.emailStatus)">
              {{ getEmailStatusLabel(row.emailStatus) }}
            </a-tag>
            <span v-else>--</span>
          </template>
        </vxe-table-column>
        
        <vxe-table-column field="action" width="100" align="center" fixed="right" title="操作">
          <template #default="{ row }">
           
            <a-tooltip title="预览">
              <a @click="viewEmail(row)">
                <!-- <a-icon style="font-size: 15px" type="read" /> -->
                <span
                  style="font-size: 17px"
                  class="icon iconfont icon-chakan-copy"
                ></span>
              </a>
            </a-tooltip>
         
           
          
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <div class="vxe-page" style="display: flex; justify-content: flex-end;margin: 16px 0;">
      <a-pagination
        size="small"
        :showQuickJumper="true"
        :showTotal="pagination.showTotal"
        :current="pagination.current"
        :total="pagination.total"
        :pageSize="pagination.pageSize"
        @change="vxeTablePageChange"
        :disabled="loading"
        :pageSizeOptions="pagination.pageSizeOptions"
        showSizeChanger
  
      />
    </div>

    <EmailFollowupModal ref="emailFollowupModalRef" @ok="modalFormOk" />
    <viewEmailContentModal ref="viewEmailContentModalRef" />
  </a-card>
</template>

<script setup name="EmailFollowupList">
import { ref, onMounted, h } from 'vue';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { getDictItems } from '/@/api/common/api';
import { useTable } from '/@/components/useSTable/useSTable';
import { Empty } from 'ant-design-vue';
import EmailFollowupModal from './modules/EmailFollowupModal.vue';
import viewEmailContentModal from './modules/viewEmailContentModal.vue';
const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
import dayjs from 'dayjs';
import { Pagination } from 'ant-design-vue';
const APagination = Pagination;
// Refs
const viewEmailContentModalRef = ref(null);
const vxeTableRef = ref(null);
const emailFollowupModalRef = ref(null);
// Data
const sedEmailList = ref([]);
const signatureList = ref([]);
const brandCategoryList = ref([]);
const followupEmailStatusList = ref([]);

const url = {
  list: '/email/emailPushMain/followUpList',

};

// 多选
const selectedRows = ref([]);

// Use Table Hook
const fetchTableApi = async (params) => {
  // 处理日期范围参数
  if (params.emailPushDateRange && params.emailPushDateRange.length > 0) {
    params.sendTimeStart = params.emailPushDateRange[0]
      ? dayjs(params.emailPushDateRange[0]).format("YYYY-MM-DD HH:mm:00")
      : undefined;
    params.sendTimeEnd = params.emailPushDateRange[1]
      ? dayjs(params.emailPushDateRange[1]).format("YYYY-MM-DD HH:mm:59")
      : undefined;
  }
  delete params.emailPushDateRange;
  
  const res = await defHttp.get({ url: url.list, params });

  return res || { records: [], total: 0 };
};

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  vxeTablePageChange,
  fetchTable,
} = useTable(fetchTableApi,-50,);

function modalFormOk() {
  selectedRows.value = [];
  vxeTableRef.value?.clearCheckboxRow();
  fetchTable();
}

function showTooltipMethod({ column, row }) {
  const { property } = column;
  if (property === "ccEmails") {
    return row[property] ? row[property].replace(/,/g, "\n") : "";
  }
  // 其余的单元格使用默认行为
  return null;
}
function viewEmail(row) {
  viewEmailContentModalRef.value?.view(row);
}

function parseDate(value, format = "YYYY-MM-DD HH:mm") {
  if (!value) return "--";
  return dayjs(value).format(format);
}

function getEmailStatusLabel(value) {
  if (value === undefined || value === null) return '';
  const item = followupEmailStatusList.value.find((i) => String(i.value) === String(value));
  return item?.text || '';
}

function getEmailStatusColor(value) {
  const item = followupEmailStatusList.value.find((i) => String(i.value) === String(value));
  return item?.color || undefined;
}

async function initFollowupEmailStatus() {
  const res = await getDictItems('followup_email_status');
  followupEmailStatusList.value = res || [];
}

async function initBrandCategoryList() {
  const res = await defHttp.get({ url: "/email/emailTemplateCategory/listAll",params: { templateType:1 } });
  if (res) {
    brandCategoryList.value = res || [];
  }
}

function searchQuery() {
  fetchTable();
}

function searchReset() {
  Object.keys(queryParam.value).forEach(key => {
    queryParam.value[key] = undefined;
  });
  fetchTable();
}

function handleContactEmailChange(value) {
  signatureList.value = [];
  queryParam.value.signatureId = undefined;
  if (value) {
    initSignature();
  }
}

function handleCheckboxChange() {
  selectedRows.value = vxeTableRef.value?.getCheckboxRecords() || [];
}

function handleBatchFollowUp() {
  if (selectedRows.value.length === 0) return;
  emailFollowupModalRef.value?.open(selectedRows.value);
}

function stripHtmlTags(html) {
  if (!html) return "";
  const div = document.createElement("div");
  div.innerHTML = html;
  return div.textContent || div.innerText || "";
}

async function initEmail() {
  const res = await defHttp.get({ url: "/storeUserContactEmail/queryListByCounselor", params: {
    type: '1'
  } });
  if (res) {
    sedEmailList.value = res;
  }
}

async function initSignature() {
  const res = await defHttp.get({ 
    url: "/email/signature/getSignatureList", 
    params: {
      id: queryParam.value.contactEmailId,
    }
  });
  if (res) {
    signatureList.value = res;
  }
}

// Lifecycle hooks
onMounted(() => {
  initEmail();
  initBrandCategoryList();
  initFollowupEmailStatus();
  fetchTable();
});
</script>
<style scoped lang="less">
:deep(.vxe-table--render-default.border--inner .vxe-table--border-line){
  border: none !important;
}
:deep(.ant-tag) {
  margin-right: 0px !important;
}
:deep(.ant-calendar-input) {
  font-size: 12px !important;
}
/deep/ .vxe-header--column {
  background: #f0f3fe !important;
  color: #0b1019 !important;

  font-weight: 700 !important;
}
/deep/ .vxe-cell {
  color: #0b1019 !important;
}
</style>
