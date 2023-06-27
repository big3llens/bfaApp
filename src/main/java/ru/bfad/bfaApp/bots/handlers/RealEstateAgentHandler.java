package ru.bfad.bfaApp.bots.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.bfad.bfaApp.bots.botsApi.*;
import ru.bfad.bfaApp.bots.dto.*;
import ru.bfad.bfaApp.bots.repositories.AgentRepository;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@PropertySource(value = "classpath:telegram.properties", ignoreResourceNotFound = true)
public class RealEstateAgentHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final AccessRoleContext accessRoleContext;
    private final ClientDataCache clientDataCache;
    private final Checker checker;
    private final AgentRepository repository;
    @Value("${keyTest}")
    private String botToken;

    @Override
    public SendMessage handle(BotMessage message) {
        long userId = message.getUserId();
        long chatId = message.getChatId();
        String text = message.getText();
        BotState currentBotState = userDataCache.getUserCurrentBotState(userId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        BelongFromAgent belong = clientDataCache.getBelong(userId);
        BookingFromAgent booking = clientDataCache.getBooking(userId);
        ShowingForAgent showing = clientDataCache.getShowing(userId);

        if(!checker.isAgentRegistered(userId)){
            sendMessage.setText("Вы не прошли регистрацию или были отключены, обратитесь в отдел поддержки за разъяснением причины");
            return sendMessage;
        }

        if (currentBotState.equals(BotState.SHOW_REAL_ESTATE_AGENT)) {
            createRealEstateAgentMenu(sendMessage);
        }
        if (currentBotState.equals(BotState.ASK_DATA_FOR_BELONGING)) {
            createRealEstateAgentMenu(sendMessage);
            sendMessage.setText("Укажите ФИО клиента:");
            userDataCache.setUserCurrentBotState(userId, BotState.ASK_FIO_FOR_BELONGING);
        }
        if (currentBotState.equals(BotState.ASK_FIO_FOR_BELONGING)) {
            createRealEstateAgentMenu(sendMessage);
            belong.setFio(text);
            sendMessage.setText("Укажите телефонный номер клиента:");
            userDataCache.setUserCurrentBotState(userId, BotState.ASK_PHONENUMBER_FOR_BELONGING);
        }
        if (currentBotState.equals(BotState.ASK_PHONENUMBER_FOR_BELONGING)) {
//            if (!checker.checkPhoneNumber(text)) {
//                sendMessage.setText("Вы ввели телефон в некорректном виде, введите пожалуйста телефон в формате +7хххххххххх (+7 и 10 цифр)");
//                return sendMessage;
//            }
            createRealEstateAgentMenu(sendMessage);
            belong.setPhone(text);
            belong.setAgent(new SmallAgentDto(userId, chatId));
            ObjectMapper om = new ObjectMapper();
            try {
                String stringEntity = om.writeValueAsString(belong);
                requestToSagalov(sendMessage, stringEntity, "https://ismaks.bfa-d.ru:31843/api/gks/client_belong");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        if (currentBotState.equals(BotState.BOOKING)) {
            List<ClientForBooking> list = checker.checkClientsOfAgent(userId);
            if (list == null) {
                sendMessage.setText("У вас пока еще нет клиентов в базе, воспользуйтесь сервисом \"Принадлежность\" чтобы добавить клиента");
                return sendMessage;
            }
            generateButtonsClients(list, sendMessage, "Выберите клиента для которого собираетесь провести бронирование квартиры", "b");
//            userDataCache.setUserCurrentBotState(userId, BotState.BOOKING_SELECT_CLIENT);
        }
        if (currentBotState.equals(BotState.BOOKING_SELECT_CLIENT)) {
            sendMessage.setText("Выберите каким способом вы будете передавать паспортные данные, передавать скан/фото паспорта или писать от руки в одну строчку");
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText("Отправить скан/фото паспорта");
            button1.setCallbackData("SendScanOfPassport");
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Ручная запись ПД");
            button2.setCallbackData("SendPD");
            List<InlineKeyboardButton> rowButtons1 = new ArrayList<>();
            rowButtons1.add(button1);
            List<InlineKeyboardButton> rowButtons2 = new ArrayList<>();
            rowButtons2.add(button2);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(rowButtons1);
            rowList.add(rowButtons2);
            inlineKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            userDataCache.setGuidForAgent(userId, message.getText());
//            userDataCache.setUserCurrentBotState(userId, BotState.SEND_PD);
        }
//        if (currentBotState.equals(BotState.PD)) {
//            sendMessage.setText("Введите ПД клиента: серию, номер паспорта, кем и когда выдан, ФИО, место жительства текущее и по регистрации");
//            userDataCache.setUserCurrentBotState(userId, BotState.ASK_OBJECTID);
//        }
        if(currentBotState.equals(BotState.ASK_OBJECTID)){
//            booking.setPassportdetails(text);
            sendMessage.setText("Введите номер объекта, который хотите забронировать");
            userDataCache.setUserCurrentBotState(userId, BotState.SEND_PD);
        }
//        if(currentBotState.equals(BotState.ASK_BOOKINGBEFOR)){
//            booking.setObjectid(text);
//            sendMessage.setText("Введите дату бронирования в формате dd-MM-yyyy (Пример: 20-12-2024)");
//            userDataCache.setUserCurrentBotState(userId, BotState.SEND_PD);
//        }
        if (currentBotState.equals(BotState.SEND_PD)) {
//            if (!checker.checkValideDate(text)){
//                sendMessage.setText("Некорректная дата, введите пожалуйста дату в формате dd-MM-yyyy");
//                return sendMessage;
//            }
//            booking.setBooking_before(text);
            booking.setObjectid(text);
            booking.setAgent(new SmallAgentDto(userId, chatId));
            booking.setClient_guid(userDataCache.getGuid(userId));
            ObjectMapper om = new ObjectMapper();
            try {
                requestToSagalov(sendMessage, om.writeValueAsString(booking), "https://ismaks.bfa-d.ru:31843/api/gks/booking_request");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        if (currentBotState.equals(BotState.SCAN_PD)) {
            sendMessage.setText("Прикрепите пдф или фото паспорта");
            userDataCache.setUserCurrentBotState(userId, BotState.SEND_SCAN_PD);
        }
        if (currentBotState.equals(BotState.SEND_SCAN_PD)) {
            String fileId = message.getDocument().getFileId();
            String fileName = message.getDocument().getFileName();
            Long fileSize = message.getDocument().getFileSize();
            try {
                if (!uploadFile(fileName, fileId, fileSize)) {
                    sendMessage.setText(String.format("По какойто причине файл %s загрузить не удалось", fileName));
                }
                sendMessage.setText(String.format("Файл %s успешно загружен", fileName));
                userDataCache.setUserCurrentBotState(userId, BotState.ASK_OBJECTID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (currentBotState.equals(BotState.SHOWING)) {
            List<ClientForBooking> list = checker.checkClientsOfAgent(userId);
            if (list == null) {
                sendMessage.setText("У вас пока еще нет клиентов в базе, воспользуйтесь сервисом \"Принадлежность\" чтобы добавить клиента");
                return sendMessage;
            }
            generateButtonsClients(list, sendMessage, "Выберите клиента для которого собираетесь провести просмотр квартиры", "s");

        }
        if (currentBotState.equals(BotState.ASK_SHOWING_DATA)) {
            showing.setClient_guid(text);
            System.out.println("GUID: " + message.getText());
            sendMessage.setText("Введите место, дату и время показа одним сообщением");
            userDataCache.setUserCurrentBotState(userId, BotState.ASK_SHOWING_OBJECT);
        }
        if (currentBotState.equals(BotState.ASK_SHOWING_OBJECT)) {
            showing.setShow_detail(message.getText());
            sendMessage.setText("Введите номер квартиры/объекта, который планируете показывать клиенту");
            userDataCache.setUserCurrentBotState(userId, BotState.SEND_SHOWING);
        }
        if (currentBotState.equals(BotState.SEND_SHOWING)) {
            showing.setObjectid(message.getText());
            ObjectMapper om = new ObjectMapper();
            try {
                requestToSagalov(sendMessage, om.writeValueAsString(showing), "https://ismaks.bfa-d.ru:31843/api/gks/show_request");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        clientDataCache.setShowing(userId, showing);
        clientDataCache.setBooking(userId, booking);
        clientDataCache.setBelong(userId, belong);
        return sendMessage;
    }

    public SendMessage requestToSagalov(SendMessage sendMessage, String stringEntity, String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            // Создать запрос на запись
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            /**Токен тестовый */
            httpPost.setHeader("Authorization", "Token Hpvs+20cPdleEObcgYEtY0S4Qd/aaclZvTjo4P6G5ff=");
            /**Токен рабочий */
//                    httpPost.setHeader("Authorization", "Token Hpvs+20cPdleEObcgYEtY0S4Qd/aaclZvTjo4P6G5ef=");
            StringEntity entity = new StringEntity(stringEntity, "UTF-8");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            org.apache.http.HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // Конвертировать содержимое ответа на строку
                String sagalovResponse = EntityUtils.toString(responseEntity, Charsets.UTF_8);
                System.out.println("ОТВЕЕЕЕТ ПРИШЕЕЕЕЕЕЕЕЕЛ!!!!!!:   " + sagalovResponse);
                if (sagalovResponse.contains("OK")) {
                    sendMessage.setText("Ваш запрос успешно отправлен");
                    return sendMessage;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage.setText("Ваш запрос не был отправлен, для уточнения причины обратитесь пожалуйста в тех. поддержку");
            return sendMessage;
        }
        sendMessage.setText("Ваш запрос не был отправлен, для уточнения причины обратитесь пожалуйста в тех. поддержку");
        return sendMessage;
    }

    public void createRealEstateAgentMenu(SendMessage message) {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton button1 = new InlineKeyboardButton();
//        button1.setText("Принадлежность");
//        button1.setCallbackData("Belonging");
//        InlineKeyboardButton button2 = new InlineKeyboardButton();
//        button2.setText("Бронирование");
//        button2.setCallbackData("Booking");
//        InlineKeyboardButton button3 = new InlineKeyboardButton();
//        button3.setText("Показ");
//        button3.setCallbackData("Showing");
//        List<InlineKeyboardButton> rowButtons1 = new ArrayList<>();
//        rowButtons1.add(button1);
//        List<InlineKeyboardButton> rowButtons2 = new ArrayList<>();
//        rowButtons2.add(button2);
//        List<InlineKeyboardButton> rowButtons3 = new ArrayList<>();
//        rowButtons3.add(button3);
//        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//        rowList.add(rowButtons1);
//        rowList.add(rowButtons2);
//        rowList.add(rowButtons3);
//        inlineKeyboardMarkup.setKeyboard(rowList);
//        message.setText("Функционал агента по продаже недвижимости:");
//        message.setReplyMarkup(inlineKeyboardMarkup);

        ReplyKeyboardMarkup inlineKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardButton button1 = new KeyboardButton();
        button1.setText("Принадлежность");
        KeyboardRow keyboardButtons1 = new KeyboardRow();
        keyboardButtons1.add(button1);
        KeyboardButton button2 = new KeyboardButton();
        button2.setText("Бронирование");
        KeyboardRow keyboardButtons2 = new KeyboardRow();
        keyboardButtons2.add(button2);
        KeyboardButton button3 = new KeyboardButton();
        button3.setText("Показ");
        KeyboardRow keyboardButtons3 = new KeyboardRow();
        keyboardButtons3.add(button3);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(keyboardButtons1);
        rows.add(keyboardButtons2);
        rows.add(keyboardButtons3);
        inlineKeyboardMarkup.setKeyboard(rows);
        inlineKeyboardMarkup.setIsPersistent(false);
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setText("Чтобы узнать принадлежность некого клиента нажмите кнопку \"Принадлежность\"" +
                "\nЧтобы забронировать показ квартиры нажмите \"Бронирование\"\nЧтобы назначить показ квартиры нажмите \"Показ\"");

//        ReplyKeyboardMarkup inlineKeyboardMarkup = new ReplyKeyboardMarkup();
//        KeyboardRow keyboardButtons1 = new KeyboardRow();
//        keyboardButtons1.add("Принадлежность");
//        keyboardButtons1.add("Бронирование");
//        keyboardButtons1.add("Показ");
//        KeyboardRow keyboardButtons2 = new KeyboardRow();
//        keyboardButtons2.add("Какая-то кнопка");
//        keyboardButtons2.add("Еще какая-то кнопка");
//        List<KeyboardRow> rows = new ArrayList<>();
//        rows.add(keyboardButtons1);
//        rows.add(keyboardButtons2);
//        inlineKeyboardMarkup.setKeyboard(rows);
//        inlineKeyboardMarkup.setIsPersistent(false);
//        message.setReplyMarkup(inlineKeyboardMarkup);
//        message.setText("Чтобы узнать принадлежность некого клиента нажмите кнопку \"Принадлежность\"" +
//                "\nЧтобы забронировать показ квартиры нажмите \"Бронирование\"\nЧтобы назначить показ квартиры нажмите \"Показ\"");
    }

    public void generateButtonsClients(List<ClientForBooking> list, SendMessage sendMessage, String textForSendMessage, String c) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sendMessage.setText(textForSendMessage);
        for (ClientForBooking client : list) {
            List<InlineKeyboardButton> rowButtons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
//            sb.append(client.getFio()).append("\n").append(client.getPhone());
//            button.setText(sb.toString());
            button.setText(String.format("%s \n %s", client.getFio(), client.getPhone()));
            sb.append(c).append(client.getGuid());
            button.setCallbackData(sb.toString());
            rowButtons.add(button);
            rowList.add(rowButtons);
            sb.setLength(0);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    private String createTelegramFullnameForAgents(String firstName, String lastName, String userName) {
        String telegramInfo = firstName + " " + lastName + " " + userName;
        telegramInfo = telegramInfo.replaceAll("null ", "");
        telegramInfo = telegramInfo.replaceAll(" null", "");
        return telegramInfo;
    }

    public boolean uploadFile(String fileName, String fileId, Long fileSize) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String res = in.readLine();
        System.out.println(fileId);

        JsonNode node = new ObjectMapper().readTree(res);
        String filePath = node.get("result").get("file_path").asText();

        File localFile = new File("C:\\Data\\Handbook\\Files\\" + fileName);
        InputStream is = new URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
        in.close();
        is.close();

        if (fileSize == localFile.length()) {
            return true;
        }

        System.out.println("Uploaded!");
        return false;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.REAL_ESTATE_AGENT;
    }
}
