<template>
  <div class="casetracker-wrap pt-5 pb-5">
    <div class="faq-title">
      {{ viewType == "normal" ? "Case Tracker" : "Order Tracker" }}
    </div>
    <div class="faq-desc mb-3">
      {{
        viewType == "normal"
          ? "Watch our tax specialists complete your case in real time."
          : "After you have placed an order, watch our tax specialists complete your order in real time."
      }}
    </div>

    <div class="container bgwhite p-4">
      <div class="casetracker">
        <div class="tracker-form">
          <div class="mb-3">
            Enter your order number, sit back, and watch us work!
          </div>
          <div class="inner-form">
            <div>
              <div class="form-group">
                <label for="caseNumber">Order Number</label>
                <input
                  type="text"
                  class="form-control"
                  id="caseNumber"
                  placeholder="Type here"
                  v-model="caseNumber"
                />
              </div>
            </div>
            <div>
              <div class="form-group">
                <label for="phoneNumber">Phone Number</label>
                <input
                  type="text"
                  class="form-control"
                  id="phoneNumber"
                  placeholder="Type here"
                  v-model="phoneNumber"
                  v-mask="'###-###-####'"
                />
              </div>
            </div>
            <div class="alert alert-danger" v-if="hasError">
              Invalid Order Number
            </div>
            <div class="green-landing-button" @click="trackOrder">
              Track Order
            </div>
          </div>
        </div>
        <div class="status-show">
          <div class="status-text" v-if="orderStatus != 5">
            {{ orderStatusDisplayText }}
          </div>
          <div class="status-text" v-else>
            <a
              :href="
                'https://tools.usps.com/go/TrackConfirmAction?qtc_tLabels1=' +
                trackingNumber
              "
              target="_blank"
              style="text-decoration: none"
              >Work Complete</a
            >
          </div>
          <div class="status-icons">
            <div class="progress-back"></div>
            <div class="progress-indicator" :style="'width:' + piWidth"></div>
            <div
              :class="
                'status-icon ' +
                (orderStatus <= 0 ? 'status-icon-active' : 'status-icon-done')
              "
            >
              <font-awesome-icon icon="cart-shopping" />
            </div>
            <div
              :class="
                'status-icon ' +
                (orderStatus > 0
                  ? orderStatus <= 2
                    ? 'status-icon-active'
                    : 'status-icon-done'
                  : '')
              "
            >
              <font-awesome-icon icon="spinner" />
            </div>
            <div
              :class="
                'status-icon ' +
                (orderStatus > 0
                  ? orderStatus == 3
                    ? 'status-icon-active'
                    : 'status-icon-done'
                  : '')
              "
            >
              <font-awesome-icon icon="layer-group" />
            </div>
            <div
              :class="
                'status-icon ' +
                (orderStatus > 0
                  ? orderStatus == 4
                    ? 'status-icon-active'
                    : 'status-icon-done'
                  : '')
              "
            >
              <font-awesome-icon icon="list-check" />
            </div>
            <div
              :class="
                'status-icon ' +
                (orderStatus > 0
                  ? orderStatus == 5
                    ? 'status-icon-done'
                    : ''
                  : '')
              "
            >
              <font-awesome-icon icon="circle-check" />
            </div>
          </div>
          <div class="orderstatus-text mt-3" v-if="!hasError">
            <div v-if="orderStatus === 2">
              <p class="orderstatus-text-lg">
                Thanks <b>{{ customerFirstName }}</b
                >.
              </p>
              Your order {{ caseNumber }} went through correctly and we're
              preparing your documents now. It will take us about 1 business day
              to complete and mail this to you, so you may close out of this
              window now. Further updates will be sent to your email address at
              <b
                ><span class="blue-text">{{ customerEmail }}</span></b
              >.
            </div>
            <div v-if="orderStatus === 3">
              <p class="orderstatus-text-lg">
                Hi <b>{{ customerFirstName }}</b
                >.
              </p>
              We are preparing your documents now and will have them completed
              within the next business day. We will mail this directly to you
              first through the
              <b>Post Office</b> (the {{ partyName }} requires your physical
              signature). We'll send you an email soon with the
              <b>USPS tracking number</b> from the <b>Post Office</b> at
              <b
                ><span class="blue-text">{{ customerEmail }}</span></b
              >
              once it is complete and in the mail to you.
            </div>
            <div v-if="orderStatus === 4">
              <p class="orderstatus-text-lg">
                Hi <b>{{ customerFirstName }}</b
                >.
              </p>
              We have your documents prepared, we're just checking the final
              work and preparing a mailing label. We'll mail this directly to
              you first through the
              <b>Post Office</b> (the {{ partyName }} requires your physical
              signature on the application). We'll send you an email at
              <b
                ><span class="blue-text">{{ customerEmail }}</span></b
              >
              within the next business day when it is complete and in the mail,
              along with the <b>USPS tracking number</b> from the
              <b>Post Office</b>. NOTE: We can only mail orders and create
              <b>USPS tracking numbers</b> Monday-Friday. No weekends or federal
              holidays.
            </div>
            <div v-if="orderStatus === 5">
              <p class="orderstatus-text-lg">
                Hi <b>{{ customerFirstName }}</b
                >.
              </p>
              We have your application complete, and it's currently in the mail
              to you now. The <b>USPS tracking info</b> is here:
              <a
                :href="
                  'https://tools.usps.com/go/TrackConfirmAction?qtc_tLabels1=' +
                  trackingNumber
                "
                target="_blank"
                style="
                  text-decoration: none;
                  color: #0094be;
                  word-break: break-word;
                "
                >https://tools.usps.com/go/TrackConfirmAction?qtc_tLabels1={{
                  trackingNumber
                }}</a
              >
              Once this arrives, please sign the first page and mail this to the
              {{ partyName }} using the included prepaid mailing envelope as
              soon as possible.
            </div>
            <div v-if="orderStatus === -1">
              We have your order on hold because some of your information is
              incorrect or invalid. Please contact us immediately to resolve it.
            </div>
            <div v-if="orderStatus === -2">
              Your order has been cancelled and refunded. Please contact us for
              more information.
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { AXIOS } from "../scripts/http-common";
import moment from "moment";
import config from "../../config";

