<template>
  <div id="app">
     <b-container>
         <div class="card rounded-lg shadow p-3 mb-5 bg-white rounded">
         <div>
             <h1></h1>
         </div>
         <b-container>
            <b-row>
                <b-col>{{qPost.subject}}</b-col>
                <b-col></b-col>
            <b-col>
                <p>
                작성자: {{qPost.writer}}
                <span class="text-muted ">{{qPost.createdAt}}</span>
                </p>
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
                          <button class="btn btn-secondary"><b-icon icon="trash"></b-icon> 삭제</button>
                      </div>
                  </div>
              </h5>

        </div>

        <div class="card rounded-lg mt-5 shadow p-3 mb-5 bg-white rounded">
             <span v-if="writeComment">
                 <div>
                <b-form-textarea
                    id="textarea-rows"
                    placeholder="Tall textarea"
                    rows="8"
                ></b-form-textarea>
            </div>
            <button class="btn btn-success btn-sm mx-1" @click='commentOpen'>답변 달기</button>
            <button class="btn btn-success btn-sm mx-1" @click='commentClose'>답변창 닫기</button>
            <!-- 누르면 새로 렌더해주게끔 로직짜야함-->
            
        </span>
        <span v-else>
            <button class="btn btn-success btn-sm mx-1" @click='commentOpen'>답변창 열기</button>
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
            qpost_id: this.$route.params.qpostId,
            qPost: [],
        }
     },
    methods: {
        commentOpen() {
            this.writeComment = true
        },
        commentClose() {
            this.writeComment = false
        },
        getQna() {
            console.log(this.qPost)
            axios.get(`${BACK_URL}/questions/${this.qpost_id}`)
            .then(res => {
                this.qPost = res.data.data
            })
            .catch(err => {
                console.log(err)
            })
        },
    },
     created(){
        this.getQna(this.qpostId)
        }
}
</script>

<style>

</style>