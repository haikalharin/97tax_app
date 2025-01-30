package com.repnox.nineseventax.features.ecommerce.chaseorbital;

import java.text.SimpleDateFormat;
import com.paymentech.orbital.sdk.interfaces.TransactionProcessorIF;
import com.paymentech.orbital.sdk.transactionProcessor.TransactionProcessor;
import com.paymentech.orbital.sdk.util.exceptions.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

public class ChaseOrbitalGatewayObject extends Thread {
	public static String MERCHANT_BIN_ID = "000002";
	public static String MERCHANT_TERMINAL_ID = "700000013809";
	public static String ORBITAL_USERNAME = "TRSTest01";
	public static String ORBITAL_PASSWORD = "m3s5c49Vy";
	public static String key = "12345678901234567890123456789044";
	public static String MerchantName = "SEMplest";

	public static SimpleDateFormat MMDDYYYY = new SimpleDateFormat("MMddyyyy");
	public static final String ORDERIDSTRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public static final int ORDERIDLENGTH = 22;

	private static final Logger logger = LoggerFactory.getLogger(ChaseOrbitalGatewayObject.class);
	private TransactionProcessorIF transactionProcessor = null;
	private AESBouncyCastle aes = null;
	private final Object object;

	public ChaseOrbitalGatewayObject(Object object) {
		this.object = object;
	}

	public void run() {
		try {
			aes = AESBouncyCastle.getInstance(key);
			transactionProcessor = new TransactionProcessor();
		} catch (InitializationException iex) {
			logger.error("TransactionProcessor failed to initialize " + iex.getMessage(), iex);
		} catch (Exception e) {
			logger.error("Exception in ChaseOrbitalGatewayObject " + e.getMessage(), e);
		}
		synchronized (object) {
			object.notify();
		}
	}

	public TransactionProcessorIF getTransactionProcessor() {
		return transactionProcessor;
	}

	public AESBouncyCastle getAes() {
		return aes;
	}
}