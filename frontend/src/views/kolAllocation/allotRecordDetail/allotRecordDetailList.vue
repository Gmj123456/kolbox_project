<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
      <a-form class="searchForm" @keyup.enter="searchQuery">
        <a-row :gutter="12">
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <JDictSelectPlatformType
                v-model:value="queryParam.platformType"
                dictCode="platform_type"
              ></JDictSelectPlatformType>
            </a-form-item>
          </a-col>
          <a-col :xl="2" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                v-model:value="queryParam.uniqueId"
                allowClear
                placeholder="账号"
              ></a-input>
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
              <a-select v-model:value="queryParam.counselorId" show-search option-filter-prop="label" placeholder="顾问" allowClear>
                <a-select-option
                  v-for="item in CelebrityConsultants"
                  :key="item.id"
                  :value="item.id"
                  :label="item.username"
                >
                  {{ item.username }}
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
          <a-col :xl="3" :lg="7" :md="8" :sm="24">
            <a-input v-model:value="queryParam.emailKeyword" placeholder="邮箱"></a-input>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-input-group style="display: flex" compact>
              <a-input
                disabled
                style="width: 50px; pointer-events: none; backgroundcolor: #fff"
                placeholder="日期"
              ></a-input>
              <a-range-picker
                style="flex: 1"
                v-model:value="historyData"
                format="YYYY-MM-DD"
                @change="startDateChange"
              />
            </a-input-group>
          </a-col>
          <a-col :xl="4" :lg="10" :md="12" :sm="24">
            <a-input-group style="top: 0px !important" compact>
              <a-select
                style="width: 100px;border-right: 0px;"
                show-search
                allowClear
                option-filter-prop="label"
                v-model:value="queryParam.brandId"
                placeholder="品牌"
                @change="onBrandChange"
              >
                <a-select-option v-for="item in brandList" :key="item.id" :label="item.brandName">{{
                  item.brandName
                }}</a-select-option>
              </a-select>
              <a-select
                style="width: calc(100% - 100px)"
                show-search

                v-model:value="queryParam.productId"
                placeholder="产品"
                allowClear
                option-filter-prop="label"
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
            </a-input-group>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :xl="24" :lg="24" :md="24" :sm="24">
          <a-form-item>
          
              <span
                style="float: left; overflow: hidden"
                class="table-page-search-submitButtons"
              >
                <el-popover
                  ref="mainFilterPopoverRef"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  width="650"
                  trigger="click"
                  @show="onPopoverShow"
                  @hide="onPopoverHide"
                >
                  <div style="height: 340px" class="more-filter-container">
                    <div class="more-filter-content">
                      <div class="more-filter-content_menu">
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
                          :class="{ selected: filterType === 'tag' }"
                          @click="selectFilterOther('tag')"
                        >
                          <span>标签</span>
                          <span>
                            <span v-if="getTagCount() > 0" class="selected-count">
                              {{ getTagCount() }}
                            </span>
                            <i style="color: #606266" class="el-icon-arrow-right"></i>
                          </span>
                        </div>
                      </div>
                      <div v-if="filterType === 'tag'" class="more-filter-form">
                        <div class="range-input">
                          <div style="display: flex; align-items: center">
                            <a-select
                              style="width: 100%"
                              @focus="closeTagEdit"
                              @blur="selBlur"
                              :autoClearSearchValue="false"
                                :getPopupContainer="getPopupContainer"
                              @search="fetchUserDebounced"
                              @popup-scroll="tagPopupScroll"
                              @change="handleChange"
                              :filter-option="false"
                              :maxTagCount="20"
                              mode="multiple"
                              v-model:value="moreFilterTagNameList"
                              allowClear
                              placeholder="标签"
                              :open="selectOpen"
                              @deselect="deselect"
                            >
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
                              style="margin-left: 8px"
                              :style="{ color: moreFilterTagVisible ? 'blue' : '' }"
                              @click="moreFilterTagVisible = !moreFilterTagVisible"
                            />
                          </div>
                          <div v-if="moreFilterTagVisible">
                            <div style="display: flex; gap: 8px">
                              <a-textarea
                                style="flex: 1"
                                v-model:value="moreFilterTagValue"
                                :auto-size="{ minRows: 9, maxRows: 9 }"
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
                                v-if="moreFilterNoTag"
                              >
                                不存在的：
                                <div
                                  style="margin-right: 5px"
                                  v-for="item in moreFilterNonExistentTag"
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
                                  @click="closeMoreFilterTagEdit"
                                  style="margin-right: 50px"
                                  >关闭</a-button
                                >
                              </div>
                              <div>
                                <a-button
                                  @click="clearMoreFilterTag"
                                  style="margin-right: 8px"
                                  >清空</a-button
                                >
                                <a-button
                                  type="primary"
                                  @click="moreFilterTagSearch(moreFilterTagValue)"
                                  >确定</a-button
                                >
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="more-filter-form" v-if="filterType == 'followersNum'">
                        <div class="filter-form-select">
                          <div
                            class="filter-form-select_item"
                            :class="{ selected: selectedFollowersRanges.includes('0-1万') }"
                            @click="selectFollowersRange('0-1万')"
                          >
                            0-1万
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedFollowersRanges.includes('1万-3万'),
                            }"
                            @click="selectFollowersRange('1万-3万')"
                          >
                            1万-3万
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedFollowersRanges.includes('3万-10万'),
                            }"
                            @click="selectFollowersRange('3万-10万')"
                          >
                            3万-10万
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedFollowersRanges.includes('10万-30万'),
                            }"
                            @click="selectFollowersRange('10万-30万')"
                          >
                            10万-30万
                          </div>
                          <div
                            class="filter-form-select_item"
                            :class="{
                              selected: selectedFollowersRanges.includes('30万+'),
                            }"
                            @click="selectFollowersRange('30万+')"
                          >
                            30万+
                          </div>
                        </div>
                        <div class="range-input">
                          <div class="range-input__custom-label">自定义范围</div>
                          <div class="range-input__content">
                            <div class="range-input__flex">
                              <div class="range-input__label">最小粉丝数</div>
                              <div class="range-input__input">
                                <a-input-number
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999999"
                                  :precision="0"
                                  v-model:value="moreFilterMinFollowers"
                                  placeholder="输入值"
                                  @change="onMoreFilterFollowersInputChange"
                                ></a-input-number>
                              </div>
                            </div>
                            <div class="range-input__sperate">-</div>
                            <div class="range-input__flex">
                              <div class="range-input__label">最大粉丝数</div>
                              <div class="range-input__input">
                                <a-input-number
                                  style="width: 100%"
                                  :min="0"
                                  :max="99999999"
                                  :precision="0"
                                  v-model:value="moreFilterMaxFollowers"
                                  placeholder="输入值"
                                  @change="onMoreFilterFollowersInputChange"
                                ></a-input-number>
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
                                  v-model:value="moreFilterMinVideoCount"
                                  placeholder="输入值"
                                  @change="onMoreFilterVideoInputChange"
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
                                  v-model:value="moreFilterMaxVideoCount"
                                  placeholder="输入值"
                                  @change="onMoreFilterVideoInputChange"
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
                         :icon="h(SafetyCertificateOutlined)"
                        >应用</a-button
                      >
                      <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
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
                      <DownOutlined class="rotating-arrow-filter-icon" />
                    </a-button>
                  </template>
                </el-popover>
                <a-button
                  type="primary"
                  :icon="h(SearchOutlined)"
                  @click="searchQuery"
                  style="margin-left: 8px"
                  >查询</a-button
                >
                <a-button 
                  :icon="h(ReloadOutlined)" 
                  @click="searchReset" 
                  style="margin: 0px 8px"
                  >重置</a-button
                >
              </span>
              <el-popover
                ref="tagFilterPopoverRef"
                  popper-class="more-filter-popover"
                  placement="bottom-start"
                  width="400"
                  trigger="click"
                  @show="onTagPopoverShow"
                  @hide="onTagPopoverHide"
              >
                <div style="height: 340px" class="more-filter-container">
                  <div class="more-filter-content">
                    <div class="more-filter-form">
                      <div class="range-input">
                        <div style="display: flex; align-items: center">
                          <a-select
                            style="width: 100%"
                            @focus="closeTagEdit"
                            @blur="selBlur"
                            :autoClearSearchValue="false"
                            @search="fetchUserDebounced"
                            @popup-scroll="tagPopupScroll"
                            :getPopupContainer="getPopupContainer"
                            @change="handleChange"
                            :filter-option="false"
                            :maxTagCount="20"
                            mode="multiple"
                            v-model:value="moreFilterTagNameList"
                            allowClear
                            placeholder="标签"
                            :open="selectOpen"
                            @deselect="deselect"
                          >
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
                          <a-icon
                            style="margin-left: 8px"
                            :style="{ color: moreFilterTagVisible ? 'blue' : '' }"
                            @click="moreFilterTagVisible = !moreFilterTagVisible"
                            type="align-left"
                          />
                        </div>
                        <div v-if="moreFilterTagVisible">
                          <div style="display: flex; gap: 8px">
                            <a-textarea
                              style="flex: 1"
                              v-model:value="moreFilterTagValue"
                              :auto-size="{ minRows: 7, maxRows: 10 }"
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
                              v-if="moreFilterNoTag"
                            >
                              不存在的：
                              <div
                                style="margin-right: 5px"
                                v-for="item in moreFilterNonExistentTag"
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
                                @click="closeMoreFilterTagEdit"
                                style="margin-right: 50px"
                                >关闭</a-button
                              >
                            </div>
                            <div>
                              <a-button
                                @click="clearMoreFilterTag"
                                style="margin-right: 8px"
                                >清空</a-button
                              >
                              <a-button
                                type="primary"
                                @click="moreFilterTagSearch(moreFilterTagValue)"
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
                      v-show="
                        queryParam.tagNameList && queryParam.tagNameList.length > 0
                      "
                      class="more-filter-button"
                      style="margin-right: 8px"
                    >
                    <span
                      >标签：{{
                        Array.isArray(queryParam.tagNameList)
                          ? queryParam.tagNameList.join(",")
                          : ""
                      }}</span
                    >
                    <span
                      :class="
                        tagFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
                      "
                    >
                      <DownOutlined
                        class="rotating-arrow-filter-icon"
                        style="margin-left: 8px; color: #666"
                      />
                    </span>
                    <span>
                      <CloseCircleFilled
                        @click.stop.prevent="clearFilterData($event, 'tag')"
                        @mousedown.stop.prevent
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <el-popover
                ref="followersFilterPopoverRef"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
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
                          :class="{ selected: selectedFollowersRanges.includes('0-1万') }"
                          @click="selectFollowersRange('0-1万')"
                        >
                          0-1万
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{ selected: selectedFollowersRanges.includes('1万-3万') }"
                          @click="selectFollowersRange('1万-3万')"
                        >
                          1万-3万
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedFollowersRanges.includes('3万-10万'),
                          }"
                          @click="selectFollowersRange('3万-10万')"
                        >
                          3万-10万
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedFollowersRanges.includes('10万-30万'),
                          }"
                          @click="selectFollowersRange('10万-30万')"
                        >
                          10万-30万
                        </div>
                        <div
                          class="filter-form-select_item"
                          :class="{
                            selected: selectedFollowersRanges.includes('30万+'),
                          }"
                          @click="selectFollowersRange('30万+')"
                        >
                          30万+
                        </div>
                      </div>
                      <div class="range-input">
                        <div class="range-input__custom-label">自定义范围</div>
                        <div class="range-input__content">
                          <div class="range-input__flex">
                            <div class="range-input__label">最小粉丝数</div>
                            <div class="range-input__input">
                              <a-input-number
                                style="width: 100%"
                                :min="0"
                                :precision="0"
                                :max="99999999"
                                v-model:value="moreFilterMinFollowers"
                                placeholder="输入值"
                                @change="onMoreFilterFollowersInputChange"
                              ></a-input-number>
                            </div>
                          </div>
                          <div class="range-input__sperate">-</div>
                          <div class="range-input__flex">
                            <div class="range-input__label">最大粉丝数</div>
                            <div class="range-input__input">
                              <a-input-number
                                style="width: 100%"
                                :min="0"
                                :precision="0"
                                :max="99999999"
                                v-model:value="moreFilterMaxFollowers"
                                placeholder="输入值"
                                @change="onMoreFilterFollowersInputChange"
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
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
                    <span> 粉丝数： </span>
                    <span v-if="queryParam.minFollowers && queryParam.maxFollowers"
                      >{{ queryParam.minFollowers }} -
                      {{ queryParam.maxFollowers }}</span
                    >
                    <span
                      v-else-if="
                        queryParam.minFollowers && !queryParam.maxFollowers
                      "
                      >≥ {{ queryParam.minFollowers }}</span
                    >
                    <span
                      v-else-if="
                        !queryParam.minFollowers &&
                        queryParam.maxFollowers
                      "
                      >≤ {{ queryParam.maxFollowers }}</span
                    >
                    <span
                      :class="
                        followersFilterPopoverVisible
                          ? 'main-filter-open'
                          : 'main-filter-closed'
                      "
                    >
                      <DownOutlined
                        class="rotating-arrow-filter-icon"
                        style="margin-left: 8px; color: #666"
                      />
                    </span>
                    <span>
                      <CloseCircleFilled
                        @click.stop.prevent="clearFilterData($event, 'followers')"
                        @mousedown.stop.prevent
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <el-popover
                ref="videoFilterPopoverRef"
                popper-class="more-filter-popover"
                placement="bottom-start"
                width="400"
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
                                v-model:value="moreFilterMinVideoCount"
                                placeholder="输入值"
                                @change="onMoreFilterVideoInputChange"
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
                                v-model:value="moreFilterMaxVideoCount"
                                placeholder="输入值"
                                @change="onMoreFilterVideoInputChange"
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
                    <a-button type="primary"  :icon="h(SearchOutlined)" @click="applyAllFilters(true)"
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
                      >{{ queryParam.minVideoStandardCount }} -
                      {{ queryParam.maxVideoStandardCount }}</span
                    >
                    <span
                      v-else-if="
                        queryParam.minVideoStandardCount &&
                        !queryParam.maxVideoStandardCount
                      "
                      >≥ {{ queryParam.minVideoStandardCount }}</span
                    >
                    <span
                      v-else-if="
                        !queryParam.minVideoStandardCount &&
                        queryParam.maxVideoStandardCount
                      "
                      >≤ {{ queryParam.maxVideoStandardCount }}</span
                    >
                    <span
                      :class="
                        videoFilterPopoverVisible ? 'main-filter-open' : 'main-filter-closed'
                      "
                    >
                      <DownOutlined
                        class="rotating-arrow-filter-icon"
                        style="margin-left: 8px; color: #666"
                      />
                    </span>
                    <span>
                      <CloseCircleFilled
                        @click.stop.prevent="clearFilterData($event, 'video')"
                        @mousedown.stop.prevent
                      />
                    </span>
                  </div>
                </template>
              </el-popover>
              <div
                v-if="
                  (queryParam.tagNameList && queryParam.tagNameList.length > 0) ||
                  queryParam.minFollowers ||
                  queryParam.maxFollowers ||
                  queryParam.minVideoStandardCount ||
                  queryParam.maxVideoStandardCount
                "
                class="clear-all-button"
                @click="clearAllFilters"
              >
                <span>全部清除</span>
              </div>
          </a-form-item>
          </a-col>
        </a-row>
      </a-form>
 
    <a-table
      ref="tableRef"
      :rangeSelection="false"
      size="small"
      rowKey="id"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :scroll="{ y: sTableHeight, x: '100%' }"
      @change="pageChange"
    >
      <template #bodyCell="{ column, record, text, index }">
        <!-- 序号列 -->
        <template v-if="column.key === 'index'">
          {{ index + 1 }}
        </template>

        <!-- 账号列 -->
        <template v-if="column.dataIndex === 'uniqueId'">
        <div style="display: flex; align-items: center; gap: 10px">
          <div style="width: 30px; height: 30px">
            <a-tooltip placement="top">
              <template #title>
                <img
                  v-if="record.authorAvatarUrl"
                  :src="authorUrl(record)"
                    @error="handleImageError"
                  style="width: 100%"
                  :preview="record.id"
                />
                <img
                  v-else
                  src="@/assets/images/avatar.png"
                  style="max-width: 30px; max-height: 30px; margin: 0 auto"
                />
              </template>
              <img
                v-if="record.authorAvatarUrl"
                :src="authorUrl(record)"
                 @error="handleImageError"
                style="max-height: 30px; max-width: 30px; cursor: pointer"
                :preview="record.id"
              />
              <img
                v-else
                src="@/assets/images/avatar.png"
                style="max-width: 30px; max-height: 30px; margin: 0 auto"
              />
            </a-tooltip>
          </div>
          <div style="flex: 1">
            <div style="width: 100%">
              <a @click="openLink(record)">
                <!-- <EllipsisTooltip :text="record.uniqueId"></EllipsisTooltip> -->
                <j-ellipsis
                  :value="record.uniqueId"
                  :length="15"
                  style="color: #3155ed"
                />
              </a>
            </div>
            <div>
              <span class="account" v-if="record.country">{{
                parseCountry(record.country)
              }}</span>
              <span class="account" v-else>-</span>
              <!-- <span style="margin-left:28px">{{record.videoStandardCount}}/{{record.videoSampleCount}}</span> -->
            </div>
          </div>
        </div>
        </template>

        <!-- 平台列 -->
        <template v-if="column.dataIndex === 'platformType'">
          <img
            v-if="text == 0"
            style="width: 20px; height: 20px"
            src="@/assets/images/ins.png"
            alt=""
          />
          <img
            v-if="text == 1"
            style="width: 20px; height: 20px"
            src="@/assets/images/yt.png"
            alt=""
          />
          <img
            v-if="text == 2"
            style="width: 20px; height: 20px"
            src="@/assets/images/tk.png"
            alt=""
          />
        </template>

        <!-- 粉丝数列 -->
        <template v-if="column.dataIndex === 'authorFollowerCount'">
          <span>{{ getFollower(text) }}</span>
        </template>

        <!-- 有效视频列 -->
        <template v-if="column.dataIndex === 'videoStandardCount'">
          <span>{{ text }}/{{ record.videoSampleCount }}</span>
        </template>

        <!-- 标签列 -->
        <template v-if="column.dataIndex === 'tagName'">
          <div v-if="text !== null && text !== ''">
            <a-tooltip :title="text">
              <a-tag
               
                @click="copyFn(text)"
                :style="{
                  border: 'none',
                  backgroundColor: tagColor(record.tagType).split(';')[0],
                  color: tagColor(record.tagType).split(';')[1],
                }"
                >{{ text }}</a-tag
              >
            </a-tooltip>
          </div>
        </template>

        <!-- 产品列 -->
        <template v-if="column.dataIndex === 'productName'">
          <EllipsisTooltip v-if="text" :text="text" />
          <span v-else>--</span>
        </template>

        <!-- 邮箱列 -->
        <template v-if="column.dataIndex === 'email'">
          <j-ellipsis
            v-if="text"
            @click="copyFn(text)"
            :value="text"
            :length="40"
            style="color: #3155ed"
          ></j-ellipsis>
          <span v-else>--</span>
        </template>

        <!-- 简介列 -->
        <template v-if="column.dataIndex === 'signature'">
          <EllipsisTooltip v-if="text" :text="text" :lines="2"></EllipsisTooltip>
          <span v-else>--</span>
        </template>

        <!-- 是否建联列 -->
        <template v-if="column.dataIndex === 'celebrityPrivateId'">
          <a-tag :color="text ? 'green' : 'orange'">{{ text ? '是' : '否' }}</a-tag>
        </template>

        <!-- 分配日期列 -->
        <template v-if="column.dataIndex === 'allotTime'">
          {{ allocationDate(text) }}
        </template>

        <!-- 操作列 -->
        <template v-if="column.key === 'action'">
          <a-tooltip placement="top" title="修改顾问">
            <a @click="handleEdit(record)">
              <FormOutlined style="font-size: 15px" />
            </a>
          </a-tooltip>
        </template>
      </template>

      <template #headerCell="{ column }">
        <!-- 有效视频表头 -->
        <template v-if="column.dataIndex === 'videoStandardCount'">
          <a-tooltip title="TOP视频数为达人首页前20个视频，播放量大于5K的个数">
            有效视频
            <QuestionCircleOutlined style="margin-left: 5px" />
          </a-tooltip>
        </template>

        <!-- 标签表头 -->
        <template v-if="column.dataIndex === 'tagName'">
          <span style="display: flex;align-items: center;">
            <span>标签</span>
          <a-popover overlayClassName="tagTypePopover">
            <template #content>
              <div
                style="display: flex; flex-direction: column; row-gap: 4px; font-size: 12px"
              >
                <div style="margin-bottom: 4px">由以下类型组成：</div>
                <div
                  v-for="item in tagTypeList"
                  :key="item.value"
                  :style="{ backgroundColor: tagColor(item.value).split(';')[0] }"
                  style="display: flex; align-items: center; height: 30px; padding: 0 10px"
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
    </a-table>
    <EditConsultantModal ref="modalRef" @ok="modalFormOk"></EditConsultantModal>
  </a-card>
