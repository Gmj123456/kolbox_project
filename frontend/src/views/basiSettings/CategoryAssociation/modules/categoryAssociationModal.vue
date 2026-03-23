<template>
  <a-modal
  width="700px"
    :maskClosable="false"
    :title="title"
    :open="visible"
    :keyboard="false"
    :confirmLoading="confirmLoading"
    cancelText="关闭"
    okText="应用"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <!-- <a-form-model ref="form"  :model="model">
        
      </a-form-model> -->
      <div class="category-tree-container">
        <div v-if="!model.id" class="category-tree">
          <el-tree
            :expand-on-click-node="false"
            ref="tree"
            icon-class=""
            highlight-current
            node-key="id"
            default-expand-all
            :data="productCategoryList"
            :default-checked-keys="model.selProductCategoryId ? [model.selProductCategoryId] : []"
            :props="defaultProps"
            @node-click="queryByCategoryId"
          ></el-tree>
        </div>
        <div style="width: 100%;">
          <div
            :style="
              model.id
                ? {
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0px',
                    marginBottom: '10px',
                  }
                : {
                    display: 'flex',
                    alignItems: 'center',
                    gap: '20px',
                    marginBottom: '10px',
                  }
            "
          >
            <div v-if="model.id">
              <span> {{ parseCategoryName(selProductCategoryName) }}</span>
            </div>
            <div
              :style="model.id ? { marginLeft: '0px' } : { marginLeft: '0px' }"
              class="association-level-item"
            >
              <div style="display: flex; align-items: center">
                <span class="association-level-item-level-height">高</span>
                <span>高关联</span>
              </div>
              <div style="display: flex; align-items: center">
                <span class="association-level-item-level-small">低</span>
                <span>低关联</span>
              </div>
            </div>
            <!-- <a-row :gutter="12">
            <a-col v-if="model.id" :span="3">
              <a-form-model-item>
              
               
              </a-form-model-item>
            </a-col>
            <a-col :span="4">
              <a-form-model-item>
              
              </a-form-model-item>
            </a-col>
          </a-row> -->
          </div>
          <div class="related-categories">
            <div class="category-item" v-for="item in categoryList" :key="item.typeId">
              <div class="category-item-title">
                {{ item.typeName }}
              </div>
              <div class="category-item-content">
                <div>
                  <a-input
                    v-model:value="item.filterCategoryName"
                    :placeholder="item.typeName + '筛选'"
                    @change="filterCategory(item)"
                  />
                </div>
                <template v-if="item.data.length > 0 && ishandleOk">
                  <div v-if="item.data.length > 0" class="category-item-content-checkbox">
                    <a-checkbox
                      :indeterminate="item.indeterminate"
                      :checked="item.checkAll"
                      :disabled="item.allDisabled || !selectedNode"
                      @change="checkAllChange(item)"
                    >
                      全选
                    </a-checkbox>
                    <span
                      >已选择 <a>{{ getTotalSelectedCount(item) }}</a> 个</span
                    >
                  </div>
                  <div class="category-item-content-checkbox-group">
                    <a-checkbox-group
                      v-model:value="item.selectedIds"
                      @change="checkboxChange(item)"
                    >
                      <a-row :gutter="12">
                        <a-col :span="12"  v-for="option in getFilteredData(item)" :key="option.value">
                          <a-checkbox
                          v-if="!option.disabled"
                          :disabled="!!option.disabled || !selectedNode"
                          :value="option.value"
                        >
                        <span class="checkbox-label">
                          <span style="width: 230px">
                            <!-- {{ option.label }} -->
                            <CategoryEllipsisTooltip
                              :text="option.label"
                            ></CategoryEllipsisTooltip>
                          </span>
                          <span v-if="isShowLevel" class="checkbox-label-level">
                            <div
                              @click="handleLevelClick(item, option, '1', $event)"
                              :class="{
                                'level-active-red': option.level == '1',
                                'level-disabled': !!option.disabled,
                              }"
                            >
                              高
                            </div>
                            <div
                              @click="handleLevelClick(item, option, '3', $event)"
                              :class="{
                                'level-active-orange': option.level == '3',
                                'level-disabled': !!option.disabled,
                              }"
                            >
                              低
                            </div>
                          </span>
                        </span>
                      </a-checkbox>
                        </a-col>
                      </a-row>
                      
                    </a-checkbox-group>
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
    <template #footer>
      <a-button @click="handleCancel" key="cancel">关闭</a-button>
      <a-button @click="handleOk" key="submit" type="primary">应用</a-button>
    </template>
  </a-modal>
</template>

<script setup>
import { ref, reactive, onBeforeUnmount } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import CategoryEllipsisTooltip from './CategoryEllipsisTooltip.vue';
import { Modal } from 'ant-design-vue';
const emit = defineEmits(['ok']);
const { createMessage, createConfirm } = useMessage();

