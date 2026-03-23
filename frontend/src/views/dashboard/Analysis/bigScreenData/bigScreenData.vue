<template>
  <div class="bigScreenData">
    <a-row :gutter="12">
      <a-col :span="24">
        <div class="currencyTime">
          <div class="title">
            <div class="title-left"></div>
            <div class="title-right">时区时间</div>
          </div>
          <div class="time-box">
            <div v-for="zone in timeZones" :key="zone.key" class="time-item">
              <div class="time-name">
                {{ zone.name }}<img :src="zone.flag" alt="" />
              </div>
              <div class="time-day">
                {{ formatDate(zone.time) }}
              </div>
              <div class="time-date">
                <div class="hour">{{ formatTime(zone.time, 'hour') }}</div>
                :
                <div class="minute">{{ formatTime(zone.time, 'minute') }}</div>
                :
                <div class="second">{{ formatTime(zone.time, 'second') }}</div>
              </div>
            </div>
          </div>
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="12">
      <a-col :xl="8" :lg="24" :md="24" :sm="24">
        <div class="exchangeRate">
          <div class="title" style="min-width: 66px; width: 90px; max-width: 110px">
            <div class="title-left"></div>
            <div class="title-right">实时汇率</div>
          </div>
          <div class="page-example3">
            <vue3-seamless-scroll :list="exchangeGroups" :options="scrollOption" class="seamless-warp">
              <div v-for="(group, index) in exchangeGroups" :key="index" class="exchangeRateRow">
                <div v-for="item in group" :key="item.id" class="exchangeRateItem">
                  1{{ item.typeName }}≈{{ formatExchangeRate(item.exchangeRate) }}人民币
                </div>
              </div>
            </vue3-seamless-scroll>
          </div>
        </div>
        <div class="kol_bg"></div>
        <div class="payPalRatebox">
          <div class="title">
            <div class="title-left"></div>
            <div class="title-right">PayPal手续费</div>
          </div>
          <div v-if="loadingPaypal" class="loading">
            <a-spin style="margin-top: 100px" />
          </div>
          <div v-else class="payPalRate">
            <div style="display: flex">
              <a-input-group compact>
                <a-select
                  v-model:value="currentCountry"
                  class="payPalRateSelect"
                  style="width: 130px"
                  @change="handleCountryChange"
                >
                  <a-select-option
                    v-for="item in countryList"
                    :key="item.id"
                    :value="item.country"
                  >
                    <img style="height: 20px" :src="item.img" alt="" />
                    <span> {{ item.country }}</span>
                  </a-select-option>
                </a-select>
                <a-input
                  v-model:value="currentPrice"
                  placeholder="请输入需要计算的金额"
                  style="width: calc(100% - 130px)"
                  :suffix="currentRate.shortCode"
                  :maxLength="9"
                  @input="handlePriceInput"
                />
              </a-input-group>
              <div class="button-group">
                <a-button type="primary" @click="calculateFees">一键计算</a-button>
                <a-button @click="resetCalculation">一键重置</a-button>
              </div>
            </div>

            <div class="result_box">
              <div class="result_item">
                <div class="result_title">
                  若要收到 {{ currentPrice }}{{ currentRate.currencyTypeName }} 则付款人应转出
                </div>
                <div class="result_num">
                  {{ currentRate.shortCode }} {{ currentRate.currencyTypeName }}
                  <span class="price">{{ transferOut }}</span>
                  <a v-copy="transferOut">
                    <CopyOutlined />
                    复制
                  </a>
                </div>
                <div class="result_info">
                  其中，本金 {{ currentRate.shortCode }} {{ currentPrice }}，手续费：
                  <a>{{ currentRate.shortCode }} {{ getHandlingFee(transferOut, 1) }}</a>
                  <br />
                  当前费率 {{ currentRate.country }} PayPal官方
                  <a>{{ currentRate.paypalRate }}% + {{ currentRate.paypalBaseFees }}</a>
                </div>
              </div>
              <div class="result_item">
                <div class="result_title">
                  若要转出 {{ currentPrice }}{{ currentRate.currencyTypeName }} 则收款人会收到
                </div>
                <div class="result_num">
                  {{ currentRate.shortCode }} {{ currentRate.currencyTypeName }}
                  <span class="price">{{ receive }}</span>
                  <a v-copy="receive">
                    <CopyOutlined />
                    复制
                  </a>
                </div>
                <div class="result_info">
                  其中，本金 {{ currentRate.shortCode }} {{ currentPrice }}，手续费：
                  <a>{{ currentRate.shortCode }} {{ getHandlingFee(receive, 2) }}</a>
                  <br />
                  当前费率 {{ currentRate.country }} PayPal官方
                  <a>{{ currentRate.paypalRate }}% + {{ currentRate.paypalBaseFees }}</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </a-col>
      <a-col style="margin-top: 10px" :xl="16" :lg="24" :md="24" :sm="24">
        <calendar></calendar>
      </a-col>
    </a-row>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { CopyOutlined } from '@ant-design/icons-vue'
