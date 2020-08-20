<template>
  <div class="wrapB container-fluid">
    <section class="cards row">
      <div class="col-lg-10 row">
        <div v-for="(post, index) in list" :key="index" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
          <div class=cardwrap>
            <div class="card-body p-0" @click="gotoQuestionDetail(post)">
              <div class="img-section" :style="{ 'background-image' : `url(${thumbnails[index]})`}">
                <a href=""></a>
              </div>
              <div class="contents">
                <h4>{{ post.subject }}</h4>
                <!-- <p>{{ post.content }}</p> -->
                <p class="comment-date">{{ post.createdAt.substring(0,10) }} · {{ post.answerCnt }}개의 댓글</p>
              </div>
            </div>
            <div class="writer-info">
              <button class="btn btn-sm" @click="gotoUserInfo(post.member_nickname)">{{ post.member_nickname }}</button>
              <p><i class="far fa-eye"></i> {{ post.views }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="tag-list-wrap col-lg-2 p-0 ml-4">
        <h4>명예의전당</h4>
        <ul class="tag-list text-left px-3">
          <li v-for="(ranker, index) in sortRanking.slice(0,5)" :key="index" @click="gotoUserInfo(ranker.nickname)" id="ranker">
              <span class="mr-2" style="color: gray;">{{ index + 1 }}위</span>{{ranker.nickname}}<span class="float-right">{{ ranker.score}}점</span>
          </li> 
        </ul>
        
        <h4 class="mt-5 mb-2">인기 태그</h4>
        <h5>TOP 20</h5>
        <ul class="tagcloud text-left mt-3">
          <li @click="searchTags(tag)" v-for="(tag, index) in tags.slice(0,20)" :key="index" class="tag-cloud-link">
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
import _ from 'lodash'
const BACK_URL = 'http://i3c206.p.ssafy.io'

export default {
  name: 'RecentlyQuestion',
  data() {
    return {
      unsortedRank : [],
      thumbnails : [],
      list: [],
      page : 1,
      tags : [],
    }
  },
  methods : {
    infiniteHandler($state) {
      axios.get(`${BACK_URL}/questions/recent?no=${this.page}`)
        .then (res => {
          if (res.data.list[0].list.length) {
            this.page += 1
            this.list.push(...res.data.list[0].list)
            this.thumbnails.push(...res.data.list[1].list)
            $state.loaded()
          } else {
            $state.complete()
          }
        })
        .catch(err => console.log(err))
    },
    gotoQuestionDetail(post) {
      this.$router.push({ name : "QuestionDetail" , params: { nickname : post.member_nickname, qpostId : post.qpostId }})
    },
    gotoUserInfo(userNickname) {
      this.$router.push({ name : "Profile" , query: { nickname : userNickname }})
    },
    getRanking() {
      axios.get(`${BACK_URL}/member/rank`)
        .then(res => {
          this.unsortedRank = res.data.list
        })
        .catch(err => console.log(err))
    },
    getTags() {
      axios.get(`${BACK_URL}/tags/qna`)
        .then(res => {
          this.tags = res.data.list[0].list
        })
        .catch(err => console.log(err))
    },
    searchTags(tag) {
      this.$router.push({ name : 'TagSearchQuestions', params : { keyword : tag }})
    }
  },
  created() {
    this.getRanking(),
    this.getTags()
  },
  mounted() {
  },
  computed: {
    sortRanking () {
      return _.orderBy(this.unsortedRank, 'score', 'desc')
    }
  }
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

.tag-list-wrap {
  height: 100px;
  position: sticky;
  top: 100px;
}

#ranker {
  cursor: pointer;
}
</style>