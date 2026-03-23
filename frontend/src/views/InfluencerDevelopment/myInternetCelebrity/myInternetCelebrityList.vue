<template>
  <div class="myInternetCelebrityList">
    <div class="searchForm">
    
      <a-form @keyup.enter="handleSearch" >
        <a-row :gutter="12">
          <a-col :xl="3" :lg="6" :md="8" :sm="24">
            <a-form-item>
              <JDictSelectPlatformType @change="onPlatformTypeChange" v-model:value="queryParam.platformType" dictCode="platform_type" />
            </a-form-item>
          </a-col>
          <a-col :xl="searchType == '0' ? 14 : 6" :lg="24" :md="24" :sm="24">
            <a-form-item>
              <a-input-group  compact>
                <a-select
                  style="width: 100px;border-right: 0px;"
                  v-model:value="searchType"
                  @change="onSearchTypeChange"
                >
                  <a-select-option value="0"> 产品 </a-select-option>
                  <a-select-option value="1"> 产品类目 </a-select-option>
                </a-select>
                <template v-if="searchType == '0'">
                  <a-select
                  :disabled="
                        queryParam.attributeIds ||
                        (queryParam.tagNameList && queryParam.tagNameList.length > 0)
                          ? true
                          : false
                      "
                    style="width: 100px;border-right: 0px;"
                    show-search
                    allowClear
                    option-filter-prop="label"
                    v-model:value="queryParam.brandId"
                    placeholder="品牌"
                    :dropdownMatchSelectWidth="false"
                    :dropdownStyle="{ width: '150px' }"
                    @change="onBrandChange"
                  >
                    <a-select-option v-for="item in brandList" :key="item.id" :label="item.brandName">{{
                      item.brandName
                    }}</a-select-option>
                  </a-select>
                  <a-select
                  :disabled="
                        queryParam.attributeIds ||
                        (queryParam.tagNameList && queryParam.tagNameList.length > 0)
                          ? true
                          : false
                      "
                    style="width: 150px;border-right: 0px;"
                    show-search
                    option-filter-prop="label"
                    v-model:value="queryParam.productId"
                    @change="onProductChange"
                    placeholder="产品"
                    allowClear
                    :dropdownMatchSelectWidth="false"
                    :dropdownStyle="{ width: '350px' }"
                  >
                    <a-select-option
                      v-for="item in productList"
                      :key="item.id"
                      :value="item.id"
                      :label="item.text"
                    >
                      {{ item.text }}
                    </a-select-option>
                  </a-select>
                  <a-select
                  
                      :disabled="
                        queryParam.attributeIds ||
                        (queryParam.tagNameList && queryParam.tagNameList.length > 0)
                          ? true
                          : false
                      "
                      class="productAttribute"
                      mode="multiple"
                      style="width: calc(100% - 350px)"
                      show-search
                      option-filter-prop="label"
                      v-model:value="queryParam.productAttributeIds"
                      placeholder="达人类型"
                      allowClear
                      :dropdownMatchSelectWidth="false"
                      :dropdownStyle="{ width: '350px' }"
                      :maxTagCount="6"
                    >
                      <template #maxTagPlaceholder>
                        <a-tooltip>
                          <template #title>
                            <div style="max-height: 200px; overflow-y: auto">
                              <div
                                v-for="(a, i) in queryParam.productAttributeIds.slice(6)"
                                :key="i"
                              >
                                <div>
                                  {{
                                    productAttributeList.find((item) => item.id == a)
                                      .attributeName
                                  }}
                                </div>
                              </div>
                            </div>
                          </template>
                          <span
                            v-if="
                              queryParam.productAttributeIds &&
                              queryParam.productAttributeIds.length > 6
                            "
                            >+
                            {{ queryParam.productAttributeIds.length - 6 }}
                          </span>
                        </a-tooltip>
                      </template>
                      <a-select-option
                        v-for="item in productAttributeList"
                        :key="item.id"
                        :value="item.id"
                        :label="item.attributeName"
                      >
                        {{ item.attributeName }}
                      </a-select-option>
                    </a-select>
                </template>
                <a-tree-select
                  style="width:  calc(100% - 100px)"
                  v-if="searchType == '1'"
                  @change="onCategoryChange"
                  v-model:value="queryParam.categoryIds"
                  show-search
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="产品类目"
                  allow-clear
                  :treeData="productCategoryList"
                  :replaceFields="{ label: 'categoryName', key: 'id', value: 'id' }"
                    tree-node-filter-prop="categoryName"
                ></a-tree-select>
              </a-input-group>
            </a-form-item> 
          </a-col>
          <a-col :xl="3" :lg="7" :md="8" :sm="24" class="isContactCol">
            <a-form-item>
            
              <a-radio-group v-model:value="queryParam.isContact" @change="changeRadio" >
                <a-radio-button :value="0"> 未开发 </a-radio-button>
                <a-radio-button :value="1"> 已开发 </a-radio-button>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                v-model:value="queryParam.countryCode"
                show-search
                option-filter-prop="label"
                allowClear
                placeholder="国家"
              >
                <a-select-option
                  v-for="(item, key) in countrys"
                  :key="key"
                  :value="item.shortCode"
                  :label="item.country"
                >
                  {{ item.country }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                v-model:value="queryParam.isPrivateKol"
                allowClear
                placeholder="是否私有网红"
              >
                <a-select-option :value="1"> 是 </a-select-option>
                <a-select-option :value="0"> 否 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model:value="queryParam.isHasEmail">
                <a-select-option value=" "> 全部账号 </a-select-option>
                <a-select-option :value="1"> 含邮箱账号 </a-select-option>
                <a-select-option :value="0"> 不含邮箱账号 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col
            :xl="5"
            :lg="7"
            :md="8"
            :sm="24"
            class="allotDateRangeradio"
            style="max-width: 21%"
          >
            <a-form-item>
              <a-radio-group  v-model:value="allotDateRadio" >
                <a-radio-button
                  v-for="item in defaultRange"
                  :key="item.value"
                  :value="item.value"
                >
                  {{ item.title }}
                </a-radio-button>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col
            v-if="allotDateRadio == '自定义'"
            :xl="5"
            :lg="7"
            :md="8"
            :sm="24"
            class="allotDateRange"
          >
            <a-form-item>
              <a-input-group compact>
                <a-input
                  class="allotDateRange_input"
                  style="width: 98px;color: #0b1019"
                  disabled
                  default-value="最后分配日期"
                />
                <a-range-picker
                  @change="allotDateRangeChange"
                  style="width: calc(100% - 98px)"
                  v-model:value="queryParam.allotDateRange"
                />
              </a-input-group>
            </a-form-item>
          </a-col>
          <a-col :xl="8" :lg="17" :md="18" :sm="24">
            <a-form-item>
              <span style="overflow: hidden; float: left; white-space: nowrap">
              
                
                <el-popover
                  ref="mainFilterPopoverRef"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  :width="650"
                  trigger="click"
                  @show="onMainPopoverShow"
                  @hide="onMainPopoverHide"
                >
                  <template #reference>
                    <a-button
                      :class="mainFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'"
                      @click="moreFilter"
                      
                    >
                      更多筛选
                      <DownOutlined class="rotating-arrow-filter-icon" />
                    </a-button>
                  </template>
                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-content_menu">
                        <div
                          class="more-filter-content_menu_item"
                          :class="{
                            selected: filterType === item.value || filterType === item.typeId,
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
                            <RightOutlined style="color: #606266" />
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
                            <RightOutlined style="color: #606266" />
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
                            <RightOutlined style="color: #606266" />
                          </span>
                        </div>
                        <div
                          class="more-filter-content_menu_item"
                          :class="{ selected: filterType === 'medianViews' }"
                          @click="selectFilterOther('medianViews')"
                        >
                          <span>视频中位数</span>
                          <span>
                            <span v-if="getMedianViewsCount() > 0" class="selected-count">
                              {{ getMedianViewsCount() }}
                            </span>
                            <RightOutlined style="color: #606266" />
                          </span>
                        </div>
                        <div
                          class="more-filter-content_menu_item"
                          :class="{ selected: filterType === 'playAvg' }"
                          @click="selectFilterOther('playAvg')"
                        >
                          <span>均播数</span>
                          <span>
                            <span v-if="getPlayAvgCount() > 0" class="selected-count">
                              {{ getPlayAvgCount() }}
                            </span>
                            <RightOutlined style="color: #606266" />
                          </span>
                        </div>
                        <div
                          class="more-filter-content_menu_item"
                          :class="{ selected: filterType === 'tag' }"
                          @click="selectFilterOther('tag')"
                        >
                          <span>标签</span>
                          <span>
                            <span v-if="getTagCount() > 0" class="selected-count">
                              {{ getTagCount() }}
                            </span>
                            <RightOutlined style="color: #606266" />
                          </span>
                        </div>
                        <div
                          class="more-filter-content_menu_item"
                          :class="{ selected: filterType === 'account' }"
                          @click="selectFilterOther('account')"
                        >
                          <span>账号</span>
                          <span>
                            <span v-if="getAccountCount() > 0" class="selected-count">
                              {{ getAccountCount() }}
                            </span>
                            <RightOutlined style="color: #606266" />
                          </span>
                        </div>
                        <div
                          class="more-filter-content_menu_item"
                          :class="{ selected: filterType === 'email' }"
                          @click="selectFilterOther('email')"
                        >
                          <span>邮箱</span>
                          <span>
                            <span v-if="getEmailCount() > 0" class="selected-count">
                              {{ getEmailCount() }}
                            </span>
                            <RightOutlined style="color: #606266" />
                          </span>
                        </div>
                      </div>
                      <!-- 筛选表单内容 -->
                      <div class="more-filter-form" v-if="filterType &&
  filterType !== 'followersNum' && filterType !== 'effectiveVideoNum' && filterType !== 'medianViews' && filterType !== 'playAvg' && filterType !== 'tag' && filterType !== 'account' && filterType !== 'email'">
                        <el-cascader-panel
                          v-model="attributeIdsMap[filterType]"
                          :options="options"
                          :props="cascaderProps"
                        />
                      </div>
                      <!-- 粉丝数筛选 -->
                      <div class="more-filter-form" v-if="filterType === 'followersNum'">
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
                            :class="{ selected: selectedFollowersRanges.includes('10K-20K') }"
                            @click="selectFollowersRange('10K-20K')"
                          >
                            10K-20K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedFollowersRanges.includes('20K-50K') }"
                            @click="selectFollowersRange('20K-50K')"
                          >
                            20K-50K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedFollowersRanges.includes('50K-100K') }"
                            @click="selectFollowersRange('50K-100K')"
                          >
                            50K-100K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedFollowersRanges.includes('100K+') }"
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
                                  :precision="0"
                                  :max="99999"
                                  v-model:value="minFollowers"
                                  placeholder="输入值"
                                  @change="onFollowersInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >K</span>
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
                                  :precision="0"
                                  :max="99999"
                                  v-model:value="maxFollowers"
                                  placeholder="输入值"
                                  @change="onFollowersInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >K</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 有效视频筛选 -->
                      <div class="more-filter-form" v-if="filterType === 'effectiveVideoNum'">
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
                                  v-model:value="minVideoCount"
                                  placeholder="输入值"
                                  @change="onVideoInputChange"
                                />
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
                                  v-model:value="maxVideoCount"
                                  placeholder="输入值"
                                  @change="onVideoInputChange"
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 视频中位数筛选 -->
                      <div class="more-filter-form" v-if="filterType === 'medianViews'">
                        <div class="filter-form-select">
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('1K-10K') }"
                            @click="selectMedianViewsRange('1K-10K')"
                          >
                            1K-10K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('10K-20K') }"
                            @click="selectMedianViewsRange('10K-20K')"
                          >
                            10K-20K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('20K-50K') }"
                            @click="selectMedianViewsRange('20K-50K')"
                          >
                            20K-50K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('50K-100K') }"
                            @click="selectMedianViewsRange('50K-100K')"
                          >
                            50K-100K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('100K+') }"
                            @click="selectMedianViewsRange('100K+')"
                          >
                            100K+
                          </div>
                        </div>
                        <div class="range-input">
                          <div class="range-input__custom-label">自定义范围</div>
                          <div class="range-input__content">
                            <div class="range-input__flex">
                              <div class="range-input__label">最小视频中位数</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  class="input-num"
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  v-model:value="minMedianViews"
                                  placeholder="输入值"
                                  @change="onMedianViewsInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >K</span>
                              </div>
                            </div>
                            <div class="range-input__sperate">-</div>
                            <div class="range-input__flex">
                              <div class="range-input__label">最大视频中位数</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  class="input-num"
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  v-model:value="maxMedianViews"
                                  placeholder="输入值"
                                  @change="onMedianViewsInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >K</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 均播数筛选 -->
                      <div class="more-filter-form" v-if="filterType === 'playAvg'">
                        <div class="filter-form-select">
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedPlayAvgRanges.includes('0-10K') }"
                            @click="selectPlayAvgRange('0-10K')"
                          >
                            0-10K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedPlayAvgRanges.includes('10K-30K') }"
                            @click="selectPlayAvgRange('10K-30K')"
                          >
                            10K-30K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedPlayAvgRanges.includes('30K-100K') }"
                            @click="selectPlayAvgRange('30K-100K')"
                          >
                            30K-100K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedPlayAvgRanges.includes('100K-300K') }"
                            @click="selectPlayAvgRange('100K-300K')"
                          >
                            100K-300K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedPlayAvgRanges.includes('300K+') }"
                            @click="selectPlayAvgRange('300K+')"
                          >
                            300K+
                          </div>
                        </div>
                        <div class="range-input">
                          <div class="range-input__custom-label">自定义范围</div>
                          <div class="range-input__content">
                            <div class="range-input__flex">
                              <div class="range-input__label">最小均播数</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  class="input-num"
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  v-model:value="minPlayAvg"
                                  placeholder="输入值"
                                  @change="onPlayAvgInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >K</span>
                              </div>
                            </div>
                            <div class="range-input__sperate">-</div>
                            <div class="range-input__flex">
                              <div class="range-input__label">最大均播数</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  class="input-num"
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  v-model:value="maxPlayAvg"
                                  placeholder="输入值"
                                  @change="onPlayAvgInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >K</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 标签筛选 -->
                      <div class="more-filter-form" v-if="filterType === 'tag'">
                        <div class="range-input">
                          <div>
                            <a-radio-group v-model:value="tagType">
                              <a-radio-button
                                v-for="item in filterTagTypeList"
                                :key="item.value"
                                :value="item.value"
                              >
                                {{ item.title }}
                              </a-radio-button>
                            </a-radio-group>
                          </div>
                          <div style="display: flex; align-items: center">
                            <a-select
                              :disabled="
                                queryParam.brandId ||
                                queryParam.productId ||
                                (queryParam.productAttributeIds &&
                                  queryParam.productAttributeIds.length > 0) ||
                                queryParam.attributeIds 
                                  ? true
                                  : false
                              "
                              style="width: 100%; flex: 1"
                              :autoClearSearchValue="false"
                              :not-found-content="fetching ? undefined : null"
                              @search="fetchUserDebounced"
                              :dropdownStyle="{
                              
                                maxHeight: '250px'
                              }"
                              :getPopupContainer="getPopupContainer"
                              @change="handleChange"
                              :filter-option="false"
                          
                              mode="multiple"
                              v-model:value="tagNameList"
                              allowClear
                              placeholder="标签"
                              @popup-scroll="tagPopupScroll"
                            >
                            
                              <template v-if="fetching" #notFoundContent>
                                <a-spin size="small" />
                              </template>
                              <a-select-option
                                v-for="(tag, index) in tags"
                                :key="index"
                                :value="tag.tagName"
                              >
                                {{ tag.tagName }}
                              </a-select-option>
                              <!-- 虚拟滚动加载更多指示器 -->
                              <a-select-option
                                v-if="tagLoading && hasMoreTags"
                                key="loading"
                                disabled
                              >
                                <a-spin size="small" />
                                <span style="margin-left: 8px">加载中...</span>
                              </a-select-option>
                            </a-select>
                            <AlignLeftOutlined
                              v-if="
                                !(
                                  queryParam.brandId ||
                                  queryParam.productId ||
                                  (queryParam.productAttributeIds &&
                                    queryParam.productAttributeIds.length > 0)
                                )
                              "
                              style="
                                margin-left: 8px;
                                width: 24px;
                                height: 24px;
                                background-color: #f0f2f5;
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                cursor: pointer;
                              "
                              :style="{ color: tagVisible ? 'blue' : '' }"
                              @click="tagVisible = !tagVisible"
                            />
                          </div>
                          <div v-if="tagVisible">
                            <div style="display: flex; gap: 8px">
                              <a-textarea
                                style="flex: 1"
                                v-model:value="tagValue"
                                :auto-size="{ minRows: 6, maxRows: 6 }"
                                placeholder="精确输入，一行一项"
                              />
                              <div
                                style="color: red; flex: 1; max-height: 156px; overflow-y: auto"
                                v-if="noTag"
                              >
                                不存在的：
                                <div
                                  style="margin-right: 5px"
                                  v-for="item in nonExistentTag"
                                  :key="item"
                                >
                                  {{ item }}
                                </div>
                              </div>
                            </div>
                            <div
                              style="
                                margin-top: 10px;
                                display: flex;
                                justify-content: space-between;
                              "
                            >
                              <div>
                                <a-button @click="closeTagEdit" style="margin-right: 50px">
                                  关闭 
                                </a-button>
                              </div>
                              <div>
                                <a-button @click="clear" style="margin-right: 8px">清空</a-button>
                                <a-button type="primary" @click="tagSerch(tagValue)">
                                  确定
                                </a-button>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 账号筛选 -->
                      <div class="more-filter-form" v-if="filterType === 'account'">
                        <div class="range-input">
                          <div style="display: flex; align-items: center">
                            <a-textarea
                              style="flex: 1"
                              v-model:value="accountValue"
                              :auto-size="{ minRows: 13, maxRows: 13 }"
                              placeholder="精确输入，一行一项"
                            />
                          </div>
                        </div>
                      </div>
                      <!-- 邮箱筛选 -->
                      <div class="more-filter-form" v-if="filterType === 'email'">
                        <div class="range-input">
                          <div style="display: flex; align-items: center">
                            <a-textarea
                              style="flex: 1"
                              v-model:value="emailValue"
                              :auto-size="{ minRows: 13, maxRows: 13 }"
                              placeholder="精确输入，一行一项"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="more-filter-footer">
                      <a-button
                        type="primary"
                        style="margin-right: 8px"
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                      >
                        应用
                      </a-button>
                      <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)">查询</a-button>
                    </div>
                  </div>
                </el-popover>
              
                <a-button
                  type="primary"
                  @click="handleSearch"
                  :icon="h(SearchOutlined)"
                  style="margin-left: 8px"
                  ></a-button
                >
                <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px"
                  ></a-button
                >
                <a-button
                  :disabled="
                    queryParam.includeBlacklist == 1 || selectedRowKeys.length == 0
                  "
                  :icon="h(PushpinOutlined)"
                  :loading="btnLoading"
                  type="primary"
                  @click="markerDeveloping"
                  style="margin-left: 8px"
                  >标记开发</a-button
                >
                <a-dropdown
                  :disabled="
                    queryParam.includeBlacklist == 1 || selectedRowKeys.length == 0
                  "
                >
                  <a-button
                    :disabled="
                      queryParam.includeBlacklist == 1 || selectedRowKeys.length == 0
                    "
                    type="primary"
                    style="margin-left: 8px"
                  >
                    批量操作 <DownOutlined
                  /></a-button>
                  <template #overlay>
                    <a-menu >
                      <a-menu-item
                        @click="privatelyOwned"
                        :disabled="
                          queryParam.includeBlacklist == 1 || selectedRowKeys.length == 0
                        "
                      >
                        <span style="text-align: center; display: block">一键私有</span>
                      </a-menu-item>
                      <a-menu-item
                        @click="handleExportXls('我的网红')"
                        :disabled="
                          queryParam.includeBlacklist == 1 || selectedRowKeys.length == 0
                        "
                      >
                        <span style="text-align: center; display: block">批量导出</span>
                      </a-menu-item>
                      <a-menu-item
                        :disabled="
                          queryParam.includeBlacklist == 1 || selectedRowKeys.length == 0
                        "
                        @click="screenBtn"
                      >
                        <span style="text-align: center; display: block">一键屏蔽</span>
                      </a-menu-item>
                      <a-menu-item
                        :disabled="
                          queryParam.includeBlacklist == 1 || selectedRowKeys.length == 0
                        "
                        @click="sendEmail"
                      >
                        <span style="text-align: center; display: block">发送邮件</span>
                      </a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
              </span>
            </a-form-item>
          </a-col>
        </a-row>
        <!-- 第二行：显示已选择的筛选条件 -->
        <a-row :gutter="12" v-if="hasActiveFilters">
          <a-col :span="24" style="margin-bottom: 8px">
            <span style="display: flex; flex-wrap: wrap; gap: 8px">
              <!-- 显示已选筛选条件的标签 -->
              <!-- 均播数筛选器 -->
              <el-popover
                ref="playAvgFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                trigger="click"
                @show="onPlayAvgPopoverShow"
                @hide="onPlayAvgPopoverHide"
              >
                <template #reference>
                  <div
                    v-show="queryParam.minPlayAvgNum || queryParam.maxPlayAvgNum"
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span> 均播数： </span>
                    <span v-if="queryParam.minPlayAvgNum && queryParam.maxPlayAvgNum">
                      {{ queryParam.minPlayAvgNum }} - {{ queryParam.maxPlayAvgNum }}
                    </span>
                    <span v-else-if="queryParam.minPlayAvgNum && !queryParam.maxPlayAvgNum">
                      ≥ {{ queryParam.minPlayAvgNum }}
                    </span>
                    <span v-else-if="!queryParam.minPlayAvgNum && queryParam.maxPlayAvgNum">
                      ≤ {{ queryParam.maxPlayAvgNum }}
                    </span>
                    <span
                      style="margin-left: 6px"
                      :class="
                        playAvgFilterPopoverVisible
                          ? 'main-filter-open'
                          : 'main-filter-closed'
                      "
                    >
                      <DownOutlined class="rotating-arrow-filter-icon" />
                    </span>
                    <span>
                      <CloseCircleOutlined
                        @click="clearFilterData($event, 'playAvg')"
                        @mousedown.stop.prevent
                        theme="filled"
                      />
                    </span>
                  </div>
                </template>

                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="filter-form-select">
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedPlayAvgRanges.includes('0-10K') }"
                          @click="selectPlayAvgRange('0-10K')"
                        >
                          0-10K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedPlayAvgRanges.includes('10K-30K') }"
                          @click="selectPlayAvgRange('10K-30K')"
                        >
                          10K-30K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedPlayAvgRanges.includes('30K-100K') }"
                          @click="selectPlayAvgRange('30K-100K')"
                        >
                          30K-100K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedPlayAvgRanges.includes('100K-300K') }"
                          @click="selectPlayAvgRange('100K-300K')"
                        >
                          100K-300K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedPlayAvgRanges.includes('300K+') }"
                          @click="selectPlayAvgRange('300K+')"
                        >
                          300K+
                        </div>
                      </div>
                      <div class="range-input">
                        <div class="range-input__custom-label">自定义范围</div>
                        <div class="range-input__content">
                          <div class="range-input__flex">
                            <div class="range-input__label">最小均播数</div>
                            <div class="range-input__input" style="position: relative">
                              <a-input-number
                                style="width: 100%"
                                :min="0"
                                :max="99999"
                                v-model:value="minPlayAvg"
                                placeholder="输入值"
                                @change="onPlayAvgInputChange"
                              />
                              <span
                                style="
                                  position: absolute;
                                  top: 50%;
                                  z-index: 2;
                                  display: flex;
                                  align-items: center;
                                  color: rgba(0, 0, 0, 0.65);
                                  line-height: 0;
                                  transform: translateY(-50%);
                                  right: 12px;
                                "
                                class="ant-input-suffix"
                              >
                                K
                              </span>
                            </div>
                          </div>
                          <div class="range-input__sperate">-</div>
                          <div class="range-input__flex">
                            <div class="range-input__label">最大均播数</div>
                            <div class="range-input__input" style="position: relative">
                              <a-input-number
                                style="width: 100%"
                                :min="0"
                                :max="99999"
                                v-model:value="maxPlayAvg"
                                placeholder="输入值"
                                @change="onPlayAvgInputChange"
                              />
                              <span
                                style="
                                  position: absolute;
                                  top: 50%;
                                  z-index: 2;
                                  display: flex;
                                  align-items: center;
                                  color: rgba(0, 0, 0, 0.65);
                                  line-height: 0;
                                  transform: translateY(-50%);
                                  right: 12px;
                                "
                                class="ant-input-suffix"
                              >
                                K
                              </span>
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
                      :icon="h(SafetyCertificateOutlined)"
                      @click="applyAllFilters(false)"
                    >
                      应用
                    </a-button>
                    <a-button
                      type="primary"
                      :icon="h(SearchOutlined)"
                      @click="applyAllFilters(true)"
                    >
                      查询
                    </a-button>
                  </div>
                </div>
              </el-popover>
                <el-popover
                  ref="emailFilterPopover"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  trigger="click"
                  @show="onEmailPopoverShow"
                  @hide="onEmailPopoverHide"
                >
                  <template #reference>
                    <div
                      v-show="queryParam.emailKeyword && queryParam.emailKeyword"
                      class="more-filter-button"
                      style="margin-right: 8px"
                    >
                      <span>邮箱：</span>
                      <span>
                        {{ queryParam.emailKeyword ? queryParam.emailKeyword : "" }}
                      </span>
                      <span
                        style="margin-left: 6px"
                        :class="
                          emailFilterPopoverVisible
                            ? 'main-filter-open'
                            : 'main-filter-closed'
                        "
                      >
                        <DownOutlined class="rotating-arrow-filter-icon" />
                      </span>
                      <span>
                        <CloseCircleOutlined
                          @click="clearFilterData($event, 'email')"
                          @mousedown.stop.prevent
                          theme="filled"
                        />
                      </span>
                    </div>
                  </template>

                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-form">
                        <div class="range-input">
                          <div style="display: flex; align-items: center">
                            <a-textarea
                              style="flex: 1"
                              v-model:value="emailValue"
                              :auto-size="{ minRows: 13, maxRows: 13 }"
                              placeholder="精确输入，一行一项"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="more-filter-footer">
                      <a-button
                        type="primary"
                        style="margin-right: 8px"
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                      >
                        应用
                      </a-button>
                      <a-button
                        type="primary"
                        :icon="h(SearchOutlined)"
                        @click="applyAllFilters(true)"
                      >
                        查询
                      </a-button>
                    </div>
                  </div>
                </el-popover>
                <el-popover
                  ref="accountFilterPopover"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  trigger="click"
                  @show="onAccountPopoverShow"
                  @hide="onAccountPopoverHide"
                >
                  <template #reference>
                    <div
                      v-show="queryParam.uniqueId && queryParam.uniqueId"
                      class="more-filter-button"
                      style="margin-right: 8px"
                    >
                      <span>账号：</span>
                      <span>
                        {{ queryParam.uniqueId ? queryParam.uniqueId : "" }}
                      </span>
                      <span
                        style="margin-left: 6px"
                        :class="
                          accountFilterPopoverVisible
                            ? 'main-filter-open'
                            : 'main-filter-closed'
                        "
                      >
                        <DownOutlined class="rotating-arrow-filter-icon" />
                      </span>
                      <span>
                        <CloseCircleOutlined
                          @click="clearFilterData($event, 'uniqueId')"
                          @mousedown.stop.prevent
                          theme="filled"
                        />
                      </span>
                    </div>
                  </template>

                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-form">
                        <div class="range-input">
                          <div style="display: flex; align-items: center">
                            <a-textarea
                              style="flex: 1"
                              v-model:value="accountValue"
                              :auto-size="{ minRows: 13, maxRows: 13 }"
                              placeholder="精确输入，一行一项"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="more-filter-footer">
                      <a-button
                        type="primary"
                        style="margin-right: 8px"
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                      >
                        应用
                      </a-button>
                      <a-button
                        type="primary"
                        :icon="h(SearchOutlined)"
                        @click="applyAllFilters(true)"
                      >
                        查询
                      </a-button>
                    </div>
                  </div>
                </el-popover>
                <el-popover
                  ref="tagFilterPopover"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  trigger="click"
                  @show="onTagPopoverShow"
                  @hide="onTagPopoverHide"
                >
                  <template #reference>
                    <div
                      v-show="queryParam.tagNameList && queryParam.tagNameList.length > 0"
                      class="more-filter-button"
                      style="margin-right: 8px"
                    >
                      <span>标签：</span>
                      <span>{{
                        Array.isArray(queryParam.tagNameList)
                          ? queryParam.tagNameList.join(",")
                          : ""
                      }}</span>
                      <span
                        style="margin-left: 6px"
                        :class="
                          tagFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
                        "
                      >
                        <DownOutlined class="rotating-arrow-filter-icon" />
                      </span>
                      <span>
                        <CloseCircleOutlined
                          @click="clearFilterData($event, 'tag')"
                          @mousedown.stop.prevent
                          theme="filled"
                        />
                      </span>
                    </div>
                  </template>

                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-form">
                        <div class="range-input">
                          <div>
                            <a-radio-group v-model:value="tagType">
                              <a-radio-button
                                v-for="item in filterTagTypeList"
                                :key="item.value"
                                :value="item.value"
                              >
                                {{ item.title }}
                              </a-radio-button>
                            </a-radio-group>
                          </div>
                          <div
                            style="
                              display: flex;
                              align-items: center;
                              justify-content: space-between;
                            "
                          >
                            <a-select
                              :disabled="
                                queryParam.brandId ||
                                queryParam.productId ||
                                (queryParam.productAttributeIds &&
                                  queryParam.productAttributeIds.length > 0) ||
                                queryParam.attributeIds
                                  ? true
                                  : false
                              "
                              style="width: 100%"
                              :autoClearSearchValue="false"
                              :not-found-content="fetching ? undefined : null"
                              @search="fetchUser"
                              :dropdownStyle="{
                              
                              maxHeight: '250px'
                            }"
                              :getPopupContainer="getPopupContainer"
                              @change="handleChange"
                              :filter-option="false"
                            
                              mode="multiple"
                              v-model:value="tagNameList"
                              allowClear
                              placeholder="标签"
                              @select="handleTagSel"
                              @popup-scroll="tagPopupScroll"
                            >
                              <!-- <template #maxTagPlaceholder>
                                <a-tooltip  overlayClassName="tag-overflow-tooltip" >
                                  <template #title>
                                    <div style="max-height: 200px; overflow-y: auto">
                                      <div
                                        v-for="(item, i) in tagNameList.slice(20)"
                                        :key="i"
                                      >
                                        <div>{{ item }}</div>
                                      </div>
                                    </div>
                                  </template>
                                  <span v-if="tagNameList">
                                    + {{ tagNameList.length - 20 }}
                                  </span>
                                </a-tooltip>
                              </template> -->
                              <template v-if="fetching" #notFoundContent>
                                <a-spin size="small" />
                              </template>
                              <a-select-option
                                v-for="(tag, index) in tags"
                                :key="index"
                                :value="tag.tagName"
                              >
                                {{ tag.tagName }}
                              </a-select-option>

                              <!-- 虚拟滚动加载更多指示器 -->
                              <a-select-option
                                v-if="tagLoading && hasMoreTags"
                                key="loading"
                                disabled
                              >
                                <a-spin size="small" />
                                <span style="margin-left: 8px">加载中...</span>
                              </a-select-option>
                            </a-select>
                            <AlignLeftOutlined
                              v-if="
                                !(
                                  queryParam.brandId ||
                                  queryParam.productId ||
                                  (queryParam.productAttributeIds &&
                                    queryParam.productAttributeIds.length > 0)
                                )
                              "
                              style="
                                margin-left: 8px;
                                width: 24px;
                                height: 24px;
                                background-color: #f0f2f5;
                                display: flex;
                                justify-content: center;
                                align-items: center;
                              "
                              :style="{ color: tagVisible ? 'blue' : '' }"
                              @click="tagVisible = !tagVisible"
                            />
                          </div>
                          <div v-if="tagVisible">
                            <div style="display: flex; gap: 8px">
                              <a-textarea
                                style="flex: 1"
                                v-model:value="tagValue"
                                :auto-size="{ minRows: 6, maxRows: 6 }"
                                placeholder="精确输入，一行一项"
                              />

                              <div
                                style="
                                  color: red;
                                  flex: 1;
                                  max-height: 156px;
                                  overflow-y: auto;
                                "
                                v-if="noTag"
                              >
                                不存在的：
                                <div
                                  style="margin-right: 5px"
                                  v-for="item in nonExistentTag"
                                  :key="item"
                                >
                                  {{ item }}
                                </div>
                              </div>
                            </div>
                            <div
                              style="
                                margin-top: 10px;
                                display: flex;
                                justify-content: space-between;
                              "
                            >
                              <div>
                                <a-button @click="closeTagEdit" style="margin-right: 50px">
                                  关闭
                                </a-button>
                              </div>
                              <div>
                                <a-button @click="clear" style="margin-right: 8px">
                                  清空
                                </a-button>
                                <a-button type="primary" @click="tagSerch(tagValue)">
                                  确定
                                </a-button>
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
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                      >
                        应用
                      </a-button>
                      <a-button
                        type="primary"
                        :icon="h(SearchOutlined)"
                        @click="applyAllFilters(true)"
                      >
                        查询
                      </a-button>
                    </div>
                  </div>
                </el-popover>
                <el-popover
                  ref="followersFilterPopover"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  trigger="click"
                  @show="onFollowersPopoverShow"
                  @hide="onFollowersPopoverHide"
                >
                  <template #reference>
                    <div
                      v-show="queryParam.minFollowers || queryParam.maxFollowers"
                      class="more-filter-button"
                      style="margin-right: 8px"
                    >
                      <span> 粉丝： </span>
                      <span v-if="queryParam.minFollowers && queryParam.maxFollowers">
                        {{ queryParam.minFollowers }} - {{ queryParam.maxFollowers }}
                      </span>
                      <span v-else-if="queryParam.minFollowers && !queryParam.maxFollowers">
                        ≥ {{ queryParam.minFollowers }}
                      </span>
                      <span v-else-if="!queryParam.minFollowers && queryParam.maxFollowers">
                        ≤ {{ queryParam.maxFollowers }}
                      </span>
                      <span
                        style="margin-left: 6px"
                        :class="
                          followersFilterPopoverVisible
                            ? 'main-filter-open'
                            : 'main-filter-closed'
                        "
                      >
                        <DownOutlined class="rotating-arrow-filter-icon" />
                      </span>
                      <span>
                        <CloseCircleOutlined
                          @click="clearFilterData($event, 'followers')"
                          @mousedown.stop.prevent
                          theme="filled"
                        />
                      </span>
                    </div>
                  </template>

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
                            :class="{ selected: selectedFollowersRanges.includes('10K-20K') }"
                            @click="selectFollowersRange('10K-20K')"
                          >
                            10K-20K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedFollowersRanges.includes('20K-50K') }"
                            @click="selectFollowersRange('20K-50K')"
                          >
                            20K-50K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedFollowersRanges.includes('50K-100K') }"
                            @click="selectFollowersRange('50K-100K')"
                          >
                            50K-100K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedFollowersRanges.includes('100K+') }"
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
                                  :precision="0"
                                  :max="99999"
                                  v-model:value="minFollowers"
                                  placeholder="输入值"
                                  @change="onFollowersInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >
                                  K
                                </span>
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
                                  :precision="0"
                                  :max="99999"
                                  v-model:value="maxFollowers"
                                  placeholder="输入值"
                                  @change="onFollowersInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >
                                  K
                                </span>
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
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                      >
                        应用
                      </a-button>
                      <a-button
                        type="primary"
                        :icon="h(SearchOutlined)"
                        @click="applyAllFilters(true)"
                      >
                        查询
                      </a-button>
                    </div>
                  </div>
                </el-popover>
                <el-popover
                  ref="videoFilterPopover"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  trigger="click"
                  @show="onVideoPopoverShow"
                  @hide="onVideoPopoverHide"
                >
                  <template #reference>
                    <div
                      v-show="
                        queryParam.minVideoStandardCount ||
                        queryParam.maxVideoStandardCount
                      "
                      class="more-filter-button"
                      style="margin-right: 8px"
                    >
                      <span>有效视频：</span>
                      <span
                        v-if="
                          queryParam.minVideoStandardCount &&
                          queryParam.maxVideoStandardCount
                        "
                      >
                        {{ queryParam.minVideoStandardCount }} -
                        {{ queryParam.maxVideoStandardCount }}
                      </span>
                      <span
                        v-else-if="
                          queryParam.minVideoStandardCount &&
                          !queryParam.maxVideoStandardCount
                        "
                      >
                        ≥ {{ queryParam.minVideoStandardCount }}
                      </span>
                      <span
                        v-else-if="
                          !queryParam.minVideoStandardCount &&
                          queryParam.maxVideoStandardCount
                        "
                      >
                        ≤ {{ queryParam.maxVideoStandardCount }}
                      </span>
                      <span
                        style="margin-left: 6px"
                        :class="
                          videoFilterPopoverVisible
                            ? 'main-filter-open'
                            : 'main-filter-closed'
                        "
                      >
                        <DownOutlined class="rotating-arrow-filter-icon" />
                      </span>
                      <span>
                        <CloseCircleOutlined
                          @click="clearFilterData($event, 'video')"
                          @mousedown.stop.prevent
                          theme="filled"
                        />
                      </span>
                    </div>
                  </template>

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
                                  v-model:value="minVideoCount"
                                  placeholder="输入值"
                                  @change="onVideoInputChange"
                                />
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
                                  v-model:value="maxVideoCount"
                                  placeholder="输入值"
                                  @change="onVideoInputChange"
                                />
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
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                      >
                        应用
                      </a-button>
                      <a-button
                        type="primary"
                        :icon="h(SearchOutlined)"
                        @click="applyAllFilters(true)"
                      >
                        查询
                      </a-button>
                    </div>
                  </div>
                </el-popover>
                <el-popover
                  ref="medianViewsFilterPopover"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  trigger="click"
                  @show="onMedianViewsPopoverShow"
                  @hide="onMedianViewsPopoverHide"
                >
                  <template #reference>
                    <div
                      v-show="queryParam.minMedianViews || queryParam.maxMedianViews"
                      class="more-filter-button"
                      style="margin-right: 8px"
                    >
                      <span> 视频中位数： </span>
                      <span v-if="queryParam.minMedianViews && queryParam.maxMedianViews">
                        {{ queryParam.minMedianViews }} - {{ queryParam.maxMedianViews }}
                      </span>
                      <span v-else-if="queryParam.minMedianViews && !queryParam.maxMedianViews">
                        ≥ {{ queryParam.minMedianViews }}
                      </span>
                      <span v-else-if="!queryParam.minMedianViews && queryParam.maxMedianViews">
                        ≤ {{ queryParam.maxMedianViews }}
                      </span>
                      <span
                        style="margin-left: 6px"
                        :class="
                          medianViewsFilterPopoverVisible
                            ? 'main-filter-open'
                            : 'main-filter-closed'
                        "
                      >
                        <DownOutlined class="rotating-arrow-filter-icon" />
                      </span>
                      <span>
                        <CloseCircleOutlined
                          @click="clearFilterData($event, 'medianViews')"
                          @mousedown.stop.prevent
                          theme="filled"
                        />
                      </span>
                    </div>
                  </template>

                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-form">
                        <div class="filter-form-select">
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('1K-10K') }"
                            @click="selectMedianViewsRange('1K-10K')"
                          >
                            1K-10K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('10K-20K') }"
                            @click="selectMedianViewsRange('10K-20K')"
                          >
                            10K-20K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('20K-50K') }"
                            @click="selectMedianViewsRange('20K-50K')"
                          >
                            20K-50K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('50K-100K') }"
                            @click="selectMedianViewsRange('50K-100K')"
                          >
                            50K-100K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedMedianViewsRanges.includes('100K+') }"
                            @click="selectMedianViewsRange('100K+')"
                          >
                            100K+
                          </div>
                        </div>
                        <div class="range-input">
                          <div class="range-input__custom-label">自定义范围</div>
                          <div class="range-input__content">
                            <div class="range-input__flex">
                              <div class="range-input__label">最小视频中位数</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  v-model:value="minMedianViews"
                                  placeholder="输入值"
                                  @change="onMedianViewsInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >
                                  K
                                </span>
                              </div>
                            </div>
                            <div class="range-input__sperate">-</div>
                            <div class="range-input__flex">
                              <div class="range-input__label">最大视频中位数</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  v-model:value="maxMedianViews"
                                  placeholder="输入值"
                                  @change="onMedianViewsInputChange"
                                />
                                <span
                                  style="
                                    position: absolute;
                                    top: 50%;
                                    z-index: 2;
                                    display: flex;
                                    align-items: center;
                                    color: rgba(0, 0, 0, 0.65);
                                    line-height: 0;
                                    transform: translateY(-50%);
                                    right: 12px;
                                  "
                                  class="ant-input-suffix"
                                >
                                  K
                                </span>
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
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                      >
                        应用
                      </a-button>
                      <a-button
                        type="primary"
                        :icon="h(SearchOutlined)"
                        @click="applyAllFilters(true)"
                      >
                        查询
                      </a-button>
                    </div>
                  </div>
                </el-popover>
                <template v-for="item in filterOptions" :key="item.typeId">
                  <el-popover
                    :ref="`attributeFilterPopover_${item.typeId}`"
                    popper-class="more-filter-popover"
                    placement="bottom-start"
                    trigger="click"
                    v-model:visible="attributeFilterPopoverVisible[item.typeId]"
                    @show="onAttributePopoverShow(item.typeId)"
                    @hide="onAttributePopoverHide(item.typeId)"
                  >
                    <template #reference>
                      <div
                        v-show="isShowAttribute(item.data)"
                        class="more-filter-button"
                        style="margin-right: 8px"
                      >
                        <span>{{ item.typeName }}:</span>
                        <span>{{
                          parseAttributeIds(queryParam.attributeIds, item.data)
                        }}</span>
                        <span
                          style="margin-left: 6px"
                          :class="
                            attributeFilterPopoverVisible[item.typeId]
                              ? 'main-filter-open'
                              : 'main-filter-closed'
                          "
                        >
                          <DownOutlined class="rotating-arrow-filter-icon" />
                        </span>
                        <span>
                          <CloseCircleOutlined
                            @click="clearAttributeFilter($event, item.typeId)"
                            @mousedown.stop.prevent
                            theme="filled"
                          />
                        </span>
                      </div>
                    </template>

                    <div style="height: 340px" class="more-filter-container">
                      <div class="more-filter-content">
                        <div class="more-filter-form">
                          <el-cascader-panel
                            v-model="attributeIdsMap[item.typeId]"
                            :options="item.data"
                            :props="cascaderProps"
                          />
                        </div>
                      </div>
                      <div class="more-filter-footer">
                        <a-button
                          type="primary"
                          style="margin-right: 8px"
                          :icon="h(SafetyCertificateOutlined)"
                          @click="applyAllFilters(false)"
                        >
                          应用
                        </a-button>
                        <a-button
                          type="primary"
                          :icon="h(SearchOutlined)"
                          @click="applyAllFilters(true)"
                        >
                          查询
                        </a-button>
                      </div>
                    </div>
                  </el-popover>
                </template>
              <!-- 全部清除按钮 -->
              <div class="clear-all-button" @click="clearAllFilters">
                <span>全部清除</span>
              </div>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="padding: 12px;background-color: #fff;">
    
      <s-table
        ref="tableRef"
        size="small"
        :rangeSelection="false"	
        :columns="columns"
        :dataSource="dataSource"
        :selectedRowKeys="selectedRowKeys"
        :pagination="false"
        :loading="loading"
        :row-selection="rowSelection"
        :scroll="{x: 2000, y: sTableHeight }"    
        :row-height="100"
        :yBuff="10" :xBuff="5"
        :rowKey="record => record.id" 
        :animate-rows="false"
        :custom-row="customRow"
        @change="pageChange"
      >
      <template #headerCell="{title, column}">
          
            <template v-if="column.key === 'avatar'">
              <span style="margin-left: 20px">#</span>
              <span style="margin-left: 32px">网红信息</span>
              <span style="margin-left: 26px">包含置顶</span>
              <span style="margin-left: 5px">
                <a-switch size="small" v-model:checked="isPinned" @change="changePinned"></a-switch>
              </span>
            </template>
            <template v-if="column.dataIndex === 'tagList'">
              <span style="display: flex;align-items: center;">
                <span>标签</span>
              <a-popover overlayClassName="tagTypePopover">
                <template #content>
                  <div
                    style="
                      display: flex;
                      flex-direction: column;
                      row-gap: 4px;
                      font-size: 12px;
                    "
                  >
                    <div style="margin-bottom: 4px">由以下类型组成：</div>
                    <div
                      v-for="item in tagTypeList"
                      :key="item.value"
                      :style="{ backgroundColor: tagColor(item.value).split(';')[0] }"
                      style="
                        display: flex;
                        align-items: center;
                        height: 30px;
                        padding: 0 10px;
                      "
                    >
                      <div
                        :style="{
                          width: '15px',
                          height: '15px',
                          borderRadius: '4px',
                          marginRight: '6px',
                          fontSize: '11px',
                          backgroundColor: tagColor(item.value).split(';')[1],
                        }"
                      ></div>

                      <div :style="{ color: tagColor(item.value).split(';')[1] }">
                        {{ item.title }}
                      </div>
                    </div>
                  </div>
                </template>
              
                <a-icon style="margin-left: 5px" type="question-circle" />
              </a-popover>
                </span>
            </template>
          </template>
        <template #bodyCell="{ record, column, index }">
        
          <!-- 头像列 -->
          <template v-if="column.key === 'avatar'">
            <div class="colum_img">
              <div style="width: 50px; text-align: center">{{ index + 1 }}</div>
              <div class="colum_avatarUrl">
                <a-popover  placement="top" overlayClassName="colum_avatarUrl_tooltip">
                  <template #content>
                    <img
                      v-if="record.authorAvatarUrl"
                      :src="getImage(record.authorAvatarUrl)"
                      @error="handleImageError"
                   style="max-width: 200px;height: 200px;"
                      :preview="record.id"
                    />
                    <img
                      v-else
                      src="@/assets/images/avatar.png"
                      style="width: 70px; height: 70px; margin: 0 auto"
                    />
                  </template>
                  <img
                    v-if="record.authorAvatarUrl"
                    :src="getImage(record.authorAvatarUrl)"
                  @error="handleImageError"
                    :class="{ 'error-img': record.isAbnormalAccount == 1 }"
                    style="
                      height: 70px;
                      border-radius: 100%;
                      width: 70px;
                      cursor: pointer;
                    "
                    :preview="record.id"
                  />
                  <img
                    v-else
                    :class="{ 'error-img': record.isAbnormalAccount == 1 }"
                  @error="handleImageError"
                    src="@/assets/images/avatar.png"
                    style="border-radius: 100%; width: 70px; height: 70px; margin: 0 auto"
                  />
                </a-popover>
                <img
                      v-if="record.platformType == 0"
                      class="platformType-img"
                      src="@/assets/images/ins.png"
                      alt=""
                    />
                    <img
                      v-if="record.platformType == 1"
                      class="platformType-img"
                      src="@/assets/images/yt.png"
                      alt=""
                    />
                    <img
                      v-if="record.platformType == 2"
                      class="platformType-img"
                      src="@/assets/images/tk.png"
                      alt=""
                    />
              </div>
              <div class="colum_account">
                <div style="width: 100%; white-space: nowrap">
                  <span
                    style="font-size: 14px; font-weight: 600; width: 100%;margin-right: 8px;"
                    @click.stop.prevent="openLink(record)"
                  >
                    <JEllipsis :value="record.uniqueId" :length="14"></JEllipsis>
                  </span>
                  <img :src="`https://flagcdn.com/w40/${record.country.toLowerCase()}.png`" alt="" style="margin-right: 5px;width:20px;">
                  <span>{{parseCountry(record.country)}}</span>
                </div>
                <div style="display: flex; align-items: center; white-space: nowrap;margin:8px 0px">
                  <div style="margin-right: 10px; display: flex; height: 100%;background-color: #EDF1FF;color:#3155ED;padding-left:6px;align-items: center;width: 70px;height: 22px;border-radius: 6px;">
                    <a-tooltip title="粉丝数">
                      <span
                        class="icon iconfont icon-fensishu"
                        style="font-size: 18px; margin-right: 5px"
                      ></span>
                    </a-tooltip>
                    <span style=" display: flex; align-items: center">{{
                      record.authorFollowerCount !== null && record.authorFollowerCount !== ""
                        ?  getFollower(record.authorFollowerCount)
                        : "--"
                    }}</span>
                  </div>
                  <div style="display: flex; height: 100%;background-color: #F4FFED;color:#05A300;justify-content: center;align-items: center;width: 77px;height: 22px;border-radius: 6px;">
                    <a-tooltip title="有效视频数(包含置顶)">
                      <span
                        class="icon iconfont icon-shipinbofang"
                        style="font-size: 18px; margin-right: 5px"
                      ></span>
                    </a-tooltip>
                    <span style="display: flex; align-items: center">
                      {{ record.videoStandardCount }}/{{ record.videoSampleCount }}
                    </span>
                  </div>
                </div>
                <div style="display: flex; align-items: center; white-space: nowrap">
                  <div style="display: flex">
                    <div style="display: flex; height: 100%;">
                      <span style="color:#969696">互动率：</span>
                      <a-tooltip>
                        <template #title>
                          <div v-if="record.videoData &&  isEmptyObject(record.videoData) && record.videoData !== '{}' " >
                            <div>{{ isPinned ? "包含置顶" : "不包含置顶" }}</div>
                            <template v-if="record.platformType == 1">
                              <template v-if="isPinned">
                                <div>播放数：--</div>
                                <div>收藏数：--</div>
                                <div>均播数：--</div>
                                <div>评论数：--</div>
                                <div>视频数：--</div>
                                <div>点赞数：--</div>
                                <div>分享数：--</div>
                                <div>播放区间:--</div>
                              </template>
                              <template v-else>
                                <div>
                                  播放数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_play_count
                                    )
                                  }}
                                </div>
                                <div>
                                  收藏数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_collect_count
                                    )
                                  }}
                                </div>
                                <div>
                                    均播数：{{
                                      getFollower(
                                        jsonParseTotal(record.videoData).play_avg_num
                                      )
                                    }}
                                  </div>
                                <div>
                                  评论数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_comment_count
                                    )
                                  }}
                                </div>
                                <div>
                                  视频数：{{
                                    getFollower(jsonParseTotal(record.videoData).video_count)
                                  }}
                                </div>
                                <div>
                                  点赞数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_digg_count
                                    )
                                  }}
                                </div>
                                <div>
                                  分享数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_share_count
                                    )
                                  }}
                                </div>
                                <div>
                                  播放区间：
                                  {{
                                    getFollower(
                                      jsonParseTotal(record.videoData).play_min_num
                                    )
                                  }}
                                  -
                                  {{
                                    getFollower(
                                      jsonParseTotal(record.videoData).play_max_num
                                    )
                                  }}
                                </div>
                              </template>
                            </template>
                            <template v-else>
                              <template v-if="isPinned">
                                <div>
                                  播放数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_play_count
                                    )
                                  }}
                                </div>
                                <div>
                                    均播数：{{
                                      getFollower(
                                        jsonParseTotal(record.videoData).play_avg_num
                                      )
                                    }}
                                  </div>
                                <div>
                                  收藏数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_collect_count
                                    )
                                  }}
                                </div>
                                <div>
                                  评论数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_comment_count
                                    )
                                  }}
                                </div>
                                <div>
                                  视频数：{{
                                    getFollower(jsonParseTotal(record.videoData).video_count)
                                  }}
                                </div>
                                <div>
                                  点赞数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_digg_count
                                    )
                                  }}
                                </div>
                                <div>
                                  分享数：{{
                                    getFollower(
                                      jsonParseTotal(record.videoData).video_share_count
                                    )
                                  }}
                                </div>
                                <div>
                                  播放区间：
                                  {{
                                    getFollower(
                                      jsonParseTotal(record.videoData).play_min_num
                                    )
                                  }}
                                  -
                                  {{
                                    getFollower(
                                      jsonParseTotal(record.videoData).play_max_num
                                    )
                                  }}
                                </div>
                              </template>
                              <template v-else>
                                <div>
                                  播放数：{{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData).video_play_count
                                    )
                                  }}
                                </div>
                                <div>
                                    均播数：{{
                                      getFollower(
                                        jsonParseNonPinned(record.videoData).play_avg_num
                                      )
                                    }}
                                  </div>
                                <div>
                                  收藏数：{{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData)
                                        .video_collect_count
                                    )
                                  }}
                                </div>
                                <div>
                                  评论数：{{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData)
                                        .video_comment_count
                                    )
                                  }}
                                </div>
                                <div>
                                  视频数：{{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData).video_count
                                    )
                                  }}
                                </div>
                                <div>
                                  点赞数：{{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData).video_digg_count
                                    )
                                  }}
                                </div>
                                <div>
                                  分享数：{{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData).video_share_count
                                    )
                                  }}
                                </div>
                                <div>
                                  播放区间：
                                  {{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData).play_min_num
                                    )
                                  }}
                                  -
                                  {{
                                    getFollower(
                                      jsonParseNonPinned(record.videoData).play_max_num
                                    )
                                  }}
                                </div>
                              </template>
                            </template>
                          </div>
                        </template>
                        <a
                          style="display: flex; align-items: center; text-decoration: underline;"
                          v-if="record.videoData &&  isEmptyObject(record.videoData) && record.videoData !== '{}' "
                        >
                          <template v-if="record.platformType == 1">
                            {{
                              isPinned
                                ? "--"
                                :  jsonParseTotal(
                                    record.videoData
                                  ).video_engagement_rate.toFixed(0) + "%"
                            }}
                          </template>
                          <template v-else>
                            {{
                              isPinned
                                ?  jsonParseTotal(
                                    record.videoData
                                  ).video_engagement_rate.toFixed(0)
                                :  jsonParseNonPinned(
                                    record.videoData
                                  ).video_engagement_rate.toFixed(0)
                            }}%
                          </template>
                        </a>
                      </a-tooltip>
                      <span
                        style="display: flex; align-items: center;"
                        v-if="!record.videoData && !isEmptyObject(record.videoData) && record.videoData !== '{}' && record.videoData !== 'null' "
                      >
                        --
                      </span>
                    </div>
                    <div style="width: 1px;height: 17px;background-color:#E8E8E8;margin: 0 10px;"></div>
                    <div style="display: flex; height: 100%;">
                      <span style="color:#969696">中位数：</span>
                      <span style="display: flex; align-items: center">
                        {{
                          record.medianViews !== null && record.medianViews !== ""
                            ? getFollower(record.medianViews)
                            : "--"
                        }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
          
            <template v-else-if="column.key === 'celebrityPrivateId'">
              <a-tag class="celebrityPrivate" v-if="record.celebrityPrivateId">私有</a-tag>
            </template>
          <!-- 标签列 -->
          <template v-if="column.dataIndex === 'tagList'">
              <template v-if="record.tagList">
                <div  style="display: flex; flex-direction: column; row-gap: 4px">
                  <div
                    v-for="(item, index) in record.tagList.slice(0, 3)"
                    :key="index"
                    style="display: flex"
                    class="taglistRow"
                  >
                    <a-tooltip :title="item.tagName">
                    
                      <a-tag
                        @click="copyFn(item.tagName)"

                        :style="{
                          border: 'none',
                          backgroundColor: tagColor(item.tagType).split(';')[0],
                          color: tagColor(item.tagType).split(';')[1],
                        }"
                        >{{ item.tagName }}</a-tag
                      >
                    </a-tooltip>
                    <a-popover trigger="hover" placement="bottom">
                      <template #content>
                        <div style="width: 300px">
                          
                          <a-table
                            size="small"
                            :columns="tagColumns"
                            :data-source="record.tagList"
                            :pagination="false"
                            :scroll="{ y: '300px' }"
                          >
                            <template #tag="{ text }">
                              <span >{{ text }}</span>
                            </template>
                            <template #tagType="{ text }">
                              {{ tagTypeParse(text) }}
                            </template>
                          </a-table>
                        </div>
                      </template>
                      <a-tag v-if="record.tagList.length > 3 && index == 2">...</a-tag>
                    </a-popover>
                    <a-tooltip title="复制全部标签">
                      <a-tag
                        style="margin-right: 0px"
                        
                        v-if="record.tagList && index == record.tagList.slice(0, 3).length - 1"
                        @click.stop.prevent="copyTag(record.tagList)"
                      >
                        <CopyOutlined />
                      </a-tag>
                    </a-tooltip>
                  </div>
                </div>
              
              </template>
              <span v-else> -- </span>
            </template>
          <!-- 近半年开发历史列 -->
          <template v-else-if="column.key === 'contactHistory'">
            <template v-if="record.contactHistory">
              <div style="display: flex; flex-direction: column; gap: 4px">
                <div
                  :style="{
                    display: 'flex',
                    justifyContent: idx == 2 ? 'space-between' : '',
                  }"
                  v-for="(item, idx) in  developHistory(record.contactHistory).slice(0, 3)"
                  :key="idx"
                >
                  <div style="display: flex; align-items: center">
                    <span style="width:10px;height:10px;background-color:#3155ED;border-radius:100%;margin-right: 4px;"></span>
                    <span
                      style="
                        display: block;
                        max-width: 150px;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                      "
                      >{{ item.productName ? item.productName : ""
                      }}{{
                        item.brandName
                          ? (item.productName ? "-" : "") + item.brandName
                          : ""
                      }}{{ item.num ? "-" + item.num : "" }}</span
                    >
                  </div>
                </div>
                <a-popover placement="bottomLeft">
                  <template #content>
                    <div style="width: 350px">
                      <a-table
                        size="small"
                        :columns="historyColumns"
                        :data-source="developHistory(record.contactHistory)"
                        :pagination="false"
                      >
                        <template #bodyCell="{ record: innerRecord, column: innerColumn }">
                          <template v-if="innerColumn.key === 'productName'">
                            {{ innerRecord.productName }}
                          </template>
                          <template v-else-if="innerColumn.key === 'brandName'">
                            {{ innerRecord.brandName }}
                          </template>
                          <template v-else-if="innerColumn.key === 'num'">
                            {{ innerRecord.num }}
                          </template>
                        </template>
                      </a-table>
                    </div>
                  </template>
                  <div v-if="developHistory(record.contactHistory).length >= 3">
                    <a>共合作{{ record.contactCount }}次</a>
                  </div>
                </a-popover>
              </div>
            </template>
            <div v-else class="contactHistory_noData">
              <div class="circle"></div>
              <div>暂无近期记录</div>
            </div>
          </template>
          <!-- 邮箱/外链列 -->
          <template v-else-if="column.key === 'email'">
            <div>
              <div
                style="display: flex; align-items: center"
              >
                <span
                  class="icon iconfont icon-mail"
                  style="font-size: 18px; margin-right: 5px"
                ></span>
                <div v-if="!record.isOpen" style="display: flex; align-items: center; justify-content: space-between; flex: 1">
                  <j-ellipsis
                    v-if="record.email"
                    @click.stop.prevent="copyFn(record.email)"
                    :value="record.email"
                    :length="25"
                    style="color: #3155ed"
                  ></j-ellipsis>
                  <span v-else>--</span>
                  <a @click.stop.prevent="editEmail(record)">
                    <FormOutlined style="font-size: 14px; color: #3155ed; margin-right: 10px; cursor: pointer;" />
                  </a>
                </div>
                <div v-if="record.isOpen" style="display: flex; align-items: center; flex: 1">
                  <a-input
                    @click="(e) => e.stopPropagation()"
                    v-model:value="record.email"
                    @change="editChange"
                    placeholder="请输入邮箱"
                    style="width: calc(100% - 40px)"
                    allowClear
                  />
                  <LoadingOutlined
                    v-if="record.loading"
                    @click.stop.prevent="() => check(record)"
                    style="font-size: 14px; margin-left: 5px; color: #3155ed; cursor: pointer;"
                  />
                  <CheckCircleOutlined
                    v-else
                    @click.stop.prevent="() => check(record)"
                    style="font-size: 14px; margin-left: 5px; color: #3155ed; cursor: pointer;"
                  />
                  <CloseCircleOutlined
                    @click.stop.prevent="closeEdit(record)"
                    style="font-size: 14px; color: #3155ed; margin-left: 5px; cursor: pointer;"
                  />
                </div>
              </div>
              <div :style="{ display: 'flex', alignItems: (record.platformType != 2 && record.linktree && parseLinkTree(record.linktree).length > 1) ? '' : 'center' }">
                <span
                  class="icon iconfont icon-lianjie"
                  style="font-size: 18px; margin-right: 5px"
                ></span>
                <template v-if="record.linktree">
                  <a
                    style="padding-right: 10px; display: flex"
                    :href="record.linktree.startsWith('http') ? record.linktree : 'https://' + record.linktree"
                    target="_blank"
                    v-if="record.platformType == 2"
                  >
                    <EllipsisTooltip :text="record.linktree" :lines="1"></EllipsisTooltip>
                  </a>
                  <div v-else style="padding-right: 10px">
                    <div style="width: 100%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">
                      <a
                        :href="parseLinkTree(record.linktree)[0] ? getFullUrl(parseLinkTree(record.linktree)[0].url) : ''"
                        target="_blank"
                        :title="parseLinkTree(record.linktree)[0] ? parseLinkTree(record.linktree)[0].url : ''"
                      >
                        {{ parseLinkTree(record.linktree)[0] ? parseLinkTree(record.linktree)[0].url : '' }}
                      </a>
                    </div>
                    <a-popover>
                      <template #content>
                        <div style="max-height: 400px; overflow: auto; max-width: 500px">
                          <div style="font-size: 18px; color: #000">链接</div>
                          <div style="margin-top: 10px" v-for="value in parseLinkTree(record.linktree)" :key="value.url">
                            <div style="color: #000">{{ value.title }}</div>
                            <a :href="getFullUrl(value.url)" target="_blank">{{ value.url }}</a>
                          </div>
                        </div>
                      </template>
                      <div style="display: inline-block; white-space: normal" v-if="parseLinkTree(record.linktree).length > 1">
                        和另外{{ parseLinkTree(record.linktree).length - 1 }}个链接
                      </div>
                    </a-popover>
                  </div>
                </template>
                <template v-else>
                  <span>--</span>
                </template>
              </div>
            </div>
          </template>
          <template v-else-if="categoryTypeList.some(cat => cat.typeCode === column.dataIndex)">
              <template v-if="record.dataList && record.dataList.length">
                <template v-for="item in categoryTypeList" :key="item.id">
                  <template v-if="item.typeCode === column.dataIndex">
                    <div style="display: flex; flex-direction: column; row-gap: 4px">
                      <div
                        style="display: flex"
                        v-for="(categoryItem, index) in parseCategoryText(item.id, record)
                          .split(',')
                          .slice(0, 3)"
                        :key="index"
                      >
                        <a-tag style="margin-bottom: 0px" :key="index">
                          {{ categoryItem }}
                        </a-tag>
                        <a-popover placement="top">
                          <template #content>
                          <div style="max-width: 180px">
                            
                            <a-table
                            size="small"
                              :columns="attrColumns"
                              :data-source="
                                attrData(parseCategoryText(item.id, record), item.typeName)
                              "
                              :pagination="false"
                              :bordered="false"
                              :scroll="{ y: '250px' }"
                            >
                              <template #categoryTitle>
                                <span>{{ item.typeName }}</span>
                              </template>
                            </a-table>
                          </div>
                          </template>
                          <span
                            v-if="
                              index == 2 &&
                              parseCategoryText(item.id, record).split(',').length > 3
                            "
                            >...</span
                          >
                        </a-popover>
                      </div>
                    </div>
                  </template>
                </template>
              </template>
              <span v-else>--</span>
            </template>
        
          <!-- 屏蔽产品列 -->
          <template v-else-if="column.key === 'kolShields'">
            <template v-if="record.kolShields && record.kolShields.length">
              <div v-for="(item, i) in record.kolShields.slice(0, 3)" :key="item.id">
                <span> {{ item.brandName }} - {{ item.productName }};</span>
                <a-popover>
                  <template #content>
                    <a-table
                      :scroll="{ y: '250', x: 400 }"
                      :columns="productColumns"
                      :data-source="record.kolShields"
                      :pagination="false"
                    >
                      <template #bodyCell="{ record: innerRecord, column: innerColumn }">
                        <template v-if="innerColumn.key === 'brandName'">
                          <EllipsisTooltip :text="innerRecord.brandName" />
                        </template>
                        <template v-else-if="innerColumn.key === 'productName'">
                          <EllipsisTooltip :text="innerRecord.productName" />
                        </template>
                      </template>
                    </a-table>
                  </template>
                  <template #default>
                    <span v-if="i == 2 && record.kolShields.length > 3">...</span>
                  </template>
                </a-popover>
              </div>
            </template>
            <template v-else> -- </template>
          </template>
          <!-- 简介列 -->
          <template v-else-if="column.key === 'signature'">
            <template v-if="record.signature">
              <EllipsisTooltip :text="record.signature" :lines="3"></EllipsisTooltip>
            </template>
            <span v-else>--</span>
          </template>
          <!-- 操作列 -->
          <template v-else-if="column.key === 'action'">
            <a-tooltip title="私有">
              <a
                :disabled="queryParam.includeBlacklist == 1"
                @click.stop.prevent="onePrivate(record)"
              >
                <span class="icon iconfont icon-siyou" style="font-size: 18px"></span>
              </a>
            
            </a-tooltip>
          </template>
        </template>
      </s-table>
      <div   class="kol-pagination" style="display: flex; justify-content: space-between; align-items: center" >
        <div
          v-if="allCount >= 0"
          style="
            margin-top: 10px;

            bottom: 10px;
            left: 16px;
            height: 32px;
            line-height: 32px;
            color: #0b1019 !important;
          "
        >
          <span style="margin-right: 10px"
            >网红数量: {{ allCount }} 当前已选 <a> {{ selectedRowKeys.length }}</a> 个
          </span>
        </div>
        <div v-else style="min-width: 0"></div>
        <div
          style="
            margin-top: 10px;

            bottom: 10px;
            right: 10px;
          "
        >
          <a-tooltip title="首页">
            <a-button
              style="font-size: 14px !important"
              :icon="h(DoubleLeftOutlined)"
              :disabled="loading"
              type="link"
              @click="handleFirstPage"
            ></a-button>
          </a-tooltip>
          <a-tooltip title="上一页">
            <a-button
              style="font-size: 14px !important"
              type="link"
              :icon="h(LeftOutlined)"
              @click="handlePrevPage"
              :disabled="pagination.current == 1 || loading"
            ></a-button>
          </a-tooltip>
          <span style="margin: 0 10px; color: #0b1019 !important"
            >第 {{ pagination.current }} 页</span
          >
          <a-tooltip title="下一页">
            <a-button
              style="font-size: 14px !important"
              type="link"
              :icon="h(RightOutlined)"
              @click="handleNextPage"
              :disabled="resultCount != 5000 || loading"
            ></a-button>
          </a-tooltip>
          <a-select style="width: 100px" v-model:value="pagination.pageSize" @change="pageSizeChange">
            <a-select-option
              v-for="item in pageSizeOptions"
              :key="item.value"
              :value="item.value"
              >{{ item.label }}</a-select-option
            >
          </a-select>
        </div>
      </div>
    </div>
    <!-- 表单区域 -->
    <TiktokEditableCellModal ref="editModelRef" @ok="editModel"></TiktokEditableCellModal>
    <!-- <tiktokCelebrityTags-modal ref="modalForm" @ok="modalFormOk"></tiktokCelebrityTags-modal> -->
    <PrivatelyOwnedModal
      ref="PrivatelyOwnedModalRef"
      @ok="modalFormOk"
    ></PrivatelyOwnedModal>
    <PrivateForm ref="PrivateFormRef" @ok="modalFormOk"></PrivateForm>
    <blockModal ref="blockModalRef" @ok="modalFormOk"></blockModal>
    <markerDevelopingModal
      ref="markerDevelopingModalRef"
      @ok="modalFormOk"
    ></markerDevelopingModal>
    <sendEmailModal ref="sendEmailModalRef" @ok="sendEmailModalOk"></sendEmailModal>
  </div>
