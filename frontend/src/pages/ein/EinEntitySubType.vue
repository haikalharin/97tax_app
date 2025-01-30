<template>
  <div>
    <div class="container" v-if="order.order_type == 'Partnership'">
      <div
        v-for="(label, idx) in partnershipSubTypes"
        :key="idx"
        :class="[
          'entity-type',
          order.sub_type == partnershipSubTypes[idx] ? 'active-entity' : '',
        ]"
        @click="setSubType(partnershipSubTypes[idx])"
      >
        {{ label }}
        <div class="reason-desc">
          {{ partnershipSubDescs[idx] }}
        </div>
      </div>
    </div>
    <div class="container" v-if="order.order_type == 'Corporation'">
      <div
        v-for="(label, idx) in corporationSubTypes"
        :key="idx"
        :class="[
          'entity-type',
          order.sub_type == corporationSubTypeValues[idx]
            ? 'active-entity'
            : '',
        ]"
        @click="setSubType(corporationSubTypeValues[idx])"
      >
        {{ label }}
        <div class="reason-desc">
          {{ corporationSubDescs[idx] }}
        </div>
      </div>
    </div>
    <div class="container" v-if="order.order_type == 'Trust'">
      <div class="row">
        <div class="col-md-12 col-lg-6">
          <div
            v-for="(label, idx) in trustSubTypes1"
            :key="idx"
            :class="[
              'entity-type entity-type-small',
              order.sub_type == trustSubTypeValues[idx]
                ? 'active-entity'
                : '',
            ]"
            @click="setSubType(trustSubTypeValues[idx])"
          >
            {{ label }}
            <span v-if="trustSubTypeDesc[idx]"
              v-b-popover.hover.bottom="trustSubTypeDesc[idx]">
              <img src="./../../assets/HelpBubble.png" />
            </span>
          </div>
        </div>

        <div class="col-md-12 col-lg-6">
          <div
            v-for="(label, idx) in trustSubTypes2"
            :key="idx+trustsHalf"
            :class="[
              'entity-type entity-type-small',
              order.sub_type == trustSubTypeValues[idx+trustsHalf]
                ? 'active-entity'
                : '',
            ]"
            @click="setSubType(trustSubTypeValues[idx+trustsHalf])"
          >
            {{ label }}
            <span v-if="trustSubTypeDesc[idx+trustsHalf]"
              v-b-popover.hover.bottom="trustSubTypeDesc[idx+trustsHalf]">
              <img src="./../../assets/HelpBubble.png" />
            </span>
          </div>
        </div>
      </div>
    </div>
    <ein-bottom-navigator @back="$emit('back')" :hideContinue="true" />
  </div>
</template>
<script>
import { SnackUtils } from "../../scripts/snack-common";
import EinBottomNavigator from "./EinBottomNavigator.vue";

