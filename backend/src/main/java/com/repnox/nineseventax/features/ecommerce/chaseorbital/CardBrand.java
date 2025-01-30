package com.repnox.nineseventax.features.ecommerce.chaseorbital;

public enum CardBrand {

	VISA ("Visa"),
	MASTER_CARD ("Master Card"),
	AMERICAN_EXPRESS ("American Express"),
	DISCOVER ("Discover"), 
	OTHER ("Other");

	private final String type;
	CardBrand(final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static CardBrand fromValue(final String type) {
        for (CardBrand brand: CardBrand.values()) {
            if (brand.getType().equals(type)) {
                return brand;
            }
        }
        return OTHER;
    }
}

