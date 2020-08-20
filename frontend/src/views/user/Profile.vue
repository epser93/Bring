<template>
    <div id="profile" style="padding-top: 10px;">
        <div class="profile container" style="width:650px">
            <UserCard class="mb-3"/>
        </div>    
    </div>
</template>

<script>
import UserCard from '../../components/user/UserCard.vue'

export default {
    name: 'Profile',
    data(){
        return {
            isUpdate: false
        }
    },
    components: {
        UserCard,
    },
    props: {
        
    },
    methods: {
    }


}
</script>

<style>
#profile{
    min-height: 1000px;
    min-width: 400px;
    background-color: #f4f4f4;
}


</style>

