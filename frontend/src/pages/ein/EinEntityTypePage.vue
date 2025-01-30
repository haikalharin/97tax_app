<template>
  <div class="container" style="padding: 0px 0px 80px">
    <div
      v-for="(label, idx) in entityTypeLabels"
      :key="idx"
      :class="[
        'entity-type',
        order.order_type == entityTypes[idx] ? 'active-entity' : '',
      ]"
      @click="setEntityType(entityTypes[idx])"
    >
      <div class="label-container">
        {{ label }}
        <span v-if="helpBubbleTexts[idx]"
              v-b-popover.hover.bottom="helpBubbleTexts[idx]">
              <img src="./../../assets/HelpBubble.png"/>
            </span>
      </div>

      <!--      <div v-if="activeHelpBubble === idx" class="help-text">-->
      <!--        {{ helpBubbleTexts[idx] }}-->
      <!--      </div>-->
    </div>
  </div>
</template>

<script>
import {SiteUtils} from "../../scripts/site-common";
import {SnackUtils} from "../../scripts/snack-common";

export default {
  props: ["order"],
  data() {
    return {
      entityTypes: ["SoleProprietor", "Partnership", "Corporation", "LLC", "Estate", "Trust"],
      entityTypeLabels: [
        "Sole Proprietor",
        "Partnership",
        "Corporation",
        "Limited Liability Company (LLC)",
        "Estate",
        "Trust",
      ],
      helpBubbleTexts: [
        "This is when one person owns and runs the business. There is no entity outside of yourself. You keep all the profits, and are personally responsible for all the operations of the business.",
        "A business owned by two or more people. You share the profits and responsibilities.",
        "A corporation is a business that is treated like its own legal person. Also select this option if you have an LLC taxed as an S corporation. Corporations can own property, make money, and owe debts.",
        "An LLC is always registered as an LLC with your state. If you have made the election to be treated as an S Corporation, you should select the 'Corporation' entity. An LLC combines parts of being a sole proprietor and a corporation. It protects your personal assets like a corporation does but is easier to manage than a corporation.",
        "An estate is what’s created to manage someone’s money or property after they pass away. It’s like a temporary business to handle their assets.",
        "A trust is a legal setup where someone puts money or property aside to be managed for the benefit of another person or group, like a child or a charity.",
      ],
      activeHelpBubble: null,
    };
  },
  methods: {
    setEntityType(type) {
      this.$emit("change", type);

      if (SiteUtils.isEmptyString(this.order.order_type)) {
        SnackUtils.warning("Please choose entity type.");
        return;
      }

      this.$emit("continue");
    },

  },
};
</script>

<style scoped>
.container {
  padding: 0 0 80px;
}

.entity-type {
  font-family: "Archivo", sans-serif;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;
  text-align: center;
  padding: 40px 0;
  background: #ffffff;
  border: 1px solid #d2d2d2;
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  max-width: 335px;
  cursor: pointer;
  margin: 0 auto 20px;
  position: relative;
}

</style>
