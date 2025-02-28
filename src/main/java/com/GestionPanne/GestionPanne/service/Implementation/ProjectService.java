package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.entity.Post;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.FamilyRepository;
import com.GestionPanne.GestionPanne.repo.FiliereRepository;
import com.GestionPanne.GestionPanne.repo.PostRepository;
import com.GestionPanne.GestionPanne.repo.ProjectRepository;
import com.GestionPanne.GestionPanne.service.Interface.IProjectService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService implements IProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FamilyRepository familyRepository;
    @Autowired
    private FiliereRepository filiereRepository;

    @Override
    public Response<ProjectDTO> createProject(long familyId, long filiereId, Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Vérifier si le projet existe déjà par son nom
            if (projectRepository.existsByNameProject(project.getNameProject())) {
                throw new OurException("Project with name '" + project.getNameProject() + "' already exists.");
            }

            if (project.getPosts() == null) {
                project.setPosts(new ArrayList<>());
            }
            Family famille=familyRepository.findById(familyId).get();
            Filiere filiere=filiereRepository.findById(filiereId).get();
            if(famille!=null){
                project.setFamily(famille);
            }
            if(filiere!=null){
                project.setFiliere(filiere);
            }
            Project savedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(savedProject);

            response.setStatusCode(201); // 201 Created
            response.setMessage("Project created successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error creating project: " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<List<ProjectDTO>> getAllProjects() {
        Response<List<ProjectDTO>> response = new Response<>();
        try {
            // Récupérer tous les projets
            List<Project> projects = projectRepository.findAll();
            List<ProjectDTO> projectDTOList = Utils.mapProjectListEntityToProjectListDTO(projects);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Projects retrieved successfully");
            response.setData(projectDTOList); // Utilisation de setData pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving projects: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> getProjectById(long id) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Récupérer le projet par son ID
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(project);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project retrieved successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> updateProject(long projectId, Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            if (!projectRepository.existsById(projectId)) {
                throw new OurException("Filiere not found with ID: " + projectId);
            }
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            Project existingProject = projectOpt.get();
            existingProject.setNameProject(project.getNameProject());
            existingProject.setPosts(project.getPosts());
            existingProject.setFamily(project.getFamily());
            existingProject.setFiliere(project.getFiliere());

            Project updatedProject = projectRepository.save(existingProject);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(updatedProject);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project updated successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error updating project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> createProject(Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Vérifier si le projet existe déjà par son nom
            if (projectRepository.existsByNameProject(project.getNameProject())) {
                throw new OurException("Project with name '" + project.getNameProject() + "' already exists.");
            }

            // Sauvegarder le nouveau projet
            Project savedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(savedProject);

            // Construire la réponse
            response.setStatusCode(201); // 201 Created
            response.setMessage("Project created successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error creating project: " + e.getMessage());
        }
        return response;
    }



    @Override
    public Response<Void> deleteProject(long id) {
        Response<Void> response = new Response<>();
        try {
            // Vérifier si le projet existe
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));

            // Détacher les postes associés en mettant project_id à NULL
            List<Post> posts = postRepository.findByProjectId(id);
            for (Post post : posts) {
                post.setProject(null); // Supprimer l'association avec le projet
                postRepository.save(post); // Mettre à jour la base de données
            }

            // Supprimer le projet
            projectRepository.delete(project);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project deleted successfully, assigned posts are now detached.");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error deleting project: " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<ProjectDTO> assignWorkstationToProject(Long projectId, Long workstationId) {
        return null;
    }
}