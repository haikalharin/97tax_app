<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <ValidationObserver ref="form">
          <form>
            <div class="sub-title">Entity Address</div>
            <div class="form-group">
              <label>{{ entityTypeName() }} Street Address</label>
              <vue-google-autocomplete
                id="address"
                :class="['form-control']"
                placeholder="Type here"
                v-show="showAddressTextBox"
                @placechanged="getAddressData"
                @blur="extractAddress"
                @inputChange="setAddress"
                country="us"
              >
              </vue-google-autocomplete>
              <ValidationProvider
                name="The address"
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
                  v-model="formInfo.address"
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
                  v-model="formInfo.apt_suite"
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
                  v-model="formInfo.city"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label
                >State/Territory where {{ entityTypeName() }} is located</label
              >
              <ValidationProvider
                name="The state/territory"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="formInfo.state"
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
            <div class="form-group">
              <label>County {{ entityTypeName() }} Location</label>
              <ValidationProvider
                name="The county"
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
                  v-model="formInfo.county"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Zip Code</label>
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
                  v-model="formInfo.zip_code"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Phone Number</label>
              <ValidationProvider
                name="The phone number"
                rules="required|phone_number"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="###-###-####"
                  v-model="formInfo.phone_number"
                  v-mask="'###-###-####'"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="sub-title">Entity Information</div>
            <div class="form-group">
              <label>
                Legal name of {{ entityTypeName() }} (must match articles of
                incorporation, if any)
              </label>
              <ValidationProvider
                name="The legal name"
                rules="required|trade_name"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="formInfo.legal_name"
                  placeholder="Type here"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label
                >Is the State/Territory where articles of incorporation are
                filed the same as the physical address?</label
              >
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_same_physical_address1"
                  name="is_same_physical_address"
                  class="custom-control-input"
                  value="1"
                  v-model="formInfo.is_same_physical_address"
                />
                <label
                  class="custom-control-label"
                  for="is_same_physical_address1"
                  >Yes</label
                >
              </div>
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_same_physical_address2"
                  name="is_same_physical_address"
                  class="custom-control-input"
                  value="0"
                  v-model="formInfo.is_same_physical_address"
                />
                <label
                  class="custom-control-label"
                  for="is_same_physical_address2"
                  >No</label
                >
              </div>
            </div>

            <div class="form-group" v-if="show_state_incorporate()">
              <label
                >State/Territory where articles of incorporation are (or will
                be) filed</label
              >
              <ValidationProvider
                name="The state/territory where articles of incorporation"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="formInfo.state_incorporated"
                >
                  <option value="" style="color: gray">Select</option>
                  <option
                    v-for="(suf, idx) in states"
                    :key="idx"
                    v-html="suf.name"
                    :value="suf.abbreviation"
                  ></option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="form-group">
              <label>
                Have you ever received an EIN before?
              </label>
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_previous_ein1"
                  class="custom-control-input"
                  :value="true"
                  v-model="formInfo.is_previous_ein"
                />
                <label
                  class="custom-control-label"
                  for="is_previous_ein1">
                  Yes
                </label>
              </div>
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_previous_ein2"
                  class="custom-control-input"
                  :value="false"
                  v-model="formInfo.is_previous_ein"
                />
                <label
                  class="custom-control-label"
                  for="is_previous_ein2">
                  No
                </label>
              </div>
            </div>

            <div class="form-group" v-if="formInfo.is_previous_ein">
              <label>
                Please enter your previous EIN
              </label>
              <ValidationProvider
                name="The EIN number"
                rules="required|ein_number"
                v-slot="{ errors }">
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="##-#######"
                  v-model="formInfo.previous_ein"
                  v-mask="'##-#######'"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="form-group">
              <label>Date {{ entityTypeName() }} started or acquired </label>
              <div class="object-row">
                <div class="width-half">
                  <ValidationProvider
                    name="The started month"
                    rules="required"
                    v-slot="{ errors }"
                  >
                    <select
                      :class="[
                        'form-control',
                        errors.length > 0 ? 'is-invalid' : '',
                      ]"
                      v-model="formInfo.start_date_month"
                    >
                      <option
                        v-for="(mo, idx) in months"
                        :key="idx"
                        v-html="mo"
                        :value="idx + 1"
                      ></option>
                    </select>
                    <span class="ein-form-err">{{ errors[0] }}</span>
                  </ValidationProvider>
                </div>
                <div class="width-half">
                  <ValidationProvider
                    name="The started year"
                    rules="required"
                    v-slot="{ errors }"
                  >
                    <select
                      :class="[
                        'form-control',
                        errors.length > 0 ? 'is-invalid' : '',
                      ]"
                      v-model="formInfo.start_date_year"
                    >
                      <option
                        v-for="(year, idx) in allowedYears"
                        :key="idx"
                        v-html="year == firstAllowedYear ? '1999 or earlier' : year"
                        :value="year"
                      ></option>
                    </select>
                    <span class="ein-form-err">{{ errors[0] }}</span>
                  </ValidationProvider>
                </div>
              </div>
              <span class="ein-form-err" v-if="showYearMonthErr">
                The year/month cannot be more than 1 year in the future or more
                than 25 years in the past.
              </span>
            </div>
            <div class="form-group">
              <label>Closing month of accounting year</label>
              <ValidationProvider
                name="The closing month"
                rules="required"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="formInfo.accounting_close_month"
                >
                  <option
                    v-for="(mo, idx) in months"
                    :key="idx"
                    v-html="mo"
                    :value="idx + 1"
                  ></option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div>
              <label
                >Do you have an address different from the above where you want
                your mail to be sent?</label
              >
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_diff_mailing_address1"
                  name="is_diff_mailing_address"
                  class="custom-control-input"
                  value="1"
                  v-model="formInfo.is_diff_mailing_address"
                  @change="onChangeDiffMailingAddress"
                />
                <label
                  class="custom-control-label"
                  for="is_diff_mailing_address1"
                  >Yes</label
                >
              </div>
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_diff_mailing_address2"
                  name="is_diff_mailing_address"
                  class="custom-control-input"
                  value="0"
                  v-model="formInfo.is_diff_mailing_address"
                  @change="onChangeDiffMailingAddress"
                />
                <label
                  class="custom-control-label"
                  for="is_diff_mailing_address2"
                  >No</label
                >
              </div>
            </div>
          </form>
        </ValidationObserver>
      </div>
    </div>
    <ein-bottom-navigator @back="$emit('back')" @continue="onContinue" />
  </div>
