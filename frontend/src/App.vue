<template>
  <div id="app">
    <Header :isLogin="isLogin" :nickname="userNickname" @logout-state="updateLogout"/>
    <demo-login-modal @submit-login-data="login" @submit-signup-data="signup"/>
    <div class="container-fluid p-0">
    <router-view />
    </div>
  </div>
</template>

<script> 
import Header from './components/common/Header.vue'
import axios from 'axios'
import DemoLoginModal from './components/user/Modal_Login.vue'

const BACK_URL = 'http://localhost:8080'

export default {
  name: 'App',
  components: { 
    Header,
    DemoLoginModal,
  },
  data () {
    return {
      isLogin: false,
      userNickname: '',
      forLogin: {
        id : '',
        password: '',
      }

    } 
  },
  created() {

  },
  mounted() {
    this.ValidateLoggedin()
  },
  methods : {
    setCookie(token) {
      this.$cookies.set('X-AUTH-TOKEN', token)
      this.isLogin = true
    },
    setNickname(nickname, msrl) {
      this.userNickname = nickname
      this.$cookies.set('nickname', nickname)
      this.$cookies.set('msrl', msrl)
    },
    login(loginData) {
      axios.post(`${BACK_URL}/sign/in`, loginData)
        .then(res => {
          this.setCookie(res.data.data1)
          this.isLogin = true
          this.setNickname(res.data.data2.nickname, res.data.data2.msrl)
          // 쿠키 모드
          this.$cookies.set('mode', 'Blog')
          this.$modal.hide('demo-login')
          this.$router.push({ name : 'RecentlyPost' }).catch(() => {})
          // location.reload() // 새로고침이 답인가??
        })
        .catch(err => {
          alert('ID와 비밀번호를 다시 확인해주세요.')
          console.log(err)
        })
    },

    signup(signupData) {
      axios.post(`${BACK_URL}/sign/up`, signupData)
        .then(() => {
          this.forLogin.id = signupData.uid
          this.forLogin.password = signupData.password1
          this.login(this.forLogin)
        })
        .catch(err =>{
          alert('회원가입 실패')
          console.log(err)
        })
    },
    // logout(isLogin) {
    //   this.isLogin = isLogin
    // },

    ValidateLoggedin() {
      if (this.$cookies.isKey('X-AUTH-TOKEN')) {
        this.isLogin = true
        this.userNickname = this.$cookies.get('nickname')
      } else {
        this.isLogin = false
        this.userNickname = ""
      }
    },
    updateLogout(isLoggedIn) {
      this.isLogin = isLoggedIn
      this.userNickname = ''
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
</style>
