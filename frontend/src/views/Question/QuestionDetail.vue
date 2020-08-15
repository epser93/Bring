<template>
  <div id="app">
     <b-container>
         <div class="card rounded-lg shadow p-3 mb-5 bg-white rounded">
         <div>
            <h1>{{qPost.subject}}</h1>
            <!-- 질문에 채택된 답변이 있으면 표시-->
            <div v-if="selectedAnswer">
                 <b-badge variant="success">채택</b-badge>
            </div>
         </div>
         <b-container>
            <b-row>
                <b-col></b-col>
                <b-col></b-col>
            <b-col>
               
                <!-- 태그부분 -->
                <div class="tag">
                    <span v-for="(tag,index) in this.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2">{{ tag }}</span>
                </div>
                {{qPost.qpostId}}
                작성자: {{qPost.member_nickname}}
                <span class="text-muted ">작성시간: {{qPost.createdAt}}</span>

            </b-col>
            </b-row>
            </b-container>
        </div>
        <div class="card rounded-lg mt-5 shadow p-3 mb-5 bg-white rounded">
             <!-- <p><b-badge pill variant="success" class="mr-3">#{{qPost.tag}}</b-badge>
             </p> -->
              <h5 class="card-text">{{qPost.content}}</h5>
                  
                  <br><br><br><br><br><br><br><br><br><br><br><br>
                  <hr>
                  <div class="row">
                      <div class="col"></div>
                      <div class="col"></div>
                      <div class="col">
                          
                          <!-- 분기처리/ 작성자와 현재 사용자의 이름이 같으면 삭제표시되게끔-->
                          <div v-if="this.nickname===qPost.member_nickname">
                          <b-button @click="deleteQna(qpost_id)" class="mr-1" ><b-icon icon="trash"></b-icon> 삭제</b-button>
                          <b-button @click="modifyQna(qpost_id)" variant="warning" class="ml-2">수정</b-button>
                          </div>
                      </div>
                  </div>      
        </div>

        <!-- 답변 작성 -->
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
                <button class="btn btn-success btn-sm mx-1" v-if="!updateAnswer" @click='writeAnswer'>답변 작성</button>
                <button class="btn btn-success btn-sm mx-1" v-if="updateAnswer" @click='modifyAnswer'>답변 수정</button>
                <button class="btn btn-success btn-sm mx-1" @click='commentClose'>답변창 닫기</button>
            </span>
            <span v-else>
                <button class="btn btn-success btn-sm mx-1" @click='commentOpen' >답변창 열기</button>
            </span>
        </div>
        
        <!-- 답변 리스트-->
        <div class="card rounded-lg mt-5 shadow p-3 mb-5 bg-white rounded" v-for="(aArticle,index) in aPost" :key="aArticle.aPostId">
            <p>글쓴이: {{aArticle.member_nickname}}
                {{aArticle.apostId}}
                <!-- 만약 채택 된 답변이라면 -->
                <span v-if="aArticle.selected===true"><b-icon icon="patch-check-fll" variant="info"></b-icon>채택된 답변</span>
                <span v-else>
                    <div v-if="aArticle.member_nickname===qPost.member_nickname">
                        <b-button variant="danger" @click="deleteAnswer(aArticle.apostId)">삭제</b-button>
                        <b-button variant="warning" @click='modifyAnswerOpen(aArticle)'>수정</b-button>
                    </div>
                    <div v-else-if="(nickname===aArticle.member_nickname && selectedAnswer===false )">
                        <b-button variant="danger" @click="deleteAnswer(aArticle.apostId)">삭제</b-button>
                        <b-button variant="warning" @click='modifyAnswerOpen(aArticle)'>수정</b-button>
                    </div>
                    <div v-else>
                        <b-button variant="danger" @click="deleteAnswer(aArticle.apostId)" v-if="(nickname===aArticle.member_nickname && selectedAnswer===false)">삭제</b-button>
                        <b-button variant="warning" @click='modifyAnswerOpen(aArticle)' v-if="nickname===aArticle.member_nickname">수정</b-button>
                        <b-button variant="primary" @click="selectAnswer(aArticle.apostId)" v-if="(nickname===qPost.member_nickname && selectedAnswer===false )">채택</b-button>
                    </div>
                </span>
            </p> 
            <hr>
            <!--내용-->
            {{aArticle.answer}}
            <hr>
            <!--좋아요-->
            <span v-if="like[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="likeAnswer(aArticle, false)"><i class="fas fa-heart"></i></span>
            <span v-if="!like[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="likeAnswer(aArticle, true)"><i class="fas fa-heart"></i></span>        
            <small :ref="'like-count-' + aArticle.apostId">{{ aArticle.likes }}</small><small>개의 좋아요</small>
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
            answerUpdate:false,
            // 답변 수정
            updateAnswer: false,
            id:0,
            newAnswerData:{
                answer:"",
            },
           
            // 답변 추천(좋아요)
            answerer: this.$cookies.get('nickname'),   
            likes:null,
            like:[],

            // 답변 채택
            selectedAnswer:false,
            // 태그
            tags: ''
        }
     },
    methods: {
        // 답변창 열기
        commentOpen() {
            this.writeComment = true
        },
        // 답변창 닫기
        commentClose() {
            this.writeComment = false
        },
        // 게시물 호출
        getQna() {
            axios.get(`${BACK_URL}/questions/${this.qpost_id}`)
            .then(res => {
                this.qPost = res.data.list[0].list[0]
                // 태그
                this.tags = res.data.list[1].list

                console.log(this.aPost.length)
            })
            .catch(err => {
                console.log(err)
            })
        },
        // 게시물 삭제
        deleteQna(qpost_id){
            console.log(qpost_id)
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            const questionDelete = confirm("질문을 삭제 하시겠습니까?")
            if (questionDelete===true) {
            axios.delete(`${BACK_URL}/questions/${qpost_id}`,config)
            .then(res=>{
                console.log(res)
                alert('삭제가 완료 되었습니다.')
                this.$router.push({ name: 'Question' })
            })
            .catch(err=>{
                alert('답변이 달린 질문은 수정이나 삭제할 수 없습니다.')
                console.log(err)
            })
            }
        },
        // 게시물 수정
        modifyQna(qpost_id){
            console.log(this.qpost_id)
            this.$router.push({name:'QuestionUpdate', params:{qpostId: qpost_id}})
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
                this.writeComment = false
                this.answerData.answer=""
                this.getAnswer() // 페이지 리로딩
                console.log(res)
            })
            .catch(err=>{
                console.log(err)
            })
            
        },
        // 답변 목록
        getAnswer(){
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN'),
                    'Content-Type': 'application/json'
                }
            }
            axios.get(`${BACK_URL}/answers/${this.qpost_id}/answers`,config)
            .then(res=>{
                this.aPost=res.data.list[0].list
                // 좋아요 불리언 값
                this.like=res.data.list[1].list
                // 채택된 값이 하나라도 있으면 채택 못해주게끔
                const array=res.data.list[0].list
                for (const i in array){
                    if (array[i].selected===true){
                        this.selectedAnswer=true
                    }
                }
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
            const askDelete = confirm("답변을 삭제 하시겠습니까?")
            if (askDelete===true) {
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
            }
        },
        // 답변 추천
        likeAnswer(aArticle,likeit){
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN'),
                    'Content-Type': 'application/json'
                }
            }
            if (likeit === false) {
                axios.post(`${BACK_URL}/answers/like/${aArticle.apostId}/${aArticle.member_nickname}`,likeit,config)
                    .then(res=>{
                        this.$refs[`like-count-${aArticle.apostId}`][0].innerText = res.data.data
                        console.log(res)
                        this.getAnswer()
                    })
                    .catch(err=>{
                        alert('본인의 답변에는 좋아요를 할 수 없습니다')
                        console.log(err)
                    })
            } else {
                axios.post(`${BACK_URL}/answers/like/${aArticle.apostId}/${aArticle.member_nickname}`,likeit,config)
                    .then(res=>{
                        this.$refs[`like-count-${aArticle.apostId}`][0].innerText = res.data.data 
                        console.log(res)
                        this.getAnswer()
                    })
                    .catch(err=>{
                        alert('본인의 답변에는 좋아요를 할 수 없습니다')
                        console.log(err)
                    })
            }
        },
        // 답변 채택
        selectAnswer(aPostId){
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN')
                }
            }
            const askAdopt = confirm("답변을 채택 하시겠습니까?")
            if (askAdopt===true) {
            axios.post(`${BACK_URL}/answers/select/${aPostId}`,this.answerData,config)
            .then(res=>{
                alert("채택 되었습니다")
                this.getAnswer()
                console.log(res)
            })
            .catch(err=>{
                console.log(err)
                alert("더 이상 답변을 채택할 수 없습니다")
            })
            }
        },
        // 답변 수정
        modifyAnswer(){
            const config={
                headers:{
                    'X-AUTH-TOKEN':this.$cookies.get('X-AUTH-TOKEN')
                }
            }
            axios.put(`${BACK_URL}/answers/${this.answerData_id}`,this.answerData,config)
            .then(res=>{
                this.getAnswer()
                this.updateAnswer=false
                this.writeComment=false
                console.log(res)
            })
            .catch(err=>{
                console.log(err)
            })
        },
        // 답변 수정창 열기
        modifyAnswerOpen(aArticle){
            this.writeComment=true
            this.updateAnswer=true
            this.answerData.answer=aArticle.answer
            this.answerData_id=aArticle.apostId
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