<template>
  <div class="post">
    <div id="nav" class="mt-4">
      <router-link v-if="mode==='blog'" :to="{ name : 'Home' }"><h4 class="d-inline ml-5">최신글</h4></router-link> 
      <router-link v-if="mode==='blog'" :to="{ name: 'HotPost'}"><h4 class="d-inline ml-5"><b-icon class="mr-2" icon="graph-up" aria-hidden="true"></b-icon>트렌딩</h4></router-link> 
      <router-link v-if="mode==='QnA'" to="#"><h4 class="d-inline ml-5">최신질문</h4></router-link> 
      <router-link v-if="mode==='QnA'" to="#"><h4 class="d-inline ml-5">인기질문</h4></router-link>
    </div>
    <hr>
    <router-view :mode="mode" :posts="posts"></router-view>
    <button @click="changeMode">{{ modeText }}보러 가기</button>   
  </div>
</template>
 
<script>
const BACK_URL = 'http://localhost:8080'
import axios from 'axios'

export default {
  name:"Home",
  components:{
  },
  watch: {
  },
  created() {
    this.getAllPost()
  },
  methods: {
    changeMode () {
      if (this.mode == "blog") {
        this.mode = "QnA"
        this.modeText = 'blog'
      } else {
        this.mode = "blog"
        this.modeText = 'QnA'
      }
    },
    getAllPost () {
      axios.get(`${BACK_URL}/search/all_blog_posts`)
        .then (res => {
          this.posts = res.data.list
        })
        .catch (err => console.log(err))
    }
  },
  data: () => {
    return {
      mode: "blog",
      modeText : 'QnA',
      posts: []
    }
  }
}
</script>
<style scoped>
#nav {
  text-align: left;
  padding: 0
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
}

#nav a.router-link-exact-active {
  color: #42b983;
}

.hidden {
  display: none;
}
</style>