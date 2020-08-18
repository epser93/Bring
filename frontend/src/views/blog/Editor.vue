<template>
  <div id="editor">
    <div class="d-flex justify-content-between ml-3 mb-5">
      <h2 class="">글 작성</h2>
      <button type="button" @click="postText" class="btn btn-outline-success" style="width:80px;">발행</button>
    </div>

    <!-- 모달 -->
    <div v-if="modal">
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
    <v-md-editor class="text-left" v-model="aboutText.post.content" :disabled-menus="[]" @upload-image="handleUploadImage" height="600px"></v-md-editor>
    <br>

    <!-- 태그 -->
    <div class="tag">
      <span v-for="(tag,index) in aboutText.post.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2" @click="deleteTag(index)">{{ tag }}</span>
    </div>
    <input placeholder="태그를 입력해주세요" class="mb-5 tag-input" type="text" v-model="tag" @keydown.enter="postTag">
    
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
    // 이미지 업로드
    handleUploadImage(event, insertImage, files) {
      if (this.aboutText.boardName === "default" || this.aboutText.boardName === null) {
        alert('카테고리 먼저 정해주세요')
      } else {
        console.log(this.aboutText.boardName)
        console.log(files[0])
        this.uploadImageDirect(files[0])
        insertImage({
          // url : 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1269952892,3525182336&fm=26&gp=0.jpg',
          url : this.imageServerUrl,
          desc : '사진설명'
        })
      }
    },
    // 바로 이미지를 서버에 업로드 업로드 된 장소의 url 받아오기
    uploadImageDirect(file) {
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
        })
        .catch(err => console.log(err))
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
.tag-input {
  width: 100%
}

.badge {
  cursor: pointer;
}
</style>



