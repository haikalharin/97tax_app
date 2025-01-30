package com.repnox.nineseventax.features.paymentplans;

import com.repnox.nineseventax.exceptions.ConflictException;
import com.repnox.nineseventax.features.admin.OrderSearchQuery;
import com.repnox.nineseventax.features.admin.OrderSearchStatuses;
import com.repnox.nineseventax.features.admin.OrderSpecs;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.email.MailjetSender;
import com.repnox.nineseventax.features.partnercode.model.PartnerCode;
import com.repnox.nineseventax.features.partnercode.model.PartnerCodeRepo;
import com.repnox.nineseventax.features.utils.TaxConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("/api/paymentplan")
@Slf4j
public class PaymentPlanController {

	public static final String CURRENT_ORDER = "currentOrder";

	@Autowired
	private OrderRecordRepo orderRecordRepo;

	@Autowired
	private PaymentPlanRepo paymentPlanRepo;

	@Autowired
	private MailjetSender mailjetSender;

	@Autowired
	private PartnerCodeRepo partnerCodeRepo;

	private OrderRecord mapOrderRecord(OrderRecord orderRecord, PaymentPlanDetails paymentPlanDetails) {
		orderRecord.setFirstName(paymentPlanDetails.getFirstName());
		orderRecord.setLastName(paymentPlanDetails.getLastName());
		orderRecord.setEmail(paymentPlanDetails.getEmail());
		orderRecord.setPhone(paymentPlanDetails.getPhone());
		orderRecord.setSecondaryPhone(paymentPlanDetails.getSecondaryPhone());
		orderRecord.setTotalDebt(paymentPlanDetails.getTotalDebt());
		orderRecord.setIsCalifornia(paymentPlanDetails.getIsCalifornia());
		orderRecord.setIsNewJersey(paymentPlanDetails.getIsNewJersey());
		orderRecord.setIsGeorgia(paymentPlanDetails.getIsGeorgia());
		orderRecord.setIsIllinois(paymentPlanDetails.getIsIllinois());
		orderRecord.setIsMichigan(paymentPlanDetails.getIsMichigan());
		orderRecord.setPaymentMonths(paymentPlanDetails.getPaymentMonths());
		orderRecord.setPayrollDeduction(paymentPlanDetails.getPayrollDeduction());
		orderRecord.setProduct(TaxConstants.PAYMENTPLAN);
		return orderRecord;
	}

	@PostMapping
	public @ResponseBody OrderRecord createPaymentPlan(HttpSession session,
			@RequestBody PaymentPlanDetails paymentPlanDetails) {
		OrderRecord orderRecord = new OrderRecord();
		orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE);
		mapOrderRecord(orderRecord, paymentPlanDetails);
		orderRecord.setLastUpdatedMillis(System.currentTimeMillis());

		orderRecordRepo.save(orderRecord);

		paymentPlanDetails.setId(null);
		paymentPlanDetails.setOrderNum(orderRecord.getOrderNum());

		Boolean isIllinois = orderRecord.getIsIllinois();
		if (isIllinois != null && isIllinois) {
			paymentPlanDetails.setMonthlyPayment(paymentPlanDetails.getMonthlyPayment());
		} else {
			paymentPlanDetails.setMonthlyPayment(
					calculatePaymentAmount(orderRecord.getTotalDebt(), orderRecord.getPaymentMonths()));
		}
		if (paymentPlanDetails.getIsOwedFromBusiness() == null) {
			paymentPlanDetails.setIsOwedFromBusiness(true);
		}
		paymentPlanRepo.save(paymentPlanDetails);

		session.setAttribute(CURRENT_ORDER, orderRecord);

		OrderSearchQuery orderSearchQuery = new OrderSearchQuery();
		orderSearchQuery.setEmail(orderRecord.getEmail());
		orderSearchQuery.setCreatedAt(orderRecord.getCreatedDate().toString());
		orderSearchQuery.setProduct(TaxConstants.PAYMENTPLAN);
		OrderSearchStatuses curentStatus = new OrderSearchStatuses();
		curentStatus.setIncomplete(true);
		orderSearchQuery.setStatus(curentStatus);
		List<OrderRecord> inCompleteOrders = orderRecordRepo.findAll(OrderSpecs.searchInCompleteOrProcessingOrFailedOrdersWithEmail(orderSearchQuery));

