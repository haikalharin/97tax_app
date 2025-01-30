package com.repnox.nineseventax.features.ecommerce.chaseorbital.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.repnox.nineseventax.features.payment.response.TransactionResponse;

public interface OrbitalFetchTransactionService {

    TransactionResponse fetch(String version, String bin, String terminalId, String txrefnum) throws Exception;

}