</template>

<script setup name="allotRecordDetailList">
import { ref, computed, watch, onMounted, onActivated, onBeforeUnmount, nextTick, h } from 'vue';
import { useRoute } from 'vue-router';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { useTable } from '/@/components/useSTable/useSTable';
import { getDictItems } from '/@/api/common/api';
import { ElPopover } from 'element-plus'
import JEllipsis from '/@/components/Form/src/jeecg/components/JEllipsis.vue';
import EditConsultantModal from './modules/editConsultantModal.vue';
import dayjs from "dayjs";

import { isEqual, cloneDeep,debounce } from 'lodash-es';
import EllipsisTooltip from '@/components/jeecg/EllipsisTooltip.vue';
import JDictSelectPlatformType from '@/components/jeecg/JDictSelectPlatformType.vue';
import { SafetyCertificateOutlined,SearchOutlined, ReloadOutlined, FormOutlined, QuestionCircleOutlined, DownOutlined, CloseCircleFilled, AlignLeftOutlined } from '@ant-design/icons-vue';

const { createMessage } = useMessage();
const route = useRoute();
const modalRef = ref(null);
const tableRef = ref(null);
// API 端点定义
const url = {
  list: "/kol/tagAllot/list",
  delete: "/kol/tagAllot/delete",
  deleteBatch: "/kol/tagAllot/deleteBatch",
  exportXlsUrl: "/kol/tagAllot/exportXls",
  importExcelUrl: "/kol/tagAllot/importExcel",
};

