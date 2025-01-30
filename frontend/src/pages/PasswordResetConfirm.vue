<template>
  <page class="home-page">
    <div class="container">
      <form @submit.prevent="doReset()" v-if="showForm">
        <h1 class="text-center">Reset Password</h1>
        <div class="row mt-4">
          <div class="col">
            <input type="password" class="form-control" placeholder="Password" v-model="password">
          </div>
        </div>
        <div class="row mt-4">
          <div class="col">
            <input type="password" class="form-control" placeholder="Confirm Password" v-model="confirmPassword">
          </div>
        </div>
        <div class="row mt-4" v-if="notMatching">
          <div class="col">
            <div class="text-warning">Password is not matching.</div>
          </div>
        </div>
        <div class="row mt-4">
          <div class="col">
            <button class="btn btn-secondary" type="submit">Send Reset Link</button>
          </div>
        </div>
      </form>
      <div class="row" v-if="isSuccess">
        <div class="col">
          <div class="text-success">Password has been reset! You may now <a href="/login">Login</a>.</div>
        </div>
      </div>
      <div class="row" v-if="isError">
        <div class="col">
          <div class="text-danger">Password Reset has already been used or is invalid.</div>
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
        password: '',
        confirmPassword: '',
        showForm: false,
        isSuccess: false,
        isError: false,
        notMatching: false,
        uuid:''
      }
    },
    mounted() {
      this.getResetRequest();
    },
    methods: {
      getResetRequest() {
        let requestId = this.$route.params.id;
        AXIOS.get(`/auth/reset-request/${requestId}`)
          .then(response => {
            let resetRequest = response.data;
            this.username = resetRequest.username;
            this.uuid = resetRequest.uuid;
            this.showForm = true;
          })
          .catch(e => {
            this.isError = true;
          });
      },
      doReset() {
        if (this.password !== this.confirmPassword) {
          this.notMatching = true;
        } else {
          AXIOS.post("/auth/reset", {
              uuid: this.uuid,
              username: this.username,
              password: this.password
            })
            .then(response => {
              this.isSuccess = true;
            })
            .catch(e => {
              this.isError = true;
            })
        }
      }
    }
  }

</script>
