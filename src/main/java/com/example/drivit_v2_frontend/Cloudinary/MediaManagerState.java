package com.example.drivit_v2_frontend.Cloudinary;

public class MediaManagerState {
    private static boolean isInitialized = false;
    public static boolean isInitialized() {
        return isInitialized;
    }
    public static void initialize() {
        isInitialized = true;
    }
}