export default {
  props: ["order"],
  components: { EinBottomNavigator },
  data() {
    return {
      partnershipSubTypes: ["Partnership", "Joint Venture"],
      partnershipSubDescs: [
        "A partnership is a relationship existing between two or more persons or groups who join together to carry on a trade or business.  Each partner contributes money, property, labor, or skill, and expects to share in the profits and losses of the business.",
        "A joint venture is a partnership formed between two or more business entities. These businesses share risk or expertise on a specific project or group of projects.",
      ],
      corporationSubTypeValues: [
        "Corporation",
        "S Corporation",
        "Personal Service Corporation",
        "REIT",
        "RIC",
        "Settlement Fund",
      ],
      corporationSubTypes: [
        "Corporation",
        "S Corporation",
        "Personal Service Corporation",
        "Real Estate Investment Trust (REIT)",
        "Regulated Investment Conduit (RIC)",
        "Settlement Fund (under IRC Sec 468B)",
      ],
      corporationSubDescs: [
        "A corporation is a person or group of people who establish a legal entity by filing articles of incorporation with the state's secretary of state granting it certain legal powers, rights, privileges, and liabilities.",
        "The income of an S corporation generally is taxed to the shareholders of the corporation rather than to the corporation itself. However, an S corporation may still owe tax on certain income.",
        "A personal service corporation involves services in the fields of health, law, engineering, architecture, accounting, actuarial science, performing arts, or consulting.",
        "A REIT is an investment vehicle established for the benefit of a group of real estate investors.",
        "A RIC is a regulated investment company that applies to any domestic corporation that meets certain criteria.",
        "A settlement fund is established for the principal purpose of settling and paying claims against the electing taxpayer under Internal Revenue Code (IRC) Section 468B.",
      ],
      trustSubTypeValues: [
        "Bankruptcy Estate",
        "Charitable Lead Annuity",
        "Charitable Lead Uni",
        "Charitable Remainder Annuity",
        "Charitable Remainder Uni",
        "Conservatorship",
        "Custodianship",
        "Escrow",
        "FNMA",
        "GNMA",
        "Guardianship",
        "Irrevocable Trust",
        "Pooled Income Fund",
        "Qualified Funeral",
        // "Receivership",
        "Revocable Trust",
        // "Settlement Fund",
        "Trust (Others)"
      ],
      trustSubTypes: [
        "Bankruptcy Estate (Individual)",
        "Charitable Lead Annuity Trust",
        "Charitable Lead Unitrust",
        "Charitable Remainder Annuity Trust",
        "Charitable Remainder Unitrust",
        "Conservatorship",
        "Custodianship",
        "Escrow",
        "FNMA (Fannie Mae)",
        "GNMA (Ginnie Mae)",
        "Guardianship",
        "Irrevocable Trust",
        "Pooled Income Fund",
        "Qualified Funeral Trust",
        // "Receivership",
        "Revocable Trust",
        // "Settlement Fund (under IRC Sec 468B)",
        "Trust (All Others)"
      ],
      trustSubTypeDesc: [
        "A bankruptcy estate is a separate and distinct taxable entity from the individual debtor, created when an individual debtor files for bankruptcy under Chapter 7 or 11.",
        "A charitable lead annuity trust is one form of a charitable lead trust, which refers to an arrangement in which property income or investment income is given to a charity while the grantor is living, but the principal passes to other designated parties upon the grantor's death.",
        "A charitable lead unitrust is one form of a charitable lead trust, which refers to an arrangement in which property income or investment income is given to a charity while the grantor is living, but the principal passes to other designated parties upon the grantor's death.",
        "A charitable remainder annuity trust is one form of a charitable remainder trust, which refers to an arrangement in which property or money is donated to a charity, but the donor (called the grantor) continues to use the property and/or receive income from it while living.",
        "A charitable remainder unitrust is one form of a charitable remainder trust, which refers to an arrangement in which property or money is donated to a charity, but the donor (called the grantor) continues to use the property and/or receive income from it while living.",
        "A conservatorship is a trust created as the result of a legal process in which the court appoints an individual or organization to make financial decisions for another person who is determined to be financially incapable of making those decisions. A person under conservatorship is a conservatee, or protected person.",
        "A custodianship is a trust set up for a minor or incapacitated person.",
        "Escrow is a legal arrangement whereby an asset is delivered to a third party (escrow agent) to be held in trust pending a contingency or the fulfillment of a condition(s) in a contract.",
        "The Federal National Mortgage Association (FNMA) is a financial services company serving the home mortgage industry.",
        "The Government National Mortgage Association (GNMA) was created by the federal government as a wholly owned corporation within the U.S. Department of Housing and Urban Development (HUD) to provide financial assistance to low or moderate income homebuyers by promoting mortgage credit.",
        "A guardian is a person who has the legal authority (and the corresponding duty) to care for the personal and property interests of another person (due to infancy, incapacity, or disability), called a ward.",
        "In an irrevocable trust the grantor has no control of the trust (the trust cannot be repealed or annulled) and the trust will pay tax.",
        "A pooled income fund is one form of a charitable remainder trust, which refers to an arrangement in which property or money is donated to a charity, but the donor (called the grantor) continues to use the property and/or receive income from it while living.",
        "A qualified funeral trust (QFT) is a grantor trust, where the grantor purchases funeral services prior to death, and the applicable funeral home files one income tax return for all separate trusts.",
        // "A receivership is a legal or equitable proceeding in which a receiver is appointed for an insolvent corporation, partnership, or individual to preserve its assets for the benefit of affected parties.",
        "A revocable trust is a trust that may be altered or terminated during the grantor's lifetime.",
        // "A designated settlement fund or qualified settlement fund is a trust or fund established under IRC Sec 468B. This code section permits a defendant to deposit money or property into a trust or fund and receive a full and complete release of liability.",
        ""
      ],
      trustsHalf: 0,
      trustSubTypes1: [],
      trustSubTypes2: [],
    };
  },
  mounted() {
    this.trustsHalf = Math.ceil(this.trustSubTypeValues.length / 2);
    this.trustSubTypes1 = this.trustSubTypes.slice(0, this.trustsHalf);
    this.trustSubTypes2 = this.trustSubTypes.slice(this.trustsHalf);
  },
  methods: {
    setSubType(subType) {
      this.$emit('change', subType);

      if (this.order.sub_type == null || this.order.sub_type == "") {
        SnackUtils.warning("Please select one sub type.");
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
  max-width: 691px;

  cursor: pointer;
  margin: 0 auto;
  margin-bottom: 20px;
}
.entity-type-small {
  line-height: 19px;
  padding: 20px 25px;

  max-width: 450px;
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
