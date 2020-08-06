<template>
<div class="container" style="padding-top: 80px; width:650px">
  <div class="card">
    <div class="card-header">
      <strong>Edit Profile</strong>
    </div>
    <div class="card-body card-block">
        <div class="row form-group">
          <!-- 닉네임 -->
          <div class="col col-md-3">
            <label class=" form-control-label"></label>Nickname
          </div>
          <!-- 회원탈퇴 -->
          <div class="col-12 col-md-9">
            <div class="row">
            <p class="form-control-static col-10" style="padding-top: 5px; padding-right: 0px; padding-left: 40px;">{{ myProfile.nickname }}</p>
            <button type="button" class="btn btn-danger btn-sm col-2" data-toggle="modal" data-target="#deleteModal">
              <i class="fa fa-ban"></i> 탈퇴
              <!-- 이거 왜 옆으로 안가냐 -->
            </button>
            </div>
          </div>
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
        
      <!-- 이메일 정보 -->
        <div class="row form-group">
          <div class="col col-md-3"><label for="email-input" class=" form-control-label">Email</label></div>
          <div class="col-12 col-md-9">
            <p class="form-control-static">{{ myProfile.uid }}</p>
          </div>
        </div>

      <!-- 패스워드 변경해야함 -->
      <!-- 이거 해야함 이거 해야함 이거 해야함 이거 해야함  -->
        <div class="row form-group">
          <div class="col col-md-3"><label for="password-input" class=" form-control-label">Password</label></div>
          <div class="col-12 col-md-9">
            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#changePwModal">비밀번호 변경</button>
            <!-- 모달 -->
            <div class="modal fade" id="changePwModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="changePwModalLabel" aria-hidden="true">
              <div class="modal-dialog">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title" id="changePwModalLabel"><b>비밀번호 변경</b></h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                      <span aria-hidden="true">&times;</span>
                    </button>
                  </div>
                  <div class="modal-body">
                    
                     <div class="row form-group">
                        <div class="col col-md-4">
                          <label class=" form-control-label"></label>새 비밀번호
                        </div>
                        <!-- 회원탈퇴 -->
                        <div class="col-12 col-md-8">
                          <input v-model="changeData.password1" id="pw1"
                          :type="password1Type"
                          placeholder="비밀번호를 입력해주세요"/>
                        </div>
                      </div>
                      <div class="row form-group">
                        <div class="col col-md-4">
                          <label class=" form-control-label"></label>새 비밀번호 확인
                        </div>
                        <!-- 회원탈퇴 -->
                        <div class="col-12 col-md-8">
                           <input v-model="changeData.password2" id="pw2"
                            :type="password2Type"
                            placeholder="비밀번호를 한번 더 입력해주세요"/>
                        </div>
                      </div>             


                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">취소</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" @click="changePw">변경하기</button>
                  </div>
                </div>
              </div>
            </div>
                    
          </div>
        </div>


      <!-- 프로필 사진 변경 -->
        <div class="row form-group">
          <div class="col col-md-3"><label for="file-input" class=" form-control-label">File input</label></div>
          <div class="col-12 col-md-9">
            <input type="file" id="file-input" name="file-input" class="form-control-file">
          </div>
        </div>

    
      <!-- 사진바꾸기 -->
      <!-- <div class="image-change">
          <img class="popupImageItem" :src="uploadImageFile">
          <button type="submit" class="btn btn-outline-success btn-sm mx-1">
              <i class="far fa-images"></i> 사진업로드
          </button>
          <button class="btn btn-outline-secondary btn-sm mx-1" @click="basicImg">
              <i class="fas fa-user-tie"></i> 기본이미지
          </button>
      </div> -->
      
      <!-- 여기다가는 자기소개 -->
      <!-- 여기다가 기본적인 자기소개 써놓기 -->
      <form action="" method="post" enctype="multipart/form-data" class="form-horizontal">
        <div class="row form-group">
          <div class="col col-md-3"><label for="email-input" class=" form-control-label">한마디</label></div>
          <div class="col-12 col-md-9">

              <textarea name="" id="" cols="30" rows="10" placeholder="dd"></textarea>
              <!-- oninput="this.parentNode.dataset.value = this.value" -->

          </div>
        </div>
      </form>
    </div>
      <!-- 여기는 제출 -->
    <div class="card-footer">
        <button type="submit" class="btn btn-primary btn-sm mx-2">
          <i class="fa fa-dot-circle-o"></i> 수정
        </button>
        <button type="reset" class="btn btn-danger btn-sm mx-2" @click="backProfile">
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
        changeData:{
          password1:null,
          password2:null,
        },
        password1Type:"password",
        password2Type:"password",
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
        this.$router.push('/')
      },
      changePw(){
        const config = {
          headers: {
          'X-AUTH-TOKEN': this.$cookies.get('X-AUTH-TOKEN')
          }
        }
        const paramMember = {
              // nickname : this.myProfile.nickname,
              password1 : this.changeData.password1,
              password2 : this.changeData.password2
            }
        console.log(paramMember)
        axios.put(`${BACK_URL}/member/update`,paramMember, config)
          .then(() => {
                console.log("수정완료")                
          })
          .catch((err) => {
            console.log('에러보기')
            console.error(err)
            alert('비밀번호가 다릅니다.')
          })

      },
    }

}
</script>

<style>

</style>
