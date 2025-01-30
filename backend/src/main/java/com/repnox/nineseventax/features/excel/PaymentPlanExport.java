package com.repnox.nineseventax.features.excel;

import com.repnox.nineseventax.features.admin.OrderSpecs;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.partnercode.model.PCUsageReport;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanRepo;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalDetails;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalRepo;
import com.repnox.nineseventax.features.utils.StringUtility;
import com.repnox.nineseventax.features.utils.TaxConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class PaymentPlanExport {

	private static final Logger LOG = LoggerFactory.getLogger(PaymentPlanExport.class);

	@Autowired
	public OrderRecordRepo orderRecordRepo;

	@Autowired
	public PaymentPlanRepo paymentPlanRepo;

	@Autowired
	public TaxLienRemovalRepo taxLienRemovalRepo;

	// public void exportPaymentPlanExcel(OutputStream out, List<OrderRecord>
	// orders) {
	// TableExporter exporter = new ExcelTableExporter("Orders");
	// exportPaymentPlanExcel(exporter, mapExcelRows(orders), out);
	// }

	public void exportProcessingOrdersExcel(OutputStream out, List<OrderRecord> orders) {
		TableExporter exporter = new ExcelTableExporter("Orders");
		exportProcessingOrdersExcel(exporter, mapProcessingExcelRows(orders), out);
	}

	public void exportUsageReportExcel(OutputStream out, List<PCUsageReport> reports, String daterangeStr) {
		TableExporter exporter = new ExcelTableExporter("UsageReport");
		exportUsageReportExcel(exporter, mapUsageReportExcelRows(reports), out, daterangeStr);
	}

	// public void exportPaymentPlanCsv(OutputStream out, List<OrderRecord> orders)
	// {
	// TableExporter exporter = new CsvTableExport();
	// exportPaymentPlanExcel(exporter, mapExcelRows(orders), out)pass;
	// }

	public void exportProcessingOrdersCsvStream(OutputStream out, List<OrderRecord> orders) {
		TableExporter exporter = new CsvTableExport();
		exportProcessingOrdersExcelStream(exporter, mapProcessingExcelRows(orders), out);
	}

	public void exportProcessingOrdersCsv(OutputStream out, List<OrderRecord> orders) {
		TableExporter exporter = new CsvTableExport();
		exportProcessingOrdersExcel(exporter, mapProcessingExcelRows(orders), out);
	}

	public List<ExcelPaymentPlanRow> mapProcessingExcelRows(List<OrderRecord> records) {
		List<ExcelPaymentPlanRow> excelRows = new ArrayList<>();

		String exportDateStr = defaultString(new Date());
		StringUtility stringUtility = new StringUtility();
		for (OrderRecord record : records) {
			PaymentPlanDetails paymentPlanDetails = null;
			TaxLienRemovalDetails taxlienPlanDetails = null;
			if (record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
				paymentPlanDetails = paymentPlanRepo.findByOrderNum(record.getOrderNum());
			} else {
				taxlienPlanDetails = taxLienRemovalRepo.getByOrderNum(record.getOrderNum());
			}

			if (paymentPlanDetails == null && taxlienPlanDetails == null) {
				LOG.error("Skipping pending order with no Payment Plan: " + record.getOrderNum());
				continue;
			}

			ExcelPaymentPlanRow row = new ExcelPaymentPlanRow();
			row.setOrderNum(defaultString(record.getOrderNum()));
			row.setDate(exportDateStr);
			row.setOrderStatus(record.getStatus());
			row.setFirstName(stringUtility.ReplaceSpecialCharactersToCharacters(record.getFirstName()));
			row.setLastName(stringUtility.ReplaceSpecialCharactersToCharacters(record.getLastName()));
			row.setFirstAndLast(stringUtility
					.ReplaceSpecialCharactersToCharacters(record.getFirstName() + " " + record.getLastName()));
			row.setSsn(record.getSsn());
			if (record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
				row.setMarried(defaultString(paymentPlanDetails.getMarried()));
				row.setSpouseFirstName(
						stringUtility.ReplaceSpecialCharactersToCharacters(paymentPlanDetails.getSpouseFirstName()));
				row.setSpouseLastName(
						stringUtility.ReplaceSpecialCharactersToCharacters(paymentPlanDetails.getSpouseLastName()));
				row.setSpouseSsn(paymentPlanDetails.getSpouseSsn());
			} else {
				row.setMarried(defaultString(false));
				row.setSpouseFirstName("");
				row.setSpouseLastName("");
				row.setSpouseSsn("");
			}

			row.setMailAddressLine1(stringUtility.ReplaceSpecialCharactersToCharacters(record.getShippingAddress1()));

			String shippingAddress2 = record.getShippingAddress2();
			if (shippingAddress2 != null && shippingAddress2.trim().length() > 0
					&& !shippingAddress2.toUpperCase().contains("STE")
					&& !shippingAddress2.toUpperCase().contains("SUITE")
					&& !shippingAddress2.toUpperCase().contains("UNIT")
					&& !shippingAddress2.toUpperCase().contains("LOT")
					&& !shippingAddress2.toUpperCase().contains("APT")) {

				row.setMailAddressLine2(record.getShippingAddress2());

			} else {
				row.setMailAddressLine2(record.getShippingAddress2());
			}
			row.setCityStateZip(
					record.getShippingCity() + ", " + record.getShippingState() + ", " + record.getShippingZip());
			row.setCity(record.getShippingCity());
			row.setState(record.getShippingState());
			row.setZip(record.getShippingZip());
			row.setPhone(record.getPhone());
			Boolean isCalifornia = record.getIsCalifornia();
			Boolean isNewJersey = record.getIsNewJersey();
			Boolean isGeorgia = record.getIsGeorgia();
			Boolean isIllinois = record.getIsIllinois();
			Boolean isMichigan = record.getIsMichigan();
			if (record.getProduct().equals(TaxConstants.PAYMENTPLAN)) {
				if (isCalifornia != null && isCalifornia) {
					row.setProduct(TaxConstants.CALIFORNIA_PAYMENTPLAN);
				} else if (isNewJersey != null && isNewJersey) {
					row.setProduct(TaxConstants.NEW_JERSEY_PAYMENTPLAN);
				} else if (isGeorgia != null && isGeorgia) {
					row.setProduct(TaxConstants.GEORGIA_PAYMENTPLAN);
				} else if (isIllinois != null && isIllinois) {
					row.setProduct(TaxConstants.ILLINOIS_PAYMENTPLAN);
				} else if (isMichigan != null && isMichigan) {
					row.setProduct(TaxConstants.MICHIGAN_PAYMENTPLAN);
				} else {
					row.setProduct(TaxConstants.IRS_PAYMENTPLAN);
				}
				row.setBestTimeToCall(paymentPlanDetails.getTimeToCall());
				if (paymentPlanDetails.getTotalDebt() != null) {
					row.setOwedDebtTotal(
							defaultString(paymentPlanDetails.getTotalDebt().setScale(0, BigDecimal.ROUND_DOWN)));
					row.setCentsNumbers(paymentPlanDetails.getTotalDebt().remainder(BigDecimal.ONE)
							.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toPlainString());
				} else {
					row.setOwedDebtTotal("");
					row.setCentsNumbers("");
				}
				row.setPaymentAmount(defaultString(paymentPlanDetails.getMonthlyPayment()));
				row.setDateOfMakeYourPayment(paymentPlanDetails.getPaymentDayOfMonth());
			} else {
				row.setProduct(record.getProduct());
				row.setBestTimeToCall("--");
				if (record.getTotalDebt() != null) {
					row.setOwedDebtTotal(defaultString(record.getTotalDebt().setScale(0, BigDecimal.ROUND_DOWN)));
					row.setCentsNumbers(record.getTotalDebt().remainder(BigDecimal.ONE).multiply(new BigDecimal("100"))
							.setScale(0, BigDecimal.ROUND_DOWN).toPlainString());
				} else {
					row.setOwedDebtTotal("--");
					row.setCentsNumbers("--");
				}
				row.setPaymentAmount(defaultString(record.getTotalDebt()));
				row.setDateOfMakeYourPayment("--");
			}

			if (isCalifornia != null && isCalifornia) {
				row.setToNameLine1(TaxConstants.STATE_OF_CALIFORNIA);
				row.setToNameLine2(TaxConstants.FRANCHISE_TAX_BOARD);
				row.setToAddress("P.O. Box 2952");
				row.setToCity("Sacramento");
				row.setToState("CA");
				row.setToZip("95812-2952");
			} else if (isNewJersey != null && isNewJersey) {
				row.setToNameLine1(TaxConstants.NEW_JERSEY_DIVISION_OF_TAXATION);
				row.setToNameLine2(TaxConstants.PAYMENT_PLAN_UNIT);
				row.setToAddress("P.O. Box 190");
				row.setToCity("Trenton");
				row.setToState("NJ");
				row.setToZip("08695-0190");
			} else if (isGeorgia != null && isGeorgia) {
				row.setToNameLine1(TaxConstants.GEORGIA_DEPARTMENT_OF_REVENUE);
				row.setToNameLine2(TaxConstants.PROCESSING_CENTER);
				row.setToAddress("P.O. Box 105596");
				row.setCity("Atlanta");
				row.setToState("GA");
				row.setToZip("30374-0396");
			} else if (isIllinois != null && isIllinois) {
				row.setToNameLine1(TaxConstants.ILLINOIS_DEPARTMENT_OF_REVENUE);
				row.setToNameLine2(TaxConstants.INSTALLMENT_CONTRACT_UNIT);
				row.setToAddress("PO BOX 19035");
				row.setCity("Springfield");
				row.setToState("IL");
				row.setToZip("62794-9035");
			} else {
				String state = record.getShippingState();

				if ("AK".equals(state) || "AZ".equals(state) || "CO".equals(state) || "CT".equals(state)
						|| "DE".equals(state) || "DC".equals(state) || "HI".equals(state) || "ID".equals(state)
						|| "IL".equals(state) || "ME".equals(state) || "MD".equals(state) || "MA".equals(state)
						|| "MT".equals(state) || "NV".equals(state) || "NH".equals(state) || "NJ".equals(state)
						|| "NM".equals(state) || "ND".equals(state) || "OR".equals(state) || "RI".equals(state)
						|| "SD".equals(state) || "TN".equals(state) || "UT".equals(state) || "VT".equals(state)
						|| "WA".equals(state) || "WI".equals(state) || "WY".equals(state)) {

					row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
					row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
					row.setToAddress("310 Lowell St. Stop 830");
					row.setToCity("Andover");
					row.setToState("MA");
					row.setToZip("01810");

				} else if ("AL".equals(state) || "FL".equals(state) || "GA".equals(state) || "KY".equals(state)
						|| "LA".equals(state) || "MS".equals(state) || "NC".equals(state) || "SC".equals(state)
						|| "TX".equals(state) || "VA".equals(state)) {

					row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
					row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
					row.setToAddress("P.O. Box 47421");
					row.setToCity("Doraville");
					row.setToState("GA");
					row.setToZip("30362-0421");

				} else if ("AR".equals(state) || "CA".equals(state) || "IN".equals(state) || "IA".equals(state)
						|| "KS".equals(state) || "MI".equals(state) || "MN".equals(state) || "MO".equals(state)
						|| "NE".equals(state) || "NY".equals(state) || "OH".equals(state) || "OK".equals(state)
						|| "PA".equals(state) || "WV".equals(state)) {

					row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
					row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
					row.setToAddress("Stop P-4 5000");
					row.setToCity("Kansas City");
					row.setToState("MO");
					row.setToZip("64999-0250");

				} else {
					row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
					row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
					row.setToAddress("310 Lowell St. Stop 830");
					row.setToCity("Andover");
					row.setToState("MA");
					row.setToZip("01810-5430");
				}

				row.setEmail(record.getEmail());
				row.setOrderDate(defaultString(record.getCreatedDate()));
			}

			excelRows.add(row);
		}
		return excelRows;
	}

	public List<ExcelUsageReportRow> mapUsageReportExcelRows(List<PCUsageReport> records) {
		List<ExcelUsageReportRow> excelRows = new ArrayList<>();

		String exportDateStr = defaultString(new Date());
		StringUtility stringUtility = new StringUtility();
		for (PCUsageReport record : records) {

			ExcelUsageReportRow row = new ExcelUsageReportRow();
			row.setPartnerName(record.getPartnerName());
			row.setTotalDue(record.getTotalDue());
			row.setCouponCode(record.getPartnerCode());
			row.setLifetimeUsage(record.getLifetimeUsage());
			row.setUsageAtPrice(record.getUsageAtPrice());
			row.setCommission(record.getCommission());
			row.setCommissionType(record.getCommissionTypeId());
			row.setContactPhone(record.getPhone());
			row.setContactEmail(record.getEmail());
			row.setNotes(record.getNotes());

			excelRows.add(row);
		}
		return excelRows;
	}

	public List<ExcelPaymentPlanRow> mapExcelRows(List<OrderRecord> records) {
		List<ExcelPaymentPlanRow> excelRows = new ArrayList<>();

		String exportDateStr = defaultString(new Date());
		for (OrderRecord record : records) {
			PaymentPlanDetails paymentPlanDetails = paymentPlanRepo.findByOrderNum(record.getOrderNum());
			if (paymentPlanDetails == null) {
				LOG.error("Skipping pending order with no Payment Plan: " + record.getOrderNum());
				continue;
			}

			ExcelPaymentPlanRow row = new ExcelPaymentPlanRow();
			row.setOrderNum(defaultString(record.getOrderNum()));
			row.setDate(exportDateStr);
			row.setOrderStatus(record.getStatus());
			row.setFirstName(record.getFirstName());
			row.setLastName(record.getLastName());
			row.setFirstAndLast(record.getFirstName() + " " + record.getLastName());
			row.setSsn(record.getSsn());
			row.setMarried(defaultString(paymentPlanDetails.getMarried()));
			row.setSpouseFirstName(paymentPlanDetails.getSpouseFirstName());
			row.setSpouseLastName(paymentPlanDetails.getSpouseLastName());
			row.setSpouseSsn(paymentPlanDetails.getSpouseSsn());
			row.setMailAddressLine1(record.getShippingAddress1());

			String shippingAddress2 = record.getShippingAddress2();
			if (shippingAddress2 != null && shippingAddress2.trim().length() > 0
					&& !shippingAddress2.toUpperCase().contains("STE")
					&& !shippingAddress2.toUpperCase().contains("SUITE")
					&& !shippingAddress2.toUpperCase().contains("UNIT")
					&& !shippingAddress2.toUpperCase().contains("LOT")
					&& !shippingAddress2.toUpperCase().contains("APT")) {

				row.setMailAddressLine2("APT " + record.getShippingAddress2());

			} else {
				row.setMailAddressLine2(record.getShippingAddress2());
			}
			row.setCityStateZip(
					record.getShippingCity() + ", " + record.getShippingState() + ", " + record.getShippingZip());
			row.setCity(record.getShippingCity());
			row.setState(record.getShippingState());
			row.setZip(record.getShippingZip());
			row.setPhone(record.getPhone());
			row.setProduct(record.getProduct());
			row.setBestTimeToCall(paymentPlanDetails.getTimeToCall());
			if (paymentPlanDetails.getTotalDebt() != null) {
				row.setOwedDebtTotal(
						defaultString(paymentPlanDetails.getTotalDebt().setScale(0, BigDecimal.ROUND_DOWN)));
				row.setCentsNumbers(paymentPlanDetails.getTotalDebt().remainder(BigDecimal.ONE)
						.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toPlainString());
			} else {
				row.setOwedDebtTotal("");
				row.setCentsNumbers("");
			}
			row.setPaymentAmount(defaultString(paymentPlanDetails.getMonthlyPayment()));
			row.setDateOfMakeYourPayment(paymentPlanDetails.getPaymentDayOfMonth());

			String state = record.getShippingState();

			if ("AK".equals(state) || "AZ".equals(state) || "CO".equals(state) || "CT".equals(state)
					|| "DE".equals(state) || "DC".equals(state) || "HI".equals(state) || "ID".equals(state)
					|| "IL".equals(state) || "ME".equals(state) || "MD".equals(state) || "MA".equals(state)
					|| "MT".equals(state) || "NV".equals(state) || "NH".equals(state) || "NJ".equals(state)
					|| "NM".equals(state) || "ND".equals(state) || "OR".equals(state) || "RI".equals(state)
					|| "SD".equals(state) || "TN".equals(state) || "UT".equals(state) || "VT".equals(state)
					|| "WA".equals(state) || "WI".equals(state) || "WY".equals(state)) {

				row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
				row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
				row.setToAddress("310 Lowell St. Stop 830");
				row.setToCity("Andover");
				row.setToState("MA");
				row.setToZip("01810");

			} else if ("AL".equals(state) || "FL".equals(state) || "GA".equals(state) || "KY".equals(state)
					|| "LA".equals(state) || "MS".equals(state) || "NC".equals(state) || "SC".equals(state)
					|| "TX".equals(state) || "VA".equals(state)) {

				row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
				row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
				row.setToAddress("P.O. Box 47421");
				row.setToCity("Doraville");
				row.setToState("GA");
				row.setToZip("30362-0421");

			} else if ("AR".equals(state) || "CA".equals(state) || "IN".equals(state) || "IA".equals(state)
					|| "KS".equals(state) || "MI".equals(state) || "MN".equals(state) || "MO".equals(state)
					|| "NE".equals(state) || "NY".equals(state) || "OH".equals(state) || "OK".equals(state)
					|| "PA".equals(state) || "WV".equals(state)) {

				row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
				row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
				row.setToAddress("Stop P-4 5000");
				row.setToCity("Kansas City");
				row.setToState("MO");
				row.setToZip("64999-0250");

			} else {
				row.setToNameLine1(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
				row.setToNameLine2(TaxConstants.INTERNAL_REVENUE_SERVICE);
				row.setToAddress("310 Lowell St. Stop 830");
				row.setToCity("Andover");
				row.setToState("MA");
				row.setToZip("01810-5430");
			}

			row.setEmail(record.getEmail());
			row.setOrderDate(defaultString(record.getCreatedDate()));

			excelRows.add(row);
		}
		return excelRows;
	}

	private String defaultString(BigDecimal v) {
		return v != null ? v.toPlainString() : "";
	}

	private String defaultString(Boolean v) {
		return v != null ? (v ? TaxConstants.YES : TaxConstants.NO) : TaxConstants.NO;
	}

	private String defaultString(Date v) {
		return v != null ? new SimpleDateFormat("MM/dd/yyyy").format(v) : "";
	}

	private String defaultString(Long v) {
		return v != null ? v.toString() : "";
	}

	private String defaultString(Integer v) {
		return v != null ? v.toString() : "";
	}

	public List<OrderRecord> findOrders() {
		return orderRecordRepo.findAll(OrderSpecs.pendingPaymentPlans());
	}

	public void exportProcessingOrdersExcel(TableExporter exporter, List<ExcelPaymentPlanRow> rows, OutputStream os) {

		exporter.nextCell("Order Number");
		exporter.nextCell("Date");
		exporter.nextCell("Order Status");
		exporter.nextCell("First Name (Billing)");
		exporter.nextCell("Last Name (Billing)");
		exporter.nextCell("First and Last");
		exporter.nextCell("social_security");
		exporter.nextCell("married");
		exporter.nextCell("wife_first_name");
		exporter.nextCell("wife_last_name");
		exporter.nextCell("wife_social_no");
		exporter.nextCell("mailing_address");
		exporter.nextCell("apt_unit_number");
		exporter.nextCell("city_state_zip_address");
		exporter.nextCell("City");
		exporter.nextCell("State");
		exporter.nextCell("Zip");
		exporter.nextCell("Phone (Billing)");
		exporter.nextCell("best_time_to_call");
		exporter.nextCell("owed_debt_total");
		exporter.nextCell("cents_numbers");
		exporter.nextCell("payment_amount");
		exporter.nextCell("date_of_make_your_payment");
		exporter.nextCell("Company2");
		exporter.nextCell("line2");
		exporter.nextCell("Address");
		exporter.nextCell("city2");
		exporter.nextCell("state2");
		exporter.nextCell("zip2");
		exporter.nextCell("email");
		exporter.nextCell("Order Date");
		exporter.nextCell("Service Type");

		for (ExcelPaymentPlanRow row : rows) {
			exporter.nextRow();
			exporter.nextCell(row.getOrderNum());
			exporter.nextCell(row.getDate());
			exporter.nextCell(row.getOrderStatus());
			exporter.nextCell(row.getFirstName());
			exporter.nextCell(row.getLastName());
			exporter.nextCell(row.getFirstAndLast());
			exporter.nextCell(row.getSsn());
			exporter.nextCell(row.getMarried());
			exporter.nextCell(row.getSpouseFirstName());
			exporter.nextCell(row.getSpouseLastName());
			exporter.nextCell(row.getSpouseSsn());
			exporter.nextCell(row.getMailAddressLine1());
			exporter.nextCell(row.getMailAddressLine2());
			exporter.nextCell(row.getCityStateZip());
			exporter.nextCell(row.getCity());
			exporter.nextCell(row.getState());
			exporter.nextCell(row.getZip());
			exporter.nextCell(row.getPhone());
			exporter.nextCell(row.getBestTimeToCall());
			exporter.nextCell(row.getOwedDebtTotal());
			exporter.nextCell(row.getCentsNumbers());
			exporter.nextCell(row.getPaymentAmount());
			exporter.nextCell(row.getDateOfMakeYourPayment());
			exporter.nextCell(row.getToNameLine1());
			exporter.nextCell(row.getToNameLine2());
			exporter.nextCell(row.getToAddress());
			exporter.nextCell(row.getToCity());
			exporter.nextCell(row.getToState());
			exporter.nextCell(row.getToZip());
			exporter.nextCell(row.getEmail());
			exporter.nextCell(row.getOrderDate());
			exporter.nextCell(row.getProduct());
		}

		exporter.export(os);

	}

	public void exportUsageReportExcel(TableExporter exporter, List<ExcelUsageReportRow> rows, OutputStream os,
			String daterangeStr) {

		exporter.nextCell("Date Range: " + daterangeStr);
		exporter.nextRow();
		exporter.nextRow();

		exporter.nextCell("Partner Name");
		exporter.nextCell("Total Due ($)");
		exporter.nextCell("Coupon Code");
		exporter.nextCell("Lifetime Usage");
		exporter.nextCell("Usage at Price (97)");
		exporter.nextCell("Commission (flat)");
		exporter.nextCell("Commission (%)");
		exporter.nextCell("Contact Name");
		exporter.nextCell("Contact Phone");
		exporter.nextCell("Contact Email");
		exporter.nextCell("Notes");

		for (ExcelUsageReportRow row : rows) {
			exporter.nextRow();
			exporter.nextCell(row.getPartnerName());
			exporter.nextCell(row.getTotalDue());
			exporter.nextCell(row.getCouponCode());
			exporter.nextCell(defaultString(row.getLifetimeUsage()));
			exporter.nextCell(defaultString(row.getUsageAtPrice()));
			if (row.getCommissionType() == 1) {
				exporter.nextCell(defaultString(row.getCommission()));
				exporter.nextCell("");
			} else {
				exporter.nextCell("");
				exporter.nextCell(defaultString(row.getCommission()));
			}
			exporter.nextCell(row.getContactName());
			exporter.nextCell(row.getContactPhone());
			exporter.nextCell(row.getContactEmail());
			exporter.nextCell(row.getNotes());
		}

		exporter.export(os);

	}

	public void exportProcessingOrdersExcelStream(TableExporter exporter, List<ExcelPaymentPlanRow> rows,
			OutputStream os) {

		exporter.nextCell("Order Number");
		exporter.nextCell("Date");
		exporter.nextCell("Order Status");
		exporter.nextCell("First Name (Billing)");
		exporter.nextCell("Last Name (Billing)");
		exporter.nextCell("First and Last");
		exporter.nextCell("social_security");
		exporter.nextCell("married");
		exporter.nextCell("wife_first_name");
		exporter.nextCell("wife_last_name");
		exporter.nextCell("wife_social_no");
		exporter.nextCell("mailing_address");
		exporter.nextCell("apt_unit_number");
		exporter.nextCell("city_state_zip_address");
		exporter.nextCell("City");
		exporter.nextCell("State");
		exporter.nextCell("Zip");
		exporter.nextCell("Phone (Billing)");
		exporter.nextCell("best_time_to_call");
		exporter.nextCell("owed_debt_total");
		exporter.nextCell("cents_numbers");
		exporter.nextCell("payment_amount");
		exporter.nextCell("date_of_make_your_payment");
		exporter.nextCell("Company2");
		exporter.nextCell("line2");
		exporter.nextCell("Address");
		exporter.nextCell("city2");
		exporter.nextCell("state2");
		exporter.nextCell("zip2");
		exporter.nextCell("email");
		exporter.nextCell("Order Date");
		exporter.nextCell("Service Type");

		for (ExcelPaymentPlanRow row : rows) {
			exporter.nextRow();
			exporter.nextCell(row.getOrderNum());
			exporter.nextCell(row.getDate());
			exporter.nextCell(row.getOrderStatus());
			exporter.nextCell(row.getFirstName());
			exporter.nextCell(row.getLastName());
			exporter.nextCell(row.getFirstAndLast());
			exporter.nextCell(row.getSsn());
			exporter.nextCell(row.getMarried());
			exporter.nextCell(row.getSpouseFirstName());
			exporter.nextCell(row.getSpouseLastName());
			exporter.nextCell(row.getSpouseSsn());
			exporter.nextCell(row.getMailAddressLine1());
			exporter.nextCell(row.getMailAddressLine2());
			exporter.nextCell(row.getCityStateZip());
			exporter.nextCell(row.getCity());
			exporter.nextCell(row.getState());
			exporter.nextCell(row.getZip());
			exporter.nextCell(row.getPhone());
			exporter.nextCell(row.getBestTimeToCall());
			exporter.nextCell(row.getOwedDebtTotal());
			exporter.nextCell(row.getCentsNumbers());
			exporter.nextCell(row.getPaymentAmount());
			exporter.nextCell(row.getDateOfMakeYourPayment());
			exporter.nextCell(row.getToNameLine1());
			exporter.nextCell(row.getToNameLine2());
			exporter.nextCell(row.getToAddress());
			exporter.nextCell(row.getToCity());
			exporter.nextCell(row.getToState());
			exporter.nextCell(row.getToZip());
			exporter.nextCell(row.getEmail());
			exporter.nextCell(row.getOrderDate());
			exporter.nextCell(row.getProduct());
		}

		exporter.export(os);

	}

// old
	public void exportPaymentPlanExcel(TableExporter exporter, List<ExcelPaymentPlanRow> rows, OutputStream os) {

		exporter.nextCell("Order Number");
		exporter.nextCell("Date");
		exporter.nextCell("Order Status");
		exporter.nextCell("First Name (Billing)");
		exporter.nextCell("Last Name (Billing)");
		exporter.nextCell("First and Last");
		exporter.nextCell("social_security");
		exporter.nextCell("married");
		exporter.nextCell("wife_first_name");
		exporter.nextCell("wife_last_name");
		exporter.nextCell("wife_social_no");
		exporter.nextCell("mailing_address");
		exporter.nextCell("apt_unit_number");
		exporter.nextCell("city_state_zip_address");
		exporter.nextCell("City");
		exporter.nextCell("State");
		exporter.nextCell("Zip");
		exporter.nextCell("Phone (Billing)");
		exporter.nextCell("best_time_to_call");
		exporter.nextCell("owed_debt_total");
		exporter.nextCell("cents_numbers");
		exporter.nextCell("payment_amount");
		exporter.nextCell("date_of_make_your_payment");
		exporter.nextCell("Company2");
		exporter.nextCell("line2");
		exporter.nextCell("Address");
		exporter.nextCell("city2");
		exporter.nextCell("state2");
		exporter.nextCell("zip2");
		exporter.nextCell("email");
		exporter.nextCell("Order Date");

		for (ExcelPaymentPlanRow row : rows) {
			exporter.nextRow();
			exporter.nextCell(row.getOrderNum());
			exporter.nextCell(row.getDate());
			exporter.nextCell(row.getOrderStatus());
			exporter.nextCell(row.getFirstName());
			exporter.nextCell(row.getLastName());
			exporter.nextCell(row.getFirstAndLast());
			exporter.nextCell(row.getSsn());
			exporter.nextCell(row.getMarried());
			exporter.nextCell(row.getSpouseFirstName());
			exporter.nextCell(row.getSpouseLastName());
			exporter.nextCell(row.getSpouseSsn());
			exporter.nextCell(row.getMailAddressLine1());
			exporter.nextCell(row.getMailAddressLine2());
			exporter.nextCell(row.getCityStateZip());
			exporter.nextCell(row.getCity());
			exporter.nextCell(row.getState());
			exporter.nextCell(row.getZip());
			exporter.nextCell(row.getPhone());
			exporter.nextCell(row.getBestTimeToCall());
			exporter.nextCell(row.getOwedDebtTotal());
			exporter.nextCell(row.getCentsNumbers());
			exporter.nextCell(row.getPaymentAmount());
			exporter.nextCell(row.getDateOfMakeYourPayment());
			exporter.nextCell(row.getToNameLine1());
			exporter.nextCell(row.getToNameLine2());
			exporter.nextCell(row.getToAddress());
			exporter.nextCell(row.getToCity());
			exporter.nextCell(row.getToState());
			exporter.nextCell(row.getToZip());
			exporter.nextCell(row.getEmail());
			exporter.nextCell(row.getOrderDate());
		}

		exporter.export(os);

	}

}
