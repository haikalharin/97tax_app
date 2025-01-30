<template>
  <page>
    <div class="top-hero pt-5">
      <div class="container p-0">
        <div class="banner-section">
          <div class="image-part">
            <img src="@/assets/home_page.png" />
          </div>
          <div class="text-part">
            <h2 class="font-weight-bold left-xs-only">
              Smarter Tax Preparation
            </h2>
            <h5 class="font-weight-bold left-xs-only">
              We want each of our clients to complete our process with their tax
              issue resolved.
            </h5>
            <p class="mt-4 left-xs-only">
              97tax offers <b>tax preparation</b> services that can be ordered
              online in less than 10 minutes. We are the fastest and easiest way
              to apply for IRS and state payment plans.
            </p>
          </div>
          <div class="text-part-mobile">
            <h2 class="font-weight-bold left-xs-only">
              Smarter Tax Preparation
            </h2>
            <h5 class="font-weight-bold left-xs-only">
              We want each of our clients to complete our process with their tax
              issue resolved.
            </h5>
            <div class="image-part-mobile">
              <img src="@/assets/home-mobile.png" />
            </div>
            <p class="mt-4 left-xs-only">
              97tax offers <b>tax preparation</b> services that can be ordered
              online in less than 10 minutes. We are the fastest and easiest way
              to apply for IRS and state payment plans.
            </p>
          </div>
        </div>
      </div>
    </div>
    <div class="our-service py-5">
      <div class="container">
        <h1 class="main-black-text text-center font-weight-bolder">
          Our Services
        </h1>

        <div class="row">
          <div class="col col-12 col-sm-12 col-md-6 col-lg-4">
            <div class="bg-sky-blue">
              <div class="">
                <div class="text-center">
                  <img src="@/assets/service.png" width="120" class="mt-3" />
                </div>
                <div class="p-4 payment-plan">
                  <p class="text-capitalize font-weight-bold">
                    State Payment Plans
                  </p>
                  <h5>
                    Set up a payment plan with your State for a remaining
                    balance
                  </h5>
                  <div class="">
                    <b-btn
                      class="
                        btn-green
                        mt-3
                        w-100
                        font-weight-bold
                        text-uppercase
                      "
                      href="/state-payment-plan"
                    >
                      State Payment Plans
                      <span class="right-sign">&gt;&gt;</span>
                    </b-btn>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col col-12 col-sm-12 col-md-6 col-lg-4">
            <div class="bg-purple">
              <div class="">
                <div class="text-center">
                  <img src="@/assets/service.png" width="120" class="mt-3" />
                </div>
                <div class="p-4 payment-plan">
                  <p class="text-capitalize font-weight-bold">
                    IRS Installment agreement
                  </p>
                  <h5>Set up an IRS Payment Plan within 10 minutes.</h5>
                  <div class="">
                    <b-btn
                      class="
                        btn-green
                        mt-3
                        w-100
                        font-weight-bold
                        text-uppercase
                      "
                      href="/payment-plan"
                    >
                      irs payment plan
                      <span class="right-sign">&gt;&gt;</span>
                    </b-btn>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col col-12 col-sm-12 col-md-6 col-lg-4">
            <div class="bg-blue">
              <div class="">
                <div class="text-center">
                  <img src="@/assets/service.png" width="120" class="mt-3" />
                </div>
                <div class="p-4 payment-plan">
                  <p class="text-capitalize font-weight-bold">
                    Penalty waiver letter
                  </p>
                  <h5>Certain IRS penalties may qualify to be waived.</h5>
                  <div class="">
                    <b-btn
                      class="
                        btn-green
                        mt-3
                        w-100
                        font-weight-bold
                        text-uppercase
                      "
                      href="/penalty-waiver"
                    >
                      penalty waiver
                      <span class="right-sign">&gt;&gt;</span>
                    </b-btn>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- <div class="row pt-5 shown-xs-only">
                    <carousel-3d :display="1" :controlsVisible="true" :inverseScaling="1" :perspective="0" :width="195"
                        :height="237" :border="0">
                        <slide v-for="(opSlide, ind) in ourProcessSlides" v-bind:key="opSlide.title" :index="ind">
                            <div class="d-flex flex-column align-items-center">
                                <img v-if="ind === 0" src="@/assets/qualify_new.png" alt="" width="150">
                                <img v-if="ind === 1" src="@/assets/analysis_new.png" alt="" width="150">
                                <img v-if="ind === 2" src="@/assets/prepare.png" alt="" width="150">
                                <img v-if="ind === 3" src="@/assets/done.png" alt="" width="150">
                                <h3 class="muted-text mt-2 text-uppercase">{{ opSlide.heading }}</h3>
                                <h3 class="main-black-text mt-5 text-uppercase">{{ opSlide.title }}</h3>
                                <p class="main-black-text text-center">{{ opSlide.text }}</p>
                            </div>
                        </slide>
                    </carousel-3d>
                </div> -->
      </div>
    </div>
    <div class="our-process py-5">
      <div class="container">
        <h1 class="main-black-text text-center font-weight-bolder">
          Our Process
        </h1>
        <div class="row">
          <div class="col-sm-8 offset-sm-2 col-12">
            <h5 class="muted-text text-center">
              We use a streamlined and efficient process that enables us to
              complete your order faster and with greater accuracy than ever
              before.
            </h5>
          </div>
        </div>
        <div class="row pt-5 hidden-xs-only">
          <div
            class="col-sm-3 col-12"
            v-for="(opSlide, index) in ourProcessSlides"
            v-bind:key="opSlide.title"
          >
            <div class="d-flex align-items-start">
              <div
                class="d-flex flex-column align-items-center flex-grow-1 w-100"
              >
                <img
                  v-if="index === 0"
                  src="@/assets/qualify_new.png"
                  alt=""
                  width="150"
                />
                <img
                  v-if="index === 1"
                  src="@/assets/analysis_new.png"
                  alt=""
                  width="150"
                />
                <img
                  v-if="index === 2"
                  src="@/assets/prepare.png"
                  alt=""
                  width="150"
                />
                <img
                  v-if="index === 3"
                  src="@/assets/done.png"
                  alt=""
                  width="150"
                />
                <!-- <img v-if="index === 0" src="@/assets/our-process.png" alt="" width="150"  > -->
                <h5 class="muted-text mt-2 text-uppercase">
                  {{ opSlide.heading }}
                </h5>
                <h3 class="main-black-text text-uppercase">
                  {{ opSlide.title }}
                </h3>
                <p class="main-black-text text-center w-100">
                  {{ opSlide.text }}
                </p>
              </div>
              <div v-if="index !== 3"></div>
              <div v-if="index !== 3">
                <!-- <img src="@/assets/right-arrow.svg" alt="" width="60"> -->
                <!-- <hr style="background-color: lightgray; height: 3px; border-radius: 20px;" /> -->
                <!-- <div class="border">sdc</div> -->
              </div>

              <div v-if="index === 3" style="width: 60px"></div>
            </div>
          </div>
        </div>
        <div class="row pt-5 shown-xs-only">
          <carousel-3d
            :display="1"
            :controlsVisible="true"
            :inverseScaling="1"
            :perspective="0"
            :width="195"
            :height="237"
            :border="0"
          >
            <slide
              v-for="(opSlide, ind) in ourProcessSlides"
              v-bind:key="opSlide.title"
              :index="ind"
            >
              <div class="d-flex flex-column align-items-center">
                <img
                  v-if="ind === 0"
                  src="@/assets/qualify_new.png"
                  alt=""
                  width="150"
                />
                <img
                  v-if="ind === 1"
                  src="@/assets/analysis_new.png"
                  alt=""
                  width="150"
                />
                <img
                  v-if="ind === 2"
                  src="@/assets/prepare.png"
                  alt=""
                  width="150"
                />
                <img
                  v-if="ind === 3"
                  src="@/assets/done.png"
                  alt=""
                  width="150"
                />
                <h3 class="muted-text text-uppercase">{{ opSlide.heading }}</h3>
                <h3 class="main-black-text text-uppercase">
                  {{ opSlide.title }}
                </h3>
                <p class="main-black-text text-center">{{ opSlide.text }}</p>
              </div>
            </slide>
          </carousel-3d>
        </div>
      </div>
    </div>
    <div id="home-page-case-tracker">
      <order-tracker></order-tracker>
    </div>
    <div id="home-page-our-clients">
      <our-clients></our-clients>
    </div>

    <!--
