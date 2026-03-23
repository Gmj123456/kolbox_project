<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :open="visible"
    :confirmLoading="confirmLoading"
    class="match-category-association-modal"
    cancelText="关闭"
    okText="应用"
  :keyboard="false"
  @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <!-- <a-form-model ref="form"  :model="model">
        
      </a-form-model> -->
      <div class="category-tree-container">
        <div v-if="!model.id" class="category-tree">
          <el-tree
            :expand-on-click-node="false"
            ref="treeRef"
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
        <div>
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
            <div class="category-item"  style="width: 600px;" v-for="item in categoryList" :key="item.typeId">
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
                      :disabled="item.allDisabled"
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
                      <a-col :span="12" v-for="option in getFilteredData(item)" :key="option.value">
                        <a-checkbox
                          v-if="!option.disabled"
                          :disabled="!!option.disabled"
                          :value="option.value"
                        >
                        <span class="checkbox-label">
                          <span style="width: 202px">
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
import {ElTree} from 'element-plus';
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
const ishandleOk = ref(true);
const isShowLevel = ref(false);
const defaultProps = {
  children: 'children',
  label: 'categoryName',
  isLeaf: 'isHasChild',
};

// 新增：存储每个类目的选中数据
const categoryDataMap = ref(new Map()); // key: categoryId, value: { categoryId, categoryName, categoryNamePath, dataList: [] }
// 新增：当前正在编辑的类目ID
const currentEditingCategoryId = ref(null);
// 新增：记录初始状态，用于检查是否有修改
const initialCategoryDataMap = ref(new Map());

const treeRef = ref(null);
const selProductCategoryName = ref('');
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
const isHasChild = (id) => {
  // 递归查找树形结构中匹配的节点
  const findNode = (nodes) => {
    for (const node of nodes) {
      if (node.id === id) {
        return node;
      }
      if (node.children && node.children.length > 0) {
        const found = findNode(node.children);
        if (found) return found;
      }
    }
    return null;
  };

  const node = findNode(productCategoryList.value);
  return node?.parentId;
};

// 新增：保存当前类目的选中数据
const saveCurrentCategoryData = () => {
  if (!currentEditingCategoryId.value) {
    return;
  }

  console.log('💾 保存当前类目数据:', currentEditingCategoryId.value);

  const categoryData = {
    categoryId: currentEditingCategoryId.value,
    dataList: [],
  };

  // 遍历所有属性类型，收集选中的数据
  categoryList.value.forEach((item) => {
    // 获取真正的选中状态：优先使用全局状态，如果没有则使用当前状态
    let finalSelectedIds;
    if (item.filterCategoryName && item.oldSelectedIds) {
      // 筛选状态下，使用全局状态
      finalSelectedIds = [...item.oldSelectedIds];
    } else {
      // 非筛选状态下，使用当前状态
      finalSelectedIds = [...item.selectedIds];
    }

    if (finalSelectedIds && finalSelectedIds.length > 0) {
      const list = item.data
        .filter(
          (subItem) => finalSelectedIds.includes(subItem.id) && !subItem.disabled
        )
        .map((subItem) => ({
          attributeId: subItem.id,
          attributeName: subItem.attributeName,
          level: subItem.level || '',
        }));

      if (list.length > 0) {
        categoryData.dataList.push({
          typeId: item.typeId,
          list: list,
        });
      }
    }
  });

  // 保存数据到Map中，包括空的选择状态
  categoryDataMap.value.set(currentEditingCategoryId.value, categoryData);
  console.log('💾 保存类目数据:', {
    categoryId: currentEditingCategoryId.value,
    hasData: categoryData.dataList.length > 0,
    dataListLength: categoryData.dataList.length,
  });
  console.log('💾 当前所有保存的类目:', Array.from(categoryDataMap.value.keys()));
};

