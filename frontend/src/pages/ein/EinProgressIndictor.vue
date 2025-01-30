<template>
  <div class="container">
    <div class="indicator-container p40">
      <div>
        <div class="indicator-label" v-html="title"></div>
        <div class="indicator-sub-label" v-html="subtitle"></div>
      </div>
      <div class="progress-bar-wrapper" v-if="!hideProgress">
        <div class="text-wrapper">
          <div>Application Progress</div>
          <div
            :class="'progress-value ' + (value >= 99 ? 'pg-clr-success' : '')"
          >
            {{ value }}%
          </div>
        </div>
        <div class="progress-bar">
          <div
            :class="'current-progress ' + (value >= 99 ? 'pg-bg-success' : '')"
            :style="'width: ' + value + '%'"
          ></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ["title", "subtitle", "value", "hideProgress"],
};
</script>
<style scoped>
.indicator-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
}
.indicator-label {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 30px;
  line-height: 36px;
  display: flex;
  align-items: center;

  color: #2a96c4;
}
.indicator-sub-label {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 14px;
  line-height: 21px;
  color: #494949;
  margin-top: 10px;
}

.progress-bar-wrapper {
  font-family: "Archivo", sans-serif;
  width: 350px;
  padding: 20px;
  background: #ffffff;
  box-shadow: 0px 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 19px;
}
.text-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-bottom: 10px;
}
.progress-value {
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;

  color: #2a96c4;
}

.progress-bar {
  position: relative;
  width: 310px;
  height: 7px;

  background: #f7f8f9;
  border-radius: 50px;
}

.current-progress {
  position: absolute;
  height: 7px;
  left: 0px;
  top: 0px;
  background: #2a96c4;
  border-radius: 50px;
}

.p40 {
  padding: 40px 0px;
}

.pg-clr-success {
  color: #2ac44c;
}

.pg-bg-success {
  background: #2ac44c;
}
</style>