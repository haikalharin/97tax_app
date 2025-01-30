<template>
  <div class="row">
    <label class="col col-form-label font-weight-bold">{{label}}</label>
    <div class="col d-flex align-items-center">
      <div v-if="type === 'boolean'">
        <input
          type="checkbox"
          :checked="propsValue"
          true-value="true"
          false-value="false"
          :value="true"
          @click="emitChange($event)"
          @keyup= "$emit('keyup', $event.target.value)"
          :disabled="disabled"
          class="mr-1"
        ><label class="mr-3">Yes</label>

         <input
          type="checkbox"
          :checked="!propsValue"
          true-value="true"
          false-value="false"
          :value="false"
          @click="emitChange($event)"
          @keyup= "$emit('keyup', $event.target.value)"
          :disabled="disabled"
          class="mr-1"
        ><label>No</label>
      </div>
      
      <div v-else>
        <input 
        :class="['form-control', this.error ? 'is-invalid' : '']"
        v-on:keyup="$emit('keyup', $event.target.value)" 
        :type="type || 'text'"
        :value="value"
        v-on:input="emitChange($event)"
        v-mask='mask?mask:""'
        :disabled="disabled">
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: [
      'label',
      'value',
      'type',
      'mask',
      'disabled',
      "error"
    ],
    data() {
      return {
        propsValue: false
      }
    },
    mounted() {
      this.propsValue = this.value;
    },
    methods: {
      emitChange(event) {
        if (this.type === 'boolean') {
          this.propsValue = typeof event.target.value === 'string' ? event.target.value === 'true' : event.target.value;
          this.$emit('input', this.propsValue);
        } else {
          this.$emit('input', event.target.value);
        }
      }
    }
  }
</script>
<style>
  .was-validated .form-control:invalid, .form-control.is-invalid, .was-validated .custom-select:invalid, .custom-select.is-invalid {
    border-width: 2px;
    background-image: none;
  }
</style>
