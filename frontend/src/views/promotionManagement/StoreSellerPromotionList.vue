<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
      <a-form  @keyup.enter="searchQuery" class="searchForm">
        <a-row :gutter="12">
          <a-col :xl="2" v-if="hasPermission('user:demand')">
            <a-button
              
              @click="handleAdd"
              type="primary"
              class="btn-large"
           
              :icon="h(PlusOutlined)"
              >
           
              新增需求
            </a-button>
          </a-col>
          <a-col :span="2">
            <a-form-item>
              <a-input v-model:value="queryParam.promTitle" placeholder="推广标题"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="2">
            <a-form-item>
              <a-input v-model:value="queryParam.kolAccount" placeholder="网红账号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="2">
            <a-form-item>
              <a-input v-model:value="queryParam.sellerName" placeholder="商家名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="2">
            <a-form-item>
              <a-select
                class="marg"
                v-model:value="queryParam.celebrityPromStatus"
                placeholder="网红进度"
              >
                <a-select-option :value="0">待寄样</a-select-option>
                <a-select-option :value="1">待上线</a-select-option>
                <a-select-option :value="2">已上线</a-select-option>
                <a-select-option :value="3">已完成</a-select-option>
                <a-select-option :value="-1">已终止</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="2"> 
            <a-form-item>
              <a-select
                class="marg"
                v-model:value="queryParam.payStatus"
                placeholder="支付状态"
              >
                <a-select-option :value="0">待支付</a-select-option>
                <a-select-option :value="10">部分支付</a-select-option>
                <a-select-option :value="20">已付全款</a-select-option>
                <a-select-option :value="-1">无需支付</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xl="4">
            <a-form-item>
            
              <a-button type="primary" @click="searchQuery" :icon="h(SearchOutlined)">
               
                查询
              </a-button>
              <a-button style="margin-left: 8px" @click="searchReset" :icon="h(ReloadOutlined)">
             
                重置
              </a-button>
            </a-form-item>
          
        </a-col>
        </a-row>
      </a-form>
 
    <!-- 操作按钮区域 -->

    <!-- table区域-begin -->
    <div>
      <!-------------------------------------------------需求 begin---------------------------------------------------->
      <a-table
       :animate-rows="false"
      :rangeSelection="false"	
        ref="table"
        size="small"
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="pagination"
        :loading="loading"
        :expandIconColumnIndex="0"
        @expand="handleExpand"
        :expanded-row-keys="expandedRowKeys"
        :rowClassName="rowClassName"
        @change="pageChange"
          :expand-column-width="25"
      
       :scroll="{ x: 2300, y: sTableHeight }"
      >
        <template #bodyCell="{ record, column }">
          <!--推广单号-->
          <template v-if="column.key === 'extensionCode'">
            <a @click="copyFn(record.extensionCode)" title="点击复制" style="color: #3155ed">
              {{ record.extensionCode }}
            </a>
          </template>
          <!--推广标题-->
          <template v-else-if="column.key === 'promTitle'">
            <!-- {{ record.promTitle }} -->
            <EllipsisTooltip :text="record.promTitle" />
          </template>
          <!--商家名称-->
          <template v-else-if="column.key === 'sellerName'">
            {{ record.sellerName }}
          </template>
          <!--产品品牌-->
          <template v-else-if="column.key === 'productBrand'">
          
            {{ record.productBrand }}
          </template>
          <!--审核状态-->
          <template v-else-if="column.key === 'approvedStatus'">
            <a-tag color="orange" style="margin-right: 0" v-if="record.approvedStatus === 0">
              等待审核
            </a-tag>
            <a-tag color="blue" style="margin-right: 0" v-if="record.approvedStatus === 1">
              审核通过
            </a-tag>
            <a-tag color="red" style="margin-right: 0" v-if="record.approvedStatus === -1">
              审核驳回
            </a-tag>
            <a-tag color="cyan" style="margin-right: 0" v-if="record.approvedStatus === 2">
              已提方案
            </a-tag>
            <a-tag color="green" style="margin-right: 0" v-if="record.approvedStatus === 3">
              匹配成功
            </a-tag>
          </template>
          <!--平台-->
          <template v-else-if="column.key === 'promPlatform'">
            <span v-if="record.promPlatform === '10'">不限</span>
            <span
              v-else
              style="width: 20px; height: 20px; display: inline-block"
              v-for="(item, idx) in promPlatformArr(record.promPlatform)"
              :key="idx"
            >
              <img :src="item.imgSrc" alt="" style="width: 100%; height: 100%" />
            </span>
          </template>
          <!--类目-->
          <template v-else-if="column.key === 'promTags'">
            <div v-if="record.promTags !== null && record.promTags !== ''">
              <a-tag
                :style="{ background: '#5e78ed', 'margin-bottom': '5px' }"
                color="white"
                style="margin-right: 0"
              >
                {{ record.promTags }}
              </a-tag>
            </div>
          </template>
          <!--网红要求-->
          <template v-else-if="column.key === 'promRequirement'">
            <EllipsisTooltip :text="record.promRequirement" />
          <!-- <JEllipsis :text="record.promRequirement" :length="20" /> -->
          </template>
          <!--案例-->
          <template v-else-if="column.key === 'promExample'">
            <a
              v-for="(item, idx) in record.promExample ? record.promExample.split('\n') : []"
              :key="idx"
              @click="openUrl(item)"
              target="_blank"
              :disabled="record.promExample == ''"
              style="color: #3155ed"
              >{{ item != "" ? "案例" + (idx + 1) + " " : "无" }}</a
            >
          </template>
          <!--推广费用-->
          <template v-else-if="column.key === 'promAmount'">
            <span v-if="record.promAmount">
              <a-tooltip placement="topLeft">
                <template #title>
                  <span v-if="record.promAmount">{{ record.currencySymbol }}{{ record.promAmount }}</span>
                </template>
                <span>¥{{ getPromAmount(record) }}</span>
              </a-tooltip>
            </span>
          </template>
          <!--税费-->
          <template v-else-if="column.key === 'totalTaxAmount'">
            <span v-if="record.totalTaxAmount">${{ splitAmount(record.totalTaxAmount) }}</span>
          </template>
          <!--汇率-->
          <template v-else-if="column.key === 'exchangeRate'">
            <span>{{ record.exchangeRate }}</span>
          </template>
          <!--订单总额-->
          <template v-else-if="column.key === 'totalAmount'">
            <span v-if="record.totalAmount">
              <a-tooltip placement="topLeft">
                <template #title>
                  <span v-if="getTotalAmountRmb(record) > 0"
                    >{{ record.currencySymbol }}{{ record.totalAmount }}</span
                  >
                </template>
                <span v-if="getTotalAmountRmb(record) > 0"
                  >¥{{ getTotalAmountRmb(record) }}</span
                >
              </a-tooltip>
            </span>
          </template>
          <!--已付金额-->
          <template v-else-if="column.key === 'paymentAmount'">
            <a-tooltip placement="topLeft">
              <template #title>
                <span v-if="getPaymentAmount(record) > 0"
                  >{{ record.currencySymbol }}{{ getPaymentAmount(record) }}</span
                >
              </template>
              <span v-if="getAllPayment(record) > 0">¥{{ getAllPayment(record) }} </span>
            </a-tooltip>
          </template>
          <!--退款金额-->
          <template v-else-if="column.key === 'refundAmount'">
            <a-tooltip placement="topLeft">
              <template #title>
                <span v-if="getRefundAmount(record) > 0"
                  >{{ record.currencySymbol }}{{ getRefundAmount(record) }}</span
                >
              </template>
              <span v-if="record.refundAmount > 0">¥{{ record.refundAmount }} </span>
            </a-tooltip>
          </template>
          <!--待付金额-->
          <template v-else-if="column.key === 'paidAmount'">
            <span v-if="getPaidAmount(record, 0) > 0 && getPaidAmount(record, 0) != null">
              <a-tooltip placement="topLeft">
                <template #title>
                  <span> {{ record.currencySymbol }}{{ getPaidAmount(record, 1) }} </span>
                </template>
                <span>¥{{ getPaidAmount(record, 0) }}</span>
              </a-tooltip>
            </span>
          </template>
          <!--创建日期-->
          <template v-else-if="column.key === 'createTime'">
            {{ getDate(record.createTime) }}
          </template>
        </template>
     
        <template #expandedRowRender>
          <div >
            
            <a-table
            :loading="innerLoading"
              :animate-rows="false"
              :rangeSelection="false"	
              size="small"
              :rowKey="
                (record, index) => {
                  return record.id;
                }
              "
            
              :columns="innerColumns"
              :data-source="innerData"
              :pagination="false"
            >
              <template #bodyCell="{ record, column }">
                <!--网红进度-->
                <template v-if="column.key === 'celebrityPromStatus'">
                  <a-tag color="orange" v-if="record.celebrityPromStatus == 0" style="margin-right: 0">
                    待寄样
                  </a-tag>
                  <a-tag color="cyan" v-if="record.celebrityPromStatus == 1" style="margin-right: 0"> 待上线 </a-tag>
                  <a-tag color="blue" v-if="record.celebrityPromStatus == 2" style="margin-right: 0"> 已上线 </a-tag>
                  <a-tag color="green" v-if="record.celebrityPromStatus == 3" style="margin-right: 0"> 已完成 </a-tag>
                  <a-tag color="red" v-if="record.celebrityPromStatus == -1" style="margin-right: 0"> 已终止 </a-tag>
                </template>
                <!--产品名称-->
                <template v-else-if="column.key === 'goodsTitle'">
                  <a
                    @click="openUrl(record.goodsUrl)"
                    target="_blank"
                    style="color: #3155ed; text-decoration: underline"
                  >
        
                    {{ record.goodsTitle }}
                  </a>
                </template>
                <!--产品图片-->
                <template v-else-if="column.key === 'goodsPicUrl'">
                  <img
                    v-if="record.goodsPicUrl"
                    :src="record.goodsPicUrl"
                    style="height: 30px; width: 30px; cursor: pointer"
                  
                  />
                </template>
                <!--网红账号-->
                <template v-else-if="column.key === 'account'">
                  <a
                    class="copy"
                  
                
                    title="点击复制"
                    style="color: #3155ed"
                    >{{ record.account }}</a
                  >
                </template>
                <!--头像-->
                <template v-else-if="column.key === 'avatarUrl'">
                  <img
                    :src="getAvatarUrl(record.avatarUrl)"
                    :preview="record.id"
                    v-if="record.account"
                    style="max-height: 30px; max-width: 30px; cursor: pointer"
                  />
                  <img
                    v-if="record.account && !record.avatarUrl"
                    src="@/assets/images/avatar.png"
                    style="max-width: 30px; max-height: 30px; margin: 0 auto"
                  />
                </template>
                <!--平台-->
                <template v-else-if="column.key === 'platformType'">
                  <img
                    v-if="record.platformType"
                    style="width: 20px; height: 20px"
                    :src="platformTypeImg(record.platformType)"
                    alt=""
                  />
                </template>
                <!--视频类型-->
                <template v-else-if="column.key === 'videoTags'">
      
                  {{ record.videoTags }}
                </template>
                <!--擅长类型-->
                <template v-else-if="column.key === 'promLikeTags'">

                  {{ record.promLikeTags }}
                </template>
                <!--粉丝-->
                <template v-else-if="column.key === 'followersNum'">
                  {{ getFollower(record.followersNum) }}
                </template>
                <!--平均点赞数-->
                <template v-else-if="column.key === 'likeAvgNum'">
                  {{ getFollower(record.likeAvgNum) || "/" }}
                </template>
                <!--均播-->
                <template v-else-if="column.key === 'playAvgNum'">
                  {{ getFollower(record.playAvgNum) || "/" }}
                </template>
                <!--总视频数-->
                <template v-else-if="column.key === 'videoCount'">
                  {{ getFollower(record.videoCount) || "/" }}
                </template>
                <!--总获赞数-->
                <template v-else-if="column.key === 'likeCount'">
                  {{ getFollower(record.likeCount) || "/" }}
                </template>
                <!--推广费用-->
                <template v-else-if="column.key === 'promPrice'">
                  <span v-if="record.promPrice">${{ splitAmount(record.promPrice) }}</span>
                </template>
                <!--推广图视-->
                <template v-else-if="column.key === 'promView'">
                  <a
                    @click="openUrl(item)"
                    :disabled="record.promView == ''"
                    v-for="(item, idx) in record.mediaUploads ? record.mediaUploads.split('\n') : []"
                    style="color: #3155ed"
                    :key="idx"
                    >{{ item != "" ? "链接" + (idx + 1) + " " : "无" }}
                  </a>
                </template>
              </template>
            </a-table>
          </div>
        </template>
        <!--------------------------------------------------产品 end--------------------------------------------------->
      </a-table>
      <!-------------------------------------------------需求 end ----------------------------------------------------->
    </div>

  
    <!-- 表单区域 -->
    <!-- <StoreSellerPromotionModalNew
      ref="modalFormRef"
      @ok="modalFormOk"
    ></StoreSellerPromotionModalNew> -->
    <StoreSellerPromotionModal
      ref="modalFormRef"
      @ok="modalFormOk"
    ></StoreSellerPromotionModal>
  
  </a-card>
