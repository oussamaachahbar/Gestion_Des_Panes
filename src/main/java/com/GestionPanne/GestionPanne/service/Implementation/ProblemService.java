package com.GestionPanne.GestionPanne.service.Implementation;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class ProblemService implements IProblemService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ProblemRepository problemRepository;

    @Override
    public Response reportProblem(Long userId, Long postId, Problem problemRequest) {
        Response response = new Response();
        try {
            if (userId == null || postId == null || problemRequest == null) {
                throw new IllegalArgumentException("L'utilisateur, le poste et le rapport de problème ne peuvent pas être nulls.");
            }
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));
            Post post = postRepository.findById(Math.toIntExact(postId)).orElseThrow(() -> new OurException("Post not found"));

            problemRequest.setUser(user);
            problemRequest.setPost(post);

            Problem savedProblem = problemRepository.save(problemRequest);

            response.setStatusCode(200);
            response.setMessage("Problème signalé avec succès.");
            response.setProblem(Utils.mapProblemEntityToProblemDTO(savedProblem));
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Entrée invalide : " + e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllProblems() {
        Response response = new Response();
        try {
            List<Problem> problems = problemRepository.findAll();
            response.setStatusCode(200);
            response.setMessage("Tous les problèmes récupérés avec succès.");
            response.setProblemList(Utils.mapProblemListEntityToProblemListDTO(problems));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur lors de la récupération des problèmes : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getProblemById(Long problemId) {
        Response response = new Response();
        try {
            Problem problem = problemRepository.findById(Math.toIntExact(problemId))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + problemId + " introuvable."));

            response.setStatusCode(200);
            response.setMessage("Problème récupéré avec succès.");
            response.setProblem(Utils.mapProblemEntityToProblemDTO(problem));
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
    public Response updateProblem(Long problemId, Problem problemRequest) {
        Response response = new Response();
        try {
            Problem existingProblem = problemRepository.findById(Math.toIntExact(problemId))
                    .orElseThrow(() -> new OurException("Problème avec l'ID " + problemId + " introuvable."));

            existingProblem.setTitle(problemRequest.getTitle());
            existingProblem.setFailureDetails(problemRequest.getFailureDetails());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            existingProblem.setFailureDate(calendar.getTime());

            Problem updatedProblem = problemRepository.save(existingProblem);

            response.setStatusCode(200);
            response.setMessage("Problème mis à jour avec succès.");
            response.setProblem(Utils.mapProblemEntityToProblemDTO(updatedProblem));
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
    public Response deleteProblem(Long problemId) {
        Response response = new Response();
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
    public Response getProblemsByPost(Long postId) {
        Response response = new Response();
        try {
            List<Problem> problems = problemRepository.findByPostId(postId);

            response.setStatusCode(200);
            response.setMessage("Problèmes pour le poste récupérés avec succès.");
            response.setProblems(Utils.mapProblemEntityListToDTOList(problems));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getProblemsByUser(Long userId) {
        Response response = new Response();
        try {
            List<Problem> problems = problemRepository.findByUserId(userId);

            response.setStatusCode(200);
            response.setMessage("Problèmes pour l'utilisateur récupérés avec succès.");
            response.setProblems(Utils.mapProblemEntityListToDTOList(problems));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur interne : " + e.getMessage());
        }
        return response;
    }
}