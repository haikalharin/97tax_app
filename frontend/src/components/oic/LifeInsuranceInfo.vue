<template>
  <div class="container">
    <div class="row">
      <yes-no-select-field
        v-model="lifeInsuranceInfo.hasLifeInsurance"
        label="Do you have life insurance?"
        v-on:input="$emit('input', lifeInsuranceInfo)">
      </yes-no-select-field>
    </div>
    <div class="row" v-if="lifeInsuranceInfo.hasLifeInsurance">
      <form-input-field
        v-model="lifeInsuranceInfo.companyName"
        :error="errors.companyName"
        label="Name of Insurance Company"
        v-on:input="$emit('input', lifeInsuranceInfo)">
      </form-input-field>
      <form-input-field
        v-model="lifeInsuranceInfo.policyNumber"
        :error="errors.policyNumber"
        label="Policy Number"
        v-on:input="$emit('input', lifeInsuranceInfo)">
      </form-input-field>
      <form-input-field
        v-model="lifeInsuranceInfo.cashValue"
        :error="errors.cashValue"
        label="Cash Value"
        placeholder="$00.00"
        v-on:input="$emit('input', lifeInsuranceInfo)">
      </form-input-field>
      <form-input-field
        v-model="lifeInsuranceInfo.loanBalance"
        :error="errors.loanBalance"
        label="Loan Balance"
        placeholder="$00.00"
        v-on:input="$emit('input', lifeInsuranceInfo)">
      </form-input-field>
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
    data() {
      return {
        lifeInsuranceInfo: {
          hasLifeInsurance: '',
          companyName: '',
          policyNumber: '',
          cashValue: '',
          loanBalance: ''
        },
        errors: {
          hasLifeInsurance: false,
          companyName: false,
          policyNumber: false,
          cashValue: false,
          loanBalance: false
        }
      }
    }
  }
</script>

