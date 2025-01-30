<template>
  <page>
    <loading :active.sync="isProcessing"></loading>
    <div class="banner">
      <div class="title">
        First Time Penalty<br/>
        (FTA) Abatement
      </div>
    </div>

    <div class="checkout-form">

      <div class="mt-10 mb-1 ml-2 mr-2 panel-between-desc">
        <div class="form-title mb-1">Qualification Check</div>
        IRS First Time Abatement (FTA) will waive your largest penalty. Let’s check now if you qualify.
      </div>

      <div class="panel">
        <ValidationObserver ref="form1">
          <form>
            <div class="form-desc">
              FTA has certain IRS requirements. I certify that:
            </div>
            <div
              class="custom-control custom-checkbox custom-checkbox-green is-invalid mt-3 mb-3"
            >
              <input
                class="custom-control-input custom-control-input-green"
                type="checkbox"
                id="isLast3Filed"
                v-model="orderInfo.isLast3Filed"
                @change="onCheckQualification"
              />
              <label
                :class="
                  'custom-control-label ' +
                  (isLast3FiledError === true ? 'text-danger' : '')
                "
                for="isLast3Filed"
              >
                I filed my last 3 tax returns (OK if not paid yet, but must be
                filed)
              </label>
            </div>

            <div
              class="custom-control custom-checkbox custom-checkbox-green mt-3 mb-3"
            >
              <input
                class="custom-control-input custom-control-input-green"
                type="checkbox"
                id="isLast3IrsWavied"
                v-model="orderInfo.isLast3IrsWavied"
                @change="onCheckQualification"
              />
              <label
                :class="
                  'custom-control-label ' +
                  (isLast3IrsWaviedError === true ? 'text-danger' : '')
                "
                for="isLast3IrsWavied"
              >
                The IRS has not waived a penalty for me in the last 3 years
              </label>
            </div>

            <div
              class="custom-control custom-checkbox custom-checkbox-green mt-3 mb-3"
            >
              <input
                class="custom-control-input custom-control-input-green"
                type="checkbox"
                id="isDecreaseIncomeTax"
                v-model="orderInfo.isDecreaseIncomeTax"
                @change="onCheckQualification"
              />
              <label
                :class="
                  'custom-control-label ' +
                  (isDecreaseIncomTaxError === true ? 'text-danger' : '')
                "
                for="isDecreaseIncomeTax"
              >
                I understand First Time Penalty Abatement will waive the largest penalty from the last 3 years,
                and it will not decrease income tax or interest.
              </label>
            </div>
            <span class="ein-form-err" v-if="qualification_error"
              >Please check all the boxes to continue.</span
            >
          </form>
        </ValidationObserver>
      </div>

      <div class="mt-4 mb-1 ml-2 mr-2 panel-between-desc">
        <div class="form-title mb-1">Penalty Information</div>
        This is the specific penalty to be waived. Check your information below now.
        The penalty amount can be your best estimate. Our tax experts ask the IRS to
        waive the largest penalty you have on record.
      </div>

      <div class="panel">
        <ValidationObserver ref="form2">
          <form>

            <div class="form-group">
              <label>
                Penalty Amount (Just your penalty amount, not your balance)
              </label>

              <ValidationProvider
                name="The penalty amount"
                rules="required|min_value:0.01"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="
                    'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                  "
                  v-model="highestPenalty"
                  placeholder="00.00"
                  inputmode="decimal"
                  @blur="onCheckStep2"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="form-group">
              <label>
                Penalty Type&nbsp;
                <span class="help-color"
                  v-b-popover.hover.bottom="
                    'There are three types of penalty: Failure to File Penalty, Failure to Pay Penalty and Failure to Deposit Penalty (which applies to employers).'
                  ">
                  <font-awesome-icon icon="circle-question" />
                </span>
              </label>

              <ValidationProvider
                name="The penalty type"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="highestPenaltyType"
                  @blur="onCheckStep2"
                >
                  <option value="" style="font-style: italic; color: gray">
                    Type
                  </option>
                  <option
                    v-for="(type, i) in penaltyTypes"
                    :key="i"
                    :value="type.name"
                  >
                    {{ type.description }}
                  </option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="form-group">
              <label>
                Penalty Year&nbsp;
                <span class="help-color"
                  v-b-popover.hover.bottom="
                    'One of the last three tax years (tax year ends April 15).'
                  ">
                  <font-awesome-icon icon="circle-question" />
                </span>
              </label>

              <ValidationProvider
                name="The penalty year"
                rules="required"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="highestPenaltyYear"
                  @blur="onCheckStep2"
                >
                  <option value="" style="font-style: italic; color: gray">
                    Year
                  </option>
                  <option
                    v-for="(y, i) in penaltyYears"
                    :key="i"
                    :value="y"
                  >
                    {{ y }}
                  </option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="form-group">
              <label>
                Did you already pay the penalty? If yes, our tax experts will include a request for a refund for you.
              </label>

              <ValidationProvider
                name="This option"
                rules="required"
                v-slot="{ errors }"
              >
                <select
                  :class="
                    'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                  "
                  v-model="orderInfo.isPenaltyPaid"
                  @blur="onCheckStep2"
                >
                  <option value="1">Yes</option>
                  <option value="0">No</option>
                </select>
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
          </form>
        </ValidationObserver>
      </div>

      <div class="mt-4 mb-1 ml-2 mr-2 panel-between-desc">
        <div class="form-title mb-1">FTA Letter Details</div>
        Our tax experts will send your FTA letter to you first with tracking information so you can sign it.
        The IRS will require your physical signature on the letter.
      </div>

      <div class="panel">
        <ValidationObserver ref="form3">
          <form>
            <div class="form-title mb-3" style="font-weight: 100">Mailing Address</div>
            <div class="form-group mb-0">
              <label v-if="!addressType">Street</label>

              <ValidationProvider
                name="The street"
                rules="required"
                v-slot="{ errors }"
              >
                <vue-google-autocomplete
                  id="gaddress"
                  ref="address"
                  :class="[
                    'form-control',
                    !addressType && errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Street"
                  v-show="!addressType"
                  v-model="orderInfo.shippingAddress1"
                  @placechanged="getAddressData"
                  @blur="extractAddress"
                  @inputChange="setAddress"
                  country="US"
                >
                </vue-google-autocomplete>
                <span class="ein-form-err" v-if="!addressType">{{
                  errors[0]
                }}</span>
              </ValidationProvider>
            </div>

            <div class="custom-control custom-checkbox custom-checkbox-green">
              <input
                class="custom-control-input custom-control-input-green"
                type="checkbox"
                id="mail_to_pobox"
                v-on:click="orderInfo.shippingAddress2 = ''"
                name="addressType"
                v-model="addressType"
              />
              <label class="custom-control-label" for="mail_to_pobox">
                Mail it to a PO Box
              </label>
            </div>

            <div class="form-group" v-if="addressType">
              <label>PO BOX #</label>
              <ValidationProvider
                name="The PO BOX #"
                rules="required"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="PO BOX #"
                  v-model="orderInfo.shippingAddress1"
                  @blur="onCheckStep3"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="form-group">
              <label>Apt/Unit</label>
              <input
                type="text"
                class="form-control"
                placeholder="Apt/Unit"
                v-model="orderInfo.shippingAddress2"
                @blur="onCheckStep3"
              />
            </div>

            <div class="form-group mt-1">
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
                  placeholder="City"
                  v-model="orderInfo.shippingCity"
                  @blur="onCheckStep3"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>State</label>
              <ValidationProvider
                name="The state"
                rules="required|allowed_str"
                v-slot="{ errors }"
              >
                <select
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  v-model="orderInfo.shippingState"
                  @blur="onCheckStep3"
                >
                  <option value="" style="font-style: italic; color: gray">
                    State
                  </option>
                  <option
                    v-for="(s, i) in states"
                    :key="i"
                    :value="s.abbreviation"
                  >
                    {{ s.name }}
                  </option>
                </select>
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
                  placeholder="Zip Code"
                  v-model="orderInfo.shippingPostalCode"
                  @blur="onCheckStep3"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>

            <div class="custom-control custom-checkbox custom-checkbox-green">
              <input
                class="custom-control-input custom-control-input-green"
                type="checkbox"
                id="same_address"
                v-model="shippingAddressSame"
              />
              <label class="custom-control-label" for="same_address">
                My billing address is the same as above
              </label>
            </div>

            <div v-if="!shippingAddressSame">
              <div class="form-group">
                <label>Street</label>
                <ValidationProvider
                  name="The billing street"
                  rules="required"
                  v-slot="{ errors }"
                >
                  <input
                    type="text"
                    :class="[
                      'form-control',
                      errors.length > 0 ? 'is-invalid' : '',
                    ]"
                    placeholder="Street"
                    v-model="orderInfo.billingAddress1"
                    @blur="onCheckStep3"
                  />
                  <span class="ein-form-err">{{ errors[0] }}</span>
                </ValidationProvider>
              </div>

              <div class="form-group">
                <label>Apt/Unit</label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Apt/Unit"
                  v-model="orderInfo.billingAddress2"
                  @blur="onCheckStep3"
                />
              </div>

              <div class="form-group">
                <label>City</label>
                <ValidationProvider
                  name="The billing city"
                  rules="required|allowed_str"
                  v-slot="{ errors }"
                >
                  <input
                    type="text"
                    :class="[
                      'form-control',
                      errors.length > 0 ? 'is-invalid' : '',
                    ]"
                    placeholder="City"
                    v-model="orderInfo.billingCity"
                    @blur="onCheckStep3"
                  />
                  <span class="ein-form-err">{{ errors[0] }}</span>
                </ValidationProvider>
              </div>
              <div class="form-group">
                <label>State</label>
                <ValidationProvider
                  name="The billing state"
                  rules="required|allowed_str"
                  v-slot="{ errors }"
                >
                  <select
                    :class="[
                      'form-control',
                      errors.length > 0 ? 'is-invalid' : '',
                    ]"
                    v-model="orderInfo.billingState"
                    @blur="onCheckStep3"
                  >
                    <option value="" style="font-style: italic; color: gray">
                      State
                    </option>
                    <option
                      v-for="(s, i) in states"
                      :key="i"
                      :value="s.abbreviation"
                    >
                      {{ s.name }}
                    </option>
                  </select>
                  <span class="ein-form-err">{{ errors[0] }}</span>
                </ValidationProvider>
              </div>
              <div class="form-group">
                <label>Zip Code</label>
                <ValidationProvider
                  name="The billing zip code"
                  rules="required|zip_code"
                  v-slot="{ errors }"
                >
                  <input
                    type="text"
                    :class="[
                      'form-control',
                      errors.length > 0 ? 'is-invalid' : '',
                    ]"
                    placeholder="Zip code"
                    v-model="orderInfo.billingPostalCode"
                    @blur="onCheckStep3"
                  />
                  <span class="ein-form-err">{{ errors[0] }}</span>
                </ValidationProvider>
              </div>
            </div>
          </form>
        </ValidationObserver>
      </div>

      <div class="mt-4 mb-1 ml-2 mr-2 panel-between-desc">
        <div class="form-title mb-1">Basic Information</div>
        We will use this information in your FTA letter only so the IRS can identify you.
      </div>

      <div class="panel">
        <ValidationObserver ref="form4">
          <form>
            <div class="form-group">
              <label>First Name</label>
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
                  placeholder="First Name"
                  v-model="orderInfo.firstName"
                  @blur="onCheckStep4"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Last Name</label>
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
                  placeholder="Last Name"
                  v-model="orderInfo.lastName"
                  @blur="onCheckStep4"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Phone Number</label>
              <ValidationProvider
                name="The phone number"
                rules="required"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="
                    'form-control ' + (errors.length > 0 ? ' is-invalid' : '')
                  "
                  placeholder="123-456-7890"
                  v-model="orderInfo.billingPhone"
                  v-mask="'###-###-####'"
                  @blur="onCheckStep4"
                />
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
                  placeholder="mail@email.com"
                  v-model="orderInfo.email"
                  @blur="onCheckStep4"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Social Security Number/TIN</label>
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
                  placeholder="000-00-0000"
                  v-model="orderInfo.ssn"
                  v-mask="'###-##-####'"
                  @blur="onCheckStep4"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
          </form>
        </ValidationObserver>
      </div>

      <div class="mt-4 mb-1 ml-2 mr-2 panel-between-desc">
        <div class="form-title mb-1">Payment Details</div>
        Our tax experts will complete your FTA letter within 24 hours and email you confirmation with a USPS tracking number.
      </div>

      <div class="panel">
        <div class="credit-wrap">
          <div class="credit-wrap-form" v-if="formValidated">
            <ValidationObserver ref="form5">
              <form autocomplete="off">
                <div class="form-group">
                  <label>Card Holder Name</label>
                  <ValidationProvider
                    name="Card Holder Name"
                    rules="required"
                    v-slot="{ errors }"
                  >
                    <input
                      type="text"
                      :class="
                        'form-control ' +
                        (errors.length > 0 ? ' is-invalid' : '')
                      "
                      placeholder="Cardholder Name"
                      v-model="orderInfo.cardHolderName"
                      @blur="onCheckStep5()"
                    />
                  </ValidationProvider>
                </div>
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
                        'form-control ' +
                        (errors.length > 0 ? ' is-invalid' : '')
                      "
                      placeholder="**** **** **** ****"
                      v-mask="'#### #### #### ####'"
                      v-model="orderInfo.cardNumber"
                      @blur="onCheckStep5"
                    />
                  </ValidationProvider>
                </div>
                <div class="row">
                  <div class="col-6">
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
                          autocomplete="off"
                          v-model="card_expire_date"
                          @blur="onCheckStep5"
                        />
                      </ValidationProvider>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="form-group">
                      <label
                        >CVC&nbsp;<span
                          class="help-color cursor-pointer"
                          @click="$refs.cardCodeModal.show()"
                          ><font-awesome-icon icon="circle-question" /></span
                      ></label>
                      <ValidationProvider
                        name="CVC"
                        rules="required"
                        v-slot="{ errors }"
                      >
                        <input
                          type="password"
                          name="cvc"
                          id="cvc"
                          :class="
                            'form-control ' +
                            (errors.length > 0 ? ' is-invalid' : '')
                          "
                          placeholder="***"
                          autocomplete="cvc"
                          v-mask="'###'"
                          v-model="orderInfo.cardCvc"
                          @blur="onCheckStep5"
                        />
                      </ValidationProvider>
                    </div>
                  </div>
                </div>
                <div v-for="(message, i) in errorMessages" :key="i">
                  <div class="ein-form-err" v-if="message.show">
                    {{ message.text }}
                  </div>
                </div>
                <div class="btn-checkout" @click="updatePaymentPlanDetails">
                  <img
                    src="@/assets/payment/pay-card.png"
                    class="card-img"
                  />Pay With Card
                </div>
              </form>
            </ValidationObserver>
          </div>
          <div style="flex-grow: 1">
            <div class="brand-wrapper" style="margin-top: -12px">
              <div
                class="brand-images mb-3"
                style="padding-top: 0px !important"
              >
                <img src="@/assets/payment/payment-brand0.png" />
                <img src="@/assets/payment/payment-brand1.png" />
                <img src="@/assets/payment/payment-brand2.png" />
              </div>
            </div>
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
                <div>${{ customEnrollmentFee }}.00</div>
              </div>
              <div class="price-item mb-3">
                <div>Subtotal</div>
                <div>${{ subTotal.toFixed(2) }}</div>
              </div>
              <div class="price-item">
                <div style="font-weight: 700">Total</div>
                <div style="font-weight: 700; font-size: 25px">
                  ${{ total.toFixed(2) }}
                </div>
              </div>
            </div>
            <div class="form-group" style="float: right" v-if="formValidated">
              <label class="partner-label"
                >Partner Code<small>(not required)</small></label
              >
              <input
                type="text"
                class="partner-input"
                placeholder="Partner Code"
                v-model="orderInfo.partnerCode"
                v-on:blur="checkPartnerCode()"
              />
            </div>
          </div>
        </div>
        <div
          class="green-landing-button mt-3"
          @click="onClickProcess"
          v-if="!formValidated"
        >
          Proceed to Checkout
        </div>
      </div>

      <div class="mt-2 ml-2 mr-2 price-description" v-if="formValidated">
        The ${{total.toFixed(2)}} will be processed now on the card above. You will receive an email with USPS tracking
        information within 1 business day when your application is complete. Any refund of your penalty will go directly
        to you from the IRS. Personal data is never shared or sold and is fully encrypted and secure in accordance with
        our Privacy Policy.
      </div>
    </div>

    <PaymentConfirm
      :amount="total"
      v-model="confirmPaymentModal"
      @continuePayment="continuePayment"
      @cancelPayment="cancelPayment"
    ></PaymentConfirm>
    <SalesWidget :phone_number="salesPhoneNumber" v-if="salesPhoneNumber" />
    <b-modal ref="cardCodeModal" title="Card Code" ok-only>
      <p class="my-4">
        The "Card Code" is the 3 digit number on the back of your credit card
        like in the picture below.
      </p>
      <img src="@/assets/whatsthis.jpg" style="max-width: 100%" ok-only />
    </b-modal>
  </page>
