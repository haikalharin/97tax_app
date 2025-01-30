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
      <input-options
        ref="type"
        :binding="binding.type"
        label="Asset Type"
        :options="[
              {value: 'CASH', label: 'Cash'},
              {value: 'CHECKING', label: 'Checking'},
              {value: 'SAVINGS', label: 'Savings'},
              {value: 'MONEY_MARKET', label: 'Money Market Account/CD'},
              {value: 'ONLINE', label: 'Online Account'},
              {value: 'CARD', label: 'Stored Value Card'},
              {value: 'VIRTUAL', label: 'Virtual Currency'}
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
        ref="bankName"
        :binding="binding.bankName"
        label="Bank Name"
        v-on:input="handleInput($event, 'bankName')">
      </input-text>

      <input-text
        ref="accountNumber"
        :binding="binding.accountNumber"
        label="Account Number"
        v-on:input="handleInput($event, 'accountNumber')">
      </input-text>

      <input-text
        ref="value"
        :binding="binding.value"
        label="Value"
        placeholder="0"
        type="integer"
        v-on:input="handleInput($event, 'value')">
      </input-text>
    </div>
  </div>
</template>

<script>
  import {SiteUtils} from "../../scripts/site-common";

  export default {
    props: {
      binding: {
        default() {
          return {
            _key: Math.random(),
            type: {},
            bankName: {},
            accountNumber: {},
            value: {name: 'value'},
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
        this.$set(this.binding, 'bankName', {value: existing.bankName});
        this.$set(this.binding, 'accountNumber', {value: existing.accountNumber});
        this.$set(this.binding, 'value', {value: existing.value});
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
  .bank-account-box {
    border: 1px solid #2896C5;
    margin: 10px 0;
    padding: 10px;
  }
</style>