</template>

<script setup name="StoreSellerPromotionList">
// import StoreSellerPromotionModalNew from "./modules/StoreSellerPromotionModalNew.vue";
// import StorePromotionGoodsModalNew from "./modules/StorePromotionGoodsModalNew";
// import DiscountCodeModalNew from "./modules/DiscountCodeModalNew";
// import SellerPayModalNew from "./modules/SellerPayModalNew";
// import CelebrityPromMediaModalNew from "./modules/CelebrityPromMediaModalNew";
import StoreSellerPromotionModal from "./modules/StoreSellerPromotionModal.vue";
import { ref, onMounted, nextTick,h } from 'vue';
import dayjs from 'dayjs';
import {
  floatAdd,
  floatDivide,
  floatMultiply,
  floatSubtract,
} from "@/utils/floatUtils";
// import { useStore } from "vuex";
import { useMessage } from '/@/hooks/web/useMessage';
// import { createConfirm } from '/@/utils/helper/confirm';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { queryStoreSellerPromotionListApi, deleteStoreSellerPromotionApi, checkCelebrityStatusForListApi, innerTableListApi, mediaUploadsEditApi } from '/@/api/promotionManagement/promotionManagementApi';
import { useTable } from '/@/components/useSTable/useSTable';
import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
// const store = useStore();
const { createMessage: $message } = useMessage();
import EllipsisTooltip from "@/components/jeecg/EllipsisTooltip.vue";
const discountCodeModalFormRef = ref(null);
const modalFormRef = ref(null);
const modalGoodsFormRef = ref(null);
const storeSellerPromotionGoodsPaymentModalFormRef = ref(null);
const sellerPayFormRef = ref(null);
const mediaModalRef = ref(null);
const currentElementIndex = ref(0);
const copyGoodsWaybill = ref("");
const copyAccount = ref("");
// 表头
const columns = ref([
        {
          title: "#",
          dataIndex: "",
          key: "rowIndex",
          align:"center",
          width: 40,
          // align: 'center',
          customRender: function ({index}) {
            return parseInt(index) + 1;
          },
        },
        {
          title: "推广单号",
          // align: 'center',
          width: 100,
          dataIndex: "extensionCode",
          key: "extensionCode",
        },
        {
          title: "推广标题",
          // align: 'center',
          width: 100,
          dataIndex: "promTitle",
          key: "promTitle",
        },
        {
          title: "商家名称",
          // align: 'center',
         width: 100,
          dataIndex: "sellerName",
          key: "sellerName",
        },
        {
          title: "产品品牌",
          // align: 'center',
         width: 120,
          dataIndex: "productBrand",
          key: "productBrand",
        },
        {
          title: "审核状态",
          align: "center",
          width: 90,
          dataIndex: "approvedStatus",
          key: "approvedStatus",
        },
        {
          title: "推广状态",
          // align: 'center',
          width: 90,
          dataIndex: "promStatus",
          customRender: function ({text}) {
            switch (text) {
              case 0:
                return "未开始";
              case 10:
                return "进行中";
              case 99:
                return "已结束(正常)";
              case 90:
                return "已结束(异常)";
            }
          },
        },
        {
          title: "支付状态",
          // align: 'center',
          dataIndex: "payStatus",
          width: 100,
          customRender: function ({text}) {
            switch (text) {
              case 0:
                return "待支付";
              case 10:
                return "部分支付";
              case 20:
                return "已付全款";
              case -1:
                return "无需支付";
            }
          },
        },
        {
          
          title: "平台",
          // align: 'center',
          width: 100,
          dataIndex: "promPlatform",
          key: "promPlatform",
        },
        {
          title: "类目",
          // align: 'center',
          width: 150,
          dataIndex: "promTags",
          key: "promTags",
        },
        {
          title: "网红要求",
          dataIndex: "promRequirement",
          // align: 'center',
          width: 150,
          key: "promRequirement",
        },
        {
          title: "案例",
          dataIndex: "promExample",
          // align: 'center',
          width: 100,
          key: "promExample",
        },
        {
          title: "推广费用",
          // align: 'center',
          width: 100,
          dataIndex: "promAmount",
          key: "promAmount",
        },
        {
          title: "税费",
          // align: 'center',
          width: 100,
          dataIndex: "totalTaxAmount",
          key: "totalTaxAmount",
        },
        {
          title: "汇率",
          // align: 'center',
          width: 100,
          dataIndex: "exchangeRate",
          key: "exchangeRate",
        },
        {
          title: "订单总额",
          // align: 'center',
          width: 100,
          dataIndex: "totalAmount",
          key: "totalAmount",
        },
        {
          title: "已付金额",
          // align: 'center',
          width: 100,
          dataIndex: "paymentAmount",
          key: "paymentAmount",
        },
        {
          title: "退款金额",
          // align: 'center',
          width: 100,
          dataIndex: "refundAmount",
          key: "refundAmount",
        },
        {
          title: "待付金额",
          // align: 'center',
          width: 100,
          dataIndex: "paidAmount",
          key: "paidAmount",
        },
        {
          title: "创建日期",
          // align: 'center',
          dataIndex: "createTime",
          width: 100,
          key: "createTime",
        },
        {
          title: "操作",
          dataIndex: "action",
          // align: 'center',
          width: 100,
          key: "action",
        },
      ]);
