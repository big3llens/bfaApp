package ru.bfad.bfaApp.webComponent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bfad.bfaApp.webComponent.models.Person;

import java.util.Optional;

public interface PersonRepositories extends JpaRepository<Person, Integer> {

    Optional<Person> findPersonByEmail(String email);

    Boolean existsByEmail(String email);
}
