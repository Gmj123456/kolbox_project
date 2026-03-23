<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="500"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="确定"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form layout="vertical" :model="model" ref="formRef">
        <a-form-item label="标签">
          <div style="display: flex; align-items: center">
            <span
              ref="tagsSpanRef"
              style="
                max-width: calc(100% - 125px);
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                display: inline-block;
              "
            >
              {{ model.tags }}
            </span>
            <span style="display: inline-block">
              共{{ selectedRowKeys.length }}个
              <a @click="viewAllTags">查看全部</a>
            </span>
          </div>
        </a-form-item>
        <a-form-item label="产品">
          <a-input-group style="display: flex" compact>
            <a-select
              style="width: 130px;border-right: 0px;"
              placeholder="请选择品牌"
              v-model:value="model.brandId"
              @change="onBrandChange"
              allowClear
              show-search
              option-filter-prop="label"
            >
              <a-select-option v-for="item in brandList" :key="item.id" :value="item.id" :label="item.brandName">
                {{ item.brandName }}
              </a-select-option>
            </a-select>
            <a-select
              option-filter-prop="label"
              v-model:value="model.productIds"
              style="flex: 1; padding-bottom: 0px"
              show-search
              placeholder="请选择产品"
              allowClear
            >
              <a-select-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.text">
                {{ item.text }}
              </a-select-option>
            </a-select>
          </a-input-group>
        </a-form-item>
        <a-form-item label="强制更新">
          <a-radio-group v-model:value="model.forceUpdate">
            <a-radio-button :value="1">是</a-radio-button>
            <a-radio-button :value="0">否</a-radio-button>
          </a-radio-group>
        </a-form-item>
      </a-form>
      <div class="related-categories">
        <div
          class="category-item"
          v-for="item in socialMediaCategoryList"
          :key="item.typeId"
        >
          <div class="category-item-title">{{ item.typeName }}</div>
          <div class="category-item-content">
            <a-input
              v-model:value="item.filterCategoryName"
              :placeholder="item.typeName + '筛选'"
              @change="filterCategory(item)"
            />
            <div class="category-item-content-checkbox">
              <a-checkbox
                :indeterminate="item.indeterminate"
                :checked="item.checkAll"
                @change="checkAllChange(item)"
              >
                全选
              </a-checkbox>
              <span>已选择 <a>{{ getTotalSelectedCount(item) }}</a> 个</span>
            </div>
            <div class="category-item-content-checkbox-group">
              <a-checkbox-group v-model:value="item.selectedIds" @change="checkboxChange(item)">
                <a-row>
                  <a-col :span="12" v-for="option in getFilteredData(item)" :key="option.value">
                    <a-checkbox :value="option.value">
                      <EllipsisTooltip :text="option.label" style="width: 230px" />
                    </a-checkbox>
                  </a-col>
                </a-row>
              </a-checkbox-group>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
    <viewAllTagsModal ref="viewAllTagsModalRef" />
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import viewAllTagsModal from './viewAllTagsModal.vue';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import {
  getProductListApi,
  queryTagsByIdsApi,
  bindingProductsApi,
  getBrandListApi,
  getCategoryTreeApi,
  getAttributeTypeListApi,
  getAttributeListApi,

} from '../tagMaintenanceApi';

const { createMessage } = useMessage();

const title = ref('绑定产品');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref();
const tagsSpanRef = ref();
const viewAllTagsModalRef = ref();
const socialMediaCategoryList = ref([]);
const model = ref({
  brandId: undefined,
  productIds: undefined,
  forceUpdate: 0,
  tags: '',
});
const productCategoryList = ref([]);
const productList = ref([]);
const brandList = ref([]);
const selectedRowKeys = ref([]);
async function initProductCategory() {
  try {
    const res = await getCategoryTreeApi();
    if (res) {
      productCategoryList.value = res;
      const processTreeData = (nodes) => {
        if (!nodes || !Array.isArray(nodes)) return;
        nodes.forEach((node) => {
          if (node.isHasChild === 1) {
            node.disabled = true;
          }
          if (node.children && node.children.length > 0) {
            processTreeData(node.children);
          }
        });
      };
      processTreeData(productCategoryList.value);
    }
  } catch (error) {
    console.error('获取产品类目失败:', error);
  }
}
// 筛选分类
function filterCategory(item) {
  // 清除之前的防抖定时器
  if (item.filterTimer) {
    clearTimeout(item.filterTimer);
  }

  // 设置防抖延迟执行
  item.filterTimer = setTimeout(() => {
    executeFilterCategory(item);
  }, 300); // 300ms防抖延迟
}

