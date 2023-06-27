package ru.bfad.bfaApp.bots.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.bfad.bfaApp.bots.models.Agent;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Integer> {

    Agent findByUserId (Long userId);
    Boolean existsAgentByUserId(Long userId);
//    @Query("zddfdgnf")
    @Transactional
    void deleteAgentByUserId(Long userId);
//    Boolean removeAgentByUserId(Long userId);
}
