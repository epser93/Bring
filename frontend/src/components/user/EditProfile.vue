<template>
      <div class="card">
        <div class="card-header">
          <strong>Edit Profile</strong>
        </div>
        <div class="card-body card-block">
          <form action="" method="post" enctype="multipart/form-data" class="form-horizontal">
            <div class="row form-group">
              <div class="col col-md-3"><label class=" form-control-label"></label>Nickname</div>
              <div class="col-12 col-md-9">
                <p class="form-control-static">{{ myProfile.nickname }}</p>
               <!-- Button trigger modal -->
                <button type="button" class="btn btn-danger btn-sm mx-2" data-toggle="modal" data-target="#deleteModal">
                  <i class="fa fa-ban"></i> 회원탈퇴
                </button>
                
                <!-- Modal -->
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
            </div>
            

            <div class="row form-group">
              <div class="col col-md-3"><label for="email-input" class=" form-control-label">Email</label></div>
              <div class="col-12 col-md-9">
                <p class="form-control-static">{{ myProfile.uid }}</p>
              </div>
            </div>
            <div class="row form-group">
              <div class="col col-md-3"><label for="password-input" class=" form-control-label">Password</label></div>
              <div class="col-12 col-md-9"><input type="password" id="password-input" name="password-input" placeholder="새 비밀번호" class="form-control"></div>
            </div>
             <div class="row form-group">
              <div class="col col-md-3"><label for="password-input" class=" form-control-label">Password</label></div>
              <div class="col-12 col-md-9"><input type="password" id="password-input" name="password-input" placeholder="새 비밀번호 확인" class="form-control"><small class="help-block form-text">Please enter a complex password</small></div>
            </div>
            <div class="row form-group">
              <div class="col col-md-3"><label for="file-input" class=" form-control-label">File input</label></div>
              <div class="col-12 col-md-9"><input type="file" id="file-input" name="file-input" class="form-control-file"></div>
            </div>
          </form>
        </div>
        <div class="card-footer">
          <button type="submit" class="btn btn-primary btn-sm mx-2">
            <i class="fa fa-dot-circle-o"></i> Submit
          </button>
          <button type="reset" class="btn btn-danger btn-sm mx-2">
            <i class="fa fa-ban"></i> Reset
          </button>
        </div>
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
</template>

<script>

import axios from 'axios'

const BACK_URL = 'http://127.0.0.1:8080'


export default {
    name: 'EditProfile',

    data() {
    return {
        myProfile: null,
        inputText:"",
      };
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
    }

}
</script>

<style>

</style>
