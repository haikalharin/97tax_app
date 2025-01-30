<template>
  <div class="container">
    <div v-if="paymentPlan != null">
      <admin-input
        label="Total Debt"
        v-model="paymentPlan.totalDebt"
        @keyup="generatePaymentAmount"
        :disabled="!isEditMode"
        :error="paymentPlanTotalDabitError"
      ></admin-input>
      <!-- v-model="orderInput.ssn"-->
      <admin-input
        :value="getSSNValueWithUser(orderInput.ssn)"
        v-on:input="setSSN"
        label="Primary SSN:"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Payment Amount"
        v-model="paymentPlan.monthlyPayment"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isNewJersey"
        label="Secondary Phone"
        v-model="paymentPlan.secondaryPhone"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="!paymentPlan.isNewJersey"
        label="Married?"
        v-model="paymentPlan.married"
        type="boolean"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="!paymentPlan.isNewJersey"
        label="Spouse First Name"
        v-model="paymentPlan.spouseFirstName"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="!paymentPlan.isNewJersey"
        label="Spouse Last Name"
        v-model="paymentPlan.spouseLastName"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="!paymentPlan.isNewJersey"
        label="Spouse SSN"
        v-model="paymentPlan.spouseSsn"
        :disabled="!isEditMode"
      ></admin-input>

      <!-- Michigan Detail -->
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Bank Name"
                   v-model="orderInput.bankName"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Bank Address Line 1"
                   v-model="orderInput.bankAddress1"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Bank Address Line 2"
                   v-model="orderInput.bankAddress2"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Bank City"
                   v-model="orderInput.bankCity"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Bank State"
                   v-model="orderInput.bankState"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Bank Zip Code"
                   v-model="orderInput.bankZip"
                   :disabled="!isEditMode"
      ></admin-input>

      <admin-input v-if="paymentPlan.isMichigan"
                   label="Employer Name"
                   v-model="orderInput.employerName"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Employer Address Line 1"
                   v-model="orderInput.employerAddress1"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Employer Address Line 2"
                   v-model="orderInput.employerAddress2"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Employer City"
                   v-model="orderInput.employerCity"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Employer State"
                   v-model="orderInput.employerState"
                   :disabled="!isEditMode"
      ></admin-input>
      <admin-input v-if="paymentPlan.isMichigan"
                   label="Employer Zip Code"
                   v-model="orderInput.employerZip"
                   :disabled="!isEditMode"
      ></admin-input>

      <admin-input v-if="paymentPlan.isMichigan"
                   label="Assessment Numbers"
                   v-model="orderInput.assessmentNumber"
                   :disabled="!isEditMode"
      ></admin-input>

      <div v-for="(partner, index) in orderInput.partners">
        <h4 class="text-center">Michigan Partner {{index+1}}</h4>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner Name"
                     v-model="partner.partnerName"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner Title"
                     v-model="partner.partnerTitle"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Effective Date"
                     v-model="partner.partnerEffectiveDate"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner Address Line 1"
                     v-model="partner.partnerAddress1"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner Address Line 2"
                     v-model="partner.partnerAddress2"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner City"
                     v-model="partner.partnerCity"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner Zip Code"
                     v-model="partner.partnerZip"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner Phone Number"
                     v-model="partner.partnerPhoneNumber"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner SSN"
                     v-model="partner.partnerSsn"
                     :disabled="!isEditMode"
        ></admin-input>
        <admin-input v-if="paymentPlan.isMichigan"
                     label="Partner % Ownership"
                     v-model="partner.partnerPercentOwnership"
                     :disabled="!isEditMode"
        ></admin-input>
      </div>
      <!-- End of Michigan Detail -->

      <admin-input v-if="!paymentPlan.isNewJersey"
        label="Time to Call"
        v-model="paymentPlan.timeToCall"
        :disabled="!isEditMode || paymentPlan.isNewJersey"
      ></admin-input>
      <admin-input
        label="Payment Day of Month"
        v-model="paymentPlan.paymentDayOfMonth"
        :disabled="!isEditMode"
      ></admin-input>
       <admin-input
        label="Number of Months"
        @keyup="generatePaymentAmount"
        v-model="paymentPlan.paymentMonths"
        :disabled="!isEditMode"
        :error="paymentPlanMonthError"
      ></admin-input>
      <admin-input
       v-if="paymentPlan.isIllinois"
        label="Mobile"
        v-model="paymentPlan.mobile"
        mask="###-###-####"
        type="tel"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
       v-if="paymentPlan.isIllinois"
        label="Down Payment"
        v-model="paymentPlan.goodFaithPayment"
        :disabled="!isEditMode"
      ></admin-input>
       <admin-input
       v-if="paymentPlan.isIllinois"
        label="Remaining Amount"
        :value = "paymentPlan.totalDebt - paymentPlan.goodFaithPayment"
        :disabled="!isEditMode"
      ></admin-input>
       <admin-input
       v-if="!paymentPlan.isCalifornia"
        label="Business?"
        @input="selectIsOwedFromBusiness"
        v-model="paymentPlan.isOwedFromBusiness"
        type="boolean"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
       v-if="!paymentPlan.isCalifornia"
        label="EIN"
        v-model="paymentPlan.ein"
        mask="##-#######"
        type="tel"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
       v-if="!paymentPlan.isCalifornia"
        label="Business Name:"
        v-model="paymentPlan.businessName"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        v-if="!paymentPlan.isCalifornia && !paymentPlan.isIllinois && !paymentPlan.isNewJersey && !paymentPlan.isGeorgia"
        label="Payroll Deduction?"
        v-model="paymentPlan.payrollDeduction"
        type="boolean"
        :disabled="!isEditMode"
      ></admin-input>

       <div class="row" v-if="!isState()">
        <label class="col col-form-label font-weight-bold">Married Filing Separately?</label>
        <div class="col d-flex align-items-center">
          <input
            type="checkbox"
            :checked="getMarriedFilling(paymentPlan.married,paymentPlan.filingJointly)"
            :disabled="!isEditMode"
          >
          <label class="col col-form-label font-weight-bold">Yes</label>
          <input
            type="checkbox"
            :checked="!getMarriedFilling(paymentPlan.married,paymentPlan.filingJointly)"
            :disabled="!isEditMode"
          >
          <label class="col col-form-label font-weight-bold">No</label>
        </div>
       </div>
       <div v-if="!isState()">
        <div class="row">
          <label class="col col-form-label font-weight-bold">Processing Option</label>
          <div class="col d-flex align-items-center">
            <input
              type="checkbox"
              :checked="paymentPlan.processingSpeed === 'Standard'"
              :disabled="!isEditMode"
            >
            <label class="col col-form-label font-weight-bold">Standard</label>
            <input
              type="checkbox"
              :checked="paymentPlan.processingSpeed === 'Deluxe'"
              :disabled="!isEditMode"
            >
            <label class="col col-form-label font-weight-bold">Deluxe</label>
          </div>
        </div>

        <div
          class="row"
          v-if="orderInput.product === 'PaymentPlan' && !orderInput.isCalifornia && !orderInput.isNewJersey && !orderInput.isGeorgia && !orderInput.isIllinois && paymentPlan.processingSpeed == 'Deluxe' && envelopeTrackingNumber.length > 0 && orderInput.status === 'Completed'"
        >
          <label class="col col-form-label font-weight-bold">Delivery to IRS Tracking:</label>
          <div class="col d-flex align-items-center">
            <input
              class="form-control usps-tracking-input"
              readonly="readonly"
              type="text"
              @click="goToEnvelopeTracking()"
              :value="envelopeTrackingNumber">
          </div>
        </div>

        <admin-input
          v-if="!paymentPlan.isCalifornia && !paymentPlan.isIllinois && !paymentPlan.isNewJersey && !paymentPlan.isGeorgia"
          label="Change of Address?"
          v-model="orderInput.hasOldAddress"
          type="boolean"
          :disabled="!isEditMode"
        ></admin-input>
        <admin-input
          v-if="orderInput.hasOldAddress"
          label="Old Address 1"
          v-model="orderInput.oldAddress1"
          :disabled="!isEditMode"
        ></admin-input>
        <admin-input
          v-if="orderInput.hasOldAddress"
          label="Old Address 2"
          v-model="orderInput.oldAddress2"
          :disabled="!isEditMode"
        ></admin-input>
        <admin-input
          v-if="orderInput.hasOldAddress"
          label="Old City"
          v-model="orderInput.oldCity"
          :disabled="!isEditMode"
        ></admin-input>
        <admin-input
          v-if="orderInput.hasOldAddress"
          label="Old State"
          v-model="orderInput.oldState"
          :disabled="!isEditMode"
        ></admin-input>
        <admin-input
          v-if="orderInput.hasOldAddress"
          label="Old Zip"
          v-model="orderInput.oldZip"
          :disabled="!isEditMode"
        ></admin-input>
       </div>

      <admin-input
       v-if="paymentPlan.isIllinois"
        label="Illinois Account ID"
        v-model="paymentPlan.illinoisAccountId"
        mask="####-####"
        type="tel"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
       v-if="paymentPlan.isIllinois"
        label="DBA"
        v-model="paymentPlan.dba"
        :disabled="!isEditMode"
      ></admin-input>
    </div>
  </div>
