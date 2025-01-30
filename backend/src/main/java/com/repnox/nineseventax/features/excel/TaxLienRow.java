package com.repnox.nineseventax.features.excel;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalDetails;

public class TaxLienRow {

    private OrderRecord orderRecord;

    private TaxLienRemovalDetails details;

    public OrderRecord getOrderRecord() {
        return orderRecord;
    }

    public void setOrderRecord(OrderRecord orderRecord) {
        this.orderRecord = orderRecord;
    }

    public TaxLienRemovalDetails getDetails() {
        return details;
    }

    public void setDetails(TaxLienRemovalDetails details) {
        this.details = details;
    }
}
