<template>
  <div class="row">

    <form-input-field
      v-model="address.addressLine1"
      :error="errors.addressLine1"
      label="Address Line 1">
    </form-input-field>

    <form-input-field
      v-model="address.addressLine2"
      :error="errors.addressLine2"
      label="Address Line 2">
    </form-input-field>

    <form-input-field
      v-model="address.addressCity"
      :error="errors.addressCity"
      label="City">
    </form-input-field>

    <state-select-field
      v-model="address.addressState"
      :error="errors.addressState"
      label="State">
    </state-select-field>

    <form-input-field
      v-model="address.addressZip"
      :error="errors.addressZip"
      label="Zip">
    </form-input-field>

  </div>
</template>

<script>
  import {SiteUtils} from '../scripts/site-common'
  export default {
    props: {
      address: {
        type: Object,
        default() {
          return {
            addressLine1: '',
            addressLine2: '',
            addressCity: '',
            addressState: '',
            addressZip: ''
          }
        }
      },
      requiredFields: {
        type: Array,
        default() {
          return ['addressLine1', 'addressCity', 'addressState', 'addressZip']
        }
      }
    },
    data() {
      return {
        errors: {
          addressLine1: false,
          addressLine2: false,
          addressCity: false,
          addressState: false,
          addressZip: false
        }
      }
    },
    methods: {
      isValid: function() {
        let hasErrors = SiteUtils.formErrors(
          this.requiredFields,
          this.address,
          this.errors
        );
        return !hasErrors;
      }
    }
  }
</script>
