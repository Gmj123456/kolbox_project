<template>
  <section class="auth-panel">
    <div class="text-group">
      <img src="/@/assets/images/login_logo.png" alt="Logo" class="login-logo" />
      <h2 >注册账号</h2>
    </div>

    <!-- 邮箱注册表单 -->
    <a-form v-show="loginType === 'account'" ref="formRef" :model="formData" class="glass-form" @keyup.enter="registerHandleClick">
      <!-- 邮箱 -->
      <div class="input-wrapper">
        <MailOutlined class="input-icon" />
        <a-input
          v-model:value="formData.email"
          class="fix-auto-fill"
          type="email"
          placeholder="邮箱"
          size="large"
        />
        <div class="input-glow"></div>
      </div>

      <!-- 邮箱验证码 -->
      <div class="captcha-row">
        <div class="input-wrapper captcha-input-wrapper">
          <SafetyOutlined class="input-icon" />
          <a-input
            v-model:value="formData.captcha"
            class="fix-auto-fill"
            type="text"
            placeholder="邮箱验证码"
            size="large"
          />
          <div class="input-glow"></div>
        </div>
        <a-button
          class="captcha-btn"
          :disabled="!showEmailInterval"
          @click="getEmailCaptcha"
        >
          {{ showEmailInterval ? '获取验证码' : `${emailTimeRunning}s` }}
        </a-button>
      </div>

      <!-- 密码 -->
      <div class="input-wrapper">
        <LockOutlined class="input-icon" />
        <a-input-password
          v-model:value="formData.password"
          class="fix-auto-fill"
          :placeholder="t('sys.login.password')"
          size="large"
        />
        <div class="input-glow"></div>
      </div>

      <!-- 确认密码 -->
      <div class="input-wrapper">
        <LockOutlined class="input-icon" />
        <a-input-password
          v-model:value="formData.confirmPassword"
          class="fix-auto-fill"
          :placeholder="t('sys.login.confirmPassword')"
          size="large"
        />
        <div class="input-glow"></div>
      </div>

      <!-- 注册按钮 -->
      <a-button
        :loading="registerLoading"
        class="prime-btn"
        type="primary"
        size="large"
        @click="registerHandleClick"
      >
        <span>立即注册</span>
      </a-button>
    </a-form>

    <!-- 手机号注册表单 -->
    <a-form v-show="loginType === 'phone'" ref="phoneFormRef" :model="phoneFormData" class="glass-form" @keyup.enter="registerPhoneHandleClick">
      <!-- 手机号 -->
      <div class="input-wrapper">
        <MobileFilled  class="input-icon" />
        <a-input
          v-model:value="phoneFormData.mobile"
          class="fix-auto-fill"
          type="tel"
          placeholder="手机号"
          size="large"
        />
        <div class="input-glow"></div>
      </div>

      <!-- 手机验证码 -->
      <div class="captcha-row">
        <div class="input-wrapper captcha-input-wrapper">
          <SafetyOutlined class="input-icon" />
          <a-input
            v-model:value="phoneFormData.captcha"
            class="fix-auto-fill"
            type="text"
            placeholder="手机验证码"
            size="large"
          />
          <div class="input-glow"></div>
        </div>
        <a-button
          class="captcha-btn"
          :disabled="!showPhoneInterval"
          @click="getPhoneCaptcha"
        >
          {{ showPhoneInterval ? '获取验证码' : `${phoneTimeRunning}s` }}
        </a-button>
      </div>

      <!-- 密码 -->
      <div class="input-wrapper">
        <LockOutlined class="input-icon" />
        <a-input-password
          v-model:value="phoneFormData.password"
          class="fix-auto-fill"
          :placeholder="t('sys.login.password')"
          size="large"
        />
        <div class="input-glow"></div>
      </div>

      <!-- 确认密码 -->
      <div class="input-wrapper">
        <LockOutlined class="input-icon" />
        <a-input-password
          v-model:value="phoneFormData.confirmPassword"
          class="fix-auto-fill"
          :placeholder="t('sys.login.confirmPassword')"
          size="large"
        />
        <div class="input-glow"></div>
      </div>

      <!-- 注册按钮 -->
      <a-button
        :loading="registerLoading"
        class="prime-btn"
        type="primary"
        size="large"
        @click="registerPhoneHandleClick"
      >
        <span>立即注册</span>
      </a-button>
    </a-form>

    <!-- 其他注册方式（手机号注册时显示） -->
    <div v-show="loginType === 'phone'" class="other-login">
      <p class="other-login-text">其他注册方式</p>
      <div class="social-login-buttons">
        <button class="social-login-btn wechat-btn" @click="switchLoginType('wechat')">
          <WechatFilled />
          <span>微信注册</span>
        </button>
        <button class="social-login-btn wechat-btn" @click="switchLoginType('account')">
          <MailOutlined />
          <span>邮箱注册</span>
        </button>
      </div>
    </div>

    <!-- 微信扫码注册表单 -->
    <div v-show="loginType === 'wechat'" class="glass-form wechat-qr-container">
      <wxlogin
        :appid="'wx67267543f15cc8d5'"
        :scope="'snsapi_login'"
        :redirect_uri="'http%3A%2F%2Fp.kolbox.com%2Fwx-loading'"
        theme="white"
        href="data:text/css;base64,LmltcG93ZXJCb3ggLnFyY29kZSB7CiAgd2lkdGg6IDIyMHB4Owp9CgouaW1wb3dlckJveCAuaW5mbyB7CiAgY29sb3I6ICNmZmYgIWltcG9ydGFudDsKfQ=="
                          >
      </wxlogin>
           
      
      <div class="wechat-switch-links">
        <p class="wechat-switch-text">
          <a href="javascript:void(0)" @click="switchLoginType('account')">使用邮箱注册</a>
        </p>
      </div>
    </div>

    <!-- 其他注册方式 -->
    <div v-show="loginType === 'account'" class="other-login">
      <p class="other-login-text">其他注册方式</p>
      <div class="social-login-buttons">
        <button class="social-login-btn wechat-btn" @click="switchLoginType('wechat')">
          <WechatFilled />
          <span>微信注册</span>
        </button>
        <button class="social-login-btn wechat-btn" @click="switchLoginType('phone')">
          <MobileFilled  />
          <span>手机号注册</span>
        </button>
      </div>
    </div>

    <p v-show="loginType !== 'wechat'" class="switch-text">
      已有账号？<a href="javascript:void(0)" @click="goBackHandleClick">{{ t('sys.login.backSignIn') }}</a>
    </p>

    <!-- 旋转验证弹窗 -->
    <a-modal
      v-model:open="verifyModalVisible"
      title="安全验证"
      :footer="null"
      :width="400"
      :maskClosable="false"
      :closable="true"
      @cancel="handleVerifyCancel"
    >
      <div class="verify-container">
        <RotateDragVerify
          ref="rotateVerifyRef"
          :src="verifyImageSrc"
          @success="handleVerifySuccess"
        />
      </div>
    </a-modal>
  </section>
