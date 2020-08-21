<template>
    <div id="mylist" class="row">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css">
 
        <!-- 컴포넌트 불러오기 -->
        <div class="container-fluid col-12 col-lg-7 mr-5">
            <router-view></router-view>
        </div>

        <!-- 사이드 바 -->
        <div id="nav-mylist" class="col-12 col-lg-4 flex-column text-left px-3">
            <!-- 검색창 -->
            <div class="mb-5 text-center">
                <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-secondary" style="margin-bottom:5px;">{{ keywordType.name }}</button>
                <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                    <button type="button" tabindex="0" @click="dropdown(typeid, value)" class="dropdown-item" v-for="(value, typeid) in dropdownList" v-bind:key="typeid">
                        {{ value }}
                    </button>
                </div>
                <input v-model="keyword" placeholder="블로그 내 검색" aria-label="Search">
                <a class="mx-2">
                    <router-link @click.native="setTotalPageNum(-1)"  :to="{ name: 'MyBlog' , query : { k: keyword, type: this.keywordType.keyid }}"><i class="fas fa-search"></i></router-link>
                </a>
            </div>

            <!-- 카테고리 목록 -->
            <div class="category-manager d-flex justify-content-between">
                <h5>카테고리</h5>
                <!-- 카테고리 버튼 -->
                <a v-if="userNow === nickname" @click="newCategory"><i class="fas fa-folder mr-2"></i>관리</a>   
            </div>
            <hr class="mb-4">
            <div class="px-5">
                <router-link @click.native="setTotalPageNum(numOfPosts)" :to="{ name: 'MyBlog' , params: { nickname: this.nickname }}">전체보기 <span class="float-right">({{ numOfPosts }})</span><hr></router-link> 
                <div class="m-0 p-0" v-for="category in categoryList" :key="category.name">
                    <router-link @click.native="setTotalPageNum(category.postCnt)" :to="{ name: 'MyBlog' , query : { c: category.name }}">{{ category.name }}<span class="float-right">({{ category.postCnt }})</span><hr></router-link> 
                </div>
            </div>
            
            <!-- 태그 리스트 -->
            <h5 class="mt-5">태그</h5>
            <div class="tagcloud mt-3">
                <div v-for="(tag,index) in this.tagList" :key="index" class="d-inline">
                    <router-link @click.native="setTotalPageNum(tagNum[index])" :to="{ name: 'MyBlog' , query : { t: tag }}" class="tag-cloud-link ">{{ tag }}</router-link>
                </div>
            </div>
        </div>

        <!-- 페이지네이션 -->
        <div class="col-12">
            <BlogPagination :totalNum="totalNum" @pageNum="sendCurrentPage"/>
        </div>
    </div>
</template>

<script>
import axios from 'axios'
import BlogPagination from '../blog/BlogPagination.vue'
import { EventBus } from '../../event-bus.js'

const BACK_URL = 'http://i3c206.p.ssafy.io:8080/api'

export default {
    name: 'MyList',
    components: {
        BlogPagination,
    }, 
    props: {

    },

    methods: {
        // 카테고리 관리
        newCategory() {
            this.$router.push(`/blog-category/${this.nickname}`)
        },

        getCategory() {
            axios.get(`${BACK_URL}/blog/${this.nickname}/categories`)
                .then(res => {
                    this.categoryList = res.data.list[0].list
                    // 전체 개시물 숫자 카운트
                    this.calPostsSum()
                })
                .catch(err => {
                    console.log(err)
                })
        },

        // 검색 종류 설정
        dropdown(typeid, value) {
            this.keywordType.keyid = typeid
            this.keywordType.name = value
        },

        calPostsSum() {
            for (const item in this.categoryList) {
                this.numOfPosts = this.numOfPosts + this.categoryList[item].postCnt
                this.totalNum = this.numOfPosts
            }
        },

        getTags() {
            axios.get(`${BACK_URL}/tags/blog/${this.nickname}`)
                .then(res => {
                    this.tagList = res.data.list[0].list
                    this.tagNum = res.data.list[1].list
                })
                .catch(err => {
                    console.log(err)
                })
        },
        // blogPosts.vue로 페이지 넘버 보내기(이벤트버스)
        sendCurrentPage(currentPage) {
            EventBus.$emit("paging", currentPage)
        },
        setTotalPageNum(num) {
            this.totalNum = num
        }       
    },
    
    mounted() {
        this.getCategory(),
        this.getTags()
    },
    data() {
        return{
            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,
            
            // 카테고리 관련
            categoryList: [],
            numOfPosts: 0,
            
            // 검색 관련
            keywordType: {
                name: '제목검색',
                keyid: 1,
            },
            keyword: '',
            dropdownList: {
                1: '제목검색',
                2: '내용검색',
                3: '통합검색',
            },

            // 태그 관련
            tagList: [],
            tagNum: [],

            // 페이지네이션 관련
            totalNum : this.numOfPosts,

        }
    },
}
</script>

<style scoped>


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

.tagcloud {
  padding: 0; }
  .tagcloud a {
    text-transform: uppercase;
    display: inline-block;
    padding: 4px 10px;
    margin-bottom: 7px;
    margin-right: 4px;
    border-radius: 4px;
    color: #000000;
    border: 1px solid #ccc;
    font-size: 11px; }
    .tagcloud a:hover {
      transition-duration: 0.5s;
      border: 1px solid #000; }

#nav a:hover {
  color: #56dbc9;
  text-decoration: none;
  transition-duration: 0.3s;
}

.category-manager a {
    padding: 10px 20px;
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
    border: 1px solid #7e7e7e;
}
.category-manager a:hover {
    color: #56dbc9 !important;
    border: 1px solid #99c9c2 !important;
}

</style>
