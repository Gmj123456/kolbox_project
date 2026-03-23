<template>
  <div class="bigScreenData">
    <!-- 三行布局 -->
    <div class="dashboard-rows">
      <!-- 第一行：标题+特色卡片 | 用户卡片 -->
      <div class="row row-1">
        <div class="row-1-left">
          <h1 class="main-title">让中国品牌，在世界发声</h1>
          <p class="subtitle">
            红人·全案·共创三大板块紧密协同，助力出海品牌打通种草、沉淀、裂变全链路，实现品效合一的高速增长！
          </p>
          <div class="feature-cards">
            <div class="feature-card">
              <div class="feature-icon">
                <!-- <img src="@/assets/images/地球-透明.png" alt="" /> -->
                <span
                          style="font-size: 36px;color:#3155ED;margin-right: 4px;"
                          class="icon iconfont icon-yingxiaoguanli"
                        ></span>
              </div>
              <div class="feature-text">
                <div class="feature-title">红人营销全域覆盖</div>
                <div class="feature-desc">整合三大平台资源，筛选种草与转化</div>
              </div>
            </div>
          
            <div class="feature-card">
              <div class="feature-icon">
                <span
                          style="font-size: 30px;color:#3155ED;margin-right: 4px;"
                          class="icon iconfont icon-pinpai"
                        ></span>
              </div>
              <div class="feature-text">
                <div class="feature-title">品牌全案精准定位</div>
                <div class="feature-desc">深度策略支持，构建长效壁垒</div>
              </div>
            </div>
          
            <div class="feature-card">
              <div class="feature-icon">
                <span
                          style="font-size: 36px;color:#3155ED;margin-right: 4px;"
                          class="icon iconfont icon-chuangyizhan"
                        ></span>
              </div>
              <div class="feature-text">
                <div class="feature-title">内容创意本地共生</div>
                <div class="feature-desc">融合文化与品牌，激发营销与传播</div>
              </div>
            </div>
          </div>
        </div>
        <div class="row-1-right">
          <div class="user-card">
            <div style="display: flex;flex-direction: column;justify-content: center;">
              <div class="user-email">{{ userInfo.email || userInfo.username }}</div>
              <div class="user-days">今天是您第{{ userDaysCount }}天使用HAIMA</div>
              <div class="user-holiday">
                <span class="holiday-icon">
                  <svg t="1772616512076" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5403" width="16" height="16"><path d="M576 701.6v-65.6c55.2-14.4 96-64 96-124s-40.8-109.6-96-124v-65.6C666.4 337.6 736 416.8 736 512s-69.6 174.4-160 189.6z m0-568v64.8c145.6 29.6 256 159.2 256 313.6 0 154.4-110.4 284-256 313.6v64.8c181.6-30.4 320-188 320-378.4S757.6 164 576 133.6zM256 384H128v256h128l256 256V128L256 384z" p-id="5404" fill="#A6A6A6"></path></svg>
                </span>
                <template v-if="holidayLoading">加载中...</template>
                <template v-else-if="nextUsHoliday">
                  <template v-if="nextUsHoliday.daysLeft === 0">今天就是{{ nextUsHoliday.name }}</template>
                  <template v-else>距离{{ nextUsHoliday.name }}还有{{ nextUsHoliday.daysLeft }}天</template>
                </template>
                <template v-else>暂无节日信息</template>
              </div>
              <a-button type="primary"    :icon="h(PlusOutlined)" @click="handleAddDemand" v-if="hasPermission('user:demand')">新增需求</a-button>
              <a-button type="primary"   :icon="h(PlusOutlined)" @click="handleAddMerchantDemand" v-else>新增商家需求</a-button>
            </div>
            <div class="data-icon">
              <img src="@/assets/images/Mask group.png" alt="" />
            </div>
          </div>
        </div>
      </div>

      <!-- 第二行：数据概览 | 行业热文 -->
      <div class="row row-2">
        <div class="row-2-left">
          <div class="section-title-row">
              <div style="display: flex;align-items: center;">
                <span class="title-lt"></span>
                <span class="section-title">数据概览</span>
              </div>
          </div>
          <a-spin :spinning="dashboardDataLoading">
            <div class="data-overview">
              <div
                v-for="(item, idx) in dashboardDataList"
                :key="idx"
                class="overview-card"
              >
                <img :src="dashboardIcons[idx % dashboardIcons.length]" class="overview-icon" alt="" />
                <div class="overview-content">
                  <div class="overview-data">{{ item.description || item.value }}</div>
                  <div  class="overview-label">{{ item.text }}</div>
                </div>
              </div>
            </div>
          </a-spin>
        </div>
        <div class="row-2-right">
          <div class="hot-articles">
            <div class="section-header">
              <div style="display: flex;align-items: center;">
                <span class="title-lt"></span>
                <span class="section-title">行业热文</span>
              </div>
              <!-- <span class="refresh-btn" @click="refreshArticles">
                <ReloadOutlined /> 换一批
              </span> -->
            </div>
            <a-spin :spinning="hotArticlesLoading">
              <div class="article-list">
              
                <a
                  v-for="(article, idx) in hotArticles"
                  :key="idx"
                  class="article-item"
                  href="javascript:;"
                  :title="article.title"
                >
                  <span v-if="idx < 2" class="article-item-icon">
                    <svg
                      width="13"
                      height="15"
                      viewBox="0 0 13 15"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M12.2342 6.25653C12.2132 6.44071 12.2132 6.64068 12.2342 6.84587V8.39811C12.2342 11.2238 10.2978 13.6128 7.69307 14.3073V13.376C7.69307 12.5025 6.98797 11.7974 6.11974 11.7974H6.11446C5.24621 11.7974 4.53586 12.5025 4.53586 13.376V14.3073C1.93115 13.6075 0 11.2185 0 8.39811V5.7356C0.699855 6.3039 1.63649 7.34052 1.5786 8.7718C2.16267 6.07765 3.32033 5.67246 4.29906 4.56219C5.57249 3.11514 5.95134 1.52599 5.21991 0C8.38767 1.09976 9.20853 5.35148 8.94544 7.58784C10.0873 5.56195 11.5238 5.01998 12.2342 4.87789V6.25653Z"
                        fill="#FF7B15"
                      />
                    </svg>
                  </span>
                  <a  :href="article.url" target="_blank" class="article-item-text">{{ article.title }}</a>
                </a>
              </div>
            </a-spin>
          </div>
        </div>
      </div>

      <!-- 第三行：三个表格 -->
      <div class="row row-3">
        <div class="tables-section">
          <!-- 商品热销榜 -->
          <div class="table-card">
            <div class="table-header">
              <div style="display: flex;align-items: center;">
                <span class="title-lt"></span>
                <span class="table-title">商品热销榜</span>
              </div>
              <span class="table-subtitle">昨日销量最高的商品</span>
             
            </div>
            <a-spin :spinning="commodityLoading">
            <a-table
              :columns="commodityColumns"
              :data-source="commodityList"
              :pagination="false"
              size="small"
              row-key="commodityId"
              :scroll="{ y: 330 }"
            >
              <template #bodyCell="{ column, record, index }">
                <template v-if="column.key === 'rank'">
                  <template v-if="index === 0">
                    <svg t="1772779661340" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15039" width="32" height="32"><path d="M713.216 338.432H310.784c-41.984 0-74.752-32.256-74.752-74.752V75.264c0-41.984 35.84-74.752 74.752-74.752h399.36c41.984 0 74.752 32.256 74.752 74.752v188.416c3.072 42.496-32.256 74.752-71.68 74.752" fill="#5096F5" p-id="15040"></path><path d="M349.696 338.432H450.56V0.512H349.696v337.92z m227.328 0h100.864V0.512h-100.864v337.92z" fill="#FFFFFF" p-id="15041"></path><path d="M512 1024c224.256 0 406.016-181.76 406.016-406.016S736.256 211.968 512 211.968s-406.016 181.76-406.016 406.016S287.744 1024 512 1024z" fill="#FEE751" p-id="15042"></path><path d="M804.352 617.984c0-162.304-130.048-292.352-292.352-292.352S219.648 455.68 219.648 617.984s130.048 292.352 292.352 292.352 292.352-130.048 292.352-292.352z" fill="#FFDE3A" p-id="15043"></path><path d="M804.352 617.984c0-162.304-130.048-292.352-292.352-292.352S219.648 455.68 219.648 617.984s130.048 292.352 292.352 292.352 292.352-130.048 292.352-292.352z m32.768 0c0 178.688-146.432 325.12-325.12 325.12s-325.12-146.432-325.12-325.12S333.312 292.864 512 292.864s325.12 146.432 325.12 325.12z" fill="#FFBC00" p-id="15044"></path><path d="M569.344 809.472H496.128v-275.456c-26.624 25.088-58.368 43.52-94.208 55.296v-66.048c18.944-6.144 39.936-17.92 61.952-35.328s37.888-37.888 46.08-60.928h59.392v382.464z" fill="#E57200" p-id="15045"></path></svg>
                  </template>
                  <template v-else-if="index === 1">
                   <svg t="1772779746415" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15328" width="32" height="32"><path d="M713.551688 338.080852H310.448312c-42.258074 0-74.763707-32.505633-74.763708-74.764958V74.768711C235.684604 32.505633 271.439801 0 310.448312 0H710.297122c42.263077 0 74.768711 32.505633 74.768711 74.768711v188.547183c3.249563 42.259325-32.505633 74.764958-71.514145 74.764958" fill="#5096F5" p-id="15329"></path><path d="M349.463077 338.080852h100.771466V0h-100.771466v338.080852z m227.551942 0h100.77647V0h-100.77647v338.080852z m0 0" fill="#FFFFFF" p-id="15330"></path><path d="M512.002502 1024c224.302379 0 406.346685-182.044305 406.346684-406.346685 0-224.308633-182.044305-406.351688-406.346684-406.351688C287.695119 211.300376 105.650814 393.344682 105.650814 617.653315 105.650814 841.955695 287.695119 1024 512.002502 1024z m0 0" fill="#DFDFDD" p-id="15331"></path><path d="M804.570714 617.653315c0-162.540675-130.028788-292.574466-292.568212-292.574466S219.429286 455.11264 219.429286 617.653315c0 162.539424 130.033791 292.568212 292.573216 292.568212s292.568212-130.028788 292.568212-292.568212z m0 0" fill="#DBDBDB" p-id="15332"></path><path d="M804.570714 617.653315c0-162.540675-130.028788-292.574466-292.568212-292.574466S219.429286 455.11264 219.429286 617.653315c0 162.539424 130.033791 292.568212 292.573216 292.568212s292.568212-130.028788 292.568212-292.568212z m32.510636 0c0 178.78974-146.289109 325.078849-325.078848 325.078849-178.794743 0-325.078849-146.289109-325.078849-325.078849 0-178.794743 146.284106-325.078849 325.078849-325.078849 178.78974-0.001251 325.078849 146.282855 325.078848 325.078849z m0 0" fill="#C1C2BD" p-id="15333"></path><path d="M465.8407 743.782127h186.592192v62.412117H372.21627v-47.461376c29.257322-44.857224 67.616671-85.820726 115.078048-122.226336 61.112542-44.862227 89.070289-79.322851 83.871989-102.726457-1.299575-33.811462-20.804456-52.014267-58.513393-53.315092-36.410612 0-58.519646 22.109034-65.666683 65.666683l-73.470386-14.950741c14.955743-75.417873 64.367108-113.129311 150.189085-113.12931 79.317848 2.59915 121.575922 38.359349 128.0788 107.926008 4.554141 48.760952-24.052768 91.674442-85.820726 128.0788-40.309337 27.958997-73.470386 57.865481-100.122304 89.725704z m0 0" fill="#C1C2BD" p-id="15334"></path><path d="M452.833693 730.776372h186.598447v62.41712H359.215518v-47.461377c29.256071-44.862227 67.61542-85.820726 115.078047-122.231338 61.112542-44.857224 89.069038-79.317848 83.865735-102.726457-1.299575-33.805208-20.804456-52.011765-58.513392-53.311341-36.409361 0-58.513392 22.104031-65.666683 65.666683l-73.464132-14.306581c14.95074-75.419123 64.362105-113.129311 150.18283-113.129311 79.322851 2.604153 121.580925 38.359349 128.083804 107.931011 4.549138 48.760952-24.057771 91.669439-85.820726 128.0788-40.308086 27.309835-73.469136 57.216319-100.127308 89.072791z m0 0" fill="#FFFFFF" p-id="15335"></path></svg>
                  </template>
                  <template v-else-if="index === 2">
                   <svg t="1772779756814" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15516" width="32" height="32"><path d="M313.806 0h396.388c72.67 0 132.129 59.458 132.129 132.129v330.323H181.677V132.129C181.677 59.459 241.135 0 313.807 0z" fill="#35ABF9" p-id="15517"></path><path d="M313.806 0h132.13v330.323h-132.13z m264.259 0h132.129v330.323h-132.13z" fill="#FFFFFF" p-id="15518"></path><path d="M512 1024c165.161 0 307.2-102.4 366.658-247.742 19.82-46.245 29.73-95.793 29.73-148.645 0-218.013-178.375-396.387-396.388-396.387S115.613 409.6 115.613 627.613 293.987 1024 512 1024z" fill="#FCC376" p-id="15519"></path><path d="M512 974.452c-191.587 0-346.839-155.252-346.839-346.84S320.413 280.775 512 280.775s346.839 155.252 346.839 346.839S703.587 974.452 512 974.452z m0-660.646c-171.768 0-313.806 142.04-313.806 313.807S340.232 941.419 512 941.419s313.806-142.038 313.806-313.806S683.768 313.806 512 313.806z" fill="#E19547" p-id="15520"></path><path d="M399.69 756.439c26.426 19.82 59.458 29.729 92.49 29.729 23.123 0 39.64-6.607 52.852-16.516 13.213-9.91 19.82-23.123 19.82-42.942 0-19.82-6.607-33.033-23.123-42.942s-36.335-16.516-66.064-16.516h-36.336V604.49h33.032c52.852 0 79.278-16.516 79.278-52.851 0-33.033-19.82-49.549-62.762-49.549-26.425 0-52.851 9.91-79.277 26.426v-69.368c26.426-13.213 59.458-19.819 95.794-19.819 36.335 0 69.367 9.91 92.49 26.426 23.122 16.516 36.335 42.942 36.335 72.67 0 52.852-26.425 85.885-79.277 99.098 26.426 3.303 49.548 13.212 66.064 29.729s26.426 39.638 26.426 62.76c0 39.64-13.213 66.065-42.942 89.188-26.425 19.82-66.064 33.032-112.31 33.032-39.638 0-72.67-6.606-99.096-19.82V756.44z" fill="#E19547" opacity=".5" p-id="15521"></path><path d="M383.174 739.923c26.426 19.819 59.458 29.729 92.49 29.729 23.123 0 39.64-6.607 52.852-16.517 13.213-9.91 19.82-23.122 19.82-42.941 0-19.82-6.607-33.033-23.123-42.942s-36.336-16.517-66.065-16.517h-36.335v-62.76h33.032c52.852 0 79.278-16.517 79.278-52.852 0-33.033-19.82-49.549-62.762-49.549-26.426 0-52.851 9.91-79.277 26.426v-69.368c26.426-13.213 59.458-19.82 95.793-19.82 36.336 0 69.368 9.91 92.49 26.427 23.123 16.516 36.336 42.942 36.336 72.67 0 52.852-26.426 85.885-79.277 99.097 26.426 3.304 49.548 13.213 66.064 29.73s26.426 39.638 26.426 62.76c0 39.64-13.213 66.065-42.942 89.188-26.426 19.82-66.064 33.032-112.31 33.032-39.638 0-72.67-6.606-99.096-19.82v-75.973z" fill="#FFFFFF" p-id="15522"></path></svg>
                  </template>
                  <template v-else>
                    <span>{{ index + 1 }}</span>
                  </template>
                </template>
                <template v-else-if="column.key === 'commodity'">
                  <div class="cell-commodity">
                    <img :src="record.commodityThumbnailUrl" class="commodity-img" alt="" />
                    <div class="commodity-info">
                      <div class="commodity-name">
                        <EllipsisTooltip :text="record.commodityTitle" />
                      </div>
                      <div class="commodity-meta">
                        {{ record.sales }}{{ record.commodityPrice !== record.commodityPriceMin ? '$' + record.commodityPrice : '' }}
                        {{ record.commodityStarRate }}分
                      </div>
                    </div>
                  </div>
                </template>
                <template v-else-if="column.key === 'sales'">
                  <span >{{ formatSales(record.latestDailySales) }}</span>
                </template>
              </template>
            </a-table>
            </a-spin>
          </div>

          <!-- 达人带货榜 -->
          <div class="table-card">
            <div class="table-header">
              <div style="display: flex;align-items: center;">
                <span class="title-lt"></span>
                <span class="table-title">达人带货榜</span>
              </div>
              <span class="table-subtitle">近30日带货销售额最多的达人              </span>
             
            </div>
            <a-spin :spinning="expertLoading">
            <a-table
              :columns="expertColumns"
              :data-source="expertList"
              :pagination="false"
              size="small"
              row-key="expertId"
              :scroll="{ y: 330 }"
            >
              <template #bodyCell="{ column, record, index }">
                <template v-if="column.key === 'rank'">
                  <template v-if="column.key === 'rank'">
                  <template v-if="index === 0">
                    <svg t="1772779661340" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15039" width="32" height="32"><path d="M713.216 338.432H310.784c-41.984 0-74.752-32.256-74.752-74.752V75.264c0-41.984 35.84-74.752 74.752-74.752h399.36c41.984 0 74.752 32.256 74.752 74.752v188.416c3.072 42.496-32.256 74.752-71.68 74.752" fill="#5096F5" p-id="15040"></path><path d="M349.696 338.432H450.56V0.512H349.696v337.92z m227.328 0h100.864V0.512h-100.864v337.92z" fill="#FFFFFF" p-id="15041"></path><path d="M512 1024c224.256 0 406.016-181.76 406.016-406.016S736.256 211.968 512 211.968s-406.016 181.76-406.016 406.016S287.744 1024 512 1024z" fill="#FEE751" p-id="15042"></path><path d="M804.352 617.984c0-162.304-130.048-292.352-292.352-292.352S219.648 455.68 219.648 617.984s130.048 292.352 292.352 292.352 292.352-130.048 292.352-292.352z" fill="#FFDE3A" p-id="15043"></path><path d="M804.352 617.984c0-162.304-130.048-292.352-292.352-292.352S219.648 455.68 219.648 617.984s130.048 292.352 292.352 292.352 292.352-130.048 292.352-292.352z m32.768 0c0 178.688-146.432 325.12-325.12 325.12s-325.12-146.432-325.12-325.12S333.312 292.864 512 292.864s325.12 146.432 325.12 325.12z" fill="#FFBC00" p-id="15044"></path><path d="M569.344 809.472H496.128v-275.456c-26.624 25.088-58.368 43.52-94.208 55.296v-66.048c18.944-6.144 39.936-17.92 61.952-35.328s37.888-37.888 46.08-60.928h59.392v382.464z" fill="#E57200" p-id="15045"></path></svg>
                  </template>
                  <template v-else-if="index === 1">
                   <svg t="1772779746415" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15328" width="32" height="32"><path d="M713.551688 338.080852H310.448312c-42.258074 0-74.763707-32.505633-74.763708-74.764958V74.768711C235.684604 32.505633 271.439801 0 310.448312 0H710.297122c42.263077 0 74.768711 32.505633 74.768711 74.768711v188.547183c3.249563 42.259325-32.505633 74.764958-71.514145 74.764958" fill="#5096F5" p-id="15329"></path><path d="M349.463077 338.080852h100.771466V0h-100.771466v338.080852z m227.551942 0h100.77647V0h-100.77647v338.080852z m0 0" fill="#FFFFFF" p-id="15330"></path><path d="M512.002502 1024c224.302379 0 406.346685-182.044305 406.346684-406.346685 0-224.308633-182.044305-406.351688-406.346684-406.351688C287.695119 211.300376 105.650814 393.344682 105.650814 617.653315 105.650814 841.955695 287.695119 1024 512.002502 1024z m0 0" fill="#DFDFDD" p-id="15331"></path><path d="M804.570714 617.653315c0-162.540675-130.028788-292.574466-292.568212-292.574466S219.429286 455.11264 219.429286 617.653315c0 162.539424 130.033791 292.568212 292.573216 292.568212s292.568212-130.028788 292.568212-292.568212z m0 0" fill="#DBDBDB" p-id="15332"></path><path d="M804.570714 617.653315c0-162.540675-130.028788-292.574466-292.568212-292.574466S219.429286 455.11264 219.429286 617.653315c0 162.539424 130.033791 292.568212 292.573216 292.568212s292.568212-130.028788 292.568212-292.568212z m32.510636 0c0 178.78974-146.289109 325.078849-325.078848 325.078849-178.794743 0-325.078849-146.289109-325.078849-325.078849 0-178.794743 146.284106-325.078849 325.078849-325.078849 178.78974-0.001251 325.078849 146.282855 325.078848 325.078849z m0 0" fill="#C1C2BD" p-id="15333"></path><path d="M465.8407 743.782127h186.592192v62.412117H372.21627v-47.461376c29.257322-44.857224 67.616671-85.820726 115.078048-122.226336 61.112542-44.862227 89.070289-79.322851 83.871989-102.726457-1.299575-33.811462-20.804456-52.014267-58.513393-53.315092-36.410612 0-58.519646 22.109034-65.666683 65.666683l-73.470386-14.950741c14.955743-75.417873 64.367108-113.129311 150.189085-113.12931 79.317848 2.59915 121.575922 38.359349 128.0788 107.926008 4.554141 48.760952-24.052768 91.674442-85.820726 128.0788-40.309337 27.958997-73.470386 57.865481-100.122304 89.725704z m0 0" fill="#C1C2BD" p-id="15334"></path><path d="M452.833693 730.776372h186.598447v62.41712H359.215518v-47.461377c29.256071-44.862227 67.61542-85.820726 115.078047-122.231338 61.112542-44.857224 89.069038-79.317848 83.865735-102.726457-1.299575-33.805208-20.804456-52.011765-58.513392-53.311341-36.409361 0-58.513392 22.104031-65.666683 65.666683l-73.464132-14.306581c14.95074-75.419123 64.362105-113.129311 150.18283-113.129311 79.322851 2.604153 121.580925 38.359349 128.083804 107.931011 4.549138 48.760952-24.057771 91.669439-85.820726 128.0788-40.308086 27.309835-73.469136 57.216319-100.127308 89.072791z m0 0" fill="#FFFFFF" p-id="15335"></path></svg>
                  </template>
                  <template v-else-if="index === 2">
                   <svg t="1772779756814" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15516" width="32" height="32"><path d="M313.806 0h396.388c72.67 0 132.129 59.458 132.129 132.129v330.323H181.677V132.129C181.677 59.459 241.135 0 313.807 0z" fill="#35ABF9" p-id="15517"></path><path d="M313.806 0h132.13v330.323h-132.13z m264.259 0h132.129v330.323h-132.13z" fill="#FFFFFF" p-id="15518"></path><path d="M512 1024c165.161 0 307.2-102.4 366.658-247.742 19.82-46.245 29.73-95.793 29.73-148.645 0-218.013-178.375-396.387-396.388-396.387S115.613 409.6 115.613 627.613 293.987 1024 512 1024z" fill="#FCC376" p-id="15519"></path><path d="M512 974.452c-191.587 0-346.839-155.252-346.839-346.84S320.413 280.775 512 280.775s346.839 155.252 346.839 346.839S703.587 974.452 512 974.452z m0-660.646c-171.768 0-313.806 142.04-313.806 313.807S340.232 941.419 512 941.419s313.806-142.038 313.806-313.806S683.768 313.806 512 313.806z" fill="#E19547" p-id="15520"></path><path d="M399.69 756.439c26.426 19.82 59.458 29.729 92.49 29.729 23.123 0 39.64-6.607 52.852-16.516 13.213-9.91 19.82-23.123 19.82-42.942 0-19.82-6.607-33.033-23.123-42.942s-36.335-16.516-66.064-16.516h-36.336V604.49h33.032c52.852 0 79.278-16.516 79.278-52.851 0-33.033-19.82-49.549-62.762-49.549-26.425 0-52.851 9.91-79.277 26.426v-69.368c26.426-13.213 59.458-19.819 95.794-19.819 36.335 0 69.367 9.91 92.49 26.426 23.122 16.516 36.335 42.942 36.335 72.67 0 52.852-26.425 85.885-79.277 99.098 26.426 3.303 49.548 13.212 66.064 29.729s26.426 39.638 26.426 62.76c0 39.64-13.213 66.065-42.942 89.188-26.425 19.82-66.064 33.032-112.31 33.032-39.638 0-72.67-6.606-99.096-19.82V756.44z" fill="#E19547" opacity=".5" p-id="15521"></path><path d="M383.174 739.923c26.426 19.819 59.458 29.729 92.49 29.729 23.123 0 39.64-6.607 52.852-16.517 13.213-9.91 19.82-23.122 19.82-42.941 0-19.82-6.607-33.033-23.123-42.942s-36.336-16.517-66.065-16.517h-36.335v-62.76h33.032c52.852 0 79.278-16.517 79.278-52.852 0-33.033-19.82-49.549-62.762-49.549-26.426 0-52.851 9.91-79.277 26.426v-69.368c26.426-13.213 59.458-19.82 95.793-19.82 36.336 0 69.368 9.91 92.49 26.427 23.123 16.516 36.336 42.942 36.336 72.67 0 52.852-26.426 85.885-79.277 99.097 26.426 3.304 49.548 13.213 66.064 29.73s26.426 39.638 26.426 62.76c0 39.64-13.213 66.065-42.942 89.188-26.426 19.82-66.064 33.032-112.31 33.032-39.638 0-72.67-6.606-99.096-19.82v-75.973z" fill="#FFFFFF" p-id="15522"></path></svg>
                  </template>
                  <template v-else>
                    <span>{{ index + 1 }}</span>
                  </template>
                </template>
                </template>
                <template v-else-if="column.key === 'expert'">
                  <div class="cell-expert">
                    <img :src="record.expertAvatar" class="expert-avatar" alt="" />
                    <div class="expert-info">
                      <div class="expert-name">{{ record.expertName }}</div>
                      <div class="expert-desc">{{ record.expertCategoryCn || '-' }}</div>
                    </div>
                  </div>
                </template>
                <template v-else-if="column.key === 'gmv'">
                  <span >{{ formatGmv(record.powerSaleGmv) }}</span>
                </template>
              </template>
            </a-table>
            </a-spin>
          </div>

          <!-- 视频热播榜 -->
          <div class="table-card">
            <div class="table-header">
              <div style="display: flex;align-items: center;">
                <span class="title-lt"></span>
                <span class="table-title">视频热播榜</span>
              </div>
              <span class="table-subtitle">近30日发布播放量最高的视频</span>
             
            </div>
            <a-spin :spinning="videoLoading">
            <a-table
              :columns="videoColumns"
              :data-source="videoList"
              :pagination="false"
              size="small"
              row-key="videoId"
              :scroll="{ y: 330 }"
            >
              <template #bodyCell="{ column, record, index }">
                <template v-if="column.key === 'rank'">
                  <template v-if="column.key === 'rank'">
                  <template v-if="index === 0">
                    <svg t="1772779661340" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15039" width="32" height="32"><path d="M713.216 338.432H310.784c-41.984 0-74.752-32.256-74.752-74.752V75.264c0-41.984 35.84-74.752 74.752-74.752h399.36c41.984 0 74.752 32.256 74.752 74.752v188.416c3.072 42.496-32.256 74.752-71.68 74.752" fill="#5096F5" p-id="15040"></path><path d="M349.696 338.432H450.56V0.512H349.696v337.92z m227.328 0h100.864V0.512h-100.864v337.92z" fill="#FFFFFF" p-id="15041"></path><path d="M512 1024c224.256 0 406.016-181.76 406.016-406.016S736.256 211.968 512 211.968s-406.016 181.76-406.016 406.016S287.744 1024 512 1024z" fill="#FEE751" p-id="15042"></path><path d="M804.352 617.984c0-162.304-130.048-292.352-292.352-292.352S219.648 455.68 219.648 617.984s130.048 292.352 292.352 292.352 292.352-130.048 292.352-292.352z" fill="#FFDE3A" p-id="15043"></path><path d="M804.352 617.984c0-162.304-130.048-292.352-292.352-292.352S219.648 455.68 219.648 617.984s130.048 292.352 292.352 292.352 292.352-130.048 292.352-292.352z m32.768 0c0 178.688-146.432 325.12-325.12 325.12s-325.12-146.432-325.12-325.12S333.312 292.864 512 292.864s325.12 146.432 325.12 325.12z" fill="#FFBC00" p-id="15044"></path><path d="M569.344 809.472H496.128v-275.456c-26.624 25.088-58.368 43.52-94.208 55.296v-66.048c18.944-6.144 39.936-17.92 61.952-35.328s37.888-37.888 46.08-60.928h59.392v382.464z" fill="#E57200" p-id="15045"></path></svg>
                  </template>
                  <template v-else-if="index === 1">
                   <svg t="1772779746415" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15328" width="32" height="32"><path d="M713.551688 338.080852H310.448312c-42.258074 0-74.763707-32.505633-74.763708-74.764958V74.768711C235.684604 32.505633 271.439801 0 310.448312 0H710.297122c42.263077 0 74.768711 32.505633 74.768711 74.768711v188.547183c3.249563 42.259325-32.505633 74.764958-71.514145 74.764958" fill="#5096F5" p-id="15329"></path><path d="M349.463077 338.080852h100.771466V0h-100.771466v338.080852z m227.551942 0h100.77647V0h-100.77647v338.080852z m0 0" fill="#FFFFFF" p-id="15330"></path><path d="M512.002502 1024c224.302379 0 406.346685-182.044305 406.346684-406.346685 0-224.308633-182.044305-406.351688-406.346684-406.351688C287.695119 211.300376 105.650814 393.344682 105.650814 617.653315 105.650814 841.955695 287.695119 1024 512.002502 1024z m0 0" fill="#DFDFDD" p-id="15331"></path><path d="M804.570714 617.653315c0-162.540675-130.028788-292.574466-292.568212-292.574466S219.429286 455.11264 219.429286 617.653315c0 162.539424 130.033791 292.568212 292.573216 292.568212s292.568212-130.028788 292.568212-292.568212z m0 0" fill="#DBDBDB" p-id="15332"></path><path d="M804.570714 617.653315c0-162.540675-130.028788-292.574466-292.568212-292.574466S219.429286 455.11264 219.429286 617.653315c0 162.539424 130.033791 292.568212 292.573216 292.568212s292.568212-130.028788 292.568212-292.568212z m32.510636 0c0 178.78974-146.289109 325.078849-325.078848 325.078849-178.794743 0-325.078849-146.289109-325.078849-325.078849 0-178.794743 146.284106-325.078849 325.078849-325.078849 178.78974-0.001251 325.078849 146.282855 325.078848 325.078849z m0 0" fill="#C1C2BD" p-id="15333"></path><path d="M465.8407 743.782127h186.592192v62.412117H372.21627v-47.461376c29.257322-44.857224 67.616671-85.820726 115.078048-122.226336 61.112542-44.862227 89.070289-79.322851 83.871989-102.726457-1.299575-33.811462-20.804456-52.014267-58.513393-53.315092-36.410612 0-58.519646 22.109034-65.666683 65.666683l-73.470386-14.950741c14.955743-75.417873 64.367108-113.129311 150.189085-113.12931 79.317848 2.59915 121.575922 38.359349 128.0788 107.926008 4.554141 48.760952-24.052768 91.674442-85.820726 128.0788-40.309337 27.958997-73.470386 57.865481-100.122304 89.725704z m0 0" fill="#C1C2BD" p-id="15334"></path><path d="M452.833693 730.776372h186.598447v62.41712H359.215518v-47.461377c29.256071-44.862227 67.61542-85.820726 115.078047-122.231338 61.112542-44.857224 89.069038-79.317848 83.865735-102.726457-1.299575-33.805208-20.804456-52.011765-58.513392-53.311341-36.409361 0-58.513392 22.104031-65.666683 65.666683l-73.464132-14.306581c14.95074-75.419123 64.362105-113.129311 150.18283-113.129311 79.322851 2.604153 121.580925 38.359349 128.083804 107.931011 4.549138 48.760952-24.057771 91.669439-85.820726 128.0788-40.308086 27.309835-73.469136 57.216319-100.127308 89.072791z m0 0" fill="#FFFFFF" p-id="15335"></path></svg>
                  </template>
                  <template v-else-if="index === 2">
                   <svg t="1772779756814" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15516" width="32" height="32"><path d="M313.806 0h396.388c72.67 0 132.129 59.458 132.129 132.129v330.323H181.677V132.129C181.677 59.459 241.135 0 313.807 0z" fill="#35ABF9" p-id="15517"></path><path d="M313.806 0h132.13v330.323h-132.13z m264.259 0h132.129v330.323h-132.13z" fill="#FFFFFF" p-id="15518"></path><path d="M512 1024c165.161 0 307.2-102.4 366.658-247.742 19.82-46.245 29.73-95.793 29.73-148.645 0-218.013-178.375-396.387-396.388-396.387S115.613 409.6 115.613 627.613 293.987 1024 512 1024z" fill="#FCC376" p-id="15519"></path><path d="M512 974.452c-191.587 0-346.839-155.252-346.839-346.84S320.413 280.775 512 280.775s346.839 155.252 346.839 346.839S703.587 974.452 512 974.452z m0-660.646c-171.768 0-313.806 142.04-313.806 313.807S340.232 941.419 512 941.419s313.806-142.038 313.806-313.806S683.768 313.806 512 313.806z" fill="#E19547" p-id="15520"></path><path d="M399.69 756.439c26.426 19.82 59.458 29.729 92.49 29.729 23.123 0 39.64-6.607 52.852-16.516 13.213-9.91 19.82-23.123 19.82-42.942 0-19.82-6.607-33.033-23.123-42.942s-36.335-16.516-66.064-16.516h-36.336V604.49h33.032c52.852 0 79.278-16.516 79.278-52.851 0-33.033-19.82-49.549-62.762-49.549-26.425 0-52.851 9.91-79.277 26.426v-69.368c26.426-13.213 59.458-19.819 95.794-19.819 36.335 0 69.367 9.91 92.49 26.426 23.122 16.516 36.335 42.942 36.335 72.67 0 52.852-26.425 85.885-79.277 99.098 26.426 3.303 49.548 13.212 66.064 29.729s26.426 39.638 26.426 62.76c0 39.64-13.213 66.065-42.942 89.188-26.425 19.82-66.064 33.032-112.31 33.032-39.638 0-72.67-6.606-99.096-19.82V756.44z" fill="#E19547" opacity=".5" p-id="15521"></path><path d="M383.174 739.923c26.426 19.819 59.458 29.729 92.49 29.729 23.123 0 39.64-6.607 52.852-16.517 13.213-9.91 19.82-23.122 19.82-42.941 0-19.82-6.607-33.033-23.123-42.942s-36.336-16.517-66.065-16.517h-36.335v-62.76h33.032c52.852 0 79.278-16.517 79.278-52.852 0-33.033-19.82-49.549-62.762-49.549-26.426 0-52.851 9.91-79.277 26.426v-69.368c26.426-13.213 59.458-19.82 95.793-19.82 36.336 0 69.368 9.91 92.49 26.427 23.123 16.516 36.336 42.942 36.336 72.67 0 52.852-26.426 85.885-79.277 99.097 26.426 3.304 49.548 13.213 66.064 29.73s26.426 39.638 26.426 62.76c0 39.64-13.213 66.065-42.942 89.188-26.426 19.82-66.064 33.032-112.31 33.032-39.638 0-72.67-6.606-99.096-19.82v-75.973z" fill="#FFFFFF" p-id="15522"></path></svg>
                  </template>
                  <template v-else>
                    <span>{{ index + 1 }}</span>
                  </template>
                </template>
                </template>
                <template v-else-if="column.key === 'video'">
                  <div class="cell-video">
                    <img :src="record.videoCoverUrl ? (record.videoCoverUrl.match(/\.heic$/i) ? record.videoCoverUrl + '?x-oss-process=image/resize,m_fill,w_100/format,png' : record.videoCoverUrl) : ''" class="video-cover" alt="" />
                    <div class="video-info">
                      <div class="video-title">
                        <EllipsisTooltip :text="record.videoTitle" />
                      </div>
                      <div class="video-meta">
                        视频时长：{{ record.videoDuration }}s 发布日期：{{ formatDate(record.publishTime) }}
                      </div>
                    </div>
                  </div>
                </template>
                <template v-else-if="column.key === 'play'">
                  <span>{{ formatPlayNumber(record.playNumber) }}</span>
                </template>
              </template>
            </a-table>
            </a-spin>
          </div>
        </div>
      </div>
    </div>
    <StoreSellerPromotionModal ref="storeSellerPromotionModalRef" />
    <merchantDemandModal ref="merchantDemandModalRef" />
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'DashboardAnalysisNew' })
import { ref, computed, onMounted,h } from 'vue'
import { ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { defHttp } from '@/utils/http/axios'
import { getDictItems } from '/@/api/common/api'
import dayjs from 'dayjs'
import { usePermission } from '/@/hooks/web/usePermission'
const { hasPermission } = usePermission();
import { useUserStore } from '@/store/modules/user'
const userStore = useUserStore()
const userInfo = computed(() => userStore.getUserInfo)
import EllipsisTooltip from "@/components/jeecg/EllipsisTooltip.vue";
import StoreSellerPromotionModal from "@/views/promotionManagement/modules/StoreSellerPromotionModal.vue";
import merchantDemandModal from "@/views/promotionManagement/modules/merchantDemandModal.vue";
import iconEarth from '@/assets/images/地球-透明.png'
import iconVR from '@/assets/images/VR透明.png'
import iconFile from '@/assets/images/文件-透明 (1).png'
import iconServer from '@/assets/images/服务器-透明.png'
const storeSellerPromotionModalRef = ref(null);
const merchantDemandModalRef = ref(null);
// 根据 createTime 计算使用天数
const userDaysCount = computed(() => {
  const info = userInfo.value as { createTime?: string | number } | null
  const createTime = info?.createTime
  if (!createTime) return 1
  const days = dayjs().diff(dayjs(createTime), 'day')
  return Math.max(1, days + 1) // 当天算第1天
})

// 商品表格列
const commodityColumns = [
  { title: '#', key: 'rank', width: 48, align: 'center' as const },
  { title: '商品', key: 'commodity', dataIndex: 'commodityTitle', ellipsis: true },
  { title: '类目', dataIndex: 'commodityCategoryCn', key: 'category', width: 90 },
  { title: '昨日销量', key: 'sales', width: 90, align: 'right' as const },
]

// 达人表格列
const expertColumns = [
  { title: '#', key: 'rank', width: 48, align: 'center' as const },
  { title: '带货达人', key: 'expert', dataIndex: 'expertName', ellipsis: true },
  { title: '近30日带货销售额($)', key: 'gmv', width: 150, align: 'right' as const },
]

// 视频表格列
const videoColumns = [
  { title: '#', key: 'rank', width: 48, align: 'center' as const },
  { title: '视频', key: 'video', dataIndex: 'videoTitle', ellipsis: true },
  { title: '近30日发布播放量', key: 'play', width: 150, align: 'right' as const },
]

// 商品列表 - 从 /proboost/proboostCommodityInfo/list 获取，按昨日销量排序
const commodityList = ref<any[]>([])
const commodityLoading = ref(false)

// 达人列表 - 从 /proboost/proboostExpertInfo/list 获取，按带货GMV排序
const expertList = ref<any[]>([])
const expertLoading = ref(false)

// 视频列表 - 从 /proboost/proboostVideoInfo/list 获取，按播放量排序
const videoList = ref<any[]>([])
const videoLoading = ref(false)

async function loadCommodityList() {
  commodityLoading.value = true
  try {
    const res = await defHttp.get({
      url: '/proboost/proboostCommodityInfo/list',
    })
    const records = res || []
    commodityList.value = [...records].sort((a, b) => (b.latestDailySales || 0) - (a.latestDailySales || 0))
   
  } catch {
    commodityList.value = []
  } finally {
    commodityLoading.value = false
  }
}

async function loadExpertList() {
  expertLoading.value = true
  try {
    const res = await defHttp.get({
      url: '/proboost/proboostExpertInfo/list',
    })
    const records = res || []
    expertList.value = [...records]
    console.log(expertList.value)
  } catch {
    expertList.value = []
  } finally {
    expertLoading.value = false
  }
}

async function loadVideoList() {
  videoLoading.value = true
  try {
    const res = await defHttp.get({
      url: '/proboost/proboostVideoInfo/list',
    })
    console.log(res)
    const records = res || []
    videoList.value = [...records]
     
  } catch {
    videoList.value = []
  } finally {
    videoLoading.value = false
  }
}

// 行业热文（从 promotionalArticle/list 接口获取）
const hotArticles = ref<{ title: string; url?: string }[]>([])
const hotArticlesLoading = ref(false)

// 美国节日倒计时（Nager.Date 接口）
const nextUsHoliday = ref<{ name: string; daysLeft: number } | null>(null)
const holidayLoading = ref(true)

// 数据概览（从数据字典 dashboard_data 获取）
const dashboardDataList = ref<{ value: string; text: string; description?: string }[]>([])
const dashboardDataLoading = ref(false)
const dashboardIcons = [iconEarth, iconVR, iconFile, iconServer]

async function loadDashboardData() {
  dashboardDataLoading.value = true
  try {
    const res = await getDictItems('dashboard_data')
    const list = Array.isArray(res) ? res : res?.result ?? []
    const mapped = list.map((item: { value?: string; text?: string; itemValue?: string; itemText?: string; description?: string; title?: string }) => ({
      value: item.value ?? item.itemValue ?? '',
      text: item.text ?? item.itemText ?? item.title ?? '',
      description: item.description ?? '',
    }))
    dashboardDataList.value = mapped.length > 0 ? mapped : [
      { value: '1', text: '30W+', description: '建联达人资源' },
      { value: '2', text: '2.5W+', description: '签约达人' },
      { value: '3', text: '2K+', description: 'DTC合作品牌' },
      { value: '4', text: '6年+', description: '网红营销经验' },
    ]
  } catch {
    dashboardDataList.value = [
      { value: '1', text: '30W+', description: '建联达人资源' },
      { value: '2', text: '2.5W+', description: '签约达人' },
      { value: '3', text: '2K+', description: 'DTC合作品牌' },
      { value: '4', text: '6年+', description: '网红营销经验' },
    ]
  } finally {
    dashboardDataLoading.value = false
  }
}

function handleAddDemand() {
  storeSellerPromotionModalRef.value.add();
}

function handleAddMerchantDemand() {
  merchantDemandModalRef.value.add();
}
async function loadNextUsHoliday() {
  holidayLoading.value = true
  try {
    const year = dayjs().year()
    const today = dayjs().format('YYYY-MM-DD')
    const [resCur, resNext] = await Promise.all([
      fetch(`https://date.nager.at/api/v3/PublicHolidays/${year}/US`),
      fetch(`https://date.nager.at/api/v3/PublicHolidays/${year + 1}/US`),
    ])
    const curList: { date: string; name: string }[] = (await resCur.json()) || []
    const nextList: { date: string; name: string }[] = (await resNext.json()) || []
    const list = [...curList, ...nextList]
    const next = list
      .filter((h) => h.date >= today)
      .sort((a, b) => a.date.localeCompare(b.date))[0]
    if (next) {
      const daysLeft = dayjs(next.date).diff(dayjs(), 'day')
      nextUsHoliday.value = { name: next.name, daysLeft }
    } else {
      nextUsHoliday.value = null
    }
  } catch {
    nextUsHoliday.value = null
  } finally {
    holidayLoading.value = false
  }
}

async function loadHotArticles() {
  hotArticlesLoading.value = true
  try {
    const res = await defHttp.get({
      url: '/promotionalArticle/list',
      params: { pageNo: 1, pageSize: 6 },
    })
    const list = res?.records ?? res?.result ?? []
    hotArticles.value = list.map((item: { title?: string; url?: string }) => ({
      title: item.title ?? '',
      url: item.url,
    }))
  } catch {
    hotArticles.value = []
  } finally {
    hotArticlesLoading.value = false
  }
}

function formatSales(sales: string | number): string {
  const num = Number(sales)

  if (num >= 1000) return (num / 1000).toFixed(2) + 'K'
  return String(num)
}

function formatGmv(gmv: number | null | undefined): string {
  if (gmv == null) return '-'
  if (gmv >= 1000000) return (gmv / 1000000).toFixed(2) + 'M'
  if (gmv >= 1000) return (gmv / 1000).toFixed(2) + 'K'
  return String(gmv)
}

function formatPlayNumber(num: number | undefined): string {
  if (num == null) return '-'
  if (num >= 1000000) return (num / 1000000).toFixed(2) + 'M'
  if (num >= 1000) return (num / 1000).toFixed(2) + 'K'
  return String(num)
}

function formatDate(dateStr: string | undefined): string {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD')
}

function refreshArticles() {
  loadHotArticles()
}

onMounted(() => {
  loadHotArticles()
  loadNextUsHoliday()
  loadDashboardData()
  loadCommodityList()
  loadExpertList()
  loadVideoList()
})
</script>

<style lang="less" scoped>
.bigScreenData {
  padding: 10px;
  height: calc(100vh - 101px);
  width: 100% !important;
  background: #f5f7fa;
  overflow-y: auto;
}
.title-lt{
  width: 4px;
  height: 18px;
  background: #1890ff;
  display: inline-block;
  margin-right: 4px;
  border-radius: 4px;
}
/* 三行布局 */
.dashboard-rows {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 0 auto;
}

.row {
  display: flex;
  gap: 16px;
  min-height: 0;
}

/* 第一行：标题+特色卡片 | 用户卡片 */
.row-1 {
  flex-shrink: 0;
  align-items: stretch;
  padding: 20px 24px;
  border-radius: 12px;
  background-image: url('@/assets/images/image 33104.png');
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.12);

  .row-1-left {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 0;
  }

  .row-1-right {

    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}

/* 第二行：数据概览 | 行业热文 */
.row-2 {
  flex-shrink: 0;

  .row-2-left {
    border-radius: 8px;
    background-color: white;
    padding: 16px;
    flex: 1;
  }

  .row-2-right {
    width: 562px;
    flex-shrink: 0;
  }
}

.section-title-row {
  margin-bottom: 12px;

  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: #333;
  }
}

