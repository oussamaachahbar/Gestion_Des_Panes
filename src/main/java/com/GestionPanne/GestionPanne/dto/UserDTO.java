package com.GestionPanne.GestionPanne.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String cin;
    private String phone;
    private String role;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean supervisor;
    private boolean status;
    private Long postId;
    private String postName;
    private PostDTO postDTO;
    private List<Long> problemIds;
    private List<ProblemDTO> problems;
}
