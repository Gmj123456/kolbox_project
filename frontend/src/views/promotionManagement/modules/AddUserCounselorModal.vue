<template>
  <a-modal
    title="新增商家顾问"
    :maskClosable="false"
    :open="visible"
    :confirmLoading="confirmLoading"
    :width="400"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form :model="formState" :rules="rules" ref="formRef" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="二维码" required>
        <a-upload
          name="file"
          list-type="picture-card"
          class="avatar-uploader"
          :show-upload-list="false"
          :action="uploadQr"
          :before-upload="beforeUpload"
          @change="handleChange"
        >
          <img v-if="formState.qrCode" :src="formState.imageUrl" alt="avatar" width="100" height="100" />
          <div v-else>
            <LoadingOutlined v-if="uploadLoading" />
            <PlusOutlined v-else />
            <div class="ant-upload-text">上传</div>
          </div>
        </a-upload>
      </a-form-item>
      <a-form-item name="username" label="账号">
        <a-input v-model:value="formState.username" placeholder="请输入账号"/>
      </a-form-item>
      <a-form-item name="phone" label="手机号">
        <a-input v-model:value="formState.phone" placeholder="请输入手机号"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { uploadUrl } from '/@/api/common/api';
import { getFileAccessHttpUrl } from '/@/utils/common/compUtils';
import { checkOnlyUser } from '/@/api/sys/user';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const visible = ref(false);
const confirmLoading = ref(false);
const uploadLoading = ref(false);

// 布局配置
const labelCol = { span: 4 };
const wrapperCol = { span: 19 };

// 上传 URL - 使用 /upload 端点（原代码逻辑）
const uploadQr = 'https://p.kolbox.com/common/file/upload';

// 表单数据
const formState = ref({
  username: '',
  phone: '',
  imageUrl: '',
  qrCode: '',
});

// 自定义验证器：检查账号是否已存在
const checkMerchantname = async (_rule: any, value: string) => {
  if (!value) {
    return Promise.resolve();
  }
  formState.value.username = value;
  try {
    const res = await checkOnlyUser({ username: value });
    return res.success ? Promise.resolve() : Promise.reject(res.message || '账号已存在');
  } catch (error) {
    return Promise.reject('验证失败');
  }
};

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入账号!' },
    { validator: checkMerchantname, trigger: 'blur' },
  ],
  phone: [{ required: true, message: '请输入手机号!' }],
};

// 文件上传处理
function handleChange(info: any) {
  console.log(info)
  if (info.file.status === 'uploading') {
    uploadLoading.value = true;
    return;
  }

  if (info.file.status === 'done') {
    uploadLoading.value = false;
    const response = info.file.response;
    if (response.success) {
      formState.value.imageUrl = 'https://image.kolbox.com/' + response.message;
      formState.value.qrCode = response.message;
    } else {
      formState.value.imageUrl = '';
      formState.value.qrCode = '';
      $message.warning(response.message);
    }
  }
}

// 上传前校验
function beforeUpload(file: File): Promise<boolean> {
  return new Promise((resolve, reject) => {
    const fileType = file.type;
    if (fileType.indexOf('image') < 0) {
      $message.warning('请上传图片');
      reject(false);
      return;
    }
    const sizeOk = file.size < 1024 * 1024 * 2;
    if (!sizeOk) {
      $message.error('照片大小超过2M！');
      reject(false);
      return;
    }
    resolve(true);
  });
}

// 打开/关闭
function close() {
  visible.value = false;
  formRef.value?.resetFields();
  Object.assign(formState.value, {
    username: '',
    phone: '',
    imageUrl: '',
    qrCode: '',
  });
}

function add({counselorUserId = null, counselorUserName}) {
  if (counselorUserId) {
    Object.assign(formState.value, {
    counselorUserId,
    username: '',
    phone: '',
    imageUrl: '',
    qrCode: '',
    counselorUserName
  });
  }else{

    Object.assign(formState.value, {
      username: '',
      phone: '',
      imageUrl: '',
      qrCode: '',
    });
  }
  formRef.value?.resetFields();
  visible.value = true;
}
function addnew({counselorUserId,counselorUserName}) {
  Object.assign(formState.value, {
    counselorUserId,
    username: '',
    phone: '',
    imageUrl: '',
    qrCode: '',
    counselorUserName
  });
  formRef.value?.resetFields();
  visible.value = true;
}
// 提交
async function handleOk() {
  if (!formState.value.qrCode) {
    $message.warning('请上传二维码！');
    return;
  }

  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    const res = await defHttp.post({
      url: '/storeUserCounselor/add',
      data: {
        ...formState.value,
      },
    });

    if (res) {
      // $message.success(res.message);
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
  addnew,
  add,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
  (e: 'close'): void;
}>();
</script>

<style lang="less" scoped>

</style>
