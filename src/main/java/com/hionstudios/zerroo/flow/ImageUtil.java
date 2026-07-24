package com.hionstudios.zerroo.flow;

import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.MapResponse;
import com.hionstudios.zerroo.oauth.WorkDrive;
import com.hionstudios.zerroo.oauth.WorkDrive.Folder;

public class ImageUtil {
    private static boolean WEBP = false;

    public static String uploadProducts(MultipartFile file, String filename) {
        return upload(file, filename, Folder.PRODUCTS, WEBP);
    }

    public static String uploadKyc(MultipartFile file, String filename) {
        return upload(file, filename, Folder.KYC, WEBP);
    }

    public static String uploadWalletRequest(MultipartFile file, String filename) {
        String safeFilename = safeFilename(file, filename);
        String resourceId = upload(file, safeFilename, Folder.WALLET_REQUEST, WEBP);
        if (resourceId == null || resourceId.isBlank()) {
            resourceId = upload(file, safeFilename, Folder.KYC, WEBP);
        }
        if (resourceId == null || resourceId.isBlank()) {
            resourceId = saveLocally(file, safeFilename, "wallet-requests");
        }
        return resourceId;
    }

    public static String uploadDistributorDp(MultipartFile file, String filename) {
        return upload(file, filename, Folder.DISTRIBUTORS_DP, WEBP);
    }

    public static String upload(MultipartFile file, String filename, WorkDrive.Folder folder) {
        return upload(file, filename, folder, WEBP);
    }

    public static String upload(MultipartFile file, WorkDrive.Folder folder) {
        return upload(file, UUID.randomUUID().toString() + ".webp", folder, WEBP);
    }

    public static String upload(MultipartFile file, String filename, WorkDrive.Folder folder, boolean webp) {
        MapResponse response = WorkDrive.upload(file, filename, folder, webp);
        if (response == null) {
            return null;
        }
        String resourceId = null;
        try {
            resourceId = response.getString("resource_id");
        } catch (Exception ignored) {
        }
        if (resourceId == null || resourceId.isBlank()) {
            try {
                resourceId = response.getString("id");
            } catch (Exception ignored) {
            }
        }
        if (resourceId == null || resourceId.isBlank()) {
            try {
                resourceId = response.getString("file_id");
            } catch (Exception ignored) {
            }
        }
        return resourceId;
    }

    public static String uploadLocal(MultipartFile file, String filename, String folder) {
        String safeFilename = safeFilename(file, filename);
        return saveLocally(file, safeFilename, folder);
    }

    private static String saveLocally(MultipartFile file, String filename, String folder) {
        try {
            Path basePath = Paths.get(System.getProperty("user.dir"), "uploads", folder).toAbsolutePath().normalize();
            Files.createDirectories(basePath);
            String safe = filename.replaceAll("[^a-zA-Z0-9._-]", "-");
            if (safe.isBlank()) {
                safe = UUID.randomUUID().toString();
            }
            Path target = basePath.resolve(safe).normalize();
            if (Files.exists(target)) {
                int idx = safe.lastIndexOf('.');
                String base = idx > 0 ? safe.substring(0, idx) : safe;
                String ext = idx > 0 ? safe.substring(idx) : "";
                target = basePath.resolve(base + "-" + UUID.randomUUID() + ext).normalize();
            }
            Files.copy(file.getInputStream(), target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return "/api/uploads/" + folder + "/" + target.getFileName().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isLocalUpload(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        String normalized = id.trim();
        return normalized.startsWith("/api/uploads/") || normalized.startsWith("/uploads/");
    }

    private static boolean deleteLocal(String id) {
        try {
            String normalized = id.trim();
            if (normalized.startsWith("/api/")) {
                normalized = normalized.substring(5);
            }
            if (normalized.startsWith("/")) {
                normalized = normalized.substring(1);
            }
            Path file = Paths.get(normalized).normalize();
            if (!file.startsWith(Paths.get("uploads").normalize())) {
                return false;
            }
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            return false;
        }
    }

    public static MapResponse delete(String id) {
        if (isLocalUpload(id)) {
            return deleteLocal(id) ? MapResponse.success() : MapResponse.failure();
        }
        return WorkDrive.delete(id);
    }

    private static String safeFilename(MultipartFile file, String fallback) {
        String original = file != null ? file.getOriginalFilename() : null;
        String filename = original != null && !original.isBlank() ? original : fallback;
        filename = filename == null || filename.isBlank() ? UUID.randomUUID().toString() : filename;
        if (!filename.contains(".")) {
            String mime = null;
            try {
                mime = file != null ? file.getContentType() : null;
            } catch (Exception ignored) {
            }
            String ext = mimeExtension(mime);
            if (ext == null) {
                ext = ".jpg";
            }
            filename += ext;
        }
        return filename;
    }

    private static String mimeExtension(String mime) {
        if (mime == null) {
            return null;
        }
        String lower = mime.toLowerCase();
        if (lower.contains("png")) {
            return ".png";
        }
        if (lower.contains("webp")) {
            return ".webp";
        }
        if (lower.contains("gif")) {
            return ".gif";
        }
        if (lower.contains("jpeg") || lower.contains("jpg")) {
            return ".jpg";
        }
        return null;
    }

}
