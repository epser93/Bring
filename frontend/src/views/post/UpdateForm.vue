<template>
  <div id="editor">
    <div class="d-flex justify-content-between ml-3 mb-5">
      <h2 class="">글 수정</h2>
      <button type="button" @click="updatePost" class="btn btn-outline-success" style="width:80px;">수정</button>
    </div>
    <!-- 카테고리 생성 부분 -->
    
    
    <!-- 카테고리 부분 -->
    <div class="row form-group">
      <div class="col-2 text-left"><label for="multiple-select" class=" form-control-label">카테고리 선택: </label></div>
      <div class="col-10 text-left">
        <select v-model="boardName">
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
        <input type="text" class="form-control" v-model="postData.subject" id="titleInput" placeholder="제목을 입력하세요">
      </div>
    </form>

    <!-- 글 에디터 부분 -->
    <v-md-editor class="text-left" v-model="postData.content" height="600px"></v-md-editor>
    <br>

    <!-- 태그 -->
    <div class="tag">
      <span v-for="(tag,index) in postData.tags" :key="index" class="badge badge-pill badge-light mr-2 p-2" @click="deleteTag(index)">{{ tag }}</span>
    </div>
    <input placeholder="태그를 입력해주세요" class="mb-5 tag-input" type="text" v-model="tag" @keydown.enter="postTag">
    
  </div>
</template>

<script>
import axios from 'axios'



const BACK_URL = 'http://localhost:8080'

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

      thumbnail: '',
      tag:""
     
    }
  },
  methods: {
    updatePost () {
      const config = {
        headers: {
          'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
        }
      }
      axios.put(`${BACK_URL}/blog/${this.nickname}/${this.boardName}/${this.post_id}`, this.postData, config)
        .then(() => {
          this.thumbnailPost()
          console.log('포스트데이터',this.postData)
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
      if (!this.postData.tags.includes(this.tag)) {
        this.postData.tags.push(this.tag)
        this.tag = ""
      } else {
        alert('중복된 태그입니다.')
        this.tag= ""
      }
      console.log('태그추가', this.postData.tags)
    },
    deleteTag(index) {
      this.postData.tags.splice(index,1)
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
          `${BACK_URL}/blog/${this.nickname}/${this.boardName}/${this.post_id}/uploads`, formData, config)

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
    this.getPostInfo()
    this.getCategory()
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




