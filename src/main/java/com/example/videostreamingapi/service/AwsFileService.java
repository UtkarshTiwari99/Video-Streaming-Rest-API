package com.example.videostreamingapi.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Service
public class AwsFileService {
        @Autowired
        private AmazonS3 amazonS3;
        @Value("${aws.s3.bucket-name}")
        private String s3BucketName;

        private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
            final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            try (final FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(multipartFile.getBytes());
            } catch (IOException e) {

            }
            return file;
        }

        public boolean isExist(String key){
            return amazonS3.doesObjectExist(s3BucketName,key);
        }

        @Async
        public S3ObjectInputStream findByNameWithRange(String fileName,long start,long end) {
            GetObjectRequest getObjectRequest = new GetObjectRequest(s3BucketName, fileName).withRange(start,end);
            return amazonS3.getObject(getObjectRequest).getObjectContent();
        }

        @Async
    public S3ObjectInputStream findByName(String fileName) {
        return amazonS3.getObject(s3BucketName,fileName).getObjectContent();
    }

        @Async
        public Long findSizeByName(String fileName) {
          return amazonS3.getObject(s3BucketName, fileName).getObjectMetadata().getContentLength();
        }

    @Async
        public void delete(String key){amazonS3.deleteObject(s3BucketName,key);}

        @Async
        public void save(final MultipartFile multipartFile,String filename) {
            try {
                final File file = convertMultiPartFileToFile(multipartFile);
                final PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, filename, file);
                amazonS3.putObject(putObjectRequest);
                Files.delete(file.toPath());
            } catch (AmazonServiceException e) {
                System.out.println(e);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
}