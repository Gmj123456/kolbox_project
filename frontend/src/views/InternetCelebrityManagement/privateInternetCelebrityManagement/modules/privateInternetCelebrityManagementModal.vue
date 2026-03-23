<template>
  <a-modal
    :title="title"
    :width="1000"
    v-model:open="visible"
    :maskClosable="false"
    centered
    :confirmLoading="confirmLoading"
    @ok="handleOk(-1)"
    @cancel="handleCancel"
    class="StoreCelebrityPrivateModalNewModal"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef" :rules="rules">
        <a-row :gutter="12">
         
          <a-col :span="8" class="avatar-container">
            <div class="colum_img">
              <div class="colum_avatarUrl">
                <a-tooltip placement="top" overlayClassName="colum_avatarUrl_tooltip">
                  <template #title>
                    <img v-if="model.avatarUrl" :src="getImage(model.avatarUrl)" class="input-width-full" />
                    <img v-else src="@/assets/images/avatar.png" class="avatar-img" />
                  </template>
                  <img
                    v-if="model.avatarUrl"
                    :src="getImage(model.avatarUrl)"
                    :class="{ 'error-img': model.isAbnormalAccount == 1, 'avatar-preview': true }"
                    :preview="model.id"
                  />
                  <img
                    v-else
                    :class="{ 'error-img': model.isAbnormalAccount == 1, 'avatar-round': true }"
                    src="/@/assets/images/avatar.png"
                  />
                </a-tooltip>
               
                  <img
                    v-if="model.platformType == 0"
                    class="platformType-img"
                    src="@/assets/images/ins.png"
                    alt=""
                  />
                  <img
                    v-if="model.platformType == 1"
                    class="platformType-img"
                    src="@/assets/images/yt.png"
                    alt=""
                  />
                  <img
                    v-if="model.platformType == 2"
                    class="platformType-img"
                    src="@/assets/images/tk.png"
                    alt=""
                  />
                <div  style="position: absolute; top: -22px; transform: translateX(-3px)">
                  <a-tag
                    v-if="model.isTopStar !== 0"
                    :style="{
                      color: 'white',
                      backgroundColor: parseCelebritySegmentColor(model.isTopStar),
                      border: '1px solid ' + parseCelebritySegmentColor(model.isTopStar),
                      lineHeight: '16px',
                    }"
                  >
                    <span>{{ parseCelebritySegment(model.isTopStar) }}</span>
                  </a-tag>
                </div>
              </div>
              <div class="colum_account">
                <div
                  style="display: flex; align-items: center;"
                >
                  <a
                    @click.stop.prevent="openLink(model)"
                    @mouseenter="$event.target.style.textDecoration = 'underline'"
                    @mouseleave="$event.target.style.textDecoration = 'none'"
                    class="text-link"
                  >
                    {{ model.account }}
                    <!-- <JEllipsis :value="model.account" :length="13"></JEllipsis> -->
                    <!-- <EllipsisTooltip :text="model.account" :lines="1"></EllipsisTooltip> -->
                    <!-- <EllipsisTooltip :text="model.account"></EllipsisTooltip> -->
                  </a>
                    <span>
                      <img
                        v-show="model.gender == 0"
                        src="@/assets/images/man.png"
                        class="gender-icon"
                        alt=""
                      />
                      <img
                        v-show="model.gender == 1"
                        src="@/assets/images/woman.png"
                        class="gender-icon"
                        alt=""
                      />
                    </span>
                </div>
                <div class="flex-between">
                  <div class="flex-row">
                    <a-tooltip title="粉丝数">
                      <span class="icon iconfont icon-fensishu icon-style"></span>
                    </a-tooltip>
                    <span class="flex-center">{{
                      model.followersNum !== null && model.followersNum !== ""
                        ? getFollower(model.followersNum)
                        : "--"
                    }}</span>
                  </div>
                  <div class="flex-row">
                    <a-tooltip title="均播数">
     
                      <span class="icon iconfont icon-bofang1 icon-style"></span>
                    </a-tooltip>
                    <span
                      v-if="model.videoData && isEmptyObject(model.videoData)"
                      class="flex-center"
                    >
                      {{
                        model.videoData && model.videoData !== "{}"
                          ? isPinned
                            ? jsonParseTotal(model.videoData).play_avg_num
                              ? getFollower(
                                  jsonParseTotal(model.videoData).play_avg_num
                                )
                              : "--"
                            : jsonParseNonPinned(model.videoData).play_avg_num
                            ? getFollower(
                                jsonParseNonPinned(model.videoData).play_avg_num
                              )
                            : "--"
                          : "--"
                      }}
                    </span>
                    <span v-else class="flex-center"> --</span>
                  </div>
                  <div class="flex-row">
                    <a-tooltip title="互动率">
                      <span class="icon iconfont icon-icon_hudong-xian icon-style"></span>
                    </a-tooltip>
                    <a-tooltip>
                      <template #title>
                        <div v-if="model.videoData && isEmptyObject(model.videoData)">
                          <div>{{ isPinned ? "包含置顶" : "不包含置顶" }}</div>
                          <template v-if="model.platformType == 1">
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
                                    jsonParseTotal(model.videoData).video_play_count
                                  )
                                }}
                              </div>
                              <div>
                                收藏数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_collect_count
                                  )
                                }}
                              </div>
                              <div>
                                评论数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_comment_count
                                  )
                                }}
                              </div>
                              <div>
                                视频数：{{
                                  getFollower(jsonParseTotal(model.videoData).video_count)
                                }}
                              </div>
                              <div>
                                点赞数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_digg_count
                                  )
                                }}
                              </div>
                              <div>
                                分享数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_share_count
                                  )
                                }}
                              </div>
                              <div>
                                播放区间：
                                {{
                                  getFollower(
                                    jsonParseTotal(model.videoData).play_min_num
                                  )
                                }}
                                -
                                {{
                                  getFollower(
                                    jsonParseTotal(model.videoData).play_max_num
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
                                    jsonParseTotal(model.videoData).video_play_count
                                  )
                                }}
                              </div>
                              <div>
                                收藏数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_collect_count
                                  )
                                }}
                              </div>
                              <div>
                                评论数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_comment_count
                                  )
                                }}
                              </div>
                              <div>
                                视频数：{{
                                  getFollower(jsonParseTotal(model.videoData).video_count)
                                }}
                              </div>
                              <div>
                                点赞数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_digg_count
                                  )
                                }}
                              </div>
                              <div>
                                分享数：{{
                                  getFollower(
                                    jsonParseTotal(model.videoData).video_share_count
                                  )
                                }}
                              </div>
                              <div>
                                播放区间：
                                {{
                                  getFollower(
                                    jsonParseTotal(model.videoData).play_min_num
                                  )
                                }}
                                -
                                {{
                                  getFollower(
                                    jsonParseTotal(model.videoData).play_max_num
                                  )
                                }}
                              </div>
                            </template>
                            <template v-else>
                              <div>
                                播放数：{{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData).video_play_count
                                  )
                                }}
                              </div>
                              <div>
                                收藏数：{{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData)
                                      .video_collect_count
                                  )
                                }}
                              </div>
                              <div>
                                评论数：{{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData)
                                      .video_comment_count
                                  )
                                }}
                              </div>
                              <div>
                                视频数：{{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData).video_count
                                  )
                                }}
                              </div>
                              <div>
                                点赞数：{{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData).video_digg_count
                                  )
                                }}
                              </div>
                              <div>
                                分享数：{{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData).video_share_count
                                  )
                                }}
                              </div>
                              <div>
                                播放区间：
                                {{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData).play_min_num
                                  )
                                }}
                                -
                                {{
                                  getFollower(
                                    jsonParseNonPinned(model.videoData).play_max_num
                                  )
                                }}
                              </div>
                            </template>
                          </template>
                        </div>
                      </template>
                      <a
                        style="display: flex; align-items: center"
                        v-if="model.videoData && isEmptyObject(model.videoData)"
                      >
                        <template v-if="model.platformType == 1">
                          {{
                            isPinned
                              ? "--"
                              : jsonParseTotal(
                                  model.videoData
                                ).video_engagement_rate.toFixed(0) + "%"
                          }}
                        </template>
                        <template v-else>
                          {{
                            isPinned
                              ? jsonParseTotal(
                                  model.videoData
                                ).video_engagement_rate.toFixed(0)
                              : jsonParseNonPinned(
                                  model.videoData
                                ).video_engagement_rate.toFixed(0)
                          }}%
                        </template>
                      </a>
                    </a-tooltip>
                    <span
                      class="flex-center"
                      v-if="!model.videoData && !isEmptyObject(model.videoData)"
                    >
                      --
                    </span>
                  </div>
                </div>
                <div class="flex-center" style="white-space: nowrap">
                  <span class="margin-right-6">{{ model.countryName }}</span>
                  <span> {{ parseDate(model.contractTime) }}</span>
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="16" >
            <a-row :gutter="12">
              <a-col :span="12">
                <a-form-item >
                  <a-input-group compact>
                    <a-input class="input-label" disabled default-value="网红邮箱" />
                    <a-input
                      :disabled="model.id || showDisabled ? true : false"
                      v-model:value="model.email"
                      class="input-width"
                    />
                  </a-input-group>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item  >
                  <a-input-group compact>
                    <a-input class="input-label" disabled default-value="建联邮箱" />
                    <a-select
                      class="input-width"
                      :disabled="model.id || showDisabled ? true : false"
                      v-model:value="model.celebrityContactEmail"
                      show-search
                    ></a-select>
                  </a-input-group>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="12">
            
              <a-col :span="12">
                <a-form-item >
                  <a-input-group compact>
                    <a-input class="input-label" disabled default-value="地址" />
                    <a-input
                      :disabled="showDisabled"
                      v-model:value="model.fullAddress"
                      class="input-width"
                    ></a-input>
                  </a-input-group>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item >
                  <a-input-group compact>
                    <a-input class="input-label" disabled default-value="电话" />
                    <a-input
                      class="input-width"
                      :disabled="showDisabled"
                      v-model:value="model.celebrityTel"
                    />
                  </a-input-group>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="12">
              <a-col :span="24">
              <a-form-item >
                <a-input-group compact>
                  <a-input class="input-label" disabled default-value="备注" />
                  <a-input v-model:value="model.remark" class="input-width" :maxlength="50" />
                </a-input-group>
              </a-form-item>
            </a-col>
            </a-row>
          </a-col>
         
        </a-row>
        <a-row
          :gutter="12"
          v-if="model.attributeList && model.attributeList.length > 0"
          class="margin-top-6"
        >
          <a-col :span="24">
            <a-form-item>
              <a-input-group compact>
                <a-input
                  disabled
                  default-value="达人类型"
                  class="input-label-80"
                  :maxTagCount="5"
                ></a-input>
                <a-select
                  class="attribute_select input-width-80"
                  disabled
                  v-model:value="model.attributeList"
                  mode="multiple"
                >
                  <a-select-option v-for="item in attributeList" :key="item">
                    {{ item }}
                  </a-select-option>
                </a-select>
              </a-input-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item>
              <a-select
                :open="false"
                :showArrow="false"
                allowClear
                mode="multiple"
                :maxTagCount="10"
                v-model:value="model.personalTags"
                :getPopupContainer="(triggerNode) => triggerNode.parentNode"
                class="input-width-full"
                placeholder="请选择个性化标签"
              >
                <a-select-option v-for="item in personalTagsList" :key="item.id">{{
                  item.tagName
                }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-item>
              <div class="tags-container">
                <a-tag
                  @click="handlePersonalTags(item.id)"
                  :style="{
                    borderColor: model.personalTags.includes(item.id) ? '#3155ED' : '',
                    color: model.personalTags.includes(item.id) ? '#3155ED' : '',
                  }"
                  v-for="item in personalTagsList"
                  :key="item.id"
                  >{{ item.tagName }}</a-tag
                >
              </div>
            </a-form-item>
          </a-col>
        </a-row>
       
        <a-row :gutter="12">
          <a-col :xs="24" :sm="24">
            <h1 class="tou margin-top-6">
              <div class="line"></div>
              历史合作
            </h1>
          </a-col>
        </a-row>
        <a-row
          ref="collaborativeProductsRef"
          
          class="table-scroll"
        >
          <a-col
            class="padding-0-6px"
            :span="12"
            v-for="(item, index) in model.collaborativeProducts"
            :key="index"
          >
            <a-form-item >
              <a-input-group compact>
                <a-select
                  style="border-right: 0px;"
                  class="input-width-130"
                  show-search
                  allowClear
                  option-filter-prop="label"
                  v-model:value="item.brandId"
                  :placeholder="'品牌' + (index + 1)"
                  @change="(v) => brandChange(v, index)"
                >
                  <a-select-option v-for="brand in brandList" :key="brand.id" :value="brand.id" :label="brand.brandName">{{
                    brand.brandName
                  }}</a-select-option>
                </a-select>
                <a-select
                  class="input-width-calc-counselor"
                  showSearch
                  option-filter-prop="label"
                  v-model:value="item.productId"
                  placeholder="合作产品"
                >
                  <a-select-option v-for="product in item.productList" :key="product.id" :value="product.id" :label="product.text">{{
                    product.text
                  }}</a-select-option>
                </a-select>
                <a-select
                  class="input-width-130"
                  style="border-right: 0px;"
                  show-search
                  allowClear
                  option-filter-prop="label"
                  v-model:value="item.celebrityCounselorId"
                  placeholder="网红顾问"
                >
                  <a-select-option v-for="celebrityConsultant in celebrityConsultantList" :key="celebrityConsultant.id" :value="celebrityConsultant.id" :label="celebrityConsultant.username">{{
                    celebrityConsultant.username
                  }}</a-select-option>
                </a-select>
                <span class="margin-left-10 margin-top-8">
                  <a>
                    <MinusCircleOutlined
                      class="icon-action-red"
                      @click="removeProduct(index)"
                    />
                  </a>
                  <a v-if="index === model.collaborativeProducts.length - 1">
                    <PlusCircleOutlined class="icon-action" @click="addProduct(index)" />
                  </a>
                </span>
              </a-input-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <a-row :gutter="12" v-if="model.id">
        <a-col :xs="24" :sm="24">
          <h1 class="tou margin-top-6">
            <div class="line"></div>
            历史报价
          </h1>
        </a-col>
      </a-row>
      <div v-if="model.id" class="table-container">
        <a-table
          size="small" 
          :rangeSelection="false"
          :loading="loading"
          :columns="consultantColumns"
          :scroll="{ y: 200 }"
          :pagination="false"
          :data-source="consultantList"
          :rowkey="record => record.id"
          bordered
        >
        <template #bodyCell="{text, record, index, column, key }">
          <template v-if="column.key == 'index'">
            <span>{{ index + 1 }}</span>
          </template>
          <template v-if="column.key == 'celebrityCounselorName'">
          
            <span>{{ record.celebrityCounselorName }}</span>
          </template>
          <template v-if="column.key == 'contractInfo'">
            <template v-if="record.contractInfo && record.contractInfo.length > 0">
              <div v-for="(item, index) in JSON.parse(record.contractInfo)" :key="index">
                <span> {{ item.videoTag }}:${{ item.contractAmount || 0 }}</span>
              </div>
            </template>
            <span v-else>--</span>
          </template>
          <template v-if="column.key == 'email'">
            <div>{{ record.email || "--" }}</div>
            <div>
              <span>{{ record.celebrityContactEmail || "--" }}</span>
            </div>
          </template>
          <template v-if="column.key == 'contractTime'">
            <span>{{ contractTimeParse(record.contractTime) || "--" }}</span>
          </template>
          <template v-if="column.key == 'remark'">
            <span>{{ record.remark || "--" }}</span>
          </template>
          <template v-if="column.key == 'action'">
            <a-tooltip title="编辑">
              <a   :disabled="isleaveAuth(record)" @click="handleEdit(record)">
                <FormOutlined style="font-size: 15px" />
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-popconfirm  :disabled="isleaveAuth(record)" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a-tooltip title="删除">
                <a  :disabled="isleaveAuth(record)">
                  <span class="icon iconfont icon-shanchu"></span>
                </a>
              </a-tooltip>
            </a-popconfirm>
          </template>
        </template>

        </a-table>
    
      </div>
    </a-spin>
    <consultant-modal ref="modalForm" @ok="formOk"></consultant-modal>
  </a-modal>
