<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <ValidationObserver ref="form">
          <form>
            <div class="form-group">
              <label>Street</label>
              <vue-google-autocomplete
                id="address"
                :class="['form-control']"
                placeholder="Type here"
                v-show="showAddressTextBox"
                @placechanged="getAddressData"
                @blur="extractAddress"
                @inputChange="setAddress"
                :country="formInfo.mailing_country"
              >
              </vue-google-autocomplete>
              <ValidationProvider
                name="The street"
                rules="required"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.mailing_address"
                  v-if="!showAddressTextBox"
                  @focusin="changeToAddressGoogle"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Apt/Suite</label>
              <ValidationProvider
                name="The apt suite"
                rules=""
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.mailing_apt_suite"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>City</label>
              <ValidationProvider
                name="The city"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.mailing_city"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>State/Province/Territory</label>
              <ValidationProvider
                name="The state/province/territory"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="formInfo.mailing_state"
                >
                  <option value="" style="color: gray">Select</option>
                  <option
                    v-for="(st, idx) in states"
                    :key="idx"
                    v-html="st.name"
                    :value="st.abbreviation"
                  ></option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="ein-warning-msg mb-3">
              <img src="@/assets/warning.png" class="ein-warning-mark" />
              For U.S. addresses, enter the state/territory abbreviation or full
              name. For foreign addresses, enter the full name of the
              province/territory.
            </div>
            <div class="form-group">
              <label>Zip/Postal Code</label>
              <ValidationProvider
                name="The zip code"
                rules="required|zip_code"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.mailing_zip_code"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Country</label>
              <ValidationProvider
                name="The country"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="formInfo.mailing_country"
                >
                  <option value="" style="color: gray">Select</option>
                  <option
                    v-for="(c, idx) in countries"
                    :key="idx"
                    v-html="c.name"
                    :value="c.code"
                  ></option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
          </form>
        </ValidationObserver>
      </div>
    </div>
    <ein-bottom-navigator @back="$emit('back')" @continue="onContinue" />
  </div>
</template>
<script>
import VueGoogleAutocomplete from "vue-google-autocomplete";
import { COUNTRIES, US_STATES } from "../../scripts/constants";
import { SiteUtils } from "../../scripts/site-common";
import { SnackUtils } from "../../scripts/snack-common";
import EinBottomNavigator from "./EinBottomNavigator.vue";

export default {
  props: ["order"],
  components: { EinBottomNavigator, VueGoogleAutocomplete },
  created() {
    this.states = US_STATES;
  },
  data() {
    return {
      showAddressTextBox: SiteUtils.isEmptyString(this.order.mailing_address),
      countries: COUNTRIES,
      formInfo: {
        mailing_address: this.order.mailing_address,
        mailing_apt_suite: this.order.mailing_apt_suite,
        mailing_city: this.order.mailing_city,
        mailing_state: this.order.mailing_state,
        mailing_zip_code: this.order.mailing_zip_code,
        mailing_country: SiteUtils.isEmptyString(this.order.mailing_country)
          ? "US"
          : this.order.mailing_country,
      },
    };
  },
  methods: {
    getStateSafely(val) {
      if (!val) {
        return "";
      }
      let theState = this.states.find(
        next => next.name === val || next.abbreviation === val.toUpperCase());
      return !!theState ? theState.abbreviation : "";
    },
    getAddressData: function (addressData, placeResultData, id) {
      console.log(addressData);
      this.formInfo.mailing_address = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;
      this.formInfo.mailing_apt_suite = addressData.subpremise || "";
      this.formInfo.mailing_city = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;

      if (this.formInfo.mailing_country == "US")
        this.formInfo.mailing_state = addressData.administrative_area_level_1
          ? this.getStateSafely(addressData.administrative_area_level_1)
          : "";
      else
        this.formInfo.mailing_state = addressData.administrative_area_level_2
          ? addressData.administrative_area_level_2
          : "";

      this.formInfo.mailing_zip_code = addressData.postal_code
        ? addressData.postal_code
        : "";

      this.showAddressTextBox = false;
    },
    extractAddress: function () {
      google.maps.places.Autocomplete(this.getAddressData, "place_changed");
      //this.showAddressTextBox=false;
    },
    setAddress: function (address, type) {
      this.formInfo.mailing_address = address.newVal
        ? address.newVal.split(",")[0]
        : "";
    },
    changeToAddressGoogle: function () {
      this.showAddressTextBox = true;
    },

    onContinue() {
      this.showAddressTextBox = false;
      this.$refs.form.validate().then((success) => {
        if (!success) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }
        this.$emit("continue", this.formInfo);
      });
    },
  },
};
</script>
<style scoped>
.form-dims {
  max-width: 440px;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 40px;
  font-family: "Archivo", sans-serif;
}
</style>
