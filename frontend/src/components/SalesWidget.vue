<template>
  <div>
    <div class="sales-widget show-on-mobile" v-if="hide">
      <span class="icon">
        <font-awesome-icon icon="phone" />
      </span>
      <span class="close" v-on:click="onHide">

      </span>
      <a :href="`tel:+1${phone_number}`">
        <h3>Questions?</h3>
        <p>Call us now to Get answers!</p>

        <span class="phone-number">{{ phone_number }}</span>
        <button class="click-to-call">Click to Call Now</button>
      </a>
    </div>
    <div class="sales-widget show-on-desktop" v-if="hide">
      <span class="icon">
        <font-awesome-icon icon="phone" />
      </span>
      <span class="close" v-on:click="onHide">
      </span>
      <h3>Questions?</h3>
      <p>Call us now to Get answers!</p>

      <span class="phone-number">{{ phone_number }}</span>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    phone_number: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      hide: true,
      displayPhoneNumber: ''
    }
  },
  mounted() {
    this.getDisplayPhoneNumber();
  },
  methods: {
    onHide() {
      this.hide = false;
    },
    getDisplayPhoneNumber() {
      let x = this.phoneNumber.replace(/\D/g, '').match(/(\d{0,3})(\d{0,3})(\d{0,4})/);
      this.displayPhoneNumber = !x[2] ? x[1] : x[1] + '-' + x[2] + (x[3] ? '-' + x[3] : '');
    }
  },
  watch: {
    phoneNumber: function () {
      this.getDisplayPhoneNumber();
    }
  }
}
</script>

<style scoped>
.sales-widget {
  width: 150px;
  height: 150px;
  background-image: linear-gradient(#069711, #4bba0d);
  position: absolute;
  right: 90px;
  top: 290px;
  padding: 0px 20px 20px 15px;
  border-top-right-radius: 40%;
}

.sales-widget h3 {
  color: white;
  font-size: 16px;
  font-weight: bold;
  margin-top: 5px;
}

.sales-widget p {
  font-size: 12px;
  margin: 2px 0px;
  font-weight: 600;
  line-height: 100%;
  color: #FFF;
}

.sales-widget .phone-number {
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 0.5px;
  color: #FFF;
}

.sales-widget .icon {
  margin-top: -20px;
  margin-left: -15px;
  width: 50px;
  height: 50px;
  background: #04dd15;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 24px;
  transform: rotate(90deg);
}

.sales-widget .close {
  margin-top: -50px;
  margin-left: 91px;
  top: 16px;
  left: 16px;
  cursor: pointer;
  color: black !important;
  ;
}

.sales-widget .close {
  border-radius: 50%;
  padding: 0.5em;
  width: 30px;
  height: 30px;
  border: 2px solid black;
  background-color: black;
  color: white;
  position: relative;
  opacity: 1 !important;
}
.sales-widget .close:hover {
  border: 2px solid black;
  background-color: black;
  color: #d0d0d0;
}

.sales-widget .close::before {
  content: " ";
  position: absolute;
  display: block;
  background-color: white;
  width: 2px;
  left: 12px;
  top: 5px;
  bottom: 5px;
  transform: rotate(45deg);
}
.sales-widget .close::after {
  content: " ";
  position: absolute;
  display: block;
  background-color: white;
  height: 2px;
  top: 12px;
  left: 5px;
  right: 5px;
  transform: rotate(45deg);
}

@media (min-width: 681px) {
  .sales-widget {
    right: 20px;
  }

  .show-on-mobile {
    display: none !important;
  }
}

@media (max-width: 680px) {
   .sales-widget {
    right: 10px;
  }
  .show-on-desktop {
    display: none !important;
  }
}

.sales-widget a {
  text-decoration: none !important;
}

.sales-widget .click-to-call {
  color: #04dd15;
  background-color: white;
  border: 1px white;
  width: 100%;
  font-size: 10px;
  border-radius: 2px;
  margin-top: 5px;
}
</style>
