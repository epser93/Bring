<template>
  <!--제목-->
<div class="container mb-5 ">
    <b-container class="mt-5">
      <h2>글쓰기</h2>
      
      <hr>
    <b-row class="my-1">
      <b-col sm="3">
        <label>제목</label>
      </b-col>
      <b-col sm="9">
        <b-form-input v-model="questionData.subject"></b-form-input>
      </b-col>
    </b-row>
  <v-md-editor class="text-left" v-model="questionData.content" height="600px"></v-md-editor>

<!--태그 추가-->
  <div>
    <b-form-input v-model="questionData.tag" placeholder="Enter your name" label="파일 첨부 "></b-form-input>
    <!-- <b-form-tags input-id="tags-basic" v-model="questionData.tag" class="mb-2" label="파일 추가:" label-cols-sm="1"></b-form-tags> -->
    <!-- <p>태그: {{ category }}</p> 이부분에 나오게 해야함 -->
  </div>

  <div>
    <b-button @click="qnaWrite" variant="outline-primary">작성</b-button>
  </div>
  </b-container>
</div>
</template>
<script>

import axios from 'axios'
const BACK_URL = 'http://localhost:8080'

export default {
   name: 'QuestionWrite',
    data() {
      return {
        questionData:{
          content:"",
          subject:"",       
          tag:"",
        },
      }
    },
    methods: {
      qnaWrite(){
        console.log(this.questionData)
        const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
        axios.post(`${BACK_URL}/qna/question`,this.questionData,config)
        .then(res=>{
          console.log(res)
          this.$router.push({name : 'Question'})
        })
        .catch(err=>{
          console.log(err)
        })
      }
    }
      }

</script>