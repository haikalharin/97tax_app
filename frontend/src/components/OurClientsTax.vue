<template>
  <div class="our-clients-tax pt-5 pb-4">
    <div class="container">
      <h1 class="main-black-text text-center font-weight-bolder">Our Clients</h1>
      <carousel-3d :display="carouselConfig.display" :controlsVisible="true" :inverseScaling="1" :perspective="0" :width="carouselConfig.width" :height="carouselConfig.height" :border="2">
        <slide :index="0">
          <div class="slide-title">
            <p class="main-black-text">
              <b>Tiffany A, </b>
              <span>Scottsdale, AZ</span>
            </p>
            <img src="@/assets/5-star-rating.svg">
          </div>
          <div class="slide-content">
            <p class="main-black-text">The fastest way to apply for tax lien removal- I placed in order in 5 minutes, and my application was in the mail for me to sign the next day. Highly recommended.</p>
          </div>
        </slide>
        <slide :index="1">
          <div class="slide-title">
            <p class="main-black-text">
              <b>Corey W, </b>
              <span>Seattle, WA</span>
            </p>
            <img src="@/assets/5-star-rating.svg">
          </div>
          <div class="slide-content">
            <p class="main-black-text">It just works- fairly priced, super fast.</p>
          </div>
        </slide>
        <slide :index="2">
          <div class="slide-title">
            <p class="main-black-text">
              <b>Delores T, </b>
              <span>Baltimore, MD</span>
            </p>
            <img src="@/assets/5-star-rating.svg">
          </div>
          <div class="slide-content">
            <p class="main-black-text">Said one business day, but it was done in 6 hours. Better than expected.</p>
          </div>
        </slide>
        <slide :index="3">
          <div class="slide-title">
            <p class="main-black-text">
              <b>Antonio S, </b>
              <span>Chesapeake, VA</span>
            </p>
            <img src="@/assets/5-star-rating.svg">
          </div>
          <div class="slide-content">
            <p class="main-black-text">97tax helped me remove a tax lien- I had no idea there were other options besides paying the IRS the full amount. Awesome!</p>
          </div>
        </slide>
      </carousel-3d>
    </div>
  </div>
</template>

<script>
  import { Carousel3d, Slide } from 'vue-carousel-3d';
  export default {
    name: 'OurClients',
    data() {
      return {
        carouselConfig: {
          display: screen.width < 1270 ? 1 : 3,
          width: screen.width < 1270 ? Math.max(screen.width - 200, 280) : 525,
          height: screen.width < 1270 ? 390 : 270
        }
      };
    },
    components: {
      Carousel3d,
      Slide
    }
  }
</script>

<style scoped>
  .container {
    max-width: 1300px !important;
  }
  p {
    font-size: 18px;
  }
  .main-black-text {
    color: #424242;
  }
  .carousel-3d-container {
    height: 420px !important;
  }
  .carousel-3d-slide {
    padding: 30px;
    background-color: #fff;
    border-color: #cdcdcd;
    box-shadow: 5px 5px 15px 0 #ccc;
  }
  .carousel-3d-slide.current {
    opacity: 1 !important;
  }
  .carousel-3d-slide.left-1 {
    opacity: 0.5 !important;
    transform: translateX(-280px) translateY(20px) rotateY(0deg) !important;
  }
  .carousel-3d-slide.right-1 {
    transform: translateX(280px) translateY(20px) rotateY(0deg) !important;
    opacity: 0.5 !important;
  }
  .carousel-3d-slide img {
    width: 100px;
  }
  .slide-title {
    margin-bottom: 40px;
  }

  @media (max-width: 575px) {
    .our-clients-tax {
      padding: 30px 0 10px !important;
    }
  }
</style>
