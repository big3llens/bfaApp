package ru.bfad.bfaApp.bots.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bfad.bfaApp.bots.botsApi.*;
import ru.bfad.bfaApp.bots.botsApi.AccessRoleDuration;
import ru.bfad.bfaApp.bots.dto.BotMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GeneralHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final AccessRoleContext accessRoleContext;
    private final Checker checker;

    public GeneralHandler(UserDataCache userDataCache, AccessRoleContext accessRoleContext, Checker checker) {
        this.userDataCache = userDataCache;
        this.accessRoleContext = accessRoleContext;
        this.checker = checker;
//        checkingTheDurationOfAccessCodeTimes(this.accessRoleContext);
    }

    @Override
    public SendMessage handle(BotMessage message) {
        Long userId = message.getUserId();
        Long chatId = message.getChatId();
        String userText = message.getText();
        BotState currentBotState = userDataCache.getUserCurrentBotState(userId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
//        System.out.println("СОООБЩЕНИЕ ОТ БОТА: " + message.toString());
        if (currentBotState.equals(BotState.AUTHORIZATION_CODE)) {
            if (!checker.isAgentRegistered(userId)){
                sendMessage.setText("Чтобы получить доступ к дополнительному функционалу зарегестрируйтесь пожалуйста по ссылке /registration");
                return sendMessage;
            }
            sendMessage.setText("Введите пятизначный код");
            userDataCache.setUserCurrentBotState(userId, BotState.ASK_AUTHORIZATION_CODE);
        }
        if (currentBotState.equals(BotState.ASK_AUTHORIZATION_CODE)) {
            sendMessage.setText(authorization(userText, userId));
        }
        if (currentBotState.equals(BotState.SHOW_MENU)) {
            if (!checker.isAgentRegistered(userId)){
                createRealEstateAgentMenu(sendMessage, (byte)1);
                return sendMessage;
            }
            createRealEstateAgentMenu(sendMessage, (byte)2);

//            List<AccessRoleDuration> list = accessRoleContext.getUserAccessRoleById(userId);
//            if (list != null && !list.isEmpty()) {
//                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//                sendMessage.setText("Вам доступен следующий функционал:");
//                for (AccessRoleDuration ar : accessRoleContext.getUserAccessRoleById(userId)) {
//                    if (ar.getRole().equals(AccessRole.REAL_ESTATE_AGENT)) {
//                        InlineKeyboardButton button1 = new InlineKeyboardButton();
//                        button1.setText("Агент по продаже недвижимости");
//                        button1.setCallbackData("Agent");
//                        List<InlineKeyboardButton> rowButtons = new ArrayList<>();
//                        rowButtons.add(button1);
//                        rowList.add(rowButtons);
//                        inlineKeyboardMarkup.setKeyboard(rowList);
//                        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//                    }
//                }
//            } else {
//                sendMessage.setText("Чтобы получить доступ к дополнительному функционалу введите соответствующий код в меню /authorization");
//            }
        }
        return sendMessage;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.GENERAL;
    }

    public String authorization(String code, long id) {
        if (code.equals("12345")) {
            accessRoleContext.setUserAccessRole(id, new AccessRoleDuration(AccessRole.REAL_ESTATE_AGENT, System.currentTimeMillis()));
            for (AccessRoleDuration ar : accessRoleContext.getUserAccessRoleById(id)) {
                System.out.println("222" + ar.getRole());
            }
            return "Вам доступен функционал агента по продаже недвижимости, нажмите /showmenu, чтобы увидеть весь доступный для вас функционал";
        }
        return "Вы ввели неправильный код доступа";
    }

    public void createRealEstateAgentMenu(SendMessage message, byte b){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        if (b == 1){
            message.setText("Чтобы пользоваться функционалом бота, пройдите пожалуйста сперва регистрацию, желаете продолжить?");
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText("Да");
            button1.setCallbackData("Registration");
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Нет");
            button2.setCallbackData("Endregistration");
            List<InlineKeyboardButton> rowButtons1 = new ArrayList<>();
            rowButtons1.add(button1);
            List<InlineKeyboardButton> rowButtons2 = new ArrayList<>();
            rowButtons2.add(button2);
            rowList.add(rowButtons1);
            rowList.add(rowButtons2);
            inlineKeyboardMarkup.setKeyboard(rowList);
            message.setReplyMarkup(inlineKeyboardMarkup);
        }
        if (b == 2){
            message.setText("Доступные сервисы:");
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText("Агент по продаже недвижимости");
            button1.setCallbackData("Agent");
            List<InlineKeyboardButton> rowButtons = new ArrayList<>();
            rowButtons.add(button1);
            rowList.add(rowButtons);
            inlineKeyboardMarkup.setKeyboard(rowList);
            message.setReplyMarkup(inlineKeyboardMarkup);
        }

    }

    private void checkingTheDurationOfAccessCodeTimes(AccessRoleContext context) {
        Thread thread = new Thread(new CheckThread(context));
        thread.start();
    }

    private class CheckThread implements Runnable {
        private final AccessRoleContext accessRoleContext;

        private CheckThread(AccessRoleContext accessRoleContext) {
            this.accessRoleContext = accessRoleContext;
        }

        @Override
        public void run() {
            while (true) {
                long currentTime = System.currentTimeMillis();
                Map<Long, List<AccessRoleDuration>> userAccessRole = accessRoleContext.getUserAccessRole();
                for (List<AccessRoleDuration> list : accessRoleContext.getUserAccessRole().values()) {
                    list.removeIf(ar -> currentTime - ar.getAddingTime() > 21600000);
                    System.out.println(list.toString());
                }
                try {
                    Thread.sleep(21600000);
                    System.out.println("проверОЧКА!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