</template>

<script>
import store from "../store/penaltyState";
import { AXIOS } from "../scripts/http-common";
import moment from "moment";
import { SiteUtils } from "../scripts/site-common";
import VueGoogleAutocomplete from "vue-google-autocomplete";
import { paymentPlansConfig } from "../../config/plans-config";
import PaymentConfirm from "./PaymentConfirm.vue";
import SalesWidget from "./../components/SalesWidget.vue";
import isLastDayOfMonth from "date-fns/is_last_day_of_month/index";
import { SnackUtils } from "../scripts/snack-common";
import { US_STATES, PENALTY_TYPES } from "../scripts/constants";
import Loading from "vue-loading-overlay";
// Import stylesheet
import "vue-loading-overlay/dist/vue-loading.css";

const marriedFilingOption = Object.freeze({ separately: "No", jointly: "Yes" });

export default {
  components: { VueGoogleAutocomplete, PaymentConfirm, Loading },
  data() {
    return {
      salesPhoneNumber: null,
      orderInfo: {
        firstName: "",
        lastName: "",
        email: "",
        billingPhone: "",
        billingAddress1: "",
        billingAddress2: "",
        billingCity: "",
        billingState: "",
        billingPostalCode: "",
        shippingAddress1: null,
        shippingAddress2: "",
        shippingCity: "",
        shippingState: "",
        shippingPostalCode: "",
        ssn: "",
        married: "no",
        largestPenalty: "",
        penaltyAmountWaived: "",
        isPenaltyPaid: 0,
        isLast3Filed: false,
        isLast3IrsWavied: false,
        isDecreaseIncomeTax: false,
        partnerCode: "",
        cardHolderName: "",
        cardNumber: "",
        cardExpMonth: "",
        cardExpYear: "",
        cardCvc: "",
      },
      card_expire_date: null,
      formValidated: false,
      processClicked: false,
      highestPenalty: null,
      highestPenaltyType: "",
      highestPenaltyYear: "",
      penaltyArray: [],
      productPrice: null,
      showAddressTextBox: false,
      percentageOff: 0,
      addressType: false,
      shippingAddressSame: true,
      qualification_error: false,
      isLast3FiledError: false,
      isLast3IrsWaviedError: false,
      isDecreaseIncomTaxError: false,
      isProcessing: false,
      confirmPaymentModal: false,
      errorMessages: [],
      jwtData: null,
    };
  },
  created() {
    this.states = US_STATES;
    this.penaltyTypes = PENALTY_TYPES;

    // Calculating last 3 tax years:
    let startYear = this.getStartTaxYear();
    this.penaltyYears = [
      `${startYear}`, `${startYear + 1}`, `${startYear + 2}`
    ];
  },
  methods: {
    getStartTaxYear() {
      const currentDate = new Date();
      let startYear = currentDate.getFullYear() - 4;
      let fmtMMdd = moment(currentDate).format("MM/DD");
      if (fmtMMdd >= "0315") {
        startYear++;
      }
      return startYear;
    },

    async onCheckStep2() {
      const f2_result = this.$refs.form2.validate();
    },
    async onCheckStep3() {
      const f3_result = this.$refs.form3.validate();
    },
    async onCheckStep4() {
      const f4_result = this.$refs.form4.validate();
    },
    async onCheckStep5() {
      const f5_result = this.$refs.form5.validate();
    },

    initializeSalesWidget() {
      AXIOS.get(`/admin/salesList`).then((res) => {
        if (res.status == 200) {
          let list = res.data;
          let index = list.findIndex((it) => {
            let condition_1 =
              it.onOff === true && it.product == "Penalty Waiver";
            let condition_2 = false;
            if (it.startTime && it.endTime) {
              let currentTimeStamp = new Date().getTime();
              let startTimeStamp = new Date(it.startTime).getTime();
              let endTimeStamp = new Date(it.endTime).getTime();
              condition_2 =
                currentTimeStamp > startTimeStamp &&
                currentTimeStamp < endTimeStamp;
            }

            let condition_3 = true;
            let currentTime = moment().tz("America/New_York").format("LT");
            if (!!it.startDaily) {
              let fromTime = moment(it.startDaily).tz("America/New_York").format("LT");
              if (fromTime != "12:00 AM") {
                condition_3 = condition_3 && moment(fromTime, "LT").isBefore(moment(currentTime, "LT"));
              }
            }
            if (!!it.endDaily) {
              let endTime = moment(it.endDaily).tz("America/New_York").format("LT");
              if (endTime != "12:00 AM") {
                condition_3 = condition_3 && moment(currentTime, "LT").isBefore(moment(endTime, "LT"));
              }
            }

            return condition_1 && condition_2 && condition_3;
          });
          if (index > -1) {
            this.salesPhoneNumber = list[index].phoneNumber;
          }
        }
      });
    },

    initializePricings() {
      AXIOS.get(`/admin/productPrice?product_name=Penalty Waiver`).then((res) => {
        if (res.status == 200) {
          console.log(res);
          this.productPrice = res.data.customEnrollmentFee;
          this.ourFee = res.data.customEnrollmentFee;
          this.customEnrollmentFee = res.data.customEnrollmentFee;
        }
      });
    },
    loadOrder() {
      AXIOS.get(`/penalty/order`).then((response) => {
        this.orderInfo.id = response.data.id;
        this.orderInfo.firstName = response.data.firstName;
        this.orderInfo.lastName = response.data.lastName;
        this.orderInfo.billingPhone = response.data.billingPhone;
        this.orderInfo.email = response.data.email;

        this.orderInfo.billingAddress1 = response.data.billingAddress1;
        this.orderInfo.billingAddress2 = response.data.billingAddress2;
        this.orderInfo.billingCity = response.data.billingCity;
        this.orderInfo.billingState = response.data.billingState;
        this.orderInfo.billingPostalCode = response.data.billingZip;

        this.orderInfo.shippingAddress1 = response.data.shippingAddress1;
        this.orderInfo.shippingAddress2 = response.data.shippingAddress2;
        this.orderInfo.shippingCity = response.data.shippingCity;
        this.orderInfo.shippingState = response.data.shippingState;
        this.orderInfo.shippingPostalCode = response.data.shippingZip;
        this.orderInfo.married = response.data.married;
        this.orderInfo.isPenaltyPaid = response.data.isPenaltyPaid;
        this.orderInfo.isLast3Filed = response.data.isLast3Filed;
        this.orderInfo.isLast3IrsWavied = response.data.isLast3IrsWavied;
        this.orderInfo.isDecreaseIncomeTax = response.data.isDecreaseIncomeTax;

        this.penaltyArray = new Array();
        this.penaltyArray.push({
          penaltyType: "",
          year: "",
          penaltyAmount: 0
        });

        this.highestPenalty = parseFloat(this.penaltyArray[0].penaltyAmount);
        this.highestPenaltyType = this.penaltyArray[0].penaltyType;
        this.highestPenaltyYear = this.penaltyArray[0].year;
      });
    },

    initializeCardinal() {
      let orderInfo = Object.assign({}, this.orderInfo);
      if (typeof orderInfo.totalDebt === "string") {
        orderInfo.totalDebt = orderInfo.totalDebt
          .replace(/,/g, "")
          .replace("$", "");
      }

      if (!window.cardinalIsInitialized) {
        Cardinal.configure({
          logging: {
            debug: "verbose",
            timeout: 10000,
          },
        });
        window.cardinalIsInitialized = true;
      }
      let self = this;

      Cardinal.off("payments.setupComplete");
      Cardinal.off("payments.validated");

      Cardinal.on("payments.setupComplete", () => {
        Cardinal.start("cca", self.jwtData.payload);
      });

      Cardinal.on("payments.validated", (data, jwt) => {
        if (data.ActionCode === "SUCCESS" || data.ActionCode === "NOACTION") {
          self.$set(self.orderInfo, "responseJwt", jwt);
          self.orderInfoTemp = SiteUtils.updateAddress(
            self.orderInfoTemp,
            self.orderInfo,
            self.addressType,
            self.shippingAddressSame
          );
          self.orderInfoTemp.isOwedFromBusiness =
            self.orderInfo.isOwedFromBusiness === "yes";
          // self.orderInfoTemp.payrollDeduction = self.orderInfo.payrollDeduction === "yes";
          self.orderInfoTemp.payrollDeduction =
            self.orderInfo.payrollDeduction === "yes" &&
            self.orderInfo.isOwedFromBusiness === "no";
          let payrollDeductionValue = self.orderInfoTemp.payrollDeduction
            ? 12
            : 0;
          self.orderInfoTemp.amount = (
            (this.total - self.discount) *
            100
          ).toString();
          self.orderInfoTemp.married = self.orderInfo.married.toString();
          delete self.orderInfoTemp.isInBusiness;
          delete self.orderInfoTemp.isPayrollTax;
          delete self.orderInfoTemp.isSoleProprietorship;

          let orderForPdf = Object.assign({}, self.orderInfoTemp);
          orderForPdf.orderNumber = self.orderNumber;
          orderForPdf.responseJwt = jwt;

          AXIOS.post(`/penalty/authorize`, orderForPdf)
            .then((response) => {
              if (response.data.success) {
                AXIOS.post(
                  "/pdforderversion/penalty-waiver/refreshOrder",
                  orderForPdf
                )
                  .then((res) => {
                    window.location.href =
                      "/order-confirmation/penalty-waiver/" +
                      response.data.correlationId;
                  })
                  .catch((error) => {
                    console.log("pdf error", error);
                    SnackUtils.warning(
                      "PDF error or unexpected error has occurred. Please try again later."
                    );
                    window.location.href =
                      "/order-confirmation/penalty-waiver/" +
                      response.data.correlationId;
                  });
              } else {
                console.log(
                  "-----------fail message------------",
                  response.data.message
                );
                this.orderInfoTemp.failureReason = response.data.message;
                self.errorMessages.push({
                  show: true,
                  text: response.data.message,
                });
                this.initializeCardinal();
                self.isProcessing = false;

                // if (this.orderInfoTemp.failureReason) {
                //   this.postFailureReason();
                // }
              }
            })
            .catch((e) => {
              if (e.response.status === 409) {
                this.orderInfoTemp.failureReason = "Possible duplicate";
                self.errorMessages.push({
                  show: true,
                  text: "Sorry, it looks like you may have already placed an order with us! Please <a href='/contact-us'>contact us</a> for help.",
                });
                self.isProcessing = false;
              } else {
                this.orderInfoTemp.failureReason = "Error!";
                self.errorMessages.push({
                  show: true,
                  text: "Unable to make purchase. Please check your payment details or try a different payment method.",
                });
                this.initializeCardinal();
                self.isProcessing = false;
              }

              if (this.orderInfoTemp.failureReason) {
                this.postFailureReason();
              }
            });
        } else {
          this.orderInfoTemp.failureReason =
            "3D Secure Failed: Your card was unable to be authenticated. Please check your information or use a different card and try again.";
          self.errorMessages.push({
            show: true,
            text: "3D Secure Failed: Your card was unable to be authenticated. Please check your information or use a different card and try again.",
          });
          this.initializeCardinal();
          self.isProcessing = false;

          if (this.orderInfoTemp.failureReason) {
            this.postFailureReason();
          }
        }
      });
    },
    postFailureReason() {
      AXIOS.put(`/penalty/fail`, this.orderInfoTemp)
        .then((response) => {})
        .catch((error) => {
          console.log(error);
        });
    },

    checkPartnerCode() {
      if (!this.orderInfo.partnerCode) {
        this.orderInfo.amount = (this.total * 100).toString();
      } else {
        this.isProcessing = true;
        AXIOS.get(`/paymentplan/partnercodes/${this.orderInfo.partnerCode}`)
          .then((res) => {
            if (res.data && !Number.isNaN(res.data.percentageOff)) {
              this.percentageOff = res.data.percentageOff;
            } else {
              this.percentageOff = 0;
            }
          })
          .catch((err) => {
            this.percentageOff = 0;
          })
          .finally(() => {
            this.isProcessing = false;
            this.orderInfo.amount = (this.total * 100).toString();
          });
      }
    },

    getAddressData: function (addressData, placeResultData, id) {
      console.log("-----------------");
      console.log(addressData);
      this.orderInfo.shippingAddress1 = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;

      document.getElementById("gaddress").value =
        this.orderInfo.shippingAddress1;

      this.orderInfo.shippingCity = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;

      this.orderInfo.shippingState = addressData.administrative_area_level_1
        ? addressData.administrative_area_level_1
        : "";

      this.orderInfo.shippingPostalCode = addressData.postal_code
        ? addressData.postal_code
        : "";

      this.showAddressTextBox = false;
    },
    extractAddress: function () {
      console.log("blur", this.orderInfo.shippingAddress1);
      google.maps.places.Autocomplete(this.getAddressData, "place_changed");
      //this.showAddressTextBox=false;

      if (this.orderInfo.shippingAddress1 != "") {
        this.showAddressTextBox = false;
      }
    },
    setAddress: function (address, type) {
      console.log("-------------1111----", address, type);
      this.orderInfo.shippingAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
      console.log("-------------1111----", this.orderInfo.shippingAddress1);
    },
    changeToAddressGoogle: function () {
      this.showAddressTextBox = true;
      document.getElementById("address").focus();
    },
    onCheckQualification() {
      if (this.processClicked) {
        this.checkQualificationError();
      }
    },
    checkQualificationError() {
      this.isLast3FiledError = !this.orderInfo.isLast3Filed;
      this.isLast3IrsWaviedError = !this.orderInfo.isLast3IrsWavied;
      this.isDecreaseIncomTaxError = !this.orderInfo.isDecreaseIncomeTax;

      this.qualification_error =
        this.isLast3FiledError ||
        this.isLast3IrsWaviedError ||
        this.isDecreaseIncomTaxError;
    },

    async onClickProcess() {
      this.processClicked = true;
      console.log("-----onClickProcess---");

      this.checkQualificationError();
      let f2_result = await this.$refs.form2.validate();
      let f3_result = await this.$refs.form3.validate();
      let f4_result = await this.$refs.form4.validate();

      this.formValidated = f2_result && f3_result && f4_result && !this.qualification_error;
      if (!this.formValidated) {
        SnackUtils.warning(
          "Please fill invalid fields above."
        );
      }
    },
    onPaymentSuccess() {},

    initializeCardinal() {
      let orderInfo = Object.assign({}, this.orderInfo);
      if (typeof orderInfo.totalDebt === "string") {
        orderInfo.totalDebt = orderInfo.totalDebt
          .replace(/,/g, "")
          .replace("$", "");
      }

      if (!window.cardinalIsInitialized) {
        Cardinal.configure({
          logging: {
            debug: "verbose",
            timeout: 10000,
          },
        });
        window.cardinalIsInitialized = true;
      }
      let self = this;

      Cardinal.off("payments.setupComplete");
      Cardinal.off("payments.validated");

      Cardinal.on("payments.setupComplete", () => {
        Cardinal.start("cca", self.jwtData.payload);
      });

      Cardinal.on("payments.validated", (data, jwt) => {
        if (data.ActionCode === "SUCCESS" || data.ActionCode === "NOACTION") {
          self.$set(self.orderInfo, "responseJwt", jwt);
          self.orderInfoTemp = SiteUtils.updateAddress(
            self.orderInfoTemp,
            self.orderInfo,
            self.addressType,
            self.shippingAddressSame
          );
          self.orderInfoTemp.isOwedFromBusiness =
            self.orderInfo.isOwedFromBusiness === "yes";
          // self.orderInfoTemp.payrollDeduction = self.orderInfo.payrollDeduction === "yes";
          self.orderInfoTemp.payrollDeduction =
            self.orderInfo.payrollDeduction === "yes" &&
            self.orderInfo.isOwedFromBusiness === "no";
          let payrollDeductionValue = self.orderInfoTemp.payrollDeduction
            ? 12
            : 0;
          self.orderInfoTemp.amount = (
            (this.total - self.discount) *
            100
          ).toString();
          self.orderInfoTemp.married = self.orderInfo.married.toString();
          delete self.orderInfoTemp.isInBusiness;
          delete self.orderInfoTemp.isPayrollTax;
          delete self.orderInfoTemp.isSoleProprietorship;

          let orderForPdf = Object.assign({}, self.orderInfoTemp);
          orderForPdf.orderNumber = self.orderNumber;
          orderForPdf.responseJwt = jwt;

          AXIOS.post(`/penalty/authorize`, orderForPdf)
            .then((response) => {
              if (response.data.success) {
                AXIOS.post(
                  "/pdforderversion/penalty-waiver/refreshOrder",
                  orderForPdf
                )
                  .then((res) => {
                    window.location.href =
                      "/order-confirmation/penalty-waiver/" +
                      response.data.correlationId;
                  })
                  .catch((error) => {
                    console.log("pdf error", error);
                    alert(
                      "PDF error or unexpected error has occurred. Please try again later."
                    );
                    window.location.href =
                      "/order-confirmation/penalty-waiver/" +
                      response.data.correlationId;
                  });
              } else {
                this.orderInfoTemp.failureReason = response.data.message;
                self.errorMessages.push({
                  show: true,
                  text: response.data.message,
                });
                this.initializeCardinal();
                self.isProcessing = false;

                if (this.orderInfoTemp.failureReason) {
                  this.postFailureReason();
                }
              }
            })
            .catch((e) => {
              if (e.response.status === 409) {
                this.orderInfoTemp.failureReason = "Possible duplicate";
                self.errorMessages.push({
                  show: true,
                  text: "Sorry, it looks like you may have already placed an order with us! Please <a href='/contact-us'>contact us</a> for help.",
                });
                self.isProcessing = false;
              } else {
                this.orderInfoTemp.failureReason = "Error!";
                self.errorMessages.push({
                  show: true,
                  text: "Unable to make purchase. Please check your payment details or try a different payment method.",
                });
                this.initializeCardinal();
                self.isProcessing = false;
              }
              if (this.orderInfoTemp.failureReason) {
                this.postFailureReason();
              }
            });
        } else {
          this.orderInfoTemp.failureReason =
            "3D Secure Failed: Your card was unable to be authenticated. Please check your information or use a different card and try again.";
          self.errorMessages.push({
            show: true,
            text: "3D Secure Failed: Your card was unable to be authenticated. Please check your information or use a different card and try again.",
          });
          this.initializeCardinal();
          self.isProcessing = false;
        }
        if (this.orderInfoTemp.failureReason) {
          this.postFailureReason();
        }
      });
    },
    updatePaymentPlanDetails() {
      console.log("---------");
      this.$refs.form5.validate().then((success) => {
        console.log(success);
        if (success) {
          this.confirmPaymentModal = true;
        } else {
          SnackUtils.warning("Please fill the fields in payment form.");
        }
      });
    },
    continuePayment() {
      this.confirmPaymentModal = false;

      // this.isProcessing = true;

      // let order=Object.assign({},this.orderInfo)
      // order.shippingAddress1 = SiteUtils.standardizeAddress(this.addressType,order.shippingAddress1);
      this.orderInfoTemp = SiteUtils.updateAddress(
        this.orderInfoTemp,
        this.orderInfo,
        this.addressType,
        this.shippingAddressSame
      );
      this.orderInfoTemp.married = this.orderInfo.married.toString();

      this.orderInfoTemp.amount = (this.total * 100).toString();
      // this.orderInfoTemp.hasOldAddress = this.orderInfo.hasOldAddress === "yes";
      let names = [];
      delete this.orderInfoTemp.hasOldAddress;
      delete this.orderInfoTemp.amount;

      console.log(this.orderInfoTemp);
      AXIOS.put(`/penalty`, this.orderInfoTemp)
        .then((result) => {
          const { id, correlationId } = result.data;
          this.postOrderInfo(id, correlationId);
        })
        .catch((err) => {
          alert(err);
          console.log(err);
          // this.isProcessing = false;
        });
    },
    cancelPayment() {
      if (typeof this.orderInfo.totalDebt === "string") {
        this.orderInfo.totalDebt = this.orderInfo.totalDebt.replace("$", "");
        this.totalDebtPrevValue = this.orderInfo.totalDebt;
      }

      // let order=Object.assign({},this.orderInfo)
      // order.shippingAddress1 = SiteUtils.standardizeAddress(this.addressType,order.shippingAddress1);
      this.orderInfoTemp = SiteUtils.updateAddress(
        this.orderInfoTemp,
        this.orderInfo,
        this.addressType,
        this.shippingAddressSame
      );
      this.orderInfoTemp.isOwedFromBusiness =
        this.orderInfo.isOwedFromBusiness === "yes";
      this.orderInfoTemp.married = this.orderInfo.married.toString();
      this.orderInfoTemp.payrollDeduction =
        this.orderInfo.payrollDeduction === "yes" &&
        this.orderInfo.isOwedFromBusiness === "no";
      let payrollDeductionValue = this.orderInfoTemp.payrollDeduction ? 12 : 0;
      this.orderInfoTemp.amount = (this.total * 100).toString();
      // this.orderInfoTemp.hasOldAddress = this.orderInfo.hasOldAddress === "yes";

      delete this.orderInfoTemp.isInBusiness;
      delete this.orderInfoTemp.isPayrollTax;
      delete this.orderInfoTemp.isSoleProprietorship;

      this.isProcessing = false;
      this.orderInfoTemp.failureReason = "Payment Confirmation Declined";
      this.postFailureReason();
    },
    postOrderInfo(orderNum, correlationId) {
      this.errorMessages = [];
      let orderInfo = Object.assign({}, this.orderInfo);

      this.isProcessing = true;

      this.orderInfo.billingPhone = SiteUtils.standardizePhone(
        this.orderInfo.billingPhone
      );
      this.orderInfo.ssn = SiteUtils.standardizeSsn(this.orderInfo.ssn);

      if (this.shippingAddressSame) {
        this.orderInfo.billingAddress1 = this.orderInfo.shippingAddress1;
        this.orderInfo.billingAddress2 = this.orderInfo.shippingAddress2;
        this.orderInfo.billingCity = this.orderInfo.shippingCity;
        this.orderInfo.billingState = this.orderInfo.shippingState;
        this.orderInfo.billingPostalCode = this.orderInfo.shippingPostalCode;
      }

      this.orderInfo.shippingFirstName = this.orderInfo.firstName;
      this.orderInfo.shippingLastName = this.orderInfo.lastName;
      this.orderInfo.shippingPhone = this.orderInfo.billingPhone;

      this.orderInfo.penaltyAmountWaived = parseFloat(this.highestPenalty).toFixed(2);
      this.orderInfo.penaltyWaivedType = this.highestPenaltyType;
      this.orderInfo.penaltyWaivedYear = this.highestPenaltyYear;

      this.orderInfo.cardNumber = this.orderInfo.cardNumber.replace(
        /[^0-9]/g,
        ""
      );

      if (typeof this.orderInfo.totalDebt === "string") {
        this.orderInfo.totalDebt = this.orderInfo.totalDebt
          .replace(/,/g, "")
          .replace("$", "");
        this.totalDebtPrevValue = this.orderInfo.totalDebt;
      }

      let curDate = new Date();
      let curYear = curDate.getFullYear().toString().substring(0, 2);
      this.orderInfo.cardExpMonth = this.card_expire_date.substring(0, 2);
      this.orderInfo.cardExpYear =
        curYear + this.card_expire_date.substring(3, 5);

      this.orderInfoTemp = SiteUtils.updateAddress(
        this.orderInfoTemp,
        this.orderInfo,
        this.addressType,
        this.shippingAddressSame
      );
      this.orderInfoTemp.isOwedFromBusiness =
        this.orderInfo.isOwedFromBusiness === "yes";
      this.orderInfoTemp.married = this.orderInfo.married.toString();
      // this.orderInfoTemp.payrollDeduction = this.orderInfo.payrollDeduction === "yes";
      // this.orderInfoTemp.hasOldAddress = this.orderInfoTemp.hasOldAddress === "yes";
      this.orderInfoTemp.payrollDeduction =
        this.orderInfo.payrollDeduction === "yes" &&
        this.orderInfo.isOwedFromBusiness === "no";
      let payrollDeductionValue = this.orderInfoTemp.payrollDeduction ? 12 : 0;
      this.orderInfoTemp.amount = (this.total * 100).toString();

      delete this.orderInfoTemp.isInBusiness;
      delete this.orderInfoTemp.isPayrollTax;
      delete this.orderInfoTemp.isSoleProprietorship;
      delete this.orderInfoTemp.oldAddressType;
      delete this.orderInfoTemp.payrollDeduction;
      delete this.orderInfoTemp.isOwedFromBusiness;
      delete this.orderInfoTemp.hasOldAddress;

      if (this.orderInfo.penaltyAmountWaived == 0) {
        let orderForPdf = Object.assign({}, this.orderInfoTemp);
        orderForPdf.orderNumber = orderNum;
        window.location.href =
          "/order-confirmation/penalty-waiver/" + correlationId;
      } else {
        let self = this;

        AXIOS.post(`/penalty/jwt`, this.orderInfoTemp)

          .then((response) => {
            self.$set(self, "jwtData", response.data);
            self.orderNumber = response.data.payload.OrderDetails.OrderNumber;
            Cardinal.setup("init", {
              jwt: self.jwtData.jwt,
            });
          })
          .catch((e) => {
            console.log(e);
            self.errorMessages.push({
              show: true,
              text: "Unexpected error occurred. Please try again later.",
            });
            this.initializeCardinal();
            self.isProcessing = false;
          });
      }
    },
  },
  computed: {
    subTotal: function () {
      /*
      return (
        this.enrollmentFee +
        this.processingFee +
        this.payrollDeductionFee +
        this.changeOfAddressFee
      );*/
      return this.productPrice;
    },
    total: function () {
      return this.subTotal - this.discount;
    },
    discount: function () {
      return this.subTotal * (this.percentageOff / 100);
    },
  },
  mounted() {
    this.initializeSalesWidget();
    this.initializeCardinal();

    this.initializePricings();
    window.ANS_customer_id = "d7332b5b-b0c5-4072-9237-bef8a02352ae";
    this.loadOrder();
  },
};
</script>

