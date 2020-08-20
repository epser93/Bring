<template>
  <div>
    <!-- 글 리스트 -->
    <div v-if="this.categoryOn === 1" class="col-12 p-0 m-0 container">
        <div class="text-center mt-5" v-if="postList.length == 0">
            <h3>현재 등록된 글이 없습니다</h3>
        </div>
        <div class="row">
            <div class="new-article col-12" v-if="userNow === nickname">
                <a type="button" @click="newArticle" class="mb-5 float-right" style="width:170px;"><i class="fas fa-pencil-alt mr-1"></i>새 질문 작성</a>
            </div>
            <div v-for="(item, index) in postList" :key="index" class="p-0 mb-5 col-12">
                <div class="card-list">
                    <div class="card-header text-left bg-transparent p-5 d-flex justify-content-between">
                        <h4 class="card-title m-0"><strong>{{ item.subject }}</strong></h4>
                        <span class="vertical-line mx-3"></span>
                        <div>
                            <p>{{ item.createdAt.slice(0,10) }}</p> 
                            
                            <p>{{ item.member_nickname }}</p>
                        </div>
                    </div>

                    <div class="card-list-body p-5">
                        <div class="card-image mb-5">
                            <img :src="thumbnail1[index]" alt="Card image cap">
                        </div>


                        <div class="d-flex justify-content-between">
                            <a class="py-3 px-5" @click="gotoDetail(item)">글 더보기</a>
                            <div class="py-3 px-5">
                                <!-- 좋아요 부분 -->
                                <i class="far fa-eye"></i>{{ item.views }}
                                <span class="vertical-line mx-3"></span>
                                <i class="far fa-comment"></i>{{ item.answerCnt }}
                                <span class="vertical-line mx-3"></span>
                                <i class="far fa-check-circle" v-if="!item.selectOver"></i>
                                <i class="fas fa-check-circle" v-if="item.selectOver"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 글 리스트 카테고리 있는 경우 -->
    <div v-if="this.categoryOn === 2" class="col-12 p-0 m-0 container">
        <div class="text-center mt-5" v-if="postListTag.length == 0">
            <h3>현재 등록된 글이 없습니다</h3>
        </div>
        <div class="row">
            <div class="new-article col-12" v-if="userNow === nickname">
                <a type="button" @click="newArticle" class="mb-5 float-right" style="width:170px;"><i class="fas fa-pencil-alt mr-1"></i>새 질문 작성</a>
            </div>
            <div v-for="(item, index) in postListTag" :key="index" class="p-0 mb-5 col-12">
                <div class="card-list">
                    <div class="card-header text-left bg-transparent p-5 d-flex justify-content-between">
                        <h4 class="card-title m-0"><strong>{{ item.subject }}</strong></h4>
                        <span class="vertical-line mx-3"></span>
                        <div>
                            <p>{{ item.createdAt.slice(0,10) }}</p> 
                            
                            <p>{{ item.member_nickname }}</p>
                        </div>
                    </div>

                    <div class="card-list-body p-5">
                        <div class="card-image mb-5">
                            <img :src="thumbnail2[index]" alt="Card image cap">
                        </div>
                        

                        <div class="d-flex justify-content-between">
                            <a class="py-3 px-5" @click="gotoDetail(item)">글 더보기</a>
                            <div class="py-3 px-5">
                                <!-- 좋아요 부분 -->
                                <i class="far fa-eye"></i>{{ item.views }}
                                <span class="vertical-line mx-3"></span>
                                <i class="far fa-comment"></i>{{ item.answerCnt }}
                                <span class="vertical-line mx-3"></span>
                                <i class="far fa-check-circle" v-if="!item.selectOver"></i>
                                <i class="fas fa-check-circle" v-if="item.selectOver"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { EventBus } from '../../event-bus.js'

const BACK_URL = 'http://i3c206.p.ssafy.io/api'

export default {
    name: 'QuestionPosts',
    components: {
   
    }, 
    props: {
    },

    methods: {
        newArticle() {
            this.$router.push({name : 'QuestionWrite'})
        },
        getAllPosts() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }

            this.categoryOn = 1
            axios.get(`${BACK_URL}/questions/${this.nickname}/qlist?no=${this.numOfPage}`, config)
                .then(res => {
                    this.totalNum = res.data.list[2].list
                    // 포스트 정보
                    this.postList = res.data.list[0].list
                    // 썸네일
                    this.thumbnail1 = res.data.list[1].list
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
       
        // 카테고리에 맞는 포스트만 가져오기
        getSomePosts(tag) {
            this.categoryOn = 2

            axios.post(`${BACK_URL}/questions/${this.nickname}/search/tags/${tag}?no=${this.numOfPage}`)
                .then(res => {
                    // 포스트 정보
                    this.postListTag = res.data.list[0].list
                    // 썸네일
                    this.thumbnail2 = res.data.list[1].list
                })
 
                .catch(err => {
                    console.log(err)
                })
        },

        // 포스트 디테일
        gotoDetail(post) {
            this.$router.push({ name : "QuestionDetail" , params: { nickname : post.member_nickname, qpostId : post.qpostId }})
        },    
    },

    mounted() {
        this.q = this.$route.query.q
        if (this.q) {
          this.getSomePosts(this.q)
        } else {
          this.getAllPosts() 
        }

        EventBus.$on("paging2", currentPage => {
            this.numOfPage = currentPage
        })
    },
    watch: {
      '$route.query.q'() {
        this.q = this.$route.query.q
        if (this.q) {
          this.getSomePosts(this.q)
        } else {
          this.getAllPosts() 
        }     
      },

      // 페이지네이션
      'numOfPage' () {
        this.q = this.$route.query.q
        if (this.q) {
          this.getSomePosts(this.q)
        } else {
          this.getAllPosts() 
        }  
      }
    },
    data() {
        return{
            q: this.$route.query.q,
            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,
            // 글 관련
            categoryOn: 1,
            postList: [],
            postListTag: [],

            // 썸네일 관련
            thumbnail1: [],
            thumbnail2: [],

            // 페이지네이션 관련
            numOfPage: 1,
            totalNum : 0,
        }
    },
}
</script>

<style scoped>
.card-image img{
    height: 300px;
}
</style>