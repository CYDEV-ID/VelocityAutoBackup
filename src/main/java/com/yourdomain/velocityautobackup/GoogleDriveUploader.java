package com.yourdomain.velocityautobackup;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveUploader {

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private final Path dataDirectory;
    private final String credentialsFileName;
    private final Logger logger;
    private final FileDataStoreFactory tokenStore;
    private final GoogleAuthorizationCodeFlow flow;
    private final String REDIRECT_URI = "http://localhost:8888";

    public GoogleDriveUploader(Path dataDirectory, String credentialsFileName, Logger logger) throws IOException, GeneralSecurityException {
        this.dataDirectory = dataDirectory;
        this.credentialsFileName = credentialsFileName;
        this.logger = logger;

        java.io.File tokensDirectory = new java.io.File(dataDirectory.toFile(), "tokens");
        this.tokenStore = new FileDataStoreFactory(tokensDirectory);

        Path credentialsPath = dataDirectory.resolve(credentialsFileName);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(credentialsPath.toFile())));

        this.flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(tokenStore)
                .setAccessType("offline")
                .build();
    }

    public boolean isAuthorized() throws IOException {
        return flow.loadCredential("user") != null;
    }

    public void generateAuthorizationUrl() {
        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        logger.warn("========================= GOOGLE AUTHORIZATION REQUIRED =========================");
        logger.warn("Open this URL in your browser: {}", url);
        logger.warn("After granting permission, your browser will show a 'localhost' error page. THIS IS NORMAL.");
        logger.warn("Copy the CODE from your browser's address bar (the part between 'code=' and '&scope=').");
        logger.warn("Paste that code into the 'authcode.txt' file in the plugin folder.");
        logger.warn("=================================================================================");
    }

    public boolean authorizeWithCode(String code) {
        try {
            GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
            flow.createAndStoreCredential(response, "user");
            return true;
        } catch (IOException e) {
            logger.error("Failed to exchange authorization code for a token. Error: {}", e.getMessage());
            return false;
        }
    }

    private Drive getDriveService() throws IOException {
        Credential credential = flow.loadCredential("user");
        return new Drive.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
                .setApplicationName("Velocity Auto Backup")
                .build();
    }

    public void uploadFile(java.io.File fileToUpload, String parentFolderId) throws IOException {
        Drive service = getDriveService();

        File fileMetadata = new File();
        fileMetadata.setName(fileToUpload.getName());
        fileMetadata.setParents(Collections.singletonList(parentFolderId));

        FileContent mediaContent = new FileContent("application/zip", fileToUpload);

        service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    }
}