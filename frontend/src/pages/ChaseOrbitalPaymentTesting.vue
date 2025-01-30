<template>
  <page :navType="'testing-chase-orbital'">
    <div class="d-flex flex-column m-auto pt-5 pb-5" style="width: 600px;">
        <p class="text-center">Chase Orbital Payment Testing Page</p>
        <button type="button" class="btn btn-green mt-3 w-100 font-weight-bold text-uppercase btn-secondary" @click="proceedPayment()">Proceed Payment</button>
    </div>
  </page>
  
</template>

<script>
    import { AXIOS } from '../scripts/http-common';
export default {
    name: "ChaseOrbitalPaymentTesting",
    methods: {
        proceedPayment() {
            AXIOS.post('/admin/chaseOrbitalPayment', {}).then(res=>{
                console.log(res);
            })
            // console.log('here');
        }
    }
}
</script>