</template>

<script setup name="myInternetCelebrityList">
import { ref, computed, watch, onMounted, onActivated, onBeforeUnmount, nextTick, h } from 'vue';
import { getFileblob } from '/@/api/common/api';
import { useRoute } from 'vue-router';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { getDictItems, downloadFile } from '/@/api/common/api';
import { filterObj } from '/@/utils/common/compUtils';
import { useTable } from '/@/components/useSTable/useSTable';
import {
  SearchOutlined,
  ReloadOutlined,
  PushpinOutlined,
  AuditOutlined,
  DownloadOutlined,
  UserDeleteOutlined,
  DoubleLeftOutlined,
  LeftOutlined,
  RightOutlined,
  DownOutlined,
  AlignLeftOutlined,
  CopyOutlined,
  FormOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  LoadingOutlined,
  SafetyCertificateOutlined,
 
} from '@ant-design/icons-vue';
import { ElCascaderPanel, ElPopover } from 'element-plus';
import JDictSelectPlatformType from "@/components/jeecg/JDictSelectPlatformType.vue";
import TiktokEditableCellModal from "./modules/TiktokEditableCellModal.vue";
import PrivatelyOwnedModal from "./modules/PrivatelyOwnedModal.vue";
import PrivateForm from "./modules/PrivateForm.vue";
import sendEmailModal from "./modules/sendEmailModal.vue";
import JEllipsis from '/@/components/Form/src/jeecg/components/JEllipsis.vue';
import { debounce } from 'lodash-es';
import blockModal from "./modules/blockModal.vue";
import EllipsisTooltip from "@/components/jeecg/EllipsisTooltip.vue";
import markerDevelopingModal from "./modules/markerDevelopingModal.vue";
import { isEqual, cloneDeep } from "lodash-es";
import { getCommonCountryApi } from '/@/api/sys/systemCommon';
import dayjs from 'dayjs';
const { createMessage } =  useMessage();
const route =  useRoute();
const url = ref({
  list: "/kol/kolAllot/getAllottedKolList",
  delete: "/tiktokcelebrity/delete",
  deleteBatch: "/tiktokcelebrity/deleteBatch",
  exportXlsUrl: "/kol/kolAllot/exportXls",
  importExcelUrl: "/tiktokcelebrity/importExcel",
});
const tableRef = ref(null);
// API 函数
async  function loadDataApi(params) {
  if (
    (params.brandId && !params.productId) ||
    (!params.brandId && params.productId)
  ) {
  
    createMessage.warning("请选择产品");
    return;
  }

  if (params.productId && params.tagNameList && params.tagNameList.length > 0) {
    createMessage.warning("产品与标签不可同时查询，请重新选择");
    return;
  }
  if (params.attributeIds && params.tagNameList && params.tagNameList.length > 0) {
    createMessage.warning("标签与达人类型不可同时查询，请重新选择");
    return;
  }
  if (params.tagNameList && params.tagNameList.length > 0) {
    params.tagType = tagType.value;
  } else {
    delete params.tagType;
  }
  currentQueryParam.value = cloneDeep(params);
  const res = await defHttp.post(
    { url: url.value.list + `?pageNo=${params.pageNo}&pageSize=${params.pageSize}`, data: params },
    {isTransformResponse:false}
  );
  if (res.success){
    resultCount.value = res.resultCount;
    return res.result;
  } else {
    return [];
  }
}