const title = ref('操作');
const visible = ref(false);
const model = reactive({});
const isEdit = ref(false);
const confirmLoading = ref(false);
const categoryList = ref([]);
const productCategoryList = ref([]);
const orangeCategoryList = ref([]);
const selProductCategoryName = ref('');
const selectedNode = ref(null);
const ishandleOk = ref(true);
const isShowLevel = ref(false);
const defaultProps = {
  children: 'children',
  label: 'categoryName',
  isLeaf: 'isHasChild',
};

// 初始状态保存，用于判断是否有修改
const initialCategoryList = ref([]);
const initialSelectedNode = ref(null);

const treeRef = ref(null);
let submitTimer = null;

onBeforeUnmount(() => {
  // 清理所有防抖定时器
  if (categoryList.value && categoryList.value.length > 0) {
    categoryList.value.forEach((item) => {
      if (item.filterTimer) {
        clearTimeout(item.filterTimer);
        item.filterTimer = null;
      }
    });
  }
  if (submitTimer) {
    clearTimeout(submitTimer);
    submitTimer = null;
  }
});
// 保存初始状态
const saveInitialState = () => {
  // 深拷贝保存初始状态
  initialCategoryList.value = JSON.parse(JSON.stringify(categoryList.value));
  initialSelectedNode.value = selectedNode.value ? { ...selectedNode.value } : null;
};

// 检查是否有修改
const hasUnsavedChanges = () => {
  // 检查选中的节点是否有变化
  if (initialSelectedNode.value && selectedNode.value) {
    if (initialSelectedNode.value.id !== selectedNode.value.id) {
      return true;
    }
  } else if (initialSelectedNode.value !== selectedNode.value) {
    return true;
  }

  // 检查categoryList是否有变化
  if (initialCategoryList.value.length !== categoryList.value.length) {
    return true;
  }

  // 获取当前的最终状态（包括筛选状态的处理）
  const currentFinalState = getCurrentFinalState();
  const initialFinalState = getInitialFinalState();

  // 比较最终状态
  return !deepEqual(currentFinalState, initialFinalState);
};

// 获取当前的最终状态
const getCurrentFinalState = () => {
  const state = {};

  categoryList.value.forEach((item) => {
    // 获取真实的选中状态
    let selectedIds;
    if (item.filterCategoryName && item.oldSelectedIds) {
      // 筛选状态下，先同步当前状态到全局
      syncCurrentSelectionToGlobal(item);
      selectedIds = [...(item.oldSelectedIds || [])];
    } else {
      selectedIds = [...(item.selectedIds || [])];
    }

    // 获取level状态
    const levels = {};
    if (item.data) {
      item.data.forEach((dataItem) => {
        if (selectedIds.includes(dataItem.id) && dataItem.level !== undefined) {
          levels[dataItem.id] = dataItem.level;
        }
      });
    }

    state[item.typeId] = {
      selectedIds: selectedIds.sort(),
      levels: levels,
    };
  });

  return state;
};

// 获取初始的最终状态
const getInitialFinalState = () => {
  const state = {};

  initialCategoryList.value.forEach((item) => {
    const selectedIds = [...(item.selectedIds || [])];

    // 获取level状态
    const levels = {};
    if (item.data) {
      item.data.forEach((dataItem) => {
        if (selectedIds.includes(dataItem.id) && dataItem.level !== undefined) {
          levels[dataItem.id] = dataItem.level;
        }
      });
    }

    state[item.typeId] = {
      selectedIds: selectedIds.sort(),
      levels: levels,
    };
  });

  return state;
};

// 深度比较两个对象
const deepEqual = (obj1, obj2) => {
  if (obj1 === obj2) return true;

  if (obj1 == null || obj2 == null) return obj1 === obj2;

  if (typeof obj1 !== typeof obj2) return false;

  if (typeof obj1 !== 'object') return obj1 === obj2;

  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);

  if (keys1.length !== keys2.length) return false;

  for (let key of keys1) {
    if (!keys2.includes(key)) return false;
    if (!deepEqual(obj1[key], obj2[key])) return false;
  }

  return true;
};

// 重置初始状态
const resetInitialState = () => {
  initialCategoryList.value = [];
  initialSelectedNode.value = null;
};

