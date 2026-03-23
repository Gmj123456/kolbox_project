<template>
  <a-modal
    title="审核"
    :width="840"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    centered
    @cancel="handleCancel"
    :maskClosable="false"
    cancelText="关闭"
    class="storeSellerPromotionListModal"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="formState" :rules="rules" ref="formRef" layout="vertical">
        <a-row  :gutter="12">

          <a-col :span="24" style="margin-bottom: 6px;">
            <div style="display: flex;align-items: center; gap: 2px;">
              <div style="border-radius: 4px;width: 4px;
    height: 18px;
    background: #3155ED;">
                
              </div>
              <div style="color: #0b1019;">
                需求信息
              </div>
            </div>
          </a-col>
         
          <a-col :span="12">
            <a-form-item  label="商务人员" name="counselorUserId">
        
       
              <a-select
              style="width: 100%"
              placeholder="请选择商务人员"
              v-model:value="formState.counselorUserId"
              @change="changeCounselorUserId"
            >
              <a-select-option v-for="item in businessPeopleList" :key="item.id" :value="item.id">
                {{ item.realname }}
              </a-select-option>
            </a-select>
           
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="商务顾问" name="sellerCounselorId">
            <!-- <a-input  style="color:rgba(51, 51, 51, 0.88)" v-model:value="formState.sellerName" disabled></a-input> -->

            <a-select
              style="width: calc(100% - 110px)"
              placeholder="请选择商家顾问"
              v-model:value="formState.sellerCounselorId"
         
            >
              <a-select-option v-for="item in userCounselorList" :key="item.id" :value="item.id">
                {{ item.username }}
              </a-select-option>
            </a-select>
            <a-button :icon="h(PlusOutlined)" style="margin-left: 8px" ghost type="primary" @click="addUserCounselor">
           
              新增顾问
            </a-button>
   
           
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="商家名称">
              <a-input style="color:rgba(51, 51, 51, 0.88)" v-model:value="formState.sellerName" disabled></a-input>
            </a-form-item>
           </a-col>
          <a-col :span="12">
            <a-form-item name="promTitle" label="推广标题">
              <a-input
                
                placeholder="请输入推广标题"
                v-model:value="formState.promTitle"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item name="productBrand" label="推广品牌">
              <a-select
                
                disabled
                mode="multiple"
                placeholder="请选择推广品牌"
                v-model:value="formState.productBrand"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item name="promPlatform" label="推广平台">
              <a-checkbox-group v-model:value="formState.promPlatform" style="width: 100%">
                <div style="width: 100%;display: flex;gap:8px">
                  <div class="prom-platform-item" :style="{borderColor:formState.promPlatform.includes('2')?'#3155ed':' #d9d9d9',}">
                    <a-checkbox value="2">
                      <div style="display: flex; align-items: center; gap: 4px;">
                        <img src="@/assets/images/tk.png" alt="TK" style="width: 20px; height: 20px; margin-right: 4px"/>
                        <span :style="{color:formState.promPlatform.includes('2')?'#3155ed':'#000'}">TK</span>
                      </div>
                      
                    </a-checkbox>
                  </div>
                
                  <div class="prom-platform-item" :style="{borderColor:formState.promPlatform.includes('1')?'#3155ed':' #d9d9d9',}">
                    

                    <a-checkbox value="1">
                      <div style="display: flex; align-items: center; gap: 4px;">
                        <img src="@/assets/images/yt.png" alt="YT" style="width: 20px; height: 20px; margin-right: 4px"/>
                        <span :style="{color:formState.promPlatform.includes('1')?'#3155ed':'#000'}">YT</span>
                      </div>
                     
                    </a-checkbox>
                  </div>
                  <div class="prom-platform-item" :style="{borderColor:formState.promPlatform.includes('0')?'#3155ed':' #d9d9d9',}">
                    
                    <a-checkbox value="0">
                       <div style="display: flex; align-items: center; gap: 4px;">
                        <img src="@/assets/images/ins.png" alt="IG" style="width: 20px; height: 20px; margin-right: 4px"/>
                        <span :style="{color:formState.promPlatform.includes('0')?'#3155ed':'#000'}">IG</span>
                       </div>
  
                      </a-checkbox>
                    </div>
                </div>
               
                <!-- <a-row :gutter="0" >
                  <a-col :span="7" class="prom-platform-item">
                   
                  </a-col>
                  <a-col :span="1"></a-col>
                  <a-col :span="8" class="prom-platform-item">
                  
                  </a-col>
                  <a-col :span="1"></a-col>
                  <a-col :span="7" class="prom-platform-item">
                    <a-checkbox value="2">
                      <div style="display: flex; align-items: center; gap: 4px;">
                        <img src="@/assets/images/tk.png" alt="TK" style="width: 20px; height: 20px; margin-right: 4px"/>
                        <span>TK</span>
                      </div>
                     
                    </a-checkbox>
                  </a-col>
                </a-row> -->
              
              </a-checkbox-group>
              <!-- <a-select
                mode="multiple"
                
                placeholder="请输入推广平台"
                v-model:value="formState.promPlatform"
              >
                <a-select-option value="0">IG</a-select-option>
                <a-select-option value="1">YT</a-select-option>
                <a-select-option value="2">TK</a-select-option>
              </a-select> -->
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item name="promTags" label="推广类目">
              <a-tree-select
                
                v-model:value="formState.promTags"
                show-search
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                placeholder="请选择推广类目"
                allow-clear
                :tree-data="productCategoryList"
                :field-names="{ label: 'categoryName', key: 'id', value: 'id' }"
                
                  tree-node-filter-prop="categoryName"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item name="promBudget" label="商家预算">
              <a-input style="width: 100%" v-model:value="formState.promBudget" placeholder="请输入商家预算" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item name="promExample" label="案例链接">
              <div class="ant-form-item-control" :class="{ 'has-error': exampleText !== '' }">
                <span class="ant-form-item-children">
                  <a-textarea
                    
                    placeholder="请输入案例链接"
                    v-model:value="formState.promExample"
                  
                    @change="checkExample"
                  />
                </span>
                <div class="ant-form-explain" style="min-height: 0px">
                  {{ exampleText }}
                </div>
              </div>
            </a-form-item>
          </a-col>

          <a-col :span="24">
            <a-form-item name="promRequirement" label="网红要求" >
              <a-textarea
                
                placeholder="请输入网红要求"
                v-model:value="formState.promRequirement"
              
              />
            </a-form-item>
          </a-col>
           <a-col :span="24">
            <a-form-item name="remark" label="商家备注" >
              <a-textarea
                
                placeholder="请输入商家备注"
                v-model:value="formState.remark"
              
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :span="12">
          <a-col :span="24">
           <div style="display: flex; align-items: center; justify-content: space-between;">
            <div style="display: flex;align-items: center; gap: 2px;">
              <div style="border-radius: 4px;width: 4px;
    height: 18px;
    background: #3155ED;">
                
              </div>
              <div style="color: #0b1019;">
                产品信息
              </div>
            </div>
            <a-button
              style="background-color: #fff !important"
              ghost
              :icon="h(PlusOutlined)"
              type="primary"
              @click="addProduct"
            >
            
              维护新产品
            </a-button>
           </div>
          </a-col>
          <a-col :span="24">
            
            <div class="product-box">
              
              <div class="product-content">
                <div
                  class="product-item"
                  v-for="(item, index) in productModel.goodsList"
                  :key="index"
                >
                  <div class="product-index">
                    {{ index + 1 }}
                  </div>
                  <!--   style="background-color: #FBFBFB;color: #fff;" -->
                  <div class="product-right">
                    <a-avatar
                    
                      shape="square"
                      :size="64"
                     :icon="h(PictureOutlined)"
                      :src="item.goodsPicUrl"
              
                    />
                    <div style="width: calc(100% - 100px)">
                      <div class="product-item-content" style="display: flex; align-items: center; gap: 10px;width: 100%;    margin-bottom: 4px;">
                        <a-form-item >
                          <a-select
                          style="flex:1"
                            @change="(value) => brandChange(value, index)"
                            allowClear
                            showSearch
                            option-filter-prop="label"
                            placeholder="品牌"
                            v-model:value="item.brandId"
                          >
                            <a-select-option v-for="brand in brandList" :key="brand.id" :value="brand.id" :label="brand.brandName">
                              {{ brand.brandName }}
                            </a-select-option>
                          </a-select>
                        </a-form-item>
                        <a-form-item style="width: 100%">
                          <a-select
                          
                            showSearch
                            option-filter-prop="label"
                            @change="(value) => onProductChange(value, index)"
                            placeholder="产品"
                            v-model:value="item.kolProductId"
                            allowClear
                          >
                            <a-select-option
                              v-for="product in item.productList"
                              :key="product.id"
                              :value="product.id"
                              :label="product.text"
                            >
                              {{ product.text }}
                            </a-select-option>
                          </a-select>
                        </a-form-item>
                        <a-form-item style="width: 100%">
                          <a-input  style="color:rgba(51, 51, 51, 0.88)" disabled placeholder="产品类目" v-model:value="item.categoryName" />
                        </a-form-item>
                      </div>
                      <div class="product-item-content" style="display: flex; align-items: center; gap: 10px;width: 100%">
                        <a-form-item >
                          <a-input  style="color:rgba(51, 51, 51, 0.88)" disabled placeholder="产品链接" v-model:value="item.goodsUrl" />
                        </a-form-item>
                      </div>
                    </div>
                    <div
                      style="
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    
                      "
                    >
                      <CloseOutlined
                        @click="delRowCustom(index)"
                        style="font-size: 18px; color: #cecece; cursor: pointer"
                      />
                    </div>
                  </div>
                </div>
                <div>
                  <a-button   :icon="h(PlusOutlined)"  @click="addRowCustom" style="width: 100%; height: 32px;color:#3155ED">
                  
                    选择需求产品
                  </a-button>
                </div>
              </div>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <template #footer>
      <a-button type="primary" :loading="confirmLoading" @click="handleReject">驳回</a-button>
      <a-button key="back" @click="handleCancel" :loading="confirmLoading">取消</a-button>
      <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">通过</a-button>
    </template>
    <a-modal v-model:open="rejectOpen" title="驳回" @ok="handleRejectOk" >
     <a-form-model :model="rejectForm" >
     <div style="display: flex;align-items: center;gap: 4px;">
      <ExclamationCircleFilled  style="font-size: 16px;color:orange"/>
      <span style="font-size: 12px;color:black;">   是否确定驳回</span>
     </div>
      <a-form-item >
        <a-textarea v-model:value="rejectForm.approvedFailReason" :autosize="{ minRows: 5, maxRows: 8 }" placeholder="请输入驳回原因" />
      </a-form-item>
     </a-form-model>
    </a-modal>
    <AddUserCounselorModal ref="addUserCounselorModalRef" @ok="addUserCounselorOk" />
    <AddSellerCounselorModal ref="addSellerCounselorModalRef" @ok="addSellerCounselorOk" />
    <!-- <AddProductBrandModal ref="addProductBrandModalRef" @ok="addProductBrandOk" /> -->
    <AddProductModal ref="addProductModalRef" @ok="addProductOk" @update="updateBrand" />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick,h } from 'vue';
