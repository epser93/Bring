<template>
    <div id="myquestions" class="row">
        <!-- 사이드 바 -->
        <div class="nav col-2 flex-column text-left">
            <h5>태그</h5>
            <hr class="ml-0" style="width:70%;">
            <button id="category-all" @click="getAllPosts" type="button" class="btn mb-3 p-0 text-left">전체보기</button>
            <div id="category-menu" v-for="(tag, index) in tagList" :key="tag.boardId">
                <button type="button" @click="getSomePosts(tag)" class="btn mb-3 p-0">{{ tag }}({{ tagNum[index] }})</button>
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
                        <img class="card-img-top" :src="thumbnail1[index]" alt="Card image cap">
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
                <button type="button" @click="newArticle(currentCategory)" class="btn btn-outline-dark mb-5 mr-5" style="width:100px;">새 글 작성</button>
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
        getAllPosts() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }

            this.categoryOn = 1
            axios.get(`${BACK_URL}/questions/${this.nickname}/qlist`, config)
                .then(res => {
                    // 포스트 정보
                    this.postList = res.data.list[0].list
                    // 썸네일
                    this.thumbnail1 = res.data.list[1].list
                    console.log(res.data)
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
       
        getTags() {
            this.msrl = this.$cookies.get('msrl')
            axios.get(`${BACK_URL}/tags/list/${this.msrl}`)
                .then(res => {
                    this.tagList = res.data.list[0].list
                    this.tagNum = res.data.list[1].list
                })
                .catch(err => {
                    console.log(err)
                })
        },
        // 카테고리에 맞는 포스트만 가져오기
        getSomePosts(tag) {
            console.log(tag)
        },

        // 포스트 디테일
        gotoDetail(post) {
            this.$router.push({ name : "QuestionDetail" , params: { nickname : post.member_nickname, qpostId : post.qpostId }})
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
                axios.post(`${BACK_URL}/blog/${post.member_nickname}/like/${post.postId}`, likeit, config)
                    .then(res => {
                        // 좋아요 수 바꾸기(화면에서)
                        this.$refs[`like-count-${post.postId}`][0].innerText = res.data.data    
                        this.getAllPosts()                 
                    })
                    .catch(err => {
                        console.log(err)
                    })   
            } else {
                axios.post(`${BACK_URL}/blog/${post.member_nickname}/like/${post.postId}`, likeit, config)
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
        this.getTags()
        
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
            tagList: [],
            tagNum: [],
            msrl: '',
            currentCategory: '',
            
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
            thumbnail1: [],
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

</style>