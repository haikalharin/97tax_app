<template>
  <div>
    <ein-header></ein-header>
    <ein-progress-indictor
      :title="piTitle"
      :subtitle="piSubTitle"
      :value="curProgressPercent"
    />
    <ein-entity-type-page
      v-if="currentPage == PAGE_ENTITY_TYPE"
      :order="order"
      @change="onChangeEntityType"
      @continue="onContinue"
    />
    <ein-entity-sub-type
      v-if="currentPage == PAGE_ENTITY_SUBTYPE"
      :order="order"
      @change="onChangeSubType"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-f-1-page
      v-if="currentPage == PAGE_FORM1"
      :order="order"
      @change="onChangeReason4Ein"
      @back="onBackPage"
      @continue="onContinue"
    />
    <EinLLCMemberNum
      v-if="currentPage == PAGE_LLC_MEMBER_NUM"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-form-6-info
      v-if="currentPage == PAGE_FORM6_INFO"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-business-physical-location
      v-if="currentPage == PAGE_BUSINESS_PHYSICAL_LOC"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
      @onChangeDiffMailingAddress="onChangeDiffMailingAddress"
    />
    <ein-business-type
      v-if="currentPage == PAGE_BUSINESS_TYPE"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-business-sub-type
      v-if="currentPage == PAGE_BUSINESS_SUB_TYPE"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-mailing-address
      v-if="currentPage == PAGE_MAILING_ADDRESS"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-partnership-business-info
      v-if="currentPage == PAGE_PARTNERSHIP_BUSINESS_INFO"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-red-flag-questions
      v-if="currentPage == PAGE_RED_FLAG_QUESTIONS"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-describe-employees
      v-if="currentPage == PAGE_DESCRIBE_EMPLOYEES"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
    />
    <ein-application-summary
      v-if="currentPage == PAGE_APPLICATION_SUMMARY"
      :order="order"
      @back="onBackPage"
      @continue="onContinue"
      @returnForm6="returnBack(PAGE_FORM6_INFO)"
      @returnEntityType="returnBack(PAGE_ENTITY_TYPE)"
      @returnForm1="returnBack(PAGE_FORM1)"
      @returnBusinessPhysical="returnBack(PAGE_BUSINESS_PHYSICAL_LOC)"
    />

    <SalesWidget :phone_number="salesPhoneNumber" v-if="salesPhoneNumber" />
    <bottom-nav></bottom-nav>
  </div>
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
import BottomNav from "../components/BottomNav.vue";
import { AXIOS } from "../scripts/http-common";
import EinHeader from "./ein/EinHeader.vue";
import EinProgressIndictor from "./ein/EinProgressIndictor.vue";
import EinEntityTypePage from "./ein/EinEntityTypePage.vue";
import EinF1Page from "./ein/EinF1Page.vue";
import EinBottomNavigator from "./ein/EinBottomNavigator.vue";
import EinForm6Info from "./ein/EinForm6Info.vue";
import EinBusinessPhysicalLocation from "./ein/EinBusinessPhysicalLocation.vue";
import EinBusinessType from "./ein/EinBusinessType.vue";
import EinBusinessSubType from "./ein/EinBusinessSubType.vue";
import EinMailingAddress from "./ein/EinMailingAddress.vue";
import EinRedFlagQuestions from "./ein/EinRedFlagQuestions.vue";
import EinDescribeEmployees from "./ein/EinDescribeEmployees.vue";
import EinApplicationSummary from "./ein/EinApplicationSummary.vue";
import EinDocuSign from "./ein/EinDocuSign.vue";
import EinEntitySubType from "./ein/EinEntitySubType.vue";
import EinLLCMemberNum from "./ein/EinLLCMemberNum.vue";
import { SiteUtils, APPLICATION_SUMMARY_PAGE } from "../scripts/site-common";
import { SnackUtils } from "../scripts/snack-common";
import SalesWidget from "./../components/SalesWidget.vue";
import { ORDER_TYPES, SUB_TYPES, TRUST_SUB_TYPES } from "../scripts/constants";
import moment from "moment";

