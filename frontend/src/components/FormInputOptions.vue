<template>
  <div class="form-group col-12 col-sm-6">
    <label>
      {{label}}
    </label>
    <label class="d-block" v-for="option in options">
      <input
        type="radio"
        :value="option.value"
        :checked="value === option.value"
        v-on:input="$emit('input', $event.target.value)"
      >
      {{option.label || option.value}}
    </label>
  </div>
</template>

<script>
  export default {
    name: 'FormInputField',
    props: {
      label: String,
      options: Array,
      value: String,
      disabled: Boolean,
      error: Boolean
    },
    data () {
      return {
      }
    }
  }
</script>
