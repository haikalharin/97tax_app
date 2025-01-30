<template>
  <page class="home-page">
    <div class="container">
      <div class="row">
        <div class="col">
          <h1 class="text-center">Additional Info</h1>
          <p>To complete your application, we'll need some additional information that is required by the IRS.</p>
        </div>
      </div>
      <div class="row">
        <div class="col" >
          <h2>Personal Details</h2>
        </div>
      </div>
      <div class="row">
        <input-text
          ref="firstName"
          label="First Name"
          :binding="form.firstName"
          :required="true"
        ></input-text>
        <input-text
          ref="lastName"
          label="Last Name"
          :binding="form.lastName"
          :required="true"
        ></input-text>
        <input-text
          ref="phoneNumber"
          label="Phone Number"
          :binding="form.phoneNumber"
          :regex="/.*[0-9]{3}.*[0-9]{3}.*[0-9]{4}.*/"
        ></input-text>
        <input-text
          ref="email"
          label="Email Address"
          :binding="form.email"
        ></input-text>
        <input-text
          ref="dob"
          label="Date of Birth"
          placeholder="mm/dd/yyyy"
          :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
          :binding="form.dob"
          :required="true"
        ></input-text>
        <input-text
          ref="ssn"
          label="Social Security Number"
          placeholder="000-00-0000"
          :regex="/^.*[0-9]{3}.*[0-9]{2}.*[0-9]{4}.*$/"
          :binding="form.ssn"
          :required="true"
        ></input-text>
      </div>

      <div v-if="existing.married">
        <div class="row">
          <div class="col" >
            <h2>Spouse Details</h2>
          </div>
        </div>
        <div class="row">
          <input-text
            ref="spouseFirstName"
            label="Spouse First Name"
            :binding="form.spouseFirstName"
            :required="true"
          ></input-text>
          <input-text
            ref="spouseLastName"
            label="Spouse Last Name"
            :binding="form.spouseLastName"
            :required="true"
          ></input-text>
          <input-text
            ref="spouseDob"
            label="Spouse Date of Birth"
            placeholder="mm/dd/yyyy"
            :regex="/^[0-9]{1,2}[\/\- ][0-9]{1,2}[\/\- ][0-9]{4}$/"
            :binding="form.spouseDob"
            :required="true"
          ></input-text>
          <input-text
            ref="spouseSsn"
            label="Spouse Social Security Number"
            placeholder="000-00-0000"
            :regex="/^.*[0-9]{3}.*[0-9]{2}.*[0-9]{4}.*$/"
            :binding="form.spouseSsn"
            :required="true"
          ></input-text>
        </div>
      </div>
      <div class="row">
        <div class="col" >
          <h2>Home Physical Address</h2>
        </div>
      </div>
      <div class="row">
        <input-text
          ref="addressLine1"
          label="Address Line 1"
          :binding="form.addressLine1"
          :required="true"
        ></input-text>
        <input-text
          ref="addressLine2"
          label="Address Line 2"
          :binding="form.addressLine2"
        ></input-text>
        <input-text
          ref="city"
          label="City"
          :binding="form.city"
          :required="true"
        ></input-text>
        <input-text
          ref="state"
          label="State"
          :binding="form.state"
          :required="true"
        ></input-text>
        <input-text
          ref="zip"
          label="Zip"
          :binding="form.zip"
          :required="true"
        ></input-text>
        <input-text
          ref="county"
          label="County"
          :binding="form.county"
        ></input-text>
      </div>
      <div class="row">
        <input-options
          ref="ownRent"
          label="Do you:"
          :binding="form.ownRent"
          :required="true"
          :options="[
            {label: 'Own', value: 'Own'},
            {label: 'Rent', value: 'Rent'},
            {label: 'Other', value: 'Other'}
          ]"
        ></input-options>
        <input-text
          v-if="form.ownRent.value === 'Other'"
          ref="ownRentDesc"
          label="Please Specify"
          placeholder="share rent, live with relative, etc."
          :binding="form.ownRentDesc"
          :multiline="true"
          :required="true"
        ></input-text>
      </div>
      <div class="row">
        <div class="col">
          <label>
            <input type="checkbox" v-model="separateMailingAddress">
            Separate Mailing Address
          </label>
        </div>
      </div>
      <div class="row" v-if="separateMailingAddress">
        <div class="col" >
          <h2>Mailing Address</h2>
        </div>
      </div>
      <div class="row" v-if="separateMailingAddress">
        <input-text
          ref="mailLine1"
          label="Address Line 1"
          :binding="form.mailLine1"
          :required="true"
        ></input-text>
        <input-text
          ref="mailLine2"
          label="Address Line 2"
          :binding="form.mailLine2"
        ></input-text>
        <input-text
          ref="mailCity"
          label="City"
          :binding="form.mailCity"
          :required="true"
        ></input-text>
        <input-text
          ref="mailState"
          label="State"
          :binding="form.mailState"
          :required="true"
        ></input-text>
        <input-text
          ref="mailZip"
          label="Zip"
          :binding="form.mailZip"
          :required="true"
        ></input-text>
      </div>

      <h3 class="mt-4">Employment Details</h3>

      <div class="row">
        <input-text
          ref="employerName"
          label="Employer Name"
          :binding="form.employerName"
        ></input-text>
        <input-text
          ref="occupation"
          label="Your Occupation"
          :binding="form.occupation"
        ></input-text>
      </div>

      <div class="row">
        <input-options
          :binding="form.employerOwnership"
          label="Do you have an ownership interest in this business?"
          v-on:input="handleInput($event, 'employerOwnership')">
        </input-options>
        <input-options
          v-if="form.employerOwnership.value"
          ref="employerBusInterest"
          label="Check the business interest that applies:"
          :binding="form.employerBusInterest"
          :required="true"
          :options="[
            {label: 'Partner', value: 'Partner'},
            {label: 'Officer', value: 'Officer'},
            {label: 'Sole Proprietor', value: 'Sole Proprietor'}
          ]"
        ></input-options>
      </div>

      <div class="row">
        <div class="col">
          How long with this employer?
        </div>
      </div>

      <div class="row">
        <input-text
          ref="employDurationYears"
          classes="col-3"
          label="Years"
          type="integer"
          :binding="form.employDurationYears"
        ></input-text>
        <input-text
          ref="employDurationMonths"
          classes="col-3"
          label="Months"
          type="integer"
          :binding="form.employDurationMonths"
        ></input-text>
      </div>


      <h4 class="mt-4">Employer Address</h4>

      <div class="row mt-4">
        <input-text
          ref="employAddressLine1"
          label="Address Line 1"
          :binding="form.employAddressLine1"
        ></input-text>
        <input-text
          ref="employAddressLine2"
          label="Address Line 2"
          :binding="form.employAddressLine2"
        ></input-text>
        <input-text
          ref="employCity"
          label="City"
          :binding="form.employCity"
        ></input-text>
        <input-text
          ref="employState"
          label="State"
          :binding="form.employState"
        ></input-text>
        <input-text
          ref="employZip"
          label="Zip"
          :binding="form.employZip"
        ></input-text>
      </div>


      <div v-if="existing.married">
        <h3 class="mt-4">Spouse Employment Details</h3>

        <div class="row">
          <input-text
            ref="spouseEmployerName"
            label="Employer Name"
            :binding="form.spouseEmployerName"
          ></input-text>
          <input-text
            ref="spouseOccupation"
            label="Spouse's Occupation"
            :binding="form.spouseOccupation"
          ></input-text>
        </div>

        <div class="row">
          <input-options
            :binding="form.spouseEmployerOwnership"
            label="Do your spouse have an ownership interest in this business?"
            v-on:input="handleInput($event, 'spouseEmployerOwnership')">
          </input-options>
          <input-options
            v-if="form.spouseEmployerOwnership.value"
            ref="spouseEmployerBusInterest"
            label="Check the business interest that applies:"
            :binding="form.spouseEmployerBusInterest"
            :required="true"
            :options="[
              {label: 'Partner', value: 'Partner'},
              {label: 'Officer', value: 'Officer'},
              {label: 'Sole Proprietor', value: 'Sole Proprietor'}
            ]"
          ></input-options>
        </div>

        <div class="row">
          <div class="col">
            How long with this employer?
          </div>
        </div>

        <div class="row">
          <input-text
            ref="spouseEmployDurationYears"
            classes="col-3"
            label="Years"
            type="integer"
            :binding="form.spouseEmployDurationYears"
          ></input-text>
          <input-text
            ref="spouseEmployDurationMonths"
            classes="col-3"
            label="Months"
            type="integer"
            :binding="form.spouseEmployDurationMonths"
          ></input-text>
        </div>


        <h4 class="mt-4">Spouse's Employer Address</h4>

        <div class="row mt-4">
          <input-text
            ref="spouseEmployAddressLine1"
            label="Address Line 1"
            :binding="form.spouseEmployAddressLine1"
          ></input-text>
          <input-text
            ref="spouseEmployAddressLine2"
            label="Address Line 2"
            :binding="form.spouseEmployAddressLine2"
          ></input-text>
          <input-text
            ref="spoouseEmployCity"
            label="City"
            :binding="form.spoouseEmployCity"
          ></input-text>
          <input-text
            ref="spouseEmployState"
            label="State"
            :binding="form.spouseEmployState"
          ></input-text>
          <input-text
            ref="spouseEmployZip"
            label="Zip"
            :binding="form.spouseEmployZip"
          ></input-text>
        </div>
      </div>
      <h3 class="mt-4">Dependents</h3>

      <div v-for="(v, index) in form.dependents" :key="v._key">
        <input-dependent
          ref="dependents"
          :binding="form.dependents[index]"
          v-on:remove="removeEntity('dependents', v)"
          v-on:input="replaceEntity('dependents', index, $event)"
        ></input-dependent>
      </div>

      <div class="row">
        <div class="col">
          <button class="btn btn-secondary" v-on:click="addEntity('dependents')">
            + Dependent
          </button>
        </div>
      </div>

      <div class="row mt-4">
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
  import InputOptions from "../components/input/Options";
  import {SiteUtils} from "../scripts/site-common";
  import InputCheckbox from "../components/input/Checkbox";
  import InputText from "../components/input/Text";
  export default {
    components: {InputText, InputCheckbox, InputOptions},
    data() {
      return {
        existing: {},
        separateMailingAddress: false,
        form: {
          firstName: {},
          lastName: {},
          phoneNumber: {},
          email: {},
          ssn: {},
          dob: {},
          addressLine1: {},
          addressLine2: {},
          city: {},
          state: {},
          zip: {},
          mailLine1: {},
          mailLine2: {},
          mailCity: {},
          mailState: {},
          mailZip: {},
          county: {},
          ownRent: { value: '' },
          ownRentDesc:{},
          spouseFirstName: {},
          spouseLastName: {},
          spouseDob: {},
          spouseSsn: {},
          employerName: {},
          employerOwnership: {},
          employerBusInterest: {},
          occupation: {},
          employDurationYears: {},
          employDurationMonths: {},
          employAddressLine1: {},
          employAddressLine2: {},
          employCity: {},
          employState: {},
          employZip: {},
          spouseEmployerName: {},
          spouseEmployerOwnership: {},
          spouseEmployerBusInterest: {},
          spouseOccupation: {},
          spouseEmployDurationYears: {},
          spouseEmployDurationMonths: {},
          spouseEmployAddressLine1: {},
          spouseEmployAddressLine2: {},
          spoouseEmployCity: {},
          spouseEmployState: {},
          spouseEmployZip: {},
          dependents: [
            {_key: Math.random()}
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
            this.$router.push('oic-order-form');
          })
        }
      }
    },
    mounted() {
      let self = this;
      AXIOS.get('/oic')
        .then(response => {
          self.existing = response.data;

          if (response.data) {
            SiteUtils.prefillForm(this, this.existing, this.form);
            SiteUtils.prefillList(self.existing.dependents, self.form.dependents, self.$refs.dependents);

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