</template>

<script lang="ts" setup name="mini-register">
import { ref, toRaw, unref, onBeforeUnmount } from 'vue';
import { MailOutlined, LockOutlined, SafetyOutlined, WechatFilled,MobileFilled  } from '@ant-design/icons-vue';
import { register, getLoginQrcode, getQrcodeToken } from '/@/api/sys/user';
import { useMessage } from '/@/hooks/web/useMessage';
import { useI18n } from '/@/hooks/web/useI18n';
import { defHttp } from '/@/utils/http/axios';
import { useUserStore } from '/@/store/modules/user';
import wxlogin from 'vue-wxlogin';
import { RotateDragVerify } from '/@/components/Verify/index';
import verifyImage from '/@/assets/images/ai/avatar.jpg';

const { t } = useI18n();
const { notification, createMessage } = useMessage();
const userStore = useUserStore();
const emit = defineEmits(['go-back', 'success', 'register']);
const formRef = ref();
const phoneFormRef = ref();
const registerLoading = ref(false);
const formData = ref<any>({
  email: '',
  captcha: '',
  password: '',
  confirmPassword: '',
});
const phoneFormData = ref<any>({
  mobile: '',
  captcha: '',
  password: '',
  confirmPassword: '',
});

// 登录类型：'account' | 'wechat'
const loginType = ref<string>('account');
const qrCodeUrl = ref<string>('');
let qrCodeTimer: IntervalHandle | null = null;

// 是否显示获取验证码（邮箱验证码）
const showEmailInterval = ref<boolean>(true);
// 60s（邮箱验证码倒计时）
const emailTimeRunning = ref<number>(60);
// 定时器（邮箱验证码）
const emailTimer = ref<any>(null);

// 是否显示获取验证码（手机号验证码）
const showPhoneInterval = ref<boolean>(true);
// 60s（手机号验证码倒计时）
const phoneTimeRunning = ref<number>(60);
// 定时器（手机号验证码）
const phoneTimer = ref<any>(null);