// 实际执行筛选的方法
function executeFilterCategory(item) {
  // 如果清除筛选（filterCategoryName为空），则恢复完整的选中状态
  if (!item.filterCategoryName) {
    // 恢复时需要合并oldSelectedIds和当前selectedIds
    if (item.oldSelectedIds) {
      // 在清空前保存当前的筛选条件，因为清空后就获取不到了
      const previousFilterText = item.previousFilterText || '';

      // 先同步当前的level状态
      syncAllLevelStates(item);

      // 清空筛选时的合并逻辑：
      // 1. 保留oldSelectedIds中的所有项目（筛选前的选中状态）
      // 2. 添加selectedIds中新选择的项目（筛选期间新增的选择）
      // 3. 移除用户在筛选期间主动取消的项目
      item.selectedIds = restoreSelectedIdsAfterClearFilter(
        item.oldSelectedIds,
        item.selectedIds,
        item.data,
        previousFilterText
      );

      // 恢复所有相关选项的level状态
      restoreLevelStatus(item);

      // 清理临时保存的筛选条件和状态
      item.previousFilterText = null;
      item.oldSelectedIds = null;
      item.originalSelectedIds = null;

      // 清空筛选后，恢复所有选项的level状态
      restoreLevelStates(item);
      // 保护并恢复所有不可见选项的level状态
      protectHiddenLevelStates(item);
    }

    // 清理防抖定时器
    if (item.filterTimer) {
      clearTimeout(item.filterTimer);
      item.filterTimer = null;
    }
  } else {
    // 第一次开始筛选时保存原始状态
    if (!item.oldSelectedIds) {
      console.log('首次筛选，初始化 oldSelectedIds:', [...item.selectedIds]);
      item.oldSelectedIds = [...item.selectedIds];
      item.originalSelectedIds = [...item.selectedIds]; // 保存最初的选中状态

      // 全面同步level状态
      syncAllLevelStates(item);
    } else {
      // 如果已经在筛选状态，先同步当前选择和level状态到全局状态
      // 这很重要：无论筛选结果如何，都要先保存当前的选择和level状态
      console.log('筛选条件变化，先同步当前状态');
      syncCurrentSelectionToGlobal(item);
      syncAllLevelStates(item);
    }

    // 更新筛选条件记录
    item.previousFilterText = item.filterCategoryName;

    // 检查当前筛选条件是否有结果
    const filteredData = getFilteredData(item);

    console.log('executeFilterCategory - 筛选结果:', {
      filterText: item.filterCategoryName,
      filteredCount: filteredData.length,
      currentSelectedIds: [...item.selectedIds],
      oldSelectedIds: [...(item.oldSelectedIds || [])],
      oldLevelMap: { ...(item.oldLevelMap || {}) },
    });

    if (filteredData.length > 0) {
      // 有筛选结果时：恢复所有状态
      restoreAllStates(item);
    } else {
      console.log(item.oldSelectedIds);
      // 无筛选结果时：只清空显示，不影响全局状态
      // 重要：不要在这里调用syncCurrentSelectionToGlobal，因为selectedIds为空会清空全局状态
      console.log('筛选无结果，仅清空显示selectedIds，保持全局状态不变');
      // item.selectedIds = [];

      // 即使筛选无结果，也要恢复所有状态
      restoreAllStates(item);
    }
  }

  // 更新全选状态
  updateCheckAllStatus(item);
}

