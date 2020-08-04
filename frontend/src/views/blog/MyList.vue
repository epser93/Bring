<template>
    <div id="mylist" class="row">
        <!-- 사이드 바 -->
        <div class="nav col-2 flex-column text-left">
            <h5>카테고리</h5>
            <hr class="ml-0" style="width:70%;">
            <div id="category-menu" v-for="category in categoryList" :key="category.board_id">
                <button @click="getSomePosts(category.name)" type="button" class="btn btn-link mb-3 p-0">{{ category.name }}</button>
            </div>
                
            <!-- 검색창 -->
            <form class="form-inline mt-5">
                <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-success">{{ keywordType.name }}</button>
                <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                    <button type="button" tabindex="0" @click="dropdown(typeid, value)" class="dropdown-item" v-for="(value, typeid) in dropdownList" v-bind:key="typeid">
                        {{ value }}
                    </button>
                </div>
                <input class="form-control mr-sm-2 w-75" type="search" v-model="keyword" placeholder="키워드 입력" aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0" @click="searchFor" type="submit">검색</button>
            </form>

            <!-- 이동버튼(나중에 합치거나 프로필에서 관리?) -->
            <button type="button" @click="newArticle" class="btn btn-outline-dark mt-5" style="width:100px;">새 글 작성</button>
            <button type="button" @click="newCategory" class="btn btn-outline-dark mt-3" style="width:100px;">카테고리 관리</button>   
        </div>

        <!-- 글 리스트 -->
        <div v-if="!this.categoryOn" class="col-10 container">
            <div class="text-left mt-5" v-if="postList.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div class="row">
                <div v-for="item in postList" :key="item.post_id" class="p-0 mb-5 col-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="cardImage" alt="Card image cap">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject }}</h5>
                            <p class="card-text mb-3">{{ item.content }}</p>
                            <p class="text-right">♥ {{ item.likes}}</p>
                        </div>
                        <div class="card-footer bg-transparent">
                            <button class="btn btn-sm" @click="gotoDetail(item)">글 보기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 글 리스트 카테고리 있는 경우 -->
        <div v-if="this.categoryOn" class="col-10 container">
            <div class="row">
                <div v-for="item in postListCategory" :key="item.post_id" class="p-0 mb-5 col-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="cardImage" alt="Card image cap">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject }}</h5>
                            <p class="card-text mb-3">{{ item.content }}</p>
                            <p class="text-right">♥ {{ item.likes}}</p>
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
    name: 'MyList',
    components: {
   
    }, 
    props: {
        cardUserImage: {
            type: String,
            default: require("@/assets/img/faces/marc.jpg")
        },
        cardImage: {
            type: String,
            default: require("@/assets/img/card.jpg")
        },
    },

    methods: {
        newArticle() {
            this.$router.push(`/blog-editor/${this.nickname}`)
        },
        newCategory() {
            this.$router.push(`/blog-category/${this.nickname}`)
        },

        getAllPosts() {
            axios.get(`${BACK_URL}/blog/${this.nickname}/post_list`)
                .then(res => {
                    this.postList = res.data.list
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
       
        getCategory() {
            axios.get(`${BACK_URL}/blog/${this.nickname}/categories`)
                .then(res => {
                    this.categoryList = res.data.list
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
        
        getSomePosts(categoryName) {
            this.categoryOn = true
            axios.get(`${BACK_URL}/blog/${this.nickname}/${categoryName}/post_list`)
                .then(res => {
                    this.postListCategory = res.data.list
                })

                .catch(err => {
                    console.log(err)
                })
        },
        dropdown(typeid, value) {
            this.keywordType.keyid = typeid
            this.keywordType.name = value
        },
        searchFor() {
            console.log(this.keyword)
            console.log(this.keywordType.keyid)
            this.categoryOn = true
            axios.get(`${BACK_URL}/blog/${this.nickname}/search/blogPosts/${this.keyword}/${this.keywordType.keyid}`)
                .then(res => {
                    console.log(res)
                    this.keywordList = res.data.list
                })

                .catch(err => {
                    console.log(err)
                })
        },
        gotoDetail(post) {
            this.$router.push({ name : "DetailPost" , params: { post: post, nickname : post.writer, post_id : post.post_id }})
        },    
    },
    
    created() {
        this.getAllPosts(),
        this.getCategory()
        
    },
    data() {
        return{
            nickname: this.$route.params.nickname,
            // 글 관련
            postList: [],
            postListCategory: [],
            categoryList: [],
            categoryOn: false,
            // 검색 관련
            keywordType: {
                name: '통합검색',
                keyid: 1,
            },
            keyword: '',
            dropdownList: {
                1: '통합검색',
                2: '제목검색',
                3: '내용검색',
                4: '태그검색'
            },
            keywordList: [],
        }
    }
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
.card {
    box-shadow: 10px 0px 60px -40px black;
}

</style>
