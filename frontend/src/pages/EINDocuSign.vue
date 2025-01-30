<template>
  <div>
    <ein-header></ein-header>

    <ein-progress-indictor
      :title="piTitle"
      :subtitle="piSubTitle"
      :value="curProgressPercent"
    />
    <ein-docu-sign v-if="dataLoaded" @signDoc="onSignDoc" @back="onBack" />

    <bottom-nav></bottom-nav>
  </div>
</template>

<script>
import BottomNav from "../components/BottomNav.vue";
import { AXIOS } from "../scripts/http-common";
import { APPLICATION_SUMMARY_PAGE } from "../scripts/site-common";
import EinHeader from "./ein/EinHeader.vue";
import EinProgressIndictor from "./ein/EinProgressIndictor.vue";
import EinDocuSign from "./ein/EinDocuSign.vue";
import moment from "moment";

export default {
  components: {
    EinHeader,
    BottomNav,
    EinProgressIndictor,
    EinDocuSign,
  },
  props: {},
  created() {},
  data() {
    return {
      curProgressPercent: 95,
      piTitle: "Form SS-4",
      piSubTitle: "It is required to sign the form below for us to legally request EIN",
      order: {},
      dataLoaded: false,
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    loadData() {
      console.log("EIN DocuSign. called loadData()");
      let order_id = this.$route.query.order;
      let self = this;

      AXIOS.get(`/ein/order/${order_id}`).then((response) => {
        self.order = response.data;
        self.dataLoaded = true;

        console.log("ORDER STATUS:", self.order.status);
        console.log("ORDER LAST SAVED PAGE:", self.order.last_saved_page);

        if (self.order.status == "Incomplete") {
          if (self.order.last_saved_page == APPLICATION_SUMMARY_PAGE) {
            return;
          }
          this.$router.push({ path: "/ein-checkout", query: { order: this.$route.query.order } });
          return;
        }
        if (self.order.status == "Failed" || self.order.status == "Signed") {
          this.$router.push({ path: "/ein-payment", query: { order: this.$route.query.order } });
          return;
        }
        if (self.order.status != "Awaiting Signature") { // Processing, Completed, Cancelled
          this.$router.push({ path: "/ein-confirmation", query: {
            order: this.$route.query.order,
            correlationId: self.order.correlationId,
          } });
          return;
        }

      });
    },
    onSignDoc() {
      let order_id = this.$route.query.order;
      AXIOS.get(`/ein/fss4/${order_id}/start-sign`).then((response) => {
        window.location.href = response.data.redirectUrl;
      });
    },
    onBack() {
      localStorage.setItem("backFromSign", 1);
      this.$router.push({ path: "/ein-checkout", query: { order: this.$route.query.order } });
    },
  },
};
</script>

<style>
</style>