// 表格数据获取函数
const fetchTableApi = async (params) => {
  const res = await defHttp.post({ 
    url: `${url.list}?pageNo=${params.pageNo}&pageSize=${params.pageSize}`, 
    data: params
  });
  return res
  // if (res.success) {
  //   // 处理 platformType
  //   if (res.result && res.result.records) {
  //     res.result.records.forEach((element) => {
  //       if (queryParam.value.platformType == 0) {
  //         element.platformType = 0;
  //       } else if (queryParam.value.platformType == 1) {
  //         element.platformType = 1;
  //       } else {
  //         element.platformType = 2;
  //       }
  //     });
  //   }
  //   return res.result || res;
  // }
  // if (res.code === 510) {
  //   createMessage.warning(res.message);
  // }
  // return { records: [], total: 0 };
};

// 使用 useTable 组合式函数
const {
  loading,
  dataSource,
  pagination,
  queryParam,
  sTableHeight,
  fetchTable,
  pageChange,
  updatePagination,
  calcTableHeight
} = useTable(fetchTableApi, 0, {
  tableType:"aTable",
  afterFetch:allotRecordDetailListAfterFetchFn  
});
updatePagination( {
    defaultPageSize:50,
    showQuickJumper: true,
    showSizeChanger: true,
    pageSizeOptions: ['50', '100', '200', ],
    current:1,
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条'
    },
    total: 0,
    pageSize: 50,
    
  
   
  })
  async function allotRecordDetailListAfterFetchFn(params,res) {
    nextTick(() => {
      calcTableHeight('aTable');
    });
  }
