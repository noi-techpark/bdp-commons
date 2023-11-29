// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services;

import javax.annotation.PostConstruct;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto;

@Component
public class S3Service {

    private Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.aws.secretAccessKey}")
    private String secretKey;

    @Value("${aws.fileName}")
    private String fileName;

    @Value("${aws.region}")
    private String region;

    private ObjectMapper mapper;

    private S3Client s3;

    @PostConstruct
    public void postConstruct() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        mapper = new ObjectMapper();
    }

    public ForecastDto getForecastDto() throws InterruptedException, JsonMappingException, JsonProcessingException {
        logger.info("download of file: {} from S3", fileName);
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(fileName)
                .bucket(bucketName)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
        String string = objectBytes.asUtf8String();
        ForecastDto dto = mapper.readValue(string, ForecastDto.class);

        logger.info("download of file: {} to S3 done", fileName);
        return dto;
    }

}
