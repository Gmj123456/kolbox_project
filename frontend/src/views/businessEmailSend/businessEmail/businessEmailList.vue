<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <a-form  class="searchForm" @keyup.enter="searchQuery">
      <a-row  :gutter="12">
        <a-col :xl="4" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-input
              placeholder="邮箱"
              v-model:value.trim="queryParam.contactEmail"
            ></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-button type="primary" @click="searchQuery" :icon="h(SearchOutlined)">查询</a-button>
            <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px"
              >重置</a-button
            >
            <a-button
              @click="handleAdd"
              type="primary"
              :icon="h(PlusOutlined)"
              style="margin-left: 8px"
              >新增</a-button
            >
            <a-button
              :loading="syncSignatureLoading"
              @click="syncSignature"
              type="primary"
              :icon="h(SyncOutlined)"
              style="margin-left: 8px"
              >同步Gmail邮箱签名</a-button
            >
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <!-- table区域-begin -->
    <div>
      <s-table
        :rangeSelection="false"
        ref="tableRef"
        size="small"
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        v-model:pagination="pagination"
        :loading="loading"
        :scroll="{ y: sTableHeight, x: '100%' }"
        :expandedRowKeys="expandedRowKeys"
        @expandedRowsChange="handleExpandChange"
    
 
        @change="pageChange"
      >
        <template #expandIcon="props">
            <template v-if="props.record.signatureList && props.record.signatureList.length > 0">
              <button @click="props.onExpand(props.record, props.expanded)" v-if="!props.expanded" type="button" class="surely-table-row-expand-icon surely-table-row-expand-icon-collapsed " aria-label="展开行"></button>
              <button @click="props.onExpand(props.record, props.expanded)" v-else type="button" class="surely-table-row-expand-icon surely-table-row-expand-icon-expanded " aria-label="收起行"></button>
            </template>
            <span v-else style="margin: 0 16px 0 0"></span>
        </template>
        <template #expandedRowRender="{ record }">
          <div style="margin-left: 50px;">
            <s-table
            :rangeSelection="false"
           size="small"
            :columns="innerColumns"
            :data-source="record.signatureList || []"
            rowKey="id"
            :pagination="false"
          >
            <template #bodyCell="{ column, record: innerRecord, text }">
              <template v-if="column.dataIndex === 'action'">
                <a-tooltip title="编辑">
                  <a @click="handleEditSignature(record, innerRecord)">
                    <FormOutlined style="font-size: 15px" />
                  </a>
                </a-tooltip>
                <a-divider type="vertical" />
                <a-popconfirm
                  placement="topLeft"
                  title="确定删除吗?"
                  @confirm="handleDeleteSignature(record, innerRecord)"
                >
                  <a-tooltip title="删除">
                    <a>
                      <span class="icon iconfont icon-shanchu"></span>
                    </a>
                  </a-tooltip>
                </a-popconfirm>
              </template>
              <template v-else-if="column.dataIndex === 'remark'">
                <span @clikc="copyFn(text)">{{ text || "--" }}</span>
              </template>
            </template>
          </s-table>

          </div>
        </template>
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.dataIndex === 'contactEmail'">
            <span @clikc="copyFn(text)">{{ text }}</span>
          </template>
          <template v-else-if="column.dataIndex === 'emailType'">
            <span v-if="text === 0" color="green">谷歌邮箱</span>
            <span v-else color="orange">企业邮箱</span>
          </template>
          <template v-else-if="column.dataIndex === 'emailPassword'">
            <div style="display: flex;" v-if="record.emailPassword">
              <div v-if="visiblePasswordIds[record.id]">
                {{ record.emailPassword  }}
 
              </div>
              <div v-else style="transform: translateY(2px);">
                ********
              </div>
              <diiv style="margin-left: 8px; cursor: pointer;">
                <EyeOutlined v-if="!visiblePasswordIds[record.id]" @click="showPassword(record)" />
                <EyeInvisibleOutlined v-else @click="hidePassword(record)" />
              </diiv>
            </div>
            <div v-else>--</div>
          </template>
          <template v-else-if="column.dataIndex === 'emailStatus'">
            <a-tag v-if="text === 1" color="green">可用</a-tag>
            <a-tag v-else color="orange">不可用</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'isAuthorized'">
            <a-tag v-if="text === 1" color="green">已授权</a-tag>
            <a-tag v-else color="orange">未授权</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <!-- <a-tooltip :title="record.contactEmail.includes('@gmail.com') ? '邮箱授权' : '修改邮箱密码'">
              <a v-if="record.contactEmail.includes('@gmail.com')" :disabled="record.isAuthorized == 1" @click="handleEmailAuth(record)">
                <span
                  class="icon iconfont icon--_shouquanguanli"
                  style="font-size: 18px"
                ></span>
              </a>
              <a v-else  @click="editPassword(record)">
                <KeyOutlined style="font-size: 15px" />
              </a>
            </a-tooltip> -->
            <a-tooltip title="邮箱授权">
              <a v-if="record.contactEmail.includes('@gmail.com')" :disabled="record.isAuthorized == 1" @click="handleEmailAuth(record)">
                <span
                  class="icon iconfont icon--_shouquanguanli"
                  style="font-size: 18px"
                ></span>
              </a>
           
            </a-tooltip>
            <a-divider type="vertical" />
            <a-tooltip title="新增签名">
              <a @click="handleAddSignature(record)">
                <PlusOutlined style="font-size: 15px" />
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-tooltip title="编辑">
              <a @click="handleEdit(record)">
                <FormOutlined style="font-size: 15px" />
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-popconfirm
              placement="topLeft"
              title="确定删除吗?"
              @confirm="handleDelete(record.id)"
            >
              <a-tooltip title="删除">
                <a>
                  <span class="icon iconfont icon-shanchu"></span>
                </a>
              </a-tooltip>
            </a-popconfirm>
          </template>
        </template>
      </s-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <businessEmailModal
      ref="modalFormRef"
      @ok="modalFormOk"
    ></businessEmailModal>
   
    <SignatureModal ref="SignatureModalRef" @ok="modalFormOk"></SignatureModal>
    <editPasswordModal ref="editPasswordModalRef" @ok="modalFormOk"></editPasswordModal>
  </a-card>
