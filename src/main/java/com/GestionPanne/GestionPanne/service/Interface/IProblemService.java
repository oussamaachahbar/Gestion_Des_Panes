package com.GestionPanne.GestionPanne.service.Interface;
import java.util.List;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Problem;
public interface IProblemService
{
    Response reportProblem(Long userId, Long postId, Problem problemRequest);

    Response getAllProblems();

    Response getProblemById(Long problemId);

    Response updateProblem(Long problemId, Problem problemRequest);

    Response deleteProblem(Long problemId);

    Response getProblemsByPost(Long postId);

    Response getProblemsByUser(Long userId);

}
