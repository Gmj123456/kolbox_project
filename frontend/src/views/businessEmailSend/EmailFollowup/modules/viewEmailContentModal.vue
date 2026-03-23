<template>
  <a-modal
    title="邮件历史预览"
    :width="900"
    centered
    :maskClosable="false"
    v-model:open="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @cancel="handleCancel"
    cancelText="关闭"
    :bodyStyle="{ padding: '24px', maxHeight: '70vh', overflowY: 'auto' }"
  >
    <a-spin :spinning="confirmLoading">
      <!-- 邮件历史时间线 -->
      <div class="email-timeline">
        <div class="timeline-title">
          <div class="title-left">
            <HistoryOutlined class="title-icon" />
            <span>邮件往来记录</span>
            <a-tag color="blue" style="margin-left: 8px;">共 {{ conversationGroups.length }} 轮对话</a-tag>
          </div>
          <div class="recipient-info-compact">
            <UserOutlined class="recipient-icon" />
            <span class="recipient-text">{{ model.brandName || '未知品牌' }}</span>
            <span class="recipient-email-text">{{ model.email }}</span>
          </div>
        </div>
        
        <div class="timeline-content">
          <!-- 对话组循环 -->
          <div 
            v-for="(group, groupIndex) in conversationGroups" 
            :key="groupIndex"
            class="conversation-group"
          >
            <!-- 时间线标记 -->
            <div class="timeline-marker">
              <div class="marker-dot marker-success"></div>
              <div class="marker-line" v-if="groupIndex !== conversationGroups.length - 1"></div>
            </div>
            
            <!-- 对话组卡片 -->
            <div class="conversation-card">
              <!-- 对话组内的邮件循环 - 发送在前，回复在后 -->
              <div 
                v-for="(emailRecord, emailIndex) in group" 
                :key="`${groupIndex}-${emailIndex}`" 
                class="email-item"
                :class="{ 'is-reply': emailRecord.isReply }"
              >
                <!-- 回复邮件（展示在前面） -->
                <template v-if="emailRecord.isReply">
                  <div class="reply-divider">
                    <div class="reply-label">
                      <InboxOutlined style="margin-right: 4px;" />
                      <span>对方回复</span>
                    </div>
                    <div class="reply-time">
                      <ClockCircleOutlined style="margin-right: 4px;" />
                      {{ emailRecord.sendTime }}
                    </div>
                  </div>
                  
                  <div class="email-content-section reply-content">
                    <div class="section-label">
                      <MessageOutlined style="margin-right: 4px;" />
                      邮件内容
                    </div>
                    <div class="content-wrapper">
                      <div class="email-content-preview" :class="{ 'content-collapsed': !isContentExpanded(groupIndex, emailIndex) }">
                        <div class="content-inner" v-html="emailRecord.content"></div>
                      </div>
                      <div class="content-toggle" @click="toggleContent(groupIndex, emailIndex)">
                        <DownOutlined v-if="!isContentExpanded(groupIndex, emailIndex)" />
                        <UpOutlined v-else />
                      </div>
                    </div>
                  </div>
                </template>
                
                <!-- 发送邮件（展示在后面） -->
                <template v-else>
                  <div class="email-header">
                    <div class="email-tags">
                      <div class="send-direction">
                        <SendOutlined class="direction-icon" />
                        <span>我发送的</span>
                      </div>
                      <a-tag color="blue" v-if="emailRecord.sendCount === 1">首次发送</a-tag>
                      <a-tag color="green" v-else>第{{ emailRecord.sendCount }}次跟进</a-tag>
                      <a-tag :color="getSendStatus(emailRecord.isSend).color">
                        {{ getSendStatus(emailRecord.isSend).text }}
                      </a-tag>
                      <span class="interaction-compact">
                        <EyeOutlined style="margin-right: 2px;" />
                        {{ emailRecord.isOpened == 1 ? '已打开' : '未打开' }} · {{ emailRecord.openCount || 0 }}次
                      </span>
                    </div>
                    <div class="email-time">
                      <ClockCircleOutlined style="margin-right: 4px;" />
                      {{ emailRecord.sendTime }}
                    </div>
                  </div>
                  
                  <div class="email-content-section">
                    <template v-if="emailRecord.sendCount === 1">
                      <div class="section-label">
                        <FileTextOutlined style="margin-right: 4px;" />
                        邮件主题
                      </div>
                      <div class="subject-text">{{ emailRecord.emailTitle || '无主题' }}</div>
                    </template>
                    
                    <div class="section-label" :style="emailRecord.sendCount === 1 ? 'margin-top: 12px;' : ''">
                      <MessageOutlined style="margin-right: 4px;" />
                      邮件内容
                    </div>
                    <div class="content-wrapper">
                      <div class="email-content-preview" :class="{ 'content-collapsed': !isContentExpanded(groupIndex, emailIndex) }">
                        <div class="content-inner" v-html="emailRecord.content"></div>
                      </div>
                      <div class="content-toggle" @click="toggleContent(groupIndex, emailIndex)">
                        <DownOutlined v-if="!isContentExpanded(groupIndex, emailIndex)" />
                        <UpOutlined v-else />
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
    
    <template #footer>
      <a-button key="back" @click="handleCancel" type="primary">关闭</a-button>
    </template>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from "vue";