</template>

<script setup name="businessEmailList">
import { ref, reactive, h } from 'vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, KeyOutlined, FormOutlined,PlusSquareOutlined,MinusSquareOutlined, EyeOutlined, EyeInvisibleOutlined, SyncOutlined   } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import businessEmailModal from "./modules/businessEmailModal.vue";
import SignatureModal from "./modules/SignatureModal.vue";
import editPasswordModal from "./modules/editPasswordModal.vue";
import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
const { createMessage } = useMessage();

// Refs
const tableRef = ref(null);
const modalFormRef = ref(null);
const ReplaceContachEmailRef = ref(null);
const SignatureModalRef = ref(null);
const editPasswordModalRef = ref(null);
const syncSignatureLoading = ref(false);
// Data
const expandedRowKeys = ref([]);
// 跟踪哪些记录的密码是可见的
const visiblePasswordIds = ref({});

const url = {
  list: "/storeUserContactEmail/list",
  delete: "/storeUserContactEmail/delete",
  deleteBatch: "/storeUserContactEmail/deleteBatch",
  exportXlsUrl: "/storeUserContactEmail/exportXls",
  importExcelUrl: "/storeUserContactEmail/importExcel",
  innerTableList: "/email/signature/getSignatureList",
  innerTableDelete: "/email/signature/delete",
};

// Use Table Hook
const fetchTableApi = async (params) => {
  params.type = 1
  const res = await defHttp.get({ url: url.list, params });
  return res || { records: [], total: 0 };
};

const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
  copyFn
} = useTable(fetchTableApi,0,{afterFetch:userBuildEmailAfterFetchFn});
function userBuildEmailAfterFetchFn() {
  // dataSource.value
  console.log( dataSource.value.filter(item => expandedRowKeys.value.includes(item.id)))
  dataSource.value.forEach(item => {
    if (expandedRowKeys.value && expandedRowKeys.value.includes(item.id)) {
      // item.signatureList = item.signatureList.filter((key) => key !== record.id);
      if (!item.signatureList || item.signatureList.length === 0) {
        expandedRowKeys.value = expandedRowKeys.value.filter((key) => key !== item.id);
      }
    }
  })
}
async function syncSignature() {
  console.log("同步签名");
  syncSignatureLoading.value = true;
  const res = await defHttp.get({ url: '/email/signature/syncGmailSignature' },{isTransformResponse:false});
  if(res.success) {
    createMessage.success(res.message);
    fetchTable();
    syncSignatureLoading.value = false;
  } else {
    syncSignatureLoading.value = false;
    createMessage.error(res.message);

  }
}
const rowExpandableFn = (record) => {
  return record.signatureList && record.signatureList.length > 0
}
// 初始化查询参数
Object.assign(queryParam, {
  contactEmail: undefined,
});