</template>
<script setup>
import { cloneDeep } from 'lodash-es';
import { getDictItems } from '@/api/common/api';
import { getCommonCountryApi, getPersonalTagsListApi, getBrandListApi, getProductListApi, queryAllCelebrityCounselorApi } from '/@/api/sys/systemCommon';
import { ref, reactive, computed, nextTick } from 'vue';
import { useUserStore } from '/@/store/modules/user';
import { usePermission } from '/@/hooks/web/usePermission';
import { ajaxGetDictItems } from '/@/utils/dict/index';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import dayjs from 'dayjs';
import ConsultantModal from './consultanModal.vue';
// import EllipsisTooltip from '/@/components/jeecg/EllipsisTooltip.vue';
import { MinusCircleOutlined, PlusCircleOutlined, FormOutlined } from '@ant-design/icons-vue';

const { createMessage: $message } = useMessage();
const { isDisabledAuth } = usePermission();
const userStore = useUserStore();

const userInfo = computed(() => userStore.getUserInfo);

// 响应式数据
const title = ref('新增');
const visible = ref(false);
const confirmLoading = ref(false);
const originalAccount = ref('');
const countrys = ref([]);
const videoTagsList = ref([]);
const consultantList = ref([]);
const contactEmailList = ref([]);
const productList = ref([]);
const celebritySegmentList = ref([]);
const attributeList = ref([]);
const isPinned = ref(false);
const loading = ref(false);
const showDisabled = ref(false);
const brandList = ref([]);
const celebrityConsultantList = ref([]);
const personalTagsList = ref([]);
const formRef = ref(null);
const modalForm = ref(null);
const consultantColumns = ref([
  {
      title: '#',
      fixed: 'left',
      width: 50,
      align: 'center',
      key: 'index',
    
    },
  
  {
    title: '顾问',
    dataIndex: 'celebrityCounselorName',
    width: 100,
    key: 'celebrityCounselorName',
  },
  {
    title: '内容报价',
    width: 170,
    dataIndex: 'contractInfo',
    key: 'contractInfo',
  },
  {
    title: '网红邮箱/建联邮箱',
    dataIndex: 'email',
    key: 'email',
  },
  {
    title: '建联时间',
    dataIndex: 'contractTime',
    key: 'contractTime',
    width: 100,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    width: 100,
  },
  
  {
    title: '操作',
    dataIndex: 'action',
    key: 'action',
   
    width: 100,
    align: 'center',
  },
]);
const collaborativeProductsRef = ref(null);

