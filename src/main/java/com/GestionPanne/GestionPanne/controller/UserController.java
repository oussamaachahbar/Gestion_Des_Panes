package com.GestionPanne.GestionPanne.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/add/{postId}")
    public ResponseEntity<Response<UserDTO>> createUser(@PathVariable Long postId, @RequestBody User user) {
        Response<UserDTO> response = userService.registre(postId, user);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (response.getMessage().equals("The user does not have a post assigned.")) {
            logService.saveLog("Tentative d'ajout d'un utilisateur sans poste attribué : " + user.getEmail());

            return ResponseEntity.status(200).body(response);
        }
        Post post=postRepository.findById(postId);

        logService.saveLog("Nouvel utilisateur ajouté : " + user.getEmail() + " (Poste Name: " + post.getName() + ") par  "+email);

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
        User usere=userRepository.findById(id).orElseThrow(() -> new OurException("User not found with id: "));
        Response<UserDTO> response = userService.updateUser(id, user);
        if (response.getStatusCode() == 200) {
            logService.saveLog("l'utilisateur avec le nom : "+usere.getName()+" a modifié ses inormations personnels");
        } else {
            logService.saveLog("Échec de la modification du user avec ID: "+id );
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PutMapping("/updateUserNew/{PostId}/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<Response<UserDTO>> updateUser(@PathVariable Long PostId,@PathVariable Long id, @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User usere=userRepository.findById(id).orElseThrow(() -> new OurException("User not found with id: "));

        Response<UserDTO> response = userService.updateUsere(PostId,id, user);
        if (response.getStatusCode() == 200) {
            logService.saveLog("l'utilisateur avec le nom : "+usere.getName()+" a été modifié par "+email);
        } else {
            logService.saveLog("Échec de la modification du user avec ID: "+id );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user=userRepository.findById(id).orElseThrow(() -> new OurException("User not found with id: "));

        Response<Void> response = userService.deleteUser(id);
        if (response.getStatusCode() == 200) {
            logService.saveLog("l'utilisateur avec le nom : "+user.getName()+" a été supprimé par "+email);
        } else {
            logService.saveLog("Échec de la delete du user avec ID: "+id );
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
