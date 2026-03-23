<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :open="visible"
    :width="300"
    @cancel="handlePayCancel"
  >
    <template #footer>
      <a-button key="back" @click="handlePayCancel"> 关闭</a-button>
      <a-button key="submit" type="primary" @click="handleOk" :loading="subLoading"
        >确定</a-button
      >
    </template>
    <a-form
      :model="formState"
      ref="formRef"
      :rules="rules"
      :label-col="labelCol"
      :wrapper-col="wrapperCol"
    >
      <a-form-item
        name="promStatus"
        label="推广状态"
        v-if="type == 0"
        style="transform: translateX(-7px)"
      >
        <a-select v-model:value="formState.promStatus" style="">
          <a-select-option :value="0">未开始</a-select-option>
          <a-select-option :value="10">进行中</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        name="goodsStatus"
        label="产品状态"
        v-if="type == 1"
        style="transform: translateX(-7px)"
      >
        <a-select v-model:value="formState.goodsStatus">
          <a-select-option :value="0">未开始</a-select-option>
          <a-select-option :value="10">进行中</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        name="celebrityPromStatus"
        label="网红进度"
        v-if="type == 2"
        style="transform: translateX(-7px)"
      >
        <a-select v-model:value="formState.celebrityPromStatus">
          <a-select-option :value="0"> 待寄样 </a-select-option>
          <a-select-option :value="1"> 待上线 </a-select-option>
          <a-select-option :value="2"> 已上线 </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const title = ref('操作');
const visible = ref(false);
const subLoading = ref(false);
const type = ref(0);

// 布局配置
const labelCol = {
  span: 8,
};
const wrapperCol = {
  span: 16,
};

// 表单数据
const formState = reactive<any>({
  id: undefined,
  correId: undefined,
  promStatus: undefined,
  goodsStatus: undefined,
  celebrityPromStatus: undefined,
});

// 表单验证规则
const rules = {
  promStatus: [
    {
      required: true,
      message: '请选择推广状态',
      trigger: 'change',
    },
  ],
  goodsStatus: [
    {
      required: true,
      message: '请选择产品状态',
      trigger: 'change',
    },
  ],
  celebrityPromStatus: [
    {
      required: true,
      message: '请选择网红进度',
      trigger: 'change',
    },
  ],
};

// 编辑
function edit(record: any, editType: number) {
  // type 0 = 推广状态  1 = 产品状态 2 = 网红进度
  type.value = editType;
  Object.assign(formState, record);
  if (editType == 0) {
    formState.promStatus = undefined;
  } else if (editType == 1) {
    formState.goodsStatus = undefined;
  } else if (editType == 2) {
    formState.celebrityPromStatus = undefined;
  }
  visible.value = true;
}

// 提交
async function handleOk() {
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    subLoading.value = true;
    let params: any = {};
    if (type.value == 0) {
      params = {
        promId: formState.id,
        promStatus: formState.promStatus,
        promotionStatus: 0,
      };
    } else if (type.value == 1) {
      params = {
        promoGoodsId: formState.id,
        promStatus: formState.goodsStatus,
        promotionStatus: 1,
      };
    } else if (type.value == 2) {
      params = {
        correId: formState.correId,
        promStatus: formState.celebrityPromStatus,
        promotionStatus: 2,
      };
    }

    const res = await defHttp.get({
      url: '/storeSellerPromotion/editPromotionStatus',
      params,
    });

    if (res.success) {
      $message.success(res.message);
      emit('ok');
      handlePayCancel();
    } else {
      $message.warning(res.message);
    }
  } catch (error) {
    console.error(error);
  } finally {
    subLoading.value = false;
  }
}

// 取消
function handlePayCancel() {
  Object.assign(formState, {
    id: undefined,
    correId: undefined,
    promStatus: undefined,
    goodsStatus: undefined,
    celebrityPromStatus: undefined,
  });
  visible.value = false;
  formRef.value?.resetFields();
}

// 暴露方法
defineExpose({
  edit,
  close: handlePayCancel,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
}>();
</script>