let model = ref({});
// 表单验证规则
const rules = reactive({

  

  videoTag: [
    {
      required: true,
      message: '请选择内容',
      trigger: 'change',
    },
  ],
  contractTime: [
    {
      required: true,
      message: '请选择签约日期',
      trigger: 'change',
    },
  ],
  email: [
    {
      required: true,
      message: '请输入网红邮箱',
      trigger: 'change',
    },
  ],
  celebrityContactEmail: [
    {
      required: true,
      message: '请输入建联邮箱',
      trigger: 'change',
    },
  ],
 
});

const url = {
  add: '/storeCelebrityPrivate/storeCelebrityPrivate/add',
  edit: '/storeCelebrityPrivate/storeCelebrityPrivate/edit',
  delete: '/privateCounselor/delete',
};

// 定义 emits
const emit = defineEmits(['close', 'ok']);

// 方法
function openLink(record) {
  if (record.platformType == '1') {
    window.open(`https://www.youtube.com/@${record.account}/videos`);
  } else if (record.platformType == '2') {
    window.open(`https://www.tiktok.com/@${record.account}`);
  } else if (record.platformType == '0') {
    window.open(`https://www.instagram.com/${record.account}`);
  }
}

function jsonParseNonPinned(jsonStr) {
  const obj = JSON.parse(jsonStr).non_pinned;
  return obj;
}

