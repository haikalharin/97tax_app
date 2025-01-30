package com.repnox.nineseventax.features.casetracker;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.ein.EinRecordRepo;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/casetracker")
public class CaseTrackerController {

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;

    @Autowired
    private EinRecordRepo einRecordRepo;

    @GetMapping("/order/{orderNumber}/{phoneNumber}")
    @ResponseBody
    public CaseTrackerData getOrderData(@PathVariable String orderNumber, @PathVariable String phoneNumber) throws Exception {
        CaseTrackerData caseTrackerData = new CaseTrackerData();

        OrderRecord orderRecord = orderRecordRepo.findById(Long.parseLong(orderNumber)).orElse(null);
        PenaltyOrder penaltyRecord = penaltyRecordRepo.findById(Long.parseLong(orderNumber)).orElse(null);
        EinOrder einOrder = einRecordRepo.findById(Long.parseLong(orderNumber)).orElse(null);

        if(orderRecord == null && penaltyRecord == null && einOrder == null)
        {
            throw new Exception("Invalid order number");
        }

        if (orderRecord != null) {
            if (orderRecord.getPhone().equals(phoneNumber)
                    || orderRecord.getPhone().replaceAll("-", "").equals(phoneNumber)) {
                caseTrackerData.setStatus(orderRecord.getStatus());
                caseTrackerData.setCreatedDate(orderRecord.getCreatedDate());
                caseTrackerData.setCustomerFirstName(orderRecord.getFirstName());
                caseTrackerData.setCustomerEmail(orderRecord.getEmail());
                caseTrackerData.setTrackingNumber(orderRecord.getTrackingNumber());
                caseTrackerData.setIsGeorgia(orderRecord.getIsGeorgia());
                caseTrackerData.setIsIllinois(orderRecord.getIsIllinois());
                caseTrackerData.setIsNewJersey(orderRecord.getIsNewJersey());
                caseTrackerData.setIsCalifornia(orderRecord.getIsCalifornia());
                caseTrackerData.setIsEin(false);
            } else {
                throw new Exception("Invalid phone number");
            }
        } else if(penaltyRecord != null){
            if (penaltyRecord.getBillingPhone().equals(phoneNumber)
                    || penaltyRecord.getBillingPhone().replaceAll("-", "").equals(phoneNumber)) {
                caseTrackerData.setStatus(penaltyRecord.getStatus());
                caseTrackerData.setCreatedDate(penaltyRecord.getCreatedDate());
                caseTrackerData.setCustomerFirstName(penaltyRecord.getFirstName());
                caseTrackerData.setCustomerEmail(penaltyRecord.getEmail());
                caseTrackerData.setTrackingNumber(penaltyRecord.getTrackingNumber());
                caseTrackerData.setIsGeorgia(false);
                caseTrackerData.setIsIllinois(false);
                caseTrackerData.setIsNewJersey(false);
                caseTrackerData.setIsCalifornia(false);
                caseTrackerData.setIsEin(false);
            } else {
                throw new Exception("Invalid phone number");
            }
        } else if(einOrder != null) {
            if (einOrder.getPhone_number().equals(phoneNumber)) {
                caseTrackerData.setStatus(einOrder.getStatus());
                caseTrackerData.setCreatedDate(einOrder.getCreatedDate());
                caseTrackerData.setCustomerFirstName(einOrder.getFirst_name());
                caseTrackerData.setCustomerEmail(einOrder.getEmail());
                caseTrackerData.setTrackingNumber(einOrder.getEin());
                caseTrackerData.setIsGeorgia(false);
                caseTrackerData.setIsIllinois(false);
                caseTrackerData.setIsNewJersey(false);
                caseTrackerData.setIsCalifornia(false);
                caseTrackerData.setIsEin(true);
            } else {
                throw new Exception("Invalid phone number");
            }
        }

        return caseTrackerData;
    }

}
