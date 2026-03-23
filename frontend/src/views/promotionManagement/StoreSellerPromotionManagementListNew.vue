<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div >
      <a-form @keyup.enter="searchQuery" class="searchForm" >
        <a-row :gutter="12">
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input
                 v-model:value="queryParam.extensionCode"
                placeholder="推广单号"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input   v-model:value="queryParam.sellerName" placeholder="商家名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input  v-model:value="queryParam.productBrand" placeholder="产品品牌"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input  v-model:value="queryParam.promTitle" placeholder="推广标题"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input  v-model:value="queryParam.account" placeholder="网红账号"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                :allowClear="true"
                 v-model:value="queryParam.counselorUserId"
                placeholder="商务人员"
              >
                <a-select-option
                  v-for="item in platformStaff"
                  :key="item.id"
                  :value="item.id"
                >
                  <a-tooltip :title="item.realname">
                    {{ item.realname }}
                  </a-tooltip>
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                :allowClear="true"
                 v-model:value="queryParam.approvedStatus"
                placeholder="审核状态"
              >
                <a-select-option value="1">审核通过</a-select-option>
                <a-select-option value="2">已提方案</a-select-option>
                <a-select-option value="3">匹配成功</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                :allowClear="true"
                 v-model:value="queryParam.promStatus"
                placeholder="推广状态"
              >
                <a-select-option :value="0">未开始</a-select-option>
                <a-select-option :value="10">进行中</a-select-option>
                <a-select-option :value="99">已结束（正常）</a-select-option>
                <a-select-option :value="90">已结束（异常）</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                :allowClear="true"
                 v-model:value="queryParam.celebrityCounselorId"
                placeholder="网红顾问"
              >
                <a-select-option
                  v-for="(item, key) in celebrityCounselorList"
                  :key="key"
                  :value="item.id"
                >
                  {{ item.username }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
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
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                style="width: 100%"
                allowClear
                 v-model:value="queryParam.promTags"
                placeholder="推广类目"
              >
                <a-select-option
                  v-for="(item, key) in categories"
                  :key="item.id"
                  :value="item.likeTagName"
                >
                  {{ item.likeTagName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xxl="2" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select
                style="width: 100%"
                allowClear
                 v-model:value="queryParam.promType"
                placeholder="推广类型"
              >
                <a-select-option
                  :value="1"
                >
                  商家
                </a-select-option>
                <a-select-option
                  :value="0"
                >
                  商务
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
         
          <a-col :xxl="6" :xl="3" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-button
                type="primary"
                @click="searchQuery"
                :disabled="showBatchModify"
                :icon="h(SearchOutlined)"
              >
                查询
              </a-button>
              <a-button
                @click="searchReset"
                style="margin-left: 8px"
                :disabled="showBatchModify"
                :icon="h(ReloadOutlined)"
              >
                重置
              </a-button>
              <a-button
                type="primary"
                @click="replaceBusiness"
                style="margin-left: 8px"
                v-if="hasPermission('BusinessPersonnel')"
                :disabled="showBatchModify"
                :icon="h(RetweetOutlined)"
              >
                更换商务
              </a-button>
              <a-popconfirm
                title="确定删除选中的网红吗?"
                @confirm="delCelebrityBatch()"
                v-if="hasPermission('matchingEdit')"
              >
                <a-button
                  type="danger"
                  class="del-btn"
                  v-show="selectedRowKeys.length > 0 && !showBatchModify"
                  :disabled="showBatchModify"
                  :icon="h(DeleteOutlined)"
                >
                  删除
                </a-button>
              </a-popconfirm>
              <!-- <a-button
                type="primary"
                v-show="!showBatchModify"
                @click="modify"
                style="margin-left: 8px"
                :disabled="!(selectedRowKeys.length > 0)"
                >批量修改</a-button
              > -->
              <span v-if="showBatchModify">
                <a-popconfirm
                  title="确定保存批量操作？"
                  cancel-text="否"
                  ok-text="是"
                  @confirm="batchModifyBtn"
                >
                  <a-button style="margin-left: 8px">保存</a-button>
                </a-popconfirm>
                <a-button @click="cancellation" style="margin-left: 8px">取消</a-button>
              </span>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- table区域-begin -->
    <div>
      <a-table
       :animate-rows="false"
      :rangeSelection="false"	
        ref="table"
        size="small"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="pagination"
        :loading="loading"
        @expand="handleExpand"
        :expandedRowKeys="expandedRowKeys"
        @change="pageChange"
        :ignoreCellKey="true"
        :scroll="{ x: 2280, y: sTableHeight }"
        :scrollToFirstRowOnChange="true"
        :expand-column-width="25"
      
      >
        <template #bodyCell="{ record, column }">
          <!--推广标题-->
          <template v-if="column.key === 'promTitle'">
            <!-- <j-ellipsis :value="record.promTitle" :length="20" ></j-ellipsis> -->
            <!-- {{ record.promTitle }} -->
            <EllipsisTooltip :text="record.promTitle" />
          </template>
          <template v-if="column.key === 'promType'">
            <a-tag  :color="record.promType == 1 ? '#f50' : '#3155ed'">{{ record.promType == 1 ? '商家' : '商务' }}</a-tag>
            <!-- {{ record.promTitle }} -->
          </template>
          <!--商家名称/商务人员-->
          <template v-else-if="column.key === 'sellerName' || column.key === 'counselorUserName'">
            <!-- <j-ellipsis :value="record[column.key]" :length="10"></j-ellipsis> -->
            <!-- {{ record[column.key] }} -->
          </template>
          <!--商家预算-->
          <template v-else-if="column.key === 'promBudget'">
            <EllipsisTooltip :text="record.promBudget" />
          </template>
          <!--产品品牌-->
          <template v-else-if="column.key === 'productBrand'">
            <!-- <j-ellipsis
              :value="record.productBrand"
              :length="15"
              @click="copyFn(record.productBrand)"
              style="color: #3155ed"
            ></j-ellipsis> -->
            <EllipsisTooltip @click="copyFn(record.productBrand)" style="color: #3155ed" :text="record.productBrand" />
            <!-- {{ record.productBrand }} -->
          </template>
          <!--推广单号-->
          <template v-else-if="column.key === 'extensionCode'">
            <a @click="copyFn(record.extensionCode)" title="点击复制" style="color: #3155ed"> {{ record.extensionCode }}</a>
          </template>
          <!--推广时间区域-->
          <template v-else-if="column.key === 'promTimeArea'">
            {{ record.promStartTime?.substr(5).replace("-", "/") }}~{{
              record.promEndTime?.substr(5).replace("-", "/")
            }}
          </template>
          <!--订单总额-->
          <template v-else-if="column.key === 'totalAmount'">
            <span v-if="record.totalRmbAmount">
              <a-tooltip placement="topLeft">
                <template #title>
                  <span>${{ record.totalAmount }}</span>
                </template>
                <span>¥{{ record.totalRmbAmount }}</span>
              </a-tooltip>
            </span>
          </template>
          <!--汇率-->
          <template v-else-if="column.key === 'exchangeRate'">
            <span>{{ record.exchangeRate }}</span>
          </template>
          <!--推广-->
          <template v-else-if="column.key === 'promAmount'">
            <span v-if="record.promAmount">
              <a-tooltip placement="topLeft">
                <template #title>
                  <span>${{ splitAmount(record.promAmount) }}</span>
                </template>
                <span>¥{{ getPromAmount(record) }}</span>
              </a-tooltip>
            </span>
          </template>
          <!--paypal-->
          <template v-else-if="column.key === 'totalTaxAmount'">
            <span v-if="record.totalTaxAmount">
              <a-tooltip placement="topLeft">
                <template #title>
                  <span>${{ record.totalTaxAmount }}</span>
                </template>
                <span>¥{{ gettotalTaxAmount(record) }}</span>
              </a-tooltip>
            </span>
          </template>
          <!--待付金额 RMB-->
          <template v-else-if="column.key === 'paidAmount'">
            <a-tooltip
              placement="topLeft"
              v-if="
                getPaidAmount(record, 0) != 0 &&
                getPaidAmount(record, 0) != null &&
                getPaidAmount(record, 0) > 0
              "
            >
              <template #title>
                <span> ${{ getPaidAmount(record, 1) }} </span>
              </template>
              <span>¥{{ getPaidAmount(record, 0) }}</span>
            </a-tooltip>
          </template>
          <!--已付金额 RMB-->
          <template v-else-if="column.key === 'paymentAmount'">
            <a-tooltip placement="topLeft">
              <template #title>
                <span v-if="getPaymentAmount(record) > 0"
                  >${{ getPaymentAmount(record) }}</span
                >
              </template>
              <span v-if="getAllPayment(record) > 0">¥{{ getAllPayment(record) }} </span>
            </a-tooltip>
          </template>
          <!--退款金额 RMB-->
          <template v-else-if="column.key === 'refundAmount'">
            <a-tooltip placement="topLeft">
              <template #title>
                <span v-if="getRefundAmount(record) > 0"
                  >${{ getRefundAmount(record) }}</span
                >
              </template>
              <span v-if="record.refundAmount > 0">¥{{ record.refundAmount }} </span>
            </a-tooltip>
          </template>
          <!--网红要求-->
          <template v-else-if="column.key === 'promRequirement'">
            <!-- <j-ellipsis :length="30" :value="record.promRequirement"></j-ellipsis> -->
            <EllipsisTooltip :text="record.promRequirement" />
            
          </template>
          <template v-else-if="column.key === 'contactInfo'">
            <!-- <j-ellipsis :length="30" :value="record.contactInfo"></j-ellipsis> -->
            <EllipsisTooltip :text="record.contactInfo" />
          </template>
          <!--审核状态-->
          <template v-else-if="column.key === 'approvedStatus'">
            <a-tag
              color="orange"
              style="margin-right: 0"
              v-if="record.approvedStatus === 0"
            >
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
          <!--推广状态-->
          <template v-else-if="column.key === 'promStatus'">
            <span style="display: flex; justify-content:space-between;padding-right: 20px;">
              <span v-if="record.promStatus == 0">未开始</span>
              <span v-if="record.promStatus == 10">进行中</span>
              <span v-if="record.promStatus == 99">已结束（正常）</span>
              <span v-if="record.promStatus == 90">已结束（异常）</span>
              <a-tooltip placement="top" v-if="hasPermission('PromotionStatus')">
                <template #title>
                  <span>编辑推广状态</span>
                </template>
                <a style="font-size: 15px;" v-if="hasPermission('PromotionStatus')" @click="openEditPromotionStatus(record, 0, '推广状态编辑')">
                  <component :is="h(FormOutlined)" />
                </a>
              
              </a-tooltip>
            </span>
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
                {{ record.promTags }}</a-tag
              >
            </div>
          </template>
          <!--案例链接-->
          <template v-else-if="column.key === 'promExample'">
            <a
              @click="openUrl(item)"
              :disabled="record.promExample == ''"
              v-for="(item, index) in (record.promExample || '').split('\n')"
              style="color: #3155ed"
              :key="index"
              >{{ item != "" ? "案例" + (index + 1) + " " : "无" }}
            </a>
          </template>
          <!--操作-->
          <template v-else-if="column.key === 'action'">


            <a-tooltip placement="top" style="margin-left: 5px">
              <template #title>
                <span>审核</span>
              </template>
              <a :disabled="record.approvedStatus != 0" @click="handleAudit(record)" style="font-size: 18px;">
                <i
                class="iconfont action-cursor"
              
              
                >&#xe633;</i
              >
              </a>
          
            </a-tooltip>
          
           
          <a-divider type="vertical" />
          <a-tooltip placement="top" style="margin-left: 5px">
            <template #title>
              <span>编辑</span>
            </template>
            <a :disabled="record.approvedStatus == 0 && record.promType == 1" style="font-size: 16px;"  @click="handleMatch(record)">
              <FormOutlined />
            </a>
            <!-- <a

              class="action-cursor"
              style="color: #3155ed;font-size: 17px;"
              @click="handleMatch(record)"
            >
              <component :is="h(FormOutlined)" />
            </a> -->
          </a-tooltip>
         
          <a-divider type="vertical" />
          <a-tooltip placement="top" >
            <template #title>
              <span>商家信息</span>
            </template>
            <a
              
              class="action-cursor"
            
              @click="merchantInformation(record)"
            >
              <i class="iconfont">&#xe66b;</i>
            </a>
          </a-tooltip>
          <a-divider type="vertical" />
            <a-tooltip placement="top" title="新增产品">
            
            <a
            
              :disabled="record.approvedStatus == 0 && record.promType == 1"
              class="action-cursor"
            
              @click="addProducts(record)"
            >
              <i class="iconfont" style="font-size: 18px;">  &#xe645;</i>
            </a>
        
          </a-tooltip>
          <a-divider type="vertical" />
          <a-tooltip placement="top" >
            <template #title>
              <span>删除</span>
            </template>
            <a-popconfirm
              title="确定删除该需求吗?"
              @confirm="handleDelete(record.id)"
              placement="bottom"
            >
            <a
              
              class="action-cursor"
             
           
            >
              <span class="icon iconfont icon-shanchu"></span>
           
            </a>
            </a-popconfirm>
            
          </a-tooltip>
  
       
         
      
          </template>
          <!--备注-->
          <template v-else-if="column.key === 'remark'">
            <!-- <j-ellipsis :value="record.remark" :length="10"></j-ellipsis> -->
            <EllipsisTooltip :text="record.remark" />
            <!-- {{ record.remark ? record.remark : '无' }} -->
          </template>
        </template>
        <!--------------------------------------------------产品 begin------------------------------------------------->
        <template #expandedRowRender="{  }">
          <a-table
          :rangeSelection="false"	
          size="small"
          :animate-rows="false"
          :loading="innerLoading"
          :columns="innerColumns"
          :dataSource="innerData"
          :pagination="false"
        >
          <template #bodyCell="{ record, column }">
          <template v-if="column.key === 'goodsTitle'">
            <a
              @click="openUrl(record.goodsUrl)"
              target="_blank"
              style="color: #3155ed; text-decoration: underline"
            >
              {{ record.goodsTitle }}
            </a>
          </template>
          <template v-else-if="column.key === 'goodsPicUrl'">
            <img
              :src="record.goodsPicUrl"
              :preview="record.id"
              style="max-height: 30px; max-width: 30px; cursor: pointer"
            />
          </template>
          <template v-else-if="column.key === 'goodsStatus'">
            <div style="text-align: center">
              <a-tag color="orange" v-if="record.goodsStatus == 0" style="margin-right: 0">
                未开始
              </a-tag>
              <a-tag color="blue" v-if="record.goodsStatus == 10" style="margin-right: 0">
                进行中
              </a-tag>
              <a-tag
                color="green"
                v-if="record.goodsStatus == 90 || record.goodsStatus == 99"
                style="margin-right: 0"
              >
                已结束
              </a-tag>
            </div>
          </template>
          <template v-else-if="column.key === 'celebrityPromStatus'">
            <a-select
              placeholder="网红进度"
              v-if="record.isOpenSchedule"
                v-model:value="record.celebrityPromStatus"
              style="font-size: 12px; margin-right: 5px"
            >
              <a-select-option :value="0"> 待寄样</a-select-option>
              <a-select-option :value="1"> 待上线</a-select-option>
              <a-select-option :value="2"> 已上线</a-select-option>
              <a-select-option :value="3"> 已完成</a-select-option>
              <a-select-option :value="-1"> 已终止</a-select-option>
            </a-select>
            <span v-else style="margin-right: 5px">
              <a-tag color="orange" v-if="record.celebrityPromStatus == 0" style="margin-right: 0">
                待寄样
              </a-tag>
              <a-tag color="cyan" v-if="record.celebrityPromStatus == 1" style="margin-right: 0">
                待上线
              </a-tag>
              <a-tag color="blue" v-if="record.celebrityPromStatus == 2" style="margin-right: 0">
                已上线
              </a-tag>
              <a-tag color="green" v-if="record.celebrityPromStatus == 3" style="margin-right: 0">
                已完成
              </a-tag>
              <a-tag color="red" v-if="record.celebrityPromStatus == -1" style="margin-right: 0">
                已终止
              </a-tag>
            </span>

            <!-- <a @click="editRowStatus(record)"
              :disabled="(!record.correId) ||(record.celebrityStatus != 1) || record.celebrityPromStatus == 3 || record.celebrityPromStatus == -1"
              v-if="!record.isOpenSchedule" style="font-size: 14px;"
              :style="{color:(!record.correId) ||(record.celebrityStatus != 1) || record.celebrityPromStatus == 3 || record.celebrityPromStatus == -1 ? '#ccc' : '#3155ED'}">
              <a-icon type="edit" /></a> -->
            <a
              @click="editRowStatus(record)"
              :disabled="
                !record.correId ||
                record.celebrityStatus != 1 ||
                record.celebrityPromStatus == 3 ||
                record.celebrityPromStatus == -1 ||
                hasPermission('edit:celebrityPromStatus')
               
              "
              v-if="!record.isOpenSchedule"
              style="font-size: 14px"
              :style="{
                color:
                  !record.correId ||
                  record.celebrityStatus != 1 ||
                  record.celebrityPromStatus == 3 ||
                  record.celebrityPromStatus == -1 ||
                  hasPermission('edit:celebrityPromStatus')
                    ? '#ccc'
                    : '#3155ED',
              }"
            >
              <!-- <img
                v-if="
                  !record.correId ||
                  record.celebrityStatus != 1 ||
                  record.celebrityPromStatus == 3 ||
                  record.celebrityPromStatus == -1 ||
                  hasPermission('edit:celebrityPromStatus')
                "
                class="yes_btn"
                src="../../../assets/edit-goods-dis.png"
                style="width: 18px; height: 18px; transform: translate(0px, -2px)"
              />
              <img
                v-else
                src="../../../assets/edit-goods.png"
                alt=""
                style="width: 18px; height: 18px; transform: translate(0px, -2px)"
              /> -->
              <!-- <a-icon type="edit" /> -->
            </a>
            <span v-else>
              <span v-show="!record.loading">
                <a @click="okRow(record)" style="color: #3155ed; font-size: 14px">
                  <component :is="h(CheckCircleOutlined)" />
                </a>
                <a-divider type="vertical" />
                <a @click="cancelRow(record)" style="color: #3155ed; font-size: 14px">
                  <component :is="h(CloseCircleOutlined)" />
                </a>
              </span>
              <component :is="h(LoadingOutlined)" v-show="record.loading" style="color: #3155ed" />
            </span>
          </template>
          <template v-else-if="column.key === 'account'">
            <span
             
              class="copy"
              @click="copyFn(record.account)"
              title="点击复制"
              >{{ record.account }}</span
            >
          </template>
          <template v-else-if="column.key === 'platformType'">
            <img
              v-if="record.platformType"
              style="width: 20px; height: 20px"
              :src="platformTypeImg(record.platformType)"
              alt=""
            />
          </template>
          <template v-else-if="column.key === 'avatarUrl'">
            <img
              :src="getAvatarUrl(record.avatarUrl)"
              :preview="record.id"
              v-if="record.avatarUrl"
              style="max-height: 30px; max-width: 30px; cursor: pointer"
            />
            <img
              v-else
              src="@/assets/images/avatar.png"
              style="max-width: 30px; max-height: 30px; margin: 0 auto"
            />
          </template>
          <!--粉丝-->
          <template v-else-if="column.key === 'followersNum'">
            {{ getFollower(record.followersNum) || "/" }}
          </template>
          <!--均播-->
          <template v-else-if="column.key === 'playAvgNum'">
            {{ getFollower(record.playAvgNum) || "/" }}
          </template>
          <!--网红费用高-->
          <template v-else-if="column.key === 'celebrityPrice'">
            <a-input-number
                v-model:value="record.celebrityPrice"
              v-if="record.isOpen"
              :formatter="(value) => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
              :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
              style="font-size: 12px; color: #000"
            />
            <span v-else>${{ record.celebrityPrice || 0 }}</span>
          </template>
          <!--推广费用-->
          <template v-else-if="column.key === 'promPrice'">
            <a-input-number
                v-model:value="record.promPrice"
              v-if="record.isOpen"
              :formatter="(value) => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
              :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
              style="font-size: 12px; color: #000; margin-right: 5px"
            />
            <span v-else style="display: inline-block; width: 88px; margin-right: 5px"
              >${{ splitAmount(record.promPrice || 0)  }}</span
            >
          </template>
          <template v-else-if="column.key === 'videoTags'">
            <!-- <j-ellipsis :value="record.videoTags" :length="6"></j-ellipsis> -->
            <EllipsisTooltip :text="record.videoTags" />
            <!-- {{ record.videoTags }} -->
          </template>
          <template v-else-if="column.key === 'promLikeTags'">
            <!-- <j-ellipsis :value="record.promLikeTags" :length="4"></j-ellipsis> -->
            <EllipsisTooltip :text="record.promLikeTags" />
            <!-- {{ record.promLikeTags }} -->
          </template>
          <!--推广图视-->
          <template v-else-if="column.key === 'promView'">
            <a
              @click="openUrl(item)"
              :disabled="record.mediaUploads == ''"
              style="margin-right: 5px"
              v-for="(item, index) in record.mediaUploads ? record.mediaUploads.split('\n') : []"
              :style="{ color: record.mediaUploads == '' ? '#ccc' : '#3155ED' }"
              :key="index"
              >{{ item != "" ? "链接" + (index + 1) + " " : "无" }}
            </a>
            <a-popover
              trigger="click"
              style="width: 300px"
              placement="bottom"
              :disabled="
                hasPermission('mediaUploadsEdit') ||
                record.celebrityStatus != 1 ||
                record.celebrityPromStatus == 3 ||
                record.celebrityPromStatus == -1
              "
              @visible-change="focusPopover($event, record)"
            >
              <template #content>
                <a-form
                  :ref="el => ruleFormRefs[record.id] = el"
                  :model="record"
                  :rules="rules"
                  v-bind="layout"
                >
                  <a-form-item
                    label="上线日期"
                    name="onlineTimeEdit"
                    style="margin-bottom: 0px"
                  >
                    <a-date-picker
                        v-model:value="record.onlineTimeEdit"
                      @change="dateChange($event, record)"
                      format="YYYY-MM-DD"
                    />
                  </a-form-item>
                  <a-form-item label="推广链接" name="mediaUploadsEdit">
                    <a-textarea
                        v-model:value="record.mediaUploadsEdit"
                      class="textarea1"
                      :auto-size="{ minRows: 3 }"
                    />
                  </a-form-item>
                  <div style="text-align: right">
                    <a-button @click="resetRow(record)"> 重置 </a-button>
                    <a-button
                      type="primary"
                      @click="subRow(record)"
                      style="margin-left: 10px"
                      :loading="record.viewLoading"
                    >
                      提交
                    </a-button>
                  </div>
                </a-form>
              </template>

              <a
                :style="{
                  color:
                    hasPermission('mediaUploadsEdit') ||
                    record.celebrityStatus != 1 ||
                    record.celebrityPromStatus == 3 ||
                    record.celebrityPromStatus == -1
                      ? '#ccc'
                      : '#3155ED',
                }"
              >
                <!-- <a-icon type="edit" /> -->
                <!-- <img
                  v-if="
                    hasPermission('mediaUploadsEdit') ||
                    record.celebrityStatus != 1 ||
                    record.celebrityPromStatus == 3 ||
                    record.celebrityPromStatus == -1
                  "
                  src="../../../assets//edit-goods-dis.png"
                  style="width: 18px; height: 18px; transform: translate(0px, -2px)"
                  alt=""
                />
                <img
                  v-else
                  src="../../../assets/edit-goods.png"
                  style="width: 18px; height: 18px; transform: translate(0px, -2px)"
                  alt=""
                /> -->
              </a>
            </a-popover>
          </template>
          <template v-else-if="column.key === 'celebrityStatus'">
            <a-switch
              checked-children="已选中"
              un-checked-children="未选中"
              :checked="record.celebrityStatus == 1"
              @change="checkKol($event, record)"
              :disabled="
                record.goodsStatus == 99 ||
                record.goodsStatus == 90 ||
                record.celebrityPromStatus == 3 ||
                record.celebrityPromStatus == -1
              "
            />
          </template>
          <template v-else-if="column.key === 'isIncludingTax'">
            <a-switch
              checked-children="是"
              un-checked-children="否"
              :checked="record.isIncludingTax == 1"
              :disabled="
                hasPermission('edit:isIncludingTax') ||
                record.goodsStatus == 99 ||
                record.goodsStatus == 90 ||
                record.celebrityStatus != 1 ||
                record.celebrityPromStatus == 3 ||
                record.celebrityPromStatus == -1
              "
              @change="changeIsIncludingTax($event, record)"
            />
          </template>
          <!--备注-->
          <template v-else-if="column.key === 'remark'">
            <div class="remarkView">
              {{ record.remark ? record.remark : '无' }}
              <a
                @click="openRemarkView(record)"
                v-if="!record.isRemarkView"
                style="color: #3155ed; margin-left: 5px"
              >
                <component :is="h(FormOutlined)" />
              </a>
              <div class="remarkViewBox" v-else>
                <a-textarea
                  :ref="el => remarkViewBoxRefs[record.id] = el"
                    v-model:value="record.remark"
                  :auto-size="{ minRows: 5 }"
                  @blur="blurRemark(record)"
                  style="width: 120px"
                />
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'innerAction'">
            <a-tooltip placement="bottom">
              <template #title> 编辑产品 </template>
              <a
                style="font-size: 16px;"
                @click="handleGoodsEdit(record)"
                :disabled="record.goodsStatus == 99 || record.goodsStatus == 90"
                :style="{color: (record.goodsStatus == 99 || record.goodsStatus == 90) ? '#ccc' : '#3155ed'}"
              >
              <FormOutlined />
                <!-- <i class="iconfont">&#xe60e;</i> -->
              </a>
            </a-tooltip>
            <a-divider type="vertical" />
            <a-tooltip
              placement="bottom"
              v-if="hasPermission('edit:goodsStatus')"
              style="margin-left: 5px"
            >
              <template #title> 修改产品状态 </template>
              <a
                v-if="hasPermission('edit:goodsStatus')"
                @click="openEditPromotionStatus(record, 1, '产品状态编辑')"
                style="color: #3155ed;font-size: 18px;"
              >
                <i class="iconfont" style="color: #3155ed;font-size: 18px;">&#xe61b;</i>
              </a>
            </a-tooltip>
          </template>
          </template>
        </a-table>
        </template>
      </a-table>
    </div>
    <StorePromotionGoodsManagementModalNew ref="storePromotionGoodsManagementModalFormRef" @ok="modelFormOK" />
    <AddProductsModal ref="addProductsModalFormRef" @ok="modelFormOK" />
    <merchantInformationModal ref="merchantInformationModalFormRef" @ok="modelFormOK" />
    <EditPromotionStatus ref="editPromotionStatusFormRef" @ok="modelFormOK" />
    <StorePromotionGoodsManagementForm ref="StorePromotionGoodsManagementFormRef" @ok="modelFormOK"></StorePromotionGoodsManagementForm>
    <StorePromotionGoodsManagementFormvNew ref="StorePromotionGoodsManagementFormvNewRef" @ok="modelFormOK"></StorePromotionGoodsManagementFormvNew>
    <ReplaceBusinessPersonnelModal ref="replaceBusinessPersonnelModalRef" @ok="modelFormOK"></ReplaceBusinessPersonnelModal>
    <AuditmerchantDemandModal ref="auditmerchantDemandModalRef" @ok="modelFormOK"></AuditmerchantDemandModal>
  </a-card>
</template>

<script setup name="StoreSellerPromotionManagementListNew">
import { ref, reactive, nextTick, watch, h } from 'vue';
import { SearchOutlined, ReloadOutlined, RetweetOutlined, DeleteOutlined, BarsOutlined, CheckCircleOutlined, CloseCircleOutlined, LoadingOutlined, FormOutlined } from '@ant-design/icons-vue';
import StorePromotionGoodsManagementModalNew from "./modules/StorePromotionGoodsManagementModalNew.vue";
import EditPromotionStatus from "./modules/EditPromotionStatus.vue";
import AddProductsModal from "./modules/AddProductsModal.vue";
import merchantInformationModal from "./modules/merchantInformationModal.vue";
import StorePromotionGoodsManagementFormvNew from './modules/StorePromotionGoodsManagementFormvNewModal.vue'
import StorePromotionGoodsManagementForm from './modules/StorePromotionGoodsManagementForm.vue'
import JEllipsis from '/@/components/Form/src/jeecg/components/JEllipsis.vue';
import EllipsisTooltip from "@/components/jeecg/EllipsisTooltip.vue";
import ReplaceBusinessPersonnelModal from "./modules/ReplaceBusinessPersonnelModal.vue";
import AuditmerchantDemandModal from "./modules/AuditmerchantDemandModal.vue";
import { floatAdd, floatDivide, floatMultiply, floatSubtract } from "@/utils/floatUtils.ts";
import { defHttp } from '/@/utils/http/axios';
import dayjs from 'dayjs';
import { useMessage } from '/@/hooks/web/useMessage';

import {
  queryStoreSellerPromotionManagementListApi,
  mediaUploadsEditApi,
  editGoodsCelebrityRemarkApi,
  deleteBackApi,
  getBusinessPeopleApi,
  innerTableListApi,
  delCelebrityBatchApi,
  checkCelebrityStatusApi,
  updateGoodsCelebrityBatchApi,
  getStoreTagsListApi
} from '/@/api/promotionManagement/promotionManagementApi';
import { queryAllCelebrityCounselorApi } from '/@/api/sys/systemCommon';
import { useTable } from '/@/components/useSTable/useSTable';

import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
const { createMessage: $message } = useMessage();
const { dataSource, pagination, loading, fetchTable, queryParam, searchQuery, pageChange, sTableHeight,copyFn } = useTable(queryStoreSellerPromotionManagementListApi,50,{tableType:'aTbale'});
const selectedRowKeys = ref([]);

const tableRef = ref(null);
const addProductsModalFormRef = ref(null);
const storePromotionGoodsManagementModalFormRef = ref(null);
const StorePromotionGoodsManagementFormRef = ref(null);
const StorePromotionGoodsManagementFormvNewRef = ref(null);
const replaceBusinessPersonnelModalRef = ref(null);
const merchantInformationModalFormRef = ref(null);
const ruleFormRefs = ref({});
const remarkViewBoxRefs = ref({});
const editPromotionStatusFormRef = ref(null);
const auditmerchantDemandModalRef =  ref(null)
// 表头
const columns = ref([
        {
          title: "#",
          dataIndex: "",
          key: "rowIndex",
          align:"center",
          width: 40,
          // // align: 'center',
          customRender: function ({index}) {
            return parseInt(index) + 1;
          },
        },
        {
          title: "操作",
          dataIndex: "action",
          key: "action",
          align: 'center',
          width: 180,
          // fixed: 'right',
        },
        {
          title: "推广单号",
          // // align: 'center',
          width: 100,
          dataIndex: "extensionCode",
          key: "extensionCode",
        },
        {
          title: "推广类型",
          // // align: 'center',
          width: 100,
          dataIndex: "promType",
          key: "promType",
        },
        {
          title: "商务人员",
          // // align: 'center',
          width: 120,
          dataIndex: "counselorUserName",
          key: "counselorUserName",
        },
        {
          title: "商家名称",
          // // align: 'center',
          width: 120,
          dataIndex: "sellerName",
          key: "sellerName",
        },
        {
          title: "产品品牌",
          // // align: 'center',
          width: 120,
          dataIndex: "productBrand",
          key: "productBrand",
        },
        {
          title: "推广标题",
          // // align: 'center',
          width: 150,
          dataIndex: "promTitle",
          key: "promTitle",
        },
        {
          title: "审核状态",
          align: "center",
          width: 100,
          dataIndex: "approvedStatus",
          key: "approvedStatus",
        },
        {
          title: "推广状态",
          // // align: 'center',
          width: 120,
          dataIndex: "promStatus",
          key: "promStatus",
        },
        {
          title: "平台",
          // // align: 'center',
          width: 100,
          dataIndex: "promPlatform",
          key: "promPlatform",
          // customRender: function (t) {
          //   let str = ''
          //   for (let index = 0; index < t.split(',').length; index++) {
          //     const element = t.split(',')[index];
          //     if (element === '0') {
          //       str += 'IG '
          //     } else if (element === '1') {
          //       str += 'YT '
          //     } else if (element === '2') {
          //       str += 'TK '
          //     } else if (element === '10') {
          //       str += '不限 '
          //     }
          //   }
          //   return str

          // }
        },
        {
          title: "类目",
          // // align: 'center',
          dataIndex: "promTags",
          key: "promTags",
          width: 150,
        },
        {
          ellipsis: true,
          title: "网红要求",
          // // align: 'center',
          width: 150,
          dataIndex: "promRequirement",
          key: "promRequirement",
        },
        {
          title: "联系方式",
          // // align: 'center',
          width: 150,
          dataIndex: "contactInfo",
          key: "contactInfo",
        },
        {
          title: "案例",
          // // align: 'center',
          width: 120,
          dataIndex: "promExample",
          key: "promExample",
        },
        {
          title: "商家预算",
          // // align: 'center',
          width: 120,
          dataIndex: "promBudget",
          key: "promBudget",
        },
        // {
        //   title: "税费",
        //   // // align: 'center',
        //   width: 100,
        //   dataIndex: "totalTaxAmount",
        //   key: "totalTaxAmount",
        // },
        {
          title: "汇率",
          // // align: 'center',
          width: 100,
          dataIndex: "exchangeRate",
          key: "exchangeRate",
        },
        // {
        //   title: "订单总额",
        //   // // align: 'center',
        //   dataIndex: "totalAmount",
        //   key: "totalAmount",
        //   width: 80,
        // },
        // {
        //   title: "网红成本",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "celebrityTotalAmount",
        //   key: "celebrityTotalAmount",
        //   customRender: function ({text}) {
        //     if (text) {
        //       return "$" + text;
        //     }
        //   },
        // },
        // {
        //   title: "已付金额",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "paymentAmount",
        //   key: "paymentAmount",
        // },
        // {
        //   title: "退款金额",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "refundAmount",
        //   key: "refundAmount",
        // },
        // {
        //   title: "待付金额",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "paidAmount",
        //   key: "paidAmount",
        // },
        // {
        //   title: "支付状态",
        //   // // align: 'center',
        //   width: 100,
        //   dataIndex: "payStatus",
        //   key: "payStatus",
        //   customRender: function ({text}) {
        //     switch (text) {
        //       case 0:
        //         return "待支付";
        //       case 10:
        //         return "部分支付";
        //       case 20:
        //         return "已付全款";
        //       case -1:
        //         return "无需支付";
        //     }
        //   },
        // },
        {
          title: "备注",
          // // align: 'center',
          width: 100,
          dataIndex: "remark",
          key: "remark",
        },
      ]);
// 计算所有列的宽度总和
const columnsTotalWidth = columns.value.reduce((sum, col) => sum + (col.width || 0), 0);
// 打印或导出
console.log("所有列的宽度总和为:", columnsTotalWidth); // 1840

const layout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 16,
  },
};
const innerLoading = ref(false);
const innerColumns = ref([
        {
          title: "#",
          dataIndex: "",
          key: "rowIndex",
          align: "center",
          width: 30,
          // // align: 'center',
          customRender: function ({index}) {
            return parseInt(index) + 1;
          },
        },
        {
          title: "操作",
          dataIndex: "action",
          key: "innerAction",
          width: 80,
          align: 'center',
        },
        {
          title: "产品名称",
          // // align: 'center',
          dataIndex: "goodsTitle",
          key: "goodsTitle",
          width: 90,
        },
        {
          title: "产品图片",
          align: "center",
          width: 60,
          dataIndex: "goodsPicUrl",
          key: "goodsPicUrl",
        },
        {
          title: "产品状态",
          align: "center",
          width: 60,
          dataIndex: "goodsStatus",
          key: "goodsStatus",
        },
        {
          title: "网红顾问",
          // align: 'center',
          dataIndex: "celebrityCounselorName",
          key: "celebrityCounselorName",
          width: 70,
        },
        {
          title: "网红进度",
          width: 120,
          dataIndex: "celebrityPromStatus",
          key: "celebrityPromStatus",
        },
        {
          title: "网红账号",
          // // align: 'center',
          dataIndex: "account",
          key: "account",
          width: 120,
        },
        {
          title: "头像",
          align: "center",
          dataIndex: "avatarUrl",
          key: "avatarUrl",
          width: 40,
        },
        {
          title: "平台",
          // // align: 'center',
          dataIndex: "platformType",
          key: "platformType",
          width: 40,
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
          title: "国家",
          align: "center",
          width: 80,
          dataIndex: "countryName",
          key: "countryName",
        },
        {
          title: "视频类型",
          // // align: 'center',
          width: 60,
          dataIndex: "videoTags",
          key: "videoTags",
        },
        {
          title: "粉丝",
          // // align: 'center',
          width: 40,
          dataIndex: "followersNum",
          key: "followersNum",
        },
        {
          title: "均播",
          // // align: 'center',
          width: 60,
          dataIndex: "playAvgNum",
          key: "playAvgNum",
        },

        {
          title: "网红费用",
          // // align: 'center',
          dataIndex: "celebrityPrice",
          key: "celebrityPrice",
          width: 80,
        },
        {
          title: "推广费用",
          // // align: 'center',
          width: 120,
          dataIndex: "promPrice",
          key: "promPrice",
        },
        // {
        //   title: "网红状态",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "celebrityStatus",
        //   key: "celebrityStatus",
        // },
        // {
        //   title: "是否含税",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "isIncludingTax",
        //   key: "isIncludingTax",
        // },
        // {
        //   title: "网红税费",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "taxAmount",
        //   key: "taxAmount",
        //   customRender: function ({text}) {
        //     if (text) {
        //       return text ? "$" + text : "";
        //     }
        //   },
        // },
        // {
        //   title: "上线时间",
        //   // // align: 'center',
        //   width: 70,
        //   dataIndex: "onlineTime",
        //   key: "onlineTime",
        //   // customRender: function (t) {
        //   //   if (t) {
        //   //     return t ? t.split(" ")[0] : "";
        //   //   }
        //   // },
        // },
        // {
        //   title: "推广图视",
        //   // // align: 'center',
        //   width: 80,
        //   dataIndex: "promView",
        //   key: "promView",
        // },
        {
          title: "备注",
          dataIndex: "remark",
          key: "remark",
          // // align: 'center',
          width: 120,
        },
      ]);

