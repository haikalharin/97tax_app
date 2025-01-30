<template>
  <div class="container">
    <div class="row">
      <div class="col">
        <h2>Personal Assets</h2>
      </div>
    </div>

    <div v-for="(a, index) in personalAssetsInfo.bankAccounts">
      <oic-bank-account-input-row
        :bankAccount="personalAssetsInfo.bankAccounts[index]"
        v-on:input="updateBankAccount(index, $event)"
        v-on:remove="removeBankAccount(a)"
        :key="a._key"
        >
      </oic-bank-account-input-row>
    </div>

    <div class="row mb-2">
      <div class="col">
        <button
          v-on:click="addBankAccount"
          class="btn btn-secondary">
          + Add Another Asset
        </button>
      </div>
    </div>

    <div class="row">
      <div class="col">
        <button class="btn btn-secondary" v-on:click="$emit('back', 'personalAssetsInfo')">Back</button>
        <button class="btn btn-primary" v-on:click="$emit('continue', 'personalAssetsInfo')">Continue</button>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        nextBankAccountKey: 1,
        personalAssetsInfo: {
          bankAccounts: [
            {
              _key: Math.random()
            }
          ]
        }
      }
    },
    methods: {
      addBankAccount() {
        this.personalAssetsInfo.bankAccounts.push({
        });
      },
      updateBankAccount(i, value) {
        this.personalAssetsInfo.bankAccounts.splice(i, 1, value);
      },
      removeBankAccount(a) {
        let index = this.personalAssetsInfo.bankAccounts.indexOf(a);
        this.personalAssetsInfo.bankAccounts.splice(index, 1);
      }
    }
  }
</script>
