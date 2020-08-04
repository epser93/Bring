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
                <div v-for="item in postList" :key="item.post_id" class="p-0 mb-5 col-12 col-lg-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="cardImage" alt="Card image cap">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject }}</h5>
                            <p class="card-text mb-3">{{ item.content }}</p>
                            <!-- (수정중) 좋아요 부분 -->
                            <small v-if="nickname in item.postMembers" class="d-inline mr-1" style="cursor:pointer" @click="postLike(item.post_id, $event)"><i class="fas fa-heart" style="color:crimson"></i></small>
                            <small v-else class="d-inline mr-1" style="cursor:pointer" @click="postLike(item.post_id, $event)"><i class="fas fa-heart" style="color:black"></i></small>
                            <small ref="like-count-{{ post_id }}">{{ item.likes }} 개의 좋아요</small>
                        </div>
                        <div class="card-footer bg-transparent">
                            <button class="btn btn-sm" @click="gotoDetail(item)">글 보기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 글 리스트 카테고리 있는 경우, 키워드가 있는 경우 -->
        <div v-if="this.categoryOn" class="col-10 container">
            <div class="row">
                <div v-for="item in postListCategory" :key="item.post_id" class="p-0 mb-5 col-12 col-lg-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="cardImage" alt="Card image cap">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject }}</h5>
                            <p class="card-text mb-3">{{ item.content }}</p>
                            <p class="text-right">♥ {{ item.likes }}</p>
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
        // 새 글
        newArticle() {
            this.$router.push(`/blog-editor/${this.nickname}`)
        },
        // 카테고리 관리
        newCategory() {
            this.$router.push(`/blog-category/${this.nickname}`)
        },

        getAllPosts() {
            axios.get(`${BACK_URL}/blog/${this.nickname}/post_list`)
                .then(res => {
                    this.postList = res.data.list
                    console.log(this.postList)
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
        // 카테고리에 맞는 포스트만 가져오기
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
        // 검색 종류 설정
        dropdown(typeid, value) {
            this.keywordType.keyid = typeid
            this.keywordType.name = value
        },
        // 검색
        searchFor() {
            console.log(this.keyword)
            console.log(this.keywordType.keyid)
            this.categoryOn = true
            axios.get(`${BACK_URL}/blog/${this.nickname}/search/blogPosts/${this.keyword}/${this.keywordType.keyid}`)
                .then(res => {
                    console.log(res)
                    this.postListCategory = res.data.list
                })

                .catch(err => {
                    console.log(err)
                })
        },
        // 포스트 디테일
        gotoDetail(post) {
            this.$router.push({ name : "DetailPost" , params: { post: post, nickname : post.writer, post_id : post.post_id }})
        },    
        // 좋아요(아직 동작x)
        postLike(post_id, event) {
            axios.post(`${BACK_URL}/blog/${this.nickname}/like/${post_id}`)
                .then(res => {
                    console.log(res)
                    console.log(this.$refs)
                    
                    // 하트 색 바꾸기
                    if (event.target.style.color === 'crimson') {
                        event.target.style.color = 'black'
                    } else {
                        event.target.style.color = 'crimson'
                    } 
                    
                    // 수정필요
                    // 좋아요 수 바꾸기
                    // refs 지양하라고 함(다른 방법이 떠오르지 않았어요...)
                    this.$refs['like-count-' + `${post_id}`].innerText = res.data;
                    
                })

                .catch(err => {
                    console.log(err)
                })            
        }
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
