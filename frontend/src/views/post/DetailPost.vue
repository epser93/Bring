<template>
  <div id="detail" class="row">

    <!-- 중심 부분 -->
    <div class="wrapper text-left col-12 col-lg-8 my-5 px-5">
        <!-- 글쓴이 정보 -->
        <div class="info text-center my-5">
            <h1 class="mb-3">{{subject}}</h1>
            <span class="text-muted">{{createdAt.slice(0, 10)}}</span>
            <span class="vertical-line mx-3"></span>
            <span class="mr-2" @click="gotoProfile(member_nickname)" id="post_writer"><strong>{{ member_nickname }}</strong></span>
            <div class="text-right">
              <a class="mx-1" v-if="(member_nickname === this.$cookies.get('nickname')) && this.$cookies.get('nickname')" @click="deletePost"><b-icon icon="trash"></b-icon> 삭제</a>
              <a class="mx-1" v-if="(member_nickname === this.$cookies.get('nickname')) && this.$cookies.get('nickname')" @click="updatePost"><b-icon icon="pen"></b-icon> 수정</a>
          </div>
        </div>
        <hr>
        
        <!-- 글 -->
        <v-md-preview :text="this.content"></v-md-preview>

        <!-- 기타 정보 -->
        <div class="" style="margin: 100px 0 50px;">     
            <b-icon icon="heart-fill" v-if="likeItOrNot" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(false)"></b-icon>
            <b-icon icon="heart" v-if="!likeItOrNot" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(true)"></b-icon>          
            <span :ref="'like-count-' + post_id">{{ likes }}</span>
            <span class="vertical-line mx-3"></span>
            <i class="far fa-eye mr-1"></i>{{ views }}
        </div>

        <!-- 댓글 목록부분 -->
        <h3 class="mt-5 mb-4">{{ recentlyComments.length }} Comments</h3>
        
        <!-- 댓글 입력 부분 -->
        <div id="commentTextArea" class="mb-5">
            <span v-if="writeComment">
              <div>
                  <b-form-textarea
                      id="textarea-rows"
                      placeholder="댓글을 작성해주세요!"
                      rows="4"
                      v-model = "comment_content"
                  ></b-form-textarea>
              </div>
              <a class="float-right " v-if="!commentUpdateToggle" @click='commentWrite'><b-icon icon="pencil-square"></b-icon>작성</a>
              <a class="float-right " v-if="commentUpdateToggle" @click='commentUpdate'>수정</a>
              <a class="float-right " @click='commentClose'>닫기</a>
          </span>
          <span v-else>
              <a class="p-2" v-if="this.$cookies.get('X-AUTH-TOKEN')" @click='commentOpen'>답변창 열기</a>
          </span>
        </div>

        <!-- 댓글 목록부분 -->
        <div class="ml-3" v-for="(comment,index) in recentlyComments" :key="comment.replyId">
          <strong @click="gotoProfile(comment.member_nickname)" id="comment_writer">{{ comment.member_nickname }}</strong>
          <p class="my-3">{{ comment.reply }}</p>
          <!-- 좋아요 -->
          <b-icon icon="heart-fill" v-if="commentsLike[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="commentLike(comment.replyId, comment.member_nickname, false)"></b-icon>
          <b-icon icon="heart" v-if="!commentsLike[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="commentLike(comment.replyId, comment.member_nickname, true)"></b-icon>          
          <span :ref="'like-comment-' + comment.replyId">{{ comment.likes }}</span>
          <span class="vertical-line mx-3"></span>
          <span>{{ comment.createdAt.slice(0,10) }}</span>
          <a class="ml-4 mr-2" v-if="$cookies.get('nickname') === comment.member_nickname" @click="commentDelete(comment)"><b-icon icon="trash"></b-icon>삭제</a>
          <a class="" v-if="$cookies.get('nickname') === comment.member_nickname" @click="openCommentUpdate(comment), setXY($event)"><b-icon icon="pen"></b-icon>수정</a>
          <hr>
        </div>
    </div>
          
    <!-- 오른쪽 바 -->
    <div class="mt-5 col-6 col-lg-3 text-left pl-5">
      <!-- 태그 리스트 -->
      <h4 class="">태그</h4>
      <div class="tagcloud mb-5">
          <a v-for="(tag,index) in this.tags" :key="index" @click="searchTag(tag)" class="tag-cloud-link">{{ tag }}</a>
      </div>

      <!-- 카테고리 글(카테고리 내부에서 또 글 번호 매겨져야?) -->
      <h4 class="mb-3">"{{ board_name }}" 의 다른 글</h4>
      <hr>
      <div v-for="(item, index) in postListCategory" :key="index" class="">
        <div v-if="item.postId != post_id" class="category-posts mb-4 p-0">
          <div class="card-wrapper">
              <div @click="gotoDetail(item)" class="img-section" :style="{ 'background-image' : `url(${thumbnail2[index]})`}">
                
              </div>
          </div>
          <div class="text-left ml-2 p-3">
            <p @click="gotoDetail(item)"><strong>{{ item.subject }}</strong></p>
            <small class="text-muted ml-1">{{ item.createdAt.slice(0, 10) }}</small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
