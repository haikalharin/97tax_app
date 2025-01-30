<template>
  <page :navType="'payment-plan'">
    <div id="top-hero" class="top-hero pt-5 pb-4">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <h1 class="font-weight-bold text-uppercase text-center">
              NEED TO SET UP AN <br />IRS PAYMENT PLAN?
            </h1>
            <h5 class="font-weight-bold text-center">
              For a one-time enrollment fee, set up a plan with small monthly
              payments.
            </h5>
          </div>
        </div>
      </div>
    </div>
    <div class="find-out py-5">
      <div class="container" style="overflow: hidden">
        <div class="row">
          <div class="col-sm-5 hidden-xs-only">
            <div class="info-row d-flex my-5 pr-4">
              <div>
                <img src="@/assets/help.svg" alt="" width="70" height="70" />
              </div>
              <div class="info-text ml-4">
                <h5 class="font-weight-bold">Fast Help</h5>
                <span
                  >Enroll now. Our cases are done within 1 business day.</span
                >
              </div>
            </div>
            <div class="info-row d-flex mb-5 pr-4">
              <div>
                <img src="@/assets/stop.svg" alt="" width="70" height="70" />
              </div>
              <div class="info-text ml-4">
                <h5 class="font-weight-bold">Prevent</h5>
                <span
                  >New wage garnishment, new tax liens, and collections.</span
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
            <div
              v-show="isQualified"
              class="find-out-input__panel px-5 pt-5 pb-4"
            >
              <h3 class="font-weight-bold text-uppercase text-center mb-4">
                FIND OUT <SPAN style="color: #0098c3">INSTANTLY</SPAN> IF YOU
                QUALIFY
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
                    v-model="paymentPlanData.firstName"
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
                    v-model="paymentPlanData.lastName"
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
                    v-model="paymentPlanData.phone"
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
                    v-model="paymentPlanData.email"
                    placeholder="Email Address"
                  />
                </div>
              </div>
              <div
                class="my-4 d-flex justify-content-between align-items-center"
              >
                <h5 class="mb-1" style="font-size: 1.15rem">
                  How much do you owe the IRS?
                </h5>
                <div class="d-flex align-items-center">
                  <span class="light-gray mr-1">$</span>
                  <input
                    id="dollars-input"
                    v-on:keypress="focusCent($event)"
                    :class="[
                      'form-control underlined-control underlined-wide mr-1',
                      errors.totalDebt ? 'price-invalid' : '',
                    ]"
                    class="decimal-whole-part"
                    v-model="paymentPlanData.totalDebt"
                    @keypress="onlyNumberSixDigitsMax"
                    type="tel"
                    v-mask="'######'"
                  />
                  <span class="dot mr-1">.</span>
                  <input
                    id="cents-input"
                    :class="[
                      'form-control underlined-control underlined-small',
                      errors.totalDebtDecimal ? 'is-invalid' : '',
                    ]"
                    class="decimal-fraction-part"
                    v-model="paymentPlanData.totalDebtDecimal"
                    @keypress="onlyNumberTwoDigitsMax"
                    type="tel"
                    v-mask="'##'"
                  />
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
                  class="btn btn-green font-weight-bold text-uppercase md-width"
                  v-on:click="paymentPlanSubmit"
                  :disabled="isSubmitting"
                >
                  <span v-if="isSubmitting">Finding...</span>
                  <span v-if="!isSubmitting">Find out now</span>
                </button>
              </div>
              <div class="row">
                <div class="col-12 col-md-10 offset-0 offset-md-1">
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

            <div
              v-show="!isQualified"
              class="find-out-warning__panel px-5 pt-4 pb-3"
            >
              <div class="orang-box"></div>
              <div style="margin-left: 3%">
                <div class="change-amount">
                  <span class="arial-17 times-31">You entered:</span>
                  <h5 class="amount font-weight-bold">
                    {{ formatCurrency(totalAmount) }}
                  </h5>
                  <button
                    class="btn font-weight-bold"
                    v-on:click="changeAmount"
                    :disabled="isSubmitting"
                  >
                    <font-awesome-icon :icon="['fas', 'chevron-left']" />
                    <span class="btn-label">Change this</span>
                  </button>
                </div>
                <div class="flag-wrapper">
                  <img class="flag" src="@/assets/flag.png" />
                  <span class="arial-17 times-31"
                    >Amounts over ${{
                      nFormatter(paymentPlanConfig.maxAmount)
                    }}
                    don’t qualify for this option, but you can do the
                    following:</span
                  >
                </div>
                <p class="arial-17" style="height: 4%">
                  Pay the IRS ${{
                    (totalAmount - paymentPlanConfig.maxAmount).toFixed(2)
                  }}
                  within the next 7 days to reduce your balance to less than ${{
                    nFormatter(paymentPlanConfig.maxAmount)
                  }}
                </p>
                <!-- <hr> -->
                <button
                  class="btn gradient curser"
                  v-on:click="confirmPlanSubmit(true)"
                >
                  <span class="link-times31 curser"
                    >I’ll reduce my balance below ${{
                      nFormatter(paymentPlanConfig.maxAmount)
                    }}, let’s continue
                    <font-awesome-icon :icon="['fas', 'chevron-right']"
                  /></span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="shown-xs-only px-5 pt-5 pb-3">
          <div class="info-row d-flex align-items-center py-3">
            <img src="@/assets/help.svg" alt="" width="100" height="100" />
            <div>
              <h4 class="font-weight-bold my-2 pl-4">Fast Help</h4>
              <h6 class="pl-4">
                Enroll now. Our cases are done within 1 business day.
              </h6>
            </div>
          </div>
          <div class="info-row d-flex align-items-center py-3">
            <img src="@/assets/stop.svg" alt="" width="100" height="100" />
            <div>
              <h4 class="font-weight-bold my-2 pl-4">Prevent</h4>
              <h6 class="pl-4">
                New wage garnishment, new tax liens, and collections.
              </h6>
            </div>
          </div>
          <div class="info-row d-flex align-items-center py-3">
            <img src="@/assets/piggybank.svg" alt="" width="100" height="100" />
            <div>
              <h4 class="font-weight-bold my-2 pl-4">Affordable</h4>
              <h6 class="pl-4">No extra fees or hidden costs.</h6>
            </div>
          </div>
        </div>
        <div class="shown-xs-only" id="embedded_case-tracker1">
          <order-tracker></order-tracker>
        </div>
      </div>
    </div>

    <div class="fresh-start-program py-4">
      <div class="container">
        <div class="row mb-5">
          <div class="col-sm-8 offset-sm-2 col-12 offset-0">
            <h1 class="mb-3 text-center font-weight-bold">
              What is the IRS Fresh Start Program?
            </h1>
            <h4 class="light-gray text-center font-weight-bold">
              The Fresh Start program through the IRS makes paying back a little
              over time and avoiding tax liens possible.
            </h4>
          </div>
        </div>
        <div class="hidden-xs-only row my-2">
          <div class="col-sm-6 col-12">
            <h3 class="xs-center font-weight-bold">Payment Plans</h3>
            <p class="mb-4">
              The Fresh Start program has expanded access to IRS payment plans.
              Now, individual taxpayers who owe up to $50,000 can pay monthly
              installments for up to 72 months (six years). Installment plans
              setup through 97tax.com always calculate your payment at the full
              72 months.
            </p>
            <p class="mb-4">
              While the IRS generally will not need a financial statement, they
              may need some additional financial information later from the
              taxpayer. In those cases, the IRS will contact you by phone or by
              mail at the provided address.
            </p>
            <p>
              Hiring an attorney is expensive and unnecessary, and doing it
              yourself can cause months of delay and even more frustration. We
              prepare all the IRS documents necessary to submit your payment
              plan. Within 24 hours, we'll mail your completed application to
              the mailing address you provide. Just sign the forms, drop it in
              the mail, and you're done. Easy!
            </p>
          </div>
          <div class="col-sm-6 col-12">
            <h3 class="xs-center font-weight-bold">Tax Liens</h3>
            <p class="mb-4">
              Just by applying to the IRS Fresh start program through 97tax.com,
              the IRS is stopped from filing any additional tax liens and will
              not continue collection (statute
              <a
                href="https://www.gpo.gov/fdsys/pkg/CFR-2015-title26-vol20/pdf/CFR-2015-title26-vol20-sec301-6159-1.pdf"
                target="_blank"
                ><SPAN
                  class="font-weight-bold"
                  style="color: #0098c3; text-decoration: underline"
                  >CFR 301.6159-1</SPAN
                ></a
              >) from the day they receive your application.
            </p>
            <p class="mb-5">
              <a href="#top-hero"
                ><SPAN
                  class="font-weight-bold"
                  style="color: #0098c3; text-decoration: underline"
                  >Click here</SPAN
                ></a
              >
              to see if you qualify and get your application for an installment
              plan started now.
            </p>
            <h3 class="xs-center font-weight-bold">
              Tax Levies and Wage Garnishments
            </h3>
            <p>
              Our payment plan service will NOT remove current wage
              garnishments, remove current tax liens, or stop a
              pending/completed tax levy (a levy is where the IRS has already
              physically taken an asset or frozen your bank account). Our payment
              plan service will only prevent new tax liens and new wage
              garnishments from being filed. It's important to note that if you
              default on the online payment agreement (OPA), the IRS may file
              new tax liens and resume collection actions.
            </p>
          </div>
        </div>
        <div class="shown-xs-only my-2">
          <payment-plan-ques></payment-plan-ques>
        </div>
        <div class="hidden-xs-only my-5 d-flex justify-content-center">
          <a
            class="btn btn-green font-weight-bold text-uppercase px-5"
            href="#top-hero"
            style="text-decoration: none"
            >Click Here to Get Your Application Started Now</a
          >
        </div>
      </div>
    </div>

    <div class="did-you-know py-5">
      <div class="container">
        <div class="row py-3">
          <div class="col-sm-10 offset-sm-1 col-12 offset-0">
            <h1 class="mb-4 text-center font-weight-bold">Did you know?</h1>
            <h4 class="text-center">
              There are 5 different addresses to send the required IRS forms
              depending on your mailing address. 97tax provides a prepaid
              mailing envelope along with your documents with the correct IRS
              address to avoid delays.
            </h4>
          </div>
        </div>
      </div>
    </div>

    <div class="frequently-asked-questions py-5">
      <div class="container">
        <h1 class="text-center font-weight-bold my-3">
          Frequently Asked Questions
        </h1>
        <h4 class="light-gray text-center mb-5">We have lots of answers</h4>
        <div class="my-3" id="embedded_faq">
          <payment-plan-faqs></payment-plan-faqs>
        </div>
      </div>
    </div>

    <div class="hidden-xs-only" id="embedded_case-tracker2">
      <order-tracker></order-tracker>
    </div>

    <div id="embedded_our-clients">
      <our-clients></our-clients>
    </div>

    <b-modal
      ref="under500Modal"
      @hidden="closeModal"
      @ok="closeModal"
      title="Not Qualified"
      ok-only
    >
      <p class="my-4">Your debt must be over $500 to qualify.</p>
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
// import {VMoney} from 'v-money'
const VueScrollTo = require("vue-scrollto");
import { paymentPlansConfig } from "../../config/plans-config";

