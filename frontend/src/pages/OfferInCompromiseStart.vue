<template>
  <page class="home-page">
    <div class="container">
      <h1 class="text-center">Offer In Compromise</h1>
    </div>
    <div class="screen-transition-container">
      <oic-personal-info
        v-model="screenData.personalInfo"
        :class="['screen-transition', screenTransitions.personalInfo]"
        v-on:continue="pageTransitionContinue">
      </oic-personal-info>
      <oic-spouse-info
        v-model="screenData.spouseInfo"
        :class="['screen-transition', screenTransitions.spouseInfo]"
        v-on:back="pageTransitionBack"
        v-on:continue="pageTransitionContinue">
      </oic-spouse-info>
      <oic-employment-info
        v-model="screenData.employmentInfo"
        :class="['screen-transition', screenTransitions.employmentInfo]"
        :maritalStatus="screenData.spouseInfo.maritalStatus"
        v-on:back="pageTransitionBack"
        v-on:continue="pageTransitionContinue">
      </oic-employment-info>
      <oic-personal-assets-info
        v-model="screenData.personalAssetsInfo"
        :class="['screen-transition', screenTransitions.personalAssetsInfo]"
        v-on:back="pageTransitionBack"
        v-on:continue="pageTransitionContinue">
      </oic-personal-assets-info>
      <oic-personal-investments-info
        v-model="screenData.personalInvestmentsInfo"
        :class="['screen-transition', screenTransitions.personalInvestmentsInfo]"
        v-on:back="pageTransitionBack"
        v-on:continue="pageTransitionContinue">
      </oic-personal-investments-info>
      <oic-life-insurance-info
        v-model="screenData.lifeInsuranceInfo"
        :class="['screen-transition', screenTransitions.lifeInsuranceInfo]"
        v-on:back="pageTransitionBack"
        v-on:continue="pageTransitionContinue">
      </oic-life-insurance-info>
      <oic-personal-property-info
        v-model="screenData.personalPropertyInfo"
        :class="['screen-transition', screenTransitions.personalPropertyInfo]"
        v-on:back="pageTransitionBack"
        v-on:continue="pageTransitionContinue">
      </oic-personal-property-info>
      <oic-personal-vehicles-info
        v-model="screenData.personalVehiclesInfo"
        :class="['screen-transition', screenTransitions.personalVehiclesInfo]"
        v-on:back="pageTransitionBack"
        v-on:continue="pageTransitionContinue">
      </oic-personal-vehicles-info>
    </div>
  </page>
</template>

<!-- sharea sale tag -->
<script>
  var shareasaleSSCID=shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e,a,r,s,t){
      if(e&&a)
            {var o,n=s?"; path="+s:"",i=t?"; domain="+t:"",l="";r&&((o=new Date).setTime(o.getTime()+r),l="; expires="+o.toUTCString()),
        document.cookie=e+"="+a+l+n+i
      }}
        function shareasaleGetParameterByName(e,a)
        {a||(a=window.location.href),e=e.replace(/[\[\]]/g,"\\$&");
        var r=new RegExp("[?&]"+e+"(=([^&#]*)|&|#|$)").exec(a);
return r?r[2]?decodeURIComponent(r[2].replace(/\+/g," ")):"":null}
shareasaleSSCID&&shareasaleSetCookie("shareasaleSSCID",
shareasaleSSCID,94670778e4,"/");
</script>

<script>
  export default {
    data() {
      return {
        screenData: {
          personalInfo: {},
          spouseInfo: {},
          employmentInfo: {},
          personalAssetsInfo: {},
          personalInvestmentsInfo: {},
          lifeInsuranceInfo: {},
          personalPropertyInfo: {},
          personalVehiclesInfo: {}
        },
        screenTransitions: {
          personalInfo: '',
          spouseInfo: '',
          employmentInfo: '',
          personalAssetsInfo: '',
          personalInvestmentsInfo: '',
          lifeInsuranceInfo: '',
          personalPropertyInfo: '',
          personalVehiclesInfo: ''
        }
      }
    },
    mounted() {
      this.screenTransitions.personalInfo = 'active';

    },
    methods: {
      getPageOrder() {
        return [
          'personalInfo',
          'spouseInfo',
          'employmentInfo',
          'personalAssetsInfo',
          'personalInvestmentsInfo',
          'lifeInsuranceInfo',
          'personalPropertyInfo',
          'personalVehiclesInfo'
        ];
      },
      pageTransitionContinue(currentPage) {
        let pageOrder = this.getPageOrder();
        let i = pageOrder.indexOf(currentPage);
        this.screenTransitions[currentPage] = 'inactive';
        this.screenTransitions[pageOrder[i+1]] = 'active';
      },
      pageTransitionBack(currentPage) {
        let pageOrder = this.getPageOrder();
        let i = pageOrder.indexOf(currentPage);
        this.screenTransitions[pageOrder[i-1]] = 'active';
        this.screenTransitions[currentPage] = '';
      }
    }
  }
</script>

<style scoped>
  .screen-transition-container {
    position: relative;
    overflow: hidden;
    max-width: 100%;
  }
  .screen-transition {
    position: absolute;
    transform: translate(150%, 0);
    transition: transform .25s;
  }
  .screen-transition.active {
    position: initial;
    transform: translate(0, 0);
    transition: transform .25s;
  }
  .screen-transition.inactive {
    position: absolute;
    transform: translate(-150%, 0);
    transition: transform .25s;
  }
  .home-page {
    background-image: url(./../assets/banner.jpg);
    background-size: cover;
  }
</style>
