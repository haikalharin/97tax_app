<template>
  <page class="home-page">
    <div class="container">
      <oic-progress></oic-progress>
      <div class="row">
        <div class="col-12">
          <h2 class=" blueheader">Settlement Options</h2>
        </div>
        <div class="col-12">
          <p>Great! We've analyzed the information you entered, compared this IRS published requirements, and calculated your options below for you. Please select the option that works best for you. </p>
        </div>
      </div>


      <div class="row">
        <div class="col-12">
          <h2 class="blueheader">Payment Plan (24 Months)</h2>
        </div>
        <div class="col-12">
          <p>Congratulations! you qualify for a settlement and payment plan. This option would allow you to pay off a fraction of you debt over 24 months.</p>
        </div>
      </div>

      <div class="row">
          <div class="col-12 col-md-4">
            <div class="row">
                <p class="font-weight-bold m-auto">This will submit an offer for</p>
              <div class="col-12 col-lg-8 m-auto">
                <h1 class="display-1 align-middle font-weight-bold greenheader text-center">22%</h1>
              </div>
              <div class="col-12 col-lg-4 m-auto">
                <p class="font-weight-bold align-middle greentext text-center">of your total debt</p>
              </div>
            </div>
          </div>
        <div class="col-12 col-md-4">
          <div class="row">
          <p class="font-weight-bold m-auto">with monthly payments of</p>
            <div class="col-12 col-lg-6 m-auto">
              <h1 class="display-1 align-middle font-weight-bold greenheader text-center">$60</h1>
            </div>
            <div class="col-12 col-lg-6 m-auto">
              <p class="font-weight-bold align-middle greentext text-center">/month</p>
            </div>
          </div>
        </div>
        <div class="p-2 mt-5 mb-4 ml-auto mr-auto col-12 col-md-4">
          <button class=" btn-block btn-primary font-weight-bold" v-on:click="handleContinue">SELECT THIS OPTION</button>
          <p class="font-weight-bold mt-2 text-center">What if I can't afford these payments?</p>
        </div>
      </div>
      <div class="row">
        <div class="col-12">
          <h2 class=" blueheader">Exceptional Circumstances</h2>
        </div>
        <div class="col-12">
          <p>You may qualify to submit a lower settlement offer if you have "Exceptional Circumstances." Please enter the largest monthly payment that you can afford and briefly describe your situation below.</p>
        </div>
        <div class="col-12">
          <p class="bluetext font-weight-bold">>What is an "Exceptional Circumstance?"</p>
        </div>
      </div>

      <div class="col-12">
        <p class="m-0">What amount can you pay monthly?</p>
        <input-text
          classes="mb-4"
          placeholder="0"
          :required="false"
        ></input-text>
      </div>
      <div class="col-12">
        <p class="m-1">An explanation of your exceptional circumstance is required (50 word minimum)</p>
        <input-text
          classes="mb-4"
          :required="false"
        ></input-text>
      </div>

      <div class="row">
        <div class="col-12 col-md-4">
          <div class="row">
            <p class="font-weight-bold m-auto">This would settle your debt for</p>
            <div class="col-12 col-lg-8 m-auto">
              <h1 class="display-1 align-middle font-weight-bold greenheader text-center">5%</h1>
            </div>
            <div class="col-12 col-lg-4 m-auto">
              <p class="font-weight-bold align-middle greentext text-center">of your total debt</p>
            </div>
          </div>
        </div>
        <div class="col-12 col-md-4">
          <div class="row">
            <p class="font-weight-bold m-auto">with monthly payments of</p>
            <div class="col-12 col-lg-6 m-auto">
              <h1 class="display-1 align-middle font-weight-bold greenheader text-center">$20</h1>
            </div>
            <div class="col-12 col-lg-6 m-auto">
              <p class="font-weight-bold align-middle greentext text-center">/month</p>
            </div>
          </div>
        </div>

        <div class="p-2 mt-5 mb-4 ml-auto mr-auto col-12 col-md-4">
          <button class=" btn-block btn-primary font-weight-bold" v-on:click="handleContinue">SELECT THIS OPTION</button>
          <p class="font-weight-bold mt-2 text-center">What if I can't afford these payments?</p>
        </div>
      </div>
    </div>
      <div class="row">
        <div class="p-2 mr-auto mt-0 ml-auto mr-auto mb-4 col-12 col-lg-4 ">
          <button class="btn-block btn-primary font-weight-bold text-uppercase">Back</button>
        </div>
      </div>







      <p>Based on the information you provided, we have a few settlement options for you. Please select one of the following:</p>

      <div v-if="qualifies12 || qualifies24">
        Congratulations, you qualify for a settlement! Please select the option that works best for you.

        <div v-if="qualifies12">
          <label>
            <input type="radio" v-model="settlement.selectedOption" value="12">
            A settlement of ${{existing.oicCalculations.calc12MonthSettlement}} over 12 months (${{monthlyPayment12}} per month)
          </label>
        </div>
        <div v-if="qualifies24">
          <label>
            <input type="radio" v-model="settlement.selectedOption" value="24">
            A settlement of ${{existing.oicCalculations.calc24MonthSettlement}} over 24 months (${{monthlyPayment24}} per month)
          </label>
        </div>
        <div>
          <label>
            <input type="radio" v-model="settlement.selectedOption" value="exceptional">
            I cannot afford either of the above options
          </label>
        </div>

        <div v-if="settlement.selectedOption != 'exceptional'">
          <button class="btn btn-secondary" v-on:click="saveForm" :disabled="!settlement.selectedOption">Select This Option</button>
        </div>
      </div>

      <div v-if="settlement.selectedOption === 'exceptional' || (!qualifies12 && !qualifies24)">
        <div class="row mt-4">
          <div class="col">
            <h3>Exceptional Circumstance: 24-Months</h3>
          </div>
        </div>
        <div>
          <p>If you have exceptional circumstances, you can enter an amount for settlement that you are able to pay. Please select one of the following payment options and enter the largest amount that you can pay.</p>
        </div>
        <div class="row">
          <input-text
            ref="exceptionPaymentAmountMonthly"
            :binding="form.exceptionPaymentAmountMonthly"
            placeholder="0"
            type="integer"
            label="What amount can you pay monthly?"
            :required="true"
          ></input-text>
        </div>
        <div class="row">
          <input-text
            ref="exceptionExplanation"
            :binding="form.exceptionExplanation"
            :multiline="true"
            classes="form-group col-12"
            label="An explanation of your exceptional circumstance is required (50 words mininum)"
            :required="true"
          ></input-text>
        </div>
        <div>
          <button class="btn greenbutton">Select This Option</button>
        </div>

        <div class="row mt-4">
          <div class="col">
            <h4>Exceptional Circumstance: 5 Payments</h4>
            <p>Five periodic payments, 20% due with application: Please enter 5 payment amounts and dates:</p>
          </div>
        </div>
        <div class="row">
          <input-text
            ref="exceptionPaymentAmount1"
            :binding="form.exceptionPaymentAmount1"
            placeholder="0"
            type="integer"
            label="First Payment"
            :required="true"
          ></input-text>
          <input-text
            ref="exceptionPaymentDate1"
            :binding="form.exceptionPaymentDate1"
            placeholder="mm/dd/yyyy"
            :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
            label="First Payment Date"
            :required="true"
          ></input-text>
        </div>
        <div class="row">
          <input-text
            ref="exceptionPaymentAmount2"
            :binding="form.exceptionPaymentAmount2"
            placeholder="0"
            type="integer"
            label="Second Payment"
            :required="true"
          ></input-text>
          <input-text
            ref="exceptionPaymentDate2"
            :binding="form.exceptionPaymentDate2"
            placeholder="mm/dd/yyyy"
            :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
            label="Second Payment Date"
            :required="true"
          ></input-text>
        </div>
        <div class="row">
          <input-text
            ref="exceptionPaymentAmount3"
            :binding="form.exceptionPaymentAmount3"
            placeholder="0"
            type="integer"
            label="Third Payment"
            :required="true"
          ></input-text>
          <input-text
            ref="exceptionPaymentDate3"
            :binding="form.exceptionPaymentDate3"
            placeholder="mm/dd/yyyy"
            :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
            label="Third Payment Date"
            :required="true"
          ></input-text>
        </div>
        <div class="row">
          <input-text
            ref="exceptionPaymentAmount4"
            :binding="form.exceptionPaymentAmount4"
            placeholder="0"
            type="integer"
            label="Forth Payment"
            :required="true"
          ></input-text>
          <input-text
            ref="exceptionPaymentDate4"
            :binding="form.exceptionPaymentDate4"
            placeholder="mm/dd/yyyy"
            :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
            label="Forth Payment Date"
            :required="true"
          ></input-text>
        </div>
        <div class="row">
          <input-text
            ref="exceptionPaymentAmount5"
            :binding="form.exceptionPaymentAmount5"
            placeholder="0"
            type="integer"
            label="Fifth Payment"
            :required="true"
          ></input-text>
          <input-text
            ref="exceptionPaymentDate5"
            :binding="form.exceptionPaymentDate5"
            placeholder="mm/dd/yyyy"
            :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
            label="Fifth Payment Date"
            :required="true"
          ></input-text>
        </div>
        <div class="row">
          <input-text
            ref="exceptionExplanation"
            :binding="form.exceptionExplanation"
            :multiline="true"
            classes="form-group col-12"
            label="An explanation of your exceptional circumstance is required (50 words minimum)"
            :required="true"
            :minWords="50"
          ></input-text>
        </div>
        <div class="row">
          <div class="col">
            <p>${{dueWithApplication5}} due with application (20% of total).</p>
          </div>
        </div>
        <div>
          <button class="btn btn-secondary" v-on:click="saveException5">Select This Option</button>
        </div>
      </div>
    </div>
  </page>
