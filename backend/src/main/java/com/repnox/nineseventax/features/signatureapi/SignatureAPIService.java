package com.repnox.nineseventax.features.signatureapi;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.repnox.nineseventax.features.ein.EinOrder;
import com.repnox.nineseventax.features.pdfcreator.PdfFormFilling;
import com.repnox.nineseventax.features.utils.PdfFields;

import lombok.Builder;
import lombok.Data;

@Component
public class SignatureAPIService {

    @Value("${signatureapi.app.url}")
    private String appURL;

    @Value("${signatureapi.apikey}")
    private String apiKey;

    @Autowired
    private PdfFormFilling pdfCreator;

    private JsonObject createEnvelope(EinOrder order, String signerEmail, String signerName) throws IOException {
        InputStream is = pdfCreator.PdfCreatorForFSS4(order, false);
        byte[] pdfContent = IOUtils.toByteArray(is);

        SigningDocument signingDocument = SigningDocument.builder()
                .fileContent(pdfContent).fileName("fss4.pdf")
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.signatureapi.com/v1/files");
            post.setHeader("X-API-Key", apiKey);
            CloseableHttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 400) {
                throw new IOException("Failed to create file: " + statusCode);
            }
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject fileInfo = JsonParser.parseString(responseBody).getAsJsonObject();
            String fileId = fileInfo.get("id").getAsString();
            String putUrl = fileInfo.get("put_url").getAsString();

            HttpPut put = new HttpPut(putUrl);
            put.setEntity(new ByteArrayEntity(signingDocument.getFileContent()));
            httpClient.execute(put);

