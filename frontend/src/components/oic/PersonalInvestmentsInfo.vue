<template>
  <div class="container">
    <div class="row">
      <div class="col">
        <h2>Personal Investments</h2>
      </div>
    </div>

    <div v-for="(a, index) in personalInvestmentsInfo.investmentAccounts">
      <oic-investment-account-input-row
        :investmentAccount="personalInvestmentsInfo.investmentAccounts[index]"
        v-on:input="updateAccount(index, $event)"
        v-on:remove="removeAccount(a)"
        :key="a._key"
      >
      </oic-investment-account-input-row>
    </div>

    <div class="row mb-2">
      <div class="col">
        <button
          v-on:click="addAccount"
          class="btn btn-secondary">
          + Add Another Investment
        </button>
      </div>
    </div>

    <div class="row">
      <div class="col">
        <button class="btn btn-secondary" v-on:click="$emit('back', 'personalInvestmentsInfo')">Back</button>
        <button class="btn btn-primary" v-on:click="$emit('continue', 'personalInvestmentsInfo')">Continue</button>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        personalInvestmentsInfo: {
          investmentAccounts: [
            {
              _key: Math.random()
            }
          ]
        }
      }
    },
    methods: {
      addAccount() {
        this.personalInvestmentsInfo.investmentAccounts.push({
        });
      },
      updateAccount(i, value) {
        this.personalInvestmentsInfo.investmentAccounts.splice(i, 1, value);
      },
      removeAccount(a) {
        let index = this.personalInvestmentsInfo.investmentAccounts.indexOf(a);
        this.personalInvestmentsInfo.investmentAccounts.splice(index, 1);
      }
    }
  }
</script>
