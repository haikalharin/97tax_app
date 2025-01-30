<template>
  <page :navType="'ein'">
    <div class="top-hero-title pt-4">
      <div class="container">
        <div class="title">NEED AN IRS</div>
        <div class="title">EIN NUMBER?</div>
        <div class="sub-title">
          For a one-time application fee, receive your Employer Identification Number electronically.
        </div>
      </div>
    </div>
    <div class="get-started-form">
      <div class="desc-wrapper">
        <div class="info-row d-flex my-5 pr-4">
          <div>
            <img src="@/assets/help.svg" alt="" width="70" height="70" />
          </div>
          <div class="info-text ml-4">
            <h5 class="font-weight-bold form-desc-title">Instant Results</h5>
            <span class="form-desc-subtitle">
              We’ll retrieve your EIN Number from the IRS electronically.
            </span>
          </div>
        </div>
        <div class="info-row d-flex mb-5 pr-4">
          <div>
            <img src="@/assets/stop.svg" alt="" width="70" height="70" />
          </div>
          <div class="info-text ml-4">
            <h5 class="font-weight-bold form-desc-title">Bank Requirements</h5>
            <span class="form-desc-subtitle">
              Meets bank FEIN requirements to open checking accounts.
            </span>
          </div>
        </div>
        <div class="info-row d-flex pr-4">
          <div>
            <img src="@/assets/piggybank.svg" alt="" width="70" height="70" />
          </div>
          <div class="info-text ml-4">
            <h5 class="font-weight-bold form-desc-title">Affordable</h5>
            <span class="form-desc-subtitle">
              No extra fees or hidden costs.
            </span>
          </div>
        </div>
      </div>
      <div>
        <div class="apply-form">
          <h3 class="font-weight-bold text-uppercase text-center mb-4">
            APPLY <SPAN style="color: #0098c3">INSTANTLY</SPAN> FOR A FEIN
            NUMBER
          </h3>
          <div class="form-input-item">
            <label class="form-control-label light-gray">First Name</label>
            <input
              :class="['form-control', errors.firstName ? 'is-invalid' : '']"
              type="text"
              v-model="formData.firstName"
              placeholder="First Name"
              v-on:keypress="isLetter($event)"
            />
            <span v-if="errors.firstName" class="ein-form-err">First name is required</span>
          </div>
          <div class="form-input-item">
            <label class="form-control-label light-gray">Last Name</label>
            <input
              :class="['form-control', errors.lastName ? 'is-invalid' : '']"
              type="text"
              v-model="formData.lastName"
              placeholder="Last Name"
              v-on:keypress="isLetter($event)"
            />
            <span v-if="errors.lastName" class="ein-form-err">Last name is required</span>
          </div>
          <div class="form-input-item">
            <label class="form-control-label light-gray">Email</label>
            <input
              :class="['form-control', errors.email ? 'is-invalid' : '']"
              type="text"
              v-model="formData.email"
              placeholder="Email"
            />
            <span v-if="errors.email" class="ein-form-err">Please provide valid email</span>
          </div>
          <div class="pt-4 pb-2">
            <div
              class="green-button"
              style="width: 75%; margin: 0 auto"
              @click="onSubmit"
            >
              GET STARTED NOW <font-awesome-icon icon="arrow-right" />
            </div>
          </div>
          <div class="form-bottom-desc">
            <img src="@/assets/ein/padlock.png" />
            <div class="pl-3">
              Your personal information is secured through SSL Encryption
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="pt-5 pb-5">
      <div class="purpose-title">What is the Purpose of a Federal Tax ID Number?</div>
      <div class="purpose-subtitle">
        EIN numbers are unique business identification numbers issued by the IRS.
      </div>
    </div>
    <div class="container">
      <div class="purpose-desc-wrapper">
        <div class="row">
          <div class="col-sm-12 col-md-6">
            <h3>EIN Applications</h3>
            <p>
              EIN numbers are required by newly formed LLC’s, corporations, and partnerships.
              Taxpayers can apply for EIN numbers online in less than 10 minutes.
              EIN numbers applied through 97tax.com will be retrieved within 24 hours
              and we will store your EIN number in case it is lost or misplaced. The IRS will
              mail you a confirmation letter directly within 30 days.
            </p>
            <p>
              Business filing documents or financial statements about your new business
              are not required. The checkout process here at 97tax will ask you the necessary
              questions, most of which you should already know. There is an electronic form to
              sign during checkout that will allow us to apply on your behalf to the IRS to
              get your EIN number. In many cases, we are able to apply for and retrieve your
              EIN number immediately, but will otherwise email you the EIN number within 24 hours.
            </p>
            <p>
              Hiring an attorney is expensive and unnecessary, and doing it yourself can cause
              delay and even more frustration. We walk you through all steps during our checkout
              to apply for a new EIN number today. Within 24 hours, we'll retrieve your EIN number
              directly from the IRS and email it to you to complete the process.
            </p>
          </div>
          <div class="col-sm-12 col-md-6">
            <h3>Hiring Employees</h3>
            <p>
              Hiring employees makes an EIN number required. It may seem like a distant goal for
              a new business, but it is wise to apply for an EIN number early before hiring
              employees becomes necessary.
            </p>
            <p>
              <a href="#top-hero" class="custom-link">Click here</a> to get your application for an EIN
              started now.
            </p>
            <h3>Starting a New Business</h3>
            <p>
              The process of starting a new business includes a lot of steps. Setting up an LLC,
              creating a new bank account, contacting your local city or county for a license, and
              applying for any trade licenses or certifications that are required to do your work.
              It is common practice to apply for an EIN as early as possible during the process of
              starting a new business. This way, all new accounts opened in the name of yourself or
              the business name will use the new EIN.
            </p>
          </div>
        </div>
      </div>

      <div class="hidden-xs-only my-5 d-flex justify-content-center">
        <a
          class="btn btn-green font-weight-bold text-uppercase px-5"
          href="#top-hero"
          style="text-decoration: none">
          CLICK HERE TO GET YOUR APPLICATION STARTED NOW
        </a>
      </div>

    </div>
    <div class="top-hero-title" style="padding: 40px 10px 60px">
      <div class="container text-center" style="color: white">
        <h2>Did you know?</h2>
        <div class="didyouknow-desc">
          There are 36 different questions required by the IRS about entity
          selection, legal structure of you business, and hiring employees.
          We’ll help you through the process and get your EIN number fast.
        </div>
      </div>
    </div>
    <div class="frequently-asked-questions py-5">
      <div class="container">
        <h1 class="text-center font-weight-bold my-3">
          Frequently Asked Questions
        </h1>
        <h4 class="light-gray text-center mb-5">We have lots of answers</h4>
        <div class="my-3" id="embedded_faq">
          <ein-faq></ein-faq>
        </div>
      </div>
    </div>
    <div id="embedded_case-tracker2" class="pt-5">
      <order-tracker></order-tracker>
    </div>

    <div id="home-page-our-clients">
      <our-clients></our-clients>
    </div>
  </page>
