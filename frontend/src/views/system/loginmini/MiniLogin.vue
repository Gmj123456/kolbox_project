<template>
  <div :class="prefixCls" class="new-login-container">
    <!-- 星空背景 -->
    <div class="stars-container"></div>
    <!-- 网格背景 -->
    <div class="grid-bg"></div>
    <!-- 光晕背景 -->
    <div class="glow-bg"></div>

    <!-- 双栏布局容器 -->
    <div class="split-container">
      <!-- 左侧图片轮播区域 -->
      <div class="video-panel">
        <div class="carousel-wrapper">
          <div class="carousel-container">
            <div class="carousel-track">
              <img
                v-for="(slideUrl, index) in carouselSlides"
                :key="index"
                :src="slideUrl"
                :alt="`Slide ${index + 1}`"
                :class="['carousel-slide', { active: currentSlideIndex === index }]"
              />
            </div>
            <div class="carousel-indicators">
              <button
                v-for="(_, index) in carouselSlides"
                :key="index"
                :class="['indicator-btn', { active: currentSlideIndex === index }]"
                @click="switchSlide(index)"
              >
                {{ index + 1 }}
              </button>
            </div>
          </div>
          <!-- 图片标题 -->
          <div class="video-title">让中国品牌，在世界发声</div>
          <!-- 图片副标题 -->
          <div class="video-subtitle"> 连接全球 30W+ 优质创作者，以数据驱动的创意内容，为 DTC 品牌构建从曝光到转化的完整增长闭环</div>
          <!-- 品牌墙 -->
          <div class="brand-wall">
            <div class="brand-track">
              <div v-for="(brand, index) in brandList" :key="index" class="brand-item">
                {{ brand }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录功能区 -->
      <div class="auth-panel-container">
        <main class="auth-container">
          <div class="auth-card" :class="{ 'card-transition': cardTransition }">
            <!-- 登录面板 -->
            <section v-if="type === 'login' || loginType === 'register'" class="auth-panel">
              <div class="text-group">
                <img src="/@/assets/images/login_logo.png" alt="Logo" class="login-logo" />
                <h2 v-if="loginType === 'account'">欢迎登录</h2>
                <h2 v-if="loginType === 'register'">注册账号</h2>
              </div>

              <!-- 账号密码登录表单 -->
              <a-form v-if="loginType === 'account'" :model="formData" class="glass-form" @keyup.enter="loginHandleClick">
                <div class="input-wrapper">
                  <UserOutlined class="input-icon" />
                  <a-input
                    v-model:value="formData.username"
                    class="fix-auto-fill"
                    placeholder="用户名/邮箱/手机号"
                    size="large"
                  />
                  <div class="input-glow"></div>
                </div>

                <div class="input-wrapper" >
                  <LockOutlined class="input-icon" />
                  <a-input-password
            
                    v-model:value="formData.password"
                    class="fix-auto-fill"
                    :placeholder="t('sys.login.password')"
                
                  >
                    
                  </a-input-password>
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

                <div class="form-footer">
                 
                  <a href="javascript:void(0)" class="link-dim" @click="forgetHandelClick">忘记密码?</a>
                </div>

                <a-button :loading="loginLoading" class="prime-btn" type="primary" size="large" @click="loginHandleClick">
                  <span>{{ t('sys.login.loginButton') }}</span>
                </a-button>
              </a-form>

              <!-- 微信扫码登录表单 -->
              <div v-if="loginType === 'wechat'" class="glass-form wechat-qr-container">
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
                    <a href="javascript:void(0)" @click="switchLoginType('account')">使用已有账号登录</a>
                  </p>
                  <p class="wechat-switch-text">
                    <a href="javascript:void(0)" @click="registerHandleClick">创建免费账号</a>
                  </p>
                </div>
              </div>

              <div v-if="loginType === 'account'" class="other-login">
                <p class="other-login-text">其他登录方式</p>
                <div class="social-login-buttons">
                  <button class="social-login-btn wechat-btn" @click="switchLoginType('wechat')">
                    <WechatFilled />
                    <span>微信登录</span>
                  </button>
                
                </div>
              </div>

              <p v-if="loginType !== 'wechat'" class="switch-text">
                新用户？<a href="javascript:void(0)" @click="registerHandleClick">创建免费账号</a>
              </p>
            </section>

            <!-- 忘记密码面板 -->
            <div v-if="type === 'forgot'" :class="`${prefixCls}-form`">
              <MiniForgotpad ref="forgotRef" @go-back="goBack" @success="handleSuccess" />
            </div>

            <!-- 注册面板 -->
            <div v-if="type === 'register'" :class="`${prefixCls}-form`">
              <MiniRegister ref="registerRef" @go-back="goBack" @success="handleSuccess" />
            </div>

            <!-- 扫码登录面板 -->
            <div v-if="type === 'codeLogin'" :class="`${prefixCls}-form`">
              <MiniCodelogin ref="codeRef" @go-back="goBack" @success="handleSuccess" />
            </div>
          </div>
        </main>
      </div>
    </div>
    <!-- 第三方登录相关弹框 -->
    <ThirdModal ref="thirdModalRef"></ThirdModal>

    <!-- 图片验证码弹窗 -->
    <CaptchaModal @register="captchaRegisterModal" @ok="getLoginCode" />
  </div>
</template>

<script lang="ts" setup name="login-mini">
import { UserOutlined, LockOutlined, SmileOutlined, WechatFilled,SafetyOutlined } from '@ant-design/icons-vue';
import { getCaptcha, getCodeInfo, getLoginQrcode, getQrcodeToken } from '/@/api/sys/user';
import { onMounted, onUnmounted, reactive, ref, toRaw, unref } from 'vue';
import codeImg from '/@/assets/images/checkcode.png';
import { useUserStore } from '/@/store/modules/user';
import { useMessage } from '/@/hooks/web/useMessage';
import { useI18n } from '/@/hooks/web/useI18n';
import { SmsEnum } from '/@/views/sys/login/useLogin';
import ThirdModal from '/@/views/sys/login/ThirdModal.vue';
import MiniForgotpad from './MiniForgotpad.vue';
import MiniRegister from './MiniRegister.vue';
import MiniCodelogin from './MiniCodelogin.vue';
import { useDesign } from '/@/hooks/web/useDesign';
import CaptchaModal from '@/components/jeecg/captcha/CaptchaModal.vue';
import { useModal } from '@/components/Modal';
import { ExceptionEnum } from '@/enums/exceptionEnum';
import { QrCode } from '/@/components/Qrcode/index';
import { useRoute } from 'vue-router'

const route = useRoute()
import wxlogin from 'vue-wxlogin'
const { prefixCls } = useDesign('mini-login');
const { notification, createMessage } = useMessage();
const userStore = useUserStore();
const { t } = useI18n();
const [captchaRegisterModal, { openModal: openCaptchaModal }] = useModal();

defineProps({
  sessionTimeout: {
    type: Boolean,
  },
});


// 轮播图相关
const carouselSlides = [
  new URL('@/assets/images/login_bg1.png', import.meta.url).href,
  new URL('@/assets/images/login_bg2.png', import.meta.url).href,
  new URL('@/assets/images/login_bg5.png', import.meta.url).href,
];
const currentSlideIndex = ref(0);
let carouselTimer: any = null;

// 品牌列表
const brandList = [
  'SHEIN',
  'Temu',
  'PatPat',
  'Anker',
  'Baleaf',
  'COOFANDY',
  'DDG',
  'Eufy',
  'H2ofloss',
  'HBADA',
  'Homary',
  'Jisulife',
  'Laifen',
  'OBSBOT',
  'OROLAY',
  'PLAUD',
  'RITFIT',
  'Rokid',
  'SANTECO',
  'SHEGLAM',
  'SUUKSESS',
  'UIKE',
  'OBSBOT',
  'OROLAY',
  'TOSHIBA',
];

// 卡片过渡动画
const cardTransition = ref(false);

const randCodeData = reactive<any>({
  randCodeImage: '',
  requestCodeSuccess: false,
  checkKey: null,
});
const rememberMe = ref(false);
const activeIndex = ref<string>('accountLogin');
const type = ref<string>('login');
const loginType = ref<string>('account'); // 'account' | 'wechat'
const qrCodeUrl = ref<string>('');
let qrCodeTimer: IntervalHandle | null = null;
const formData = reactive<any>({
  inputCode: '',
  username: '',
  password: '',
});
const phoneFormData = reactive<any>({
  mobile: '',
  smscode: '',
});
const thirdModalRef = ref();
const codeRef = ref();
const showInterval = ref<boolean>(true);
const timeRuning = ref<number>(60);
const timer = ref<any>(null);
const forgotRef = ref();
const registerRef = ref();
const loginLoading = ref<boolean>(false);

/**
 * 切换轮播图
 */
function switchSlide(index: number) {
  currentSlideIndex.value = index;
}

/**
 * 自动轮播
 */
function startCarousel() {
  carouselTimer = setInterval(() => {
    currentSlideIndex.value = (currentSlideIndex.value + 1) % carouselSlides.length;
  }, 10000);
}

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

/**
 * 账号或者手机登录
 */
async function loginHandleClick() {
  if (unref(activeIndex) === 'accountLogin') {
    accountLogin();
  } else {
    phoneLogin();
  }
}

async function accountLogin() {
  if (!formData.username) {
    createMessage.warn(t('sys.login.accountPlaceholder'));
    return;
  }
  if (!formData.password) {
    createMessage.warn(t('sys.login.passwordPlaceholder'));
    return;
  }
    try {
      loginLoading.value = true;
      const loginParam: any = {
        password: formData.password,
        username: '',
        email: '',
        captcha: formData.inputCode,
        checkKey: randCodeData.checkKey,
        mode: 'none',
      }
      if (formData.username) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
   
       
        const mobilePattern = /^1[3-9]\d{9}$/;
        console.log(mobilePattern.test(formData.username))
        if (emailPattern.test(formData.username)) {
          loginParam.email = formData.username;
          delete loginParam.username;
          delete loginParam.mobile;
        } else if (mobilePattern.test(formData.username)) {
          loginParam.mobile = formData.username;
          delete loginParam.username;
          delete loginParam.email;
        } else {
          loginParam.username = formData.username;
          delete loginParam.email;
          delete loginParam.mobile;
        }
      }
      const result = await userStore.login(
        toRaw(loginParam)
      );
      if (result?.userInfo) {
        notification.success({
          message: t('sys.login.loginSuccessTitle'),
          description: `${t('sys.login.loginSuccessDesc')}: ${result.userInfo.realname}`,
          duration: 3,
        });
      }
    } catch (error: any) {
      notification.error({
        message: t('sys.api.errorTip'),
        description: error?.message || t('sys.login.networkExceptionMsg'),
        duration: 3,
      });
      handleChangeCheckCode();
    } finally {
      loginLoading.value = false;
    }
}

