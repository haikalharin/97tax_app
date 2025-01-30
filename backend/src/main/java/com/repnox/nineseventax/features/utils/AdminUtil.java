package com.repnox.nineseventax.features.utils;

import com.repnox.nineseventax.features.admin.StateAddress;
import com.repnox.nineseventax.features.ecommerce.OrderRecord;
import com.repnox.nineseventax.features.mailroom.model.Batch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.repnox.nineseventax.features.order.repo.FulfillmentOrder;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AdminUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AdminUtil.class);

    @Value("${mailroom.upload.path}")
    private String mailroomFilePath;

    public List<FulfillmentOrder>[] chunk(List<FulfillmentOrder> arrayToSplit, int chunkSize) {
        if (chunkSize <= 0) {
            return null;
        }
        int rest = arrayToSplit.size() % chunkSize;
        int chunks = arrayToSplit.size() / chunkSize + (rest > 0 ? 1 : 0);
        List<FulfillmentOrder>[] arrays = new List[chunks];
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            arrays[i] = arrayToSplit.subList(i * chunkSize, i * chunkSize + chunkSize);
        }
        if (rest > 0) {
            arrays[chunks - 1] = arrayToSplit.subList((chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }
        return arrays;
    }

    public void deleteFiles(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(mailroomFilePath + fileName + TaxConstants.PDF_EXTENSION));
            Files.deleteIfExists(Paths.get(mailroomFilePath + fileName + TaxConstants.CSV_EXTENSION));

        } catch (NoSuchFileException e) {
            LOG.error("No such file/directory exists", fileName, e);
        } catch (DirectoryNotEmptyException e) {
            LOG.error("Directory is not empty.", fileName, e);
        } catch (IOException e) {
            LOG.error("Invalid permissions.", fileName, e);
        }

        LOG.info("Deletion successful.", fileName);
    }

    public byte[] fileToByte(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            // read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            LOG.error("Failed to conver file to Bytes ", filePath, e);
            throw new RuntimeException(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LOG.error("Failed to close the stream fileToByte: ", filePath, e);
                }
            }
        }
        return bytesArray;
    }

    public InputStream Base64ToStream(String orignalBase64) {
        String base64 = orignalBase64.substring(orignalBase64.indexOf(",") + 1);
        InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64.getBytes()));
        return inputStream;
    }

    /**
     * @param batches
     * @return
     */
    public static ArrayList<Batch> convertBatch(List<Object[]> batches) {

        ArrayList<Batch> arrayList = new ArrayList<Batch>();

        for (Object[] object : batches) {
            Batch batch = new Batch();
            batch.setId(object[0] != null ? Long.valueOf(object[0].toString()) : null);
            batch.setCreatedAt(object[1] != null ? (Timestamp) object[1] : null);
            batch.setStatus(object[2] != null ? object[2].toString() : null);
            batch.setEndOrderNumber(object[3] != null ? Long.valueOf(object[3].toString()) : null);
            batch.setStartOrderNumber(object[4] != null ? Long.valueOf(object[4].toString()) : null);

            arrayList.add(batch);
        }

        return arrayList;
    }

    public StateAddress getStateAddress(FulfillmentOrder record) {

        if(FulfillmentOrder.ORDER_TYPE_PENATY_WAIVER.equals(record.getOrderType())) {
            return this.getStateAddress4Penalty(record);
        }

        String state = record.getShippingState();
        Boolean isCalifornia = record.getIsCalifornia();
        Boolean isNewJersey = record.getIsNewJersey();
        Boolean isGeorgia = record.getIsGeorgia();
        Boolean isIllinois = record.getIsIllinois();

        StateAddress stateAddress = new StateAddress();

        if (isCalifornia != null && isCalifornia) {
            stateAddress.setCompany(TaxConstants.STATE_OF_CALIFORNIA);
            stateAddress.setLine(TaxConstants.FRANCHISE_TAX_BOARD);
            stateAddress.setAddress("P.O. Box 2952");
            stateAddress.setCity("Sacramento");
            stateAddress.setState("CA");
            stateAddress.setZip("95812-2952");
        } else if (isNewJersey != null && isNewJersey) {
            stateAddress.setCompany(TaxConstants.NEW_JERSEY_DIVISION_OF_TAXATION);
            stateAddress.setLine(TaxConstants.PAYMENT_PLAN_UNIT);
            stateAddress.setAddress("P.O. Box 190");
            stateAddress.setCity("Trenton");
            stateAddress.setState("NJ");
            stateAddress.setZip("08695-0190");

        } else if (isGeorgia != null && isGeorgia) {
            stateAddress.setCompany(TaxConstants.GEORGIA_DEPARTMENT_OF_REVENUE);
            stateAddress.setLine(TaxConstants.PROCESSING_CENTER);
            stateAddress.setAddress("P.O. Box 105596");
            stateAddress.setCity("Atlanta");
            stateAddress.setState("GA");
            stateAddress.setZip("30374-0396");
        } else if (isIllinois != null && isIllinois) {
            stateAddress.setCompany(TaxConstants.ILLINOIS_DEPARTMENT_OF_REVENUE);
            stateAddress.setLine(TaxConstants.INSTALLMENT_CONTRACT_UNIT);
            stateAddress.setAddress("PO BOX 19035");
            stateAddress.setCity("Springfield");
            stateAddress.setState("IL");
            stateAddress.setZip("62794-9035");

        } else {

            stateAddress.setCompany(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
            stateAddress.setLine(TaxConstants.INTERNAL_REVENUE_SERVICE);

            if (StringUtils.isNotBlank(state)) {
                if ("PR".equals(state) || "AE".equals(state) || "AP".equals(state) || "AA".equals(state)) {
                    stateAddress.setAddress("3651 South Ih 35 5501AUSC");
                    stateAddress.setCity("Austin");
                    stateAddress.setState("TX");
                    stateAddress.setZip("78741-7855");

                } else if ("AK".equals(state) || "AZ".equals(state) || "CO".equals(state) || "CT".equals(state)
                        || "DE".equals(state) || "DC".equals(state) || "HI".equals(state) || "ID".equals(state)
                        || "IL".equals(state) || "ME".equals(state) || "MD".equals(state) || "MA".equals(state)
                        || "MT".equals(state) || "NV".equals(state) || "NH".equals(state) || "NJ".equals(state)
                        || "NM".equals(state) || "ND".equals(state) || "OR".equals(state) || "RI".equals(state)
                        || "SD".equals(state) || "TN".equals(state) || "UT".equals(state) || "VT".equals(state)
                        || "WA".equals(state) || "WI".equals(state) || "WY".equals(state)) {

                    stateAddress.setAddress("310 Lowell St. Stop 830");
                    stateAddress.setCity("Andover");
                    stateAddress.setState("MA");
                    stateAddress.setZip("01810-5430");

                } else if ("AL".equals(state) || "FL".equals(state) || "GA".equals(state) || "KY".equals(state)
                        || "LA".equals(state) || "MS".equals(state) || "NC".equals(state) || "SC".equals(state)
                        || "TX".equals(state) || "VA".equals(state)) {

                    stateAddress.setAddress("P.O. Box 47421");
                    stateAddress.setCity("Doraville");
                    stateAddress.setState("GA");
                    stateAddress.setZip("30362-0421");

                } else if ("AR".equals(state) || "CA".equals(state) || "IN".equals(state) || "IA".equals(state)
                        || "KS".equals(state) || "MI".equals(state) || "MN".equals(state) || "MO".equals(state)
                        || "NE".equals(state) || "NY".equals(state) || "OH".equals(state) || "OK".equals(state)
                        || "PA".equals(state) || "WV".equals(state)) {

                    stateAddress.setAddress("Stop P-4 5000");
                    stateAddress.setCity("Kansas City");
                    stateAddress.setState("MO");
                    stateAddress.setZip("64999-0250");

                } else {
                    stateAddress.setAddress("310 Lowell St. Stop 830");
                    stateAddress.setCity("Andover");
                    stateAddress.setState("MA");
                    stateAddress.setZip("01810-5430");
                }
            } else {

                stateAddress.setAddress("310 Lowell St. Stop 830");
                stateAddress.setCity("Andover");
                stateAddress.setState("MA");
                stateAddress.setZip("01810-5430");
            }
        }

        String aptUnit = record.getShippingAddress2();

        if (StringUtils.isNotBlank(aptUnit)) {
            if (aptUnit.startsWith("lot") || aptUnit.startsWith("suite") || aptUnit.startsWith("ste")
                    || aptUnit.startsWith("apt") || aptUnit.startsWith("spc") || aptUnit.startsWith("space")) {
                stateAddress.setShippingAddress2(aptUnit);
            } else {
                stateAddress.setShippingAddress2(aptUnit);
            }
        } else {
            stateAddress.setShippingAddress2("");
        }
        return stateAddress;
    }

    public StateAddress getStateAddress4Penalty(FulfillmentOrder record) {
        String billingState = record.getShippingState();
        StateAddress stateAddress = new StateAddress();

        stateAddress.setCompany(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
        stateAddress.setLine(TaxConstants.INTERNAL_REVENUE_SERVICE);

        if (StringUtils.isNotBlank(billingState)) {
            if("AR".equals(billingState) || "DE".equals(billingState) || "IL".equals(billingState) || "IN".equals(billingState) ||
                    "IA".equals(billingState) || "KY".equals(billingState) || "ME".equals(billingState) || "MA".equals(billingState) ||
                    "MN".equals(billingState) || "MO".equals(billingState) || "NH".equals(billingState) || "NJ".equals(billingState) ||
                    "NY".equals(billingState) || "OK".equals(billingState) || "VT".equals(billingState) || "VA".equals(billingState) ||
                    "WI".equals(billingState)
            ) {
                stateAddress.setAddress(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
                stateAddress.setShippingAddress2(TaxConstants.INTERNAL_REVENUE_SERVICE);
                stateAddress.setCity("Kansas");
                stateAddress.setState("MO");
                stateAddress.setZip("64999-0002");
            } else if("CT".equals(billingState) || "DC".equals(billingState) || "MD".equals(billingState) || "PA".equals(billingState) ||
                    "RI".equals(billingState) || "WV".equals(billingState)) {
                stateAddress.setAddress(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
                stateAddress.setShippingAddress2(TaxConstants.INTERNAL_REVENUE_SERVICE);
                stateAddress.setCity("Ogden");
                stateAddress.setState("UT");
                stateAddress.setZip("84201-0002");
            } else if("FL".equals(billingState) || "LA".equals(billingState) || "MS".equals(billingState) || "TX".equals(billingState)) {
                stateAddress.setAddress(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
                stateAddress.setShippingAddress2(TaxConstants.INTERNAL_REVENUE_SERVICE);
                stateAddress.setCity("Austin");
                stateAddress.setState("TX");
                stateAddress.setZip("73301-0002");
            } else if("AL".equals(billingState) || "GA".equals(billingState) || "NC".equals(billingState) || "SC".equals(billingState) || "TN".equals(billingState)) {
                stateAddress.setAddress(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
                stateAddress.setShippingAddress2(TaxConstants.INTERNAL_REVENUE_SERVICE);
                stateAddress.setCity("Austin");
                stateAddress.setState("TX");
                stateAddress.setZip("73301-0002");
            } else if("AL".equals(billingState) || "GA".equals(billingState) || "NC".equals(billingState) || "SC".equals(billingState) || "TN".equals(billingState)) {
                stateAddress.setAddress(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
                stateAddress.setShippingAddress2(TaxConstants.INTERNAL_REVENUE_SERVICE);
                stateAddress.setCity("Kansas");
                stateAddress.setState("MO");
                stateAddress.setZip("64999-0002");
            } else if("AK".equals(billingState) || "CA".equals(billingState) || "HI".equals(billingState) || "OH".equals(billingState) || "WA".equals(billingState)) {
                stateAddress.setAddress(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
                stateAddress.setShippingAddress2(TaxConstants.INTERNAL_REVENUE_SERVICE);
                stateAddress.setCity("Ogden");
                stateAddress.setState("UT");
                stateAddress.setZip("84201-0002");
            } else if("AZ".equals(billingState) || "CO".equals(billingState) || "ID".equals(billingState) || "KS".equals(billingState) || "MI".equals(billingState) ||
                    "MT".equals(billingState) || "NE".equals(billingState) || "NV".equals(billingState) || "NM".equals(billingState) || "OR".equals(billingState) ||
                    "ND".equals(billingState) || "SD".equals(billingState) || "UT".equals(billingState) || "WY".equals(billingState)) {
                stateAddress.setAddress(TaxConstants.DEPARTMENT_OF_THE_TREASURY);
                stateAddress.setShippingAddress2(TaxConstants.INTERNAL_REVENUE_SERVICE);
                stateAddress.setCity("Ogden");
                stateAddress.setState("UT");
                stateAddress.setZip("84201-0002");
            }
        } else {
            stateAddress.setAddress("310 Lowell St. Stop 830");
            stateAddress.setCity("Andover");
            stateAddress.setState("MA");
            stateAddress.setZip("01810-5430");
        }

        String aptUnit = record.getShippingAddress2();

        if (StringUtils.isNotBlank(aptUnit)) {
            if (aptUnit.startsWith("lot") || aptUnit.startsWith("suite") || aptUnit.startsWith("ste")
                    || aptUnit.startsWith("apt") || aptUnit.startsWith("spc") || aptUnit.startsWith("space")) {
                stateAddress.setShippingAddress2(aptUnit);
            } else {
                stateAddress.setShippingAddress2(aptUnit);
            }
        } else {
            stateAddress.setShippingAddress2("");
        }
        return stateAddress;
    }
}