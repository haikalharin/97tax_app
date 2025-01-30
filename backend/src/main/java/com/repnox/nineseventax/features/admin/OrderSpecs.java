package com.repnox.nineseventax.features.admin;

import com.google.gson.Gson;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecord_;
import com.repnox.nineseventax.features.utils.TaxConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class OrderSpecs {

  private static final Logger LOG = LoggerFactory.getLogger(OrderSpecs.class);

  public static Specification<OrderRecord> orderSearch(OrderSearchQuery query) {
    return new Specification<OrderRecord>() {
      /**
       *
       */
      private static final long serialVersionUID = 7315361595701333908L;

      @Override
      public Predicate toPredicate(Root<OrderRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (query != null) {
          Boolean isOwedFromBusiness = query.getIsOwedFromBusiness();
          Boolean isCalifornia = query.getIsCalifornia();
          Boolean isNewJersey = query.getIsNewJersey();
          Boolean isGeorgia = query.getIsGeorgia();
          Boolean isIllinois=query.getIsIllinois();

          if (StringUtils.isNotBlank(query.getFirstName())) {
            predicates.add(cb.like(root.get(OrderRecord_.firstName), "%" + query.getFirstName() + "%"));
          }
          if (StringUtils.isNotBlank(query.getLastName())) {
            predicates.add(cb.like(root.get(OrderRecord_.lastName), "%" + query.getLastName() + "%"));
          }
          if (StringUtils.isNotBlank(query.getEmail())) {
            predicates.add(cb.like(root.get(OrderRecord_.email), "%" + query.getEmail() + "%"));
          }
          if (StringUtils.isNotBlank(query.getPhone())) {
            predicates.add(cb.like(root.get(OrderRecord_.phone), "%" + query.getPhone() + "%"));
          }
          if (isOwedFromBusiness != null && isOwedFromBusiness) {
            predicates.add(cb.equal(root.get(OrderRecord_.isOwedFromBusiness), isOwedFromBusiness));
          }
          if (StringUtils.isNotBlank(query.getProduct())) {
            predicates.add(cb.like(root.get(OrderRecord_.product), "%" + query.getProduct() + "%"));
            if (isCalifornia != null && isCalifornia) {
              predicates.add(cb.isTrue(root.get(OrderRecord_.isCalifornia)));
            } else if (isGeorgia != null && isGeorgia) {
              predicates.add(cb.isTrue(root.get(OrderRecord_.isGeorgia)));
            }else if (isIllinois != null && isIllinois) {
              predicates.add(cb.isTrue(root.get(OrderRecord_.isIllinois)));
            } else if (isNewJersey != null && isNewJersey) {
              predicates.add(cb.isTrue(root.get(OrderRecord_.isNewJersey)));
            } else {
              predicates.add(cb.notEqual(root.get(OrderRecord_.isCalifornia), true));
              predicates.add(cb.notEqual(root.get(OrderRecord_.isGeorgia), true));
              predicates.add(cb.notEqual(root.get(OrderRecord_.isIllinois), true));
              predicates.add(cb.notEqual(root.get(OrderRecord_.isNewJersey), true));
            }
          }

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
                /*
                LocalDate startDate = LocalDate.parse(st, inputFormatter);
                start = java.sql.Date.valueOf(startDate);
                LocalDate endDate = LocalDate.parse(en, inputFormatter);
                end = java.sql.Date.valueOf(endDate);

                calStart.setTime(start);
                calEnd.setTime(end);

                Calendar createdStartDateCal = Calendar.getInstance();
                createdStartDateCal.set(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH),
                    calStart.get(Calendar.DATE), 0, 0, 1);
                Calendar createdEndDateCal = Calendar.getInstance();
                createdEndDateCal.set(calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DATE),
                    23, 59, 59);

                Date createdStartDate = createdStartDateCal.getTime();
                Date createdEndDate = createdEndDateCal.getTime();
                */
                // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                // formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                // try {
                //   createdStartDate = formatter.parse(st);
                //   createdEndDate = formatter.parse(en);
                // } catch (ParseException e) {
                //   e.printStackTrace();
                // }

                LocalDateTime stDate = LocalDateTime.parse(st, inputFormatter);
                LocalDateTime enDate = LocalDateTime.parse(en, inputFormatter);

                Date date1 = Date.from(stDate.atZone(ZoneId.systemDefault()).toInstant());
                Date date2 = Date.from(enDate.atZone(ZoneId.systemDefault()).toInstant());
                System.out.println(date1);
                System.out.println(date2);
                predicates.add(cb.between(root.get(OrderRecord_.createdDate), date1, date2));
                isSelectedDate = true;
              }
            }
          }
          if (query.getDateRange() != null && isSelectedDate == false) {
            if (query.getDateRange() == DateRange.TODAY) {
              predicates.add(cb.greaterThanOrEqualTo(root.get(OrderRecord_.createdDate), today()));
            } else if (query.getDateRange() == DateRange.YESTERDAY) {
              predicates.add(cb.greaterThanOrEqualTo(root.get(OrderRecord_.createdDate), todayPlus(-1)));
              predicates.add(cb.lessThan(root.get(OrderRecord_.createdDate), today()));
            } else if (query.getDateRange() == DateRange.SEVEN_DAYS) {
              predicates.add(cb.greaterThanOrEqualTo(root.get(OrderRecord_.createdDate), todayPlus(-7)));
            } else if (query.getDateRange() == DateRange.THIRTY_DAYS) {
              predicates.add(cb.greaterThanOrEqualTo(root.get(OrderRecord_.createdDate), todayPlus(-30)));
            }
          }
          if (query.getStatus() != null) {
            if (query.getStatus().getAny() != Boolean.TRUE) {
              List<String> statuses = new ArrayList<>();
              if (query.getStatus().getIncomplete() == Boolean.TRUE) {
                statuses.add(OrderRecord.STATUS_INCOMPLETE);
              }
              if (query.getStatus().getCancelled() == Boolean.TRUE) {
                statuses.add(OrderRecord.STATUS_CANCELLED);
              }
              if (query.getStatus().getProcessing() == Boolean.TRUE) {
                statuses.add(OrderRecord.STATUS_PROCESSING);
              }
              if (query.getStatus().getOnHold() == Boolean.TRUE) {
                statuses.add(OrderRecord.STATUS_ON_HOLD);
              }
              if (query.getStatus().getCompleted() == Boolean.TRUE) {
                statuses.add(OrderRecord.STATUS_COMPLETE);
              }
              if (query.getStatus().getChargeback() == Boolean.TRUE) {
                statuses.add(OrderRecord.STATUS_CHARGE_BACK);
              }
              if (query.getStatus().getFailed() == Boolean.TRUE) {
                statuses.add(OrderRecord.STATUS_FAILED);
              }
              predicates.add(cb.or(
                  statuses.stream().map(s -> cb.equal(root.get(OrderRecord_.status), s)).toArray(Predicate[]::new)));
            }
          }
          if (query.getOrderId() != null) {
            predicates.add(cb.equal(root.get(OrderRecord_.orderNum), query.getOrderId()));
          }

        }
        predicates.add(cb.notEqual(root.get(OrderRecord_.status), OrderRecord.STATUS_DELETED));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  public static Specification<OrderRecord> orderSearchForIncompleteOrFailedEmail(OrderSearchQuery query) {
    return new Specification<OrderRecord>() {
      private static final long serialVersionUID = -4759580575535609009L;

      @Override
      public Predicate toPredicate(Root<OrderRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (query != null) {
          DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
          String created = query.getCreatedAt();

          predicates.add(cb.equal(root.get(OrderRecord_.email), query.getEmail()));
          predicates.add(cb.equal(root.get(OrderRecord_.product), query.getProduct()));

          List<String> statuses = new ArrayList<>();
          statuses.add(OrderRecord.STATUS_PROCESSING);
          statuses.add(OrderRecord.STATUS_COMPLETE);
          Predicate firstPredicate = cb
              .or(statuses.stream().map(s -> cb.equal(root.get(OrderRecord_.status), s)).toArray(Predicate[]::new));

          if (!created.isEmpty()) {
            Date firstCreatedStart = java.sql.Date.valueOf(LocalDate.parse(created, inputFormatter).minusDays(90));
            firstPredicate = cb.and(firstPredicate,
                cb.greaterThanOrEqualTo(root.get(OrderRecord_.createdDate), firstCreatedStart));
          }

          if (query.getStatus().getIncomplete()) {
            Predicate secondPredicate = cb.equal(root.get(OrderRecord_.status), OrderRecord.STATUS_FAILED);

            if (!created.isEmpty()) {
              Date secondCreatedStart = java.sql.Date.valueOf(LocalDate.parse(created, inputFormatter));
              secondPredicate = cb.and(secondPredicate,
                  cb.greaterThanOrEqualTo(root.get(OrderRecord_.createdDate), secondCreatedStart));
            }
            firstPredicate = cb.or(firstPredicate, secondPredicate);
          }
          predicates.add(firstPredicate);
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  public static Specification<OrderRecord> searchInCompleteOrProcessingOrFailedOrdersWithEmail(OrderSearchQuery query) {
	    return new Specification<OrderRecord>() {
	      /**
	       *
	       */
	      private static final long serialVersionUID = 7315360575535609009L;

	      @Override
	      public Predicate toPredicate(Root<OrderRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
	        List<Predicate> predicates = new ArrayList<>();
	        if (query != null) {
	            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
	            String created = query.getCreatedAt();

	            predicates.add(cb.equal(root.get(OrderRecord_.email), query.getEmail()));
	            predicates.add(cb.equal(root.get(OrderRecord_.product), query.getProduct()));
	            if (query.getStatus() != null) {
	                  List<String> statuses = new ArrayList<>();
	                  if (query.getStatus().getIncomplete() == Boolean.TRUE) {
	                    statuses.add(OrderRecord.STATUS_INCOMPLETE);
	                  }
	                   if (query.getStatus().getProcessing() == Boolean.TRUE) {
	                    statuses.add(OrderRecord.STATUS_PROCESSING);
	                  }
	                  if (query.getStatus().getCompleted() == Boolean.TRUE) {
	                    statuses.add(OrderRecord.STATUS_COMPLETE);
	                  }
	                  if (query.getStatus().getFailed() == Boolean.TRUE) {
	                    statuses.add(OrderRecord.STATUS_FAILED);
	                  }
	                  if(statuses.size() > 0) {
	                	  predicates.add(cb.or(
	    	                      statuses.stream().map(s -> cb.equal(root.get(OrderRecord_.status), s)).toArray(Predicate[]::new)));
	                }
	              }
	            if (!created.isEmpty()) {
	            	 Date createdStartDate = java.sql.Date.valueOf(LocalDate.parse(created, inputFormatter).minusDays(90));
	 	            predicates.add(cb.greaterThanOrEqualTo(root.get(OrderRecord_.createdDate), createdStartDate));
	            }
	        }
	        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	      }
	    };
	  }

  public static Specification<OrderRecord> orderSearchByBatchDocument(Long batchId) {
    return new Specification<OrderRecord>() {
      /**
       *
       */
      private static final long serialVersionUID = -4759580575535609009L;

      @Override
      public Predicate toPredicate(Root<OrderRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(OrderRecord_.batch), batchId));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  public static Date today() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("US/Eastern")));
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    Date today = cal.getTime();
    LOG.info("Today: " + DateFormat.getDateTimeInstance().format(today));
    return today;
  }

  public static Date todayPlus(int days) {
    Date today = today();
    Calendar cal = new GregorianCalendar();
    cal.setTime(today);
    cal.add(Calendar.DATE, days);
    return cal.getTime();
  }

  public static Specification<OrderRecord> pendingPaymentPlans() {
    return new Specification<OrderRecord>() {
      /**
       *
       */
      private static final long serialVersionUID = -5328598247435814063L;

      @Override
      public Predicate toPredicate(Root<OrderRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        return cb.and(cb.equal(root.get(OrderRecord_.status), OrderRecord.STATUS_PROCESSING),
            cb.equal(root.get(OrderRecord_.product), TaxConstants.PAYMENTPLAN));
      }
    };
  }

  public static Specification<OrderRecord> pendingOic() {
    return new Specification<OrderRecord>() {
      /**
       *
       */
      private static final long serialVersionUID = -5027912106381355290L;

      @Override
      public Predicate toPredicate(Root<OrderRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        return cb.and(cb.equal(root.get(OrderRecord_.status), OrderRecord.STATUS_PROCESSING),
            cb.equal(root.get(OrderRecord_.product), "OfferInCompromise"));
      }
    };
  }

  public static Specification<OrderRecord> pendingTaxLien() {
    return new Specification<OrderRecord>() {
      /**
       *
       */
      private static final long serialVersionUID = -8526769740560083542L;

      @Override
      public Predicate toPredicate(Root<OrderRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        return cb.and(cb.equal(root.get(OrderRecord_.status), OrderRecord.STATUS_PROCESSING),
            cb.equal(root.get(OrderRecord_.product), "TaxLienRemoval"));
      }
    };
  }

}