import { PlusOutlined, CloseOutlined, PictureOutlined,ExclamationCircleFilled } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useUserStore } from '/@/store/modules/user';
import { useMessage } from '/@/hooks/web/useMessage';
import AddUserCounselorModal from './AddUserCounselorModal.vue';
import AddSellerCounselorModal from './AddSellerCounselorModal.vue';
// import AddProductBrandModal from './addProductBrandModal.vue';
import AddProductModal from '@/views/basiSettings/productionInformation/modules/productionInformationModal.vue';

const { createMessage: $message } = useMessage();
const { userInfo } = useUserStore();

// 表单引用
const formRef = ref();
const addUserCounselorModalRef = ref();
const addSellerCounselorModalRef = ref();
// const addProductBrandModalRef = ref();
const addProductModalRef = ref();
let rejectOpen = ref(false);
// 状态
const visible = ref(false);
const confirmLoading = ref(false);
const exampleText = ref('');
let goodsUrl = ref('')
const productModel = reactive<{ id?: number; goodsList: any[] }>({
  goodsList: [{}],
});
const rejectForm = ref({})
// 表单数据
const formState = reactive({
  sellerCounselorId: undefined,
  sellerId: undefined,
  promTitle: '',
  productBrand: [] as string[],
  promPlatform: ['0', '1', '2'],
  promTags: undefined,
  promExample: '',
  promRequirement: '',
  promBudget: undefined,
  remark: undefined,
  sellerCounselorName: undefined,
  sellerName: undefined,
  counselorUserId: undefined,
});

