<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
      <a-form  @keyup.enter="searchQuery" class="searchForm">
        <a-row :gutter="12">
          <!-- <a-col :span="2">
            <a-form-item>
              <a-input v-model:value="queryParam.extensionCode" placeholder="推广单号"></a-input>
            </a-form-item>
          </a-col> -->
          <a-col :span="3">
            <a-form-item>
              <!-- <a-select
              
                v-model:value="queryParam.promPlatform"
                placeholder="推广平台"
              >
                <a-select-option :value="2">TK</a-select-option>
                <a-select-option :value="1">YT</a-select-option>
                <a-select-option :value="0">IG</a-select-option>
               
              </a-select> -->
              <JDictSelectPlatformType  v-model:value="queryParam.promPlatform" dictCode="platform_type" />
            </a-form-item>
          </a-col>
          <!-- <a-col :span="2"> 
            <a-form-item>
              <a-select
              
                v-model:value="queryParam.countryId"
                placeholder="推广国家"
              >
                <a-select-option :value="0" v-for="(item, key) in countrys" :key="key">
                  {{ item.country }}
                </a-select-option>
              </a-select>
            
            </a-form-item>
          </a-col> -->
          <a-col :span="2"> 
            <a-form-item>
              <a-input
                v-model:value="queryParam.promBudget"
                placeholder="推广预算"
              ></a-input>
          
                        
            
            </a-form-item>
          </a-col>
          <a-col :xl="4">
            <a-form-item>
             
              <a-button   type="primary" @click="searchQuery" :icon="h(SearchOutlined)">
               
                查询
              </a-button>
              <a-button style="margin-left: 8px" @click="searchReset" :icon="h(ReloadOutlined)">
             
                重置
              </a-button>
              <a-button
              style="margin-left: 8px"
              @click="handleAdd"
              type="primary"
         
          
              :icon="h(PlusOutlined)"
              >
          
              新增需求
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
       :scroll="{ y: sTableHeight }"
       @change="pageChange"
      >
        <template #bodyCell="{ record, column }">
          <!--推广单号-->
          <template v-if="column.key === 'extensionCode'">
            <a @clikc="copyFn(record.extensionCode)" title="点击复制" style="color: #3155ed">
              {{ record.extensionCode }}
            </a>
          </template>
          <template v-if="column.key === 'productUrl'">
            <a @click="openUrl(record.productUrl)" target="_blank" style="color: #3155ed">
              <EllipsisTooltip :text="record.productUrl" />
              <!-- {{ record.productUrl }} -->
            </a>
          </template>
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
          <template v-else-if="column.key === 'approvedStatus'">
            <a-tag color="orange" style="margin-right: 0" v-if="record.approvedStatus === 0">等待审核</a-tag>
            <a-tag color="red" style="margin-right: 0" v-if="record.approvedStatus === -1">审核驳回</a-tag>
            <a-tag color="blue" style="margin-right: 0" v-if="record.approvedStatus === 1">审核通过</a-tag>
          </template>
          <!--推广标题-->
          <template v-else-if="column.key === 'countryName'">
            {{ record.countryName }}
          </template>
          <!--商家名称-->
          <template v-else-if="column.key === 'promRequirement'">
            <!-- <EllipsisTooltip :text="record.promRequirement" /> -->
            {{ record.promRequirement }}
          </template>
          <!--推广费用-->
          <template v-else-if="column.key === 'promBudget'">
                <span>{{ record.promBudget || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'remark'">
            <!-- <EllipsisTooltip :text="record.remark" /> -->
            {{ record.remark }}
          </template>
          <template v-if="column.key == 'action'">
            <a-tooltip title="编辑" placement="bottom">
              <a @click="handleEdit(record)" :disabled="record.approvedStatus !==0">
               
                <FormOutlined  style="font-size: 15px" />
              </a>
            </a-tooltip>

            <a-divider type="vertical"   />
            <a-popconfirm placement="topRight"  title="确定删除吗?"  @confirm="() => handleDelete(record.id)">
            <a :disabled="record.approvedStatus !==0">
              <span class="icon iconfont icon-shanchu"></span>
            </a>
          </a-popconfirm>
          </template>
        </template>
     
     
        <!--------------------------------------------------产品 end--------------------------------------------------->
      </a-table>
      <!-------------------------------------------------需求 end ----------------------------------------------------->
    </div>

    <merchantDemandModal
      ref="modalFormRef"
      @ok="modalFormOk"
    ></merchantDemandModal>
  
  </a-card>
</template>

<script setup name="merchantDemandModal">

import merchantDemandModal from "./modules/merchantDemandModal.vue";
import { ref, onMounted, nextTick,h } from 'vue';
import { defHttp } from '/@/utils/http/axios';
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
import { SearchOutlined, ReloadOutlined, PlusOutlined, FormOutlined } from '@ant-design/icons-vue';
import { merchantDemandListApi, deleteStoreSellerPromotionApi, checkCelebrityStatusForListApi, innerTableListApi, mediaUploadsEditApi } from '/@/api/promotionManagement/promotionManagementApi';
import { useTable } from '/@/components/useSTable/useSTable';
import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
import EllipsisTooltip from "@/components/jeecg/EllipsisTooltip.vue";
import { Model } from "echarts";
// const store = useStore();
const { createMessage} = useMessage();
const countrys = ref([])
const modalFormRef = ref(null);
import JDictSelectPlatformType from "@/components/jeecg/JDictSelectPlatformType.vue";
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
        // {
        //   title: "推广单号",
        //   // align: 'center',
        //   width: 250,
        //   dataIndex: "extensionCode",
        //   key: "extensionCode",
        // },
        {
          
          title: "产品链接",
          // align: 'center',
          width: 300,
          dataIndex: "productUrl",
          key: "productUrl",
        },
        {
          
          title: "推广平台",
          // align: 'center',
          width: 200,
          dataIndex: "promPlatform",
          key: "promPlatform",
        },
        {
          
          title: "审核状态",
          // align: 'center',
          width: 150,
          dataIndex: "approvedStatus",
          key: "approvedStatus",
        },
        {
          
          title: "驳回原因",
          // align: 'center',
          width: 200,
          dataIndex: "approvedFailReason",
          key: "approvedFailReason",
        },
        
        {
          title: "网红要求",
          dataIndex: "promRequirement",
          // align: 'center',
          dataIndex: "promRequirement",
          key: "promRequirement",
        },
      
        {
          title: "推广预算",
          // align: 'center',
          width: 120,
          dataIndex: "promBudget",
          key: "promBudget",
        },
          {
            title: "联系方式",
            // align: 'center',
            width: 200,
            dataIndex: "contactInfo",
            key: "contactInfo",
          },
          {
            title: "备注",
            // align: 'center',
            width: 200,
            dataIndex: "remark",
            key: "remark",
          },
        {
          title: "操作",
          dataIndex: "action",
          // align: 'center',
          width: 100,
          key: "action",
        },
      ]);


