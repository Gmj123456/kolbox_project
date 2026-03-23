<template>
  <a-modal
    :title="title"
    :width="500"
    v-model:open="visible"
    centered
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    class="InvUnitModal"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="顾问">
              <span style="font-size: 12px"> {{ model.celebrityCounselorName }}</span>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              prop="email"
              label="邮箱"
            >
              <a-input
                style="width: 100%"
                placeholder="请输入邮箱"
                v-model:value="model.email"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              prop="celebrityContactEmail"
              label="建联邮箱"
            >
              <a-select
                v-model:value="model.celebrityContactEmail"
                show-search
                @change="celebrityContactEmailChange"
              >
                <a-select-option
                  v-for="item in contactEmailList"
                  :key="item.contactEmail"
                  :value="item.id"
                >
                  {{ item.contactEmail }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              prop="contractTime"
              label="建联时间"
            >
              <a-date-picker
                style="width: 100%"
                v-model:value="model.contractTime"
                placeholder="请选择建联时间"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24" v-for="(item, index) in model.contentQuotation" :key="index">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="内容报价"
            >
              <a-select
                v-model:value="item.videoTag"
                style="width: 50%"
                placeholder="请选择内容"
              >
                <a-select-option v-for="tag in videoTagsList" :key="tag">{{
                  tag
                }}</a-select-option>
              </a-select>
              <a-input
                v-model:value="item.contractAmount"
                @blur="roundValue(item, index)"
                placeholder="费用"
                style="margin-left: 10px; width: calc(50% - 64px)"
              >
                <template #addonAfter>$</template>
              </a-input>
              <span style="margin-left: 10px">
                <a>
                  <MinusCircleOutlined
                    style="font-size: 18px; margin-right: 8px; color: red"
                    @click="removeContent(index)"
                  />
                </a>
                <a
                  v-if="
                    index == model.contentQuotation.length - 1 &&
                    index < videoTagsList.length - 1
                  "
                >
                  <PlusCircleOutlined
                    style="font-size: 18px"
                    @click="addContent(index)"
                  />
                </a>
              </span>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
              <a-input
                v-model:value="model.remark"
                :maxLength="100"
                placeholder="请输入备注"
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
import { useMessage } from '/@/hooks/web/useMessage';
import dayjs from 'dayjs';
import { MinusCircleOutlined, PlusCircleOutlined } from '@ant-design/icons-vue';

const { createMessage: $message } = useMessage();

// 响应式数据
const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const contactEmailList = ref([]);
const videoTagsList = ref([]);
const formRef = ref(null);

const model = reactive({
  email: undefined,
  celebrityContactEmail: undefined,
  contractTime: undefined,
  contentQuotation: [],
  remark: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 4 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 20 },
};

const validatorRules = {
  email: [
    {
      required: true,
      message: '请输入邮箱!',
    },
  ],
  celebrityContactEmail: [
    {
      required: true,
      message: '请输入建联邮箱!',
    },
  ],
  contractTime: [
    {
      required: true,
      message: '请选择建联时间!',
    },
  ],
};

const url = {
  add: '',
  edit: '/privateCounselor/edit',
};

// 定义 emits
const emit = defineEmits(['close', 'ok']);

// 方法
function roundValue(_item, index) {
  if (model.contentQuotation[index].contractAmount) {
    let num = Number(model.contentQuotation[index].contractAmount);
    if (isNaN(num)) {
      model.contentQuotation[index].contractAmount = undefined;
      return;
    }
    console.log(num);
    if (num > 9999999) {
      num = 9999999;
    }
    model.contentQuotation[index].contractAmount = num;
  }
}

function checkContentItemFilled() {
  for (let i = 0; i < model.contentQuotation.length; i++) {
    const item = model.contentQuotation[i];
    console.log(item);
    if (!item.videoTag) {
      $message.warning(`请选择内容`);
      return false;
    }
    if (!item.contractAmount) {
      $message.warning(`请填写签约费用`);
      return false;
    }
  }
  return true;
}

function checkVideoTagsDuplicate() {
  const videoTagsSet = new Set();
  for (const item of model.contentQuotation) {
    if (videoTagsSet.has(item.videoTag)) {
      return false;
    }
    videoTagsSet.add(item.videoTag);
  }
  return true;
}

function addContent(_index) {
  if (model.contentQuotation.length == videoTagsList.value.length) {
    $message.warning('最多添加' + videoTagsList.value.length + '个内容');
    return;
  }
  model.contentQuotation.push({
    videoTag: undefined,
    contractAmount: undefined,
  });
}

function removeContent(index) {
  if (model.contentQuotation.length == 1) {
    $message.warning('请至少添加一个内容');
    return;
  }
  model.contentQuotation.splice(index, 1);
}

async function initContactEmail() {
  const res = await defHttp.get({ url: '/storeUserContactEmail/queryListByCounselor' });
  contactEmailList.value = res.filter((item) => {
    return item.emailStatus;
  });
}

function celebrityContactEmailChange(e) {
  const filterEmail = contactEmailList.value.filter((item) => {
    return item.id === e;
  });
  if (filterEmail.length > 0) {
    model.celebrityContactEmail = filterEmail[0].contactEmail;
  }
}

function add() {
  edit({});
}

function edit(record, videoTagsListParam) {
  videoTagsList.value = videoTagsListParam || [];
  initContactEmail();
  Object.assign(model, record);
  model.contractTime = model.contractTime ? dayjs(model.contractTime) : dayjs(new Date());
  model.contentQuotation = model.contractInfo ? JSON.parse(model.contractInfo) : [];
  visible.value = true;
}

function close() {
  emit('close');
  visible.value = false;
  formRef.value?.clearValidate();
}

async function handleOk() {
  if (!checkVideoTagsDuplicate()) {
    $message.warning('内容标签不能重复');
    return;
  }
  if (!checkContentItemFilled()) {
    return;
  }
  
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (!model.id) {
      httpurl += url.add;
      method = 'post';
    } else {
      httpurl += url.edit;
      method = 'put';
    }
    const params = {
      ...model,
      contractInfo: JSON.stringify(model.contentQuotation),
      contractTime: model.contractTime
        ? model.contractTime.format('YYYY-MM-DD 00:00:00')
        : null,
    };
    
    const res = await defHttp.request({
      url: httpurl,
      method,
      data: params,
    },{isTransformResponse: false});
    
    if (res.success) {
      $message.success(res.message);
      emit('ok');
      close();
    } else {
      $message.warning(res.message);
    }
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

// 暴露方法给父组件
defineExpose({
  edit,
  add,
});
</script>

<style lang="less">
.InvUnitModal {
  .ant-form-item {
    margin-bottom: 5px !important;
  }
}
</style>