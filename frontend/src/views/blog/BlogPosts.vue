<template>
  <div id="blog-posts">
      <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css">
        <!-- 글 리스트 -->
        <div v-if="this.categoryOn === 1" class="col-12 p-0 m-0 container">
            <div class="text-left ml-5 mt-5" v-if="postList.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div class="row">
                <div class="new-article col-12" v-if="userNow === nickname">
                    <a type="button" @click="newArticle('default')" class="mb-5 float-right" style="width:100px;">새 글 작성</a>
                </div>
                <div v-for="(item, index) in postList" :key="index" class="p-0 mb-5 col-12">
                    <div class="card-list">
                        <div class="card-header text-left bg-transparent p-5 d-flex justify-content-between">
                            <h4 class="card-title m-0"><strong>{{ item.subject }}</strong></h4>
                            <span class="vertical-line mx-3"></span>
                            <div>
                                <p>{{ item.createdAt.slice(0,10) }}</p> 
                                
                                <p>{{ item.member_nickname }}</p>
                            </div>
                        </div>

                        <div class="card-list-body p-5">
                            <div class="card-image">
                                <img :src="thumbnail1[index]" alt="Card image cap">
                            </div>
                            <p class="card-list-text my-5">{{ item.content.slice(0, 200) }}</p>

                            <div class="d-flex justify-content-between">
                                <a class="py-3 px-5" @click="gotoDetail(item)">글 더보기</a>
                                <div class="py-3 px-5">
                                    <!-- 좋아요 부분 -->
                                    <i class="far fa-eye"></i>{{ item.views }}
                                    <span class="vertical-line mx-3"></span>
                                    <i class="far fa-comment"></i>{{ item.replyCnt }}
                                    <span class="vertical-line mx-3"></span>
                                    <b-icon icon="heart-fill" v-if="postLike1[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(item, false, 1)"></b-icon>
                                    <b-icon icon="heart" v-if="!postLike1[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(item, true, 1)"></b-icon>
                                    <small :ref="'like-count-' + item.postId">{{ item.likes }}</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 글 리스트 카테고리 있는 경우 -->
        <div v-if="this.categoryOn === 2" class="col-10 container">
            <div class="row text-left ml-5 mt-5" v-if="postListCategory.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div class="row">
                <div v-for="(item, index) in postListCategory" :key="index" class="p-0 mb-5 col-12 col-lg-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="thumbnail2[index]" alt="Card image cap" style="height: 150px;">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject.slice(0, 10) + '...'  }}</h5>
                            <p class="card-text mb-3">{{ item.content.slice(0, 20) + '...' }}</p>
                            <!-- 좋아요 부분 -->
                            <b-icon icon="heart-fill" v-if="postLike2[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(item, false, 2)"></b-icon>
                            <b-icon icon="heart" v-if="!postLike2[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(item, true, 2)"></b-icon>
                            <small :ref="'like-count-' + item.postId">{{ item.likes }}</small><small>개의 좋아요</small>
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

        <!-- 글 리스트 키워드 있는 경우 -->
        <div v-if="this.categoryOn === 3" class="col-10 container">
            <div class="text-left ml-5 mt-5" v-if="postListKeyword.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div class="row">
                <div v-for="(item, index) in postListKeyword" :key="index" class="p-0 mb-5 col-12 col-lg-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="thumbnail3[index]" alt="Card image cap" style="height: 150px;">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject.slice(0, 10) + '...'  }}</h5>
                            <p class="card-text mb-3">{{ item.content.slice(0, 20) + '...' }}</p>
                            <!-- 좋아요 부분 -->
                            <b-icon icon="heart-fill" v-if="postLike3[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(item, false, 3)"></b-icon>
                            <b-icon icon="heart" v-if="!postLike3[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(item, true, 3)"></b-icon>
                            <small :ref="'like-count-' + item.postId">{{ item.likes }}</small><small>개의 좋아요</small>
                        </div>
                        <div class="card-footer bg-transparent">
                            <button class="btn btn-sm" @click="gotoDetail(item)">글 보기</button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 무한스크롤 -->
            <infinite-loading @infinite="infiniteHandler"></infinite-loading>
        </div>

        <!-- 태그로 검색한 경우 -->
        <div v-if="this.categoryOn === 4" class="col-10 container">
            <div class="row text-left ml-5 mt-5" v-if="postListTag.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div class="row">
                <div v-for="(item, index) in postListTag" :key="index" class="p-0 mb-5 col-12 col-lg-3">
                    <div class="card" style="width: 75%;">
                        <img class="card-img-top" :src="thumbnail4[index]" alt="Card image cap" style="height: 150px;">
                        <div class="card-body pb-0">
                            <h5 class="card-title">{{ item.subject.slice(0, 10) + '...'  }}</h5>
                            <p class="card-text mb-3">{{ item.content.slice(0, 20) + '...' }}</p>
                            <!-- 좋아요 부분 -->
                            <b-icon icon="heart-fill" v-if="postLike4[index]" class="d-inline mr-1" style="cursor:pointer; color: crimson;" @click="postLike(item, false, 4)"></b-icon>
                            <b-icon icon="heart" v-if="!postLike4[index]" class="d-inline mr-1" style="cursor:pointer; color: black;" @click="postLike(item, true, 4)"></b-icon>
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

        
  </div>