const imgUrl = ref("");
const innerColumns = ref([
        {
          title: "#",
          dataIndex: "",
          key: "rowIndex",
          width: 30,
          align: "center",
          customRender: function ({index}) {
            return parseInt(index) + 1;
          },
        },
        {
          title: "产品名称",
          // align: 'center',
          dataIndex: "goodsTitle",
          width: 100,
          key: "goodsTitle",
        },
        {
          title: "产品图片",
          align: "center",
          width: 70,
          dataIndex: "goodsPicUrl",
          key: "goodsPicUrl",
        },
        {
          title: "产品状态",
          // align: 'center',
          width: 70,
          dataIndex: "goodsStatus",
          customRender: function ({text}) {
            switch (text) {
              case 0:
                return "未开始";
              case 10:
                return "进行中";
              case 99:
                return "已结束";
              case 90:
                return "已结束";
            }
          },
        },
        // {
        //   title: '拍摄卖点',
        //   // align: 'center',
        //   dataIndex: 'goodsRemark',
        //   width: 100,
        //   scopedSlots: {
        //     customRender: 'goodsRemark'
        //   }
        // },
        // {
        //   title: '折扣码',
        //   // align: 'center',
        //   dataIndex: 'discountCode',
        //   width: 80,
        //   scopedSlots: {
        //     customRender: 'discountCode'
        //   }
        // },
        // {
        //   title: '折扣力度',
        //   // align: 'center',
        //   width: 80,
        //   dataIndex: 'discountOff',
        //   customRender: function (t) {
        //     if (t) {
        //       return t + '%OFF'
        //     }
        //   }
        // },
        // {
        //   title: '有效期',
        //   // align: 'center',
        //   dataIndex: 'discountDate',
        //   width: 120,
        //   scopedSlots: {
        //     customRender: 'discountDate'
        //   }
        // },
        {
          title: "网红账号",
          // align: 'center',
          dataIndex: "account",
          width: 150,
          key: "account",
        },
        {
          title: "头像",
          align: "center",
          dataIndex: "avatarUrl",
          width: 50,
          key: "avatarUrl",
        },
        {
          title: "平台",
          // align: 'center',
          dataIndex: "platformType",
          width: 50,
          key: "platformType",
          // customRender: function (t) {
          //   switch (t) {
          //     case 0:
          //       return 'IG'
          //     case 1:
          //       return 'YT'
          //     case 2:
          //       return 'TK'
          //   }
          // }
        },
        {
          title: "视频类型",
          // align: 'center',
          width: 120,
          dataIndex: "videoTags",
          key: "videoTags",
        },
        {
          title: "擅长类型",
          // align: 'center',
          width: 85,
          dataIndex: "promLikeTags",
          key: "promLikeTags",
        },
        {
          title: "粉丝",
          // align: 'center',
          width: 65,
          dataIndex: "followersNum",
          scopedSlots: {
            customRender: "followersNum",
          },
        },
        // {
        //   title: '平均点赞数',
        //   // align: 'center',
        //   width: 80,
        //   dataIndex: 'likeAvgNum',
        //   scopedSlots: {
        //     customRender: 'followersNum'
        //   }
        // },
        // {
        //   title: '平均互动率',
        //   // align: 'center',
        //   width: 80,
        //   dataIndex: 'interactAvgNum',
        //   scopedSlots: {
        //     customRender: 'followersNum'
        //   }
        // },
        {
          title: "均播",
          // align: 'center',
          width: 80,
          dataIndex: "playAvgNum",
          scopedSlots: {
            customRender: "followersNum",
          },
        },
        // {
        //   title: '总视频数',
        //   // align: 'center',
        //   width: 80,
        //   dataIndex: 'videoCount',
        //   scopedSlots: {
        //     customRender: 'followersNum'
        //   }
        // },
        // {
        //   title: '总获赞数',
        //   // align: 'center',
        //   width: 80,
        //   dataIndex: 'likeCount',
        //   scopedSlots: {
        //     customRender: 'followersNum'
        //   }
        // },
        {
          title: "推广费用",
          // align: 'center',
          width: 80,
          dataIndex: "promPrice",
          key: "promPrice",
        },
        /*  {
              title: '网红状态',
              // align: 'center',
              width: 80,
              dataIndex: 'celebrityStatus',
              scopedSlots: { customRender: 'celebrityStatus' }
            },*/
        // {
        //   title: '网红地址',
        //   // align: 'center',
        //   width: 80,
        //   dataIndex: 'fullAddress',
        //   scopedSlots: {
        //     customRender: 'fullAddress'
        //   }
        // },
        // {
        //   title: '物流单号',
        //   // align: 'center',
        //   dataIndex: 'goodsWaybill',
        //   width: 125,
        //   scopedSlots: {
        //     customRender: 'goodsWaybill'
        //   }
        // },
        // {
        //   title: '运输商',
        //   // align: 'center',
        //   width: 120,
        //   dataIndex: 'goodsCarrier',
        //   scopedSlots: {
        //     customRender: 'goodsCarrier'
        //   }
        // },
        {
          title: "网红进度",
          align: "center",
          width: 100,
          dataIndex: "celebrityPromStatus",
          key: "celebrityPromStatus",
        },
        {
          title: "推广图视",
          // align: 'center',
          width: 120,
          dataIndex: "promView",
          key: "promView",
        },
      ]);
