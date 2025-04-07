package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.PasswordResetRequest;
import com.GestionPanne.GestionPanne.dto.PostDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
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
import java.util.Optional;

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
    @PostMapping("/add/{postId}")
    public ResponseEntity<Response<UserDTO>> createUser(@PathVariable Long postId, @RequestBody User user) {
        Response<UserDTO> response = userService.registre(postId, user);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (response.getMessage().equals("The user does not have a post assigned.")) {
            logService.saveLog("Échec de l'ajout de l'utilisateur " + user.getEmail() + " : aucun poste attribué.");

            return ResponseEntity.status(200).body(response);
        }

        Post post = postRepository.findById(postId);
        logService.saveLog("Utilisateur créé avec succès : " + user.getEmail() + " | Poste : " + post.getName() + " | Ajouté par : " + email);

        return ResponseEntity.status(response.getStatusCode()).body(response);
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
        Response<UserDTO> response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<Response<UserDTO>> getUserByEmail(@PathVariable String email) {
        Response<UserDTO> response = userService.getUserByEmail(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<Void>> resetPassword(
            @RequestBody @Valid PasswordResetRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(new Response<>(400, "Données invalides", null));
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new Response<>(400, "Les mots de passe ne correspondent pas", null));
        }

        try {
            Response<Void> response = userService.resetPassword(
                    request.getEmail(),
                    request.getNewPassword()
            );

            if (response.getStatusCode() < 100 || response.getStatusCode() >= 600) {
                throw new IllegalArgumentException("Code d'état HTTP invalide");
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response<>(500, e.getMessage(), null));
        }
    }

    @PutMapping("/updateUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<Response<UserDTO>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Response<UserDTO> response = userService.updateUser(id, user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PutMapping("/updateUserProfil/{id}")
    public ResponseEntity<Response<UserDTO>> updateUserProfil(@PathVariable Long id, @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new OurException("Utilisateur introuvable avec l'ID : " + id));

        Response<UserDTO> response = userService.updateUser(id, user);

        if (response.getStatusCode() == 200) {
            logService.saveLog("Profil mis à jour avec succès : " + existingUser.getName() + " (" + existingUser.getEmail() + ") a modifié ses informations personnelles.");
        } else {
            logService.saveLog("Échec de la mise à jour du profil de l'utilisateur avec l'ID : " + id);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateUserNew/{postId}/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<UserDTO>> updateUser(@PathVariable Long postId, @PathVariable Long id, @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new OurException("Utilisateur introuvable avec l'ID : " + id));

        Response<UserDTO> response = userService.updateUsere(postId, id, user);

        if (response.getStatusCode() == 200) {
            logService.saveLog("Mise à jour réussie : L'utilisateur '" + existingUser.getName() + "' (ID: " + id + ") a été modifié par " + email + ".");
        } else {
            logService.saveLog("Échec de la mise à jour de l'utilisateur avec l'ID : " + id + ".");
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new OurException("Utilisateur introuvable avec l'ID : " + id));

        Response<Void> response = userService.deleteUser(id);

        if (response.getStatusCode() == 200) {
            logService.saveLog("Suppression réussie : L'utilisateur '" + existingUser.getName() + "' (ID: " + id + ") a été supprimé par " + email + ".");
        } else {
            logService.saveLog("Échec de la suppression de l'utilisateur avec l'ID : " + id + ".");
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-logged-in-profile-info")
    public ResponseEntity<Response<UserDTO>> getLoggedInUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Response<UserDTO> response = userService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/getpost")
    public ResponseEntity<Response<PostDTO>> getUserPost() {
        Response<PostDTO> response = userService.getUserPost();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
