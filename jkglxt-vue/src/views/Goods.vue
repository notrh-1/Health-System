<template>
  <div class="chat-container">
    <div class="chat-wrapper">
      <div class="chat-header">
        <span class="chat-title">聊天区</span>
        <el-button size="small" @click="getMessageList" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      
      <div class="message-list" ref="messageListRef">
        <el-empty v-if="messageList.length === 0 && !loading" description="暂无消息，快来发表第一条吧！" />
        <div 
          v-for="item in messageList" 
          :key="item.id" 
          class="message-item"
          :class="{ 'is-own': item.userId === user.id }"
        >
          <div class="avatar-wrapper">
            <img v-if="item.avatar" :src="item.avatar" class="avatar" />
            <div v-else class="avatar-placeholder">{{ item.username?.charAt(0)?.toUpperCase() }}</div>
          </div>
          
          <div class="message-content-wrapper">
            <div class="message-meta">
              <span class="username">{{ item.username }}</span>
              <span class="time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="message-bubble">
              {{ item.content }}
            </div>
            <el-button
              v-if="isAdmin"
              size="small"
              type="danger"
              text
              class="delete-btn"
              @click="deleteMessage(item.id)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="message-input-wrapper">
        <el-input
          v-model="messageContent"
          type="textarea"
          :rows="3"
          placeholder="说点什么..."
          @keydown.ctrl.enter="sendMessage"
        />
        <div class="input-footer">
          <span class="hint">按 Ctrl+Enter 发送</span>
          <el-button type="primary" @click="sendMessage" :loading="sending">
            <el-icon><ChatDotRound /></el-icon>
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Delete, ChatDotRound } from '@element-plus/icons-vue'
import request from '@/utils/request'

const messageContent = ref('')
const messageList = ref([])
const loading = ref(false)
const sending = ref(false)
const messageListRef = ref(null)

const user = ref(JSON.parse(window.sessionStorage.getItem('user') || '{}'))
const isAdmin = computed(() => {
  return user.value?.role === 'admin'
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

const getMessageList = async (shouldScroll = true) => {
  loading.value = true
  try {
    const res = await request.get('/chat/list')
    if (res.flag === 200) {
      messageList.value = res.list || []
      if (shouldScroll) {
        scrollToBottom()
      }
    } else {
      ElMessage.error('获取消息失败')
    }
  } catch (error) {
    ElMessage.error('获取消息失败')
  } finally {
    loading.value = false
  }
}

const sendMessage = async () => {
  if (!messageContent.value.trim()) {
    return ElMessage.warning('请输入内容')
  }
  
  sending.value = true
  try {
    const res = await request.post('/chat/add', {
      content: messageContent.value
    })
    
    if (res.flag === 200) {
      ElMessage.success(res.message)
      messageContent.value = ''
      await getMessageList(true)
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (error) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

const deleteMessage = async (id) => {
  try {
    const res = await request.delete(`/chat/delete/${id}`)
    if (res.flag === 200) {
      ElMessage.success(res.message)
      getMessageList(false)
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  getMessageList(true)
  setInterval(() => {
    getMessageList(false)
  }, 5000)
})
</script>

<style scoped>
.chat-container {
  padding: 20px;
  height: calc(100vh - 60px);
}

.chat-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e6e6e6;
  background: #f5f5f5;
}

.chat-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: #f5f5f5;
}

.message-list::-webkit-scrollbar {
  width: 8px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #c9c9c9;
  border-radius: 4px;
}

.message-list::-webkit-scrollbar-track {
  background: #f0f0f0;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-item.is-own {
  flex-direction: row-reverse;
}

.avatar-wrapper {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.message-content-wrapper {
  max-width: 60%;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message-item.is-own .message-content-wrapper {
  align-items: flex-end;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #999;
  padding: 0 8px;
}

.message-item.is-own .message-meta {
  flex-direction: row-reverse;
}

.username {
  font-weight: 500;
  color: #666;
}

.time {
  color: #999;
}

.message-bubble {
  background: #fff;
  border-radius: 4px;
  padding: 12px 16px;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 14px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  position: relative;
}

.message-item.is-own .message-bubble {
  background: #95ec69;
  color: #333;
}

.message-bubble::before {
  content: '';
  position: absolute;
  top: 14px;
  left: -8px;
  border: 6px solid transparent;
  border-right-color: #fff;
}

.message-item.is-own .message-bubble::before {
  left: auto;
  right: -8px;
  border-right-color: transparent;
  border-left-color: #95ec69;
}

.delete-btn {
  margin-top: 4px;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.delete-btn:hover {
  opacity: 1;
}

.message-input-wrapper {
  padding: 16px 24px;
  border-top: 1px solid #e6e6e6;
  background: #fff;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
}

.hint {
  color: #999;
  font-size: 12px;
}
</style>