<style scoped>
.banner {
  background: #f8faf9;
  padding: 50px 0px 0px 0px;
}
.title {
  margin: 0 auto;
  width: 100%;
  max-width: 620px;
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 60px;
  line-height: 74px;
  text-align: center;

  color: #252020;
}
@media only screen and (max-width: 862px) {
  .title {
    font-size: 48px;
    line-height: 58px;
  }
}
.green-underline {
  text-decoration: underline;
  text-decoration-color: #2ac44c;
}
.green-color {
  color: #2ac44c;
}
.sub-title {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 100;
  font-size: 18px;
  line-height: 24px;
  text-align: center;

  color: #817e7e;
}

.checkout-img {
  text-align: center;
  position: relative;
}

.checkout-form {
  background: white;
  padding: 0px 0px 40px 0px;
  max-width: 872px;
  width: 100%;
  margin: 0 auto;
}

.form-title {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 22px;
  line-height: 150%;

  /* text-align: center; */
  color: #252020;
}

.mt-10 {
    margin-top: 4.5rem !important;
}

.form-desc {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 28px;
  color: #252020;
}

.circle-price {
  position: absolute;
  left: 50%;
  top: 72px;
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 31.2849px;
  line-height: 38px;
  text-align: center;
  transform: translateX(-50%);

  color: #ffffff;
}

