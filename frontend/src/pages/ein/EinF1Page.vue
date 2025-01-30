<template>
  <div>
    <div class="container">
      <div
        v-for="(label, idx) in einReasons"
        :key="idx"
        :class="[
          'entity-type',
          order.reason == einReasons[idx] ? 'active-entity' : '',
        ]"
        @click="setEinReason(einReasons[idx])"
      >
        {{ label }}
        <div class="reason-desc">
          {{ einReasonDescs[idx] }}
        </div>
      </div>
    </div>
    <ein-bottom-navigator @back="$emit('back')" :hideContinue="true" />
  </div>
</template>
<script>
import EinBottomNavigator from "./EinBottomNavigator.vue";
import { SiteUtils } from "../../scripts/site-common";
import { SnackUtils } from "../../scripts/snack-common";

export default {
  props: ["order"],
  components: { EinBottomNavigator },
  data() {
    return {
      einReasonDescs: [
        "Select this option if you are beginning a new business.",
        "Select this option if you already have a business and need to hire employees.",
        "Select this option if the reason for applying for the EIN is strictly to satisfy banking requirements or local law.",
        "Select this option if you are changing the type of organization you currently operate, such as changing from a sole proprietor to a partnership, changing from a partnership to a corporation, etc.",
        "Select this option if you are purchasing a business that is already in operation.",
      ],
      einReasons: [
        "Started a new business",
        "Hired employee(s)",
        "Banking Purposes",
        "Changed Type Of Organization",
        "Purchased Active Business",
      ],
    };
  },
  methods: {
    setEinReason(reason) {
      this.$emit('change', reason);

      if (SiteUtils.isEmptyString(this.order.reason)) {
        SnackUtils.warning("Please choose a reason.");
        return;
      }

      this.$emit("continue");
    },
  },
};
</script>
<style scoped>
.entity-type {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;
  padding: 40px 60px;

  background: #ffffff;
  border: 1px solid #d2d2d2;
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  width: 100%;
  max-width: 691px;

  cursor: pointer;
  margin: 0 auto;
  margin-bottom: 20px;
}

.active-entity {
  background: #e8f7fd;
  border: 1px solid #2a96c4;
  border-radius: 12px;
  color: #2a96c4;
}
.reason-desc {
  margin-top: 10px;
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 21px;
}
</style>
