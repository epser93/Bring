<template>
    <div id="header">
        <nav class="navbar navbar-expand navbar-light bg-light">
            <router-link v-if="this.mode === 'Blog'" :to="{ name: 'RecentlyPost' }" class="navbar-brand">
                GEESHIQUEEN블로그
            </router-link>
            <router-link v-if="this.mode === 'QnA'" :to="{ name: 'RecentlyQuestion' }" class="navbar-brand">
                GEESHIQUEEN지식인
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

                <!-- 검색창 -->
                <div class="form-inline my-2 my-lg-0">
                    <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-success">{{ keywordType.name }}</button>
                    <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                        <button type="button" tabindex="0" @click="dropdown(typeid, value)" class="dropdown-item" v-for="(value, typeid) in dropdownList" v-bind:key="typeid">
                            {{ value }}
                        </button>
                    </div>
                    <input class="form-control" type="search" v-model="keyword" placeholder="키워드 입력" aria-label="Search">
                    <button class="btn btn-outline-success my-2 my-sm-0" @click="gotoSearch">검색</button>
                </div>
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
        watch: {
          // '$cookies' : {
          //   handler : function () {
          //     if (this.$cookies.get('mode') === 'Blog') {
          //       this.mode = 'Blog'
          //     } else {
          //       this.mode = 'QnA'
          //     }
          //   },
          //   deep: true,
          //   immediate : true
          // }
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
                this.$cookies.remove('mode')
                isLoggedIn = false
                this.$emit("logout-state", isLoggedIn)
                this.$router.push({name : 'Home'}).catch(() => {})
              })
              .catch((err) => {
                console.error(err)
              })
          },
          gotoSearch() {
            if (this.keyword.length !== 0){
              this.$router.push({name : 'Search', query: { q: this.keyword, type: this.keywordType.keyid }})
            }
            
          },
          // 검색 종류 설정
          dropdown(typeid, value) {
              this.keywordType.keyid = typeid
              this.keywordType.name = value
          }
        },
        data() {
          return {
            // 검색 관련
            keyword : "",
            keywordType: {
                name: '제목검색',
                keyid: 1,
            },
            dropdownList: {
                1: '제목검색',
                2: '내용검색',
                3: '작성자검색',
                4: '통합검색',
            },
            mode : this.$cookies.get('mode')
          }
        },

    }
</script>

<style scoped>
#logout {
  cursor: pointer;
}
</style>
