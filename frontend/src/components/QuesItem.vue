<template>
  <div class="mb-2">
    <div class="ques-graybox d-flex flex-row" @click="toggleContent()">
      <h5 class="ques-title font-weight-bold align-self-center m-0">{{title}}</h5>
      <div class="ques-bluebox font-weight-bold" style="width: 32.5px; height: 70px;">
        <font-awesome-icon icon="chevron-right" v-if="!isExpanded"/>
        <font-awesome-icon icon="chevron-down" v-if="isExpanded"/>
      </div>
    </div>
    <div class="d-flex flex-column align-items-start">
      <div class="ques-description" :style="contentStyles" >
        <div ref="contentContainer" class="ques-bluebox">
          <slot></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: ['title'],
    data() {
      return {
        isExpanded: false,
        contentClasses: [],
        contentStyles: {height: 0}
      }
    },
    methods: {
      toggleContent() {
        if (!this.isExpanded) {
          this.expandContent();
        } else {
          this.collapseContent();
        }
      },
      expandContent() {
        this.contentStyles.height = this.$refs.contentContainer.scrollHeight + 'px';
        this.isExpanded = true;
      },
      collapseContent() {
        this.contentStyles.height = 0;
        this.isExpanded = false;
      },
      handleResize() {
        if (this.isExpanded) {
          this.contentStyles.height = this.$refs.contentContainer.scrollHeight + 'px';
        }
      }
    },
    mounted() {
      window.addEventListener('resize', this.handleResize);
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.handleResize);
    }
  }
</script>

<style scoped>

  @import url('https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i');
  .ques-bluebox {
    font-size: 1.25em;
    color: #231f20;
    padding: 20px 20px 20px 0;
  }
  .ques-graybox {
    cursor: pointer;
    border-bottom: 1px solid #231f20;
    justify-content: space-between;
  }
  .ques-description {
    transition: height .35s ease;
    overflow: hidden;
    width: 100%;
  }
  .ques-description .ques-bluebox {
    width: 100%;
  }
  .ques-graybox .ques-title {
    transition: color .35s;
    color: #231f20;
    font-weight: 1000;
    font-size: 24px;
    font-weight: 400;
    font-family: 'Raleway', sans-serif;
  }
  .ques-graybox:hover .ques-title {
    transition: color .2s;
  }
  a:link, a:visited, a:active, a:hover {
    text-decoration: underline;
  }

  @media (max-width: 575px) {
    .ques-graybox .ques-title {
      font-size: 20px;
    }
  }
</style>
