package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.AwsS3Service;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.*;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.ProblemRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
//import com.GestionPanne.GestionPanne.service.AwsS3Service;
import com.GestionPanne.GestionPanne.service.GoogleDriveService;
import com.GestionPanne.GestionPanne.service.Interface.IProblemService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private AwsS3Service awsS3Service;



    @Autowired
    private GoogleDriveService googleDriveService;




    public Response<ProblemDTO> reportProblem(Long userId, Long postId, Problem problemDTO, MultipartFile imageFile) {
        Response<ProblemDTO> response = new Response<>();

        Post post = postRepository.findById(postId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OurException("Utilisateur introuvable"));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = awsS3Service.saveImageToS3(imageFile);
        }

        Problem problem = new Problem();
        problem.setTitle(problemDTO.getTitle());
        problem.setPriority(problemDTO.getPriority());
        problem.setFailureDetails(problemDTO.getFailureDetails());
        problem.setPost(post);
        problem.setEtat(Etat.PENDING);
        problem.setFailureDate(problemDTO.getFailureDate() != null ? problemDTO.getFailureDate() : LocalDateTime.now());
        problem.setUser(user);
        problem.setImageUrl(imageUrl);
        sendProblemReportEmail(problem);

        problem = problemRepository.save(problem);

        response.setData(Utils.mapProblemEntityToProblemDTO(problem));
        response.setMessage("Problème signalé avec succès");
        response.setStatusCode(HttpStatus.CREATED.value());

        return response;
    }
    private String saveImage(MultipartFile imageFile) throws IOException {
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath);
        return filePath.toString();
    }
    private void sendProblemReportEmail(Problem problem) {
        try {
            System.out.println("📩 Envoi de l'email en cours...");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo("tangerfujikuraautomobile@gmail.com");
            helper.setSubject("🔔 [Alerte] Nouveau Problème Signalé - " + problem.getPost().getName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
            String formattedDate = problem.getFailureDate().format(formatter);

            String emailContent = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; background-color: #f4f4f4;'>"
                    + "<div style='max-width: 800px; margin: 0 auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>"
                    + "<h2 style='color: #d9534f; font-size: 28px; text-align: center;'><strong>🚨 Nouveau Problème Signalé</strong></h2>"
                    + "<hr style='border: none; height: 1px; background-color: #ddd; margin-top: 20px;'>"

                    + "<p style='font-size: 18px;'><strong>Le Supervisor :</strong> " + problem.getUser().getUsername() + "</p>"
                    + "<p style='font-size: 18px;'><strong>Poste Concerné :</strong> " + problem.getPost().getName() + "</p>"
                    + "<p style='font-size: 18px;'><strong>Date de l'incident :</strong> " + formattedDate + "</p>"
                    + "<p style='font-size: 18px;'><strong>Détails :</strong> " + problem.getFailureDetails() + "</p>"
                    + "<p style='font-size: 18px;'><strong>Priorité :</strong> <span style='color: #d9534f; font-weight: bold;'>"
                    + problem.getPriority().name() + "</span></p>"
                    + "<p style='font-size: 18px;'><strong>État Actuel :</strong> <span style='color: #f0ad4e; font-weight: bold;'>"
                    + problem.getEtat().name() + "</span></p>"

                    + "<p style='text-align: center;'><img src='" + problem.getImageUrl() + "' alt='Image du problème' style='max-width: 100%; height: auto; border-radius: 8px;'></p>"

                    + "<br/>"
                    + "<p style='font-size: 18px; color: #555;'>Merci de prendre les mesures nécessaires.</p>"
                    + "<hr style='border: none; height: 1px; background-color: #ddd; margin-top: 20px;'>"
                    + "<p style='font-size: 14px; text-align: center; color: #777;'>Ce message est généré automatiquement, merci de ne pas répondre.</p>"
                    + "</div>"
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

            helper.setTo("tangerfujikuraautomobile@gmail.com");
            helper.setSubject("🔔 [Alerte] Problème Finished - " + problem.getPost().getName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
            String formattedDate = problem.getSolutionDate().format(formatter);
            String emailContent = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; background-color: #f4f4f4;'>"
                    + "<div style='max-width: 800px; margin: 0 auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>"
                    + "<h2 style='color: #d9534f; font-size: 28px; text-align: center;'><strong>🚨 Nouveau Problème Validé</strong></h2>"
                    + "<hr style='border: none; height: 1px; background-color: #ddd; margin-top: 20px;'>"
                    + "<p style='font-size: 18px;'><strong>Le Supervisor :</strong> " + problem.getUser().getUsername() + "</p>"
                    + "<p style='font-size: 18px;'><strong>Poste Concerné :</strong> " + problem.getPost().getName() + "</p>"
                    + "<p style='font-size: 18px;'><strong>Date de l'incident :</strong> " + formattedDate + "</p>"
                    + "<p style='font-size: 18px;'><strong>Détails :</strong> " + problem.getFailureDetails() + "</p>"
                    + "<p style='font-size: 18px;'><strong>Priorité :</strong> <span style='color: #d9534f; font-weight: bold;'>"
                    + problem.getPriority().name() + "</span></p>"
                    + "<p style='font-size: 18px;'><strong>État Actuel :</strong> <span style='color: #f0ad4e; font-weight: bold;'>"
                    + problem.getEtat().name() + "</span></p>"
                    + "<br/>"
                    + "<p style='font-size: 18px; color: #555;'>Merci de prendre les mesures nécessaires.</p>"

                    + "<hr style='border: none; height: 1px; background-color: #ddd; margin-top: 20px;'>"
                    + "<p style='font-size: 14px; text-align: center; color: #777;'>Ce message est généré automatiquement, merci de ne pas répondre.</p>"
                    + "</div>"
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
            case PENDING: return "#d9534f";
            case EN_PROGRESS: return "#f0ad4e";
            case FINISHED: return "#5bc0de";
            default: return "#333";
        }
    }

    private String getPriorityColor(Priority priority) {
        switch (priority) {
            case CRITICAL: return "#d9534f";
            case HIGH: return "#f0ad4e";
            case MEDIUM: return "#5bc0de";
            case LOW: return "#5cb85c";
            default: return "#333";
        }
    }



    @Override
    public Response<List<ProblemDTO>> getAllProblems() {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            List<Problem> problems = problemRepository.findAllOrderedByPriority();

            response.setStatusCode(200);
            response.setMessage("Tous les problèmes récupérés avec succès.");
            response.setData(Utils.mapProblemListEntityToProblemListDTO(problems));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur lors de la récupération des problèmes : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProblemDTO> getProblemById(Long problemId) {
        Response<ProblemDTO> response = new Response<>();
        try {
            Problem problem = problemRepository.findById(Math.toIntExact(problemId))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + problemId + " introuvable."));
            response.setStatusCode(200);
            response.setMessage("Problème récupéré avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(problem));
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
    public Response<ProblemDTO> updateProblem(Long problemId, Problem problemRequest) {
        Response<ProblemDTO> response = new Response<>();
        try {
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

            response.setStatusCode(200);
            response.setMessage("Problème mis à jour avec succès.");
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
    public Response<Void> deleteProblem(Long problemId) {
        Response<Void> response = new Response<>();
        try {

            Problem problem = problemRepository.findById(Math.toIntExact(problemId))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + problemId + " introuvable."));


            problemRepository.delete(problem);


            response.setStatusCode(200);
            response.setMessage("Problème supprimé avec succès.");
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
    public Response<List<ProblemDTO>> getProblemsByPost(Long postId) {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            List<Problem> problems = problemRepository.findByPostId(postId);
            response.setStatusCode(200);
            response.setMessage("Problèmes pour le poste récupérés avec succès.");
            response.setData(Utils.mapProblemListEntityToProblemListDTO(problems));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<ProblemDTO>> getProblemsByUser(Long userId) {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            List<Problem> problems = problemRepository.findByUserId(userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("Utilisateur avec l'ID " + userId + " introuvable."));

            boolean isSupervisor = user.getIsSupervisor();
            List<ProblemDTO> problemDTOs = Utils.mapProblemListEntityToProblemListDTO(problems);

            response.setStatusCode(200);
            response.setSupervisor(isSupervisor);

            response.setMessage("Problèmes pour l'utilisateur récupérés avec succès.");
            response.setData(problemDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<ProblemDTO> validateProblem(Long id) {
        Response<ProblemDTO> response = new Response<>();
        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("Problème introuvable"));

            problem.setEtat(Etat.FINISHED);
            problem.setSolutionDate(LocalDateTime.now());


            Problem updatedProblem = problemRepository.save(problem);
            sendProblemValidateEmail(problem);
            response.setStatusCode(200);
            response.setData(Utils.mapProblemEntityToProblemDTO(updatedProblem));

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @Override
    public Response<ProblemDTO> assignProblemToTeam(Long id, Long technicienId) {
        Response<ProblemDTO> response = new Response<>();
        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + id + " introuvable."));

            User technicien = userRepository.findById(technicienId)
                    .orElseThrow(() -> new OurException("Technicien avec l'ID " + technicienId + " introuvable."));

            problem.setEtat(Etat.EN_PROGRESS);
            problem.setTechnicien(technicien);
            problem.setAssignmentDate(LocalDateTime.now());

            Problem updatedProblem = problemRepository.save(problem);

            response.setStatusCode(200);
            response.setMessage("Problème assigné au technicien avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(updatedProblem));
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
            e.printStackTrace(); // utile pour débogage
        }
        return response;
    }


    @Override
    public Response<List<ProblemDTO>> getProblemsByTechnicien(Long technicienId) {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            List<Problem> problems = problemRepository.findByTechnicienId(technicienId);

            User technicien = userRepository.findById(technicienId)
                    .orElseThrow(() -> new OurException("Technicien avec l'ID " + technicienId + " introuvable."));

            List<ProblemDTO> problemDTOs = Utils.mapProblemListEntityToProblemListDTO(problems);

            response.setStatusCode(200);
            response.setTechnicien(technicien.isTechnicien());
            response.setMessage("Problèmes pour le technicien récupérés avec succès.");
            response.setData(problemDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }
}