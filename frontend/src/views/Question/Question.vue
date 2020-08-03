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
        <!-- <button>최신글</button>
        <button>HOT 지식인</button> -->
        <div class="container">
            
            
            <div>
            <b-card no-body class="overflow-hidden" style="max-width: 540px;">
                <b-row no-gutters>
                <b-col md="6">
                    <b-card-img src="https://picsum.photos/400/400/?image=20" alt="Image" class="rounded-0"></b-card-img>
                </b-col>
                <b-col md="6">
                    <b-card-body title="Horizontal Card">
                    <b-card-text>
                        This is a wider card with supporting text as a natural lead-in to additional content.
                        This content is a little bit longer.
                       
                    </b-card-text>
                    <b-button variant="primary"><router-link :to="{ name: 'QuestionDetail' }">카드 디테일로 가게끔</router-link></b-button>
                    </b-card-body>
                        <!--heart icon-->
                            <b-icon icon="heart" scale="1"></b-icon>

                </b-col>
                </b-row>
            </b-card>
        </div>
            
            
            
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
// import axios from 'axios'
// const BACK_URL = 'http://localhost:8080'

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

            qnaList:[],
      
      }
    },
    // methods:{
    //     getAllQna() {
    //         axios.get(`${BACK_URL}/qna/qlist`)
    //         .then(res => {
    //             this.qnaList = res.data
    //             console.log(this.qnaList)
    //         })
    //         .catch(err => {
    //             console.log(err)
    //         })
    //     },
    // created(){
    //     this.getAllQna()
    //     }
    // },
}
    
     
    

</script>

<style>

</style>