// 旋转验证相关
const verifyModalVisible = ref<boolean>(false);
const rotateVerifyRef = ref<any>(null);
const verifyImageSrc = verifyImage;
const phoneCaptchaPending = ref<boolean>(false); // 标记是否等待验证后发送验证码

/**
 * 切换登录类型
 */
function switchLoginType(type: string) {
  loginType.value = type;
  if (type === 'wechat') {
    loadQrCode();
  } else {
    closeQrCodeTimer();
  }
}

/**
 * 加载微信二维码
 */
function loadQrCode() {
  qrCodeUrl.value = '';
  getLoginQrcode()
    .then((res) => {
      qrCodeUrl.value = res.qrcodeId;
      if (res.qrcodeId) {
        openQrCodeTimer(res.qrcodeId);
      }
    })
    .catch((error) => {
      console.error('loadQrCode error:', error);
      createMessage.error('获取二维码失败');
    });
}

/**
 * 监控扫码状态
 */
function watchQrcodeToken(qrcodeId: string) {
  getQrcodeToken({ qrcodeId: qrcodeId })
    .then((res) => {
      const token = res.token;
      if (token == '-2') {
        // 二维码过期重新获取
        loadQrCode();
        closeQrCodeTimer();
      }
      // 扫码成功
      if (res.success) {
        closeQrCodeTimer();
        setTimeout(() => {
          userStore.qrCodeLogin(token);
        }, 500);
      }
    })
    .catch((error) => {
      console.error('watchQrcodeToken error:', error);
    });
}

/**
 * 开启二维码定时器
 */
function openQrCodeTimer(qrcodeId: string) {
  watchQrcodeToken(qrcodeId);
  closeQrCodeTimer();
  qrCodeTimer = setInterval(() => {
    watchQrcodeToken(qrcodeId);
  }, 1500);
}

/**
 * 关闭二维码定时器
 */
function closeQrCodeTimer() {
  if (qrCodeTimer) {
    clearInterval(qrCodeTimer);
    qrCodeTimer = null;
  }
}


/**
 * 返回
 */
function goBackHandleClick() {
  emit('go-back');
  initForm();
}

/**
 * 获取邮箱验证码
 */
function getEmailCaptcha() {
  if (!formData.value.email) {
    createMessage.warn('请输入邮箱地址');
    return;
  }
  // 如果正在倒计时，不允许重复点击
  if (!showEmailInterval.value) {
    return;
  }
  defHttp
    .post(
      { url: '/sys/getEmailCode', params: { email: formData.value.email, smsmode: '1' } },
      { isTransformResponse: false }
    )
    .then((res) => {
      if (res.success) {
        createMessage.success('验证码发送成功');
        // 启动倒计时
        startEmailCountdown();
      } else {
        createMessage.warn(res.message);
      }
    })
    .catch((error) => {
      console.error('getEmailCaptcha error:', error);
      createMessage.error('获取验证码失败');
    });
}

/**
 * 启动邮箱验证码倒计时
 */
function startEmailCountdown() {
  const TIME_COUNT = 60;
  if (!unref(emailTimer)) {
    emailTimeRunning.value = TIME_COUNT;
    showEmailInterval.value = false;
    emailTimer.value = setInterval(() => {
      if (unref(emailTimeRunning) > 0 && unref(emailTimeRunning) <= TIME_COUNT) {
        emailTimeRunning.value = emailTimeRunning.value - 1;
      } else {
        showEmailInterval.value = true;
        clearInterval(unref(emailTimer));
        emailTimer.value = null;
      }
    }, 1000);
  }
}


function registerHandleClick() {
  if (!formData.value.email) {
    createMessage.warn('请输入邮箱地址');
    return;
  }
  if (!formData.value.captcha) {
    createMessage.warn('请输入邮箱验证码');
    return;
  }
  if (!formData.value.password) {
    createMessage.warn('请输入密码');
    return;
  }
  if (!formData.value.confirmPassword) {
    createMessage.warn('请输入确认密码');
    return;
  }
  if (formData.value.password !== formData.value.confirmPassword) {
    createMessage.warn('密码不一致');
    return;
  }
  registerAccount();
}
function generateRandomUsername() {
  const prefix = "SG";
  const maxLength = 6;
  const availableLength = maxLength - prefix.length; // 最多再加4个字符

  // 可选字符集（字母 + 数字）
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  
  let suffix = "";
  const suffixLength = Math.floor(Math.random() * (availableLength + 1)); // 0 到 4 之间

  for (let i = 0; i < suffixLength; i++) {
    suffix += chars.charAt(Math.floor(Math.random() * chars.length));
  }

  return prefix + suffix;
}
/**
 * 注册账号
 */
