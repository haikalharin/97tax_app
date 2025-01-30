<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <ValidationObserver ref="form">
          <form>
            <div class="sub-title">{{mainPerson()}}</div>
            <div class="form-group">
              <label>First Name
                <span style="color:grey;">(must match the name on your last tax return)</span>
              </label>
              <ValidationProvider
                name="The first name"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="
                    'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                  "
                  placeholder="Type here"
                  v-model="formInfo.first_name"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="">
              <label>Full Middle Name
                <span style="color:grey;">(required if you have a legal middle name)</span>
              </label>
              <div class="form-inline" style="margin-bottom: 12px;">
                <div class="form-group" style="width: 55%;">
                  <ValidationProvider
                    name="The middle name"
                    rules="allowed_str"
                    v-slot="{ errors }"
                  >
                    <input
                      type="text"
                      :class="
                        'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                      "
                      placeholder="Type here"
                      v-model="formInfo.middle_name"
                      @input="onInputChange"
                    />
                    <span class="ein-form-err">{{ errors[0] }}</span>
                  </ValidationProvider>
                </div>

                <div class="form-group checkbox-group" style="width: 40%;">
                  <label for="no_middle_name">
                    Check here<br/>
                    if none
                  </label>
                  <input
                    type="checkbox"
                    id="no_middle_name"
                    :class="
                      'form-control ' + (errors.no_middle_name ? ' is-invalid' : '')
                    "
                    v-model="formInfo.no_middle_name"
                    @change="onCheckboxChange"
                  />
                </div>
                <span class="ein-form-err" v-if="errors.no_middle_name">
                  Please confirm there is no middle name.
                </span>
              </div>
            </div>

            <div class="form-group">
              <label>Last Name
                <span style="color:grey;">(must match the name on your last tax return)</span>
              </label>
              <ValidationProvider
                name="The last name"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="
                    'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                  "
                  placeholder="Type here"
                  v-model="formInfo.last_name"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group" v-if="order.order_type == orderTypes.ESTATE">
              <label>Title</label>
              <ValidationProvider
                name="The title"
                rules="required"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="formInfo.title"
                >
                  <option value="" style="color: gray">Select</option>
                  <option
                    v-for="(t, idx) in titles"
                    :key="idx"
                    v-html="t"
                    :value="t"
                  ></option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Email</label>
              <ValidationProvider
                name="The email"
                rules="required|email"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="
                    'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                  "
                  placeholder="Type here"
                  v-model="formInfo.email"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Suffix</label>
              <select class="form-control" v-model="formInfo.suffix">
                <option value="" style="color: gray">Select</option>
                <option
                  v-for="(suf, idx) in suffixes"
                  :key="idx"
                  v-html="suf"
                  :value="suf"
                ></option>
              </select>
            </div>

            <div class="form-group" v-if="showSSN()">
              <label>SSN/ITIN</label>
              <ValidationProvider
                name="The SSN/ITIN"
                rules="required|ssn_tin"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="
                    'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                  "
                  placeholder="XXX-XX-XXXX"
                  v-model="formInfo.ssn"
                  v-mask="'###-##-####'"
                  @input="onSSNChange"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="form-group" v-if="showSec645()">
              <label>
                Trust filing as an Estate under Sec. 645?
              </label>
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_sec6451"
                  name="is_sec6451"
                  class="custom-control-input"
                  value="1"
                  v-model="formInfo.is_sec645"
                />
                <label class="custom-control-label" for="is_sec6451">
                  Yes
                </label>
              </div>
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_sec6452"
                  name="is_sec6452"
                  class="custom-control-input"
                  value="0"
                  v-model="formInfo.is_sec645"
                />
                <label class="custom-control-label" for="is_sec6452">
                  No
                </label>
              </div>
              <span class="ein-form-err" v-if="errors.is_sec645">
                Please choose an option.
              </span>
            </div>

            <div v-if="showSecondaryPerson()">
              <div class="sub-title">{{secondaryPerson()}}</div>
              <div class="form-group">
                <label>First Name
                  <span style="color:grey;">(must match the name on last tax return)</span>
                </label>
                <ValidationProvider
                  name="The first name"
                  rules="required|allowed_str"
                  v-slot="{ errors }"
                >
                  <input
                    type="text"
                    :class="
                      'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                    "
                    placeholder="Type here"
                    v-model="formInfo.secondary_first_name"
                  />
                  <span class="ein-form-err">{{ errors[0] }}</span>
                </ValidationProvider>
              </div>

              <div class="">
                <label>Full Middle Name
                  <span style="color:grey;">(required if there is a legal middle name)</span>
                </label>
                <div class="form-inline" style="margin-bottom: 12px;">
                  <div class="form-group" style="width: 55%;">
                    <ValidationProvider
                      name="The middle name"
                      rules="allowed_str"
                      v-slot="{ errors }"
                    >
                      <input
                        type="text"
                        :class="
                          'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                        "
                        placeholder="Type here"
                        v-model="formInfo.secondary_middle_name"
                        @input="onInputChange"
                      />
                      <span class="ein-form-err">{{ errors[0] }}</span>
                    </ValidationProvider>
                  </div>

                  <div class="form-group checkbox-group" style="width: 40%;">
                    <label for="secondary_no_middle_name">
                      Check here<br/>
                      if none
                    </label>
                    <input
                      type="checkbox"
                      id="secondary_no_middle_name"
                      :class="
                        'form-control ' + (errors.secondary_no_middle_name ? ' is-invalid' : '')
                      "
                      v-model="formInfo.secondary_no_middle_name"
                      @change="onCheckboxChange"
                    />
                  </div>
                  <span class="ein-form-err" v-if="errors.secondary_no_middle_name">
                    Please confirm there is no middle name.
                  </span>
                </div>
              </div>

              <div class="form-group">
                <label>Last Name
                  <span style="color:grey;">(must match the name on last tax return)</span>
                </label>
                <ValidationProvider
                  name="The last name"
                  rules="required|allowed_str"
                  v-slot="{ errors }"
                >
                  <input
                    type="text"
                    :class="
                      'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                    "
                    placeholder="Type here"
                    v-model="formInfo.secondary_last_name"
                  />
                  <span class="ein-form-err">{{ errors[0] }}</span>
                </ValidationProvider>
              </div>

              <div class="form-group">
                <label>Suffix</label>
                <select class="form-control" v-model="formInfo.secondary_suffix">
                  <option value="" style="color: gray">Select</option>
                  <option
                    v-for="(suf, idx) in suffixes"
                    :key="idx"
                    v-html="suf"
                    :value="suf"
                  ></option>
                </select>
              </div>

              <div class="form-group" v-if="showSSN()">
                <label>SSN/TIN</label>
                <ValidationProvider
                  name="The SSN/TIN"
                  rules="required|ssn_tin"
                  v-slot="{ errors }"
                >
                  <input
                    type="text"
                    :class="
                      'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                    "
                    placeholder="XXX-XX-XXXX"
                    v-model="formInfo.secondary_ssn"
                    v-mask="'###-##-####'"
                    @input="onSSNChange"
                  />
                  <span class="ein-form-err">{{ errors[0] }}</span>
                </ValidationProvider>
                <span class="ein-form-err" v-if="errors.ssn_similar">
                  Needs to be different from the Estate's SSN
                </span>
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
import { SiteUtils } from "../../scripts/site-common";
import { SnackUtils } from "../../scripts/snack-common";
import { ORDER_TYPES, TRUST_SUB_TYPES } from "../../scripts/constants";
import EinBottomNavigator from "./EinBottomNavigator.vue";

