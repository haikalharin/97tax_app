<template>
  <page :navType="'tax-liens'">
    <div class="top-hero pt-5 pb-4">
      <div class="container">
        <div class="row">
          <div class="col-sm-8 offset-sm-2 col-12">
            <h1 class="font-weight-bold text-uppercase text-center">
              Do you have an IRS tax lien on your home or business?
            </h1>
          </div>
        </div>
        <div class="row">
          <div class="col-12">
            <h5 class="font-weight-bold text-center">
              For a one-time fee, we will prepare your application for
              removal.
            </h5>
          </div>
        </div>
      </div>
    </div>
    <div class="find-out py-5">
      <div class="container">
        <div class="row">
          <div class="col-sm-5 hidden-xs-only">
            <div class="info-row d-flex my-5 pr-4">
              <div>
                <img src="@/assets/money.svg" alt="" width="70" height="70" />
              </div>
              <div class="info-text ml-4">
                <h5 class="font-weight-bold">Credit</h5>
                <span
                  >Enroll now. Our cases are done within 1 business day.</span
                >
              </div>
            </div>
            <div class="info-row d-flex mb-5 pr-4">
              <div>
                <img
                  src="@/assets/document.svg"
                  alt=""
                  width="70"
                  height="70"
                />
              </div>
              <div class="info-text ml-4">
                <h5 class="font-weight-bold">Flexibility</h5>
                <span
                  >Sell or refinance without restrictions from the IRS.</span
                >
              </div>
            </div>
            <div class="info-row d-flex pr-4">
              <div>
                <img
                  src="@/assets/piggybank.svg"
                  alt=""
                  width="70"
                  height="70"
                />
              </div>
              <div class="info-text ml-4">
                <h5 class="font-weight-bold">Affordable</h5>
                <span>No extra fees or hidden costs.</span>
              </div>
            </div>
          </div>
          <div class="col-sm-7 col-12">
            <div class="find-out-input__panel px-5 pt-5 pb-4">
              <h3 class="font-weight-bold text-uppercase text-center mb-4">
                FIND OUT <SPAN style="color: #0098c3">INSTANTLY</SPAN> IF YOU
                QUALIFY
              </h3>
              <div class="row mb-2">
                <div class="col-sm-6 col-12">
                  <label class="form-control-label light-gray"
                    >First Name</label
                  >
                  <input
                    :class="[
                      'form-control',
                      errors.firstname ? 'is-invalid' : '',
                    ]"
                    type="text"
                    v-model="taxLienData.firstname"
                    placeholder="First Name"
                    v-on:keypress="isLetter($event)"
                  />
                </div>
                <div class="col-sm-6 col-12">
                  <label class="form-control-label light-gray">Last Name</label>
                  <input
                    :class="[
                      'form-control',
                      errors.lastname ? 'is-invalid' : '',
                    ]"
                    type="text"
                    v-model="taxLienData.lastname"
                    placeholder="Last Name"
                    v-on:keypress="isLetter($event)"
                  />
                </div>
              </div>
              <div class="row mb-2">
                <div class="col-sm-6 col-12 hidden-xs-only">
                  <label class="form-control-label light-gray"
                    >Phone Number</label
                  >
                  <input
                    :class="['form-control', errors.phone ? 'is-invalid' : '']"
                    type="text"
                    v-model="taxLienData.phone"
                    placeholder="Phone Number"
                    v-mask="'###-###-####'"
                  />
                </div>
                <div class="col-sm-6 col-12">
                  <label class="form-control-label light-gray">Email</label>
                  <input
                    :class="['form-control', errors.email ? 'is-invalid' : '']"
                    @keydown.space="(event) => event.preventDefault()"
                    type="text"
                    v-model="taxLienData.email"
                    placeholder="Email Address"
                  />
                </div>
              </div>
              <div class="row mt-4 mb-3">
                <div class="col-12">
                  <div class="selectWrapper">
                    <select class="selectBox form-control">
                      <option value="House">My tax lien is on my home</option>
                      <option value="Business">
                        My tax lien is on my business
                      </option>
                      <option value="Other Property">
                        My tax lien is on other property
                      </option>
                    </select>
                  </div>
                </div>
              </div>
              <div
                v-if="hasErrors"
                class="
                  d-flex
                  justify-content-center
                  mb-2
                  text-danger
                  flex-column
                "
              >
                <div v-for="(error, i) in errors" :key="i">
                  <div v-if="error" class="mb-2">
                    {{ error }}
                  </div>
                </div>
              </div>
              <div class="d-flex justify-content-center mb-4">
                <button
                  class="btn btn-green font-weight-bold text-uppercase"
                  v-on:click="paymentPlanSubmit"
                  style="width: 300px"
                >
                  Find out now
                </button>
              </div>
              <div class="row">
                <div class="col-sm-12 col-8 offset-sm-0 offset-2">
                  <div class="d-flex justify-content-center align-items-center">
                    <font-awesome-icon
                      class="mr-2"
                      style="color: #0094be"
                      icon="lock"
                    />
                    <span style="font-size: 12px"
                      >Your personal information is secured through SSL
                      Encryption</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="shown-xs-only px-5 pt-5 pb-3">
          <div class="info-row d-flex flex-column align-items-center py-3">
            <img src="@/assets/money.svg" alt="" width="100" height="100" />
            <h4 class="font-weight-bold my-2">Credit</h4>
            <h6 class="text-center">
              Enroll now. Our cases are done within 1 business day.
            </h6>
          </div>
          <div class="info-row d-flex flex-column align-items-center py-3">
            <img src="@/assets/document.svg" alt="" width="100" height="100" />
            <h4 class="font-weight-bold my-2">Flexibility</h4>
            <h6 class="text-center">
              Sell or refinance without restrictions from the IRS.
            </h6>
          </div>
          <div class="info-row d-flex flex-column align-items-center py-3">
            <img src="@/assets/piggybank.svg" alt="" width="100" height="100" />
            <h4 class="font-weight-bold my-2">Affordable</h4>
            <h6 class="text-center">No extra fees or hidden costs.</h6>
          </div>
        </div>
      </div>
    </div>

    <div class="fresh-tax-lien py-4">
      <div class="container">
        <div class="row mb-5">
          <div class="col-sm-10 offset-sm-1 col-12 offset-0">
            <h1 class="mb-3 text-center font-weight-bold">
              What is an IRS tax lien?
            </h1>
            <h4 class="light-gray text-center font-weight-bold">
              A federal tax lien is a legal claim against your property, usually
              your home or business, to secure payment of your tax debt.
            </h4>
          </div>
        </div>
        <div class="row my-2">
          <div class="col-sm-6 col-12">
            <h3 class="xs-center font-weight-bold">
              How do IRS tax liens get removed?
            </h3>
            <p class="mb-4">
              You are currently on a payment plan for your debt with the IRS. In
              this case, even though you still owe money the IRS considers you
              in good standing with them and a lien is unnecessary.
            </p>
            <p class="mb-4">
              Removing the lien would be in the best interest of you and the
              IRS.
            </p>
            <p class="mb-4">
              You have an opportunity for a new job a few towns away that pays
              better, but you can't move there without selling your home.
            </p>
            <p class="mb-4">
              Removing the lien would increase your income and ability to repay
              your debt, so withdrawing your lien would be in the best interest
              of you and the IRS.
            </p>
            <p class="mb-4">
              You have a business and an opportunity to sell more of your
              product or service, but you need to borrow money to do it.
            </p>
            <p class="mb-4">
              You cannot get approved for business lending with the tax lien.
              Removing the lien would increase your income and ability to repay
              your debt, so withdrawing your lien would be in the best interest
              of you and the IRS.
            </p>
            <p class="mb-4">
              It's been over 10 years since your tax debt was incurred. The
              statute of limitations for collections has now passed, and
              withdrawing the tax lien is in the best interest of you and the
              IRS.
            </p>
            <p class="mb-4">
              The above is not the complete list. Be sure to enter your
              information in at the top of this page to access the options that
              apply to you. It's always free to check.
            </p>
          </div>
          <div class="col-sm-6 col-12">
            <h3 class="xs-center font-weight-bold">What is an IRS tax lien?</h3>
            <p class="mb-4">
              A federal tax lien is a legal claim against your property, usually
              your home or business, to secure payment of your tax debt. Once
              the IRS files a tax lien, they have alerted creditors and the
              public that the government has a legal right to your property.
            </p>
            <h3 class="xs-center font-weight-bold">
              Why is an IRS tax lien bad?
            </h3>
            <p class="mb-4">
              An IRS tax lien can prevent the sale or refinancing of your home,
              the sale of your vehicles, negatively impact your credit score, or
              prevent you ability to borrow money. Paying off your debt in full
              is often the best way to remove a tax lien, but not always
              possible. See below for the other reasons that removal would be in
              the best interest of you and the IRS.
            </p>
            <h3 class="xs-center font-weight-bold">Our Benefits</h3>
            <p class="mb-4">
              Fast and Easy - We are the only company with an online process
              where you can place an order for a tax lien removal application in
              less than 10 minutes. Free to Check - It's always 100% free to
              check what removal options you qualify for to withdraw your tax
              lien. Huge Savings - We charge a one-time fee of $97 to prepare
              your application. There are no other hidden fees or costs.
            </p>
            <h3 class="xs-center font-weight-bold">What's next?</h3>
            <p>
              Hiring an attorney is expensive and unnecessary, and doing it
              yourself can cause months of delay and even more frustration. We
              prepare all the IRS documents necessary to submit your tax lien
              removal application. Within 24 hours, we'll mail your completed
              application to the mailing address you provide. Just sign the
              forms, drop it in the mail, and you're doone. Easy! Head to the
              top of this page to start the process.
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="frequently-asked-questions pb-5">
      <div class="container">
        <h1 class="text-center font-weight-bold my-3">
          Frequently Asked Questions
        </h1>
        <h4 class="light-gray text-center mb-5">We have lots of answers</h4>
        <div class="my-3" id="embedded_faq">
          <tax-lien-faqs></tax-lien-faqs>
        </div>
      </div>
    </div>

    <div id="embedded_case-tracker2">
      <order-tracker></order-tracker>
    </div>

    <div id="embedded_our-clients-tax">
      <our-clients-tax></our-clients-tax>
    </div>

    <b-modal
      ref="notQualifyModal"
      title="Not Qualified"
      ok-title="Yes"
      cancel-title="No"
    >
      <p class="my-4">
        Based on your amount of debt, you do not qualify for this payment plan.
      </p>
      <p class="my-4">
        Would you like an experienced attorney in our network to contact you and
        discuss your options?
      </p>
    </b-modal>
  </page>
