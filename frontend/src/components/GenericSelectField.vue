<template>
  <div :class="classes ? classes : 'form-group col-12 col-sm-6'">
    <label v-if="label" :for="id">
      {{label}}
      <span v-if="helpText" v-b-popover.hover.bottom="helpText">
        <img :style="imageStyle" src="./../assets/HelpBubble.png"/>
      </span>
    </label>
    <select :id="id" :class="['form-control', error ? 'is-invalid' : '']" v-bind:value="value"
            v-on:change="$emit('input', $event.target.value)">
      <!-- <option v-if="!withoutPlaceHolder" value="" selected>Please Select</option> -->
      <slot></slot>
    </select>
  </div>
</template>

<script>
  export default {
    name: 'YesNoSelectField',
    props: {
      value: String,
      label: String,
      id: String,
      error: String | Boolean,
      helpText: String,
      classes: String,
      withoutPlaceHolder: Boolean,
      imageStyle: String
    },
    data() {
      return {};
    },
    methods: {}
  }
</script>
