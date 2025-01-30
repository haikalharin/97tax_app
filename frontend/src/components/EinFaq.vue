<template>
  <div class="row">
    <div class="col-12 col-md-6">
      <faq-item title="Is an EIN required?">
        <p>
          Business entities like corporations and LLC's must use EIN numbers.
          Sole proprietors will need an EIN if you ever plan to hire employees or
          open a bank account in the name of the business. Some other examples
          includes estates, trusts, and non-profit organizations. Our easy checkout
          process will help you determine if an EIN is required.
        </p>
      </faq-item>

      <faq-item title="How will you get my EIN number from the IRS?">
        <p>
          We complete the entire process electronically directly with the IRS.
        </p>
      </faq-item>

      <faq-item title="Do I need to sign anything?">
        <p>
          There is a
          <a
            href="https://www.irs.gov/pub/irs-pdf/fss4.pdf"
            title="https://www.irs.gov/pub/irs-pdf/fss4.pdf"
            class="css-tgpl01">
            one page form
          </a>
          to sign electronically during our checkout that authorizes us as a
          "Third Party Designee" so that we can apply to the IRS on your behalf
          for your EIN number. This form does not authorize us to perform any
          other actions on your behalf, only the EIN number.
        </p>
      </faq-item>

      <faq-item title="How long does it take to get my EIN?">
        <p>
          We'll get your EIN number from the IRS within 24 hours. The EIN number is immediately
          valid and ready to use. The IRS will mail you a confirmation letter within 30 days with
          the same number.
        </p>
      </faq-item>

      <faq-item title="I lost my EIN number, can you get it?">
        <p>
          We will store your EIN number for up to 7 years
          and provide this to you in the event you lose or misplace it. If you have an
          existing EIN number but did not order through 97tax, we are not able to search
          for it. In that case, you will need to contact the IRS directly.
        </p>
      </faq-item>

      <faq-item title="How do you work so fast?">
        <p>
          We use a streamlined and efficient process that enables us to prepare your
          documents and apply electronically for your EIN directly with the IRS.
        </p>
      </faq-item>

      <faq-item title="What is an EIN number? ">
        <p>
          The EIN is an Employer Identification Number, also known as the Federal Employer
          Identification Number or the Federal Tax Identification Number, is a nine-digit
          number from the Internal Revenue Service to business entities in the United States
          for identification.
        </p>
      </faq-item>
    </div>

    <div class="col-12 col-md-6">
      <faq-item title="Are there any other fees?">
        <p>
          <!-- TODO: Possible to take the price from the DB, but on the other side landing page is fully static, no pre-rendering API calls -->
          No. Beyond the $49 one-time fee shown on the checkout page, there are no additional
          fees or hidden costs, ever.
        </p>
      </faq-item>

      <faq-item title="What does the IRS do with EIN numbers?">
        <p>
          The IRS uses the number to identify a business or sole proprietor for tax returns.
          Your bank may use the EIN number to identify a business or sole proprietor.
        </p>
      </faq-item>

      <faq-item title="Are you authorized to get my EIN number?">
        <p>
          During checkout you will electronically sign an IRS form that gives us permission
          to retrieve your EIN number directly from the IRS. This form does not give us
          permission to take any other actions.
        </p>
      </faq-item>

      <faq-item title="Can I do this myself with the IRS directly?">
        <p>
          Yes. Our service is fast and convenient and there are other options available,
          including mailing the appropriate forms through the Post Office directly to the IRS.
        </p>
      </faq-item>

      <faq-item title="I have an EIN already. Can I change it?">
        <p>
          No. An existing EIN number cannot be changed, you will need to apply for a new EIN number.
        </p>
      </faq-item>

      <faq-item title="Can I have more than one EIN number?">
        <p>
          Yes, there is no limit on the number of EIN's you can have. You may need multiple EIN's
          if you have multiple businesses or one business being separated into multiple entities.
        </p>
      </faq-item>

      <faq-item title="How do I contact you?">
        <p>
          For the fastest service, or if it’s after hours, please use our <a href="/contact-us" target="_blank">contact form</a>
          or email us at <a href="mailto:support@97tax.com">support@97tax.com</a> and we’ll reply to you as soon as possible.
          Please include your order number if you have an existing order with us. You can reach us by phone at <a href="tel:8138530140">813-853-0140</a>.
        </p>
      </faq-item>

    </div>
  </div>
</template>
<style scoped>
@import url("https://fonts.googleapis.com/css?family=Raleway:400,700,800,900,900i");

p {
  font-weight: 400;
  font-family: "Raleway", sans-serif;
  color: #464646;
}
</style>
