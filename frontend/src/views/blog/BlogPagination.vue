<template>
  <div>
    <!-- 페이지네이션 -->
    <div v-if="totalNum !== -1" class="offset-4">
        <b-pagination v-model="currentPage" :total-rows="totalNum" :per-page="perPage" class="mt-4">
            <template v-slot:first-text><span class="text-success">First</span></template>
            <template v-slot:last-text><span class="text-info">Last</span></template>
            <template v-slot:page="{ page, active }">
                <b v-if="active">{{ page }}</b>
                <i v-else>{{ page }}</i>
            </template>
        </b-pagination>
    </div>
  </div>
</template>

<script>
export default {
    name: '',
    props: {
        totalNum: Number,
    },
    
    data() {
        return {
            perPage: 8,
            currentPage: 1
        }
    },
    methods: {
        sendCurrentPage() {
            this.$emit('pageNum', this.currentPage)
        }
    },
    watch: {
        'currentPage' () {
            this.sendCurrentPage()
        },
        'totalNum' () {
            // 다른곳으로 이동 시 커렌트 페이지 1로 만들어야 함
            this.currentPage = 1
        }
    },
    mounted() {
        
    }
}
</script>

<style>

</style>