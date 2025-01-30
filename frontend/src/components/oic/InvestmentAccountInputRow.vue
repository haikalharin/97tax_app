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

      <form-input-options
        v-model="investmentAccount.type"
        label="Investment Type"
        :options="[
              {value: 'Stocks', label: 'Stocks'},
              {value: 'Bonds', label: 'Bonds'},
              {value: '401K', label: '401K'},
              {value: 'IRA', label: 'IRA'},
              {value: 'Retirement', label: 'Retirement Account'},
              {value: 'Other', label: 'Other'},
            ]"
        :error="errors.type"
        v-on:input="$emit('input', investmentAccount)">
      </form-input-options>
      <form-input-field
        v-if="investmentAccount.type === 'Retirement' || investmentAccount.type === 'Other'"
        v-model="investmentAccount.typeDescription"
        :error="errors.typeDescription"
        label="Investment Type"
        v-on:input="$emit('input', investmentAccount)">
      </form-input-field>

    </div>

    <div class="row">

      <form-input-field
        v-model="investmentAccount.institutionName"
        :error="errors.institutionName"
        label="Financial Institution Name"
        v-on:input="$emit('input', investmentAccount)">
      </form-input-field>

      <form-input-field
        v-model="investmentAccount.accountNumber"
        :error="errors.accountNumber"
        label="Account Number"
        v-on:input="$emit('input', investmentAccount)">
      </form-input-field>

      <form-input-field
        v-model="investmentAccount.marketValue"
        :error="errors.marketValue"
        label="Current Market Value"
        placeholder="$00.00"
        v-on:input="$emit('input', investmentAccount)">
      </form-input-field>

      <form-input-field
        v-model="investmentAccount.loanBalance"
        :error="errors.loanBalance"
        label="Loan Balance"
        placeholder="$00.00"
        v-on:input="$emit('input', investmentAccount)">
      </form-input-field>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      investmentAccount: {
        default() {
          return {
            _key: Math.random(),
            type: '',
            typeDescription: '',
            institutionName: '',
            accountNumber: '',
            marketValue: '',
            loanBalance: ''
          }
        }
      }
    },
    data() {
      return {
        errors: {
          type: false,
          typeDescription: false,
          institutionName: false,
          accountNumber: false,
          marketValue: false,
          loanBalance: false
        }
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
