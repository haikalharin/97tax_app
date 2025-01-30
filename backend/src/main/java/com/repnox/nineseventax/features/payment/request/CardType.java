package com.repnox.nineseventax.features.payment.request;

import java.util.regex.Pattern;

/**
 * Possible values for the {@code cardBrand} element in the Orbital payment request are described in the
 * <a href="https://developer.jpmorgan.com/api/v0/items/orbital-api/assets/OGW%20JSON%20Developer%20Guide%20Payments%20Request.pdf">
 *     <i>Developer Guide: Payments Request Elements</i></a>.
 */
public enum CardType {

    UNKNOWN,
    VI("^4[0-9]{12}(?:[0-9]{3}){0,2}$"),
    MC("^(?:5[1-5]|2(?!2([01]|20)|7(2[1-9]|3))[2-7])\\d{14}$"),
    AX("^3[47][0-9]{13}$"),
    /**
     * DI = Discover/Discover Diners/UnionPay (same for all three brands)
     * <p>
     *
     * Brand specific patterns used to compile unified one:
     * <ul><li>Discover: {@code ^6(?:011|[45][0-9]{2})[0-9]{12}$}
     *     <li>Discover Diners: {@code ^3(?:0[0-5]\\d|095|6\\d{0,2}|[89]\\d{2})\\d{12,15}$}
     *     <li>UnionPay: {@code ^62[0-9]{14,17}$}
     * </ul>
     */
    DI("^(?:6(?:011|[45][0-9]{2})[0-9]{12}|3(?:0[0-5]\\d|095|6\\d{0,2}|[89]\\d{2})\\d{12,15}|62[0-9]{14,17})$"),
    JC("^(?:2131|1800|35\\d{3})\\d{11}$");

    private Pattern pattern;

    CardType() {
        this.pattern = null;
    }

    CardType(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public static CardType detect(String cardNumber) {

        for (CardType cardType : CardType.values()) {
            if (null == cardType.pattern) continue;
            if (cardType.pattern.matcher(cardNumber).matches()) return cardType;
        }

        return UNKNOWN;
    }

    @Override
    public String toString() {
        if (this == UNKNOWN) {
            return null;
        }
        return super.toString();
    }

}