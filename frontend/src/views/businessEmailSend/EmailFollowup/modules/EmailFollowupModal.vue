<template>
  <a-modal
    :title="title"
    :width="440"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item label="跟进类型" name="followUpCategoryId">
              <a-select
                v-model:value="model.followUpCategoryId"
                placeholder="请选择跟进类型"
                allowClear
                showSearch
                option-filter-prop="label"
              >
                <a-select-option
                  v-for="item in followUpCategoryList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.categoryName"
                >
                  {{ item.categoryName }}
                </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="发送时间" name="emailPushDate">
              <a-date-picker
                v-model:value="model.emailPushDate"
                show-time
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择发送时间"
                style="width: 100%"
                :disabled-date="disabledDate"
                :disabled-time="disabledTime"
              />
            </a-form-item>
            <a-form-item label="发送间隔" name="sendIntervalRange" :rules="[{ required: true, message: '请设置发送间隔' }]">
              <div class="send-interval-wrap">
                <a-slider
                  v-model:value="model.sendIntervalRange"
                  range
                  :min="0"
                  :max="60"
                  :step="1"
                  @change="onSliderChange"
                />
                <div class="send-interval-inputs">
                  <a-input-number
                    v-model:value="model.sendIntervalMin"
                    :min="0"
                    :max="model.sendIntervalMax ?? 60"
                    placeholder="0"
                    @change="onMinInputChange"
                  />
                  <span class="interval-unit">分钟</span>
                  <span class="interval-sep">~</span>
                  <a-input-number
                    v-model:value="model.sendIntervalMax"
                    :min="model.sendIntervalMin ?? 0"
                    :max="60"
                    placeholder="0"
                    @change="onMaxInputChange"
                  />
                  <span class="interval-unit">分钟</span>
                </div>
              </div>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import dayjs from 'dayjs';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);

const title = ref('批量跟进');
const visible = ref(false);
const confirmLoading = ref(false);
const followUpCategoryList = ref([]);
const detailIds = ref([]);

const model = ref({
  followUpCategoryId: undefined,
  emailPushDate: undefined,
  sendIntervalMin: 0,
  sendIntervalMax: 8,
  sendIntervalRange: [0, 8],
});

function onSliderChange(val) {
  model.value.sendIntervalMin = val[0];
  model.value.sendIntervalMax = val[1];
  model.value.sendIntervalRange = [val[0], val[1]];
}

function onMinInputChange() {
  const min = model.value.sendIntervalMin ?? 0;
  const max = model.value.sendIntervalMax ?? 8;
  model.value.sendIntervalRange = [min, Math.max(min, max)];
  model.value.sendIntervalMax = Math.max(min, max);
}

function onMaxInputChange() {
  const min = model.value.sendIntervalMin ?? 0;
  const max = model.value.sendIntervalMax ?? 8;
  model.value.sendIntervalRange = [Math.min(min, max), max];
  model.value.sendIntervalMin = Math.min(min, max);
}

function disabledDate(current) {
  return current && current < dayjs().startOf('day');
}

function disabledTime(date) {
  if (!date) return {};
  const now = dayjs();
  const currentHour = now.hour();
  const currentMinute = now.minute();
  if (date.isSame(now, 'day')) {
    const disabledHours = [];
    for (let h = 0; h < currentHour; h++) {
      disabledHours.push(h);
    }
    return {
      disabledHours: () => disabledHours,
      disabledMinutes: (selectedHour) => {
        if (selectedHour === currentHour) {
          const disabledMinutes = [];
          for (let m = 0; m <= currentMinute; m++) {
            disabledMinutes.push(m);
          }
          return disabledMinutes;
        }
        return [];
      },
    };
  }
  return {};
}

const validatorRules = {
  followUpCategoryId: [{ required: true, message: '请选择跟进类型' }],
  emailPushDate: [
    { required: true, message: '请选择发送时间' },
    {
      validator: (_rule, value) => {
        if (!value) return Promise.resolve();
        const selectedTime = dayjs(value);
        if (selectedTime.isBefore(dayjs()) || selectedTime.isSame(dayjs(), 'minute')) {
          return Promise.reject(new Error('发送时间必须大于当前时间'));
        }
        return Promise.resolve();
      },
      trigger: 'change',
    },
  ],
};

const url = {
  followUpCategoryList: '/email/emailTemplateCategory/listAll',
  batchFollowUp: '/email/emailPushMain/batchFollowUp',
};

async function initFollowUpCategoryList() {
  const res = await defHttp.get({
    url: url.followUpCategoryList,
    params: { templateType: 1 },
  });
  if (res) {
    followUpCategoryList.value = res || [];
  }
}

function open(selectedRows) {
  initFollowUpCategoryList();
  detailIds.value = (selectedRows || []).map((r) => r.id);
  model.value = {
    followUpCategoryId: undefined,
    emailPushDate: undefined,
    sendIntervalMin: 0,
    sendIntervalMax: 8,
    sendIntervalRange: [0, 8],
  };
  title.value = '批量跟进';
  visible.value = true;
}

function close() {
  emit('close');
  model.value = {};
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    if (detailIds.value.length === 0) {
      createMessage.warning('请先选择要跟进的记录');
      return;
    }
    confirmLoading.value = true;
    await defHttp.post({
      url: url.batchFollowUp,
      data: {
        detailIds: detailIds.value,
        followUpCategoryId: model.value.followUpCategoryId,
        emailPushDate: model.value.emailPushDate,
        sendIntervalMin: model.value.sendIntervalMin,
        sendIntervalMax: model.value.sendIntervalMax,
      },
    });
    // createMessage.success('操作成功');
    emit('ok');
    close();
  } catch (error) {
    console.error('提交失败:', error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

onMounted(() => {
 
});

defineExpose({
  open,
});
</script>
<style scoped lang="less">
.send-interval-wrap {
  width: 100%;
  .send-interval-inputs {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 12px;
    .ant-input-number {
      width: 80px;
    }
    .interval-sep {
      color: rgba(0, 0, 0, 0.45);
    }
    .interval-unit {
      color: rgba(0, 0, 0, 0.45);
      margin-left: 4px;
    }
  }
}
</style>