/**
 * 手机号登录
 */
async function phoneLogin() {
  if (!phoneFormData.mobile) {
    createMessage.warn(t('sys.login.mobilePlaceholder'));
    return;
  }
  if (!phoneFormData.smscode) {
    createMessage.warn(t('sys.login.smsPlaceholder'));
    return;
  }
  try {
    loginLoading.value = true;
    const result: any = await userStore.phoneLogin({
      captcha: phoneFormData.smscode,
      mode: 'none',
    } as any);
    if (result?.userInfo) {
      notification.success({
        message: t('sys.login.loginSuccessTitle'),
        description: `${t('sys.login.loginSuccessDesc')}: ${result.userInfo.realname}`,
        duration: 3,
      });
    }
  } catch (error: any) {
    notification.error({
      message: t('sys.api.errorTip'),
      description: error?.message || t('sys.login.networkExceptionMsg'),
      duration: 3,
    });
  } finally {
    loginLoading.value = false;
  }
}

/**
 * 获取手机验证码
 */
async function getLoginCode() {
  if (!phoneFormData.mobile) {
    createMessage.warn(t('sys.login.mobilePlaceholder'));
    return;
  }
  const result = await getCaptcha({ mobile: phoneFormData.mobile, smsmode: SmsEnum.LOGIN }).catch((res) => {
    if (res.code === ExceptionEnum.PHONE_SMS_FAIL_CODE) {
      openCaptchaModal(true, {});
    }
  });
  if (result) {
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
  }
}

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
 * 第三方登录
 */
