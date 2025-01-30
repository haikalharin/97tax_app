<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <form>
          <div
            class="pt-3"
            v-if="
              !(order.order_type == orderTypes.ESTATE || order.order_type == orderTypes.TRUST ||
              (order.order_type == orderTypes.CORPORATION && ['RIC', 'REIT', 'Settlement Fund'].includes(order.sub_type)))
            "
          >
            <label
              >Does your business own a highway motor vehicle with a taxable
              gross weight of 55,000 pounds or more?</label
            >
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_large_motor_vehicle1"
                name="is_large_motor_vehicle"
                class="custom-control-input"
                value="1"
                v-model="formInfo.is_large_motor_vehicle"
              />
              <label class="custom-control-label" for="is_large_motor_vehicle1"
                >Yes</label
              >
            </div>
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_large_motor_vehicle2"
                name="is_large_motor_vehicle"
                class="custom-control-input"
                value="0"
                v-model="formInfo.is_large_motor_vehicle"
              />
              <label class="custom-control-label" for="is_large_motor_vehicle2"
                >No</label
              >
            </div>
            <span class="ein-form-err" v-if="errors.is_large_motor_vehicle"
              >Please choose an option.</span
            >
          </div>
          <div
            class="pt-3"
            v-if="
              !(order.order_type == orderTypes.ESTATE || order.order_type == orderTypes.TRUST ||
              (order.order_type == orderTypes.CORPORATION && ['RIC', 'REIT', 'Settlement Fund'].includes(order.sub_type)))
            "
          >
            <label>Does your business involve gambling/wagering?</label>
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_gambling1"
                name="is_gambling"
                class="custom-control-input"
                value="1"
                v-model="formInfo.is_gambling"
              />
              <label class="custom-control-label" for="is_gambling1">Yes</label>
            </div>
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_gambling2"
                name="is_gambling"
                class="custom-control-input"
                value="0"
                v-model="formInfo.is_gambling"
              />
              <label class="custom-control-label" for="is_gambling2">No</label>
            </div>
            <span class="ein-form-err" v-if="errors.is_gambling"
              >Please choose an option.</span
            >
          </div>
          <div
            class="pt-3"
            v-if="
              !(order.order_type == orderTypes.ESTATE || order.order_type == orderTypes.TRUST ||
              (order.order_type == orderTypes.CORPORATION && ['RIC', 'REIT', 'Settlement Fund'].includes(order.sub_type)))
            "
          >
            <label
              >Does your business need to file Form 720(Quarterly Federal Excise
              Tax Return)?</label
            >
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_excise_tax_form7201"
                name="is_excise_tax_form720"
                class="custom-control-input"
                value="1"
                v-model="formInfo.is_excise_tax_form720"
              />
              <label class="custom-control-label" for="is_excise_tax_form7201"
                >Yes</label
              >
            </div>
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_excise_tax_form7202"
                name="is_excise_tax_form720"
                class="custom-control-input"
                value="0"
                v-model="formInfo.is_excise_tax_form720"
              />
              <label class="custom-control-label" for="is_excise_tax_form7202"
                >No</label
              >
            </div>
            <span class="ein-form-err" v-if="errors.is_excise_tax_form720"
              >Please choose an option.</span
            >
          </div>
          <div
            class="pt-3"
            v-if="
              !(order.order_type == orderTypes.ESTATE || order.order_type == orderTypes.TRUST ||
              (order.order_type == orderTypes.CORPORATION && ['RIC', 'REIT', 'Settlement Fund'].includes(order.sub_type)))
            "
          >
            <label
              >Does your business sell or manufacture alcohol, tobacco, or
              firearms?</label
            >
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_atf1"
                name="is_atf"
                class="custom-control-input"
                value="1"
                v-model="formInfo.is_atf"
              />
              <label class="custom-control-label" for="is_atf1">Yes</label>
            </div>
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_atf2"
                name="is_atf"
                class="custom-control-input"
                value="0"
                v-model="formInfo.is_atf"
              />
              <label class="custom-control-label" for="is_atf2">No</label>
            </div>
            <span class="ein-form-err" v-if="errors.is_atf"
              >Please choose an option.</span
            >
          </div>
          <div class="pt-3">
            <label
              >Do you have, or do you expect to have, any employees who will
              receive Forms W-2 in the next 12 months?</label
            >
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_w2_employees1"
                name="is_w2_employees"
                class="custom-control-input"
                value="1"
                v-model="formInfo.is_w2_employees"
              />
              <label class="custom-control-label" for="is_w2_employees1"
                >Yes</label
              >
            </div>
            <div class="custom-control custom-radio">
              <input
                type="radio"
                id="is_w2_employees2"
                name="is_w2_employees"
                class="custom-control-input"
                value="0"
                v-model="formInfo.is_w2_employees"
              />
              <label class="custom-control-label" for="is_w2_employees2"
                >No</label
              >
            </div>
            <span class="ein-form-err" v-if="errors.is_w2_employees"
              >Please choose an option.</span
            >
          </div>
        </form>
      </div>
    </div>
    <ein-bottom-navigator @back="$emit('back')" @continue="onContinue" />
  </div>
</template>
<script>
import { SiteUtils } from "../../scripts/site-common";
import { SnackUtils } from "../../scripts/snack-common";
import { ORDER_TYPES } from "../../scripts/constants";
import EinBottomNavigator from "./EinBottomNavigator.vue";

export default {
  props: ["order"],
  components: { EinBottomNavigator },
  data() {
    return {
      orderTypes: ORDER_TYPES,
      formInfo: {
        is_large_motor_vehicle: this.order.is_large_motor_vehicle,
        is_gambling: this.order.is_gambling,
        is_excise_tax_form720: this.order.is_excise_tax_form720,
        is_atf: this.order.is_atf,
        is_w2_employees: this.order.is_w2_employees,
      },
      errors: {
        is_large_motor_vehicle: false,
        is_gambling: false,
        is_excise_tax_form720: false,
        is_atf: false,
        is_w2_employees: false,
      },
    };
  },
  methods: {
    onContinue() {
      this.errors.is_large_motor_vehicle = SiteUtils.isNullOrUnd(
        this.formInfo.is_large_motor_vehicle
      );
      this.errors.is_gambling = SiteUtils.isNullOrUnd(
        this.formInfo.is_gambling
      );
      this.errors.is_excise_tax_form720 = SiteUtils.isNullOrUnd(
        this.formInfo.is_excise_tax_form720
      );
      this.errors.is_atf = SiteUtils.isNullOrUnd(this.formInfo.is_atf);
      this.errors.is_w2_employees = SiteUtils.isNullOrUnd(
        this.formInfo.is_w2_employees
      );

      if (
        this.errors.is_large_motor_vehicle ||
        this.errors.is_gambling ||
        this.errors.is_excise_tax_form720 ||
        this.errors.is_atf ||
        this.errors.is_w2_employees
      ) {
        SnackUtils.warning("Please fill invalid fields.");
        return;
      }

      this.$emit("continue", this.formInfo);
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
</style>
