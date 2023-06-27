package ru.bfad.bfaApp.webComponent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bfad.bfaApp.webComponent.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findUserByUsername(String name);
}
