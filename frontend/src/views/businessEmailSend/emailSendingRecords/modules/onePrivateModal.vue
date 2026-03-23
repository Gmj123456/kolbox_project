<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="600"
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
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账号">
              <span style="margin-right: 10px">{{ model.username }}</span>
              <span class="platformType-img" v-if="model.platformType == 0">
                <img src="@/assets/images/ins.png" alt="" />
              </span>
              <span class="platformType-img" v-if="model.platformType == 1">
                <img src="@/assets/images/yt.png" alt="" />
              </span>
              <span class="platformType-img" v-if="model.platformType == 2">
                <img src="@/assets/images/tk.png" alt="" />
              </span>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="性别"
              name="gender"
            >
              <a-radio-group v-model:value="model.gender">
                <a-radio-button :value="0">
                  <div>
                    <img
                      style="width: 22px; height: 22px"
                      src="@/assets/images/man.png"
                      alt=""
                    />
                  </div>
                </a-radio-button>
                <a-radio-button :value="1">
                  <div>
                    <img
                      style="width: 22px; height: 22px"
                      src="@/assets/images/woman.png"
                      alt=""
                    />
                  </div>
                </a-radio-button>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="国家"
              name="country"
            >
              <a-select
                show-search
                placeholder="请选择国家"
                option-filter-prop="label"

                v-model:value="model.country"
                style="width: 100%"
                allowClear
              >
                <a-select-option
                  v-for="item in countryList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.country"
                >
                  {{ item.country }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="个性化标签"
            >
              <a-select
                :open="false"
                :showArrow="false"
                v-model:value="model.personalTags"
                style="width: 100%"
                show-search
                allowClear
                option-filter-prop="label"
                mode="multiple"
              >
                <a-select-option
                  v-for="item in personalTagsList"
                  :key="item.id"
                  :value="item.id"
                  :label="item.tagName"
                >
                  {{ item.tagName }}
                </a-select-option>
              </a-select>
              <div class="personalTagsBox" @click="personalTagsBoxClick">
                <a-tag
                  v-for="item in personalTagsList"
                   :key="item.id"
                 
                  :class="{
                    currentTag: model.personalTags?.includes(item.id),
                  }"
                  @click="clickPersonalTag(item)"
                >
                {{ item.tagName }}
                </a-tag>
              </div>
              <!-- <div class="personalTagsSelect">
                <div
                  v-for="item in personalTagsList"
                  :key="item.id"
                  @click="clickPersonalTag(item)"
                  :class="{
                    currentTag: model.personalTags?.includes(item.id),
                  }"
                >
                  {{ item.tagName }}
                </div>
              </div> -->
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="合作内容"
              class="PrivateFormcontractInfo"
            >
              <div
                v-for="(item, index) in model.contractInfo"
                :key="index"
                style="display: flex; align-items: center; gap: 8px"
              >
                <a-form-item :wrapperCol="{ span: 24 }">
                  <a-select
                    show-search
                    placeholder="请选择内容"
                    option-filter-prop="label"
                    v-model:value="model.contractInfo[index].videoTag"
                    style="width: 250px"
                  >
                    <a-select-option
                      v-for="(tag, tagIndex) in videoTagsList"
                      :key="tagIndex"
                      :value="tag"
                      :label="tag"
                    >
                      {{ tag }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item :wrapperCol="{ span: 24 }">
                  <a-input-number
                    :min="0"
                    :max="9999999"
                    :precision="0"
                    @blur="checkNum(index)"
                    v-model:value="model.contractInfo[index].contractAmount"
                    placeholder="签约费用"
                    style="width: 250px"
                  >
                    <template #addonAfter>
                      <span>$</span>
                    </template>
                  </a-input-number>
                </a-form-item>
              </div>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="建联邮箱"
              name="celebrityContactEmail"
            >
              <a-select
                disabled
                show-search
                placeholder="请选择建联邮箱"
                option-filter-prop="label"
                v-model:value="model.celebrityContactEmail"
                style="width: 100%"
                allowClear
              >
                <a-select-option
                  v-for="(item, index) in contactEmailList"
                  :key="index"
                  :value="item"
                  :label="item"
                >
                  {{ item }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
              <a-textarea
                v-model:value="model.remark"
                placeholder="请输入备注"
                :auto-size="{ minRows: 3, maxRows: 5 }"
              />
            </a-form-item>
          </a-col>
       
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { getDictItems } from '/@/api/common/api';

const { createMessage } = useMessage();
const emit = defineEmits(['ok', 'close']);

const formRef = ref(null);
const title = ref("一键私有");
const visible = ref(false);
const confirmLoading = ref(false);

const model = ref({
  username: undefined,
  email: undefined,
  gender: undefined,
  platformType: undefined,
  country: undefined,
  personalTags: [],
  celebrityContactEmail: undefined,
  remark: undefined,
  contractInfo: [],
});

const labelCol = {
  xs: { span: 24 },
  sm: { span: 3 },
};

const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 21 },
};

