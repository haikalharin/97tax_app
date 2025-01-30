package com.repnox.nineseventax.features.utils;

public class StringUtility {
    String[] SmallCapAVarients = { "à", "á", "â", "ä", "ã", "æ", "å", "ā" };
    String[] BigCapAVarients = { "À", "Á", "Â", "Ä", "Æ", "Ã", "Å", "Ā" };
    String[] SmallCapCVarients = {"ç","ć","č"};
    String[] BigCapCVarients = { "Ç","Ć","Č" };
    String[] SmallCapNVarients = {"ñ","ń"};
    String[] BigCapNVarients = { "Ñ","Ń" };
    String[] SmallCapZVarients = {"ž","ź","ż"};
    String[] BigCapZVarients = { "Ž","Ź","Ż" };
    String[] SmallCapEVarients = { "è", "é", "ê", "ë" };
    String[] BigCapEVarients = { "È", "É", "Ê", "Ë" };
    String[] SmallCapIVarients = { "î", "ï", "í", "ì" };
    String[] BigCapIVarients = { "Î", "Ï", "Í", "Ì" };
    String[] SmallCapOVarients = { "ô", "ö", "ò", "ó", "œ", "ø", "õ" };
    String[] BigCapOVarients = { "Ô", "Ö", "Ò", "Ó", "Œ", "Ø", "Õ" };
    String[] SmallCapUVarients = { "û", "ü", "ù", "ú" };
    String[] BigCapUVarients = { "Û", "Ü", "Ù", "Ú" };

    public String ReplaceSpecialCharactersToCharacters(String StringToValidate) {
        String StringToReturn = StringToValidate;
       
        if(StringToReturn != null) {
        	for (String SmallCapAVarient : SmallCapAVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapAVarient, "a");
            }
            for (String BigCapAVarient : BigCapAVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapAVarient, "A");
            }

            for (String SmallCapCVarient : SmallCapCVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapCVarient, "c");
            }
            for (String BigCapCVarient : BigCapCVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapCVarient, "C");
            }

            for (String SmallCapNVarient : SmallCapNVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapNVarient, "n");
            }
            for (String BigCapNVarient : BigCapNVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapNVarient, "N");
            }

            for (String SmallCapZVarient : SmallCapZVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapZVarient, "z");
            }
            for (String BigCapZVarient : BigCapZVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapZVarient, "Z");
            }            

            for (String SmallCapEVarient : SmallCapEVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapEVarient, "e");
            }
            for (String BigCapEVarient : BigCapEVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapEVarient, "E");
            }

            for (String SmallCapIVarient : SmallCapIVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapIVarient, "i");
            }
            for (String BigCapIVarient : BigCapIVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapIVarient, "I");
            }

            for (String SmallCapOVarient : SmallCapOVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapOVarient, "o");
            }
            for (String BigCapOVarient : BigCapOVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapOVarient, "O");
            }
            for (String SmallCapUVarient : SmallCapUVarients) {
                StringToReturn = StringToReturn.replaceAll(SmallCapUVarient, "u");
            }
            for (String BigCapUVarient : BigCapUVarients) {
                StringToReturn = StringToReturn.replaceAll(BigCapUVarient, "U");
            }
            StringToReturn = StringToReturn.replaceAll("š", "s");
            StringToReturn = StringToReturn.replaceAll("Š", "S");
            StringToReturn = StringToReturn.replaceAll("ÿ", "y");
            StringToReturn = StringToReturn.replaceAll("Ÿ", "Y");
        }
        return StringToReturn;
    }

    public static boolean isNullOrEmpty(String str) {
        if(str == null)
            return true;
        if("".equals(str))
            return true;

        return false;
    }
}
