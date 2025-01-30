<template>
  <page class="home-page" :hideTopNav="true" :navType="'tax-liens-order-form'">
    <div class="container mx-auto">
      <div class="row pt-4 mb-4">
        <router-link to="/">
          <img src="@/assets/LogoFooter.png">
        </router-link>
      </div>

      <div class="row">
        <div class="col-12 col-md-2 mt-3 mx-md-auto">
          <img class="mx-auto d-block" src="@/assets/qualifyGreen.png">
        </div>
        <div class="col-12 col-md-10 my-4 mx-md-auto">
          <h3 class="mt-3">
            You may qualify to have your <span style="color: #2896C5;"> tax lien removed.</span>
            Let's find the best removal option for you below.
          </h3>
        </div>
      </div>

      <div class="row">
        <div class="col-12">
          <h4>Removal Options</h4>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <label :class="error.taxLienRemediationType ? 'text-danger' : ''">
            <input
              type="radio"
              value="currentPaymentPlan"
              v-model="orderInfo.taxLienRemediationType">
            I am currently on a payment plan or installment agreement with the IRS
            <font-awesome-icon
              v-b-popover.hover.bottom="'Currently on a payment plan means your payment plan has been accepted by the IRS, and you have made at least your first monthly payment.'"
              :icon="['far', 'question-circle']"
              style="color: #2896C5"></font-awesome-icon>
          </label>
        </div>
      </div>
      <div class="row" v-if="orderInfo.taxLienRemediationType === 'currentPaymentPlan'">
        <div class="col-11 offset-1" :class="error.taxLienAutomaticDebit ? 'text-danger':''">
          Are payments currently debited automatically from your checking account?
        </div>
        <div class="col-11 offset-1">
          <label>
            <input
              type="radio"
              name="automaticDebit"
              v-model="orderInfo.taxLienAutomaticDebit"
              :value="true"
            > Yes
          </label>
          <label>
            <input
              type="radio"
              name="automaticDebit"
              v-model="orderInfo.taxLienAutomaticDebit"
              :value="false"
            > No
          </label>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <label :class="error.taxLienRemediationType ? 'text-danger' : ''">
            <input
              type="radio"
              value="removeForSale"
              v-model="orderInfo.taxLienRemediationType"
              v-on:input="clearRemediationDescription">
            I need the lien removed so I can pay off at least part of my tax debt
            <font-awesome-icon
              v-b-popover.hover.bottom="'If your lien is preventing the sale of an asset that will help pay off your debt, select this option. Example: You plan to sell your asset that has a lien on it (boat, car, property, business) to help at least in part to pay off your tax debt, but you need the lien removed to sell it.'"
              :icon="['far', 'question-circle']"
              style="color: #2896C5"></font-awesome-icon>
          </label>
        </div>
      </div>
      <div class="row" v-if="orderInfo.taxLienRemediationType === 'removeForSale'">
        <div class="col">
          <div>
            Please briefly explain how removing the lien will help you pay off your tax debt.
          </div>
          <textarea
            :class="['form-control', error.taxLienRemediationDescription ? 'is-invalid' : '']"
            v-model="orderInfo.taxLienRemediationDescription"></textarea>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <label :class="error.taxLienRemediationType ? 'text-danger' : ''">
            <input
              type="radio"
              value="removeForMoreIncome"
              v-model="orderInfo.taxLienRemediationType"
              v-on:input="clearRemediationDescription">
            Removing the lien would help increase my income and would help me to pay off my tax debt
            <font-awesome-icon
              v-b-popover.hover.bottom="'Example: You use your vehicle or property for work, and need to sell it, replace it, or exchange it to a different vehicle or property for work purposes. Having the lien removed would improve your ability to produce an income and improve your ability to pay off your tax debt. Example #2: You\'ve been offered a job, promotion, or raise but are not eligible unless the tax lien is removed.'"
              :icon="['far', 'question-circle']"
              style="color: #2896C5"></font-awesome-icon>
          </label>
        </div>
      </div>
      <div class="row" v-if="orderInfo.taxLienRemediationType === 'removeForMoreIncome'">
        <div class="col">
          <div>
            Please briefly explain how removing the lien will help increase your income.
          </div>
          <textarea
            :class="['form-control', error.taxLienRemediationDescription ? 'is-invalid' : '']"
            class="form-control"
            v-model="orderInfo.taxLienRemediationDescription"></textarea>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <label :class="error.taxLienRemediationType ? 'text-danger' : ''">
            <input
              type="radio"
              value="paidInFull"
              v-model="orderInfo.taxLienRemediationType">
            I've paid off all my tax debt in full
          </label>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <label :class="error.taxLienRemediationType ? 'text-danger' : ''">
            <input
              type="radio"
              value="needWithdrawnStatus"
              v-model="orderInfo.taxLienRemediationType">
            My tax lien is in "released" or "paid" status, and now I need it "withdrawn"
          </label>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <label :class="error.taxLienRemediationType ? 'text-danger' : ''">
            <input
              type="radio"
              value="agedTenYears"
              v-model="orderInfo.taxLienRemediationType">
            My tax lien is for tax year(s) 2007 or earlier
            <font-awesome-icon
              v-b-popover.hover.bottom="'After 10 years, your tax lien will have expired. You still need to apply to have it withdrawn.'"
              :icon="['far', 'question-circle']"
              style="color: #2896C5"></font-awesome-icon>
          </label>
        </div>
      </div>

      <div class="row mt-3">
      <div class="col-md-6 col-sm-12">
        <label>First Name</label>
        <input
          :style= "error.billingFirstName ? 'margin-bottom: 2.5rem;' : 'margin-bottom: 1rem;'"
          label="First Name"
          placeholder="First Name"
          v-model="orderInfo.billingFirstName"
          :error="error.billingFirstName"
          class="form-control"
          v-on:keypress="isLetter($event)"
        />

        <div v-if="error.billingFirstName" class="text-danger"  style="margin-top: -5%;margin-left: 3%;">
          {{error.billingFirstName}}
        </div>

      </div>
      <div class="col-md-6 col-sm-12">
        <label>Last Name</label>
        <input
        label="Last Name"
        placeholder="Last Name"
        v-model="orderInfo.billingLastName"
        :error="error.billingLastName"
        class="form-control"
        v-on:keypress="isLetter($event)"
        />

        <div v-if="error.billingLastName" class="text-danger pl-3">
          {{error.billingLastName}}
        </div>

 </div>
        <div class="col-sm-12 col-md-6 ml-3">
          <div class="mt-2 mb-2">
            <strong>Tax Lien Serial Number</strong>
          </div>
          <div>
            <label>
              <input type="radio" name="haveSerialNumber" :value="true" v-model="hasSerialNumber">
              I have the Serial Number on the tax lien notice
            </label>
          </div>
          <div>
            <label>
              <input type="radio" name="haveSerialNumber" :value="false" v-model="hasSerialNumber">
              I don't have this
            </label>
          </div>
        </div>
      </div>

      <div class="row" v-if="hasSerialNumber">
         <div class="col-sm-12 col-md-6">
        <form-input-field
          label="Tax Lien Serial Number"
          placeholder="Serial Number"
          v-model="orderInfo.serialNumber"
          :error="error.serialNumber"
        ></form-input-field>
         </div>
      </div>
      <div class="row mt-2 mb-4  ">
        <div class="col-sm-12 col-md-6 ml-3 mt-2 mb-2">
          <span>
            Is the tax lien in the name of a business?
          </span>
          <label class="pl-2">
            <input
              type="radio"
              name="taxLienIsBusiness"
              :value="false"
              v-model="orderInfo.taxLienIsBusiness"
              :error="error.taxLienIsBusiness"
            >No
          </label>
          <label class="pl-2">
            <input
              type="radio"
              name="taxLienIsBusiness"
              :value="true"
              v-model="orderInfo.taxLienIsBusiness"
              :error="error.taxLienIsBusiness"
            >Yes
          </label>
        </div>
      </div>

      <div class="row" v-if="!orderInfo.taxLienIsBusiness">
       <div class="col-sm-12 col-md-6">
        <form-input-field
          label="Your SSN"
          placeholder="000-00-0000"
          v-model="orderInfo.ssn"
          :error="error.ssn"
          :name="'ssn'"
        ></form-input-field>
       </div>
      </div>

      <div class="row" v-if="orderInfo.taxLienIsBusiness">
        <div class="col-sm-12 col-md-6">
        <form-input-field
          label="Name of Business on Tax Lien"
          placeholder="Name of Business on Tax Lien"
          v-model="orderInfo.taxLienBusinessName"
          :error="error.taxLienBusinessName"
        ></form-input-field>
        </div>
        <div class="col-sm-12 col-md-6">
        <form-input-field
          label="Business EIN"
          placeholder="00-0000000"
          v-model="orderInfo.taxLienBusinessEin"
          :error="error.taxLienBusinessEin"
          :name="'EIN'"
        ></form-input-field>
        </div>
      </div>

      <div class="row">
        <div class="col-sm-12 col-md-6">
           <div class="col-12 ">
              <label> Phone Number </label>
              <input :class="['form-control', error.billingPhone ? 'is-invalid' : '']" :error="error.billingPhone" class="mb-10"
              v-model="orderInfo.billingPhone"  @keypress="onlyNumberValidation" v-mask="'###-###-####'" inputmode="numeric" placeholder="Phone Number" type="tel" >
              <div v-if="error.billingPhone" class="text-danger" style="margin-top: 10px;">
              {{error.billingPhone}}
            </div>
           </div>

        <!-- <form-input-field
          v-model="orderInfo.billingPhone"
          :error="error.billingPhone"
          label="Phone Number1"
        ></form-input-field> -->
        </div>

        <div class="col-sm-12 col-md-6">

          <form-input-field
            v-model="orderInfo.email"
            :error="error.email"
            label="Email Address"
            placeholder="Email Address"
          ></form-input-field>
          <div v-if="error.email" class="col-12 col-sm-6 text-danger" style="margin-top: 10px;">
            {{error.email}}
          </div>

        </div>

      </div>
      <div class="row">
        <div class="col">
          <h5>Mailing Address</h5>
        </div>
      </div>
      <div class="row mt-4">

       <div class="col-md-6 col-sm-12 form-group">

            <div style="margin-bottom: -6%;margin-left: 3%;display: flex;"><input type="checkbox" v-on:click="orderInfo.shippingAddress1='',orderInfo.shippingAddress2=''" name="addressType" :value="true" v-model="addressType" style="z-index:100" > <p style="margin-top: -3px; margin-left: 4px;"> I have PO. Box</p></div>
         <vue-google-autocomplete  style="width:94%; margin-top: 5%; margin-left: 3%;" id="g_address" classname="form-control"
         v-show="!addressType && showGoogleTextBox" placeholder="Street Address" @placechanged="getAddressData" @blur="extractAddress"  @inputChange="setAddress" country="us"></vue-google-autocomplete>
           <input class="form-control "  style="width:94%; margin-top: 5%; margin-left: 3%;"  v-if="!showGoogleTextBox && !addressType"
           v-model="orderInfo.shippingAddress1" :error="error.shippingAddress1 || error.addressType"  @focusin="changeToGoogle"/>
            <!--<form-input-field style="width: 93%; max-width:unset;" v-model="orderInfo.shippingAddress1" v-if="addressType" :error="error.shippingAddress1 || error.addressType" :placeholder="PO Box"></form-input-field>-->
          <div class="d-flex" style="align-items: baseline;"> <div v-if="addressType" style="width:110px; margin-left:3%"> PO. BOX #</div>
          <input class="form-control po-box"  v-if="addressType " v-model="orderInfo.shippingAddress1" :error="error.shippingAddress1 || error.addressType"  /></div>

        </div>

      <div class="col-md-3 col-sm-12" v-if="!addressType">
        <form-input-field maxlength="12"  v-model="orderInfo.shippingAddress2"  :error="error.shippingAddress2" label="Apt/Unit #"></form-input-field>
      </div>
          <!--<div class="col-12 col-sm-3">
          <label class="ml-1">Apt/Unit #</label>
          <input class="form-control" type="text" placeholder="Apt/Unit #" v-model="orderInfo.shippingAddress2" :error="error.shippingAddress2" />
          <form-input-field v-model="orderInfo.shippingAddress2" :error="error.shippingAddress2" label="Apt/Unit #"></form-input-field>
          </div>-->
      <div class="col-sm-12 form-group" :class="addressType ? 'col-md-6':'col-md-3'">
        <div class="col-sm-12">
          <label>City</label>
          <input
            v-model="orderInfo.shippingCity"
            class="form-control"
            :error="error.shippingCity"
            label="City"
            v-on:keypress="isLetter($event)"
          />
        </div>
      </div>
       </div>
       <div class="row">
          <div class="col-12 col-sm-6">
            <span v-if="error.shippingAddress1 && !error.addressType && !addressType" class="w-100 text-danger pl-3">
              {{error.shippingAddress1}}
            </span>
            <span v-if="error.addressType && !error.shippingAddress1 && addressType" class="w-100 text-danger pl-3">
              {{error.shippingAddress1}}
            </span>
          </div>
          <div class="col-12 col-sm-3">
            <span v-if="error.shippingAddress2" class="text-danger mt-1">
              This box is for your APT/UNIT/STE number only.
            </span>
          </div>
          <div class="col-12 col-sm-3">
            <span v-if="error.shippingCity" class="text-danger mt-1">
              This box is for your City.
            </span>
          </div>
      </div>

      <div class="row mb-4">
         <div class="col-md-6 col-sm-12" >
        <state-select-field
          v-model="orderInfo.shippingState"
          :error="error.shippingState"
          label="State"
        ></state-select-field>
         </div>
         <div class="col-md-6 col-sm-12" >
          <div class="col-12 ">
            <label> Zip Code </label>
            <input :class="['form-control', error.shippingPostalCode ? 'is-invalid' : '']" :error="error.shippingPostalCode" class="mb-10"
            v-model="orderInfo.shippingPostalCode" placeholder="Zip Code" v-mask="'#####'" @keypress="onlyNumberValidation" inputmode="numeric" type="text" >

            <span v-if="error.shippingPostalCode" class="text-danger mt-1">
              {{error.shippingPostalCode}}
            </span>
        </div>
        <!-- <form-input-field type="tel" v-mask="'#####'" v-model="orderInfo.shippingPostalCode" :error="error.shippingPostalCode" :maxlength="5" label="Zip Code"></form-input-field>  -->
         </div>
      </div>

    <div class="row mt-4 mb-4">
 <div class="col-md-6 col-sm-12 ml-3" >
           Billing Address Same As Mailing?
        <label class="pl-2">
          <input
            type="radio"
            name="sameAsMailing"
            :value="true"
            v-model="shippingAddressSame">Yes
        </label>

        <label class="pl-2">
          <input
            type="radio"
            name="sameAsMailing"
            :value="false"
            v-model="shippingAddressSame">No
        </label>
 </div>
      <!-- </div> -->
    </div>
    <div class="row" v-if="!shippingAddressSame">
      <div class="col-12">
        <h4>Billing Address</h4>
      </div>
      <form-input-field v-model="orderInfo.billingAddress1" :error="error.billingAddress1" label="Address Line 1"></form-input-field>
      <form-input-field v-model="orderInfo.billingAddress2" :error="error.billingAddress2" label="Address Line 2"></form-input-field>
      <form-input-field v-model="orderInfo.billingCity" :error="error.billingCity" label="City"></form-input-field>
      <state-select-field v-model="orderInfo.billingState" :error="error.billingState" label="State"></state-select-field>
         <div class="col-12 col-md-6">
          <label >Zip Code</label>
          <input :class="['form-control', error.billingPostalCode ? 'is-invalid' : '']" :error="error.billingPostalCode" class="mb-10"
          v-model="orderInfo.billingPostalCode" placeholder="Zip Code" @keypress="onlyNumberValidation" v-mask="'#####'" inputmode="numeric" type="text" >
        </div>
      <!-- <form-input-field v-mask="'#####'" v-model="orderInfo.billingPostalCode" :maxlength="5" :error="error.billingPostalCode" type="tel" label="Zip Code"></form-input-field> -->
    </div>

      <div class="row m-auto">
        <div class="col-12 col-sm-4 pt-4">
          <div class="mx-auto mt-0 mt-sm-5" style="text-align: center">
            <div>
              <a href="https://verify.authorize.net/anetseal/?pid=d7332b5b-b0c5-4072-9237-bef8a02352ae&amp;rurl=https%3A//www.97tax.com" onmouseover="window.status='http://www.authorize.net/'; return true;" onmouseout="window.status=''; return true;" onclick="window.open('https://verify.authorize.net/anetseal/?pid=d7332b5b-b0c5-4072-9237-bef8a02352ae&amp;rurl=https%3A//www.97tax.com','AuthorizeNetVerification','width=600,height=430,dependent=yes,resizable=yes,scrollbars=yes,menubar=no,toolbar=no,status=no,directories=no,location=yes'); return false;" target="_blank">
                <img src="https://verify.authorize.net/anetseal/images/secure90x72.gif" width="90" height="72" border="0" alt="Authorize.Net Merchant - Click to Verify">
              </a>
            </div>
            <div>
              <a href="https://usa.visa.com/pay-with-visa/featured-technologies/verified-by-visa.html" target="_blank">
                <img src="./../assets/logo_vbv.gif">
              </a>
            </div>
            <div class="mt-4">
              <a href="https://www.mastercard.us/en-us/consumers/payment-technologies/securecode.html" target="_blank">
                <img src="./../assets/secureCode_logo.gif">
              </a>
            </div>
          </div>
        </div>
        <div class="col-12 col-sm-8 mt-4">
          <div class="row">
            <div class="col-12">
              <p class="float-right font-weight-bold" style="font-size: .75em">Total</p>
            </div>
          </div>
          <div class="row mt-3">
            <div class="col-6">
            <span class="float-right text-right">
              One-Time Enrollment Fee
            </span>
            </div>
            <div class="col-6">
            <span class="otef-helpbubble" v-b-popover.hover.bottom="'This is our one-time fee for preparing your tax lien removal application. There are no other fees from 97tax, ever.'">
              <img src="./../assets/HelpBubble.png"/>
            </span>
              <p class="float-right">$97.00</p>
            </div>
            <div class="col-6">
              <span class="float-right">Subtotal</span>
            </div>
            <div class="col-6">
              <p class="float-right">$97.00</p>
            </div>
            <div class="col-6">
              <span class="float-right">Total</span>
            </div>
            <div class="col-6">
              <p class="float-right">$97.00</p>
            </div>
          </div>

          <div class="row">
            <div class="col-6">
              <p>Credit Card</p>
            </div>
            <div class="col-6 float-right">
              <img class="float-right" src="@/assets/Mastercard.png">
              <img class="float-right" src="@/assets/Visa.png">

            </div>
          </div>

          <div class="row bg-grey mt-1">
            <form-input-field
              classes="col-12 mt-4 mb-3"
              v-model="orderInfo.cardNumber"
              :error="error.cardNumber"
              label="Card Number"
              placeholder="**** **** **** ****"
              :name="'cardNumber'"
            ></form-input-field>
          </div>
          <div class="row bg-grey">

            <input-month-dropdown
              v-model="orderInfo.cardExpMonth"
              :error="error.cardExpMonth"
              label="Card Expiration Month"
              placeholder="MM"
            ></input-month-dropdown>

            <input-year-dropdown
              v-model="orderInfo.cardExpYear"
              :error="error.cardExpYear"
              label="Card Expiration Year"
              placeholder="YYYY"
            ></input-year-dropdown>

          </div>
          <div class="row bg-grey">
            <div class="col col-lg-3">
              <label>Card Code</label>
              <button class="btn btn-sm btn-link p-0 d-inline" @click="$refs.cardCodeModal.show()">
                <img src="./../assets/HelpBubble.png"/>
              </button>
            </div>
            <div class="col col-lg-3 form-group">
              <input
                :class="['form-control', error.cardCvc ? 'is-invalid' : '']"
                v-model="orderInfo.cardCvc"
                label="Card Code"
                v-mask="'###'"
                placeholder="CVC">
            </div>
          </div>
          <div class="row" v-for="message in errorMessages" :key="message">
            <div class="col">
              <b-alert variant="danger"
                       dismissible
                       :show="message.show"
                       @dismissed="message.show=false"
                       v-html="message.text">
              </b-alert>
            </div>
          </div>
          <div class="row mt-4">
            <div class="col-12 col-md-7 col-lg-8 mt-2 mx-0">
              <p>Your personal data will be used to process your order, support your experience throughout this website, and for other purpose described in our privacy policy.</p>
            </div>
            <div class="col-12 col-md-5 col-lg-4 mt-2 mx-0">
              <div class="text-center" v-if="isProcessing">
                <font-awesome-icon :icon="['fa', 'cog']" spin size="3x" />
              </div>
              <div v-if="!isProcessing">
                <button class="btn btn-primary float-right" v-on:click="postOrderInfo()">PROCESS PAYMENT</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <form name="acsForm" method="POST" :action="authorizeResult.acsUrl">
      <input type="hidden" name="PaReq" :value="authorizeResult.paReq">
      <input type="hidden" name="TermUrl" :value="authorizeResult.termUrl">
      <input type="hidden" name="MD" :value="authorizeResult.md">
    </form>

    <b-modal ref="errorsModal" title="Form Errors" ok-only>
      <p class="my-4">Oops! Please check the fields outlined in red.</p>
    </b-modal>

    <b-modal ref="cardCodeModal" title="Card Code" ok-only>
      <p class="my-4">
        The "Card Code" is the 3 digit number on the back of your credit card like in the picture below.
      </p>
      <img src="@/assets/whatsthis.jpg" style="max-width: 100%">
    </b-modal>
    <SalesWidget :phone_number="salesPhoneNumber" v-if="salesPhoneNumber" />
  </page>
