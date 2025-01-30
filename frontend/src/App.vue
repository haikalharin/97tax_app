<template>
  <div id="app">
    <router-view :hellomsg="msg"></router-view>
  </div>
</template>

<script>

import jquery from 'jquery';
export default {
  name: 'app',
  data () {
    return {
      
      msg: 'Welcome to your Vue.js powered Spring Boot App'
    }
  },
  mounted(){
    let $=jquery;
    $('input').attr('autocomplete','off');
  }
}
</script>

<style>
  .google-language-selection__panel {
    background-color: #0094be;
  }
  #google_translate_element {
    margin-top: 10px;
  }
  .goog-te-gadget .goog-te-gadget-simple {
    background: transparent !important;
    border: 0 !important;
  }
  .goog-te-gadget-simple .goog-te-gadget-icon {
    display: none;
  }
  .goog-te-gadget-simple .goog-te-menu-value {
    color: #fff !important;
  }
  .goog-te-gadget-simple .goog-te-menu-value:hover {
    text-decoration: none;
  }
  .goog-te-gadget-simple .goog-te-menu-value :nth-child(3) {
    display: none;
  }
  .goog-te-gadget-simple .goog-te-menu-value :nth-child(5) {
    color: #fff !important;
  }
    
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }
  .btn-green {
    color: #fff !important;
    background-color: #2bc929 !important;
    padding: 10px;
    border: 1px solid #fff !important;
    border-radius: 0 !important;
    box-shadow: 1px 1px 5px #444;
  }
  .btn-green:hover {
    background-color: #2bc929 !important;
  }

  .font-size-large{
  font-size: large;
  }

  .font-size-small{
  font-size: small;
  }

  @media (max-width: 575px) {
    .hidden-xs-only {
      display: none !important;
    }
    .center-xs-only {
      text-align: center !important;
    }
  }
  @media (min-width: 576px) {
    .shown-xs-only {
      display: none !important;
    }
  }
</style>
