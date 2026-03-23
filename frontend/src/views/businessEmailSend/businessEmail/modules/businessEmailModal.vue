<template>
  <a-modal 
    :title="title" 
    :width="470" 
    v-model:open="visible" 
    :confirmLoading="confirmLoading" 
    switchFullscreen
    centered
    @ok="handleOk" 
    @cancel="handleCancel" 
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24" v-if="!model.id">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱类型"
              name="emailType"
            >
            <a-radio-group v-model:value="model.emailType" @change="emailTypeChange">
              <a-radio-button :value="0">谷歌邮箱</a-radio-button>
              <a-radio-button :value="1">企业邮箱</a-radio-button>
          
            </a-radio-group>
              <!-- <a-input :disabled="model.isAuthorized && model.contactEmail.includes('@gmail.com')" v-model:value="model.contactEmail" placeholder="请输入邮箱" /> -->
            </a-form-item>
          </a-col>
        
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱"
              name="contactEmail"
            >
              <a-input :disabled="model.isAuthorized && model.emailType == 0" v-model:value="model.contactEmail" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱名称"
              name="emailDisplayName"
            >
              <a-input 
                v-model:value="model.emailDisplayName" 
                placeholder="请输入邮箱名称" 
                autocomplete="off"
              />
            </a-form-item>
          </a-col>
          <template v-if="model.emailType == 1">
          
            <a-col :span="24" >
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="邮箱密码"
      
                name="emailPassword"
              >
                <a-input-password  v-model:value="model.emailPassword" placeholder="请输入邮箱密码" autocomplete="off" />
              </a-form-item>
            </a-col>
            <a-col :span="24"  >
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="确认密码"
                name="emailPasswordConfirm"
              >
                <a-input-password  v-model:value="model.emailPasswordConfirm" placeholder="请输入确认密码" />
              </a-form-item>
            </a-col>
          </template>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="邮箱状态"
              name="emailStatus"
            >
              <a-select v-model:value="model.emailStatus" placeholder="请选择邮箱状态">
                <a-select-option :value="1">
                  可用
                </a-select-option>
                <a-select-option :value="0">
                  不可用
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24" v-show="model.emailType == 1">
           
            <a-row :gutter="12">
            
              <a-col v-for="item in stationery" :key="item.id" :span="6" style="margin-bottom: 12px;">
                <a-popover >
                  <template #content>
                    <div
                      style="height: 230px; overflow: hidden; "
                      :style="getStationeryStyle(item)"
                    >
                      <div class="noticetext" style="font-size: 14px;">
                        <h3>您好：</h3>
                        <p>这是一封测试邮件，用来预览信纸的效果。</p>
                        <p>正文 AaBbCc</p>
                      </div>
                    </div>
                  </template>
      
                <div 
                
                  :class="['stationery-item', { 'stationery-item-selected': selectedStationeryId === item.id }]"
                  @click="selectStationery(item)"
                >
                  <img :src="item.imageUrl" style="width: 100%;height: 100%;object-fit: cover;" />
                  <div v-if="selectedStationeryId === item.id" class="stationery-check-icon">
                    <check-outlined />
                  </div>
                </div>
                </a-popover>
              
              </a-col>
            </a-row>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { CheckOutlined } from '@ant-design/icons-vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);
const title = ref("操作");
const visible = ref(false);
const confirmLoading = ref(false);

const model = ref({
  contactEmail: undefined,
  emailDisplayName: undefined,
  emailPassword: undefined,
  emailStatus: undefined,
  emailPasswordConfirm: undefined,
  stationeryId: undefined,
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 4},
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 20 },
};
const stationery = ref([])
const selectedStationeryId = ref(null);
const validatorRules = {
  contactEmail: [
    {
      required: true,
      message: '请输入邮箱!',
      trigger: 'change'
    },
    // { 
    //   pattern: /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/, 
    //   message: '请输入正确格式的邮箱' 
    // },
    {
      // emailType 为 0 时校验谷歌邮箱
      validator: (rule, value, callback) => emailValidator(rule, value, callback),
      trigger: 'change'
    }
  ],
  emailDisplayName: [
    {
      required: true,
      message: '请输入邮箱名称!',
      trigger: 'change'
    }
  ],
  emailPassword: [
    {
      required: true,
      message: '请输入邮箱密码!',
      trigger: 'change'
    }
  ],
  emailPasswordConfirm: [
    {
      required: true,
      message: '请输入确认密码!',
      trigger: 'change'
    }
  ],
  emailStatus: [
    {
      required: true,
      message: '请选择邮箱状态!',
      trigger: 'change'
    }
  ],
};

const url = {
  add: "/storeUserContactEmail/add",
  edit: "/storeUserContactEmail/edit",
};

const emailValidator = (rule, value, callback) => {
  if (model.value.emailType == 0) {
    if (value && !/^[^@]+@(gmail\.com|googlemail\.com)$/i.test(value)) {
      callback('请输入正确的谷歌邮箱');
      return;
    }
  }else{
    if (value && !/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i.test(value)) {
      callback('请输入正确的邮箱');
      return;
    }
  }
  callback();
};