import { Vue3SeamlessScroll } from "vue3-seamless-scroll";
import dayjs from 'dayjs'
import { defHttp } from '@/utils/http/axios'
import calendar from './components/calendar.vue'

// 时区配置
const TIME_ZONES = [
  { key: 'zg', name: '中国', offset: 8, flag: new URL('@/assets/currency/CNY.jpg', import.meta.url).href },
  { key: 'mg', name: '美国', offset: -7, flag: new URL('@/assets/currency/USD.jpg', import.meta.url).href },
  { key: 'yg', name: '英国', offset: 1, flag: new URL('@/assets/currency/GBP.jpg', import.meta.url).href },
  { key: 'fg', name: '法国', offset: 2, flag: new URL('@/assets/currency/FG.jpg', import.meta.url).href },
  { key: 'rb', name: '日本', offset: 9, flag: new URL('@/assets/currency/JPY.jpg', import.meta.url).href },
  { key: 'adly', name: '澳大利亚', offset: 10, flag: new URL('@/assets/currency/AUD.jpg', import.meta.url).href }
]
 

// 国家图片映射
  const COUNTRY_IMAGES = {
    美国: new URL('@/assets/currency/USD.jpg', import.meta.url).href,
    日本: new URL('@/assets/currency/JPY.jpg', import.meta.url).href,
    加拿大: new URL('@/assets/currency/CAD.jpg', import.meta.url).href,
    英国: new URL('@/assets/currency/GBP.jpg', import.meta.url).href,
    德国: new URL('@/assets/currency/DG.png', import.meta.url).href,
    西班牙: new URL('@/assets/currency/XBY.png', import.meta.url).href,
    意大利: new URL('@/assets/currency/YDL.png', import.meta.url).href,
    法国: new URL('@/assets/currency/FG.jpg', import.meta.url).href
  }

// 响应式数据
const currentTime = ref(new Date())
const allExchangeRates = ref([])
const loadingPaypal = ref(true)
const countryList = ref([])
const currentCountry = ref('')
const currentPrice = ref('100')
const transferOut = ref('0.00')
const receive = ref('0.00')

// 当前汇率信息
const currentRate = computed(() => {
  return countryList.value.find(item => item.country === currentCountry.value) || {}
})

// 时区时间计算
const timeZones = computed(() => {
  return TIME_ZONES.map(zone => ({
    ...zone,
    time: getTimeByOffset(currentTime.value, zone.offset)
  }))
})

// 汇率分组显示
const exchangeGroups = computed(() => {
  const groups = []
  for (let i = 0; i < allExchangeRates.value.length; i += 3) {
    groups.push(allExchangeRates.value.slice(i, i + 3))
  }
  return groups
})

// 滚动配置
const scrollOption = computed(() => ({
  step: 1,
  limitMoveNum: 2,
  hoverStop: true,
  direction: 1,
  openWatch: true,
  singleHeight: 51,
  singleWidth: 0,
  waitTime: 2000
}))

// 时间格式化函数
const getTimeByOffset = (date: Date, offset: number): string => {
  const utc = date.getTime() + (date.getTimezoneOffset() * 60000)
  const targetTime = new Date(utc + (offset * 3600000))
  return dayjs(targetTime).format('YYYY-MM-DD HH:mm:ss')
}

const formatDate = (timeStr: string): string => {
  return timeStr.substring(0, 10)
}

const formatTime = (timeStr: string, type: 'hour' | 'minute' | 'second'): string => {
  const timeMap = {
    hour: timeStr.substring(11, 13),
    minute: timeStr.substring(14, 16),
    second: timeStr.substring(17, 19)
  }
  return timeMap[type] || '00'
}

const formatExchangeRate = (rate: number): string => {
  return (rate * 0.01).toFixed(4)
}

// PayPal 手续费计算
const calculateTransferOut = (): void => {
  const price = parseFloat(currentPrice.value) || 0
  const rate = parseFloat(currentRate.value.paypalRate) || 0
  const baseFee = parseFloat(currentRate.value.paypalBaseFees) || 0
  
  transferOut.value = (price + ((price + baseFee) / (100 - rate)) * rate + baseFee).toFixed(2)
}

const calculateReceive = (): void => {
  const price = parseFloat(currentPrice.value) || 0
  const rate = parseFloat(currentRate.value.paypalRate) || 0
  const baseFee = parseFloat(currentRate.value.paypalBaseFees) || 0
  
  receive.value = (price - (price / 100) * rate - baseFee).toFixed(2)
}

const getHandlingFee = (amount: string, type: number): string => {
  const num = parseFloat(amount)
  const price = parseFloat(currentPrice.value)
  
  if (type === 1) {
    return (num - price).toFixed(2)
  } else {
    return (price - num).toFixed(2)
  }
}

// 价格输入处理
const handlePriceInput = (): void => {
  let value = currentPrice.value.toString()
  
  // 限制最大值
  if (parseFloat(value) > 999999999) {
    value = '999999999'
  }
  
  // 去除空格和非数字字符
  value = value.replace(/(^\s*)|(\s*$)/g, '').replace(/[^\d.]/g, '')
  
  // 确保第一位不是小数点
  value = value.replace(/^\./g, '')
  
  // 只允许一个小数点
  value = value.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.')
  
  // 限制小数点后两位
  value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')
  
  currentPrice.value = value
  calculateFees()
}

