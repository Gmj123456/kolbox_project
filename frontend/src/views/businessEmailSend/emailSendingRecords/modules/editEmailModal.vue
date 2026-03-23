<template>
  <a-modal
    :title="title"
    :width="400"
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
      <a-form
        ref="formRef"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        :model="model"
        :rules="validatorRules"
      >
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="model.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="发送时间" name="planSendTime">
              <a-date-picker
                dropdownClassName="send-email-time-picker"
                placeholder="请选择发送时间"
                style="width: 100%; font-size: 12px"
                format="YYYY-MM-DD HH:mm"
                :show-time="{ format: 'YYYY-MM-DD HH:mm' }"
                :disabled-date="disabledDate"
                :disabled-time="disabledTime"
                v-model:value="model.planSendTime"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import dayjs from 'dayjs';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);
const title = ref("操作");
const visible = ref(false);
const model = ref({
  id: undefined,
  email: undefined,
  planSendTime: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 19 },
};

const validatorRules = {
  email: [{ required: true, message: "请输入邮箱", trigger: "blur" }],
  planSendTime: [
    { required: true, message: "请选择发送时间", trigger: "change" },
    {
      validator: (_rule, value) => {
      if (!value) {
        return Promise.resolve();
      }
      const selectedTime = dayjs.isDayjs(value) ? value : dayjs(value);
      const now = dayjs();

      // 精确到秒 或 分钟，根据业务需求调整
      if (selectedTime.isBefore(now) || selectedTime.isSame(now, 'second')) {
        return Promise.reject(new Error("发送时间必须大于当前时间"));
      }
      return Promise.resolve();
    },
      trigger: "change",
    },
  ],
};

const confirmLoading = ref(false);

const url = {
  add: "/email/emailTemplate/add",
  edit: "/email/emailPushDetail/edit",
};

function disabledDate(current) {
  const now = dayjs();
  return current && current < now.startOf("day");
}

function disabledTime(date) {
  if (!date) return {};

  const now = dayjs();
  const currentHour = now.hour();
  const currentMinute = now.minute();

  // 是否今天
  if (date.isSame(now, "day")) {
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

function add() {
  edit({});
}

function edit(record) {
  Object.assign(model.value, record);
  model.value.planSendTime = dayjs(record.planSendTime);
  visible.value = true;
}

function close() {
  emit("close");
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    
    let httpurl = "";
    let method = "";
    if (!model.value.id) {
      httpurl = url.add;
      method = "post";
    } else {
      httpurl = url.edit;
      method = "put";
    }

    const params = {
      id: model.value.id,
      email: model.value.email,
      planSendTime: dayjs(model.value.planSendTime).format("YYYY-MM-DD HH:mm:00"),
    };

    const res = await defHttp.request({ url: httpurl, method, params });
    emit("ok");
    close();
    // if (res.success) {
    //   createMessage.success(res.message);
    // } else {
    //   createMessage.warning(res.message);
    // }
  } catch (error) {
    console.error(error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

defineExpose({
  add,
  edit,
});
</script>
<style>
.send-email-time-picker {
  .ant-calendar-today-btn {
    display: none !important;
  }
}
</style>
<style lang="less" scoped></style>
