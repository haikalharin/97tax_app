<template>
  <page class="home-page">
    <div class="container">
      <div class="row">
        <div class="col-12 col-md-6 order-md-1">
          <div class="payment-plan-start">
            <h1 class="text-center font-weight-bold text-uppercase" style="color: #2896C5">Do you owe the IRS more than $3000?</h1>
            <p class="text-center font-weight-bold">
              An Offer in Compromise could reduce your debt by up to 90%
            </p>
              <h4 class="text-center" >
                FIND OUT
                <span style="color: #2896C5">NOW</span>
                TO SEE IF YOU QUALIFY
              </h4>
            <input-text
              ref="firstName"
              :binding="form.firstName"
              classes="col-12 mb-2"
              placeholder="First Name"
              :required="true"
            ></input-text>
            <input-text
              ref="lastName"
              :binding="form.lastName"
              classes="col-12 mb-2"
              placeholder="Last Name"
              :required="true"
            ></input-text>
            <input-text
              ref="phone"
              :binding="form.phone"
              classes="col-12 mb-2"
              placeholder="Phone Number"
              :required="true"
              validationType="phone"
            ></input-text>
            <input-text
              ref="email"
              :binding="form.email"
              classes="col-12 mb-2"
              placeholder="Email"
              :required="true"
              validationType="email"
            ></input-text>
            <input-text
              ref="debtOwed"
              type="integer"
              :binding="form.debtOwed"
              classes="col-12 mb-2"
              placeholder="Debt Owed"
              :required="true"
            ></input-text>
            <div class="text-center mt-4">
              <button class="btn btn-primary font-weight-bold" v-on:click="submit" style="background-color: #2896C5" >Find out now</button>
            </div>
          </div>
        </div>
        <div class="col-12 col-md-6 order-md-0 mt-5">
          <div class="aside-item d-flex">
            <div class="mr-2">
              <img width="100px" src="@/assets/easy.png" alt="Easy">
            </div>
            <div>
              <h2>Easy</h2>
              <p>Offer in Compromise ordered online within 10 minutes.</p>
              <div class="clearfix"></div>
            </div>
          </div>
          <div class="aside-item d-flex">
            <div class="mr-2">
              <img width="100px" src="@/assets/settle.png" alt="Settle">
            </div>
            <div>
              <h2>Settle</h2>
              <p>Get back in good standing by reducing your taxes owed.</p>
              <div class="clearfix"></div>
            </div>
          </div>
          <div class="aside-item d-flex">
            <div class="mr-2">
              <img width="100px" src="@/assets/money.png" alt="Affordable">
            </div>
            <div class="mt-2">
              <h2>Affordable</h2>
              <p>No extra fees or hidden costs.</p>
              <div class="clearfix"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="row mt-4">
        <div class="bg-dark text-light">
          <p class="m-4">What is an IRS Offer in Compromise?</p>
          <p class="m-4">The IRS Offer in Compromise may allow you to pay less than you owe to the IRS if you qualify. It's an agreement between you and the IRS to settle your tax liabilities for less than you owe. At 97tax, our process is:</p>
          <p class="m-4">Fast and Easy- We are the only company with an online process where you can place an order for an Offer in Compromise application in less than 10 minutes.</p>
          <p class="m-4">Free to Check- It's always 100% free to check what settlement option you qualify for with the IRS and find out how much you could save.</p>
          <p class="m-4">Huge Savings- Our online system will ask about income and expenses, assets, and the amount you can pay to accurately present you with your IRS settlement options.</p>
          <p class="m-4">Hiring an attorney is expensive and unnecessary, and doing it yourself can cause months of delay and even more frustration. We prepare all the IRS documents necessary to submit your Offer in Compromise. Within 24 hours, we'll mail your completed Offer in Compromise to the mailing address you provide. Just sign the forms, drop it in the mail, and you're done. Easy!</p>
          <p class="m-4">DID YOU KNOW? There are over 30 pages of IRS forms required to prepare an Offer in Compromise, filled with confusing equations and calculations. With our online process, you can complete this in less than ten minutes, and we'll prepare your application for you within 24 hours. We'll mail this application directly to you for your signature, along with a prepaid, USPS Certified Mail envelope so you can send this to the IRS.</p>
        </div>
      </div>
    </div>
    <b-modal ref="notQualifyModal" title="Not Qualified" ok-title="Yes" cancel-title="No">
      <p class="my-4">Based on your amount of debt, you do not qualify for this payment plan.</p>
      <p class="my-4">Would you like an experienced attorney in our network to contact you and discuss your options?</p>
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
  import {AXIOS} from '../scripts/http-common'
  import {SiteUtils} from "../scripts/site-common";

  export default {
    data() {
      return {
        existing: {},
        form: {
          firstName: {value: ''},
          lastName: {value: ''},
          phone: {value: ''},
          email: {value: ''},
          debtOwed: {value: null}
        }
      }
    },
    methods: {
      prefillForm() {
        this.form.firstName.value = this.existing.firstName;
        this.form.lastName.value = this.existing.lastName;
        this.form.phone.value = this.existing.phoneNumber;
        this.form.email.value = this.existing.email;
        this.form.debtOwed.value = this.existing.debtOwed;
        this.$forceUpdate();
      },
      buildServiceRequest() {
        return {
          firstName: this.form.firstName.value,
          lastName: this.form.lastName.value,
          phoneNumber: this.form.phone.value,
          email: this.form.email.value,
          debtOwed: this.form.debtOwed.value
        }
      },
      buildUpdateRequest() {
        this.existing.firstName = this.form.firstName.value;
        this.existing.lastName = this.form.lastName.value;
        this.existing.phoneNumber = this.form.phone.value;
        this.existing.email = this.form.email.value;
        this.existing.debtOwed = this.form.debtOwed.value;
        return this.existing;
      },
      submit() {
        alert('fd')

        let isValid = true;
        for (let name in this.$refs) {
          let component = this.$refs[name];
          if (component.validate) {
            isValid = component.validate() && isValid;
          }
        }
        if (isValid) {
          // if (this.existing) {
          //   AXIOS.put("/oic", this.buildUpdateRequest())
          //     .then(response => {
          //       this.$router.push('oic-certification')
          //     })
          //     .catch(e => {
          //       alert("Sorry, we are doing maintenance on the website right now. Please check again later.");
          //       console.log(e);
          //     });
          // } else {
            AXIOS.post("/oic", this.buildServiceRequest())
              .then(response => {
                this.$router.push('oic-certification')
              })
              .catch(e => {
                alert("Sorry, we are doing maintenance on the website right now. Please check again later.");
                console.log(e);
              });
          // }
        }
      }
    },
    mounted() {
      AXIOS.get("/oic")
        .then(response => {
          this.existing = response.data;
          this.prefillForm();
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
  .aside-item {
    margin: 30px 0;
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
}</style>
