<template>
  <a-modal
    class="merchantDemandModal"
    :maskClosable="false"
    title="推广需求"
    :width="500"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" :rules="rules" ref="formRef" layout="vertical">
        <a-form-item label="产品链接" name="productUrl">
          <a-input v-model:value="model.productUrl" placeholder="产品链接" />
        </a-form-item>
        <a-form-item label="推广平台" name="platformTypeArr" class="platform-card-group">
          <a-checkbox-group
                style=" width: 100%"
                v-model:value="model.platformTypeArr"
              >
                <div style="display: flex; height: 32px; width: 100%">
                  <div
                    class="platform-card"
                    :style="{
                      borderRadius: '4px 0 0 4px',
                    }"
                    :class="{ CheckboxChecked: model.platformTypeArr.includes('2') }"
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
                      CheckboxChecked: model.platformTypeArr.includes('1'),
                      'platform-card-show-left': !model.platformTypeArr?.includes('2'),
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
                    :class="{
                      CheckboxChecked: model.platformTypeArr.includes('0'),
                      'platform-card-show-left': !model.platformTypeArr?.includes('2') && !model.platformTypeArr?.includes('1'),
                    }"
                    :style="{
                      borderRadius: '0 4px 4px 0',
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
        <a-form-item label="网红要求" name="promRequirement">
          <a-textarea
            v-model:value="model.promRequirement"
          
            placeholder="请输入达人类型，粉丝量，均播等要求"
            :auto-size="{ minRows: 5, maxRows: 5 }"
            
          />
        </a-form-item>
        <a-form-item label="推广预算" name="promBudget">
          <a-input style="width: 100%" v-model:value="model.promBudget" placeholder="填写单个商品预算或打包预算" />
        </a-form-item>
        <a-form-item label="联系方式" name="contactInfo">
          <a-input v-model:value="model.contactInfo"  :maxlength="100" placeholder="请输入联系方式" />
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-input    :maxlength="50" v-model:value="model.remark" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref,h } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { PictureOutlined } from '@ant-design/icons-vue';
const { createMessage: $message } = useMessage();
import { useUserStore } from '/@/store/modules/user';
const userStore = useUserStore();
// 表单引用
const formRef = ref();

// 状态
const visible = ref(false);
const confirmLoading = ref(false);
const rules = ref({
  productUrl: [
    { required: true, message: '请输入产品链接' },
    { type: 'url', message: '请输入有效的产品链接', trigger: 'blur' }
  ],
  platformTypeArr: [{ required: true, message: '请选择推广平台' }],
  countryId: [{ required: true, message: '请选择推广国家' }],

  contactInfo: [{ required: true, message: '请输入联系方式' }],

})
const url = ref({
  add:"/storeSellerPromotion/sellerAdd",
  edit:"/storeSellerPromotion/sellerEdit"
})

// 表单数据
const model = ref({
  
});
const countryList = ref([]);

async function initCountryList() {
  const res = await defHttp.post({
    url: '/storecountry/storeCountry/getCommonCountry',
   
  });
  countryList.value = Array.isArray(res?.result) ? res.result : Array.isArray(res) ? res : [];
}


function add() {
  model.value = {}
  let contactInfo =   userStore.userInfo.email || userStore.userInfo.phone || undefined;
  
  edit({platformTypeArr:["2"],contactInfo});
}

function edit(record) {
  Object.assign(model.value, record);
  console.log(model.value)
  
  visible.value = true;
 
}

// 提交
async function handleOk() {
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    
    const httpurl = model.value.id ? url.value.edit : url.value.add;
    const method = model.value.id ? 'put' : 'post';
    const {id,productUrl,platformTypeArr,promRequirement,promBudget,contactInfo,remark} = model.value
    const params = {
      productUrl,
      promPlatform:platformTypeArr.join(','),
      promRequirement,
      promBudget,
      contactInfo,
      remark
    }
    if(id){
      params.id = id;
    }
    console.log(httpurl)
    const res = await defHttp.request({
      url: httpurl,
      method,
      data: params,
    },{isTransformResponse:false});
    if (res.success) {
      $message.success(res.message);
      handleCancel();
      emit('ok');
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
  visible.value = false;
  Object.assign(model.value, {
  });
  formRef.value?.resetFields();
}
const emit = defineEmits(['ok', 'close']);
// 暴露方法
defineExpose({

  add,
  edit,
  close,
});

// 定义事
</script>
<style scoped lang="less" >
 .platform-card {
  border: 1px solid #d9d9d9;
  border-left: none;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.platform-card:first-child {
  border-left: 1px solid #d9d9d9;
}
.platform-card label{
  width: 100%;
  margin: 0 auto;
  justify-content: center;
}
.platform-card-yt {
  /* 中间卡片不再特殊处理左右边框，保持统一分割线 */
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
/* YT/IG 单独选中时显示左边框（作为最左侧卡片） */
.platform-card-show-left {
  border-left: 1px solid #d9d9d9 !important;
}
.platform-card-group .CheckboxChecked.platform-card-show-left {
  border-left-color: @primary-color !important;
}

</style>
<style >
.merchantDemandModal .ant-modal-body{
  padding: 10px 20px !important;

}
</style>