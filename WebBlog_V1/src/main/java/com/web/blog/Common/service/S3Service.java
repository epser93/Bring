package com.web.blog.Common.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class S3Service {
    public static final String CLOUD_FRONT_DOMAIN_NAME = "dp02rmdt3p3bw.cloudfront.net";
//    public static final String CLOUD_FRONT_DOMAIN_NAME = "lawliet0521.s3.amazonaws.com";

    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName;
    }

    public String upload(MultipartFile file, long id, int num, String nickname) throws IOException {
        // 고유한 key 값을 갖기위해 현재 시간을 postfix로 붙여줌
        SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        String time = format.format(date);
        String fileName = nickname + "-" + time + "___" + id + "-" + num + "___" +file.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        // 파일 업로드
        s3Client.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName;
    }

    public String rename(String original, String fileName, long id, int num, String nickname) {
        SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        String time = format.format(date);
        String rename = original.replace("___-100-", "___" + id + "-");
        CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucket,
                original, bucket, rename);
        s3Client.copyObject(copyObjRequest);
        s3Client.deleteObject(new DeleteObjectRequest(bucket, original));
        return rename;
    }

    public void delete(String fileName) {
        s3Client.deleteObject(bucket, fileName);
    }
}