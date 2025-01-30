<template>
  <div class="container vehicle-input-box">
    <div class="row">
      <div class="col">
        <button
          class="btn btn-outline-secondary float-right"
          v-on:click="$emit('remove')">Remove</button>
      </div>
    </div>
    <div class="row">
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
        :binding="binding.makeModel"
        label="Make and Model"
        v-on:input="handleInput($event, 'makeModel')"
      ></input-text>
      <input-text
        :binding="binding.year"
        label="Year"
        v-on:input="handleInput($event, 'year')"
      ></input-text>
      <input-text
        :binding="binding.datePurchased"
        label="Date Purchased"
        placeholder="mm/dd/yyyy"
        :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
        v-on:input="handleInput($event, 'datePurchased')"
      ></input-text>
      <input-text
        :binding="binding.mileage"
        label="Mileage"
        type="integer"
        v-on:input="handleInput($event, 'mileage')"
      ></input-text>
      <input-options
        :binding="binding.financeType"
        :options="[{value:'Loan',label:'Loan'},{value:'Lease',label:'Lease'}]"
        v-on:input="handleInput($event, 'financeType')"
      ></input-options>
      <input-text
        :binding="binding.mileage"
        label="Name of Creditor"
        v-on:input="handleInput($event, 'mileage')"
      ></input-text>
      <input-text
        :binding="binding.dateFinalPayment"
        label="Date of Final Payment"
        placeholder="mm/dd/yyyy"
        :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
        v-on:input="handleInput($event, 'dateFinalPayment')"
      ></input-text>
      <input-text
        :binding="binding.monthlyPayment"
        label="Monthly Lease/Loan Amount"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'monthlyPayment')"
      ></input-text>
      <input-text
        :binding="binding.marketValue"
        label="Current Market Value"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'marketValue')"
      ></input-text>
      <input-text
        :binding="binding.loanBalance"
        label="Loan Balance"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'loanBalance')"
      ></input-text>

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
            makeModel: {},
            year: {},
            datePurchased: {},
            mileage: {},
            financeType: {},
            creditorName: {},
            dateFinalPayment: {},
            monthlyPayment: {},
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
        this.$set(this.binding, 'makeModel', {value: existing.makeModel});
        this.$set(this.binding, 'year', {value: existing.year});
        this.$set(this.binding, 'datePurchased', {value: existing.datePurchased});
        this.$set(this.binding, 'mileage', {value: existing.mileage});
        this.$set(this.binding, 'financeType', {value: existing.financeType});
        this.$set(this.binding, 'creditorName', {value: existing.creditorName});
        this.$set(this.binding, 'dateFinalPayment', {value: existing.dateFinalPayment});
        this.$set(this.binding, 'monthlyPayment', {value: existing.monthlyPayment});
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
  .vehicle-input-box {
    border: 1px solid #2896C5;
    margin: 10px 0;
    padding: 10px;
  }
</style>
