package com.GestionPanne.GestionPanne.controller;
import com.GestionPanne.GestionPanne.repo.ProblemRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.entity.Problem;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.exception.OurException;
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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("Utilisateur non trouvé avec l'email : " + email));

        Response<ProblemDTO> response = problemService.reportProblem(user.getId(), postId, problemDTO, imageFile);
        if(response.getStatusCode()==200){
        logService.saveLog("Probleme assigner par : "+email+" dans le post "+user.getPost().getName());

        }
        else{
            logService.saveLog("Error lors de signaler le probleme par :"+email);
        }


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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Problem problem =problemRepository.findById(Math.toIntExact(id)).orElseThrow(()->new OurException("NO PROBLEM FOUND "));
        Response<Void> response = problemService.deleteProblem(id);
        if (response.getStatusCode() == 200) {
            logService.saveLog("le problem avec le nom : "+problem.getTitle() +" avec  ID : "+problem.getId()+" a été supprimé par "+email);
        } else {
            logService.saveLog("Échec de la delete du familly avec ID: "+id );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/validate/{id}")
    public ResponseEntity<Response<ProblemDTO>> validateProblem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Problem problem =problemRepository.findById(Math.toIntExact(id)).orElseThrow(()-> new OurException("NO PROBLEM FOUND WITH ID"));
        Response<ProblemDTO> response = problemService.validateProblem(id);
        if (response.getStatusCode() == 200) {
            logService.saveLog("Admin à validé le problem "+problem.getTitle()+ " Avec l'ID "+problem.getId());
        } else {
            logService.saveLog(" ERROR DE VALIDATION DE PROBLEM AVEC L'ID "+problem.getId() );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/assign/{id}")
    public ResponseEntity<Response<ProblemDTO>> assignProblemToTeam(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Problem problem =problemRepository.findById(Math.toIntExact(id)).orElseThrow(()-> new OurException("NO PROBLEM FOUND WITH ID"));
        Response<ProblemDTO> response = problemService.assignProblemToTeam(id);
        if (response.getStatusCode() == 200) {
            logService.saveLog(" Admin à assignée le problem "+problem.getTitle()+ " Avec l'ID "+problem.getId());
        } else {
            logService.saveLog("ERROR DE L'ASSIGNATION DE PROBLEM AVEC L'ID "+problem.getId() );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
