<template>
  <div id="blogCategory">
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
    }
  },  
  props: {
    nickname: String,
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

          .then((res) =>{
            console.log(res)
            alert('카테고리 생성 성공')
          })
          .catch((err) => {
            alert('실패')
            console.error(err)
          })
    },
  }
}
</script>

<style>

</style>