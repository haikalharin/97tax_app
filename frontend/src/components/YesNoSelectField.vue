<template>
  <div class="form-group col-12 col-sm-6">
    <label :for="id">
      {{label}}
      <span v-if="helpText" v-b-popover.hover.bottom="helpText">
        <img :style="imageStyle" src="./../assets/HelpBubble.png"/>
      </span>
    </label>
    <select :id="id" :class="['form-control', error ? 'is-invalid' : '']" v-bind:value="value"
            v-on:change="$emit('input', $event.target.value === '' ? null : $event.target.value === 'true')">
      <!-- <option value="" selected>Please Select</option> -->
      <option value="true">{{yesLabel || 'Yes'}}</option>
      <option value="false">{{noLabel || 'No'}}</option>
    </select>
  </div>
</template>

<script>
  export default {
    name: 'YesNoSelectField',
    props: ['value', 'label', 'id', 'error', 'yesLabel', 'noLabel', 'helpText','imageStyle'],
    data() {
      return {};
    },
    methods: {

    }
  }
</script>
