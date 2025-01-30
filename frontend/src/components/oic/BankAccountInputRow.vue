<template>
  <div class="container bank-account-box">
    <div class="row">
      <div class="col">
        <button
          class="btn btn-outline-secondary float-right"
          v-on:click="$emit('remove')">Remove</button>
      </div>
    </div>
    <div class="row">
      <form-input-options
        v-model="bankAccount.type"
        label="Asset Type"
        :options="[
              {value: 'Cash', label: 'Cash'},
              {value: 'Checking', label: 'Checking'},
              {value: 'Savings', label: 'Savings'},
              {value: 'MoneyMarket', label: 'Money Market Account/CD'},
              {value: 'Online', label: 'Online Account'},
              {value: 'Stored', label: 'Stored Value Card'},
              {value: 'Virtual', label: 'Virtual Currency'}
            ]"
        :error="errors.type"
        v-on:input="$emit('input', bankAccount)">
      </form-input-options>
    </div>
    <div class="row">

      <form-input-field
        v-model="bankAccount.bankName"
        :error="errors.bankName"
        label="Bank Name"
        v-on:input="$emit('input', bankAccount)">
      </form-input-field>

      <form-input-field
        v-model="bankAccount.accountNumber"
        :error="errors.accountNumber"
        label="Account Number"
        v-on:input="$emit('input', bankAccount)">
      </form-input-field>

      <form-input-field
        v-model="bankAccount.marketValue"
        :error="errors.marketValue"
        label="Value"
        placeholder="$00.00"
        v-on:input="$emit('input', bankAccount)">
      </form-input-field>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      bankAccount: {
        default() {
          return {
            _key: Math.random(),
            type: '',
            bankName: '',
            accountNumber: '',
            marketValue: '',
          }
        }
      }
    },
    data() {
      return {
        errors: {
          type: false,
          bankName: false,
          accountNumber: false,
          marketValue: false
        }
      }
    }
  }
</script>


<style scoped>
  .bank-account-box {
    border: 1px solid #2896C5;
    margin: 10px 0;
    padding: 10px;
  }
</style>
