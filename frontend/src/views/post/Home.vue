<template>
  <div class="post">
    <div id="nav" class="mt-4">
      <router-link v-if="mode==='blog'" :to="{ name : 'Home' }"><h4 class="d-inline ml-5">최신글</h4></router-link> 
      <router-link v-if="mode==='blog'" :to="{ name: 'HotPost'}"><h4 class="d-inline ml-5"><b-icon class="mr-2" icon="graph-up" aria-hidden="true"></b-icon>트렌딩</h4></router-link> 
      <router-link v-if="mode==='QnA'" :to="{ name : 'Home' }"><h4 class="d-inline ml-5">최신질문</h4></router-link> 
      <router-link v-if="mode==='QnA'" :to="{ name: 'HotPost'}"><h4 class="d-inline ml-5">인기질문</h4></router-link>
    </div>
    <hr>
    <router-view :mode="mode" :posts="posts"></router-view>
    <div id="modeToggler">
      <button @click="changeMode">{{ modeText }}보러 가기</button>   
    </div>
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
        this.getAllPost()
      } else {
        this.mode = "blog"
        this.modeText = 'QnA'
        this.getAllPost()
      }
    },
    getAllPost () {
      if (this.mode == "blog"){
        axios.get(`${BACK_URL}/blog/recent`)
          .then (res => {
            this.posts = res.data.list
            console.log(this.posts)
          })
          .catch (err => console.log(err))
      } else {
        axios.get(`${BACK_URL}/questions/qlist`)
          .then (res => {
            this.posts = res.data.list
          })
      }
    },
  },
  data: () => {
    return {
      mode: "blog",
      modeText : 'QnA',
      posts: [],
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

#modeToggler {
  display: flex;
  justify-content: flex-end;
  margin-right : 60px;
}

#modeToggler button {
  border: none;
  background-color: #fff;
  color: #000;
  font-weight: 500;
  border-radius: 45px;
  box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
  height: 45px;
  width: 140px;
  outline: none;
  position: fixed;
  bottom : 0;
  margin-bottom: 30px;
  transition: all 0.3s ease 0s;
}

#modeToggler button:hover {
  background-color: #2EE59D;
  box-shadow: 0px 15px 20px rgba(46, 229, 157, 0.4);
  color: #fff;
  transform: translateY(-7px);
}

.hidden {
  display: none;
}
</style>