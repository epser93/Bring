<template>
    <div id="myanswers" class="row mt-5">
        <!-- 글 리스트 -->  
        <div class="col-12">
            <h5>{{ nickname }}의 답변 목록</h5>
            <hr class="ml-0">
            <div v-for="(item, index) in answersList" :key="item.answerId" class="p-0 mb-5">
                <div class="card border-secondary mb-3" style="width: 70%;">
                    <div class="card-header bg-transparent border-secondary"><strong>{{ answersQList[index] }}</strong><br><small>작성자: {{ answersWList[index] }}</small></div>
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
            answersQList: [],
            answersWList: [],
        }
    },
    methods: {
        getAllAnswers() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }

            axios.get(`${BACK_URL}/blog/${this.nickname}/나의 Answers/post_list`, config)
                .then(res => {
                    this.answersList = res.data.list[0].list.reverse()
                    this.answersLike1 = res.data.list[1].list.reverse()
                    this.answersList.forEach((item, index) => {
                        const newS = this.answersList[index].subject.split('(Q writer: ') 
                        const newSt = newS[1].split(', Q number: ')
                        this.answersQList.push(newS[0])
                        this.answersWList.push(newSt[0])
                    })
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
    },
    mounted() {
        this.getAllAnswers()   
    },
}
</script>

<style>

</style>