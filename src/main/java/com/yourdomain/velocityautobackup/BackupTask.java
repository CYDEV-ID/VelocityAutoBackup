package com.yourdomain.velocityautobackup;

import org.slf4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupTask implements Runnable {

    private final Logger logger;
    private final Path dataDirectory;
    private final SimpleConfig config;
    private final Path serverRoot;

    public BackupTask(Logger logger, Path dataDirectory, SimpleConfig config) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.config = config;
        this.serverRoot = Paths.get("").toAbsolutePath();
    }

    @Override
    public void run() {
        logger.info("Starting server backup process...");

        String timestamp = new SimpleDateFormat("yyyy-M-d--HH-mm").format(new Date());
        String serverName = config.getString("server-name", "server");
        String fileNameFormat = config.getString("backup-file-name", "Backup-{server_name}-{timestamp}.zip");

        String zipFileName = fileNameFormat
                .replace("{timestamp}", timestamp)
                .replace("{server_name}", serverName);

        if (!zipFileName.toLowerCase().endsWith(".zip")) {
            zipFileName += ".zip";
        }

        Path zipFilePath = dataDirectory.resolve(zipFileName);

        try {
            logger.info("Compressing server files to {}...", zipFileName);
            zipDirectory(serverRoot, zipFilePath);
            logger.info("Compression finished.");

            logger.info("Uploading to Google Drive...");
            GoogleDriveUploader uploader = new GoogleDriveUploader(dataDirectory, config.getString("credentials-file-name", "credentials.json"), logger);
            String folderId = config.getString("google-drive-folder-id");

            if (folderId == null || folderId.isEmpty() || folderId.equals("PASTE_YOUR_FOLDER_ID_HERE")) {
                logger.error("Google Drive Folder ID is not set in config.yml! Upload cancelled.");
                return;
            }

            uploader.uploadFile(zipFilePath.toFile(), folderId);
            logger.info("Backup successfully uploaded to Google Drive.");

        } catch (Exception e) {
            logger.error("An error occurred during the backup process:", e);
        } finally {
            try {
                Files.deleteIfExists(zipFilePath);
                logger.info("Cleaned up local backup file: {}", zipFileName);
            } catch (IOException e) {
                logger.error("Failed to delete local backup file {}.", zipFileName, e);
            }
        }
    }

    private void zipDirectory(Path sourceDir, Path zipFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    Path absoluteDir = dir.toAbsolutePath().normalize();
                    Path absoluteDataDir = dataDirectory.toAbsolutePath().normalize();

                    if (absoluteDir.equals(absoluteDataDir)) {
                        logger.warn("Successfully detected and skipped backup directory: {}", dir);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = sourceDir.relativize(file).toString();
                    String zipEntryName = relativePath.replace(File.separator, "/");
                    zos.putNextEntry(new ZipEntry(zipEntryName));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}