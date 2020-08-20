<template>
  <div id="questionDetail">
      <div class="container">
        <!-- 글쓴이 정보 -->
        <div class="info text-center my-5">
            <h1 class="mb-3">{{qPost.subject}}</h1>
            <span class="text-muted">{{ qPost.createdAt.slice(0,10) }}</span>
            <span class="vertical-line mx-3"></span>
            <span class="mr-2" @click="gotoProfile(qPost.member_nickname)"><strong>{{ qPost.member_nickname }}</strong></span>
            <!-- 분기처리/ 작성자와 현재 사용자의 이름이 같으면 삭제표시되게끔-->
            <div v-if="this.nickname===qPost.member_nickname" class="text-right">
                <a class="float-right mx-1" @click="deleteQna(qpost_id)" ><b-icon icon="trash"></b-icon> 삭제</a>
                <a class="float-right mx-1" @click="modifyQna(qpost_id)"><b-icon icon="pen"></b-icon>수정</a>
            </div>
            <!-- 태그부분 -->
            <div class="tag">
                <span v-for="(tag,index) in this.tags" :key="index" class="badge badge-pill badge-info mr-2" @click="searchTag(tag)">{{ tag }}</span>
            </div>
        </div>
        <hr>

        <!-- 글 -->
        <v-md-preview :text="qPost.content"></v-md-preview>
        <!-- 조회수-->
        <div class=" text-left" style="margin: 100px 0 50px;">
            <i class="far fa-eye ml-1"></i> {{ qPost.views }}
        </div>
        <!-- 댓글 입력 부분 -->
        <div id="commentTextArea" v-if="!qPost.selectOver">
            <span v-if="writeComment">
                <div>
                    <b-form-textarea
                        id="textarea-rows"
                        placeholder="댓글을 작성해주세요!"
                        rows="4"
                        v-model="answerData.answer"
                    ></b-form-textarea>
                </div>
                <a class="float-right " v-if="!updateAnswer" @click='writeAnswer'><b-icon icon="pencil-square"></b-icon> 답변 작성</a>
                <a class="float-right " v-if="updateAnswer" @click='modifyAnswer'>답변 수정</a>
                <a class="float-right " @click='commentClose'>닫기</a>
            </span>
            <span v-else class="d-flex flex-row">
                <a class="p-2" @click='commentOpen' >답변창 열기</a>
            </span>
        </div>

        <!--채택된 답변 -->
        <h3 class="mt-5 mb-4 text-left">{{aPost.length}} 개의 답변</h3>
        <div v-for="(aArticle,index) in aPost" :key="`A-${index}`">
            <div class="d-flex flex-row"> 
            <div v-if="aArticle.selected===true" id="selected-answer">
                <p class="text-left ml-1">
                    <b-icon icon="patch-check-fll" variant="info"></b-icon>채택 |
                    <strong class="" @click="gotoProfile(aArticle.member_nickname)">{{aArticle.member_nickname}}</strong>
                </p>
                <p class="my-3 text-left ml-1">{{aArticle.answer}}</p>
                <!--좋아요-->
                <div class="mr-5 pr-3 text-left">
                    <b-icon icon="heart-fill" v-if="like[index]" class="d-inline ml-1" style="cursor:pointer; color: crimson;" @click="likeAnswer(aArticle, false)"></b-icon>
                    <b-icon icon="heart" v-if="!like[index]" class="d-inline ml-1" style="cursor:pointer; color: black;" @click="likeAnswer(aArticle, true)"></b-icon> 
                    <small :ref="'like-count-' + aArticle.apostId"> {{ aArticle.likes }}</small><small>개의 좋아요</small>
                    <span class="vertical-line mx-3"></span>
                    <span>{{ aArticle.createdAt.slice(0,10) }}</span>
                    <a class="ml-4 mr-2" @click="deleteAnswer(aArticle.apostId)" v-if="selectedAnswer===false"><b-icon icon="trash"></b-icon>삭제</a>
                    <a class="" @click='modifyAnswerOpen(aArticle)' v-if="selectedAnswer===false"><b-icon icon="pen"></b-icon>수정</a>
                </div>
            </div>
        </div>
        </div>
        <hr>

        <!--채택되지 않은 답변-->
        <div class="row-12" v-for="(aArticle,index) in aPost" :key="aArticle.apostId">
            <div v-if="aArticle.selected===false">
                <p class="text-left ml-1" @click="gotoProfile(aArticle.member_nickname)"><strong>{{aArticle.member_nickname}}</strong></p>
                <p class="text-left mb-3 mt-3 mr-5 pr-5">{{aArticle.answer}}</p>
                <!-- 좋아요 -->
                <p class="text-left">
                <b-icon icon="heart-fill" v-if="like[index]" class="d-inline ml-1" style="cursor:pointer; color: crimson;" @click="likeAnswer(aArticle, false)"></b-icon>
                <b-icon icon="heart" v-if="!like[index]" class="d-inline ml-1" style="cursor:pointer; color: black;" @click="likeAnswer(aArticle, true)"></b-icon> 
                <small :ref="'like-count-' + aArticle.apostId"> {{ aArticle.likes }}</small><small>개의 좋아요</small>
                <span class="vertical-line mx-3"></span>
                <span>{{ aArticle.createdAt.slice(0,10) }}</span>
                 <!-- 수정 삭제 버튼-->
                <span v-if="(nickname===aArticle.member_nickname)" class="ml-1">
                    <a class="ml-4 mr-2" @click="deleteAnswer(aArticle.apostId)" v-if="selectedAnswer===false"><b-icon icon="trash"></b-icon>삭제</a>
                    <a class="" @click='modifyAnswerOpen(aArticle)' v-if="selectedAnswer===false"><b-icon icon="pen"></b-icon>수정</a>
                </span>
                <span v-else class="ml-1">
                    <a class="ml-4 mr-2" @click="deleteAnswer(aArticle.apostId)" v-if="(nickname===aArticle.member_nickname)"><b-icon icon="trash"></b-icon>삭제</a>
                    <a class="" @click='modifyAnswerOpen(aArticle)' v-if="nickname===aArticle.member_nickname "><b-icon icon="pen"></b-icon>수정</a>
                    <a class="" @click="selectAnswer(aArticle.apostId)" v-if="(nickname===qPost.member_nickname && selectedAnswer===false)"><b-icon icon="check-circle" class="mb-1"></b-icon> 채택</a>
                </span>
                </p>
                <hr>
            </div>
        </div>
    </div>
  </div>