// 单选
function checkboxChange(item) {
  console.log('🔵 checkboxChange - 选中状态变化:', {
    selectedIds: [...item.selectedIds],
    filterText: item.filterCategoryName,
    hasOldSelectedIds: !!item.oldSelectedIds,
  });

  // 先将所有未选中的选项的level设为undefined
  item.data.forEach((option) => {
    if (!item.selectedIds.includes(option.value)) {
      option.level = undefined;
    }
  });
  // 为新选中的选项设置默认level为'1'（只处理非disabled的选项）
  item.selectedIds.forEach((id) => {
    const option = item.data.find((option) => option.value == id);
    if (option && !option.level && !option.disabled) {
      option.level = '1';
    }
  });

  // 在筛选状态下，需要实时同步选中状态
  if (item.filterCategoryName && item.oldSelectedIds) {
    // 实时更新筛选前的状态记录，确保状态同步
    syncFilteredSelectionState(item);
    // 同时同步level状态
    syncAllLevelStates(item);
  }

  updateCheckAllStatus(item);
}

// 全选
function checkAllChange(item) {
  if (item.checkAll) {
    // 如果当前是全选状态，则取消全选
    // 在筛选状态下，只取消当前可见的选中项
    if (item.filterCategoryName && item.oldSelectedIds) {
      const filteredData = getFilteredData(item);
      const visibleIds = filteredData.map((option) => option.value);
      // 从selectedIds中移除当前可见的项目
      item.selectedIds = item.selectedIds.filter((id) => !visibleIds.includes(id));
    } else {
      // 非筛选状态，清空所有选中
      item.selectedIds = [];
    }
    item.checkAll = false;
    item.indeterminate = false;
    // 清除当前可见选项的level
    const currentVisibleData = getFilteredData(item);
    currentVisibleData.forEach((option) => {
      option.level = undefined;
    });
  } else {
    // 如果当前不是全选状态，则全选当前过滤显示的数据（排除disabled的选项）
    const filteredData = getFilteredData(item);
    const enabledData = filteredData.filter((option) => !option.disabled);
    const newSelectedIds = enabledData.map((option) => option.value);

    // 合并新选中的ID到现有selectedIds中
    item.selectedIds = [...new Set([...item.selectedIds, ...newSelectedIds])];
    item.checkAll = true;
    item.indeterminate = false;
    // 为所有选项设置默认level为'1'（只处理非disabled的选项）
    enabledData.forEach((option) => {
      if (!option.level) {
        option.level = '1';
      }
    });
  }

  // 在筛选状态下，需要实时同步选中状态
  if (item.filterCategoryName && item.oldSelectedIds) {
    // 实时更新筛选前的状态记录，确保状态同步
    syncFilteredSelectionState(item);
    // 同时同步level状态
    syncAllLevelStates(item);
  }

  updateCheckAllStatus(item);
}
function getTotalSelectedCount(item) {
  if (item.filterCategoryName && item.oldSelectedIds) {
    return item.oldSelectedIds.length;
  }
  return item.selectedIds.length;
}
async function initData() {
  confirmLoading.value = true;
  try {
    const [typeRes, res] = await Promise.all([
      getAttributeTypeListApi(),
      getAttributeListApi({ isParent: 1 }),
    ]);
    if (res) {
      await initProductCategory();
      socialMediaCategoryList.value = (typeRes || [])
        .filter((item) => item.type == 2)
        .map((typeItem) => {
          const existingItem = (res || []).find((item) => item.typeId === typeItem.id);
          if (existingItem) {
            return {
              ...existingItem,
              selectedIds: [],
              checkAll: false,
              filterCategoryName: '',
              indeterminate: false,
              oldSelectedIds: null,
              originalSelectedIds: null,
              oldLevelMap: {},
              previousFilterText: null,
              filterTimer: null,
              typeName: typeItem.typeName,
            };
          } else {
            return {
              ...typeItem,
              selectedIds: [],
              checkAll: false,
              filterCategoryName: '',
              indeterminate: false,
              oldSelectedIds: null,
              originalSelectedIds: null,
              oldLevelMap: {},
              previousFilterText: null,
              filterTimer: null,
            };
          }
        });
        if (!model.value.id) {
          handleCategoryList();
        } else if (model.value.id && model.value.productId) {
          queryTagProductAttr();
          // handleCategoryList(model.dataList);
        } else {
          handleCategoryList();
        }
    }
  } catch (error) {
    console.error('初始化数据失败:', error);
  } finally {
    confirmLoading.value = false;
  }
}
function handleCategoryList(queryByCategoryList = []) {
  if (queryByCategoryList.length === 0) {
    socialMediaCategoryList.value.forEach((item) => {
      item.selectedIds = [];
      item.checkAll = false;
      item.filterCategoryName = '';
      item.indeterminate = false;
      item.oldSelectedIds = null;
      item.originalSelectedIds = null;
      item.oldLevelMap = {};
      item.previousFilterText = null;
      item.filterTimer = null;
      if (item.data) {
        item.data.forEach((subItem) => {
          subItem.level = undefined;
          subItem.label = subItem.attributeName;
          subItem.value = subItem.id;
        });
      }
    });
  } else {
    socialMediaCategoryList.value.forEach((item) => {
      item.selectedIds = queryByCategoryList;
      item.checkAll = false;
      item.filterCategoryName = '';
      item.indeterminate = false;
      item.oldSelectedIds = null;
      item.originalSelectedIds = null;
      item.oldLevelMap = {};
      item.previousFilterText = null;
      item.filterTimer = null;
      item.data.forEach((subItem) => {
        subItem.level = undefined;
        subItem.label = subItem.attributeName;
        subItem.value = subItem.id;
        subItem.disabled = false;
      });
    });
  }
  socialMediaCategoryList.value.forEach((item) => {
    if (item.selectedIds.length > 0) {
      const enabledData = item.data.filter((subItem) => !subItem.disabled);
      if (enabledData.length > 0) {
        if (item.selectedIds.length === enabledData.length) {
          item.checkAll = true;
          item.indeterminate = false;
        } else {
          item.checkAll = false;
          item.indeterminate = true;
        }
      }
    }
  });
}
// 获取过滤后的数据，不修改原数据
// 筛选时只显示匹配条件的数据，但保留原有的选中状态不变
function getFilteredData(item) {
  if (!item.filterCategoryName) {
    return item.data || [];
  }

  console.log('getFilteredData 筛选过程:', {
    filterText: item.filterCategoryName,
    totalData: (item.data || []).length,
    sampleData: (item.data || [])
      .slice(0, 3)
      .map((d) => ({ id: d.id, value: d.value, label: d.label })),
  });

  // 显示匹配筛选条件的选项
  const filteredResult = (item.data || []).filter((option) =>
    option.label.includes(item.filterCategoryName)
  );

  console.log('筛选结果:', {
    filteredCount: filteredResult.length,
    filteredItems: filteredResult.map((d) => ({
      id: d.id,
      value: d.value,
      label: d.label,
    })),
  });

  return filteredResult;
}

