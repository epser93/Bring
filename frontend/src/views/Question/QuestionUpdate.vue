<template>
  <div id="editor" class="row">
    <div class="wrapper text-left my-5 p-5">

      <h2 class="mb-5">질문 수정</h2>
      
      <!-- 제목 부분 -->
      <form class="row text-left">
        <div class="col-12 form-group">
          <h5>제목</h5> 
          <input type="text" class="form-control" v-model="questionData.subject" id="titleInput" placeholder="제목을 입력하세요">
        </div>
      </form>

      <!-- 글 에디터 부분 -->
      <v-md-editor class="text-left" v-model="questionData.content" :disabled-menus="[]" @upload-image="handleUploadImage" height="600px"></v-md-editor>
      <br>

      <!-- 태그 -->
      <div class="tag">
        <span v-for="(tag,index) in questionData.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2" @click="deleteTag(index)">{{ tag }}<span id="closeTag">  x</span></span>
      </div>
      <input placeholder="태그를 입력후 enter키를 눌러주세요" class="mt-3 mb-5 tag-input" type="text" v-model="tag" @keydown.enter="postTag">
      
      <!-- 제출 버튼 -->
      <a @click="modifyQuestion" class="float-right">수정</a>
    </div>  
  </div>
</template>

<script>

import axios from 'axios'
const BACK_URL = 'http://i3c206.p.ssafy.io:8080/api'

export default {
    name:'QuestionUpdate',
    data(){
        return{
            qpost_id: this.$route.params.qpostId,
            qPost : [],
            questionData:{
                content:"",
                subject:"",       
                tags:[],
            },
            // 태그
            tag:"",
            imageServerUrl : ''
            
        }
    },
    methods:{
      // 바로 이미지를 서버에 업로드 업로드 된 장소의 url 받아오기
      uploadImageDirect(file, insertImage) {
        const formData = new FormData()
        formData.append('files', file)
        const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
            'Content-Type' : 'multipart/form-data'
          }
        }
        axios.post(`${BACK_URL}/questions/ask/${this.qpost_id}/uploads`, formData, config)
          .then(res => {
            this.imageServerUrl = res.data.list[0]
            insertImage({
            url : this.imageServerUrl,
            desc : '사진설명'
          })
          })
          .catch(err => console.log(err))
      },
      // 이미지 업로드
      handleUploadImage(event, insertImage, files) {
          this.uploadImageDirect(files[0], insertImage)
      },
        // 질문 수정
        modifyQuestion(){
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.put(`${BACK_URL}/questions/${this.qpost_id}`,this.questionData,config)
            .then(()=>{
                this.$router.push({name:'QuestionDetail', params:{ nickname: this.$cookies.get('nickname'), qpostId : this.qpost_id }})
            })
            .catch(err=>{
                console.log(err)
            })
        },
        // 기존 질문 내용 호출
        getQna() {
            axios.get(`${BACK_URL}/questions/${this.qpost_id}`)
            .then(res => {
                this.questionData.subject=res.data.list[0].list[0].subject
                this.questionData.content=res.data.list[0].list[0].content
                // 태그
                this.questionData.tags = res.data.list[1].list                  
            })
            .catch(err => {
                console.log(err)
            })
        },
        // 태그
        postTag() {
          const chkpatterns = /[~!@#$%^&*()_+|<>?:{}]/;
          if (this.tag === null || this.tag.replace(/^\s*|\s*$/g, '').length === 0) {
            alert('빈칸은 태그로 입력 불가능합니다.')
            this.tag = ""
          } else if (chkpatterns.test(this.tag)) {
            alert('특수문자는 입력할 수 없습니다.')
            this.tag = ""
          } else if (!this.questionData.tags.includes(this.tag)) {
            this.questionData.tags.push(this.tag)
            this.tag = ""
          } else {
            alert('중복된 태그입니다.')
            this.tag= ""
          }
        },
        deleteTag(index) {
          this.questionData.tags.splice(index,1)
        },
    },
     created(){
        this.getQna()
        }
    }

    
    

</script>

<style scoped>
#closeTag {
  opacity: 0.5;
}

#editor {
    min-height: 100vh;
    background-color: #f4f4f4;
}

.wrapper {
    width: 100%;
    margin: 0 auto;
    background-color: white;
    border: 1px solid #e7e7e7;
    margin-bottom: 200px;
}

.tag-input {
  width: 100%
}

.badge {
  cursor: pointer;
}

.wrapper a {
    width: 80px;
    text-align: center;
    padding: 10px 20px;
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
    border: 1px solid #e7e7e7;
}
.wrapper a:hover {
    color: #56dbc9 !important;
    border: 1px solid #99c9c2 !important;
}
</style>