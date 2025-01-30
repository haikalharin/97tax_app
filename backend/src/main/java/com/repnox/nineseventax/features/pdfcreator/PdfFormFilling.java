package com.repnox.nineseventax.features.pdfcreator;

import com.repnox.nineseventax.features.ecommerce.OrderInfo;
import com.repnox.nineseventax.features.ecommerce.PartnerInfo;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import com.repnox.nineseventax.features.utils.DateUtil;
import com.repnox.nineseventax.features.utils.PdfFields;
import com.repnox.nineseventax.features.utils.TaxConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
public class PdfFormFilling {

    @Value("${mailroom.upload.path}")
    private String mailroomFilePath;

	private static final Logger LOG = LoggerFactory.getLogger(PdfFormFilling.class);

	public byte[] PdfCreator(OrderInfo orderInfo, PaymentPlanDetails plan) throws IOException {

		String pdfPath = null;
		boolean isCalifornia = BooleanUtils.isTrue(orderInfo.getIsCalifornia());
		boolean isNewJersey = BooleanUtils.isTrue(orderInfo.getIsNewJersey());
		boolean isGeorgia = BooleanUtils.isTrue(orderInfo.getIsGeorgia());
		boolean isIllinois = BooleanUtils.isTrue(orderInfo.getIsIllinois());
		boolean isMichigan = BooleanUtils.isTrue(orderInfo.getIsMichigan());

		Boolean isGreaterThan10k = plan.getTotalDebt().compareTo(BigDecimal.valueOf(10000))>0;
		Boolean isGreaterThan50k = plan.getTotalDebt().compareTo(BigDecimal.valueOf(50000))>0;
		Boolean isOwedForBusiness = orderInfo.getIsOwedFromBusiness();
		Boolean isPayrollDeduction = orderInfo.getPayrollDeduction();
		Boolean hasOldAddress = orderInfo.getHasOldAddress();

		if (isCalifornia) {
			pdfPath = TaxConstants.CALIFORNIA_PAYMENT_PLAN_BLANK_PDF_PATH;
		} else if (isMichigan) {
			pdfPath = TaxConstants.MICHIGAN_PAYMENT_PLAN_BLANK_PDF_PATH;
		} else if (isNewJersey) {
			if(isOwedForBusiness) {
				pdfPath = TaxConstants.NEW_JERSEY_PAYMENT_PLAN_BUSINESS_BLANK_PDF_PATH;
			}else {
				pdfPath = TaxConstants.NEW_JERSEY_PAYMENT_PLAN_INDIVIDUAL_BLANK_PDF_PATH;
			}
		} else if (isGeorgia) {
			pdfPath = TaxConstants.GEORGIA_PAYMENT_PLAN_BLANK_PDF_PATH;
		} else if (isIllinois) {
			if(!isGreaterThan10k) {
				pdfPath = TaxConstants.ILLINOIS_PAYMENT_PLAN_BLANK_PDF_PATH_LESS_THAN_10K;
			}else {
				if(isOwedForBusiness) {
					pdfPath = TaxConstants.ILLINOIS_PAYMENT_PLAN_BLANK_PDF_PATH_BUSINESS_OVER_10K;
				}else {
					pdfPath = TaxConstants.ILLINOIS_PAYMENT_PLAN_BLANK_PDF_PATH_INDIVIDUAL_OVER_10K;
				}

			}
		} else {
			if (!isGreaterThan50k) {
				pdfPath = TaxConstants.PAYMENT_PLAN_BLANK_PDF_PATH;
			} else {
				pdfPath = TaxConstants.PAYMENT_PLAN_BLANK_PDF_PATH_OVER_50K;
			}
		}

		try {
			PDDocument document = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(pdfPath));
			PDDocument payrollDeductionDocument = null;
			PDFMergerUtility pdfMerger = new PDFMergerUtility();

			InputStream payrollDeductionInputStream = null;
			LOG.info("User data" + orderInfo);

			PDDocument changeOfAddressDocument = null;
			InputStream changeOfAddressInputStream = null;

			PDDocumentOutline outline = new PDDocumentOutline();
			document.getDocumentCatalog().setDocumentOutline(outline);
			outline.addFirst(this.addBookmark(document,orderInfo.getOrderNumber()));

			PDDocumentCatalog docCatalog = document.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			String areaCode = null;
			String shippingPhone = orderInfo.getShippingPhone();
			if ((isIllinois || isCalifornia) && shippingPhone != null && shippingPhone.length() > 3) {
				areaCode = shippingPhone.substring(0, 3);
				shippingPhone = shippingPhone.substring(4);
			}
			String pattern = "MM/dd/yyyy";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String orderDate = simpleDateFormat.format(orderInfo.getSubmissionDate()!=null ? orderInfo.getSubmissionDate() :new java.util.Date(System.currentTimeMillis()));

			acroForm.setAppendOnly(true);

			if (isCalifornia) {

				COSName helvName = acroForm.getDefaultResources().add(PDType1Font.HELVETICA);

				getActionFormField(acroForm,"First Name").setValue(orderInfo.getBillingFirstName());
				getActionFormField(acroForm,"Last Name").setValue(orderInfo.getBillingLastName());
				getActionFormField(acroForm,"SSN").setValue(orderInfo.getSsn());
				getActionFormField(acroForm,"SpouseFirst").setValue(orderInfo.getSpouseFirstName());

				PDTextField lastSpouseNameField = (PDTextField) getActionFormField(acroForm,"SpouseLast");
				lastSpouseNameField.setDefaultAppearance("/" + helvName.getName() + " 10 Tf 0 g");

				getActionFormField(acroForm,"SpouseLast").setValue(orderInfo.getSpouseLastName());
				lastSpouseNameField.setReadOnly(true);

				getActionFormField(acroForm,"SpouseSSN").setValue(orderInfo.getSpouseSsn());
				getActionFormField(acroForm,"Street").setValue(orderInfo.getShippingAddress1());
				getActionFormField(acroForm,"APT").setValue(orderInfo.getShippingAddress2());
				getActionFormField(acroForm,"City").setValue(orderInfo.getShippingCity());
				getActionFormField(acroForm,"State").setValue(orderInfo.getShippingState());
				getActionFormField(acroForm,"Zip").setValue(orderInfo.getShippingPostalCode());
				getActionFormField(acroForm,"Phone").setValue(shippingPhone);
				getActionFormField(acroForm,"MonthlyPayment").setValue(plan.getMonthlyPayment().toString());
				getActionFormField(acroForm,"PaymentDate").setValue(orderInfo.getPaymentDayOfMonth());
				getActionFormField(acroForm,"FirstandLast").setValue(orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName());
				getActionFormField(acroForm,"AreaCode").setValue(areaCode);
				getActionFormField(acroForm,"FullPhone").setValue(orderInfo.getShippingPhone());
				getActionFormField(acroForm,"OrderDate").setValue(orderDate);
			} else if (isMichigan) {
				getActionFormField(acroForm,"Name and Address").setValue(orderInfo.getBillingLastName() + "," + orderInfo.getBillingFirstName());
				getActionFormField(acroForm,"Name").setValue(orderInfo.getBillingLastName() + "," + orderInfo.getBillingFirstName());
				getActionFormField(acroForm,"Michigan account number").setValue(orderInfo.getTreasuryAccountNumber());
				getActionFormField(acroForm,"Michigan Account Number").setValue(orderInfo.getTreasuryAccountNumber());
				if (StringUtils.equalsIgnoreCase("Corporation", orderInfo.getBusinessEntityType())) {
					getActionFormCheckbox(acroForm,"Business, Corporation").check();
				} else if (StringUtils.equalsIgnoreCase("Sole Proprietorship", orderInfo.getBusinessEntityType())) {
					getActionFormCheckbox(acroForm,"Business, Sole Proprietorship").check();
				} else if (StringUtils.equalsIgnoreCase("Partnership", orderInfo.getBusinessEntityType())) {
					getActionFormCheckbox(acroForm,"Business, Partnership").check();
				} else {
					getActionFormCheckbox(acroForm,"Individual").check();
				}
				Date currentDate = orderInfo.getSubmissionDate() != null ? orderInfo.getSubmissionDate() : new java.util.Date(System.currentTimeMillis());
				getActionFormField(acroForm,"Submission Date").setValue(String.valueOf(currentDate));
				getActionFormField(acroForm,"Social Security Number").setValue(orderInfo.getSsn());
				if (CollectionUtils.isNotEmpty(orderInfo.getAssessmentNumbers())) {
					for (int i = 1; i <= orderInfo.getAssessmentNumbers().size(); i++) {
						getActionFormField(acroForm, "Assessment number " + i).setValue(orderInfo.getAssessmentNumbers().get(i - 1));
					}
				}
				getActionFormField(acroForm,"Spouse Social Security Number").setValue(orderInfo.getSpouseSsn());
				getActionFormField(acroForm,"Address").setValue(orderInfo.getShippingAddress1() + ", " + orderInfo.getShippingAddress2());
				getActionFormField(acroForm,"City").setValue(orderInfo.getShippingCity());
				getActionFormField(acroForm,"State").setValue(orderInfo.getShippingState());
				getActionFormField(acroForm,"ZIP Code").setValue(orderInfo.getShippingPostalCode());
				getActionFormField(acroForm,"Phone").setValue(shippingPhone);
				getActionFormField(acroForm,"Secondary Phone").setValue(orderInfo.getSecondaryPhone());
				getActionFormField(acroForm,"monthly_payment").setValue(plan.getMonthlyPayment().toString());
				getActionFormField(acroForm,"Total Debt").setValue(orderInfo.getTotalDebt().toString());
				getActionFormField(acroForm,"Bank Name and Address").setValue(orderInfo.getBankName() + ", " +
						orderInfo.getBankAddress1() + ", " + orderInfo.getBankAddress2() + ", " + orderInfo.getBankCity() + ", " + orderInfo.getBankState() + ", " +
						orderInfo.getBankZip());
				getActionFormField(acroForm,"Employer Name and Address").setValue(orderInfo.getEmployerName() + ", " + orderInfo.getEmployerAddress1() + ", "
						+ orderInfo.getEmployerAddress2() + ", " + orderInfo.getEmployerCity() + ", " + orderInfo.getEmployerState() + ", " + orderInfo.getEmployerZip());
				if (CollectionUtils.isNotEmpty(orderInfo.getPartners())) {
					for (int i = 1; i <= orderInfo.getPartners().size(); i++) {
						PartnerInfo partnerInfo = orderInfo.getPartners().get(i - 1);
						getActionFormField(acroForm,"Partner " + i + " name and title").setValue(partnerInfo.getPartnerName() + " " + partnerInfo.getPartnerTitle());
						getActionFormField(acroForm,"Partner " + i + " effective date").setValue(partnerInfo.getPartnerEffectiveDay() + "-"
								+ partnerInfo.getPartnerEffectiveMonth() + "-" + partnerInfo.getPartnerEffectiveYear());
						getActionFormField(acroForm,"Partner " + i + " address").setValue(partnerInfo.getPartnerAddress1());
						getActionFormField(acroForm,"Partner " + i + " Phone").setValue(partnerInfo.getPartnerPhoneNumber());
						getActionFormField(acroForm,"Partner " + i + " ssn").setValue(partnerInfo.getPartnerSsn());
						getActionFormField(acroForm,"Partner " + i + " percent of ownership").setValue(partnerInfo.getPartnerPercentOwnership());
					}
				}
			} else if (isNewJersey) {
				getActionFormField(acroForm,"phone").setValue(shippingPhone);
				getActionFormField(acroForm,"secondaryPhone").setValue(orderInfo.getSecondaryPhone());
				getActionFormField(acroForm,"emailAddress").setValue(orderInfo.getEmail());
				getActionFormField(acroForm,"primarySocialSecurityNumber").setValue(orderInfo.getSsn());
				getActionFormField(acroForm,"balanceDue").setValue(orderInfo.getTotalDebt().toString());
				getActionFormField(acroForm,"requestedMonthlyPayment").setValue(plan.getMonthlyPayment().toString());
				getActionFormField(acroForm,"preferredMonthlyDueDate").setValue(orderInfo.getPaymentDayOfMonth());

				if (isOwedForBusiness != null && isOwedForBusiness == false) {
					getActionFormField(acroForm, "lastName").setValue(orderInfo.getBillingLastName());
					getActionFormField(acroForm, "firstName").setValue(orderInfo.getBillingFirstName());
					getActionFormField(acroForm, "address").setValue(orderInfo.getShippingAddress1());
					getActionFormField(acroForm, "apt").setValue(orderInfo.getShippingAddress2());
					getActionFormField(acroForm, "city").setValue(orderInfo.getShippingCity());
					getActionFormField(acroForm, "state").setValue(orderInfo.getShippingState());
					getActionFormField(acroForm, "zipCode").setValue(orderInfo.getShippingPostalCode());
				} else if (isOwedForBusiness != null && isOwedForBusiness == true) {
					getActionFormField(acroForm, "responsibleOfficer").setValue(orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName());
					getActionFormField(acroForm, "businessName").setValue(orderInfo.getBusinessName());
					getActionFormField(acroForm, "NJRegistrationFEIN").setValue(orderInfo.getEin());

					if(orderInfo.getPhysNotMailing() != null && orderInfo.getPhysNotMailing().equals(true)){
						getActionFormField(acroForm,"address").setValue(orderInfo.getBusinessAddress1());
						getActionFormField(acroForm,"apt").setValue(orderInfo.getBusinessAddress2());
						getActionFormField(acroForm,"city").setValue(orderInfo.getBusinessCity());
						getActionFormField(acroForm,"state").setValue(orderInfo.getBusinessState());
						getActionFormField(acroForm,"zipCode").setValue(orderInfo.getBusinessPostalCode());

						getActionFormField(acroForm, "address_2").setValue(orderInfo.getShippingAddress1());
						getActionFormField(acroForm, "apt_2").setValue(orderInfo.getShippingAddress2());
						getActionFormField(acroForm, "city_2").setValue(orderInfo.getShippingCity());
						getActionFormField(acroForm, "state_2").setValue(orderInfo.getShippingState());
						getActionFormField(acroForm, "zipCode_2").setValue(orderInfo.getShippingPostalCode());
					}
					else if (orderInfo.getPhysNotMailing() != null && orderInfo.getPhysNotMailing().equals(false)) {
						getActionFormField(acroForm,"address").setValue(orderInfo.getShippingAddress1());
						getActionFormField(acroForm,"apt").setValue(orderInfo.getShippingAddress2());
						getActionFormField(acroForm,"city").setValue(orderInfo.getShippingCity());
						getActionFormField(acroForm,"state").setValue(orderInfo.getShippingState());
						getActionFormField(acroForm,"zipCode").setValue(orderInfo.getShippingPostalCode());
					}
				}
			} else if (isGeorgia) {
				getActionFormField(acroForm, "firstname").setValue(orderInfo.getBillingFirstName());
				getActionFormField(acroForm, "lastname").setValue(orderInfo.getBillingLastName());
				getActionFormField(acroForm, "Social Security Number").setValue(orderInfo.getSsn());
				getActionFormField(acroForm, "spousefname").setValue(orderInfo.getSpouseFirstName());
				getActionFormField(acroForm, "spouselastname").setValue(orderInfo.getSpouseLastName());
				getActionFormField(acroForm, "Social Security Number_2").setValue(orderInfo.getSpouseSsn());
				getActionFormField(acroForm, "streetaddress").setValue(orderInfo.getShippingAddress1());
				getActionFormField(acroForm, "City").setValue(orderInfo.getShippingCity());
				getActionFormField(acroForm, "State").setValue(orderInfo.getShippingState());
				getActionFormField(acroForm, "ZIP").setValue(orderInfo.getShippingPostalCode());
				getActionFormField(acroForm, "Phone Number").setValue(shippingPhone);
				getActionFormField(acroForm, "totalowed").setValue(plan.getTotalDebt().toString());
				getActionFormField(acroForm, "number_of_months").setValue(plan.getPaymentMonths().toString());
				getActionFormField(acroForm, "payment_amount").setValue(plan.getMonthlyPayment().toString());
				getActionFormField(acroForm, "dayofmonth").setValue(orderInfo.getPaymentDayOfMonth());

				if (!orderInfo.getIsOwedFromBusiness()) {
					getActionFormCheckbox(acroForm, "individual_yesorno").check();
					String ssn = orderInfo.getSsn();
					if (!ssn.isEmpty()) {
						ssn = ssn.replaceAll("-", "");
						char[] ssnChars = ssn.toCharArray();
						for(int i = 0; i < 9; i++) {
							getActionFormField(acroForm, "SSN" + (i + 1)).setValue(String.valueOf(ssnChars[i]));
						}
					}
				} else {
					getActionFormCheckbox(acroForm, "businessyesorno").check();
					getActionFormField(acroForm, "businessname").setValue(orderInfo.getBusinessName());
					getActionFormField(acroForm, "EINnumber").setValue(orderInfo.getEin());

					String ein = orderInfo.getEin() != null ? orderInfo.getEin() :"";
					if (ein != null && !ein.isEmpty()) {
						ein = ein.replaceAll("-", "");
						char[] einChars = ein.toCharArray();
						for (int i = 0; i < 9; i++) {
							getActionFormField(acroForm, "EIN" + (i + 1)).setValue(String.valueOf(einChars[i]));
						}
					}
				}
			} else if (isIllinois) {

				String ssn = orderInfo.getSsn() != null ? orderInfo.getSsn(): "" ;
				String ssn1 = "",ssn2 = "",ssn3 = "";
				if (!ssn.isEmpty()) {
					ssn = ssn.replaceAll("-", "");
					char[] ssnChars = ssn.toCharArray();
					ssn1 = new String(ssnChars,0,3);
					ssn2 = new String(ssnChars,3,2);
					ssn3 = new String(ssnChars,5,4);
					getActionFormField(acroForm, "ssn1").setValue(ssn1);
					getActionFormField(acroForm, "ssn2").setValue(ssn2);
					getActionFormField(acroForm, "ssn3").setValue(ssn3);
				}
				String spouse_ssn = orderInfo.getSpouseSsn() != null ? orderInfo.getSpouseSsn() :"";
				if (!spouse_ssn.isEmpty()) {
					spouse_ssn = spouse_ssn.replaceAll("-", "");
					char[] ssnChars = spouse_ssn.toCharArray();
					String spouse_ssn_1 = new String(ssnChars,0,3);
					String spouse_ssn_2 = new String(ssnChars,3,2);
					String spouse_ssn_3 = new String(ssnChars,5,4);
					getActionFormField(acroForm, "ssn4").setValue(spouse_ssn_1);
					getActionFormField(acroForm, "ssn5").setValue(spouse_ssn_2);
					getActionFormField(acroForm, "ssn6").setValue(spouse_ssn_3);
				}

				getActionFormField(acroForm, "name1").setValue(orderInfo.getBillingFirstName());
				getActionFormField(acroForm, "last1").setValue(orderInfo.getBillingLastName());
				getActionFormField(acroForm, "name2").setValue(orderInfo.getSpouseFirstName());
				getActionFormField(acroForm, "last2").setValue(orderInfo.getSpouseLastName());


				String streetAddress = orderInfo.getShippingAddress1() + " " + orderInfo.getShippingAddress2();
				getActionFormField(acroForm, "address1").setValue(streetAddress);
				getActionFormField(acroForm, "city1").setValue(orderInfo.getShippingCity());
				getActionFormField(acroForm, "state1").setValue(orderInfo.getShippingState());
				getActionFormField(acroForm, "zip1").setValue(orderInfo.getShippingPostalCode());
				getActionFormField(acroForm, "name6").setValue(orderInfo.getEmail());
				getActionFormField(acroForm, "area1").setValue(areaCode);
				getActionFormField(acroForm, "phone2").setValue(shippingPhone);
				getActionFormField(acroForm, "amt").setValue(plan.getMonthlyPayment().toString());
				if(plan.getGoodFaithPayment() != null) {
					getActionFormField(acroForm, "downpayment").setValue(plan.getGoodFaithPayment().toString());
					getActionFormField(acroForm, "good_faith_payment2").setValue(plan.getGoodFaithPayment().toString());

					BigDecimal remainingAmount=(plan.getTotalDebt().subtract(BigDecimal.valueOf(plan.getGoodFaithPayment())));
					getActionFormField(acroForm, "liability").setValue(remainingAmount.toString());
				}

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Calendar calender = Calendar.getInstance();
				try {
					calender.setTime(sdf.parse(orderDate));
				}
				catch(Exception e) {
					LOG.error("Fail to parse orderDate " + orderInfo.getOrderNumber() , e);
				}

				Integer orderYear = calender.get(Calendar.YEAR);
				Integer month = calender.get(Calendar.MONTH)+1;
				Integer day = calender.get(Calendar.DAY_OF_MONTH);
				String orderMonth = month.toString().length() == 1 ? "0".concat(month.toString()) : month.toString();
				String orderDay = day.toString().length() == 1 ? "0".concat(day.toString()) : day.toString();

				calender.add(Calendar.DATE,30);
				Integer orderInstallmentYear = calender.get(Calendar.YEAR);
				Integer installmentMonth = calender.get(Calendar.MONTH)+1;
				Integer installmentDay = calender.get(Calendar.DAY_OF_MONTH);
				String orderInstallmentMonth = installmentMonth.toString().length() == 1 ? "0".concat(installmentMonth.toString()) : installmentMonth.toString();
				String orderInstallmentDay = installmentDay.toString().length() == 1 ? "0".concat(installmentDay.toString()) : installmentDay.toString();


				getActionFormField(acroForm, "day1").setValue(orderInstallmentDay);
				getActionFormField(acroForm, "mo1").setValue(orderInstallmentMonth);
				getActionFormField(acroForm, "yr1").setValue(orderInstallmentYear.toString());
				getActionFormField(acroForm, "day2").setValue(orderDay);
				getActionFormField(acroForm, "mo2").setValue(orderMonth);
				getActionFormField(acroForm, "yr2").setValue(orderYear.toString());
				getActionFormCheckbox(acroForm, "CheckBox1").check();
				getActionFormField(acroForm, "datemo").setValue(plan.getPaymentDayOfMonth());
				if(isGreaterThan10k) {
					String fullName = orderInfo.getBillingFirstName()+" "+orderInfo.getBillingLastName();
					if (!isOwedForBusiness) {
						getActionFormField(acroForm, "fullName2").setValue(fullName);
						getActionFormField(acroForm, "street_address2").setValue(streetAddress);
						getActionFormField(acroForm, "city3").setValue(orderInfo.getShippingCity());
						getActionFormField(acroForm, "state3").setValue(orderInfo.getShippingState());
						getActionFormField(acroForm, "zip3").setValue(orderInfo.getShippingPostalCode());
						getActionFormField(acroForm, "email2").setValue(orderInfo.getEmail());
						getActionFormField(acroForm, "area_code3").setValue(areaCode);
						getActionFormField(acroForm, "phone3").setValue(shippingPhone);
						getActionFormField(acroForm, "ssn_debtor_1").setValue(ssn1);
						getActionFormField(acroForm, "ssn_debtor_2").setValue(ssn2);
						getActionFormField(acroForm, "ssn_debtor_3").setValue(ssn3);

					} else {
						String ein = orderInfo.getEin() != null ? orderInfo.getEin() : "";
						if (!ein.isEmpty()) {
							ein = ein.replaceAll("-", "");
							char[] einChars = ein.toCharArray();
							String ein1 = new String(einChars,0,2);
							String ein2 = new String(einChars,2,7);
							getActionFormField(acroForm, "fein1").setValue(ein1);
							getActionFormField(acroForm, "fein2").setValue(ein2);
							getActionFormField(acroForm, "fein3").setValue(ein1);
							getActionFormField(acroForm, "fein4").setValue(ein2);
						}

						String illinoisAccountIdString = orderInfo.getIllinoisAccountId() != null ? orderInfo.getIllinoisAccountId() : "" ;
						if (!illinoisAccountIdString.isEmpty()) {
							illinoisAccountIdString = illinoisAccountIdString.replaceAll("-", "");
							char[] illinoisAccountIdChars = illinoisAccountIdString.toCharArray();
							String ein1 = new String(illinoisAccountIdChars,0,4);
							String ein2 = new String(illinoisAccountIdChars,4,4);
							getActionFormField(acroForm, "ibt1").setValue(ein1);
							getActionFormField(acroForm, "ibt2").setValue(ein2);
						}

						getActionFormField(acroForm, "LegalName").setValue(orderInfo.getBusinessName());
						getActionFormField(acroForm, "name3").setValue(orderInfo.getDba());
						getActionFormField(acroForm, "address2").setValue(streetAddress);
						getActionFormField(acroForm, "city2").setValue(orderInfo.getShippingCity());
						getActionFormField(acroForm, "state").setValue(orderInfo.getShippingState());
						getActionFormField(acroForm, "zip2").setValue(orderInfo.getShippingPostalCode());
						getActionFormField(acroForm, "responsible").setValue(fullName);
						getActionFormField(acroForm, "area4").setValue(areaCode);
						getActionFormField(acroForm, "phone").setValue(shippingPhone);

						if (!illinoisAccountIdString.isEmpty()) {
							getActionFormField(acroForm, "illinois_account_id").setValue(illinoisAccountIdString);
						}
						getActionFormField(acroForm, "businessName2").setValue(orderInfo.getBusinessName());
						getActionFormField(acroForm, "city3").setValue(orderInfo.getShippingCity());
						getActionFormField(acroForm, "zip3").setValue(orderInfo.getShippingPostalCode());
						getActionFormField(acroForm, "state3").setValue(orderInfo.getShippingState());
						getActionFormField(acroForm, "area_code3").setValue(areaCode);
						getActionFormField(acroForm, "phone3").setValue(shippingPhone);
						getActionFormField(acroForm, "business_street_address2").setValue(streetAddress);
					}
				} else {
					String fullName = orderInfo.getBillingFirstName()+" "+orderInfo.getBillingLastName();
					if (isOwedForBusiness != null && isOwedForBusiness) {
						String ein = orderInfo.getEin() != null ? orderInfo.getEin() : "";
						if (!ein.isEmpty()) {
							ein = ein.replaceAll("-", "");
							char[] einChars = ein.toCharArray();
							String ein1 = new String(einChars,0,2);
							String ein2 = new String(einChars,2,7);
							getActionFormField(acroForm, "fein1").setValue(ein1);
							getActionFormField(acroForm, "fein2").setValue(ein2);
						}

						String illinoisAccountIdString = orderInfo.getIllinoisAccountId() != null ? orderInfo.getIllinoisAccountId() : "" ;
						if (!illinoisAccountIdString.isEmpty()) {
							illinoisAccountIdString = illinoisAccountIdString.replaceAll("-", "");
							char[] illinoisAccountIdChars = illinoisAccountIdString.toCharArray();
							String ein1 = new String(illinoisAccountIdChars,0,4);
							String ein2 = new String(illinoisAccountIdChars,4,4);
							getActionFormField(acroForm, "ibt1").setValue(ein1);
							getActionFormField(acroForm, "ibt2").setValue(ein2);
						}

						getActionFormField(acroForm, "LegalName").setValue(orderInfo.getBusinessName());
						getActionFormField(acroForm, "name3").setValue(orderInfo.getDba());
						getActionFormField(acroForm, "address2").setValue(orderInfo.getShippingAddress1());
						getActionFormField(acroForm, "city2").setValue(orderInfo.getShippingCity());
						getActionFormField(acroForm, "state").setValue(orderInfo.getShippingState());
						getActionFormField(acroForm, "zip2").setValue(orderInfo.getShippingPostalCode());
						getActionFormField(acroForm, "responsible").setValue(fullName);
						getActionFormField(acroForm, "area4").setValue(areaCode);
						getActionFormField(acroForm, "phone").setValue(shippingPhone);
					}
				}
			} else {
				if (!isGreaterThan50k) {
					getActionFormField(acroForm,"fname").setValue(orderInfo.getBillingFirstName());
					getActionFormField(acroForm,"lname").setValue(orderInfo.getBillingLastName());
					getActionFormField(acroForm,"sfname").setValue(orderInfo.getSpouseFirstName());
					getActionFormField(acroForm,"slname").setValue(orderInfo.getSpouseLastName());

					if (StringUtils.isNoneBlank((orderInfo.getShippingAddress1()))) {
						getActionFormField(acroForm,"address1").setValue(orderInfo.getShippingAddress1());
					}

					getActionFormField(acroForm,"aptnumber").setValue(orderInfo.getShippingAddress2());

					if (StringUtils.isNoneBlank(orderInfo.getShippingCity())) {
						getActionFormField(acroForm,"address2").setValue(orderInfo.getShippingCity() + ", " + orderInfo.getShippingState() + ", "
						+ orderInfo.getShippingPostalCode());
					}

					getActionFormField(acroForm,"ssecuritynumber").setValue(orderInfo.getSsn());
					getActionFormField(acroForm,"spousessecuritynumber").setValue(orderInfo.getSpouseSsn());
					getActionFormField(acroForm,"homecontactnumber").setValue(orderInfo.getShippingPhone());
					getActionFormField(acroForm,"timetocall").setValue(orderInfo.getTimeToCall());

					double doubleNumber = Double.parseDouble(plan.getTotalDebt().toString());
					String doubleAsString = String.valueOf(doubleNumber);
					int indexOfDecimal = doubleAsString.indexOf(".");
					String totalDebit = "";
					if(indexOfDecimal == -1) {
						totalDebit = doubleAsString.concat(".00");
					}else if(plan.getTotalDebt() != null && plan.getTotalDebt().toString().length() == indexOfDecimal) {
						totalDebit = doubleAsString.substring(0, indexOfDecimal).concat(".00");
					}else {
						String decimalAfterDot = doubleAsString.substring(indexOfDecimal).replace(".", "");
						totalDebit = doubleAsString.substring(0, indexOfDecimal).concat(".").concat(decimalAfterDot);
					}

					getActionFormField(acroForm,"col5amount").setValue(totalDebit);
					getActionFormField(acroForm,"col7amount").setValue(totalDebit);
					getActionFormField(acroForm,"col9amount").setValue(totalDebit);

					String monthlyAmmount72 = String.valueOf((int) doubleNumber / 72);
					String defaultPrice="25";

					getActionFormField(acroForm,"col10amount").setValue(Integer.parseInt(monthlyAmmount72) < 25  ? defaultPrice:monthlyAmmount72);
					getActionFormField(acroForm,"col11amount").setValue(String.valueOf(plan.getMonthlyPayment()));

					getActionFormField(acroForm,"datefield12").setValue(orderInfo.getPaymentDayOfMonth());
					if (orderInfo.getIsOwedFromBusiness() != null && orderInfo.getIsOwedFromBusiness()) {
						getActionFormField(acroForm, "businessname").setValue(orderInfo.getBusinessName());
						getActionFormField(acroForm, "emplyeeno").setValue(orderInfo.getEin());
					}
					if(isPayrollDeduction !=null && isPayrollDeduction) {
						payrollDeductionDocument = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(TaxConstants.PAYROLL_DEDUCTION_PDF_PATH));
						PDDocumentOutline payrollDeductionOutline = new PDDocumentOutline();
						payrollDeductionDocument.getDocumentCatalog().setDocumentOutline(payrollDeductionOutline);
						payrollDeductionOutline.addFirst(this.addBookmark(payrollDeductionDocument,orderInfo.getOrderNumber()));

						PDDocumentCatalog payrollDeductionDocCatalog = payrollDeductionDocument.getDocumentCatalog();
						PDAcroForm payrollDeductionAcroForm = payrollDeductionDocCatalog.getAcroForm();


						payrollDeductionAcroForm.setAppendOnly(true);
						String toEmployerNameAddress = orderInfo.getEmployerName() + ", " + orderInfo.getEmployerAddress1() + " " + orderInfo.getEmployerAddress2() + ", "
								+ orderInfo.getEmployerCity() + ", " + orderInfo.getEmployerState() + ", " + orderInfo.getEmployerZip();
						getActionFormField(payrollDeductionAcroForm,"ToEmployerNameAddress").setValue(toEmployerNameAddress);
						String regardingTaxpayerNameAddress = orderInfo.getShippingFirstName() + " " + orderInfo.getShippingLastName() + ", " + orderInfo.getShippingAddress1() + " "
								+ orderInfo.getShippingAddress2() + ", " + orderInfo.getShippingCity() + ", " + orderInfo.getShippingState() + ", " + orderInfo.getShippingPostalCode();
						getActionFormField(payrollDeductionAcroForm,"RegardingTaxpayerNameAddress").setValue(regardingTaxpayerNameAddress);
						getActionFormField(payrollDeductionAcroForm,"ContactPersonName").setValue(orderInfo.getEmployerContactName());
						getActionFormField(payrollDeductionAcroForm,"ContactPersonTelephone").setValue(orderInfo.getEmployerContactPhoneNumber());
						getActionFormField(payrollDeductionAcroForm,"TaxpayerSSNEIN").setValue(orderInfo.getSsn());
						String spouseSsn = orderInfo.getSpouseSsn() != null ? orderInfo.getSpouseSsn() :"";
						if (!spouseSsn.isEmpty()) {
							spouseSsn = spouseSsn.replaceAll("-", "");
							char[] ssnChars = spouseSsn.toCharArray();
							String spouse_ssn_3 = new String(ssnChars,5,4);
							getActionFormField(payrollDeductionAcroForm,"SpouseSSNEIN").setValue(spouse_ssn_3);
						}

						double calculatePayment = 0.00;
						if(orderInfo.getPayFrequency() == 0) {
							getActionFormCheckbox(payrollDeductionAcroForm, "CB_05").check();
							double number = plan.getMonthlyPayment() / 4.0;
							calculatePayment  = (double) Math.round(number * 100) / 100;
						}
						else if(orderInfo.getPayFrequency() == 1) {
							getActionFormCheckbox(payrollDeductionAcroForm, "CB_06").check();
							double number = plan.getMonthlyPayment() / 2.0;
							calculatePayment  = (double) Math.round(number * 100) / 100;
						}
						else if(orderInfo.getPayFrequency() == 2)
							getActionFormCheckbox(payrollDeductionAcroForm, "CB_07").check();
						else
							getActionFormCheckbox(payrollDeductionAcroForm, "CB_08").check();

						getActionFormField(payrollDeductionAcroForm,"OrderDate").setValue(orderDate);
						getActionFormField(payrollDeductionAcroForm,"AmountOwedDollars").setValue(orderInfo.getTotalDebt().toString());
						String monthlyPayment  = calculatePayment != 0.00 ? String.valueOf(calculatePayment): plan.getMonthlyPayment().toString();
						getActionFormField(payrollDeductionAcroForm, "MonthlyPayment").setValue(monthlyPayment);

						Date currentDate = orderInfo.getSubmissionDate()!=null ? orderInfo.getSubmissionDate() : new java.util.Date(System.currentTimeMillis());
						Calendar c = Calendar.getInstance();
						c.setTime(currentDate);
						c.add(Calendar.DATE, 60);
						String OrderDatePlus60 = simpleDateFormat.format(c.getTime());
						getActionFormField(payrollDeductionAcroForm,"OrderDatePlus60").setValue(OrderDatePlus60);
						getActionFormField(payrollDeductionAcroForm,"SignatureDate").setValue(orderDate);
						getActionFormField(payrollDeductionAcroForm,"SpouseSignatureDate").setValue(orderDate);

						payrollDeductionAcroForm.flatten();

						ByteArrayOutputStream payrollDeductionOutputStream = new ByteArrayOutputStream();

						payrollDeductionDocument.save(payrollDeductionOutputStream);
						payrollDeductionInputStream = new ByteArrayInputStream(payrollDeductionOutputStream.toByteArray());
					}
					if (hasOldAddress != null && hasOldAddress) {
						changeOfAddressDocument = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(TaxConstants.CHANGE_OF_ADDRESS_BLANK_PDF_PATH));
						PDDocumentOutline changeOfAddressOutline = new PDDocumentOutline();
						changeOfAddressDocument.getDocumentCatalog().setDocumentOutline(changeOfAddressOutline);
						changeOfAddressOutline.addFirst(this.addBookmark(changeOfAddressDocument,orderInfo.getOrderNumber()));

						PDDocumentCatalog changeOfAddressDocCatalog = changeOfAddressDocument.getDocumentCatalog();
						PDAcroForm changeOfAddressAcroForm = changeOfAddressDocCatalog.getAcroForm();

						String name = orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName();
						getActionFormField(changeOfAddressAcroForm, "fname + lname").setValue(name);
						getActionFormField(changeOfAddressAcroForm, "ssn").setValue(orderInfo.getSsn());
						String spouseName = orderInfo.getSpouseFirstName() + " " + orderInfo.getSpouseLastName();
						getActionFormField(changeOfAddressAcroForm, "spouse_first_name + spouse_last_name").setValue(spouseName);
						getActionFormField(changeOfAddressAcroForm, "spouse_ssn").setValue(orderInfo.getSpouseSsn());
						getActionFormField(changeOfAddressAcroForm, "prev_fname1 + prev_lname1, prev_fname2 + prev_lname2").setValue(orderInfo.getPriorNames());
						String oldAddress = orderInfo.getOldAddress1() + " " + orderInfo.getOldAddress2() + " " + orderInfo.getOldCity() + ", " + orderInfo.getOldState() + " " + orderInfo.getOldZip();
						getActionFormField(changeOfAddressAcroForm, "old_shipaddr1 old_shipaddr2 old_shipcity, old_shipstate old_shipzip").setValue(oldAddress);
						String newAddress = orderInfo.getShippingAddress1() + " " + orderInfo.getShippingAddress2() + " " + orderInfo.getShippingCity() + ", " + orderInfo.getShippingState() + " " + orderInfo.getShippingPostalCode();
						getActionFormField(changeOfAddressAcroForm, "new_shipadd1 new_shipadd2 new_shipcity, new_shipstate new_shipzip").setValue(newAddress);
						getActionFormField(changeOfAddressAcroForm, "Phone").setValue(orderInfo.getBillingPhone());
						getActionFormField(changeOfAddressAcroForm, "order_date").setValue(orderDate);

						changeOfAddressAcroForm.flatten();

						ByteArrayOutputStream changeOfAddressOutputStream = new ByteArrayOutputStream();

						changeOfAddressDocument.save(changeOfAddressOutputStream);
						changeOfAddressInputStream = new ByteArrayInputStream(changeOfAddressOutputStream.toByteArray());
					}
				} else {

					String name = orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName();
					String shippingAddress = orderInfo.getShippingAddress1() + " " + orderInfo.getShippingAddress2();
					String cityInfo = orderInfo.getShippingCity() + ", " + orderInfo.getShippingState() + ", " + orderInfo.getShippingPostalCode();

					getActionFormField(acroForm,"NamesAndAddress1").setValue(name);
					getActionFormField(acroForm,"NamesAndAddress2").setValue(shippingAddress);
					getActionFormField(acroForm,"NamesAndAddress3").setValue(cityInfo);

					getActionFormField(acroForm, "SSN").setValue(orderInfo.getSsn());
					getActionFormField(acroForm, "Spouse SSN").setValue(orderInfo.getSpouseSsn());
					getActionFormField(acroForm, "Phone").setValue(orderInfo.getShippingPhone());
					getActionFormField(acroForm, "Name of Business").setValue(orderInfo.getBusinessName());
					getActionFormField(acroForm, "Business EIN").setValue(orderInfo.getEin());
				}
			}

			acroForm.flatten();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			document.save(byteArrayOutputStream);
			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

			document.close();
			if(isPayrollDeduction!=null && isPayrollDeduction && payrollDeductionDocument!= null) {
				payrollDeductionDocument.close();
			}
			if (hasOldAddress != null && hasOldAddress && changeOfAddressInputStream != null) {
				changeOfAddressDocument.close();
			}

			if(isPayrollDeduction!=null && isPayrollDeduction && payrollDeductionInputStream != null) {
				String uniqueFilename = UUID.randomUUID().toString();
				String filePath = this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION;
				InputStream payrollDeductionAgreementInputStream= Thread.currentThread().getContextClassLoader().getResourceAsStream(TaxConstants.PAYROLL_DEDUCTION_AGREEMENT_PDF_PATH);
				pdfMerger.setDestinationFileName(filePath);
				pdfMerger.addSource(inputStream);
				pdfMerger.addSource(payrollDeductionAgreementInputStream);
				pdfMerger.addSource(payrollDeductionInputStream);
				if ( hasOldAddress != null && hasOldAddress && changeOfAddressInputStream != null ) {
					pdfMerger.addSource(changeOfAddressInputStream);
				}
				pdfMerger.mergeDocuments(null);
				byte[] bytes = Files.readAllBytes(Paths.get(filePath));
				Files.deleteIfExists(Paths.get(mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION));
				return bytes;
			} else {
				if ( hasOldAddress != null && hasOldAddress && changeOfAddressInputStream != null ) {
					String uniqueFilename = UUID.randomUUID().toString();
					String filePath = this.mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION;
					pdfMerger.setDestinationFileName(filePath);
					pdfMerger.addSource(inputStream);
					pdfMerger.addSource(changeOfAddressInputStream);
					pdfMerger.mergeDocuments(null);
					byte[] bytes = Files.readAllBytes(Paths.get(filePath));
					Files.deleteIfExists(Paths.get(mailroomFilePath + uniqueFilename + TaxConstants.PDF_EXTENSION));
					return bytes;
				} else {
					byte[] bytes = IOUtils.toByteArray(inputStream);
					return bytes;
				}
			}

		} catch (Exception e) {
			LOG.error("Fail to Autofill payment plan PDF file " + orderInfo.getOrderNumber() , e);
			throw e;
		}
	}

	public String PdfCreatorForPenaltyWaiverLetter(PenaltyOrder penaltyOrderInfo) throws IOException {

		String penaltyWaiverLetterPath = TaxConstants.PENALTY_WAIVER_LETTER;

		try {
			PDDocument document_penaltyWaiverLetter = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(penaltyWaiverLetterPath));

			LOG.info("User data" + penaltyOrderInfo);

			PDDocumentOutline outline = new PDDocumentOutline();
			document_penaltyWaiverLetter.getDocumentCatalog().setDocumentOutline(outline);
			outline.addFirst(this.addBookmark(document_penaltyWaiverLetter, String.valueOf(penaltyOrderInfo.getId())));

			PDDocumentCatalog docCatalog = document_penaltyWaiverLetter.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			acroForm.setAppendOnly(true);

			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.DATE).setValue(DateUtil.getTodayMMDDYYYY());
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.IRS_PW_STREET1).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractIRSAddressLine1(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.FIRST_NAME).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractFirstName(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.LAST_NAME).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractLastName(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.SSN).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractSSN(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.SPOUSE_FIRST_NAME).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractSpouseFirstName(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.SPOUSE_LAST_NAME).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractSpouseLastName(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.SPOUSE_SSN).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractSpouseSSN(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.CITY_STATE_ZIP).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractCityStateZip(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.ADDRESS1_ADDRESS2).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractAddressLine1(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.PENALTY_TAX_YEAR).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractYear(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.PENALTY_TYPE).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractPenaltyType(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.IRS_CITY_STATE_ZIP).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractIRSCityStateZip(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.PenaltyWaiverFillableLetter.PHONE_NUMBER).setValue(PdfFields.PenaltyWaiverFillableLetter.Extractor.extractPhone(penaltyOrderInfo));

			acroForm.flatten();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			document_penaltyWaiverLetter.save(byteArrayOutputStream);
			// InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			String filename = "./PWL_" + penaltyOrderInfo.getId() + ".pdf";
			document_penaltyWaiverLetter.save(new File(filename)); //TODO: remove this localcopying after demo
			document_penaltyWaiverLetter.close();

			//return IOUtils.toByteArray(inputStream);
			return filename;

		} catch (Exception e) {
			LOG.error("Fail to Autofill payment plan PDF file " + penaltyOrderInfo.getId() , e);
			throw e;
		}
	}

	public String PdfCreatorForF843(PenaltyOrder penaltyOrderInfo) throws IOException {

		String f843Path = TaxConstants.F_843;

		try {
			PDDocument document_f843 = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(f843Path));

			LOG.info("User data" + penaltyOrderInfo);

			PDDocumentOutline outline = new PDDocumentOutline();
			document_f843.getDocumentCatalog().setDocumentOutline(outline);
			outline.addFirst(this.addBookmark(document_f843, String.valueOf(penaltyOrderInfo.getId())));

			PDDocumentCatalog docCatalog = document_f843.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			String orderDate = DateUtil.getTodayMMDDYYYY();
			acroForm.setAppendOnly(true);

			getActionFormField(acroForm, PdfFields.F843.NAME_AND_SPOUSE_NAME).setValue(PdfFields.F843.Extractor.extractNameAndSpouseName(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.SSN).setValue(PdfFields.F843.Extractor.extractSSN(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.ADDRESS).setValue(PdfFields.F843.Extractor.extractAddress(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.SPOUSE_SSN).setValue(PdfFields.F843.Extractor.extractSpouseSSN(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.CITY_TOWN_STATE_PIN).setValue(PdfFields.F843.Extractor.extractCityTownStatePin(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.TELEPHONE).setValue(PdfFields.F843.Extractor.extractTelephone(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.YEAR_START).setValue(PdfFields.F843.Extractor.extractYearStart(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.YEAR_END).setValue(PdfFields.F843.Extractor.extractYearEnd(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.REFUND_AMOUNT).setValue(PdfFields.F843.Extractor.extractRefundAmount(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.IRC_XXXX).setValue(PdfFields.F843.Extractor.extractIRCxxxx(penaltyOrderInfo));
			getActionFormField(acroForm, PdfFields.F843.DATE1).setValue(orderDate);
			getActionFormField(acroForm, PdfFields.F843.DATE2).setValue(orderDate);

			acroForm.flatten();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			document_f843.save(byteArrayOutputStream);
			String filename = "./f843_" + penaltyOrderInfo.getId() + ".pdf";
			//InputStream inputStream = new ByteArrayInputSream(byteArrayOutputStream.toByteArray());
			document_f843.save(new File(filename)); //TODO: remove this local copying process after demo
			document_f843.close();

			//return IOUtils.toByteArray(inputStream);

			return filename;

		} catch (Exception e) {
			LOG.error("Fail to Autofill payment plan PDF file " + penaltyOrderInfo.getId() , e);
			throw e;
		}
	}

	@Deprecated
	public InputStream PdfCreatorForF8821(EinOrder order, boolean helpBubbles) throws IOException {
		String f8821Path = helpBubbles ? TaxConstants.F_8821_BUBBLES : TaxConstants.F_8821;

		try {
			PDDocument document_f8821 = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(f8821Path));

			LOG.info("User data" + order);

			PDDocumentOutline outline = new PDDocumentOutline();
			document_f8821.getDocumentCatalog().setDocumentOutline(outline);
			outline.addFirst(this.addBookmark(document_f8821, String.valueOf(order.getId())));

			PDDocumentCatalog docCatalog = document_f8821.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			acroForm.setAppendOnly(true);

			PDVariableText nameAndAddress = (PDVariableText) getActionFormField(acroForm, PdfFields.F8821.NAME_AND_ADDRESS);
			nameAndAddress.setDefaultAppearance("/Helv 8 Tf 0 g"); // Workaround: changed font size to fit 3 lines in the input
			nameAndAddress.setValue(PdfFields.F8821.Extractor.extractNameAndAddress(order));

			getActionFormField(acroForm, PdfFields.F8821.SSN).setValue(PdfFields.F8821.Extractor.extractSSN(order));
			getActionFormField(acroForm, PdfFields.F8821.PHONE_NUMBER).setValue(PdfFields.F8821.Extractor.extractPhoneNumber(order));
			getActionFormField(acroForm, PdfFields.F8821.DATE).setValue(DateUtil.getTodayMMDDYYYY());
			getActionFormField(acroForm, PdfFields.F8821.SIGNATURE_NAME).setValue(PdfFields.F8821.Extractor.extractSignatureName(order));

			acroForm.flatten();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document_f8821.save(byteArrayOutputStream);

			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			return inputStream;
		} catch (Exception e) {
			LOG.error("Fail to Autofill Form 8821 PDF file for EIN Order " + order.getId(), e);
			throw e;
		}
	}

	public InputStream PdfCreatorForFSS4(EinOrder order, boolean helpBubbles) throws IOException {
		String fss4Path = helpBubbles ? TaxConstants.F_SS4_BUBBLES : TaxConstants.F_SS4_SIGNATURE_API;

		try {
			PDDocument document_fss4 = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fss4Path));

			LOG.info("User data" + order);

			PDDocumentOutline outline = new PDDocumentOutline();
			document_fss4.getDocumentCatalog().setDocumentOutline(outline);
			outline.addFirst(this.addBookmark(document_fss4, String.valueOf(order.getId())));

			PDDocumentCatalog docCatalog = document_fss4.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			acroForm.setAppendOnly(true);

			getActionFormField(acroForm, PdfFields.FSS4.LEGAL_NAME_1).setValue(PdfFields.FSS4.Extractor.extractLegalName(order));

			boolean field3Applicable = "Trust".equals(order.getOrder_type()) ||
					("Estate".equals(order.getOrder_type()) && !"Personal Representative".equals(order.getTitle()));
			if (field3Applicable) {
				getActionFormField(acroForm, PdfFields.FSS4.EXECUTOR_ADMINISTRATOR_TRUSTEE_3)
						.setValue(PdfFields.FSS4.Extractor.extractName(order));
			}

			getActionFormField(acroForm, PdfFields.FSS4.MAILING_ADDRESS_4A).setValue(PdfFields.FSS4.Extractor.extractMailingAddress(order));
			getActionFormField(acroForm, PdfFields.FSS4.MAILING_CITY_STATE_ZIP_4B).setValue(PdfFields.FSS4.Extractor.extractMailingCityStateZip(order));
			getActionFormField(acroForm, PdfFields.FSS4.COUNTY_STATE_6).setValue(PdfFields.FSS4.Extractor.extractCountyState(order));
			getActionFormField(acroForm, PdfFields.FSS4.NAME_7A).setValue(PdfFields.FSS4.Extractor.extractName(order));
			getActionFormField(acroForm, PdfFields.FSS4.SSN_7B).setValue(PdfFields.FSS4.Extractor.extractSSN(order));

			if (order.getIs_diff_mailing_address() != null && order.getIs_diff_mailing_address() == 1) {
				getActionFormField(acroForm, PdfFields.FSS4.ADDRESS_5A).setValue(PdfFields.FSS4.Extractor.extractAddress(order));
				getActionFormField(acroForm, PdfFields.FSS4.CITY_STATE_ZIP_5B).setValue(PdfFields.FSS4.Extractor.extractCityStateZip(order));
			}

			if ("LLC".equals(order.getOrder_type())) {
				PDCheckBox llcYesCheckbox = (PDCheckBox) getActionFormField(acroForm, PdfFields.FSS4.LLC_8A_YES);
				llcYesCheckbox.check();

				PDCheckBox llcUSYesCheckbox = (PDCheckBox) getActionFormField(acroForm, PdfFields.FSS4.LLC_US_8C_YES);
				llcUSYesCheckbox.check();

				getActionFormField(acroForm, PdfFields.FSS4.LLC_MEMBERS_8B).setValue(PdfFields.FSS4.Extractor.extractLLCMembers(order));
			} else {
				PDCheckBox llcNoCheckbox = (PDCheckBox) getActionFormField(acroForm, PdfFields.FSS4.LLC_8A_NO);
				llcNoCheckbox.check();

				if ("Corporation".equals(order.getOrder_type())) {
					getActionFormField(acroForm, PdfFields.FSS4.CORPORATION_STATE_9B).setValue(PdfFields.FSS4.Extractor.extractStateIncorporated(order));
				}
			}

			String entityTypeCheckboxID = PdfFields.FSS4.Extractor.entityTypeCheckbox(order);
			if (null != entityTypeCheckboxID) {
				PDCheckBox entityTypeCheckbox = (PDCheckBox) getActionFormField(acroForm, entityTypeCheckboxID);
				entityTypeCheckbox.check();
			}
			String entityTypeSSNInput = PdfFields.FSS4.Extractor.entityTypeSSNInput(order);
			if (null != entityTypeSSNInput) {
				getActionFormField(acroForm, entityTypeSSNInput).setValue(PdfFields.FSS4.Extractor.extractSSN(order));
			}

			boolean reasonNotApplicable = "Estate".equals(order.getOrder_type()) ||
					("Corporation".equals(order.getOrder_type()) && "Settlement Fund".equals(order.getSub_type())) ||
					("Trust".equals(order.getOrder_type()) && !"Escrow".equals(order.getSub_type()));
			if (!reasonNotApplicable) {
				String reasonCheckboxID = PdfFields.FSS4.Extractor.reasonCheckbox(order);
				PDCheckBox reasonCheckbox = (PDCheckBox) getActionFormField(acroForm, reasonCheckboxID);
				reasonCheckbox.check();
				if ("Banking Purposes".equals(order.getReason())) {
					getActionFormField(acroForm, PdfFields.FSS4.REASON_BANKING_PURPOSE_DETAILS_INPUT_10).setValue("new account");
				}
			}

			getActionFormField(acroForm, PdfFields.FSS4.DATE_BUSINESS_STARTED_11).setValue(PdfFields.FSS4.Extractor.extractDateBusinessStarted(order));
			getActionFormField(acroForm, PdfFields.FSS4.CLOSING_MONTH_12).setValue(PdfFields.FSS4.Extractor.extractClosingMonth(order));

			getActionFormField(acroForm, PdfFields.FSS4.EMPLOYEES_AGRICULTURAL_13).setValue(PdfFields.FSS4.Extractor.extractEmployeesAgricultural(order));
			getActionFormField(acroForm, PdfFields.FSS4.EMPLOYEES_HOUSEHOLD_13).setValue(PdfFields.FSS4.Extractor.extractEmployeesHousehold(order));
			getActionFormField(acroForm, PdfFields.FSS4.EMPLOYEES_OTHER_13).setValue(PdfFields.FSS4.Extractor.extractEmployeesOther(order));
			boolean isLiability1000 = PdfFields.FSS4.Extractor.isLiability1000(order);
			if (isLiability1000) {
				PDCheckBox liabilityCheckbox = (PDCheckBox) getActionFormField(acroForm, PdfFields.FSS4.LIABILITY_EXPECT_LESS_1000_14);
				liabilityCheckbox.check();
			}

			boolean activityNotApplicable = "Estate".equals(order.getOrder_type()) ||
					"Trust".equals(order.getOrder_type());
			if (!activityNotApplicable) {
				PDCheckBox activityCheckbox = (PDCheckBox) getActionFormField(acroForm, PdfFields.FSS4.Extractor.activityCheckbox(order));
				activityCheckbox.check();
				getActionFormField(acroForm, PdfFields.FSS4.ACTIVITY_OTHER_SPECIFY_16).setValue(PdfFields.FSS4.Extractor.otherActivityDetails(order));
				getActionFormField(acroForm, PdfFields.FSS4.ACTIVITY_ADDITIONAL_DETAILS_17).setValue(PdfFields.FSS4.Extractor.extractAdditionalDetails(order));
			}

			if (order.getIs_previous_ein() == null || !order.getIs_previous_ein()) {
				PDCheckBox noPreviousEINCheckbox = (PDCheckBox) getActionFormField(acroForm, PdfFields.FSS4.PREVIOUS_EIN_NO_18);
				noPreviousEINCheckbox.check();
			} else {
				PDCheckBox havePreviousEINCheckbox = (PDCheckBox) getActionFormField(acroForm, PdfFields.FSS4.PREVIOUS_EIN_YES_18);
				havePreviousEINCheckbox.check();

				getActionFormField(acroForm, PdfFields.FSS4.PREVIOUS_EIN_TEXT_18).setValue(PdfFields.FSS4.Extractor.extractPreviousEIN(order));
			}

			getActionFormField(acroForm, PdfFields.FSS4.TPD_NAME).setValue("Joseph Sortor");
			getActionFormField(acroForm, PdfFields.FSS4.TPD_ADDRESS_ZIP).setValue("1044 E BRANDON BLVD, BRANDON, FL 33511");
			getActionFormField(acroForm, PdfFields.FSS4.TPD_PHONE_NUMBER).setValue("813-853-0140");

			getActionFormField(acroForm, PdfFields.FSS4.DATE).setValue(DateUtil.getTodayMMDDYYYY());
			getActionFormField(acroForm, PdfFields.FSS4.PHONE_NUMBER).setValue(PdfFields.FSS4.Extractor.extractPhoneNumber(order));
			getActionFormField(acroForm, PdfFields.FSS4.SIGNATURE_NAME).setValue(PdfFields.FSS4.Extractor.extractSignatureName(order));

			acroForm.flatten();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document_fss4.save(byteArrayOutputStream);

			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			return inputStream;
		} catch (Exception e) {
			LOG.error("Fail to Autofill Form SS4 PDF file for EIN Order " + order.getId(), e);
			throw e;
		}
	}

	private PDField getActionFormField(PDAcroForm acroForm, String fieldName){
		return acroForm.getField(fieldName);
	}
	private PDCheckBox getActionFormCheckbox(PDAcroForm acroForm,String fieldName){
		return (PDCheckBox) acroForm.getField(fieldName);
	}

	public byte[] TaxLienPdfCreator(OrderInfo orderInfo) throws IOException {

		// Creating PDF document object
		try(PDDocument document = PDDocument.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(TaxConstants.TAX_LIEN_BLANK_PDF_PATH))){

			System.out.println(" ======= TaxLienPdfCreator file @@@@ ====== ");

			PDDocumentOutline outline = new PDDocumentOutline();
    		document.getDocumentCatalog().setDocumentOutline( outline );
    		outline.addFirst(this.addBookmark(document,orderInfo.getOrderNumber()));

			PDDocumentCatalog docCatalog = document.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			getActionFormField(acroForm,"FillText2").setValue(orderInfo.getTaxLienIsBusiness() ? orderInfo.getTaxLienBusinessName() : orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName());
			getActionFormField(acroForm,"FillText3").setValue(StringUtils.isNoneBlank(orderInfo.getSsn()) ? orderInfo.getSsn() : orderInfo.getTaxLienBusinessEin());
			getActionFormField(acroForm,"FillText5").setValue(
					orderInfo.getBillingAddress1() + (StringUtils.isNotBlank(orderInfo.getBillingAddress2())
							? orderInfo.getBillingAddress2()
							: ""));
			getActionFormField(acroForm,"FillText6").setValue(orderInfo.getBillingCity());
			getActionFormField(acroForm,"FillText7").setValue(orderInfo.getBillingState());
			getActionFormField(acroForm,"FillText8").setValue(orderInfo.getBillingPostalCode());
			getActionFormField(acroForm,"FillText9").setValue(orderInfo.getBillingPhone());
			getActionFormField(acroForm,"FillText10").setValue(orderInfo.getSerialNumber());


		if( orderInfo.getTaxLienAutomaticDebit() != null && orderInfo.getTaxLienRemediationType() != null &&  orderInfo.getTaxLienAutomaticDebit() && orderInfo.getTaxLienRemediationType().equals("currentPaymentPlan")) {
			getActionFormCheckbox(acroForm,"CheckBox1").check();
			getActionFormCheckbox(acroForm,"CheckBox5").check();
			getActionFormCheckbox(acroForm,"CheckBox8").check();

		}else if ( orderInfo.getTaxLienAutomaticDebit() != null && orderInfo.getTaxLienRemediationType() != null &&  !orderInfo.getTaxLienAutomaticDebit() && orderInfo.getTaxLienRemediationType().equals("currentPaymentPlan")){
			getActionFormCheckbox(acroForm,"CheckBox1").check();
			getActionFormCheckbox(acroForm,"CheckBox5").check();
		}
		else if(orderInfo.getTaxLienRemediationType() != null &&  orderInfo.getTaxLienRemediationType().equals(TaxConstants.TAX_LIEN_OPTION2)){
			getActionFormCheckbox(acroForm,"CheckBox1").check();
			getActionFormCheckbox(acroForm,"CheckBox6").check();

		}
		else if (orderInfo.getTaxLienRemediationType() !=null && orderInfo.getTaxLienRemediationType().equals(TaxConstants.TAX_LIEN_OPTION3)){
			getActionFormCheckbox(acroForm,"CheckBox1").check();
			getActionFormCheckbox(acroForm,"CheckBox6").check();
		}
		else {
			getActionFormCheckbox(acroForm,"CheckBox2").check();
			getActionFormCheckbox(acroForm,"CheckBox7").check();
			}

			if(orderInfo.getTaxLienRemediationType() != null && orderInfo.getTaxLienRemediationType().equals(TaxConstants.TAX_LIEN_OPTION2) || orderInfo.getTaxLienRemediationType().equals(TaxConstants.TAX_LIEN_OPTION3)){
				getActionFormField(acroForm,"FillText13").setValue(orderInfo.getTaxLienRemediationDescription());
			}else if (orderInfo.getTaxLienRemediationType()!= null && orderInfo.getTaxLienRemediationType().equals(TaxConstants.TAX_LIEN_OPTION4) || orderInfo.getTaxLienRemediationType().equals(TaxConstants.TAX_LIEN_OPTION5)){
				getActionFormField(acroForm,"FillText13").setValue(TaxConstants.TAX_LIEN_OPTION_4_5);
			}else if (orderInfo.getTaxLienRemediationType() != null && orderInfo.getTaxLienRemediationType().equals(TaxConstants.TAX_LIEN_OPTION6)){
				getActionFormField(acroForm,"FillText13").setValue(TaxConstants.TAX_LIEN_OPTION_6);
			}

			acroForm.flatten();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document.save(byteArrayOutputStream);

			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			byte[] bytes = IOUtils.toByteArray(inputStream);

			return bytes;
		} catch (Exception e) {
			LOG.error("Fail to Autofill Tax Lien PDF file " + orderInfo.getOrderNumber() , e);
			throw e;
		}

	}

	public PDOutlineItem addBookmark(PDDocument document, String orderNumber) {
		PDOutlineItem root = new PDOutlineItem();
		root.setTitle(orderNumber + TaxConstants.PDF_EXTENSION);
		root.setDestination(document.getPage(0));
		return root;
	}

}
