<template>
  <a-modal
    :maskClosable="false"
    centered
    title="调整网红顾问"
    v-model:open="visible"
    :width="1350"
    @cancel="handleCancel"
    @ok="handleOk"
    :confirmLoading="confirmLoading"
    class="ReplaceModifyAdvisorModal"
  >
    <a-form :model="queryParam">
      <a-row style="margin-bottom: 8px">
        <a-col :span="3">
          <a-form-item>
            <a-input-group compact>
              <a-input
                style="width: 62px; background-color: #fff; color: #0b1019"
                defaultValue="交接人"
                disabled
              ></a-input>
              <a-select
                style="width: calc(100% - 62px);border-right: none;"
                show-search
                option-filter-prop="label"
                v-model:value="queryParam.celebrityCounselorOldId"
                placeholder="交接人"
                @change="changeUser($event, '0')"
                allowClear
              >
                <a-select-option
                  v-for="item in celebrityCounselorList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.username"
                >
                  {{ item.username }}
                </a-select-option>
              </a-select>
            </a-input-group>
          </a-form-item>
        </a-col>
        <a-col :span="4" style="margin-left: 7px">
          <a-form-item>
            <a-select
              @change="changeCelebrityContactEmail"
              placeholder="交接人建联邮箱"
              v-model:value="queryParam.celebrityContactEmail"
              allowClear
            >
              <a-select-option
                v-for="item in celebrityContactEmailList"
                :key="item.id"
                :value="item.id"
                :label="item.contactEmail"
              >
                {{ item.contactEmail }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="3" style="margin-left: 7px; width: 200px">
          <a-form-item>
            <a-input placeholder="账号" v-model:value="queryParam.account">
              <template #suffix>
                <a-popover
                  @visible-change="onVisibleChange"
                  v-model:open="accountPopoverVisible"
                  trigger="click"
                  placement="bottomRight"
                  :overlayStyle="{ width: '300px', height: '400px' }"
                >
                  <template #content>
                    <a-textarea
                      v-model:value="accountTextarea"
                      placeholder="请输入账号,一行一个"
                      :auto-size="{ minRows: 8, maxRows: 8 }"
                    />
                    <div
                      style="
                        display: flex;
                        justify-content: space-between;
                        margin-top: 10px;
                      "
                    >
                      <a-button @click="addAccount">关闭</a-button>
                      <div>
                        <a-button @click="clearAccount">清空</a-button>
                        <a-button
                          style="margin-left: 8px"
                          type="primary"
                          @click="addAccount"
                          >确定</a-button
                        >
                      </div>
                    </div>
                  </template>

                  <!-- 阻止图标点击冒泡，避免 input blur 导致弹层立刻关闭 -->
                  <div>
                    <AlignLeftOutlined @click.stop="toggleAccountPopover" />
                  </div>
                </a-popover>
              </template>
            </a-input>
          </a-form-item>
        </a-col>

        <a-col :span="2" style="width: 74px">
          <a-button
            type="primary"
            @click="searchQuery"
            :icon="h(SearchOutlined)"
            style="margin-left: 8px"
          ></a-button>
          <!-- <a-button @click="searchReset" icon="reload" style="margin-top: 4px;margin-left: 8px;">重置</a-button> -->
        </a-col>
        <!-- <a-col :span="2" style="width: 90px!important;">
          <a-form-item style="margin-left: 5px">
          
          </a-form-item>
        </a-col> -->
        <a-col :span="6" style="margin-left: 7px">
          <a-form-item>
            <a-input-group compact>
              <div style="width: 80px;transform: translateY(6px);">
                <a-checkbox v-model:checked="queryParam.isCooperation" @change="changeIsCooperation">
                  合作中
                </a-checkbox>
              </div>
              <a-input
                style="width: 62px; background-color: #fff; color: #0b1019"
                defaultValue="接收人"
                disabled
              ></a-input>
              <a-select
                style="width: calc(100% - 62px - 90px);border-right: none;"
                show-search
                option-filter-prop="label"
                allowClear
                v-model:value="queryParam.celebrityCounselorId"
                placeholder="接收人(必选)"
                @change="changeUser($event, '1')"
              >
                <a-select-option
                  v-for="item in celebrityCounselorList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.username"
                >
                  {{ item.username }}
                </a-select-option>
              </a-select>
            </a-input-group>
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item style="margin-left: 7px">
            <a-select
              :disabled="!queryParam.isCooperation"
              style="width: 100%"
              v-model:value="queryParam.contactEmailId"
              placeholder="接收人建联邮箱"
              allowClear
            >
              <a-select-option
                v-for="item in contactEmailList"
                :key="item.id"
                :value="item.id"
                :label="item.contactEmail"
              >
                {{ item.contactEmail }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>

        <!-- <a-col :span="4" style="margin-left: 7px;">
           <a-form-item>
            <a-select style="width: 90%;" v-model="queryParam.sysUserId" placeholder="网红顾问" @change="changeUser($event,'0')"
              allowClear>
              <a-select-option v-for="item in celebrityCounselorList" :key="item.id" :value="item.id">
                <a-tooltip :title="item.realname">
                  {{ item.username }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col> -->
      </a-row>
    </a-form>
    <a-spin :spinning="loading">
      <a-transfer
        :loading="loading"
        :dataSource="mockData"
        v-model:target-keys="targetKeys"
        v-model:selected-keys="selectedKeys"
        :list-style="{
          width: '400px',
          height: '500px',
        }"
        :render="(item) => item.title"
        :showSelectAll="false"
      >
        <template

        #children="{
          direction,
          filteredItems,
          selectedKeys: transferSelectedKeys,
         
          onItemSelectAll,
          onItemSelect,
        }"
         
        >
          <a-table
            v-if="direction === 'left'"
            class="table-page-background-one"
            :row-selection="getRowSelection({
         
              selectedKeys: transferSelectedKeys,
              onItemSelectAll,
              onItemSelect,
            })"
            rowKey="id"
            :pagination="ipaginationLeft"
            :columns="leftColumns"
            :data-source="filteredItems"
            :scroll="{ y: 362 }"
            size="middle"
            @change="handleLeftTableChange"
          >
            <template #platformType="{ record }">
              <img
                v-if="record.platformType == 0"
                style="width: 22px; height: 22px"
                src="@/assets/images/ins.png"
                alt=""
              />
              <img
                v-if="record.platformType == 1"
                style="width: 22px; height: 22px"
                src="@/assets/images/yt.png"
                alt=""
              />
              <img
                v-if="record.platformType == 2"
                style="width: 22px; height: 22px"
                src="@/assets/images/tk.png"
                alt=""
              />
            </template>
            <template #followersNum="{ record }">
              <span v-if="record.followersNum !== null && record.followersNum !== ''">{{
                getFollower(record.followersNum)
              }}</span>
              <span v-else>--</span>
            </template>
          </a-table>
          <!-- <div
            v-if="direction === 'left' && transferSelectedKeys.length > 0"
            style="position: absolute; bottom: 15px; left: 12px"
          >
            已选择<a>{{ transferSelectedKeys.length }}</a> 个
          </div>
          <div
            v-if="direction === 'right' && transferSelectedKeys.length > 0"
            style="position: absolute; bottom: 15px; left: 15px"
          >
            已选择<a>{{ transferSelectedKeys.length }}</a> 个
          </div> -->
          <a-table
            v-if="direction === 'right'"
            class="table-page-background-one"
            :row-selection="getRowSelection({
            
              selectedKeys: transferSelectedKeys,
              onItemSelectAll,
              onItemSelect,
            })"
            rowKey="id"
            :pagination="ipaginationRight"
            :columns="rightColumns"
            :data-source="filteredItems"
            :scroll="{ y: 362 }"
            size="middle"
            @change="handleRightTableChange"
          >
            <template #platformType="{ record }">
              <img
                v-if="record.platformType == 0"
                style="width: 22px; height: 22px"
                src="@/assets/images/ins.png"
                alt=""
              />
              <img
                v-if="record.platformType == 1"
                style="width: 22px; height: 22px"
                src="@/assets/images/yt.png"
                alt=""
              />
              <img
                v-if="record.platformType == 2"
                style="width: 22px; height: 22px"
                src="@/assets/images/tk.png"
                alt=""
              />
            </template>
            <template #followersNum="{ record }">
              <span v-if="record.followersNum !== null && record.followersNum !== ''">{{
                getFollower(record.followersNum)
              }}</span>
              <span v-else>--</span>
            </template>
          </a-table>
        </template>
      </a-transfer>
    </a-spin>
  </a-modal>
