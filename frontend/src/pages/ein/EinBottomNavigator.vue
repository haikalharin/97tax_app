<template>
  <div class="top-border">
    <div class="container">
      <div class="wrapper">
        <div class="prev-btn" @click="$emit('back')">
          <img src="@/assets/left-arrow.png" />&nbsp;Previous
        </div>
        <div v-if="!hideContinue" class="continue-btn" @click="$emit('continue')">Continue</div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {};
  },

  methods: {},

  props: ["hideContinue"],
};
</script>
<style scoped>
.top-border {
  background: #ffffff;
  box-shadow: 0px -4px 10px rgba(0, 0, 0, 0.05);
}
.wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 20px 0px;
}
.continue-btn {
  font-family: "Archivo", sans-serif;

  background: #2ac44c;
  border-radius: 8px;

  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;

  color: #ffffff;
  text-align: center;
  padding: 20px 40px;
  cursor: pointer;
}
.prev-btn {
  font-family: "Archivo", sans-serif;

  background: #f4f4f4;
  border-radius: 8px;

  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;

  color: #000000;
  text-align: center;
  padding: 20px 40px;
  cursor: pointer;
}
</style>
