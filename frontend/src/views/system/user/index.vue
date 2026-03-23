<template>
 <a-card :bordered="false">
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <j-input v-model:value="queryParam.username" placeholder="账号" />
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <j-input v-model:value="queryParam.realname" placeholder="昵称" />
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model:value="queryParam.sex" placeholder="性别">
                <a-select-option :value="0">男</a-select-option>
                <a-select-option :value="1">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input v-model:value="queryParam.phone" placeholder="手机号">
                
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
            <!-- <JDictSelectTag 
              v-model:value="queryParam.userType" 
              dictCode="user_type" 
              placeholder="用户类型" /> -->
             <a-select v-model:value="queryParam.userType" placeholder="用户类型">
              <a-select-option v-for="item in userTypeList" :key="item.value" :value="item.value">
                {{ item.title }}
              </a-select-option>
             </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="8" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="searchQuery">查询</a-button>
              <a-button :icon="h(ReloadOutlined)" style="margin-left: 8px" @click="searchReset">
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
       
              <a-button   style="margin-left: 8px" type="primary" @click="openModal(true, {})" preIcon="ant-design:hdd-outlined"> 回收站</a-button>
              <a-dropdown v-if="selectedRowKeys.length > 0">
                <template #overlay>
                  <a-menu>
                    <a-menu-item key="1" @click="batchHandleDelete">
                  
                      删除
                    </a-menu-item>
                    <a-menu-item key="2" @click="batchFrozen(2)">
                     
                      冻结
                    </a-menu-item>
                    <a-menu-item key="3" @click="batchFrozen(1)">
             
                      解冻
                    </a-menu-item>
                    <a-menu-item key="4" @click="batchSetEmployee">
             
                      设为员工
                    </a-menu-item>
                  </a-menu>
                </template>
                <a-button
                    style="margin-left: 8px"
                  >批量操作
                  <Icon icon="mdi:chevron-down"></Icon>
                </a-button>
              </a-dropdown>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
 

    <s-table
      :rangeSelection="false"
      ref="tableRef"
      :scroll="{ y: sTableHeight, x: '100%' }"
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :rowSelection="rowSelection"
      :selectedRowKeys="selectedRowKeys"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>
        <template v-else-if="column.key === 'avatar'">
          <a-avatar :src="record.avatar" shape="square" >
            <template #icon><UserOutlined /></template>
          </a-avatar>
          
        </template>
        <template v-else-if="column.key === 'userType'">
          <span v-if="record.userType == 0">平台</span>
          <span v-else-if="record.userType == 1">商家</span>
          <span v-else-if="record.userType == 2">网红</span>
          <span v-else-if="record.userType == 3">商家顾问</span>
          <span v-else-if="record.userType == 4">网红顾问</span>
          <span v-else-if="record.userType == 10">商务人员</span>
        
        </template>
        <template v-else-if="column.key === 'isLeave'">
          <a-tag :color="record.isLeave == 1 ? 'red' : 'blue'">{{ record.isLeave == 1 ? '是' : '否' }}</a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a @click="handleEdit(record)">
          编辑
          </a>
          
          <a-divider type="vertical" />
            <a href="javascript:;" @click="handleChangePassword(record.username)">密码</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record)">
      
            <a style="color:red;">删除</a>
          </a-popconfirm>
          <a-divider type="vertical" />
          <a-popconfirm title="确定冻结吗?" @confirm="() => handleFrozen(record.id,2,record.username)">
            <a v-if="record.status==1">冻结</a>
          </a-popconfirm>
          <a-divider type="vertical" />
          <a-popconfirm title="确定解冻吗?" @confirm="() => handleFrozen(record.id,1,record.username)">
            <a v-if="record.status==2">解冻</a>
          </a-popconfirm>
       
        
          <!-- <a-popconfirm
            title="确定删除吗?"
            @confirm="() => handleDelete(record.id)"
            style="margin-left: 7px"
          >
            <a-tooltip placement="top" title="删除">
              <a>
                <span class="icon iconfont icon-shanchu"></span>
              </a>
            </a-tooltip>
          </a-popconfirm> -->
        </template>
        <template v-else>
          {{ text }}
        </template>
      </template>
    </s-table>

    <UserDrawer @register="registerDrawer" @success="handleSuccess" />
    <!--修改密码-->
    <PasswordModal @register="registerPasswordModal" @success="fetchTable" />
    <!--回收站-->
    <UserRecycleBinModal @register="registerModal" @success="fetchTable" />
    <!-- 离职人员列弹窗 -->
    <UserQuitModal @register="registerQuitModal" @success="fetchTable" />
  </a-card>
</template>

