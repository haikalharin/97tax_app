<template>
  <div class="container pb-2">
    <div class="row">
      <form-input-field
        v-model="personalInfo.firstName"
        :error="errors.firstName"
        label="First Name"
        v-on:input="$emit('input', personalInfo)">
      </form-input-field>

      <form-input-field
        v-model="personalInfo.lastName"
        :error="errors.lastName"
        label="Last Name"
        v-on:input="$emit('input', personalInfo)">
      </form-input-field>

      <form-input-field
        v-model="personalInfo.dateOfBirth"
        :error="errors.dateOfBirth"
        label="Date of Birth"
        placeholder="mm/dd/yyyy"
        v-on:input="$emit('input', personalInfo)">
      </form-input-field>

      <form-input-field
        v-model="personalInfo.ssn"
        :error="errors.ssn"
        label="Social Security Number"
        placeholder="000-00-0000"
        v-on:input="$emit('input', personalInfo)">
      </form-input-field>

      <form-input-field
        v-model="personalInfo.phone"
        :error="errors.phone"
        label="Phone Number"
        placeholder="000-000-0000"
        v-on:input="$emit('input', personalInfo)">
      </form-input-field>

    </div>

    <h2>Home Address</h2>

    <address-input-row
      v-model="personalInfo.homeAddress"
      ref="homeAddress"
      v-on:input="$emit('input', personalInfo)">
    </address-input-row>

    <div class="row">

      <form-input-options
        v-model="personalInfo.homeOwnership"
        label="Home Ownership"
        :options="[
            {value: 'Own', label: 'Own your home'},
            {value: 'Rent', label: 'Rent'},
            {value: 'Other', label: 'Other'}
          ]"
        :error="errors.homeOwnership"
        v-on:input="$emit('input', personalInfo)">
      </form-input-options>

      <form-input-field
        v-model="personalInfo.homeOwnershipDesc"
        :error="errors.homeOwnershipDesc"
        label="Please specify"
        placeholder="e.g. share rent, live with relative, etc"
        v-on:input="$emit('input', personalInfo)">
      </form-input-field>

    </div>

    <h2>Mailing Address</h2>

    <div class="row">
      <yes-no-select-field
        v-model="hasDifferentMailingAddress"
        label="Mailing address different from home address?"
        v-on:input="$emit('input', personalInfo)">
      </yes-no-select-field>
    </div>

    <address-input-row
      v-model="personalInfo.mailingAddress"
      v-if="hasDifferentMailingAddress === true"
      ref="mailingAddress"
      v-on:input="$emit('input', personalInfo)">
    </address-input-row>

    <div class="row">

    </div>

    <div class="row">
      <button class="btn btn-primary" v-on:click="clickContinue">Continue</button>
    </div>

  </div>
</template>
<script>
  import FormInputField from "../FormInputField";
  import StateSelectField from "../StateSelectField";
  import YesNoSelectField from "../YesNoSelectField";
  export default {
    components: {YesNoSelectField, StateSelectField, FormInputField},
    data() {
      return {
        hasDifferentMailingAddress: false,
        personalInfo: {
          firstName: '',
          lastName: '',
          phone: '',
          dateOfBirth: '',
          ssn: '',
          homeAddress: {},
          mailingAddress: {},
          homeOwnership: '',
          homeOwnershipDesc: ''
        },
        errors: {
          firstName: false,
          lastName: false,
          phone: false,
          dateOfBirth: false,
          ssn: false,
          homeOwnership: false,
          homeOwnershipDesc: false
        }
      }
    },
    methods: {
      clickContinue() {
        let isValid = true;
        isValid &= this.$refs.homeAddress.isValid();
        if (this.hasDifferentMailingAddress) {
          isValid &= this.$refs.mailingAddress.isValid();
        }
        //if (isValid) {
          this.$emit('continue', 'personalInfo');
        //}
      }
    }
  }
</script>
