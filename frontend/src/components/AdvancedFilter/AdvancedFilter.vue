<template>
  <div class="advanced-filter">
    <el-popover
      ref="mainFilterPopover"
      popper-class="more-filter-popover"
      placement="bottom-start"
      width="650"
      trigger="click"
      @show="onMainPopoverShow"
      @hide="onMainPopoverHide"
    >
      <div style="height: 340px" class="more-filter-container">
        <div class="more-filter-content">
          <div class="more-filter-content_menu">
            <div
              class="more-filter-content_menu_item"
              :class="{
                selected:
                  filterType === item.value || filterType === item.typeId,
              }"
              v-for="item in filterOptions"
              :key="item.typeId"
              @click="selectFilter(item)"
            >
              <span>{{ item.typeName }}</span>
              <span>
                <span
                  v-if="getAttributeCount(item.typeId) > 0"
                  class="selected-count"
                >
                  {{ getAttributeCount(item.typeId) }}
                </span>
                <i style="color: #606266" class="el-icon-arrow-right"></i>
              </span>
            </div>
            <div
              class="more-filter-content_menu_item"
              :class="{ selected: filterType === 'followersNum' }"
              @click="selectFilterOther('followersNum')"
            >
              <span>粉丝数</span>
              <span>
                <span v-if="getFollowersCount() > 0" class="selected-count">
                  {{ getFollowersCount() }}
                </span>
                <i style="color: #606266" class="el-icon-arrow-right"></i>
              </span>
            </div>
            <div
              class="more-filter-content_menu_item"
              :class="{ selected: filterType === 'effectiveVideoNum' }"
              @click="selectFilterOther('effectiveVideoNum')"
            >
              <span>有效视频</span>
              <span>
                <span v-if="getVideoCount() > 0" class="selected-count">
                  {{ getVideoCount() }}
                </span>
                <i style="color: #606266" class="el-icon-arrow-right"></i>
              </span>
            </div>
          </div>

          <div
            v-if="
              filterType !== 'followersNum' &&
              filterType !== 'effectiveVideoNum' &&
              filterType !== 'tag'
            "
            class="more-filter-form"
          >
            <el-cascader-panel
              v-model="attributeIdsMap[filterType]"
              :options="options"
              :props="{ value: 'id', label: 'attributeName' }"
            ></el-cascader-panel>
          </div>
          <div class="more-filter-form" v-if="filterType == 'followersNum'">
            <div class="filter-form-select">
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('1K-10K'),
                }"
                @click="selectFollowersRange('1K-10K')"
              >
                1K-10K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('10K-20K'),
                }"
                @click="selectFollowersRange('10K-20K')"
              >
                10K-20K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('20K-50K'),
                }"
                @click="selectFollowersRange('20K-50K')"
              >
                20K-50K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('50K-100K'),
                }"
                @click="selectFollowersRange('50K-100K')"
              >
                50K-100K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('100K+'),
                }"
                @click="selectFollowersRange('100K+')"
              >
                100K+
              </div>
            </div>
            <div class="range-input">
              <div class="range-input__custom-label">自定义范围</div>
              <div class="range-input__content">
                <div class="range-input__flex">
                  <div class="range-input__label">最小粉丝数</div>
                  <div class="range-input__input" style="position: relative">
                    <a-input-number
                      class="input-num"
                      style="width: 100%"
                      :min="0"
                      :max="99999"
                      :precision="0"
                      v-model="minFollowers"
                      placeholder="输入值"
                      @change="onFollowersInputChange"
                    ></a-input-number>
                    <span
                      style="
                        position: absolute;
                        top: 50%;
                        z-index: 2;
                        display: -ms-flexbox;
                        display: flex;
                        -ms-flex-align: center;
                        align-items: center;
                        color: rgba(0, 0, 0, 0.65);
                        line-height: 0;
                        transform: translateY(-50%);
                        right: 12px;
                      "
                      class="ant-input-suffix"
                      >K</span
                    >
                  </div>
                </div>
                <div class="range-input__sperate">-</div>
                <div class="range-input__flex">
                  <div class="range-input__label">最大粉丝数</div>
                  <div class="range-input__input" style="position: relative">
                    <a-input-number
                      class="input-num"
                      style="width: 100%"
                      :min="0"
                      :max="99999"
                      :precision="0"
                      v-model="maxFollowers"
                      placeholder="输入值"
                      @change="onFollowersInputChange"
                    ></a-input-number>
                    <span
                      style="
                        position: absolute;
                        top: 50%;
                        z-index: 2;
                        display: -ms-flexbox;
                        display: flex;
                        -ms-flex-align: center;
                        align-items: center;
                        color: rgba(0, 0, 0, 0.65);
                        line-height: 0;
                        transform: translateY(-50%);
                        right: 12px;
                      "
                      class="ant-input-suffix"
                      >K</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div
            class="more-filter-form"
            v-if="filterType == 'effectiveVideoNum'"
          >
            <div class="range-input">
              <div class="range-input__custom-label">自定义范围</div>
              <div class="range-input__content">
                <div class="range-input__flex">
                  <div class="range-input__label">最小有效视频数</div>
                  <div class="range-input__input">
                    <a-input-number
                      style="width: 100%"
                      :min="0"
                      :precision="0"
                      :max="20"
                      v-model="minVideoCount"
                      placeholder="输入值"
                      @change="onVideoInputChange"
                    ></a-input-number>
                  </div>
                </div>
                <div class="range-input__sperate">-</div>
                <div class="range-input__flex">
                  <div class="range-input__label">最大有效视频数</div>
                  <div class="range-input__input">
                    <a-input-number
                      style="width: 100%"
                      :min="0"
                      :precision="0"
                      :max="20"
                      v-model="maxVideoCount"
                      placeholder="输入值"
                      @change="onVideoInputChange"
                    ></a-input-number>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="more-filter-footer">
          <a-button
            type="primary"
            style="margin-right: 8px"
            @click="applyAllFilters(false)"
            >应用</a-button
          >
          <a-button type="primary" @click="applyAllFilters(true)"
            >查询</a-button
          >
        </div>
      </div>
      <template #reference>
        <a-button
          :class="
            mainFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
          "
          @click="moreFilter"
        >
          更多筛选
          <a-icon class="rotating-arrow-filter-icon" type="down" />
        </a-button>
      </template>
    </el-popover>

    <!-- 粉丝数筛选展示 -->
    <el-popover
      ref="followersFilterPopover"
      popper-class="more-filter-popover"
      placement="bottom-start"
      trigger="click"
      @show="onFollowersPopoverShow"
      @hide="onFollowersPopoverHide"
    >
      <div style="height: 340px" class="more-filter-container">
        <div class="more-filter-content">
          <div class="more-filter-form">
            <div class="filter-form-select">
              <div
                class="filter-form-select_item"
                :class="{ selected: selectedFollowersRanges.includes('1K-10K') }"
                @click="selectFollowersRange('1K-10K')"
              >
                1K-10K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('10K-20K'),
                }"
                @click="selectFollowersRange('10K-20K')"
              >
                10K-20K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('20K-50K'),
                }"
                @click="selectFollowersRange('20K-50K')"
              >
                20K-50K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('50K-100K'),
                }"
                @click="selectFollowersRange('50K-100K')"
              >
                50K-100K
              </div>
              <div
                class="filter-form-select_item"
                :class="{
                  selected: selectedFollowersRanges.includes('100K+'),
                }"
                @click="selectFollowersRange('100K+')"
              >
                100K+
              </div>
            </div>
            <div class="range-input">
              <div class="range-input__custom-label">自定义范围</div>
              <div class="range-input__content">
                <div class="range-input__flex">
                  <div class="range-input__label">最小粉丝数</div>
                  <div class="range-input__input" style="position: relative">
                    <a-input-number
                      style="width: 100%"
                      :min="0"
                      :precision="0"
                      :max="99999"
                      v-model="minFollowers"
                      placeholder="输入值"
                      @change="onFollowersInputChange"
                    ></a-input-number>
                    <span
                      style="
                        position: absolute;
                        top: 50%;
                        z-index: 2;
                        display: -ms-flexbox;
                        display: flex;
                        -ms-flex-align: center;
                        align-items: center;
                        color: rgba(0, 0, 0, 0.65);
                        line-height: 0;
                        transform: translateY(-50%);
                        right: 12px;
                      "
                      class="ant-input-suffix"
                      >K</span
                    >
                  </div>
                </div>
                <div class="range-input__sperate">-</div>
                <div class="range-input__flex">
                  <div class="range-input__label">最大粉丝数</div>
                  <div class="range-input__input" style="position: relative">
                    <a-input-number
                      style="width: 100%"
                      :min="0"
                      :precision="0"
                      :max="99999"
                      v-model="maxFollowers"
                      placeholder="输入值"
                      @change="onFollowersInputChange"
                    ></a-input-number>
                    <span
                      style="
                        position: absolute;
                        top: 50%;
                        z-index: 2;
                        display: -ms-flexbox;
                        display: flex;
                        -ms-flex-align: center;
                        align-items: center;
                        color: rgba(0, 0, 0, 0.65);
                        line-height: 0;
                        transform: translateY(-50%);
                        right: 12px;
                      "
                      class="ant-input-suffix"
                      >K</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="more-filter-footer">
          <a-button
            type="primary"
            style="margin-right: 8px"
            @click="applyAllFilters(false)"
            >应用</a-button
          >
          <a-button type="primary" @click="applyAllFilters(true)"
            >查询</a-button
          >
        </div>
      </div>

      <template #reference>
        <div
          v-show="showFollowersFilter"
          class="more-filter-button"
          style="margin-right: 8px"
        >
          <span> 粉丝： </span>
          <span v-if="minFollowers && maxFollowers">
            {{ minFollowers }}K - {{ maxFollowers }}K
          </span>
          <span
            v-else-if="
              minFollowers && !maxFollowers
            "
          >
            ≥ {{ minFollowers }}K
          </span>
          <span
            v-else-if="
              !minFollowers && maxFollowers
            "
          >
            ≤ {{ maxFollowers }}K
          </span>
          <span
            style="margin-left: 6px"
            :class="
              followersFilterPopoverVisible
                ? 'main-filter-open'
                : 'main-filter-closed'
            "
          >
            <a-icon class="rotating-arrow-filter-icon" type="down" />
          </span>
          <span>
            <a-icon
              @click.stop.prevent="clearFilter('followers')"
              @mousedown.stop.prevent
              type="close-circle"
              theme="filled"
            />
          </span>
        </div>
      </template>
    </el-popover>

    <!-- 有效视频筛选展示 -->
    <el-popover
      ref="videoFilterPopover"
      popper-class="more-filter-popover"
      placement="bottom-start"
      trigger="click"
      @show="onVideoPopoverShow"
      @hide="onVideoPopoverHide"
    >
      <div style="height: 340px" class="more-filter-container">
        <div class="more-filter-content">
          <div class="more-filter-form">
            <div class="range-input">
              <div class="range-input__custom-label">自定义范围</div>
              <div class="range-input__content">
                <div class="range-input__flex">
                  <div class="range-input__label">最小有效视频数</div>
                  <div class="range-input__input">
                    <a-input-number
                      style="width: 100%"
                      :min="0"
                      :max="20"
                      :precision="0"
                      v-model="minVideoCount"
                      placeholder="输入值"
                      @change="onFollowersInputChange"
                    ></a-input-number>
                  </div>
                </div>
                <div class="range-input__sperate">-</div>
                <div class="range-input__flex">
                  <div class="range-input__label">最大有效视频数</div>
                  <div class="range-input__input">
                    <a-input-number
                      style="width: 100%"
                      :min="0"
                      :max="20"
                      :precision="0"
                      v-model="maxVideoCount"
                      placeholder="输入值"
                      @change="onFollowersInputChange"
                    ></a-input-number>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="more-filter-footer">
          <a-button
            type="primary"
            style="margin-right: 8px"
            @click="applyAllFilters(false)"
            >应用</a-button
          >
          <a-button type="primary" @click="applyAllFilters(true)"
            >查询</a-button
          >
        </div>
      </div>

      <template #reference>
        <div
          v-show="showVideoFilter"
          class="more-filter-button"
          style="margin-right: 8px"
        >
          <span>有效视频：</span>
          <span
            v-if="
              minVideoCount &&
              maxVideoCount
            "
          >
            {{ minVideoCount }} - {{ maxVideoCount }}
          </span>
          <span
            v-else-if="
              minVideoCount &&
              !maxVideoCount
            "
          >
            ≥ {{ minVideoCount }}
          </span>
          <span
            v-else-if="
              !minVideoCount &&
              maxVideoCount
            "
          >
            ≤ {{ maxVideoCount }}
          </span>
          <span
            style="margin-left: 6px"
            :class="
              videoFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
            "
          >
            <a-icon class="rotating-arrow-filter-icon" type="down" />
          </span>
          <span>
            <a-icon
              @click.stop.prevent="clearFilter('video')"
              @mousedown.stop.prevent
              type="close-circle"
              theme="filled"
            />
          </span>
        </div>
      </template>
    </el-popover>

    <!-- 属性筛选展示 -->
    <template v-for="item in filterOptions" :key="item.typeId">
      <el-popover
        :ref="`attributeFilterPopover_${item.typeId}`"
        popper-class="more-filter-popover"
        placement="bottom-start"
        trigger="click"
        @show="onAttributePopoverShow(item.typeId)"
        @hide="onAttributePopoverHide(item.typeId)"
      >
        <div style="height: 340px" class="more-filter-container">
          <div class="more-filter-content">
            <el-cascader-panel
              v-model="attributeIdsMap[item.typeId]"
              :options="item.data"
              :props="{ value: 'id', label: 'attributeName' }"
            ></el-cascader-panel>
          </div>
          <div class="more-filter-footer">
            <a-button
              type="primary"
              style="margin-right: 8px"
              @click="applyAllFilters(false)"
              >应用</a-button
            >
            <a-button type="primary" @click="applyAllFilters(true)"
              >查询</a-button
            >
          </div>
        </div>

        <template #reference>
          <div
            v-show="isShowAttribute(item.data)"
            class="more-filter-button"
            style="margin-right: 8px"
          >
            <span>{{ item.typeName }}:</span>
            <span>{{ parseAttributeIds(attributeIds, item.data) }}</span>
            <span
              style="margin-left: 6px"
              :class="
                attributeFilterPopoverVisible[item.typeId]
                  ? 'main-filter-open'
                  : 'main-filter-closed'
              "
            >
              <a-icon class="rotating-arrow-filter-icon" type="down" />
            </span>
            <span>
              <a-icon
                @click.stop.prevent="clearAttributeFilter(item.typeId)"
                @mousedown.stop.prevent
                type="close-circle"
                theme="filled"
              />
            </span>
          </div>
        </template>
      </el-popover>
    </template>

    <!-- 全部清除按钮 -->
    <div
      v-if="hasActiveFilters"
      class="clear-all-button"
      @click="clearAllFilters"
    >
      <span>全部清除</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue';
