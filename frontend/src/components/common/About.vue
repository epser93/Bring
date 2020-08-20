<template>
  <div class="aboutTem">
      <div class="container">
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
    width:50%;
    height: 50%;
}
.features{
    width:25%;
    height:25%;
    margin:5px;
}
.container{
    padding:6em
}
.content-section{
    background-color: white;
}
</style>