// 表单验证规则
const rules = {
  counselorUserId: [{ required: true, message: '请选择商务人员' }],

  sellerCounselorId: [{ required: true, message: '请选择商家顾问' }],
  sellerId: [{ required: true, message: '请选择商家名称' }],
  promTitle: [{ required: true, message: '请输入推广标题' }],
  promPlatform: [{ required: true, message: '请选择推广平台' }],
  promTags: [{ required: true, message: '请选择推广类目' }],
};

// 布局配置

// 数据列表
const userCounselorList = ref<any[]>([]);
const sellerCounselor = ref<any[]>([]);
const productCategoryList = ref<any[]>([]);
const brandList = ref<any[]>([]);
const businessPeopleList = ref<any[]>([])

// 工具函数
async function getBusinessPeople() {
  try {
    const res = await defHttp.get({ url: '/sys/user/getBusinessPeople' });
    if (res) {
      businessPeopleList.value = res;
    }
  } catch (error) {
    console.error('getBusinessPeople error:', error);
  }
}
function changeCounselorUserId(value: number) {
  userCounselorList.value = []
  formState.sellerCounselorId = undefined
  if (value){

    storeUserCounselor()
  }
}
const filterTreeNode = (inputValue: string, treeNode: any) => {
  return treeNode.title.toLowerCase().indexOf(inputValue.toLowerCase()) === 0;
};
function updateBrand() {
  initBrandList();
}
async function addProductOk(model){
  const filteredList = productModel.goodsList.filter(item => !item.branId && !item.kolProductId);
  console.log(filteredList)
  if (filteredList.length > 0) {
    const res = await defHttp.get({
      url: '/kol/kolProduct/listAll',
      params: {
        brandId: model.brandId,
      },
    });
    if (res) {
      filteredList[0].brandId = model.brandId;
      filteredList[0].productList = res.map((p: any) => ({
        ...p,
        text: p.productName,
      }));
      filteredList[0].kolProductId = filteredList[0].productList.find(p => p.productName === model.productName)?.id;
      updateProductBrands();
      const product = filteredList[0].productList?.find((p) => p.id === filteredList[0].kolProductId);
      if (product) {
      
        filteredList[0].categoryName = product.categoryName || '';
        filteredList[0].goodsUrl = product.productUrl || '';
        filteredList[0].productImage = product.productImage || '';
        filteredList[0].goodsTitle = product.productName || '';
        filteredList[0].goodsPicUrl = product.productImage || '';
      } else {
        filteredList[0].categoryName = undefined;
        filteredList[0].goodsUrl = undefined;
        filteredList[0].productImage = undefined;
        filteredList[0].goodsTitle = undefined;
        filteredList[0].goodsPicUrl = undefined;
      }
    }
   
    
    
  }else{
    addRowCustom()
    // await initProduct(productModel.goodsList.length - 1)
    brandChange(model.brandId, productModel.goodsList.length - 1,false)
    await initProduct(productModel.goodsList.length - 1)
   let productId = productModel.goodsList[productModel.goodsList.length - 1]?.productList.find((p) => p.productName === model.productName)?.id;
   console.log(productId)
    onProductChange(productId, productModel.goodsList.length - 1)
    
  }
//  await initProduct()
}
// 初始化数据
async function initBrandList() {
  const res = await defHttp.get({
    url: '/kolBrand/listAll',
  });
  if (res) {
    brandList.value = res;
  }
}

