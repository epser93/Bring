<template>
  <!--제목-->
<div class="container mb-5 ">
    <b-container class="mt-5">
      <h2>글쓰기</h2>
      <hr>
  <div class="row ml-1 mt-5 mb-5">
      <label for="thumbnailInput">썸네일 : </label>
      <input @change="thumbnailSelect" type="file" ref="thumbnailImage" id="thumbnailInput">
    </div>

    <b-row class="my-1">
      <b-col sm="3">
        <label>제목</label>
      </b-col>
      <b-col sm="9">
        <b-form-input v-model="questionData.subject"></b-form-input>
      </b-col>
    </b-row>
  <v-md-editor class="text-left" v-model="questionData.content" height="600px"></v-md-editor>

<!-- 수빈태그 추가
  <div>
    
    <b-form-tags input-id="tags-basic" v-model="questionData.tags" class="mb-2" label="파일 추가:" label-cols-sm="1"></b-form-tags>
    
  </div> -->

  <!--태그 추가-->
  <div class="tag mt-3">
    <span v-for="(tag,index) in questionData.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2" @click="deleteTag(index)">{{ tag }}</span>
  </div>
  <input placeholder="태그를 입력해주세요" class="mb-5 tag-input" type="text" v-model="tag" @keydown.enter="postTag">

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
          tags:[],
        },
        thumbnail: '',
        // 태그
        tag: ""
      }
    },
    methods: {
      // 게시물 작성
      qnaWrite(){
        const config = {
              headers: {
                'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
              }
            }
        axios.post(`${BACK_URL}/questions/ask`,this.questionData,config)
        .then(res=>{
          console.log(res)
          this.thumbnailPost()
          this.$router.push({name : 'Question'})
          this.$router.go(0) // 이 부분 안해주면 question으로 돌아갈 때 썸네일이 안보임
        })
        .catch(err=>{
          console.log(err)
        })
      },
      // 썸네일
      thumbnailSelect() {
        this.thumbnail = this.$refs.thumbnailImage.files[0]
      },
      // 썸네일 게시
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
            `${BACK_URL}/questions/ask/uploads`, formData, config)
            .then((res) =>{
              console.log(res)
            })
            .catch((err) => {
              console.error(err)
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