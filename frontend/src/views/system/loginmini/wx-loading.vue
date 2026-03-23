<template>
  <div  class="new-login-container">
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
          <div class="video-subtitle">
            连接全球 30W+ 优质创作者，以数据驱动的创意内容，为 DTC 品牌构建从曝光到转化的完整增长闭环
          </div>
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
            <!-- Loading 状态 -->
            <section v-if="pageLoading" class="auth-panel loading-panel">
              <div class="loading-content">
                <a-spin size="large" />
                <p class="loading-text">正在验证微信授权...</p>
              </div>
            </section>

            <!-- 登录面板 -->
            <section v-if="!pageLoading && type === 'login'" class="auth-panel">
              <div class="text-group">
                <img src="/@/assets/images/login_logo.png" alt="Logo" class="login-logo" />
                <h2>微信注册</h2>
              </div>

              <!-- 账号密码登录表单 -->
              <a-form v-show="loginType === 'account'" :model="formData" class="glass-form" @keyup.enter="handleSubmit">
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

                <div class="input-wrapper" >
                  <LockOutlined class="input-icon" />
                  <a-input-password
            
                    v-model:value="formData.password"
                    class="fix-auto-fill"
                    placeholder="密码"
                    
                  >
                    
                  </a-input-password>
                  <!-- <div class="input-glow"></div> -->
                </div>

          

           

                <a-button  class="prime-btn" type="primary" size="large" @click="handleSubmit">
                  <span>注册</span>
                </a-button>
              </a-form>

          
            </section>

          </div>
        </main>
      </div>
    </div>

  </div>
</template>

<script lang="ts" setup name="login-mini">
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { onMounted, onUnmounted, ref } from 'vue';
import { useUserStore } from '/@/store/modules/user';
import { useMessage } from '/@/hooks/web/useMessage';
import { useI18n } from '/@/hooks/web/useI18n';
import { useRoute } from 'vue-router';
const route = useRoute();
import { defHttp } from '/@/utils/http/axios';
const { createMessage } = useMessage();
const userStore = useUserStore();
const { t } = useI18n();

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


const type = ref<string>('login');
const loginType = ref<string>('account'); // 'account' | 'wechat'

const formData = ref<any>({

  username: '',
  password: '',
});

const timer = ref<any>(null);
const pageLoading = ref<boolean>(true); // 页面加载状态


const userInfo = ref<any>({})

