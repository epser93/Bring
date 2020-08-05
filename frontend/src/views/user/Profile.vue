<template>
    <div style="padding-top: 80px;">
        <div class="profile container" style="width:650px">
            <UserCard class="mb-3"/>
            <span v-if="isUpdate">
                <div class="mb-3">
                    <EditProfile/>  
                </div>
                <button class="btn btn-success btn-sm mx-1" @click='updateClose'>창 닫기</button>
            </span>
            <span v-else>
                <button class="btn btn-success btn-sm mx-1" @click='updateOpen'>수정하기</button>
            </span>
        </div>    
    </div>
</template>

<script>
import EditProfile from '../../components/user/EditProfile.vue'
import UserCard from '../../components/user/UserCard.vue'

export default {
    name: 'Profile',
    data(){
        return {
            isUpdate: false
        }
    },
    components: {
        EditProfile,
        UserCard,
    },
    props: {
        
    },
    methods: {
        updateOpen() {
            this.isUpdate = true
            
        },
        updateClose() {
            this.isUpdate = false
            
        }
    }


}
</script>

<style>

</style>