function onThirdLogin() {
    location.href =
        'https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101941012&redirect_uri=https%3a%2f%2fp.kolbox.com%2fuser%2fqq-loading'
}

/**
 * 忘记密码
 */
function forgetHandelClick() {
  cardTransition.value = true;
  type.value = 'forgot';
  setTimeout(() => {
    forgotRef.value?.initForm();
    cardTransition.value = false;
  }, 300);
}

/**
 * 返回登录页面
 */
function goBack() {
  cardTransition.value = true;
  activeIndex.value = 'accountLogin';
  type.value = 'login';
  loginType.value = 'account';
  closeQrCodeTimer();
  setTimeout(() => {
    cardTransition.value = false;
  }, 300);
}

/**
 * 忘记密码/注册账号回调事件
 */
function handleSuccess(value: any) {
  // 延迟赋值，避免浏览器自动填充覆盖背景色
  setTimeout(() => {
    Object.assign(formData, value);
    Object.assign(phoneFormData, { mobile: '', smscode: '' });
    // 强制重新渲染输入框，清除自动填充样式
    const inputs = document.querySelectorAll('.fix-auto-fill input');
    inputs.forEach((input: any) => {
      if (input) {
        const value = input.value;
        input.value = '';
        setTimeout(() => {
          input.value = value;
        }, 0);
      }
    });
  }, 100);
  type.value = 'login';
  activeIndex.value = 'accountLogin';
  handleChangeCheckCode();
}

