<template>
<div id="editProfile">
  <div class="card">
    <div class="card-body card-block">
      <div id="innerCard">
        <h4 class="my-4">Edit Profile</h4>
        <!-- 이메일 정보 -->
        <div class="section">
          <h5 class="title"><b>이메일</b></h5>
          <div class="d-flex justify-content-between align-items-center">
            <p id="infoEmail"><b>{{ userInfo.uid }}</b></p>
            <!-- 회원탈퇴 -->
            <button button type="button" class="btn btn-danger" data-toggle="modal" data-target="#deleteModal">
              <i class="fa fa-ban"></i> 회원 탈퇴
            </button>
          </div>

          <!-- 회원탈퇴 모달 -->
          <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title" id="deleteModalLabel">회원 탈퇴</h5>
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                  </button>
                </div>
                <div class="modal-body">
                  <div class="form-group">
                    <p style="font-size:14px">
                      삭제된 계정은 다시 복구할 수 없고, 게시물이나 정보는 완전히 삭제됩니다.
                      그래도 삭제하시려면 <b>"역사는 산 사람이 쓴다"</b> 문구를 작성해주세요.
                    </p>
                    <label for="recipient-name" class="col-form-label"></label>
                    <input type="text" class="form-control" id="recipient-name" v-model="inputText" placeholder="내용을 입력해주세요.">
                  </div>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-primary" data-dismiss="modal">취소</button>
                  <button type="button" class="btn btn-danger" data-dismiss="modal" @click="deleteUser">회원탈퇴</button>
                </div>
              </div>
            </div>
          </div>

        </div>
        <!-- 닉네임 -->
        <div class="section">
          <h5 class="title"><b>닉네임</b></h5>
          <input type="text" v-model="changeData.nickname" class="inputRange">   
        </div>

        <!-- 패스워드 -->
        <div class="section">
          <h5 class="title"><b>현재 비밀번호</b></h5>
          <input v-model="changeData.password3" id="pw1" :type="password3Type" placeholder="현재 비밀번호를 입력해주세요" class="inputRange"/>
        </div>
        <div class="section">
          <h5 class="title"><b>새 비밀번호</b></h5>
          <input v-model="changeData.password1" id="pw2" :type="password1Type" placeholder="새 비밀번호를 입력해주세요" class="inputRange"/>
        </div>
        <div class="section">
          <h5 class="title"><b>새 비밀번호 확인</b></h5>
           <input v-model="changeData.password2" id="pw3" :type="password2Type" placeholder="새 비밀번호를 한번 더 입력해주세요" class="inputRange"/>
        </div>

        <div class="section" style="margin-bottom:0">
          <h5 class="title"><b>프로필 사진 변경</b></h5>
          <div class="filebox" style="margin-top:0">
            <label for="ex_file"><i class="far fa-images"></i> 사진 변경 </label>
            <input type="file" id="ex_file" ref="files" v-on:change="uploadImage">
          </div>
          <!-- <div class="previewBg">
            <img class="previewImg" :src="galleryData.filePath"> 
          </div> -->
        </div>
      </div>
    </div>
      <!-- 여기는 제출 -->
    <div class="card-footer bg-transparent">
        <button type="submit" class="btn btn-success mx-2" @click="changeInfo">
          <i class="fa fa-dot-circle-o"></i> 완료
        </button>
        <button type="reset" class="btn btn-danger mx-2" @click="backProfile">
          <i class="fa fa-ban"></i> 취소
        </button>
      </div>
  </div>
</div>
</template>

<script>

import axios from 'axios'

const BACK_URL = 'http://i3c206.p.ssafy.io:8080/api'


