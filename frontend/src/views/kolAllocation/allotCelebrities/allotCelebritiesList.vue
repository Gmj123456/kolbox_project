<template>
 
    <div class="allotCelebritiesList">
      <div class="searchForm">
        <a-form  @keyup.enter.native="searchQuery">
          <a-row :gutter="12">
            <a-col :xl="3" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <JDictSelectPlatformType
                  v-model:value="queryParam.platformType"
                  dictCode="platform_type"
                  :triggerChange="true"
                  @change="onPlatformTypeChange"
                ></JDictSelectPlatformType>
              </a-form-item>
            </a-col>
            <a-col :xl="searchType == 0 ? 14 : 6" :lg="24" :md="24" :sm="24">
              <a-form-item>
                <a-input-group compact >
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
              
                      v-model:value="queryParam.brandId"
                      placeholder="品牌"
                      option-filter-prop="label"
                      @change="onBrandChange"
                      :dropdownStyle="{ width: '150px' }"
                    >
                      <a-select-option v-for="item in brandList" :key="item.id" :value="item.id" :label="item.brandName">{{
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
                      style="width: 200px;border-right: 0px;"
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
                      class="productAttribute"
                      mode="multiple"
                      style="width: calc(100% - 400px)"
                      show-search
                      :disabled="
                        queryParam.attributeIds ||
                        (queryParam.tagNameList && queryParam.tagNameList.length > 0)
                          ? true
                          : false
                      "
                      option-filter-prop="label"
                      v-model:value="queryParam.productAttributeIds"
                      placeholder="达人类型"
                      allowClear
                      :dropdownMatchSelectWidth="false"
                      :dropdownStyle="{ width: '350px' }"
                      :maxTagCount="5"
                    >
                      <template #maxTagPlaceholder>
                        <a-tooltip>
                          <template #title>
                            <div style="max-height: 200px; overflow-y: auto">
                              <div
                                v-for="(a, i) in queryParam.productAttributeIds.slice(5)"
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
                              queryParam.productAttributeIds.length > 5
                            "
                            >+
                            {{ queryParam.productAttributeIds.length - 5 }}
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
                    style="width: calc(100% - 100px)"
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
            <a-col :xl="7" :lg="10" :md="12" :sm="24">
              <a-form-item>
                <a-input-group compact>
                  <a-select
                    style="width: 130px;border-right: 0px;"
                    
                    v-model:value="tagType"
                    @change="onTagTypeChange"
                  >
                    <a-select-option
                      v-for="item in filterTagTypeList"
                      :key="item.value"
                      :value="item.value"
                    >
                      {{ item.title }}
                    </a-select-option>
                  </a-select>
                  <a-select
                  :disabled="
                      queryParam.brandId ||
                      queryParam.productId ||
                      (queryParam.productAttributeIds &&
                        queryParam.productAttributeIds.length > 0) ||
                      (queryParam.attributeIds && queryParam.attributeIds.length > 0)
                        ? true
                        : false
                    "
                    class="tagSelect"
                    style="width: calc(100% - 130px)"
                    :autoClearSearchValue="false"
                    @search="fetchUser"
                  
                    @change="handleChange"
                  
                    :maxTagCount="queryParam.platformType == 1 ? 2 : 3"
                    mode="multiple"
                    v-model:value="queryParam.tagNameList"
                    allowClear
                    placeholder="标签"
                    :not-found-content="fetching ? undefined : null"
                    @popup-scroll="tagPopupScroll"
                  >
                    <template #maxTagPlaceholder>
                      <a-tooltip>
                        <template #title>
                          <div style="max-height: 200px; overflow-y: auto">
                            <div
                              v-for="(item, i) in queryParam.tagNameList.slice(
                                queryParam.platformType == 1 ? 2 : 3
                              )"
                              :key="i"
                            >
                              <div>{{ item }}</div>
                            </div>
                          </div>
                        </template>
                        <span v-if="queryParam.tagNameList"
                          >+
                          {{
                            queryParam.platformType == 1
                              ? queryParam.tagNameList.length - 2
                              : queryParam.tagNameList.length - 3
                          }}
                        </span>
                      </a-tooltip>
                    </template>
                    <template #notFoundContent>
                      <a-spin v-if="fetching" size="small" />
                    </template>
                    <a-select-option
                      v-for="(tag, index) in tags"
                      :key="index"
                      :value="tag.tagName"
                    >
                      {{ tag.tagName }}
                    </a-select-option>
                  </a-select>
                  <a-popover
                    :destroyTooltipOnHide="true"
                    :visible="tagVisible"
                    :getPopupContainer="(trigger) => trigger.parentNode"
                    trigger="click"
                    placement="bottomRight"
                  >
                    <template #content>
                      <div style="width: 270px">
                        
                        <a-textarea
                        
                          v-model:value="tagValue"
                          :auto-size="{ minRows: 6, maxRows: 6 }"
                          placeholder="精确输入，一行一项"
                          rows=""
                          cols=""
                        ></a-textarea>

                        <div
                          style="color: red; margin-top: 5px; max-height: 100px; overflow-y: auto"
                          v-if="noTag"
                        >
                          不存在的标签名：
                          <div
                            style="margin-right: 5px"
                            v-for="item in nonExistentTag"
                            :key="item"
                          >
                            {{ item }}
                          </div>
                        </div>
                        <div style="margin-top: 10px;display: flex;justify-content: space-between">
                          <a-button @click="closeTagEdit" style="margin-right: 50px"
                            >关闭</a-button
                          >
                          <div>
                            <a-button @click="clear" style="margin-right: 8px">清空</a-button>
                            <a-button @click="tagSerch(tagValue)">确定</a-button>
                          </div>
                        </div>
                      </div>
                    </template>
                    <div
                      style="
                        position: absolute;
                        right: 24px;
                        top: 4px;
                        width: 24px;
                        height: 24px;
                        background-color: #f0f2f5;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        z-index: 1000;
                      "
                    >
                      <AlignLeftOutlined
                      v-if="
                          !(
                            queryParam.brandId ||
                            queryParam.productId ||
                            (queryParam.productAttributeIds &&
                              queryParam.productAttributeIds.length > 0) ||
                            (queryParam.attributeIds && queryParam.attributeIds.length > 0)
                          )
                        "
                        class="tagIcon"
                        :style="{ color: tagVisible ? 'blue' : '' }"
                        @click="tagPopover"
                      />
                    </div>
                  </a-popover>
                </a-input-group>
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
                  v-model:value="queryParam.isHasEmail"
                  @change="(e) => (e === 0 ? (queryParam.emailKeyword = undefined) : true)"
                >
                  <a-select-option value=" "> 全部账号 </a-select-option>
                  <a-select-option :value="1"> 含邮箱账号 </a-select-option>
                  <a-select-option :value="0"> 不含邮箱账号 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="2" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-input
                  :disabled="queryParam.isHasEmail === 0 ? true : false"
                  v-model:value="queryParam.emailKeyword"
                  placeholder="邮箱"
                  allowClear
                ></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="10" :lg="12" :md="16" :sm="24">
              <a-form-item>
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
                                <i style="color: #606266" class="el-icon-arrow-right"></i>
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
                                <i style="color: #606266" class="el-icon-arrow-right"></i>
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
                                <i style="color: #606266" class="el-icon-arrow-right"></i>
                              </span>
                            </div>
                          </div>

                          <div
                            v-if="
                              filterType !== 'followersNum' &&
                              filterType !== 'effectiveVideoNum' &&
                              filterType !== 'medianViews' &&
                              filterType !== 'playAvg' &&
                              filterType !== 'tag' &&
                              filterType !== 'account'
                            "
                            class="more-filter-form"
                          >
                            <!-- <div style="padding: 8px; background: #f5f5f5; margin-bottom: 8px; font-size: 12px;">
                                    当前筛选类型: {{ filterType }} | 已选择: {{ attributeIds.length }} 项
                                  </div> -->
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
                                      v-model:value="minFollowers"
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
                                      v-model:value="maxFollowers"
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
                                      v-model:value="minVideoCount"
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
                                      v-model:value="maxVideoCount"
                                      placeholder="输入值"
                                      @change="onVideoInputChange"
                                    ></a-input-number>
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
                          <div class="more-filter-form" v-if="filterType === 'account'">
                            <div class="range-input">
                              <div style="display: flex; align-items: center">
                                <a-textarea
                              
                                  v-model:value="accountValue"
                                  :auto-size="{ minRows: 12, maxRows: 12 }"
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
                            >应用</a-button
                          >
                          <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
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
                          <!-- <a-icon  type="down" /> -->
                          <DownOutlined class="rotating-arrow-filter-icon"/>
                        </a-button>
                      </template>
                    </el-popover>
                      <a-button
                        type="primary"
                        @click="searchQuery"
                      
                        :icon="h(SearchOutlined)"
                        style="margin-left: 8px"
                      ></a-button>
                  
                      <a-button
                        @click="searchReset"
                        :icon="h(ReloadOutlined)"
                        style="margin-left: 8px"
                      ></a-button>
                  
                  
                      <a-button
                        @click="notAllotTags"
                      
                        type="primary"
                        style="margin-left: 8px"
                      >
                      <!-- <template #icon>
                        <icon>
                          <template #component>
                            <svg t="1772153650081" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4078" width="200" height="200"><path d="M268.2 366.2c78.2 0 141.8-63.6 141.8-141.8 0-78.2-63.6-141.8-141.8-141.8-78.2 0-141.8 63.6-141.8 141.8 0 78.1 63.6 141.8 141.8 141.8z m0-213.7c39.6 0 71.8 32.2 71.8 71.8s-32.2 71.8-71.8 71.8-71.8-32.2-71.8-71.8 32.2-71.8 71.8-71.8zM402.6 396.5H133.9c-25.9 0-47 21.1-47 47v349.9c0 25.9 21.1 47 47 47h104.6v69.4c0 19.3 15.7 35 35 35s35-15.7 35-35v-69.4h94c25.9 0 47-21.1 47-47V443.5c0.1-26-21-47-46.9-47z m-23 373.9H156.9V466.5h222.6v303.9zM708.3 475.4H583.2V204h117.7c19.3 0 35-15.7 35-35s-15.7-35-35-35h-171c-9.2 0-16.7 7.5-16.7 16.7v735.7c0 9.2 7.5 16.7 16.7 16.7h178.4c19.3 0 35-15.7 35-35s-15.7-35-35-35H583.2V545.4h125.1c19.3 0 35-15.7 35-35s-15.7-35-35-35z" p-id="4079"></path><path d="M844.9 169m-55.7 0a55.7 55.7 0 1 0 111.4 0 55.7 55.7 0 1 0-111.4 0Z" p-id="4080"></path><path d="M844.9 513.9m-55.7 0a55.7 55.7 0 1 0 111.4 0 55.7 55.7 0 1 0-111.4 0Z" p-id="4081"></path><path d="M844.9 858.8m-55.7 0a55.7 55.7 0 1 0 111.4 0 55.7 55.7 0 1 0-111.4 0Z" p-id="4082"></path></svg>
                          </template>
                        </icon>
                      </template> -->
                      <span
                        style="color:#fff;transform: translateY(2px);"
                            class=" anticon "
                         
                          >
                          <svg t="1772154248844" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4230" width="16px" height="16px"><path d="M268.2 366.2c78.2 0 141.8-63.6 141.8-141.8 0-78.2-63.6-141.8-141.8-141.8-78.2 0-141.8 63.6-141.8 141.8 0 78.1 63.6 141.8 141.8 141.8z m0-213.7c39.6 0 71.8 32.2 71.8 71.8s-32.2 71.8-71.8 71.8-71.8-32.2-71.8-71.8 32.2-71.8 71.8-71.8zM402.6 396.5H133.9c-25.9 0-47 21.1-47 47v349.9c0 25.9 21.1 47 47 47h104.6v69.4c0 19.3 15.7 35 35 35s35-15.7 35-35v-69.4h94c25.9 0 47-21.1 47-47V443.5c0.1-26-21-47-46.9-47z m-23 373.9H156.9V466.5h222.6v303.9zM708.3 475.4H583.2V204h117.7c19.3 0 35-15.7 35-35s-15.7-35-35-35h-171c-9.2 0-16.7 7.5-16.7 16.7v735.7c0 9.2 7.5 16.7 16.7 16.7h178.4c19.3 0 35-15.7 35-35s-15.7-35-35-35H583.2V545.4h125.1c19.3 0 35-15.7 35-35s-15.7-35-35-35z" p-id="4231" fill="#ffffff"></path><path d="M844.9 169m-55.7 0a55.7 55.7 0 1 0 111.4 0 55.7 55.7 0 1 0-111.4 0Z" p-id="4232" fill="#ffffff"></path><path d="M844.9 513.9m-55.7 0a55.7 55.7 0 1 0 111.4 0 55.7 55.7 0 1 0-111.4 0Z" p-id="4233" fill="#ffffff"></path><path d="M844.9 858.8m-55.7 0a55.7 55.7 0 1 0 111.4 0 55.7 55.7 0 1 0-111.4 0Z" p-id="4234" fill="#ffffff"></path></svg>

                      </span>
                      未分配标签
                      
                      </a-button>

                    <a-button
                      :disabled="loading"
                      type="primary"
                  
                        @click="allocationBtn"
                        style="margin: 0px 8px"
                      >
                      <img
                    src="/@/assets/images/allocation.png"
                    style="
                      transform: translate(-3px, -2px);
                      width: 12px;
                      height: 12px;
                      margin-right: 3px;
                    "
                  />
                      分配网红</a-button
                    >
              </a-form-item>
            </a-col>

          </a-row>

          <a-row :gutter="12"  v-if="
                      queryParam.uniqueId ||
                      queryParam.minFollowers ||
                      queryParam.maxFollowers ||
                      queryParam.minVideoStandardCount ||
                      queryParam.maxVideoStandardCount ||
                      queryParam.minMedianViews ||
                      queryParam.maxMedianViews ||
                      queryParam.minPlayAvgNum ||
                      queryParam.maxPlayAvgNum ||
                      queryParam.attributeIds
                    ">
            <!-- <a-col :xl="7" :lg="7" :md="8" :sm="24">
              <span class="table-page-search-submitButtons">
                
              </span>
            </a-col> -->
            <a-col :xl="24" :lg="24" :md="24" :sm="24"  style="margin-bottom: 6px">
                <span style="float: left">
                
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
                                    v-model:value="minFollowers"
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
                                    v-model:value="maxFollowers"
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
                          :icon="h(SafetyCertificateOutlined)"
                          @click="applyAllFilters(false)"
                          >应用</a-button
                        >
                        <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                          >查询</a-button
                        >
                      </div>
                    </div>

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
                        <span
                          v-else-if="
                            queryParam.minFollowers && !queryParam.maxFollowers
                          "
                        >
                          ≥ {{ queryParam.minFollowers }}
                        </span>
                        <span
                          v-else-if="
                            !queryParam.minFollowers && queryParam.maxFollowers
                          "
                        >
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
                          <a-icon class="rotating-arrow-filter-icon" type="down" />
                        </span>
                        <span>
                          <a-icon
                            @click.stop.prevent="clearFilterData($event, 'followers')"
                            @mousedown.stop.prevent
                            type="close-circle"
                            theme="filled"
                          />
                        </span>
                      </div>
                    </template>
                  </el-popover>
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
                                    v-model:value="minVideoCount"
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
                                    :max="20"
                                    :precision="0"
                                    v-model:value="maxVideoCount"
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
                          :icon="h(SafetyCertificateOutlined)"
                          @click="applyAllFilters(false)"
                          >应用</a-button
                        >
                        <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                          >查询</a-button
                        >
                      </div>
                    </div>

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
                            videoFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
                          "
                        >
                          <a-icon class="rotating-arrow-filter-icon" type="down" />
                        </span>
                        <span>
                          <a-icon
                            @click.stop.prevent="clearFilterData($event, 'video')"
                            @mousedown.stop.prevent
                            type="close-circle"
                            theme="filled"
                          />
                        </span>
                      </div>
                    </template>
                  </el-popover>
                  <!-- 视频中位数筛选展示 -->
                  <el-popover
                    ref="medianViewsFilterPopover"
                    popper-class="more-filter-popover"
                    placement="bottom-start"
                    trigger="click"
                    @show="onMedianViewsPopoverShow"
                    @hide="onMedianViewsPopoverHide"
                  >
                    <div style="height: 340px" class="more-filter-container">
                      <div class="more-filter-content">
                        <div class="more-filter-form">
                          <div class="filter-form-select">
                            <div
                              class="filter-form-select_item"
                              :class="{
                                selected: selectedMedianViewsRanges.includes('1K-10K'),
                              }"
                              @click="selectMedianViewsRange('1K-10K')"
                            >
                              1K-10K
                            </div>
                            <div
                              class="filter-form-select_item"
                              :class="{
                                selected: selectedMedianViewsRanges.includes('10K-20K'),
                              }"
                              @click="selectMedianViewsRange('10K-20K')"
                            >
                              10K-20K
                            </div>
                            <div
                              class="filter-form-select_item"
                              :class="{
                                selected: selectedMedianViewsRanges.includes('20K-50K'),
                              }"
                              @click="selectMedianViewsRange('20K-50K')"
                            >
                              20K-50K
                            </div>
                            <div
                              class="filter-form-select_item"
                              :class="{
                                selected: selectedMedianViewsRanges.includes('50K-100K'),
                              }"
                              @click="selectMedianViewsRange('50K-100K')"
                            >
                              50K-100K
                            </div>
                            <div
                              class="filter-form-select_item"
                              :class="{
                                selected: selectedMedianViewsRanges.includes('100K+'),
                              }"
                              @click="selectMedianViewsRange('100K+')"
                            >
                              100K+
                            </div>
                          </div>
                          <div class="range-input">
                            <div class="range-input__custom-label">自定义范围</div>
                            <div class="range-input__content">
                              <div class="range-input__flex">
                                <div class="range-input__label">最小视频中位数                              </div>
                                <div class="range-input__input" style="position: relative">
                                  <a-input-number
                                    style="width: 100%"
                                    :min="0"
                                    :precision="0"
                                    :max="99999"
                                    v-model:value="minMedianViews"
                                    placeholder="输入值"
                                    @change="onMedianViewsInputChange"
                                  ></a-input-number>
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
                                    >K</span
                                  >
                                </div>
                              </div>
                              <div class="range-input__sperate">-</div>
                              <div class="range-input__flex">
                                <div class="range-input__label">最大视频中位数</div>
                                <div class="range-input__input" style="position: relative">
                                  <a-input-number
                                    style="width: 100%"
                                    :min="0"
                                    :precision="0"
                                    :max="99999"
                                    v-model:value="maxMedianViews"
                                    placeholder="输入值"
                                    @change="onMedianViewsInputChange"
                                  ></a-input-number>
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
                          :icon="h(SafetyCertificateOutlined)"
                          @click="applyAllFilters(false)"
                          >应用</a-button
                        >
                        <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                          >查询</a-button
                        >
                      </div>
                    </div>

                    <template #reference>
                      <div
                        v-show="queryParam.minMedianViews || queryParam.maxMedianViews"
                        class="more-filter-button"
                        style="margin-right: 8px"
                      >
                        <span>视频中位数：</span>
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
                          <a-icon class="rotating-arrow-filter-icon" type="down" />
                        </span>
                        <span>
                          <a-icon
                            @click.stop.prevent="clearFilterData($event, 'medianViews')"
                            @mousedown.stop.prevent
                            type="close-circle"
                            theme="filled"
                          />
                        </span>
                      </div>
                    </template>
                  </el-popover>
                  <!-- 均播数筛选展示 -->
                  <el-popover
                    ref="playAvgFilterPopover"
                    popper-class="more-filter-popover"
                    placement="bottom-start"
                    trigger="click"
                    @show="onPlayAvgPopoverShow"
                    @hide="onPlayAvgPopoverHide"
                  >
                    <div style="height: 340px" class="more-filter-container">
                      <div class="more-filter-content">
                        <div class="more-filter-form">
                          <div class="filter-form-select">
                            <div
                              class="filter-form-select_item"
                              :class="{ selected: selectedPlayAvgRanges.includes('0K-10K') }"
                              @click="selectPlayAvgRange('0K-10K')"
                            >
                              0K-10K
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
                                    :precision="0"
                                    :max="99999"
                                    v-model:value="minPlayAvg"
                                    placeholder="输入值"
                                    @change="onPlayAvgInputChange"
                                  ></a-input-number>
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
                                    >K</span
                                  >
                                </div>
                              </div>
                              <div class="range-input__sperate">-</div>
                              <div class="range-input__flex">
                                <div class="range-input__label">最大均播数</div>
                                <div class="range-input__input" style="position: relative">
                                  <a-input-number
                                    style="width: 100%"
                                    :min="0"
                                    :precision="0"
                                    :max="99999"
                                    v-model:value="maxPlayAvg"
                                    placeholder="输入值"
                                    @change="onPlayAvgInputChange"
                                  ></a-input-number>
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
                          :icon="h(SafetyCertificateOutlined)"
                          @click="applyAllFilters(false)"
                          >应用</a-button
                        >
                        <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                          >查询</a-button
                        >
                      </div>
                    </div>

                    <template #reference>
                      <div
                        v-show="queryParam.minPlayAvgNum || queryParam.maxPlayAvgNum"
                        class="more-filter-button"
                        style="margin-right: 8px"
                      >
                        <span>均播数：</span>
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
                          <a-icon class="rotating-arrow-filter-icon" type="down" />
                        </span>
                        <span>
                          <a-icon
                            @click.stop.prevent="clearFilterData($event, 'playAvg')"
                            @mousedown.stop.prevent
                            type="close-circle"
                            theme="filled"
                          />
                        </span>
                      </div>
                    </template>
                  </el-popover>
                  <!-- 账号筛选展示 -->
                  <el-popover
                    ref="accountFilterPopoverRef"
                    popper-class="more-filter-popover"
                    placement="bottom-start"
                    trigger="click"
                    @show="onAccountPopoverShow"
                    @hide="onAccountPopoverHide"
                  >
                    <div style="height: 340px" class="more-filter-container">
                      <div class="more-filter-content">
                        <div class="more-filter-form">
                          <div class="range-input">
                            <div style="display: flex; align-items: center">
                              <a-textarea
                              
                                v-model:value="accountValue"
                                :auto-size="{ minRows: 12, maxRows: 12 }"
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
                    <template #reference>
                      <div
                        v-show="queryParam.uniqueId"
                        class="more-filter-button"
                        style="margin-right: 8px"
                      >
                        <span>账号：</span>
                        <span>{{ queryParam.uniqueId  }}</span>
                        <span
                          style="margin-left: 6px"
                          :class="
                            accountFilterPopoverVisible
                              ? 'main-filter-open'
                              : 'main-filter-closed'
                          "
                        >
                          <a-icon class="rotating-arrow-filter-icon" type="down" />
                        </span>
                        <span>
                          <a-icon
                            @click.stop.prevent="clearFilterData($event, 'uniqueId')"
                            @mousedown.stop.prevent
                            type="close-circle"
                            theme="filled"
                          />
                        </span>
                      </div>
                    </template>
                  </el-popover>
                  <template v-for="item in filterOptions" :key="item.typeId">
                    <el-popover
                      :ref="`attributeFilterPopover_${item.typeId}`"
                      popper-class="more-filter-popover"
                      v-model:visible="attributeFilterPopoverVisible[item.typeId]"
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
                            :icon="h(SafetyCertificateOutlined)"
                            @click="applyAllFilters(false)"
                            >应用</a-button
                          >
                          <a-button type="primary" :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
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
                          <span>{{ parseAttributeIds(queryParam.attributeIds, item.data) }}</span>
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
                              @click.stop.prevent="clearAttributeFilter($event, item.typeId)"
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
                    v-if="
                      queryParam.uniqueId ||
                      queryParam.minFollowers ||
                      queryParam.maxFollowers ||
                      queryParam.minVideoStandardCount ||
                      queryParam.maxVideoStandardCount ||
                      queryParam.minMedianViews ||
                      queryParam.maxMedianViews ||
                      queryParam.minPlayAvgNum ||
                      queryParam.maxPlayAvgNum ||
                      queryParam.attributeIds
                    "
                    class="clear-all-button"
                    @click="clearAllFilters"
                  >
                    <span>全部清除</span>
                  </div>
                </span>
            
            
            </a-col>
          </a-row>
        </a-form>
      </div>
      <!-- @cell-mouseenter="cellMouseenter"
      @cell-mouseleave="cellMouseleave" -->
      <div style="padding: 12px;background-color: #fff;">
      
          <s-table
          ref="tableRef"
          :loading="loading" :rangeSelection="false"	 size="small"  :scroll="{ y: sTableHeight }"    :row-height="100"
          :columns="columns" :data-source="dataSource" :pagination="false " :yBuff="10" :xBuff="5" :rowKey="record => record.id"   :animate-rows="false"
          @change="pageChange"
          >
          
            <template #headerCell="{ column }">
              <template v-if="column.dataIndex === 'account'">
                <span style="margin-left: 5px">网红信息</span>
                <span style="margin-left: 35px">包含置顶</span>
                <span style="margin-left: 5px">
                  <a-switch size="small" v-model:checked="isPinned"></a-switch>
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
            <template #bodyCell="{ column, record: row }">
              <template v-if="column.dataIndex === 'account'">
                <div class="colum_img">
                  <div class="colum_avatarUrl">
                    <a-popover  placement="top" overlayClassName="colum_avatarUrl_tooltip">
                      <template #content>
                        <img
                          v-if="row.avatarUrl"
                          :src="getImage(row.avatarUrl)"
                        @error="handleImageError"
                          style="max-width: 200px;height: 200px;"
                          :preview="row.id"
                        />
                        <img
                          v-else
                          src="@/assets/images/avatar.png"
                          style="width: 70px; height: 70px; margin: 0 auto"
                        />
                      </template>
                      <img
                        v-if="row.avatarUrl && !row.avatarUrl.includes('https')"
                        :src="getImage(row.avatarUrl)"
                      @error="handleImageError"
                        :class="{ 'error-img': row.isAbnormalAccount == 1 }"
                        style="
                          height: 70px;
                          border-radius: 100%;
                          width: 70px;
                          cursor: pointer;
                        "
                        :preview="row.id"
                      />
                      <img
                        v-else-if="row.avatarUrl && row.avatarUrl.includes('https')"
                        src="@/assets/images/avatar.png"
                        :class="{ 'error-img': row.isAbnormalAccount == 1 }"
                        style="
                          height: 70px;
                          border-radius: 100%;
                          width: 70px;
                          cursor: pointer;
                        "
                        :preview="row.id"
                      />
                      <img
                        v-else
                        :class="{ 'error-img': row.isAbnormalAccount == 1 }"
                        src="@/assets/images/avatar.png"
                        style="border-radius: 100%; width: 70px; height: 70px; margin: 0 auto"
                      />
                    </a-popover>
                    <img
                        v-if="row.platformType == 0"
                        class="platformType-img"
                        src="@/assets/images/ins.png"
                        alt=""
                      />
                      <img
                        v-if="row.platformType == 1"
                        class="platformType-img"
                        src="@/assets/images/yt.png"
                        alt=""
                      />
                      <img
                        v-if="row.platformType == 2"
                        class="platformType-img"
                        src="@/assets/images/tk.png"
                        alt=""
                      />
                    <!-- <div
                      style="
                        display: flex;
                        flex-direction: column;
                        justify-content: flex-end;
                        transform: translateX(-15px);
                      "
                    >
                    
                    </div> -->
                  </div>
                  <div class="colum_account">
                    <div style="width: 100%; white-space: nowrap">
                      <span                        style="font-size: 14px; font-weight: 600; width: 100%;margin-right:6px"
                        @click="openLink(row)"
                      >
                        <JEllipsis
                          v-if="row.platformType == 2"
                          :value="row.account"
                          :length="12"
                        ></JEllipsis>

                        <!-- <JEllipsis
                          v-else-if="row.platformType == 1"
                          :value="row.username"
                          :length="14"
                        ></JEllipsis> -->
                        <JEllipsis v-else :value="row.username" :length="12"></JEllipsis>
                        <!-- <JEllipsis :value="row.uniqueId" :length="14"></JEllipsis> -->
                      </span>
                      <img :src="`https://flagcdn.com/w40/${parseCountry(row.country).toLowerCase()}.png`" alt="" style="margin-right: 5px;width:20px;">
                      <span>
                        {{ row.country }}
                      </span>
                    </div>
                    <div style="display: flex; align-items: center; white-space: nowrap;margin:8px 0px">
                      <div style="margin-right: 10px; display: flex; height: 100%;background-color: #EDF1FF;color:#3155ED;padding-left:6px;align-items: center;width: 77px;height: 22px;border-radius: 6px;">
                        <a-tooltip title="粉丝数">
                          <span
                            class="icon iconfont icon-fensishu"
                            style="font-size: 18px; margin-right: 5px"
                          ></span>
                        </a-tooltip>
                        <span style=" display: flex; align-items: center">{{
                          row.followerCount !== null && row.followerCount !== ""
                            ? getFollower(row.followerCount)
                            : "--"
                        }}</span>
                      </div>
                      <div style="display: flex; height: 100%;background-color: #F4FFED;color:#05A300;justify-content: center;align-items: center;width: 70px;height: 22px;border-radius: 6px;">
                        <a-tooltip title="有效视频数(包含置顶)">
                          <span
                            class="icon iconfont icon-shipinbofang"
                            style="font-size: 18px; margin-right: 5px"
                          ></span>
                        </a-tooltip>
                        <span style="display: flex; align-items: center">
                          {{ row.videoStandardCount }}/{{ row.videoSampleCount }}
                        </span>
                      </div>
                    </div>
                    <div style="display: flex; align-items: center; white-space: nowrap">
                      <div style="display: flex">
                        <div style="display: flex; height: 100%;">
                        
                          <span style="color:#969696">互动率：</span>
                          <a-tooltip>
                            <template #title>
                              <div v-if="row.videoData && isEmptyObject(row.videoData) && row.videoData !== '{}' ">
                                <div>{{ isPinned ? "包含置顶" : "不包含置顶" }}</div>
                                <template v-if="row.platformType == 1">
                                  <template v-if="isPinned">
                                    <div>播放数：--</div>
                                    <div>均播数：--</div>
                                    <div>收藏数：--</div>
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
                                          jsonParseTotal(row.videoData).video_play_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      均播数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).play_avg_num
                                        )
                                      }}
                                    </div>
                                    <div>
                                      收藏数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_collect_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      评论数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_comment_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      视频数：{{
                                        getFollower(jsonParseTotal(row.videoData).video_count)
                                      }}
                                    </div>
                                    <div>
                                      点赞数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_digg_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      分享数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_share_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      播放区间：
                                      {{
                                        getFollower(
                                          jsonParseTotal(row.videoData).play_min_num
                                        )
                                      }}
                                      -
                                      {{
                                        getFollower(
                                          jsonParseTotal(row.videoData).play_max_num
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
                                          jsonParseTotal(row.videoData).video_play_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      均播数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).play_avg_num
                                        )
                                      }}
                                    </div>
                                    <div>
                                      收藏数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_collect_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      评论数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_comment_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      视频数：{{
                                        getFollower(jsonParseTotal(row.videoData).video_count)
                                      }}
                                    </div>
                                    <div>
                                      点赞数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_digg_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      分享数：{{
                                        getFollower(
                                          jsonParseTotal(row.videoData).video_share_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      播放区间：
                                      {{
                                        getFollower(
                                          jsonParseTotal(row.videoData).play_min_num
                                        )
                                      }}
                                      -
                                      {{
                                        getFollower(
                                          jsonParseTotal(row.videoData).play_max_num
                                        )
                                      }}
                                    </div>
                                  </template>
                                  <template v-else>
                                    <div>
                                      播放数：{{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData).video_play_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      均播数：{{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData).play_avg_num
                                        )
                                      }}
                                    </div>
                                    <div>
                                      收藏数：{{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData)
                                            .video_collect_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      评论数：{{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData)
                                            .video_comment_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      视频数：{{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData).video_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      点赞数：{{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData).video_digg_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      分享数：{{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData).video_share_count
                                        )
                                      }}
                                    </div>
                                    <div>
                                      播放区间：
                                      {{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData).play_min_num
                                        )
                                      }}
                                      -
                                      {{
                                        getFollower(
                                          jsonParseNonPinned(row.videoData).play_max_num
                                        )
                                      }}
                                    </div>
                                  </template>
                                </template>
                              </div>
                            </template>
                            <a
                              style="display: flex; align-items: center; text-decoration: underline;"
                              v-if="row.videoData && isEmptyObject(row.videoData) && row.videoData !== '{}' "
                            >
                              <template v-if="row.platformType == 1">
                                {{
                                  isPinned
                                    ? "--"
                                    : jsonParseTotal(
                                        row.videoData
                                      ).video_engagement_rate.toFixed(0) + "%"
                                }}
                              </template>
                              <template v-else>
                                {{
                                  isPinned
                                    ? jsonParseTotal(
                                        row.videoData
                                      ).video_engagement_rate.toFixed(0)
                                    : jsonParseNonPinned(
                                        row.videoData
                                      ).video_engagement_rate.toFixed(0)
                                }}%
                              </template>
                            </a>
                          </a-tooltip>
                          <span
                              style="display: flex; align-items: center;"
                              v-if="!row.videoData && !isEmptyObject(row.videoData) && row.videoData !== '{}' && row.videoData !== 'null' "
                          >
                            --
                          </span>
                        </div>
                        <!-- <a-tooltip title="视频中位数">
                          <span
                            class="icon iconfont icon-bofang"
                            style="font-size: 18px; margin-right: 5px"
                          ></span>
                        </a-tooltip> -->
                        <!-- <a-divider type="vertical" style="border-color:#969696"/> -->
                        <div style="width: 1px;height: 17px;background-color:#E8E8E8;margin: 0 10px;"></div>
                        <div style="display: flex; height: 100%;">
                          <span style="color:#969696">中位数：</span>
                          <span style="display: flex; align-items: center">
                            {{
                              row.medianViews !== null && row.medianViews !== ""
                                ? getFollower(row.medianViews)
                                : "--"
                            }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
              <template v-if="column.dataIndex === 'celebrityPrivateId'">
                <a-tag class="celebrityPrivate" v-if="row.celebrityPrivateId">私有</a-tag>
              </template>
              <template v-if="column.dataIndex === 'tagList'">
                <template v-if="row.tagList">
                  <div style="display: flex; flex-direction: column; row-gap: 4px">
                    <div
                      v-for="(item, index) in row.tagList.slice(0, 3)"
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
                          fontSize: '11px',
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
                                :data-source="row.tagList"
                                :pagination="false"
                                :scroll="{ y: '300px' }"
                              >
                                <template #tag="{ text }">
                                  <span @click="copyFn(text)">{{ text }}</span>
                                </template>
                                <template #tagType="{ text }">
                                  {{ tagTypeParse(text) }}
                                </template>
                              </a-table>
                            </div>
                        </template>
                        
                        <a-tag v-if="row.tagList.length > 3 && index == 2">...</a-tag>
                      </a-popover>
                      <a-tooltip title="复制全部标签">
                        <a-tag
                          style="margin-right: 0px"
                          v-if="row.tagList && index == row.tagList.slice(0, 3).length - 1"
                          @click.stop.prevent="copyTag(row.tagList)"
                        >
                          <CopyOutlined />
                        </a-tag>
                      </a-tooltip>
                    </div>
                  </div>
                
                </template>
                <span v-else> -- </span>
              </template>
              <template v-if="column.dataIndex === 'contactHistory'">
                <template v-if="row.contactHistory">
                  <div style="display: flex; flex-direction: column; gap: 4px">
                    <div
                      :style="{
                        display: 'flex',
                        justifyContent: index == 2 ? 'space-between' : '',
                      }"
                      v-for="(item, index) in developHistory(row.contactHistory).slice(0, 3)"
                      :key="index"
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
                              :data-source="developHistory(row.contactHistory)"
                              :pagination="false"
                            
                            >
                              <template #bodyCell="{ row: innerRecord, column: innerColumn }">
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

                        <div
                        v-if="developHistory(row.contactHistory).length >= 3"
                      >
                        <a>共合作{{ row.contactCount }}次</a>
                      </div>
                        </a-popover>
                    
                  
                  </div>
                </template>
                <div v-else class="contactHistory_noData">
                    <div class="circle"></div>
                    <div>暂无近期记录</div>
                  </div>
              </template>
              <template v-else-if="column.dataIndex === 'email'">
                <div >
                  <span
                    class="icon iconfont icon-mail"
                    style="font-size: 18px; margin-right: 5px"
                  ></span>
                    <!-- <EllipsisTooltip  :text="row.email?row.email:'--'"></EllipsisTooltip> -->
                   <span>{{ row.email?row.email:'--' }}</span>
                  <!-- <div style="height: 28px;line-height: 32px;">
                  
                  </div> -->
                  <!-- <template v-if="row.email">
                    <EllipsisTooltip :text="row.email"></EllipsisTooltip>
                  </template>
                  <div v-else>
                  --
                  </div> -->
                </div>
                <div :style="{
                  display: 'flex',
                  alignItems: (row.platformType != 2 && parseLinkTree(row.linktree).length > 1) ? '' : 'center'
                }">
                  <span
                    class="icon iconfont icon-lianjie"
                    style="font-size: 18px; margin-right: 5px"
                  ></span>
                  <template v-if="row.linktree">
                  <a
                    style="padding-right: 10px; display: flex;"
                    :href="
                      row.linktree.startsWith('http')
                        ? row.linktree
                        : 'https://' + row.linktree
                    "
                    target="_blank"
                    v-if="row.platformType == 2"
                  >
                    <EllipsisTooltip :text="row.linktree" :lines="1"></EllipsisTooltip>
                  </a>
                  <div v-else style="padding-right: 10px">
                    <div
                      style="
                        width: 100%;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                      "
                    >
                      <a
                        :href="
                          parseLinkTree(row.linktree)[0]
                            ? getFullUrl(parseLinkTree(row.linktree)[0].url)
                            : ''
                        "
                        target="_blank"
                        :title="
                          parseLinkTree(row.linktree)[0]
                            ? parseLinkTree(row.linktree)[0].url
                            : ''
                        "
                      >
                        {{
                          parseLinkTree(row.linktree)[0]
                            ? parseLinkTree(row.linktree)[0].url
                            : ""
                        }}
                      </a>
                    </div>
                    <a-popover>
                      <template #content>
                        <div style="max-height: 400px; overflow: auto; max-width: 500px">
                          <div style="font-size: 18px; color: #000">链接</div>
                          <div
                            style="margin-top: 10px"
                            v-for="(value, idx) in parseLinkTree(row.linktree)"
                            :key="idx"
                          >
                            <div style="color: #000">{{ value.title }}</div>
                            <a :href="getFullUrl(value.url)" target="_blank">{{
                              value.url
                            }}</a>
                          </div>
                        </div>
                      </template>

                      <div
                        style="display: inline-block; white-space: normal"
                        v-if="parseLinkTree(row.linktree).length > 1"
                      >
                        和另外{{ parseLinkTree(row.linktree).length - 1 }}个链接
                      </div>
                    </a-popover>
                  </div>
                </template>
                <template v-else>
                  <span>--</span>
                </template>
                </div>
                
              </template>
            
              <template v-else-if="categoryTypeList.some(cat => cat.typeCode === column.dataIndex)">
                <template v-if="row.dataList && row.dataList.length">
                  <template v-for="item in categoryTypeList" :key="item.id">
                    <template v-if="item.typeCode === column.dataIndex">
                      <div style="display: flex; flex-direction: column; row-gap: 4px">
                        <div
                          style="display: flex"
                          v-for="(categoryItem, index) in parseCategoryText(item.id, row)
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
                                  attrData(parseCategoryText(item.id, row), item.typeName)
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
                                parseCategoryText(item.id, row).split(',').length > 3
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
              <template v-else-if="column.dataIndex === 'description'">
                <EllipsisTooltip
                  v-if="row.description"
                  :text="row.description"
                  :lines="3"
                ></EllipsisTooltip>
                <span v-else>--</span>
              </template>
            </template>
          </s-table>

        <div style="display: flex; justify-content: space-between; align-items: center">
          <div
            style="
              margin-top: 10px;

              bottom: 10px;
              left: 16px;
              height: 32px;
              line-height: 32px;
            "
          >
            <template v-if="!countLoading && !loading">
              <template v-if="allCountObj.allottedKolCount">
                <span> 总网红数量:{{ allCountObj.allottedKolCount }}</span>
                <span v-if="allCountObj.allotCount" style="margin-left: 8px">
                  已分配网红数量:{{ allCountObj.allotCount }}</span
                >
                <span v-if="allCountObj.notAllotCount" style="margin-left: 8px">
                  未分配网红数量:{{ allCountObj.notAllotCount }}</span
                >
              </template>
            </template>
            <template v-else>
              <LoadingOutlined />
              <span style="margin-left: 8px">正在查询网红数量...</span>
            </template>
          </div>
          <div
            class="kol-pagination"
            style="
              margin-top: 10px;

              bottom: 10px;
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
            <span style="margin: 0 10px">第 {{ pagination.current }} 页</span>
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
      <allocationModalNew ref="allocationModalNewRef" @ok="modalFormOk" />
      <undistributedTagNumbersModel ref="undistributedTagNumbersModelRef" @ok="modalFormOk" />
    </div>
</template>

<script setup name="allotCelebritiesList">
import { ref, reactive, computed, nextTick, h } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { getDictItems } from '/@/api/common/api';
import { isEqual, cloneDeep,debounce } from 'lodash-es';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import JDictSelectPlatformType from '@/components/jeecg/JDictSelectPlatformType.vue';
import AllocationModalNew from './modules/allocationModalNew.vue';
import undistributedTagNumbersModel from './modules/undistributedTagNumbersModel.vue';
import JEllipsis from '/@/components/Form/src/jeecg/components/JEllipsis.vue';
import { ElCascaderPanel, ElPopover } from 'element-plus'
// import 'element-plus/es/components/cascader-panel/style/css'

import { useTable } from '/@/components/useSTable/useSTable';
import axios from 'axios';
import Icon,{
  SearchOutlined,
  ReloadOutlined,
  ApartmentOutlined,
  AlignLeftOutlined,
  CopyOutlined,
  LoadingOutlined,
  DoubleLeftOutlined,
  LeftOutlined,
  RightOutlined,
  SafetyCertificateOutlined,
  DownOutlined
} from '@ant-design/icons-vue';

const { createMessage } = useMessage();
const tableRef = ref(null);
// API 函数
const fetchTableApi = async (params) => {
  // 从 params 中提取 pageNo 和 pageSize
 
  if (params.productId && params.tagNameList && params.tagNameList.length > 0) {
    this.$message.warning("产品与标签不可同时查询，请重新选择");
    return;
  }
  if (params.attributeIds && params.tagNameList && params.tagNameList.length > 0) {
    this.$message.warning("标签与达人类型不可同时查询，请重新选择");
    return;
  }
  currentQueryParam.value = cloneDeep(params);
  const { pageNo, pageSize, ...data } = params;
  const res = await defHttp.post({
    url: '/kol/screening/list',
    data,
    params: {
      pageNo,
      pageSize,
    },
  },{isTransformResponse:false});
  if (res.success){
    resultCount.value = res.resultCount;
    return res.result;
  } else {
    return [];
  }
};

// 使用 useTable hook
const {
  newLayout,
  loading,
  dataSource,
  pagination,
  queryParam,
  fetchTable,
  awaitFetchParamFn,
  copyFn,
  sTableHeight,
  calcTableHeight,
  pageChange,
  updatePagination
} = useTable(fetchTableApi,0,{
  beforeFetch: (params) => {
    return allotCelebritiesListBeforeFetch(params)
  },
  afterFetch: (res) => {
    return allotCelebritiesListAfterFetch(res)
  }
});
newLayout.value = true
updatePagination({
  defaultPageSize: 50,           // 默认每页条数
  pageSize: 50,                  // 当前每页条数
  pageSizeOptions: ['20','30','50', '100', '200'],  // 每页条数选项
  current: 1,                    // 当前页码
  total: 0,                      // 总条数
  showQuickJumper: true,         // 是否显示快速跳转
  showSizeChanger: true,         // 是否显示每页条数选择器
  showTotal: (total, range) => { // 自定义显示总数
    return `${range[0]}-${range[1]} 共${total}条`
  }

})
const allotCelebritiesListBeforeFetch = (param) =>{
  const params = cloneDeep(param);
  params.isNonPinned = isPinned.value ? 0 : 1;
  // 获取社交媒体分类的选择值
  socialMediaCategoryList.value.forEach((item) => {
    if (item.selectedValues && item.selectedValues.length > 0) {
      params[`subCategoryIds`] = item.selectedValues.join(",");
    }
  });
  if (
    params.productId ||
    (params.productAttributeIds && params.productAttributeIds.length > 0)
  ) {
    params.attributeIds = params.productAttributeIds.join(",");
    delete params.productAttributeIds;
  }
  if (params.attributeIds && params.attributeIds.length == 0) {
    params.attributeIds = undefined;
  }
  
  if (!params.maxFollowers) delete params.maxFollowers;
  if (!params.minFollowers) delete params.minFollowers;
  if (!params.maxVideoStandardCount) delete params.maxVideoStandardCount;
  if (!params.minVideoStandardCount) delete params.minVideoStandardCount;
  if (params.tagNameList && params.tagNameList.length > 0) {
    params.tagType = tagType.value;
  } else {
    delete params.tagType;
  }
  if (params.tagNameList && params.tagNameList.length == 0) {
    delete params.tagType;
  }

  return params
}

const allotCelebritiesListAfterFetch = (params) =>{
  getAllCount(params)
  
}

// 基础状态
// const sTableRef = ref(null);
// const offsetHeight = ref(0);
const lastFetchId = ref(0);
const options = ref([]);
const tagNameList = ref([]);
const fetching = ref(false);
const selectOpen = ref(false);
const tagVisible = ref(false);
const tags = ref([]);
const filterType = ref('');
const attributeIdsMap = reactive({});
const filterTagTypeList = ref([]);
const tagValue = ref('');
const nonExistentTag = ref([]);
const categoryTypeList = ref([]);
const noTag = ref(false);
const countSource = ref(null);
const countLoading = ref(false);
const resultCount = ref(0);
const isPinned = ref(false);
const currentQueryParam = ref({});
const brandList = ref([]);
const productList = ref([]);
const countrys = ref([]);
const tagType = ref('0');
const searchType = ref('0');
const productCategoryList = ref([]);
const socialMediaCategoryList = ref([]);
const productAttributeList = ref([])
const tagTypeList = ref([]);
const tagGroupList = ref([]);
const platformTypeList = ref([]);
const allCountObj = ref({});
const allTags = ref([]);
const tagPageSize = ref(50);
const tagCurrentPage = ref(1);
const hasMoreTags = ref(true);
const tagLoading = ref(false);
const isApplyClose = ref(false);
const mainFilterPopover = ref(null);
const mainFilterPopoverVisible = ref(false);
const attributeFilterPopoverVisible = reactive({});
const _tagFilterPopoverVisible = ref(false);
const followersFilterPopover = ref(null);
const followersFilterPopoverVisible = ref(false);
const videoFilterPopover = ref(null);
const videoFilterPopoverVisible = ref(false);
const medianViewsFilterPopover = ref(null);
const medianViewsFilterPopoverVisible = ref(false);
const playAvgFilterPopover = ref(null);
const playAvgFilterPopoverVisible = ref(false);
const minFollowers = ref(null);
const maxFollowers = ref(null);
const selectedFollowersRanges = ref([]);
const minVideoCount = ref(null);
const maxVideoCount = ref(null);
const selectedVideoRanges = ref([]);
// 视频中位数范围相关变量
const minMedianViews = ref(null);
const maxMedianViews = ref(null);
const selectedMedianViewsRanges = ref([]);
// 均播数范围相关变量
const minPlayAvg = ref(null);
const maxPlayAvg = ref(null);
const selectedPlayAvgRanges = ref([]);
const accountValue = ref('');
const accountFilterPopoverVisible = ref(false);
const accountFilterPopoverRef = ref(null);
const allocationModalNewRef = ref(null);
const pageSizeOptions = ref([
  { label: '50条/页', value: 50 },
  { label: '100条/页', value: 100 },
  { label: '200条/页', value: 200 },
]);
const undistributedTagNumbersModelRef = ref(null);
const filterData = reactive({
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
  accountData: {
    accountValue: '',
  },
  tagData: {
    tagType: undefined,
    tagNameList: [],
  },
});

const backupData = reactive({
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
  accountValue: '',
  tagNameList: [],
});

const attrColumns = ref([
  {
    title: '#',
    dataIndex: '',
    width: '50px',
    key: 'index',
    align: 'center',
    customRender: function ({index}) {
      return parseInt(index) + 1;
    },
  },
  {
    slots: { title: 'categoryTitle' },
    dataIndex: 'category',
    width: 120,
    key: 'category',
  },
]);

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

const historyColumns = ref([
  {
    title: '#',
    dataIndex: '',
    key: 'historyRowIndex',
    width: 60,
    align: 'center',
    customRender: function ({index}) {
      return parseInt(index) + 1;
    },
  },
  {
    title: '产品',
    dataIndex: 'productName',
    width: '100px',
    scopedSlots: {
      customRender: 'productName',
    },
  },
  {
    title: '品牌',
    dataIndex: 'brandName',
    width: '100px',
    scopedSlots: {
      customRender: 'brandName',
    },
  },
  {
    title: '开发次数',
    dataIndex: 'num',
    width: '100px',
    scopedSlots: {
      customRender: 'num',
    },
  },
]);

// 计算属性
const attributeIds = computed({
  get() {
    return attributeIdsMap[filterType.value] || [];
  },
  set(value) {
    attributeIdsMap[filterType.value] = value;
  },
});

const _hasActiveFilters = computed(() => {
  if (queryParam.value.tagNameList && queryParam.value.tagNameList.length > 0) {
    return true;
  }
  if (queryParam.value.minFollowers || queryParam.value.maxFollowers) {
    return true;
  }
  if (
    queryParam.value.minVideoStandardCount ||
    queryParam.value.maxVideoStandardCount
  ) {
    return true;
  }
  if (queryParam.value.attributeIds && queryParam.value.attributeIds) {
    return true;
  }
  if (queryParam.value.uniqueId) {
    return true;
  }
  return false;
});

// 表格列定义
const columns = computed(() => {
  const baseColumns = [
    {
      title: '#',
      dataIndex: '',
      key: 'index',
      width: 60,
      // fixed: 'left',
      align: 'center',
      customRender: function ({index}) {
        return parseInt(index) + 1;
      },
    },
    {
      title: '网红信息',
      dataIndex: 'account',
      width: 280,
      // fixed: 'left',
    },
    {
      title: "私有网红",
      dataIndex: "celebrityPrivateId",
      key: "celebrityPrivateId",
      width: 80,
      align: "center",
    },
    {
      title: '标签',
      dataIndex: 'tagList',
      width: 280,
    },
    {
      title: '近半年开发历史',
      dataIndex: 'contactHistory',
      width: 280,
    },
    {
      title: '邮箱/外链',
      dataIndex: 'email',
      width: 350,
    },
    // {
    //   title: '外链',
    //   dataIndex: 'linktree',
    //   width: 250,
    // },
    // 不要在baseColumns里放简介(description)，我们要放最外面后面
    // {
    //   title: '简介',
    //   dataIndex: 'description',
    //   width: 300,
    // },
  ];

  // 动态添加分类类型列
  const categoryColumns = categoryTypeList.value.map((item) => ({
    title: item.typeName,
    dataIndex: item.typeCode,
    width: 180,
  }));

  // 简介(description)列
  const descriptionColumn = {
    title: '简介',
    dataIndex: 'description',
    width: 300,
  };

  // 顺序：[...baseColumns, ...categoryColumns, descriptionColumn]
  return [...baseColumns, ...categoryColumns, descriptionColumn];
});
function modalFormOk(){
 
  fetchTable();
}
// 方法定义
function tagPopover() {
  tagVisible.value = !tagVisible.value;
}

function copyTag(tagList) {
  const headers = ['标签', '类型'];
  const headerRow = headers.join('\t');
  const dataRows = tagList.map(
    (item) => `${item.tagName}\t${tagTypeParse(item.tagType)}`
  );
  const tsv = [headerRow, ...dataRows].join('\n');

  // ✅ 检查 clipboard 是否可用
  if (navigator.clipboard && typeof navigator.clipboard.writeText === 'function') {
    navigator.clipboard
      .writeText(tsv)
      .then(() => {
        createMessage.success('复制成功！');
      })
      .catch((err) => {
        console.error('Clipboard write failed:', err);
        createMessage.error('复制失败，请手动复制。');
      });
  } else {
    // 💡 降级方案：使用 document.execCommand（旧方法）
    fallbackCopyText(tsv);
  }
}

// 降级复制方法（适用于不支持 Clipboard API 的环境）
function fallbackCopyText(text) {
  const textarea = document.createElement('textarea');
  textarea.value = text;
  textarea.style.position = 'fixed'; // 避免滚动
  textarea.style.opacity = '0';
  textarea.style.left = '-9999px';
  document.body.appendChild(textarea);
  textarea.select();
  try {
    const successful = document.execCommand('copy');
    if (successful) {
      createMessage.success('复制成功');
    } else {
      createMessage.error('复制失败，请手动复制。');
    }
  } catch (err) {
    createMessage.error('复制失败，请手动复制。');
  }
  document.body.removeChild(textarea);
}

function searchQuery() {

  fetchTable(1);
}


function attrData(text) {
  return text.split(',').map((item) => ({
    category: item,
  }));
}

function pageSizeChange(value) {
  pagination.value.pageSize = value;
  tableRef.value.scrollTo({top:0},'smooth');
  fetchTable();
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
function initBrandList() {
  defHttp.get({ url: '/kolBrand/listAll' }).then((res) => {
    brandList.value = res;
   
  });
}

function onBrandChange(value) {
  queryParam.value.productId = undefined;
  productList.value = [];
  productAttributeList.value = [];
  if (value) {
    initProduct();
  } else {
    onProductChange(undefined);
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
  console.log('分类变更:', value);
  // 分类变更时可以在这里添加属性筛选逻辑
  // 目前保持简化实现
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

function onSearchTypeChange(_value) {
  queryParam.value.productId = undefined;
  queryParam.value.categoryIds = undefined;
  queryParam.value.brandId = undefined;
  productList.value = [];
  onCategoryChange(undefined);
  onProductChange(undefined);
}

function getFullUrl(url) {
  return url.startsWith('http') ? url : 'https://' + url;
}

function parseLinkTree(text) {
  if (text) {
    return JSON.parse(text);
  } else {
    return [];
  }
}
function tagPopupScroll(e) {
  const { target } = e;
  const { scrollTop, scrollHeight, clientHeight } = target;

  // 计算滚动位置
  const scrollBottom = scrollTop + clientHeight;
  const isNearBottom = scrollHeight - scrollBottom < 50; // 距离底部50px时触发加载

  if (isNearBottom && !tagLoading.value && hasMoreTags.value) {
    debouncedLoadMoreTags();
  }
}

// 加载更多标签数据
async function loadMoreTags() {
  if (tagLoading.value || !hasMoreTags.value) return;

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
    console.error('加载更多标签失败:', error);
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
  if (!allTags.value || allTags.value.length === 0) {
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

// 优化虚拟滚动性能的防抖方法
const debouncedLoadMoreTags = debounce(function () {
  loadMoreTags();
}, 200);

// 清理标签数据，释放内存
function clearTagData() {
  allTags.value = [];
  tags.value = [];
  resetTagPagination();
}

function developHistory(text) {
  if (text) {
    return JSON.parse(text);
  } else {
    return [];
  }
}

function allocationBtn() {
  if (
    queryParam.value.tagNameList &&
    queryParam.value.tagNameList.length > 0 &&
    queryParam.value.attributeIds
  ) {
    createMessage.warning('标签与达人类型不可同时分配，请重新选择');
    return;
  }
  // 检查是否有有效的查询条件
  const hasValidQuery =
    (Array.isArray(queryParam.value.tagNameList) &&
      queryParam.value.tagNameList.length > 0) ||
    queryParam.value.attributeIds ||
    queryParam.value.productId;

  if (!hasValidQuery) {
    createMessage.warning('产品、标签或社媒属性，至少选择一种');
    return;
  }
  let processParam = allotCelebritiesListBeforeFetch(awaitFetchParamFn({}))
  // 检查查询条件是否与当前结果一致
  if (!isEqual(currentQueryParam.value, processParam)) {
    createMessage.warning('当前查询条件与列表结果不一致，请重新查询后再进行网红分配');
    return;
  }

  // 深拷贝queryParam以确保数据安全
  const params = {};
  Object.keys(queryParam.value).forEach((key) => {
    const value = queryParam.value[key];
    if (Array.isArray(value)) {
      params[key] = [...value];
    } else if (value && typeof value === 'object') {
      params[key] = JSON.parse(JSON.stringify(value));
    } else {
      params[key] = value;
    }
  });
  if (params.productId) {
    params.productName =
      brandList.value.find((item) => item.id === params.brandId).brandName +
      '-' +
      productList.value.find((item) => item.id === params.productId).productName;
  }
  console.log(params, socialMediaCategoryList.value);
  params.tagTypeName = tagTypeParse(tagType.value);
  if (params.tagNameList && params.tagNameList.length > 0) {
    params.tagType = tagType.value;
  }
  // 打开分配模态框
  allocationModalNewRef.value?.openModalTable(allCountObj.value.notAllotCount, params);
}

function tagColor(type) {
  const found = tagTypeList.value.filter((item) => item.value == type)[0];
  return found ? found.description : '';
}
// 通用清除筛选数据方法
function clearFilterData(event, filterType) {
  console.log(`清除${filterType}数据`);
  if (event) {
    event.stopPropagation();
    event.preventDefault();
    event.stopImmediatePropagation();
  }

  // 无论如何都要关闭所有弹窗
  closeAllPopovers();

  nextTick(() => {
    switch (filterType) {
      case 'followers':
        clearFollowersData();
        break;
      case 'video':
        clearVideoData();
        break;
      case 'medianViews':
        clearMedianViewsData();
        break;
      case 'playAvg':
        clearPlayAvgData();
        break;
    case 'uniqueId':
      clearUniqueIdData();
      break;
      case 'tag':
        clearTagData();
        break;
      case 'attribute':
        clearAttributeData(
          event && event.attributeType ? event.attributeType : null
        );
        break;
      default:
        console.warn(`未知的筛选类型: ${filterType}`);
        return;
    }
    nextTick(() => {
      console.log('清除筛选数据');
      calcTableHeight();
    });
    // 手动触发查询以更新界面
    fetchTable(1);
  });
}

// 清除粉丝数据
function clearFollowersData() {
  // 清除queryParam中的粉丝数据
  delete queryParam.value.minFollowers;
  delete queryParam.value.maxFollowers;

  // 清除界面显示变量
  minFollowers.value = null;
  maxFollowers.value = null;
  selectedFollowersRanges.value = [];

  // 清除filterData中的粉丝范围数据
  filterData.followersRange = {
    minFollowers: null,
    maxFollowers: null,
    selectedRanges: [],
  };

  // 清除备份数据中的粉丝相关数据
  if (backupData && backupData.filterData) {
    backupData.minFollowers = null;
    backupData.maxFollowers = null;
    backupData.selectedFollowersRanges = [];
    backupData.filterData.followersRange = {
      minFollowers: null,
      maxFollowers: null,
      selectedRanges: [],
    };
  }

  console.log('已清除所有粉丝数据');
}

// 清除有效视频数据
function clearVideoData() {
  // 清除queryParam中的视频数据
  delete queryParam.value.minVideoStandardCount;
  delete queryParam.value.maxVideoStandardCount;

  // 清除界面显示变量
  minVideoCount.value = null;
  maxVideoCount.value = null;
  selectedVideoRanges.value = [];

  // 清除filterData中的视频范围数据
  filterData.effectiveVideoRange = {
    minVideoCount: null,
    maxVideoCount: null,
    selectedRanges: [],
  };

  // 清除备份数据中的视频相关数据
  if (backupData && backupData.filterData) {
    backupData.minVideoCount = null;
    backupData.maxVideoCount = null;
    backupData.selectedVideoRanges = [];
    backupData.filterData.effectiveVideoRange = {
      minVideoCount: null,
      maxVideoCount: null,
      selectedRanges: [],
    };
  }

  console.log('已清除所有视频数据');
}

// 清除视频中位数数据
function clearMedianViewsData() {
  delete queryParam.value.minMedianViews;
  delete queryParam.value.maxMedianViews;

  minMedianViews.value = null;
  maxMedianViews.value = null;
  selectedMedianViewsRanges.value = [];

  filterData.medianViewsRange = {
    minMedianViews: null,
    maxMedianViews: null,
    selectedRanges: [],
  };

  if (backupData && backupData.filterData) {
    backupData.minMedianViews = null;
    backupData.maxMedianViews = null;
    backupData.selectedMedianViewsRanges = [];
    backupData.filterData.medianViewsRange = {
      minMedianViews: null,
      maxMedianViews: null,
      selectedRanges: [],
    };
  }

  console.log('已清除所有视频中位数数据');
}

// 清除均播数数据
function clearPlayAvgData() {
  delete queryParam.value.maxPlayAvgNum;
  delete queryParam.value.minPlayAvgNum;

  minPlayAvg.value = null;
  maxPlayAvg.value = null;
  selectedPlayAvgRanges.value = [];

  filterData.playAvgRange = {
    minPlayAvg: null,
    maxPlayAvg: null,
    selectedRanges: [],
  };

  if (backupData && backupData.filterData) {
    backupData.minPlayAvg = null;
    backupData.maxPlayAvg = null;
    backupData.selectedPlayAvgRanges = [];
    backupData.filterData.playAvgRange = {
      minPlayAvg: null,
      maxPlayAvg: null,
      selectedRanges: [],
    };
  }

  console.log('已清除所有均播数数据');
}

// 清除账号数据
function clearUniqueIdData() {
  delete queryParam.value.uniqueId;
  accountValue.value = undefined;
  filterData.accountData = {
    accountValue: '',
  };
  if (backupData) {
    backupData.accountValue = undefined;
  }
}

// 清除属性数据
function clearAttributeData(attributeType) {
  if (attributeType) {
    // 清除特定属性类型的数据
    attributeIdsMap[attributeType] = [];

    // 重新计算attributeIds
    let allAttributeIds = [];
    Object.keys(attributeIdsMap).forEach((typeId) => {
      if (
        attributeIdsMap[typeId] &&
        Array.isArray(attributeIdsMap[typeId])
      ) {
        const flatIds = Array.isArray(attributeIdsMap[typeId])
          ? attributeIdsMap[typeId].flat()
          : [];
        allAttributeIds = allAttributeIds.concat(flatIds);
      }
    });

    const uniqueAttributeIds = [...new Set(allAttributeIds)];
    if (uniqueAttributeIds.length > 0) {
      queryParam.value.attributeIds = uniqueAttributeIds.join(',');
    } else {
      delete queryParam.value.attributeIds;
    }

    console.log(`已清除属性类型 ${attributeType} 的数据`);
  } else {
    // 清除所有属性数据
    Object.keys(attributeIdsMap).forEach((key) => {
      delete attributeIdsMap[key];
    });
    delete queryParam.value.attributeIds;
    console.log('已清除所有属性数据');
  }
}

// 清除特定属性筛选
function clearAttributeFilter(event, attributeType) {
  console.log(`清除属性筛选: ${attributeType}`);
  if (event) {
    event.stopPropagation();
    event.preventDefault();
    event.stopImmediatePropagation();
  }
  closeAllPopovers();
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
  if (uniqueAttributeIds.length > 0) {
    queryParam.value.attributeIds = uniqueAttributeIds.join(',');
  } else {
    delete queryParam.value.attributeIds;
  }

  // 清除备份数据中对应的属性数据
  if (backupData && backupData.attributeIdsMap) {
    backupData.attributeIdsMap[attributeType] = [];
  }

  console.log(`已清除属性类型 ${attributeType} 的数据`);

  // 手动触发查询以更新界面
  fetchTable(1);
}

// 清除所有筛选条件
function clearAllFilters() {
  console.log('清除所有筛选条件');

  // 清除标签筛选
  delete queryParam.value.tagNameList;
  tagNameList.value = [];
  tagValue.value = '';

  // 清除粉丝数筛选
  delete queryParam.value.minFollowers;
  delete queryParam.value.maxFollowers;
  minFollowers.value = null;
  maxFollowers.value = null;
  selectedFollowersRanges.value = [];

  // 清除有效视频筛选
  delete queryParam.value.minVideoStandardCount;
  delete queryParam.value.maxVideoStandardCount;
  minVideoCount.value = null;
  maxVideoCount.value = null;
  selectedVideoRanges.value = [];

  // 清除视频中位数筛选
  delete queryParam.value.minMedianViews;
  delete queryParam.value.maxMedianViews;
  minMedianViews.value = null;
  maxMedianViews.value = null;
  selectedMedianViewsRanges.value = [];

  // 清除均播数筛选
  delete queryParam.value.minPlayAvgNum;
  delete queryParam.value.maxPlayAvgNum;
  minPlayAvg.value = null;
  maxPlayAvg.value = null;
  selectedPlayAvgRanges.value = [];

  // 清除属性筛选
  delete queryParam.value.attributeIds;
  Object.keys(attributeIdsMap).forEach((key) => {
    delete attributeIdsMap[key];
  });

  // 重置筛选数据
  filterData.attributeSelections = {};
  filterData.followersRange = {
    minFollowers: null,
    maxFollowers: null,
    selectedRanges: [],
  };
  filterData.effectiveVideoRange = {
    minVideoCount: null,
    maxVideoCount: null,
    selectedRanges: [],
  };
  filterData.medianViewsRange = {
    minMedianViews: null,
    maxMedianViews: null,
    selectedRanges: [],
  };
  filterData.playAvgRange = {
    minPlayAvg: null,
    maxPlayAvg: null,
    selectedRanges: [],
  };
  delete queryParam.value.uniqueId;
  accountValue.value = '';
  filterData.accountData = {
    accountValue: '',
  };
  const tableWrapper = document.querySelector('.surely-table-body');
  if (tableWrapper) {
    tableWrapper.scrollTo({
      top: 0,
      behavior: 'smooth', // 平滑滚动
    });
  }
  // 关闭所有弹窗
  closeAllPopovers();

  // 触发查询以更新界面
  fetchTable(1);
  nextTick(() => {
    calcTableHeight();
  });
  console.log('已清除所有筛选条件');
}

function closeTagEdit() {
  tagVisible.value = false;
  clear();
  noTag.value = false;
}

function clear() {
  tagValue.value = '';
  noTag.value = false;
}

function tagSerch(value) {
  if (!value) {
    return;
  }
  let allTag = value.trim().split('\n');
  const arr = [];
  for (let index = 0; index < value.trim().split('\n').length; index++) {
    const el = allTag[index];
    arr.push(el.trim());
  }
  allTag = [...arr];
  let filterTags = [];
  console.log(allTag);
  if (allTag.length> 0){
    // return;

    defHttp.post({
      url: '/kol/tags/queryTagsListByName',
      data: {
        tagNames: allTag.join('|'),
        platformType: queryParam.value.platformType,
        tagType: tagType.value,
      },
    },{isTransformResponse: false}).then((res) => {
      if (res.success) {
  
        console.log(res);
        filterTags = res.result;
        nonExistentTag.value = allTag.filter((item) => {
          const tagName = [];
          console.log(filterTags);
          filterTags.forEach((tag) => {
            tagName.push(tag.tagName.toLowerCase());
          });
          console.log(tagName);
          return !tagName.includes(item.toLowerCase());
        });
        console.log(nonExistentTag.value);
        if (nonExistentTag.value.length > 0) {
          noTag.value = true;
        } else {
          queryParam.value.tagNameList = allTag;
          tagVisible.value = false;
          noTag.value = false;
        }
      }
    });
  }
}
function _deselect(value) {
  console.log(value);
  setTimeout(() => {
    selectOpen.value = true;
  }, 0);
}

function handleChange(value) {
  fetching.value = false;
  if (value.length > 0) {
    // selectOpen.value = true;
  } else {
    tags.value = [];
  }
}

const fetchUser = debounce(function (value) {
  console.log('fetching user', value);
  if (value) {
    lastFetchId.value += 1;
    const fetchId = lastFetchId.value;

    // 重置虚拟滚动状态
    resetTagPagination();

    fetching.value = true;

    defHttp.post({
      url: '/kol/tags/queryTagsListByName',
      data: {
        tagName: value,
        platformType: queryParam.value.platformType,
        tagType: tagType.value,
      },
    }).then((res) => {
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

      fetching.value = false;
    });
  } else {
    tags.value = [];
  }
}, 800);

function notAllotTags() {
  undistributedTagNumbersModelRef.value.add(
    queryParam.value.platformType,
    queryParam.value.countryCode,
    queryParam.value
  );
}

function _getPopupContainer(triggerNode) {
  return triggerNode.parentNode;
}

function _filterTreeNode(inputValue, treeNode) {
  return (
    treeNode.data.props.title.toLowerCase().indexOf(inputValue.toLowerCase()) === 0
  );
}

function onPlatformTypeChange(value) {
  console.log('平台变更:', value);
  queryParam.value.platformType = value != null ? value.toString() : undefined;

  onTagTypeChange(value);

  filterTagTypeList.value = getTagTypeListByPlatformType(value);
  tagType.value = filterTagTypeList.value[0].value;
  queryParam.value.tagNameList = [];
  tags.value = [];
  onProductChange(queryParam.value.productId)
  // initTagTypeAndGroup()
}

function onTagTypeChange(_value) {
  tags.value = [];
  queryParam.value.tagNameList = [];
  clear();
  // clearTagData();
}
function tagTypeParse(type) {
  console.log(type)
  const found = tagTypeList.value.filter((item) => item.value == type)[0];
  return found ? found.title : '';
}


function openLink(record) {
  if (record.platformType == '1') {
    window.open(`https://www.youtube.com/channel/${record.account}`);
  } else if (record.platformType == '2') {
    window.open(`https://www.tiktok.com/@${record.account}`);
  } else if (record.platformType == '0') {
    window.open(`https://www.instagram.com/${record.username}`);
  }
}

function isShowAttribute(data) {
  if (queryParam.value.attributeIds) {
    const arr = queryParam.value.attributeIds.split(',');
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

function _parseData(data) {
  return data.map((item) => {
    return {
      id: item.id,
      attributeName: item.attributeName,
    };
  });
}

async function initProductCategory() {
  const res = await defHttp.get({ url: '/kol/category/loadTreeDataAll' });
  productCategoryList.value = res;
  
}

async function initProduct() {
  const res = await defHttp.get({
    url: '/kol/kolProduct/listAll',
    params: {
      brandId: queryParam.value.brandId,
    },
  });
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


function initCountry() {
  defHttp.get({ url: '/tiktokcountry/getCountryList' }).then((res) => {
    console.log(res);
    countrys.value = res;
  });
}


// 兼容旧版 vxe 页面的高度计算占位方法
// 当前页面使用 s-table + useTable 管理高度，这里仅保留空实现避免运行时报错
function _getOffSetNum() {}

const isCanceling = ref(false);

function getAllCount(params) {
  // 如果存在正在进行的请求，则取消
  if (countSource.value) {
    isCanceling.value = true; // ✅ 标记正在取消
    countSource.value.cancel('');
  }

  // 创建新的 CancelToken 源
  countSource.value = axios.CancelToken.source();
  countLoading.value = true;
  allCountObj.value = {};


  if (params.tagNameList && params.tagNameList.length > 0) {
    params.tagType = tagType.value;
  }
  defHttp.post(
    {
      url: '/kol/screening/getScreeningListCount',
      data: params,
    },
    
  )
    
    .then((res) => {
      console.log(res);
      if (res) {
        allCountObj.value = res;
        console.log(allCountObj.value);
        allocationModalNewRef.value?.updateCount(res);
      } else {
        createMessage.error(res.message);
      }
    })
    .catch((error) => {
      if (axios.isCancel(error)) {
        console.log('%c请求已取消', 'color: orange;', error.message);
      } else {
        console.error(error);
        // createMessage.error("请求失败");
      }
    })
    .finally(() => {
      // ✅ 如果是取消导致的，不要关闭 loading（让新请求接管）
      if (isCanceling.value) {
        isCanceling.value = false;
        return;
      }

      countLoading.value = false;
      countSource.value = null;
    });
}

function selectFilterOther(type) {
  // 保存当前筛选条件的数据
  nextTick(() => {
    if (type == 'tag' && !tagType.value) {
      onTagTypeChange(queryParam.value.platformType);
    }
  });
  saveCurrentFilterData();

  // 切换到新的筛选条件
  filterType.value = type;

  // 恢复该筛选条件的已保存数据
  restoreFilterData(type);
}

function selectFilter(item) {
  console.log(item);
  // 保存当前筛选条件的数据
  saveCurrentFilterData();

  // 切换到新的筛选条件
  filterType.value = item.typeId;
  options.value = [...item.data].map((item) => {
    return {
      id: item.id,
      attributeName: item.attributeName,
      disabled:
           queryParam.value.brandId ||
           queryParam.value.productId ||
            (this.queryParam.productAttributeIds &&
             queryParam.value.productAttributeIds.length > 0) ||
            (this.queryParam.tagNameList &&queryParam.value.tagNameList.length > 0),

    };
  });

  // 恢复该筛选条件的已保存数据
  restoreFilterData(item.typeId);
}

const filterOptions = ref([]);

async function initAttributeList() {
  const res = await defHttp.get({ url: '/kol/attribute/loadTreeDataAll' });
  if (res) {
    console.log(res.result);

    filterOptions.value = res.map((item) => {
      return {
        ...item,
        data: item.data.map((dataItem) => {
          return {
            ...dataItem,
            children: null,
          };
        }),
      };
    });

    // 根据 socialMediaCategoryList 中的 typeName 对 filterOptions 进行排序
    filterOptions.value.sort((a, b) => {
      const aIndex = socialMediaCategoryList.value.findIndex(
        (item) => item.typeId === a.typeId
      );
      const bIndex = socialMediaCategoryList.value.findIndex(
        (item) => item.typeId === b.typeId
      );

      // 如果都找到了对应的索引，按照socialMediaCategoryList的顺序排序
      if (aIndex !== -1 && bIndex !== -1) {
        return aIndex - bIndex;
      }
      // 如果只有a找到了，a排在前面
      if (aIndex !== -1 && bIndex === -1) {
        return -1;
      }
      // 如果只有b找到了，b排在前面
      if (aIndex === -1 && bIndex !== -1) {
        return 1;
      }
      // 如果都没找到，保持原顺序
      return 0;
    });
  }
}

function moreFilter() {}

function handleImageError(e) {
  e.target.src = new URL("/@/assets/images/avatar.png", import.meta.url).href;
}

// 开发历史
function getImage(url) {
    return url.includes("https") || url.includes("http")
      ? url
      : 'https://gemstone-image.kolbox.com/avatar/' + url;
  }

function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    const kValue = (num / 1000).toFixed(0);
    return parseFloat(kValue) % 1 === 0
      ? `${parseInt(kValue)}K`
      : `${parseFloat(kValue)}K`;
  } else if (num >= 1000000) {
    const mValue = (num / 1000000).toFixed(0);
    return parseFloat(mValue) % 1 === 0
      ? `${parseInt(mValue)}M`
      : `${parseFloat(mValue)}M`;
  } else if (num > 0 && num < 1000) {
    return num;
  } else {
    return '--';
  }
}

function searchReset() {
  Object.keys(queryParam.value).forEach((key) => {
    delete queryParam.value[key];
  });
  productList.value = [];
  for (let i = 0; i < socialMediaCategoryList.value.length; i++) {
    socialMediaCategoryList.value[i].selectedValues = [];
  }

  // 重置更多筛选中的数据
  // 重置属性选择
  Object.keys(attributeIdsMap).forEach((key) => {
    delete attributeIdsMap[key];
  });

  // 重置粉丝数范围
  minFollowers.value = null;
  maxFollowers.value = null;
  selectedFollowersRanges.value = [];
  filterData.followersRange = {
    minFollowers: null,
    maxFollowers: null,
    selectedRanges: [],
  };

  // 重置有效视频范围
  minVideoCount.value = null;
  maxVideoCount.value = null;
  selectedVideoRanges.value = [];
  filterData.effectiveVideoRange = {
    minVideoCount: null,
    maxVideoCount: null,
    selectedRanges: [],
  };

  // 重置标签数据
  tagType.value = '0';
  queryParam.value.tagNameList = [];

  // 重置筛选类型
  filterType.value = '';

  // 重置所有属性筛选数据
  filterData.attributeSelections = {};
  filterData.accountData = {
    accountValue: '',
  };
  accountValue.value = undefined;
  selectedPlayAvgRanges.value = [];
  minPlayAvg.value = null;
  maxPlayAvg.value = null;
  filterData.playAvgRange = {
    minPlayAvg: null,
    maxPlayAvg: null,
    selectedRanges: [],
  };
  minMedianViews.value = null;
  maxMedianViews.value = null;
  selectedMedianViewsRanges.value = [];
  filterData.medianViewsRange = {
    minMedianViews: null,
    maxMedianViews: null,
    selectedRanges: [],
  };

  console.log(socialMediaCategoryList.value);
  queryParam.value.platformType = '2';
  queryParam.value.countryCode = 'US';
  queryParam.value.isHasEmail = ' ';
  onTagTypeChange('2');
  const tableWrapper = document.querySelector('.surely-table-body');
  if (tableWrapper) {
    tableWrapper.scrollTo({
      top: 0,
      behavior: 'smooth', // 平滑滚动
    });
  }
  fetchTable(1);

  nextTick(() => {
    calcTableHeight();
  });
}
function parseCategoryText(id, row) {
  const filterList = row.dataList.filter((item) => item.typeId == id);
  if (filterList.length > 0) {
    return filterList
      .map((item) => item.list.map((category) => category.attributeName).join(', '))
      .join(', ');
  }
  return '';
}
function parseCountry(country) {
  if (country) {
    return countrys.value.find((item) => item.country == country)?.shortCode || "-";
  } else {
    return "-";
  }
}
// 以下函数从 Vue2 版本保留，当前未在新版中直接使用，如需还原对应展示可基于此实现
// async function initSocialMediaCategoryList() {}
// function parseProductData(text) {}
// function parseTime(time) {}
// function parseCategory(id, row) {}

async function initCatoryTypeList() {
  const res = defHttp.get({ url: '/kol/kolAttributeType/listAll' });
  const categoryRes = defHttp.get({ url: '/kol/attribute/getKolAttribute' });
  Promise.all([res, categoryRes])
    .then(async ([res, categoryRes]) => {
      if (res && categoryRes) {
        categoryTypeList.value = res.filter((item) => item.type == 2);
        console.log(categoryTypeList.value);

            // let typeCodeCategory = res.result.filter(item => item.type == 1)[0]
            // // this.productCategoryList = res.result.filter(item => item.type == 2)
            // if (!typeCodeCategory) {
            //   this.$message.error('未配置产品类目类型')
            //   return
            // }
            // await this.initProductCategory(typeCodeCategory.typeCode)

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
            };
          }
        });
        console.log(socialMediaCategoryList.value);
        await initProductCategory();
        await initAttributeList();

        fetchTable(1);
        // console.log(columns.value, dataSource.value);
      }
    })
    .catch((error) => {
      console.error('获取数据失败:', error);
    });
}

async function initTagTypeAndGroup() {
  const tagTypePromise = getDictItems('tag_type');
  const tagGroupPromise = getDictItems('tag_group');
  const platformTypePromise = getDictItems('platform_type');
  Promise.all([platformTypePromise, tagTypePromise, tagGroupPromise])
    .then(([platformTypeRes, tagTypeRes, tagGroupRes]) => {
      if (platformTypeRes && tagTypeRes && tagGroupRes) {
        platformTypeList.value = platformTypeRes;
        tagTypeList.value = tagTypeRes;
        tagGroupList.value = tagGroupRes;
        filterTagTypeList.value = getTagTypeListByPlatformType(
          queryParam.value.platformType
        );

        tagType.value = filterTagTypeList.value[0].value;

      }
    })
    .catch((error) => {
      console.error('获取字典数据失败:', error);
    });
}

/**
 * 获取平台类型对应的标签类型
 * @param {string} platformType 平台类型
 * @returns {Array} 标签类型列表
 */
function getTagTypeListByPlatformType(platformType) {
  const found = tagGroupList.value.filter((item) => item.title == platformType)[0];
  if (!found) return [];
  const value = found.value.split(',');
  console.log(value, tagTypeList.value);

  return tagTypeList.value.filter((item) => value.includes(item.value));
}
// 以下方法为从旧版保留的占位方法，当前未在 Vue3 版本中使用，如后续需要相关功能可在此实现
// function handleAdd() {}
// function handleEdit(_record) {}
// async function initTagType() {}
// const storeTagsList = ref([]);
// function getStoreTagsList() {}
// function associationTag() {}
// function platformTypeImg(_text) {}
// function openImport() {}
// function importExcelModelOk() {}
// function editStatus(record) {}
// function downloadTemplate() {}
// 保存当前筛选条件的数据
function saveCurrentFilterData() {
  if (filterType.value) {
    if (filterType.value === 'followersNum') {
      // 保存粉丝数范围数据
      filterData.followersRange = {
        minFollowers: minFollowers.value || null,
        maxFollowers: maxFollowers.value || null,
        selectedRanges: selectedFollowersRanges.value || [],
      };
    } else if (filterType.value === 'effectiveVideoNum') {
      // 保存有效视频范围数据
      filterData.effectiveVideoRange = {
        minVideoCount: minVideoCount.value || null,
        maxVideoCount: maxVideoCount.value || null,
        selectedRanges: selectedVideoRanges.value || [],
      };
    } else if (filterType.value === 'medianViews') {
      // 保存视频中位数范围数据
      filterData.medianViewsRange = {
        minMedianViews: minMedianViews.value || null,
        maxMedianViews: maxMedianViews.value || null,
        selectedRanges: selectedMedianViewsRanges.value || [],
      };
    } else if (filterType.value === 'playAvg') {
      // 保存均播数范围数据
      filterData.playAvgRange = {
        minPlayAvg: minPlayAvg.value || null,
        maxPlayAvg: maxPlayAvg.value || null,
        selectedRanges: selectedPlayAvgRanges.value || [],
      };
    } else if (filterType.value === 'account') {
      filterData.accountData = {
        accountValue: accountValue.value || '',
      };
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
    const data = filterData.followersRange;
    minFollowers.value = data.minFollowers;
    maxFollowers.value = data.maxFollowers;
    selectedFollowersRanges.value = data.selectedRanges;
  } else if (filterTypeValue === 'effectiveVideoNum') {
    // 恢复有效视频范围数据
    const data = filterData.effectiveVideoRange;
    minVideoCount.value = data.minVideoCount;
    maxVideoCount.value = data.maxVideoCount;
    selectedVideoRanges.value = data.selectedRanges;
  } else if (filterTypeValue === 'medianViews') {
    // 恢复视频中位数范围数据
    const data = filterData.medianViewsRange;
    minMedianViews.value = data.minMedianViews;
    maxMedianViews.value = data.maxMedianViews;
    selectedMedianViewsRanges.value = data.selectedRanges;
  } else if (filterTypeValue === 'playAvg') {
    // 恢复均播数范围数据
    const data = filterData.playAvgRange;
    minPlayAvg.value = data.minPlayAvg;
    maxPlayAvg.value = data.maxPlayAvg;
    selectedPlayAvgRanges.value = data.selectedRanges;
  } else if (filterTypeValue === 'account') {
    const data = filterData.accountData || {};
    accountValue.value = data.accountValue || '';
  } else {
    // 恢复属性选择数据，通过计算属性自动获取
    // attributeIds 会通过计算属性自动从 attributeIdsMap[filterType] 获取
  }
}

function selectFollowersRange(range) {
  // 清空自定义输入框
  minFollowers.value = null;
  maxFollowers.value = null;

  // 清空之前的选择，只保留当前选择
  selectedFollowersRanges.value = [range];

  // 不再根据选择的范围自动赋值给输入框

  // 保存数据到filterData
  filterData.followersRange = {
    minFollowers: minFollowers.value,
    maxFollowers: maxFollowers.value,
    selectedRanges: selectedFollowersRanges.value,
  };
}

// 切换粉丝数范围选择
// 旧版中支持多选预设区间，若后续需要可在此恢复切换逻辑
// function toggleFollowersRange(range) {}
// function toggleVideoRange(range) {}

// 获取所有筛选条件的汇总数据
function getAllFilterData() {
  const result = {
    attributeFilters: {},
    followersRange: filterData.followersRange,
    effectiveVideoRange: filterData.effectiveVideoRange,
    tagData: filterData.tagData,
  };

  // 收集所有typeId的attributeIds
  Object.keys(attributeIdsMap).forEach((typeId) => {
    if (attributeIdsMap[typeId] && attributeIdsMap[typeId].length > 0) {
      result.attributeFilters[typeId] = attributeIdsMap[typeId];
    }
  });

  return result;
}

// 应用所有筛选条件
function applyAllFilters(isSearch) {
  // 标记为通过应用按钮关闭
  isApplyClose.value = true;

  // 先保存当前筛选条件的数据
  saveCurrentFilterData();

  const allData = getAllFilterData();
  console.log('应用所有筛选条件:', allData);
  console.log('当前filterData状态:', filterData);
  console.log('当前输入框状态:', {
    minFollowers: minFollowers.value,
    maxFollowers: maxFollowers.value,
    selectedFollowersRanges: selectedFollowersRanges.value,
    minVideoCount: minVideoCount.value,
    maxVideoCount: maxVideoCount.value,
    selectedVideoRanges: selectedVideoRanges.value,
  });

  // 构建查询参数
  const _params = {
    ...queryParam.value,
    attributeFilters: allData.attributeFilters,
    followersRange: allData.followersRange,
    effectiveVideoRange: allData.effectiveVideoRange,
  };
  console.log('当前attributeIdsMap:', attributeIdsMap);

  // 处理粉丝数范围 - 优先使用预设范围，如果没有则使用输入框的值
  let minFollowersValue = null;
  let maxFollowersValue = null;
  if (
    filterData.followersRange.selectedRanges &&
    filterData.followersRange.selectedRanges.length > 0
  ) {
    // 使用预设范围
    const range = filterData.followersRange.selectedRanges[0];
    const rangeValues = parseFollowersRange(range);
    minFollowersValue = rangeValues.min ? rangeValues.min * 1000 : null;
    maxFollowersValue = rangeValues.max ? rangeValues.max * 1000 : null;
  } else {
    // 使用输入框的值
    minFollowersValue = filterData.followersRange.minFollowers
      ? filterData.followersRange.minFollowers * 1000
      : null;
    maxFollowersValue = filterData.followersRange.maxFollowers
      ? filterData.followersRange.maxFollowers * 1000
      : null;
  }

  queryParam.value.minFollowers = minFollowersValue;
  queryParam.value.maxFollowers = maxFollowersValue;
  queryParam.value.minVideoStandardCount =
    filterData.effectiveVideoRange.minVideoCount;
  queryParam.value.maxVideoStandardCount =
    filterData.effectiveVideoRange.maxVideoCount;

  // 处理视频中位数范围
  let minMedianViewsValue = null;
  let maxMedianViewsValue = null;
  if (
    filterData.medianViewsRange.selectedRanges &&
    filterData.medianViewsRange.selectedRanges.length > 0
  ) {
    const range = filterData.medianViewsRange.selectedRanges[0];
    const rangeValues = parseFollowersRange(range);
    minMedianViewsValue = rangeValues.min ? rangeValues.min * 1000 : null;
    maxMedianViewsValue = rangeValues.max ? rangeValues.max * 1000 : null;
  } else {
    minMedianViewsValue = filterData.medianViewsRange.minMedianViews
      ? filterData.medianViewsRange.minMedianViews * 1000
      : null;
    maxMedianViewsValue = filterData.medianViewsRange.maxMedianViews
      ? filterData.medianViewsRange.maxMedianViews * 1000
      : null;
  }
  queryParam.value.minMedianViews = minMedianViewsValue;
  queryParam.value.maxMedianViews = maxMedianViewsValue;

  // 处理均播数范围
  let minPlayAvgValue = null;
  let maxPlayAvgValue = null;
  if (
    filterData.playAvgRange.selectedRanges &&
    filterData.playAvgRange.selectedRanges.length > 0
  ) {
    const range = filterData.playAvgRange.selectedRanges[0];
    const rangeValues = parsePlayAvgRange(range);
    minPlayAvgValue = rangeValues.min ? rangeValues.min * 1000 : null;
    maxPlayAvgValue = rangeValues.max ? rangeValues.max * 1000 : null;
  } else {
    minPlayAvgValue = filterData.playAvgRange.minPlayAvg
      ? filterData.playAvgRange.minPlayAvg * 1000
      : null;
    maxPlayAvgValue = filterData.playAvgRange.maxPlayAvg
      ? filterData.playAvgRange.maxPlayAvg * 1000
      : null;
  }
  queryParam.value.minPlayAvgNum = minPlayAvgValue;
  queryParam.value.maxPlayAvgNum = maxPlayAvgValue;

  // 处理账号筛选
  if (accountValue.value && accountValue.value.length > 0) {
    queryParam.value.uniqueId = accountValue.value
      .split('\n')
      .map((item) => item.trim())
      .filter((item) => item !== '')
      .join(',');
  } else {
    delete queryParam.value.uniqueId;
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

  // 去重并设置到查询参数
  const uniqueAttributeIds = [...new Set(allAttributeIds)];
  queryParam.value.attributeIds = uniqueAttributeIds.join(',');
  console.log(queryParam.value);
  if (isSearch) {
    fetchTable();
  } else {

  }

  console.log('最终查询参数:', queryParam.value);

  // 关闭所有弹窗
  closeAllPopovers();
  nextTick(() => {
    calcTableHeight();
  });

}

// 关闭所有弹窗
function closeAllPopovers() {
  console.log(mainFilterPopover.value)
  // 关闭主筛选弹窗
  if (mainFilterPopover.value) {
    mainFilterPopover.value.hide?.();
  }
  if (medianViewsFilterPopover.value) {
    medianViewsFilterPopover.value.hide?.();
  }
  if (playAvgFilterPopover.value) {
    playAvgFilterPopover.value.hide?.();
  }
  // 关闭粉丝数筛选弹窗
  if (followersFilterPopover.value) {
    followersFilterPopover.value.hide?.();
  }

  // 关闭视频筛选弹窗
  if (videoFilterPopover.value) {
    videoFilterPopover.value.hide?.();
  }
  if (accountFilterPopoverRef.value) {
    accountFilterPopoverRef.value.hide?.();
  }

  // 关闭标签筛选弹窗（如果存在的话）
  // if (tagFilterPopover.value) {
  //   tagFilterPopover.value.hide();
  // }

  // 关闭属性筛选弹窗
  if (filterOptions.value && Array.isArray(filterOptions.value)) {
    filterOptions.value.forEach((item) => {
      if (item && item.typeId) {
        // 对于动态 ref，需要通过特定方式访问
        // 这里先设置 visible 为 false，如果需要 hide 可以通过其他方式
        attributeFilterPopoverVisible[item.typeId] = false;
      }
    });
  }
}

// popover显示时备份当前数据
function onPopoverShow() {
  isApplyClose.value = false;
  // 备份当前所有数据
  backupData.attributeIdsMap = JSON.parse(JSON.stringify(attributeIdsMap));
  backupData.filterData = JSON.parse(JSON.stringify(filterData));
  backupData.minFollowers = minFollowers.value;
  backupData.maxFollowers = maxFollowers.value;
  backupData.selectedFollowersRanges = [...selectedFollowersRanges.value];
  backupData.minVideoCount = minVideoCount.value;
  backupData.maxVideoCount = maxVideoCount.value;
  backupData.selectedVideoRanges = [...selectedVideoRanges.value];
  backupData.accountValue = accountValue.value;
}

// popover隐藏时检查是否需要恢复数据
function onPopoverHide() {
  // 如果不是通过应用按钮关闭，则恢复备份数据
  if (!isApplyClose.value) {
    Object.keys(attributeIdsMap).forEach((key) => {
      delete attributeIdsMap[key];
    });
    Object.assign(attributeIdsMap, JSON.parse(JSON.stringify(backupData.attributeIdsMap)));
    Object.assign(filterData, JSON.parse(JSON.stringify(backupData.filterData)));
    minFollowers.value = backupData.minFollowers;
    maxFollowers.value = backupData.maxFollowers;
    selectedFollowersRanges.value = [...backupData.selectedFollowersRanges];
    minVideoCount.value = backupData.minVideoCount;
    maxVideoCount.value = backupData.maxVideoCount;
    selectedVideoRanges.value = [...backupData.selectedVideoRanges];
    accountValue.value = backupData.accountValue || '';
  }
  // 重置标记
  isApplyClose.value = false;
}

function getAttributeCount(typeId) {
  if (attributeIdsMap.value && attributeIdsMap.value[typeId]) {
    return attributeIdsMap.value[typeId].length;
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

function getAccountCount() {
  if (accountValue.value && accountValue.value.length > 0) {
    return accountValue.value
      .split('\n')
      .map((item) => item.trim())
      .filter((item) => item !== '').length;
  }
  if (queryParam.value.uniqueId) {
    return queryParam.value.uniqueId.split(',').filter((item) => item !== '').length;
  }
  return 0;
}

function _getTagCount() {
  // 如果有选择标签，返回标签数量
  if (tagNameList.value && tagNameList.value.length > 0) {
    return tagNameList.value.length;
  }
  return 0;
}
function _selectVideoRangeBackup(range) {
  // 清空自定义输入框
  minVideoCount.value = null;
  maxVideoCount.value = null;

  // 清空之前的选择，只保留当前选择
  selectedVideoRanges.value = [range];

  // 根据选择的范围自动赋值给输入框
  switch (range) {
    case '0-1万':
      minVideoCount.value = 0;
      maxVideoCount.value = 10000;
      break;
    case '10K-20K':
      minVideoCount.value = 10000;
      maxVideoCount.value = 30000;
      break;
    case '20K-50K':
      minVideoCount.value = 30000;
      maxVideoCount.value = 100000;
      break;
    case '50K-100K':
      minVideoCount.value = 100000;
      maxVideoCount.value = 300000;
      break;
    case '100K+':
      minVideoCount.value = 300000;
      maxVideoCount.value = null; // 不设上限
      break;
  }

  // 保存数据到filterData
  filterData.effectiveVideoRange = {
    minVideoCount: minVideoCount.value,
    maxVideoCount: maxVideoCount.value,
    selectedRanges: selectedVideoRanges.value,
  };
}

function onFollowersInputChange() {
  selectedFollowersRanges.value = [];

  // 保存数据到filterData
  filterData.followersRange = {
    minFollowers: minFollowers.value,
    maxFollowers: maxFollowers.value,
    selectedRanges: selectedFollowersRanges.value,
  };
}

function onVideoInputChange() {
  selectedVideoRanges.value = [];

  // 保存数据到filterData
  filterData.effectiveVideoRange = {
    minVideoCount: minVideoCount.value,
    maxVideoCount: maxVideoCount.value,
    selectedRanges: selectedVideoRanges.value,
  };
}

function selectMedianViewsRange(range) {
  minMedianViews.value = null;
  maxMedianViews.value = null;
  selectedMedianViewsRanges.value = [range];
  filterData.medianViewsRange = {
    minMedianViews: minMedianViews.value,
    maxMedianViews: maxMedianViews.value,
    selectedRanges: selectedMedianViewsRanges.value,
  };
}

function onMedianViewsInputChange() {
  selectedMedianViewsRanges.value = [];
  filterData.medianViewsRange = {
    minMedianViews: minMedianViews.value,
    maxMedianViews: maxMedianViews.value,
    selectedRanges: selectedMedianViewsRanges.value,
  };
}

function selectPlayAvgRange(range) {
  minPlayAvg.value = null;
  maxPlayAvg.value = null;
  selectedPlayAvgRanges.value = [range];
  filterData.playAvgRange = {
    minPlayAvg: minPlayAvg.value,
    maxPlayAvg: maxPlayAvg.value,
    selectedRanges: selectedPlayAvgRanges.value,
  };
}

function onPlayAvgInputChange() {
  selectedPlayAvgRanges.value = [];
  filterData.playAvgRange = {
    minPlayAvg: minPlayAvg.value,
    maxPlayAvg: maxPlayAvg.value,
    selectedRanges: selectedPlayAvgRanges.value,
  };
}

// 主筛选弹窗显示状态管理
function onMainPopoverShow() {
  mainFilterPopoverVisible.value = true;

  console.log('弹窗打开时的状态:', {
    filterType: filterType.value,
    filterOptionsLength: filterOptions.value.length,
    filterOptions: filterOptions.value,
  });

  // 第一次打开时默认选中左侧菜单第一个选项
  if (!filterType.value && filterOptions.value.length > 0) {
    // 如果有动态属性类型，选中第一个
    const firstOption = filterOptions.value[0];
    filterType.value = firstOption.typeId;
    // 加载对应的数据
    options.value = [...firstOption.data].map((item) => {
      return {
        id: item.id,
        attributeName: item.attributeName,
        disabled:
             queryParam.value.brandId ||
             queryParam.value.productId ||
              (queryParam.value.productAttributeIds &&
               queryParam.value.productAttributeIds.length > 0) ||
              (queryParam.value.tagNameList &&queryParam.value.tagNameList.length > 0) ? true :false,
 
      };
    });
    // 恢复该筛选条件的已保存数据
    restoreFilterData(firstOption.typeId);
    console.log('选中动态属性类型:', firstOption.typeId, 'options:', options.value);
  } else if (!filterType.value) {
    // 如果没有动态属性类型，默认选中粉丝数
    filterType.value = 'followersNum';
    // 恢复粉丝数数据
    restoreFilterData('followersNum');
    console.log('选中粉丝数, filterType:', filterType.value);
  }

  console.log('弹窗打开后的状态:', {
    filterType: filterType.value,
    options: options.value,
    minFollowers: minFollowers.value,
    maxFollowers: maxFollowers.value,
    selectedFollowersRanges: selectedFollowersRanges.value,
  });

  onPopoverShow(); // 调用原有的备份逻辑
}

function onMainPopoverHide() {
  mainFilterPopoverVisible.value = false;
  // 重置 filterType 和 options，确保下次打开时能重新应用默认选择逻辑
  filterType.value = '';
  options.value = [];
  onPopoverHide(); // 调用原有的恢复逻辑
}


// 粉丝数筛选弹窗显示状态管理
function onFollowersPopoverShow() {
  followersFilterPopoverVisible.value = true;
  onPopoverShow();
}

function onFollowersPopoverHide() {
  followersFilterPopoverVisible.value = false;
  onPopoverHide();
}

// 有效视频筛选弹窗显示状态管理
function onVideoPopoverShow() {
  videoFilterPopoverVisible.value = true;
  onPopoverShow();
}

function onVideoPopoverHide() {
  videoFilterPopoverVisible.value = false;
  onPopoverHide();
}

// 视频中位数筛选弹窗显示状态管理
function onMedianViewsPopoverShow() {
  medianViewsFilterPopoverVisible.value = true;
  onPopoverShow();
}

function onMedianViewsPopoverHide() {
  medianViewsFilterPopoverVisible.value = false;
  onPopoverHide();
}

// 均播数筛选弹窗显示状态管理
function onPlayAvgPopoverShow() {
  playAvgFilterPopoverVisible.value = true;
  onPopoverShow();
}

function onPlayAvgPopoverHide() {
  playAvgFilterPopoverVisible.value = false;
  onPopoverHide();
}

// 账号筛选弹窗显示状态管理
function onAccountPopoverShow() {
  accountFilterPopoverVisible.value = true;
  onPopoverShow();
}

function onAccountPopoverHide() {
  accountFilterPopoverVisible.value = false;
  onPopoverHide();
}

// 属性筛选弹窗显示状态管理
function onAttributePopoverShow(typeId) {
  attributeFilterPopoverVisible[typeId] = true;
  onPopoverShow();
}

function onAttributePopoverHide(typeId) {
  attributeFilterPopoverVisible[typeId] = false;
  onPopoverHide();
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

  // 解析均播数范围字符串，返回具体的数值范围
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
      return { min: 300, max: null }; // 不设上限
    default:
      return { min: null, max: null };
  }
}
queryParam.value.platformType = '2';
queryParam.value.countryCode = 'US';
queryParam.value.isHasEmail = ' ';
initTagTypeAndGroup();
initCountry();
initBrandList();
// 初始化
initCatoryTypeList();
</script>
<style scoped lang="less">
.contactHistory_noData {
  display: flex;
  gap: 4px;
  align-items: center;
  color:#ADADAD;
}
.contactHistory_noData .circle{
  
  width: 10px;
  height: 10px;
  border-radius: 100%;
  background-color:#ADADAD;

}
.celebrityPrivate{
  background-color: #EEF2FF;
  color: #3155ED;
  // padding: 2px 6px;
  border-radius: 6px;
  font-size: 12px;
  // width: 34px;
  border: none;
  margin-right: 0px;
}
.allotCelebritiesList {
  padding: 12px;
 
}
.searchForm {
  background-color: white;
  padding: 12px 12px 4px 12px;
  margin-bottom: 12px;
}
  /deep/ .ant-radio-button-wrapper:not(:first-child)::before {
  width: 0.1px !important;
  background-color: transparent;
}
/deep/ .ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before {
  background-color: #3155ed !important;
}
.taglistRow .ant-tag {
  max-width: 186px !important;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
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

.main-filter-closed {
  display: inline-flex !important;
  align-items: center !important; 
}
/* 当 mainFilterPopoverVisible 为 false 时，图标不旋转 (0 度) */
.main-filter-closed .rotating-arrow-filter-icon {
  transform: rotate(0deg);
}
:deep(.input-num .ant-input-number-handler-wrap) {
  display: none !important;
}
:deep(.tagSelect .ant-select-selection-item) {
  max-width: 100px !important;
}

:deep(.vxecelebrityMatchingListtable .ant-tag) {
  max-width: 180px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}


.colum_account {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
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
        // align-items: center;
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
    // overflow-y: auto;
  }
  .more-filter-footer {
    align-items: center;
    border-top: 1px solid #d9d9d9;
    display: flex;
    justify-content: flex-end;
    padding: 8px 16px;
    padding: 8px 16px;
    padding-bottom: 0px;
    padding-top: 12px;
  }
}
.category-list {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  /* gap: 10px; */
  .category-item {
    display: flex;
    align-items: center;
  }
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
.level-middle {
  color: #1890ff;
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
  left: 66px;
  
}

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
  display: flex !important;
  align-items: center;
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
  // color: @primary-color;
  // border: 1px solid @primary-color;
}

:deep(.more-filter-button span) {
  display: inline-block !important;
  max-width: 350px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
  // line-height: 32px;
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
.virtual-list {
  height: 256px;
  overflow-y: auto;
}
.colum_avatarUrl {
  width: 70px;
  height: 70px;
  line-height: 70px;
  margin-right: 20px;
  display: flex;
  // position: relative;
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


</style>
