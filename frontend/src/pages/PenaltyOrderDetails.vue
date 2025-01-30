<template>
  <page logoHref="/admin/orders" class="home-page">
    <loader :isLoaderOn="loaderOn"></loader>
    <div class="container">
      <b-modal
        ref="userModel"
        title="Attention Required!"
        hide-footer
        no-close-on-backdrop
      >
        <div class="row">
          <div class="col-12">
            By refreshing this order it will create new application for this
            order. are you sure to do this ?
          </div>
        </div>
        <hr />
        <div class="col col-6">
          <button class="btn btn-primary" @click="onConfirmRefresh()">
            Refresh
          </button>
          <button
            class="btn btn-outline-secondary"
            @click="closeRefreshDialog()"
          >
            Close
          </button>
        </div>
      </b-modal>

      <div
        class="d-flex flex-row justify-content-start mt-4 align-middle"
        style="gap: 32px"
      >
        <div>
          <button
            class="btn btn-secondary font-weight-bold"
            v-on:click="backToResults"
            :disabled="disableButtons"
          >
            &lt; Back to<br class="hide-br" />&nbsp;&nbsp; Results
          </button>
        </div>
        <div class="d-flex flex-column" style="font-size: 20px; color: black">
          <div><b>Order #: </b> {{ record.id }}</div>
          <div>
            <b>Product: </b>
            <span>Penalty Waiver</span>
          </div>
        </div>

        <div class="mt-3">
          <button
            :class="['btn', 'btn-secondary', 'font-weight-bold']"
            @click="isEditMode = true"
            :disabled="disableButtons || record.status == 'Charge Back' || record.status == 'Cancelled'"
            v-if="editRecordAuth"
          >
            <!-- <font-awesome-icon :icon="['fa', 'pencil-alt']" />-->
            Edit
          </button>
        </div>
        <div class="mt-3">
          <button
            class="btn btn-warning font-weight-bold"
            @click="saveOrderDetails()"
            :disabled="disableButtons || !isEditMode"
          >
            Save Order Details
          </button>
          <div class="text-success" v-if="addressesSaved">Details Saved</div>
        </div>
      </div>
      <hr />

      <div class="row">
        <div
          class="d-flex col-12 col-sm-6 col-md-6 flex-column"
          style="border-right: 4px solid black; display: none"
        >
          <div class="d-flex mb-3">
            <h3>Basic Info</h3>
          </div>
          <div class="d-flex mb-3 align-middle">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Submitted Date:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              {{ record.createdDate }}
            </div>
          </div>
          <div class="d-flex mb-3 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Customer IP:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              {{ record.customerIpAddress }}
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>USPS Tracking Number:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                class="usps-tracking-input form-control"
                @click="goToTracking()"
                :value="record.trackingNumber"
                readonly="readonly"
                v-if="!isEditMode"
              />
              <input
                class="form-control"
                type="text"
                v-model.trim="record.trackingNumber"
                v-if="isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>First Name:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.firstName"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Last Name:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.lastName"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Email:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.email"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Phone:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.billingPhone"
                :disabled="!isEditMode"
              />
            </div>
          </div>

          <hr />

          <div class="d-flex mb-3">
            <h3>Internal Note</h3>
          </div>
          <div class="d-flex mb-2 align-middle row">
            <div class="d-flex col-12 col-sm-8 col-md-8">
              <div class="col-5 pl-0">
                <button
                  class="btn btn-custome-rounded btn-blue pl-2 pr-2 text-dark"
                  @click="isAddNote = true"
                  v-if="!isAddNote">
                  <b>+ADD A NOTE</b>
                </button>
                <button
                  class="btn btn-custome-rounded btn-round-green pl-2 pr-2 text-dark"
                  @click="saveNewNote()"
                  v-if="isAddNote">
                  <b>SAVE NOTE</b>
                </button>
              </div>
              <div class="col-4" />
              <div class="col-2 pl-0">
                <button
                  class="btn btn-custome-rounded btn-red pl-2 pr-2 text-dark"
                  @click="cancelAddANote()"
                  v-if="isAddNote">
                  <b>CANCEL</b>
                </button>
              </div>
            </div>
          </div>

          <div class="d-flex mb-2 align-middle row">
            <div class="col-12 col-sm-8 col-md-8">
              <div class="mb-3">
                <input
                  type="text"
                  class="form-control"
                  placeholder="Please add your note."
                  v-model="manual_note"
                  :disabled="!isAddNote"
                />
              </div>
            </div>

            <div
              class="col-12 col-sm-8 col-md-8"
              v-for="(item, index) in detailNoteList"
              :key="index"
            >
              <div class="mb-1" v-if="item.type == 'MANUAL'">
                {{ item.content }}
              </div>
              <div class="mb-1" v-else>
                {{ item.content }} - {{ item.type }}
              </div>

              <div
                v-if="item.content && item.user"
                class="mb-3"
                style="font-size: 11px"
              >
                Date: {{ formatDate(item.created) }} + {{ item.user }}
              </div>
            </div>
          </div>

          <hr />

          <div class="d-flex mb-3">
            <h3>Transaction Info</h3>
          </div>
          <div class="d-flex mb-2 align-middle">
            <table style="width: 100%">
              <tr>
                <td class="font-weight-bold" colspan="2">
                  Basic Information
                </td>
              </tr>
              <tr>
                <td>
                  Charge Amount:
                </td>
                <td>${{ parseInt(record.amount, 10) / 100 }}</td>
              </tr>
              <tr>
                <td>
                  Card Brand:
                </td>
                <td>
                  {{ record.cardBrand }}
                </td>
              </tr>
              <tr>
                <td>Last Four of Card Number</td>
                <td>{{record.last4DigitsCard}}</td>
              </tr>

              <tr>
                <td>Original Payment Date</td>
                <td>{{record.createdDate}}</td>
              </tr>

              <tr>
                <td class="font-weight-bold" colspan="2">Refund Information</td>
              </tr>
              <tr>
                <td>Voided</td>
                <td>{{ record.status != 'Cancelled' ? '' : record.refundDate ? '' :  'Yes' }}</td>
              </tr>
              <tr>
                <td>Refunded</td>
                <td>{{ record.status == 'Cancelled' && !record.refundDate ? '' : record.refundDate ? 'Yes' :  ''}}</td>
              </tr>


              <tr>
                <td>Refund Orbital Transaction ID</td>
                <td>{{ record.refundAuthTransId }}</td>
              </tr>

              <tr>
                <td>Refund Date</td>
                <td>{{ record.refundDate != null ? new Date(record.refundDate).toLocaleDateString() : '' }}</td>
              </tr>

              <tr>
                <td class="font-weight-bold" colspan="2">
                  Orbital Information
                </td>
              </tr>

              <tr>
                <td>Orbital Original Transaction ID</td>
                <td>{{record.orbitalTransactionNumber}}</td>
              </tr>
              <tr>
                <td>Orbital Decline Reason</td>
                <td>{{record.authorizeErrorMessage}}</td>
              </tr>
              <tr>
                <td>Orbital Error Code</td>
                <td>{{ record.authorizeErrorCode == "00" ? "" : record.authorizeErrorCode }}</td>
              </tr>
              <tr>
                <td class="font-weight-bold" colspan="2">Cardinal 3D Secure Information</td>
              </tr>
              <tr>
                <td>Cardinal Trans ID</td>
                <td>{{ record.transactionId }}</td>
              </tr>
              <tr>
                <td>ECI Code</td>
                <td>{{record.eciFlag}}</td>
              </tr>
              <tr>
                <td>Authentication Result</td>
                <td>{{record.authorizeErrorMessage}}</td>
              </tr>
              <tr>
                <td>Cardinal Response</td>
                <td>{{ record.authorizeErrorMessage == "Success" ? "YAY" : ""}}</td>
              </tr>
            </table>
          </div>
        </div>

        <div class="d-flex col-12 col-sm-6 col-md-6 flex-column">
          <div class="d-flex mb-3">
            <h3>Status</h3>
          </div>
          <div class="d-flex flex-column mb-2 align-middle">
            <div class="d-flex mb-3">
              <b class="mt-2">{{ record.status }}</b>
              <div class="ml-3"
                v-if="
                (record.status !== 'Cancelled') &&
                (loggedInUser.userType == 'admin' ||
                (loggedInUser.userType == 'StandardUser' && loggedInUser.manualStatusChange))
              ">
                <select
                  class="form-control"
                  v-model="manualStatus"
                  @change="$refs.manualStatusChangeModal.show()"
                >
                  <option value="" disabled selected hidden>Change Status...</option>
                  <option value="Cancelled">Cancelled</option>
                </select>
              </div>
            </div>
            <div class="d-flex" style="gap: 20px">
              <div v-if="record.status !== 'Cancelled'">
                <button
                  class="btn btn-secondary font-weight-bold"
                  @click="showHideCancelOrderConfirmation(true)"
                  v-if="statusChangeAuth"
                  :disabled="isChargeBackAvailable(record.status)"
                >
                  Cancel <span v-if="record.amount !== '0'">and Refund</span>
                </button>
              </div>
              <div
                v-if="
                  record.status !== 'Cancelled' &&
                  record.status !== 'Incomplete'
                "
              >
                <button
                  class="btn btn-secondary font-weight-bold"
                  @click="clickOnHoldButton()"
                  v-if="statusChangeAuth"
                  :disabled="disableButtons && !isEditMode"
                >
                  {{
                    record.status === "On Hold" ? "Remove Hold" : "Put On Hold"
                  }}
                </button>
              </div>
              <div
                v-if="record.amount !== '0' && record.status !== 'Incomplete'"
              >
                <button
                  class="btn btn-secondary font-weight-bold"
                  @click="clickOnChargebackButton()"
                  :disabled="isChargeBackAvailable(record.status)"
                  v-if="
                    loggedInUser.userType == 'admin' ||
                    (loggedInUser.userType == 'StandardUser' &&
                      loggedInUser.chargebackAuth)
                  "
                >
                  Chargeback
                </button>
              </div>
            </div>
          </div>

          <hr />

          <div class="d-flex mb-3">
            <h3>Customer Documents</h3>
          </div>
          <div class="d-flex flex-column mb-2 align-middle">
            <div
              v-if="pdfOrderVersions && pdfOrderVersions.length > 0"
              class="d-flex mb-3"
              style="gap: 20px"
            >
              <div
                style="color: #2e85f5; cursor: pointer"
                @click="
                  pdfVersion(pdfOrderVersions[pdfOrderVersions.length - 1])
                "
              >
                <u>
                  <b
                    >{{
                      pdfOrderVersions &&
                      pdfOrderVersions[pdfOrderVersions.length - 1].versionId
                    }}.pdf</b
                  ></u
                >
              </div>
              <div>
                {{
                  pdfOrderVersions &&
                  pdfOrderVersions[pdfOrderVersions.length - 1].createdDate
                }}
              </div>
            </div>

            <div class="d-flex mb-3">
              <button
                @click="emailPdfCopy()"
                class="btn btn-secondary font-weight-bold"
              >
                Email Customer Copy >
              </button>
              <div v-if="record.status == 'Completed' && record.trackingNumber">
                <button
                  class="btn btn-secondary font-weight-bold ml-2"
                  @click="clickOnPhysicalCopy()"
                >
                  Mail New Physical Copy
                </button>
              </div>
            </div>

            <div class="d-flex">Previous Versions:</div>

            <div v-if="!pdfOrderVersions || pdfOrderVersions.length <= 1">
              <i>No PDF version found</i>
            </div>
            <div v-if="pdfOrderVersions && pdfOrderVersions.length > 1">
              <div
                class="d-flex"
                style="gap: 20px"
                v-for="(version, index) in pdfOrderVersions.slice().reverse()"
                :key="version.createdDate"
              >
                <div
                  v-if="index != 0"
                  style="color: #2e85f5; cursor: pointer"
                  @click="pdfVersion(version)"
                >
                  <u
                    ><b>{{ version.versionId }}.pdf</b></u
                  >
                </div>
                <div v-if="index != 0">
                  {{ version.createdDate }}
                </div>
              </div>
            </div>
          </div>

          <hr />

          <div class="d-flex mb-3">
            <h3>Mailing Address</h3>
          </div>

          <div class="d-flex flex-column mb-2 align-middle">
            <div class="d-flex">
              <input
                type="text"
                class="form-control"
                v-model="record.shippingAddress1"
                :disabled="!isEditMode"
              />
            </div>
            <div class="d-flex">
              <input
                type="text"
                class="form-control"
                v-model="record.shippingAddress2"
                :disabled="!isEditMode"
              />
            </div>
            <div class="d-flex flex-row">
              <input
                type="text"
                class="form-control"
                v-model="record.shippingCity"
                :disabled="!isEditMode"
              />
              <input
                type="text"
                class="form-control"
                v-model="record.shippingState"
                :disabled="!isEditMode"
              />
              <input
                type="text"
                class="form-control"
                v-model="record.shippingPostalCode"
                :disabled="!isEditMode"
              />
            </div>
          </div>

          <hr />

          <div class="d-flex mb-3">
            <h3>Additional Details</h3>
          </div>

          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Penalty Amount Waived:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.penaltyAmountWaived"
                :disabled="!isEditMode"
              />
            </div>
          </div>

          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Penalty Type Waived:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <select
                class="form-control"
                v-model="record.penaltyWaivedType"
                :disabled="!isEditMode"
              >
                <option value="" style="font-style: italic; color: gray">
                  Select
                </option>
                <option
                  v-for="(type, i) in penaltyTypes"
                  :key="i"
                  :value="type.name"
                >
                  {{ type.description }}
                </option>
              </select>
            </div>
          </div>

          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Penalty Year Waived:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.penaltyWaivedYear"
                :disabled="!isEditMode"
              />
            </div>
          </div>

          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Include 843?:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input type="checkbox" v-model="record.isPenaltyPaid" disabled />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Primary SSN:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.ssn"
                :disabled="!isEditMode"
                v-mask="'###-##-####'"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Married?:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="checkbox"
                v-model="record.married"
                :disabled="!isEditMode"
                class="mr-1"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Spouse First Name:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.spouseFirstname"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Spouse Last Name:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.spouseLastname"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>Spouse SSN:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                v-model="record.spouseSsn"
                :disabled="!isEditMode"
                v-mask="'###-##-####'"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6">
              <b>IRS Address for Letter:</b>
            </div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                placeholder="Address"
                v-model="record.irsAddress1"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6"></div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                placeholder="City"
                v-model="record.irsCity"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6"></div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                placeholder="State"
                v-model="record.irsState"
                :disabled="!isEditMode"
              />
            </div>
          </div>
          <div class="d-flex mb-2 align-items-center">
            <div class="col-12 col-sm-6 col-md-6"></div>
            <div class="col-12 col-sm-6 col-md-6">
              <input
                type="text"
                class="form-control"
                placeholder="Zipcode"
                v-model="record.irsZipcode"
                :disabled="!isEditMode"
              />
            </div>
          </div>

          <div class="d-flex flex-column mb-2 align-middle"></div>
          <hr />
          <div class="d-flex mb-3">
            <h3>Billing Address</h3>
          </div>

          <div class="d-flex flex-column mb-2 align-middle">
            <div class="d-flex">
              <input
                type="text"
                class="form-control"
                v-model="record.billingAddress1"
                :disabled="!isEditMode"
              />
            </div>
            <div class="d-flex">
              <input
                type="text"
                class="form-control"
                v-model="record.billingAddress2"
                :disabled="!isEditMode"
              />
            </div>
            <div class="d-flex flex-row">
              <input
                type="text"
                class="form-control"
                v-model="record.billingCity"
                :disabled="!isEditMode"
              />
              <input
                type="text"
                class="form-control"
                v-model="record.billingState"
                :disabled="!isEditMode"
              />
              <input
                type="text"
                class="form-control"
                v-model="record.billingPostalCode"
                :disabled="!isEditMode"
              />
            </div>
          </div>
        </div>
      </div>

      <br />
      <div class="row" style="display: none">
        <div class="col" style="border-right: 4px solid black">
          <br />
          <div v-if="record != null">
            <div class="row" v-if="record.partnerCode">
              <label class="col-6 col-form-label font-weight-bold"
                >Promo Code:</label
              >
              <div class="col col-6 py-2">
                {{ record.partnerCode }}
              </div>
            </div>
            <br />
            <br />
            <br />
            <br />
            <br />
          </div>
        </div>

        <div class="col-12 col-sm-12 col-md-6">
          <div v-if="record != null">
            <div v-if="record.product === 'OfferInCompromise'">
              <h2 class="mt-4">Offer In Compromise</h2>
              <admin-offer-in-compromise
                :orderNum="record.orderNum"
              ></admin-offer-in-compromise>
            </div>

            <div v-if="record.product === 'TaxLienRemoval'">
              <h2 class="mt-4">Tax Lien Removal</h2>
              <admin-tax-lien-removal
                :orderNum="record.orderNum"
                ref="taxlienRemoval"
                :isEditMode="isEditMode"
              ></admin-tax-lien-removal>
            </div>
          </div>
        </div>
      </div>
    </div>

    <b-modal
      ref="transactionDetailsModal"
      hide-footer
      title="Transaction Details"
      v-if="transactionStatus != null"
    >
      <div class="row">
        <div class="col" v-if="voidRefundAuth">
          <button
            v-if="
              !['settledSuccessfully', 'voided'].includes(
                transactionStatus.transactionStatus
              )
            "
            class="btn btn-warning"
            @click="voidTransaction()"
            :disabled="disableButtons || record.status == 'Charge Back'"
          >
            Void
          </button>
          <button
            v-if="transactionStatus.transactionType != 'refundTransaction'"
            class="btn btn-danger"
            @click="refundTransaction()"
            :disabled="disableButtons || record.status == 'Charge Back'"
          >
            Refund
          </button>
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold"
          >Transaction Type</label
        >
        <div class="col">
          {{ transactionStatus.transactionType }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold"
          >Transaction Status</label
        >
        <div class="col">
          {{ transactionStatus.transactionStatus }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold"
          >Card Number (masked)</label
        >
        <div class="col">
          {{ transactionStatus.cardNumberMasked }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold">Auth Amount</label>
        <div class="col">
          {{ transactionStatus.authAmount }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold"
          >Settlement Amount</label
        >
        <div class="col">
          {{ transactionStatus.settlementAmount }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold">Auth Code</label>
        <div class="col">
          {{ transactionStatus.authCode }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold">AVS Response</label>
        <div class="col">
          {{ transactionStatus.avsResponse }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold"
          >Card Code Response</label
        >
        <div class="col">
          {{ transactionStatus.cardCodeResponse }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold">CAVV Response</label>
        <div class="col">
          {{ transactionStatus.cavvResponse }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold"
          >Response Reason Code</label
        >
        <div class="col">
          {{ transactionStatus.responseReasonCode }}
        </div>
      </div>
      <div class="row">
        <label class="col col-form-label font-weight-bold"
          >Response Reason Description</label
        >
        <div class="col">
          {{ transactionStatus.responseReasonDescription }}
        </div>
      </div>
    </b-modal>

    <b-modal
      ref="trackingNumberModal"
      hide-footer
      title="Update Tracking Number"
    >
      <div class="row">
        <div class="col">
          <p>Would you like to send an email notification to the customer?</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button class="btn btn-success" @click="saveTrackingNumber(true)">
            Send Notification
          </button>
          <button class="btn btn-warning" @click="saveTrackingNumber(false)">
            Don't Send
          </button>
        </div>
      </div>
    </b-modal>
    <b-modal ref="backConfirmation" hide-footer title="Alert">
      <div class="row">
        <div class="col">
          <p>Are you sure? Edits are not saved!</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button
            class="btn btn-success pl-4 pr-4 text-dark"
            @click="backToResults(true)"
          >
            <b>YES</b>
          </button>
          <button
            class="btn btn-warning pl-4 pr-4 text-dark"
            @click="backToResults(false)"
          >
            <b>NO</b>
          </button>
        </div>
      </div>
    </b-modal>

    <b-modal ref="physicalCopyModal" hide-footer :title="'New Physical Copy'">
      <div class="row">
        <div class="col">
          <p>Would you like to send an email notification to the customer?</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button class="btn btn-success" @click="physicalCopy(true)">
            Send Notification
          </button>
          <button class="btn btn-warning" @click="physicalCopy(false)">
            Don't Send
          </button>
        </div>
      </div>
    </b-modal>

    <b-modal
      ref="offHoldModal"
      hide-footer
      :title="record.status === 'On Hold' ? 'Take Off Hold' : 'Put On Hold'"
    >
      <div class="row">
        <div class="col">
          <p>Would you like to send an email notification to the customer?</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button class="btn btn-success" @click="putOnHold(true)">
            Send Notification
          </button>
          <button class="btn btn-warning" @click="putOnHold(false)">
            Don't Send
          </button>
        </div>
      </div>
    </b-modal>

    <b-modal ref="cbStatusModal" hide-footer title="Chargeback Status">
      <div class="row">
        <div class="col">
          <p>Is this a CDRN or Chase Chargeback?</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button class="btn btn-success" @click="chargebackCDRN()">CDRN</button>
          <button class="btn btn-warning" @click="chargebackChase()">CHASE</button>
        </div>
      </div>
    </b-modal>

    <b-modal
      ref="emailNotificationModel"
      hide-footer
      :title="emailNotificationModelData.title"
    >
      <div class="row">
        <div class="col">
          <p>{{ emailNotificationModelData.content }}</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button class="btn btn-success" @click="sendEmailNotification(true)">
            {{ emailNotificationModelData.sendButtonText }}
          </button>
          <button class="btn btn-warning" @click="sendEmailNotification(false)">
            {{ emailNotificationModelData.cancelButtonText }}
          </button>
        </div>
      </div>
    </b-modal>

    <b-modal ref="cancelOrderModel" hide-footer title="Cancel Order">
      <div class="row">
        <div class="col">
          <p>Are you sure?</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button
            class="btn btn-success ml-1 float-right"
            @click="cancelOrder()"
          >
            Yes
          </button>
          <button
            class="btn btn-warning float-right"
            @click="showHideCancelOrderConfirmation(false)"
          >
            No
          </button>
        </div>
      </div>
    </b-modal>

    <b-modal ref="manualStatusChangeModal" hide-footer title="Manual Status Change" @hidden="manualStatusChangeModalHidden()">
      <div class="row">
        <div class="col">
          <p>You are manually changing a status, are you sure?</p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <button class="btn btn-success ml-1 float-right" @click="manualStatusChange()">
            Yes
          </button>
          <button
            class="btn btn-warning float-right"
            @click="hideManualStatusChangeModal()"
          >
            No
          </button>
        </div>
      </div>
    </b-modal>
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
import { methods } from "../shared/methods";
import { clone } from "lodash";
import moment from "moment";
import { PENALTY_TYPES } from "../scripts/constants";

export default {
  data() {
    return {
      isEditMode: false,
      isAddNote: false,
      trackingNumberSaved: false,
      notificationSent: false,
      showSsn: false,
      record: "",
      pastRecord: {},
      paymentPlanDetails: null,
      transactionStatus: null,
      disableButtons: false,
      notesSaved: false,
      addressesSaved: false,
      loggedInUser: "",
      ssnToShow: "",
      statusChangeAuth: false,
      editRecordAuth: false,
      voidRefundAuth: false,
      pdfOrderVersions: null,
      loaderOn: false,
      detailNoteList: [],
      manual_note: "",
      put_on_hold: "",
      emailNotificationModelData: {
        title: "",
        content: "",
        sendButtonText: "Send Notification",
        cancelButtonText: "Don’t Send",
      },
      envelopeTrackingNumber: "",
      manualStatus: "",
    };
  },
  methods: {
    clickOnPhysicalCopy() {
      this.$refs.physicalCopyModal.show();
    },
    physicalCopy(sendNotification) {
      let self = this;
      console.log("[new physical copy clicked!]", sendNotification);
      self.disableButtons = true;
      AXIOS.put(
        `/admin/penalty-order/id/${this.$route.params.id}/physicalCopy`,
        {
          sendNotification: sendNotification,
          orderNum: this.record.orderNum,
          type: "SYSTEM",
          content: `ORIGINAL TRACKING: ${this.record.trackingNumber}`,
          user: self.loggedInUser.username,
        }
      )
        .then((response) => {
          self.disableButtons = false;
          self.$refs.physicalCopyModal.hide();
          self.refresh();
        })
        .catch((e) => {
          alert("Unable to change status.");
          self.disableButtons = false;
        });
    },
    saveTrackingNumber(sendNotification) {
      self.disableButtons = true;
      AXIOS.put(`/admin/order/id/${this.record.orderNum}/trackingNumber`, {
        trackingNumber: this.record.trackingNumber,
        sendNotification: sendNotification,
      })
        .then((response) => {
          self.disableButtons = false;
          this.trackingNumberSaved = response.data.saved;
          this.notificationSent = response.data.notificationSent;
          this.refresh();
        })
        .catch((e) => {
          self.disableButtons = false;
          alert("Unable to update tracking number!");
          this.refresh();
        });

      this.$refs.trackingNumberModal.hide();
    },
    sendEmailNotification(sendNotification) {
      if (sendNotification) {
        this.disableButtons = true;
        AXIOS.post(
          `/admin/penalty-order/id/${this.record.id}/sendEmailNotification`
        )
          .then((response) => {
            this.disableButtons = false;
            alert("Email sent!");
          })
          .catch((e) => {
            this.disableButtons = false;
            alert("Unable to send email!");
          });
      }
      this.$refs.emailNotificationModel.hide();
    },

    openTrackingNumberModal() {
      this.$refs.trackingNumberModal.show();
    },
    onRefresh() {
      this.$refs.userModel.show();
    },
    closeRefreshDialog() {
      this.$refs.userModel.hide();
    },
    onConfirmRefresh() {
      let self = this;
      console.log("before map record = ", this.record);
      let orderInfo = this.getRecord(this.record, null);
      orderInfo.orderNumber = this.record.orderNum;
      // orderInfo.totalDebt = this.$refs.paymentPlan.paymentPlan.totalDebt;

      console.log("before refreshOrder = ", orderInfo);

      AXIOS.post(`/pdforderversion/penalty-waiver/refreshOrder`, orderInfo)
        .then((response) => {
          self.closeRefreshDialog();
          // alert("saved");
          self.refresh();
        })
        .catch((e) => {
          console.error(e);
          // this.initializeCardinal();
          alert(e.message);
        });
    },
    unpack(str) {
      var bytes = [];
      for (var i = 0; i < str.length; i++) {
        var char = str.charCodeAt(i);
        bytes.push(char >>> 8);
        bytes.push(char & 0xff);
      }
      return bytes;
    },
    emailPdfCopy() {
      this.loaderOn = true;
      let orderInfo = this.getRecord(this.record, null);
      orderInfo.orderNumber = this.record.id;
      AXIOS.post("/pdforderversion/penalty/emailpdf", orderInfo)
        .then((res) => {
          this.loaderOn = false;
          if (res) {
            alert("Email Sent!");
          } else {
            alert("Something is wrong Email not sent successfully");
          }
        })
        .catch((error) => {
          this.loaderOn = false;
          alert(Error + error);
        });
    },
    pdfVersion(order) {
      window.location =
        "/api/pdforderversion/pdfDownload/id/" + order.versionId;
    },
    toParams(request, params = {}, prefix = null) {
      for (let [key, value] of Object.entries(request)) {
        let newKey = prefix ? `${prefix}.${key}` : key;
        if (value !== null && typeof value === "object") {
          this.toParams(value, params, newKey);
        } else {
          params[newKey] = value;
        }
      }
      return params;
    },
    getRecord(order, @Nullable spouseDetails) {
      let orderDetails = Object.assign({}, order);
      let orderInfo = {
        id: order.id,
        firstName: order.firstName,
        lastName: order.lastName,
        email: order.email,
        billingPhone: order.billingPhone,
        billingAddress1: order.billingAddress1
          ? order.billingAddress1
          : order.shippingAddress1,
        billingAddress2: order.billingAddress2
          ? order.billingAddress2
          : order.shippingAddress2,
        billingCity: order.billingCity ? order.billingCity : order.shippingCity,
        billingState: order.billingState
          ? order.billingState
          : order.shippingState,
        billingPostalCode: order.billingZip
          ? order.billingZip
          : order.shippingZip,

        shippingAddress1: order.shippingAddress1,
        shippingAddress2: order.shippingAddress2,
        shippingCity: order.shippingCity,
        shippingState: order.shippingState,
        shippingPostalCode: order.shippingZip,

        ssn: order.ssn,
        married: order.married,
        spouseFirstname: spouseDetails
          ? spouseDetails.spouseFirstName
          : order.spouseFirstname,
        spouseLastname: spouseDetails
          ? spouseDetails.spouseLastName
          : order.spouseLastname,
        spouseSsn:
          spouseDetails && spouseDetails.spouseSsn
            ? spouseDetails.spouseSsn
            : !spouseDetails
            ? order.spouseSsn
            : "",
        timeToCall: spouseDetails ? spouseDetails.timeToCall : order.timeToCall,
        status: order.status,
      };
      return orderInfo;
    },

    updateInternalNote() {
      AXIOS.put(`/admin/order/id/${this.$route.params.id}/updatenotes`, {
        user: self.loggedInUser.username,
      })
        .then((response) => {
          console.log("SUCCESS!");
          self.notesSaved = true;
          this.disableButtons = false;
          //this.refresh();
        })
        .catch((e) => {
          console.log("Error Detail", e);
          this.$router.push("/login");
          this.disableButtons = false;
        });
    },

    chargebackCDRN() {
      let self = this;
      this.disableButtons = true;

      const authTransactionId = this.record.authorizeTransactionId;
      AXIOS.put(`/v2/admin/transaction/${authTransactionId}/voidOrRefundOnChargeBack`, {
        cbtype: 1,
        sendNotification: false,
        orderNum: this.record.orderNum,
        type: "SYSTEM",
        content: "CDRN C/B DNS",
        user: self.loggedInUser.username,
      })
        .then((res) => {
          if (res.data.responseIdentifier != "Success") {
            this.alertManager("Chargeback");
            return;
          }

          alert("Transaction has chargeback");
          this.disableButtons = false;
          this.refresh();
          this.$refs.cbStatusModal.hide();
        })
        .catch((e) => {
          this.alertManager("Chargeback");
        });
    },

    chargebackChase() {
      let self = this;
      this.disableButtons = true;

      const authTransactionId = this.record.authorizeTransactionId;
      AXIOS.put(`/v2/admin/transaction/${authTransactionId}/voidOrRefundOnChargeBack`, {
        cbtype: 2,
        sendNotification: false,
        orderNum: this.record.orderNum,
        type: "SYSTEM",
        content: "CHASE C/B DNS",
        user: self.loggedInUser.username,
      })
        .then((res) => {
          let message = res.data.responseIdentifier == "Success"
            ? "Transaction has chargeback"
            : "Chargeback is not successful, alert a Manager to manually process the chargeback";

          alert(message);
          this.disableButtons = false;
          this.refresh();
          this.$refs.cbStatusModal.hide();
        })
        .catch((e) => {
          alert("Chargeback is not successful, alert a Manager to manually process the chargeback");
          self.disableButtons = false;
          this.refresh();
          this.$refs.cbStatusModal.hide();
        });
    },

    clickOnChargebackButton() {
      this.$refs.cbStatusModal.show();
    },

    clickOnHoldButton() {
      this.$refs.offHoldModal.show();
      this.record.status === "On Hold"
        ? (this.put_on_hold = "REMOVE HOLD")
        : (this.put_on_hold = "PUT ON HOLD");
    },
    putOnHold(sendNotification) {
      let self = this;

      console.log("[put on hold clicked!]", sendNotification);

      self.disableButtons = true;
      AXIOS.put(`/admin/penalty-order/id/${this.$route.params.id}/onhold`, {
        sendNotification: sendNotification,
        orderNum: this.record.id,
        type: "SYSTEM",
        content: this.put_on_hold,
        user: self.loggedInUser.username,
      })
        .then((response) => {
          self.disableButtons = false;
          this.$refs.offHoldModal.hide();
          this.refresh();
          //this.updateInternalNote('SYSTEM', this.put_on_hold);
        })
        .catch((e) => {
          alert("Unable to change status.");
          self.disableButtons = false;
        });
    },
    showHideCancelOrderConfirmation(isOpen) {
      isOpen
        ? this.$refs.cancelOrderModel.show()
        : this.$refs.cancelOrderModel.hide();
    },
    refundTransactionOnOrderCancel() {
      const authTransactionId = this.record.authorizeTransactionId;
      /*let refundAuthTransId = this.record.refundAuthTransId
        ? this.record.refundAuthTransId
        : this.record.authorizeTransactionId;*/

      let refundAuthTransId = this.record.authorizeTransactionId;
      AXIOS.get(`/v2/admin/transaction/penalty/${refundAuthTransId}`)
        .then((response) => {
          let self = this;
          this.$set(this, "transactionStatus", response.data);
          AXIOS.put(`/v2/admin/transaction/penalty/${authTransactionId}/void`)
            .then((res) => {
              self.refresh();
              self.disableButtons = false;
            })
            .catch((e) => {
              alert("Unable to void transaction.");
              self.disableButtons = false;
              this.refresh();
            });
        })
        .catch((e) => {
          this.$router.push("/login");
        });
    },
    refundTransactionOnChargeBackCancel() {
      const authTransactionId = this.record.authorizeTransactionId;
      let refundAuthTransId = this.record.refundAuthTransId
        ? this.record.refundAuthTransId
        : this.record.authorizeTransactionId;
      AXIOS.get(`/v2/admin/transaction/penalty/${refundAuthTransId}`)
        .then((response) => {
          let self = this;
          this.$set(this, "transactionStatus", response.data);
          AXIOS.put(
            `/v2/admin/transaction/penalty/${authTransactionId}/voidOnChargeBack`
          )
            .then((res) => {
              self.refresh();
              self.disableButtons = false;
            })
            .catch((e) => {
              AXIOS.put(
                `/v2/admin/transaction/penalty/${authTransactionId}/refundOnChargeBack`,
                {
                  cardNumber: self.transactionStatus.cardNumberMasked,
                  expirationDate: self.transactionStatus.expirationDateMasked,
                  amount: self.transactionStatus.authAmount,
                  invoiceNumber: self.record.orderNum,
                }
              )
                .then((response) => {
                  self.refresh();
                  self.disableButtons = false;
                })
                .catch((e) => {
                  alert("Unable to refund transaction.");
                  self.disableButtons = false;
                });
            });
        })
        .catch((e) => {
          this.$router.push("/login");
        });
    },

    alertManager(process) {
      let successMessage = process + " is not successful, alert email is sent to a Manager";
      let failureMessage = process + " is not successful, error occurred while sending alert email to a Manager. Please alert a Manager to manually process the " + process.toLowerCase();

      AXIOS.put(`/alerts/notify/refund`, {
          orderPath: `/admin/penalty-order/${this.$route.params.id}`
        })
        .then((res) => {
          let informed = res.data.responseIdentifier === "Informed";
          alert(informed ? successMessage : failureMessage);
          this.$refs.cbStatusModal.hide();
          this.disableButtons = false;
          this.refresh();
        })
        .catch((e) => {
          alert(failureMessage);
          this.$refs.cbStatusModal.hide();
          this.disableButtons = false;
          this.refresh();
        });
    },

    cancelOrder() {
      this.showHideCancelOrderConfirmation();
      let self = this;
      this.disableButtons = true;
      //this.refundTransactionOnOrderCancel();
      const authTransactionId = this.record.authorizeTransactionId;

      AXIOS.put(`/v2/admin/transaction/${authTransactionId}/void`, {
          sendNotification: false,
          orderNum: this.record.id,
          type: "SYSTEM",
          content: "CANCEL AND REFUND",
          user: self.loggedInUser.username,
        })
        .then((res) => {
          if (res.data.responseIdentifier != "Success") {
            this.alertManager("Refund");
            return;
          }

          alert("Transaction has voided");
          this.disableButtons = false;
          this.refresh();
        })
        .catch((e) => {
          this.alertManager("Refund");
        });

      // AXIOS.put(`/admin/penalty-order/id/${this.$route.params.id}/cancel`, {
      //   sendNotification: false,
      //   orderNum: this.record.id,
      //   type: "SYSTEM",
      //   content: "CANCEL AND REFUND",
      //   user: self.loggedInUser.username,
      // })
      //   .then((response) => {
      //     if (this.record.authorizeTransactionId) {
      //       this.refundTransactionOnOrderCancel();
      //     } else {
      //       self.disableButtons = false;
      //       this.record.status = "Cancelled";
      //     }
      //   })
      //   .catch((e) => {
      //     alert("Unable to cancel order.");
      //     this.disableButtons = false;
      //     this.$router.push("/login");
      //   });
    },

    hideManualStatusChangeModal() {
      this.manualStatus = "";
      this.$refs.manualStatusChangeModal.hide();
    },

    manualStatusChangeModalHidden() {
      this.manualStatus = "";
    },

    manualStatusChange() {
      if (this.manualStatus != "Cancelled") { // Currently supporting only one status
        return;
      }

      let self = this;
      AXIOS.put(`/admin/penalty-order/id/${this.$route.params.id}/cancel`, {
        sendNotification: false,
        orderNum: this.record.id,
        type: "SYSTEM",
        content: "MANUALLY CANCELLED",
        user: self.loggedInUser.username,
      })
        .then((response) => {
          self.disableButtons = false;
          self.hideManualStatusChangeModal();
          self.refresh();
        })
        .catch((e) => {
          console.log(e);
          alert("Unable to manually change order's status");
          self.disableButtons = false;
          self.hideManualStatusChangeModal();
          self.refresh();
        });
    },

    refundTransaction() {
      let authTransactionId = this.record.authorizeTransactionId;
      let self = this;
      this.disableButtons = true;

      AXIOS.put(`/v2/admin/transaction/penalty/${authTransactionId}/refund`, {
        cardNumber: this.transactionStatus.cardNumberMasked,
        expirationDate: this.transactionStatus.expirationDateMasked,
        amount: this.transactionStatus.authAmount,
        invoiceNumber: this.record.orderNum,
      })
        .then((response) => {
          self.$refs.transactionDetailsModal.hide();
          self.disableButtons = false;
          this.refresh();
        })
        .catch((e) => {
          alert("Unable to refund transaction.");
          self.disableButtons = false;
        });
    },
    voidTransaction() {
      let authTransactionId = this.transactionStatus.authorizeTransactionId;
      let self = this;
      this.disableButtons = true;

      AXIOS.put(`/v2/admin/transaction/${authTransactionId}/void`)
        .then((response) => {
          self.$refs.transactionDetailsModal.hide();
          self.disableButtons = false;
          this.refresh();
        })
        .catch((e) => {
          alert("Unable to void transaction.");
          self.disableButtons = false;
        });
    },
    getTransactionStatus(transId) {
      let authTransactionId = transId || this.record.authorizeTransactionId;
      let self = this;
      this.disableButtons = true;

      AXIOS.get(`/v2/admin/transaction/${authTransactionId}`)
        .then((response) => {
          this.$set(this, "transactionStatus", response.data);
          setTimeout(function () {
            self.$refs.transactionDetailsModal.show();
            self.disableButtons = false;
          }, 1);
        })
        .catch((e) => {
          this.$router.push("/login");
        });
    },
    refresh() {
      this.disableButtons = true;
      let self = this;
      AXIOS.get(`/admin/penalty-order/id/${this.$route.params.id}`)
        .then((response) => {
          let params = Object.assign(
            {},
            this.toParams({ orderId: this.$route.params.id })
          );
          AXIOS.get(`/pdforderversion/getOrder/id/${this.$route.params.id}`, {
            params: params,
          })
            .then((orders) => {
              self.pdfOrderVersions = orders.data;

              self.$set(self, "record", response.data);
              self.pastRecord = clone(response.data);

              //self.record.statusLastChanged = moment
              //  .unix(self.record.statusLastChanged / 1000)
              //  .format("MM/DD/YYYY h:mm:ss A");
              let today = new Date();
              let orderCreatedDate = new Date(self.record.createdDate);
              let dateDifference = Math.round(today - orderCreatedDate);
              let weekDifference = Math.round(
                dateDifference / 1000 / 60 / 60 / 24 / 7
              );
              let weeksAllowedToChangeStatus = self.loggedInUser.orderStatus;
              let weeksAllowedToEdit = self.loggedInUser.editRecord;
              let weeksAllowedToRefund = self.loggedInUser.refundPayment;

              if (
                self.loggedInUser.userType == "admin" ||
                (self.loggedInUser.userType == "StandardUser" &&
                  weekDifference <= weeksAllowedToChangeStatus)
              ) {
                self.statusChangeAuth = true;
              }
              if (
                self.loggedInUser.userType == "admin" ||
                (self.loggedInUser.userType == "StandardUser" &&
                  weekDifference <= weeksAllowedToEdit)
              ) {
                self.editRecordAuth = true;
              }
              if (
                self.loggedInUser.userType == "admin" ||
                (self.loggedInUser.userType == "StandardUser" &&
                  weekDifference <= weeksAllowedToRefund)
              ) {
                self.voidRefundAuth = true;
              }
              this.disableButtons = false;
              this.generateNoteLists(this.record.notes);
              console.log("[record]", this.record);
            })
            .catch((error) => {
              self.$set(self, "record", response.data);
              self.pastRecord = clone(response.data);
              let today = new Date();
              let orderCreatedDate = new Date(self.record.createdDate);
              let dateDifference = Math.round(today - orderCreatedDate);
              let weekDifference = Math.round(
                dateDifference / 1000 / 60 / 60 / 24 / 7
              );
              let weeksAllowedToChangeStatus = self.loggedInUser.orderStatus;
              let weeksAllowedToEdit = self.loggedInUser.editRecord;
              let weeksAllowedToRefund = self.loggedInUser.refundPayment;

              if (
                self.loggedInUser.userType == "admin" ||
                (self.loggedInUser.userType == "StandardUser" &&
                  weekDifference <= weeksAllowedToChangeStatus)
              ) {
                self.statusChangeAuth = true;
              }
              if (
                self.loggedInUser.userType == "admin" ||
                (self.loggedInUser.userType == "StandardUser" &&
                  weekDifference <= weeksAllowedToEdit)
              ) {
                self.editRecordAuth = true;
              }
              if (
                self.loggedInUser.userType == "admin" ||
                (self.loggedInUser.userType == "StandardUser" &&
                  weekDifference <= weeksAllowedToRefund)
              ) {
                self.voidRefundAuth = true;
              }
              this.disableButtons = false;
              this.generateNoteLists(this.record.notes);
              console.log("[record]", this.record);
            });
        })
        .catch((e) => {
          this.$router.push("/login");
          this.disableButtons = false;
        });
    },

    // detailNoteList: [
    //     {type:'MANUAL', content:'this is added by admin manaully', user: 'ADMIN', created: '10/10/2020 12:39 PM'},
    //     {type:'SYSTEM', content:'REMOVE HOLD TEST1', user: 'ADMIN1', created: '10/11/2020 12:39 PM'},
    //     {type:'SYSTEM', content:'REMOVE HOLD TEST2', user: 'ADMIN2', created: '10/12/2020 14:39 PM'},
    //     {type:'SYSTEM', content:'REMOVE HOLD TEST3', user: 'ADMIN3', created: '10/13/2020 16:39 PM'},
    //   ],
    generateNoteLists(notelist) {
      if (notelist != null) {
        this.detailNoteList = [];

        let notes = notelist.split("###");

        for (let i = 0; i < notes.length; i++) {
          let items = notes[i].split("=*=");

          if (items.length == 4) {
            this.detailNoteList.push({
              type: items[0],
              content: items[1],
              user: items[2].toUpperCase(),
              created: items[3],
            });
          }
          if (items.length == 1) {
            this.detailNoteList.push({
              type: "MANUAL",
              content: items[0],
              user: null,
              created: null,
            });
          }
        }
      }
      const firstArray = this.detailNoteList.filter(
        (x) => x.created == null || x.created == undefined
      );
      let secondArray = this.detailNoteList.filter(
        (x) => x.created !== null && x.created !== undefined
      );
      secondArray.sort((a, b) => {
        const fistValue = moment(a.created, "DD/MM/YYYY hh:mm:ss A").valueOf();
        const secondValue = moment(
          b.created,
          "DD/MM/YYYY hh:mm:ss A"
        ).valueOf();
        return secondValue - fistValue;
      });
      this.detailNoteList = firstArray.concat(secondArray);
    },
    formatDate(date) {
      const dateInNY = moment.tz(
        date,
        "DD/MM/YYYY hh:mm:ss A",
        "America/New_York"
      );
      const utcTime = dateInNY.toDate();
      const localTime = moment
        .utc(utcTime)
        .local()
        .format("MM/DD/YYYY hh:mm A");
      return localTime;

      // return moment(date,"DD/MM/YYYY hh:mm:ss A").format('MM/DD/YYYY hh:mm A');
    },
    saveNotes() {
      let self = this;
      this.disableButtons = true;
      AXIOS.put(`/admin/order/id/${this.$route.params.id}`, this.record)
        .then((response) => {
          self.notesSaved = true;
          this.disableButtons = false;
          this.refresh();
        })
        .catch((e) => {
          this.$router.push("/login");
          this.disableButtons = false;
        });
    },
    saveNewNote() {
      this.isAddNote = false;
      let self = this;
      AXIOS.put(`/admin/penalty-order/id/${this.$route.params.id}/updatenotes`, {
        orderNum: this.record.id,
        content: this.manual_note,
        type: "MANUAL",
        user: self.loggedInUser.username,
      })
        .then((response) => {
          this.manual_note = "";
          this.pastRecord = response.data;
          self.onConfirmRefresh();
        })
        .catch((e) => {
          this.$router.push("/login");
        });
    },
    cancelAddANote() {
      this.manual_note = "";
      this.isAddNote = false;
    },
    saveOrderDetails() {
      let self = this;
      this.disableButtons = true;

      //if(this.manual_note.length > 0) {
      const isEmailUpdate =
        this.record.email !== this.pastRecord.email ? true : false;
      const isTrackingNumberUpdate =
        this.record.trackingNumber !== this.pastRecord.trackingNumber
          ? true
          : false;

      if (this.record.married == true || this.record.married == "true")
        this.record.married = 1;
      else this.record.married = 0;

      console.log(this.record);
      AXIOS.put(`/admin/penalty-order/id/${this.$route.params.id}/update`, {
        orderRecord: this.record,
        content: this.manual_note,
        trackingNumberContent: isTrackingNumberUpdate
          ? "TRACKING - EDITED"
          : "",
        emailChangeContent: isEmailUpdate ? "EMAIL - EDITED" : "",
        user: self.loggedInUser.username,
      })
        .then((response) => {
          self.addressesSaved = true;
          this.disableButtons = false;
          this.manual_note = "";
          if (
            response.data &&
            response.data.status === "Completed" &&
            response.data.trackingNumber &&
            response.data.trackingNumber !== null &&
            isTrackingNumberUpdate
          ) {
            this.emailNotificationModelData.title = "Notification Sending";
            this.emailNotificationModelData.content =
              "Would you like to send an email notification ?";
            this.$refs.emailNotificationModel.show();
          } else if (
            response.data.trackingNumber !== null &&
            isTrackingNumberUpdate
          ) {
            this.emailNotificationModelData.title = "Update Tracking Number";
            this.emailNotificationModelData.content =
              "Would you like to send an email notification to the customer ?";
            this.$refs.emailNotificationModel.show();
          } else if (response.data.email != null && isEmailUpdate) {
            this.emailNotificationModelData.title = "Update Email Address";
            this.emailNotificationModelData.content =
              "Would you like to send an email notification to the customer ?";
            this.$refs.emailNotificationModel.show();
          }
          this.pastRecord = response.data;
          self.onConfirmRefresh();
          this.isEditMode = false;
        })
        .catch((e) => {
          console.log(e);
          this.$router.push("/login");
          this.disableButtons = false;
        });
      //}
    },
    isChargeBackAvailable(status) {
      return (
        (this.disableButtons && !this.isEditMode) ||
        (status !== "Processing" && status !== "Completed")
      );
    },
    backToResults(isRoute) {
      if (isRoute === true) {
        this.$refs.backConfirmation.hide();
        this.$router.push("/admin/orders");
      } else if (isRoute == false) {
        this.$refs.backConfirmation.hide();
      } else if (this.isEditMode) {
        this.$refs.backConfirmation.show();
      } else {
        this.$refs.backConfirmation.hide();
        this.$router.push("/admin/orders");
      }
    },
    goToTracking() {
      if (this.record && this.record.trackingNumber)
        methods.uspsTracking(this.record.trackingNumber);
    },
  },
  mounted() {
    this.penaltyTypes = PENALTY_TYPES;

    let userInStorage = JSON.parse(localStorage.getItem("loggedUser"));
    this.loggedInUser =
      this.$store.getters.getUser != ""
        ? this.$store.getters.getUser
        : userInStorage
        ? userInStorage
        : "";

    this.refresh();
  },
};
</script>
<style scoped>
.aside-item h2 {
  font-size: 3em;
}
.aside-item p {
  font-size: 1.5em;
}
.home-page {
  /* background-image: url(./../assets/banner.jpg); */
  background-size: cover;
}

table,
th,
td {
  border: 2px solid lightgrey;
  border-collapse: collapse;
}
th,
td {
  padding: 5px;
  text-align: left;
  width: 50%;
}

hr {
  border: 0;
  clear: both;
  display: block;
  width: 100%;
  background-color: lightgray;
  height: 1px;
}
.hide-br {
  display: none;
}

@media (min-width: 64em) {
  .hide-br {
    display: initial;
  }
}

.business-address label {
  font-weight: 700 !important;
  margin-top: 0.25rem !important;
  margin-bottom: 0 !important;
}

.penalty-type {
  padding: 5px;
  border-radius: 5px;
  border: 1px solid #e9ecef;
  background: #e9ecef;
}

.penalty-options-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.penalty-options {
  width: 31%;
  display: inline-block;
}

.penalty-amount-text-wrapper {
  background-color: #e9ecef;
  border: 1px solid #ced4da;
  width: 100%;
  height: calc(1.5em + 0.75rem + 2px);
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  border-radius: 0.25rem;
  padding: 0.375rem 0.75rem;
  position: relative;
}
.penalty-amount-text-wrapper-active {
  background-color: #fff;
}

.penalty-amount-text-wrapper input {
  width: 100%;
  border: none;
  background: transparent;
}
.penalty-amount-text-wrapper input:focus {
  outline: none;
}
.dollar-mark {
  position: absolute;
  left: 5px;
  top: 7px;
}
</style>
