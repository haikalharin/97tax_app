package com.repnox.nineseventax.features.efile.util;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class EFileUtil {

    private static String addZeros(String value, int length) {
        int vLength = value.length();
        if (vLength > length) {
            return value.substring(0, length);
        }
        for (int i = 0; i < length - vLength; i++) {
            value = "0" + value;
        }
        return value;
    }

    public static String submissionId(String efin) {
        long now = System.currentTimeMillis();
        long unique = (now / 10) % 10000000L;
        System.out.println((System.currentTimeMillis() / 1000));

        ZoneId zone = ZoneId.of("America/New_York");
        LocalDate today = LocalDate.now(zone);

        int year = today.getYear();
        int dayOfYear = today.getDayOfYear();
        return efin + year + addZeros(Integer.toString(dayOfYear), 3) + addZeros(Long.toString(unique), 7);
    }

    public static String clearNonDigits(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("[^\\d]", "");
    }

    public static String fixNamespaces(String toString) {
        String defaultNamespace = "xmlns=\"http://www.irs.gov/efile\"";
        String correctNamespaces = "xmlns=\"http://www.irs.gov/efile\" xmlns:efile=\"http://www.irs.gov/efile\"";
        return toString.replaceFirst(defaultNamespace, correctNamespaces);
    }

    public static String getNameControl(String name) {
        if (name == null) {
            return "";
        }
        String valuable = name.toUpperCase().replaceAll("[^A-Z0-9\\-\\&]", "");
        return valuable.length() < 4 ? valuable : valuable.substring(0, 4);
    }

    public static String formatCalendar(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZoneId zone = ZoneId.of("America/New_York");

        ZonedDateTime zdt = calendar.toGregorianCalendar().toZonedDateTime();
        ZonedDateTime zdtEST = zdt.withZoneSameInstant(zone);
        return formatter.format(zdtEST);
    }

}
