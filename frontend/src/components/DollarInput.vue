<template>
  <div>
    <div>
      {{ label }}
    </div>
    <div :class="['dollar-input-wrapper', error ? 'is-invalid' : '']">
      <div class="dollar-mark">$</div>
      <input
        :id="id"
        type="number"
        pattern="[0-9]*"
        :name="name || id"
        :class="['dollar-input']"
        :maxlength="maxlength"
        v-bind:value="value"
        v-on:input="
          $emit(
            'input',
            type === 'number'
              ? parseInt($event.target.value)
              : $event.target.value
          )
        "
        v-on:blur="$emit('blur', $event)"
        :disabled="disabled"
        v-mask="mask || ''"
        :min="min || 0"
        :max="max || 9999999"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "DollarInput",
  props: [
    "id",
    "name",
    "type",
    "label",
    "placeholder",
    "value",
    "disabled",
    "error",
    "classes",
    "helpText",
    "maxlength",
    "labelClass",
    "mask",
    "min",
    "max",
  ],
  data() {
    return {};
  },
  methods: {},
  mounted() {},
  beforeDestroy() {},
};
</script>

<style scoped>
.dollar-input-wrapper {
  background-color: #fff;
  border: 1px solid #ced4da;
  width: 100%;
  height: calc(1.5em + 0.75rem + 2px);
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  border-radius: 0.25rem;
  padding: 5px 15px;
  position: relative;
}
.dollar-mark {
  position: absolute;
  left: 10%;
  top: 6px;
  color: #6a6c6e;
}
.dollar-input {
  border: none;
  width: 90%;
  margin-left: 10%;
}
.dollar-input:focus {
  outline: none;
}
.is-invalid {
  border: 1px solid red;
}
</style>
