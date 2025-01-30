<template>
  <div>
      <p v-if="isValid">Tax return extension</p>  
  </div>
  

  
</template>

<script>
  import {AXIOS} from '../scripts/http-common'

  export default {
    data() {
      return {
        password: null,
        isValid:false
      }
    },
    mounted() {
      this.getPasswordAndVarify();
    },
    methods: {
      getPasswordAndVarify() {
          this.password = prompt("Please enter password:");
        if (this.password == null || this.password == "") {
          alert('user cancel the popup please reload')
        } else {
            AXIOS.post('/taxextension/validate', {password:this.password})
              .then((response) => {
                if(response.data == true){
                  sthis.isValid = true;
                 //alert('password is valid')
                }else {
                  alert('password not valid')
                }
              })
              .catch(()=>{
                  this.isValid = false;
              })
         
        }
       
      },
    }
  }

</script>
