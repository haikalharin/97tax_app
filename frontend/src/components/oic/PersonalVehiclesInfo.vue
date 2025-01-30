<template>
  <div class="container">
    <div class="row">
      <div class="col">
        <h2>Vehicles</h2>
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
  </div>
</template>

<script>
  export default {
    data() {
      return {
        personalVehiclesInfo: {
          vehicles: [
            {
              _key: Math.random()
            }
          ]
        }
      }
    },

  }
</script>