async function handleSubmit() {
  console.log(formData.value);
  if (!formData.value.username) {
    createMessage.warn('请输入用户名')
    return
  }
  // 用户名不能包含特殊符号或表情，仅允许中英文、数字和下划线
  // 用户名不能包含特殊符号，如@￥%等
  const invalidUsernamePattern = /[@￥%#\^\*\(\)\[\]\{\}\|\\\/\?\.,;:'"!`~<>\$&=+]/;
  if (invalidUsernamePattern.test(formData.value.username)) {
    createMessage.warn('用户名格式不正确');
    return;
  }
  if (!formData.value.password) {
    createMessage.warn('请输入密码')
    return
  }
  const res = await checkUsername()
  if (!res) {
    createMessage.warn('用户已存在');
    return;
  }
  // if (formData.value.password !== formData.value.password2) {
  //   createMessage.warn('两次输入的密码不一致')
  //   return
  // }
  defHttp.post({ url: '/sys/user/wxRegister', data: formData.value }, { isTransformResponse: false }).then(res => {
    console.log(res)
    if (res.success) {
      userStore.qrCodeLogin(res.result.token);
    
    } else {
      createMessage.warn(res.message)
    }
  })
  .catch(err => {
    console.log(err)
  })
}


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
 * 检查用户名
 */
 async function checkUsername(){
  const res = await defHttp.get({ url: '/sys/user/checkOnlyUser', params: { username: formData.value.username } },{isTransformResponse:false})
  return res.success
}



/**
 * 确认密码检查
 */
function handlePasswordCheck(_rule: any, value: string) {
  if (!value) {
    return Promise.reject('请确认密码');
  }
  if (!formData.value.password) {
    return Promise.reject('请先输入密码');
  }
  if (value !== formData.value.password) {
    return Promise.reject('两次输入的密码不一致');
  }
  return Promise.resolve();
}



/**
 * 获取微信登录的code
 */
async function getCode() {
  const query = route.query;
  if (Object.keys(query).length > 0) {
    pageLoading.value = true; // 开始加载
    const code = decodeURIComponent(route.fullPath).split('code=')[1].split('&state=')[0];
    try {
      const res = await defHttp.get({ url: '/sys/wxLogin', params: { code } }, { isTransformResponse: false });
      console.log(res)
      if (res.code == 500) {
        // 可以在这里处理错误
        createMessage.error(res.message || '微信授权失败');
        pageLoading.value = false;
        return;
      }
      if (res.success) {
        let nickname = res.result.userInfo.nickname;
        const regStr =
          /[\uD83C|\uD83D|\uD83E][\uDC00-\uDFFF][\u200D|\uFE0F]|[\uD83C|\uD83D|\uD83E][\uDC00-\uDFFF]|[0-9|*|#]\uFE0F\u20E3|[0-9|#]\u20E3|[\u203C-\u3299]\uFE0F\u200D|[\u203C-\u3299]\uFE0F|[\u2122-\u2B55]|\u303D|[\u00A9|\u00AE]\u3030|\u00A9|\u00AE|\u3030/gi;
        if (regStr.test(nickname)) {
          nickname = nickname.replace(regStr, '');
        }
        if (!res.result.token ) {
          // 需要注册，显示注册表单
          formData.value.username = nickname;
          console.log(formData.value)
          formData.value.openId = res.result.userInfo.openId,
          formData.value.unionId = res.result.userInfo.unionId,
          formData.value.sex = res.result.userInfo.sex,
          formData.value.nickname = res.result.userInfo.nickname,
          formData.value.avatar = res.result.userInfo.avatar,
       
          pageLoading.value = false; // 显示注册表单
        } else {
          // 已注册，直接登录
          userStore.qrCodeLogin(res.result.token);
          // setAuthCache(TOKEN_KEY, res.result.token);
          // setAuthCache(USER_INFO_KEY, res.result.userInfo);
          // loginSuccess();
          // 登录成功后保持 loading 状态，等待跳转
        }
      } else {
        createMessage.error(res.message || '微信授权失败');
        pageLoading.value = false;
      }
    } catch (error) {
      console.error('getCode error:', error);
      createMessage.error('微信授权验证失败，请重试');
      pageLoading.value = false;
    }
  } else {
    // 没有 code 参数，直接显示表单
    pageLoading.value = false;
  }
}



onMounted(() => {
  // 设置页面背景色
  document.body.style.backgroundColor = '#0b0e14';
  document.documentElement.style.backgroundColor = '#0b0e14';
 
  startCarousel();
  
  // 检查URL参数，如果有微信授权码，自动验证
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  if (code) {
    getCode();
  } else {
    // 没有 code 参数，直接显示表单
    pageLoading.value = false;
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
  text-align: left;
  margin-top: 30px;
  margin-bottom: 16px;
  background: linear-gradient(90deg, #61a4fa, #f273ba, #61a4fa);
  background-size: 200% auto;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 56px;
  font-weight: 800;

  z-index: 10;
  white-space: nowrap;
  animation: gradient-shift 3s ease-in-out infinite;
  width: 100%;
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

:deep(.ant-input),
:deep(.ant-input-password) {
  width: 100%;
  height: 48px !important;
  padding: 0 16px 0 48px;
  background:#171B21;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  color: white;
  font-size: 15px;
  outline: none;
  transition: var(--transition);
}

:deep(.ant-input-password .ant-input) {
  height: 46px !important;
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.08);
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  outline: none;
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

/* Loading 面板 */
.loading-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.loading-text {
  color: var(--text-light);
  font-size: 16px;
  margin: 0;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
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

.input-password .ant-input{
  background-color: #171B21 !important;
    opacity: 0.9;
}

/* 密码强度样式 */
.user-register {
  font-size: 14px;
  margin-bottom: 8px;
}

.user-register-low {
  color: #ff4d4f;
}

.user-register-medium {
  color: #faad14;
}

.user-register-strong {
  color: #52c41a;
}

/* 注册表单样式调整 */
:deep(.ant-form-item) {
  margin-bottom: 16px;
}

:deep(.ant-popover-inner-content) {
  padding: 16px;
}

:deep(.ant-progress-line) {
  margin-bottom: 0;
}
</style>