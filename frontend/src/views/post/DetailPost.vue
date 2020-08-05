<template>
  <div>
    <b-container>
      <div class="card rounded-lg shadow p-3 mb-5 bg-white rounded">
        <div>
          <h1></h1>
        </div>
        <b-container>
          <b-row>
            <b-col>{{subject}}</b-col>
            <b-col></b-col>
          <b-col>
            <p>
              작성자: {{ writer }}
              <span class="text-muted ">{{ createdAt }}</span>
            </p>
          </b-col>
          </b-row>
          </b-container>
      </div>
      <div class="card rounded-lg mt-5 shadow p-3 mb-5 bg-white rounded">
            <p><b-badge pill variant="success" class="mr-3">태그</b-badge>
            </p>
            <h5 class="card-text">{{ content }}
                <br><br><br><br><br><br><br><br><br><br><br><br>
                <hr>
                <div class="row">
                    <div class="col"></div>
                    <div class="col"></div>
                    <div class="col">
                        <button class="btn btn-secondary" @click="updatePost"><b-icon icon="trash"></b-icon> 수정</button>
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
    name:'DetailPost',
     data(){
        return {
            writeComment: false,
            // 넘겨받는 param
            post_id: this.$route.params.post_id,
            nickname: this.$route.params.nickname,
            board_name : this.$route.params.boardName,

            subject: null,
            writer: null,
            content: null,
            createdAt: null,

            // qpost_id: this.$route.params.qpostId,
            // qPost: null,
        }
     },
    methods: {
        commentOpen() {
          this.writeComment = true
        },
        commentClose() {
          this.writeComment = false
        },
        updatePost() {
          this.$router.push({ name : 'UpdateForm' , params: { nickname : this.writer, boardName : this.board_name, post_id : this.post_id}})
        },
        fetchPost() {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          axios.get(`${BACK_URL}/blog/${this.nickname}/${this.board_name}/${this.post_id}`,config)
            .then(res => {
              console.log(res.data.list[0].list[0])
              this.writer = res.data.list[0].list[0].writer
              this.subject = res.data.list[0].list[0].subject
              this.content = res.data.list[0].list[0].content
              this.createdAt = res.data.list[0].list[0].createdAt
              console.log(res.data.list[0].list[0].content)
            })
            .catch(err => console.log(err))
        }
    },
     created(){
       console.log(this.post_id)
       console.log(this.nickname)
       console.log(this.board_name)
       this.fetchPost()

      }
}
</script>

<style>

</style>