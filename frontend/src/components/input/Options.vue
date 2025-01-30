<template>
  <div :class="classes" v-if="!hidden">
    <label v-if="label">
      {{label}}
    </label>
    <div v-if="type === 'radio'">
      <div v-for="option in options">
        <label :class="binding.isInvalid ? 'text-danger' : ''">
          <input
            type="radio"
            v-model="binding.value"
            :value="option.value"
            v-on:input="handleInput($event)"
            v-on:blur="handleBlur($event)">
          {{option.label}}
        </label>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'InputOptions',
    props: {
      classes: {
        type: String,
        default: "form-group col-12 col-sm-6"
      },
      type: {
        type: String,
        default() {
          return 'radio'
        },
        validator(value) {
          return ['select', 'radio'].indexOf(value) !== -1;
        }
      },
      options: {
        type: Array,
        default() {
          return [
            {value: true, label: 'Yes'},
            {value: false, label: 'No'}
          ]
        }
      },
      binding: {
        type: Object,
        default() {
          return {
            name: null,
            value: null,
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

        if (this.required && value === null) {
          this.$set(this.binding, 'isInvalid', true);
        } else {
          this.$set(this.binding, 'isInvalid', false);
        }

        this.$set(this.binding, 'value', value);
        this.$emit('input', this.binding);

        return !this.binding.isInvalid;
      },
      handleInput(event) {
        this.binding.value = event.target.value;
        this.validate();
      },
      handleBlur(event) {
        this.validate();
        this.$emit('blur', event);
      }
    }
  }
</script>
