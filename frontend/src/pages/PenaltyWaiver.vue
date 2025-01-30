<template>
  <page :navType="'penalty-waiver'">
    <div id="top-hero" class="top-hero pt-5 pb-4">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <h1 class="font-weight-bold text-uppercase text-center">
              APPLY FOR IRS FIRST TIME<br />
              PENALTY ABATEMENT
            </h1>
            <h5 class="font-weight-bold text-center">
              For a one-time fee, get a professionally done First Time
              Penalty Abatement Request letter.
            </h5>
          </div>
        </div>
      </div>
    </div>

    <div class="banner-root">
      <div class="container">
        <div class="banner pt-60 pb-60">
          <div class="banner-left">
            <div class="desc-wrapper">
              <div class="info-row d-flex my-4 pr-4">
                <div>
                  <img src="@/assets/v2/moneyback1.png" alt="" width="70" height="70" />
                </div>
                <div class="info-text ml-4">
                  <h5 class="font-weight-bold form-desc-title">Money Back Guarantee</h5>
                  <span class="form-desc-subtitle">
                    We only get paid if your penalty is waived.
                    <a href="/penalty-waiver/see-guarantee-details" target="_blank">See more details.</a>
                  </span>
                </div>
              </div>
              <div class="info-row d-flex mb-4 pr-4">
                <div>
                  <img src="@/assets/refund_usd.svg" alt="" width="70" height="70" />
                </div>
                <div class="info-text ml-4">
                  <h5 class="font-weight-bold form-desc-title">Already Paid?</h5>
                  <span class="form-desc-subtitle">
                    No problem. The IRS will write you a refund check for the penalty.
                  </span>
                </div>
              </div>
              <div class="info-row d-flex mb-4 pr-4">
                <div>
                  <img src="@/assets/money.svg" alt="" width="70" height="70" />
                </div>
                <div class="info-text ml-4">
                  <h5 class="font-weight-bold form-desc-title">Largest Possible Savings</h5>
                  <span class="form-desc-subtitle">
                    The penalty to be waived is always the largest from the last 3 years.
                  </span>
                </div>
              </div>
              <div class="info-row d-flex mb-4 pr-4">
                <div>
                  <img src="@/assets/piggybank.svg" alt="" width="70" height="70" />
                </div>
                <div class="info-text ml-4">
                  <h5 class="font-weight-bold form-desc-title">Affordable</h5>
                  <span class="form-desc-subtitle">
                    No extra fees or hidden costs.
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div class="banner-right mt-3">
            <div class="find-out-input__panel px-5 pt-4 pb-4">
              <h3 class="font-weight-bold text-uppercase text-center mb-4">
                APPLY <SPAN style="color: #0098c3">INSTANTLY</SPAN> FOR PENALTY ABATEMENT
              </h3>
              <div class="row mb-2">
                <div class="col-6">
                  <label class="form-control-label light-gray"
                    >First Name</label
                  >
                  <input
                    :class="[
                      'form-control',
                      errors.firstName ? 'is-invalid' : '',
                    ]"
                    type="text"
                    v-model="penaltyData.firstName"
                    placeholder="First Name"
                    v-on:keypress="isLetter($event)"
                  />
                </div>
                <div class="col-6">
                  <label class="form-control-label light-gray">Last Name</label>
                  <input
                    :class="[
                      'form-control',
                      errors.lastName ? 'is-invalid' : '',
                    ]"
                    type="text"
                    v-model="penaltyData.lastName"
                    placeholder="Last Name"
                    v-on:keypress="isLetter($event)"
                  />
                </div>
              </div>
              <div class="row mb-2">
                <div class="col-6">
                  <label class="form-control-label light-gray"
                    >Phone Number</label
                  >
                  <input
                    :class="['form-control', errors.phone ? 'is-invalid' : '']"
                    type="text"
                    v-model="penaltyData.phone"
                    placeholder="Phone Number"
                    v-mask="'###-###-####'"
                  />
                </div>
                <div class="col-6">
                  <label class="form-control-label light-gray">Email</label>
                  <input
                    :class="['form-control', errors.email ? 'is-invalid' : '']"
                    @keydown.space="(event) => event.preventDefault()"
                    type="text"
                    v-model="penaltyData.email"
                    placeholder="Email Address"
                  />
                </div>
              </div>

              <div
                v-if="hasErrors"
                class="d-flex justify-content-center mb-2 text-danger flex-column">
                <div v-for="(error, i) in errors" :key="i">
                  <div v-if="error" class="mb-2">
                    {{ error }}
                  </div>
                </div>
              </div>
              <div class="d-flex justify-content-center mb-4 mt-4">
                <button
                  class="btn btn-green font-weight-bold text-uppercase md-width"
                  v-on:click="onApply"
                  :disabled="isSubmitting"
                >
                  <span v-if="isSubmitting">Applying...</span>
                  <span v-if="!isSubmitting">Apply now</span>
                </button>
              </div>

              <div class="row">
                <div class="col-12 col-md-10 offset-0 offset-md-1">
                  <div class="d-flex justify-content-center align-items-center">
                    <font-awesome-icon
                      class="mr-2"
                      style="color: #0094be"
                      icon="lock"/>
                    <span style="font-size: 12px">
                      Your personal information is secured through SSL Encryption
                    </span>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>

    <div class="py-4">
      <div class="container">
        <div class="row mb-5">
          <div class="col-sm-8 offset-sm-2 col-12 offset-0">
            <h1 class="mb-3 text-center font-weight-bold">
              What is the IRS First Time Penalty Abatement?
            </h1>
            <h4 class="light-gray text-center font-weight-bold">
              FTA is an IRS administrative procedure that allows you to waive your single
              largest penalty from a single tax year.
            </h4>
          </div>
        </div>

        <div class="what-section row">
          <div class="col-sm-12 col-md-6">
            <h3 class="font-weight-bold">Understanding Penalty Abatement</h3>
            <p>
              Everyone is entitled to one mistake, so the IRS offers the
              option of a First Time Penalty abatement administrative waiver
              (IRM 20.1.1.3.3.2.1). 97tax follows all IRS published
              requirements when creating your penalty waiver request.
            </p>

            <p>
              There are requirements you must meet:
              <ul>
                <li>Not have waived any penalties in the last 3 years</li>
                <li>The largest penalty from a single tax year</li>
                <li>Applies to the late filling penalty, late payment fee, and failure to deposit penalties.</li>
              </ul>
            </p>
          </div>

          <div class="col-sm-12 col-md-6">
            <h3 class="font-weight-bold">We Work Fast for you</h3>
            <p>
              After you place an order, we’ll write you waiver letter
              and mail this to you within 24 hours, along with instructions
              and a prepaid mailing envelope to the IRS. We’ll even include a
              form to request a refund if you already paid the IRS. Just sign
              the form, mail it, and done!
            </p>
          </div>
        </div>

        <div class="hidden-xs-only my-5 d-flex justify-content-center">
          <a
            class="btn btn-green font-weight-bold text-uppercase px-5"
            href="#top-hero"
            style="text-decoration: none">
            CLICK HERE TO GET YOUR APPLICATION STARTED NOW
          </a>
        </div>
      </div>
    </div>

    <div class="second-part">
      <div class="container">
        <div class="row py-3">
          <div class="col-sm-10 offset-sm-1 col-12 offset-0">
            <h1 class="mb-4 text-center font-weight-bold">Did you know?</h1>
            <h4 class="text-center">
              Most IRS penalties are because of late tax returns or having a tax
              balance. Millions of dollars worth of penalties could be waived,
              but aren’t. 97tax will write you a personalized waiver letter to
              waive your largest penalty. Even if you already paid it, the IRS
              is required to refund the waived penalty amount.
            </h4>
          </div>
        </div>
      </div>
    </div>

    <div class="container pt-5 pb-5">
      <h1 class="text-center font-weight-bold my-3">Frequently Asked Questions</h1>
      <h4 class="light-gray text-center mb-5">We have lots of answers</h4>
      <div class="my-3" id="embedded_faq">
        <penalty-Waiver-Faq></penalty-Waiver-Faq>
      </div>
    </div>

    <order-tracker></order-tracker>

    <our-clients></our-clients>

  </page>
