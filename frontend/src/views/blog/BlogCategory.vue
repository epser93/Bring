<template>
  <div id="blogCategory" class="row">
    <div class="wrapper text-left col-12 col-lg-8 mt-5 px-5">
      <h2 class="mt-5">카테고리 관리</h2>

      <!-- 카테고리 목록 -->
      <div class="p-5">
        <h4><i class="fas fa-stream mr-3"></i>카테고리 목록</h4>
        <hr class="mb-4">
        <div class="px-5">
            <span>전체보기 <span class="float-right">({{ numOfPosts }})</span><hr></span> 
            <div class="m-0 p-0" v-for="category in categoryList" :key="category.name">
                <span>{{ category.name }}<span class="float-right">({{ category.postCnt }})</span><hr></span> 
            </div>
        </div>
      </div>

      <!-- 카테고리 생성 -->
      <div class="p-5">
        <h4><i class="fas fa-plus-circle mr-3"></i>카테고리 생성</h4>
        <div class="d-flex">
          <input type="text" v-model="newCategoryName.name" placeholder="새 카테고리 이름" class="w-100">
          <a type="submit" class="" @click="postCategory">
            생성
          </a>
        </div>
      </div>

      <!-- 카테고리 수정 -->
      <div class="p-5">
        <h4><i class="fa fa-dot-circle mr-3"></i>카테고리 수정</h4>
        <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-secondary">{{ categorySelected }}</button>
        <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
          <button type="button" @click="dropdown(category.name)" tabindex="0" class="dropdown-item" v-for="category in categoryList" v-bind:key="category.name">
            {{ category.name }}
          </button>
        </div>
        <div class="d-flex">
          <input type="text" id="input1-group3" v-model="editCategoryName.name" placeholder="새 카테고리 이름" class="w-100">
          <a type="submit" class="" @click="putCategory">
            수정
          </a>
        </div>
      </div>

      <!-- 카테고리 삭제 -->
      <div class="p-5">
        <h4><i class="fas fa-minus-circle mr-3"></i>카테고리 삭제</h4>

          <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-secondary">{{ categorySelected2 }}</button>
          <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
            <button type="button" @click="dropdown2(category.name)" tabindex="0" class="dropdown-item" v-for="category in categoryList" v-bind:key="category.name">
              {{ category.name }}
            </button>
          </div>
          <a type="submit" class="" @click="modalChange">
            삭제
          </a>

      </div>
      
    </div>

    <!-- 모달 -->
    <div v-if="modal">
      <DeleteCategoryWarning :categorySelected2="categorySelected2" @deleted="changeDropdown"/>
    </div>
  </div>
</template>

<script>
import DeleteCategoryWarning from '@/components/blog/DeleteCategoryWarning.vue'
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

export default {
  name: 'BlogCategory',
  components: {
      DeleteCategoryWarning,
  },
  data() {
    return {
      newCategoryName: {
        name: ""
      },
      nickname: this.$route.params.nickname,
      categoryList: [],
      editCategoryName: {
        name: ""
      },
      categorySelected: '카테고리 선택',
      categorySelected2: '카테고리 선택',
      numOfPosts: 0,

      // 모달
      modal: false,
    }
  },  

  methods: {
    dropdown(newBoardName) {
      this.categorySelected = newBoardName
    }, 

    dropdown2(deleteBoardName) {
      this.categorySelected2 = deleteBoardName
    },


    getCategory() {
      axios.get(`${BACK_URL}/blog/${this.nickname}/categories`)
          .then(res => {
              this.categoryList = res.data.list[0].list
              this.calPostsSum()
          })

          .catch(err => {
              console.log(err)
          })
    },

    postCategory() {
      const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        axios.post(
          `${BACK_URL}/blog/${this.nickname}/create`, this.newCategoryName, config)
          .then(() =>{
            // 카테고리 생성하면 바로 수정에서 반영 되게
            this.getCategory()
            this.newCategoryName.name = ""
          })
          .catch((err) => {
            alert('오류 발생.')
            console.error(err)
          })
    },

    putCategory() {
      const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        axios.put(
          `${BACK_URL}/blog/${this.nickname}/${this.categorySelected}`, this.editCategoryName, config)
          .then(() =>{
            this.getCategory()
            this.categorySelected = this.editCategoryName.name
            this.editCategoryName.name = ""
            this.categorySelected = "카테고리 선택"
          })
          .catch((err) => {
            alert('중복된 카테고리 이름이거나, 없는 카테고리를 변경 요청 하셨습니다.')
            console.error(err)
          })
    },


    calPostsSum() {
      for (const item in this.categoryList) {
          this.numOfPosts = this.numOfPosts + this.categoryList[item].postCnt
          this.totalNum = this.numOfPosts
      }
    },
    // 모달 띄우는 기준
    modalChange() {
      this.modal = !this.modal
    },
    // 모달로 삭제 끝나면 드롭다운 내부 값 바꿔줘야
    changeDropdown(deletedOrNot) {
      this.modal = false
      if (deletedOrNot) {
        this.categorySelected2 = "카테고리 선택"
        this.getCategory()
      }
    }
  },
  created() {
    this.getCategory()
  },
}
</script>

<style scoped>
#blogCategory {
    min-height: 1000px;
    background-color: #f4f4f4;
}

.wrapper {
    margin: 0 auto;
    background-color: white;
    border: 1px solid #e7e7e7;
    margin-bottom: 200px;
}

h4 {
  margin-bottom: 20px;;
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

input {
    height: auto; /* 높이값 초기화 */ 
    line-height : normal; /* line-height 초기화 */ 
    padding: .7em .5em; /* 원하는 여백 설정, 상하단 여백으로 높이를 조절 */ 
    font-family: inherit; /* 폰트 상속 */ 
    border: 1px solid #999; 
    border-radius: 0; /* iSO 둥근모서리 제거 */ 
    outline-style: none; /* 포커스시 발생하는 효과 제거를 원한다면 */ 
    -webkit-appearance: none; /* 브라우저별 기본 스타일링 제거 */ 
    -moz-appearance: none; appearance: none;
}

input:hover {
  border: 1px solid #56dbc9;
  text-decoration: none;
  transition-duration: 0.3s;
}

button {
    height: auto; /* 높이값 초기화 */ 
    line-height : normal; /* line-height 초기화 */ 
    padding: .7em .5em; /* 원하는 여백 설정, 상하단 여백으로 높이를 조절 */ 
    font-family: inherit; /* 폰트 상속 */ 
    border-radius: 0; /* iSO 둥근모서리 제거 */ 
}
</style>
