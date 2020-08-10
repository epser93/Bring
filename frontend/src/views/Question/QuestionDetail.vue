<template>
  <div id="app">
     <b-container>
         <div class="card rounded-lg shadow p-3 mb-5 bg-white rounded">
         <div>
             <h1>{{qPost.subject}}</h1>
         </div>
         <b-container>
            <b-row>
                <b-col></b-col>
                <b-col></b-col>
            <b-col>
                
                작성자: {{qPost.writer}}
                <span class="text-muted ">작성시간: {{qPost.createdAt}}</span>

            </b-col>
            </b-row>
            </b-container>
        </div>
        <div class="card rounded-lg mt-5 shadow p-3 mb-5 bg-white rounded">
             <!-- <p><b-badge pill variant="success" class="mr-3">#{{qPost.tag}}</b-badge>
             </p> -->
              <h5 class="card-text">{{qPost.content}}
                  
                  <br><br><br><br><br><br><br><br><br><br><br><br>
                  <hr>
                  <div class="row">
                      <div class="col"></div>
                      <div class="col"></div>
                      <div class="col">
                          
                          <!-- 분기처리/ 작성자와 현재 사용자의 이름이 같으면 삭제표시되게끔-->
                          <div v-if="this.nickname===qPost.writer">
                          <b-button @click="deleteQna" class="mr-1" ><b-icon icon="trash"></b-icon> 삭제</b-button>
                          <b-button @click="modifyQna(qpost_id)" variant="warning" class="ml-2">수정</b-button>
                          
                          </div>
                      </div>
                  </div>
              </h5>

        </div>

        <!-- 답변 리스트-->
        

        <div class="card rounded-lg mt-5 shadow p-3 mb-5 bg-white rounded" v-for="aArticle in aPost" :key="aArticle.aPostId">
            <p>글쓴이: {{aArticle.writer}}<span class="ml-5"> 좋아요 수: {{aArticle.likes}}</span></p> 
            <span>채택여부: {{aArticle.selected}}</span>
            
            <b-button variant="danger" @click="deleteAnswer(aArticle.apostId)" v-if="nickname===aArticle.writer">삭제</b-button>
             
            <b-button variant="primary" @click="selectAnswer(aArticle.apostId)" v-if="nickname===qPost.writer">채택</b-button>
 
            <hr>
            {{aArticle.answer}}
            {{aArticle}}
            <!--좋아요-->
            <span v-if="answerLike[aArticle.apostId]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="likeAnswer(aArticle, true)"><i class="fas fa-heart"></i></span>
            <span v-if="!answerLike[aArticle.apostId]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="likeAnswer(aArticle, false)"><i class="fas fa-heart"></i></span>
            <small :ref="'like-count-' + aArticle.apostId">{{ aArticle.likes }}</small><small>개의 좋아요</small>
            {{answerLike}}
        </div>

        <div class="card rounded-lg mt-5 shadow p-3 mb-5 bg-white rounded">
             <span v-if="writeComment">
                 <div>
                <b-form-textarea
                    id="textarea-rows"
                    placeholder="Tall textarea"
                    rows="8"
                    v-model="answerData.answer"
                ></b-form-textarea>
            </div>
            <button class="btn btn-success btn-sm mx-1" @click='writeAnswer' >답변 달기</button>
            <button class="btn btn-success btn-sm mx-1" @click='commentClose'>답변창 닫기</button>
            <!-- 누르면 새로 렌더해주게끔 로직짜야함-->
            
        </span>
        <span v-else-if="this.nickname!=qPost.writer">
            <button class="btn btn-success btn-sm mx-1" @click='commentOpen' >답변창 열기</button>
        </span>

        </div>
    </b-container>
  </div>
</template>

<script>
import axios from 'axios'
const BACK_URL = 'http://localhost:8080'