/**
 * 注册
 */
function registerHandleClick() {
  cardTransition.value = true;
  type.value = 'register';
  setTimeout(() => {
    registerRef.value?.initForm();
    cardTransition.value = false;
  }, 300);
}


onMounted(() => {
  // 设置页面背景色
  document.body.style.backgroundColor = '#0b0e14';
  document.documentElement.style.backgroundColor = '#0b0e14';
  handleChangeCheckCode();
  startCarousel();

  // 路由中type=1 执行 registerHandleClick
 if (route.query.type == '1') {
    registerHandleClick()
  }
});

onUnmounted(() => {
  // 恢复默认背景色（可选）
  // document.body.style.backgroundColor = '';
  // document.documentElement.style.backgroundColor = '';
  if (carouselTimer) {
    clearInterval(carouselTimer);
  }
  if (timer.value) {
    clearInterval(timer.value);
  }
  closeQrCodeTimer();
});
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{namespace}-mini-login';

.new-login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: #0b0e14 !important;
  color: #f8fafc;
  font-family: 'Inter', -apple-system, sans-serif;
  overflow: hidden;
}

/* 星空背景 */
.stars-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  overflow: hidden;
}

.stars-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(2px 2px at 20px 30px, #fff, transparent),
    radial-gradient(1px 1px at 40px 70px, #fff, transparent),
    radial-gradient(1px 1px at 90px 40px, #fff, transparent),
    radial-gradient(2px 2px at 130px 80px, rgba(255, 255, 255, 0.8), transparent),
    radial-gradient(1px 1px at 160px 30px, rgba(255, 255, 255, 0.6), transparent),
    radial-gradient(2px 2px at 200px 50px, #fff, transparent),
    radial-gradient(1px 1px at 250px 90px, rgba(255, 255, 255, 0.7), transparent),
    radial-gradient(1px 1px at 300px 20px, #fff, transparent),
    radial-gradient(2px 2px at 350px 60px, rgba(255, 255, 255, 0.5), transparent),
    radial-gradient(1px 1px at 50px 100px, #fff, transparent);
  background-size: 400px 200px;
  animation: sparkle 10s linear infinite;
}

.stars-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(1.5px 1.5px at 60px 40px, #fff, transparent),
    radial-gradient(1px 1px at 180px 120px, #fff, transparent),
    radial-gradient(1px 1px at 320px 70px, rgba(255, 255, 255, 0.9), transparent),
    radial-gradient(2px 2px at 80px 160px, #fff, transparent),
    radial-gradient(1px 1px at 220px 30px, rgba(255, 255, 255, 0.7), transparent);
  background-size: 400px 200px;
  animation: twinkle 15s linear infinite;
  opacity: 0.7;
}

@keyframes sparkle {
  from {
    transform: translateY(0px);
  }
  to {
    transform: translateY(-100px);
  }
}

@keyframes twinkle {
  0% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.3;
  }
}

/* 网格背景 */
.grid-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  background-image: linear-gradient(rgba(37, 99, 235, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(37, 99, 235, 0.1) 1px, transparent 1px);
  background-size: 40px 40px;
  animation: grid-move 20s linear infinite;
}

@keyframes grid-move {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(40px, 40px);
  }
}

/* 光晕背景 */
.glow-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  pointer-events: none;
  overflow: hidden;
}

.glow-bg::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.15) 0%, transparent 70%);
  top: -300px;
  left: -300px;
  filter: blur(60px);
  animation: glow-move 8s ease-in-out infinite alternate;
}

.glow-bg::after {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.1) 0%, transparent 70%);
  bottom: -250px;
  right: -250px;
  filter: blur(60px);
  animation: glow-move 10s ease-in-out infinite alternate-reverse;
}

