<template>
  <div class="aboutTem">
      <div class="container-about">
          <div class="content-section">
            <div class="">
                <img src="../../assets/img/aboutUs.png" id="logoImg" alt="">
            </div>
            <div class="d-flex justify-content-center">
                <img src="../../assets/img/blog.png" class="features" alt="">
                <!-- <img src="../../assets/img/analy.png" class="features" alt=""> -->
                <img src="../../assets/img/share.png" class="features" alt="">
                <img src="../../assets/img/feature.png" class="features" alt="">
            </div>
            <hr style="width: 30%; min-width:300px">
            <div>
                
            </div>
          </div>
      </div>
  </div>

</template>

<script>
export default {
    name: 'About',
}
</script>

<style>
.aboutTem{
    background-color: #f4f4f4;
    height: 100vh;
}
#logoImg{
    width:25%;
    min-width: 250px;
    height: 25%;
}
.features{
    width:10%;
    min-width: 100px;
    height:10%;
    margin:5px;
}
.container-about{
    padding-top: 50px;
    padding-left: 50px;
    padding-right:50px;

    /* padding-left: 15em;
    padding-right: 15em; */

}
.content-section{
    min-width: 400px;
    background-color: white;
    padding:10px
}
</style>