import { ElCascaderPanel, ElPopover } from 'element-plus';
// 定义组件属性
const props = defineProps({
  // 筛选选项数据
  filterOptions: {
    type: Array,
    default: () => []
  },
  // 当前查询参数
  queryParam: {
    type: Object,
    default: () => ({})
  }
});

// 定义事件
const emit = defineEmits(['apply', 'search', 'update:queryParam']);

// 响应式数据
const mainFilterPopover = ref(null);
const mainFilterPopoverVisible = ref(false);
const followersFilterPopover = ref(null);
const followersFilterPopoverVisible = ref(false);
const videoFilterPopover = ref(null);
const videoFilterPopoverVisible = ref(false);
const attributeFilterPopoverVisible = reactive({});

const filterType = ref('');
const options = ref([]);
const attributeIdsMap = reactive({});

const minFollowers = ref(null);
const maxFollowers = ref(null);
const selectedFollowersRanges = ref([]);

const minVideoCount = ref(null);
const maxVideoCount = ref(null);
const selectedVideoRanges = ref([]);

const isApplyClose = ref(false);

// 备份数据
const backupData = reactive({
  attributeIdsMap: {},
  minFollowers: null,
  maxFollowers: null,
  selectedFollowersRanges: [],
  minVideoCount: null,
  maxVideoCount: null,
  selectedVideoRanges: []
});