const validatorRules = {
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  country: [{ required: true, message: "请选择国家", trigger: "change" }],
  celebrityContactEmail: [
    { required: true, message: "请选择建联邮箱", trigger: "change" },
  ],
};

const personalTagsList = ref([]);
const personalTags = ref([]);
const contactEmailList = ref([]);
const celebrityTags = ref([]);
const videoTagsList = ref([]);
const countryList = ref([]);

function personalTagsBoxClick() {
  // Handle click on personal tags box
}

function removePersonalTag(tagId) {
  const index = model.value.personalTags.indexOf(tagId);
  if (index > -1) {
    model.value.personalTags.splice(index, 1);
  }
}

function clickPersonalTag(item) {
  if (model.value.personalTags.includes(item.id)) {
    removePersonalTag(item.id);
  } else {
    model.value.personalTags.push(item.id);
  }
}


function checkContentItemFilled() {
  for (let i = 0; i < model.value.contractInfo.length; i++) {
    const item = model.value.contractInfo[i];
    if (
      (item.videoTag && !item.contractAmount) ||
      (!item.videoTag && item.contractAmount)
    ) {
      createMessage.warning("请完整填写内容和金额");
      return false;
    }
  }
  return true;
}

function checkVideoTagsDuplicate() {
  const videoTags = model.value.contractInfo
    .filter((item) => item.videoTag)
    .map((item) => item.videoTag);
  const uniqueVideoTags = [...new Set(videoTags)];
  return videoTags.length === uniqueVideoTags.length;
}

async function initCountry() {
  const res = await defHttp.get({ url: "/tiktokcountry/getCountryList" });
  if (res) {
    countryList.value = res || [];
  }
}

function checkNum(index) {
  let num = model.value.contractInfo[index].contractAmount;
  if (num !== undefined && num !== null) {
    if (num < 0) {
      num = 0;
    }
    if (num > 9999999) {
      num = 9999999;
    }
    model.value.contractInfo[index].contractAmount = num;
  }
}

async function getPersonalTags() {
  const res = await defHttp.get({ url: "/personalTags/listAll" });
  if (res) {
    personalTagsList.value = (res || []).filter((item) => item.tagName != "无");
  }
}

async function initContactEmail() {
  const res = await defHttp.get({ url: "/storeUserContactEmail/queryListByCounselor" });
  if (res) {
    contactEmailList.value = (res || []).map((item) => item.contactEmail);
  }
}

async function initCelebrityTag() {
  const res = await defHttp.get({ 
    url: "/storetags/storeTags/list", 
    params: {
      pageSize: 10000,
    }
  });
  if (res) {
    celebrityTags.value = (res.records || []).map((item) => item.likeTagName);
  }
}

async function getVideoTagsList() {
  const res = await getDictItems("celebrity_content");
  if (res) {
    videoTagsList.value = res
      .filter((item) => item.title == model.value.platformType)[0]
      ?.value.split(",") || [];
    
    const contractInfo = [];
    for (let i = 0; i < videoTagsList.value.length; i++) {
      contractInfo.push({
        videoTag: undefined,
        contractAmount: undefined,
      });
    }
    model.value.contractInfo = contractInfo;
  }
}

