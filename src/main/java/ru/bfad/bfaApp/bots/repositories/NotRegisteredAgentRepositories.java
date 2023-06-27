package ru.bfad.bfaApp.bots.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.bfad.bfaApp.bots.models.NotRegisteredAgent;

import java.util.Optional;

public interface NotRegisteredAgentRepositories extends JpaRepository<NotRegisteredAgent, Long> {
    Optional<NotRegisteredAgent> findByUserId (Long userId);
    Boolean existsNotRegisteredAgentByUserId(Long userId);

    @Transactional
    void deleteNotRegisteredAgentByUserId(Long userId);
}
