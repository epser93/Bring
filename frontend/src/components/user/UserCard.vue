<template>
        <div class="card mt-2">
            <div class="card-body">
                <div class="row">
                    <div class="col-6 quote">
                        <div>
                            <img class="rounded-circle mx-auto d-block" :src=userThumbnail alt="Card image cap" style="width:120px; height:120px; padding:4px; border:1px solid darkgrey">
                        </div>
                        <div class="mb-2">
                            <h5 class="mt-1 mb-1"><b>{{ userInfo.nickname }}</b></h5>
                        </div>
                        <div class="location text-sm-center mb-1"><i class="far fa-envelope"></i>  {{ userInfo.uid }}</div>
                        <span><a href="/" data-toggle="modal" data-target="#staticBackdrop" style="color:gray" @click="seeFollower"><i class="fas fa-user-friends"></i> {{userInfo.followersCnt}} follower · {{userInfo.followingCnt}} following</a></span> 
                        <!-- 여기 위에 수정함 data-toggle & href 처리방법 연구필요 -->
                        <div class="d-flex justify-content-center mb-2" style="align-items: flex-end;">
                            <button class="btn btn-outline-success btn-sm" id="homeBt" @click="gotoBlog"><i class="fas fa-home"></i> 블로그 가기</button>
                            <div v-if="loginNickname == userNickname"> 
                                <button class="btn btn-outline-info btn-sm mx-1 mt-2" @click="gotoEdit">
                                    <i class="fas fa-user-tie"></i> Edit profile
                                </button>
                            </div>
                            <div v-else>
                                <div v-if="userFCheck==false">
                                    <button class="btn btn-outline-success btn-sm mx-1 mt-2" @click="doFollow">
                                        <i class="fas fa-user-tie"></i> 팔로우 하기
                                    </button>
                                </div>
                                <div v-else>
                                    <button class="btn btn-outline-danger btn-sm mx-1 mt-2" @click="unFollow">
                                        <i class="fas fa-user-tie"></i> 팔로우 취소
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal fade" id="staticBackdrop" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-sm">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 v-if="selectFollow == true" class="modal-title" id="staticBackdropLabel">{{ userInfo.nickname }}'s</h5>
                                    <h5 v-else class="modal-title" id="staticBackdropLabel">{{ userInfo.nickname }}'s</h5>
                        
                                    <toggle-button v-model="selectFollow"
                                        :value="true"
                                        :color="{checked: '#82C7EB', unchecked: '#A6D608'}"
                                        :width="110"
                                        :height="30"
                                        :font-size="15"
                                        :labels="{checked: 'Follower', unchecked: 'Following'}"/>
                                        <!-- {{selectFollow}} -->
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <ul class="listGroup container">
                                        <div v-if="selectFollow == true">
                                            <div v-if="followerUserImg.length == 0">
                                                <h5><i class="far fa-frown"></i> 팔로우가 없습니다.</h5>
                                            </div>
                                            <div v-else class="row row-cols-2">
                                                <li v-for="user in followerUserImg" v-bind:key="user.nickname" class="listFollow col">
                                                    <div class="d-flex" id="modalFollow">
                                                        <img class="rounded-circle" :src=user.img alt="Card image cap" id="mFollow1">
                                                        <a :href="$router.resolve({name: 'Profile', query: { nickname: user.nickname }}).href"> {{ user.nickname }}</a>                                                        
                                                        <!-- <router-link :to="{ name: 'Profile', query: { nickname: user.nickname }}" data-dismiss="modal">{{ user.nickname }}</router-link> -->
                                                    </div>
                                                </li>
                                            </div>
                                        </div>

                                        <div v-else>
                                            <div v-if="followingUserImg.length == 0">
                                                <h5><i class="far fa-frown"></i> 팔로잉이 없습니다.</h5>
                                            </div>
                                            <div v-else class="row row-cols-2">
                                                <li v-for="user in followingUserImg" v-bind:key="user.nickname" class="listFollow col">
                                                    <div class="d-flex" id="modalFollow">
                                                        <img class="rounded-circle" :src=user.img alt="Card image cap" id="mFollow2">
                                                        <a :href="$router.resolve({name: 'Profile', query: { nickname: user.nickname }}).href"> {{ user.nickname }}</a>
                                                        <!-- <router-link :to="{ name: 'Profile', query: { nickname: user.nickname }}" data-dismiss="modal">{{ user.nickname }}</router-link> -->
                                                    </div>
                                                </li>
                                            </div>
                                        </div>

                                    </ul>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">닫기</button>
                                </div>
                            </div>
                        </div>
                    </div>




                    <!-- 육성게임  -->
                    <div class="col-5 mx-3" id="gameContent"> 
                        <h3 class="card-title"><b>My Level</b> </h3>
                        <div v-if="computedGrade === 'bronze'" class="mb-4">
                            <img class="grade-img" src="../../assets/img/브론즈.png" alt="">
                            <p>Bronze</p>
                        </div>
                        <div v-else-if="computedGrade === 'silver'" class="mb-4">
                            <img class="grade-img" src="../../assets/img/실버.png" alt="">
                            <p>Silver</p>
                        </div>
                        <div v-else-if="computedGrade === 'gold'" class="mb-4">
                            <img class="grade-img" src="../../assets/img/골드.png" alt="">
                            <p>Gold</p>
                        </div>
                        <div v-else-if="computedGrade === 'platinum'" class="mb-4">
                            <img class="grade-img" src="../../assets/img/플래티넘.png" alt="">
                            <p>Platinum</p>
                        </div>
                        <div v-else-if="computedGrade === 'diamond'" class="mb-4">
                            <img class="grade-img" src="../../assets/img/다이아.png" alt="">
                            <p>Diamond</p>
                        </div>
                        <div v-else class="mb-4">
                            <img class="grade-img" src="../../assets/img/마스터.png" alt="">
                            <p>Master</p>
                        </div>
                        <div style="text-align: justify;">
                            <div class="d-flex">
                                <button type="button" class="btn btn-sm" id="expBtn1"><b>EXP</b></button>
                                <span style="background-color: whitesmoke; width:40%;">
                                    <p style="padding-top:3px; text-align-last: center; font-size:15px">({{userScore}}/{{computedNext}})</p>
                                </span>
                                 <button type="button" class="btn btn-sm" id="expBtn2"><b>RANK</b></button>
                                <span style="background-color: whitesmoke; width:20%;">
                                    <p style="padding-top:3px; text-align-last: center; font-size:15px"><b>{{computedRank}}위</b></p>
                                </span>
                            </div>
                            <div class="progress m-t-20" style="height: 20px;">
                                <div class="progress-bar bg-warning progress-bar-striped" aria-valuemin="0" aria-valuemax="100" :style="{ width: computedScore + '%' }" role="progressbar"> <span>{{computedScore}}%</span> </div>
                            </div>
                        </div>
                    </div>
                </div>
                <hr>

                <!--  TIL   -->
                <div>
                    <h4><b>Today's Post</b></h4>
                    <br>
                    <calendar-heatmap
                    :values="valPostList"
                    :end-date= "todays"
                    tooltip-unit="posts"
                    :max="10"
                    :range-color="['ebedf0', '#c0ddf9', '#73b3f3', '#3886e1', '#17459e']" />
                    <!-- :range-color="['ebedf0', 'dae2ef', '#c0ddf9', '#73b3f3', '#3886e1', '#17459e']" -->
                </div>
                    <hr>

                <!-- Tag chart & Today visited chart -->
                <div class="row row-cols-2" style="height:330px">
                    <div class="quote-chart"  id="lineChart" >
                        <apexchart type="line" height="330" :options="lineOptions" :series="lineSeries"></apexchart>
                    </div>

                    <div id="chart">
                        <h4 class="tagList"><b>Tag List</b></h4>
                        <div v-if="checkTag == true">
                            <apexchart type="donut" :options="donutOptions" :series="donutSeries" class="tag-list"></apexchart>                    
                        </div>
                        <div v-else>
                            <br><br>
                            <h5>게시물을 등록해보세요!</h5>
                        </div>
                    </div>
                </div>

        </div>
    </div>

