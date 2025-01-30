package com.repnox.nineseventax.features.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class ValidationUtil {

    public static void isTrue(boolean validate, String exception) throws Exception {
        if (validate) {
            return;
        }

        throw new Exception(exception);
    }

    public static void isFalse(boolean validate, String exception) throws Exception {
        if (!validate) {
            return;
        }

        throw new Exception(exception);
    }

    public static void isNotNull(Object value, String exception) throws Exception {
        if (ObjectUtils.allNotNull(value)) {
            return;
        }

        throw new Exception(exception);
    }

    public static void isNotBlank(String values, String exception) throws Exception {
        if (StringUtils.isNotBlank(values)) {
            return;
        }

        throw new Exception(exception);
    }

}
