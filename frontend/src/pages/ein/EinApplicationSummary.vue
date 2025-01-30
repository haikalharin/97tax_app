<template>
  <div>
    <div class="container">
      <div class="row">
        <div class="col-md-12 offset-lg-2 col-lg-8">
          <div class="block-title">Confirm of all your information below is correct to continue</div>
          <ValidationObserver ref="form">
            <form class="ein-form mt-4 mb-4">
              <div class="ein-summary-item mb20px">
                <div>SSN (or TIN):</div>
                <div v-if="ORDER_TYPES.ESTATE == order.order_type">
                  {{ order.ssn }}&nbsp;&nbsp;<img
                    class="cursor-pointer"
                    src="@/assets/pencil.png"
                    @click="onClickForm6"
                  />
                </div>

                <div v-if="ORDER_TYPES.ESTATE != order.order_type">
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
                      style="max-width: 155px;"
                      placeholder="XXX-XX-XXXX"
                      v-model="formInfo.ssn"
                      v-mask="'###-##-####'"
                    />
                    <span class="ein-form-err">{{ errors[0] }}</span>
                  </ValidationProvider>
                </div>
              </div>
              <div class="ein-summary-item mb20px">
                <div>Legal Name of Entity:</div>
                <div>
                  {{ order.legal_name }}&nbsp;&nbsp;<img
                    class="cursor-pointer"
                    src="@/assets/pencil.png"
                    @click="onClickBusinessPhysical"
                  />
                </div>
              </div>
              <div class="ein-summary-item mb20px">
                <div>Name of Responsible Party:</div>
                <div>
                  {{ order.first_name }}&nbsp;{{
                    order.last_name
                  }}
                  &nbsp;&nbsp;<img
                    class="cursor-pointer"
                    src="@/assets/pencil.png"
                    @click="onClickForm6"
                  />
                </div>
              </div>
              <div class="ein-summary-item mb20px">
                <div>Type of Entity:</div>
                <div>
                  {{ order.order_type }}&nbsp;&nbsp;<img
                    class="cursor-pointer"
                    src="@/assets/pencil.png"
                    @click="onClickEntityType"
                  />
                </div>
              </div>
              <div class="ein-summary-item mb20px"
                v-if="
                  !(ORDER_TYPES.ESTATE == order.order_type ||
                  (ORDER_TYPES.CORPORATION == order.order_type && SUB_TYPES.SETTLEMENT_FUND == order.sub_type) ||
                  (ORDER_TYPES.TRUST == order.order_type && TRUST_SUB_TYPES.ESCROW != order.sub_type))
                ">
                <div>Reason for Applying:</div>
                <div>
                  {{ order.reason }}&nbsp;&nbsp;<img
                    class="cursor-pointer"
                    src="@/assets/pencil.png"
                    @click="onClickForm1"
                  />
                </div>
              </div>
              <div class="ein-summary-item">
                <div>Has ever applied for and received an EIN?</div>
                <div>
                  {{ order.is_previous_ein ? "YES" : "NO" }}&nbsp;&nbsp;<img
                    class="cursor-pointer"
                    src="@/assets/pencil.png"
                    @click="onClickBusinessPhysical"
                  />
                </div>
              </div>
            </form>
          </ValidationObserver>
        </div>

        <div class="col-md-12 offset-lg-2 col-lg-8">
          <div class="button-grp">
            <div class="ein-button ein-back-button mb-5" @click="onClickBack">
              <img src="@/assets/left-arrow.png" />&nbsp;Back
            </div>

            <div class="btn-checkout mb-5" @click="onClickProceed">
              CONTINUE >>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>
<script>

import { SiteUtils } from "../../scripts/site-common";
import { SnackUtils } from "../../scripts/snack-common";
import { ORDER_TYPES, SUB_TYPES, TRUST_SUB_TYPES } from "../../scripts/constants";

export default {
  props: ["order"],
  components: {},
  data() {
    return {
      ORDER_TYPES: ORDER_TYPES,
      SUB_TYPES: SUB_TYPES,
      TRUST_SUB_TYPES: TRUST_SUB_TYPES,
      formInfo: {
        ssn: this.order.ssn,
      },
    };
  },
  methods: {
    onClickBack() {
      this.$emit("back");
    },
    onClickProceed() {
      this.$refs.form.validate().then((success) => {
        if (!success) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }

        this.$emit("continue", this.formInfo);
      });
    },
    onClickForm6() {
      this.$emit("returnForm6");
    },
    onClickEntityType() {
      this.$emit("returnEntityType");
    },
    onClickForm1() {
      this.$emit("returnForm1");
    },
    onClickBusinessPhysical() {
      this.$emit("returnBusinessPhysical");
    },
  },
};
</script>
<style scoped>
.block-title {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 24px;
  line-height: 21px;

  color: #2a96c4;
}

.mb20px {
  margin-bottom: 20px;
}

.mb30px {
  margin-bottom: 20px;
}

.order-desc {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 21px;

  color: #2a96c4;
  padding: 20px 10px;
}

.price-items {
  padding: 20px;
  font-family: "Archivo", sans-serif;
  background: #f9f9f9;
  border-radius: 8px;
}

.price-item {
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 21px;

  color: #000000;

  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.total-item {
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 21px;

  color: #000000;

  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.total-price {
  font-size: 25px;
  line-height: 21px;
}

.button-grp {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.btn-checkout {
  padding: 20px 40px;

  background: #2ac44c;
  border-radius: 8px;

  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;

  color: #ffffff;

  text-align: center;

  cursor: pointer;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