const emailTypeChange = (value) => {
  // model.value.emailType = value;
  // if(value == 0){
  //   model.value.contactEmail = '';
  // }
  model.value.contactEmail = undefined;
  model.value.emailDisplayName = undefined;
  model.value.emailPassword = undefined;
  model.value.emailPasswordConfirm = undefined;
  model.value.emailStatus = undefined;
  selectedStationeryId.value = null;
  model.value.stationeryId = undefined;
  selectedStationeryId.value = undefined
}

// 从 item.code 中提取样式
function getStationeryStyle(item) {
  const baseStyle = {
    height: '230px',
    overflow: 'hidden'
  };
  
  if (item.code) {
    // 使用正则表达式提取 td 标签中的 style 属性
    const styleMatch = item.code.match(/<td[^>]*style=["']([^"']+)["'][^>]*>/i);
    if (styleMatch && styleMatch[1]) {
      const extractedStyle = styleMatch[1];
      // 解析样式字符串，转换为对象
      const stylePairs = extractedStyle.split(';').filter(s => s.trim());
      stylePairs.forEach(pair => {
        const colonIndex = pair.indexOf(':');
        if (colonIndex > 0) {
          const key = pair.substring(0, colonIndex).trim();
          const value = pair.substring(colonIndex + 1).trim();
          // 过滤掉 min-height 样式
          if (key && value && key.toLowerCase() !== 'min-height') {
            // 转换 CSS 属性名为驼峰命名（Vue 支持的格式）
            const camelKey = key.replace(/-([a-z])/g, (g) => g[1].toUpperCase());
            baseStyle[camelKey] = value;
          }
        }
      });
      return baseStyle;
    }
  }
  
  // 如果没有找到 style，使用 imageBg 作为背景
  if (item.imageBg) {
    baseStyle.backgroundImage = `url(${item.imageBg})`;
  }
  
  return baseStyle;
}

const selectStationery = (item) => {
  const id = item.id || item.thumb;
  if (selectedStationeryId.value === id) {
    // 取消选中
    selectedStationeryId.value = null;
    model.value.stationeryId = undefined;
  } else {
    // 选中
    selectedStationeryId.value = id;
    model.value.stationeryId = item.id || item.thumb;
  }
}
async function initStationery() {
  const res = await defHttp.get({ url: "/email/emailStationery/listAll"},{isTransformResponse: false});
  if(res.success){
    stationery.value = res.result;
  }
  
}
function add() {
  edit({emailType: 0,emailStatus: 1});
}

function edit(record) {
  Object.assign(model.value, record);
  initStationery().then(() => {
    // 恢复选中的信纸
    if (model.value.stationeryId) {
      selectedStationeryId.value = model.value.stationeryId;
    } else {
      selectedStationeryId.value = null;
    }
  });
  if (model.value.emailPassword){
    model.value.emailPasswordConfirm = model.value.emailPassword;
  }
  title.value = record.id ? "编辑" : "新增";
  visible.value = true;
}

function close() {
  emit('close');
  model.value = {};
  selectedStationeryId.value = null;
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  try {
    await formRef.value.validate();
    if(model.value.emailType == 1){

      if (model.value.emailPassword !== model.value.emailPasswordConfirm) {
        createMessage.error('两次输入的密码不一致!');
        return;
      }
    }else{
      model.value.emailPassword = '';
    }
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (!model.value.id) {
      httpurl = url.add;
      method = 'post';
      delete model.value.emailPasswordConfirm;
    } else {
      httpurl = url.edit;
      method = 'put';
      delete model.value.emailPasswordConfirm;
   
    }
    model.value.type = 1
    if (selectedStationeryId.value && model.value.emailType == 1){
      model.value.stationeryId = selectedStationeryId.value;
      const matchedStationery = stationery.value.find(item => item.id === selectedStationeryId.value);
      model.value.stationeryImageUrl = matchedStationery.imageUrl
      model.value.stationeryImageBg =  matchedStationery.imageBg
      model.value.stationeryImageCode = matchedStationery.code
    }else if (model.value.emailType == 0){
      model.value.stationeryId = null;
      model.value.stationeryImageUrl = null;
      model.value.stationeryImageBg = null;
      model.value.stationeryImageCode = null;
    }else{
      model.value.stationeryId = null;
      model.value.stationeryImageUrl = null;
      model.value.stationeryImageBg = null;
      model.value.stationeryImageCode = null;
    }
    const res = await defHttp.request({ url: httpurl, method, params: model.value });
    emit('ok');
    close();
    
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    confirmLoading.value = false;
  }
}

function handleCancel() {
  close();
}

defineExpose({
  add,
  edit,
});
</script>

<style lang="less" scoped>
/deep/ .ant-radio-button-wrapper:not(:first-child)::before {
      width: 0.1px !important;
      background-color: transparent;
    }
    /deep/ .ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled)::before {
      background-color: #3155ed !important;
    }

.stationery-item {
  position: relative;
  margin-bottom: 12px;
  width: 100%;
  height: 100%;
  cursor: pointer;
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #d9d9d9;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.stationery-item-selected {
  border-color: #3155ed !important;
  box-shadow: 0 0 0 2px rgba(49, 85, 237, 0.2) !important;

  .stationery-check-icon {
    position: absolute;
    top: 4px;
    right: 4px;
    width: 20px;
    height: 20px;
    background-color: #3155ed;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 12px;
    z-index: 1;
  }
}
</style>