</template>

<script>
import axios from 'axios'
import moment from 'moment'
import { CalendarHeatmap } from 'vue-calendar-heatmap'
import VueApexCharts from 'vue-apexcharts'

const BACK_URL = 'http://i3c206.p.ssafy.io:8080/api'



export default {
  name: "UserCard",
  components:{
      CalendarHeatmap,
      apexchart:VueApexCharts,
  },
 
  data() {
    return {
        loginNickname: null,  // 로그인한 사용자 닉네임
        userNickname: null,  // mypage의 사용자 닉네임
        userInfo: null, // mypage-user : 0번
        userFCheck: null, // mypage-user : 1번
        userFerList: null, // mypage-user : 2번 나를 팔로우한사람들의 리스트
        userFingList: null, // mypage-user : 3번 내가 팔로우한사람들의 리스트
        userPostList:[], // mypage-user : 4번
        userThumbnail: null, // mypage-user : 5번
        userTodays: null, // mypage-user : 6번 [오늘 방문자, 토탈 방문자]
        userVistDate:[], // mypage-user : 7번 [오늘 방문자(날짜) - '2020-08-19']
        userVistCnt:[], // mypage-user : 8번 [오늘 방문자(cnt) - '3']
        userScore: '', // mypage-user : 스코어
        userRank: [], // 모든 회원들의 랭킹
        allUsers: '', // 회원가입한 전체 User 수
        todays: null, // sys의 오늘날짜를 계산하기 위한 변수
        cntPostList: {}, // 게시글 및 지식인 질문 및 답변 갯수
        valPostList: [], // TIL에 표시하기 위해서 {date : yyyy-mm-dd, count: num}
        showFollower: false, // 팔로워 & 팔로잉 버튼 활성화 비활성화
        selectFollow: true, // 팔로우 modal 내의 check button 활성화 비활성화(toggle)
        followerUserImg:null, // 팔로우 list목록의 프로필마다 사진
        followingUserImg:null, // 팔로워 list 목록의 프로필마다 사진
        userTagList:[], // mypage-user가 작성한 post의 tag list // 데이터 받아오는 용도
        userTagCnt:[], // mypage-user가 작성한 post의 tag cnt 
        checkTag:true,

        // 도넛 차트(Tag)
        donutSeries: [],
        donutOptions: {
            chart: {
                type: 'donut',
                },           
            labels: [],
            plotOptions: {
                pie: {
                    customScale: 0.9,
                    donut: {
                        size: '50%'
                    }
                }
            },
            responsive: [{
                breakpoint: 480,
                options: {
                    chart: {
                        width: 200,
                        height: 330
                    },
                  
                    legend: {
                        position: 'bottom'
                        }
                    }
                }]
            },
        // 라인 차트(Visited)
        lineSeries: [{
            name:"방문자",
            data:[]
        }],
        lineOptions: {
            chart: {
              height: '330px',
              type: 'line',
              zoom: {
                enabled: false
              }
            },
            dataLabels: {
              enabled: false
            },
            stroke: {
              curve: 'straight'
            },
            title: {
              text: 'Daily visitors',
              align: 'center',
              style: {
                fontSize:  '25px',
                fontWeight: 'bold',
                fontFamily: '600 inherit'
              }
            },
            grid: {
              row: {
                colors: ['#f3f3f3', 'transparent'], // takes an array which will be repeated on columns
                opacity: 0.1
              },
            },
            xaxis: {
              categories: [],
            }
        },
    };
  },

  props: {
    cardUserImage: {
      type: String,
      default: require("@/assets/img/faces/no_img.png")
    },
  },
  
  created() {   
  },

  watch: {
      '$route.params.query' : {
          handler : function () {
            this.Init()
            this.tagFunc()
          },
        deep: true,
        immediate : true
      },
  },
  computed: {
    computedScore(){
          const bronze = 0;
          const silver = 30;
          const gold = 60;
          const platinum = 120;
          const diamond = 240;
          const master = 480;

          let score = this.userScore;
          let percent = 0;

          if(bronze <= score && score < silver){ // 브론즈
              percent = Math.floor((score+0.1)/silver * 100)
              return percent

          }else if(silver <= score && score < gold){ // 실버
              percent = Math.floor((score-silver)/(gold-silver)  * 100)
              return percent

          }else if(gold <= score && score < platinum){ // 골드
              percent = Math.floor((score-gold)/(platinum-gold)  * 100)
              return percent
          }else if(platinum <= score && score < diamond){ // 플레
              percent = Math.floor((score-platinum)/(diamond-platinum) * 100)
              return percent
          }else if(diamond <= score && score < master){ // 다이아
              percent = Math.floor((score-diamond)/(master-diamond)  * 100)
              return percent
          }else{ // 마스터
              percent = Math.floor((score-master)/(500-master)  * 100)
              return percent
          }
      },
    computedGrade(){
          const bronze = 0;
          const silver = 30;
          const gold = 60;
          const platinum = 120;
          const diamond = 240;
          const master = 480;
          
          let score = this.userScore;
          let myGrade = '';

          if(bronze <= score && score < silver){ // 브론즈
              myGrade = 'bronze'
              return myGrade
          }else if(silver <= score && score < gold){ // 실버
              myGrade = 'silver'
              return myGrade
          }else if(gold <= score && score < platinum){ // 골드
              myGrade ='gold'
              return myGrade
          }else if(platinum <= score && score < diamond){ // 플레
              myGrade = 'platinum'
              return myGrade
          }else if(diamond <= score && score < master){ // 다이아
              myGrade = 'diamond'
              return myGrade
          }else{ // 마스터
              myGrade = 'master'
              return myGrade
          }
      },
    computedNext(){
          const bronze = 0;
          const silver = 30;
          const gold = 60;
          const platinum = 120;
          const diamond = 240;
          const master = 480;

          let score = this.userScore;

          if(bronze <= score && score < silver){ // 브론즈
              return silver
          }else if(silver <= score && score < gold){ // 실버
              return gold
          }else if(gold <= score && score < platinum){ // 골드
              return platinum
          }else if(platinum <= score && score < diamond){ // 플레
              return diamond
          }else if(diamond <= score && score < master){ // 다이아
              return master
          }else{ // 마스터
              return 'MAX'
          }
      },
    computedRank() {
        const myNick = this.userInfo.nickname
        const lenUserList = this.allUsers
        let ranks = this.userRank
        let rank = 0
        ranks.sort(compareSecondColumn);

        function compareSecondColumn(a, b) {  // score별로 역순으로 정렬(내림차순)
            if (a[1] === b[1]) {
                return 0;
            }
            else {
                return (a[1] < b[1]) ? 1 : -1;
            }
        }
        
        for(var i=0; i<lenUserList; i++){
            if(myNick == ranks[i].nickname){
                rank = i + 1
            }
        }
        return rank
    },

  },
  

  methods : {
    seeFollower() {
        this.showFollower = true
        // this.$router.push({})
    },
    callFunction() {  
        var currentDateWithFormat = new Date().toJSON().slice(0,10);
        this.todays = currentDateWithFormat
    },

    gotoEdit() {
          this.$router.push({ name : "Edit" })
      },
    doFollow() {
        const config = {
            headers: {
                'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
                }
        }
        axios.post(`${BACK_URL}/follow/${this.userInfo.msrl}`, {msrl:this.userInfo.msrl}, config)
            .then(() => {
                this.Init()
            })
            .catch((err) => {
                console.error(err)
            })
        },
    unFollow() {
        const config = {
            headers: {
                'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
                }
            }
        axios.delete(`${BACK_URL}/follow/${this.userInfo.msrl}`, config)
            .then(() => {
                this.Init()
            })
            .catch((err) => {
                console.error(err)
            })
        },
    gotoBlog(){
          this.$router.push({ name: 'MyBlog', params: { nickname: this.userNickname }})
      },

    Init() {
        const config = {
            headers: {
            'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
            }
        }
    
        this.userNickname = this.$route.query.nickname
        this.loginNickname = this.$cookies.get('nickname')

        axios.get(`${BACK_URL}/member/${this.userNickname}/profile`,config)
        .then(res => {
            this.userInfo = res.data.list[0].list[0]
            this.userFCheck = res.data.list[1].list[0]
            this.userFingList = res.data.list[2].list
            this.userFerList = res.data.list[3].list
            this.userPostList = res.data.list[4].list
            this.userThumbnail = res.data.list[5].list[0]
            this.userTodays = res.data.list[6].list[0]
            this.userVistDate = res.data.list[7].list
            this.userVistCnt = res.data.list[8].list
            this.userScore = this.userInfo.score
            // TIL 찍기
            this.valPostList = [] // 초기화
            this.cntPostList = {} // 초기화

            for(var i=0; i<this.userPostList.length; i++){
                this.userPostList[i] = moment(this.userPostList[i], "YYYY-MM-DD").format().slice(0,10)
                if(this.userPostList[i] in this.cntPostList){
                    this.cntPostList[this.userPostList[i]] += 1
                }
                else{
                    this.cntPostList[this.userPostList[i]] = 1
                }
            }
            for(var key in this.cntPostList){
                var tmp = {date:key, count:this.cntPostList[key]}
                this.valPostList.push(tmp)
            }         
            
            // 팔로우 부분
            let getFerUser = []
            let getFingUser = []
            let tmpFerUser = []
            let tmpFingUser = []
            // 팔로워 닉네임 : 팔로워 사진
            if(this.userFerList.length>0){
                const lenFer = (this.userFerList[0].length + this.userFerList[1].length) / 2
                for(var j=0; j<lenFer; j++){
                    tmpFerUser = {nickname:this.userFerList[0][j], img:this.userFerList[1][j]}
                    getFerUser.push(tmpFerUser)
                }

                this.followerUserImg = getFerUser
            }

            // 팔로잉 닉네임 : 팔로잉 사진
            if(this.userFingList.length>0){
                const lenFing = (this.userFingList[0].length + this.userFingList[1].length) / 2
                for(var p=0; p<lenFing; p++){
                    tmpFingUser = {nickname:this.userFingList[0][p], img:this.userFingList[1][p]}
                    getFingUser.push(tmpFingUser)
                }

                this.followingUserImg = getFingUser
            }

            // Today 계산
            var tmpToday = new Date().toJSON().slice(0,10)
            var tmpDate = []
            var tmpCnt = []
            tmpDate.push(tmpToday)
            tmpCnt.push(this.userTodays)
            var dateLen = this.userVistDate.length
            var dayAgo = new Date()
            for(var z=1; z<5; z++){
                dayAgo.setDate(dayAgo.getDate() - 1)
                tmpDate.push(dayAgo.toJSON().slice(0,10))
                if(dateLen - z >= 0){
                    if(tmpDate[i] == this.userVistDate[dateLen - z]){
                        tmpCnt.push(this.userVistCnt[dateLen - z])
                    }
                    else{
                        tmpCnt.push(0)
                    }
                }
                else{
                    tmpCnt.push(0)
                }
            }
            tmpCnt.reverse()
            tmpDate.reverse()
            this.lineSeries[0].data = tmpCnt
            this.lineOptions.xaxis.categories = tmpDate

        })
        .catch((err) => {
            console.error(err)
        }),


        // 유저 랭크 가져오기
        axios.get(`${BACK_URL}/member/rank`)
        .then(res => {
            this.userRank = res.data.list
            this.allUsers = this.userRank.length
        })
        .catch((err) => {
            console.error(err)
        })      
    },

    tagFunc(){
         axios.get(`${BACK_URL}/tags/blog/${this.userNickname}`)
        .then(res => {            
            this.donutOptions.labels = []
            this.donutSeries = []
            this.userTagList = res.data.list[0].list  // 데이터 받아오는 용도
            this.userTagCnt = res.data.list[1].list
            var tmpTagList = []
            var tmpTagCnt = []
            var sumETC = 0
            for(var i=0; i<this.userTagList.length; i++){
                if(i >= 4){
                    sumETC += this.userTagCnt[i]
                }
                else{
                    tmpTagList.push(this.userTagList[i])
                    tmpTagCnt.push(this.userTagCnt[i])
                }
            }
            if(this.userTagList.length > 4){
                tmpTagList.push('ETC') //donutSeries
                tmpTagCnt.push(sumETC)
            }           
            this.donutOptions.labels = tmpTagList
            this.donutSeries = tmpTagCnt
            if(this.userTagList.length > 0){
                this.checkTag = true
            }
            else{
                this.checkTag = false
            }

        })
        .catch((err) => {
            console.error(err)
        })
    },
  },
  mounted () {
      this.callFunction()
  },
  
}
</script>

