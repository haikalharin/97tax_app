package com.repnox.nineseventax.features.excel;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.oic.model.OicModel;

public class OfferInCompromiseRow {

    private OrderRecord orderRecord;

    private OicModel oicModel;

    public OrderRecord getOrderRecord() {
        return orderRecord;
    }

    public void setOrderRecord(OrderRecord orderRecord) {
        this.orderRecord = orderRecord;
    }

    public OicModel getOicModel() {
        return oicModel;
    }

    public void setOicModel(OicModel oicModel) {
        this.oicModel = oicModel;
    }
}
