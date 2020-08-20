<template>
  <div id="myfeeds">
    <!-- 글 리스트 -->
        <div class="col-12 p-0 container">
            <div class="row">
                <div class="text-center mt-5" v-if="postList.length == 0">
                    <h3>팔로워의 최근 글이 없습니다</h3>
                </div>
                <div v-for="(item, index) in postList" :key="index" class="p-0 mb-5 col-12 mt-5">
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
                            <div class="card-image mb-5">
                                <img :src="thumbnail1[index]" alt="Card image cap">
                            </div>
                            

                            <div class="d-flex justify-content-between">
                                <a class="py-3 px-5" @click="gotoDetail(item, index)">글 더보기</a>
                                <div class="py-3 px-5">
                                    <!-- 좋아요 부분 -->
                                    <i class="far fa-eye"></i>{{ item.views }}
                                    <span class="vertical-line mx-3"></span>
                                    <i class="far fa-comment"></i>{{ item.replyCnt }}
                                    <span class="vertical-line mx-3"></span>
                                    <b-icon icon="heart-fill" v-if="postLike1[index]" class="d-inline mr-1" style="color: crimson;"></b-icon>
                                    <b-icon icon="heart" v-if="!postLike1[index]" class="d-inline mr-1" style="color: black;"></b-icon>
                                    <span :ref="'like-count-' + item.postId">{{ item.likes }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
  </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://i3c206.p.ssafy.io/api'

export default {
    name: 'MyFeeds',
    components: {
    }, 
    props: {
    },

    data() {
      return {
        postList: [],
        postLike1: [],
        thumbnail1: [],
        boardName: [],
      }
    },
    created() {
      this.getPosts()
    },

    methods: {
        getPosts() {
            const config = {
                headers: {
                    'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
                }
            }

            axios.get(`${BACK_URL}/follow/feed`, config)
                .then(res => {
                    // 썸네일
                    this.thumbnail1 = res.data.list[3].list.reverse()
                    // 포스트 정보
                    this.postList = res.data.list[0].list.reverse()
                    // 포스트에 사용자가 좋아요를 눌렀는지에 대한 불린 값
                    this.postLike1 = res.data.list[2].list.reverse()
                    // 보드네임
                    this.boardName = res.data.list[1].list.reverse()
                })
 
                .catch(err => {
                    console.log(err)
                })
        },
 
        // 포스트 디테일
        gotoDetail(post, index) {
            this.$router.push({ name : "DetailPost" , params: { boardName: this.boardName[index], nickname : post.member_nickname, post_id : post.postId }})
        },    

    },
}
</script>

<style scoped>
#myfeeds {
    min-height: 1000px;
}
.card-image img{
    height: 300px;
}
</style>
