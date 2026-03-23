<template>
  <div class="forgot-password-panel">
    <div class="text-group">
      <h2>忘记密码</h2>
      <p>请按步骤完成密码重置</p>
    </div>

    <!-- 步骤1: 验证账户 -->
    <div v-if="activeKey === 1" class="forgot-step active">
      <a-form ref="formRef" :model="formData" class="glass-form">
        <div class="input-wrapper">
          <UserOutlined class="input-icon" />
          <a-input
            v-model:value="formData.username"
            class="fix-auto-fill"
            placeholder="用户名"
            size="large"
          />
          <div class="input-glow"></div>
        </div>

        <div class="captcha-row">
          <div class="input-wrapper captcha-input-wrapper">
            <SafetyOutlined class="input-icon" />
            <a-input
              v-model:value="formData.inputCode"
              class="fix-auto-fill"
              type="text"
              :placeholder="t('sys.login.inputCode')"
              size="large"
            />
            <div class="input-glow"></div>
          </div>
          <div class="captcha-display" @click="handleChangeCheckCode">
            <img
              v-if="randCodeData.requestCodeSuccess"
              :src="randCodeData.randCodeImage"
              alt="验证码"
              style="width: 100%; height: 100%; object-fit: contain; cursor: pointer"
            />
            <img
              v-else
              :src="codeImg"
              alt="验证码"
              style="width: 100%; height: 100%; object-fit: contain; cursor: pointer"
            />
          </div>
        </div>

        <a-button
          :loading="loading"
          class="prime-btn"
          type="primary"
          size="large"
          @click="handleStep1Next"
        >
          <span>下一步</span>
        </a-button>
      </a-form>
    </div>

    <!-- 步骤2: 邮箱验证 -->
    <div v-else-if="activeKey === 2" class="forgot-step active">
      <a-form ref="emailFormRef" :model="emailFormData" class="glass-form">
        <div class="input-wrapper">
          <MailOutlined class="input-icon" />
          <a-input
            v-model:value="emailFormData.email"
            class="fix-auto-fill"
            type="email"
            placeholder="邮箱"
            size="large"
          />
          <div class="input-glow"></div>
        </div>

        <div class="input-wrapper captcha-wrapper">
          <SafetyOutlined class="input-icon" />
          <a-input
            v-model:value="emailFormData.emailCode"
            class="fix-auto-fill"
            type="text"
            placeholder="邮箱验证码"
            size="large"
          />
          <div class="input-glow"></div>
          <button
            type="button"
            class="captcha-btn"
            :disabled="!showInterval"
            @click="getEmailCode"
          >
            <span v-if="showInterval">获取验证码</span>
            <span v-else>{{ timeRuning }} s</span>
          </button>
        </div>

        <div class="form-actions">
          <a-button
            class="prime-btn secondary-btn"
            type="default"
            size="large"
            @click="prevStep"
          >
            <span>上一步</span>
          </a-button>
          <a-button
            :loading="loading"
            class="prime-btn"
            type="primary"
            size="large"
            @click="handleStep2Next"
          >
            <span>下一步</span>
          </a-button>
        </div>
      </a-form>
    </div>

    <!-- 步骤3: 密码修改 -->
    <div v-else-if="activeKey === 3" class="forgot-step active">
      <a-form ref="pwdFormRef" :model="pwdFormData" class="glass-form">
        <div class="input-wrapper">
          <LockOutlined class="input-icon" />
          <a-input-password
            v-model:value="pwdFormData.password"
            class="fix-auto-fill"
            placeholder="密码"
            size="large"
          />
          <div class="input-glow"></div>
        </div>

        <div class="input-wrapper">
          <LockOutlined class="input-icon" />
          <a-input-password
            v-model:value="pwdFormData.confirmPassword"
            class="fix-auto-fill"
            placeholder="确认密码"
            size="large"
          />
          <div class="input-glow"></div>
        </div>

        <div class="form-actions">
          <a-button
            class="prime-btn secondary-btn"
            type="default"
            size="large"
            @click="prevStep"
          >
            <span>上一步</span>
          </a-button>
          <a-button
            :loading="loading"
            class="prime-btn"
            type="primary"
            size="large"
            @click="finishedPwd"
          >
            <span>确认修改</span>
          </a-button>
        </div>
      </a-form>
    </div>

    <!-- 步骤4: 完成 -->
    <div v-else class="forgot-step active">
      <div class="completion-message">
        <div class="checkmark-circle">
          <CheckCircleOutlined />
        </div>
        <h3>密码重置成功</h3>
        <p>您的密码已成功重置，请使用新密码登录</p>
        <a-button
          class="prime-btn"
          type="primary"
          size="large"
          @click="toLogin"
        >
          <span>返回登录</span>
        </a-button>
      </div>
    </div>

    <!-- 步骤指示器 -->
    <div class="forgot-password-steps" v-if="activeKey !== 4">
      <div
        class="forgot-password-step"
        :class="{ active: activeKey === 1 }"
      ></div>
      <div
        class="forgot-password-step"
        :class="{ active: activeKey === 2 }"
      ></div>
      <div
        class="forgot-password-step"
        :class="{ active: activeKey === 3 }"
      ></div>
      <div
        class="forgot-password-step"
        :class="{ active: activeKey === 4 }"
      ></div>
    </div>

    <p class="switch-text">
     <a href="javascript:void(0)" @click="goBack">返回登录</a>
    </p>

    <!-- 图片验证码弹窗 -->
    <CaptchaModal @register="captchaRegisterModal" @ok="handleChangeCheckCode" />
  </div>