const innerData = ref([]);
const innerLoading = ref(false);

const expandedRowKeys = ref([]);
const expandedSecondRowKeys = ref([]);
const imgShow = ref(false);

const { dataSource, pagination, loading, fetchTable, queryParam, searchQuery: searchQueryHook, searchReset: searchResetHook, pageChange,calcTableHeight, sTableHeight,copyFn } = useTable(
  queryStoreSellerPromotionListApi,0,{tableType:'aTbale',afterFetch:(params,res) => {
    // console.log(res)
    nextTick(() => {
      calcTableHeight('aTbale')
    })
   
  }});

function platformTypeImg(text) {
  switch (text) {
    case 0:
      return new URL('@/assets/images/ins.png', import.meta.url).href;
    case 1:
      return new URL('@/assets/images/yt.png', import.meta.url).href;
    case 2:
      return new URL('@/assets/images/tk.png', import.meta.url).href;
  }
}
function getAvatarUrl(url) {
  return url.includes("https") || url.includes("http")
    ? url
    : 'https://image.kolbox.com' + "/" + url;
}
function promPlatformArr(t) {
  // 静态导入图片资源
  const platformImgs = {
    "0": new URL("@/assets/images/ins.png", import.meta.url).href,
    "1": new URL("@/assets/images/yt.png", import.meta.url).href,
    "2": new URL("@/assets/images/tk.png", import.meta.url).href,
  };
  let newArr = [];
  const arr = t.split(",");
  for (let index = 0; index < arr.length; index++) {
    const element = arr[index];
    if (platformImgs[element]) {
      newArr.push({
        imgSrc: platformImgs[element],
      });
    }
  }
  return newArr;
}
// const getUserInfo = () => store.dispatch('getUserInfo');




