package ru.bfad.bfaApp.bots.botsApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Data;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import ru.bfad.bfaApp.bots.dto.ClientForBooking;
import ru.bfad.bfaApp.bots.models.Agent;
import ru.bfad.bfaApp.bots.repositories.AgentRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Data
public class Checker {
    private final AgentRepository agentRepository;
    private final Map<Long, Boolean> registeredAgents = new HashMap<>();
    private final Map<Long, Boolean> authorizedUsers = new HashMap<>();

    public boolean isAgentRegistered(Long userId) {
        if (!registeredAgents.containsKey(userId)) registeredAgents.put(userId, false);
        boolean isRegistered = registeredAgents.get(userId);
//        boolean isRegistered = agentRepository.findByUserId(userId) != null;
        if (!isRegistered) {
//            System.out.println("ПОЛЬЗОВАТЕЛЬ НЕ ЗАРЕГАНЫЙ");
//            isRegistered = agentRepository.findByUserId(userId) != null;
            isRegistered = agentRepository.existsAgentByUserId(userId);
            if (isRegistered) {
//                System.out.println("ЮЗЕРА НЕ БЫЛО В КОНТЕКСТЕ, НО МЫ НАШЛИ ЕГО В БАЗЕ И ДОБАВИЛИ В КОНТЕКСТ");
                registeredAgents.put(userId, true);
            }
        }
//        System.out.println("isRegistered = " + isRegistered);
        return isRegistered;
    }

//    public boolean isAgentRegistered(Long userId) {
//        if (registeredAgents.get(userId) == null){
//            registeredAgents.put(userId, Boolean.FALSE);
//        }
//        return registeredAgents.get(userId);
//    }

    public void deleteRegisteredAgent(Long userId) {
        registeredAgents.put(userId, Boolean.FALSE);
    }

    public boolean checkPhoneNumber(String phoneNumber) {
//        Pattern pattern = Pattern.compile("^((\\+?7)([0-10]{10}))$");
//        Pattern pattern = Pattern.compile("^\\+?[78][-\\(]?\\d{3}\\)?-?\\d{3}-?\\d{2}-?\\d{2}$");


        Pattern pattern = Pattern.compile("^\\+[7][-\\(]?\\d{3}\\)?-?\\d{3}-?\\d{2}-?\\d{2}$");


//        Pattern pattern = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{10}\\)?[\\- ]?)?[\\d\\- ]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public List<ClientForBooking> checkClientsOfAgent(Long userId) {
        List<ClientForBooking> list = new ArrayList<>();
//        list.add(new ClientForBooking("Иванова Мария Петрова", "+78956325589", "24t89qhjk3q4y9ghhn3q4g9034yh"));
//        list.add(new ClientForBooking("Иванов Петр Петрович", "+78956325589", "24t89qhjk3q4yrghhn3q4g9034yh"));
//        list.add(new ClientForBooking("Марийский Василий Олегович", "+78956325589", "24t89q5tgk3q4y9ghhn3q4g9034yh"));
//        list.add(new ClientForBooking("Артемский Артем Артемович", "+78956325589", "24t89qhjktq4y9ghhn3q4g9034yh"));
//        list.add(new ClientForBooking("Велийи И Ужасный", "+78956325589", "24t89qhjk3q4y9ghhjuq4g9034yh"));
//        list.add(new ClientForBooking("Иванова Мария Петрова", "+78956325589", "24t89qhjk3q456ghhn3q4g9034yh"));
//        list.add(new ClientForBooking("Иванова Мария Петрова", "+78956325589", "24t89qhjk3q4y9ghhn3q4h9034yh"));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            // Создать запрос на запись
            HttpPost httpPost = new HttpPost("https://ismaks.bfa-d.ru:31843/api/gks/client_list");
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            /**Токен тестовый */
            httpPost.setHeader("Authorization", "Token Hpvs+20cPdleEObcgYEtY0S4Qd/aaclZvTjo4P6G5ff=");
            /**Токен рабочий */
//                    httpPost.setHeader("Authorization", "Token Hpvs+20cPdleEObcgYEtY0S4Qd/aaclZvTjo4P6G5ef=");
            StringEntity entity = new StringEntity(String.format("{\"agent\":{\"userid\":%d}}", userId), "UTF-8");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            org.apache.http.HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String sagalovResponse = EntityUtils.toString(responseEntity, Charsets.UTF_8);
                System.out.println("ОТВЕЕЕЕТ ПРИШЕЕЕЕЕЕЕЕЕЛ!!!!!!:   " + sagalovResponse);
                if (sagalovResponse.contains("OK")) {
                    ObjectMapper om = new ObjectMapper();
                    JsonNode node = om.readTree(sagalovResponse);
                    String clientList = node.get("data").toString();
                    TypeFactory typeFactory = om.getTypeFactory();
                    list = om.readValue(clientList, typeFactory.constructCollectionType(List.class, ClientForBooking.class));
//                    System.out.println(clientList);
//                    System.out.println(list.toString());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean checkValideDate(String date) {
        String[] formats = {"dd-MM-yyyy", "dd.MM.yyyy"};
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
