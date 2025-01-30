package com.repnox.nineseventax.features.penalty;

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
public class PenaltyOrderRepo {

    @Autowired
    private EntityManager entityManager;

    public List<Object[]> getAllResult(String id) {
        Query q = (Query) entityManager.createNativeQuery("SELECT * FROM penalty_order");
        List<Object[]> results = q.getResultList();
        return results;
    }

    private String configFilter(PenaltyOrderFilterRequest params) {
        String where = "WHERE TRUE AND ";
        PenaltyOrderFilterQuery filter = params.getQuery();
        String orderId = filter.getOrderId();
        String fname = filter.getFirstName();
        String lname = filter.getLastName();
        String email = filter.getEmail();
        OrderSearchStatuses status = filter.getStatus();

        if(orderId != null && !orderId.isEmpty()) {
            where += " id LIKE '%" + orderId + "%' AND ";
        }

        if(fname != null && !fname.isEmpty()) {
            where += " fname LIKE '%" + fname + "%' AND ";
        }

        if(lname != null && !lname.isEmpty()) {
            where += " lname LIKE '%" + lname + "%' AND ";
        }

        if(email != null && !email.isEmpty()) {
            where += " email LIKE '%" + email + "%' AND ";
        }

        if(!status.getAny()) {
            String statusCond = "";

            if (status.getIncomplete() == Boolean.TRUE) {
                statusCond += " status = '" + OrderRecord.STATUS_INCOMPLETE + "' OR ";
            }
            if (status.getCancelled() == Boolean.TRUE) {
                statusCond += " status = '" + OrderRecord.STATUS_CANCELLED + "' OR ";
            }
            if (status.getProcessing() == Boolean.TRUE) {
                statusCond += " status = '" + OrderRecord.STATUS_PROCESSING + "' OR ";
            }
            if (status.getOnHold() == Boolean.TRUE) {
                statusCond += " status = '" + OrderRecord.STATUS_ON_HOLD + "' OR ";
            }
            if (status.getCompleted() == Boolean.TRUE) {
                statusCond += " status = '" + OrderRecord.STATUS_COMPLETE + "' OR ";
            }
            if (status.getChargeback() == Boolean.TRUE) {
                statusCond += " status = '" + OrderRecord.STATUS_CHARGE_BACK + "' OR ";
            }
            if (status.getFailed() == Boolean.TRUE) {
                statusCond += " status = '" + OrderRecord.STATUS_FAILED + "' OR ";
            }

            if(!"".equals(statusCond)) {
                statusCond = "(" + statusCond.substring(0, statusCond.length() - 4) + ")";
                where += statusCond + " AND ";
            }
        }

        where = where.substring(0, where.length() - 4);

        return where;
    }

    public int getFilteredPenaltyOrdersCount(PenaltyOrderFilterRequest params) {
        String where = configFilter(params);

        String sql = "SELECT " +
                "   COUNT(id) AS total " +
                " FROM penalty_order " + where;

        Query q = (Query) entityManager.createNativeQuery(sql);
        Object result = q.getSingleResult();

        return Integer.valueOf(result.toString());
    }

    public List<Map<String, Object>> getFilteredPenaltyOrders(PenaltyOrderFilterRequest params) {

        String where = configFilter(params);
        int page = params.getPageNumber();
        int size = params.getPageSize();
        String order = params.getOrderBy();
        String dir = params.getOrderDirection();

        int start = page * size;

        String limit = " LIMIT " + start + ", " + size;
        String orderClause = " ORDER BY " + order + " " + dir;

        String sql = "SELECT " +
                "   id, fname, lname, status, email, status_last_changed " +
                " FROM penalty_order " + where + orderClause + limit;

        Query q = (Query) entityManager.createNativeQuery(sql);

        List<Object[]> results = q.getResultList();

        List<Map<String, Object>> res = new ArrayList<>();
        if(results.size() > 0) {
            Iterator<Object[]> it = results.iterator();
            while(it.hasNext()) {
                Object[] o = it.next();
                Map<String, Object> row = new HashMap<>();
                row.put("id", o[0]);
                row.put("fname", o[1]);
                row.put("lname", o[2]);
                row.put("status", o[3]);
                row.put("email", o[4]);
                row.put("status_last_changed", o[5]);

                res.add(row);
            }
        }

        return res;
    }

    public int getProcessingFailedCompleteEmails(String email) {
        String sql = " SELECT COUNT(id) AS total FROM penalty_order WHERE email = '" + email + "' AND `status` IN('"
                + OrderRecord.STATUS_FAILED + "','"
                + OrderRecord.STATUS_PROCESSING + "','"
                + OrderRecord.STATUS_COMPLETE + "')";

        Query q = (Query) entityManager.createNativeQuery(sql);

        Object result = q.getSingleResult();
        return Integer.valueOf(result.toString());
    }
}