</template>

<script lang="ts" name="mini-forgotpad" setup>
import { reactive, ref, toRaw, unref } from 'vue';
import { UserOutlined, LockOutlined, SafetyOutlined, CheckCircleOutlined, MailOutlined } from '@ant-design/icons-vue';
import { useI18n } from '/@/hooks/web/useI18n';
import { useMessage } from '/@/hooks/web/useMessage';
import { getCodeInfo, passwordChange } from '/@/api/sys/user';
import { defHttp } from '/@/utils/http/axios';
import codeImg from '/@/assets/images/checkcode.png';
import CaptchaModal from '@/components/jeecg/captcha/CaptchaModal.vue';
import { useModal } from '@/components/Modal';
import { ExceptionEnum } from '@/enums/exceptionEnum';

const [captchaRegisterModal, { openModal: openCaptchaModal }] = useModal();

// 步骤控制
const activeKey = ref<number>(1);
const loading = ref<boolean>(false);
const { t } = useI18n();
const { notification, createMessage, createErrorModal } = useMessage();

// 是否显示获取验证码
const showInterval = ref<boolean>(true);
// 60s
const timeRuning = ref<number>(60);
// 定时器
const timer = ref<any>(null);
const formRef = ref();
const emailFormRef = ref();
const pwdFormRef = ref();

// 验证码数据
const randCodeData = reactive<any>({
  randCodeImage: '',
  requestCodeSuccess: false,
  checkKey: null,
});

// 账号数据
const accountInfo = reactive<any>({});

// 步骤1表单：用户名 + 验证码
const formData = reactive({
  username: '',
  inputCode: '',
});

// 步骤2表单：邮箱 + 邮箱验证码
const emailFormData = reactive({
  email: '',
  emailCode: '',
});

// 步骤3表单：密码
const pwdFormData = reactive<any>({
  password: '',
  confirmPassword: '',
});

const emit = defineEmits(['go-back', 'success', 'register']);

/**
 * 获取验证码
 */
function handleChangeCheckCode() {
  formData.inputCode = '';
  randCodeData.checkKey = new Date().getTime() + Math.random().toString(36).slice(-4);
  getCodeInfo(randCodeData.checkKey).then((res) => {
    randCodeData.randCodeImage = res;
    randCodeData.requestCodeSuccess = true;
  });
}
async function checkUsername(){
  const res = await defHttp.get({ url: '/sys/user/checkOnlyUser', params: { username: formData.username } },{isTransformResponse:false})
  return res.success
}
/**
 * 步骤1下一步 - 验证用户名和验证码
 */