@keyframes glow-move {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(100px, 100px);
  }
}

/* 双栏布局容器 */
.split-container {
  display: flex;
  width: 100%;
  height: 100vh;
}

/* 左侧图片面板 */
.video-panel {
  flex: 5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding-left: 70px;
}

/* 轮播图包装容器 */
.carousel-wrapper {
  width: calc(100% - 140px);
  margin-left: 70px;
  margin-right: 70px;
  position: relative;
  display: flex;
  flex-direction: column;
  // align-items: center;
}

/* 轮播图容器 */
.carousel-container {
  width: 100%;
  height: 55vh;
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.carousel-track {
  width: 100%;
  height: 100%;
  position: relative;
}

.carousel-slide {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  object-fit: cover;
  object-position: center 20%;
  display: none;
  z-index: 1;
}

.carousel-slide.active {
  display: block;
  z-index: 2;
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 150;
}

.indicator-btn {
  background: rgba(255, 255, 255, 0.4);
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.indicator-btn:hover {
  background: rgba(255, 255, 255, 0.7);
}

.indicator-btn.active {
  background: @primary-color;
}

/* 视频标题 */
.video-title {
  margin-top: 30px;
  margin-bottom: 16px;
  background: linear-gradient(90deg, #61a4fa, #f273ba, #61a4fa);
  background-size: 200% auto;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 56px;
  font-weight: 800;
  text-align: center;
  z-index: 10;
  white-space: nowrap;
  animation: gradient-shift 3s ease-in-out infinite;
  width: 100%;
  text-align: left;
}

@keyframes gradient-shift {
  0% {
    background-position: 0% center;
  }
  50% {
    background-position: 100% center;
  }
  100% {
    background-position: 0% center;
  }
}

.video-subtitle {

  margin-bottom: 20px;
  color: rgba(255, 255, 255, 0.85);
  font-size: 20px;
  text-align: left;
  z-index: 2;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.5);
  max-width: 100%;
  line-height: 1.5;
}

/* 品牌墙 */
.brand-wall {
  width: 100%;
  max-width: 100%;
  height: 50px;
  overflow: hidden;
  z-index: 2;
}

.brand-track {
  display: flex;
  align-items: center;
  animation: scroll 20s linear infinite;
  width: max-content;
}

.brand-item {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 30px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  font-weight: 500;
  white-space: nowrap;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

@keyframes scroll {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

/* 右侧登录功能区容器 */
.auth-panel-container {
  flex: 5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding-right: 70px;
}

/* 卡片容器 */
.auth-container {
  perspective: 1000px;
  z-index: 10;
  width: 100%;
  max-width: 500px;
  padding: 0;
}

.auth-card {
  width: 500px;
  padding: 48px;
  background: var(--card-bg);
  backdrop-filter: blur(30px);
  border: 1px solid var(--border-light);
  border-radius: 32px;
  box-shadow: var(--shadow-lg);
  position: relative;
  transition: var(--transition);
  min-height: auto;
  height: auto;
  display: flex;
  flex-direction: column;
}

.auth-card:hover {
  transform: translateY(0);
  box-shadow: var(--shadow-hover);
}

.auth-card.card-transition {
  opacity: 0;
  transform: translateY(10px) scale(0.98);
}

/* 卡片顶部的流光条 */
.auth-card::before {
  content: '';
  position: absolute;
  top: -1px;
  left: 20%;
  right: 20%;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(37, 99, 235, 0) 15%,
    #2563eb 30%,
    #da69a9 50%,
    #2563eb 70%,
    rgba(37, 99, 235, 0) 85%,
    transparent 100%
  );
  background-size: 200% auto;
  animation: border-gradient 3s linear infinite;
}

@keyframes border-gradient {
  0% {
    background-position: 0% center;
  }
  100% {
    background-position: 200% center;
  }
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
  color: var(--text-h);
}

.text-group p {
  color: var(--text-light);
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
  color: var(--text-p);
  transition: var(--transition);
  pointer-events: none;
  z-index: 2;
}


:deep(.ant-input-affix-wrapper:hover){
  border-color: transparent !important;
    border-inline-end-width: 1px !important;
    z-index: 1;
}

:deep(.ant-input),
:deep(.ant-input-password) {
  width: 100%;
  height: 48px !important;
  padding: 0 16px 0 48px;
  background:#171B21;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  color: white;
  font-size: 15px !important;
  outline: none;
  transition: var(--transition);
  caret-color: #fff !important;
}

:deep(.ant-input-password .ant-input) {
  height: 46px !important;
  caret-color: #fff !important;
}

:deep(.ant-input::placeholder),
:deep(.ant-input-password input::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.ant-input:focus),
:deep(.ant-input-password:focus),
:deep(.ant-input-password .ant-input:focus) {
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
  height: 48px !important;
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

/* 表单页脚 */
.form-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;

}

/* 自定义复选框 */
.custom-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 13px;
  color: var(--text-light);
  position: relative;
}

.custom-checkbox input {
  display: none;
}

.custom-checkbox .box {
  width: 16px;
  height: 16px;
  border: 1.5px solid #334155;
  border-radius: 4px;
  position: relative;
  transition: var(--transition);
}

.custom-checkbox input:checked + .box {
  background: var(--primary);
  border-color: var(--primary);
}

.custom-checkbox input:checked + .box::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 10px;
  font-weight: bold;
}

.link-dim {
  color: var(--text-light);
  text-decoration: none;
  font-size: 13px;
  transition: var(--transition);
  position: relative;
  padding-bottom: 2px;
}

.link-dim::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--primary);
  transition: var(--transition-fast);
}

