<template>
    <div id="myquestions" class="">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css">
        <!-- 컴포넌트 불러오기 -->
        <div class="container-fluid col-12">
            <router-view></router-view>
        </div>

        <!-- 페이지네이션 -->
        <div class="col-12">
            <BlogPagination :totalNum="totalNum" @pageNum="sendCurrentPage"/>
        </div>

        <!-- 사이드 바 -->
        <div id="nav-mylist" class="flex-column text-left px-3">
            <h5>태그</h5>
            <div class="tagcloud" style="width: 330px;">
                <router-link @click.native="setTotalPageNum(numOfPosts)" :to="{ name: 'MyQuestions' , params: { nickname: this.nickname }}" class="tag-cloud-link">전체보기</router-link> 
                <div v-for="(tag,index) in this.tagList" :key="index" class="d-inline">
                    <router-link @click.native="setTotalPageNum(tagNum[index])" :to="{ name: 'MyQuestions' , query : { q: tag }}" class="tag-cloud-link ">{{ tag }}</router-link>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios'
import BlogPagination from '../blog/BlogPagination.vue'
import { EventBus } from '../../event-bus.js'


const BACK_URL = 'http://i3c206.p.ssafy.io:80'

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
@media only screen and (min-width: 1000px) {
    #myquestions {
        position: relative;
    }

    #nav-mylist{
        position: absolute;
        top: 260px;
        left: -420px;
    }
}

#myquestions {
    min-height: 1000px;
}

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

/* 트렌지션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
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


#nav-mylist {
    max-width: 370px;
}
</style>