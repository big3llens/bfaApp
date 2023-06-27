package ru.bfad.bfaApp.webComponent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bfad.bfaApp.webComponent.models.From;

public interface FromRepository extends JpaRepository<From, Integer> {

    From findFromByEmail(String email);
}