</template>

<script>
import $ from "jquery";
import "bootstrap";
import { AXIOS } from "../scripts/http-common";
import { SiteUtils } from "../scripts/site-common";

export default {
  components: {},
  name: "PenaltyWaiver",

  data() {
    return {
      isSubmitting: false,
      penaltyData: {
        firstName: "",
        lastName: "",
        phone: "",
        email: "",
      },
      errors: {
        firstName: false,
        lastName: false,
        phone: false,
        email: false,
      },
      hasErrors: false,
    };
  },
  methods: {
    isLetter(e) {
      if (e.keyCode === 32) return true;
      let char = String.fromCharCode(e.keyCode);
      if (/^[A-Za-z]+$/.test(char)) return true;
      else e.preventDefault();
    },

    onApply() {
      this.isSubmitting = true;
      let required = ["firstName", "lastName", "phone", "email"];
      this.hasErrors = false;

      required.forEach((p) => {
        if (
          !this.penaltyData[p] ||
          this.penaltyData[p].trim().length === 0
        ) {
          this.errors[p] = true;
          this.hasErrors = true;
        } else {
          this.errors[p] = false;
        }
      });

      let checkFirstName = SiteUtils.validateFirstName(
        this.penaltyData.firstName
      );
      if (checkFirstName) {
        this.errors.firstName = checkFirstName;
        this.hasErrors = true;
      }

      let checkLastName = SiteUtils.validateLastName(
        this.penaltyData.lastName
      );
      if (checkLastName) {
        this.errors.lastName = checkLastName;
        this.hasErrors = true;
      }

      let checkEmailAddressErrorMsg = SiteUtils.validateEmail(
        this.penaltyData.email
      );
      if (checkEmailAddressErrorMsg) {
        this.errors.email = checkEmailAddressErrorMsg;
        this.hasErrors = true;
      }

      let checkPhoneNumberErrorMsg = SiteUtils.validatePhone(
        this.penaltyData.phone
      );
      if (
        screen.width >= 576 &&
        checkPhoneNumberErrorMsg
      ) {
        this.errors.phone = checkPhoneNumberErrorMsg;
        this.hasErrors = true;
      }

      if (this.hasErrors) {
        this.isSubmitting = false;
        return;
      }

      if (screen.width >= 576) {
        this.penaltyData.phone = SiteUtils.standardizePhone(
          this.penaltyData.phone
        );
      }

      const penaltyObj = {
        firstName: this.penaltyData.firstName,
        lastName: this.penaltyData.lastName,
        email: this.penaltyData.email,
        billingPhone: this.penaltyData.phone,
        billingAddress1: "",
        billingAddress2: "",
        billingCity: "",
        billingState: "",
        billingPostalCode: "",
        shippingAddress1: "",
        shippingAddress2: "",
        shippingCity: "",
        shippingState: "",
        shippingPostalCode: "",
        ssn: "",
        married: false,
        largestPenalty: "",
        penaltyAmountWaived: "",
        isPenaltyPaid: 0,
        isLast3Filed: false,
        isLast3IrsWavied: false,
        isDecreaseIncomeTax: false,
        partnerCode: "",
      };

      console.log(penaltyObj);
      AXIOS.post("/penalty", penaltyObj)
        .then((result) => {
          console.log(result);
          this.$router.push("/penalty-waiver-checkout");
        })
        .catch((err) => {
          console.log("Something went wrong: ", err)
        })
        .finally(() => {
          this.isSubmitting = false;
        });
    },

    onSeeMoreDetails() {
      this.$router.push("/penalty-waiver/see-guarantee-details");
    },
  },
  mounted() {
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i");
::placeholder {
  /* Chrome, Firefox, Opera, Safari 10.1+ */
  color: #aaa9a9;
  opacity: 1; /* Firefox */
  font-weight: 300;
}

* {
  font-family: "Raleway", sans-serif;
}

.top-hero {
  background-color: #0094be;
  color: #fff;
}

.find-out-input__panel {
  background-color: #f5f5f5;
}

.form-control-label {
  font-size: 12px;
  margin-bottom: 0;
}

.md-width {
  width: 300px;
}

.banner-root {
  background-color: #f4fbf9;
}
.pt-60 {
  padding-top: 60px;
}
.pb-60 {
  padding-bottom: 60px;
}
.banner-text {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 60px;
  line-height: 74px;
  color: #252020;
}
.green-underline {
  text-decoration: underline;
  text-decoration-color: #2ac44c;
}
.banner {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
}
.banner-left {
  width: 460px;
  margin: 0 auto;
  padding-bottom: 20px;
}
.banner-right {
  width: 100%;
  max-width: 650px;
  margin: 0 auto;
  position: relative;
}
.banner-image {
  width: 100%;
}
.guarantee {
  position: absolute;
  right: 40px;
  top: 0px;
  transform: translateX(50%) translateY(-50%);
}

.guarantee img {
  width: 100%;
}

.guarantee-inner {
  width: 100%;
  position: relative;
}

.see-more {
  cursor: pointer;
  position: absolute;
  font-family: "Inter";
  font-style: normal;
  font-weight: 400;
  font-size: 10px;
  line-height: 12px;
  text-align: center;

  color: #ffffff;

  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  transform: rotate(-15.76deg);
  left: 88px;
  bottom: 17px;
}

.banner-subtext {
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 24px;

  color: #817e7e;
  padding: 15px 0px 15px 0px;
}

@media only screen and (max-width: 862px) {
  .pt-60 {
    padding-top: 45px;
  }
  .pb-60 {
    padding-bottom: 45px;
  }

  .banner-text {
    font-size: 48px;
    line-height: 58px;
  }

  .guarantee {
    width: 80px;
    height: 80px;
  }

  .see-more {
    display: none;
  }
}

@media only screen and (max-width: 1199px) {
  .banner-left {
    width: 100%;
    margin: 0 auto;
  }
}

.second-part {
  font-family: "PT Sans", serif;
  background: #0094be;
  color: #fff;
  padding: 30px 0px 40px 0px;
}

.panel-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
}

.panel {
  background: #f4fbfe;
  border-radius: 16px;
  width: 49%;
  padding: 32px;

  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 28px;

  /* Text color */

  color: #252020;
  margin: 20px 0px;
}

.panel-title {
  margin: 10px 0px;
  font-weight: 700;
  font-size: 24px;
  line-height: 29px;

  color: #252020;
}

.m15 {
  margin-right: -15px;
  margin-left: -15px;
}

ul {
  list-style: none; /* Remove default bullets */
}

ul li::before {
  content: "\2022"; /* Add content: \2022 is the CSS Code/unicode for a bullet */
  color: #0094be; /* Change the color */
  font-weight: bold; /* If you want it to be bold */
  display: inline-block; /* Needed to add space between the bullet and the text */
  width: 1em; /* Also needed for space (tweak if needed) */
  margin-left: -1em; /* Also needed for space (tweak if needed) */
}

li {
  margin-left: -1em;
}

@media only screen and (max-width: 768px) {
  .panel {
    width: 100%;
  }
}

.bggray {
  background: #f8faf9;
  border-radius: 16px;
}

.moneyback-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
  padding: 50px 30px;
}

