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
                          <b-button @click="modifyQna" variant="warning" class="ml-2">수정</b-button>
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
             
            <!-- <b-button variant="primary" @click="selectAnswer(aArticle.apostId)" v-if="nickname===qPost.writer">채택</b-button> -->
            <b-button variant="primary" @click="likeAnswer(aArticle.apostId)">좋아요</b-button>
             
             <!-- 채택할지 여부 묻는 Modal / 이 부분 안됨 더 고민해보기-->

                <!-- <b-button id="show-btn" v-if="nickname===qPost.writer" @click="$bvModal.show('bv-modal')">채택</b-button>
                
                <b-modal id="bv-modal" hide-footer>
                    <template v-slot:modal-title>
                        답변 채택
                    </template>
                    <div class="d-block text-center">
                        <h3>답변을 채택 하시겠습니까? 채택 하시면 다시 되돌릴 수 없습니다.</h3>
                        {{aArticle.apostId}}
                    
                    </div>
                    <b-button class="mt-3" variant="outline-danger" block @click="selectAnswer(aArticle.apostId)">채택</b-button>
                    <b-button class="mt-3" variant="outline-warning" block @click="$bvModal.hide('bv-modal')">취소</b-button>
                </b-modal> -->
 
            <hr>
            {{aArticle.answer}}
            
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
            // 답변 채택
            likeit:false,
            answerer: this.$cookies.get('nickname'),
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
                // console.log(res.data.list)
                this.qPost = res.data.list[0].list[0]
                
            })
            .catch(err => {
                console.log(err)
            })
        },
        // 게시물 삭제
        deleteQna(){
            console.log(this.qpost_id)
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
        modifyQna(){
            // console.log(this.qpost_id)
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.put(`${BACK_URL}/questions/${this.qpost_id}`,config) // 이 부분 수정 해야함
            .then(res=>{
                console.log(res)
            })
            .catch(err=>{
                console.log(err)
            })
        },
        // 답변 작성
        writeAnswer(){
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.post(`${BACK_URL}/answers/${this.qpost_id}`,this.answerData,config)
            .then(res=>{

                this.getAnswer() // 페이지 리로딩
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
                console.log(res)
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
                alert('삭제가 실패하였습니다')
                console.log(err)
            })
        },
        // 답변 추천
        likeAnswer(aPostId){
            console.log(this.nickname)
            console.log(this.likeit)
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN')
                }
            }
            axios.post(`${BACK_URL}/answers/like/${aPostId}/${this.nickname}`,this.answerer,config)
            .then(res=>{
                
                alert("추천 완료")
                console.log(res)
            })
            .catch(err=>{
                console.log(err)
            })
        },
        // 답변 채택
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