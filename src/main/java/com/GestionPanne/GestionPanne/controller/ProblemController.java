package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.entity.Problem;
import com.GestionPanne.GestionPanne.service.Implementation.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @PostMapping("/add/{userId}/{postId}")
    public ResponseEntity<Response<ProblemDTO>> reportProblem(@PathVariable Long userId,
                                                              @PathVariable Long postId,
                                                              @RequestBody Problem problemDTO) {
        Response<ProblemDTO> response = problemService.reportProblem(userId, postId, problemDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<ProblemDTO>>> getAllProblems() {
        Response<List<ProblemDTO>> response = problemService.getAllProblems();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProblemDTO>> getProblemById(@PathVariable Long id) {
        Response<ProblemDTO> response = problemService.getProblemById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response<ProblemDTO>> updateProblem(@PathVariable Long id, @RequestBody Problem problemDTO) {
        Response<ProblemDTO> response = problemService.updateProblem(id, problemDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
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
