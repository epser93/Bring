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
                        <div class="d-flex justify-content-center mb-1">
                            <h5 class="mt-1 mb-1"><b class="mr-3">{{ userInfo.nickname }}</b></h5>
                            <button class="btn btn-success btn-sm" id="homeBt" @click="gotoBlog"><i class="fas fa-home"></i></button>
                        </div>
                        <div class="location text-sm-center"><i class="far fa-envelope"></i>  {{ userInfo.uid }}</div>
                        <span><a href="/" data-toggle="modal" data-target="#staticBackdrop" style="color:gray" @click="seeFollower"><i class="fas fa-user-friends"></i> {{userInfo.followersCnt}} follower · {{userInfo.followingCnt}} following</a></span> 
                        <!-- 여기 위에 수정함 data-toggle & href 처리방법 연구필요 -->
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

<!-- <toggle-button @change="onChangeEventHandler"/>
<toggle-button v-model="myDataVariable"/> -->

                    <div class="modal fade" id="staticBackdrop" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-scrollable modal-sm">
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
                                    <ul class="listGroup row row-cols-2">
                                        <div v-if="selectFollow == true">
                                            <li v-for="(follower, img) in followerUserImg" v-bind:key="follower" class="listFollow col">
                                                <div class="row d-flex" id="modalFollow">
                                                    <img class="rounded-circle" :src=img alt="Card image cap" style="width:30px; height:30px;">
                                                    <router-link :to="{ name: 'Profile', query: { nickname: follower }}">{{ follower }}</router-link>
                                                </div>
                                            </li>
                                        </div>

                                        <div v-else>
                                            <li v-for="following in userFingList" v-bind:key="following" class="listFollow col">
                                                <div class="row d-flex" id="modalFollow">
                                                    <img class="rounded-circle" :src=userThumbnail alt="Card image cap" style="width:30px; height:30px;">
                                                    <router-link :to="{ name: 'Profile', query: { nickname: following }}">{{ following }}</router-link>
                                                </div>
                                            </li>
                                        </div>
                                    </ul>
                                    
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">닫기</button>
                                </div>
                            </div>
                        </div>
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
        <!--  TIL   -->
            <h4><b>Today I Post</b></h4>
            
            <calendar-heatmap
            :values="valPostList"
            :end-date= "todays"
            tooltip-unit="posts"
            :max="10"
            :range-color="['ebedf0', '#c0ddf9', '#73b3f3', '#3886e1', '#17459e']" />
            <!-- :range-color="['ebedf0', 'dae2ef', '#c0ddf9', '#73b3f3', '#3886e1', '#17459e']" -->
            <hr>

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
      CalendarHeatmap,
  },
    
  data() {
    return {
        loginNickname: null,
        userNickname: null,
        userInfo: null, // 0번
        userFCheck: null, // 1번
        userFerList: null, // 2번 나를 팔로우한사람들의 리스트
        userFingList: null, // 3번 내가 팔로우한사람들의 리스트
        userPostList:[], // 4번
        userThumbnail: null, // 5번
        userScore: '',
        userRank: [],
        allUsers: '', // 회원가입한 전체 User 수
        todays: null,
        cntPostList: {},
        valPostList: [],
        showFollower: false,
        showFollowing: false,
        selectFollow: true,
        followerUserImg: null, // 팔로우 list목록의 프로필마다 사진
        followingUserImg: null,
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
    },
  },
  
  created() {
    //   this.Init()
  },

  watch: {
      '$route.params.query' : {
          handler : function () {
            this.Init()
          },
        deep: true,
        immediate : true
      },
  },
  computed: {
    // onChangeEventHandler(){
    //     console.log(value)

    // },
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
        //console.log(ranks)
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
    seeFollowing() {
        this.showFollowing = true
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
        //console.log(this.userInfo.msrl)
        axios.post(`${BACK_URL}/follow/${this.userInfo.msrl}`, {msrl:this.userInfo.msrl}, config)
            .then(() => {
                //console.log("팔로우완료")
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
                //console.log("팔로우취소")
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
        //console.log(this.loginNickname)
        this.loginNickname = this.$cookies.get('nickname')

        axios.get(`${BACK_URL}/member/${this.userNickname}/profile`,config)
        .then(res => {
            console.log(res)
            this.userInfo = res.data.list[0].list[0]
            this.userFCheck = res.data.list[1].list[0]
            this.userFingList = res.data.list[2].list
            this.userFerList = res.data.list[3].list
            this.userPostList = res.data.list[4].list
            this.userThumbnail = res.data.list[5].list[0]
            console.log(this.userFerList)
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
            this.userScore = this.userInfo.score  
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

        ////////////////////////////////////////////////////////////////////////
//             console.log("dd./////////////////////////////////dd")
//         console.log(this.userFerList)
// console.log("dd./////////////////////////////////dd")
//         let ferUserImg = []
//         let fingUserImg = []

//         for(var i=0; i<this.userFerList.length; i++){
//             axios.get(`${BACK_URL}/member/${this.userFerList[i]}/profile`,config)
//             .then(res => {
//                 ferUserImg.push(this.userFerList[i], res.data.list[5].list[0])
//                 console.log(this.userFerList[i])
//             })
//             .catch((err) => {
//             console.error(err)
//             })
//         }
//         this.followerUserImg = ferUserImg

//         // console.log("ddddddddddddddddddddddddddddddddddddddddd")
//         // console.log(this.followerUserImg)
//         // console.log("ddddddddddddddddddddddddddddddddddddddddd")


//         for(var j=0; j<this.userFingList.length; i++){
//             axios.get(`${BACK_URL}/member/${this.userFingList[j]}/profile`,config)
//             .then(res => {
//                 fingUserImg.push(this.userFingList[j], res.data.list[5].list[0])
//             })
//             .catch((err) => {
//             console.error(err)
//             })
//         }
//         this.followingUserImg = fingUserImg
    }   
  },
  mounted () {
      this.callFunction()
  },
  
}
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
#homeBt {
    height: fit-content;
}
.listFollow{
    list-style: none;
    padding: 5px 0px 5px 5px;
    margin-bottom: 5px;
    border-bottom: 1px solid #efefef;
    font-size: 18px;
}
.listGroup{
    margin: 0;
    padding: 0;
}
#modalFollow{
    align-items: center;
    justify-content: space-evenly;
}
</style>

