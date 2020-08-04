<template>
    <div id="myanswers" class="row">
        <!-- 사이드 바 -->
        <div class="nav col-2 flex-column text-left">
            <h5>카테고리</h5>
            <hr class="ml-0" style="width:70%;">
            <a class="nav-link" href="#">Link</a>
            <a class="nav-link" href="#">Link</a>
            <a class="nav-link" href="#">Link</a>
            <a class="nav-link" href="#">Link</a>

            <!-- 검색창 -->
            <form class="form-inline mt-5">
                <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
            </form>
            <br>
        </div>

        <!-- 글 리스트 -->
        <div class="list offset-1 col-8">
            <div class="card border-secondary mb-3" style="width: 75%;">
                <div class="card-header bg-transparent border-secondary">Header</div>
                <div class="card-body text-secondary">
                    <h5 class="card-title">Success card title</h5>
                    <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
                </div>
                <div class="card-footer bg-transparent border-secondary">Footer</div>
            </div>
        </div>
    </div>
</template>

<script>
export default {

}
</script>

<style>

</style>