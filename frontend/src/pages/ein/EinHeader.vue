<template>
  <div class="header-wrapper">
    <div class="container header-group">
      <div class="header-left">
        <div><img src="@/assets/LogoHeader.png" width="188" /></div>
        <!--
        <div class="header-title">
          Application for Employer Identification Number
        </div>
        -->
      </div>
      <div class="header-right">
        <div class="lang-selector">
          <b-dropdown :right="true" variant="outline-link">
            <template #button-content>
              <div v-if="currentLang == 'US'" class="lang-inner">
                <img src="@/assets/flags/united-states.png" width="28" />
                <span>English</span>
              </div>
              <div v-if="currentLang == 'Spain'" class="lang-inner">
                <img src="@/assets/flags/spain.png" width="28" />
                <span>Spain&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
              </div>
            </template>
            <b-dropdown-item href="#" @click="onClickUnitedStates">
              <img src="@/assets/flags/united-states.png" width="28" /> English
            </b-dropdown-item>
            <b-dropdown-item href="#" @click="onClickSpain">
              <img src="@/assets/flags/spain.png" width="28" /> Spain
            </b-dropdown-item>
          </b-dropdown>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      currentLang: "US",
    };
  },

  methods: {
    onClickUnitedStates() {
      this.currentLang = "US";
    },
    onClickSpain() {
      this.currentLang = "Spain";
    },
  },
};
</script>
<style scoped>
.header-wrapper {
  padding: 25px 0px;
  margin: 0px;
  width: 100%;
  background-color: #0094be;
}
.header-title {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 20px;
  line-height: 24px;
  color: #ffffff;
  padding: 22px 0px 0px 10px;
}

.header-group {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.header-left {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  flex-wrap: wrap;
}

.lang-selector {
  background-color: white !important;
  color: black;
  border: 1px solid #fff;
  border-radius: 8px;
}

.lang-inner {
  display: inline-block;
  text-align: left;

  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 19px;
}
</style>
