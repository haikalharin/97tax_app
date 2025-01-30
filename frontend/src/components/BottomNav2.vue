<template>
  <div class="bottom-nav pt-5 pb-2">
    <div class="container">
      <div class="row mb-4 no-border" v-if="checkIsPaymentPlanLandingPage()">
        <div class="col-12">
          <p>
            <b>DISCLOSURE:</b> 97tax prepares tax documents as a private tax preparation company.
            We do not offer any debt management, debt settlement, or credit repair services.
            We do not attempt to settle or negotiate with any creditors on your behalf, and
            we do not offer any refinancing or lending products. 97tax is not affiliated with
            any government agency.
          </p>
        </div>
      </div>

      <div
        class="row white-border1"
        v-if="checkIsNormalLandingPage() || checkIsPaymentPlanLandingPage()"
      >
        <div class="col-12 col-md-3 mb-4 bottom-nav-last white-border2">
          <!--v-bind:class="[subDomainActive ? '' : 'mt-4']"-->
          <div class="row">
            <div class="col-6 col-md-12">
              <a href="/" target="_self"
                ><img
                  src="@/assets/LogoHeader.png"
                  class="mb-4"
                  style="max-width: 200px; width: 100%"
              /></a>
            </div>
            <div class="col-6 col-md-12 mb-4">
              <span class="m-0 p-0">1044 E. Brandon Blvd</span>
              <div class="address-text">
                <span>Brandon, FL 33511</span>
              </div>
            </div>
          </div>
          <div class="mt-2 copyright">
            <span
              >Copyright &copy; {{ new Date().getFullYear() }} by
              97tax.com</span
            >
          </div>
        </div>
        <div class="col-6 col-md-3 mb-4">
          <div class="d-flex flex-column">
            <div v-if="!subDomainActive" class="d-flex flex-column">
              <div class="copyright mb-2">Quick Links</div>
              <router-link class="mb-2" to="/">Home</router-link>
              <router-link class="mb-2" to="/faqs"
                >Frequently Asked Questions</router-link
              >
              <router-link class="mb-2" to="/about-us">About Us</router-link>
              <router-link class="mb-2" to="/payment-plan"
                >Payment Plans</router-link
              >
              <router-link class="mb-2" to="/penalty-waiver"
                >Penalty Waiver</router-link
              >
            </div>
          </div>
        </div>
        <div class="col-6 col-md-6 mb-4">
          <div class="d-flex flex-column">
            <div class="copyright mb-2">Legals</div>
            <a class="pointer mb-2" @click="goToNextPage('/terms-of-service')"
              >Terms of Service</a
            >
            <router-link class="mb-2" v-if="!subDomainActive" to="/cancel"
              >Cancellations and Refunds</router-link
            >
            <router-link class="mb-2" v-if="!subDomainActive" to="/services"
              >Services</router-link
            >
            <a class="pointer mb-2" @click="goToNextPage('/privacy-policy')"
              >Privacy Policy</a
            >
            <router-link class="mb-2" v-if="!subDomainActive" to="/contact-us"
              >Contact Us</router-link
            >
            <div v-if="subDomainActive">
              <a
                class="mb-0"
                href="https://www.97tax.com/contact-us"
                target="_blank"
                >Contact Us
              </a>
            </div>
          </div>
        </div>

        <!-- <div class="d-flex flex-column">
            <div v-if="!subDomainActive">
              <div class="shown-xs-only text-center mb-5">
                <a href="/" target="_self"><img src="@/assets/LogoHeader.png" width="250" /></a>
              </div>
              <h4 class="mb-4 text-center">Have issues besides payment plans and tax liens?</h4>
              <p>97tax offers tax preparation services that can be ordered online in less than 10 minutes.</p>
              <b-btn
                class="btn-green w-100 font-weight-bold text-uppercase mt-3 btn-contact-us"
                href="/contact-us"
              >Get in Touch With Us!</b-btn>
            </div>
            <span class="copyright">Copyright &copy; {{ new Date().getFullYear() }} by 97tax.com</span>
          </div>
          -->
      </div>
      <!-- <div class="logo-with-copyright" v-else-if="checkIsPaymentPlanOrderForm()">
        <div class="mb-1">
              <a href="/" target="_self"><img src="@/assets/LogoHeader.png" width="150" /></a>
          <div>
            <span >1044 E. Brandon Blvd Brandon, FL 33511</span>
          </div>
        </div>
        <div class="text">
          <span>Copyright &copy; {{ new Date().getFullYear() }} by 97tax.com</span>
        </div>
      </div>
      <div v-else class="pt-5">

      </div> -->
    </div>
  </div>
</template>

<script>
const VueScrollTo = require("vue-scrollto");
import config from "../../config";
export default {
  props: {
    logoHref: {
      type: String,
      default: "#/",
    },
    navType: {
      type: String,
      default: "normal",
    },
  },
  data() {
    return {
      subDomainActive: false,
    };
  },
  mounted() {
    let host = window.location.host;
    if (
      host.match(config.build.paymentPlanSubdomain) ||
      host.match(config.build.paymentPlanSubdomainDev)
    ) {
      this.subDomainActive = true;
    } else {
      this.subDomainActive = false;
    }
  },

  methods: {
    goToNextPage(path) {
      if (this.subDomainActive) {
        window.open(path);
      } else {
        this.$router.push(path);
      }
    },
    scrollToCaseTracker() {
      if (
        window.location.href.includes("case-tracker") ||
        window.location.pathname === "/"
      ) {
        VueScrollTo.scrollTo("#home-page-case-tracker", 500, {
          easing: "ease-out",
        });
      } else {
        this.$router.push("/case-tracker");
      }
    },
    checkIsPaymentPlanLandingPage() {
      let paymentPlanLandingPages = [
        "payment-plan",
        "california-payment-plan",
        "georgia-payment-plan",
        "new-jersey-payment-plan",
        "tax-liens",
        "illinois-payment-plan",
        "michigan-payment-plan",
        "penalty-waiver",
        "ein"
      ];
      return this.navType && paymentPlanLandingPages.indexOf(this.navType) >= 0
        ? true
        : false;
    },
    checkIsPaymentPlanOrderForm() {
      let paymentPlanOrderFormPages = [
        "payment-plan-order-form",
        "california-payment-plan-order-form",
        "georgia-payment-plan-order-form",
        "new-jersey-payment-plan-order-form",
        "tax-liens-order-form",
        "illinois-payment-plan-order-form",
        "michigan-payment-plan-order-form",
      ];
      return this.navType &&
        paymentPlanOrderFormPages.indexOf(this.navType) >= 0
        ? true
        : false;
    },
    checkIsNormalLandingPage() {
      return this.navType && this.navType == "normal";
    },
  },
};
</script>

<style scoped>
.bottom-nav {
  background-color: #2a96c4;
  color: #fff;
  font-family: "PT Sans", serif;
  font-size: 18px;
}
a {
  color: #fff;
  font-size: 18px;
}

.copyright {
  color: #c9eef9;
}
</style>
