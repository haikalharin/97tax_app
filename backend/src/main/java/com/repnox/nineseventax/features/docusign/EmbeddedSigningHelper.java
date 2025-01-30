package com.repnox.nineseventax.features.docusign;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;

import java.util.Collections;

public final class EmbeddedSigningHelper {

    // Anchor text within the signed document - usually of the same color as a background of the document.
    // Signature element will be put over it.
    private static final String ANCHOR_TEXT = "/sign_here/";
    private static final int ANCHOR_OFFSET_Y = 0;
    private static final int ANCHOR_OFFSET_X = 0;

    public static ViewUrl embeddedSigning(ApiClient apiClient, String accountId,
                                          String envelopeId, RecipientViewRequest viewRequest) throws ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        return envelopesApi.createRecipientView(accountId, envelopeId, viewRequest);
    }

    public static String createEnvelope(ApiClient apiClient, String accountId, EnvelopeDefinition envelope) throws ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envelope);
        return envelopeSummary.getEnvelopeId();
    }

    public static Envelope getEnvelope(ApiClient apiClient, String accountId, String envelopeId) throws ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        Envelope envelope = envelopesApi.getEnvelope(accountId, envelopeId);
        return envelope;
    }

    public static byte[] getSignedDocument(ApiClient apiClient, String accountId, String envelopeId) throws ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        byte[] firstDocument = envelopesApi.getDocument(accountId, envelopeId, "combined");
        return firstDocument;
    }

    public static RecipientViewRequest makeRecipientViewRequest(String signerEmail, String signerName,
                                                                String clientUserId, String returnURL, String pingURL) {
        RecipientViewRequest viewRequest = new RecipientViewRequest();

        // Mostly a stub state value, since it can be changed/spoofed
        String stateValue = "&signed=1";
        viewRequest.setReturnUrl(returnURL + stateValue);

        // Might be SMS, etc. if needed
        String authenticationMethod = "none";
        viewRequest.setAuthenticationMethod(authenticationMethod);

        // Recipient information must match embedded recipient info, used to create the envelope.
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        viewRequest.setClientUserId(clientUserId);

        // NOTE: The pings will only be sent if the pingUrl is https based
        String pingFrequency = "600"; // seconds
        viewRequest.setPingFrequency(pingFrequency);
        viewRequest.setPingUrl(pingURL);

        return viewRequest;
    }

    public static EnvelopeDefinition makeEnvelope(String signerEmail, String signerName, String signerClientId,
                                                  byte[] documentContent, String documentName) {
        // Create a signer recipient to sign the document, identified by name and email
        // We set the clientUserId to enable embedded signing for the recipient
        Signer signer = new Signer();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.clientUserId(signerClientId);
        signer.recipientId("1");
        signer.setTabs(EnvelopeHelper.createSingleSignerTab(ANCHOR_TEXT, ANCHOR_OFFSET_Y, ANCHOR_OFFSET_X));

        // Add the recipient to the envelope object
        Recipients recipients = new Recipients();
        recipients.setSigners(Collections.singletonList(signer));

        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setRecipients(recipients);
        Document doc = EnvelopeHelper.createDocument(documentContent, documentName, "3");
        envelopeDefinition.setDocuments(Collections.singletonList(doc));

        // Request that the envelope be sent by setting status to "sent" ("created" is for a draft).
        envelopeDefinition.setStatus(EnvelopeHelper.ENVELOPE_STATUS_SENT);
        return envelopeDefinition;
    }

}
