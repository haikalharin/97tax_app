package com.repnox.nineseventax.features.ecommerce;

import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class OrderService {

    final OrderRecordRepo orderRecordRepo;
    final PaymentPlanRepo paymentPlanRepo;

    public OrderService(OrderRecordRepo orderRecordRepo, PaymentPlanRepo paymentPlanRepo) {
        this.orderRecordRepo = orderRecordRepo;
        this.paymentPlanRepo = paymentPlanRepo;
    }


//    public AuthorizeResult createAndPostOrder() {
//
//        return null;
//
//    }

    public List<OrderRecord> findPossibleDuplicate(OrderInfo orderInfo) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -120);

        List<OrderRecord> potentialDupes;

        if (orderInfo.getIsCalifornia() != null && orderInfo.getIsCalifornia()) {
            potentialDupes = orderRecordRepo.findPotentialDuplicateForCalifornia(orderInfo.getEmail(),
                    Arrays.asList(OrderRecord.STATUS_PROCESSING, OrderRecord.STATUS_COMPLETE,OrderRecord.STATUS_ON_HOLD ), cal.getTime(),
                    orderInfo.getProduct());
        } else if (orderInfo.getIsNewJersey() != null && orderInfo.getIsNewJersey()) {
            potentialDupes = orderRecordRepo.findPotentialDuplicateForNewJersey(orderInfo.getEmail(),
                    Arrays.asList(OrderRecord.STATUS_PROCESSING, OrderRecord.STATUS_COMPLETE,OrderRecord.STATUS_ON_HOLD ), cal.getTime(),
                    orderInfo.getProduct());
        } else if (orderInfo.getIsGeorgia() != null && orderInfo.getIsGeorgia()) {
            potentialDupes = orderRecordRepo.findPotentialDuplicateForGeorgia(orderInfo.getEmail(),
                    Arrays.asList(OrderRecord.STATUS_PROCESSING, OrderRecord.STATUS_COMPLETE,OrderRecord.STATUS_ON_HOLD ), cal.getTime(),
                    orderInfo.getProduct());
        } else if (orderInfo.getIsIllinois() != null && orderInfo.getIsIllinois()) {
            potentialDupes = orderRecordRepo.findPotentialDuplicateForIllinois(orderInfo.getEmail(),
                    Arrays.asList(OrderRecord.STATUS_PROCESSING, OrderRecord.STATUS_COMPLETE,OrderRecord.STATUS_ON_HOLD ), cal.getTime(),
                    orderInfo.getProduct());
        } else if (orderInfo.getIsMichigan() != null && orderInfo.getIsMichigan()) {
            potentialDupes = orderRecordRepo.findPotentialDuplicateForMichigan(orderInfo.getEmail(),
                    Arrays.asList(OrderRecord.STATUS_PROCESSING, OrderRecord.STATUS_COMPLETE,OrderRecord.STATUS_ON_HOLD ), cal.getTime(),
                    orderInfo.getProduct());
        } else {
            potentialDupes = orderRecordRepo.findPotentialDuplicateForIRS(orderInfo.getEmail(),
                    Arrays.asList(OrderRecord.STATUS_PROCESSING, OrderRecord.STATUS_COMPLETE,OrderRecord.STATUS_ON_HOLD ), cal.getTime(),
                    orderInfo.getProduct());
            if(potentialDupes!= null && potentialDupes.size() == 1 && potentialDupes.get(0).getOrderNum() != null){
                PaymentPlanDetails paymentPlanDetails = paymentPlanRepo.findByOrderNum(potentialDupes.get(0).getOrderNum());
                if(paymentPlanDetails != null && paymentPlanDetails.getMarried() != null &&  paymentPlanDetails.getFilingJointly() != null){
                    potentialDupes = paymentPlanDetails.getMarried().equals(true) && paymentPlanDetails.getFilingJointly().equals("No") ?
                            Collections.<OrderRecord>emptyList() : potentialDupes;
                }
            }
        }
        return potentialDupes;
    }


}
