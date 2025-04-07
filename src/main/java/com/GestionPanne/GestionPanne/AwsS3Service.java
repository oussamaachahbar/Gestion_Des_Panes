package com.GestionPanne.GestionPanne;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.GestionPanne.GestionPanne.exception.OurException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AwsS3Service {

    private final String bucketName="gestionpannes3image";

    private final String awsS3AccessKey="AKIA6K5V7N5BLTLSUIG2";

    private final String awsS3SecretKey="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";

    public String saveImageToS3(MultipartFile photo) {
        String s3LocationImage = null;

        try {
            if (!photo.getContentType().startsWith("image")) {
                throw new OurException("Only image files are allowed.");
            }

            if (photo.getSize() > 100 * 1024 * 1024) {
                throw new OurException("Image size exceeds 5MB.");
            }

            String s3Filename = UUID.randomUUID().toString() + "-" + photo.getOriginalFilename();
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.US_EAST_2)
                    .build();

            InputStream inputStream = photo.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(photo.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Filename, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

            s3LocationImage = "https://" + bucketName + ".s3.amazonaws.com/" + s3Filename;

        } catch (AmazonServiceException | IOException e) {
            throw new OurException("Image upload failed: " + e.getMessage());
        }

        return s3LocationImage;
    }
}
