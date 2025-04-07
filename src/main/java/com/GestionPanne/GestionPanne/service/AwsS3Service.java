/*/package com.GestionPanne.GestionPanne.service;

import com.GestionPanne.GestionPanne.exception.OurException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class AwsS3Service {

    // Chemin local pour stocker les images
    private final String localStoragePath = "C:\\Users\\oussa\\Desktop\\pictures\\";

    public String saveImageLocally(MultipartFile photo) {
        String localFilePath = null;

        try {
            // Vérification que le fichier est bien une image
            if (!photo.getContentType().startsWith("image")) {
                throw new OurException("Only image files are allowed.");
            }

            // Vérification de la taille de l'image
            if (photo.getSize() > 5 * 1024 * 1024) {  // Limite à 5 Mo
                throw new OurException("Image size exceeds 5MB.");
            }

            // Génération d'un nom de fichier unique
            String localFileName = UUID.randomUUID().toString() + "-" + photo.getOriginalFilename();
            File localFile = new File(localStoragePath + localFileName);

            // Créer le répertoire si nécessaire
            File directory = new File(localStoragePath);
            if (!directory.exists()) {
                directory.mkdirs();  // Crée le répertoire si il n'existe pas
            }

            // Sauvegarde de l'image localement
            photo.transferTo(localFile);

            // Retourner le chemin local du fichier
            localFilePath = "file://" + localFile.getAbsolutePath();

        } catch (IOException e) {
            throw new OurException("File error: " + e.getMessage());
        } catch (Exception e) {
            throw new OurException("Unable to save image locally: " + e.getMessage());
        }

        return localFilePath;
    }
}
/*/