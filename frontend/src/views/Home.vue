<template>
  <div class="home">
    <el-carousel v-if="banners.length" height="320px" class="banner" :interval="4000">
      <el-carousel-item v-for="c in banners" :key="c.cuid">
        <div class="banner-item" @click="$router.push(`/courses/${c.cuid}`)">
          <img v-if="c.courseImage" :src="c.courseImage" :alt="c.courseName" />
          <div v-else class="banner-placeholder">{{ c.courseName }}</div>
          <div class="banner-title">{{ c.courseName }}</div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <section v-if="data.recommend.length" class="block">
      <h3>推荐课程</h3>
      <div class="grid">
        <CourseCard v-for="c in data.recommend" :key="c.cuid" :course="c" />
      </div>
    </section>

    <section v-if="data.latest.length" class="block">
      <h3>最新课程</h3>
      <div class="grid">
        <CourseCard v-for="c in data.latest" :key="c.cuid" :course="c" />
      </div>
    </section>

    <section v-if="data.hottest.length" class="block">
      <h3>热门课程</h3>
      <div class="grid">
        <CourseCard v-for="c in data.hottest" :key="c.cuid" :course="c" />
      </div>
    </section>

    <el-empty v-if="!loading && !data.recommend.length && !data.latest.length && !data.hottest.length" description="暂无课程" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import CourseCard from '../components/CourseCard.vue'
import { getHome } from '../api/home'

const loading = ref(false)
const data = reactive({ recommend: [], latest: [], hottest: [] })
const banners = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getHome()
    data.recommend = res.data.recommend || []
    data.latest = res.data.latest || []
    data.hottest = res.data.hottest || []
    // Banner 使用推荐课程封面轮播
    banners.value = data.recommend
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.banner {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
}
.banner-item {
  position: relative;
  height: 100%;
  cursor: pointer;
}
.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.banner-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #304156;
  color: #fff;
  font-size: 20px;
}
.banner-title {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 16px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  color: #fff;
  font-size: 16px;
}
.block {
  margin-bottom: 28px;
}
.block h3 {
  font-size: 17px;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
</style>
