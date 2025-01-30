<template>
  <div class="container">
    <div v-if="taxLienRemovalDetails != null">

      <admin-input
        label="Got IRS Form"
        v-model="taxLienRemovalDetails.gotIrsForm"
        type="boolean"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Existing Tax Lien?"
        v-model="taxLienRemovalDetails.existingTaxLien"
        type="boolean"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Is Business?"
        v-model="taxLienRemovalDetails.taxLienIsBusiness"
        type="boolean"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Tax Lien Type"
        v-model="taxLienRemovalDetails.taxLienType"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Serial Number"
        v-model="taxLienRemovalDetails.serialNumber"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Tax Lien Business Type"
        v-model="taxLienRemovalDetails.taxLienBusinessName"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="EIN"
        v-model="taxLienRemovalDetails.taxLienBusinessEin"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Remediation Type"
        v-model="taxLienRemovalDetails.taxLienRemediationType"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Remediation Description"
        v-model="taxLienRemovalDetails.taxLienRemediationDescription"
        :disabled="!isEditMode"
      ></admin-input>
      <admin-input
        label="Payments Currently Automatic Debit?"
        v-model="taxLienRemovalDetails.taxLienAutomaticDebit"
        type="boolean"
        :disabled="!isEditMode"
      ></admin-input>

     <!-- <div class="row">
        <div class="col">
          <button :disabled="disableButtons || !isEditMode" class="btn btn-secondary" @click="saveTaxLien()">Save Tax Lien Details</button>
        </div>
      </div> -->

    </div>
  </div>
</template>

<script>
  import {AXIOS} from '../../scripts/http-common';

  export default {
    props: {
      orderNum: Number,
      isEditMode: Boolean
    },
    data() {
      return {
        disableButtons: false,
        taxLienRemovalDetails: null
      }
    },
    methods: {
      saveTaxLien() {
        this.disableButtons = true;
        AXIOS.put(`/admin/taxlien`, this.taxLienRemovalDetails)
          .then(response => {
            alert("Saved!");
            this.disableButtons = false;
          })
          .catch(e => {
            alert("Error");
            console.log(e);
            this.disableButtons = false;
          })
      }
    },
    mounted() {
      let self = this;
      this.disableButtons = true;
      AXIOS.get(`/admin/taxlien/orderNum/${this.orderNum}`)
        .then(response => {
          self.$set(self, 'taxLienRemovalDetails', response.data);
          this.disableButtons = false;
        })
        .catch(e => {
          console.log(e);
          this.disableButtons = false;
        })
    }

  }

</script>
