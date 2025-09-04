package com.realestate.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.exception.UnsupportedMediaTypeException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws FileUploadException, UnsupportedMediaTypeException {
        // 1. Resolve filename and content type
        String original = Optional.ofNullable(file.getOriginalFilename())
            .orElseThrow(() -> new UnsupportedMediaTypeException("Filename is missing"))
            .toLowerCase();
        String contentType = Optional.ofNullable(file.getContentType())
            .orElseThrow(() -> new UnsupportedMediaTypeException("Content-Type is unknown"));

        // 2. Decide folder by extension
        String ext = getFileExtension(original);
        String folder = switch (ext) {
            case "jpg","jpeg","png","gif" -> "images";
            // case "mp4","mov"              -> "videos";
            // case "pdf","doc","docx","txt" -> "documents";
            default -> throw new UnsupportedMediaTypeException("Unsupported file type: " + contentType);
        };

        // 3. Build object key
        String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), original);

        // 4. Prepare S3 Put request
        PutObjectRequest req = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build();

        // 5. Perform upload
        try {
            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new FileUploadException("File upload to Cloudflare R2 failed", e);
        }

        return key;
    }

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length()-1) {
            throw new IllegalArgumentException("Invalid file extension in filename: " + filename);
        }
        return filename.substring(idx+1);
    }

    public Resource downloadFile(String key) throws Exception {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return new ByteArrayResource(objectBytes.asByteArray());
        } catch (S3Exception e) {
            throw new Exception("File not found: " + key, e);
        }
    }

    public String getContentType(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            String contentType = headObjectResponse.contentType();
            
            // Fallback to guessing from file extension if content type is not available
            if (contentType == null || contentType.isEmpty()) {
                return guessContentTypeFromKey(key);
            }
            
            return contentType;
        } catch (S3Exception e) {
            // Fallback to guessing from file extension
            return guessContentTypeFromKey(key);
        }
    }

    private String guessContentTypeFromKey(String key) {
        String extension = getFileExtension(key.toLowerCase());
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "bmp" -> "image/bmp";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }
}