// 计算属性
const attributeIds = computed({
  get() {
    return attributeIdsMap[filterType.value] || [];
  },
  set(value) {
    attributeIdsMap[filterType.value] = value;
  }
});

// 是否显示粉丝筛选条件
const showFollowersFilter = computed(() => {
  return props.queryParam.minFollowers || props.queryParam.maxFollowers;
});

// 是否显示视频筛选条件
const showVideoFilter = computed(() => {
  return props.queryParam.minVideoStandardCount || props.queryParam.maxVideoStandardCount;
});

// 是否有激活的筛选条件
const hasActiveFilters = computed(() => {
  return showFollowersFilter.value || showVideoFilter.value || props.queryParam.attributeIds;
});

// 方法定义
function moreFilter() {}

function selectFilter(item) {
  // 保存当前筛选条件的数据
  saveCurrentFilterData();

  // 切换到新的筛选条件
  filterType.value = item.typeId;
  options.value = [...item.data].map((item) => {
    return {
      id: item.id,
      attributeName: item.attributeName,
    };
  });

  // 恢复该筛选条件的已保存数据
  restoreFilterData(item.typeId);
}

function selectFilterOther(type) {
  // 保存当前筛选条件的数据
  saveCurrentFilterData();

  // 切换到新的筛选条件
  filterType.value = type;

  // 恢复该筛选条件的已保存数据
  restoreFilterData(type);
}