// 使用 useTable hook
const {
  newLayout,
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  rowSelection,
  selectedRowKeys,
  searchQuery,
  awaitFetchParamFn,
  pageChange,
  updatePagination,
  calcTableHeight,
  copyFn
} =  useTable(loadDataApi,-10,{
  beforeFetch: (params) => {
    return myInternetCelebritBeforeFetch(params)
  },
  afterFetch: (param,res) => {
    return myInternetCelebritAfterFetch(param,res)
  }
});
newLayout.value = true
updatePagination({
  defaultPageSize: 50,           // 默认每页条数
  pageSize: 50,                  // 当前每页条数
  pageSizeOptions: ['20','30','50','100', '200'],  // 每页条数选项
  current: 1,                    // 当前页码
  total: 0,                      // 总条数
  showQuickJumper: true,         // 是否显示快速跳转
  showSizeChanger: true,         // 是否显示每页条数选择器
  showTotal: (total, range) => { // 自定义显示总数
    return `${range[0]}-${range[1]} 共${total}条`
  }

})
queryParam.value.countryCode = "US";
queryParam.value.isHasEmail = 1;
queryParam.value.isContact = 0;
queryParam.value.isPrivateKol = 0;
queryParam.value.platformType = "2";

queryParam.value.allotDateRange = [
  dayjs().subtract(89, "days"),
  dayjs(),
];
const allotDateRadio = ref("近90天");
const defaultRange = ref([
  {
    title: "近7天",
    value: "近7天",
    range: [dayjs().subtract(6, "days"), dayjs()],
  },
  {
    title: "近30天",
    value: "近30天",
    range: [dayjs().subtract(29, "days"), dayjs()],
  },

  {
    title: "近90天",
    value: "近90天",
    range: [dayjs().subtract(89, "days"), dayjs()],
  },
  {
    title: "近半年",
    value: "近半年",
    range: [dayjs().subtract(180, "days"), dayjs()],
  },
  {
    title: "自定义",
    value: "自定义",
    range: [],
  },
])
const myInternetCelebritBeforeFetch = (param) =>{
  
  if (
    param.productId ||
    (param.productAttributeIds && param.productAttributeIds.length > 0)
  ) {
    param.attributeIds = param.productAttributeIds.join(",");
    delete param.productAttributeIds;
  }
  if (param.attributeIds && param.attributeIds.length == 0) {
    param.attributeIds = undefined;
  }
  if (param.tagNameList && param.tagNameList.length > 0) {
    param.tagType = tagType.value;
  } else {
    delete param.tagType;
  }
  if (param.tagNameList && param.tagNameList.length == 0) {
    delete param.tagType;
  }
  if (param.allotDateRange && param.allotDateRange.length > 0) {
    param.allotStartTime = dayjs(param.allotDateRange[0]).format("YYYY-MM-DD");
    param.allotEndTime = dayjs(param.allotDateRange[1]).format("YYYY-MM-DD");
    delete param.allotDateRange;
  }
  param.isNonPinned = isPinned.value ? 0 : 1;

  return param
}