<div>
<h3 class="m-auto text-center font-weight-bold">Our Clients Agree, We Got This.</h3>
<p style="text-align: center; font-weight: 800;">You&#8217;ll love how fast we work</p>
<div class="m-auto text-center">
<img src="https://www.97tax.com/wp-content/uploads/quote-icon.png">
</div>

<p>“The process on your website was simple to use, and you did exactly what you said you would. I would recommend you to a friend.”</p>
</q></blockquote><div class="author" style="color:#231f20;"><span class="company-name"><strong>Raymond S</strong>, <span>Topeka, KS</span></span></div></div><div class="review avatar-image"><div class="testimonial-thumbnail"><img class="testimonial-image" src="https://www.97tax.com/wp-content/uploads/quote-icon.png" style="-webkit-border-radius: ;-moz-border-radius: ;border-radius: ;" /></div><blockquote style="background-color:#f1f2f2;"><q style="background-color:#f1f2f2;color:#231f20;">
<p>“I owed the IRS $23,000 and they wouldn&#8217;t stop calling. Collections immediately stopped and it&#8217;s like a weight has been lifted. Thanks so much!”</p>
</q></blockquote><div class="author" style="color:#231f20;"><span class="company-name"><strong>Joan M</strong>, <span>Sacramento, CA</span></span></div></div><div class="review avatar-image"><div class="testimonial-thumbnail"><img class="testimonial-image" src="https://www.97tax.com/wp-content/uploads/quote-icon.png" style="-webkit-border-radius: ;-moz-border-radius: ;border-radius: ;" /></div><blockquote style="background-color:#f1f2f2;"><q style="background-color:#f1f2f2;color:#231f20;">
<p>It only took hours for your specialists to complete my case, and I&#8217;ve been worrying about this for months!</p>
</q></blockquote><div class="author" style="color:#231f20;"><span class="company-name"><strong>Marcus S</strong>, <span>Miami, FL</span></span></div></div><div class="review avatar-image"><div class="testimonial-thumbnail"><img class="testimonial-image" src="https://www.97tax.com/wp-content/uploads/quote-icon.png" style="-webkit-border-radius: ;-moz-border-radius: ;border-radius: ;" /></div><blockquote style="background-color:#f1f2f2;"><q style="background-color:#f1f2f2;color:#231f20;">
<p>I got a lot of quotes, and the best estimate was over $600 from a local attorney. Your service is a no brainer!</p>
</q></blockquote><div class="author" style="color:#231f20;"><span class="company-name"><strong>James R</strong>, <span>Cincinnati, OH</span></span></div>
</div>
-->
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
import { Carousel3d, Slide } from "vue-carousel-3d";
import { SiteUtils } from "../scripts/site-common";
import { AXIOS } from "../scripts/http-common";
const VueScrollTo = require("vue-scrollto");

