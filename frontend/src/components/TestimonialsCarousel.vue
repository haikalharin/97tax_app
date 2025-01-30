<template>
  <div class="mt-2">
    <div class="row">
      <img src="@/assets/OurClients.png" class="mx-auto mb-4 img-fluid" />
    </div>

    <div class="text-center">
      <img class="mx-auto" src="./../assets/quote-icon.png" />
    </div>
    <b-carousel
      id="our-clients-carousel"
      class="carousel-fade"
      indicators
      :interval="4000"
      img-height="500"
      img-width="1024"
    >
      <slot></slot>
    </b-carousel>
  </div>
</template>

<script>
export default {
  data() {
    return {
      slide: 0,
      sliding: null
    };
  }
};
</script>

<style>
#our-clients-carousel div,
#our-clients-carousel img {
  outline: none;
  color: black;
}
.carousel-indicators li {
  border-radius: 50%;
  width: 10px;
  height: 10px;
  background-color: black;
  outline: none;
}
.carousel-indicators .active {
  background-color: #2896c5;
  outline: none;
}

.carousel-responsive {
  height: 410px;
}

@media (min-width: 576px) {
  .carousel-responsive {
    height: 340px;
  }
}

@media (min-width: 768px) {
  .carousel-responsive {
    height: 290px;
  }
}
</style>