const rules = reactive({
        onlineTimeEdit: [
          {
            required: true,
            message: "请选择上线日期！",
            trigger: "change",
          },
        ],
        mediaUploadsEdit: [
          {
            required: true,
            message: "请输入推广链接！",
            trigger: "change",
          },
          {
            validator: (rule, value, callback) => {
              if (value) {
                let urlList = value.split("\n");
                if (urlList.length > 2) {
                  callback(new Error("推广链接最多为2条"));
                }
                var strRegex = /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/;
                const re = new RegExp(strRegex, "i");
                let flag = false;
                for (let i = 0; i < urlList.length; i++) {
                  if (re.test(encodeURI(urlList[i]))) {
                  } else {
                    flag = true;
                  }
                }
                if (flag) {
                  callback(new Error("推广链接格式错误"));
                } else {
                  callback();
                }
              }
            },
            trigger: "change",
          },
        ],
      });

const innerData = ref([]);
const copyAccount = ref("");
const num = ref(3);
const expandedRowKeys = ref([]);
const celebrityCounselorList = ref([]);
const categories = ref([]);
const likeTags = ref([]);
const promId = ref("");
const approvedStatus = ref("");
const expandedRowPayStatus = ref(0);
const dataSourceId = ref("");
const showBatchModify = ref(false);
const checkedAllCelebrityStatus = ref(false);
const checkedIsIncludingTax = ref(false);
queryParam.value.promotionType = 1
const platformStaff = ref([]);

