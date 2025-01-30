<template>
  <page class="home-page">
    <div class="container">
      <div class="row">
        <div class="col text-center">
          <h1>Determine Eligibility</h1>
          <p>We need to get a picture of your current net worth to determine eligibility for a reduction of tax liability.</p>
          <p>All amounts must be in USD.</p>
        </div>
      </div>

      <div class="row align-items-end">
        <form-input-field
          v-model="data.totalOwed"
          :error="errors.totalOwed"
          label="Total owed to IRS"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasBankAccounts"
          :error="errors.hasBankAccounts"
          label="Do you have any bank accounts, including checking, savings, money market, or CD accounts?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasBankAccounts"
          v-model="data.totalBankAccountValue"
          :error="errors.totalBankAccountValue"
          label="Total value of accounts"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasCash"
          :error="errors.hasCash"
          label="Do you have cash, virtual currency, stored value cards, or online accounts?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasCash"
          v-model="data.totalCashValue"
          :error="errors.totalCashValue"
          label="Total cash value"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasInvestment"
          :error="errors.hasInvestment"
          label="Do you have investments, including stocks or bonds?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasInvestment"
          v-model="data.totalInvestmentValue"
          :error="errors.totalInvestmentValue"
          label="Total market value"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="hasInvestment">
        <yes-no-select-field
          v-model="hasInvestmentLoan"
          :error="errors.hasInvestmentLoan"
          label="Have you taken out any loans on your investments?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasInvestmentLoan"
          v-model="data.totalInvestmentLoan"
          :error="errors.totalInvestmentLoan"
          label="Total loan balance"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasRetirement"
          :error="errors.hasRetirement"
          label="Do you have any retirement account, including 401K or IRA?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasRetirement"
          v-model="data.totalRetirementValue"
          :error="errors.totalRetirementValue"
          label="Total value of retirement accounts"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="hasRetirement">
        <yes-no-select-field
          v-model="hasRetirementLoan"
          :error="errors.hasRetirementLoan"
          label="Have you taken out any loans on your retirement accounts?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasRetirementLoan"
          v-model="data.totalRetirementLoan"
          :error="errors.totalRetirementLoan"
          label="Total loan balance"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasLifeInsurance"
          :error="errors.hasLifeInsurance"
          label="Do you have any life insurance with a cash value?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasLifeInsurance"
          v-model="data.lifeInsuranceValue"
          :error="errors.lifeInsuranceValue"
          label="Total cash value of life insurance"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="hasLifeInsurance">
        <yes-no-select-field
          v-model="hasLifeInsuranceLoan"
          :error="errors.hasLifeInsuranceLoan"
          label="Have you taken out any loans on your life insurance?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasLifeInsuranceLoan"
          v-model="data.lifeInsuranceLoan"
          :error="errors.lifeInsuranceLoan"
          label="Total loan balance"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasRealEstate"
          :error="errors.hasRealEstate"
          label="Do you own real estate?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasRealEstate"
          v-model="data.realEstateValue"
          :error="errors.realEstateValue"
          label="Total market value of real estate"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="hasRealEstate">
        <yes-no-select-field
          v-model="hasRealEstateLoan"
          :error="errors.hasRealEstateLoan"
          label="Do you have any loans on the real estate?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasRealEstateLoan"
          v-model="data.realEstateLoan"
          :error="errors.realEstateLoan"
          label="Total amount owed"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasVehicles"
          :error="errors.hasVehicles"
          label="Do you own any vehicles?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasVehicles"
          v-model="data.vehicleValue"
          :error="errors.vehicleValue"
          label="Total market value of vehicles"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="hasVehicles">
        <yes-no-select-field
          v-model="hasVehicleLoan"
          :error="errors.hasVehicleLoan"
          label="Do you have any loans on the vehicles?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasVehicleLoan"
          v-model="data.vehicleLoan"
          :error="errors.vehicleLoan"
          label="Total amount owed"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="hasValuables"
          :error="errors.hasValuables"
          label="Do you own other valuables, such as artwork, collections, jewelry, items of value in safe deposit boxes, interest in a company or business which is not publicly traded, etc.?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasValuables"
          v-model="data.valuablesValue"
          :error="errors.valuablesValue"
          label="Total market value of valuables"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="hasValuables">
        <yes-no-select-field
          v-model="hasValuablesLoan"
          :error="errors.hasValuablesLoan"
          label="Do you have any loans on the valuable items?">
        </yes-no-select-field>
        <form-input-field
          v-if="hasValuablesLoan"
          v-model="data.valuablesLoan"
          :error="errors.valuablesLoan"
          label="Total amount owed"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <form-input-field
          v-model="data.grossIncome"
          :error="errors.grossIncome"
          label="Total household monthly income (all income that supports the household, regardless of source)"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <form-input-field
          v-model="data.grossExpenses"
          :error="errors.grossExpenses"
          label="Total monthly household expenses"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end">
        <yes-no-select-field
          v-model="isSelfEmployed"
          :error="errors.isSelfEmployed"
          label="Are you self-employed?">
        </yes-no-select-field>
      </div>

      <div class="row align-items-end" v-if="isSelfEmployed">
        <yes-no-select-field
          v-model="busHasBankAccounts"
          :error="errors.busHasBankAccounts"
          label="Does your business have any bank accounts, including checking, savings, money market, or CD accounts?">
        </yes-no-select-field>
        <form-input-field
          v-if="busHasBankAccounts"
          v-model="data.busBankAccountValue"
          :error="errors.busBankAccountValue"
          label="Total value of accounts"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="isSelfEmployed">
        <yes-no-select-field
          v-model="busHasCash"
          :error="errors.busHasCash"
          label="Does your business have cash, virtual currency, stored value cards, or online accounts?">
        </yes-no-select-field>
        <form-input-field
          v-if="busHasCash"
          v-model="data.busCashValue"
          :error="errors.busCashValue"
          label="Total cash value"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end" v-if="isSelfEmployed">
        <yes-no-select-field
          v-model="busHasAssets"
          :error="errors.busHasAssets"
          label="Does your business have any assets, such as tools, books, machinery, equipment, vehicles, or real estate?">
        </yes-no-select-field>
        <form-input-field
          v-if="busHasAssets"
          v-model="data.busAssetValue"
          :error="errors.busAssetValue"
          label="Total market value of assets"
          placeholder="00.00">
        </form-input-field>
      </div>
      <div class="row align-items-end" v-if="busHasAssets">
        <yes-no-select-field
          v-model="busAssetsLoan"
          :error="errors.busAssetsLoan"
          label="Do you have any loans on the assets?">
        </yes-no-select-field>
        <form-input-field
          v-if="busAssetsLoan"
          v-model="data.busAssetLoan"
          :error="errors.busAssetLoan"
          label="Total amount owed"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end" v-if="isSelfEmployed">
        <form-input-field
          v-model="data.busGrossIncome"
          :error="errors.busGrossIncome"
          label="Total monthly gross business income (you may average 6-12 months income)"
          placeholder="00.00">
        </form-input-field>
      </div>

      <div class="row align-items-end" v-if="isSelfEmployed">
        <form-input-field
          v-model="data.busGrossExpense"
          :error="errors.busGrossExpense"
          label="Total business expenses (you may average 6-12 months expenses)"
          placeholder="00.00">
        </form-input-field>
      </div>


      <div class="row">
        <div class="col">
          <button class="btn btn-primary" v-on:click="calculateOfferAmount">Check Eligibility</button>
        </div>
      </div>

    </div>
    <b-modal ref="qualifyModal24" title="You may qualify" ok-title="Yes" cancel-title="No">
      <p class="my-4">We think you qualify to reduce your IRS tax liability down to
        only ${{offer.amount24}} over a 6-12 month period,
        or ${{offer.amount12}} over a 5 month period.</p>
      <p class="my-4">We need more details to fill the required paperwork. Would you like to continue?</p>
    </b-modal>
    <b-modal ref="qualifyModal12" title="You may qualify" ok-title="Yes" cancel-title="No">
      <p class="my-4">We think you qualify to pay
        only ${{offer.amount12}} over a 5 month period.</p>
      <p class="my-4">We need more details to fill the required paperwork. Would you like to continue?</p>
    </b-modal>
    <b-modal ref="qualifyModalNone" title="Special Circumstances" ok-title="Yes" cancel-title="No">
      <p class="my-4">It seems like you have enough income and/or assets to pay off this IRS debt. But don't worry! Under special circumstances you can still reduce your tax debt.</p>
      <p class="my-4">Would you like to explore some more options to reduce your tax debt?</p>
    </b-modal>
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
  let isBool = function(b) {
    return typeof(b) === 'boolean';
  };

  let parseNumber = function(b) {
    if (/[^0-9\.\$]/.exec(b)) {
      return false;
    } else {
      let filtered = b.replace(/[^0-9\.]/g, "");
      let parsed = parseFloat(filtered);
      if (isNaN(parsed)) {
        return null;
      } else {
        return parsed;
      }
    }
  };

  let validateBool = function(name, dataObject, errors) {
    if (isBool(dataObject[name])) {
      errors[name] = false;
      return true;
    } else {
      errors[name] = true;
      return false;
    }
  };

  let validateDollars = function(name, dataObject, errors) {
    if (parseNumber(dataObject[name])) {
      errors[name] = false;
      return true;
    } else {
      errors[name] = true;
      return false;
    }
  };

  export default {
    methods: {
      validate: function() {
        let isValid = true;
        isValid &= validateBool('hasBankAccounts', this, this.errors);
        isValid &= validateBool('hasCash', this, this.errors);
        isValid &= validateBool('hasInvestment', this, this.errors);
        isValid &= validateBool('hasRetirement', this, this.errors);
        isValid &= validateBool('hasLifeInsurance', this, this.errors);
        isValid &= validateBool('hasRealEstate', this, this.errors);
        isValid &= validateBool('hasVehicles', this, this.errors);
        isValid &= validateBool('hasValuables', this, this.errors);
        isValid &= validateBool('isSelfEmployed', this, this.errors);

        isValid &= validateDollars('totalOwed', this.data, this.errors);
        isValid &= validateDollars('grossIncome', this.data, this.errors);
        isValid &= validateDollars('grossExpenses', this.data, this.errors);

        if (this.hasBankAccounts) {
          isValid &= validateDollars('totalBankAccountValue', this.data, this.errors);
        }

        if (this.hasCash) {
          isValid &= validateDollars('totalCashValue', this.data, this.errors);
        }

        if (this.hasInvestment) {
          isValid &= validateDollars('totalInvestmentValue', this.data, this.errors);

          isValid &= validateBool('hasInvestmentLoan', this, this.errors);

          if (this.hasInvestmentLoan) {
            isValid &= validateDollars('totalInvestmentLoan', this.data, this.errors);
          }
        }

        if (this.hasRetirement) {
          isValid &= validateDollars('totalRetirementValue', this.data, this.errors);

          isValid &= validateBool('hasRetirementLoan', this, this.errors);

          if (this.hasRetirementLoan) {
            isValid &= validateDollars('totalRetirementLoan', this.data, this.errors);
          }
        }

        if (this.hasLifeInsurance) {
          isValid &= validateDollars('lifeInsuranceValue', this.data, this.errors);

          isValid &= validateBool('hasLifeInsuranceLoan', this, this.errors);

          if (this.hasLifeInsuranceLoan) {
            isValid &= validateDollars('lifeInsuranceLoan', this.data, this.errors);
          }
        }

        if (this.hasRealEstate) {
          isValid &= validateDollars('realEstateValue', this.data, this.errors);

          isValid &= validateBool('hasRealEstateLoan', this, this.errors);

          if (this.hasRealEstateLoan) {
            isValid &= validateDollars('realEstateLoan', this.data, this.errors);
          }
        }

        if (this.hasVehicles) {
          isValid &= validateDollars('vehicleValue', this.data, this.errors);

          isValid &= validateBool('hasVehicleLoan', this, this.errors);

          if (this.hasVehicleLoan) {
            isValid &= validateDollars('vehicleLoan', this.data, this.errors);
          }
        }

        if (this.hasValuables) {
          isValid &= validateDollars('valuablesValue', this.data, this.errors);

          isValid &= validateBool('hasValuablesLoan', this, this.errors);

          if (this.hasValuablesLoan) {
            isValid &= validateDollars('valuablesLoan', this.data, this.errors);
          }
        }

        if (this.isSelfEmployed) {

          isValid &= validateBool('busHasBankAccounts', this, this.errors);
          isValid &= validateBool('busHasCash', this, this.errors);
          isValid &= validateBool('busHasAssets', this, this.errors);

          if (this.busHasBankAccounts) {
            isValid &= validateDollars('busBankAccountValue', this.data, this.errors);
          }

          if (this.busHasCash) {
            isValid &= validateDollars('busCashValue', this.data, this.errors);
          }

          if (this.busHasAssets) {
            isValid &= validateDollars('busAssetValue', this.data, this.errors);

            isValid &= validateBool('busAssetsLoan', this, this.errors);

            if (this.busAssetsLoan) {
              isValid &= validateDollars('busAssetLoan', this.data, this.errors);
            }
          }

          isValid &= validateDollars('busGrossIncome', this.data, this.errors);
          isValid &= validateDollars('busGrossExpense', this.data, this.errors);


        }

        return isValid;
      },






      calculateOfferAmount: function() {
        let isValid = this.validate();
        if (isValid) {

          let netWorth = 0;

          if (this.hasBankAccounts) {
            netWorth += parseNumber(this.data.totalBankAccountValue);
          }

          if (this.hasCash) {
            netWorth += parseNumber(this.data.totalCashValue);
          }

          let value = 0;
          let liability = 0;
          if (this.hasInvestment) {
            value = parseNumber(this.data.totalInvestmentValue);
            if (this.hasInvestmentLoan) {
              liability = parseNumber(this.data.totalInvestmentLoan);
            }
            let net = value * .8 - liability;
            if (net > 0) {
              netWorth += net;
            }
          }

          value = 0;
          liability = 0;
          if (this.hasRetirement) {
            value += parseNumber(this.data.totalRetirementValue);
            if (this.hasRetirementLoan) {
              liability += parseNumber(this.data.totalRetirementLoan);
            }
            let net = value * .8 - liability;
            if (net > 0) {
              netWorth += net;
            }
          }

          value = 0;
          liability = 0;
          if (this.hasLifeInsurance) {
            value += parseNumber(this.data.lifeInsuranceValue);
            if (this.hasLifeInsuranceLoan) {
              liability += parseNumber(this.data.lifeInsuranceLoan);
            }
            let net = value * .8 - liability;
            if (net > 0) {
              netWorth += net;
            }
          }

          value = 0;
          liability = 0;
          if (this.hasRealEstate) {
            value += parseNumber(this.data.realEstateValue);
            if (this.hasRealEstateLoan) {
              liability += parseNumber(this.data.realEstateLoan);
            }
            let net = value * .8 - liability;
            if (net > 0) {
              netWorth += net;
            }
          }

          value = 0;
          liability = 0;
          if (this.hasVehicles) {
            value += parseNumber(this.data.vehicleValue);
            if (this.hasVehicleLoan) {
              liability += parseNumber(this.data.vehicleLoan);
            }
            let net = value * .8 - liability;
            if (net > 0) {
              netWorth += net;
            }
          }

          value = 0;
          liability = 0;
          if (this.hasValuables) {
            value += parseNumber(this.data.valuablesValue);
            if (this.hasValuablesLoan) {
              liability += parseNumber(this.data.valuablesLoan);
            }
            let net = value * .8 - liability;
            if (net > 0) {
              netWorth += net;
            }
          }

          if (this.isSelfEmployed) {

            if (this.busHasBankAccounts) {
              netWorth += parseNumber(this.data.busBankAccountValue);
            }

            if (this.busHasCash) {
              netWorth += parseNumber(this.data.busCashValue);
            }

            value = 0;
            liability = 0;
            if (this.busHasAssets) {
              value += parseNumber(this.data.busAssetValue);
              if (this.busAssetsLoan) {
                liability += parseNumber(this.data.busAssetLoan);
              }
              let net = value * .8 - liability;
              if (net > 0) {
                netWorth += net;
              }
            }

          }


          let remainingPersonalIncome = parseNumber(this.data.grossIncome) - parseNumber(this.data.grossExpenses);

          let futureRemainingIncome12 = 12 * remainingPersonalIncome;
          let futureRemainingIncome24 = 24 * remainingPersonalIncome;

          if (remainingPersonalIncome < 0) {
            futureRemainingIncome12 = 0;
            futureRemainingIncome24 = 0;
          }

          console.log("futureRemainingIncome12 "+futureRemainingIncome12);
          console.log("futureRemainingIncome24 "+futureRemainingIncome24);

          console.log("net worth "+netWorth);

          this.offer.amount12 = Math.ceil(netWorth + futureRemainingIncome12);
          this.offer.amount24 = Math.ceil(netWorth + futureRemainingIncome24);

          let debt = parseNumber(this.data.totalOwed);

          if (this.offer.amount24 < debt) {
            this.$refs.qualifyModal24.show();
          } else if (this.offer.amount12 < debt) {
            this.$refs.qualifyModal12.show();
          } else {
            this.$refs.qualifyModalNone.show();
          }

        }
      }
    },
    data() {
      return {
        hasBankAccounts: '',
        hasCash: '',
        hasInvestment: '',
        hasInvestmentLoan: '',
        hasRetirement: '',
        hasRetirementLoan: '',
        hasLifeInsurance: '',
        hasLifeInsuranceLoan: '',
        hasRealEstate: '',
        hasRealEstateLoan: '',
        hasVehicles: '',
        hasVehicleLoan: '',
        hasValuables: '',
        hasValuablesLoan: '',
        isSelfEmployed: '',
        busHasBankAccounts: '',
        busHasCash: '',
        busHasAssets: '',
        busAssetsLoan: '',
        data: {
          totalOwed: '',
          totalBankAccountValue: '',
          totalCashValue: '',
          totalInvestmentValue: '',
          totalInvestmentLoan: '',
          totalRetirementValue: '',
          totalRetirementLoan: '',
          lifeInsuranceValue: '',
          lifeInsuranceLoan: '',
          realEstateValue: '',
          realEstateLoan: '',
          vehicleValue: '',
          vehicleLoan: '',
          valuablesValue: '',
          valuablesLoan: '',
          grossIncome: '',
          grossExpenses: '',
          busBankAccountValue: '',
          busCashValue: '',
          busAssetValue: '',
          busAssetLoan: '',
          busGrossIncome: '',
          busGrossExpense: '',
        },
        errors: {
          hasBankAccounts: false,
          hasCash: false,
          hasInvestment: false,
          hasInvestmentLoan: false,
          hasRetirement: false,
          hasRetirementLoan: false,
          hasLifeInsurance: false,
          hasLifeInsuranceLoan: false,
          hasRealEstate: false,
          hasRealEstateLoan: false,
          hasVehicles: false,
          hasVehicleLoan: false,
          hasValuables: false,
          hasValuablesLoan: false,
          isSelfEmployed: false,
          busHasBankAccounts: false,
          busHasCash: false,
          busHasAssets: false,
          busAssetsLoan: false,
          totalOwed: false,
          totalBankAccountValue: false,
          totalCashValue: false,
          totalInvestmentValue: false,
          totalInvestmentLoan: false,
          totalRetirementValue: false,
          totalRetirementLoan: false,
          lifeInsuranceValue: false,
          lifeInsuranceLoan: false,
          realEstateValue: false,
          realEstateLoan: false,
          vehicleValue: false,
          vehicleLoan: false,
          valuablesValue: false,
          valuablesLoan: false,
          grossIncome: false,
          grossExpenses: false,
          busBankAccountValue: false,
          busCashValue: false,
          busAssetValue: false,
          busAssetLoan: false,
          busGrossIncome: false,
          busGrossExpense: false,
        },
        offer: {
          amount12: 0,
          amount24: 0
        }
      }
    }
  }
</script>
<style scoped>
  .btn-primary {
    background-color: #2896C5;
  }
  .aside-item h2 {
    font-size: 3em;
  }
  .aside-item p {
    font-size: 1.5em;
  }
  .home-page {
    background-image: url(./../assets/banner.jpg);
    background-size: cover;
  }
</style>
