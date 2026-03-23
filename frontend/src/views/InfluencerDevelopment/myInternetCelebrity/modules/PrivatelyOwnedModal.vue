<template>
  <a-modal
    :maskClosable="false"
    :title="title"
    :width="1300"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <vxe-table size="mini" ref="xTable" height="500px" keep-source id="id" :data="dataSource">
        <vxe-table-column type="seq" align="center" width="50" title="#"></vxe-table-column>
        <vxe-table-column type="uniqueId" title="账号" width="150px">
          <template #default="{ row }">
            <div style="display: flex; align-items: center">
              <div style="width: 40px; height: 40px">
                <a-tooltip placement="top">
                  <template #title>
                    <span>
                      <img
                        v-if="row.authorAvatarUrl"
                        :src="authorUrl(row.authorAvatarUrl)"
                        style="width: 100%"
                        :preview="row.id"
                      />
                      <img
                        v-else
                        src="@/assets/images/avatar.png"
                        style="max-width: 40px; max-height: 40px; margin: 0 auto"
                      />
                    </span>
                  </template>
                  <span>
                    <img
                      v-if="row.authorAvatarUrl"
                      :src="authorUrl(row.authorAvatarUrl)"
                      style="max-height: 40px; max-width: 40px; cursor: pointer"
                      :preview="row.id"
                    />
                    <img
                      v-else
                      src="@/assets/images/avatar.png"
                      style="max-width: 40px; max-height: 40px; margin: 0 auto"
                    />
                  </span>
                </a-tooltip>
              </div>
              <div style="margin-left: 10px">
                <!-- <j-ellipsis :value="row.uniqueId" :length="10" /> -->
                <a><j-ellipsis :value="row.uniqueId" :length="9" /></a>
                <div style="display: flex; align-items: center">
                  <span v-if="row.country" style="margin-right: 10px">{{
                    row.country ? row.country : "-"
                  }}</span>
                  <!-- <span v-else="row.country">-</span> -->
                  <span class="platformType-img" v-if="row.platformType == 0">
                    <img
                      src="@/assets/images/ins.png"
                      alt=""
                    />
                  </span>
                  <span class="platformType-img" v-if="row.platformType == 1">
                    <img
                      src="@/assets/images/yt.png"
                      alt=""
                    />
                  </span>
                  <span class="platformType-img" v-if="row.platformType == 2">
                    <img
                      src="@/assets/images/tk.png"
                      alt=""
                    />
                  </span>
                  <!-- <span style="margin-left:28px">{{row.videoStandardCount}}/{{row.videoSampleCount}}</span> -->
                </div>
              </div>
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column field="gender" title="性别" width="100px">
          <template #default="{ row }">
            <a-radio-group v-model:value="row.gender" style="display: flex" name="radioGroup">
              <a-radio-button value="0"> 男 </a-radio-button>
              <a-radio-button value="1"> 女 </a-radio-button>
            </a-radio-group>
          </template>
        </vxe-table-column>
        <vxe-table-column field="personalTags" title="个性化标签" min-width="200px">
          <template #default="{ row }">
            <a-select
              mode="multiple"
              :maxTagCount="2"
           
              v-model:value="row.personalTags"
              style="width: 100%"
              showArrow
              allowClear
              placeholder="个性化标签"
            >
              <a-select-option
                :value="item.id"
                v-for="item in personalTagsList"
                :key="item.id"
                >{{ item.tagName }}</a-select-option
              >
            </a-select>
          </template>
        </vxe-table-column>
      
        <vxe-table-column
          v-for="(item, index) in dataSource.length > 0
            ? dataSource[0].contentQuotation
            : []"
          :key="index"
          :field="`contentQuotation[${index}].contractAmount`"
          :title="item.videoTag"
          width="130px"
        >
          <template #default="{ row }">
            <a-input
              @blur="roundValue(row, index)"
              v-model:value="row.contentQuotation[index].contractAmount"
              placeholder="签约费用"
              suffix="$"
            />
          </template>
        </vxe-table-column>
      

        <vxe-table-column
          field="celebrityContactEmail"
          min-width="200px"
          title="建联邮箱"
        >
          <template #default="{ row }">
            <a-select
              v-model:value="row.celebrityContactEmail"
              style="width: 100%"
              allowClear
              :getPopupContainer="getPopupContainer"
              placeholder="建联邮箱"
            >
              <a-select-option
                v-for="(item, key) in contactEmailList"
                :key="key"
                :value="item"
              >
                {{ item }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="email"
          min-width="200px"
          title="网红邮箱"
        >
          <template #default="{ row }">
            <a-input v-model:value="row.email" placeholder="网红邮箱"></a-input>
          </template>
        </vxe-table-column>
      </vxe-table>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { getDictItems } from '/@/api/common/api';
import { JEllipsis } from '/@/components/Form';

const { createMessage: $message } = useMessage();

// 状态
const title = ref('一键私有');
const visible = ref(false);
const confirmLoading = ref(false);
const dataSource = ref<any[]>([]);
const videoTagsList = ref<string[]>([]);
const contactEmailList = ref<string[]>([]);
const personalTagsList = ref<any[]>([]);
const tableRef = ref();

// 计算列
const columns = computed(() => {
  const cols: any[] = [
    {
      title: '#',
      key: 'rowIndex',
      width: 50,
      align: 'center',
    },
    {
      title: '账号',
      key: 'uniqueId',
      width: 150,
    },
    {
      title: '性别',
      key: 'gender',
      width: 120,
    },
    {
      title: '个性化标签',
      key: 'personalTags',
      minWidth: 200,
    },
  ];

  // 动态添加内容报价列
  if (dataSource.value.length > 0 && dataSource.value[0].contentQuotation) {
    dataSource.value[0].contentQuotation.forEach((item: any, index: number) => {
      cols.push({
        title: item.videoTag,
        key: `contentQuotation_${index}`,
        width: 130,
      });
    });
  }

  cols.push({
    title: '建联邮箱',
    key: 'celebrityContactEmail',
    minWidth: 200,
  });

  return cols;
});

// 获取内容索引
function getContentIndex(key: string) {
  return parseInt(key.split('_')[1]);
}

// 获取弹窗容器
function getPopupContainer(triggerNode: HTMLElement) {
  return triggerNode.parentNode as HTMLElement;
}

// 四舍五入值
function roundValue(row: any, index: number) {
  if (row.contentQuotation[index].contractAmount) {
    let num = Number(row.contentQuotation[index].contractAmount);
    if (isNaN(num)) {
      row.contentQuotation[index].contractAmount = undefined;
      return;
    }
    if (num > 9999999) {
      num = 9999999;
    }
    row.contentQuotation[index].contractAmount = num;
  }
}

// 获取头像URL
function authorUrl(url: any) {
  return url.includes("https") || url.includes("http")
      ? url
      : 'https://gemstone-image.kolbox.com/avatar/' + url;
}

// 获取个性化标签
async function getPersonalTags() {
  try {
    const res = await defHttp.get({ url: '/personalTags/listAll' });
    if (res) {
      personalTagsList.value = res.filter((item: any) => item.tagName != '无');
    }
  } catch (error) {
    console.error(error);
  }
}

// 初始化联系邮箱
async function initContactEmail() {
  try {
    const res = await defHttp.get({
      url: '/storeUserContactEmail/queryListByCounselor',
    });
    if (res) {
      contactEmailList.value = res.map((item: any) => item.contactEmail);
      if (contactEmailList.value.length == 1) {
        dataSource.value.forEach((item) => {
          item.celebrityContactEmail = contactEmailList.value[0];
        });
      }
    }
  } catch (error) {
    console.error(error);
  }
}

// 获取视频标签列表
async function getVideoTagsList() {
  try {
    const res = await getDictItems('celebrity_content');
    if (res) {
      const filtered = res.filter(
        (item: any) => item.title == dataSource.value[0].platformType
      );
      if (filtered.length > 0) {
        videoTagsList.value = filtered[0].value.split(',');
        dataSource.value.forEach((item) => {
          item.videoTags = [videoTagsList.value[0]];
        });
      }
    }
  } catch (error) {
    console.error(error);
  }
}



// 添加
function add(arr: any[]) {
  visible.value = true;
  dataSource.value = JSON.parse(JSON.stringify(arr.map(item => {
    // 原写法有问题：如果item已有这些字段会被覆盖，可能不符合预期，容易丢失原有选择。此处应当尽量保留原有数据，必要时只初始化未定义的字段。
    return {
      ...item,
      likeTags: [],
      gender: '1',
      contractAmount: undefined,
      videoTags: [],
      personalTags: [],
      inputValue:  undefined,
      celebrityContactEmail: undefined,
    }
  })));

  getVideoTagsList();
  initContactEmail();
  getPersonalTags();
}

// 关闭
function close() {
  emit('close');
  visible.value = false;
  dataSource.value = [];
}

// 提交
async function handleOk() {
  const apiArr: any[] = [];
  let hasError = false;

  for (const item of dataSource.value) {
    const hasValidQuotation = item.contentQuotation.some(
      (quotation: any) => quotation.contractAmount
    );

    if (!hasValidQuotation) {
      $message.warning(`${item.uniqueId} 的内容报价中至少需要填写一个签约费用`);
      hasError = true;
      return;
    }

    const personalTagIdName = personalTagsList.value
      .filter((d) => item.personalTags.includes(d.id))
      .map((p) => p.tagName)
      .join(',');

    if (personalTagIdName.length > 1 && personalTagIdName.includes('无')) {
      $message.warning('请检查选择的个性化标签，"无"不可以与其他标签同时选择');
      hasError = true;
      return;
    }

    if (!item.celebrityContactEmail || !item.email) {
      $message.warning(`有未填数据，无法一键私有`);
      hasError = true;
      return;
    }

    const requestData = {
      kolId:item.id,
      account: item.uniqueId,
      nickname: item.nickname,
      email: item.email,
      gender: item.gender,
      celebrityId: item.account,
      platformType: item.platformType,
      followersNum: item.authorFollowerCount,
      avatarUrl: item.authorAvatarUrl,
      countryName: item.country,
      personalTagIds: item.personalTags.length > 0 ? item.personalTags.join(',') : '',
      celebrityContactEmail: item.celebrityContactEmail,
      privateContractInfoList: item.contentQuotation
        .filter((quotation: any) => Number(quotation.contractAmount) > 0)
        .map((quotation: any) => ({
          ...quotation,
          contractAmount: Number(quotation.contractAmount),
        })),
    };

    apiArr.push(requestData);
  }

  if (hasError) {
    confirmLoading.value = false;
    return;
  }

  try {
    confirmLoading.value = true;
    const res = await defHttp.post({
      url: '/storeCelebrityPrivate/storeCelebrityPrivate/celebrityPrivateAdd',
      data: {
        celebrityPrivates: apiArr,
      },
    },{isTransformResponse: false});

    if (res.success) {
      $message.success(res.message);
      emit('ok');
      close();
    } else {
      $message.error(res.message);
    }
  } catch (error) {
    console.error(error);
  } finally {
    confirmLoading.value = false;
  }
}

// 取消
function handleCancel() {
  close();
}

// 暴露方法
defineExpose({
  add,
  close,
});

// 定义事件
const emit = defineEmits<{
  (e: 'ok'): void;
  (e: 'close'): void;
}>();
</script>

<style lang="less" scoped>

.personalTagsBox {
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
}
.platformType-img {
  display: flex;
  align-items: center;
  height: auto;

  img {
    width: 16px;
    height: 16px;
  }
}
</style>