</template>

<script>
  import {AXIOS}             from '../../scripts/http-common';
  import {SiteUtils}         from '../../scripts/site-common';
  import {paymentPlansConfig,
          paymentPlansFlags} from '../../../config/plans-config';
  import _                   from 'lodash';
  import {methods}           from '../../shared/methods';

  export default {
    props: {
      orderInput: Object,
      isEditMode: Boolean,
      loggedInUser:Object
    },
    data() {
      return {
        disableButtons: false,
        paymentPlan: null,
        paymentPlanConfig:{},
        paymentPlanMonthError:"",
        paymentPlanTotalDabitError:"",
        envelopeTrackingNumber: ""
      }
    },
    methods: {
      savePaymentPlan() {
        AXIOS.put(`/admin/paymentPlan`, this.paymentPlan)
          .then(() => null)
          .catch(e => {
            console.log(e);
          });
      },
      isState(){
        return this.paymentPlan.isNewJersey || this.paymentPlan.isCalifornia || this.paymentPlan.isGeorgia || this.paymentPlan.isIllinois
      },
      selectIsOwedFromBusiness(isBusiness){
          if(!isBusiness){
            this.paymentPlan.businessName = null;
            this.paymentPlan.ein = null;
            this.paymentPlan.illinoisAccountId = null;
            this.paymentPlan.dba = null;
          }
      },
      getMarriedFilling(married,fillingJointly){
        return married == true && fillingJointly == "No" ? true : false
      },
      generatePaymentAmount() {
        const totalDebt =  this.paymentPlan.totalDebt != undefined ? parseFloat(this.paymentPlan.totalDebt) : 0;
        this.paymentPlanTotalDabitError = totalDebt >= this.paymentPlanConfig.minAmount && totalDebt <= this.paymentPlanConfig.maxAmount ?
          "":`This amount must be between $${this.paymentPlanConfig.minAmount} and $${this.paymentPlanConfig.maxAmount} to qualify.`

        const paymentMonths = this.paymentPlan.paymentMonths != undefined ? parseFloat(this.paymentPlan.paymentMonths) : 0;
        this.paymentPlanMonthError = paymentMonths >= this.paymentPlanConfig.minMonth && paymentMonths <= this.paymentPlanConfig.maxMonth ?
          "":`This Month must be between ${this.paymentPlanConfig.minMonth} and ${this.paymentPlanConfig.maxMonth} to qualify.`

        let paymentPlan = SiteUtils.calculatePaymentPlan(this.paymentPlan.totalDebt, this.paymentPlan.paymentMonths);
        if (paymentPlan.paymentAmount === 'N/A') {
          this.paymentPlan.monthlyPayment = 25;
        } else {
          this.paymentPlan.monthlyPayment = Math.ceil( paymentPlan.paymentAmount );
        }
      },
      setPaymentPlanConfig(){
      const paymentPlansFlagsKeys =  _.keys(paymentPlansFlags);
      const flags = _.pick(this.paymentPlan, paymentPlansFlagsKeys);
      let paymentPlanKey = paymentPlansFlags.isIrs;
        for(var f in flags){
            if (flags[f] == true) {
              paymentPlanKey = paymentPlansFlags[f];
          }
        }
        this.paymentPlanConfig = paymentPlansConfig[paymentPlanKey];
      },
      setSSN(value){
        if(value && !value.includes('X')){
          this.orderInput.ssn = value;
        }
      },
      getSSNValueWithUser(ssnValue){
        if(this.loggedInUser && this.loggedInUser.userType == 'StandardUser' && this.loggedInUser.ssnPrivacy && ssnValue){
            return "XXX-XX-".concat(ssnValue.substring(ssnValue.length - 4));
        }
        else if(this.loggedInUser && this.loggedInUser.userType == 'StandardUser' && this.loggedInUser.ssnPrivacy == false && ssnValue){
           return ssnValue;
        }
        else if(this.loggedInUser && this.loggedInUser.userType == "admin"){
            return ssnValue;
        }
        else{
          return "";
        }
       //need to varify
       // self.loggedInUser.userType=='StandardUser' && !self.loggedInUser.ssnPrivacy?"XXX-XX-XXXX":'';
      },
      goToTracking (){
        if(this.orderInput && this.orderInput.trackingNumber) methods.uspsTracking(this.orderInput.trackingNumber)
      },
      goToEnvelopeTracking() {
        if(this.envelopeTrackingNumber) methods.uspsTracking(this.envelopeTrackingNumber)
      }
    },
    mounted() {
      let self = this;
      this.disableButtons = true;
      AXIOS.get(`/admin/paymentPlan/orderNum/${this.orderInput.orderNum}`)
        .then(response => {
          self.$set(self, 'paymentPlan', response.data);
          self.setPaymentPlanConfig();
          self.disableButtons = false;
          if (self.orderInput.status == 'Completed' && response.data.processingSpeed === 'Deluxe') {
            AXIOS.get(`/admin/envelopeTrackingNumber/orderNum/${this.orderInput.orderNum}`).then(res => {
              if (res.status == 200 && res.data.length > 0) {
                self.envelopeTrackingNumber = res.data[0].trackingNumber;
              }
            });
          } else {
            self.envelopeTrackingNumber = "";
          }
        })
        .catch(e => {
          console.log(e);
          this.disableButtons = false;
        })
    }
  }
</script>
