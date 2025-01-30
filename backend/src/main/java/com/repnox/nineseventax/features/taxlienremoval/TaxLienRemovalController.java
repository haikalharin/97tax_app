package com.repnox.nineseventax.features.taxlienremoval;

import com.repnox.nineseventax.features.admin.OrderSearchQuery;
import com.repnox.nineseventax.features.admin.OrderSearchStatuses;
import com.repnox.nineseventax.features.admin.OrderSpecs;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanController;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpSession;

@RestController()
@RequestMapping("/api/taxlienremoval")
public class TaxLienRemovalController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentPlanController.class);

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private TaxLienRemovalRepo taxLienRemovalRepo;
    
    @Autowired
    private MailjetSender mailjetSender;

    private void mapToOrderRecord(@RequestBody TaxLienRemovalDetails taxLienRemovalDetails, OrderRecord orderRecord) {
        orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE);
        orderRecord.setFirstName(taxLienRemovalDetails.getFirstname());
        orderRecord.setLastName(taxLienRemovalDetails.getLastname());
        orderRecord.setEmail(taxLienRemovalDetails.getEmail());
        orderRecord.setPhone(taxLienRemovalDetails.getPhone());
        orderRecord.setGotIrsFormQ(taxLienRemovalDetails.getGotIrsForm());
        orderRecord.setExistingTaxLienQ(taxLienRemovalDetails.getExistingTaxLien());
        orderRecord.setProduct("TaxLienRemoval");
        orderRecord.setIsCalifornia(false);
        orderRecord.setIsNewJersey(false);
        orderRecord.setIsGeorgia(false);
        orderRecord.setIsIllinois(false);
    }

    @PostMapping
    public @ResponseBody
    OrderRecord createTaxLienRemoval(HttpSession session, HttpServletRequest request, @RequestBody TaxLienRemovalDetails taxLienRemovalDetails) {
        OrderRecord orderRecord = new OrderRecord();
        mapToOrderRecord(taxLienRemovalDetails, orderRecord);

        orderRecordRepo.save(orderRecord);

        taxLienRemovalDetails.setId(null);
        taxLienRemovalDetails.setOrderNum(orderRecord.getOrderNum());

        taxLienRemovalRepo.save(taxLienRemovalDetails);

        session.setAttribute("currentOrder", orderRecord);

        Timer timer = new Timer();
        TimerTask delayedThreadStartTask = new TimerTask() {
            @Override
            public void run() {
            	OrderSearchQuery orderSearchQuery = new OrderSearchQuery();
            	orderSearchQuery.setEmail(orderRecord.getEmail());
            	orderSearchQuery.setCreatedAt(orderRecord.getCreatedDate().toString());
            	orderSearchQuery.setProduct("TaxLienRemoval");
            	
            	OrderSearchStatuses statuses = new OrderSearchStatuses();
            	statuses.setIncomplete(true);
            	orderSearchQuery.setStatus(statuses);
            	
            	Optional<OrderRecord> order = orderRecordRepo.findOne(OrderSpecs.orderSearchForIncompleteOrFailedEmail(orderSearchQuery));
            	if (!order.isPresent()) {
                    List<OrderRecord> incompleteOrders = orderRecordRepo.findByStatusAndEmailAndProductOrderByCreatedDate(orderSearchQuery.getEmail(),
                            OrderRecord.STATUS_INCOMPLETE, orderSearchQuery.getProduct());
                    OrderRecord firstOrder = incompleteOrders.get(0);
                    if(firstOrder.getOrderNum().equals(orderRecord.getOrderNum())) {
                        mailjetSender.sendTaxLienIncompleteEmail(orderRecord);
                    }
            	}
            }
        };

        timer.schedule(delayedThreadStartTask, 30 * 60 * 1000); //1 minute
        return orderRecord;
    }
}