const BACK_URL = 'http://i3c206.p.ssafy.io/api'

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
            content: '',
            createdAt: '',
            views: null,
            likes: null,
            likeItOrNot: null,
            thumbnail: null,

            comments: null,
            comment_content: '',
            commentUpdateToggle : false,
            Y : 0,
            comment_id : 0,
            tags : '',
            commentsLike: [],

            // 같은 카테고리 다른 글들
            postListCategory: [],
            thumbnail2: [],
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
              this.comments = res.data.list[0].list
              this.commentsLike = res.data.list[1].list.reverse()
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
              this.thumbnail = res.data.list[5].list[0]
              // 좋아요 했는지
              this.likeItOrNot = res.data.list[4].list[0]
              this.tags = res.data.list[1].list
            })
            .catch(err => {
              if (err) {
                this.$router.push({name:'PageNotFound'})
              }
            })
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
                this.$router.push({ name: 'MyBlog', params: { nickname : this.nickname } })
              })
          }
        },

        // 좋아요
        postLike(likeit) {
          if (!this.$cookies.get('X-AUTH-TOKEN')) {
            alert('비회원은 게시글 좋아요를 할 수 없어요~')
          } else if (this.member_nickname === this.$cookies.get('nickname')) {
            alert('본인이 쓴 글은 좋아요 할 수 없어요~') 
          } else {
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
          }
        },
        // 댓글 좋아요
        commentLike(replyId, replyer, likeit) {
          if (!this.$cookies.get('X-AUTH-TOKEN')) {
            alert('비회원은 좋아요를 누를수 없어요~')
          } else if (replyer !== this.$cookies.get('nickname')) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                    'Content-Type': 'application/json'
                }
            }

            if (likeit === false) {
              axios.post(`${BACK_URL}/reply/like/${replyId}/${replyer}`, likeit, config)
                  .then(res => {
                      this.$refs[`like-comment-${replyId}`][0].innerText = res.data.data 
                      this.getComment()       
                  })
                  .catch(err => {
                      console.log(err)
                  })   
            } else {
              axios.post(`${BACK_URL}/reply/like/${replyId}/${replyer}`, likeit, config)
                  .then(res => {
                      this.$refs[`like-comment-${replyId}`][0].innerText = res.data.data
                      this.getComment()   
                  })
                  .catch(err => {
                      console.log(err)
                  })
            }   
          } else if (replyer === this.$cookies.get('nickname')) {
            alert('본인 댓글에는 좋아요를 누를수 없어요~')
          }

        },
        // 카테고리에 맞는 포스트만 가져오기
        getSomePosts() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            axios.get(`${BACK_URL}/blog/${this.nickname}/${this.board_name}/post_list?no=1`, config)
                .then(res => {
                    this.postListCategory = res.data.list[0].list.slice(0,6)
                    this.thumbnail2 = res.data.list[2].list.slice(0,6)
                })
                .catch(err => {
                    console.log(err)
                })
        },
        // 포스트 디테일
        gotoDetail(post) {
            this.$router.push({ name : "DetailPost" , params: { boardName: post.board_name, nickname : post.member_nickname, post_id : post.postId }})
        },   
        // 태그 검색
        searchTag(tag) {
          this.$router.push({ name : 'TagSearch', params: { keyword : tag }})
        },
        // 프로필 가기
        gotoProfile(nickname) {
          this.$router.push({ name : 'Profile', query: { nickname: nickname } })
        }
    },
    created(){
       this.fetchPost(),
       this.getComment(),
       this.getSomePosts()
    },
    computed: {
      recentlyComments () {
        return _.orderBy(this.comments, ['createdAt'], ['desc'])
      },
    },
    watch: {
      '$route.params.post_id' () {
        // 동일한 경로의 params 변경 사항에 반응하려면
        location.reload()
      }
    }
}
</script>

<style scoped>
#detail {
  display: flex;
  justify-content: center;
  font-family: 'Noto Serif KR', serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f4f4f4;
  min-height:100vh
}

.wrapper {
  background-color: white;
  border: 1px solid #e7e7e7;
  min-height: 700px;
}

#detail a {
    padding: 5px 10px;
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
    border: 1px solid #e7e7e7;
}

#detail a:hover {
    color: #56dbc9 !important;
    border: 1px solid #99c9c2 !important;
}

#textarea-rows {
  border: 1px solid #c0c0c0;
  border-radius: 0;
}

#textarea-rows:focus {
  border: 1px solid #56dbc9 !important;
}

.tagcloud {
  padding: 0; }
  .tagcloud a {
    text-transform: uppercase;
    display: inline-block;
    padding: 4px 10px;
    margin-bottom: 7px;
    margin-right: 4px;
    border-radius: 4px;
    color: #000000;
    border: 1px solid #ccc;
    font-size: 11px; }
    .tagcloud a:hover {
      color: #56dbc9;
      transition-duration: 0.5s;
      border: 1px solid #000; }

.category-posts {
  background-color: white;
  border: 1px solid #e7e7e7;  
}

.category-posts p {
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
}
.category-posts p:hover {
    color: #56dbc9 !important;
}

.img-section {
  transform: scale(1);
  -webkit-transform: scale(1);
  -moz-transform: scale(1);
  -ms-transform: scale(1);
  -o-transform: scale(1);
  transition: all 0.3s ease-in-out;
}

.img-section {
  width: 100%;
  height: 180px;
  background-repeat : no-repeat;
	background-size : contain;
  background-position: center center;
  border-top-right-radius: 0px;
  border-top-left-radius: 0px;
  border: none;
}

.img-section:hover {
  transform: scale(1.1);
  -webkit-transform: scale(1.1);
  -moz-transform: scale(1.1);
  -ms-transform: scale(1.1);
  -o-transform: scale(1.1);
}

.card-wrapper {
  height: 180px; 
  width:100%;
  overflow:hidden;
  cursor: pointer;
  } 

.card-wrapper img {
  height: 180px; 
  width:100%;
}

.tag-cloud-link {
  cursor: pointer;
}

#comment_writer {
  cursor: pointer;
}

#post_writer {
  cursor: pointer;
}
</style>