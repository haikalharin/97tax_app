package com.repnox.nineseventax.features.ein;

public enum EINBusinessType {

    ACCOMMODATIONS("ACCOMMODATIONS"),
    CONSTRUCTION("CONSTRUCTION"),
    FINANCE("FINANCE"),
    FOOD_SERVICE("FOOD_SERVICE"),
    HEALTH_CARE("HEALTH_CARE"),
    INSURANCE("INSURANCE"),
    MANUFACTURING("MANUFACTURING"),
    REAL_ESTATE("REAL_ESTATE"),
    RENTAL_AND_LEASING("RENTAL_AND_LEASING"),
    RETAIL("RETAIL"),
    SOCIAL_ASSISTANCE("SOCIAL_ASSISTANCE"),
    TRANSPORTATION("TRANSPORTATION"),
    WAREHOUSING("WAREHOUSING"),
    WHOLESALE("WHOLESALE"),
    OTHER("OTHER");

    private String name;

    EINBusinessType(String name) {
        this.name = name;
    }

    public static EINBusinessType from(String name) {
        for (EINBusinessType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return OTHER;
    }

}
