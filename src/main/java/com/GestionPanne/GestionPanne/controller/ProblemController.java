package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.entity.Problem;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.ProblemRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import com.GestionPanne.GestionPanne.service.Implementation.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private LogService logService;

    @PostMapping(value = "/add/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ProblemDTO>> reportProblem(
            @PathVariable Long postId,
            @RequestPart("problem") Problem problemDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found with email: " + email));

            logService.saveLog("Attempting to report problem by user: " + email + " for post ID: " + postId);

            Response<ProblemDTO> response = problemService.reportProblem(user.getId(), postId, problemDTO, imageFile);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Problem reported successfully by: " + email + " in post: " + user.getPost().getName());
            } else {
                logService.saveLog("Failed to report problem by: " + email + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error reporting problem by: " + email + ". Error: " + e.getMessage());
            Response<ProblemDTO> errorResponse = new Response<>();
            errorResponse.setStatusCode(404);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<ProblemDTO>>> getAllProblems() {
        Response<List<ProblemDTO>> response = problemService.getAllProblems();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProblemDTO>> getProblemById(@PathVariable Long id) {
        logService.saveLog("Fetching problem with ID: " + id);
        Response<ProblemDTO> response = problemService.getProblemById(id);

        if(response.getStatusCode() != 200) {
            logService.saveLog("Problem not found with ID: " + id);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getProblemByUserId")
    public ResponseEntity<Response<List<ProblemDTO>>> getProblemByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            logService.saveLog("Fetching problems for user: " + email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found"));

            Response<List<ProblemDTO>> response = problemService.getProblemsByUser(user.getId());
            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error fetching problems for user: " + email + ". Error: " + e.getMessage());
            Response<List<ProblemDTO>> errorResponse = new Response<>();
            errorResponse.setStatusCode(404);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TECHNICIAN')")
    public ResponseEntity<Response<ProblemDTO>> updateProblem(@PathVariable Long id, @RequestBody Problem problemDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("No problem found with ID: " + id));

            logService.saveLog("Attempting to update problem: " + problem.getTitle() + " (ID: " + id + ") by " + email);

            Response<ProblemDTO> response = problemService.updateProblem(id, problemDTO);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Problem updated successfully: " + problem.getTitle() + " (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to update problem with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error updating problem with ID: " + id + ". Error: " + e.getMessage());
            Response<ProblemDTO> errorResponse = new Response<>();
            errorResponse.setStatusCode(404);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteProblem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("No problem found with ID: " + id));

            logService.saveLog("Attempting to delete problem: " + problem.getTitle() + " (ID: " + id + ") by " + email);

            Response<Void> response = problemService.deleteProblem(id);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Problem deleted successfully: " + problem.getTitle() + " (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to delete problem with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error deleting problem with ID: " + id + ". Error: " + e.getMessage());
            Response<Void> errorResponse = new Response<>();
            errorResponse.setStatusCode(404);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/validate/{id}")
    public ResponseEntity<Response<ProblemDTO>> validateProblem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("No problem found with ID: " + id));

            logService.saveLog("Attempting to validate problem: " + problem.getTitle() + " (ID: " + id + ") by admin: " + email);

            Response<ProblemDTO> response = problemService.validateProblem(id);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Problem validated successfully: " + problem.getTitle() + " (ID: " + id + ") by admin: " + email);
            } else {
                logService.saveLog("Failed to validate problem with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error validating problem with ID: " + id + ". Error: " + e.getMessage());
            Response<ProblemDTO> errorResponse = new Response<>();
            errorResponse.setStatusCode(404);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/assign/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<ProblemDTO>> assignProblemToTeam(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {

        Long technicienId = request.get("technicienId");
        if (technicienId == null) {
            logService.saveLog("Assignment failed - technicianId is required");
            return ResponseEntity.badRequest().body(
                    new Response<>(400, "technicianId is required.", null)
            );
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Problem problem = problemRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new OurException("No problem found with ID: " + id));

            logService.saveLog("Attempting to assign problem: " + problem.getTitle() + " (ID: " + id +
                    ") to technician ID: " + technicienId + " by admin: " + email);

            Response<ProblemDTO> response = problemService.assignProblemToTeam(id, technicienId);

            if(response.getStatusCode() == 200) {
                logService.saveLog("Problem assigned successfully: " + problem.getTitle() + " (ID: " + id +
                        ") to technician ID: " + technicienId + " by admin: " + email);
            } else {
                logService.saveLog("Failed to assign problem with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error assigning problem with ID: " + id + ". Error: " + e.getMessage());
            Response<ProblemDTO> errorResponse = new Response<>();
            errorResponse.setStatusCode(404);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/getProblemByTechnicienId")
    public ResponseEntity<Response<List<ProblemDTO>>> getProblemByTechnicienId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            logService.saveLog("Fetching problems assigned to technician: " + email);

            User technicien = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("Technician not found"));

            Response<List<ProblemDTO>> response = problemService.getProblemsByTechnicien(technicien.getId());
            return ResponseEntity.ok(response);

        } catch (OurException e) {
            logService.saveLog("Error fetching problems for technician: " + email + ". Error: " + e.getMessage());
            Response<List<ProblemDTO>> errorResponse = new Response<>();
            errorResponse.setStatusCode(404);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }
}