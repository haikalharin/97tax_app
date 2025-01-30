<template>
  <page class="home-page">
    <div class="container">
      <oic-progress></oic-progress>

      <div class="row">
        <div class="col">
          <p class="font-weight-bold">
            The IRS will want to know about your assets you own to help figure out what debt can be settled for. Assets include houses, investments, vehicles, bank accounts, and other assets like expensive jewelry. Your best estimates for the value of your assets is OK, since the values usually change over time. Click on the categories below to complete this page. If a section does not apply to you, you don't have to fill it out.
          </p>
        </div>
      </div>

      <div class="row">
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0 "  src="@/assets/Vehicles.png">
          <h3 class="text-uppercase m-0 text-center">Vehicles</h3>
          <p>Fill out information here about any cars, trucks, boats, or motorcycles that you own.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/BankAccounts.png">
          <h3 class="text-uppercase m-0 text-center">Bank Accounts</h3>
          <p>Fill out basic information about your bank accounts, like checking and savings accounts in this section. This includes less common online bank accounts or money market accounts.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/RealEstate.png">
          <h3 class="text-uppercase m-0 text-center">Real Estate</h3>
          <p>If you own a home or other real estate, fill out this section.<br></p>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/Investments.png">
          <h3 class="text-uppercase m-0 text-center">Investments</h3>
          <p>If you own stocks, bonds, retirement accounts, and certificates of deposit, fill out this section.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/Insurance.png">
          <h3 class="text-uppercase m-0 text-center">Whole Life Insurance</h3>
          <p>If you own any insurance that has a cash value, fill out this section.</p>
        </div>
        <div class="col">
          <img class="mx-auto d-block img-fluid m-0"  src="@/assets/OtherAssets.png">
          <h3 class="text-uppercase m-0 text-center">Other Assets</h3>
          <p>This includes uncommon assets like very expensive jewelry, artwork, bitcoin or other virtual currency, stored value cards (like a payroll card from your employer), and safe deposit boxes. </p>
        </div>
      </div>


      <div class="row">
        <div class="p-2 mr-auto mt-4 mb-4 col-4">
          <button class="btn-block btn-primary" v-on:click="handle">Back</button>
        </div>
        <div class="p-2 mt-4 mb-4 flex-row-reverse col-4">
          <button class=" btn-block btn-primary" v-on:click="handleContinue">Continue</button>
        </div>
      </div>


      <h3 class="mt-4">Accounts</h3>

      <div v-for="(account, index) in form.accounts" :key="account._key">
        <input-bank-account
          ref="accounts"
          :binding="form.accounts[index]"
          :selfEmployed="existing.selfEmployed"
          v-on:remove="removeEntity('accounts', account)"
          v-on:input="replaceEntity('accounts', index, $event)"
        ></input-bank-account>
      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-secondary" v-on:click="addEntity('accounts')">
            + Account
          </button>
        </div>
      </div>

      <h3 class="mt-4">Investments</h3>

      <div v-for="(investment, index) in form.investments" :key="investment._key">
        <input-investment-account
          ref="investments"
          :binding="form.investments[index]"
          :selfEmployed="existing.selfEmployed"
          v-on:remove="removeEntity('investments', investment)"
          v-on:input="replaceEntity('investments', index, $event)"
        ></input-investment-account>
      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-secondary" v-on:click="addEntity('investments')">
            + Investment
          </button>
        </div>
      </div>

      <h3 class="mt-4">Real Estate</h3>

      <div v-for="(re, index) in form.realEstates" :key="re._key">
        <input-real-estate
          ref="realEstates"
          :binding="form.realEstates[index]"
          :selfEmployed="existing.selfEmployed"
          v-on:remove="removeEntity('realEstates', re)"
          v-on:input="replaceEntity('realEstates', index, $event)"
        ></input-real-estate>
      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-secondary" v-on:click="addEntity('realEstate')">
            + Real Estate
          </button>
        </div>
      </div>

      <h3 class="mt-4">Vehicles</h3>

      <div v-for="(v, index) in form.vehicles" :key="v._key">
        <input-vehicle
          ref="vehicles"
          :binding="form.vehicles[index]"
          :selfEmployed="existing.selfEmployed"
          v-on:remove="removeEntity('vehicles', v)"
          v-on:input="replaceEntity('vehicles', index, $event)"
        ></input-vehicle>
      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-secondary" v-on:click="addEntity('vehicles')">
            + Vehicle
          </button>
        </div>
      </div>

      <h3 class="mt-4">Life Insurance</h3>

      <div class="row">
        <input-options
          ref="hasLifeInsurance"
          classes="col-12"
          :binding="form.lifeInsurances[0].hasLifeInsurance"
          @input="handleInsuranceInput($event, 'hasLifeInsurance')"
          label="Do you have life insurance with a cash value? (e.g. whole life, universal life, etc.)"
        ></input-options>
      </div>

      <div v-if="form.lifeInsurances[0].hasLifeInsurance.value">
        <div class="row">
          <input-text
            ref="name"
            label="Name of Insurance Company"
            :binding="form.lifeInsurances[0].name"
            @input="handleInsuranceInput($event, 'name')"
          ></input-text>
          <input-text
            ref="policyNumber"
            label="Policy Number"
            :binding="form.lifeInsurances[0].policyNumber"
            @input="handleInsuranceInput($event, 'policyNumber')"
          ></input-text>
          <input-text
            ref="cashValue"
            label="Current Cash Value"
            placeholder="0"
            :binding="form.lifeInsurances[0].cashValue"
            @input="handleInsuranceInput($event, 'cashValue')"
          ></input-text>
          <input-text
            ref="loanBalance"
            label="Loan Balance"
            placeholder="0"
            :binding="form.lifeInsurances[0].loanBalance"
            @input="handleInsuranceInput($event, 'loanBalance')"
          ></input-text>
        </div>
      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-primary" v-on:click="handleContinue">Continue</button>
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

  export default {
    data() {
      return {
        existing: {},
        form: {
          accounts: [
            {_key: Math.random()}
          ],
          investments: [
            {_key: Math.random()}
          ],
          realEstates: [
            {_key: Math.random()}
          ],
          vehicles: [
            {_key: Math.random()}
          ],
          lifeInsurances: [
            {
              _key: Math.random(),
              hasLifeInsurance: {},
              name: {},
              policyNumber: {},
              cashValue: {},
              loanBalance: {}
            }
          ]
        }
      }
    },

    methods: {
      addEntity(name) {
        this.form[name].push({_key: Math.random()});
      },
      removeEntity(name, entity) {
        let index = this.form[name].indexOf(entity);
        this.form[name].splice(index, 1);
      },
      replaceEntity(name, index, event) {
        this.form[name].splice(index, 1, event);
      },

      handleContinue() {
        if (SiteUtils.validateAll(this.$refs)) {
          SiteUtils.saveOicPage(this.existing, this.form, () => {
            this.$router.push("oic-income");
          })
        }
      },

      handleInsuranceInput(event, name) {
        this.form.lifeInsurances[0][name] = event;
      }
    },

    mounted() {
      let self = this;
      AXIOS.get('/oic')
        .then(response => {
          self.existing = response.data;

          if (response.data) {
            SiteUtils.prefillList(self.existing.accounts, self.form.accounts, self.$refs.accounts);
            SiteUtils.prefillList(self.existing.investments, self.form.investments, self.$refs.investments);
            SiteUtils.prefillList(self.existing.realEstates, self.form.realEstates, self.$refs.realEstates);
            SiteUtils.prefillList(self.existing.vehicles, self.form.vehicles, self.$refs.vehicles);


            if (self.existing.lifeInsurances && self.existing.lifeInsurances.length > 0) {
              let insurance = self.existing.lifeInsurances[0];
              self.$set(self.form.lifeInsurances[0], 'hasLifeInsurance', {value: insurance.hasLifeInsurance});
              self.$set(self.form.lifeInsurances[0], 'name', {value: insurance.name});
              self.$set(self.form.lifeInsurances[0], 'policyNumber', {value: insurance.policyNumber});
              self.$set(self.form.lifeInsurances[0], 'cashValue', {value: insurance.cashValue});
              self.$set(self.form.lifeInsurances[0], 'loanBalance', {value: insurance.loanBalance});
            }
          } else {
            this.$router.push('offer-in-compromise');
          }
        })
        .catch(e => {
          alert("Sorry, we are doing maintenance on the website right now. Please check again later.");
          console.log(e);
        });
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