</template>

<script>
import { AXIOS } from "../scripts/http-common";
import { SiteUtils } from "../scripts/site-common";
import { SnackUtils } from "../scripts/snack-common";

export default {
  data() {
    return {
      formData: {
        firstName: "",
        lastName: "",
        email: "",
      },
      errors: {
        firstName: false,
        lastName: false,
        email: false,
      },
    };
  },
  methods: {
    isLetter(e) {
      if (e.keyCode === 32) return true;
      let char = String.fromCharCode(e.keyCode);
      if (/^[A-Za-z]+$/.test(char)) return true;
      else e.preventDefault();
    },
    onGetStarted() {
      this.$router.push("/ein-checkout");
    },
    onSubmit() {
      this.errors.firstName = false;
      this.errors.lastName = false;
      this.errors.email = false;

      this.errors.firstName = !this.formData.firstName || !this.formData.firstName.trim();
      this.errors.lastName = !this.formData.lastName || !this.formData.lastName.trim();
      this.errors.email = !!SiteUtils.validateEmail(this.formData.email);
      if (this.errors.firstName || this.errors.lastName || this.errors.email) {
        return;
      }

      AXIOS.post(`/ein/init`, {
        first_name: this.formData.firstName,
        last_name: this.formData.lastName,
        email: this.formData.email,
        card_expire_date: "01/99" // temporary value
      })
        .then((response) => {
          if (!!response.data.id) {
            this.$router.push({ path: "/ein-checkout", query: { order: response.data.id } });
          } else {
            setTimeout(() => {
              SnackUtils.danger("Error occurred, please try again later");
            }, 300);
          }
        })
        .catch((e) => {
          setTimeout(() => {
            SnackUtils.danger("Error occurred, please try again later");
          }, 300);
        });
    },
  },
};
</script>
<style scoped>
* {
  font-family: "Archivo", sans-serif;
}

.top-hero-title {
  padding-bottom: 16px;
  background-color: #0094be;
}
.title {
  font-size: 48px;
  line-height: 50px;
  font-weight: bold;
  color: white;
  text-align: center;
}
.sub-title {
  color: white;
  font-size: 24px;
  text-align: center;
}
.get-started-form {
  display: flex;
  flex-direction: row;
  justify-content: center;
  padding: 40px 0px 50px 0px;
  flex-wrap: wrap;
}
.desc-wrapper {
  width: 340px;
}
.form-desc-item {
  width: 340px;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  padding-bottom: 40px;
}
.form-desc-image {
  width: 80px;
  text-align: center;
}
.form-desc-text {
  padding-left: 10px;
  color: #424242;
  width: 260px;
}

.form-desc-title {
  font-family: "Archivo", sans-serif;
  font-size: 17px;
  font-weight: bold;
}

.form-desc-subtitle {
  font-size: 14px;
}

.apply-form {
  background-color: #f5f5f5;
  padding: 30px;
}

.form-control-label {
  font-size: 12px;
  margin-bottom: 0;
}

.light-gray {
  color: #a5a5a5;
}

.form-input-item {
  width: 100%;
  max-width: 270px;
  margin: 0 auto;
}

.green-button {
  background-color: #34ca00;
  color: white;
  font-size: 16px;
  border-radius: 2px;
  padding: 15px;
  font-weight: bold;
  text-align: center;
  cursor: pointer;
}

.form-bottom-desc {
  color: #424242;
  text-align: center;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.purpose-title {
  font-size: 32px;
  font-weight: 600;
  color: #424242;
  text-align: center;
}

.purpose-subtitle {
  font-size: 14px;
  color: #9f9f9f;
  text-align: center;
}

.purpose-desc-wrapper {
  padding: 10px 5%;
}

.custom-link {
  color: #2997c5;
}

.didyouknow-desc {
  width: 100%;
  max-width: 680px;
  margin: 0 auto;
}
</style>
