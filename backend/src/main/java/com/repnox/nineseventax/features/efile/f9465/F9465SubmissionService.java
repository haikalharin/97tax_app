package com.repnox.nineseventax.features.efile.f9465;

import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.efile.f9465.model.IRS9465;
import com.repnox.nineseventax.features.efile.f9465.model.IRS9465Type.HomePhoneGrp;
import com.repnox.nineseventax.features.efile.f9465.model.Return;
import com.repnox.nineseventax.features.efile.f9465.model.ReturnData;
import com.repnox.nineseventax.features.efile.f9465.model.ReturnHeaderType;
import com.repnox.nineseventax.features.efile.f9465.model.ReturnHeaderType.Filer;
import com.repnox.nineseventax.features.efile.f9465.model.ReturnHeaderType.Form9465BusinessFilerGrp;
import com.repnox.nineseventax.features.efile.f9465.model.ReturnHeaderType.OriginatorGrp;
import com.repnox.nineseventax.features.efile.f9465.model.ReturnHeaderType.OriginatorGrp.PractitionerPINGrp;
import com.repnox.nineseventax.features.efile.model.IRSSubmissionManifest;
import com.repnox.nineseventax.features.paymentplans.PaymentPlanDetails;
import gov.irs.efile.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.repnox.nineseventax.features.efile.IRSSubmissionService.PRACTITIONER_PIN_CODE;
import static com.repnox.nineseventax.features.efile.util.EFileUtil.*;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Service
@Slf4j
public class F9465SubmissionService {

    @Value("${irs.efile.efin}")
    private String efin;

    @Value("${irs.efile.software-id}")
    private String softwareId;

    @Value("${irs.efile.etin}")
    private String etin;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssX");

