package com.repnox.nineseventax.features.utils;

public class OrderUtil {

    /**
     * generate a random string of length returnLength
     *
     * @param charString
     * @param returnLength
     * @return random string
     */
    public static String generateOrderID(String charString, int returnLength) {
        String random = "";
        for (int j = 0; j < returnLength; j++) {
            random += charString.charAt((int) (Math.random() * charString.length()));
        }
        return random;
    }

}
