<template>
  <div id="app">
    <div v-if="$route.meta.header === 1">
    </div>
    <div v-else>
      <Header :isLogin="isLogin" :nickname="userNickname" @logout-state="updateLogout"/>
    </div>
    <div class="container-fluid p-0">
      <router-view />
    </div>
    <demo-login-modal @submit-login-data="login" @submit-signup-data="validateSignupData"/>
  </div>
</template>

<script> 
import Header from './components/common/Header.vue'
import axios from 'axios'
import DemoLoginModal from './components/user/Modal_Login.vue'

const BACK_URL = 'http://i3c206.p.ssafy.io'

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
    validateSignupData(signupData) {
      console.log('hihi')
      const passwordchk = /^.*(?=^.{8,15}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/;
      const emailchk = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
      const namechk = /^[가-힣a-zA-Z]{2,5}$/;
      const nicknamechk = /^[가-힣a-zA-Z0-9]{2,15}$/;
      // 회원가입 이메일 형식 체크
      if (!emailchk.test(signupData.uid)) {
        alert('아이디가 이메일 형식이 아닙니다.')
      }
      // 이름 검증
      else if (!namechk.test(signupData.name)) {
        alert('이름은 2~5글자 이내 / 문자 이외는 입력할 수 없습니다.')
      }
      // 닉네임 검증
      else if (!nicknamechk.test(signupData.nickname)) {
        alert('닉네임은 2~15자 이내여야 합니다.')
      }
      // 비밀번호가 문자+숫자 8~20자리 체크
      else if (!passwordchk.test(signupData.password1)) {
        alert("비밀번호는 문자+숫자+특수문자 8~20 자리입니다.")
      }
      else if (signupData.password1 != signupData.password2) {
        alert('비밀번호와 확인란이 서로 다릅니다.')
      }
      else (this.signup(signupData))
    },

    signup(signupData) {
      console.log(signupData)
      axios.post(`${BACK_URL}/sign/up`, signupData)
        .then(() => {
          this.forLogin.id = signupData.uid
          this.forLogin.password = signupData.password1
          this.login(this.forLogin)
        })
        .catch(err =>{
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
  font-family: 'Noto Serif KR', serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale; 
  text-align: center;
  color: #2c3e50;
  height:100vh
  
}
</style>
