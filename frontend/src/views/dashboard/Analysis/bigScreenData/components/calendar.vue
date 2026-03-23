<template>
  <div class="calendar-container">
    <div class="calendar-header">
      <div class="header-left">
        <div class="title">
          <div class="title-left"></div>
          <div class="title-right">跨境日历</div>
        </div>
        <div class="filter-section">
          <a-checkbox
            :indeterminate="indeterminate"
            :checked="checkAll"
            @change="handleCheckAllChange"
          >
            全部
          </a-checkbox>
          <a-checkbox-group
            v-model:value="checkedRegions"
            :options="regionOptions"
            @change="handleRegionChange"
          />
        </div>
      </div>
      <div class="header-right">
        <a class="nav-button" @click="prevMonth">
          <LeftOutlined />
        </a>
        <div class="date-display">{{ currentYear }}年{{ currentMonth }}月</div>
        <a class="nav-button" @click="nextMonth">
          <RightOutlined />
        </a>
        <div class="today-button" @click="goToToday">
          今天
        </div>
      </div>
    </div>
    <div class="calendar">
      <!-- 周标题 -->
      <div class="week-header">
        <div v-for="day in weekDays" :key="day" class="day">{{ day }}</div>
      </div>
      <!-- 日历网格 -->
      <div class="calendar-grid">
        <div
          v-for="(day, index) in calendarDays"
          :key="index"
          :class="{ today: isToday(day.date), 'other-month': !day.isCurrentMonth }"
          class="calendar-cell"
        >
          <div class="date-info">
            <div class="date">{{ day.date.getDate() }}</div>
            <div class="lunar">{{ day.lunar }}</div>
          </div>
          <div v-for="activity in day.activities" :key="activity.id || activity.date">
            <a-popover
              :overlay-style="{ maxWidth: '300px' }"
              overlay-class-name="calendar-popover"
              trigger="click"
              placement="rightTop"
            >
              <template #content>
                <div class="popover-content">
                  <div class="popover-title">
                    <div class="popover-name">{{ activity.festival }}</div>
                    <div class="popover-tags">
                      <a-tag
                        v-for="country in activity.country_list"
                        :key="country.id"
                        :color="getRegionColor(country.name)"
                      >
                        {{ country.name }}
                      </a-tag>
                    </div>
                  </div>
                  <a-divider />
                  <div class="popover-section">
                    <ClockCircleOutlined class="section-icon" />
                    <span>{{ formatDate(activity.date) }}</span>
                  </div>
                  <a-divider />
                  <div class="popover-section">
                    <ArrowRightOutlined class="section-icon" />
                    <span>热销产品</span>
                  </div>
                  <div class="popover-text">{{ activity.hot_sale_products }}</div>
                  <a-divider />
                  <div class="popover-section">
                    <ArrowRightOutlined class="section-icon" />
                    <span>小贴士</span>
                  </div>
                  <div class="popover-text">{{ activity.tips }}</div>
                </div>
              </template>
              <div class="event" :style="{ backgroundColor: getRegionColor() }">
                {{ activity.festival }}
              </div>
            </a-popover>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { LeftOutlined, RightOutlined, ClockCircleOutlined, ArrowRightOutlined } from '@ant-design/icons-vue'
// @ts-ignore
import LunarCalendar from 'lunar-calendar'
// @ts-ignore
import { defHttp } from '@/utils/http/axios'
// @ts-ignore
import dayjs from 'dayjs'

// 类型定义
interface CalendarDay {
  date: Date
  lunar: string
  isCurrentMonth: boolean
}

interface Activity {
  id: string
  date: number
  festival: string
  country_list: Array<{ id: string; name: string }>
  hot_sale_products: string
  tips: string
}

// 常量定义
const WEEK_DAYS = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
const REGION_OPTIONS = ['欧洲', '中东', '印度', '美国', '日本', '东南亚', '澳洲', '墨西哥']
const REGION_COLORS = [
  { name: '全部', color: '#008DF4' },
  { name: '美国', color: '#9cbdf2' },
  { name: '日本', color: '#ffeb9b' },
  { name: '欧洲', color: '#aea0f3' },
  { name: '中东', color: '#F6A3CA' },
  { name: '印度', color: '#CAECC7' },
  { name: '澳洲', color: '#ffc49b' },
  { name: '东南亚', color: '#00BFB3' },
  { name: '墨西哥', color: '#2dd7eb' },
  { name: '其他', color: '#2dd7eb' }
]

