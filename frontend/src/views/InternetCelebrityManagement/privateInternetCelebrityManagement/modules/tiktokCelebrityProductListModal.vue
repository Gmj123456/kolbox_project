<template>
  <a-modal
    :title="title"
    :width="800"
    v-model:visible="visible"
    @cancel="handleCancel"
    @ok="handleOk"
    cancelText="关闭"
  >
    <vxe-table height="400" :data="data" size="mini" show-overflow>
      <vxe-table-column type="seq" width="50"></vxe-table-column>
      <vxe-table-column
        field="coverUrl"
        title="产品图片"
        width="100"
        :show-overflow="false"
        align="center"
      >
        <template #default="{ row }">
          <div style="height: 70px">
            <img style="height: 100%" :src="row.coverUrl" alt="" />
          </div>
        </template>
      </vxe-table-column>
      <vxe-table-column field="title" title="产品标题">
        <template #default="{ row }">
          <a :href="row.seoUrl" target="_blank" style="text-decoration: underline">{{
            row.title
          }}</a>
        </template>
      </vxe-table-column>
      <vxe-table-column field="categories" title="所属类目" :show-overflow="false">
        <template #default="{ row }">
          <span>{{ parseCategories(row.categories) }}</span>
        </template>
      </vxe-table-column>
    </vxe-table>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';

const title = ref('带货产品信息');
const visible = ref(false);
const data = ref([]);

const parseCategories = (categories) => {
  // Create a copy of the array to avoid mutating the original
  const sortedCategories = [...categories].sort((a, b) => a.level - b.level);
  return sortedCategories.map((item) => item.categoryName).join(' -> ');
};

const show = (productData) => {
  visible.value = true;
  data.value = [...productData];
};

const handleCancel = () => {
  visible.value = false;
};

const handleOk = () => {
  visible.value = false;
};

defineExpose({
  show,
});
</script>

<style lang="less" scoped></style>