// 表头
const columns = [
  {
    title: "#",
    dataIndex: "index",
    key: "rowIndex",
    width: 60,
    align: "center",
    customRender: ({ index }) => parseInt(index) + 1,
  },
  {
    title: "邮箱",
    dataIndex: "contactEmail",
  },
  {
    title: "邮箱类型",
    dataIndex: "emailType",
  },
  {
    title: "邮箱名称",
    dataIndex: "emailDisplayName",
  },
  {
    title: "邮箱密码",
    dataIndex: "emailPassword",
  },
  {
    title: "邮箱状态",
    dataIndex: "emailStatus",
  },
  {
    title: "授权状态",
    dataIndex: "isAuthorized",
  },
  {
    title: "操作",
    width: 150,
    dataIndex: "action",
    align: "center",
  },
];

const innerColumns = [
  {
    title: "#",
    dataIndex: "index",
    key: "rowIndex",
    width: 60,
    align: "center",
    customRender: ({ index }) => parseInt(index) + 1,
  },
  {
    title: "签名",

    dataIndex: "signatureTitle",
  },
  {
    title: "签名内容",

    dataIndex: "signatureContent",
  },
  {
    title: "操作",
    dataIndex: "action",
    align: "center",
    width: 150,
  },
];
function showPassword(record) {
  visiblePasswordIds.value[record.id] = true;
}

function hidePassword(record) {
  visiblePasswordIds.value[record.id] = false;
}

function editPassword(record) {
  editPasswordModalRef.value?.edit(record);
}
function handleEmailAuth(record) {
  // console.log(record);
  const email = record.contactEmail || "";
  const googleEmailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/i;
  if (!googleEmailRegex.test(email)) {
    createMessage.warning("当前邮箱不是谷歌邮箱，无法进行授权操作");
    return;
  }
  window.open(
    `https://gmail.kolbox.com/api/gmail/authorize?user_email=${email}`,
    "_blank"
  );
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

function replaceEmail() {
  ReplaceContachEmailRef.value?.show();
}

function handleExpandChange(keys) {
  expandedRowKeys.value = keys;
}

function handleExpand(expanded, record,props) {
  console.log(expanded, record,props);
  if (expanded) {
    if (!expandedRowKeys.value.includes(record.id)) {
      expandedRowKeys.value.push(record.id);
    }
  } else {
    expandedRowKeys.value = expandedRowKeys.value.filter((id) => id !== record.id);
  }
}

function handleAddSignature(record) {
  console.log(record);
  SignatureModalRef.value?.add(record);
  if (SignatureModalRef.value) {
    SignatureModalRef.value.title = "新增签名";
  }
}

function handleEditSignature(record, innerRecord) {
  console.log(record);
  SignatureModalRef.value?.edit(record, innerRecord);
  if (SignatureModalRef.value) {
    SignatureModalRef.value.title = "编辑签名";
  }
}

async function handleDeleteSignature(record, innerRecord) {
  if (!url.innerTableDelete) {
    createMessage.error("请设置url.delete属性!");
    return;
  }
  try {
    const res = await defHttp.delete({ url: url.innerTableDelete, data: { id: innerRecord.id } },{ joinParamsToUrl: true });
    fetchTable();

  } catch (error) {
    console.error("删除失败:", error);
  }
}

function handleAdd() {
  modalFormRef.value?.add();
}

function handleEdit(record) {
  modalFormRef.value?.edit(record);
}

async function handleDelete(id) {
  if (!url.delete) {
    createMessage.error("请设置url.delete属性!");
    return;
  }
  try {
    const res = await defHttp.delete({ url: url.delete, data: {type:1, id } },{ joinParamsToUrl: true });
    fetchTable();
  
  } catch (error) {
    console.error("删除失败:", error);
  }
}

function modalFormOk() {
  fetchTable();
}

fetchTable()
</script>
<style scoped>
</style>
