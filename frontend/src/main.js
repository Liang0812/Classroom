import { createApp } from 'vue'
// 命令式组件（ElMessage / ElMessageBox / ElNotification / v-loading）样式兜底
import 'element-plus/theme-chalk/base.css'
import 'element-plus/es/components/message/style/css'
import 'element-plus/es/components/message-box/style/css'
import 'element-plus/es/components/notification/style/css'
import 'element-plus/es/components/loading/style/css'
import { vLoading } from 'element-plus/es/components/loading/index.mjs'
import App from './App.vue'
import router from './router'

createApp(App).use(router).directive('loading', vLoading).mount('#app')
