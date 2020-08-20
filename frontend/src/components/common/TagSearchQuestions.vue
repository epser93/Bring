<template>
  <div>
    <h3 id="search-heading"># {{ this.keyword }} 검색결과</h3>
    <!-- 글 리스트 -->
    <div class="col-10 container">
        <div class="text-left ml-5 mt-5" v-if="postList.length == 0">
            <h3>해당 내용의 글이 없습니다</h3>
        </div>
        <section class="cards row">
            <div class="col-lg-12 row">
                <div v-for="(post, index) in postList" :key="post.postId" class="card1 col-lg-3 col-md-4 col-sm-6 col-12">
                <div class="cardwrap">
                    <div class="card-body p-0" @click="gotoDetail(post)">
                    <div class="img-section" :style="{ 'background-image' : `url(${thumbnails[index]})`}">
                        <a href=""></a>
                    </div>
                    <div class="contents">
                        <h4>{{ post.subject }}</h4>
                        <!-- <p>{{ post.content }}</p> -->
                        <!-- <v-md-preview :text="post.content"></v-md-preview> -->
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
        </section>    
    </div>
    <infinite-loading @infinite="infiniteHandler"></infinite-loading>
  </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://i3c206.p.ssafy.io:80'

export default {
    data() {
        return {
            keyword: this.$route.params.keyword,
            postList: [],
            thumbnails: [],
            page : 1
        }
    },
    methods: {
        infiniteHandler ($state) {
            axios.post(`${BACK_URL}/questions/search/tags/${this.keyword}?no=${this.page}`)
                .then(res => {
                    if (res.data.list[0].list.length) {
                        this.page += 1
                        this.postList.push(...res.data.list[0].list)
                        this.thumbnails.push(...res.data.list[1].list)
                        console.log(this.postList)
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

}
</script>

<style>

</style>


















