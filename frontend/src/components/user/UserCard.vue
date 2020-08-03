<template>
        <div class="card">
            <div class="card-body">
                <div class="row">
                    <div class="col-5 quote">
                        
                        <img class="rounded-circle mx-auto d-block" :src=cardUserImage alt="Card image cap" style="width:200px;">
                        <h5 class="text-sm-center mt-2 mb-1"><b class="mr-3">{{ myProfile.nickname }}</b><button type="submit" class="btn btn-danger btn-sm "><i class="far fa-user"></i>1</button></h5>

                        <div class="location text-sm-center"><i class="far fa-envelope"></i>  {{ myProfile.uid }}</div>

                        <br>
                        
                        <!-- 사진바꾸기 -->
                        <div class="image-change">
                            <img class="popupImageItem" :src="uploadImageFile">
                            <button type="submit" class="btn btn-outline-success btn-sm mx-1">
                                <i class="far fa-images"></i> 사진업로드
                            </button>
                            <button class="btn btn-outline-secondary btn-sm mx-1" @click="basicImg">
                                <i class="fas fa-user-tie"></i> 기본이미지
                            </button>
                        </div>
                    </div>
                   

        <!-- 나의 정보(인기게시글, 최근 지식인) -->
                    <div class="col-7">
                        <div class="mt-4 row">
                            <div class="col-6"> 
                                <h5><b>HOT Post</b></h5>
                                <div>
                                    조회수
                                </div>
                            </div>
                            <div class="col-6">
                                <h5><b>지식인</b></h5>
                                <div>
                                    recently
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <hr>
        <!-- 육성게임 -->
               <div>
                    <h3 class="card-title"><b>My Level</b> </h3>
                    <div v-if="computedRank === 'bronze'" class="mb-3">
                        <h3 class="r_bronze"><i class="fas fa-medal"></i></h3>
                        <p>Bronze</p>
                    </div>
                    <div v-else-if="computedRank === 'silver'" class="mb-3">
                        <h3 class="r_silver"><i class="fas fa-medal"></i></h3>
                        <p>Silver</p>
                    </div>
                    <div v-else-if="computedRank === 'gold'" class="mb-3">
                        <h3 class="r_gold"><i class="fas fa-medal"></i></h3>
                        <p>Gold</p>
                    </div>
                    <div v-else-if="computedRank === 'platinum'" class="mb-3">
                        <h3 class="r_platinum"><i class="fas fa-trophy"></i></h3>
                        <p>Platinum</p>
                    </div>
                    <div v-else-if="computedRank === 'diamond'" class="mb-3">
                        <h3 class="r_diamond"><i class="fas fa-trophy"></i></h3>
                        <p>Diamond</p>
                    </div>
                    <div v-else class="mb-3">
                        <h3 class="r_master"><i class="fas fa-trophy"></i></h3>
                        <p>Master</p>
                    </div>
                     
                   <!-- <div class="progress m-t-20">
                        <div class="progress-bar bg-info progress-bar-striped" aria-valuenow="85" aria-valuemin="0" aria-valuemax="100" style="width: 50%; height:15px;" role="progressbar"> <span>30% Complete (success)</span> </div>
                    </div> --> 

                    <h5>다음 등급까지: </h5>

                    <div class="progress m-t-20">
                        <div class="progress-bar bg-warning progress-bar-striped" aria-valuemin="0" aria-valuemax="100" :style="{ width: computedScore + '%' }" role="progressbar"> <span>{{computedScore}}% Complete</span> </div>
                    </div>
                </div>
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
    // console.log(config)
    axios.get(`${BACK_URL}/member/mypage`,config)
    .then(res => {
        console.log(res)
        this.myProfile = res.data.data
        this.myScore = res.data.data.score //
        // this.myScore = 200 // test 다이아
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
          const platinum = 100;
          const diamond = 150;
          const master = 250;

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
      computedRank(){
          const bronze = 0;
          const silver = 30;
          const gold = 60;
          const platinum = 100;
          const diamond = 150;
          const master = 250;
          
          let score = this.myScore;
          let myRank = '';

          if(bronze <= score && score < silver){ // 브론즈
              myRank = 'bronze'
              return myRank
          }else if(silver <= score && score < gold){ // 실버
              myRank = 'silver'
              return myRank
          }else if(gold <= score && score < platinum){ // 골드
              myRank ='gold'
              return myRank
          }else if(platinum <= score && score < diamond){ // 플레
              myRank = 'platinum'
              return myRank
          }else if(diamond <= score && score < master){ // 다이아
              myRank = 'diamond'

              return myRank
          }else{ // 마스터
              myRank = 'master'
              return myRank
          }
      },
  },

  methods : {
      is_img() {
          
      },
      basicImg() {

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

