package com.repnox.nineseventax.features.ecommerce;

import com.repnox.nineseventax.features.models.ProductsList;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EcommerceService {


    public OrderConfirmation getOrderConfirmation(OrderRecord orderRecord, PaymentPlanDetails paymentPlanDetails) {
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setOrderId(orderRecord.getOrderNum().toString());
        orderConfirmation.setAmount(orderRecord.getAmount());
        orderConfirmation.setBillingAddress1(orderRecord.getBillingAddress1());
        orderConfirmation.setBillingAddress2(orderRecord.getBillingAddress2());
        orderConfirmation.setBillingCity(orderRecord.getBillingCity());
        orderConfirmation.setBillingState(orderRecord.getBillingState());
        orderConfirmation.setBillingZip(orderRecord.getBillingZip());
        orderConfirmation.setEmail(orderRecord.getEmail());
        orderConfirmation.setPhone(orderRecord.getPhone());
        orderConfirmation.setFirstName(orderRecord.getFirstName());
        orderConfirmation.setLastName(orderRecord.getLastName());
        orderConfirmation.setShippingAddress1(orderRecord.getShippingAddress1());
        orderConfirmation.setShippingAddress2(orderRecord.getShippingAddress2());
        orderConfirmation.setShippingCity(orderRecord.getShippingCity());
        orderConfirmation.setShippingState(orderRecord.getShippingState());
        orderConfirmation.setShippingZip(orderRecord.getShippingZip());
        orderConfirmation.setOrderDate(orderRecord.getSubmissionDate());
        orderConfirmation.setIsOwedFromBusiness(orderRecord.getIsOwedFromBusiness());
        orderConfirmation.setIsCalifornia(orderRecord.getIsCalifornia());
        orderConfirmation.setIsNewJersey(orderRecord.getIsNewJersey());
        orderConfirmation.setIsGeorgia(orderRecord.getIsGeorgia());
        orderConfirmation.setIsIllinois(orderRecord.getIsIllinois());
        orderConfirmation.setTotalDebt(orderRecord.getTotalDebt());
        orderConfirmation.setStatus(orderRecord.getStatus());
        orderConfirmation.setUpsellProduct(orderRecord.getUpsellProduct());
        orderConfirmation.setUpsellClicked(orderRecord.getUpsellClicked());
        orderConfirmation.setUpsellShown(orderRecord.getUpsellShown());

        if(paymentPlanDetails!=null) {
            orderConfirmation.setMonthlyPayment(paymentPlanDetails.getMonthlyPayment());
            orderConfirmation.setPaymentDayOfMonth(paymentPlanDetails.getPaymentDayOfMonth());
            orderConfirmation.setMarried(paymentPlanDetails.getMarried());
            orderConfirmation.setfilingJointly(paymentPlanDetails.getFilingJointly());
        }
        return orderConfirmation;
    }

    public OrderConfirmation getOrderConfirmationForPenaltyOrder (PenaltyOrder penaltyOrder, ProductsList penaltyWaiverProductItem) {
        Float penaltyWaiverFeeAmount = penaltyWaiverProductItem != null ? penaltyWaiverProductItem.getCurrentPrice()*100 : 0L;
        Double totalDebt = Double.valueOf(penaltyOrder.getPenaltyAmountWaived());
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setOrderId(penaltyOrder.getId().toString());
        orderConfirmation.setAmount(String.valueOf(penaltyWaiverFeeAmount));
        orderConfirmation.setBillingAddress1(penaltyOrder.getBillingAddress1());
        orderConfirmation.setBillingAddress2(penaltyOrder.getBillingAddress2());
        orderConfirmation.setBillingCity(penaltyOrder.getBillingCity());
        orderConfirmation.setBillingState(penaltyOrder.getBillingState());
        orderConfirmation.setBillingZip(penaltyOrder.getBillingPostalCode());
        orderConfirmation.setEmail(penaltyOrder.getEmail());
        orderConfirmation.setPhone(penaltyOrder.getBillingPhone());
        orderConfirmation.setFirstName(penaltyOrder.getFirstName());
        orderConfirmation.setLastName(penaltyOrder.getLastName());
        orderConfirmation.setShippingAddress1(penaltyOrder.getShippingAddress1());
        orderConfirmation.setShippingAddress2(penaltyOrder.getShippingAddress2());
        orderConfirmation.setShippingCity(penaltyOrder.getShippingCity());
        orderConfirmation.setShippingState(penaltyOrder.getShippingState());
        orderConfirmation.setShippingZip(penaltyOrder.getShippingPostalCode());
        orderConfirmation.setOrderDate(penaltyOrder.getStatusLastChanged());
        orderConfirmation.setStatus(penaltyOrder.getStatus());
        orderConfirmation.setTotalDebt(new BigDecimal(totalDebt));
        return orderConfirmation;
    }

    public String validateShippingAddr2(String argAddr2) {

        if(argAddr2 == "")	return "";

        String shippingAddr2 = argAddr2.replaceAll("#", "");

        Map<String, String> map = new HashMap<>();
        map.put("Apartment", "APT");
        map.put("Basement", "BSMT");
        map.put("Building", "BLDG");
        map.put("Department", "DEPT");
        map.put("Floor", "FL");
        map.put("Front", "FRNT");
        map.put("Hanger", "HNGR");
        map.put("Key", "KEY");
        map.put("Lobby", "LBBY");
        map.put("Lot", "LOT");
        map.put("Lower", "LOWR");
        map.put("Office", "OFC");
        map.put("Penthouse", "PH");
        map.put("Pier", "PIER");
        map.put("Rear", "REAR");
        map.put("Room", "RM");
        map.put("Side", "SIDE");
        map.put("Slip", "SLIP");
        map.put("Space", "SPC");
        map.put("Stop", "STOP");
        map.put("Suite", "STE");
        map.put("Trailer", "UNIT");
        map.put("Upper", "UPPR");

        String lowStrKey1 = "";
        String lowStrKey2 = "";
        String[] splitStr = argAddr2.split("\\s+");
        String strIdentifier = splitStr[0].toLowerCase();

        boolean findIdentifier = false;

        for (Map.Entry<String,String> entry : map.entrySet()) {
            lowStrKey1 = entry.getValue().toLowerCase();
            lowStrKey2 = entry.getKey().toLowerCase();
            System.out.println(lowStrKey1);

            if(strIdentifier.equals(lowStrKey1) || strIdentifier.equals(lowStrKey2))  {
                findIdentifier = true;
                break;
            }
        }
        if(findIdentifier == false ) {
            shippingAddr2 = "APT " + shippingAddr2;
        }
        if(argAddr2.toLowerCase().contains("home") || argAddr2.toLowerCase().contains("private home")|| argAddr2.toLowerCase().contains("pvh")) {
            shippingAddr2 = "";
        }
        return shippingAddr2;
    }
}
