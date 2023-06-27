package ru.bfad.bfaApp.bots.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.bfad.bfaApp.bots.dto.BotMessage;
import ru.bfad.bfaApp.bots.botsApi.BotState;
import ru.bfad.bfaApp.bots.botsApi.UserDataCache;
import ru.bfad.bfaApp.bots.services.TelegramBotService;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItHandler implements InputMessageHandler{
    private static final long MARKELOV_ID = 1274244996;
    private final UserDataCache userDataCache;

    public ItHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(BotMessage message) {
        Long userId = message.getUserId();
        Long chatId = message.getChatId();
        String userText = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        BotState currentBotState = userDataCache.getUserCurrentBotState(userId);
        if (currentBotState.equals(BotState.SHOW_IT)){
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText("Написать сообщение тех. поддержке");
            button1.setCallbackData("Support");
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Скопировать ссылку");
            button2.setCallbackData("CopyLink");
            List<InlineKeyboardButton> rowButtons = new ArrayList<>();
            rowButtons.add(button1);
            rowButtons.add(button2);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(rowButtons);
            inlineKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setText("Функционал взаимодействия с it-отделом:");
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        if (currentBotState.equals(BotState.WRITE_TO_SUPPORT)){
            sendMessage.setText("Опишите вашу проблему или суть вопроса");
            userDataCache.setUserCurrentBotState(userId, BotState.SEND_MESSAGE_TO_SUPPORT);
        }
        if(currentBotState.equals(BotState.SEND_MESSAGE_TO_SUPPORT)){
            SendMessage sendMessage1 = new SendMessage();
            sendMessage1.setChatId(MARKELOV_ID);
            sendMessage1.setText(userText);
            sendMessage = sendMessage1;
//            sendMessage.setText("Ваше обращение отправленно в тех. поддержку");
//            userDataCache.setUserCurrentBotState(userId, BotState.DEFAULT);
        }
        return sendMessage;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.IT;
    }
}
