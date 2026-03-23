<template>
  <a-tooltip :visible="showTooltip">
    <template #title>
      <div class="ellipsis-tooltip">
        <template v-for="(part, index) in parsedText" :key="index">
          <span>{{ part.text }}</span>
        </template>
      </div>
    </template>
    <div class="ellipsis-item">
      <span @mouseenter="enterName" @mouseleave="leaveName">
        <template v-for="(part, index) in parsedText" :key="index">
          <span :style="{ color: part.isMain ? '#0b1019' : 'rgba(184, 184, 184, 1)' }">{{
            part.text
          }}</span>
        </template>
      </span>
    </div>
  </a-tooltip>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  text: {
    type: String,
    default: '',
  },
});

const showTooltip = ref(false);

const parsedText = computed(() => {
  if (!props.text) return [];

  // 按 '|' 分割文本
  const parts = props.text.split('|');

  if (parts.length === 0) {
    return [{ text: props.text, isMain: true }];
  }

  if (parts.length === 1) {
    return [{ text: parts[0], isMain: true }];
  }

  // 第一个部分为黑色高亮，其余部分为灰色，用-连接
  const result = [];
  parts.forEach((part, index) => {
    if (index > 0) {
      result.push({ text: '-', isMain: false });
    }
    result.push({ text: part, isMain: index === 0 });
  });

  return result;
});

const enterName = (e) => {
  // div父元素
  const parentWidth = e.currentTarget.parentNode.offsetWidth;
  // span子元素
  const contentWidth = e.currentTarget.offsetWidth;
  // 子元素>父元素，文字内容超出当前宽度时手动控制气泡框显示
  setTimeout(() => {
    if (contentWidth > parentWidth) {
      showTooltip.value = true;
    }
  }, 100);
};

const leaveName = () => {
  setTimeout(() => {
    showTooltip.value = false;
  }, 200);
};
</script>

<style scoped>
.ellipsis-item {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.ellipsis-tooltip {
  overflow: auto;
  max-height: 400px;
}
</style>