function parseDate(date) {
  if (date) {
    return dayjs(date).format('YYYY-MM-DD');
  } else {
    return '-';
  }
}

async function getCommonCountry() {
  const res = await getCommonCountryApi({});
  countrys.value = res || [];
}
async function getPersonalTagsList() {
  const res = await getPersonalTagsListApi({});
  personalTagsList.value = res || [];
}
  
async function getCelebritySegmentList() {
    const res = await getDictItems('celebrity_segment');
    celebritySegmentList.value = res;
   
  }

function isEmptyObject(obj) {
  if (obj) {
    return Object.keys(obj).length !== 0;
  } else {
    return false;
  }
}

function getImage(url) {
    return url.includes("https") || url.includes("http")
      ? url
      : 'https://gemstone-image.kolbox.com/avatar/' + url;
  }

function parseCelebritySegmentColor(text) {
  const result = celebritySegmentList.value.filter((item) => item.value == text)[0];
  return result ? result.description : '';
}

function parseCelebritySegment(text) {
  const result = celebritySegmentList.value.filter((item) => item.value == text)[0];
  return result ? result.title : '';
}

function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    const kValue = (num / 1000).toFixed(0);
    return `${kValue}K`;
  } else if (num >= 1000000) {
    const mValue = (num / 1000000).toFixed(0);
    return `${mValue}M`;
  } else if (num > 0 && num < 1000) {
    return num;
  } else {
    return '--';
  }
}