</template>
<script setup>
import { ref, nextTick, h,watch } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { SearchOutlined, AlignLeftOutlined } from '@ant-design/icons-vue';
import { queryAllCelebrityCounselorApi } from '/@/api/sys/systemCommon';
const { createMessage: $message } = useMessage();

// 响应式数据
const accountPopoverVisible = ref(false);
const accountTextarea = ref('');
const visible = ref(false);
const confirmLoading = ref(false);
const loading = ref(false);
const mockData = ref([]);
const contactEmailList = ref([]);
const contactEmail = ref('');
const celebrityCounselorList = ref([]);
const targetKeys = ref([]);
const selectedKeys = ref([]);
const celebrityContactEmailList = ref([]);

const queryParam = ref({
  celebrityCounselorOldId: undefined,
  celebrityCounselorId: undefined,
  account: undefined,
  contactEmailId: undefined,
  celebrityContactEmail: undefined,
  isCooperation: false,
});

const ipaginationLeft = ref({
  current: 1,
  pageSize: 30,
  pageSizeOptions: ['30', '50', '100'],
  showTotal: (total, range) => {
    return range[0] + '-' + range[1] + ' 共' + total + '条';
  },
  showQuickJumper: false,
  showSizeChanger: true,
  total: 0,
});

const ipaginationRight = ref({
  current: 1,
  pageSize: 30,
  pageSizeOptions: ['30', '50', '100'],
  showTotal: (total, range) => {
    return range[0] + '-' + range[1] + ' 共' + total + '条';
  },
  showQuickJumper: false,
  showSizeChanger: true,
  total: 0,
});
watch(
  targetKeys,
  (val) => {
    ipaginationRight.value.total = val.length;
    ipaginationLeft.value.total = mockData.value.length - val.length;
  },
  { immediate: true },
);

