<template>
  <div>
    <div>
      <div class="plus-icon">
        <img src="../assets/PlusButton.png"/>
      </div>
      <blockquote class="custom-border mb-0">
        <p>
          <slot></slot>
        </p>
      </blockquote>
      <div class="testimonial-arrow">
        <img src="./../assets/testimonial_arrow_icon.png"/>
      </div>
    </div>
    <div class="mt-4 mb-4">
      <span class="font-weight-bold">{{name}}</span>, {{location}}
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      name: String,
      location: String
    }
  }
</script>

<style scoped>

  @import url('https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i');

  * {
    font-family: 'Raleway', sans-serif;
  }

  .custom-border {
    border: 2px solid #aaa;
  }

  blockquote {
    min-height: 100px;
    display: flex;
  }

  blockquote p {
    display: block;
    margin: auto;
    padding: 20px;
  }

  blockquote::after {
    background: url(./../assets/testimonial_arrow_icon.jpg);
  }

  .testimonial-arrow {
    margin-top: -2px;
  }

  .plus-icon {
    position: absolute;
    top: 0px;
    left: -20px;
  }

</style>
