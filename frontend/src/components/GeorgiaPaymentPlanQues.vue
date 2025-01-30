<template>
  <div class="row">
    <div class="col-12">
      <ques-item title="Payment Plans">
        <p class="mb-4" >The State of Georgia has provided access to payment plans. Individual taxpayers who owe the Georgia Department of Revenue can pay in monthly installments for up to {{paymentPlanConfig.maxMonth}} months (five years). Payment plan requests setup through 97tax.com always calculate your payment at the full {{paymentPlanConfig.maxMonth}} months and according to the published rules of the state.</p>
        <p class="mb-4" >While Georgia generally will not need a financial statement, they may need some additional financial information later from you. In those cases, the state will contact you by phone or by mail at the provided address. Take a look at our FAQ further down this page for more information.</p>
        <p>Hiring a local attorney is expensive and unnecessary, and doing it yourself can cause months of delay and even more frustration. We prepare all the Georgia Department of Revenue documents necessary to submit your payment plan. Within one business day, we'll mail your completed application to the mailing address you provide. Just sign the forms, drop it in the mail, and you're done.</p>
      </ques-item>

      <ques-item title="Flexibility">
        <p class="mb-4" >By applying for a payment plan online through 97tax.com, our system will automatically calculate the minimum payment allowed by Georgia with a maximum of {{paymentPlanConfig.maxMonth}} months. During the process, you can also select your normal monthly due date and increase payments to pay off your balance faster.</p>
        <p class=""><a href="#top-hero"><span class="font-weight-bold highlight-text-color" style="text-decoration: underline;">Click here</span></a> to see if you qualify and get your application for an installment plan started now.</p>
      </ques-item>

      <ques-item title="Eligibility">
        <p>The maximum amount you can owe to the Georgia Department of Revenue and still be eligible for an installment agreement is ${{formatCurrencyWithCommas(paymentPlanConfig.maxAmount)}}. You also must:</p>
          <ul style="padding-inline-start: 20px;">
            <li>
              Pay the balance in a maximum of {{paymentPlanConfig.maxMonth}} months.
            </li>
            <li>
              Have filed all of your Georgia state tax returns.
            </li>
            <li>
              Not be in middle of bankruptcy.
            </li>
          </ul>
        <p>It's important to note that if you miss payments on the Georgia installment agreement, the state will cancel the payment plan.</p>
      </ques-item>
    </div>
  </div>
</template>
<script>
  import { paymentPlansConfig } from '../../config/plans-config';
  export default {
    data() {
      return {
        paymentPlanConfig:paymentPlansConfig.californiaPaymentPlan
      }
    },
  }
</script>

<style scoped>
  @import url('https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i');
  p {
    font-weight: 400;
    font-family: 'Raleway', sans-serif;
  }
</style>
