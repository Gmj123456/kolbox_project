<template>
  <div v-if="categoryData.length > 0">
    <!-- 高级别分类 -->
    <div
      v-if="highLevelGroup"
      class="category-list"
      :style="{ marginBottom: lowLevelGroup ? '10px' : undefined }"
    >
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
            max-width: calc(100% - 36px);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          "
        >
          <a-tag v-for="categoryItem in highLevelGroup.list" :key="categoryItem.attributeId">
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
            max-width: calc(100% - 26px);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          "
        >
          <a-tag v-for="categoryItem in lowLevelGroup.list" :key="categoryItem.attributeId">
            {{ categoryItem.attributeName }}
          </a-tag>
        </span>
      </a-popover>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  typeId: {
    type: [String, Number],
    required: true,
  },
  record: {
    type: Object,
    required: true,
  },
  typeName: {
    type: String,
    default: '',
  },
});

const parseCategory = (id, row) => {
  const filterList = row.dataList?.filter((item) => item.typeId == id) || [];

  if (filterList.length > 0) {
    return groupByLevel(filterList);
  }
  return [];
};

const groupByLevel = (data) => {
  const allItems = data.flatMap((item) => item.list || []);

  const grouped = allItems.reduce((acc, item) => {
    const { level, attributeId } = item;

    let group = acc.find((g) => g.level === level);
    if (!group) {
      group = { level, list: [] };
      acc.push(group);
    }

    const seen = new Set(group.list.map((i) => i.attributeId));

    if (!seen.has(attributeId)) {
      group.list.push(item);
    }

    return acc;
  }, []);

  return grouped;
};

const categoryData = computed(() => {
  return parseCategory(props.typeId, props.record);
});

const highLevelGroup = computed(() => {
  return categoryData.value.find((group) => group.level === 1);
});

const lowLevelGroup = computed(() => {
  return categoryData.value.find((group) => group.level === 3);
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

