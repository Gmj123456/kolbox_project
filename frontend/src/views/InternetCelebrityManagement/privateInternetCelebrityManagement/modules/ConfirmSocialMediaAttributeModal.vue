<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    :closable="true"
    :width="999 "
    @ok="handleOk"
    @cancel="handleCancel"
    :okText="okText"
    cancelText="关闭"
    class="ConfirmSocialMediaAttributeModalNew"
  >
    <a-spin :spinning="confirmLoading">
      <a-transfer
        :data-source="attributeList"
        v-model:target-keys="targetKeys"
        v-model:selected-keys="selectedKeys"
        :show-select-all="false"
        @change="handleChange"
        @select-change="handleSelectChange"
        class="transfer-items-container"
      >
        <!-- ✅ 注意：必须使用 slotProps 才能自定义渲染 -->
        <template #children="slotProps">
          <!-- 左侧 -->
          <div
            v-if="slotProps.direction === 'left'"
            class="transfer-panel left-panel"
          >
            <a-input-search
              placeholder="达人类型筛选"
              v-model:value="leftSearch"
              @change="onSearch('left')"
              allowClear
              style="margin-bottom: 10px"
            />

            <div style="width: 600px; height: 530px; overflow-y: auto">
            <a-row>
              <a-col
                :span="12"
                style="margin-bottom: 4px"
                v-for="item in leftData"
                :key="item.id"
              >
                <a-checkbox
                  :checked="selectedKeys.includes(item.id)"
                  @change="toggleSelect(item.id)"
                >
                {{ item.attributeNamePath }}
                </a-checkbox>
              </a-col>
            </a-row>
            </div>
          </div>

          <!-- ✅ 右侧：可拖拽排序 -->
          <div
            v-if="slotProps.direction === 'right'"
            class="transfer-panel right-panel"
          >
            <a-input-search
              placeholder="达人类型筛选"
              v-model:value="rightSearch"
              @change="onSearch('right')"
              allowClear
              style="margin-bottom: 10px"
            />

            <draggable
              v-model="rightList"
              handle=".drag-handle"
              animation="150"
              @end="onRightDragEnd"
              item-key="id"
              style="width: 300px; height: 530px; overflow-y: auto"
            >
              <template #item="{ element }">
                <a-col
                  :key="element.id"
                  style="margin-bottom: 4px"
                >
                  <div style="display: flex; align-items: center">
                    <span class="drag-handle" style="cursor: move; margin-right: 6px"
                      >⋮⋮</span
                    >

                    <a-checkbox
                      :checked="selectedKeys.includes(element.id)"
                      @change="toggleSelect(element.id)"
                    >
                      <!-- <CategoryEllipsisTooltip :text="element.attributeNamePath" /> -->
                      {{ element.attributeNamePath }}
                    </a-checkbox>
                  </div>
                </a-col>
              </template>
            </draggable>
          </div>
        </template>

        <!-- footer 数量统计 -->
        <template #footer="{ direction }">
          <div v-if="direction === 'left'" style="padding: 6px 12px; font-size: 12px">
            <span>合计 {{ leftData.length }} </span>
            <span>
              已选择
              <a>{{ leftData.filter((i) => selectedKeys.includes(i.id)).length }}</a>
            </span>
          </div>

          <div v-if="direction === 'right'" style="padding: 6px 12px; font-size: 12px">
            <span>合计 {{ rightList.length }} </span>
            <span>
              已选择
              <a>{{ rightList.filter((i) => selectedKeys.includes(i.id)).length }}</a>
            </span>
          </div>
        </template>
      </a-transfer>
    </a-spin>
    <template #footer>
      <span>拖拽 ⋮⋮ 可以调整社媒属性顺序</span>
      <a-button key="back" @click="handleCancel" style="margin-left: 8px">
        关闭
      </a-button>
      <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">
        {{ okText || '保存' }}
      </a-button>
    </template>
  </a-modal>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import draggable from 'vuedraggable';
// import CategoryEllipsisTooltip from '../../../tiktok/CategoryAssociation/modules/CategoryEllipsisTooltip';
import { cloneDeep } from 'lodash-es';

const { createMessage: $message } = useMessage();

const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const okText = ref('保存');
const model = ref({});
const typeId = ref('');

const attributeList = ref([]);
const targetKeys = ref([]);
const selectedKeys = ref([]);

const leftSearch = ref('');
const rightSearch = ref('');

/** ✅ 右侧真实数组，拖拽修改此数组 */
const rightList = ref([]);

/** 左侧筛选后的数据 */
const leftData = computed(() => {
  return attributeList.value.filter(
    (item) =>
      !targetKeys.value.includes(item.id) &&
      item.attributeNamePath.includes(leftSearch.value)
  );
});

watch(targetKeys, () => {
  syncRightList();
});

watch(rightSearch, () => {
  syncRightList();
});

/** ✅ 同步右侧数据(展示顺序、搜索过滤) */
function syncRightList() {
  rightList.value = attributeList.value
    .filter((item) => targetKeys.value.includes(item.id))
    .filter((item) => item.attributeNamePath.includes(rightSearch.value));
}

