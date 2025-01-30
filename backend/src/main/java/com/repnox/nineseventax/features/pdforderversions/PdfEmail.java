package com.repnox.nineseventax.features.pdforderversions;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import com.repnox.nineseventax.features.utils.TaxConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PdfEmail {
    /**
     * This call sends a message based on a template.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PdfEmail.class);

    @Value("${email.smtp.auth.key}")
    private String authkey;

    @Value("${email.smtp.auth.secret}")
    private String secretkey;

    @Value("${email.from.email}")
    private String emailFrom;
    
    @Value("${email.from.name}")
    private String nameFrom;

    public MailjetResponse sendTemplateEmail(String file, String userEmail, String name, String fileName, Map<String,String> address, Integer templeteId) throws Exception {
       
        MailjetClient client = new MailjetClient(authkey, secretkey, new ClientOptions("v3.1"));
        JSONObject message = new JSONObject();

        message.put(
                Emailv31.Message.FROM,
                new JSONObject()
                    .put(Emailv31.Message.EMAIL, this.emailFrom)
                    .put(Emailv31.Message.NAME, this.nameFrom)
            ).put(Emailv31.Message.TO, new JSONArray()
            .put(new JSONObject()
                .put("Email", userEmail)
                .put("Name", name))).put(
                Emailv31.Message.TEMPLATEID,
                templeteId
            ).put(
                Emailv31.Message.TEMPLATELANGUAGE,
                true
            )
        .put(
                Emailv31.Message.VARIABLES,
                address
            )
            .put(Emailv31.Message.ATTACHMENTS, new JSONArray()
                        .put(new JSONObject()
                            .put("ContentType", "application/pdf")
                            .put("Filename", fileName+TaxConstants.PDF_EXTENSION)
                            .put("Base64Content", file)));
        

        MailjetRequest email = new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, (new JSONArray()).put(message));

        try {
            MailjetResponse response = client.post(email);
            if (response.getStatus() == 200) {
                return response;
            } else {
                throw null;
            }
        } catch (Exception e) {
            LOG.error("Failed to send email file = "+file,e);
            throw e;
        }
    }

}
