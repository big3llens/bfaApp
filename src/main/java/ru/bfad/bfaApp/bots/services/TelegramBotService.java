package ru.bfad.bfaApp.bots.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.Config;
import jcifs.Configuration;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.bfad.bfaApp.bots.botsApi.Checker;
import ru.bfad.bfaApp.bots.dto.*;
import ru.bfad.bfaApp.bots.botsApi.BotState;
import ru.bfad.bfaApp.bots.botsApi.BotStateContext;
import ru.bfad.bfaApp.bots.botsApi.UserDataCache;
import ru.bfad.bfaApp.bots.configs.BotConfig;
import ru.bfad.bfaApp.bots.models.Agent;
import ru.bfad.bfaApp.bots.models.NotRegisteredAgent;
import ru.bfad.bfaApp.bots.repositories.AgentRepository;
import ru.bfad.bfaApp.bots.repositories.NotRegisteredAgentRepositories;
import ru.bfad.bfaApp.webComponent.dto.SupportMessage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class TelegramBotService extends TelegramLongPollingBot {
    private static final long MARKELOV_ID = 1274244996;
//    private static final long ORLOV_ID = 1274244996;

    private final long[] engineersId = {MARKELOV_ID};

    private final BotConfig botConfig;
    private final UserDataCache cache;
    private final BotStateContext botStateContext;
    private final AgentRepository agentRepository;
    private final NotRegisteredAgentRepositories notRegisteredAgentRepositories;
    private final ObjectMapper om;
    private final Checker checker;

    public TelegramBotService(BotConfig botConfig, UserDataCache cache, BotStateContext botStateContext,AgentRepository agentRepository,
                              NotRegisteredAgentRepositories notRegisteredAgentRepositories, Checker checker) {
        this.botConfig = botConfig;
        this.cache = cache;
        this.botStateContext = botStateContext;
        this.agentRepository = agentRepository;
        this.notRegisteredAgentRepositories = notRegisteredAgentRepositories;
        this.checker = checker;
        om = new ObjectMapper();
        List<BotCommand> commandList = new ArrayList<>();
//        commandList.add(new BotCommand("/registration", "Регистрация"));
//        commandList.add(new BotCommand("/authorization", "Авторизация для получения доступа к дополнительному функционалу"));
        commandList.add(new BotCommand("/showmenu", "Вывести доступное для вас меню"));
//        commandList.add(new BotCommand("/it", "Написать обращение в it-отдел"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());
        BotState currentBotState;
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().toString());
            long userId = update.getMessage().getFrom().getId();
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText.split(" ")[0]) {
                case "/registration":
                    currentBotState = BotState.REGISTRATION;
                    break;
                case "/authorization":
                    currentBotState = BotState.AUTHORIZATION_CODE;
                    break;
                case "/showmenu":
                    currentBotState = BotState.SHOW_MENU;
                    break;
                case "/it":
                    currentBotState = BotState.SHOW_IT;
                    break;
                case "/manager":
                    currentBotState = BotState.MANAGMENT;
                    break;
                case "Принадлежность":
                    currentBotState = BotState.ASK_DATA_FOR_BELONGING;
                    break;
                case "Бронирование":
                    currentBotState = BotState.BOOKING;
                    break;
                case "Показ":
                    currentBotState = BotState.SHOWING;
                    break;
                default:
                    currentBotState = cache.getUserCurrentBotState(userId);
                    break;
            }
            cache.setUserCurrentBotState(userId, currentBotState);
            System.out.println("111" + currentBotState);
                sendMessage(botStateContext.processInputMessage(currentBotState, new BotMessage(chatId, userId, messageText,
                        update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), update.getMessage().getFrom().getUserName())));

        }
        else if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            long userId = callbackQuery.getFrom().getId();
            long chatId = callbackQuery.getMessage().getChatId();
            System.out.println("Коллбекдата: " + callbackQuery.getData());
            if(callbackQuery.getData().equals("Agent")){
                cache.setUserCurrentBotState(userId, BotState.SHOW_REAL_ESTATE_AGENT);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("Registration")){
                cache.setUserCurrentBotState(userId, BotState.REGISTRATION);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("Endregistration")){
                cache.setUserCurrentBotState(userId, BotState.END_REGISTRATION);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("Belonging")){
                cache.setUserCurrentBotState(userId, BotState.ASK_DATA_FOR_BELONGING);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("Support")){
                cache.setUserCurrentBotState(userId, BotState.WRITE_TO_SUPPORT);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("Booking")){
                cache.setUserCurrentBotState(userId, BotState.BOOKING);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("Showing")){
                cache.setUserCurrentBotState(userId, BotState.SHOWING);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("SendScanOfPassport")){
                cache.setUserCurrentBotState(userId, BotState.SCAN_PD);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            if(callbackQuery.getData().equals("SendPD")){
                cache.setUserCurrentBotState(userId, BotState.PD);
                sendMessage(botStateContext.processInputMessage(cache.getUserCurrentBotState(userId), new BotMessage(chatId, userId)));
                return;
            }
            generateCallbacksForAgent(callbackQuery);

        } else if (update.getMessage().hasDocument()){
            Document document = update.getMessage().getDocument();
            long userId = update.getMessage().getFrom().getId();
            long chatId = update.getMessage().getChatId();
            currentBotState = cache.getUserCurrentBotState(userId);
            if(currentBotState == BotState.SEND_SCAN_PD) sendMessage(botStateContext.processInputMessage(currentBotState, new BotMessage(chatId, userId, null,
                    update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), update.getMessage().getFrom().getUserName(), document)));
        } else if(update.getMessage().hasPhoto()){
            List<PhotoSize> photos = update.getMessage().getPhoto();
            for (PhotoSize p: photos) {
                System.out.println(p.toString());
            }
            System.out.println("*****************************************************************************************");
        }
    }
    @SneakyThrows
    private void generateCallbacksForAgent(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        System.out.println("зашел в генерацию коллбеков");

        System.out.println("ботстейт: " + cache.getUserCurrentBotState(userId));
        List<ClientForBooking> clientslist = checker.checkClientsOfAgent(userId);
        for (ClientForBooking client: clientslist) {
            if (callbackQuery.getData().equals("b" + client.getGuid())){
//                cache.setUserCurrentBotState(userId, BotState.BOOKING_SELECT_CLIENT); Вернуть при появлении необходимости отправлять сканы/фото
                cache.setUserCurrentBotState(userId, BotState.ASK_OBJECTID);
                cache.setGuidForAgent(userId, client.getGuid());
//                sendMessage(botStateContext.processInputMessage(BotState.BOOKING_SELECT_CLIENT, new BotMessage(chatId, userId, client.getGuid())));
                sendMessage(botStateContext.processInputMessage(BotState.ASK_OBJECTID, new BotMessage(chatId, userId, client.getGuid())));
            }
            if (callbackQuery.getData().equals("s" + client.getGuid())){
                cache.setUserCurrentBotState(userId, BotState.ASK_SHOWING_DATA);
                cache.setGuidForAgent(userId, client.getGuid());
                sendMessage(botStateContext.processInputMessage(BotState.ASK_SHOWING_DATA, new BotMessage(chatId, userId, client.getGuid())));
            }
        }
    }


    /** 99 = отказ, 1 = зареган*/
    public String saveAgent(RegistrationResponse registrationResponse) {
        Long userId = registrationResponse.getUserid();
        IsMaxResponse status = new IsMaxResponse();
        System.out.println(registrationResponse.toString());
//        System.out.println("ЗАШЕЛ В СОХРАНЕНИЕ АГЕНТА");
        try {
            if(!notRegisteredAgentRepositories.existsNotRegisteredAgentByUserId(userId)){
                status.setStatus((byte) 99);
                status.setNote(String.format("Пользователь с userId: %d еще не проходил регистрацию, следовательно мы не можем его ни добавить ни удалить", userId));
                return om.writeValueAsString(status);
            }
            if(registrationResponse.getStatus() == 11){
                if (agentRepository.existsAgentByUserId(userId)) agentRepository.deleteAgentByUserId(userId);
                notRegisteredAgentRepositories.deleteNotRegisteredAgentByUserId(userId);
                checker.deleteRegisteredAgent(userId);
                if (!agentRepository.existsAgentByUserId(userId) && !notRegisteredAgentRepositories.existsNotRegisteredAgentByUserId(userId)){
                    sendMessage(registrationResponse.getChatid(), "Вы были удалены из приложения");
                    status.setStatus((byte) 1);
                    status.setNote(String.format("Пользователь с userId: %d удален из зарегестрированных", userId));
                    return om.writeValueAsString(status);
                }
                status.setStatus((byte) 99);
                status.setNote(String.format("По какой-то причине пользователя с userId: %d не получилось удалить", userId));
                return om.writeValueAsString(status);
            }
            NotRegisteredAgent nra = notRegisteredAgentRepositories.findByUserId(userId).orElseThrow(() -> new RuntimeException("Не получилось вытащить юзера с id: " + userId +" из промежуточной базы"));
            if (registrationResponse.getStatus() == 99){
                if(agentRepository.existsAgentByUserId(userId)){
                    agentRepository.deleteAgentByUserId(userId);
                    if (!agentRepository.existsAgentByUserId(userId)){
                        checker.deleteRegisteredAgent(userId);
                        nra.setFirstRegistration(false);
                        notRegisteredAgentRepositories.save(nra);
                        sendMessage(registrationResponse.getChatid(), "Вам запрещен доступ к дополнительным функциям в приложении");
                        status.setStatus((byte) 1);
                        status.setNote(String.format("Пользователь с userId: %d удален из зарегестрированных", userId));
                        return om.writeValueAsString(status);
                    }
                }
                if (notRegisteredAgentRepositories.existsNotRegisteredAgentByUserId(userId)){
                    if (nra.isFirstRegistration()){
                        sendMessage(userId, "Вам отказано в регистрации");
                        status.setStatus((byte) 1);
                        status.setNote("Пользователю отправлен отказ в регистрации");
                        return om.writeValueAsString(status);
                    }
//                    sendMessage(registrationResponse.getChatid(), "Вы были удаленны из приложения");
//                    status.setStatus((byte) 99);
//                    status.setNote(String.format("Пользователь с userId: %d удален из зарегестрированных", userId));
//                    return om.writeValueAsString(status);
                }
                if(!agentRepository.existsAgentByUserId(userId)){
                    status.setStatus((byte) 99);
                    status.setNote(String.format("Пользователь с userId: %d и так не был зарегестрирован", userId));
                    return om.writeValueAsString(status);
                }
            }
            if (registrationResponse.getStatus() == 1){
                if(agentRepository.existsAgentByUserId(userId)){
                    status.setStatus((byte) 99);
                    status.setNote(String.format("Пользователь с userId: %d уже присутствует в базе", userId));
                    return om.writeValueAsString(status);
                }
                Agent agent = new Agent(null, nra.getUserId(), nra.getFio(), nra.getPhone(), nra.getCompany(), nra.getTelegramInfo());
                agentRepository.save(agent);
                if(agentRepository.existsAgentByUserId(userId)){
                    if (nra.isFirstRegistration()){
                        sendMessage(registrationResponse.getUserid(), "Регистрация прошла успешно, теперь вам доступен дополнительный функционал бота");
                        status.setStatus((byte) 1);
                        status.setNote("Пользователю отправлено сообщение об успешной регистрации");
                        return om.writeValueAsString(status);
                    }
                    sendMessage(registrationResponse.getUserid(), "Вам снова доступен дополнительный функционал");
                    status.setStatus((byte) 1);
                    status.setNote("Пользователю отправлено сообщение о повторном предоставлении доступа");
                    return om.writeValueAsString(status);
                }
            }
            status.setStatus((byte) 99);
            status.setNote("Произошла непредвиденная ошибка");
            return om.writeValueAsString(status);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
            return "{\"status\":99, \"note\":\"Произошла непредвиденная ошибка, сообщение не было отправлено пользователю\"}";
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return "{\"status\":99, \"note\":\"Произошла непредвиденная ошибка, вероятно бот не смог распарсить свой ответ для исмакса(\"}";
    }

/** 1-агент, 2-БФА, 99-некорректные данные   */
    public String sendBelongResponse(BelongResponse response){
        System.out.println("вошли в сендбелонг");
//        try {
//            IsMaxResponse status = new IsMaxResponse();
//            status.setStatus((byte) 1);
//            status.setNote("Сообщение доставлено");
//            System.out.println(response.toString());
//            if (response.getFio().isEmpty() || response.getPhone().isEmpty()){
//                status.setStatus((byte) 99);
//                status.setNote("Отсутствует один из параметров: fio или phone");
//                return om.writeValueAsString(status);
//            }
//            if (response.getStatus() == 1){
//                sendMessage(response.getChatid(), String.format("Клиент [имя: %S, телефон: %s] закреплен за агентом до %s", response.getFio(), response.getPhone(), response.getValidtill()));
//                return om.writeValueAsString(status);
//            }
//            if (response.getStatus() == 2) {
//                sendMessage(response.getChatid(), String.format("Клиент [имя: %S, телефон: %s] закреплен за БФА до %s", response.getFio(), response.getPhone(),response.getValidtill()));
//                return om.writeValueAsString(status);
//            }
//            if (response.getStatus() == 99) {
//                sendMessage(response.getChatid(), String.format("Данные [имя: %S, телефон: %s] недостаточны для получения корректного ответа", response.getFio(), response.getPhone()));
//                return om.writeValueAsString(status);
//            }
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        } catch (TelegramApiException e){
//            e.printStackTrace();
//            return "{\"status\":99, \"note\":\"Произошла непредвиденная ошибка, сообщение не было отправлено пользователю\"}";
//        }
        try {
            sendMessage(response.getChatid(), response.getStatus());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "{\"status\":99, \"note\":\"Произошла непредвиденная ошибка, вероятно бот не смог распарсить свой ответ для исмакса(\"}";
        }
        return "{\"status\":1, \"note\":\"Сообщение отправлено пользователю\"}";
    }

    public void sendMessageToSupport(SupportMessage message, Model model) {
        if (message.getText().isEmpty() || message == null) {
            model.addAttribute("messageContentError", "Сообщение не было отправлено т.к. Вы не описали проблему, опишите пожалуйста проблему и повторите отправку");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Пользователь из кабинета: ").append(message.getRoomNumber())
                .append("\nСообщает о следующей проблеме: ").append(message.getText());
        try {
            for (long id : engineersId) {
                sendMessage(id, sb.toString());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sb.setLength(0);
    }

    public void writeLinkOnConferencComp(String link) {
//        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("10.160.1.106", "markelov", "pp1245!");
        BaseContext baseCxt = null;
        Properties jcifsProperties = new Properties();
        jcifsProperties.setProperty("jcifs.smb.client.enableSMB2", "true");
        jcifsProperties.setProperty("jcifs.smb.client.dfs.disabled", "true");

        try {
            Configuration config = null;
            config = new PropertyConfiguration(jcifsProperties);
            baseCxt = new BaseContext(config);
//            CIFSContext auth1 = baseCxt.withCredentials(new NtlmPasswordAuthenticator("10.160.1.106", "markelov",
//                    "pp1245!"));
            CIFSContext auth1 = baseCxt.withCredentials(new NtlmPasswordAuthenticator("CONHALL-PC", "conhall",
                    "123456"));

            Config.registerSmbURLHandler();
//            URL url = new URL("file:\\\\EDO1Test\\Link\\link.txt");
            URL url = new URL("file:\\\\CONHALL-PC\\Link\\link.txt");

            SmbFile file = new SmbFile(url, auth1);
            byte[] linkBytes = link.getBytes();
            try {
                SmbFileOutputStream smbfos = new SmbFileOutputStream(file);
                smbfos.write(linkBytes, 0, linkBytes.length);
            } catch (IOException e) {
                System.out.println("какого-то хрена файл не записался");
                e.printStackTrace();
            }
        } catch (CIFSException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void defaultBlock (String text, long chatId) throws TelegramApiException {
        if(text.length() > 5 && text.substring(0, 4).equals("http"))writeLinkOnConferencComp(text);
        else sendMessage(chatId, "Простите, не знаю такую команду(");
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    public void startCommandRecieved(long chatid, String name) throws TelegramApiException {
        String answer = "BfaItBot приветствует вас, " + name;
        sendMessage(chatid, answer);
    }

    public void sendMessage(long chatid, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatid));
        message.setText(text);
        execute(message);
    }

    public void sendMessage(SendMessage message) throws TelegramApiException {
        execute(message);
    }
}
