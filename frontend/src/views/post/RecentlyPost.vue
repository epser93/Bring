<template>
  <div class="wrapB container-fluid">
    <section class="cards row">
      <div class="col-lg-10 row">
        <div v-for="(post, index) in list" :key="post.postId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
          <div class="cardwrap">
            <div class="card-body p-0" @click="gotoDetail(post)">
              <div class="img-section" :style="{ 'background-image' : `url(${thumbnails[index]})`}">
                <a href=""></a>
              </div>
              <div class="contents">
                <h4>{{ post.subject }}</h4>
                <p>{{ post.content }}</p>
                <!-- <v-md-preview :text="post.content"></v-md-preview> -->
                <p class="comment-date">{{ post.createdAt.substring(0,10) }} · {{ post.replyCnt }}개의 댓글</p>
              </div>
            </div>
            <div class="writer-info">
              <button class="btn btn-sm" @click="gotoUserInfo(post.member_nickname)">{{ post.member_nickname }}</button>
              <p>♥ {{ post.likes }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="tag-list-wrap col-lg-2">
        <h4>인기 태그</h4>
        <ul class="tag-list text-left">
          <li @click="searchTags(tag)" v-for="(tag, index) in tags.slice(0,10)" :key="index" class="mb-3 pl-5 trendtags">
              # {{ tag }}
          </li> 
        </ul>
      </div>
    </section>
    <infinite-loading @infinite="infiniteHandler"></infinite-loading>
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
      page : 1,
      tags : [],
      text : '# 안녕하세요'
    }
  },
  methods : {
    infiniteHandler ($state) {
      axios.get(`${BACK_URL}/blog/recent?no=${this.page}`)
        .then(res => {
          if (res.data.list[0].list.length) {
            this.page += 1
            this.list.push(...res.data.list[0].list)
            this.thumbnails.push(...res.data.list[2].list)
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
      this.$router.push({ name : "Profile" , query: { nickname : userNickname }})
    },
    Init() {
      axios.get(`${BACK_URL}/blog/recent?no=1`)
        .then (res => {
          this.list = res.data.list[0].list
          this.thumbnails = res.data.list[2].list
        })
        .catch (err => console.log(err))
    },
    getTags() {
      axios.get(`${BACK_URL}/tags/blog`)
        .then(res => {
          this.tags = res.data.list[0].list
        })
        .catch(err => console.log(err))
    },
    searchTags(tag) {
      console.log(tag)
      this.$router.push({ name : 'TagSearch', params : { keyword : tag }})
    }
  },
  created() {
    // this.Init()
    this.getTags()
  },
}
</script>

<style>
h2 {
  width:100%;
  margin-bottom: 30px;
}

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
.trendtags {
  cursor: pointer;
}
</style>