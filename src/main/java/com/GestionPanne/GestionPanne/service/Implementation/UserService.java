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
    private LogService logService;
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

    public Response<UserDTO> registre(Long postId, User user) {
        Response<UserDTO> response = new Response<>();
        try {
            // Validation des doublons
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new OurException("Email already exists: " + user.getEmail());
            }

            // Gestion du post
            Post post = null;
            if (postId != null && !user.getIsTechnicien()) {
                post = postRepository.findById(postId);
                if (post == null) {
                    throw new OurException("Post not found");
                }
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPost(post);

            // Nettoyage des champs selon le rôle
            if (user.getIsTechnicien()) {
                user.setPost(null);
                user.setStartTime(null);
                user.setEndTime(null);
            }

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
            response.setMessage("Error during registration: " + e.getMessage());
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

            if (userRepository.existsByCin(user.getCin())) {
                throw new OurException("CIN already exists. Please use a different CIN.");
            }

            if ("ADMIN".equals(user.getRole())) {
                postId = null;
            }

            Post post = null;
            if (postId != null) {
                post = postRepository.findById(postId);

                // Vérification du superviseur existant pour le poste
                boolean hasSupervisor = post.getUsers().stream().anyMatch(User::getIsSupervisor);
                if (user.getIsSupervisor() && hasSupervisor) {
                    throw new OurException("This post already has a supervisor. Only one supervisor is allowed per post.");
                }
            }

            // Encodage du mot de passe et sauvegarde
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPost(post);
            user.setRole("USER");
            user.setCin(user.getCin());
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
            List<User> users = userRepository.findByUserRole();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(users);
            response.setStatusCode(200); // 200 OK
            response.setMessage("Users retrieved successfully");
            response.setData(userDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving users: " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<String> login(LoginRequest loginRequest) {
        Response<String> response = new Response<>();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new OurException("User not found"));

            if (!user.isStatus()) {
                user.setStatus(true);
                userRepository.save(user);
            }

            String token = jwtUtils.generateToken(user);

            response.setStatusCode(200); // 200 OK
            response.setMessage("Login successful");
            response.setData(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setToken(token);
            response.setSupervisor(user.isSupervisor());
            response.setTechnicien(user.isTechnicien());
            response.setStatus(true);

        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error during login: " + e.getMessage());
        }
        return response;
    }



    @Override
    public Response<List<UserDTO>> getAllUsers() {
        Response<List<UserDTO>> response = new Response<>();
        try {
            List<User> users = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(users);
            response.setStatusCode(200);
            // 200 OK
            response.setMessage("Users retrieved successfully");
            response.setData(userDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving users: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<UserDTO> getUserById(long id) {
        Response<UserDTO> response = new Response<>();
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            response.setStatusCode(200); // 200 OK
            response.setMessage("User retrieved successfully");
            response.setData(userDTO);
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<UserDTO> updateUser(Long userId, User user) {
        Response<UserDTO> response = new Response<>();
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new OurException("User not found with ID: " + userId);
            }

            User existingUser = userOpt.get();
            boolean wasSupervisor = existingUser.getIsSupervisor() != null && existingUser.getIsSupervisor();
            boolean wantsToBeSupervisor = user.getIsSupervisor() != null && user.getIsSupervisor();
            if (!wasSupervisor && wantsToBeSupervisor) {
                if (existingUser.getPost() != null) {
                    Long postId = existingUser.getPost().getId();
                    boolean postHasSupervisor = userRepository.existsByPostIdAndIsSupervisorTrue(postId);
                    if (postHasSupervisor) {
                        throw new OurException("This post already has a supervisor. You cannot assign another one.");
                    }
                }
            }
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            existingUser.setPhone(user.getPhone());
            existingUser.setName(user.getName());
            existingUser.setCin(user.getCin());

            if (user.getPost() != null) {
                existingUser.setPost(user.getPost());
            }

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
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
    public Response<UserDTO> updateUsere(Long userId, User user) {
        Response<UserDTO> response = new Response<>();
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new OurException("User not found with ID: " + userId);
            }

            User existingUser = userOpt.get();
            boolean wasSupervisor = existingUser.getIsSupervisor() != null && existingUser.getIsSupervisor();
            boolean wantsToBeSupervisor = user.getIsSupervisor() != null && user.getIsSupervisor();

            if (!wasSupervisor && wantsToBeSupervisor) {
                if (existingUser.getPost() != null) {
                    Long postId = existingUser.getPost().getId();
                    boolean postHasSupervisor = userRepository.existsByPostIdAndIsSupervisorTrue(postId);
                    if (postHasSupervisor) {
                        throw new OurException("This post already has a supervisor. You cannot assign another one.");
                    }
                }
            }
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setName(user.getName());
            existingUser.setCin(user.getCin());
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
    public Response<UserDTO> updateUsere(Long postIde, Long userId, User user) {
        Response<UserDTO> response = new Response<>();
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new OurException("User not found with ID: " + userId);
            }
            User existingUser = userOpt.get();
            boolean wasSupervisor = existingUser.getIsSupervisor() != null && existingUser.getIsSupervisor();
            boolean wantsToBeSupervisor = user.getIsSupervisor() != null ? user.getIsSupervisor() : existingUser.getIsSupervisor();
            if (wantsToBeSupervisor) {
                if (existingUser.getPost() != null) {
                    Long postId = existingUser.getPost().getId();


                } else {
                    throw new OurException("User must be assigned to a post to become a supervisor.");
                }
            }
            Post posteNow = postRepository.findById(postIde);
            if (posteNow == null) {
                throw new OurException("Post not found with ID: " + postIde);
            }
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            existingUser.setPhone(user.getPhone());
            existingUser.setName(user.getName());
            existingUser.setIsSupervisor(wantsToBeSupervisor);
            existingUser.setIsTechnicien(user.getIsTechnicien());
            existingUser.setCin(user.getCin());
            existingUser.setPost(posteNow);
            existingUser.setStartTime(user.getStartTime());
            existingUser.setEndTime(user.getEndTime());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
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
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new OurException("User not found with ID: " + id));
            userRepository.delete(user);
            response.setStatusCode(200); // 200 OK
            response.setMessage("User deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
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
            response.setData(userDTO);
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
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
            response.setStatusCode(200); // 200 OK
            response.setMessage("User problems retrieved successfully");
            response.setData(userDTO.getProblems());
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving user problems: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<UserDTO> getMyInfo(String email) {
        Response<UserDTO> response = new Response<>();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found with email: " + email));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200); // 200 OK
            response.setMessage("User info retrieved successfully");
            response.setData(userDTO);
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving user info: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<PostDTO> getUserPost() {
        Response<PostDTO> response = new Response<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found"));
            if (user.getPost() == null) {
                response.setStatusCode(404);
                response.setMessage("No post found for this user.");
                return response;
            }

            Post post = user.getPost();
            PostDTO postDTO = Utils.mapPostEntityToPostDTO(post);

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

    @Override
    public Response<Void> resetPassword(String email, String newPassword) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return new Response<>(400, "Le nouveau mot de passe doit être différent de l'ancien", null);
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return new Response<>(200, "Mot de passe réinitialisé avec succès", null);
        } catch (Exception e) {
            return new Response<>(500, e.getMessage(), null);
        }
    }

}