    public String generateSubmission(OrderRecord order, PaymentPlanDetails details) throws Exception {
        Return request = new Return();
        request.setReturnVersion("CUv27.0");

        /* ReturnHeader begin */
        ReturnHeaderType header = new ReturnHeaderType();
        header.setBinaryAttachmentCnt(BigInteger.ZERO);

        Instant now = Instant.now();
        header.setReturnTs(dateTimeFormatter.withZone(ZoneId.from(ZoneOffset.UTC)).format(now));
        header.setConsortiumReturnCd(ConsortiumType.ENGLISH_FREE_FILE);
        header.setSoftwareId(softwareId);

        OriginatorGrp originatorGrp = new OriginatorGrp();
        originatorGrp.setEFIN(efin);
        originatorGrp.setOriginatorTypeCd(OriginatorType.ONLINE_FILER);
        PractitionerPINGrp practitionerPINGrp = new PractitionerPINGrp();
        practitionerPINGrp.setEFIN(efin);
//        practitionerPINGrp.setPIN(etin);
        practitionerPINGrp.setPIN(PRACTITIONER_PIN_CODE);
        originatorGrp.setPractitionerPINGrp(practitionerPINGrp);
        header.setOriginatorGrp(originatorGrp);

//        header.setPINTypeCd(PINCodeType.SELF_SELECT_PRACTITIONER);
        header.setPINTypeCd(PINCodeType.PRACTITIONER);
        header.setJuratDisclosureCd("FORM 9465");
        header.setPrimarySignaturePIN(PRACTITIONER_PIN_CODE);
        header.setPrimarySignatureDt(dateFormat.format(Date.from(now)));
        header.setReturnTypeCd("9465");

        /* Filer begin */
        Filer filer = new Filer();
        filer.setPrimarySSN(clearNonDigits(order.getSsn()));
        filer.setPrimaryNameControlTxt(getNameControl(order.getLastName()));
        PersonFullNameType person = new PersonFullNameType();
        person.setPersonFirstNm(order.getFirstName());
        person.setPersonLastNm(order.getLastName());
        filer.setPersonFullName(person);

        if (details.getMarried() != null && details.getMarried()) {
            filer.setSpouseSSN(clearNonDigits(details.getSpouseSsn()));
            filer.setPrimaryNameControlTxt(getNameControl(details.getSpouseLastName()));
            PersonFullNameType spouse = new PersonFullNameType();
            spouse.setPersonFirstNm(details.getSpouseFirstName());
            spouse.setPersonLastNm(details.getSpouseLastName());
            filer.setSpouseName(spouse);
        }

        USAddressType address = new USAddressType();
        address.setAddressLine1Txt(order.getShippingAddress1());
        address.setAddressLine2Txt(order.getShippingAddress2());
        address.setCityNm(order.getShippingCity());
        address.setZIPCd(order.getShippingZip());
        address.setStateAbbreviationCd(StateType.fromValue(order.getShippingState()));
        filer.setUSAddress(address);
        header.setFiler(filer);
        /* Filer end */

        if (order.getIsOwedFromBusiness() != null && order.getIsOwedFromBusiness()) {
            Form9465BusinessFilerGrp business = new Form9465BusinessFilerGrp();
            business.setBusinessNameLine1Txt(order.getBusinessName());
            business.setEIN(clearNonDigits(order.getEin()));
            header.setForm9465BusinessFilerGrp(business);
        }
        request.setReturnHeader(header);
        /* ReturnHeader end */

        /* ReturnData begin */
        ReturnData data = new ReturnData();
        data.setDocumentCnt(BigInteger.ONE);
        IRS9465 irs9465 = new IRS9465();
        irs9465.setDocumentId("DOC001");
        irs9465.setSoftwareId(softwareId);
        irs9465.setDocumentName("IRS9465");
        irs9465.getF9465TaxReturnTypeCd().add("FORM 1040");


        int year = Instant.now().atZone(ZoneId.of("America/New_York")).getYear();
        irs9465.setIATaxYrDt(String.valueOf(year - 1));
        /* TaxPeriodDetail begin */
//        LocalDate firstDay = LocalDate.now().minusYears(1).with(firstDayOfYear());
//        LocalDate lastDay = LocalDate.now().minusYears(1).with(lastDayOfYear());
//        TaxPeriodDetailGrp taxPeriod = new TaxPeriodDetailGrp();
//        taxPeriod.setTaxPeriodBeginDt(firstDay.format(dateFormatter));
//        taxPeriod.setTaxPeriodEndDt(lastDay.format(dateFormatter));
//        irs9465.setTaxPeriodDetailGrp(taxPeriod);
        /* TaxPeriodDetail end */

        irs9465.setPrimarySSN(clearNonDigits(order.getSsn()));
        irs9465.setPrimaryNameControlTxt(getNameControl(order.getLastName()));
        PersonFullNameType personData = new PersonFullNameType();
        personData.setPersonFirstNm(order.getFirstName());
        personData.setPersonLastNm(order.getLastName());
        irs9465.setPersonFullName(personData);

        if (details.getMarried() != null && details.getMarried()) {
            irs9465.setSpouseSSN(clearNonDigits(details.getSpouseSsn()));
            irs9465.setPrimaryNameControlTxt(getNameControl(details.getSpouseLastName()));
            PersonFullNameType spouseData = new PersonFullNameType();
            spouseData.setPersonFirstNm(details.getSpouseFirstName());
            spouseData.setPersonLastNm(details.getSpouseLastName());
            irs9465.setSpouseName(spouseData);
        }

        if (order.getHasOldAddress() != null && order.getHasOldAddress()) {
            irs9465.setNewAddressInd(CheckboxType.X);
            // TODO: Fill form 8821 with data from CHANGE_OF_ADDRESS_BLANK_PDF_PATH and order.getOldAddress1()
        }

        HomePhoneGrp phone = new HomePhoneGrp();
        phone.setPhoneNum(clearNonDigits(order.getPhone()));
        phone.setBestTimeToCallAtHomeTxt(details.getTimeToCall());
        irs9465.setHomePhoneGrp(phone);

        BigDecimal totalDebt = details.getTotalDebt();
        BigInteger total = totalDebt.toBigInteger();
        irs9465.setTaxDueAmt(total);
        irs9465.setTotalBalanceDueAmt(total);
        irs9465.setTotalTaxDueAmt(total);

        BigInteger monthlyAmount72 = totalDebt.divide(BigDecimal.valueOf(72), 2, RoundingMode.FLOOR).toBigInteger();
        BigInteger defaultAmount = BigInteger.valueOf(25);
        irs9465.setCalculatedMonthlyPymtAmt(monthlyAmount72.max(defaultAmount));
        irs9465.setRevisedMonthlyPaymentAmt(BigInteger.valueOf(details.getMonthlyPayment()));
        irs9465.setPaymentDueDayNum(Integer.parseInt(details.getPaymentDayOfMonth()));

        if (details.getMarried() != null && details.getMarried()) {
            irs9465.setMaritalStatusMarriedInd(CheckboxType.X);
        } else {
            irs9465.setMaritalStatusSingleInd(CheckboxType.X);
        }
        irs9465.setPrimaryPdOnceAMonthInd(CheckboxType.X);

        data.setIRS9465(irs9465);
        request.setReturnData(data);
        /* ReturnData end */

        JAXBContext context = JAXBContext.newInstance(Return.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        return fixNamespaces(writer.toString());
    }

public String generateManifest(OrderRecord order, String submissionId) throws Exception {
        IRSSubmissionManifest manifest = new IRSSubmissionManifest();
        manifest.setSubmissionId(submissionId);
        manifest.setEFIN(efin);
        manifest.setGovernmentCd(GovernmentCodeType.IRS);
        manifest.setFederalSubmissionTypeCd("9465");

        int year = Instant.now().atZone(ZoneId.of("America/New_York")).getYear();
        manifest.setTaxYr(String.valueOf(year - 1));
        LocalDate firstDay = LocalDate.now().minusYears(1).with(firstDayOfYear());
        LocalDate lastDay = LocalDate.now().minusYears(1).with(lastDayOfYear());
        manifest.setTaxPeriodBeginDt(firstDay.format(dateFormatter));
        manifest.setTaxPeriodEndDt(lastDay.format(dateFormatter));
        manifest.setTIN(clearNonDigits(order.getSsn()));

        JAXBContext context = JAXBContext.newInstance(IRSSubmissionManifest.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(manifest, writer);
        return fixNamespaces(writer.toString());
    }

}
