<template>
  <page>
    <div class="container">
      <div class="row">
        <div class="col-12">
          <div v-if="success">
            <h1 class="mt-4 py-3 text-center font-weight-bold text-uppercase">Thank you</h1>
            <p>Thank you for your order. Your payment of ${{amountDollars}} was successful and we'll begin working on your case now. Your order number is <b>{{confirmation.orderId}}</b>. Please check your other order details below:</p>
            <div class="upsell-product" v-if="upsellProductData.isShow">
              {{updateUpsellShown()}}
              <div class="d-flex">
                  <hr class="m-0 border-line">
                  <span class="border-arrow font-weight-bold">&gt;&gt;</span>
              </div>
            <img src="@/assets/lightbulb-colored.png" class="icon"/>
            <div class="row">
              <div class="col-1"></div>
              <div class="col-7 pb-2">
                  <span v-if="upsellProductData.displayMessage">
                    {{ upsellProductData.displayMessage }}
                  </span>
                  <span v-else>
                      Do you also owe a balance to the <b>{{upsellProductData.messageDepartment}}</b>? It
                      so, we can also help you set up a payment plan with {{upsellProductData.messageState}}.</span>
              </div>
              <div class="col-3">
                  <button class="btn btn-primary btn-green w-100 font-weight-bold d-flex justify-content-between" v-on:click="updateUpsellProduct(upsellProductData.productName,upsellProductData.route)">
                    <span class="font-weight-bold">GET STARTED</span>
                    <span class="font-weight-bold">&gt;&gt;</span>
                  </button>
              </div>
            </div>
            </div>
            <div class="d-flex" v-if="upsellProductData.isShow">
                <hr class="m-0 border-line"/>
                <span class="border-arrow font-weight-bold">&gt;&gt;</span>
            </div>

           <div class="order-details__panel mt-5 mb-4">
              <h3 class="text-center font-weight-bold mb-4">
                Your Order Details - {{confirmation.orderId}}
              </h3>
              <div class="row">
                <div class="col-12 col-sm-6">
                  <div class="info-block">
                    <div class="info-label">Name</div>
                    <div class="info-value">{{confirmation.firstName}} {{confirmation.lastName}}</div>
                  </div>
                  <div class="info-block">
                    <div class="info-label">Email</div>
                    <div class="info-value">{{confirmation.email}}</div>
                  </div>
                  <div class="info-block">
                    <div class="info-label">Phone Number</div>
                    <div class="info-value">{{confirmation.phone}}</div>
                  </div>
                  <div class="info-block">
                    <div class="info-label">Order Date</div>
                    <div class="info-value">{{formatDate(confirmation.orderDate)}}</div>
                  </div>
                </div>
                <div class="col-12 col-sm-6">
                  <div class="info-block">
                    <div class="info-label">Mailing Address</div>
                    <div class="info-value">
                      <div>{{confirmation.shippingAddress1}}</div>
                      <div>{{confirmation.shippingAddress2}}</div>
                      <div>{{confirmation.shippingCity}},</div>
                      <div>{{confirmation.shippingState}} {{confirmation.shippingZip}}</div>
                    </div>
                  </div>
                  <div class="info-block">
                    <div class="info-label" v-if="confirmation.isCalifornia">Payments to the FTB:</div>
                    <div class="info-label" v-if="confirmation.isNewJersey">Payments to the Division of Taxation:</div>
                    <div class="info-label" v-if="confirmation.isGeorgia">Payments to the Board of Revenue:</div>
                    <div class="info-label" v-if="confirmation.isIllinois">Payments to Illinois:</div>
                    <div class="info-label" v-if="confirmation.isMichigan">Payments to Michigan:</div>
                    <div class="info-label" v-if="!confirmation.isCalifornia && !confirmation.isNewJersey && !confirmation.isGeorgia && !confirmation.isIllinois && !confirmation.isMichigan">Payments to the IRS:</div>
                    <div class="info-value">
                      <div>Total Amount Owed: ${{confirmation.totalDebt}}</div>
                      <div>Monthly Payment: ${{confirmation.monthlyPayment}}</div>
                      <div>Monthly Due Date (this month excluded): {{confirmation.paymentDayOfMonth}}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="explanation">
              <p>We have also sent you an email confirmation with the above information. If any of this information is incorrect, please use our contact form now to let us know so we can update it right away.</p>
              <p>We are preparing your documents now and will have them complete and in the mail to you within the next business day (Monday - Friday). We will mail this directly to you first through the <b>Post Office</b> (your physical signature is required) and email you with the <b>USPS tracking number</b> once it's on the way.</p>
              <p>You can get updates by entering your order number and phone number into the order tracker on our homepage.</p>
            </div>
          </div>
          <div v-if="!success">
            <p>Error occurred while trying to get order confirmation info.</p>
          </div>
        </div>
      </div>
    </div>
  </page>
