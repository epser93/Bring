<template>

<modal v-if="true" name="demo-login" transition="pop-out" :width="modalWidth" :focus-trap="true" :height="400">
  <div class="box">
    <div class="box-part" id="bp-left">
      <div class="partition" id="partition-register">
        <div class="partition-title">LOGIN</div>
        <div class="partition-form">
          <form autocomplete="false">

            <div class="autocomplete-fix">
              <input disabled type="password">
            </div>
            <input id="n-email" type="text" placeholder="이메일을 입력해주세요" v-model="loginData.id">
            <input id="n-password2" type="password" placeholder="영문, 숫자 혼용 8자 이상" v-model="loginData.password">
          </form>

          <div class="button-set">
            <button id="goto-signin-btn" @click="login">Sign In</button>


            <button id="register-btn" @click="register">Register</button>

          </div>

          <div style="margin-top: 20px">
          </div>

          <button class="large-btn naver-btn" @click="loginNaver">connect with <span>naver</span></button>
          <button class="large-btn github-btn" @click="loginGithub">connect with <span>github</span></button>
          <button class="large-btn facebook-btn" @click="loginFacebook">connect with <span>facebook</span></button>
        
        </div>
      </div>
    </div>
    <div class="box-part" id="bp-right">
      <div class="box-messages">
      </div>
    </div>
  </div>
</modal>
</template>
<script>
const MODAL_WIDTH = 656
export default {

  name: 'LoginModal',
  data() {
    return {
      modalWidth: MODAL_WIDTH,
      loginData: {
        id: '',
        password: '',
      },
      Lmodal: false
    }
  },
  created() {
    this.modalWidth =
      window.innerWidth < MODAL_WIDTH ? MODAL_WIDTH / 2 : MODAL_WIDTH
  },
  methods: {
    openModal() {
      this.Lmodal = false
    },
    closeModal() {
      this.Lmodal = false
    },
    // signIn() {
    //   alert(this.loginData.useremail+"//"+this.loginData.password)
    // },
    login() {
      // alert(this.loginData.email+"//"+this.loginData.password)
      this.$emit('submit-login-data', this.loginData)
    },
    register() {
        // this.$router.push({name:constants.URL_TYPE.USER.JOIN})
        this.closeModal()

         console.log('dfsdfsdfsdfsd')

    },
    loginNaver() {
      alert('naver')
      // this.$emit(`submit-login-data`, this.loginData)
    },
    loginFacebook() {
      alert('facebook')
      // this.$emit(`submit-login-data`, this.loginData)
    },
    loginGithub() {
      alert('github')
      // this.$emit(`submit-login-data`, this.loginData)
    }

  }
}
</script>
<style lang="scss">
$background_color: #404142;
$github_color: #dba226;
$facebook_color: #3880ff;
$naver_color: #1EC800;
.box {
  background: white;
  overflow: hidden;
  width: 656px;
  height: 400px;
  border-radius: 2px;
  box-sizing: border-box;
  box-shadow: 0 0 40px black;
  color: #8b8c8d;
  font-size: 0;
  .box-part {
    display: inline-block;
    position: relative;
    vertical-align: top;
    box-sizing: border-box;
    height: 100%;
    width: 50%;
    &#bp-right {
      background: url('/static/panorama.jpg') no-repeat top left;
      border-left: 1px solid #eee;
    }
  }
  .box-messages {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
  }
  .box-error-message {
    position: relative;
    overflow: hidden;
    box-sizing: border-box;
    height: 0;
    line-height: 32px;
    padding: 0 12px;
    text-align: center;
    width: 100%;
    font-size: 11px;
    color: white;
    background: #f38181;
  }
  .partition {
    width: 100%;
    height: 100%;
    .partition-title {
      box-sizing: border-box;
      padding: 30px;
      width: 100%;
      text-align: center;
      letter-spacing: 1px;
      font-size: 20px;
      font-weight: 300;
    }
    .partition-form {
      padding: 0 20px;
      box-sizing: border-box;
    }
  }
  input[type='password'],
  input[type='text'] {
    display: block;
    box-sizing: border-box;
    margin-bottom: 4px;
    width: 100%;
    font-size: 12px;
    line-height: 2;
    border: 0;
    border-bottom: 1px solid #dddedf;
    padding: 4px 8px;
    font-family: inherit;
    transition: 0.5s all;
  }
  button {
    background: white;
    border-radius: 4px;
    box-sizing: border-box;
    padding: 10px;
    letter-spacing: 1px;
    font-family: 'Open Sans', sans-serif;
    font-weight: 400;
    min-width: 140px;
    margin-top: 8px;
    color: #8b8c8d;
    cursor: pointer;
    border: 1px solid #dddedf;
    text-transform: uppercase;
    transition: 0.1s all;
    font-size: 10px;
    &:hover {
      border-color: mix(#dddedf, black, 90%);
      color: mix(#8b8c8d, black, 80%);
    }
  }
  .large-btn {
    width: 100%;
    background: white;
    span {
      font-weight: 600;
    }
    &:hover {
      color: white !important;
    }
  }
  .button-set {
    margin-bottom: 8px;
  }
  #register-btn,
  #signin-btn {
    margin-left: 8px;
  }
  .facebook-btn {
    border-color: $facebook_color;
    color: $facebook_color;
    &:hover {
      border-color: $facebook_color;
      background: $facebook_color;
    }
  }
  .github-btn {
    border-color: $github_color;
    color: $github_color;
    &:hover {
      border-color: $github_color;
      background: $github_color;
    }
  }
  .naver-btn {
    border-color: $naver_color;
    color: $naver_color;
    &:hover {
      border-color: $naver_color;
      background: $naver_color;
    }
  }
  .autocomplete-fix {
    position: absolute;
    visibility: hidden;
    overflow: hidden;
    opacity: 0;
    width: 0;
    height: 0;
    left: 0;
    top: 0;
  }
}
.pop-out-enter-active,
.pop-out-leave-active {
  transition: all 0.5s;
}
.pop-out-enter,
.pop-out-leave-active {
  opacity: 0;
  transform: translateY(24px);
}

</style>