/** ✅ 拖拽完成后，将顺序写回 targetKeys */
function onRightDragEnd() {
  // 1️⃣ 更新 targetKeys
  targetKeys.value = rightList.value.map((item) => item.id);

  // 2️⃣ 更新 attributeList 中对应右侧顺序
  const rightMap = rightList.value.reduce((acc, item, index) => {
    acc[item.id] = index;
    return acc;
  }, {});

  attributeList.value = attributeList.value.slice().sort((a, b) => {
    const aIndex = rightMap[a.id];
    const bIndex = rightMap[b.id];

    if (aIndex !== undefined && bIndex !== undefined) {
      // 两个都在右侧，用拖拽顺序排序
      return aIndex - bIndex;
    } else if (aIndex !== undefined) {
      // a 在右侧，b 在左侧，a靠前
      return -1;
    } else if (bIndex !== undefined) {
      // b 在右侧，a 在左侧，b靠前
      return 1;
    } else {
      // 都在左侧，保持原顺序
      return 0;
    }
  });
}

function onSearch() {
  syncRightList();
}

function toggleSelect(id) {
  if (selectedKeys.value.includes(id)) {
    selectedKeys.value = selectedKeys.value.filter((k) => k !== id);
  } else {
    selectedKeys.value = [...selectedKeys.value, id];
  }
}

function handleSelectChange(sourceSelectedKeys, targetSelectedKeys) {
  selectedKeys.value = [...sourceSelectedKeys, ...targetSelectedKeys];
}

function handleChange(nextTargetKeys) {
  targetKeys.value = nextTargetKeys;
}

/** ✅ 打开弹窗初始化 */
async function show(record) {
  model.value = cloneDeep(record);
  await initData();

  if (record.dataList && record.dataList.length > 0) {
    const list = record.dataList[0].list;

    // 提取右侧已选 ID，按 rank 或原顺序排序
    const sortedIds = [...list].map((i) => i.attributeId);

    targetKeys.value = sortedIds;

    // ✅ 提取右侧 attribute 对象，按顺序排列
    const rightPart = sortedIds
      .map((id) => attributeList.value.find((a) => a.id === id))
      .filter(Boolean);

    // ✅ 未选部分保留原位
    const leftPart = attributeList.value.filter((a) => !sortedIds.includes(a.id));

    // ✅ 最终写回 attributeList
    attributeList.value = [...leftPart, ...rightPart];
  } else {
    targetKeys.value = [];
  }

  syncRightList();
  visible.value = true;
}

async function initData() {
  try {
    const res = await defHttp.get({ url: '/kol/attribute/getKolAttribute' });
    attributeList.value = res[0].data;
    typeId.value = res[0].typeId;
  } catch (error) {
    console.error('获取属性列表失败:', error);
  }
}

async function handleOk() {
  if (confirmLoading.value) return;

  if (targetKeys.value.length === 0) {
    $message.warning('至少选择一项社媒属性');
    return;
  }

  confirmLoading.value = true;

  try {
    // 先过滤 targetKeys 中能在 attributeList 里找到的 id
    const filteredTargetKeys = targetKeys.value.filter((item) =>
      attributeList.value.some((a) => a.id === item)
    );
    // 接口返回的数据中 有的达人类型不是最下面的叶子节点 处理数据时去除掉
    const params = {
      id: model.value.id,
      celebrityId: model.value.celebrityId,
      account: model.value.account,
      dataList: [
        {
          typeId: model.value.dataList ? model.value.dataList[0].typeId : typeId.value,
          list: filteredTargetKeys.map((item, index) => {
            const foundAttr = attributeList.value.find((a) => a.id === item);
            return {
              rank: index + 1,
              attributeId: item,
              attributeName: foundAttr ? foundAttr.attributeName : '',
            };
          }),
        },
      ],
    };

    const res = await defHttp.post({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/celebrityAttributeUpdate',
      data: params,
    },{isTransformResponse:false});

    if (res.success) {
      emit('ok');
      close();
      $message.success('保存成功');
    } else {
      $message.error(res.message);
    }
  } catch (error) {
    console.error('保存失败:', error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

function close() {
  visible.value = false;
  attributeList.value = [];
  targetKeys.value = [];
  selectedKeys.value = [];
  rightList.value = [];
  leftSearch.value = '';
  rightSearch.value = '';
  model.value = {};
}

const emit = defineEmits(['ok']);

defineExpose({
  show,
  title,
  okText,
});
</script>
<style lang="less" scoped>

  .checkbox-label {
    display: flex;
    width: 100%;
    align-items: center;
    justify-content: space-between;
  }
  :deep(.ant-transfer-list-header) {
    display: none !important;
  }
  :deep(.ant-transfer-list) {
    padding: 8px !important;
  }
  
</style>
<style>
.drag-handle {
  cursor: move;
}
.drag-ghost {
  opacity: 0.5;
}
</style>

<style>
.ConfirmSocialMediaAttributeModalNew .ant-modal-body {
  padding: 12px 12px !important;
}
.transfer-items-container .ant-checkbox-wrapper {
  display: flex !important;
  align-items: center;
  width: 100% !important;
  font-size: 12px !important;
}
.transfer-items-container .ant-checkbox + span {
  width: 100% !important;
}
.transfer-items-container .ant-checkbox-wrapper + .ant-checkbox-wrapper {
  margin-left: 0 !important;
}
.transfer-items-container :deep(.ant-checkbox-wrapper span:nth-child(2)) {
  padding-left: 3px;
}
.transfer-items-container :deep(.ant-checkbox-wrapper) {
  font-size: 12px !important;
}
</style>

