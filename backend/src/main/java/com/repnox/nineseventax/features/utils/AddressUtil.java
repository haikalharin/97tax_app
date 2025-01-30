package com.repnox.nineseventax.features.utils;

import com.repnox.nineseventax.features.admin.StateAddress;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;

public class AddressUtil {
    public static PenaltyOrder resolveIrsAddress(PenaltyOrder penaltyOrder) {
        String billingState = penaltyOrder.getShippingState();

        if("AR".equals(billingState) || "DE".equals(billingState) || "IL".equals(billingState) || "IN".equals(billingState) ||
                "IA".equals(billingState) || "KY".equals(billingState) || "ME".equals(billingState) || "MA".equals(billingState) ||
                "MN".equals(billingState) || "MO".equals(billingState) || "NH".equals(billingState) || "NJ".equals(billingState) ||
                "NY".equals(billingState) || "OK".equals(billingState) || "VT".equals(billingState) || "VA".equals(billingState) ||
                "WI".equals(billingState)
        ) {
            penaltyOrder.setIrsAddress1("Department of the Treasury");
            penaltyOrder.setIrsAddress2("Internal Revenue Service");
            penaltyOrder.setIrsCity("Kansas");
            penaltyOrder.setIrsState("MO");
            penaltyOrder.setIrsZipcode("64999-0002");
        } else if("CT".equals(billingState) || "DC".equals(billingState) || "MD".equals(billingState) || "PA".equals(billingState) ||
                "RI".equals(billingState) || "WV".equals(billingState)) {
            penaltyOrder.setIrsAddress1("Department of the Treasury");
            penaltyOrder.setIrsAddress2("Internal Revenue Service");
            penaltyOrder.setIrsCity("Ogden");
            penaltyOrder.setIrsState("UT");
            penaltyOrder.setIrsZipcode("84201-0002");
        } else if("FL".equals(billingState) || "LA".equals(billingState) || "MS".equals(billingState) || "TX".equals(billingState)) {
            penaltyOrder.setIrsAddress1("Department of the Treasury");
            penaltyOrder.setIrsAddress2("Internal Revenue Service");
            penaltyOrder.setIrsCity("Austin");
            penaltyOrder.setIrsState("TX");
            penaltyOrder.setIrsZipcode("73301-0002");
        } else if("AL".equals(billingState) || "GA".equals(billingState) || "NC".equals(billingState) || "SC".equals(billingState) || "TN".equals(billingState)) {
            penaltyOrder.setIrsAddress1("Department of the Treasury");
            penaltyOrder.setIrsAddress2("Internal Revenue Service");
            penaltyOrder.setIrsCity("Kansas");
            penaltyOrder.setIrsState("MO");
            penaltyOrder.setIrsZipcode("64999-0002");
        } else if("AK".equals(billingState) || "CA".equals(billingState) || "HI".equals(billingState) || "OH".equals(billingState) || "WA".equals(billingState)) {
            penaltyOrder.setIrsAddress1("Department of the Treasury");
            penaltyOrder.setIrsAddress2("Internal Revenue Service");
            penaltyOrder.setIrsCity("Ogden");
            penaltyOrder.setIrsState("UT");
            penaltyOrder.setIrsZipcode("84201-0002");
        } else if("AZ".equals(billingState) || "CO".equals(billingState) || "ID".equals(billingState) || "KS".equals(billingState) || "MI".equals(billingState) ||
                "MT".equals(billingState) || "NE".equals(billingState) || "NV".equals(billingState) || "NM".equals(billingState) || "OR".equals(billingState) ||
                "ND".equals(billingState) || "SD".equals(billingState) || "UT".equals(billingState) || "WY".equals(billingState)) {
            penaltyOrder.setIrsAddress1("Department of the Treasury");
            penaltyOrder.setIrsAddress2("Internal Revenue Service");
            penaltyOrder.setIrsCity("Ogden");
            penaltyOrder.setIrsState("UT");
            penaltyOrder.setIrsZipcode("84201-0002");
        }

        return penaltyOrder;
    }

    public boolean checkPOBox(String myInput) {
        String spattern = "(?i)^\\s*((P(OST)?.?\\s*(O(FF(ICE)?)?)?.?\\s+(B(IN|OX))?)|B(IN|OX))";
        if (myInput.matches(spattern)) {
            return true;
        } else {
            return false;
        }
    }
}
