package com.GestionPanne.GestionPanne.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;

@Service
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "Panne Management";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";  // Vérifier l'orthographe
    private static final String FOLDER_ID = "1bFhuinb6PSkHw36U11znySi_79OqwPKS";

    private Drive getDriveService() throws Exception {
        try (InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH)) {
            GoogleCredential credential = GoogleCredential.fromStream(in)
                    .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }

    public String uploadFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        Drive driveService = getDriveService();

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(FOLDER_ID));

        File tempFile = convertMultiPartToFile(file);
        FileContent mediaContent = new FileContent(file.getContentType(), tempFile);

        try {
            com.google.api.services.drive.model.File uploadedFile = driveService.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            return "https://drive.google.com/file/d/" + uploadedFile.getId() + "/view";
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload sur Google Drive", e);
        } finally {
            // Supprimer le fichier temporaire après l'upload
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
