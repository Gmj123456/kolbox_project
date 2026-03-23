<template>
  <div>
    <template
      v-if="
        record.dataList &&
        record.dataList.length > 0 &&
        record.dataList[0].list &&
        record.dataList[0].list[0] &&
        !record.dataList[0].list[0].level
      "
    >
      <a-popover placement="topRight">
        <template #content>
          <div style="border: 1px solid #e8e8e8; border-bottom: none">
            <div
              style="
                height: 26px;
                width: 100%;
                display: flex;
                align-items: center;
                background-color: #f0f3fe;
                padding: 0 10px;
              "
            >
              {{ typeName }}
            </div>
            <div style="max-height: 300px; overflow-y: auto; margin: 5px 0">
              <div
                v-for="(categoryItem, index) in categoryTextList"
                :key="index"
                style="
                  display: flex;
                  align-items: center;
                  padding: 4px 10px;
                  border-bottom: 1px solid #e8e8e8;
                "
              >
                <span>{{ categoryItem }}</span>
              </div>
            </div>
          </div>
        </template>
        <span
          style="
            max-width: calc(100% - 26px);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            display: block;
          "
        >
          <a-tag v-for="(categoryItem, index) in categoryTextList" :key="index">
            {{ categoryItem }}
          </a-tag>
        </span>
      </a-popover>
    </template>
    <template v-else>
      <div
        v-if="
          record.dataList &&
          record.dataList.length > 0 &&
          categoryGroups.length > 0
        "
        style="display: flex; gap: 10px; flex-direction: column"
      >
        <!-- 高级别分类 -->
        <div v-if="highLevelGroup" class="category-list">
          <span class="level-high">高</span>
          <a-popover placement="topRight">
            <template #content>
              <div style="border: 1px solid #e8e8e8; border-bottom: none">
                <div
                  style="
                    height: 26px;
                    width: 100%;
                    display: flex;
                    align-items: center;
                    background-color: #f0f3fe;
                    padding: 0 10px;
                    border-bottom: 1px solid #e8e8e8;
                  "
                >
                  {{ typeName }}
                </div>
                <div style="max-height: 300px; overflow-y: auto; margin: 5px 0">
                  <div
                    v-for="categoryItem in highLevelGroup.list"
                    :key="categoryItem.attributeId"
                    style="
                      display: flex;
                      align-items: center;
                      padding: 4px 10px;
                      border-bottom: 1px solid #e8e8e8;
                    "
                  >
                    <span>{{ categoryItem.attributeName }}</span>
                  </div>
                </div>
              </div>
            </template>
            <span
              style="
                display: inline-block;
                max-width: calc(100% - 26px);
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              "
            >
              <a-tag
                v-for="categoryItem in highLevelGroup.list"
                :key="categoryItem.attributeId"
              >
                {{ categoryItem.attributeName }}
              </a-tag>
            </span>
          </a-popover>
        </div>

        <!-- 低级别分类 -->
        <div v-if="lowLevelGroup" class="category-list">
          <span class="level-small">低</span>
          <a-popover placement="topRight">
            <template #content>
              <div>
                <div
                  style="
                    height: 26px;
                    width: 100%;
                    display: flex;
                    align-items: center;
                    background-color: #f0f3fe;
                    padding: 0 10px;
                  "
                >
                  {{ typeName }}
                </div>
                <div style="max-height: 300px; overflow-y: auto; margin: 5px 0">
                  <div
                    v-for="categoryItem in lowLevelGroup.list"
                    :key="categoryItem.attributeId"
                    style="
                      display: flex;
                      align-items: center;
                      padding: 4px 10px;
                      border-bottom: 1px solid #e8e8e8;
                    "
                  >
                    <span>{{ categoryItem.attributeName }}</span>
                  </div>
                </div>
              </div>
            </template>
            <span
              style="
                display: inline-block;
                width: 100%;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              "
            >
              <a-tag
                v-for="categoryItem in lowLevelGroup.list"
                :key="categoryItem.attributeId"
              >
                {{ categoryItem.attributeName }}
              </a-tag>
            </span>
          </a-popover>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  typeId: {
    type: [String, Number],
    required: true,
  },
  typeName: {
    type: String,
    required: true,
  },
  record: {
    type: Object,
    required: true,
  },
  parseCategoryText: {
    type: Function,
    required: true,
  },
  parseCategory: {
    type: Function,
    required: true,
  },
});

const categoryText = computed(() => {
  return props.parseCategoryText(props.typeId, props.record);
});

const categoryTextList = computed(() => {
  const text = categoryText.value;
  return text ? text.split(',').filter((item) => item.trim()) : [];
});

const categoryGroups = computed(() => {
  return props.parseCategory(props.typeId, props.record);
});

const highLevelGroup = computed(() => {
  return categoryGroups.value.find((group) => group.level === 1);
});

const lowLevelGroup = computed(() => {
  return categoryGroups.value.find((group) => group.level === 3);
});
</script>

<style scoped lang="less">
.category-list {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
}

.level-high {
  width: 22px;
  border-radius: 4px;
  height: 16px;
  background: rgba(255, 232, 232, 1);
  color: rgba(255, 0, 0, 1);
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.level-small {
  margin-right: 10px;
  width: 22px;
  border-radius: 4px;
  height: 16px;
  background: rgba(255, 232, 209, 1);
  color: rgba(255, 128, 0, 1);
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