export default {
  props: ["order"],
  components: { EinBottomNavigator },
  data() {
    return {
      orderTypes: ORDER_TYPES,
      suffixes: "DDS, MD, PHD, JR, SR, I, II, III, IV, V, VI".split(", "),
      titles: "Administrator, Executor, Personal Representative".split(", "),

      formInfo: {
        first_name: this.order.first_name,
        middle_name: this.order.middle_name,
        no_middle_name: this.order.no_middle_name,
        last_name: this.order.last_name,
        title: this.order.title,
        email: this.order.email,
        suffix: this.order.suffix,
        ssn: this.order.ssn,
        is_sec645: this.order.is_sec645,

        secondary_first_name: this.order.secondary_first_name,
        secondary_middle_name: this.order.secondary_middle_name,
        secondary_no_middle_name: this.order.secondary_no_middle_name,
        secondary_last_name: this.order.secondary_last_name,
        secondary_suffix: this.order.secondary_suffix,
        secondary_ssn: this.order.secondary_ssn,
      },
      formSubmitted: false,
      errors: {
        is_sec645: false,
        no_middle_name: false,
        secondary_no_middle_name: false,
        ssn_similar: false,
      },
    };
  },
  methods: {
    mainPerson() {
      if (this.order.order_type == ORDER_TYPES.TRUST) {
        return "Responsible Party";
      }
      if (this.order.order_type == ORDER_TYPES.ESTATE) {
        return "Estate";
      }
      return "";
    },
    showSecondaryPerson() {
      return [ORDER_TYPES.TRUST, ORDER_TYPES.ESTATE].includes(this.order.order_type);
    },
    secondaryPerson() {
      if (this.order.order_type == ORDER_TYPES.TRUST) {
        return "Trustee";
      }
      if (this.order.order_type == ORDER_TYPES.ESTATE) {
        return "Deceased Person";
      }
      return "";
    },
    onSSNChange(event) {
      if (this.formSubmitted && this.showSSN()) {
        let ssnLength = "XXX-XX-XXXX".length;
        this.errors.ssn_similar = (!!this.formInfo.ssn && this.formInfo.ssn.length == ssnLength) &&
          (!!this.formInfo.secondary_ssn && this.formInfo.secondary_ssn.length == ssnLength) &&
          (this.formInfo.ssn == this.formInfo.secondary_ssn);
      }
    },
    onInputChange(event) {
      if (this.formSubmitted) {
        this.errors.no_middle_name = SiteUtils.isTrimEmptyString(this.formInfo.middle_name) && !this.formInfo.no_middle_name;
        this.errors.secondary_no_middle_name = this.showSecondaryPerson() &&
          SiteUtils.isTrimEmptyString(this.formInfo.secondary_middle_name) && !this.formInfo.secondary_no_middle_name;
      }
    },
    onCheckboxChange() {
      if (this.formSubmitted) {
        this.errors.no_middle_name = SiteUtils.isTrimEmptyString(this.formInfo.middle_name) && !this.formInfo.no_middle_name;
        this.errors.secondary_no_middle_name = this.showSecondaryPerson() &&
          SiteUtils.isTrimEmptyString(this.formInfo.secondary_middle_name) && !this.formInfo.secondary_no_middle_name;
      }
    },
    showSec645() {
      return this.order.order_type == ORDER_TYPES.TRUST &&
        ![TRUST_SUB_TYPES.BANKRUPTCY_ESTATE, TRUST_SUB_TYPES.CONSERVATORSHIP,
          TRUST_SUB_TYPES.CUSTODIANSHIP, TRUST_SUB_TYPES.GUARDIANSHIP].includes(this.order.sub_type);
    },
    showSSN() {
      return this.order.order_type == ORDER_TYPES.ESTATE;
    },
    onContinue() {
      this.formSubmitted = true;
      this.$refs.form.validate().then((success) => {
        if (!success) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }

        if (this.showSSN()) {
          this.errors.ssn_similar = this.formInfo.ssn == this.formInfo.secondary_ssn;
          if (this.errors.ssn_similar) {
            SnackUtils.warning("Deceased Person's SSN and Estate's SSN can't be similar.");
            return;
          }
        }

        this.errors.is_sec645 = SiteUtils.isNullOrUnd(this.formInfo.is_sec645);
        this.errors.no_middle_name = SiteUtils.isTrimEmptyString(this.formInfo.middle_name) && !this.formInfo.no_middle_name;
        this.errors.secondary_no_middle_name = this.showSecondaryPerson() &&
          SiteUtils.isTrimEmptyString(this.formInfo.secondary_middle_name) && !this.formInfo.secondary_no_middle_name;
        if (this.errors.is_sec645 || this.errors.no_middle_name || this.errors.secondary_no_middle_name) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }

        if (this.formInfo.suffix == "") this.formInfo.suffix = null;
        if (this.formInfo.secondary_suffix == "") this.formInfo.secondary_suffix = null;

        let middle_name_empty = SiteUtils.isTrimEmptyString(this.formInfo.middle_name);
        if (middle_name_empty) {
          this.formInfo.middle_name = "";
        } else {
          this.formInfo.no_middle_name = false;
        }
        let secondary_middle_name_empty = SiteUtils.isTrimEmptyString(this.formInfo.secondary_middle_name);
        if (secondary_middle_name_empty) {
          this.formInfo.secondary_middle_name = "";
        } else {
          this.formInfo.secondary_no_middle_name = false;
        }

        this.$emit("continue", this.formInfo);
      });
    },
  },
};
</script>
<style scoped>
.form-dims {
  max-width: 510px;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 40px;
  font-family: "Archivo", sans-serif;
}

.checkbox-group label {
  line-height: 19px;
  margin-top: 0px;
  margin-right: 15px;
}

.checkbox-group input {
  width: 18px;
  height: 18px;
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
