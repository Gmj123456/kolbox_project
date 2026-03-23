<template>
  <a-modal
    :maskClosable="false"
    title="产品编辑"
    :width="400"
    :open="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef" class="clearfix" :label-col="labelColAll" :wrapper-col="wrapperColAll">
        <a-row>
          <a-col :xs="24" :sm="24" style="transform: translateX(10px)">
            <a-form-item label="产品名称" name="goodsTitle">
              <a-input style="width: 100%" v-model:value="model.goodsTitle" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="24" style="transform: translateX(10px)">
            <a-form-item label="产品链接" name="goodsUrl">
              <a-input style="width: 100%" v-model:value="model.goodsUrl" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="24" style="transform: translateX(10px)">
            <a-form-item label="产品图片" name="goodsPicUrl">
              <a-input
                style="width: 77%"
                :precision="0"
                :min="0"
                v-model:value="model.goodsPicUrl"
              />
              <a-avatar
                shape="square"
                :size="32"
                icon="picture"
                :src="model.goodsPicUrl"
                style="margin-left: 20px"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();

// 表单引用
const formRef = ref(null);

// 状态
const visible = ref(false);
const confirmLoading = ref(false);

// 布局配置
const labelColAll = {
  xs: { span: 24 },
  sm: { span: 5 },
};
const wrapperColAll = {
  xs: { span: 24 },
  sm: { span: 16 },
};

// 表单数据
const model = ref({
  id: undefined,
  promId: undefined,
  goodsTitle: undefined,
  goodsPicUrl: undefined,
  goodsRemark: undefined,
  goodsUrl: undefined,
});

// API 端点
const url = {
  add: '/storeSellerPromotion/add',
  edit: '/storepromotiongoods/storePromotionGoods/edit',
};

// 新增
function add() {
  edit({});
}

// 编辑
function edit(record) {
  Object.assign(model.value, record);
  visible.value = true;
  
}

// 关闭
function close() {
  emit('close');
  visible.value = false;
}

// 取消
function handleCancel() {
  close();
}

// 确认
async function handleOk() {
  try {
    await formRef.value.validate();
  } catch (error) {
    return;
  }

  try {
    confirmLoading.value = true;
    let httpurl = '';
    let method = '';
    if (!model.value.id) {
      httpurl = url.add;
      method = 'post';
    } else {
      httpurl = url.edit;
      method = 'put';
    }

    const formData = {
      id: model.value.id,
      promId: model.value.promId,
      goodsTitle: model.value.goodsTitle,
      goodsPicUrl: model.value.goodsPicUrl,
      goodsRemark: model.value.goodsRemark,
      goodsUrl: model.value.goodsUrl,
    };

    console.log(formData);

    let res;
    if (method === 'post') {
      res = await defHttp.post({ url: httpurl, data: formData }, { isTransformResponse: false });
    } else {
      res = await defHttp.put({ url: httpurl, data: formData }, { isTransformResponse: false });
    }

    if (res.success) {
      createMessage.success(res.message);
      emit('ok');
    } else {
      createMessage.warning(res.message);
    }
  } catch (error) {
    console.error('handleOk error:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
    close();
  }
}

// 定义事件
const emit = defineEmits(['ok', 'close']);

// 暴露方法给父组件
defineExpose({
  add,
  edit,
  close,
});
</script>

<style lang="less" scoped>
.clearfix:after {
  content: '.';
  display: block;
  height: 0;
  font-size: 0;
  clear: both;
  visibility: hidden;
}
</style>
