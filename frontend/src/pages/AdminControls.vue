<template>
  <div class="container mt-2">
    <div>
      <div class="inner-page-title mb-2 mt-4">
        Admin Controls
      </div>
      <div class="inner-panel p-3">
        <table class="table admin-controls-table">
          <tbody>
          <tr>
            <td class="text-nowrap">
              Email addresses for "Manager's Refund Alert":
            </td>
            <td>
              <input
                type="text"
                :class="['form-control', errors.email1 ? 'is-invalid' : '']"
                @keydown.space="(event) => event.preventDefault()"
                v-model="alertConfig1.email"
                placeholder="Email"/>
            </td>
            <td>
              <toggle-button :width="40" :height="20" :font-size="18" v-model="alertConfig1.enabled" color="#00d35f"
                             :sync="true" :labels="false" @change="''"/>
            </td>
          </tr>

          <tr>
            <td class="text-nowrap"></td>
            <td>
              <input
                type="text"
                :class="['form-control', errors.email2 ? 'is-invalid' : '']"
                @keydown.space="(event) => event.preventDefault()"
                v-model="alertConfig2.email"
                placeholder="Email"/>
            </td>
            <td>
              <toggle-button :width="40" :height="20" :font-size="18" v-model="alertConfig2.enabled" color="#00d35f"
                             :sync="true" :labels="false" @change="''"/>
            </td>
          </tr>

          <tr>
            <td class="text-nowrap">
              Enable EIN auto fulfillment:
            </td>
            <td>
              <toggle-button :width="40" :height="20" :font-size="18" v-model="einAutoFulfillmentConfig.enabled"
                             color="#00d35f"
                             :sync="true" :labels="false" @change="''"/>
            </td>
            <td></td>
          </tr>
          </tbody>
        </table>
        <div class="button-wrap">
          <div class="btn-custome-rounded" @click="onClickSave()">
            <img src="@/assets/lock.png">
            SAVE CHANGES
          </div>
        </div>


        <div class="row">
          <div class="col-sm-3 mt-2">
            <!-- <datepicker :clear-button="true" :bootstrap-styling="true" placeholder="Select Search Date" v-model="request.query.createdAt" id="createdAt"></datepicker> -->
            <div class="form-group select-date">
              <input
                placeholder="Select Date"
                class="form-control"
                id="selectDate"
                :value="
                      formatDates(
                        request.query.startAt,
                        request.query.endAt
                      )
                    "
              />
              <span
                id="cleardates"
                @click="clearDates()"
              >X</span
              >
              <AirbnbStyleDatepicker
                style="position: absolute; width: 350px; left: 15px; z-index: 9999"
                :showShortcutsMenuTrigger="false"
                :trigger-element-id="'selectDate'"
                :monthsToShow="1"
                :mode="'range'"
                :fullscreen-mobile="false"
                :date-one="request.query.startAt"
                :date-two="request.query.endAt"
                @date-one-selected="
  (val) => {
    request.query.startAt = val;
  }
"
                @date-two-selected="
  (val) => {
    request.query.endAt = val;
  }
"

              />
            </div>
          </div>

          <div class="col mt-2">
            <a
              class="btn btn-secondary"
              :href="'/api/admin/processorders/ordersByDate.csv?' + searchParams">
              Export CSV &nbsp;</a>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import {AXIOS} from '../scripts/http-common';
import {SiteUtils} from "../scripts/site-common";
import querystring from "querystring";
import Datepicker from "vuejs-datepicker";
import format from "date-fns/format";
import omit from "lodash/omit";
// import AirbnbStyleDatepicker from "vue-airbnb-style-datepicker";

export default {
  name: 'AdminControls',
  components: {
    Datepicker,
    // AirbnbStyleDatepicker,
  },
  data() {
    return {
      refundManual1: "notifications.refund.manual",
      refundManual2: "notifications.refund.manual.secondary",
      einAutoFulfillment: "ein.fulfillment.auto",
      alertConfig1: {},
      alertConfig2: {},
      einAutoFulfillmentConfig: {},
      errors: {
        email1: false,
        email2: false
      },
      request: {
        pageNumber: 0,
        pageSize: 20,
        orderBy: "createdDate",
        orderDirection: "DESC",
        query: {
          email: "",
          phone: "",
          product: "",
          isCalifornia: false,
          isNewJersey: false,
          isGeorgia: false,
          isIllinois: false,
          isMichigan: false,
          createdAt: {
            start: "",
            end: "",
          },
          startAt: "",
          endAt: "",
        },
      },
      dateFormat: "MM/DD/YYYY",
    }
  },
  mounted() {
    AXIOS.get(`/alerts/config/list`).then(res => {
      if (res.status == 200) {
        this.setUpConfigs(res.data);
      }
    });
    // this.request.query.dateRange = this.$store.getters.getSearchValues.query
    //   ? this.$store.getters.getSearchValues.query.dateRange
    //   : "THIRTY_DAYS";
    // this.request.query.createdAt = this.$store.getters.getSearchValues.query
    //   ? this.$store.getters.getSearchValues.query.createdAt
    //   : {
    //     startAt: "",
    //     endAt: "",
    //   };
  },
  computed: {
    searchParams() {
      return querystring.encode(this.toParams(this.request.query));
    },
  },
  methods: {
    setUpConfigs(configs) {
      this.alertConfig1 = configs.find(next => next.name === this.refundManual1);
      this.alertConfig2 = configs.find(next => next.name === this.refundManual2);
      this.einAutoFulfillmentConfig = configs.find(next => next.name === this.einAutoFulfillment);
    },

    clearDates() {
      (this.request.query.startAt = ""), (this.request.query.endAt = "");
    },

    onClickSave() {
      this.errors.email1 = false;
      this.errors.email2 = false;
      let checkEmailErrorMsg1 = SiteUtils.validateEmail(this.alertConfig1.email);
      if (checkEmailErrorMsg1) {
        this.errors.email1 = checkEmailErrorMsg1;
      }
      let checkEmailErrorMsg2 = SiteUtils.validateEmail(this.alertConfig2.email);
      if (checkEmailErrorMsg2) {
        this.errors.email2 = checkEmailErrorMsg2;
      }
      if (this.errors.email1 || this.errors.email2) {
        return;
      }

      AXIOS.put(`/alerts/config/list`, [this.alertConfig1, this.alertConfig2, this.einAutoFulfillmentConfig]).then(res => {
        if (res.status == 200) {
          this.setUpConfigs(res.data);
          alert('Updated Successfully!');
        }
      }).catch(err => {
        alert('Error occurred while updating alerts configuration');
        console.log(err);
      });
    },

    formatDates(dateOne, dateTwo) {
      let formattedDates = "";
      if (dateOne) {
        formattedDates = format(dateOne, this.dateFormat);
      }
      if (dateTwo) {
        formattedDates += " - " + format(dateTwo, this.dateFormat);
      }
      this.request.query.createdAt = JSON.stringify({
        start: dateOne,
        end: dateTwo

      });
      return formattedDates;
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


  }
}
</script>

<style>
.admin-controls-table th, td {
  font-family: "Archivo", sans-serif;
  border-top: none !important;
}

.table th {
  border-top: none;
}

table.admin-controls-table td {
  vertical-align: middle !important;
}

.button-wrap {
  text-align: right;
}

.select-date {
  padding-right: 0;
}

.select-date #selectDate {
  border-radius: 999px;
}

.vue-airbnb-style-datepicker {
  z-index: 9999 !important;
  position: absolute !important;
}

#cleardates {
  position: absolute;
  right: 10%;
  top: 12%;
  cursor: pointer;
  color: darkgrey;
}
</style>
