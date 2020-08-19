<template>
  <div id="editor" class="row">
    <div class="wrapper text-left my-5 p-5">
      <!-- 모달 -->
      <div v-if="modal">
        <CreateCategoryWarning />
      </div>

      <h2 class="mb-5">글 작성</h2>
      
      <!-- 카테고리 부분 -->
      <div class="">
        <h5 class="d-inline mr-4"><i class="fas fa-folder mr-3"></i>카테고리 선택</h5>
        <div class="d-inline">
          <select v-model="aboutText.boardName">
            <option v-for="category in categoryList" v-bind:key="category.name">
              {{ category.name }}
            </option>
          </select>
        </div>
      </div>

      <!-- 썸네일  -->
      <div class="mt-4 mb-5">
        <h5 class="d-inline mr-5"><i class="far fa-image mr-3"></i>썸네일 선택</h5>
        <input @change="thumbnailSelect" type="file" ref="thumbnailImage" id="thumbnailInput">
      </div>

      <!-- 제목 부분 -->
      <form class="row text-left">
        <div class="col-12 form-group">
          <h5>제목</h5> 
          <input type="text" class="form-control" v-model="aboutText.post.subject" id="titleInput" placeholder="제목을 입력하세요">
        </div>
      </form>

      <!-- 글 에디터 부분 -->
      <v-md-editor class="text-left" v-model="aboutText.post.content" :disabled-menus="[]" @upload-image="handleUploadImage" height="600px"></v-md-editor>
      <br>

      <!-- 태그 -->
      <div class="tag">
        <span v-for="(tag,index) in aboutText.post.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2" @click="deleteTag(index)">{{ tag }}</span>
      </div>
      <input placeholder="태그를 입력해주세요" class="mb-5 tag-input" type="text" v-model="tag" @keydown.enter="postTag">
      
      <!-- 제출 버튼 -->
      <a @click="postText" class="float-right">발행</a>
    </div>  
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
                tags: []
              },
        nickname: this.$route.params.nickname,
        
      },
      categoryList: [],
      thumbnail: '',
      tag: null,
      modal: false,
      // 이미지 업로드 반환 url
      imageServerUrl : ''
    }
  },
  methods: {
    // 바로 이미지를 서버에 업로드 업로드 된 장소의 url 받아오기
    uploadImageDirect(file, insertImage) {
      const formData = new FormData()
      formData.append('files', file)
      const config = {
        headers: {
          'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN'),
          'Content-Type' : 'multipart/form-data'
        }
      }
      axios.post(`${BACK_URL}/blog/${this.aboutText.nickname}/${this.aboutText.boardName}/uploads`, formData, config)
        .then(res => {
          console.log('업로드',res)
          this.imageServerUrl = res.data.list[0]
          insertImage({
          // url : 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1269952892,3525182336&fm=26&gp=0.jpg',
          url : this.imageServerUrl,
          desc : '사진설명'
        })
        })
        .catch(err => console.log(err))
    },
    // 이미지 업로드
    handleUploadImage(event, insertImage, files) {
      if (this.aboutText.boardName === "default" || this.aboutText.boardName === null) {
        alert('카테고리 먼저 정해주세요')
      } else {
        console.log(this.aboutText.boardName)
        console.log(files)
        console.log(files[0])
        this.uploadImageDirect(files[0], insertImage)
      }
    },
    postText() {
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
            alert('필수 항목을 모두 입력해 주세요')
            console.error(err)
          })
    },
    postTag() {
      if (this.tag === null || this.tag.replace(/^\s*|\s*$/g, '').length === 0) {
        alert('빈칸은 태그로 입력 불가능합니다.')
        this.tag = ""
      } else if (!this.aboutText.post.tags.includes(this.tag)) {
        this.aboutText.post.tags.push(this.tag)
        this.tag = ""
      } else {
        alert('중복된 태그입니다.')
        this.tag= ""
      }
    },
    deleteTag(index) {
      this.aboutText.post.tags.splice(index,1)
    },
    getCategory() {
        axios.get(`${BACK_URL}/blog/${this.aboutText.nickname}/categories`)
            .then(res => {
                this.categoryList = res.data.list[0].list
            })

            .catch(err => {
                console.log(err)
            })
    },
    getModal() {
      if (this.categoryList.length === 0) {
        this.modal = true 
      }
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
    
  },
  watch: {
    'categoryList' () {
        this.getModal()
    }
  }
};
</script>
<style scoped>
@media only screen and (min-width: 1000px) {
  .wrapper {
    width: 80% !important;
    margin: 0 auto;
    background-color: white;
    border: 1px solid #e7e7e7;
    margin-bottom: 200px;
}
}


#editor {
    min-height: 1000px;
    background-color: #f4f4f4;
}

.wrapper {
    width: 100%;
    margin: 0 auto;
    background-color: white;
    border: 1px solid #e7e7e7;
    margin-bottom: 200px;
}

.tag-input {
  width: 100%
}

.badge {
  cursor: pointer;
}

.wrapper a {
    width: 80px;
    text-align: center;
    padding: 10px 20px;
    cursor: pointer;
    text-decoration: none;
    transition-duration: 0.3s;
    border: 1px solid #e7e7e7;
}
.wrapper a:hover {
    color: #56dbc9 !important;
    border: 1px solid #99c9c2 !important;
}

</style>



