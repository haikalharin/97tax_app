package com.repnox.nineseventax.features.ecommerce;

import com.cardinalcommerce.client.CentinelRequest;
import com.cardinalcommerce.client.CentinelResponse;
import com.repnox.nineseventax.features.ecommerce.util.LinkedMapBuilder;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.penalty.PenaltyOrder;

import com.repnox.nineseventax.features.productprices.ProductPriceRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class CentinelFacade {

    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CentinelFacade.class);

    private String MESSAGE_VERSION = "1.7";

    @Value("${cardinal.auth.pattern}")
    private String CARDINAL_AUTH_PATTERN;

    @Value("${cardinal.cruise.api.identifier}")
    private String CRUISE_API_IDENTIFIER;

    @Value("${cardinal.cruise.org.unit.id}")
    private String CRUISE_ORG_UNIT_ID;

    @Value("${cardinal.cruise.api.key}")
    private String CRUISE_API_KEY;

    @Value("${cardinal.cruise.script.url}")
    private String CRUISE_SCRIPT_URL;

    @Value("${cardinal.cruise.ttl}")
    private int CRUISE_TTL;

    @Value("${cardinal.centinel.transaction.url}")
    private String TRANSACTION_URL;

    @Value("${cardinal.centinel.processor.id}")
    private String PROCESSOR_ID;

    @Value("${cardinal.centinel.merchant.id}")
    private String MERCHANT_ID;

    @Value("${cardinal.centinel.transaction.pwd}")
    private String TRANSACTION_PWD;

    @Value("${cardinal.centinel.timeout.read}")
    private int TIMEOUT_READ;

    @Value("${cardinal.centinel.timeout.connect}")
    private int TIMEOUT_CONNECT;


    public static final String CRUISE_ALGORITHM = "SHA-256";

    private static final String CARD_TRANSACTION_TYPE = "C";

    private static final String USD_CURRENCY_CODE = "840";

    private static final String PAYMENT_PAGE_ORDER_CHANNEL = "MARK";

    @SuppressWarnings("unused")
	private static final String CART_ORDER_CHANNEL = "CART";

    @SuppressWarnings("unused")
    private static final String PRODUCT_ORDER_CHANNEL = "PRODUCT";

    private static final String PHYSICAL_DELIVERY_PRODUCT_CODE = "PHY";

    @SuppressWarnings("unused")
    private static final String DIGITAL_GOOD_PRODUCT_CODE = "DIG";
    @SuppressWarnings("unused")
    private static final String SERVICE_PRODUCT_CODE = "SVC";

    private static final String ECOMMERCE_TRANSACTION_MODE = "S";

    private static final String MERCHANT_DATA = "97tax.com";

    private static final String LOOKUP_MESSAGE_TYPE = "cmpi_lookup";

    private static final String AUTHENTICATE_MESSAGE_TYPE = "cmpi_authenticate";

    private static final String UNITED_STATES_COUNTRY_CODE = "US";

    private final ProductPriceRepo productPriceRepo;

    CentinelFacade(@Autowired final ProductPriceRepo productPriceRepo) {
        this.productPriceRepo = productPriceRepo;
    }

    public CentinelResponse invokeService(CentinelRequest request) {
        CentinelResponse response = request.sendHTTP(TRANSACTION_URL, TIMEOUT_CONNECT, TIMEOUT_READ);
        return response;
    }

    private CentinelRequest baseRequest(OrderInfo orderInfo) {
        CentinelRequest centinelRequest = new CentinelRequest();
        centinelRequest.add("Version", MESSAGE_VERSION);
        Long nowMillis = System.currentTimeMillis();
        centinelRequest.add("Algorithm", CRUISE_ALGORITHM);
        centinelRequest.add("Identifier", CRUISE_API_IDENTIFIER);
        centinelRequest.add("OrgUnit", CRUISE_ORG_UNIT_ID);
        centinelRequest.add("Signature", generateSignature(nowMillis, orderInfo));
        centinelRequest.add("Timestamp", nowMillis.toString());

        return centinelRequest;
    }


    public Map validateJwt(String jwt) {
        // The API Key used here to validate the Cardinal response is the same
        // API Key you use to generate your request jwt.
        Claims claims = (Claims) Jwts.parser()
                .setSigningKey(CRUISE_API_KEY.getBytes())
                .parse(jwt)
                .getBody();

        return (Map) claims.get("Payload");
    }

    public String generateSignature(long nowMillis, Map<String, Object> map) {
        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Date now = new Date(nowMillis);

        // We will sign our JWT with our API Key
        byte[] apiKeySecretBytes = CRUISE_API_KEY.getBytes();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes,
                signatureAlgorithm.getJcaName());

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setIssuer(CRUISE_API_IDENTIFIER)
                .claim("OrgUnitId", CRUISE_ORG_UNIT_ID)
                .claim("Payload", map)
                .signWith(signatureAlgorithm, signingKey);

        // Add the expiration or TTL (Time To Live) for this JWT
        long expMillis = nowMillis + CRUISE_TTL;
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public String generateSignature(long nowMillis, OrderInfo orderInfo) {
        return generateSignature(nowMillis, generatePayload(orderInfo));
    }

    public String generateSignature(long nowMillis, PenaltyOrder orderInfo) {
        return generateSignature(nowMillis, generatePayload(orderInfo));
    }

    public String generateSignature(long nowMillis, EinOrder orderInfo) {
        return generateSignature(nowMillis, generatePayload(orderInfo));
    }

    public Map<String,Object> generatePayload(EinOrder orderInfo) {
        return new LinkedMapBuilder()
                .put("OrderDetails", new LinkedMapBuilder()
                                .put("OrderNumber", orderInfo.getId().toString())
                                .put("Amount", String.valueOf(orderInfo.getAmount()))
                                .put("CurrencyCode", "840")
                        // .put("OrderChannel", "MARK")
                )
                .put("Consumer", new LinkedMapBuilder()
                        .put("Email1", orderInfo.getEmail())
                        .put("ShippingAddress", new LinkedMapBuilder()
                                .put("FirstName", orderInfo.getFirst_name())
                                .put("LastName", orderInfo.getLast_name())
                                .put("Address1", orderInfo.getMailing_address() + (StringUtils.isBlank(orderInfo.getMailing_apt_suite()) ? "" : " " + orderInfo.getMailing_apt_suite()))
                                .put("Address2", "")
                                .put("City", orderInfo.getMailing_city())
                                .put("State", orderInfo.getState())
                                .put("PostalCode", orderInfo.getMailing_zip_code())
                                .put("CountryCode", orderInfo.getMailing_country())
                                .put("Phone1", orderInfo.getPhone_number())
                        )
                        .put("BillingAddress", new LinkedMapBuilder()
                                .put("FirstName", orderInfo.getFirst_name())
                                .put("LastName", orderInfo.getLast_name())
                                .put("Address1", orderInfo.getAddress() + (StringUtils.isBlank(orderInfo.getApt_suite()) ? "" : " " + orderInfo.getApt_suite()))
                                .put("Address2", "")
                                .put("City", orderInfo.getCity())
                                .put("State", orderInfo.getState())
                                .put("PostalCode", orderInfo.getZip_code())
                                .put("CountryCode", UNITED_STATES_COUNTRY_CODE)
                                .put("Phone1", orderInfo.getPhone_number())
                        )
                        .put("Account", new LinkedMapBuilder()
                                .put("AccountNumber", orderInfo.getCard_number())
                                .put("ExpirationMonth", orderInfo.getCardExpMonth())
                                .put("ExpirationYear", orderInfo.getCardExpYear())
                                .put("CardCode", orderInfo.getCard_cvc())
                                .put("NameOnAccount", orderInfo.getCard_holder_name())
                        )
                )
                .getMap();
    }
    public Map<String,Object> generatePayload(PenaltyOrder orderInfo) {
        return new LinkedMapBuilder()
                .put("OrderDetails", new LinkedMapBuilder()
                        .put("OrderNumber", orderInfo.getId().toString())
                        .put("Amount", String.valueOf(orderInfo.getAmount()))
                        .put("CurrencyCode", "840")
                       // .put("OrderChannel", "MARK")
                )
                .put("Consumer", new LinkedMapBuilder()
                        .put("Email1", orderInfo.getEmail())
                        .put("ShippingAddress", new LinkedMapBuilder()
                                .put("FirstName", orderInfo.getShippingFirstName())
                                .put("LastName", orderInfo.getShippingLastName())
                                .put("Address1", orderInfo.getShippingAddress1())
                                .put("Address2", orderInfo.getShippingAddress2())
                                .put("City", orderInfo.getShippingCity())
                                .put("State", orderInfo.getShippingState())
                                .put("PostalCode", orderInfo.getShippingPostalCode())
                                .put("CountryCode", UNITED_STATES_COUNTRY_CODE)
                                .put("Phone1", orderInfo.getShippingPhone())
                        )
                        .put("BillingAddress", new LinkedMapBuilder()
                                .put("FirstName", orderInfo.getFirstName())
                                .put("LastName", orderInfo.getLastName())
                                .put("Address1", orderInfo.getBillingAddress1())
                                .put("Address2", orderInfo.getBillingAddress2())
                                .put("City", orderInfo.getBillingCity())
                                .put("State", orderInfo.getBillingState())
                                .put("PostalCode", orderInfo.getBillingPostalCode())
                                .put("CountryCode", UNITED_STATES_COUNTRY_CODE)
                                .put("Phone1", orderInfo.getBillingPhone())
                        )
                        .put("Account", new LinkedMapBuilder()
                                .put("AccountNumber", orderInfo.getCardNumber())
                                .put("ExpirationMonth", orderInfo.getCardExpMonth())
                                .put("ExpirationYear", orderInfo.getCardExpYear())
                                .put("CardCode", orderInfo.getCardCvc())
                                .put("NameOnAccount", orderInfo.getFirstName() + " " + orderInfo.getLastName())
                        )
                )
                .getMap();
    }
    public Map<String,Object> generatePayload(OrderInfo orderInfo) {
        return new LinkedMapBuilder()
                .put("OrderDetails", new LinkedMapBuilder()
                        .put("OrderNumber", orderInfo.getOrderNumber())
                        .put("Amount", orderInfo.getAmount())
                        .put("CurrencyCode", "840")
                       // .put("OrderChannel", "MARK")
                )
                .put("Consumer", new LinkedMapBuilder()
                        .put("Email1", orderInfo.getEmail())
                        .put("ShippingAddress", new LinkedMapBuilder()
                                .put("FirstName", orderInfo.getShippingFirstName())
                                .put("LastName", orderInfo.getShippingLastName())
                                .put("Address1", orderInfo.getShippingAddress1())
                                .put("Address2", orderInfo.getShippingAddress2())
                                .put("City", orderInfo.getShippingCity())
                                .put("State", orderInfo.getShippingState())
                                .put("PostalCode", orderInfo.getShippingPostalCode())
                                .put("CountryCode", UNITED_STATES_COUNTRY_CODE)
                                .put("Phone1", orderInfo.getShippingPhone())
                        )
                        .put("BillingAddress", new LinkedMapBuilder()
                                .put("FirstName", orderInfo.getBillingFirstName())
                                .put("LastName", orderInfo.getBillingLastName())
                                .put("Address1", orderInfo.getBillingAddress1())
                                .put("Address2", orderInfo.getBillingAddress2())
                                .put("City", orderInfo.getBillingCity())
                                .put("State", orderInfo.getBillingState())
                                .put("PostalCode", orderInfo.getBillingPostalCode())
                                .put("CountryCode", UNITED_STATES_COUNTRY_CODE)
                                .put("Phone1", orderInfo.getBillingPhone())
                        )
                        .put("Account", new LinkedMapBuilder()
                                .put("AccountNumber", orderInfo.getCardNumber())
                                .put("ExpirationMonth", orderInfo.getCardExpMonth())
                                .put("ExpirationYear", orderInfo.getCardExpYear())
                                .put("CardCode", orderInfo.getCardCvc())
                                .put("NameOnAccount", orderInfo.getBillingFirstName() + " " + orderInfo.getBillingLastName())
                        )
                )
                .getMap();
    }

    public CentinelRequest mapAuthenticateRequest(OrderRecord orderRecord, OrderInfo orderInfo) {
        CentinelRequest centinelRequest = baseRequest(orderInfo);

        centinelRequest.add("MsgType", AUTHENTICATE_MESSAGE_TYPE);

        centinelRequest.add("OrderId", orderRecord.getOrderId());
        centinelRequest.add("TransactionType", CARD_TRANSACTION_TYPE);
        centinelRequest.add("PAResPayload", orderRecord.getPayloadResponse());

        return centinelRequest;
    }

    public CentinelRequest mapLookupRequest(HttpServletRequest servletRequest, OrderInfo orderInfo) {
        CentinelRequest centinelRequest = baseRequest(orderInfo);

        centinelRequest.add("MsgType", LOOKUP_MESSAGE_TYPE);

        centinelRequest.add("TransactionType", CARD_TRANSACTION_TYPE);
        centinelRequest.add("UserAgent", servletRequest.getHeader("User-Agent"));
        centinelRequest.add("BrowserHeader", servletRequest.getHeader("Accept"));
        centinelRequest.add("IPAddress", servletRequest.getRemoteAddr());

        centinelRequest.add("OrderNumber", orderInfo.getOrderNumber());
        centinelRequest.add("OrderDescription", orderInfo.getOrderDescription());
        centinelRequest.add("Amount", orderInfo.getAmount());
        centinelRequest.add("CurrencyCode", USD_CURRENCY_CODE);
        //centinelRequest.add("ShippingAmount", orderInfo.getShippingAmount());
        centinelRequest.add("TaxAmount", orderInfo.getTaxAmount());
        //centinelRequest.add("GiftCardAmount", orderInfo.getGiftCardAmount());
        //centinelRequest.add("Recurring", orderInfo.getRecurring());
        centinelRequest.add("OrderChannel", PAYMENT_PAGE_ORDER_CHANNEL);
        centinelRequest.add("ProductCode", PHYSICAL_DELIVERY_PRODUCT_CODE);
        centinelRequest.add("TransactionMode", ECOMMERCE_TRANSACTION_MODE);
        centinelRequest.add("CategoryCode", orderInfo.getCategoryCode());
        centinelRequest.add("MerchantData", MERCHANT_DATA);

        centinelRequest.add("BillingFirstName", orderInfo.getBillingFirstName());
        centinelRequest.add("BillingMiddleName", orderInfo.getBillingMiddleName());
        centinelRequest.add("BillingLastName", orderInfo.getBillingLastName());
        centinelRequest.add("BillingAddress1", orderInfo.getBillingAddress1());
        centinelRequest.add("BillingAddress2", orderInfo.getBillingAddress2());
        centinelRequest.add("BillingCity", orderInfo.getBillingCity());
        centinelRequest.add("BillingState", orderInfo.getBillingState());
        centinelRequest.add("BillingPostalCode", orderInfo.getBillingPostalCode());
        centinelRequest.add("BillingCountryCode", UNITED_STATES_COUNTRY_CODE);
        centinelRequest.add("BillingPhone", orderInfo.getBillingPhone());

        centinelRequest.add("ShippingFirstName", orderInfo.getShippingFirstName());
        centinelRequest.add("ShippingMiddleName", orderInfo.getShippingMiddleName());
        centinelRequest.add("ShippingLastName", orderInfo.getShippingLastName());
        centinelRequest.add("ShippingAddress1", orderInfo.getShippingAddress1());
        centinelRequest.add("ShippingAddress2", orderInfo.getShippingAddress2());
        centinelRequest.add("ShippingCity", orderInfo.getShippingCity());
        centinelRequest.add("ShippingState", orderInfo.getShippingState());
        centinelRequest.add("ShippingPostalCode", orderInfo.getShippingPostalCode());
        centinelRequest.add("ShippingCountryCode", UNITED_STATES_COUNTRY_CODE);
        centinelRequest.add("ShippingPhone", orderInfo.getShippingPhone());

        centinelRequest.add("EMail", orderInfo.getEmail());

        centinelRequest.add("Item_Name_1", orderInfo.getItem_Name_1());
        centinelRequest.add("Item_Desc_1", orderInfo.getItem_Desc_1());
        centinelRequest.add("Item_SKU_1", orderInfo.getItem_SKU_1());
        centinelRequest.add("Item_Price_1", orderInfo.getItem_Price_1());
        centinelRequest.add("Item_Quantity_1", orderInfo.getItem_Quantity_1());

        centinelRequest.add("CardNumber", orderInfo.getCardNumber());
        centinelRequest.add("CardExpMonth", orderInfo.getCardExpMonth());
        centinelRequest.add("CardExpYear", orderInfo.getCardExpYear());
        //centinelRequest.add("RecurringFrequency", orderInfo.getRecurringFrequency());
        //centinelRequest.add("RecurringEnd", orderInfo.getRecurringEnd());
        //centinelRequest.add("Installment", orderInfo.getInstallment());
        //centinelRequest.add("AcquirerPassword", orderInfo.getAcquirerPassword());

        return centinelRequest;
    }

}
