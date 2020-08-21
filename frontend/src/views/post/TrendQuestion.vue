!<template>
  <div class="wrapB container-fluid">
    <section class="cards row">
      <div class="col-lg-10 row">
        <div v-for="(question, index) in list" :key="question.qpostId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
          <div class="cardwrap">
            <div v-if="question.selectOver" id="solved">solved!</div>
            <div class="card-body p-0" @click="gotoQuestionDetail(question)">
              <div class="img-section" :style="{ 'background-image' : `url(${thumbnails[index]})`}">
                <a href=""></a>
              </div>
              <div class="contents">
                <h4>{{ question.subject }}</h4>
                <p class="comment-date">{{ question.createdAt.substring(0,10) }} · {{ question.answerCnt }}개의 답변</p>
              </div>
            </div>
            <div class="writer-info">
              <button class="btn btn-sm" @click="gotoUserInfo(question.member_nickname)">{{ question.member_nickname }}</button>
              <p><i class="far fa-eye"></i> {{ question.views }}</p>
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
const BACK_URL = 'http://i3c206.p.ssafy.io:8080/api'
export default {
  name: 'HotPost',
  props: {

  },
  data() {
    return {
      list: [],
      unsortedRank: [],
      thumbnails: [],
      page : 1,
      tags : [],
    }
  },
  created() {
    this.getRanking(),
    this.getTags()
  },
  methods: {
    infiniteHandler ($state) {
      axios.get(`${BACK_URL}/questions/trend?no=${this.page}`)
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
  computed: {
    sortRanking () {
      return _.orderBy(this.unsortedRank, 'score', 'desc')
    }
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