// 比较原数据和当前数据，检查是否有原本选中的项目被修改（取消选中）
const compareSelectedItems = () => {
  // 遍历原数据(orangeCategoryList)，检查原本选中的项目
  for (let orangeItem of orangeCategoryList.value) {
    if (orangeItem.selectedIds && orangeItem.selectedIds.length > 0) {
      // 找到对应的当前数据项
      const currentItem = categoryList.value.find(
        (item) => item.typeId === orangeItem.typeId
      );

      if (currentItem) {
        // 获取当前项目的真实选中状态
        // 如果在筛选状态下，使用oldSelectedIds；否则使用selectedIds
        let currentSelectedIds;
        if (currentItem.filterCategoryName && currentItem.oldSelectedIds) {
          // 筛选状态下：需要合并oldSelectedIds和当前selectedIds
          // 先同步当前状态到全局
          syncCurrentSelectionToGlobal(currentItem);
          currentSelectedIds = currentItem.oldSelectedIds;
        } else {
          // 非筛选状态下：直接使用selectedIds
          currentSelectedIds = currentItem.selectedIds;
        }

        // 检查原本选中的项目是否有任何一个不在当前数据的选中列表中
        for (let originalSelectedId of orangeItem.selectedIds) {
          if (!currentSelectedIds.includes(originalSelectedId)) {
            // 发现有原数据中选中的项目不在新数据中，立即返回true
            return {
              hasModified: true,
            };
          }
        }
      }
    } else {
      // 原数据中没有选中项，检查当前是否有选中项
      const currentItem = categoryList.value.find(
        (item) => item.typeId === orangeItem.typeId
      );
      if (currentItem) {
        let currentSelectedIds;
        if (currentItem.filterCategoryName && currentItem.oldSelectedIds) {
          syncCurrentSelectionToGlobal(currentItem);
          currentSelectedIds = currentItem.oldSelectedIds;
        } else {
          currentSelectedIds = currentItem.selectedIds;
        }

        if (currentSelectedIds && currentSelectedIds.length > 0) {
          return {
            hasModified: true,
          };
        }
      }
    }
  }

  // 所有原数据中的选中项目都在新数据中找到了，且没有新增项目
  return {
    hasModified: false,
  };
};

const parseCategoryName = (name) => {
  return name?.replaceAll('|', '-');
};

const getSelProductCategory = (value) => {
  // 递归查找树形结构中的节点
  const findNodeInTree = (tree, targetValue) => {
    for (let node of tree) {
      if (node.id === targetValue) {
        return node;
      }
      if (node.children && node.children.length > 0) {
        const found = findNodeInTree(node.children, targetValue);
        if (found) {
          return found;
        }
      }
    }
    return null;
  };

  if (value && productCategoryList.value.length > 0) {
    const foundNode = findNodeInTree(productCategoryList.value, value);
    console.log(foundNode);
    if (foundNode) {
      selProductCategoryName.value = foundNode.attributeNamePath?.replaceAll('/', '- ');
      return foundNode;
    }
  }

  selProductCategoryName.value = '';
  return null;
};

const isObject = (value) => {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
};

const queryByCategoryId = async (node) => {
  if (!isObject(node)) {
    selectedNode.value = getSelProductCategory(node);
  } else {
    model.selProductCategoryId = node.id;
    selectedNode.value = node;
  }
  console.log(selectedNode.value);

  if (selectedNode.value.isHasChild == 0) {
    isShowLevel.value = true;
  } else {
    isShowLevel.value = false;
  }
  const res = await defHttp.get({
    url: '/kol/kolCategoryRelation/queryByCategoryId',
    params: { categoryId: selectedNode.value.id },
  });
  if (res) {
    if (selectedNode.value.parentId == '0') {
      handleCategoryList(res);
      ishandleOk.value = true;
    } else if (selectedNode.value.parentId !== '0' && res.length == 0) {
      ishandleOk.value = false;
    } else {
      ishandleOk.value = true;
      handleCategoryList(res);
    }
  }
};

// 获取过滤后的数据，不修改原数据
// 筛选时只显示匹配条件的数据，但保留原有的选中状态不变
const getFilteredData = (item) => {
  if (!item.filterCategoryName) {
    return item.data;
  }

  // 显示匹配筛选条件的选项
  const filteredResult = item.data.filter((option) =>
    option.label.includes(item.filterCategoryName)
  );

  return filteredResult;
};

// 更新筛选时的选中状态显示（保留旧方法以防其他地方使用）
const _updateSelectedIdsForFilter = (item) => {
  if (!item.oldSelectedIds) return;

  // 获取当前筛选显示的数据
  const filteredData = getFilteredData(item);
  const filteredIds = filteredData.map((option) => option.value);

  // 更新selectedIds：只包含当前可见的选中项
  // 1. 从oldSelectedIds中筛选出仍在筛选结果中的项目
  const visibleFromOld = item.oldSelectedIds.filter((id) => filteredIds.includes(id));
  // 2. 当前selectedIds中在筛选结果中的项目
  const visibleFromCurrent = item.selectedIds.filter((id) =>
    filteredIds.includes(id)
  );
  // 3. 合并去重
  item.selectedIds = [...new Set([...visibleFromOld, ...visibleFromCurrent])];
};

