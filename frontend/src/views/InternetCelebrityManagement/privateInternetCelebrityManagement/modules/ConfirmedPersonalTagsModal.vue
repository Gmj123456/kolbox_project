<template>
  <a-modal
    :title="title"
    :width="700"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    class="ConfirmedPersonalTagsModal"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item>
              <a-input-group style="top: 0px !important" compact>
                <a-input
                  style="width: 96px; color: #0b1019; background-color: #fff"
                  disabled
                  default-value="原个性化标签"
                ></a-input>

                <a-select
                  :showArrow="false"
                  allowClear
                  show-search
                  disabled
                  mode="multiple"
                  :maxTagCount="10"
                  option-filter-prop="label"
                  v-model:value="model.personalTags"
                  :getPopupContainer="(triggerNode) => triggerNode.parentNode"
                  style="width: calc(100% - 96px); background-color: #fff !important"
                >
                  <a-select-option v-for="item in personalTags" :key="item" :value="item" :label="item">{{
                    item
                  }}</a-select-option>
                </a-select>
              </a-input-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item>
              <a-input-group style="top: 0px !important" compact>
                <a-input
                  style="width: 96px; color: #0b1019; background-color: #fff"
                  disabled
                  default-value="个性化标签"
                ></a-input>

                <a-select
                  :open="false"
                  :showArrow="false"
                  allowClear
                  mode="multiple"
                  :maxTagCount="5"
                  v-model:value="model.personalTagIds"
                  :getPopupContainer="(triggerNode) => triggerNode.parentNode"
                  style="width: calc(100% - 96px)"
                  placeholder="请选择个性化标签"
                  @change="handlePersonalTagsChange"
                >
                  <a-select-option v-for="item in personalTagsList" :key="item.id">{{
                    item.tagName
                  }}</a-select-option>
                </a-select>
              </a-input-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="margin-top: 3px">
          <a-col :span="24">
            <a-form-item>
              <div
                style="
                  display: flex;
                  flex-wrap: wrap;
                  row-gap: 6px;
                  max-height: 254px;
                  overflow-y: auto;
                  border: 1px solid #e8e8e8;
                  padding: 4px;
                  border-radius: 4px;
                "
              >
                <a-tag
                  @click="handlePersonalTags(item.id)"
                  :style="{
                    borderColor: model.personalTagIds.includes(item.id) ? '#3155ED' : '',
                    color: model.personalTagIds.includes(item.id) ? '#3155ED' : '',
                  }"
                  v-for="item in personalTagsList"
                  :key="item.id"
                  >{{ item.tagName }}</a-tag
                >
              </div>
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
import { getPersonalTagsListApi } from '/@/api/sys/systemCommon';

const { createMessage: $message } = useMessage();

// 响应式数据
const title = ref('私有网红确认个性化标签');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);
const personalTags = ref([]);
const personalTagsList = ref([]);
const isPinned = ref(false);

const model = ref({});

const validatorRules = reactive({});

const url = {
  add: '',
  edit: '/storeCelebrityPrivate/storeCelebrityPrivate/confirmedPersonalTags',
};

// 定义 emits
const emit = defineEmits(['close', 'ok']);

// 方法
function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    const kValue = (num / 1000).toFixed(0);
    return `${kValue}K`;
  } else if (num >= 1000000) {
    const mValue = (num / 1000000).toFixed(0);
    return `${mValue}M`;
  } else if (num > 0 && num < 1000) {
    return num;
  } else {
    return '-';
  }
}

function handlePersonalTags(id) {
  if (model.value.personalTagIds.includes(id)) {
    model.value.personalTagIds = model.value.personalTagIds.filter((item) => item !== id);
  } else {
    model.value.personalTagIds.push(id);
  }
}

function handlePersonalTagsChange(value) {
  console.log(value);
}

async function initPersonalTags() {
  const res = await getPersonalTagsListApi({});
  personalTagsList.value = res || [];
}

function jsonParseTotal(jsonStr) {
  console.log(jsonStr);
  const obj = JSON.parse(jsonStr).total;
  return obj;
}

