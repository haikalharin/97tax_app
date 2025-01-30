<template>
  <page>
    <div class="top-hero py-5">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <h1 class="py-2 text-center font-weight-bold">Who is 97tax?</h1>
            <h5 class="pb-2 font-weight-bold text-center">We've helped thousands of clients improve their tax situation</h5>
          </div>
        </div>
      </div>
    </div>
    <div class="content container my-4 py-5">
      <div class="row mb-2">
        <div class="col-12 col-sm-6">
          <p class="main-black-text"><b>97tax</b> is a tax preparation company in Brandon, FL. We believe affordable tax help should be available to everyone. Since 2017, we’ve helped thousands of clients improve their tax situation.</p>
          <p class="main-black-text">
            We are proud to be active members of <b>The National Society of Accountants</b> and <b>The National Society of Tax Professionals</b>. We are committed to providing ethical and trustworthy tax preparation services.
          </p>
        </div>
        <div class="col-12 col-sm-6  d-flex align-items-center">
          <div class="row align-items-center">
            <div class="col-12 col-sm-4 d-flex align-items-center justify-content-center justify-content-sm-start">
              <a href="https://www.nsacct.org/home" target="_blank">
                <img src="@/assets/NSA.svg" width="100%" class="mb-4 mb-sm-0">
              </a>
            </div>
            <div class="col-12 col-sm-8">
              <p class="main-black-text small-text">The National Society of Accountants helps connect people with tax and accounting professionals that are bound by their code of ethics.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-sm-6">
          <p class="main-black-text">Our website is fully encrypted and secure, and all private information entered is never shared or sold to a third party.</p>
          <p class="main-black-text">We are always ready to provide professional and reliable help setting up <router-link class="blue-text" to="/payment-plan">IRS PAYMENT PLANS</router-link>, <router-link class="blue-text" to="/state-payment-plan">STATE PAYMENT PLANS</router-link> and <router-link class="blue-text" to="/penalty-waiver">IRS PENALTY WAIVERS</router-link>.</p>
        </div>
        <div class="col-12 col-sm-6">
          <div class="row">
            <div class="col-12 col-sm-4 text-center">
              <a href="http://www.nstp.org/" target="_blank">
                <img class="mb-4" src="@/assets/nstp.svg" width="70%">
              </a>
            </div>
            <div class="col-12 col-sm-8">
              <p class="main-black-text small-text">The National Society of Tax Professionals helps its members to provide the most professional tax preparation services for clients.</p>
            </div>
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
    import {gmapApi} from 'vue2-google-maps'
    import {AXIOS} from '../scripts/http-common'

    export default {

        data() {
            return {

            }
        },
        methods: {

        }
    }
</script>

<style scoped>
  * {
    font-family: 'Raleway', sans-serif;
  }

  p {
    line-height: 2em;
  }

  .top-hero {
    background-color: #0094be;
  }

  .top-hero h1,
  .top-hero h5 {
    color: #fff;
  }

  .main-black-text {
    color: #424242;
  }

  .blue-text {
    color: #0094be;
    font-weight: 600;
  }

  .img-container {
    flex: 0 0 180px;
  }

  .small-text {
    font-size: 13px;
    line-height: 1.7em;
    margin-bottom: 0;
  }
</style>
