<template>
  <page :hideTopNav="true" :navType="'illinois-payment-plan-order-form'">
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
          Illinois
        </h2>
        <p class="mt-3">First, let's gather your billing details.</p>
      </div>
      <div class="billing-form">
        <div class="border-bottom border-secondary border-3 mb-3">
          <h4 class="font-weight-bolder">Billing Details</h4>
        </div>
        <div class="row">
          <div class="form-group col-12 col-sm-5">
            <label>First Name</label>
            <input
              classes="billing__first-name"
              v-model="orderInfo.billingFirstName"
              :class="[
                'form-control',
                error.billingFirstName ? 'is-invalid' : '',
              ]"
              :error="error.billingFirstName"
              :placeholder="'First Name'"
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
          <div class="form-group col-12 col-sm-5">
            <label>Last Name</label>
            <input
              classes="billing__last-name"
              :class="[
                'form-control',
                error.billingLastName ? 'is-invalid' : '',
              ]"
              v-model="orderInfo.billingLastName"
              :error="error.billingLastName"
              :placeholder="'Last Name'"
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
          <div class="form-group col-12 col-sm-5">
            <form-input-field
              v-model="orderInfo.email"
              classes="billing__email"
              :error="error.email"
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
          <div class="form-group col-12 col-sm-3">
            <form-input-field
              v-model="orderInfo.billingPhone"
              classes="billing__phoneNumber"
              :error="error.billingPhone"
              label="Phone Number"
              placeholder="123-45-6789"
              :mask="'###-###-####'"
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
          <div class="form-group col-12 col-sm-4">
            <form-input-field
              v-model="orderInfo.mobile"
              :error="error.mobile"
              classes="billing__phoneNumber"
              label="Alternate Number (Optional)"
              :mask="'###-###-####'"
              placeholder="123-456-7890"
              :name="'secondaryPhone'"
              type="tel"
            ></form-input-field>
          </div>
        </div>
      </div>
      <p class="my-3">
        Your application will go to the mailing address below with tracking.
        Illinois requires your physical signature:
      </p>
      <div class="mailing-form">
        <div class="border-bottom border-secondary border-3 mb-3">
          <h4 class="font-weight-bolder">Mailing Address</h4>
        </div>
        <div class="row align-items-end">
          <div class="form-group col-12 col-sm-5">
            <label>Address (No PO Box Allowed)</label>
            <vue-google-autocomplete
              id="address"
              :class="[
                'form-control',
                error.shippingAddress1 ? 'is-invalid' : '',
              ]"
              v-show="showGoogleTextBox"
              placeholder="Street"
              @placechanged="getAddressData"
              @blur="extractAddress"
              @inputChange="setAddress"
              country="us"
            >
            </vue-google-autocomplete>
            <input
              :class="[
                'form-control',
                error.shippingAddress1 ? 'is-invalid' : '',
              ]"
              v-show="!showGoogleTextBox"
              v-model="orderInfo.shippingAddress1"
              :error="error.shippingAddress1"
              @focusin="changeToGoogle"
            />
          </div>
          <div class="form-group col-12 col-sm-3">
            <form-input-field
              classes="mailing__address2"
              maxlength="12"
              v-model="orderInfo.shippingAddress2"
              :error="error.shippingAddress2"
              label="Apt/Unit"
            ></form-input-field>
          </div>
          <div class="col-sm-4"></div>
          <div class="col-12 col-sm-5">
            <span
              v-if="
                error.shippingAddress1 && !error.addressType && !addressType
              "
              class="w-100 text-danger"
            >
              {{ error.shippingAddress1 }}
            </span>
            <span
              v-if="error.addressType && !error.shippingAddress1 && addressType"
              class="w-100 text-danger"
            >
              {{ error.shippingAddress1 }}
            </span>
          </div>
          <div class="col-12 col-sm-3">
            <span v-if="error.shippingAddress2" class="text-danger">
              This box is for your APT/UNIT/STE number only.
            </span>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-12 col-sm-5">
            <label>City</label>
            <input
              :placeholder="'City'"
              classes="mailing__city"
              :class="['form-control', error.shippingCity ? 'is-invalid' : '']"
              v-model="orderInfo.shippingCity"
              :error="error.shippingCity"
              v-on:keypress="isLetter($event)"
            />
          </div>
          <div class="form-group col-12 col-sm-3">
            <state-select-field
              v-model="orderInfo.shippingState"
              classes="state_select_field"
              :error="error.shippingState"
              label="State"
            ></state-select-field>
          </div>
          <div class="form-group col-12 col-sm-3">
            <label>Zip Code</label>
            <input
              v-model="orderInfo.shippingPostalCode"
              :maxlength="5"
              :error="error.shippingPostalCode"
              :placeholder="'Zip Code'"
              type="tel"
              v-mask="'#####'"
              :class="[
                'form-control',
                error.shippingPostalCode ? 'is-invalid' : '',
              ]"
            />
          </div>
          <div class="col-sm-1"></div>
          <div class="col-12 col-sm-5">
            <span v-if="error.shippingCity" class="text-danger">
              This box is for your City.
            </span>
          </div>
          <div class="col-12 col-sm-3">
            <span v-if="error.shippingState" class="text-danger mt-1">
              This box is for your State.
            </span>
          </div>
          <div class="col-12 col-sm-3">
            <span v-if="error.shippingPostalCode" class="text-danger mt-1">
              {{ error.shippingPostalCode }}
            </span>
          </div>
          <div class="col-12 mailing col-sm-6">
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

        <div class="row" v-if="!shippingAddressSame">
          <form-input-field
            v-model="orderInfo.billingAddress1"
            :error="error.billingAddress1"
            labelClass="font-size-small"
            label="Address Line 1"
          ></form-input-field>
          <form-input-field
            v-model="orderInfo.billingAddress2"
            :error="error.billingAddress2"
            label="Address Line 2"
            labelClass="font-size-small"
          ></form-input-field>
          <form-input-field
            v-model="orderInfo.billingCity"
            :error="error.billingCity"
            label="City"
            labelClass="font-size-small"
          ></form-input-field>
          <state-select-field
            v-model="orderInfo.billingState"
            :error="error.billingState"
            label="State"
            labelClass="font-size-small"
          ></state-select-field>
          <form-input-field
            v-model="orderInfo.billingPostalCode"
            :maxlength="5"
            :error="error.billingPostalCode"
            labelClass="font-size-small"
            label="Zip Code"
            v-mask="'#####'"
            type="tel"
          ></form-input-field>
        </div>
      </div>

      <p class="my-3 d-flex">
        Illinois asks for a down payment with your application (not due today).
        Make it as large as possible to limit interest.
        <span
          v-b-popover.hover.bottom="
            'The state asks for the largest down payment you are able to make. We recommend at least 5% of the amount you owe, but it is not required. This amount is not due today, but you’ll include a check or money order for this amount when you sign and mail the application.'
          "
        >
          <img style="margin-top: -1rem" src="./../assets/HelpBubble.png" />
        </span>
      </p>
      <div class="payment-form">
        <div class="border-bottom border-secondary border-3 mb-3">
          <h4 class="font-weight-bolder">Down Payment</h4>
        </div>
        <div class="row">
          <div class="col-12">
            <h6>
              The State of Illinois requires a Good Faith Down payment. Please
              enter the amount you'd like to pay.
            </h6>
            <h6>We've calculated the minimum recommended amount.</h6>
          </div>
        </div>

        <div class="row">
          <div class="col-12 col-sm-4 mt-sm-3 pt-sm-2">
            <label> Total Amount Owed:</label>
          </div>
          <div class="col-12 col-sm-3 mt-sm-3 pt-sm-2">
            <span class="d-flex">
              <span class="mr-2 payment-amount-sign font-weight-bold">$</span>
              <input
                :class="[
                  'form-control payment-amount font-weight-bold',
                  error.totalDebt ? 'is-invalid' : '',
                ]"
                :error="!!error.totalDebt"
                class="decimalWholePart"
                v-model="orderInfo.totalDebt"
                v-on:input="validateTotalDebt"
                placeholder="0"
                inputmode="decimal"
                ref="target"
                @blur="checkTotalDebtQualification"
                type="text"
              />
            </span>
          </div>
          <div
            class="text-danger col-12 col-sm-5 mt-sm-3 pt-sm-2"
            v-if="error.totalDebt"
          >
            {{ error.totalDebt }}
          </div>
        </div>

        <div class="row">
          <div class="col-12 col-sm-4">
            <label class="d-flex">
              Good Faith Down Payment:
              <span
                class="business-selector-help"
                v-b-popover.hover.bottom="
                  'The state asks for the largest down payment you are able to make. We recommend at least 5% of the amount you owe, but it is not required. This amount is not due today, but you’ll include a check or money order for this amount when you sign and mail the application.'
                "
              >
                <img src="./../assets/HelpBubble.png" />
              </span>
            </label>
          </div>
          <div class="col-12 col-sm-3">
            <span class="d-flex">
              <span class="mr-2 payment-amount-sign font-weight-bold">$</span>
              <input
                :class="[
                  'form-control green-text payment-amount font-weight-bold',
                  error.goodFaithPayment ? 'is-invalid' : '',
                ]"
                :error="!!error.goodFaithPayment"
                class="decimalWholePart"
                v-on:input="validateGoodFaithPayment"
                v-model="orderInfo.goodFaithPayment"
                placeholder="0"
                inputmode="decimal"
                ref="goodFaithPayment"
                @blur="checkGoodFaithQualification"
                type="text"
              />
            </span>
          </div>
          <div
            class="text-danger col-12 col-sm-5"
            v-if="error.goodFaithPayment"
          >
            {{ error.goodFaithPayment }}
          </div>
          <div v-else class="col-12 col-sm-5">
            <p>5% of Total Amount Owed</p>
          </div>
        </div>

        <div class="row">
          <div class="col-12 col-sm-4">
            <label class="d-flex"
              >Remaining Balance:
              <span
                class="business-selector-help"
                v-b-popover.hover.bottom="
                  'Your monthly payments are based on your remaining balance. It is the total amount you owe subtracted by your good faith down payment.'
                "
              >
                <img src="./../assets/HelpBubble.png" />
              </span>
            </label>
          </div>
          <div class="col-12 col-sm-3">
            <span class="d-flex">
              <span class="mr-2 payment-amount-sign font-weight-bold">$</span>
              <input
                :class="[
                  'form-control remaining-balance text-danger font-weight-bold',
                ]"
                class="form-control decimalWholePart"
                placeholder="0"
                v-model="remainingBalance"
                disabled
              />
            </span>
          </div>
        </div>
        <div class="row">
          <div class="col-12 mt-sm-3">
            <h6>
              Note: The Good Faith Down Payment is not due today. You will pay
              that amount directly to the
            </h6>
            <h6>State of Illinois when you sign and mail the application.</h6>
          </div>
        </div>
      </div>
      <p class="my-3">
        You may adjust your monthly payments below. The payments are calculated
        at the minimum to start.
      </p>
      <div class="payment-form">
        <div class="border-bottom border-secondary border-3 mb-3">
          <h4 class="font-weight-bolder">Monthly Payment</h4>
        </div>

        <div class="row">
          <div class="col-12 col-sm-5 d-flex justify-content-between pr-1">
            <label> Choose the duration of your payments:</label>
            <span class="d-none d-sm-block"><i class="arrow left"></i></span>
          </div>
          <div class="col-12 col-sm-5 pl-0 pr-0">
            <input
              type="range"
              :min="3"
              :max="maxPaymentMonth"
              class="slider"
              v-model="orderInfo.paymentMonths"
              step="1"
              v-on:input="onPaymentMonthsChange"
            />
          </div>
          <div class="col-12 col-sm-2 d-flex pl-1">
            <span class="d-none d-sm-block mr-3"
              ><i class="arrow right"></i
            ></span>
            <label class="font-weight-bold"
              >{{ orderInfo.paymentMonths }} months</label
            >
          </div>
        </div>

        <div class="row">
          <div class="col-12 col-sm-5 mt-sm-2 pt-sm-2">
            <label> Your monthly payment will be: </label>
          </div>
          <div class="col-12 col-sm-3 mt-sm-2 pt-sm-2">
            <span class="d-flex">
              <span class="mr-2 payment-amount-sign font-weight-bold">$</span>
              <input
                class="form-control payment-amount text-info font-weight-bold"
                :class="error.paymentAmount ? 'is-invalid' : ''"
                type="tel"
                v-model="paymentAmount"
                placeholder="0"
                :error="error.paymentAmount"
                v-on:input="validateInputPaymentAmount"
                v-on:blur="onPaymentAmountChange"
              />
            </span>
          </div>
          <div class="col-12 col-sm-4 mt-sm-2 pt-sm-2">
            <div
              v-if="error.paymentAmount && error.paymentAmount != true"
              class="text-danger mt-1"
            >
              {{ error.paymentAmount }}
            </div>
          </div>
        </div>

        <div class="row">
          <div class="col-12 col-sm-5 mt-sm-2 pt-sm-2">
            <label class="d-flex w-max-content">
              Day of Month to Make Payments
              <span
                class="business-selector-help"
                v-b-popover.hover.bottom="
                  'This is the day of the month you prefer to make your normal monthly payment. It will not necessarily be the day your first payment is due because it takes time for Illinois to approve your payment plan application.'
                "
              >
                <img src="./../assets/HelpBubble.png" />
              </span>
            </label>
          </div>
          <div class="form-group col-12 col-sm-4 mt-sm-2 pt-sm-2">
            <generic-select-field
              v-model="orderInfo.paymentDayOfMonth"
              :error="error.paymentDayOfMonth"
              imageStyle="margin-top:-1rem"
              classes="payment__day__month"
            >
              <option :value="''">Please Select</option>
              <option v-for="index in 28" :value="index" :key="index">
                {{ index }}
              </option>
            </generic-select-field>
          </div>
        </div>
      </div>

      <p class="my-3">
        To prepare your application, we need your Social Security Number or TIN.
        Illinois will use this for identification.
      </p>
      <div class="application-details-form">
        <div class="mb-3">
          <div class="border-bottom border-secondary border-3 mb-3">
            <h4 class="font-weight-bolder">Taxpayer Identification</h4>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-sm-6">
            <div class="row">
              <div class="col-12">
                <label class="d-flex w-max-content">
                  Social Security Number or TIN
                  <span
                    class="business-selector-help"
                    v-b-popover.hover.bottom="
                      'Please input Social Security Number or Taxpayer Identification Number'
                    "
                  >
                    <img src="./../assets/HelpBubble.png" />
                  </span>
                </label>
              </div>
              <div class="form-group col-12">
                <form-input-field
                  v-model="orderInfo.ssn"
                  :error="error.ssn"
                  classes="ssn"
                  placeholder="000-00-0000"
                  :mask="'###-##-####'"
                  :name="'ssn'"
                  type="tel"
                  autocomplete="off"
                ></form-input-field>
              </div>
            </div>

            <div class="row">
              <div class="col-12">
                <label class="d-flex w-max-content">
                  Is this application being prepared for a business?
                  <span
                    class="business-selector-help"
                    v-b-popover.hover.bottom="
                      'Select &quot;Yes&quot; if you own an LLC, Corporation, or Partnership. Select &quot;No&quot; if you are a sole proprietor.'
                    "
                  >
                    <img src="./../assets/HelpBubble.png" />
                  </span>
                </label>
              </div>
              <div class="form-group col-12 amount-owe-selector">
                <select
                  class="form-control"
                  :value="orderInfo.isOwedFromBusiness"
                  @change="onIsOwedFromBusinessChange($event.target.value)"
                >
                  <option :value="true">Yes</option>
                  <option :value="false">No</option>
                </select>
              </div>
            </div>
            <div
              class="row"
              style="margin-top: 1.5rem"
              v-if="orderInfo.isOwedFromBusiness"
            >
              <div class="col-12" style="padding: 0">
                <form-input-field
                  v-model="orderInfo.businessName"
                  :error="error.businessName"
                  :maxlength="36"
                  label="Legal Business Name (Include INC or LLC if applicable)"
                  :placeholder="'Business Name'"
                  :name="'businessName'"
                ></form-input-field>
                <form-input-field
                  v-model="orderInfo.ein"
                  :error="error.ein"
                  label="Business EIN (if issued)"
                  placeholder="00-0000000"
                  :mask="'##-#######'"
                  :name="'ein'"
                  type="tel"
                ></form-input-field>
                <form-input-field
                  v-model="orderInfo.dba"
                  :error="error.dba"
                  label="DBA (leave blank if none)"
                  placeholder="DBA"
                  :name="'dba'"
                ></form-input-field>
                <form-input-field
                  v-model="orderInfo.illinoisAccountId"
                  placeholder="0000-0000"
                  type="tel"
                  :error="error.illinoisAccountId"
                  :maxlength="9"
                  label="Illinois Account ID (if issued)"
                  :mask="'####-####'"
                  autocomplete="off"
                  :name="'accountId'"
                ></form-input-field>
              </div>
            </div>
          </div>
          <div class="col-12 col-sm-6">
            <div class="row">
              <div class="col-12" style="padding: 0">
                <yes-no-select-field
                  v-model="orderInfo.married"
                  :error="error.married"
                  imageStyle="margin-top:-1rem;margin-left:2rem"
                  helpText="If you filed your tax returns, for the years that you owe, jointly with a spouse or resident domestic partner (RDP), answer yes to this question. If you and your spouse/RDP filed separately you can answer 'No' to this question."
                  label="Are You Married AND Filing Jointly?"
                >
                </yes-no-select-field>
              </div>
            </div>
            <div v-if="orderInfo.married === true" class="row">
              <div class="col-12">
                <label>Spouse First Name</label>
                <input
                  class="form-control"
                  v-model="orderInfo.spouseFirstName"
                  :error="error.spouseFirstName"
                  label="Spouse First Name"
                  placeholder=""
                  v-on:keypress="isLetter($event)"
                />

                <div
                  v-if="error.spouseFirstName"
                  class="text-danger"
                  style="margin-top: 2px"
                >
                  {{ error.spouseFirstName }}
                </div>
              </div>
              <div class="col-12 mt-2">
                <label>Spouse Last Name</label>
                <input
                  class="form-control"
                  v-model="orderInfo.spouseLastName"
                  :error="error.spouseLastName"
                  label="Spouse Last Name"
                  placeholder=""
                  v-on:keypress="isLetter($event)"
                />

                <div
                  v-if="error.spouseLastName"
                  class="text-danger"
                  style="margin-top: 2px"
                >
                  {{ error.spouseLastName }}
                </div>
              </div>
              <div class="col-12 mt-2" style="padding: 0">
                <form-input-field
                  v-if="orderInfo.married === true"
                  v-model="orderInfo.spouseSsn"
                  :error="error.spouseSsn"
                  label="Spouse SSN"
                  placeholder="000-00-0000"
                  v-mask="'###-##-####'"
                  autocomplete="off"
                  type="tel"
                  :name="'ssn'"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
      <p class="my-3">
        We charge a one-time fee to prepare your application. No other charges
        are ever applied to this card.
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
                  :mask="'#### #### #### ####'"
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
                      'This is our one-time fee for preparing your payment plan application. There are no other charges, ever, from 97tax. Your normal monthly payments will go directly from you to Illinois to pay off your debt.'
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
      <div class="mt-3 text-center">
        <p style="color: #888">
          The ${{ (productPrice - discount).toFixed(2) }} will be processed now on the
          card above. You will receive an email with USPS tracking information
          within 1 business day when your application is complete. Your normal
          monthly payments go directly from you to the Illinois Department of
          Revenue to pay off your tax balance. Personal data is never shared or
          sold and is fully encrypted and secure in accordance with our Privacy
          Policy.
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
  components: { VueGoogleAutocomplete, PaymentConfirm },
  data() {
    return {
      salesPhoneNumber: null,
      confirmPaymentModal: false,
      shippingAddressSame: true,
      isProcessing: false,
      showPaymentMessageInPage: false,
      addressType: false,
      jwtData: null,
      showGoogleTextBox: true,
      orderNumber: "",
      discount: 0,
      paymentAmount: "",
      productPrice: 0,
      remainingBalance: "",
      paymentPlanConfig: paymentPlansConfig.illinoisPaymentPlan,
      maxPaymentMonth: paymentPlansConfig.illinoisPaymentPlan.maxMonth,
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
        dba: "",
        illinoisAccountId: "",
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

        amount: "9700",
        totalDebt: "",
        isNewJersey: false,
        isCalifornia: false,
        isGeorgia: false,
        isIllinois: true,
        goodFaithPayment: "",
        mobile: "",
        paymentMonths: paymentPlansConfig.irsPaymentPlan.maxMonth,
        ssn: "",
        married: false,
        spouseFirstName: "",
        spouseLastName: "",
        spouseSsn: "",
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
        dba: "",
        illinoisAccountId: "",
        billingAddress1: "",
        billingAddress2: "",
        billingCity: "",
        billingState: "",
        billingPostalCode: "",

        shippingAddress1: "",
        shippingAddress2: "",
        shippingCity: "",
        shippingState: "",
        shippingPostalCode: "",

        totalDebt: "",
        goodFaithPayment: "",
        ssn: "",
        spouseFirstName: "",
        spouseLastName: "",
        spouseSsn: "",
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
              it.onOff === true && it.product == "Illinois Payment Plan";
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
      AXIOS.get(`/admin/productPrice?product_name=Illinois Payment Plan`).then((res) => {
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
    validateGoodFaithPayment($event) {
      this.showPaymentMessageInPage = true;
      this.orderInfo.goodFaithPayment = SiteUtils.validateInputNumber($event);
      this.generateRemainingBalance();
      this.generatePaymentMonths();
    },
    checkTotalDebtQualification() {
      const paymentPlan = SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths,
        { isIllinois: true }
      );
      if (!paymentPlan.valid) {
        this.error.totalDebt = "Please input valid number.";
        return false;
      } else if (!paymentPlan.qualifies) {
        this.error.totalDebt =
          "This amount must be between $500 and $75,000 to qualify.";
        return false;
      }
      this.calculatePaymentMonths();
      this.error.totalDebt = false;
      return true;
    },
    checkGoodFaithQualification() {
      if (!this.orderInfo.goodFaithPayment) {
        this.error.goodFaithPayment = "Please input valid number.";
        return false;
      }
      this.error.goodFaithPayment = false;
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
            false,
            self.shippingAddressSame
          );
          self.orderInfoTemp.id = self.orderInfo.id;

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
                this.orderInfo.failureReason = "Transaction error";
                this.orderInfoTemp = SiteUtils.updateAddress(
                  this.orderInfoTemp,
                  this.orderInfo,
                  false,
                  this.shippingAddressSame
                );
                this.errorMessages.push({
                  show: true,
                  text: "Unable to make purchase. Please check your payment details or try a different payment method.",
                });
                this.isProcessing = false;
                // if (this.orderInfoTemp.failureReason) {
                //   this.postFailureReason();
                // }
              }
            })
            .catch((e) => {
              if (e.response.status === 409) {
                this.orderInfo.failureReason = "Duplicate Order";
                this.orderInfoTemp = SiteUtils.updateAddress(
                  this.orderInfoTemp,
                  this.orderInfo,
                  false,
                  this.shippingAddressSame
                );
                this.errorMessages.push({
                  show: true,
                  text: "Sorry, it looks like you may have already placed an order with us! Please <a href='/contact-us'>contact us</a> for help.",
                });
              } else {
                this.orderInfo.failureReason = "Error: " + e;
                this.orderInfoTemp = SiteUtils.updateAddress(
                  this.orderInfoTemp,
                  this.orderInfo,
                  false,
                  this.shippingAddressSame
                );
                this.errorMessages.push({
                  show: true,
                  text: "Unable to make purchase. Please check your payment details or try a different payment method.",
                });
                this.isProcessing = false;
              }
              if (this.orderInfoTemp.failureReason) {
                this.postFailureReason();
              }
            });
        } else {
          this.orderInfo.failureReason = "3-D Secure verification failure";
          this.orderInfoTemp = SiteUtils.updateAddress(
            this.orderInfoTemp,
            this.orderInfo,
            false,
            this.shippingAddressSame
          );
          this.errorMessages.push({
            show: true,
            text: "3-D Secure verification failed.",
          });
          this.initializeCardinal();
          this.isProcessing = false;

          if (this.orderInfoTemp.failureReason) {
            this.postFailureReason();
          }
        }
      });
    },
    onIsOwedFromBusinessChange(option) {
      this.orderInfo.isOwedFromBusiness = option == "true";
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
      this.error.shippingAddress1 = false;
      this.error.shippingAddress2 = false;
      this.error.billingAddress1 = false;
      this.error.billingAddress2 = false;
      this.error.billingFirstName = false;
      this.error.billingLastName = false;
      this.error.shippingCity = false;

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

        let checkSpouseFirstNameErrorMsg = SiteUtils.validateSpouseFirstName(
          this.orderInfo.spouseFirstName
        );
        if (checkSpouseFirstNameErrorMsg) {
          this.error.spouseFirstName = checkSpouseFirstNameErrorMsg;
          this.hasErrors = true;
        }
        if (
          this.orderInfo.spouseFirstName === this.orderInfo.billingFirstName
        ) {
          this.error.spouseFirstName =
            "Your spouse’s name must be different from your own.";
          this.hasErrors = true;
        }
        let checkSpouseLastNameErrorMsg = SiteUtils.validateSpouseLastName(
          this.orderInfo.spouseLastName
        );
        if (checkSpouseLastNameErrorMsg) {
          this.error.spouseLastName = checkSpouseLastNameErrorMsg;
          this.hasErrors = true;
        }
      } else {
        SiteUtils.clearErrors(spouseFields, this.error);
      }

      let shippingPostalCodeErrorMsg = SiteUtils.validateZipCode(
        this.orderInfo.shippingPostalCode
      );
      if (shippingPostalCodeErrorMsg) {
        this.error.shippingPostalCode = shippingPostalCodeErrorMsg;
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

      let checkShippingCityErrorMsg = SiteUtils.validateShippingCity(
        this.orderInfo.shippingCity
      );
      if (checkShippingCityErrorMsg) {
        this.error.shippingCity = checkShippingCityErrorMsg;
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
      this.orderInfo.paymentMonths =
        parseInt(this.orderInfo.paymentMonths, 10) - 1;
      this.onPaymentMonthsChange();
    },

    increasePaymentMonths() {
      this.orderInfo.paymentMonths =
        parseInt(this.orderInfo.paymentMonths, 10) + 1;
      this.onPaymentMonthsChange();
    },

    onPaymentMonthsChange() {
      this.validatePaymentMonths();
      this.calculatePaymentPlan();
      this.validatePaymentAmount();
      // this.generatePaymentAmount();
    },
    calculatePaymentMonths() {
      let maxPaymentMonth = Math.floor(
        this.remainingBalance / this.paymentPlanConfig.minMonthlyPayment
      );
      this.maxPaymentMonth =
        maxPaymentMonth >= this.paymentPlanConfig.maxMonth
          ? this.paymentPlanConfig.maxMonth
          : maxPaymentMonth;
      let tempMonthPayment =
        this.remainingBalance / parseInt(this.orderInfo.paymentMonths, 10);
      if (
        tempMonthPayment < this.paymentPlanConfig.minMonthlyPayment &&
        tempMonthPayment !== 0
      ) {
        this.orderInfo.paymentMonths = Math.floor(
          this.remainingBalance / this.paymentPlanConfig.minMonthlyPayment
        );
      }
      this.calculatePaymentPlan();
    },
    calculatePaymentPlan() {
      let paymentPlan = SiteUtils.calculatePaymentPlan(
        this.remainingBalance,
        this.orderInfo.paymentMonths,
        { isIllinois: true }
      );
      if (paymentPlan.paymentAmount === "N/A") {
        this.paymentAmount = this.paymentPlanConfig.minMonthlyPayment;
      } else {
        this.paymentAmount = Math.ceil(paymentPlan.paymentAmount);
      }
    },
    generateGoodFaithPayment() {
      let totalDebt = isNaN(parseFloat(this.orderInfo.totalDebt))
        ? 0
        : parseFloat(this.orderInfo.totalDebt);
      let goodFaithPayment = Math.ceil(
        Number(((totalDebt * 5) / 100).toFixed(2))
      );
      this.orderInfo.goodFaithPayment = goodFaithPayment;
      //this.generatePaymentAmount()
    },
    generateRemainingBalance() {
      let totalDebt = isNaN(parseFloat(this.orderInfo.totalDebt))
        ? 0
        : parseFloat(this.orderInfo.totalDebt);
      let goodFaithPayment = this.orderInfo.goodFaithPayment;
      let remainingBalance = totalDebt - goodFaithPayment;
      this.remainingBalance = Math.round(remainingBalance * 100) / 100;
      this.calculatePaymentPlan();
    },
    generatePaymentAmount() {
      this.generateGoodFaithPayment();
      this.generateRemainingBalance();

      // let paymentPlan = SiteUtils.calculatePaymentPlan(this.orderInfo.totalDebt, this.orderInfo.paymentMonths, { isIllinois: true });
    },

    onPaymentAmountChange() {
      this.validatePaymentAmount();
      let currentPaymentMonth = Math.floor(
        this.remainingBalance / parseFloat(this.paymentAmount)
      );
      this.error.paymentMonths =
        currentPaymentMonth > this.maxPaymentMonth ? true : false;
      //this.error.paymentAmount = this.error.paymentMonths ? true : !this.error.paymentMonths && !this.error.paymentAmount ? false : this.error.paymentAmount;
      /**/
      if (!this.error.paymentMonths) {
        this.generatePaymentMonths();
      }
    },
    generatePaymentMonths() {
      this.orderInfo.paymentMonths = SiteUtils.calculatePaymentMonths(
        this.remainingBalance,
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
        parseFloat(SiteUtils.parseTotalDebit(this.remainingBalance)) /
        this.maxPaymentMonth;
      minimumMonthlyPayment = Number(minimumMonthlyPayment.toFixed(0));
      let currentPaymentAmount = isNaN(parseFloat(this.paymentAmount))
        ? 0
        : parseFloat(this.paymentAmount);
      // if (currentPaymentAmount < this.paymentPlanConfig.minMonthlyPayment) {
      //   this.error.paymentAmount = `Minimum payment must be $${this.paymentPlanConfig.minMonthlyPayment}`;
      // }
      if (currentPaymentAmount < minimumMonthlyPayment) {
        this.error.paymentAmount = `Minimum payment must be $${minimumMonthlyPayment}`;
        // this.error.paymentAmount = true;
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
            this.orderInfo.amount = ((this.productPrice - this.discount) * 100).toString();
          });
      }
    },

    postFailureReason() {
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
      if (typeof this.orderInfo.totalDebt === "string") {
        this.orderInfo.totalDebt = this.orderInfo.totalDebt.replace("$", "");
      }

      this.isProcessing = true;
      // let order=Object.assign({},this.orderInfo)
      // order.shippingAddress1 = SiteUtils.standardizeAddress(order.shippingAddress1);
      this.orderInfoTemp = SiteUtils.updateAddress(
        this.orderInfoTemp,
        this.orderInfo,
        false,
        this.shippingAddressSame
      );

      this.isProcessing = false;
      this.orderInfoTemp.failureReason = "Payment Confirmation Declined";
      this.postFailureReason();
    },
    continuePayment() {
      this.confirmPaymentModal = false;

      if (typeof this.orderInfo.totalDebt === "string") {
        this.orderInfo.totalDebt = this.orderInfo.totalDebt.replace("$", "");
      }

      this.isProcessing = true;
      // let order=Object.assign({},this.orderInfo)
      // order.shippingAddress1 = SiteUtils.standardizeAddress(order.shippingAddress1);
      this.orderInfoTemp = SiteUtils.updateAddress(
        this.orderInfoTemp,
        this.orderInfo,
        false,
        this.shippingAddressSame
      );
      AXIOS.put(`/ecommerce/presave`, this.orderInfoTemp)
        .then((res) => {
          let self = this;
          AXIOS.put(`/paymentplan`, {
            married: this.orderInfo.married,
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
            isOwedFromBusiness: this.orderInfo.isOwedFromBusiness,
            ein: this.orderInfo.ein,
            businessName: this.orderInfo.businessName,
            dba: this.orderInfo.dba,
            illinoisAccountId: this.orderInfo.illinoisAccountId,
            goodFaithPayment: this.orderInfo.goodFaithPayment,
            mobile: this.orderInfo.mobile,
            monthlyPayment: this.paymentAmount,
          })
            .then(() => self.postOrderInfo())
            .catch((e) => {
              self.isProcessing = false;
              self.initializeCardinal();
              if (e.response.status === 409) {
                self.orderInfo.failureReason = "Chargeback Order Prevented";
                self.errorMessages.push({
                  show: true,
                  text: "Sorry, it looks like you may have already placed an order with us! Please <a href='/contact-us'>contact us</a> for help.",
                });
              } else {
                self.orderInfo.failureReason = "Failed in paymentplan API call";
                alert(
                  "Duplicated order or unexpected error has occurred. Please try again later."
                );
              }
              self.orderInfoTemp = SiteUtils.updateAddress(
                self.orderInfoTemp,
                self.orderInfo,
                self.addressType,
                self.shippingAddressSame
              );
              self.postFailureReason();
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
      this.orderInfo.illinoisAccountId = SiteUtils.standardizeIllinoisId(
        this.orderInfo.illinoisAccountId
      );

      if (this.shippingAddressSame) {
        this.orderInfo.billingAddress1 = this.orderInfo.shippingAddress1;
        this.orderInfo.billingAddress2 = this.orderInfo.shippingAddress2;
        this.orderInfo.billingCity = this.orderInfo.shippingCity;
        this.orderInfo.billingState = this.orderInfo.shippingState;
        this.orderInfo.billingPostalCode = this.orderInfo.shippingPostalCode;
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
      }

      this.orderInfoTemp = SiteUtils.updateAddress(
        this.orderInfoTemp,
        this.orderInfo,
        false,
        this.shippingAddressSame
      );

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
      this.showGoogleTextBox = false;
    },
    extractAddress: function () {
      google.maps.places.Autocomplete(this.getAddressData, "place_changed");
      this.showGoogleTextBox = false;
    },
    changeToGoogle: function () {
      this.showGoogleTextBox = true;
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
  },
  computed: {
    paymentPlan: function () {
      return SiteUtils.calculatePaymentPlan(
        this.remainingBalance,
        this.orderInfo.paymentMonths,
        { isIllinois: true }
      );
      // return SiteUtils.calculatePaymentPlan(this.orderInfo.totalDebt, this.orderInfo.paymentMonths, { isIllinois: true });
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
        let mainAmount = parseInt(amounts[0]) - this.orderInfo.goodFaithPayment;
        let amountToReturn = "";
        amountToReturn = SiteUtils.calculatePaymentPlan(
          mainAmount,
          this.orderInfo.paymentMonths,
          { isIllinois: true }
        ).paymentAmount;
        if (amounts.length > 1) {
          amountToReturn =
            "$" +
            amountToReturn +
            " - $" +
            SiteUtils.calculatePaymentPlan(
              amounts[1],
              this.orderInfo.paymentMonths,
              { isIllinois: true }
            ).paymentAmount;
        }
        return amountToReturn;
      } else {
        return "$" + this.paymentAmount;
      }

      return "...";
    },
    paymentMessage: function () {
      let paymentPlan = SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths,
        { isIllinois: true }
      );
      if (paymentPlan.paymentAmount === "N/A") {
        return "If your debt is between $500 and $75,000 then you will qualify.";
      }
      if (paymentPlan.paymentAmount < 102) {
        return "You qualify for a $25 a month payment plan with the IRS, but your first payment will be $102.";
      } else {
        return `You qualify for a $${paymentPlan.paymentAmount} a month payment plan with the IRS.`;
      }
    },
  },
  mounted() {
    this.initializeSalesWidget();
    this.initializeCardinal();
    this.initializePricings();

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
        this.orderInfo.paymentMonths =
          response.data.paymentMonths || this.paymentPlanConfig.maxMonth;
        this.generateGoodFaithPayment();
        this.generatePaymentAmount();
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
  /* width: 80px;*/
  background-color: white;
  font-size: 20px;
  height: 38px;
}

.remaining-balance {
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

/* .partner-code-block {
  position: relative;
  margin-bottom: 20px !important;
  display: flex;
  justify-content: center;
  left: 20px;
} */

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
  /* width: 56%;
  height: 35%; */
  /* margin-top:5px; */
  margin-bottom: 10px;
  /* margin-left:15px; */
}

.amount-owe-selector {
  display: flex;
  /*left: -2rem;*/
}
.btn-primary {
  background-color: #2896c5;
  border: 0px;
}

.partner-conde-text {
  font-size: 45%;
  position: absolute;
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
.w-max-content {
  width: max-content;
}
.po-box {
  width: 77%;
  margin-top: 5%;
  margin-left: -4%;
}

hr {
  margin-top: 0.2rem;
}

.business-selector-help {
  margin-top: -0.5rem;
  margin-left: 0.5rem;
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
.slider {
  height: 25px;
  width: 100%;
  /* background: #d3d3d3;  Grey background */
  /*opacity: 0.7; /* Set transparency (for mouse-over effects on hover) */
  /* transition: opacity .2s;*/
}
.arrow {
  border: solid black;
  border-width: 0 3px 3px 0;
  display: inline-block;
  padding: 3px;
}
.right {
  transform: rotate(-45deg);
  -webkit-transform: rotate(-45deg);
}

.left {
  transform: rotate(135deg);
  -webkit-transform: rotate(135deg);
}
</style>