function selectFollowersRange(range) {
  // 清空自定义输入框
  minFollowers.value = null;
  maxFollowers.value = null;

  // 清空之前的选择，只保留当前选择
  selectedFollowersRanges.value = [range];
}

function onFollowersInputChange() {
  selectedFollowersRanges.value = [];
}

function onVideoInputChange() {
  selectedVideoRanges.value = [];
}

function getAttributeCount(typeId) {
  if (attributeIdsMap[typeId]) {
    return attributeIdsMap[typeId].length;
  }
  return 0;
}

function getFollowersCount() {
  // 如果有预设选择，返回预设选择数量
  if (selectedFollowersRanges.value.length > 0) {
    return selectedFollowersRanges.value.length;
  }
  // 如果有自定义输入，返回1
  if (minFollowers.value || maxFollowers.value) {
    return 1;
  }
  return 0;
}

function getVideoCount() {
  // 如果有预设选择，返回预设选择数量
  if (selectedVideoRanges.value.length > 0) {
    return selectedVideoRanges.value.length;
  }
  // 如果有自定义输入，返回1
  if (minVideoCount.value || maxVideoCount.value) {
    return 1;
  }
  return 0;
}

function isShowAttribute(data) {
  if (props.queryParam.attributeIds) {
    const arr = props.queryParam.attributeIds.split(',');
    if (data.find((item) => arr.includes(item.id))) {
      return true;
    } else {
      return false;
    }
  } else {
    return false;
  }
}

