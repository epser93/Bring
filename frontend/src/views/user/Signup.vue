<template>
    <div class="user" id="join"> 
        <div class="wrapC table">
            <div class="middle">
                <h1>회원가입</h1>
                <div class="form-wrap">
                    <div class="input-wrap">
                        <input v-model="signupData.uid"
                            id="id"
                            placeholder="이메일을 입력해주세요" type="text"/>
                    </div>
                    <div class="input-wrap">
                        <input v-model="signupData.name"
                            id="name"
                            placeholder="이름을 입력해주세요" type="text"/>
                    </div>

                    <div class="input-wrap">
                        <input v-model="signupData.nickname" 
                            id="nickname"
                            placeholder="닉네임을 입력해주세요"
                            type="text"/>
                    </div>

                    <div class="input-wrap password-wrap">
                        <input v-model="signupData.password1"
                            id="password" 
                            :type="password1Type"
                            placeholder="비밀번호를 입력해주세요"/>
                        <span :class="{active : password1Type==='text'}">
                                <i class="fas fa-eye"></i>
                            </span>
                    </div>

                    <div class="input-wrap password-wrap">
                        <input v-model="signupData.password2" 
                            id="password-confirm"
                            :type="password2Type"
                            placeholder="비밀번호를 한번 더 입력해주세요"/>
                        <span :class="{active : password2Type==='text'}">
                                <i class="fas fa-eye"></i> 
                            </span>
                    </div>
                </div>

                <!-- <label>
                    <input v-model="isTerm" type="checkbox" id="term"/>
                    <span>약관에 동의합니다</span>
                </label>

                <span class="go-term">약관 보기</span> -->

                <button class="btn" @click="signup">
                    <span>
                        작성완료
                    </span>
                </button>
            </div>


        </div> 
        

    </div>
</template>

<script>


    export default {
        name: 'Signup',
        components: {
        },
        created(){


        },
        methods: {
            signup() {
                this.$emit('submit-signup-data', this.signupData)
            }

        },
        watch: {
        },
        data: () => {
            return {
                signupData: {
                    email: '',
                    uid: '',
                    nickname: '',
                    password1: null,
                    password2: null,
                },

                isTerm: false,
                password1Type:"password",
                password2Type:"password",
            }
        }

    }

</script>


