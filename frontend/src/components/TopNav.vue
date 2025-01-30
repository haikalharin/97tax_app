<template>
  <b-navbar toggleable="lg" type="dark">
    <div class="container">
      <b-navbar-brand href="/" v-if="showLogo">
        <img src="@/assets/LogoHeader.png" width="250" />
        <!-- <img v-if="subDomainActive" src="@/assets/whiteLogo.png" width="250" /> -->
      </b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse id="nav-collapse" is-nav>
        <div class="ml-auto" v-if="navType === 'normal' && !subDomainActive">
          <b-navbar-nav>
            <b-nav-item-dropdown text="Services">
              <b-dropdown-item class="dropdown-top-m" href="/payment-plan"
                >IRS Payment Plan Application</b-dropdown-item
              >
              <b-dropdown-item class="dropdown-top-m" href="/state-payment-plan"
                >State Payment Plan Applications</b-dropdown-item
              >
              <b-dropdown-item class="dropdown-top-m" href="/penalty-waiver"
                >Penalty Waiver Letter</b-dropdown-item
              >
              <b-dropdown-item class="dropdown-top-m" href="/ein"
                >EIN Application</b-dropdown-item
              >
              <!-- <b-dropdown-item href="/california-payment-plan">California Payment Plan Service</b-dropdown-item>
              <b-dropdown-item href="/new-jersey-payment-plan">New Jersey Payment Plan Service</b-dropdown-item> -->
            </b-nav-item-dropdown>
            <b-nav-item href="/about-us">About Us</b-nav-item>
            <b-nav-item href="/faqs">FAQ</b-nav-item>
            <b-nav-item href="/contact-us">Contact Us</b-nav-item>
            <!--<b-nav-item v-if="loggedInUser" @click="logout">Logout</b-nav-item>-->
          </b-navbar-nav>
        </div>
        <div
          class="ml-auto"
          v-if="
            navType === 'payment-plan' ||
            navType === 'california-payment-plan' ||
            navType === 'georgia-payment-plan' ||
            navType === 'new-jersey-payment-plan' ||
            navType === 'michigan-payment-plan' ||
            navType === 'illinois-payment-plan' ||
            navType === 'penalty-waiver' ||
            navType === 'ein'
          "
        >
          <b-navbar-nav>
            <b-nav-item-dropdown text="Services">
              <b-dropdown-item class="dropdown-top-m" href="/payment-plan"
                >IRS Payment Plan Application</b-dropdown-item
              >
              <b-dropdown-item class="dropdown-top-m" href="/state-payment-plan"
                >State Payment Plan Applications</b-dropdown-item
              >
              <b-dropdown-item class="dropdown-top-m" href="/penalty-waiver"
                >Penalty Waiver Letter</b-dropdown-item
              >
              <b-dropdown-item class="dropdown-top-m" href="/ein"
                >EIN Application</b-dropdown-item
              >
              <!-- <b-dropdown-item href="/california-payment-plan">California Payment Plan Service</b-dropdown-item>
              <b-dropdown-item href="/new-jersey-payment-plan">New Jersey Payment Plan Service</b-dropdown-item> -->
            </b-nav-item-dropdown>
            <b-nav-item @click="scrollMeToFaq()"
              >Frequently Asked Questions</b-nav-item
            >
            <b-nav-item @click="scrollMeToTracker()"
              >Case Tracker</b-nav-item
            >
            <b-nav-item href="/contact-us">Contact Us</b-nav-item>
            <!--<b-nav-item v-if="loggedInUser">Logout</b-nav-item>-->
          </b-navbar-nav>
        </div>
        <div class="ml-auto" v-if="navType === 'tax-liens'">
          <b-navbar-nav>
            <b-nav-item-dropdown text="Services">
              <b-dropdown-item href="/payment-plan"
                >IRS Payment Plan Application</b-dropdown-item
              >
              <b-dropdown-item href="/state-payment-plan"
                >State Payment Plan Applications</b-dropdown-item
              >
              <b-dropdown-item href="/tax-liens"
                >Tax Lien Removal</b-dropdown-item
              >
            </b-nav-item-dropdown>
            <b-nav-item href="/about-us">About Us</b-nav-item>
            <b-nav-item href="/faqs">FAQ</b-nav-item>
            <b-nav-item href="/contact-us">Contact Us</b-nav-item>
            <!-- <b-nav-item v-if="loggedInUser">Logout</b-nav-item>-->
          </b-navbar-nav>
        </div>
      </b-collapse>
    </div>
  </b-navbar>
</template>

<script>
import config from "../../config";
import { AXIOS } from "../scripts/http-common";

const VueScrollTo = require("vue-scrollto");

export default {
  props: {
    navType: {
      type: String,
      default: "normal",
    },
    logoHref: {
      type: String,
      default: "/",
    },
    showLogo: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      subDomainActive: false,
      loggedInUser: "",
    };
  },
  mounted() {
    let userInStorage = JSON.parse(localStorage.getItem("loggedUser"));

    this.loggedInUser =
      this.$store.getters.getUser != ""
        ? this.$store.getters.getUser
        : userInStorage
        ? userInStorage
        : "";

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
    scrollMeToFaq() {
      VueScrollTo.scrollTo("#embedded_faq", 500, { easing: "ease-out" });
    },
    scrollMeToTracker() {
      // We had duplicate IDs for the Case Trackers and #scrollTo didn't work, replaced with 2 IDs
      VueScrollTo.scrollTo("#embedded_case-tracker1", 500, { easing: "ease-out" });
      VueScrollTo.scrollTo("#embedded_case-tracker2", 500, { easing: "ease-out" });
    },
    scrollMeTo(selector) {
      VueScrollTo.scrollTo(selector, 500, { easing: "ease-out" });
    },
    logout() {
      AXIOS.post("/auth/logout")
        .then((response) => {
          localStorage.clear();
          window.location.href = "/";
        })
        .catch((e) => {
          alert(
            "An error has occurred while attempting to log out. Please contact the administrator."
          );
          console.log(e);
        });
    },
  },
};
</script>

<style>
.navbar {
  background-color: #0094be;
}
.navbar-toggler {
  border: 0;
}
.navbar-dark .navbar-toggler-icon {
  background-image: none;
  position: relative;
}
.navbar-dark .navbar-toggler-icon::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 5px;
  background: #fff;
  box-shadow: 0 11px 0 0 #fff, 0 22px 0 0 #fff;
}
.dropdown-toggle::after {
  display: none;
}
.navbar-dark .navbar-nav .nav-link {
  font-family: "PT Sans", sans-serif;
  font-size: 20px !important;
  color: #fff;
  font-weight: 400;
  margin-left: 20px;
}
.navbar-dark .navbar-nav .nav-link:hover {
  color: #fff;
}
.navbar .dropdown-item {
  font-family: "PT Sans", sans-serif;
  padding: 5px 10px;
  color: #424242;
  font-size: 16px !important;
}
.navbar .dropdown-item:hover {
  outline: none;
}
.navbar .dropdown-menu {
  padding: 7px;
  border-radius: 10px;
  border: 0;
  margin-top: -8px;
  margin-left: -15px;
}
.navbar .dropdown-menu:before {
  content: "";
  position: absolute;
  left: 50px;
  top: -6px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 10px 10px 10px;
  border-color: transparent transparent #fff transparent;
  z-index: 9999;
}
.navbar .dropdown:hover .dropdown-menu {
  display: block;
}

@media (max-width: 575px) {
  .navbar {
    padding-top: 25px;
  }
  .navbar-brand img {
    width: 120px;
  }
}
</style>
