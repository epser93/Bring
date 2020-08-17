<template>
  <div>
    <!-- 페이지네이션 -->
    <div class="offset-4">
        <b-pagination v-model="currentPage" :total-rows="rows" :per-page="perPage" class="mt-4">
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
    data() {
        return {
            rows: 10,
            perPage: 8,
            currentPage: 1
        }
    }
}
</script>

<style>

</style>