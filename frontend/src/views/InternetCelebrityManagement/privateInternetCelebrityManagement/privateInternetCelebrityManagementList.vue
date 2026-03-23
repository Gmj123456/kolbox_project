<template>
  <a-card :bordered="false">
    <a-form  class="searchForm" @keyup.enter.native="searchQuery">
      <a-row :gutter="12">
        <a-col class="platform-card-group" :xl="3" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-checkbox-group
                style=" width: 100%"
                v-model:value="queryParam.platformTypeArr"
              >
                <div style="display: flex; height: 32px; width: 100%">
                  <div
                    class="platform-card"
                    :style="{
                      borderRadius: '4px 0 0 4px',

                      borderRight:
                        queryParam.platformTypeArr.length > 1 &&
                        queryParam.platformTypeArr.includes('2') &&
                        queryParam.platformTypeArr.includes('1')
                          ? '1px solid #fff'
                          : '',
                    }"
                    :class="{ CheckboxChecked: queryParam.platformTypeArr.includes('2') }"
                  >
                    <a-checkbox value="2">
                      <img
                        src="@/assets/images/tk.png"
                        alt=""
                        style="width: 20px; height: 20px; margin-right: 4px"
                      />
                      <span >TK</span>
                    </a-checkbox>
                  </div>
                  <div
                    class="platform-card platform-card-yt"
                    :class="{
                      CheckboxChecked: queryParam.platformTypeArr.includes('1'),
                    }"
                  >
                    <a-checkbox value="1">
                      <img
                        src="@/assets/images/yt.png"
                        alt=""
                        style="width: 20px; height: 20px; margin-right: 4px"
                      />
                      <span>YT</span>
                    </a-checkbox>
                  </div>
                  <div
                    class="platform-card"
                    :class="{ CheckboxChecked: queryParam.platformTypeArr.includes('0') }"
                    :style="{
                      borderRadius: '0 4px 4px 0',
                      borderLeft:
                        queryParam.platformTypeArr.length > 1 &&
                        queryParam.platformTypeArr.includes('0') &&
                        queryParam.platformTypeArr.includes('1')
                          ? '1px solid #fff'
                          : '',
                    }"
                  >
                    <a-checkbox value="0">
                      <img
                        src="@/assets/images/ins.png"
                        alt=""
                        style="width: 20px; height: 20px; margin-right: 4px"
                      />
                      <span>IG</span>
                    </a-checkbox>
                  </div>
                </div>
              </a-checkbox-group>
          </a-form-item>
        </a-col>
        <a-col :xl="2" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-input
              v-model:value="queryParam.account"
              allowClear
              placeholder="账号"
            ></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="2" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-select
            style="width: 100%"
              v-model:value="queryParam.countryId"
              show-search
              option-filter-prop="label"
              allowClear
              placeholder="国家"
            >
              <a-select-option
                v-for="(item, key) in countrys"
                :key="key"
                :value="item.id"
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
              style="width: 100%"
              v-model:value="queryParam.gender"
              allowClear
              placeholder="性別"
            >
              <a-select-option :value="0">男</a-select-option>
              <a-select-option :value="1">女</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :xl="3" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-input
              v-model:value="queryParam.email"
              allowClear
              placeholder="网红邮箱"
            ></a-input>
          </a-form-item>
        </a-col>
        <a-col :xl="2" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-select
            
              show-search
              option-filter-prop="label"
              v-model:value="queryParam.celebrityCounselorId"
              allowClear
              placeholder="网红顾问"
            >
              <a-select-option
                v-for="(user, key) in celebrityConsultantList"
                :key="key"
                :value="user.id"
                :label="user.username"
              >
                {{ user.username }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :xl="2" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <JDictSelectTag :showChooseOption="false" v-model:value="queryParam.isTopStar" placeholder="段位" 
            dictCode="celebrity_segment"/>
           
          </a-form-item>
        </a-col>

        <a-col :xl="2" :lg="7" :md="8" :sm="24">
          <a-form-item>
            <a-select
              v-model:value="queryParam.isAbnormalAccount"
              allowClear
              placeholder="状态"
            >
              <a-select-option :value="0">正常</a-select-option>
              <a-select-option :value="1">异常</a-select-option>
              <a-select-option :value="2">待更新</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :xl="6" :lg="10" :md="12" :sm="24">
            <a-input-group style="top: 0px !important" compact>
              <a-select
                style="width: 100px;border-right: 0px;"
                v-model:value="searchType"
                @change="onSearchTypeChange"
              >
                <a-select-option value="0"> 推广产品 </a-select-option>
                <a-select-option value="1"> 产品类目 </a-select-option>
              </a-select>
              <template v-if="searchType == '0'">
                <a-select
                  style="width: 120px;border-right: 0px;"
                  show-search
                  allowClear
                  option-filter-prop="label"
                  v-model:value="queryParam.brandId"
                  placeholder="品牌"
                  :dropdownMatchSelectWidth="false"
                  :dropdownStyle="{ width: '150px' }"
                  @change="onBrandChange"
                >
                  <a-select-option v-for="item in brandList" :key="item.id" :value="item.id" :label="item.brandName">{{
                    item.brandName
                  }}</a-select-option>
                </a-select>
                <a-select
                  style="width: calc(100% - 220px)"
                  show-search
                  option-filter-prop="label"
                  v-model:value="queryParam.productId"
                  placeholder="产品"
                  @change="onProductChange"
                  allowClear
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
              </template>
              <template v-if="searchType == '1'">
                <a-tree-select
                  style="width: calc(100% - 100px)"
                  v-model:value="queryParam.categoryIds"
                  show-search
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="产品类目"
                  allow-clear
                  :treeData="productCategoryList"
                  :replaceFields="{ label: 'categoryName', key: 'id', value: 'id' }"
                   tree-node-filter-prop="categoryName"
                  @change="onCategoryChange"
                ></a-tree-select>
              </template>
            </a-input-group>
          </a-col>
      </a-row>
       <a-row :gutter="12">
        <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input-group compact class="contractAmountinput">
                <a-input
                  class="contractAmountinput_input"
                  style="width: 70px"
                  disabled
                  default-value="费用($)"
                />
                <a-input-number
                  :min="0"
                  :max="99999999"
                  style="width: calc(50% - 70px)"
                  step="100"
                  v-model:value="queryParam.mixContractAmount"
                />
                <a-input
                  style="
                    width: 30px;
                    border-left: 0;
                    pointer-events: none;
                    backgroundcolor: #fff;
                  "
                  placeholder="~"
                  disabled
                />
                <a-input-number
                  :min="0"
                  :max="99999999"
                  style="width: calc(50% - 55px); text-align: center; border-left: 0"
                  step="100"
                  v-model:value="queryParam.maxContractAmount"
                />
              </a-input-group>
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <JDictSelectTag :showChooseOption="false" v-model:value="queryParam.isAttributeConfirmed" placeholder="社媒确认状态" 
              dictCode="attribute_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                v-model:value="queryParam.personalTagIds"
                allowClear
                option-filter-prop="label"
                showSearch
                placeholder="个性化标签"
              >
                <a-select-option v-for="item in personalTagList" :key="item.id" :value="item.id" :label="item.tagName">{{
                  item.tagName
                }}</a-select-option>
              </a-select>
            </a-form-item>
            <!-- <a-form-item>
            </a-form-item> -->
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24" style="width: 8.7%">
            <a-form-item>
              <a-select
                v-model:value="queryParam.isPersonalTagsConfirmed"
                allowClear
                placeholder="个性化标签状态"
              >
                <a-select-option :value="0">待确认</a-select-option>
                <a-select-option :value="1">已确认</a-select-option>
              </a-select>
            </a-form-item>
            <!-- <a-form-item>
            </a-form-item> -->
          </a-col>
          <a-col :xl="14" :lg="10" :md="12" :sm="24">
            <a-form-item>
              <span class="table-page-search-submitButtons">
                <el-popover
                  ref="mainFilterPopover"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  width="650"
                  trigger="click"
                  v-model:visible="mainFilterPopoverVisible"
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
                          @click="selectFilter(item.typeId, item.typeName)"
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
                          :class="{ selected: filterType === 'gmv' }"
                          @click="selectFilterOther('gmv')"
                        >
                          <span>视频GMV</span>
                          <span>
                            <span v-if="getGmvCount() > 0" class="selected-count">
                              {{ getGmvCount() }}
                            </span>
                            <i style="color: #606266" class="el-icon-arrow-right"></i>
                          </span>
                        </div>

                        <div
                          class="more-filter-content_menu_item"
                          :class="{ selected: filterType === 'lastPostDate' }"
                          @click="selectFilterOther('lastPostDate')"
                        >
                          <span>发布日期</span>
                          <span>
                            <span
                              v-if="getLastPostDateCount() > 0"
                              class="selected-count"
                            >
                              {{ getLastPostDateCount() }}
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
                          :class="{ selected: filterType === 'historyCooperation' }"
                          @click="selectFilterOther('historyCooperation')"
                        >
                          <span>历史合作</span>
                          <span>
                            <span
                              v-if="getHistoryCooperationCount() > 0"
                              class="selected-count"
                            >
                              {{ getHistoryCooperationCount() }}
                            </span>
                            <i style="color: #606266" class="el-icon-arrow-right"></i>
                          </span>
                        </div>
                        <div
                          class="more-filter-content_menu_item"
                          :class="{ selected: filterType === 'kolTypes' }"
                          @click="selectFilterOther('kolTypes')"
                        >
                          <span>商家达人类型</span>
                          <span>
                            <span v-if="getKolTypesCount() > 0" class="selected-count">
                              {{ getKolTypesCount() }}
                            </span>
                            <i style="color: #606266" class="el-icon-arrow-right"></i>
                          </span>
                        </div>
                      </div>
                      <div class="more-filter-form" v-if="filterType == 'gmv'">
                        <div class="filter-form-select">
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedGmvRanges.includes('0-5K') }"
                            @click="selectGmvRange('0-5K')"
                          >
                            0-5K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedGmvRanges.includes('5K-10K'),
                            }"
                            @click="selectGmvRange('5K-10K')"
                          >
                            5K-10K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedGmvRanges.includes('10K+'),
                            }"
                            @click="selectGmvRange('10K+')"
                          >
                            10K+
                          </div>
                        </div>
                        <div class="range-input">
                          <div class="range-input__custom-label">自定义范围</div>
                          <div class="range-input__content">
                            <div class="range-input__flex">
                              <div class="range-input__label">最小GMV</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  class="input-num"
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  :precision="0"
                                  v-model:value="videoGmvValueStart"
                                  placeholder="输入值"
                                  @change="onGmvInputChange"
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
                              <div class="range-input__label">最大GMV</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  class="input-num"
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999"
                                  :precision="0"
                                  v-model:value="videoGmvValueEnd"
                                  placeholder="输入值"
                                  @change="onGmvInputChange"
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
                      <div class="more-filter-form" v-if="filterType == 'lastPostDate'">
                        <div class="filter-form-select">
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedLastPostDateRanges.includes('近14天'),
                            }"
                            @click="selectLastPostDateRange('近14天')"
                          >
                            近14天
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedLastPostDateRanges.includes('近30天'),
                            }"
                            @click="selectLastPostDateRange('近30天')"
                          >
                            近30天
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedLastPostDateRanges.includes('近60天'),
                            }"
                            @click="selectLastPostDateRange('近60天')"
                          >
                            近60天
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedLastPostDateRanges.includes('近90天'),
                            }"
                            @click="selectLastPostDateRange('近90天')"
                          >
                            近90天
                          </div>
                        </div>
                        <div class="range-input">
                          <div class="range-input__custom-label">大于指定日期</div>
                          <div class="range-input__content">
                            <a-date-picker
                              :getCalendarContainer="getPopupContainer"
                              style="width: 100%"
                              v-model:value="videoCreateTimeLastStart"
                              @change="onLastPostDateChange"
                            />
                          </div>
                        </div>
                      </div>
                      <div
                        v-if="filterType == 'historyCooperation'"
                        class="more-filter-form"
                      >
                        <div class="range-input">
                          <a-select
                            :getPopupContainer="getPopupContainer"
                            show-search
                            allowClear
                            option-filter-prop="label"
                            v-model:value="hesBrandId"
                            placeholder="合作品牌"
                            @change="onHistoricalBrandChange"
                          >
                            <a-select-option v-for="item in brandList" :key="item.id" :value="item.id" :label="item.brandName">{{
                              item.brandName
                            }}</a-select-option>
                          </a-select>
                          <a-select
                            :showArrow="true"
                            :getPopupContainer="getPopupContainer"
                            mode="multiple"
                            :maxTagCount="4"
                            showSearch
                            v-model:value="historyCooperation"
                            placeholder="合作产品"
                          >
                            <a-select-option
                              v-for="item in historyCooperationList"
                              :key="item.id"
                              :value="item.id"
                              >{{ item.text }}</a-select-option
                            >
                          </a-select>
                        </div>
                      </div>
                      <div v-if="filterType == 'kolTypes'" class="more-filter-form">
                        <div class="range-input">
                          <div style="display: flex; align-items: center">
                            <a-select
                              style="width: 100%"
                              :autoClearSearchValue="false"
                              :not-found-content="kolTypeFetching ? undefined : null"
                              @search="fetchKolTypes"
                              :getPopupContainer="getPopupContainer"
                              @change="kolTypeChange"
                              :filter-option="false"
                              :maxTagCount="15"
                              mode="multiple"
                              v-model:value="kolTypes"
                              allowClear
                              placeholder="商家达人类型"
                              @popupScroll="kolTypePopupScroll"
                            >
                              <template #maxTagPlaceholder>
                                <a-tooltip>
                                  <template #title>
                                    <div style="max-height: 200px; overflow-y: auto">
                                      <div
                                        v-for="(item, i) in kolTypes.slice(15)"
                                        :key="i"
                                      >
                                        <div>{{ item }}</div>
                                      </div>
                                    </div>
                                  </template>
                                  <span v-if="kolTypes && kolTypes.length > 15"
                                    >+
                                    {{ kolTypes.length - 15 }}
                                  </span>
                                </a-tooltip>
                              </template>
                              <a-spin
                                v-if="kolTypeFetching"
                                slot="notFoundContent"
                                size="small"
                              />
                              <a-select-option
                                v-for="(item, index) in kolTypesList"
                                :key="item.kolType"
                                :value="item.kolType"
                              >
                                {{ item.kolType }}
                              </a-select-option>
                              <!-- 虚拟滚动加载更多指示器 -->
                              <a-select-option
                                v-if="kolTypeLoading && hasMoreKolTypes"
                                key="loading"
                                disabled
                              >
                                <a-spin size="small" />
                                <span style="margin-left: 8px">加载中...</span>
                              </a-select-option>
                            </a-select>
                            <a-icon
                              style="
                                margin-left: 8px;
                                width: 24px;
                                height: 24px;
                                background-color: #f0f2f5;
                                display: flex;
                                justify-content: center;
                                align-items: center;
                              "
                              :style="{ color: kolTypeVisible ? 'blue' : '' }"
                              @click="kolTypeVisible = !kolTypeVisible"
                              type="align-left"
                            />
                          </div>
                          <div v-if="kolTypeVisible">
                            <div style="display: flex; gap: 8px">
                              <a-textarea
                                style="flex: 1"
                                v-model:value="kolTypeValue"
                                :auto-size="{ minRows: 7, maxRows: 7 }"
                                placeholder="精确输入，一行一项"
                                rows=""
                                cols=""
                              ></a-textarea>

                              <div
                                style="
                                  color: red;
                                  flex: 1;
                                  max-height: 156px;
                                  overflow-y: auto;
                                "
                                v-if="noKolType"
                              >
                                不存在的商家达人类型：
                                <div
                                  style="margin-right: 5px"
                                  v-for="item in nonExistentKolType"
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
                                <a-button
                                  @click="closeKolTypeEdit"
                                  style="margin-right: 50px"
                                  >关闭</a-button
                                >
                              </div>
                              <div>
                                <a-button @click="clearKolType" style="margin-right: 8px"
                                  >清空</a-button
                                >
                                <a-button
                                  type="primary"
                                  @click="kolTypeSearch(kolTypeValue)"
                                  >确定</a-button
                                >
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div
                        v-if="filterType == 'historicalSelection'"
                        class="more-filter-form"
                      >
                        <div class="range-input">
                          <a-input-group style="top: 0px !important" compact>
                            <a-select
                              :getPopupContainer="getPopupContainer"
                              style="width: 100%"
                              mode="multiple"
                              :maxTagCount="4"
                              showSearch
                              v-model:value="historicalSelection"
                              placeholder="请选择历史选中"
                            >
                              <a-select-option
                                v-for="item in historicalSelectionList"
                                :key="item.id"
                                :value="item.id"
                                >{{ item.text }}</a-select-option
                              >
                            </a-select>
                          </a-input-group>
                        </div>
                      </div>
                      <div v-if="filterType == 'playAvg'" class="more-filter-form">
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
                            :class="{
                              selected: selectedPlayAvgRanges.includes('10K-30K'),
                            }"
                            @click="selectPlayAvgRange('10K-30K')"
                          >
                            10K-30K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedPlayAvgRanges.includes('30K-100K'),
                            }"
                            @click="selectPlayAvgRange('30K-100K')"
                          >
                            30K-100K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedPlayAvgRanges.includes('100K-300K'),
                            }"
                            @click="selectPlayAvgRange('100K-300K')"
                          >
                            100K-300K
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedPlayAvgRanges.includes('300K+'),
                            }"
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
                              <div class="range-input__label">最大均播数</div>
                              <div class="range-input__input" style="position: relative">
                                <a-input-number
                                  class="input-num"
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
                      <!--    v-model:value="attributeIdsMap[filterType]" -->
                      <div
                        v-if="
                          filterType !== 'gmv' &&
                          filterType !== 'followersNum' &&
                          filterType !== 'playAvg' &&
                          filterType !== 'historicalSelection' &&
                          filterType !== 'historyCooperation' &&
                          filterType !== 'lastPostDate' &&
                          filterType !== 'kolTypes'
                        "
                        class="more-filter-form"
                      >
                        <el-cascader-panel
                          :ref="'cascaderPanel' + filterType"
                          v-model="attributeIdsMap[filterType]"
                          @change="onAttributeChange"
                          :options="options"
                          :props="{
                            multiple: true,
                            value: 'id',
                            label: 'attributeName',
                            children: 'children',

                            emitPath: false,
                          }"
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
                    </div>
                    <div class="more-filter-footer">
                      <a-button
                        type="primary"
                        style="margin-right: 8px"
                        :icon="h(SafetyCertificateOutlined)"
                        @click="applyAllFilters(false)"
                        >应用</a-button
                      >
                      <a-button
                        type="primary"
                         :icon="h(SearchOutlined)"
                        @click="applyAllFilters(true)"
                        >查询</a-button
                      >
                    </div>
                  </div>
                  <template #reference>
                    <a-button
                      :class="
                        mainFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
                      "
                      >更多筛选
                      <a-icon class="rotating-arrow-filter-icon" :type="'down'" />
                    </a-button>
                  </template>
                </el-popover>
                <a-button
                  type="primary"
                  @click="searchQuery"
                  style="margin-left: 8px"
                  :icon="h(SearchOutlined)"
                  >查询</a-button
                >
                <a-button @click="searchReset" :icon="h(ReloadOutlined)" style="margin-left: 8px"
                  >重置</a-button
                >

              
                <a-button
                  type="primary"
                  :icon="h(DownloadOutlined)"
                  style="margin-left: 8px"
                  v-if="hasPermission('user:export')"
                  @click="exportPrivateCelebrity"
                  >导出</a-button
                >
                <a-dropdown>
                  <a-button  v-if="hasPermission('user:import')" type="primary" style="margin-left: 8px">
                    模版地址  <DownOutlined />
               </a-button>
                  <template #overlay>
                  
                    <a-menu >
                      <a-menu-item key="1">
                        <span @click="getCelebrityPrivateURL">私有网红模版</span>
                      </a-menu-item>
                      <a-menu-item key="2">
                        <span @click="getHistoryURL">历史合作模版</span>
                      </a-menu-item>
                    </a-menu>
                  </template>
                
                </a-dropdown>

             
                <a-button
                  type="primary"
                  :icon="h(TeamOutlined)"
                  style="margin-left: 8px"
                  @click="adjustmentConsultant"
                  v-if="hasPermission('user:modificationcelebrity')"
                 
                  >调整顾问</a-button
                >
                <a-button
                  type="primary"
                  :icon="h(FileSyncOutlined)"
                  :disabled="selectedRowKeys.length == 0"
                  style="margin-left: 8px"
                  @click="updateCelebrityStatus(2)"  
                  >更新网红状态</a-button
                >

                <a-button
                  type="primary"
                  :icon="h(SyncOutlined)"
                  style="margin-left: 8px"
                  v-if="hasPermission('import:privatedata')"
               
                  @click="syncPrivateCelebrity"
                  >同步私有网红</a-button
                >
                <a-button
                  type="primary"
                 :icon="h(SyncOutlined)"
                  v-if="hasPermission('import:product')"
                  style="margin-left: 8px"
                  @click="syncProduct"
                  >同步历史合作</a-button
                >
              </span>
            </a-form-item>
          </a-col>
       </a-row>
       <a-row :gutter="12"  v-if="
                  queryParam.cooperatedProductIds ||
                  queryParam.selectedProductIds ||
                  queryParam.playAvgStartNum ||
                  queryParam.playAvgEndNum ||
                  queryParam.followersStartNum ||
                  queryParam.followersEndNum ||
                  queryParam.videoCreateTimeLastStart ||
                  queryParam.attributeIds ||
                  queryParam.videoGmvValueStart ||
                  queryParam.videoGmvValueEnd ||
                  queryParam.kolTypes
                ">
          <a-col  :span="24" style="margin-bottom: 8px">
            <span
           
              style="display: flex; flex-wrap: wrap; gap: 8px"
            >
              <el-popover
                ref="historyCooperationFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
                trigger="click"
                v-model:visible="historyCooperationFilterPopoverVisible"
                @show="onHistoryCooperationPopoverShow"
                @hide="onHistoryCooperationPopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="range-input">
                        <a-select
                          :getPopupContainer="getPopupContainer"
                          show-search
                          allowClear
                          option-filter-prop="label"
                          v-model:value="hesBrandId"
                          placeholder="合作品牌"
                          @change="onHistoricalBrandChange"
                        >
                          <a-select-option v-for="item in brandList" :key="item.id" :value="item.id" :label="item.brandName">{{
                            item.brandName
                          }}</a-select-option>
                        </a-select>
                        <a-select
                          :showArrow="true"
                          :getPopupContainer="getPopupContainer"
                          mode="multiple"
                          :maxTagCount="4"
                          showSearch
                          v-model:value="historyCooperation"
                          @change="historyCooperationChange"
                          placeholder="合作产品"
                        >
                          <a-select-option
                            v-for="item in historyCooperationList"
                            :key="item.id"
                            :value="item.id"
                            :label="item.text"
                            >{{ item.text }}</a-select-option
                          >
                        </a-select>
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                      >查询</a-button
                    >
                  </div>
                </div>

                <template #reference>
                  <div
                    v-show="queryParam.cooperatedProductIds"
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span> 历史合作： </span>
                    <span v-if="queryParam.cooperatedProductIds">{{
                      parseHistoryCooperationProductIds(
                        queryParam.cooperatedProductIds
                      )
                    }}</span>

                    <span
                      style="margin-left: 6px"
                      :class="
                        historyCooperationFilterPopoverVisible
                          ? 'main-filter-open'
                          : 'main-filter-closed'
                      "
                    >
                      <a-icon class="rotating-arrow-filter-icon" type="down" />
                    </span>
                    <span>
                      <a-icon
                        @click.stop.prevent="clearFilterData($event, 'historyCooperation')"
                        @mousedown.stop.prevent
                        type="close-circle"
                        theme="filled"
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <!-- 商家达人类型筛选 -->
              <el-popover
                ref="kolTypeFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
                trigger="click"
                v-model:visible="kolTypeFilterPopoverVisible"
                @show="onKolTypePopoverShow"
                @hide="onKolTypePopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="range-input">
                        <div style="display: flex; align-items: center">
                          <a-select
                            style="width: 100%"
                            :autoClearSearchValue="false"
                            :not-found-content="kolTypeFetching ? undefined : null"
                            @search="fetchKolTypes"
                            :getPopupContainer="getPopupContainer"
                            @change="kolTypeChange"
                            :filter-option="false"
                            :maxTagCount="15"
                            mode="multiple"
                            v-model:value="kolTypes"
                            allowClear
                            placeholder="商家达人类型"
                            @popupScroll="kolTypePopupScroll"
                          >
                            <template #maxTagPlaceholder>
                              <a-tooltip>
                                <template #title>
                                  <div style="max-height: 200px; overflow-y: auto">
                                    <div v-for="(item, i) in kolTypes.slice(15)" :key="i">
                                      <div>{{ item }}</div>
                                    </div>
                                  </div>
                                </template>
                                <span v-if="kolTypes && kolTypes.length > 15"
                                  >+
                                  {{ kolTypes.length - 15 }}
                                </span>
                              </a-tooltip>
                            </template>
                            <a-spin
                              v-if="kolTypeFetching"
                              slot="notFoundContent"
                              size="small"
                            />
                            <a-select-option
                              v-for="(item, index) in kolTypesList"
                              :key="item.kolType"
                              :value="item.kolType"
                            >
                              {{ item.kolType }}
                            </a-select-option>
                            <!-- 虚拟滚动加载更多指示器 -->
                            <a-select-option
                              v-if="kolTypeLoading && hasMoreKolTypes"
                              key="loading"
                              disabled
                            >
                              <a-spin size="small" />
                              <span style="margin-left: 8px">加载中...</span>
                            </a-select-option>
                          </a-select>
                          <a-icon
                            style="
                              margin-left: 8px;
                              width: 24px;
                              height: 24px;
                              background-color: #f0f2f5;
                              display: flex;
                              justify-content: center;
                              align-items: center;
                            "
                            :style="{ color: kolTypeVisible ? 'blue' : '' }"
                            @click="kolTypeVisible = !kolTypeVisible"
                            type="align-left"
                          />
                        </div>
                        <div v-if="kolTypeVisible">
                          <div style="display: flex; gap: 8px">
                            <a-textarea
                              style="flex: 1"
                              v-model:value="kolTypeValue"
                              :auto-size="{ minRows: 7, maxRows: 7 }"
                              placeholder="精确输入，一行一项"
                              rows=""
                              cols=""
                            ></a-textarea>

                            <div
                              style="
                                color: red;
                                flex: 1;
                                max-height: 156px;
                                overflow-y: auto;
                              "
                              v-if="noKolType"
                            >
                              不存在的商家达人类型：
                              <div
                                style="margin-right: 5px"
                                v-for="item in nonExistentKolType"
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
                              <a-button
                                @click="closeKolTypeEdit"
                                style="margin-right: 50px"
                                >关闭</a-button
                              >
                            </div>
                            <div>
                              <a-button @click="clearKolType" style="margin-right: 8px"
                                >清空</a-button
                              >
                              <a-button
                                type="primary"
                                @click="kolTypeSearch(kolTypeValue)"
                                >确定</a-button
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                      >查询</a-button
                    >
                  </div>
                </div>

                <template #reference>
                  <div
                    v-show="queryParam.kolTypes"
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span> 商家达人类型： </span>
                    <span v-if="queryParam.kolTypes">{{
                      parseKolTypes(queryParam.kolTypes)
                    }}</span>

                    <span
                      style="margin-left: 6px"
                      :class="
                        kolTypeFilterPopoverVisible
                          ? 'main-filter-open'
                          : 'main-filter-closed'
                      "
                    >
                      <a-icon class="rotating-arrow-filter-icon" type="down" />
                    </span>
                    <span>
                      <a-icon
                        @click.stop.prevent="clearFilterData($event, 'kolTypes')"
                        @mousedown.stop.prevent
                        type="close-circle"
                        theme="filled"
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <el-popover
                ref="historicalSelectionFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
                trigger="click"
                v-model:visible="historicalSelectionFilterPopoverVisible"
                @show="onHistoricalSelectionPopoverShow"
                @hide="onHistoricalSelectionPopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="range-input">
                        <a-select
                          :getPopupContainer="getPopupContainer"
                          style="width: 100%"
                          mode="multiple"
                          :maxTagCount="4"
                          showSearch
                          v-model:value="historicalSelection"
                          placeholder="请选择历史选中"
                        >
                          <a-select-option
                            v-for="item in historicalSelectionList"
                            :key="item.id"
                            :value="item.id"
                            >{{ item.text }}</a-select-option
                          >
                        </a-select>
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                      >查询</a-button
                    >
                  </div>
                </div>

                <template #reference>
                  <div
                    v-show="queryParam.selectedProductIds"
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span> 历史选中： </span>
                    <span>{{
                      parseHistoricalSelectionProductIds(queryParam.selectedProductIds)
                    }}</span>
                    <span
                      style="margin-left: 6px"
                      :class="
                        historicalSelectionFilterPopoverVisible
                          ? 'main-filter-open'
                          : 'main-filter-closed'
                      "
                    >
                      <a-icon class="rotating-arrow-filter-icon" type="down" />
                    </span>
                    <span>
                      <a-icon
                        @click.stop.prevent="clearFilterData($event, 'historicalSelection')"
                        @mousedown.stop.prevent
                        type="close-circle"
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <el-popover
                ref="playAvgFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
                trigger="click"
                v-model:visible="playAvgFilterPopoverVisible"
                @show="onPlayAvgPopoverShow"
                @hide="onPlayAvgPopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="filter-form-select">
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedPlayAvgRanges.includes('0-1K') }"
                          @click="selectPlayAvgRange('0-10K')"
                        >
                          0-10K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedPlayAvgRanges.includes('10K-30K'),
                          }"
                          @click="selectPlayAvgRange('10K-30K')"
                        >
                          10K-30K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedPlayAvgRanges.includes('30K-100K'),
                          }"
                          @click="selectPlayAvgRange('30K-100K')"
                        >
                          30K-100K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedPlayAvgRanges.includes('100K-300K'),
                          }"
                          @click="selectPlayAvgRange('100K-300K')"
                        >
                          100K-300K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedPlayAvgRanges.includes('300K+'),
                          }"
                          @click="selectPlayAvgRange('300K+')"
                        >
                          30万+
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
                                :precision="0"
                                v-model:value="minPlayAvg"
                                placeholder="输入值"
                                @change="onPlayAvgInputChange"
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
                            <div class="range-input__label">最大均播数</div>
                            <div class="range-input__input" style="position: relative">
                              <a-input-number
                                class="input-num"
                                style="width: 100%"
                                :min="0"
                                :max="99999"
                                :precision="0"
                                v-model:value="maxPlayAvg"
                                placeholder="输入值"
                                @change="onPlayAvgInputChange"
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                      >查询</a-button
                    >
                  </div>
                </div>

                <template #reference>
                  <div
                    v-show="
                      queryParam.playAvgStartNum || queryParam.playAvgEndNum
                    "
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span> 均播数： </span>
                    <span
                      v-if="
                        queryParam.playAvgStartNum && queryParam.playAvgEndNum
                      "
                      >{{ queryParam.playAvgStartNum }} -
                      {{ queryParam.playAvgEndNum }}</span
                    >
                    <span
                      v-else-if="
                        queryParam.playAvgStartNum && !queryParam.playAvgEndNum
                      "
                      >≥ {{ queryParam.playAvgStartNum }}</span
                    >
                    <span
                      v-else-if="
                        !queryParam.playAvgStartNum && queryParam.playAvgEndNum
                      "
                      >≤ {{ queryParam.playAvgEndNum }}</span
                    >
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
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <el-popover
                ref="followersFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
                trigger="click"
                v-model:visible="followersFilterPopoverVisible"
                @show="onFollowersPopoverShow"
                @hide="onFollowersPopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
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
                  </div>
                  <div class="more-filter-footer">
                    <a-button
                      type="primary"
                      style="margin-right: 8px"
                      :icon="h(SafetyCertificateOutlined)"
                      @click="applyAllFilters(false)"
                      >应用</a-button
                    >
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                      >查询</a-button
                    >
                  </div>
                </div>

                <template #reference>
                  <div
                    v-show="
                      queryParam.followersStartNum || queryParam.followersEndNum
                    "
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span> 粉丝数： </span>
                    <span
                      v-if="
                        queryParam.followersStartNum && queryParam.followersEndNum
                      "
                      >{{ queryParam.followersStartNum }} -
                      {{ queryParam.followersEndNum }}</span
                    >
                    <span
                      v-else-if="
                        queryParam.followersStartNum &&
                        !queryParam.followersEndNum
                      "
                      >≥ {{ queryParam.followersStartNum }}</span
                    >
                    <span
                      v-else-if="
                        !queryParam.followersStartNum &&
                        queryParam.followersEndNum
                      "
                      >≤ {{ queryParam.followersEndNum }}</span
                    >
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
                ref="gmvFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
                trigger="click"
                v-model:visible="gmvFilterPopoverVisible"
                @show="onGmvPopoverShow"
                @hide="onGmvPopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="filter-form-select">
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedGmvRanges.includes('0-5K') }"
                          @click="selectGmvRange('0-5K')"
                        >
                          0-5K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedGmvRanges.includes('5K-10K'),
                          }"
                          @click="selectGmvRange('5K-10K')"
                        >
                          5K-10K
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedGmvRanges.includes('10K+'),
                          }"
                          @click="selectGmvRange('10K+')"
                        >
                          10K+
                        </div>
                      </div>
                      <div class="range-input">
                        <div class="range-input__custom-label">自定义范围</div>
                        <div class="range-input__content">
                          <div class="range-input__flex">
                            <div class="range-input__label">最小GMV</div>
                            <div class="range-input__input" style="position: relative">
                              <a-input-number
                                style="width: 100%"
                                :min="0"
                                :max="99999"
                                :precision="0"
                                v-model:value="videoGmvValueStart"
                                placeholder="输入值"
                                @change="onGmvInputChange"
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
                            <div class="range-input__label">最大GMV</div>
                            <div class="range-input__input" style="position: relative">
                              <a-input-number
                                style="width: 100%"
                                :min="0"
                                :max="99999"
                                :precision="0"
                                v-model:value="videoGmvValueEnd"
                                placeholder="输入值"
                                @change="onGmvInputChange"
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                      >查询</a-button
                    >
                  </div>
                </div>

                <template #reference>
                  <div
                    v-show="
                      queryParam.videoGmvValueStart || queryParam.videoGmvValueEnd
                    "
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span> 视频GMV： </span>
                    <span
                      v-if="
                        queryParam.videoGmvValueStart &&
                        queryParam.videoGmvValueEnd
                      "
                      >{{ queryParam.videoGmvValueStart }} -
                      {{ queryParam.videoGmvValueEnd }}</span
                    >
                    <span
                      v-else-if="
                        queryParam.videoGmvValueStart &&
                        !queryParam.videoGmvValueEnd
                      "
                      >≥ {{ queryParam.videoGmvValueStart }}</span
                    >
                    <span
                      v-else-if="
                        !queryParam.videoGmvValueStart &&
                        queryParam.videoGmvValueEnd
                      "
                      >≤ {{ queryParam.videoGmvValueEnd }}</span
                    >
                    <span
                      style="margin-left: 6px"
                      :class="
                        gmvFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
                      "
                    >
                      <a-icon class="rotating-arrow-filter-icon" type="down" />
                    </span>
                    <span>
                      <a-icon
                        @click.stop.prevent="clearFilterData($event, 'gmv')"
                        @mousedown.stop.prevent
                        type="close-circle"
                        theme="filled"
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <!-- 最近发布日期筛选按钮 -->
              <el-popover
                ref="lastPostDateFilterPopover"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
                trigger="click"
                v-model:visible="lastPostDateFilterPopoverVisible"
                @show="onLastPostDatePopoverShow"
                @hide="onLastPostDatePopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="filter-form-select">
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedLastPostDateRanges.includes('近14天'),
                          }"
                          @click="selectLastPostDateRange('近14天')"
                        >
                          近14天
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedLastPostDateRanges.includes('近30天'),
                          }"
                          @click="selectLastPostDateRange('近30天')"
                        >
                          近30天
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedLastPostDateRanges.includes('近60天'),
                          }"
                          @click="selectLastPostDateRange('近60天')"
                        >
                          近60天
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedLastPostDateRanges.includes('近90天'),
                          }"
                          @click="selectLastPostDateRange('近90天')"
                        >
                          近90天
                        </div>
                      </div>
                      <div class="range-input">
                        <div class="range-input__custom-label">大于指定日期</div>
                        <div class="range-input__content">
                          <a-date-picker
                            :getCalendarContainer="getPopupContainer"
                            style="width: 100%"
                            v-model:value="videoCreateTimeLastStart"
                            @change="onLastPostDateChange"
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
                      >查询</a-button
                    >
                  </div>
                </div>

                <template #reference>
                  <div
                    v-show="queryParam.videoCreateTimeLastStart"
                    class="more-filter-button"
                    style="margin-right: 8px"
                  >
                    <span>最近发布日期：</span>
                    <span v-if="selectedLastPostDateRanges.length > 0">
                      {{ selectedLastPostDateRanges[0] }}
                    </span>
                    <span v-else-if="queryParam.videoCreateTimeLastStart">
                      大于 {{ parseDate(queryParam.videoCreateTimeLastStart) }}
                    </span>
                    <span style="margin-left: 6px">
                      <a-icon type="down" />
                    </span>
                    <span>
                      <a-icon
                        @click.stop.prevent="clearFilterData($event, 'lastPostDate')"
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
                  placement="bottom-start"
                  v-model:visible="attributeFilterPopoverVisible[item.typeId]"
                  width="400"
                  trigger="click"
                  @show="onAttributePopoverShow(item.typeId)"
                  @hide="onAttributePopoverHide(item.typeId)"
                >
                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-form" style="overflow-x: auto !important">
                        <el-cascader-panel
                          v-model="attributeIdsMap[item.typeId]"
                          :options="processTreeData(item.data)"
                          :props="{
                            multiple: true,
                            value: 'id',
                            label: 'attributeName',
                            emitPath: false,
                          }"
                        ></el-cascader-panel>
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
                      <a-button
                        type="primary"
                        :icon="h(SearchOutlined)" 

                        @click="applyAllFilters(true)"
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
                  queryParam.cooperatedProductIds ||
                  queryParam.selectedProductIds ||
                  queryParam.playAvgStartNum ||
                  queryParam.playAvgEndNum ||
                  queryParam.followersStartNum ||
                  queryParam.followersEndNum ||
                  queryParam.videoCreateTimeLastStart ||
                  queryParam.attributeIds ||
                  queryParam.videoGmvValueStart ||
                  queryParam.videoGmvValueEnd ||
                  queryParam.kolTypes
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
     
      <s-table :row-selection="rowSelection" :loading="loading"  :rangeSelection="false"	 size="small"  :scroll="{x: 2000, y: sTableHeight }"    :row-height="86"
       :columns="columns" :data-source="dataSource" :selectedRowKeys="selectedRowKeys" :pagination="pagination " :yBuff="10" :xBuff="5" :rowKey="record => record.id"   :animate-rows="false" @change="pageChange">
        <template #headerCell="{title, column}">
          <template v-if="column.key == 'account'">
            <div style="display: flex; align-items: center;">
              <span style="margin-right: 10px;">网红信息</span>
              <span style="margin-right: 10px;margin-left: 30px;">包含置顶</span>
              <a-switch v-model:checked="isPinned" />
            </div>
          </template>
        </template>
        <template #bodyCell="{text, record, index, column, key }">

          <template v-if="column.key == 'account'">
            <!-- {{record}} -->
            
                <span
                  v-if="record.isAbnormalAccount == 1 || record.abnormalCode == -419"
                  style="
                    position: fixed;
                    top: 0px;
                    left: 2px;
                    padding: 0px 4px;
                    border: 1px solid red;
                    color: red;
                    border-radius: 4px;
                    font-size: 11px;
                    white-space: normal;
                  "
                >
                  <a-icon style="font-size: 12px" type="info-circle" />
                  {{ parseCode(record.abnormalCode) }}
                </span>
                <div class="colum_img">
                
                  <div class="colum_avatarUrl">
                    <a-popover  placement="top" overlayClassName="colum_avatarUrl_tooltip">
                      <template #content>
                        <img
                          v-if="record.avatarUrl"
                          :src="getImage(record.avatarUrl)"
                            @error="handleImageError"
                          
                          style="max-width: 200px;height: 200px;"
                        />
                        <img
                          v-else
                          src="/@/assets/images/avatar.png"
                          style="width: 70px; height: 70px; margin: 0 auto"
                        />
                      </template>
                      <img
                        v-if="record.avatarUrl"
                        :src="getImage(record.avatarUrl)"
                        :class="{ 'error-img': record.isAbnormalAccount == 1 }"
                          @error="handleImageError"
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
                        src="/@/assets/images/avatar.png"
                        style="border-radius: 100%; width: 70px; height: 70px; margin: 0 auto"
                      />
                    </a-popover>
                    <div
                      style="
                           position: absolute;
                            left: 64px;
                            bottom: -10px;
                       "
                    >
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
                    <div style="position: absolute; top: -20px; transform: translateX(-3px)">
                      <a-tag
                        v-if="record.isTopStar !== 0"
                        :style="{
                          color: 'white',
                          backgroundColor: parseCelebritySegmentColor(record.isTopStar),
                          border: '1px solid ' + parseCelebritySegmentColor(record.isTopStar),
                          lineHeight: '16px',
                        }"
                      >
                      {{ parseCelebritySegment(record.isTopStar) }}
                      </a-tag>
                    </div>
                  </div>
                  <div class="colum_account">
                    <div
                      style="
                        width: 100%;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        white-space: nowrap;
                      "
                    >
                      <a
                        style="font-size: 14px; font-weight: 600; text-decoration: none"
                        @click.stop.prevent="openLink(record)"
                        @mouseenter="$event.target.style.textDecoration = 'underline'"
                        @mouseleave="$event.target.style.textDecoration = 'none'"
                      >
                        
                      <JEllipsis :value="record.account" :length="14"></JEllipsis>
                      
                      </a>
                      <span>
                        <img
                          src="@/assets/images/man.png"
                          v-show="record.gender == 0"
                          style="width: 22px; height: 22px"
                          alt=""
                        />
                        <img
                          src="@/assets/images/woman.png"
                          v-show="record.gender == 1"
                          style="width: 22px; height: 22px"
                          alt=""
                        />
                      </span>
                    
                      <JEllipsis :value="record.countryName" :length="3"> </JEllipsis>
                    </div>
                    <div style="display: flex; align-items: center; white-space: nowrap">
                      <div style="margin-right: 10px; display: flex; height: 100%">
                        <a-tooltip title="粉丝数">
                          <span
                            class="icon iconfont icon-fensishu"
                            style="font-size: 18px; margin-right: 5px"
                          ></span>
                        </a-tooltip>
                        <span style="width: 50px; display: flex; align-items: center">{{
                          record.followersNum !== null && record.followersNum !== ""
                            ? getFollower(record.followersNum)
                            : "--"
                        }}</span>
                      </div>
                      <div style="display: flex; height: 100%; margin-right: 10px">
                        <a-tooltip title="均播数">
                          <span
                           class="icon iconfont icon-bofang1"
                            style="font-size: 18px; margin-right: 5px"
                          ></span>
                        </a-tooltip>
                        <span
                          v-if="record.videoData && isEmptyObject(record.videoData)"
                          style="display: flex; align-items: center; width: 50px"
                        >
                            {{
                              record.videoData && record.videoData !== "{}"
                                ? isPinned
                                  ? jsonParseTotal(record.videoData).play_avg_num
                                    ? getFollower(jsonParseTotal(record.videoData).play_avg_num)
                                    : "--"
                                  : jsonParseNonPinned(record.videoData).play_avg_num
                                  ? getFollower(
                                      jsonParseNonPinned(record.videoData).play_avg_num
                                    )
                                  : "--"
                                : "--"
                            }}
                         
                        </span>
                        <span v-else style="display: flex; align-items: center; width: 50px">
                          --
                        </span>
                      </div>
                      <div style="display: flex; height: 100%; margin-right: 10px">
                        <a-tooltip title="互动率">
                          <span
                            class="icon iconfont icon-icon_hudong-xian"
                            style="font-size: 18px; margin-right: 5px"
                          ></span>
                        </a-tooltip>
                        <a-tooltip>
                          <template #title>
                            <div v-if="record.videoData && isEmptyObject(record.videoData) && record.videoData !== '{}' ">
                              <div>{{ isPinned ? "包含置顶" : "不包含置顶" }}</div>
                              <template v-if="record.platformType == 1">
                                <template v-if="isPinned">
                                  <div>播放数：--</div>
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
                                      getFollower(jsonParseTotal(record.videoData).play_min_num)
                                    }}
                                    -
                                    {{
                                      getFollower(jsonParseTotal(record.videoData).play_max_num)
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
                                      getFollower(jsonParseTotal(record.videoData).play_min_num)
                                    }}
                                    -
                                    {{
                                      getFollower(jsonParseTotal(record.videoData).play_max_num)
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
                                    收藏数：{{
                                      getFollower(
                                        jsonParseNonPinned(record.videoData).video_collect_count
                                      )
                                    }}
                                  </div>
                                  <div>
                                    评论数：{{
                                      getFollower(
                                        jsonParseNonPinned(record.videoData).video_comment_count
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
                            style="display: flex; align-items: center"
                            v-if="record.videoData && isEmptyObject(record.videoData) && record.videoData !== '{}' "
                          >
                            <template v-if="record.platformType == 1">
                              {{
                                isPinned
                                  ? "-"
                                  : jsonParseTotal(
                                      record.videoData
                                    ).video_engagement_rate.toFixed(0) + "%"
                              }}
                            </template>
                            <template v-else>
                              {{
                                isPinned
                                  ? jsonParseTotal(
                                      record.videoData
                                    ).video_engagement_rate.toFixed(0)
                                  : jsonParseNonPinned(
                                      record.videoData
                                    ).video_engagement_rate.toFixed(0)
                              }}%
                            </template>
                          </a>
                        </a-tooltip>
                        <span
                          style="display: flex; align-items: center; width: 50px"
                          v-if="!record.videoData && !isEmptyObject(record.videoData) && record.videoData !== '{}' "
                        >
                          --
                        </span>
                      </div>
                    </div>
                    
                  </div>
                </div>
          </template>
          <template v-if="column.key == 'privateContractInfoList'">
            <div
                  style="display: flex; flex-direction: column; gap: 4px"
                  v-if="record.privateContractInfoList && record.privateContractInfoList.length > 0"
                >
                  <div
                    style="
                      display: flex;

                      flex: 1;
                    "
                    v-for="(item, index) in record.privateContractInfoList"
                    :key="index"
                  >
                    <span> {{ item.videoTag }}</span>

                    <a style="color: #3155ed !important">:${{ item.contractAmount }}</a>
                  </div>
                </div>
                <span v-else>--</span>
            
          </template>
          <template v-if="column.key == 'privateCounselorList'">
            <div
                  class="sale-stock"
                  v-if="record.privateCounselorList.length > 0 && record.privateCounselorList"
                >
                  <a-popover placement="bottom">
                    <template #content>
                      <!-- <div style="font-size: 12px; color: #0b1019">由以下报价组成</div> -->
                      <a-table
                        class="private-counselor-table"
                        :columns="privateCounselorColumns"
                        :data-source="preprocessPrivateCounselorList(record.privateCounselorList)"
                        size="small"
                        :pagination="false"
                        :bordered="false"
                      >
                        <template #contractAmount="{ record }">
                          ${{ record.contractAmount }}
                        </template>

                        <template #contractTime="{ record }">
                          {{ parseDate(record.contractTime) }}
                        </template>
                      </a-table>
                    </template>

                    <div style="display: flex; flex-wrap: wrap; gap: 4px;flex-direction: column;">
                      <div
                        v-for="item in record.privateCounselorList.slice(0, 3)"
                        style="
                          white-space: normal;
                          word-break: break-all;
                          overflow: hidden;
                          text-overflow: ellipsis;
                          display: -webkit-box;
                          -webkit-line-clamp: 1;
                          -webkit-box-orient: vertical;
                        "
                      >
                        {{ item.contractInfo }}:<a>${{ item.contractAmount }}</a
                        >-{{ item.celebrityCounselorName }}
                      </div>
                    </div>
                  </a-popover>
                </div>
                <span v-else>--</span>
          </template>
          <template v-if="column.key == 'videoGmv'">
            <span v-if="record.videoGmv">{{ parseMedGmvRevenueRange(record) }}</span>
            <span v-else>--</span>
          </template>
          <template v-if="isShowAttributeCol(column)">
            <div
              style="display: flex; white-space: normal"
              v-if="
                record.dataList && record.dataList.length && parseCategoryText(column.key, record)
              "
            >
              <span
                style="
                  display: flex;
                  flex-wrap: wrap;
                  row-gap: 4px;
                  max-width: 100%;
                  height: 49px; /* 两行 */
                  overflow: hidden;
                "
              >
                  <a-tag
                    style="margin-right: 4px; max-height: 19px"
                    v-for="(categoryItem, index) in parseAttr(
                      parseCategoryText(column.key, record)
                    ).visibleAttars"
                    :key="index"
                  >
                    {{ categoryItem }}
                  </a-tag>
                  <a-popover placement="top">
                    <template #content>
                     <div style="max-width: 200px">
                      <a-table
                        size="small"
                        :columns="attrColumns"
                        :data-source="
                          attrData(parseCategoryText(column.key, record), column.title)
                        "
                        :pagination="false"
                        :bordered="false"
                        :scroll="{  y: 300 }"
                      >
                        <template #categoryTitle>
                          <span>{{ column.title }}</span>
                        </template>
                      </a-table>
                     </div>
                    </template>
                    <span
                      v-if="
                        parseAttr(parseCategoryText(column.key, record)).hiddenAttars.length
                      "
                    >
                      ...</span
                    >
                  </a-popover>
                
              </span>
            </div>
            <span
              v-if="
                !(
                  record.dataList &&
                  record.dataList.length &&
                  parseCategoryText(column.key, record)
                )
              "
              style="
                display: flex;
                flex-wrap: wrap;
                row-gap: 4px;
                max-width: 100%;
                height: 49px; /* 两行 */
                overflow: hidden;
              "
            ></span>
            <!--   -->
            <div style="margin-top: 4px">
              <div v-if="record.isAttributeUpdate == 0" style="height: 21px"></div>
              <a-tag
                style="cursor: pointer"
                @click="handleConfirmSocialMediaAttribute(record)"
               v-if="record.isAttributeConfirmed >= 0 && record.isAttributeUpdate != 0"
                :color="parseAttributeStatus(record.isAttributeConfirmed).description"
                >{{ parseAttributeStatus(record.isAttributeConfirmed).title }}</a-tag
              >
            </div>
          
          </template>
          <template v-if="column.key == 'personalTagNames'">
            <template v-if="record.personalTagNames">
              <div
                v-if="record.personalTagNames"
                class="personal-tag-names"
                ref="tagContainerRefs"
              >
                <a-tag
                  v-for="(item, index) in parsepersonalTagNames(record.personalTagNames)
                    .visibleTags"
                  :key="item"
                >
                  {{ item }}
                </a-tag>
                <a-popover >
                  <template #content>
                    <div style="width:160px">
                      
                      <a-table
                        size="small"
                        :scroll="{
                          x:150,
                          y:200
                        }"
                        :columns="personalTagColumns"
                        :data-source="
                          record.personalTagNames ? record.personalTagNames.split(',').map((item) => ({ name: item })) : []
                        "
                      
                        :pagination="false"
                        :bordered="false"
                      >
                        <div slot="name" slot-scope="text">
                          <a-tag>{{ text }}</a-tag>
                        </div>
                      </a-table>
                    </div>
                  </template>

                  <span
                    v-if="parsepersonalTagNames(record.personalTagNames).hiddenTags.length"
                  >
                    ...</span
                  >
                </a-popover>
              </div>
            </template>
            <!-- <span v-else style="height: 49px; display: block">
              <a-tag style="color: red; background-color: #fff"> 无标签</a-tag>
            </span> -->
            <div
              v-if="!record.personalTagNames"
              class="personal-tag-names"
              ref="tagContainerRefs"
            ></div>
            <div style="margin-top: 4px">
            
               <div
                style="height: 21px"
                v-if="record.isAttributeUpdate == 0 && record.isPersonalTagsConfirmed !== 1"
              ></div>
              <div
                style="height: 21px"
                v-if="record.isAttributeUpdate == 0 && record.isPersonalTagsConfirmed == 1"
              ></div>
              <!-- <a-tag
              
                v-if="record.isAttributeUpdate == 0 && record.isPersonalTagsConfirmed !== 1"
                style="
                  cursor: pointer;
                  background-color: #f5f5f5;
                  border-color: #f5f5f5;
                  color: rgba(0, 0, 0, 0.25);
                "
                >待确认</a-tag
              > -->
              <a-tag
                v-if="record.isAttributeUpdate != 0"
                style="cursor: pointer"
                @click="handleConfirmedPersonalTags(record)"
                :color="record.isPersonalTagsConfirmed == 1 ? 'green' : 'red'"
                >{{ record.isPersonalTagsConfirmed == 1 ? "已确认" : "待确认" }}</a-tag
              >
            </div>
          </template>
          <template v-if="column.key == 'personalTags'">
            <template v-if="record.personalTags">
                  <div
                    v-if="record.personalTags"
                    class="personal-tag-names"
                    ref="tagContainerRefs"
                  >
                    <a-tag
                      v-for="(item, index) in parsepersonalTagNames(record.personalTags)
                        .visibleTags"
                      :key="item"
                    >
                      {{ item }}
                    </a-tag>
                    <a-popover>
                      <template #content>
                      <div style="width:160px">
                        <a-table
                          :columns="personalTagColumns"
                          :data-source="
                            record.personalTags ? record.personalTags.split(',').map((item) => ({ name: item })) : []
                          "
                           :scroll="{ x: 150, y: 300 }"
                          :pagination="false"
                          :bordered="false"
                        >
                          <div slot="name" slot-scope="text">
                            <a-tag>{{ text }}</a-tag>
                          </div>
                        </a-table>
                      </div>
                      </template>

                      <span v-if="parsepersonalTagNames(record.personalTags).hiddenTags.length">
                        ...</span
                      >
                    </a-popover>
                  </div>
                  <div style="height: 25px"></div>
                </template>
          </template>
          <template v-if="column.key == 'productCooperateInfo'">
            <div
                  v-if="record.productCooperateInfo && record.productCooperateInfo.length > 0"
                  :style="{
                    gap:
                      parseProduct(record.productCooperateInfo).length <= 1 ? '0px 8px' : '8px',
                  }"
                  style="display: flex; flex-wrap: wrap; white-space: normal"
                >
                  <template
                    v-for="(item, i) in record.productCooperateInfo
                      ? parseProduct(record.productCooperateInfo).length <= 3
                        ? parseProduct(record.productCooperateInfo)
                        : parseProduct(record.productCooperateInfo).slice(0, 3)
                      : []"
                  >
                    <div style="width: 100%; display: flex">
                      <div
                        style="
                          width: calc(100% - 40px);
                          background: #f0f3fe;
                          padding: 0 4px;
                          border-radius: 4px;
                          text-align: center;
                          display: flex;
                          height: 21px;
                          line-height: 21px;
                        "
                      >
                        <span>
                          {{
                            item.celebrityCounselorName
                              ? `${item.brandName}-${item.productName}-${item.celebrityCounselorName}`
                              : `${item.brandName}-${item.productName}`
                          }}
                        </span>
                  
                      </div>
                      <a-popover>
                        <template #content>
                          <div style="max-width:250px">
                            <a-table
                              size="small"
                                :scroll="{ x:200, y: 300 }"
                              :columns="productColumns"
                              :data-source="parseProduct(record.productCooperateInfo)"
                              :pagination="false"
                            >
                              <!-- <template slot="productName" slot-scope="text, record">
                                <span >{{ record.productName }}</span>
                              </template>
                              <template slot="brandName" slot-scope="text, record">
                                <span >{{ record.brandName }}</span>
                              </template> -->
                              <!-- <template slot="productModel" slot-scope="text, record">
                              <span :text="record.productModel"></span>
                            </template> -->
                            </a-table>
                            
                          </div>
                        </template>
                        <div
                          v-if="i == 2 && parseProduct(record.productCooperateInfo).length > 3"
                          style="
                            width: 35px;

                            padding: 0 4px;
                            border-radius: 4px;
                            text-align: center;
                            margin-left: 6px;
                            height: 21px;
                            line-height: 21px;
                          "
                        >
                          ...
                        </div>
                      </a-popover>
                    </div>
                  </template>
                </div>
                <span v-else>--</span>
          </template>
          <template v-if="column.key == 'tiktokCelebrityProductList'">
            <div
                  style="display: flex; gap: 6px; white-space: normal"
                  v-if="
                    record.tiktokCelebrityProductList &&
                    record.tiktokCelebrityProductList.length > 2
                  "
                >
                  <div
                    v-for="item in record.tiktokCelebrityProductList.slice(0, 2)"
                    :key="item.id"
                  >
                    <img style="height: 70px" :src="item.coverUrl" alt="" />
                  </div>
                  <a
                    @click="handleTiktokCelebrityProductList(record)"
                    style="
                      width: 25px;
                      height: 25px;
                      background: white;
                      border-radius: 100%;
                      position: absolute;
                      right: 10px;
                      top: 50%;
                      transform: translateY(-50%);
                      text-align: center;
                      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
                    "
                  >
                    ...
                  </a>
                </div>
                <div
                  style="display: flex; gap: 6px"
                  v-else-if="
                    record.tiktokCelebrityProductList &&
                    record.tiktokCelebrityProductList.length > 0
                  "
                >
                  <div
                    v-for="item in record.tiktokCelebrityProductList.slice(0, 2)"
                    :key="item.id"
                  >
                    <a-popover>
                      <template #content>
                        <div
                          style="
                            max-width: 250px;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                          "
                        >
                          <a
                            :href="item.seoUrl"
                            target="_blank"
                            style="text-decoration: underline"
                            >{{ item.title }}</a
                          >
                        </div>
                        <div
                          style="max-width: 250px; white-space: normal; word-wrap: break-word"
                        >
                          {{ parseCategories(item.categories) }}
                        </div>
                      </template>
                      <img style="height: 70px" :src="item.coverUrl" alt="" />
                    </a-popover>
                  </div>
                </div>
                <div v-else>
                  <span>--</span>
                </div>
          </template>
          <template v-if="column.key == 'email'">
            <div>
                  <span @click="copyFn(record.celebrityTel)">{{
                    record.celebrityTel ? record.celebrityTel : "--"
                  }}</span>
                </div>
                <div style="padding-right: 20px">
                  <a @click="copyFn(record.email)">{{ record.email ? record.email : "--" }}</a>
                  <a-tooltip title="复制">
                    <a @click="handleCopy(record.email)" v-if="record.email" style="margin-left: 4px" >
                      <a-icon type="copy" />
                    </a>
                  </a-tooltip>
                </div>
          </template>
          <template v-if="column.key == 'celebrityContactEmail'">
            <div style="padding-right: 20px">
                  <span>{{
                    record.celebrityCounselorName ? record.celebrityCounselorName : "--"
                  }}</span>
                </div>
                <div style="padding-right: 20px">
                  <a   @click="copyFn(record.celebrityContactEmail)">{{
                    record.celebrityContactEmail ? record.celebrityContactEmail : "--"
                  }}</a>
                  <a-tooltip title="复制">
                    <a
                      v-if="record.celebrityContactEmail"
                      style="margin-left: 4px"
                    
                      @click="copyFn(record.celebrityContactEmail)"
                    >
                      <a-icon type="copy" />
                    </a>
                  </a-tooltip>
      
                </div>
          </template>
          <template v-if="column.key == 'kolTypes'">
            <template v-if="record.kolTypes">
              <div v-if="record.kolTypes" class="kol-types">
                <a-tag
                  v-for="(item) in parseKolTypesColumn(record.kolTypes).visibleTags"
                  :key="item"
                >
                  {{ item }}
                </a-tag>
                <a-popover>
                  <template #content>
                    <div style="max-width:250px">
                      <a-table
                        :columns="kolTypeColumns"
                        :data-source="
                          record.kolTypes.split(',').map((item) => ({ name: item }))
                        "
                        :scroll="{ x: 200, y: 300 }"
                        :pagination="false"
                        :bordered="false"
                      >
                        <template #bodyCell="{ record, column,index}">
                          <template v-if="column.key == 'name'">
                            <a-tag>{{ record.name }}</a-tag>
                          </template>
                        </template>
                      </a-table>
                      
                    </div>
                  </template>

                  <span v-if="parseKolTypesColumn(record.kolTypes).hiddenTags.length">
                    ...</span
                  >
                </a-popover>
              </div>
              <div style="height: 25px"></div>
            </template>
          
          </template>
          <template v-if="column.key == 'contractTime'">
            <div>
              {{ record.contractTime ? parseDate(record.contractTime) : "--" }}
            </div>
            <div>
              {{ record.lastCrawledTime ? parseDate(record.lastCrawledTime) : "--" }}
            </div>
          </template>
          <template v-if="column.key == 'remark'">
    
            <EllipsisTooltip :text="record.remark" :lines="2"></EllipsisTooltip>
          </template>
          <template v-if="column.key == 'action'">
            <a-tooltip title="编辑" placement="bottom">
              <a @click="handleNewEdit(record)">
               
                <FormOutlined  style="font-size: 15px" />
              </a>
            </a-tooltip>

            <a-divider type="vertical"   />
            <a-tooltip title="编辑信息" placement="bottom">
              <a @click="handleNewEditInfo(record)" v-if="hasPermission('user:edit_country_gender')" >
                <span class="icon iconfont icon-bianji2" style="font-size: 18px"></span>
              </a>
            </a-tooltip>
            <a-divider type="vertical" v-if="hasPermission('user:edit_country_gender')"  />
            <a-tooltip title="编辑历史合作" placement="bottom">
              <a @click="handleEditCollaborative(record)">
                <span class="icon iconfont icon-lishijilu" style="font-size: 17px"></span>
              </a>
            </a-tooltip>
          </template>
        </template>
      </s-table>
      <div style="display: flex; align-items: center; justify-content: space-between; position: absolute; bottom: 32px;">
      <div>
        已选择 <a>{{ selectedRowKeys.length }}</a>个
      </div>
     
    </div>
      <ExportPrivateModal ref="exportPrivateModalRef" />
      <AdjustmentConsultantModal ref="adjustmentConsultantModalRef" @ok="modalFormOk" />
      <privateInternetCelebrityManagementModal ref="privateInternetCelebrityManagementModalRef" @ok="modalFormOk"/>
      <editInfoModal ref="editInfoModalRef" @ok="modalFormOk" />
      <EditCollaborativeModal ref="editCollaborativeModalRef" @ok="modalFormOk" />
      <ConfirmedPersonalTagsModal ref="confirmedPersonalTagsModalRef" @ok="modalFormOk" />
      <ConfirmSocialMediaAttributeModal ref="confirmSocialMediaAttributeModalRef" @ok="modalFormOk" />
      <TiktokCelebrityProductListModal ref="tiktokCelebrityProductListModalRef" @ok="modalFormOk" />
  </a-card>
</template>
 <script setup name="privateInternetCelebrityManagementList">
  import {
    SearchOutlined,
    ReloadOutlined,
    DownloadOutlined,
    DownOutlined,
    TeamOutlined,
    FileSyncOutlined,
    SyncOutlined,
    SafetyCertificateOutlined,
    FormOutlined
  } from '@ant-design/icons-vue';
  import { JDictSelectTag } from '/@/components/Form';
  import { ElCascaderPanel, ElPopover } from 'element-plus'
  import { ref, h, nextTick, computed } from 'vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { defHttp } from '/@/utils/http/axios';
  import dayjs from 'dayjs';
  import { demoListApi,getCelebrityPrivateURLApi,getHistoryURLApi,syncPrivateCelebrityApi,syncProductApi,productListApi } from './storeCelebrityPrivateApi';
  import {getDictItems} from "@/api/common/api";
  import { getCommonCountryApi,queryAllCelebrityCounselorApi,getBrandListApi,getProductAttributeApi,getProductListApi,getPersonalTagsListApi,getProductCategoryListApi } from '/@/api/sys/systemCommon';
  import { useTable } from '/@/components/useSTable/useSTable';
  import JEllipsis from '/@/components/Form/src/jeecg/components/JEllipsis.vue';
  import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
  // 定义 beforeFetch 函数
  function beforeFetchFn(params) {
    if (!params.followersStartNum) delete params.followersStartNum;
    if (!params.followersEndNum) delete params.followersEndNum;
    if (!params.videoGmvValueStart) delete params.videoGmvValueStart;
    if (!params.videoGmvValueEnd) delete params.videoGmvValueEnd;
    if (!params.playAvgStartNum) delete params.playAvgStartNum;
    if (!params.playAvgEndNum) delete params.playAvgEndNum;
    
    params.isMcn = 0;
    params.platFormTypeStr =
      params.platformTypeArr && params.platformTypeArr.length > 0
        ? params.platformTypeArr.join(",")
        : null;
    params.isTop = isPinned.value ? 1 : 0;
    delete params.platformTypeArr;
    
    return params;
  }
  
  const { dataSource, pagination, loading, fetchTable,queryParam,searchQuery,selectedRowKeys,sTableHeight,pageChange,updatePagination,calcTableHeight,updateSort,copyFn } = useTable(demoListApi,0,{beforeFetch:beforeFetchFn});
  const { createMessage: $message, createConfirm } = useMessage();
  import { copyTextToClipboard } from '@/hooks/web/useCopyToClipboard';
  import { usePermission } from '/@/hooks/web/usePermission'
  const { hasPermission } = usePermission();
  import ExportPrivateModal from './modules/exportPrivateModal.vue';
  import AdjustmentConsultantModal from './modules/adjustmentConsultantModal.vue';
  import privateInternetCelebrityManagementModal from './modules/privateInternetCelebrityManagementModal.vue';
  import editInfoModal from './modules/editInfoModal.vue';
  import EditCollaborativeModal from './modules/EditCollaborativeModal.vue';
  import ConfirmedPersonalTagsModal from './modules/ConfirmedPersonalTagsModal.vue';
  import ConfirmSocialMediaAttributeModal from './modules/ConfirmSocialMediaAttributeModal.vue';
  import TiktokCelebrityProductListModal from './modules/tiktokCelebrityProductListModal.vue';
  const tiktokCelebrityProductListModalRef = ref(null);
  const exportPrivateModalRef = ref(null);
  const adjustmentConsultantModalRef = ref(null);
  const privateInternetCelebrityManagementModalRef = ref(null);
  const editInfoModalRef = ref(null);
  const editCollaborativeModalRef = ref(null);
  const confirmedPersonalTagsModalRef = ref(null);
  const confirmSocialMediaAttributeModalRef = ref(null);
  let isPinned = ref(false);
  let searchType = ref('0')
  const countrys = ref([]);
  const celebrityConsultantList = ref([]);
  const productList = ref([]);
  const brandList = ref([]);
  const productCategoryList = ref([]);
  const personalTagList = ref([]);
  const filterOptions = ref([]);
  const socialMediaCategoryList = ref([]);
  const categoryTypeList = ref([]);
  const bnormalCodeList = ref([]);
  const attributeFilterPopoverRefs = ref({});
  queryParam.value = {
    platformTypeArr:[],
    celebrityCounselorId:undefined,
  };
  const columns = ref([
    {
      title: '#',
      fixed: 'left',
      width: 50,
      align: 'center',
      customRender: ({ index }) => index + 1,
    },
    {
      title: '网红信息',
      dataIndex: 'account',
      key: 'account',
      width: 330,
      fixed: 'left',
    
    },
    {
      title: '内容报价',
      dataIndex: 'privateContractInfoList',
      key: 'privateContractInfoList',
      width: 150,
    },
    {
      title:'历史报价',
      dataIndex: 'privateCounselorList',
      key: 'privateCounselorList',
      width: 230,
    },
    {
      title: '视频GMV',
      width: 100,
      
      dataIndex: 'videoGmv',
      key: 'videoGmv',
    },
    {
      title: '个性化标签',
      width: 250,
    
      dataIndex: 'personalTagNames',
      key: 'personalTagNames',
    },
    {
      title: '原个性化标签',
      width: 230,
    
      dataIndex: 'personalTags',
      key: 'personalTags',
    },
    {
      title: '历史合作',
      width: 250,
    
      dataIndex: 'productCooperateInfo',
      key: 'productCooperateInfo',
    },
    {
      title: '带货产品',
      width: 180,
    
      dataIndex: 'tiktokCelebrityProductList',
      key: 'tiktokCelebrityProductList',
    },
    {
      title: '手机号/网红邮箱',
      width: 250,
    
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '网红顾问/建联邮箱',
      width: 250,
    
      dataIndex: 'celebrityContactEmail',
      key: 'celebrityContactEmail',
    },
    {
      title: '商家达人类型',
      width: 200,
    
      dataIndex: 'kolTypes',
      key: 'kolTypes',
    },

    {
      title: '签约/同步',
      width: 100,
    
      dataIndex: 'contractTime',
      key: 'contractTime',
    },
    {
      title: '备注',
      width: 100,
  
      dataIndex: 'remark',
      key: 'remark',
    },
    {
      title: '操作',
      width: 120,
      fixed: 'right',
      align: 'center',
      dataIndex: 'action',
      key: 'action',
      
    }
  ]);
  const kolTypeColumns = ref([
    {
      title: "#",
      dataIndex: "",
      key: "rowIndex",
      width: 30,
      customRender: function ({index}) {
        return parseInt(index) + 1;
      },
    },
    {
      title: "商家达人类型",
      dataIndex: "name",
      width: 120,
    }
  ]);
  const personalTagColumns = ref([
    {
      title: "#",
      dataIndex: "",
      key: "rowIndex",
      width: '30',
      customRender: function ({index}) {
        return parseInt(index) + 1;
      },
    },
    {
      title: "个性化标签",
      dataIndex: "name",
      width: 120,
    },
  ]);
  const privateCounselorColumns = [
  {
    title: "#",
    key: "rowIndex",
    width: 25,
    customCell: (record, rowIndex) => ({
      style: {
       
        borderBottom: "1px solid #F0F0F0",
        borderLeft: "1px solid #F0F0F0",
      },
    }),
    customRender: ({ record, index }) => {
      const data = record._tableData || [];
      const valueKey = record.contractInfo;

      const uniqueGroups = [...new Set(data.map((item) => item.contractInfo))];
      const groupIndex = uniqueGroups.indexOf(valueKey) + 1;

      const sameGroup = data.filter((item) => item.contractInfo === valueKey);
      const firstIndex = data.findIndex((item) => item.contractInfo === valueKey);

      if (index === firstIndex) {
        return {
          children: groupIndex,
          props: { rowSpan: sameGroup.length },
        };
      }

      return {
        children: "",
        props: { rowSpan: 0 },
      };
    },
  },

  {
    title: "内容",
    dataIndex: "contractInfo",
    width: 120,
    customCell: (record, rowIndex) => ({
      style: {
        borderRight: "1px solid #F0F0F0",
        borderBottom: "1px solid #F0F0F0",
      },
    }),
    customRender: ({ text, record, index }) => {
      const data = record._tableData || [];

      const sameGroup = data.filter((item) => item.contractInfo === text);
      const firstIndex = data.findIndex((item) => item.contractInfo === text);

      if (index === firstIndex) {
        return {
          children: text,
          props: { rowSpan: sameGroup.length },
        };
      }

      return {
        children: "",
        props: { rowSpan: 0 },
      };
    },
  },

  {
    title: "历史报价",
    dataIndex: "contractAmount",
    width: 100,
    slots: { customRender: "contractAmount" },
  },

  {
    title: "网红顾问",
    dataIndex: "celebrityCounselorName",
    width: 80,
  },

  {
    title: "建联日期",
    dataIndex: "contractTime",
    width: 100,
    slots: { customRender: "contractTime" },
    customCell: (record, rowIndex) => ({
      style: {
       
        borderRight: "1px solid #F0F0F0",
        // borderLeft: "1px solid #F0F0F0",
      },
    }),
  },
];
 const attrColumns = ref([
      {
        title: "#",
        dataIndex: "",
        width: "50px",
        key: "index",
        align: "center",
        customRender: function ({index}) {
          return parseInt(index) + 1;
        },
      },
      {
        slots: { title: "categoryTitle" },
        dataIndex: "category",
        key: "category",
      },
    ],
  );
  const celebritySegmentList = ref([]);
  const productColumns = ref([
    {
      title: "#",
      dataIndex: "",
      key: "rowIndex",
      width:30,
      customRender: function ({index}) {
        return parseInt(index) + 1;
      },
    },
    {
      title: "品牌名称",
      dataIndex: "brandName",
      scopedSlots: {
        customRender: "brandName",
      },
    },
    {
      title: "产品名称",
      dataIndex: "productName",
      scopedSlots: {
        customRender: "productName",
      },
    },
    {
      title: "网红顾问",
      dataIndex: "celebrityCounselorName",
      scopedSlots: {
        customRender: "celebrityCounselorName",
      },
    }
  ]);
  const rowSelection = computed(() => ({
    columnWidth: 40,
    preserveSelectedRowKeys: true, // 启用跨页勾选
    selectedRowKeys: selectedRowKeys.value, // 响应式绑定选中的行keys
    onChange: handleRowSelectionChange,
  }));
  // 更多筛选相关变量
  const mainFilterPopoverVisible = ref(false);
  const historyCooperationFilterPopoverVisible = ref(false);
  const kolTypeFilterPopoverVisible = ref(false);
  const historicalSelectionFilterPopoverVisible = ref(false);
  const playAvgFilterPopoverVisible = ref(false);
  const followersFilterPopoverVisible = ref(false);
  const gmvFilterPopoverVisible = ref(false);
  const lastPostDateFilterPopoverVisible = ref(false);
  const attributeFilterPopoverVisible = ref({});
  const filterType = ref('');
  const attributeIdsMap = ref({});
  const attributeIds = ref([]);
  const options = ref([]);
  
  // 粉丝数筛选
  const minFollowers = ref(null);
  const maxFollowers = ref(null);
  const selectedFollowersRanges = ref([]);
  
  // 均播数筛选
  const minPlayAvg = ref(null);
  const maxPlayAvg = ref(null);
  const selectedPlayAvgRanges = ref([]);
  
  const selectedGmvRanges = ref([]);
  
  // 历史合作筛选
  const historyCooperation = ref([]);
  const historyCooperationList = ref([]);
  const hesBrandId = ref(undefined);
  
  
  // 历史筛选
  const historicalSelectionList = ref([]);
  const historicalSelection = ref([]);
  const selectedProductIds = ref([]);
  
  // KOL类型
  const kolTypesList = ref([]);
  const kolTypes = ref([]);
  const kolTypeFetching = ref(false);
  const kolTypeLoading = ref(false);
  const hasMoreKolTypes = ref(true);
  const kolTypeVisible = ref(false);
  const kolTypeValue = ref('');
  const noKolType = ref(false);
  const nonExistentKolType = ref([]);
  const allKolTypes = ref([]);  // 存储所有KOL类型数据
  const tagPageSize = 50;  // 每页加载的标签数量
  const tagCurrentPage = ref(1);  // 当前标签页
  const lastKolTypeScrollTop = ref(0);  // 记录上次滚动位置
  const lastFetchId = ref(0);  // 用于防抖的请求ID
  
  // GMV筛选
  const videoGmvValueStart = ref(null);
  const videoGmvValueEnd = ref(null);
  
  // 最后发帖时间
  const videoCreateTimeLastStart = ref(null);
  const selectedLastPostDateRanges = ref([]);

  // 备份数据（用于弹窗取消恢复）
  const backupData = ref({});
  const isApplyClose = ref(false);

  // 筛选数据存储（用于切换筛选条件时保存/恢复数据）
  const filterData = ref({
    attributeSelections: {},
    gmvRange: {
      videoGmvValueStart: null,
      videoGmvValueEnd: null,
      selectedRanges: [],
    },
    followersRange: {
      minFollowers: null,
      maxFollowers: null,
      selectedRanges: [],
    },
    playAvgRange: {
      minPlayAvg: null,
      maxPlayAvg: null,
      selectedRanges: [],
    },
    lastPostDateRange: {
      videoCreateTimeLastStart: null,
      selectedRanges: [],
    },
    historicalSelection: [],
    historyCooperation: [],
    hesBrandId: undefined,
    kolTypes: [],
  });
  updatePagination({
    defaultPageSize: 50,           // 默认每页条数
    pageSize: 50,                  // 当前每页条数
    pageSizeOptions: ['50', '100', '200'],  // 每页条数选项
    current: 1,                    // 当前页码
    total: 0,                      // 总条数
    showQuickJumper: true,         // 是否显示快速跳转
    showSizeChanger: true,         // 是否显示每页条数选择器
    showTotal: (total, range) => { // 自定义显示总数
      return `${range[0]}-${range[1]} 共${total}条`
    }
 
  })
  updateSort({
    column: "contractTime",
    order: "desc",
  })
  function handleRowSelectionChange(_keys, selectedRows) {
    console.log(_keys, selectedRows);
    // 直接使用 _keys，这样可以保持跨页选择
    selectedRowKeys.value = _keys;
  }
  function parseKolTypesColumn(kolTypes) {
    if (!kolTypes || typeof kolTypes !== "string")
      return { visibleTags: [], hiddenTags: [] };
    const kolTypeArray = kolTypes.split(",");
    let totalLength = 0;
    const visibleTags = [];
    const hiddenTags = [];
    let isOverflow = false;
    for (const kolType of kolTypeArray) {
      if (!isOverflow && totalLength + kolType.length <= 20) {
        visibleTags.push(kolType);
        totalLength += kolType.length;
      } else {
        isOverflow = true;
        hiddenTags.push({ name: kolType });
      }
    }
    return { visibleTags, hiddenTags };
  }
  function isShowAttributeCol(column){

      if (categoryTypeList.value.find(item => item.id == column.key)){
        return true;
      } else {
        return false;
      }
  }
  function searchReset() {
    productList.value = [];

    for (let i = 0; i < socialMediaCategoryList.value.length; i++) {
      socialMediaCategoryList.value[i].selectedValues = [];
    }

    filterType.value = "";
    attributeIdsMap.value = {};
    attributeIds.value = [];

    // 重置粉丝数范围
    minFollowers.value = null;
    maxFollowers.value = null;
    selectedFollowersRanges.value = [];
    
    filterData.value.followersRange = {
      minFollowers: null,
      maxFollowers: null,
      selectedRanges: [],
    };
    filterData.value.playAvgRange = {
      minPlayAvg: null,
      maxPlayAvg: null,
      selectedRanges: [],
    };

    // 重置最近发布日期数据
    videoCreateTimeLastStart.value = null;
    selectedLastPostDateRanges.value = [];
    filterData.value.lastPostDateRange = {
      videoCreateTimeLastStart: null,
      selectedRanges: [],
    };

    selectedGmvRanges.value = [];
    filterData.value.gmvRange = {
      videoGmvValueStart: null,
      videoGmvValueEnd: null,
      selectedRanges: [],
    };
    videoGmvValueStart.value = null;
    videoGmvValueEnd.value = null;
    selectedPlayAvgRanges.value = [];
    filterData.value.playAvgRange = {
      minPlayAvg: null,
      maxPlayAvg: null,
      selectedRanges: [],
    };
    minPlayAvg.value = null;
    maxPlayAvg.value = null;
    selectedLastPostDateRanges.value = [];
    filterData.value.lastPostDateRange = {
      videoCreateTimeLastStart: null,
      selectedRanges: [],
    };
    videoCreateTimeLastStart.value = null;

    filterData.value.followersRange = {
      minFollowers: null,
      maxFollowers: null,
      selectedRanges: [],
    };
    minFollowers.value = null;
    maxFollowers.value = null;
    // 重置所有属性筛选数据
    filterData.value.attributeSelections = {};
    filterData.value.historicalSelection = [];
    filterData.value.historyCooperation = [];
    filterData.value.hesBrandId = undefined;
    hesBrandId.value = undefined;
    historyCooperationList.value = [];
    historyCooperation.value = [];
    kolTypes.value = [];
    queryParam.value = {
      platformTypeArr: [],
      celebrityCounselorId: undefined,
    };
    // 重置已选中的行
    selectedRowKeys.value = [];

    nextTick(() => {
      calcTableHeight();
    });
    fetchTable();
  }
  function handleTiktokCelebrityProductList(row) {
    tiktokCelebrityProductListModalRef.value.show(row.tiktokCelebrityProductList);
  }
  function handleCopy(text) {
    const success = copyTextToClipboard(text);
      if (success) {
        $message.success('复制成功！');
      } else {
        $message.error('复制失败！');
      }
      return success;
  }

  const parseCelebritySegmentColor = (text) => {
    let result = celebritySegmentList.value.filter((item) => item.value == text)[0];
    return result.description;
  }
  const parseCelebritySegment = (text) => {
    let result = celebritySegmentList.value.filter((item) => item.value == text)[0];
    return result.title;
  }
  const handleNewEdit = (row) => {
    console.log(row);
    privateInternetCelebrityManagementModalRef.value.edit(row);
  }
  const handleNewEditInfo = (row) => {
    console.log(row);
    editInfoModalRef.value.edit(row);
  }
  const handleEditCollaborative = (row) => {
    const newRecord = Object.assign(
        {},
        {
          id: row.id,
          productSelectIds: row.productSelectIds,
          productCooperateIds: row.productCooperateIds,
          personalTagIds: row.personalTagIds,
          privateCounselorList: row.privateCounselorList,
        }
      );
    editCollaborativeModalRef.value.edit(newRecord);
  }
  const handleConfirmedPersonalTags = (record) => {
    
    confirmedPersonalTagsModalRef.value.edit(record);
    confirmedPersonalTagsModalRef.value.title = `【${record.account}】个性化标签确认`;
  }
  const updateCelebrityStatus = (type) => {
    console.log(selectedRowKeys.value);
    if (selectedRowKeys.value.length == 0) {
      $message.error("请选择私有网红");
      return;
    }
    if (selectedRowKeys.value) {
      createConfirm({
        title: "提示",
        content: "确定更新状态吗？",
        onOk: async () => {
          try {
            const res = await defHttp.post({
              url: "/storeCelebrityPrivate/storeCelebrityPrivate/batchUpdateAbnormalStatus",
              data: {
                celebrityIds: selectedRowKeys.value.join(","),
                isAbnormalAccount: type,
              },
             
            },{isTransformResponse:false});
            if (res.success) {
              $message.success('更新成功');
              fetchTable();
            } else {
              $message.error('更新失败');
            }
           
          } catch (error) {
            console.error(error);
          }
        },
        onCancel() {},
      });
    }
  }
  const exportPrivateCelebrity = () => {
    if (selectedRowKeys.value.length == 0) {
      $message.warning("请选择需要导出的私有网红");
      return;
    }
    let param = { ...queryParam.value };

    param.platFormTypeStr =
      queryParam.value.platformTypeArr && queryParam.value.platformTypeArr.length > 0
        ? queryParam.value.platformTypeArr.join(",")
        : null;
    if (queryParam.value.platformTypeArr) {
      delete param.platformTypeArr;
    }

    exportPrivateModalRef.value.show(selectedRowKeys.value, pagination.total, param);
  }
  function adjustmentConsultant() {
    adjustmentConsultantModalRef.value.show();
  }
  function  parseCategories(categories) {
      // Create a copy of the array to avoid mutating the original
      const sortedCategories = [...categories].sort((a, b) => a.level - b.level);
      return sortedCategories.map((item) => item.categoryName).join(" -> ");
  }
  function parseProduct(text) {
    if (text) {
      return JSON.parse(text);
    } else {
      return [];
    }
  }
  function handleImageError(e) {
    e.target.src = new URL('@/assets/images/avatar.png', import.meta.url).href;
  }
  function preprocessPrivateCounselorList(list) {
    return (list || []).map((item) => ({
      ...item,
      _tableData: list, // 关键点：为每行绑定父数组引用
    }));
  }
  function jsonParseTotal(jsonStr) {
    const obj = JSON.parse(jsonStr).total;
    return obj;
  }
  function jsonParseNonPinned(jsonStr) {
    const obj = JSON.parse(jsonStr).non_pinned;
    return obj;
  }
  function _rowParse(_text, _record, _index, _column, _key) {
    console.log(_text, _record, _index, _column, _key);
    
  }
  function modalFormOk() {
    fetchTable();
  }
  function parseAttr(attrs) {
    if (!attrs || typeof attrs !== "string")
      return { visibleAttars: [], hiddenTags: [] };
  
    const attrArray = attrs.split(",");
    let totalLength = 0;
    const visibleAttars = [];
    const hiddenAttars = [];
    let isOverflow = false;
  
    for (const attr of attrArray) {
      if (!isOverflow && totalLength + attr.length <= 23) {
        visibleAttars.push(attr);
        totalLength += attr.length;
      } else {
        isOverflow = true;
        hiddenAttars.push({ name: attr });
      }
    }
  
    return { visibleAttars, hiddenAttars };
  }
  function  attrData(text, typeName) {
    if (!text) return [];
    return text.split(",").map((item) => ({
      category: item,
      typeName: typeName,
    }));
  }
  async function initbnormalCode(){
    const res = await getDictItems('abnormalCode');
    bnormalCodeList.value = res;
  }
   function parseCode(c) {
    let filterCode = bnormalCodeList.value.filter((item) => item.value == c);

    if (filterCode.length) {
      return filterCode[0].text;
    } else {
      return "";
    }
  }
  function parseCategoryText(id, row) {
    const filterList = row.dataList.filter((item) => item.typeId == id);
      if (filterList.length > 0) {
     
        return filterList
          .map((item) => item.list.map((category) => category.attributeName).join(", "))
          .join(", ");
      }
      return "";
  }
  function parseAttributeStatus(status) {
    const statusMap = {
      0: { title: '待生成', description: 'orange' },
      1: { title: '待确认', description: 'red' },
      2: { title: '已确认', description: 'green' },
    };
    return statusMap[status] ;
  }
  function handleConfirmSocialMediaAttribute(row) {
    if (row.isAttributeUpdate == 0) {
      return;
    }
    confirmSocialMediaAttributeModalRef.value.show(row);
    confirmSocialMediaAttributeModalRef.value.title =
      row.isAttributeConfirmed == 1
        ? `【${row.account}】社媒属性确认`
        : `【${row.account}】社媒属性修改`;
    confirmSocialMediaAttributeModalRef.value.okText =
      row.isAttributeConfirmed == 1 ? `保存` : "保存";
  }
  function addDynamicColumns() {
    // 找到视频GMV列的索引
    const videoGmvIndex = columns.value.findIndex(col => col.key === 'videoGmv');
    if (videoGmvIndex === -1) return;
    
    // 移除已存在的动态列（如果存在）
    // columns.value = columns.value.filter(col => !col.key || !col.key.startsWith('categoryType_'));
    
    // 在视频GMV列之后插入动态列
    const dynamicColumns = categoryTypeList.value.map(item => ({
      title: item.typeName,
      width: 250,
      dataIndex: item.id,
      key: item.id,
      typeId: item.id,
      typeCode: item.typeCode,
    }));
    
    columns.value.splice(videoGmvIndex + 1, 0, ...dynamicColumns);
    columns.value = [...columns.value]
    console.log(columns.value);
  }
  function parsepersonalTagNames(names) {
    // if (!names || typeof names !== "string") return { visibleTags: [], hiddenTags: [] };
    if (!names){
      return { visibleTags: [], hiddenTags: [] };
    }
    const tagArray = names.split(",");
    let totalLength = 0;
    const visibleTags = [];
    const hiddenTags = [];
    let isOverflow = false;
  
    for (const tag of tagArray) {
      if (!isOverflow && totalLength + tag.length <= 20) {
        visibleTags.push(tag);
        totalLength += tag.length;
      } else {
        isOverflow = true;
        hiddenTags.push({ name: tag });
      }
    }
  
    return { visibleTags, hiddenTags };
  }
  function parseDate(date) {
    if (date) {
      return dayjs(date).format("YY/MM/DD");
    } else {
      return "-";
    }
  }
  function  isEmptyObject(obj) {
    if (obj) {
      return Object.keys(obj).length !== 0;
    } else {
      return false;
    }
  }
  function   parseMedGmvRevenueRange(record) {
    if (JSON.parse(record.videoGmv).is_authorized) {
      return JSON.parse(record.videoGmv).value.format;
    } else {
      return JSON.parse(record.medGmvRevenueRange).value;
    }
  }
  function getImage(url) {
    return url.includes("https") || url.includes("http")
      ? url
      : 'https://gemstone-image.kolbox.com/avatar/' + url;
  }
  function  openLink(record) {
    if (record.platformType == "1") {
      open(`https://www.youtube.com/@${record.account}/videos`);
    } else if (record.platformType == "2") {
      open(`https://www.tiktok.com/@${record.account}`);
    } else if (record.platformType == "0") {
      open(`https://www.instagram.com/${record.account}/reels`);
    }
  }
  function  getFollower(num) {
    if (num >= 1000 && num < 1000000) {
      const kValue = (num / 1000).toFixed(0);
      return `${kValue}K`;
    } else if (num >= 1000000) {
      const mValue = (num / 1000000).toFixed(0);
      return `${mValue}M`;
    } else if (num > 0 && num < 1000) {
      return num;
    } else {
      return "--";
    }
  }
  async function getCelebrityPrivateURL() {
    const res = await getCelebrityPrivateURLApi({});
    if (res.success) {
      if (res.result.spreadSheetUrl) {
        window.open(res.result.spreadSheetUrl, "_blank");
      } else {
        $message.error("未拥有权限，请联系管理员");
      }
    } else {
      $message.error(res.message);
    }
  }
  async function getHistoryURL() {
    const res = await getHistoryURLApi({});
    if (res.success) {
      if (res.result.spreadSheetUrl) {
        window.open(res.result.spreadSheetUrl, "_blank");
      } else {
        $message.error("未拥有权限，请联系管理员");
      }
    } else {
      $message.error(res.message);
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
  function onBrandChange(value) {
    // this.queryParam.categoryIds = undefined;
    queryParam.value.productId = undefined;
    productList.value = [];
    if (value) {
      initProduct();
    } else {
      onProductChange(undefined);
    }
  }
  async function onProductChange(value) {
    console.log("产品变更:", value);

  }
  async function initProduct() {
    const res = await productListApi({
      brandId: queryParam.value.brandId,
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
  async function syncPrivateCelebrity() {
    const res = await syncPrivateCelebrityApi({});
    if (res.success) {
      $message.success("同步成功");
    } else {
      $message.error(res.message);
    }
  }
  async function syncProduct() {
    const res = await syncProductApi({});
    if (res.success) {
      $message.success("同步成功");
    } else {
      $message.error(res.message);
    }
  }
  async function getCommonCountry() {
    const res = await getCommonCountryApi({});
    countrys.value = Array.isArray(res?.result) ? res.result : Array.isArray(res) ? res : [];
  }
  async function queryAllCelebrityCounselor() {
    const res = await queryAllCelebrityCounselorApi({});
    celebrityConsultantList.value = Array.isArray(res)
      ? res
      : [];
  }
  async function getBrandList() {
    const res = await getBrandListApi({});
    brandList.value = Array.isArray(res?.result) ? res.result : Array.isArray(res) ? res : [];
  }
  async function getPersonalTagsList() {
    const res = await getPersonalTagsListApi({});
    personalTagList.value = Array.isArray(res?.result)
      ? res.result
      : Array.isArray(res)
        ? res
        : [];
  }
  // 更多筛选相关方法
  function selectFilterOther(type) {
    filterType.value = type;
    // if (type === 'historyCooperation') {
    //   onHistoryCooperationPopoverShow();
    // } else if (type === 'historicalSelection') {
    //   onHistoricalSelectionPopoverShow();
    // }
  }

    function selectFilter(typeId, typeName) {
      // 保存当前筛选条件的数据
      saveCurrentFilterData();

      // 切换到新的筛选条件
      filterType.value = typeId;

      // 获取产品属性数据
      // getProductAttribute(typeId, typeName);

      // 恢复该筛选条件的已保存数据
      restoreFilterData(typeId);
    }

    // 保存当前筛选条件的数据
    function saveCurrentFilterData() {
      if (filterType.value) {
        if (filterType.value === 'followersNum') {
          // 保存粉丝数范围数据
          filterData.value.followersRange = {
            minFollowers: minFollowers.value || null,
            maxFollowers: maxFollowers.value || null,
            selectedRanges: [...(selectedFollowersRanges.value || [])],
          };
        } else if (filterType.value === 'playAvg') {
          filterData.value.playAvgRange = {
            minPlayAvg: minPlayAvg.value || null,
            maxPlayAvg: maxPlayAvg.value || null,
            selectedRanges: [...(selectedPlayAvgRanges.value || [])],
          };
        } else if (filterType.value === 'historicalSelection') {
          filterData.value.historicalSelection = [...(historicalSelection.value || [])];
        } else if (filterType.value === 'historyCooperation') {
          filterData.value.historyCooperation = [...(historyCooperation.value || [])];
          filterData.value.hesBrandId = hesBrandId.value || undefined;
        } else if (filterType.value === 'kolTypes') {
          filterData.value.kolTypes = [...(kolTypes.value || [])];
        } else if (filterType.value === 'lastPostDate') {
          filterData.value.lastPostDateRange = {
            videoCreateTimeLastStart: videoCreateTimeLastStart.value || null,
            selectedRanges: [...(selectedLastPostDateRanges.value || [])],
          };
        } else if (filterType.value === 'gmv') {
          filterData.value.gmvRange = {
            videoGmvValueStart: videoGmvValueStart.value || null,
            videoGmvValueEnd: videoGmvValueEnd.value || null,
            selectedRanges: [...(selectedGmvRanges.value || [])],
          };
        } else {
          // 保存属性选择数据到attributeIdsMap
          if (attributeIdsMap.value[filterType.value]) {
            attributeIdsMap.value[filterType.value] = [...(attributeIdsMap.value[filterType.value] || [])];
          }
        }
      }
    }
    
    // 恢复筛选条件的数据
    function restoreFilterData(filterTypeValue) {
      if (filterTypeValue === 'followersNum') {
        // 恢复粉丝数范围数据
        const data = filterData.value.followersRange || {};
        minFollowers.value = data.minFollowers || null;
        maxFollowers.value = data.maxFollowers || null;
        selectedFollowersRanges.value = data.selectedRanges || [];
      } else if (filterTypeValue === 'playAvg') {
        // 恢复均播数范围数据
        const data = filterData.value.playAvgRange || {};
        minPlayAvg.value = data.minPlayAvg || null;
        maxPlayAvg.value = data.maxPlayAvg || null;
        selectedPlayAvgRanges.value = data.selectedRanges || [];
      } else if (filterTypeValue === 'historicalSelection') {
        // 恢复历史选中数据
        const data = filterData.value.historicalSelection || [];
        historicalSelection.value = [...data];
      } else if (filterTypeValue === 'historyCooperation') {
        // 恢复历史合作数据
        const data = filterData.value.historyCooperation || [];
        historyCooperation.value = [...data];
        hesBrandId.value = filterData.value.hesBrandId || undefined;
      } else if (filterTypeValue === 'kolTypes') {
        // 恢复商家达人类型数据
        const data = filterData.value.kolTypes || [];
        kolTypes.value = [...data];
      } else if (filterTypeValue === 'lastPostDate') {
        // 恢复最近发布日期数据
        const data = filterData.value.lastPostDateRange || {};
        videoCreateTimeLastStart.value = data.videoCreateTimeLastStart || null;
        selectedLastPostDateRanges.value = data.selectedRanges || [];
      } else if (filterTypeValue === 'gmv') {
        // 恢复GMV数据
        const data = filterData.value.gmvRange || {};
        videoGmvValueStart.value = data.videoGmvValueStart || null;
        videoGmvValueEnd.value = data.videoGmvValueEnd || null;
        selectedGmvRanges.value = data.selectedRanges || [];
      } else {
        // 恢复属性选择数据，通过attributeIdsMap自动获取
      }
    }
  
  function _rowClassNameFn(_record, _index) {
    // 预留函数
  }

  async function getProductAttribute(typeId, typeName) {
    try {
      const res = await getProductAttributeApi({
        id: typeId,
        typeName: typeName,
      });
      if (res && Array.isArray(res)) {
        options.value = res;
      }
    } catch (error) {
      console.error('获取产品属性失败:', error);
    }
  }

  function onAttributeChange(value) {
    console.log('属性变更:', value);
    const allSelectedIds = Object.values(attributeIdsMap.value).flat();
    attributeIds.value = allSelectedIds;
  }

  function selectFollowersRange(range) {
    // const index = selectedFollowersRanges.value.indexOf(range);
    selectedFollowersRanges.value = [range];
    // if (index > -1) {
    //   selectedFollowersRanges.value.splice(index, 1);
    // } else {
    //   selectedFollowersRanges.value.push(range);
    // }
    calculateFollowersRange();
  }

  function calculateFollowersRange() {
    if (selectedFollowersRanges.value.length === 0) {
      minFollowers.value = null;
      maxFollowers.value = null;
      return;
    }

    const ranges = {
      '1K-10K': { min: 1, max: 10 },
      '10K-20K': { min: 10, max: 20 },
      '20K-50K': { min: 20, max: 50 },
      '50K-100K': { min: 50, max: 100 },
      '100K+': { min: 100, max: 99999 }
    };

    const selectedRanges = selectedFollowersRanges.value.map(r => ranges[r]);
    minFollowers.value = Math.min(...selectedRanges.map(r => r.min));
    maxFollowers.value = Math.max(...selectedRanges.map(r => r.max));
  }

  function onFollowersInputChange() {
    selectedFollowersRanges.value = [];
  }

  function selectPlayAvgRange(range) {
    selectedPlayAvgRanges.value = [range];
    
    calculatePlayAvgRange();
  }

  function calculatePlayAvgRange() {
    if (selectedPlayAvgRanges.value.length === 0) {
      minPlayAvg.value = null;
      maxPlayAvg.value = null;
      return;
    }

    const ranges = {
      '0-10K': { min: 0, max: 10 },
      '10K-30K': { min: 10, max: 30 },
      '30K-100K': { min: 30, max: 100 },
      '100K-300K': { min: 100, max: 300 },
      '300K+': { min: 300, max: 99999 }
    };

    const selectedRanges = selectedPlayAvgRanges.value.map(r => ranges[r]);
    minPlayAvg.value = Math.min(...selectedRanges.map(r => r.min));
    maxPlayAvg.value = Math.max(...selectedRanges.map(r => r.max));
  }

  function onPlayAvgInputChange() {
    selectedPlayAvgRanges.value = [];
  }

  function selectGmvRange(range) {
    selectedGmvRanges.value = [range];

    calculateGmvRange();
  }

  function calculateGmvRange() {
    if (selectedGmvRanges.value.length === 0) {
      videoGmvValueStart.value = null;
      videoGmvValueEnd.value = null;
      return;
    }

    const ranges = {
      '0-5K': { min: 0, max: 5 },
      '5K-10K': { min: 5, max: 10 },
      '10K+': { min: 10, max: 99999 }
    };

    const selectedRanges = selectedGmvRanges.value.map(r => ranges[r]);
    videoGmvValueStart.value = Math.min(...selectedRanges.map(r => r.min));
    videoGmvValueEnd.value = Math.max(...selectedRanges.map(r => r.max));
  }

  function onGmvInputChange() {
    selectedGmvRanges.value = [];
  }

  function onHistoryCooperationPopoverShow() {
    historyCooperationFilterPopoverVisible.value = true;
    // 初始化历史合作数据
    // if (historyCooperationList.value.length === 0) {
    //   initHistoryCooperationList();
    // }
    onPopoverShow();
  }

  function onHistoryCooperationPopoverHide() {
    // 隐藏时的处理
    historyCooperationFilterPopoverVisible.value = false;
  
    onPopoverHide()
  }



  async function initHistoryCooperationList() {
    try {
      const res = await getProductListApi({
        brandId: hesBrandId.value
      });
      if (res && Array.isArray(res)) {
        historyCooperationList.value = res.map(item => ({
          id: item.id,
          text: item.productName
        }));
      }
    } catch (error) {
      console.error('获取历史合作列表失败:', error);
    }
  }

  async function initHistoricalSelectionList() {
    try {
      const res = await getProductListApi({});
      if (res && Array.isArray(res)) {
        historicalSelectionList.value = res;
      }
    } catch (error) {
      console.error('获取历史筛选列表失败:', error);
    }
  }

  function onHistoricalBrandChange(value) {
    hesBrandId.value = value;
    historyCooperation.value = [];
    historyCooperationList.value = [];
    if (value) {
      initHistoryCooperationList();
    }
  }

  function historyCooperationChange(value) {
    console.log('历史合作变更:', value);
  }

  function onCategoryChange(_value) {
    console.log('分类变更:', _value);
  }

  // Popover 显示隐藏事件
  function onMainPopoverShow() {
    mainFilterPopoverVisible.value = true;

    // 第一次打开时默认选中左侧菜单第一个选项
    if (!filterType.value && filterOptions.value.length > 0) {
      // 如果有动态属性类型，选中第一个
      const firstOption = filterOptions.value[0];
      filterType.value = firstOption.typeId;
      // 加载对应的数据
      options.value = processTreeData([...firstOption.data]);
      // 恢复该筛选条件的已保存数据
      restoreFilterData(firstOption.typeId);
    } else if (!filterType.value) {
      // 如果没有动态属性类型，默认选中粉丝数
      filterType.value = 'followersNum';
      // 恢复粉丝数数据
      restoreFilterData('followersNum');
    }

    onPopoverShow(); // 调用原有的备份逻辑
  }

  function onMainPopoverHide() {
    mainFilterPopoverVisible.value = false;
    // 重置 filterType 和 options，确保下次打开时能重新应用默认选择逻辑
    filterType.value = '';
    options.value = [];
    // 重置商家达人类型相关状态
    kolTypeVisible.value = false;
    noKolType.value = false;
    nonExistentKolType.value = [];
    kolTypeValue.value = '';
    
    onPopoverHide(); // 调用原有的恢复逻辑
  }

  // popover显示时备份当前数据
  function onPopoverShow() {
    isApplyClose.value = false;
    // 备份当前所有数据
    backupData.value = {
      attributeIdsMap: JSON.parse(JSON.stringify(attributeIdsMap.value)),
      filterData: JSON.parse(JSON.stringify(filterData.value)),
      minFollowers: minFollowers.value,
      maxFollowers: maxFollowers.value,
      selectedFollowersRanges: [...(selectedFollowersRanges.value || [])],
      minPlayAvg: minPlayAvg.value,
      maxPlayAvg: maxPlayAvg.value,
      selectedPlayAvgRanges: [...(selectedPlayAvgRanges.value || [])],
      historicalSelection: [...(historicalSelection.value || [])],
      historyCooperation: [...(historyCooperation.value || [])],
      hesBrandId: hesBrandId.value,
      kolTypes: [...(kolTypes.value || [])],
      videoGmvValueStart: videoGmvValueStart.value,
      videoGmvValueEnd: videoGmvValueEnd.value,
      selectedGmvRanges: [...(selectedGmvRanges.value || [])],
      videoCreateTimeLastStart: videoCreateTimeLastStart.value,
      selectedLastPostDateRanges: [...(selectedLastPostDateRanges.value || [])],
    };
  }

  // popover隐藏时检查是否需要恢复数据
  function onPopoverHide() {
    // 如果不是通过应用按钮关闭，则恢复备份数据
    if (!isApplyClose.value && backupData.value.attributeIdsMap) {
      attributeIdsMap.value = JSON.parse(JSON.stringify(backupData.value.attributeIdsMap));
      filterData.value = JSON.parse(JSON.stringify(backupData.value.filterData));
      minFollowers.value = backupData.value.minFollowers;
      maxFollowers.value = backupData.value.maxFollowers;
      selectedFollowersRanges.value = [...(backupData.value.selectedFollowersRanges || [])];
      minPlayAvg.value = backupData.value.minPlayAvg;
      maxPlayAvg.value = backupData.value.maxPlayAvg;
      selectedPlayAvgRanges.value = [...(backupData.value.selectedPlayAvgRanges || [])];
      historicalSelection.value = [...(backupData.value.historicalSelection || [])];
      historyCooperation.value = [...(backupData.value.historyCooperation || [])];
      hesBrandId.value = backupData.value.hesBrandId;
      kolTypes.value = [...(backupData.value.kolTypes || [])];
      videoGmvValueStart.value = backupData.value.videoGmvValueStart;
      videoGmvValueEnd.value = backupData.value.videoGmvValueEnd;
      selectedGmvRanges.value = [...(backupData.value.selectedGmvRanges || [])];
      videoCreateTimeLastStart.value = backupData.value.videoCreateTimeLastStart;
      selectedLastPostDateRanges.value = [...(backupData.value.selectedLastPostDateRanges || [])];
    }
  }

  function onKolTypePopoverShow() {
    kolTypeFilterPopoverVisible.value = true;
    onPopoverShow();
  }

  function onKolTypePopoverHide() {
    kolTypeFilterPopoverVisible.value = false;
    // 重置商家达人类型相关状态
    kolTypeVisible.value = false;
    noKolType.value = false;
    nonExistentKolType.value = [];
    kolTypeValue.value = '';
    onPopoverHide();
  }

  function onHistoricalSelectionPopoverShow() {
    historicalSelectionFilterPopoverVisible.value = true;
    if (historicalSelectionList.value.length === 0) {
      initHistoricalSelectionList();
    }
    onPopoverShow();
  }

  function onHistoricalSelectionPopoverHide() {
    historicalSelectionFilterPopoverVisible.value = false;
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

  function onFollowersPopoverShow() {
    followersFilterPopoverVisible.value = true;
    onPopoverShow();
  }

  function onFollowersPopoverHide() {
    followersFilterPopoverVisible.value = false;
    onPopoverHide();
  }

  function onGmvPopoverShow() {
    gmvFilterPopoverVisible.value = true;
    onPopoverShow();
  }

  function onGmvPopoverHide() {
    gmvFilterPopoverVisible.value = false;
    onPopoverHide();
  }

  function onLastPostDatePopoverShow() {
    lastPostDateFilterPopoverVisible.value = true;
    onPopoverShow();
  }

  function onLastPostDatePopoverHide() {
    lastPostDateFilterPopoverVisible.value = false;
    onPopoverHide();
  }

  function onAttributePopoverShow(typeId) {
    attributeFilterPopoverVisible.value[typeId] = true;
    onPopoverShow();
  }

  function onAttributePopoverHide(typeId) {
    attributeFilterPopoverVisible.value[typeId] = false;
    onPopoverHide();
  }

  // 清除筛选数据
  function clearFilterData(event, type) {
    event.stopPropagation();
    switch (type) {
      case 'historyCooperation':
        historyCooperation.value = [];
        delete queryParam.value.cooperatedProductIds;
        break;
      case 'kolTypes':
        kolTypes.value = [];
        delete queryParam.value.kolTypes;
        break;
      case 'historicalSelection':
        historicalSelection.value = [];
        selectedProductIds.value = [];
        delete queryParam.value.selectedProductIds;
        break;
      case 'playAvg':
        minPlayAvg.value = null;
        maxPlayAvg.value = null;
        selectedPlayAvgRanges.value = [];
        delete queryParam.value.playAvgStartNum;
        delete queryParam.value.playAvgEndNum;
        break;
      case 'followers':
        minFollowers.value = null;
        maxFollowers.value = null;
        selectedFollowersRanges.value = [];
        delete queryParam.value.followersStartNum;
        delete queryParam.value.followersEndNum;
        break;
      case 'gmv':
        videoGmvValueStart.value = null;
        videoGmvValueEnd.value = null;
        selectedGmvRanges.value = [];
        delete queryParam.value.videoGmvValueStart;
        delete queryParam.value.videoGmvValueEnd;
        break;
      case 'lastPostDate':
        videoCreateTimeLastStart.value = null;
        selectedLastPostDateRanges.value = [];
        delete queryParam.value.videoCreateTimeLastStart;
        break;
    }
    fetchTable();
    nextTick(() => {
      calcTableHeight();
    });
  }

  function clearAllFilters() {
    // 清除所有筛选条件
    historyCooperation.value = [];
    kolTypes.value = [];
    historicalSelection.value = [];
    selectedProductIds.value = [];
    minPlayAvg.value = null;
    maxPlayAvg.value = null;
    selectedPlayAvgRanges.value = [];
    minFollowers.value = null;
    maxFollowers.value = null;
    selectedFollowersRanges.value = [];
    videoGmvValueStart.value = null;
    videoGmvValueEnd.value = null;
    selectedGmvRanges.value = [];
    videoCreateTimeLastStart.value = null;
    selectedLastPostDateRanges.value = [];
    attributeIdsMap.value = {};
    attributeIds.value = [];
    selectedRowKeys.value = [];
    delete queryParam.value.cooperatedProductIds;
    delete queryParam.value.kolTypes;
    delete queryParam.value.selectedProductIds;
    delete queryParam.value.playAvgStartNum;
    delete queryParam.value.playAvgEndNum;
    delete queryParam.value.followersStartNum;
    delete queryParam.value.followersEndNum;
    delete queryParam.value.videoGmvValueStart;
    delete queryParam.value.videoGmvValueEnd;
    delete queryParam.value.videoCreateTimeLastStart;
    delete queryParam.value.attributeIds;

    fetchTable();
    nextTick(() => {
      calcTableHeight();
    });
  }

  function clearAttributeFilter(event, typeId) {
    event.stopPropagation();
    delete attributeIdsMap.value[typeId];
    const allSelectedIds = Object.values(attributeIdsMap.value).flat();
    attributeIds.value = allSelectedIds;
    if (allSelectedIds.length > 0) {
      queryParam.value.attributeIds = allSelectedIds.join(',');
    } else {
      delete queryParam.value.attributeIds;
    }
    fetchTable();
  }

  function isShowAttribute(data) {
    if (!queryParam.value.attributeIds || !data) return false;
    const attributeIdArray = queryParam.value.attributeIds.split(',');
    const dataIds = getAllAttributeIds(data);
    return attributeIdArray.some(id => dataIds.includes(id));
  }

  function getAllAttributeIds(data) {
    let ids = [];
    if (!data || !Array.isArray(data)) return ids;
    data.forEach(item => {
      if (item.id) ids.push(item.id.toString());
      if (item.children && item.children.length > 0) {
        ids = ids.concat(getAllAttributeIds(item.children));
      }
    });
    return ids;
  }

  function parseAttributeIds(attributeIds, data) {
    if (!attributeIds || !data) return '';
    const attributeIdArray = attributeIds.split(',');
    const names = [];
    attributeIdArray.forEach(id => {
      const attr = findAttributeById(data, id);
      if (attr) names.push(attr.attributeName);
    });
    return names.slice(0, 3).join(',') + (names.length > 3 ? '...' : '');
  }

  function findAttributeById(data, id) {
    if (!data || !Array.isArray(data)) return null;
    for (const item of data) {
      if (item.id && item.id.toString() === id) return item;
      if (item.children && item.children.length > 0) {
        const found = findAttributeById(item.children, id);
        if (found) return found;
      }
    }
    return null;
  }

  function parseHistoryCooperationProductIds(ids) {
    if (!ids) return '';
    const idArray = ids.split(',');
    const names = [];
    idArray.forEach(id => {
      const product = historyCooperationList.value.find(p => p.id.toString() === id);
      if (product) names.push(product.text);
    });
    return names.slice(0, 2).join(',') + (names.length > 2 ? '...' : '');
  }

  function parseHistoricalSelectionProductIds(ids) {
    if (!ids) return '';
    const idArray = ids.split(',');
    const names = [];
    idArray.forEach(id => {
      const product = historicalSelectionList.value.find(p => p.id.toString() === id);
      if (product) names.push(product.text);
    });
    return names.slice(0, 2).join(',') + (names.length > 2 ? '...' : '');
  }

  function parseKolTypes(types) {
    if (!types) return '';
    const typeArray = types.split(',');
    return typeArray.slice(0, 2).join(',') + (typeArray.length > 2 ? '...' : '');
  }

  // KOL类型相关方法
  // 获取商家达人类型列表
  async function fetchKolTypes(value) {
    if (value) {
      lastFetchId.value += 1;
      const fetchId = lastFetchId.value;

      // 重置虚拟滚动状态
      resetKolTypePagination();

      kolTypeFetching.value = true;

      try {
        const res = await defHttp.get({
          url: '/history/kolHistoryKolType/listAll',
          params: { kolType: value }
        });

        // 检查是否是最新的请求
        if (fetchId !== lastFetchId.value) {
          return;
        }

        if (loading.value) {
          kolTypeFetching.value = false;
          return;
        }

        // 存储所有标签数据
        allKolTypes.value = res || [];

        // 初始化虚拟滚动
        initKolTypePagination();

        kolTypeFetching.value = false;
      } catch (error) {
        console.error('获取KOL类型失败:', error);
        kolTypeFetching.value = false;
      }
    } else {
      kolTypesList.value = [];
    }
  }

  // 初始化KOL类型分页
  function initKolTypePagination() {
    if (!allKolTypes.value || !Array.isArray(allKolTypes.value)) {
      kolTypesList.value = [];
      hasMoreKolTypes.value = false;
      return;
    }

    if (allKolTypes.value.length === 0) {
      kolTypesList.value = [];
      hasMoreKolTypes.value = false;
      return;
    }

    // 加载第一页数据
    const firstPageTags = allKolTypes.value.slice(0, tagPageSize);
    kolTypesList.value = firstPageTags;

    // 检查是否还有更多数据
    hasMoreKolTypes.value = allKolTypes.value.length > tagPageSize;
    tagCurrentPage.value = 1;
  }

  // 重置KOL类型分页状态
  function resetKolTypePagination() {
    tagCurrentPage.value = 1;
    kolTypesList.value = [];
    hasMoreKolTypes.value = true;
    kolTypeLoading.value = false;
    lastKolTypeScrollTop.value = 0;
  }

  // 加载更多KOL类型数据
  async function loadMoreKolTypes() {
    if (kolTypeLoading.value || !hasMoreKolTypes.value) return;

    if (!allKolTypes.value || !Array.isArray(allKolTypes.value)) {
      return;
    }

    kolTypeLoading.value = true;

    try {
      await nextTick();

      const startIndex = tagCurrentPage.value * tagPageSize;
      const endIndex = startIndex + tagPageSize;

      const newTags = allKolTypes.value.slice(startIndex, endIndex);

      if (newTags.length > 0) {
        kolTypesList.value = [...kolTypesList.value, ...newTags];
        tagCurrentPage.value++;
        hasMoreKolTypes.value = endIndex < allKolTypes.value.length;
      } else {
        hasMoreKolTypes.value = false;
      }
    } catch (error) {
      console.error('加载更多KOL类型失败:', error);
      hasMoreKolTypes.value = false;
    } finally {
      kolTypeLoading.value = false;
    }
  }

  // 商家达人类型变更
  function kolTypeChange(value) {
    console.log('KOL类型变更:', value);
    kolTypes.value = value;
    kolTypeFetching.value = false;
  }

  // KOL类型弹窗滚动事件
  function kolTypePopupScroll(e) {
    const { target } = e;
    const { scrollTop, scrollHeight, clientHeight } = target;

    // 记录滚动方向
    const isScrollingDown = scrollTop > (lastKolTypeScrollTop.value || 0);
    lastKolTypeScrollTop.value = scrollTop;

    // 只在向下滚动且接近底部时加载更多
    if (isScrollingDown && !kolTypeLoading.value && hasMoreKolTypes.value) {
      const scrollBottom = scrollTop + clientHeight;
      const isNearBottom = scrollHeight - scrollBottom < 100;

      if (isNearBottom) {
        loadMoreKolTypes();
      }
    }
  }

  // 精确搜索KOL类型
  async function kolTypeSearch(value) {
    if (!value) return;

    let allKolType = value.trim().split('\n');
    const arr = [];
    
    for (let index = 0; index < allKolType.length; index++) {
      const el = allKolType[index];
      arr.push(el.trim());
    }
    
    allKolType = [...arr].filter(item => item); // 过滤空字符串

    try {
      const res = await defHttp.get({
        url: '/history/kolHistoryKolType/listAll',
        params: { kolTypes: allKolType.join('|') }
      });

      const filterKolTypes = res.map((item) => item.kolType);
      nonExistentKolType.value = allKolType.filter((item) => {
        return !filterKolTypes.includes(item);
      });

      if (nonExistentKolType.value.length > 0) {
        noKolType.value = true;
      } else {
        kolTypes.value = allKolType;
        kolTypeVisible.value = false;
        noKolType.value = false;
      }
    } catch (error) {
      console.error('搜索KOL类型失败:', error);
    }
  }

  // 关闭KOL类型编辑
  function closeKolTypeEdit() {
    kolTypeVisible.value = false;
    clearKolType();
    noKolType.value = false;
  }

  // 清空KOL类型
  function clearKolType() {
    kolTypeValue.value = '';
    noKolType.value = false;
    nonExistentKolType.value = [];
  }

  // 最后发帖时间相关方法
  function selectLastPostDateRange(range) {
    const index = selectedLastPostDateRanges.value.indexOf(range);
    if (index > -1) {
      selectedLastPostDateRanges.value.splice(index, 1);
      videoCreateTimeLastStart.value = null;
    } else {
      selectedLastPostDateRanges.value = [range];
      const today = dayjs();
      switch (range) {
        case '近14天':
          videoCreateTimeLastStart.value = today.subtract(14, 'day');
          break;
        case '近30天':
          videoCreateTimeLastStart.value = today.subtract(30, 'day');
          break;
        case '近60天':
          videoCreateTimeLastStart.value = today.subtract(60, 'day');
          break;
        case '近90天':
          videoCreateTimeLastStart.value = today.subtract(90, 'day');
          break;
      }
    }
  }

  function onLastPostDateChange(date, dateString) {
    videoCreateTimeLastStart.value = dateString;
    selectedLastPostDateRanges.value = [];
  }

  // 计数方法
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

  function getGmvCount() {
    // 如果有预设选择，返回预设选择数量
    if (selectedGmvRanges.value.length > 0) {
      return selectedGmvRanges.value.length;
    }
    // 如果有自定义输入，返回1
    if (videoGmvValueStart.value || videoGmvValueEnd.value) {
      return 1;
    }
    return 0;
  }

  function getPlayAvgCount() {
    if (selectedPlayAvgRanges.value.length > 0) {
      return selectedPlayAvgRanges.value.length;
    }
    // 如果有自定义输入，返回1
    if (minPlayAvg.value || maxPlayAvg.value) {
      return 1;
    }
    return 0;
  }

  function getLastPostDateCount() {
    // 如果有预设选择，返回预设选择数量
    if (selectedLastPostDateRanges.value.length > 0) {
      return selectedLastPostDateRanges.value.length;
    }
    // 如果有自定义日期输入，返回1
    if (videoCreateTimeLastStart.value) {
      return 1;
    }
    return 0;
  }

  function getHistoryCooperationCount() {
    if (historyCooperation.value && historyCooperation.value.length > 0) {
      return historyCooperation.value.length;
    }
    return 0;
  }

  function _getHistoricalSelectionCount() {
    if (historicalSelection.value && historicalSelection.value.length > 0) {
      return historicalSelection.value.length;
    }
    return 0;
  }

  function getKolTypesCount() {
    const kolTypesData = kolTypes.value || [];
    if (kolTypesData && kolTypesData.length > 0) {
      return kolTypesData.length;
    }
    return 0;
  }

  function getAttributeCount(typeId) {
    if (attributeIdsMap.value[typeId]) {
      return attributeIdsMap.value[typeId].length;
    }
    return 0;
  }

  function applyAllFilters(isSearch) {
    // 标记为通过应用按钮关闭
    isApplyClose.value = true;

    // 先保存当前筛选条件的数据
    saveCurrentFilterData();

    // 应用所有筛选条件
    const allSelectedIds = Object.values(attributeIdsMap.value).flat();
    attributeIds.value = allSelectedIds;

    // 设置粉丝数范围
    if (minFollowers.value !== null) {
      queryParam.value.followersStartNum = minFollowers.value * 1000;
    } else {
      delete queryParam.value.followersStartNum;
    }
    if (maxFollowers.value !== null) {
      queryParam.value.followersEndNum = maxFollowers.value * 1000;
    } else {
      delete queryParam.value.followersEndNum;
    }

    // 设置均播数范围
    if (minPlayAvg.value !== null) {
      queryParam.value.playAvgStartNum = minPlayAvg.value * 1000;
    } else {
      delete queryParam.value.playAvgStartNum;
    }
    if (maxPlayAvg.value !== null) {
      queryParam.value.playAvgEndNum = maxPlayAvg.value * 1000;
    } else {
      delete queryParam.value.playAvgEndNum;
    }

    // 设置GMV范围
    if (videoGmvValueStart.value !== null) {
      queryParam.value.videoGmvValueStart = videoGmvValueStart.value * 1000;
    } else {
      delete queryParam.value.videoGmvValueStart;
    }
    if (videoGmvValueEnd.value !== null) {
      queryParam.value.videoGmvValueEnd = videoGmvValueEnd.value * 1000;
    } else {
      delete queryParam.value.videoGmvValueEnd;
    }

    // 设置属性IDs
    if (attributeIds.value.length > 0) {
      queryParam.value.attributeIds = attributeIds.value.join(',');
    } else {
      delete queryParam.value.attributeIds;
    }

    // 设置历史合作
    if (historyCooperation.value.length > 0) {
      queryParam.value.cooperatedProductIds = historyCooperation.value.join(',');
    } else {
      delete queryParam.value.cooperatedProductIds;
    }

    // 设置历史筛选
    if (historicalSelection.value.length > 0) {
      queryParam.value.selectedProductIds = historicalSelection.value.join(',');
    } else {
      delete queryParam.value.selectedProductIds;
    }

    // 设置KOL类型
    if (kolTypes.value.length > 0) {
      queryParam.value.kolTypes = kolTypes.value.join(',');
    } else {
      delete queryParam.value.kolTypes;
    }

    // 设置最后发帖时间
    if (videoCreateTimeLastStart.value) {
      queryParam.value.videoCreateTimeLastStart = dayjs(videoCreateTimeLastStart.value).format('YYYY-MM-DD');
    } else {
      delete queryParam.value.videoCreateTimeLastStart;
    }

    // 关闭所有弹窗
    mainFilterPopoverVisible.value = false;
    historyCooperationFilterPopoverVisible.value = false;
    kolTypeFilterPopoverVisible.value = false;
    historicalSelectionFilterPopoverVisible.value = false;
    playAvgFilterPopoverVisible.value = false;
    followersFilterPopoverVisible.value = false;
    gmvFilterPopoverVisible.value = false;
    lastPostDateFilterPopoverVisible.value = false;
    
    if (filterOptions.value && Array.isArray(filterOptions.value)) {
    filterOptions.value.forEach((item) => {
      if (item && item.typeId) {
        
        attributeFilterPopoverVisible.value[item.typeId] = false;
      }
    });
  }
    // 如果是搜索，则执行查询
    if (isSearch) {
      selectedRowKeys.value = [];
      fetchTable();
      
    } else {
    }
    nextTick(() => {
      calcTableHeight();
    });
  }

  function getPopupContainer(triggerNode) {
    return triggerNode.parentNode.parentNode ;
  }

  function processTreeData(data) {
    if (!data || !Array.isArray(data)) return [];
    return data.map(item => ({
      ...item,
      value: item.id,
      label: item.attributeName,
      children: item.children ? processTreeData(item.children) : undefined
    }));
  }

  async function getProductCategoryList() {
    const res = await getProductCategoryListApi({});
    productCategoryList.value = Array.isArray(res?.result)
      ? res.result
      : Array.isArray(res)
        ? res
        : [];
  }
  async function getCelebritySegmentList() {
    const res = await getDictItems('celebrity_segment');
    celebritySegmentList.value = res;
   
  }
  async function initAttributeList() {
    try {
      const res = await defHttp.get({ url: '/kol/attribute/loadTreeDataAll' });
      if (res) {
        console.log(res.result);

        filterOptions.value = [...res];

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
    } catch (error) {
      console.error('获取属性列表失败:', error);
    }
  }
  async function initCatoryTypeList() {
    try {
      const [res, categoryRes] = await Promise.all([
        defHttp.get({ url: '/kol/kolAttributeType/listAll' }),
        defHttp.get({ url: '/kol/attribute/getKolAttribute' }),
      ]);

      if (res && categoryRes) {
        const mappedList = res
          .map((typeItem) => {
            // 查找是否在 categoryRes 中存在对应的数据
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
            return null;
          })
          .filter(Boolean); // 过滤掉 null 值


        socialMediaCategoryList.value = mappedList;
        categoryTypeList.value = res.map(item => ({
          id: item.id,
          typeName: item.typeName,
          typeCode: item.typeCode || `type_${item.id}`,
        }));
        console.log(mappedList);

        // 如果这些函数存在，则调用
     
         initAttributeList();
         
         // 动态添加列
         addDynamicColumns();

        fetchTable(1)
        // console.log(columns.value, dataSource.value);
      }
    } catch (error) {
      console.error('获取数据失败:', error);
    }
  }
  initCatoryTypeList();
  getCommonCountry()
  queryAllCelebrityCounselor()
  getBrandList()
  getPersonalTagsList()
  getProductCategoryList()
  getCelebritySegmentList()
  initbnormalCode()
 </script>
 <style scoped lang="less" >

 .platform-card {
  border: 1px solid #d9d9d9;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.personal-tag-names {
  display: flex;
  flex-wrap: wrap;
  row-gap: 4px;
  max-width: 100%;
  height: 49px; /* 两行 */
  overflow: hidden;
  white-space: normal;
}
.personal-tag-names  :deep(.ant-tag) {
  color: @primary-color !important;
  background-color: white !important;
  border-color: #dbe2ff !important;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 4px;
  max-height: 19.5px;
}
.platform-card-yt {
  border-left: 1px solid transparent !important;
  border-right: 1px solid transparent !important;
}
.platform-card-group  :deep(.ant-checkbox + span) {
  display: flex;
    line-height: 32px;
    align-items: center;
}
.platform-card-group  :deep(.ant-checkbox-wrapper) {
  // line-height: 32px !important;
}
.platform-card-group :deep(.ant-checkbox) {
  display: none !important;
}
.platform-card-group .CheckboxChecked {
  border-color: @primary-color !important;
  color: @primary-color !important;
  background-color: #eff5fe !important;
}
.CheckboxChecked + .CheckboxChecked {
  border-left: none !important;
}
 :deep(.surely-table) {

  .surely-table-horizontal-scroll + div {
    visibility: hidden !important;
  }
    .surely-table-body-viewport-container + div {
      // 使用CSS的clip属性来隐藏元素。这样可以根据元素的尺寸和位置来裁剪显示区域，将其设为与元素相同大小的矩形，即可隐藏该元素。
      clip: rect(0, 0, 0, 0);
    }
  
}
:deep(.surely-table){ 
  .surely-table-header-cell{
    background: #f0f3fe ;
    color: #0b1019 ;
    font-weight: 700 ;
    font-size: 12px;
  }
  .surely-table-row {
    font-size: 12px;
    color: #0b1019 ;
  }
}
.colum_avatarUrl {
  width: 70px;
  height: 70px;
  line-height: 70px;
  margin-right: 20px;
  display: flex;

  .colum_jingjie {
    position: absolute;
    bottom: -22px;
    right: -10px;
    color: red;
    font-size: 18px;
  }

  .colum_dengdai {
    position: absolute;
    bottom: -27px;
    right: 4px;
    color: #ff8d1a;
    font-size: 18px;
  }
}
.platformType-img {
  display: inline-block;
  width: 24px;
  height: 24px;

  img {
    width: 100%;
    height: 100%;
  }
}
.colum_account {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: calc(100% - 70px - 20px);
  color: #0b1019 !important;
}
.colum_img {
  display: flex;
  align-items: center;
}
:deep(.contractAmountinput .ant-input-disabled) {
background: #fff !important;
}
:deep(.contractAmountinput_input) {
color: #0b1019 !important;
}
:deep(.contractAmountinput  .ant-input-number) {
min-width: calc(50% - 50px) !important;
width: calc(50% - 50px) !important;
}

/* 更多筛选相关样式 */
.rotating-arrow-filter-icon {
  color: rgba(0, 0, 0, 0.25);
  transition: transform 0.3s ease;
  transform-origin: center center;
}

.main-filter-open .rotating-arrow-filter-icon {
  transform: rotate(180deg);
}

.main-filter-closed .rotating-arrow-filter-icon {
  transform: rotate(0deg);
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
    overflow-y:auto;
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
          color: #8a8a8a;
          font-size: 12px;
          margin-bottom: 4px;
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
  background-color: @primary-color;
  color: white;
  border-radius: 8px;
  font-size: 12px;
  margin-right: 8px;
  padding: 0 4px;
}

.more-filter-button {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 15px;
  display: flex;
  align-items: center;
  cursor: pointer;
  height: 32px;

  font-size: 12px;
  background-color: #fff;
  text-align: center;

  span:first-child {
    color: #969696;
  }

  span:last-child {
    margin-left: 8px;
  }
}

:deep(.more-filter-button span) {
  display: inline-flex !important;
  align-items: center !important;
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
  display: inline-block;
  align-items: center;
  cursor: pointer;
  height: 32px;
  line-height: 32px;
  font-size: 12px;
  background-color: #fff;
  text-align: center;

  margin-right: 8px;

}

:deep(.input-num .ant-input-number-handler-wrap) {
  display: none !important;
}

/* Element Plus Cascader 样式 */
:deep(.el-cascader-menu) {
  width: 100% !important;
}

:deep(.el-cascader-panel) {
  height: 100% !important;
  width: 100% !important;
  border: none !important;
}

:deep(.el-cascader-menu__wrap) {
  height: 100% !important;
}

 </style>