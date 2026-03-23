<template>
  <a-modal
    class="production-information-modal"
    :maskClosable="false"
    :title="title"
    v-model:visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <div style="display: flex; gap: 20px">
        <div style="width: 440px">
          <a-form
            ref="formRef"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 20 }"
            :model="model"
            :rules="validatorRules"
          >
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="产品图片">
                  <a-avatar
                    shape="square"
                    :size="64"
                    :icon="h(PictureOutlined)"
                    :src="model.productImage"
                  />
                  <!-- <div style="display: flex;gap: 10px;">
                </div> -->
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item prop="brandId" label="品牌">
                  <a-select
                    showSearch
                    style="width: calc(100% - 110px)"
                    option-filter-prop="label"

                    v-model:value="model.brandId"
                    placeholder="品牌"
                  >
                    <a-select-option
                      v-for="item in brandList"
                      :key="item.id"
                      :value="item.id"
                      :label="item.brandName"
                    >
                      {{ item.brandName }}
                    </a-select-option>
                  </a-select>
                  <a-button
                    style="margin-left: 10px"
                    ghost
                    type="primary"
                    :icon="h(PlusOutlined)"
                    @click="addBrand"
                    >新增品牌</a-button
                  >
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item prop="productName" label="产品名称">
                  <a-input v-model:value.trim="model.productName" placeholder="产品名称" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item prop="productCategoryId" label="产品类目">
                  <a-tree-select
                    labelInValue
                    showSearch
                    treeDefaultExpandAll
                    :replaceFields="replaceFields"
                    @change="queryByCategoryId"
                    @select="handleSelect"
                    v-model:value="model.productCategoryId"
                    :filterTreeNode="filterTreeNode"
                    :tree-data="productCategoryList"
                    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                    placeholder="产品类目"
                  >
                  </a-tree-select>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item prop="productImage" label="图片链接">
                  <a-input v-model:value.trim="model.productImage" placeholder="图片链接" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item prop="productUrl" label="产品链接">
                  <a-input v-model:value="model.productUrl" placeholder="产品链接" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="24">
                <a-button
                  :block="true"
                  :icon="isShow ? h(MenuFoldOutlined) : h(MenuUnfoldOutlined)"
                  @click="isShow = !isShow"
                  >{{ isShow ? "收起" : "展开" }}</a-button
                >
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div v-if="isShow" class="related-categories">
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
              <div>
                <div class="category-item-content-checkbox">
                  <a-checkbox
                    :indeterminate="item.indeterminate"
                    :disabled="!model.productCategoryId || !model.productCategoryId.value"
                    :checked="item.checkAll"
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
                    <a-row>
                    
                      <a-col
                        :span="12"
                        v-for="option in getFilteredData(item)"
                        :key="option.value"
                      >
                        <a-checkbox
                          :disabled="
                            !model.productCategoryId || !model.productCategoryId.value
                          "
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
              </div>
            </div>
          </div>
        </div>
      </div>
      <AddBrandModal ref="AddBrandModalRef" @ok="addBrandOk"></AddBrandModal>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, reactive, onBeforeUnmount, h } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { PictureOutlined, PlusOutlined, MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons-vue';
import CategoryEllipsisTooltip from '@/views/basiSettings/CategoryAssociation/modules/CategoryEllipsisTooltip.vue';
import AddBrandModal from '@/views/basiSettings/brand/modules/brandModal.vue';

const emit = defineEmits(['ok', 'update']);
const { createMessage } = useMessage();

const title = ref('操作');
const visible = ref(false);
const model = ref({
  productCategoryId: {
    value: '',
    label: '',
  },
});
const isShow = ref(false);
const isEdit = ref(false);
const confirmLoading = ref(false);
const categoryList = ref([]);
const productCategoryList = ref([]);
const orangeCategoryList = ref([]);
const selProductCategoryName = ref('');
const selectedNode = ref(null);
const ishandleOk = ref(true);
const isShowLevel = ref(false);
const brandList = ref([]);
const validatorRules = {
  brandId: [{ required: true, message: '请选择品牌!', trigger: 'change' }],
  productName: [
    { required: true, message: '请输入产品名称!', trigger: 'change' },
    { max: 20, message: '产品名称不能超过20个字符!', trigger: 'change' },
  ],
  productModel: [
    { required: false, message: '请输入产品型号!', trigger: 'change' },
    { max: 20, message: '产品型号不能超过20个字符!', trigger: 'change' },
  ],
  productUrl: [{ required: true, message: '请输入产品链接!', trigger: 'change' }],
  productCategoryId: [
    { required: true, message: '请选择类目!', trigger: 'change' },
  ],
  productImage: [{ required: true, message: '请输入图片链接!', trigger: 'change' }],
};
const replaceFields = {
  children: 'children',
  label: 'categoryName',
  value: 'id',
};
const editCategoryId = ref('');
const formRef = ref(null);
const AddBrandModalRef = ref(null);

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
});
const addBrandOk = () => {
  initBrand();
  emit('update');
};

const addBrand = () => {
  AddBrandModalRef.value?.open();
  AddBrandModalRef.value.title = '新增品牌';
};

const _filter = (inputValue, path) => {
  return path.some(
    (option) => option.label.toLowerCase().indexOf(inputValue.toLowerCase()) > -1
  );
};

