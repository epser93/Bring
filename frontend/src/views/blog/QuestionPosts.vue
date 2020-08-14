<template>
  <div>
    <!-- 글 리스트 -->
    <div v-if="this.categoryOn === 1" class="col-10 container">
        <div class="text-left ml-5 mt-5" v-if="postList.length == 0">
            <h3>현재 등록된 글이 없습니다</h3>
        </div>
        <div class="row">
            <div v-for="(item, index) in postList" :key="item.postId" class="p-0 mb-5 col-12 col-lg-3">
                <div class="card" style="width: 75%;">
                    <img class="card-img-top" :src="thumbnail1[index]" alt="Card image cap">
                    <div class="card-body pb-0">
                        <h5 class="card-title">{{ item.subject.slice(0, 10) + '...'  }}</h5>
                        <p class="card-text mb-3">{{ item.content.slice(0, 20) + '...' }}</p>
                    </div>
                    <div class="card-footer bg-transparent">
                        <button class="btn btn-sm" @click="gotoDetail(item)">글 보기</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="text-right" v-if="userNow === nickname">
            <button type="button" @click="newArticle" class="btn btn-outline-dark mb-5 mr-5" style="width:100px;">새 글 작성</button>
        </div>
    </div>

    <!-- 글 리스트 카테고리 있는 경우 -->
    <div v-if="this.categoryOn === 2" class="col-10 container">
        <div class="row text-left ml-5 mt-5" v-if="postListTag.length == 0">
            <h3>현재 등록된 글이 없습니다</h3>
        </div>
        <div class="row">
            <div v-for="(item, index) in postListTag" :key="item.postId" class="p-0 mb-5 col-12 col-lg-3">
                <div class="card" style="width: 75%;">
                    <img class="card-img-top" :src="thumbnail2[index]" alt="Card image cap">
                    <div class="card-body pb-0">
                        <h5 class="card-title">{{ item.subject }}</h5>
                        <p class="card-text mb-3">{{ item.content }}</p>
                    </div>
                    <div class="card-footer bg-transparent">
                        <button class="btn btn-sm" @click="gotoDetail(item)">글 보기</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="text-right" v-if="userNow === nickname">
            <button type="button" @click="newArticle" class="btn btn-outline-dark mb-5 mr-5" style="width:100px;">새 글 작성</button>
        </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

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
            axios.get(`${BACK_URL}/questions/${this.nickname}/qlist`, config)
                .then(res => {
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

            axios.post(`${BACK_URL}/questions/${this.nickname}/search/tags/${tag}`)
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
    
    created() {
        
    },
    mounted() {
        this.q = this.$route.query.q
        if (this.q) {
          this.getSomePosts(this.q)
        } else {
          this.getAllPosts() 
        }
    },
    watch: {
      '$route.query.q'() {
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
        }
    },
}
</script>

<style>

</style>