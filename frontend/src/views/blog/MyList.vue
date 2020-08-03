<template>
    <div id="mylist" class="row">
        <!-- 사이드 바 -->
        <div class="nav col-2 flex-column text-left mt-5">
            <h5>카테고리</h5>
            <hr class="ml-0" style="width:70%;">
            <div id="category-menu" v-for="category in categoryList" :key="category.board_id">
                <button @click="getSomePosts(category.name)" type="button" class="btn btn-link mb-3 p-0">{{ category.name }}</button>
            </div>
                
            <!-- 검색창 -->
            <form class="form-inline mt-5">
                <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
            </form>
            <br>
            <button type="button" @click="newArticle" class="btn btn-outline-dark" style="width:100px;">새 글 작성</button>
            <button type="button" @click="newCategory" class="btn btn-outline-dark mt-3" style="width:100px;">카테고리 관리</button>
            
        </div>

        <!-- 글 리스트 -->
        <div v-if="!this.categoryOn" class="offset-1 col-8">
            <div class="text-left mt-5" v-if="postList.length == 0">
                <h3>현재 등록된 글이 없습니다</h3>
            </div>
            <div v-for="item in postList" :key="item.post_id" class="mb-5">
                <div class="card" style="width: 75%;">
                    <img class="card-img-top" :src="cardImage" alt="Card image cap">
                    <div class="card-body">
                        <h5 class="card-title">{{ item.subject }}</h5>
                        <p class="card-text">{{ item.content }}</p>
                        <a href="#" class="btn btn-outline-success">글 보기</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- 글 리스트 카테고리 있는 경우 -->
        <div v-if="this.categoryOn" class="offset-1 col-8">
            <div v-for="item in postListCategory" :key="item.post_id" class="mb-5">
                <div class="card" style="width: 75%;">
                    <img class="card-img-top" :src="cardImage" alt="Card image cap">
                    <div class="card-body">
                        <h5 class="card-title">{{ item.subject }}</h5>
                        <p class="card-text">{{ item.content }}</p>
                        <a href="#" class="btn btn-outline-success">글 보기</a>
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
        }    
    },
    
    created() {
        this.getAllPosts(),
        this.getCategory()
        
    },
    data() {
        return{
            nickname: this.$route.params.nickname,
            postList: [],
            postListCategory: [],
            categoryList: [],
            categoryOn: false,
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

</style>