async function initProductCategory() {
  const res = await defHttp.get({
    url: '/kol/category/loadTreeDataAll',
  });
  if (res) {
    productCategoryList.value = res;
  }
}

async function storeUserCounselor() {
  if (!userInfo?.id) return;
  const res = await defHttp.get({
    url: '/storeUserCounselor/getList',
    params: {
      sysUserId: formState.counselorUserId,
    },
  });
  if (res) {
    userCounselorList.value = res;
  }
}

async function getSellerList(sellerCounselorId: number) {
  const res = await defHttp.get({
    url: '/sys/user/getSellerList',
    params: { sellerCounselorId },
  });
  if (res) {
    sellerCounselor.value = res;
  } else {
    sellerCounselor.value = [];
  }
}

// 品牌变更
async function brandChange(value: number | undefined, index: number, isInit = true) {
  const item = productModel.goodsList[index];
  if (!item) return;

  // 重置相关字段
  item.brandId = value;
  item.productList = [];
  item.kolProductId = undefined;
  item.categoryName = undefined;
  item.goodsUrl = undefined;
  item.brandName = undefined;
  item.goodsTitle = undefined;
  item.goodsPicUrl = undefined;

  if (value) {
    const brand = brandList.value.find((b) => b.id === value);
    if (brand) {
      item.brandName = brand.brandName;
    }
    if (isInit) {
      await initProduct(index);
    }
  }

  // 更新推广品牌
  updateProductBrands();
}

