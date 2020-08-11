<template>
  <div class="post">
    <div id="nav" class="mt-4">
      <router-link v-if="mode==='blog'" :to="{ name : 'Home' }"><h4 class="d-inline ml-5">최신글</h4></router-link> 
      <router-link v-if="mode==='blog'" :to="{ name: 'HotPost'}"><h4 class="d-inline ml-5"><b-icon class="mr-2" icon="graph-up" aria-hidden="true"></b-icon>트렌딩</h4></router-link> 
      <router-link v-if="mode==='QnA'" :to="{ name : 'Home' }"><h4 class="d-inline ml-5">최신질문</h4></router-link> 
      <router-link v-if="mode==='QnA'" :to="{ name: 'HotPost'}"><h4 class="d-inline ml-5">인기질문</h4></router-link>
    </div>
    <hr>
    <router-view :mode="mode" :posts="posts" :thumbnail="thumbnail"></router-view>
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
        // 에러 헨들링
        this.$router.push({ name: 'Home' }).catch(()=>{})
        this.getAllPost()
      } else {
        this.mode = "blog"
        this.modeText = 'QnA'
        this.$router.push({ name: 'Home' }).catch(()=>{})
        this.getAllPost()
      }
    },
    getAllPost () {
      if (this.$cookies.get('X-AUTH-TOKEN')) {
        const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        if (this.mode == "blog"){
          axios.get(`${BACK_URL}/blog/recent`, config)
            .then (res => {
              this.posts = res.data.list[0].list
              if (res.data.list[2].list[0]) {
                this.thumbnail = res.data.list[2].list
              }
              // console.log(this.thumbnail)
            })
            .catch (err => console.log(err))
        } else {
          axios.get(`${BACK_URL}/questions/recent`, config)
            .then (res => {
              this.posts = res.data.list[0].list
            })
        }
      } else {
        if (this.mode == "blog"){
          axios.get(`${BACK_URL}/blog/recent`)
            .then (res => {
              this.posts = res.data.list[0].list
              if (res.data.list[2].list[0]) {
                this.thumbnail = res.data.list[2].list
              } 
            })
            .catch (err => console.log(err))
        } else {
          axios.get(`${BACK_URL}/questions/recent`)
            .then (res => {
              this.posts = res.data.list
            })
        }
      }
    },
  },
  data: () => {
    return {
      mode: "blog",
      modeText : 'QnA',
      posts: [],
      thumbnail: []
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