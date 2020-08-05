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
                        <button class="btn btn-secondary" v-if="(writer === this.$cookies.get('nickname')) && this.$cookies.get('nickname')" @click="updatePost"><b-icon icon="trash"></b-icon> 수정</button>
                        <button class="btn btn-secondary" v-if="(writer === this.$cookies.get('nickname')) && this.$cookies.get('nickname')" @click="deletePost"><b-icon icon="trash"></b-icon> 삭제</button>
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
                  v-model = "comment_content"
              ></b-form-textarea>
          </div>
          <button class="btn btn-success btn-sm mx-1" @click='commentWrite'>답변 달기</button>
          <button class="btn btn-success btn-sm mx-1" @click='commentClose'>답변창 닫기</button>
          
      </span>
      <span v-else>
          <button class="btn btn-success btn-sm mx-1" @click='commentOpen'>답변창 열기</button>
      </span>

      </div>

      <div class="card rounded-lg mb-3 shadow p-3 bg-white rounded" v-for="comment in recentlyComments" :key="comment.replyId">
        <p>{{ comment.writer }}</p>
        <p>{{ comment.reply}}</p>
        <p>이 댓글을{{ comment.likes}}명이 좋아합니다</p>
        <p>{{ comment.createdAt }}</p>
        <button v-if="$cookies.get('nickname') === comment.writer" @click="commentDelete(comment)">삭제</button>
        <button v-if="$cookies.get('nickname') === comment.writer">수정</button>
      </div>
      
      

    </b-container>
  </div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
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

            comments: null,
            comment_content: '',
        }
     },
    methods: {
        commentOpen() {
          this.writeComment = true
        },
        commentClose() {
          this.writeComment = false
        },
        commentWrite() {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          axios.post(`${BACK_URL}/reply/${this.post_id}`,{ reply : this.comment_content },config)
            .then(() => {
              this.comment_content = ''
              this.getComment()
              this.writeComment = false
            })
            .catch(err => console.log(err))
        },
        commentDelete(comment) {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          if (comment.writer === this.$cookies.get('nickname')) {
            axios.delete(`${BACK_URL}/reply/${comment.replyId}`, config)
              .then(() => {
                this.getComment()
              })
              .catch(err => console.log(err))
          } else {
            alert("작성자가 다릅니다.")
          }
        },
        getComment() {
          axios.get(`${BACK_URL}/reply/${this.post_id}/replies`)
            .then(res => {
              this.comments = res.data.list
              })
            .catch(err => console.log(err))
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
              this.writer = res.data.list[0].list[0].writer
              this.subject = res.data.list[0].list[0].subject
              this.content = res.data.list[0].list[0].content
              this.createdAt = res.data.list[0].list[0].createdAt
            })
            .catch(err => console.log(err))
        },
        deletePost() {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          axios.delete(`${BACK_URL}/blog/${this.nickname}/${this.board_name}/${this.post_id}`,config)
            .then(() =>{
              alert('삭제되었습니다!')
              this.$router.push({ name : 'Home' })
            })
        }
    },
    created(){
       this.fetchPost(),
       this.getComment()
    },
    computed: {
      recentlyComments () {
        return _.orderBy(this.comments, ['createdAt'], ['desc'])
      },
    }
}
</script>

<style>

</style>