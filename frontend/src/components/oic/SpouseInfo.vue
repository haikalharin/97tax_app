<template>
  <div class="container pb-2">
    <div class="row">
      <form-input-options
        v-model="spouseInfo.maritalStatus"
        label="Marital Status"
        :options="[
            {value: 'Unmarried'},
            {value: 'Married'}
          ]"
        :error="errors.homeOwnership"
        v-on:input="$emit('input', spouseInfo)">
      </form-input-options>
    </div>

    <div class="row" v-if="spouseInfo.maritalStatus === 'Married'">
      <form-input-field
        v-model="spouseInfo.spouseFirstName"
        :error="errors.spouseFirstName"
        label="Spouse First Name"
        v-on:input="$emit('input', spouseInfo)">
      </form-input-field>

      <form-input-field
        v-model="spouseInfo.spouseLastName"
        :error="errors.spouseLastName"
        label="Spouse Last Name"
        v-on:input="$emit('input', spouseInfo)">
      </form-input-field>

      <form-input-field
        v-model="spouseInfo.spouseDateOfBirth"
        :error="errors.spouseDateOfBirth"
        label="Spouse Date of Birth"
        placeholder="mm/dd/yyyy"
        v-on:input="$emit('input', spouseInfo)">
      </form-input-field>

      <form-input-field
        v-model="spouseInfo.spouseSsn"
        :error="errors.spouseSsn"
        label="Spouse Social Security Number"
        placeholder="000-00-0000"
        v-on:input="$emit('input', spouseInfo)">
      </form-input-field>
    </div>

    <div class="row">
      <form-input-field
        v-model="spouseInfo.numDependents"
        label="Number of Dependents"
        :error="errors.numDependents"
        type="number"
        v-on:input="changeDependents">
      </form-input-field>
    </div>

    <div v-for="(d, index) in spouseInfo.dependents">
      <oic-dependent
        v-model="spouseInfo.dependents[index]"
        v-on:input="$emit('input', spouseInfo)">
      </oic-dependent>
    </div>

    <div class="row">
      <div class="col">
        <button class="btn btn-secondary" v-on:click="$emit('back', 'spouseInfo')">Back</button>
        <button class="btn btn-primary" v-on:click="clickContinue">Continue</button>
      </div>
    </div>

  </div>
</template>
<script>
  import FormInputField from "../FormInputField";
  import StateSelectField from "../StateSelectField";
  export default {
    components: {StateSelectField, FormInputField},
    data() {
      return {
        spouseInfo: {
          maritalStatus: '',
          spouseFirstName: '',
          spouseLastName: '',
          spouseDateOfBirth: '',
          spouseSsn: '',
          numDependents: 0,
          dependents: []
        },
        errors: {
          maritalStatus: false,
          spouseFirstName: false,
          spouseLastName: false,
          spouseDateOfBirth: false,
          spouseSsn: false,
          numDependents: false
        }
      }
    },
    methods: {
      changeDependents() {
        let difference = this.spouseInfo.numDependents - this.spouseInfo.dependents.length;
        if (difference < 0) {
          for (let i=this.spouseInfo.dependents.length; i>=this.spouseInfo.numDependents; i--) {
            this.spouseInfo.dependents.splice(i, 1);
          }
        } else {
          for (let i=this.spouseInfo.dependents.length; i<this.spouseInfo.numDependents; i++) {
            this.spouseInfo.dependents[i] = {};
          }
        }
      },
      clickContinue() {
        console.log(this.spouseInfo.dependents[0]);
        this.$emit('continue', 'spouseInfo');
      }
    }
  }
</script>
