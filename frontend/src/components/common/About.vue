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
            <div class="team">
                <div class="member">
                    <img src="../../assets/img/myteams/주환.png" alt="장주환">
                    <div class="intro">
                        <h5><p>장주환</p></h5>
                        <p>Team Leader</p>
                        <p>juhwan4129@naver.com</p>
                        <p><a href="https://github.com/zjohn99" target="_blank"><i class="fab fa-github"></i>  Github</a></p>
                    </div>
                </div>
                <div class="member">
                    <img src="../../assets/img/myteams/은석.png" alt="송은석">
                    <div class="intro">
                        <h5><p>송은석</p></h5>
                        <p>FrontEnd</p>
                        <p>epseriper@gmail.com</p>
                        <p><a href="https://github.com/epser93" target="_blank"><i class="fab fa-github"></i>  Github</a></p>
                    </div>
                </div>
                <div class="member">
                    <img src="../../assets/img/myteams/정연.png" alt="한정연">
                    <div class="intro">
                        <h5><p>한정연</p></h5>
                        <p>FrontEnd</p>
                        <p>51sarang@hanmail.net</p>
                        <p><a href="https://github.com/hanjeongyoun9" target="_blank"><i class="fab fa-github"></i>  Github</a></p>
                    </div>
                </div>
                <div class="member">
                    <img src="../../assets/img/myteams/도현.jpg" alt="권도현">
                    <div class="intro">
                        <h5><p>권도현</p></h5>
                        <p>BackEnd</p>
                        <p>lawliet0521@gmail.com</p>
                        <p><a href="https://github.com/hanjeongyoun9" target="_blank"><i class="fab fa-github"></i>  Github</a></p>
                    </div>
                </div>
                <div class="member">
                    <img src="../../assets/img/myteams/수빈.png" alt="김수빈">
                    <div class="intro">
                        <h5><p>김수빈</p></h5>
                        <p>FrontEnd</p>
                        <p>ksb8320@gmail.com</p>
                        <p><a href="https://github.com/ksb8320" target="_blank"><i class="fab fa-github"></i>  Github</a></p>
                    </div>
                </div>
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

<style scoped>

hr {
    margin-bottom : 40px;
    margin-top : 40px;
}
.container-about {
    min-height: 100vh;
    background : white;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.content-section {
    padding-top : 30px;
    padding-bottom : 30px;
}
.team {
    display: flex;
    justify-content: space-around;
    min-width : 210px
}
.member {
    padding: 30px;
    border: 3px solid rgb(86, 219, 201);
    background : white;
    border-radius: 10px;
    max-width: 1000px;
}

.member img {
    width: 80px;
    height: 80px;
    background: black;
    border-radius: 50%;
    border: 1px solid gray
}

.intro p {
    margin-top : 10px;
}

.intro i {
    margin-top : 10px
}

a {
    text-decoration: none;
    color : black
}

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
/* .container-about{
    padding-top: 50px;
    padding-left: 50px;
    padding-right:50px;
} */
.content-section{
    min-width: 400px;
    background-color: white;
    padding : 30px 10px;
}
</style>