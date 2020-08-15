<template>
    <div id="myquestions" class="row">
        <!-- 컴포넌트 불러오기 -->
        <div class="container-fluid col-9">
            <router-view></router-view>
        </div>

        <!-- 사이드 바 -->
        <div id="nav" class="col-12 col-lg-3 flex-column text-left p-0">
            <h5>태그</h5>
            <hr class="ml-0" style="width:70%;">
            <router-link :to="{ name: 'MyQuestions' , params: { nickname: this.nickname }}" class="badge badge-pill badge-light mr-2 p-2 ">전체보기</router-link> 
            
            <div v-for="(tag, index) in tagList" :key="tag.boardId">
                <router-link :to="{ name: 'MyQuestions' , query : { q: tag }}" class="badge badge-pill badge-light mr-2 p-2">{{ tag }}({{ tagNum[index] }})</router-link> 
            </div>

            <!-- 지식인 답변 목록 -->
            <MyAnswers />
        </div>


    </div>
</template>

<script>
import axios from 'axios'
import MyAnswers from '../blog/MyAnswers.vue'

const BACK_URL = 'http://localhost:8080'

export default {
    name: 'MyList',
    components: {
        MyAnswers, 
    }, 
    props: {
    },

    methods: {       
        getTags() {
            axios.get(`${BACK_URL}/tags/qna/${this.nickname}`)
                .then(res => {
                    this.tagList = res.data.list[0].list
                    this.tagNum = res.data.list[1].list
                })
                .catch(err => {
                    console.log(err)
                })
        },
    },

    mounted() {
        this.getTags()
    },
    
    data() {
        return{
            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,
            
            // 태그 관련
            tagList: [],
            tagNum: [],       
        }
    },
}
</script>

<style>
#category-menu button{
    text-decoration: none;
    color: black;
}

#category-menu button:focus{
    font-weight: bold;
    color: #42b983;
}

#category-all button{
    text-decoration: none;
    color: black;
}

#category-all button:focus{
    font-weight: bold;
    color: #42b983;
}

.card {
    box-shadow: 10px 0px 60px -40px black;
}
/* 트렌지션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>