package com.repnox.nineseventax.features.docusign;

import com.docusign.esign.model.Document;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Tabs;
import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.Base64;

public final class EnvelopeHelper {

    public static final String ENVELOPE_STATUS_SENT = "sent";

    private EnvelopeHelper() {
    }

    public static Document createDocument(byte[] data, String documentName, String documentId) {
        Document document = new Document();
        document.setDocumentBase64(Base64.getEncoder().encodeToString(data));
        document.setName(documentName);
        String extension = FilenameUtils.getExtension(documentName);
        document.setFileExtension(extension);
        document.setDocumentId(documentId);
        return document;
    }

    public static SignHere createSignHere(String anchorString, int yOffsetPixels, int xOffsetPixels) {
        SignHere signHere = new SignHere();
        signHere.setAnchorString(anchorString);
        signHere.setAnchorUnits("pixels");
        signHere.setAnchorYOffset(String.valueOf(yOffsetPixels));
        signHere.setAnchorXOffset(String.valueOf(xOffsetPixels));
        return signHere;
    }

    public static Tabs createSingleSignerTab(String anchorString, int yOffsetPixels, int xOffsetPixels) {
        SignHere signHere = createSignHere(anchorString, yOffsetPixels, xOffsetPixels);
        return createSignerTabs(signHere);
    }

    private static Tabs createSignerTabs(SignHere... signs) {
        Tabs signerTabs = new Tabs();
        signerTabs.setSignHereTabs(Arrays.asList(signs));
        return signerTabs;
    }

}
