<template>
  <div class="container">

    <div class="row">
      <form-input-field
        v-model="employerInfo.employerName"
        :error="errors.employerName"
        label="Your Employer's Name"
        v-on:input="$emit('input', employerInfo)">
      </form-input-field>
    </div>

    <address-input-row
      v-model="employerInfo.employerAddress"
      ref="employerAddress"
      v-on:input="$emit('input', employerInfo)">
    </address-input-row>

    <div class="row">
      <yes-no-select-field
        v-model="employerInfo.employerOwnership"
        label="Do you have an ownership interest in this business? "
        v-on:input="$emit('input', employerInfo)">
      </yes-no-select-field>

      <form-input-options
        v-if="employerInfo.employerOwnership"
        v-model="employerInfo.businessInterest"
        label="Specify business interest:"
        :options="[
            {value: 'Partner', label: 'Partner'},
            {value: 'Officer', label: 'Officer'},
            {value: 'Proprietor', label: 'Sole Proprietor'}
          ]"
        :error="errors.businessInterest"
        v-on:input="$emit('input', employerInfo)">
      </form-input-options>

    </div>

    <div class="row">
      <form-input-field
        v-model="employerInfo.occupation"
        :error="errors.occupation"
        label="Your Occupation"
        v-on:input="$emit('input', employerInfo)">
      </form-input-field>
      <div class="col-6">
        <label>
          How long with this employer?
        </label>
        <div class="row">
          <form-input-field
            v-model="employerInfo.employDurationYears"
            :error="errors.employDurationYears"
            label="Years"
            v-on:input="$emit('input', employerInfo)">
          </form-input-field>
          <form-input-field
            v-model="employerInfo.employDurationMonths"
            :error="errors.employDurationMonths"
            label="Months"
            v-on:input="$emit('input', employerInfo)">
          </form-input-field>
        </div>
      </div>
    </div>

    <div v-if="maritalStatus === 'Married'">
      <div class="row">
        <form-input-field
          v-model="employerInfo.spouseInfo.employerName"
          :error="spouseErrors.employerName"
          label="Spouse's Employer's Name"
          v-on:input="$emit('input', employerInfo)">
        </form-input-field>
      </div>

      <address-input-row
        v-model="employerInfo.spouseInfo.employerAddress"
        ref="employerAddress"
        v-on:input="$emit('input', employerInfo)">
      </address-input-row>

      <div class="row">
        <yes-no-select-field
          v-model="employerInfo.spouseInfo.employerOwnership"
          label="Does your spouse have an ownership interest in this business? "
          v-on:input="$emit('input', employerInfo)">
        </yes-no-select-field>

        <form-input-options
          v-if="employerInfo.spouseInfo.employerOwnership"
          v-model="employerInfo.spouseInfo.businessInterest"
          label="Specify business interest:"
          :options="[
            {value: 'Partner', label: 'Partner'},
            {value: 'Officer', label: 'Officer'},
            {value: 'Proprietor', label: 'Sole Proprietor'}
          ]"
          :error="spouseErrors.businessInterest"
          v-on:input="$emit('input', employerInfo)">
        </form-input-options>

      </div>

      <div class="row">
        <form-input-field
          v-model="employerInfo.spouseInfo.occupation"
          :error="spouseErrors.occupation"
          label="Spouse's Occupation"
          v-on:input="$emit('input', employerInfo)">
        </form-input-field>
        <div class="col-6">
          <label>
            How long with this employer?
          </label>
          <div class="row">
            <form-input-field
              v-model="employerInfo.spouseInfo.employDurationYears"
              :error="spouseErrors.employDurationYears"
              label="Years"
              v-on:input="$emit('input', employerInfo)">
            </form-input-field>
            <form-input-field
              v-model="employerInfo.spouseInfo.employDurationMonths"
              :error="spouseErrors.employDurationMonths"
              label="Months"
              v-on:input="$emit('input', employerInfo)">
            </form-input-field>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col">
        <button class="btn btn-secondary" v-on:click="$emit('back', 'employmentInfo')">Back</button>
        <button class="btn btn-primary" v-on:click="$emit('continue', 'employmentInfo')">Continue</button>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: ['maritalStatus'],
    data() {
      return {
        employerInfo: {
          employerName: '',
          employerAddress: {},
          employerOwnership: '',
          businessInterest: '',
          occupation: '',
          employDurationYears: '',
          employDurationMonths: '',
          spouseInfo: {
            employerName: '',
            employerAddress: {},
            employerOwnership: '',
            businessInterest: '',
            occupation: '',
            employDurationYears: '',
            employDurationMonths: ''
          }
        },

        errors: {
          employerName: false,
          employerAddress: false,
          employerOwnership: false,
          businessInterest: false,
          occupation: false,
          employDurationYears: false,
          employDurationMonths: false
        },
        spouseErrors: {
          employerName: false,
          employerAddress: false,
          employerOwnership: false,
          businessInterest: false,
          occupation: false,
          employDurationYears: false,
          employDurationMonths: false
        }
      }
    }
  }
</script>