import { UserOutlined, FileTextOutlined, ClockCircleOutlined, HistoryOutlined, MessageOutlined, EyeOutlined, DownOutlined, UpOutlined, SendOutlined, InboxOutlined } from "@ant-design/icons-vue";
import dayjs from "dayjs";
import { defHttp } from "/@/utils/http/axios";

const emit = defineEmits(["close"]);

const visible = ref(false);
const model = reactive({
  brandName: undefined,
  email: undefined,
  emailTitle: undefined,
  content: undefined,
  emailContent: undefined,
  signatureContent: undefined,
  sendCount: undefined,
});
const confirmLoading = ref(false);
const conversationGroups = ref([]); // 对话组数组，每个组包含：我发送的 + 对方回复的
const expandedIndexes = ref(new Set());

// 判断内容是否展开
function isContentExpanded(groupIndex, emailIndex) {
  const key = `${groupIndex}-${emailIndex}`;
  return expandedIndexes.value.has(key);
}

// 切换内容展开/收起
function toggleContent(groupIndex, emailIndex) {
  const key = `${groupIndex}-${emailIndex}`;
  if (expandedIndexes.value.has(key)) {
    expandedIndexes.value.delete(key);
  } else {
    expandedIndexes.value.add(key);
  }
}

async function view(record) {
  Object.assign(model, record);
  confirmLoading.value = true;
  try {
    const res = await defHttp.get({
      url: '/email/emailPushDetail/conversation',
      params: { detailId: record.id },
    });
    const data = res || {};
    model.brandName = data.nickname ?? record.brandName;
    model.email = data.email ?? record.email;
    conversationGroups.value = buildConversationGroups(data.conversations || []);
  } catch (error) {
    console.error('获取邮件对话失败:', error);
    conversationGroups.value = [];
  } finally {
    confirmLoading.value = false;
  }
  visible.value = true;
}