export default {
  name: "Home",
  data() {
    return {
      ourProcessSlides: [
        {
          heading: "Step 1",
          title: "Qualify",
          text: "Always free to check the options available",
        },
        {
          heading: "Step 2",
          title: "Analysis",
          text: "We analyze your scenario to determine the correct tax forms",
        },
        {
          heading: "Step 3",
          title: "Prepare",
          text: "We prepare and mail your tax documents to you within 1 business day with USPS tracking.",
        },
        {
          heading: "Step 4",
          title: "Done!",
          text: "Sign and mail your prepared tax forms with the included prepaid envelope. We'll email you when the IRS has received it!",
        },
      ],
    };
  },
  components: {
    Carousel3d,
    Slide,
  },
  methods: {
    isMobile: function () {
      return SiteUtils.isMobile();
    },
  },
  mounted() {
    if (window.location.href.includes("case-tracker")) {
      VueScrollTo.scrollTo("#home-page-case-tracker", 1, {});
    }
  },
};
</script>

<style scoped>
.main-black-text {
  color: #424242;
}

.muted-text {
  color: #a0a0a0;
}

.top-hero {
  background-color: #0094be;
}

.btn-green {
  position: relative;
  font-size: 14px;
  padding: 11px;
  text-align: left;
}

.right {
  color: #fff;
  /* margin-left: 4vw; */
  margin-top: -5% !important;
}

