package com.repnox.nineseventax.features.ein;

public enum EINBusinessSubType {

    // Accommodations
    CASINO("CASINO", "Casino hotel"),
    HOTEL("HOTEL", "Hotel"),
    MOTEL("MOTEL", "Motel"),
    // Behaviour for OTHER is similar in terms of additional details: ACCOMMODATIONS, FINANCE, INSURANCE, etc.
    OTHER("OTHER", "Other", true),

    // Construction, Health Care, Wholesale
    YES("YES", "Yes"),
    NO("NO", "No"),

    // Finance (+OTHER)
    COMMODITIES_BROKER("COMMODITIES_BROKER", "Commodities broker"),
    CREDIT_CARD_ISSUING("CREDIT_CARD_ISSUING", "Credit card issuing"),
    INVESTMENT_ADVICE("INVESTMENT_ADVICE", "Investment advice"),
    INVESTMENT_CLUB("INVESTMENT_CLUB", "Investment club"),
    INVESTMENT_HOLDING("INVESTMENT_HOLDING", "Investment holding"),
    MORTGAGE_AGENT("MORTGAGE_AGENT", "Mortgage broker"),
    MORTGAGE_COMPANY("MORTGAGE_COMPANY", "Mortgage company"),
    PORTFOLIO_MANAGEMENT("PORTFOLIO_MANAGEMENT", "Portfolio management"),
    SALES_FINANCING("SALES_FINANCING", "Sales financing"),
    SECURITIES_BROKER("SECURITIES_BROKER", "Securities broker"),
    TRUST_ADMIN("TRUST_ADMIN", "Trust administration"),
    VENTURE_CAPITAL_COMPANY("VENTURE_CAPITAL_COMPANY", "Venture capital company"),

    // Food Service (+OTHER)
    BAR("BAR", "Bar"),
    BAR_AND_RESTAURANT("BAR_AND_RESTAURANT", "Bar and restaurant"),
    CATERING_SERVICE("CATERING_SERVICE", "Catering service"),
    COFFEE_SHOP("COFFEE_SHOP", "Coffee shop"),
    FAST_FOOD_RESTAURANT("FAST_FOOD_RESTAURANT", "Fast food restaurant"),
    FULL_SERVICE_RESTAURANT("FULL_SERVICE_RESTAURANT", "Full service restaurant"),
    ICE_CREAM_SHOP("ICE_CREAM_SHOP", "Ice cream shop"),
    MOBILE_FOOD_SERVICE("MOBILE_FOOD_SERVICE", "Mobile food service"),

    // Health Care - Yes/No

    // Insurance (+OTHER)
    INSURANCE_CARRIER("INSURANCE_CARRIER", "Insurance carrier"),
    INSURANCE_AGENT("INSURANCE_AGENT", "Insurance agent/broker"),

    // Manufacturing - only additional details

    // Real Estate (+OTHER)
    RENT_PROPERTY("RENT_PROPERTY", "Rent/lease own property"),
    CAPITAL_BUILD("CAPITAL_BUILD", "Build property"),
    SELL_PROPERTY("SELL_PROPERTY", "Sell property for others"),
    MANAGE_PROPERTY("MANAGE_PROPERTY", "Manage estate for others"),

    // Rental and Leasing
    RENT_ESTATE("RENT_ESTATE", "Rent, lease, or sell estate"),
    RENT_GOODS("RENT_GOODS", "Rent or lease goods", true),
    MANAGE_ESTATE("MANAGE_ESTATE", "Manage estate for others"),

    // Retail (+OTHER)
    SELLING_GOODS("SELLING_GOODS", "Selling goods (Internet)"),
    STOREFRONT_SALES("STOREFRONT_SALES", "Sales from a storefront", true),
    DIRECT_SALES("DIRECT_SALES", "Direct sales", true),
    AUCTION_HOUSE("AUCTION_HOUSE", "Auction house"),

    // Social Assistance (+OTHER)
    NURSING_HOME("NURSING_HOME", "Nursing home"),
    SHELTER("SHELTER", "Shelter"),
    YOUTH_SERVICES("YOUTH_SERVICES", "Youth services"),

    // Transportation
    CARGO("CARGO", "Cargo"),
    PASSENGERS("PASSENGERS", "Passengers"),
    PROVIDE_SUPPORT("PROVIDE_SUPPORT", "Provide support activity", true),

    // Warehousing (no sub types and additional details)
    // Wholesale - Yes/No

    // Other (+OTHER)
    CONSULTING("CONSULTING", "Consulting"),
    MANUFACTURING("MANUFACTURING", "Manufacturing"),
    ORGANIZATION("ORGANIZATION", "Organization (religious, social, etc.)"),
    RENTAL("RENTAL", "Rental"),
    REPAIR("REPAIR", "Repair"),
    OTHER_SELL("OTHER_SELL", "Sell goods"),
    OTHER_SERVICE("OTHER_SERVICE", "Service");

    private String name;
    private String description;
    private boolean additionalDetails;

    EINBusinessSubType(String name, String description) {
        this(name, description, false);
    }

    EINBusinessSubType(String name, String description, boolean additionalDetails) {
        this.name = name;
        this.description = description;
        this.additionalDetails = additionalDetails;
    }

    public static EINBusinessSubType from(String name) {
        for (EINBusinessSubType subType : values()) {
            if (subType.name.equals(name)) {
                return subType;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdditionalDetails() {
        return additionalDetails;
    }

}
