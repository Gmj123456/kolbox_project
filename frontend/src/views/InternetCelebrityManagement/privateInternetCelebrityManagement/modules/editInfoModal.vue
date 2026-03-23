<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    :closable="true"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="保存"
    cancelText="关闭"
    width="380px"
    class="EditInfoModal"
  >
    <a-spin :spinning="confirmLoading">
      <a-form
        ref="formRef"
        :model="model"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
        :rules="rules"
      >
        <a-row :gutter="12">
          <a-col :span="24" style="margin-bottom: 10px">
            <div style="display: flex; align-items: center; justify-content: center">
              <img
                v-if="model.avatarUrl"
                :src="getImage(model.avatarUrl)"
                style="height: 70px; border-radius: 100%; width: 70px; cursor: pointer"
                :preview="model.id"
              />
              <img
                v-else
                src="@/assets/images/avatar.png"
                style="border-radius: 100%; width: 70px; height: 70px"
              />
              <div style="position: absolute; bottom: -2px; right: 144px">
                <img
                  v-if="model.platformType == 0"
                  class="platformType-img"
                  src="@/assets/images/ins.png"
                  alt=""
                />
                <img
                  v-if="model.platformType == 1"
                  class="platformType-img"
                  src="@/assets/images/yt.png"
                  alt=""
                />
                <img
                  v-if="model.platformType == 2"
                  class="platformType-img"
                  src="@/assets/images/tk.png"
                  alt=""
                />
              </div>
            </div>
            <!-- <div style="text-align: center; font-weight: 700">{{ model.account }}</div> -->
          </a-col>

          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="性别">
              <a-radio-group v-model:value="model.gender">
                <a-radio :value="0">男</a-radio>
                <a-radio :value="1">女</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="网红账号"
              name="account"
            >
              <a-input v-model:value="model.account" placeholder="请输入网红账号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="国家">
              <a-select
                show-search
                option-filter-prop="label"
                v-model:value="model.countryId"
                placeholder="请选择国家"
              >
                <a-select-option v-for="item in countrys" :key="item.id" :value="item.id" :label="item.country">
                  {{ item.country }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="网红邮箱"
            >
              <a-input
                style="width: 100%; background-color: #fff; color: #0b1019"
                disabled
                :value="model.email"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="段位"
              prop="isTopStar"
            >
              <a-select
                show-search
                option-filter-prop="label"
                v-model:value="model.isTopStar"
                placeholder="请选择段位"
              >
                <a-select-option
                  v-for="item in celebritySegmentList"
                  :key="item.value"
                  :value="item.value"
                  :label="item.title"
                >
                  {{ item.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="建联邮箱"
            >
              <a-input
                style="width: 100%; background-color: #fff; color: #0b1019"
                disabled
                :value="model.celebrityContactEmail"
              ></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>
<script setup>
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import {getDictItems} from "@/api/common/api";
import { useMessage } from '/@/hooks/web/useMessage';
import { getCommonCountryApi } from '/@/api/sys/systemCommon';

const { createMessage: $message } = useMessage();

// 响应式数据
const title = ref('编辑');
const visible = ref(false);
const confirmLoading = ref(false);
const countrys = ref([]);
const formRef = ref(null);
const celebritySegmentList = ref([]);
const model = ref({});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 19 },
};

const rules = reactive({
  account: [
    { required: true, message: '请输入网红账号' },
    {
      max: 50,
      message: '网红账号最多50个字符',
      trigger: 'change',
    },
    {
      validator: (_rule, value, callback) => {
        if (value && /\s/.test(value)) {
          callback(new Error('网红账号不能包含空格'));
        } else {
          callback();
        }
      },
    },
  ],
});

// 定义 emits
const emit = defineEmits(['close', 'ok']);



function getImage(url) {
  return url.includes('https') || url.includes('http')
    ? url
    : 'https://gemstone-image.kolbox.com/avatar/' + url;
}

async function initCountry() {
  const res = await getCommonCountryApi({});
  countrys.value = res || [];
}

async function getCelebritySegmentList() {
  const res = await getDictItems('celebrity_segment');
  celebritySegmentList.value = res.map(item => ({
  ...item,
  value:Number(item.value),
 
  }));
}

async function handleOk() {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    const res = await defHttp.get({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/editCelebrityPrivate',
      params: {
        id: model.value.id,
        gender: model.value.gender,
        countryId: model.value.countryId,
        account: model.value.account,
        isTopStar: model.value.isTopStar,
      },
    },{isTransformResponse: false});
    if (res.success) {
      $message.success(res.message);
      emit('ok');
      handleCancel();
    } else {
      $message.error(res.message);
    }
   
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  visible.value = false;
}

function edit(record) {
  model.value = { ...record };
  initCountry();
  getCelebritySegmentList();
  visible.value = true;
}

// 暴露方法给父组件
defineExpose({
  edit,
});
</script>
<style>
.EditInfoModal .ant-modal-body {
  padding: 12px 12px !important;
}
</style>
<style lang="less" scoped>
.platformType-img {
  display: inline-block;
  width: 24px;
  height: 24px;

  img {
    width: 100%;
    height: 100%;
  }
}
</style>