// 响应式数据
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)
const activities = ref<Activity[]>([])
const calendarGrid = ref<CalendarDay[]>([])
const checkedRegions = ref([...REGION_OPTIONS])
const indeterminate = ref(false)
const checkAll = ref(true)

// 计算属性
const weekDays = computed(() => WEEK_DAYS)
const regionOptions = computed(() => REGION_OPTIONS)

const calendarDays = computed(() => {
  return calendarGrid.value.map(day => ({
    ...day,
    activities: getActivitiesForDate(day.date)
  }))
})

// 工具函数
const isToday = (date: Date): boolean => {
  const today = new Date()
  return (
    date.getDate() === today.getDate() &&
    date.getMonth() === today.getMonth() &&
    date.getFullYear() === today.getFullYear()
  )
}

const formatDate = (timestamp: number): string => {
  return timestamp ? dayjs.unix(timestamp).format('YYYY-MM-DD') : '--'
}

const getLunarDate = (date: Date): string => {
  try {
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const lunar = LunarCalendar.solarToLunar(year, month, day)
    return lunar.lunarMonthName + lunar.lunarDayName
  } catch (error) {
    console.error('获取农历日期失败:', error)
    return ''
  }
}

const getRegionColor = (regionName?: string): string => {
  if (!regionName) {
    return REGION_COLORS[Math.floor(Math.random() * REGION_COLORS.length)].color
  }
  const region = REGION_COLORS.find(r => r.name === regionName)
  return region ? region.color : REGION_COLORS[REGION_COLORS.length - 1].color
}

// 日历生成
const generateCalendarDates = (): Date[] => {
  const firstDayOfMonth = new Date(currentYear.value, currentMonth.value - 1, 1)
  const firstDayOfWeek = firstDayOfMonth.getDay()
  
  const startDate = new Date(firstDayOfMonth)
  startDate.setDate(firstDayOfMonth.getDate() - firstDayOfWeek)
  
  const dates: Date[] = []
  for (let i = 0; i < 42; i++) {
    dates.push(new Date(startDate))
    startDate.setDate(startDate.getDate() + 1)
  }
  return dates
}

const initCalendar = (): void => {
  const dates = generateCalendarDates()
  calendarGrid.value = dates.map(date => ({
    date,
    lunar: getLunarDate(date),
    isCurrentMonth: date.getMonth() + 1 === currentMonth.value && date.getFullYear() === currentYear.value
  }))
}

// 活动数据处理
const getActivitiesForDate = (date: Date): Activity[] => {
  if (!activities.value || !Array.isArray(activities.value)) {
    return []
  }
  
  try {
    const targetTimestamp = Math.floor(date.getTime() / 1000)
    // 处理 country_list 为对象
    return activities.value
      .filter(item => {
        let countryList: { id: string; name: string }[] = []
        try {
          countryList = typeof item.country_list === 'string'
            ? JSON.parse(item.country_list)
            : item.country_list
        } catch (e) {
          countryList = []
        }

        return (
          item.date === targetTimestamp &&
          (countryList.length === 0 ||
            countryList.some(country =>
              checkedRegions.value.includes(country.name)
            ))
        )
      })
      .map(item => {
        // 保证返回时 item.country_list 是 JSON 对象
        let countryList: { id: string; name: string }[] = []
        try {
          countryList = typeof item.country_list === 'string'
            ? JSON.parse(item.country_list)
            : item.country_list
        } catch (e) {
          countryList = []
        }
        return {
          ...item,
          country_list: countryList
        }
      })
  } catch (error) {
    console.error('解析活动数据时出错:', error)
    return []
  }
}

const loadActivityData = async (): Promise<void> => {
  try {
    const dates = generateCalendarDates()
    const startTime = Math.floor(dates[0].getTime() / 1000)
    const endTime = Math.floor(dates[dates.length - 1].getTime() / 1000)
    
    const res = await defHttp.post({ url: '/employeeSecurity/calendar/front_list', params: {
      country_list: [],
      start_time: startTime,
      end_time: endTime
    } })
    console.log(res)
    activities.value = res || []
  } catch (error) {
    console.error('加载活动数据失败:', error)
  }
}

// 事件处理
const prevMonth = (): void => {
  currentMonth.value--
  if (currentMonth.value < 1) {
    currentMonth.value = 12
    currentYear.value--
  }
  initCalendar()
  loadActivityData()
}

const nextMonth = (): void => {
  currentMonth.value++
  if (currentMonth.value > 12) {
    currentMonth.value = 1
    currentYear.value++
  }
  initCalendar()
  loadActivityData()
}

