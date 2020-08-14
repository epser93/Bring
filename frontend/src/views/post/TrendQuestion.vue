!<template>
  <div class="wrapB container-fluid">
    <section class="cards row">
      <div class="col-lg-10 row">
        <div v-for="question in list" :key="question.qpostId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
          <div class="cardwrap" @click="gotoQuestionDetail(question)">
            <div class="img-section">
              <a href=""></a>
            </div>
            <div class="contents">
              <h4>{{ question.subject }}</h4>
              <p>{{ question.content }}</p>
              <p class="comment-date">{{ question.createdAt.substring(0,10) }} · {{ question.answerCnt }}개의 댓글</p>
            </div>
            <div class="writer-info">
              <p>{{ question.member_nickname }}</p>
              <p>♥ {{ question.views }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="tag-list-wrap col-lg-2">
        <h4>명예의전당</h4>
        <ul class="tag-list">
          <li v-for="(ranker, index) in sortRanking.slice(0,10)" :key="index">
              {{ index + 1 }}등 : {{ranker.nickname}}({{ ranker.score}}점)
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
const BACK_URL = 'http://localhost:8080'
export default {
  name: 'HotPost',
  props: {

  },
  data() {
    return {
      list: [],
      unsortedRank: [],
      page : 1
    }
  },
  created() {
    this.getRanking()
  },
  methods: {
    infiniteHandler ($state) {
      console.log($state)
      axios.get(`${BACK_URL}/questions/trend?no=${this.page}`)
        .then (res => {
          if (res.data.list[0].list.length) {
            this.page += 1
            this.list.push(...res.data.list[0].list)
            $state.loaded()
          } else {
            $state.complete()
          }
        })
        .catch(err => console.log(err))
    },
    gotoQuestionDetail(question) {
      this.$router.push({ name : "DetailPost" , params: { post: question, nickname : question.member_nickname, post_id : question.qpost_id }})
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
  height: 450px;
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