<template>

  <div class="bottom-nav" style="height: 100px;">
    <div class="container">
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
      default: "#/"
    },
    navType: {
      type: String,
      default: "normal"
    }
  },
  data() {
    return {
      subDomainActive: false
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
    goToNextPage(path){
      if(this.subDomainActive){
        window.open(path);
      }else{
        this.$router.push(path)
      }

    },
    scrollToCaseTracker() {
      if (
        window.location.href.includes("case-tracker") ||
        window.location.pathname === "/"
      ) {
        VueScrollTo.scrollTo("#home-page-case-tracker", 500, {
          easing: "ease-out"
        });
      } else {
        this.$router.push("/case-tracker");
      }
    }
  }
};
</script>

<style scoped>
.bottom-nav {
  background-color: #0094be;
  color: #fff;
}
.pointer{
cursor: pointer;
}
.center-align {
  /* justify-content: center; */
  align-items: center;
  display: flex;
}
.bottom-nav-last {
  position: relative;
}

a {
  color: inherit;
  text-decoration: none;
  margin-bottom: 20px;
}

span {
  font-size: 14px;
}

.btn-contact-us {
  margin-bottom: 30px;
}

.copyright {
  position: absolute;
  bottom: 0;
  right: 15px;
}

@media (max-width: 575px) {
  .copyright {
    position: static;
    text-align: center;
    margin-bottom: 20px;
  }
}
</style>
