<template>
  <div class="container real-estate-box">
    <div class="row">
      <div class="col">
        <button
          class="btn btn-outline-secondary float-right"
          v-on:click="$emit('remove')">Remove</button>
      </div>
    </div>
    <div class="row">
      <yes-no-select-field
        v-model="realEstate.primaryResidence"
        :error="errors.primaryResidence"
        label="Primary Residence?"
        v-on:input="$emit('input', realEstate)">
      </yes-no-select-field>
    </div>
    <div class="row">
      <form-input-field
        v-model="realEstate.addressLine1"
        :error="errors.addressLine1"
        label="Address Line 1"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.addressLine2"
        :error="errors.addressLine2"
        label="Address Line 2"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.addressCity"
        :error="errors.addressCity"
        label="Address City"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.addressState"
        :error="errors.addressState"
        label="Address State"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.addressZip"
        :error="errors.addressZip"
        label="Address Zip"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
    </div>
    <div class="row">
      <form-input-field
        v-model="realEstate.county"
        :error="errors.county"
        label="County"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.country"
        :error="errors.country"
        label="Country"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
    </div>
    <div class="row">
      <form-input-field
        v-model="realEstate.datePurchased"
        :error="errors.datePurchased"
        label="Date Purchased"
        placeholder="mm/dd/yyyy"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.dateFinalPayment"
        :error="errors.dateFinalPayment"
        label="Date of Final Payment"
        placeholder="mm/dd/yyyy"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.titleDesc"
        :error="errors.titleDesc"
        label="How title is held"
        placeholder="e.g. joint tenancy, etc."
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.propertyDesc"
        :error="errors.propertyDesc"
        label="Description of Property"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.marketValue"
        :error="errors.marketValue"
        label="Current Market Value"
        placeholder="$00.00"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
      <form-input-field
        v-model="realEstate.loanBalance"
        :error="errors.loanBalance"
        label="Loan Balance"
        placeholder="$00.00"
        v-on:input="$emit('input', realEstate)">
      </form-input-field>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      realEstate: {
        default() {
          return {
            _key: Math.random(),
            addressLine1: '',
            addressLine2: '',
            addressCity: '',
            addressState: '',
            addressZip: '',
            primaryResidence: '',
            datePurchased: '',
            county: '',
            country: '',
            dateFinalPayment: '',
            titleDesc: '',
            propertyDesc: '',
            marketValue: '',
            loanBalance: ''
          }
        }
      }
    },
    data() {
      return {
        errors: {
          addressLine1: false,
          addressLine2: false,
          addressCity: false,
          addressState: false,
          addressZip: false,
          primaryResidence: false,
          datePurchased: false,
          county: false,
          country: false,
          dateFinalPayment: false,
          titleDesc: false,
          propertyDesc: false,
          marketValue: false,
          loanBalance: false
        }
      }
    }
  }
</script>

<style scoped>
  .real-estate-box {
    border: 1px solid #2896C5;
    margin: 10px 0;
    padding: 10px;
  }
</style>
