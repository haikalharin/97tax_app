package com.repnox.nineseventax.features.efile;

import gov.irs.a2a.mef.mefheader.TestCdType;
import gov.irs.mef.exception.ServiceException;
import gov.irs.mef.exception.ToolkitException;
import gov.irs.mef.inputcomposition.*;
import gov.irs.mef.services.ServiceContext;
import gov.irs.mef.services.data.ETIN;
import gov.irs.mef.services.msi.LoginClient;
import gov.irs.mef.services.msi.LoginResult;
import gov.irs.mef.services.msi.LogoutClient;
import gov.irs.mef.services.transmitter.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.GregorianCalendar;

@Service
@Slf4j
public class IRSSubmissionService {

    private static final Logger LOG = LoggerFactory.getLogger(IRSSubmissionService.class);

    /**
     * Practitioner PIN code will be similar for all submissions.
     */
    public static final String PRACTITIONER_PIN_CODE = "82549";
    public static final String ACCEPTED_STATUS = "Accepted";
    public static final String REJECTED_STATUS = "Rejected";

    @Value("${irs.efile.efin}")
    private String efin;

    @Value("${irs.efile.application-id}")
    private String appId;

    @Value("${irs.efile.cd-type}")
    public String cdType;

    @Value("${irs.efile.etin}")
    private String etin;

    @Value("${irs.efile.client.base.path}")
    public String clientBasePath;

    @Value("${irs.efile.cert.filename}")
    public String keyFilename;

    @Value("${irs.efile.cert.key.alias}")
    public String keyAlias;

    @Value("${irs.efile.cert.key.password}")
    public String keyPassword;

    public GetAckResult submitFor(String submissionId, String submissionXMLData, String manifestXMLData) {
        try {
            LOG.info("=== IRS submission Started, submissionId = " + submissionId + " ===");
            File keyFile = new File(clientBasePath + keyFilename);
            TestCdType theType = EnumUtils.getEnum(TestCdType.class, cdType);
            ServiceContext serviceContext = irsLogin(keyFile, keyPassword, keyAlias, etin, appId, theType);

            try {
                invokeIRSService(submissionXMLData, manifestXMLData, submissionId, serviceContext);
                GetAckResult ackResult = getSubmissionAck(serviceContext, submissionId);
                LOG.info("=== IRS submission Completed, submissionId = " + submissionId + " ===");
                return ackResult;
            } catch (Exception e) {
                LOG.error("ERROR: IRS e-File submission error: submitFor(): 1781");
                throw new EFileException("IRS e-File submission error: submitFor(): 1781", e);
            } finally {
                irsLogout(serviceContext);
            }

        } catch (Exception e) {
            LOG.error("ERROR: IRS submission error: submitFor(): 1782");
            throw new EFileException("IRS submission error: submitFor(): 1782", e);
        }
    }

    private ServiceContext irsLogin(File certFile, String certPassword, String certAlias, String ETIN, String appId, TestCdType cdType) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(certFile);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(fileInputStream, certPassword.toCharArray());
        ETIN etin = new ETIN(ETIN);
        ServiceContext serviceContext = new ServiceContext(etin, appId, cdType);
        LoginClient client = new LoginClient();
        LoginResult result = client.invoke(serviceContext, certFile, certPassword, certAlias);
        LOG.info("=== Login Completed ===");
        return serviceContext;
    }

    private SendSubmissionsResult invokeIRSService(String submissionXMLData, String manifestXMLData,
                                                   String submissionId, ServiceContext loginServiceContext) throws Exception {
        SubmissionXML submission = new SubmissionXML("submission_" + submissionId + ".xml", submissionXMLData);
        SubmissionManifest manifest = new SubmissionManifest("manifest_" + submissionId + ".xml", manifestXMLData);
        SubmissionArchive subArchive = SubmissionBuilder.createIRSSubmissionArchive(submissionId, manifest, submission, (BinaryAttachment[]) null);
        PostmarkedSubmissionArchive postmarkedArchive1 = SubmissionBuilder.createPostmarkedSubmissionArchive(subArchive, new GregorianCalendar());
        PostmarkedSubmissionArchive[] postmarkedArchiveArray = new PostmarkedSubmissionArchive[]{postmarkedArchive1};
        SendSubmissionsClient client = new SendSubmissionsClient();
        SubmissionContainer submissions = SubmissionBuilder.createSubmissionContainer(postmarkedArchiveArray);
        SendSubmissionsResult result = client.invoke(loginServiceContext, submissions);
        LOG.info("=== Submission call Completed ===");
        return result;
    }

    private GetAckResult getSubmissionAck(ServiceContext serviceContext, String submissionID) throws ToolkitException, ServiceException {
        String ackDir = clientBasePath + "ack/";
        GetAckClient ackClient = new GetAckClient(new File(ackDir));
        // TODO: utilize acksClient when requesting ackResponses
        // GetAcksClient acksClient = new GetAcksClient(new File(ackDir));
        GetAckResult ackResult = ackClient.invoke(serviceContext, submissionID);
        LOG.info("=== Get Submission Ack Completed ===");
        return ackResult;
    }

    private void irsLogout(ServiceContext serviceContext) throws ToolkitException, ServiceException {
        LogoutClient logout = new LogoutClient();
        logout.invoke(serviceContext);
        LOG.info("=== Logout Completed ===");
    }

}