// 恢复level状态的方法
function restoreLevelStates(item) {
  console.log('🔧 开始恢复level状态');

  if (!item.oldLevelMap) {
    console.log('🔧 没有保存的level状态，跳过恢复');
    return;
  }

  // 恢复所有选项的level状态（包括不可见的）
  (item.data || []).forEach((option) => {
    if (item.oldLevelMap[option.value] !== undefined) {
      option.level = item.oldLevelMap[option.value];
      console.log('🔧 恢复选项level:', option.label, 'level:', option.level);
    }
  });

  console.log('🔧 level状态恢复完成');
}

// 清空筛选时恢复选中状态的专用方法
function restoreSelectedIdsAfterClearFilter(
  oldSelectedIds,
  currentSelectedIds,
  allData,
  filterText
) {
  if (!oldSelectedIds) return currentSelectedIds;

  // 核心逻辑：
  // 1. 保留当前选中的项目（用户在筛选中的操作结果）
  // 2. 补充那些在oldSelectedIds中但因筛选被隐藏的项目（用户没有机会操作的）

  let finalSelectedIds = [...currentSelectedIds];

  // 找出那些在oldSelectedIds中但因为筛选被隐藏的项目
  const hiddenButShouldKeep = oldSelectedIds.filter((oldId) => {
    // 如果已经在当前选中中，跳过（已经处理了）
    if (currentSelectedIds.includes(oldId)) return false;

    // 检查这个项目是否存在于数据中
    const option = allData.find((dataItem) => dataItem.id === oldId);
    if (!option) return false;

    // 判断该项目是否因为筛选而被隐藏
    // 如果不匹配筛选条件，说明被隐藏了，用户没有机会看到和操作，应该保留原有选中状态
    if (filterText && !option.label.includes(filterText)) {
      return true; // 被隐藏的项目应该保留
    }

    // 如果匹配筛选条件但不在currentSelectedIds中，说明用户主动取消了
    return false;
  });

  // 合并：当前选中 + 被隐藏但应保留的
  finalSelectedIds = [...new Set([...finalSelectedIds, ...hiddenButShouldKeep])];

  return finalSelectedIds;
}