</template>
<script>
import { MONTHS, US_STATES } from "../../scripts/constants";
import VueGoogleAutocomplete from "vue-google-autocomplete";

import EinBottomNavigator from "./EinBottomNavigator.vue";
import { SnackUtils } from "../../scripts/snack-common";
import moment from "moment";
import { SiteUtils } from "../../scripts/site-common";

export default {
  props: ["order"],
  components: { VueGoogleAutocomplete, EinBottomNavigator },
  computed: {
  },
  created() {
    this.months = MONTHS;
    this.states = US_STATES;
    let currentYear = moment().year();
    this.firstAllowedYear = currentYear - 25;
    this.allowedYears = Array.from(Array(27).keys()).map(index => currentYear + index - 25);
  },
  data() {
    return {
      showAddressTextBox: SiteUtils.isEmptyString(this.order.address),
      showYearMonthErr: false,
      formInfo: {
        is_diff_mailing_address: SiteUtils.isNullOrUnd(
          this.order.is_diff_mailing_address
        )
          ? 0
          : this.order.is_diff_mailing_address,
        is_same_physical_address: SiteUtils.isNullOrUnd(
          this.order.is_same_physical_address
        )
          ? 1
          : this.order.is_same_physical_address,
        address: this.order.address,
        apt_suite: this.order.apt_suite,
        city: this.order.city,
        state: this.order.state,
        county: this.order.county,
        zip_code: this.order.zip_code,
        phone_number: this.order.phone_number,
        legal_name: this.order.legal_name,
        start_date_month: !!this.order.start_date_month ? this.order.start_date_month : moment().month() + 1, // define initial value
        start_date_year: !!this.order.start_date_year ? this.order.start_date_year : moment().year(), // define initial value
        accounting_close_month: !!this.order.accounting_close_month ? this.order.accounting_close_month : "12", // define initial value
        state_incorporated: this.order.state_incorporated,
        is_previous_ein: this.order.is_previous_ein,
        previous_ein: this.order.previous_ein,
      },
    };
  },
  mounted() {
    this.showYearMonthErr = false;
  },
  methods: {
    entityTypeName() {
      if (this.order.order_type == "SoleProprietor") return "Sole Proprietor";
      else if (this.order.order_type == "Partnership") {
        if (this.order.sub_type == "Partnership") {
          return "Partnership";
        } else {
          return "Joint Venture";
        }
      } else if (this.order.order_type == "Corporation") {
        if (this.order.sub_type == "Settlement Fund") return "Settlement Funds";
        else return this.order.sub_type;
      } else { // Trust and Estate
        return this.order.order_type;
      }
    },
    show_state_incorporate() {
      console.log(this.formInfo.is_same_physical_address);
      if (SiteUtils.isNullOrUnd(this.formInfo.is_same_physical_address)) {
        return false;
      }

      if (parseInt(this.formInfo.is_same_physical_address) == 1) return false;

      return true;
    },
    getStateSafely(val) {
      if (!val) {
        return "";
      }
      let theState = this.states.find(
        next => next.name === val || next.abbreviation === val.toUpperCase());
      return !!theState ? theState.abbreviation : "";
    },
    getAddressData: function (addressData, placeResultData, id) {
      this.formInfo.address = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;
      this.formInfo.apt_suite = addressData.subpremise || "";
      this.formInfo.city = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;
      this.formInfo.state = addressData.administrative_area_level_1
        ? this.getStateSafely(addressData.administrative_area_level_1)
        : "";
      this.formInfo.county = addressData.administrative_area_level_2
        ? addressData.administrative_area_level_2
        : "";
      this.formInfo.zip_code = addressData.postal_code
        ? addressData.postal_code
        : "";

      this.showAddressTextBox = false;
    },
    extractAddress: function () {
      google.maps.places.Autocomplete(this.getAddressData, "place_changed");
      //this.showAddressTextBox=false;
    },
    setAddress: function (address, type) {
      this.formInfo.address = address.newVal
        ? address.newVal.split(",")[0]
        : "";
    },
    changeToAddressGoogle: function () {
      this.showAddressTextBox = true;
    },

    onChangeDiffMailingAddress() {
      this.$emit(
        "onChangeDiffMailingAddress",
        this.formInfo.is_diff_mailing_address
      );
    },

    onContinue() {
      this.showAddressTextBox = false;
      this.$refs.form.validate().then((success) => {
        if (!success) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }

        // start year/month validation: Year cannot be more than 1 year in the future or more than 25 years in the past.
        let curDate = new Date();
        let start_year = parseInt(this.formInfo.start_date_year);
        let start_month = parseInt(this.formInfo.start_date_month);
        let startDate = moment([start_year, start_month, 1]).toDate();

        console.log(curDate, startDate);

        let diff = SiteUtils.monthDiff(curDate, startDate);
        console.log(diff);

        if (diff > 12 || diff < -25 * 12) {
          this.showYearMonthErr = true;
          SnackUtils.warning(
            "The started year/month cannot be more than 1 year in the future or more than 25 years in the past."
          );
          return;
        }

        if (parseInt(this.formInfo.is_same_physical_address) == 1) {
          this.formInfo.state_incorporated = this.formInfo.state;
        }

        if (!this.formInfo.is_previous_ein) {
          this.formInfo.previous_ein = "";
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
.object-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.width-half {
  width: 48%;
}

.sub-title {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 21px;

  color: #000000;
}
</style>
