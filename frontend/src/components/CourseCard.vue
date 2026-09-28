<template>
  <div class="course-card" @click="$router.push(`/courses/${course.cuid}`)">
    <div class="cover">
      <img v-if="course.courseImage" :src="course.courseImage" :alt="course.courseName" />
      <div v-else class="cover-placeholder">{{ course.courseName }}</div>
    </div>
    <div class="info">
      <div class="name" :title="course.courseName">{{ course.courseName }}</div>
      <div class="meta">老师：{{ course.teacher }} · 课时：{{ course.learnTime }}</div>
      <div class="meta">章节：{{ course.chapterCount || 0 }} 个</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  course: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.course-card {
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.course-card:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.1);
}
.cover {
  height: 150px;
  background: #e8edf2;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-placeholder {
  color: #909399;
  font-size: 14px;
  padding: 0 8px;
  text-align: center;
}
.info {
  padding: 10px 12px;
}
.name {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.meta {
  font-size: 12px;
  color: #909399;
  line-height: 1.8;
}
</style>
