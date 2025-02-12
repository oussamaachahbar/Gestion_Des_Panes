package com.GestionPanne.GestionPanne.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int statusCode;
    private String message;

    private String token;
    private String role;
    private String expirationTime;
    private UserDTO user;
    private ProjectDTO project;
    private ProblemDTO problem;
    private PostDTO post;
    private FiliereDTO filiere;
    private FamilyDTO family;
    private List<UserDTO> userList;
    private List<ProjectDTO> projectList;
    private List<ProblemDTO> problemList=new ArrayList<>();
    private List<PostDTO> postList;
    private List<FiliereDTO> filiereList;
    private List<FamilyDTO> familyList;

}
