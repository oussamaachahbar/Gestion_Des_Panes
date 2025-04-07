package com.GestionPanne.GestionPanne;

import com.GestionPanne.GestionPanne.repo.ProjectRepository;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        projectRepository.makeFamilyIdNullable();
        projectRepository.makeFiliereIdNullable();
        userRepository.makePostIdNullable();
    }
}
