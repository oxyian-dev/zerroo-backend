package com.hionstudios.zerroo.flow;

import java.util.UUID;

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
        return upload(file, filename, Folder.WALLET_REQUEST, WEBP);
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
        return response == null ? null : response.getString("resource_id");
    }

    public static MapResponse delete(String id) {
        return WorkDrive.delete(id);
    }
}