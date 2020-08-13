<template>
  <div class="wrapB container-fluid">
    <section class="cards row">
      <div v-for="(post, index) in list" :key="post.postId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
        <div class="cardwrap">
          <div class="card-body p-0" @click="gotoDetail(post)">
            <div class="img-section" :style="{ 'background-image' : `url(${thumbnails[index]})`}">
              <a href=""></a>
            </div>
            <div class="contents">
              <h4>{{ post.subject }}</h4>
              <p>{{ post.content }}</p>
              <p class="comment-date">{{ post.createdAt.substring(0,10) }} · {{ post.replyCnt }}개의 댓글</p>
            </div>
          </div>
          <div class="writer-info">
            <button class="btn btn-sm" @click="gotoUserInfo(post.member_nickname)">{{ post.member_nickname }}</button>
            <p>♥ {{ post.likes }}</p>
          </div>
        </div>
      </div>
      <infinite-loading @infinite="infiniteHandler"></infinite-loading>
    </section>
  </div>
</template>

<script>
import axios from 'axios'
const BACK_URL = 'http://localhost:8080'

export default {
  name: 'RecentlyPost',
  data() {
    return {
      list: [],
      thumbnails: [],
      page : 2,
    }
  },
  methods : {
    infiniteHandler ($state) {
      console.log($state)
      console.log('mode',this.mode)
      axios.get(`${BACK_URL}/blog/recent?no=${this.page}`)
        .then(res => {
          if (res.data.list[0].list.length) {
            this.page += 1
            this.list.push(...res.data.list[0].list)
            this.thumbnails.push(...res.data.list[2].list)
            // console.log(this.thumbnails)
            $state.loaded()
          } else {
            $state.complete()
          }
        })
        .catch (err => console.log(err))
    },
    gotoDetail(post) {
      this.$router.push({ name : "DetailPost" , params: { boardName: post.board_name, nickname : post.member_nickname, post_id : post.postId }})
    },
    gotoUserInfo(userNickname) {
      console.log(userNickname)
      this.$router.push({ name : "Profile" , query: { nickname : userNickname }})
    },
    Init() {
      axios.get(`${BACK_URL}/blog/recent?no=1`)
        .then (res => {
          this.list = res.data.list[0].list
          this.thumbnails = res.data.list[2].list
          console.log('초기화 blog', res)
          // console.log(this.thumbnail)
        })
        .catch (err => console.log(err))
    }
  },
  created() {
    this.Init()
  },
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