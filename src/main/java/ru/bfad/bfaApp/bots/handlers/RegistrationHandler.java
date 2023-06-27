package ru.bfad.bfaApp.bots.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bfad.bfaApp.bots.botsApi.BotState;
import ru.bfad.bfaApp.bots.botsApi.Checker;
import ru.bfad.bfaApp.bots.botsApi.UserDataCache;
import ru.bfad.bfaApp.bots.dto.AgentDto;
import ru.bfad.bfaApp.bots.dto.BotMessage;
import ru.bfad.bfaApp.bots.models.Agent;
import ru.bfad.bfaApp.bots.models.NotRegisteredAgent;
import ru.bfad.bfaApp.bots.repositories.AgentRepository;
import ru.bfad.bfaApp.bots.repositories.NotRegisteredAgentRepositories;

@Component
@Data
public class RegistrationHandler implements InputMessageHandler{
    private final AgentRepository repository;
    private final NotRegisteredAgentRepositories notRegisteredAgentRepositories;
    private final UserDataCache userDataCache;
    private final Checker checker;

    @Override
    public SendMessage handle(BotMessage message) {
        Long userId = message.getUserId();
        Long chatId = message.getChatId();
        String userText = message.getText();
        BotState currentBotState = userDataCache.getUserCurrentBotState(userId);
        Agent agent = userDataCache.getAgent(userId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if(currentBotState.equals(BotState.REGISTRATION)){
            if (notRegisteredAgentRepositories.existsNotRegisteredAgentByUserId(userId)){
                sendMessage.setText("Вы уже регестрировались ранее, дождитесь решения по вашей заявке на регистрацию");
                userDataCache.setUserCurrentBotState(userId, BotState.DEFAULT);
                return sendMessage;
            }
            sendMessage.setText("Введите ваши фамилию и имя");
            userDataCache.setUserCurrentBotState(userId, BotState.REGISTRATION_ASK_FIO);
        }
        if (currentBotState.equals(BotState.REGISTRATION_ASK_FIO)){
            sendMessage.setText("Введите ваш телефон");
            System.out.println(agent.toString());
            agent.setFio(userText);
            userDataCache.setUserCurrentBotState(userId, BotState.REGISTRATION_ASK_PHONE);
        }
        if (currentBotState.equals(BotState.REGISTRATION_ASK_PHONE)){
            sendMessage.setText("Введите компанию, которую вы представляете");
            System.out.println(agent.toString());
            agent.setPhone(userText);
            userDataCache.setUserCurrentBotState(userId, BotState.REGISTRATION_ASK_AGENCY);
        }
        if (currentBotState.equals(BotState.REGISTRATION_ASK_AGENCY)){
            agent.setCompany(userText);
            System.out.println("Данные для внесения в базу: " + agent.toString());
            if(!repository.existsAgentByUserId(userId)){
                System.out.println("КЛИЕНТ НЕ ЗАРЕГАН В НАШЕЙ ОСНОВНОЙ БАЗЕ");
                if (!notRegisteredAgentRepositories.existsNotRegisteredAgentByUserId(userId)){
                    System.out.println("КЛИЕНТ НЕ ЗАРЕГАН И В НАШЕЙ ДОПОЛНИТЕЛЬНОЙ БАЗЕ(((((((");
//                    if (notRegisteredAgentRepositories.existsNotRegisteredAgentByUserId(userId)){
                        ObjectMapper om = new ObjectMapper();
                        AgentDto requestAgent = new AgentDto(agent.getUserId(), chatId, agent.getFio(), agent.getPhone(), agent.getCompany(),
                                createTelegramFullnameForAgents(message.getFirstName(), message.getLastName(), message.getUserName()));
                        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
                            // Создать запрос на запись
                            HttpPost httpPost = new HttpPost("https://ismaks.bfa-d.ru:31843/api/gks/registration");
                            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

                            /**Токен тестовый */
                            httpPost.setHeader("Authorization", "Token Hpvs+20cPdleEObcgYEtY0S4Qd/aaclZvTjo4P6G5ff=");
                            /**Токен рабочий */
//                            httpPost.setHeader("Authorization", "Token Hpvs+20cPdleEObcgYEtY0S4Qd/aaclZvTjo4P6G5ef=");
                            StringEntity entity = new StringEntity(om.writeValueAsString(requestAgent), "UTF-8");

                            System.out.println("КЛИЕЕЕЕНТ: " + requestAgent.toString());
                            httpPost.setEntity(entity);
                            HttpResponse response = httpClient.execute(httpPost);
                            org.apache.http.HttpEntity responseEntity = response.getEntity();
                            if (responseEntity != null) {
                                // Конвертировать содержимое ответа на строку
                                String sagalovResponse = EntityUtils.toString(responseEntity, Charsets.UTF_8);
                                System.out.println("ОТВЕЕЕЕТ ПРИШЕЕЕЕЕЕЕЕЕЛ!!!!!!:   " + sagalovResponse);
                                if (sagalovResponse.contains("OK")){
                                    NotRegisteredAgent notRegisteredAgent = new NotRegisteredAgent(null, agent.getUserId(), agent.getFio(), agent.getPhone(), agent.getCompany(),
                                            createTelegramFullnameForAgents(message.getFirstName(), message.getFirstName(), message.getUserName()), true);
                                    notRegisteredAgentRepositories.save(notRegisteredAgent);
                                    sendMessage.setText("Ваша регистрационная заявка передана на рассмотрение, мы сообщим вам об окончании регистрации");
                                    return sendMessage;
                                }
                                sendMessage.setText("Что-то пошло не так, повторите пожалуйста регистрацию");
                                return sendMessage;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendMessage.setText("Что-то пошло не так, повторите пожалуйста регистрацию");
                            return sendMessage;
                        }
                        sendMessage.setText("Ваша регистрационная заявка передана на рассмотрение, мы сообщим вам об окончании регистрации");
//                    } else sendMessage.setText("Что-то пошло не так, повторите пожалуйста регистрацию");

                } else if (notRegisteredAgentRepositories.existsNotRegisteredAgentByUserId(userId)) sendMessage.setText("Вы уже ранее проводили попытку регистрации, дождитесь пожалуйста окончания рассмотрения заявки");

            }else sendMessage.setText("Вы уже зарегестрированы в приложении, повторная регистрация не требуется");

//            if (repository.save(agent).equals(agent)){
//                sendMessage.setText(String.format("Регистрация прошла успешно, вы зарегестрированы как: \nИмя: %s\nТелефон: %s\nАгенство: %s", agent.getFio(), agent.getPhone(), agent.getAgency()));
//                userDataCache.setUserCurrentBotState(userId, BotState.DEFAULT);
//            } else sendMessage.setText("Что-то пошло не так, повторите пожалуйста регистрацию");

        }
        if (currentBotState.equals(BotState.END_REGISTRATION)) sendMessage.setText("Вы можете пройти регистрацию позже, в любое удобное для вас время");
        userDataCache.setAgent(userId, agent);
        return sendMessage;
    }

    private String createTelegramFullnameForAgents(String firstName, String lastName, String userName){
        String telegramInfo = firstName + " " + lastName + " " + userName;
        telegramInfo = telegramInfo.replaceAll("null ", "");
        telegramInfo = telegramInfo.replaceAll(" null", "");
        return telegramInfo;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.REGISTRATION;
    }
}
