<template>
        <div class="card">
            <div class="card-body">
                <div class="row">
                    <div class="col-5 quote">
                        
                        <img class="rounded-circle mx-auto d-block" :src=cardUserImage alt="Card image cap" style="width:200px;">
                        <h5 class="text-sm-center mt-2 mb-1"><b class="mr-3">{{ myProfile.nickname }}</b></h5>
                        <div class="location text-sm-center"><i class="far fa-envelope"></i>  {{ myProfile.uid }}</div>
                        <span><a href="" style="color:gray"><i class="fas fa-user-friends"></i> {{myProfile.followers.length}} follower</a></span>
                        <span> · </span>
                        <span><a href="" style="color:gray"> {{myProfile.following.length}} follower</a></span>
                        
                        <button class="btn btn-outline-info btn-sm mx-1 mt-2" @click="gotoEdit">
                            <i class="fas fa-user-tie"></i> Edit profile
                        </button>
                        <br>

                    </div>
                       <!-- 에디터 라우터도 바꿔야함 내일 무조건바꾸기 -->
        <!-- 육성게임 -->
                    <div class="mx-3 mt-3"> 
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

                        <h5>다음 등급까지: ({{myScore}}/{{computedNext}})</h5>

                        <p>{{allUsers}}명중 {{computedRank}}위 ---> 이걸 어떻게 표현해야 좋을까?</p> 
                        
                        <div class="progress m-t-20">
                            <div class="progress-bar bg-warning progress-bar-striped" aria-valuemin="0" aria-valuemax="100" :style="{ width: computedScore + '%' }" role="progressbar"> <span>{{computedScore}}%</span> </div>
                        </div>
                    </div>
                </div>
                <hr>
        <!--  TIL  이거 주소다 있음  -->
               <span>ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ
                   <br>
                   ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ
                   <br>
                   ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ
                   <br>
                   ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ
                   <br>
                   ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ
                   <br>
                   ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ
                   <br>
                   ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ
               </span>
               <hr>
            

        <!-- 여기다가는 chart 할거임 이것도 -->
            ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
            <br>
            <br>
            <br>
            <br>
                                            차트
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

const BACK_URL = 'http://127.0.0.1:8080'

export default {
  name: "UserCard",

  data() {
    return {
        uploadImageFile: '',
        myProfile: null,
        myScore: '',
        myRank: [],
        allUsers: '', // 회원가입한 전체 User 수
    };
  },

  props: {
    cardUserImage: {
      type: String,
      default: require("@/assets/img/faces/no_img.jpg")
    }
  },
  
  created() {
    const config = {
        headers: {
        'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
        }
    }

    // 나의 score 가져오기
    axios.get(`${BACK_URL}/member/mypage`,config)
    .then(res => {
        console.log(res)
        this.myProfile = res.data.data
        this.myScore = res.data.data.score //
        // this.myScore = 500 // test 플렌
    })
    .catch((err) => {
        console.error(err)
    }),

    // 나의 rank 가져오기
    axios.get(`${BACK_URL}/member/rank`)
    .then(res => {
        this.myRank = res.data.list
        this.allUsers = this.myRank.length
        console.log(this.myRank)
    })
    .catch((err) => {
        console.error(err)
    })

  },

  computed: {
    computedScore(){
          console.log('computed');
          const bronze = 0;
          const silver = 30;
          const gold = 60;
          const platinum = 120;
          const diamond = 240;
          const master = 480;

          let score = this.myScore;
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
          
          let score = this.myScore;
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

          let score = this.myScore;

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
        const myNick = this.myProfile.nickname
        const lenUserList = this.allUsers
        let ranks = this.myRank
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
      is_img() {
          
      },
      gotoEdit() {
          //this.$router.push({ name : "DetailPost" , params: { post: post, nickname : post.writer, post_id : post.post_id }})
      }
  },


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

