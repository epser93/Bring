<template>
    <div>
    <b-jumbotron>
        <template v-slot:header>지식IN</template>

        <template v-slot:lead>
        당신의 지식을 전해주세요
        </template>
        <hr class="my-4">
        <p>
        It uses utility classes for typography and spacing to space content out within the larger
        container.
        </p>
        <b-button variant="primary"><router-link :to="{ name : 'QuestionWrite' }"><b-icon icon="pencil-square" /> 글쓰기</router-link></b-button>
        
        </b-jumbotron>
        <hr>
        <!-- 최신글 / 트랜드 -->
        <div class= "mt-4 mb-4">
            <b-button variant="outline-primary" class="mr-2" @click="getAllQna">최신글</b-button>
            <b-button variant="outline-info" class="ml-2" @click="getHotQna">인기글</b-button>
        </div>

        <!-- 썸네일 default 값 고민해볼것-->
        <div class="row">
            <div v-for="(qArticle,index) in qPost" :key="qArticle.qpostId" class="p-0 mb-5 col-12 col-lg-3">
                <div class="card" style="width: 18rem;">
                    <img class="card-img-top" :src="thumbnail[index]" alt="Card image cap">
                    <div class="card-body pb-0">
                        <h5 class="card-title">{{ qArticle.subject.slice(0, 10) + '...'  }}</h5>
                        <p class="card-text mb-3">{{ qArticle.content.slice(0, 20) + '...' }}</p>
                        <!-- 태그: {{qArticle.tags}} -->
                        <!-- 작성시간: {{qArticle.createdAt}} -->
                        <h5>글쓴이: {{qArticle.member_nickname}} / 조회수:{{qArticle.views}} </h5>
                    </div>
                    <div class="card-footer bg-transparent">
                        <button class="btn btn-primary" @click="getQnaDetail(qArticle)">글 자세히</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="container">
            <!--pagination-->
            <div class="mt-5 container">
                <div class="row justify-content-center">
                    <b-pagination v-model="currentPage" :total-rows="rows"></b-pagination>
                </div>
            </div>
        
        </div>
        <div class="container-fluid">
            <router-view></router-view>
        </div>
    </div>
</template>

<script>
import axios from 'axios'
const BACK_URL = 'http://localhost:8080'

export default {
    name:'Question',
    components:{
        
    },
    data() {
        return {
            rows: 100,
            currentPage: 1,
            articles:10,
            count:5,
            qPost: [],
            thumbnail: [],
      
      }
    },
    props:{
        cardImage: {
            type: String,
            default: require("@/assets/img/card.jpg")
        },
    },
    methods:{
        // 전체 질문 리스트 최신글
        getAllQna() {
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN')
                }
            }
            axios.get(`${BACK_URL}/questions/recent`,config)
            .then(res => {
                this.thumbnail=res.data.list[1].list
                this.qPost=res.data.list[0].list
                // console.log(this.qPost)
            })
            .catch(err => {
                console.log(err)
            })
        },
        // 전체 질문 리스트 인기글
        getHotQna(){
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN')
                }
            }
            axios.get(`${BACK_URL}/questions/trend`,config)
            .then(res => {
                console.log(res.data)
                this.thumbnail=res.data.list[1].list
                this.qHotPost=res.data.list[0].list

                // console.log(res.data.list[0].list)
                var qHotPost=this.qHOtPost
                // console.log(qHotPost)
                const sortHotQuestion = "views";
                qHotPost.sort(function(a, b) { // 오름차순
                    return b[sortHotQuestion] - a[sortHotQuestion];
                    // 13, 21, 25, 44
                });
            })
            .catch(err => {
                console.log(err)
            })
        },

        // 게시물 디테일 페이지로 이동
        getQnaDetail(qArticle){
            this.$router.push({ name: 'QuestionDetail', params: {nickname: qArticle.member_nickname, qpostId: qArticle.qpostId}})
        }
    },
    created(){
        this.getAllQna()
        this.getHotQna()
        }
}
    
     
    

</script>

<style>

</style>