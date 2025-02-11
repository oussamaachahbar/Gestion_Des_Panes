package com.GestionPanne.GestionPanne.utils;

import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.*;

import java.security.SecureRandom;
import java.util.Collections;
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

    // 🟢 Mapping de l'entité User vers UserDTO
    public static UserDTO mapUserEntityToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());
        userDTO.setSupervisor(user.isSupervisor());
        return userDTO;
    }

    // 🟢 Mapping de l'entité Project vers ProjectDTO
    public static ProjectDTO mapProjectEntityToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setNameProject(project.getNameProject());
        projectDTO.setFamilyId(project.getFamily().getId());
        projectDTO.setFiliereId(project.getFiliere().getId());
        return projectDTO;
    }

    // 🟢 Mapping de l'entité Family vers FamilyDTO
    public static FamilyDTO mapFamilyEntityToFamilyDTO(Family family) {
        FamilyDTO familyDTO = new FamilyDTO();
        familyDTO.setId(family.getId());
        familyDTO.setName(family.getName());
        return familyDTO;
    }

    // 🟢 Mapping de l'entité Filiere vers FiliereDTO
    public static FiliereDTO mapFiliereEntityToFiliereDTO(Filiere filiere) {
        FiliereDTO filiereDTO = new FiliereDTO();
        filiereDTO.setId(filiere.getId());
        filiereDTO.setName(filiere.getName());
        filiereDTO.setCity(filiere.getCity());
        filiereDTO.setCountry(filiere.getCountry());
        return filiereDTO;
    }

    // 🟢 Mapping de l'entité Post vers PostDTO
    public static PostDTO mapPostEntityToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setName(post.getName());
        postDTO.setDescription(post.getDescription());
        postDTO.setPosition(post.getPosition());
        postDTO.setProjectId(post.getProject().getId());
        postDTO.setUsers(post.getUsers().stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList()));
        return postDTO;
    }

    // 🟢 Mapping de l'entité Problem vers ProblemDTO
    public static ProblemDTO mapProblemEntityToProblemDTO(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setId(problem.getId());
        problemDTO.setUser(problem.getUser().getId());
        problemDTO.setPost(Collections.singletonList(problem.getPost().getId()));
        problemDTO.setFailureDate(problem.getFailureDate());
        problemDTO.setSolutionDate(problem.getSolutionDate());
        problemDTO.setFailureDetails(problem.getFailureDetails());
        problemDTO.setPriority(String.valueOf(problem.getPriority()));
        problemDTO.setEtat(String.valueOf(problem.getEtat()));
        return problemDTO;
    }

    public static ProjectDTO mapProjectEntityToProjectDTOWithPosts(Project project) {
        ProjectDTO projectDTO = mapProjectEntityToProjectDTO(project);
        if (project.getPosts() != null) {
            projectDTO.setPosts(
                    project.getPosts().stream().map(Utils::mapPostEntityToPostDTO).collect(Collectors.toList())
            );
        }
        return projectDTO;
    }

    // 🔄 Mapping avancé : Ajouter la liste des problèmes dans PostDTO
    public static PostDTO mapPostEntityToPostDTOWithProblems(Post post) {
        PostDTO postDTO = mapPostEntityToPostDTO(post);
        if (post.getProblems() != null) {
            postDTO.setProblems(
                    post.getProblems().stream().map(Utils::mapProblemEntityToProblemDTO).collect(Collectors.toList())
            );
        }
        return postDTO;
    }

    // 🔄 Mapping avancé : Ajouter la liste des problèmes et posts dans UserDTO
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

    // 📜 Conversion de listes d'entités vers listes de DTOs
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
