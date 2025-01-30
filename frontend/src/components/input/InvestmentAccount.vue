<template>
  <div class="container investment-account-box">
    <div class="row">
      <div class="col">
        <button
          class="btn btn-outline-secondary float-right"
          v-on:click="$emit('remove')">Remove</button>
      </div>
    </div>
    <div class="row">

      <input-options
        :binding="binding.type"
        label="Investment Type"
        :options="[
              {value: 'STOCKS', label: 'Stocks'},
              {value: 'BONDS', label: 'Bonds'},
              {value: '_401K', label: '401K'},
              {value: 'IRA', label: 'IRA'},
              {value: 'RETIREMENT', label: 'Retirement Account'},
              {value: 'OTHER', label: 'Other'},
            ]"
        v-on:input="handleInput($event, 'type')">
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
        v-if="binding.type === 'RETIREMENT' || binding.type === 'OTHER'"
        :binding="binding.typeDescription"
        label="Investment Type"
        v-on:input="handleInput($event, 'typeDescription')">
      </input-text>
    </div>

    <div class="row">

      <input-text
        :binding="binding.name"
        label="Financial Institution Name"
        v-on:input="handleInput($event, 'name')">
      </input-text>

      <input-text
        :binding="binding.accountNumber"
        label="Account Number"
        v-on:input="handleInput($event, 'accountNumber')">
      </input-text>

      <input-text
        :binding="binding.marketValue"
        label="Current Market Value"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'marketValue')">
      </input-text>

      <input-text
        :binding="binding.loanBalance"
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
            type: {},
            typeDescription: {},
            name: {},
            accountNumber: {},
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
        this.$set(this.binding, 'type', {value: existing.type});
        this.$set(this.binding, 'typeDescription', {value: existing.typeDescription});
        this.$set(this.binding, 'name', {value: existing.name});
        this.$set(this.binding, 'accountNumber', {value: existing.accountNumber});
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
  .investment-account-box {
    border: 1px solid #2896C5;
    margin: 10px 0;
    padding: 10px;
  }
</style>