// 新增：恢复指定类目的数据
const restoreCategoryData = (categoryId) => {
  if (!categoryDataMap.value.has(categoryId)) {
    console.log('📂 类目', categoryId, '没有保存的数据');
    return;
  }

  const savedData = categoryDataMap.value.get(categoryId);
  console.log('📂 恢复类目数据:', {
    categoryId,
    hasData: savedData.dataList.length > 0,
    dataListLength: savedData.dataList.length,
  });

  // 恢复选中状态和级别，但保留接口返回的disabled状态
  savedData.dataList.forEach((typeData) => {
    const categoryItem = categoryList.value.find(
      (item) => item.typeId === typeData.typeId
    );
    if (categoryItem) {
      // 恢复选中状态
      categoryItem.selectedIds = typeData.list.map((item) => item.attributeId);

      // 恢复级别状态，对所有保存的选项设置级别（不管是否禁用）
      typeData.list.forEach((attrData) => {
        const option = categoryItem.data.find(
          (opt) => opt.id === attrData.attributeId
        );
        if (option) {
          option.level = attrData.level;
        }
      });

      // 清除未保存选项的level状态
      const savedAttributeIds = typeData.list.map((item) => item.attributeId);
      categoryItem.data.forEach((option) => {
        if (!savedAttributeIds.includes(option.id)) {
          option.level = undefined;
        }
      });

      // 更新全选状态
      updateCheckAllStatus(categoryItem);
    }
  });

  // 处理那些在暂存数据中完全没有的类型（用户取消了所有选中）
  categoryList.value.forEach((item) => {
    const hasTypeData = savedData.dataList.some(
      (typeData) => typeData.typeId === item.typeId
    );
    if (!hasTypeData) {
      // 这个类型在暂存数据中没有，说明用户取消了所有选中
      item.selectedIds = [];
      item.data.forEach((option) => {
        option.level = undefined;
      });
      updateCheckAllStatus(item);
      console.log('📂 清空类型选择:', item.typeId);
    }
  });

  // 恢复数据后，重新检查所有选项的disabled状态
  categoryList.value.forEach((item) => {
    const parentId = isHasChild(currentEditingCategoryId.value);
    item.data.forEach((subItem) => {
      if (parentId === '0') {
        // 顶级类目，不禁用
        subItem.disabled = false;
      } else {
        // 非顶级类目，检查父级是否有修改缓存
        const parentSelection = checkParentCategorySelections(
          subItem.id,
          item.typeId
        );

        if (parentSelection === true) {
          // 父级已选中，当前类目未选中，设置为false（可操作）
          subItem.disabled = false;
        } else if (parentSelection === false) {
          // 父级未选中，当前类目也未选中，设置为true（禁用）
          subItem.disabled = true;
        } else {
          // 父级无数据，无法确定，默认禁用
          subItem.disabled = true;
        }
      }
    });

    // 重新检查是否所有选项都被禁用
    const allDisabled = item.data.every((subItem) => subItem.disabled);
    item.allDisabled = allDisabled;
  });

  // 恢复ishandleOk状态
  ishandleOk.value = true;

  console.log('📂 类目', categoryId, '数据恢复完成');
};

// 新增：获取所有类目的数据用于提交
const getAllCategoryData = () => {
  const allData = [];

  // 先保存当前正在编辑的类目数据
  if (currentEditingCategoryId.value) {
    console.log('📤 提交前保存当前类目', currentEditingCategoryId.value, '的数据');
    saveCurrentCategoryData();
  }

  // 收集所有类目的数据
  categoryDataMap.value.forEach((categoryData, categoryId) => {
    if (categoryData.dataList) {
      console.log('📤 收集类目', categoryId, '的数据:', categoryData);
      allData.push(categoryData);
    }
  });

  console.log('📤 所有类目数据:', allData);
  console.log('📤 总共收集到', allData.length, '个类目的数据');
  return allData;
};

// 新增：检查上级类目的选中状态，优先使用保存的数据
const checkParentCategorySelections = (attributeId, typeId) => {
  if (!currentEditingCategoryId.value) {
    return null;
  }

  // 获取当前类目的父级ID
  const parentId = isHasChild(currentEditingCategoryId.value);
  if (!parentId || parentId === '0') {
    return null; // 顶级类目或无父级，返回null
  }

  console.log('🔍 检查父级选择:', {
    attributeId,
    typeId,
    parentId: parentId,
    hasParentData: categoryDataMap.value.has(parentId),
    hasParentInitial: initialCategoryDataMap.value.has(parentId),
  });

  // 首先检查保存的父级类目暂存数据（categoryDataMap）
  const parentCategoryData = categoryDataMap.value.get(parentId);

  if (parentCategoryData && parentCategoryData.dataList) {
    const parentTypeData = parentCategoryData.dataList.find(
      (item) => item.typeId === typeId
    );
    if (parentTypeData && parentTypeData.list) {
      const parentAttribute = parentTypeData.list.find(
        (item) => item.attributeId === attributeId
      );
      if (parentAttribute) {
        console.log('🔍 ✅ 父级暂存数据中找到选择');
        return true; // 在父级保存数据中找到，说明被选中
      }
    }
    // 如果父级有暂存数据但没找到该属性，说明父级没有选择该属性
    console.log('🔍 ❌ 父级暂存数据中未选择');
    return false;
  }

  // 如果父级没有暂存数据，检查父级的初始状态数据
  const parentInitialData = initialCategoryDataMap.value.get(parentId);

  if (parentInitialData && parentInitialData.dataList) {
    const parentTypeData = parentInitialData.dataList.find(
      (item) => item.typeId === typeId
    );
    if (parentTypeData && parentTypeData.list) {
      const parentAttribute = parentTypeData.list.find(
        (item) => item.attributeId === attributeId
      );
      if (parentAttribute) {
        console.log('🔍 ✅ 父级初始数据中找到选择');
        return true; // 在父级初始数据中找到，说明被选中
      }
    }
    // 父级初始数据中没有找到，说明父级没有选择该属性
    console.log('🔍 ❌ 父级初始数据中未选择');
    return false;
  }

  // 如果父级既没有暂存数据也没有初始数据，返回null表示无法确定
  console.log('🔍 ⚠️ 父级无数据，无法确定状态');
  return null;
};

