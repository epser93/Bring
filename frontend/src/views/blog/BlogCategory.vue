<template>
  <div id="blogCategory">
    <h2>카테고리 관리</h2>

    <!-- 카테고리 생성 -->
    <div class="row mt-5">
      <div class="card offset-2 col-8">
        <div class="card-header bg-white">
          <strong>카테고리 생성</strong>
        </div>
        <div class="card-body card-block">
          <form class="form-horizontal">
            <div class="row form-group">
              <div class="col col-md-12">
                <div class="input-group">
                  <div class="input-group-btn">
                    <div class="btn-group">
                      <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                        <button type="button" @click="dropdown(category.name)" tabindex="0" class="dropdown-item" v-for="category in categoryList" v-bind:key="category.name">
                          {{ category.name }}
                        </button>
                      </div>
                    </div>
                  </div>
                  <input type="text" id="input1-group3" v-model="newCategoryName.name" placeholder="새 카테고리 이름" class="form-control">
                </div>
              </div>
            </div>
          </form>
        </div>
        <div class="card-footer bg-white">
          <button type="submit" class="btn btn-success btn-sm" @click="postCategory">
            <i class="fa fa-dot-circle"></i> 생성
          </button>
        </div>
      </div>
    </div>  


    <!-- 카테고리 수정 -->
    <div class="row mt-5">
      <div class="card offset-2 col-8">
        <div class="card-header bg-white">
          <strong>카테고리 수정</strong>
        </div>
        <div class="card-body card-block">
          <form class="form-horizontal">
            <div class="row form-group">
              <div class="col col-md-12">
                <div class="input-group">
                  <div class="input-group-btn">
                    <div class="btn-group">
                      <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-success">{{ categorySelected }}</button>
                      <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                        <button type="button" @click="dropdown(category.name)" tabindex="0" class="dropdown-item" v-for="category in categoryList" v-bind:key="category.name">
                          {{ category.name }}
                        </button>
                      </div>
                    </div>
                  </div>
                  <input type="text" id="input1-group3" v-model="editCategoryName.name" placeholder="새 카테고리 이름" class="form-control">
                </div>
              </div>
            </div>
          </form>
        </div>
        <div class="card-footer bg-white">
          <button type="submit" class="btn btn-success btn-sm" @click="putCategory">
            <i class="fa fa-dot-circle"></i> 수정
          </button>
        </div>
      </div>
    </div>

    <!-- 카테고리 삭제 -->
    <div class="row mt-5">
      <div class="card offset-2 col-8">
        <div class="card-header bg-white">
          <strong>카테고리 삭제</strong>
        </div>
        <div class="card-body card-block">
          <form class="form-horizontal">
            <div class="row form-group">
              <div class="col col-md-12">
                <div class="input-group">
                  <div class="input-group-btn">
                    <div class="btn-group">
                      <button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dropdown-toggle btn btn-outline-success">{{ categorySelected2 }}</button>
                      <div tabindex="-1" aria-hidden="true" role="menu" class="dropdown-menu">
                        <button type="button" @click="dropdown2(category.name)" tabindex="0" class="dropdown-item" v-for="category in categoryList" v-bind:key="category.name">
                          {{ category.name }}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
        <div class="card-footer bg-white">
          <button type="submit" class="btn btn-success btn-sm" @click="deleteCategory">
            <i class="fa fa-dot-circle"></i> 삭제
          </button>
        </div>
      </div>
    </div>
  
  </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

export default {
  name: 'BlogCategory',
  components: {
      
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
              this.categoryList = res.data.list
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
            alert('카테고리를 생성하였습니다.')
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
            alert('성공')
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

    deleteCategory() {
        const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        axios.delete(
          `${BACK_URL}/blog/${this.nickname}/${this.categorySelected2}`, config)
          .then(() =>{
            alert('카테고리를 삭제하였습니다.')
            this.getCategory()
            this.categorySelected2 = "카테고리 선택"
          })
          .catch((err) => {
            alert('오류 발생.')
            console.error(err)
          })
    }
  },
  created() {
    this.getCategory()
  },
}
</script>

<style>

</style>