async function registerAccount() {
  try {
    registerLoading.value = true;
    // const username = generateRandomUsername()
    const resultInfo = await register(
      toRaw({
        // username,
        email: formData.value.email,
        emailCode: formData.value.captcha,
        password: formData.value.password,
      })
    );
    if (resultInfo && resultInfo.data.success) {
      notification.success({
     
        description: resultInfo.data.message || t('sys.api.registerMsg'),
        duration: 3,
      });
      emit('success', { username:formData.value.email, password: formData.value.password });
      initForm();
    } else {
      notification.warning({
    
        description: resultInfo.data.message || t('sys.api.networkExceptionMsg'),
        duration: 3,
      });
    }
  } catch (error: any) {
    notification.error({
      // message: t('sys.api.errorTip'),
      description: error?.message || t('sys.api.networkExceptionMsg'),
      duration: 3,
    });
  } finally {
    registerLoading.value = false;
  }
}

/**
 * 初始化表单
 */
function initForm() {
  Object.assign(formData.value, {
    email: undefined,
    captcha: undefined,
    password: undefined,
    confirmPassword: undefined,
  });
  Object.assign(phoneFormData.value, {
    mobile: undefined,
    captcha: undefined,
    password: undefined,
    confirmPassword: undefined,
  });
  // 清除邮箱验证码倒计时
  if (unref(emailTimer)) {
    showEmailInterval.value = true;
    clearInterval(unref(emailTimer));
    emailTimer.value = null;
  }
  // 清除手机验证码倒计时
  if (unref(phoneTimer)) {
    showPhoneInterval.value = true;
    clearInterval(unref(phoneTimer));
    phoneTimer.value = null;
  }
  // 重置登录类型
  loginType.value = 'account';
  closeQrCodeTimer();
  formRef.value?.resetFields();
  phoneFormRef.value?.resetFields();
  // 关闭验证弹窗
  verifyModalVisible.value = false;
  phoneCaptchaPending.value = false;
  // 重置验证组件
  if (rotateVerifyRef.value) {
    rotateVerifyRef.value.resume?.();
  }
}


/**
 * 组件卸载前清理定时器
 */
onBeforeUnmount(() => {
  if (emailTimer.value) {
    clearInterval(emailTimer.value);
    emailTimer.value = null;
  }
  if (phoneTimer.value) {
    clearInterval(phoneTimer.value);
    phoneTimer.value = null;
  }
  closeQrCodeTimer();
});

defineExpose({
  initForm,
});

/**
 * 获取手机验证码
 */
function getPhoneCaptcha() {
  if (!phoneFormData.value.mobile) {
    createMessage.warn('请输入手机号');
    return;
  }
  // 手机号格式校验（以1开头的11位数字）
  const mobilePattern = /^1\d{10}$/;
  if (!mobilePattern.test(phoneFormData.value.mobile)) {
    createMessage.warn('请输入正确的手机号格式');
    return;
  }
  if (!showPhoneInterval.value) {
    return;
  }
  // 先显示验证弹窗
  phoneCaptchaPending.value = true;
  verifyModalVisible.value = true;
  // 重置验证组件
  if (rotateVerifyRef.value) {
    rotateVerifyRef.value.resume?.();
  }
}

/**
 * 验证成功后发送验证码
 */
function sendPhoneCaptchaAfterVerify() {
  defHttp
    .post(
      { url: '/sys/sms', params: { mobile: phoneFormData.value.mobile, smsmode: '1' } },
      { isTransformResponse: false }
    )
    .then((res) => {
      if (res.success) {
        createMessage.success('验证码发送成功');
        startPhoneCountdown();
       
      } else {
        createMessage.warn(res.message);
        // 验证失败，重置验证组件
        if (rotateVerifyRef.value) {
          rotateVerifyRef.value.resume?.();
        }
      }
    })
    .catch((error) => {
      console.error('sendPhoneCaptchaAfterVerify error:', error);
      createMessage.error('获取验证码失败');
      // 验证失败，重置验证组件
      if (rotateVerifyRef.value) {
        rotateVerifyRef.value.resume?.();
      }
    }).finally(() => {
      verifyModalVisible.value = false;
      phoneCaptchaPending.value = false;
    });
}

