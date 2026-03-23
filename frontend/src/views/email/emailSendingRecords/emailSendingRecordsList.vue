<template>
  <div class="emailSendingRecordsList">
    <!-- 查询区域 -->
    <div style="display: flex;gap: 10px;margin-bottom: 12px;">
      <div class="email-summary-item" >
        <div class="email-summary-item-title">总发送量</div>
        <div>
          <span class="email-summary-item-value">{{ emailAllData.totalSentCount }}</span>

        </div>
      </div>
      <div class="email-summary-item" >
        <div class="email-summary-item-title">总成功量</div>
        <div>
          <span class="email-summary-item-value">{{ emailAllData.actualSentCount }}</span>
          <span>（{{ emailAllData.sendSuccessRate }}%）</span>
        </div>
      </div>
      <div class="email-summary-item" >
        <div class="email-summary-item-title">总失败量</div>
        <div>
          <span class="email-summary-item-value">{{ emailAllData.bounceCount }}</span>
          <span>（{{ emailAllData.bounceRate }}%）</span>
        </div>
      </div>
      <div class="email-summary-item" >
        <div class="email-summary-item-title">总打开量</div>
        <div>
          <span class="email-summary-item-value">{{ emailAllData.openCount }}</span>
          <span>（{{ emailAllData.openRate }}%）</span>
        </div>
      </div>
      <div class="email-summary-item" >
        <div class="email-summary-item-title">总回复量</div>
        <div>
          <span class="email-summary-item-value">{{ emailAllData.replyCount }}</span>
          <span>（{{ emailAllData.replyRate }}%）</span>
        </div>
      </div>
      
    </div>
    <div class="searchForm">
    
      <a-form  @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input-group compact>
                <a-select
                  style="width: 60%;border-right: none;"
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
                <a-select
                   style="width: 40%;;"
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
              </a-input-group>
             </a-form-item>
          </a-col>

       
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value.trim="queryParam.username" placeholder="网红账号" />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value.trim="queryParam.email" placeholder="网红邮箱" />
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input-group compact>
                <a-select
                  show-search
                  option-filter-prop="label"
                  allowClear
                  placeholder="品牌"
                  v-model:value="queryParam.brandId"
                  style="width: 120px;border-right: 0px;"
                  @change="onBrandChange"
                >
                  <a-select-option
                    v-for="item in brandList"
                    :key="item.id"
                    :value="item.id"
                    :label="item.brandName"
                  >
                    {{ item.brandName }}
                  </a-select-option>
                </a-select>
                <a-select
                  show-search
                  option-filter-prop="label"
                  allowClear
                  placeholder="产品"
                  style="width: calc(100% - 120px)"
                  v-model:value="queryParam.productId"
                  @change="onProductChange"
                >
                  <a-select-option
                    v-for="item in brandProductList"
                    :key="item.id"
                    :value="item.id"
                    :label="item.productName"
                  >
                    {{ item.productName }}
                  </a-select-option>
                </a-select>
              </a-input-group>
            </a-form-item>
          </a-col>
         

          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model:value="queryParam.isReply" placeholder="回复状态">
                <a-select-option :value="1">已回复</a-select-option>
                <a-select-option :value="2">自动回复</a-select-option>
                <a-select-option :value="0">未回复</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model:value="queryParam.isSend" placeholder="发送状态">
                <a-select-option :value="0">等待发送</a-select-option>
                <a-select-option :value="1">发送成功</a-select-option>
                <a-select-option :value="-1">发送失败</a-select-option>
                <a-select-option :value="-2">取消发送</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-range-picker
                style="width: 100%"
                v-model:value="queryParam.emailPushDateRange"
                format="YYYY-MM-DD HH:mm"
                :show-time="{ format: 'YYYY-MM-DD HH:mm' }"
                :placeholder="['发送开始时间', '发送结束时间']"
                :presets="rangePresets"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
            
                  <a-button type="primary" @click="searchQuery" :icon="h(SearchOutlined)"></a-button>
                  <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px"
                    ></a-button
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
    </div>

 <div  style="padding: 12px;background-color: #fff;">
 
    <div :style="{ height: sTableHeight + 'px' }">
      <vxe-table
        :cell-config="{height:'50px'        }"
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
            <div style="margin-left: 50px;border-left: 2px solid #3155ED;padding:12px;max-height: 442px;overflow-y: auto;" @wheel="handleInnerWheel">
              <div v-for="(item,innerDataIndex) in row.innerData" :key="item.id" class="email-expand-item">
              <div style="display: flex;align-items: center;width: 50px;">
                <div>#</div>
                <div> {{ innerDataIndex + 1 }}</div>
              </div>
                <div class="email-expand-avatar">
                  <a-popover  placement="top" overlayClassName="colum_avatarUrl_tooltip">
                      <template #content>
                        <img
                          v-if="item.avatarUrl"
                          :src="getImage(item.avatarUrl)"
                        @error="handleImageError"
                          style="max-width: 70px;max-height: 70px;"
                          :preview="item.id"
                        />
                        <img
                          v-else
                          src="@/assets/images/avatar.png"
                          style="width: 70px; height: 70px; margin: 0 auto"
                        />
                      </template>
                      <div class="email-expand-avatar-img">
                          <img
                            v-if="item.avatarUrl && !item.avatarUrl.includes('https')"
                            :src="getImage(item.avatarUrl)"
                          @error="handleImageError"
                          
                            style="
                              height: 70px;
                              border-radius: 100%;
                              width: 70px;
                              cursor: pointer;
                            "
                            :preview="item.id"
                          />
                          <img
                            v-else-if="item.avatarUrl && item.avatarUrl.includes('https')"
                            src="@/assets/images/avatar.png"
                          
                            style="
                              height: 70px;
                              border-radius: 100%;
                              width: 70px;
                              cursor: pointer;
                            "
                            :preview="item.id"
                          />
                          <img
                            v-else
                          
                            src="@/assets/images/avatar.png"
                            style="border-radius: 100%; width: 70px; height: 70px; margin: 0 auto"
                          />
                      
                          <img
                            v-if="item.platformType == 0"
                            class="platformType-img"
                            src="@/assets/images/ins.png"
                            alt=""
                          />
                          <img
                            v-if="item.platformType == 1"
                            class="platformType-img"
                            src="@/assets/images/yt.png"
                            alt=""
                          />
                          <img
                            v-if="item.platformType == 2"
                            class="platformType-img"
                            src="@/assets/images/tk.png"
                            alt=""
                          />
                      </div>
                    </a-popover>
                </div>
                <div class="email-expand-account">
                  <div>
                    <span style="color: black;" @click="openLink(item)" >{{ item.username }}</span>
                    <a-tag v-if="item.isPrivate == 1" class="celebrityPrivate">私有</a-tag>
                  </div>
                  <div>
                    {{ item.email }}
                  </div>
                </div>
                <div class="email-expand-email-content">
                  <div class="email-expand-item-title">邮件标题/内容</div>
                  <div style="color: black;">{{ item.emailTitle }}</div>
                  <div>
                    <EllipsisTooltip :text="stripHtmlTags(item.emailContent)" />
                  </div>
                </div>
                <div class="email-expand-email-status">
                  <div class="email-expand-item-title">状态/交互</div>
                  <div style="display: flex;align-items: center;">
                    <div >
                      <span v-if="item.isSend == 1" style="color: green;">发送成功</span>
                      <span v-else-if="item.isSend == 0" style="color: orange;">等待发送</span>
                      <span v-else-if="item.isSend == -1" style="color: red;">发送失败</span>
                      <span v-else-if="item.isSend == -2" >取消发送</span>
                      <span v-else-if="item.isSend == 10" style="color: blue;">部分发送</span>
                    </div>
                    <div style="width: 3px;height: 3px;background-color: #D9D9D9;border-radius: 100%;margin: 0 8px;">
                      
                    </div>
                    <div>
                  
                      <a v-if="item.isOpened == 1">已打开{{ item.openCount }}次</a>
                      <span v-else>未打开</span>
                    </div>
                  </div>
                </div>
                <div class="email-expand-email-sendTime">
                  <div class="email-expand-item-title">预计发送/实际发送</div>
                  <div> <span>{{ parseDate(item.planSendTime, "YYYY-MM-DD HH:mm") }}</span></div>
                  <div> <span>{{ parseDate(item.sendTime, "YYYY-MM-DD HH:mm") }}</span></div>
                </div>
                <div class="email-expand-email-error">
                  <div class="email-expand-item-title">失败原因</div>
                  <div> <span>{{ item.errorMessage || "--" }}</span></div>
                </div>
                <div class="email-expand-email-reply">
                  <div class="email-expand-item-title">回信状态</div>
                  <div>  <a-tag style="padding: 2px 6px;" v-if="item.isReply == 1" color="green">已回复</a-tag>
                    <a-tag style="padding: 2px 6px;" v-else-if="item.isReply == 2" color="green">自动回复</a-tag>
                    <a-tag style="padding: 2px 6px;" v-else color="orange">未回复</a-tag> </div>
                </div>
                <div class="email-expand-email-action">
                  <a-tooltip title="修改邮箱">
                      <a :disabled="item.isSend == 1" @click="editEmail(item)">
                        <a-icon style="font-size: 17px" type="form" />
                      </a>
                    </a-tooltip>
                    <a-divider type="vertical" />
                    <a-tooltip title="预览">
                      <a @click="viewEmail(item)">
          
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
                          (item.isReply != 1 && item.isReply != 2) || item.isSend != 1
                        "
                        @click="replyEmail(item)"
                      >
                        
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
                      @confirm="handleCancelSend(item)"
                    >
                      <a-tooltip title="取消发送">
              
                        <a v-if="item.isSend !== -2" :disabled="item.isSend !== 0">
                          <a-icon style="font-size: 17px" type="close-circle" />
                        </a>
                      </a-tooltip>
                    </a-popconfirm>
                    <a-tooltip title="重新发送">
                      <a v-if="item.isSend === -2" @click="handleResendDetail(item)">
                        <span
                          style="font-size: 16px"
                          class="icon iconfont icon-zhongxinshenqing"
                        ></span>
                      </a>
                    </a-tooltip>
                    <a-divider type="vertical" />
                    <a-tooltip title="一键私有">
                      <a :disabled="item.isSend !== 1" @click="onePrivate(item)">
                        <span
                          class="icon iconfont icon-siyou"
                          style="font-size: 17px"
                        ></span>
                      </a>
                    </a-tooltip>
                </div>
              </div>
             
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column type="seq" width="50" align="center" title="#"></vxe-table-column>
        <vxe-table-column
          field="sendEmail"
          title="发送邮箱"
          minWidth="160"
        >
        <template #default="{ row }">
          <div>{{ row.sendEmail }}</div>
          <div style="color: #969696;">
            <span>签名：</span>
            <span>{{ row.signatureTitle || "--" }}</span>
          </div>
        </template>
        </vxe-table-column> 
        <vxe-table-column field="productName" minWidth="150" title="标记开发产品">
          <template #default="{ row }">
            <span class="productName">{{ row.brandName }} - {{ row.productName }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="totalSentCount"
          minWidth="170"
          title="实时进度"
        >
        <template #default="{ row }">
         <div style="padding-right: 4px;">
          <a-progress style="margin-bottom:0px" :percent="row.totalSentCount ? Math.round((row.actualSentCount / row.totalSentCount) * 100) : 0" />
         </div>
          <div>
            <span style="color:#969696;">成功 {{ row.actualSentCount }} / 总额 {{ row.totalSentCount }}</span>
          </div>
        </template>
        </vxe-table-column>
        <vxe-table-column field="openCount" width="200" title="核心数据">
          <template #default="{ row }">
            <span style="margin-right: 8px;">{{ row.openCount }} 打开</span> <span><a>{{ row.replyCount }}</a> 回信</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="ccEmails" minWidth="160" title="抄送邮箱">
          <template #default="{ row }">
            <span>{{ row.ccEmails || "--" }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="emailPushDate" minWidth="150" title="创建时间/发送时间">
          <template #default="{ row }">
            <div>{{  parseDate(row.createTime, "YYYY-MM-DD HH:mm") }}</div>
            <div>{{ parseDate(row.emailPushDate, "YYYY-MM-DD HH:mm") }}</div>
          </template>
        </vxe-table-column>
        <vxe-table-column field="sendIntervalMin" title="发送间隔" width="150">
          <template #default="{ row }">
            <HistoryOutlined  style="margin-right: 4px;"/>
            <span>{{ row.sendIntervalMin }} - {{ row.sendIntervalMax }} 分钟</span>
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
       
       
        <!-- <vxe-table-column field="remark" width="60" title="备注">
          <template #default="{ row }">
            <span>{{ row.remark || "--" }}</span>
          </template>
        </vxe-table-column> -->
        <vxe-table-column field="action" width="120" align="center" title="操作">
          <template #default="{ row }">
            <a-tooltip title="编辑">
              <a :disabled="row.isSend !== 0" @click="handleEdit(row)">
                <a-icon style="font-size: 17px" type="form" />
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-popconfirm
              :disabled="row.isSend !== 0"
              placement="topLeft"
              title="确定取消当前产品下所有待发送的邮件吗?"
              @confirm="handleCancelAllSend(row)"
            >
              <a-tooltip title="取消发送">
                <a v-if="row.isSend !== -2" :disabled="row.isSend !== 0">
                  <a-icon style="font-size: 17px" type="close-circle" />
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
 </div>
    <importSendEmailModal ref="importModalRef" @ok="modalFormOk"></importSendEmailModal>
    <viewEmailModal ref="viewEmailModalRef"></viewEmailModal>
    <editEmailModal ref="editEmailModalRef" @ok="modalFormOk"></editEmailModal>
    <replyEmailModal ref="replyModalRef" @ok="modalFormOk"></replyEmailModal>
    <onePrivateModal ref="onePrivateModalRef" @ok="modalFormOk"></onePrivateModal>
    <editSendEmailModal ref="editModalRef" @ok="modalFormOk"></editSendEmailModal>
  
  </div>
</template>

<script setup name="emailSendingRecordsList">
import { ref, reactive, computed, onMounted, nextTick, h } from 'vue';
import { Empty } from 'ant-design-vue';
const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
import { SearchOutlined, ReloadOutlined, DownloadOutlined, ImportOutlined, FormOutlined, CloseCircleOutlined,HistoryOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import importSendEmailModal from "./modules/importSendEmailModal.vue";
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import replyEmailModal from "./modules/replyEmailModal.vue";
import viewEmailModal from "./modules/viewEmailModal.vue";
import editSendEmailModal from "./modules/editSendEmailModal.vue";
import onePrivateModal from "./modules/onePrivateModal.vue";
import editEmailModal from "./modules/editEmailModal.vue";
import dayjs from 'dayjs';
import { Pagination } from 'ant-design-vue';
const APagination = Pagination;
const { createMessage } = useMessage();
import { useUserStore } from '/@/store/modules/user';
const userStore = useUserStore();
const userInfo = userStore.getUserInfo;
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
const brandList = ref([]);
const productList = ref([]);
const brandProductList = ref([]);
const currentRow = ref(undefined);
const innerLoading = ref(false);
const expandedRowKeys = ref([]);
const vxeTableRef = ref(null)
const emailAllData = ref({})
const rangePresets = computed(() => [
  { label: '近三天', value: [dayjs().subtract(2, 'day').startOf('day'), dayjs().endOf('day')] },
  { label: '近七天', value: [dayjs().subtract(6, 'day').startOf('day'), dayjs().endOf('day')] },
  { label: '近十五天', value: [dayjs().subtract(14, 'day').startOf('day'), dayjs().endOf('day')] },
])
const url = {
  list: "/email/emailPushMain/list",
  delete: "/email/emailPushMain/delete",
  deleteBatch: "/email/emailPushMain/deleteBatch",
  exportXlsUrl: "/email/emailPushMain/exportXls",
  importExcelUrl: "/email/emailPushMain/importExcel",
};

const initAllEmailData = () => {
  const startDate = dayjs().subtract(1, 'day').set('hour', 7).set('minute', 0).set('second', 0).format('YYYY-MM-DD HH:mm:ss');
  const endDate = dayjs().set('hour', 7).set('minute', 0).set('second', 0).format('YYYY-MM-DD HH:mm:ss');
  defHttp.post({
    url: "/email/emailPushDetail/summary",
    data: { counselorId: userInfo.id }
  }).then((res) => {
    if (res) {
      emailAllData.value = res;
    } else {
      createMessage.error(res?.message);
    }
  });
};
initAllEmailData()
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
  newLayout,
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  vxeTablePageChange,
  fetchTable,
} = useTable(fetchTableApi,67,{
  afterFetch: emailSendingRecordsAfterFetch,
});

newLayout.value = true
async function emailSendingRecordsAfterFetch(res) {
  console.log(res,dataSource)
  console.log(expandedRowKeys.value)
  nextTick(() => {
    if(currentRow.value?.id){

      let current = dataSource.value.filter(
        (item) => item.id == currentRow.value.id
      )[0];
      vxeTableRef.value.setRowExpand(current, true);
      getInnerData(currentRow.value.id);
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
    title: '标记开发产品',
    dataIndex: 'productName',
    minWidth: 150,
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
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
  },
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
    title: '邮件标题',
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
function handleInnerWheel(e) {
  const el = e.currentTarget;
  const { scrollHeight, clientHeight } = el;
  const hasScroll = scrollHeight > clientHeight;
  if (!hasScroll) return;
  e.preventDefault();
  e.stopPropagation();
  el.scrollTop += e.deltaY;
}
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
    getInnerData(row.id);
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

function handleImageError(e) {
  e.target.src = new URL("/@/assets/images/avatar.png", import.meta.url).href;
}
function getImage(url) {
    return url.includes("https") || url.includes("http")
      ? url
      : 'https://gemstone-image.kolbox.com/avatar/' + url;
  }

function parseDate(value, format = "YYYY-MM-DD HH:mm") {
  if (!value) return "--";
  return dayjs(value).format(format);
}

async function initBrandList() {
  const res = await defHttp.get({ url: "/kolBrand/listAll" });
  if (res) {
    brandList.value = res || [];
  }
}

async function initProductList() {
  const res = await defHttp.get({ url: "/kol/kolProduct/listAll" });
  if (res) {
    productList.value = res || [];
  }
}

function onBrandChange(value) {
  queryParam.value.productId = undefined;
  brandProductList.value = [];
  brandProductList.value = productList.value.filter((item) => item.brandId == value);
  if (brandProductList.value.length == 1) {
    queryParam.value.productId = brandProductList.value[0].id;
  }
}

function onProductChange(value) {
  queryParam.value.productId = value;
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

async function handleCancelAllSend(row) {
  console.log(row);
  try {
    const res = await defHttp.get({ 
      url: "/email/emailPushMain/batchCancel", 
      params: { id: row.id } 
    },{isTransformResponse:false});
    if(res.success){
      createMessage.success(res.message);
      fetchTable();
    } else {
      createMessage.warning(res.message);
    }
   
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

function openLink(row) {
  if (row.platformType == "1") {
    open(`https://www.youtube.com/@${row.username}`);
  } else if (row.platformType == "2") {
    open(`https://www.tiktok.com/@${row.username}`);
  } else if (row.platformType == "0") {
    open(`https://www.instagram.com/${row.username}`);
  }
}

function handleDownloadTemplate() {
  const url = '/开发信模板.xlsx'; // 注意：public 下的文件通过根路径访问
  const link = document.createElement('a');
  link.href = url;
  link.download = '开发信模板.xlsx'; // 用户保存时的默认文件名
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
      url: "/email/emailPushDetail/listByMainId", 
      params: {
        mainId: id,
        ...params,
      }
    });
    if (res) {
      const targetItem = dataSource.value.find((item) => item.id === id);
      if (targetItem) {
       
        targetItem.innerData = res
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
  const res = await defHttp.get({ url: "/storeUserContactEmail/queryListByCounselor" });
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

// Lifecycle hooks
onMounted(() => {
  initEmail();
  initBrandList();
  initProductList();
  fetchTable();
});
</script>
<style scoped lang="less">
.celebrityPrivate{
  background-color: #EEF2FF;
  color: #3155ED;
  // padding: 2px 6px;
  border-radius: 6px;
  font-size: 12px;
  // width: 34px;
  border: none;
  margin-right: 0px;
  margin-left: 6px;
}
.email-expand-account{
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 230px;
}
.email-expand-email-content{
  flex: 1;
  min-width: 0;
}
.email-expand-email-sendTime{
  // display: flex;
  // flex-direction: column;
  // justify-content: center;
  width: 200px;
}
.email-expand-email-reply{
 
  width: 120px;
}
.email-expand-item-title{
  color: #969696;
  margin-bottom:6px;
}
.email-expand-email-error{
 
  width: 150px;
}
.email-expand-email-status{
 
  width: 180px;
}
.email-expand-email-action{
  display: flex;
  align-items: center;
   width:154px;
}
.email-expand-avatar-img {
width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.platformType-img {
  display: inline-block;
  width: 24px;
  height: 24px;
  position: absolute;
  right: -10px;
  bottom: 0px;
}
.emailSendingRecordsList {
  padding: 12px;
 
}
.productName{
  background-color: #EEF2FF;
  color:@primary-color;
  padding: 4px 8px;
  border-radius: 8px;
}
.email-summary-item{
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex:1;background-color:#fff;padding: 20px;
  border-radius: 8px;
}
.email-summary-item-title{
  font-size: 14px;
  color: #38426B;
  margin-bottom:6px;
}
.email-summary-item-value{
  font-size: 24px;
  color: #000000;
}
.searchForm {
  background-color: white;
  padding: 12px 12px 4px 12px;
  margin-bottom: 12px;
}
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
.email-expand-item{
  background-color: #fff;
  padding: 12px;
  border-radius: 8px;
  border:1px solid #DBEAFE;
  margin-bottom: 12px;
  display: flex;
  // justify-content: space-between;
  gap:20px;
}
</style>