// 合并选中状态的通用方法
const _mergeSelectedIds = (oldSelectedIds, currentSelectedIds, allData, filterText) => {
  if (!oldSelectedIds) return currentSelectedIds;

  // 以当前选中为基础
  let mergedIds = [...currentSelectedIds];

  // 如果有筛选条件，需要补充那些被筛选隐藏但未被主动取消的项目
  if (filterText) {
    const hiddenButStillSelected = oldSelectedIds.filter((oldId) => {
      // 检查是否在当前选中中（如果在，说明用户保持选中状态）
      if (currentSelectedIds.includes(oldId)) return false;

      // 检查是否因筛选被隐藏
      const option = allData.find((dataItem) => dataItem.id === oldId);
      if (!option) return false;

      // 如果不匹配筛选条件，说明被隐藏了，应该保留
      return !option.label.includes(filterText);
    });

    mergedIds = [...new Set([...mergedIds, ...hiddenButStillSelected])];
  }

  return mergedIds;
};

// 更新全选状态
const updateCheckAllStatus = (item) => {
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
};

// 清空筛选时恢复选中状态的专用方法
const restoreSelectedIdsAfterClearFilter = (
  oldSelectedIds,
  currentSelectedIds,
  allData,
  filterText
) => {
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
};

// 恢复level状态
const restoreLevelStatus = (item) => {
  // 为所有选中的选项恢复或设置level状态
  item.selectedIds.forEach((id) => {
    const option = item.data.find((dataItem) => dataItem.id === id);
    if (option && !option.disabled) {
      // 如果没有level，设置默认level为'1'
      if (!option.level) {
        option.level = '1';
      }
    }
  });

  // 清除未选中选项的level
  item.data.forEach((option) => {
    if (!item.selectedIds.includes(option.id)) {
      option.level = undefined;
    }
  });
};

// 同步当前筛选状态下的选择到全局状态
const syncCurrentSelectionToGlobal = (item) => {
  // 获取当前筛选的数据
  const currentFilteredData = getFilteredData(item);

  // 如果当前筛选无结果，跳过同步避免清空全局状态
  if (currentFilteredData.length === 0) {
    return;
  }

  // 如果oldSelectedIds未初始化，使用originalSelectedIds初始化
  if (!item.oldSelectedIds) {
    item.oldSelectedIds = [...(item.originalSelectedIds || [])];
  }

  // 如果oldLevelMap未初始化，初始化为空对象
  if (!item.oldLevelMap) {
    item.oldLevelMap = {};
  }

  // 遍历当前筛选的数据，同步选中状态和level状态到全局
  currentFilteredData.forEach((subItem) => {
    const isCurrentlySelected = item.selectedIds.includes(subItem.id);
    const wasGloballySelected = item.oldSelectedIds.includes(subItem.id);

    if (isCurrentlySelected && !wasGloballySelected) {
      // 新选中的项目，添加到全局状态
      item.oldSelectedIds.push(subItem.id);
    } else if (!isCurrentlySelected && wasGloballySelected) {
      // 取消选中的项目，从全局状态移除
      const index = item.oldSelectedIds.indexOf(subItem.id);
      if (index > -1) {
        item.oldSelectedIds.splice(index, 1);
      }
    }

    // 同步level状态：无论是否选中，都要保存当前的level状态
    if (subItem.level !== undefined) {
      item.oldLevelMap[subItem.value] = subItem.level;
    } else if (item.oldLevelMap[subItem.value] !== undefined) {
      // 如果当前没有level但之前有，保持之前的状态
    }
  });
};

// 根据当前筛选条件更新显示的选中状态
const updateSelectedIdsForCurrentFilter = (item) => {
  if (!item.oldSelectedIds) return;

  // 获取当前筛选显示的数据
  const filteredData = getFilteredData(item);

  // 从全局选中状态中筛选出在当前筛选结果中可见的选中项
  // 关键：检查oldSelectedIds中的每个ID是否在当前筛选结果中存在
  const newSelectedIds = [];

  item.oldSelectedIds.forEach((oldId) => {
    // 检查这个ID是否在当前筛选结果中
    const matchedOption = filteredData.find(
      (option) => option.value === oldId || option.id === oldId
    );

    if (matchedOption) {
      newSelectedIds.push(oldId);

      // 同时恢复level状态
      if (item.oldLevelMap && item.oldLevelMap[oldId] !== undefined) {
        matchedOption.level = item.oldLevelMap[oldId];
      }
    } else {
      // 即使不在当前筛选结果中，也要保持选中状态
      newSelectedIds.push(oldId);
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
    }
  });

  item.selectedIds = newSelectedIds;
};

