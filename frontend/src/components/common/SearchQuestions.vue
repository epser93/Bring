<template>
  <div>
    <!-- 글 리스트 -->
    <div class="col-10 container">
        <div class="text-left ml-5 mt-5" v-if="postList.length == 0">
            <h3>해당 내용의 글이 없습니다</h3>
        </div>
        <div class="row">
            <div v-for="(item, index) in postList" :key="item.postId" class="p-0 mb-5 col-12 col-lg-3">
                <div class="card" style="width: 75%;">
                    <img class="card-img-top" :src="thumbnail1[index]" alt="Card image cap" style="height: 150px;">
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
    </div>
  </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

export default {
    data() {
        return {
            keyword: this.$route.query.q,
            type: this.$route.query.type,
            postList: [],
            thumbnail1: [],
            userNow: this.$cookies.get('nickname'), 
        }
    },
    methods: {
        // 검색
        searchFor() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            axios.get(`${BACK_URL}/questions/search/all_questions/${this.keyword}/${this.type}`, config)
                .then(res => {
                    this.postList = res.data.list[0].list
                    this.thumbnail1 = res.data.list[1].list
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
        this.searchFor()
    },
    watch: {
        '$route.query.q'() {
            this.keyword = this.$route.query.q
            this.type = this.$route.query.type
            this.searchFor()
        },

        '$route.query.type'() {
            this.keyword = this.$route.query.q
            this.type = this.$route.query.type
            this.searchFor()
        }
    }
}
</script>

<style>

</style>