<template>
    <div id="blog">
      <!-- 프로필 -->
      <div class="row">
        <div class="offset-3 col-6">
          <a class="d-inline" href=''>
            <img class="rounded-circle mx-auto img-thumbnail mb-3" :src='cardUserImage' alt="Card image cap" style="width: 150px;">
          </a>
            <h5 class="d-inline ml-3">{{ this.nickname }}</h5>
            <h6>follower 수:  </h6>
            <hr>
        </div>
      </div>

      <div class="row mt-3">
        <!-- 네비게이션 -->
        <div id="nav" class="col-12 ml-5">
            <router-link :to="{ name: 'MyBlog' , params: { nickname: this.nickname }}"><h3 class="d-inline mr-5">글</h3></router-link> 
            <router-link :to="{ name: 'MyQuestions' , params: { nickname: this.nickname }}"><h3 class="d-inline mr-5">질문</h3></router-link> 
            <router-link :to="{ name: 'MyAnswers' , params: { nickname: this.nickname }}"><h3 class="d-inline mr-5">답변</h3></router-link>
            
            <!-- 컴포넌트 불러오기 -->
            <div class="container-fluid mt-5">
              <router-view></router-view>
            </div>
        </div>
      </div>
    </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

export default {
    name: 'MyBlog',
    components: {

    },
    props: {
    cardUserImage: {
      type: String,
      default: require("@/assets/img/faces/marc.jpg")
    },
    cardImage: {
      type: String,
      default: require("@/assets/img/card.jpg")
    },
  },
  data() {
    return {
      // 닉네임 라우터 매개변수로 받아오기
      nickname: this.$route.params.nickname,
    }
  },
  methods: {
    // 닉네임으로 유저 정보 get
    getUserInfo() {
      axios.get(`${BACK_URL}/member/user/${this.nickname}`)
        .then(res => {
          this.userInfo = res.data
        })
        .catch(err => {
          alert('ID와 비밀번호를 다시 확인해주세요.')
          console.log(err)
        })
    }
    
  },
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
}

#nav a.router-link-exact-active {
  color: #42b983;
}

</style>