/**
 * 验证成功回调
 */
function handleVerifySuccess() {
  if (phoneCaptchaPending.value) {
    // 验证通过，发送验证码
    console.log(1111)
    sendPhoneCaptchaAfterVerify();
  }
}

/**
 * 取消验证弹窗
 */
function handleVerifyCancel() {
  verifyModalVisible.value = false;
  phoneCaptchaPending.value = false;
  // 重置验证组件
  if (rotateVerifyRef.value) {
    rotateVerifyRef.value.resume?.();
  }
}

/**
 * 启动手机验证码倒计时
 */
function startPhoneCountdown() {
  const TIME_COUNT = 60;
  if (!unref(phoneTimer)) {
    phoneTimeRunning.value = TIME_COUNT;
    showPhoneInterval.value = false;
    phoneTimer.value = setInterval(() => {
      if (unref(phoneTimeRunning) > 0 && unref(phoneTimeRunning) <= TIME_COUNT) {
        phoneTimeRunning.value = phoneTimeRunning.value - 1;
      } else {
        showPhoneInterval.value = true;
        clearInterval(unref(phoneTimer));
        phoneTimer.value = null;
      }
    }, 1000);
  }
}

function registerPhoneHandleClick() {
  if (!phoneFormData.value.mobile) {
    createMessage.warn('请输入手机号');
    return;
  }
  if (!phoneFormData.value.captcha) {
    createMessage.warn('请输入手机验证码');
    return;
  }
  if (!phoneFormData.value.password) {
    createMessage.warn('请输入密码');
    return;
  }
  if (!phoneFormData.value.confirmPassword) {
    createMessage.warn('请输入确认密码');
    return;
  }
  if (phoneFormData.value.password !== phoneFormData.value.confirmPassword) {
    createMessage.warn('密码不一致');
    return;
  }
  registerPhoneAccount();
}

/**
 * 手机号注册
 */
async function registerPhoneAccount() {
  try {
    registerLoading.value = true;
    // const username = generateRandomUsername();
    const resultInfo = await register(
      toRaw({
        // username,
        phone: phoneFormData.value.mobile,
        smscode: phoneFormData.value.captcha,
        password: phoneFormData.value.password,
      })
    );
    if (resultInfo && resultInfo.data.success) {
      notification.success({
        // message: t('sys.api.successTip'),
        description: resultInfo.data.message || t('sys.api.registerMsg'),
        duration: 3,
      });
      emit('success', { username:phoneFormData.value.mobile, password: phoneFormData.value.password });
      initForm();
    } else {
      notification.warning({
        // message: t('sys.api.errorTip'),
        description: resultInfo.data.message || t('sys.api.networkExceptionMsg'),
        duration: 3,
      });
    }
  } catch (error: any) {
    notification.error({
      message: t('sys.api.errorTip'),
      description: error?.message || t('sys.api.networkExceptionMsg'),
      duration: 3,
    });
  } finally {
    registerLoading.value = false;
  }
}
</script>

<style lang="less" scoped>
  .social-login-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;

  background: rgba(255, 255, 255, 0.08);
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  outline: none;
  height: 48px;
}

.social-login-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.social-login-btn svg,
.social-login-btn i {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}
  .wechat-btn {
  background: rgba(37, 211, 102, 0.15);
  border-color: rgba(37, 211, 102, 0.4);
}

.wechat-btn:hover {
  background: rgba(37, 211, 102, 0.25);
  border-color: rgba(37, 211, 102, 0.6);
}

.wechat-btn svg,
.wechat-btn i {
  color: #25d366;
}

/* 其他注册方式 */
.other-login {
  margin-top: 24px;
  text-align: center;
}

.other-login-text {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  margin-bottom: 16px;
  position: relative;
}

.other-login-text::before,
.other-login-text::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 60px;
  height: 1px;
  background: rgba(255, 255, 255, 0.2);
}

.other-login-text::before {
  left: 0;
}

.other-login-text::after {
  right: 0;
}

.social-login-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* 微信二维码注册样式 */
.wechat-qr-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 20px 0;
}

.wechat-switch-links {
  display: flex;
  flex-direction: row;
  gap: 24px;
  margin-top: 16px;
  justify-content: center;
}

.wechat-switch-text {
  text-align: center;
  font-size: 14px;
  color: #60a5fa;
  margin: 0;
}

