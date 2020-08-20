<template>
    <div id="blog" class= "row">
      <!-- 프로필 -->
      <div class="col-12 col-lg-4 px-5 py-4 mt-3">
        <div class="info">
          <a class="" style="cursor:pointer;">
            <img @click="gotoProfile" class="rounded-circle mx-auto img-thumbnail mb-3" :src='cardUserImage' alt="Card image cap" style="width: 90px; height:90px;">
          </a>
            <h3 class=""><strong>{{ this.userInfo.nickname }}</strong> 's Blog</h3>
            <p class="card-list-text">{{ this.userInfo.nickname }}의 브링에 오신것을</p>
            <p class="card-list-text mb-5">환영합니다!</p>

            <!-- 조회수 -->
            <small>TODAY: {{ todayViewers }}</small>
            <span class="vertical-line mx-3"></span>
            <small>TOTAL: {{ totalViewers }}</small>
            <hr>
        </div>
      </div>
    
      <!-- 네비게이션 -->
      <div id="nav" class="col-12 col-lg-8 p-0">
          <router-link :to="{ name: 'MyBlog' , params: { nickname: this.nickname }}"><h3 :ref="'blog-word'" class="d-inline mr-5">포스트</h3></router-link> 
          <router-link :to="{ name: 'MyQuestions' , params: { nickname: this.nickname }}"><h3 :ref="'gisik-word'" class="d-inline mr-5">질문</h3></router-link> 
          <router-link :to="{ name: 'MyAnswers' , params: { nickname: this.nickname }}"><h3 :ref="'answer-word'" class="d-inline mr-5">답변</h3></router-link> 
          <span v-if="userNow === nickname">
            <router-link :to="{ name: 'Myfeeds' , params: { nickname: this.nickname }}"><h3 class="d-inline">피드</h3></router-link>
          </span>
          <!-- 컴포넌트 불러오기 -->
          <div class="container-fluid mt-5">
            <router-view></router-view>
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
    cardImage: {
      type: String,
      default: require("@/assets/img/card.jpg")
    },
  },
  data() {
    return {
      nickname: this.$route.params.nickname,
      userNow: this.$cookies.get('nickname'), 
      userInfo: '',
      cardUserImage: '',
      todayViewers: 0,
      totalViewers: 0,
    }
  },
  methods: {
    // 닉네임으로 유저 정보 get
    getUserInfo(nickname) {
      const config = {
        headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
        }
      }
      axios.get(`${BACK_URL}/member/${nickname}/profile`, config)
        .then(res => {
          this.todayViewers = res.data.list[6].list[0]
          this.totalViewers = res.data.list[6].list[1]
          this.userInfo = res.data.list[0].list[0]
          this.cardUserImage = res.data.list[5].list[0]
        })
        .catch(err => {
          alert('ID와 비밀번호를 다시 확인해주세요.')
          console.log(err)
        })
    },
    gotoProfile() {
      this.$router.push({name : 'Profile', query: { nickname: this.nickname }})
    } 
  },
  created() {
    this.getUserInfo(this.nickname)
  },
  mounted() {
    if (this.$route.path.match('/blog/') && !this.$route.path.match('/blog/my')) {
      this.$refs['blog-word'].style.color = "#56dbc9"
      this.$refs['gisik-word'].style.color = "#2c3e50"
      this.$refs['answer-word'].style.color = "#2c3e50"
    } else if (this.$route.path.match('/blog/myquestions/')) {
      this.$refs['gisik-word'].style.color = "#56dbc9"
      this.$refs['blog-word'].style.color = "#2c3e50"
      this.$refs['answer-word'].style.color = "#2c3e50"
    } else if (this.$route.path.match('/blog/myfeeds')) {
      this.$refs['blog-word'].style.color = "#2c3e50"
      this.$refs['gisik-word'].style.color = "#2c3e50"
      this.$refs['answer-word'].style.color = "#2c3e50"
    } else if (this.$route.path.match('/blog/myanswer')) {
      this.$refs['blog-word'].style.color = "#2c3e50"
      this.$refs['gisik-word'].style.color = "#2c3e50"
      this.$refs['answer-word'].style.color = "#56dbc9"      
    }
  },
  watch: {
    '$route.params.nickname' () {
      location.reload()
    },

    '$route.path' () {
      if (this.$route.path.match('/blog/') && !this.$route.path.match('/blog/my')) {
        this.$refs['blog-word'].style.color = "#56dbc9"
        this.$refs['gisik-word'].style.color = "#2c3e50"
        this.$refs['answer-word'].style.color = "#2c3e50"
      } else if (this.$route.path.match('/blog/myquestions/')) {
        this.$refs['gisik-word'].style.color = "#56dbc9"
        this.$refs['blog-word'].style.color = "#2c3e50"
        this.$refs['answer-word'].style.color = "#2c3e50"
      } else if (this.$route.path.match('/blog/myfeeds')) {
        this.$refs['blog-word'].style.color = "#2c3e50"
        this.$refs['gisik-word'].style.color = "#2c3e50"
        this.$refs['answer-word'].style.color = "#2c3e50"
      } else if (this.$route.path.match('/blog/myanswer')) {
        this.$refs['blog-word'].style.color = "#2c3e50"
        this.$refs['gisik-word'].style.color = "#2c3e50"
        this.$refs['answer-word'].style.color = "#56dbc9"      
      }
    }
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Do+Hyeon&family=Noto+Serif+KR:wght@200;400&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Nanum+Gothic:wght@700&display=swap');

@media only screen and (min-width: 1000px) {
    #blog {
        padding: 0 100px;
        font-family: 'Noto Serif KR', serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        background-color: #f4f4f4;
    }

    #nav {
      padding: 30px;
      margin: 50px 0 0 0;
    }

    .info {
      text-align: left;
    }
}

#blog {
  font: 'Noto Serif KR', serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f4f4f4;
}

#nav {
  padding: 30px;
}

#nav a {
  color: #2c3e50;
  text-decoration: none;
}


#nav a.router-link-exact-active {
  color: #56dbc9;
}

#upsideTerritory{
  background-color: rgb(231, 231, 231);
}


</style>



