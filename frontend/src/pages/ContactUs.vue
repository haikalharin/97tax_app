<template>
  <page>
    <div class="top-hero py-5">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <h1 class="py-2 text-center font-weight-bold">Contact 97tax</h1>
            <h5 class="pb-2 font-weight-bold text-center">
              Please fill out this quick form or give us a call, we'd love to chat and see how we can help.
            </h5>
          </div>
        </div>
      </div>
    </div>
    <div class="content container my-4 py-5">
      <div class="row">
        <div class="col-12 col-sm-7">
          <div class="contact-us-form px-5 py-4">
            <div class="row">
              <div class="col-12">
                <h2 class="mb-3 text-center font-weight-bold text-uppercase main-black-text">Contact Us</h2>
              </div>
            </div>
            <div class="row mb-2">
              <div class="col-12 col-sm-6">
                <label class="form-control-label light-gray">Name</label>
                <input
                  class="form-control"
                  placeholder="Name"
                  v-model="emailDetails.name"
                >
              </div>
              <div class="col-12 col-sm-6">
                <label class="form-control-label light-gray">Email</label>
                <input
                  class="form-control"
                  placeholder="Email"
                  v-model="emailDetails.email"
                >
              </div>
            </div>
            <div class="row mb-2">
              <div class="col-12">
                <label class="form-control-label light-gray">Subject</label>
                <input
                  class="form-control"
                  placeholder="Subject"
                  v-model="emailDetails.subject"
                >
              </div>
            </div>
            <div class="row mb-4">
              <div class="col-12">
                <label class="form-control-label light-gray">Message</label>
                <textarea
                  class="form-control"
                  rows="5"
                  placeholder="Message"
                  v-model="emailDetails.message"
                ></textarea>
              </div>
            </div>
            <div class="row">
              <div class="col-12 text-center">
                <button
                  class="btn btn-green font-weight-bold text-uppercase mx-auto w-50"
                  @click="sendEmail()"
                  :disabled="isProcessing"
                >
                  Get in touch with us
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="col-12 col-sm-5">
          <h3 class="my-4 text-center font-weight-bold main-black-text">Have a Question?</h3>
          <p class="mb-4 text-center light-gray">Please let us know if you have a question want to leave a comment.</p>
          <div class="mb-4">
            <div class="info-label mb-1">
              <font-awesome-icon icon="envelope" />
              <span class="ml-2 text-uppercase font-weight-bold">Email:</span>
            </div>
            <div class="info-value">
              <p class="main-black-text font-weight-bold">support@97tax.com</p>
            </div>
          </div>
          <div class="pt-2 mb-4">
            <div class="info-label mb-1">
              <font-awesome-icon icon="phone" />
              <span class="ml-2 text-uppercase font-weight-bold">Phone:</span>
            </div>
            <div class="info-value">
              <p class="main-black-text font-weight-bold">813-853-0140</p>
            </div>
          </div>
          <div class="pt-2 mb-4">
            <div class="info-label mb-1">
              <font-awesome-icon icon="map-marker-alt" />
              <span class="ml-2 text-uppercase font-weight-bold">Address:</span>
            </div>
            <div class="info-value">
              <p class="main-black-text font-weight-bold">1044 E. Brandon Blvd, Brandon, FL 33511</p>
              <h5 class="light-gray">
                Mon-Fri: 9:00am - 9:00pm EST
              </h5>
              <h5 class="light-gray">
                Sat-Sun: Closed
              </h5>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="game-map">
      <GmapMap
        :center="{lat:27.9397443, lng:-82.268012}"
        :zoom="14"
        map-type-id="terrain"
        style="width: 100%; height: 500px"
        :options="{
                  map_style: 'custom',
                  map_type: 'roadmap',
                  overlay_color: '#2896c5',
                  overlay_color_hsl: {'hue':198,'sat':66,'lum':46}
                  }"
      >
        <GmapMarker
          :position="google && new google.maps.LatLng(27.9397443, -82.268012)"
        />
      </GmapMap>
    </div>

    <b-modal ref="emailSentConfirm" title="Email Sent" ok-only>
      <p>
        Thank you for your message! It has been sent.
      </p>
    </b-modal>
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
    computed: {
      google: gmapApi
    },
    data() {
      return {
        isProcessing: false,
        emailDetails: {
          name: '',
          email: '',
          subject: '',
          message: ''
        }
      }
    },
    methods: {
      sendEmail() {
        this.isProcessing = true;
        AXIOS.post(`/email/contact/send`, this.emailDetails)
          .then(response => {
            this.isProcessing = false;
            this.emailDetails.name = "";
            this.emailDetails.email = "";
            this.emailDetails.subject = "";
            this.emailDetails.message = "";
            this.$refs.emailSentConfirm.show();
          })
          .catch(e => {
            this.isProcessing = false;
            window.alert("Unable to send!");
          })
      }
    }
  }
</script>

<style scoped>

  @import url('https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i');
  ::placeholder { /* Chrome, Firefox, Opera, Safari 10.1+ */
    color: #AAA9A9;
    opacity: 1; /* Firefox */
    font-style: italic;
    font-weight: 300;
  }

  * {
    font-family: 'Raleway', sans-serif;
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

  .contact-us-form {
    background-color: #f5f5f5;
  }

  .form-control-label {
    font-size: 12px;
    margin-bottom: 0;
  }

  .light-gray {
    color: #a5a5a5;
  }

  .info-label {
    color: #0094be;
    display: flex;
    align-items: center;
  }

  .info-value {
    margin-left: 24px;
  }

  @media (max-width: 575px) {
    .contact-us-form {
      padding-left: 1rem !important;
      padding-right: 1rem !important;
    }
  }
</style>
