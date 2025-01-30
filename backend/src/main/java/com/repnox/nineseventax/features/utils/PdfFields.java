package com.repnox.nineseventax.features.utils;

import com.repnox.nineseventax.features.ein.EINBusinessSubType;
import com.repnox.nineseventax.features.ein.EINBusinessType;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PdfFields {
    public static class F843 {
        public static String NAME_AND_SPOUSE_NAME = "name-and-spouse-name";
        public static String SSN = "ssn";
        public static String ADDRESS = "address";
        public static String SPOUSE_SSN = "spouse-ssn";
        public static String CITY_TOWN_STATE_PIN = "city-town-state-pin";
        public static String TELEPHONE = "telephone";
        public static String YEAR_START = "year-start";
        public static String YEAR_END = "year-end";
        public static String REFUND_AMOUNT = "refund-amount";
        public static String IRC_XXXX = "irc-xxxx";
        public static String DATE1 = "date1";
        public static String DATE2 = "date2";
        public static class Extractor{
            private static final String FAILURE_TO_PAY = "IRC § 6651";
            private static final String FAILURE_TO_DEPOSIT = "IRC § 6656";
            private static final String FAILURE_TO_FILE_TIMELY = "IRC § 6698";
            public static String extractNameAndSpouseName(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getFirstName()+" "+penaltyOrder.getLastName();
                // TODO: append the Spouse details once available.
            }
            public static String extractSSN(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getSsn();
            }
            public static String extractAddress(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getShippingAddress1()+" "+penaltyOrder.getShippingAddress2();
            }
            public static String extractSpouseSSN(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getSpouseSsn();
                // TODO: replace with spouse ssn once available.
            }
            public static String extractCityTownStatePin(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getShippingCity()+", "+ penaltyOrder.getShippingState()+" "+ penaltyOrder.getShippingPostalCode();
            }
            public static String extractTelephone(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getBillingPhone();
            }
            public static String extractYearStart(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getPenaltyTaxYear();
            }
            public static String extractYearEnd(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getPenaltyTaxYear();
            }
            public static String extractRefundAmount(final PenaltyOrder penaltyOrder){
                return String.valueOf(penaltyOrder.extractHighestPenalty());
            }
            public static String extractIRCxxxx(final PenaltyOrder penaltyOrder){
                String penaltyType = penaltyOrder.getPenaltyType();

                if(PenaltyOrder.STR_FTFP.equals(penaltyType))
                    return FAILURE_TO_FILE_TIMELY;
                else if(PenaltyOrder.STR_FTPP.equals(penaltyType))
                    return FAILURE_TO_PAY;
                else if(PenaltyOrder.STR_FTDP.equals(penaltyType))
                    return FAILURE_TO_DEPOSIT;

                return "";
            }
        }
    }

    public static class F8821 {
        public static String NAME_AND_ADDRESS = "entity_name_address";
        public static String SSN = "entity_ssn";
        public static String PHONE_NUMBER = "entity_phone_number";
        public static String DATE = "current_date";
        public static String SIGNATURE_NAME = "signature_name";

        public static class Extractor {
            public static String extractNameAndAddress(final EinOrder order) {
                return order.getFirst_name() + " " + order.getMiddle_name() + " " + order.getLast_name() + " " + StringUtils.trimToEmpty(order.getSuffix()) + "\n" +
                        order.getAddress() + (StringUtils.isBlank(order.getApt_suite()) ? "" : " " + order.getApt_suite()) + "\n" +
                        order.getCity() + ", " + order.getState() + " " + order.getZip_code();
            }
            public static String extractSSN(final EinOrder order) {
                return order.getSsn();
            }
            public static String extractPhoneNumber(final EinOrder order) {
                return order.getPhone_number();
            }
            public static String extractSignatureName(final EinOrder order) {
                return order.getFirst_name() + " " + order.getMiddle_name() + " " + order.getLast_name() + " " + StringUtils.trimToEmpty(order.getSuffix());
            }
        }
    }

    public static class FSS4 {
        public static String LEGAL_NAME_1 = "topmostSubform[0].Page1[0].f1_2[0]";
        public static String EXECUTOR_ADMINISTRATOR_TRUSTEE_3 = "topmostSubform[0].Page1[0].f1_4[0]";
        public static String MAILING_ADDRESS_4A = "topmostSubform[0].Page1[0].Line4ReadOrder[0].f1_5[0]";
        public static String MAILING_CITY_STATE_ZIP_4B = "topmostSubform[0].Page1[0].Line4ReadOrder[0].f1_6[0]";
        public static String ADDRESS_5A = "topmostSubform[0].Page1[0].f1_7[0]";
        public static String CITY_STATE_ZIP_5B = "topmostSubform[0].Page1[0].f1_8[0]";
        public static String COUNTY_STATE_6 = "topmostSubform[0].Page1[0].f1_9[0]";
        public static String NAME_7A = "topmostSubform[0].Page1[0].f1_10[0]";
        public static String SSN_7B = "topmostSubform[0].Page1[0].f1_11[0]";

        public static String LLC_8A_YES = "topmostSubform[0].Page1[0].c1_1[0]";
        public static String LLC_8A_NO = "topmostSubform[0].Page1[0].c1_1[1]";
        public static String LLC_MEMBERS_8B = "topmostSubform[0].Page1[0].f1_12[0]";
        public static String LLC_US_8C_YES = "topmostSubform[0].Page1[0].c1_2[0]"; // Always checked
        public static String ENTITY_TYPE_SOLE_PROPRIETOR_9A = "topmostSubform[0].Page1[0].c1_3[0]";
        public static String ENTITY_TYPE_PARTNERSHIP_9A = "topmostSubform[0].Page1[0].c1_3[2]";
        public static String ENTITY_TYPE_CORPORATION_9A = "topmostSubform[0].Page1[0].c1_3[4]";
        public static String ENTITY_TYPE_SOLE_PROPRIETOR_SSN_INPUT_9A = "topmostSubform[0].Page1[0].f1_13[0]";
        public static String ENTITY_TYPE_ESTATE_9A = "topmostSubform[0].Page1[0].c1_3[1]";
        public static String ENTITY_TYPE_ESTATE_SSN_INPUT_9A = "topmostSubform[0].Page1[0].f1_14[0]";
        public static String ENTITY_TYPE_TRUST_9A = "topmostSubform[0].Page1[0].c1_3[5]";
        public static String ENTITY_TYPE_TRUST_SSN_INPUT_9A = "topmostSubform[0].Page1[0].f1_17[0]";

        public static String CORPORATION_STATE_9B = "topmostSubform[0].Page1[0].f1_21[0]";
        public static String CORPORATION_COUNTRY_9B = "topmostSubform[0].Page1[0].f1_22[0]";

        public static String REASON_STARTED_NEW_BUSINESS_10 = "topmostSubform[0].Page1[0].c1_4[1]";
        public static String REASON_HIRED_EMPLOYEES_10 = "topmostSubform[0].Page1[0].c1_4[4]";
        public static String REASON_BANKING_PURPOSE_10 = "topmostSubform[0].Page1[0].c1_4[0]";
        public static String REASON_CHANGED_TYPE_OF_ORGANIZATION_10 = "topmostSubform[0].Page1[0].c1_4[2]";
        public static String REASON_PURCHASED_GOING_BUSINESS_10 = "topmostSubform[0].Page1[0].c1_4[3]";
        public static String REASON_BANKING_PURPOSE_DETAILS_INPUT_10 = "topmostSubform[0].Page1[0].f1_24[0]";

        public static String DATE_BUSINESS_STARTED_11 = "topmostSubform[0].Page1[0].f1_31[0]";
        public static String CLOSING_MONTH_12 = "topmostSubform[0].Page1[0].f1_32[0]";

        public static String EMPLOYEES_AGRICULTURAL_13 = "topmostSubform[0].Page1[0].f1_33[0]";
        public static String EMPLOYEES_HOUSEHOLD_13 = "topmostSubform[0].Page1[0].f1_34[0]";
        public static String EMPLOYEES_OTHER_13 = "topmostSubform[0].Page1[0].f1_35[0]";
        public static String LIABILITY_EXPECT_LESS_1000_14 = "topmostSubform[0].Page1[0].c1_5[0]";

        public static String ACTIVITY_HEALTHCARE_AND_SOCIAL_ASSISTANCE_16 = "topmostSubform[0].Page1[0].c1_6[0]";
        public static String ACTIVITY_WHOLESALE_AGENT_BROKER_16 = "topmostSubform[0].Page1[0].c1_6[1]";
        public static String ACTIVITY_CONSTRUCTION_16 = "topmostSubform[0].Page1[0].c1_6[2]";
        public static String ACTIVITY_RENTAL_AND_LEASING_16 = "topmostSubform[0].Page1[0].c1_6[3]";
        public static String ACTIVITY_TRANSPORTATION_AND_WAREHOUSING_16 = "topmostSubform[0].Page1[0].c1_6[4]";
        public static String ACTIVITY_ACCOMMODATION_AND_FOOD_SERVICE_16 = "topmostSubform[0].Page1[0].c1_6[5]";
        public static String ACTIVITY_RETAIL_16 = "topmostSubform[0].Page1[0].c1_6[7]";
        public static String ACTIVITY_REAL_ESTATE_16 = "topmostSubform[0].Page1[0].c1_6[8]";
        public static String ACTIVITY_MANUFACTURING_16 = "topmostSubform[0].Page1[0].c1_6[9]";
        public static String ACTIVITY_FINANCE_AND_INSURANCE_16 = "topmostSubform[0].Page1[0].c1_6[10]";
        public static String ACTIVITY_OTHER_16 = "topmostSubform[0].Page1[0].c1_6[11]";
        public static String ACTIVITY_OTHER_SPECIFY_16 = "topmostSubform[0].Page1[0].f1_37[0]";
        public static String ACTIVITY_ADDITIONAL_DETAILS_17 = "topmostSubform[0].Page1[0].f1_38[0]";

        public static String PREVIOUS_EIN_YES_18 = "topmostSubform[0].Page1[0].c1_7[0]";
        public static String PREVIOUS_EIN_NO_18 = "topmostSubform[0].Page1[0].c1_7[1]";
        public static String PREVIOUS_EIN_TEXT_18 = "topmostSubform[0].Page1[0].f1_39[0]";

        public static String TPD_NAME = "topmostSubform[0].Page1[0].f1_40[0]";
        public static String TPD_ADDRESS_ZIP = "topmostSubform[0].Page1[0].f1_42[0]";
        public static String TPD_PHONE_NUMBER = "topmostSubform[0].Page1[0].f1_41[0]";
        public static String DATE = "Text1";
        public static String PHONE_NUMBER = "topmostSubform[0].Page1[0].f1_45[0]";
        public static String SIGNATURE_NAME = "topmostSubform[0].Page1[0].f1_44[0]";

        static Map<EINBusinessType, String> activityMap = new HashMap<EINBusinessType, String>() {{
            put(EINBusinessType.ACCOMMODATIONS, ACTIVITY_ACCOMMODATION_AND_FOOD_SERVICE_16);
            put(EINBusinessType.CONSTRUCTION, ACTIVITY_CONSTRUCTION_16);
            put(EINBusinessType.FINANCE, ACTIVITY_FINANCE_AND_INSURANCE_16);
            put(EINBusinessType.FOOD_SERVICE, ACTIVITY_ACCOMMODATION_AND_FOOD_SERVICE_16);
            put(EINBusinessType.HEALTH_CARE, ACTIVITY_HEALTHCARE_AND_SOCIAL_ASSISTANCE_16);
            put(EINBusinessType.INSURANCE, ACTIVITY_FINANCE_AND_INSURANCE_16);
            put(EINBusinessType.MANUFACTURING, ACTIVITY_MANUFACTURING_16);
            put(EINBusinessType.REAL_ESTATE, ACTIVITY_REAL_ESTATE_16);
            put(EINBusinessType.RENTAL_AND_LEASING, ACTIVITY_RENTAL_AND_LEASING_16);
            put(EINBusinessType.RETAIL, ACTIVITY_RETAIL_16);
            put(EINBusinessType.SOCIAL_ASSISTANCE, ACTIVITY_HEALTHCARE_AND_SOCIAL_ASSISTANCE_16);
            put(EINBusinessType.TRANSPORTATION, ACTIVITY_TRANSPORTATION_AND_WAREHOUSING_16);
            put(EINBusinessType.WAREHOUSING, ACTIVITY_TRANSPORTATION_AND_WAREHOUSING_16);
            put(EINBusinessType.WHOLESALE, ACTIVITY_WHOLESALE_AGENT_BROKER_16);
            put(EINBusinessType.OTHER, ACTIVITY_OTHER_16);
        }};

        public static class Extractor {
            public static String extractLegalName(final EinOrder order) {
                if ("SoleProprietor".equals(order.getOrder_type())) {
                    return extractName(order);
                }
                return order.getLegal_name();
            }
            public static String extractName(final EinOrder order) {
                return order.getFirst_name() + " " + order.getMiddle_name() + " " + order.getLast_name() + " " + StringUtils.trimToEmpty(order.getSuffix());
            }

            public static String extractMailingAddress(final EinOrder order) {
                if (order.getIs_diff_mailing_address() != null && order.getIs_diff_mailing_address() == 1) {
                    return order.getMailing_address() + (StringUtils.isBlank(order.getMailing_apt_suite()) ? "" : " " + order.getMailing_apt_suite());
                }
                return extractAddress(order);
            }
            public static String extractMailingCityStateZip(final EinOrder order) {
                if (order.getIs_diff_mailing_address() != null && order.getIs_diff_mailing_address() == 1) {
                    return order.getMailing_city() + ", " + order.getMailing_state() + " " + order.getMailing_zip_code();
                }
                return extractCityStateZip(order);
            }
            public static String extractCountyState(final EinOrder order) {
                return StringUtils.trimToEmpty(order.getCounty()) + " " + order.getState();
            }
            public static String extractAddress(final EinOrder order) {
                return order.getAddress() + (StringUtils.isBlank(order.getApt_suite()) ? "" : " " + order.getApt_suite());
            }
            public static String extractCityStateZip(final EinOrder order) {
                return order.getCity() + ", " + order.getState() + " " + order.getZip_code();
            }
            public static String extractSSN(final EinOrder order) {
                return order.getSsn();
            }

            public static String extractLLCMembers(final EinOrder order) {
                return StringUtils.trimToEmpty(order.getLlc_number_members());
            }

            public static String entityTypeCheckbox(final EinOrder order) {
                String type = order.getOrder_type();
                switch (type) {
                    case "SoleProprietor":
                        return ENTITY_TYPE_SOLE_PROPRIETOR_9A;
                    case "LLC":
                    case "Partnership":
                        return ENTITY_TYPE_PARTNERSHIP_9A;
                    case "Corporation":
                        return ENTITY_TYPE_CORPORATION_9A;
                    case "Estate":
                        return ENTITY_TYPE_ESTATE_9A;
                    case "Trust":
                        return ENTITY_TYPE_TRUST_9A;
                    default:
                        return null; // no checkbox to select for LLC, separate logic
                }
            }

            public static String entityTypeSSNInput(final EinOrder order) {
                String type = order.getOrder_type();
                switch (type) {
                    case "SoleProprietor":
                        return ENTITY_TYPE_SOLE_PROPRIETOR_SSN_INPUT_9A;
                    case "Estate":
                        return ENTITY_TYPE_ESTATE_SSN_INPUT_9A;
                    case "Trust":
                        return ENTITY_TYPE_TRUST_SSN_INPUT_9A;
                    default:
                        return null; // no SSN input to fill for LLC, Partnership and Corporation, separate logic
                }
            }

            public static String extractStateIncorporated(final EinOrder order) {
                String stateIncorporated = StringUtils.trimToNull(order.getState_incorporated());
                return stateIncorporated != null ? stateIncorporated : order.getState();
            }

            public static String reasonCheckbox(final EinOrder order) {
                String reason = order.getReason();
                switch (reason) {
                    case "Started a new business":
                        return REASON_STARTED_NEW_BUSINESS_10;
                    case "Hired employee(s)":
                        return REASON_HIRED_EMPLOYEES_10;
                    case "Banking Purposes":
                        return REASON_BANKING_PURPOSE_10;
                    case "Changed Type Of Organization":
                        return REASON_CHANGED_TYPE_OF_ORGANIZATION_10;
                    case "Purchased Active Business":
                        return REASON_PURCHASED_GOING_BUSINESS_10;
                    default:
                        return REASON_STARTED_NEW_BUSINESS_10;
                }
            }

            public static String extractDateBusinessStarted(final EinOrder order) {
                int index = (order.getStart_date_month() == null) ? 0 : order.getStart_date_month() - 1;
                String month = new DateFormatSymbols(Locale.US).getMonths()[index];
                return month + " " + order.getStart_date_year().toString();
            }

            public static String extractClosingMonth(final EinOrder order) {
                int index = (order.getAccounting_close_month() == null) ? 0 : order.getAccounting_close_month() - 1;
                return new DateFormatSymbols(Locale.US).getMonths()[index];
            }

            public static String extractEmployeesAgricultural(final EinOrder order) {
                Integer maxAgricultural = order.getMax_ees_next12mos_agri();
                maxAgricultural = maxAgricultural != null ? maxAgricultural : 0;
                return maxAgricultural.toString();
            }

            public static String extractEmployeesHousehold(final EinOrder order) {
                Integer maxHousehold = order.getMax_ees_next12mos_household();
                maxHousehold = maxHousehold != null ? maxHousehold : 0;
                return maxHousehold.toString();
            }

            public static String extractEmployeesOther(final EinOrder order) {
                Integer maxOther = order.getMax_ees_next12mos_other();
                maxOther = maxOther != null ? maxOther : 0;
                return maxOther.toString();
            }

            public static boolean isLiability1000(final EinOrder order) {
                return order.getIs_employment_tax_liability() != null && order.getIs_employment_tax_liability();
            }

            public static String activityCheckbox(final EinOrder order) {
                EINBusinessType type = EINBusinessType.from(order.getBusiness_type());
                return activityMap.get(type);
            }

            public static String otherActivityDetails(final EinOrder order) {
                EINBusinessType type = EINBusinessType.from(order.getBusiness_type());
                if (type != EINBusinessType.OTHER) {
                    return "";
                }
                EINBusinessSubType subType = EINBusinessSubType.from(order.getBusiness_sub_type());
                if (subType == null) {
                    return "";
                }
                return subType.getDescription();
            }

            public static String extractAdditionalDetails(final EinOrder order) {
                EINBusinessType type = EINBusinessType.from(order.getBusiness_type());
                EINBusinessSubType subType = EINBusinessSubType.from(order.getBusiness_sub_type());
                if (
                        (type == EINBusinessType.CONSTRUCTION && subType == EINBusinessSubType.NO) ||
                        (type == EINBusinessType.MANUFACTURING) ||
                        (type == EINBusinessType.WAREHOUSING && subType == EINBusinessSubType.YES)
                ) {
                    return StringUtils.trimToEmpty(order.getBusiness_details());
                }

                if (type == EINBusinessType.CONSTRUCTION && subType == EINBusinessSubType.YES) {
                    return "Multiple construction trades";
                }
                if (type == EINBusinessType.HEALTH_CARE) {
                    return (subType == EINBusinessSubType.YES) ?
                            "Establishment includes medical practitioners having the degree of M.D. or D.O." :
                            "Establishment doesn't include medical practitioners having the degree of M.D. or D.O.";
                }

                if (subType != null) {
                    return subType.isAdditionalDetails()
                            ? StringUtils.trimToEmpty(order.getBusiness_details())
                            : subType.getDescription();
                }
                return "";
            }

            public static String extractPreviousEIN(final EinOrder order) {
                return StringUtils.trimToEmpty(order.getPrevious_ein());
            }

            public static String extractPhoneNumber(final EinOrder order) {
                return order.getPhone_number();
            }
            public static String extractSignatureName(final EinOrder order) {
                return order.getFirst_name() + " " + order.getMiddle_name() + " " + order.getLast_name() + " " + StringUtils.trimToEmpty(order.getSuffix());
            }
        }
    }

    public static class PenaltyWaiverFillableLetter{
        public static String DATE = "Date";
        public static String IRS_PW_STREET1 = "IRS_pw_street1";
        public static String FIRST_NAME = "first_name";
        public static String LAST_NAME = "last_name";
        public static String SSN = "SSN";
        public static String SPOUSE_FIRST_NAME= "spouse_first name";
        public static String SPOUSE_LAST_NAME= "spouse_last";
        public static String SPOUSE_SSN= "spouse_SSN";
        public static String CITY_STATE_ZIP = "citystatezip";
        public static String ADDRESS1_ADDRESS2 = "address1 +address2";
        public static String PHONE_NUMBER = "phone_number";
        public static String IRS_CITY_STATE_ZIP = "IRS_pw_citystatezip";
        public static String PENALTY_TAX_YEAR = "penalty_tax_year";
        public static String PENALTY_TYPE = "penalty_type";

        /*
        public static String IRS_ADDRESS_LINE_1 = "irs-address-line-1";
        public static String IRS_CITY = "irs-city";
        public static String IRS_STATE = "irs-state";
        public static String IRS_ZIP = "irs-zip";
        public static String FIRST_NAME_HEAD = "first-name-head";
        public static String LAST_NAME_HEAD = "last-name-head";
        public static String SSN_HEAD = "ssn-head";
        public static String SPOUSE_FIRST_HEAD = "spouse-first-head";
        public static String SPOUSE_LAST_HEAD = "spouse-last-head";
        public static String SPOUSE_SSN_HEAD = "spouse-ssn-head";
        public static String ADDRESS_LINE_1 = "address-line-1";
        public static String CITY_STATE_ZIP = "city-state-zip";
        public static String PHONE_HEAD = "phone-head";
        public static String TAX_YEAR = "tax-year";
        public static String PENALTY_TYPE = "penalty-type";
        public static String PHONE = "phone";
        public static String FIRST_NAME = "first-name";
        public static String LAST_NAME = "last-name";
        public static String SSN = "ssn";
        public static String SPOUSE_FIRST = "spouse-first";
        public static String SPOUSE_LAST = "spouse-last";
        public static String SPOUSE_SSN = "spouse-ssn";
         */

        public static class Extractor{
            private static final String FAILURE_TO_PAY = "IRC § 6651";
            private static final String FAILURE_TO_DEPOSIT = "IRC § 6656";
            private static final String FAILURE_TO_FILE_TIMELY = "IRC § 6698";
            public static String extractIRSAddressLine1(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getIrsAddress1();
            }
            public static String extractIRSCity(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getIrsCity();
            }
            public static String extractIRSState(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getIrsState();
            }
            public static String extractIRSZip(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getIrsZipcode();
            }
            public static String extractFirstName(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getFirstName();
            }
            public static String extractLastName(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getLastName();
            }
            public static String extractSSN(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getSsn();
            }
            public static String extractSpouseFirstName(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getSpouseFirstname();
            }
            public static String extractSpouseLastName(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getSpouseLastname();
            }
            public static String extractSpouseSSN(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getSpouseSsn();
            }
            public static String extractAddressLine1(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getShippingAddress1()+" "+penaltyOrder.getShippingAddress2();
                //return penaltyOrder.getBillingAddress1()+" "+penaltyOrder.getBillingAddress2();
            }
            public static String extractCityStateZip(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getShippingCity()+", "+penaltyOrder.getShippingState()+", "+ penaltyOrder.getShippingPostalCode();
                //return penaltyOrder.getBillingCity()+", "+penaltyOrder.getBillingState()+", "+ penaltyOrder.getBillingPostalCode();
            }
            public static String extractPhone(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getBillingPhone();
            }
            public static String extractYear(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getPenaltyTaxYear();
            }
            public static String extractPenaltyType(final PenaltyOrder penaltyOrder){
                return penaltyOrder.getPenaltyType();
            }
            public static String extractIRSCityStateZip(final PenaltyOrder penaltyOrder) {
                return penaltyOrder.getIrsCity()+", "+penaltyOrder.getIrsState()+", "+ penaltyOrder.getIrsZipcode();
            }
        }
    }
}