const leftColumns = ref([
  {
    title: '#',
    dataIndex: '',
    width: '40px',
    customRender: function ({index}) {

      return parseInt(index) + 1;
    },
  },
  {
    title: '账号',
    dataIndex: 'account',
  },
  {
    title: '平台',
    align: 'center',
    width: '80px',
    dataIndex: 'platformType',
    slots: { customRender: 'platformType' },
  },
  {
    title: '国家',
    width: '100px',
    dataIndex: 'countryName',
  },
  {
    title: '粉丝',
    width: '100px',
    dataIndex: 'followersNum',
    key: 'followersNum',
    slots: { customRender: 'followersNum' },
  },
]);

const rightColumns = ref([
  {
    title: '#',
    dataIndex: '',
    width: '40px',
    customRender: function ({index}) {

return parseInt(index) + 1;
},
  },
  {
    title: '账号',
    dataIndex: 'account',
  },
  {
    title: '平台',
    align: 'center',
    width: '80px',
    dataIndex: 'platformType',
    slots: { customRender: 'platformType' },
  },
  {
    title: '国家',
    width: '100px',
    dataIndex: 'countryName',
  },
  {
    title: '粉丝',
    width: '100px',
    dataIndex: 'followersNum',
    key: 'followersNum',
    slots: { customRender: 'followersNum' },
  },
]);

// 定义 emits
const emit = defineEmits(['close', 'ok']);

// 方法
function onVisibleChange(visible) {
  // 只允许打开，不允许自动关闭
  if (visible) accountPopoverVisible.value = true;
}

function toggleAccountPopover(_e) {
  if (queryParam.value.account) {
    accountTextarea.value = queryParam.value.account.split(',').join('\n');
  }
  // click 已被 .stop 阻止冒泡，这里仅切换可见性
  accountPopoverVisible.value = !accountPopoverVisible.value;
}



function handleLeftTableChange(pagination, _filters, _sorter) {
  //分页、排序、筛选变化时触发
  console.log(pagination);
  Object.assign(ipaginationLeft.value, pagination);
}

function handleRightTableChange(pagination, _filters, _sorter) {
  //分页、排序、筛选变化时触发
  console.log(pagination);
  Object.assign(ipaginationRight.value, pagination);
}

function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    const kValue = Math.round(num / 1000);
    return `${kValue}K`;
  } else if (num >= 1000000) {
    const mValue = Math.round(num / 1000000);
    return `${mValue}M`;
  } else if (num > 0 && num < 1000) {
    return num;
  } else {
    return '--';
  }
}
const getRowSelection = ({
  selectedKeys,
  onItemSelectAll,
  onItemSelect,
}) => {
  return {
    onSelectAll(selected, selectedRows) {
      const treeSelectedKeys = selectedRows
        .filter((item) => !item.disabled)
        .map((item) => item.key || item.id);
      onItemSelectAll(treeSelectedKeys, selected);
    },
    onSelect(record, selected) {
      const key = record.key || record.id;
      onItemSelect(key, selected);
    },
    selectedRowKeys: selectedKeys,
  };
};


async function changeUser(e, type) {
  if (type === '1') {
    queryParam.value.contactEmailId = undefined;
    const res = await defHttp.get({
      url: '/storeUserContactEmail/queryListByCounselor',
      params: {
        sysUserId: queryParam.value.celebrityCounselorId,
      },
    });
    console.log(res);
    contactEmailList.value = res;
  } else {
    queryParam.value.celebrityContactEmail = undefined;
    const res = await defHttp.get({
      url: '/storeUserContactEmail/queryListByCounselor',
      params: {
        sysUserId: queryParam.value.celebrityCounselorOldId,
      },
    });
    console.log(res);
    celebrityContactEmailList.value = res;
  }
}