export default {
  props: {
    viewType: {
      type: String,
      default: "normal",
    },
    defaultOrderNumber: {
      type: String,
      default: "",
    },
    defaultPhoneNumber: {
      type: String,
      default: "",
    },
    defaultOrderStatus: {
      default: 0,
    },
    isCalifornia: {
      type: String,
      default: "",
    },
    isGeorgia: {
      type: String,
      default: "",
    },
    isIllinois: {
      type: String,
      default: "",
    },
    isNewJersey: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      carouselConfig: {
        display: 1,
        width: Math.max(screen.width - 100, 250),
        height: 200,
      },
      customerFirstName: "",
      customerEmail: "",
      trackingNumber: "",
      orderStatus: this.defaultOrderStatus || 0,
      orderStatusText: "",
      orderStatusDisplayText: "",
      caseNumber: this.defaultOrderNumber || "",
      phoneNumber: this.defaultPhoneNumber || "",
      hasError: false,
      piWidth: "0px",
    };
  },
  computed: {
    partyName: function () {
      if (this.isCalifornia) {
        return "FTB";
      }
      if (this.isNewJersey) {
        return "Division of Taxation";
      }
      if (this.isGeorgia) {
        return "Georgia Department of Revenue";
      }
      if (this.isIllinois) {
        return "Illinois Department of Revenue";
      }
      return "IRS";
    },
  },
  methods: {
    trackOrder: function () {
      this.orderStatus = 0;
      AXIOS.get(
        `/casetracker/order/${this.caseNumber.trim()}/${this.phoneNumber.trim()}`
      )
        .then((response) => {
          const {
            status,
            createdDate,
            customerFirstName,
            customerEmail,
            trackingNumber,
            isCalifornia,
            isNewJersey,
            isGeorgia,
            isIllinois,
          } = response.data;
          const unspecifiedStatusValues = [
            "Incomplete",
            "Charge Back",
            "Failed",
            "Deleted",
          ];
          const inProgressStatusValues = ["Processing"];
          const completeStatusValues = ["Completed"];
          const onHoldStatusValues = ["On Hold"];
          const cancelledStatusValues = ["Cancelled"];
          const duration = moment().unix() - moment(createdDate).unix();
          const mins30 = 30 * 60;
          const hours2 = 2 * 60 * 60;

          this.orderStatusText = status;

          if (unspecifiedStatusValues.includes(status)) {
            this.orderStatus = -3;
          } else if (inProgressStatusValues.includes(status)) {
            if (duration < mins30) {
              this.orderStatus = 2;
            } else if (duration < hours2) {
              this.orderStatus = 3;
            } else {
              this.orderStatus = 4;
            }
          } else if (completeStatusValues.includes(status)) {
            this.orderStatus = 5;
          } else if (onHoldStatusValues.includes(status)) {
            this.orderStatus = -1;
          } else if (cancelledStatusValues.includes(status)) {
            this.orderStatus = -2;
          } else {
            this.orderStatus = 0;
          }
          this.customerFirstName = customerFirstName;
          this.customerEmail = customerEmail;
          this.trackingNumber = trackingNumber;
          this.isCalifornia = isCalifornia;
          this.isGeorgia = isGeorgia;
          this.isNewJersey = isNewJersey;
          this.isIllinois = isIllinois;
          this.hasError = false;

          console.log(this.orderStatus);
          this.piWidth = "calc(" + (this.orderStatus * 20 + "%") + " - 56px";

          if (this.orderStatus <= 0)
            this.orderStatusDisplayText = "Order Confirmed";
          else if (this.orderStatus > 0 && this.orderStatus <= 2)
            this.orderStatusDisplayText = "Work in Progress";
          else if (this.orderStatus == 3)
            this.orderStatusDisplayText = "Compiling Forms";
          else if (this.orderStatus == 4)
            this.orderStatusDisplayText = "Checking Final Work";
        })
        .catch((e) => {
          this.hasError = true;
        });
    },
  },
};
</script>