// 新增：检查是否有未保存的修改
const hasUnsavedChanges = () => {
  // 先保存当前类目的数据
  if (currentEditingCategoryId.value) {
    saveCurrentCategoryData();
  }

  // 比较当前数据与初始状态
  let hasChanges = false;

  // 检查是否有新增的类目数据或数据变化
  categoryDataMap.value.forEach((categoryData, categoryId) => {
    // 检查初始状态中是否有这个类目的数据
    const initialData = initialCategoryDataMap.value.get(categoryId);

    // 比较当前数据和初始数据
    if (!initialData || !initialData.dataList) {
      // 初始状态没有数据
      if (categoryData.dataList && categoryData.dataList.length > 0) {
        // 现在有数据，说明有新增
        const hasSelectedItems = categoryData.dataList.some(
          (typeData) => typeData.list && typeData.list.length > 0
        );
        if (hasSelectedItems) {
          hasChanges = true;
          console.log('📋 发现新增的类目修改:', categoryId);
        }
      }
    } else {
      // 初始状态有数据，比较数据是否有变化
      const isDataChanged = compareDataLists(
        categoryData.dataList,
        initialData.dataList
      );
      if (isDataChanged) {
        hasChanges = true;
        console.log('📋 发现类目数据变化:', {
          categoryId,
          current: categoryData.dataList,
          initial: initialData.dataList,
        });
      }
    }
  });

  // 检查是否有类目被删除（初始有数据，现在没有数据或数据为空）
  initialCategoryDataMap.value.forEach((initialData, categoryId) => {
    if (initialData.dataList && initialData.dataList.length > 0) {
      const currentData = categoryDataMap.value.get(categoryId);
      if (
        !currentData ||
        !currentData.dataList ||
        currentData.dataList.length === 0
      ) {
        hasChanges = true;
        console.log('📋 发现类目数据被删除:', categoryId);
      }
    }
  });

  console.log('📋 是否有未保存的修改:', hasChanges);
  return hasChanges;
};

// 新增：深度比较两个数据列表是否相同
const compareDataLists = (currentList, initialList) => {
  if (currentList.length !== initialList.length) {
    return true; // 长度不同，有变化
  }

  // 按typeId排序后比较
  const sortedCurrent = [...currentList].sort((a, b) =>
    a.typeId.localeCompare(b.typeId)
  );
  const sortedInitial = [...initialList].sort((a, b) =>
    a.typeId.localeCompare(b.typeId)
  );

  for (let i = 0; i < sortedCurrent.length; i++) {
    const currentType = sortedCurrent[i];
    const initialType = sortedInitial[i];

    if (currentType.typeId !== initialType.typeId) {
      return true; // typeId不同
    }

    if (currentType.list.length !== initialType.list.length) {
      return true; // 列表长度不同
    }

    // 比较每个属性项
    const sortedCurrentList = [...currentType.list].sort((a, b) =>
      a.attributeId.localeCompare(b.attributeId)
    );
    const sortedInitialList = [...initialType.list].sort((a, b) =>
      a.attributeId.localeCompare(b.attributeId)
    );

    for (let j = 0; j < sortedCurrentList.length; j++) {
      const currentItem = sortedCurrentList[j];
      const initialItem = sortedInitialList[j];

      if (
        currentItem.attributeId !== initialItem.attributeId ||
        currentItem.level !== initialItem.level
      ) {
        return true; // 属性或级别不同
      }
    }
  }

  return false; // 完全相同，没有变化
};

