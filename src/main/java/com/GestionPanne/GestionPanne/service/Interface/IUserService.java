package com.GestionPanne.GestionPanne.service.Interface;

import java.util.List;
import com.GestionPanne.GestionPanne.dto.ProblemDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.LoginRequest;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.User;

public interface IUserService {
    Response<String> login(LoginRequest loginRequest);
    Response<UserDTO> registre(Long postId, User user);
    Response<List<UserDTO>> getAllUsers();
    Response<UserDTO> getUserById(long id);
    Response<UserDTO> updateUser(Long userId, User user);
    Response<Void> deleteUser(long id);
    Response<UserDTO> getUserByEmail(String email);
    Response<List<ProblemDTO>> getUserProblems(long userId);
    Response<UserDTO> getMyInfo(String email);
}