const _handleChange = (info) => {
  if (info.file.status === 'done') {
    if (info.file.response?.success) {
      model.value.productImage = window._CONFIG['image'] + info.file.response.message;
      console.log(model.value.productImage);
    }
  } else if (info.file.status === 'error') {
    createMessage.error(`${info.file.name} 上传失败.`);
  }
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

const _parseCategoryName = (name) => {
  return name.replaceAll('|', '-');
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
    if (foundNode) {
      selProductCategoryName.value = foundNode.categoryNamePath.replaceAll('/', '- ');
      return foundNode;
    }
  }

  selProductCategoryName.value = '';
  return null;
};

const _isObject = (value) => {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
};

const filterTreeNode = (inputValue, treeNode) => {
  return (
    treeNode.data.props.title.toLowerCase().indexOf(inputValue.toLowerCase()) === 0
  );
};

const handleSelect = (_value) => {
  if (model.value.id) {
    createMessage.warning('产品关联的社媒属性将会更新为新类目的社媒属性');
  }
};

const queryByCategoryId = async (node) => {
  console.log(node);

  selectedNode.value = getSelProductCategory(node.value);
  console.log(selectedNode.value);
  if (selectedNode.value) {
    model.value.productCategoryId.label = selectedNode.value.categoryNamePath.replaceAll('|', '-');
    if (selectedNode.value.isHasChild == 0) {
      isShowLevel.value = true;
    } else {
      isShowLevel.value = false;
    }
    let res = null;
    if (model.value.id && editCategoryId.value == selectedNode.value.id) {
      console.log(model.value.dataList);
      handleCategoryList(model.value.dataList || []);
    } else {
      res = await defHttp.get({
        url: '/kol/kolCategoryRelation/queryByCategoryId',
        params: { categoryId: selectedNode.value.id },
      });
      if (res) {
        handleCategoryList(res);
      }
    }
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
        } else if (item.oldLevelMap[subItem.value] !== undefined) {
          // 如果当前没有level但之前有，保持之前的状态
          console.log(
            "🟡 保持之前的level状态:",
            subItem.label,
            "level:",
            item.oldLevelMap[subItem.value]
          );
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

  // 更新全选状态
  updateCheckAllStatus(item);

  // 确保在筛选状态下，用户的选择能被保存到全局状态
  if (item.filterCategoryName) {
    // 如果还没有初始化 oldSelectedIds，先初始化
    if (!item.oldSelectedIds) {
      console.log('🟡 首次在筛选状态下选择，初始化 oldSelectedIds');
      item.oldSelectedIds = [];
    }

    console.log('🟠 同步前 oldSelectedIds:', [...item.oldSelectedIds]);
    // 实时更新筛选前的状态记录，确保状态同步
    syncFilteredSelectionState(item);
    // 同时同步level状态
    syncAllLevelStates(item);
    console.log('🟢 同步后 oldSelectedIds:', [...item.oldSelectedIds]);
  }
};

const handleLevelClick = (item, option, level, event) => {
  // 判断选中selectedIds中是否有点击的选项，有则可以设置值无则不设置
  // 同时检查选项是否被禁用
  if (item.selectedIds.includes(option.value) && !option.disabled) {
    option.level = level;
    console.log('🔧 设置level:', option.label, 'level:', level);

    // 如果在筛选状态下，需要同步level状态到全局
    if (item.filterCategoryName && item.oldLevelMap) {
      item.oldLevelMap[option.value] = level;
      console.log('🔧 同步level到全局状态:', option.label, 'level:', level);
    }
  } else {
    option.level = undefined;
    console.log('🔧 清除level:', option.label);

    // 如果在筛选状态下，也要同步到全局
    if (item.filterCategoryName && item.oldLevelMap) {
      delete item.oldLevelMap[option.value];
      console.log('🔧 从全局状态清除level:', option.label);
    }
  }

  // 无论是否在筛选状态，都进行一次全面的level状态同步
  syncAllLevelStates(item);

  event.preventDefault();
  event.stopPropagation();
};

const merchantDemandModalAdd = (productUrl) => {
  Object.assign(model.value, {productUrl});
  visible.value = true;
  initData();
  initBrand();
};
const add = () =>{
  edit({});
}
const edit = (record) => {
  Object.assign(model.value, record);
  if (record.id) {
    Object.assign(model.value, {
      id: record.id,
      brandId: record.brandId,
      productName: record.productName,
      productUrl: record.productUrl,
      productModel:
        record.productAttributes &&
        JSON.parse(record.productAttributes).productModel
          ? JSON.parse(record.productAttributes).productModel
          : '',
      dataList: record.dataList,
      productImage: record.productImage,
    });
    model.value.productCategoryId = {
      value: record.categoryId,
      label: '',
    };
    console.log(record.categoryId);
    if (record.categoryIds) {
      model.value.productCategoryIds = record.categoryIds;
    }
    isEdit.value = true;
    editCategoryId.value = record.categoryId;
  } else {
    isEdit.value = false;
  }
  visible.value = true;
  initData();
  initBrand();
};

const initBrand = async () => {
  const res = await defHttp.get({ url: '/kolBrand/listAll' });
  if (res) {
    brandList.value = res;
  }
};

const initData = async () => {
  const typeRes = await defHttp.get({ url: '/kol/kolAttributeType/listAll' });
  const res = await defHttp.get({ url: '/kol/attribute/getKolAttribute' });
  const [resResult, typeResResult] = await Promise.all([res, typeRes]);
  if (resResult && typeResResult) {
    await initProductCategory();
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

    if (!model.value.id) {
      handleCategoryList();
    } else {
      queryByCategoryId(model.value.productCategoryId);
    }
  }
};

const _initCategory = async () => {
  const res = await defHttp.get({ url: '/kol/category/getKolCategory' });
  if (res) {
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
            if (
              (selectedNode.value?.isHasChild == 1 &&
                selectedNode.value?.parentId != '0') ||
              selectedNode.value?.isHasChild == 0
            ) {
              subItem.disabled = false;
            } else {
              subItem.disabled = false;
            }
          }
        }

        // 检查是否所有选项都被禁用
        const allDisabled = matchedCategory.data.every((subItem) => subItem.disabled);
        matchedCategory.allDisabled = allDisabled;
      }
    });

    // 处理没有在queryByCategoryList中找到的categoryList数据
    categoryList.value.forEach((item) => {
      const hasMatch = queryByCategoryList.some((q) => q.typeId == item.typeId);
      if (!hasMatch) {
        // 根据isOnLevelFn判断是否需要禁用
        item.data.forEach((subItem) => {
          if (
            (selectedNode.value?.isHasChild == 1 &&
              selectedNode.value?.parentId != '0') ||
            selectedNode.value?.isHasChild == 0
          ) {
            subItem.disabled = false;
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
  // 深拷贝categoryList，确保orangeCategoryList不会被后续操作影响
  orangeCategoryList.value = JSON.parse(JSON.stringify(categoryList.value));

  // 数据初始化完成后，确保所有选项的状态都正确
  categoryList.value.forEach((item) => {
    if (item.oldLevelMap) {
      // 如果有保存的level状态，恢复所有选项的level状态
      restoreLevelStates(item);
    }
  });
};

const sortData = () => {
  categoryList.value.forEach((item) => {
    item.data.sort((a, b) => {
      const aSelected = item.selectedIds.includes(a.id);
      const bSelected = item.selectedIds.includes(b.id);
      if (aSelected && !bSelected) return -1;
      if (!aSelected && bSelected) return 1;
      return 0;
    });
  });
};

const _initDict = async () => {
  // 未使用的方法，保留但不调用
};

const initProductCategory = async () => {
  const res = await defHttp.get({ url: '/kol/category/loadTreeDataAll' });
  if (res) {
        // this.productCategoryList1 = [
        //   {
        //     id: "1955926277530480642",
        //     parentId: "0",
        //     categoryCode: "B16",
        //     categoryName: "456",
        //     sortCode: 0,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: "admin",
        //     createTime: "2025-08-14 17:35:39",
        //     updateBy: null,
        //     updateTime: null,
        //     categoryCodePath: "B16",
        //     categoryNamePath: "456",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "1952969856669179905",
        //     parentId: "0",
        //     categoryCode: "B12",
        //     categoryName: "测试产品类目",
        //     sortCode: 0,
        //     isDelete: 0,
        //     isHasChild: 1,
        //     remark: "1111",
        //     createBy: "admin",
        //     createTime: "2025-08-06 13:47:53",
        //     updateBy: "admin",
        //     updateTime: "2025-08-14 11:11:19",
        //     categoryCodePath: "B12",
        //     categoryNamePath: "测试产品类目",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [
        //       {
        //         id: "1952969927263510529",
        //         parentId: "1952969856669179905",
        //         categoryCode: "",
        //         categoryName: "测试类目A",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 1,
        //         remark: "2222",
        //         createBy: "admin",
        //         createTime: "2025-08-06 13:48:10",
        //         updateBy: "admin",
        //         updateTime: "2025-08-21 11:41:29",
        //         categoryCodePath: "B12|null",
        //         categoryNamePath: "测试产品类目|测试类目A",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [
        //           {
        //             id: "1958373865718521857",
        //             parentId: "1952969927263510529",
        //             categoryCode: null,
        //             categoryName: "cs1",
        //             sortCode: 1,
        //             isDelete: 0,
        //             isHasChild: 1,
        //             remark: null,
        //             createBy: "admin",
        //             createTime: "2025-08-21 11:41:29",
        //             updateBy: "admin",
        //             updateTime: "2025-08-21 11:41:38",
        //             categoryCodePath: "B12|null|null",
        //             categoryNamePath: "测试产品类目|测试类目A|cs1",
        //             categoryTypeId: "0",
        //             categoryTypeName: "0",
        //             children: [
        //               {
        //                 id: "1958373904540999681",
        //                 parentId: "1958373865718521857",
        //                 categoryCode: null,
        //                 categoryName: "cs2",
        //                 sortCode: 1,
        //                 isDelete: 0,
        //                 isHasChild: 0,
        //                 remark: null,
        //                 createBy: "admin",
        //                 createTime: "2025-08-21 11:41:38",
        //                 updateBy: null,
        //                 updateTime: null,
        //                 categoryCodePath: "B12|null|null|null",
        //                 categoryNamePath: "测试产品类目|测试类目A|cs1|cs2",
        //                 categoryTypeId: "0",
        //                 categoryTypeName: "0",
        //                 children: [],
        //                 size: 0,
        //                 personalTags: [],
        //                 attributeCode: null,
        //                 attributeName: null,
        //                 attributeEnName: null,
        //                 attributeCodePath: null,
        //                 attributeNamePath: null,
        //                 attributeEnNamePath: null,
        //                 attributeTypeId: null,
        //                 attributeTypeName: null,
        //               },
        //             ],
        //             size: 0,
        //             personalTags: [],
        //             attributeCode: null,
        //             attributeName: null,
        //             attributeEnName: null,
        //             attributeCodePath: null,
        //             attributeNamePath: null,
        //             attributeEnNamePath: null,
        //             attributeTypeId: null,
        //             attributeTypeName: null,
        //           },
        //         ],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "1954703144702099458",
        //         parentId: "1952969856669179905",
        //         categoryCode: null,
        //         categoryName: "测试类目B",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: "admin",
        //         createTime: "2025-08-11 08:35:21",
        //         updateBy: null,
        //         updateTime: null,
        //         categoryCodePath: "B12|null",
        //         categoryNamePath: "测试产品类目|测试类目B",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //     ],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "1956646662525911041",
        //     parentId: "0",
        //     categoryCode: "B18",
        //     categoryName: "测试类目0816-1",
        //     sortCode: 0,
        //     isDelete: 0,
        //     isHasChild: 1,
        //     remark: null,
        //     createBy: "admin",
        //     createTime: "2025-08-16 17:18:12",
        //     updateBy: "admin",
        //     updateTime: "2025-08-16 17:18:38",
        //     categoryCodePath: "B18",
        //     categoryNamePath: "测试类目0816-1",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [
        //       {
        //         id: "1956646954516578306",
        //         parentId: "1956646662525911041",
        //         categoryCode: null,
        //         categoryName: "测试类目0816-1子级2",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: "admin",
        //         createTime: "2025-08-16 17:19:21",
        //         updateBy: null,
        //         updateTime: null,
        //         categoryCodePath: "B18|null",
        //         categoryNamePath: "测试类目0816-1|测试类目0816-1子级2",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "1956646773599469569",
        //         parentId: "1956646662525911041",
        //         categoryCode: null,
        //         categoryName: "测试类目0816-1子级",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: "admin",
        //         createTime: "2025-08-16 17:18:38",
        //         updateBy: null,
        //         updateTime: null,
        //         categoryCodePath: "B18|null",
        //         categoryNamePath: "测试类目0816-1|测试类目0816-1子级",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //     ],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "91",
        //     parentId: "0",
        //     categoryCode: "10000000000",
        //     categoryName: "教育",
        //     sortCode: 1,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "10000000000",
        //     categoryNamePath: "教育",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "92",
        //     parentId: "0",
        //     categoryCode: "11000000000",
        //     categoryName: "车辆与运输",
        //     sortCode: 2,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "11000000000",
        //     categoryNamePath: "车辆与运输",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "1956241368457048065",
        //     parentId: "0",
        //     categoryCode: "B16",
        //     categoryName: "g测试产品类目1级",
        //     sortCode: 3,
        //     isDelete: 0,
        //     isHasChild: 1,
        //     remark:
        //       "fdasasssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssg灌灌灌灌灌",
        //     createBy: "admin",
        //     createTime: "2025-08-15 14:27:42",
        //     updateBy: "admin",
        //     updateTime: "2025-08-15 14:28:16",
        //     categoryCodePath: "B16",
        //     categoryNamePath: "g测试产品类目1级",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [
        //       {
        //         id: "1956241602373382145",
        //         parentId: "1956241368457048065",
        //         categoryCode: null,
        //         categoryName: "g测试产品类目2.2",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: "admin",
        //         createTime: "2025-08-15 14:28:38",
        //         updateBy: null,
        //         updateTime: null,
        //         categoryCodePath: "B16|null",
        //         categoryNamePath: "g测试产品类目1级|g测试产品类目2.2",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "1956241508802654209",
        //         parentId: "1956241368457048065",
        //         categoryCode: null,
        //         categoryName: "g测试产品类目2级",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: "admin",
        //         createTime: "2025-08-15 14:28:16",
        //         updateBy: null,
        //         updateTime: null,
        //         categoryCodePath: "B16|null",
        //         categoryNamePath: "g测试产品类目1级|g测试产品类目2级",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //     ],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "93",
        //     parentId: "0",
        //     categoryCode: "12000000000",
        //     categoryName: "母婴和儿童用品",
        //     sortCode: 3,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "12000000000",
        //     categoryNamePath: "母婴和儿童用品",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "94",
        //     parentId: "0",
        //     categoryCode: "13000000000",
        //     categoryName: "金融服务",
        //     sortCode: 4,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "13000000000",
        //     categoryNamePath: "金融服务",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "95",
        //     parentId: "0",
        //     categoryCode: "14000000000",
        //     categoryName: "美妆个护",
        //     sortCode: 5,
        //     isDelete: 0,
        //     isHasChild: 1,
        //     remark: "",
        //     createBy: "",
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: "admin",
        //     updateTime: "2025-08-13 09:51:53",
        //     categoryCodePath: "14000000000",
        //     categoryNamePath: "美妆个护",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [
        //       {
        //         id: "96",
        //         parentId: "95",
        //         categoryCode: "14100000000",
        //         categoryName: "口腔护理",
        //         sortCode: 1,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14100000000",
        //         categoryNamePath: "美妆个护|口腔护理",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "97",
        //         parentId: "95",
        //         categoryCode: "14101000000",
        //         categoryName: "护发",
        //         sortCode: 2,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: "",
        //         createBy: "",
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: "admin",
        //         updateTime: "2025-08-13 09:51:45",
        //         categoryCodePath: "14000000000|14101000000",
        //         categoryNamePath: "美妆个护|护发",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "98",
        //         parentId: "95",
        //         categoryCode: "14102000000",
        //         categoryName: "假发与发型设计",
        //         sortCode: 3,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14102000000",
        //         categoryNamePath: "美妆个护|假发与发型设计",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "99",
        //         parentId: "95",
        //         categoryCode: "14103000000",
        //         categoryName: "皮肤护理",
        //         sortCode: 4,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14103000000",
        //         categoryNamePath: "美妆个护|皮肤护理",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "100",
        //         parentId: "95",
        //         categoryCode: "14104000000",
        //         categoryName: "美妆",
        //         sortCode: 5,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14104000000",
        //         categoryNamePath: "美妆个护|美妆",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "101",
        //         parentId: "95",
        //         categoryCode: "14105000000",
        //         categoryName: "医疗美容",
        //         sortCode: 6,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14105000000",
        //         categoryNamePath: "美妆个护|医疗美容",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "102",
        //         parentId: "95",
        //         categoryCode: "14106000000",
        //         categoryName: "香水和香氛",
        //         sortCode: 7,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14106000000",
        //         categoryNamePath: "美妆个护|香水和香氛",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "103",
        //         parentId: "95",
        //         categoryCode: "14107000000",
        //         categoryName: "女性护理",
        //         sortCode: 8,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14107000000",
        //         categoryNamePath: "美妆个护|女性护理",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "104",
        //         parentId: "95",
        //         categoryCode: "14999000000",
        //         categoryName: "其他美妆个护",
        //         sortCode: 9,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "14000000000|14999000000",
        //         categoryNamePath: "美妆个护|其他美妆个护",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //     ],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "105",
        //     parentId: "0",
        //     categoryCode: "15000000000",
        //     categoryName: "科技与电子",
        //     sortCode: 6,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "15000000000",
        //     categoryNamePath: "科技与电子",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "106",
        //     parentId: "0",
        //     categoryCode: "16000000000",
        //     categoryName: "家电",
        //     sortCode: 7,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "16000000000",
        //     categoryNamePath: "家电",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "107",
        //     parentId: "0",
        //     categoryCode: "17000000000",
        //     categoryName: "旅行",
        //     sortCode: 8,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "17000000000",
        //     categoryNamePath: "旅行",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "108",
        //     parentId: "0",
        //     categoryCode: "18000000000",
        //     categoryName: "家居用品",
        //     sortCode: 9,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "18000000000",
        //     categoryNamePath: "家居用品",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "109",
        //     parentId: "0",
        //     categoryCode: "19000000000",
        //     categoryName: "宠物",
        //     sortCode: 10,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "19000000000",
        //     categoryNamePath: "宠物",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "110",
        //     parentId: "0",
        //     categoryCode: "20000000000",
        //     categoryName: "应用",
        //     sortCode: 11,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "20000000000",
        //     categoryNamePath: "应用",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "111",
        //     parentId: "0",
        //     categoryCode: "21000000000",
        //     categoryName: "家居装修",
        //     sortCode: 12,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "21000000000",
        //     categoryNamePath: "家居装修",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "112",
        //     parentId: "0",
        //     categoryCode: "22000000000",
        //     categoryName: "服装及配饰",
        //     sortCode: 13,
        //     isDelete: 0,
        //     isHasChild: 1,
        //     remark: "",
        //     createBy: "",
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: "admin",
        //     updateTime: "2025-08-13 09:50:58",
        //     categoryCodePath: "22000000000",
        //     categoryNamePath: "服装及配饰",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [
        //       {
        //         id: "113",
        //         parentId: "112",
        //         categoryCode: "22101000000",
        //         categoryName: "服装配饰",
        //         sortCode: 1,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22101000000",
        //         categoryNamePath: "服装及配饰|服装配饰",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "114",
        //         parentId: "112",
        //         categoryCode: "22102000000",
        //         categoryName: "箱包",
        //         sortCode: 2,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22102000000",
        //         categoryNamePath: "服装及配饰|箱包",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "115",
        //         parentId: "112",
        //         categoryCode: "22105000000",
        //         categoryName: "手表",
        //         sortCode: 3,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22105000000",
        //         categoryNamePath: "服装及配饰|手表",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "116",
        //         parentId: "112",
        //         categoryCode: "22106000000",
        //         categoryName: "普通饰品",
        //         sortCode: 4,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22106000000",
        //         categoryNamePath: "服装及配饰|普通饰品",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "117",
        //         parentId: "112",
        //         categoryCode: "22107000000",
        //         categoryName: "高档首饰",
        //         sortCode: 5,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22107000000",
        //         categoryNamePath: "服装及配饰|高档首饰",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "118",
        //         parentId: "112",
        //         categoryCode: "22108000000",
        //         categoryName: "男士服装",
        //         sortCode: 6,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22108000000",
        //         categoryNamePath: "服装及配饰|男士服装",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "119",
        //         parentId: "112",
        //         categoryCode: "22109000000",
        //         categoryName: "男士鞋",
        //         sortCode: 7,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22109000000",
        //         categoryNamePath: "服装及配饰|男士鞋",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "120",
        //         parentId: "112",
        //         categoryCode: "22110000000",
        //         categoryName: "女士服装",
        //         sortCode: 8,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: "",
        //         createBy: "",
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: "admin",
        //         updateTime: "2025-08-13 09:58:21",
        //         categoryCodePath: "22000000000|22110000000",
        //         categoryNamePath: "服装及配饰|女士服装",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "121",
        //         parentId: "112",
        //         categoryCode: "22111000000",
        //         categoryName: "女士鞋",
        //         sortCode: 9,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22111000000",
        //         categoryNamePath: "服装及配饰|女士鞋",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "122",
        //         parentId: "112",
        //         categoryCode: "22112000000",
        //         categoryName: "传统服装与礼服",
        //         sortCode: 10,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22112000000",
        //         categoryNamePath: "服装及配饰|传统服装与礼服",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "123",
        //         parentId: "112",
        //         categoryCode: "22113000000",
        //         categoryName: "可穿戴技术设备",
        //         sortCode: 11,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22113000000",
        //         categoryNamePath: "服装及配饰|可穿戴技术设备",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "124",
        //         parentId: "112",
        //         categoryCode: "22999000000",
        //         categoryName: "其他服装与服饰",
        //         sortCode: 12,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22999000000",
        //         categoryNamePath: "服装及配饰|其他服装与服饰",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "125",
        //         parentId: "112",
        //         categoryCode: "22114000000",
        //         categoryName: "鞋饰",
        //         sortCode: 13,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "22000000000|22114000000",
        //         categoryNamePath: "服装及配饰|鞋饰",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //     ],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "126",
        //     parentId: "0",
        //     categoryCode: "23000000000",
        //     categoryName: "新闻娱乐",
        //     sortCode: 14,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "23000000000",
        //     categoryNamePath: "新闻娱乐",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "127",
        //     parentId: "0",
        //     categoryCode: "24000000000",
        //     categoryName: "商业服务",
        //     sortCode: 15,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "24000000000",
        //     categoryNamePath: "商业服务",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "128",
        //     parentId: "0",
        //     categoryCode: "25000000000",
        //     categoryName: "游戏",
        //     sortCode: 16,
        //     isDelete: 0,
        //     isHasChild: 1,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "25000000000",
        //     categoryNamePath: "游戏",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [
        //       {
        //         id: "129",
        //         parentId: "128",
        //         categoryCode: "25100101000",
        //         categoryName: "模拟游戏1",
        //         sortCode: 1,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: "",
        //         createBy: "",
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: "admin",
        //         updateTime: "2025-08-13 10:06:04",
        //         categoryCodePath: "25000000000|25100101000",
        //         categoryNamePath: "游戏|模拟游戏1",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "130",
        //         parentId: "128",
        //         categoryCode: "25100102000",
        //         categoryName: "策略游戏",
        //         sortCode: 2,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100102000",
        //         categoryNamePath: "游戏|策略游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "131",
        //         parentId: "128",
        //         categoryCode: "25100103000",
        //         categoryName: "射击游戏",
        //         sortCode: 3,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100103000",
        //         categoryNamePath: "游戏|射击游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "132",
        //         parentId: "128",
        //         categoryCode: "25100104000",
        //         categoryName: "运动游戏",
        //         sortCode: 4,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100104000",
        //         categoryNamePath: "游戏|运动游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "133",
        //         parentId: "128",
        //         categoryCode: "25100105000",
        //         categoryName: "动作游戏",
        //         sortCode: 5,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100105000",
        //         categoryNamePath: "游戏|动作游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "134",
        //         parentId: "128",
        //         categoryCode: "25100107000",
        //         categoryName: "赛车游戏",
        //         sortCode: 6,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100107000",
        //         categoryNamePath: "游戏|赛车游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "135",
        //         parentId: "128",
        //         categoryCode: "25100110000",
        //         categoryName: "益智游戏",
        //         sortCode: 7,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: "",
        //         createBy: "",
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: "admin",
        //         updateTime: "2025-08-13 09:58:29",
        //         categoryCodePath: "25000000000|25100110000",
        //         categoryNamePath: "游戏|益智游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "136",
        //         parentId: "128",
        //         categoryCode: "25100115000",
        //         categoryName: "博彩游戏",
        //         sortCode: 8,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100115000",
        //         categoryNamePath: "游戏|博彩游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "137",
        //         parentId: "128",
        //         categoryCode: "25100118000",
        //         categoryName: "消消乐游戏",
        //         sortCode: 9,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100118000",
        //         categoryNamePath: "游戏|消消乐游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "138",
        //         parentId: "128",
        //         categoryCode: "25100119000",
        //         categoryName: "角色扮演游戏",
        //         sortCode: 10,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100119000",
        //         categoryNamePath: "游戏|角色扮演游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "139",
        //         parentId: "128",
        //         categoryCode: "25100120000",
        //         categoryName: "派对游戏",
        //         sortCode: 11,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100120000",
        //         categoryNamePath: "游戏|派对游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "140",
        //         parentId: "128",
        //         categoryCode: "25100121000",
        //         categoryName: "桌面游戏",
        //         sortCode: 12,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100121000",
        //         categoryNamePath: "游戏|桌面游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "141",
        //         parentId: "128",
        //         categoryCode: "25100122000",
        //         categoryName: "儿童游戏",
        //         sortCode: 13,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: null,
        //         createBy: null,
        //         createTime: "2025-07-10 18:23:04",
        //         updateBy: null,
        //         updateTime: "2025-07-10 18:23:04",
        //         categoryCodePath: "25000000000|25100122000",
        //         categoryNamePath: "游戏|儿童游戏",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //     ],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "142",
        //     parentId: "0",
        //     categoryCode: "26000000000",
        //     categoryName: "生活服务",
        //     sortCode: 17,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "26000000000",
        //     categoryNamePath: "生活服务",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "143",
        //     parentId: "0",
        //     categoryCode: "27000000000",
        //     categoryName: "美食与饮料",
        //     sortCode: 18,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "27000000000",
        //     categoryNamePath: "美食与饮料",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "145",
        //     parentId: "0",
        //     categoryCode: "29000000000",
        //     categoryName: "健康",
        //     sortCode: 18,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: "",
        //     createBy: "",
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: "admin",
        //     updateTime: "2025-07-29 10:09:15",
        //     categoryCodePath: "29000000000",
        //     categoryNamePath: "健康",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "144",
        //     parentId: "0",
        //     categoryCode: "28000000000",
        //     categoryName: "运动与户外活动",
        //     sortCode: 19,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "28000000000",
        //     categoryNamePath: "运动与户外活动",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "146",
        //     parentId: "0",
        //     categoryCode: "30000000000",
        //     categoryName: "电商（非应用）",
        //     sortCode: 21,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: null,
        //     createTime: "2025-07-10 18:23:04",
        //     updateBy: null,
        //     updateTime: "2025-07-10 18:23:04",
        //     categoryCodePath: "30000000000",
        //     categoryNamePath: "电商（非应用）",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "1947895771886919682",
        //     parentId: "0",
        //     categoryCode: "B04",
        //     categoryName: "其他1",
        //     sortCode: 22,
        //     isDelete: 0,
        //     isHasChild: 1,
        //     remark: "",
        //     createBy: "admin",
        //     createTime: "2025-07-23 13:45:17",
        //     updateBy: "admin",
        //     updateTime: "2025-08-13 10:06:19",
        //     categoryCodePath: "B04",
        //     categoryNamePath: "其他1",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [
        //       {
        //         id: "1947896118793609218",
        //         parentId: "1947895771886919682",
        //         categoryCode: "B04002",
        //         categoryName: "房车",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: "",
        //         createBy: "admin",
        //         createTime: "2025-07-23 13:46:40",
        //         updateBy: "admin",
        //         updateTime: "2025-07-23 16:50:19",
        //         categoryCodePath: "B04|B04002",
        //         categoryNamePath: "其他|房车",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "1947896009905283074",
        //         parentId: "1947895771886919682",
        //         categoryCode: "B04001",
        //         categoryName: "大码",
        //         sortCode: 1,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: "",
        //         createBy: "admin",
        //         createTime: "2025-07-23 13:46:14",
        //         updateBy: "admin",
        //         updateTime: "2025-07-29 13:41:09",
        //         categoryCodePath: "B04|B04001",
        //         categoryNamePath: "其他|大码",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //       {
        //         id: "1947896780130492417",
        //         parentId: "1947895771886919682",
        //         categoryCode: "B04003",
        //         categoryName: "清洁收纳",
        //         sortCode: 0,
        //         isDelete: 0,
        //         isHasChild: 0,
        //         remark: "",
        //         createBy: "admin",
        //         createTime: "2025-07-23 13:49:17",
        //         updateBy: "admin",
        //         updateTime: "2025-07-23 16:50:22",
        //         categoryCodePath: "B04|B04003",
        //         categoryNamePath: "其他|清洁收纳",
        //         categoryTypeId: "0",
        //         categoryTypeName: "0",
        //         children: [],
        //         size: 0,
        //         personalTags: [],
        //         attributeCode: null,
        //         attributeName: null,
        //         attributeEnName: null,
        //         attributeCodePath: null,
        //         attributeNamePath: null,
        //         attributeEnNamePath: null,
        //         attributeTypeId: null,
        //         attributeTypeName: null,
        //       },
        //     ],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
        //   {
        //     id: "1955918112248197122",
        //     parentId: "0",
        //     categoryCode: "B15",
        //     categoryName: "123",
        //     sortCode: 23,
        //     isDelete: 0,
        //     isHasChild: 0,
        //     remark: null,
        //     createBy: "admin",
        //     createTime: "2025-08-14 17:03:12",
        //     updateBy: null,
        //     updateTime: null,
        //     categoryCodePath: "B15",
        //     categoryNamePath: "123",
        //     categoryTypeId: "0",
        //     categoryTypeName: "0",
        //     children: [],
        //     size: 0,
        //     personalTags: [],
        //     attributeCode: null,
        //     attributeName: null,
        //     attributeEnName: null,
        //     attributeCodePath: null,
        //     attributeNamePath: null,
        //     attributeEnNamePath: null,
        //     attributeTypeId: null,
        //     attributeTypeName: null,
        //   },
    productCategoryList.value = res;
    // 递归处理树形数据，设置 isHasChild 为 1 的节点 disabled 为 true
    const processTreeData = (nodes) => {
      if (!nodes || !Array.isArray(nodes)) return;

      nodes.forEach((node) => {
        if (node.isHasChild === 1) {
          node.disabled = true;
        }

        // 递归处理子节点
        if (node.children && node.children.length > 0) {
          processTreeData(node.children);
        }
      });
    };

    processTreeData(productCategoryList.value);
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

  model.value = {}
  selectedNode.value = null;
  isShowLevel.value = false;
  selProductCategoryName.value = '';
  ishandleOk.value = true;
  visible.value = false;
  formRef.value?.clearValidate();
  isShow.value = false;
  isEdit.value = false;
};

const isValidURL = (url) => {
  // 不严格的URL验证，支持 http/https/ftp/rtsp/mms 协议
  const pattern = /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/i;
  return pattern.test(url);
};

const handleOk = () => {
  // 防止连续执行 - 使用loading状态和防抖机制
  if (confirmLoading.value) {
    return;
  }
  if (model.value.productUrl && !isValidURL(model.value.productUrl)) {
    createMessage.warning('请输入正确的产品链接');
    return;
  }

  if (model.value.productImageUrl && !isValidURL(model.value.productImageUrl)) {
    createMessage.warning('请输入正确的产品图片链接');
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
  formRef.value?.validate().then((res) => {
    if (res) {
      executeSubmit();
    }
  });
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
  const params = {
    id: model.value.id ? model.value.id : '',
    brandId: model.value.brandId,
    productName: model.value.productName,
    productUrl: model.value.productUrl,
    categoryId: selectedNode.value.id,
    categoryName: selectedNode.value.categoryName,
    productAttributes: model.value.productModel
      ? JSON.stringify({ productModel: model.value.productModel })
      : undefined,
    productImage: model.value.productImage,
    dataList: [],
  };

  for (let i = 0; i < categoryList.value.length; i++) {
    const item = categoryList.value[i];

    console.log('📤 处理类型:', item.typeName, {
      filterCategoryName: item.filterCategoryName,
      selectedIds: [...item.selectedIds],
      oldSelectedIds: [...(item.oldSelectedIds || [])],
      oldLevelMap: { ...(item.oldLevelMap || {}) },
    });

    // 获取真正的选中状态：优先使用全局状态，如果没有则使用当前状态
    let finalSelectedIds;
    if (item.filterCategoryName && item.oldSelectedIds) {
      // 筛选状态下，使用全局状态
      finalSelectedIds = [...item.oldSelectedIds];
      console.log('📤 使用全局选中状态:', finalSelectedIds);
    } else {
      // 非筛选状态下，使用当前状态
      finalSelectedIds = [...item.selectedIds];
      console.log('📤 使用当前选中状态:', finalSelectedIds);
    }

    const list = item.data
      .filter((subItem) => finalSelectedIds.includes(subItem.id) && !subItem.disabled)
      .map((subItem) => {
        console.log('📤 处理选中项:', subItem.label, 'level:', subItem.level);

        return {
          attributeId: subItem.id,
          attributeName: subItem.attributeName,
          level: selectedNode.value.isHasChild !== 1 ? subItem.level : '',
        };
      });

    console.log('📤 最终生成的list:', list);

    // 只有当list不为空时才添加到dataList中
    if (list.length > 0) {
      params.dataList.push({
        typeId: item.typeId,
        list: list,
      });
    }
  }

  console.log('📤 最终提交的params:', params);

  // 判断dataList中所有的list都是空的
  const influencerType = params.dataList.find(
    (item) => item.typeId === '20250513000003'
  );

  if (!influencerType || influencerType.list.length === 0) {
    createMessage.error('至少选择一个达人类型');
    confirmLoading.value = false;
    return;
  }

  defHttp
    .post({ url: '/kol/kolProduct/add', data: params })
    .then((res) => {
      if (res) {
        // createMessage.success('保存成功');
        emit('ok', model.value);
        close();
      }
    })
    .catch((_error) => {
      // console.error('提交失败:', error)
    })
    .finally(() => {
      confirmLoading.value = false;
    });
};

const handleCancel = () => {
  close();
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
        '🛡️ 恢复不可见选项的level状态:',
        option.label,
        'level:',
        option.level
      );
    }
  });

  console.log('🛡️ 不可见选项level状态保护完成');
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

  // 4. 更新全选状态
  updateCheckAllStatus(item);

  console.log('🔄 所有状态恢复完成');
};




defineExpose({
  merchantDemandModalAdd,
  add,
  edit,
  open: edit,
});
</script>
<style>
.production-information-modal {
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
  max-width: 1066px;
  .category-item {
    flex: none;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    width: 680px;
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
:deep(.ant-checkbox-group) label {
  margin-bottom: 14px;
}
a {
  color: @primary-color;
}
</style>
