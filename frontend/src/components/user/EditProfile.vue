<template>
<div class="container" style="width:650px">
  <div class="card">
    <div class="card-header">
      <strong>Edit Profile</strong>
    </div>
    <div class="card-body card-block">
      <div id="innerCard">
        
        <!-- 이메일 정보 -->
        <div class="section">
          <h5 class="title"><b>이메일</b></h5>
          <div class="d-flex justify-content-between align-items-center">
            <p id="infoEmail"><b>{{ myProfile.uid }}</b></p>
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

      <!-- 프로필 사진 변경 -->
        <div class="section" style="margin-bottom:0">
          <h5 class="title"><b>프로필 사진 변경</b></h5>
          <!-- <img class="popupImageItem" :src="uploadImageFile"> -->
          <div class="filebox" style="margin-top:0">
            <label for="ex_file"><i class="far fa-images"></i> 사진 변경 </label>
            <input type="file" id="ex_file" v-on:change="upload">
          </div>
          <div class="previewBg">
            <img class="previewImg" :src="changeData.uploadFile"> 
          </div>
        </div>



      </div>
    </div>
      <!-- 여기는 제출 -->
    <div class="card-footer">
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

const BACK_URL = 'http://127.0.0.1:8080'


export default {
    name: 'EditProfile',

    data() {
    return {
        myProfile: "",
        inputText:"",
        newImgSrc:"",
        changeData:{
          nickname:null,
          password1:null,
          password2:null,
          password3:null,
          uploadFile:null,
        },
        password1Type:"password",
        password2Type:"password",
        password3Type:"password",
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
          this.changeData.nickname = res.data.data.nickname
          this.changeData.uploadFile = res.data.data.uploadfile
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
          axios.delete(`${BACK_URL}/member/delete/${this.myProfile.msrl}`,config)

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
        this.$router.push({ name : "Profile" }) 
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
              uploadFile : this.changeData.uploadFile

            }
        console.log(paramMember)
        axios.put(`${BACK_URL}/member/update`,paramMember, config)
          .then(() => {
                console.log("수정완료")
                //this.$router.go(-1)
                this.$router.push({ name : "Profile" })                
          })
          .catch((err) => {
            console.log('에러보기')
            console.error(err)
            alert('비밀번호가 다릅니다.')
          })

      },
      upload(e){
        let file = e.target.files;
        let reader = new FileReader();
        
        reader.readAsDataURL(file[0]);
        reader.onload = e => {
        // console.log(e.target.result);
        this.changeData.uploadFile = e.target.result;
        console.log(this.changeData)
        }
      }
    }

}
</script>

<style>
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

</style>
