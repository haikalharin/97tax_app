<template>
  <div :class="classes ? classes : 'form-group col-12 col-sm-6'">
    <label :for="id" v-if="label" :class="labelClass?labelClass:''">
      {{label}}
      <span v-if="helpText" v-b-popover.hover.bottom="helpText">
        <img src="./../assets/HelpBubble.png"/>
      </span>
    </label>
    <input
      :id="id"
      :type="type || 'text'"
      :name="name || id"
      :class="['form-control', this.error ? 'is-invalid' : '']"
      :placeholder="placeholder || label"
      :maxlength="maxlength"
      v-bind:value="value"
      v-on:input="$emit('input', type === 'number' ? parseInt($event.target.value) : $event.target.value )"
      v-on:blur="$emit('blur', $event)"
      :disabled="disabled"
      v-mask="mask ||''"
      v-if="(name !== 'phoneNumber') && (name !== 'secondaryPhone') && (name !== 'ssn') && (name !== 'cardNumber') && (name !== 'ein')">
    <input
      :id="id"
      :type="type || 'text'"
      :name="name || id"
      :class="['form-control', this.error ? 'is-invalid' : '']"
      :placeholder="placeholder || label"
      :maxlength="maxlength"
      v-bind:value="value"
      v-on:input="$emit('input', type === 'number' ? parseInt($event.target.value) : $event.target.value )"
      v-on:blur="$emit('blur', $event)"
      :disabled="disabled"
      v-if="name === 'phoneNumber' || name === 'secondaryPhone'"
      v-mask="'###-###-####'">
    <input
      :id="id"
      :type="type || 'text'"
      :name="name || id"
      :class="['form-control', this.error ? 'is-invalid' : '']"
      :placeholder="placeholder || label"
      :maxlength="maxlength"
      v-bind:value="value"
      v-on:input="$emit('input', type === 'number' ? parseInt($event.target.value) : $event.target.value )"
      v-on:blur="$emit('blur', $event)"
      :disabled="disabled"
      v-if="name === 'ssn'"
      v-mask="'###-##-####'">
    <input
      :id="id"
      :type="type || 'text'"
      :name="name || id"
      :class="['form-control', this.error ? 'is-invalid' : '']"
      :placeholder="placeholder || label"
      :maxlength="maxlength"
      v-bind:value="value"
      v-on:input="$emit('input', type === 'number' ? parseInt($event.target.value) : $event.target.value )"
      v-on:blur="$emit('blur', $event)"
      :disabled="disabled"
      v-if="name === 'cardNumber'"
      v-mask="'#### #### #### ####'">
    <input
      :id="id"
      :type="type || 'text'"
      :name="name || id"
      :class="['form-control', this.error ? 'is-invalid' : '']"
      :placeholder="placeholder || label"
      :maxlength="maxlength"
      v-bind:value="value"
      v-on:input="$emit('input', type === 'number' ? parseInt($event.target.value) : $event.target.value )"
      v-on:blur="$emit('blur', $event)"
      :disabled="disabled"
      v-if="name === 'ein'"
      v-mask="'##-#######'">

  </div>
</template>

<script>
  export default {
    name: 'FormInputField',
    props: ['id', 'name', 'type', 'label', 'placeholder', 'value', 'disabled', 'error', 'classes', 'helpText', 'maxlength','labelClass','mask'],
    data () {
      return {
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
