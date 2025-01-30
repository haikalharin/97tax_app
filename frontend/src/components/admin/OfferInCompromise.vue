<template>
  <div class="container">
    <div v-if="oicModel != null">
      <admin-input
        label="First Name"
        v-model="oicModel.firstName"
      ></admin-input>
      <admin-input
        label="Last Name"
        v-model="oicModel.lastName"
      ></admin-input>
      <admin-input
        label="Phone Number"
        v-model="oicModel.phoneNumber"
      ></admin-input>
      <admin-input
        label="Email"
        v-model="oicModel.email"
      ></admin-input>
      <admin-input
        label="Debt Owed"
        v-model="oicModel.debtOwed"
      ></admin-input>
      <admin-input
        label="DOB"
        v-model="oicModel.dob"
      ></admin-input>
      <admin-input
        label="SSN"
        v-model="oicModel.ssn"
      ></admin-input>
      <admin-input
        label="Address Line 1"
        v-model="oicModel.addressLine1"
      ></admin-input>
      <admin-input
        label="Address Line 2"
        v-model="oicModel.addressLine2"
      ></admin-input>
      <admin-input
        label="City"
        v-model="oicModel.city"
      ></admin-input>
      <admin-input
        label="State"
        v-model="oicModel.state"
      ></admin-input>
      <admin-input
        label="Zip"
        v-model="oicModel.zip"
      ></admin-input>
      <admin-input
        label="County"
        v-model="oicModel.county"
      ></admin-input>
      <admin-input
        label="Mail Line 1"
        v-model="oicModel.mailLine1"
      ></admin-input>
      <admin-input
        label="Mail Line 2"
        v-model="oicModel.mailLine2"
      ></admin-input>
      <admin-input
        label="Mail City"
        v-model="oicModel.mailCity"
      ></admin-input>
      <admin-input
        label="Mail State"
        v-model="oicModel.mailState"
      ></admin-input>
      <admin-input
        label="Mail Zip"
        v-model="oicModel.mailZip"
      ></admin-input>
      <admin-input
        label="Mail County"
        v-model="oicModel.mailCounty"
      ></admin-input>
      <admin-input
        label="Own/Rent?"
        v-model="oicModel.ownRent"
      ></admin-input>
      <admin-input
        label="Own/Rent Description"
        v-model="oicModel.ownRentDesc"
      ></admin-input>
      <admin-input
        label="Spouse First Name"
        v-model="oicModel.spouseFirstName"
      ></admin-input>
      <admin-input
        label="Spouse Last Name"
        v-model="oicModel.spouseLastName"
      ></admin-input>
      <admin-input
        label="Spouse DOB"
        v-model="oicModel.spouseDob"
      ></admin-input>
      <admin-input
        label="Spouse SSN"
        v-model="oicModel.spouseSsn"
      ></admin-input>
      <admin-input
        label="Employer Name"
        v-model="oicModel.employerName"
      ></admin-input>
      <admin-input
        label="Employer Ownership?"
        type="boolean"
        v-model="oicModel.employerOwnership"
      ></admin-input>
      <admin-input
        label="Employer Business Iterest"
        v-model="oicModel.employerBusInterest"
      ></admin-input>
      <admin-input
        label="Occupation"
        v-model="oicModel.occupation"
      ></admin-input>
      <admin-input
        label="Employer Duration (Years)"
        v-model="oicModel.employDurationYears"
      ></admin-input>
      <admin-input
        label="Employer Duration (Months)"
        v-model="oicModel.employDurationMonths"
      ></admin-input>
      <admin-input
        label="Employer Address 1"
        v-model="oicModel.employAddressLine1"
      ></admin-input>
      <admin-input
        label="Employer Address 2"
        v-model="oicModel.employAddressLine2"
      ></admin-input>
      <admin-input
        label="Employer City"
        v-model="oicModel.employCity"
      ></admin-input>
      <admin-input
        label="Employer State"
        v-model="oicModel.employState"
      ></admin-input>
      <admin-input
        label="Employer Zip"
        v-model="oicModel.employZip"
      ></admin-input>
      <admin-input
        label="Spouse Employer Name"
        v-model="oicModel.spouseEmployerName"
      ></admin-input>
      <admin-input
        label="Spouse Employer Ownership?"
        type="boolean"
        v-model="oicModel.spouseEmployerOwnership"
      ></admin-input>
      <admin-input
        label="Spouse Employer Business Interest"
        v-model="oicModel.spouseEmployerBusInterest"
      ></admin-input>
      <admin-input
        label="Spouse Occupation"
        v-model="oicModel.spouseOccupation"
      ></admin-input>
      <admin-input
        label="Spouse Employer Duration Years"
        v-model="oicModel.spouseEmployDurationYears"
      ></admin-input>
      <admin-input
        label="Spouse Employ Duration Months"
        v-model="oicModel.spouseEmployDurationMonths"
      ></admin-input>
      <admin-input
        label="Spouse Employ Address 1"
        v-model="oicModel.spouseEmployAddressLine1"
      ></admin-input>
      <admin-input
        label="Spouse Employ Address 2"
        v-model="oicModel.spouseEmployAddressLine2"
      ></admin-input>
      <admin-input
        label="Spouse Employ City"
        v-model="oicModel.spoouseEmployCity"
      ></admin-input>
      <admin-input
        label="Spouse Employ State"
        v-model="oicModel.spouseEmployState"
      ></admin-input>
      <admin-input
        label="Spouse Employ Zip"
        v-model="oicModel.spouseEmployZip"
      ></admin-input>
      <admin-input
        label="Monthly Income"
        v-model="oicModel.monthlyIncome"
      ></admin-input>
      <admin-input
        label="Other Income"
        v-model="oicModel.otherIncome"
      ></admin-input>
      <admin-input
        label="Married?"
        type="boolean"
        v-model="oicModel.married"
      ></admin-input>
      <admin-input
        label="Spouse Income"
        v-model="oicModel.spouseIncome"
      ></admin-input>
      <admin-input
        label="Spouse Other Income"
        v-model="oicModel.spouseOtherIncome"
      ></admin-input>
      <admin-input
        label="Additional Income"
        v-model="oicModel.additionalIncome"
      ></admin-input>
      <admin-input
        label="Interest and Dividends"
        v-model="oicModel.interestAndDividends"
      ></admin-input>
      <admin-input
        label="Distributions"
        v-model="oicModel.distributions"
      ></admin-input>
      <admin-input
        label="Net Rental Income"
        v-model="oicModel.netRentalIncome"
      ></admin-input>
      <admin-input
        label="Child Support Received"
        v-model="oicModel.childSupportReceived"
      ></admin-input>
      <admin-input
        label="Alimony Received"
        v-model="oicModel.alimonyReceived"
      ></admin-input>
      <admin-input
        label="Food Clothing Misc"
        v-model="oicModel.foodClothingMisc"
      ></admin-input>
      <admin-input
        label="Housing And Utilities"
        v-model="oicModel.housingAndUtilities"
      ></admin-input>
      <admin-input
        label="Vehicle Loan Lease Payment"
        v-model="oicModel.vehicleLoanLeasePayment"
      ></admin-input>
      <admin-input
        label="Vehicle Operating Cost"
        v-model="oicModel.vehicleOperatingCost"
      ></admin-input>
      <admin-input
        label="Public Transport Costs"
        v-model="oicModel.publicTransportCost"
      ></admin-input>
      <admin-input
        label="Health Insurance Premiums"
        v-model="oicModel.healthInsurancePremium"
      ></admin-input>
      <admin-input
        label="Healthcare Cost"
        v-model="oicModel.healthcareCost"
      ></admin-input>
      <admin-input
        label="Court Ordered Payments"
        v-model="oicModel.courtOrderedPayments"
      ></admin-input>
      <admin-input
        label="Child Care Payments"
        v-model="oicModel.childCarePayments"
      ></admin-input>
      <admin-input
        label="Life Insurance Premiums"
        v-model="oicModel.lifeInsurancePremiums"
      ></admin-input>
      <admin-input
        label="Current Monthly Taxes"
        v-model="oicModel.currentMonthlyTaxes"
      ></admin-input>
      <admin-input
        label="Secured Debts"
        v-model="oicModel.securedDebts"
      ></admin-input>
      <admin-input
        label="Delinquent Taxes"
        v-model="oicModel.delinquentTaxes"
      ></admin-input>
      <admin-input
        label="Self Employed?"
        type="boolean"
        v-model="oicModel.selfEmployed"
      ></admin-input>
      <admin-input
        label="Gross Receipts"
        v-model="oicModel.grossReceipts"
      ></admin-input>
      <admin-input
        label="Gross Rental Income"
        v-model="oicModel.grossRentalIncome"
      ></admin-input>
      <admin-input
        label="Interest Income"
        v-model="oicModel.interestIncome"
      ></admin-input>
      <admin-input
        label="Business Dividends"
        v-model="oicModel.businessDividends"
      ></admin-input>
      <admin-input
        label="Business Other Income"
        v-model="oicModel.businessOtherIncome"
      ></admin-input>
      <admin-input
        label="Materials Purchased"
        v-model="oicModel.materialsPurchased"
      ></admin-input>
      <admin-input
        label="Inventory Purchased"
        v-model="oicModel.inventoryPurchased"
      ></admin-input>
      <admin-input
        label="Gross Wages Salaries"
        v-model="oicModel.grossWagesSalaries"
      ></admin-input>
      <admin-input
        label="Rent"
        v-model="oicModel.rent"
      ></admin-input>
      <admin-input
        label="Supplies"
        v-model="oicModel.supplies"
      ></admin-input>
      <admin-input
        label="Utilities/Phones"
        v-model="oicModel.utilitiesPhones"
      ></admin-input>
      <admin-input
        label="Vehicle Cost"
        v-model="oicModel.vehicleCost"
      ></admin-input>
      <admin-input
        label="Business Insurance"
        v-model="oicModel.businessInsurance"
      ></admin-input>
      <admin-input
        label="Current Business Taxes"
        v-model="oicModel.currentBusinessTaxes"
      ></admin-input>
      <admin-input
        label="Business Secured Debts"
        v-model="oicModel.businessSecuredDebts"
      ></admin-input>
      <admin-input
        label="Business Other Expenses"
        v-model="oicModel.businessOtherExpenses"
      ></admin-input>

      <h2>Accounts</h2>

      <div v-for="account in oicModel.accounts">
        <div class="border border-info p-1 m-1">
          <admin-input
            label="Type"
            v-model="account.type"
          ></admin-input>
          <admin-input
            label="Bank Name"
            v-model="account.bankName"
          ></admin-input>
          <admin-input
            label="Account Number"
            v-model="account.accountNumber"
          ></admin-input>
          <admin-input
            label="Value"
            v-model="account.value"
          ></admin-input>
          <admin-input
            label="Business Asset?"
            v-model="account.businessAsset"
            type="boolean"
          ></admin-input>
        </div>
      </div>

      <h2>Investments</h2>

      <div v-for="investment in oicModel.investments">
        <div class="border border-info p-1 m-1">
          <admin-input
            label="Type"
            v-model="investment.type"
          ></admin-input>
          <admin-input
            label="Type Description"
            v-model="investment.typeDescription"
          ></admin-input>
          <admin-input
            label="Name"
            v-model="investment.name"
          ></admin-input>
          <admin-input
            label="Account Number"
            v-model="investment.accountNumber"
          ></admin-input>
          <admin-input
            label="Market Value"
            v-model="investment.marketValue"
          ></admin-input>
          <admin-input
            label="Loan Balance"
            v-model="investment.loanBalance"
          ></admin-input>
          <admin-input
            label="Business Asset?"
            v-model="investment.businessAsset"
            type="boolean"
          ></admin-input>
        </div>
      </div>

      <h2>Real Estate</h2>

      <div v-for="realEstate in oicModel.realEstates">
        <div class="border border-info p-1 m-1">
          <admin-input
            label="Primary Residence?"
            v-model="realEstate.type"
            type="boolean"
          ></admin-input>
          <admin-input
            label="Address Line 1"
            v-model="realEstate.addressLine1"
          ></admin-input>
          <admin-input
            label="Address Line 2"
            v-model="realEstate.addressLine2"
          ></admin-input>
          <admin-input
            label="Address City"
            v-model="realEstate.addressCity"
          ></admin-input>
          <admin-input
            label="Address State"
            v-model="realEstate.addressState"
          ></admin-input>
          <admin-input
            label="Address Zip"
            v-model="realEstate.addressZip"
          ></admin-input>
          <admin-input
            label="County"
            v-model="realEstate.county"
          ></admin-input>
          <admin-input
            label="Country"
            v-model="realEstate.country"
          ></admin-input>
          <admin-input
            label="Business Asset?"
            v-model="realEstate.businessAsset"
            type="boolean"
          ></admin-input>
        </div>
      </div>

      <h2>Vehicles</h2>

      <div v-for="vehicle in oicModel.vehicles">
        <div class="border border-info p-1 m-1">
          <admin-input
            label="Make and Model"
            v-model="vehicle.makeModel"
          ></admin-input>
          <admin-input
            label="Year"
            v-model="vehicle.year"
          ></admin-input>
          <admin-input
            label="Date Purchased (mm/dd/yyyy)"
            v-model="vehicle.datePurchased"
          ></admin-input>
          <admin-input
            label="Mileage"
            v-model="vehicle.mileage"
          ></admin-input>
          <admin-input
            label="Finance Type"
            v-model="vehicle.financeType"
          ></admin-input>
          <admin-input
            label="Creditor Name"
            v-model="vehicle.creditorName"
          ></admin-input>
          <admin-input
            label="Final Payment Date (mm/dd/yyyy)"
            v-model="vehicle.dateFinalPayment"
          ></admin-input>
          <admin-input
            label="Monthly Payment"
            v-model="vehicle.monthlyPayment"
          ></admin-input>
          <admin-input
            label="Market Value"
            v-model="vehicle.marketValue"
          ></admin-input>
          <admin-input
            label="Loan Balance"
            v-model="vehicle.loanBalance"
          ></admin-input>
          <admin-input
            label="Business Asset?"
            v-model="vehicle.businessAsset"
            type="boolean"
          ></admin-input>

        </div>
      </div>

      <h2>Life Insurance</h2>

      <div v-for="lifeInsurance in oicModel.lifeInsurances">
        <div class="border border-info p-1 m-1">
          <admin-input
            label="Has Life Insurance?"
            v-model="lifeInsurance.hasLifeInsurance"
            type="boolean"
          ></admin-input>
          <admin-input
            label="Name"
            v-model="lifeInsurance.name"
          ></admin-input>
          <admin-input
            label="Policy Number"
            v-model="lifeInsurance.policyNumber"
          ></admin-input>
          <admin-input
            label="Cash Value"
            v-model="lifeInsurance.cashValue"
          ></admin-input>
          <admin-input
            label="Loan Balance"
            v-model="lifeInsurance.loanBalance"
          ></admin-input>
        </div>
      </div>

      <h2>Settlement</h2>

      <div v-if="oicModel.oicCalculations">
        <div class="border border-info p-1 m-1">
          <admin-input
            label="Calculated 12-Month Settlement"
            v-model="oicModel.oicCalculations.calc12MonthSettlement"
          ></admin-input>
          <admin-input
            label="Calculated 24-Month Settlement"
            v-model="oicModel.oicCalculations.calc24MonthSettlement"
          ></admin-input>
          <admin-input
            label="Calculated Due With Application"
            v-model="oicModel.oicCalculations.calcDueWithApplication"
          ></admin-input>
          <admin-input
            label="Selected Settlement"
            v-model="oicModel.oicCalculations.selectedSettlement"
          ></admin-input>
          <admin-input
            label="Exception Payment Amount Monthly"
            v-model="oicModel.oicCalculations.exceptionPaymentAmountMonthly"
          ></admin-input>
          <admin-input
            label="Exception Payment Amount 1"
            v-model="oicModel.oicCalculations.exceptionPaymentAmount1"
          ></admin-input>
          <admin-input
            label="Exception Payment Amount 2"
            v-model="oicModel.oicCalculations.exceptionPaymentAmount2"
          ></admin-input>
          <admin-input
            label="Exception Payment Amount 3"
            v-model="oicModel.oicCalculations.exceptionPaymentAmount3"
          ></admin-input>
          <admin-input
            label="Exception Payment Amount 4"
            v-model="oicModel.oicCalculations.exceptionPaymentAmount4"
          ></admin-input>
          <admin-input
            label="Exception Payment Amount 5"
            v-model="oicModel.oicCalculations.exceptionPaymentAmount5"
          ></admin-input>

          <admin-input
            label="Exception Payment Date 1 (mm/dd/yyyy)"
            v-model="oicModel.oicCalculations.exceptionPaymentDate1"
          ></admin-input>
          <admin-input
            label="Exception Payment Date 2 (mm/dd/yyyy)"
            v-model="oicModel.oicCalculations.exceptionPaymentDate2"
          ></admin-input>
          <admin-input
            label="Exception Payment Date 3 (mm/dd/yyyy)"
            v-model="oicModel.oicCalculations.exceptionPaymentDate3"
          ></admin-input>
          <admin-input
            label="Exception Payment Date 4 (mm/dd/yyyy)"
            v-model="oicModel.oicCalculations.exceptionPaymentDate4"
          ></admin-input>
          <admin-input
            label="Exception Payment Date 5 (mm/dd/yyyy)"
            v-model="oicModel.oicCalculations.exceptionPaymentDate5"
          ></admin-input>
          <admin-input
            label="Exception Explanation"
            v-model="oicModel.oicCalculations.exceptionExplanation"
          ></admin-input>

        </div>
      </div>


      <div class="row">
        <div class="col">
          <button :disabled="disableButtons" class="btn btn-secondary" @click="saveOic()">Save Offer In Compromise</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import {AXIOS} from '../../scripts/http-common';

  export default {
    props: {
      orderNum: Number
    },
    data() {
      return {
        disableButtons: false,
        oicModel: null
      }
    },
    methods: {
      saveOic() {
        AXIOS.put(`/admin/oic`, this.oicModel)
          .then(response => {
            alert("Saved!");
          })
          .catch(e => {
            alert("An issue occurred while saving. Double check that the data is valid.");
            console.log(e);
          })
      }
    },
    mounted() {
      let self = this;
      this.disableButtons = true;
      AXIOS.get(`/admin/oic/orderNum/${this.orderNum}`)
        .then(response => {
          self.$set(self, 'oicModel', response.data);
          this.disableButtons = false;
        })
        .catch(e => {
          console.log(e);
          this.disableButtons = false;
        })
    }
  }
</script>
