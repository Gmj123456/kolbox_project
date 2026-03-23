<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="400"
    :open="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
 
    cancelText="关闭"
  >
  <template #footer>
      <a-button key="back" @click="handleCancel"> 关闭</a-button>
    
    </template>
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="商家顾问">
          <span>{{ model.sellerCounselorName }}</span>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户名称">
          <span>{{ model.username }}</span>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="公司名称">
          <span>{{ model.companyName }}</span>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="获客渠道">
          <span>{{ parseAcquisitionChannel(model.acquisitionChannel) }}</span>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="微信ID">
          <span>{{ model.wxAccount }}</span>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="飞书名称">
          <span>{{ model.feishuAccount }}</span>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="邮箱">
          <span>{{ model.email }}</span>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
          <span>{{ model.remark }}</span>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { getDictItems } from '/@/api/common/api';

// 表单引用
const formRef = ref();

// 状态
const title = ref('商家信息');
const visible = ref(false);
const confirmLoading = ref(false);
const acquisitionChannelList = ref<any[]>([]);

// 布局配置
const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 19 },
};

// 表单数据
const model = reactive<any>({});

// 解析获客渠道
function parseAcquisitionChannel(acquisitionChannel: any) {
  const item = acquisitionChannelList.value.find(
    (item) => item.value === acquisitionChannel
  );
  return item ? item.title : '';
}

// 初始化获客渠道列表
async function initAcquisitionChannelList() {
  const res = await getDictItems('customer_acquisition_method');
  if (res) {
    acquisitionChannelList.value = res;
  }
}

// 显示
function show(record: any) {
  visible.value = true;
  initAcquisitionChannelList();
  getSellerCounselor(record.sellerId);
}

// 获取商家顾问信息
async function getSellerCounselor(id: string) {
  const res = await defHttp.get({
    url: '/sys/user/getSellerById',
    params: { id },
  });
  if (res) {
    Object.assign(model, res);
  }
}

// 关闭
function close() {
  visible.value = false;
}

function handleCancel() {
  close();
}

// 暴露方法
defineExpose({
  show,
  close,
});
</script>

<style lang="less" scoped></style>
