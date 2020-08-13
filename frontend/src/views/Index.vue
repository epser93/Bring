<template>
  <div>
    index page
    <router-link :to="{ name : 'RecentlyPost' }">시작하기</router-link>
    <!-- <a href="http://localhost:8081/postqna/recentlypost">시작하기2</a> -->
  </div>
</template>

<script>
export default {
  name: "Index",
  created () {
    this.$cookies.set('mode', 'Blog')
  }

}
</script>

<style>

</style>