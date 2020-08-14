<template>
  <div class="container mb-5 ">
    <b-container class="mt-5">
      <h2>글수정</h2>
      
      <hr>
      <!-- 썸네일 ( 이 부분 추가하면 에러남)-->
      <!-- <div class="row ml-1 mt-5 mb-5">
        <label for="thumbnailInput">썸네일 : </label>
        <input @change="thumbnailSelect" type="file" ref="thumbnailImage" id="thumbnailInput">
      </div> -->

    <b-row class="my-1">
      <b-col sm="3">
        <label>제목</label>
      </b-col>
      <b-col sm="9">

        <b-form-input v-model="questionData.subject"></b-form-input>
      </b-col>
    </b-row>
  <v-md-editor class="text-left" v-model="questionData.content" height="600px"></v-md-editor>

<!-- 수빈 태그 추가
  <div>
    <b-form-tags input-id="tags-basic" v-model="questionData.tags" class="mb-2" label="파일 추가:" label-cols-sm="1"></b-form-tags>
  </div> -->

  <!--태그 추가-->
  <div class="tag mt-3">
    <span v-for="(tag,index) in questionData.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2" @click="deleteTag(index)">{{ tag }}</span>
  </div>
  <input placeholder="태그를 입력해주세요" class="mb-5 tag-input" type="text" v-model="tag" @keydown.enter="postTag">

  <div>
    <b-button @click="modifyQuestion" variant="outline-primary">수정</b-button>
  </div>
  </b-container>
</div>
</template>

<script>

import axios from 'axios'
const BACK_URL = 'http://localhost:8080'

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
            thumbnail:"",
            
        }
    },
    methods:{
        // 질문 수정
        modifyQuestion(){
            const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
            axios.put(`${BACK_URL}/questions/${this.qpost_id}`,this.questionData,config)
            .then(res=>{
                console.log(res)
                this.thumbnailPost()
                this.$router.push({name:'QuestionDetail'})
            })
            .catch(err=>{
                console.log(err)
            })
        },
        // 기존 질문 내용 호출
        getQna() {
          console.log(this.qpost_id)
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
          if (this.tag === null || this.tag.replace(/^\s*|\s*$/g, '').length === 0) {
            alert('빈칸은 태그로 입력 불가능합니다.')
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
        // 썸네일
        thumbnailSelect() {
          this.thumbnail = this.$refs.thumbnailImage.files[0]
      },
        thumbnailPost() {
          const formData = new FormData()
          formData.append('files', this.thumbnail)

          const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                'Content-Type' : 'multipart/form-data'
              }
            }
            axios.post(
              `${BACK_URL}/questions/ask/${this.qpost_id}/uploads`, formData, config)
              .then(() =>{
                this.$router.go(-1)
              })
              .catch((err) => {
                alert('에러')
                console.error(err)
              })      
      },

     created(){
        this.getQna()
        
        }
    }

    
    

</script>

<style scoped>
.badge {
  cursor: pointer;
}

.tag-input {
  width: 100%
}
</style>