function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    return (num / 1000).toFixed(1) + "K";
  } else if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + "M";
  } else {
    if (num == null || num == "" || num == "0") {
      return "/";
    }
    return num;
  }
}


async function updateKolStatus(status, id) {
  const res = await checkCelebrityStatusForListApi({
    id,
    status,
  });
  if (!res.success) {
    $message.error("修改失败！");
  }
  handleExpand(true, {
    id: expandedRowKeys.value[0],
  });
}


function getDate(date) {
  if (date) {
    return dayjs(date).format("YY/MM/DD");
  }
}



function getAccount(text) {
  copyAccount.value = text;
}

function getTotalAmountRmb(data) {
  if (data.totalAmount > 0 && data.exchangeRate > 0) {
    const num = floatMultiply(data.totalAmount, data.exchangeRate) || 0;
    return splitAmount(num);
  }
  return 0;
}

function openUrl(url) {
  let s1 = url.split(":")[0].toLowerCase();
  let s = s1 == "http" || s1 == "https" ? url : "http://" + url;
  window.open(s, "_blank");
}



function handleDelete(id) {
  deleteStoreSellerPromotionApi({
    id: id,
  }).then((res) => {
    if (res.success) {
      $message.success(res.message);
      fetchTable();
      let newElement = document.querySelector(".new-element-tr");
      if (newElement) {
        newElement.remove();
      }
    } else {
      $message.warning(res.message);
    }
  });
}


