<template>
  <page>
    <div class="top-section">
      <div class="container-fluid">
        <b-row>
          <b-col class="text-center">
            <div class="title">
              MONEY BACK GUARANTEE IF ALL INFORMATION PROVIDED IS ACCURATE
            </div>
            <div class="container subtitle">
              PLEASE READ THIS DOCUMENT CAREFULLY! IT CONTAINS IMPORTANT
              INFORMATION ABOUT YOUR RIGHTS AND OBLIGATIONS AS WELL AS
              LIMITATIONS AND EXCLUSIONS THAT APPLY TO YOU.
            </div>
            <!-- <img src="@/assets/guarantee.svg" class="mobile-view-img"> -->
          </b-col>
        </b-row>
      </div>
    </div>
    <!-- </div> -->
    <div class="container pt-5">
      <p>
        <span class="bold-text">Limited Money Back Guarantee:</span> 97tax
        guarantees that it will provide error free tax documents and will abide
        by currently published IRS guidelines. 97tax also guarantees that it
        will refund the full purchase price to the customer, in accordance with
        industry standards, if all the information provided by the customer is
        accurate and true, and the customer’s penalty is not abated. The
        customer is entitled to 100% of their money back, should they meet all
        of the following conditions:
      </p>
      <ul>
        <li>
          The customer’s tax penalty was not abated (commonly referred to as
          “waived”) by the IRS;
        </li>
        <li>
          The customer provided all of the information requested and deemed
          necessary by 97tax and the information provided is true and accurate;
        </li>
        <li>
          The customer must have signed and submitted/mailed to the IRS, in
          accordance with the 97tax instructions, the tax documents 97tax
          prepared for the customer; and
        </li>
        <li>
          Within 30 days of receipt, the customer must provide the denial notice
          or letter addressed to them from the IRS that is specifically
          declining the abatement request.
        </li>
      </ul>
      <p>
        Understanding these parameters, the customer consents to sharing any
        information or data with 97tax that 97tax deems necessary to assist the
        customer in procuring the abatement of penalty. Further, customer
        accepts the terms and conditions set forth above regarding the 100%
        money back guarantee.
      </p>
      <p>
        THIS LIMITED MONEY BACK GUARANTEE IS NOT A WARRANTY. 97TAX MAY CHANGE OR
        CANCEL IT AT ANY TIME BEFORE ACCEPTING YOUR CASE.
      </p>
      <p>
        <span class="bold-text">DISCLAIMER:</span>THE NATURE OF THIS PRODUCT IS
        SERVICE ORIENTED, AND THE OUTCOME IS DEPENDENT ON THE INFORMATION
        PROVIDED BY THE CUSTOMER AND THE DETERMINATION MADE BY THE IRS. AS SUCH,
        NEITHER 97TAX NOR ANY OF ITS EMPLOYEES, AGENTS, ASSOCIATES, OR
        AFFILIATES ARE LIABLE FOR ANY AMOUNTS THAT THE CUSTOMER BELIEVES SHOULD
        BE ABATED OR WAIVED OR OWED TO HIM/HER. THIS COMPANY MAKES NO ATTEMPT TO
        WARRANT OR PROMISE A SPECIFIC DOLLAR AMOUNT BY WAY OF USING OUR SERVICE.
        BY SIGNING THE ABOVE PAPERWORK AND HIRING 97TAX TO PROCURE AN ABATEMENT
        OF PENALTY, CUSTOMER RELEASES 97TAX FROM ANY AND ALL CLAIMS THAT MAY
        ARISE DURING THE PROCESS. FURTHER, CUSTOMER UNDERSTANDS THAT SHOULD A
        DISPUTE ARISE BETWEEN CUSTOMER AND 97TAX, HIS/HER REMEDY WILL BE LIMITED
        TO ONLY THE FEE PAID BY CUSTOMER FOR USE OF THE SERVICE. PURSUANT TO
        THIS WARRANTY, SHOULD ANY FUNDS BE PAID TO THE CUSTOMER IN SATISFACTION
        OF A DISPUTE, THE PAYMENT OF SUCH FUNDS WILL FULFILL ANY OBLIGATIONS
        THAT 97TAX, ITS EMPLOYEES, AGENTS, ASSOCIATES, OR AFFILIATES OWE TO THE
        CUSTOMER.
      </p>
    </div>
  </page>
</template>

export default {
    name: 'PenaltyGuaranteeDetail',
    data() {
        return {

        }
    },
    methods: {

    }
}
</script>

<style scoped>
.top-section {
  background-color: #0094be;
  padding: 30px 0px;
}

.title {
  font-weight: bold;
  font-size: 30px;
  line-height: 50px;
  text-align: center;
  color: #fff;
}
.subtitle {
  margin-top: 20px;
  font-weight: 100;
  font-size: 16px;
  text-align: center;
  color: #fff;
}
.bold-text {
  font-weight: bold;
}
</style>
