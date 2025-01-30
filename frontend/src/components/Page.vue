<template>
  <div v-if="!isAdmin">
    <top-nav
      v-if="!hideTopNav"
      :navType="navType"
      :showLogo="showLogo"
      :logoHref="logoHref"
    ></top-nav>
    <div class="container-fluid" style="padding: 0">
      <slot></slot>
    </div>
    <bottom-nav-2 :navType="navType"></bottom-nav-2>
  </div>
  <div v-else>
    <internal-header />
    <div class="container-fluid" style="padding-bottom: 90px">
      <slot></slot>
    </div>
    <internal-bottom-nav></internal-bottom-nav>
  </div>
</template>

<script>
import BottomNav2 from "./BottomNav2.vue";
import TopNav2 from "./TopNav2.vue";
export default {
  components: { BottomNav2, TopNav2 },
  props: {
    navType: {
      type: String,
    },
    logoHref: {
      type: String,
    },
    hideTopNav: {
      type: Boolean,
      default: false,
    },
    showLogo: {
      type: Boolean,
      default: true,
    },
  },

  data() {
    return {
      isAdmin: false,
    };
  },

  beforeMount() {
    this.isAdmin = window.location.href.includes("admin");
  },
};
</script>
