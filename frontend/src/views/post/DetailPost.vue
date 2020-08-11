<template>
  <div id="detail" class="row">

    <!-- 글 부분(나중에 양 옆 채울것 필요) -->
    <div class="wrapper text-left col-12 col-lg-8">
        <div class="info">
            <h1 class="mb-3">{{subject}}</h1>
            <div class="text-right">
              <button class="btn btn-outline-warning btn-sm mx-1" v-if="(member_nickname === this.$cookies.get('nickname')) && this.$cookies.get('nickname')" @click="updatePost"><b-icon icon="trash"></b-icon> 수정</button>
              <button class="btn btn-outline-danger btn-sm mx-1" v-if="(member_nickname === this.$cookies.get('nickname')) && this.$cookies.get('nickname')" @click="deletePost"><b-icon icon="trash"></b-icon> 삭제</button>
          </div>
        </div>
        
        <!-- 태그부분 -->
        <div class="tag">
            <span v-for="(tag,index) in this.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2">{{ tag }}</span>
        </div>
        <hr>
        <p v-html="compiledMarkdown"></p>

        <!-- 포스트 정보 박스 -->
        <div class="bg-light" style="margin: 100px 0 50px;">
            <span class="mr-2"><strong>{{ member_nickname }}</strong></span>
            <span class="text-muted">{{ createdAt }}</span>
            <p>조회수: {{ views }}</p>
            
        </div>

        <!-- 좋아요 -->
        <div class="mb-5">
          <b-icon icon="heart-fill" v-if="likeItOrNot" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(false)"></b-icon>
          <b-icon icon="heart" v-if="!likeItOrNot" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(true)"></b-icon>          
          <span :ref="'like-count-' + post_id">{{ likes }}</span>
        </div>

        <!-- 댓글 입력 부분 -->
        <div id="commentTextArea" class="">
            <span v-if="writeComment">
              <div>
                  <b-form-textarea
                      id="textarea-rows"
                      placeholder="댓글을 작성해주세요!"
                      rows="4"
                      v-model = "comment_content"
                  ></b-form-textarea>
              </div>
              <button class="btn btn-success btn-sm mx-1" v-if="!commentUpdateToggle" @click='commentWrite'>답변 달기</button>
              <button class="btn btn-success btn-sm mx-1" v-if="commentUpdateToggle" @click='commentUpdate'>답변 수정</button>
              <button class="btn btn-success btn-sm mx-1" @click='commentClose'>답변창 닫기</button>
          </span>
          <span v-else>
              <button class="btn btn-success btn-sm mx-1" @click='commentOpen'>답변창 열기</button>
          </span>
        </div>

        <!-- 댓글 목록부분 -->
        <h3 class="mt-5">{{ recentlyComments.length }} Comments</h3>
        <div class="ml-3" v-for="(comment,index) in recentlyComments" :key="comment.replyId">
          <p>{{ comment.member_nickname }}</p>
          <p>{{ comment.reply }}</p>
          <!-- 좋아요 -->
          <b-icon icon="heart-fill" v-if="commentsLike[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="commentLike(comment.replyId, comment.member_nickname, false)"></b-icon>
          <b-icon icon="heart" v-if="!commentsLike[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="commentLike(comment.replyId, comment.member_nickname, true)"></b-icon>          
          <span :ref="'like-comment-' + comment.replyId">{{ comment.likes }}</span>
          <p>{{ comment.createdAt }}</p>
          <button class="btn btn-outline-success btn-sm mx-1" v-if="$cookies.get('nickname') === comment.member_nickname" @click="commentDelete(comment)">삭제</button>
          <button class="btn btn-outline-success btn-sm" v-if="$cookies.get('nickname') === comment.member_nickname" @click="openCommentUpdate(comment), setXY($event)">수정</button>
        </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