.moneyback-left {
  font-family: "PT Sans", serif;
  background: #0094be;
  border-radius: 16px;
  padding: 48px;
  color: white;
  width: 45%;
}

.moneyback-right {
  width: 50%;
  font-family: "PT Sans", serif;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
}

.moneyback-title {
  font-style: normal;
  font-weight: 700;
  font-size: 48px;
  line-height: 58px;
}

.moneyback-subtitle {
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 28px;
}

.moneyback-item {
  width: 48%;
  margin-bottom: 15px;
}

.moneyback-item-title {
  font-style: normal;
  font-weight: 700;
  font-size: 24px;
  line-height: 29px;

  color: #252020;
}
.moneyback-item-desc {
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 22px;

  color: #252020;
}

@media only screen and (max-width: 1024px) {
  .moneyback-item {
    width: 100%;
  }
}

@media only screen and (max-width: 768px) {
  .moneyback-wrapper {
    padding: 20px 15px;
  }

  .moneyback-left {
    width: 100%;
    margin-bottom: 20px;
  }

  .moneyback-right {
    width: 100%;
  }

  .moneyback-item {
    width: 48%;
  }

  .moneyback-title {
    font-size: 28px;
    line-height: 38px;
  }

  .moneyback-subtitle {
    font-size: 12px;
    line-height: 22px;
  }

  .moneyback-item-title {
    font-size: 14px;
    line-height: 20px;
  }
  .moneyback-item-desc {
    font-size: 11px;
    line-height: 18px;
  }
}

.light-gray {
  color: #a5a5a5;
}

.what-section p {
  line-height: 2em;
}
</style>
