<template>
  <b-navbar toggleable="lg">
    <div class="container">
      <b-navbar-brand href="/" v-if="showLogo">
        <img src="@/assets/logo-v2.png" />
        <!-- <img v-if="subDomainActive" src="@/assets/whiteLogo.png" width="250" /> -->
      </b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse id="nav-collapse" is-nav>
        <div
          class="margin-auto"
          v-if="navType === 'normal' && !subDomainActive"
        >
          <b-navbar-nav>
            <b-nav-item-dropdown text="Services" class="pl-2 pr-2 navcolor">
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
            <b-nav-item href="/about-us" class="pl-2 pr-2">About Us</b-nav-item>
            <b-nav-item href="/faqs" class="pl-2 pr-2">FAQ</b-nav-item>
            <b-nav-item href="/contact-us" class="pl-2 pr-2"
              >Contact Us</b-nav-item
            >
            <!--<b-nav-item v-if="loggedInUser" @click="logout">Logout</b-nav-item>-->
          </b-navbar-nav>
        </div>
        <div
          class="margin-auto"
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
            <b-nav-item-dropdown text="Services" class="pl-2 pr-2">
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
            <b-nav-item @click="scrollMeToFaq()" class="pl-2 pr-2"
              >Frequently Asked Questions</b-nav-item
            >
            <b-nav-item
              @click="scrollMeToTracker()"
              class="pl-2 pr-2"
              >Case Tracker</b-nav-item
            >
            <b-nav-item href="/contact-us" class="pl-2 pr-2"
              >Contact Us</b-nav-item
            >
            <!--<b-nav-item v-if="loggedInUser">Logout</b-nav-item>-->
          </b-navbar-nav>
        </div>
        <div class="margin-auto" v-if="navType === 'tax-liens'">
          <b-navbar-nav>
            <b-nav-item-dropdown text="Services" class="pl-2 pr-2">
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
            <b-nav-item href="/about-us" class="pl-2 pr-2">About Us</b-nav-item>
            <b-nav-item href="/faqs" class="pl-2 pr-2">FAQ</b-nav-item>
            <b-nav-item href="/contact-us" class="pl-2 pr-2"
              >Contact Us</b-nav-item
            >
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
  computed: {
    currentRouteName() {
      return this.$route.name;
    },
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

<style scoped>
.margin-auto {
  margin: 0 auto;
}
.navbar {
  background-color: #fff;
}
.navbar-toggler {
  border: 0;
}
.navbar-dark .navbar-toggler-icon {
  background-image: none;
  position: relative;
}
.navbar-light .navbar-nav .nav-link {
  color: #252020;
}
.navbar-dark .navbar-toggler-icon::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 5px;
  background: #252020;
  box-shadow: 0 11px 0 0 #252020, 0 22px 0 0 #252020;
}
.dropdown-toggle::after {
  display: none;
}
.navbar-dark .navbar-nav .nav-link {
  font-family: "PT Sans", sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 19px;

  color: #252020;
  margin-left: 20px;
}
.navbar-dark .navbar-nav .nav-link:hover {
  color: #252020;
}
.navbar .dropdown-item {
  padding: 5px 10px;
  color: #424242;
  font-size: 16px !important;
}
.navbar .dropdown-item:hover {
  outline: none;
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
<style>
.navbar-light .navbar-nav .nav-link {
  font-family: "PT Sans", sans-serif;
  color: #252020 !important;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 19px;
}
</style>
