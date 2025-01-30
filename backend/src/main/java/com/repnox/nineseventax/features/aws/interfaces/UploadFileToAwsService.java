package com.repnox.nineseventax.features.aws.interfaces;

import java.io.File;

public interface UploadFileToAwsService {

    String upload(File file, String fileName);

}