function handlePersonalTags(id) {
  if (model.value.personalTags && model.value.personalTags.includes(id)) {
    model.value.personalTags = model.value.personalTags.filter((item) => item !== id);
  } else {
    if (!model.value.personalTags) {
      model.value.personalTags = [];
    }
    model.value.personalTags.push(id);
  }
}

function jsonParseTotal(jsonStr) {
  const obj = JSON.parse(jsonStr).total;
  return obj;
}

function _selectPlatform(value) {
  console.log(value);
  getVideoTagsList();
  model.value.contractInfo = [
    {
      videoTag: undefined,
      contractAmount: undefined,
    },
    {
      videoTag: undefined,
      contractAmount: undefined,
    },
  ];
}

function checkProductCooperateIds() {
  const productIds = model.value.collaborativeProducts
    .map((item) => item.productId)
    .filter(Boolean);
  const hasDuplicate = new Set(productIds).size !== productIds.length;
  const hasEmptyProductId = model.value.collaborativeProducts.some(
    (item) => item.brandId && !item.productId
  );
  return {
    hasDuplicate,
    hasEmptyProductId,
  };
}

function getBrandProduct(index) {
  const filteredProductList = productList.value.filter(
    (item) => item.brandId === model.value.collaborativeProducts[index].brandId
  );
  model.value.collaborativeProducts[index].productList = filteredProductList;
  if (filteredProductList.length == 1) {
    model.value.collaborativeProducts[index].productId = filteredProductList[0].id;
  }
}

function addProduct(_index) {
  model.value.collaborativeProducts.push({
    brandId: undefined,
    productId: undefined,
    celebrityCounselorId: undefined,
    productList: [],
  });
  nextTick(() => {
    const ref = collaborativeProductsRef.value;
    if (ref && ref.$el) {
      ref.$el.scrollTop = ref.$el.scrollHeight;
    }
  });
}

function removeProduct(index) {
  if (model.value.collaborativeProducts.length == 1) {
    model.value.collaborativeProducts[0].productId = undefined;
    model.value.collaborativeProducts[0].productList = [];
    model.value.collaborativeProducts[0].brandId = undefined;
    model.value.collaborativeProducts[0].celebrityCounselorId = undefined;
    return;
  } else {
    model.value.collaborativeProducts.splice(index, 1);
  }
}

function brandChange(value, index) {
  model.value.collaborativeProducts[index].productId = undefined;
  model.value.collaborativeProducts[index].productList = [];
  if (value) {
    getBrandProduct(index);
  }
}

async function initBrandList() {
  const res = await getBrandListApi({});
  brandList.value = res || [];
}

async function queryAllCelebrityCounselor() {
  const res = await queryAllCelebrityCounselorApi({});
  celebrityConsultantList.value = Array.isArray(res) ? res : [];
}
function checkContentItemFilled() {
  const hasAtLeastOneItem = model.value.contractInfo.some(
    (item) => item.videoTag || item.contractAmount
  );
  if (!hasAtLeastOneItem) {
    $message.warning(`至少填写一项内容和报价`);
    return false;
  }

  for (let i = 0; i < model.value.contractInfo.length; i++) {
    const item = model.value.contractInfo[i];
    console.log(item);
    if (item.videoTag || item.contractAmount) {
      if (!item.videoTag) {
        $message.warning(`请选择内容`);
        return false;
      }
      if (!item.contractAmount) {
        $message.warning(`请填写签约费用`);
        return false;
      }
    }
  }
  return true;
}

function checkVideoTagsDuplicate() {
  const videoTagsSet = new Set();
  for (const item of model.value.contractInfo) {
    if (videoTagsSet.has(item.videoTag)) {
      return false;
    }
    videoTagsSet.add(item.videoTag);
  }
  return true;
}

function addContent(_index) {
  if (model.value.contractInfo.length == videoTagsList.value.length) {
    $message.warning('最多添加' + videoTagsList.value.length + '个内容');
    return;
  }
  model.value.contractInfo.push({
    videoTag: undefined,
    contractAmount: undefined,
  });
}

function removeContent(index) {
  if (model.value.contractInfo.length == 1) {
    $message.warning('请至少添加一个内容');
    return;
  }
  model.value.contractInfo.splice(index, 1);
}
function _roundValue(field) {
  if (model.value[field]) {
    let num = Number(model.value[field]);
    if (isNaN(num)) {
      model.value[field] = undefined;
      return;
    }
    if (num > 999999) {
      num = 999999;
    }
    model.value[field] = num;
  }
}