// 恢复level状态
function restoreLevelStatus(item) {
  // 为所有选中的选项恢复或设置level状态
  item.selectedIds.forEach((id) => {
    const option = (item.data || []).find((dataItem) => dataItem.id === id);
    if (option && !option.disabled) {
      // 如果没有level，设置默认level为'1'
      if (!option.level) {
        option.level = '1';
      }
    }
  });

  // 清除未选中选项的level
  (item.data || []).forEach((option) => {
    if (!item.selectedIds.includes(option.id)) {
      option.level = undefined;
    }
  });
}

// 同步当前筛选状态下的选择到全局状态
function syncCurrentSelectionToGlobal(item) {
  console.log('🟡 开始同步当前选择到全局状态');

  // 获取当前筛选的数据
  const currentFilteredData = getFilteredData(item);
  console.log(item.oldSelectedIds);

  // 如果当前筛选无结果，跳过同步避免清空全局状态
  if (currentFilteredData.length === 0) {
    console.log('🟡 当前筛选无结果，跳过同步以保护全局状态');
    return;
  }
  console.log(item.oldSelectedIds);

  // 如果oldSelectedIds未初始化，使用originalSelectedIds初始化
  if (!item.oldSelectedIds) {
    console.log('🟡 oldSelectedIds未初始化，使用originalSelectedIds初始化');
    item.oldSelectedIds = [...(item.originalSelectedIds || [])];
  }

  // 如果oldLevelMap未初始化，初始化为空对象
  if (!item.oldLevelMap) {
    item.oldLevelMap = {};
  }

  console.log('🟡 当前筛选数据:', currentFilteredData.map((d) => d.label));
  console.log('🟡 当前selectedIds:', item.selectedIds);
  console.log('🟡 同步前oldSelectedIds:', item.oldSelectedIds);
  console.log('🟡 同步前oldLevelMap:', item.oldLevelMap);

  // 遍历当前筛选的数据，同步选中状态和level状态到全局
  currentFilteredData.forEach((subItem) => {
    const isCurrentlySelected = item.selectedIds.includes(subItem.id);
    const wasGloballySelected = item.oldSelectedIds.includes(subItem.id);

    if (isCurrentlySelected && !wasGloballySelected) {
      // 新选中的项目，添加到全局状态
      item.oldSelectedIds.push(subItem.id);
      console.log('🟢 新选中项目添加到全局:', subItem.label);
    } else if (!isCurrentlySelected && wasGloballySelected) {
      // 取消选中的项目，从全局状态移除
      const index = item.oldSelectedIds.indexOf(subItem.id);
      if (index > -1) {
        item.oldSelectedIds.splice(index, 1);
        console.log('🔴 取消选中项目从全局移除:', subItem.label);
      }
    }

    // 同步level状态：无论是否选中，都要保存当前的level状态
    if (subItem.level !== undefined) {
      item.oldLevelMap[subItem.value] = subItem.level;
      console.log('🟡 保存level状态:', subItem.label, 'level:', subItem.level);
    } else if (item.oldLevelMap[subItem.value] !== undefined) {
      // 如果当前没有level但之前有，保持之前的状态
      console.log(
        '🟡 保持之前的level状态:',
        subItem.label,
        'level:',
        item.oldLevelMap[subItem.value]
      );
    }
  });

  console.log('🟡 同步后oldSelectedIds:', item.oldSelectedIds);
  console.log('🟡 同步后oldLevelMap:', item.oldLevelMap);
}

