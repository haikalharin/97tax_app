package com.repnox.nineseventax.features.admin.repo;

import com.google.gson.Gson;
import com.repnox.nineseventax.features.admin.AdminController;
import com.repnox.nineseventax.features.admin.CreatedAtFormat;
import com.repnox.nineseventax.features.admin.DateRange;
import com.repnox.nineseventax.features.admin.OrderSearchQuery;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class OrderRepo {
    @Autowired
    private EntityManager entityManager;

    public static Date today() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("US/Eastern")));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date today = cal.getTime();
        return today;
    }

    public static Date todayPlus(int days) {
        Date today = today();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private Date[] getDatesFromQuery(OrderSearchQuery query) {

        Boolean isSelectedDate = false;
        if (query.getCreatedAt() != "") {
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();

            Date start = null;
            Date end = null;
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String created = query.getCreatedAt();

            CreatedAtFormat fmt = new Gson().fromJson(created, CreatedAtFormat.class);

            if (fmt != null) {
                String st = fmt.getStartDate();
                String en = fmt.getEndDate();

                if (!st.isEmpty() && !en.isEmpty()) {
                    LocalDateTime stDate = LocalDateTime.parse(st, inputFormatter);
                    LocalDateTime enDate = LocalDateTime.parse(en, inputFormatter);

                    Date date1 = Date.from(stDate.atZone(ZoneId.systemDefault()).toInstant());
                    Date date2 = Date.from(enDate.atZone(ZoneId.systemDefault()).toInstant());
                    System.out.println(date1);
                    System.out.println(date2);

                    isSelectedDate = true;

                    return new Date[] {date1, date2};
                }
            }
        }
        if (query.getDateRange() != null && isSelectedDate == false) {
            if (query.getDateRange() == DateRange.TODAY) {
                return new Date[] {today()};
            } else if (query.getDateRange() == DateRange.YESTERDAY) {
                return new Date[] {todayPlus(-1)};
            } else if (query.getDateRange() == DateRange.SEVEN_DAYS) {
                return new Date[] {todayPlus(-7)};
            } else if (query.getDateRange() == DateRange.THIRTY_DAYS) {
                return new Date[] {todayPlus(-30)};
            }
        }

        return null;
    }

    private String configureStatusClause(OrderSearchQuery query) {
        if(query.getStatus() != null && !"".equals(query.getStatus())) {
            if (query.getStatus().getAny() != Boolean.TRUE) {
                String statusClause = "status IN(";
                String statusClauseInner = "";
                if (query.getStatus().getIncomplete() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_INCOMPLETE + "',";
                }
                if (query.getStatus().getCancelled() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_CANCELLED + "',";
                }
                if (query.getStatus().getProcessing() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_PROCESSING + "',";
                }
                if (query.getStatus().getOnHold() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_ON_HOLD + "',";
                }
                if (query.getStatus().getBotError() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_BOT_ERROR + "',";
                }
                if (query.getStatus().getCompleted() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_COMPLETE + "',";
                }
                if (query.getStatus().getChargeback() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_CHARGE_BACK + "',";
                }
                if (query.getStatus().getFailed() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_FAILED + "',";
                }
                if (query.getStatus().getAwaitingSignatureService() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_AWAITING_SIGNATURE_SERVICE + "',";
                }
                if (query.getStatus().getSigned() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_SIGNED + "',";
                }
                if (query.getStatus().getDeleted() == Boolean.TRUE) {
                    statusClauseInner += "'" + OrderRecord.STATUS_DELETED + "',";
                }

                if("".equals(statusClauseInner))
                    return null;

                statusClause = statusClause + statusClauseInner.substring(0, statusClauseInner.length() - 1) + ")";

                return statusClause;
            }
        }

        return null;
    }

    private String configureConditionForOrders(OrderSearchQuery query) {
        if(query == null)
            return null;

        String where = "";

        if ("PenaltyWaiver".equals(query.getProduct()) || "EIN".equals(query.getProduct())) {
            where = " WHERE false ";
            return where;
        } else {

            Boolean isOwedFromBusiness = query.getIsOwedFromBusiness();
            Boolean isCalifornia = query.getIsCalifornia();
            Boolean isNewJersey = query.getIsNewJersey();
            Boolean isGeorgia = query.getIsGeorgia();
            Boolean isMichigan = query.getIsMichigan();
            Boolean isIllinois = query.getIsIllinois();

            if (query.getOrderId() != null && StringUtils.isNotBlank(query.getOrderId().toString())) {
                where += " id LIKE '%" + query.getOrderId() + "%' AND ";
            }
            if (StringUtils.isNotBlank(query.getFirstName())) {
                where += " fname LIKE '%" + query.getFirstName() + "%' AND ";
            }
            if (StringUtils.isNotBlank(query.getLastName())) {
                where += " lname LIKE '%" + query.getLastName() + "%' AND ";
            }
            if (StringUtils.isNotBlank(query.getEmail())) {
                where += " email LIKE '%" + query.getEmail() + "%' AND ";
            }
            if (StringUtils.isNotBlank(query.getPhone())) {
                where += " phone LIKE '%" + query.getPhone() + "%' AND ";
            }
            if (isOwedFromBusiness != null && isOwedFromBusiness) {
                where += " is_owed_from_business=" + (isOwedFromBusiness ? "1" : "0") + " AND ";
            }
            if (StringUtils.isNotBlank(query.getProduct())) {
                where += " product LIKE '%" + query.getProduct() + "%' AND ";
            }

            boolean stateSpecific = (isCalifornia != null && isCalifornia) || (isGeorgia != null && isGeorgia) ||
                    (isIllinois != null && isIllinois) || (isNewJersey != null && isNewJersey) || (isMichigan != null && isMichigan);

            boolean onlyIRSPaymentPlan = "PaymentPlan".equals(query.getProduct()) && !stateSpecific;

            if (stateSpecific) {
                where += " (is_california=" + (isCalifornia != null && isCalifornia ? "1)" : "0 OR is_california IS NULL)") + " AND ";
                where += " (is_georgia=" + (isGeorgia != null && isGeorgia ? "1)" : "0 OR is_georgia IS NULL)") + " AND ";
                where += " (is_illinois=" + (isIllinois != null && isIllinois ? "1)" : "0 OR is_illinois IS NULL)") + " AND ";
                where += " (is_new_jersey=" + (isNewJersey != null && isNewJersey ? "1)" : "0 OR is_new_jersey IS NULL)") + " AND ";
                where += " (is_michigan=" + (isMichigan != null && isMichigan ? "1)" : "0 OR is_michigan IS NULL)") + " AND ";
            } else if (onlyIRSPaymentPlan) {
                where += " (is_california=0 OR is_california IS NULL) AND ";
                where += " (is_georgia=0 OR is_georgia IS NULL) AND ";
                where += " (is_illinois=0 OR is_illinois IS NULL) AND ";
                where += " (is_new_jersey=0 OR is_new_jersey IS NULL) AND ";
                where += " (is_michigan=0 OR is_michigan IS NULL) AND ";
            }

            Date[] dates = this.getDatesFromQuery(query);

            if (dates != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                if (dates.length == 2) {
                    where += " created_date >= '" + formatter.format(dates[0]) + "' AND created_date <= '" + formatter.format(dates[1]) + "' AND ";
                } else {
                    where += " created_date >= '" + formatter.format(dates[0]) + "' AND ";
                }
            }

            String statusClause = this.configureStatusClause(query);
            if (statusClause != null)
                where = (where.isEmpty() ? "" : where) + statusClause + " AND ";

            return where.isEmpty() ? "" : " WHERE " + where.substring(0, where.length() - 4);
        }
    }

    private String configureConditionForPenaltyOrders(OrderSearchQuery query) {
        if(query == null)
            return null;

        if (!"".equals(query.getProduct()) && !"PenaltyWaiver".equals(query.getProduct())) {
            return " WHERE false ";
        }

        String where = "";

        if(query.getOrderId() != null && StringUtils.isNotBlank(query.getOrderId().toString())) {
            where += " id LIKE '%" + query.getOrderId() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getFirstName())) {
            where += " fname LIKE '%" + query.getFirstName() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getLastName())) {
            where += " lname LIKE '%" + query.getLastName() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getEmail())) {
            where += " email LIKE '%" + query.getEmail() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getPhone())) {
            where += " billing_phone LIKE '%" + query.getPhone() + "%' AND ";
        }

        Date[] dates = this.getDatesFromQuery(query);

        if(dates != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            if(dates.length == 2) {
                where += " created_date >= '" + formatter.format(dates[0]) + "' AND created_date <= '" + formatter.format(dates[1]) + "' AND ";
            } else {
                where += " created_date >= '" + formatter.format(dates[0]) + "' AND ";
            }
        }

        String statusClause = this.configureStatusClause(query);
        if(statusClause != null && !"".equals(statusClause))
            where = (where.isEmpty() ? "" : where) + statusClause + " AND ";

        return where.isEmpty() ? "" : " WHERE " + where.substring(0, where.length() - 4);
    }

    private String configureConditionForEinOrders(OrderSearchQuery query) {
        if(query == null)
            return null;

        if (!"".equals(query.getProduct()) && !"EIN".equals(query.getProduct())) {
            return " WHERE false ";
        }

        String where = "";

        if(query.getOrderId() != null && StringUtils.isNotBlank(query.getOrderId().toString())) {
            where += " id LIKE '%" + query.getOrderId() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getFirstName())) {
            where += " first_name LIKE '%" + query.getFirstName() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getLastName())) {
            where += " last_name LIKE '%" + query.getLastName() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getEmail())) {
            where += " email LIKE '%" + query.getEmail() + "%' AND ";
        }
        if (StringUtils.isNotBlank(query.getPhone())) {
            where += " phone_number LIKE '%" + query.getPhone() + "%' AND ";
        }

        Date[] dates = this.getDatesFromQuery(query);

        if(dates != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            if(dates.length == 2) {
                where += " created_date >= '" + formatter.format(dates[0]) + "' AND created_date <= '" + formatter.format(dates[1]) + "' AND ";
            } else {
                where += " created_date >= '" + formatter.format(dates[0]) + "' AND ";
            }
        }

        String statusClause = this.configureStatusClause(query);
        if(statusClause != null && !"".equals(statusClause))
            where = (where.isEmpty() ? "" : where) + statusClause + " AND ";

        return where.isEmpty() ? "" : " WHERE " + where.substring(0, where.length() - 4);
    }

    @Transactional
    @Modifying
    public void markOrdersForDeletion(List<Long> ids) {
        String jpqlOrders = "UPDATE OrderRecord r SET r.status='Deleted' WHERE r.id IN :ids";
        String jpqlPenaltyOrders = "UPDATE PenaltyOrder r SET r.status='Deleted' WHERE r.id IN :ids";
        String jpqlEinOrders = "UPDATE EinOrder r SET r.status='Deleted' WHERE r.id IN :ids";

        // Table's IDs (OrderRecord, PenaltyOrder, EinOrder) are generated with a same generator, double-check when extending this method
        entityManager.createQuery(jpqlOrders).setParameter("ids", ids).executeUpdate();
        entityManager.createQuery(jpqlPenaltyOrders).setParameter("ids", ids).executeUpdate();
        entityManager.createQuery(jpqlEinOrders).setParameter("ids", ids).executeUpdate();
    }

    public int getOrdersCount(AdminController.OrderSearchPaginationRequest request) {
        String whereOrder = this.configureConditionForOrders(request.getQuery());
        String wherePenalty = this.configureConditionForPenaltyOrders(request.getQuery());
        String whereEin = this.configureConditionForEinOrders(request.getQuery());
        String sql =
                "SELECT " +
                        " COUNT(*) total " +
                        " FROM (" +
                        "   SELECT id FROM orders " + whereOrder +
                        "   UNION( SELECT id FROM penalty_order " + wherePenalty + ")" +
                        "   UNION( SELECT id FROM ein_orders " + whereEin + ")" +
                        " ) t0";

        Query q = (Query) entityManager.createNativeQuery(sql);
        Object result = q.getSingleResult();

        return Integer.valueOf(result.toString());
    }

    public List<Map<String, Object>> getOrdersList(AdminController.OrderSearchPaginationRequest request) {
        String whereOrder = this.configureConditionForOrders(request.getQuery());
        String wherePenalty = this.configureConditionForPenaltyOrders(request.getQuery());
        String whereEin = this.configureConditionForEinOrders(request.getQuery());

        String sql =
                "SELECT " +
                        " * " +
                        " FROM (" +
                        "   SELECT " +
                        "           id, " +
                        "           fname, " +
                        "           lname, " +
                        "           email, " +
                        "           phone, " +
                        "           product, " +
                        "           created_date, " +
                        "           'ORDER' AS order_type, " +
                        "           status, " +
                        "           is_california," +
                        "           is_new_jersey," +
                        "           is_georgia," +
                        "           is_illinois," +
                        "           is_michigan" +
                        "       FROM orders " + whereOrder +
                        "   UNION( " +
                        "   SELECT " +
                        "           id, " +
                        "           fname, " +
                        "           lname, " +
                        "           email, " +
                        "           billing_phone AS phone, " +
                        "           'Penalty Waiver' AS product, " +
                        "           created_date," +
                        "           'PENALTY' AS order_type, " +
                        "           status, " +
                        "           0 AS is_california," +
                        "           0 AS is_new_jersey," +
                        "           0 AS is_georgia," +
                        "           0 AS is_illinois," +
                        "           0 AS is_michigan" +
                        "       FROM penalty_order " + wherePenalty + ")" +
                        "   UNION( " +
                        "   SELECT " +
                        "           id, " +
                        "           first_name AS fname, " +
                        "           last_name AS lname, " +
                        "           email, " +
                        "           phone_number AS phone, " +
                        "           'EIN' AS product, " +
                        "           created_date," +
                        "           'EIN' AS order_type, " +
                        "           status, " +
                        "           0 AS is_california," +
                        "           0 AS is_new_jersey," +
                        "           0 AS is_georgia," +
                        "           0 AS is_illinois," +
                        "           0 AS is_michigan" +
                        "       FROM ein_orders " + whereEin + ")" +
                        " ) t0";

        String orderClause = " ORDER BY created_date DESC ";
        String limitClause = " LIMIT " + request.getPageNumber() * request.getPageSize() + ", " + request.getPageSize();
        sql += orderClause + " " + limitClause;

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
                row.put("email", o[3]);
                row.put("phone", o[4]);
                row.put("product", o[5]);
                row.put("created_date", o[6]);
                row.put("order_type", o[7]);
                row.put("status", o[8]);
                row.put("is_california", convertByteArrayToInt(o[9]));
                row.put("is_new_jersey", convertByteArrayToInt(o[10]));
                row.put("is_georgia", convertByteArrayToInt(o[11]));
                row.put("is_illinois", convertByteArrayToInt(o[12]));
                row.put("is_michigan", convertByteArrayToInt(o[13]));

                res.add(row);
            }
        }

        return res;
    }

    private int convertByteArrayToInt(Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof byte[]) {
            byte[] bytes = (byte[]) obj;
            return ByteBuffer.wrap(bytes).getInt();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        } else {
            throw new IllegalArgumentException("Unexpected type: " + obj.getClass());
        }
    }
}