function parseAttributeIds(ids, data) {
  if (ids) {
    const arr = ids.split(',');
    const res = data.filter((item) => arr.includes(item.id));
    return res.map((item) => item.attributeName).join(',');
  } else {
    return '';
  }
}

// 保存当前筛选条件的数据
function saveCurrentFilterData() {
  if (filterType.value) {
    if (filterType.value === 'followersNum') {
      // 保存粉丝数范围数据
      backupData.minFollowers = minFollowers.value || null;
      backupData.maxFollowers = maxFollowers.value || null;
      backupData.selectedFollowersRanges = selectedFollowersRanges.value || [];
    } else if (filterType.value === 'effectiveVideoNum') {
      // 保存有效视频范围数据
      backupData.minVideoCount = minVideoCount.value || null;
      backupData.maxVideoCount = maxVideoCount.value || null;
      backupData.selectedVideoRanges = selectedVideoRanges.value || [];
    } else {
      // 保存属性选择数据到attributeIdsMap
      if (attributeIds.value) {
        attributeIdsMap[filterType.value] = [...attributeIds.value];
      }
    }
  }
}

// 恢复筛选条件的数据
function restoreFilterData(filterTypeValue) {
  if (filterTypeValue === 'followersNum') {
    // 恢复粉丝数范围数据
    minFollowers.value = backupData.minFollowers;
    maxFollowers.value = backupData.maxFollowers;
    selectedFollowersRanges.value = backupData.selectedFollowersRanges;
  } else if (filterTypeValue === 'effectiveVideoNum') {
    // 恢复有效视频范围数据
    minVideoCount.value = backupData.minVideoCount;
    maxVideoCount.value = backupData.maxVideoCount;
    selectedVideoRanges.value = backupData.selectedVideoRanges;
  } else {
    // 恢复属性选择数据，通过计算属性自动获取
    // attributeIds 会通过计算属性自动从 attributeIdsMap[filterType] 获取
  }
}