// 将接口返回的对话列表转换为对话组（发送在前，回复在后）
// 每条 sent/follow_up 后的 reply 归属于该条发送，不归属于下一条发送
function buildConversationGroups(conversations) {
  const groups = [];
  let pendingReplies = [];

  for (const conv of conversations) {
    if (conv.type === 'reply') {
      pendingReplies.push({
        content: conv.emailContent || '',
        sendTime: conv.time ? dayjs(conv.time).format('YYYY-MM-DD HH:mm') : '--',
        isReply: true,
      });
    } else if (conv.type === 'sent' || conv.type === 'follow_up') {
      // 将当前累积的回复归入上一组（它们是上一轮发送的回复）
      if (groups.length > 0 && pendingReplies.length > 0) {
        groups[groups.length - 1].push(...pendingReplies);
        pendingReplies = [];
      }
      const sendCount = (conv.sequence ?? 0) + 1;
      const sentItem = {
        emailTitle: conv.emailTitle || '无主题',
        content: conv.emailContent || '',
        sendTime: conv.time ? dayjs(conv.time).format('YYYY-MM-DD HH:mm') : '--',
        sendCount,
        isSend: conv.isSend ?? 1,
        isOpened: conv.isOpened ?? 0,
        openCount: conv.openCount ?? 0,
        isReply: false,
      };
      groups.push([sentItem]);
    }
  }
  if (pendingReplies.length > 0 && groups.length > 0) {
    groups[groups.length - 1].push(...pendingReplies);
  }
  return groups;
}

// 获取发送状态
function getSendStatus(isSend) {
  const statusMap = {
    1: { text: '发送成功', color: 'green', type: 'success' },
    0: { text: '等待发送', color: 'orange', type: 'waiting' },
    '-1': { text: '发送失败', color: 'red', type: 'error' },
    '-2': { text: '取消发送', color: 'default', type: 'cancel' },
  };
  return statusMap[isSend] || statusMap[1];
}

function close() {
  emit("close");
  Object.keys(model).forEach((key) => {
    model[key] = undefined;
  });
  conversationGroups.value = [];
  expandedIndexes.value = new Set();
  visible.value = false;
}

function handleCancel() {
  close();
}

defineExpose({
  view,
});
</script>

<style lang="less" scoped>
.email-timeline {
  .timeline-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;
    
    .title-left {
      display: flex;
      align-items: center;
      font-size: 14px;
      font-weight: 600;
      color: #202124;
      
      .title-icon {
        font-size: 16px;
        color: #1a73e8;
        margin-right: 6px;
      }
    }
    
    .recipient-info-compact {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 4px 12px;
      background: #f5f7fa;
      border-radius: 14px;
      
      .recipient-icon {
        font-size: 12px;
        color: #667eea;
      }
      
      .recipient-text {
        font-size: 12px;
        font-weight: 500;
        color: #202124;
      }
      
      .recipient-email-text {
        font-size: 11px;
        color: #8c8c8c;
      }
    }
  }
}

.timeline-content {
  padding-left: 8px;
}

// 对话组样式
.conversation-group {
  display: flex;
  gap: 16px;
  position: relative;
  margin-bottom: 16px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

// 对话组卡片（整体框）
.conversation-card {
  flex: 1;
  background: white;
  border-radius: 10px;
  border: 1px solid #e8eaed;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    border-color: #d0d7de;
  }
}

// 对话组内的邮件项
.email-item {
  padding: 12px 14px;
  position: relative;
  background: white;
  
  &:last-child {
    padding-bottom: 12px;
  }
}

// 回复分隔线
.reply-divider {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-top: 8px;
  
  .reply-label {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 4px 12px;
    background: #722ed1;
    border-radius: 12px;
    font-size: 12px;
    color: #fff;
    font-weight: 500;
    white-space: nowrap;
  }
  
  .reply-time {
    font-size: 12px;
    color: #5f6368;
    white-space: nowrap;
    display: flex;
    align-items: center;
  }
}

