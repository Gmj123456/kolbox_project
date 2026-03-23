<template>
  <a-radio-group
    class="platform-type-radio-group"
    button-style="outline"
    v-model:value="currentValue"
    :disabled="disabled"
    @change="handleInput"
    
  >
    <a-radio-button  v-for="item in dictOptions" :key="item.value" :value="item.value">
      <div style="display: flex; align-items: center">
        <img
          v-if="item.value == 0"
          style="width: 20px; height: 20px"
          :src="platformIcons[0]"
          alt=""
        />
        <img
          v-if="item.value == 1"
          style="width: 20px; height: 20px"
          :src="platformIcons[1]"
          alt=""
        />
        <img
          v-if="item.value == 2"
          style="width: 20px; height: 20px"
          :src="platformIcons[2]"
          alt=""
        />
        <span>{{ item.text }}</span>
      </div>
    </a-radio-button>
  </a-radio-group>
</template>

<script setup>
import { ref, computed, watch, defineExpose } from 'vue';
import { getDictItems } from '@/api/common/api';

const props = defineProps({
  dictCode: {
    type: String,
    default: '',
  },
  placeholder: {
    type: String,
    default: '',
  },
  triggerChange: {
    type: Boolean,
    default: true,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  value: {
    type: [String, Number],
    default: undefined,
  },
  modelValue: {
    type: [String, Number],
    default: undefined,
  },
  type: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['update:value', 'update:modelValue', 'change', 'input']);

const dictOptions = ref([]);

const platformIcons = {
  0: new URL('@/assets/images/ins.png', import.meta.url).href,
  1: new URL('@/assets/images/yt.png', import.meta.url).href,
  2: new URL('@/assets/images/tk.png', import.meta.url).href,
};

const getValueSting = computed(() => {
  const val = props.modelValue !== undefined ? props.modelValue : props.value;
  return val != null ? val.toString() : undefined;
});

const currentValue = computed({
  get: () => getValueSting.value,
  set: (val) => {
    handleValueChange(val);
  },
});

function initDictData() {
  if (!props.dictCode) return;
  getDictItems(props.dictCode).then((res) => {
    console.log(res);
    if (res) {
      dictOptions.value = res;
    }
  });
}

function handleValueChange(val) {
  if (props.triggerChange) {
    emit('change', val);
  } else {
    emit('input', val);
  }
  emit('update:value', val);
  emit('update:modelValue', val);
}

function handleInput(e) {
  const val = e.target ? e.target.value : e;
  handleValueChange(val);
}

function setCurrentDictOptions(options) {
  dictOptions.value = options;
}

function getCurrentDictOptions() {
  return dictOptions.value;
}

watch(
  () => props.dictCode,
  () => {
    initDictData();
  },
  { immediate: true },
);

defineExpose({
  setCurrentDictOptions,
  getCurrentDictOptions,
  handleInput
});
</script>

<style scoped lang="less">
.platform-type-radio-group {

  /deep/ .ant-radio-button-wrapper:not(:first-child)::before {
      width: 0.1px !important;
      background-color: transparent;
    }
    /deep/ .ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before {
      background-color: #3155ed !important;
    }
  // margin-top: 1px !important;
  display: flex !important;
}
.platform-type-radio-group .ant-radio-button-wrapper {
  display: flex !important;
  flex: 1 !important;
  padding: 0px !important;
  justify-content: center;
  align-items: center;
}
</style>
