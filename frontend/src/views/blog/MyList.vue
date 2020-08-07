<template>
    <div id="mylist" class="row">
        <!-- 사이드 바 -->
        <div class="nav col-2 flex-column text-left">
            <h5>카테고리</h5>
            <hr class="ml-0" style="width:70%;">
            <button id="category-all" @click="getAllPosts" type="button" class="btn mb-3 p-0 text-left">전체보기()</button>
            <div id="category-menu" v-for="category in categoryList" :key="category.boardId">
                <button @click="getSomePosts(category.name)" type="button" class="btn mb-3 p-0">{{ category.name }}({{  }})</button>
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
                <button class="btn btn-outline-success my-2 my-sm-0" @click="searchFor">검색</button>
            </div>

            <!-- 카테고리 버튼 -->
            <div v-if="userNow === nickname">
                <button type="button" @click="newCategory" class="btn btn-outline-dark mt-3" style="width:100px;">카테고리 관리</button>   
            </div>
        </div>

        <!-- 글 리스트 -->
        <div v-if="this.categoryOn === 1" class="col-10 container">
            <div class="text-left ml-5 mt-5" v-if="postList.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div class="row">
                <div v-for="(item, index) in postList" :key="item.postId" class="p-0 mb-5 col-12 col-lg-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="cardImage" alt="Card image cap">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject }}</h5>
                            <p class="card-text mb-3">{{ item.content }}</p>
                            <!-- 좋아요 부분 -->
                            <span v-if="postLike1[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(item, false)"><i class="fas fa-heart"></i></span>
                            <span v-if="!postLike1[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(item, true)"><i class="fas fa-heart"></i></span>
                            <small :ref="'like-count-' + item.postId">{{ item.likes }}</small><small>개의 좋아요</small>
                        </div>
                        <div class="card-footer bg-transparent">
                            <button class="btn btn-sm" @click="gotoDetail(item)">글 보기</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="text-right" v-if="userNow === nickname">
                <button type="button" @click="newArticle('default')" class="btn btn-outline-dark mb-5 mr-5" style="width:100px;">새 글 작성</button>
            </div>
        </div>

        <!-- 글 리스트 카테고리 있는 경우 -->
        <div v-if="this.categoryOn === 2" class="col-10 container">
            <div class="row text-left ml-5 mt-5" v-if="postListCategory.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
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
            <div class="text-right" v-if="userNow === nickname">
                <button type="button" @click="newArticle(postListCategory[0].board_name)" class="btn btn-outline-dark mb-5 mr-5" style="width:100px;">새 글 작성</button>
            </div>
        </div>

        <!-- 글 리스트 키워드 있는 경우 -->
        <div v-if="this.categoryOn === 3" class="col-10 container">
            <div class="text-left ml-5 mt-5" v-if="postListKeyword.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div class="row">
                <div v-for="item in postListKeyword" :key="item.postId" class="p-0 mb-5 col-12 col-lg-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="cardImage" alt="Card image cap">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject }}</h5>
                            <p class="card-text mb-3">{{ item.content }}</p>
                            
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
        newArticle(boardName) {
            this.$router.push(`/blog-editor/${this.nickname}/${boardName}/write`)
        },
        // 카테고리 관리
        newCategory() {
            this.$router.push(`/blog-category/${this.nickname}`)
        },

        getAllPosts() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            this.categoryOn = 1
            axios.get(`${BACK_URL}/blog/${this.nickname}/post_list`, config)
                .then(res => {
                    // 포스트 정보
                    this.postList = res.data.list[0].list
                    // 포스트에 사용자가 좋아요를 눌렀는지에 대한 불린 값
                    this.postLike1 = res.data.list[1].list
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
            this.categoryOn = 2
            axios.get(`${BACK_URL}/blog/${this.nickname}/${categoryName}/post_list`)
                .then(res => {
                    this.postListCategory = res.data.list[0].list
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
            this.categoryOn = 3
            axios.get(`${BACK_URL}/blog/${this.nickname}/search/blogPosts/${this.keyword}/${this.keywordType.keyid}`)
                .then(res => {
                    this.postListKeyword = res.data.list
                    
                })

                .catch(err => {
                    console.log(err)
                })
        },
        // 포스트 디테일
        gotoDetail(post) {
            this.$router.push({ name : "DetailPost" , params: { boardName: post.board_name, nickname : post.writer, post_id : post.postId }})
        },    

        // 좋아요
        postLike(post, likeit) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                    'Content-Type': 'application/json'
                }
            }
            // 좋아요 현 상태로 구분
            if (likeit === false) {
                axios.post(`${BACK_URL}/blog/${post.writer}/like/${post.postId}`, likeit, config)
                    .then(res => {
                        // 좋아요 수 바꾸기(화면에서)
                        this.$refs[`like-count-${post.postId}`][0].innerText = res.data.data    
                        this.getAllPosts()                 
                    })
                    .catch(err => {
                        console.log(err)
                    })   
            } else {
                axios.post(`${BACK_URL}/blog/${post.writer}/like/${post.postId}`, likeit, config)
                    .then(res => {
                        // 좋아요 수 바꾸기(화면에서)
                        this.$refs[`like-count-${post.postId}`][0].innerText = res.data.data   
                        this.getAllPosts()                      
                    })
                    .catch(err => {
                        console.log(err)
                    })   
            }        
        }
    },
    
    created() {
        this.getAllPosts(),
        this.getCategory()
        
    },
    data() {
        return{
            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,
            // 글 관련
            categoryOn: 1,
            postList: [],
            postListCategory: [],
            postListKeyword: [],
            categoryList: [],
            
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
            // 좋아요 관련
            postLike1: [],
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