async function handleStep1Next() {
  if (!formData.username) {
    createMessage.warn('请输入用户名');
    return;
  }
  if (!formData.inputCode) {
    createMessage.warn('请输入验证码');
    return;
  }
  const res = await checkUsername()
  if (res) {
    createMessage.warn('用户不存在');
    return;
  }
  try {
    loading.value = true;
    // 这里需要调用验证用户名的接口
    // 暂时先保存用户名，进入下一步
    accountInfo.username = formData.username;
    accountInfo.checkKey = randCodeData.checkKey;
    accountInfo.inputCode = formData.inputCode;
    activeKey.value = 2;
    setTimeout(() => {
      emailFormRef.value?.resetFields();
    }, 300);
  } catch (error) {
    console.error('handleStep1Next error:', error);
    createMessage.error('验证失败，请重试');
  } finally {
    loading.value = false;
  }
}

/**
 * 步骤2下一步 - 验证邮箱和邮箱验证码
 */
async function handleStep2Next() {
  if (!emailFormData.email) {
    createMessage.warn('请输入邮箱地址');
    return;
  }
  if (!emailFormData.emailCode) {
    createMessage.warn('请输入邮箱验证码');
    return;
  }
  try {
    loading.value = true;
    // 这里需要调用验证邮箱验证码的接口
    // 暂时先保存邮箱信息，进入下一步
    accountInfo.email = emailFormData.email;
    accountInfo.emailCode = emailFormData.emailCode;
    activeKey.value = 3;
    setTimeout(() => {
      pwdFormRef.value?.resetFields();
    }, 300);
  } catch (error) {
    console.error('handleStep2Next error:', error);
    createMessage.error('验证失败，请重试');
  } finally {
    loading.value = false;
  }
}

/**
 * 获取邮箱验证码
 */
async function getEmailCode() {
  if (!emailFormData.email) {
    createMessage.warn('请先输入邮箱地址');
    return;
  }
  if (!accountInfo.username) {
    createMessage.warn('请先完成第一步验证');
    return;
  }
  try {
    const params = {
      username: accountInfo.username,
      email: emailFormData.email,
      smsmode: '2',
    };
    const result = await defHttp.post({ url: '/sys/getEmailCode', params }, { isTransformResponse: false }).catch((res) => {
      if (res.code === ExceptionEnum.PHONE_SMS_FAIL_CODE) {
        openCaptchaModal(true, {});
      }
    });
    if (result && result.success) {
      const TIME_COUNT = 60;
      if (!unref(timer)) {
        timeRuning.value = TIME_COUNT;
        showInterval.value = false;
        timer.value = setInterval(() => {
          if (unref(timeRuning) > 0 && unref(timeRuning) <= TIME_COUNT) {
            timeRuning.value = timeRuning.value - 1;
          } else {
            showInterval.value = true;
            clearInterval(unref(timer));
            timer.value = null;
          }
        }, 1000);
      }
    } else if (result && !result.success) {
      createMessage.error(result.message || '获取验证码失败');
    }
  } catch (error) {
    console.error('getEmailCode error:', error);
    createMessage.error('获取验证码失败');
  }
}

/**
 * 上一步
 */
function prevStep() {
  if (activeKey.value > 1) {
    activeKey.value = activeKey.value - 1;
  }
}

/**
 * 完成修改密码
 */
async function finishedPwd() {
  if (!pwdFormData.password) {
    createMessage.warn(t('sys.login.passwordPlaceholder'));
    return;
  }
  if (!pwdFormData.confirmPassword) {
    createMessage.warn(t('sys.login.confirmPassword'));
    return;
  }
  if (pwdFormData.password !== pwdFormData.confirmPassword) {
    createMessage.warn(t('sys.login.diffPwd'));
    return;
  }
  try {
    loading.value = true;
    // 使用邮箱验证码来修改密码
    const resultInfo = await passwordChange(
      toRaw({
        username: accountInfo.username,
        password: pwdFormData.password,
        smsCode: accountInfo.emailCode,
        email: accountInfo.email,
      })
    );
    if (resultInfo.success) {
      accountInfo.password = pwdFormData.password;
      activeKey.value = 4;
    } else {
      createErrorModal({
        title: t('sys.api.errorTip'),
        content: resultInfo.message || t('sys.api.networkExceptionMsg'),
      });
    }
  } catch (error) {
    console.error('finishedPwd error:', error);
    createMessage.error('修改密码失败');
  } finally {
    loading.value = false;
  }
}

/**
 * 去登录
 */
function toLogin() {
  emit('success', { username: accountInfo.username, password: accountInfo.password });
  initForm();
}

