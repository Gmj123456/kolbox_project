<template>
  <a-modal
    centered
    title="建联邮箱更换网红顾问"
    v-model:open="visible"
    :width="1200"
    @cancel="handleCancel"
    @ok="handleOk"
    class="ReplaceContachEmailModel"
    :confirmLoading="confirmLoading"
  >
    <a-form :model="queryParam">
      <a-row :gutter="12">
        <a-col :span="4">
          <a-form-item>
            <a-select
              
              v-model:value="queryParam.celebrityCounselorOldId"
              placeholder="网红顾问"
              @change="changeUser($event, '0')"
              allowClear
            >
              <a-select-option
                v-for="item in celebrityCounselorList"
                :key="item.id"
                :value="item.id"
              >
                <a-tooltip :title="item.realname">
                  {{ item.username }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="4" >
          <a-form-item>
            <a-input placeholder="建联邮箱" v-model:value="queryParam.contactEmail"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item>
            <a-button
              type="primary"
              @click="searchQuery"
              :icon="h(SearchOutlined)"
             
              >查询</a-button
            >
            <a-button
              @click="searchReset"
              :icon="h(ReloadOutlined)"
              style="margin-left: 8px"
              >重置</a-button
            >
          </a-form-item>
        </a-col>
        <a-col :span="4" style="margin-left: 15px">
          <a-form-item>
            <a-select
              style="width: 90%"
              v-model:value="queryParam.celebrityCounselorId"
              placeholder="网红顾问"
              @change="changeUser($event, '1')"
              allowClear
            >
              <a-select-option
                v-for="item in celebrityCounselorList"
                :key="item.id"
                :value="item.id"
              >
                <a-tooltip :title="item.realname">
                  {{ item.username }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-spin :spinning="loading">
      <a-transfer
        :loading="loading"
         :data-source="mockData"
        v-model:target-keys="targetKeys"
  
        :list-style="{
          width: '555px',
          height: '500px',
        }"
        :render="(item) => item.title"
  
       
        @scroll="handleScroll"
        :show-select-all="false"
      >
        <template
          #children="{
            direction,
            filteredItems,
            selectedKeys,
            onItemSelectAll,
            onItemSelect,
          }"
        >
          <a-table
            size="small"
          
            :row-selection="
              getRowSelection({
                selectedKeys,
                onItemSelectAll,
                onItemSelect,
              })
            "
            :pagination="false"
            :columns="direction === 'left' ? leftColumns : rightColumns"
            :data-source="filteredItems"
            :scroll="{ y: 400 }"
          />
        </template>
      </a-transfer>
    </a-spin>
  </a-modal>
</template>
<script setup>
import { ref, reactive, h } from 'vue';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const visible = ref(false);
const confirmLoading = ref(false);
const loading = ref(false);
const mockData = ref([]);

const queryParam = reactive({
  celebrityCounselorOldId: undefined,
  celebrityCounselorId: undefined,
  contactEmail: undefined,
});

const celebrityCounselorList = ref([]);
const targetKeys = ref([]);

const leftColumns = [
  {
    title: "#",
    dataIndex: "index",
    width: "50px",
    customRender: ({ index }) => parseInt(index) + 1,
  },
  {
    dataIndex: "contactEmail",
    title: "建联邮箱",
  },
  {
    dataIndex: "emailDisplayName",
    title: "邮箱名称",
    width: "150px",
  },
];

const rightColumns = [
  {
    title: "#",
    dataIndex: "index",
    width: "50px",
    customRender: ({ index }) => parseInt(index) + 1,
  },
  {
    dataIndex: "contactEmail",
    title: "建联邮箱",
  },
  {
    dataIndex: "emailDisplayName",
    title: "邮箱名称",
    width: "150px",
  },
];

const getRowSelection = ({
  disabled,
  selectedKeys,
  onItemSelectAll,
  onItemSelect,
}) => {
return {
    getCheckboxProps: (item) => ({
      disabled: disabled || item.disabled,
    }),
    onSelectAll(selected, selectedRows) {
      const treeSelectedKeys = selectedRows.filter(item => !item.disabled).map(({ key }) => key);
      onItemSelectAll(treeSelectedKeys, selected);
    },
    onSelect({ key },selected) {
      onItemSelect(key, selected);
    },
    selectedRowKeys: selectedKeys,
  }
}
function changeUser(_e, _type) {
  // Placeholder for future logic
}

function show() {
  mockData.value = [];
  targetKeys.value = [];

  Object.assign(queryParam, {
    celebrityCounselorOldId: undefined,
    celebrityCounselorId: undefined,
    contactEmail: undefined,
  });
  visible.value = true;
  initCelebrityCounselor();
}

async function initCelebrityCounselor() {
  const res = await defHttp.get({ url: "/sys/user/queryAllCelebrityCounselor" },{isTransformResponse:false});
  if (res.success) {
    celebrityCounselorList.value = res.result;
  }
}

function close() {
  emit("close");
  visible.value = false;
}

async function handleOk() {
  if (!queryParam.celebrityCounselorId) {
    createMessage.warning("请选择需要更换的网红顾问！");
    return;
  }
  if (targetKeys.value.length < 1) {
    createMessage.warning("请至少选择一个建联邮箱");
    return;
  }
  
  const selectItem = mockData.value.filter((item) =>
    targetKeys.value.includes(item.id)
  );
  let contactEmails = [];
  selectItem.forEach((item) => {
    contactEmails.push(item.contactEmail);
  });
  confirmLoading.value = true;
  const res = await defHttp.get({ 
    url: "/storeUserContactEmail/updateContactEmailCounselor", 
    params: {
      contactEmails: contactEmails.join(","),
      celebrityCounselorOldId: queryParam.celebrityCounselorOldId,
      celebrityCounselorId: queryParam.celebrityCounselorId,
    }
  },{isTransformResponse:false});
  
  if (res.success) {
    createMessage.success(res.message);
    emit("ok");
    close();
  } else {
    createMessage.warning(res.message);
  }
  confirmLoading.value = false;
}

function handleCancel() {
  close();
}

async function loadData() {
  if (!queryParam.celebrityCounselorOldId) {
    createMessage.warning("请选择网红顾问！");
    return;
  }
  loading.value = true;
  const res = await defHttp.get({ 
    url: "/storeUserContactEmail/queryListByCounselor", 
    params: {
      sysUserId: queryParam.celebrityCounselorOldId,
      contactEmail: queryParam.contactEmail,
    }
  },{isTransformResponse:false});
  if (res.success) {
    for (let index = 0; index < res.result.length; index++) {
      const element = res.result[index];
      element.title = element.contactEmail + " " + element.emailDisplayName;
      element.key = element.id;
    }
    mockData.value = res.result.filter((item) => {
      return item.emailStatus;
    });
    console.log(mockData.value);
  }
  loading.value = false;
}

function searchQuery() {
  loadData();
}

function searchReset() {
  Object.assign(queryParam, {
    sysUserId: undefined,
    contactEmail: undefined,
  });
  loadData();
}

function handleChange(nextTargetKeys, _direction, _moveKeys) {
  targetKeys.value = nextTargetKeys;
}



function handleScroll(direction, e) {
  console.log("direction:", direction);
  console.log("target:", e.target);
}

defineExpose({
  show,
});
</script>
<style lang="less">
.ReplaceContachEmailModel  .ant-table-body .ant-table-placeholder{
    height: 400px !important;
}
</style>
<style scoped>
.ant-table-tbody tr td {
  color: #0b1019 !important;
}

</style>
