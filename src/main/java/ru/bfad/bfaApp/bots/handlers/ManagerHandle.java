package ru.bfad.bfaApp.bots.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bfad.bfaApp.bots.dto.BotMessage;
import ru.bfad.bfaApp.bots.botsApi.BotState;
import ru.bfad.bfaApp.bots.botsApi.UserDataCache;

@Component
public class ManagerHandle implements InputMessageHandler{
    private final UserDataCache userDataCache;

    public ManagerHandle(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;

    }

    @Override
    public SendMessage handle(BotMessage message) {
        Long userId = message.getUserId();
        Long chatId = message.getChatId();
        String userText = message.getText();
        BotState currentBotState = userDataCache.getUserCurrentBotState(userId);
        SendMessage sendMessage = new SendMessage();

        if(currentBotState.equals(BotState.MANAGMENT)){
            sendMessage.setText("Введите номер ИНН");
            sendMessage.setChatId(chatId);
            userDataCache.setUserCurrentBotState(userId, BotState.ASK_INN);
        }
        if(currentBotState.equals(BotState.ASK_INN)){
            sendMessage.setText("Ваш ИНН: " + userText);
            sendMessage.setChatId(chatId);
            userDataCache.setUserCurrentBotState(userId, BotState.DEFAULT);
        }

        return sendMessage;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.MANAGMENT;
    }

}
