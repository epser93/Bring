<template>
  <div class="wrapB container-fluid">
    <section v-if="mode==='blog'" class="cards row">
      <div v-for="(post, index) in posts" :key="post.postId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
        <div class="cardwrap">
          <div class="card-body p-0" @click="gotoDetail(post)">
            <div class="img-section" :style="{ 'background-image' : `url(${thumbnail[index]})`}">
              <a href=""></a>
            </div>
            <div class="contents">
              <h4>{{ post.subject }}</h4>
              <p>{{ post.content }}</p>
              <p class="comment-date">{{ post.createdAt}} · {{ post.replyCnt }}개의 댓글</p>
            </div>
          </div>
          <div class="writer-info">
            <button class="btn btn-sm" @click="gotoUserInfo(post.member_nickname)">{{ post.member_nickname }}</button>
            <p>♥ {{ post.likes }}</p>
          </div>
        </div>
      </div>
    </section>

    <section v-if="mode==='QnA'" class="cards row">
      <div class="col-lg-10 row">
        <div v-for="post in posts" :key="post.qpostId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
          <div class=cardwrap>
            <div class="card-body p-0" @click="gotoQuestionDetail(post)">
              <div class="img-section">
                <a href=""></a>
              </div>
              <div class="contents">
                <h4>{{ post.subject }}</h4>
                <p>{{ post.content }}</p>
                <p class="comment-date">{{ post.createdAt}} · {{ post.answerCnt }}개의 답변</p>
              </div>
            </div>
            <div class="writer-info">
              <button class="btn btn-sm" @click="gotoUserInfo(post.member_nickname)">{{ post.member_nickname }}</button>
              <p><i class="far fa-eye"></i> {{ post.views }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="mode==='QnA'" class="tag-list-wrap col-lg-2">
        <h4>명예의전당</h4>
        <ul class="tag-list">
          <li v-for="(ranker, index) in sortRanking.slice(0,5)" :key="index">
              {{ index + 1 }}등 : {{ranker.nickname}}({{ ranker.score}}점)
          </li> 
        </ul>
      </div>   
    </section>
  </div>

</template>

<script>
import axios from 'axios'
import _ from 'lodash'
const BACK_URL = 'http://localhost:8080'

export default {
  name: 'PostList',
  props: {
    mode: String,
    posts: Array,
    thumbnail : Array
  },
  data() {
    return {
      unsortedRank : [],
    }
  },
  methods : {
    gotoDetail(post) {
      this.$router.push({ name : "DetailPost" , params: { boardName: post.board_name, nickname : post.member_nickname, post_id : post.postId }})
    },
    gotoQuestionDetail(post) {
      this.$router.push({ name : "QuestionDetail" , params: { nickname : post.member_nickname, qpostId : post.qpostId }})
    },
    // gotoUserInfo(userNickname) {
    //   this.$router.push({ name : "MyBlog" , params: { nickname : userNickname }})
    // },
    gotoUserInfo(userNickname) {
      console.log(userNickname)
      this.$router.push({ name : "Profile" , params: { userNickname : userNickname }})
    },
    getRanking() {
      axios.get(`${BACK_URL}/member/rank`)
        .then(res => {
          this.unsortedRank = res.data.list
          // console.log(this.unsortedRank)
        })
        .catch(err => console.log(err))
    }
  },
  created() {
    this.getRanking()
  },
  computed: {
    sortRanking () {
      return _.orderBy(this.unsortedRank, 'score', 'desc')
    }
  }
}
</script>

<style>
/* .wrapB {
  display: flex;
  margin-left: 20px;
  margin-right: 20px;
} */

h2 {
  width:100%;
  margin-bottom: 30px;
}
/* .cards {
  flex: 4;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
} */

/* .tag-list-wrap {
  flex:1;
} */
.tag-list-wrap h4 {
  margin-bottom: 30px
}
.tag-list {
  list-style-type: none;
  padding:0;
}

.card1 {
  box-sizing: border-box;
  width: 380px;
  height: 450px;
  margin-bottom: 30px;
  overflow: hidden;
  cursor: pointer;
}

.cardwrap {
  box-shadow: 10px 0px 60px -40px black
}

.img-section {
  width: 100%;
  height: 200px;
  background-image: url(https://picsum.photos/600/300/?image=25);
  background-repeat : no-repeat;
	background-size : 100% 100%;
  border-top-right-radius: 6px;
  border-top-left-radius: 6px;
  border: 1px solid rgba(0,0,0,0.1);
}

.contents {
  height: 200px;
  padding: 10px;
  border: 1px solid rgba(0,0,0,0.1);
  border-top: 0px; 
}
.contents h4 {
  margin-bottom: 10px;
  height: 30px;
  overflow: hidden;
}

.contents p {
  margin-bottom: 10px;
  height: 100px;
  text-align: left;
  overflow: hidden;
}

.writer-info {
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 6px;
  padding:10px;
  border: 1px solid rgba(0,0,0,0.1);
  border-top: 0px;
  display: flex;
  justify-content: space-between;
}
p {
  margin: 0
}
.comment-date {
  font-size: 12px;
  text-align: left;
}
</style>