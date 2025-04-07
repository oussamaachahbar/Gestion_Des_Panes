package com.GestionPanne.GestionPanne.service.Interface;
import java.util.List;

import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Problem;
import com.GestionPanne.GestionPanne.entity.Project;
import org.springframework.web.multipart.MultipartFile;

public interface IProblemService
{
    Response<ProblemDTO> reportProblem(Long userId, Long postId, Problem problemRequest,MultipartFile file);

    Response<List<ProblemDTO>> getAllProblems();

    Response<ProblemDTO> getProblemById(Long problemId);

    Response<ProblemDTO> updateProblem(Long problemId, Problem problemRequest);

    Response<Void> deleteProblem(Long problemId);

    Response<List<ProblemDTO>> getProblemsByPost(Long postId);

    Response<List<ProblemDTO>> getProblemsByUser(Long userId);


    Response<ProblemDTO> validateProblem(Long id);

    Response<ProblemDTO> assignProblemToTeam(Long id);
}
