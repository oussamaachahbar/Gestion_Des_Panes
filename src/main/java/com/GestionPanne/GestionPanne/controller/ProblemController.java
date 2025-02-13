package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.entity.Problem;
import com.GestionPanne.GestionPanne.service.Implementation.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    // Signaler un problème
    @PostMapping
    public Response<ProblemDTO> reportProblem(@PathVariable Long UserId,@PathVariable Long PostId,@RequestBody Problem problemDTO) {
        return problemService.reportProblem(UserId,PostId,problemDTO);
    }

    // Récupérer tous les problèmes
    @GetMapping
    public Response<List<ProblemDTO>> getAllProblems() {
        return problemService.getAllProblems();
    }

    // Récupérer un problème par son ID
    @GetMapping("/{id}")
    public Response<ProblemDTO> getProblemById(@PathVariable Long id) {
        return problemService.getProblemById(id);
    }

    // Mettre à jour un problème
    @PutMapping("/{id}")
    public Response<ProblemDTO> updateProblem(@PathVariable Long id, @RequestBody Problem problemDTO) {
        return problemService.updateProblem(id, problemDTO);
    }

    // Supprimer un problème
    @DeleteMapping("/{id}")
    public Response<Void> deleteProblem(@PathVariable Long id) {
        return problemService.deleteProblem(id);
    }

    // Valider un problème (Admin seulement)
    @PutMapping("/{id}/validate")
    public Response<ProblemDTO> validateProblem(@PathVariable Long id) {
        return problemService.validateProblem(id);
    }

    // Assigner un problème à une équipe (Admin seulement)
    @PutMapping("/{id}/assign-team")
    public Response<ProblemDTO> assignProblemToTeam(
            @PathVariable Long id,
            @RequestParam Long teamId) {
        return problemService.assignProblemToTeam(id, teamId);
    }
}