<script lang="ts" name="system-user" setup>
  //ts语法
  import { ref, computed, unref,h } from 'vue';
  import { useTable } from '/@/components/useSTable/useSTable';
  import { BasicTable, TableAction, ActionItem } from '/@/components/Table';
  import { SearchOutlined, ReloadOutlined, PlusOutlined ,UserOutlined} from '@ant-design/icons-vue';
  import UserDrawer from './UserDrawer.vue';
  import UserRecycleBinModal from './UserRecycleBinModal.vue';
  import PasswordModal from './PasswordModal.vue';
  import JThirdAppButton from '/@/components/jeecg/thirdApp/JThirdAppButton.vue';
  import UserQuitModal from './UserQuitModal.vue';
  import { useDrawer } from '/@/components/Drawer';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, searchFormSchema } from './user.data';
  import { listNoCareTenant, deleteUser, batchDeleteUser, getImportUrl, getExportUrl, frozenBatch,batchSetEmployeeApi } from './user.api';
  import { usePermission } from '/@/hooks/web/usePermission';
  import {getDictItems} from "@/api/common/api";
  import { JDictSelectTag,JInput} from '/@/components/Form'
  import ImportExcelProgress from './components/ImportExcelProgress.vue';
  const userTypeList = ref<any>([])
  const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  searchQuery,
  rowSelection,
  selectedRowKeys,
  fetchTable,
  pageChange,
} = useTable(listNoCareTenant);
  const { createMessage, createConfirm } = useMessage();
  const { isDisabledAuth } = usePermission();
  //注册drawer
  const [registerDrawer, { openDrawer }] = useDrawer();
  //回收站model
  const [registerModal, { openModal }] = useModal();
  //密码model
  const [registerPasswordModal, { openModal: openPasswordModal }] = useModal();
  //代理人model
  const [registerAgentModal, { openModal: openAgentModal }] = useModal();
  //离职代理人model
  const [registerQuitAgentModal, { openModal: openQuitAgentModal }] = useModal();
  //离职用户列表model
  const [registerQuitModal, { openModal: openQuitModal }] = useModal();


  //注册table数据

function searchReset(){
  queryParam.value = {};
  selectedRowKeys.value = []
  fetchTable(1);
}
  /**
   * 新增事件
   */
  function handleAdd() {
    openDrawer(true, {
      isUpdate: false,
      showFooter: true,
      tenantSaas: false,
    });
  }
  /**
   * 编辑事件
   */
  async function handleEdit(record: Recordable) {
    openDrawer(true, {
      record,
      isUpdate: true,
      showFooter: true,
      tenantSaas: false,
    });
  }
  /**
   * 详情
   */
  async function handleDetail(record: Recordable) {
    openDrawer(true, {
      record,
      isUpdate: true,
      showFooter: false,
      tenantSaas: false,
    });
  }
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    if ('admin' == record.username) {
      createMessage.warning('管理员账号不允许此操作！');
      return;
    }
    await deleteUser({ id: record.id }, fetchTable);
  }
  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    // let hasAdmin = unref(selectedRowKeys).filter((item) => item.username == 'admin');
    // if (unref(hasAdmin).length > 0) {
    //   createMessage.warning('管理员账号不允许此操作！');
    //   return;
    // }
    await batchDeleteUser({ ids: selectedRowKeys.value }, () => {
      selectedRowKeys.value = [];
      fetchTable();
    });
  }
  /**
   * 成功回调
   */
  function handleSuccess() {
    fetchTable();
  }

  /**
   * 打开修改密码弹窗
   */
  function handleChangePassword(username) {
    openPasswordModal(true, { username });
  }
  /**
   * 冻结解冻
   */
  async function handleFrozen(record, status) {
    if ('admin' == record.username) {
      createMessage.warning('管理员账号不允许此操作！');
      return;
    }
    await frozenBatch({ ids: record.id, status: status }, fetchTable);
  }
  /**
   * 批量冻结解冻
   */
  function batchFrozen(status) {
    // let hasAdmin = selectedRows.value.filter((item) => item.username == 'admin');
    // if (unref(hasAdmin).length > 0) {
    //   createMessage.warning('管理员账号不允许此操作！');
    //   return;
    // }
    createConfirm({
      iconType: 'warning',
      title: '确认操作',
      content: '是否' + (status == 1 ? '解冻' : '冻结') + '选中账号?',
      onOk: async () => {
        await frozenBatch({ ids: unref(selectedRowKeys).join(','), status: status }, fetchTable);
      },
    });
  }
  /**
   * 设为员工
   */
  function batchSetEmployee() {
    if (selectedRowKeys.value.length <= 0) {
          createMessage.warning('请选择一条记录！')
          return false
        } else {
          let ids = ''
          selectedRowKeys.value.forEach(function (val) {
            ids += val + ','
          })
  
          createConfirm({
            iconType: 'warning',
            title: '是否设置当前选中用户为员工？',
            onOk:async () => {
             const res = await batchSetEmployeeApi({ ids });
             if (res.success) {
              createMessage.success('设置成功！')
              fetchTable();
             }
             else {
              createMessage.error('设置失败！')
             }
            },
          });
        }
  }
  /**
   *同步钉钉和微信回调
   */
  function onSyncFinally({ isToLocal }) {
    // 同步到本地时刷新下数据
    if (isToLocal) {
      fetchTable();
    }
  }

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        // ifShow: () => hasPermission('system:user:edit'),
      },
    ];
  }
  /**
   * 下拉操作栏
   */
  function getDropDownAction(record): ActionItem[] {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '密码',
        //auth: 'user:changepwd',
        onClick: handleChangePassword.bind(null, record.username),
      },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
        },
      },
      {
        label: '冻结',
        ifShow: record.status == 1,
        popConfirm: {
          title: '确定冻结吗?',
          confirm: handleFrozen.bind(null, record, 2),
        },
      },
      {
        label: '解冻',
        ifShow: record.status == 2,
        popConfirm: {
          title: '确定解冻吗?',
          confirm: handleFrozen.bind(null, record, 1),
        },
      },
    ];
  }
  async function initUserType() {
 
    const res = await getDictItems('user_type')
    userTypeList.value = res
  }
  initUserType()
  fetchTable(1)
</script>

<style scoped></style>