// 响应式数据
const lastFetchId = ref(0);
const fetching = ref(false);
const productList = ref([]);
const countrys = ref([]); // 国家
const tags = ref([]); // 标签（当前显示的标签，分页后的数据）
const allTags = ref([]); // 所有标签数据
const tagPageSize = ref(50); // 每页显示的标签数量
const tagCurrentPage = ref(1); // 当前页码
const hasMoreTags = ref(true); // 是否还有更多标签
const tagLoading = ref(false); // 标签加载状态
const tagType = ref('0'); // 标签类型
const filterTagTypeList = ref([]); // 标签类型列表
const CelebrityConsultants = ref([]); // 网红顾问
const brandList = ref([]);
const historyData = ref([]);
const tagTypeList = ref([]);
const tagTitle = ref("");
const selectOpen = ref(false);

// 更多筛选相关数据
const filterType = ref("");
const moreFilterTagNameList = ref([]);
const moreFilterTagVisible = ref(false);
const moreFilterTagValue = ref("");
const moreFilterNonExistentTag = ref([]);
const moreFilterNoTag = ref(false);
const moreFilterMinFollowers = ref(null);
const moreFilterMaxFollowers = ref(null);
const selectedFollowersRanges = ref([]);
const moreFilterMinVideoCount = ref(null);
const moreFilterMaxVideoCount = ref(null);

