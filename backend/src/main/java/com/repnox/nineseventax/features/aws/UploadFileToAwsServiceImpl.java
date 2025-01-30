package com.repnox.nineseventax.features.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.repnox.nineseventax.features.aws.interfaces.UploadFileToAwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

@Service
@Slf4j
public class UploadFileToAwsServiceImpl implements UploadFileToAwsService {

    @Value("${aws.bucket.name}")
    private String awsBucketName;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Override
    public String upload(File file, String fileName) {
        Date nowDate = new Date();
        int year = 1900 + nowDate.getYear();
        int month = nowDate.getMonth() + 1;
        int dayOfMonth = nowDate.getDate();
        String yearString = String.valueOf(year);
        String monthString = month < 10 ? "0" + month : String.valueOf(month);
        String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
        String path = yearString + "/" + monthString + "/" + dayString;
        String uploadUrl = path + "/" + fileName;
        try {
            AWSCredentials awsCred = new BasicAWSCredentials(
                    this.awsAccessKey,
                    this.awsSecretKey);
            AmazonS3 s3client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCred))
                    .withRegion(Regions.US_EAST_2)
                    .build();
            if (!s3client.doesBucketExist(this.awsBucketName)) {
                s3client.createBucket(this.awsBucketName);
            }
            if (s3client.doesBucketExist(this.awsBucketName)) {
                s3client.putObject(
                        this.awsBucketName,
                        uploadUrl,
                        file);
            }
        } catch (Exception e) {
            uploadUrl = "";
            log.error("Error when uploading file to AWS : ", e);
        }

        return uploadUrl;
    }
}
