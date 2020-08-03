<template>
  <div id="editor">
    <div class="d-flex justify-content-between ml-3 mb-5">
      <h2 class="">글 작성</h2>
      <button type="button" @click="postText" class="btn btn-outline-success" style="width:80px;">발행</button>
    </div>
    <!-- 카테고리 생성 부분 -->
    <BlogCategory :nickname="aboutText.nickname"/>
    
    <!-- 카테고리 부분 -->
    <div class="row form-group">
      <div class="col-2 text-left"><label for="multiple-select" class=" form-control-label">카테고리 선택: </label></div>
      <div class="col-10 text-left">
        <select v-model="aboutText.boardName">
          <option v-for="category in categoryList" v-bind:key="category.name">
            {{ category.name }}
          </option>
        </select>
      </div>
    </div>

    <!-- 제목 부분 -->
    <form class="row text-left">
      <div class="col-12 form-group">
        <label for="titleInput">제목 :</label>
        <input type="text" class="form-control" v-model="aboutText.post.subject" id="titleInput" placeholder="제목을 입력하세요">
      </div>
    </form>

    <!-- 글 에디터 부분 -->
    <v-md-editor class="text-left" v-model="aboutText.post.content" height="600px"></v-md-editor>
    <br>
    
  </div>
</template>

<script>
import axios from 'axios'
import BlogCategory from '../../components/blog/BlogCategory.vue'


const BACK_URL = 'http://localhost:8080'

export default {
  name: 'Editor',
  components: {
      BlogCategory,  
  },
  data() {
    return {
      aboutText: {
        boardName: '',
        post: {
                content: "",
                subject: "",
                tag: ""
              },
        nickname: this.$route.params.nickname,
      },
      categoryList: [],
    }
  },
  methods: {
    postText() {
      console.log(this.aboutText)
      const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        axios.post(
          `${BACK_URL}/blog/${this.aboutText.nickname}/${this.aboutText.boardName}`, this.aboutText.post, config)

          .then(() =>{
            this.$router.push({name : 'MyBlog'})
          })
          .catch((err) => {
            alert('카테고리를 선택해 주세요')
            console.error(err)
          })
    },
    getCategory() {
          axios.get(`${BACK_URL}/blog/${this.aboutText.nickname}/categories`)
              .then(res => {
                  this.categoryList = res.data.list
              })

              .catch(err => {
                  console.log(err)
              })
      },
  },
  created() {
    this.getCategory()
  }
};
</script>




