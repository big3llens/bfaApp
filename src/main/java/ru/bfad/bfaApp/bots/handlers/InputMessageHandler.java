package ru.bfad.bfaApp.bots.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bfad.bfaApp.bots.dto.BotMessage;
import ru.bfad.bfaApp.bots.botsApi.BotState;

public interface InputMessageHandler {
    SendMessage handle(BotMessage message);
    BotState getHandlerName();
}