// popover状态控制
const isApplyClose = ref(false);
const mainFilterPopoverVisible = ref(false);
const tagFilterPopoverVisible = ref(false);
const followersFilterPopoverVisible = ref(false);
const videoFilterPopoverVisible = ref(false);

// 备份数据，用于取消时恢复
const backupData = ref({
  moreFilterTagNameList: [],
  moreFilterTagVisible: false,
  moreFilterTagValue: "",
  moreFilterNonExistentTag: [],
  moreFilterNoTag: false,
  moreFilterMinFollowers: null,
  moreFilterMaxFollowers: null,
  selectedFollowersRanges: [],
  moreFilterMinVideoCount: null,
  moreFilterMaxVideoCount: null,
});

// 列配置
const sortedInfo = ref(null);
const columns = computed(() => {
  const sortInfo = sortedInfo.value || {};
  return [
    {
      title: "#",
      key: "index",
      width: 60,
      align: "center",
    },
    {
      title: "账号",
      width: 180,
      dataIndex: "uniqueId",
      autoHeight:true
    },
    {
      title: "平台",
      dataIndex: "platformType",
      width: 40,
    },
    {
      title: "粉丝",
      width: 60,
      dataIndex: "authorFollowerCount",
      key: "authorFollowerCount",
  
    },
    {
      width: 120,
      dataIndex: "videoStandardCount",
      key: "videoStandardCount",
    
    },
    {
      title: "顾问",
      width: 100,
      dataIndex: "counselorName",
    },
    {
      title: "私有网红",
      align: "center",
      width: 100,
      dataIndex: "celebrityPrivateId",
    },
    {
      dataIndex: "tagName",
    },
    {
      title: "产品",
      dataIndex: "productName",
      width: 150,
    },
    {
      title: "邮箱",
      dataIndex: "email",
      width: 300,
      key: "email",
    
    },
    {
      title: "简介",
      dataIndex: "signature",
      width: 200,
    },
    {
      title: "分配日期",
      dataIndex: "allotTime",
      width: 100,
    },
    {
      title: "操作",
      width: 100,
      align: "center",
      key: "action",
      fixed: "right",
    },
  ];
});
// Watch


watch(
  tags,
  (nVal) => {
    if (nVal.length > 0) {
      selectOpen.value = true;
    }
  }
);
queryParam.value.platformType = 2;
queryParam.value.isHasEmail = " ";

fetchTable();
// 生命周期
resetTagPagination();
initTagTypeList();
initBrandList();
initCountry();
initlCelebrityConsultant();

onBeforeUnmount(() => {
  // 清理防抖函数，避免内存泄漏
  if (debouncedLoadMoreTags.value && debouncedLoadMoreTags.value.cancel) {
    debouncedLoadMoreTags.value.cancel();
  }
});
function handleImageError(e) {
  e.target.src = new URL("/@/assets/images/avatar.png", import.meta.url).href;
}
function modalFormOk(){
  fetchTable();
}
function handleEdit(record){
  modalRef.value.open(record);
}

// 方法定义
const parseCountry = (country) => {
  const countryItem = countrys.value.find((item) => item.shortCode == country);
  return countryItem ? countryItem.country : '';
};

const onBrandChange = (value) => {
  queryParam.value.productId = undefined;
  productList.value = [];
  if (value) {
    initProduct();
  }
};

async function initBrandList() {
  const res = await defHttp.get({ url: "/kolBrand/listAll" });
  if (res) {
    brandList.value = res;
  }
};

async function initProduct() {
  const res = await defHttp.get({ url: "/kol/kolProduct/listAll", params: {
    brandId: queryParam.value.brandId,
  }});
  if (res) {
    productList.value = res.map((item) => {
      return {
        ...item,
        text: item.productName,
      };
    });
  }
}
function deselect(value) {
  console.log(value);
  setTimeout(() => {
    selectOpen.value = false;
  }, 1);
};

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

  // 判断是在更多筛选中还是在普通筛选中
  if (filterType.value === "tag") {
    // 更多筛选标签
    if (!moreFilterTagNameList.value) {
      moreFilterTagNameList.value = [];
    }

    const index = moreFilterTagNameList.value.indexOf(tagName);
    if (index === -1) {
      moreFilterTagNameList.value.push(tagName);
    } else {
      moreFilterTagNameList.value.splice(index, 1);
    }
  } else {
    // 普通筛选标签
    if (!queryParam.value.tagNameList) {
      queryParam.value.tagNameList = [];
    }

    const index = queryParam.value.tagNameList.indexOf(tagName);
    if (index === -1) {
      queryParam.value.tagNameList.push(tagName);
    } else {
      queryParam.value.tagNameList.splice(index, 1);
    }
  }

  // 保持下拉框打开
  selectOpen.value = true;
};

