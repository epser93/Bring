<template>
  <div>
    <!-- 글 리스트 -->
    <div class="col-12">
        <div class="text-left ml-5 mt-5" v-if="postList.length == 0">
            <h3>팔로워들의 활동이 없습니다</h3>
        </div>
        <div class="row">
            <div v-for="(item, index) in postList" :key="item.postId" class="p-0 mb-5 col-12 col-lg-3">
                <div class="card" style="width: 75%;">
                    <img class="card-img-top" :src="thumbnail1[index]" alt="Card image cap" style="height: 150px;">
                    <div class="card-body pb-0">
                        <h5 class="card-title">{{ item.subject.slice(0, 10) + '...'  }}</h5>
                        <p class="card-text mb-3">{{ item.content.slice(0, 20) + '...' }}</p>
                        <!-- 좋아요 부분 -->
                        <b-icon icon="heart-fill" v-if="postLike1[index]" class="d-inline mr-1" style="color: crimson;" @click="postLike(item, false)"></b-icon>
                        <b-icon icon="heart" v-if="!postLike1[index]" class="d-inline mr-1" style="color: black;" @click="postLike(item, true)"></b-icon>
                        <small>{{ item.likes }}</small><small>개의 좋아요</small>
                    </div>
                    <div class="card-footer bg-transparent">
                        <button class="btn btn-sm" @click="gotoDetail(item, index)">글 보기</button>
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

<style>

</style>
