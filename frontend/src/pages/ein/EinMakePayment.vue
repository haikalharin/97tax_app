<template>
  <div class="container">
    <div class="row">
      <div class="col-md-12 offset-lg-2 col-lg-8">
        <p class="my-3">
          This is for our one-time fee to prepare your application. No other
          charges are ever applied to this card.
        </p>

        <div class="payment-wrapper">
          <div class="ein-form form-dims mt-4 mb-4">
            <ValidationObserver ref="form">
              <form>
                <div class="row">
                  <div class="col-sm-12 col-md-6">
                    <div class="form-group">
                      <label>Cardholder Name</label>
                      <ValidationProvider
                        name="Cardholder Name"
                        rules="required|allowed_str"
                        v-slot="{ errors }"
                      >
                        <input
                          type="text"
                          :class="
                            'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                          "
                          placeholder="Type here"
                          v-model="formInfo.card_holder_name"
                        />
                      </ValidationProvider>
                    </div>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-6 col-sm-12">
                    <div class="form-group">
                      <div class="card-num-label">
                        <label>Card Number</label>
                        <div>
                          <img src="@/assets/payment/visa.png" />&nbsp;<img
                            src="@/assets/payment/mastercard.png"
                          />
                        </div>
                      </div>
                      <ValidationProvider
                        name="Card Number"
                        rules="required"
                        v-slot="{ errors }"
                      >
                        <input
                          type="text"
                          :class="
                            'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                          "
                          placeholder="**** **** **** ****"
                          v-mask="'#### #### #### ####'"
                          v-model="formInfo.card_number"
                        />
                      </ValidationProvider>
                    </div>
                  </div>
                  <div class="col-md-3 col-sm-6">
                    <div class="form-group">
                      <label>Expiration Date</label>
                      <ValidationProvider
                        name="Expiration Date"
                        rules="required"
                        v-slot="{ errors }"
                      >
                        <input
                          type="text"
                          :class="
                            'form-control ' +
                            (errors.length > 0 ? ' is-invalid' : '')
                          "
                          placeholder="MM/YY"
                          v-mask="'##/##'"
                          v-model="formInfo.card_expire_date"
                        />
                      </ValidationProvider>
                    </div>
                  </div>
                  <div class="col-md-3 col-sm-6">
                    <div class="form-group">
                      <label>CVC&nbsp;<span @click="$refs.cardCodeModal.show()" class="cursor-pointer help-color">
                          <font-awesome-icon icon="circle-question"/>
                        </span>
                      </label>

                      <ValidationProvider
                        name="CVC"
                        rules="required"
                        v-slot="{ errors }"
                      >
                        <input
                          type="password"
                          :class="
                            'form-control ' +
                            (errors.length > 0 ? ' is-invalid' : '')
                          "
                          placeholder="***"
                          v-mask="'###'"
                          v-model="formInfo.card_cvc"
                        />
                      </ValidationProvider>
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-12 col-sm-12">
                    <div class="price-info">
                      <div class="price-item mb-3">
                        <div>
                          One-Time Application Fee&nbsp;<span
                            class="help-color"
                            v-b-popover.hover.bottom="
                              'One-Time Enrollment Fee varies by product.'
                            "
                            ><font-awesome-icon icon="circle-question"
                          /></span>
                        </div>
                        <div>${{order.amount / 100}}</div>
                      </div>
                      <div class="price-item mb-3">
                        <div>Subtotal</div>
                        <div>${{order.amount / 100}}</div>
                      </div>
                      <div class="price-item">
                        <div style="font-weight: 700">Total</div>
                        <div style="font-weight: 700; font-size: 25px">
                          ${{order.amount / 100}}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="brand-images mb-3">
                  <img src="@/assets/payment/payment-brand0.png" />
                  <img src="@/assets/payment/payment-brand1.png" />
                  <img src="@/assets/payment/payment-brand2.png" />
                  <img src="@/assets/payment/payment-brand3.png" />
                </div>

                <div class="btn-checkout" @click="onPaymentSuccess">
                  <img src="@/assets/payment/pay-card.png" class="card-img" />Pay
                  With Card
                </div>
              </form>
            </ValidationObserver>
          </div>
        </div>

        <div class="mt-3">
          <p class="text-center" style="color: #888">
            The ${{order.amount / 100}} will be processed now on the card above. You will receive an email with your EIN number within 1 business day when
            your application is complete. We will contact you by email if any additional information is needed to complete your order.
            Personal data is never shared or sold and is fully encrypted and secure in accordance with our Privacy Policy.
          </p>
        </div>

      </div>
    </div>

    <b-modal ref="cardCodeModal" id="cardCodeModal" title="Card Code" ok-only>
      <p class="my-4">
        The "Card Code" is the 3 digit number on the back of your credit card
        like in the picture below.
      </p>
      <img src="@/assets/whatsthis.jpg" style="max-width: 100%" ok-only />
    </b-modal>
  </div>
</template>

<script>
import { SnackUtils } from "../../scripts/snack-common";
export default {
  props: ["order"],
  components: {},
  data() {
    return {
      formInfo: {
        card_holder_name: this.order.first_name + " " + this.order.last_name,
        card_number: this.order.card_number,
        card_expire_date: this.order.card_expire_date,
        card_cvc: this.order.card_cvc,
      },
    };
  },
  methods: {
    onPaymentSuccess() {
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
.payment-wrapper {
  font-family: "Archivo", sans-serif;
  display: flex;
  flex-direction: row;
  justify-content: center;
  flex-wrap: wrap-reverse;
  gap: 10px;
}
.cc-title {
  font-style: normal;
  font-weight: 700;
  font-size: 24px;
  line-height: 16px;
  color: #000000;
  text-align: center;
  margin-bottom: 24px;
}
.card-num-label {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.form-dims {
  max-width: 680px;
  width: 100%;
}
.btn-checkout {
  width: 100%;
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
  position: relative;
}

.card-img {
  position: absolute;
  left: 30px;
  top: 50%;
  width: 24px;
  transform: translateY(-50%);
}
.cursor-pointer {
  cursor: pointer;
}

.price-info {
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  flex-grow: 1;
}
.price-item {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
}

.help-color {
  color: #22a6ab;
}
</style>
