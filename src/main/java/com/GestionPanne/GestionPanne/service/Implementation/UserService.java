package com.GestionPanne.GestionPanne.service.Implementation;
import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.*;
import com.GestionPanne.GestionPanne.service.Interface.IUserService;
public class UserService implements IUserService {
    @Override
    public Response login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public Response registre(User user) {
        return null;
    }

    @Override
    public Response getAllUsers() {
        return null;
    }

    @Override
    public Response getUserById(long id) {
        return null;
    }

    @Override
    public Response updateUser(User user) {
        return null;
    }

    @Override
    public Response deleteUser(long id) {
        return null;
    }

    @Override
    public Response getUserByEmail(String email) {
        return null;
    }

    @Override
    public Response getUserProblems(long userId) {
        return null;
    }

    @Override
    public Response getMyInfo(String email) {
        return null;
    }
}