.wechat-switch-text a {
  color: #60a5fa;
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
  position: relative;
  padding-bottom: 2px;
}

.wechat-switch-text a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background: #2563eb;
  transition: all 0.2s ease;
}

.wechat-switch-text a:hover::after {
  width: 100%;
}

/* 认证面板 */
.auth-panel {
  position: relative;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  opacity: 1;
  min-height: auto;
  height: auto;
  display: flex;
  flex-direction: column;
}

/* 文本组 */
.text-group {
  text-align: left;
  margin-bottom: 20px;
}

.text-group h2 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #f8fafc;
}

.text-group p {
  color: #cbd5e1;
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.5;
}

/* 登录页面logo */
.login-logo {
  width: 280px;
  height: auto;
  display: block;
  margin: 0 0 32px 0;
}

/* 输入框细节 */
.input-wrapper {
  position: relative;
  margin-bottom: 16px;
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
  color: #94a3b8;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  pointer-events: none;
  z-index: 2;
}

:deep(.ant-input),
:deep(.ant-input-password),
:deep(.ant-input-group) {
  width: 100%;
}

:deep(.ant-input),
:deep(.ant-input-password .ant-input) {
  height: 48px;
  padding: 0 16px 0 0px;
  background: #171B21;
  // border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  color: white;
  font-size: 15px;
  outline: none;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

:deep(.ant-input::placeholder),
:deep(.ant-input-password input::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.ant-input:focus),
:deep(.ant-input-password:focus),
:deep(.ant-input-password .ant-input:focus) {

  border-color: none !important;
  box-shadow: none !important;
  outline: none !important;
}

.input-wrapper:focus-within .input-icon {
  color: #2563eb;
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
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  pointer-events: none;
}

.input-wrapper:focus-within .input-glow {
  opacity: 1;
  box-shadow: 0 0 0 1px #2563eb, 0 0 20px rgba(37, 99, 235, 0.3);
}

/* 验证码行容器 */
.captcha-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  height: 48px;
}

.captcha-input-wrapper {
  flex: 1;
  margin-bottom: 0;
}

/* 验证码按钮 */
.captcha-btn {
  padding: 12px 16px;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  white-space: nowrap;
  flex-shrink: 0;
  height: 48px;
  outline: none;
}

.captcha-btn:hover {
  background: #1d4ed8;
}

.captcha-btn:disabled {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.5);
  cursor: not-allowed;
}

/* 主按钮 */
.prime-btn {
  width: 100%;
  // padding: 16px;
  height: 48px;
  background: #2563eb;
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
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  outline: none;
  margin-top: 8px;
}

.prime-btn:hover {
  background: #1d4ed8;
  transform: translateY(0);
  box-shadow: 0 10px 25px -5px rgba(124, 58, 237, 0.4);
}

.prime-btn:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1), 0 0 0 4px #2563eb;
}

/* 按钮扫光动画 */
.prime-btn::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: 0.5s;
}

.prime-btn:hover::after {
  left: 100%;
}

/* 切换文本 */
.switch-text {
  text-align: center;
  font-size: 14px;
  color: #cbd5e1;
  margin-top: 20px;
}

.switch-text a {
  color: #60a5fa;
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
  background: #2563eb;
  transition: all 0.2s ease;
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

/* 手机号输入框特殊样式 */
:deep(.ant-input-group .ant-input-group-addon) {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-right: none;
  border-radius: 14px 0 0 14px;
  padding: 0;
}

:deep(.ant-input-group .ant-input-group-addon .ant-select) {
  width: 70px;
}

:deep(.ant-input-group .ant-input-group-addon .ant-select .ant-select-selector) {
  background: transparent;
  border: none;
  color: white;
}

:deep(.ant-input-group .ant-input) {
  border-left: none;
  border-radius: 0 14px 14px 0;
}
</style>

<style lang="less">
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
  -webkit-box-shadow: 0 0 0 1000px #171B21 inset, 0 0 0 1px #2563eb !important;
  box-shadow: 0 0 0 1000px #171B21 inset, 0 0 0 1px #2563eb !important;
  border-color: #2563eb !important;
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
  color: #94a3b8;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

:deep(.ant-input-password .ant-input-password-icon:hover) {
  color: #cbd5e1;
}

:deep(.ant-input-password:focus-within .ant-input-password-icon) {
  color: #2563eb;
}
:deep( .ant-input-password .ant-input){
  padding: 0px !important;
}

/* 验证容器样式 */
.verify-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  min-height: 300px;
}
</style>