.link-dim:hover {
  color: var(--primary-light);
}

.link-dim:hover::after {
  width: 100%;
}

/* 主按钮 */
.prime-btn {
  height: 48px;
  width: 100%;
  // padding: 14px 20px;
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
  margin-top: 8px;
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

/* 其他登录方式 */
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

.social-login-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  // padding: 14px 20px;
  background: rgba(255, 255, 255, 0.08);
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
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

.qq-btn {
  background: rgba(66, 165, 245, 0.15);
  border-color: rgba(66, 165, 245, 0.4);
}

.qq-btn:hover {
  background: rgba(66, 165, 245, 0.25);
  border-color: rgba(66, 165, 245, 0.6);
}

.qq-btn svg,
.qq-btn i {
  color: #42a5f5;
}

/* 微信二维码登录样式 */
.wechat-qr-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 20px 0;
}

.qr-code-wrapper {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 10px;
}

.qr-loading {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.qr-tips {
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
}

.qr-tips p {
  font-size: 14px;
  margin: 4px 0;
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
  color: var(--primary-light);
  margin: 0;
}

.wechat-switch-text a {
  color: var(--primary-light);
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
  background: var(--primary);
  transition: var(--transition-fast);
}

.wechat-switch-text a:hover::after {
  width: 100%;
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

/* 认证面板 */
.auth-panel {
  position: relative;
  transition: var(--transition);
  opacity: 1;
  min-height: auto;
  height: auto;
  display: flex;
  flex-direction: column;
}

/* 玻璃态表单 */
.glass-form {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 版权信息 */
.copyright-footer {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  text-align: center;
}

.copyright-footer p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  margin: 0;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .split-container {
    flex-direction: column;
  }

  .video-panel {
    flex: 1;
    padding: 20px;
  }

  .carousel-container {
    width: 100%;
    margin: 20px 0;
    height: 300px;
  }

  .video-title {
    position: relative;
    bottom: auto;
    left: auto;
    font-size: 32px;
    margin: 20px 0 10px;
  }

  .video-subtitle {
    position: relative;
    bottom: auto;
    left: auto;
    margin: 0 0 20px;
  }

  .brand-wall {
    position: relative;
    bottom: auto;
    left: auto;
    margin: 20px 0;
  }

  .auth-panel-container {
    flex: 1;
    padding: 20px;
  }

  .auth-card {
    width: 100%;
    max-width: 500px;
  }
}

@media (max-width: 480px) {
  .auth-card {
    width: calc(100vw - 40px);
    padding: 32px 24px;
    margin: 0 10px;
  }

  .text-group h2 {
    font-size: 24px;
  }

  .form-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    margin-bottom: 20px;
  }

  .prime-btn {
    padding: 14px;
  }
}
</style>

<style lang="less">
@prefix-cls: ~'@{namespace}-mini-login';

:root {
  --primary: #2563eb;
  --primary-light: #60a5fa;
  --primary-dark: #1d4ed8;
  --bg-color: #0b0e14;
  --card-bg: rgba(17, 21, 28, 0.7);
  --input-bg: rgba(255, 255, 255, 0.03);
  --text-p: #94a3b8;
  --text-h: #f8fafc;
  --text-light: #cbd5e1;
  --border-light: rgba(255, 255, 255, 0.1);
  --transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  --transition-fast: all 0.2s ease;
  --shadow-lg: 0 25px 50px -12px rgba(0, 0, 0, 0.7);
  --shadow-hover: 0 30px 60px -10px rgba(0, 0, 0, 0.8);
}

/* 确保登录页面背景色 */
.@{prefix-cls}.new-login-container {
  background-color: #0b0e14 !important;
}

/* 确保 body 和 html 背景色 - 覆盖 base.less 中的样式 */
html,
body {
  background-color: #0b0e14 !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* 确保登录容器覆盖整个页面 */
.@{prefix-cls}.new-login-container {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100% !important;
  height: 100vh !important;
  background-color: #0b0e14 !important;
  z-index: 1;
}

/* 覆盖 base.less 中的 body 背景色 */
body.login-page,
body[class*='login'] {
  background-color: #0b0e14 !important;
  background: #0b0e14 !important;
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



:deep(.ant-input-password:focus-within .ant-input-password-icon) {
  color: var(--primary);
}

.input-password .ant-input{
  background-color: #171B21 !important;
    opacity: 0.9;
}
</style>