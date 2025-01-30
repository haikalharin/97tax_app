package com.repnox.nineseventax.features.order.validation;

import com.repnox.nineseventax.features.admin.AdminSharedService;
import com.repnox.nineseventax.features.order.interfaces.VoidOrderService;
import com.repnox.nineseventax.features.processedorders.ProcessedOrders;
import com.repnox.nineseventax.features.processedorders.ProcessedOrdersRepo;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabels;
import com.repnox.nineseventax.features.shippinglabels.ShippingLabelsRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VoidOrderServiceImpl implements VoidOrderService {

    @Autowired
    private ProcessedOrdersRepo processedOrdersRepo;

    @Autowired
    private ShippingLabelsRepo shippingLabelsRepo;

    @Autowired
    private AdminSharedService adminSharedService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void voidUnsentProcessOrders() {
        try {
            List<ProcessedOrders> processedOrders = processedOrdersRepo.findUnsentProcessedOrders(false);
            for (ProcessedOrders pOrder : processedOrders) {
                ArrayList<ShippingLabels> shippingLabels = shippingLabelsRepo.findByProcessBatchId(pOrder.getId());
                for (ShippingLabels label : shippingLabels) {
                    adminSharedService.setShipingLabelAsVoid(label);
                }
            }

            if (CollectionUtils.isNotEmpty(processedOrders)) {
                processedOrdersRepo.deleteAll(processedOrders);
            }
        } catch (Exception e) {
            log.error("ERROR: Occured while process the unsent Processed Orders", e);
        }
    }
}