async function initTagTypeList(){
  const res = await getDictItems("tag_type");
  tagTypeList.value = res;
  tagTitle.value = "";
  res.forEach((item) => {
    tagTitle.value += `${item.text}-${item.description}`;
  });

}
const tagColor = (type) => {
  const item = tagTypeList.value.find((item) => item.value == type);
  return item ? item.description : "";
};
// tagTypeParse 方法已不再使用

const handleChange = (value) => {
  fetching.value = false;
  if (value.length > 0) {
    // selectOpen.value = true;
  } else {
    tags.value = [];
  }
};

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

// 防抖函数
const fetchUserDebounced = debounce((value) => {
  fetchUser(value);
}, 800);

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

// 优化虚拟滚动性能的防抖方法
const debouncedLoadMoreTags = ref(debounce(() => {
  loadMoreTags();
}, 200));

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

function closeTagEdit() {
  // tagVisible 已移除，不再需要
  clear();
  // noTag 已移除，不再需要
};

function clear() {
  // tagValue 已移除，不再需要
  // noTag 已移除，不再需要
};

// tagSerch 方法已不再使用，由 moreFilterTagSearch 替代

function allocationDate(text) {
  if (text) {
    return dayjs(text).format("YYYY-MM-DD");
  } else {
    return "";
  }
};

function startDateChange(date, dateString) {
  queryParam.value.allotStartTime = dateString[0]
    ? dayjs(dateString[0]).format("YYYY-MM-DD")
    : undefined;
  queryParam.value.allotEndTime = dateString[1]
    ? dayjs(dateString[1]).format("YYYY-MM-DD")
    : undefined;
};

const openLink = (record) => {
  if (queryParam.value.platformType == "1") {
    window.open(`https://www.youtube.com/channel/${record.account}`);
  } else if (record.platformType == "2") {
    window.open(`https://www.tiktok.com/@${record.uniqueId}`);
  } else {
    window.open(`https://www.instagram.com/${record.uniqueId}`);
  }
};

// 搜索
const searchQuery = () => {

  fetchTable(1);
  closeTagEdit();
};
// 国家
function initCountry() {
  defHttp.get({ url: "/tiktokcountry/getCountryList" }).then((res) => {
    countrys.value = res;
  });
};

// 网红顾问
function initlCelebrityConsultant() {
  defHttp.get({ url: "/sys/user/queryAllCelebrityCounselor" }).then((res) => {
    CelebrityConsultants.value = res;
  });
};

// 重置
const searchReset = () => {
  queryParam.value = {
    
   
  };
  queryParam.value.isHasEmail = " ",
  queryParam.value.platformType =  2,
  productList.value = [];
  historyData.value = [];
  closeTagEdit();

  // 重置更多筛选数据
  filterType.value = "";
  moreFilterTagNameList.value = [];
  moreFilterTagVisible.value = false;
  moreFilterTagValue.value = "";
  moreFilterNonExistentTag.value = [];
  moreFilterNoTag.value = false;
  moreFilterMinFollowers.value = null;
  moreFilterMaxFollowers.value = null;
  selectedFollowersRanges.value = [];
  moreFilterMinVideoCount.value = null;
  moreFilterMaxVideoCount.value = null;
  
  const tableBody = document.querySelector(".s-table-body") || document.querySelector(".ant-table-body");
  if (tableBody) {
    tableBody.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }
  fetchTable(1);
  sortedInfo.value = null;
};

const getFollower = (num) => {
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
    return "--";
  }
};

const authorUrl = (record) => {
  return record.authorAvatarUrl.includes("https") || record.authorAvatarUrl.includes("http")
      ? record.authorAvatarUrl
      : 'https://gemstone-image.kolbox.com/avatar/' + record.authorAvatarUrl;
};

// 更多筛选相关方法
const moreFilter = () => {
  // 更多筛选按钮点击事件
};

// popover显示时备份当前数据
const onPopoverShow = () => {
  isApplyClose.value = false;
  mainFilterPopoverVisible.value = true;

  // 如果没有选中任何筛选类型，默认选中第一个
  if (!filterType.value) {
    filterType.value = "followersNum";
  }

  // 备份当前所有数据
  backupData.value = {
    moreFilterTagNameList: moreFilterTagNameList.value
      ? [...moreFilterTagNameList.value]
      : [],
    moreFilterTagVisible: moreFilterTagVisible.value,
    moreFilterTagValue: moreFilterTagValue.value,
    moreFilterNonExistentTag: moreFilterNonExistentTag.value
      ? [...moreFilterNonExistentTag.value]
      : [],
    moreFilterNoTag: moreFilterNoTag.value,
    moreFilterMinFollowers: moreFilterMinFollowers.value,
    moreFilterMaxFollowers: moreFilterMaxFollowers.value,
    selectedFollowersRanges: [...selectedFollowersRanges.value],
    moreFilterMinVideoCount: moreFilterMinVideoCount.value,
    moreFilterMaxVideoCount: moreFilterMaxVideoCount.value,
  };
};

