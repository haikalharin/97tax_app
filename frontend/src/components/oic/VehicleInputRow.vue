<template>
  <div class="container vehicle-input-box">
    <div class="row">
      <form-input-field
        v-model="entity.makeAndModel"
        :error="errors.makeAndModel"
        label="Make and Model"
        v-on:input="$emit('input', entity)">
      </form-input-field>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      entity: {
        default() {
          return {
            _key: Math.random(),
            makeAndModel: '',
            year: '',
            datePurchased: '',
            mileage: '',
            isLease: '',
            isLoan: '',
            creditorName: '',
            dateFinalPayment: '',
            monthlyAmount: '',
            marketValue: '',
            loanBalance: ''
          }
        }
      }
    },
    data() {
      return {
        errors: {
          makeAndModel: false,
          year: false,
          datePurchased: false,
          mileage: false,
          isLease: false,
          isLoan: false,
          creditorName: false,
          dateFinalPayment: false,
          monthlyAmount: false,
          marketValue: false,
          loanBalance: false
        }
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
