<template>
  <a-tooltip placement="topLeft">
    <template #title>
      <div class="ellipsis-tooltip">{{ text }}</div>
    </template>
    <div
      class="ellipsis-item"
      :class="{ 'multi-line': lines > 1 }"
      :style="lineClampStyle"
    >
      <span>
        {{ text }}
      </span>
    </div>
  </a-tooltip>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  text: {
    type: String,
    default: "",
  },
  lines: {
    type: Number,
    default: 1,
  },
});

const lineClampStyle = computed(() => {
  return {
    display: "-webkit-box",
    WebkitLineClamp: props.lines,
    WebkitBoxOrient: "vertical",
  };
});
</script>

<style scoped>
.ellipsis-item {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ellipsis-item.multi-line {
  white-space: normal;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ellipsis-tooltip {
  overflow: auto;
  max-height: 400px;
}
</style>
