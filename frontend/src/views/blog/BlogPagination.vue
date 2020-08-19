<template>
  <div>
    <!-- 페이지네이션 -->
    <div class="pagination mb-5">
        <a @click="pageCount(-1)">&laquo;</a>
        <div class="d-inline">
            <div class="d-inline" v-if="one <= pageTotalc">
                <a @click="pageSelect(one)" v-show="one != pageSelected">{{ one }}</a>
                <a @click="pageSelect(one)" v-show="one === pageSelected" style="background-color: #56dbc9; color: white; border: 1px solid #56dbc9;">{{ one }}</a>
            </div>
            <div class="d-inline" v-if="two <= pageTotalc">
                <a @click="pageSelect(two)" v-show="two != pageSelected">{{ two }}</a>
                <a @click="pageSelect(two)" v-show="two === pageSelected" style="background-color: #56dbc9; color: white; border: 1px solid #56dbc9;">{{ two }}</a>
            </div>
             <div class="d-inline" v-if="three <= pageTotalc">
                <a @click="pageSelect(three)" v-show="three != pageSelected">{{ three }}</a>
                <a @click="pageSelect(three)" v-show="three === pageSelected" style="background-color: #56dbc9; color: white; border: 1px solid #56dbc9;">{{ three }}</a>
            </div>
            <div class="d-inline" v-if="four <= pageTotalc">
                <a @click="pageSelect(four)" v-show="four != pageSelected">{{ four }}</a>
                <a @click="pageSelect(four)" v-show="four === pageSelected" style="background-color: #56dbc9; color: white; border: 1px solid #56dbc9;">{{ four }}</a>
            </div>
            <div class="d-inline" v-if="four <= pageTotalc">
                <a @click="pageSelect(one)" v-show="five != pageSelected">{{ five }}</a>
                <a @click="pageSelect(one)" v-show="five === pageSelected" style="background-color: #56dbc9; color: white; border: 1px solid #56dbc9;">{{ five }}</a>
            </div>
        </div>
        <a @click="pageCount(1)">&raquo;</a>
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
            one : 1,
            two : 2,
            three : 3,
            four : 4,
            five : 5,
            pageSelected: 1,
            pageTotal : 1,
        }
    },
    methods: {
        sendCurrentPage() {
            this.$emit('pageNum', this.pageSelected)
        },

        upScroll () {
            window.scrollTo(0,0)
        },

        pageSelect(page) {
            this.pageSelected = page
        },

        // 페이지네이션 양 옆 이동
        pageCount(num) {
            if (num === 1) {
                if (this.five < this.pageTotal) {
                    this.one ++
                    this.two ++
                    this.three ++
                    this.four ++
                    this.five ++
                }
            } else {
                if (this.one > 1) {
                    this.one --
                    this.two --
                    this.three --
                    this.four --
                    this.five --                    
                }
            }
        }
    },
    watch: {
        'pageSelected' () {
            this.sendCurrentPage()
            this.upScroll()
        },
        '$route.query' () {
            // 다른곳으로 이동 시 커렌트 페이지 1로 만들어야 함
            this.pageSelected = 1
            this.one = 1
            this.two = 2
            this.three = 3
            this.four = 4
            this.five = 5
        }
    },
    created() {
        this.pageTotal = parseInt(this.totalNum / 8) + 1
    },
    computed: {
        pageTotalc(){
            return parseInt(this.totalNum/8) + 1
        }
    }
}
</script>

<style>
.pagination {
  display: inline-block;
}

.pagination a {
    cursor: pointer;
    color: black;
    float: left;
    padding: 8px 16px;
    text-decoration: none;
    transition: background-color .3s;
    border: 1px solid #ddd;
    margin: 0 4px;
}


.pagination a:hover:not(.active) {background-color: #ddd;}
</style>