package com.GestionPanne.GestionPanne.controller;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.entity.Problem;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.service.Implementation.ProblemService;
import com.GestionPanne.GestionPanne.service.Implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add/{postId}")
    public ResponseEntity<Response<ProblemDTO>> reportProblem(@PathVariable Long postId,
                                                              @RequestBody Problem problemDTO) {
        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Trouver l'utilisateur dans la base de données
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("User not found"));


        // Appeler le service pour signaler le problème
        Response<ProblemDTO> response = problemService.reportProblem(user.getId(), postId, problemDTO);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<ProblemDTO>>> getAllProblems() {
        Response<List<ProblemDTO>> response = problemService.getAllProblems();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProblemDTO>> getProblemById(@PathVariable Long id) {
        Response<ProblemDTO> response = problemService.getProblemById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/getProblemByUserId")
    public ResponseEntity<Response<List<ProblemDTO>>> getProblemByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Trouver l'utilisateur dans la base de données
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("User not found"));
        Response<List<ProblemDTO>> response = problemService.getProblemsByUser(user.getId());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response<ProblemDTO>> updateProblem(@PathVariable Long id, @RequestBody Problem problemDTO) {
        Response<ProblemDTO> response = problemService.updateProblem(id, problemDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteProblem(@PathVariable Long id) {
        Response<Void> response = problemService.deleteProblem(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/validate/{id}")
    public ResponseEntity<Response<ProblemDTO>> validateProblem(@PathVariable Long id) {
        Response<ProblemDTO> response = problemService.validateProblem(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/assign/{id}")
    public ResponseEntity<Response<ProblemDTO>> assignProblemToTeam(@PathVariable Long id) {
        Response<ProblemDTO> response = problemService.assignProblemToTeam(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