async function initProductList() {
  const res = await getProductListApi({});
  const list = res || [];
  productList.value = list.map((item) => ({
    ...item,
    text: item.productName,
  }));

    if (model.value.id) {
      const collaborativeProducts = [];
      if (model.value.productCooperateIds) {
        JSON.parse(model.value.productCooperateIds).forEach((item) => {
          collaborativeProducts.push({
            brandId: item.brandId,
            productId: item.productId,
            celebrityCounselorId: item.celebrityCounselorId,
            productList: productList.value.filter(
              (product) => product.brandId === item.brandId
            ),
          });
        });
      } else {
        collaborativeProducts.push({
          brandId: undefined,
          productId: undefined,
          celebrityCounselorId: undefined,
          productList: [],
        });
        collaborativeProducts.push({
          brandId: undefined,
          productId: undefined,
          celebrityCounselorId: undefined,
          productList: [],
        });
      }
      model.value.collaborativeProducts =
        collaborativeProducts.length > 0
          ? collaborativeProducts
          : [{ brandId: undefined, productId: undefined, celebrityCounselorId: undefined, productList: [] }];
    }
    console.log(model.value.collaborativeProducts);
}
function _filterTreeNode(inputValue, treeNode) {
  if (!inputValue) return true;
  const title = treeNode.data.props.categoryName || treeNode.data.props.title;
  return title.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0;
}

function _handleContractAmount(_rule, value, callback) {
  if (null === value || value === '' || value === undefined) {
    callback(' ');
  } else {
    if (value > 0) {
      callback();
    } else {
      callback(new Error('正确的费用'));
    }
  }
}



async function handleOk(isAbnormalAccount) {
  const personalTagIdName = personalTagsList.value
    .filter((item) => model.value.personalTags && model.value.personalTags.includes(item.id))
    .map((item) => item.tagName)
    .join(',');
  if (personalTagIdName.length == 0) {
    $message.warning('请选择个性化标签');
    return;
  }
  if (personalTagIdName.length > 1 && personalTagIdName.includes('无')) {
    $message.warning('请检查选择的个性化标签，"无"不可以与其他标签同时选择');
    return;
  }

  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (isAbnormalAccount < 0) {
      if (!model.value.id) {
        httpurl += url.add;
        method = 'post';
      } else {
        httpurl += url.edit;
        method = 'put';
      }
    } else {
      httpurl += '/storeCelebrityPrivate/storeCelebrityPrivate/verifyCelebrityInfo';
      method = 'post';
    }
    let formData = Object.assign({}, model.value);

    if (!model.value.id && !checkContentItemFilled()) {
      confirmLoading.value = false;
      return;
    }
    if (!model.value.id && !checkVideoTagsDuplicate()) {
      $message.warning('内容标签不能重复');
      confirmLoading.value = false;
      return;
    }
    for (let i = 0; i < model.value.collaborativeProducts.length; i++) {
      const cpItem = model.value.collaborativeProducts[i];
      const hasBrand = !!cpItem.brandId;
      const hasProduct = !!cpItem.productId;
      const hasCounselor = !!cpItem.celebrityCounselorId;
      if (!hasBrand && !hasProduct && !hasCounselor) continue;
      if (!hasBrand) {
        $message.warning(`请选择历史合作${i + 1}的品牌`);
        confirmLoading.value = false;
        return;
      }
      if (!hasProduct) {
        $message.warning(`请选择历史合作${i + 1}的合作产品`);
        confirmLoading.value = false;
        return;
      }
      if (!hasCounselor) {
        $message.warning(`请选择历史合作${i + 1}的网红顾问`);
        confirmLoading.value = false;
        return;
      }
    }
    const { hasDuplicate } = checkProductCooperateIds();
    if (hasDuplicate) {
      $message.warning('合作产品不能重复');
      confirmLoading.value = false;
      return;
    }
    if (!model.value.id) {
      formData.contractInfo = JSON.stringify(
        model.value.contractInfo.filter((item) => item.videoTag && item.contractAmount)
      );
    }
    delete formData.privateContractInfoList;
    delete formData.tiktokCelebrityProductList;
    delete formData.isPersonalTagsConfirmed;
    if (model.value.id) {
      delete formData.email;
      delete formData.contractInfo;
    }
    if (!model.value.videoData) {
      delete formData.videoData;
    }

    formData.personalTagIds =
      model.value.personalTags && model.value.personalTags.length > 0 ? model.value.personalTags.join(',') : '';
    delete formData.personalTags;
    formData.privateProducts = model.value.collaborativeProducts
      .filter(item => item.brandId || item.productId || item.celebrityCounselorId)
      .map(item => ({
        brandId: item.brandId,
        productId: item.productId,
        celebrityCounselorId: item.celebrityCounselorId,
        celebrityCounselorName: celebrityConsultantList.value.find(c => c.id === item.celebrityCounselorId)?.username,
      }));
    delete formData.productCooperateIds;
    formData.countryName = countrys.value.filter((item) => item.id === model.value.countryId)[0]
      ?.country;

    formData.contractTime = formData.contractTime
      ? formData.contractTime.format('YYYY-MM-DD HH:mm:ss')
      : null;
    formData.nickName = formData.account;
    if (model.value.id) {
      delete formData.celebrityContactEmail;
    } else {
      const contactEmail = contactEmailList.value.find(
        (item) => item.id === model.value.celebrityContactEmail
      );
      if (contactEmail) {
        formData.celebrityContactEmail = contactEmail.contactEmail;
      }
    }

    if (originalAccount.value !== formData.nickName) {
      formData.isAbnormalAccount = 2;
    }
    formData.dataList = formData.dataList ? formData.dataList : [];

    const res = await defHttp.request({
      url: httpurl,
      method,
      data: formData,
    });

    close();
      emit('ok');
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    confirmLoading.value = false;
  }
}
function _changeAccount(_e) {
  if (model.value.account && model.value.platformType >= 0) {
    getCelebrityPrivateData(model.value.account, model.value.platformType);
  }
}

