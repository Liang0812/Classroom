<template>
  <div class="certificate-page">
    <div ref="certRef" class="certificate">
      <div class="cert-border">
        <div class="cert-head">
          <div class="title">结业证书</div>
          <div class="subtitle">CERTIFICATE OF COMPLETION</div>
        </div>
        <div class="body">
          <p class="line">兹证明 <span class="hl">{{ userName }}</span> 同学：</p>
          <p class="line">在<b>酷云课堂</b>在线学习平台完成课程</p>
          <p class="course">《{{ courseName }}》</p>
          <p class="line">全部章节的学习，成绩合格，特发此证，以资鼓励。</p>
          <p class="time">完成时间：{{ finishTimeText }}</p>
          <p class="no">证书编号：{{ certNo }}</p>
        </div>
        <div class="seal">酷云课堂<br />认证中心</div>
      </div>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="doPrint">打印 / 保存 PDF</el-button>
      <el-button @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { currentUser } from '../api/auth'
import { getCourseDetail } from '../api/course'
import { getLearnProgress } from '../api/learn'

const route = useRoute()
const certRef = ref(null)

const userName = ref('')
const courseName = ref('')
const finishTimeText = ref('')
const certNo = ref('')

onMounted(async () => {
  const cuid = Number(route.params.cuid)
  const [me, detail, progress] = await Promise.all([
    currentUser(),
    getCourseDetail(cuid),
    getLearnProgress()
  ])
  userName.value = me.data.user.userName

  const p = (progress.data || []).find((x) => x.cuid === cuid)
  const course = detail.data?.course || {}

  if (p && p.progress === 100) {
    courseName.value = p.courseName
    const d = p.finishTime ? new Date(p.finishTime) : new Date()
    finishTimeText.value = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  } else if (course.courseName) {
    // 未学完也允许预览证书（进度不足）
    courseName.value = course.courseName
    finishTimeText.value = '—'
  } else {
    courseName.value = '该课程'
  }
  certNo.value = `KY-${String(cuid).padStart(4, '0')}-${String(me.data.user.uid).padStart(4, '0')}-${finishTimeText.value.replace(/-/g, '') || '00000000'}`
})

const doPrint = () => {
  const node = certRef.value
  if (!node) return
  const clone = node.cloneNode(true)
  // 收集页面全部样式（含 scoped），保证打印窗口渲染一致
  const styles = Array.from(document.querySelectorAll('style'))
    .map((s) => s.textContent)
    .join('\n')
  const win = window.open('', '_blank')
  win.document.write('<html><head><title>结业证书</title></head><body>')
  win.document.write(`<style>${styles}</style>`)
  win.document.write(clone.outerHTML)
  win.document.write('</body></html>')
  win.document.close()
  win.print()
}
</script>

<style scoped>
.certificate-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}
.certificate {
  max-width: 760px;
  width: 100%;
  background: #fffdf5;
}
.cert-border {
  border: 3px double #b8860b;
  padding: 44px 52px;
  position: relative;
  min-height: 380px;
  box-sizing: border-box;
}
.cert-head {
  text-align: center;
  margin-bottom: 28px;
}
.title {
  font-size: 36px;
  font-weight: 700;
  color: #8a5a1b;
  letter-spacing: 14px;
}
.subtitle {
  font-size: 12px;
  color: #b8860b;
  letter-spacing: 4px;
  margin-top: 4px;
}
.body {
  font-size: 16px;
  color: #3a3a3a;
  text-align: center;
  line-height: 2;
}
.hl {
  font-weight: 700;
  color: #8a5a1b;
  font-size: 20px;
}
.course {
  font-size: 22px;
  font-weight: 700;
  color: #8a5a1b;
  margin: 8px 0;
}
.time,
.no {
  font-size: 13px;
  color: #7a7a7a;
  margin-top: 4px;
}
.seal {
  position: absolute;
  right: 56px;
  bottom: 34px;
  width: 96px;
  height: 96px;
  border: 2px solid #c0392b;
  border-radius: 50%;
  color: #c0392b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  line-height: 1.6;
  text-align: center;
  transform: rotate(-12deg);
  opacity: 0.85;
}
.toolbar {
  display: flex;
  gap: 12px;
}
</style>