/* 第三行：三个表格 */
.row-3 {
  flex: 1;
  min-height: 0;

  .tables-section {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    width: 100%;
  }
}

.main-title {
  font-size: 36px;
  font-weight: 700;
  color: #102478;
  margin: 0 0 12px 0;
  font-family: Inter !important;
}

.subtitle {
  font-size: 14px;
  color: #102478;
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.feature-cards {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 16px;
  flex-wrap: nowrap;
}

.feature-card {
  display: flex;
  align-items: center;
  position: relative;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.9);
  margin-right: 20px;
  border: none;
}
.feature-card::after{
  content: '';
  position: absolute;
  right: -50px;
  top: 0;
  width: 50px;
  height: 100%;
  z-index: 99;
  background: #fff;
  clip-path: polygon(
    0 0,
    0 100%,
    100% 50%
  );
}
.feature-card:first-child::before{
  display: none;
}
.feature-card:last-child::after{
  display: none;
}
.feature-card:nth-child(2){
  padding-left: 44px;
}
.feature-card:last-child{
  padding-left: 44px;
}
.feature-card::before{
  content: '';
  position: absolute;
  left: 0px;
  top: 0;
  width: 40px;
  height: 100%;
  background: #E4EDFF;
  clip-path: polygon(
    0 0,
    0 100%,
    100% 50%
  );
}
.feature-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.feature-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.feature-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.feature-arrow {
  color: #1890ff;
  font-size: 18px;
  font-weight: bold;
}