.stepper-graph {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  position: relative;
}

.stepper-circle {
  z-index: 999;
  background: #d9d9d9;
  width: 48px;
  height: 48px;
  border-radius: 50%;

  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 20px;
  line-height: 24px;

  color: #ffffff;

  text-align: center;
  padding-top: 11px;
  position: relative;
}

.stepper-active {
  background: #2ac44c;
}

.bg-line {
  width: 100%;
  background: #d9d9d9;
  height: 4px;
  position: absolute;
  left: 0px;
  top: 50%;
  transform: translateY(-50%);
}

.active-line-wrapper {
  background: #d9d9d9;
  height: 4px;
  position: absolute;
  left: 24px;
  width: calc(100% - 48px);
  top: 50%;
  transform: translateY(-50%);
}

.active-line {
  height: 4px;
  background: #2ac44c;
}

.panel {
  background: #f5f5f5;
  padding: 40px;

  border: 1px solid #d9d9d9;
  border-radius: 12px;
}

.panel-active {
  border: 1px solid #2ac44c;
}

.blue-item {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  gap: 10px;
  flex-wrap: wrap;

  background: rgba(42, 150, 196, 0.2);
  border-radius: 6px;

  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 21px;
  margin-bottom: 10px;
}

.blue-item-value {
  width: 160px;
}

