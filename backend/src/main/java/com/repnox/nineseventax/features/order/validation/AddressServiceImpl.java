package com.repnox.nineseventax.features.order.validation;

import com.repnox.nineseventax.features.admin.AdminSharedService;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.models.AddressValidateResponse;
import com.repnox.nineseventax.features.order.interfaces.AddressService;
import com.repnox.nineseventax.features.order.repo.FulfillmentOrder;
import com.repnox.nineseventax.features.order.repo.FulfillmentRepo;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import com.repnox.nineseventax.features.utils.AdminUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Value("${shipengine.rate.limit}")
    private String shipEngineRateLimit;

    @Autowired
    private OrderRecordRepo orderRecordRepo;
    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;
    @Autowired
    private FulfillmentRepo fulfillmentRepo;
    @Autowired
    private AdminUtil adminUtil;
    @Autowired
    private AdminSharedService adminSharedService;
    @Autowired
    private MailjetSender mailjetSender;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void validateAddress() {
        List<FulfillmentOrder> allOrders = fulfillmentRepo.findOrderInProcessingAndNotShipengine();
        List<OrderRecord> ordersToSave = new ArrayList<>();
        List<PenaltyOrder> penaltyOrdersToSave = new ArrayList<>();

        try {
            if (CollectionUtils.isNotEmpty(allOrders)) {
                List<FulfillmentOrder>[] orderChunks = this.adminUtil.chunk(allOrders, 150);
                AddressValidateResponse[] responseAddressValidate;
                for (int i = 0; i < orderChunks.length; i++) {
                    responseAddressValidate = adminSharedService.postAddressValidateRequest(orderChunks[i]);
                    for (int j = 0; j < responseAddressValidate.length; j++) {
                        Long currentOrderId = Long.parseLong(responseAddressValidate[j].getOriginal_address().getName());
                        FulfillmentOrder order = orderChunks[i].stream()
                                .filter(o -> Long.valueOf(o.getId()).equals(currentOrderId))
                                .findAny()
                                .orElse(null);

                        if (order != null) {

                            String shippingAddr = order.getShippingAddress1();

                            if(FulfillmentOrder.ORDER_TYPE_PAYMENT_PLAN.equals(order.getOrderType())) {
                                Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderId);
                                if(optRecord.isPresent()) {
                                    OrderRecord existing = optRecord.get();

                                    if( shippingAddr != null &&
                                            shippingAddr.replaceAll(" ", "").replaceAll("\\.", "").toUpperCase().contains("POBOX")) {
                                        existing.setAddressStatus("verified");
                                    } else {
                                        existing.setAddressStatus(responseAddressValidate[j].getStatus());

                                        if (StringUtils.equalsAnyIgnoreCase(responseAddressValidate[j].getStatus(),
                                                "unverified", "error")) {
                                            existing.setStatus(OrderRecord.STATUS_ON_HOLD);
                                            existing.setNotes(buildNotes(existing.getNotes(), "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM"));
                                            log.error("Address Validation Response: 321 => ", responseAddressValidate[j]);
                                            mailjetSender.sendOnHoldOrderEmail(existing);
                                        }
                                    }

                                    ordersToSave.add(existing);
                                }
                            } else {
                                Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(currentOrderId);
                                if(optRecord.isPresent()) {
                                    PenaltyOrder existing = optRecord.get();

                                    if( shippingAddr != null && shippingAddr.startsWith("PO BOX")) {
                                        existing.setAddressStatus("verified");
                                    } else {
                                        existing.setAddressStatus(responseAddressValidate[j].getStatus());

                                        if (StringUtils.equalsAnyIgnoreCase(responseAddressValidate[j].getStatus(),
                                                "unverified", "error")) {
                                            existing.setStatus(OrderRecord.STATUS_ON_HOLD);
                                            existing.setNotes(buildNotes(existing.getNotes(), "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM"));
                                            log.error("Address Validation Response: 321 => ", responseAddressValidate[j]);
                                            mailjetSender.sendPenaltyOrderOnholdEmail(existing);
                                        }
                                    }

                                    penaltyOrdersToSave.add(existing);
                                }
                            }

                            /*
                            order.setAddressStatus(responseAddressValidate[j].getStatus());
                            if (StringUtils.equalsAnyIgnoreCase(responseAddressValidate[j].getStatus(),
                                    "unverified", "error")) {
                                order.setStatus(OrderRecord.STATUS_ON_HOLD);
                                order.setNotes(buildNotes(order.getNotes(), "SYSTEM", "ON HOLD DUE TO FULFILLMENT ERROR", "SYSTEM"));
                                log.error("Address Validation Response: 321 => ", responseAddressValidate[j]);
                                mailjetSender.sendOnHoldOrderEmail(order);
                            }
                             */
                        }
                    }
                }

                if(ordersToSave.size() > 0)
                    orderRecordRepo.saveAll(ordersToSave);

                if(penaltyOrdersToSave.size() > 0)
                    penaltyRecordRepo.saveAll(penaltyOrdersToSave);

                //Thread.sleep(70000);
            }
        } catch (Exception e) {
            log.error("ERROR: in On HOLDING: 331", e);
        }
    }

    private String buildNotes(String existingNotes, String type, String content, String user) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String strDate = formatter.format(date);
        String notes = type + "=*=" + content + "=*=" + user + "=*=" + strDate;

        if (StringUtils.isBlank(existingNotes)) {
            return notes;
        }

        return existingNotes + "###" + notes;
    }
}
