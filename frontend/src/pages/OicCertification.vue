<template>
  <page class="home-page">
    <div class="container mt-0">
<oic-progress></oic-progress>

      <div class="row">
        <p class="font-weight-bold">To create an offer for a settlement on your tax debt, we'll need some basic information about you and your tax situation. As you enter your information below, you can move to the next step. You can always come back to this step by clicking the above icons if you need to change something. No charges are due until we calculate a settlement and determine you qualify.
        </p>
      </div>
      <div class="row">
        <h2 class="blueheader font-weight-bold">Basic Info</h2>
      </div>
      <div class="row">
        <div class="col-6">
          <p class="m-1">First Name</p>
          <input-text
            ref="firstName"
            :binding="form.firstName"
            classes="mb-4"
            placeholder="First Name"
            :required="true"
          ></input-text>
        </div>
          <div class="col-6">
            <p class="m-1">Last Name</p>
            <input-text
              ref="lastName"
              :binding="form.lastName"
              classes="mb-4"
              placeholder="Last Name"
              :required="true"
            ></input-text>
          </div>
        <div class="col-6">
          <p class="m-1">Phone Number</p>
          <input-text
            ref="phone"
            :binding="form.phone"
            classes="mb-4"
            placeholder="Phone Number"
            :required="true"
            validationType="phone"
          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">Email Address</p>
          <input-text
            ref="email"
            :binding="form.email"
            classes="mb-4"
            placeholder="Email Address"
            :required="true"
            validationType="email"
          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">Date of Birth</p>
          <input-text


            classes="mb-4"
            placeholder="mm/dd/yy"
            :required="false"

          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">Social Security Number</p>
          <input-text

            classes="mb-4"
            placeholder="000-00-0000"
            :required="false"

          ></input-text>
        </div>
      </div>

      <div class="row">
        <h2 class="blueheader font-weight-bold">Mailing Address</h2>
      </div>
      <div class="row">
        <div class="col-6">
          <p class="m-1">Address Line 1</p>
          <input-text
            classes="mb-4"
            placeholder="Address Line 1"
            :required="false"
          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">Address Line 2</p>
          <input-text
            classes="mb-4"
            placeholder="Address Line 2"
            :required="false"
          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">City</p>
          <input-text
            classes="mb-4"
            placeholder="City"
            :required="false"
          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">State</p>
          <input-text
            classes="mb-4"
            placeholder="State"
            :required="false"
          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">Zip</p>
          <input-text
            classes="mb-4"
            placeholder="Zip"
            :required="false"
          ></input-text>
        </div>
        <div class="col-6">
          <p class="m-1">County</p>
          <input-text
            classes="mb-4"
            placeholder="County"
            :required="false"
          ></input-text>
        </div>
      </div>



      <div class="row">
        <div class="col-12">
          <input-checkbox
            :required="false"
            classes="col"
            label="Are you self-employed?"
          ></input-checkbox>
        </div>
        <div class="col-12 mt-4">
          <p>To settle your tax debt for less than you owe to the IRS, the following statements must be true:</p>
        </div>
      </div>
      <div class="row">
        <input-checkbox
          ref="bankruptcyCheckbox"
          :required="true"
          classes="col"
          :binding="form.bankruptcy"
          label="I am not currently going through bankruptcy proceedings."
        ></input-checkbox>
      </div>
      <div class="row">
        <input-checkbox
          ref="returnsFiledCheckbox"
          :required="true"
          classes="col"
          :binding="form.returnsFiled"
          label="I have all my tax returns filed."
        ></input-checkbox>
      </div>
      <div class="row">

        <input-checkbox
          ref="before2008Checkbox"
          :required="true"
          classes="col"
          :binding="form.before2008"
          label="None of the money I owe to the IRS is from tax years before 2008."
        ></input-checkbox>

      </div>
      <div class="row">

        <input-checkbox
          ref="atLeast5kCheckbox"
          :required="true"
          classes="col"
          :binding="form.atLeast5k"
          label="I currently owe at least $5,000 to the IRS."
        ></input-checkbox>

      </div>
      <div class="row">

        <input-checkbox
          ref="unableToPay"
          :required="true"
          classes="col"
          :binding="form.unableToPay"
          label="I am unable to pay the amount that I owe through a payment plan over 72 months."
        ></input-checkbox>

      </div>

      <div class="row">
        <div class="col">
          <p>
            By continuing, I certify that the above statements are true.
          </p>
        </div>
      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-primary" v-on:click="handleSubmit">Continue</button>
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
  import Progress from "../components/oic/Progress";
  export default {
    components: {Progress},
    data() {
      return {
        form: {
          bankruptcy: {},
          returnsFiled: {},
          before2008: {},
          atLeast5k: {},
          unableToPay: {}
        }
      }
    },
    methods: {
      validate() {
        let isValid = true;
        for (let name in this.$refs) {
          let component = this.$refs[name];
          if (component.validate) {
            isValid = component.validate() && isValid;
          }
        }
        return isValid;
      },

      handleSubmit() {
        if(this.validate()) {
          this.$router.push("oic-assets");
        }
      }
    }
  }
</script>
<style scoped>
  .btn-primary {
    background-color: #2896C5;
  }
  .aside-item p {
    font-size: 1.5em;
  }
  h2.blueheader {
    color: #2896C5;
  }
  .home-page {
    background-image: url(./../assets/banner.jpg);
    background-size: cover;
  }
</style>