/**
 * 返回
 */
function goBack() {
  emit('go-back');
  initForm();
}

/**
 * 初始化表单
 */
function initForm() {
  activeKey.value = 1;
  Object.assign(formData, { username: '', inputCode: '' });
  Object.assign(emailFormData, { email: '', emailCode: '' });
  Object.assign(pwdFormData, { password: '', confirmPassword: '' });
  Object.assign(accountInfo, {});
  Object.assign(randCodeData, {
    randCodeImage: '',
    requestCodeSuccess: false,
    checkKey: null,
  });
  if (unref(timer)) {
    clearInterval(unref(timer));
    timer.value = null;
    showInterval.value = true;
  }
  setTimeout(() => {
    formRef.value?.resetFields();
    emailFormRef.value?.resetFields();
    pwdFormRef.value?.resetFields();
    handleChangeCheckCode();
  }, 300);
}

// 初始化时获取验证码
handleChangeCheckCode();

defineExpose({
  initForm,
});
</script>

<style lang="less" scoped>
.forgot-password-panel {
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: column;
  min-height: auto;
  height: auto;
}

/* 文本组 */
.text-group {
  text-align: left;
  margin-bottom: 20px;
}

.text-group h2 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-h);
}

.text-group p {
  color: var(--text-light);
  font-size: 14px;
  margin-bottom: 0;
  line-height: 1.5;
}

/* 步骤指示器 */
.forgot-password-steps {
  display: flex;
  gap: 8px;
  margin: 20px auto 15px;
  justify-content: center;
  align-items: center;
  z-index: 20;
}

.forgot-password-step {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--text-p);
  transition: var(--transition);
}

.forgot-password-step.active {
  background: var(--primary);
  transform: scale(1.2);
}

/* 步骤容器 */
.forgot-step {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-top: -10px;
  opacity: 1;
  transform: translateX(0);
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.forgot-step.active {
  display: flex !important;
  opacity: 1;
  transform: translateX(0);
}

/* 输入框样式 */
.input-wrapper {
  position: relative;
  // margin-bottom: 16px;
}

.input-wrapper:focus-within {
  z-index: 1;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--text-p);
  transition: var(--transition);
  pointer-events: none;
  z-index: 2;
}

:deep(.ant-input),
:deep(.ant-input-password) {
  width: 100%;
  height: 48px;
  padding: 0 16px 0 48px;
  background: var(--input-bg);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  color: white;
  font-size: 15px;
  outline: none;
  transition: var(--transition);
}

:deep(.ant-input::placeholder),
:deep(.ant-input-password input::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.ant-input:focus),
:deep(.ant-input-password:focus),
:deep(.ant-input-password .ant-input:focus) {
  background: rgba(255, 255, 255, 0.07);
  border-color: var(--primary);
  box-shadow: 0 0 0 1px var(--primary);
  outline: none;
}

.input-wrapper:focus-within .input-icon {
  color: var(--primary);
}

/* 输入框发光效果 */
.input-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 14px;
  opacity: 0;
  transition: var(--transition);
  pointer-events: none;
}

.input-wrapper:focus-within .input-glow {
  opacity: 1;
  box-shadow: 0 0 0 1px var(--primary), 0 0 20px rgba(37, 99, 235, 0.3);
}

/* 验证码行容器 */
.captcha-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 0;
  height: 48px;
}

.captcha-input-wrapper {
  flex: 1;
  margin-bottom: 0;
}

/* 验证码显示 */
.captcha-display {
  width: 120px;
  height: 48px;
  background: var(--input-bg);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  position: relative;
  z-index: 2;
  transition: var(--transition);
}

.captcha-display:hover {
  transform: scale(1.05);
  box-shadow: 0 0 10px rgba(37, 99, 235, 0.5);
}

/* 验证码输入框容器 */
.captcha-wrapper {
  position: relative;
}

.captcha-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  font-size: 13px;
  cursor: pointer;
  transition: var(--transition);
  z-index: 3;
  white-space: nowrap;
}

.captcha-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

.captcha-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 12px;
  // margin-top: 12px;
}

.form-actions .prime-btn {
  flex: 1;
}

/* 主按钮 */
.prime-btn {
  width: 100%;
  height: 48px;
  // padding: 16px;
  background: var(--primary);
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  overflow: hidden;
  position: relative;
  transition: var(--transition);
  outline: none;

}

