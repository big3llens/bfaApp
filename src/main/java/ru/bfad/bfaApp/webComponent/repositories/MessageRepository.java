package ru.bfad.bfaApp.webComponent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bfad.bfaApp.webComponent.models.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
