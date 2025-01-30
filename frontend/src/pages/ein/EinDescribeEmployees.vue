<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <ValidationObserver ref="form">
          <form>
            <div class="form-group">
              <label>
                What is the first date wages or annuities were or will be paid?
              </label>
              <div class="object-row">
                <div class="width-half">
                  <ValidationProvider
                    name="the first wage month"
                    rules="required"
                    v-slot="{ errors }"
                  >
                    <select
                      :class="[
                        'form-control',
                        errors.length > 0 ? 'is-invalid' : '',
                      ]"
                      v-model="formInfo.date_first_wages_month"
                    >
                      <option value="" style="color: gray">Select</option>
                      <option
                        v-for="(mo, idx) in months"
                        :key="idx"
                        v-html="mo"
                        :value="idx + 1"
                      ></option>
                    </select>
                    <span class="ein-form-err">{{ errors[0] }}</span>
                  </ValidationProvider>
                </div>
                <div class="width-half">
                  <ValidationProvider
                    name="The first wage year"
                    rules="required"
                    v-slot="{ errors }"
                  >
                    <select
                      :class="[
                        'form-control',
                        errors.length > 0 ? 'is-invalid' : '',
                      ]"
                      v-model="formInfo.date_first_wages_year"
                    >
                      <option
                        v-for="(year, idx) in allowedYears"
                        :key="idx"
                        v-html="year == firstAllowedYear ? '1999 or earlier' : year"
                        :value="year"
                      ></option>
                    </select>
                    <span class="ein-form-err">{{ errors[0] }}</span>
                  </ValidationProvider>
                </div>
              </div>
              <span class="ein-form-err" v-if="showYearMonthErr">
                The year/month cannot be before the date started or acquired or more than 1 year in the future.
              </span>
            </div>
            <div class="ein-warning-msg mb-3">
              <img src="@/assets/warning.png" class="ein-warning-mark" />
              If applicant is a withholding agent, enter the date income will
              first be paid to a non-resident alien.
            </div>
            <label
              >What is the highest number of employees expected in the next 12
              months?</label
            >
            <div class="form-group">
              <label>Number of agricultural employees</label>
              <ValidationProvider
                name="Number of agricultural employees"
                rules="required|min_value:0|max_value:9999999"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.max_ees_next12mos_agri"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group" v-if="order.order_type == 'SoleProprietor' || order.order_type == 'LLC'">
              <label>Number of household employees</label>
              <ValidationProvider
                name="Number of household employees"
                rules="required|min_value:0|max_value:9999999"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.max_ees_next12mos_household"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="form-group">
              <label>Number of other employees</label>
              <ValidationProvider
                name="Number of other employees"
                rules="required|min_value:0|max_value:9999999"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.max_ees_next12mos_other"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
            <div class="ein-warning-msg">
              <img src="@/assets/warning.png" class="ein-warning-mark" />Total
              number of employees must be at least 1.
            </div>
            <div class="pt-3">
              <label
                >Do you expect your employment tax liability to be $1,000 or
                less in a full calendar year?(January-December)?</label
              >
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_employment_tax_liability1"
                  name="is_employment_tax_liability"
                  class="custom-control-input"
                  :value="true"
                  v-model="formInfo.is_employment_tax_liability"
                />
                <label class="custom-control-label" for="is_employment_tax_liability1"
                  >Yes</label
                >
              </div>
              <div class="custom-control custom-radio">
                <input
                  type="radio"
                  id="is_employment_tax_liability2"
                  name="is_employment_tax_liability"
                  class="custom-control-input"
                  :value="false"
                  v-model="formInfo.is_employment_tax_liability"
                />
                <label class="custom-control-label" for="is_employment_tax_liability2"
                  >No</label
                >
              </div>
              <span class="ein-form-err" v-if="errors.is_employment_tax_liability"
                >Please choose an option.</span
              >
            </div>
            <div class="ein-warning-msg mt-3">
              <img src="@/assets/warning.png" class="ein-warning-mark" />By
              selecting "yes", you are electing to file an annual employment tax
              return, Form 944. If you prefer to file a quarterly return, Form
              941, select "no".
            </div>
          </form>
        </ValidationObserver>
      </div>
    </div>
    <ein-bottom-navigator @back="$emit('back')" @continue="onContinue" />
  </div>
