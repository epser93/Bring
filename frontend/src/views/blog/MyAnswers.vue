<template>
    <div id="myanswers" class="row mt-5">
        <!-- 글 리스트 -->  
        <div class="col-12">
            <div v-for="(item, index) in answersList.slice(0,6)" :key="item.answerId" class="p-0 mb-5">
                <div class="card-wrapper mb-3">
                    <div class="p-3"><h4 class="mb-2">{{ answersQList[index] }}</h4><small>{{ item.createdAt.slice(0,10)}}</small></div>
                    <hr class="m-0">
                    <p class="p-3 text-left">{{ item.content }}</p>

                    <div class="p-3 mb-3">
                        <!-- 좋아요 부분 -->
                        <small class="float-right">{{ item.likes }}</small>
                        <b-icon icon="heart-fill" v-if="answersLike1[index]" class="d-inline mr-1 float-right" style="color: crimson;"></b-icon>
                        <b-icon icon="heart" v-if="!answersLike1[index]" class="d-inline mr-1 float-right" style="color: black;"></b-icon>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://i3c206.p.ssafy.io'

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
                    this.answersList = res.data.list[0].list
                    this.answersLike1 = res.data.list[1].list
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
#myanswers {
    min-height: 1000px;
}

.card-wrapper{
    background-color: white;
    border: 1px solid #e7e7e7
}
</style>