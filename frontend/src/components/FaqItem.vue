<template>
  <div class="mb-2">
    <div
      class="faq-graybox d-flex flex-row justify-content-between"
      @click="toggleContent()"
    >
      <h5 class="faq-title font-weight-bold align-self-center m-0">
        {{ title }}
      </h5>
      <div class="font-weight-bold" style="width: 24px; height: 30px">
        <font-awesome-icon icon="chevron-down" v-if="!isExpanded" />
        <font-awesome-icon icon="chevron-up" v-if="isExpanded" />
      </div>
    </div>
    <div class="d-flex flex-column align-items-start">
      <div class="faq-description" :style="contentStyles">
        <div ref="contentContainer" class="faq-bluebox">
          <slot></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ["title"],
  data() {
    return {
      isExpanded: false,
      contentClasses: [],
      contentStyles: { height: 0 },
    };
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
      this.contentStyles.height =
        this.$refs.contentContainer.scrollHeight + "px";
      this.isExpanded = true;
    },
    collapseContent() {
      this.contentStyles.height = 0;
      this.isExpanded = false;
    },
    handleResize() {
      if (this.isExpanded) {
        this.contentStyles.height =
          this.$refs.contentContainer.scrollHeight + "px";
      }
    },
  },
  mounted() {
    window.addEventListener("resize", this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.handleResize);
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i");
.faq-bluebox {
  padding: 15px 22px 5px 0px;
}
.faq-graybox {
  cursor: pointer;
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 24px;

  color: #000000;
  background: #fafbfb;
  border-radius: 8px;
  padding: 8px 16px;
}
.faq-description {
  transition: height 0.35s ease;
  overflow: hidden;
  width: 100%;
  padding-left: 32.5px;

  background: #fafbfb;
  border-radius: 8px;
  margin-top: 2px;

  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 28px;
  color: #252020;
}
.faq-description .faq-bluebox {
  width: 100%;
}
.faq-graybox .faq-title {
  font-family: "PT Sans", serif;
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 24px;

  color: #000000;
}
.faq-graybox:hover .faq-title {
  transition: color 0.2s;
}
a:link,
a:visited,
a:active,
a:hover {
  text-decoration: underline;
}

@media (max-width: 575px) {
  .faq-graybox .faq-title {
    font-size: 20px;
  }
}

.faq-bluebox li {
  color: #464646;
  font-weight: 400;
  text-decoration: none;
}
</style>