function changeCelebrityContactEmail(e) {
  console.log(e);
  if (e){

    loadData()
  }
}

async function show() {
  mockData.value = [];
  targetKeys.value = [];
  selectedKeys.value = [];
  contactEmailList.value = [];

  Object.assign(queryParam.value, {
    celebrityCounselorOldId: undefined,
    celebrityCounselorId: undefined,
    account: undefined,
    contactEmailId: undefined,
    celebrityContactEmail: undefined,
    isCooperation: false,
  });
  visible.value = true;
  await initCelebrityCounselor();

  nextTick(() => {
    document
      .querySelectorAll('.ReplaceModifyAdvisorModal .ant-table-body')
      .forEach((item) => {
        item.style.minHeight = 400 + 'px';
      });
  });
}

async function initCelebrityCounselor() {
  const res = await queryAllCelebrityCounselorApi();
  celebrityCounselorList.value = res
}

function close() {
  emit('close');
  visible.value = false;
}

async function handleOk() {
  if (!queryParam.value.celebrityCounselorOldId) {
    $message.warning('请选择交接人！');
    return;
  }
  if (!queryParam.value.celebrityContactEmail) {
    $message.warning('请选择交接人建联邮箱！');
    return;
  }
  if (!queryParam.value.celebrityCounselorId) {
    $message.warning('请选择接收人！');
    return;
  }
  if (targetKeys.value.length < 1) {
    $message.warning('请至少选择一个网红');
    return;
  }
  if (queryParam.value.isCooperation && !queryParam.value.contactEmailId) {
    $message.warning('请选择接收人建联邮箱');
    return;
  }
  confirmLoading.value = true;
  try {
    const res = await defHttp.post({
      url: '/privateCounselor/updateCelebrityCounselor',
      data: {
        celebrityPrivateIds: targetKeys.value.join(','),
        celebrityCounselorOldId: queryParam.value.celebrityCounselorOldId,
        celebrityCounselorId: queryParam.value.celebrityCounselorId,
        contactEmailId: queryParam.value.contactEmailId,
        contactEmailIdOld: queryParam.value.celebrityContactEmail,
        isCooperation: queryParam.value.isCooperation ? 1 : 0,
      },
    });
    emit('ok');
    close();
   
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  contactEmail.value = '';
  mockData.value = [];
  close();
}

async function loadData(_arg) {
  if (!queryParam.value.celebrityCounselorOldId) {
    $message.warning('请选择交接人！');
    return;
  }
  if (!queryParam.value.celebrityContactEmail) {
    $message.warning('请选择交接人建联邮箱！');
    return;
  }
  targetKeys.value = []
  mockData.value = [];
  ipaginationLeft.value.current = 1;
  loading.value = true;
  try {
    const res = await defHttp.get({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/getPageCelebrityPrivatesNew',
      params: {
        celebrityCounselorId: queryParam.value.celebrityCounselorOldId,
        account: queryParam.value.account,
        celebrityContactEmail: queryParam.value.celebrityContactEmail
          ? celebrityContactEmailList.value.find(
              (item) => item.id === queryParam.value.celebrityContactEmail
            )?.contactEmail
          : undefined,
      },
    });
   
    for (let index = 0; index < res.length; index++) {
      const element = res[index];
      element.title = element.contactEmail + ' ' + element.emailDisplayName;
      element.key = element.id;
    }
    mockData.value = res;
    ipaginationLeft.value
   
  } finally {
    loading.value = false;
  }
}

function searchQuery() {
  loadData(1);
}

// handleChange 已移除，使用 v-model:target-keys 双向绑定

function changeIsCooperation() {
  if (!queryParam.value.isCooperation) {
    queryParam.value.contactEmailId = undefined;
  }
}

function clearAccount() {
  accountTextarea.value = '';
}

function addAccount() {
  const accounts = accountTextarea.value
    .split('\n')
    .filter((item) => item.trim() !== '');
  queryParam.value.account = accounts.join(',');
  accountPopoverVisible.value = false;
}

// 暴露方法给父组件
defineExpose({
  show,
});
</script>
<style lang="less" scoped>
:deep(.ant-transfer-customize-list .ant-table-wrapper .ant-table-pagination.ant-table-pagination){
  margin: 16px 10px !important;
}
.ant-table-tbody tr td {
  color: #0b1019 !important;
}
:deep(.ant-transfer-list-header) {
  display: none !important;
}
:deep(.ant-transfer-list) {
  padding-top: 0px !important;
}
</style>