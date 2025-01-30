<template>
  <div>
    <loading :active.sync="isLoading"></loading>

    <ein-header></ein-header>

    <ein-progress-indictor
      :title="piTitle"
      :subtitle="piSubTitle"
      :value="curProgressPercent"
    />

    <ein-make-payment
      v-if="dataLoaded"
      :order="order"
      @continue="onRequestJWT"
    />

    <bottom-nav></bottom-nav>
  </div>
</template>

<script>
import BottomNav from "../components/BottomNav.vue";
import { AXIOS } from "../scripts/http-common";
import { SnackUtils } from "../scripts/snack-common";
import EinHeader from "./ein/EinHeader.vue";
import EinMakePayment from "./ein/EinMakePayment.vue";

import EinProgressIndictor from "./ein/EinProgressIndictor.vue";
import moment from "moment";

import Loading from "vue-loading-overlay";
// Import stylesheet
import "vue-loading-overlay/dist/vue-loading.css";

export default {
  components: {
    EinHeader,
    BottomNav,
    EinProgressIndictor,
    EinMakePayment,
    Loading,
  },
  props: {},
  created() {},
  data() {
    return {
      curProgressPercent: 99,
      piTitle: "Make Payment",
      piSubTitle: "Pay with your Credit card or pay with Paypal",

      order: {},
      dataLoaded: false,
      isLoading: false,

      jwtData: "",
      responseJwt: "",
      failureReason: null,
    };
  },
  mounted() {
    this.initializeCardinal();
    this.loadData();
  },
  methods: {
    loadData() {
      console.log("EIN Payment. called loadData()");
      let order_id = this.$route.query.order;
      let self = this;

      AXIOS.get(`/ein/order/${order_id}`).then((response) => {
        self.dataLoaded = true;
        self.order = response.data;

        if (self.order.status == "Incomplete") {
          this.$router.push({ path: "/ein-checkout", query: { order: this.$route.query.order } });
          return;
        }
        if (self.order.status == "Awaiting Signature") {
          this.$router.push({ path: "/ein-docu-sign", query: { order: this.$route.query.order } });
          return;
        }
        if (self.order.status != "Failed" && self.order.status != "Signed") { // Processing, Completed, Cancelled
          this.$router.push({ path: "/ein-confirmation", query: {
            order: this.$route.query.order,
            correlationId: self.order.correlationId,
          } });
          return;
        }
      });
    },

    initializeCardinal() {
      if (!window.cardinalIsInitialized) {
        Cardinal.configure({
          logging: {
            debug: "verbose",
            timeout: 10000,
          },
        });
        window.cardinalIsInitialized = true;
      }
      let self = this;

      Cardinal.off("payments.setupComplete");
      Cardinal.off("payments.validated");

      Cardinal.on("payments.setupComplete", () => {
        console.log(self.jwtData.payload);
        Cardinal.start("cca", self.jwtData.payload);
      });

      Cardinal.on("payments.validated", (data, jwt) => {
        console.log(data, jwt);
        this.failureReason = null;

        if (data.ActionCode === "SUCCESS" || data.ActionCode === "NOACTION") {
          self.responseJwt = jwt;

          // send authorize request
          AXIOS.post(`/ein/authorize`, {
            order: this.order,
            responseJwt: this.responseJwt,
          })
            .then((response) => {
              if (response.data.success) {
                window.location.href = "/ein-confirmation?order=" + this.order.id + "&correlationId=" + response.data.correlationId;
              } else {
                this.failureReason = response.data.message;
                this.initializeCardinal();
                self.isLoading = false;

                setTimeout(() => {
                  SnackUtils.danger(this.failureReason);
                }, 300);
              }
            })
            .catch((e) => {
              if (e.response.status === 409) {
                this.failureReason =
                  "Sorry, it looks like you may have already placed an order with us! Please <a href='/contact-us'>contact us</a> for help.";
              } else {
                this.failureReason =
                  "Unable to make purchase. Please check your payment details or try a different payment method.";
                this.initializeCardinal();
              }

              self.isLoading = false;
              setTimeout(() => {
                SnackUtils.danger(this.failureReason);
              }, 300);
            });
        } else {
          this.failureReason =
            "3D Secure Failed: Your card was unable to be authenticated. Please check your information or use a different card and try again.";

          this.initializeCardinal();
        }

        if (this.failureReason) {
          this.isLoading = false;
          setTimeout(() => {
            SnackUtils.danger(this.failureReason);
          }, 300);

          this.postFailureReason();
        }
      });
    },
    postFailureReason() {
      AXIOS.put(`/ein/fail`, this.order)
        .then((response) => {})
        .catch((error) => {
          console.log(error);
        });
    },

    onRequestJWT(params) {
      this.order = Object.assign(this.order, params);
      this.isLoading = true;

      let self = this;
      this.order.card_number = this.order.card_number.replace(/\s/g, "");

      // TODO: check with Joe
      this.order.business_state = this.order.state;

      AXIOS.post(`/ein/jwt`, this.order)
        .then((response) => {
          self.$set(self, "jwtData", response.data);
          self.order.id = response.data.payload.OrderDetails.OrderNumber;

          console.log("jwtData", self.jwtData.jwt);

          Cardinal.setup("init", {
            jwt: self.jwtData.jwt,
          });
        })
        .catch((e) => {
          console.log(e);
          this.initializeCardinal();
          this.isLoading = false;
          setTimeout(() => {
            SnackUtils.danger(
              "Unexpected error occurred. Please try again later."
            );
          }, 300);
        });
    },

  },
};
</script>

<style>
</style>