.prime-btn:hover {
  background: var(--primary-dark);
  transform: translateY(0);
  box-shadow: 0 10px 25px -5px rgba(124, 58, 237, 0.4);
}

.prime-btn:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1), 0 0 0 4px var(--primary);
}

/* 次要按钮样式 */
.secondary-btn {
  background: rgba(255, 255, 255, 0.1) !important;
  outline: none;
}

.secondary-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

/* 完成页面样式 */
.completion-message {
  text-align: center;
  padding: 40px 0;
}

.checkmark-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.3);
  position: relative;
  overflow: hidden;
}

.checkmark-circle :deep(svg) {
  width: 40px;
  height: 40px;
  color: white;
}

.completion-message h3 {
  font-size: 24px;
  margin-bottom: 12px;
  color: var(--text-h);
}

.completion-message p {
  color: var(--text-light);
  margin-bottom: 24px;
  line-height: 1.5;
}

/* 切换文本 */
.switch-text {
  text-align: center;
  font-size: 14px;
  color: var(--text-light);
  margin-top: 20px;
}

.switch-text a {
  color: var(--primary-light);
  text-decoration: none;
  font-weight: 500;
  position: relative;
  padding-bottom: 2px;
}

.switch-text a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--primary);
  transition: var(--transition-fast);
}

.switch-text a:hover::after {
  width: 100%;
}

/* 玻璃态表单 */
.glass-form {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

input.fix-auto-fill,
.fix-auto-fill input {
  -webkit-text-fill-color: #fff !important;
  box-shadow: inherit !important;
}

/* 浏览器自动填充样式覆盖 - 使用更高优先级 */
:deep(.ant-input:-webkit-autofill),
:deep(.ant-input:-webkit-autofill:hover),
:deep(.ant-input:-webkit-autofill:focus),
:deep(.ant-input:-webkit-autofill:active),
:deep(.ant-input-password .ant-input:-webkit-autofill),
:deep(.ant-input-password .ant-input:-webkit-autofill:hover),
:deep(.ant-input-password .ant-input:-webkit-autofill:focus),
:deep(.ant-input-password .ant-input:-webkit-autofill:active) {
  -webkit-text-fill-color: #fff !important;
  -webkit-box-shadow: 0 0 0 1000px #171B21 inset !important;
  box-shadow: 0 0 0 1000px #171B21 inset !important;
  background-color: #171B21 !important;
  background-image: none !important;
  border-color: rgba(255, 255, 255, 0.05) !important;
  transition: background-color 5000s ease-in-out 0s !important;
  caret-color: #fff !important;
}

:deep(.ant-input:-webkit-autofill:focus),
:deep(.ant-input-password .ant-input:-webkit-autofill:focus) {
  -webkit-box-shadow: 0 0 0 1000px #171B21 inset, 0 0 0 1px var(--primary) !important;
  box-shadow: 0 0 0 1000px #171B21 inset, 0 0 0 1px var(--primary) !important;
  border-color: var(--primary) !important;
}

/* 强制覆盖 Ant Design 的自动填充样式 */
:deep(.fix-auto-fill.ant-input:-webkit-autofill),
:deep(.fix-auto-fill .ant-input:-webkit-autofill) {
  -webkit-box-shadow: 0 0 0 1000px #171B21 inset !important;
  box-shadow: 0 0 0 1000px #171B21 inset !important;
  background: #171B21 !important;
}

/* 密码输入框图标位置调整 */
:deep(.ant-input-password .ant-input-password-icon) {
  color: var(--text-p);
  transition: var(--transition);
}

:deep(.ant-input-password .ant-input-password-icon:hover) {
  color: var(--text-light);
}

:deep(.ant-input-password:focus-within .ant-input-password-icon) {
  color: var(--primary);
}
</style>

<style lang="less">
:root {
  --primary: #2563eb;
  --primary-light: #60a5fa;
  --primary-dark: #1d4ed8;
  --text-p: #94a3b8;
  --text-h: #f8fafc;
  --text-light: #cbd5e1;
  --input-bg: rgba(255, 255, 255, 0.03);
  --transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  --transition-fast: all 0.2s ease;
}
</style>