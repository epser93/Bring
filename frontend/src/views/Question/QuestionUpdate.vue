<template>
  <div class="container mb-5 ">
    <b-container class="mt-5">
      <h2>글수정</h2>
      
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
    <b-form-tags input-id="tags-basic" v-model="questionData.tags" class="mb-2" label="파일 추가:" label-cols-sm="1"></b-form-tags>
  </div>

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
               
                this.$router.push({name:'QuestionDetail'})
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
            })
            .catch(err => {
                console.log(err)
            })
        },
    },
     created(){
        this.getQna()
        
        }
    }

    
    

</script>

<style>

</style>