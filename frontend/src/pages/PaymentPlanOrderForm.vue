<template>
  <page :hideTopNav="true" :navType="'payment-plan-order-form'">
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
          the IRS
        </h2>
        <!-- <p v-if="orderInfo.totalDebt >= 50000 && orderInfo.totalDebt <= 150000">Since your debt is over $50,000, the IRS requires you to fill out a financial statement. We will include this with your application.</p> -->
        <p class="my-3">Complete the information below:</p>
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
        </div>
      </div>
      <p class="my-3">
        Your application will go to the mailing address below with tracking. The
        IRS requires your physical signature.
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
              id="shipping_address1"
              :class="[
                'form-control',
                error.shippingAddress1 || error.addressType ? 'is-invalid' : '',
              ]"
              v-show="!addressType && showAddressTextBox"
              placeholder="Street Address"
              @placechanged="getShippingAddressData"
              @blur="extractShippingAddress"
              @inputChange="setShippingAddress"
              country="us"
            >
            </vue-google-autocomplete>

            <input
              :class="[
                'form-control',
                error.shippingAddress1 || error.addressType ? 'is-invalid' : '',
              ]"
              v-if="!showAddressTextBox && !addressType"
              v-model="orderInfo.shippingAddress1"
              :error="error.shippingAddress1 || error.addressType"
              @focusin="changeToAddressGoogle"
            />

            <div class="align-items-center">
              <div v-if="addressType" class="mr-2">PO. BOX #</div>
              <input
                :class="[
                  'form-control',
                  error.addressType && !error.shippingAddress1 && addressType
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
            <label>City</label>
            <input
              classes="mailing__city"
              :class="['form-control', error.shippingCity ? 'is-invalid' : '']"
              v-model="orderInfo.shippingCity"
              :error="error.shippingCity"
              label="City"
              v-on:keypress="isLetter($event)"
            />
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-sm-4">
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
          <div class="col-12 col-sm-4">
            <span v-if="error.shippingAddress2" class="text-danger mt-1">
              This box is for your APT/UNIT/STE number only.
            </span>
          </div>
          <div class="col-12 col-sm-4">
            <span v-if="error.shippingCity" class="text-danger mt-1">
              {{ error.shippingCity }}
            </span>
          </div>
        </div>
        <div class="row">
          <div class="col-12 col-sm-6">
            <state-select-field
              classes="billing__shippingState"
              v-model="orderInfo.shippingState"
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
              {{ error.shippingPostalCode }}
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

          <div class="col-12 col-sm-6">
            <label>City</label>
            <input
              class="form-control"
              v-model="orderInfo.billingCity"
              :error="error.billingCity"
              label="City"
              v-on:keypress="isLetter($event)"
            />
            <span v-if="error.billingCity" class="text-danger mt-1">{{
              error.billingCity
            }}</span>
          </div>
          <div class="col-12 col-sm-6">
            <state-select-field
              classes="billing_billingState"
              v-model="orderInfo.billingState"
              :error="error.billingState"
              label="State"
            ></state-select-field>
          </div>
          <div class="col-12 col-sm-6 mt-1">
            <label>Zip Code</label>
            <input
              classes="billing_billingPostalCode"
              class="form-control"
              v-model="orderInfo.billingPostalCode"
              :maxlength="5"
              :error="error.billingPostalCode"
              label="Zip Code"
              v-mask="'#####'"
              type="tel"
              @keypress="onlyNumber"
            />
            <span v-if="error.billingPostalCode" class="text-danger mt-1">{{
              error.billingPostalCode
            }}</span>
          </div>
        </div>
        <div class="row">
          <div class="col-12 mt-1">
            <div class="d-flex flex-column">
              <label for="hasOldAddress"
                >Is this a new address since your last tax return?</label
              >
              <select
                id="hasOldAddress"
                v-model="orderInfo.hasOldAddress"
                class="form-control"
                v-on:change="updateHasOldAddress($event)"
              >
                <option value="yes">Yes</option>
                <option value="no">No</option>
              </select>
            </div>
          </div>
        </div>
        <!-- Change Address Form -->
        <div
          class="change-address-form"
          v-if="orderInfo.hasOldAddress == 'yes'"
        >
          <div class="row">
            <div class="col-12">
              <label>What is your old address?</label>
            </div>
          </div>
          <div class="row align-items-end">
            <div class="form-group col-12 col-sm-4">
              <div class="d-flex align-items-center">
                <input
                  type="checkbox"
                  v-on:click="(oldAddress1 = ''), (oldAddress2 = '')"
                  name="oldAddressType"
                  :value="true"
                  v-model="oldAddressType"
                />
                <span class="ml-2">Mail to a PO BOX</span>
              </div>

              <vue-google-autocomplete
                id="old_shipping_address1"
                :class="[
                  'form-control',
                  error.oldAddress1 || error.oldAddressType ? 'is-invalid' : '',
                ]"
                v-show="!oldAddressType && showOldAddressTextBox"
                placeholder="Street Address"
                @placechanged="getOldShippingAddressData"
                @blur="extractOldShippingAddress"
                @inputChange="setOldShippingAddress"
                country="us"
              >
              </vue-google-autocomplete>

              <input
                :class="[
                  'form-control',
                  error.oldAddress1 || error.oldAddressType ? 'is-invalid' : '',
                ]"
                v-if="!showOldAddressTextBox && !oldAddressType"
                v-model="orderInfo.oldAddress1"
                :error="error.oldAddress1 || error.oldAddressType"
                @focusin="changeToOldAddressGoogle"
              />

              <div class="align-items-center">
                <div v-if="oldAddressType" class="mr-2">PO. BOX #</div>
                <input
                  :class="[
                    'form-control',
                    error.oldAddressType && !error.oldAddress1 && oldAddressType
                      ? 'is-invalid'
                      : '',
                  ]"
                  style="flex: 1"
                  v-if="oldAddressType"
                  v-model="orderInfo.oldAddress1"
                  :error="error.oldAddress1 || error.oldAddressType"
                  @keypress="onlyNumber"
                  type="tel"
                />
              </div>
            </div>
            <div class="form-group col-12 col-sm-4" v-if="!oldAddressType">
              <form-input-field
                classes="mailing__address2"
                maxlength="12"
                v-model="orderInfo.oldAddress2"
                :error="error.oldAddress2"
                label="Apt/Unit #"
              ></form-input-field>
            </div>
            <div class="form-group col-12 col-sm-4">
              <label>City</label>
              <input
                classes="mailing__city"
                :class="['form-control', error.oldCity ? 'is-invalid' : '']"
                v-model="orderInfo.oldCity"
                :error="error.oldCity"
                label="City"
                v-on:keypress="isLetter($event)"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-12 col-sm-4">
              <span
                v-if="
                  error.oldAddress1 && !error.oldAddressType && !oldAddressType
                "
                class="w-100 text-danger"
              >
                {{ error.oldAddress1 }}
              </span>
              <span
                v-if="
                  error.oldAddressType && !error.oldAddress1 && oldAddressType
                "
                class="w-100 text-danger"
              >
                {{ error.oldAddress1 }}
              </span>
            </div>
            <div class="col-12 col-sm-4">
              <span v-if="error.oldAddress2" class="text-danger mt-1">
                This box is for your APT/UNIT/STE number only.
              </span>
            </div>
            <div class="col-12 col-sm-4">
              <span v-if="error.oldCity" class="text-danger mt-1">
                {{ error.oldCity }}
              </span>
            </div>
          </div>
          <div class="row">
            <div class="col-12 col-sm-6">
              <state-select-field
                classes="billing__shippingState"
                v-model="orderInfo.oldState"
                :error="error.oldState"
                label="State"
              ></state-select-field>
            </div>
            <div class="col-12 col-sm-6">
              <label>Zip Code</label>
              <input
                v-model="orderInfo.oldZip"
                :maxlength="5"
                :error="error.oldZip"
                placeholder="Zip Code"
                type="tel"
                v-mask="'#####'"
                :class="['form-control', error.oldZip ? 'is-invalid' : '']"
              />
            </div>
          </div>
        </div>

        <!-- End Change Address Form -->
        <!-- Prior Names -->
        <div class="prior-names mt-2" v-if="orderInfo.hasOldAddress == 'yes'">
          <div class="d-flex flex-column">
            <yes-no-select-field
              class="p-0"
              v-model="hadAnyPriorNames"
              label="Have you had any prior names?"
            >
            </yes-no-select-field>
          </div>
          <div class="prior-names-form" v-if="hadAnyPriorNames">
            <div class="row">
              <div class="col-5">
                <label class="mb-0">First Name</label>
              </div>
              <div class="col-2">
                <label class="mb-0">Middle Initial</label>
              </div>
              <div class="col-5">
                <label class="mb-0">Last Name</label>
              </div>
            </div>
            <div
              class="row mt-2"
              v-for="(name, index) in priorNames"
              :key="index"
            >
              <div class="col-5">
                <input
                  type="text"
                  v-model="name.firstName"
                  class="form-control"
                  placeholder="First Name"
                />
              </div>
              <div class="col-2">
                <input
                  type="text"
                  v-model="name.middle"
                  class="form-control"
                  placeholder=""
                />
              </div>
              <div class="col-5">
                <input
                  type="text"
                  v-model="name.lastName"
                  class="form-control"
                  placeholder="Last Name"
                />
              </div>
            </div>
            <button
              type="button"
              class="btn-add-prior-name d-flex justify-content-center align-items-center mt-2"
              @click="addPriorName"
            >
              +
            </button>
          </div>
        </div>
        <!-- End Prior Names -->
      </div>
      <p class="my-3">
        You may adjust your monthly payments below. The payments are calculated
        at the minimum to start.
      </p>
      <div class="payment-form">
        <div class="my-3">
          <h4 class="text-center">Payment Information</h4>
        </div>
        <div class="row">
          <div class="col-12 col-md-4 mt-sm-5 pt-sm-2">
            <label>
              Total Debt Owed
              <span
                v-b-popover.hover.bottom="
                  'The total amount you owe can be found on notices received by the IRS. This amount includes all tax years.'
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
              @blur="checkTotalDebtQualification"
              type="text"
            />
            <div class="text-danger" v-if="error.totalDebt">
              {{ error.totalDebt }}
            </div>
          </div>
          <div class="col-12 col-md-4">
            <div class="mb-2">
              <div class="text-center font-weight-bold">Your monthly</div>
              <div class="text-center font-weight-bold">IRS payment:</div>
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
        <div class="row mt-4">
          <div class="col-12 col-md-10">
            <div class="row">
              <div class="col-12 col-sm-8 pt-2">
                Is this amount owed from a business?
              </div>
              <div class="form-group col-12 col-sm-4">
                <label for="isOwedFromBusiness" class="d-none"
                  >Is this amount owed from a business?</label
                >
                <select
                  id="isOwedFromBusiness"
                  v-model="orderInfo.isOwedFromBusiness"
                  class="form-control"
                  v-on:change="updateIsOwedFromBusiness($event)"
                >
                  <option value="yes">Yes</option>
                  <option value="no">No</option>
                </select>
              </div>
            </div>
          </div>
        </div>
        <div class="row" v-if="!isInBusinessVisible">
          <div class="col-12 col-md-10">
            <div class="row">
              <div class="col-12 col-sm-8">
                For convenience, would you like the IRS to process your monthly
                payment automatically through your paycheck?
              </div>
              <div class="form-group col-12 col-sm-4">
                <label for="payrollDeduction" class="d-none"
                  >For convenience, would you like the IRS to process your
                  monthly payment automatically through your paycheck?</label
                >
                <select
                  id="payrollDeduction"
                  v-model="orderInfo.payrollDeduction"
                  class="form-control"
                  v-on:change="updatePayrollDeduction($event)"
                >
                  <option value="yes">Yes</option>
                  <option value="no">No</option>
                </select>
              </div>
            </div>
          </div>
        </div>
        <div class="employer-information-form" v-if="isPayrollOptionVisible">
          <div class="my-3">
            <h4 class="text-center">Employer Information</h4>
          </div>
          <div class="row">
            <div class="form-group col-12 col-sm-6">
              <label>Employer Name</label>
              <input
                :class="[
                  'form-control',
                  error.employerName ? 'is-invalid' : '',
                ]"
                v-model="orderInfo.employerName"
                :error="error.employerName"
                placeholder="Employer Name"
              />
              <div
                v-if="error.employerName"
                class="text-danger"
                style="margin-top: 10px"
              >
                {{ error.employerName }}
              </div>
            </div>
          </div>
          <div class="row align-items-end">
            <div class="form-group col-12 col-sm-4">
              <label>Employer Location</label>
              <div class="d-flex align-items-center">
                <input
                  type="checkbox"
                  v-on:click="
                    (orderInfo.employerAddress1 = ''),
                      (orderInfo.employerAddress2 = '')
                  "
                  name="employerAddressType"
                  :value="true"
                  v-model="employerAddressType"
                />
                <span class="ml-2">Mail to a P.O. BOX</span>
              </div>

              <vue-google-autocomplete
                id="employer_address1"
                :class="[
                  'form-control',
                  error.employerAddress1 || error.employerAddressType
                    ? 'is-invalid'
                    : '',
                ]"
                v-show="!employerAddressType && showEmployerAddressTextBox"
                placeholder="Street Address"
                @placechanged="getEmployerAddressData"
                @blur="extractEmployerAddress"
                @inputChange="setEmployerAddress"
                country="us"
              >
              </vue-google-autocomplete>
              <input
                :class="[
                  'form-control',
                  error.employerAddress1 || error.employerAddressType
                    ? 'is-invalid'
                    : '',
                ]"
                v-if="!showEmployerAddressTextBox && !employerAddressType"
                v-model="orderInfo.employerAddress1"
                :error="error.employerAddress1 || error.employerAddressType"
                @focusin="changeToEmployerAddressGoogle"
              />
              <div class="align-items-center">
                <div v-if="employerAddressType" class="mr-2">P.O. BOX #</div>
                <input
                  :class="[
                    'form-control',
                    error.employerAddressType &&
                    !error.employerAddress1 &&
                    employerAddressType
                      ? 'is-invalid'
                      : '',
                  ]"
                  style="flex: 1"
                  v-if="employerAddressType"
                  v-model="orderInfo.employerAddress1"
                  :error="error.employerAddress1 || error.addressType"
                  @keypress="onlyNumber"
                  type="tel"
                />
              </div>
            </div>
            <div class="form-group col-12 col-sm-4" v-if="!employerAddressType">
              <form-input-field
                classes="mailing__address2"
                maxlength="12"
                v-model="orderInfo.employerAddress2"
                :error="error.employerAddress2"
                label="Suite #"
              ></form-input-field>
            </div>
            <div class="form-group col-12 col-sm-4">
              <label>Employer City</label>
              <input
                classes="mailing__city"
                :class="[
                  'form-control',
                  error.employerCity ? 'is-invalid' : '',
                ]"
                v-model="orderInfo.employerCity"
                :error="error.employerCity"
                placeholder="City"
                v-on:keypress="isLetter($event)"
              />
            </div>
          </div>
          <div class="row">
            <div class="col-12 col-sm-4">
              <span
                v-if="
                  error.employerAddress1 &&
                  !error.employerAddressType &&
                  !employerAddressType
                "
                class="w-100 text-danger"
              >
                {{ error.employerAddress1 }}
              </span>
              <span
                v-if="
                  error.employerAddressType &&
                  !error.employerAddress1 &&
                  employerAddressType
                "
                class="w-100 text-danger"
              >
                {{ error.employerAddress1 }}
              </span>
            </div>
            <div class="col-12 col-sm-4">
              <span v-if="error.employerAddress2" class="text-danger mt-1">
                This box is for your APT/UNIT/STE number only.
              </span>
            </div>
            <div class="col-12 col-sm-4">
              <span v-if="error.employerCity" class="text-danger mt-1">
                {{ error.employerCity }}
              </span>
            </div>
          </div>

          <div class="row">
            <div class="col-12 col-sm-6">
              <state-select-field
                classes="billing__employerState"
                v-model="orderInfo.employerState"
                :error="error.employerState"
                label="Employer State"
              ></state-select-field>
            </div>
            <div class="col-12 col-sm-6">
              <label>Employer Zip Code</label>
              <input
                v-model="orderInfo.employerZip"
                :maxlength="5"
                :error="error.employerZip"
                placeholder="Zip Code"
                type="tel"
                v-mask="'#####'"
                :class="['form-control', error.employerZip ? 'is-invalid' : '']"
              />
            </div>
            <div class="col-12 col-sm-6">
              <span v-if="error.employerZip" class="text-danger mt-1">
                {{ error.employerZip }}
              </span>
            </div>
          </div>

          <div class="row">
            <div class="col-12 col-sm-6 pt-3">
              <label>How often are you paid? Every:</label>
              <generic-select-field
                classes="form-group"
                v-model="orderInfo.payFrequency"
                label=""
                placeholder="Please Select"
                error=""
              >
                <option value="-1">Please select</option>
                <option value="0">One Week</option>
                <option value="1">Two Weeks</option>
                <option value="2">One Month</option>
              </generic-select-field>
            </div>
          </div>

          <div class="row">
            <div class="col-12 pt-2">
              <p class="font-italic">
                Please note: This option requires us to prepare additional tax
                documents, and adds an additional processing fee of ${{
                  customPayrollFee
                }}
                to your order. You will also need to have your employer sign
                your paperwork.
              </p>
            </div>
          </div>

          <div class="row pt-4">
            <div class="col-12 col-sm-6">
              <form-input-field
                v-model="orderInfo.employerContactName"
                classes="billing__phoneNumber"
                :error="error.employerContactName"
                label="Employer Contact Name"
                placeholder="First, Last Name"
              ></form-input-field>

              <div
                v-if="error.employerContactName"
                class="text-danger"
                style="margin-top: 10px"
              >
                {{ error.employerContactName }}
              </div>
            </div>
            <div class="col-12 col-sm-6">
              <form-input-field
                v-model="orderInfo.employerContactPhoneNumber"
                classes="billing__phoneNumber"
                :error="error.employerContactPhoneNumber"
                label="Employer Contact Phone Number"
                placeholder="123-456-7890"
                v-mask="'###-###-####'"
                :name="'phoneNumber'"
                type="tel"
              ></form-input-field>

              <div
                v-if="error.employerContactPhoneNumber"
                class="text-danger"
                style="margin-top: 10px"
              >
                {{ error.employerContactPhoneNumber }}
              </div>
            </div>
          </div>
        </div>

        <div class="row" v-if="isInBusinessVisible">
          <div class="col-12 col-md-10">
            <div class="row">
              <div class="col-12 col-sm-8 pt-2">Are you still in business?</div>
              <generic-select-field
                withoutPlaceHolder
                classes="form-group col-12 col-sm-4"
                v-model="orderInfo.isInBusiness"
                label=""
                error=""
              >
                <option value="yes">Yes</option>
                <option value="no">No</option>
              </generic-select-field>
            </div>
          </div>
        </div>
        <div class="row" v-if="isPayrollTaxVisible">
          <div class="col-12 col-md-10">
            <div class="row">
              <div class="col-12 col-sm-8 pt-2">
                Does this balance include any payroll or employment tax?
              </div>
              <generic-select-field
                withoutPlaceHolder
                classes="form-group col-12 col-sm-4"
                v-model="orderInfo.isPayrollTax"
                label=""
                error=""
              >
                <option value="yes">Yes</option>
                <option value="no">No</option>
              </generic-select-field>
            </div>
          </div>
        </div>
        <div class="row" v-if="isSoleProprietorshipVisible">
          <div class="col-12 col-md-10">
            <div class="row">
              <div class="col-12 col-sm-8 pt-2">
                Is this a sole proprietorship?
              </div>
              <generic-select-field
                withoutPlaceHolder
                classes="form-group col-12 col-sm-4"
                v-model="orderInfo.isSoleProprietorship"
                label=""
                error=""
              >
                <option value="yes">Yes</option>
                <option value="no">No</option>
              </generic-select-field>
            </div>
          </div>
        </div>
        <div class="row mt-4" v-if="isBusinessLogicAcceptable">
          <div class="col-12 col-md-8">
            <form-input-field
              classes="form-group"
              v-model="orderInfo.businessName"
              :error="error.businessName"
              label="Legal Business Name (Include 'INC' or 'LLC' if applicable)"
              placeholder="Business Name"
            />
          </div>
          <div class="col-12 col-md-4">
            <form-input-field
              name="ein"
              type="tel"
              classes="form-group"
              v-model="orderInfo.ein"
              :error="error.ein"
              label="Business EIN"
              placeholder="EIN #"
            />
          </div>
        </div>
        <div
          class="mt-4"
          v-if="
            orderInfo.isOwedFromBusiness === 'yes' && !isBusinessLogicAcceptable
          "
        >
          <b-alert variant="danger" show>
            I am sorry, this method of applying for a payment plan will not work
            in your case. Please use our
            <router-link to="/contact-us">contact form</router-link> for other
            options.
          </b-alert>
        </div>
      </div>
      <p class="my-3">Here are more details for your application:</p>
      <div class="application-details-form">
        <div class="my-3">
          <h4 class="text-center">Application Details</h4>
        </div>
        <div class="row">
          <form-input-field
            v-model="orderInfo.ssn"
            :error="error.ssn"
            label="Social Security Number or TIN"
            placeholder="000-00-0000"
            v-mask="'###-##-####'"
            :name="'ssn'"
            type="tel"
            autocomplete="off"
          ></form-input-field>
          <yes-no-select-field
            v-model="orderInfo.married"
            :error="error.married"
            label="Are you married?"
          >
            <!-- helpText="If you filed your tax returns, for the years that you owe, jointly with a spouse or resident domestic partner (RDP), answer yes to this question.
           If you and your spouse/RDP filed separately you can answer 'No' to this question."-->
          </yes-no-select-field>
          <generic-select-field
            v-if="orderInfo.married == true"
            classes="form-group col-12"
            v-model="orderInfo.filingJointly"
            label="How did you file your last tax return with your spouse?"
          >
            <option :value="marriedFilingOption.jointly">
              I filed my tax return jointly with my spouse (we have one
              together)
            </option>
            <option :value="marriedFilingOption.separately">
              I filed my tax return separately from my spouse (we each have our
              own)
            </option>
          </generic-select-field>

          <div
            class="form-group col-12"
            v-if="
              orderInfo.filingJointly === marriedFilingOption.separately &&
              orderInfo.married == true
            "
          >
            <p class="font-italic">
              Alert: Since you filed separately, you will need to place a second
              order after this one. The IRS requires two separate payment plans
              for spouse's who file separately.
            </p>
          </div>

          <div
            v-if="
              orderInfo.filingJointly === marriedFilingOption.jointly &&
              orderInfo.married == true
            "
            class="col-12 col-sm-6"
          >
            <label>Spouse First Name</label>
            <input
              :class="[
                'form-control',
                error.spouseFirstName ? 'is-invalid' : '',
              ]"
              v-model="orderInfo.spouseFirstName"
              :error="error.spouseFirstName"
              label="Spouse First Name"
              placeholder="First Name"
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

          <div
            v-if="
              orderInfo.filingJointly === marriedFilingOption.jointly &&
              orderInfo.married == true
            "
            class="col-12 col-sm-6"
          >
            <label>Spouse Last Name</label>
            <input
              :class="[
                'form-control',
                error.spouseLastName ? 'is-invalid' : '',
              ]"
              v-model="orderInfo.spouseLastName"
              :error="error.spouseLastName"
              label="Spouse Last Name"
              placeholder="Last Name"
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

          <form-input-field
            v-if="
              orderInfo.filingJointly === marriedFilingOption.jointly &&
              orderInfo.married == true
            "
            v-model="orderInfo.spouseSsn"
            :error="error.spouseSsn"
            label="Spouse Social Security Number or TIN"
            placeholder="000-00-0000"
            v-mask="'###-##-####'"
            autocomplete="off"
            type="tel"
            :name="'ssn'"
          >
          </form-input-field>
          <div
            v-if="
              orderInfo.filingJointly === marriedFilingOption.jointly &&
              orderInfo.married == true
            "
            class="col-12 col-sm-6"
          ></div>
          <generic-select-field
            v-model="orderInfo.timeToCall"
            :error="error.timeToCall"
            label="Best Time to Call"
          >
            <option value="Morning">Morning</option>
            <option value="Afternoon">Afternoon</option>
            <option value="Evening">Evening</option>
            <option value="Anytime">Anytime</option>
          </generic-select-field>
          <generic-select-field
            v-model="orderInfo.paymentDayOfMonth"
            :error="error.paymentDayOfMonth"
            label="Day of Month to Make Payments"
            helpText="This is the day of the month you prefer to make your normal monthly payment. It will not necessarily be the day your first payment is due because it takes time for the IRS to approve your payment plan application."
          >
            <option v-for="index in 28" :value="index" :key="index">
              {{ index }}
            </option>
          </generic-select-field>
          <div class="form-group col-12 col-sm-6">
            <label for="processingSpeed"> Processing Speed </label>
            <select
              id="processingSpeed"
              v-model="orderInfo.processingSpeed"
              class="form-control processing-select"
              v-on:change="updateProcessingFee($event)"
            >
              <option value="Standard">Standard</option>
              <option value="Deluxe">
                Deluxe +${{ customExpressOptionFee }}
              </option>
            </select>
            <img
              v-if="orderInfo.processingSpeed === 'Standard'"
              src="@/assets/processing-standard.svg"
              width="40"
              height="35"
            />
            <img
              v-if="orderInfo.processingSpeed === 'Deluxe'"
              src="@/assets/processing-deluxe.svg"
              width="40"
              height="35"
            />
          </div>
          <div style="display: none;" class="form-group col-12 col-sm-6">
            <label>Confirmation</label>

            <input
              v-model="orderInfo.confirmation"
              class="form-control"
              placeholder="Confirm"
            />
          </div>
          <div class="col-12 pt-2">
            <p
              class="font-italic"
              v-if="orderInfo.processingSpeed === 'Standard'"
            >
              97tax will create your application within 24 hours and mail it to
              you using First Class mail. You should receive your application
              within 5-7 business days. No additional charge.
            </p>
            <p
              class="font-italic"
              v-if="orderInfo.processingSpeed === 'Deluxe'"
            >
              97tax will mail your application to you using the faster USPS
              Priority mail. The included prepaid mailing envelope that sends
              your application to the IRS will also include a tracking number to
              confirm delivery to the IRS. Cost: ${{ customExpressOptionFee }}.
            </p>
          </div>
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
                  <p class="float-right">${{ customEnrollmentFee }}.00</p>
                </div>
              </div>
              <div class="row" v-if="isPayrollOptionVisible">
                <div class="col-8">Automatic Paycheck Deduction</div>
                <div class="col-4">
                  <p class="float-right">${{ customPayrollFee }}.00</p>
                </div>
              </div>
              <div class="row" v-if="orderInfo.processingSpeed === 'Deluxe'">
                <div class="col-8">Deluxe Processing</div>
                <div class="col-4">
                  <p class="float-right">${{ customExpressOptionFee }}.00</p>
                </div>
              </div>
              <div class="row" v-if="orderInfo.hasOldAddress == 'yes'">
                <div class="col-8">Change of Address</div>
                <div class="col-4">
                  <p class="float-right">${{ customChangeOfAddressFee }}.00</p>
                </div>
              </div>
              <div class="row">
                <div class="col-8">Subtotal</div>
                <div class="col-4">
                  <!-- <p class="float-right">${{ isPayrollOptionVisible?'109.00':'97.00' }}</p> -->
                  <p class="float-right">${{ subTotal.toFixed(2) }}</p>
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
                  <p class="float-right">${{ total.toFixed(2) }}</p>
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
            :disabled="
              orderInfo.isOwedFromBusiness === 'yes' &&
              !isBusinessLogicAcceptable
            "
            v-on:click="updatePaymentPlanDetails()"
          >
            PROCESS PAYMENT &nbsp;&gt;&gt;
          </button>
        </div>
      </div>
      <div class="mt-3">
        <p class="text-center" style="color: #888">
          The ${{ total }} will be processed now on the card above. You will
          receive an email with USPS tracking information within 1 business day
          when your application is complete. Your normal monthly payments go
          directly from you to the IRS to pay off your tax balance. Personal
          data is never shared or sold and is fully encrypted and secure in
          accordance with our Privacy Policy.
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
      :amount="total"
      :efile="true"
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
const marriedFilingOption = Object.freeze({ separately: "No", jointly: "Yes" });

