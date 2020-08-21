<template>
  <div class="wrapB container-fluid">
    <section class="cards row">
      <div class="col-lg-10 row">
        <div v-for="(post,index) in list" :key="post.postId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
          <div class="cardwrap">
            <div class="card-body p-0" @click="gotoDetail(post)">
              <div class="img-section" :style="{ 'background-image' : `url(${hotThumbnail[index]})`}">
                <a href=""></a>
              </div>
              <div class="contents">
                <h4>{{ post.subject }}</h4>
                <p class="comment-date">{{ post.createdAt.substring(0,10) }} · {{ post.replyCnt }}개의 댓글</p>
              </div>
            </div>
            <div class="writer-info">
              <button class="btn btn-sm" @click="gotoUserInfo(post.member_nickname)">{{ post.member_nickname }}</button>
              <p>♥ {{ post.likes}}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="tag-list-wrap col-lg-2 p-0 ml-4">
        <h4 class="mb-2">인기 태그</h4>
        <h5>TOP 30</h5>
        <ul class="tagcloud text-left mt-3">
          <li @click="searchTags(tag)" v-for="(tag, index) in tags.slice(0,30)" :key="index" class="tag-cloud-link">
              {{ tag }}
          </li> 
        </ul>
      </div>
    </section>
    <infinite-loading @infinite="infiniteHandler"></infinite-loading>
  </div>

</template>

<script>
import axios from 'axios'
const BACK_URL = 'http://i3c206.p.ssafy.io:8080/api'
export default {
  name: 'HotPost',
  props: {

  },
  data() {
    return {
      list: [],
      hotThumbnail: [],
      page : 1,
      tags: []
    }
  },
  created() {
    this.getTags()
  },
  methods: {
    infiniteHandler ($state) {
      axios.get(`${BACK_URL}/blog/trend?no=${this.page}`)
        .then (res => {
          if (res.data.list[0].list.length) {
            this.page += 1
            this.list.push(...res.data.list[0].list)
            this.hotThumbnail.push(...res.data.list[2].list)
            $state.loaded()
          } else {
            $state.complete()
          }
        })
        .catch(err => console.log(err))
    },
    gotoDetail(post) {
      this.$router.push({ name : "DetailPost" , params: { boardName: post.board_name, nickname : post.member_nickname, post_id : post.postId }})
    },
    getHotPost() {
      if (this.mode === "blog") {
        axios.get(`${BACK_URL}/blog/trend`)
          .then(res => {
            this.hotThumbnail = res.data.list[2].list
            this.postings = res.data.list[0].list
          })
          .catch(err => console.log(err))
      } else {
        axios.get(`${BACK_URL}/questions/trend`)
          .then (res=> {
            this.postings = res.data.list[0].list
          })
          .catch (err => console.log(err))
      }
    },
    getTags() {
      axios.get(`${BACK_URL}/tags/blog`)
        .then(res => {
          this.tags = res.data.list[0].list
        })
        .catch(err => console.log(err))
    },
    searchTags(tag) {
      this.$router.push({ name : 'TagSearch', params : { keyword : tag }})
    },
    gotoUserInfo(userNickname) {
      this.$router.push({ name : "Profile" , query: { nickname : userNickname }})
    },
  },
  computed: {
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
  height: 380px;
  margin-bottom: 30px;
  cursor: pointer;
  overflow: visible;
}

.cardwrap:hover {
  box-shadow: 1px 8px 20px grey;
  transform: translateY(-10px);
  transition: .3s ease-in;
  overflow: visible;
}

.img-section {
  width: 100%;
  height: 200px;
  background-image: url(https://picsum.photos/600/300/?image=25);
  background-repeat : no-repeat;
	background-size : contain;
  background-position: center center;
  border-top-right-radius: 6px;
  border-top-left-radius: 6px;
  border: 1px solid rgba(0,0,0,0.1);
}

.contents {
  height: 100px;
  padding: 10px;
  border: 1px solid rgba(0,0,0,0.1);
  border-top: 0px; 
}
.contents h4 {
  margin-bottom: 30px;
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