<template>
  <div class="wrapB container-fluid">
    <section v-if="mode==='blog'" class="cards row">
      <div v-for="post in posts" :key="post.postId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
        <div class="cardwrap">
          <div class="card-body p-0" @click="gotoDetail(post)">
            <div class="img-section">
              <a href=""></a>
            </div>
            <div class="contents">
              <h4>{{ post.subject }}</h4>
              <p>{{ post.content }}</p>
              <p class="comment-date">{{ post.createdAt}} · {{ post.replyCnt }}개의 댓글</p>
            </div>
          </div>
          <div class="writer-info">
            <button class="btn btn-sm" @click="gotoUserInfo(post.writer)">{{ post.writer }}</button>
            <p>♥ {{ post.likes }}</p>
          </div>
        </div>
      </div>
    </section>

    <section v-if="mode==='QnA'" class="cards row">
      <div class="col-lg-10 row">
        <div v-for="post in orderedPosts" :key="post.qpost_id" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
          <div class=cardwrap>
            <div class="card-body p-0" @click="gotoQuestionDetail(post)">
              <div class="img-section">
                <a href=""></a>
              </div>
              <div class="contents">
                <h4>{{ post.subject }}</h4>
                <p>{{ post.content }}</p>
                <p class="comment-date">{{ post.createdAt}} · {{ post.answerCnt }}개의 댓글</p>
              </div>
            </div>
            <div class="writer-info">
              <button class="btn btn-sm" @click="gotoUserInfo(post.writer)">{{ post.writer }}</button>
              <p>♥ {{ post.views }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="mode==='QnA'" class="tag-list-wrap col-lg-2">
        <h4>명예의전당</h4>
        <ul class="tag-list">
          <li>
              1등 : user1
          </li> 
          <li>
              2등 : user2
          </li> 
          <li>
              3등 : user3
          </li> 
        </ul>
      </div>   
    </section>
  </div>

</template>

<script>
import _ from 'lodash'
export default {
  name: 'PostList',
  props: {
    mode: String,
    posts: Array,
  },
  methods : {
    gotoDetail(post) {
      this.$router.push({ name : "DetailPost" , params: { boardName: post.board_name, nickname : post.writer, post_id : post.postId }})
    },
    gotoQuestionDetail(post) {
      this.$router.push({ name : "DetailPost" , params: { post: post, nickname : post.writer, post_id : post.qpost_id }})
    },
    gotoUserInfo(userNickname) {
      this.$router.push({ name : "MyBlog" , params: { nickname : userNickname }})
    }
  },
  computed: {
    // qna때문에 남겨짐 지워야함!
    orderedPosts () {
      return _.orderBy(this.posts, 'createdAt', 'desc')
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

.img-section {
  width: 100%;
  height: 200px;
  background-image: url(https://picsum.photos/600/300/?image=25);
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