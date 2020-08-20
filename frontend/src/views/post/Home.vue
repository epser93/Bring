<template>
  <div id="home">
    <div id="nav" class="">
      <router-link v-if="this.mode==='Blog'" :to="{ name : 'RecentlyPost' }"><h4 class="d-inline ml-5">최신글</h4></router-link> 
      <router-link v-if="this.mode==='Blog'" :to="{ name: 'HotPost'}"><h4 class="d-inline ml-5"><b-icon class="mr-2" icon="graph-up" aria-hidden="true"></b-icon>트렌딩</h4></router-link> 
      <router-link v-if="this.mode==='QnA'" :to="{ name : 'RecentlyQuestion' }"><h4 class="d-inline ml-5">최신질문</h4></router-link> 
      <router-link v-if="this.mode==='QnA'" :to="{ name: 'TrendQuestion'}"><h4 class="d-inline ml-5">인기질문</h4></router-link>
    </div>
    <hr class="mt-0">
    <router-view  ></router-view>
    <div class="Buttons">
      <button id="modeToggler" @click="changeMode">{{ this.modeText }} 보러 가기</button>
      <button id="upScroll" @click="upScroll"><i class="fas fa-arrow-up"></i></button>
    </div>
  </div>
</template>
 
<script>
// const BACK_URL = 'http://i3c206.p.ssafy.io/api'
// import axios from 'axios'

export default {
  name:"Home",
  components:{
  },
  created() {
   
  },
  watch: {
    '$route.name' : {
      handler :function() {
        if (this.$cookies.get('mode') === 'Blog') {
          this.mode = 'Blog'
          this.modeText = 'QnA'
        } else {
          this.mode = 'QnA'
          this.modeText = 'Blog'
        }
      },
      deep : true,
      immediate : true
    }
  },
  methods: {
    changeMode () {
      if (this.$cookies.get('mode') == "Blog") {
        this.$cookies.set('mode', 'QnA')
        this.mode = 'Blog'
        // 에러 헨들링
        this.$router.push({ name: 'RecentlyQuestion' }).catch(()=>{})
        // this.getAllPost()
      } else {
        this.$cookies.set('mode',"Blog")
        this.mode = 'QnA'
        this.$router.push({ name: 'RecentlyPost' }).catch(()=>{})
        // this.getAllPost()
      }
    },
    upScroll () {
      window.scrollTo(0,0)
    }
  },
  data: () => {
    return {
      mode : '',
      modeText : 'QnA'
    }
  }
}
</script>
<style scoped>
@media only screen and (min-width: 1000px) {
    #home {
        min-height: 1000px;
        padding: 0 50px;
        font-family: 'Noto Serif KR', serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        background-color: #f4f4f4;
    }
}

#home {
    min-height: 1000px;
    padding: 0 50px;
    font-family: 'Noto Serif KR', serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    background-color: #f4f4f4;
}

#nav {
  font-family: 'Noto Serif KR', serif;
  background-color: #f4f4f4;
  margin: 0;
  padding: 20px 0;
  text-align: left;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
}

#nav a.router-link-exact-active {
  color: #56dbc9;
}

.Buttons {
  display: flex;
  justify-content: flex-end;
  margin-right : 30px;
}

#modeToggler {
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

#upScroll {
  border: none;
  background-color: #fff;
  color: #000;
  padding: 10px 18px 10px 18px;
  border-radius: 100%;
  box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
  outline: none;
  position: fixed;
  bottom : 0;
  right: 20px;
  margin-right: 0;
  margin-bottom: 30px;
  transition: all 0.3s ease 0s;
}

#modeToggler:hover,#upScroll:hover {
  background-color: #56dbc9;
  box-shadow: 0px 10px 15px #2a6d64;
  color: #fff;
  transform: translateY(-7px);
}

</style>