export default {
    name:'QuestionDetail',
     data(){
        return {
            writeComment: false,
            nickname: this.$cookies.get('nickname'),
            qpost_id: this.$route.params.qpostId,
            qPost : [],

            // 질문 수정
            newqPost:[],

            // 답변
            answerData:{
                answer:"",
            },
            aPost:[],
            apost_id: this.$route.params.apostId,
           
            // 답변 추천(좋아요)
            answerer: this.$cookies.get('nickname'),
            answerLike:[],      
        }
     },
    methods: {
        commentOpen() {
            this.writeComment = true
        },
        commentClose() {
            this.writeComment = false
        },
        // 게시물 호출
        getQna() {
            axios.get(`${BACK_URL}/questions/${this.qpost_id}`)
            .then(res => {
                this.qPost = res.data.list[0].list[0]
                
            })
            .catch(err => {
                console.log(err)
            })
        },
        // 게시물 삭제
        deleteQna(){
            
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.delete(`${BACK_URL}/questions/${this.qpost_id}`,config)
            .then(res=>{
                console.log(res)
                alert('삭제가 완료 되었습니다.')
                this.$router.push({ name: 'Question' })
            })
            .catch(err=>{
                alert('답변이 등록 된 질문이거나 권한이 없습니다.')
                console.log(err)
            })
        },
        // 게시물 수정
        modifyQna(qpost_id){
            console.log(this.qpost_id)
            this.$router.push({name:'QuestionUpdate', params:{qpostId: qpost_id}})
        },
        // 답변 작성 (페이지 리로딩 하는 방식 고민해볼것)
        writeAnswer(){
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.post(`${BACK_URL}/answers/${this.qpost_id}`,this.answerData,config)
            .then(res=>{
                this.getAnswer() // 페이지 리로딩
                this.writeComment = false
                this.answerData.answer=""
                // 새로고침 하는 방법도 고민

                console.log(res)
            })
            .catch(err=>{
                console.log(err)
            })
        },
        // 답변 목록
        getAnswer(){
            axios.get(`${BACK_URL}/answers/${this.qpost_id}/answers`)
            .then(res=>{
                this.aPost=res.data.list
                
            })
            .catch(err=>{
                console.log(err)
            })
        },
        // 답변 삭제
        deleteAnswer(aPostId){
            console.log(aPostId)
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.delete(`${BACK_URL}/answers/${aPostId}`,config)
            .then(res=>{
                alert('삭제가 완료 되었습니다.')
                this.getAnswer()
                console.log(res)
            })
            .catch(err=>{
                alert('권한이 없습니다.')
                console.log(err)
            })
        },
        // 답변 추천(좋아요) 아직 안됨
        likeAnswer(aArticle,likeit){
            
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN'),
                    'Content-Type': 'application/json'
                }
            }
            if (likeit === false) {
                console.log(aArticle)
                console.log(aArticle.apostId)
                console.log(aArticle.writer)
                axios.post(`${BACK_URL}/answers/like/${aArticle.apostId}/${aArticle.writer}`,likeit,config)
                    .then(res=>{
                        console.log(res)
                        this.$refs[`like-count-${aArticle.apostId}`][0].innerText = res.data.data
                        this.getAnswer()
                    })
                    .catch(err=>{
                        console.log(err)
                    })
            }else{
                console.log(aArticle)
                
                axios.post(`${BACK_URL}/answers/like/${aArticle.apostId}/${aArticle.writer}`,likeit,config)
                    .then(res=>{
                        this.$refs[`like-count-${aArticle.apostId}`][0].innerText = res.data.data 
                        this.getAnswer()
                        console.log(res)
                    })
                    .catch(err=>{
                        console.log(err)
                    })
            }
        },
        // 답변 채택 (되는데 모달로 할 때는 에러남// 그냥 에러남;;)
        selectAnswer(aPostId){
            console.log(aPostId)
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN')
                }
            }
            axios.post(`${BACK_URL}/answers/select/${aPostId}`,config)
            .then(res=>{
                alert("채택 되었습니다")
                this.getAnswer()
                console.log(res)
            })
            .catch(err=>{
                console.log(err)
                alert("더 이상 답변을 채택할 수 없습니다")
            })
        },
        
    },
    
    created(){
        this.getQna()
        this.getAnswer()
        
        }
}
</script>

<style>

</style>