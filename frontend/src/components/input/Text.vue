<template>
  <div :class="classes" v-if="!hidden">
    <label v-if="label">
      {{label}}
    </label>
    <input
      v-if="!multiline"
      type="text"
      :class="['form-control', binding.isInvalid ? 'is-invalid' : '']"
      :placeholder="placeholder || label"
      v-model="binding.value"
      v-on:input="handleInput($event)"
      v-on:blur="handleBlur($event)"
      :disabled="disabled">
    <textarea
      v-if="multiline"
      :class="['form-control', binding.isInvalid ? 'is-invalid' : '']"
      :placeholder="placeholder"
      v-model="binding.value"
      v-on:input="handleInput($event)"
      v-on:blur="handleBlur($event)"
      :disabled="disabled">
    </textarea>
  </div>
</template>

<script>
  import {SiteUtils} from '../../scripts/site-common'

  export default {
    name: 'InputText',
    props: {
      classes: {
        type: String,
        default: "form-group col-12 col-sm-6"
      },
      type: {
        type: String,
        default() {
          return 'string'
        },
        validator(value) {
          return ['string', 'integer'].indexOf(value) !== -1;
        }
      },
      binding: {
        type: Object,
        default() {
            return {
              name: null,
              value: '',
              isInvalid: false
            }
          }
      },
      label: String,
      placeholder: String,
      disabled: {
        type: Boolean,
        default: false
      },
      hidden: Boolean,
      required: Boolean,
      regex: RegExp,
      multiline: Boolean,
      minWords: Number,
      validationType: String
    },
    methods: {
      validate() {
        let value = this.binding.value;
        let isInvalid = false;
        if (value != null && this.type === 'integer') {
          if ((typeof value) !== 'number') {
            value = value.replace(/[^0-9]/g, '');
          }
        }

        if (this.required && (value == null || value.length === 0)) {
          isInvalid = true;
        } else {
          if (this.regex) {
            isInvalid = !this.regex.test(value);
          } else {
            isInvalid = false;
          }
        }

        if (value != null && this.type === 'integer') {
          value = parseInt(value);
          if (isNaN(value)) {
            value = '';
          }
        }

        if (value && this.minWords) {
          let words = value.split(/\s+/);
          if (words.length < this.minWords) {
            isInvalid = true;
          }
        }

        if (this.validationType) {
          if (this.validationType === 'email') {
            if (!SiteUtils.validateEmail(value)) {
              isInvalid = true;
            }
          }else if (this.validationType === 'phone') {
            if (!SiteUtils.validatePhone(value)) {
              isInvalid = true;
            }
          }
        }

        this.$set(this.binding, 'value', value);
        this.$set(this.binding, 'isInvalid', isInvalid);
        this.$emit('input', this.binding);
        return !isInvalid;
      },
      handleInput(event) {
        this.binding.value = event.target.value;
        this.validate();
      },
      handleBlur(event) {
        if (this.type === 'string') {
          this.binding.value = this.binding.value ? this.binding.value.trim() : null;
        }
        this.validate();
        this.$emit('blur', event);
      }
    }
  }
</script>