function changeCountry(val) {
  if (val == '马来西亚') {
    rules.celebrityTel[0].required = true;
    rules.email[0].required = false;
    formRef.value?.resetFields(['email']);
  } else {
    rules.celebrityTel[0].required = false;
    rules.email[0].required = true;
  }
}

async function getCelebrityPrivateData(account, platformType) {
  const res = await defHttp.get({
    url: '/storeCelebrityPrivate/storeCelebrityPrivate/getCelebrityPrivateData',
    params: {
      account,
      platformType,
    },
  });
  if (res.success && res.result) {
    console.log(res.result);
    const info = res.result;
    model.value.countryId = info.countryId;
    model.value.isTopStar = info.isTopStar;
    model.value.countryName = info.countryName;
    model.value.platformType = info.platformType;
    model.value.account = info.account;
    model.value.gender = info.gender;
    model.value.followersNum = info.followersNum * 1000 ;
    model.value.playAvgNum = info.playAvgNum * 1000;
    model.value.email = info.email;
    model.value.celebrityTel = info.celebrityTel;
    model.value.fullAddress = info.fullAddress;
    model.value.remark = info.remark;
    changeCountry(info.countryName);
  }
}
function handleIntger(_rule, value, callback) {
  if (!value) {
    callback();
  } else {
    if (isNaN(value)) {
      callback('数字');
    } else if (value < 0) {
      callback('不能输入负数');
    } else if (new RegExp(/^(([1-9]{1}\d*)|(0{1}))(\.\d{0,2})?$/).test(value)) {
      callback();
    } else {
      callback('最多允许小数点后两位');
    }
  }
}
function formOk() {
  consultantList.value = []
  queryByCelebrityPrivateId(model.value.id);
}

function handleEdit(record) {
  modalForm.value?.edit(record, videoTagsList.value);
  if (modalForm.value) {
    modalForm.value.title = '编辑';
  }
}

async function handleDelete(id) {
  if (!url.delete) {
    $message.error('请设置url.delete属性!');
    return;
  }

  loading.value = true;
  try {
    const res = await defHttp.delete({
      url: url.delete,
      data: { id },
    },{joinParamsToUrl:true,isTransformResponse:false});
    if (res.success) {
      $message.success(res.message);
      consultantList.value = [];
      queryByCelebrityPrivateId(model.value.id);
    } else {
      $message.warning(res.message);
    }
  } finally {
    loading.value = false;
  }
}

function isleaveAuth(row) {
    // return false;
 if (userInfo.value.id != row.celebrityCounselorId) {
      return true;
    } else {
      return false;
    }
}

function contractTimeParse(contractTime) {
  if (contractTime) {
    return dayjs(contractTime).format('YYYY-MM-DD');
  } else {
    return '-';
  }
}

function add() {
  edit({
    collaborativeProducts: [
      { brandId: undefined, productId: undefined, celebrityCounselorId: undefined, productList: [] },
      { brandId: undefined, productId: undefined, celebrityCounselorId: undefined, productList: [] },
    ],
    contractInfo: [
      { videoTag: undefined, contractAmount: undefined },
      { videoTag: undefined, contractAmount: undefined },
    ],
    platformType: 2,
    gender: 1,
    personalTags: [],
  });
}

function handleCancel() {
  close();
}

function close() {
  emit('close');
  formRef.value?.clearValidate();
  visible.value = false;
  showDisabled.value = false;
  consultantList.value = []
}

function _checkNum(num) {
  if (num == '' || num == null || num == undefined) {
    return 0;
  }
  return num;
}
function edit(row) {
  visible.value = true;
  consultantList.value = []
  // 使用 Object.assign 或展开运算符创建新对象，避免直接赋值引用
  // 如果需要深拷贝，可以使用：
  model.value = cloneDeep(row);
  celebrityConsultantList.value  = row.privateCounselorList.map(item => ({
       id: item.celebrityCounselorId,
    username: item.celebrityCounselorName,
  }));
  console.log(celebrityConsultantList.value);
  getVideoTagsList();
  // initContactEmail();
  initProductList();
  initBrandList();
  // queryAllCelebrityCounselor();
  getCelebritySegmentList();
  getCommonCountry();
  getPersonalTagsList();

  if (model.value.id) {
    attributeList.value = row.attributeList;
    queryByCelebrityPrivateId(model.value.id);


    model.value.personalTags = model.value.personalTagIds ? model.value.personalTagIds.split(',') : [];
  }
  model.value.contractTime = model.value.contractTime ? dayjs(model.value.contractTime) : dayjs(new Date());

  // nextTick(() => {
  //   setTreeHeight();
  // });
  title.value = row.id?'编辑':'新增';

}

async function queryByCelebrityPrivateId(id) {
  loading.value = true;
  try {
    const res = await defHttp.get({
      url: '/privateCounselor/queryByCelebrityPrivateId',
      params: { id },
    });
    console.log(res);
    consultantList.value = [...res];
    // if (res.success) {
    // }
  } finally {
    loading.value = false;
  }
}

// initCelebrityTag removed


async function initContactEmail() {
  const res = await defHttp.get({ url: '/storeUserContactEmail/queryListByCounselor' });
  contactEmailList.value = res.result.filter((item) => {
    return item.emailStatus;
  });
}

async function getVideoTagsList() {
  const res = await ajaxGetDictItems('celebrity_content');
  if (res) {
    const filtered = res.filter((item) => item.title == model.value.platformType);
    if (filtered.length > 0) {
      videoTagsList.value = filtered[0].value.split(',');
    }
  }
}
// 暴露方法给父组件
defineExpose({
  edit,
  add,
});
</script>
<style>

