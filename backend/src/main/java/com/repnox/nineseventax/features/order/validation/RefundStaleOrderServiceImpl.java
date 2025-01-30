package com.repnox.nineseventax.features.order.validation;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalRefundService;
import com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces.OrbitalReversalService;
import com.repnox.nineseventax.features.order.interfaces.RefundStaleOrderService;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.penalty.PenaltyOrderRepo;
import com.repnox.nineseventax.features.penalty.PenaltyRecordRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RefundStaleOrderServiceImpl implements RefundStaleOrderService {

    @Autowired
    private OrderRecordRepo orderRecordRepo;
    @Autowired
    private OrbitalRefundService refundService;

    @Autowired
    private OrbitalReversalService reversalService;

    @Autowired
    private PenaltyRecordRepo penaltyRecordRepo;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refund() throws Exception {
        List<OrderRecord> staleOnHoldOrders = orderRecordRepo.findStaleOnHoldOrders();
        if (CollectionUtils.isNotEmpty(staleOnHoldOrders)) {
            for (int i = 0; i < staleOnHoldOrders.size(); i++) {
                Long currentOrderNum = staleOnHoldOrders.get(i).getOrderNum();
                Optional<OrderRecord> optRecord = orderRecordRepo.findById(currentOrderNum);
                if (optRecord.isPresent()) {
                    OrderRecord existing = optRecord.get();
                    refundService.refundPayment(existing.getAuthorizeTransactionId(), existing.getAmount(), OrderRecord.STATUS_CANCELLED, true);
                }
            }
        }

        // refund for penalty orders
        List<PenaltyOrder> staleOnHoldPenaltyOrders = penaltyRecordRepo.findStaleOnHoldOrders();
        if (CollectionUtils.isNotEmpty(staleOnHoldPenaltyOrders)) {
            for (int i = 0; i < staleOnHoldPenaltyOrders.size(); i++) {
                Long currentOrderNum = staleOnHoldPenaltyOrders.get(i).getId();
                Optional<PenaltyOrder> optRecord = penaltyRecordRepo.findById(currentOrderNum);
                if (optRecord.isPresent()) {
                    PenaltyOrder existing = optRecord.get();
                    refundService.refundPayment(existing.getAuthorizeTransactionId(), existing.getAmount(), OrderRecord.STATUS_CANCELLED, true);
                }
            }
        }
    }
}