</template>
<script>
import { MONTHS } from "../../scripts/constants";
import { SiteUtils } from "../../scripts/site-common";
import { SnackUtils } from "../../scripts/snack-common";
import EinBottomNavigator from "./EinBottomNavigator.vue";
import moment from "moment";

export default {
  props: ["order"],
  components: { EinBottomNavigator },
  created() {
    this.months = MONTHS;
    let startYear = this.order.start_date_year;
    let currentYear = moment().year();
    this.firstAllowedYear = currentYear - 25;
    this.allowedYears = [];
    for (let y = startYear; y <= currentYear + 1; y++) {
      this.allowedYears.push(y);
    }
  },
  data() {
    return {
      errors: {
        is_employment_tax_liability: false,
      },
      showYearMonthErr: false,
      formInfo: {
        date_first_wages_year: this.order.date_first_wages_year,
        date_first_wages_month: this.order.date_first_wages_month,
        is_employment_tax_liability: this.order.is_employment_tax_liability,
        max_ees_next12mos_agri: SiteUtils.isNullOrUnd(
          this.order.max_ees_next12mos_agri
        )
          ? 0
          : this.order.max_ees_next12mos_agri,
        max_ees_next12mos_household: SiteUtils.isNullOrUnd(
          this.order.max_ees_next12mos_household
        )
          ? 0
          : this.order.max_ees_next12mos_household,
        max_ees_next12mos_other: SiteUtils.isNullOrUnd(
          this.order.max_ees_next12mos_other
        )
          ? 0
          : this.order.max_ees_next12mos_other,
      },
    };
  },
  methods: {
    onContinue() {
      this.$refs.form.validate().then((success) => {
        if (!success) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }

        // verify month and year :
        // * First Wages
        //    Pay date must not be before start date (See start_date_month & start_date_year from EINBusinessPhysicalLocation).
        //    Pay date must not be greater than current date plus 1 year.

        let curDate = new Date();
        let date_first_wages_year = parseInt(
          this.formInfo.date_first_wages_year
        );
        let date_first_wages_month = parseInt(
          this.formInfo.date_first_wages_month
        );
        let date_first_wages = moment([
          date_first_wages_year,
          date_first_wages_month,
          1,
        ]).toDate();

        let start_year = parseInt(this.order.start_date_year);
        let start_month = parseInt(this.order.start_date_month);
        let startDate = moment([start_year, start_month, 1]).toDate();

        this.showYearMonthErr = false;
        if (date_first_wages.getTime() < startDate.getTime()) {
          this.showYearMonthErr = true;
          SnackUtils.warning(
            "Pay date must not be before start date ('" +
              moment(startDate).format("MMM/YYYY") +
              ")'"
          );
          return;
        }

        let diff = SiteUtils.monthDiff(curDate, date_first_wages);
        console.log(diff);

        if (diff > 12) {
          this.showYearMonthErr = true;
          SnackUtils.warning(
            "Pay date must not be greater than current date plus 1 year."
          );
          return;
        }

        if (SiteUtils.isNullOrUnd(this.formInfo.is_employment_tax_liability)) {
          this.errors.is_employment_tax_liability = true;
          return;
        } else {
          this.errors.is_employment_tax_liability = false;
        }

        let sum =
          parseInt(this.formInfo.max_ees_next12mos_agri) +
          parseInt(this.formInfo.max_ees_next12mos_household) +
          parseInt(this.formInfo.max_ees_next12mos_other);

        if (sum == 0) {
          SnackUtils.warning("Total number of employees must be at least 1.");
          return;
        }

        this.$emit("continue", this.formInfo);
      });
    },
  },
};
</script>
<style scoped>
.form-dims {
  max-width: 440px;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 40px;
  font-family: "Archivo", sans-serif;
}
.object-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.width-half {
  width: 48%;
}
</style>