const myInternetCelebritAfterFetch = (param,res) =>{
  console.log(res);

  dataSource.value.forEach(item => {
    item.isOpen = false;
    item.loading = false
  });
  // return res
  getAllCount(param)
}
// 其他查询参数
const superQueryParams = ref("");
const isorter = ref({
  column: 'createTime',
  order: 'desc'
});
const filters = ref({});

// 响应式数据
const lastFetchId =  ref(0);
const description =  ref("我的网红管理页面");
const fetching =  ref(false);
const averageBroadcast =  ref("");
const averageBroadcasts =  ref("");
const accountVisible =  ref(false);
const defaultChecked =  ref(false);
const editEmailValue =  ref(undefined);
const accountValue =  ref("");
const uniqueIdList =  ref([]);
const countrys =  ref([]); //国家
const tags =  ref([]); //标签
const productAttributeList = ref([])
const CelebrityConsultants =  ref([]); //网红顾问
const categoryTypeList =  ref([]);
const isPinned =  ref(false);
// 虚拟滚动相关变量
const tagPageSize =  ref(50); // 每页加载的标签数量
const tagCurrentPage =  ref(1); // 当前标签页
const allTags =  ref([]); // 存储所有标签数据
const hasMoreTags =  ref(true); // 是否还有更多标签
const tagLoading =  ref(false); // 标签加载状态
const platformTypeList =  ref([]);
const brandList =  ref([]);
const platforms =  ref([]);
const unilList =  ref([]);
const emailList =  ref([]);
const celebrityContentList =  ref([]);
const searchType =  ref("0");
const emailVisible =  ref(false);
const emailValue =  ref("");
const cascaderProps = ref({ multiple: true, value: "id", label: "attributeName" });
const participativeDistributionNum =  ref(0);
const pageSizeOptions =  ref([
  { label: "20条/页", value: 20 },
  { label: "30条/页", value: 30 },
  { label: "50条/页", value: 50 },
  { label: "100条/页", value: 100 },
  { label: "200条/页", value: 200 },
]);
const tagVisible =  ref(false);
const uniVisible =  ref(false);
const resultCount =  ref(undefined);
const allCount =  ref(undefined);
const nonExistentTag =  ref([]);
const noTag =  ref(false);
const tagValue =  ref("");
const emailStr =  ref("");
const uniStr =  ref("");
const oldEmail =  ref("");
const overlayStyle = ref({ width: "270px" });
const brandId =  ref("");
const btnLoading =  ref(false);
const selectIndex =  ref(null);
      // 更多筛选相关数据
