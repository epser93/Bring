<template>
    <div id="header">
        <nav class="navbar navbar-expand-lg p-0 m-0">
            <router-link v-if="this.mode === 'Blog'" :to="{ name: 'RecentlyPost' }" class="navbar-brand p-3 ml-3">
                BD BLOG
            </router-link>
            <router-link v-if="this.mode === 'QnA'" :to="{ name: 'RecentlyQuestion' }" class="navbar-brand p-3 ml-3">
                BD GISIK
            </router-link>
            
            <a class="navbar-toggler" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
              <span><i class="fas fa-sliders-h"></i></span>
            </a>
            <div class="collapse navbar-collapse m-0" id="navbarSupportedContent">
                
                <ul class="navbar-nav mr-auto text-left">
                  <span class="vertical-line ml-3"></span>
                    <li class="nav-item ml-5">
                        <a href='#' v-if="!isLogin" @click="$modal.show('demo-login')" class="nav-link">LOGIN</a>
                    </li>

                    <li class="nav-item ml-5">
                        <router-link v-if="isLogin" :to="{ name: 'Profile', query: { nickname: this.nickname }}" class="nav-link">PROFILE</router-link> 
                    </li>

                    <li class="nav-item ml-5">
                        <router-link v-if="isLogin" :to="{ name: 'MyBlog', params: { nickname: this.nickname }}" class="nav-link">BRING</router-link> 
                    </li>  
                    <li class="nav-item ml-5">
                        <router-link v-if="isLogin" :to="{ name: 'About' }" class="nav-link">ABOUT</router-link> 
                    </li>  
                    <li class="nav-item ml-5" id="logout">
                        <p v-if="isLogin" @click="logout" class="nav-link">LOGOUT</p> 
                    </li>
                </ul>

                <!-- 검색창 -->
                <div v-if="this.mode === 'Blog'" class="form-inline ml-5 my-2">
                    <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-secondary">{{ keywordType.name }}</button>
                    <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                        <button type="button" tabindex="0" @click="dropdown(typeid, value)" class="dropdown-item" v-for="(value, typeid) in dropdownList" v-bind:key="typeid">
                            {{ value }}
                        </button>
                    </div>
                    <input type="search" v-model="keyword" placeholder="전체 글 검색" @keydown.enter="gotoSearch" aria-label="Search">
                    <a class="mx-3" @click="gotoSearch"><i class="fas fa-search"></i></a>
                </div>

                <!-- 질문 검색창 -->
                <div v-if="this.mode === 'QnA'" class="form-inline ml-5 my-2">
                    <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-secondary">{{ keywordType.name }}</button>
                    <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                        <button type="button" tabindex="0" @click="dropdown(typeid, value)" class="dropdown-item" v-for="(value, typeid) in dropdownList" v-bind:key="typeid">
                            {{ value }}
                        </button>
                    </div>
                    <input type="search" v-model="keyword" placeholder="전체 질문 검색" @keydown.enter="gotoSearchQuestions" aria-label="Search">
                    <a class="mx-3" @click="gotoSearchQuestions"><i class="fas fa-search"></i></a>
                </div>                
            </div>
        </nav>
        <hr class="m-0">
        
    </div>
</template>   

<script> 
    const BACK_URL = 'http://i3c206.p.ssafy.io/api'
    import axios from 'axios'

    export default {
        name: 'Header',
        components: { 
            
        },
        watch: {
          '$route.name' : {
            handler :function() {
              if (this.$cookies.get('mode') === 'Blog') {
                this.mode = 'Blog'
              } else {
                this.mode = 'QnA'
              }
            },
            deep : true,
            immediate : true
          }
        },
        props: {
          isLogin : Boolean,
          nickname: String,
        },
        created() {
          this.mode = this.$cookies.get('mode')
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
                this.$cookies.set('mode', 'Blog')
                isLoggedIn = false
                this.$emit("logout-state", isLoggedIn)
                this.$router.push({name : 'RecentlyPost'}).catch(() => {})
              })
              .catch((err) => {
                console.error(err)
              })
          },

          // 블로그 글 검색
          gotoSearch() {
            if (this.keyword.length !== 0){
              this.$router.push({name : 'Search', query: { q: this.keyword, type: this.keywordType.keyid }})
              this.keyword = ''
            } 
          },

          // 질문 글 검색
          gotoSearchQuestions() {
            if (this.keyword.length !== 0){
              this.$router.push({name : 'SearchQuestions', query: { q: this.keyword, type: this.keywordType.keyid }})
              this.keyword = ''
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

.navbar a:visited{
    color: #494949;
}

.navbar li {
  color:  #494949;
}

.navbar p {
  color: #494949;
}

.navbar p:hover {
  color: #56dbc9;
  transition-duration: 0.3s;
}

.navbar a{
  height: 100%;
  color: gray;
}

input {
    width:210px;
    height: auto; /* 높이값 초기화 */ 
    line-height : normal; /* line-height 초기화 */ 
    padding: .7em .5em; /* 원하는 여백 설정, 상하단 여백으로 높이를 조절 */ 
    font-family: inherit; /* 폰트 상속 */ 
    border: 1px solid rgb(202, 202, 202); 
    border-radius: 0; /* iSO 둥근모서리 제거 */ 
    outline-style: none; /* 포커스시 발생하는 효과 제거를 원한다면 */ 
    -webkit-appearance: none; /* 브라우저별 기본 스타일링 제거 */ 
    -moz-appearance: none; appearance: none;
}

input:hover {
  border: 1px solid #56dbc9;
  text-decoration: none;
  transition-duration: 0.3s;
}

button {
    height: auto; /* 높이값 초기화 */ 
    line-height : normal; /* line-height 초기화 */ 
    padding: .7em .5em; /* 원하는 여백 설정, 상하단 여백으로 높이를 조절 */ 
    font-family: inherit; /* 폰트 상속 */ 
    border-radius: 0; /* iSO 둥근모서리 제거 */ 

}

button:hover {
  background-color: #56dbc9;
  border: 1px solid #56dbc9;
}

button:focus {
  background-color: #56dbc9 !important;
  border: 1px solid #56dbc9 !important;
}

.dropdown-menu {
  left:auto;
}

.navbar a:hover {
  cursor: pointer;
  color: #56dbc9;
  text-decoration: none;
  transition-duration: 0.3s;
}
.dropdown-menu {
  left:auto;
}
</style>