// 初始化产品列表
async function initProduct(index: number) {
  const item = productModel.goodsList[index];
  if (!item || !item.brandId) return;

  const res = await defHttp.get({
    url: '/kol/kolProduct/listAll',
    params: {
      brandId: item.brandId,
    },
  });
  if (res) {
    item.productList = res.map((p: any) => ({
      ...p,
      text: p.productName,
    }));
  }
}

// 产品变更
function onProductChange(value: number | undefined, index: number) {
  const item = productModel.goodsList[index];
  if (!item) return;

  const product = item.productList?.find((p) => p.id === value);
  if (product) {
    item.kolProductId = value || null;
    item.categoryName = product.categoryName || '';
    item.goodsUrl = product.productUrl || '';
    item.productImage = product.productImage || '';
    item.goodsTitle = product.productName || '';
    item.goodsPicUrl = product.productImage || '';
  } else {
    item.categoryName = undefined;
    item.goodsUrl = undefined;
    item.productImage = undefined;
    item.goodsTitle = undefined;
    item.goodsPicUrl = undefined;
  }
}

// 更新推广品牌
function updateProductBrands() {
  const productBrands = [
    ...new Set(productModel.goodsList.map((item) => item.brandName).filter(Boolean)),
  ];
  formState.productBrand = productBrands as string[];
}

// 商家顾问变更
function changeSellerCounselor(value: number) {
  formState.sellerId = undefined;
  getSellerList(value);
}

// 案例链接验证
function checkExample(e: any) {
  const value = e.target.value;
  if (!value) {
    exampleText.value = '';
    return;
  }

  const urlList = value.split('\n');
  if (urlList.length > 2) {
    exampleText.value = '参考链接最多为2条';
    return;
  }

  const strRegex = /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/;
  const re = new RegExp(strRegex, 'i');
  for (const url of urlList) {
    if (!re.test(encodeURI(url))) {
      exampleText.value = '参考链接格式错误';
      return;
    }
  }
  exampleText.value = '';
}