</template>

<script>
import axios from 'axios'
const BACK_URL = 'http://i3c206.p.ssafy.io/api'

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
            tags: '',
            text : ''
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
            this.answerData.answer = ""
        },
        // 게시물 호출
        getQna() {
            axios.get(`${BACK_URL}/questions/${this.qpost_id}`)
            .then(res => {
                this.qPost = res.data.list[0].list[0]
                // 태그
                this.tags = res.data.list[1].list
            })
            .catch(err => {
                console.log(err)
            })
        },
        // 게시물 삭제
        deleteQna(qpost_id){
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            const questionDelete = confirm("질문을 삭제 하시겠습니까?")
            if (questionDelete===true) {
            axios.delete(`${BACK_URL}/questions/${qpost_id}`,config)
            .then(()=>{
                alert('삭제가 완료 되었습니다.')
                this.$router.push({ name: 'MyQuestions', params: { nickname : this.nickname } })
            })
            .catch(err=>{
                alert('답변이 달린 질문은 수정이나 삭제할 수 없습니다.')
                console.log(err)
            })
            }
        },
        // 게시물 수정
        modifyQna(qpost_id){
            if (this.qPost.selectOver) {
                alert('답변이 채택된 질문은 수정할 수 없습니다.')
            } else {
                this.$router.push({name:'QuestionUpdate', params:{qpostId: qpost_id}})
            }
        },
        // 답변 작성
        writeAnswer(){
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.post(`${BACK_URL}/answers/${this.qpost_id}`,this.answerData,config)
            .then(()=>{
                this.writeComment = false
                this.answerData.answer=""
                this.getAnswer() // 페이지 리로딩
            })
            .catch(err=>{
                alert("더 이상 답변을 작성 할 수 없습니다")
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
                console.log(res.data)
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
            .then(()=>{
                alert('삭제가 완료 되었습니다.')
                this.getAnswer()
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
            .then(()=>{
                alert("채택 되었습니다")
                this.getAnswer()
            })
            .catch(()=>{
                this.getAnswer()
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
            .then(()=>{
                this.getAnswer()
                this.updateAnswer=false
                this.writeComment=false
                this.answerData.answer=""
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
        searchTag(tag) {
            this.$router.push({ name : 'TagSearchQuestions', params: { keyword : tag }})
        },
        // 프로필 가기
        gotoProfile(nickname) {
          this.$router.push({ name : 'Profile', query: { nickname: nickname } })
        }
    },
    created(){
        this.getQna()
        this.getAnswer()
        
        }
}
</script>

<style scoped>
#selected-answer{
    border : 5px solid #99c9c2;
    border-radius: 10px;
    padding: 20px;
    width: 100%;
}

strong {
    cursor: pointer;
}
#questionDetail a {
    padding: 5px 10px;
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
    border: 1px solid #e7e7e7;
}

#questionDetail a:hover {
    color: #56dbc9 !important;
    border: 1px solid #99c9c2 !important;
}

.tag {
    cursor: pointer;
}
</style>