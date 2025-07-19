package com.example.demo.Service;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeToExtensionMapper {

    private static final Map<String, String> MIME_TO_EXTENSION = new HashMap<>();

    static {
        // Images
        MIME_TO_EXTENSION.put("image/jpeg", "jpg");
        MIME_TO_EXTENSION.put("image/png", "png");
        MIME_TO_EXTENSION.put("image/gif", "gif");
        MIME_TO_EXTENSION.put("image/webp", "webp");
        MIME_TO_EXTENSION.put("image/tiff", "tiff");
        MIME_TO_EXTENSION.put("image/svg+xml", "svg");
        MIME_TO_EXTENSION.put("image/bmp", "bmp");

        // Documents
        MIME_TO_EXTENSION.put("application/pdf", "pdf");
        MIME_TO_EXTENSION.put("text/plain", "txt");
        MIME_TO_EXTENSION.put("text/csv", "csv");
        MIME_TO_EXTENSION.put("text/html", "html");
        MIME_TO_EXTENSION.put("application/rtf", "rtf");
        MIME_TO_EXTENSION.put("application/msword", "doc");
        MIME_TO_EXTENSION.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");

        // Spreadsheets
        MIME_TO_EXTENSION.put("application/vnd.ms-excel", "xls");
        MIME_TO_EXTENSION.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        MIME_TO_EXTENSION.put("application/vnd.oasis.opendocument.spreadsheet", "ods");

        // Presentations
        MIME_TO_EXTENSION.put("application/vnd.ms-powerpoint", "ppt");
        MIME_TO_EXTENSION.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");

        // Archives
        MIME_TO_EXTENSION.put("application/zip", "zip");
        MIME_TO_EXTENSION.put("application/x-7z-compressed", "7z");
        MIME_TO_EXTENSION.put("application/x-rar-compressed", "rar");
        MIME_TO_EXTENSION.put("application/gzip", "gz");
        MIME_TO_EXTENSION.put("application/x-tar", "tar");

        // Audio
        MIME_TO_EXTENSION.put("audio/mpeg", "mp3");
        MIME_TO_EXTENSION.put("audio/wav", "wav");
        MIME_TO_EXTENSION.put("audio/ogg", "ogg");
        MIME_TO_EXTENSION.put("audio/aac", "aac");

        // Video
        MIME_TO_EXTENSION.put("video/mp4", "mp4");
        MIME_TO_EXTENSION.put("video/x-msvideo", "avi");
        MIME_TO_EXTENSION.put("video/x-matroska", "mkv");
        MIME_TO_EXTENSION.put("video/quicktime", "mov");
        MIME_TO_EXTENSION.put("video/x-ms-wmv", "wmv");

        MIME_TO_EXTENSION.put("application/xml", "xml");
        MIME_TO_EXTENSION.put("text/xml", "xml");

        MIME_TO_EXTENSION.put("application/octet-stream", "bin");
    }

    public static String getExtensionFromMimeType(String mimeType) {
        return MIME_TO_EXTENSION.getOrDefault(mimeType.toLowerCase(), "bin");
    }
}