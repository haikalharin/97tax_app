<template>
  <b-navbar toggleable="lg" type="dark">
    <div class="container navbar-adjust">
      <b-navbar-brand href="/" v-if="showLogo">
        <div class="d-flex flex-column">
          <div>Admin Panel</div>
          <div>
            <img src="@/assets/LogoHeader.png" width="180" />
          </div>
        </div>
      </b-navbar-brand>

      <div class="ml-auto">
        <b-navbar-nav class="mt-5">
          <b-nav-item
            class="font-weight-light"
            v-if="loggedInUser"
            @click="logout"
            >Logout ></b-nav-item
          >
        </b-navbar-nav>
      </div>
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
  font-family: sans-serif;
  font-size: 20px !important;
  color: #fff;
  font-weight: 400;
  margin-left: 20px;
}
.navbar-dark .navbar-nav .nav-link:hover {
  color: #fff;
}
.navbar .dropdown-item {
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
/*
.navbar .dropdown-menu:before {
  content: "";
  position: absolute;
  right: 120px;
  top: -10px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 10px 10px 10px;
  border-color: transparent transparent #fff transparent;
  z-index: 9999;
}
*/
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
.navbar-adjust {
  padding-right: 11rem;
  padding-left: 8rem;
}
</style>
