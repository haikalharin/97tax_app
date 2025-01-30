<template>
  <page class="home-page">
    <div class="container">
      <form @submit.prevent="resetPassword()">
        <h1 class="text-center">Reset Password</h1>
        <div class="row mt-4">
          <div class="col">
            <input type="text" class="form-control" placeholder="Username" v-model="username">
          </div>
        </div>
        <div class="row mt-4">
          <div class="col">
            <button class="btn btn-secondary" type="submit">Send Reset Link</button>
          </div>
        </div>
      </form>
      <div class="row" v-if="isSent">
        <div class="col">
          <div class="text-success">
            If the user exists, then a reset link was sent to it.
          </div>
        </div>
      </div>
      <div class="row" v-if="isError">
        <div class="col">
          <div class="text-danger">
            An error has occurred.
          </div>
        </div>
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
        isSent: false,
        isError: false
      }
    },
    methods: {
      resetPassword() {
      let domain =  window.location.host.toLowerCase().replace(/www./g, '');
        AXIOS.post('/auth/reset-email', {username: this.username, domain:domain})
          .then(response => {
            this.isSent = true;
          })
          .catch(e => {
            this.isError = true;
          });

      }
    }
  }
</script>
