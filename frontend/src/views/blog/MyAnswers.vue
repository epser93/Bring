<template>
    <div id="myanswers" class="row">
        <!-- 글 리스트 -->
        <div class="offset-1 col-10">
            <div class="ml-5 mt-5" v-if="answersList.length == 0">
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
                        <b-icon icon="heart-fill" v-if="answersLike1[index]" class="d-inline mr-1" style="color: crimson;"></b-icon>
                        <b-icon icon="heart" v-if="!answersLike1[index]" class="d-inline mr-1" style="color: black;"></b-icon>
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