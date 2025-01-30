<template>
  <page class="home-page">
    <div class="container">
      <oic-progress></oic-progress>
      <div class="row">
        <div class="col">
          <h1 class="blueheader">Income</h1>
          <p>
            Your income is a big part in deciding how much we can offer to settle your tax debt with the IRS. Income includes paychecks from job or social security income. If section does not apply to you, you may leave it blank.
          </p>
        </div>
      </div>

      <div class="row">
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0 "  src="@/assets/Employment.png">
          <h3 class="text-uppercase m-0 text-center">Employment & Social Security</h3>
          <p>Paychecks and income from work or Social Security.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/OtherIncome.png">
          <h3 class="text-uppercase m-0 text-center">Other Income</h3>
          <p>Child Support, alimony, and additional household members who contribute.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/Investments.png">
          <h3 class="text-uppercase m-0 text-center">Income From investments</h3>
          <p>Rental Property Income, Interest and dividends, Distributions.</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/Basics.png">
          <h3 class="text-uppercase m-0 text-center">The Basics</h3>
          <p>Housing and utilities, Food, Clothing, Day Care Costs, healthcare costs, and life insurance.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/Transportation.png">
          <h3 class="text-uppercase m-0 text-center">Transportation</h3>
          <p>This includes vehicle loan and lease payments, vehicle operating costs, or public transportation costs.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/StudentLoans.png">
          <h3 class="text-uppercase m-0 text-center">Student loans & other expenses</h3>
          <p>Student loans, secured debts, personal loans, alimony, child support, past due local and state taxes.</p>
        </div>
      </div>


      <div class="row">
        <div class="p-2 mr-auto mt-4 mb-4 col-4">
          <button class="btn-block btn-primary" v-on:click="handle">Back</button>
        </div>
        <div class="p-2 mt-4 mb-4 flex-row-reverse col-4">
          <button class=" btn-block btn-primary" v-on:click="handleContinue">Continue</button>
        </div>
      </div>









      <h3>Monthly Household Income</h3>

      <div class="row mb-4 align-items-end">
        <input-text
          ref="income"
          label="Your Income, including Social Security and any Pension(s)"
          placeholder="0"
          type="integer"
          :binding="form.income"
        ></input-text>
        <input-text
          ref="otherIncome"
          label="Other Income (e.g. unemployment)"
          placeholder="0"
          type="integer"
          :binding="form.otherIncome"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-options
          ref="married"
          :required="true"
          label="Are you currently married?"
          :binding="form.married"
        ></input-options>
      </div>

      <div class="row mb-4 align-items-end" v-if="form.married.value">
        <input-text
          ref="spouseIncome"
          label="Your Spouse's Income, including Social Security and any Pension(s)"
          placeholder="0"
          type="integer"
          :binding="form.spouseIncome"
        ></input-text>
        <input-text
          ref="spouseOtherIncome"
          label="Spouse's Other Income (e.g. unemployment)"
          placeholder="0"
          type="integer"
          :binding="form.spouseOtherIncome"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="additionalHouseholdIncome"
          classes="col-12"
          label="Additional Household Income (e.g. non-liable spouse, or anyone else who contributes to household income)"
          placeholder="0"
          type="integer"
          :binding="form.additionalHouseholdIncome"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="interestAndDividends"
          classes="col-12"
          label="Interest and dividends"
          placeholder="0"
          type="integer"
          :binding="form.interestAndDividends"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="distributions"
          classes="col-12"
          label="Distributions (e.g. income from partnerships, sub-S Corporations, etc.)"
          placeholder="0"
          type="integer"
          :binding="form.distributions"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="rentalIncome"
          classes="col-12"
          label="Net Rental Income"
          placeholder="0"
          type="integer"
          :binding="form.rentalIncome"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="childSupport"
          classes="col-12"
          label="Child Support Received"
          placeholder="0"
          type="integer"
          :binding="form.childSupport"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="alimony"
          classes="col-12"
          label="Alimony Received"
          placeholder="0"
          type="integer"
          :binding="form.alimony"
        ></input-text>
      </div>

      <h3>Monthly Household Expenses</h3>

      <div class="row mb-4">
        <input-text
          ref="exFoodClothingMisc"
          classes="col-12"
          label="Food, Clothing, and Miscellaneous"
          placeholder="0"
          type="integer"
          :binding="form.exFoodClothingMisc"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exHousingUtils"
          classes="col-12"
          label="Housing and Utilities  (e.g., rent or mortgage payment and average monthly cost of property taxes, home insurance, maintenance, dues, fees and utilities including electricity, gas, other fuels, trash collection, water, cable television and internet, telephone, and cell phone)"
          placeholder="0"
          type="integer"
          :binding="form.exHousingUtils"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exVehicle"
          classes="col-12"
          label="Vehicle loan and/or lease payment(s)"
          placeholder="0"
          type="integer"
          :binding="form.exVehicle"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exVehicleOps"
          classes="col-12"
          label="Vehicle operating costs (e.g., average monthly cost of maintenance, repairs, insurance, fuel, registrations, licenses, inspections, parking, tolls, etc.). A reasonable estimate of these expenses may be used."
          placeholder="0"
          type="integer"
          :binding="form.exVehicleOps"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exPublicTrans"
          classes="col-12"
          label="Public transportation costs (e.g., average monthly cost of fares for mass transit such as bus, train, ferry, taxi, etc.). A reasonable estimate of these expenses may be used."
          placeholder="0"
          type="integer"
          :binding="form.exPublicTrans"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exHealthIns"
          classes="col-12"
          label="Health insurance premiums"
          placeholder="0"
          type="integer"
          :binding="form.exHealthIns"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exPocketHealth"
          classes="col-12"
          label="Out-of-pocket health care costs (e.g. average monthly cost of prescription drugs, medical services, and medical supplies like  eyeglasses, hearing aids, etc.)"
          placeholder="0"
          type="integer"
          :binding="form.exPocketHealth"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exCourtPayment"
          classes="col-12"
          label="Court-ordered payments (e.g., monthly cost of any alimony, child support, etc.)"
          placeholder="0"
          type="integer"
          :binding="form.exCourtPayment"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exDependentCare"
          classes="col-12"
          label="Child/dependent care payments (e.g., daycare, etc.)"
          placeholder="0"
          type="integer"
          :binding="form.exDependentCare"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exLifeIns"
          classes="col-12"
          label="Life insurance premiums"
          placeholder="0"
          type="integer"
          :binding="form.exLifeIns"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exMonthlyTaxes"
          classes="col-12"
          label="Current monthly taxes (e.g., monthly cost of federal, state, and local tax, personal property tax, etc.)"
          placeholder="0"
          type="integer"
          :binding="form.exMonthlyTaxes"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exSecureDebt"
          classes="col-12"
          label="Secured debts (e.g., any loan where you pledged an asset as collateral not previously listed, government guaranteed Student Loan)"
          placeholder="0"
          type="integer"
          :binding="form.exSecureDebt"
        ></input-text>
      </div>

      <div class="row mb-4">
        <input-text
          ref="exStateLocalTax"
          classes="col-12"
          label="Enter the amount of your monthly delinquent State and/or Local Tax payment(s)"
          placeholder="0"
          type="integer"
          :binding="form.exStateLocalTax"
        ></input-text>
      </div>

      <h3>Self-Employment</h3>

      <div class="row mb-4">
        <input-options
          ref="selfEmployed"
          :required="true"
          label="Are you self-employed?"
          :binding="form.selfEmployed"
        ></input-options>
      </div>

      <div v-if="form.selfEmployed.value">

        <h3>Monthly Business Income</h3>

        <div class="row mb-4">
          <input-text
            ref="busGrossReceipts"
            classes="col-12"
            label="Gross receipts"
            placeholder="0"
            type="integer"
            :binding="form.busGrossReceipts"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busGrossRentalIncome"
            classes="col-12"
            label="Gross rental income"
            placeholder="0"
            type="integer"
            :binding="form.busGrossRentalIncome"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busInterestIncome"
            classes="col-12"
            label="Interest income"
            placeholder="0"
            type="integer"
            :binding="form.busInterestIncome"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busDividends"
            classes="col-12"
            label="Dividends"
            placeholder="0"
            type="integer"
            :binding="form.busDividends"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busOtherIncome"
            classes="col-12"
            label="Other Income"
            placeholder="0"
            type="integer"
            :binding="form.busOtherIncome"
          ></input-text>
        </div>


        <h3>Monthly Business Expenses</h3>

        <div class="row mb-4">
          <input-text
            ref="busMaterialsPurchased"
            classes="col-12"
            label="Materials purchased (e.g., items directly related to the production of a product or service) "
            placeholder="0"
            type="integer"
            :binding="form.busMaterialsPurchased"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busInventoryPurchased"
            classes="col-12"
            label="Inventory purchased (e.g., goods bought for resale)"
            placeholder="0"
            type="integer"
            :binding="form.busInventoryPurchased"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busGrossWagesAndSalaries"
            classes="col-12"
            label="Gross wages and salaries"
            placeholder="0"
            type="integer"
            :binding="form.busGrossWagesAndSalaries"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busRent"
            classes="col-12"
            label="Rent"
            placeholder="0"
            type="integer"
            :binding="form.busRent"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busSupplies"
            classes="col-12"
            label="Supplies (items used to conduct business and used up within one year, e.g., books, office supplies, professional equipment, etc.)"
            placeholder="0"
            type="integer"
            :binding="form.busSupplies"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busUtilities"
            classes="col-12"
            label="Utilities and telephones"
            placeholder="0"
            type="integer"
            :binding="form.busUtilities"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busVehicleCost"
            classes="col-12"
            label="Vehicle cost (gas, oil, repairs, maintenance)"
            placeholder="0"
            type="integer"
            :binding="form.busVehicleCost"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busInsurance"
            classes="col-12"
            label="Business Insurance"
            placeholder="0"
            type="integer"
            :binding="form.busInsurance"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busTaxes"
            classes="col-12"
            label="Current Business Taxes (e.g., Real estate,  excise, franchise, occupational, personal property, sales and employer's portion of employment taxes) "
            placeholder="0"
            type="integer"
            :binding="form.busTaxes"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busSecureDebt"
            classes="col-12"
            label="Secured debts (not credit cards)"
            placeholder="0"
            type="integer"
            :binding="form.busSecureDebt"
          ></input-text>
        </div>

        <div class="row mb-4">
          <input-text
            ref="busOtherExpenses"
            classes="col-12"
            label="Other business expenses"
            placeholder="0"
            type="integer"
            :binding="form.busOtherExpenses"
          ></input-text>
        </div>

      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-primary" v-on:click="handleContinue">Continue</button>
        </div>
      </div>
    </div>
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

  export default {
    data() {
      return {
        existing: {},
        form: {
          income: {name: 'monthlyIncome'},
          otherIncome: {},

          married: {},
          spouseIncome: {},
          spouseOtherIncome: {},

          additionalHouseholdIncome: {name: 'additionalIncome'},
          interestAndDividends: {},
          distributions: {},
          rentalIncome: {name: 'netRentalIncome'},
          childSupport: {name: 'childSupportReceived'},
          alimony: {name: 'alimonyReceived'},

          exFoodClothingMisc: {name: 'foodClothingMisc'},
          exHousingUtils: {name: 'housingAndUtilities'},
          exVehicle: {name: 'vehicleLoanLeasePayment'},
          exVehicleOps: {name: 'vehicleOperatingCost'},
          exPublicTrans: {name: 'publicTransportCost'},
          exHealthIns: {name: 'healthInsurancePremium'},
          exPocketHealth: {name: 'healthcareCost'},
          exCourtPayment: {name: 'courtOrderedPayments'},
          exDependentCare: {name: 'childCarePayments'},
          exLifeIns: {name: 'lifeInsurancePremiums'},
          exMonthlyTaxes: {name: 'currentMonthlyTaxes'},
          exSecureDebt: {name: 'securedDebts'},
          exStateLocalTax: {name: 'delinquentTaxes'},

          selfEmployed: {},
          busGrossReceipts: {name: 'grossReceipts'},
          busGrossRentalIncome: {name: 'grossRentalIncome'},
          busInterestIncome: {name: 'interestIncome'},
          busDividends: {name: 'businessDividends'},
          busOtherIncome: {name: 'businessOtherIncome'},

          busMaterialsPurchased: {name: 'materialsPurchased'},
          busInventoryPurchased: {name: 'inventoryPurchased'},
          busGrossWagesAndSalaries: {name: 'grossWagesSalaries'},
          busRent: {name: 'rent'},
          busSupplies: {name: 'supplies'},
          busUtilities: {name: 'utilitiesPhones'},
          busVehicleCost: {name: 'vehicleCost'},
          busInsurance: {name: 'businessInsurance'},
          busTaxes: {name: 'currentBusinessTaxes'},
          busSecureDebt: {name: 'businessSecuredDebts'},
          busOtherExpenses: {name: 'businessOtherExpenses'},

        }
      }
    },
    methods: {
      handleContinue() {
        if (SiteUtils.validateAll(this.$refs)) {
          SiteUtils.saveOicPage(this.existing, this.form, () => {
            this.$router.push("oic-settlement");
          })
        }
      }
    },
    mounted() {
      AXIOS.get('/oic')
        .then(response => {
          this.existing = response.data;
          if (this.existing) {
            SiteUtils.prefillForm(this, this.existing, this.form);
          } else {
            this.$router.push('offer-in-compromise');
          }
        })
        .catch(e => {
          alert("Sorry, we are doing maintenance on the website right now. Please check again later.");
          console.log(e);
        });
    }
  }
</script>
<style scoped>
  .btn-primary {
    background-color: #2896C5;
  }
  .aside-item h2 {
    font-size: 3em;
  }
  .aside-item p {
    font-size: 1.5em;
  }
  .home-page {
    background-image: url(./../assets/banner.jpg);
    background-size: cover;
  }
  h1.blueheader {
    color: #2896C5;
  }
</style>