<style scoped>
.casetracker-wrap {
  background: #f8faf9;
}

.bgwhite {
  background: white;
  border-radius: 24px;
}

.faq-title {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 32px;
  line-height: 48px;

  color: #252020;
  text-align: center;
}

.faq-desc {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 24px;

  color: #252020;
  text-align: center;
}

.casetracker {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 24px;

  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
  align-items: flex-start;
}

.tracker-form {
  background: #2a96c4;
  border-radius: 16px;
  color: white;
  padding: 32px;
  width: 40%;
}
.status-show {
  padding: 10px 0px 15px 20px;
  width: 58%;
}

input {
  background-color: #4ea7ce;
  border: none;
  color: white;
}

.form-control::placeholder {
  /* Chrome, Firefox, Opera, Safari 10.1+ */
  color: white;
  opacity: 1; /* Firefox */
}

.form-control:-ms-input-placeholder {
  /* Internet Explorer 10-11 */
  color: white;
}

.form-control::-ms-input-placeholder {
  /* Microsoft Edge */
  color: white;
}

textarea:focus,
input[type="text"]:focus {
  border-color: white;
  box-shadow: none;
  outline: 0 none;
  background-color: #4ea7ce;
  color: white;
}

.status-text {
  font-style: normal;
  font-weight: 700;
  font-size: 24px;
  line-height: 48px;

  color: #2a96c4;
}

.status-icons {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  position: relative;
}

.status-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: 1px solid #edeeef;
  background: #edeeef;
  color: white;
  text-align: center;
  padding-top: 15px;
  z-index: 999;
}

.status-icon-done {
  background-color: #2a96c4;
  border: 1px solid #2a96c4;
}

.status-icon-active {
  background-color: white;
  border: 1px solid #2a96c4;
  color: #2a96c4;
}

.progress-back {
  position: absolute;
  left: 0px;
  top: 50%;
  transform: translateY(-50%);
  width: 100%;
  height: 2px;
  background-color: #edeeef;
}

.progress-indicator {
  position: absolute;
  left: 0px;
  top: 50%;
  width: 0%;
  transform: translateY(-50%);
  width: 100%;
  height: 2px;
  background-color: #2a96c4;
}

.orderstatus-text-lg {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 24px;
}
.orderstatus-text {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 28px;
}

@media only screen and (max-width: 768px) {
  .tracker-form {
    background: #2a96c4;
    border-radius: 16px;
    color: white;
    padding: 32px;
    width: 100%;
  }
  .status-show {
    padding: 10px 0px 15px 20px;
    width: 100%;
  }
}
</style>