function viewImg(imgUrlValue) {
  imgUrl.value = imgUrlValue;
  imgShow.value = true;
}
function getPaidAmount(record, num) {
  if (record.totalRmbAmount) {
    let totalRmbAmount = record.totalRmbAmount || 0;
    let paymentAmount = record.paymentAmount || 0;
    let intentionAmount = record.intentionAmount || 0;
    let refundAmount = record.refundAmount || 0;
    let allPaymentTemp = floatAdd(paymentAmount, intentionAmount);
    let allPayment = floatSubtract(allPaymentTemp, refundAmount);
    if (num === 0) {
      const paidAmount = floatSubtract(totalRmbAmount, allPayment) || 0;
      return splitAmount(paidAmount);
    } else {
      let data = floatSubtract(totalRmbAmount, allPayment) || 0;
      const paidAmount = floatDivide(data, record.exchangeRate) || 0;
      return splitAmount(paidAmount);
    }
  }
}

function getPaymentAmount(record) {
  let addAmount = floatAdd(record.paymentAmount, record.intentionAmount);
  if (addAmount > 0 && record.exchangeRate > 0) {
    const paymentAmount = floatDivide(addAmount, record.exchangeRate) || 0;
    return splitAmount(paymentAmount);
  }
  return 0;
}

function getRefundAmount(record) {
  let refundAmount = record.refundAmount;
  if (refundAmount > 0 && record.exchangeRate > 0) {
    const refundRateAmount = floatDivide(refundAmount, record.exchangeRate) || 0;
    return splitAmount(refundRateAmount);
  }
  return 0;
}