// 应用所有筛选条件
function applyAllFilters(isSearch) {
  // 标记为通过应用按钮关闭
  isApplyClose.value = true;

  // 先保存当前筛选条件的数据
  saveCurrentFilterData();

  // 处理粉丝数范围 - 优先使用预设范围，如果没有则使用输入框的值
  let minFollowersValue = null;
  let maxFollowersValue = null;
  if (
    selectedFollowersRanges.value &&
    selectedFollowersRanges.value.length > 0
  ) {
    // 使用预设范围
    const range = selectedFollowersRanges.value[0];
    const rangeValues = parseFollowersRange(range);
    minFollowersValue = rangeValues.min ? rangeValues.min * 1000 : null;
    maxFollowersValue = rangeValues.max ? rangeValues.max * 1000 : null;
  } else {
    // 使用输入框的值
    minFollowersValue = minFollowers.value
      ? minFollowers.value * 1000
      : null;
    maxFollowersValue = maxFollowers.value
      ? maxFollowers.value * 1000
      : null;
  }

  // 收集所有筛选类型的attributeIds
  let allAttributeIds = [];
  Object.keys(attributeIdsMap).forEach((typeId) => {
    if (attributeIdsMap[typeId] && Array.isArray(attributeIdsMap[typeId])) {
      // 将每个筛选类型的属性ID展开并添加到总数组中
      const flatIds = Array.isArray(attributeIdsMap[typeId])
        ? attributeIdsMap[typeId].flat()
        : [];
      allAttributeIds = allAttributeIds.concat(flatIds);
    }
  });

  // 去重
  const uniqueAttributeIds = [...new Set(allAttributeIds)];

  // 构建新的查询参数
  const newQueryParam = {
    ...props.queryParam,
    minFollowers: minFollowersValue,
    maxFollowers: maxFollowersValue,
    minVideoStandardCount: minVideoCount.value,
    maxVideoStandardCount: maxVideoCount.value,
    attributeIds: uniqueAttributeIds.join(',')
  };

  // 更新查询参数
  emit('update:queryParam', newQueryParam);

  // 触发事件
  if (isSearch) {
    emit('search', newQueryParam);
  } else {
    emit('apply', newQueryParam);
  }

  // 关闭所有弹窗
  closeAllPopovers();
}

// 关闭所有弹窗
function closeAllPopovers() {
  // 关闭主筛选弹窗
  if (mainFilterPopover.value) {
    mainFilterPopover.value.hide?.();
  }

  // 关闭粉丝数筛选弹窗
  if (followersFilterPopover.value) {
    followersFilterPopover.value.hide?.();
  }

  // 关闭视频筛选弹窗
  if (videoFilterPopover.value) {
    videoFilterPopover.value.hide?.();
  }

  // 关闭属性筛选弹窗
  if (props.filterOptions && Array.isArray(props.filterOptions)) {
    props.filterOptions.forEach((item) => {
      if (item && item.typeId) {
        attributeFilterPopoverVisible[item.typeId] = false;
      }
    });
  }
}

// 清除筛选条件
function clearFilter(filterType) {
  const newQueryParam = { ...props.queryParam };
  
  if (filterType === 'followers') {
    delete newQueryParam.minFollowers;
    delete newQueryParam.maxFollowers;
    minFollowers.value = null;
    maxFollowers.value = null;
    selectedFollowersRanges.value = [];
  } else if (filterType === 'video') {
    delete newQueryParam.minVideoStandardCount;
    delete newQueryParam.maxVideoStandardCount;
    minVideoCount.value = null;
    maxVideoCount.value = null;
    selectedVideoRanges.value = [];
  }
  
  emit('update:queryParam', newQueryParam);
  emit('search', newQueryParam);
}