// 同步筛选状态下的选中状态（保留原方法供其他地方使用）
const syncFilteredSelectionState = (item) => {
  // 直接调用新的同步方法
  syncCurrentSelectionToGlobal(item);
};

const filterCategory = (item) => {
  // 清除之前的防抖定时器
  if (item.filterTimer) {
    clearTimeout(item.filterTimer);
  }

  // 设置防抖延迟执行
  item.filterTimer = setTimeout(() => {
    executeFilterCategory(item);
  }, 300); // 300ms防抖延迟
};

// 实际执行筛选的方法
const executeFilterCategory = (item) => {
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
      item.oldSelectedIds = [...item.selectedIds];
      item.originalSelectedIds = [...item.selectedIds]; // 保存最初的选中状态

      // 全面同步level状态
      syncAllLevelStates(item);
    } else {
      // 如果已经在筛选状态，先同步当前选择和level状态到全局状态
      // 这很重要：无论筛选结果如何，都要先保存当前的选择和level状态
      syncCurrentSelectionToGlobal(item);
      syncAllLevelStates(item);
    }

    // 更新筛选条件记录
    item.previousFilterText = item.filterCategoryName;

    // 检查当前筛选条件是否有结果
    const filteredData = getFilteredData(item);

    if (filteredData.length > 0) {
      // 有筛选结果时：恢复所有状态
      restoreAllStates(item);
    } else {
      // 无筛选结果时：只清空显示，不影响全局状态
      // 重要：不要在这里调用syncCurrentSelectionToGlobal，因为selectedIds为空会清空全局状态
      // item.selectedIds = [];

      // 即使筛选无结果，也要恢复所有状态
      restoreAllStates(item);
    }
  }

  // 更新全选状态
  updateCheckAllStatus(item);
};
// 全选
const checkAllChange = (item) => {
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
};

// 单选
const checkboxChange = (item) => {
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

  // 更新全选状态
  updateCheckAllStatus(item);

  // 确保在筛选状态下，用户的选择能被保存到全局状态
  if (item.filterCategoryName) {
    // 如果还没有初始化 oldSelectedIds，先初始化
    if (!item.oldSelectedIds) {
      item.oldSelectedIds = [];
    }

    // 实时更新筛选前的状态记录，确保状态同步
    syncFilteredSelectionState(item);
    // 同时同步level状态
    syncAllLevelStates(item);
  }
};

const handleLevelClick = (item, option, level, event) => {
  // 判断选中selectedIds中是否有点击的选项，有则可以设置值无则不设置
  // 同时检查选项是否被禁用
  if (item.selectedIds.includes(option.value) && !option.disabled) {
    option.level = level;

    // 如果在筛选状态下，需要同步level状态到全局
    if (item.filterCategoryName && item.oldLevelMap) {
      item.oldLevelMap[option.value] = level;
    }
  } else {
    option.level = undefined;

    // 如果在筛选状态下，也要同步到全局
    if (item.filterCategoryName && item.oldLevelMap) {
      delete item.oldLevelMap[option.value];
    }
  }

  // 无论是否在筛选状态，都进行一次全面的level状态同步
  syncAllLevelStates(item);

  event.preventDefault();
  event.stopPropagation();
};
const _filterTreeNode = (inputValue, treeNode) => {
  return (
    treeNode.data.props.title.toLowerCase().indexOf(inputValue.toLowerCase()) === 0
  );
};

const add = () => {
  edit({});
};

const edit = (record) => {
  Object.assign(model, record);
  model.selProductCategoryId = record.id;
  if (record.id) {
    isEdit.value = true;
  } else {
    isEdit.value = false;
  }
  visible.value = true;

  // 重置初始状态
  resetInitialState();

  // this.initDict()
  initData();
};
const initData = async () => {
  const typeRes = await defHttp.get({ url: '/kol/kolAttributeType/listAll' });
  const res = await defHttp.get({ url: '/kol/attribute/getKolAttribute' });
  if (res && typeRes) {
    // 按照 typeRes.result 的顺序创建 categoryList

    await initProductCategory();
    categoryList.value = typeRes
      .filter((item) => item.type == 2)
      .map((typeItem) => {
        // 查找是否在 res.result 中存在对应的数据
        const existingItem = res.find((item) => item.typeId === typeItem.id);

        if (existingItem) {
          // 如果存在，使用现有数据并添加必要属性
          return {
            ...existingItem,
            selectedIds: [],
            checkAll: false,
            filterCategoryName: '',
            indeterminate: false,
            allDisabled: false,
            oldSelectedIds: null,
            originalSelectedIds: null,
            previousFilterText: null,
            filterTimer: null,
            oldLevelMap: {}, // 保存筛选前的level状态
            typeName: typeItem.typeName,
          };
        } else {
          // 如果不存在，创建新的空项
          return {
            data: [],
            typeId: typeItem.id,
            typeName: typeItem.typeName,
            selectedIds: [],
            checkAll: false,
            filterCategoryName: '',
            indeterminate: false,
            allDisabled: false,
            oldSelectedIds: null,
            originalSelectedIds: null,
            previousFilterText: null,
            filterTimer: null,
            oldLevelMap: {}, // 保存筛选前的level状态
          };
        }
      });
      console.log(categoryList.value,model.selProductCategoryId);
    selectedNode.value = getSelProductCategory(model.selProductCategoryId);

    if (!model.selProductCategoryId) {
      handleCategoryList();
    } else {
      queryByCategoryId(model.selProductCategoryId);
    }
  }
};