// 마크다운
import marked from 'marked'
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
            member_nickname: null,
            content: null,
            createdAt: null,
            views: null,
            likes: null,
            likeItOrNot: null,

            comments: null,
            comment_content: '',
            commentUpdateToggle : false,
            Y : 0,
            comment_id : 0,
            tags : '',
            commentsLike: []
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
        openCommentUpdate(comment) {
          this.writeComment = true
          let target = document.getElementById('commentTextArea')
          let targetTop = target.getBoundingClientRect().top;
          let abTop = window.pageYOffset + targetTop -100;
          window.scrollTo(0, abTop)
          this.commentUpdateToggle = true
          this.comment_content = comment.reply
          this.comment_id = comment.replyId
        },
        setXY(event) {
          this.Y = event.pageY
        },
        // isSelected 부분 다시 binding 해야함
        commentUpdate() {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          axios.put(`${BACK_URL}/reply/${this.comment_id}?isSelected=false`, { reply : this.comment_content },config)
            .then(() => {
              this.getComment()
              this.commentUpdateToggle = false,
              this.writeComment = false,
              this.comment_content = "",
              window.scrollTo(0, this.Y)
            })
            .catch(err => console.log(err))
        },
        commentDelete(comment) {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          const askDelete = confirm("정말 삭제하시겠습니까?")
          if (askDelete === true) {
            if (comment.member_nickname === this.$cookies.get('nickname')) {
              axios.delete(`${BACK_URL}/reply/${comment.replyId}`, config)
                .then(() => {
                  this.getComment()
                  alert("삭제가 완료되었습니다.")
                })
                .catch(err => console.log(err))
            } else {
              alert("작성자가 다릅니다.")
            }
          }
        },
        getComment() {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          axios.get(`${BACK_URL}/reply/${this.post_id}/replies`, config)
            .then(res => {
              console.log(res.data.list[0])
              this.comments = res.data.list[0].list
              this.commentsLike = res.data.list[1].list
              })
            .catch(err => console.log(err))
        },
        updatePost() {
          this.$router.push({ name : 'UpdateForm' , params: { nickname : this.member_nickname, boardName : this.board_name, post_id : this.post_id}})
        },
        fetchPost() {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          axios.get(`${BACK_URL}/blog/${this.nickname}/${this.board_name}/${this.post_id}`,config)
            .then(res => {
              this.member_nickname = res.data.list[0].list[0].member_nickname
              this.subject = res.data.list[0].list[0].subject
              this.content = res.data.list[0].list[0].content
              this.createdAt = res.data.list[0].list[0].createdAt
              this.likes = res.data.list[0].list[0].likes
              this.views = res.data.list[0].list[0].views
              // 좋아요 했는지
              this.likeItOrNot = res.data.list[4].list[0]
              this.tags = res.data.list[1].list
              console.log('fetch',res.data)
            })
            .catch(err => console.log(err))
        },
        deletePost() {
          const config = {
            headers: {
              'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
            }
          }
          const askConfirm = confirm("정말 삭제하시나요?")
          if (askConfirm === true) {
            axios.delete(`${BACK_URL}/blog/${this.nickname}/${this.board_name}/${this.post_id}`,config)
              .then(() =>{
                alert('삭제되었습니다!')
                this.$router.push({ name : 'Home' })
              })
          }
        },

        // 좋아요
        postLike(likeit) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                    'Content-Type': 'application/json'
                }
            }
            // 좋아요 현 상태로 구분
            
            if (likeit === false) {
                axios.post(`${BACK_URL}/blog/${this.member_nickname}/like/${this.post_id}`, likeit, config)
                    .then(res => {
                        // 좋아요 수 바꾸기(화면에서)
                        this.$refs[`like-count-${this.post_id}`].innerText = res.data.data    
                        this.likeItOrNot = false            
                    })
                    .catch(err => {
                        console.log(err)
                    })   
            } else {
                axios.post(`${BACK_URL}/blog/${this.member_nickname}/like/${this.post_id}`, likeit, config)
                    .then(res => {
                        // 좋아요 수 바꾸기(화면에서)
                        this.$refs[`like-count-${this.post_id}`].innerText = res.data.data   
                        this.likeItOrNot = true                    
                    })
                    .catch(err => {
                        console.log(err)
                    })   
            }        
        },
        // 댓글 좋아요
        commentLike(replyId, replyer, likeit) {          
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                    'Content-Type': 'application/json'
                }
            }

            if (likeit === false) {
              axios.post(`${BACK_URL}/reply/like/${replyId}/${replyer}`, likeit, config)
                  .then(res => {
                    console.log(this.$refs)
                    console.log(res.data.data)
                      this.$refs[`like-comment-${replyId}`][0].innerText = res.data.data 
                      this.getComment()       
                  })
                  .catch(err => {
                      console.log(err)
                  })   
            } else {
              axios.post(`${BACK_URL}/reply/like/${replyId}/${replyer}`, likeit, config)
                  .then(res => {
                      console.log(this.$refs)
                      console.log(res.data.data)
                      this.$refs[`like-comment-${replyId}`][0].innerText = res.data.data
                      this.getComment()   
                  })
                  .catch(err => {
                      console.log(err)
                  })
            }   

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
      compiledMarkdown: function () {
        return marked(this.content, { sanitize: true })
      },
    }
}
</script>

<style scoped>
#detail {
  display: flex;
  justify-content: center;
}
</style>