package com.repnox.nineseventax.features.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CreatePaymentRequestAfterSettled implements OrbitalRequest {
    @JsonProperty("version")
    private String version;

    @JsonProperty("transType")
    private String transType;

    @JsonProperty("merchant")
    private MerchantRequest merchant;

    @JsonProperty("order")
    private Order order;

    @JsonProperty("paymentInstrument")
    private PaymentInstrument paymentInstrument;

    @Getter
    @Builder
    @ToString
    public static class Order {
        @JsonProperty("orderID")
        private String orderId;

        @JsonProperty("comments")
        private String comments;

        @JsonProperty("industryType")
        private String industryType;

        @JsonProperty("amount")
        private String amount;
    }

    @Getter
    @Builder
    @ToString
    public static class PaymentInstrument {
        @JsonProperty("useProfile")
        private UseProfile useProfile;
    }
}