// 清除属性筛选
function clearAttributeFilter(attributeType) {
  // 清除特定属性类型的数据
  attributeIdsMap[attributeType] = [];

  // 重新计算attributeIds
  let allAttributeIds = [];
  Object.keys(attributeIdsMap).forEach((typeId) => {
    if (attributeIdsMap[typeId] && Array.isArray(attributeIdsMap[typeId])) {
      const flatIds = Array.isArray(attributeIdsMap[typeId])
        ? attributeIdsMap[typeId].flat()
        : [];
      allAttributeIds = allAttributeIds.concat(flatIds);
    }
  });

  const uniqueAttributeIds = [...new Set(allAttributeIds)];
  
  const newQueryParam = { 
    ...props.queryParam,
    attributeIds: uniqueAttributeIds.length > 0 ? uniqueAttributeIds.join(',') : undefined
  };
  
  emit('update:queryParam', newQueryParam);
  emit('search', newQueryParam);
}

// 清除所有筛选条件
function clearAllFilters() {
  // 清除数据
  minFollowers.value = null;
  maxFollowers.value = null;
  selectedFollowersRanges.value = [];
  minVideoCount.value = null;
  maxVideoCount.value = null;
  selectedVideoRanges.value = [];
  
  Object.keys(attributeIdsMap).forEach((key) => {
    delete attributeIdsMap[key];
  });

  // 构建新的查询参数
  const newQueryParam = { ...props.queryParam };
  delete newQueryParam.minFollowers;
  delete newQueryParam.maxFollowers;
  delete newQueryParam.minVideoStandardCount;
  delete newQueryParam.maxVideoStandardCount;
  delete newQueryParam.attributeIds;
  
  emit('update:queryParam', newQueryParam);
  emit('search', newQueryParam);
}

// 解析粉丝数范围字符串，返回具体的数值范围
function parseFollowersRange(range) {
  switch (range) {
    case '1K-10K':
      return { min: 1, max: 10 };
    case '10K-20K':
      return { min: 10, max: 20 };
    case '20K-50K':
      return { min: 20, max: 50 };
    case '50K-100K':
      return { min: 50, max: 100 };
    case '100K+':
      return { min: 100, max: null }; // 不设上限
    default:
      return { min: null, max: null };
  }
}

// 主筛选弹窗显示状态管理
function onMainPopoverShow() {
  mainFilterPopoverVisible.value = true;

  // 第一次打开时默认选中左侧菜单第一个选项
  if (!filterType.value && props.filterOptions.length > 0) {
    // 如果有动态属性类型，选中第一个
    const firstOption = props.filterOptions[0];
    filterType.value = firstOption.typeId;
    // 加载对应的数据
    options.value = [...firstOption.data].map((item) => {
      return {
        id: item.id,
        attributeName: item.attributeName,
      };
    });
    // 恢复该筛选条件的已保存数据
    restoreFilterData(firstOption.typeId);
  } else if (!filterType.value) {
    // 如果没有动态属性类型，默认选中粉丝数
    filterType.value = 'followersNum';
    // 恢复粉丝数数据
    restoreFilterData('followersNum');
  }
}

function onMainPopoverHide() {
  mainFilterPopoverVisible.value = false;
  // 重置 filterType 和 options，确保下次打开时能重新应用默认选择逻辑
  filterType.value = '';
  options.value = [];
}

// 粉丝数筛选弹窗显示状态管理
function onFollowersPopoverShow() {
  followersFilterPopoverVisible.value = true;
}

function onFollowersPopoverHide() {
  followersFilterPopoverVisible.value = false;
}

// 有效视频筛选弹窗显示状态管理
function onVideoPopoverShow() {
  videoFilterPopoverVisible.value = true;
}

function onVideoPopoverHide() {
  videoFilterPopoverVisible.value = false;
}

// 属性筛选弹窗显示状态管理
function onAttributePopoverShow(typeId) {
  attributeFilterPopoverVisible[typeId] = true;
}

function onAttributePopoverHide(typeId) {
  attributeFilterPopoverVisible[typeId] = false;
}

// 监听筛选选项变化
watch(
  () => props.filterOptions,
  (newVal) => {
    if (newVal && newVal.length > 0 && !filterType.value) {
      // 默认选中第一个
      filterType.value = newVal[0].typeId;
    }
  },
  { immediate: true }
);
</script>

