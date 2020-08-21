<template>
    <div id="myquestions" class="row">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css">
        <!-- 컴포넌트 불러오기 -->
        <div class="container-fluid col-12 col-lg-7 mr-5">
            <router-view></router-view>
        </div>

        <!-- 사이드 바 -->
        <div id="nav-mylist" class="col-12 col-lg-4 flex-column text-left px-3 mt-5">
            <h5 class="mt-5">태그</h5>
            <div class="tagcloud mt-3">
                <router-link @click.native="setTotalPageNum(numOfPosts)" :to="{ name: 'MyQuestions' , params: { nickname: this.nickname }}" class="tag-cloud-link">전체보기</router-link> 
                <div v-for="(tag,index) in this.tagList" :key="index" class="d-inline">
                    <router-link @click.native="setTotalPageNum(tagNum[index])" :to="{ name: 'MyQuestions' , query : { q: tag }}" class="tag-cloud-link ">{{ tag }}</router-link>
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
        getTags() {
            axios.get(`${BACK_URL}/tags/qna/${this.nickname}`)
                .then(res => {
                    this.numOfPosts = res.data.list[2].list[0]
                    this.tagList = res.data.list[0].list
                    this.tagNum = res.data.list[1].list
                    this.setTotalPageNum(this.numOfPosts)
                })
                .catch(err => {
                    console.log(err)
                })
        },
        
        // blogPosts.vue로 페이지 넘버 보내기(이벤트버스)
        sendCurrentPage(currentPage) {
            EventBus.$emit("paging2", currentPage)
        },
        setTotalPageNum(num) {
            this.totalNum = num
        }     
    },

    mounted() {
        this.getTags()
    },
    
    data() {
        return{
            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,
            
            // 태그 관련
            tagList: [],
            tagNum: [],    
            
            // 페이지네이션 관련
            totalNum : 0,
            numOfPosts: 0,
        }
    },
}
</script>

<style scoped>
#category-menu button{
    text-decoration: none;
    color: black;
}

#category-menu button:focus{
    font-weight: bold;
    color: #42b983;
}

#category-all button{
    text-decoration: none;
    color: black;
}

#category-all button:focus{
    font-weight: bold;
    color: #42b983;
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

</style>