		if(inCompleteOrders.size() == 1) {
			final OrderRecord tempOrderRecord = orderRecord;
			final PaymentPlanDetails tempPaymentPlanDetails = paymentPlanDetails;
			Timer timer1 = new Timer();
            Timer timer2 = new Timer();
            TimerTask delayedThreadStartTask1 = new TimerTask() {
                @Override
                public void run() {
                	sendInCompleteOrderMail(tempOrderRecord, tempPaymentPlanDetails);
                }
            };
            TimerTask delayedThreadStartTask2 = new TimerTask() {
                @Override
                public void run() {
                	sendInCompleteOrderMail(tempOrderRecord, tempPaymentPlanDetails);
                }
            };

            timer1.schedule(delayedThreadStartTask1, 30 * 60 * 1000);
            timer2.schedule(delayedThreadStartTask2, 3 * 24 * 3600 * 1000);
		}

		return orderRecord;
	}

	@PostMapping("/unbounce")
	public RedirectView createPaymentPlanFromUnbounce(HttpSession session, HttpServletRequest request) {

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String totalDebt = request.getParameter("totalDebt");

		PaymentPlanDetails paymentPlanDetails = new PaymentPlanDetails();
		paymentPlanDetails.setFirstName(firstName);
		paymentPlanDetails.setLastName(lastName);
		paymentPlanDetails.setEmail(email);
		paymentPlanDetails.setPhone(phone);
		paymentPlanDetails.setTotalDebt(new BigDecimal(totalDebt));

		OrderRecord orderRecord = new OrderRecord();
		orderRecord.setStatus(OrderRecord.STATUS_INCOMPLETE);

		mapOrderRecord(orderRecord, paymentPlanDetails);

		orderRecordRepo.save(orderRecord);

		paymentPlanDetails.setId(null);
		paymentPlanDetails.setOrderNum(orderRecord.getOrderNum());
		paymentPlanDetails.setMonthlyPayment(calculatePaymentAmount(paymentPlanDetails.getTotalDebt(), 72));
		paymentPlanRepo.save(paymentPlanDetails);

		session.setAttribute(CURRENT_ORDER, orderRecord);

		Timer timer = new Timer();
		TimerTask delayedThreadStartTask = new TimerTask() {
			@Override
			public void run() {
				OrderSearchQuery orderSearchQuery = new OrderSearchQuery();
				orderSearchQuery.setEmail(orderRecord.getEmail());
				orderSearchQuery.setCreatedAt(orderRecord.getCreatedDate().toString());
				orderSearchQuery.setProduct(TaxConstants.PAYMENTPLAN);

				OrderSearchStatuses statuses = new OrderSearchStatuses();
				statuses.setIncomplete(true);
				orderSearchQuery.setStatus(statuses);

				Optional<OrderRecord> order = orderRecordRepo
						.findOne(OrderSpecs.orderSearchForIncompleteOrFailedEmail(orderSearchQuery));
				if (!order.isPresent()) {
					mailjetSender.sendPaymentPlanIncompleteEmail(orderRecord, paymentPlanDetails);
				}
			}
		};

		timer.schedule(delayedThreadStartTask, 30 * 60 * 1000); // 1 minute

		return new RedirectView("/payment-plan-order-form");
	}

	@PutMapping
	public void updatePaymentPlan(HttpSession session, @RequestBody PaymentPlanDetails paymentPlanDetails)
			throws URISyntaxException {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -120);

		Calendar calSubtract365days = new GregorianCalendar();
		calSubtract365days.setTime(new Date());
		calSubtract365days.add(Calendar.DATE, -365);

		PaymentPlanDetails existing = null;

		OrderRecord orderRecord = (OrderRecord) session.getAttribute(CURRENT_ORDER);
		if (orderRecord != null) {
			existing = paymentPlanRepo.findByOrderNum(orderRecord.getOrderNum());
		} else {
			orderRecord = new OrderRecord();
			mapOrderRecord(orderRecord, paymentPlanDetails);
			orderRecord.setLastUpdatedMillis(System.currentTimeMillis());
			orderRecordRepo.save(orderRecord);
		}

		if (existing == null) {
			existing = new PaymentPlanDetails();
			existing.setOrderNum(orderRecord.getOrderNum());
		}

		// List<OrderRecord> potentialDupes;
		// List<OrderRecord> potentialDupes2;

		List<OrderRecord> duplicateOrdersWithChargeback = orderRecordRepo.findOrdersWithEmailAndStatusAndTime(existing.getEmail(),
				Arrays.asList(OrderRecord.STATUS_CHARGE_BACK), calSubtract365days.getTime(), TaxConstants.PAYMENTPLAN);
		if (duplicateOrdersWithChargeback.size() > 0 ) {
			throw new ConflictException();
		}
		// I think it was extra code not valid checking
