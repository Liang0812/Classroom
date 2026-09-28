<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Microsoft YaHei', 'PingFang SC', Arial, sans-serif;
}

/* ===== 全局按钮统一规范：格式与间距 ===== */
.el-button {
  border-radius: 6px;
  font-size: 14px;
}
.el-button--small {
  font-size: 13px;
}
.el-button--large {
  font-size: 15px;
}
/* 同一行相邻按钮统一间距 */
.el-button + .el-button {
  margin-left: 8px;
}

/* ===== 表单项间距兜底（按需引入时 form 包样式缺失 margin-bottom，补回 EP 默认 18px） ===== */
.el-form-item {
  margin-bottom: 18px;
}
.el-form-item:last-child {
  margin-bottom: 18px;
}

/* ===== 表单校验错误提示：红色文字 + 输入框红框（form 包样式缺失兜底） ===== */
.el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  line-height: 1.2;
  padding-top: 4px;
}
.el-form-item.is-error .el-input__wrapper,
.el-form-item.is-error .el-textarea__inner {
  box-shadow: 0 0 0 1px #f56c6c inset;
}
</style>
