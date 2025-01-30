<template>
  <div>
    <div class="container">
      <div class="title">
        Please sign the Form below to authorize your tax preparer to<br/>
        retrieve your EIN from the IRS. The IRS requires this form to be signed.
      </div>
      <div class="button-grp">
        <div class="ein-button ein-back-button" @click="onClickBack"
          v-bind:class="[signStarted ? 'disabled' : '']">
          <img src="@/assets/left-arrow.png" />&nbsp;Back
        </div>

        <div class="ein-button" @click="onClickSignDoc"
          v-bind:class="[signStarted ? 'disabled' : '']">
          Sign Document &nbsp;<font-awesome-icon icon="fa-regular fa-clock" v-if="signStarted"/>
        </div>
      </div>
    </div>
    <div class="pdf-render">
      <div class="pdf-viewer">
        <vue-pdf-embed :source="pdfSrc" :page="1" />
      </div>
    </div>
  </div>
</template>
<script>
import VuePdfEmbed from "vue-pdf-embed/dist/vue2-pdf-embed";

export default {
  props: ["reason"],
  components: { VuePdfEmbed },
  data() {
    return {
      signStarted: false,
      pdfSrc: "/api/ein/fss4/" + this.$route.query.order,
    };
  },
  methods: {
    onClickBack() {
      if (!this.signStarted) {
        this.signStarted = true;
        this.$emit("back");
      }
    },

    onClickSignDoc() {
      if (!this.signStarted) {
        this.signStarted = true;
        this.$emit("signDoc");
      }
    },
  },
};
</script>
<style scoped>
.title {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-size: 18px;
  line-height: 24px;
  color: #000000;
  padding: 0px 0px 20px 0px;
  text-align: center;
}
.button-grp {
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  padding-bottom: 40px;
}
.pdf-render {
  background: #cbcbcb;
  padding: 30px 0px;
}
.pdf-viewer {
  width: 100%;
  max-width: 790px;
  margin: 0 auto;
}
</style>
