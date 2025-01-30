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
      <input-options
        v-model="binding.primaryResidence"
        label="Primary Residence?"
        v-on:input="handleInput($event, 'primaryResidence')">
      </input-options>
      <input-options
        ref="businessAsset"
        v-if="selfEmployed"
        v-model="binding.businessAsset"
        label="Business Asset?"
        v-on:input="handleInput($event, 'businessAsset')">
      </input-options>
    </div>
    <div class="row">
      <input-text
        v-model="binding.addressLine1"
        label="Address Line 1"
        v-on:input="handleInput($event, 'addressLine1')">
      </input-text>
      <input-text
        v-model="binding.addressLine2"
        label="Address Line 2"
        v-on:input="handleInput($event, 'addressLine2')">
      </input-text>
      <input-text
        v-model="binding.addressCity"
        label="Address City"
        v-on:input="handleInput($event, 'addressCity')">
      </input-text>
      <input-text
        v-model="binding.addressState"
        label="Address State"
        v-on:input="handleInput($event, 'addressState')">
      </input-text>
      <input-text
        v-model="binding.addressZip"
        label="Address Zip"
        v-on:input="handleInput($event, 'addressZip')">
      </input-text>
    </div>
    <div class="row">
      <input-text
        v-model="binding.county"
        label="County"
        v-on:input="handleInput($event, 'county')">
      </input-text>
      <input-text
        v-model="binding.country"
        label="Country"
        v-on:input="handleInput($event, 'country')">
      </input-text>
    </div>
    <div class="row">
      <input-text
        v-model="binding.datePurchased"
        label="Date Purchased"
        placeholder="mm/dd/yyyy"
        :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
        v-on:input="handleInput($event, 'datePurchased')">
      </input-text>
      <input-text
        v-model="binding.dateFinalPayment"
        label="Date of Final Payment"
        placeholder="mm/dd/yyyy"
        :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
        v-on:input="handleInput($event, 'dateFinalPayment')">
      </input-text>
      <input-text
        v-model="binding.titleDescription"
        label="How title is held"
        placeholder="e.g. joint tenancy, etc."
        v-on:input="handleInput($event, 'titleDescription')">
      </input-text>
      <input-text
        v-model="binding.propertyDescription"
        label="Description of Property"
        v-on:input="handleInput($event, 'propertyDescription')">
      </input-text>
      <input-text
        v-model="binding.marketValue"
        label="Current Market Value"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'marketValue')">
      </input-text>
      <input-text
        v-model="binding.loanBalance"
        label="Loan Balance"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'loanBalance')">
      </input-text>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      binding: {
        default() {
          return {
            _key: Math.random(),
            addressLine1: {},
            addressLine2: {},
            addressCity: {},
            addressState: {},
            addressZip: {},
            primaryResidence: {},
            datePurchased: {},
            county: {},
            country: {},
            dateFinalPayment: {},
            titleDescription: {},
            propertyDescription: {},
            marketValue: {},
            loanBalance: {},
            businessAsset: {}
          }
        }
      },
      selfEmployed: {
        type: Boolean
      }
    },
    methods: {
      validate() {

      },
      prefill(existing) {
        this.$set(this.binding, 'addressLine1', {value: existing.addressLine1});
        this.$set(this.binding, 'addressLine2', {value: existing.addressLine2});
        this.$set(this.binding, 'addressCity', {value: existing.addressCity});
        this.$set(this.binding, 'addressState', {value: existing.addressState});
        this.$set(this.binding, 'addressZip', {value: existing.addressZip});
        this.$set(this.binding, 'primaryResidence', {value: existing.primaryResidence});
        this.$set(this.binding, 'datePurchased', {value: existing.datePurchased});
        this.$set(this.binding, 'county', {value: existing.county});
        this.$set(this.binding, 'country', {value: existing.country});
        this.$set(this.binding, 'dateFinalPayment', {value: existing.dateFinalPayment});
        this.$set(this.binding, 'titleDescription', {value: existing.titleDescription});
        this.$set(this.binding, 'propertyDescription', {value: existing.propertyDescription});
        this.$set(this.binding, 'marketValue', {value: existing.marketValue});
        this.$set(this.binding, 'loanBalance', {value: existing.loanBalance});
        this.$set(this.binding, 'businessAsset', {value: existing.businessAsset});
      },

      handleInput(event, name) {
        this.binding[name] = event;
        this.$emit('input', this.binding);
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
