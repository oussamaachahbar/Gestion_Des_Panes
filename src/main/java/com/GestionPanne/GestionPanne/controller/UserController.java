package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import com.GestionPanne.GestionPanne.service.Interface.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<UserDTO>> createUser(
            @RequestParam(required = false) Long postId,
            @RequestBody User user) {

        // Validation des rôles
        if (user.getIsTechnicien() == null) user.setIsTechnicien(false);
        if (user.getIsSupervisor() == null) user.setIsSupervisor(false);

        // Un technicien ne peut pas être superviseur
        if (user.getIsTechnicien()) {
            user.setIsSupervisor(false);
            user.setStartTime(null);
            user.setEndTime(null);
        }

        // Force postId à null pour les techniciens
        if (user.getIsTechnicien() || "ADMIN".equals(user.getRole())) {
            postId = null;
        }

        try {
            Response<UserDTO> response = userService.registre(postId, user);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            Response<UserDTO> errorResponse = new Response<>();
            errorResponse.setMessage("Error creating user: " + e.getMessage());
            errorResponse.setStatusCode(500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<Response<List<UserDTO>>> getAll() {
        Response<List<UserDTO>> response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers() {
        Response<List<UserDTO>> response = userService.getAllUserse();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable Long id) {
        logService.saveLog("Fetching user with ID: " + id);
        Response<UserDTO> response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Response<UserDTO>> getUserByEmail(@PathVariable String email) {
        logService.saveLog("Fetching user with email: " + email);
        Response<UserDTO> response = userService.getUserByEmail(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<Void>> resetPassword(
            @RequestBody @Valid PasswordResetRequest request,
            BindingResult bindingResult) {

        try {
            logService.saveLog("Attempting password reset for email: " + request.getEmail());

            if (bindingResult.hasErrors()) {
                logService.saveLog("Invalid password reset data for email: " + request.getEmail());
                return ResponseEntity.badRequest()
                        .body(new Response<>(400, "Invalid data", null));
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                logService.saveLog("Password mismatch for email: " + request.getEmail());
                return ResponseEntity.badRequest()
                        .body(new Response<>(400, "Passwords do not match", null));
            }

            Response<Void> response = userService.resetPassword(
                    request.getEmail(),
                    request.getNewPassword()
            );

            if (response.getStatusCode() == 200) {
                logService.saveLog("Password reset successfully for email: " + request.getEmail());
            } else {
                logService.saveLog("Failed to reset password for email: " + request.getEmail() +
                        ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (Exception e) {
            logService.saveLog("Error resetting password: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new Response<>(500, e.getMessage(), null));
        }
    }

    @PutMapping("/updateUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<UserDTO>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getName();

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));

            logService.saveLog("Admin " + adminEmail + " attempting to update user: " + existingUser.getEmail());

            Response<UserDTO> response = userService.updateUser(id, user);

            if (response.getStatusCode() == 200) {
                logService.saveLog("User updated successfully: " + existingUser.getEmail() +
                        " (ID: " + id + ") by admin: " + adminEmail);
            } else {
                logService.saveLog("Failed to update user with ID: " + id +
                        ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error updating user: " + e.getMessage());
            Response<UserDTO> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/updateUserProfil/{id}")
    public ResponseEntity<Response<UserDTO>> updateUserProfil(@PathVariable Long id, @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));

            logService.saveLog("User " + userEmail + " attempting to update their profile");

            Response<UserDTO> response = userService.updateUser(id, user);

            if (response.getStatusCode() == 200) {
                logService.saveLog("Profile updated successfully: " + existingUser.getName() +
                        " (" + existingUser.getEmail() + ")");
            } else {
                logService.saveLog("Failed to update profile for user ID: " + id);
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error updating profile: " + e.getMessage());
            Response<UserDTO> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/updateUserNew/{postId}/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<UserDTO>> updateUserWithPost(
            @PathVariable Long postId,
            @PathVariable Long id,
            @RequestBody User user) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getName();

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));

            logService.saveLog("Admin " + adminEmail + " attempting to update user with post assignment: " + existingUser.getEmail());

            Response<UserDTO> response = userService.updateUsere(postId, id, user);

            if (response.getStatusCode() == 200) {
                String postInfo = (postId != null) ?
                        " to post ID: " + postId :
                        " (post unassigned)";

                logService.saveLog("User updated successfully: " + existingUser.getName() +
                        " (ID: " + id + ")" + postInfo + " by admin: " + adminEmail);
            } else {
                logService.saveLog("Failed to update user with ID: " + id +
                        ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error updating user with post assignment: " + e.getMessage());
            Response<UserDTO> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getName();

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));

            logService.saveLog("Admin " + adminEmail + " attempting to delete user: " + existingUser.getEmail());

            Response<Void> response = userService.deleteUser(id);

            if (response.getStatusCode() == 200) {
                logService.saveLog("User deleted successfully: " + existingUser.getName() +
                        " (ID: " + id + ") by admin: " + adminEmail);
            } else {
                logService.saveLog("Failed to delete user with ID: " + id +
                        ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error deleting user: " + e.getMessage());
            Response<Void> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/get-logged-in-profile-info")
    public ResponseEntity<Response<UserDTO>> getLoggedInUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logService.saveLog("Fetching profile info for logged in user: " + email);
        Response<UserDTO> response = userService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getpost")
    public ResponseEntity<Response<PostDTO>> getUserPost() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logService.saveLog("Fetching post information for user: " + email);
        Response<PostDTO> response = userService.getUserPost();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/techniciens")
    public ResponseEntity<List<User>> getAllTechniciens() {
        List<User> techniciens = userRepository.findByIsTechnicienTrue();
        return ResponseEntity.ok(techniciens);
    }
}