.table-scroll .ant-form-item{
  margin: 6px 0px !important;
}

</style>
<style lang="less" scoped>

:deep(.surely-table-row){
  background-color: #fff !important;
}
/deep/.attribute_select .ant-select-disabled .ant-select-selection {
  background-color: #fff !important;
}
/deep/.ant-select-disabled .ant-select-selection--multiple .ant-select-selection__choice {
  color: rgba(0, 0, 0, 0.65);
  background-color: #fafafa;
}
.colum_img {
  display: flex;
  align-items: center;
  border: 1px solid #d9d9d9 !important;
  padding: 0px 6px;
  border-radius: 10px;
  width: 100%;
}
.platformType-img {
  position: absolute;
    left: 79px;
    bottom: 15px;
  display: inline-block;
  width: 24px;
  height: 24px;

  img {
    width: 100%;
    height: 100%;
  }
}
.colum_avatarUrl {
  width: 90px;
  height: 90px;
  line-height: 90px;
  margin-right: 20px;
  display: flex;

  // .colum_tag {
  //   position: absolute;
  //   top: -5px;
  //   left: -5px;
  //   font-size: 10px;
  //   line-height: 15px;
  // }

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
.colum_account {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: calc(100% - 70px - 20px);
  color: #0b1019 !important;
  font-size: 12px;
}
.StoreCelebrityPrivateModalNewModal .ant-modal-body {
  max-height: 800px !important;
  overflow-y: auto !important;
  overflow-x: hidden !important;
  padding: 12px 24px !important;
}
/deep/ .ant-select-dropdown--multiple .ant-select-dropdown-menu-item-selected {
  background-color: #f2f8ff !important;
}
/deep/ .ant-radio-button-wrapper {
  padding: 0px !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}
.tou {
  display: flex;
  font-size: 14px;
  align-items: center;

  .line {
    width: 3px;
    height: 15px;
    background: #3155ed;
    margin-right: 7px;
  }
}

/deep/ .ant-calendar-picker input {
  font-size: 12px !important;
}

.gender-icon {
  width: 22px;
  height: 22px;
}

/* 常用布局类 */
.flex-center {
  display: flex;
  align-items: center;
}

.flex-column {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.flex-between {
  display: flex;
  align-items: center;
  white-space: nowrap;
  margin: 4px 0;
  justify-content: space-between;
}

.flex-row {
  display: flex;
  height: 100%;
  margin-right: 0px;
}

/* 头像相关 */
.avatar-img {
  width: 90px;
  height: 90px;
  margin: 0 auto;
}

.avatar-round {
  border-radius: 100%;
  width: 90px;
  height: 90px;
  margin: 0 auto;
}

.avatar-preview {
  height: 90px;
  border-radius: 100%;
  width: 90px;
  cursor: pointer;
}

.avatar-container {
  height: 114px;
  display: flex;

}

/* 图标样式 */
.icon-style {
  font-size: 18px;
  margin-right: 5px;
}

.icon-action {
  font-size: 18px;
  margin: 0px 8px;
}

.icon-action-red {
  font-size: 18px;
  margin: 0px 8px;
  color: red;
}

/* 表单项 */
.form-item-no-margin {
  margin-bottom: 0px !important;
}

.form-item-small-margin {
  margin-bottom: 3px !important;
}

/* 输入组标签 */
.input-label {
  width: 72px;
  color: #0b1019;
  background-color: #fff;
}

.input-label-80 {
  width: 80px;
  color: #0b1019;
  background-color: #fff;
}

/* 输入框宽度 */
.input-width {
  width: calc(100% - 72px);
}

.input-width-80 {
  width: calc(100% - 80px);
}

.input-width-46 {
  width: 46%;
}

.input-width-130 {
  width: 130px;
}

.input-width-full {
  width: 100%;
}

.input-width-calc {
  width: calc(100% - 130px - 20px - 68px);
}

.input-width-calc-counselor {
  width: calc(100% - 130px - 130px - 20px - 68px);
}

.input-width-content {
  margin-left: 10px;
  width: calc(54% - 100px);
}

/* 间距 */
.margin-top-8 {
  margin-top: 8px;
}

.margin-top-6 {
  margin-top: 6px;
}

.margin-right-6 {
  margin-right: 6px;
}

.margin-left-10 {
  margin-left: 10px;
}

.padding-0-6 {
  padding: 0 6px;
}

.padding-0-6px {
  padding: 0px 6px;
}

.padding-col-16 {
  padding-left: 0px;
  padding-right: 0px;
}

/* 背景色 */
.bg-gray {
  background-color: #f7f8fa;
}

/* 标签容器 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  row-gap: 6px;
  max-height: 174px;
  overflow-y: auto;
  border: 1px solid #e8e8e8;
  padding: 4px;
  border-radius: 4px;
}
.tags-container  .ant-tag{
  font-size: 12px !important;
}
/* 操作按钮容器 */
.action-buttons {
  margin-left: 10px;
  transform: translateY(2px);
  display: inline-block;
}


.table-scroll {
  background-color: #f7f8fa;
  max-height: 132px;
  overflow-y: auto;
}

/* 其他 */
.text-link {
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  max-width: 150px;
}

.input-group-top {
  top: 0px !important;
}
</style>