<style scoped>
/* @media only screen and (min-width: 400px) {
    #home {
        min-height: 1000px;
        padding: 0 50px;
        font-family: 'Noto Serif KR', serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        background-color: #f4f4f4;
    }
} */
.quote {
    border-right: 0.1em solid whitesmoke;
    padding: 0.5em;
}
.r_bronze {
    color: #894218
}
.r_silver {
    color: #8d9396
}
.r_gold {
    color: #ffd662
}
.r_platinum {
    color: #a0b2c6
}
.r_diamond {
    color: #7DC4E0
}
.r_master {
    color: #8b00ff
}
#homeBt {
    height: fit-content;
}
.listFollow{
    list-style: none;
    padding: 5px 0px 5px 5px;
    margin-bottom: 5px;
    /* border-bottom: 1px solid #efefef; */
    font-size: 18px;
}
.listGroup{
    margin: 0;
    padding: 0;
}
#modalFollow{
    align-items: center;
    justify-content: left;
}
#chart{
    width:300px;
    height:320px;
    margin-top:3px;
}
.card-title{
    margin-top:10px;
    margin-bottom:15px;
}
.tag-list{
    text-transform: uppercase;
}
.grade-img{
    height: 100px;
    width: 100px;
}
#gameContent{
    text-align: -webkit-center;
    padding:0;
}
.card-body{
    margin:auto;
    font-family:inherit;
    padding-bottom: 0;
}
.tagList{
    margin-bottom: 55px;
}
.quote-chart{
    border-right: 0.1em solid whitesmoke;
    padding: 0.5em;
    height: 310px;
}
#chart{
    margin-top:10px;
}
#expBtn1{
    background-color: gray;
    height: 30px;
    padding: 0;
    color: white;
    width: 20%;
}
#expBtn2{
    background-color: gray;
    height: 30px;
    padding: 0;
    color: white;
    width: 20%;
}
#mFollow1{
    width:55px; 
    height:55px; 
    padding:2px;
    border:1px solid darkgrey; 
    margin-right:10px;
    margin-left:10px;
}
#mFollow2{
    width:55px; 
    height:55px; 
    padding:2px;
    border:1px solid darkgrey; 
    margin-right:10px;
    margin-left:10px;
}
.modal-title{
    margin-right: 5px;
}
</style>