let timer = null;

watch(num, () => {
  clearInterval(timer);
});

;
initCelebrityCounselor();
  getBusinessPeople();
  initCategory();
  fetchTable();


function merchantInformation(record) {
  console.log(record);
  merchantInformationModalFormRef.value.show(record);
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


function modify() {
  if (selectedRowKeys.value.length === 0) {
    $message.warning(`请选择批量修改的网红`);
    return;
  }
  let flag =
    innerData.value.some((item) => item.isOpenSchedule) ||
    innerData.value.some((item) => item.isOpen);
  if (flag) {
    $message.warning("请保存其他！");
    return;
  }
  showBatchModify.value = true;
  for (let index = 0; index < innerData.value.length; index++) {
    let item = innerData.value[index];
    selectedRowKeys.value.forEach((key) => {
      if (key === item.correId) {
        if (item.celebrityPromStatus == 3) {
          const indexToRemove = selectedRowKeys.value.indexOf(key);
          if (indexToRemove !== -1) {
            selectedRowKeys.value.splice(indexToRemove, 1);
          }
          if (selectedRowKeys.value.length == 0) {
            showBatchModify.value = false;
          }
          item.isOpen = false;
          $message.warning(`${item.account}进度已完成不可编辑`);
          return;
        }

        if (item.celebrityPromStatus == -1) {
          const indexToRemove = selectedRowKeys.value.indexOf(key);
          if (indexToRemove !== -1) {
            selectedRowKeys.value.splice(indexToRemove, 1);
          }
          if (selectedRowKeys.value.length == 0) {
            showBatchModify.value = false;
          }
          $message.warning("进度已终止不可编辑");
          item.isOpen = false;
          return;
        } else if (!item.correId) {
          const indexToRemove = selectedRowKeys.value.indexOf(key);
          if (indexToRemove !== -1) {
            selectedRowKeys.value.splice(indexToRemove, 1);
          }
          if (selectedRowKeys.value.length == 0) {
            showBatchModify.value = false;
          }
          $message.warning("未匹配网红不可编辑");
          item.isOpen = false;
          return;
        } else if (item.goodsStatus == 90 || item.goodsStatus == 99) {
          $message.warning("产品状态已结束不可编辑");
          item.isOpen = false;
          const indexToRemove = selectedRowKeys.value.indexOf(key);
          if (indexToRemove !== -1) {
            selectedRowKeys.value.splice(indexToRemove, 1);
          }
          if (selectedRowKeys.value.length == 0) {
            showBatchModify.value = false;
          }
        } else {
          item.isOpen = true;
        }
      }
    });
  }
}


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
function cancellation() {
  showBatchModify.value = false;
  checkedAllCelebrityStatus.value = false;
  checkedIsIncludingTax.value = false;
  selectedRowKeys.value = [];
  innerData.value.forEach((item) => {
    item.isOpen = false;
  });
  fetchTable();
  if (dataSourceId.value) {
    handleExpand(true, {
      id: dataSourceId.value,
    });
  }
}

function batchModifyBtn() {
  const filterList = innerData.value.filter((item) =>
    selectedRowKeys.value.includes(item.correId)
  );

  const priceFlag = filterList.find((item) => item.celebrityPrice > item.promPrice);
  if (priceFlag) {
    $message.warning("网红费用不能大于推广费用！");
    return;
  } else {
    const goodsCelebrityList = filterList.map((item) => ({
      id: item.correId,
      celebrityPrice: item.celebrityPrice,
      promPrice: item.promPrice.toString(),
      isIncludingTax: item.isIncludingTax,
      celebrityStatus: item.celebrityStatus,
    }));
    console.log(goodsCelebrityList);
    let currentPromId = promId.value;
    updateGoodsCelebrityBatchApi({
      promId: currentPromId,
      goodsCelebrityList,
    }).then((res) => {
      console.log(res);
      if (res.success) {
        if (res.code === 200) {
          $message.success(res.message);
          showBatchModify.value = false;
          checkedAllCelebrityStatus.value = false;
          checkedIsIncludingTax.value = false;
          fetchTable();
          if (dataSourceId.value) {
            handleExpand(true, {
              id: dataSourceId.value,
            });
          }
        } else if (res.code === 500) {
          $message.error(res.message);
          checkedAllCelebrityStatus.value = false;
          checkedIsIncludingTax.value = false;
        }
      } else {
        $message.error(res.message);
        checkedAllCelebrityStatus.value = false;
        checkedIsIncludingTax.value = false;
      }
    });
  }
}

function modelFormOK(){
  fetchTable()
  if (dataSourceId.value) {
    handleExpand(true, {
      id: dataSourceId.value,
    });
  }
}

function initCategory() {
  getStoreTagsListApi().then((res) => {
    console.log(res);
    categories.value = res.records;
  });
}

function focusPopover(e, row) {
  nextTick(() => {
    setTimeout(() => {
      ruleFormRefs.value["ruleForm" + row.id].resetFields();
      if (e) {
        row.mediaUploadsEdit = row.mediaUploads;
        row.onlineTimeEdit = row.onlineTime
          ? dayjs(row.onlineTime)
          : dayjs(new Date());
      }
    }, 200);
  });
}

function dateChange(_e, _row) {
}

function resetRow(row) {
  ruleFormRefs.value["ruleForm" + row.id].resetFields();
}

function subRow(row) {
  ruleFormRefs.value["ruleForm" + row.id].validate((valid) => {
    console.log(valid);
    if (valid) {
      let params = {
        id: row.correId,
        promId: row.promId,
        mediaUploads: row.mediaUploadsEdit,
        celebrityPromStatus: 3,
        onlineTime: dayjs(row.onlineTimeEdit).format("YYYY-MM-DD"),
      };
      row.viewLoading = true;
      mediaUploadsEditApi(params).then((res) => {
        if (res.success) {
          $message.success(res.message);
          modalFormOk();
        } else {
          $message.warning(res.message);
        }
        row.viewLoading = false;
      });
    } else {
      console.log("error submit!!");
      return false;
    }
  });
}

function openRemarkView(record) {
  record.oldRemark = record.remark;
  record.isRemarkView = true;
  nextTick(() => {
    remarkViewBoxRefs.value["remarkViewBox" + record.id].focus();
  });
}

async function blurRemark(record) {
  record.isRemarkView = false;
  let params = {
    id: record.id,
    remark: record.remark,
  };
  const res = await editGoodsCelebrityRemarkApi(params);
  if (res.success) {
    $message.success(res.message);
  } else {
    $message.warning(res.message);
    record.remark = record.oldRemark;
  }
}

async function okRow(row) {
  if (row.celebrityPrice > row.promPrice) {
    return $message.warning("网红费用不能大于推广费用！");
  }
  row.loading = true;
  const res = await defHttp.put({
    url: "/storePromotionGoodsCelebrity/editGoodsCelebrity",
    data: {
      id: row.correId,
      celebrityPrice: row.celebrityPrice,
      promPrice: row.promPrice,
      promId: row.promId,
      celebrityPromStatus: row.celebrityPromStatus,
      celebrityStatus: row.celebrityStatus,
    },
  });
  if (res.success) {
    $message.success(res.message);
    row.isOpen = false;
    row.isOpenSchedule = false;
    fetchTable();
    if (dataSourceId.value) {
      handleExpand(true, {
        id: dataSourceId.value,
      });
    }
  } else {
    $message.error(res.message);
  }
  row.loading = false;
}

function cancelRow(row) {
  row.celebrityPromStatus = row.oldcelebrityPromStatus;
  row.celebrityPrice = row.oldcelebrityPrice || "";
  row.promPrice = row.oldpromPrice || "";
  row.isOpen = false;
  row.isOpenSchedule = false;
}

function _editRow(row) {
  console.log(row);
  if (!row.correId) {
    $message.warning("未匹配网红不可编辑！");
    return;
  }
  if (row.celebrityPromStatus == 3) {
    $message.warning("该进度已完成不可编辑！");
    return;
  }
  let flag =
    innerData.value.some((item) => item.isOpenSchedule) ||
    innerData.value.some((item) => item.isOpen);
  if (flag) {
    $message.warning("请保存其他！");
    return;
  }
  row.oldcelebrityPromStatus = row.celebrityPromStatus;
  row.oldcelebrityPrice = row.celebrityPrice;
  row.oldpromPrice = row.promPrice;
  row.isOpen = true;
}

function editRowStatus(row) {
  if (!row.correId) {
    $message.warning("未匹配网红不可编辑！");
    return;
  }
  if (row.celebrityStatus != 1) {
    $message.warning("网红状态未选中不可编辑！");
    return;
  }
  let flag =
    innerData.value.some((item) => item.isOpenSchedule) ||
    innerData.value.some((item) => item.isOpen);
  if (flag) {
    $message.warning("请保存其他！");
    return;
  }
  row.oldcelebrityPromStatus = row.celebrityPromStatus;
  row.oldcelebrityPrice = row.celebrityPrice;
  row.oldpromPrice = row.promPrice;
  row.isOpenSchedule = true;
}

function replaceBusiness() {
  if (replaceBusinessPersonnelModalRef.value) {
    replaceBusinessPersonnelModalRef.value.show();
  }
}

function openEditPromotionStatus(record, type, _title) {
  if (editPromotionStatusFormRef.value) {
    editPromotionStatusFormRef.value.edit(record, type);
    editPromotionStatusFormRef.value.title = _title;
  }
}

function _changeSellerCounselor(val) {
  queryParam.value.sid = val;
}

async function handleDelete(id) {
  const res = await deleteBackApi(id);
  if (res.success) {
    $message.success(res.message);
    expandedRowKeys.value = [];
    fetchTable();
  } else {
    $message.error(res.message);
  }
}

function initCelebrityCounselor() {
  queryAllCelebrityCounselorApi().then((res) => {
    celebrityCounselorList.value = res;
  
  });
}

async function changeIsIncludingTax(checked, record) {
  if (!record.isOpen) {
    var status = checked ? 1 : 0;
    await selectTaxes(status, record);
    return;
  }
  if (!checked) {
    record.isIncludingTax = 0;
  } else {
    record.isIncludingTax = 1;
  }
}
async function checkKol(checked, record) {
  if (record.promPrice <= 0) {
    $message.warning("推广费用不能为0");
    return;
  }
  if (!record.isOpen || record.checked) {
    var status = checked ? 1 : 0;
    await updateKolStatus(status, record);
    return;
  }
  if (!record.correId) {
    $message.warning("请匹配网红后操作！");
    return;
  }
  if (!checked) {
    record.celebrityStatus = "0";
  } else {
    record.celebrityStatus = "1";
  }
}

async function updateKolStatus(status, record) {
  loading.value = true;
  const res = await checkCelebrityStatusApi({
    id: record.correId,
    status,
  });
  try {
    if (!res.success) {
      if (res.code == "10001") {
        $message.error(res.message);
        return;
      }
      $message.error(res.message);
    } else {
      fetchTable();
      handleExpand(true, {
        id: dataSourceId.value,
      });
    }
  } finally {
    loading.value = false;
  }
}

async function selectTaxes(_isIncludingTax, _record) {
  loading.value = true;
  try {
    // TODO: 需要实现 selectTaxesApi
    // const res = await selectTaxesApi({
    //   id: _record.correId,
    //   isIncludingTax: _isIncludingTax,
    // });
    // if (!res.success) {
    //   $message.error(res.message);
    // } else {
      fetchTable();
      handleExpand(true, {
        id: dataSourceId.value,
      });
    // }
  } finally {
    loading.value = false;
  }
}

function getAvatarUrl(url) {
  return url.includes("https") || url.includes("http")
    ? url
    :'https://image.kolbox.com/' + url;
}

// 未使用的函数，添加下划线前缀
function _platformTypeImg(text) {
  switch (text) {
    case 0:
      return require("@/asstes/images/ins.png");
    case 1:
      return require("@/asstes/images/yt.png");
    case 2:
      return require("@/asstes/images/tk.png");
  }
}

function _promPlatformArr(t) {
  let newArr = [];
  for (let index = 0; index < t.split(",").length; index++) {
    const element = t.split(",")[index];
    if (element === "0") {
      newArr.push({
        imgSrc: require("@/asstes/images/ins.png"),
      });
    } else if (element === "1") {
      newArr.push({
        imgSrc: require("@/asstes/images/yt.png"),
      });
    } else if (element === "2") {
      newArr.push({
        imgSrc: require("@/asstes/images/tk.png"),
      });
    }
  }
  return newArr;
}
async function delCelebrityBatch() {
  var ids = "";
  if (selectedRowKeys.value.length <= 0) {
    $message.warning("请选择一条记录！");
    return;
  } else {
    if (selectedRowKeys.value[0] == "") {
      $message.warning("请分配网红后删除！");
      return;
    }
    for (var a = 0; a < selectedRowKeys.value.length; a++) {
      ids += selectedRowKeys.value[a] + ",";
    }
  }
  const res = await delCelebrityBatchApi({ ids });
  if (res.success) {
    $message.success(res.message);
  } else {
    $message.error(res.message);
  }
  fetchTable();
  handleExpand(true, {
    id: dataSourceId.value,
  });
  loading.value = false;
}

async function getBusinessPeople() {
  const res = await getBusinessPeopleApi();

  platformStaff.value = res

}

function _handleSellerGoodsCelebrity(_record) {
  // storeSellerGoodsCelebrityFormRef.value.getPromotionGoodsInfo(_record, promId.value);
  // storeSellerGoodsCelebrityFormRef.value.title = "匹配网红";
  // storeSellerGoodsCelebrityFormRef.value.disableSubmit = false;
}

function _doTrack() {
  // nextTick(() => {
  //   if (goodsWaybill.value !== "") {
  //     YQV5.trackSingle({
  //       YQ_ContainerId: "YQCode",
  //       YQ_Height: 560,
  //       YQ_Fc: "0",
  //       YQ_Num: goodsWaybill.value,
  //     });
  //   } else {
  //     waybill.value = "暂未填写物流单号，无法查询！";
  //   }
  // });
}

function openUrl(url) {
  let s1 = url.split(":")[0].toLowerCase();
  let s = s1 == "http" || s1 == "https" ? url : "http://" + url;
  window.open(s, "_blank");
}

function _getTotalAmountRmb(data) {
  if (data.totalAmount > 0 && data.exchangeRate > 0) {
    const num = floatMultiply(data.totalAmount, data.exchangeRate) || 0;
    return splitAmount(num);
  }
  return 0;
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

function _getTaxFeeAmount(record) {
  return floatMultiply(record.taxFeeAmount, record.exchangeRate);
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

function gettotalTaxAmount(record) {
  if (record.totalTaxAmount > 0 && record.exchangeRate > 0) {
    const totalTaxAmount =
      floatMultiply(record.totalTaxAmount, record.exchangeRate) || 0;
    return splitAmount(totalTaxAmount);
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

function paymentModalFormOk() {
  expandedRowKeys.value = [];
  fetchTable();
}

function storeSellerPromotionGoodsManagementModalOk() {
  expandedRowKeys.value = [];
  fetchTable();
}

function searchReset() {
  approvedStatus.value = "";
  dataSourceId.value = "";
  expandedRowKeys.value = [];
  likeTags.value = [];
  queryParam.value = {}
  queryParam.value.promotionType = 1
  fetchTable()
}

function getFollower(num) {
  if (num >= 1000 && num < 1000000) {
    return (num / 1000).toFixed(1) + "K";
  } else if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + "M";
  } else {
    return num;
  }
}

function getFullAddress(data) {
  fullAddress.value = data;
}

function getWayBill(text) {
  copyGoodsWaybill.value = text;
}

function getAccount(text) {
  copyAccount.value = text;
}

function openPromView(record, num) {
  if (num == 0) {
    mediaModalRef.value.title = "推广图视";
  } else {
    mediaModalRef.value.title = "视频草稿";
  }
  mediaModalRef.value.showModel(record, num);
  mediaModalRef.value.disableSubmit = false;
}
function handleAudit(record) {
  console.log(record);
  auditmerchantDemandModalRef.value.edit(record);
}
function handleMatch(record) {
  storePromotionGoodsManagementModalFormRef.value.getPromotionGoods(record);
  storePromotionGoodsManagementModalFormRef.value.title = "修改";
  storePromotionGoodsManagementModalFormRef.value.disableSubmit = false;
  storePromotionGoodsManagementModalFormRef.value.promId = record.id;
  storePromotionGoodsManagementModalFormRef.value.approvedStatus = record.approvedStatus;
}

function isNewProductEdit(record) {
  return record.kolProductId ? true : false;
}

function handleGoodsEdit(record) {
  if (isNewProductEdit(record)) {
    StorePromotionGoodsManagementFormvNewRef.value.edit(record);
    StorePromotionGoodsManagementFormvNewRef.value.title = "编辑产品";
  } else {
    StorePromotionGoodsManagementFormRef.value.edit(record);
    StorePromotionGoodsManagementFormRef.value.title = "编辑产品";

  }
}

function celebrityEdit(record, status) {
  let payableAmount = record.payableAmount || 0;
  celebrityEditFormRef.value.edit(record, payableAmount, status);
  celebrityEditFormRef.value.title = "编辑网红";
  celebrityEditFormRef.value.disableSubmit = false;
}

function handlePayment(record) {
  storeSellerPromotionGoodsPaymentModalFormRef.value.getPromotionGoods(record);
  storeSellerPromotionGoodsPaymentModalFormRef.value.title = "推广支付";
  storeSellerPromotionGoodsPaymentModalFormRef.value.disableSubmit = false;
  storeSellerPromotionGoodsPaymentModalFormRef.value.promId = record.id;
}

function handleRefund(record) {
  console.log(record);
  storePromotionGoodsRefundModalFormRef.value.getPromotionGoods(record);
  storePromotionGoodsRefundModalFormRef.value.title = "推广线下退款";
  storePromotionGoodsRefundModalFormRef.value.disableSubmit = false;
  storePromotionGoodsRefundModalFormRef.value.promId = record.id;
}

function addProducts(record) {
  addProductsModalFormRef.value.add(record.id);
  addProductsModalFormRef.value.title = "新增产品";
  // addProductsModalFormRef.value.promId = record.id;
}


function modalFormOk() {
  expandedRowKeys.value = [];
  fetchTable();
  handleExpand(true, {
    id: dataSourceId.value,
  });
}



async function handleExpand(expanded, record) {
  if (selectedRowKeys.value.length > 0 && showBatchModify.value) {
    $message.error("正在进行批量操作，先取消或者保存批量操作");
    return;
  }
  promId.value = record.id;
  expandedRowKeys.value = [];
  selectedRowKeys.value = [];
  innerData.value = [];
  if (expanded === true) {
    innerLoading.value = true;
    dataSourceId.value = record.id;
    expandedRowKeys.value.push(record.id);
    if (record.payStatus != "" && record.payStatus != null) {
      expandedRowPayStatus.value = record.payStatus;
    } else {
      var filter = dataSource.value.filter((item) => item.id == record.id);
      if (filter.length > 0) {
        expandedRowPayStatus.value = filter[0].payStatus;
      }
    }
    const res = await innerTableListApi({
      promId: record.id,
    });
    if (res) {
      for (let index = 0; index < res.length; index++) {
        const element = res[index];
        element.isOpenSchedule = false;
        element.isOpen = false;
        element.loading = false;
        element.isRemarkView = false;
        element.mediaUploadsEdit = undefined;
        element.onlineTimeEdit = undefined;
        element.viewLoading = undefined;
      }
      innerData.value = res;
    }
  }
  innerLoading.value = false;
}

function toEdit(record) {
  addProductsModalFormRef.value.edit(record);
}

async function getOldCelebrity(id) {
  historicalMatchModalFormRef.value.init(id);
}


function addStoreUserPromotionModalOk() {
  fetchTable(1);
}
</script>
<style lang="less" scoped>
.remarkView {
  position: relative;

  .remarkViewBox {
    position: absolute;
    z-index: 999;
  }
}

.yes_btn {
  cursor: pointer;
}

.font {
  font-size: 12px;
  color: #bebebe;
  font-weight: 400;
}

.font-rmb {
  font-size: 16px;
  font-weight: 700;
}

.font-symbol {
  font-size: 12px;
}

.copy {
  cursor: pointer;
}

.copy:hover {
  /* color: #1890ff; */
}

.del-btn {
  margin-left: 8px;
  background-color: #ff4d4f;
  color: white;
}

.del-btn:hover {
  background-color: #ff7875;
}
.menu_item {
  font-size: 12px;
}
.action-cursor {
  cursor: pointer;
}
.textarea1 {
  line-height: 20px !important;
  font-size: 14px !important;
}
</style>