const filterType =  ref("");
const options =  ref([]);
const tagNameList =  ref([]);
const attributeIdsMap =  ref({});
const filterOptions =  ref([]);
const filterTagTypeList =  ref([]);
      // 保存各筛选条件的数据
const filterData = ref({
        attributeSelections: {},
        followersRange: {
          minFollowers: null,
          maxFollowers: null,
          selectedRanges: [],
        },
        effectiveVideoRange: {
          minVideoCount: null,
          maxVideoCount: null,
          selectedRanges: [],
        },
        medianViewsRange: {
          minMedianViews: null,
          maxMedianViews: null,
          selectedRanges: [],
        },
        playAvgRange: {
          minPlayAvg: null,
          maxPlayAvg: null,
          selectedRanges: [],
        },
        tagData: {
          tagType: undefined,
          tagNameList: [],
        },
});
      // 粉丝数范围相关变量
const minFollowers =  ref(null);
const maxFollowers =  ref(null);
const selectedFollowersRanges =  ref([]);
      // 有效视频范围相关变量
const minVideoCount =  ref(null);
const maxVideoCount =  ref(null);
const selectedVideoRanges =  ref([]);
      // 视频中位数范围相关变量
const minMedianViews =  ref(null);
const maxMedianViews =  ref(null);
const selectedMedianViewsRanges =  ref([]);
      // 均播数范围相关变量
const minPlayAvg =  ref(null);
const maxPlayAvg =  ref(null);
const selectedPlayAvgRanges =  ref([]);
      // 标记是否通过应用按钮关闭
const isApplyClose =  ref(false);
      // 备份数据，用于取消时恢复
const backupData = ref({
        attributeIdsMap: {},
        filterData: {
          attributeSelections: {},
          followersRange: {
            minFollowers: null,
            maxFollowers: null,
            selectedRanges: [],
          },
          effectiveVideoRange: {
            minVideoCount: null,
            maxVideoCount: null,
            selectedRanges: [],
          },
          medianViewsRange: {
            minMedianViews: null,
            maxMedianViews: null,
            selectedRanges: [],
          },
          playAvgRange: {
            minPlayAvg: null,
            maxPlayAvg: null,
            selectedRanges: [],
          },
          tagData: {
            tagType: undefined,
            tagNameList: [],
          },
        },
        minFollowers: null,
        maxFollowers: null,
        selectedFollowersRanges: [],
        minVideoCount: null,
        maxVideoCount: null,
        selectedVideoRanges: [],
        minMedianViews: null,
        maxMedianViews: null,
        selectedMedianViewsRanges: [],
        minPlayAvg: null,
        maxPlayAvg: null,
        selectedPlayAvgRanges: [],
        tagType: undefined,
        tagNameList: [],
        accountValue: '',
        emailValue: '',
});
const tagType =  ref(undefined);

// 表头定义
const tagColumns =  ref([
        {
          title: "#",
          dataIndex: "",
          key: "tagRowIndex",
          align: "center",
          width: 60,
    customRender:  function({ index }) {
            return  parseInt(index) + 1;
          },
        },
        {
          title: "标签",
          dataIndex: "tagName",

        slots: { customRender: "tagName" },
        },
        {
          title: "类型",
          dataIndex: "tagType",
    slots: { customRender: "tagType" },
        },
]);

const historyColumns =  ref([
        {
          title: "#",
          dataIndex: "",
          key: "historyRowIndex",
          width: 60,
          align: "center",
    customRender:  function({ index }) {
            return  parseInt(index) + 1;
          },
        },
        {
          title: "产品",
          dataIndex: "productName",
          width: "100px",
    slots: { customRender: "productName" },
        },
        {
          title: "品牌",
          dataIndex: "brandName",
          width: "100px",
    slots: { customRender: "brandName" },
        },
        {
          title: "开发次数",
          dataIndex: "num",
          width: "100px",
    slots: { customRender: "num" },
        },
]);

const productList =  ref([]);
const productCategoryList =  ref([]);
const sortedInfo =  ref(null);
const tagTitle =  ref("");
const selectOpen =  ref(false);
const tagTypeList =  ref([]);
const socialMediaCategoryList = ref([]);
// 优化虚拟滚动性能的防抖方法
const debouncedLoadMoreTags = ref(debounce(() => {
  loadMoreTags();
}, 200));
const tagGroupList = ref([]);
const productColumns =  ref([
        {
          title: "#",
          dataIndex: "",
          key: "rowIndex",
          width: 50,
    customRender:  function({ index }) {
            return  parseInt(index) + 1;
          },
        },
        {
          title: "品牌名称",
          dataIndex: "brandName",
    slots: { customRender: "brandName" },
        },
        {
          title: "产品名称",
          dataIndex: "productName",
    slots: { customRender: "productName" },
        },
]);

const attrColumns =  ref([
        {
          title: "#",
          dataIndex: "",
          width: "50px",
          key: "index",
          align: "center",
    customRender:  function({ index }) {
            return  parseInt(index) + 1;
          },
        },
        {
          slots: { title: "categoryTitle" },
          dataIndex: "category",
          key: "category",
        },
]);

const currentQueryParam =  ref({});
      // 弹窗显示状态管理
const mainFilterPopoverVisible =  ref(false);
const attributeFilterPopoverVisible =  ref({});
const emailFilterPopoverVisible =  ref(false);
const accountFilterPopoverVisible =  ref(false);
const tagFilterPopoverVisible =  ref(false);
const followersFilterPopoverVisible =  ref(false);
const videoFilterPopoverVisible =  ref(false);
const medianViewsFilterPopoverVisible =  ref(false);
const playAvgFilterPopoverVisible =  ref(false);

// 组件引用
const editModelRef =  ref();
const PrivatelyOwnedModalRef =  ref();
const PrivateFormRef =  ref();
const blockModalRef =  ref();
const markerDevelopingModalRef =  ref();
const mainFilterPopoverRef =  ref();
const emailFilterPopoverRef =  ref();
const accountFilterPopoverRef =  ref();
const tagFilterPopoverRef =  ref();
const followersFilterPopoverRef =  ref();
const videoFilterPopoverRef =  ref();
const medianViewsFilterPopoverRef =  ref();
const playAvgFilterPopoverRef =  ref();
const attributeFilterPopoverRefs =  ref({});
const sendEmailModalRef = ref(null)

// 定义表格列
const columns =  computed(() => {
  const baseColumns = [
    {
      title: "头像",
      key: "avatar",
      width: 360,
      fixed: 'left',
    },
    {
      title: "私有网红",
      dataIndex: "celebrityPrivateId",
      key: "celebrityPrivateId",
      width: 80,
      align: "center",
    },
    {
      title: "标签",
      dataIndex: "tagList",
      key: "tagList",
      width: 270,
    },
    {
      title: "近半年开发历史",
      dataIndex: "contactHistory",
      key: "contactHistory",
      width: 210,
    },
    {
      title: "邮箱/外链",
      dataIndex: "email",
      key: "email",
      width: 250,
    },
  ];
  
  // 动态添加类目列
  const categoryColumns = categoryTypeList.value.map((item) => ({
    title: item.typeName,
    dataIndex: item.typeCode,
    key: item.typeCode,
    width: 200,
  }));
  
  // 添加其他列
  const otherColumns = [
    {
      title: "最后分配日期",
      dataIndex: "allotTime",
      key: "allotTime",
      width: 100,
    },
    {
      title: "屏蔽产品",
      dataIndex: "kolShields",
      key: "kolShields",
      width: 200,
    },
    {
      title: "简介",
      dataIndex: "signature",
      key: "signature",
      width: 150,
    },
    {
      title: "操作",
      key: "action",
      width: 80,
      align: "center",
      fixed: 'right',
    },
  ];
  
  return [...baseColumns, ...categoryColumns, ...otherColumns];
});
// Watch
 watch(
  () => route.path,
  () => {
 closeTagEdit();
  },
  { immediate: true }
);

 watch(tags, (nVal) => {
 if(nVal.length > 0) {
    selectOpen.value = true;
  }
});

 watch(tagVisible, (nVal) => {
 if(!nVal) {
    noTag.value = false;
  }
});

// Computed
const attributeIds =  computed({
  get() {
return attributeIdsMap.value[filterType.value] || [];
  },
  set(value) {
attributeIdsMap.value[filterType.value] = value;
  },
});

const importExcelUrl =  computed(() => {
  // return `${window._CONFIG["domianURL"]}/${url.importExcelUrl}`;
  return "";
});

const hasActiveFilters =  computed(() => {
 if(queryParam.value.emailKeyword && queryParam.value.emailKeyword) {
        return true;
      }
 if(queryParam.value.uniqueId && queryParam.value.uniqueId) {
        return true;
      }
 if(queryParam.value.tagNameList && queryParam.value.tagNameList.length > 0) {
        return true;
      }
 if(queryParam.value.minFollowers || queryParam.value.maxFollowers) {
        return true;
      }
 if(
    queryParam.value.minVideoStandardCount ||
    queryParam.value.maxVideoStandardCount
      ) {
        return true;
      }
 if(queryParam.value.minMedianViews || queryParam.value.maxMedianViews) {
        return true;
      }
 if(queryParam.value.minPlayAvgNum || queryParam.value.maxPlayAvgNum) {
        return true;
      }
 if(queryParam.value.attributeIds && queryParam.value.attributeIds) {
        return true;
      }
      return false;
});
// 防抖函数
const fetchUserDebounced =  debounce((value) => {
 fetchUser(value);
}, 800);

// 生命周期
 onMounted(() => {
  // 初始化
  getOffSetNum();
  });
  
initAttribute();
resetTagPagination();
initTagTypeList();
initCountry();
initTagTypeAndGroup();
initProductCategory();
initBrandList();
initCelebrityContent();

 onBeforeUnmount(() => {
    // 清理防抖函数，避免内存泄漏
 if(debouncedLoadMoreTags.value && debouncedLoadMoreTags.value.cancel) {
    debouncedLoadMoreTags.value.cancel();
    }
});
function allotDateRangeChange(e) {
  console.log(e);
  if (allotDateRadio.value !== "自定义") {
    allotDateRadio.value = undefined;
  }
}
function sendEmailModalOk(){
  selectedRowKeys.value = [];
  fetchTable();
}
function handleSearch(){
  // 新查询时清空勾选
  selectedRowKeys.value = [];
  fetchTable(1);
}
function modalFormOk(){
  fetchTable();
}
function developHistory(text) {
  if (text) {
    return JSON.parse(text);
  } else {
    return [];
  }
}

function changePinned(value) {
  console.log(value)
  isPinned.value = value;
  // fetchTable();
}


function handleTagSel(value) {
      console.log(value);
}

function copyTag(tagList) {
      const headers = ["标签", "类型"];
      const headerRow = headers.join("\t");
      const dataRows = tagList.map(
        (item) => `${item.tagName}\t${tagTypeParse(item.tagType)}`
      );
      const tsv = [headerRow, ...dataRows].join("\n");

      // ✅ 检查 clipboard 是否可用
      if (navigator.clipboard && typeof navigator.clipboard.writeText === "function") {
        navigator.clipboard
          .writeText(tsv)
          .then(() => {
            createMessage.success("复制成功！");
          })
          .catch((err) => {
            console.error("Clipboard write failed:", err);
            createMessage.error("复制失败，请手动复制。");
          });
      } else {
        // 💡 降级方案：使用 document.execCommand（旧方法）
        fallbackCopyText(tsv);
      }
    }

    // 降级复制方法（适用于不支持 Clipboard API 的环境）
function fallbackCopyText(text) {
  const textarea = document.createElement("textarea");
  textarea.value = text;
  textarea.style.position = "fixed"; // 避免滚动
  textarea.style.opacity = "0";
  textarea.style.left = "-9999px";
  document.body.appendChild(textarea);
  textarea.select();
  try {
    const successful = document.execCommand("copy");
    if (successful) {
      createMessage.success("复制成功");
    } else {
      createMessage.error("复制失败，请手动复制。");
    }
  } catch (err) {
    createMessage.error("复制失败，请手动复制。");
  }
  document.body.removeChild(textarea);
}
function attrData(text) {
  return text.split(",").map((item) => ({
    category: item,
  }));
}


function pageSizeChange(value) {
      // pagination.pageSize is handled by useTable
      tableRef.value.scrollTo({top:0},'smooth');
      pagination.value.pageSize = value;
      fetchTable(1);
    }

async function getAllCount(params) {

      if (params.tagNameList && params.tagNameList.length > 0) {
        params.tagType = tagType.value
      } else {
        delete params.tagType;
      }
      const res = await defHttp.post({ url: "/kol/kolAllot/getAllotListCount", data: params },{isTransformResponse: false});
      console.log(res);
      if (res.success) {
        allCount.value = res.result;
      } else {
        allCount.value = 0;
      }
    }

function handleFirstPage() {
      if (loading.value) return;
      tableRef.value.scrollTo({top:0},'smooth');
      fetchTable(1);
    }

function handlePrevPage() {
      if (loading.value) return;
      tableRef.value.scrollTo({top:0},'smooth');
      pagination.value.current -= 1;
     
      fetchTable();
    }

function handleNextPage() {
      if (loading.value) return;
      tableRef.value.scrollTo({top:0},'smooth');
      pagination.value.current += 1;
      fetchTable();
    }

async function initCelebrityContent() {
      const res = await getDictItems("celebrity_content");
      if (res) {
        celebrityContentList.value = res;
      }
    }

async function initBrandList() {
  const res = await defHttp.get({ url: "/kol/tagAllot/getBrandList", params: {
    platformType: queryParam.value.platformType,
  } });
  if (res) {
    brandList.value = res;
  }
}

function onBrandChange(value) {
      // queryParam.value.categoryIds = undefined;
      queryParam.value.productId = undefined;
      productList.value = [];
      
      productAttributeList.value = [];
      if (value) {
        initProduct();
      } else {
        onProductChange(undefined);
        queryParam.value.isContact = undefined;
      }
    }

function jsonParseTotal(jsonStr) {
      if (jsonStr) {
        const obj = JSON.parse(jsonStr).total;
        return obj;
      } else {
        return null;
      }
    }

function jsonParseNonPinned(jsonStr) {
      if (jsonStr) {
        const obj = JSON.parse(jsonStr).non_pinned;
        return obj;
      } else {
        return null;
      }
    }

function isEmptyObject(obj) {
      if (obj) {
        return Object.keys(obj).length !== 0;
      } else {
        return false;
      }
    }

function getFullUrl(url) {
      return url.startsWith("http") ? url : "https://" + url;
    }

function parseLinkTree(text) {
      if (text) {
        return JSON.parse(text);
      } else {
        return [];
      }
    }

function onSearchTypeChange(value) {
      console.log(value);
      // queryParam.value.productId = undefined;
      // queryParam.value.categoryIds = undefined;
      queryParam.value.productId = undefined;
      queryParam.value.categoryIds = undefined;
      queryParam.value.brandId = undefined;
      productList.value = [];
      onProductChange(undefined);
      // onCategoryChange(undefined);
      // onProductChange(undefined);
      // queryParam.value.tagType = value;
    }

function parseCountry(country) {
      if (country) {
        return countrys.value.find((item) => item.shortCode == country)?.country || "-";
      } else {
        return "-";
      }
    }

function emailSerch(value) {
      console.log(value);
      emailList.value = value
        .split("\n")
        .map((item) => item.trim())
        .filter((item) => item !== "");
      emailVisible.value = false;
    }


function accountSerch(value) {
      console.log(value);
      uniqueIdList.value = value
        .split("\n")
        .map((item) => item.trim())
        .filter((item) => item !== "");
      console.log(uniqueIdList.value);

      accountVisible.value = false;
    }

function clearAccountInput() {
      accountValue.value = "";
    }

function closeAccountEdit() {
      accountVisible.value = false;
      clearAccountInput();
    }

async function initProduct() {
      const res = await defHttp.get({ 
        url: "/kol/kolProduct/listAll", 
        params: {
          brandId: queryParam.value.brandId,
        }
      });
      if (res) {
        productList.value = res.map((item) => {
          return {
            ...item,
            text: item.productName,
          };
        });
        if (productList.value.length == 1) {
          queryParam.value.productId = productList.value[0].id;
          onProductChange(productList.value[0].id);
        }
      }
    }

async function initProductCategory() {
      const res = await defHttp.get({ url: "/kol/category/loadTreeDataAll" });
      if (res) {
        productCategoryList.value = res;
      }
    }



async function onProductChange(value) {
  
  productAttributeList.value = [];
  queryParam.value.productAttributeIds = [];
  if (value) {
    const res = await defHttp.get({ url: "/kol/attribute/queryByProductId", params: {
      productId: value,
      platformType: queryParam.value.platformType,
    }},{isTransformResponse:false});
    if (res.success) {
      if (res && res.result && Array.isArray(res.result)) {
        console.log(res);
        const arr = [];
        filterOptions.value[0].data.forEach((item) => {
          if (res.result.find((a) => a == item.id)) {
            console.log(item);
            arr.push(item);
          }
        });
        productAttributeList.value = arr;
        queryParam.value.productAttributeIds = res.result;
      } 
    }
  } 
}

async function onCategoryChange(value) {
  console.log("分类变更:", value);
  
}

function handleImageError(e) {
  e.target.src = new URL("/@/assets/images/avatar.png", import.meta.url).href;
}

function getImage(url) {
    return url.includes("https") || url.includes("http")
      ? url
      : 'https://gemstone-image.kolbox.com/avatar/' + url;
  }