// popover隐藏时检查是否需要恢复数据
const onPopoverHide = () => {
  mainFilterPopoverVisible.value = false;

  // 如果不是通过应用按钮关闭，则恢复备份数据
  if (!isApplyClose.value) {
    moreFilterTagNameList.value = backupData.value.moreFilterTagNameList
      ? [...backupData.value.moreFilterTagNameList]
      : [];
    moreFilterTagVisible.value = backupData.value.moreFilterTagVisible;
    moreFilterTagValue.value = backupData.value.moreFilterTagValue;
    moreFilterNonExistentTag.value = backupData.value.moreFilterNonExistentTag
      ? [...backupData.value.moreFilterNonExistentTag]
      : [];
    moreFilterNoTag.value = backupData.value.moreFilterNoTag;
    moreFilterMinFollowers.value = backupData.value.moreFilterMinFollowers;
    moreFilterMaxFollowers.value = backupData.value.moreFilterMaxFollowers;
    selectedFollowersRanges.value = [...backupData.value.selectedFollowersRanges];
    moreFilterMinVideoCount.value = backupData.value.moreFilterMinVideoCount;
    moreFilterMaxVideoCount.value = backupData.value.moreFilterMaxVideoCount;
  }
  // 重置标记
  isApplyClose.value = false;
};

// 标签筛选弹窗显示/隐藏控制
const onTagPopoverShow = () => {
  tagFilterPopoverVisible.value = true;
};

const onTagPopoverHide = () => {
  tagFilterPopoverVisible.value = false;
};

// 粉丝数筛选弹窗显示/隐藏控制
const onFollowersPopoverShow = () => {
  followersFilterPopoverVisible.value = true;
};

const onFollowersPopoverHide = () => {
  followersFilterPopoverVisible.value = false;
};

// 有效视频筛选弹窗显示/隐藏控制
const onVideoPopoverShow = () => {
  videoFilterPopoverVisible.value = true;
};

const onVideoPopoverHide = () => {
  videoFilterPopoverVisible.value = false;
};

const selectFilterOther = (type) => {
  filterType.value = type;
};

const getFollowersCount = () => {
  // 如果有预设选择，返回预设选择数量
  if (selectedFollowersRanges.value.length > 0) {
    return selectedFollowersRanges.value.length;
  }
  // 如果有自定义输入，返回1
  if (moreFilterMinFollowers.value || moreFilterMaxFollowers.value) {
    return 1;
  }
  return 0;
};

const getVideoCount = () => {
  // 如果有自定义输入，返回1
  if (moreFilterMinVideoCount.value || moreFilterMaxVideoCount.value) {
    return 1;
  }
  return 0;
};

const getTagCount = () => {
  // 如果有选择标签，返回标签数量
  if (moreFilterTagNameList.value && moreFilterTagNameList.value.length > 0) {
    return moreFilterTagNameList.value.length;
  }
  return 0;
};

// 粉丝数范围选择
const selectFollowersRange = (range) => {
  // 清空自定义输入框
  moreFilterMinFollowers.value = null;
  moreFilterMaxFollowers.value = null;

  // 清空之前的选择，只保留当前选择
  selectedFollowersRanges.value = [range];
};

const onMoreFilterFollowersInputChange = () => {
  // 清除范围选择
  selectedFollowersRanges.value = [];
};

const onMoreFilterVideoInputChange = () => {
  // 有效视频数输入变化处理
};

// 更多筛选标签相关方法
const closeMoreFilterTagEdit = () => {
  moreFilterTagVisible.value = false;
  clearMoreFilterTag();
  moreFilterNoTag.value = false;
};

const clearMoreFilterTag = () => {
  moreFilterTagValue.value = "";
  moreFilterNoTag.value = false;
};

async function moreFilterTagSearch(value) {
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
    moreFilterNonExistentTag.value = allTag.filter((item) => !filterTags.includes(item.toLowerCase()));
    if (moreFilterNonExistentTag.value.length > 0) {
      moreFilterNoTag.value = true;
    } else {
      moreFilterTagNameList.value = allTag;
      moreFilterTagVisible.value = false;
    }
  } catch (error) {
    console.error("moreFilterTagSearch error:", error);
  }
}

function getPopupContainer(triggerNode) {
      return (triggerNode && triggerNode.parentNode.parentNode) || document.body;
    }

// 应用所有筛选条件
const applyAllFilters = (isSearch) => {
  // 标记为通过应用按钮关闭
  isApplyClose.value = true;

  // 应用粉丝数筛选
  if (selectedFollowersRanges.value.length > 0) {
    // 根据选择的范围设置查询条件
    const range = selectedFollowersRanges.value[0];
    const rangeValues = parseFollowersRange(range);
    queryParam.value.minFollowers = rangeValues.min;
    if (rangeValues.max !== null) {
      queryParam.value.maxFollowers = rangeValues.max;
    } else {
      delete queryParam.value.maxFollowers;
    }
  } else {
    // 使用自定义输入的值
    if (moreFilterMinFollowers.value !== null) {
      queryParam.value.minFollowers = moreFilterMinFollowers.value;
    } else {
      delete queryParam.value.minFollowers;
    }

    if (moreFilterMaxFollowers.value !== null) {
      queryParam.value.maxFollowers = moreFilterMaxFollowers.value;
    } else {
      delete queryParam.value.maxFollowers;
    }
  }

  // 应用有效视频筛选
  if (moreFilterMinVideoCount.value !== null) {
    queryParam.value.minVideoStandardCount = moreFilterMinVideoCount.value;
  } else {
    delete queryParam.value.minVideoStandardCount;
  }

  if (moreFilterMaxVideoCount.value !== null) {
    queryParam.value.maxVideoStandardCount = moreFilterMaxVideoCount.value;
  } else {
    delete queryParam.value.maxVideoStandardCount;
  }

  // 应用标签筛选
  if (moreFilterTagNameList.value && moreFilterTagNameList.value.length > 0) {
    queryParam.value.tagNameList = moreFilterTagNameList.value;
  } else {
    delete queryParam.value.tagNameList;
  }

  // 关闭弹窗
  closeAllPopovers();

  // 执行查询
  if (isSearch) {
  
    fetchTable(1);
  }
};

// refs 定义（用于 el-popover）
const mainFilterPopoverRef = ref(null);
const tagFilterPopoverRef = ref(null);
const followersFilterPopoverRef = ref(null);
const videoFilterPopoverRef = ref(null);

const closeAllPopovers = () => {
  if (mainFilterPopoverRef.value) {
    mainFilterPopoverRef.value.hide();
  }
  if (tagFilterPopoverRef.value) {
    tagFilterPopoverRef.value.hide();
  }
  if (followersFilterPopoverRef.value) {
    followersFilterPopoverRef.value.hide();
  }
  if (videoFilterPopoverRef.value) {
    videoFilterPopoverRef.value.hide();
  }
};

