<template>
        <div class="card">
            <div class="card-body">
                <div class="row">
                    <div class="col-5 quote">
                        <div v-if="userThumbnail == null">
                            <img class="rounded-circle mx-auto d-block" :src=cardUserImage alt="Card image cap" style="width:120px; height:120px;">
                        </div>
                        <div v-else>
                            <img class="rounded-circle mx-auto d-block" :src=userThumbnail alt="Card image cap" style="width:120px; height:120px;">
                        </div>
                        <h5 class="text-sm-center mt-2 mb-1"><b class="mr-3">{{ userInfo.nickname }}</b></h5>
                        <div class="location text-sm-center"><i class="far fa-envelope"></i>  {{ userInfo.uid }}</div>
                        <span><a href="" style="color:gray"><i class="fas fa-user-friends"></i> {{userInfo.followersCnt}} follower</a></span>
                        <span> · </span>
                        <span><a href="" style="color:gray"> {{userInfo.followingCnt}} follower</a></span>
                        
                        <button class="btn btn-outline-info btn-sm mx-1 mt-2" @click="gotoEdit">
                            <i class="fas fa-user-tie"></i> Edit profile
                        </button>
                        <br>

                    </div>

                    <!-- 육성게임 -->
                    <div class="col-6 mx-3 mt-3"> 
                        <h3 class="card-title"><b>My Level</b> </h3>
                        <div v-if="computedGrade === 'bronze'" class="mb-3">
                            <h3 class="r_bronze"><i class="fas fa-medal"></i></h3>
                            <p>Bronze</p>
                        </div>
                        <div v-else-if="computedGrade === 'silver'" class="mb-3">
                            <h3 class="r_silver"><i class="fas fa-medal"></i></h3>
                            <p>Silver</p>
                        </div>
                        <div v-else-if="computedGrade === 'gold'" class="mb-3">
                            <h3 class="r_gold"><i class="fas fa-medal"></i></h3>
                            <p>Gold</p>
                        </div>
                        <div v-else-if="computedGrade === 'platinum'" class="mb-3">
                            <h3 class="r_platinum"><i class="fas fa-trophy"></i></h3>
                            <p>Platinum</p>
                        </div>
                        <div v-else-if="computedGrade === 'diamond'" class="mb-3">
                            <h3 class="r_diamond"><i class="fas fa-trophy"></i></h3>
                            <p>Diamond</p>
                        </div>
                        <div v-else class="mb-3">
                            <h3 class="r_master"><i class="fas fa-trophy"></i></h3>
                            <p>Master</p>
                        </div>

                        <h5>다음 등급까지: ({{userScore}}/{{computedNext}})</h5>

                        <p>{{allUsers}}명중 {{computedRank}}위</p> 
                        
                        <div class="progress m-t-20">
                            <div class="progress-bar bg-warning progress-bar-striped" aria-valuemin="0" aria-valuemax="100" :style="{ width: computedScore + '%' }" role="progressbar"> <span>{{computedScore}}%</span> </div>
                        </div>
                    </div>
                </div>
                <hr>
        <!--  TIL  이거 주소다 있음  -->
        <!-- https://github.com/julienr114/vue-calendar-heatmap#usage -->
            <h4><b>Today I Post</b></h4>
            
            <calendar-heatmap
            :values="valPostList"
            :end-date= "todays"
            tooltip-unit="posts"
            :max="4"
            :range-color="['ebedf0', '#c0ddf9', '#73b3f3', '#3886e1', '#17459e']" />
            <!-- :range-color="['ebedf0', 'dae2ef', '#c0ddf9', '#73b3f3', '#3886e1', '#17459e']" -->
            <hr>
            {{computedPost}}

        <!-- 여기다가는 chart 할거임 이것도 -->
            ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
            <br>
            <br>
            <br>
            <br>
            <br>                                
            <br>
            <br>
            <br>
            ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        </div>
    </div>

</template>

<script>
import axios from 'axios'
import moment from 'moment'
import { CalendarHeatmap } from 'vue-calendar-heatmap'

const BACK_URL = 'http://127.0.0.1:8080'

export default {
  name: "UserCard",
  components:{
      CalendarHeatmap
  },
    
  data() {
    return {
        //changeNickname: null,
        loginNickname: null,
        userNickname: null,
        userInfo: null,
        userPostList:[],
        userThumbnail: null,
        userScore: '',
        userRank: [],
        allUsers: '', // 회원가입한 전체 User 수
        todays: null,
        cntPostList: {},
        valPostList: [],
    };
  },

  props: {
    cardUserImage: {
      type: String,
      default: require("@/assets/img/faces/no_img.jpg")
    },
    changeNickname: {
        type: String,
        default: null
    }
  },
  
  created() {
    const config = {
        headers: {
        'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
        }
    }
    this.loginNickname = this.$cookies.get('nickname')
    // console.log("파람")
    // console.log(this.$route.params.changeNickname)
    // console.log(this.changeNickname)
    this.userNickname = this.$cookies.get('nickname')
    // if(this.$route.params.changeNickname != null){
    //     this.userNickname = this.$route.params.changeNickname
    // }
    // else{
    //     this.userNickname = this.$cookies.get('nickname') // 나  일단 이걸로 사용 그 다음에는 넘겨줄때의 nickname으로 대체해야함
    // }
    // user정보 가져오기
    axios.get(`${BACK_URL}/member/${this.userNickname}/profile`,config)
    .then(res => {
        // console.log(res)
        this.userInfo = res.data.list[0].list[0]
        this.userPostList = res.data.list[3].list
        this.userThumbnail = res.data.list[4].list[0]
        
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
            console.log("vhvh")
            var tmp = {date:key, count:this.cntPostList[key]}
            this.valPostList.push(tmp)
            console.log(this.valPostList)
            console.log(this.cntPostList[key])
            console.log(this.valPostList)
            console.log("vhvh")
        }
        console.log("아래는 딕셔너리로 뽑기")
        console.log(this.cntPostList)
        console.log("위에는 딕셔너리로 뽑기")
        

        console.log("유저 정보")
        console.log(this.userInfo)
        console.log("유저 게시글 날짜")
        console.log(this.userPostList)
        console.log("유저 썸네일")
        console.log(this.userThumbnail)
         
        this.userScore = this.userInfo.score

        console.log("유저 점수")
        console.log(this.userScore)

    })
    .catch((err) => {
        console.error(err)
    }),

    // 유저 랭크 가져오기
    axios.get(`${BACK_URL}/member/rank`)
    .then(res => {
        console.log("유저 랭크")
        // console.log(res)
        this.userRank = res.data.list
        this.allUsers = this.userRank.length
        console.log(this.userRank)
    })
    .catch((err) => {
        console.error(err)
    })
  },

  watch: {
      
  },
  computed: {
    // computedPost(){
    // // 유저 게시글 날짜 계산
    //     for(var i=0; this.userPostList.length; i++){
    //         let tmp = this.userPostList[i]
    //     }
    //     return 0
    // },
    computedScore(){
          // console.log('computed');
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
    callFunction() {  
        var currentDateWithFormat = new Date().toJSON().slice(0,10);
        this.todays = currentDateWithFormat
        console.log(currentDateWithFormat);
        console.log(this.today + "ㅅㅅㅅㅅㅅ")
     
      },
      gotoEdit() {
          this.$router.push({ name : "Edit" })
      }
  },
  mounted () {
      this.callFunction()
    }



};
</script>

<style scoped>
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
</style>