// 处理数据
const handleCategoryList = (queryByCategoryList = []) => {
  if (queryByCategoryList.length == 0) {
    categoryList.value.forEach((item) => {
          item.selectedIds = [];
          item.checkAll = false;
          item.filterCategoryName = '';
          item.indeterminate = false;
          item.allDisabled = false;
          item.oldSelectedIds = null; // 清理筛选前的选中状态
          item.originalSelectedIds = null; // 清理原始选中状态
          item.previousFilterText = null; // 清理筛选条件记录
          if (item.filterTimer) {
            clearTimeout(item.filterTimer);
            item.filterTimer = null;
          }
          item.data.forEach((subItem) => {
            subItem.level = undefined;
            subItem.label = subItem.attributeNamePath;
            subItem.value = subItem.id;
            subItem.disabled = false;
          });
        });
      } else {
        // 先初始化所有categoryList项
        categoryList.value.forEach((item) => {
          item.selectedIds = [];
          item.checkAll = false;
          item.filterCategoryName = '';
          item.indeterminate = false;
          item.allDisabled = false;
          item.oldSelectedIds = null; // 清理筛选前的选中状态
          item.originalSelectedIds = null; // 清理原始选中状态
          item.previousFilterText = null; // 清理筛选条件记录
          if (item.filterTimer) {
            clearTimeout(item.filterTimer);
            item.filterTimer = null;
          }

          item.data.forEach((subItem) => {
            subItem.level = undefined;
            subItem.label = subItem.attributeNamePath;
            subItem.value = subItem.id;
            subItem.disabled = false;
            subItem.level = undefined;
          });
        });

        // 遍历queryByCategoryList设置选中状态和level
        queryByCategoryList.forEach((q) => {
          const matchedCategory = categoryList.value.find(
            (item) => q.typeId == item.typeId
          );
          if (matchedCategory) {
            // 收集该类型下所有选中的categoryId
            const selectedIds = q.list.filter((l) => l.isSel).map((l) => l.attributeId);
            matchedCategory.selectedIds = selectedIds;

            // 设置选中状态
            if (selectedIds.length > 0) {
              // 检查是否全选（只考虑非disabled的选项）
              const enabledData = matchedCategory.data.filter(
                (subItem) => !subItem.disabled
              );
              if (selectedIds.length === enabledData.length) {
                matchedCategory.checkAll = true;
                matchedCategory.indeterminate = false;
              } else {
                matchedCategory.checkAll = false;
                matchedCategory.indeterminate = true;
              }
            }

            // 为所有项设置level和disabled状态
            for (let i = 0; i < matchedCategory.data.length; i++) {
              const subItem = matchedCategory.data[i];
              const matchedQuery = q.list.find((l) => l.attributeId == subItem.id);
              if (matchedQuery) {
                subItem.level = matchedQuery.level;
                subItem.disabled = false;
              } else {
                

                if (selectedNode.value.parentId == '0') {
                  subItem.disabled = false;
                } else {
                  subItem.disabled = true;
                }
              }
            }

            // 检查是否所有选项都被禁用
            const allDisabled = matchedCategory.data.every((subItem) => subItem.disabled);
            matchedCategory.allDisabled = allDisabled;
          } else {
          }
        });

        // 处理没有在queryByCategoryList中找到的categoryList数据
        categoryList.value.forEach((item) => {
          const hasMatch = queryByCategoryList.some((q) => q.typeId == item.typeId);
          if (!hasMatch) {
            // 根据isOnLevelFn判断是否需要禁用
            item.data.forEach((subItem) => {
              if (
                (selectedNode.value.isHasChild == 1 &&
                  selectedNode.value.parentId != '0') ||
                selectedNode.value.isHasChild == 0
              ) {
                subItem.disabled = true;
              } else {
                subItem.disabled = false;
              }
            });

            // 检查是否所有选项都被禁用
            const allDisabled = item.data.every((subItem) => subItem.disabled);
            item.allDisabled = allDisabled;
          }
        });
      }

      // 所有数据处理完成后需要处理全选的状态
      categoryList.value.forEach((item) => {
        if (item.selectedIds.length > 0) {
          // 检查是否全选（只考虑非disabled的选项）
          const enabledData = item.data.filter((subItem) => !subItem.disabled);
          if (enabledData.length > 0) {
            if (item.selectedIds.length === enabledData.length) {
              item.checkAll = true;
              item.indeterminate = false;
            } else {
              item.checkAll = false;
              item.indeterminate = true;
            }
          } else {
            item.checkAll = false;
            item.indeterminate = false;
          }
        } else {
          item.checkAll = false;
          item.indeterminate = false;
        }
      });
      sortData();
      if (treeRef.value && selectedNode.value) {
        treeRef.value.setCurrentKey(selectedNode.value.id);
      }
      // 深拷贝categoryList，确保orangeCategoryList不会被后续操作影响
      orangeCategoryList.value = JSON.parse(JSON.stringify(categoryList.value));
      console.log(orangeCategoryList.value);
      // 数据初始化完成后，确保所有选项的状态都正确
      categoryList.value.forEach((item) => {
        if (item.oldLevelMap) {
          // 如果有保存的level状态，恢复所有选项的level状态
          restoreLevelStates(item);
        }
      });

      // 保存初始状态，用于判断是否有修改
      saveInitialState();
};

