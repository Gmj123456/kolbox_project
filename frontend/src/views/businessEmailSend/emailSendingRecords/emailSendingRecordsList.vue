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
              <a-select  allowClear showSearch v-model:value="queryParam.brandCategoryId" placeholder="品牌类目"   option-filter-prop="label">
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
              <a-select  allowClear v-model:value="queryParam.isReply" placeholder="回复状态">
                <a-select-option :value="1">已回复</a-select-option>
                <a-select-option :value="2">自动回复</a-select-option>
                <a-select-option :value="0">未回复</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select allowClear v-model:value="queryParam.isSend" placeholder="发送状态">
                <a-select-option :value="0">等待发送</a-select-option>
                <a-select-option :value="1">发送成功</a-select-option>
                <a-select-option :value="-1">发送失败</a-select-option>
                <a-select-option :value="-2">取消发送</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
            
                  <a-button type="primary" @click="searchQuery" :icon="h(SearchOutlined)">查询</a-button>
                  <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px"
                    >重置</a-button
                  >
                  <a-button
                    @click="handleImport"
                    type="primary"
                    :icon="h(ImportOutlined)"
                    style="margin-left: 8px"
                    >导入</a-button
                  >
                  <a-button
                    @click="handleDownloadTemplate"
                    type="primary"
                    :icon="h(DownloadOutlined)"
                    style="margin-left: 8px"
                    >下载模板</a-button
                  >
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
        :expand-config="{ accordion: true, iconOpen: ' ', iconClose: ' ' }"
        :tooltip-config="{ contentMethod: showTooltipMethod }"
        size="small"
        :show-overflow="true"
      >
      <template #empty>
          <a-empty :image="simpleImage" />
        
       
      </template>
  
        <vxe-table-column type="expand" width="50">
          <template v-slot="{ row }">
            <div style="transform: translate(14px, -12px)">
              <div
                v-if="!vxeTableRef.isRowExpandByRow(row)"
                
                class="surely-table-row-expand-icon surely-table-row-expand-icon-collapsed"
                @click="toggleRowExpand(row, 'vxeTable')"
              />

              <div
                class="surely-table-row-expand-icon surely-table-row-expand-icon-expanded"
                v-if="vxeTableRef.isRowExpandByRow(row)"
                @click="toggleRowExpand(row, 'vxeTable')"
                type="minus-square"
              />
            </div>
          </template>
          <template #content="{ row, rowIndex }">
            <div style="margin-left: 50px">
              <vxe-table
                :data="row.innerData"
                :loading="innerLoading"
                size="small"
                :show-overflow="true"
                  max-height="250"
               
                border="inner"
              >
                <vxe-table-column type="seq" width="60" align="center" title="#"></vxe-table-column>
                <vxe-table-column field="brandName" title="品牌" width="120">
                  
                </vxe-table-column>
              
                <vxe-table-column field="email"  title="品牌邮箱" :show-overflow="true">
                  <template #default="{ row:innerRow }">
                    <span>{{ innerRow.email }}</span>
                   
       
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  field="brandCategoryId"
                  title="品牌类目"
                  width="150"
                >
                <template #default="{ row:innerRow }">
                  <span>{{ innerRow.brandCategory || "--" }}</span>
                </template>
                </vxe-table-column>
                <vxe-table-column
                  field="emailTitle"
                  title="邮件主题"
                  width="150"
                ></vxe-table-column>
              
                <vxe-table-column field="emailContent" title="邮件内容">
                  <template #default="{ row:innerRow }">
                    <span>{{ stripHtmlTags(innerRow.emailContent) }}</span>
                  </template>
                </vxe-table-column>
                <vxe-table-column field="planSendTime" width="130" title="预计发送时间">
                  <template #default="{ row:innerRow }">
                    <span>{{ parseDate(innerRow.planSendTime, "YYYY-MM-DD HH:mm") }}</span>
                  </template>
                </vxe-table-column>
                <vxe-table-column field="sendTime" width="130" title="实际发送时间">
                  <template #default="{ row:innerRow }">
                    <span>{{ parseDate(innerRow.sendTime, "YYYY-MM-DD HH:mm") }}</span>
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  field="isSend"
                  width="100"
                  title="发送状态"
                  align="center"
                >
                  <template #default="{ row:innerRow }">
                    <a-tag v-if="innerRow.isSend == 1" color="green">发送成功</a-tag>
                    <a-tag v-else-if="innerRow.isSend == 0" color="orange">等待发送</a-tag>
                    <a-tag v-else-if="innerRow.isSend == -1" color="red">发送失败</a-tag>
                    <a-tag v-else-if="innerRow.isSend == -2">取消发送</a-tag>
                    <a-tag v-else-if="innerRow.isSend == 10" color="blue">部分发送</a-tag>
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  field="isOpened"
                  width="100"
                  title="是否打开"
                  align="center"
                >
                  <template #default="{ row:innerRow }">
                    <a-tag v-if="innerRow.isOpened == 1" color="green">是</a-tag>
                    <a-tag v-else color="orange">否</a-tag>
                  </template>
                </vxe-table-column>
                <vxe-table-column field="openCount" width="80" title="打开次数">
                  <template #default="{ row:innerRow }">
                    <span>{{ innerRow.openCount || 0 }}</span>
                  </template>
                </vxe-table-column>
                <vxe-table-column field="errorMessage" width="100" title="失败原因">
                  <template #default="{ row:innerRow }">
                    <span>{{ innerRow.errorMessage || "--" }}</span>
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  field="isReply"
                  width="100"
                  title="是否回复"
                  align="center"
                >
                  <template #default="{ row:innerRow }">
                    <a-tag v-if="innerRow.isReply == 1" color="green">已回复</a-tag>
                    <a-tag v-else-if="innerRow.isReply == 2" color="green">自动回复</a-tag>
                    <a-tag v-else color="orange">未回复</a-tag>
                  </template>
                </vxe-table-column>
                <vxe-table-column field="action" width="175" title="操作" align="center">
                  <template #default="{ row:innerRow }">
                    <a-tooltip title="修改邮箱">
                      <a :disabled="innerRow.isSend == 1" @click="editEmail(innerRow)">
                        <a-icon style="font-size: 15px" type="form" />
                      </a>
                    </a-tooltip>
                    <a-divider type="vertical" />
                    <a-tooltip title="预览">
                      <a @click="viewEmail(innerRow)">
                        <!-- <a-icon style="font-size: 15px" type="read" /> -->
                        <span
                          style="font-size: 17px"
                          class="icon iconfont icon-chakan-copy"
                        ></span>
                      </a>
                    </a-tooltip>
                    <a-divider type="vertical" />
                    <a-tooltip title="邮件回复信息">
                      <a
                        :disabled="
                          (innerRow.isReply != 1 && innerRow.isReply != 2) || innerRow.isSend != 1
                        "
                        @click="replyEmail(innerRow)"
                      >
                        <!-- <a-icon type="mail" style="font-size: 15px"></a-icon> -->
                        <span
                          class="icon iconfont icon-xiaohuifu"
                          style="font-size: 16px"
                        ></span>
                      </a>
                    </a-tooltip>
                    <a-divider type="vertical" />
                    <a-popconfirm
                      placement="topLeft"
                      title="确定取消发送吗?"
                      @confirm="handleCancelSend(innerRow)"
                    >
                      <a-tooltip title="取消发送">
                        <!-- 问题：下面的 v-if 逻辑有错误，总是真值，应该用 && 而不是 ||，并且条件顺序应更清晰 -->
                        <a v-if="innerRow.isSend !== -2" :disabled="innerRow.isSend !== 0">
                          <a-icon style="font-size: 15px" type="close-circle" />
                        </a>
                      </a-tooltip>
                    </a-popconfirm>
                    <a-tooltip title="重新发送">
                      <a v-if="innerRow.isSend === -2" @click="handleResendDetail(innerRow)">
                        <span
                          style="font-size: 16px"
                          class="icon iconfont icon-zhongxinshenqing"
                        ></span>
                      </a>
                    </a-tooltip>
             
                  
                  </template>
                </vxe-table-column>
              </vxe-table>
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column type="seq" width="50" align="center" title="#"></vxe-table-column>
        <vxe-table-column
          field="sendEmail"
          title="发送邮箱"
          minWidth="160"
        ></vxe-table-column>
        <vxe-table-column field="signatureTitle" width="160" title="邮箱签名">
          <template #default="{ row }">
            <span>{{ row.signatureTitle || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="ccEmails" minWidth="160" title="抄送邮箱">
          <template #default="{ row }">
            <span>{{ row.ccEmails || "--" }}</span>
          </template>
        </vxe-table-column>
       
        <vxe-table-column field="sendIntervalMin" title="发送间隔" width="100">
          <template #default="{ row }">
            <span>{{ row.sendIntervalMin }} - {{ row.sendIntervalMax }} 分钟</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="emailPushDate" minWidth="150" title="发送时间">
          <template #default="{ row }">
            <span>{{ parseDate(row.emailPushDate, "YYYY-MM-DD HH:mm") }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="isSend" width="100" title="发送状态">
          <template #default="{ row }">
            <a-tag v-if="row.isSend == 99" color="green">发送成功</a-tag>
            <a-tag v-else-if="row.isSend == 0" color="orange">等待发送</a-tag>
            <a-tag v-else-if="row.isSend == -1" color="red">发送失败</a-tag>
            <a-tag v-else-if="row.isSend == -2">取消发送</a-tag>
            <a-tag v-else-if="row.isSend == 10" color="blue">部分发送</a-tag>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="totalSentCount"
          width="60"
          title="发送"
        ></vxe-table-column>
        <vxe-table-column
          field="actualSentCount"
          width="60"
          title="成功"
        ></vxe-table-column>
        <vxe-table-column field="bounceCount" width="60" title="失败"></vxe-table-column>
        <vxe-table-column field="openCount" width="60" title="打开"></vxe-table-column>
        <vxe-table-column field="replyCount" width="60" title="回复"></vxe-table-column>
        <vxe-table-column
          field="createTime"
          width="150"
          title="创建时间"
        ></vxe-table-column>
        <!-- <vxe-table-column field="remark" width="60" title="备注">
          <template #default="{ row }">
            <span>{{ row.remark || "--" }}</span>
          </template>
        </vxe-table-column> -->
        <vxe-table-column field="action" width="120" align="center" title="操作">
          <template #default="{ row }">
            <a-tooltip title="编辑">
              <a :disabled="row.isSend !== 0" @click="handleEdit(row)">
                <a-icon style="font-size: 15px" type="form" />
              </a>
            </a-tooltip>
            <a-divider type="vertical" />

            <a-popconfirm
              :disabled="row.isSend !== 0"
              placement="topLeft"
              title="确定取消当前邮件下所有待发送的邮件吗?"
              @confirm="handleCancelAllSend(row)"
            >
              <a-tooltip title="取消发送">
                <a v-if="row.isSend !== -2" :disabled="row.isSend !== 0">
                  <a-icon style="font-size: 15px" type="close-circle" />
                </a>
              </a-tooltip>
            </a-popconfirm>
            <a-tooltip title="重新发送">
              <a v-if="row.isSend == -2" @click="handleResend(row)">
                <span
                  style="font-size: 16px"
                  class="icon iconfont icon-zhongxinshenqing"
                ></span>
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-popconfirm
              placement="topLeft"
              title="确定删除吗?"
              @confirm="handleDelete(row.id)"
            >
              <a-tooltip title="删除">
                <a>
                  <span class="icon iconfont icon-shanchu"></span>
                </a>
              </a-tooltip>
            </a-popconfirm>
          
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
    <importSendEmailModal ref="importModalRef" @ok="modalFormOk"></importSendEmailModal>
    <viewEmailModal ref="viewEmailModalRef"></viewEmailModal>
    <editEmailModal ref="editEmailModalRef" @ok="modalFormOk"></editEmailModal>
    <replyEmailModal ref="replyModalRef" @ok="modalFormOk"></replyEmailModal>
    <onePrivateModal ref="onePrivateModalRef" @ok="modalFormOk"></onePrivateModal>
    <editSendEmailModal ref="editModalRef" @ok="modalFormOk"></editSendEmailModal>
  </a-card>
</template>

<script setup name="businessEmailSendRecordsList">
import { ref, reactive, computed, onMounted, nextTick, h } from 'vue';
import { SearchOutlined, ReloadOutlined, DownloadOutlined, ImportOutlined, FormOutlined, CloseCircleOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import importSendEmailModal from "./modules/importSendEmailModal.vue";
import { Empty } from 'ant-design-vue';
const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
import replyEmailModal from "./modules/replyEmailModal.vue";
import viewEmailModal from "./modules/viewEmailModal.vue";
import editSendEmailModal from "./modules/editSendEmailModal.vue";
import onePrivateModal from "./modules/onePrivateModal.vue";
import editEmailModal from "./modules/editEmailModal.vue";
import dayjs from 'dayjs';
import { Pagination } from 'ant-design-vue';
const APagination = Pagination;
const { createMessage } = useMessage();

// Refs 
const tableRef = ref(null);
const importModalRef = ref(null);
const replyModalRef = ref(null);
const editModalRef = ref(null);
const viewEmailModalRef = ref(null);
const onePrivateModalRef = ref(null);
const editEmailModalRef = ref(null);

// Data
const sedEmailList = ref([]);
const signatureList = ref([]);
const brandCategoryList = ref([]);
const currentRow = ref(undefined);
const innerLoading = ref(false);
const expandedRowKeys = ref([]);
const vxeTableRef = ref(null)
const url = {
  list: "/email/emailPushMain/businessList",
  delete: "/email/emailPushMain/delete",
  deleteBatch: "/email/emailPushMain/deleteBatch",
  exportXlsUrl: "/email/emailPushMain/exportXls",
  importExcelUrl: "/email/emailPushMain/importExcel",
};

// Use Table Hook
const fetchTableApi = async (params) => {
  // 处理日期范围参数
  if (params.emailPushDateRange && params.emailPushDateRange.length > 0) {
    params.emailPushDateStart = params.emailPushDateRange[0]
      ? dayjs(params.emailPushDateRange[0]).format("YYYY-MM-DD HH:mm:00")
      : undefined;
    params.emailPushDateEnd = params.emailPushDateRange[1]
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
} = useTable(fetchTableApi,-50,{
  afterFetch: emailSendingRecordsAfterFetch,
});
queryParam.value.businessType = '1'

async function emailSendingRecordsAfterFetch(res) {
  console.log(res,dataSource)
  console.log(expandedRowKeys.value)
  nextTick(() => {
    if(currentRow.value?.id){

      let current = dataSource.value.filter(
        (item) => item.id == currentRow.value.id
      )[0];
      vxeTableRef.value.setRowExpand(current, true);
      nextTick(() =>{
        getInnerData(currentRow.value.id);
      })
    }
  });
 
}

// 主表列定义
const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 60,
    align: 'center',
    customRender: ({ index }) => index + 1,
  },
  {
    title: '发送邮箱',
    dataIndex: 'sendEmail',
    minWidth: 160,
  },
  {
    title: '邮箱签名',
    dataIndex: 'signatureTitle',
    width: 160,
  },
  {
    title: '抄送邮箱',
    dataIndex: 'ccEmails',
    minWidth: 160,
    autoHeight:true
  },
  
  {
    title: '发送间隔',
    dataIndex: 'sendInterval',
    width: 100,
  },
  {
    title: '发送时间',
    dataIndex: 'emailPushDate',
    minWidth: 150,
  },
  {
    title: '发送状态',
    dataIndex: 'isSend',
    width: 100,
  },
  {
    title: '发送',
    dataIndex: 'totalSentCount',
    width: 60,
  },
  {
    title: '成功',
    dataIndex: 'actualSentCount',
    width: 60,
  },
  {
    title: '失败',
    dataIndex: 'bounceCount',
    width: 60,
  },
  {
    title: '打开',
    dataIndex: 'openCount',
    width: 60,
  },
  {
    title: '回复',
    dataIndex: 'replyCount',
    width: 60,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 150,
  },
  // {
  //   title: '备注',
  //   dataIndex: 'remark',
  //   width: 100,
  // },
  {
    title: '操作',
    key: 'action',
    width: 120,
    align: 'center',
  },
];

// 内部表格列定义
const innerColumns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 60,
    align: 'center',
    customRender: ({ index }) => index + 1,
  },
  {
    title: '网红账号',
    dataIndex: 'username',
    width: 120,
  },
  {
    title: '平台',
    dataIndex: 'platformType',
    align: 'center',
    width: 60,
  },
  {
    title: '网红邮箱',
    dataIndex: 'email',
    autoHeight:true
  },
  {
    title: '私有',
    dataIndex: 'isPrivate',
    width: 80,
    align: 'center',
  },
  {
    title: '邮件主题',
    dataIndex: 'emailTitle',
    width: 150,
  },
  {
    title: '邮件内容',
    dataIndex: 'emailContent',
  },
  {
    title: '预计发送时间',
    dataIndex: 'planSendTime',
    width: 130,
  },
  {
    title: '实际发送时间',
    dataIndex: 'sendTime',
    width: 130,
  },
  {
    title: '发送状态',
    dataIndex: 'isSend',
    width: 100,
    align: 'center',
  },
  {
    title: '是否打开',
    dataIndex: 'isOpened',
    width: 100,
    align: 'center',
  },
  {
    title: '打开次数',
    dataIndex: 'openCount',
    width: 80,
  },
  {
    title: '失败原因',
    dataIndex: 'errorMessage',
    width: 100,
  },
  {
    title: '是否回复',
    dataIndex: 'isReply',
    width: 100,
    align: 'center',
  },
  {
    title: '操作',
    key: 'action',
    width: 175,
    align: 'center',
  },
];
function showTooltipMethod({ type, column, row, items, _columnIndex }) {
  const { property } = column;
  if (property === "ccEmails") {
    return row[property] ? row[property].replace(/,/g, "\n") : "";
  }
  // 其余的单元格使用默认行为
  return null;
}
function toggleRowExpand(row, name) {
  vxeTableRef.value.toggleRowExpand(row);
  if (vxeTableRef.value.isRowExpandByRow(row)) {
    currentRow.value = row;
    nextTick(() =>{
      getInnerData(row.id);
    })
  } else {
    currentRow.value = undefined;
    // this.$set(this.currentRow, "innerData", []);
  }
}
// Methods
function editEmail(row) {
  console.log(row);
  editEmailModalRef.value?.edit(row);
}

function onePrivate(row) {
  console.log(row);
  onePrivateModalRef.value?.show(row);
}

function viewEmail(row) {
  console.log(  viewEmailModalRef.value)
  viewEmailModalRef.value?.view(row);
}

function parseDate(value, format = "YYYY-MM-DD HH:mm") {
  if (!value) return "--";
  return dayjs(value).format(format);
}

async function handleCancelSend(row) {
  console.log(row);
  try {
    const res = await defHttp.get({ 
      url: "/email/emailPushDetail/cancelSend", 
      params: { id: row.id } 
    });
    fetchTable();
    
  } catch (error) {
    console.error(error);
  }
}
async function initBrandCategoryList() {
  const res = await defHttp.get({ url: "/email/emailTemplateCategory/listAll",params: { templateType:0}  });
  if (res) {
    brandCategoryList.value = res || [];
  }
}

async function handleCancelAllSend(row) {
  console.log(row);
  try {
    const res = await defHttp.get({ 
      url: "/email/emailPushMain/batchCancel", 
      params: { id: row.id } 
    });
    fetchTable();
   
  } catch (error) {
    console.error(error);
  }
}

async function handleResend(row) {
  console.log(row);
  try {
    const res = await defHttp.get({ 
      url: "/email/emailPushMain/batchResend", 
      params: { id: row.id } 
    });
    fetchTable();
    
  } catch (error) {
    console.error(error);
  }
}

async function handleResendDetail(row) {
  console.log(row);
  try {
    const res = await defHttp.get({ 
      url: "/email/emailPushDetail/resend", 
      params: { id: row.id } 
    });
    if (res) {
     
      fetchTable();
    } 
  } catch (error) {
    console.error(error);
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
  console.log(value);
  signatureList.value = [];
  queryParam.value.signatureId = undefined;
  if (value) {
    initSignature();
  }
}

function handleEdit(record) {
  editModalRef.value?.show(record);
}

function handleDelete(id) {
  defHttp.delete({ url: url.delete, data: { id } },{ joinParamsToUrl: true }).then((res) => {
    fetchTable();
 
  });
}

function replyEmail(record) {
  console.log(record);
  replyModalRef.value?.edit(record);
}

function handleDownloadTemplate() {
  const url = '/商务邮件模版.xlsx'; // 注意：public 下的文件通过根路径访问
  const link = document.createElement('a');
  link.href = url;
  link.download = '商务邮件模版.xlsx'; // 用户保存时的默认文件名
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

function stripHtmlTags(html) {
  if (!html) return "";
  const div = document.createElement("div");
  div.innerHTML = html;
  return div.textContent || div.innerText || "";
}

function handleExpandChange(keys) {
  expandedRowKeys.value = keys;
}

async function handleExpand(expanded, record) {
  console.log(expanded, record);
  if (expanded) {
    if (!expandedRowKeys.value.includes(record.id)) {
      expandedRowKeys.value = [record.id]
    }
    await getInnerData(record.id);
  } else {
    expandedRowKeys.value = expandedRowKeys.value.filter((id) => id !== record.id);
  }
}

async function getInnerData(id) {
  console.log(id);
  innerLoading.value = true;
  const params = { ...queryParam.value };
  if (params.emailPushDateRange && params.emailPushDateRange.length > 0) {
    params.emailPushDateStart = params.emailPushDateRange[0]
      ? dayjs(params.emailPushDateRange[0]).format("YYYY-MM-DD HH:mm:00")
      : undefined;
    params.emailPushDateEnd = params.emailPushDateRange[1]
      ? dayjs(params.emailPushDateRange[1]).format("YYYY-MM-DD HH:mm:59")
      : undefined;
  }
  delete params.emailPushDateRange;
  try {
    const res = await defHttp.get({ 
      url: "/email/emailPushDetail/businessListByMainId", 
      params: {
        mainId: id,
        ...params,
      }
    });
    if (res) {
      const targetItem = dataSource.value.find((item) => item.id === id);
      if (targetItem) {
        targetItem.innerData = res;
        nextTick(() => {
          vxeTableRef.value?.recalculate();
        });
      }
    }
  } finally {
    innerLoading.value = false;
  }
}

function handleImport() {
  importModalRef.value?.show();
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

function modalFormOk() {
  fetchTable();
}

function parseBrandCategoryId(id) {
  return brandCategoryList.value.find((item) => item.id == id)?.categoryName || "--";
}

// Lifecycle hooks
onMounted(() => {
  initEmail();
  initBrandCategoryList();
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
