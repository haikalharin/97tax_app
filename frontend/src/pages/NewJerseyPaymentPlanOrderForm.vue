<template>
  <page :hideTopNav="true" :navType="'new-jersey-payment-plan-order-form'">
    <div class="container">
      <div class="my-3">
        <router-link to="/">
          <img src="@/assets/LogoHeader.png" width="150" />
        </router-link>
      </div>
      <div class="mt-3">
        <h2 class="font-weight-bold">
          You qualify for a
          <span class="blue">{{ paymentAmountMobile }}</span> payment plan with
          New Jersey
        </h2>
        <p class="my-3">
          We'll need the information below completed to prepare your payment
          plan application:
        </p>
      </div>
      <div class="billing-form">
        <div class="my-3">
          <h4 class="text-center">Billing Details</h4>
        </div>
        <div class="row">
          <div class="form-group col-12 col-sm-6">
            <label>First Name</label>
            <input
              classes="billing__first-name"
              v-model="orderInfo.billingFirstName"
              :class="[
                'form-control',
                error.billingFirstName ? 'is-invalid' : '',
              ]"
              :error="error.billingFirstName"
              placeholder="First Name"
              v-on:keypress="isLetter($event)"
            />
            <div
              v-if="error.billingFirstName"
              class="text-danger"
              style="margin-top: 10px"
            >
              {{ error.billingFirstName }}
            </div>
          </div>
          <div class="form-group col-12 col-sm-6">
            <label>Last Name</label>
            <input
              classes="billing__last-name"
              v-model="orderInfo.billingLastName"
              :class="[
                'form-control',
                error.billingLastName ? 'is-invalid' : '',
              ]"
              :error="error.billingLastName"
              placeholder="Last Name"
              v-on:keypress="isLetter($event)"
            />

            <div
              v-if="error.billingLastName"
              class="text-danger"
              style="margin-top: 10px"
            >
              {{ error.billingLastName }}
            </div>
          </div>

          <div class="form-group col-12 col-sm-6">
            <form-input-field
              v-model="orderInfo.billingPhone"
              classes="billing__phoneNumber"
              :error="error.billingPhone"
              label="Phone Number"
              v-mask="'###-###-####'"
              :name="'phoneNumber'"
              type="tel"
            ></form-input-field>
            <div
              v-if="error.billingPhone"
              class="text-danger"
              style="margin-top: 10px"
            >
              {{ error.billingPhone }}
            </div>
          </div>

          <div class="form-group col-12 col-sm-6">
            <form-input-field
              v-model="orderInfo.email"
              :error="error.email"
              classes="billing__email"
              label="Email"
            ></form-input-field>
            <div
              v-if="error.email"
              class="text-danger"
              style="margin-top: 10px"
            >
              {{ error.email }}
            </div>
          </div>
        </div>
      </div>
      <p class="my-3">
        We'll mail your tax documents to the address below with tracking. New
        Jersey requires your physical signature:
      </p>
      <div class="mailing-form">
        <div class="my-3">
          <h4 class="text-center">Mailing Address</h4>
        </div>
        <div class="row align-items-end">
          <div class="form-group col-12 col-sm-4">
            <label>Address</label>
            <div class="d-flex align-items-center">
              <input
                type="checkbox"
                v-on:click="
                  (orderInfo.shippingAddress1 = ''),
                    (orderInfo.shippingAddress2 = '')
                "
                name="addressType"
                :value="true"
                v-model="addressType"
              />
              <span class="ml-2">Mail to a PO BOX</span>
            </div>
            <vue-google-autocomplete
              id="address"
              :class="[
                'form-control',
                error.shippingAddress1 || error.addressType ? 'is-invalid' : '',
              ]"
              v-show="!addressType && showGoogleTextBox"
              placeholder="Street Address"
              @placechanged="getAddressData"
              @blur="extractAddress"
              @inputChange="setAddress"
              country="us"
            >
            </vue-google-autocomplete>
            <input
              :class="[
                'form-control',
                error.shippingAddress1 || error.addressType ? 'is-invalid' : '',
              ]"
              v-if="!showGoogleTextBox && !addressType"
              v-model="orderInfo.shippingAddress1"
              :error="error.shippingAddress1 || error.addressType"
              @focusin="changeToGoogle"
            />
            <div class="align-items-center">
              <div v-if="addressType" class="mr-2">PO. BOX #</div>
              <input
                :class="[
                  'form-control',
                  error.shippingAddress1 || error.addressType
                    ? 'is-invalid'
                    : '',
                ]"
                style="flex: 1"
                v-if="addressType"
                v-model="orderInfo.shippingAddress1"
                :error="error.shippingAddress1 || error.addressType"
                @keypress="onlyNumber"
                type="tel"
              />
            </div>
          </div>
          <div class="form-group col-12 col-sm-4" v-if="!addressType">
            <form-input-field
              classes="mailing__address2"
              maxlength="12"
              v-model="orderInfo.shippingAddress2"
              :error="error.shippingAddress2"
              label="Apt/Unit #"
            ></form-input-field>
          </div>
          <div class="form-group col-12 col-sm-4">
            <form-input-field
              classes="mailing__city"
              v-model="orderInfo.shippingCity"
              :error="error.shippingCity"
              label="City"
            ></form-input-field>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-sm-4">
            <span
              v-if="
                error.shippingAddress1 && !error.addressType && !addressType
              "
              class="w-100 text-danger pl-3"
            >
              {{ error.shippingAddress1 }}
            </span>
            <span
              v-if="error.addressType && !error.shippingAddress1 && addressType"
              class="w-100 text-danger pl-3"
            >
              {{ error.shippingAddress1 }}
            </span>
          </div>
          <div class="col-12 col-sm-4">
            <span v-if="error.shippingAddress2" class="text-danger mt-1">
              This box is for your APT/UNIT/STE number only.
            </span>
          </div>
          <div class="col-12 col-sm-4">
            <span v-if="error.shippingCity" class="text-danger mt-1">
              This box is for your City.
            </span>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-sm-6">
            <state-select-field
              v-model="orderInfo.shippingState"
              classes="state_select_field"
              :error="error.shippingState"
              label="State"
            ></state-select-field>
          </div>
          <div class="col-12 col-sm-6">
            <label>Zip Code</label>
            <input
              v-model="orderInfo.shippingPostalCode"
              :maxlength="5"
              :error="error.shippingPostalCode"
              placeholder="Zip Code"
              type="tel"
              v-mask="'#####'"
              :class="[
                'form-control',
                error.shippingPostalCode ? 'is-invalid' : '',
              ]"
            />
          </div>

          <div class="col-12 col-sm-6">
            <div class="mailing form-group">
              <div class="d-flex align-items-center">
                <input
                  type="checkbox"
                  class="mr-2"
                  v-model="shippingAddressSame"
                />
                <span>My billing address is the same as above</span>
              </div>
            </div>
          </div>

          <div class="col-12 col-sm-6">
            <span v-if="error.shippingPostalCode" class="text-danger mt-1">
              5 numbers only. Letters not allowed.
            </span>
          </div>
        </div>

        <div class="row" v-if="!shippingAddressSame">
          <form-input-field
            v-model="orderInfo.billingAddress1"
            :error="error.billingAddress1"
            label="Address Line 1"
          ></form-input-field>
          <form-input-field
            v-model="orderInfo.billingAddress2"
            :error="error.billingAddress2"
            label="Address Line 2"
          ></form-input-field>
          <form-input-field
            v-model="orderInfo.billingCity"
            :error="error.billingCity"
            label="City"
          ></form-input-field>
          <state-select-field
            v-model="orderInfo.billingState"
            :error="error.billingState"
            label="State"
          ></state-select-field>
          <form-input-field
            v-model="orderInfo.billingPostalCode"
            :maxlength="5"
            :error="error.billingPostalCode"
            label="Zip Code"
            v-mask="'#####'"
            type="tel"
          ></form-input-field>
        </div>
      </div>
      <p class="my-3">
        You can adjust your monthly payments below. We calculate the payments at
        the minimum allowed to start.
      </p>
      <div class="payment-form">
        <div class="my-3">
          <h4 class="text-center">Payment Information</h4>
        </div>
        <div class="row">
          <div class="col-12 col-sm-4 mt-sm-5 pt-sm-2">
            <label>
              Total Debt Owed
              <span
                v-b-popover.hover.bottom="
                  'The total amount you owe can be found on notices received by the FTB. This amount includes all tax years.'
                "
              >
                <img src="./../assets/HelpBubble.png" />
              </span>
            </label>
            <input
              :class="['form-control', error.totalDebt ? 'is-invalid' : '']"
              :error="!!error.totalDebt"
              class="decimalWholePart"
              v-model="orderInfo.totalDebt"
              v-on:input="validateTotalDebt"
              placeholder="$00.00"
              inputmode="decimal"
              ref="target"
              @blur="checkTotalDebtQualification"
              type="text"
            />
            <div class="text-danger" v-if="error.totalDebt">
              {{ error.totalDebt }}
            </div>
          </div>
          <div class="col-12 col-sm-4">
            <div class="mb-2">
              <div class="text-center font-weight-bold">
                Your monthly payment
              </div>
              <div class="text-center font-weight-bold">to New Jersey:</div>
            </div>
            <div class="d-flex justify-content-center align-items-center">
              <span class="green-text mr-2 payment-amount-sign font-weight-bold"
                >$</span
              >
              <input
                class="form-control green-text payment-amount font-weight-bold"
                :class="error.paymentAmount ? 'is-invalid' : ''"
                type="tel"
                v-model="paymentAmount"
                :error="error.paymentAmount"
                v-on:input="validateInputPaymentAmount"
                v-on:blur="onPaymentAmountChange"
              />
            </div>
            <h6 class="text-center my-1">over</h6>
            <div class="d-flex justify-content-center align-items-center">
              <button
                class="btn btn-months-adjust mr-2"
                :class="
                  orderInfo.paymentMonths <= paymentPlanConfig.minMonth
                    ? 'btn-secondary'
                    : 'btn-primary'
                "
                :disabled="
                  orderInfo.paymentMonths <= paymentPlanConfig.minMonth
                "
                v-on:click="decreasePaymentMonths()"
              >
                -
              </button>
              <input
                class="form-control months"
                :class="error.paymentMonths ? 'is-invalid' : ''"
                type="tel"
                v-model="orderInfo.paymentMonths"
                v-mask="'##'"
                :error="error.paymentMonths"
                @keyup="onPaymentMonthsChange"
              />
              <button
                class="btn btn-months-adjust ml-2"
                :class="
                  orderInfo.paymentMonths >= maxPaymentMonth
                    ? 'btn-secondary'
                    : 'btn-primary'
                "
                :disabled="orderInfo.paymentMonths >= maxPaymentMonth"
                v-on:click="increasePaymentMonths()"
              >
                +
              </button>
            </div>
            <h6 class="text-center my-1">months</h6>
          </div>
          <div class="col-12 col-sm-4 mt-sm-5 pt-sm-2">
            <div
              v-if="error.paymentAmount && error.paymentAmount != true"
              class="text-danger mt-1"
            >
              {{ error.paymentAmount }}
            </div>
          </div>
        </div>

        <div class="row">
          <div class="col col-3">
            <label> Is this amount owed for a business? </label>
          </div>
          <div class="col col-1" style="display: flex">
            <select
              class="form-control"
              style="width: fit-content"
              :value="orderInfo.isOwedFromBusiness"
              @change="onIsOwedFromBusinessChange($event.target.value)"
            >
              <option :value="true">Yes</option>
              <option :value="false">No</option>
            </select>
            <span
              v-b-popover.hover.bottom="
                'Select &quot;Yes&quot; if you own an LLC, Corporation, or Partnership. Select &quot;No&quot; if you are a sole proprietor.'
              "
            >
              <img src="./../assets/HelpBubble.png" />
            </span>
          </div>
          <div class="col-2" v-if="orderInfo.isOwedFromBusiness"></div>
          <div class="col-5 d-flex" v-if="orderInfo.isOwedFromBusiness">
            <div class="mr-3">
              <input type="checkbox" v-model="orderInfo.physNotMailing" />
            </div>
            <div>
              <span
                >The physical address of my business is same as mailing address
                given above.</span
              >
            </div>
          </div>
        </div>
        <div class="row" v-if="orderInfo.isOwedFromBusiness">
          <form-input-field
            v-model="orderInfo.businessName"
            :error="error.businessName"
            label="Legal Business Name"
            placeholder="Business Name"
            :name="'businessName'"
          ></form-input-field>
        </div>
        <div class="row" v-if="orderInfo.isOwedFromBusiness">
          <form-input-field
            v-model="orderInfo.ein"
            :error="error.ein"
            label="Business EIN"
            placeholder="EIN #"
            v-mask="'##-#######'"
            :name="'ein'"
            type="tel"
          ></form-input-field>
        </div>
        <div
          class="row"
          v-if="!orderInfo.physNotMailing && orderInfo.isOwedFromBusiness"
        >
          <div class="my-3 col-12">
            <h4 class="text-center">Business Physical Address</h4>
          </div>
          <div class="form-group col-12 col-sm-6">
            <label>Address (No PO Box Allowed)</label>
            <vue-google-autocomplete
              v-show="showBusinessAddressGoogle"
              id="businessAddress"
              :class="[
                'form-control',
                error.businessAddress1 ? 'is-invalid' : '',
              ]"
              placeholder="Please type street address"
              v-on:placechanged="getBusinessAddressData"
              v-on:inputChange="setBusinessAddress"
              v-on:blur="extractBusinessAddress"
              :country="['us']"
              :value="orderInfo.businessAddress1"
            >
            </vue-google-autocomplete>
            <input
              :class="[
                'form-control',
                error.businessAddress1 ? 'is-invalid' : '',
              ]"
              v-if="!showBusinessAddressGoogle"
              v-model="orderInfo.businessAddress1"
              :error="error.businessAddress1"
              v-on:focusin="changeToBusinessAddressGoogle"
            />
          </div>
          <form-input-field
            v-model="orderInfo.businessAddress2"
            :error="error.businessAddress2"
            label="Address Line 2"
          ></form-input-field>
          <form-input-field
            v-model="orderInfo.businessCity"
            :error="error.businessCity"
            label="City"
          ></form-input-field>
          <state-select-field
            v-model="orderInfo.businessState"
            :error="error.businessState"
            label="State"
          ></state-select-field>
          <form-input-field
            v-model="orderInfo.businessPostalCode"
            :maxlength="5"
            :error="error.businessPostalCode"
            label="Zip Code"
            v-mask="'#####'"
            type="tel"
          ></form-input-field>
        </div>
      </div>
      <p class="my-3">Here are more details for your application.</p>
      <div class="application-details-form">
        <div class="my-3">
          <h4 class="text-center">Application Details</h4>
        </div>
        <div class="row">
          <form-input-field
            classes="form-group col-12 col-sm-6 offset-sm-3"
            v-model="orderInfo.ssn"
            :error="error.ssn"
            label="Social Security Number or TIN"
            placeholder="000-00-0000"
            :name="'ssn'"
            type="tel"
            autocomplete="off"
          ></form-input-field>
          <form-input-field
            classes="form-group col-12 col-sm-6 offset-sm-3"
            v-model="orderInfo.secondaryPhone"
            :error="error.secondaryPhone"
            label="Daytime / Second Phone Number (not required)"
            placeholder="000-000-0000"
            :name="'secondaryPhone'"
            type="tel"
            autocomplete="off"
          ></form-input-field>
          <generic-select-field
            classes="form-group col-12 col-sm-6 offset-sm-3"
            v-model="orderInfo.paymentDayOfMonth"
            :error="error.paymentDayOfMonth"
            label="Day of Month to Make Payments"
            helpText="This is the day of the month you prefer to make your normal monthly payment. It will not necessarily be the day your first payment is due because it takes time for New Jersey to approve your payment plan application."
          >
            <option v-for="index in 28" :value="index" :key="index">
              {{ index }}
            </option>
          </generic-select-field>
        </div>
      </div>
      <p class="my-3">
        This is for our one-time fee to prepare your application. No other
        charges are ever applied to this card.
      </p>
      <div class="card-form">
        <div class="my-3 card-form-header">
          <h4 class="text-center">Credit or Debit Card</h4>
          <div class="card-imgs">
            <img class="mr-2" src="@/assets/Visa.png" height="35" />
            <img src="@/assets/Mastercard.png" height="35" />
          </div>
          <div class="partner-code-block d-flex align-items-center">
            <div class="mr-3">
              <div><small>Partner Code</small></div>
              <div><small>(not required)</small></div>
            </div>
            <div>
              <form-input-field
                classes="card__partner-code"
                v-model="orderInfo.partnerCode"
                v-on:blur="checkPartnerCode()"
                maxlength="8"
              ></form-input-field>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-sm-3">
            <div class="mx-auto text-center mt-4 card-logos">
              <div class="d-inline-block d-sm-block mb-3">
                <a
                  href="https://verify.authorize.net/anetseal/?pid=d7332b5b-b0c5-4072-9237-bef8a02352ae&amp;rurl=https%3A//www.97tax.com"
                  onmouseover="window.status='http://www.authorize.net/'; return true;"
                  onmouseout="window.status=''; return true;"
                  onclick="window.open('https://verify.authorize.net/anetseal/?pid=d7332b5b-b0c5-4072-9237-bef8a02352ae&amp;rurl=https%3A//www.97tax.com','AuthorizeNetVerification','width=600,height=430,dependent=yes,resizable=yes,scrollbars=yes,menubar=no,toolbar=no,status=no,directories=no,location=yes'); return false;"
                  target="_blank"
                >
                  <img
                    src="https://verify.authorize.net/anetseal/images/secure90x72.gif"
                    width="90"
                    height="72"
                    border="0"
                    alt="Authorize.Net Merchant - Click to Verify"
                  />
                </a>
              </div>
              <div class="d-inline-block d-sm-block mb-3">
                <a
                  href="https://usa.visa.com/pay-with-visa/featured-technologies/verified-by-visa.html"
                  target="_blank"
                >
                  <img width="100" src="./../assets/logo_vbv.gif" />
                </a>
              </div>
              <div class="d-inline-block d-sm-block mb-3">
                <a
                  href="https://www.mastercard.us/en-us/consumers/payment-technologies/securecode.html"
                  target="_blank"
                >
                  <img width="100" src="./../assets/secureCode_logo.gif" />
                </a>
              </div>
            </div>
          </div>
          <div class="col-12 col-sm-9">
            <div class="row my-4">
              <div class="form-group col-12 col-sm-5">
                <form-input-field
                  type="tel"
                  classes="card__number"
                  v-model="orderInfo.cardNumber"
                  :error="error.cardNumber"
                  label="Card Number"
                  placeholder="**** **** **** ****"
                  v-mask="'#### #### #### ####'"
                  :name="'cardNumber'"
                  autocomplete="off"
                ></form-input-field>
              </div>
              <div class="form-group col-12 col-sm-4 p-sm-0">
                <label>Card Expiration</label>
                <div class="d-flex align-items-center">
                  <input-month-dropdown
                    classes="card__exp-month"
                    v-model="orderInfo.cardExpMonth"
                    :error="error.cardExpMonth"
                    placeholder="MM"
                    autocomplete="off"
                  ></input-month-dropdown>
                  <span class="mx-2">/</span>
                  <input-year-dropdown
                    classes="card__exp-year flex-grow-1"
                    v-model="orderInfo.cardExpYear"
                    :error="error.cardExpYear"
                    placeholder="YYYY"
                    autocomplete="off"
                  ></input-year-dropdown>
                </div>
              </div>
              <div class="form-group col-12 col-sm-3">
                <div class="d-flex align-items-center">
                  <label class="mr-1">Card Code</label>
                  <button
                    class="btn btn-sm btn-link p-0 d-inline mb-2"
                    style="height: 20px; border: 0; line-height: 1"
                    @click="$refs.cardCodeModal.show()"
                  >
                    <img src="./../assets/HelpBubble.png" />
                  </button>
                </div>
                <div>
                  <input
                    :class="['form-control', error.cardCvc ? 'is-invalid' : '']"
                    class="form-control"
                    v-model="orderInfo.cardCvc"
                    v-mask="'###'"
                    autocomplete="off"
                    placeholder="CVC"
                  />
                </div>
              </div>
            </div>
            <div class="mt-3">
              <div class="row">
                <div class="col-8">
                  <span class="mr-1"> One-Time Enrollment Fee </span>
                  <span
                    v-b-popover.hover.bottom="
                      'This is our one-time fee for preparing your payment plan application. There are no other charges, ever, from 97tax. Your normal monthly payments will go directly from you to the IRS to pay off your debt.'
                    "
                  >
                    <img src="./../assets/HelpBubble.png" />
                  </span>
                </div>
                <div class="col-4">
                  <p class="float-right">${{productPrice.toFixed(2)}}</p>
                </div>
              </div>
              <div class="row">
                <div class="col-8">Subtotal</div>
                <div class="col-4">
                  <p class="float-right">${{productPrice.toFixed(2)}}</p>
                </div>
              </div>
              <div class="row" v-if="discount">
                <div class="col-8">Discount</div>
                <div class="col-4">
                  <p class="float-right">-${{ discount.toFixed(2) }}</p>
                </div>
              </div>
              <div class="row">
                <div class="col-8">Total</div>
                <div class="col-4">
                  <p class="float-right">${{ (productPrice - discount).toFixed(2) }}</p>
                </div>
              </div>
            </div>
            <div v-for="(message, i) in errorMessages" :key="i">
              <b-alert
                variant="danger"
                dismissible
                :show="message.show"
                @dismissed="message.show = false"
                v-html="message.text"
              >
              </b-alert>
            </div>
          </div>
        </div>
      </div>
      <div
        v-if="hasErrors"
        class="mt-2 d-flex justify-content-center text-danger"
      >
        A box on this page is not filled in correctly. Check that all boxes on
        this page are complete.
      </div>
      <div class="mt-2">
        <div class="text-center" v-if="isProcessing">
          <font-awesome-icon :icon="['fa', 'cog']" spin size="3x" />
        </div>
        <div v-if="!isProcessing">
          <button
            class="btn btn-primary btn-green w-100 font-weight-bold"
            v-on:click="updatePaymentPlanDetails()"
          >
            PROCESS PAYMENT &nbsp;&gt;&gt;
          </button>
        </div>
      </div>
      <div class="mt-3">
        <p style="color: #888">
          The ${{ (productPrice - discount).toFixed(2) }} will be processed now on the
          card above. You will receive an email with USPS tracking information
          within 1 business day when your application is complete. Your normal
          monthly payments go directly from you to the Division of Taxation to
          pay off your tax balance. Personal data is never shared or sold and is
          fully encrypted and secure in accordance with our Privacy Policy.
        </p>
      </div>
    </div>

    <b-modal ref="cardCodeModal" title="Card Code" ok-only>
      <p class="my-4">
        The "Card Code" is the 3 digit number on the back of your credit card
        like in the picture below.
      </p>
      <img src="@/assets/whatsthis.jpg" style="max-width: 100%" ok-only />
    </b-modal>
    <PaymentConfirm
      :amount="(productPrice - discount).toFixed(2)"
      v-model="confirmPaymentModal"
      @continuePayment="continuePayment"
      @cancelPayment="cancelPayment"
    ></PaymentConfirm>
    <SalesWidget :phone_number="salesPhoneNumber" v-if="salesPhoneNumber" />
  </page>