</template>

<script>
import axios from 'axios'
import { EventBus } from '../../event-bus.js'

const BACK_URL = 'http://localhost:8080'

export default {
    name: 'BlogPosts',
    components: {

    }, 
    props: {

    },

    methods: {
        // 새 글
        newArticle(boardName) {
            this.$router.push(`/blog-editor/${this.nickname}/${boardName}/write`)
        },

        getAllPosts() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            this.categoryOn = 1
            axios.get(`${BACK_URL}/blog/${this.nickname}/post_list?no=${this.numOfPage}`, config)
                .then(res => {
                    // 썸네일
                    this.thumbnail1 = res.data.list[2].list
                    // 포스트 정보
                    this.postList = res.data.list[0].list
                    // 포스트에 사용자가 좋아요를 눌렀는지에 대한 불린 값
                    this.postLike1 = res.data.list[1].list
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
       
        // 카테고리에 맞는 포스트만 가져오기
        getSomePosts(categoryName) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            this.categoryOn = 2
            axios.get(`${BACK_URL}/blog/${this.nickname}/${categoryName}/post_list?no=${this.numOfPage}`, config)
                .then(res => {
                    this.postListCategory = res.data.list[0].list.reverse()
                    this.postLike2 = res.data.list[1].list.reverse()
                    this.thumbnail2 = res.data.list[2].list.reverse()
                    // 카테고리 바로 에디터로 가져가기 위한 용도
                    this.currentCategory = categoryName
                })
                .catch(err => {
                    console.log(err)
                })
        },

        // 검색
        searchFor(keyword, type) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            this.categoryOn = 3
            axios.get(`${BACK_URL}/blog/${this.nickname}/search/blogPosts/${keyword}/${type}?no=1`, config)
                .then(res => {
                    this.postListKeyword = res.data.list[0].list.reverse()
                    this.postLike3 = res.data.list[1].list.reverse()
                    this.thumbnail3 = res.data.list[2].list.reverse()
                })

                .catch(err => {
                    console.log(err)
                })
        },

        //무한스크롤
        infiniteHandler ($state) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            this.categoryOn = 3
            axios.get(`${BACK_URL}/blog/${this.nickname}/search/blogPosts/${this.k}/${this.type}?no=${this.page}`, config)
                .then(res => {
                if (res.data.list[0].list.length) {
                    this.page += 1
                    this.postListKeyword.push(...res.data.list[0].list.reverse())
                    this.postLike3.push(...res.data.list[1].list.reverse())
                    this.thumbnail3.push(...res.data.list[2].list.reverse())
                    $state.loaded()
                } else {
                    $state.complete()
                }
                })
                .catch (err => console.log(err))
        },

        // 태그 검색
        getTagPosts(tag) {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }
            this.categoryOn = 4
            axios.post(`${BACK_URL}/blog/${this.nickname}/search/tags/${tag}?no=${this.numOfPage}`, config)
                .then(res => {
                    this.postListTag = res.data.list[0].list.reverse()
                    this.postLike4 = res.data.list[1].list.reverse()
                    this.thumbnail4 = res.data.list[2].list.reverse()
                })

                .catch(err => {
                    console.log(err)
                })
        },

        // 포스트 디테일
        gotoDetail(post) {
            this.$router.push({ name : "DetailPost" , params: { boardName: post.board_name, nickname : post.member_nickname, post_id : post.postId }})
        },    

        // 좋아요
        postLike(post, likeit, num) {
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
                        if (num === 1) {
                            this.getAllPosts()  
                        } else if (num === 2) {
                            this.getSomePosts(this.c) 
                        } else if (num === 3) {
                            this.searchFor(this.k, this.type)
                        }
                                    
                    })
                    .catch(err => {
                        console.log(err)
                    })   
            } else {
                axios.post(`${BACK_URL}/blog/${post.member_nickname}/like/${post.postId}`, likeit, config)
                    .then(res => {
                        // 좋아요 수 바꾸기(화면에서)
                        this.$refs[`like-count-${post.postId}`][0].innerText = res.data.data   
                        if (num === 1) {
                            this.getAllPosts()  
                        } else if (num === 2) {
                            this.getSomePosts(this.currentCategory) 
                        } else if (num === 3)  {
                            this.searchFor(this.k, this.type)
                        }                   
                    })
                    .catch(err => {
                        console.log(err)
                    })   
            }        
        },
    },
    
    mounted() {
        this.c = this.$route.query.c
        this.k = this.$route.query.k
        this.type = this.$route.query.type
        this.t = this.$route.query.t
        // 카테고리가 있는 경우
        if (this.c) {
            this.getSomePosts(this.c)
        // 검색 키워드 있으면
        } else if (this.k) {
            this.searchFor(this.k, this.type)
        } else if (this.t) {
            this.getTagPosts(this.t)
        } else {
            this.getAllPosts()
        }

        EventBus.$on("paging", currentPage => {
            this.numOfPage = currentPage
        })
    },
    watch: {
      // 카테고리가 있는 경우
      '$route.query.c'() {
        this.c = this.$route.query.c
        if (this.c) {
          this.getSomePosts(this.c)
        } else {
          this.getAllPosts() 
        }     
      },
      // 검색 키워드 변경되면
      '$route.query.k'() {
        this.k = this.$route.query.k
        this.type = this.$route.query.type
        this.t = this.$route.query.t
        if (this.k) {
          this.searchFor(this.k, this.type)
        } else if (this.t) {
            this.getTagPosts(this.t)
        } else {
          this.getAllPosts() 
        }               
      },
      // 검색 키워드 변경되면
      '$route.query.t'() {
        this.t = this.$route.query.t
        this.k = this.$route.query.k
        this.type = this.$route.query.type
        if (this.t) {
          this.getTagPosts(this.t)
        } else if (this.k) {
          this.searchFor(this.k, this.type)
        } else {
          this.getAllPosts() 
        }               
      },

      // 페이지네이션
      'numOfPage' () {
        this.c = this.$route.query.c
        this.k = this.$route.query.k
        this.type = this.$route.query.type
        this.t = this.$route.query.t
        // 카테고리가 있는 경우
        if (this.c) {
            this.getSomePosts(this.c)
        // 검색 키워드 있으면
        } else if (this.k) {
            this.searchFor(this.k, this.type)
        } else if (this.t) {
            this.getTagPosts(this.t)
        } else {
            this.getAllPosts()
        }
      }
    },

    data() {
        return{
            // 쿼리로 모드 조절 
            c : this.$route.query.c,
            k : this.$route.query.k,
            type: this.$route.query.type,
            t : this.$route.query.t,

            userNow: this.$cookies.get('nickname'), 
            nickname: this.$route.params.nickname,

            // 글 관련
            categoryOn: '',
            postList: [],
            postListCategory: [],
            postListKeyword: [],
            postListTag: [],
            currentCategory: '',
            thumbnail1: [],
            thumbnail2: [],
            thumbnail3: [],
            thumbnail4: [],

            // 좋아요 관련
            postLike1: [],
            postLike2: [],
            postLike3: [],
            postLike4: [],

            // 페이지네이션
            numOfPage: 1,
            page : 1,
        }
    },

}
</script>

<style>
#blog-posts {
    min-height: 1000px;
}

.card-list {
    background-color: #ffffff;
    border: 1px solid #e7e7e7;
}

.vertical-line {
    background-color: #e7e7e7;
    border: 1px solid #e7e7e7 ;
}
.card-title {
    color: #313131
}

.card-list-text {
    color: #6b6b6b
}

.card-list a {
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
    border: 1px solid #e7e7e7;
}
.card-list a:hover {
    color: #56dbc9 !important;
    border: 1px solid #99c9c2 !important;
}

.new-article a {
    padding: 10px 30px;
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
    border: 1px solid #929292;
}

.new-article a:hover {
    padding: 10px 30px;
    color: #56dbc9 !important;
    border: 1px solid #99c9c2 !important;
}


.card-image{
    overflow: hidden;
}

.card-image img{
    height: 300px;
    width: 90%;
}

.card-title {
    width: 70%;
}
</style>