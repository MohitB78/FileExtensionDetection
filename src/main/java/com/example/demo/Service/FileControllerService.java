package com.example.demo.Service;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileControllerService {

    private static final Map<String, String> FILE_SIGNATURES = new HashMap<>();

    static {
        // Images
        FILE_SIGNATURES.put("FFD8FF", "image/jpeg");
        FILE_SIGNATURES.put("89504E47", "image/png");
        FILE_SIGNATURES.put("47494638", "image/gif");
        FILE_SIGNATURES.put("52494646", "image/webp");
        FILE_SIGNATURES.put("49492A00", "image/tiff");
        FILE_SIGNATURES.put("4D4D002A", "image/tiff");

        // Documents (Office)
        FILE_SIGNATURES.put("25504446", "application/pdf");
        FILE_SIGNATURES.put("504B0304", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // docx/xlsx/pptx zip-based
        FILE_SIGNATURES.put("504B0506", "application/zip");
        FILE_SIGNATURES.put("504B0708", "application/zip");
        FILE_SIGNATURES.put("D0CF11E0", "application/x-ole-storage"); // old Office: doc, xls, ppt
        FILE_SIGNATURES.put("377ABCAF", "application/x-7z-compressed");
        FILE_SIGNATURES.put("1F8B08", "application/gzip");

        // Text files
        FILE_SIGNATURES.put("EFBBBF", "text/plain");
        FILE_SIGNATURES.put("FEFF", "text/plain");
        FILE_SIGNATURES.put("FFFE", "text/plain");

        // Audio/Video
        FILE_SIGNATURES.put("66747970", "video/mp4");
        FILE_SIGNATURES.put("00000020", "video/mp4");
        FILE_SIGNATURES.put("494433", "audio/mpeg");
    }

    public String detectMimeType(byte[] fileBytes) throws IOException {
        byte[] header = java.util.Arrays.copyOfRange(fileBytes, 0, Math.min(fileBytes.length, 8));
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : header) hexBuilder.append(String.format("%02X", b));
        String hexHeader = hexBuilder.toString();

        for (Map.Entry<String, String> entry : FILE_SIGNATURES.entrySet()) {
            if (hexHeader.startsWith(entry.getKey())) {
                String mime = entry.getValue();

                // Handle OpenXML formats (docx, xlsx, pptx)
                if (mime.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        || mime.equals("application/zip")) {
                    try (java.util.zip.ZipInputStream zis =
                                 new java.util.zip.ZipInputStream(new ByteArrayInputStream(fileBytes))) {
                        java.util.zip.ZipEntry entryZip;
                        while ((entryZip = zis.getNextEntry()) != null) {
                            String name = entryZip.getName();
                            if (name.startsWith("word/"))
                                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; // DOCX
                            if (name.startsWith("xl/"))
                                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; // XLSX
                            if (name.startsWith("ppt/"))
                                return "application/vnd.openxmlformats-officedocument.presentationml.presentation"; // PPTX
                        }
                    }
                    return "application/zip";
                }

                // Handle old binary Office formats (doc, xls, ppt)
                if (mime.equals("application/x-ole-storage")) {
                    try (POIFSFileSystem poifs = new POIFSFileSystem(new ByteArrayInputStream(fileBytes))) {
                        if (poifs.getRoot().hasEntry("WordDocument")) return "application/msword";          // DOC
                        if (poifs.getRoot().hasEntry("Workbook") || poifs.getRoot().hasEntry("Book"))
                            return "application/vnd.ms-excel";                                             // XLS
                        if (poifs.getRoot().hasEntry("PowerPoint Document"))
                            return "application/vnd.ms-powerpoint";                                       // PPT
                    } catch (Exception ignored) { }
                    return "application/x-ole-storage";
                }

                return mime;
            }
        }

        // XML fallback
        String fileStart = new String(fileBytes, 0, Math.min(fileBytes.length, 100),
                java.nio.charset.StandardCharsets.UTF_8).replace("\uFEFF", "").trim();
        if (fileStart.startsWith("<?xml") || fileStart.contains("<root"))
            return "application/xml";

        return "application/octet-stream";
    }

    public String findExtensionOfFile(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();

        String mimeType = detectMimeType(fileBytes);
        String extension = MimeTypeToExtensionMapper.getExtensionFromMimeType(mimeType);

        System.out.println("Detected MIME Type: " + mimeType);
        System.out.println("Mapped Extension: " + extension);
        System.out.println("Original Filename: " + file.getOriginalFilename());

        return extension;
    }
}
