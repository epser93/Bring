<template>
    <div id="mylist" class="row">
        <!-- 사이드 바 -->
        <div id="nav" class="col-2 flex-column text-left p-0">
            <h5>카테고리</h5>
            <hr class="ml-0" style="width:70%;">
            <router-link :to="{ name: 'MyBlog' , params: { nickname: this.nickname }}"><p>전체보기 ({{ numOfPosts }})</p></router-link> 
            <div v-for="category in categoryList" :key="category.name">
                <router-link :to="{ name: 'MyBlog' , query : { c: category.name }}"><p>{{ category.name }}({{ category.postCnt }})</p></router-link> 
            </div>

            <!-- 검색창 -->
            <div class="mt-5">
                <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-success">{{ keywordType.name }}</button>
                <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                    <button type="button" tabindex="0" @click="dropdown(typeid, value)" class="dropdown-item" v-for="(value, typeid) in dropdownList" v-bind:key="typeid">
                        {{ value }}
                    </button>
                </div>
                <input class="form-control mr-sm-2 w-75" type="search" v-model="keyword" placeholder="키워드 입력" aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0">
                    <router-link :to="{ name: 'MyBlog' , query : { k: keyword, type: this.keywordType.keyid }}"><p>검색</p></router-link>
                </button>
            </div>

            <!-- 태그 리스트 -->
            <h5 class="mt-5">태그</h5>
            <div class="tag mb-5">
                <div v-for="(tag,index) in this.tagList" :key="index">
                    <router-link :to="{ name: 'MyBlog' , query : { t: tag }}" class="badge badge-pill badge-light mr-2 p-2">{{ tag }}</router-link>
                </div>
            </div>

            <!-- 카테고리 버튼 -->
            <div v-if="userNow === nickname">
                <button type="button" @click="newCategory" class="btn btn-outline-dark mt-3" style="width:100px;">카테고리 관리</button>   
            </div>
        </div>

        <!-- 컴포넌트 불러오기 -->
        <div class="container-fluid col-10">
            <router-view></router-view>
        </div>
    </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

export default {
    name: 'MyList',
    components: {
   
    }, 
    props: {

    },

    methods: {
        // 카테고리 관리
        newCategory() {
            this.$router.push(`/blog-category/${this.nickname}`)
        },

        getCategory() {
            axios.get(`${BACK_URL}/blog/${this.nickname}/categories`)
                .then(res => {
                    this.categoryList = res.data.list[0].list
                    // 전체 개시물 숫자 카운트
                    this.calPostsSum()
                })
                .catch(err => {
                    console.log(err)
                })
        },

        // 검색 종류 설정
        dropdown(typeid, value) {
            this.keywordType.keyid = typeid
            this.keywordType.name = value
        },

        calPostsSum() {
            for (const item in this.categoryList) {
                this.numOfPosts = this.numOfPosts + this.categoryList[item].postCnt
                this.rows = this.numOfPosts
            }
        },

        getTags() {
            axios.get(`${BACK_URL}/tags/blog/${this.nickname}`)
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
        this.getCategory(),
        this.getTags()
    },
    data() {
        return{
            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,
            
            // 카테고리 관련
            categoryOn: '',
            categoryList: [],
            numOfPosts: 0,
            
            // 검색 관련
            keywordType: {
                name: '제목검색',
                keyid: 1,
            },
            keyword: '',
            dropdownList: {
                1: '제목검색',
                2: '내용검색',
                3: '통합검색',
            },

            // 태그 관련
            tagList: [],
            tagNum: [],
        }
    },
}
</script>

<style scoped>
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

</style>
