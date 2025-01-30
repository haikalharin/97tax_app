package com.repnox.nineseventax.features.docusign;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.model.*;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.pdfcreator.PdfFormFilling;
import com.repnox.nineseventax.features.utils.PdfFields;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DocuSignService {

    @Value("${docusign.account.userid}")
    private String accountUserId;

    @Value("${docusign.account.apiid}")
    private String accountAPIId;

    @Value("${docusign.key.clientid}")
    private String keyId;

    @Value("${docusign.key.path}")
    private String keyPath;

    @Value("${docusign.app.url}")
    private String appURL;

    @Value("${docusign.restapi}")
    private String restApiURL;

    @Autowired
    private PdfFormFilling pdfCreator;

    private static final String BEARER_AUTHENTICATION = "Bearer ";

    /**
     * Envelopes are expired after 120 days, Recipient View URL expires after 5 minutes
     */
    private static final long ENVELOPE_EXPIRATION_MILLIS = 119L * 24 * 60 * 60 * 1000L;

    // Details on Envelope statuses: https://developers.docusign.com/docs/esign-rest-api/esign101/concepts/envelopes/status-codes/
    private static final List<String> SIGNED_STATUSES = Arrays.asList("completed", "signed");

    public DocuSignRedirect getFSS4Redirect(EinOrder order) throws IOException, ApiException {
        Long id = order.getId();
        String signerName = PdfFields.FSS4.Extractor.extractSignatureName(order);
        String signerEmail = order.getEmail();
        String signerClientId = String.valueOf(id);
        String returnPath = "/ein-docu-sign?order=" + id + "&correlationId=" + order.getCorrelationId();

        ApiClient apiClient = initClient();
        String envelopeId = order.getDsEnvelopeId();
        Long envelopeMillis = order.getDsEnvelopeMillis();
        long envelopeAgeMillis = envelopeMillis == null
                ? ENVELOPE_EXPIRATION_MILLIS + 1
                : System.currentTimeMillis() - envelopeMillis;

        if (StringUtils.isBlank(envelopeId) || envelopeAgeMillis > ENVELOPE_EXPIRATION_MILLIS) { // envelope is not created or expired
            InputStream is = pdfCreator.PdfCreatorForFSS4(order, false);
            byte[] pdfContent = IOUtils.toByteArray(is);

            SigningDocument document = SigningDocument.builder()
                    .fileContent(pdfContent).fileName("fss4.pdf")
                    .build();

            EnvelopeDefinition envelope = EmbeddedSigningHelper.makeEnvelope(
                    signerEmail, signerName, signerClientId,
                    document.getFileContent(), document.getFileName());

            envelopeId = EmbeddedSigningHelper.createEnvelope(apiClient, accountAPIId, envelope);
        }

        // Create the recipient view, the embedded signing
        RecipientViewRequest viewRequest = EmbeddedSigningHelper.makeRecipientViewRequest(
                signerEmail, signerName, signerClientId,
                appURL + returnPath, appURL + "/");

        ViewUrl viewUrl = EmbeddedSigningHelper.embeddedSigning(
                apiClient, accountAPIId,
                envelopeId, viewRequest);

        // Redirect the user to the embedded signing. Don't use an iFrame (bad practice)!
        return DocuSignRedirect.builder()
                .redirectUrl(viewUrl.getUrl()).envelopeId(envelopeId)
                .build();
    }

    public boolean isSigned(String envelopeId) throws IOException, ApiException {
        ApiClient apiClient = initClient();
        try {
            Envelope envelope = EmbeddedSigningHelper.getEnvelope(apiClient, accountAPIId, envelopeId);

            return SIGNED_STATUSES.contains(envelope.getStatus());
        } catch (Exception ex) {
            // Exception might be thrown e.g. when there is no envelope found (cleaned out old/unused envelope by DocuSign, etc.)
            return false;
        }
    }

    public byte[] getSignedDocument(String envelopeId) throws IOException, ApiException {
        ApiClient apiClient = initClient();
        byte[] documentContent = EmbeddedSigningHelper.getSignedDocument(apiClient, accountAPIId, envelopeId);
        return documentContent;
    }

    private ApiClient initClient() throws IOException, ApiException {
        // Get temporary access token and set it up
        ApiClient apiClient = new ApiClient(restApiURL);
        // ApiClient#deriveOAuthBasePathFromRestBasePath() will be called from the constructor to propagate oAuthBasePath correctly
        // apiClient.setOAuthBasePath("account-d.docusign.com");
        List<String> scopes = Arrays.asList("signature", "impersonation");

        InputStream keyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(keyPath);
        byte[] privateKeyBytes = IOUtils.toByteArray(keyIS);
        OAuth.OAuthToken oAuthToken = apiClient.requestJWTUserToken(
                keyId, accountUserId,
                scopes, privateKeyBytes, 600);

        String accessToken = oAuthToken.getAccessToken();
        apiClient.addDefaultHeader(HttpHeaders.AUTHORIZATION, BEARER_AUTHENTICATION + accessToken);
        return apiClient;
    }

    @Data
    @Builder
    public static class SigningDocument {
        private byte[] fileContent;
        private String fileName;
    }

    @Data
    @Builder
    public static class DocuSignRedirect {
        private String redirectUrl;
        private String envelopeId;
    }

}
