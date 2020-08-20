<template>
  <div id="index">
    <!-- <a href="http://localhost:8081/postqna/recentlypost">시작하기2</a> -->
    <div class="css-typing">
      <p>
        'Bring'
      </p>
      <p>
        for
      </p>
      <p>
        Developers.
      </p>
    </div>

    <!-- 이동 버튼 부분 -->
    <router-link :to="{ name : 'RecentlyPost' }"><div class="circle"><h4>start</h4></div></router-link>
  </div>
</template>

<script>

export default {
  name: "Index",
  created () {
    this.$cookies.set('mode', 'Blog')
  },
  data() {
    return {
      
    }
  },
  methods: {
  }

}
</script>

<style scoped>
.css-typing {
    text-align:-webkit-center;
    margin-top: 50px;
}

.css-typing p { 
  border-right: .10em solid #99c9c2;
  font-family: "Courier";
  font-size: 100px;
  white-space: nowrap;
  overflow: hidden;
}
.css-typing p:nth-child(1) {
  width: 4em;
  -webkit-animation: type 0.8s steps(40, end);
  animation: type 0.8s steps(40, end);
  -webkit-animation-fill-mode: forwards;
  animation-fill-mode: forwards;
}

.css-typing p:nth-child(2) {
  width: 1.8em;
  opacity: 0;
  -webkit-animation: type2 0.8s steps(40, end);
  animation: type2 0.8s steps(40, end);
  -webkit-animation-delay: 1s;
  animation-delay: 1s;
  -webkit-animation-fill-mode: forwards;
  animation-fill-mode: forwards;
}

.css-typing p:nth-child(3) {
  width: 6.8em;
  opacity: 0;
  -webkit-animation: type3 3s steps(20, end), blink .2s step-end infinite alternate;
  animation: type3 3s steps(20, end), blink .2s step-end infinite alternate;
  -webkit-animation-delay: 2s;
  animation-delay: 2s;
  -webkit-animation-fill-mode: forwards;
  animation-fill-mode: forwards;
}

@keyframes type {
  0% {
    width: 0;
  }
  99.9% {
    border-right: .15em solid black;
  }
  100% {
    border: none;
  }
}

@-webkit-keyframes type {
  0% {
    width: 0;
  }
  99.9% {
    border-right: .15em solid black;
  }
  100% {
    border: none;
  }
}

@keyframes type2 {
  0% {
    width: 0;
  }
  1% {
    opacity: 1;
  }
  99.9% {
    border-right: .15em solid black;
  }
  100% {
    opacity: 1;
    border: none;
  }
}

@-webkit-keyframes type2 {
  0% {
    width: 0;
  }
  1% {
    opacity: 1;
  }
  99.9% {
    border-right: .15em solid black;
  }
  100% {
    opacity: 1;
    border: none;
  }
}

@keyframes type3 {
  0% {
    width: 0;
  }
  1% {
    opacity: 1;
  }
  100% {
    opacity: 1;
  }
}

@-webkit-keyframes type3 {
  0% {
    width: 0;
  }
  1% {
    opacity: 1;
  }
  100% {
    opacity: 1;
  }
}

@keyframes blink {
  50% {
    border-color: transparent;
  }
}
@-webkit-keyframes blink {
  50% {
    border-color: tranparent;
  }
}

.circle {
  width: 90px;
  height: 90px;
  background: transparent;
  border: 3px solid rgb(94, 94, 94);
  border-radius: 50%;
  text-align: center;
  margin: 20px auto;
}

.circle {
  color:rgb(94, 94, 94);
  transform: scale(1);
  -webkit-transform: scale(1);
  -moz-transform: scale(1);
  -ms-transform: scale(1);
  -o-transform: scale(1);
  transition: all 0.3s ease-in-out;
  text-decoration: none;
}

.circle:visited {
  color:rgb(94, 94, 94);
}

.circle:hover {
  transform: scale(1.1);
  text-decoration: none;
  -webkit-transform: scale(1.1);
  -moz-transform: scale(1.1);
  -ms-transform: scale(1.1);
  -o-transform: scale(1.1);
}

a:hover {
  text-decoration: none;
}

h4 {
  margin-top: 25px;
}

</style>