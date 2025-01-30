<template>
  <page :navType="'new-jersey-payment-plan'">
    <div id="top-hero" class="top-hero pt-5 pb-4">
      <div class="container">
        <div class="row">
          <div class="col-sm-6 offset-sm-3 col-12">
            <h1 class="font-weight-bold text-uppercase text-center">
              OWE NEW JERSEY TAXES<br />& NEED A PAYMENT PLAN?
            </h1>
          </div>
        </div>
        <div class="row">
          <div class="col-12">
            <h5 class="font-weight-bold text-center">
              For a one-time enrollment fee, set up a Division of Taxation
              payment plan with small monthly payments
              <!-- For a one-time enrollment fee of $97, set up a Division of Taxation payment plan with small monthly payments. -->
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
                  >New wage garnishments and collections from New Jersey.</span
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
                  How much do you owe New Jersey?
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
                    don’t qualify for this option, so please change
                    amount.</span
                  >
                </div>
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
          <order-tracker isNewJersey="true"></order-tracker>
        </div>
      </div>
    </div>

    <div class="fresh-start-program py-4">
      <div class="container">
        <div class="row mb-5">
          <div class="col-sm-8 offset-sm-2 col-12 offset-0">
            <h1 class="mb-3 text-center font-weight-bold">
              What is the New Jersey Payment Plan Request?
            </h1>
            <h4 class="light-gray text-center font-weight-bold">
              A New Jersey payment plan requested with the Division of Taxation
              allows monthly payments on state income tax over
              {{ paymentPlanConfig.maxMonth }} months. Here's more about how to
              request a payment plan:
            </h4>
          </div>
        </div>
        <div class="hidden-xs-only row my-2">
          <div class="col-sm-6 col-12">
            <h3 class="xs-center font-weight-bold">Payment Plans</h3>
            <p class="mb-4">
              The state of New Jersey has provided access to payment plans.
              Individual taxpayers who owe up to $75,000 to the Division of
              Taxation can pay in monthly installments for up to
              {{ paymentPlanConfig.maxMonth }} months (five years). Payment plan
              requests setup through 97tax.com always calculate your payment at
              the full {{ paymentPlanConfig.maxMonth }} months and according to
              the published rules of the state.
            </p>
            <p class="mb-4">
              While New Jersey generally will not need a financial statement,
              they may need some additional financial information later from
              you. In those cases, the state will contact you by phone or by
              mail at the provided address. Take a look at our FAQ further down
              this page for more information.
            </p>
            <p>
              Hiring a local attorney is expensive and unnecessary, and doing it
              yourself can cause months of delay and even more frustration. We
              prepare all the New Jersey Division of Taxation documents
              necessary to submit your payment plan. Within one business day,
              we'll mail your completed application to the mailing address you
              provide. Just sign the forms, drop it in the mail, and you're
              done.
            </p>
          </div>
          <div class="col-sm-6 col-12">
            <h3 class="xs-center font-weight-bold">Flexibility</h3>
            <p class="mb-4">
              By applying for a payment plan online through 97tax.com, our
              system will automatically calculate the minimum payment allowed by
              New Jersey with a maximum of
              {{ paymentPlanConfig.maxMonth }} months. During the process, you
              can also select your normal monthly due date and increase payments
              to pay off your balance faster.
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
            <h3 class="xs-center font-weight-bold">Eligibility</h3>
            <p>
              The maximum amount you can owe to the Division of Taxation and
              still be eligible for an installment agreement is $75,000. You
              also must:
            </p>
            <ul>
              <li>
                pay the balance in a maximum of
                {{ paymentPlanConfig.maxMonth }} months.
              </li>
              <li>have filed all of your New Jersey state tax returns.</li>
            </ul>
            <p>
              It's important to note that if you miss payments on the New Jersey
              installment agreement, the state will cancel the payment plan.
            </p>
          </div>
        </div>
        <div class="shown-xs-only my-2">
          <new-jersey-payment-plan-ques></new-jersey-payment-plan-ques>
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
              Normal payments and payment plan applications go to different
              addresses within the state of New Jersey. 97tax provides a prepaid
              mailing envelope along with your documents with the correct
              Division of Taxation address to avoid delays.
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
          <new-jersey-payment-plan-faqs></new-jersey-payment-plan-faqs>
        </div>
      </div>
    </div>

    <div id="embedded_case-tracker2">
      <order-tracker isNewJersey="true"></order-tracker>
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
      <p class="my-4">
        Your debt must be over ${{
          formatCurrencyWithCommas(paymentPlanConfig.minAmount)
        }}
        to qualify.
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
import { paymentPlansConfig } from "../../config/plans-config";

export default {
  data() {
    return {
      isSubmitting: false,
      isQualified: true,
      planCreated: false,
      paymentPlanConfig: paymentPlansConfig.newJerseyPaymentPlan,
      paymentPlanData: {
        firstName: "",
        lastName: "",
        phone: "",
        email: "",
        totalDebt: "",
        totalDebtDecimal: "",
        isNewJersey: true,
        isCalifornia: false,
        isGeorgia: false,
        isIllinois: false,
        paymentMonths: paymentPlansConfig.newJerseyPaymentPlan.maxMonth,
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
        this.paymentPlanData.totalDebtDecimal.trim() || "00";
      let required = ["firstName", "lastName", "email", "totalDebt"];
      this.hasErrors = false;

      /* if (screen.width >= 576) {
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
          this.paymentPlanData.totalDebtDecimal.trim(),
        this.paymentPlanConfig.maxMonth,
        this.paymentPlanData
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

      return this.confirmPlanSubmit();
    },
    closeModal(e) {
      this.isSubmitting = false;
    },
    confirmPlanSubmit() {
      let totalDebt =
        this.paymentPlanData.totalDebt.trim() +
        "." +
        this.paymentPlanData.totalDebtDecimal.trim();
      totalDebt = isNaN(parseFloat(totalDebt)) ? 0 : Number(totalDebt);
      if (totalDebt > this.paymentPlanConfig.maxAmount) {
        totalDebt = this.paymentPlanConfig.maxAmount;
      }
      this.paymentPlanData.paymentMonths =
        SiteUtils.calculatePaymentMonthsWithAmount(
          totalDebt,
          this.paymentPlanConfig.maxMonth
        );

      const paymentPlan = SiteUtils.calculatePaymentPlan(
        totalDebt,
        this.paymentPlanData.paymentMonths,
        this.paymentPlanData
      );
      const monthlyPayment =
        paymentPlan.paymentAmount === "N/A"
          ? this.paymentPlanConfig.minMonthlyPayment
          : paymentPlan.paymentAmount;

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
          if (this.isQualified) {
            this.$router.push("/new-jersey-payment-plan-order-form");
          }
        })
        .catch((e) => console.log(e))
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
    //     this.totalAmount = response.data.totalDebt;
    //   })
    //   .catch(e => console.log(e));
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
  height: 45px;
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

  .owe-new-jersey {
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
