<template>
  <div id="deleteCategoryWarning">
    <b-modal ref="my-modal" size="" hide-header hide-footer title="">
      <div class="d-block text-center mt-3 mb-5">
          <h4>삭제하면 모든 글이 전부 사라집니다</h4>
          <h4>카테고리를 삭제하시겠습니까?</h4>
      </div>
      <b-button class="mt-2" variant="outline-success" block @click="deleteCategory">삭제</b-button>
      <b-button class="mt-3" variant="outline-warning" block @click="hideModal">취소</b-button>
    </b-modal>
  </div>
</template>

<script>
import axios from 'axios'

const BACK_URL = 'http://localhost:8080'

  export default {
    props: {
      categorySelected2: String,
    },
    methods: {
      showModal() {
        this.$refs['my-modal'].show()
      },
      hideModal() {
        this.$refs['my-modal'].hide()
        this.$emit('deleted', false)
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
            this.hideModal()
            this.$emit('deleted', true)
          })
          .catch((err) => {
            alert('해당 카테고리가 존재하지 않습니다.')
            console.error(err)
          })
    },

    },
    mounted() {
        this.showModal()
    },
    data() {
        return {
            nickname : this.$cookies.get('nickname')
        }
    }
  }
</script>
<style scoped>


</style>