// 根据当前筛选条件更新显示的选中状态
function updateSelectedIdsForCurrentFilter(item) {
  if (!item.oldSelectedIds) return;

  // 获取当前筛选显示的数据
  const filteredData = getFilteredData(item);

  // 调试：检查数据结构
  console.log('🔧 updateSelectedIdsForCurrentFilter 开始:', {
    filterText: item.filterCategoryName,
    filteredDataCount: filteredData.length,
    oldSelectedIds: [...item.oldSelectedIds],
    oldLevelMap: { ...(item.oldLevelMap || {}) },
  });

  // 从全局选中状态中筛选出在当前筛选结果中可见的选中项
  // 关键：检查oldSelectedIds中的每个ID是否在当前筛选结果中存在
  const newSelectedIds = [];
  console.log(item.oldSelectedIds);

  item.oldSelectedIds.forEach((oldId) => {
    // 检查这个ID是否在当前筛选结果中
    const matchedOption = filteredData.find(
      (option) => option.value === oldId || option.id === oldId
    );

    if (matchedOption) {
      newSelectedIds.push(oldId);
      console.log('🟢 恢复选中项:', oldId, '- 在当前筛选结果中找到');

      // 同时恢复level状态
      if (item.oldLevelMap && item.oldLevelMap[oldId] !== undefined) {
        matchedOption.level = item.oldLevelMap[oldId];
        console.log('🟢 恢复level状态:', matchedOption.label, 'level:', matchedOption.level);
      }
    } else {
      // 即使不在当前筛选结果中，也要保持选中状态
      newSelectedIds.push(oldId);
      console.log('🔸 保持选中项:', oldId, '- 虽不在当前筛选结果中但保持选中状态');
    }
  });

  // 对于当前筛选结果中的其他项目，也要恢复它们的level状态（即使没有选中）
  filteredData.forEach((option) => {
    if (
      item.oldLevelMap &&
      item.oldLevelMap[option.value] !== undefined &&
      !newSelectedIds.includes(option.value)
    ) {
      option.level = item.oldLevelMap[option.value];
      console.log('🟡 恢复未选中项的level状态:', option.label, 'level:', option.level);
    }
  });

  console.log('🔧 最终恢复的选中状态:', newSelectedIds);

  // 如果没有恢复任何项目，但oldSelectedIds中有数据，进行详细诊断
  if (newSelectedIds.length === 0 && item.oldSelectedIds.length > 0) {
    console.log('⚠️ 恢复失败诊断:');
    console.log(
      '当前筛选结果:',
      filteredData.map((d) => ({
        id: d.id,
        value: d.value,
        label: d.label,
      }))
    );

    item.oldSelectedIds.forEach((oldId) => {
      const matchInFiltered = filteredData.find((d) => d.value === oldId || d.id === oldId);
      console.log(`ID ${oldId}:`, {
        matchInFiltered: matchInFiltered
          ? {
              id: matchInFiltered.id,
              value: matchInFiltered.value,
              label: matchInFiltered.label,
            }
          : null,
      });
    });
  }

  item.selectedIds = newSelectedIds;
}

// 同步筛选状态下的选中状态（保留原方法供其他地方使用）
function syncFilteredSelectionState(item) {
  // 直接调用新的同步方法
  syncCurrentSelectionToGlobal(item);
}

// 全面同步level状态的方法
function syncAllLevelStates(item) {
  console.log('🔧 开始全面同步level状态');

  // 确保oldLevelMap已初始化
  if (!item.oldLevelMap) {
    item.oldLevelMap = {};
  }

  // 获取当前筛选的数据
  const currentFilteredData = getFilteredData(item);

  // 1. 保存当前可见选项的level状态到全局
  currentFilteredData.forEach((option) => {
    if (option.level !== undefined) {
      item.oldLevelMap[option.value] = option.level;
      console.log('🔧 保存可见选项level:', option.label, 'level:', option.level);
    }
  });

  // 2. 保存所有选项的level状态（包括不可见的）
  // 这确保了被筛选隐藏的选项的level状态不会丢失
  (item.data || []).forEach((option) => {
    if (option.level !== undefined) {
      // 只有当前选项有level值时才更新，避免覆盖已保存的隐藏选项状态
      item.oldLevelMap[option.value] = option.level;
      console.log(
        '🔧 保存选项level:',
        option.label,
        'level:',
        option.level,
        '可见:',
        currentFilteredData.some((f) => f.value === option.value)
      );
    }
  });

  console.log('🔧 全面同步后的oldLevelMap:', item.oldLevelMap);
}