// 比较原数据和当前数据，检查是否有原本选中的项目被修改（取消选中）
const _compareSelectedItems = () => {
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

        console.log('compareSelectedItems 比较:', {
          typeId: currentItem.typeId,
          original: orangeItem.selectedIds,
          current: currentSelectedIds,
          isFiltering: !!(
            currentItem.filterCategoryName && currentItem.oldSelectedIds
          ),
        });

        // 检查原本选中的项目是否有任何一个不在当前数据的选中列表中
        for (let originalSelectedId of orangeItem.selectedIds) {
          if (!currentSelectedIds.includes(originalSelectedId)) {
            // 发现有原数据中选中的项目不在新数据中，立即返回true
            console.log('发现被取消的选中项:', originalSelectedId);
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
          console.log('发现原本无选中但现在有选中的项目');
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
  return name.replaceAll('|', '-');
};

const isObject = (value) => {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
};

const queryByCategoryId = async (node) => {
  // 如果切换了类目，先保存当前类目的数据
  if (
    currentEditingCategoryId.value &&
    currentEditingCategoryId.value !== (node.id || node)
  ) {
    console.log(
      '💾 切换类目前，保存当前类目',
      currentEditingCategoryId.value,
      '的数据'
    );
    saveCurrentCategoryData();
  }

  if (!isObject(node)) {
    model.selProductCategoryId = node;
  } else {
    model.selProductCategoryId = node.id;
  }
  if (node.parentId != 0) {
    isShowLevel.value = true;
  } else {
    isShowLevel.value = false;
  }
  // 更新当前编辑的类目ID
  currentEditingCategoryId.value = node.id || node;

  console.log('🔄 切换后的节点信息:', {
    categoryId: currentEditingCategoryId.value,
    categoryDataMapKeys: Array.from(categoryDataMap.value.keys()),
    initialCategoryDataMapKeys: Array.from(initialCategoryDataMap.value.keys()),
  });

  // 每次切换类目都调用接口
  console.log('🔍 切换类目', currentEditingCategoryId.value, '，调用接口获取数据');
  const res = await defHttp.get({
    url: '/kol/kolCategoryRelation/queryByCategoryId',
    params: { categoryId: currentEditingCategoryId.value },
  });
  if (res) {
    console.log('📡 接口返回结果:', {
      resultLength: res.length,
      result: res,
    });

    handleCategoryList(res);
    ishandleOk.value = true;
  }
};

// 获取过滤后的数据，不修改原数据
// 筛选时只显示匹配条件的数据，但保留原有的选中状态不变
const getFilteredData = (item) => {
  if (!item.filterCategoryName) {
    return item.data;
  }

  console.log('getFilteredData 筛选过程:', {
    filterText: item.filterCategoryName,
    totalData: item.data.length,
    sampleData: item.data
      .slice(0, 3)
      .map((d) => ({ id: d.id, value: d.value, label: d.label })),
  });

  // 显示匹配筛选条件的选项
  const filteredResult = item.data.filter((option) =>
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
  console.log('🟡 开始同步当前选择到全局状态');

  // 获取当前筛选的数据
  const currentFilteredData = getFilteredData(item);
      console.log(item.oldSelectedIds);

      // 如果当前筛选无结果，跳过同步避免清空全局状态
      if (currentFilteredData.length === 0) {
        console.log("🟡 当前筛选无结果，跳过同步以保护全局状态");
        return;
      }
      console.log(item.oldSelectedIds);

      // 如果oldSelectedIds未初始化，使用originalSelectedIds初始化
      if (!item.oldSelectedIds) {
        console.log("🟡 oldSelectedIds未初始化，使用originalSelectedIds初始化");
        item.oldSelectedIds = [...(item.originalSelectedIds || [])];
      }

      // 如果oldLevelMap未初始化，初始化为空对象
      if (!item.oldLevelMap) {
        item.oldLevelMap = {};
      }

      console.log(
        "🟡 当前筛选数据:",
        currentFilteredData.map((d) => d.label)
      );
      console.log("🟡 当前selectedIds:", item.selectedIds);
      console.log("🟡 同步前oldSelectedIds:", item.oldSelectedIds);
      console.log("🟡 同步前oldLevelMap:", item.oldLevelMap);

      // 遍历当前筛选的数据，同步选中状态和level状态到全局
      currentFilteredData.forEach((subItem) => {
        const isCurrentlySelected = item.selectedIds.includes(subItem.id);
        const wasGloballySelected = item.oldSelectedIds.includes(subItem.id);

        if (isCurrentlySelected && !wasGloballySelected) {
          // 新选中的项目，添加到全局状态
          item.oldSelectedIds.push(subItem.id);
          console.log("🟢 新选中项目添加到全局:", subItem.label);
        } else if (!isCurrentlySelected && wasGloballySelected) {
          // 取消选中的项目，从全局状态移除
          const index = item.oldSelectedIds.indexOf(subItem.id);
          if (index > -1) {
            item.oldSelectedIds.splice(index, 1);
            console.log("🔴 取消选中项目从全局移除:", subItem.label);
          }
        }

        // 同步level状态：无论是否选中，都要保存当前的level状态
        if (subItem.level !== undefined) {
          item.oldLevelMap[subItem.value] = subItem.level;
          console.log("🟡 保存level状态:", subItem.label, "level:", subItem.level);
        } else {
          // 如果当前level为undefined，需要从oldLevelMap中删除
          if (item.oldLevelMap[subItem.value] !== undefined) {
            delete item.oldLevelMap[subItem.value];
            console.log("🟡 清除之前的level状态:", subItem.label);
          }
        }
      });

  console.log('🟡 同步后oldSelectedIds:', item.oldSelectedIds);
  console.log('🟡 同步后oldLevelMap:', item.oldLevelMap);
};

// 根据当前筛选条件更新显示的选中状态
const updateSelectedIdsForCurrentFilter = (item) => {
  if (!item.oldSelectedIds) return;

  // 获取当前筛选显示的数据
  const filteredData = getFilteredData(item);

      // 调试：检查数据结构
      console.log("🔧 updateSelectedIdsForCurrentFilter 开始:", {
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
          console.log("🟢 恢复选中项:", oldId, "- 在当前筛选结果中找到");

          // 同时恢复level状态
          if (item.oldLevelMap && item.oldLevelMap[oldId] !== undefined) {
            matchedOption.level = item.oldLevelMap[oldId];
            console.log(
              "🟢 恢复level状态:",
              matchedOption.label,
              "level:",
              matchedOption.level
            );
          }
        } else {
          // 即使不在当前筛选结果中，也要保持选中状态
          newSelectedIds.push(oldId);
          console.log("🔸 保持选中项:", oldId, "- 虽不在当前筛选结果中但保持选中状态");
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
          console.log(
            "🟡 恢复未选中项的level状态:",
            option.label,
            "level:",
            option.level
          );
        }
      });

      console.log("🔧 最终恢复的选中状态:", newSelectedIds);

      // 如果没有恢复任何项目，但oldSelectedIds中有数据，进行详细诊断
      if (newSelectedIds.length === 0 && item.oldSelectedIds.length > 0) {
        console.log("⚠️ 恢复失败诊断:");
        console.log(
          "当前筛选结果:",
          filteredData.map((d) => ({
            id: d.id,
            value: d.value,
            label: d.label,
          }))
        );

        item.oldSelectedIds.forEach((oldId) => {
          const matchInFiltered = filteredData.find(
            (d) => d.value === oldId || d.id === oldId
          );
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
      console.log('🔧 取消全选，清除选项level:', option.label);
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

    // 为所有新选中的选项设置默认level为'1'（只处理非disabled的选项）
    enabledData.forEach((option) => {
      if (!option.level) {
        option.level = '1';
        console.log('🔧 全选，为选项设置默认level:', option.label, 'level: 1');
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

  // 用户操作后，保存当前类目数据
  if (currentEditingCategoryId.value) {
    console.log('💾 全选操作后保存当前类目数据');
    saveCurrentCategoryData();
  }
};

// 单选
const checkboxChange = (item) => {
  console.log('🔵 选中状态变化:', {
    selectedIds: [...item.selectedIds],
    typeId: item.typeId,
  });

  // 先将所有未选中的选项的level设为undefined
  item.data.forEach((option) => {
    if (!item.selectedIds.includes(option.value)) {
      if (option.level !== undefined) {
        option.level = undefined;
        console.log('🔧 清除未选中选项的level:', option.label);
      }
    }
  });

  // 为新选中的选项设置默认level为'1'（只处理非disabled的选项）
  item.selectedIds.forEach((id) => {
    const option = item.data.find((option) => option.value == id);

    if (option && !option.level && !option.disabled) {
      option.level = '1';
      console.log('🔧 为新选中选项设置默认level:', option.label);
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

  // 用户操作后，保存当前类目数据
  if (currentEditingCategoryId.value) {
    saveCurrentCategoryData();
  }
};
const handleLevelClick = (item, option, level, event) => {
  // 判断选中selectedIds中是否有点击的选项，有则可以设置值无则不设置
  // 同时检查选项是否被禁用
  if (item.selectedIds.includes(option.value) && !option.disabled) {
    option.level = level;
    console.log('🔧 设置level:', option.label, '->', level);

    // 如果在筛选状态下，需要同步level状态到全局
    if (item.filterCategoryName && item.oldLevelMap) {
      item.oldLevelMap[option.value] = level;
    }
  } else {
    // 如果选项未被选中或被禁用，清除其level状态
    if (option.level !== undefined) {
      option.level = undefined;
      console.log(
        '🔧 清除level:',
        option.label,
        '原因:',
        !item.selectedIds.includes(option.value) ? '未选中' : '被禁用'
      );
    }

    // 如果在筛选状态下，也要同步到全局
    if (item.filterCategoryName && item.oldLevelMap) {
      delete item.oldLevelMap[option.value];
    }
  }

  // 无论是否在筛选状态，都进行一次全面的level状态同步
  syncAllLevelStates(item);

  // 用户操作后，保存当前类目数据
  if (currentEditingCategoryId.value) {
    saveCurrentCategoryData();
  }

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

 const edit = async (record) => {
  Object.assign(model, record);
  visible.value = true;
  isEdit.value = true;
  confirmLoading.value = true;
  // 清空之前的数据
  categoryDataMap.value.clear();
  initialCategoryDataMap.value.clear();
  currentEditingCategoryId.value = null;

  await initDict();
  await initData();
  confirmLoading.value = false;
};
const initData = async () => {
  const typeRes = await defHttp.get({ url: '/kol/kolAttributeType/listAll' });
  const res = await defHttp.get({ url: '/kol/attribute/getKolAttribute' });
  const [resResult, typeResResult] = await Promise.all([res, typeRes]);
  if (resResult && typeResResult) {
    // 按照 typeRes.result 的顺序创建 categoryList
    if (!model.selProductCategoryId) {
      await initProductCategory();
    }
    categoryList.value = typeResResult
      .filter((item) => item.type == 2)
      .map((typeItem) => {
        // 查找是否在 res.result 中存在对应的数据
        const existingItem = resResult.find((item) => item.typeId === typeItem.id);

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

    handleCategoryList(model.dataList || []);
  }
};

const _initCategory = async () => {
  const res = await defHttp.get({ url: '/kol/category/getKolCategory' });
  if (res?.success) {
    // 可以在这里处理返回的数据
  }
};
// 处理数据
const handleCategoryList = (queryByCategoryList = []) => {
  console.log('🔧 开始处理类目数据:', {
    queryByCategoryListLength: queryByCategoryList.length,
    categoryDataMapKeys: Array.from(categoryDataMap.value.keys()),
    initialCategoryDataMapKeys: Array.from(initialCategoryDataMap.value.keys()),
  });

  if (queryByCategoryList.length == 0) {
    categoryList.value.forEach((item) => {
          item.selectedIds = [];
          item.checkAll = false;
          item.filterCategoryName = "";
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
          item.filterCategoryName = "";
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
                // 接口返回的数据中有这个选项
                subItem.level = matchedQuery.level;
                subItem.disabled = false;
              } else {
              // 接口返回的数据中没有这个选项，根据父级类目判断是否禁用
              const parentId = isHasChild(currentEditingCategoryId.value);
              if (parentId === '0') {
                // 顶级类目，不禁用
                subItem.disabled = false;
              } else {
                // 非顶级类目，检查父级是否有修改缓存
                const parentSelection = checkParentCategorySelections(
                  subItem.id,
                  matchedCategory.typeId
                );

                  if (parentSelection === true) {
                    // 父级已选中，当前类目未选中，设置为false（可操作）
                    subItem.disabled = false;
                  } else if (parentSelection === false) {
                    // 父级未选中，当前类目也未选中，设置为true（禁用）
                    subItem.disabled = true;
                  } else {
                    // 父级无数据，无法确定，默认禁用
                    subItem.disabled = true;
                  }
                }
              }
            }
          } else {
          }
        });

    // 处理没有在queryByCategoryList中找到的categoryList数据
    categoryList.value.forEach((item) => {
      const hasMatch = queryByCategoryList.some((q) => q.typeId == item.typeId);
      if (!hasMatch) {
        // 根据父级类目判断是否需要禁用
        const parentId = isHasChild(currentEditingCategoryId.value);
        item.data.forEach((subItem) => {
          if (parentId === '0') {
            // 顶级类目，不禁用
            subItem.disabled = false;
          } else {
            // 非顶级类目，检查父级是否有修改缓存
            const parentSelection = checkParentCategorySelections(
              subItem.id,
              item.typeId
            );

                if (parentSelection === true) {
                  // 父级已选中，当前类目未选中，设置为false（可操作）
                  subItem.disabled = false;
                } else if (parentSelection === false) {
                  // 父级未选中，当前类目也未选中，设置为true（禁用）
                  subItem.disabled = true;
                } else {
                  // 父级无数据，无法确定，默认禁用
                  subItem.disabled = true;
                }
              }
            });
          }
        });
      }

  // 所有数据处理完成后需要处理全选的状态
  categoryList.value.forEach((item) => {
    // 重新计算全选状态，因为我们可能已经修改了selectedIds
    const enabledData = item.data.filter((subItem) => !subItem.disabled);

    if (enabledData.length > 0) {
      // 计算在可用选项中实际被选中的数量
      const selectedInEnabled = item.selectedIds.filter((id) =>
        enabledData.some((option) => option.id === id || option.value === id)
      );

      if (selectedInEnabled.length === 0) {
        item.checkAll = false;
        item.indeterminate = false;
      } else if (selectedInEnabled.length === enabledData.length) {
        item.checkAll = true;
        item.indeterminate = false;
      } else {
        item.checkAll = false;
        item.indeterminate = true;
      }

      console.log('🔄 重新计算全选状态:', {
        typeId: item.typeId,
        selectedCount: selectedInEnabled.length,
        totalEnabled: enabledData.length,
      });
    } else {
      item.checkAll = false;
      item.indeterminate = false;
    }
  });
  sortData();

  console.log(categoryList.value);
  // 深拷贝categoryList，确保orangeCategoryList不会被后续操作影响
  orangeCategoryList.value = JSON.parse(JSON.stringify(categoryList.value));

  // 数据初始化完成后，确保所有选项的状态都正确
  categoryList.value.forEach((item) => {
    if (item.oldLevelMap) {
      // 如果有保存的level状态，恢复所有选项的level状态
      restoreLevelStates(item);
    }
  });

  // 重要：在接口数据处理完成后，恢复用户之前的选择
  if (currentEditingCategoryId.value) {
    restoreCategoryData(currentEditingCategoryId.value);
  }

  // 确保所有类目的level状态与选中状态同步
  categoryList.value.forEach((item) => {
    syncLevelWithSelection(item);
  });

  // 记录初始状态，用于检查是否有未保存的修改
  if (
    currentEditingCategoryId.value &&
    !initialCategoryDataMap.value.has(currentEditingCategoryId.value)
  ) {
    // 只在第一次加载时记录初始状态
    const initialData = {
      categoryId: currentEditingCategoryId.value,
      categoryName: '当前类目',
      categoryNamePath: '当前类目路径',
      dataList: [],
    };

    // 记录接口返回的选中状态作为初始状态
    queryByCategoryList.forEach((q) => {
      const selectedItems = q.list.filter((l) => l.isSel);
      if (selectedItems.length > 0) {
        initialData.dataList.push({
          typeId: q.typeId,
          list: selectedItems.map((item) => ({
            attributeId: item.attributeId,
            attributeName: item.attributeName,
            level: item.level,
          })),
        });
      }
    });

    // 只有当有实际的初始选中数据时才记录
    if (initialData.dataList.length > 0) {
      initialCategoryDataMap.value.set(currentEditingCategoryId.value, initialData);
      console.log('📋 记录初始状态:', currentEditingCategoryId.value, initialData);
    } else {
      console.log(
        '📋 类目无初始选中数据，不记录初始状态:',
        currentEditingCategoryId.value
      );
    }
  }
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

const initDict = async () => {
  // 这个方法可能不再需要，因为 associationLevelList 可能不再使用
  // 如果需要，可以使用 defHttp 调用字典接口
};

const initProductCategory = async () => {
  const res = await defHttp.get({ url: '/kol/category/loadTreeDataAll' });
  if (res) {
    productCategoryList.value = res;
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
  Object.keys(model).forEach((key) => delete model[key]);
  isShowLevel.value = false;
  visible.value = false;

  // 清空数据
  categoryDataMap.value.clear();
  initialCategoryDataMap.value.clear();
  currentEditingCategoryId.value = null;
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

  // 防抖处理 - 清除之前的定时器
  if (submitTimer) {
    clearTimeout(submitTimer);
  }

  // 设置防抖延迟
  submitTimer = setTimeout(() => {
    executeSubmit();
  }, 300);
};

const executeSubmit = () => {
  // 再次检查loading状态
  if (confirmLoading.value) {
    return;
  }

  // 在提交前，先同步所有状态，确保数据完整性
  console.log('📤 提交前同步所有状态');
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

  // 获取所有类目的数据
  const allCategoryData = getAllCategoryData();

  console.log('📤 准备提交的所有类目数据:', allCategoryData);

  // 批量提交所有类目的数据
  const submitPromises = allCategoryData.map((categoryData) => {
    return {
      categoryId: categoryData.categoryId,
      categoryName: categoryData.categoryName,
      categoryNamePath: categoryData.categoryNamePath,
      dataList: categoryData.dataList,
    };
  });
  console.log(submitPromises);

  if (submitPromises.length === 0) {
    createMessage.error('请选择产品类目');
    confirmLoading.value = false;
    return;
  }
  defHttp
    .post({ url: '/kol/kolCategoryRelation/addBatch', data: submitPromises })
    .then(async (res) => {
      if (res) {
    
        // 提交成功后清理保存的数据，避免误判为有未保存修改
        categoryDataMap.value.clear();
        initialCategoryDataMap.value.clear();
        console.log(model.selProductCategoryId);
        await initData();

        await queryByCategoryId(model.selProductCategoryId);
      } else {
        createMessage.error(res.message);
      }
    })
    .finally(() => {
      confirmLoading.value = false;
    });
};

const handleCancel = (e) => {
  e?.preventDefault?.()
  // 检查是否有未保存的修改
  if (hasUnsavedChanges()) {
    createConfirm({
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
    // 没有修改，直接关闭
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
          console.log("🔧 保存可见选项level:", option.label, "level:", option.level);
        } else {
          // 如果level为undefined，从oldLevelMap中删除
          if (item.oldLevelMap[option.value] !== undefined) {
            delete item.oldLevelMap[option.value];
            console.log("🔧 清除可见选项level:", option.label);
          }
        }
      });

      // 2. 保存所有选项的level状态（包括不可见的）
      // 这确保了被筛选隐藏的选项的level状态不会丢失
      item.data.forEach((option) => {
        if (option.level !== undefined) {
          // 只有当前选项有level值时才更新，避免覆盖已保存的隐藏选项状态
          item.oldLevelMap[option.value] = option.level;
          console.log(
            "🔧 保存选项level:",
            option.label,
            "level:",
            option.level,
            "可见:",
            currentFilteredData.some((f) => f.value === option.value)
          );
        } else if (currentFilteredData.some((f) => f.value === option.value)) {
          // 如果是可见选项且level为undefined，从oldLevelMap中删除
          if (item.oldLevelMap[option.value] !== undefined) {
            delete item.oldLevelMap[option.value];
            console.log("🔧 清除选项level:", option.label);
          }
        }
      });

  console.log('🔧 全面同步后的oldLevelMap:', item.oldLevelMap);
};

// 恢复level状态的方法
const restoreLevelStates = (item) => {
  console.log('🔧 开始恢复level状态');

  if (!item.oldLevelMap) {
    console.log('🔧 没有保存的level状态，跳过恢复');
    return;
  }

  // 恢复所有选项的level状态（包括不可见的）
  item.data.forEach((option) => {
    if (item.oldLevelMap[option.value] !== undefined) {
      option.level = item.oldLevelMap[option.value];
      console.log('🔧 恢复选项level:', option.label, 'level:', option.level);
    }
  });

  console.log('🔧 level状态恢复完成');
};

// 保护不可见选项的level状态
const protectHiddenLevelStates = (item) => {
  console.log('🛡️ 开始保护不可见选项的level状态');

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
          console.log(
            "🛡️ 恢复不可见选项的level状态:",
            option.label,
            "level:",
            option.level
          );
        }
      });

  console.log('🛡️ 不可见选项level状态保护完成');
};

// 确保level状态与选中状态同步
const syncLevelWithSelection = (item) => {
  console.log('🔧 开始同步level状态与选中状态');

  item.data.forEach((option) => {
    const isSelected = item.selectedIds.includes(option.id);

    if (isSelected) {
      // 选中的选项必须有level值
      if (!option.level) {
        option.level = '1'; // 设置默认level
        console.log('🔧 为选中选项设置默认level:', option.label, 'level: 1');
      }
    } else {
      // 未选中的选项不能有level值
      if (option.level !== undefined) {
        option.level = undefined;
        console.log('🔧 清除未选中选项的level:', option.label);
      }
    }
  });

  console.log('🔧 level状态与选中状态同步完成');
};

// 通用的状态恢复方法，确保所有状态都正确
const restoreAllStates = (item) => {
  console.log('🔄 开始恢复所有状态');

  // 1. 恢复所有选项的level状态
  restoreLevelStates(item);

  // 2. 保护不可见选项的level状态
  protectHiddenLevelStates(item);

  // 3. 如果在筛选状态下，恢复选中状态
  if (item.filterCategoryName && item.oldSelectedIds) {
    updateSelectedIdsForCurrentFilter(item);
  }

  // 4. 确保level状态与选中状态同步
  syncLevelWithSelection(item);

  // 5. 更新全选状态
  updateCheckAllStatus(item);

  console.log('🔄 所有状态恢复完成');
};

// 暴露方法供外部调用
defineExpose({
  add,
  edit,
  open: edit,
});
</script>
<style>
.match-category-association-modal {
  width: fit-content !important;
}
</style>
<style lang="less" scoped>

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
    width: 340px;
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
:deep(.ant-checkbox-group) {
  width: 100% !important;
  display: block !important;
}
:deep(.ant-checkbox-group .ant-checkbox-wrapper) {
  display: flex !important;
  align-items: center;
  width: 100% !important;
}
:deep(.ant-checkbox + span) {
  width: 100% !important;
}
:deep(.ant-checkbox-wrapper + .ant-checkbox-wrapper) {
  margin-left: 0 !important;
}
.category-item-content-checkbox :deep(.ant-checkbox-wrapper) span:nth-child(2) {
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
:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background-color: #f0f3fe;
  color: @primary-color;
}
:deep(.ant-checkbox-group label) {
  margin-bottom: 14px;
}
a {
  color: @primary-color;
}
</style>
