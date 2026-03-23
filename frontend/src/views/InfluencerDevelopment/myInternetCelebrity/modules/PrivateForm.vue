<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="500"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="modelState" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-row>
          <a-col :span="24">
            <a-form-item label="账号">
              <span style="margin-right: 10px">{{ modelState.uniqueId }}</span>
              <span class="platformType-img" v-if="modelState.platformType == 0">
                <img
                  src="@/assets/images/ins.png"
                  alt=""
                />
              </span>
              <span class="platformType-img" v-if="modelState.platformType == 1">
                <img src="@/assets/images/yt.png" alt="" />
              </span>
              <span class="platformType-img" v-if="modelState.platformType == 2">
                <img src="@/assets/images/tk.png" alt="" />
              </span>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item name="gender" label="性别">
              <a-radio-group v-model:value="modelState.gender">
                <a-radio-button :value="0">
                  <img
                    style="width: 22px; height: 22px; transform: translateY(-1.5px)"
                    src="@/assets/images/man.png"
                    alt=""
                  />
                </a-radio-button>
                <a-radio-button :value="1">
                  <img
                    style="width: 22px; height: 22px; transform: translateY(-1.5px)"
                    src="@/assets/images/woman.png"
                    alt=""
                  />
                </a-radio-button>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="个性化标签">
              <a-select
                showArrow
                v-model:value="modelState.personalTags"
                style="width: 100%"
                show-search
                allowClear
                option-filter-prop="label"
                mode="multiple"
                :maxTagCount="4"
                placeholder="请选择个性化标签"
              >
                <a-select-option
                  v-for="item in personalTagsList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.tagName"
                  >{{ item.tagName }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24" class="PrivateFormcontractInfo">
            <a-form-item
              v-for="(item, index) in modelState.contractInfo"
              :key="index"
              :label="`内容${Number(index) + 1}`"
            >
              <a-select
                option-filter-prop="label"
                placeholder="请选择内容"
                v-model:value="item.videoTag"
                style="width: 50%; margin-right: 10px"
              >
                <a-select-option
                  :value="tagItem"
                  v-for="(tagItem, tagIndex) in videoTagsList"
                  :key="tagIndex"
                  :label="tagItem"
                >
                  {{ tagItem }}
                </a-select-option>
              </a-select>
              <a-input
                style="width: calc(50% - 10px)"
                placeholder="签约费用"
                @blur="roundValue(item, Number(index))"
                v-model:value="item.contractAmount"
                suffix="$"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item name="celebrityContactEmail" label="建联邮箱">
              <a-select
                v-model:value="modelState.celebrityContactEmail"
                style="width: 100%"
                placeholder="请选择建联邮箱"
              >
                <a-select-option
                  v-for="(item, key) in contactEmailList"
                  :value="item"
                  :key="key"
                >
                  {{ item }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item name="email" label="网红邮箱">
              <a-input v-model:value="modelState.email" placeholder="请输入网红邮箱"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { getDictItems } from '/@/api/common/api';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const title = ref('私有');
const visible = ref(false);
const confirmLoading = ref(false);
const videoTagsList = ref<string[]>([]);
const contactEmailList = ref<string[]>([]);
const personalTagsList = ref<any[]>([]);

// 布局配置
const labelCol = {
  xs: { span: 24 },
  sm: { span: 4 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 20 },
};

// 表单数据
const modelState = ref<any>({
  uniqueId: undefined,
  platformType: undefined,
  gender: 1,
  personalTags: [],
  contractInfo: [],
  celebrityContactEmail: undefined,
});

// 表单验证规则
const rules = {
  celebrityContactEmail: [
    {
      required: true,
      message: '请选择建联邮箱',
      trigger: 'change',
    },
  ],
  email: [
    {
      required: true,
      message: '请输入网红邮箱',
      trigger: 'change',
    },
  ],
};

// 检查内容项是否填写完整
function checkContentItemFilled() {
  for (const item of modelState.value.contractInfo) {
    if (item.videoTag || item.contractAmount) {
      if (!item.videoTag) {
        $message.warning(`请选择内容`);
        return false;
      }
      if (!item.contractAmount) {
        $message.warning(`请填写签约费用`);
        return false;
      }
    }
  }

  const hasValidItem = modelState.value.contractInfo.some(
    (item: any) => item.videoTag && item.contractAmount
  );
  if (!hasValidItem) {
    $message.warning(`至少填写一项完整的内容和报价`);
    return false;
  }

  return true;
}

// 检查videoTags是否重复
function checkVideoTagsDuplicate() {
  const videoTagsSet = new Set();
  for (const item of modelState.value.contractInfo) {
    if (item.videoTag) {
      if (videoTagsSet.has(item.videoTag)) {
        return false;
      }
      videoTagsSet.add(item.videoTag);
    }
  }
  return true;
}

// 四舍五入值
function roundValue(_item: any, index: number) {
  if (modelState.value.contractInfo[index].contractAmount) {
    let num = Number(modelState.value.contractInfo[index].contractAmount);
    if (isNaN(num)) {
      modelState.value.contractInfo[index].contractAmount = undefined;
      return;
    }
    if (num > 9999999) {
      num = 9999999;
    }
    modelState.value.contractInfo[index].contractAmount = num;
  }
}

// 获取个性化标签
async function getPersonalTags() {
  try {
    const res = await defHttp.get({ url: '/personalTags/listAll' });
    if (res) {
      personalTagsList.value = res.filter((item: any) => item.tagName != '无');
    }
  } catch (error) {
    console.error(error);
  }
}

// 初始化联系邮箱
async function initContactEmail() {
  try {
    const res = await defHttp.get({
      url: '/storeUserContactEmail/queryListByCounselor',
    });
    if (res) {
      contactEmailList.value = res.map((item: any) => item.contactEmail);
      if (contactEmailList.value.length == 1) {
        modelState.value.celebrityContactEmail = contactEmailList.value[0];
      }
    }
  } catch (error) {
    console.error(error);
  }
}

// 获取视频标签列表
async function getVideoTagsList() {
  try {
    const res = await getDictItems('celebrity_content');
    if (res) {
      const filtered = res.filter(
        (item: any) => item.title == modelState.value.platformType
      );
      if (filtered.length > 0) {
        videoTagsList.value = filtered[0].value.split(',');
        const contractInfo: any[] = [];
        for (let i = 0; i < videoTagsList.value.length; i++) {
          contractInfo.push({
            videoTag: undefined,
            contractAmount: undefined,
          });
        }
        modelState.value.contractInfo = contractInfo;
      }
    }
  } catch (error) {
    console.error(error);
  }
}

// 添加
function add() {
  edit({});
}

// 编辑
function edit(record: any) {
  Object.assign(modelState.value, record);
  modelState.value.gender = 1
  modelState.value.personalTags = []
  modelState.value.contractInfo = []
  modelState.value.celebrityContactEmail = record.celebrityContactEmail
  modelState.value.email = record.email? record.email:undefined


  visible.value = true;
  getPersonalTags();
  initContactEmail();
  getVideoTagsList();
}

// 关闭
function close() {
  emit('close');
  Object.assign(modelState.value, {
    uniqueId: undefined,
    platformType: undefined,
    gender: 1,
    personalTags: [],
    contractInfo: [],
    celebrityContactEmail: undefined,
  });
  personalTagsList.value = [];
  formRef.value?.resetFields();
  visible.value = false;
}

// 提交
async function handleOk() {
  const personalTagIdName = personalTagsList.value
    .filter((item) => modelState.value.personalTags.includes(item.id))
    .map((item) => item.tagName)
    .join(',');

  if (personalTagIdName.length > 1 && personalTagIdName.includes('无')) {
    $message.warning('请检查选择的个性化标签，"无"不可以与其他标签同时选择');
    return;
  }

  if (!checkContentItemFilled()) {
    return;
  }

  if (!checkVideoTagsDuplicate()) {
    $message.warning('请勿重复选择内容');
    return;
  }

  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    const obj = {
      kolId: modelState.value.id,
      account: modelState.value.uniqueId,
      nickName: modelState.value.nickname,
      celebrityId: modelState.value.account,
      email: modelState.value.email,
      gender: modelState.value.gender,
      platformType: modelState.value.platformType,
      followersNum: modelState.value.authorFollowerCount,
      avatarUrl: modelState.value.authorAvatarUrl,
      countryName: modelState.value.country,
      celebrityContactEmail: modelState.value.celebrityContactEmail,
      privateContractInfoList: modelState.value.contractInfo.filter(
        (item: any) => item.videoTag && item.contractAmount
      ),
      personalTagIds:
        modelState.value.personalTags.length > 0 ? modelState.value.personalTags.join(',') : '',
    };

    const res = await defHttp.post({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/celebrityPrivateAdd',
      data: {
        celebrityPrivates: [obj],
      },
      
    },{isTransformResponse: false});

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

// 取消
function handleCancel() {
  close();
}

// 暴露方法
defineExpose({
  add,
  edit,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
  (e: 'close'): void;
}>();
</script>

<style>
.PrivateFormcontractInfo .ant-input-suffix {
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB,
    Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji,
    Segoe UI Emoji, Segoe UI Symbol !important;
}
</style>

<style lang="less" scoped>
.platformType-img {
  display: inline-block;
  width: 20px;
  height: 20px;

  img {
    width: 100%;
    height: 100%;
  }
}
.personalTagsBox {
  border: 1px solid #d9d9d9;
  width: 100%;
  min-height: 34px;
  padding: 2px 10px;
  border-radius: 3px;
  box-sizing: border-box;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  position: relative;
  max-height: 150px;
  overflow-y: auto;
}

.personalTagsSelect {
  min-height: 33px;
  margin-top: 10px;
  width: 100%;
  background: #fff;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  height: 200px;
  overflow-y: auto;
  overflow-x: hidden;

  div {
    height: 23px;
    line-height: 23px;
    border-radius: 8px;
    font-size: 12px;
    padding: 0 10px;
    margin: 5px;
    cursor: pointer;
    background: #e7e7f0;
    border: none;
    user-select: none;
  }

  .currentTag {
    background: #fff;
    border: 1px solid #3155ed;
    color: #3155ed;
  }
}
</style>
