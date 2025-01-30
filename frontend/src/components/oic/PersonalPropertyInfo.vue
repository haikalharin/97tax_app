<template>
  <div class="container">
    <div class="row">
      <div class="col">
        <h2>Personal Assets</h2>
      </div>
    </div>

    <div v-for="(a, index) in personalPropertyInfo.realEstate">
      <oic-real-estate-input-row
        :realEstate="personalPropertyInfo.realEstate[index]"
        v-on:input="updateRealEstate(index, $event)"
        v-on:remove="removeRealEstate(a)"
        :key="a._key"
      >
      </oic-real-estate-input-row>
    </div>

    <div class="row mb-2">
      <div class="col">
        <button
          v-on:click="addRealEstate"
          class="btn btn-secondary">
          + Add Real Estate
        </button>
      </div>
    </div>

    <div class="row">
      <div class="col">
        <button class="btn btn-secondary" v-on:click="$emit('back', 'personalPropertyInfo')">Back</button>
        <button class="btn btn-primary" v-on:click="$emit('continue', 'personalPropertyInfo')">Continue</button>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        personalPropertyInfo: {
          realEstate: [
            {
              _key: Math.random()
            }
          ]
        }
      }
    },
    methods: {
      addRealEstate() {
        this.personalPropertyInfo.realEstate.push({
        });
      },
      updateRealEstate(i, value) {
        this.personalPropertyInfo.realEstate.splice(i, 1, value);
      },
      removeRealEstate(a) {
        let index = this.personalPropertyInfo.realEstate.indexOf(a);
        this.personalPropertyInfo.realEstate.splice(index, 1);
      }
    }
  }
</script>