const goToToday = (): void => {
  const today = new Date()
  currentYear.value = today.getFullYear()
  currentMonth.value = today.getMonth() + 1
  initCalendar()
  loadActivityData()
}

const handleCheckAllChange = (e: any): void => {
  const checked = e.target.checked
  checkedRegions.value = checked ? [...REGION_OPTIONS] : []
  indeterminate.value = false
  checkAll.value = checked
}

const handleRegionChange = (checkedList: string[]): void => {
  indeterminate.value = !!checkedList.length && checkedList.length < REGION_OPTIONS.length
  checkAll.value = checkedList.length === REGION_OPTIONS.length
}

// 监听地区选择变化，重新计算活动显示
watch(checkedRegions, () => {
  // 当地区选择变化时，日历会自动重新计算显示的活动
}, { deep: true })

// 生命周期
onMounted(() => {
  initCalendar()
  loadActivityData()
})

// 定义组件名称
defineOptions({
  name: 'CalendarComponent'
})
</script>

<style lang="less" scoped>
.calendar-container {
  padding: 10px;
  background: #fff;
}

.calendar-header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.header-left {
  display: flex;
  align-items: center;
  
  .title {
    display: flex;
    align-items: center;
    margin-right: 20px;
    
    .title-left {
      width: 4px;
      background: @primary-color;
      height: 18px;
      margin-right: 4px;
      border-radius: 4px;
    }
  }
  
  .filter-section {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .nav-button {
    color: #0000005c;
    font-size: 14px;
    display: flex;
    justify-content: center;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
    transition: all 0.2s;
    
    &:hover {
      background-color: #f0f0f0;
      color: @primary-color;
    }
  }
  
  .date-display {
    font-size: 14px;
    padding: 0 20px;
    font-weight: 500;
  }
  
  .today-button {
    height: 24px;
    width: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    // background-color: #0000000f;
    border: none;
    border-radius: 4px;
    color: #0000005c;
    font-size: 14px;
    cursor: pointer;
    margin-left: 12px;
    transition: all 0.2s;
    
    // &:hover {
    //   background-color: @primary-color;
    //   color: #fff;
    // }
  }
}

.calendar {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  font-family: Arial, sans-serif;
}

.week-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background-color: #f9f9f9;
  
  .day {
    padding: 10px;
    text-align: center;
    font-weight: bold;
    color: #333;
    border-right: 1px solid #ddd;
    background-color: #f9f9f9;
    box-sizing: border-box;
    
    &:last-child {
      border-right: none;
    }
  }
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background-color: #ddd;
  width: 100%;
  height: calc(100vh - 101px - 130px - 20px - 33px - 64px);
  overflow-y: auto;
}

.calendar-cell {
  padding: 8px;
  background-color: #fff;
  height: 120px;
  position: relative;
  box-sizing: border-box;
  overflow: hidden;
  overflow-y: auto;
  
  &.today {
    background-color: #edf0fc;
  }
  
  &.other-month {
    opacity: 0.6;
  }
  
  .date-info {
    display: flex;
    gap: 8px;
    margin-bottom: 4px;
    
    .date {
      font-size: 14px;
      font-weight: bold;
      color: #333;
    }
    
    .lunar {
      font-size: 12px;
      color: #888;
    }
  }
  
  .event {
    margin-top: 2px;
    padding: 2px 4px;
    border-radius: 3px;
    font-size: 12px;
    line-height: 1.2;
    color: #000;
    cursor: pointer;
    word-wrap: break-word;
    word-break: break-word;
    white-space: normal;
    overflow-wrap: break-word;
    transition: all 0.2s;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }
}

// Popover 样式
.popover-content {
  .popover-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .popover-name {
      max-width: 150px;
      font-size: 14px;
      font-weight: 600;
    }
    
    .popover-tags {
      margin-left: 8px;
      display: flex;
      flex-grow: 1;
      flex-wrap: wrap;
      justify-content: flex-end;
      gap: 4px;
    }
  }
  
  .popover-section {
    display: flex;
    align-items: center;
    color: #52525b;
    font-size: 14px;
    
    .section-icon {
      height: 16px;
      width: 16px;
      margin-right: 6px;
    }
  }
  
  .popover-text {
    padding-left: 22px;
    color: #666;
    line-height: 1.4;
  }
}

:deep(.ant-divider-horizontal) {
  margin: 8px 0 !important;
}

:deep(.calendar-popover .ant-popover-inner-content) {
  max-width: 300px !important;
}
</style>