export default {
    name: 'EditProfile',

    data() {
    return {
        loginNickname:"",
        userInfo: "",
        inputText:"",
        file:[],
        // galleryData:[],
        changeData:{
          nickname:null,
          password1:null,
          password2:null,
          password3:null,
        },
        password1Type:"password",
        password2Type:"password",
        password3Type:"password",
      }
    },

    mounted(){

    },

    created() {
      const config = {
          headers: {
          'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
          }
      }
      this.loginNickname = this.$cookies.get('nickname')

      axios.get(`${BACK_URL}/member/${this.loginNickname}/profile`,config)
      .then(res => {
          console.log(res)
          this.userInfo = res.data.list[0].list[0]
          this.changeData.nickname = this.userInfo.nickname
      })
      .catch((err) => {
          console.error(err)
      })
    },

    methods:{
      deleteUser(){
        const config = {
          headers: {
          'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
          }
        }

        var deleteText = "역사는 산 사람이 쓴다";
        if(deleteText == this.inputText){
          axios.delete(`${BACK_URL}/member/delete/${this.userInfo.msrl}`,config)

          .then(() => {
                this.$cookies.remove('X-AUTH-TOKEN')
                this.$cookies.remove('nickname')
                console.log("위")
                this.$router.replace({ name : 'Home' })
                location.reload()
                console.log("아래")
          })
          .catch((err) => {
            console.error(err)
          })
        }
      },

      backProfile(){
        // this.$router.go(-1) // 뭘로할까?? 뒤로가기?? 아니면 메인화면으로 가기??
        //this.$router.push('/')
        this.$router.push({ name : "Profile", query: {nickname: this.changeData.nickname}})
      },

      changeInfo(){
        const config = {
          headers: {
          'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        const paramMember = {
              nickname : this.changeData.nickname,
              password1 : this.changeData.password1,
              password2 : this.changeData.password2,
              password3 : this.changeData.password3,
        }
        const nicknamechk = /^[가-힣a-zA-Z0-9]{2,15}$/;
        const passwordchk = /^.*(?=^.{8,15}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/;
        // 비밀번호를 안 적고 확인만 누르는 경우
        console.log(this.changeData.password1)
        console.log(this.changeData.nickname)
        if (!this.changeData.password3) {
          alert('현재 비밀번호를 입력해주세요')
        } 
        // 닉네임 변경이 없을 경우
        else if (this.$cookies.get('nickname') === this.changeData.nickname) {
          if (!this.changeData.password1 && !this.changeData.password2) {
            alert('닉네임을 변경해주세요')
          } else if (!this.changeData.password1) {
            alert('새 비밀번호를 입력해주세요')
          } else if (!passwordchk.test(this.changeData.password1)) {
            alert("비밀번호는 문자+숫자+특수문자 8~20 자리입니다.")
          } else if (this.changeData.password1 !== this.changeData.password2) {
            alert('새 비밀번호와 확인란이 서로 다릅니다.')
          } else {
            axios.put(`${BACK_URL}/member/update`,paramMember, config)
              .then(() => {
                    this.$cookies.set('nickname', this.changeData.nickname)
                    this.$router.push({ name: 'Profile', query: {nickname: this.changeData.nickname}})    
                    location.reload()  // 여기
              })
              .catch((err) => {
                console.error(err)
                console.log('여기 들어옴')
                alert('비밀번호가 다릅니다.')
              })
          }
        }
        // 닉네임 변경이 일어난 경우
        else {
          if (!nicknamechk.test(this.changeData.nickname)) {
            alert('닉네임은 2~15자 이내여야 합니다.')
          }
          // 새 비밀번호 , 확인란 공란 == 순수 닉네임만 변경하려는 경우
          else if (!this.changeData.password1 && !this.changeData.password2) {
            axios.put(`${BACK_URL}/member/update`,paramMember, config)
              .then(() => {
                    this.$cookies.set('nickname', this.changeData.nickname)
                    this.$router.push({ name: 'Profile', query: {nickname: this.changeData.nickname}})    
                    location.reload()  // 여기
              })
              .catch((err) => {
                console.error(err)
                alert('비밀번호가 다릅니다.')
              })
          } else if (!passwordchk.test(this.changeData.password1)) {
            alert("비밀번호는 문자+숫자+특수문자 8~20 자리입니다.")
          } else if (this.changeData.password1 !== this.changeData.password2) {
            alert('새 비밀번호와 확인란이 서로 다릅니다.')
          } else {
            axios.put(`${BACK_URL}/member/update`,paramMember, config)
              .then(() => {
                    this.$cookies.set('nickname', this.changeData.nickname)
                    this.$router.push({ name: 'Profile', query: {nickname: this.changeData.nickname}})    
                    location.reload()  // 여기
              })
              .catch((err) => {
                console.error(err)
                alert('비밀번호가 다릅니다.')
              })
          }
        }
      },

      uploadImage(){
        const config = {
          headers: {
          'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
           }
         }
        this.file = this.$refs.files.files;
        console.log(this.$refs.files.files)


        let formData = new FormData();
        formData.append('file', this.file[0]);
        console.log(this.file[0])


        console.log(formData[0])

        axios.post(`${BACK_URL}/member/profile/image/${this.userInfo.msrl}`, formData, config)
          .then(() => {
            console.log("사진완료")
            this.$router.push({ name: 'Profile', query: { nickname: this.loginNickname }})           
          })
          .catch((err) => {
            console.error(err)
            alert('사진부분에러남.')
          })

      }

    }

}
</script>

<style scoped>
@media only screen and (min-width: 1000px) {
  #editProfile {
    background-color: #f4f4f4;
    padding: 50px;
    
  }
  .card {
    width: 800px;
    margin: auto;
  }
}

#editProfile {
    background-color: #f4f4f4;
    padding: 50px;
}

.inputRange{
  width:100%;
  height:40px;
}
#innerCard{
  width:70%;
  margin: auto;
}
.title{
  text-align: left;
}
#infoEmail{
  text-align: left;
}
.section{
  margin-top: 15px;
  margin-bottom: 15px;
}
.filebox{
  margin-top: 20px;
  text-align: left;
}
.filebox label {
  display: inline-block;
  padding: .5em .75em;
  color: #fff;
  font-size: inherit;
  line-height: normal;
  vertical-align: middle;
  background-color: #5cb85c;
  cursor: pointer;
  border: 1px solid #4cae4c;
  border-radius: .25em;
  -webkit-transition: background-color 0.2s;
  transition: background-color 0.2s;
}

.filebox label:hover {
  background-color: #6ed36e;
}

.filebox label:active {
  background-color: #367c36;
}

.filebox input[type="file"] {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  /* overflow: hidden; */
  clip: rect(0, 0, 0, 0);
  border: 0;
}
.previewImg{
  width:150px;
  height:150px;
  margin-top:15px;
}
.previewBg{
  background-color: whitesmoke;
  height: 180px;
}
#editProfile{
  min-height: 1000px;
}

</style>