export default {
  components: { VueGoogleAutocomplete, PaymentConfirm, SalesWidget },
  data() {
    return {
      salesPhoneNumber: null,
      customEnrollmentFee: 97,
      customPayrollFee: 12,
      customChangeOfAddressFee: 12,
      customExpressOptionFee: 22,
      confirmPaymentModal: false,
      shippingAddressSame: true,
      isProcessing: false,
      showPaymentMessageInPage: false,
      jwtData: null,
      addressType: false,
      oldAddressType: false,
      employerAddressType: false,
      showAddressTextBox: true,
      showOldAddressTextBox: true,
      showEmployerAddressTextBox: true,
      orderNumber: "",
      percentageOff: 0,
      paymentAmount: 0,
      totalDebtPrevValue: "",
      processingFee: 0,
      enrollmentFee: 97,
      payrollDeductionFee: 0,
      changeOfAddressFee: 0,
      paymentPlanConfig: paymentPlansConfig.irsPaymentPlan,
      maxPaymentMonth: paymentPlansConfig.irsPaymentPlan.maxMonth,
      marriedFilingOption: marriedFilingOption,
      orderInfo: {
        confirmation: "",
        billingFirstName: "",
        billingLastName: "",
        billingPhone: "",
        shippingFirstName: "",
        shippingLastName: "",
        shippingPhone: "",
        email: "",

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
        employerName: "",
        employerAddress1: "",
        employerAddress2: "",
        employerCity: "",
        employerState: this.$cookie.get("state"),
        employerZip: "",
        payFrequency: "-1",
        employerContactName: "",
        employerContactPhoneNumber: "",

        amount: "9700",
        totalDebt: "",
        isCalifornia: false,
        isNewJersey: false,
        isGeorgia: false,
        isIllinois: false,
        paymentMonths: paymentPlansConfig.irsPaymentPlan.maxMonth,
        isOwedFromBusiness: "no",
        payrollDeduction: "no",
        isInBusiness: "no",
        isPayrollTax: "no",
        isSoleProprietorship: "no",
        processingSpeed: "Standard",
        businessName: "",
        ein: "",
        ssn: "",
        married: false,
        filingJointly: marriedFilingOption.jointly,
        spouseFirstName: "",
        spouseLastName: "",
        spouseSsn: "",
        timeToCall: "Anytime",
        paymentDayOfMonth: "14",

        partnerCode: "",

        cardNumber: "",
        cardExpMonth: "",
        cardExpYear: "",
        cardCvc: "",

        product: "PaymentPlan",
        responseJwt: null,
        failureReason: "",
        hasOldAddress: "no",
        oldAddress1: "",
        oldAddress2: "",
        oldCity: "",
        oldState: this.$cookie.get("state"),
        oldZip: "",
        oldAddressType: false,
        hasPriorNames: false,
        priorNames: "",
      },

      error: {
        paymentAmount: "",
        paymentMonths: "",
        billingFirstName: "",
        billingLastName: "",
        billingPhone: "",
        email: "",

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
        employerAddressType: false,
        employerAddress1: "",
        employerAddress2: "",
        employerCity: "",
        employerState: "",
        employerZip: "",
        employerContactName: "",
        employerContactPhoneNumber: "",

        totalDebt: "",
        businessName: "",
        ein: "",
        ssn: "",
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

        // hasOldAddress: false,
        oldAddress1: "",
        oldAddress2: "",
        oldCity: "",
        oldState: "",
        oldZip: "",
        oldAddressType: false,
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

      // for change address
      hadAnyPriorNames: false,
      priorNames: [
        {
          firstName: "",
          middle: "",
          lastName: "",
        },
      ],
    };
  },
  methods: {
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
    validateTotalDebt($event) {
      this.showPaymentMessageInPage = true;
      this.orderInfo.totalDebt = SiteUtils.validateInputNumber($event);
      this.generatePaymentAmount();
    },
    checkTotalDebtQualification() {
      const paymentPlan = SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths
      );
      if (!paymentPlan.valid) {
        this.error.totalDebt = "Please input valid number.";
        return false;
      } else if (!paymentPlan.qualifies) {
        this.error.totalDebt =
          "This amount must be between $500 and $50,000.00 to qualify.";
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
        console.log(data, jwt);
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
                    console.log("pdf error", error);
                    alert(
                      "PDF error or unexpected error has occurred. Please try again later."
                    );
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
        "timeToCall",
        "paymentDayOfMonth",
        "cardNumber",
        "cardExpMonth",
        "cardExpYear",
        "cardCvc",
        "businessName",
      ];

      if (this.orderInfo.isOwedFromBusiness === "no") {
        required = [
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
          "timeToCall",
          "paymentDayOfMonth",
          "cardNumber",
          "cardExpMonth",
          "cardExpYear",
          "cardCvc",
        ];
      }

      if (
        this.orderInfo.payrollDeduction === "yes" &&
        this.orderInfo.isOwedFromBusiness === "no"
      ) {
        required = [
          "billingFirstName",
          "billingLastName",
          "billingPhone",
          "email",
          "shippingAddress1",
          "shippingCity",
          "shippingState",
          "shippingPostalCode",
          "employerAddress1",
          "employerCity",
          "employerState",
          "employerZip",
          "employerContactName",
          "employerName",
          "employerContactPhoneNumber",
          "totalDebt",
          "ssn",
          "married",
          "timeToCall",
          "paymentDayOfMonth",
          "cardNumber",
          "cardExpMonth",
          "cardExpYear",
          "cardCvc",
        ];
      }
      if (this.orderInfo.hasOldAddress == "yes") {
        required.push("oldAddress1", "oldCity", "oldState", "oldZip");
      }

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
      this.error.employerAddressType = false;
      this.error.employerAddress1 = false;
      this.error.employerAddress2 = false;
      this.error.employerFirstName = false;
      this.error.employerLastName = false;
      this.error.employerCity = false;
      this.error.spouseSsn = false;
      this.error.oldAddressType = false;
      this.error.oldAddress1 = false;
      this.error.oldAddress2 = false;
      this.error.oldCity = false;
      // this.error.oldState=false;
      this.error.oldZip = false;
      this.error.oldAddressType = false;
      if (this.error.paymentMonths || this.error.paymentAmount) {
        this.hasErrors = true;
      }

      this.hasErrors = this.hasErrors || !this.checkTotalDebtQualification();
      let spouseFields = ["spouseFirstName", "spouseLastName"];

      if (
        this.orderInfo.married &&
        this.orderInfo.filingJointly === marriedFilingOption.jointly
      ) {
        this.hasErrors = SiteUtils.formErrors(
          spouseFields,
          this.orderInfo,
          this.error
        );

        if (!SiteUtils.validateSpouseSsn(this.orderInfo.spouseSsn)) {
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
          this.orderInfo.spouseFirstName.toLowerCase() ===
          this.orderInfo.billingFirstName.toLowerCase()
        ) {
          this.error.spouseFirstName =
            "Your spouse’s name must be different from your own.";
          console.log("Your spouse’s name must be different from your own.");
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

        let checkbillingPostalCodeErrorMsg = SiteUtils.validateZipCode(
          this.orderInfo.billingPostalCode
        );
        if (checkbillingPostalCodeErrorMsg) {
          this.error.billingPostalCode = checkbillingPostalCodeErrorMsg;
          this.hasErrors = true;
        }

        let checkbillingCityErrorMsg = SiteUtils.validateShippingCity(
          this.orderInfo.billingCity
        );
        if (checkbillingCityErrorMsg) {
          this.error.billingCity = checkbillingCityErrorMsg;
          this.hasErrors = true;
        }

        let checkingbillingStateErrorMsg = SiteUtils.validateShippingCity(
          this.orderInfo.billingState
        );
        if (checkingbillingStateErrorMsg) {
          this.error.billingState = checkingbillingStateErrorMsg;
          this.hasErrors = true;
        }
      } else {
        SiteUtils.clearErrors(billingAddressFields, this.error);
      }

      let checkBillingFirstName = SiteUtils.validateFirstName(
        this.orderInfo.billingFirstName
      );

      if (checkBillingFirstName) {
        this.error.billingFirstName = checkBillingFirstName;
        this.hasErrors = true;
      }

      let checkBillingLastName = SiteUtils.validateLastName(
        this.orderInfo.billingLastName
      );
      if (checkBillingLastName) {
        this.error.billingLastName = checkBillingLastName;
        this.hasErrors = true;
      }

      let checkEmailAddressErrorMsg = SiteUtils.validateEmail(
        this.orderInfo.email
      );
      console.log("checkEmailAddressErrorMsg:", checkEmailAddressErrorMsg);
      if (checkEmailAddressErrorMsg) {
        this.error.email = checkEmailAddressErrorMsg;
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

      let checkShippingCityErrorMsg = SiteUtils.validateShippingCity(
        this.orderInfo.shippingCity
      );
      if (checkShippingCityErrorMsg) {
        this.error.shippingCity = checkShippingCityErrorMsg;
        this.hasErrors = true;
      }

      let checkShipingStateErrorMsg = SiteUtils.validateShippingCity(
        this.orderInfo.shippingState
      );
      if (checkShipingStateErrorMsg) {
        this.error.shippingState = checkShipingStateErrorMsg;
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

      if (
        this.orderInfo.payrollDeduction === "yes" &&
        this.orderInfo.isOwedFromBusiness === "no"
      ) {
        if (
          !this.employerAddressType &&
          !SiteUtils.validateAddress(this.orderInfo.employerAddress1)
        ) {
          this.error.employerAddress1 = true;
          this.error.employerAddressType = false;
          this.hasErrors = true;
        }

        let checkStressAddressErrorMSG = SiteUtils.validateStreetAddress(
          this.orderInfo.employerAddress1,
          this.orderInfo.employerAddress2
        );
        if (!this.employerAddressType && checkStressAddressErrorMSG) {
          this.error.employerAddress1 = checkStressAddressErrorMSG;
          this.error.employerAddressType = false;
          this.hasErrors = true;
        }

        let checkEmployerCityErrorMsg = SiteUtils.validateShippingCity(
          this.orderInfo.employerCity
        );
        if (checkEmployerCityErrorMsg) {
          this.error.employerCity = checkEmployerCityErrorMsg;
          this.hasErrors = true;
        }

        if (
          !SiteUtils.validateAddressType(
            this.employerAddressType,
            this.orderInfo.employerAddress1
          )
        ) {
          this.error.employerAddressType = true;
          this.error.employerAddress1 = false;
          this.hasErrors = true;
        }

        let checkEmployerName = SiteUtils.validateShippingCity(
          this.orderInfo.employerName
        );
        if (checkEmployerName) {
          this.error.employerName = checkEmployerName;
          this.hasErrors = true;
        }

        let checkEmployerContactName = SiteUtils.validateFirstName(
          this.orderInfo.employerContactName
        );

        if (checkEmployerContactName) {
          this.error.employerContactName = checkEmployerContactName;
          this.hasErrors = true;
        }

        let checkPhoneNumberErrorMsg = SiteUtils.validatePhone(
          this.orderInfo.employerContactPhoneNumber
        );
        if (checkPhoneNumberErrorMsg) {
          this.error.employerContactPhoneNumber = checkPhoneNumberErrorMsg;
          this.hasErrors = true;
        }

        let checkEmployerZipErrorMsg = SiteUtils.validateZipCode(
          this.orderInfo.employerZip
        );
        if (checkEmployerZipErrorMsg) {
          this.error.employerZip = checkEmployerZipErrorMsg;
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

      if (this.orderInfo.hasOldAddress == "yes") {
        if (
          !this.oldAddressType &&
          !SiteUtils.validateAddress(this.orderInfo.oldAddress1)
        ) {
          this.error.oldAddress1 = true;
          this.error.oldAddressType = false;
          this.hasErrors = true;
        }

        checkStressAddressErrorMSG = SiteUtils.validateStreetAddress(
          this.orderInfo.oldAddress1,
          this.orderInfo.oldAddress2
        );
        if (!this.oldAddressType && checkStressAddressErrorMSG) {
          this.error.oldAddress1 = checkStressAddressErrorMSG;
          this.error.oldAddressType = false;
          this.hasErrors = true;
        }

        checkShippingCityErrorMsg = SiteUtils.validateShippingCity(
          this.orderInfo.oldCity
        );
        if (checkShippingCityErrorMsg) {
          this.error.oldCity = checkShippingCityErrorMsg;
          this.hasErrors = true;
        }

        if (
          !SiteUtils.validateAddressType(
            this.oldAddressType,
            this.orderInfo.oldAddress1
          )
        ) {
          this.error.oldAddressType = true;
          this.error.oldAddress1 = false;
          this.hasErrors = true;
        }

        checkShippingPostalCodeErrorMsg = SiteUtils.validateZipCode(
          this.orderInfo.oldZip
        );
        if (checkShippingPostalCodeErrorMsg) {
          this.error.oldZip = checkShippingPostalCodeErrorMsg;
          this.hasErrors = true;
        }
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
        this.orderInfo.paymentMonths
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
        if (!parseInt(this.orderInfo.paymentMonths, 10)) {
          this.error.paymentMonths = true;
        } else {
          this.error.paymentMonths = false;
        }
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

      if (
        !SiteUtils.validateCardExpiry(
          parseInt(this.orderInfo.cardExpMonth),
          parseInt(this.orderInfo.cardExpYear)
        )
      ) {
        alert("Invalid Card Expiration!");
        return false;
      }
      this.confirmPaymentModal = true;
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
      this.orderInfoTemp.hasOldAddress = this.orderInfo.hasOldAddress === "yes";

      delete this.orderInfoTemp.isInBusiness;
      delete this.orderInfoTemp.isPayrollTax;
      delete this.orderInfoTemp.isSoleProprietorship;

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
      this.orderInfo.businessName =
        this.orderInfo.isOwedFromBusiness === "yes"
          ? this.orderInfo.businessName
          : "";
      this.orderInfo.ein =
        this.orderInfo.isOwedFromBusiness === "yes" ? this.orderInfo.ein : "";
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
      this.orderInfoTemp.hasOldAddress = this.orderInfo.hasOldAddress === "yes";
      let names = [];
      this.priorNames.forEach((it) => {
        names.push(
          `${it.firstName} ${it.middle ? it.middle : ""} ${it.lastName}`
        );
      });
      this.orderInfoTemp.priorNames = names.join(", ");
      delete this.orderInfoTemp.isInBusiness;
      delete this.orderInfoTemp.isPayrollTax;
      delete this.orderInfoTemp.isSoleProprietorship;
      delete this.orderInfoTemp.oldAddressType;

      AXIOS.put(`/ecommerce/presave`, this.orderInfoTemp)
        .then((res) => {
          if (!!res.data && !!res.data.redirectTo) {
            window.location.href = res.data.redirectTo;
            return;
          }
          let self = this;

          const { orderNum, correlationId } = res.data;

          AXIOS.put(`/paymentplan`, {
            married: this.orderInfo.married,
            filingJointly: this.orderInfo.filingJointly,
            timeToCall: this.orderInfo.timeToCall,
            paymentDayOfMonth: this.orderInfo.paymentDayOfMonth,
            spouseFirstName: this.orderInfo.spouseFirstName,
            spouseLastName: this.orderInfo.spouseLastName,
            spouseSsn: this.orderInfo.spouseSsn,
            totalDebt: this.orderInfo.totalDebt,
            isCalifornia: false,
            isNewJersey: false,
            isGeorgia: false,
            paymentMonths: this.orderInfo.paymentMonths,
            payrollDeduction: this.orderInfoTemp.payrollDeduction,
          })
            .then(() => self.postOrderInfo(orderNum, correlationId))
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

    postOrderInfo(orderNum, correlationId) {
      this.errorMessages = [];
      let orderInfo = Object.assign({}, this.orderInfo);

      this.orderInfo.billingPhone = SiteUtils.standardizePhone(
        this.orderInfo.billingPhone
      );
      this.orderInfo.ssn = SiteUtils.standardizeSsn(this.orderInfo.ssn);
      this.orderInfo.spouseSsn = SiteUtils.standardizeSsn(
        this.orderInfo.spouseSsn
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
        this.totalDebtPrevValue = this.orderInfo.totalDebt;
      }

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
      this.orderInfoTemp.hasOldAddress =
        this.orderInfoTemp.hasOldAddress === "yes";
      this.orderInfoTemp.payrollDeduction =
        this.orderInfo.payrollDeduction === "yes" &&
        this.orderInfo.isOwedFromBusiness === "no";
      let payrollDeductionValue = this.orderInfoTemp.payrollDeduction ? 12 : 0;
      this.orderInfoTemp.amount = (this.total * 100).toString();
      let names = [];
      this.priorNames.forEach((it) => {
        names.push(
          `${it.firstName} ${it.middle ? it.middle : ""} ${it.lastName}`
        );
      });
      this.orderInfoTemp.priorNames = names.join(", ");
      delete this.orderInfoTemp.isInBusiness;
      delete this.orderInfoTemp.isPayrollTax;
      delete this.orderInfoTemp.isSoleProprietorship;
      delete this.orderInfoTemp.oldAddressType;
      console.log("Amount = " + orderInfo.amount);
      if (orderInfo.amount == 0) {
        let orderForPdf = Object.assign({}, this.orderInfoTemp);
        orderForPdf.orderNumber = orderNum;
        AXIOS.post("/pdforderversion/refreshOrder", orderForPdf)
          .then((res) => {
            window.location.href = "/order-confirmation/" + correlationId;
          })
          .catch((error) => {
            console.log("pdf error", error);
            alert(
              "PDF error or unexpected error has occurred. Please try again later."
            );
          });
      } else {
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
      }
    },
    getShippingAddressData: function (addressData, placeResultData, id) {
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
      this.showAddressTextBox = false;
    },
    getEmployerAddressData: function (addressData, placeResultData, id) {
      this.orderInfo.employerAddress1 = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;
      this.orderInfo.employerCity = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;
      this.orderInfo.employerState = addressData.administrative_area_level_1
        ? addressData.administrative_area_level_1
        : "";
      this.orderInfo.employerZip = addressData.postal_code
        ? addressData.postal_code
        : "";
      this.showEmployerAddressTextBox = false;
    },
    extractShippingAddress: function () {
      google.maps.places.Autocomplete(
        this.getShippingAddressData,
        "place_changed"
      );
      //this.showAddressTextBox=false;
    },
    extractEmployerAddress: function () {
      google.maps.places.Autocomplete(
        this.getEmployerAddressData,
        "place_changed"
      );
      //this.showEmployerAddressTextBox=false;
    },
    changeToAddressGoogle: function () {
      if (!this.addressType) {
        this.showAddressTextBox = true;
      }
    },
    changeToEmployerAddressGoogle: function () {
      if (!this.employerAddressType) {
        this.showEmployerAddressTextBox = true;
      }
    },
    changeToShortAddress: function () {
      this.showAddressTextBox = false;
    },
    changeToShortEmployerAddress: function () {
      this.showEmployerAddressTextBox = false;
    },
    setShippingAddress: function (address, type) {
      this.orderInfo.shippingAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
      this.orderInfo.billingAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
    },
    setEmployerAddress: function (address, type) {
      this.orderInfo.employerAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
    },
    updateProcessingFee: function (event) {
      const { value } = event.target;
      this.processingFee = value === "Deluxe" ? this.customExpressOptionFee : 0;
    },
    updatePayrollDeduction: function (event) {
      const { value } = event.target;
      this.payrollDeductionFee =
        value === "yes" && this.orderInfo.isOwedFromBusiness === "no"
          ? this.customPayrollFee
          : 0;
    },
    updateIsOwedFromBusiness: function (event) {
      const { value } = event.target;

      if (value === "yes") {
        this.payrollDeductionFee = 0;
      } else {
        this.payrollDeductionFee =
          this.orderInfo.payrollDeduction === "yes" ? this.customPayrollFee : 0;
      }
    },
    updateHasOldAddress: function (event) {
      const { value } = event.target;

      if (value === "yes") {
        this.changeOfAddressFee = this.customChangeOfAddressFee;
      } else {
        this.changeOfAddressFee = 0;
      }
    },
    setOldShippingAddress: function (address, type) {
      this.orderInfo.oldAddress1 = address.newVal
        ? address.newVal.split(",")[0]
        : "";
      // this.orderInfo.billingAddress1=address.newVal?address.newVal.split(',')[0]:'';
    },
    getOldShippingAddressData: function (addressData, placeResultData, id) {
      console.log("Address => ", addressData);
      this.orderInfo.oldAddress1 = addressData.street_number
        ? addressData.street_number + " " + addressData.route
        : addressData.route;
      this.orderInfo.oldCity = addressData.locality
        ? addressData.locality
        : placeResultData.vicinity
        ? placeResultData.vicinity
        : placeResultData.address_components[3].long_name;
      this.orderInfo.oldState = addressData.administrative_area_level_1
        ? addressData.administrative_area_level_1
        : "";
      this.orderInfo.oldZip = addressData.postal_code
        ? addressData.postal_code
        : "";
      this.showOldAddressTextBox = false;
    },
    extractOldShippingAddress: function () {
      google.maps.places.Autocomplete(
        this.getOldShippingAddressData,
        "place_changed"
      );
      //this.showAddressTextBox=false;
    },
    changeToOldAddressGoogle: function () {
      if (!this.oldAddressType) {
        this.showOldAddressTextBox = true;
      }
    },
    addPriorName() {
      this.priorNames.push({
        firstName: "",
        middle: "",
        lastName: "",
      });
    },
    initializeSalesWidget() {
      AXIOS.get(`/admin/salesList`).then((res) => {
        if (res.status == 200) {
          let list = res.data;
          let index = list.findIndex((it) => {
            let condition_1 =
              it.onOff === true && it.product == "IRS Payment Plan";
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
      AXIOS.get(`/admin/productPrice?product_name=IRS Payment Plan`).then((res) => {
        if (res.status == 200) {
          console.log(res.data);

          this.customChangeOfAddressFee = res.data.customChangeOfAddressFee;
          this.customEnrollmentFee = res.data.customEnrollmentFee;
          this.customExpressOptionFee = res.data.customExpressOptionFee;
          this.customPayrollFee = res.data.customPayrollFee;
          this.enrollmentFee = res.data.enrollmentFee;

          // let productList = res.data;
          // let IRSProducts = productList.filter((it) => {
          //   return it.product == "IRS Payment Plan";
          // });

          // for (let i = 0; i < IRSProducts.length; i++) {
          //   let price = -1;
          //   let priceItem = IRSProducts[i];
          //   let current = new Date().getTime();
          //   let additionalTimeframes = priceItem.additionalTimeframes
          //     ? JSON.parse(priceItem.additionalTimeframes)
          //     : [];
          //   if (priceItem.timePeriodsStart && !priceItem.timePeriodsEnd) {
          //     if (current > new Date(priceItem.timePeriodsStart).getTime()) {
          //       price = priceItem.changePrice;
          //     } else {
          //       price = priceItem.currentPrice;
          //     }
          //   } else if (
          //     !priceItem.timePeriodsStart &&
          //     priceItem.timePeriodsEnd
          //   ) {
          //     if (current < new Date(priceItem.timePeriodsEnd).getTime()) {
          //       price = priceItem.changePrice;
          //     } else {
          //       price = priceItem.currentPrice;
          //     }
          //   } else if (priceItem.timePeriodsStart && priceItem.timePeriodsEnd) {
          //     if (
          //       current < new Date(priceItem.timePeriodsEnd).getTime() &&
          //       current > new Date(priceItem.timePeriodsStart).getTime()
          //     ) {
          //       price = priceItem.changePrice;
          //     } else {
          //       price = priceItem.currentPrice;
          //     }
          //   }

          //   let additionalIndex = additionalTimeframes.findIndex((it) => {
          //     let startTime = new Date(it.startDate).getTime();
          //     let endTime = new Date(it.endDate).getTime();

          //     if (it.startDate && !it.endDate) {
          //       return current > startTime;
          //     } else if (!it.startDate && it.endDate) {
          //       return current < endTime;
          //     } else if (it.startDate && it.endDate) {
          //       return current < endTime && current > startTime;
          //     }
          //   });
          //   if (additionalIndex > -1) {
          //     price = Number(additionalTimeframes[additionalIndex].price);
          //   }
          //   if (priceItem.isPrimary == true) {
          //     if (price != -1) {
          //       this.customEnrollmentFee = price;
          //       this.enrollmentFee = price;
          //     }
          //   } else {
          //     if (priceItem.subOptions == "change_of_address") {
          //       if (price != -1) {
          //         this.customChangeOfAddressFee = price;
          //       }
          //     } else if (priceItem.subOptions == "express") {
          //       console.log("here");
          //       console.log(additionalTimeframes);
          //       if (price != -1) {
          //         this.customExpressOptionFee = price;
          //       }
          //     } else if (priceItem.subOptions == "payroll") {
          //       if (price != -1) {
          //         this.customPayrollFee = price;
          //       }
          //     }
          //   }
          // }
        }
      });
    },
  },
  computed: {
    paymentPlan: function () {
      return SiteUtils.calculatePaymentPlan(
        this.orderInfo.totalDebt,
        this.orderInfo.paymentMonths
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
          this.orderInfo.paymentMonths
        ).paymentAmount;
        if (amounts.length > 1) {
          amount =
            "$" +
            amount +
            " - $" +
            SiteUtils.calculatePaymentPlan(
              amounts[1],
              this.orderInfo.paymentMonths
            ).paymentAmount;
        }

        return amount;
      } else {
        return "$" + this.paymentAmount;
      }

      return "...";
    },
    isInBusinessVisible: function () {
      return this.orderInfo.isOwedFromBusiness === "yes";
    },
    isPayrollOptionVisible: function () {
      return (
        this.orderInfo.payrollDeduction === "yes" &&
        this.orderInfo.isOwedFromBusiness === "no"
      );
    },
    isPayrollTaxVisible: function () {
      const totalDebt =
        this.orderInfo.totalDebt &&
        Number(this.orderInfo.totalDebt.toString().replace(/[^0-9\.]/g, ""));
      const { isOwedFromBusiness, isInBusiness } = this.orderInfo;

      if (isOwedFromBusiness === "no") {
        return false;
      }
      if ((totalDebt <= 25000 && isInBusiness === "no") || totalDebt > 25000) {
        return false;
      }
      return true;
    },
    isSoleProprietorshipVisible: function () {
      const totalDebt =
        this.orderInfo.totalDebt &&
        Number(this.orderInfo.totalDebt.toString().replace(/[^0-9\.]/g, ""));
      const { isOwedFromBusiness } = this.orderInfo;

      if (isOwedFromBusiness === "no" || totalDebt <= 25000) {
        return false;
      }
      return true;
    },
    isBusinessLogicAcceptable: function () {
      const totalDebt =
        this.orderInfo.totalDebt &&
        Number(this.orderInfo.totalDebt.toString().replace(/[^0-9\.]/g, ""));
      const {
        isOwedFromBusiness,
        isInBusiness,
        isPayrollTax,
        isSoleProprietorship,
      } = this.orderInfo;

      if (isOwedFromBusiness === "no") {
        return false;
      }
      if (
        totalDebt <= 25000 &&
        isInBusiness === "yes" &&
        isPayrollTax === "yes"
      ) {
        return false;
      }
      if (
        totalDebt > 25000 &&
        (isInBusiness === "yes" || isSoleProprietorship === "no")
      ) {
        return false;
      }
      return true;
    },
    subTotal: function () {
      return (
        this.enrollmentFee +
        this.processingFee +
        this.payrollDeductionFee +
        this.changeOfAddressFee
      );
    },
    total: function () {
      return this.subTotal - this.discount;
    },
    discount: function () {
      return this.subTotal * (this.percentageOff / 100);
    },
  },
  mounted() {
    this.initializeCardinal();
    window.ANS_customer_id = "d7332b5b-b0c5-4072-9237-bef8a02352ae";
    this.initializeSalesWidget();
    this.initializePricings();
    let self = this;
    AXIOS.get(`/ecommerce/order`)
      .then((response) => {
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
        //this.generatePaymentAmount();

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
  watch: {
    hadAnyPriorNames: function () {
      this.orderInfo.hasPriorNames = this.hadAnyPriorNames;
    },
    priorNames: function () {
      let names = [];
      this.priorNames.forEach((it) => {
        names.push(
          `${it.firstName} ${it.middle ? it.middle : ""} ${it.lastName}`
        );
      });
      this.orderInfo.priorNames = names.join(", ");
    },
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
.employer-information-form {
  background-color: #f5f5f5;
  padding: 20px 0px;
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

.processing-select {
  max-width: 80%;
  display: inline-block;
  margin-right: 10px;
}

.btn-add-prior-name {
  border: 1px solid #ced4da;
  width: 30px;
  height: 30px;
  border-radius: 50%;
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