async function initAttribute() {
      const res = await defHttp.get({ url: "/kol/kolAttributeType/listAll" });
      const categoryRes = await defHttp.get({ url: "/kol/attribute/getKolAttribute" });
      Promise.all([res, categoryRes]).then(async ([res, categoryRes]) => {
        categoryTypeList.value = res.filter((item) => item.type == 2);
        socialMediaCategoryList.value = res.map((typeItem) => {
          // 查找是否在 res.result 中存在对应的数据
          const existingItem = categoryRes.find(
            (item) => item.typeId === typeItem.id
          );
          console.log(existingItem);

          if (existingItem) {
            // 如果存在，使用现有数据并添加必要属性
            return {
              ...existingItem,
              selectedValues: [],
              typeName: typeItem.typeName,
              // typeId: typeItem.id
            };
          } else {
            return {
              data: [],
              selectedValues: [],
              typeName: typeItem.typeName,

              typeId: typeItem.id,
            };
          }
        });
        console.log(socialMediaCategoryList.value);
        // await initProductCategory()
        await initAttributeList();

        fetchTable(1);
      });
    }

function parseCategoryText(id, row) {
      const filterList = row.dataList.filter((item) => item.typeId == id);
      // console.log(filterList);
      if (filterList.length > 0) {
        return filterList
          .map((item) => item.list.map((category) => category.attributeName).join(", "))
          .join(", ");
      }
      return "";
    }

function parseCategory(id, row) {
      const filterList = row.dataList.filter((item) => item.typeId == id);

      if (filterList.length > 0) {
        // console.log(groupByLevel(filterList));

        return groupByLevel(filterList);
      }
      return [];
    }

    // 按 level 分组
function groupByLevel(data) {
      // 扁平化所有 list 数据
      const allItems = data.flatMap((item) => item.list);

      // 按 level 分组，并去重
      const grouped = allItems.reduce((acc, item) => {
        const { level, attributeId } = item;

        // 查找是否已有该 level 的分组
        let group = acc.find((g) => g.level === level);
        if (!group) {
          group = { level, list: [] };
          acc.push(group);
        }

        // 使用 Set 来记录已经存在的 categoryId（每个 group 内部独立）
        const seen = new Set(group.list.map((i) => i.attributeId));

        // 如果 attributeId 还没出现过，则加入 list
        if (!seen.has(attributeId)) {
          group.list.push(item);
        }

        return acc;
      }, []);

      return grouped;
    }


function deselect(value) {
      console.log(value);
      setTimeout(() => {
        selectOpen.value = false;
      });
    }
function sendEmail(){
  let processParam = myInternetCelebritBeforeFetch(awaitFetchParamFn({}))
  console.log(currentQueryParam.value, processParam)
  if (!isEqual(currentQueryParam.value, processParam)) {
    createMessage.warning("当前查询条件与列表结果不一致，请重新查询后再进行发送邮件");
    return;
  }
  const arr = [];
      let hasEmptyEmail = false;
      let equalPlatformType = true;
      for (let index = 0; index < dataSource.value.length; index++) {
        const item = dataSource.value[index];
        if (selectedRowKeys.value.includes(item.id)) {
          if (!item.email) {
            hasEmptyEmail = true;
          }
          if (item.platformType != queryParam.value.platformType) {
            equalPlatformType = false;
          }
          arr.push({
            account: item.platformType == 2 ? item.authorId : item.account,
            celebrityId: item.id,
            username: item.uniqueId,
            email: item.email,
            nickname: item.nickname,
            platformType: item.platformType,
          });
        }
      }
      if (hasEmptyEmail) {
        createMessage.warning("所选网红中存在没有邮箱的账号，请补充邮箱后再操作！");
        return;
      }
      if (!equalPlatformType) {
        createMessage.warning(
          "所选网红与当前平台不一致，请选择当前平台下的网红后再操作！"
        );
        return;
      }

      sendEmailModalRef.value?.show(arr, {
        brandId: queryParam.value.brandId,
        productId: queryParam.value.productId,
        platformType: queryParam.value.platformType,
      });
      let platformTypeText = undefined;
      if (queryParam.value.platformType == 0) {
        platformTypeText = "Instagram";
      } else if (queryParam.value.platformType == 1) {
        platformTypeText = "YouTube";
      } else if (queryParam.value.platformType == 2) {
        platformTypeText = "TikTok";
      }

      // sendEmailModalRef.value?.title = "发送邮件" + "-" + platformTypeText;
}
function screenBtn() {
      let processParam = myInternetCelebritBeforeFetch(awaitFetchParamFn({}))
      if (!processParam.productId) {
        createMessage.warning("请选择产品");
        return;
      }

      if (!isEqual(currentQueryParam.value, processParam)) {
        createMessage.warning("当前查询条件与列表结果不一致，请重新查询后再进行一键屏蔽");
        return;
      }

      const arr = [];
      for (let index = 0; index < dataSource.value.length; index++) {
        const item = dataSource.value[index];
        if (selectedRowKeys.value.includes(item.id)) {
          if (item.celebrityPrivateId) {
            createMessage.warning("所选网红中存在私有网红，无法屏蔽！");
            return;
          }

          if (item.platformType == 2) {
            arr.push({
              celebrityId: item.authorId,
              uniqueId: item.uniqueId,
              platformType: item.platformType,
              brandId: queryParam.value.brandId,
              productId: queryParam.value.productId,
            });
          } else {
            arr.push({
              celebrityId: item.account,
              uniqueId: item.uniqueId,
              platformType: item.platformType,
              brandId: queryParam.value.brandId,
              productId: queryParam.value.productId,
            });
          }
        }
      }
      // let brandName = this.brandList.find((item) => item.id ===queryParam.value.brandId)
      //   .brandName;
      // let productName = productList.value.find(
      //   (item) => item.id === queryParam.value.productId
      // ).text;
      blockModalRef.value?.show(arr, {
        brandId: processParam.brandId,
        productId: processParam.productId,
      });
    }

function selBlur() {
      console.log("selBlur");

      selectOpen.value = false;
      clearTagDataForVirtualScroll();
    }

function handleSelectTag(tagName, event) {
      // 阻止事件冒泡和默认行为
      if (event) {
        event.stopPropagation();
        event.preventDefault();
      }

      // 确保 tagNameList 存在
      if (!tagNameList.value) {
        tagNameList.value = [];
      }

      const index = tagNameList.value.indexOf(tagName);
      if (index === -1) {
        // 如果不存在，则添加
        tagNameList.value.push(tagName);
      } else {
        // 如果已存在，则移除
        tagNameList.value.splice(index, 1);
      }

      // 保持下拉框打开
      selectOpen.value = true;
    }
const handleExportXls = (fileName) => {


  let param = {};
      if (selectedRowKeys.value && selectedRowKeys.value.length > 0) {
        param["selections"] = selectedRowKeys.value.join(",");
      }
      param.platformType = queryParam.value.platformType;
 

  getFileblob(
    "/kol/kolAllot/exportXls",
    param
   
  )
    .then((data) => {
      console.log(data);
         if (typeof window.navigator.msSaveBlob !== "undefined") {
        window.navigator.msSaveBlob(
          new Blob([data], { type: "application/vnd.ms-excel" }),
          fileName + ".xlsx"
        );
      } else {
        let url = window.URL.createObjectURL(
          new Blob([data], { type: "application/vnd.ms-excel" })
        );
        let link = document.createElement("a");
        link.style.display = "none";
        link.href = url;
        link.setAttribute("download", fileName + ".xls");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link); //下载完成移除元素
        window.URL.revokeObjectURL(url); //释放掉blob对象
      }
    })


}
async function initTagTypeList() {
      const res = await getDictItems("tag_type");
      if (res) {
        tagTypeList.value = res;
        res.forEach((item) => {
          tagTitle.value += `${item.text}-${item.description}`;
          });
        }
    }

function tagColor(type) {
      let color = tagTypeList.value.filter((item) => item.value == type)[0]
        ? tagTypeList.value.filter((item) => item.value == type)[0].description
        : "";
      return color;
    }

function tagTypeParse(type) {
      let typeText = tagTypeList.value.filter((item) => item.value == type)[0]?.title || "";
      return typeText;
    }

function closeuniqueIdEdit() {
      uniVisible.value = false;
      clearUniqueIdInput();
    }

function clearUniqueIdInput() {
      uniStr.value = "";
    }

function uniqueIdSerch() {
      const uniVal = uniStr.value.trim().split("\n");
      unilList.value = [...new Set(uniVal)];
      // closeuniqueIdEdit()
      uniVisible.value = false;
    }

function clearEmailInput() {
      emailStr.value = "";
    }

function closeEmailEdit() {
      emailVisible.value = false;
      clearEmailInput();
    }

function onePrivate(row) {
      console.log(row);
      PrivateFormRef.value?.edit(row, socialMediaCategoryList.value);
    }

function privatelyOwned() {
  if (selectedRowKeys.value.length === 0) {
    createMessage.warning("请选择需要私有的网红");
  } else {
    const filterArr = dataSource.value
      .filter((item) => selectedRowKeys.value.includes(item.id))
      .map((item) => {
        return {
          ...item,

          contentQuotation: celebrityContentList.value
            .filter((c) => c.text == item.platformType)[0]
            ?.value.split(",")
            .map((t) => {
              return {
                videoTag: t,
                contractAmount: undefined,
              };
            }),
        };
      });

    console.log(filterArr, socialMediaCategoryList.value);
    PrivatelyOwnedModalRef.value?.add(filterArr, socialMediaCategoryList.value);
  }
}

function handleChange(value) {
      console.log("handleChange", value);

      // Object.assign(this, {
      //   value,
      //   // tags: [],
      //   fetching: false,
      // });

      // 保持下拉框打开状态
      if (value.length > 0) {
        // selectOpen.value = true;
      } else {
        tags.value = [];
      }
      // tagNameList.value = value;
    }

function fetchUser(value) {
      console.log("fetchUser", value);

      if (value) {
        lastFetchId.value += 1;
        const fetchId = lastFetchId.value;

        // 重置虚拟滚动状态
        resetTagPagination();

        fetching.value = true;

        defHttp.post({ url: "/kol/tags/queryTagsListByName", data: { tagName: value, platformType: queryParam.value.platformType } }).then((res) => {
          if (fetchId !== lastFetchId.value) {
            // for fetch callback order
            return;
          }
          if (loading.value) {
            fetching.value = false;
            return;
          }

          // 存储所有标签数据
          allTags.value = res || [];

          // 初始化虚拟滚动
          initTagPagination();

          // 调试信息
          debugVirtualScroll();

          fetching.value = false;
        });
      } else {
        tags.value = [];
      }
    }

    // 标签虚拟滚动相关方法
function tagPopupScroll(e) {
      const { target } = e;
      const { scrollTop, scrollHeight, clientHeight } = target;

      // 计算滚动位置
      const scrollBottom = scrollTop + clientHeight;
      const isNearBottom = scrollHeight - scrollBottom < 50; // 距离底部50px时触发加载

      if (isNearBottom && !tagLoading.value && hasMoreTags.value) {
        debouncedLoadMoreTags.value();
      }
    }

    // 加载更多标签数据
async function loadMoreTags() {
      if (tagLoading.value || !hasMoreTags.value) return;

      // 验证数据完整性
      if (!validateTagData()) {
        return;
      }

      tagLoading.value = true;

      try {
        // 计算下一页的起始位置
        const startIndex = (tagCurrentPage.value - 1) * tagPageSize.value;
        const endIndex = startIndex + tagPageSize.value;

        // 从所有标签中截取当前页的数据
        const newTags = allTags.value.slice(startIndex, endIndex);

        if (newTags.length > 0) {
          // 将新数据添加到当前显示的标签中
          tags.value = [...tags.value, ...newTags];
          tagCurrentPage.value++;

          // 检查是否还有更多数据
          hasMoreTags.value = endIndex < allTags.value.length;
        } else {
          hasMoreTags.value = false;
        }
      } catch (error) {
        console.error("加载更多标签失败:", error);
        // 发生错误时，标记为没有更多数据，避免无限重试
        hasMoreTags.value = false;
      } finally {
        tagLoading.value = false;
      }
    }

    // 重置标签分页状态
function resetTagPagination() {
      tagCurrentPage.value = 1;
      tags.value = [];
      hasMoreTags.value = true;
      tagLoading.value = false;
    }

    // 初始化标签分页
function initTagPagination() {
      // 验证数据完整性
      if (!validateTagData()) {
        return;
      }

      if (allTags.value.length === 0) {
        tags.value = [];
        hasMoreTags.value = false;
        return;
      }

      // 加载第一页数据
      const firstPageTags = allTags.value.slice(0, tagPageSize.value);
      tags.value = firstPageTags;

      // 检查是否还有更多数据
      hasMoreTags.value = allTags.value.length > tagPageSize.value;
      tagCurrentPage.value = 1;
    }

    // 清理标签数据，释放内存（用于虚拟滚动）
function clearTagDataForVirtualScroll() {
      allTags.value = [];
      tags.value = [];
      resetTagPagination();
    }

    // 验证标签数据完整性
function validateTagData() {
      if (!allTags.value || !Array.isArray(allTags.value)) {
        console.warn("标签数据格式不正确，重置数据");
        clearTagDataForVirtualScroll();
        return false;
      }
      return true;
    }

    // 调试虚拟滚动状态
function debugVirtualScroll() {
      console.log("虚拟滚动状态:", {
        allTagsCount: allTags.value.length,
        currentTagsCount: tags.value.length,
        currentPage: tagCurrentPage.value,
        hasMoreTags: hasMoreTags.value,
        tagLoading: tagLoading.value,
        pageSize: tagPageSize.value,
      });
    }

function rowClassName(record, index) {
      // return selectIndex.value === index ? "active" : "";
    }

function customRow(record) {
  return {
    
    onClick: () => {
      // 事件
     selectRow(record)
    },
  };
}
const selectRow = (record) => {
  if (selectedRowKeys.value.includes(record.id)) {
    selectedRowKeys.value.splice(selectedRowKeys.value.indexOf(record.id), 1);
  } else {
    selectedRowKeys.value.push(record.id);
  }
  console.log(selectedRowKeys.value);
};
function editModel() {
      // selectedRowKeys.value = [];
      fetchTable();
    }

function markerDeveloping() {
      // btnLoading.value = true;
  console.log(selectedRowKeys.value);
  if (selectedRowKeys.value.length === 0) {
    createMessage.error("请先选择网红");
    btnLoading.value = false;
  } else {
    const markerDevelopingList = dataSource.value
      .filter((item) => selectedRowKeys.value.includes(item.id))
      .map(({ id, email }) => {
        return { id, kolEmail: email };
      });
    
    markerDevelopingModalRef.value?.show(
      queryParam.value.platformType,
      markerDevelopingList
    );
  }
}

function onSelectChange(selectedRowKeys, selectedRows) {
      // s-table handles row selection automatically
    }

function changeRadio(e) {
      console.log(e.target.value);
      queryParam.value.isContact = e.target.value;
      fetchTable();
    }
    function editEmail(record) {
  editEmailValue.value = record.email;
  oldEmail.value = record.email;

  updateRow(record.id, item => ({
    ...item,
    isOpen: true,
  }));
}
function selectChange(value, option) {
  brandList.value.forEach((item) => {
    if (item.brandName === value) {
      brandId.value = item.id;
    }
  });
  console.log(value, option);
  if (value) {
    queryParam.value.isContact = 0;
  } else {
    queryParam.value.isContact = undefined;
  }
}

function closeEdit(record) {
  updateRow(record.id, item => ({
    ...item,
    isOpen: false,
    email: oldEmail.value,
    loading: false,
  }));
}
function editChange(e) {
  editEmailValue.value = e.target.value;
}
function updateRow(id, updater) {
  dataSource.value = dataSource.value.map(item =>
    item.id === id ? updater(item) : item
  );
}
function check(record) {
  const email = editEmailValue.value || "";
  const reg =
    /(?=^.{3,256}$)^([\w\-]+([\.][\w\-]+)*)@[a-zA-Z0-9][a-zA-Z0-9\-]*[\.][A-Za-z]{2,6}$/;

  if (!reg.test(email) && email) {
    createMessage.error("邮箱输入错误");
    return;
  }

  // 开启 loading
  updateRow(record.id, item => ({
    ...item,
    loading: true,
  }));

  let url = "";
  if (record.platformType === 2) {
    url = "/tiktok/tkCelebrity/editEmail";
  } else if (record.platformType === 1) {
    url = "/youtube/ytCelebrity/editEmail";
  } else {
    url = "/instagram/insCelebrity/editEmail";
  }

  defHttp
    .put(
      {
        url,
        params: {
          id: record.id,
          email,
        },
      },
      { isTransformResponse: false }
    )
    .then(res => {
      if (res) {
        updateRow(record.id, item => ({
          ...item,
          email,
          isOpen: false,
          loading: false,
        }));
        createMessage.success("修改成功");
      } else {
        throw new Error();
      }
    })
    .catch(() => {
      updateRow(record.id, item => ({
        ...item,
        loading: false,
      }));
      createMessage.error("修改失败");
    });
}
function openLink(record) {
  if(record.platformType == "1") {
    window.open(`https://www.youtube.com/channel/${record.account}/`);
  } else if(record.platformType == "2") {
    window.open(`https://www.tiktok.com/@${record.uniqueId}`);
  } else {
    window.open(`https://www.instagram.com/${record.uniqueId}`);
  }
  // else  if(record.platformType == '0') {
  //    window.open(`https://www.instagram.com/${record.uniqueId}`)
  // }
}

//国家
async function initCountry() {
      const res = await getCommonCountryApi({});
      if (res) {
        countrys.value = res.result || res;
      }
    }

  //重置
function searchReset() {
      // unilList.value = [];
      accountValue.value = "";
      // emailList.value = [];
      emailValue.value = "";
      queryParam.value.tagNameList = [];
      // selectedRowKeys.value = [];
      // 注意：如果使用了 vxe-table，需要通过 ref 访问
      // vxeTableRef.value?.clearCheckboxRow();
      // vxeTableRef.value?.clearCheckboxReserve();
      
      // 重置 queryParam
      queryParam.value = {
        emailFlag: "",
        isContact: 0,
        isPrivateKol: 0,
        platformType: "2",
        countryCode: "US",
        isHasEmail: 1,
      };
      productList.value = [];

      // 重置更多筛选中的数据
      // 重置属性选择
      attributeIdsMap.value = {};

      // 重置粉丝数范围
      minFollowers.value = null;
      maxFollowers.value = null;
      selectedFollowersRanges.value = [];
      filterData.value.followersRange = {
        minFollowers: null,
        maxFollowers: null,
        selectedRanges: [],
      };

      // 重置有效视频范围
      minVideoCount.value = null;
      maxVideoCount.value = null;
      selectedVideoRanges.value = [];
      filterData.value.effectiveVideoRange = {
        minVideoCount: null,
        maxVideoCount: null,
        selectedRanges: [],
      };

      // 重置视频中位数范围
      minMedianViews.value = null;
      maxMedianViews.value = null;
      selectedMedianViewsRanges.value = [];
      filterData.value.medianViewsRange = {
        minMedianViews: null,
        maxMedianViews: null,
        selectedRanges: [],
      };

      // 重置均播数范围
      minPlayAvg.value = null;
      maxPlayAvg.value = null;
      selectedPlayAvgRanges.value = [];
      filterData.value.playAvgRange = {
        minPlayAvg: null,
        maxPlayAvg: null,
        selectedRanges: [],
      };

      // 重置标签数据
      tagType.value = undefined;
      tagNameList.value = [];
      filterData.value.tagData = {
        tagType: undefined,
        tagNameList: [],
      };

      // 重置筛选类型
      filterType.value = "";

      // 重置所有属性筛选数据
      filterData.value.attributeSelections = {};
      accountValue.value = "";
      selectedRowKeys.value = [];
      queryParam.value.allotDateRange = [
        dayjs().subtract(89, "days"),
        dayjs(),
      ];
      allotDateRadio.value = "近90天";
      onTagTypeChange(queryParam.value.platformType);
      closeTagEdit();
      clearTagData();
      fetchTable(1);
      nextTick(() => {
        calcTableHeight();
      });
      // sortedInfo.value = null
    }

