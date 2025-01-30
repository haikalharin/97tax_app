<template>
  <b-modal
    v-model="show"
    title="I Understand the Following:"
    centered
    size="lg"
    class="payment-confirmation-modal"
    @hidden="onHidden"
  >
    <div class="w-100">
      <div class="mb-1">
        <p class="mx-4 my-3">1. The card I entered will be charged <strong v-if="amount!==null">${{ amount }}</strong><strong v-else>$97</strong> immediately by the website 97tax.com, a private tax preparation company.</p>
        <p class="mx-4 my-3">2. This is a one time-fee paid to 97tax to prepare my tax documents, and it will not apply to my tax balance.</p>
        <p class="mx-4 my-3">3. I will see "97TAX" on my credit or debit card statement within next couple of days, and I will check my email for updates from 97tax.</p>
        <p class="mx-4 my-3" v-if="efile">4. I authorize 97tax to use IRS e-file to file my tax documents electronically if able.</p>
      </div>
      <div class="mx-4">
        <img src="@/assets/card_transations_mockup_updated.png" class="w-auto">
      </div>
    </div>
    <template slot="modal-footer">
      <div class="w-100 mx-4 my-3 d-flex justify-content-between align-items-center">
        <div>
        <div>
          <label class="custom-checkbox-container">I Understand
            <input type="checkbox" id="understandPayment" v-model="understandPayment" v-on:click="error = false">
            <span class="checkmark"></span>
          </label>
        </div>
        <div v-if="error" class="text-danger">
          You must check this box first to continue.
        </div>
        </div>
        <button class="btn btn-primary btn-continue float-right" @click="ok">
          Continue
        </button>
        </div>
    </template>
  </b-modal>
</template>
<script>

export default {
  props: {
    value: {
      required: true
    },
    amount: {
      required: false
    },
    efile: {
      required: false
    }
  },
  data() {
    return {
      show: this.value,
      understandPayment: false,
      error: false,
      isEmittedContinue: false
    }
  },
  methods: {
    onHidden() {
      this.$emit('input', this.show);
      if(!this.isEmittedContinue)
        this.$emit('cancelPayment');
      this.isEmittedContinue = false;
    },
    ok() {
      if(this.understandPayment){
        this.isEmittedContinue = true;
        this.$emit('continuePayment');
      }
      else
        this.error = true;
    }
  },
  watch: {
    value: function(newVal, oldVal) {
      console.log('Prop changed: ', newVal, ' | was: ', oldVal)
      this.show = this.value;
    }
  }
};
</script>
