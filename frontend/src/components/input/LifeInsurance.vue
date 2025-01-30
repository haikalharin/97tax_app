<template>
  <div class="container">
    <div class="row">
      <input-options
        v-model="binding.hasLifeInsurance"
        label="Do you have life insurance?"
        v-on:input="handleInput($event, 'hasLifeInsurance')">
      </input-options>
    </div>
    <div class="row" v-if="lifeInsuranceInfo.hasLifeInsurance">
      <input-text
        v-model="binding.name"
        label="Name of Insurance Company"
        v-on:input="handleInput($event, 'name')">
      </input-text>
      <input-text
        v-model="binding.policyNumber"
        label="Policy Number"
        v-on:input="handleInput($event, 'policyNumber')">
      </input-text>
      <input-text
        v-model="binding.cashValue"
        label="Cash Value"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'cashValue')">
      </input-text>
      <input-text
        v-model="binding.loanBalance"
        label="Loan Balance"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'loanBalance')">
      </input-text>
    </div>

    <div class="row">
      <div class="col">
        <button class="btn btn-secondary" v-on:click="$emit('back', 'lifeInsuranceInfo')">Back</button>
        <button class="btn btn-primary" v-on:click="$emit('continue', 'lifeInsuranceInfo')">Continue</button>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      binding: {
        default() {
          return {
            hasLifeInsurance: {},
            name: {},
            policyNumber: {},
            cashValue: {},
            loanBalance: {}
          }
        }
      }
    },
    methods: {
      validate() {

      },
      prefill(existing) {
        this.$set(this.binding, 'hasLifeInsurance', {value: existing.hasLifeInsurance});
        this.$set(this.binding, 'name', {value: existing.name});
        this.$set(this.binding, 'policyNumber', {value: existing.policyNumber});
        this.$set(this.binding, 'cashValue', {value: existing.cashValue});
        this.$set(this.binding, 'loanBalance', {value: existing.loanBalance});
      },

      handleInput(event, name) {
        this.binding[name] = event;
        this.$emit('input', this.binding);
      }
    }
  }
</script>

