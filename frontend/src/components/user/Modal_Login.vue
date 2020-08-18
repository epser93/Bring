<template>

<modal v-if="true" name="demo-login" transition="pop-out" :width="380" :focus-trap="true" :height="550" class="wrap">
  <div class="form-box">
      <div class="button-box">
        <div id="btn"></div>
        <button type="button" class="toggle-btn" @click="loginToggle">Login</button>
        <button type="button" class="toggle-btn" @click="registerToggle">Register</button>
      </div>
      <div class="social-icons">
        <i class="fab fa-twitter fa-3x"></i>
        <i class="fab fa-facebook-square fa-3x"></i>
        <i class="fab fa-google fa-3x"></i>
      </div>
      <form id="login" class="input-group">
        <input type="text" class="input-field" placeholder="User Id" v-model="loginData.id" required>
        <input type="password" class="input-field" placeholder="Enter Password" v-model="loginData.password" required>
        <button type="submit" id="login-btn" class="submit-btn" @click.prevent="login">Login</button>
      </form>
      <div id="register" class="input-group">
        <input type="email" class="input-field" placeholder="Email Id" v-model="signupData.uid" required>
        <input type="text" class="input-field" placeholder="User Name" v-model="signupData.name" required>
        <input type="text" class="input-field" placeholder="User Nickname" v-model="signupData.nickname" required>
        <input type="password" class="input-field" placeholder="Enter Password" v-model="signupData.password1" required>
        <input type="password" class="input-field" placeholder="Check Password" v-model="signupData.password2" required>
        <button type="submit" id="register-btn" class="submit-btn" @click.prevent="signup" >Register</button>
      </div>
    </div>
</modal>
</template>
<script>
export default {

  name: 'LoginModal',
  data() {
    return {
      loginData: {
        id: '',
        password: '',
      },
      signupData: {
        name: '',
        uid : '',
        nickname: '',
        password1: '',
        password2: ''
      }
    }
  },
  created() {
  },
  methods: {
    loginToggle() {
      const x = document.getElementById("login");
      const y = document.getElementById("register");
      const z = document.getElementById("btn")
      x.style.left = "50px";
      y.style.left = "450px";
      z.style.left = "0"
    },
    registerToggle() {
      const x = document.getElementById("login");
      const y = document.getElementById("register");
      const z = document.getElementById("btn")
      x.style.left = "-400px";
      y.style.left = "50px";
      z.style.left = "110px"
    },
    login() {
      this.$emit('submit-login-data', this.loginData)
    },
    signup() {
      this.$emit('submit-signup-data', this.signupData)
    },
  }
}
</script>
<style scoped>
* {
  margin : 0;
  padding: 0;
  font-family: sans-serif;
}
.wrap {
  /* background-color: black; */
  position: absolute;
}

.form-box {
  width: 380px;
  height: 550px;
  position: relative;
  margin: 6% auto;
  background: white;
  padding: 5px;
  overflow: hidden;
}

.button-box {
  width: 220px;
  margin: 2px auto;
  position: relative;
  box-shadow: 0 0 20px 9px #ff61241f;
  border-radius: 30px;
}

.toggle-btn {
  padding: 10px 30px;
  cursor: pointer;
  background: transparent;
  border : 0;
  outline : none;
  position: relative;
}

#btn {
  top: 0;
  left: 0;
  position: absolute;
  width: 110px;
  height: 100%;
  background : linear-gradient(to right, #ff105f, #ffad06);
  border-radius: 30px;
  transition: .5s;
}

.social-icons {
  margin: 30px auto;
  text-align: center;
}
.social-icons svg{
  width : 30px;
  margin: 0 12px;
  /* box-shadow: 0 0 20px 0 #7f7f7f3d; */
  background: white;
  cursor: pointer;
  /* border-radius: 50%; */
}
.input-field {
  width :100%;
  padding : 10px 0;
  margin: 5px 0;
  border-left : 0;
  border-top : 0;
  border-right : 0;
  border-bottom: 1px solid #999;
  outline: none;
  background: transparent;
}

.submit-btn {
  width : 85%;
  padding: 10px 30px;
  cursor: pointer;
  display: block;
  margin: auto;
  background: linear-gradient(to right, #ff105f, #ffad06);
  border : 0;
  outline: none;
  border-radius: 30px;
}

.check-box {
  margin: 30px 10px 30px 0;
}
span {
  color: #777;
  font-size: 12px;
  bottom : 68px;
  position: absolute;
}

#login {
  left : 50px;
  top: 180px;
  position: absolute;
  width: 280px;
  transition: .5s;
}

#register {
  left : 450px;
  top: 140px;
  position: absolute;
  width: 280px;
  transition: .5s;
}

#login-btn {
  margin-top: 40px;
}

#register-btn {
  margin-top : 30px
}

.input-field:focus {
  border-bottom : 2px solid #ffad06;
  transition: .3s;
}
</style>