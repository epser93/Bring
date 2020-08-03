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

}
</script>

<style>

</style>
