package ru.bfad.bfaApp.bots.botsApi;

import org.springframework.stereotype.Component;
import ru.bfad.bfaApp.bots.models.Agent;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache {
    private final Map<Long, BotState> userBotState = new HashMap<>();
    private final Map<Long, Agent> registrationData = new HashMap<>();
    private final Map<Long, String> agentsClientGuid = new HashMap<>();

    public void setUserCurrentBotState(Long id, BotState botState){
        userBotState.put(id, botState);
    }

    public BotState getUserCurrentBotState(Long id){
        BotState botState = userBotState.get(id);
        if(botState == null){
            botState = BotState.DEFAULT;
        }
        return botState;
    }

    public Agent getAgent(Long id) {
        Agent agent = registrationData.get(id);
        if (agent ==null){
            agent = new Agent();
            agent.setUserId(id);
        }
        return agent;
    }

    public void setAgent(Long id, Agent agent){
        registrationData.put(id, agent);
    }

    public void setGuidForAgent(Long userId, String guid){
        agentsClientGuid.put(userId, guid);
    }

    public String getGuid(Long userId){
        return agentsClientGuid.get(userId);
    }
}