.right-sign {
  position: absolute;
  right: 15px;
}

.info-block-col {
  margin: 10px 0;
}

.info-block {
  cursor: pointer;
  padding: 5px;
  height: 100%;
  transition: all 0.2s ease-out;
  border: 1px solid rgba(0, 0, 0, 0);
  box-shadow: 0 0 20px gray;
  background-color: #2896c5;
  color: white;
}

.info-block h2 {
  font-weight: 900;
}

.info-block:hover {
  color: black;
  transition: all 0.2s ease-out;
  background-color: white;
  border: 1px solid gray;
  box-shadow: 0 0 20px gray;
}

.info-block-button {
  color: white;
  border: 0px solid white;
  padding: 5px;
  transition: all 0.2s ease-out;
  margin-right: 20px;
}

.info-block:hover .info-block-button {
  color: black;
  background: none;
  border: 1px solid black;
  transition: all 0.2s ease-out;
}

.home-page {
  background-image: url(./../assets/banner.jpg);
  background-size: cover;
}

h1 {
  color: black;
  margin: 30px 0;
  font-family: sans-serif;
}

h2.blueheader {
  color: #2896c5;
  font-family: sans-serif;
}

p {
  font-family: sans-serif;
}

.overflow-hidden {
  overflow: hidden;
}

.arrow-mb-6 {
  margin-top: 6%;
}

.selection-button {
  margin-right: -3rem;
}

.payment-plan p {
  color: white;
  font-size: 18px;
}

.payment-plan h5 {
  color: white;
  font-size: 15px;
}

.bg-sky-blue {
  background-color: #3582c7;
}

.bg-purple {
  background-color: #6047a2;
}
.bg-blue {
  background-color: #009eb0;
}

.image-part {
  text-align: center;
}
.image-part > img {
  max-width: 450px;
  margin: 0 auto;
}

.text-part {
  color: white;
  padding: 0px 0px 0px 100px;
}

.text-part-mobile {
  color: white;
  padding: 0px 25px;
  display: none;
}

.image-part-mobile {
  text-align: center;
  padding: 0px 15px;
}

@media screen and (max-width: 575px) {
  .image-part {
    display: none;
  }
  .text-part {
    display: none;
  }

  .text-part-mobile {
    display: block;
  }
  .image-part-mobile > img {
    width: 100%;
    max-width: 250px;
    margin: 0 auto;
  }

  .top-hero {
    padding: 2rem 0 !important;
  }

  .our-process {
    padding: 30px 0 20px !important;
  }

  .our-service {
    padding: 30px 0 20px !important;
  }

  .our-process .carousel-3d-slide {
    background: transparent;
  }

  .our-process .carousel-3d-slide img {
    width: 60px;
  }

  .selection-button {
    margin-right: 0rem;
  }
}

@media screen and (max-width: 992px) {
  .text-part {
    color: white;
    padding: 0px 0px 0px 40px;
  }
}

@media screen and (max-width: 1200px) {
  .arrow-mb-6 {
    margin-top: 0;
  }

  .right {
    margin-top: 0 !important;
  }

  .circle {
    width: 80px !important;
    height: 80px !important;
    font-size: 12px !important;
  }

  .md-mb-20 {
    margin-bottom: 20px;
  }

  .btn-green {
    padding: 11px !important;
  }
}

.banner-section {
  display: flex;
  flex-direction: column;
  width: 100%;
  margin: 0 auto;
}

@media screen and (min-width: 768px) {
  .banner-section {
    display: flex;
    flex-direction: row;
  }
}
</style>
