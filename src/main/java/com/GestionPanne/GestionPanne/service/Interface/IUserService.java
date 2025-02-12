package com.GestionPanne.GestionPanne.service.Interface;
import java.util.List;
import java.util.Optional;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.LoginRequest;
import com.GestionPanne.GestionPanne.entity.User;
public interface IUserService {
    Response login(LoginRequest loginRequest);
    Response registre(User user);
    Response getAllUsers();
    Response getUserById(long id);
    Response updateUser(User user);
    Response deleteUser(long id);
    Response getUserByEmail(String email);
    Response getUserProblems(long userId);
    Response getMyInfo(String email);

}
