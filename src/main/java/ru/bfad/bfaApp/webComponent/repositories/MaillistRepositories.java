package ru.bfad.bfaApp.webComponent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bfad.bfaApp.webComponent.models.Mailslist;

import java.util.Optional;

@Repository
public interface MaillistRepositories extends JpaRepository<Mailslist, Integer> {

    Optional<Mailslist> findMaillistByName (String name);
}
