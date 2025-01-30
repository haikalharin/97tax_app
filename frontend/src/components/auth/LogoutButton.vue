<template>
  <div class="float-right">
    <button class="btn btn-outline-secondary" @click="logout()">Logout</button>
  </div>
</template>

<script>
  import {AXIOS} from '../../scripts/http-common'
  export default {
    methods: {
      logout() {
        AXIOS.post('/auth/logout')
          .then(response => {
            this.$router.push('/');
          })
          .catch(e => {
            alert("An error has occurred while attempting to log out. Please contact the administrator.");
            console.log(e);
          });
      }
    }
  }
</script>
