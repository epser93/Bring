<template>
  <div id="editor" class="row">
    <div class="wrapper text-left my-5 p-5">
      <h2 class="mb-5">글 수정</h2>
      
      <!-- 카테고리 부분 -->
      <div class="mb-5">
        <h5 class="d-inline mr-4"><i class="fas fa-folder mr-3"></i>카테고리 선택</h5>
        <div class="d-inline">
          <select v-model="boardName">
            <option v-for="category in categoryList" v-bind:key="category.name">
              {{ category.name }}
            </option>
          </select>
        </div>
      </div>

      <!-- 제목 부분 -->
      <form class="row text-left">
        <div class="col-12 form-group">
          <h5>제목</h5> 
          <input type="text" class="form-control" v-model="postData.subject" id="titleInput" placeholder="제목을 입력하세요">
        </div>
      </form>

      <!-- 글 에디터 부분 -->
      <v-md-editor class="text-left" v-model="postData.content" :disabled-menus="[]" @upload-image="handleUploadImage" height="600px"></v-md-editor>
      <br>

      <!-- 태그 -->
      <div class="tag">
        <span v-for="(tag,index) in postData.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2" @click="deleteTag(index)">{{ tag }}<span id="closeTag">  x</span></span>
      </div>
      <input placeholder="태그를 입력후 enter키를 눌러주세요" class="mt-3 mb-5 tag-input" type="text" v-model="tag" @keydown.enter="postTag">
      
      <!-- 제출 버튼 -->
      <a @click="updatePost" class="float-right">수정</a>
    </div>  
  </div>

</template>

<script>
import axios from 'axios'



const BACK_URL = 'http://i3c206.p.ssafy.io/api'

export default {
  name: 'UpdateForm',
  components: {
      
  },
  data() {
    return {
      // 받아오는 정보 param
      nickname : this.$route.params.nickname,
      boardName: this.$route.params.boardName,
      post_id : this.$route.params.post_id,
      
      postData: {
        subject: '',
        content: '',
        tags: [],
      },

      member_nickname: null,
      createdAt: null,
      categoryList: [],
      tag:"",

      imageServerUrl: ''
     
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
      axios.post(`${BACK_URL}/blog/${this.nickname}/${this.boardName}/${this.post_id}/uploads`, formData, config)
        .then(res => {
          this.imageServerUrl = res.data.list[0]
          insertImage({
            url : this.imageServerUrl,
            desc : '사진설명'
          })
        })
        .catch(err => console.log(err))
    },
    // 이미지 업로드
    handleUploadImage(event, insertImage, files) {
      if (this.boardName === "default" || this.boardName === null) {
        alert('카테고리 먼저 정해주세요')
      } else {
        this.uploadImageDirect(files[0], insertImage)
      }
    },
    updatePost () {
      const config = {
        headers: {
          'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
        }
      }
      axios.put(`${BACK_URL}/blog/${this.nickname}/${this.boardName}/${this.post_id}`, this.postData, config)
        .then(() => {
          this.$router.go(-1)
        })
        .catch(err => console.log(err))
    },
    getCategory() {
          axios.get(`${BACK_URL}/blog/${this.nickname}/categories`)
              .then(res => {
                  this.categoryList = res.data.list[0].list
              })
              .catch(err => {
                  console.log(err)
              })
      },
    getPostInfo() {
      axios.get(`${BACK_URL}/blog/${this.nickname}/${this.boardName}/${this.post_id}`)
        .then(res => {
          this.member_nickname = res.data.list[0].list[0].member_nickname
          this.postData.subject = res.data.list[0].list[0].subject
          this.postData.content = res.data.list[0].list[0].content
          this.createdAt = res.data.list[0].list[0].createdAt
          this.postData.tags = res.data.list[1].list
        })
        .catch(err => console.log(err))
    },

    postTag() {
      const chkpatterns = /[~!@#$%^&*()_+|<>?:{}]/;
      if (this.tag === null || this.tag.replace(/^\s*|\s*$/g, '').length === 0) {
        alert('빈칸은 태그로 입력 불가능합니다.')
        this.tag = ""
      } else if (chkpatterns.test(this.tag)) {
        alert('특수문자는 입력할 수 없습니다.')
        this.tag = ""
      } else if (!this.postData.tags.includes(this.tag)) {
        this.postData.tags.push(this.tag)
        this.tag = ""
      } else {
        alert('중복된 태그입니다.')
        this.tag= ""
      }
    },
    deleteTag(index) {
      this.postData.tags.splice(index,1)
    },
  },
  created() {
    this.getPostInfo()
    this.getCategory()
  }
};
</script>
<style scoped>
#closeTag {
  opacity: 0.5;
}
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
.badge {
  cursor: pointer;
}
</style>




