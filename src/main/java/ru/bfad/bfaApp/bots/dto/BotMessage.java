package ru.bfad.bfaApp.bots.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Document;

@Data
@RequiredArgsConstructor
public class BotMessage {
    private long chatId;
    private long userId;
    private String text;
    private String firstName;
    private String lastName;
    private String userName;
    private String telegramInfo;
    private Document document;

    public BotMessage(long chatId, long userId, String text, String firstName, String lastName, String userName) {
        this.chatId = chatId;
        this.userId = userId;
        this.text = text;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        createTelegramInfo(firstName, lastName, userName);
    }

    public BotMessage(long chatId, long userId, String text, String firstName, String lastName, String userName, Document document) {
        this.chatId = chatId;
        this.userId = userId;
        this.text = text;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.document = document;
        createTelegramInfo(firstName, lastName, userName);
    }

    public BotMessage(long chatId, long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    public BotMessage(long chatId, long userId, String text) {
        this.chatId = chatId;
        this.userId = userId;
        this.text = text;
    }

    private void createTelegramInfo(String firstName, String lastName, String userName){
        telegramInfo = firstName + " " + lastName + " " + userName;
        telegramInfo = telegramInfo.replaceAll("null ", "");
        telegramInfo = telegramInfo.replaceAll(" null", "");
    }
}
