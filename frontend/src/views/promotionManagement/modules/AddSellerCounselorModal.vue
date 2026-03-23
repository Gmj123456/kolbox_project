<template>
  <a-modal
    title="新增商家账号"
    :open="visible"
    :confirmLoading="confirmLoading"
    :maskClosable="false"
    :width="440"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form :model="formState" :rules="rules" ref="formRef" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="商家顾问">
        <span style="font-size: 12px">{{ formState.sellerCounselorName }}</span>
      </a-form-item>
      <a-form-item name="username" label="用户名称">
        <a-input
          v-model:value="formState.username"
          autocomplete="new-username"
          placeholder="请输入用户名称"
        />
      </a-form-item>
      <a-form-item name="password" label="用户密码">
        <a-input-password
          v-model:value="formState.password"
          autocomplete="new-password"
          placeholder="请输入用户密码"
        />
      </a-form-item>
      <a-form-item name="confirmPassword" label="确认密码">
        <a-input-password
          v-model:value="formState.confirmPassword"
          placeholder="确认密码"
        />
      </a-form-item>
      <a-form-item name="companyName" label="公司名称">
        <a-input v-model:value="formState.companyName" placeholder="请输入公司名称" />
      </a-form-item>
      <a-form-item name="acquisitionChannel" label="获客渠道">
        <a-select v-model:value="formState.acquisitionChannel" placeholder="请选择获客渠道">
          <a-select-option v-for="item in acquisitionChannelList" :key="item.value" :value="item.value">
            {{ item.title }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item name="wxAccount" label="微信ID">
        <a-input v-model:value="formState.wxAccount" placeholder="请输入微信ID" />
      </a-form-item>
      <a-form-item name="feishuAccount" label="飞书名称">
        <a-input v-model:value="formState.feishuAccount" placeholder="请输入飞书名称" />
      </a-form-item>
      <a-form-item name="email" label="邮箱">
        <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item name="remark" label="备注">
        <a-input v-model:value="formState.remark" placeholder="请输入备注" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { getDictItems } from '/@/api/common/api';
import { checkOnlyUser } from '/@/api/sys/user';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const visible = ref(false);
const confirmLoading = ref(false);
const acquisitionChannelList = ref<any[]>([]);

// 布局配置
const labelCol = { span: 4 };
const wrapperCol = { span: 20 };

// 表单数据
const formState = ref({
  sellerCounselorId: undefined,
  sellerCounselorName: '',
  username: '',
  password: '',
  confirmPassword: '',
  companyName: '',
  acquisitionChannel: undefined,
  wxAccount: '',
  feishuAccount: '',
  email: '',
  remark: '',
});

// 自定义验证器：检查用户名是否已存在
const checkUsername = async (_rule: any, value: string) => {
  if (!value) {
    return Promise.resolve();
  }
  formState.value.username = value;
  try {
    const res = await checkOnlyUser({ username: value });
    if (res.success) {
      return Promise.resolve();
    } else {
      return Promise.reject(res.message || '用户名已存在');
    }
  } catch (error) {
    return Promise.reject('验证失败');
  }
};

// 自定义验证器：确认密码
const validateConfirmPassword = (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject('请再次输入新密码');
  }
  if (value !== formState.value.password) {
    return Promise.reject('两次输入新密码不一致!');
  }
  return Promise.resolve();
};

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名称!', trigger: 'change' },
    { validator: checkUsername, trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入用户密码!', trigger: 'change' },
    {
      pattern: /^[0-9A-Za-z]{6,}$/,
      message: '密码由6位数字或大小写字母组成!',
      trigger: 'change',
    },
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'change' }],
  acquisitionChannel: [{ required: true, message: '请选择获客渠道!', trigger: 'change' }],
  companyName: [{ required: true, message: '请输入公司名称!', trigger: 'change' }],
  // email: [{ required: true, message: '请输入商家邮箱!', trigger: 'change' }],
};

// 初始化获客渠道列表
async function initAcquisitionChannelList() {
  const res = await getDictItems('customer_acquisition_method');
  if (res) {
    acquisitionChannelList.value = res;
  }
}

// 打开/关闭
function close() {
  visible.value = false;
  formRef.value?.resetFields();
  Object.assign(formState.value, {
    sellerCounselorId: undefined,
    sellerCounselorName: '',
    username: '',
    password: '',
    confirmPassword: '',
    companyName: '',
    acquisitionChannel: undefined,
    wxAccount: '',
    feishuAccount: '',
    email: '',
    remark: '',
  });
}

function add(record?: any) {
  if (record) {
    Object.assign(formState.value, record);
  }
  formRef.value?.resetFields();
  initAcquisitionChannelList();
  visible.value = true;
}

// 提交
async function handleOk() {
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  if (!formState.value.wxAccount && !formState.value.feishuAccount && !formState.value.email) {
    $message.warning('微信ID、飞书名称、邮箱至少输入一个');
    return;
  }

  try {
    confirmLoading.value = true;
    const res = await defHttp.post({
      url: '/sys/user/addSeller',
      data: {
        ...formState.value,
      },
    },{isTransformResponse:false});

    if (res.success) {
      $message.success(res.message);
      emit('ok');
      close();
    } else {
      $message.warning(res.message);
    }
  } catch (error) {
    console.error(error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

// 暴露方法
defineExpose({
  add,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
  (e: 'close'): void;
}>();
</script>

<style lang="less" scoped></style>
