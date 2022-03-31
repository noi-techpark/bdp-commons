package it.bz.odh.spreadsheets.utils;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * To upload files to an S3 bucket
 */
@Component
public class S3FileUtil {

    private Logger logger = LoggerFactory.getLogger(S3FileUtil.class);

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

    /**
     * Uploads a fileInputStream to a S3 bucket
     * 
     * @param fileInputStream
     * @param fileName
     * @param contentLength
     * @param lastModified
     * @throws AmazonServiceException
     * @throws AmazonClientException
     * @throws InterruptedException
     */
    public void uploadFile(InputStream fileInputStream, String fileName, int contentLength)
            throws AmazonServiceException, AmazonClientException, InterruptedException {
        logger.info("upload of file: " + fileName + " to S3");
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);

        Upload upload = transferManager.upload(bucketName, fileName, fileInputStream, objectMetadata);
        // upload.waitForCompletion();

        logger.info("upload of file: " + fileName + " to S3 done");
    }

    /**
     * Returns the object list of a S3 bucket in form of a map with key: objectname
     * and value: lastModifiedDate
     * 
     * @return a map of the objects
     */
    public Map getObjectListing() {
        logger.info("Getting S3 object list");
        Map objectListing = new HashMap<String, Date>();

        ListObjectsV2Request listRequest = new ListObjectsV2Request().withBucketName(bucketName);

        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(listRequest);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                objectListing.put(objectSummary.getKey(), objectSummary.getLastModified());
            }
            listRequest.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        logger.info("Getting S3 object list done");
        return objectListing;
    }

}
