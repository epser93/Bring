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

        <div class="container">
            <ul >
                <li v-for="qArticle in qPost" :key="qArticle.qPost_id">
                    번호: {{qArticle.qpost_id}}
                    제목: {{qArticle.subject}}
                    <h5>내용: {{qArticle.content}}</h5>
                    태그: {{qArticle.tag}}
                    글쓴이: {{qArticle.writer}}
                    조회수: {{qArticle.views}}
                    
                    <!--heart icon-->
                <b-icon icon="heart" scale="1"></b-icon>
                <p><button class="btn btn-primary"><router-link :to="{ name: 'QuestionDetail', params: {qpostId: qArticle.qpost_id} }">글 자세히</router-link></button></p>
                </li>
                
            </ul>
            
             

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
            nickname: this.$route.params.nickname,

            qPost:[],
      
      }
    },
    methods:{
        getAllQna() {
            console.log(this.qPost)
            axios.get(`${BACK_URL}/qna/question/qlist`)
            .then(res => {
                this.qPost = res.data.list
                
            })
            .catch(err => {
                console.log(err)
            })
        },
    },
    created(){
        this.getAllQna()
        }
}
    
     
    

</script>

<style>

</style>