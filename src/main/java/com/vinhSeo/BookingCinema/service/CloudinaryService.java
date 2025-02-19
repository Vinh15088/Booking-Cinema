package com.vinhSeo.BookingCinema.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j(topic = "CLOUDINARY_SERVICE")
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, String> uploadFile(MultipartFile file, String folderName) throws IOException {
        String fileName = file.getOriginalFilename();
        String publicId = fileName.substring(0, fileName.lastIndexOf("."));
        log.info("Uploading file with publicId: {}", publicId);
        Map<?, ?> uploadResult =  cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName,
                        "public_id", publicId
                ));

        return extractUrlAndSecureUrl(uploadResult);
    }

    public Map<String, String> updateFile(MultipartFile file, String folderName, String publicId) throws IOException {
        if(file != null && !file.isEmpty()) {
            deleteFile(publicId);

            return uploadFile(file, folderName);
        }

        return null;
    }

    public Map deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public Map<String, String> uploadVideo(MultipartFile file, String folderName) throws IOException {
        String fileName = file.getOriginalFilename();
        String publicId = fileName.substring(0, fileName.lastIndexOf("."));
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "video",
                        "folder", folderName,
                        "public_id", publicId
                ));

        return extractUrlAndSecureUrl(uploadResult);
    }

    public Map<String, String> updateVideo(MultipartFile file, String folderName, String publicId) throws IOException {
        if(file != null && !file.isEmpty()) {
            deleteFile(publicId);

            return uploadVideo(file, folderName);
        }

        return null;
    }

    public Map deleteVideo(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId,
                ObjectUtils.asMap(
                    "resource_type", "video"
        ));
    }

    private Map<String, String> extractUrlAndSecureUrl(Map<?, ?> uploadResult) {
        Map<String, String> result = new HashMap<>();

        result.put("url", uploadResult.get("url").toString());
        result.put("secure_url", uploadResult.get("secure_url").toString());

        return result;
    }
}