function jsonParseNonPinned(jsonStr) {
  const obj = JSON.parse(jsonStr).non_pinned;
  return obj;
}

function isEmptyObject(obj) {
  if (obj) {
    return Object.keys(obj).length !== 0;
  } else {
    return false;
  }
}

function close() {
  emit('close');
  visible.value = false;
  formRef.value?.clearValidate();
}

async function handleOk() {
  const personalTagIdName = personalTagsList.value
    .filter((item) => model.value.personalTagIds.includes(item.id))
    .map((item) => item.tagName)
    .join(',');
  if (personalTagIdName.length == 0) {
    $message.warning('请选择个性化标签');
    return;
  }
  if (personalTagIdName.length > 1 && personalTagIdName.includes('无')) {
    $message.warning('请检查选择的个性化标签，"无"不可以与其他标签同时选择');
    return;
  }

  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (!model.value.id) {
      httpurl += url.add;
      method = 'post';
    } else {
      httpurl += url.edit;
      method = 'post';
    }
    const params = {
      id: model.value.id,
      personalTagIds: model.value.personalTagIds.join(','),
    };
    const res = await defHttp.request({
      url: httpurl,
      method,
      data: params,
    },{isTransformResponse: false});
    if (res.success) {
      emit('ok');
      close();
      $message.success(res.message);
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
  close();
}

function add() {
  //初始化默认值
  edit({});
}

function edit(record) {
  model.value = { ...record };
  visible.value = true;
  initPersonalTags();
  personalTags.value = model.value.personalTags ? model.value.personalTags.split(',') : [];

  model.value.personalTagIds = model.value.personalTagIds
    ? model.value.personalTagIds.split(',')
    : [];
  model.value.personalTags = personalTags.value;
  model.value.followersNum =
    model.value.followersNum >= 0 ? getFollower(model.value.followersNum) : '-';

  let playAvgNum = undefined;
  if (model.value.videoData && isEmptyObject(model.value.videoData)) {
    if (model.value.platformType != 1) {
      playAvgNum = isPinned.value
        ? jsonParseTotal(model.value.videoData).play_avg_num
          ? getFollower(jsonParseTotal(model.value.videoData).play_avg_num)
          : '-'
        : jsonParseNonPinned(model.value.videoData).play_avg_num
          ? getFollower(jsonParseNonPinned(model.value.videoData).play_avg_num)
          : '-';
    } else {
      playAvgNum = isPinned.value
        ? '-'
        : jsonParseTotal(model.value.videoData).play_avg_num
          ? getFollower(jsonParseTotal(model.value.videoData).play_avg_num)
          : '-';
    }
  }

  let interactRate = undefined;
  if (model.value.videoData && isEmptyObject(model.value.videoData)) {
    if (model.value.platformType == 1) {
      interactRate = isPinned.value
        ? '-'
        : jsonParseTotal(model.value.videoData).video_engagement_rate.toFixed(0) + '%';
    } else {
      interactRate = isPinned.value
        ? jsonParseTotal(model.value.videoData).video_engagement_rate.toFixed(0) + '%'
        : jsonParseNonPinned(model.value.videoData).video_engagement_rate.toFixed(0) + '%';
    }
  }
  model.value.playAvgNum = playAvgNum || '-';
  model.value.interactRate = interactRate || '-';
  model.value.gender = model.value.gender == 0 ? '男' : '女';
}

// 暴露方法给父组件
defineExpose({
  edit,
  add,
});
</script>
<style>
.ConfirmedPersonalTagsModal .ant-modal-body {
  /* max-height: 800px !important; */
  /* overflow-y: auto !important;
  overflow-x: hidden !important; */
  padding: 12px 12px !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-select-disabled .ant-select-selection) {
  background-color: #fff !important;
}
:deep(.ant-select-disabled .ant-select-selection--multiple .ant-select-selection__choice) {
  color: rgba(0, 0, 0, 0.65);
  background-color: #fafafa;
}
.InvUnitModal {
  .ant-form-item {
    margin-bottom: 5px !important;
  }
}
</style>
