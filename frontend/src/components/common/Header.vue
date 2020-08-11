<template>
    <div id="header">
        <nav class="navbar navbar-expand navbar-light bg-light">
            <router-link :to="{ name: 'Home' }" class="navbar-brand">
                GEESHIQUEEN
            </router-link>

            <div class="collapse navbar-collapse" id="navbarTogglerDemo03">
                <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
                    <!-- <li class="nav-item">
                        <router-link v-if="!isLogin" :to="{ name: 'Login' }" class="nav-link">로그인</router-link>
                    </li> -->
                    <li class="nav-item">
                        <a href='#' v-if="!isLogin" @click="$modal.show('demo-login')" class="nav-link">LOGIN</a>
                    </li>
                    <li class="nav-item">
                        <router-link v-if="!isLogin" :to="{ name: 'Signup' }" class="nav-link">회원가입</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link v-if="isLogin" :to="{ name: 'Profile', query: { nickname: this.nickname }}" class="nav-link">프로필</router-link> 
                    </li>
                    <li class="nav-item">
                        <router-link v-if="isLogin" :to="{ name: 'MyBlog', params: { nickname: this.nickname }}" class="nav-link">블로그</router-link> 
                    </li>
                    <li class="nav-item">
                        <router-link v-if="isLogin" :to="{ name: 'Question' }" class="nav-link">지식인</router-link> 
                    </li>                    
                    <li class="nav-item" id="logout">
                        <p v-if="isLogin" @click="logout" class="nav-link">로그아웃</p> 
                    </li>
                </ul>
                <form class="form-inline my-2 my-lg-0">
                    <input class="form-control mr-sm-2" v-model="keyword" type="text" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
                </form>
            </div>
        </nav>
        
    </div>
</template>   

<script> 
    const BACK_URL = 'http://localhost:8080'
    import axios from 'axios'

    export default {
        name: 'Header',
        components: { 
            
        },
        props: {
          isLogin : Boolean,
          nickname: String,
        },
        created() {
          // console.log(this.isLogin)
        },
        methods : {
          logout() {
            let isLoggedIn = this.isLogin
            const config = {
              headers: {
                'Authorization' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.post(`${BACK_URL}/sign/out`, null, config)
              .then(() =>{
                this.$cookies.remove('X-AUTH-TOKEN')
                this.$cookies.remove('nickname')
                isLoggedIn = false
                this.$emit("logout-state", isLoggedIn)
                this.$router.push({name : 'Home'}).catch(() => {})
              })
              .catch((err) => {
                console.error(err)
              })
          },
        },
        data() {
          return {
            keyword : "",
          }
        },

    }
</script>

<style scoped>
#logout {
  cursor: pointer;
}
</style>
