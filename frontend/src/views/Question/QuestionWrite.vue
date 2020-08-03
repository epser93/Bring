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
        <b-form-input v-model="questionData.title"></b-form-input>
      </b-col>
    </b-row>
  <v-md-editor class="text-left" v-model="questionData.text" height="600px"></v-md-editor>


<!--태그 추가-->
  <div>
    <b-form-tags input-id="tags-basic" v-model="questionData.category" class="mb-2" label="파일 추가:" label-cols-sm="1"></b-form-tags>
    <!-- <p>태그: {{ category }}</p> 이부분에 나오게 해야함 -->
  </div>

  <div>
    <b-button @click="questionWrite" variant="outline-primary"> <router-link to="/ask">작성</router-link></b-button>
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
          title:"",
          text:"",
          category:[],
        },
      }
    },
    methods: {
      // 로직 구현, 아직 백엔드 완성되자않아서 추후 수정해야함
      questionWrite(questionData){
        console.log(questionData)
        axios.post(`${BACK_URL}/ /`,questionData)
        .then(res=>{
          console.log(res)
        })
        .catch(err=>{
          console.log(err)
        })
      }
    }
  
}
</script>