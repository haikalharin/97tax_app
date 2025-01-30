package com.repnox.nineseventax.features.payment;

import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCapturePaymentService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreatePaymentService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalCreateProfileService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalDeleteProfileService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalFetchProfileService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalFetchTransactionService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalInquiryService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalRefundService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalReversalService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalSettleService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalUpdateProfileService;
import com.repnox.nineseventax.features.payment.request.CapturePaymentRequest;
import com.repnox.nineseventax.features.payment.request.CreatePaymentRequest;
import com.repnox.nineseventax.features.payment.request.InquiryRequest;
import com.repnox.nineseventax.features.payment.request.ProfileRequest;
import com.repnox.nineseventax.features.payment.request.RefundRequest;
import com.repnox.nineseventax.features.payment.request.ReversalRequest;
import com.repnox.nineseventax.features.payment.request.SettleRequest;
import com.repnox.nineseventax.features.payment.response.CapturePaymentResponse;
import com.repnox.nineseventax.features.payment.response.CreatePaymentResponse;
import com.repnox.nineseventax.features.payment.response.InquiryResponse;
import com.repnox.nineseventax.features.payment.response.ProfileResponse;
import com.repnox.nineseventax.features.payment.response.RefundResponse;
import com.repnox.nineseventax.features.payment.response.ReversalResponse;
import com.repnox.nineseventax.features.payment.response.SettleResponse;
import com.repnox.nineseventax.features.payment.response.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class OrbitalController {

    private static final Logger LOG = LoggerFactory.getLogger(OrbitalController.class);

    @Autowired
    private OrbitalRefundService refundService;
    @Autowired
    private OrbitalCreatePaymentService createPaymentService;
    @Autowired
    private OrbitalCapturePaymentService capturePaymentService;
    @Autowired
    private OrbitalCreateProfileService createProfileService;
    @Autowired
    private OrbitalUpdateProfileService updateProfileService;
    @Autowired
    private OrbitalDeleteProfileService deleteProfileService;
    @Autowired
    private OrbitalFetchProfileService fetchProfileService;
    @Autowired
    private OrbitalFetchTransactionService fetchTransactionService;
    @Autowired
    private OrbitalSettleService settleService;
    @Autowired
    private OrbitalInquiryService orbitalInquiryService;
    @Autowired
    private OrbitalReversalService reversalService;

    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) throws Exception {
        return ResponseEntity.ok(createPaymentService.createPayment(request));
    }

    @PostMapping("/capture")
    public ResponseEntity<CapturePaymentResponse> capturePayment(@RequestBody CapturePaymentRequest request) throws Exception {
        return ResponseEntity.ok(capturePaymentService.capture(request));
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> refund(@RequestBody RefundRequest request) throws Exception {
        return ResponseEntity.ok(refundService.refundPayment(request));
    }

    @PostMapping("/settle")
    public ResponseEntity<SettleResponse> settle(@RequestBody SettleRequest request) throws Exception {
        return ResponseEntity.ok(settleService.settle(request));
    }

    @PostMapping("/reversal")
    public ResponseEntity<ReversalResponse> settle(@RequestBody ReversalRequest request) throws Exception {
        return ResponseEntity.ok(reversalService.reverse(request));
    }

    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> createProfile(@RequestBody ProfileRequest request) throws Exception {
        return ResponseEntity.ok(createProfileService.create(request));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody ProfileRequest request) throws Exception {
        return ResponseEntity.ok(updateProfileService.update(request));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ProfileResponse> deleteProfile(@RequestBody ProfileRequest request) throws Exception {
        return ResponseEntity.ok(deleteProfileService.delete(request));
    }

    @GetMapping("/profile/version/{version}/bin/{bin}/customerrefnum/{customerRefNum}")
    public ResponseEntity<ProfileResponse> fetchProfile(@PathVariable String version, @PathVariable String bin,
                                                        @PathVariable String customerRefNum) throws Exception {
        return ResponseEntity.ok(fetchProfileService.fetch(version, bin, customerRefNum));
    }

    @GetMapping("/transactionstatus/version/{version}/txrefnum/{txrefnum}/bin/{bin}/terminalid/{terminalId}")
    public ResponseEntity<TransactionResponse> fetchProfile(@PathVariable String version,
                                                            @PathVariable String txrefnum,
                                                            @PathVariable String bin,
                                                            @PathVariable String terminalId) throws Exception {
        return ResponseEntity.ok(fetchTransactionService.fetch(version, bin, terminalId, txrefnum));
    }

    @PutMapping("/inquiry")
    public ResponseEntity<InquiryResponse> inquiry(@RequestBody InquiryRequest request) throws Exception {
        return ResponseEntity.ok(orbitalInquiryService.inquiry(request));
    }

}
