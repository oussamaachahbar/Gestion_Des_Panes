package com.GestionPanne.GestionPanne.utils;

import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomConfirmationCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    public static UserDTO mapUserEntityToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        // Map de base
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setCin(user.getCin());
        userDTO.setRole(user.getRole());
        userDTO.setSupervisor(user.isSupervisor());
        userDTO.setTechnicien(user.isTechnicien()); // Nouveau champ mappé
        userDTO.setStatus(user.isStatus());
        userDTO.setEndTime(user.getEndTime());
        userDTO.setStartTime(user.getStartTime());

        if (user.getPost() != null) {
            userDTO.setPostName(user.getPost().getName());
            userDTO.setPostId(user.getPost().getId());
        } else {
            userDTO.setPostName("Not assigned");
            userDTO.setPostId(null);
        }
        return userDTO;
    }




    public static ProjectDTO mapProjectEntityToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setNameProject(project.getNameProject());
        if(project.getFamily()!=null) {
            projectDTO.setFamilyName(project.getFamily().getName());
            projectDTO.setFamilyId(project.getFamily().getId());
        }else{
            projectDTO.setFamilyName("Non Family");
            projectDTO.setFamilyId(null);
        }
        if(project.getFiliere()!=null) {
            projectDTO.setFiliereName(project.getFiliere().getName());
            projectDTO.setFiliereId(project.getFiliere().getId());

        }else{
            projectDTO.setFiliereName("Non Filiere");
            projectDTO.setFiliereId(null);
        }

        return projectDTO;
    }

    public static FamilyDTO mapFamilyEntityToFamilyDTO(Family family) {
        FamilyDTO familyDTO = new FamilyDTO();
        familyDTO.setId(family.getId());
        familyDTO.setName(family.getName());
        return familyDTO;
    }

    public static FiliereDTO mapFiliereEntityToFiliereDTO(Filiere filiere) {
        FiliereDTO filiereDTO = new FiliereDTO();
        filiereDTO.setId(filiere.getId());
        filiereDTO.setName(filiere.getName());
        filiereDTO.setCity(filiere.getCity());
        filiereDTO.setCountry(filiere.getCountry());
        return filiereDTO;
    }

    public static PostDTO mapPostEntityToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setName(post.getName());
        postDTO.setDescription(post.getDescription());
        postDTO.setPosition(post.getPosition());

        if (post.getProject() != null) {
            postDTO.setProjectId(post.getProject().getId());
            postDTO.setProjectName(post.getProject().getNameProject());
        } else {
            postDTO.setProjectId(null); // Ou une valeur par défaut si nécessaire
            postDTO.setProjectName("Projet non attribué"); // Valeur par défaut
        }

        postDTO.setUsers(post.getUsers() != null
                ? post.getUsers().stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList())
                : new ArrayList<>());

        postDTO.setProblems(post.getProblems() != null
                ? post.getProblems().stream().map(Utils::mapProblemEntityToProblemDTO).collect(Collectors.toList())
                : new ArrayList<>());

        return postDTO;
    }


    public static ProblemDTO mapProblemEntityToProblemDTO(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setId(problem.getId());
        problemDTO.setUserId(problem.getUser().getId());
        problemDTO.setPostId(problem.getPost().getId());
        problemDTO.setTitle(problem.getTitle());
        problemDTO.setPostname(problem.getPost().getName());
        problemDTO.setUsername(problem.getUser().getName());
        problemDTO.setFailureDate(problem.getFailureDate());

        // Ajout des nouveaux champs
        problemDTO.setAssignmentDate(problem.getAssignmentDate());
        problemDTO.setSolutionDate(problem.getSolutionDate());

        if(problem.getTechnicien() != null) {
            problemDTO.setTechnicien(problem.getTechnicien().getName());
            problemDTO.setTechnicienemail(problem.getTechnicien().getUsername());
            problemDTO.setTechnicienId(problem.getTechnicien().getId());
        }

        problemDTO.setFailureDetails(problem.getFailureDetails());
        problemDTO.setImageUrl(problem.getImageUrl());

        if (problem.getPriority() != null) {
            problemDTO.setPriority(problem.getPriority().name());
        }
        if (problem.getEtat() != null) {
            problemDTO.setEtat(problem.getEtat().name());
        }

        return problemDTO;
    }

    public static List<logDTO> mapLogListEntityToLogListDTO(List<Log> logList) {
        List<logDTO> logDTOList = new ArrayList<>();
        for (Log log : logList) {
            logDTO logDTO = new logDTO();
            logDTO.setId(log.getId());
            logDTO.setMessage(log.getAction());
            logDTO.setDate(log.getCreatedAt());
            logDTOList.add(logDTO);
        }
        return logDTOList;
    }





    public static PostDTO mapPostEntityToPostDTOWithProblems(Post post) {
        PostDTO postDTO = mapPostEntityToPostDTO(post);
        if (post.getProblems() != null) {
            postDTO.setProblems(
                    post.getProblems().stream().map(Utils::mapProblemEntityToProblemDTO).collect(Collectors.toList())
            );
        }
        return postDTO;
    }

    public static UserDTO mapUserEntityToUserDTOWithPostsAndProblems(User user) {
        UserDTO userDTO = mapUserEntityToUserDTO(user);
        if (user.getPost() != null) {
            userDTO.setPostId(user.getPost().getId());
        }
        if (user.getProblems() != null) {
            userDTO.setProblems(user.getProblems().stream().map(Utils::mapProblemEntityToProblemDTO).collect(Collectors.toList()));
        }
        return userDTO;
    }

    public static List<UserDTO> mapUserListEntityToUserListDTO(List<User> userList) {
        return userList.stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList());
    }

    public static List<ProjectDTO> mapProjectListEntityToProjectListDTO(List<Project> projectList) {
        return projectList.stream().map(Utils::mapProjectEntityToProjectDTO).collect(Collectors.toList());
    }

    public static List<FamilyDTO> mapFamilyListEntityToFamilyListDTO(List<Family> familyList) {
        return familyList.stream().map(Utils::mapFamilyEntityToFamilyDTO).collect(Collectors.toList());
    }

    public static List<FiliereDTO> mapFiliereListEntityToFiliereListDTO(List<Filiere> filiereList) {
        return filiereList.stream().map(Utils::mapFiliereEntityToFiliereDTO).collect(Collectors.toList());
    }

    public static List<PostDTO> mapPostListEntityToPostListDTO(List<Post> postList) {
        return postList.stream().map(Utils::mapPostEntityToPostDTO).collect(Collectors.toList());
    }

    public static List<ProblemDTO> mapProblemListEntityToProblemListDTO(List<Problem> problemList) {
        return problemList.stream().map(Utils::mapProblemEntityToProblemDTO).collect(Collectors.toList());
    }

}
