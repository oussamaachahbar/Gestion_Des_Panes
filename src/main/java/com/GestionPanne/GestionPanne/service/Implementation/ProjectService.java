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

            response.setStatusCode(201);
            response.setMessage("Project created successfully");
            response.setData(projectDTO);
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
            List<Project> projects = projectRepository.findAll();
            List<ProjectDTO> projectDTOList = Utils.mapProjectListEntityToProjectListDTO(projects);

            response.setStatusCode(200); // 200 OK
            response.setMessage("Projects retrieved successfully");
            response.setData(projectDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving projects: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> getProjectById(long id) {
        Response<ProjectDTO> response = new Response<>();
        try {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(project);
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project retrieved successfully");
            response.setData(projectDTO);
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> updateProject(long projectId, Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (!projectOpt.isPresent()) {
                throw new OurException("Project not found with ID: " + projectId);
            }
            Project existingProject = projectOpt.get();
            existingProject.setNameProject(project.getNameProject());
            existingProject.setPosts(project.getPosts());

            if (project.getFamily() != null) {
                existingProject.setFamily(project.getFamily());
            }
            if (project.getFiliere() != null) {
                existingProject.setFiliere(project.getFiliere());
            }

            Project updatedProject = projectRepository.save(existingProject);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(updatedProject);

            response.setStatusCode(200);
            response.setMessage("Project updated successfully");
            response.setData(projectDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(500);
            response.setMessage("Error updating project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> createProject(Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            if (projectRepository.existsByNameProject(project.getNameProject())) {
                throw new OurException("Project with name '" + project.getNameProject() + "' already exists.");
            }
            Project savedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(savedProject);
            response.setStatusCode(201); // 201 Created
            response.setMessage("Project created successfully");
            response.setData(projectDTO);
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error creating project: " + e.getMessage());
        }
        return response;
    }



    @Override
    public Response<Void> deleteProject(long id) {
        Response<Void> response = new Response<>();
        try {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));

            List<Post> posts = postRepository.findByProjectId(id);
            for (Post post : posts) {
                post.setProject(null);
                postRepository.save(post);
            }
            projectRepository.delete(project);
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project deleted successfully, assigned posts are now detached.");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting project: " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response<ProjectDTO> assignWorkstationToProject(Long projectId, Long workstationId) {
        return null;
    }
}