</template>

<script>
  import moment from 'moment';
  import { AXIOS } from '../scripts/http-common'
  import { hashEmail, hashPhone, cleanupAddress, hashAddress, cleanupPostalCode } from "../scripts/gtag-common";
  import pick from "lodash/pick";
  export default {
    data() {
      return {
        marriedFilingOptions: Object.freeze({ separately:"No", jointly: "Yes" }),
        confirmation: {
          orderId: '',
          firstName: '',
          lastName: '',
          phone: '',
          email: '',
          amount: '',
          billingAddress1: '',
          billingAddress2: '',
          billingCity: '',
          billingState: '',
          billingZip: '',
          shippingAddress1: '',
          shippingAddress2: '',
          shippingCity: '',
          shippingState: '',
          shippingZip: '',
          orderDate: null,
          isMichigan: false,
          isCalifornia: false,
          isNewJersey: false,
          isGeorgia:false,
          isIllinois:false,
          totalDebt: 0,
          status:"",
          monthlyPayment: 0,
          paymentDayOfMonth: '',
          upsellProduct:"",
          upsellClicked:0,
          upsellShown:0,
          married:false,
          filingJointly:""
        },
        upsellProductData:{
          route:"",
          messageDepartment:"",
          messageState:"",
          productName:"",
          isShow:false,
          displayMessage: null
        },
        shareasaleSSCID:'',
        success: true
      }
    },
    computed: {
      amountDollars() {
        if (this.confirmation.amount) {
          return parseInt(this.confirmation.amount) / 100;
        } else {
          return 0;
        }
      }
    },
    mounted() {
      AXIOS.get(`/ecommerce/confirmation/${this.$route.params.correlationId}`)
        .then(response => {
          this.confirmation = response.data;
          this.success = true;
          const self = this;
          this.shareasaleSSCID=this.shareasaleGetCookie('shareasaleSSCID');
          console.log("shareasaleSSCID: ", this.shareasaleSSCID);

          var script=document.createElement('script');
          script.defer = true;
          script.async = true;
          script.type="text/javascript";
          script.src="https://shareasale-analytics.com/j.js";


          console.log("Amount : ",this.amountDollars);

          console.log("Order Id : ",this.confirmation.orderId);


          var imagetag = document.createElement('img');
              imagetag.id = "_SHRSL_img_1";
              imagetag.src = "https://www.shareasale.com/sale.cfm?tracking="+this.confirmation.orderId+"&amount="+this.amountDollars+"&merchantID=76439&transtype=sale&sscidmode=6&sscid="+this.shareasaleSSCID;
              imagetag.width = "1";
              imagetag.height = "1";
              imagetag.style.float = "left";

              document.getElementById('app').insertAdjacentElement("beforebegin",imagetag);
              document.getElementById('_SHRSL_img_1').insertAdjacentElement("afterend",script);

          var gtagEventConversion = {
            'send_to': 'AW-820207740/nAqzCKff6Y0BEPzAjYcD',
            'transaction_id': this.confirmation.orderId,
            'user_id': String(this.confirmation.userId),
            'user_data': {
              'sha256_email_address': [
                hashEmail(this.confirmation.email),
              ],
              'sha256_phone_number': [
                hashPhone(this.confirmation.phone),
              ],
              'address': [{
                'sha256_first_name': hashAddress(this.confirmation.firstName),
                'sha256_last_name': hashAddress(this.confirmation.lastName),
                'sha256_street': hashAddress(this.confirmation.billingAddress1),
                'city': cleanupAddress(this.confirmation.billingCity),
                'region': cleanupAddress(this.confirmation.billingState),
                'postal_code': cleanupPostalCode(this.confirmation.billingZip),
                'country': 'US',
              }]
            }
          }

          console.log("GTAG EVENT CONVERSION:", gtagEventConversion);
          window.gtag('event', 'conversion', gtagEventConversion);

          AXIOS.get(`/ecommerce/orders/listInProcessAndComplete/${this.confirmation.orderId}`)
          .then(orderResponse => {
              this.setUpsell(orderResponse.data);
          })
          .catch(() => {
            alert('upsell not show')
          })
        })
        .catch(() => {
          this.success = false;
        })
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
      printPage() {
        window.print();
        return false;
      },
      formatDate(dateTimeStamp) {
        if (!dateTimeStamp) {
          return null;
        }
        return moment(dateTimeStamp).format('MMM DD, YYYY');
      },
      updateUpsell(){
        AXIOS.put(`/ecommerce/order/id/${this.confirmation.orderId}/update`, pick(this.confirmation,['upsellProduct','upsellClicked','upsellShown']))
        .then(response => {
          this.success = true;
        })
        .catch(() => {
            this.success = false;
        })
      },
      updateUpsellProduct(upsellProduct,route){
        this.confirmation.upsellProduct = upsellProduct;
        this.confirmation.upsellClicked = 1 ;
        this.updateUpsell();
        this.$router.push(route);
      },
      updateUpsellShown(){
        if(this.confirmation.upsellShown == 0 || !this.confirmation.upsellShown){
          this.confirmation.upsellShown = 1;
          this.updateUpsell();
        }
      },
      checkIsIRSPayment(){
        return this.confirmation && this.confirmation.orderId && !(this.confirmation.isCalifornia || this.confirmation.isNewJersey
          || this.confirmation.isGeorgia || this.confirmation.isIllinois || this.confirmation.isMichigan);
      },
      checkIsStatePaymentPlan(){
        return this.confirmation && (this.confirmation.isCalifornia || this.confirmation.isNewJersey
          || this.confirmation.isGeorgia || this.confirmation.isIllinois || this.confirmation.isMichigan);
      },
      checkUserIsMarriedAndSeparately(){
          return this.confirmation && this.confirmation.married &&
                this.confirmation.filingJointly == this.marriedFilingOptions.separately ? true : false;
      },
      setUpsell(orders){
        this.upsellProductData = {
          route:"",
          messageDepartment:"",
          messageState:"",
          productName:"",
          isShow:false,
          displayMessage: null
        }
        if (this.checkIsIRSPayment()) {
          if (this.checkUserIsMarriedAndSeparately()) {
            this.upsellProductData.displayMessage = 'Does your spouse also need a payment plan with the Internal Revenue Service?  If so, we can also help you set up a payment plan for them.';
            this.upsellProductData.route = "/payment-plan";
            this.upsellProductData.productName = "IRS Payment Plan";
            this.upsellProductData.isShow = true;
          } else {
            if(this.confirmation.shippingState === 'CA' && orders.findIndex(x => x.isCalifornia == true) < 0){
              this.upsellProductData.route = "/california-payment-plan";
              this.upsellProductData.messageDepartment = "California Franchise Tax Board";
              this.upsellProductData.messageState = "California";
              this.upsellProductData.productName = "California State Payment Plan";
              this.upsellProductData.isShow = true;
            } else if( this.confirmation.shippingState === 'GA' && orders.findIndex(x => x.isGeorgia == true) < 0){
              this.upsellProductData.route = "/georgia-payment-plan";
              this.upsellProductData.messageDepartment = "Georgia Department of Revenue";
              this.upsellProductData.messageState = "Georgia";
              this.upsellProductData.productName = "Georgia State Payment Plan";
              this.upsellProductData.isShow = true;
            } else if( this.confirmation.shippingState === 'IL' && orders.findIndex(x => x.isIllinois == true) < 0){
              this.upsellProductData.route = "/illinois-payment-plan";
              this.upsellProductData.messageDepartment = "Illinois Department of Revenue";
              this.upsellProductData.messageState = "Illinois";
              this.upsellProductData.productName = "Illinois State Payment Plan";
              this.upsellProductData.isShow = true;
            } else if( this.confirmation.shippingState === 'NJ' && orders.findIndex(x => x.isNewJersey == true) < 0){
              this.upsellProductData.route = "/new-jersey-payment-plan";
              this.upsellProductData.messageDepartment = "New Jersey Department of Revenue";
              this.upsellProductData.messageState = "New Jersey";
              this.upsellProductData.productName = "New Jersey State Payment Plan";
              this.upsellProductData.isShow = true;
            } else if ( this.confirmation.shippingState === 'MI' && orders.findIndex(x => x.isMichigan == true) < 0) {
              this.upsellProductData.route = "/michigan-payment-plan";
              this.upsellProductData.messageDepartment = "Michigan Department of Treasury";
              this.upsellProductData.messageState = "Michigan";
              this.upsellProductData.productName = "Michigan State Payment Plan";
              this.upsellProductData.isShow = true;

            } else if (this.confirmation.shippingState === 'AZ' && orders.findIndex(x => x.isArizona == true) < 0) {
              this.upsellProductData.route = "/arizona-payment-plan";
              this.upsellProductData.messageDepartment = "Arizona Department of Revenue";
              this.upsellProductData.messageState = "Arizona";
              this.upsellProductData.productName = "Arizona State Payment Plan";
              this.upsellProductData.isShow = true;

            } else if (this.confirmation.shippingState === 'LA' && orders.findIndex(x => x.Louisiana == true) < 0) {

              this.upsellProductData.route = "/louisiana-payment-plan";
              this.upsellProductData.messageDepartment = "Louisiana Department of Revenue";
              this.upsellProductData.messageState = "Louisiana";
              this.upsellProductData.productName = "Louisiana State Payment Plan";
              this.upsellProductData.isShow = true;

            } else if (this.confirmation.shippingState === 'MI' && orders.findIndex(x => x.Michigan == true) < 0) {
              this.upsellProductData.route = "/michigan-payment-plan";
              this.upsellProductData.messageDepartment = "Michigan Department of Revenue";
              this.upsellProductData.messageState = "Michigan";
              this.upsellProductData.productName = "Michigan State Payment Plan";
              this.upsellProductData.isShow = true;

            } else {
            }
          }
        } else {
          if (this.checkIsStatePaymentPlan() && orders.findIndex(x => x.isCalifornia == false &&
                  x.isNewJersey == false  && x.isIllinois == false && x.isIllinois == false && x.isMichigan == false ) == -1) {
            this.upsellProductData.route = "/payment-plan";
            this.upsellProductData.messageDepartment = "Internal Revenue Service";
            this.upsellProductData.messageState = "IRS";
            this.upsellProductData.productName = "IRS Payment Plan";
            this.upsellProductData.isShow = true;
          }
        }
        console.log(this.checkIsIRSPayment());
        console.log(this.upsellProductData);
      }
    }
  }

</script>
<style scoped>
  .order-details__panel {
    background-color: #f5f5f5;
    padding: 30px 80px;
    position: relative;
  }
  .order-details__panel .switch-state {
    position: absolute;
    top: 20px;
    right: 100px;
    width: 200px;
    padding: 5px 10px;
    border-radius: 10px;
    background-color: #ff8525;
  }

  .switch-state a {
    color: #fff;
  }

  .switch-state a:hover {
    color: #fff;
  }

  .info-block {
    margin-bottom: 20px;
  }
  .info-label {
    font-weight: 600;
    font-size: 12px;
  }
  .info-value {
    font-size: 16px;
  }
  p {
    line-height: 2em;
    font-weight: 400;
    font-family: 'Raleway', sans-serif;
  }
  .upsell-product{
     background-color: #f5f5f5;
     position: relative;
  }
  .border-line{
    border: 1px solid #0094be;
    width: 99%;
  }
  .border-arrow{
    margin-top: -0.7rem;
    color: #0094be;
    float: right;
    z-index:1
  }
   .upsell-product .icon {
    position: absolute;
    top: -0.4rem;
    left: -1.1rem;
    width: 60px;
  }
</style>