</template>

<!-- sharea sale tag -->
<script>
  var shareasaleSSCID=shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e,a,r,s,t){
      if(e&&a)
            {var o,n=s?"; path="+s:"",i=t?"; domain="+t:"",l="";r&&((o=new Date).setTime(o.getTime()+r),l="; expires="+o.toUTCString()),
        document.cookie=e+"="+a+l+n+i
      }}
        function shareasaleGetParameterByName(e,a)
        {a||(a=window.location.href),e=e.replace(/[\[\]]/g,"\\$&");
        var r=new RegExp("[?&]"+e+"(=([^&#]*)|&|#|$)").exec(a);
return r?r[2]?decodeURIComponent(r[2].replace(/\+/g," ")):"":null}
shareasaleSSCID&&shareasaleSetCookie("shareasaleSSCID",
shareasaleSSCID,94670778e4,"/");
</script>

<script>
  import {AXIOS} from '../scripts/http-common'
  import {SiteUtils} from '../scripts/site-common'
  import InputText from "../components/input/Text";
  import InputCheckbox from "../components/input/Checkbox";

  export default {
    components: {InputCheckbox, InputText},
    data() {
      return {
        existing: {
          debtOwed: 0,
          oicCalculations: {
            id: '',
            calc12MonthSettlement: 0,
            calc24MonthSettlement: 0,
            calcDueWithApplication: 0,
            selectedSettlement: ''
          }
        },
        form: {
          exceptionPaymentAmountMonthly: {},
          exceptionPaymentAmount1: {},
          exceptionPaymentAmount2: {},
          exceptionPaymentAmount3: {},
          exceptionPaymentAmount4: {},
          exceptionPaymentAmount5: {},
          exceptionPaymentDate1: {},
          exceptionPaymentDate2: {},
          exceptionPaymentDate3: {},
          exceptionPaymentDate4: {},
          exceptionPaymentDate5: {},
          exceptionExplanation: {}
        },
        settlement: {
          selectedOption: null
        }
      }
    },
    computed: {
      qualifies12() {
        let threshold = .9 * this.existing.debtOwed;
        return this.existing.oicCalculations.calc12MonthSettlement <= threshold;
      },
      monthlyPayment12() {
        return Math.ceil(this.existing.oicCalculations.calc12MonthSettlement / 12);
      },
      qualifies24() {
        let threshold = .9 * this.existing.debtOwed;
        return this.existing.oicCalculations.calc24MonthSettlement <= threshold;
      },
      monthlyPayment24() {
        return Math.ceil(this.existing.oicCalculations.calc24MonthSettlement / 24);
      },
      dueWithApplication5() {
        let fields = [
          this.form.exceptionPaymentAmount1,
          this.form.exceptionPaymentAmount2,
          this.form.exceptionPaymentAmount3,
          this.form.exceptionPaymentAmount4,
          this.form.exceptionPaymentAmount5
        ];
        let total = 0;
        for (let f of fields) {
          if (!isNaN(f.value)) {
            total += f.value;
          }
        }
        let due = Math.ceil(total * .2);
        if (isNaN(due)) {
          return 0;
        } else {
          return due;
        }
      }
    },
    mounted() {
      let self = this;
      AXIOS.get('/oic')
        .then(response => {
          self.existing = response.data;
        })
    },
    methods: {
      saveException24() {
        let valid1 = this.$refs.exceptionPaymentAmountMonthly.validate();
        let valid2 = this.$refs.exceptionExplanation.validate();
        if (valid1 && valid2) {
          this.settlement.selectedOption = 'exception-24';
          this.saveForm();
        }
      },
      saveException5() {
        let fields = [
          'exceptionPaymentAmount1',
          'exceptionPaymentDate1',
          'exceptionPaymentAmount2',
          'exceptionPaymentDate2',
          'exceptionPaymentAmount3',
          'exceptionPaymentDate3',
          'exceptionPaymentAmount4',
          'exceptionPaymentDate4',
          'exceptionPaymentAmount5',
          'exceptionPaymentDate5',
          'exceptionExplanation'
        ];

        let isValid = true;
        for (let k of fields) {
          isValid = this.$refs[k].validate() && isValid;
        }

        if (isValid) {
          this.settlement.selectedOption = 'exception-5';
          this.saveForm();
        }
      },
      saveForm() {
        let entity = this.existing.oicCalculations;
        for (let k in this.form) {
          entity[k] = this.form[k].value
        }
        entity.selectedSettlement = this.settlement.selectedOption;
        AXIOS.put('/oic', this.existing)
          .then(response => {
            this.$router.push("oic-additional-info");
          });
      }
    }
  };
</script>
<style scoped>
  .btn-primary {
    background-color: #51c2a4;
    border: 0px;
    font-size: 1.5em;
  }

  .aside-item p {
    font-size: 1.5em;
  }
  .home-page {
    background-image: url(./../assets/banner.jpg);
    background-size: cover;
  }

  button.greenbutton {
    color: #51c2a4;
  }

  h1.greenheader {
    color: #51c2a4;
  }

  h2.blueheader {
    color: #2896C5;
  }
  p.bluetext {
    color: #2896C5;
  }
  p.greentext {
    color: #51c2a4;
  }
</style>
