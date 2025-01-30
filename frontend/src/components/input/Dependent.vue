<template>
  <div class="container dependent-box">
    <div class="row">
      <div class="col">
        <button
          class="btn btn-outline-secondary float-right"
          v-on:click="$emit('remove')">Remove</button>
      </div>
    </div>
    <div class="row">
      <input-text
        :binding="binding.name"
        label="Name"
        v-on:input="handleInput($event, 'name')">
      </input-text>
      <input-text
        :binding="binding.age"
        label="Age"
        type="integer"
        v-on:input="handleInput($event, 'age')">
      </input-text>
      <input-text
        :binding="binding.relationship"
        label="Relationship"
        v-on:input="handleInput($event, 'relationship')">
      </input-text>
    </div>
    <div class="row">
      <input-options
        :binding="binding.dependentOn1040"
        label="Claimed as a dependent on your Form 1040?"
        v-on:input="handleInput($event, 'dependentOn1040')">
      </input-options>
      <input-options
        :binding="binding.contributesIncome"
        label="Contributes to household income?"
        v-on:input="handleInput($event, 'contributesIncome')">
      </input-options>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      binding: {
        default() {
          return {
            _key: Math.random(),
            name: {},
            age: {},
            relationship: {},
            dependentOn1040: {},
            contributesIncome: {}
          }
        }
      }
    },
    methods: {
      validate() {

      },
      prefill(existing) {
        this.$set(this.binding, 'name', {value: existing.name});
        this.$set(this.binding, 'age', {value: existing.age});
        this.$set(this.binding, 'relationship', {value: existing.relationship});
        this.$set(this.binding, 'dependentOn1040', {value: existing.dependentOn1040});
        this.$set(this.binding, 'contributesIncome', {value: existing.contributesIncome});
      },

      handleInput(event, name) {
        this.binding[name] = event;
        this.$emit('input', this.binding);
      }
    }
  }
</script>

<style scoped>
  .dependent-box {
    border: 1px solid #2896C5;
    margin: 10px 0;
    padding: 10px;
  }
</style>
