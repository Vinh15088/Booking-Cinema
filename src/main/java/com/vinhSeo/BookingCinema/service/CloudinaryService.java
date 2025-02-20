package com.vinhSeo.BookingCinema.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j(topic = "CLOUDINARY_SERVICE")
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Async
    public CompletableFuture<Map<String, String>> uploadFileAsync(MultipartFile file, String folderName) {
        try {
            String fileName = file.getOriginalFilename();
            String publicId = fileName.substring(0, fileName.lastIndexOf("."));
            log.info("Uploading file with publicId: {}", publicId);
            Map<?, ?> uploadResult =  cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "public_id", publicId
                    ));

            return CompletableFuture.completedFuture(extractUrlAndSecureUrl(uploadResult));
        } catch (IOException e) {
            log.error("Uploading file failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Map<String, String>> deleteFile(String publicId) throws IOException {
        try {
            Map<String, String> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Deleting file failed", e);
            return CompletableFuture.failedFuture(e);
        }

    }

    private Map<String, String> extractUrlAndSecureUrl(Map<?, ?> uploadResult) {
        Map<String, String> result = new HashMap<>();

        result.put("url", uploadResult.get("url").toString());
        result.put("secure_url", uploadResult.get("secure_url").toString());

        return result;
    }
}
