package it.bz.odh.spreadsheets.utils;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * To upload files to an S3 bucket
 */
@Component
public class S3FileUploadUtil {

    private Logger logger = LogManager.getLogger(S3FileUploadUtil.class);

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Value("${aws.access-secret-key}")
    private String accessSecretKey;

    private AmazonS3 amazonS3;

    private TransferManager transferManager;

    @PostConstruct
    public void postConstruct() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, accessSecretKey);

        amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.EU_WEST_1)
                .build();

        transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                // .withMultipartUploadThreshold((long) (5 * 1024 * 1025))
                .build();

    }

    public void uploadFile(InputStream fileInputStream, String fileName, int contentLength) throws AmazonServiceException, AmazonClientException, InterruptedException {
        
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        
        Upload upload = transferManager.upload(bucketName, fileName, fileInputStream, objectMetadata);
        // upload.waitForCompletion();
        
        logger.info("upload of file to S3 done: " + fileName);
    }

}