// 清除筛选数据
const clearFilterData = (event, filterTypeParam) => {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
    event.stopImmediatePropagation();
  }

  closeAllPopovers();

  nextTick(() => {
    switch (filterTypeParam) {
      case "followers":
        clearFollowersData();
        break;
      case "video":
        clearVideoData();
        break;
      case "tag":
        clearTagData();
        break;

      default:
        console.warn(`未知的筛选类型: ${filterTypeParam}`);
        return;
    }

    fetchTable(1);
  });
};

const clearFollowersData = () => {
  delete queryParam.value.minFollowers;
  delete queryParam.value.maxFollowers;
  moreFilterMinFollowers.value = null;
  moreFilterMaxFollowers.value = null;
  selectedFollowersRanges.value = [];
};

const clearVideoData = () => {
  delete queryParam.value.minVideoStandardCount;
  delete queryParam.value.maxVideoStandardCount;
  moreFilterMinVideoCount.value = null;
  moreFilterMaxVideoCount.value = null;
};

const clearTagData = () => {
  delete queryParam.value.tagNameList;
  moreFilterTagNameList.value = [];
  moreFilterTagValue.value = "";
  moreFilterTagVisible.value = false;
  moreFilterNoTag.value = false;
  moreFilterNonExistentTag.value = [];
};

// 解析粉丝数范围字符串，返回具体的数值范围
const parseFollowersRange = (range) => {
  switch (range) {
    case "0-1万":
      return { min: 0, max: 10000 };
    case "1万-3万":
      return { min: 10000, max: 30000 };
    case "3万-10万":
      return { min: 30000, max: 100000 };
    case "10万-30万":
      return { min: 100000, max: 300000 };
    case "30万+":
      return { min: 300000, max: null }; // 不设上限
    default:
      return { min: null, max: null };
  }
};

const clearAllFilters = () => {
  // 关闭所有弹窗
  closeAllPopovers();

  // 重置查询参数
  queryParam.value = {
    isHasEmail: " ",
    platformType: 2,
    uniqueId: undefined,
    countryCode: undefined,
    counselorId: undefined,
    isPrivateKol: undefined,
    emailKeyword: undefined,
    brandId: undefined,
    productId: undefined,
    allotStartTime: undefined,
    allotEndTime: undefined,
    tagNameList: [],
    minFollowers: undefined,
    maxFollowers: undefined,
    minVideoStandardCount: undefined,
    maxVideoStandardCount: undefined,
  };
  historyData.value = [];
  closeTagEdit();

  // 重置更多筛选数据
  filterType.value = "";
  moreFilterTagNameList.value = [];
  moreFilterTagVisible.value = false;
  moreFilterTagValue.value = "";
  moreFilterNonExistentTag.value = [];
  moreFilterNoTag.value = false;

  // 重置粉丝数相关
  moreFilterMinFollowers.value = null;
  moreFilterMaxFollowers.value = null;
  selectedFollowersRanges.value = [];

  // 重置有效视频相关
  moreFilterMinVideoCount.value = null;
  moreFilterMaxVideoCount.value = null;

  // 重置弹窗状态
  mainFilterPopoverVisible.value = false;
  tagFilterPopoverVisible.value = false;
  followersFilterPopoverVisible.value = false;
  videoFilterPopoverVisible.value = false;
  
  const tableBody = document.querySelector(".s-table-body") || document.querySelector(".ant-table-body");
  if (tableBody) {
    tableBody.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }
  // 重新加载数据
  fetchTable(1);
  sortedInfo.value = null;
};
</script>
<style scoped>
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


/* .account{
    width:60px;
    height:
  } */
.account {
  display: inline-block;
  width: 80px;
}
</style>
<style lang="less" scoped>
/deep/ .ant-tag {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
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

/* 更多筛选样式 - 从celebrityMatchingList复制 */
.more-filter-popover {
  padding: 0 !important;
}

.more-filter-container {
  display: flex;
  flex-direction: column;
}

.more-filter-content {
  flex: 1;
  display: flex;
  overflow-y: auto;
}

.more-filter-form {
  width: 100%;
  overflow-x: auto;
}

.filter-form-select {
  height: 180px;
  width: 100%;
  overflow-y: auto;
  border-bottom: 1px solid #d9d9d9;
}

.filter-form-select_item {
  color: #121415;
  cursor: pointer;
  font-size: 14px;
  line-height: 22px;
  list-style: none;
  padding: 6px 12px;
}

.filter-form-select_item:hover,
.filter-form-select_item.selected {
  background-color: #f2f8ff;
}

.range-input {
  height: auto;
  flex-direction: column;
  display: flex;
  justify-content: center;
  padding: 12px;
  gap: 10px;
}

.range-input__content {
  align-items: flex-end;
  display: flex;
}

.range-input__flex {
  flex: 1;
}

.range-input__label {
  margin-bottom: 4px;
  color: #8a8a8a;
  font-size: 12px;
}

.range-input__sperate {
  margin-bottom: 6px;
  margin-left: 8px;
  margin-right: 8px;
}

.more-filter-content_menu {
  width: 200px;
  height: 100%;
  border-right: 1px solid #d9d9d9;
}

.more-filter-content_menu_item {
  width: 100%;
  display: flex;
  align-items: center;
  color: #121415;
  cursor: pointer;
  justify-content: space-between;
  padding: 6px 12px;
  transition: all 0.3s;
}

.more-filter-content_menu_item:hover,
.more-filter-content_menu_item.selected {
  background-color: #f2f8ff;
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
  max-width: 350px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;

}
/deep/ .more-filter-button span:nth-child(2) {
  font-weight: 600;
}
/* 按钮组样式 */
.table-page-search-submitButtons {
  display: flex;
  align-items: center;
  // gap: 8px;
}

.table-page-search-submitButtons .ant-btn {
  display: flex;
  align-items: center;
  // gap: 4px;
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