function getAllPayment(record) {
  return floatAdd(record.paymentAmount, record.intentionAmount);
}

function getPromAmount(record) {
  if (record.promAmount > 0 && record.exchangeRate > 0) {
    const promAmount = floatMultiply(record.promAmount, record.exchangeRate) || 0;
    return splitAmount(promAmount);
  }
  return 0;
}


function splitAmount(num) {
  num = num + "";
  if (num == null || num == 0) {
    return 0;
  }
  if (num.split(".")[1] === "00") {
    return num.split(".")[0];
  } else {
    return num;
  }
}


async function handleExpand(expanded, record) {
  expandedRowKeys.value = [];
  innerData.value = [];
  expandedSecondRowKeys.value = [];
  if (expanded === true) {
    innerLoading.value = true;
    expandedRowKeys.value.push(record.id);
    const res = await innerTableListApi({
      promId: record.id,
    });
    console.log(res);
    if (res) {
      innerLoading.value = false;
      innerData.value = res;
    }
  }
  nextTick(() => {
    let index = dataSource.value.findIndex((item) => item.id === record.id);
    currentElementIndex.value = index;
  });
}

function rowClassName(record, index) {
  return "one-table-tr-" + index;
}

function modalFormOk() {
  fetchTable(1);
  // getUserInfo();
  if (expandedRowKeys.value[0]) {
    handleExpand(true, {
      id: expandedRowKeys.value[0],
    });
  }
}