const sortData = () => {
  categoryList.value.forEach((item) => {
        item.data.sort((a, b) => {
          // 先按是否选中排序
          const aSelected = item.selectedIds.includes(a.id);
          const bSelected = item.selectedIds.includes(b.id);
          if (aSelected && !bSelected) return -1;
          if (!aSelected && bSelected) return 1;

          // 再按是否禁用排序，未禁用的排在前面
          if (!a.disabled && b.disabled) return -1;
          if (a.disabled && !b.disabled) return 1;

          return 0;
        });
      });
};

const _initDict = async () => {
  // 如果需要字典数据，可以在这里调用
  // const res = await ajaxGetDictItems("association_level");
  // if (res.success) {
  //   associationLevelList.value = res.result;
  // }
};

const initProductCategory = async () => {
  const res = await defHttp.get({ url: '/kol/category/loadTreeDataAll' });
  if (res) {
    productCategoryList.value = res;
    console.log(productCategoryList.value);
  }
};
const close = () => {
  // 清理所有筛选定时器
  categoryList.value.forEach((item) => {
    if (item.filterTimer) {
      clearTimeout(item.filterTimer);
      item.filterTimer = null;
    }
  });

  emit('ok');
  Object.assign(model, {});
  selectedNode.value = null;
  isShowLevel.value = false;
  selProductCategoryName.value = '';
  ishandleOk.value = true;
  visible.value = false;

  // 重置初始状态
  resetInitialState();
};

const handleOk = () => {
  // 防止连续执行 - 使用loading状态和防抖机制
  if (confirmLoading.value) {
    return;
  }
  if (!ishandleOk.value) {
    createMessage.error('当前类目的上层没有设置匹配数据，请先去设置匹配数据');
    return;
  }
  if (!selectedNode.value || !selectedNode.value.id) {
    createMessage.error('请选择类目');
    return;
  }
  // 防抖处理 - 清除之前的定时器
  if (submitTimer) {
    clearTimeout(submitTimer);
  }

  const result =
    selectedNode.value.children && selectedNode.value.children.length > 0
      ? compareSelectedItems()
      : { hasModified: false };
  if (result.hasModified) {
    createConfirm({
      title: '提示',
      content: '当前操作会同步影响子级，是否继续操作？',
      onOk: () => {
        // 设置防抖延迟
        submitTimer = setTimeout(() => {
          executeSubmit();
        }, 300);
      },
      onCancel: () => {
        return;
      },
    });
    return;
  }
  // 设置防抖延迟
  submitTimer = setTimeout(() => {
    executeSubmit();
  }, 300);
};

