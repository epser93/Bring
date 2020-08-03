<template>
  <div id="blogCategory">
    <h2>카테고리</h2>

    <h3 class="text-left">카테고리 수정</h3>
    <!-- 기존 카테고리 -->
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

    <!-- 카테고리 수정 -->
    <form class="form-inline mb-5">
        <label>카테고리 수정:</label>
        <input class="form-control mr-sm-2 ml-5" v-model="editCategoryName" placeholder="새 카테고리 이름" aria-label="editCategory">
        <button class="btn btn-outline-secondary my-2 my-sm-0" @click="putCategory" type="submit">수정</button>
    </form>

    <!-- 카테고리 생성 -->
    <form class="form-inline mb-5">
        <label>카테고리 생성:</label>
        <input class="form-control mr-sm-2 ml-5" v-model="newCategoryName" placeholder="새 카테고리 이름" aria-label="newCategory">
        <button class="btn btn-outline-secondary my-2 my-sm-0" @click="postCategory" type="submit">생성</button>
    </form>
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
      newCategoryName: '',
      nickname: this.$route.params.nickname,
      categoryList: [],
      boardName:'',
      editCategoryName: '',
    }
  },  

  methods: {
    postCategory() {
      const config = {
          headers: {
            'X-AUTH-TOKEN' : this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        axios.post(
          `${BACK_URL}/blog/${this.nickname}/create`, this.newCategoryName, config)

          .then(() =>{
          })
          .catch((err) => {
            alert('실패')
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
          `${BACK_URL}/blog/${this.nickname}/${this.boardName}`, {
            params: {
              name: this.editCategoryName,
            }}, config)

          .then(() =>{
          })
          .catch((err) => {
            alert('실패')
            console.error(err)
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
  },
  created() {
    this.getCategory()
  }
}
</script>

<style>

</style>