//		if (paymentPlanDetails.getIsCalifornia() != null && paymentPlanDetails.getIsCalifornia()) {
//			potentialDupes = orderRecordRepo.findPotentialDuplicateForCalifornia(paymentPlanDetails.getEmail(),
//					Arrays.asList(OrderRecord.STATUS_COMPLETE), cal.getTime(), TaxConstants.PAYMENTPLAN);
//			potentialDupes2 = orderRecordRepo.findPotentialDuplicate2ForCalifornia(paymentPlanDetails.getEmail(),
//					paymentPlanDetails.getPhone(), Arrays.asList(OrderRecord.STATUS_CHARGE_BACK));
//		} else if (paymentPlanDetails.getIsNewJersey() != null && paymentPlanDetails.getIsNewJersey()) {
//			potentialDupes = orderRecordRepo.findPotentialDuplicateForNewJersey(paymentPlanDetails.getEmail(),
//					Arrays.asList(OrderRecord.STATUS_COMPLETE), cal.getTime(), TaxConstants.PAYMENTPLAN);
//			potentialDupes2 = orderRecordRepo.findPotentialDuplicate2ForNewJersey(paymentPlanDetails.getEmail(),
//					paymentPlanDetails.getPhone(), Arrays.asList(OrderRecord.STATUS_CHARGE_BACK));
//		} else if (paymentPlanDetails.getIsGeorgia() != null && paymentPlanDetails.getIsGeorgia()) {
//			potentialDupes = orderRecordRepo.findPotentialDuplicateForGeorgia(paymentPlanDetails.getEmail(),
//					Arrays.asList(OrderRecord.STATUS_COMPLETE), cal.getTime(), TaxConstants.PAYMENTPLAN);
//			potentialDupes2 = orderRecordRepo.findPotentialDuplicate2ForGeorgia(paymentPlanDetails.getEmail(),
//					paymentPlanDetails.getPhone(), Arrays.asList(OrderRecord.STATUS_CHARGE_BACK));
//		} else if (paymentPlanDetails.getIsIllinois() != null && paymentPlanDetails.getIsIllinois()) {
//			potentialDupes = orderRecordRepo.findPotentialDuplicateForIllinois(paymentPlanDetails.getEmail(),
//					Arrays.asList(OrderRecord.STATUS_COMPLETE), cal.getTime(), TaxConstants.PAYMENTPLAN);
//			potentialDupes2 = orderRecordRepo.findPotentialDuplicate2ForIllinois(paymentPlanDetails.getEmail(),
//					paymentPlanDetails.getPhone(), Arrays.asList(OrderRecord.STATUS_CHARGE_BACK));
//		} else {
//			potentialDupes = orderRecordRepo.findPotentialDuplicateForIRS(paymentPlanDetails.getEmail(),
//					Arrays.asList(OrderRecord.STATUS_COMPLETE), cal.getTime(), TaxConstants.PAYMENTPLAN);
//			potentialDupes2 = orderRecordRepo.findPotentialDuplicate2ForIRS(paymentPlanDetails.getEmail(),
//					paymentPlanDetails.getPhone(), Arrays.asList(OrderRecord.STATUS_CHARGE_BACK));
//		}
//
//		if (potentialDupes.size() > 0 || potentialDupes2.size() > 0) {
//			throw new ConflictException();
//		}


		if (paymentPlanDetails.getIsOwedFromBusiness() != null && paymentPlanDetails.getIsOwedFromBusiness() == true) {
			existing.setIsOwedFromBusiness(true);
			existing.setBusinessName(paymentPlanDetails.getBusinessName());
			existing.setEin(paymentPlanDetails.getEin());
			existing.setDba(paymentPlanDetails.getDba());
			existing.setIllinoisAccountId(paymentPlanDetails.getIllinoisAccountId());
			existing.setMobile(paymentPlanDetails.getMobile());
			existing.setGoodFaithPayment(paymentPlanDetails.getGoodFaithPayment());
		}
		if (paymentPlanDetails.getOrderUrl() != null) {
			existing.setOrderUrl(paymentPlanDetails.getOrderUrl());
		}
		if (paymentPlanDetails.getIsMobile() != null) {
			existing.setIsMobile(paymentPlanDetails.getIsMobile());
		}
		existing.setTotalDebt(paymentPlanDetails.getTotalDebt());
		existing.setSecondaryPhone(paymentPlanDetails.getSecondaryPhone());
		existing.setMarried(paymentPlanDetails.getMarried());
		existing.setFilingJointly(paymentPlanDetails.getFilingJointly());
		existing.setTimeToCall(paymentPlanDetails.getTimeToCall());
		existing.setPaymentDayOfMonth(paymentPlanDetails.getPaymentDayOfMonth());
		existing.setSpouseFirstName(paymentPlanDetails.getSpouseFirstName());
		existing.setSpouseLastName(paymentPlanDetails.getSpouseLastName());
		existing.setSpouseSsn(paymentPlanDetails.getSpouseSsn());
		existing.setIsCalifornia(paymentPlanDetails.getIsCalifornia());
		existing.setIsNewJersey(paymentPlanDetails.getIsNewJersey());
		existing.setIsGeorgia(paymentPlanDetails.getIsGeorgia());
		existing.setPaymentMonths(paymentPlanDetails.getPaymentMonths());
		Boolean isIllinois = paymentPlanDetails.getIsIllinois();
		existing.setIsIllinois(isIllinois);
		if (isIllinois != null && isIllinois) {
			existing.setMonthlyPayment(paymentPlanDetails.getMonthlyPayment());
		} else {
			existing.setMonthlyPayment(
					calculatePaymentAmount(paymentPlanDetails.getTotalDebt(), paymentPlanDetails.getPaymentMonths()));
		}

		orderRecord.setFirstName(existing.getFirstName());
		orderRecord.setLastName(existing.getLastName());
		orderRecord.setTotalDebt(existing.getTotalDebt());
		orderRecord.setEmail(existing.getEmail());
		orderRecord.setPhone(existing.getPhone());
		orderRecord.setSecondaryPhone(existing.getSecondaryPhone());
		orderRecord.setProduct(TaxConstants.PAYMENTPLAN);
		orderRecord.setPaymentMonths(existing.getPaymentMonths());

		orderRecord.setLastUpdatedMillis(System.currentTimeMillis());
		orderRecordRepo.save(orderRecord);
		paymentPlanRepo.save(existing);

		session.setAttribute(CURRENT_ORDER, orderRecord);
	}

	@GetMapping("/partnercodes/{code}")
	public @ResponseBody PartnerCode getPartnerCode(HttpSession session, @PathVariable String code) {
		return partnerCodeRepo.findByCode(code);
	}

	public static Integer calculatePaymentAmount(BigDecimal totalDebt, Integer paymentMonths) {
		BigDecimal months = null;

		if (totalDebt == null || totalDebt.equals(BigDecimal.ZERO)) {
			return 0;
		}

		if (paymentMonths == null) {
			months = new BigDecimal(72);
		} else {
			months = new BigDecimal(paymentMonths);
		}

		Integer amount = totalDebt.divide(months, 0, RoundingMode.UP).intValue();

		if (amount < 25) {
			amount = 25;
		}

		return amount;
	}

	private void sendInCompleteOrderMail(OrderRecord orderRecord,PaymentPlanDetails paymentPlanDetails) {
		try {
			OrderSearchQuery orderSearchQuery = new OrderSearchQuery();
			orderSearchQuery.setEmail(orderRecord.getEmail());
			orderSearchQuery.setCreatedAt(orderRecord.getCreatedDate().toString());
			orderSearchQuery.setProduct(TaxConstants.PAYMENTPLAN);

			OrderSearchStatuses statuses = new OrderSearchStatuses();
			statuses.setProcessing(true);
			statuses.setCompleted(true);
			statuses.setFailed(true);
			orderSearchQuery.setStatus(statuses);

			List<OrderRecord> currentOrders = orderRecordRepo.findAll(OrderSpecs.searchInCompleteOrProcessingOrFailedOrdersWithEmail(orderSearchQuery));

			if(currentOrders == null || currentOrders.size() == 0 ) {
				mailjetSender.sendPaymentPlanIncompleteEmail(orderRecord, paymentPlanDetails);
			}
		} catch (Exception e) {
			log.error("ERROR: occurred while sending incomplete order mail", e);
		}
	}

}
