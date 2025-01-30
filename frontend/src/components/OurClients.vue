<template>
  <div class="our-clients pt-5 pb-4">
    <div class="container">
      <div class="faq-title">What Our Clients Say</div>
      <div class="faq-desc mb-4">Read what our past customers say about us</div>

      <div class="carousel-container">
        <carousel
          :loop="true"
          :nav="false"
          :margin="10"
          :dotsEach="true"
          :responsive="{
            0: { items: 1 },
            768: { items: 2 },
            1024: { items: 3 },
          }"
        >
          <template slot="next">
            <div class="carousel-nav nav-left">
              <font-awesome-icon icon="chevron-right" />
            </div>
          </template>

          <template slot="prev">
            <div id="nav-right" class="carousel-nav nav-right">
              <font-awesome-icon icon="chevron-left" />
            </div>
          </template>

          <div class="slide-item">
            <div class="slide-content">
              97tax works fast! They got my prepared docs in the mail in one
              business day…thanks!
            </div>
            <div class="slide-title">
              <b>Maurice R, </b>
              <span>Columbus, OH</span>
              <img src="@/assets/5-star-rating.svg" style="width: 80px" />
            </div>
          </div>
          <div class="slide-item">
            <div class="slide-content">
              Faster and way less expensive than an attorney. Couldn’t be more
              satisfied!
            </div>
            <div class="slide-title">
              <b>Susan N, </b>
              <span>Sacramento, CA</span>
              <img src="@/assets/5-star-rating.svg" style="width: 80px" />
            </div>
          </div>
          <div class="slide-item">
            <div class="slide-content">
              Couldn’t afford an attorney, but couldn’t afford to do nothing.
              You guys rock!
            </div>
            <div class="slide-title">
              <b>James H, </b>
              <span>Detroit, Mi</span>
              <img src="@/assets/5-star-rating.svg" style="width: 80px" />
            </div>
          </div>
          <div class="slide-item">
            <div class="slide-content">
              Got quotes from all over. 97tax was the best option for me by far.
            </div>
            <div class="slide-title">
              <b>Felicia J, </b>
              <span>Greensboro, NC</span>
              <img src="@/assets/5-star-rating.svg" style="width: 80px" />
            </div>
          </div>
        </carousel>
      </div>
    </div>
  </div>
</template>
<script>
import carousel from "vue-owl-carousel";

export default {
  name: "OurClients",
  data() {
    return {};
  },
  components: {
    carousel,
  },
  methods: {
    init() {
      console.log("init");
    },
    changed() {
      console.log("change");
    },
  },
  mounted() {
    setTimeout(() => {
      let navRight = document.getElementById("nav-right");
      navRight.parentElement.style.display = "";
    }, 1000);
  },
};
</script>

<style scoped>
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

.slide-content {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 24px;
  color: #252020;
}

.slide-title {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 24px;

  color: #252020;
}

.slide-title > span {
  color: gray;
}

.slide-item {
  background: #f8faf9;
  border-radius: 16px;
  padding: 26px 32px;
}

.slide-item {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 180px;
}

.carousel-container {
  position: relative;
}

.carousel-nav {
  width: 50px;
  height: 50px;
  background: #ffffff;
  box-shadow: 0px 0px 40px rgba(0, 0, 0, 0.12);
  border-radius: 50%;
  color: #2a96c4;
  text-align: center;
  font-size: 18px;
  padding-top: 12px;
  top: 70px;
  cursor: pointer;
}

.nav-left {
  z-index: 999;
  position: absolute;
  right: 0px;
  transform: translateX(50%);
}

.nav-right {
  z-index: 999;
  position: absolute;
  left: 0px;
  transform: translateX(-50%);
}

@media only screen and (max-width: 578px) {
  .nav-left {
    left: 0px;
    transform: translateX(-25%);
  }

  .nav-right {
    right: 0px;
    transform: translateX(25%);
  }
}
</style>
