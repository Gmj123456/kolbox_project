<template>
  <a-modal
    :maskClosable="false"
    title="交接明细"
    :width="600"
    v-model:visible="visible"
    :confirm-loading="confirmLoading"
    class="AdjustmentCounselorDetail"
    @ok="handleOk"
    @cancel="handleCancel"
    cancel-text="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form
        ref="formRef"
        :model="model"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
      >
        <a-row :gutter="12" style="margin-bottom: 10px">
          <a-col :span="12">
            <div
              style="
                background-color: #f6f8fa;
                padding: 10px 10px 0 10px;
                display: flex;
                align-items: center;
                font-size: 12px;
                color: #0b1019;
              "
            >
              <span style="width: 90px; display: inline-block">交接人：</span>
              <span
                :content="model.fromCounselorName"
                style="display: inline-block; width: calc(100% - 90px)"
                >{{ model.fromCounselorName }}</span
              >
            </div>
            <div
              style="
                background-color: #f6f8fa;
                padding: 0 10px 10px 10px;
                display: flex;
                align-items: center;
                font-size: 12px;
                color: #0b1019;
              "
            >
              <span style="width: 90px; display: inline-block">交接人邮箱：</span>
              <span style="display: inline-block; width: calc(100% - 90px)">
                <EllipsisTooltip
                  v-if="model.fromContactEmail"
                  :text="model.fromContactEmail"
                  :lines="1"
                />
                <span v-else>--</span>
              </span>
            </div>
          </a-col>
          <a-col :span="12">
            <div
              style="
                background-color: #f6f8fa;
                padding: 10px 10px 0 10px;
                display: flex;
                align-items: center;
                font-size: 12px;
                color: #0b1019;
              "
            >
              <span style="width: 90px; display: inline-block">接收人：</span>
              <span style="display: inline-block; width: calc(100% - 90px)">{{
                model.toCounselorName
              }}</span>
            </div>
            <div
              style="
                background-color: #f6f8fa;
                padding: 0 10px 10px 10px;
                display: flex;
                align-items: center;
                font-size: 12px;
                color: #0b1019;
              "
            >
              <span style="width: 90px; display: inline-block">接收人邮箱：</span>
              <span style="display: inline-block; width: calc(100% - 90px)">
                <EllipsisTooltip
                  v-if="model.toContactEmail"
                  :text="model.toContactEmail"
                  :lines="1"
                />
                <span v-else>--</span>
              </span>
            </div>
          </a-col>
        </a-row>
      </a-form>
      <a-table  :rangeSelection="false" :scroll="{ y: 400 }" size="small" :pagination="{hideOnSinglePage:true}" :columns="columns" :data-source="dataSource" >
          <template #bodyCell="{ column, record ,index}">
            <template v-if="column.dataIndex === 'index'">
              {{ index + 1 }}
            </template>
            <template v-if="column.dataIndex === 'platformType'">
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
            <template v-if="column.dataIndex === 'followersNum'">
              <span v-if="record.followersNum !== null && record.followersNum !== ''">{{
                getFollower(record.followersNum)
              }}</span>
              <span v-else>--</span>
            </template>
            <!-- <template v-else>
              {{ text }}
            </template> -->
          </template>
        </a-table>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { message } from 'ant-design-vue';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';

// 定义组件名称
defineOptions({
  name: 'AdjustmentCounselorDetail'
});

// 定义 emits
const emit = defineEmits(['close']);

// 响应式数据
const visible = ref(false);
const confirmLoading = ref(false);
const model = reactive({});
const dataSource = ref([]);
const formRef = ref();

const labelCol = {
  span: 6,
};

const wrapperCol = {
  span: 18,
};

const columns = [
  {
    title: '#',
    dataIndex: 'index',
    width: '50px',
    align: 'center',
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
  
  
  },
];

const ipagination = reactive({
  current: 1,
  pageSize: 50,
  pageSizeOptions: ['50', '100', '200'],
  showTotal: (total, range) => {
    return range[0] + '-' + range[1] + ' 共' + total + '条';
  },
  showQuickJumper: true,
  showSizeChanger: true,
  total: 0,
});

// 方法定义
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

function handleTableChange(pagination, filters, sorter, extra) {
  console.log(pagination, filters, sorter, extra);
  ipagination.current = pagination.current;
  ipagination.pageSize = pagination.pageSize;
}

async function intDetail() {
  confirmLoading.value = true;
  try {
    const res = await defHttp.get({
      url: '/counselorChangeDetail/listAll',
      params: { changeLogId: model.id }
    });
    
    if (res) {
      dataSource.value = res;
    } else {
      message.error(res.message);
    }
  } finally {
    confirmLoading.value = false;
  }
}

function close() {
  dataSource.value = [];
  emit('close');
  visible.value = false;
}

function handleOk() {
  close();
}

function handleCancel() {
  close();
}
async function edit(record) {
  Object.assign(model, record);
  visible.value = true;
  await intDetail();
  
  // 设置表格最小高度

}
  
 async function add() {
    // 清空 model
    Object.keys(model).forEach(key => {
      delete model[key];
    });
    visible.value = true;
  }
// 对外暴露方法
defineExpose({
  edit,
  add,
});
</script>

<style lang="less">

 .AdjustmentCounselorDetail .ant-table-body {
  height: 400px !important;
 }
 .AdjustmentCounselorDetail .ant-pagination .ant-pagination-item{
  background-color: #ffffff !important;
  border: 1px solid #d9d9d9 !important;

 }

 .AdjustmentCounselorDetail .ant-pagination .ant-pagination-item-active{
  border-color: #3155ed !important;
  color: #3155ed !important;
 }
 .AdjustmentCounselorDetail .ant-pagination .ant-pagination-item-active a{
  color: #3155ed !important;
 }
</style>