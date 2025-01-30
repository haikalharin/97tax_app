<template>
  <div :class="classes" v-if="!hidden">
    <div class="form-check">
    <label :class="['form-check-label', binding.isInvalid ? 'text-danger' : '']">
      <input
        type="checkbox"
        class="form-check-input"
        v-model="binding.value"
        v-on:input="handleInput($event)"
        v-on:blur="handleBlur($event)"
        :disabled="disabled">
      {{label}}
    </label>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'InputCheckbox',
    props: {
      classes: {
        type: String,
        default: "form-group col-12 col-sm-6"
      },
      binding: {
        type: Object,
        default() {
          return {
            value: false,
            isInvalid: false
          }
        }
      },
      label: String,
      disabled: {
        type: Boolean,
        default: false
      },
      hidden: Boolean,
      required: Boolean
    },
    methods: {
      validate() {
        let value = this.binding.value;

        if (this.required && !value) {
          this.binding.isInvalid = true;
        } else {
          this.binding.isInvalid = false;
        }

        this.$emit('input', this.binding);

        this.$forceUpdate();
        return !this.binding.isInvalid;
      },
      handleInput(event) {
        this.binding.value = event.target.checked;
        this.validate();
      },
      handleBlur(event) {
        this.validate();
        this.$emit('blur', event);
      }
    }
  }
</script>