function getFollower(num) {
      if(num >= 1000 && num < 1000000) {
        const kValue = (num / 1000).toFixed(0);
        return parseFloat(kValue) % 1 === 0
          ? `${parseInt(kValue)}K`
          : `${parseFloat(kValue)}K`;
      } else if(num >= 1000000) {
        const mValue = (num / 1000000).toFixed(0);
        return parseFloat(mValue) % 1 === 0
          ? `${parseInt(mValue)}M`
          : `${parseFloat(mValue)}M`;
      } else if(num > 0 && num < 1000) {
        return num;
      } else {
        return "--";
      }
    }

    // 更多筛选相关方法
function onPlatformTypeChange(value) {
      console.log("平台变更:", value);
      queryParam.value.platformType = value != null ? value.toString() : undefined;
      tagNameList.value = []
      queryParam.value.brandId = undefined;
      onTagTypeChange(value);

      initBrandList();
      onBrandChange(undefined)
      // onProductChange(queryParam.value.productId);
    }

    // 根据平台切换标签类型
function onTagTypeChange(value) {
      filterTagTypeList.value = getTagTypeListByPlatformType(value);
      const first = filterTagTypeList.value[0];
      tagType.value = first ? first.value.toString() : undefined;
      clearTagDataForVirtualScroll();
    }

async function initTagTypeAndGroup() {
      try {
        const [platformTypeRes, tagTypeRes, tagGroupRes] = await Promise.all([
          getDictItems("platform_type"),
          getDictItems("tag_type"),
          getDictItems("tag_group"),
        ]);
        platformTypeList.value = platformTypeRes || [];
        tagTypeList.value = tagTypeRes || [];
        tagGroupList.value = tagGroupRes || [];
        filterTagTypeList.value = getTagTypeListByPlatformType(queryParam.value.platformType);
        const first = filterTagTypeList.value[0];
        tagType.value = first ? first.value.toString() : undefined;
      } catch (error) {
          console.error("获取字典数据失败:", error);
      }
    }

function getTagTypeListByPlatformType(platformType) {
      if (!platformType) {
        return tagTypeList.value;
      }
      const group = tagGroupList.value.find((item) => item.title == platformType);
      const values = group?.value?.split(",") || [];
      return tagTypeList.value.filter((item) => values.includes(item.value));
    }
async function initAttributeList() {
      try {
        const res = await defHttp.get({ url: "/kol/attribute/loadTreeDataAll" });
        if (res) {
          filterOptions.value = res.map((item) => ({
            ...item,
            data: (item.data || []).map((dataItem) => ({
                ...dataItem,
                children: null,
            })),
          }));
          filterOptions.value.sort((a, b) => {
            const aIndex = socialMediaCategoryList.value.findIndex((item) => item.typeId === a.typeId);
            const bIndex = socialMediaCategoryList.value.findIndex((item) => item.typeId === b.typeId);
            if (aIndex !== -1 && bIndex !== -1) return aIndex - bIndex;
            if (aIndex !== -1 && bIndex === -1) return -1;
            if (aIndex === -1 && bIndex !== -1) return 1;
          return 0;
        });
        }
      } catch (error) {
        console.error("initAttributeList error:", error);
      }
    }

function getPopupContainer(triggerNode) {
      return (triggerNode && triggerNode.parentNode.parentNode) || document.body;
    }
function closeTagEdit() {
      tagVisible.value = false;
      tagValue.value = "";
      noTag.value = false;
    }

function clear() {
      tagValue.value = "";
      noTag.value = false;
    }

async function tagSerch(value) {
      const allTag = value
        .trim()
        .split("\n")
        .map((item) => item.trim())
        .filter((item) => item);
      try {
        const res = await defHttp.post({
          url: "/kol/tags/queryTagsListByName",
          data: {
            tagNames: allTag.join("|"),
            platformType: queryParam.value.platformType,
            tagType: tagType.value,
          }
        });
        const filterTags = (res?.result || res || []).map((item) => item.tagName.toLowerCase());
        nonExistentTag.value = allTag.filter((item) => !filterTags.includes(item.toLowerCase()));
        if (nonExistentTag.value.length > 0) {
          noTag.value = true;
        } else {
          tagNameList.value = allTag;
          tagVisible.value = false;
        }
      } catch (error) {
        console.error("tagSerch error:", error);
      }
    }

const moreFilter = () => {};

function selectFilterOther(type) {
      nextTick(() => {
        if (type === "tag" && (tagType.value === undefined || tagType.value === null)) {
          onTagTypeChange(queryParam.value.platformType);
        }
      });
      filterType.value = type;
      saveCurrentFilterData();
      restoreFilterData(type);
    }

function selectFilter(item) {
      console.log(item);
      saveCurrentFilterData();
      filterType.value = item.typeId;
      options.value = [...(item.data || [])].map((option) => ({
        id: option.id,
        attributeName: option.attributeName,

      }));
      restoreFilterData(item.typeId);
    }
function saveCurrentFilterData() {
      if (!filterType.value) {
        return;
      }
      if (filterType.value === "followersNum") {
        filterData.value.followersRange = {
          minFollowers: minFollowers.value || null,
          maxFollowers: maxFollowers.value || null,
          selectedRanges: selectedFollowersRanges.value || [],
          };
      } else if (filterType.value === "effectiveVideoNum") {
        filterData.value.effectiveVideoRange = {
          minVideoCount: minVideoCount.value || null,
          maxVideoCount: maxVideoCount.value || null,
          selectedRanges: selectedVideoRanges.value || [],
          };
      } else if (filterType.value === "medianViews") {
        filterData.value.medianViewsRange = {
          minMedianViews: minMedianViews.value || null,
          maxMedianViews: maxMedianViews.value || null,
          selectedRanges: selectedMedianViewsRanges.value || [],
        };
      } else if (filterType.value === "playAvg") {
        filterData.value.playAvgRange = {
          minPlayAvg: minPlayAvg.value || null,
          maxPlayAvg: maxPlayAvg.value || null,
          selectedRanges: selectedPlayAvgRanges.value || [],
        };
      } else if (filterType.value === "tag") {
        filterData.value.tagData = {
          tagType: tagType.value,
          tagNameList: tagNameList.value ? [...tagNameList.value] : [],
          };
      } else if (filterType.value === "account") {
        accountValue.value = accountValue.value ? accountValue.value : "";
      } else if (filterType.value === "email") {
        emailValue.value = emailValue.value ? emailValue.value : "";
        } else {
        attributeIdsMap.value[filterType.value] = attributeIds.value ? [...attributeIds.value] : [];
        }
        console.log(filterData.value);
      }

function restoreFilterData(type) {
      if (type === "followersNum") {
        const data = filterData.value.followersRange;
        minFollowers.value = data.minFollowers;
        maxFollowers.value = data.maxFollowers;
        selectedFollowersRanges.value = data.selectedRanges;
      } else if (type === "effectiveVideoNum") {
        const data = filterData.value.effectiveVideoRange;
        minVideoCount.value = data.minVideoCount;
        maxVideoCount.value = data.maxVideoCount;
        selectedVideoRanges.value = data.selectedRanges;
      } else if (type === "medianViews") {
        const data = filterData.value.medianViewsRange;
        minMedianViews.value = data.minMedianViews;
        maxMedianViews.value = data.maxMedianViews;
        selectedMedianViewsRanges.value = data.selectedRanges;
      } else if (type === "playAvg") {
        const data = filterData.value.playAvgRange;
        minPlayAvg.value = data.minPlayAvg;
        maxPlayAvg.value = data.maxPlayAvg;
        selectedPlayAvgRanges.value = data.selectedRanges;
      } else if (type === "tag") {
        const data = filterData.value.tagData || {};
        tagType.value = data.tagType;
        tagNameList.value = data.tagNameList ? [...data.tagNameList] : [];
      }
    }

function getAttributeCount(typeId) {
      const list = attributeIdsMap.value[typeId];
      return Array.isArray(list) ? list.length : 0;
    }

function getFollowersCount() {
      if (selectedFollowersRanges.value.length > 0) {
        return selectedFollowersRanges.value.length;
      }
      if (minFollowers.value || maxFollowers.value) {
        return 1;
      }
      return 0;
    }

function getVideoCount() {
      if (selectedVideoRanges.value.length > 0) {
        return selectedVideoRanges.value.length;
      }
      if (minVideoCount.value || maxVideoCount.value) {
        return 1;
      }
      return 0;
    }

function getMedianViewsCount() {
      if (selectedMedianViewsRanges.value.length > 0) {
        return selectedMedianViewsRanges.value.length;
      }
      if (minMedianViews.value || maxMedianViews.value) {
        return 1;
      }
      return 0;
    }

function getPlayAvgCount() {
      if (selectedPlayAvgRanges.value.length > 0) {
        return selectedPlayAvgRanges.value.length;
      }
      if (minPlayAvg.value || maxPlayAvg.value) {
        return 1;
      }
      return 0;
    }

function getTagCount() {
      return tagNameList.value?.length || 0;
      }

function getAccountCount() {
      if (accountValue.value && accountValue.value.length > 0) {
        return accountValue.value
          .split("\n")
          .filter((item) => item.trim() !== "").length;
      }
      return 0;
    }

function getEmailCount() {
      if (emailValue.value && emailValue.value.length > 0) {
        return emailValue.value
          .split("\n")
          .filter((item) => item.trim() !== "").length;
      }
      return 0;
    }
function selectFollowersRange(range) {
      minFollowers.value = null;
      maxFollowers.value = null;
      selectedFollowersRanges.value = [range];
      filterData.value.followersRange = {
        minFollowers: minFollowers.value,
        maxFollowers: maxFollowers.value,
        selectedRanges: selectedFollowersRanges.value,
      };
    }

function selectVideoRange(range) {
      minVideoCount.value = null;
      maxVideoCount.value = null;
      selectedVideoRanges.value = [range];
      filterData.value.effectiveVideoRange = {
        minVideoCount: minVideoCount.value,
        maxVideoCount: maxVideoCount.value,
        selectedRanges: selectedVideoRanges.value,
      };
    }

function onFollowersInputChange() {
      selectedFollowersRanges.value = [];
      filterData.value.followersRange = {
        minFollowers: minFollowers.value,
        maxFollowers: maxFollowers.value,
        selectedRanges: selectedFollowersRanges.value,
      };
    }

function onVideoInputChange() {
      selectedVideoRanges.value = [];
      filterData.value.effectiveVideoRange = {
        minVideoCount: minVideoCount.value,
        maxVideoCount: maxVideoCount.value,
        selectedRanges: selectedVideoRanges.value,
      };
    }

function selectMedianViewsRange(range) {
      minMedianViews.value = null;
      maxMedianViews.value = null;
      selectedMedianViewsRanges.value = [range];
      filterData.value.medianViewsRange = {
        minMedianViews: minMedianViews.value,
        maxMedianViews: maxMedianViews.value,
        selectedRanges: selectedMedianViewsRanges.value,
      };
    }

function onMedianViewsInputChange() {
      selectedMedianViewsRanges.value = [];
      filterData.value.medianViewsRange = {
        minMedianViews: minMedianViews.value,
        maxMedianViews: maxMedianViews.value,
        selectedRanges: selectedMedianViewsRanges.value,
      };
    }

function selectPlayAvgRange(range) {
      minPlayAvg.value = null;
      maxPlayAvg.value = null;
      selectedPlayAvgRanges.value = [range];
      filterData.value.playAvgRange = {
        minPlayAvg: minPlayAvg.value,
        maxPlayAvg: maxPlayAvg.value,
        selectedRanges: selectedPlayAvgRanges.value,
      };
    }

function onPlayAvgInputChange() {
      selectedPlayAvgRanges.value = [];
      filterData.value.playAvgRange = {
        minPlayAvg: minPlayAvg.value,
        maxPlayAvg: maxPlayAvg.value,
        selectedRanges: selectedPlayAvgRanges.value,
      };
    }

function applyAllFilters(isSearch) {
      isApplyClose.value = true;
      saveCurrentFilterData();

      const followersRange = filterData.value.followersRange;
      const hasPresetRange =
        followersRange.selectedRanges && followersRange.selectedRanges.length > 0;
      let minFollowersValue = null;
      let maxFollowersValue = null;
      if (hasPresetRange) {
        const range = followersRange.selectedRanges[0];
        const rangeValues = parseFollowersRange(range);
        minFollowersValue = rangeValues.min ? rangeValues.min * 1000 : null;
        maxFollowersValue = rangeValues.max ? rangeValues.max * 1000 : null;
      } else {
        minFollowersValue = followersRange.minFollowers
          ? followersRange.minFollowers * 1000
          : null;
        maxFollowersValue = followersRange.maxFollowers
          ? followersRange.maxFollowers * 1000
          : null;
      }

      queryParam.value.minFollowers = minFollowersValue;
      queryParam.value.maxFollowers = maxFollowersValue;
      queryParam.value.minVideoStandardCount =
        filterData.value.effectiveVideoRange.minVideoCount != null ? filterData.value.effectiveVideoRange.minVideoCount : null;
      queryParam.value.maxVideoStandardCount =
        filterData.value.effectiveVideoRange.maxVideoCount != null ? filterData.value.effectiveVideoRange.maxVideoCount : null;

      // 处理视频中位数范围
      const medianViewsRange = filterData.value.medianViewsRange;
      const hasMedianViewsPresetRange =
        medianViewsRange.selectedRanges && medianViewsRange.selectedRanges.length > 0;
      let minMedianViewsValue = null;
      let maxMedianViewsValue = null;
      if (hasMedianViewsPresetRange) {
        const range = medianViewsRange.selectedRanges[0];
        const rangeValues = parseFollowersRange(range);
        minMedianViewsValue = rangeValues.min ? rangeValues.min * 1000 : null;
        maxMedianViewsValue = rangeValues.max ? rangeValues.max * 1000 : null;
      } else {
        minMedianViewsValue = medianViewsRange.minMedianViews
          ? medianViewsRange.minMedianViews * 1000
          : null;
        maxMedianViewsValue = medianViewsRange.maxMedianViews
          ? medianViewsRange.maxMedianViews * 1000
          : null;
      }
      queryParam.value.minMedianViews = minMedianViewsValue;
      queryParam.value.maxMedianViews = maxMedianViewsValue;

      // 处理均播数范围
      const playAvgRange = filterData.value.playAvgRange;
      const hasPlayAvgPresetRange =
        playAvgRange.selectedRanges && playAvgRange.selectedRanges.length > 0;
      let minPlayAvgValue = null;
      let maxPlayAvgValue = null;
      if (hasPlayAvgPresetRange) {
        const range = playAvgRange.selectedRanges[0];
        const rangeValues = parsePlayAvgRange(range);
        minPlayAvgValue = rangeValues.min ? rangeValues.min * 1000 : null;
        maxPlayAvgValue = rangeValues.max ? rangeValues.max * 1000 : null;
      } else {
        minPlayAvgValue = playAvgRange.minPlayAvg ? playAvgRange.minPlayAvg * 1000 : null;
        maxPlayAvgValue = playAvgRange.maxPlayAvg ? playAvgRange.maxPlayAvg * 1000 : null;
      }
      queryParam.value.minPlayAvgNum = minPlayAvgValue;
      queryParam.value.maxPlayAvgNum = maxPlayAvgValue;

      if (tagNameList.value && tagNameList.value.length > 0) {
        queryParam.value.tagNameList = [...tagNameList.value];
      } else {
        delete queryParam.value.tagNameList;
      }

      if (accountValue.value && accountValue.value.length > 0) {
        queryParam.value.uniqueId = accountValue.value
          .split("\n")
          .map((item) => item.trim())
          .filter((item) => item !== "")
          .join(",");
      } else {
        delete queryParam.value.uniqueId;
      }

      if (emailValue.value && emailValue.value.length > 0) {
        queryParam.value.emailKeyword = emailValue.value
          .split("\n")
          .map((item) => item.trim())
          .filter((item) => item !== "")
          .join(",");
      } else {
        delete queryParam.value.emailKeyword;
      }

      const uniqueAttributeIds = [
        ...new Set(
          Object.values(attributeIdsMap.value)
            .filter((item) => Array.isArray(item))
            .flat()
        ),
      ];
      if (uniqueAttributeIds.length > 0) {
        queryParam.value.attributeIds = uniqueAttributeIds.join(",");
      } else {
        delete queryParam.value.attributeIds;
      }

      if (isSearch) {
      
        fetchTable();
      }

      closeAllPopovers();
      nextTick(() => {
        calcTableHeight();
      });
    }

function getAllFilterData() {
      const result = {
        attributeFilters: {},
        followersRange: filterData.value.followersRange,
        effectiveVideoRange: filterData.value.effectiveVideoRange,
        tagData: filterData.value.tagData,
      };
      Object.keys(attributeIdsMap.value).forEach((typeId) => {
        const list = attributeIdsMap.value[typeId];
        if (list && list.length > 0) {
          result.attributeFilters[typeId] = list;
        }
      });
      return result;
    }

function clearEmailData() {
      delete queryParam.value.emailKeyword;
      emailValue.value = "";
      closeEmailEdit();
      }

function clearUniqueIdData() {
      delete queryParam.value.uniqueId;
      uniqueIdList.value = [];
      accountValue.value = undefined;
      console.log(accountValue.value);
      closeAccountEdit();
    }

function clearFollowersData() {
      delete queryParam.value.minFollowers;
      delete queryParam.value.maxFollowers;
      minFollowers.value = null;
      maxFollowers.value = null;
      selectedFollowersRanges.value = [];
      filterData.value.followersRange = {
        minFollowers: null,
        maxFollowers: null,
        selectedRanges: [],
      };
    }

function clearVideoData() {
      delete queryParam.value.minVideoStandardCount;
      delete queryParam.value.maxVideoStandardCount;
      minVideoCount.value = undefined;
      maxVideoCount.value = undefined;
  
      console.log(minVideoCount.value,maxVideoCount.value);
      filterData.value.effectiveVideoRange = {
        minVideoCount: null,
        maxVideoCount: null,
       
      };
    }

function clearMedianViewsData() {
      delete queryParam.value.minMedianViews;
      delete queryParam.value.maxMedianViews;
      minMedianViews.value = null;
      maxMedianViews.value = null;
      selectedMedianViewsRanges.value = [];
      filterData.value.medianViewsRange = {
        minMedianViews: null,
        maxMedianViews: null,
        selectedRanges: [],
      };
    }

