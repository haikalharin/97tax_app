package com.repnox.nineseventax.util;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.common.PaginationResponse;
import com.repnox.nineseventax.features.ecommerce.AuthorizeResult;
import com.repnox.nineseventax.features.ecommerce.OrderConfirmation;
import com.repnox.nineseventax.features.ecommerce.OrderInfo;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.util.JwtPayloadResponse;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanController;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.model.Address;
import com.repnox.nineseventax.model.OrderData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.repnox.nineseventax.BaseTest.TEST_RESOURCES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderCreator {

    // OrderCreator is utilized out of the Spring context - to have readable, fluent-type code (util code with no @Autowired).
    private BaseTest test;

    private OrderCreator(BaseTest test) {
        this.test = test;
    }

    public static OrderCreator instantiateFor(BaseTest test) {
        if (test == null) {
            throw new IllegalArgumentException("'test' is not defined!");
        }
        return new OrderCreator(test);
    }

    public NextOrderCreator prepareNext() {
        return new NextOrderCreator(test);
    }

    public static class NextOrderCreator {
        private final String ORDER_INFO_TEMPLATE = TEST_RESOURCES + "/json/orderInfoTemplate.json";
        private final String VALID_PAYMENT = TEST_RESOURCES + "/json/cardinal/validPayment.json";
        private final String INVALID_PAYMENT = TEST_RESOURCES + "/json/cardinal/invalidPayment.json";

        private static final Address DEFAULT_VALID_ADDRESS = Address.builder()
                .address1("525 S Winchester Blvd").address2(null)
                .city("San Jose").state("CA").postalCode("95128")
                .build();

        private BaseTest test;

        private Address address;
        private boolean validPayment;

        private NextOrderCreator(BaseTest test) {
            this.test = test;
            this.address = DEFAULT_VALID_ADDRESS;
            this.validPayment = true;
        }

        public NextOrderCreator withAddress(Address address) {
            this.address = address;
            return this;
        }

        public NextOrderCreator withValidPayment(boolean validPayment) {
            this.validPayment = validPayment;
            return this;
        }

        // For test cases readability
        public NextOrderCreator fullyValid() {
            this.address = DEFAULT_VALID_ADDRESS;
            this.validPayment = true;
            return this;
        }

        public OrderData create() throws IOException {
            // Creating payment plan
            String email = "jane.doe." + System.currentTimeMillis() + "@mailinator.com";
            PaymentPlanDetails toCreate = PaymentPlanDetails.builder()
                    .firstName("Jane").lastName("Doe").phone("111-222-3333").email(email)
                    .totalDebt(BigDecimal.valueOf(1000)).monthlyPayment(25)
                    .isCalifornia(false).isNewJersey(false).isGeorgia(false).isIllinois(false)
                    .paymentMonths(72).paymentDayOfMonth("14")
                    .build();
            EntityExchangeResult<OrderRecord> response = test.getWebClient()
                    .post().uri(test.getBaseURI() + "/paymentplan")
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .syncBody(toCreate)
                    .exchange()
                    .expectBody(OrderRecord.class)
                    .returnResult();

            assertEquals(HttpStatus.OK, response.getStatus());
            String sessionCookie = response.getResponseHeaders().get("Set-Cookie").get(0);
            OrderRecord orderRecord = response.getResponseBody();
            Long orderNum = orderRecord.getOrderNum();

            // Payment and transaction authorization:
            int futureYear = LocalDate.now().plusYears(5).getYear();
            File file = new File(ORDER_INFO_TEMPLATE);
            OrderInfo orderInfo = test.getMapper().readValue(file, OrderInfo.class);
            orderInfo.setEmail(email);
            orderInfo.setCardExpYear("" + futureYear);
            orderInfo.setOrderNumber(orderNum.toString());
            // Set up addresses
            orderInfo.setBillingAddress1(address.getAddress1());
            orderInfo.setBillingCity(address.getCity());
            orderInfo.setBillingState(address.getState());
            orderInfo.setBillingPostalCode(address.getPostalCode());
            orderInfo.setShippingAddress1(address.getAddress1());
            orderInfo.setShippingCity(address.getCity());
            orderInfo.setShippingState(address.getState());
            orderInfo.setShippingPostalCode(address.getPostalCode());

            EntityExchangeResult<JwtPayloadResponse> jwtResponse = test.getWebClient()
                    .post().uri(test.getBaseURI() + "/ecommerce/jwt")
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .syncBody(orderInfo)
                    .header("Cookie", sessionCookie)
                    .attribute(PaymentPlanController.CURRENT_ORDER, orderRecord)
                    .exchange()
                    .expectBody(JwtPayloadResponse.class)
                    .returnResult();

            assertEquals(HttpStatus.OK, jwtResponse.getStatus());
            JwtPayloadResponse jwtPayload = jwtResponse.getResponseBody();
            assertNotNull(jwtPayload.getJwt());
            assertNotNull(jwtPayload.getPayload());

            // Do authorize transaction
            String paymentJwt = validPayment ? validPaymentJWT() : invalidPaymentJWT();
            orderInfo.setResponseJwt(paymentJwt);

            EntityExchangeResult<AuthorizeResult> authorizeResponse = test.getWebClient()
                    .post().uri(test.getBaseURI() + "/ecommerce/authorize")
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .syncBody(orderInfo)
                    .header("Cookie", sessionCookie)
                    .attribute(PaymentPlanController.CURRENT_ORDER, orderRecord)
                    .exchange()
                    .expectBody(AuthorizeResult.class)
                    .returnResult();

            assertEquals(HttpStatus.OK, authorizeResponse.getStatus());
            AuthorizeResult authorizeResult = authorizeResponse.getResponseBody();
            assertEquals(validPayment, authorizeResult.getSuccess());

            if (!validPayment) { // FAILED order created, returning it back
                String adminCookie = test.loginAdmin();
                OrderData theOrder = searchForOrder(email, adminCookie);
                assertEquals(OrderRecord.STATUS_FAILED, theOrder.getStatus());
                return theOrder;
            }

            // Check created order status is Processing
            String correlationId = authorizeResult.getCorrelationId();
            EntityExchangeResult<OrderConfirmation> confirmationResponse = test.getWebClient()
                    .get().uri(test.getBaseURI() + "/ecommerce/confirmation/" + correlationId)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .exchange()
                    .expectBody(OrderConfirmation.class)
                    .returnResult();

            assertEquals(HttpStatus.OK, confirmationResponse.getStatus());
            OrderConfirmation result = confirmationResponse.getResponseBody();
            assertEquals(OrderRecord.STATUS_PROCESSING, result.getStatus());

            // Generating PDF for the order
            EntityExchangeResult pdfResponse = test.getWebClient()
                    .post().uri(test.getBaseURI() + "/pdforderversion/refreshOrder")
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .syncBody(orderInfo)
                    .header("Cookie", sessionCookie)
                    .exchange()
                    .expectBody().returnResult();

            assertEquals(HttpStatus.OK, pdfResponse.getStatus());

            // Search for the created order and return it back
            String adminCookie = test.loginAdmin();
            OrderData createdOrder = searchForOrder(email, adminCookie);
            assertEquals(OrderRecord.STATUS_PROCESSING, createdOrder.getStatus());
            return createdOrder;
        }

        private OrderData searchForOrder(String email, String sessionCookie) {
            EntityExchangeResult<PaginationResponse<OrderData>> response = test.getWebClient()
                    .get().uri(test.getBaseURI() + "/admin/orders?" +
                            "pageNumber=0&pageSize=20&orderBy=createdDate&orderDirection=DESC" +
                            "&query.isCalifornia=false&query.isNewJersey=false&query.isGeorgia=false" +
                            "&query.isIllinois=false&query.isMichigan=false&query.status.any=true" +
                            "&query.email=" + email)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .exchange()
                    .expectBody(new ParameterizedTypeReference<PaginationResponse<OrderData>>() {
                    }).returnResult();

            assertEquals(HttpStatus.OK, response.getStatus());
            PaginationResponse<OrderData> pageResult = response.getResponseBody();
            assertNotNull(pageResult);
            assertNotNull(pageResult.getRows());
            assertThat(pageResult.getRows().size()).isGreaterThan(0);
            return pageResult.getRows().get(0);
        }

        private String validPaymentJWT() throws IOException {
            return generatePaymentJWT(VALID_PAYMENT);
        }

        private String invalidPaymentJWT() throws IOException {
            return generatePaymentJWT(INVALID_PAYMENT);
        }

        private String generatePaymentJWT(String file) throws IOException {
            String paymentJson = new String(Files.readAllBytes(Paths.get(file)));
            Map<String, Object> paymentMap = test.getMapper().readValue(paymentJson, HashMap.class);
            long nowMillis = System.currentTimeMillis();
            return test.getCentinelFacade().generateSignature(nowMillis, paymentMap);
        }

    }

}