// 查找树节点
function findNodeInTree(targetValue: any, tree: any[] = productCategoryList.value): any {
  if (!tree || !Array.isArray(tree)) return null;

  for (const node of tree) {
    if (node.id === targetValue) return node;
    if (node.children?.length) {
      const found = findNodeInTree(targetValue, node.children);
      if (found) return found;
    }
  }
  return null;
}

// 添加/删除产品行
function addRowCustom() {
  productModel.goodsList.push({});
}

function delRowCustom(index: number) {
  if (productModel.goodsList.length === 1) {
    $message.warning('至少添加一条产品信息！');
    return;
  }
  productModel.goodsList.splice(index, 1);
  updateProductBrands();
}

// 模态框相关
function addUserCounselor() {
  if (formState.counselorUserId){
    console.log(  addUserCounselorModalRef.value)
    addUserCounselorModalRef.value?.add({counselorUserId:formState.counselorUserId,counselorUserName:businessPeopleList.value.find((item) => item.id === formState.counselorUserId)?.realname});
  }else{
    $message.warning('请选择商务人员！');
    addUserCounselorModalRef.value?.add();
  }
}

function addUserCounselorOk(e) {

  storeUserCounselor();
}

function addSellerCounselor() {
  if (!formState.sellerCounselorId) {
    $message.warning('请选择商家顾问！');
    return;
  }
  const counselor = userCounselorList.value.find((item) => item.id === formState.sellerCounselorId);
  if (counselor) {
    addSellerCounselorModalRef.value?.add({
      sellerCounselorId: formState.sellerCounselorId,
      sellerCounselorName: counselor.username,
    });
  }
}

function addSellerCounselorOk() {
  getSellerList(formState.sellerCounselorId!);
}

function addProduct() {
  addProductModalRef.value?.merchantDemandModalAdd(goodsUrl.value);
  // addProductModalRef.value.title = '新增产品';

}

// function addProductOk() {
//   // 产品添加成功后的处理
// }

// function updateBrand() {
//   initBrandList();
// }

// function addProductBrandOk() {
//   // 品牌添加成功后的处理
// }

// 打开/关闭
function close() {
  visible.value = false;
  productModel.goodsList = [{}];
  exampleText.value = '';
  confirmLoading.value = false;
  formRef.value?.resetFields();
  Object.assign(formState, {
    sellerCounselorId: undefined,
    sellerId: undefined,
    promTitle: '',
    productBrand: [],
    promPlatform: ['0', '1', '2'],
    promTags: undefined,
    promExample: '',
    promRequirement: '',
  });
}

async function add() {
  productModel.goodsList = [{}];
  formRef.value?.resetFields();
  visible.value = true;
  await Promise.all([
    // storeUserCounselor(),
    initBrandList(),
    initProductCategory(),
  ]);
}

