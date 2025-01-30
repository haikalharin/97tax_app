package com.repnox.nineseventax.features.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.features.payment.request.*;
import com.repnox.nineseventax.features.payment.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class OrbitalControllerTest extends BaseTest {

    private final String CREATE_PROFILE = TEST_RESOURCES + "/json/orbital/createProfile.json";
    private final String UPDATE_PROFILE = TEST_RESOURCES + "/json/orbital/updateProfile.json";
    private final String DELETE_PROFILE = TEST_RESOURCES + "/json/orbital/deleteProfile.json";
    private final String CREATE_PAYMENT_VISA = TEST_RESOURCES + "/json/orbital/createPaymentVisa.json";
    private final String CREATE_PAYMENT_MASTERCARD = TEST_RESOURCES + "/json/orbital/createPaymentMastercard.json";
    private final String CREATE_PAYMENT_AND_CAPTURE = TEST_RESOURCES + "/json/orbital/createPaymentAndCapture.json";
    private final String REVERSE_PAYMENT = TEST_RESOURCES + "/json/orbital/reversal.json";
    private final String CAPTURE_PAYMENT = TEST_RESOURCES + "/json/orbital/capturePayment.json";
    private final String SETTLE_PAYMENT = TEST_RESOURCES + "/json/orbital/settlePayment.json";
    private final String INQUIRY_PAYMENT = TEST_RESOURCES + "/json/orbital/inquiryPayment.json";
    private final String REFUND_REFNUM_PAYMENT = TEST_RESOURCES + "/json/orbital/refundWithRefNum.json";
    private final String REFUND_WITHOUT_REFNUM_PAYMENT = TEST_RESOURCES + "/json/orbital/refundWithoutRefNum.json";

    //@Test
    public void profileCrudTests() throws IOException {
        ProfileResponse profileResponse = createProfile();
        String customerRefNum = profileResponse.getProfile().getCustomerRefNum();
        String version = profileResponse.getVersion();
        String bin = profileResponse.getMerchant().getBin();

        ProfileResponse fetchProfileResponseAfterCreate = fetchProfile(version, bin, customerRefNum);
        assertNotNull(fetchProfileResponseAfterCreate);
        assertNotNull(fetchProfileResponseAfterCreate.getProfile().getCustomerRefNum());
        assertEquals("Customer Name", fetchProfileResponseAfterCreate.getProfile().getCustomerName());
        assertEquals(customerRefNum, fetchProfileResponseAfterCreate.getProfile().getCustomerRefNum());

        updateProfile(profileResponse.getProfile().getCustomerRefNum());
        deleteProfile();

        ProfileResponse fetchProfileResponseAfterDelete = fetchProfile(version, bin, customerRefNum);
        assertNull(fetchProfileResponseAfterDelete);
    }

    @Test
    public void createPaymentVisaReverseTest() throws IOException {
        CreatePaymentResponse createPaymentResponse = createPaymentVisaResponse("vReserve-123");
        reverse(createPaymentResponse.getOrder());
    }

    @Test
    public void createPaymentMasterCardReverseTest() throws IOException {
        CreatePaymentResponse createPaymentResponse = createPaymentMasterCardResponse("mReserve-123");
        reverse(createPaymentResponse.getOrder());
    }

    @Test
    public void createPaymentVisaRefundTest() throws IOException {
        CreatePaymentResponse createPaymentResponse1 = createPaymentVisaResponse("vRefundTest-123");
        CreatePaymentResponse createPaymentResponse2 = createPaymentVisaResponse("vRefundTest-456");
        CreatePaymentResponse createPaymentResponse3 = createPaymentVisaResponse("vRefundTest-789");
        CreatePaymentResponse createPaymentResponse4 = createPaymentVisaResponse("vRefundTest-159");
        settle();
        refundRefNum(createPaymentResponse1.getOrder(), true, "1000");
        refundRefNum(createPaymentResponse2.getOrder(), false, "");
        refundWithoutRefNum(createPaymentResponse3.getOrder(), true, "1000");
        //refundWithoutRefNum(createPaymentResponse4.getOrder(), false, "");
    }

    @Test
    public void createPaymentMasterCardRefundTest() throws IOException {
        CreatePaymentResponse createPaymentResponse1 = createPaymentMasterCardResponse("mRefundTest-123");
        CreatePaymentResponse createPaymentResponse2 = createPaymentMasterCardResponse("mRefundTest-456");
        CreatePaymentResponse createPaymentResponse3 = createPaymentMasterCardResponse("mRefundTest-789");
        CreatePaymentResponse createPaymentResponse4 = createPaymentMasterCardResponse("mRefundTest-159");
        settle();
        refundRefNum(createPaymentResponse1.getOrder(), true, "1000");
        refundRefNum(createPaymentResponse2.getOrder(), false, "");
        refundWithoutRefNum(createPaymentResponse3.getOrder(), true, "1000");
        //refundWithoutRefNum(createPaymentResponse4.getOrder(), false, "");
    }

    private CreatePaymentResponse createPaymentMasterCardResponse(String orderId) throws IOException {
        File createPaymentMCFile = new File(CREATE_PAYMENT_MASTERCARD);
        CreatePaymentRequest createPaymentRequest = mapper.readValue(createPaymentMCFile, CreatePaymentRequest.class);
        if (StringUtils.isNotBlank(orderId)) {
            createPaymentRequest.getOrder().setOrderID(orderId + "-" + new Random().nextInt(100) + 1);
        }

        HttpEntity<CreatePaymentRequest> createPaymentRequestHttpEntity = new HttpEntity(createPaymentRequest);

        ResponseEntity<CreatePaymentResponse> createPaymentMastercardResponse =
                restTemplate.exchange(baseURI + "/payment", HttpMethod.POST, createPaymentRequestHttpEntity, CreatePaymentResponse.class);

        assertNotNull(createPaymentMastercardResponse.getBody());
        log.debug("createPaymentMasterCardResponse : {}", mapper.writeValueAsString(createPaymentMastercardResponse.getBody()));
        assertNotNull(createPaymentMastercardResponse.getBody().getOrder().getOrderID());
        assertNotNull(createPaymentMastercardResponse.getBody().getOrder().getTxRefIdx());
        assertNotNull(createPaymentMastercardResponse.getBody().getOrder().getTxRefNum());

        return createPaymentMastercardResponse.getBody();
    }

    private CreatePaymentResponse createPaymentVisaResponse(String orderId) throws IOException {
        File createPaymentVisaFile = new File(CREATE_PAYMENT_VISA);
        CreatePaymentRequest createPaymentRequest = mapper.readValue(createPaymentVisaFile, CreatePaymentRequest.class);
        createPaymentRequest.getOrder().setOrderID(orderId);

        if (StringUtils.isNotBlank(orderId)) {
            createPaymentRequest.getOrder().setOrderID(orderId + "-" + new Random().nextInt(100) + 1);
        }

        HttpEntity<CreatePaymentRequest> createPaymentRequestHttpEntity = new HttpEntity(createPaymentRequest);

        ResponseEntity<CreatePaymentResponse> createPaymentVisaResponse =
                restTemplate.exchange(baseURI + "/payment", HttpMethod.POST, createPaymentRequestHttpEntity, CreatePaymentResponse.class);

        assertNotNull(createPaymentVisaResponse.getBody());
        log.debug("createPaymentVisaResponse : {}", mapper.writeValueAsString(createPaymentVisaResponse.getBody()));
        assertNotNull(createPaymentVisaResponse.getBody().getOrder());
        assertNotNull(createPaymentVisaResponse.getBody().getOrder().getOrderID());
        assertNotNull(createPaymentVisaResponse.getBody().getOrder().getTxRefIdx());
        assertNotNull(createPaymentVisaResponse.getBody().getOrder().getTxRefNum());
        assertNotNull(createPaymentVisaResponse.getBody().getOrder().getStatus().getAuthorizationCode());
        assertEquals("Approved", createPaymentVisaResponse.getBody().getOrder()
                .getStatus().getProcStatusMessage());
        assertEquals("Approved", createPaymentVisaResponse.getBody().getOrder()
                .getStatus().getProcStatusMessage());

        return createPaymentVisaResponse.getBody();
    }

    private CreatePaymentResponse createPaymentAndCaptureResponse() throws IOException {
        File createPaymentAndCapture = new File(CREATE_PAYMENT_AND_CAPTURE);
        CreatePaymentRequest createPaymentRequest = mapper.readValue(createPaymentAndCapture, CreatePaymentRequest.class);
        HttpEntity<CreatePaymentRequest> createPaymentRequestHttpEntity = new HttpEntity(createPaymentRequest);

        ResponseEntity<CreatePaymentResponse> createPaymentAndCaptureResponse =
                restTemplate.exchange(baseURI + "/payment", HttpMethod.POST, createPaymentRequestHttpEntity, CreatePaymentResponse.class);

        assertNotNull(createPaymentAndCaptureResponse.getBody());
        log.debug("createPaymentAndCaptureResponse : {}", mapper.writeValueAsString(createPaymentAndCaptureResponse.getBody()));
        assertNotNull(createPaymentAndCaptureResponse.getBody().getOrder().getOrderID());
        assertNotNull(createPaymentAndCaptureResponse.getBody().getOrder().getTxRefIdx());
        assertNotNull(createPaymentAndCaptureResponse.getBody().getOrder().getTxRefNum());

        return createPaymentAndCaptureResponse.getBody();
    }

    private CapturePaymentResponse capturePayment(String orderId, String txRefNum) throws IOException {
        File capturePaymentFile = new File(CAPTURE_PAYMENT);
        CapturePaymentRequest request = mapper.readValue(capturePaymentFile, CapturePaymentRequest.class);
        request.getOrder().setOrderID(orderId);
        request.getOrder().setTxRefNum(txRefNum);

        HttpEntity<CapturePaymentRequest> capturePaymentRequestHttpEntity = new HttpEntity(request);

        ResponseEntity<CapturePaymentResponse> capturePaymentResponse =
                restTemplate.exchange(baseURI + "/payment/capture", HttpMethod.POST, capturePaymentRequestHttpEntity, CapturePaymentResponse.class);

        assertNotNull(capturePaymentResponse.getBody());
        log.debug("capturePayment : {}", capturePaymentResponse.getBody());
        assertNotNull(capturePaymentResponse.getBody().getOrder().getOrderID());

        return capturePaymentResponse.getBody();
    }

    private InquiryResponse inquiryPayment(String orderId) throws IOException {
        File inquiryPayment = new File(INQUIRY_PAYMENT);
        InquiryRequest request = mapper.readValue(inquiryPayment, InquiryRequest.class);
        request.getOrder().setOrderID(orderId);

        HttpEntity<InquiryRequest> inquiryRequestHttpEntity = new HttpEntity(request);

        ResponseEntity<InquiryResponse> inquiryResponse =
                restTemplate.exchange(baseURI + "/payment/capture", HttpMethod.POST, inquiryRequestHttpEntity, InquiryResponse.class);

        assertNotNull(inquiryResponse.getBody());
        log.debug("inquiryPayment : {}", mapper.writeValueAsString(inquiryResponse.getBody()));
        assertNotNull(inquiryResponse.getBody().getOrder().getOrderID());

        return inquiryResponse.getBody();
    }

    private RefundResponse refundRefNum(OrderResponse orderResponse, boolean withRefundAmount, String refundAmount) throws IOException {
        File refundFile = new File(REFUND_REFNUM_PAYMENT);
        RefundRequest request = mapper.readValue(refundFile, RefundRequest.class);
        request.getOrder().setOrderID(orderResponse.getOrderID());
        request.getOrder().setTxRefNum(orderResponse.getTxRefNum());
        if (withRefundAmount) {
            if (StringUtils.isNotBlank(refundAmount)) {
                request.getOrder().setAmount(refundAmount);
            } else {
                request.getOrder().setAmount(orderResponse.getAmount());
            }
        } else {
            request.getOrder().setAmount(orderResponse.getAmount());
        }

        request.getOrder().setIndustryType(orderResponse.getIndustryType());

        HttpEntity<RefundRequest> refundRequestHttpEntity = new HttpEntity(request);

        ResponseEntity<RefundResponse> refundResponseResponseEntity =
                restTemplate.exchange(baseURI + "/payment/refund", HttpMethod.POST, refundRequestHttpEntity, RefundResponse.class);

        assertNotNull(refundResponseResponseEntity.getBody());
        log.debug("refund : {}", mapper.writeValueAsString(refundResponseResponseEntity.getBody()));

        return refundResponseResponseEntity.getBody();
    }

    private RefundResponse refundWithoutRefNum(OrderResponse orderResponse, boolean withRefundAmount, String refundAmount) throws IOException {
        File refundFile = new File(REFUND_WITHOUT_REFNUM_PAYMENT);

        RefundRequest request = mapper.readValue(refundFile, RefundRequest.class);
        request.getOrder().setOrderID(orderResponse.getOrderID());
        if (withRefundAmount) {
            if (StringUtils.isNotBlank(refundAmount)) {
                request.getOrder().setAmount(refundAmount);
            } else {
                request.getOrder().setAmount(orderResponse.getAmount());
            }
        } else {
            request.getOrder().setAmount(orderResponse.getAmount());
        }

        request.getOrder().setIndustryType(orderResponse.getIndustryType());

        HttpEntity<RefundRequest> refundRequestHttpEntity = new HttpEntity(request);

        ResponseEntity<RefundResponse> refundResponseResponseEntity =
                restTemplate.exchange(baseURI + "/payment/refund", HttpMethod.POST, refundRequestHttpEntity, RefundResponse.class);

        assertNotNull(refundResponseResponseEntity.getBody());
        log.debug("refund : {}", mapper.writeValueAsString(refundResponseResponseEntity.getBody()));

        return refundResponseResponseEntity.getBody();
    }

    private ReversalResponse reverse(OrderResponse orderResponse) throws IOException {
        File reverseFile = new File(REVERSE_PAYMENT);
        ReversalRequest request = mapper.readValue(reverseFile, ReversalRequest.class);
        request.getOrder().setOrderID(orderResponse.getOrderID());
        request.getOrder().setTxRefNum(orderResponse.getTxRefNum());
        request.getOrder().setAmount(orderResponse.getAmount());
        request.getOrder().setIndustryType(orderResponse.getIndustryType());
        log.debug("reversal req : {}", mapper.writeValueAsString(request));

        HttpEntity<RefundRequest> refundRequestHttpEntity = new HttpEntity(request);

        ResponseEntity<ReversalResponse> reverseResponseResponseEntity =
                restTemplate.exchange(baseURI + "/payment/reversal",
                        HttpMethod.POST, refundRequestHttpEntity, ReversalResponse.class);

        assertNotNull(reverseResponseResponseEntity.getBody());
        log.debug("reversal : {}", mapper.writeValueAsString(reverseResponseResponseEntity.getBody()));

        assertNotNull(reverseResponseResponseEntity.getBody().getOrder());
        assertNotNull(reverseResponseResponseEntity.getBody().getOrder().getStatus());
        assertEquals("1", reverseResponseResponseEntity.getBody().getOrder().getStatus().getApprovalStatus());

        return reverseResponseResponseEntity.getBody();
    }

    private SettleResponse settle() throws IOException {
        File settleFile = new File(SETTLE_PAYMENT);
        SettleRequest request = mapper.readValue(settleFile, SettleRequest.class);

        HttpEntity<SettleRequest> refundRequestHttpEntity = new HttpEntity(request);

        ResponseEntity<SettleResponse> settleResponseResponseEntity =
                restTemplate.exchange(baseURI + "/payment/settle",
                        HttpMethod.POST, refundRequestHttpEntity, SettleResponse.class);

        assertNotNull(settleResponseResponseEntity.getBody());
        log.debug("Settle : {}", mapper.writeValueAsString(settleResponseResponseEntity.getBody()));

        assertNotNull(settleResponseResponseEntity.getBody().getBatch());
        assertNotNull(settleResponseResponseEntity.getBody().getBatch().getBatchSeqNum());
        assertNotNull(settleResponseResponseEntity.getBody().getOrder());
        assertEquals("0", settleResponseResponseEntity.getBody().getOrder().getStatus().getProcStatus());

        return settleResponseResponseEntity.getBody();
    }

    private ProfileResponse createProfile() throws IOException {
        File file = new File(CREATE_PROFILE);
        ProfileRequest request = mapper.readValue(file, ProfileRequest.class);
        HttpEntity<ProfileRequest> entity = new HttpEntity<>(request);

        ResponseEntity<ProfileResponse> createProfileResponse =
                restTemplate.exchange(baseURI + "/payment/profile", HttpMethod.POST, entity, ProfileResponse.class);


        assertNotNull(createProfileResponse.getBody());
        log.debug("createProfileResponse : {}", mapper.writeValueAsString(createProfileResponse.getBody()));
        if (createProfileResponse.getBody().getProfile() != null) {
            assertNotNull(createProfileResponse.getBody().getProfile().getCustomerRefNum());
            assertEquals("Customer Name", createProfileResponse.getBody().getProfile().getCustomerName());
        }

        return createProfileResponse.getBody();
    }

    private ProfileResponse fetchProfile(String version, String bin, String customerRefNum) throws JsonProcessingException {
        ResponseEntity<ProfileResponse> fetchProfileResponse =
                restTemplate.getForEntity(baseURI + "/payment/profile/version/" + version  +
                        "/bin/" + bin + "/customerrefnum/" + customerRefNum, ProfileResponse.class);

        log.debug("fetchProfileResponse : {}", mapper.writeValueAsString(fetchProfileResponse.getBody()));

        return fetchProfileResponse.getBody();
    }

    private TransactionResponse fetchTransaction(String version, String bin, String terminalId, String txrefnum) throws JsonProcessingException {
        ResponseEntity<TransactionResponse> fetchTransactionResponse =
                restTemplate.getForEntity(baseURI + "/payment/transactionstatus/version/" + version  +
                        "/txrefnum/" + txrefnum + "/bin/" + bin + "/terminalid/" + terminalId, TransactionResponse.class);

        log.debug("fetchTransactionResponse : {}", mapper.writeValueAsString(fetchTransactionResponse.getBody()));

        return fetchTransactionResponse.getBody();
    }

    private ProfileResponse updateProfile(String customerRefNum) throws IOException {
        File file = new File(UPDATE_PROFILE);
        ProfileRequest request = mapper.readValue(file, ProfileRequest.class);
        request.getProfile().setCustomerRefNum(customerRefNum);
        request.getProfile().setCustomerName("update customer name");

        HttpEntity<ProfileRequest> entity = new HttpEntity<>(request);

        ResponseEntity<ProfileResponse> updateProfileResponse =
                restTemplate.exchange(baseURI + "/payment/profile", HttpMethod.PUT, entity, ProfileResponse.class);

        assertNotNull(updateProfileResponse.getBody());
        log.debug("updateProfileResponse : {}", mapper.writeValueAsString(updateProfileResponse.getBody()));
        assertNotNull(updateProfileResponse.getBody().getProfile().getCustomerRefNum());
        assertEquals("update customer name", updateProfileResponse.getBody().getProfile().getCustomerName());
        assertEquals(customerRefNum, updateProfileResponse.getBody().getProfile().getCustomerRefNum());

        return updateProfileResponse.getBody();
    }

    private ProfileResponse deleteProfile() throws IOException {
        File file = new File(DELETE_PROFILE);
        ProfileRequest request = mapper.readValue(file, ProfileRequest.class);
        HttpEntity<ProfileRequest> entity = new HttpEntity<>(request);

        ResponseEntity<ProfileResponse> deleteProfileResponse =
                restTemplate.exchange(baseURI + "/payment/profile", HttpMethod.DELETE, entity, ProfileResponse.class);

        assertNotNull(deleteProfileResponse.getBody());

        return deleteProfileResponse.getBody();
    }

}
