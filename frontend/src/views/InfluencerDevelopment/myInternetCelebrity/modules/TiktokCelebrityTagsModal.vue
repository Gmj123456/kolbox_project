<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="800"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="modelState" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-item name="uniqueId" label="网红唯一id">
          <a-input
            placeholder="请输入网红唯一id"
            v-model:value="modelState.uniqueId"
          />
        </a-form-item>
        <a-form-item name="tagName" label="tag名称">
          <a-input
            placeholder="请输入tag名称"
            v-model:value="modelState.tagName"
          />
        </a-form-item>
        <a-form-item name="isAllot" label="是否分配 (0=未分配,1=已分配)">
          <a-input
            placeholder="请输入是否分配 (0=未分配,1=已分配)"
            v-model:value="modelState.isAllot"
          />
        </a-form-item>
        <a-form-item name="celebrityCounselorId" label="网红顾问_ID">
          <a-input
            placeholder="请输入网红顾问_ID"
            v-model:value="modelState.celebrityCounselorId"
          />
        </a-form-item>
        <a-form-item name="celebrityCounselorName" label="网红顾问名称">
          <a-input
            placeholder="请输入网红顾问名称"
            v-model:value="modelState.celebrityCounselorName"
          />
        </a-form-item>
        <a-form-item name="allotTime" label="分配时间">
          <a-date-picker
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            v-model:value="modelState.allotTime"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item name="videoId" label="视频id">
          <a-input placeholder="请输入视频id" v-model:value="modelState.videoId" />
        </a-form-item>
        <a-form-item name="videoCreateTime" label="视频创建时间">
          <a-date-picker
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            v-model:value="modelState.videoCreateTime"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item name="videoDesc" label="视频描述">
          <a-input placeholder="请输入视频描述" v-model:value="modelState.videoDesc" />
        </a-form-item>
        <a-form-item name="videoCover" label="视频封面">
          <a-input placeholder="请输入视频封面" v-model:value="modelState.videoCover" />
        </a-form-item>
        <a-form-item name="videoCollectCount" label="视频收藏数">
          <a-input-number v-model:value="modelState.videoCollectCount" style="width: 100%" />
        </a-form-item>
        <a-form-item name="videoCommentCount" label="视频评论数">
          <a-input-number v-model:value="modelState.videoCommentCount" style="width: 100%" />
        </a-form-item>
        <a-form-item name="videoDiggCount" label="视频点赞数">
          <a-input-number v-model:value="modelState.videoDiggCount" style="width: 100%" />
        </a-form-item>
        <a-form-item name="videoPlayCount" label="视频播放数">
          <a-input-number v-model:value="modelState.videoPlayCount" style="width: 100%" />
        </a-form-item>
        <a-form-item name="videoShareCount" label="视频分享数">
          <a-input-number v-model:value="modelState.videoShareCount" style="width: 100%" />
        </a-form-item>
        <a-form-item name="remark" label="备注">
          <a-input placeholder="请输入备注" v-model:value="modelState.remark" />
        </a-form-item>
        <a-form-item name="isDelete" label="删除状态（0=未删除,1=已删除）">
          <a-input
            placeholder="请输入删除状态（0=未删除,1=已删除）"
            v-model:value="modelState.isDelete"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import dayjs, { Dayjs } from 'dayjs';

const { createMessage: $message } = useMessage();

// 表单引用
const formRef = ref();

// 状态
const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);

// 布局配置
const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 },
};
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 16 },
};

// 表单数据
const modelState = reactive<any>({
  id: undefined,
  uniqueId: undefined,
  tagName: undefined,
  isAllot: undefined,
  celebrityCounselorId: undefined,
  celebrityCounselorName: undefined,
  allotTime: undefined,
  videoId: undefined,
  videoCreateTime: undefined,
  videoDesc: undefined,
  videoCover: undefined,
  videoCollectCount: undefined,
  videoCommentCount: undefined,
  videoDiggCount: undefined,
  videoPlayCount: undefined,
  videoShareCount: undefined,
  remark: undefined,
  isDelete: undefined,
});

// 表单验证规则
const rules = {
  uniqueId: [{ required: true, message: '请输入网红唯一id!', trigger: 'blur' }],
  tagName: [{ required: true, message: '请输入tag名称!', trigger: 'blur' }],
};

// 添加
function add() {
  edit({});
}

// 编辑
function edit(record: any) {
  formRef.value?.resetFields();
  Object.assign(modelState, {
    id: record.id,
    uniqueId: record.uniqueId,
    tagName: record.tagName,
    isAllot: record.isAllot,
    celebrityCounselorId: record.celebrityCounselorId,
    celebrityCounselorName: record.celebrityCounselorName,
    allotTime: record.allotTime ? dayjs(record.allotTime) : undefined,
    videoId: record.videoId,
    videoCreateTime: record.videoCreateTime ? dayjs(record.videoCreateTime) : undefined,
    videoDesc: record.videoDesc,
    videoCover: record.videoCover,
    videoCollectCount: record.videoCollectCount,
    videoCommentCount: record.videoCommentCount,
    videoDiggCount: record.videoDiggCount,
    videoPlayCount: record.videoPlayCount,
    videoShareCount: record.videoShareCount,
    remark: record.remark,
    isDelete: record.isDelete,
  });
  visible.value = true;
}

// 关闭
function close() {
  emit('close');
  visible.value = false;
}

// 提交
async function handleOk() {
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    const url = modelState.id ? '/tiktokcelebritytags/edit' : '/tiktokcelebritytags/add';
    const method = modelState.id ? 'put' : 'post';

    const formData = {
      ...modelState,
      allotTime: modelState.allotTime
        ? (modelState.allotTime as Dayjs).format('YYYY-MM-DD HH:mm:ss')
        : null,
      videoCreateTime: modelState.videoCreateTime
        ? (modelState.videoCreateTime as Dayjs).format('YYYY-MM-DD HH:mm:ss')
        : null,
    };

    const res = await defHttp.request({
      url,
      method,
      data: formData,
    });

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

<style lang="less" scoped></style>
