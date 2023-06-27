package ru.bfad.bfaApp.bots.botsApi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bfad.bfaApp.bots.handlers.InputMessageHandler;
import ru.bfad.bfaApp.bots.dto.BotMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> handlersList) {
        for (InputMessageHandler handler: handlersList) {
            messageHandlers.put(handler.getHandlerName(), handler);
        }
    }
    public SendMessage processInputMessage(BotState currentState, BotMessage message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isManagmentProfileState(currentState)) return messageHandlers.get(BotState.MANAGMENT);
        if (isGeneralProfileState(currentState)) return messageHandlers.get(BotState.GENERAL);
        if (isRealEstateAgenState(currentState)) return messageHandlers.get(BotState.REAL_ESTATE_AGENT);
        if (isItState(currentState)) return messageHandlers.get(BotState.IT);
        if (isRegistrationState(currentState)) return messageHandlers.get(BotState.REGISTRATION);
        return messageHandlers.get(currentState);
    }

    private boolean isManagmentProfileState(BotState currentState) {
        switch (currentState) {
            case MANAGMENT:
            case ASK_INN:
                return true;
            default:
                return false;
        }
    }
    private boolean isGeneralProfileState(BotState currentState) {
        switch (currentState) {
            case GENERAL:
            case AUTHORIZATION_CODE:
            case ASK_AUTHORIZATION_CODE:
            case SHOW_MENU:
                return true;
            default:
                return false;
        }
    }
    private boolean isRealEstateAgenState(BotState currentState) {
        switch (currentState) {
            case REAL_ESTATE_AGENT:
            case ASK_DATA_FOR_BELONGING:
            case ASK_FIO_FOR_BELONGING:
            case ASK_PHONENUMBER_FOR_BELONGING:
            case SHOW_REAL_ESTATE_AGENT:
            case BOOKING:
            case BOOKING_SELECT_CLIENT:
            case PD:
            case SCAN_PD:
            case SEND_PD:
            case SEND_SCAN_PD:
            case ASK_OBJECTID:
            case ASK_BOOKINGBEFOR:
            case SHOWING:
            case ASK_SHOWING_DATA:
            case  ASK_SHOWING_OBJECT:
            case SEND_SHOWING:
                return true;
            default:
                return false;
        }
    }

    private boolean isItState(BotState currentState) {
        switch (currentState) {
            case IT:
            case SHOW_IT:
            case WRITE_TO_SUPPORT:
            case SEND_MESSAGE_TO_SUPPORT:
                return true;
            default:
                return false;
        }
    }

    private boolean isRegistrationState(BotState currentState) {
        switch (currentState) {
            case REGISTRATION:
            case REGISTRATION_ASK_FIO:
            case REGISTRATION_ASK_PHONE:
            case REGISTRATION_ASK_AGENCY:
            case END_REGISTRATION:
                return true;
            default:
                return false;
        }
    }

}
