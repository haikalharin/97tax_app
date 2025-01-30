<template>
  <page>
    <div class="top-hero py-5">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <h1 class="py-2 text-center font-weight-bold text-uppercase">Frequently Asked Questions</h1>
            <h5 class="pb-2 font-weight-bold text-center">We have lots of answers</h5>
          </div>
        </div>
      </div>
    </div>
    <div class="content container py-6">
      <h1 class="pt-2 pb-6 text-center font-weight-bold main-black-text">Payment Plans</h1>
      <payment-plan-faqs></payment-plan-faqs>
      <h1 class="py-6 text-center font-weight-bold main-black-text">California Payment Plans</h1>
      <california-payment-plan-faqs></california-payment-plan-faqs>
      <h1 class="py-6 text-center font-weight-bold main-black-text">New Jersey Payment Plans</h1>
      <new-jersey-payment-plan-faqs></new-jersey-payment-plan-faqs>
      <h1 class="py-6 text-center font-weight-bold main-black-text">Georgia Payment Plans</h1>
      <georgia-payment-plan-faqs/>
      <h1 class="py-6 text-center font-weight-bold main-black-text">Illinois Payment Plans</h1>
      <illinois-payment-plan-faqs/>
      <h1 class="py-6 text-center font-weight-bold main-black-text">Michigan Payment Plans</h1>
      <michigan-payment-plan-faqs></michigan-payment-plan-faqs>
      <!-- <h1 class="py-5 text-center font-weight-bold main-black-text">Tax Liens</h1>
      <tax-lien-faqs></tax-lien-faqs> -->
      <h1 class="py-6 text-center font-weight-bold main-black-text">Penalty Waiver Letter</h1>
      <penalty-Waiver-Faq></penalty-Waiver-Faq>
      <h1 class="py-6 text-center font-weight-bold main-black-text">EIN Application</h1>
      <ein-faq></ein-faq>
    </div>
  </page>
</template>

<!-- sharea sale tag -->
<script>
  var shareasaleSSCID=shareasaleGetParameterByName("sscid");
function shareasaleSetCookie(e,a,r,s,t){
      if(e&&a)
            {var o,n=s?"; path="+s:"",i=t?"; domain="+t:"",l="";r&&((o=new Date).setTime(o.getTime()+r),l="; expires="+o.toUTCString()),
        document.cookie=e+"="+a+l+n+i
      }}
        function shareasaleGetParameterByName(e,a)
        {a||(a=window.location.href),e=e.replace(/[\[\]]/g,"\\$&");
        var r=new RegExp("[?&]"+e+"(=([^&#]*)|&|#|$)").exec(a);
return r?r[2]?decodeURIComponent(r[2].replace(/\+/g," ")):"":null}
shareasaleSSCID&&shareasaleSetCookie("shareasaleSSCID",
shareasaleSSCID,94670778e4,"/");
</script>

<style scoped>
  * {
    font-family: 'Raleway', sans-serif;
  }

  .top-hero {
    background-color: #0094be;
  }

  .top-hero h1,
  .top-hero h5 {
    color: #fff;
  }

  .main-black-text {
    color: #424242;
  }
</style>
