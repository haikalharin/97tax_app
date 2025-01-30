<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <ValidationObserver ref="form">
          <form>
            <div class="form-group">
              <label>
                Check the box that is closest to your type of intended business:
              </label>
              <div class="custom-control custom-radio"
                v-for="(type, idx) in businessTypes" :key="idx">
                <input
                  type="radio"
                  :id="idx"
                  class="custom-control-input"
                  :value="type.value"
                  v-model="formInfo.business_type"
                  @change="onTypeUpdate"
                />
                <label
                  class="custom-control-label"
                  :for="idx">
                  {{type.label}}
                </label>
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

import EinBottomNavigator from "./EinBottomNavigator.vue";
import { SnackUtils } from "../../scripts/snack-common";
import { SiteUtils } from "../../scripts/site-common";
import { BUSINESS_TYPES } from "../../scripts/constants";

export default {
  props: ["order"],
  components: { EinBottomNavigator },
  computed: {
  },
  created() {
  },
  data() {
    return {
      businessTypes: BUSINESS_TYPES,
      formInfo: {
        business_type: !!this.order.business_type ? this.order.business_type : "ACCOMMODATIONS", // define initial value
        business_sub_type: this.order.business_sub_type,
        business_sub_type_2: this.order.business_sub_type_2,
        business_sub_type_3: this.order.business_sub_type_3,
        business_sub_type_4: this.order.business_sub_type_4,
        business_details: this.order.business_details,
      },
    };
  },
  mounted() {
  },
  methods: {
    onTypeUpdate() {
      this.formInfo.business_sub_type = "";
      this.formInfo.business_sub_type_2 = "";
      this.formInfo.business_sub_type_3 = "";
      this.formInfo.business_sub_type_4 = "";
      this.formInfo.business_details = "";
    },

    onContinue() {
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