</template>

<!-- sharea sale tag -->
<script>
var shareasaleSSCID = shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e, a, r, s, t) {
  if (e && a) {
    var o,
      n = s ? "; path=" + s : "",
      i = t ? "; domain=" + t : "",
      l = "";
    r &&
      ((o = new Date()).setTime(o.getTime() + r),
      (l = "; expires=" + o.toUTCString())),
      (document.cookie = e + "=" + a + l + n + i);
    console.log("cookies", document.cookie);
  }
}
function shareasaleGetParameterByName(e, a) {
  a || (a = window.location.href), (e = e.replace(/[\[\]]/g, "\\$&"));
  var r = new RegExp("[?&]" + e + "(=([^&#]*)|&|#|$)").exec(a);
  return r ? (r[2] ? decodeURIComponent(r[2].replace(/\+/g, " ")) : "") : null;
}
shareasaleSSCID &&
  shareasaleSetCookie("shareasaleSSCID", shareasaleSSCID, 94670778e4, "/");
</script>


<script>
import { AXIOS } from "../scripts/http-common";
import { SiteUtils } from "../scripts/site-common";
const VueScrollTo = require("vue-scrollto");

export default {
  data() {
    return {
      taxLienData: {
        firstname: "",
        lastname: "",
        phone: "",
        email: "",
        gotIrsForm: false,
        existingTaxLien: false,
      },
      errors: {
        firstname: false,
        lastname: false,
        phone: false,
        email: false,
        optionSelect: false,
      },
    };
  },
  methods: {
    isLetter(e) {
      if (e.keyCode === 32) return true;
      let char = String.fromCharCode(e.keyCode);
      if (/^[A-Za-z]+$/.test(char)) return true;
      else e.preventDefault();
    },

    paymentPlanSubmit() {
      let required = ["firstname", "lastname", "phone", "email"];
      let hasErrors = false;

      hasErrors = SiteUtils.formErrors(required, this.taxLienData, this.errors);

      this.hasErrors = false;

      let checkFirstName = SiteUtils.validateFirstName(
        this.taxLienData.firstname
      );
      if (checkFirstName) {
        this.errors.firstname = checkFirstName;
        this.hasErrors = true;
      }

      let checkLastName = SiteUtils.validateLastName(this.taxLienData.lastname);
      if (checkLastName) {
        this.errors.lastname = checkLastName;
        this.hasErrors = true;
      }

      let checkEmailAddressErrorMsg = SiteUtils.validateEmail(
        this.taxLienData.email
      );
      if (checkEmailAddressErrorMsg) {
        this.errors.email = checkEmailAddressErrorMsg;
        this.hasErrors = true;
      }

      let checkPhoneNumberErrorMsg = SiteUtils.validatePhone(
        this.taxLienData.phone
      );
      if (screen.width >= 576 && checkPhoneNumberErrorMsg) {
        this.errors.phone = checkPhoneNumberErrorMsg;
        this.hasErrors = true;
      }

      if (this.hasErrors) {
        this.isSubmitting = false;
        return;
      }

      this.taxLienData.phone = SiteUtils.standardizePhone(
        this.taxLienData.phone
      );
      AXIOS.post(`/taxlienremoval`, this.taxLienData)
        .then((response) => {
          window.location.href = "/tax-lien-order-form";
        })
        .catch((e) => {
          console.log(e);
        });
    },
    scrollMeTo(selector) {
      VueScrollTo.scrollTo(selector, 500, { easing: "ease-out" });
    },
  },
  mounted() {
    // AXIOS.get(`/ecommerce/order`)
    //   .then(response => {
    //     this.paymentPlanData.firstName = response.data.firstName;
    //     this.paymentPlanData.lastName = response.data.lastName;
    //     this.paymentPlanData.phone = response.data.phone;
    //     this.paymentPlanData.email = response.data.email;
    //   })
    //   .catch(e => {
    //     console.log(e);
    //   })
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i");
::placeholder {
  /* Chrome, Firefox, Opera, Safari 10.1+ */
  color: #aaa9a9;
  opacity: 1; /* Firefox */
  font-family: "Raleway", sans-serif;
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

.light-gray {
  color: #a5a5a5;
}

.fresh-tax-lien p {
  line-height: 2em;
}

@media (max-width: 575px) {
  .top-hero {
    padding-top: 24px !important;
  }

  .top-hero h1 {
    font-size: 28px;
  }

  .top-hero h5 {
    font-size: 18px;
  }

  .find-out {
    padding-top: 0 !important;
    padding-bottom: 0 !important;
  }

  .find-out > .container {
    padding-left: 0;
    padding-right: 0;
  }

  .find-out-input__panel {
    padding-left: 1rem !important;
    padding-right: 1rem !important;
  }

  .xs-center {
    text-align: center;
  }
}

.selectWrapper {
  border-radius: 0.25rem;
  display: inline-block;
  overflow: hidden;
  background: #cccccc;
  border: #b0b0b0 2px solid;
  width: 100%;
  height: 40px;
}

.selectBox {
  height: 40px;
  border: 0px;
  outline: none;
  width: 100%;
  color: #000;
}
</style>
