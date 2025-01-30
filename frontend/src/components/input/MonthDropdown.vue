<template>
  <div :class="classes ? classes : 'form-group col-12 col-sm-6'">
    <label v-if="!!label">{{ label }}</label>
    <select
      :class="['form-control', this.error ? 'is-invalid' : '']"
      style="padding-left: 5px !important; padding-right: 0rem !important"
      v-model="value"
      @change="$emit('input', $event.target.value)"
    >
      <option>01</option>
      <option>02</option>
      <option>03</option>
      <option>04</option>
      <option>05</option>
      <option>06</option>
      <option>07</option>
      <option>08</option>
      <option>09</option>
      <option>10</option>
      <option>11</option>
      <option>12</option>
    </select>
  </div>
</template>

<script>
export default {
  props: ["label", "error", "classes"],
  data() {
    return {
      value: null,
    };
  },
};
</script>