// 保护不可见选项的level状态
function protectHiddenLevelStates(item) {
  console.log('🛡️ 开始保护不可见选项的level状态');

  if (!item.oldLevelMap) {
    item.oldLevelMap = {};
  }

  // 获取当前筛选的数据
  const currentFilteredData = getFilteredData(item);
  const visibleIds = currentFilteredData.map((option) => option.value);

  // 遍历所有数据，为不可见但有level状态的选项恢复level
  (item.data || []).forEach((option) => {
    // 如果选项不在当前筛选结果中，但在oldLevelMap中有保存的level状态
    if (
      !visibleIds.includes(option.value) &&
      item.oldLevelMap[option.value] !== undefined
    ) {
      option.level = item.oldLevelMap[option.value];
      console.log('🛡️ 恢复不可见选项的level状态:', option.label, 'level:', option.level);
    }
  });

  console.log('🛡️ 不可见选项level状态保护完成');
}

// 通用的状态恢复方法，确保所有状态都正确
function restoreAllStates(item) {
  console.log('🔄 开始恢复所有状态');

  // 1. 恢复所有选项的level状态
  restoreLevelStates(item);

  // 2. 保护不可见选项的level状态
  protectHiddenLevelStates(item);

  // 3. 如果在筛选状态下，恢复选中状态
  if (item.filterCategoryName && item.oldSelectedIds) {
    updateSelectedIdsForCurrentFilter(item);
  }

  // 4. 更新全选状态
  updateCheckAllStatus(item);

  console.log('🔄 所有状态恢复完成');
}

// 更新全选状态
function updateCheckAllStatus(item) {
  const filteredData = getFilteredData(item);
  const enabledData = filteredData.filter((option) => !option.disabled);

  if (enabledData.length === 0) {
    item.checkAll = false;
    item.indeterminate = false;
    return;
  }

  const selectedInVisible = item.selectedIds.filter((id) =>
    enabledData.some((option) => option.value === id)
  );

  if (selectedInVisible.length === 0) {
    item.checkAll = false;
    item.indeterminate = false;
  } else if (selectedInVisible.length === enabledData.length) {
    item.checkAll = true;
    item.indeterminate = false;
  } else {
    item.checkAll = false;
    item.indeterminate = true;
  }
}
function viewAllTags() {
  viewAllTagsModalRef.value?.show(model.value.tags);
}

function onBrandChange(value) {
  model.value.productIds = undefined;
  productList.value = [];
  if (value) {
    initProduct();
  }
}

async function initBrand() {
  try {
    const res = await getBrandListApi();
    brandList.value = res || [];
  } catch (error) {
    console.error('获取品牌列表失败:', error);
  }
}

async function initProduct() {
  try {
    const res = await getProductListApi({ brandId: model.value.brandId });
    if (res) {
      productList.value = res.map((item) => ({
        ...item,
        text: item.productName,
      }));
      if (productList.value.length === 1) {
        model.value.productIds = productList.value[0].id;
      }
    }
  } catch (error) {
    console.error('获取产品列表失败:', error);
  }
}

function add() {
  edit([]);
}

function edit(keys) {
  selectedRowKeys.value = keys;
  model.value = {
    brandId: undefined,
    productIds: undefined,
    forceUpdate: 0,
    tags: '',
  };
  initBrand();
  visible.value = true;
  getTags();
  initData();
}

async function getTags() {
  try {
    const res = await queryTagsByIdsApi(selectedRowKeys.value);
    console.log(res,'res');
    if (res.success) {
      model.value.tags = res.result.replace(/\|/g, '、');
    }
  } catch (error) {
    console.error('获取标签失败:', error);
  }
}

function close() {
  selectedRowKeys.value = [];
  model.value = {
    brandId: undefined,
    productIds: undefined,
    forceUpdate: 0,
    tags: '',
  };
  visible.value = false;
}