const executeSubmit = async () => {
  // 再次检查loading状态
  if (confirmLoading.value) {
    return;
  }

  // 在提交前，先同步所有状态，确保数据完整性
  categoryList.value.forEach((item) => {
    // 如果在筛选状态下，先同步当前状态到全局
    if (item.filterCategoryName) {
      syncCurrentSelectionToGlobal(item);
      syncAllLevelStates(item);
    }
    // 恢复所有状态，确保数据完整
    restoreAllStates(item);
  });

  confirmLoading.value = true;
  const params = {
    categoryId: selectedNode.value.id,
    categoryName: selectedNode.value.categoryName,
    categoryNamePath: selectedNode.value.categoryNamePath,
    dataList: [],
  };

  for (let i = 0; i < categoryList.value.length; i++) {
    const item = categoryList.value[i];

    // 获取真正的选中状态：优先使用全局状态，如果没有则使用当前状态
    let finalSelectedIds;
    if (item.filterCategoryName && item.oldSelectedIds) {
      // 筛选状态下，使用全局状态
      finalSelectedIds = [...item.oldSelectedIds];
    } else {
      // 非筛选状态下，使用当前状态
      finalSelectedIds = [...item.selectedIds];
    }

    const list = item.data
      .filter((subItem) => finalSelectedIds.includes(subItem.id) && !subItem.disabled)
      .map((subItem) => {
        return {
          attributeId: subItem.id,
          attributeName: subItem.attributeName,
          level: selectedNode.value.isHasChild !== 1 ? subItem.level : '',
        };
      });

    // 只有当list不为空时才添加到dataList中
    if (list.length > 0) {
      params.dataList.push({
        typeId: item.typeId,
        list: list,
      });
    }
  }

  try {
    const res = await defHttp.post({ url: '/kol/kolCategoryRelation/add', data: params });
    initData();
    // if (res?.success) {
    //   createMessage.success('保存成功');
    // } else {
    //   createMessage.error(res?.message || '保存失败');
    // }
  } catch (error) {
    console.error('提交失败:', error);
  } finally {
    confirmLoading.value = false;
    // 清除定时器
    if (submitTimer) {
      clearTimeout(submitTimer);
      submitTimer = null;
    }
  }
};

const handleCancel = (e) => {
  console.log(e)
  e?.preventDefault?.()
  // 检查是否有未保存的修改
  if (hasUnsavedChanges()) {
    Modal.confirm({
      title: '提示',
      content: '您有未保存的修改，确定要关闭吗？关闭后修改将丢失。',
      okText: '确定关闭',
      cancelText: '继续编辑',
      centered: true,
      onOk: () => {
        close();
      },
      onCancel: () => {
        // 用户选择继续编辑，不做任何操作
      },
    });
  } else {
    // 没有未保存的修改，直接关闭
    close();
  }
};

const getTotalSelectedCount = (item) => {
  // 如果在筛选状态下，显示全局选中数量（oldSelectedIds）
  // 如果不在筛选状态下，显示当前选中数量（selectedIds）
  if (item.filterCategoryName && item.oldSelectedIds) {
    return item.oldSelectedIds.length;
  }
  return item.selectedIds.length;
};

// 全面同步level状态的方法
const syncAllLevelStates = (item) => {
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
    }
  });

  // 2. 保存所有选项的level状态（包括不可见的）
  // 这确保了被筛选隐藏的选项的level状态不会丢失
  item.data.forEach((option) => {
    if (option.level !== undefined) {
      // 只有当前选项有level值时才更新，避免覆盖已保存的隐藏选项状态
      item.oldLevelMap[option.value] = option.level;
    }
  });
};

// 恢复level状态的方法
const restoreLevelStates = (item) => {
  if (!item.oldLevelMap) {
    return;
  }

  // 恢复所有选项的level状态（包括不可见的）
  item.data.forEach((option) => {
    if (item.oldLevelMap[option.value] !== undefined) {
      option.level = item.oldLevelMap[option.value];
    }
  });
};

// 保护不可见选项的level状态
const protectHiddenLevelStates = (item) => {
  if (!item.oldLevelMap) {
    item.oldLevelMap = {};
  }

  // 获取当前筛选的数据
  const currentFilteredData = getFilteredData(item);
  const visibleIds = currentFilteredData.map((option) => option.value);

  // 遍历所有数据，为不可见但有level状态的选项恢复level
  item.data.forEach((option) => {
    // 如果选项不在当前筛选结果中，但在oldLevelMap中有保存的level状态
    if (
      !visibleIds.includes(option.value) &&
      item.oldLevelMap[option.value] !== undefined
    ) {
      option.level = item.oldLevelMap[option.value];
    }
  });
};

// 通用的状态恢复方法，确保所有状态都正确
const restoreAllStates = (item) => {
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
};

defineExpose({
  add,
  edit,
});
</script>

<style lang="less" scoped>
/deep/ .ant-modal {
  width: fit-content !important;
}
.category-tree-container {
  display: flex;
  gap: 20px;
}
.category-tree {
  overflow: auto;
  width: 300px;
  padding-right: 10px;
  height: 531px;
}
.related-categories {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 8px;
  .category-item {
    flex: none;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    // width: 340px;
    width: 100%;
    height: 500px;
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
      height: 468px;
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
        height: 380px;
      }
    }
  }
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
/deep/ .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
  background-color: #f0f3fe;
  color: @primary-color;
}
/deep/.ant-checkbox-group label {
  margin-bottom: 14px;
}
a {
  color: @primary-color;
}
</style>