</template>

<!-- sharea sale tag -->
<script>
var shareasaleSSCID = shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e, a, r, s, t) {
  if (e && a) {
    var o,
      n = s ? "; path=" + s : "",
      i = t ? "; domain=" + t : "",
      l = "";
    r &&
      ((o = new Date()).setTime(o.getTime() + r),
      (l = "; expires=" + o.toUTCString())),
      (document.cookie = e + "=" + a + l + n + i);
  }
}
function shareasaleGetParameterByName(e, a) {
  a || (a = window.location.href), (e = e.replace(/[\[\]]/g, "\\$&"));
  var r = new RegExp("[?&]" + e + "(=([^&#]*)|&|#|$)").exec(a);
  return r ? (r[2] ? decodeURIComponent(r[2].replace(/\+/g, " ")) : "") : null;
}
shareasaleSSCID &&
  shareasaleSetCookie("shareasaleSSCID", shareasaleSSCID, 94670778e4, "/");
</script>

<script>
import { AXIOS } from "../scripts/http-common";
import { SiteUtils } from "../scripts/site-common";
import VueGoogleAutocomplete from "vue-google-autocomplete";
import { paymentPlansConfig } from "../../config/plans-config";
import PaymentConfirm from "./PaymentConfirm.vue";
import SalesWidget from "./../components/SalesWidget.vue";
import moment from "moment";

