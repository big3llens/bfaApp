package ru.bfad.bfaApp.webComponent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bfad.bfaApp.webComponent.models.PersonMailslist;

import java.util.Optional;

public interface PersonMaillistRepositories extends JpaRepository<PersonMailslist, Long> {

    Optional<PersonMailslist> findByMaillistIdAndPersonId (Integer maillistId, Integer personId);
}