const { dataSource, pagination, loading, fetchTable, queryParam, searchQuery: searchQueryHook, searchReset: searchResetHook, pageChange,calcTableHeight, sTableHeight,copyFn } = useTable(
  merchantDemandListApi,0,{tableType:'aTbale',afterFetch:(params,res) => {
    // console.log(res)
    nextTick(() => {
      calcTableHeight('aTbale')
    })
   
  }});
const handleDelete = async (id) => {
  try {

    const res =  await defHttp.delete({ url: '/storeSellerPromotion/delete', data: { id } },{ joinParamsToUrl: true, isTransformResponse: false });
    if (res.success) {
      createMessage.success(res.message);
      fetchTable();
    } else {
      createMessage.error(res?.message || '删除失败');
    }
   
  } catch (error) {
    console.error(error);
  }
};

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




function openUrl(url) {
  let s1 = url.split(":")[0].toLowerCase();
  let s = s1 == "http" || s1 == "https" ? url : "http://" + url;
  window.open(s, "_blank");
}




function getPromAmount(record) {
  if (record.promBudget > 0 && record.exchangeRate > 0) {
    const promBudget = floatMultiply(record.promBudget, record.exchangeRate) || 0;
    return splitAmount(promBudget);
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



function modalFormOk() {
  fetchTable(1);
  // getUserInfo();

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

}

function searchReset() {
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
  searchResetHook();
}



function handleAdd() {
  modalFormRef.value.add();
}
function handleEdit(record) {
  const newRecord = {
    ...record,
    platformTypeArr: record.promPlatform.split(','),
  }
  modalFormRef.value.edit(newRecord);
}

fetchTable()
</script>