// 回复内容区域
.reply-content {
  background: #f9f0ff;
  padding: 10px 12px;
  border-radius: 6px;
  margin-top: 0;
  
  .subject-text {
    background: #fff;
    border-color: #d3adf7;
  }
  
  .email-content-preview {
    background: #fff;
    border-color: #d3adf7;
    
    &.content-collapsed::after {
      background: linear-gradient(to bottom, transparent, #fff);
    }
  }
}

.timeline-marker {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 16px;
  
  .marker-dot {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    flex-shrink: 0;
    position: relative;
    z-index: 1;
    
    &.marker-success {
      background: #52c41a;
      box-shadow: 0 0 0 4px rgba(82, 196, 26, 0.1);
    }
    
    &.marker-waiting {
      background: #faad14;
      box-shadow: 0 0 0 4px rgba(250, 173, 20, 0.1);
    }
    
    &.marker-error {
      background: #ff4d4f;
      box-shadow: 0 0 0 4px rgba(255, 77, 79, 0.1);
    }
    
    &.marker-cancel {
      background: #d9d9d9;
      box-shadow: 0 0 0 4px rgba(217, 217, 217, 0.1);
    }
    
    &.marker-reply {
      background: #722ed1;
      box-shadow: 0 0 0 4px rgba(114, 46, 209, 0.1);
    }
  }
  
  .marker-line {
    width: 2px;
    flex: 1;
    background: linear-gradient(180deg, #e8eaed 0%, #e8eaed 100%);
    margin: 8px 0;
    min-height: 40px;
  }
}


// 回复邮件顶部标识条
.reply-banner {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  margin: -6px -14px 8px -11px;
  background: #e8e8e8;
  color: #595959;
  border-radius: 0;
  
  .reply-banner-icon {
    font-size: 13px;
  }
  
  .reply-banner-text {
    font-size: 12px;
    font-weight: 600;
  }
  
  .auto-reply-badge {
    margin-left: auto;
    padding: 2px 8px;
    background: rgba(0,0,0,0.08);
    border-radius: 10px;
    font-size: 11px;
  }
}

// 发送方向标识
.send-direction {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
  background: #e6f7ff;
  color: #1890ff;
  
  .direction-icon {
    font-size: 13px;
    color: #1890ff;
  }
}

.email-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  min-height: 26px;
}

.email-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.email-time {
  font-size: 12px;
  color: #5f6368;
  display: flex;
  align-items: center;
}

// 交互数据紧凑样式
.interaction-compact {
  display: flex;
  align-items: center;
  font-size: 11px;
  color: #8c8c8c;
  margin-left: 8px;
  padding-left: 8px;
  border-left: 1px solid #e8eaed;
}

.email-content-section {
  .section-label {
    font-size: 12px;
    color: #5f6368;
    font-weight: 500;
    margin-bottom: 6px;
    display: flex;
    align-items: center;
  }
  
  .subject-text {
    font-size: 14px;
    font-weight: 600;
    color: #202124;
    padding: 6px 10px;
    background: #fafbfc;
    border-radius: 6px;
    border: 1px solid #e8eaed;
  }
  
  .content-wrapper {
    position: relative;
  }
  
  .email-content-preview {
    padding: 10px;
    padding-right: 32px;
    min-height: 46px;
    max-height: 300px;
    overflow: hidden;
    line-height: 1.6;
    font-size: 13px;
    color: #333;
    background: #fafbfc;
    border-radius: 6px;
    border: 1px solid #e8eaed;
    position: relative;
    transition: max-height 0.3s ease;
    
    &.content-collapsed {
      max-height: 80px;
      
      .content-inner {
        overflow: hidden;
        max-height: 56px;
      }
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 24px;
        background: linear-gradient(to bottom, transparent, #fafbfc);
        pointer-events: none;
      }
    }
    
    .content-inner {
      overflow-y: auto;
      max-height: 276px;
      
      &::-webkit-scrollbar {
        width: 6px;
      }
      
      &::-webkit-scrollbar-thumb {
        background: #d0d7de;
        border-radius: 3px;
      }
      
      &::-webkit-scrollbar-track {
        background: transparent;
      }
    }
    
    img {
      max-width: 100%;
      height: auto;
    }
    
    p {
      margin: 8px 0;
    }
    
    a {
      color: #1890ff;
    }
  }
  
  .content-toggle {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: white;
    border-radius: 4px;
    border: 1px solid #d9d9d9;
    cursor: pointer;
    color: #666;
    z-index: 10;
    transition: all 0.2s;
    
    &:hover {
      color: #1890ff;
      border-color: #1890ff;
      background: #f0f7ff;
    }
  }
}
</style>