async function edit(record) {
  visible.value = true;
  await Promise.all([
    // storeUserCounselor(),
    initBrandList(),
    initProductCategory(),
    getBusinessPeople()
  ]);

  formRef.value?.resetFields();
  Object.assign(productModel, record);
  productModel.goodsList = [{}];

  nextTick(() => {
    goodsUrl.value = record.productUrl
    Object.assign(formState, {
      id: record.id,
      sellerCounselorId:record.sellerCounselorId || undefined,
      sellerId: record.sellerId,
      sellerName: record.sellerName,
      promTitle: record.promTitle,
      promPlatform: record.promPlatform ? record.promPlatform.split(',') : ['0', '1', '2'],
      promTags: record.promTags,
      promExample: record.promExample,
      productBrand: record.productBrand ? record.productBrand.split(',') : [],
      promRequirement: record.promRequirement,
      promBudget: record.promBudget,
      remark: record.remark,
      counselorUserId: record.counselorUserId || undefined,
      // sellerCounselorName: record.sellerCounselorName,
      
    });
    if (record.sellerCounselorId) {
      storeUserCounselor();
    }
  });

}
async function handleRejectOk(){

  const res = await defHttp.put({
    url: '/storeSellerPromotion/rejectPromotion  ',
    data: {
      id:formState.id,
      approvedFailReason:rejectForm.value.approvedFailReason,
    },
  },{isTransformResponse:false})
  if (res.success) {
    $message.success(res.message);
    rejectOpen.value = false;
    emit('ok');
    close();
  } else {
    $message.warning(res.message);
  }
}
async function handleReject(){
  rejectForm.value.approvedFailReason = undefined;
  rejectOpen.value = true;
}
// 提交
async function handleOk() {

  if (
    !Array.isArray(productModel.goodsList) ||
    productModel.goodsList.length === 0 ||
    (productModel.goodsList.length === 1 && Object.keys(productModel.goodsList[0]).length === 0)
  ) {
    $message.warning('推广产品不可为空！');
    return;
  }

  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    const isEdit = !!productModel.id;

    // 检查重复产品
    const kolProductIds = productModel.goodsList
      .map((item) => item.kolProductId)
      .filter((id) => id !== undefined && id !== null);
    const uniqueKolProductIds = [...new Set(kolProductIds)];
    if (kolProductIds.length !== uniqueKolProductIds.length) {
      confirmLoading.value = false;
      return $message.warning('请勿重复选择产品！');
    }

    const counselor = userCounselorList.value.find((item) => item.id === formState.sellerCounselorId);
    const seller = sellerCounselor.value.find((item) => item.id === formState.sellerId);
    const categoryNode = findNodeInTree(formState.promTags);

    const formData: any = {
      ...formState,
      isNewData: 1,
      sellerCounselorName: counselor?.username,
      counselorUserName:businessPeopleList.value.find((item) => item.id === formState.counselorUserId)?.realname,
      promTags: categoryNode?.categoryName,
      promotionType: 1,
      productBrand: formState.productBrand.join(','),
      promPlatform: Array.isArray(formState.promPlatform)
        ? formState.promPlatform.join(',')
        : formState.promPlatform,
      goodsList: productModel.goodsList.map((item) => ({
        ...item,
        productAttributes: JSON.stringify(item.productModel || {}),
      })),
    };
    const res = await defHttp.post({
          url: '/storeSellerPromotion/add',
          data: formData,
        },{isTransformResponse:false})
    if (res.success) {
      $message.success(res.message);
      emit('ok');
      close();
    } else {
      $message.warning(res.message);
    }
  } catch (error) {
    console.error(error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

// 暴露方法
defineExpose({
  add,
  edit,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
  (e: 'close'): void;
}>();
</script>
<style lang="less" >
.storeSellerPromotionListModal .ant-modal-body{
  padding: 10px 20px !important;
  max-height: 859px !important;
  overflow-y: auto !important;
}
.product-item-content .ant-form-item{
  width: 100%;
  margin-bottom: 0 !important;
}
</style>
<style lang="less" scoped>
  .prom-platform-item .ant-checkbox-wrapper{
    display: flex !important;
    align-items: center !important;
  }
  .prom-platform-item{
    flex:1;
    border: 1px solid #e9e9e9;
    border-radius: 5px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
 
  }
.product-box {

  width: 100%;
  border: 1px solid #e9e9e9;
  margin-top: 12px;
  border-radius: 5px;
  background: #f0f3ff;


  .product-content {
    height: 232px;
    overflow-y: auto;
    overflow-x: hidden;
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 10px;

    .product-item {
      display: flex;
      align-items: center;
      border: 1px solid #e9e9e9;
      padding: 5px;
      border-radius: 5px;
      position: relative;
      background: #fff;
      .product-index {
        width: 30px;
        text-align: center;
        margin-top: 5px;
        color: #000;
        margin-right: 5px;
      }

      .product-right {
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
      }
    }
  }
}

.has-error{
  color:red;
}



</style>
