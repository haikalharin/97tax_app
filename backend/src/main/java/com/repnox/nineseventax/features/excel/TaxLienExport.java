package com.repnox.nineseventax.features.excel;

import com.repnox.nineseventax.features.admin.OrderSpecs;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.ecommerce.OrderRecordRepo;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalDetails;
import com.repnox.nineseventax.features.taxlienremoval.TaxLienRemovalRepo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaxLienExport {

    private static final Logger LOG = LoggerFactory.getLogger(OfferInCompromiseExport.class);

    @Autowired
    private OrderRecordRepo orderRecordRepo;

    @Autowired
    private TaxLienRemovalRepo taxLienRemovalRepo;

    public void exportTaxLienCsv(OutputStream os, List<OrderRecord> orders) {
        TableExporter exporter = new CsvTableExport();
        exportTaxLienExcel(exporter, getTaxLienRows(orders), os);
    }

    public void exportTaxLienExcel(OutputStream os, List<OrderRecord> orders) {
        TableExporter exporter = new ExcelTableExporter("Orders");
        exportTaxLienExcel(exporter, getTaxLienRows(orders), os);
    }

    public List<TaxLienRow> getTaxLienRows(List<OrderRecord> records) {
        return records.stream().map((r) -> {
            TaxLienRow row = new TaxLienRow();
            row.setOrderRecord(r);
            row.setDetails(taxLienRemovalRepo.getByOrderNum(r.getOrderNum()));
            return row;
        }).collect(Collectors.toList());
    }

    public List<OrderRecord> findOrders() {
        return orderRecordRepo.findAll(OrderSpecs.pendingTaxLien());
    }

    public void exportTaxLienExcel(TableExporter exporter, List<TaxLienRow> rows, OutputStream os) {

        exporter.nextCell("Order");
        exporter.nextCell("Date");

        exporter.nextCell("First Name");
        exporter.nextCell("Last Name");
        exporter.nextCell("Email");

        exporter.nextCell("First and Last");
        exporter.nextCell("SSN or EIN");
        exporter.nextCell("Mail Address Line 1");
        exporter.nextCell("Mail Address Line 2");
        exporter.nextCell("Phone");

        exporter.nextCell("Is Business");

        exporter.nextCell("Business Name");
        exporter.nextCell("Mail Address City");
        exporter.nextCell("Mail Address State");
        exporter.nextCell("Mail Address Zip");

        exporter.nextCell("Billing Address Line 1");
        exporter.nextCell("Billing Address Line 2");
        exporter.nextCell("Billing Address City");
        exporter.nextCell("Billing Address State");
        exporter.nextCell("Billing Address Zip");

        exporter.nextCell("Tax Lien Notice");
        exporter.nextCell("Open");
        exporter.nextCell("Released");
        exporter.nextCell("On a payment plan");
        exporter.nextCell("Direct Debit");
        exporter.nextCell("Withdrawl will Facilitate");
        exporter.nextCell("Best Interest");
        exporter.nextCell("Explanation");
        exporter.nextCell("IRSFirst Line");
        exporter.nextCell("IRSAddress");
        exporter.nextCell("IRSAddress2");
        exporter.nextCell("IRScity");
        exporter.nextCell("IRSstate");
        exporter.nextCell("IRSzip");

        for (TaxLienRow row : rows) {
            exporter.nextRow();

            TaxLienRemovalDetails details = row.getDetails();
            if (details == null) {
                LOG.error("Skipping pending order with no Tax Lien Details: "+row.getOrderRecord().getOrderNum());
                continue;
            }
            OrderRecord record = row.getOrderRecord();

            exporter.nextCell(record.getOrderNum() != null ? record.getOrderNum().toString() : "");
            exporter.nextCell(defaultString(record.getCreatedDate()));

            exporter.nextCell(details.getFirstname());
            exporter.nextCell(details.getLastname());
            exporter.nextCell(details.getEmail());

            if (details.getTaxLienIsBusiness() == Boolean.TRUE) {
                exporter.nextCell(details.getTaxLienBusinessName());
            } else {
                exporter.nextCell(record.getFirstName() + " " + record.getLastName());
            }
            if (details.getTaxLienIsBusiness() == Boolean.TRUE) {
                exporter.nextCell(details.getTaxLienBusinessEin());
            } else {
                exporter.nextCell(record.getSsn());
            }
            if (StringUtils.isNotBlank(record.getShippingAddress2())) {
                exporter.nextCell(record.getShippingAddress1() + " " + record.getShippingAddress2());
            } else {
                exporter.nextCell(record.getShippingAddress1());
            }
            exporter.nextCell(record.getShippingAddress2());
            exporter.nextCell(details.getPhone());

            exporter.nextCell(convertBool(details.getTaxLienIsBusiness()));

            exporter.nextCell(details.getTaxLienBusinessName());
            exporter.nextCell(record.getShippingCity());
            exporter.nextCell(record.getShippingState());
            exporter.nextCell(record.getShippingZip());

            // Billing address
            if (StringUtils.isNotBlank(record.getBillingAddress1())) {
                exporter.nextCell(record.getBillingAddress1());
                exporter.nextCell(record.getBillingAddress2());
                exporter.nextCell(record.getBillingCity());
                exporter.nextCell(record.getBillingState());
                exporter.nextCell(record.getBillingZip());
            } else {
                exporter.nextCell(record.getShippingAddress1());
                exporter.nextCell(record.getShippingAddress2());
                exporter.nextCell(record.getShippingCity());
                exporter.nextCell(record.getShippingState());
                exporter.nextCell(record.getShippingZip());
            }

            boolean option1 = "currentPaymentPlan".equals(details.getTaxLienRemediationType());
            boolean option2 = "removeForSale".equals(details.getTaxLienRemediationType());
            boolean option3 = "removeForMoreIncome".equals(details.getTaxLienRemediationType());
            boolean option4 = "paidInFull".equals(details.getTaxLienRemediationType());
            boolean option5 = "needWithdrawnStatus".equals(details.getTaxLienRemediationType());
            boolean option6 = "agedTenYears".equals(details.getTaxLienRemediationType());

            // Tax Lien Notice
            exporter.nextCell(details.getSerialNumber());
            // Open
            exporter.nextCell(option1 || option2 || option3 ? "1" : "");
            // Released
            exporter.nextCell(option4 || option5 || option6 ? "1" : "");
            // On a payment plan
            exporter.nextCell(option1 ? "1" : "");
            // Direct Debit
            exporter.nextCell(Boolean.TRUE.equals(details.getTaxLienAutomaticDebit()) ? "1" : "");
            // Withdrawl will Facilitate
            exporter.nextCell(option2 || option3 ? "1" : "");
            // Best Interest
            exporter.nextCell(option4 || option5 || option6 ? "1" : "");

            // Explanation
            if (option2 || option3) {
                exporter.nextCell(details.getTaxLienRemediationDescription());
            } else if (option4 || option5) {
                exporter.nextCell("This lien has been satisfied in full. I hereby request that the IRS release this lien, thereby acting in the best interest of myself the taxpayer and the government.");
            } else if (option6) {
                exporter.nextCell("This lien is for tax debt incurred 10 or more years ago and the statute of limitations for collecting this debt has expired. I hereby request the IRS withdraw this lien and act in the best interest of myself the taxpayer and the government.");
            } else {
                exporter.nextCell("");
            }

            exporter.nextCell("Centralized Lien Operation");
            exporter.nextCell("P.O. Box 145595");
            exporter.nextCell("Stop 8420G");
            exporter.nextCell("Cincinnati");
            exporter.nextCell("OH");
            exporter.nextCell("45250-5595");

            // if ("serialNumber".equals(details.getTaxLienType())) {
            //     exporter.nextCell(details.getSerialNumber());
            // } else {
            //     exporter.nextCell("");
            // }

            // exporter.nextCell(convertBoolOne("currentPaymentPlan".equals(details.getTaxLienRemediationType())));
            // exporter.nextCell(convertBoolOne("removeForSale".equals(details.getTaxLienRemediationType())));
            // exporter.nextCell(convertBoolOne("removeForMoreIncome".equals(details.getTaxLienRemediationType())));
            // exporter.nextCell(convertBoolOne("paidInFull".equals(details.getTaxLienRemediationType())));
            // exporter.nextCell(convertBoolOne("needWithdrawnStatus".equals(details.getTaxLienRemediationType())));
            // exporter.nextCell(convertBoolOne("agedTenYears".equals(details.getTaxLienRemediationType())));
            // exporter.nextCell(details.getTaxLienRemediationDescription());
        }



        exporter.export(os);
    }

    private String defaultString(Date v) {
        return v != null ? new SimpleDateFormat("MM/dd/yyyy").format(v) : "";
    }

    private String convertBoolOne(Boolean b) {
        return b != null ? (b ? "1" : "") : "";
    }

    private String convertBool(Boolean b) {
        return b != null ? (b ? "Y" : "") : "";
    }

}
