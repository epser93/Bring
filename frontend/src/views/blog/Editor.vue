<template>
  <div id="editor">
    <div class="d-flex justify-content-between ml-3 mb-5">
      <h2 class="">글 작성</h2>
      <button type="button" @click="postText" class="btn btn-outline-success" style="width:80px;">발행</button>
    </div>

    <!-- 모달 -->
    <div v-if="categoryList.length === 0">
      <CreateCategoryWarning />
    </div>

    <!-- 카테고리 부분 -->
    <div class="row form-group">
      <div class="col-2 text-left"><label for="multiple-select" class=" form-control-label">카테고리: </label></div>
      <div class="col-10 text-left">
        <select v-model="aboutText.boardName">
          <option v-for="category in categoryList" v-bind:key="category.name">
            {{ category.name }}
          </option>
        </select>

      </div>
    </div>

    <!-- 썸네일  -->
    <div class="row ml-1 mt-5 mb-5">
      <label for="thumbnailInput">썸네일 : </label>
      <input @change="thumbnailSelect" type="file" ref="thumbnailImage" id="thumbnailInput">
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
import CreateCategoryWarning from '@/components/blog/CreateCategoryWarning.vue'
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

export default {
  name: 'Editor',
  components: {
     CreateCategoryWarning,
  },
  data() {
    return {
      aboutText: {
        boardName: '',
        post: {
                content: "",
                subject: "",
                tags: ['']
              },
        nickname: this.$route.params.nickname,
        
      },
      categoryList: [],
      thumbnail: '',
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
            // 글 작성 완료 후, 썸네일 포스트 요청 
            this.thumbnailPost()
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

    // 썸네일 관련
    thumbnailSelect() {
      this.thumbnail = this.$refs.thumbnailImage.files[0]
    },
    
    thumbnailPost() {
      const formData = new FormData()
      formData.append('files', this.thumbnail)

      const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
            'Content-Type' : 'multipart/form-data'
          }
        }
        axios.post(
          `${BACK_URL}/blog/${this.aboutText.nickname}/${this.aboutText.boardName}/uploads`, formData, config)

          .then(() =>{
            this.$router.go(-1)
          })
          .catch((err) => {
            alert('카테고리를 선택해 주세요')
            console.error(err)
          })      
    }
  },
  created() {
    this.getCategory(),
    this.aboutText.boardName = this.$route.params.category
  }
};
</script>




