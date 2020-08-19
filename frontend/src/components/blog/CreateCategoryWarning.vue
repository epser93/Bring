<template>
  <div>
    <b-modal class="modal" ref="my-modal" size="sm" hide-header hide-footer title="">
      <div class="d-block text-center mt-3 mb-5">
          <h4>먼저 카테고리를 만들어 보세요!</h4>
      </div>
      <b-button class="mt-2" variant="outline-success" block @click="gotoCategory">카테고리 만들기</b-button>
      <b-button class="mt-3" variant="outline-warning" block @click="hideModal">취소</b-button>
    </b-modal>
  </div>
</template>

<script>
  export default {
    methods: {
      showModal() {
        this.$refs['my-modal'].show()
      },
      hideModal() {
        this.$refs['my-modal'].hide()
      },
      gotoCategory() {
        this.$router.push(`/blog-category/${this.nickname}`)
      }
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
<style>


</style>