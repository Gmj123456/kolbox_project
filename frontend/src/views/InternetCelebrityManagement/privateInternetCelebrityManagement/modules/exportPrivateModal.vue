<template>
  <a-modal v-model:visible="visible" :title="title" width="400"  @cancel="handleCancel">
    <a-spin :spinning="exportLoading">
      <div
        style="
          margin: 0 30px;
          padding: 10px 20px;
          background: #f6f8fa;
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;
        "
      >
        <div>
          选中网红:<a>{{ selCelebrityList.length }}</a>
        </div>
        <a
          style="
            padding: 0 4px;
            border: 1px solid #e8eaec;
            border-radius: 4px;
            background: #fff;
          "
          :disabled="!selCelebrityList.length"
          @click="handleExport(1)"
        >
          <a-icon type="download" />
        </a>
      </div>
    </a-spin>
    <template #footer>
      <a-button :loading="exportLoading" @click="handleCancel"> 取消 </a-button>
    </template>
  </a-modal>
</template>
<script setup>
import { ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
const { createMessage: $message } = useMessage();
import { postDownloadFile } from '/@/api/common/api';
const visible = ref(false);
const title = ref('导出私有网红');
const exportLoading = ref(false);
let allCelebrityCount = ref(0);
const param = ref({});
const selCelebrityList = ref([]);
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};
const handleExport = (type) => {
  if (selCelebrityList.value.length > 200) {
    $message.warning("最多支持导出200个网红");
    return;
  }
  exportLoading.value = true;

  let selectedIds = selCelebrityList.value.join(",");
  let fileName = "私有网红";

  postDownloadFile(
    "/storeCelebrityPrivate/storeCelebrityPrivate/exportXls",
    fileName + ".xlsx",
    type == 1 ? { selectedIds } : param.value
  )
    .then((data) => {
      console.log(data);
    })
    .finally(() => {
      
      exportLoading.value = false;
    });

};
const show = (list, total, params) => {
  console.log(list, allCelebrityCount, params);
  visible.value = true;
  selCelebrityList.value = [...list];
  allCelebrityCount.value = total;
  param.value = params;
};
defineExpose({
  show,
});
</script>