<template>
  <div class="container">
    <div class="inner-container">
      <div class="ein-form top-message mb10px">
        <img src="@/assets/check-mark.png" />
        <div class="bold-text">&nbsp;&nbsp;&nbsp;Payment successful</div>
        <div class="normal-text">
          &nbsp;&nbsp;&nbsp;&nbsp;Thank you! Your order number is {{order.id}} and is processing now.
        </div>
      </div>
      <div class="ein-form mb10px">
        <div class="progress-message-grp">
          <div class="pgmsg-text1">
            We are working now to retrieve your EIN from the IRS. We will send
            further updates to your email address at {{order.email}}.
          </div>
        </div>
      </div>

      <div class="ein-form mb20px">
        <div class="ein-summary-item mb20px">
          <div>Name</div>
          <div>{{ order.first_name }}&nbsp;{{ order.last_name }}</div>
        </div>
        <div class="ein-summary-item mb20px">
          <div>Email</div>
          <div>{{ order.email }}</div>
        </div>
        <div class="ein-summary-item mb20px">
          <div>Phone number</div>
          <div>{{ order.phone_number }}</div>
        </div>
        <div class="ein-summary-item mb20px">
          <div>Order Number</div>
          <div>{{ order.id }}</div>
        </div>
        <div class="ein-summary-item">
          <div>Mailing Address</div>
          <div>
            {{ order.mailing_address + (order.mailing_apt_suite ? " " + order.mailing_apt_suite : "") }},&nbsp;{{ order.mailing_city }},&nbsp;{{
              order.mailing_state
            }},&nbsp;{{ order.mailing_country }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ["order"],
  components: {},
  data() {
    return {
    };
  },
  methods: {},
};
</script>
<style scoped>
.inner-container {
  font-family: "Archivo", sans-serif;
  font-style: normal;
  font-weight: 700;
  max-width: 840px;
  width: 100%;
  margin: 0 auto;
}
.bold-text {
  font-style: normal;
  font-weight: 700;
  font-size: 24px;
  line-height: 16px;
  color: #000000;
  width: fit-content;
}
.normal-text {
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 24px;
  width: fit-content;
}
.top-message {
  padding: 20px;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  flex-wrap: wrap;
}
.progress-message-grp {
  background: #2a96c4;
  padding: 40px 0px;
  text-align: center;
  color: white;
  border-radius: 6px;
}

.pgmsg-text1 {
  font-weight: 400;
  font-size: 16px;
  line-height: 21px;
  padding-left: 40px;
  padding-right: 40px;
}

.mb10px {
  margin-bottom: 10px;
}
.mb20px {
  margin-bottom: 20px;
}
</style>
