<template>
  <a-modal
    v-model:visible="visible"
    :maskClosable="false"
    :title="title"
    :width="500"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :model="model" ref="formRef" :rules="validatorRules">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item prop="sysUserId">
              <a-select
                show-search
                option-filter-prop="label"
                v-model:value="model.sysUserId"
                allowClear
                placeholder="用户名称(必选)"
              >
                <a-select-option
                  v-for="(user, key) in consultantList"
                  :key="key"
                  :value="user.id"
                  :label="user.username"
                >
                  {{ user.username }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item prop="spreadSheetUrl">
              <a-input
                @change="handleSheetIdChange"
                v-model:value="model.spreadSheetUrl"
                placeholder="飞书表格URL(必填)"
                @blur="model.spreadSheetUrl = model.spreadSheetUrl?.trim()"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item>
              <a-input
                disabled
                v-model:value="model.spreadSheetId"
                placeholder="飞书在线表格ID"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item>
              <a-input
                disabled
                v-model:value="model.sheetId"
                placeholder="飞书在线表格sheetID"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item prop="spreadSheetType">
              <a-select
                v-model:value="model.spreadSheetType"
                placeholder="飞书在线表格类型(必选)"
              >
                <a-select-option
                  v-for="(type, key) in feiSshuTypeList"
                  :key="key"
                  :value="type.value"
                >
                  {{ type.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-item>
              <a-input
                placeholder="开始列"
                v-model:value="model.startColumn"
                @blur="model.startColumn = model.startColumn?.trim()"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item>
              <a-input
                placeholder="结束列"
                v-model:value="model.endColumn"
                @blur="model.endColumn = model.endColumn?.trim()"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item>
              <a-input-number
                style="width: 100%"
                :min="1"
                :precision="0"
                :max="9999999"
                placeholder="标题行"
                v-model:value="model.headerRow"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item>
              <a-input
                placeholder="是否同步列"
                v-model:value="model.isSynchronizeColumn"
                @blur="model.isSynchronizeColumn = model.isSynchronizeColumn?.trim()"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item>
              <a-input
                placeholder="错误原因列"
                v-model:value="model.errorReasonColumn"
                @blur="model.errorReasonColumn = model.errorReasonColumn?.trim()"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { defHttp } from '/@/utils/http/axios';
import { getDictItems } from '@/api/common/api';

const emit = defineEmits(['ok', 'close']);
const title = ref('操作');
const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref(null);

const consultantList = ref([]);
const feiSshuTypeList = ref([]);

const model = reactive({
  id: undefined,
  sysUserId: undefined,
  spreadSheetUrl: '',
  spreadSheetId: '',
  sheetId: '',
  spreadSheetType: undefined,
  startColumn: '',
  endColumn: '',
  headerRow: 1,
  isSynchronizeColumn: '',
  errorReasonColumn: '',
});

const validatorRules = {
  sysUserId: [{ required: true, message: '请选择用户名称', trigger: 'change' }],
  spreadSheetUrl: [
    { required: true, message: '请输入飞书表格URL', trigger: 'change' },
    {
      pattern: /^https:\/\/[a-zA-Z0-9]+\.feishu\.cn\/sheets\/([a-zA-Z0-9]+)(\?sheet=([a-zA-Z0-9]+))?$|^https:\/\/[a-zA-Z0-9]+\.feishu\.cn\/sheets\/([a-zA-Z0-9]+)$/,
      message: '请输入正确的飞书表格URL格式，必须包含表格ID',
      trigger: 'change',
    },
  ],
  spreadSheetId: [
    { required: true, message: '请输入飞书在线表格ID', trigger: 'change' },
  ],
  sheetId: [
    { required: true, message: '请输入飞书在线表格sheetID', trigger: 'change' },
  ],
  spreadSheetType: [
    { required: true, message: '请选择飞书在线表格类型', trigger: 'change' },
  ],
  headerRow: [{ required: true, message: '请输入标题行', trigger: 'change' }],
};

const url = {
  add: '/kolsysuserfeishusheet/add',
  edit: '/kolsysuserfeishusheet/edit',
};

const { createMessage } = useMessage();

const handleSheetIdChange = (e) => {
  const value = e.target.value;
  const reg = /^https:\/\/[a-zA-Z0-9]+\.feishu\.cn\/sheets\/([a-zA-Z0-9]+)(\?sheet=([a-zA-Z0-9]+))?$|^https:\/\/[a-zA-Z0-9]+\.feishu\.cn\/sheets\/([a-zA-Z0-9]+)$/;

  if (reg.test(value)) {
    const sheetIdMatch = value.match(/sheets\/([a-zA-Z0-9]+)/);
    const sheetNameMatch = value.match(/sheet=([a-zA-Z0-9]+)/);
    if (sheetIdMatch) {
      model.spreadSheetId = sheetIdMatch[1];
    }
    if (sheetNameMatch) {
      model.sheetId = sheetNameMatch[1];
    }
  }
};


const initFeiSshuTypeList = async () => {
  const res = await getDictItems('feishu_type');
  if (res) {
    feiSshuTypeList.value = res || [];
  }
};

const initConsultantList = async () => {
  const res = await defHttp.get({ url: '/sys/user/queryAllCelebrityCounselor' });
  if (res) {
    consultantList.value = res || [];
  }
};

const resetModel = () => {
  model.id = undefined;
  model.sysUserId = undefined;
  model.spreadSheetUrl = '';
  model.spreadSheetId = '';
  model.sheetId = '';
  model.spreadSheetType = undefined;
  model.startColumn = '';
  model.endColumn = '';
  model.headerRow = 1;
  model.isSynchronizeColumn = '';
  model.errorReasonColumn = '';
};

const close = () => {
  resetModel();
  formRef.value?.resetFields();
  visible.value = false;
  emit('close');
};

const handleOk = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  if (
    model.isSynchronizeColumn &&
    model.errorReasonColumn &&
    model.isSynchronizeColumn.toUpperCase() == model.errorReasonColumn.toUpperCase()
  ) {
    createMessage.warning('是否同步列和错误原因列不能相同');
    return;
  }

  confirmLoading.value = true;
  try {
    const httpurl = model.id ? url.edit : url.add;
    const method = model.id ? 'put' : 'post';

    const selectedUser = consultantList.value.find((item) => item.id == model.sysUserId);
    const params = {
      ...model,
      username: selectedUser?.username || '',
    };

    const res = await defHttp.request({
      url: httpurl,
      method,
      data: params,
    });

    if (res) {
      
      close();
      emit('ok');
    } else {
    
    }
  } catch (error) {
    console.error('handleOk error:', error);
    createMessage.error('操作失败');
  } finally {
    confirmLoading.value = false;
  }
};

const handleCancel = () => {
  close();
};

const open = (record = {}) => {
  visible.value = true;
  Object.assign(model, {
    id: record.id,
    sysUserId: record.sysUserId,
    spreadSheetUrl: record.spreadSheetUrl || '',
    spreadSheetId: record.spreadSheetId || '',
    sheetId: record.sheetId || '',
    spreadSheetType: record.spreadSheetType,
    startColumn: record.startColumn || '',
    endColumn: record.endColumn || '',
    headerRow: record.headerRow || 1,
    isSynchronizeColumn: record.isSynchronizeColumn || '',
    errorReasonColumn: record.errorReasonColumn || '',
  });
  initConsultantList();
  initFeiSshuTypeList();
};

defineExpose({
  open,
});
</script>

<style lang="less" scoped></style>
