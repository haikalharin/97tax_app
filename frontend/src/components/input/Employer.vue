<template>
  <div class="container employer-box">
    <div class="row">
      <div class="col">
        <button
          class="btn btn-outline-secondary float-right"
          v-on:click="$emit('remove')">Remove</button>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <input-text
          :binding="binding.name"
          label="Name"
          v-on:input="handleInput($event, 'name')">
        </input-text>
      </div>
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

  }
</script>

<style scoped>
  .employer-box {
    border: 1px solid #2896C5;
    margin: 10px 0;
    padding: 10px;
  }
</style>
