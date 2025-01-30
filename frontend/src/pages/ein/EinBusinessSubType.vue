<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <ValidationObserver ref="form">
          <form>
            <div class="form-group" v-if="!!question1">
              <label>
                {{question1}}
              </label>
              <div class="custom-control custom-radio"
                v-for="(subType, idx) in subTypes1" :key="idx">
                <input
                  type="radio"
                  :id="idx"
                  class="custom-control-input"
                  :value="subType.value"
                  v-model="formInfo.business_sub_type"
                  @change="onSubTypeUpdate"
                />
                <label
                  class="custom-control-label"
                  :for="idx">
                  {{subType.label}}
                </label>
              </div>
            </div>

            <div class="form-group" v-if="!!question2">
              <label>
                {{question2}}
              </label>
              <div class="custom-control custom-radio"
                v-for="(subType, idx) in subTypes2" :key="100+idx">
                <input
                  type="radio"
                  :id="100+idx"
                  class="custom-control-input"
                  :value="subType.value"
                  v-model="formInfo.business_sub_type_2"
                  @change="onSubType2Update"
                />
                <label
                  class="custom-control-label"
                  :for="100+idx">
                  {{subType.label}}
                </label>
              </div>
            </div>

            <div class="form-group" v-if="!!question3">
              <label>
                {{question3}}
              </label>
              <div class="custom-control custom-radio"
                v-for="(subType, idx) in subTypes3" :key="200+idx">
                <input
                  type="radio"
                  :id="200+idx"
                  class="custom-control-input"
                  :value="subType.value"
                  v-model="formInfo.business_sub_type_3"
                  @change="onSubType3Update"
                />
                <label
                  class="custom-control-label"
                  :for="200+idx">
                  {{subType.label}}
                </label>
              </div>
            </div>

            <div class="form-group" v-if="!!question4">
              <label>
                {{question4}}
              </label>
              <div class="custom-control custom-radio"
                v-for="(subType, idx) in subTypes4" :key="300+idx">
                <input
                  type="radio"
                  :id="300+idx"
                  class="custom-control-input"
                  :value="subType.value"
                  v-model="formInfo.business_sub_type_4"
                  @change="onSubType4Update"
                />
                <label
                  class="custom-control-label"
                  :for="300+idx">
                  {{subType.label}}
                </label>
              </div>
            </div>

            <div class="form-group" v-if="!!theInputText">
              <label>
                {{theInputText}}
              </label>

              <ValidationProvider
                name="This field"
                rules="required|letter_spaces_str"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :maxlength="50"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.business_details"
                />
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

import EinBottomNavigator from "./EinBottomNavigator.vue";
import { SnackUtils } from "../../scripts/snack-common";
import { SiteUtils } from "../../scripts/site-common";
import { BUSINESS_SUB_TYPES_MAP } from "../../scripts/constants";

export default {
  props: ["order"],
  components: { EinBottomNavigator },
  computed: {
  },
  created() {
  },
  data() {
    return {
      typeData: {},
      question1: "",
      subTypes1: [],
      question2: "",
      subTypes2: [],
      question3: "",
      subTypes3: [],
      question4: "",
      subTypes4: [],
      theInputText: "",
      formInfo: {
        business_type: this.order.business_type,
        business_sub_type: this.order.business_sub_type,
        business_sub_type_2: this.order.business_sub_type_2,
        business_sub_type_3: this.order.business_sub_type_3,
        business_sub_type_4: this.order.business_sub_type_4,
        business_details: this.order.business_details,
      },
    };
  },
  mounted() {
    this.typeData = BUSINESS_SUB_TYPES_MAP[this.order.business_type];
    this.question1 = this.typeData.question;
    this.subTypes1 = this.typeData.options;
    this.theInputText = this.typeData.detailsText;
    if (!this.question1) {
      return;
    }

    this.formInfo.business_sub_type = !!this.order.business_sub_type ? this.order.business_sub_type : this.subTypes1[0].value;
    let subType1 = this.subTypes1.find((next) => next.value == this.formInfo.business_sub_type);
    this.question2 = subType1.question;
    this.subTypes2 = subType1.options;
    this.theInputText = subType1.detailsText;
    if (!this.question2) {
      return;
    }

    this.formInfo.business_sub_type_2 = !!this.order.business_sub_type_2 ? this.order.business_sub_type_2 : this.subTypes2[0].value;
    let subType2 = this.subTypes2.find((next) => next.value == this.formInfo.business_sub_type_2);
    this.question3 = subType2.question;
    this.subTypes3 = subType2.options;
    this.theInputText = subType2.detailsText;
    if (!this.question3) {
      return;
    }

    this.formInfo.business_sub_type_3 = !!this.order.business_sub_type_3 ? this.order.business_sub_type_3 : this.subTypes3[0].value;
    let subType3 = this.subTypes3.find((next) => next.value == this.formInfo.business_sub_type_3);
    this.question4 = subType3.question;
    this.subTypes4 = subType3.options;
    this.theInputText = subType3.detailsText;
    if (!this.question4) {
      return;
    }

    this.formInfo.business_sub_type_4 = !!this.order.business_sub_type_4 ? this.order.business_sub_type_4 : this.subTypes4[0].value;
    let subType4 = this.subTypes4.find((next) => next.value == this.formInfo.business_sub_type_4);
    this.theInputText = subType4.detailsText;
  },
  methods: {
    onSubTypeUpdate() {
      this.formInfo.business_sub_type_2 = "";
      this.formInfo.business_sub_type_3 = "";
      this.formInfo.business_sub_type_4 = "";
      this.question2 = "";
      this.question3 = "";
      this.question4 = "";
      this.formInfo.business_details = "";

      let subType1 = this.subTypes1.find((next) => next.value == this.formInfo.business_sub_type);
      this.question2 = subType1.question;
      this.subTypes2 = subType1.options;
      this.theInputText = subType1.detailsText;

      if (!!this.subTypes2) {
        this.formInfo.business_sub_type_2 = this.subTypes2[0].value;
        this.onSubType2Update();
      }
    },
    onSubType2Update() {
      this.formInfo.business_sub_type_3 = "";
      this.formInfo.business_sub_type_4 = "";
      this.question3 = "";
      this.question4 = "";
      this.formInfo.business_details = "";

      let subType2 = this.subTypes2.find((next) => next.value == this.formInfo.business_sub_type_2);
      this.question3 = subType2.question;
      this.subTypes3 = subType2.options;
      this.theInputText = subType2.detailsText;

      if (!!this.subTypes3) {
        this.formInfo.business_sub_type_3 = this.subTypes3[0].value;
        this.onSubType3Update();
      }
    },
    onSubType3Update() {
      this.formInfo.business_sub_type_4 = "";
      this.question4 = "";
      this.formInfo.business_details = "";

      let subType3 = this.subTypes3.find((next) => next.value == this.formInfo.business_sub_type_3);
      this.question4 = subType3.question;
      this.subTypes4 = subType3.options;
      this.theInputText = subType3.detailsText;

      if (!!this.subTypes4) {
        this.formInfo.business_sub_type_4 = this.subTypes4[0].value;
        this.onSubType4Update();
      }
    },
    onSubType4Update() {
      this.formInfo.business_details = "";

      let subType4 = this.subTypes4.find((next) => next.value == this.formInfo.business_sub_type_4);
      this.theInputText = subType4.detailsText;
    },

    onContinue() {
      this.$refs.form.validate().then((success) => {
        if (!success) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }

        if (!this.theInputText) {
          this.formInfo.business_details = "";
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
