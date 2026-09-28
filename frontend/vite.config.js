import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'

// 项目实际用到的 Element Plus 组件 → 子路径目录（含分组组件如 form-item 在 form 包）
const EP_DIR = {
  ElAside: 'container', ElBadge: 'badge', ElButton: 'button', ElCard: 'card',
  ElCarousel: 'carousel', ElCarouselItem: 'carousel', ElCheckbox: 'checkbox',
  ElCheckboxGroup: 'checkbox', ElCol: 'col', ElConfigProvider: 'config-provider',
  ElContainer: 'container', ElDescriptions: 'descriptions', ElDescriptionsItem: 'descriptions',
  ElDialog: 'dialog', ElDivider: 'divider', ElDrawer: 'drawer', ElDropdown: 'dropdown',
  ElDropdownMenu: 'dropdown', ElDropdownItem: 'dropdown', ElEmpty: 'empty',
  ElForm: 'form', ElFormItem: 'form', ElInput: 'input', ElInputNumber: 'input-number',
  ElLink: 'link', ElMain: 'container', ElMenu: 'menu', ElMenuItem: 'menu',
  ElOption: 'select', ElPagination: 'pagination', ElProgress: 'progress', ElRadio: 'radio',
  ElRadioButton: 'radio', ElRadioGroup: 'radio', ElRate: 'rate', ElRow: 'row',
  ElSelect: 'select', ElSwitch: 'switch', ElTabPane: 'tabs', ElTable: 'table',
  ElTableColumn: 'table', ElTabs: 'tabs', ElTag: 'tag', ElUpload: 'upload'
}

// 组件子路径解析：JS 与 CSS 均按需
function elementPlusSubPathResolver(name) {
  const dir = EP_DIR[name]
  if (!dir) return undefined
  return {
    importName: name,
    path: `element-plus/es/components/${dir}/index.mjs`,
    sideEffects: [`element-plus/es/components/${dir}/style/css`]
  }
}

// 开发环境代理：/api 转发到后端 Tomcat（默认 8080）
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({ resolvers: [elementPlusSubPathResolver], dts: false }),
    Components({ resolvers: [elementPlusSubPathResolver], dts: false })
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    chunkSizeWarningLimit: 900,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('element-plus') || id.includes('@element-plus')) return 'element-plus'
            if (id.includes('vue') || id.includes('vue-router') || id.includes('@vue')) return 'vue-vendor'
            return 'vendor'
          }
        }
      }
    }
  }
})
