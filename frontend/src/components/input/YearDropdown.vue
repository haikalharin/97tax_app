<template>
  <div :class="classes ? classes : 'form-group col-12 col-sm-6'">
    <label v-if="!!label">{{ label }}</label>
    <select
      :class="['form-control', this.error ? 'is-invalid' : '']"
      style="padding-left: 5px !important; padding-right: 0rem !important"
      v-model="value"
      @change="$emit('input', $event.target.value)"
    >
      <option v-for="option in options">{{ option }}</option>
    </select>
  </div>
</template>

<script>
import moment from "moment";

export default {
  props: ["error", "classes", "label"],
  data() {
    return {
      options: [],
      value: null,
    };
  },
  mounted() {
    let currentYear = moment().year();
    for (let i = currentYear; i <= currentYear + 15; i++) {
      this.options.push(i);
    }
  },
};
</script>