function add() {
  edit({});
}

async function edit(record) {
  Object.assign(model.value, record);
  visible.value = true;
  model.value.personalTags = [];
  model.value.celebrityContactEmail = record.sendEmail;
  model.value.remark = undefined;
  
  await getPersonalTags();
  await initContactEmail();
  await initCelebrityTag();
  await getVideoTagsList();
  await initCountry();
}

function close() {
  emit("close");
  Object.keys(model.value).forEach(key => {
    if (key === 'personalTags' || key === 'contractInfo') {
      model.value[key] = [];
    } else {
      model.value[key] = undefined;
    }
  });
  personalTagsList.value = [];
  personalTags.value = [];
  formRef.value?.resetFields();
  visible.value = false;
}

async function handleOk() {
  const personalTagIdName = personalTagsList.value
    .filter((item) => model.value.personalTags.includes(item.id))
    .map((item) => item.tagName)
    .join(",");

  if (personalTagIdName.length > 1 && personalTagIdName.includes("无")) {
    createMessage.warning("请检查选择的个性化标签，\"无\"不可以与其他标签同时选择");
    return;
  }
  
  if (!checkContentItemFilled()) {
    return;
  }
  
  if (!checkVideoTagsDuplicate()) {
    createMessage.warning("请勿重复选择内容");
    return;
  }

  try {
    await formRef.value.validate();
    
    const personalTags =
      personalTagsList.value && personalTagsList.value.length > 0
        ? personalTagsList.value
            .filter((item) => model.value.personalTags.includes(item.id))
            .map((item) => item.tagName)
            .join(",")
        : "";
    
    confirmLoading.value = true;
    
    const obj = {
      kolId: model.value.celebrityId,
      account: model.value.username,
      email: model.value.email,
      gender: model.value.gender,
      platformType: model.value.platformType,
      countryName: countryList.value.find((item) => item.id == model.value.country)
        ?.country,
      celebrityContactEmail: model.value.celebrityContactEmail,
      privateContractInfoList: model.value.contractInfo.filter(
        (item) => item.videoTag && item.contractAmount
      ),
      personalTags,
      remark: model.value.remark,
    };

    const apiArr = [obj];
    const res = await defHttp.post({
      url: "/storeCelebrityPrivate/storeCelebrityPrivate/push/celebrityAdd",
      params: apiArr,
    },{isTransformResponse: false});
    
    if (res.success) {
      if (res.result.errorList == 0) {
        createMessage.success(res.message);
        emit("ok");
        close();
      } else {
        createMessage.warning(res.result.errorList[0].failReason);
      }
    } else {
      createMessage.warning(res.message);
    }
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
  show: edit,
});
</script>
<style>
.PrivateFormcontractInfo .ant-input-suffix {
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB,
    Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji,
    Segoe UI Emoji, Segoe UI Symbol !important;
}
</style>
<style lang="less" scoped>
.platformType-img {
  display: inline-block;
  width: 20px;
  height: 20px;

  img {
    width: 100%;
    height: 100%;
  }
}
.personalTagsBox {
  margin-top: 8px;
  border: 1px solid #d9d9d9;
    width: 100%;
    min-height: 34px;
    padding: 2px 10px;
    border-radius: 3px;
    box-sizing: border-box;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    position: relative;
    max-height: 266px;
    overflow-y: auto;
    row-gap: 4px;
}
.currentTag {
    background: #fff;
    border: 1px solid #3155ed;
    color: #3155ed;
  }
.personalTagsSelect {
  min-height: 33px;
  margin-top: 10px;
  width: 100%;
  background: #fff;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  height: 200px;
  overflow-y: auto;
  overflow-x: hidden;

  div {
    height: 23px;
    line-height: 23px;
    border-radius: 8px;
    font-size: 12px;
    padding: 0 10px;
    margin: 5px;
    cursor: pointer;
    background: #e7e7f0;
    border: none;
    user-select: none;
  }

  
}
</style>