function clearPlayAvgData() {
      delete queryParam.value.minPlayAvgNum;
      delete queryParam.value.maxPlayAvgNum;
      minPlayAvg.value = null;
      maxPlayAvg.value = null;
      selectedPlayAvgRanges.value = [];
      filterData.value.playAvgRange = {
        minPlayAvg: null,
        maxPlayAvg: null,
        selectedRanges: [],
      };
    }

function clearTagData() {
      delete queryParam.value.tagNameList;
      tagNameList.value = [];
      queryParam.value.tagNameList = [];
      queryParam.value.tagType =
        filterTagTypeList.value.length > 0 ? filterTagTypeList.value[0].value : undefined;
      tagValue.value = "";
      tagVisible.value = false;
      noTag.value = false;
      nonExistentTag.value = [];
      filterData.value.tagData = {
        tagType: undefined,
        tagNameList: [],
      };
      closeTagEdit();
    }

function clearAttributeFilter(event, attributeType) {
      if (event) {
        event.stopPropagation();
        event.preventDefault();
      }
      closeAllPopovers();
      if (attributeType){

        attributeIdsMap.value[attributeType] = [];
      }else{
        filterOptions.value.forEach(item => {
          attributeIdsMap.value[item.typeId] = [];
        })
      }
      const uniqueAttributeIds = [
        ...new Set(
          Object.values(attributeIdsMap.value)
            .filter((item) => Array.isArray(item))
            .flat()
        ),
      ];
      if (uniqueAttributeIds.length > 0) {
        queryParam.value.attributeIds = uniqueAttributeIds.join(",");
      } else {
        delete queryParam.value.attributeIds;
      }

      fetchTable();
    }

function isShowAttribute(data) {
      if (!queryParam.value.attributeIds) {
          return false;
        }
      const arr = queryParam.value.attributeIds.split(",");
      return data.some((item) => arr.includes(item.id));
      }

function parseAttributeIds(ids, data) {
      if (ids) {
        const arr = ids.split(",");
        const res = data.filter((item) => arr.includes(item.id));
        return res.map((item) => item.attributeName).join(",");
      }
        return "";
      }

function parseData(data) {
      return data.map((item) => ({
          id: item.id,
          attributeName: item.attributeName,
      }));
    }

function onMainPopoverShow() {
      mainFilterPopoverVisible.value = true;
      if (!filterType.value && filterOptions.value.length > 0) {
        const firstOption = filterOptions.value[0];
        filterType.value = firstOption.typeId;
        options.value = [...(firstOption.data || [])].map((item) => ({
            id: item.id,
            attributeName: item.attributeName,
            disabled:
             queryParam.value.brandId ||
             queryParam.value.productId ||
              (queryParam.value.productAttributeIds &&
               queryParam.value.productAttributeIds.length > 0) ||
              (queryParam.value.tagNameList &&queryParam.value.tagNameList.length > 0)
                ? true
                : false,
        }));
        restoreFilterData(firstOption.typeId);
      } else if (!filterType.value) {
        filterType.value = "followersNum";
        restoreFilterData("followersNum");
      }
      onPopoverShow();
      }

function onMainPopoverHide() {
      mainFilterPopoverVisible.value = false;
      filterType.value = "";
      options.value = [];
      onPopoverHide();
    }

function onEmailPopoverShow() {
      emailFilterPopoverVisible.value = true;
      onPopoverShow();
    }

function onEmailPopoverHide() {
      emailFilterPopoverVisible.value = false;
      onPopoverHide();
    }

function onAccountPopoverShow() {
      accountFilterPopoverVisible.value = true;
      onPopoverShow();
    }

function onAccountPopoverHide() {
      accountFilterPopoverVisible.value = false;
      onPopoverHide();
    }

function onTagPopoverShow() {
      tagFilterPopoverVisible.value = true;
      onPopoverShow();
    }

function onTagPopoverHide() {
      tagFilterPopoverVisible.value = false;
      onPopoverHide();
    }

function onFollowersPopoverShow() {
      followersFilterPopoverVisible.value = true;
      onPopoverShow();
    }

function onFollowersPopoverHide() {
      followersFilterPopoverVisible.value = false;
      onPopoverHide();
    }

function onVideoPopoverShow() {
      videoFilterPopoverVisible.value = true;
      onPopoverShow();
    }

function onVideoPopoverHide() {
      videoFilterPopoverVisible.value = false;
      onPopoverHide();
    }

function onMedianViewsPopoverShow() {
      medianViewsFilterPopoverVisible.value = true;
      onPopoverShow();
    }

function onMedianViewsPopoverHide() {
      medianViewsFilterPopoverVisible.value = false;
      onPopoverHide();
    }

function onPlayAvgPopoverShow() {
      playAvgFilterPopoverVisible.value = true;
      onPopoverShow();
    }

function onPlayAvgPopoverHide() {
      playAvgFilterPopoverVisible.value = false;
      onPopoverHide();
    }

function closeAllPopovers() {
  mainFilterPopoverRef.value?.hide?.();
  followersFilterPopoverRef.value?.hide?.();
  videoFilterPopoverRef.value?.hide?.();
  medianViewsFilterPopoverRef.value?.hide?.();
  playAvgFilterPopoverRef.value?.hide?.();
  tagFilterPopoverRef.value?.hide?.();
  accountFilterPopoverRef.value?.hide?.();
  emailFilterPopoverRef.value?.hide?.();
    // 关闭属性筛选弹窗
  if (filterOptions.value && Array.isArray(filterOptions.value)) {
    filterOptions.value.forEach((item) => {
      if (item && item.typeId) {
 
        attributeFilterPopoverVisible.value[item.typeId] = false;
      }
    });
  }
}

function onPopoverShow() {
      isApplyClose.value = false;
      backupData.value = {
        attributeIdsMap: JSON.parse(JSON.stringify(attributeIdsMap.value)),
        filterData: JSON.parse(JSON.stringify(filterData.value)),
        minFollowers: minFollowers.value,
        maxFollowers: maxFollowers.value,
        selectedFollowersRanges: [...selectedFollowersRanges.value],
        minVideoCount: minVideoCount.value,
        maxVideoCount: maxVideoCount.value,
        selectedVideoRanges: [...selectedVideoRanges.value],
        minMedianViews: minMedianViews.value,
        maxMedianViews: maxMedianViews.value,
        selectedMedianViewsRanges: [...selectedMedianViewsRanges.value],
        minPlayAvg: minPlayAvg.value,
        maxPlayAvg: maxPlayAvg.value,
        selectedPlayAvgRanges: [...selectedPlayAvgRanges.value],
        tagType: tagType.value,
        tagNameList: tagNameList.value ? [...tagNameList.value] : [],
        accountValue: accountValue.value ? accountValue.value : "",
        emailValue: emailValue.value ? emailValue.value : "",
      };
    }

function onPopoverHide() {
      if (!isApplyClose.value && backupData.value) {
        attributeIdsMap.value = JSON.parse(JSON.stringify(backupData.value.attributeIdsMap));
        Object.assign(filterData.value, JSON.parse(JSON.stringify(backupData.value.filterData)));
        minFollowers.value = backupData.value.minFollowers;
        maxFollowers.value = backupData.value.maxFollowers;
        selectedFollowersRanges.value = [...backupData.value.selectedFollowersRanges];
        minVideoCount.value = backupData.value.minVideoCount;
        maxVideoCount.value = backupData.value.maxVideoCount;
        selectedVideoRanges.value = [...backupData.value.selectedVideoRanges];
        minMedianViews.value = backupData.value.minMedianViews;
        maxMedianViews.value = backupData.value.maxMedianViews;
        selectedMedianViewsRanges.value = [...backupData.value.selectedMedianViewsRanges];
        minPlayAvg.value = backupData.value.minPlayAvg;
        maxPlayAvg.value = backupData.value.maxPlayAvg;
        selectedPlayAvgRanges.value = [...backupData.value.selectedPlayAvgRanges];
        tagType.value = backupData.value.tagType;
        tagNameList.value = backupData.value.tagNameList
          ? [...backupData.value.tagNameList]
          : [];
        accountValue.value = backupData.value.accountValue || "";
        emailValue.value = backupData.value.emailValue || "";
      }
      isApplyClose.value = false;
    }
    // 通用清除筛选数据方法
function clearFilterData(event, filterType) {
      console.log(`清除${filterType}数据`);
      if(event) {
        event.stopPropagation();
        event.preventDefault();
        event.stopImmediatePropagation();
      }
      closeAllPopovers();
      switch(filterType) {
        case "followers":
          clearFollowersData();
          break;
        case "video":
          clearVideoData();
          break;
        case "medianViews":
          clearMedianViewsData();
          break;
        case "playAvg":
          clearPlayAvgData();
          break;
        case "tag":
          clearTagData();
          break;
        case "attribute":
          clearAttributeFilter(event, event && event.attributeType ? event.attributeType : "");
          break;
        case "uniqueId":
          clearUniqueIdData();
          break;
        case "email":
          clearEmailData();
          break;
        default:
          console.warn(`未知的筛选类型: ${filterType}`);
          return;
      }

      // 手动触发查询以更新界面
      nextTick(() => {
        console.log('清除筛选数据');
        calcTableHeight();
      });
      fetchTable();
      videoFilterPopoverVisible.value = false;
      // onPopoverHide();
    }

    // 属性筛选弹窗显示状态管理
function onAttributePopoverShow(typeId) {
      attributeFilterPopoverVisible.value[typeId] = true;
      onPopoverShow();
    }

function onAttributePopoverHide(typeId) {
      attributeFilterPopoverVisible.value[typeId] = false;
      onPopoverHide();
    }

function clearAllFilters() {
      clearAttributeFilter(null, null);
      clearEmailData();
      clearUniqueIdData();
      clearTagData();
      clearFollowersData();
      clearVideoData();
      clearMedianViewsData();
      clearPlayAvgData();
      attributeIdsMap.value = {};
      filterData.value.attributeSelections = {};
      queryParam.value.isHasEmail = 1;
      queryParam.value.isContact = 0;
      queryParam.value.isPrivateKol = 0;
      queryParam.value.platformType = "2";
      queryParam.value.countryCode = "US";
      closeAllPopovers();
      nextTick(() => {
    calcTableHeight();
  });
     
      fetchTable();
    }

function parseFollowersRange(range) {
      switch (range) {
        case "1K-10K":
          return { min: 1, max: 10 };
        case "10K-20K":
          return { min: 10, max: 20 };
        case "20K-50K":
          return { min: 20, max: 50 };
        case "50K-100K":
          return { min: 50, max: 100 };
        case "100K+":
          return { min: 100, max: null };
        default:
          return { min: null, max: null };
      }
    }

function parsePlayAvgRange(range) {
      switch (range) {
        case "0-10K":
          return { min: 0, max: 10 };
        case "10K-30K":
          return { min: 10, max: 30 };
        case "30K-100K":
          return { min: 30, max: 100 };
        case "100K-300K":
          return { min: 100, max: 300 };
        case "300K+":
          return { min: 300, max: null };
        default:
          return { min: null, max: null };
      }
    }

function getOffSetNum() {
      // try {
      //   let height = document.body.clientHeight;
      //   let seach = 0;

      //   if (document.querySelector('.table-page-search-wrapper') != null) {
      //     seach = document.querySelector('.table-page-search-wrapper').offsetHeight + seach;
      //   }

      //   offsetHeight.value = height - 81 - seach - 42 - 32 + 'px';
      // } catch (e) {
      //   console.error('getOffSetNum error:', e);
      // }
    }
</script>
<style>
  .searchForm {
  background-color: white;
  padding: 12px 12px 4px 12px;
  margin-bottom: 12px;
}
  .allotDateRangeradio .ant-radio-group {
  display: flex !important;
}
.allotDateRangeradio .ant-radio-button-wrapper {
  padding: 0 10px !important;
}
.allotDateRangeradio .ant-radio-button-wrapper {
  flex: 1 !important;
  text-align: center;
}
  .isContactCol .ant-radio-group{
    width: 100% !important;
  }
  .isContactCol .ant-radio-group  .ant-radio-button-wrapper{
    width: 50% !important;
    text-align: center !important;
  }
.tagTypePopover .ant-popover-inner-content {
  padding: 10px !important;
}
.taglistRow .ant-tag {
  max-width: 170px !important;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
<style scoped lang="less">
.searchForm {
  background-color: white;
  padding: 12px 12px 4px 12px;
  margin-bottom: 12px;
}
.myInternetCelebrityList{
  padding: 12px;
}
/deep/ .tagTypePopover .ant-popover-inner-content {
  padding: 10px !important;
}
/deep/ .ant-radio-button-wrapper:not(:first-child)::before {
  width: 0.1px !important;
  background-color: transparent;
}
/deep/ .ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before {
  background-color: #3155ed !important;
}
/deep/ .tagListColumn .vxe-cell {
  white-space: normal !important;
  word-wrap: break-word !important;
  word-break: break-word !important; /* 根据需要选择是否开启 */
  overflow: hidden !important; /* 或 visible */
}
.rotating-arrow-filter-icon {
  color:  rgba(0, 0, 0, 0.25);
  /* 启用过渡动画 */
  transition: transform 0.3s ease;
  /* 定义变换的中心点为图标中心 */
  transform-origin: center center;
}
/* 当 mainFilterPopoverVisible 为 true 时，图标旋转 180 度 */
.main-filter-open .rotating-arrow-filter-icon {
  transform:  rotate(180deg);
}

/* 当 mainFilterPopoverVisible 为 false 时，图标不旋转 (0 度) */
.main-filter-closed .rotating-arrow-filter-icon {
  transform:  rotate(0deg);
}
/deep/ .white-bg {
  background-color: #fff;
}
/deep/ .input-num .ant-input-number-handler-wrap {
  display: none !important;
}
/deep/ .vxe-header--gutter {
  position: sticky !important;
  right: 0px !important;
  z-index: 1 !important;
  background: #f0f3fe !important;
}
/deep/ .vxe-table--render-default.border--default .vxe-body--column {
  background-image: none !important;
  border-bottom: 1px solid #e8eaec !important;
}
// /deep/ .vxe-table--body-wrapper {
//   scroll-behavior: smooth !important;
// }
/deep/ .vxe-cell--checkbox {
  display: flex !important;
  align-items: center !important;
}

/deep/ .vxe-header--column {
  background: #f0f3fe !important;
  color: #0b1019 !important;

  font-weight: 700 !important;
}
/deep/ .col--ellipsis {
  max-height: none !important;
}
/deep/ .vxe-cell {
  color: #0b1019 !important;
}
/deep/ .vxe-table--render-default.size--small .vxe-body--column.col--ellipsis,
/deep/ .vxe-table--render-default.size--small .vxe-footer--column.col--ellipsis,
/deep/ .vxe-table--render-default.size--small .vxe-header--column.col--ellipsis,
/deep/ .vxe-table--render-default.vxe-editable.size--small .vxe-body--column {
  // max-height: none !important;
  height: 88px !important;
}

/deep/ .vxe-table--render-default.size--small .vxe-body--column.col--ellipsis > .vxe-cell,
/deep/
  .vxe-table--render-default.size--small
  .vxe-footer--column.col--ellipsis
  > .vxe-cell,
/deep/
  .vxe-table--render-default.size--small
  .vxe-header--column.col--ellipsis
  > .vxe-cell {
  max-height: none !important;
}
.contactHistoryTag {
  width: 124px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}
.celebrityPrivate {
  background-color: #EEF2FF;
  color: #3155ED;
  border-radius: 6px;
  font-size: 12px;
  border: none;
  margin-right: 0px;
}
.contactHistory_noData {
  display: flex;
  gap: 4px;
  align-items: center;
  color: #ADADAD;
}
.contactHistory_noData .circle {
  width: 10px;
  height: 10px;
  border-radius: 100%;
  background-color: #ADADAD;
}
.colum_account {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #0b1019 !important;
}
/deep/ .platform-type-radio-group .ant-radio-group {
  // margin-top: 1px !important;
  display: flex !important;
}
/deep/ .platform-type-radio-group .ant-radio-group .ant-radio-button-wrapper {
  flex: 1 !important;
}
/deep/ .ant-dropdown-menu-item-active {
  color: @primary-color !important;
}
/deep/ .el-cascader-menu {
  width: 100% !important;
}
/deep/ .el-cascader-panel {
  height: 100% !important;
  width: 100% !important;
  border: none !important;
  height: 291px !important;
}
/deep/ .el-cascader-menu__wrap {
  height: 100% !important;
}
.more-filter-popover {
  padding: 0 !important;
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
        .range-input__custom-label {
          margin-bottom: 4px;
          color: #121415;
          font-size: 14px;
          font-weight: 600;
        }
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

/* 虚拟滚动相关样式 */
.ant-select-dropdown {
  .ant-select-item-option {
    &.ant-select-item-option-disabled {
      cursor: default;

      .ant-spin {
        margin-right: 8px;
      }

      span {
        color: #999;
        font-size: 12px;
      }
    }
  }
}

/* 加载状态指示器样式 */
.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 12px;
  color: #999;
  font-size: 12px;

  .ant-spin {
    margin-right: 8px;
  }
}
/* 选中个数样式 */
.selected-count {
  display: inline-block;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  background-color: @primary-color;
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
  display: inline-block;
  align-items: center;
  cursor: pointer;
  height: 32px;
  line-height: 32px;
  font-size: 12px;
  background-color: #fff;
  text-align: center;
}
/deep/ .more-filter-button span {
  display: inline-block !important;
  max-width: 250px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
  // line-height: 32px;
}
/deep/ .more-filter-button span:nth-child(2) {
  font-weight: 600;
}
/deep/ .ant-radio-button-wrapper {
  height: 32px !important;
  // line-height: 32px !important;
}
.colum_img {
  display: flex;
  align-items: center;
}
.platformType-img {
  display: inline-block;
  width: 24px;
  height: 24px;
  position: absolute;
  bottom: 18px;
  left: 114px;
  
}
.colum_avatarUrl {
  width: 70px;
  height: 70px;
  line-height: 70px;
  margin-right: 20px;
  display: flex;
  // position: relative;
}
.celebrityPrivateId {
  border: 1px solid orange;
  border-radius: 20%;
  color: orange;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.celebrityPrivateId1 {
  border: 1px solid #52c41a;
  border-radius: 20%;
  color: #52c41a;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.account {
  display: inline-block;
  width: 80px;
}

.bioLink a {
  text-decoration: underline;
}
</style>
<style>
.custom-dropdown {
  padding: 4px 0;
}

.virtual-list {
  height: 256px;
  overflow-y: auto;
}

.virtual-option {
  padding: 5px 12px;
  cursor: pointer;

  &:hover {
    background-color: #f5f5f5;
  }

  &.ant-select-item-option-selected {
    background-color: #e6f7ff;
    font-weight: 600;
  }
}
.clear-all-button {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 15px;
  display: inline-block;
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
</style>
<style>

.tag-overflow-tooltip {
  max-width: 300px;
}
</style>