async function handleOk() {
  if (!model.value.brandId) {
    createMessage.warning('请选择品牌');
    return;
  }
  if (!model.value.productIds || model.value.productIds.length === 0) {
    createMessage.warning('请选择产品');
    return;
  }
  try {
    const params = {
      tagIds: selectedRowKeys.value.join(','),
      productIds: model.value.productIds,
    };
    socialMediaCategoryList.value.forEach((item) => {
      let finalSelectedIds = item.selectedIds;
      if (item.filterCategoryName && item.oldSelectedIds) {
        finalSelectedIds = [...item.oldSelectedIds];
      }
      const list = item.data
        .filter((subItem) => finalSelectedIds.includes(subItem.id) && !subItem.disabled)
        .map((subItem) => ({
          attributeId: subItem.id,
          attributeName: subItem.attributeName,
          level: '',
        }));
      if (list.length > 0) {
        params.attributeIds = list.map((item) => item.attributeId).join(",");
      }
    });
    if (!params.attributeIds) {
      createMessage.warning("至少选择一个达人类型");
      
      return;
    }
    confirmLoading.value = true;
    const res = await bindingProductsApi(params);
    if (res.success) {
      createMessage.success(res.message);
      close();
      emit('ok');
    } else {
      createMessage.warning(res.message || '操作失败');
    }
  } catch (error) {
    console.error('绑定产品失败:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

const emit = defineEmits(['ok']);

defineExpose({
  add,
  edit,
  close,
});
</script>

<style lang="less" scoped>
:deep(.ant-select-selection--multiple) {
  padding-bottom: 0px !important;
}
.related-categories {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 8px;
  max-width: 480px;
  min-width: 480px;
  .category-item {
    flex: none;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    width: 100%;
    height: 350px;
    border: 1px solid rgba(217, 217, 217, 1);

    .category-item-title {
      background-color: #f0f3fe;
      padding: 0 10px;
      display: flex;
      align-items: center;
      height: 32px;
      font-size: 14px;
    }
    .category-item-content {
      height: 300px;
      // overflow-y: auto;
      padding: 10px;
      .category-item-content-checkbox {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin: 10px 0;
      }
      .category-item-content-checkbox-group {
        overflow-y: auto;
        overflow-x: hidden;
        height: 232px;
        .ant-checkbox {
          top: 0 !important;
        }
      }
    }
  }
}
/deep/ .category-item-content-checkbox-group .ant-checkbox {
  top: 0 !important;
}
.checkbox-label {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
}
.checkbox-label-level {
  margin-left: 10px;
  display: flex;
  align-items: center;
  width: 45px;
  div {
    background-color: #f0f0f0;
    color: rgba(184, 184, 184, 1);
    width: 22px;
    height: 16px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 12px;
  }
  .level-active-red {
    background: rgba(255, 232, 232, 1);
    color: rgba(255, 102, 102, 1);
  }
  .level-active-orange {
    background: rgba(255, 232, 209, 1);
    color: rgba(255, 128, 0, 1);
  }
  .level-disabled {
    background-color: #f5f5f5 !important;
    color: #d9d9d9 !important;
    cursor: not-allowed !important;
  }
}
/deep/ .ant-checkbox-group {
  width: 100% !important;
  display: block !important;
}
/deep/ .ant-checkbox-group .ant-checkbox-wrapper {
  display: flex !important;
  align-items: center;
  width: 100% !important;
  font-size: 13px !important;
}
/deep/.ant-checkbox + span {
  width: 100% !important;
}
/deep/ .ant-checkbox-wrapper + .ant-checkbox-wrapper {
  margin-left: 0 !important;
}
.category-item-content-checkbox /deep/ .ant-checkbox-wrapper span:nth-child(2) {
  padding-left: 3px;
}
.association-level-item {
  display: flex;
  align-items: center;
  gap: 20px;
  // margin-left: 28px;
  // width: 100%;
  .association-level-item-level-height {
    background-color: #f0f0f0;
    color: rgba(184, 184, 184, 1);
    width: 22px;
    height: 16px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 12px;
    background: rgba(255, 232, 232, 1);
    color: rgba(255, 102, 102, 1);
    margin-right: 5px;
  }
  .association-level-item-level-small {
    background-color: #f0f0f0;
    color: rgba(184, 184, 184, 1);
    width: 22px;
    height: 16px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 12px;
    background: rgba(255, 232, 209, 1);
    color: rgba(255, 128, 0, 1);
    margin-right: 5px;
  }
}
</style>
