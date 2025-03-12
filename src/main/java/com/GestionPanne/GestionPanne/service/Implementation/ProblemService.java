package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.*;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.ProblemRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
//import com.GestionPanne.GestionPanne.service.AwsS3Service;
import com.GestionPanne.GestionPanne.service.Interface.IProblemService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class ProblemService implements IProblemService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private JavaMailSender mailSender;




    @Override
    public Response<ProblemDTO> reportProblem(Long userId, Long postId, Problem problemRequest) {
        Response<ProblemDTO> response = new Response<>();
        try {
            if (userId == null || postId == null || problemRequest == null) {
                throw new OurException("L'utilisateur, le poste et le rapport de problème ne peuvent pas être nulls.");
            }

            // Vérification de l'utilisateur
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("Utilisateur non trouvé avec l'ID : " + userId));

            // Vérification des permissions
            if (!"ADMIN".equals(user.getRole()) && !user.isSupervisor()) {
                throw new OurException("Seuls les superviseurs ou admins peuvent signaler un problème.");
            }

            // Vérification du post
            Post post = postRepository.findById(postId);
            if (!post.getUsers().contains(user)) {
                throw new OurException("L'utilisateur ne fait pas partie de ce poste.");
            }



            problemRequest.setUser(user);
            problemRequest.setPost(post);
            problemRequest.setPriority(problemRequest.getPriority() != null ? problemRequest.getPriority() : Priority.MEDIUM);
            problemRequest.setEtat(Etat.PENDING);
            problemRequest.setFailureDate(problemRequest.getFailureDate() != null ? problemRequest.getFailureDate() : LocalDateTime.now());

            Problem savedProblem = problemRepository.save(problemRequest);

            //sendProblemReportEmail(savedProblem);***********************************************************

            // Construction de la réponse
            response.setStatusCode(201);
            response.setMessage("Problème signalé avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(savedProblem));

        } catch (OurException e) {
            response.setStatusCode(403);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }








    // Méthode pour sauvegarder l'image et retourner son chemin
    private String saveImage(MultipartFile imageFile) throws IOException {
        // Dossier où les images seront stockées
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Générer un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

        // Sauvegarder l'image sur le serveur
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath);

        // Retourner le chemin relatif de l'image
        return filePath.toString();
    }

    // Méthode pour envoyer un appel téléphonique avec les informations du problème




    private void sendProblemReportEmail(Problem problem) {
        try {
            System.out.println("📩 Envoi de l'email en cours...");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo("oussamaachahbar.bac2019@gmail.com");
            helper.setSubject("🔔 [Alerte] Nouveau Problème Signalé - " + problem.getPost().getName());

            String emailContent = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px;'>"
                    + "<h2 style='color: #d9534f;'>🚨 Nouveau Problème Signalé</h2>"
                    + "<p><strong>Le Supervisor :</strong> " + problem.getUser().getUsername() + "</p>"
                    + "<p><strong>Poste Concerné :</strong> " + problem.getPost().getName() + "</p>"
                    + "<p><strong>Date de l'incident :</strong> " + problem.getFailureDate() + "</p>"
                    + "<p><strong>Détails :</strong> " + problem.getFailureDetails() + "</p>"
                    + "<p><strong>Priorité :</strong> <span style='color: " + getPriorityColor(problem.getPriority()) + "; font-weight: bold;'>"
                    + problem.getPriority().name() + "</span></p>"

                    + "<p><strong>État Actuel :</strong> <span style='color: "+ getEtatColor(problem.getEtat()) +"; font-weight: bold;'>" +
                     problem.getEtat().name() + "</span></p>"
                    + "<br/>"
                    + "<p style='color: #555;'>Merci de prendre les mesures nécessaires.</p>"
                    + "<hr style='border: none; height: 1px; background-color: #ddd;'/>"
                    + "<p style='font-size: 12px; color: #777;'>Ce message est généré automatiquement, merci de ne pas répondre.</p>"
                    + "</div>";

            helper.setText(emailContent, true);

            mailSender.send(mimeMessage);
            System.out.println("✅ Email envoyé avec succès !");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
    private void sendProblemValidateEmail(Problem problem) {
        try {
            System.out.println("📩 Envoi de l'email en cours...");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo("oussamaachahbar.bac2019@gmail.com");
            helper.setSubject("🔔 [Alerte] Problème Finished - " + problem.getPost().getName());

            String emailContent = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px;'>"
                    + "<h2 style='color: #d9534f;'>🚨 Nouveau Problème Signalé</h2>"
                    + "<p><strong>Le Supervisor qui signale le probleme est  :</strong> " + problem.getUser().getUsername() + "</p>"
                    + "<p><strong>La Poste Fixé c'est :</strong> " + problem.getPost().getName() + "</p>"
                    + "<p><strong>Date de la solution de cette poste est  :</strong> " + problem.getSolutionDate() + "</p>"
                    + "<p><strong>Priorité :</strong> <span style='color: " + getPriorityColor(problem.getPriority()) + "; font-weight: bold;'>"
                    + problem.getPriority().name() + "</span></p>"

                    + "<p><strong>État Actuel :</strong> <span style='color: "+ getEtatColor(problem.getEtat()) +"; font-weight: bold;'>" +
                    problem.getEtat().name() + "</span></p>"
                    + "<br/>"
                    + "<p style='color: #555;'>Merci de prendre les mesures nécessaires.</p>"
                    + "<hr style='border: none; height: 1px; background-color: #ddd;'/>"
                    + "<p style='font-size: 12px; color: #777;'>Ce message est généré automatiquement, merci de ne pas répondre.</p>"
                    + "</div>";

            helper.setText(emailContent, true);

            mailSender.send(mimeMessage);
            System.out.println("✅ Email envoyé avec succès !");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    private String getEtatColor(Etat etat) {
        switch (etat) {
            case PENDING: return "#d9534f"; // Rouge
            case EN_PROGRESS: return "#f0ad4e"; // Orange
            case FINISHED: return "#5bc0de"; // Bleu
            default: return "#333"; // Noir par défaut
        }
    }

    private String getPriorityColor(Priority priority) {
        switch (priority) {
            case CRITICAL: return "#d9534f"; // Rouge
            case HIGH: return "#f0ad4e"; // Orange
            case MEDIUM: return "#5bc0de"; // Bleu
            case LOW: return "#5cb85c"; // Vert
            default: return "#333"; // Noir par défaut
        }
    }



    @Override
    public Response<List<ProblemDTO>> getAllProblems() {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            List<Problem> problems = problemRepository.findAllOrderedByPriority();

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Tous les problèmes récupérés avec succès.");
            response.setData(Utils.mapProblemListEntityToProblemListDTO(problems));
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Erreur lors de la récupération des problèmes : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProblemDTO> getProblemById(Long problemId) {
        Response<ProblemDTO> response = new Response<>();
        try {
            // Récupérer le problème par son ID
            Problem problem = problemRepository.findById(Math.toIntExact(problemId))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + problemId + " introuvable."));

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Problème récupéré avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(problem));
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProblemDTO> updateProblem(Long problemId, Problem problemRequest) {
        Response<ProblemDTO> response = new Response<>();
        try {
            // Récupérer le problème existant
            Problem existingProblem = problemRepository.findById(Math.toIntExact(problemId))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + problemId + " introuvable."));

            existingProblem.setTitle(problemRequest.getTitle());
            existingProblem.setFailureDetails(problemRequest.getFailureDetails());
            existingProblem.setUser(problemRequest.getUser());
            existingProblem.setPost(problemRequest.getPost());
            existingProblem.setPriority(problemRequest.getPriority());
            existingProblem.setEtat(problemRequest.getEtat());
            existingProblem.setFailureDate(LocalDateTime.now());





            Problem updatedProblem = problemRepository.save(existingProblem);

            response.setStatusCode(200); // 200 OK
            response.setMessage("Problème mis à jour avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(updatedProblem));
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Void> deleteProblem(Long problemId) {
        Response<Void> response = new Response<>();
        try {

            Problem problem = problemRepository.findById(Math.toIntExact(problemId))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + problemId + " introuvable."));


            problemRepository.delete(problem);


            response.setStatusCode(200); // 200 OK
            response.setMessage("Problème supprimé avec succès.");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<ProblemDTO>> getProblemsByPost(Long postId) {
        Response<List<ProblemDTO>> response = new Response<>();
        try {

            List<Problem> problems = problemRepository.findByPostId(postId);


            response.setStatusCode(200); // 200 OK
            response.setMessage("Problèmes pour le poste récupérés avec succès.");
            response.setData(Utils.mapProblemListEntityToProblemListDTO(problems));
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<ProblemDTO>> getProblemsByUser(Long userId) {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            // Récupérer les problèmes par utilisateur
            List<Problem> problems = problemRepository.findByUserId(userId);

            // Vérifier si l'utilisateur existe
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("Utilisateur avec l'ID " + userId + " introuvable."));

            // Vérification si l'utilisateur est un superviseur
            boolean isSupervisor = user.getIsSupervisor();

            // Mapper les problèmes à partir de l'entité Problem
            List<ProblemDTO> problemDTOs = Utils.mapProblemListEntityToProblemListDTO(problems);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setSupervisor(isSupervisor);
            response.setMessage("Problèmes pour l'utilisateur récupérés avec succès.");
            response.setData(problemDTOs);
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<ProblemDTO> validateProblem(Long id) {
        Response<ProblemDTO> response = new Response<>();
        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + id + " introuvable."));

            if (problem.getSolutionDate() == null) {
                // Utilisation de LocalDateTime pour inclure la date et l'heure actuelles
                LocalDateTime currentDateTime = LocalDateTime.now();
                problem.setSolutionDate(LocalDateTime.now()); // Convertir LocalDateTime en Timestamp
            }

            problem.setEtat(Etat.FINISHED);
            Problem updatedProblem = problemRepository.save(problem);
            sendProblemValidateEmail(updatedProblem);

            response.setStatusCode(200);
            response.setMessage("Problème validé avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(updatedProblem));
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<ProblemDTO> assignProblemToTeam(Long id) {
        Response<ProblemDTO> response = new Response<>();
        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + id + " introuvable."));

            problem.setEtat(Etat.EN_PROGRESS);
            Problem updatedProblem = problemRepository.save(problem);
            response.setStatusCode(200);
            response.setMessage("Problème assigné avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(updatedProblem));
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

}