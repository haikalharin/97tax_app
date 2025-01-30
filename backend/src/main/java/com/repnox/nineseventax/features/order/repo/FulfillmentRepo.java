package com.repnox.nineseventax.features.order.repo;

import com.repnox.nineseventax.features.admin.OrderSearchStatuses;
import com.repnox.nineseventax.features.admin.request.PenaltyOrderFilterQuery;
import com.repnox.nineseventax.features.admin.request.PenaltyOrderFilterRequest;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Repository
public class FulfillmentRepo {

    @Autowired
    private EntityManager entityManager;

    public List<FulfillmentOrder> findOrderInProcessingAndNotShipengine() {

        String sql =
                "SELECT" +
                        "   id," +
                        "   fname," +
                        "   lname," +
                        "   shipaddr1," +
                        "   shipaddr2," +
                        "   shipcity," +
                        "   shipstate," +
                        "   shipzip," +
                        "   phone," +
                        "   processing_speed, " +
                        "   '" + FulfillmentOrder.ORDER_TYPE_PAYMENT_PLAN + "' AS order_type," +
                        "   is_california," +
                        "   is_new_jersey," +
                        "   is_georgia," +
                        "   is_illinois," +
                        "   authorize_transaction_id " +
                "FROM" +
                "   orders " +
                "WHERE" +
                "   STATUS = 'Processing' " +
                "       AND ( shipengine_status_code IS NULL OR shipengine_status_code = '' OR shipengine_status_code <> 'in_transit' ) " +
                "UNION(" +
                "SELECT" +
                        "   id," +
                        "   fname," +
                        "   lname," +
                        "   shipping_address1 AS shipaddr1," +
                        "   shipping_address2 AS shipping_address2," +
                        "   shipping_city AS shipcity," +
                        "   shipping_state AS shipstate," +
                        "   shipping_postal_code AS shipzip," +
                        "   billing_phone AS phone," +
                        "   processing_speed, " +
                        "   '" + FulfillmentOrder.ORDER_TYPE_PENATY_WAIVER + "' AS order_type, " +
                        "   0 AS is_california," +
                        "   0 AS is_new_jersey," +
                        "   0 AS is_georgia," +
                        "   0 AS is_illinois," +
                        "   authorize_transaction_id " +
                "FROM" +
                "   penalty_order " +
                "WHERE" +
                "   STATUS = 'Processing' " +
                "       AND ( shipengine_status_code IS NULL OR shipengine_status_code = '' OR shipengine_status_code <> 'in_transit' ) " +
                ")";

        Query q = (Query) entityManager.createNativeQuery(sql);

        List<Object[]> results = q.getResultList();

        List<FulfillmentOrder> res = new ArrayList<>();
        if(results.size() > 0) {
            Iterator<Object[]> it = results.iterator();
            while(it.hasNext()) {
                Object[] o = it.next();

                FulfillmentOrder row = new FulfillmentOrder();
                row.setId(o[0] == null ? "" : o[0].toString());
                row.setFirstName(o[1] == null ? "" : o[1].toString());
                row.setLastName(o[2] == null ? "" : o[2].toString());
                row.setShippingAddress1(o[3] == null ? "" : o[3].toString());
                row.setShippingAddress2(o[4] == null ? "" : o[4].toString());
                row.setShippingCity(o[5] == null ? "" : o[5].toString());
                row.setShippingState(o[6] == null ? "" : o[6].toString());
                row.setShippingZip(o[7] == null ? "" : o[7].toString());
                row.setPhone(o[8] == null ? "" : o[8].toString());
                row.setProcessingSpeed(o[9] == null ? "" : o[9].toString());
                row.setOrderType(o[10] == null ? "" : o[10].toString());

                row.setIsCalifornia("1".equals(o[11] == null ? "0" : o[11].toString()));
                row.setIsNewJersey("1".equals(o[12] == null ? "0" : o[12].toString()));
                row.setIsGeorgia("1".equals(o[13] == null ? "0" : o[13].toString()));
                row.setIsIllinois("1".equals(o[14] == null ? "0" : o[14].toString()));

                row.setAuthorizeTransactionId(o[15] == null ? null : o[15].toString());
                res.add(row);
            }
        }

        return res;
    }

    public List<FulfillmentOrder> findStaleOnHoldOrders() {
        String sql =
                "SELECT" +
                        "   id," +
                        "   fname," +
                        "   lname," +
                        "   shipaddr1," +
                        "   shipaddr2," +
                        "   shipcity," +
                        "   shipstate," +
                        "   shipzip," +
                        "   phone," +
                        "   processing_speed, " +
                        "   '" + FulfillmentOrder.ORDER_TYPE_PAYMENT_PLAN + "' AS order_type," +
                        "   is_california," +
                        "   is_new_jersey," +
                        "   is_georgia," +
                        "   is_illinois," +
                        "   authorize_transaction_id" +
                        "FROM" +
                        "   orders " +
                        "WHERE" +
                        "   `status` = 'On Hold' AND datediff(now(), r.status_last_changed) >= 2 " +
                        "UNION(" +
                        "SELECT" +
                        "   id," +
                        "   fname," +
                        "   lname," +
                        "   shipping_address1 AS shipaddr1," +
                        "   shipping_address2 AS shipping_address2," +
                        "   shipping_city AS shipcity," +
                        "   shipping_state AS shipstate," +
                        "   shipping_postal_code AS shipzip," +
                        "   billing_phone AS phone," +
                        "   processing_speed, " +
                        "   '" + FulfillmentOrder.ORDER_TYPE_PENATY_WAIVER + "' AS order_type, " +
                        "   0 AS is_california," +
                        "   0 AS is_new_jersey," +
                        "   0 AS is_georgia," +
                        "   is_illinois," +
                        "   authorize_transaction_id" +
                        "FROM" +
                        "   penalty_order " +
                        "WHERE" +
                        "   `status` = 'On Hold' AND datediff(now(), r.status_last_changed) >= 2 " +
                        ")";

        Query q = (Query) entityManager.createNativeQuery(sql);

        List<Object[]> results = q.getResultList();

        List<FulfillmentOrder> res = new ArrayList<>();
        if(results.size() > 0) {
            Iterator<Object[]> it = results.iterator();
            while(it.hasNext()) {
                Object[] o = it.next();

                FulfillmentOrder row = new FulfillmentOrder();
                row.setId(o[0] == null ? "" : o[0].toString());
                row.setFirstName(o[1] == null ? "" : o[1].toString());
                row.setLastName(o[2] == null ? "" : o[2].toString());
                row.setShippingAddress1(o[3] == null ? "" : o[3].toString());
                row.setShippingAddress2(o[4] == null ? "" : o[4].toString());
                row.setShippingCity(o[5] == null ? "" : o[5].toString());
                row.setShippingState(o[6] == null ? "" : o[6].toString());
                row.setShippingZip(o[7] == null ? "" : o[7].toString());
                row.setPhone(o[8] == null ? "" : o[8].toString());
                row.setProcessingSpeed(o[9] == null ? "" : o[9].toString());
                row.setOrderType(o[10] == null ? "" : o[10].toString());

                row.setIsCalifornia("1".equals(o[11].toString()));
                row.setIsNewJersey("1".equals(o[12].toString()));
                row.setIsGeorgia("1".equals(o[13].toString()));
                row.setIsIllinois("1".equals(o[14].toString()));

                row.setAuthorizeTransactionId(o[15] == null ? null : o[15].toString());

                res.add(row);
            }
        }

        return res;
    }
}