/* 数据概览 */
.data-overview {

  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.overview-card {
  background-color: #F2F8FF;
  border-radius: 8px;
  // padding: 20px;
  height: 100px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
}

.overview-icon {
  position: absolute;
    width: 120px;
    height: 120px;
    -o-object-fit: contain;
    object-fit: contain;
    flex-shrink: 0;

    left: -18px;

    z-index: 16;
    bottom: -18px;

}

.overview-content {
  display: flex;
  flex-direction: column;
  margin-left: 130px;
}

.overview-data {
  font-size: 28px;
  font-weight: 550;
  color: #38426B;
}

.overview-label {
  font-size: 14px;
  color: #38426B;

}

/* 表格区域 */

.table-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  padding: 12px;
}

.table-header {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: space-between;
  margin-bottom: 10px;
}

.table-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.table-subtitle {
  font-size: 12px;
  color: #999;

}

.table-link {
  font-size: 12px;
  color: #1890ff;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
}

.medal {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: #fff;

  &.medal-1 {
    background: linear-gradient(135deg, #ffd700, #ffb300);
  }

  &.medal-2 {
    background: linear-gradient(135deg, #c0c0c0, #9e9e9e);
  }

  &.medal-3 {
    background: linear-gradient(135deg, #cd7f32, #a0522d);
  }
}

.cell-commodity {
  display: flex;
  align-items: center;
  gap: 12px;
}

.commodity-img {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.commodity-info {
  min-width: 0;
}

.commodity-name {
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.commodity-meta {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.cell-sales {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
}

.cell-expert {
  display: flex;
  align-items: center;
  gap: 12px;
}

.expert-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.expert-info {
  min-width: 0;
}

.expert-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.expert-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cell-gmv {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
}

.cell-video {
  display: flex;
  align-items: center;
  gap: 12px;
}

.video-cover {
  width: 64px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.video-info {
  min-width: 0;
}

.video-title {
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-meta {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.cell-play {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
}

/* 表格内 ant-design-vue 样式覆盖 */
.table-card :deep(.ant-table) {
  font-size: 13px;
}

.table-card :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  padding: 10px 12px;
}

.table-card :deep(.ant-table-tbody > tr > td) {
  padding: 10px 12px;
}

/* 用户卡片 */
.user-card {
 
  display: flex;
  gap: 60px;
  padding: 6px 28px;
  background: #F2F8FF;
  border-radius: 16px;
  color: #222;
  box-shadow: 0 12px 30px rgba(9, 109, 217, 0.18);
  overflow: hidden;
}

.user-email {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.user-days {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 12px;
}

.user-holiday {
  font-size: 12px;
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #ffffff;
  display: flex;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.03), 0 6px 16px rgba(31, 89, 255, 0.08);
}

.holiday-icon {
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-demand-btn {
  margin-top: 4px;
  padding: 0 20px;
  height: 32px;
  border-radius: 999px;
  font-size: 13px;
}

.data-icon {
  // width: 140px;
  height: 190px;
  opacity: 1;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.hot-articles {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  height: 100%;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.refresh-btn {
  font-size: 12px;
  color: #1890ff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;

  &:hover {
    opacity: 0.8;
  }
}

.article-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  column-gap: 16px;
  row-gap: 11px;
}

.article-item-icon {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  margin-right: 6px;

  svg {
    display: block;
    width: 13px;
    height: 15px;
  }
}

.article-item-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
  color: black;
}

.article-item {
  height: 26px;
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #333;
  text-decoration: none;
  line-height: 1.5;
  transition: color 0.2s;
  overflow: hidden;
  max-width: 100%;
  background-color: #F8F9FA;
  padding: 0 4px;
  gap: 0;
  &:last-child {
    border-bottom: none;
  }

  &:hover {
    color: #1890ff;
  }
}

@media (max-width: 1400px) {
  .row-3 .tables-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1200px) {
  .row-1,
  .row-2 {
    flex-direction: column;
  }

  .row-1-right,
  .row-2-right {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .data-overview {
    grid-template-columns: repeat(2, 1fr);
  }

  .feature-cards {
    flex-direction: column;
    align-items: flex-start;
  }

  .feature-arrow {
    transform: rotate(90deg);
  }
}
</style>