<style scoped lang="less">
.rotating-arrow-filter-icon {
  color: rgba(0, 0, 0, 0.25);
  /* 启用过渡动画 */
  transition: transform 0.3s ease;
  /* 定义变换的中心点为图标中心 */
  transform-origin: center center;
}

/* 当 mainFilterPopoverVisible 为 true 时，图标旋转 180 度 */
.main-filter-open .rotating-arrow-filter-icon {
  transform: rotate(180deg);
}

/* 当 mainFilterPopoverVisible 为 false 时，图标不旋转 (0 度) */
.main-filter-closed .rotating-arrow-filter-icon {
  transform: rotate(0deg);
}

:deep(.input-num .ant-input-number-handler-wrap) {
  display: none !important;
}

.more-filter-popover {
  padding: 0 !important;
}

.more-filter-container {
  display: flex;
  flex-direction: column;
  
  .more-filter-content {
    flex: 1;
    display: flex;
    overflow-y: auto;
    
    .more-filter-form {
      width: 100%;
      overflow-x: auto;
      
      .filter-form-select {
        height: 180px;
        width: 100%;
        overflow-y: auto;
        border-bottom: 1px solid #d9d9d9;
        
        .filter-form-select_item {
          color: #121415;
          cursor: pointer;
          font-size: 14px;
          line-height: 22px;
          list-style: none;
          padding: 6px 12px;

          &:hover,
          &.selected {
            background-color: #f2f8ff;
          }
        }
      }
      
      .range-input {
        height: auto;
        flex-direction: column;
        display: flex;
        justify-content: center;
        padding: 12px;
        gap: 10px;
        
        .range-input__content {
          align-items: flex-end;
          display: flex;
          
          .range-input__flex {
            flex: 1;
            
            .range-input__label {
              margin-bottom: 4px;
              color: #8a8a8a;
              font-size: 12px;
            }
          }
          
          .range-input__sperate {
            margin-bottom: 6px;
            margin-left: 8px;
            margin-right: 8px;
          }
        }
      }
    }
    
    .more-filter-content_menu {
      width: 200px;
      height: 100%;
      border-right: 1px solid #d9d9d9;
      
      .more-filter-content_menu_item {
        width: 100%;
        display: flex;
        align-items: center;
        color: #121415;
        cursor: pointer;
        display: flex;
        justify-content: space-between;
        padding: 6px 12px;
        transition: all;

        &:hover,
        &.selected {
          background-color: #f2f8ff;
        }
      }
    }
  }
  
  .more-filter-footer {
    align-items: center;
    border-top: 1px solid #d9d9d9;
    display: flex;
    justify-content: flex-end;
    padding: 8px 16px;
    padding-bottom: 0px;
    padding-top: 12px;
  }
}

/* 选中个数样式 */
.selected-count {
  display: inline-block;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  background-color: #1890ff;
  color: white;
  border-radius: 8px;
  font-size: 12px;
  margin-right: 8px;
  padding: 0 4px;
}

.more-filter-button span:first-child {
  color: #969696;
}

.more-filter-button span:last-child {
  margin-left: 8px;
}

.more-filter-button {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 15px;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  height: 32px;
  line-height: 32px;
  font-size: 12px;
  background-color: #fff;
  text-align: center;
}

:deep(.more-filter-button span) {
  display: inline-block !important;
  max-width: 350px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}

:deep(.more-filter-button span:nth-child(2)) {
  font-weight: 600;
}

.clear-all-button {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 15px;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  height: 32px;
  line-height: 32px;
  font-size: 12px;
  background-color: #fff;
  text-align: center;
  
  span {
    display: inline-block !important;
    max-width: 350px !important;
    overflow: hidden !important;
    text-overflow: ellipsis !important;
    white-space: nowrap !important;
    line-height: 32px;
    font-weight: 600;
  }
}

:deep(.el-cascader-menu) {
  width: 100% !important;
}

:deep(.el-cascader-panel) {
  height: 100% !important;
  width: 100% !important;
  border: none !important;
  height: 291px !important;
}

:deep(.el-cascader-menu__wrap) {
  height: 100% !important;
}
</style>