export default {
  components: { VueGoogleAutocomplete, PaymentConfirm, SalesWidget },
  data() {
    return {
      salesPhoneNumber: null,
      confirmPaymentModal: false,
      shippingAddressSame: true,
      isProcessing: false,
      showPaymentMessageInPage: false,
      jwtData: null,
      addressType: false,
      showGoogleTextBox: true,
      showBusinessAddressGoogle: true,
      orderNumber: "",
      discount: 0,
      paymentAmount: 0,
      productPrice: 0,
      totalDebtPrevValue: "",
      paymentPlanConfig: paymentPlansConfig.newJerseyPaymentPlan,
      maxPaymentMonth: paymentPlansConfig.newJerseyPaymentPlan.maxMonth,
      orderInfo: {
        billingFirstName: "",
        billingLastName: "",
        billingPhone: "",
        shippingFirstName: "",
        shippingLastName: "",
        shippingPhone: "",
        email: "",
        isOwedFromBusiness: false,
        businessName: "",
        ein: "",
        billingAddress1: "",
        billingAddress2: "",
        billingCity: "",
        billingState: this.$cookie.get("state"),
        billingPostalCode: "",

        shippingAddress1: "",
        shippingAddress2: "",
        shippingCity: "",
        shippingState: this.$cookie.get("state"),
        shippingPostalCode: "",

        businessAddress1: "",
        businessAddress2: "",
        businessCity: "",
        businessState: this.$cookie.get("state"),
        businessPostalCode: "",
        physNotMailing: true,

        amount: "9700",
        totalDebt: "",
        isNewJersey: true,
        isCalifornia: false,
        isGeorgia: false,
        isIllinois: false,
        paymentMonths: paymentPlansConfig.newJerseyPaymentPlan.maxMonth,
        ssn: "",
        secondaryPhone: "",
        married: false,
        spouseFirstName: "",
        spouseLastName: "",
        spouseSsn: "",
        timeToCall: "",
        paymentDayOfMonth: "14",

        partnerCode: "",

        cardNumber: "",
        cardExpMonth: "",
        cardExpYear: "",
        cardCvc: "",

        product: "PaymentPlan",
        responseJwt: null,
        failureReason: "",
      },

      error: {
        paymentAmount: "",
        paymentMonths: "",
        billingFirstName: "",
        billingLastName: "",
        billingPhone: "",
        email: "",
        isOwedFromBusiness: "",
        businessName: "",
        ein: "",
        billingAddress1: "",
        billingAddress2: "",
        billingCity: "",
        billingState: "",
        billingPostalCode: "",

        addressType: false,
        shippingAddress1: "",
        shippingAddress2: "",
        shippingCity: "",
        shippingState: "",
        shippingPostalCode: "",

        businessAddress1: "",
        businessAddress2: "",
        businessCity: "",
        businessState: "",
        businessPostalCode: "",

        totalDebt: "",
        ssn: "",
        secondaryPhone: "",
        married: "",
        spouseFirstName: "",
        spouseLastName: "",
        spouseSsn: "",
        timeToCall: "",
        paymentDayOfMonth: "",

        cardNumber: "",
        cardExpMonth: "",
        cardExpYear: "",
        cardCvc: "",
      },
      hasErrors: false,
      orderInfoTemp: {},
      errorMessages: [],
      authorizeResult: {
        acsUrl: "",
        paReq: "",
        termUrl: "",
        md: "",
      },
    };
  },
  methods: {
    initializeSalesWidget() {
      AXIOS.get(`/admin/salesList`).then((res) => {
        if (res.status == 200) {
          let list = res.data;
          let index = list.findIndex((it) => {
            let condition_1 =
              it.onOff === true && it.product == "New Jersey Payment Plan";
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
      AXIOS.get(`/admin/productPrice?product_name=New Jersey Payment Plan`).then((res) => {
        if (res.status == 200) {
          console.log(res);
          this.productPrice = res.data.customEnrollmentFee;
          this.ourFee = res.data.customEnrollmentFee;
          this.customEnrollmentFee = res.data.customEnrollmentFee;
        }
      });
    },
    isLetter(e) {
      if (e.keyCode === 32) return true;

      let char = String.fromCharCode(e.keyCode);
      if (/^[A-Za-z]+$/.test(char)) return true;
      else e.preventDefault();
    },

    onlyNumber($event) {
      const keyCode = $event.keyCode ? $event.keyCode : $event.which;

      if (keyCode < 48 || keyCode > 57) {
        $event.preventDefault();
      }
    },
    standardizeEin(event) {
      SiteUtils.standardizeEin(this.orderInfo.ein);
    },
    validateTotalDebt($event) {
      this.showPaymentMessageInPage = true;
      this.orderInfo.totalDebt = SiteUtils.validateInputNumber($event);
      this.generatePaymentAmount();
    },
    onIsOwedFromBusinessChange(option) {
      this.orderInfo.isOwedFromBusiness = option == "true";
    },
    checkTotalDebtQualification() {
      const paymentPlan = SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths,
        { isNewJersey: true }
      );
      if (!paymentPlan.valid) {
        this.error.totalDebt = "Please input valid number.";
        return false;
      } else if (!paymentPlan.qualifies) {
        this.error.totalDebt =
          "This amount must be between $500 and $75,000.00 to qualify.";
        return false;
      }
      this.error.totalDebt = false;
      this.calculatePaymentMonths();
      this.validatePaymentAmount();
      return true;
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
          self.orderInfoTemp.physNotMailing = !self.orderInfo.physNotMailing;
          AXIOS.post(`/ecommerce/authorize`, self.orderInfoTemp)
            .then((response) => {
              if (response.data.success) {
                let orderForPdf = Object.assign({}, self.orderInfoTemp);
                orderForPdf.orderNumber = self.orderNumber;
                AXIOS.post("/pdforderversion/refreshOrder", orderForPdf)
                  .then((res) => {
                    window.location.href =
                      "/order-confirmation/" + response.data.correlationId;
                  })
                  .catch((error) => {
                    console.error("Pdf Error: ", error);
                    window.location.href =
                      "/order-confirmation/" + response.data.correlationId;
                  });
              } else {
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

    validateForm() {
      let required = [
        "billingFirstName",
        "billingLastName",
        "billingPhone",
        "email",
        "shippingAddress1",
        "shippingCity",
        "shippingState",
        "shippingPostalCode",
        "totalDebt",
        "ssn",
        "married",
        "paymentDayOfMonth",
        "cardNumber",
        "cardExpMonth",
        "cardExpYear",
        "cardCvc",
      ];

      this.hasErrors = SiteUtils.formErrors(
        required,
        this.orderInfo,
        this.error
      );
      this.error.addressType = false;
      this.error.shippingAddress1 = false;
      this.error.shippingAddress2 = false;
      this.error.billingAddress1 = false;
      this.error.billingAddress2 = false;
      this.error.billingFirstName = false;
      this.error.billingLastName = false;
      this.error.shippingCity = false;
      this.error.businessName = false;

      if (this.error.paymentMonths || this.error.paymentAmount) {
        this.hasErrors = true;
      }

      this.hasErrors = this.hasErrors || !this.checkTotalDebtQualification();

      let spouseFields = ["spouseFirstName", "spouseLastName", "spouseSsn"];
      if (this.orderInfo.married) {
        this.hasErrors = SiteUtils.formErrors(
          spouseFields,
          this.orderInfo,
          this.error
        );

        if (!SiteUtils.validateSsn(this.orderInfo.spouseSsn)) {
          this.error.spouseSsn = true;
          this.hasErrors = true;
        }
      } else {
        SiteUtils.clearErrors(spouseFields, this.error);
      }

      let checkShippingPostalCodeErrorMsg = SiteUtils.validateZipCode(
        this.orderInfo.shippingPostalCode
      );
      if (checkShippingPostalCodeErrorMsg) {
        this.error.shippingPostalCode = checkShippingPostalCodeErrorMsg;
        this.hasErrors = true;
      }

      let billingAddressFields = [
        "billingAddress1",
        "billingCity",
        "billingState",
        "billingPostalCode",
      ];
      if (!this.shippingAddressSame) {
        this.hasErrors = SiteUtils.formErrors(
          billingAddressFields,
          this.orderInfo,
          this.error
        );

        if (SiteUtils.validateZipCode(this.orderInfo.billingPostalCode)) {
          this.error.billingPostalCode = true;
          this.hasErrors = true;
        }
      } else {
        SiteUtils.clearErrors(billingAddressFields, this.error);
      }

      let checkBillingFirstNameErrorMsg = SiteUtils.validateFirstName(
        this.orderInfo.billingFirstName
      );
      if (checkBillingFirstNameErrorMsg) {
        this.error.billingFirstName = checkBillingFirstNameErrorMsg;
        this.hasErrors = true;
      }

      let checkBillingLastNameErrorMsg = SiteUtils.validateLastName(
        this.orderInfo.billingLastName
      );
      if (checkBillingLastNameErrorMsg) {
        this.error.billingLastName = checkBillingLastNameErrorMsg;
        this.hasErrors = true;
      }

      if (
        !this.addressType &&
        !SiteUtils.validateAddress(this.orderInfo.shippingAddress1)
      ) {
        this.error.shippingAddress1 = true;
        this.error.addressType = false;
        this.hasErrors = true;
      }

      let checkStressAddressErrorMSG = SiteUtils.validateStreetAddress(
        this.orderInfo.shippingAddress1,
        this.orderInfo.shippingAddress2
      );
      if (!this.addressType && checkStressAddressErrorMSG) {
        this.error.shippingAddress1 = checkStressAddressErrorMSG;
        this.error.addressType = false;
        this.hasErrors = true;
      }

      let checkShippingCity = SiteUtils.validateShippingCity(
        this.orderInfo.shippingCity
      );
      if (checkShippingCity > 0) {
        this.error.shippingCity = checkShippingCity;
        this.hasErrors = true;
      }

      if (
        !SiteUtils.validateAddressType(
          this.addressType,
          this.orderInfo.shippingAddress1
        )
      ) {
        this.error.addressType = true;
        this.error.shippingAddress1 = false;
        this.hasErrors = true;
      }

      //  if (!SiteUtils.validateAptUnit(this.orderInfo.shippingAddress2)) {
      //     this.error.shippingAddress2 = true;
      //     this.hasErrors = true;
      // }

      let checkEmailAddressErrorMsg = SiteUtils.validateEmail(
        this.orderInfo.email
      );
      if (checkEmailAddressErrorMsg) {
        this.error.email = checkEmailAddressErrorMsg;
        this.hasErrors = true;
      }

      if (this.orderInfo.isOwedFromBusiness) {
        if (!SiteUtils.validateBusinessName(this.orderInfo.businessName)) {
          this.error.businessName = true;
          this.hasErrors = true;
        }

        if (this.orderInfo.ein && !SiteUtils.validateEin(this.orderInfo.ein)) {
          this.error.ein = true;
          this.hasErrors = true;
        }

        let businessAddressFields = [
          "businessAddress1",
          "businessCity",
          "businessState",
          "businessPostalCode",
        ];
        if (!this.orderInfo.physNotMailing) {
          this.hasErrors = SiteUtils.formErrors(
            businessAddressFields,
            this.orderInfo,
            this.error
          );

          if (SiteUtils.validateZipCode(this.orderInfo.businessPostalCode)) {
            this.error.businessPostalCode = true;
            this.hasErrors = true;
          }

          const checkStressAddressErrorMSG = SiteUtils.validateStreetAddress(
            this.orderInfo.businessAddress1,
            this.orderInfo.businessAddress2
          );
          if (checkStressAddressErrorMSG) {
            this.error.businessAddress1 = checkStressAddressErrorMSG;
            this.hasErrors = true;
          }

          const checkShippingCity = SiteUtils.validateShippingCity(
            this.orderInfo.businessCity
          );
          if (checkShippingCity > 0) {
            this.error.businessCity = checkShippingCity;
            this.hasErrors = true;
          }

          const checkBusinessPostalCodeErrorMsg = SiteUtils.validateZipCode(
            this.orderInfo.businessPostalCode
          );
          if (checkBusinessPostalCodeErrorMsg) {
            this.error.businessPostalCode = checkBusinessPostalCodeErrorMsg;
            this.hasErrors = true;
          }
        } else {
          SiteUtils.clearErrors(businessAddressFields, this.error);
        }
      }

      let checkPhoneNumberErrorMsg = SiteUtils.validatePhone(
        this.orderInfo.billingPhone
      );
      if (checkPhoneNumberErrorMsg) {
        this.error.billingPhone = checkPhoneNumberErrorMsg;
        this.hasErrors = true;
      }

      if (!SiteUtils.validateSsn(this.orderInfo.ssn)) {
        this.error.ssn = true;
        this.hasErrors = true;
      }

      if (!SiteUtils.validateDollarsCents(this.orderInfo.totalDebt)) {
        this.error.totalDebt = "Please input the field correctly.";
        this.hasErrors = true;
      }

      if (!SiteUtils.validateCardNumber(this.orderInfo.cardNumber)) {
        this.error.cardNumber = true;
        this.hasErrors = true;
      }

      if (!SiteUtils.validateInteger(this.orderInfo.cardCvc)) {
        this.error.cardCvc = true;
        this.hasErrors = true;
      }

      return this.hasErrors;
    },

    decreasePaymentMonths() {
      this.orderInfo.paymentMonths = isNaN(
        parseInt(this.orderInfo.paymentMonths, 10)
      )
        ? this.paymentPlanConfig.minMonth
        : parseInt(this.orderInfo.paymentMonths, 10) - 1;
      this.onPaymentMonthsChange();
    },

    increasePaymentMonths() {
      this.orderInfo.paymentMonths = isNaN(
        parseInt(this.orderInfo.paymentMonths, 10)
      )
        ? this.paymentPlanConfig.minMonth
        : parseInt(this.orderInfo.paymentMonths, 10) + 1;
      this.onPaymentMonthsChange();
    },

    onPaymentMonthsChange() {
      this.validatePaymentMonths();
      this.generatePaymentAmount();
      this.validatePaymentAmount();
    },

    generatePaymentAmount() {
      let paymentPlan = SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths,
        { isNewJersey: true }
      );
      if (paymentPlan.paymentAmount === "N/A") {
        this.paymentAmount = this.paymentPlanConfig.minMonthlyPayment;
      } else {
        this.paymentAmount = Math.ceil(paymentPlan.paymentAmount);
      }
    },

    onPaymentAmountChange() {
      this.validatePaymentAmount();
      if (!this.error.paymentAmount) {
        this.generatePaymentMonths();
      }
    },

    generatePaymentMonths() {
      this.orderInfo.paymentMonths = SiteUtils.calculatePaymentMonths(
        this.orderInfo.totalDebt,
        parseFloat(this.paymentAmount)
      );
      this.validatePaymentMonths();
    },
    validateInputPaymentAmount($event) {
      this.paymentAmount = SiteUtils.validateInputNumber($event);
      this.validatePaymentAmount();
    },
    validatePaymentAmount() {
      let minimumMonthlyPayment =
        parseFloat(SiteUtils.parseTotalDebit(this.orderInfo.totalDebt)) /
        this.maxPaymentMonth;
      minimumMonthlyPayment = Number(minimumMonthlyPayment.toFixed(2));
      let currentPaymentAmount = isNaN(parseFloat(this.paymentAmount))
        ? 0
        : parseFloat(this.paymentAmount);
      if (currentPaymentAmount < this.paymentPlanConfig.minMonthlyPayment) {
        this.error.paymentAmount = `Minimum payment must be $${this.paymentPlanConfig.minMonthlyPayment}`;
      } else if (
        currentPaymentAmount < minimumMonthlyPayment &&
        minimumMonthlyPayment >= this.paymentPlanConfig.minMonthlyPayment
      ) {
        // this.error.paymentAmount = `Minimum payment must be $${minimumMonthlyPayment}`;
        this.error.paymentAmount = true;
      } else {
        this.error.paymentAmount = false;
      }
    },

    validatePaymentMonths() {
      if (
        parseInt(this.orderInfo.paymentMonths, 10) <
          this.paymentPlanConfig.minMonth ||
        parseInt(this.orderInfo.paymentMonths, 10) > this.maxPaymentMonth
      ) {
        this.error.paymentMonths = true;
      } else {
        this.error.paymentMonths = false;
      }
    },
    calculatePaymentMonths() {
      let totalDebt = SiteUtils.parseTotalDebit(this.orderInfo.totalDebt);
      let maxPaymentMonth = Math.floor(
        totalDebt / this.paymentPlanConfig.minMonthlyPayment
      );
      this.maxPaymentMonth =
        maxPaymentMonth >= this.paymentPlanConfig.maxMonth
          ? this.paymentPlanConfig.maxMonth
          : maxPaymentMonth;
      let tempMonthPayment =
        totalDebt / parseInt(this.orderInfo.paymentMonths, 10);
      if (
        tempMonthPayment < this.paymentPlanConfig.minMonthlyPayment &&
        tempMonthPayment !== 0
      ) {
        this.orderInfo.paymentMonths = maxPaymentMonth;
      }
      this.generatePaymentAmount();
    },
    checkPartnerCode() {
      if (!this.orderInfo.partnerCode) {
        this.discount = 0;
        this.orderInfo.amount = ((this.productPrice - this.discount) * 100).toString();
      } else {
        this.isProcessing = true;
        AXIOS.get(`/paymentplan/partnercodes/${this.orderInfo.partnerCode}`)
          .then((res) => {
            if (res.data && !Number.isNaN(res.data.percentageOff)) {
              this.discount = parseFloat(
                (res.data.percentageOff * 0.97).toFixed(2)
              );
            } else {
              this.discount = 0;
            }
          })
          .catch((err) => {
            this.discount = 0;
          })
          .finally(() => {
            this.isProcessing = false;
            this.orderInfo.amount = ((productPrice - this.discount) * 100).toString();
          });
      }
    },
    postFailureReason() {
      this.orderInfoTemp.physNotMailing = !this.orderInfo.physNotMailing;
      AXIOS.put(`/ecommerce/fail`, this.orderInfoTemp)
        .then((response) => {})
        .catch((error) => {
          console.log(error);
        });
    },
    updatePaymentPlanDetails() {
      this.hasErrors = this.validateForm();
      if (this.hasErrors) {
        return false;
      }

      this.confirmPaymentModal = true;
    },
    cancelPayment() {
      this.isProcessing = true;
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
      this.orderInfoTemp.physNotMailing = !this.orderInfo.physNotMailing;

      this.isProcessing = false;
      this.orderInfoTemp.failureReason = "Payment Confirmation Declined";
      this.postFailureReason();
    },
    continuePayment() {
      this.confirmPaymentModal = false;

      this.isProcessing = true;
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
      this.orderInfoTemp.physNotMailing = !this.orderInfo.physNotMailing;

      AXIOS.put(`/ecommerce/presave`, this.orderInfoTemp)
        .then((res) => {
          let self = this;

          AXIOS.put(`/paymentplan`, {
            secondaryPhone: this.orderInfo.secondaryPhone,
            married: this.orderInfo.married,
            timeToCall: this.orderInfo.timeToCall,
            paymentDayOfMonth: this.orderInfo.paymentDayOfMonth,
            spouseFirstName: this.orderInfo.spouseFirstName,
            spouseLastName: this.orderInfo.spouseLastName,
            spouseSsn: this.orderInfo.spouseSsn,
            totalDebt: this.orderInfo.totalDebt,
            isCalifornia: this.orderInfo.isCalifornia,
            isGeorgia: this.orderInfo.isGeorgia,
            isIllinois: this.orderInfo.isIllinois,
            isNewJersey: this.orderInfo.isNewJersey,
            paymentMonths: this.orderInfo.paymentMonths,
          })
            .then(() => self.postOrderInfo())
            .catch((e) => {
              this.isProcessing = false;
              this.initializeCardinal();
              if (e.response.status === 409) {
                this.orderInfo.failureReason = "Chargeback Order Prevented";
                this.errorMessages.push({
                  show: true,
                  text: "Sorry, it looks like you may have already placed an order with us! Please <a href='/contact-us'>contact us</a> for help.",
                });
              } else {
                this.orderInfo.failureReason = "Failed in paymentplan API call";
                alert(
                  "Duplicated order or unexpected error has occurred. Please try again later."
                );
              }
              this.orderInfoTemp = SiteUtils.updateAddress(
                this.orderInfoTemp,
                this.orderInfo,
                this.addressType,
                this.shippingAddressSame
              );
              this.postFailureReason();
            });
        })
        .catch((error) => {
          console.log(error);
          this.isProcessing = false;
          alert(
            "Duplicated order or unexpected error has occurred. Please try again later."
          );
          this.orderInfoTemp.failureReason =
            "Failed in presave API call. Error: " + error;
          this.postFailureReason();
        });
    },
    postOrderInfo() {
      this.checkPartnerCode();

      this.errorMessages = [];
      let orderInfo = Object.assign({}, this.orderInfo);
      this.isProcessing = true;

      this.orderInfo.billingPhone = SiteUtils.standardizePhone(
        this.orderInfo.billingPhone
      );
      this.orderInfo.ssn = SiteUtils.standardizeSsn(this.orderInfo.ssn);
      this.orderInfo.spouseSsn = SiteUtils.standardizeSsn(
        this.orderInfo.spouseSsn
      );
      this.orderInfo.ein = SiteUtils.standardizeEin(this.orderInfo.ein);

      if (this.shippingAddressSame) {
        this.orderInfo.billingAddress1 = this.orderInfo.shippingAddress1;
        this.orderInfo.billingAddress2 = this.orderInfo.shippingAddress2;
        this.orderInfo.billingCity = this.orderInfo.shippingCity;
        this.orderInfo.billingState = this.orderInfo.shippingState;
        this.orderInfo.billingPostalCode = this.orderInfo.shippingPostalCode;
      }

      if (this.orderInfo.physNotMailing) {
        this.orderInfo.businessAddress1 = this.orderInfo.shippingAddress1;
        this.orderInfo.businessAddress2 = this.orderInfo.shippingAddress2;
        this.orderInfo.businessCity = this.orderInfo.shippingCity;
        this.orderInfo.businessState = this.orderInfo.shippingState;
        this.orderInfo.businessPostalCode = this.orderInfo.shippingPostalCode;
      }

      this.orderInfo.shippingFirstName = this.orderInfo.billingFirstName;
      this.orderInfo.shippingLastName = this.orderInfo.billingLastName;
      this.orderInfo.shippingPhone = this.orderInfo.billingPhone;

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

      this.orderInfoTemp = SiteUtils.updateAddress(
        this.orderInfoTemp,
        this.orderInfo,
        this.addressType,
        this.shippingAddressSame
      );
      this.orderInfoTemp.physNotMailing = !this.orderInfo.physNotMailing;
      let self = this;
      AXIOS.post(`/ecommerce/jwt`, this.orderInfoTemp)
        .then((response) => {
          self.$set(self, "jwtData", response.data);
          self.orderNumber = response.data.payload.OrderDetails.OrderNumber;
          Cardinal.setup("init", {
            jwt: self.jwtData.jwt,
          });
        })
        .catch((e) => {
          self.errorMessages.push({
            show: true,
            text: "Unexpected error occurred. Please try again later.",
          });
          this.initializeCardinal();
          self.isProcessing = false;
        });
    },

    getAddressData: function (addressData, placeResultData, id) {
      this.orderInfo.shippingAddress1 = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;
      this.orderInfo.shippingCity = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;
      this.orderInfo.shippingState = addressData.administrative_area_level_1
        ? addressData.administrative_area_level_1
        : "";
      this.orderInfo.billingAddress1 = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;
      this.orderInfo.billingCity = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;
      this.orderInfo.billingState = addressData.administrative_area_level_1
        ? addressData.administrative_area_level_1
        : "";
      this.orderInfo.shippingPostalCode = addressData.postal_code
        ? addressData.postal_code
        : "";
      this.orderInfo.billingPostalCode = addressData.postal_code
        ? addressData.postal_code
        : "";
      if (this.orderInfo.physNotMailing) {
        this.getBusinessAddressData(addressData, placeResultData, id);
      }
      this.showGoogleTextBox = false;
    },
    getBusinessAddressData(addressData, placeResultData, id) {
      this.orderInfo.businessAddress1 = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;
      this.orderInfo.businessCity = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;
      this.orderInfo.businessState = addressData.administrative_area_level_1
        ? addressData.administrative_area_level_1
        : "";
      this.orderInfo.businessPostalCode = addressData.postal_code
        ? addressData.postal_code
        : "";
      this.showBusinessAddressGoogle = false;
    },
    extractBusinessAddress() {
      google.maps.places.Autocomplete(
        this.getBusinessAddressData,
        "place_changed"
      );
    },
    changeToBusinessAddressGoogle() {
      this.showBusinessAddressGoogle = true;
    },
    extractAddress: function () {
      google.maps.places.Autocomplete(this.getAddressData, "place_changed");
      this.showGoogleTextBox = false;
    },
    changeToGoogle: function () {
      if (!this.addressType) {
        this.showGoogleTextBox = true;
      }
    },
    changeToshortAddress: function () {
      this.showGoogleTextBox = false;
    },
    setAddress: function (address, type) {
      this.orderInfo.shippingAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
      this.orderInfo.billingAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
    },
    setBusinessAddress: function (address, type) {
      this.orderInfo.businessAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
    },
  },
  computed: {
    paymentPlan: function () {
      return SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths,
        { isNewJersey: true }
      );
    },
    amountSelectedMobile: function () {
      let amount = this.$cookie.get("amount");
      if (amount !== undefined) {
        return amount;
      }
      return "$00.00";
    },
    paymentAmountMobile: function () {
      let amount = this.$cookie.get("amount");
      if (
        amount &&
        ((this.orderInfo &&
          this.orderInfo.totalDebt &&
          this.orderInfo.totalDebt.length <= 0) ||
          (this.orderInfo && !this.orderInfo.totalDebt))
      ) {
        amount = amount.replace(/\$/g, "");
        amount = amount.replace(/,/g, "");
        amount = amount.replace(/ /g, "");
        amount = amount.replace("+", "");
        let amounts = amount.split("-");
        amount = SiteUtils.calculatePaymentPlan(
          amounts[0],
          this.orderInfo.paymentMonths,
          { isNewJersey: true }
        ).paymentAmount;
        if (amounts.length > 1) {
          amount =
            "$" +
            amount +
            " - $" +
            SiteUtils.calculatePaymentPlan(
              amounts[1],
              this.orderInfo.paymentMonths,
              { isNewJersey: true }
            ).paymentAmount;
        }

        return amount;
      } else {
        return "$" + this.paymentAmount;
      }

      return "...";
    },
    paymentMessage: function () {
      let paymentPlan = SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths,
        { isNewJersey: true }
      );
      if (paymentPlan.paymentAmount === "N/A") {
        return "If your debt is between $500 and $50,000 then you will qualify.";
      }
      if (paymentPlan.paymentAmount < 102) {
        return "You qualify for a $25 a month payment plan with the IRS, but your first payment will be $102.";
      } else {
        return `You qualify for a $${paymentPlan.paymentAmount} a month payment plan with the IRS.`;
      }
    },
  },
  mounted() {
    this.initializeCardinal();
    this.initializePricings();
    this.initializeSalesWidget();

    window.ANS_customer_id = "d7332b5b-b0c5-4072-9237-bef8a02352ae";
    let self = this;
    AXIOS.get(`/ecommerce/order`)
      .then((response) => {
        this.orderInfo.id = response.data.orderNum;
        this.orderInfo.billingFirstName = response.data.firstName;
        this.orderInfo.billingLastName = response.data.lastName;
        this.orderInfo.billingPhone = response.data.phone;
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

        this.orderInfo.totalDebt = response.data.totalDebt;
        this.totalDebtPrevValue = this.orderInfo.totalDebt;
        this.orderInfo.paymentMonths =
          response.data.paymentMonths || this.paymentPlanConfig.maxMonth;
        this.calculatePaymentMonths();
        //  this.generatePaymentAmount();

        let state = self.$cookie.get("state");
        if (state !== undefined) {
          self.orderInfo.billingState = state;
          self.orderInfo.shippingState = state;
          self.$cookie.delete("state");
        }
      })
      .catch((e) => {
        self.errorMessages.push({
          show: true,
          text: "Unexpected error occurred. Please try again later.",
        });
        self.isProcessing = false;
      });
  },
};
</script>
<style scoped>
.container {
  max-width: 876px;
}

.billing-form,
.mailing-form,
.payment-form,
.application-details-form,
.card-form {
  background-color: #f5f5f5;
  padding: 20px 30px;
}

.green-text {
  color: #2cca2a !important;
}

.payment-amount-sign {
  font-size: 28px;
  margin-left: -22px;
}

.payment-amount {
  width: 45%;
  background-color: white;
  font-size: 20px;
  height: 38px;
}

.payment-amount.is-invalid.form-control {
  padding-right: 0.75rem;
  background-image: none;
}

.btn-months-adjust {
  line-height: 1;
  font-size: 28px;
  font-weight: 600;
  width: 45px;
}

.months {
  width: 50px;
}

.months.is-invalid.form-control {
  padding-right: 0.75rem;
  background-image: none;
}

.card-form-header {
  position: relative;
}

.partner-code-block {
  position: absolute;
  left: 0;
  top: -10px;
}

.card__partner-code {
  width: 90px;
}

.card__exp-month .form-control.is-invalid,
.card__exp-year .form-control.is-invalid {
  padding-right: 0.75rem;
}

.card-imgs {
  position: absolute;
  right: 0;
  top: -5px;
}

.debt-label {
  margin-left: 15px;
}
.decimalWholePart {
  /* width: 94%; */
  margin-top: 5px;
  margin-bottom: 10px;
  /* margin-left:15px; */
}
.btn-primary {
  background-color: #2896c5;
  border: 0px;
}

.blue {
  color: #2896c5;
}
.aside-item h2 {
  font-size: 3em;
  font-family: sans-serif;
}
.aside-item p {
  font-size: 1.5em;
  font-family: sans-serif;
}
p {
  font-family: sans-serif;
}

.bg-grey {
  background-color: #f0f0f0;
}

.po-box {
  width: 77%;
  margin-top: 5%;
  margin-left: -4%;
}

@media (max-width: 576px) {
  .mailing {
    padding-left: 6%;
  }
  .po-box {
    width: 66%;
    margin-top: 5%;
    margin-left: 2%;
  }
  .partner-code-block {
    position: static;
    justify-content: center;
    margin-top: 10px;
  }
  .card-imgs {
    position: static;
    text-align: center;
    margin-top: 10px;
  }
  .card-logos {
    margin-left: -10px !important;
    margin-right: -10px !important;
  }
}
@media (min-width: 576px) {
  .col-sm-6 {
    -ms-flex: 0 0 50%;
    flex: 0 0 50%;
    max-width: 100% !important;
  }
}
</style>
