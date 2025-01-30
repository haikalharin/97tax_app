package com.repnox.nineseventax.features.ecommerce.chaseorbital;

import com.paymentech.orbital.sdk.interfaces.ResponseIF;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class OrbitalResponse {

	private String xmlReturn = null;

	private Boolean isGood;

	private Boolean isError;

	private Boolean isQuickResponse;

	private Boolean isApproved;

	private Boolean isDeclined;

	private String authCode;

	private String txRefNum;

	private String responseCode;

	private String status;

	private String message;

	private String aVSCode;

	private String cVV2ResponseCode;

	private String orderId = null;

	private String amountRequestedNoDecimal = null;

	private String amountRedeemedNoDecimal = null;

	private String remainingBalanceNoDecimal = null;

	private String customerRefNum = null;

	public static OrbitalResponse convertToGatewayReturnObject(ResponseIF response, Double refundAmount) {
		OrbitalResponse orbitalResponse = OrbitalResponse.builder()
				.xmlReturn(response.toXmlString())
				.isGood(response.isGood())
				.isError(response.isError())
				.isQuickResponse(response.isQuickResponse())
				.isApproved(response.isApproved())
				.isDeclined(response.isDeclined())
				.authCode(response.getAuthCode())
				.txRefNum(response.getTxRefNum())
				.responseCode(response.getResponseCode())
				.status(response.getStatus())
				.message(response.getMessage())
				.aVSCode(response.getAVSResponseCode())
				.cVV2ResponseCode(response.getCVV2RespCode())
				.orderId(response.getValue("orderId"))
				.build();

		if (!response.isError()) {
			orbitalResponse.setAmountRequestedNoDecimal(String.valueOf(refundAmount));
			orbitalResponse.setAmountRedeemedNoDecimal(String.valueOf(refundAmount));
		}

		return orbitalResponse;
	}
}

