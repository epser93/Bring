<template>
    <div id="myanswers" class="row">
        <!-- 사이드 바 -->
        <div class="nav col-2 flex-column text-left">
            <h5>태그</h5>
            <hr class="ml-0" style="width:70%;">
            <a class="nav-link" href="#">Link</a>
            <a class="nav-link" href="#">Link</a>
            <a class="nav-link" href="#">Link</a>
            <a class="nav-link" href="#">Link</a>

            <!-- 검색창 -->
            <div class="mt-5">
                <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-success"></button>
                <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                    <button type="button" tabindex="0" class="dropdown-item">
                    </button>
                </div>
                <input class="form-control mr-sm-2 w-75" type="search" placeholder="키워드 입력" aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0">검색</button>
            </div>
        </div>
        
        <!-- 글 리스트 -->
        <!-- 수정필요 -->
        <div class="col-10">
            <div class="text-left ml-5 mt-5" v-if="answersList.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div v-for="(item, index) in answersList" :key="item.answerId" class="p-0 mb-5">
                <div class="card border-secondary mb-3" style="width: 75%;">
                    <div class="card-header bg-transparent border-secondary">tag?</div>
                    <div class="card-body text-secondary">
                        <p class="card-text">{{ item.content }}</p>
                    </div>
                    <div class="card-footer bg-transparent border-secondary">
                        <!-- 좋아요 부분 -->
                        <span v-if="answersLike1[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(item, false)"><i class="fas fa-heart"></i></span>
                        <span v-if="!answersLike1[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(item, true)"><i class="fas fa-heart"></i></span>
                        <small :ref="'like-count-' + item.postId">{{ item.likes }}</small><small>개의 좋아요</small>
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
    name: '',
    data() {
        return {
            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,

            categoryOn: 1,
            answersList: [], 
            answersLike1: [],
        }
    },
    methods: {
        getAllAnswers() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            this.categoryOn = 1
            axios.get(`${BACK_URL}/blog/${this.nickname}/나의 Answers/post_list`, config)
                .then(res => {
                    this.answersList = res.data.list[0].list
                    this.answersLike1 = res.data.list[1].list
                    console.log(this.answersList)
                    console.log(this.answersLike1)
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
    },
    created() {
        this.getAllAnswers()
    }

}
</script>

<style>

</style>