const calculateFees = (): void => {
  calculateTransferOut()
  calculateReceive()
}

const handleCountryChange = (value: string): void => {
  currentCountry.value = value
  calculateFees()
}

const resetCalculation = (): void => {
  currentPrice.value = '100'
  if (countryList.value.length > 0) {
    currentCountry.value = countryList.value[0].country
  }
  calculateFees()
}

// API 调用
const fetchCountryRates = async (): Promise<void> => {
  try {
    const res = await defHttp.get({ url: '/storecountry/storeCountry/getCountryList' })
    countryList.value = res.map(item => ({
      ...item,
      img: COUNTRY_IMAGES[item.country] || ''
    }))
    
    if (countryList.value.length > 0) {
      currentCountry.value = countryList.value[0].country
      calculateFees()
    }
    loadingPaypal.value = false
  } catch (error) {
    console.error('获取国家汇率失败:', error)
    loadingPaypal.value = false
  }
}

const fetchExchangeRates = async (): Promise<void> => {
  try {
    const res = await defHttp.get({ url: '/forexrates/list', params: { isUse: 1 } })
    console.log(res)
    if (res) {
      allExchangeRates.value = res || []
      console.log(allExchangeRates.value)
    }
  } catch (error) {
    console.error('获取汇率数据失败:', error)
  }
}

// 定时器
let timer: NodeJS.Timeout | null = null

const startTimer = (): void => {
  timer = setInterval(() => {
    currentTime.value = new Date()
  }, 1000)
}

const stopTimer = (): void => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

// 生命周期
onMounted(() => {
})
fetchCountryRates()
fetchExchangeRates()
startTimer()

onUnmounted(() => {
  stopTimer()
})
</script>
<style lang="less" scoped>
/deep/ .bigScreenData .ant-input {
  color: #000 !important;
}
.bigScreenData {
  padding: 10px;
  height: calc(100vh - 101px);
  width: 100% !important;
  .title {
    display: flex;
    align-items: center;
    .title-left {
      width: 4px;
      background: @primary-color;
      height: 18px;
      margin-right: 4px;
      border-radius: 4px;
    }
  }
  .currencyTime {
    height: 130px;
    background: #fff;
    overflow: hidden;
    padding: 10px;
    .time-box {
      display: flex;

      .time-item {
        width: 16.7%;
        text-align: center;

        .time-name {
          display: flex;
          align-items: center;
          justify-content: center;

          img {
            margin-left: 15px;
            width: 24px;
            box-shadow: 0 0 10px 0 #ccc;
          }
        }

        .time-day {
          margin: 7px 0;
        }

        .time-date {
          display: flex;
          justify-content: center;
          align-items: center;
          font-size: 20px;

          div {
            width: 30px;
            height: 30px;
            background: linear-gradient(#525252, #000);
            margin: 0 5px;
            border-radius: 5px;
            color: #fff;
            font-size: 20px;
            line-height: 30px;
          }

          .second {
            color: #5d5e5e;
            background: linear-gradient(#ececec, #d1d1d1);
          }
        }
      }
    }
  }
  .exchangeRate {
    height: 51px;
    background: #fff;
    margin-top: 10px;
    color: #898989;
    line-height: 51px;
    padding: 0 10px;
    display: flex;
    overflow: hidden;

    p {
      min-width: 110px;
    }

    .exchangeRateRow {
      display: flex;
      justify-content: space-between;
    }

    .exchangeRateItem {
      // margin-left: 10px;
      // min-width: 130px;
    }
  }
  .kol_bg {
    margin-top: 10px;
    background-image: url("./image/分组 3.png");
    background-size: 100% 100%;
    background-repeat: no-repeat;
    height: 300px;
    width: 100%;
    max-width: 642px;
  }
  .payPalRatebox {
    margin-top: 10px;
    background: #fff;
    padding: 10px;
    height: calc(100vh - 101px - 130px - 10px - 51px - 10px - 300px - 10px);
    min-height: 200px;
  }
  .payPalRate {
    margin-top: 40px;

    .button-group {
      margin-left: 10px;
      width: 240px;
      display: flex;
      justify-content: space-between;
    }

    .result_box {
      display: flex;
      gap: 10px;
      margin-top: 20px;
      
      .result_item {
        width: 50%;
        color: #8d93a0;
        text-align: center;
        padding: 20px 0;
        border-radius: 5px;

        .result_title {
          background-color: #f8f9fa;
          height: 40px;
          text-align: center;
          line-height: 40px;
          color: #000;
          font-size: 12px;
        }

        .result_num {
          color: #000;
          font-size: 12px;
          margin: 10px;

          .price {
            color: @primary-color;
            font-size: 20px;
          }
        }

        .result_info {
          font-size: 12px;
          line-height: 1.5;
        }
      }
    }
  }

  /deep/ .payPalRateSelect .ant-select-selection-selected-value {
    display: flex !important;
    align-items: center !important;
    gap: 4px !important;
  }
}
</style>
