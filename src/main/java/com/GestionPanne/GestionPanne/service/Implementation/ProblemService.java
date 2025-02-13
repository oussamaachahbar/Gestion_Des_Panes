package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.Problem;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.ProblemRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.service.Interface.IProblemService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class ProblemService implements IProblemService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Override
    public Response<ProblemDTO> reportProblem(Long userId, Long postId, Problem problemRequest) {
        Response<ProblemDTO> response = new Response<>();
        try {
            // Vérifier les entrées
            if (userId == null || postId == null || problemRequest == null) {
                throw new IllegalArgumentException("L'utilisateur, le poste et le rapport de problème ne peuvent pas être nulls.");
            }

            // Récupérer l'utilisateur et le post
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("Utilisateur non trouvé avec l'ID : " + userId));
            Post post = postRepository.findById(Math.toIntExact(postId))
                    .orElseThrow(() -> new OurException("Poste non trouvé avec l'ID : " + postId));

            // Associer l'utilisateur et le post au problème
            problemRequest.setUser(user);
            problemRequest.setPost(post);

            Problem savedProblem = problemRepository.save(problemRequest);

            response.setStatusCode(201); // 201 Created
            response.setMessage("Problème signalé avec succès.");
            response.setData(Utils.mapProblemEntityToProblemDTO(savedProblem));
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage("Entrée invalide : " + e.getMessage());
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
    public Response<List<ProblemDTO>> getAllProblems() {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            // Récupérer tous les problèmes
            List<Problem> problems = problemRepository.findAll();

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

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(problemRequest.getFailureDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            existingProblem.setFailureDate(calendar.getTime());

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

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Problèmes pour l'utilisateur récupérés avec succès.");
            response.setData(Utils.mapProblemListEntityToProblemListDTO(problems));
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProblemDTO> validateProblem(Long id) {
        return null;
    }

    @Override
    public Response<ProblemDTO> assignProblemToTeam(Long id, Long teamId) {
        return null;
    }
}