<template>
  <div>
    <div class="container">
      <div class="ein-form form-dims">
        <ValidationObserver ref="form">
          <form>
            <div class="form-group">
              <label>How many member(s) are in the LLC?</label>
              <ValidationProvider
                name="The count"
                rules="required|min_value:0|max_value:999999"
                v-slot="{ errors }"
              >
                <input
                  type="text"
                  :class="[
                    'form-control',
                    errors.length > 0 ? 'is-invalid' : '',
                  ]"
                  placeholder="Type here"
                  v-model="formInfo.llc_number_members"
                />
                <span class="ein-form-err">{{ errors[0] }}</span>
              </ValidationProvider>
            </div>
          </form>
        </ValidationObserver>
      </div>
    </div>
    <ein-bottom-navigator @back="$emit('back')" @continue="onContinue" />
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
      formInfo: {
        llc_number_members: this.order.llc_number_members,
      },
    };
  },
  methods: {
    onContinue() {
      this.$refs.form.validate().then((success) => {
        if (!success) {
          SnackUtils.warning("Please fill invalid fields.");
          return;
        }
        this.$emit("continue", this.formInfo);
      });
    },
  },
};
</script>
<style scoped>
.form-dims {
  max-width: 440px;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 40px;
  font-family: "Archivo", sans-serif;
}
</style>