            HttpPost envelope = new HttpPost("https://api.signatureapi.com/v1/envelopes");
            envelope.setHeader("X-API-Key", apiKey);
            JsonObject envelopeBody = new JsonObject();
            envelopeBody.addProperty("title", "FSS4");
            JsonObject document = new JsonObject();
            document.addProperty("url", "https://api.signatureapi.com/v1/files/" + fileId);
            JsonObject signer = new JsonObject();
            signer.addProperty("key", "sign_here");
            signer.addProperty("type", "signature");
            signer.addProperty("recipient_key", "human");
            signer.addProperty("height", 45);
            document.add("places", JsonParser.parseString("[" + signer.toString() + "]").getAsJsonArray());
            envelopeBody.add("documents", JsonParser.parseString("[" + document.toString() + "]").getAsJsonArray());
            JsonObject recipient = new JsonObject();
            recipient.addProperty("type", "signer");
            recipient.addProperty("key", "human");
            recipient.addProperty("name", signerName);
            recipient.addProperty("email", signerEmail);
            recipient.addProperty("ceremony_creation", "manual");
            envelopeBody.add("recipients", JsonParser.parseString("[" + recipient.toString() + "]").getAsJsonArray());
            envelope.setEntity(new ByteArrayEntity(envelopeBody.toString().getBytes()));
            response = httpClient.execute(envelope);
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 400) {
                String errorBody = EntityUtils.toString(response.getEntity());
                throw new IOException("Failed to create envelope: " + statusCode + " [" + errorBody + "]");
            }
            responseBody = EntityUtils.toString(response.getEntity());
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }

    private JsonObject createCeremony(JsonObject envelope, String redirectURL) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String recipientId = envelope.getAsJsonArray("recipients").get(0).getAsJsonObject().get("id").getAsString();
            HttpPost post = new HttpPost("https://api.signatureapi.com/v1/recipients/" + recipientId + "/ceremony");
            post.setHeader("X-API-Key", apiKey);
            JsonObject ceremonyBody = new JsonObject();
            ceremonyBody.addProperty("redirect_url", redirectURL);
            JsonObject authentication = new JsonObject();
            authentication.addProperty("type", "custom");
            authentication.addProperty("provider", "i7tax");
	        authentication.add("data", new JsonObject()); // Add empty "data" key
            ceremonyBody.add("authentication", authentication);
            post.setEntity(new ByteArrayEntity(ceremonyBody.toString().getBytes()));
            CloseableHttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 400) {
                String errorBody = EntityUtils.toString(response.getEntity());
                throw new IOException("Failed to create ceremony: " + statusCode + " [" + errorBody + "]");
            }
            String responseBody = EntityUtils.toString(response.getEntity());
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }

    public SignatureAPIRedirect getFSS4Redirect(EinOrder order) throws IOException {
        Long id = order.getId();
        String signerName = PdfFields.FSS4.Extractor.extractSignatureName(order);
        String signerEmail = order.getEmail();
        String returnPath = "/ein-docu-sign?order=" + id + "&correlationId=" + order.getCorrelationId();

        JsonObject envelope = null;
        String envelopeId = order.getDsEnvelopeId();
        if (StringUtils.isBlank(envelopeId)) {
            envelope = createEnvelope(order, signerEmail, signerName);
        } else {
            // Get the existing envelope from signatureapi
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet get = new HttpGet("https://api.signatureapi.com/v1/envelopes/" + envelopeId);
                get.setHeader("X-API-Key", apiKey);
                CloseableHttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 400) {
                    envelope = createEnvelope(order, signerEmail, signerName);
                } else {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    envelope = JsonParser.parseString(responseBody).getAsJsonObject();
                    String envelopeStatus = envelope.get("status").getAsString();
                    if (
                        envelopeStatus.equals("completed") ||
                        envelopeStatus.equals("failed") ||
                        envelopeStatus.equals("canceled")
                    ) {
                        envelope = createEnvelope(order, signerEmail, signerName);
                    }
                }
            }
        }
        JsonObject ceremony = createCeremony(envelope, appURL + returnPath);

        return SignatureAPIRedirect.builder()
                .redirectUrl(ceremony.get("url").getAsString()).envelopeId(envelope.get("id").getAsString())
                .build();
    }

    public boolean isSigned(String envelopeId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("https://api.signatureapi.com/v1/envelopes/" + envelopeId);
            get.setHeader("X-API-Key", apiKey);
            CloseableHttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 400) {
                throw new IOException("Failed to retrieve envelope: " + statusCode);
            }
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject envelope = JsonParser.parseString(responseBody).getAsJsonObject();
            return envelope.get("status").getAsString().equals("completed");
        }
    }

    public byte[] getSignedDocument(String envelopeId, int waitForContentSeconds) throws IllegalArgumentException, IOException, InterruptedException {
        if (waitForContentSeconds < 0) {
            throw new IllegalArgumentException("waitForContentSeconds must be >= 0");
        }
        long now = System.currentTimeMillis();
        long stopTime = now + waitForContentSeconds * 1000;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            while (now <= stopTime) {
                HttpGet get = new HttpGet("https://api.signatureapi.com/v1/envelopes/" + envelopeId);
                get.setHeader("X-API-Key", apiKey);
                CloseableHttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 400) {
                    throw new IOException("Failed to retrieve envelope: " + statusCode);
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                JsonObject envelope = JsonParser.parseString(responseBody).getAsJsonObject();

                // Available envelope status: draft, processing, in_progress, completed, failed, canceled
                String envelopeStatus = envelope.get("status").getAsString();
                if (envelopeStatus.equals("in_progress")) {
                    Thread.sleep(1000); // Wait for 1 second before retrying
                    now = System.currentTimeMillis();
                    continue;
                }

                if (!envelopeStatus.equals("completed")) {
                    throw new IOException("Envelope not signable, canceled, or failed");
                }

                // envelopeStatus is completed
                JsonObject deliverable = envelope.getAsJsonObject("deliverable");

                // Available deliverable status: processing, generated, failed
                JsonElement deliverableStatusElement = deliverable.get("status");
                String deliverableStatus = deliverableStatusElement.getAsString();
                if (deliverableStatus.equals("failed")) {
                    throw new IOException("Failed to generate signature deliverable");
                }

                if (deliverableStatus.equals("processing")) {
                    Thread.sleep(1000); // Wait for 1 second before retrying
                    now = System.currentTimeMillis();
                    continue;
                }

                // Deliverable status is generated
                JsonElement urlElement = deliverable.get("url");
                String url = urlElement.getAsString();
                response = httpClient.execute(new HttpGet(url));
                statusCode = response.getStatusLine().getStatusCode();

                if (statusCode >= 400) {
                    throw new IOException("Failed to retrieve signed document: " + statusCode);
                }

                return EntityUtils.toByteArray(response.getEntity());
            }
        }

        throw new IOException("Failed to retrieve signed document after " + waitForContentSeconds + " seconds");
    }

    @Data
    @Builder
    public static class SigningDocument {
        private byte[] fileContent;
        private String fileName;
    }

    @Data
    @Builder
    public static class SignatureAPIRedirect {
        private String redirectUrl;
        private String envelopeId;
    }

}