export default {
  components: {
    EinHeader,
    BottomNav,
    EinProgressIndictor,
    EinBottomNavigator,
    EinEntityTypePage,
    EinF1Page,
    EinEntitySubType,
    EinForm6Info,
    EinBusinessPhysicalLocation,
    EinBusinessType,
    EinBusinessSubType,
    EinMailingAddress,
    EinRedFlagQuestions,
    EinDescribeEmployees,
    EinApplicationSummary,
    EinDocuSign,
    EinLLCMemberNum,
  },
  created() {
    this.PAGE_ENTITY_TYPE = "entity-type";
    this.PAGE_ENTITY_SUBTYPE = "entity-subtype";
    this.PAGE_FORM1 = "form1-page";
    this.PAGE_LLC_MEMBER_NUM = "llc-member-num";
    this.PAGE_FORM6_INFO = "form6-info";
    this.PAGE_BUSINESS_PHYSICAL_LOC = "sole-business-physical-loc";
    this.PAGE_BUSINESS_TYPE = "business-type";
    this.PAGE_BUSINESS_SUB_TYPE = "business-sub-type";
    this.PAGE_MAILING_ADDRESS = "mailing-address";

    this.PAGE_RED_FLAG_QUESTIONS = "red-flag-quesitons";
    this.PAGE_DESCRIBE_EMPLOYEES = "describe-employees";
    this.PAGE_APPLICATION_SUMMARY = APPLICATION_SUMMARY_PAGE;

    this.PAGE_PARTNERSHIP_BUSINESS_INFO = "partnership-business-info";
  },
  data() {
    return {
      salesPhoneNumber: null,
      curProgressPercent: 10,
      currentPage: "entity-type",
      piTitle: "",
      piSubTitle: "",
      order: {
        id: this.$route.query.order ? this.$route.query.order : "",
        last_saved_page: null,
        order_type: null,
        sub_type: null,
        reason: null,
        is_diff_mailing_address: null,
        amount: null,
        llc_number_members: "",

        first_name: "",
        middle_name: "",
        no_middle_name: false,
        last_name: "",
        title: "",
        email: "",
        suffix: "",
        is_sec645: 0,
        ssn: "",

        secondary_first_name: "",
        secondary_middle_name: "",
        secondary_no_middle_name: false,
        secondary_last_name: "",
        secondary_suffix: "",
        secondary_ssn: "",

        is_same_physical_address: 1,
        address: "",
        apt_suite: "",
        city: "",
        state: "",
        county: "",
        zip_code: "",
        phone_number: "",

        legal_name: "",
        business_state: "",
        business_country: "",
        business_type: "",
        business_sub_type: "",
        business_sub_type_2: "",
        business_sub_type_3: "",
        business_sub_type_4: "",
        business_details: "",
        is_previous_ein: false,
        previous_ein: "",
        start_date_month: "",
        start_date_year: "",
        accounting_close_month: "",
        is_large_motor_vehicle: 0,
        is_gambling: 0,
        is_excise_tax_form720: 0,
        is_atf: 0,
        is_w2_employees: 0,
        date_first_wages_year: null,
        date_first_wages_month: null,
        max_ees_next12mos_agri: null,
        max_ees_next12mos_household: null,
        max_ees_next12mos_other: null,
        is_employment_tax_liability: false,

        mailing_address: "",
        mailing_apt_suite: "",
        mailing_city: "",
        mailing_state: "",
        mailing_country: "",
        mailing_zip_code: "",

        card_holder_name: "",
        card_number: "",
        card_expire_date: "",
        card_cvc: "",
      },
    };
  },
  computed: {
  },
  mounted() {
    this.initializeOrderData();
    this.getPiTitle();
    this.initializeSalesWidget();
    this.initializePricings();
  },
  methods: {
    returnBack(page) {
      this.currentPage = page;
      this.getPiTitle();
    },
    formalNameFromSubtype() {
      if (this.order.order_type == ORDER_TYPES.CORPORATION) {
        return SiteUtils.einCorporationSubTypeName(this.order.sub_type);
      }
      return SiteUtils.einTrustSubTypeName(this.order.sub_type); // it is Trust otherwise
    },
    initializeOrderData() {
      AXIOS.get(`/ein/id/${this.$route.query.order}`)
        .then((res) => {
          if (res.status == 200) {
            let order = res.data;
            let backFromSign = !!localStorage.getItem("backFromSign");
            localStorage.removeItem("backFromSign");

            if (!backFromSign) {
              if (order.status == "Awaiting Signature") {
                this.$router.push({ path: "/ein-docu-sign", query: { order: this.$route.query.order } });
                return;
              }
              if (order.status == "Failed" || order.status == "Signed") {
                this.$router.push({ path: "/ein-payment", query: { order: this.$route.query.order } });
                return;
              }
              if (order.status != "Incomplete") { // Processing, Completed, Cancelled
                this.$router.push({ path: "/ein-confirmation", query: {
                  order: this.$route.query.order,
                  correlationId: order.correlationId,
                } });
                return;
              }
            }

            if (!order.last_saved_page) {
              this.order.first_name = order.first_name;
              this.order.last_name = order.last_name;
              this.order.email = order.email;
              this.order.id = order.id;
            } else {
              this.order = order;
              this.currentPage = order.last_saved_page;
              if (this.currentPage != this.PAGE_APPLICATION_SUMMARY) {
                this.goNextPage();
              } else { // in case of going back from the signing page
                this.getPiTitle();
              }
            }
          } else {
            setTimeout(() => {
              SnackUtils.danger("Order not found");
            }, 300);
            this.$router.push("/ein");
          }
        })
        .catch((e) => {
            setTimeout(() => {
              SnackUtils.danger("Order not found");
            }, 300);
            this.$router.push("/ein");
        });
    },

    initializeSalesWidget() {
      AXIOS.get(`/admin/salesList`).then((res) => {
        if (res.status == 200) {
          let list = res.data;
          let index = list.findIndex((it) => {
            let condition_1 =
              it.onOff === true && it.product == "EIN Application";
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
      AXIOS.get(`/admin/productPrice?product_name=EIN Application`).then((res) => {
        if (res.status == 200) {
          console.log(res);
          this.order.amount = res.data.customEnrollmentFee * 100;
        }
      });
    },

    getPiTitle() {
      switch (this.currentPage) {
        case this.PAGE_ENTITY_TYPE:
          this.piTitle = "Entity Type";
          this.piSubTitle = "Please select your entity type below";
          this.curProgressPercent = 10;
          break;
        case this.PAGE_ENTITY_SUBTYPE:
          if (this.order.order_type == ORDER_TYPES.PARTNERSHIP) {
            this.piTitle = "Partnership";
            this.piSubTitle =
              "You have chosen Partnership.<br/>Read the descriptions below and choose the type for which you are applying.";
          } else if (this.order.order_type == ORDER_TYPES.CORPORATION) {
            this.piTitle = "Corporations";
            this.piSubTitle =
              "You have chosen Corporation.<br/>Read the descriptions below and choose the type for which you are applying.";
          } else if (this.order.order_type == ORDER_TYPES.TRUST) {
            this.piTitle = "Trusts";
            this.piSubTitle =
              "You have chosen Trust.<br/>Read the descriptions below and choose the type for which you are applying.";
          }
          this.curProgressPercent = 12;
          break;
        case this.PAGE_FORM1:
          this.piTitle = "Why are you requesting an EIN?";
          this.piSubTitle =
            "Choose one reason that best describes why you are applying for an EIN.";
          this.curProgressPercent = 15;
          break;
        case this.PAGE_LLC_MEMBER_NUM:
          this.piTitle =
            "Tell Us About The Members Of The Limited Liability Company (LLC)";
          this.piSubTitle = "";
          this.curProgressPercent = 20;
          break;
        case this.PAGE_FORM6_INFO:
          if (this.order.order_type == ORDER_TYPES.SOLE) {
            this.piTitle = "Sole Proprietor Information";
          } else if (this.order.order_type == ORDER_TYPES.PARTNERSHIP) {
            this.piTitle = this.order.sub_type + " Information";
          } else if (this.order.order_type == ORDER_TYPES.CORPORATION) {
            this.piTitle = this.formalNameFromSubtype() + " Information";
          } else if (this.order.order_type == ORDER_TYPES.LLC) {
            this.piTitle = "Limited Liability Company (LLC) Information";
          } else if (this.order.order_type == ORDER_TYPES.ESTATE) {
            this.piTitle = "Estate Information";
          } else if (this.order.order_type == ORDER_TYPES.TRUST) {
            this.piTitle = this.formalNameFromSubtype() + " Information";
          }

          this.piSubTitle =
            "Must match IRS records or this application cannot be processed.<br/>The only punctuation and special characters allowed are hyphen (-) and ampersand (&).";
            this.curProgressPercent = 20;
          break;
        case this.PAGE_BUSINESS_PHYSICAL_LOC:
          if (this.order.order_type == ORDER_TYPES.SOLE) {
            this.piTitle =
              "Sole Proprietor Business Physical Location & Information";
          } else if (this.order.order_type == ORDER_TYPES.PARTNERSHIP) {
            this.piTitle =
              this.order.sub_type + " Business Physical Location & Information";
          } else if (this.order.order_type == ORDER_TYPES.CORPORATION) {
            this.piTitle =
              this.formalNameFromSubtype() +
              " Business Physical Location & Information";
          } else if (this.order.order_type == ORDER_TYPES.LLC) {
            this.piTitle =
              "Limited Liability Company (LLC) Business Physical Location & Information";
          } else if (this.order.order_type == ORDER_TYPES.ESTATE) {
            this.piTitle =
              "Estate Business Physical Location & Information";
          } else if (this.order.order_type == ORDER_TYPES.TRUST) {
            this.piTitle =
              this.formalNameFromSubtype() +
              " Business Physical Location & Information";
          }
          this.piSubTitle =
            "The only punctuation and special characters allowed are hyphen (-) and ampersand (&).<br/>The legal name may not contain any of the following endings: LLC, PLLC, LC.";
          this.curProgressPercent = 60;
          break;
        case this.PAGE_BUSINESS_TYPE:
          this.piTitle = "What Is The Type Of Business?";
          this.piSubTitle = "";
          this.curProgressPercent = 70;
          break;
        case this.PAGE_BUSINESS_SUB_TYPE:
          this.piTitle = "Business Details";
          this.piSubTitle = "";
          this.curProgressPercent = 71;
          break;
        case this.PAGE_MAILING_ADDRESS:
          this.curProgressPercent = 60;
          this.piTitle = "Mailing Address";
          this.piSubTitle =
            "Enter the mailing address for the business.<br/>The only special characters allowed are hyphen (-) and forward slash (/).<br/>You must enter a complete address. P.O. boxes and international mailing addresses are allowed.";
          break;
        case this.PAGE_RED_FLAG_QUESTIONS:
          if (this.order.order_type == ORDER_TYPES.SOLE) {
            this.piTitle = "Tell us more about the Sole Proprietor";
          } else if (this.order.order_type == ORDER_TYPES.PARTNERSHIP) {
            this.piTitle = "Tell us more about the " + this.order.sub_type;
          } else if (this.order.order_type == ORDER_TYPES.CORPORATION) {
            this.piTitle =
              "Tell us more about the " + this.formalNameFromSubtype();
          } else if (this.order.order_type == ORDER_TYPES.LLC) {
            this.piTitle =
              "Tell us more about the Limited Liability Company (LLC)";
          } else if (this.order.order_type == ORDER_TYPES.ESTATE) {
            this.piTitle =
              "Tell us more about the Estate";
          } else if (this.order.order_type == ORDER_TYPES.TRUST) {
            this.piTitle =
              "Tell us more about the " + this.formalNameFromSubtype();
          }
          this.curProgressPercent = 75;
          this.piSubTitle = "";
          break;
        case this.PAGE_DESCRIBE_EMPLOYEES:
          this.piTitle = "Describe Your Employees";
          this.curProgressPercent = 90;
          this.piSubTitle = "";
          break;
        case this.PAGE_APPLICATION_SUMMARY:
          this.piTitle = "EIN Application Summary";
          this.piSubTitle = "Your application summary";
          this.curProgressPercent = 90;
          break;
      }
    },
    onBackPage() {
      switch (this.currentPage) {
        case this.PAGE_ENTITY_SUBTYPE:
          // ORDER_TYPES.CORPORATION, ORDER_TYPES.PARTNERSHIP and ORDER_TYPES.TRUST
          this.currentPage = this.PAGE_ENTITY_TYPE;
          break;
        case this.PAGE_FORM1:
          if (this.order.order_type == ORDER_TYPES.CORPORATION) {
            this.currentPage = this.PAGE_ENTITY_SUBTYPE;
          } else if (this.order.order_type == ORDER_TYPES.PARTNERSHIP) {
            this.currentPage = this.PAGE_ENTITY_SUBTYPE;
          } else if (this.order.order_type == ORDER_TYPES.TRUST) {
            this.currentPage = this.PAGE_ENTITY_SUBTYPE;
          } else {
            this.currentPage = this.PAGE_ENTITY_TYPE;
          }
          break;
        case this.PAGE_LLC_MEMBER_NUM:
          this.currentPage = this.PAGE_FORM1;
          break;
        case this.PAGE_FORM6_INFO:
          if (this.order.order_type == ORDER_TYPES.CORPORATION) {
            if (this.order.sub_type == SUB_TYPES.SETTLEMENT_FUND) {
              this.currentPage = this.PAGE_ENTITY_SUBTYPE;
            } else {
              this.currentPage = this.PAGE_FORM1;
            }
          } else if (this.order.order_type == ORDER_TYPES.TRUST) {
            if (this.order.sub_type == TRUST_SUB_TYPES.ESCROW) {
              this.currentPage = this.PAGE_FORM1;
            } else {
              this.currentPage = this.PAGE_ENTITY_SUBTYPE;
            }
          } else if (this.order.order_type == ORDER_TYPES.LLC) {
            this.currentPage = this.PAGE_LLC_MEMBER_NUM;
          } else if (this.order.order_type == ORDER_TYPES.ESTATE) {
            this.currentPage = this.PAGE_ENTITY_TYPE;
          } else {
            this.currentPage = this.PAGE_FORM1;
          }
          break;
        case this.PAGE_BUSINESS_PHYSICAL_LOC:
          this.currentPage = this.PAGE_FORM6_INFO;
          break;
        case this.PAGE_MAILING_ADDRESS:
          this.currentPage = this.PAGE_BUSINESS_PHYSICAL_LOC;
          break;
        case this.PAGE_BUSINESS_TYPE:
          if (this.order.is_diff_mailing_address == 1) {
            this.currentPage = this.PAGE_MAILING_ADDRESS;
          } else {
            this.currentPage = this.PAGE_BUSINESS_PHYSICAL_LOC;
          }
          break;
        case this.PAGE_BUSINESS_SUB_TYPE:
          this.currentPage = this.PAGE_BUSINESS_TYPE;
          break;
        case this.PAGE_RED_FLAG_QUESTIONS:
          if ([ORDER_TYPES.TRUST, ORDER_TYPES.ESTATE].includes(this.order.order_type)) {
            if (this.order.is_diff_mailing_address == 1) {
              this.currentPage = this.PAGE_MAILING_ADDRESS;
            } else {
              this.currentPage = this.PAGE_BUSINESS_PHYSICAL_LOC;
            }
          } else {
            let alreadyDone = ['WAREHOUSING'].includes(this.order.business_type);
            if (alreadyDone) {
              this.currentPage = this.PAGE_BUSINESS_TYPE;
            } else {
              this.currentPage = this.PAGE_BUSINESS_SUB_TYPE;
            }
          }
          break;
        case this.PAGE_DESCRIBE_EMPLOYEES:
          this.currentPage = this.PAGE_RED_FLAG_QUESTIONS;
          break;
        case this.PAGE_APPLICATION_SUMMARY:
          if (this.order.is_w2_employees == "1") {
            this.currentPage = this.PAGE_DESCRIBE_EMPLOYEES;
          } else {
            this.currentPage = this.PAGE_RED_FLAG_QUESTIONS;
          }
          break;
      }

      this.getPiTitle();
    },
    onContinue(param) {
      this.order = Object.assign(this.order, param);
      if (this.currentPage == this.PAGE_BUSINESS_PHYSICAL_LOC && this.order.is_diff_mailing_address != 1) {
        this.order = Object.assign(this.order, {
          mailing_address: this.order.address,
          mailing_apt_suite: this.order.apt_suite,
          mailing_city: this.order.city,
          mailing_state: this.order.state,
          mailing_country: "US",
          mailing_zip_code: this.order.zip_code,
        });
      }
      this.order.last_saved_page = this.currentPage;

      AXIOS.post(`/ein/save/${this.$route.query.order}`, this.order)
        .then((response) => {
          if (response.status == 200) {
            this.goNextPage();
          } else {
            setTimeout(() => {
              SnackUtils.danger("Order not found");
            }, 300);
            this.$router.push("/ein");
          }
        })
        .catch((e) => {
          setTimeout(() => {
            SnackUtils.danger("Order not found");
          }, 300);
          this.$router.push("/ein");
        });
    },

    goNextPage() {
      let self = this;
      switch (this.currentPage) {
        case this.PAGE_ENTITY_TYPE:
          setTimeout(() => {
            if ([ORDER_TYPES.SOLE, ORDER_TYPES.LLC].includes(self.order.order_type)) {
              self.currentPage = self.PAGE_FORM1;
            } else if (self.order.order_type == ORDER_TYPES.ESTATE) {
              self.currentPage = self.PAGE_FORM6_INFO;
            } else { // PARTNERSHIP, CORPORATION, TRUST
              self.currentPage = self.PAGE_ENTITY_SUBTYPE;
            }

            self.getPiTitle(); // because of setTimeout()
          }, 300);
          break;
        case this.PAGE_ENTITY_SUBTYPE:
          setTimeout(() => {
            if (self.order.order_type == ORDER_TYPES.PARTNERSHIP) {
              self.currentPage = self.PAGE_FORM1;
            } else if (self.order.order_type == ORDER_TYPES.CORPORATION) {
              if (self.order.sub_type == SUB_TYPES.SETTLEMENT_FUND) {
                self.currentPage = self.PAGE_FORM6_INFO;
              } else {
                self.currentPage = self.PAGE_FORM1;
              }
            } else if (self.order.order_type == ORDER_TYPES.TRUST) {
              if (self.order.sub_type == TRUST_SUB_TYPES.ESCROW) {
                self.currentPage = self.PAGE_FORM1;
              } else {
                self.currentPage = self.PAGE_FORM6_INFO;
              }
            }
            self.getPiTitle(); // because of setTimeout()
          }, 300);
          break;
        case this.PAGE_FORM1:
          setTimeout(() => {
            if (self.order.order_type == ORDER_TYPES.LLC) {
              self.currentPage = self.PAGE_LLC_MEMBER_NUM;
            } else {
              self.currentPage = self.PAGE_FORM6_INFO;
            }

            self.getPiTitle(); // because of setTimeout()
          }, 300);
          break;
        case this.PAGE_LLC_MEMBER_NUM:
          this.currentPage = this.PAGE_FORM6_INFO;
          break;
        case this.PAGE_FORM6_INFO:
          this.currentPage = this.PAGE_BUSINESS_PHYSICAL_LOC;
          break;
        case this.PAGE_BUSINESS_PHYSICAL_LOC:
          if (this.order.is_diff_mailing_address == 1) {
            this.currentPage = this.PAGE_MAILING_ADDRESS;
          } else {
            let noBusiness = [ORDER_TYPES.ESTATE, ORDER_TYPES.TRUST].includes(this.order.order_type);
            if (noBusiness) {
              this.currentPage = this.PAGE_RED_FLAG_QUESTIONS;
            } else {
              this.currentPage = this.PAGE_BUSINESS_TYPE;
            }
          }
          break;
        case this.PAGE_MAILING_ADDRESS:
          let noBusiness = [ORDER_TYPES.ESTATE, ORDER_TYPES.TRUST].includes(this.order.order_type);
          if (noBusiness) {
            this.currentPage = this.PAGE_RED_FLAG_QUESTIONS;
          } else {
            this.currentPage = this.PAGE_BUSINESS_TYPE;
          }
          break;
        case this.PAGE_BUSINESS_TYPE:
          let alreadyDone = ['WAREHOUSING'].includes(this.order.business_type);
          if (alreadyDone) {
            this.currentPage = this.PAGE_RED_FLAG_QUESTIONS;
          } else {
            this.currentPage = this.PAGE_BUSINESS_SUB_TYPE;
          }
          break;
        case this.PAGE_BUSINESS_SUB_TYPE:
          this.currentPage = this.PAGE_RED_FLAG_QUESTIONS;
          break;
        case this.PAGE_RED_FLAG_QUESTIONS:
          if (this.order.is_w2_employees == "1") {
            this.currentPage = this.PAGE_DESCRIBE_EMPLOYEES;
          } else {
            this.currentPage = this.PAGE_APPLICATION_SUMMARY;
          }
          break;
        case this.PAGE_DESCRIBE_EMPLOYEES:
          this.currentPage = this.PAGE_APPLICATION_SUMMARY;
          break;
        case this.PAGE_APPLICATION_SUMMARY:
          this.$router.push({ path: "/ein-docu-sign", query: { order: this.$route.query.order } });
          break;
      }

      this.getPiTitle();
    },
    onChangeEntityType(value) {
      this.order.order_type = value;
    },
    onChangeReason4Ein(value) {
      this.order.reason = value;
    },
    onChangeSubType(value) {
      this.order.sub_type = value;
    },
    onChangeDiffMailingAddress(value) {
      this.order.is_diff_mailing_address = value;
    },
  },
};
</script>

<style>
</style>
