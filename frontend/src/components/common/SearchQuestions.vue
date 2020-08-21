<template>
  <div>
    <h1 id="search-heading"> {{ this.keyword }} 검색결과</h1>
    <!-- 글 리스트 -->
    <div class="col-10 container">
        <div class="text-left ml-5 mt-5" v-if="postList.length == 0">
            <h3>해당 내용의 글이 없습니다</h3>
        </div>
        <section class="cards row">
            <div class="col-lg-12 row">
                <div v-for="(post, index) in postList" :key="post.postId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
                <div class="cardwrap">
                    <div v-if="post.selectOver" id="solved">solved!</div>
                    <div class="card-body p-0" @click="gotoDetail(post)">
                    <div class="img-section" :style="{ 'background-image' : `url(${thumbnails[index]})`}">
                        <a href=""></a>
                    </div>
                    <div class="contents">
                        <h4>{{ post.subject }}</h4>
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
        </section>    
    </div>
    <infinite-loading @infinite="infiniteHandler"></infinite-loading>
  </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://i3c206.p.ssafy.io:8080/api'

export default {
    data() {
        return {
            keyword: this.$route.query.q,
            type: this.$route.query.type,
            postList: [],
            thumbnails: [],
            page : 1
        }
    },
    methods: {
        infiniteHandler($state) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            axios.get(`${BACK_URL}/questions/search/all_questions/${this.keyword}/${this.type}?no=${this.page}`, config)
                .then(res => {
                    if (res.data.list[0].list.length) {
                        this.page += 1
                        this.postList.push(...res.data.list[0].list)
                        this.thumbnails.push(...res.data.list[1].list)
                        $state.loaded()
                    } else {
                        $state.complete()
                    }
                })
                .catch (err => console.log(err))
        },
        // 포스트 디테일
        gotoDetail(post) {
            this.$router.push({ name : "QuestionDetail" , params: { nickname : post.member_nickname, qpostId : post.qpostId }})
        },   
        gotoUserInfo(userNickname) {
            this.$router.push({ name : "Profile" , query: { nickname : userNickname }})
        },  
    },
    mounted() {
    },
    watch: {
        '$route.query.q'() {
            this.keyword = this.$route.query.q
            this.type = this.$route.query.type
            location.reload()
        },

        '$route.query.type'() {
            this.keyword = this.$route.query.q
            this.type = this.$route.query.type
            location.reload()
        }
    }
}
</script>

<style>

</style>