</template>
<!-- sharea sale tag -->
<script>
  var shareasaleSSCID=shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e,a,r,s,t){
      if(e&&a)
            {var o,n=s?"; path="+s:"",i=t?"; domain="+t:"",l="";r&&((o=new Date).setTime(o.getTime()+r),l="; expires="+o.toUTCString()),
        document.cookie=e+"="+a+l+n+i
      }}
        function shareasaleGetParameterByName(e,a)
        {a||(a=window.location.href),e=e.replace(/[\[\]]/g,"\\$&");
        var r=new RegExp("[?&]"+e+"(=([^&#]*)|&|#|$)").exec(a);
return r?r[2]?decodeURIComponent(r[2].replace(/\+/g," ")):"":null}
shareasaleSSCID&&shareasaleSetCookie("shareasaleSSCID",
shareasaleSSCID,94670778e4,"/");
</script>
<script>
  import {AXIOS} from '../scripts/http-common'
  import {SiteUtils} from '../scripts/site-common'
  import VueGoogleAutocomplete from 'vue-google-autocomplete'
  import SalesWidget from "./../components/SalesWidget.vue";
  import moment from "moment";

  export default {
    components: { VueGoogleAutocomplete, SalesWidget },
    data(){
      return {
        salesPhoneNumber: null,
        shippingAddressSame: true,
        isProcessing: false,
        hasSerialNumber: false,
        errorMessages: [],
        addressType:false,
        jwtData: null,
        showGoogleTextBox:true,
        orderNumber:'',

        orderInfo: {
          billingFirstName: "",
          billingLastName: "",
          billingPhone: "",
          shippingFirstName: "",
          shippingLastName: "",
          shippingPhone: "",
          email: "",

          billingAddress1: "",
          billingAddress2: "",
          billingCity: "",
          billingState: "",
          billingPostalCode: "",


          shippingAddress1: "",
          shippingAddress2: "",
          shippingCity: "",
          shippingState: "",
          shippingPostalCode: "",

          amount: "9700",
          taxLienIsBusiness: false,
          taxLienType: "",
          taxLienBusinessName: "",
          taxLienBusinessEin: "",
          taxLienRemediationType: "",
          taxLienRemediationDescription: "",
          taxLienAutomaticDebit: null,
          serialNumber: "",
          isCalifornia: false,
          isNewJersey: false,
          isGeorgia: false,
          isIllinois:false,
          ssn: "",
          married: "false",
          spouseFirstName: "",
          spouseLastName: "",
          spouseSsn: "",
          timeToCall: "",
          paymentDayOfMonth: "",

          cardNumber: "",
          cardExpMonth: "",
          cardExpYear: "",
          cardCvc: "",

          product: "TaxLienRemoval",
          responseJwt: null,
          failureReason: ""
        },

        error: {
          billingFirstName: "",
          billingLastName: "",
          billingPhone: "",
          email: "",

          addressType:false,
          billingAddress1: "",
          billingAddress2: "",
          billingCity: "",
          billingState: "",
          billingPostalCode: "",

          shippingAddress1: "",
          shippingAddress2: "",
          shippingCity: "",
          shippingState: "",
          shippingPostalCode: "",

          taxLienType: "",
          taxLienBusinessName: "",
          taxLienBusinessEin: "",
          taxLienRemediationType: "",
          taxLienRemediationDescription: "",
          taxLienAutomaticDebit: "",
          serialNumber: "",

          ssn: "",

          cardNumber: "",
          cardExpMonth: "",
          cardExpYear: "",
          cardCvc: ""
        },
          orderInfoTemp:{},
        authorizeResult: {
          acsUrl: "",
          paReq: "",
          termUrl: "",
          md: ""
        }
      }
    },
    methods: {
      initializeSalesWidget() {
        AXIOS.get(`/admin/salesList`).then(res=> {
            if (res.status == 200) {
              let list = res.data;
              let index = list.findIndex(it => {
                let condition_1 = it.onOff === true && it.product == 'Tax Lien Removal';
                let condition_2 = false;
                if (it.startTime && it.endTime) {
                  let currentTimeStamp = new Date().getTime();
                  let startTimeStamp = new Date(it.startTime).getTime();
                  let endTimeStamp = new Date(it.endTime).getTime();
                  condition_2 = currentTimeStamp > startTimeStamp && currentTimeStamp < endTimeStamp;
                }

                let condition_3 = true;
                let currentTime = moment().tz("America/New_York").format("LT");
                if (!!it.startDaily) {
                  let fromTime = moment(it.startDaily).tz("America/New_York").format("LT");
                  if (fromTime != "12:00 AM") {
                    condition_3 = condition_3 && moment(fromTime, "LT").isBefore(moment(currentTime, "LT"));
                  }
                }
                if (!!it.endDaily) {
                  let endTime = moment(it.endDaily).tz("America/New_York").format("LT");
                  if (endTime != "12:00 AM") {
                    condition_3 = condition_3 && moment(currentTime, "LT").isBefore(moment(endTime, "LT"));
                  }
                }

                return condition_1 && condition_2 && condition_3;
              });
              if (index > -1) {
                this.salesPhoneNumber = list[index].phoneNumber;
              }
            }
        });
      },

      isLetter(e) {
            if(e.keyCode === 32) return true;
            let char = String.fromCharCode(e.keyCode);
            if(/^[A-Za-z]+$/.test(char)) return true;
            else e.preventDefault();
          },
      onlyNumberValidation($event){
             let keyCode = ($event.keyCode ? $event.keyCode : $event.which);
          if (keyCode < 48 || keyCode > 57) {
                   $event.preventDefault();
                }
          },
      initializeCardinal() {
        let self = this;
        if (!window.cardinalIsInitialized) {
          Cardinal.configure({
            logging: {
              debug: 'verbose',
              timeout: 10000
            }
          });
          window.cardinalIsInitialized = true;
        }

        Cardinal.off('payments.setupComplete');
        Cardinal.off('payments.validated');

        Cardinal.on('payments.setupComplete', () => {
          Cardinal.start('cca', self.jwtData.payload);
        });

        Cardinal.on('payments.validated', (data, jwt) => {
          if (data.ActionCode === 'SUCCESS' || data.ActionCode === 'NOACTION') {
            self.$set(self.orderInfo, 'responseJwt', jwt);

            AXIOS.post(`/ecommerce/authorize`, self.orderInfo)
              .then(response => {
                if (response.data.success) {
                  let orderForPdf = Object.assign({}, self.orderInfoTemp);
                  orderForPdf.orderNumber = self.orderNumber;
                  AXIOS.post('/pdforderversion/refreshOrder',orderForPdf).then(res=>{
                    window.location.href = '/order-confirmation/' + response.data.correlationId;
                  }).catch(error=>{
                    console.log("pdf error",error)
                    window.location.href = '/order-confirmation/' + response.data.correlationId;
                  })
                } else {
                  this.orderInfoTemp.failureReason = response.data.message;
                  self.errorMessages.push({
                      show: true,
                      text: response.data.message
                  });
                  this.initializeCardinal();
                  self.isProcessing = false;
                }
              })
              .catch(e => {
                if (e.response.status === 409) {
                    this.orderInfoTemp.failureReason = "Possible duplicate";
                    self.errorMessages.push({
                        show: true,
                        text: "Sorry, it looks like you may have already placed an order with us! Please <a href='/contact-us'>contact us</a> for help."
                    });
                    self.isProcessing = false;
                } else {
                    this.orderInfoTemp.failureReason = "Error!";
                    self.errorMessages.push({
                        show: true,
                        text: "Unable to make purchase. Please check your payment details or try a different payment method."
                    });
                    this.initializeCardinal();
                    self.isProcessing = false;
                }
              })
          } else {
            this.orderInfoTemp.failureReason = "3D Secure Failed: Your card was unable to be authenticated. Please check your information or use a different card and try again.";
            self.errorMessages.push({
                show: true,
                text: "3D Secure Failed: Your card was unable to be authenticated. Please check your information or use a different card and try again."
            });
            this.initializeCardinal();
            self.isProcessing = false;

            if( this.orderInfoTemp.failureReason){
                this.postFailureReason()
            }
          }
        });
      },
      postFailureReason() {
        AXIOS.put(`/ecommerce/fail`, this.orderInfoTemp)
          .then(response => {
          })
          .catch(error => {
            console.log(error);
          });
      },
      validateForm() {
        let required = ['billingFirstName', 'billingLastName', 'billingPhone', 'email', 'shippingAddress1',
          'shippingCity', 'shippingState', 'shippingPostalCode',
          'cardNumber', 'cardExpMonth', 'cardExpYear', 'cardCvc','addressType', 'taxLienRemediationType'];

        let hasErrors = SiteUtils.formErrors(required, this.orderInfo, this.error);

        this.error.ssn = false;
        this.error.addressType=false;
        this.error.taxLienBusinessEin = false;
        this.error.taxLienBusinessName = false;
        this.error.taxLienAutomaticDebit = false;
        this.error.shippingAddress1=false;
        this.error.shippingAddress2=false;
        this.error.billingAddress1=false;
        this.error.billingAddress2=false;
        this.error.billingFirstName=false;
        this.error.billingLastName=false;
        this.error.shippingCity=false

        if (this.orderInfo.taxLienIsBusiness) {
          hasErrors = SiteUtils.formErrors(
            ['taxLienBusinessName', 'taxLienBusinessEin'],
            this.orderInfo, this.error
          );

          if (!SiteUtils.validateEin(this.orderInfo.taxLienBusinessEin)) {
            this.error.taxLienBusinessEin = true;
            hasErrors = true;
          }

        } else {
          if (!SiteUtils.validateSsn(this.orderInfo.ssn)) {
            this.error.ssn = true;
            hasErrors = true;
          }
        }

        let checkShippingPostalCodeErrorMsg = SiteUtils.validateZipCode(this.orderInfo.shippingPostalCode);
        if (checkShippingPostalCodeErrorMsg) {
            this.error.shippingPostalCode = checkShippingPostalCodeErrorMsg;
          hasErrors = true;
        }

        if (!this.shippingAddressSame) {
          let shippingAddressFields = ['billingAddress1', 'billingCity', 'billingState', 'billingPostalCode'];

          hasErrors = SiteUtils.formErrors(shippingAddressFields, this.orderInfo, this.error);


          if (SiteUtils.validateZipCode(this.orderInfo.billingPostalCode)) {
            this.error.billingPostalCode = true;
            hasErrors = true;
          }
        }

        if (this.orderInfo.taxLienRemediationType === 'currentPaymentPlan') {
          if (this.orderInfo.taxLienAutomaticDebit === null) {
            this.error.taxLienAutomaticDebit = true;
            hasErrors = true;
          }
        }

        let serialNumberError = false;
        if (this.hasSerialNumber) {
          if (!this.orderInfo.serialNumber || this.orderInfo.serialNumber.length === 0) {
            serialNumberError = true;
            hasErrors = true;
          }
        }
        this.error.serialNumber = serialNumberError;

        if (['removeForSale', 'removeForMoreIncome'].includes(this.orderInfo.taxLienRemediationType)) {
          hasErrors = SiteUtils.formErrors(
            ['taxLienRemediationDescription'], this.orderInfo, this.error
          );
        }

        let checkBillingFirstNameErrorMsg = SiteUtils.validateFirstName(this.orderInfo.billingFirstName)
        if (checkBillingFirstNameErrorMsg) {
          this.error.billingFirstName = checkBillingFirstNameErrorMsg;
          this.hasErrors = true;
        }

        let checkBillingLastNameErrorMsg = SiteUtils.validateLastName(this.orderInfo.billingLastName)
        if (checkBillingLastNameErrorMsg) {
          this.error.billingLastName = checkBillingLastNameErrorMsg;
          this.hasErrors = true;
        }

       if (!this.addressType && !SiteUtils.validateAddress(this.orderInfo.shippingAddress1)) {
          this.error.shippingAddress1 = true;
          this.error.addressType = false;
          hasErrors = true;
        }

        let checkStressAddressErrorMSG = SiteUtils.validateStreetAddress(this.orderInfo.shippingAddress1, this.orderInfo.shippingAddress2);
        if (!this.addressType && checkStressAddressErrorMSG) {
            this.error.shippingAddress1 = checkStressAddressErrorMSG;
            this.error.addressType = false;
            this.hasErrors = true;
        }

        let checkShippingCityErrorMsg = SiteUtils.validateShippingCity(this.orderInfo.shippingCity)
        if(checkShippingCityErrorMsg){
            this.error.shippingCity=checkShippingCityErrorMsg
            this.hasErrors = true;
        }

       if (!SiteUtils.validateAddressType(this.addressType,this.orderInfo.shippingAddress1)) {
          this.error.addressType = true;
          this.error.shippingAddress1 = false;
          hasErrors = true;
        }


        if (!SiteUtils.validateAddress(this.orderInfo.billingAddress1)) {
          this.error.billingAddress1 = true;
          hasErrors = true;
        }

       if (!SiteUtils.validateAddress(this.orderInfo.billingAddress2)) {
          this.error.billingAddress2 = true;
          hasErrors = true;
        }

      //  if (!SiteUtils.validateAptUnit(this.orderInfo.shippingAddress2)) {
      //      this.error.shippingAddress2 = true;
      //      hasErrors = true;
      //     }

        let checkEmailAddressErrorMsg = SiteUtils.validateEmail(this.orderInfo.email);
        if (checkEmailAddressErrorMsg) {
            this.error.email = checkEmailAddressErrorMsg;
            this.hasErrors = true;
        }

        let checkPhoneNumberErrorMsg = SiteUtils.validatePhone(this.orderInfo.billingPhone);
        if (checkPhoneNumberErrorMsg) {
            this.error.billingPhone = checkPhoneNumberErrorMsg
            this.hasErrors = true;
        }

        if (!SiteUtils.validateCardNumber(this.orderInfo.cardNumber)) {
          this.error.cardNumber = true;
          hasErrors = true;
        }

        if (!SiteUtils.validateInteger(this.orderInfo.cardCvc)) {
          this.error.cardCvc = true;
          hasErrors = true;
        }
          console.log("errors details is" ,this.error);
        return hasErrors;
      },

      postOrderInfo() {
        this.errorMessages = [];
        let hasErrors = this.validateForm();

        // let order=Object.assign({},this.orderInfo)
        // order.shippingAddress1 = SiteUtils.standardizeAddress(this.addressType,order.shippingAddress1);
        this.orderInfoTemp=SiteUtils.updateAddress(this.orderInfoTemp,this.orderInfo, this.addressType,this.shippingAddressSame);
        AXIOS.put(`/ecommerce/presave`, this.orderInfoTemp)
          .then(() => {
            if (hasErrors) {
              this.$refs.errorsModal.show();
              return false;
            }
            this.isProcessing = true;

            let self = this;

            this.orderInfo.billingPhone = SiteUtils.standardizePhone(this.orderInfo.billingPhone);
            this.orderInfo.ssn = SiteUtils.standardizeSsn(this.orderInfo.ssn);
            this.orderInfo.spouseSsn = SiteUtils.standardizeSsn(this.orderInfo.spouseSsn);
            this.orderInfo.taxLienBusinessEin = SiteUtils.standardizeEin(this.orderInfo.taxLienBusinessEin);

            if (this.shippingAddressSame) {
              this.orderInfo.billingAddress1 = this.orderInfo.shippingAddress1;
              this.orderInfo.billingAddress2 = this.orderInfo.shippingAddress2;
              this.orderInfo.billingCity = this.orderInfo.shippingCity;
              this.orderInfo.billingState = this.orderInfo.shippingState;
              this.orderInfo.billingPostalCode = this.orderInfo.shippingPostalCode;
            }

            this.orderInfo.shippingFirstName = this.orderInfo.billingFirstName;
            this.orderInfo.shippingLastName = this.orderInfo.billingLastName;
            this.orderInfo.shippingPhone = this.orderInfo.billingPhone;

            this.orderInfo.cardNumber = this.orderInfo.cardNumber.replace(/[^0-9]/g, '');

            this.orderInfoTemp=SiteUtils.updateAddress(this.orderInfoTemp,this.orderInfo, this.addressType,this.shippingAddressSame);
            AXIOS.post(`/ecommerce/jwt`, this.orderInfoTemp)
              .then(response => {
                self.$set(self, 'jwtData', response.data);
                self.orderNumber=response.data.payload.OrderDetails.OrderNumber;
                Cardinal.setup('init', {
                  jwt: self.jwtData.jwt
                });
              })
              .catch(e => {
                self.errorMessages.push({
                    show: true,
                    text: "Unexpected error occurred. Please try again later."
                });
                this.initializeCardinal();
                self.isProcessing = false;
              });
          });
      },
            getAddressData: function (addressData, placeResultData, id) {
              this.orderInfo.shippingAddress1=addressData.street_number?addressData.street_number+' '+addressData.route:addressData.route;
              this.orderInfo.shippingCity=addressData.locality?addressData.locality:placeResultData.vicinity?placeResultData.vicinity:placeResultData.address_components[3].long_name;
              this.orderInfo.shippingState=addressData.administrative_area_level_1?addressData.administrative_area_level_1:'';
              this.orderInfo.billingAddress1=addressData.street_number?addressData.street_number+' '+addressData.route:addressData.route;
              this.orderInfo.billingCity=addressData.locality?addressData.locality:placeResultData.vicinity?placeResultData.vicinity:placeResultData.address_components[3].long_name;
              this.orderInfo.billingState=addressData.administrative_area_level_1?addressData.administrative_area_level_1:'';
              this.orderInfo.shippingPostalCode = addressData.postal_code ? addressData.postal_code:'';
              this.orderInfo.billingPostalCode = addressData.postal_code ? addressData.postal_code:'';
              // this.showGoogleTextBox=false;
            },
            extractAddress:function(){
              google.maps.places.Autocomplete(this.getAddressData, 'place_changed');
              this.showGoogleTextBox=false
            },
             onClick: function (e) {
      console.log(e.target.tagName) // "A"
      console.log(e.targetVM === this) // true
    },
            setAddress:function(address,type){
              this.orderInfo.shippingAddress1=address.newVal?address.newVal.split(',')[0]:'';
              this.orderInfo.billingAddress1=address.newVal?address.newVal.split(',')[0]:'';
            },
            changeToGoogle:function(){
              if(!this.addressType){
                this.showGoogleTextBox=true;
              }
            },
      clearRemediationDescription() {
        this.orderInfo.taxLienRemediationDescription = "";
      }
    },
    mounted() {
      this.initializeCardinal();
      this.initializeSalesWidget();
      // let self = this;
      //       let g_address= document.getElementById("g_address");
      //      g_address.onblur=(async res=>{
      //       await this.google.maps.places.Autocomplete(self.getAddressData, 'place_changed');
      //       self.showGoogleTextBox=false;
      //     })
      AXIOS.get(`/ecommerce/order`)
        .then(response => {
          this.orderInfo.billingFirstName = response.data.firstName;
          this.orderInfo.billingLastName = response.data.lastName;
          this.orderInfo.billingPhone = response.data.phone;
          this.orderInfo.email = response.data.email;

          this.orderInfo.billingAddress1 = response.data.billingAddress1;
          this.orderInfo.billingAddress2 = response.data.billingAddress2;
          this.orderInfo.billingCity = response.data.billingCity;
          this.orderInfo.billingState = response.data.billingState;
          this.orderInfo.billingPostalCode = response.data.billingZip;

          this.orderInfo.shippingAddress1 = response.data.shippingAddress1;
          this.orderInfo.shippingAddress2 = response.data.shippingAddress2;
          this.orderInfo.shippingCity = response.data.shippingCity;
          this.orderInfo.shippingState = response.data.shippingState;
          this.orderInfo.shippingPostalCode = response.data.shippingZip;

          this.orderInfo.totalDebt = response.data.totalDebt;
        })
        .catch(e => {
          self.errorMessages.push({
              show: true,
              text: "Unexpected error occurred. Please try again later."
          });
          self.isProcessing = false;
        })
    }
  }
</script>
<style scoped>
  .btn-primary {
    background-color: #2896C5;
    border: 0px;
  }
  .mb-10{
  margin-bottom:10px;
}
  .aside-item h2 {
    font-size: 3em;
    font-family: sans-serif;
  }
  .aside-item p {
    font-size: 1.5em;
    font-family: sans-serif;
  }
  .home-page {
    background-size: cover;
  }
  p{
    font-family: sans-serif;
  }

  .bg-grey {
    background-color: #f0f0f0;
  }
  .po-box{
    width: 77%;
    margin-top: 5%;
    margin-left: -4%;
}
@media (max-width: 576px){
.po-box{
    width: 66%;
    margin-top: 5%;
    margin-left: 2%;
}
}

  .otef-helpbubble {
    margin-left: -15px;
  }
  @media (min-width: 576px){
.col-sm-6 {
    -ms-flex: 0 0 50%;
    /* flex: 0 0 50%; */
    max-width: 100% !important;
}
}
</style>