.help-color {
  color: #22a6ab;
}

.brand-wrapper {
  max-width: 460px;
  width: 100%;
  margin: 0 auto;
}

.price-info {
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  flex-grow: 1;
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

.credit-wrap {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap-reverse;
  gap: 20px;
  align-items: flex-end;
}

.credit-wrap-form {
  max-width: 360px;
  width: 100%;
}

@media screen and (max-width: 703px) {
  .credit-wrap-form {
    max-width: 100%;
    width: 100%;
  }
}

.card-num-label {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.price-item {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
}

.good-news {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 18px;
  line-height: 24px;
  color: #817e7e;
}

.panel-between-desc {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 150%;

  color: #252020;
}

.price-description {
  font-family: "PT Sans", serif;
  font-size: 17px;
  font-weight: 400;
  line-height: 25px;
  text-align: center;
  color: #716060;
}

.step-desc {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 18px;
  line-height: 22px;
  text-align: center;
  color: #252020;
  display: block;
  position: absolute;
  transform: translateX(-50%);
  left: 50%;
  top: 100%;
}

.mt80 {
  margin-top: 80px;
}

.cursor-pointer {
  cursor: pointer;
}

.bold-font {
  font-weight: bold;
}

::placeholder {
  /* Chrome, Firefox, Opera, Safari 10.1+ */
  color: #e5e5e5 !important;
  opacity: 1; /* Firefox */
}

:-ms-input-placeholder {
  /* Internet Explorer 10-11 */
  color: #e5e5e5 !important;
}

::-ms-input-placeholder {
  /* Microsoft Edge */
  color: #e5e5e5 !important;
}
.partner-label {
  font-size: 0.8rem;
}
.partner-input {
  width: 110px;
  display: block;
  border: 1px solid #b3b3b3;
  font-size: 0.8rem;
  border-radius: 3px;
}
</style>
