package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.*;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.service.Interface.IUserService;
import com.GestionPanne.GestionPanne.utils.JWTUtils;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response<UserDTO> registre(Long postId, User user) {
        Response<UserDTO> response = new Response<>();
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new OurException("Email already exists: " + user.getEmail());
            }

            if ("ADMIN".equals(user.getRole())) {
                postId = null;
            }

            Post post = null;
            if (postId != null) {
                post = postRepository.findById(postId);

                // Vérifier si le post a déjà un superviseur
                boolean hasSupervisor = post.getUsers().stream().anyMatch(User::getIsSupervisor);
                if (user.getIsSupervisor() && hasSupervisor) {
                    throw new OurException("This post already has a supervisor. Only one supervisor is allowed per post.");
                }
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPost(post); // Assigne null si l'utilisateur est ADMIN

            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);

            response.setStatusCode(201);
            response.setMessage("User registered successfully");
            response.setData(userDTO);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during user registration: " + e.getMessage());
        }
        return response;
    }
    @Override
    public Response<UserDTO> registreUser(Long postId, User user) {
        Response<UserDTO> response = new Response<>();
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new OurException("Email already exists: " + user.getEmail());
            }

            if ("ADMIN".equals(user.getRole())) {
                postId = null;
            }

            Post post = null;
            if (postId != null) {
                post = postRepository.findById(postId);

                // Vérifier si le post a déjà un superviseur
                boolean hasSupervisor = post.getUsers().stream().anyMatch(User::getIsSupervisor);
                if (user.getIsSupervisor() && hasSupervisor) {
                    throw new OurException("This post already has a supervisor. Only one supervisor is allowed per post.");
                }
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPost(post); // Assigne null si l'utilisateur est ADMIN
            user.setIsSupervisor(false);
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);

            response.setStatusCode(201);
            response.setMessage("User registered successfully");
            response.setData(userDTO);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during user registration: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<UserDTO>> getAllUserse() {
        Response<List<UserDTO>> response = new Response<>();
        try {
            // Récupérer tous les utilisateurs
            List<User> users = userRepository.findByUserRole();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(users);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Users retrieved successfully");
            response.setData(userDTOList); // Utilisation de setData pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving users: " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<String> login(LoginRequest loginRequest) {
        Response<String> response = new Response<>();
        try {
            // Authentifier l'utilisateur
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Récupérer l'utilisateur
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new OurException("User not found"));

            // Générer le token JWT
            String token = jwtUtils.generateToken(user);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Login successful");
            response.setData(token); // Utilisation de setData pour le token
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setToken(token);
            response.setSupervisor(user.isSupervisor());
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error during login: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {
        Response<List<UserDTO>> response = new Response<>();
        try {
            // Récupérer tous les utilisateurs
            List<User> users = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(users);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Users retrieved successfully");
            response.setData(userDTOList); // Utilisation de setData pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving users: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<UserDTO> getUserById(long id) {
        Response<UserDTO> response = new Response<>();
        try {
            // Récupérer l'utilisateur par son ID
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("User retrieved successfully");
            response.setData(userDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<UserDTO> updateUser(Long userId, User user) {
        Response<UserDTO> response = new Response<>();
        try {
            // Vérifier si l'utilisateur existe
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new OurException("User not found with ID: " + userId);
            }

            User existingUser = userOpt.get();
            boolean wasSupervisor = existingUser.getIsSupervisor() != null && existingUser.getIsSupervisor();
            boolean wantsToBeSupervisor = user.getIsSupervisor() != null && user.getIsSupervisor();

            // Vérification avant mise à jour du statut de superviseur
            if (!wasSupervisor && wantsToBeSupervisor) {
                if (existingUser.getPost() != null) {
                    Long postId = existingUser.getPost().getId();

                    // Vérifier si un superviseur existe déjà pour ce poste
                    boolean postHasSupervisor = userRepository.existsByPostIdAndIsSupervisorTrue(postId);
                    if (postHasSupervisor) {
                        throw new OurException("This post already has a supervisor. You cannot assign another one.");
                    }
                }
            }

            // Mise à jour des informations de l'utilisateur
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            existingUser.setPhone(user.getPhone());
            existingUser.setName(user.getName());
            existingUser.setIsSupervisor(wantsToBeSupervisor);

            if (user.getPost() != null) {
                existingUser.setPost(user.getPost());
            }

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Sauvegarde des modifications
            User updatedUser = userRepository.save(existingUser);
            response.setStatusCode(200);
            response.setMessage("User updated successfully");
            response.setData(Utils.mapUserEntityToUserDTO(updatedUser));

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Void> deleteUser(long id) {
        Response<Void> response = new Response<>();
        try {
            // Vérifier si l'utilisateur existe
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));

            // Supprimer l'utilisateur
            userRepository.delete(user);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("User deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error deleting user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<UserDTO> getUserByEmail(String email) {
        Response<UserDTO> response = new Response<>();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found with email: " + email));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            response.setStatusCode(200); // 200 OK
            response.setMessage("User retrieved successfully");
            response.setData(userDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<ProblemDTO>> getUserProblems(long userId) {
        Response<List<ProblemDTO>> response = new Response<>();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("User not found with ID: " + userId));

            UserDTO userDTO = Utils.mapUserEntityToUserDTOWithPostsAndProblems(user);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("User problems retrieved successfully");
            response.setData(userDTO.getProblems()); // Utilisation directe de la liste de ProblemDTO
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving user problems: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<UserDTO> getMyInfo(String email) {
        Response<UserDTO> response = new Response<>();
        try {
            // Récupérer l'utilisateur par son email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found with email: " + email));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("User info retrieved successfully");
            response.setData(userDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving user info: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<PostDTO> getUserPost() {
        Response<PostDTO> response = new Response<>();
        try {
            // 1. Récupérer l'utilisateur connecté
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // L'email de l'utilisateur connecté

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found"));

            // 2. Vérifier si l'utilisateur a un post_id
            if (user.getPost() == null) {
                response.setStatusCode(404);
                response.setMessage("No post found for this user.");
                return response;
            }

            // 3. Convertir l'entité Post en PostDTO
            Post post = user.getPost();
            PostDTO postDTO = Utils.mapPostEntityToPostDTO(post);

            // 4. Retourner le DTO
            response.setStatusCode(200);
            response.setMessage("Post retrieved successfully");
            response.setData(postDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving post: " + e.getMessage());
        }
        return response;
    }

}