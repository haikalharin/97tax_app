<template>
  <div>
    <ein-header></ein-header>

    <div class="mt-4">
      <ein-payment-success
        :order="order"
      />
    </div>

    <bottom-nav></bottom-nav>
  </div>
</template>

<script>
import BottomNav from "../components/BottomNav.vue";
import { AXIOS } from "../scripts/http-common";
import { hashEmail, hashPhone, cleanupAddress, hashAddress, cleanupPostalCode } from "../scripts/gtag-common";
import EinHeader from "./ein/EinHeader.vue";
import EinPaymentSuccess from "./ein/EinPaymentSuccess.vue";
import moment from "moment";

export default {
  components: {
    EinHeader,
    BottomNav,
    EinPaymentSuccess,
  },
  props: {},
  created() {},
  data() {
    return {
      order: {},

      shareasaleSSCID: '',
      success: true,
    };
  },
  mounted() {
    this.handleShareasale();
    this.loadData();
  },
  methods: {
    shareasaleGetCookie(e) {
      var r=e+"=";
      var a=decodeURIComponent(document.cookie);
      var o=a.split(";");
      for(var n=0;n<o.length;n++)
      {var t=o[n];while(t.charAt(0)==" ")
      {t=t.substring(1)}if(t.indexOf(r)==0)
      {return t.substring(r.length,t.length)}}
      return""
    },
    handleShareasale() {
      AXIOS.get(`/ein/confirmation/${this.$route.query.correlationId}`)
        .then(response => {
          let confirmation = response.data;
          this.success = true;
          this.shareasaleSSCID=this.shareasaleGetCookie('shareasaleSSCID');
          console.log("shareasaleSSCID: ", this.shareasaleSSCID);

          var script=document.createElement('script');
          script.defer = true;
          script.async = true;
          script.type="text/javascript";
          script.src="https://shareasale-analytics.com/j.js";

          let theAmount = confirmation.amount/100;
          console.log("Amount: ",theAmount);
          console.log("Order Id: ",confirmation.id);

          var imagetag = document.createElement('img');
              imagetag.id = "_SHRSL_img_1";
              imagetag.src = "https://www.shareasale.com/sale.cfm?tracking="+confirmation.id+"&amount="+theAmount+"&merchantID=76439&transtype=sale&sscidmode=6&sscid="+this.shareasaleSSCID;
              imagetag.width = "1";
              imagetag.height = "1";
              imagetag.style.float = "left";
          document.getElementById('app').insertAdjacentElement("beforebegin",imagetag);
          document.getElementById('_SHRSL_img_1').insertAdjacentElement("afterend",script);

          var gtagEventConversion = {
            'send_to': 'AW-820207740/nAqzCKff6Y0BEPzAjYcD',
            'transaction_id': confirmation.orderId,
            'user_id': String(confirmation.userId),
            'user_data': {
              'sha256_email_address': [
                hashEmail(confirmation.email),
              ],
              'sha256_phone_number': [
                hashPhone(confirmation.phone_number),
              ],
              'address': [{
                'sha256_first_name': hashAddress(confirmation.first_name),
                'sha256_last_name': hashAddress(confirmation.last_name),
                'sha256_street': hashAddress(confirmation.address + (confirmation.apt_suite ? " " + confirmation.apt_suite : "")),
                'city': cleanupAddress(confirmation.city),
                'region': cleanupAddress(confirmation.state),
                'postal_code': cleanupPostalCode(confirmation.zip_code),
                'country': 'US',
              }]
            }
          }

          console.log("GTAG EVENT CONVERSION:", gtagEventConversion);
          window.gtag('event', 'conversion', gtagEventConversion);
        })
        .catch(() => {
          this.success = false;
        })
    },

    loadData() {
      console.log("EIN Confirmation. called loadData()");
      let order_id = this.$route.query.order;
      let self = this;

      AXIOS.get(`/ein/order/${order_id}`).then((response) => {
        self.order = response.data;

        if (self.order.status == "Incomplete") {
          // Last user's visited page might be a signature page (just without pressing the "Sign Document" button)
          // We will redirect user to the EIN Application Summary page anyway here (not a big deal, code wil be more clear)
          this.$router.push({ path: "/ein-checkout", query: { order: this.$route.query.order } });
          return;
        }
        if (self.order.status == "Awaiting Signature") {
          this.$router.push({ path: "/ein-docu-sign", query: { order: this.$route.query.order } });
          return;
        }
        if (self.order.status == "Failed" || self.order.status == "Signed") {
          this.$router.push({ path: "/ein-payment", query: { order: this.$route.query.order } });
          return;
        }

        // TODO: Utilize when tracking number will be introduced
        // let awaitingDocusign = self.order.status === 'Awaiting Signature';
        // if (!awaitingDocusign && !self.order.ein) {
        //  setTimeout(self.loadData, 10*60000);
        // }
      });
    },
  },
};
</script>

<style>
</style>