export default {
  data() {
    return {
      isSubmitting: false,
      isQualified: true,
      planCreated: false,
      paymentPlanConfig: paymentPlansConfig.irsPaymentPlan,
      paymentPlanData: {
        firstName: "",
        lastName: "",
        phone: "",
        email: "",
        totalDebt: "",
        totalDebtDecimal: "",
        isCalifornia: false,
        isNewJersey: false,
        isGeorgia: false,
        isIllinois: false,
        paymentMonths: paymentPlansConfig.irsPaymentPlan.maxMonth,
        paymentDayOfMonth: "14",
      },
      money: {
        decimal: ".",
        thousands: ",",
        prefix: "",
        suffix: "",
        precision: 0,
        masked: false,
      },
      errors: {
        firstName: false,
        lastName: false,
        phone: false,
        email: false,
        totalDebt: false,
        totalDebtDecimal: false,
      },
      hasErrors: false,
      totalAmount: 0,
    };
  },
  methods: {
    isLetter(e) {
      if (e.keyCode === 32) return true;
      let char = String.fromCharCode(e.keyCode);
      if (/^[A-Za-z]+$/.test(char)) return true;
      else e.preventDefault();
    },

    isMobile() {
      return SiteUtils.isMobile();
    },
    formatCurrency(amount) {
      return `$${amount.toFixed(2).replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,")}`;
    },
    focusCent: function (e) {
      if (e.code == "Period" || e.code == "Comma") {
        document.getElementById("cents-input").focus();
      }
    },
    onlyNumber(keyCode) {
      return keyCode >= 48 && keyCode <= 57;
    },
    onlyNumberSixDigitsMax($event) {
      let keyCode = $event.keyCode ? $event.keyCode : $event.which;

      if (!this.onlyNumber(keyCode)) {
        return $event.preventDefault();
      }
    },
    onlyNumberTwoDigitsMax($event) {
      let keyCode = $event.keyCode ? $event.keyCode : $event.which;

      if (!this.onlyNumber(keyCode)) {
        return $event.preventDefault();
      }
    },
    scrollMeTo(selector) {
      VueScrollTo.scrollTo(selector, 500, { easing: "ease-out" });
    },
    changeAmount() {
      this.isQualified = true;
    },
    paymentPlanSubmit() {
      this.isSubmitting = true;
      this.paymentPlanData.totalDebtDecimal =
        this.paymentPlanData.totalDebtDecimal.trim() !== ""
          ? this.paymentPlanData.totalDebtDecimal.trim()
          : "00";
      let required = ["firstName", "lastName", "email", "totalDebt"];
      this.hasErrors = false;

      /*if (screen.width >= 576) {
              required.push('phone');
            }*/
      required.forEach((p) => {
        if (
          !this.paymentPlanData[p] ||
          this.paymentPlanData[p].trim().length === 0
        ) {
          this.errors[p] = true;
          this.hasErrors = true;
        } else {
          this.errors[p] = false;
        }
      });

      let checkFirstName = SiteUtils.validateFirstName(
        this.paymentPlanData.firstName
      );
      if (checkFirstName) {
        this.errors.firstName = checkFirstName;
        this.hasErrors = true;
      }

      let checkLastName = SiteUtils.validateLastName(
        this.paymentPlanData.lastName
      );
      if (checkLastName) {
        this.errors.lastName = checkLastName;
        this.hasErrors = true;
      }

      let checkEmailAddressErrorMsg = SiteUtils.validateEmail(
        this.paymentPlanData.email
      );
      if (checkEmailAddressErrorMsg) {
        this.errors.email = checkEmailAddressErrorMsg;
        this.hasErrors = true;
      }

      let checkPhoneNumberErrorMsg = SiteUtils.validatePhone(
        this.paymentPlanData.phone
      );
      if (
        screen.width >= 576 &&
        checkPhoneNumberErrorMsg &&
        this.paymentPlanData.phone
      ) {
        this.errors.phone = checkPhoneNumberErrorMsg;
        this.hasErrors = true;
      }

      if (!SiteUtils.validateDollarsCents(this.paymentPlanData.totalDebt)) {
        this.errors.totalDebt = true;
        this.hasErrors = true;
      } else {
        this.paymentPlanData.totalDebt = this.paymentPlanData.totalDebt.replace(
          /,/g,
          ""
        );
        this.paymentPlanData.totalDebt = this.paymentPlanData.totalDebt.replace(
          /\$/g,
          ""
        );
      }

      if (this.hasErrors) {
        this.isSubmitting = false;
        return;
      }

      if (screen.width >= 576) {
        this.paymentPlanData.phone = SiteUtils.standardizePhone(
          this.paymentPlanData.phone
        );
      }

      let paymentPlan = SiteUtils.calculatePaymentPlan(
        this.paymentPlanData.totalDebt.trim() +
          "." +
          this.paymentPlanData.totalDebtDecimal.trim()
      );
      if (!paymentPlan.qualifies) {
        if (paymentPlan.totalDebt < this.paymentPlanConfig.minAmount) {
          this.$refs.under500Modal.show();
          return false;
        } else {
          this.isQualified = false;
          this.totalAmount = paymentPlan.totalDebt;
        }
      }

      return this.confirmPlanSubmit(false);
    },
    closeModal(e) {
      this.isSubmitting = false;
    },
    confirmPlanSubmit(isAmountReduced) {
      let totalDebt =
        this.paymentPlanData.totalDebt.trim() +
        "." +
        this.paymentPlanData.totalDebtDecimal.trim();
      totalDebt = isNaN(parseFloat(totalDebt)) ? 0 : Number(totalDebt);
      if (totalDebt > this.paymentPlanConfig.maxAmount) {
        totalDebt = this.paymentPlanConfig.maxAmount;
      }

      this.paymentPlanData.paymentMonths = this.paymentPlanConfig.maxMonth;

      const paymentPlan = SiteUtils.calculatePaymentPlan(
        totalDebt,
        this.paymentPlanData.paymentMonths,
        this.paymentPlanData
      );
      const monthlyPayment = totalDebt / this.paymentPlanData.paymentMonths;

      const planData = {
        ...this.paymentPlanData,
        orderUrl: window.location.href,
        isMobile: this.isMobile(),
        monthlyPayment,
        totalDebt,
      };
      delete planData.totalDebtDecimal;
      const paymentplanSubmitPromise = this.planCreated
        ? AXIOS.put("/paymentplan", planData)
        : AXIOS.post("/paymentplan", planData);

      paymentplanSubmitPromise
        .then(() => {
          this.planCreated = true;
          if (this.isQualified || isAmountReduced) {
            this.$router.push("/payment-plan-order-form");
          }
        })
        .catch((e) =>
          console.log("Error while trying to submit payment plan: ", e)
        )
        .finally(() => {
          this.isSubmitting = false;
        });
    },
  },
  mounted() {
    const dollarsInput = document.getElementById("dollars-input");
    const centsInput = document.getElementById("cents-input");

    function filterNonDigits(event) {
      setTimeout(() => {
        dollarsInput.value = dollarsInput.value.replace(/[^0-9]/g, "");
        centsInput.value = centsInput.value.replace(/[^0-9]/g, "");
        if (event.data === "." && event.target.id === "dollars-input") {
          dollarsInput.blur();
          centsInput.focus();
        }
      }, 0);
    }

    ["input", "change", "paste"].forEach((eventName) => {
      dollarsInput.addEventListener(eventName, filterNonDigits);
      centsInput.addEventListener(eventName, filterNonDigits);
    });

    // AXIOS.get(`/ecommerce/order`)
    //   .then(response => {
    //     this.paymentPlanData.firstName = response.data.firstName;
    //     this.paymentPlanData.lastName = response.data.lastName;
    //     this.paymentPlanData.phone = response.data.phone;
    //     this.paymentPlanData.email = response.data.email;
    //     // this.paymentPlanData.totalDebt = response.data.totalDebt;
    //     this.totalAmount=response.data.totalDebt;
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
  font-weight: 300;
}

input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
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

.underlined-control {
  border: 0;
  border-bottom: 1px solid #a5a5a5;
  background-color: transparent;
  border-radius: 0;
  height: auto;
  line-height: 1em;
  outline: none !important;
  padding: 0 3px 3px;
  font-style: italic;
  font-family: sans-serif;
  font-size: 28px;
}
.price-invalid {
  border: 1px solid #dc3545;
  border-radius: 0.25rem;
}
.underlined-control:focus,
.underlined-control:active {
  outline: none !important;
  box-shadow: none;
  background-color: transparent !important;
}

.underlined-wide {
  width: 103px;
}

.underlined-small {
  width: 40px;
}

.dot {
  align-self: flex-end;
  font-size: 28px;
}

.fresh-start-program p {
  line-height: 2em;
}

.did-you-know {
  background-color: #0094be;
  color: #fff;
}

.did-you-know h4 {
  line-height: 1.5em;
}

.gradient {
  padding-top: 3px;
  margin-bottom: 15px;
  border: 3px solid;
  box-shadow: 0 -2px 2px -2px rgba(0, 0, 0, 0.1),
    0 -2px 20px 0 rgba(0, 0, 0, 0.13);
  border-image-slice: 1;
  border-radius: 15px !important;
  border-left: 0px;
  border-right: 0px;
  border-color: rgba(3, 4, 5, 0.2);
  border-top-color: rga(2, 4, 6, 0.6);
  text-align: center;
}

.arial-17 {
  font-family: Arial, Helvetica, sans-serif;
  font-size: 17px;
  color: black;
}

.change-amount {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}
.change-amount .amount,
.change-amount .btn {
  color: #0fb153;
  margin-left: 10px;
  margin-bottom: 0;
}
.change-amount .btn {
  border: 1px solid #ccc;
  border-radius: 15px;
  box-shadow: 1px 1px 5px #444;
  width: 150px;
}
.change-amount .btn .btn-label {
  margin-left: 5px;
}

.flag-wrapper {
  display: flex;
  margin-bottom: 20px;
}

.flag {
  height: 45px;
  width: 45px;
  margin-bottom: 10px;
  margin-right: 3%;
  margin-top: 2%;
  margin-left: -60px;
}

.tick {
  height: 65px;
  width: 65px;
  margin-right: 7%;
  margin-top: 2%;
  margin-left: -10%;
}
.times-31 {
  font-size: 20px;
  font-weight: bold;
  font-family: "Times New Roman", Times, serif;
  color: black;
}
.link-times31 {
  font-size: 1.17rem;
  font-weight: bold;
  font-family: "Times New Roman", Times, serif;
  color: black;
}

.covid19-alert {
  height: 35px;
  width: 35px;
}

.md-width {
  width: 300px;
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

  .find-out-input__panel,
  .find-out-warning__panel {
    padding-left: 1rem !important;
    padding-right: 1rem !important;
  }

  .owe-irs {
    flex-direction: column;
  }

  .xs-center {
    text-align: center;
  }

  .arial-17 {
    display: block;
  }

  .flag-wrapper {
    display: block;
  }

  .flag {
    margin-left: 0;
  }

  .gradient {
    height: 80px;
    padding: 10px;
  }

  .md-width {
    width: 100%;
  }
}
</style>
