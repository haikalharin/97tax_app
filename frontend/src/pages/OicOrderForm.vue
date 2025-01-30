<template>
  <page class="home-page">
    <div class="container">

      <h1 class="text-center">Submit Order</h1>
      <div class="row">
        <div class="col">
          Congrats! We have everything we need to complete your Offer in Compromise application to the IRS. Once complete, we'll mail your completed application packet.
        </div>
      </div>

      <h2>Billing Details</h2>

      <div class="row">
        <input-text
          ref="billingFirstName"
          label="First Name"
          :binding="form.billingFirstName"
          :required="true"
        ></input-text>
        <input-text
          ref="billingLastName"
          label="Last Name"
          :binding="form.billingLastName"
          :required="true"
        ></input-text>
        <input-text
          ref="billingPhone"
          label="Phone Number"
          :binding="form.billingPhone"
          :required="true"
        ></input-text>
        <input-text
          ref="email"
          label="Email Address"
          :binding="form.email"
          :required="true"
        ></input-text>
      </div>

      <div class="row">
        <input-text
          ref="billingAddress1"
          label="Address Line 1"
          :binding="form.billingAddress1"
          :required="true"
        ></input-text>
        <input-text
          ref="billingAddress2"
          label="Address Line 2"
          :binding="form.billingAddress2"
        ></input-text>
        <input-text
          ref="billingCity"
          label="City"
          :binding="form.billingCity"
          :required="true"
        ></input-text>
        <input-text
          ref="billingState"
          label="State"
          :binding="form.billingState"
          :required="true"
        ></input-text>
        <input-text
          ref="billingPostalCode"
          label="Zip"
          :binding="form.billingPostalCode"
          :required="true"
        ></input-text>
      </div>

      <h2>Shipping Details</h2>

      <div class="row">
        <input-text
          ref="shippingAddress1"
          label="Address Line 1"
          :binding="form.shippingAddress1"
          :required="true"
        ></input-text>
        <input-text
          ref="shippingAddress2"
          label="Address Line 2"
          :binding="form.shippingAddress2"
        ></input-text>
        <input-text
          ref="shippingCity"
          label="City"
          :binding="form.shippingCity"
          :required="true"
        ></input-text>
        <input-text
          ref="shippingState"
          label="State"
          :binding="form.shippingState"
          :required="true"
        ></input-text>
        <input-text
          ref="shippingPostalCode"
          label="Zip"
          :binding="form.shippingPostalCode"
          :required="true"
        ></input-text>
      </div>

      <h2>Payment</h2>

      <p>You will be charged a one-time application fee of $397</p>

      <div class="row align-items-end">
        <input-text
          ref="cardNumber"
          label="Card Number"
          :binding="form.cardNumber"
          :required="true"
        ></input-text>
        <div class="col-6">
          <div class="row">
            <div class="col">
            <label>Expiration</label>
            </div>
          </div>
          <div class="row">
            <input-text
              ref="cardExpMonth"
              label="Month"
              :binding="form.cardExpMonth"
              :required="true"
              type="integer"
            ></input-text>
            <input-text
              ref="cardExpYear"
              label="Year"
              :binding="form.cardExpYear"
              :required="true"
              type="integer"
            ></input-text>
          </div>
        </div>
      </div>

      <div class="row">
        <input-text
          ref="cardCvc"
          label="CVC"
          :binding="form.cardCvc"
          :required="true"
        ></input-text>
      </div>

      <div class="row mt-4">
        <div class="col">
          <button class="btn btn-primary" v-on:click="handleContinue" :disabled="disableContinue">Continue</button>
        </div>
      </div>

    </div>

    <form name="acsForm" method="POST" :action="authorizeResult.acsUrl">
      <input type="hidden" name="PaReq" :value="authorizeResult.paReq">
      <input type="hidden" name="TermUrl" :value="authorizeResult.termUrl">
      <input type="hidden" name="MD" :value="authorizeResult.md">
    </form>
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
  import {SiteUtils} from "../scripts/site-common";
  import {AXIOS} from '../scripts/http-common'

  export default {
    data() {
      return {
        exiting: {},
        form: {
          billingFirstName: {},
          billingLastName: {},
          billingPhone: {},
          email: {},

          billingAddress1: {},
          billingAddress2: {},
          billingCity: {},
          billingState: {},
          billingPostalCode: {},

          shippingAddress1: {},
          shippingAddress2: {},
          shippingCity: {},
          shippingState: {},
          shippingPostalCode: {},

          cardNumber: {},
          cardExpMonth: {},
          cardExpYear: {},
          cardCvc: {}
        },

        authorizeResult: {
          acsUrl: '',
          paReq: '',
          termUrl: '',
          md: ''
        },

        disableContinue: false
      }
    },
    methods: {
      handleContinue() {
        if (SiteUtils.validateAll(this.$refs)) {
          this.disableContinue = true;
          let req = SiteUtils.buildServiceRequest({}, this.form);
          req.product = "OfferInCompromise";

          AXIOS.post(`/ecommerce/authorize`, req)
            .then(response => {
              this.authorizeResult = response.data
              console.log(response.data)
              this.$forceUpdate()
              if (response.data.paReq && response.data.acsUrl) {
                setTimeout(function () {
                  document.acsForm.submit();
                }, 1)
              } else {
                this.disableContinue = false;
                this.errorMessages.push({
                  show: true,
                  text: "There was an issue authorizing your card. Please double check your name and billing information."
                });
              }
            })
            .catch(e => {
              console.log(e);
            })

        }
      }
    },
    mounted() {
      let self = this;
      AXIOS.get('/oic')
        .then(response => {
          self.existing = response.data;

          if (response.data) {
            let model = response.data;

            if (model.mailLine1) {
              self.$set(self.form, 'shippingAddress1', {value: model.mailLine1});
              self.$set(self.form, 'shippingAddress2', {value: model.mailLine2});
              self.$set(self.form, 'shippingCity', {value: model.mailCity});
              self.$set(self.form, 'shippingState', {value: model.mailState});
              self.$set(self.form, 'shippingPostalCode', {value: model.mailZip});
            } else {
              self.$set(self.form, 'shippingAddress1', {value: model.addressLine1});
              self.$set(self.form, 'shippingAddress2', {value: model.addressLine2});
              self.$set(self.form, 'shippingCity', {value: model.city});
              self.$set(self.form, 'shippingState', {value: model.state});
              self.$set(self.form, 'shippingPostalCode', {value: model.zip});
            }

            self.$set(self.form, 'billingFirstName', {value: model.firstName});
            self.$set(self.form, 'billingLastName', {value: model.lastName});
            self.$set(self.form, 'billingPhone', {value: model.phoneNumber});
            self.$set(self.form, 'email', {value: model.email});

            self.$set(self.form, 'billingAddress1', {value: model.addressLine1});
            self.$set(self.form, 'billingAddress2', {value: model.addressLine2});
            self.$set(self.form, 'billingCity', {value: model.city});
            self.$set(self.form, 'billingState', {value: model.state});
            self.$set(self.form, 'billingPostalCode', {value: model.zip});

          } else {
            this.$router.push('offer-in-compromise');
          }
        })
        .catch(e => {
          alert("Sorry, we are doing maintenance on the website right now. Please check again later.");
          console.log(e);
        });
    }
  }
</script>
<style scoped>
  .btn-primary {
    background-color: #2896C5;
  }
  .aside-item h2 {
    font-size: 3em;
  }
  .aside-item p {
    font-size: 1.5em;
  }
  .home-page {
    background-image: url(./../assets/banner.jpg);
    background-size: cover;
  }
</style>