function searchQuery() {
  const tableBody = document.querySelector(".ant-table-body");
  if (tableBody) {
    tableBody.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }
  let newElement = document.querySelector(".new-element-tr");
  if (newElement) {
    newElement.remove();
  }
  searchQueryHook();
  nextTick(() => {
    if (queryParam.value.goodsTitle && dataSource.value[0]?.id) {
      handleExpand(true, {
        id: dataSource.value[0].id,
      });
    } else {
      expandedRowKeys.value = [];
      expandedSecondRowKeys.value = [];
    }
  });
}

function searchReset() {
  const tableBody = document.querySelector(".ant-table-body");
  if (tableBody) {
    tableBody.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }
  expandedRowKeys.value = [];
  expandedSecondRowKeys.value = [];
  let newElement = document.querySelector(".new-element-tr");
  if (newElement) {
    newElement.remove();
  }
  searchResetHook();
}



function handleAdd() {
  modalFormRef.value.add();
}

fetchTable()
</script>
<style lang="less" scoped>
.span-term {
  color: rgba(0, 0, 0, 0.85);
  line-height: 20px;
  margin-right: 8px;
  padding-bottom: 16px;
  white-space: nowrap;
}

.font {
  font-size: 12px;
  color: #bebebe;
  font-weight: 400;
}

.font-rmb {
  font-size: 16px;
  // font-weight: 700;
}

.font-symbol {
  font-size: 12px;
}

.copy {
  cursor: pointer;
}

.ant-col-xl-4 {
  width: 16.6%;
}

.btn-large {
  transition: 0.2s all;
}

.btn-large:hover {
  transform: scale(1.2);
  transition: 0.2s all;
}

.noselect {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;

  img {
    width: 18px;
    height: 18px;
  }

  .yes_btn {
    cursor: pointer;
  }

  .no_btn {
    cursor: no-drop;
  }
}

.ant-modal-body {
  .pay_btn {
    height: 30px;
    line-height: 30px;

    span {
      display: inline-block;
      border: 1px solid #d9d9d9;
      overflow: hidden;
      position: relative;
      cursor: pointer;
      box-sizing: border-box;
    }

    span:nth-child(1),
    span:nth-child(2) {
      margin-right: 40px;
    }

    span.active {
      border: 2px solid #1890ff;
      color: #fc923f;
    }

    span.active i {
      position: absolute;
      right: -2px;
      bottom: -1px;
      width: 18px;
      /*图标宽度*/
      height: 18px;
      /*图标高度*/
      // background: url("../../../assets/pay.png") no-repeat;
      display: inline-block;
    }
  }
}

.spin-content {
  background-color: #fbfffd;
  padding: 30px;
  height: 110px;
}
</style>
