<template>
  <page class="home-page" :navType="'admin-login'">
    <div class="container">
      <form @submit.prevent="login()">
        <h1 class="text-center mt-4">Login</h1>
        <div class="row mt-4">
          <div class="col">
            <input type="text" class="form-control" placeholder="Username" v-model="username">
          </div>
        </div>
        <div class="row mt-4">
          <div class="col">
            <input type="password" class="form-control" placeholder="Password" v-model="password">
          </div>
        </div>
        <div class="row mt-4">
          <div class="col">
            <button class="btn btn-secondary" type="submit">Login</button>
            <div class="float-right">
              <a href="/password-reset">Reset Password</a>
            </div>
          </div>
        </div>
      </form>
      <div class="padding-for-footer">
      </div>
    </div>
  </page>
</template>

<!-- sharea sale tag -->
<script>
  var shareasaleSSCID=shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e,a,r,s,t){
      if(e&&a)
            {var o,n=s?"; path="+s:"",i=t?"; domain="+t:"",l="";r&&((o=new Date).setTime(o.getTime()+r),l="; expires="+o.toUTCString()),
        document.cookie=e+"="+a+l+n+i
    }}
        function shareasaleGetParameterByName(e,a)
        {a||(a=window.location.href),e=e.replace(/[\[\]]/g,"\\$&");
        var r=new RegExp("[?&]"+e+"(=([^&#]*)|&|#|$)").exec(a);
return r?r[2]?decodeURIComponent(r[2].replace(/\+/g," ")):"":null}
shareasaleSSCID&&shareasaleSetCookie("shareasaleSSCID",
shareasaleSSCID,94670778e4,"/");
</script>

<script>
  import {AXIOS} from '../scripts/http-common'

  export default {
    data() {
      return {
        username: '',
        password: ''
      }
    },
    methods: {
      login() {
        AXIOS.post('/auth/login', {username: this.username, password: this.password})
          .then(response => {
            console.log(response)
            if (response.data.userType === "admin" || response.data.userType === "StandardUser" ) {
              this.$store.dispatch('setUser', response.data)
              localStorage.setItem("loggedUser",JSON.stringify(response.data));
              this.$router.push('/admin/orders');
            } else {
              this.$router.push('/mailroom');
            }
          })
          .catch(e => {
            alert("Invalid username or password.");
            console.log(e);
          });
        return false;
      }
    }
  }
</script>
<style scoped>

  .aside-item h2 {
    font-size: 3em;
  }
  .aside-item p {
    font-size: 1.5em;
  }
  .home-page {
    background-size: cover;
  }
  .padding-for-footer{
     padding-bottom: 11rem !important;
  }
</style>
