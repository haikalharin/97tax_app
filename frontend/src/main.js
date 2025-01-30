// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from "vue";
import App from "./App";
import routes from "./router/index";
import VueCookie from "vue-cookie";
import BootstrapVue from "bootstrap-vue";
import ToggleButton from "vue-js-toggle-button";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import "./assets/style.css";
import upperFirst from "lodash/upperFirst";
import camelCase from "lodash/camelCase";
import { library } from "@fortawesome/fontawesome-svg-core";
import { ValidationProvider, ValidationObserver, extend } from 'vee-validate';
import * as rules from 'vee-validate/dist/rules';

Object.keys(rules).forEach(rule => {
  extend(rule, rules[rule]);
});


extend('allowed_str', {
  validate(value) {
    return /^[A-Za-z][A-Za-z.\-& ]*$/.test(value);
  },
  message: (fieldName) => {
    return `${fieldName} is invalid`;
  }
});

extend('letter_spaces_str', {
  validate(value) {
    return /^[A-Za-z][A-Za-z ]*$/.test(value);
  },
  message: (fieldName) => {
    return `${fieldName} is invalid`;
  }
});

extend('zip_code', {
  validate(value) {
    return /(^\d{5,9}$)|(^\d{5}-\d{4}$)/.test(value);
  },
  message: (fieldName) => {
    return `${fieldName} is invalid`;
  }
});

extend('phone_number', {
  validate(value) {
    return /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/.test(value);
  },
  message: (fieldName) => {
    return `${fieldName} is invalid`;
  }
});

extend('ein_number', {
  validate(value) {
    return /^\d{2}\-?\d{7}$/.test(value);
  },
  message: (fieldName) => {
    return `${fieldName} is invalid`;
  }
});

extend('trade_name', {
  validate(value) {
    let result = /^[A-Za-z][A-Za-z.\-& ]*$/.test(value);
    if (!result)
      return false;

    result = /(llc|lc|pllc|pa|corp|inc)$/.test(value.toLowerCase());
    return !result;
  },
  message: (fieldName) => {
    return `${fieldName} may not contain an ending such as 'LLC', 'LC', 'PLLC', 'PA', 'Corp', or 'Inc'.`;
  }
});

extend('ssn_tin', {
  validate(value) {
    return /^(?:\d{3}-\d{2}-\d{4}|\d{2}-\d{7})$/.test(value);
  },
  message: (fieldName) => {
    return `${fieldName} is invalid`;
  }
});

import {
  faGavel,
  faWindowClose,
  faUniversity,
  faCarCrash,
  faArrowRight,
  faChevronLeft,
  faChevronRight,
  faChevronDown,
  faChevronUp,
  faPlus,
  faCheck,
  faMinus,
  faCog,
  faPencilAlt,
  faCaretDown,
  faLock,
  faEnvelope,
  faPhone,
  faMapMarkerAlt,
  faTimes,
  faExclamationCircle,
  faCircle,
  faArrowUp,
  faCartShopping,
  faSpinner,
  faLayerGroup,
  faListCheck,
  faCheckCircle,
  faTimesCircle,
  faCircleQuestion
} from "@fortawesome/free-solid-svg-icons";
import {
  faMoneyBillAlt,
  faFile,
  faFileAlt,
  faQuestionCircle,
  faClock
} from "@fortawesome/free-regular-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

import * as VueGoogleMaps from "vue2-google-maps";
import VueMask from "v-mask";
import store from "./scripts/common-store";

import AirbnbStyleDatepicker from "vue-airbnb-style-datepicker";
import "vue-airbnb-style-datepicker/dist/vue-airbnb-style-datepicker.min.css";
import config from '../config'
import { plugins } from './shared/plugins';

const datepickerOptions = {};

const prod = process.env.NODE_ENV === 'production';
const shouldSW = 'serviceWorker' in navigator && prod;
if (shouldSW) {
  navigator.serviceWorker.register('/service-worker.js')
    .then(() => {
      console.log("Service Worker Registered!");
    })
    .catch(err => {
      console.log("Service Worker Not Registered!");
    });
}

library.add(
  faGavel,
  faWindowClose,
  faUniversity,
  faCarCrash,
  faCheck,
  faTimesCircle,
  faArrowRight,
  faChevronLeft,
  faChevronRight,
  faChevronUp,
  faChevronDown,
  faMoneyBillAlt,
  faFile,
  faFileAlt,
  faQuestionCircle,
  faPlus,
  faMinus,
  faCog,
  faPencilAlt,
  faCaretDown,
  faLock,
  faEnvelope,
  faPhone,
  faMapMarkerAlt,
  faTimes,
  faExclamationCircle,
  faCircle,
  faArrowUp,
  faCartShopping,
  faSpinner,
  faLayerGroup,
  faListCheck,
  faCheckCircle,
  faCircleQuestion,
  faClock
);
Vue.component("font-awesome-icon", FontAwesomeIcon);

Vue.config.productionTip = false;

Vue.use(BootstrapVue);
Vue.use(VueCookie);
Vue.use(AirbnbStyleDatepicker, datepickerOptions);
Vue.use(ToggleButton);
Vue.use(VueGoogleMaps, {
  load: {
    key: "AIzaSyBQ_42V9szfY0MYS-PWYUPzJJo3WOS0A3M",
    libraries: "places" // This is required if you use the Autocomplete plugin
    // OR: libraries: 'places,drawing'
    // OR: libraries: 'places,drawing,visualization'
    // (as you require)

    //// If you want to set the version, you can do so:
    // v: '3.26',
  }

  //// If you intend to programmatically custom event listener code
  //// (e.g. `this.$refs.gmap.$on('zoom_changed', someFunc)`)
  //// instead of going through Vue templates (e.g. `<GmapMap @zoom_changed="someFunc">`)
  //// you might need to turn this on.
  // autobindAllEvents: false,

  //// If you want to manually install components, e.g.
  //// import {GmapMarker} from 'vue2-google-maps/src/components/marker'
  //// Vue.component('GmapMarker', GmapMarker)
  //// then disable the following:
  // installComponents: true,
});

// As a plugin
Vue.use(VueMask);
Vue.use(plugins);

Vue.component('ValidationProvider', ValidationProvider);
Vue.component('ValidationObserver', ValidationObserver);

const requireComponent = require.context("./components", true, /\w+\.vue$/);

requireComponent.keys().forEach(fileName => {
  const componentConfig = requireComponent(fileName);

  const componentName = upperFirst(
    camelCase(fileName.replace(/^\.\/(.*)\.\w+$/, "$1"))
  );
  // console.log(componentName);

  Vue.component(componentName, componentConfig.default || componentConfig);
});


/* eslint-disable no-new */
new Vue({
  el: "#app",
  router: routes,
  store,
  template: "<App/>",
  components: { App }
});
